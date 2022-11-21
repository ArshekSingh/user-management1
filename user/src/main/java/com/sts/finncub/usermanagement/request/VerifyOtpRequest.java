package com.sts.finncub.usermanagement.request;

import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String otp;
    private String mobileNumber;

}
