package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.usermanagement.assembler.SignUpConverter;
import com.sts.fincub.usermanagement.entity.*;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.InternalServerErrorException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.repository.*;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.response.SignupResponse;
import com.sts.fincub.usermanagement.service.AuthenticationService;
import com.sts.fincub.usermanagement.service.UserCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sts.fincub.usermanagement.constants.RestMappingConstants.SUCCESS;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    private final UserOrganisationMappingRepository userOrganisationMappingRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder,
                                     UserRedisRepository userRedisRepository,UserRoleMappingRepository userRoleMappingRepository,
                                     UserCredentialService userCredentialService,UserOrganisationMappingRepository userOrganisationMappingRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.userCredentialService = userCredentialService;
        this.userOrganisationMappingRepository = userOrganisationMappingRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException,BadRequestException, InternalServerErrorException {
        LoginResponse loginResponse = new LoginResponse();

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
            loginResponse.setAuthToken(authToken);
            loginResponse.setUserType(user.getType());
        }catch (Exception e){
            log.error("Exception- {}",e);
            throw new InternalServerErrorException("Exception while saving token - "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
//        loginResponse.setUserRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));

        return loginResponse;

    }


    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws BadRequestException{

        User newUser = SignUpConverter.convertToUser(signupRequest);

        newUser.setPassword(passwordEncoder,signupRequest.getPassword());

        newUser.setUserId(userRepository.getGeneratedUserId());

        newUser = userRepository.save(newUser);
        String operationUserName = userCredentialService.getUserData().getName();
        final Long organisationId =  userCredentialService.getUserData().getOrganisationId();
        UserOrganisationMapping userOrganisationMapping = new UserOrganisationMapping(organisationId,newUser.getUserId(),operationUserName);
        userOrganisationMappingRepository.save(userOrganisationMapping);


        if(signupRequest.hasRoles()) {
            final String userId = newUser.getUserId();

            userRoleMappingRepository.saveAll(signupRequest.getRoleList()
                                                .stream()
                                                .map(id -> new UserRoleMapping(userId,id,organisationId,operationUserName))
                                                .collect(Collectors.toSet()));
        }
        return SignUpConverter.convertToResponse(newUser);
    }

    @Override
    public Response<UserSession> verify(String authToken) throws ObjectNotFoundException{
        UserSession userSession = userRedisRepository.findById(authToken)
                                                    .orElseThrow(
                                                            ()-> new ObjectNotFoundException("User session not found, " +
                                                                    "Please login again!",HttpStatus.NOT_FOUND));

        return new Response<>(SUCCESS,userSession,HttpStatus.OK);

    }


    private String saveToken(UserSession userSession) {
        return userRedisRepository.save(userSession).getId();
    }


}
