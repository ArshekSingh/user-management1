package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.EmployeeDesignationRequest;

public interface DesignationService {

    Response getAllEmployeeDesignationDetails();

    Response getEmployeeDesignationDetail(Long designationId) throws BadRequestException;

    Response addEmployeeDesignation(EmployeeDesignationRequest request) throws BadRequestException;

    Response updateEmployeeDesignation(EmployeeDesignationRequest request) throws BadRequestException;
}
