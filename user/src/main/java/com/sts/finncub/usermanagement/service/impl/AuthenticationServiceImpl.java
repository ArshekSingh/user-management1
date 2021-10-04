package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.usermanagement.assembler.SignUpConverter;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import com.sts.finncub.usermanagement.service.UserCredentialService;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.enums.UserType;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder,
                                     UserRedisRepository userRedisRepository,UserRoleMappingRepository userRoleMappingRepository,
                                     UserCredentialService userCredentialService,UserOrganisationMappingRepository userOrganisationMappingRepository,
                                     EmployeeRepository employeeRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.userCredentialService = userCredentialService;
        this.userOrganisationMappingRepository = userOrganisationMappingRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException,BadRequestException, InternalServerErrorException {
        LoginResponse loginResponse = new LoginResponse();

        log.info("Request received for userId -"+loginRequest.getUserId());

        User user = userRepository
                        .findByUserId(loginRequest.getUserId())
                        .orElseThrow(()->new ObjectNotFoundException(
                                                        "Invalid userId - "+loginRequest.getUserId(),
                                                            HttpStatus.NOT_FOUND));
        if(!user.isPasswordCorrect(loginRequest.getPassword())){
            throw new BadRequestException("Invalid password",HttpStatus.BAD_REQUEST);
        }
        try {
            String authToken = saveToken(user.toSessionObject());
            log.info("User session saved with id -"+authToken);
            loginResponse.setAuthToken(authToken);
            loginResponse.setUserType(user.getType().name());
        }catch (Exception e){
            log.error("Exception- {}",e);
            throw new InternalServerErrorException("Exception while saving token - "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
//        loginResponse.setUserRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));

        return loginResponse;

    }

    @Transactional
    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws BadRequestException {

        User newUser = SignUpConverter.convertToUser(signupRequest);
        String operationUserName = userCredentialService.getUserData().getName();
        final Long organisationId =  userCredentialService.getUserData().getOrganisationId();

        newUser.setPassword(passwordEncoder,signupRequest.getPassword());

        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(organisationId,signupRequest.getUserType());
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        newUser.setUserId(userId);
        log.info("Generated user Id -"+newUser.getUserId());

        newUser = userRepository.save(newUser);

        log.info("Operation user name - "+operationUserName);


        log.info("Operation user organisationId"+organisationId);
        UserOrganisationMapping userOrganisationMapping = new UserOrganisationMapping(organisationId,newUser.getUserId(),operationUserName);
        userOrganisationMappingRepository.save(userOrganisationMapping);
        if(signupRequest.getUserType().equals(UserType.EMP.name())) {
            log.info("New user saved to db");
            employeeRepository.save(new Employee(organisationId, employeeId, userId));
        }

        if(signupRequest.hasRoles()) {
            log.info("Setting roles for userId - "+userId);

            userRoleMappingRepository.saveAll(signupRequest.getRoleList()
                                                .stream()
                                                .map(id -> new UserRoleMapping(userId,id,organisationId,operationUserName))
                                                .collect(Collectors.toSet()));
            log.info("Roles saved to db");
        }
        return SignUpConverter.convertToResponse(newUser);
    }

    @Override
    public Response<UserSession> verify(String authToken) throws ObjectNotFoundException{
        UserSession userSession = userRedisRepository.findById(authToken)
                                                    .orElseThrow(
                                                            ()-> new ObjectNotFoundException("User session not found, " +
                                                                    "Please login again!",HttpStatus.NOT_FOUND));

        return new Response<>(RestMappingConstants.SUCCESS,userSession,HttpStatus.OK);

    }


    private String saveToken(UserSession userSession) {
        log.info("saving user session");
        return userRedisRepository.save(userSession).getId();
    }


}
