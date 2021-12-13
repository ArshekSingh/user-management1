package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDepartmentRoleRequest {

    private Long empDeptRoleId;
    private String empDeptRoleName;
    private Long empSubDeptId;
}
