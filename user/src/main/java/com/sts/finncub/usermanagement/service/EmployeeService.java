package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.EmployeeTransferRequest;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.EmployeeRequest;

public interface EmployeeService {

    Response addEmployee(EmployeeRequest request) throws BadRequestException;

    Response getAllEmployeeDetails(FilterRequest request) throws BadRequestException;

    Response getEmployeeDetail(Long employeeId) throws BadRequestException;

    Response updateEmployeeDetails(EmployeeRequest request) throws BadRequestException;

    Response employeeTransferPackageCall(FilterRequest filterRequest) throws BadRequestException;

    Response getSubEmpDeptByEmpDepartmentId(Long empDepartmentId);

    Response transferEmployee(EmployeeTransferRequest employeeTransferRequest) throws BadRequestException;

    Response validateAadhaarPanMobForSaveEmployee(EmployeeRequest request);

    Response validateAadhaarPanMobForUpdateEmployee(EmployeeRequest request);

    Response validateActiveAadhaarOrPanOrMobForSaveEmployee(EmployeeRequest request);
}