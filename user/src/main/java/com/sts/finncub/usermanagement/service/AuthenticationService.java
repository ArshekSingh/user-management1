package com.sts.finncub.usermanagement.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.SignupResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException, InternalServerErrorException;

    SignupResponse signup(SignupRequest signupRequest) throws BadRequestException;

	Response verify(String authToken) throws ObjectNotFoundException;

    ResponseEntity<Response> logout(HttpServletRequest request);

    ResponseEntity<Response> changePassword(LoginRequest password) throws ObjectNotFoundException, BadRequestException;

    ResponseEntity<Response> resetPassword(LoginRequest loginRequest) throws ObjectNotFoundException;
}