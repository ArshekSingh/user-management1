package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeDepartmentDto;
import com.sts.finncub.core.entity.EmployeeDepartmentMaster;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeDepartmentRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.EmployeeDepartmentService;
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
public class EmployeeDepartmentServiceImpl implements EmployeeDepartmentService {

    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    private final UserCredentialService userCredentialService;

    @Autowired
    public EmployeeDepartmentServiceImpl(EmployeeDepartmentRepository employeeDepartmentRepository,
                                         UserCredentialService userCredentialService) {
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public Response getAllEmployeeDepartmentDetails() throws BadRequestException {
        Response response = new Response();
        List<EmployeeDepartmentDto> employeeDepartmentDtos = new ArrayList<>();
        List<EmployeeDepartmentMaster> employeeDepartmentMaster = employeeDepartmentRepository.findByOrgId
                (userCredentialService.getUserSession().getOrganizationId());
        if (CollectionUtils.isEmpty(employeeDepartmentMaster)) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        for (EmployeeDepartmentMaster employeeDepartment : employeeDepartmentMaster) {
            EmployeeDepartmentDto employeeDepartmentDto = new EmployeeDepartmentDto();
            employeeDepartmentDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDepartment.getInsertedOn()));
            employeeDepartmentDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDepartment.getUpdatedOn()));
            BeanUtils.copyProperties(employeeDepartment, employeeDepartmentDto);
            employeeDepartmentDtos.add(employeeDepartmentDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDepartmentDtos);
        return response;
    }

    @Override
    public Response getEmployeeDepartmentDetail(Long departmentId) throws BadRequestException {
        Response response = new Response();
        EmployeeDepartmentDto employeeDepartmentDto = new EmployeeDepartmentDto();
        EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId
                (userCredentialService.getUserSession().getOrganizationId(), departmentId);
        if (employeeDepartmentMaster == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        employeeDepartmentDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getInsertedOn()));
        employeeDepartmentDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getUpdatedOn()));
        BeanUtils.copyProperties(employeeDepartmentMaster, employeeDepartmentDto);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDepartmentDto);
        return response;
    }

    @Override
    public Response addEmployeeDepartment(EmployeeDepartmentRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        EmployeeDepartmentMaster employeeDepartmentMaster = new EmployeeDepartmentMaster();
        BeanUtils.copyProperties(request, employeeDepartmentMaster);
        employeeDepartmentMaster.setOrgId(userCredentialService.getUserSession().getOrganizationId());
        employeeDepartmentMaster.setInsertedBy(userCredentialService.getUserSession().getUserId());
        employeeDepartmentMaster.setInsertedOn(LocalDateTime.now());
        employeeDepartmentRepository.save(employeeDepartmentMaster);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    private void validateRequest(EmployeeDepartmentRequest request) throws BadRequestException {
        if (request == null || !StringUtils.hasText(request.getEmpDepartmentCode()) ||
                !StringUtils.hasText(request.getEmpDepartmentName()) || !StringUtils.hasText(request.getStatus())
                || request.getHodEmployeeId() == null) {
            throw new BadRequestException("Invalid Request ", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateEmployeeDepartment(EmployeeDepartmentRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        if (request.getEmpDepartmentId() != null) {
            EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.
                    findByOrgIdAndEmpDepartmentId(userCredentialService.getUserSession().getOrganizationId(),
                            request.getEmpDepartmentId());
            if (employeeDepartmentMaster == null) {
                throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
            }
            employeeDepartmentMaster.setUpdatedBy(userCredentialService.getUserSession().getUserId());
            employeeDepartmentMaster.setUpdatedOn(LocalDateTime.now());
            BeanUtils.copyProperties(request, employeeDepartmentMaster);
            employeeDepartmentRepository.save(employeeDepartmentMaster);
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Transaction completed successfully.");
        } else {
            throw new BadRequestException(" Employee Department Id Is Mandatory", HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
