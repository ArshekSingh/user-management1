package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeSubDepartmentRequest {

    private Long empSubDepartmentId;
    private Long empDepartmentId;
    private String empSubDepartmentCode;
    private String empSubDepartmentName;
    private String status;
}
