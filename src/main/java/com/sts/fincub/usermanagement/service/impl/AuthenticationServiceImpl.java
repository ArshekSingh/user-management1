package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.usermanagement.entity.Role;
import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.entity.UserSession;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.repository.UserRedisRepository;
import com.sts.fincub.usermanagement.repository.UserRepository;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.response.SignupResponse;
import com.sts.fincub.usermanagement.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sts.fincub.usermanagement.constants.RestMappingConstants.SUCCESS;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRedisRepository userRedisRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder,UserRedisRepository userRedisRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRedisRepository = userRedisRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException,BadRequestException{
        LoginResponse loginResponse = new LoginResponse();
        User user = userRepository
                        .findByName(loginRequest.getUserId())
                        .orElseThrow(()->new ObjectNotFoundException(
                                                        "Invalid userId - "+loginRequest.getUserId(),
                                                            HttpStatus.NOT_FOUND));
        if(user.isPasswordCorrect(loginRequest.getPassword())){
            throw new BadRequestException("Invalid password",HttpStatus.BAD_REQUEST);
        }
        String authToken = saveToRedis(user.toSessionObject());
        loginResponse.setUserRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
        loginResponse.setAuthToken(authToken);
        loginResponse.setUserType(user.getType());
        return loginResponse;

    }

    @Override
    public Response<String> signup(SignupRequest signupRequest) throws BadRequestException{
        Optional<User> userQuery = userRepository.findByName(signupRequest.getName());

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


    private String saveToRedis(UserSession userSession){
        return userRedisRepository.save(userSession).getId();
    }


}
