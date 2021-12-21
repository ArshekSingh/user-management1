package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.UserRedisRepository;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.response.SignupResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final UserRedisRepository userRedisRepository;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserRedisRepository userRedisRepository) {
        this.authenticationService = authenticationService;
        this.userRedisRepository = userRedisRepository;
    }

    @PostMapping("login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest loginRequest) throws BadRequestException,
            ObjectNotFoundException, InternalServerErrorException {
        Response<LoginResponse> response = new Response<>();
        if (!loginRequest.isValid()) {
            throw new BadRequestException("Invalid userId / password", HttpStatus.BAD_REQUEST);
        }
        log.info("Request is valid");
        response.setData(authenticationService.login(loginRequest));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.SUCCESS);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/signup")
    public ResponseEntity<Response<SignupResponse>> signup(@RequestBody SignupRequest signupRequest) throws BadRequestException {
        signupRequest.validate();
        log.info("Request is valid");
        return ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS, authenticationService.signup(signupRequest), HttpStatus.OK));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        Response response = new Response();
        authenticationService.logout(request);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.LOGGED_OUT);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/changePassword")
    public ResponseEntity<Response> changePassword(@RequestBody String password) throws ObjectNotFoundException {
        Response response = new Response();
        authenticationService.changePassword(password);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.CHANGED_PASSWORD);
        return ResponseEntity.ok(response);
    }
}