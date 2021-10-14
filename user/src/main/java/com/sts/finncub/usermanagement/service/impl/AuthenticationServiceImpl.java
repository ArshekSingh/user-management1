package com.sts.finncub.usermanagement.service.impl;

import com.google.gson.Gson;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.dto.DivisionDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.enums.UserType;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.usermanagement.assembler.SignUpConverter;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import com.sts.finncub.usermanagement.service.UserCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    private final UserOrganisationMappingRepository userOrganisationMappingRepository;
    private final EmployeeRepository employeeRepository;
    private final BranchMasterRepository branchMasterRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                                     UserRedisRepository userRedisRepository, UserRoleMappingRepository userRoleMappingRepository,
                                     UserCredentialService userCredentialService, UserOrganisationMappingRepository userOrganisationMappingRepository,
                                     EmployeeRepository employeeRepository, BranchMasterRepository branchMasterRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.userCredentialService = userCredentialService;
        this.userOrganisationMappingRepository = userOrganisationMappingRepository;
        this.employeeRepository = employeeRepository;
        this.branchMasterRepository = branchMasterRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException, InternalServerErrorException {
        Gson gson = new Gson();
        LoginResponse loginResponse = new LoginResponse();
        log.info("Request received for userId -" + loginRequest.getUserId());
        User user = userRepository
                .findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Invalid userId - " + loginRequest.getUserId(),
                        HttpStatus.NOT_FOUND));
        if (!user.isPasswordCorrect(loginRequest.getPassword())) {
            throw new BadRequestException("Invalid password", HttpStatus.BAD_REQUEST);
        }

        UserSession userSession = toSessionObject(user);
        try {
            String authToken = saveToken(userSession);
            log.info("User session saved with id -" + authToken);
            loginResponse.setAuthToken(authToken);
            loginResponse.setUserSession(gson.fromJson(userSession.getUserSessionJSON(), UserSession.class));
        } catch (Exception e) {
            log.error("Exception- {}", e);
            throw new InternalServerErrorException("Exception while saving token - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return loginResponse;
    }

    public UserSession toSessionObject(User user) throws InternalServerErrorException {
        UserSession userSession = new UserSession();
        try {
            Set<Integer> parentIdList = new HashSet<>();
            Map<Integer, String> branchIdMap = new HashMap<>();
            Map<Integer, String> divisionMap = new HashMap<>();
            List<BranchMaster> divisionList = new ArrayList<>();
            userSession.setEmail(user.getEmail());
            if (user.getUserRoleMapping() != null && !user.getUserRoleMapping().isEmpty()) {
                userSession.setRoles(user.getUserRoleMapping()
                        .stream()
                        .map(mapping -> mapping.getId().getRoleId())
                        .collect(Collectors.toSet()));
            }
            if (user.getUserBranchMapping() != null && !user.getUserBranchMapping().isEmpty()) {
                for (UserBranchMapping userBranchMapping : user.getUserBranchMapping()) {
                    if (userBranchMapping.getBranchMaster().getParentId() != null) {
                        parentIdList.add(userBranchMapping.getBranchMaster().getParentId());
                    }
                    branchIdMap.put(userBranchMapping.getBranchMaster().getBranchId(), StringUtils.hasText(userBranchMapping.getBranchMaster().getBranchCode()) ? userBranchMapping.getBranchMaster().getBranchCode() : "");
                }
            }
            if (!CollectionUtils.isEmpty(parentIdList)) {
                divisionList = branchMasterRepository.findByBranchIdIn(parentIdList);
                for (BranchMaster branchMaster : divisionList) {
                    divisionMap.put(branchMaster.getBranchId(), StringUtils.hasText(branchMaster.getBranchCode()) ? branchMaster.getBranchCode() : "");
                }
            }
            userSession.setName(user.getName());
            userSession.setType(user.getType().name());
            userSession.setUserId(user.getUserId());
            userSession.setOrganisationId(getActiveOrganisationId(user));
            userSession.setBranchMap(branchIdMap);
            userSession.setDivisionMap(divisionMap);

        } catch (Exception ex) {
            log.error("Exception- {}", ex);
            throw new InternalServerErrorException("Exception while set data in userSession object" + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return userSession;
    }

    public Long getActiveOrganisationId(User user) {
        Long activeOrgId = null;
        for (UserOrganisationMapping orgMapping : user.getUserOrganisationMapping()) {
            if ("Y".equalsIgnoreCase(orgMapping.getActive())) {
                activeOrgId = orgMapping.getId().getOrganisationId();
            }
        }
        return activeOrgId;
    }

    @Transactional
    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws BadRequestException {

        User newUser = SignUpConverter.convertToUser(signupRequest);
        String operationUserName = userCredentialService.getUserData().getName();
        final Long organisationId = userCredentialService.getUserData().getOrganisationId();

        newUser.setPassword(passwordEncoder, signupRequest.getPassword());

        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(organisationId, signupRequest.getUserType());
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        newUser.setUserId(userId);
        log.info("Generated user Id -" + newUser.getUserId());

        newUser = userRepository.save(newUser);

        log.info("Operation user name - " + operationUserName);


        log.info("Operation user organisationId" + organisationId);
        UserOrganisationMapping userOrganisationMapping = new UserOrganisationMapping(organisationId, newUser.getUserId(), operationUserName);
        userOrganisationMappingRepository.save(userOrganisationMapping);
        if (signupRequest.getUserType().equals(UserType.EMP.name())) {
            log.info("New user saved to db");
            employeeRepository.save(new Employee(organisationId, employeeId, userId));
        }

        if (signupRequest.hasRoles()) {
            log.info("Setting roles for userId - " + userId);

            userRoleMappingRepository.saveAll(signupRequest.getRoleList()
                    .stream()
                    .map(id -> new UserRoleMapping(userId, id, organisationId, operationUserName))
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


}
