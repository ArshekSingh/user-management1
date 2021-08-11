package com.sts.fincub.usermanagement.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String userId;
    private String password;

    @JsonIgnore
    public boolean isValid(){
        return (userId != null && !userId.isEmpty()) && (password != null && !password.isEmpty());
    }
}
