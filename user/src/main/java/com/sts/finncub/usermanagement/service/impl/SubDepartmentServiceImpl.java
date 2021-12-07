package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeSubDepartmentDto;
import com.sts.finncub.core.entity.EmployeeSubDepartment;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeSubDepartmentRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.request.EmployeeSubDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.SubDepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubDepartmentServiceImpl implements SubDepartmentService {

    private final EmployeeSubDepartmentRepository employeeSubDepartmentRepository;

    private final UserCredentialService userCredentialService;

    @Autowired
    public SubDepartmentServiceImpl(EmployeeSubDepartmentRepository employeeSubDepartmentRepository,
                                    UserCredentialService userCredentialService) {
        this.employeeSubDepartmentRepository = employeeSubDepartmentRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public Response getAllSubDepartmentDetails() throws BadRequestException {
        Response response = new Response();
        List<EmployeeSubDepartmentDto> employeeSubDepartmentDtos = new ArrayList<>();
        List<EmployeeSubDepartment> employeeSubDepartmentList = employeeSubDepartmentRepository.findByOrgId
                (userCredentialService.getUserSession().getOrganizationId());
        if (CollectionUtils.isEmpty(employeeSubDepartmentList)) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        for (EmployeeSubDepartment employeeDepartmentMaster : employeeSubDepartmentList) {
            EmployeeSubDepartmentDto employeeSubDepartmentDto = new EmployeeSubDepartmentDto();
            BeanUtils.copyProperties(employeeDepartmentMaster, employeeSubDepartmentDto);
            employeeSubDepartmentDtos.add(employeeSubDepartmentDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeSubDepartmentDtos);
        return response;
    }

    @Override
    public Response getSubDepartmentDetails(Long empSubDepartmentId) throws BadRequestException {
        Response response = new Response();
        EmployeeSubDepartmentDto employeeSubDepartmentDto = new EmployeeSubDepartmentDto();
        EmployeeSubDepartment employeeDepartmentMaster = employeeSubDepartmentRepository.
                findByEmpSubDepartmentIdAndOrgId(empSubDepartmentId, userCredentialService.getUserSession().getOrganizationId());
        if (employeeDepartmentMaster == null) {
            throw new BadRequestException("Invalid Employee Department Id", HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(employeeDepartmentMaster, employeeSubDepartmentDto);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeSubDepartmentDto);
        return response;
    }

    @Override
    public Response addSubDepartmentDetails(EmployeeSubDepartmentRequest request) throws BadRequestException {
        Response response = new Response();
        if (request == null || request.getEmpDepartmentId() == null ||
                !StringUtils.hasText(request.getEmpSubDepartmentCode()) ||
                !StringUtils.hasText(request.getEmpSubDepartmentName()) ||
                !StringUtils.hasText(request.getStatus())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeSubDepartment employeeSubDepartment = new EmployeeSubDepartment();
        employeeSubDepartment.setOrgId(userCredentialService.getUserSession().getOrganizationId());
        employeeSubDepartment.setInsertedBy(userCredentialService.getUserSession().getUserId());
        employeeSubDepartment.setInsertedOn(LocalDateTime.now());
        BeanUtils.copyProperties(request, employeeSubDepartment);
        employeeSubDepartmentRepository.save(employeeSubDepartment);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response updateSubDepartmentDetails(EmployeeSubDepartmentRequest request) throws BadRequestException {
        Response response = new Response();
        if (request == null || request.getEmpSubDepartmentId() == null || request.getEmpDepartmentId() == null ||
                !StringUtils.hasText(request.getEmpSubDepartmentCode()) ||
                !StringUtils.hasText(request.getEmpSubDepartmentName()) ||
                !StringUtils.hasText(request.getStatus())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeSubDepartment employeeSubDepartment = employeeSubDepartmentRepository.
                findByEmpSubDepartmentIdAndOrgId(request.getEmpSubDepartmentId(), userCredentialService.getUserSession().getOrganizationId());
        if (employeeSubDepartment == null) {
            throw new BadRequestException("Invalid Employee Department Id", HttpStatus.BAD_REQUEST);
        }
        employeeSubDepartment.setUpdatedBy(userCredentialService.getUserSession().getUserId());
        employeeSubDepartment.setUpdatedOn(LocalDateTime.now());
        BeanUtils.copyProperties(employeeSubDepartment, request);
        employeeSubDepartmentRepository.save(employeeSubDepartment);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}
