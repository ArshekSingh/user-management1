package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.exception.InternalServerErrorException;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.CallbackMailRequest;
import com.sts.finncub.usermanagement.request.CreateNewPasswordRequest;
import com.sts.finncub.usermanagement.request.LoginRequest;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.LoginResponse;
import com.sts.finncub.usermanagement.response.SignupResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws ObjectNotFoundException, BadRequestException, InternalServerErrorException;

    SignupResponse signup(SignupRequest signupRequest) throws BadRequestException;

    Response verify(String authToken) throws ObjectNotFoundException;

    Response logout(HttpServletRequest request);

    Response changePassword(LoginRequest password) throws ObjectNotFoundException, BadRequestException;

    Response resetPassword(LoginRequest loginRequest) throws ObjectNotFoundException, BadRequestException;

    Response forgetPassword(String userId) throws ObjectNotFoundException, InternalServerErrorException, BadRequestException;

    ResponseEntity<Response> verifyForgetPasswordOtp(String otp, String userId) throws ObjectNotFoundException, BadRequestException;

    ResponseEntity<Response> updatePassword(CreateNewPasswordRequest createNewPasswordRequest) throws ObjectNotFoundException, BadRequestException;

    Response sendCallbackMail(CallbackMailRequest callbackMailRequest);
}