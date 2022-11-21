package com.sts.finncub.usermanagement.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ForgetPasswordRequest {

    private String mobileNumber;
    private String userId;

}
