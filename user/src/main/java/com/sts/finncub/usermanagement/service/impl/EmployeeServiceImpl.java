package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeDepartmentDto;
import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.repository.dao.EmployeeDao;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.service.EmployeeService;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// @Author Sumit kumar

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final UserCredentialService userCredentialService;

    private final EmployeeRepository employeeRepository;

    private final EmployeeDao employeeDao;

    private final UserRepository userRepository;

    private final UserService userService;

    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    private final EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository;
    private final BranchMasterRepository branchMasterRepository;

    @Autowired
    public EmployeeServiceImpl(UserCredentialService userCredentialService, EmployeeRepository employeeRepository, EmployeeDao employeeDao, UserRepository userRepository, UserService userService, EmployeeDepartmentRepository employeeDepartmentRepository, EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository, BranchMasterRepository branchMasterRepository) {
        this.userCredentialService = userCredentialService;
        this.employeeRepository = employeeRepository;
        this.employeeDao = employeeDao;
        this.userRepository = userRepository;
        this.userService = userService;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.employeeFunctionalTitleRepository = employeeFunctionalTitleRepository;
        this.branchMasterRepository = branchMasterRepository;
    }

    @Override
    @Transactional
    public Response addEmployee(EmployeeRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        validateRequest(request);
        Employee employee = new Employee();
        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(userCredentialService.getUserSession().getOrganizationId(), "EMP");
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        employee.setOrganizationId(userSession.getOrganizationId());
        employee.setEmployeeCode(employeeId);
        // save value in employee master
        saveValueEmployeeMaster(request, employee, request.getEmployeeId());
        // create  employee user details in user master
        SaveValueInUserMaster(userId, request);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    private void SaveValueInUserMaster(String userId, EmployeeRequest employeeRequest) throws BadRequestException {
        UserRequest request = new UserRequest();
        request.setPassword(userId);
        request.setUserId(userId);
        request.setEmail(employeeRequest.getOfficialEmail());
        request.setName(employeeRequest.getFirstName());
        request.setMobileNumber(employeeRequest.getPersonalMob() == null ? null : "" + employeeRequest.getPersonalMob());
        request.setType("EMP");
        request.setIsActive("Y");
        request.setEmployeeCreate(true);
        request.setBranchId(employeeRequest.getBranchId());
        request.setDesignationType(employeeRequest.getDesignationType());
        userService.addUser(request);

    }

    private void saveValueEmployeeMaster(EmployeeRequest request, Employee employee, Long employeeId) {
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setGender(request.getGender());
        employee.setDob(DateTimeUtil.stringToDate(request.getDob()));
        employee.setFatherName(request.getFatherName());
        employee.setMotherName(request.getMotherName());
        employee.setSpouseName(request.getSpouseName());
        employee.setLanguageKnown(request.getLanguageKnown());
        employee.setQualification(request.getQualification());
        employee.setBloodGroup(request.getBloodGroup());
        employee.setPersonalMob(request.getPersonalMob());
        employee.setCugMob(request.getCugMob());
        employee.setPersonalEmail(request.getPersonalEmail());
        employee.setOfficialEmail(request.getOfficialEmail());
        employee.setLandline(request.getLandline());
        employee.setCurrentAdd(request.getCurrentAdd());
        employee.setCurrentCity(request.getCurrentCity());
        employee.setCurrentState(request.getCurrentState());
        employee.setCurrentPincode(request.getCurrentPincode());
        employee.setPermanentAdd(request.getPermanentAdd());
        employee.setPermanentCity(request.getPermanentCity());
        employee.setPermanentState(request.getPermanentState());
        employee.setPermanentPincode(request.getPermanentPincode());
        employee.setNationality(request.getNationality());
        employee.setAlternateCon(request.getAlternateCon());
        employee.setEmergencyCon(request.getEmergencyCon());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setJoiningDate(StringUtils.hasText(request.getJoiningDate()) ? DateTimeUtil.stringToDate(request.getJoiningDate()) : null);
        employee.setConfirmationDate(StringUtils.hasText(request.getConfirmationDate()) ? DateTimeUtil.stringToDate(request.getConfirmationDate()) : null);
        employee.setPromotionDate(StringUtils.hasText(request.getPromotionDate()) ? DateTimeUtil.stringToDate(request.getPromotionDate()) : null);
        employee.setRelievingDate(StringUtils.hasText(request.getRelievingDate()) ? DateTimeUtil.stringToDate(request.getRelievingDate()) : null);
        employee.setAadharCard(request.getAadharCard());
        employee.setPancardNo(request.getPancardNo());
        employee.setPfNumber(request.getPfNumber());
        employee.setUanNo(request.getUanNo());
        employee.setDrivingLicenceNo(request.getDrivingLicenceNo());
        employee.setPassportNo(request.getPassportNo());
        employee.setEsicNo(request.getEsicNo());
        employee.setBankName(request.getBankName());
        employee.setIfscCode(request.getIfscCode());
        employee.setBankMMID(request.getBankMMID());
        employee.setBankVPA(request.getBankVPA());
        employee.setBankAccType(request.getBankAccType());
        employee.setBankAccNo(request.getBankAccNo());
        employee.setBankBranch(request.getBankBranch());
        employee.setProfileImgPath(request.getProfileImgPath());
        employee.setSignImgPath(request.getSignImgPath());
        employee.setBranchId(request.getBranchId());
        employee.setBranchJoinDate(StringUtils.hasText(request.getBranchJoinDate()) ? DateTimeUtil.stringToDate(request.getBranchJoinDate()) : null);
        employee.setDepartmentId(request.getDepartmentId());
        employee.setDepartmentRoleId(request.getDepartmentRoleId());
        employee.setDesignationType(request.getDesignationType());
        employee.setDesignationId(request.getDesignationId());
        employee.setFunctionalTitleId(request.getFunctionalTitleId());
        employee.setOrganizationBand(request.getOrganizationBand());
        employee.setReportManagerId(request.getReportManagerId());
        employee.setHrManagerId(request.getHrManagerId());
        employee.setAccManagerId(request.getAccManagerId());
        employee.setStatus(request.getStatus());
        employee.setTitle(request.getTitle());
        employee.setCaste(request.getCaste());
        employee.setReligion(request.getReligion());
        employee.setReferenceType(request.getReferenceType());
        employee.setReferenceSource(request.getReferenceSource());
        employee.setReferenceName(request.getReferenceName());
        employee.setReferenceId(request.getReferenceId());
        employee.setRelWithEmergPerson(request.getRelWithEmergPerson());
        if (StringUtils.hasText(request.getResignDate())) {
            employee.setResignDate(DateTimeUtil.stringToDate(request.getResignDate()));
        }
        employee.setTypeOfExit(request.getTypeOfExit());
        employee.setGrade(request.getGrade());
        if (StringUtils.hasText(request.getExitDate())) {
            employee.setExitDate(DateTimeUtil.stringToDate(request.getExitDate()));
        }
        employee.setIsSignatory(request.getIsSignatory());
        employee.setIsFnfClear(request.getIsFnfClear());
        if (employeeId == null) {
            employee.setInsertedOn(LocalDateTime.now());
            employee.setInsertedBy(userCredentialService.getUserSession().getUserId());
        } else {
            employee.setUpdatedOn(LocalDateTime.now());
            employee.setUpdatedBy(userCredentialService.getUserSession().getUserId());
        }
        employeeRepository.save(employee);
    }

    private void validateRequest(EmployeeRequest request) throws BadRequestException {
        // validate employee add / update request
    	if (request == null || !StringUtils.hasText(request.getStatus()) ||
                !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getGender())) {
        	log.warn("Request failed validation : {} ",request.getFirstName());

            throw new BadRequestException("Invalid Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response getAllEmployeeDetails(FilterRequest request) throws BadRequestException {
        Response response = new Response();
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        // fetch employee detail list using organizationId
        UserSession userSession = userCredentialService.getUserSession();
        request.setOrganizationId(userSession.getOrganizationId());
        List<Employee> employeeList = employeeDao.fetchAllEmployeeDetails(request);
        // fetch Employee details list count
        Long employeeDetailsCount = employeeDao.findAllFilterEmployeeDetailsCount(request);
        if (CollectionUtils.isEmpty(employeeList)) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        for (Employee employee : employeeList) {
            EmployeeDto employeeDto = new EmployeeDto();
            BeanUtils.copyProperties(employee, employeeDto);
            employeeDto.setJoiningDate(DateTimeUtil.dateToString(employee.getJoiningDate()));
            employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employee.getConfirmationDate()));
            employeeDto.setRelievingDate(DateTimeUtil.dateToString(employee.getRelievingDate()));
            employeeDto.setPromotionDate(DateTimeUtil.dateToString(employee.getPromotionDate()));
            employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employee.getBranchJoinDate()));
            employeeDto.setDob(DateTimeUtil.dateToString(employee.getDob()));
            employeeDto.setDepartmentName(employee.getEmployeeDepartmentMaster() == null ? "" : employee.getEmployeeDepartmentMaster().getEmpDepartmentName());
            employeeDto.setDesignationName(employee.getEmployeeDesignationMaster() == null ? "" : employee.getEmployeeDesignationMaster().getEmpDesignationName());
            if (employee.getSubDepartmentId() != null) {
                EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId(userSession.getOrganizationId(), employee.getSubDepartmentId());
                employeeDto.setSubDepartmentName(employeeDepartmentMaster.getEmpDepartmentName());
            }
            if (employee.getFunctionalTitleId() != null) {
                EmployeeFunctionalTitle employeeFunctionalTitle = employeeFunctionalTitleRepository.findByOrgIdAndEmpFuncTitleId(userSession.getOrganizationId(), employee.getFunctionalTitleId());
                employeeDto.setFunctionalTitleName(employeeFunctionalTitle.getEmpFuncTitleName());
            }
            if (employee.getAccManagerId() != null) {
                Employee accManager = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employee.getAccManagerId());
                employeeDto.setAccountManagerName(accManager.getEmployeeId() + " " + accManager.getFirstName());
            }
            if (employee.getReportManagerId() != null) {
                Employee reportManager = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employee.getReportManagerId());
                employeeDto.setReportManagerName(reportManager.getEmployeeId() + " " + reportManager.getFirstName());
            }
            if (employee.getResignDate() != null) {
                employeeDto.setResignDate(DateTimeUtil.dateToString(employee.getResignDate()));
            }
            if (employee.getExitDate() != null) {
                employeeDto.setExitDate(DateTimeUtil.dateToString(employee.getExitDate()));
            }
            if (employee.getBranchId() != null) {
                BranchMaster branchMaster = branchMasterRepository.findByBranchId(employee.getBranchId().intValue()).orElse(null);
                employeeDto.setBaseLocation(branchMaster==null?"":branchMaster.getBranchCode() + " " + branchMaster.getBranchName());
            }
            if (employee.getSubDepartmentId() != null) {
                EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId(userSession.getOrganizationId(), employee.getSubDepartmentId());
                employeeDto.setSubDepartmentName(employeeDepartmentMaster==null?"":employeeDepartmentMaster.getEmpDepartmentName());
            }
            employeeDtoList.add(employeeDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDtoList);
        response.setTotalCount(employeeDetailsCount);
        return response;
    }

    @Override
    public Response getEmployeeDetail(Long employeeId) throws BadRequestException {
        Response response = new Response();
        EmployeeDto employeeDto = new EmployeeDto();
        UserSession userSession = userCredentialService.getUserSession();
        // fetch employee detail using organizationId and employeeId
        Employee employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employeeId);
        if (employee == null) {
        	log.warn("Employee not found for employeeId : {} ",employeeId);
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(employee, employeeDto);
        employeeDto.setDob(DateTimeUtil.dateToString(employee.getDob()));
        employeeDto.setJoiningDate(DateTimeUtil.dateToString(employee.getJoiningDate()));
        employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employee.getConfirmationDate()));
        employeeDto.setRelievingDate(DateTimeUtil.dateToString(employee.getRelievingDate()));
        employeeDto.setPromotionDate(DateTimeUtil.dateToString(employee.getPromotionDate()));
        employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employee.getBranchJoinDate()));
        employeeDto.setDepartmentName(employee.getEmployeeDepartmentMaster() == null ? "" : employee.getEmployeeDepartmentMaster().getEmpDepartmentName());
        employeeDto.setDesignationName(employee.getEmployeeDesignationMaster() == null ? "" : employee.getEmployeeDesignationMaster().getEmpDesignationName());
        if (employee.getSubDepartmentId() != null) {
            EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId(userSession.getOrganizationId(), employee.getSubDepartmentId());
            employeeDto.setSubDepartmentName(employeeDepartmentMaster.getEmpDepartmentName());
        }
        if (employee.getFunctionalTitleId() != null) {
            EmployeeFunctionalTitle employeeFunctionalTitle = employeeFunctionalTitleRepository.findByOrgIdAndEmpFuncTitleId(userSession.getOrganizationId(), employee.getFunctionalTitleId());
            employeeDto.setFunctionalTitleName(employeeFunctionalTitle.getEmpFuncTitleName());
        }
        if (employee.getAccManagerId() != null) {
            Employee accManager = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employee.getAccManagerId());
            employeeDto.setAccountManagerName(accManager.getEmployeeId() + " " + accManager.getFirstName());
        }
        if (employee.getReportManagerId() != null) {
            Employee reportManager = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employee.getReportManagerId());
            employeeDto.setReportManagerName(reportManager.getEmployeeId() + " " + reportManager.getFirstName());
        }
        if (employee.getBranchId() != null) {
            BranchMaster branchMaster = branchMasterRepository.findByBranchId(employee.getBranchId().intValue()).orElse(null);
            employeeDto.setBaseLocation(branchMaster==null?"":branchMaster.getBranchCode() + " " + branchMaster.getBranchName());
        }
        if(employee.getReferenceId() != null){
            Employee emp=employeeRepository.findByEmployeeCodeAndOrganizationId(employee.getReferenceId(),userSession.getOrganizationId());
            employeeDto.setReferenceIdName(emp==null?"":emp.getEmployeeCode() + "-" + emp.getFirstName() + " " + emp.getLastName());
        }
        try {
            employeeDto.setCurrentVillageName(employee.getCurrentVillageMaster().getVillageName());
            employeeDto.setPermanentVillageName(employee.getPermanentVillageMaster().getVillageName());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        employeeDto.setResignDate(DateTimeUtil.dateToString(employee.getResignDate()));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDto);
        return response;
    }

    @Override
    public Response updateEmployeeDetails(EmployeeRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        // fetch employee detail using organizationId and employeeId
        Employee employee = new Employee();
        if (request.getEmployeeId() != null) {
            try {
                employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), request.getEmployeeId());
            } catch (Exception exception) {
                throw new BadRequestException(exception.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (employee != null) {
                // save value in employee master table
                saveValueEmployeeMaster(request, employee, request.getEmployeeId());
                response.setCode(HttpStatus.OK.value());
                response.setStatus(HttpStatus.OK);
                response.setMessage("Transaction completed successfully.");
            } else {
                throw new BadRequestException("Invalid Employee Id", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @Override
    public Response employeeTransferPackageCall(FilterRequest filterRequest) throws BadRequestException {
        Response response;
        UserSession userSession = userCredentialService.getUserSession();
        if (filterRequest.getEmployeeId() == null) {
            filterRequest.setEmployeeId(0L);
        }
        if (filterRequest.getEmplDesigType() == null) {
            filterRequest.setEmplDesigType("");
        }
        if (filterRequest.getEmplDesigAreaId() == null) {
            filterRequest.setEmplDesigAreaId(0L);
        }
        if (filterRequest.getIsManager() == null) {
            filterRequest.setIsManager("");
        }
        if (filterRequest.getBasedLocationId() == null) {
            filterRequest.setBasedLocationId(0L);
        }
        response = employeeDao.employeeTransferPackageCall(filterRequest, userSession.getOrganizationId(), userSession.getUserId());
        return response;
    }

    @Override
    public Response getSubEmpDeptByEmpDepartmentId(Long empDepartmentId) {
        UserSession userSession = userCredentialService.getUserSession();
        Response response = new Response();
        List<EmployeeDepartmentMaster> employeeDepartmentMasterList = employeeDepartmentRepository.findByOrgIdAndMainEmpDeptIdAndIsMainEmpDept(userSession.getOrganizationId(), empDepartmentId, "N");
        Set<EmployeeDepartmentDto> employeeSubDeptMap = new HashSet<>();
        for (EmployeeDepartmentMaster employeeDepartmentMaster : employeeDepartmentMasterList) {
            EmployeeDepartmentDto employeeDepartmentDto = new EmployeeDepartmentDto();
            BeanUtils.copyProperties(employeeDepartmentMaster, employeeDepartmentDto);
            employeeDepartmentDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getInsertedOn()));
            employeeDepartmentDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getUpdatedOn()));
            employeeSubDeptMap.add(employeeDepartmentDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(employeeSubDeptMap);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}