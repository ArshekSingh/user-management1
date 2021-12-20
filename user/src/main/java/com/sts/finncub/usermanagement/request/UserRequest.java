package com.sts.finncub.usermanagement.request;

import com.sts.finncub.core.enums.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String name;
    private String userId;
    private String email;
    private UserType type;
    private String password;
    private boolean isActive;
    private Character isUserValidated;
    private String mobileNumber;
    private Character isOtpValidated;
    private String passwordResetDate;
    private String disabledOn;
    private String approvedOn;
    private String approvedBy;
    private String insertedOn;
    private String isFrozenBookFlag;
}
