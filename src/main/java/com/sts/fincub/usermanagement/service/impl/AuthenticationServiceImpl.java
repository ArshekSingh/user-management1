package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.authentication.validation.RedisRepository;
import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.entity.UserSession;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.InternalServerErrorException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.repository.UserRedisRepository;
import com.sts.fincub.usermanagement.repository.UserRepository;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sts.fincub.usermanagement.constants.RestMappingConstants.SUCCESS;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder,
                                     UserRedisRepository userRedisRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
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
    public Response<String> signup(SignupRequest signupRequest) throws BadRequestException{
        Optional<User> userQuery = userRepository.findByUserId(signupRequest.getName());

        if(userQuery.isPresent()){
            throw new BadRequestException("User with name -"+ signupRequest.getName()+" already exists", HttpStatus.BAD_REQUEST);
        }

        userRepository.save(signupRequest.toDAO(passwordEncoder));
        return new Response<>("Success",HttpStatus.OK);
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
