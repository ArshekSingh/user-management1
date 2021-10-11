package com.sts.finncub.usermanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String authToken;
    private String userType;
    private List<String> userRoles;
    private Map<Integer, String> branchDetails;

}
