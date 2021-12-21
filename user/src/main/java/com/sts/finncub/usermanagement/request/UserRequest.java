package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String name;
    private String userId;
    private String email;
    private String type;
    private String password;
    private String isActive;
    private String mobileNumber;
    private String passwordResetDate;
    private boolean employeeCreate;
}
