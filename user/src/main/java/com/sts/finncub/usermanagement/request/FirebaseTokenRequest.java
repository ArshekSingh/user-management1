package com.sts.finncub.usermanagement.request;

import lombok.Data;

@Data
public class FirebaseTokenRequest {
    private String token;
    private String userId;
}
