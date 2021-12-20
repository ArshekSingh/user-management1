package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeDesignationDto;
import com.sts.finncub.core.entity.EmployeeDesignationMaster;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeDesignationMasterRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeDesignationRequest;
import com.sts.finncub.usermanagement.service.DesignationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DesignationServiceImpl implements DesignationService {

    private final EmployeeDesignationMasterRepository employeeDesignationMasterRepository;
    private final UserCredentialService userCredentialService;

    @Autowired
    public DesignationServiceImpl(EmployeeDesignationMasterRepository employeeDesignationMasterRepository,
                                  UserCredentialService userCredentialService) {
        this.employeeDesignationMasterRepository = employeeDesignationMasterRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public Response getAllEmployeeDesignationDetails() {
        Response response = new Response();
        List<EmployeeDesignationDto> employeeDesignationDtos = new ArrayList<>();
        List<EmployeeDesignationMaster> employeeDesignationMasters = employeeDesignationMasterRepository.
                findByOrgId(userCredentialService.getUserSession().getOrganizationId());
        for (EmployeeDesignationMaster employeeDesignationMaster : employeeDesignationMasters) {
            EmployeeDesignationDto employeeDesignationDto = new EmployeeDesignationDto();
            employeeDesignationDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDesignationMaster.getInsertedOn()));
            employeeDesignationDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDesignationMaster.getUpdatedOn()));
            BeanUtils.copyProperties(employeeDesignationMaster, employeeDesignationDto);
            employeeDesignationDtos.add(employeeDesignationDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(employeeDesignationDtos);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response getEmployeeDesignationDetail(Long designationId) throws BadRequestException {
        Response response = new Response();
        EmployeeDesignationDto employeeDesignationDto = new EmployeeDesignationDto();
        EmployeeDesignationMaster employeeDesignationMaster = employeeDesignationMasterRepository.
                findByOrgIdAndEmpDesignationId(
                        userCredentialService.getUserSession().getOrganizationId(), designationId);
        if (employeeDesignationMaster == null) {
            throw new BadRequestException("Invalid Employee Designation Id", HttpStatus.BAD_REQUEST);
        }
        employeeDesignationDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDesignationMaster.getInsertedOn()));
        employeeDesignationDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDesignationMaster.getUpdatedOn()));
        BeanUtils.copyProperties(employeeDesignationMaster, employeeDesignationDto);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(employeeDesignationDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response addEmployeeDesignation(EmployeeDesignationRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || !StringUtils.hasText(request.getEmpDesignationName()) ||
                !StringUtils.hasText(request.getEmpDesignationType()) || !StringUtils.hasText(request.getStatus()) ||
                request.getConfNoticePeriod() == null || request.getOnProbNoticePeriod() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeDesignationMaster employeeDesignationMaster = new EmployeeDesignationMaster();
        BeanUtils.copyProperties(request, employeeDesignationMaster);
        employeeDesignationMaster.setOrgId(userSession.getOrganizationId());
        employeeDesignationMaster.setInsertedBy(userSession.getUserId());
        employeeDesignationMaster.setInsertedOn(LocalDateTime.now());
        employeeDesignationMasterRepository.save(employeeDesignationMaster);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response updateEmployeeDesignation(EmployeeDesignationRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || request.getEmpDesignationId() == null ||
                !StringUtils.hasText(request.getEmpDesignationName()) ||
                !StringUtils.hasText(request.getEmpDesignationType()) || !StringUtils.hasText(request.getStatus()) ||
                request.getConfNoticePeriod() == null || request.getOnProbNoticePeriod() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeDesignationMaster employeeDesignationMaster = employeeDesignationMasterRepository.
                findByOrgIdAndEmpDesignationId(
                        userCredentialService.getUserSession().getOrganizationId(), request.getEmpDesignationId());
        if (employeeDesignationMaster == null) {
            throw new BadRequestException("Invalid Employee Designation Id", HttpStatus.BAD_REQUEST);
        }
        employeeDesignationMaster.setUpdatedBy(userSession.getUserId());
        employeeDesignationMaster.setUpdatedOn(LocalDateTime.now());
        BeanUtils.copyProperties(request, employeeDesignationMaster);
        employeeDesignationMasterRepository.save(employeeDesignationMaster);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}
