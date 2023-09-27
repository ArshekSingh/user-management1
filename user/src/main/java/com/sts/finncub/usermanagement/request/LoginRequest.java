package com.sts.finncub.usermanagement.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class LoginRequest {
    private String userId;
    private String password;
    //    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]*$", message = "Password should contain at-least one upper case/lower case number and special character")
//    @Size(min = 8, max = 16, message = "Password length should at least 8 and maximum of 16 characters")
    private String newPassword;
    private String applicationVersion;
    private String ipAddress;
    private String deviceId;
    private String imeiNumber1;
    private String imeiNumber2;
    private String loginMode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    //    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]*$", message = "Password should contain at-least one upper case/lower case number and special character")
//    @Size(min = 8, max = 16, message = "Password length should at least 8 and maximum of 16 characters")
    private String confirmPassword;

    @JsonIgnore
    public boolean isValid() {
        log.info("Validating login request data");
        return (userId != null && !userId.isEmpty()) && (password != null && !password.isEmpty());
    }
}