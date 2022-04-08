package com.sts.finncub.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.assembler.SignUpConverter;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${password.old.count}")
    private Integer oldPasswordCount;

    @Value("${password.failed.count}")
    private Integer passwordFailedCount;

    private final String PASSWORD_SEPARATOR = ",,";

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

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserRedisRepository userRedisRepository, UserRoleMappingRepository userRoleMappingRepository, UserCredentialService userCredentialService, UserOrganizationMappingRepository userOrganizationMappingRepository, BranchMasterRepository branchMasterRepository, UserLoginLogRepository userLoginLogRepository, EmployeeRepository employeeRepository, MiscellaneousServiceRepository miscellaneousServiceRepository) {
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
            if ("N".equalsIgnoreCase(user.getIsPasswordActive())) {
				log.error("User password is blocked , userId : {}", loginRequest.getUserId());
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
            String authToken;
            try {
                authToken = saveToken(userSession);
				log.info("User session saved , userId : {}", loginRequest.getUserId());
                loginResponse.setAuthToken(authToken);
                loginResponse.setUserSession(gson.fromJson(userSession.getUserSessionJSON(), UserSession.class));
                try {
                    Employee employee = employeeRepository.findByUserId(loginRequest.getUserId()).orElse(null);
                    if(employee != null) {
                        loginResponse.setBaseLocation((employee.getBranchMaster() != null) ? employee.getBranchMaster().getBranchName() : "");
                        loginResponse.setDepartmentName((employee.getEmployeeDepartmentMaster() != null) ? employee.getEmployeeDepartmentMaster().getEmpDepartmentName() : "");
                        loginResponse.setDesignationName((employee.getEmployeeDesignationMaster() != null) ? employee.getEmployeeDesignationMaster().getEmpDesignationName() : "");
                        loginResponse.setDesignationType((employee.getEmployeeDesignationMaster() != null) ? employee.getEmployeeDesignationMaster().getEmpDesignationType() : "");
                    }
                    Optional<MiscellaneousService> miscellaneousServices = miscellaneousServiceRepository.findByKey("APP_VERSION");
                    if (miscellaneousServices.isPresent()) {
                        loginResponse.setAppVersion(miscellaneousServices.get().getValue());
                    }
                } catch (Exception exception) {
					log.error("Exception while fetching employee details for userId :{} , message : {}", loginRequest,
							exception.getMessage(), exception);
                }
            } catch (Exception e) {
				log.error("Exception occurred while login , userId : {} , message : {}", loginRequest.getUserId(),
						e.getMessage(), e);
                throw new InternalServerErrorException("Exception while saving token - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //save user Login log
            userLoginLog.setTokenId(authToken);
            userLoginLogRepository.save(userLoginLog);
			log.info("Login successful , userId : {}", loginRequest.getUserId());
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
            userSession.setEmail(user.getEmail());
            userSession.setIsTemporaryPassword(user.getIsTemporaryPassword());
            if (user.getUserRoleMapping() != null && !user.getUserRoleMapping().isEmpty()) {
                userSession.setRoles(user.getUserRoleMapping().stream().map(mapping -> mapping.getRoleMaster().getRoleName()).collect(Collectors.toSet()));
            }
            if (user.getUserBranchMapping() != null && !user.getUserBranchMapping().isEmpty()) {
                for (UserBranchMapping userBranchMapping : user.getUserBranchMapping()) {
                    if (userBranchMapping.getBranchMaster().getParentId() != null) {
                        parentIdList.add(userBranchMapping.getBranchMaster().getParentId());
                    }
                    branchIdMap.put(userBranchMapping.getBranchMaster().getBranchId(), userBranchMapping.getBranchMaster().getBranchCode() + " - " + userBranchMapping.getBranchMaster().getBranchName());
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                List<BranchMaster> divisionList = branchMasterRepository.findByBranchIdIn(parentIdList);
                parentIdList.clear();
                for (BranchMaster division : divisionList) {
                    if (division.getParentId() != null) {
                        parentIdList.add(division.getParentId());
                    }
                    divisionMap.put(division.getBranchId(), division.getBranchCode() + "-" + division.getBranchName());
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                List<BranchMaster> zoneList = branchMasterRepository.findByBranchIdIn(parentIdList);
                for (BranchMaster zone : zoneList) {
                    zoneMap.put(zone.getBranchId(), zone.getBranchCode() + "-" + zone.getBranchName());
                }
            }
            userSession.setName(user.getName());
            userSession.setType(user.getType());
            userSession.setUserId(user.getUserId());
            userSession.setOrganizationId(getActiveOrganizationId(user));
            userSession.setBranchMap(branchIdMap);
            userSession.setDivisionMap(divisionMap);
            userSession.setZoneMap(zoneMap);
        } catch (Exception ex) {
			log.error("Exception occurred while preparing BranchMap , DivisionMap and ZoneMap , message : {}", ex.getMessage(), ex);
            throw new InternalServerErrorException("Exception while set data in userSession object" + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return userSession;
    }

    public Long getActiveOrganizationId(User user) {
        Long activeOrgId = null;
        for (UserOrganizationMapping orgMapping : user.getUserOrganizationMapping()) {
            if ("Y".equalsIgnoreCase(orgMapping.getActive())) {
                activeOrgId = orgMapping.getId().getOrganizationId();
            }
        }
        return activeOrgId;
    }

    @Transactional
    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws BadRequestException {

        User newUser = SignUpConverter.convertToUser(signupRequest);
        String operationUserName = userCredentialService.getUserSession().getName();
        final Long organizationId = userCredentialService.getUserSession().getOrganizationId();

        newUser.setPassword(passwordEncoder, signupRequest.getPassword());

        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(organizationId, signupRequest.getUserType());
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        newUser.setUserId(userId);
		log.info("Generated user Id : {} , email : {}", newUser.getUserId(), newUser.getEmail());

        newUser = userRepository.save(newUser);

		log.info("Operation user name : {} , email : {} , organizationId : {}", operationUserName,
				signupRequest.getEmail(), organizationId);

		UserOrganizationMapping userOrganizationMapping = new UserOrganizationMapping(organizationId,
				newUser.getUserId(), operationUserName);
        userOrganizationMappingRepository.save(userOrganizationMapping);
//        if (signupRequest.getUserType().equals(UserType.EMP.name())) {
//            log.info("New user saved to db");
////            employeeRepository.save(new Employee(organizationId, employeeId, userId));
//        }

        if (signupRequest.hasRoles()) {
			log.info("Setting roles , userId :{} ", userId);

            userRoleMappingRepository.saveAll(signupRequest.getRoleList().stream().map(id -> new UserRoleMapping(userId, id, organizationId, operationUserName)).collect(Collectors.toSet()));
			log.info("Roles saved to db, userId :{} ", userId);
        }
        return SignUpConverter.convertToResponse(newUser);
    }

    @Override
    public Response<UserSession> verify(String authToken) throws ObjectNotFoundException {
        UserSession userSession = userRedisRepository.findById(authToken).orElseThrow(() -> new ObjectNotFoundException("User session not found, " + "Please login again!", HttpStatus.NOT_FOUND));

        return new Response<>(RestMappingConstants.SUCCESS, userSession, HttpStatus.OK);

    }

    private void updateLoginAttempt() {

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
        String tokenString = request.getHeader("Authorization");
        String token = tokenString.split(" ")[1];
        userRedisRepository.deleteById(token);
		log.info("logout successful");
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
            if (oldPasswordList.length < oldPasswordCount) {
                oldPassword = oldPassword + PASSWORD_SEPARATOR + user.getPassword();
            } else {
                String updatedOldPassword = "";
                for (int i = 1; i < oldPasswordList.length; i++) {
                    if (updatedOldPassword.isEmpty()) {
                        updatedOldPassword = oldPasswordList[i];
                    } else {
                        updatedOldPassword = updatedOldPassword + PASSWORD_SEPARATOR + oldPasswordList[i];
                    }
                }
                oldPassword = updatedOldPassword + PASSWORD_SEPARATOR + user.getPassword();
            }
        }
//      update new password
        user.setOldPassword(oldPassword);
        user.setPassword(passwordEncoder, request.getNewPassword());
        user.setIsTemporaryPassword("N");
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(userSession.getUserId());
        userRepository.save(user);
		log.info("Password updated successfully , userId : {}", request.getUserId());
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> resetPassword(LoginRequest loginRequest) throws ObjectNotFoundException {
        Response response = new Response();
		log.info("Fetching userSession for resetPassword request, userId : {} ", loginRequest.getUserId());
        UserSession userSession = userCredentialService.getUserSession();
        User user = userRepository.findByUserIdIgnoreCase(loginRequest.getUserId()).orElseThrow(() -> new ObjectNotFoundException("Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder, loginRequest.getPassword());
        user.setIsTemporaryPassword("Y");
        user.setIsPasswordActive("Y");
        user.setLoginAttempt(0);
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(userSession.getUserId());
        userRepository.save(user);
		log.info("Password reset was successful, userId : {}", loginRequest.getUserId());
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }
}