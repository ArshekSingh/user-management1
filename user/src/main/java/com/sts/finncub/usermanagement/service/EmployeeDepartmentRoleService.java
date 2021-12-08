package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRoleRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface EmployeeDepartmentRoleService {
    Response getAllEmployeeDepartmentRoles();

    Response getEmployeeDepartmentRole(Long empDeptRoleId) throws BadRequestException;

    Response addEmployeeDepartmentRole(EmployeeDepartmentRoleRequest request) throws BadRequestException;

    Response updateEmployeeDepartmentRole(EmployeeDepartmentRoleRequest request) throws BadRequestException;
}
