package com.sts.finncub.usermanagement.request;

import lombok.Data;

@Data
public class CallbackMailRequest {
    private String name;
    private String email;
    private String mobileNumber;
    private String description;
}
