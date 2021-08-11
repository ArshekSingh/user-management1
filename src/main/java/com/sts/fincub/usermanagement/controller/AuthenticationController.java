package com.sts.fincub.usermanagement.controller;

import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.response.SignupResponse;
import com.sts.fincub.usermanagement.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest loginRequest) throws BadRequestException, ObjectNotFoundException {
        if(!loginRequest.isValid()){
            throw new BadRequestException("Invalid values for userId/password",HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new Response<>("Success",authenticationService.login(loginRequest), HttpStatus.OK));
    }


    @PostMapping("/api/signup")
    public ResponseEntity<Response> signup(@RequestBody SignupRequest signupRequest) throws BadRequestException {
        return  ResponseEntity.ok(authenticationService.signup(signupRequest));
    }


}
