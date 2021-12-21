package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.entity.User;
import com.sts.finncub.usermanagement.request.SignupRequest;
import com.sts.finncub.usermanagement.response.SignupResponse;

import java.time.LocalDate;

public class SignUpConverter {
    public static User convertToUser(SignupRequest signupRequest) {
        User user = new User();
        user.setIsActive("Y");
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setMobileNumber(signupRequest.getMobile());
        user.setType(signupRequest.getUserType());
        user.setPasswordResetDate(LocalDate.now());
        user.setInsertedOn(LocalDate.now());
        user.setInsertedBy(signupRequest.getName());
        return user;
    }

    public static SignupResponse convertToResponse(User user) {
        SignupResponse response = new SignupResponse();
        response.setName(user.getName());
        response.setUserId(user.getUserId());
        response.setUserType(user.getType());
        return response;
    }
}
