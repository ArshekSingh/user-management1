package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private Long branchId;
    private String designationType;
    private String bcId;
    private String extUserId;
    private String isFrozenBookFlag;
    private String imeiNumber;
    private String isPasswordActive;
}