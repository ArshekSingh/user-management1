package com.sts.finncub.usermanagement.service.impl;

import com.google.gson.Gson;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.assembler.SignUpConverter;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${old.password.count}")
    private Integer oldPasswordCount;

    private final String PASSWORD_SEPARATOR = ",,";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    private final UserOrganizationMappingRepository userOrganizationMappingRepository;
    private final BranchMasterRepository branchMasterRepository;
    private final UserLoginLogRepository userLoginLogRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserRedisRepository userRedisRepository,
                                     UserRoleMappingRepository userRoleMappingRepository, UserCredentialService userCredentialService,
                                     UserOrganizationMappingRepository userOrganizationMappingRepository, BranchMasterRepository branchMasterRepository,
                                     UserLoginLogRepository userLoginLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.userCredentialService = userCredentialService;
        this.userOrganizationMappingRepository = userOrganizationMappingRepository;
        this.branchMasterRepository = branchMasterRepository;
        this.userLoginLogRepository = userLoginLogRepository;
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
            log.info("Request received for userId -" + loginRequest.getUserId());
            User user = userRepository
                    .findByUserIdIgnoreCase(loginRequest.getUserId()).orElseThrow(()
                            -> new ObjectNotFoundException("Invalid userId - " + loginRequest.getUserId(), HttpStatus.NOT_FOUND));
            if ("N".equalsIgnoreCase(user.getIsActive())) {
                throw new BadRequestException("User is not active.", HttpStatus.BAD_REQUEST);
            }
            if (!user.isPasswordCorrect(loginRequest.getPassword())) {
                throw new BadRequestException("Invalid password", HttpStatus.BAD_REQUEST);
            }

            UserSession userSession = toSessionObject(user);
            String authToken;
            try {
                authToken = saveToken(userSession);
                log.info("User session saved with id -" + authToken);
                loginResponse.setAuthToken(authToken);
                loginResponse.setUserSession(gson.fromJson(userSession.getUserSessionJSON(), UserSession.class));
            } catch (Exception e) {
                log.error("Exception- {}", e.getMessage());
                throw new InternalServerErrorException("Exception while saving token - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //save user Login log
            userLoginLog.setTokenId(authToken);
            userLoginLogRepository.save(userLoginLog);

        } catch (Exception exception) {
            log.error("Exception- {}", exception.getMessage());
            userLoginLog.setFailureReason(exception.getMessage());
            userLoginLog.setStatus("E");
            try{
                userLoginLogRepository.save(userLoginLog);
            }catch (Exception ex){
                log.error("Exception while saving UserLogin log - {} ", ex.getMessage());
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
            List<BranchMaster> divisionList;
            userSession.setEmail(user.getEmail());
            userSession.setIsTemporaryPassword(user.getIsActive());
            if (user.getUserRoleMapping() != null && !user.getUserRoleMapping().isEmpty()) {
                userSession.setRoles(user.getUserRoleMapping()
                        .stream()
                        .map(mapping -> mapping.getRoleMaster().getRoleName())
                        .collect(Collectors.toSet()));
            }
            if (user.getUserBranchMapping() != null && !user.getUserBranchMapping().isEmpty()) {
                for (UserBranchMapping userBranchMapping : user.getUserBranchMapping()) {
                    if (userBranchMapping.getBranchMaster().getParentId() != null) {
                        parentIdList.add(userBranchMapping.getBranchMaster().getParentId());
                    }
                    branchIdMap.put(userBranchMapping.getBranchMaster().getBranchId(), userBranchMapping.getBranchMaster().getBranchCode()+" - "+userBranchMapping.getBranchMaster().getBranchName());
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                divisionList = branchMasterRepository.findByBranchIdIn(parentIdList);
                for (BranchMaster branchMaster : divisionList) {
                    divisionMap.put(branchMaster.getBranchId(), branchMaster.getBranchCode()+"-"+branchMaster.getBranchName());
                }
            }
            userSession.setName(user.getName());
            userSession.setType(user.getType());
            userSession.setUserId(user.getUserId());
            userSession.setOrganizationId(getActiveOrganizationId(user));
            userSession.setBranchMap(branchIdMap);
            userSession.setDivisionMap(divisionMap);

        } catch (Exception ex) {
            log.error("Exception- {}", ex.getMessage());
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
        log.info("Generated user Id -" + newUser.getUserId());

        newUser = userRepository.save(newUser);

        log.info("Operation user name - " + operationUserName);


        log.info("Operation user organizationId" + organizationId);
        UserOrganizationMapping userOrganizationMapping = new UserOrganizationMapping(organizationId, newUser.getUserId(), operationUserName);
        userOrganizationMappingRepository.save(userOrganizationMapping);
//        if (signupRequest.getUserType().equals(UserType.EMP.name())) {
//            log.info("New user saved to db");
////            employeeRepository.save(new Employee(organizationId, employeeId, userId));
//        }

        if (signupRequest.hasRoles()) {
            log.info("Setting roles for userId - " + userId);

            userRoleMappingRepository.saveAll(signupRequest.getRoleList()
                    .stream()
                    .map(id -> new UserRoleMapping(userId, id, organizationId, operationUserName))
                    .collect(Collectors.toSet()));
            log.info("Roles saved to db");
        }
        return SignUpConverter.convertToResponse(newUser);
    }

    @Override
    public Response<UserSession> verify(String authToken) throws ObjectNotFoundException {
        UserSession userSession = userRedisRepository.findById(authToken)
                .orElseThrow(
                        () -> new ObjectNotFoundException("User session not found, " +
                                "Please login again!", HttpStatus.NOT_FOUND));

        return new Response<>(RestMappingConstants.SUCCESS, userSession, HttpStatus.OK);

    }

    private String saveToken(UserSession userSession) {
        Gson gson = new Gson();
        log.info("saving user session");
        userSession.setUserSessionJSON(gson.toJson(userSession));
        return userRedisRepository.save(userSession).getId();
    }

    @Override
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        Response response = new Response();
        String tokenString = request.getHeader("Authorization");
        String token = tokenString.split(" ")[1];
        userRedisRepository.deleteById(token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> changePassword(LoginRequest request) throws ObjectNotFoundException, BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
//      validate password(Regex)
        User user = userRepository.findByUserIdIgnoreCase(userSession.getUserId()).orElseThrow(() -> new ObjectNotFoundException(
                "Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
//      check current password
        if (!user.isPasswordCorrect(request.getPassword())) {
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
                    throw new BadRequestException("New password matches with recent passwords ", HttpStatus.BAD_REQUEST);
                }
            }
//          Maintain old passwords
            if (oldPasswordList.length < oldPasswordCount) {
                oldPassword = oldPassword + PASSWORD_SEPARATOR + user.getPassword();
            } else {
                String updatedOldPassword = "";
                for (int i= 1; i < oldPasswordList.length; i++) {
                    if (updatedOldPassword.isEmpty()) {
                        updatedOldPassword = oldPasswordList[i];
                    } else {
                        updatedOldPassword = updatedOldPassword + PASSWORD_SEPARATOR+oldPasswordList[i];
                    }
                }
                oldPassword = updatedOldPassword + PASSWORD_SEPARATOR + user.getPassword();
            }
        }
//      update new password
        user.setOldPassword(oldPassword);
        user.setPassword(passwordEncoder, request.getNewPassword());
        user.setIsTemporaryPassword("N");
        user.setUpdatedOn(LocalDate.now());
        user.setUpdatedBy(userSession.getUserId());
        userRepository.save(user);

        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> resetPassword(LoginRequest loginRequest) throws ObjectNotFoundException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        User user = userRepository.findByUserIdIgnoreCase(loginRequest.getUserId()).orElseThrow(() -> new ObjectNotFoundException(
                "Invalid userId - " + userSession.getUserId(), HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder, loginRequest.getPassword());
        user.setIsTemporaryPassword("Y");
        user.setUpdatedOn(LocalDate.now());
        user.setUpdatedBy(userSession.getUserId());
        userRepository.save(user);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }
}