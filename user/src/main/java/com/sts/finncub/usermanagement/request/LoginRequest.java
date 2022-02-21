package com.sts.finncub.usermanagement.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class LoginRequest {
    private String userId;
    private String password;
    private String newPassword;
    private String applicationVersion;
    private String ipAddress;
    private String deviceId;
    private String imeiNumber1;
    private String imeiNumber2;
    private String loginMode;

    @JsonIgnore
    public boolean isValid(){
        log.info("Validating signup request data");
        return (userId != null && !userId.isEmpty()) && (password != null && !password.isEmpty());
    }
}
