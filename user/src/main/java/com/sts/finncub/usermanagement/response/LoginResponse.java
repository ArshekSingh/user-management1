package com.sts.finncub.usermanagement.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sts.finncub.core.entity.UserSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String authToken;
    private UserSession userSession;
    private String baseLocation;
    private String designationName;
    private String departmentName;

}
