package com.sts.finncub.usermanagement.service.impl;

import com.google.gson.Gson;
import com.sts.finncub.core.components.SmsProperties;
import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.SmsUtil;
import com.sts.finncub.usermanagement.assembler.SignUpConverter;
import com.sts.finncub.usermanagement.config.MobileAppConfig;
import com.sts.finncub.usermanagement.request.CallbackMailRequest;
import com.sts.finncub.usermanagement.request.CreateNewPasswordRequest;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import com.sts.finncub.usermanagement.util.MaintainPasswordHistory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService, Constant {

    private static final String KEY = "USER_SESSION";
    private final RedisTemplate<String, Object> template;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    private final UserOrganizationMappingRepository userOrganizationMappingRepository;
    private final BranchMasterRepository branchMasterRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    private final EmployeeRepository employeeRepository;
    private final MiscellaneousServiceRepository miscellaneousServiceRepository;
    private final MobileAppConfig mobileAppConfig;
    private final OrganizationRepository organizationRepository;
    private final VendorSmsLogRepository vendorSmsLogRepository;
    private final SmsProperties smsProperties;
    private final SmsUtil smsUtil;
    @Value("${password.old.count}")
    private Integer oldPasswordCount;
    @Value("${password.failed.count}")
    private Integer passwordFailedCount;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    MaintainPasswordHistory maintainPasswordHistory;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserRedisRepository userRedisRepository, UserRoleMappingRepository userRoleMappingRepository, UserCredentialService userCredentialService, UserOrganizationMappingRepository userOrganizationMappingRepository, BranchMasterRepository branchMasterRepository, UserLoginLogRepository userLoginLogRepository, EmployeeRepository employeeRepository, MiscellaneousServiceRepository miscellaneousServiceRepository, MobileAppConfig mobileAppConfig, RedisTemplate<String, Object> template, OrganizationRepository organizationRepository, VendorSmsLogRepository vendorSmsLogRepository, SmsProperties smsProperties, SmsUtil smsUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.userCredentialService = userCredentialService;
        this.userOrganizationMappingRepository = userOrganizationMappingRepository;
        this.branchMasterRepository = branchMasterRepository;
        this.userLoginLogRepository = userLoginLogRepository;
        this.employeeRepository = employeeRepository;
        this.miscellaneousServiceRepository = miscellaneousServiceRepository;
        this.mobileAppConfig = mobileAppConfig;
        this.template = template;
        this.organizationRepository = organizationRepository;
        this.vendorSmsLogRepository = vendorSmsLogRepository;
        this.smsProperties = smsProperties;
        this.smsUtil = smsUtil;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException, InternalServerErrorException {
        LoginResponse loginResponse = new LoginResponse();
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setLoginMode(loginRequest.getLoginMode());
        userLoginLog.setUserId(loginRequest.getUserId());
        userLoginLog.setLoginTime(LocalDateTime.now());
        userLoginLog.setApplicationVersion(loginRequest.getApplicationVersion());
        userLoginLog.setIpAddress(loginRequest.getIpAddress());
        userLoginLog.setDeviceId(loginRequest.getDeviceId());
        userLoginLog.setImeiNumber1(loginRequest.getImeiNumber1());
        userLoginLog.setImeiNumber2(loginRequest.getImeiNumber2());
        userLoginLog.setLatitude(loginRequest.getLatitude());
        userLoginLog.setLongitude(loginRequest.getLongitude());
        userLoginLog.setStatus("S");
        try {
            Gson gson = new Gson();
            log.info("Login Request received for userId : {}", loginRequest.getUserId());
            User user = userRepository.findByUserIdIgnoreCase(loginRequest.getUserId()).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + loginRequest.getUserId(), HttpStatus.NOT_FOUND));
            if ("N".equalsIgnoreCase(user.getIsActive())) {
                log.error("User is not active , userId : {}", loginRequest.getUserId());
                throw new BadRequestException("User is not active.", HttpStatus.BAD_REQUEST);
            }
            if("Y".equalsIgnoreCase(user.getIsPasswordExpired())){
                log.error("User password has been expired, userId :{}",loginRequest.getUserId());
                throw new BadRequestException("Your password has been expired. Please reset your password",HttpStatus.BAD_REQUEST);
            }
//            if(StringUtils.hasText(user.getImeiNumber()) && !user.getImeiNumber().equals(loginRequest.getImeiNumber1())){
//                log.error("User {} not authorized due to IMEI Number ", loginRequest.getUserId());
//                throw new BadRequestException("You are not authorized. Please contact HR", HttpStatus.BAD_REQUEST);
//            }
            if ("N".equalsIgnoreCase(user.getIsPasswordActive())) {
                log.error("User password is blocked for userId : {}", loginRequest.getUserId());
                throw new BadRequestException("Your password is blocked. Please contact HR", HttpStatus.BAD_REQUEST);
            }
            if (!user.isPasswordCorrect(loginRequest.getPassword())) {
                user.setLoginAttempt((user.getLoginAttempt() != null) ? user.getLoginAttempt() + 1 : 1);
                if (user.getLoginAttempt() != null && user.getLoginAttempt() >= passwordFailedCount) {
                    user.setIsPasswordActive("N");
                }
                userRepository.updateLoginAttemptByUserId(user.getUserId(), user.getLoginAttempt(), user.getUserId(), LocalDateTime.now());
                log.error("Invalid password supplied for login , userId : {}", loginRequest.getUserId());
                throw new BadRequestException("Invalid password", HttpStatus.BAD_REQUEST);
            } else {
                userRepository.updateLoginAttemptByUserId(user.getUserId(), 0, user.getUserId(), LocalDateTime.now());
            }
            log.info("User session enter to get branchMap, ZoneMap , DivisionMap for , userId : {}", loginRequest.getUserId());
            UserSession userSession = toSessionObject(user);
            if (CollectionUtils.isEmpty(userSession.getRoles())) {
                throw new BadRequestException("No Role Assigned,Please Contact HR", HttpStatus.BAD_REQUEST);
            }
            Organization organizationMaster = organizationRepository.findByOrgId(userSession.getOrganizationId()).orElse(null);
            if (organizationMaster != null) {
                LocalTime appLoginTime = organizationMaster.getAppLoginTime().toLocalTime();
                LocalTime appLogoutTime = organizationMaster.getAppLogoutTime().toLocalTime();
                LocalTime now = LocalTime.now();
                int start = now.compareTo(appLoginTime);
                int end = now.compareTo(appLogoutTime);
                if (!(start >= 0 && end <= 0)) {
                    if (!userSession.getRoles().contains("ROLE_ADMIN")) {
                        throw new BadRequestException("System is under maintenance. Login is allowed between " + organizationMaster.getAppLoginTime().toLocalTime() + " AM - " + organizationMaster.getAppLogoutTime().toLocalTime() + " PM.", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            String authToken;
            try {
                authToken = saveToken(userSession);
                log.info("User session saved , userId : {}", loginRequest.getUserId());
                loginResponse.setAuthToken(authToken);
                loginResponse.setUserSession(gson.fromJson(userSession.getUserSessionJSON(), UserSession.class));
                loginResponse.setAppConfigs(mobileAppConfig.getConfig());
                if ("M".equalsIgnoreCase(loginRequest.getLoginMode())) {
                    MiscellaneousService miscellaneousServices = miscellaneousServiceRepository.findByKey("APP_VERSION");
                    if (miscellaneousServices != null) {
                        String[] appVersionConfig = miscellaneousServices.getValue().split("\\.");
                        String[] loginRequestAppVersion = loginRequest.getApplicationVersion().split("\\.");
                        if (Integer.parseInt(loginRequestAppVersion[0]) < Integer.parseInt(appVersionConfig[0])) {
                            throw new BadRequestException("Please update your app version", HttpStatus.BAD_REQUEST);
                        } else if (Integer.parseInt(loginRequestAppVersion[1]) < Integer.parseInt(appVersionConfig[1])) {
                            throw new BadRequestException("Please update your app version", HttpStatus.BAD_REQUEST);
                        } else if (Integer.parseInt(loginRequestAppVersion[2]) < Integer.parseInt(appVersionConfig[2])) {
                            throw new BadRequestException("Please update your app version", HttpStatus.BAD_REQUEST);
                        }
                        loginResponse.setAppVersion(miscellaneousServices.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("Exception occurred while login , userId : {} , message : {}", loginRequest.getUserId(), e.getMessage(), e);
                throw new BadRequestException("Login error - " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            //save user Login log
            userLoginLog.setTokenId(authToken);
            userLoginLogRepository.save(userLoginLog);
            log.info("Login successful, userId : {}", loginRequest.getUserId());
        } catch (Exception exception) {
            log.error("Exception- {}", exception.getMessage());
            userLoginLog.setFailureReason(exception.getMessage());
            userLoginLog.setStatus("E");
            try {
                userLoginLogRepository.save(userLoginLog);
            } catch (Exception ex) {
                log.error("Exception while saving UserLogin , message {} ", ex.getMessage(), ex);
            }
            throw exception;
        }
        return loginResponse;
    }

    public UserSession toSessionObject(User user) throws InternalServerErrorException {
        UserSession userSession = new UserSession();
        try {
            Set<Integer> parentIdList = new HashSet<>();
            Map<Integer, String> branchIdMap = new HashMap<>();
            Map<Integer, String> divisionMap = new HashMap<>();
            Map<Integer, String> zoneMap = new HashMap<>();
            Map<Integer, String> circleMap = new HashMap<>();
            userSession.setEmail(user.getEmail());
            userSession.setName(user.getName());
            userSession.setType(user.getType());
            userSession.setUserId(user.getUserId());
            userSession.setOrganizationId(getActiveOrganizationId(user));
            userSession.setBcId(user.getBcId());
            Organization organization = organizationRepository.findByOrgId(userSession.getOrganizationId()).orElse(null);
            if (organization != null) {
                userSession.setOrgCode(organization.getOrgCode() != null ? organization.getOrgCode() : "");
            }
            userSession.setIsTemporaryPassword(user.getIsTemporaryPassword());
            if (getActiveOrganizationId(user) != null && user.getUserId() != null) {
                Set<String> userRoleMappingList = userRoleMappingRepository.findRoleName(getActiveOrganizationId(user), user.getUserId());
                userSession.setRoles(userRoleMappingList);
            }
            if (getActiveOrganizationId(user) != null && user.getUserId() != null) {
                List<Object[]> branchMasterList = branchMasterRepository.findBranchNameAndCode(getActiveOrganizationId(user), user.getUserId());
                for (Object[] branchMaster : branchMasterList) {
                    if (branchMaster[3] != null) {
                        BigDecimal parentId = (BigDecimal) branchMaster[3];
                        parentIdList.add(parentId.intValue());
                    }
                    if (branchMaster[2] != null) {
                        BigDecimal branchId = (BigDecimal) branchMaster[2];
                        branchIdMap.put(branchId.intValue(), branchMaster[0] + "-" + branchMaster[1]);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                List<Object[]> divisionList = branchMasterRepository.findByDivisionZoneByBranchId(getActiveOrganizationId(user), parentIdList);
                parentIdList.clear();
                for (Object[] division : divisionList) {
                    if (division[3] != null) {
                        BigDecimal parentId = (BigDecimal) division[3];
                        parentIdList.add(parentId.intValue());
                    }
                    if (division[2] != null) {
                        BigDecimal branchId = (BigDecimal) division[2];
                        divisionMap.put(branchId.intValue(), division[0] + "-" + division[1]);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                List<Object[]> zoneList = branchMasterRepository.findByDivisionZoneByBranchId(getActiveOrganizationId(user), parentIdList);
                parentIdList.clear();
                for (Object[] zone : zoneList) {
                    if (zone[3] != null) {
                        BigDecimal parentId = (BigDecimal) zone[3];
                        parentIdList.add(parentId.intValue());
                    }
                    if (zone[2] != null) {
                        BigDecimal branchId = (BigDecimal) zone[2];
                        zoneMap.put(branchId.intValue(), zone[0] + "-" + zone[1]);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                List<Object[]> circleList = branchMasterRepository.findByDivisionZoneByBranchId(getActiveOrganizationId(user), parentIdList);
                for (Object[] circle : circleList) {
                    if (circle[2] != null) {
                        BigDecimal branchId = (BigDecimal) circle[2];
                        circleMap.put(branchId.intValue(), circle[0] + "-" + circle[1]);
                    }
                }
            }
            userSession.setBranchMap(branchIdMap);
            userSession.setDivisionMap(divisionMap);
            userSession.setZoneMap(zoneMap);
            userSession.setCircleMap(circleMap);
            try {
                Employee employee = employeeRepository.findByUserId(user.getUserId()).orElse(null);
                if (employee != null) {
                    userSession.setBaseLocation(branchMasterRepository.findBranchName(getActiveOrganizationId(user), employee.getBranchId()));
                    userSession.setDepartmentName(employee.getEmployeeDepartmentMaster() != null ? employee.getEmployeeDepartmentMaster().getEmpDepartmentName() : "");
                    userSession.setDesignationName(employee.getEmployeeDesignationMaster() != null ? employee.getEmployeeDesignationMaster().getEmpDesignationName() : "");
                    userSession.setDesignationType(employee.getDesignationType() != null ? employee.getDesignationType() : "");
                } else {
                    userSession.setDesignationType("HO");
                }
            } catch (Exception exception) {
                log.error("Exception while fetching employee details for userId :{} , message : {}", userSession, exception.getMessage(), exception);
            }
        } catch (Exception ex) {
            log.error("Exception occurred while preparing BranchMap , DivisionMap and ZoneMap , message : {}", ex.getMessage(), ex);
            throw new InternalServerErrorException("Exception while set data in userSession object" + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return userSession;
    }

    public Long getActiveOrganizationId(User user) {
        Long activeOrgId = null;
        List<UserOrganizationMapping> orgList = userOrganizationMappingRepository.findById_UserId(user.getUserId());
        for (UserOrganizationMapping orgMapping : orgList) {
            if ("Y".equalsIgnoreCase(orgMapping.getActive())) {
                activeOrgId = orgMapping.getId().getOrganizationId();
                break;
            }
        }
        return activeOrgId;
    }

    @Transactional
    @Override
    public SignupResponse signup(SignupRequest signupRequest) {
        User newUser = SignUpConverter.convertToUser(signupRequest);
        String operationUserName = userCredentialService.getUserSession().getName();
        final Long organizationId = userCredentialService.getUserSession().getOrganizationId();
        newUser.setPassword(passwordEncoder, signupRequest.getPassword());
        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(organizationId, signupRequest.getUserType());
        final String userId = userEmployeeId.split(",")[0];
        newUser.setUserId(userId);
        log.info("Generated user Id : {} , email : {}", newUser.getUserId(), newUser.getEmail());
        newUser = userRepository.save(newUser);
        log.info("Operation user name : {} , email : {} , organizationId : {}", operationUserName, signupRequest.getEmail(), organizationId);
        UserOrganizationMapping userOrganizationMapping = new UserOrganizationMapping(organizationId, newUser.getUserId(), operationUserName);
        userOrganizationMappingRepository.save(userOrganizationMapping);
        if (signupRequest.hasRoles()) {
            log.info("Setting roles , userId :{} ", userId);
            userRoleMappingRepository.saveAll(signupRequest.getRoleList().stream().map(id -> new UserRoleMapping(userId, id, organizationId, operationUserName)).collect(Collectors.toSet()));
            log.info("Roles saved to db, userId :{} ", userId);
        }
        return SignUpConverter.convertToResponse(newUser);
    }

    @Override
    public Response verify(String authToken) throws ObjectNotFoundException {
        UserSession userSession = userRedisRepository.findById(authToken).orElseThrow(() -> new ObjectNotFoundException("User session not found, " + "Please login again!", HttpStatus.NOT_FOUND));
        return new Response(RestMappingConstants.SUCCESS, userSession, HttpStatus.OK);
    }

    private String saveToken(UserSession userSession) {
        Gson gson = new Gson();
        log.info("saving user session, userId : {}", userSession.getUserId());
        userSession.setUserSessionJSON(gson.toJson(userSession));
        return userRedisRepository.save(userSession).getId();
    }

    @Override
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        String tokenString = request.getHeader("Authorization");
        String token = tokenString.split(" ")[1];
        template.delete(KEY + ":" + token);
        log.info("Logout successful");
        UserLoginLog loginLog = userLoginLogRepository.findByUserIdAndTokenId(userSession.getUserId(), token);
        if (loginLog != null) {
            loginLog.setLogoutTime(LocalDateTime.now());
            userLoginLogRepository.save(loginLog);
            log.info("Token marked expired in db for TOKEN {}", token);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> changePassword(LoginRequest request) throws ObjectNotFoundException, BadRequestException {
        Response response = new Response();
        log.info("Fetching userSession for changePassword request, userId : {} ", request.getUserId());
        UserSession userSession = userCredentialService.getUserSession();
//      validate password(Regex)
        User user = userRepository.findByUserIdIgnoreCase(userSession.getUserId()).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
//      check current password
        if (!user.isPasswordCorrect(request.getPassword())) {
            log.error("Incorrect password supplied , userId : {}", request.getUserId());
            throw new BadRequestException("Invalid current password", HttpStatus.BAD_REQUEST);
        }
        // check confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("New password is not same as confirm password for userId : {} ", request.getUserId());
            throw new BadRequestException("New password is not same as confirm password ", HttpStatus.BAD_REQUEST);
        }
        String newPassword = passwordEncoder.encode(request.getNewPassword());
//      check new password with 5 old password
        String oldPassword = user.getOldPassword();
        if (oldPassword == null) {
            oldPassword = user.getPassword();
        } else {
            String[] oldPasswordList = oldPassword.split(PASSWORD_SEPARATOR);
            for (String pass : oldPasswordList) {
                if (BCrypt.checkpw(request.getNewPassword(), pass)) {
                    log.error("New password matches with recent passwords  , userId : {}", request.getUserId());
                    throw new BadRequestException("New password matches with recent passwords ", HttpStatus.BAD_REQUEST);
                }
            }
//          Maintain old passwords
            String oldPasswordUpdated = maintainPasswordHistory.maintainOldPasswordHistory(oldPasswordList, oldPassword, PASSWORD_SEPARATOR, newPassword);
            oldPassword = (new StringBuilder()).append(oldPasswordUpdated).toString();
        }
//      update new password
        user.setOldPassword(oldPassword);
        user.setPassword(newPassword);
        user.setIsTemporaryPassword("N");
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(userSession.getUserId());
        user.setPasswordResetDate(LocalDate.now());
        userRepository.save(user);
//        userRedisRepository.deleteById(userSession.g);
        log.info("Password updated successfully , userId : {}", request.getUserId());
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> resetPassword(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException {
        Response response = new Response();
        log.info("Fetching userSession for resetPassword request, userId : {} ", loginRequest.getUserId());
        UserSession userSession = userCredentialService.getUserSession();
        User user = userRepository.findByUserIdIgnoreCase(loginRequest.getUserId()).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
        if (loginRequest.getUserId().equalsIgnoreCase(loginRequest.getPassword())) {
            log.error("New password can't be userId  for user: {} ", loginRequest.getUserId());
            throw new BadRequestException("New password can't be userId", HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.hasText(loginRequest.getPassword()) && loginRequest.getPassword().length() < 8){
            log.error("Password length should at least 8 character  for user: {} ", loginRequest.getUserId());
            throw new BadRequestException("Password length should at least 8 character", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder, loginRequest.getPassword());
        user.setIsTemporaryPassword("Y");
        user.setIsPasswordActive("Y");
        user.setIsPasswordExpired(null);
        user.setLoginAttempt(0);
        user.setPasswordResetDate(LocalDate.now());
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(userSession.getUserId());
        userRepository.save(user);
        log.info("Password reset was successful, userId : {}", loginRequest.getUserId());
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<Response> forgetPassword(String userId) throws ObjectNotFoundException, InternalServerErrorException, BadRequestException {
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException("userId can't be null", HttpStatus.BAD_REQUEST);
        }
        String otp = RandomStringUtils.randomNumeric(6);
        String message = "Use OTP " + otp + " to reset your SVCL-FINNCUB password. Do not share the OTP or your number with anyone-SV Creditline Ltd";
        User user = getUser(userId);
        if (user != null && StringUtils.hasText(user.getMobileNumber())) {
            String mobileNumber = user.getMobileNumber();
            Long orgId = getActiveOrgId(userId);
            if (orgId == null) {
                log.error("User {} is not mapped with organization ", userId);
                return new ResponseEntity<>(new Response("User is not mapped with organization", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
            getVendorSmsLog(otp, message, user, mobileNumber, orgId);
            log.info("Otp sent to the registered mobile number");
            return new ResponseEntity<>(new Response("Otp sent to the registered mobile number", HttpStatus.OK), HttpStatus.OK);
        } else {
            log.error("User details not found for userId {}", userId);
            return new ResponseEntity<>(new Response("Your mobile is not mapped correctly", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    private void getVendorSmsLog(String otp, String message, User user, String mobileNumber, Long activeOrgId) throws InternalServerErrorException {
        VendorSmsLog vendorSmsLogData = new VendorSmsLog();
        vendorSmsLogData.setOrgId(activeOrgId);
        vendorSmsLogData.setSmsMobile(mobileNumber);
        vendorSmsLogData.setSmsText(message);
        vendorSmsLogData.setSmsType("FORGET"); // FORGET is for FORGET type
        vendorSmsLogData.setStatus("S"); // S is for SENT status
        vendorSmsLogData.setSmsOtp(otp);
        vendorSmsLogData.setSmsVendor("SMSJUST");
        vendorSmsLogData.setInsertedBy(user.getUserId());
        vendorSmsLogData.setInsertedOn(LocalDateTime.now());
        vendorSmsLogData = vendorSmsLogRepository.save(vendorSmsLogData);
        // hit sms API
        String responseId = smsUtil.sendSms(user.getMobileNumber(), message);
        // update response id in VendorSmsLog returned from API
        if (StringUtils.hasText(responseId)) {
            vendorSmsLogData.setStatus("D");  // Status D is for Delivered.
            vendorSmsLogData.setSmsResponse(responseId);
            vendorSmsLogData.setIsDelivered("N");
            vendorSmsLogRepository.save(vendorSmsLogData);
        } else {
            throw new InternalServerErrorException("Empty response received from vendor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long getActiveOrgId(String userId) {
        Long activeOrgId = null;
        if (StringUtils.hasText(userId)) {
            List<UserOrganizationMapping> orgList = userOrganizationMappingRepository.findById_UserId(userId);
            for (UserOrganizationMapping orgMapping : orgList) {
                if ("Y".equalsIgnoreCase(orgMapping.getActive())) {
                    activeOrgId = orgMapping.getId().getOrganizationId();
                    break;
                }
            }
        }
        return activeOrgId;
    }

    private User getUser(String userId) throws ObjectNotFoundException {
        return userRepository.findByUserIdIgnoreCase(userId).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + userId, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public ResponseEntity<Response> verifyForgetPasswordOtp(String otp, String userId) throws ObjectNotFoundException, BadRequestException {
        if (!StringUtils.hasText(otp) && !StringUtils.hasText(userId)) {
            log.error("otp cannot be empty.");
            throw new BadRequestException("otp cannot be empty.", HttpStatus.BAD_REQUEST);
        }
        // first check data is available in database
        User user = getUser(userId);
        if (user == null) {
            log.error("user details not found for userId : {} ", userId);
            throw new BadRequestException("user details not found ", HttpStatus.BAD_REQUEST);
        }
        String mobileNumber = user.getMobileNumber();
        Long activeOrgId = getActiveOrgId(userId);
        Optional<VendorSmsLog> vendorSmsLog = vendorSmsLogRepository.findTop1BySmsMobileAndOrgIdAndStatusAndSmsTypeAndInsertedOnGreaterThanOrderBySmsIdDesc(mobileNumber, activeOrgId, "D", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        // otp check
        if (vendorSmsLog.isPresent() && otp.equalsIgnoreCase(vendorSmsLog.get().getSmsOtp())) {
            vendorSmsLog.get().setStatus("U");    // U is for USED status
            vendorSmsLog.get().setUpdatedBy(userId);
            vendorSmsLog.get().setUpdatedOn(LocalDateTime.now());
            vendorSmsLogRepository.save(vendorSmsLog.get());
            user.setIsOtpValidated('Y');
            user.setIsUserValidated('Y');
            userRepository.save(user);
            log.info("OTP verified successfully for userId {}", userId);
            return new ResponseEntity<>(new Response("OTP verified successfully", HttpStatus.OK), HttpStatus.OK);
        } else {
            log.error("Invalid OTP entered by user {}", userId);
            return new ResponseEntity<>(new Response("Invalid OTP.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<Response> updatePassword(CreateNewPasswordRequest createNewPasswordRequest) throws NullPointerException, ObjectNotFoundException, BadRequestException {
        if (!createNewPasswordRequest.getNewPassword().equals(createNewPasswordRequest.getConfirmPassword())) {
            log.error("New password is not same as confirm password for userId : {} ", createNewPasswordRequest.getUserId());
            throw new BadRequestException("New password is not same as confirm password ", HttpStatus.BAD_REQUEST);
        }
        if (createNewPasswordRequest.getUserId().equalsIgnoreCase(createNewPasswordRequest.getNewPassword())) {
            log.error("New password can't be userId  for user: {} ", createNewPasswordRequest.getUserId());
            throw new BadRequestException("New password can't be userId", HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.hasText(createNewPasswordRequest.getNewPassword()) && createNewPasswordRequest.getNewPassword().length() < 8){
            log.error("Password length should at least 8 character  for user: {} ", createNewPasswordRequest.getUserId());
            throw new BadRequestException("Password length should at least 8 character", HttpStatus.BAD_REQUEST);
        }
        User user = getUser(createNewPasswordRequest.getUserId());
        String newPassword = passwordEncoder.encode(createNewPasswordRequest.getNewPassword());
        //      check new password with 5 old password
        String oldPassword = user.getOldPassword();
        if (oldPassword == null) {
            oldPassword = user.getPassword();
        } else {
            String[] oldPasswordList = oldPassword.split(PASSWORD_SEPARATOR);
            for (String pass : oldPasswordList) {
                if (BCrypt.checkpw(createNewPasswordRequest.getNewPassword(), pass)) {
                    log.error("New password matches with recent passwords  , userId : {}", createNewPasswordRequest.getUserId());
                    throw new BadRequestException("New password matches with recent passwords ", HttpStatus.BAD_REQUEST);
                }
            }
//          Maintain old passwords
            String oldPasswordUpdated = maintainPasswordHistory.maintainOldPasswordHistory(oldPasswordList, oldPassword, PASSWORD_SEPARATOR, newPassword);
            oldPassword = (new StringBuilder()).append(oldPasswordUpdated).toString();
        }
        String mobileNumber = user.getMobileNumber();
        Long activeOrgId = getActiveOrgId(user.getUserId());
        Optional<VendorSmsLog> vendorSmsLog = vendorSmsLogRepository.findTop1BySmsMobileAndOrgIdAndStatusAndSmsTypeAndInsertedOnGreaterThanOrderBySmsIdDesc(mobileNumber, activeOrgId, "U", "FORGET", LocalDateTime.now().minusMinutes(smsProperties.getOtpExpiryTime()));
        if (vendorSmsLog.isPresent() && createNewPasswordRequest.getOtp().equalsIgnoreCase(vendorSmsLog.get().getSmsOtp())) {
//            user.setPassword(passwordEncoder, createNewPasswordRequest.getNewPassword());
            user.setPassword(newPassword);
            user.setIsTemporaryPassword("N");
            user.setIsPasswordActive("Y");
            user.setIsPasswordExpired(null);
            user.setPasswordResetDate(LocalDate.now());
            user.setOldPassword(oldPassword);
            user.setUpdatedOn(LocalDateTime.now());
            user.setUpdatedBy(createNewPasswordRequest.getUserId());
            userRepository.save(user);
            log.info("Password reset was successful, userId : {}", createNewPasswordRequest.getUserId());
            return new ResponseEntity<>(new Response("Password reset was successful", HttpStatus.OK), HttpStatus.OK);
        } else {
            log.error("Otp is not verified.");
            return new ResponseEntity<>(new Response("Otp is not verified.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response sendCallbackMail(CallbackMailRequest callbackMailRequest) {
        Response response = new Response();
        try {
            if (isValidMailRequest(callbackMailRequest)) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo("info@sastechstudio.com");
                message.setSubject("New lead request for Finncub");
                if (callbackMailRequest.getEmail() != null) {
                    message.setText("Name : " + callbackMailRequest.getName() + "\n" + "Mobile Number : " + callbackMailRequest.getMobileNumber() + "\n" + "Email : " + callbackMailRequest.getEmail() + "\n" + "Remarks : " + callbackMailRequest.getDescription());
                } else {
                    message.setText("Name : " + callbackMailRequest.getName() + "\n" + "Mobile Number : " + callbackMailRequest.getMobileNumber() + "\n" + "Remarks : " + callbackMailRequest.getDescription());
                }
                mailSender.send(message);
                response.setStatus(HttpStatus.OK);
                response.setMessage("Mail Sent Successfully");
                response.setCode(HttpStatus.OK.value());
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Invalid Request");
                response.setCode(HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception exception) {
            log.error("Something went wrong while sending mail to admin. Reason: {}", exception.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Invalid Request");
            response.setCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    private boolean isValidMailRequest(CallbackMailRequest callbackMailRequest) {
        if (callbackMailRequest.getMobileNumber() == null || callbackMailRequest.getMobileNumber().isEmpty()) {
            return false;
        } else if (callbackMailRequest.getName() == null || callbackMailRequest.getName().isEmpty()) {
            return false;
        } else if (callbackMailRequest.getDescription() == null || callbackMailRequest.getDescription().isEmpty()) {
            return false;
        }
        return true;
    }
}