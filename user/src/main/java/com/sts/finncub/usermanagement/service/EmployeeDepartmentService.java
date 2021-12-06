package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface EmployeeDepartmentService {
    Response getAllEmployeeDepartmentDetails() throws BadRequestException;

    Response getEmployeeDepartmentDetail(Long departmentId) throws BadRequestException;

    Response addEmployeeDepartment(EmployeeDepartmentRequest request) throws BadRequestException;

    Response updateEmployeeDepartment(EmployeeDepartmentRequest request) throws BadRequestException;
}
