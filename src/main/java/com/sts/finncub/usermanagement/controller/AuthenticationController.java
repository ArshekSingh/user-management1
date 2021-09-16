package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.usermanagement.constants.RestMappingConstants;
import com.sts.finncub.usermanagement.exception.BadRequestException;
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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {

        Response<LoginResponse> response = new Response<>();


        try{
            if(!loginRequest.isValid()){
                throw new BadRequestException("Invalid values for userId/password",HttpStatus.BAD_REQUEST);
            }
            log.info("Request is valid");
            response.setResponseObject(authenticationService.login(loginRequest));
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setMessage(RestMappingConstants.SUCCESS);
        }catch (Exception e){
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.OK);
            response.setCode(HttpStatus.OK.value());

        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/signup")
    public ResponseEntity<Response<SignupResponse>> signup(@RequestBody SignupRequest signupRequest) throws BadRequestException {
        signupRequest.validate();
        log.info("Request is valid");
        return  ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS,authenticationService.signup(signupRequest),HttpStatus.OK));
    }


}
