package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface EmployeeService {

    Response addEmployee(EmployeeRequest request) throws BadRequestException;

    Response getAllEmployeeDetails() throws BadRequestException;

    Response getEmployeeDetail(Long employeeId) throws BadRequestException;

    Response updateEmployeeDetails(EmployeeRequest request) throws BadRequestException;
}
