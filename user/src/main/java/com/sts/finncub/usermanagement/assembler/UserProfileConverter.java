package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.entity.User;
import com.sts.finncub.usermanagement.response.UserProfileResponse;

public class UserProfileConverter {
    public static UserProfileResponse convertToProfile(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setUserName(user.getName());
        response.setUserType(user.getType());
        response.setEmail(user.getEmail());
        return response;
    }
}
