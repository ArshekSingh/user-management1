package com.sts.finncub.usermanagement.request;

import lombok.Data;

@Data
public class CreateNewPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
