package com.sts.fincub.usermanagement.response;

import com.sts.fincub.usermanagement.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    String userId;
    String name;
    String userType;
}
