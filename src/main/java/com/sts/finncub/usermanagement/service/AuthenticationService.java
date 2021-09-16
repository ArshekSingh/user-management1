package com.sts.finncub.usermanagement.service;

import com.sts.finncub.usermanagement.entity.UserSession;
import com.sts.finncub.usermanagement.exception.BadRequestException;
import com.sts.finncub.usermanagement.exception.InternalServerErrorException;
import com.sts.finncub.usermanagement.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.response.SignupResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException, InternalServerErrorException;
    SignupResponse signup(SignupRequest signupRequest) throws BadRequestException;
    Response<UserSession> verify(String authToken) throws ObjectNotFoundException;

}
