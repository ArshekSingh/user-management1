package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeDepartmentRoleDto;
import com.sts.finncub.core.entity.EmployeeDepartmentRole;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeDepartmentRoleRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRoleRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.EmployeeDepartmentRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Author Sumit Kumar

@Service
public class EmployeeDepartmentRoleServiceImpl implements EmployeeDepartmentRoleService {

    private final EmployeeDepartmentRoleRepository employeeDepartmentRoleRepository;

    private final UserCredentialService userCredentialService;

    @Autowired
    public EmployeeDepartmentRoleServiceImpl(EmployeeDepartmentRoleRepository employeeDepartmentRoleRepository,
                                             UserCredentialService userCredentialService) {
        this.employeeDepartmentRoleRepository = employeeDepartmentRoleRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public Response getAllEmployeeDepartmentRoles() {
        Response response = new Response();
        List<EmployeeDepartmentRoleDto> employeeDepartmentRoleDtos = new ArrayList<>();
        List<EmployeeDepartmentRole> employeeDepartmentRoles = employeeDepartmentRoleRepository.
                findByOrgId(userCredentialService.getUserSession().getOrganizationId());
        for (EmployeeDepartmentRole employeeDepartmentRole : employeeDepartmentRoles) {
            EmployeeDepartmentRoleDto employeeDepartmentRoleDto = new EmployeeDepartmentRoleDto();
            BeanUtils.copyProperties(employeeDepartmentRole, employeeDepartmentRoleDto);
            employeeDepartmentRoleDtos.add(employeeDepartmentRoleDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDepartmentRoleDtos);
        return response;
    }

    @Override
    public Response getEmployeeDepartmentRole(Long empDeptRoleId) throws BadRequestException {
        Response response = new Response();
        EmployeeDepartmentRoleDto employeeDepartmentRoleDto = new EmployeeDepartmentRoleDto();
        if (empDeptRoleId == null) {
            throw new BadRequestException("Invalid empDeptRoleId", HttpStatus.BAD_REQUEST);
        }
        EmployeeDepartmentRole employeeDepartmentRole = employeeDepartmentRoleRepository.
                findByOrgIdAndEmpDeptRoleId(userCredentialService.getUserSession().getOrganizationId(), empDeptRoleId);
        if (employeeDepartmentRole == null) {
            throw new BadRequestException("No Data Found", HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(employeeDepartmentRole, employeeDepartmentRoleDto);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDepartmentRoleDto);
        return response;
    }

    @Override
    public Response addEmployeeDepartmentRole(EmployeeDepartmentRoleRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || !StringUtils.hasText(request.getEmpDeptRoleName()) || request.getEmpSubDeptId() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeDepartmentRole employeeDepartmentRole = new EmployeeDepartmentRole();
        employeeDepartmentRole.setOrgId(userSession.getOrganizationId());
        employeeDepartmentRole.setEmpDeptRoleName(request.getEmpDeptRoleName());
        employeeDepartmentRole.setEmpSubDeptId(request.getEmpSubDeptId());
        employeeDepartmentRole.setInsertedBy(userSession.getUserId());
        employeeDepartmentRole.setInsertedOn(LocalDateTime.now());
        employeeDepartmentRoleRepository.save(employeeDepartmentRole);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response updateEmployeeDepartmentRole(EmployeeDepartmentRoleRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || request.getEmpDeptRoleId() == null
                || !StringUtils.hasText(request.getEmpDeptRoleName()) || request.getEmpSubDeptId() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeDepartmentRole employeeDepartmentRole = employeeDepartmentRoleRepository.
                findByOrgIdAndEmpDeptRoleId(userCredentialService.getUserSession().getOrganizationId(),
                        request.getEmpDeptRoleId());
        if (employeeDepartmentRole == null) {
            throw new BadRequestException("No Data Found", HttpStatus.BAD_REQUEST);
        }
        employeeDepartmentRole.setEmpDeptRoleName(request.getEmpDeptRoleName());
        employeeDepartmentRole.setEmpSubDeptId(request.getEmpSubDeptId());
        employeeDepartmentRole.setUpdatedBy(userSession.getUserId());
        employeeDepartmentRole.setUpdatedOn(LocalDateTime.now());
        employeeDepartmentRoleRepository.save(employeeDepartmentRole);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}
