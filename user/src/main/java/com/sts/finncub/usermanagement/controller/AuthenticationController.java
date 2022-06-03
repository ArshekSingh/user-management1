package com.sts.finncub.usermanagement.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
	public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) throws BadRequestException,
            ObjectNotFoundException, InternalServerErrorException {
		Response response = new Response();
        if (!loginRequest.isValid()) {
			log.error("Invalid Login Request received , userId : {}", loginRequest.getUserId());
            throw new BadRequestException("Invalid userId / password", HttpStatus.BAD_REQUEST);
        }
		response.setData(authenticationService.login(loginRequest));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage(RestMappingConstants.SUCCESS);

        return ResponseEntity.ok(response);
    }

    // API replaces with User Controller Add user API
    @PostMapping("/api/signup")
    public ResponseEntity<Response> signup(@RequestBody SignupRequest signupRequest) throws BadRequestException {
        signupRequest.validate();
		log.info("Signup Request received , email : {} , userType : {}", signupRequest.getEmail(),
				signupRequest.getUserType());
        return ResponseEntity.ok(new Response(RestMappingConstants.SUCCESS,
                authenticationService.signup(signupRequest), HttpStatus.OK));
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
    public ResponseEntity<Response> changePassword(HttpServletRequest httpServletRequest, @RequestBody LoginRequest request) throws ObjectNotFoundException, BadRequestException {
		log.info("changePassword request received , userId : {} ", request.getUserId());
        ResponseEntity<Response> responseEntity = authenticationService.changePassword(request);
        authenticationService.logout(httpServletRequest);
        return responseEntity;
    }

    @PostMapping("/api/resetPassword")
    public ResponseEntity<Response> resetPassword(@RequestBody LoginRequest loginRequest)
            throws ObjectNotFoundException {
		log.info("resetPassword request received , userId : {} ", loginRequest.getUserId());
        ResponseEntity<Response> responseEntity = authenticationService.resetPassword(loginRequest);
        return responseEntity;
    }
}