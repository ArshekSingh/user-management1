package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.entity.EmployeeMovementLogs;
import com.sts.finncub.core.entity.EmployeeMovementLogsPK;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.repository.EmployeeMovementLogsRepo;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeAssembler {

    @Autowired
    private EmployeeMovementLogsRepo employeeMovementLogsRepo;

    public void dtoToEntity(EmployeeRequest request, UserSession userSession) {
        EmployeeMovementLogs employeeMovementLogs = new EmployeeMovementLogs();
        EmployeeMovementLogsPK employeeMovementLogsPK = new EmployeeMovementLogsPK();
        employeeMovementLogsPK.setOrgId(userSession.getOrganizationId());
        employeeMovementLogsPK.setRev(employeeMovementLogsRepo.getNextRev());
        employeeMovementLogs.setEmployeeMovementLogsPK(employeeMovementLogsPK);
        employeeMovementLogs.setEmployeeId(request.getEmployeeId());
        employeeMovementLogs.setEmployementType(request.getEmploymentType());
        employeeMovementLogs.setPromotionDate(DateTimeUtil.stringToDate(request.getPromotionDate()));
        employeeMovementLogs.setBranchId(request.getBranchId().longValue());
        employeeMovementLogs.setBranchJoiningDate(DateTimeUtil.stringToDate(request.getBranchJoinDate()));
        employeeMovementLogs.setConfirmationDate(DateTimeUtil.stringToDate(request.getConfirmationDate()));
        employeeMovementLogs.setRelievingDate(DateTimeUtil.stringToDate(request.getRelievingDate()));
        employeeMovementLogs.setDepartmentId(request.getDepartmentId());
        employeeMovementLogs.setSubDepartmentId(request.getSubDepartmentId());
        employeeMovementLogs.setDesignationType(request.getDesignationType());
        employeeMovementLogs.setDesignationId(request.getDesignationId());
        employeeMovementLogs.setFunctionalTitleId(request.getFunctionalTitleId());
        employeeMovementLogs.setInsertedBy(userSession.getUserId());
        employeeMovementLogs.setInsertedOn(LocalDateTime.now());
        employeeMovementLogs.setUpdatedBy(userSession.getUserId());
        employeeMovementLogs.setUpdatedOn(LocalDateTime.now());
        employeeMovementLogsRepo.save(employeeMovementLogs);
    }

    public List<EmployeeDto> entityToDtoList(List<EmployeeMovementLogs> employeeMovementLogsList) {
        return employeeMovementLogsList.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    public EmployeeDto entityToDto(EmployeeMovementLogs employeeMovementLogs) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setRev(employeeMovementLogs.getEmployeeMovementLogsPK().getRev());
        employeeDto.setEmployeeId(employeeMovementLogs.getEmployeeId());
        employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employeeMovementLogs.getConfirmationDate()));
        employeeDto.setPromotionDate(DateTimeUtil.dateToString(employeeMovementLogs.getPromotionDate()));
        employeeDto.setRelievingDate(DateTimeUtil.dateToString(employeeMovementLogs.getRelievingDate()));
        employeeDto.setBranchId(employeeMovementLogs.getBranchId());
        employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employeeMovementLogs.getBranchJoiningDate()));
        employeeDto.setDepartmentId(employeeMovementLogs.getDepartmentId());
        employeeDto.setSubDepartmentId(employeeMovementLogs.getSubDepartmentId());
        employeeDto.setDesignationType(employeeMovementLogs.getDesignationType());
        employeeDto.setDesignationId(employeeMovementLogs.getDesignationId());
        employeeDto.setFunctionalTitleId(employeeMovementLogs.getFunctionalTitleId());
        employeeDto.setUpdatedBy(employeeMovementLogs.getUpdatedBy());
        employeeDto.setInsertedBy(employeeMovementLogs.getInsertedBy());
        employeeDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeMovementLogs.getInsertedOn()));
        employeeDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeMovementLogs.getUpdatedOn()));
        return employeeDto;
    }
}
