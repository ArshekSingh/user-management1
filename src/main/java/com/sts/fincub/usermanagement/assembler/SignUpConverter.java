package com.sts.fincub.usermanagement.assembler;

import com.sts.fincub.usermanagement.entity.Role;
import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.request.SignupRequest;
import com.sts.fincub.usermanagement.response.SignupResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SignUpConverter {
    public static User convertToUser(SignupRequest signupRequest){
        User user = new User();
        user.setActive(true);
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setMobileNumber(signupRequest.getMobile());
        user.setType(signupRequest.getUserType());
        user.setPasswordResetDate(LocalDate.now());
        user.setInsertedOn(LocalDate.now());
        user.setInsertedBy(signupRequest.getName());
        return user;
    }

    public static SignupResponse convertToResponse(User user){
        SignupResponse response = new SignupResponse();
        response.setName(user.getName());
        response.setUserId(user.getUserId());
        response.setUserType(user.getType());
        return response;
    }
}
