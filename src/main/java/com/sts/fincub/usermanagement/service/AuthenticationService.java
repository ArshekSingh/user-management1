package com.sts.fincub.usermanagement.service;

import com.sts.fincub.usermanagement.entity.UserSession;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.InternalServerErrorException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.response.SignupResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest) throws ObjectNotFoundException,BadRequestException, InternalServerErrorException;
    SignupResponse signup(SignupRequest signupRequest) throws BadRequestException;
    Response<UserSession> verify(String authToken) throws ObjectNotFoundException;

}
