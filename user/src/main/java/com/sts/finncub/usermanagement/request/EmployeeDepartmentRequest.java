package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDepartmentRequest {

    private Long empDepartmentId;
    private String empDepartmentCode;
    private String empDepartmentName;
    private String status;
    private Long hodEmployeeId;

}
