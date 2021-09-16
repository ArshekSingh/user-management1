package com.sts.finncub.usermanagement.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String userType;
    private String userId;
    private String userName;
    private String mobile;
    private String email;
}
