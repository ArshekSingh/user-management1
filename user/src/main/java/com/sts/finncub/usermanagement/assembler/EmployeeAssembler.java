package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.repository.BranchMasterRepository;
import com.sts.finncub.core.repository.EmployeeDepartmentRepository;
import com.sts.finncub.core.repository.EmployeeFunctionalTitleRepository;
import com.sts.finncub.core.repository.EmployeeMovementLogsRepository;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class EmployeeAssembler {

    private final EmployeeMovementLogsRepository employeeMovementLogsRepository;
    private final EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository;
    private final BranchMasterRepository branchMasterRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    public void dtoToEntity(EmployeeRequest request, UserSession userSession) {
        EmployeeMovementLogs employeeMovementLogs = new EmployeeMovementLogs();
        EmployeeMovementLogsPK employeeMovementLogsPK = new EmployeeMovementLogsPK();
        employeeMovementLogsPK.setOrgId(userSession.getOrganizationId());
        employeeMovementLogsPK.setRev(employeeMovementLogsRepository.getNextRev());
        employeeMovementLogs.setEmployeeMovementLogsPK(employeeMovementLogsPK);
        employeeMovementLogs.setEmployeeId(request.getEmployeeId());
        employeeMovementLogs.setEmployementType(request.getEmploymentType());
        employeeMovementLogs.setPromotionDate(StringUtils.hasText(request.getBranchJoinDate()) ? DateTimeUtil.stringToDate(request.getPromotionDate()) : null);
        employeeMovementLogs.setDepartmentId(request.getDepartmentId());
        employeeMovementLogs.setSubDepartmentId(request.getSubDepartmentId());
        employeeMovementLogs.setDesignationType(request.getDesignationType());
        employeeMovementLogs.setDesignationId(request.getDesignationId());
        employeeMovementLogs.setInsertedBy(userSession.getUserId());
        employeeMovementLogs.setInsertedOn(LocalDateTime.now());
        employeeMovementLogs.setUpdatedBy(userSession.getUserId());
        employeeMovementLogs.setUpdatedOn(LocalDateTime.now());
        employeeMovementLogs.setBranchId(request.getBranchId().longValue());
        employeeMovementLogs.setBranchJoiningDate(StringUtils.hasText(request.getBranchJoinDate()) ? DateTimeUtil.stringToDate(request.getBranchJoinDate()) : null);
        employeeMovementLogs.setFunctionalTitleId(request.getFunctionalTitleId());
        employeeMovementLogsRepository.save(employeeMovementLogs);
    }

    public List<EmployeeDto> entityToDtoList(List<EmployeeMovementLogs> employeeMovementLogsList, UserSession userSession) {
        return employeeMovementLogsList.stream().map(o -> entityToDto(o, userSession)).collect(Collectors.toList());
    }

    public EmployeeDto entityToDto(EmployeeMovementLogs employeeMovementLogs, UserSession userSession) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setRev(employeeMovementLogs.getEmployeeMovementLogsPK().getRev());
        employeeDto.setEmployeeId(employeeMovementLogs.getEmployeeId());
        employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employeeMovementLogs.getConfirmationDate()));
        employeeDto.setPromotionDate(DateTimeUtil.dateToString(employeeMovementLogs.getPromotionDate()));
        employeeDto.setRelievingDate(DateTimeUtil.dateToString(employeeMovementLogs.getRelievingDate()));
        if (employeeMovementLogs.getBranchId() != null) {
            Optional<BranchMaster> branchMasterOptional = branchMasterRepository.findByBranchIdAndOrgId(employeeMovementLogs.getBranchId().intValue(), userSession.getOrganizationId());
            if (branchMasterOptional.isPresent()) {
                BranchMaster branchMaster = branchMasterOptional.get();
                employeeDto.setBranchName(branchMaster.getBranchCode() + "-" + branchMaster.getBranchName());
            }
        }
        employeeDto.setBranchId(employeeMovementLogs.getBranchId());
        employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employeeMovementLogs.getBranchJoiningDate()));
        employeeDto.setDepartmentName(employeeMovementLogs.getEmployeeDepartmentMaster() != null ? employeeMovementLogs.getEmployeeDepartmentMaster().getEmpDepartmentName() : "");
        employeeDto.setDepartmentId(employeeMovementLogs.getDepartmentId());
        if (employeeMovementLogs.getSubDepartmentId() != null) {
            EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId(userSession.getOrganizationId(), employeeMovementLogs.getSubDepartmentId());
            employeeDto.setSubDepartmentName(employeeDepartmentMaster.getEmpDepartmentName());
        }
        employeeDto.setSubDepartmentId(employeeMovementLogs.getSubDepartmentId());
        employeeDto.setDesignationType(employeeMovementLogs.getDesignationType());
        employeeDto.setDesignationName(employeeMovementLogs.getEmployeeDesignationMaster() != null ? employeeMovementLogs.getEmployeeDesignationMaster().getEmpDesignationName() : "");
        employeeDto.setDesignationId(employeeMovementLogs.getDesignationId());
        if (employeeMovementLogs.getFunctionalTitleId() != null) {
            try {
                EmployeeFunctionalTitle functionalTitle = employeeFunctionalTitleRepository.findByOrgIdAndEmpFuncTitleId(userSession.getOrganizationId(), employeeMovementLogs.getFunctionalTitleId());
                employeeDto.setFunctionalTitleName(functionalTitle.getEmpFuncTitleName());
                employeeDto.setFunctionalTitleId(employeeMovementLogs.getFunctionalTitleId());
            } catch (Exception e) {
                log.error("Exception occurred due to {}", e.getMessage());
            }
        }
        employeeDto.setUpdatedBy(employeeMovementLogs.getUpdatedBy());
        employeeDto.setInsertedBy(employeeMovementLogs.getInsertedBy());
        employeeDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeMovementLogs.getInsertedOn()));
        employeeDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeMovementLogs.getUpdatedOn()));
        return employeeDto;
    }
}
