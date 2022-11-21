package com.sts.finncub.usermanagement.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CreateNewPasswordRequest {
    private String userId;
    private String newPassword;
    private String confirmPassword;
}
