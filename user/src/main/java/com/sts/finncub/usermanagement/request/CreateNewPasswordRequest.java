package com.sts.finncub.usermanagement.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Slf4j
public class CreateNewPasswordRequest {
    private String userId;
    private String otp;
    @Pattern(regexp =  "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]*$",message = "Password should contain alpha-numeric values")
    @Size(min = 8,max = 16,message = "Password length should at least 8 and maximum of 16 characters")
    private String newPassword;
    private String confirmPassword;
}
