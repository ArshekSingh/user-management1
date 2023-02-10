package com.sts.finncub.usermanagement.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RamsonUserRequest {
    private String name;
    private String userId;
    private String email;
    private String type;
    private String active;
    private Character isUserValidated;
    private String mobileNumber;
    private Character isOtpValidated;
    private String password;
    private String disabledOn;
    private String approvedOn;
    private String approvedBy;
    private String isFrozenBookFlag;
    private String bcId;
    private String extUserId;
    private Long orgId;
    private Long roleId;
    private Integer branchId;
    private String branchCode;
    private String insertedBy;
    private String insertedOn;
    private String passwordResetDate;
    private boolean employeeCreate;
    private String designationType;
}