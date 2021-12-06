package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRequest;
import com.sts.finncub.usermanagement.request.EmployeeSubDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface SubDepartmentService {

    Response getAllSubDepartmentDetails() throws BadRequestException;

    Response getSubDepartmentDetails(Long departmentId) throws BadRequestException;

    Response addSubDepartmentDetails(EmployeeSubDepartmentRequest request) throws BadRequestException;

    Response updateSubDepartmentDetails(EmployeeSubDepartmentRequest request) throws BadRequestException;
}
