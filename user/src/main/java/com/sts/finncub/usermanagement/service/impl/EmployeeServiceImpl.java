package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.dto.EmployeeDepartmentDto;
import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.enums.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.repository.dao.EmployeeDao;
import com.sts.finncub.core.request.EmployeeTransferRequest;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.core.util.ValidationUtils;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.service.EmployeeService;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// @Author Sumit kumar

@Slf4j
@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService, Constant {

    private final UserService userService;
    private final EmployeeDao employeeDao;
    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final EmployeeRepository employeeRepository;
    private final UserCredentialService userCredentialService;
    private final BranchMasterRepository branchMasterRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository;
    private final CenterMasterRepository centerMasterRepository;

    @Override
    @Transactional
    public Response addEmployee(EmployeeRequest request) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        validateRequest(request);
        Employee employee = new Employee();
        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(userSession.getOrganizationId(), "EMP");
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        employee.setOrganizationId(userSession.getOrganizationId());
        employee.setEmployeeCode(employeeId);
        request.setUserId(userId);
        employee.setEmployeeId(Long.valueOf(employeeId));
        // save value in employee master
        saveValueEmployeeMaster(request, employee, request.getEmployeeId(), userSession);
        log.info("Employee save success fully");
        // create  employee user details in user master
        saveValueInUserMaster(userId, request, true);
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void saveValueInUserMaster(String userId, EmployeeRequest employeeRequest, Boolean isActive) throws BadRequestException {
        UserRequest request = new UserRequest();
        request.setPassword(userId);
        request.setUserId(userId);
        request.setEmail(employeeRequest.getOfficialEmail());
        request.setName(employeeRequest.getFirstName());
        request.setMobileNumber(employeeRequest.getPersonalMob() == null ? null : "" + employeeRequest.getPersonalMob());
        request.setType("EMP");
        if (isActive) {
            request.setIsActive("Y");
        } else {
            request.setIsActive("N");
        }
        request.setEmployeeCreate(true);
        request.setDesignationType(employeeRequest.getDesignationType());
        List<String> bcId = employeeRequest.getBcId();
        StringBuilder stringBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty(bcId)) {
            for (String string : bcId) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(string);
            }
        }
        request.setBcId(stringBuilder.toString());
        userService.addUser(request);
    }

    private void saveValueEmployeeMaster(EmployeeRequest request, Employee employee, Long employeeId, UserSession userSession) {
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
        employee.setIsVehicle(request.getIsVehicle());
        employee.setVehicleType(request.getVehicleType());
        employee.setVehicleNumber(request.getVehicleNumber());
        if (StringUtils.hasText(request.getResignDate())) {
            employee.setResignDate(DateTimeUtil.stringToDate(request.getResignDate()));
        }
        employee.setTypeOfExit(request.getTypeOfExit());
        employee.setGrade(request.getGrade());
        employee.setExitDate(StringUtils.hasText(request.getExitDate()) ? DateTimeUtil.stringToDate(request.getExitDate()) : null);
        employee.setIsSignatory(request.getIsSignatory());
        employee.setIsFnfClear(request.getIsFnfClear());
        if (employeeId == null) {
            employee.setInsertedOn(LocalDateTime.now());
            employee.setInsertedBy(userSession.getUserId());
        } else {
            employee.setUpdatedOn(LocalDateTime.now());
            employee.setUpdatedBy(userSession.getUserId());
        }
        if (StringUtils.hasText(request.getUserId())) {
            employee.setUserId(request.getUserId());
        }
        employee.setSubDepartmentId(request.getSubDepartmentId());
        employeeRepository.save(employee);
    }

    private void validateRequest(EmployeeRequest request) throws BadRequestException {
        // validate employee add / update request
        if (request == null || !StringUtils.hasText(request.getStatus()) || !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getGender())) {
            assert request != null;
            log.warn("Request failed validation, these field are mandatory : Status {} , FirstName {} , Gender {} ", StringUtils.hasText(request.getStatus()), request.getFirstName(), request.getGender());
            throw new BadRequestException("Invalid Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response getAllEmployeeDetails(FilterRequest request) throws BadRequestException {
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        // fetch employee detail list using organizationId
        UserSession userSession = userCredentialService.getUserSession();
        Long count = null;
        request.setOrganizationId(userSession.getOrganizationId());
        if (request.getStart() == 0) {
            count = employeeDao.findAllFilterEmployeeDetailsCount(request);
        }
        if ("Y".equalsIgnoreCase(request.getIsCsv())) {
            assert count != null;
            request.setLimit(count.intValue());
        }
        List<Employee> employeeList = employeeDao.fetchAllEmployeeDetails(request);
        for (Employee employee : employeeList) {
            EmployeeDto employeeDto = new EmployeeDto();
            BeanUtils.copyProperties(employee, employeeDto);
            employeeDto.setStatus(EmployeeStatus.findByName(employee.getStatus()));
            employeeDto.setGender(Gender.findByKey(employee.getGender()));
            employeeDto.setMaritalStatus(MaritalStatus.findByKey(employee.getMaritalStatus()));
            employeeDto.setLanguageKnown(Language.findByKey(employee.getLanguageKnown()));
            employeeDto.setQualification(Qualification.findByName(employee.getQualification()));
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
                BranchMaster branchMaster = branchMasterRepository.findByBranchId(employee.getBranchId()).orElse(null);
                if (branchMaster != null) {
                    employeeDto.setBranchBcName(StringUtils.hasText(branchMaster.getBusinessPartner()) ? branchMaster.getBusinessPartner() : "");
                    employeeDto.setBaseLocationName(StringUtils.hasText(branchMaster.getBranchName()) ? branchMaster.getBranchName() : "");
                    employeeDto.setBaseLocationCode(StringUtils.hasText(branchMaster.getBranchCode()) ? branchMaster.getBranchCode() : "");
                    if (branchMaster.getStateId() != null) {
                        stateRepository.findByStateId(branchMaster.getStateId()).ifPresent(stateMaster -> employeeDto.setBranchStateName(StringUtils.hasText(stateMaster.getStateName()) ? stateMaster.getStateName() : ""));
                    }
                }
            }
            if (employee.getSubDepartmentId() != null) {
                EmployeeDepartmentMaster employeeDepartmentMaster = employeeDepartmentRepository.findByOrgIdAndEmpDepartmentId(userSession.getOrganizationId(), employee.getSubDepartmentId());
                employeeDto.setSubDepartmentName(employeeDepartmentMaster == null ? "" : employeeDepartmentMaster.getEmpDepartmentName());
            }
            employeeDto.setEmergencyCon(employee.getEmergencyCon());
            employeeDtoList.add(employeeDto);
        }
        return new Response(SUCCESS, employeeDtoList, count, HttpStatus.OK);
    }

    @Override
    public Response getEmployeeDetail(Long employeeId) throws BadRequestException {
        EmployeeDto employeeDto = new EmployeeDto();
        UserSession userSession = userCredentialService.getUserSession();
        // fetch employee detail using organizationId and employeeId
        Employee employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employeeId);
        if (employee == null) {
            log.warn("Employee not found for employeeId : {} ", employeeId);
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        } else {
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
                employeeDto.setBranchId(Long.valueOf(employee.getBranchId()));
            }
            if (employee.getReferenceId() != null) {
                Employee emp = employeeRepository.findByEmployeeCodeAndOrganizationId(employee.getReferenceId(), userSession.getOrganizationId());
                employeeDto.setReferenceIdName(emp == null ? "" : emp.getEmployeeCode() + "-" + emp.getFirstName() + " " + emp.getLastName());
            }
            employeeDto.setCurrentCity(employee.getCurrentCity());
            employeeDto.setCurrentState(employee.getCurrentState());
            employeeDto.setCurrentPincode(employee.getCurrentPincode());
            employeeDto.setCurrentAdd(employee.getCurrentAdd());
            employeeDto.setPermanentCity(employee.getPermanentCity());
            employeeDto.setPermanentAdd(employee.getPermanentAdd());
            employeeDto.setPermanentPincode(employee.getPermanentPincode());
            employeeDto.setPermanentState(employee.getPermanentState());
            employeeDto.setIsVehicle(employee.getIsVehicle());
            employeeDto.setVehicleType(employee.getVehicleType());
            employeeDto.setVehicleNumber(employee.getVehicleNumber());
            employeeDto.setResignDate(DateTimeUtil.dateToString(employee.getResignDate()));
            employeeDto.setExitDate(DateTimeUtil.dateToString(employee.getExitDate()));
        }
        return new Response(SUCCESS, employeeDto, HttpStatus.OK);
    }

    @Override
    public Response validateAadhaarPanMobForSaveEmployee(EmployeeRequest request) {
        //check dedupe by Aadhaar card/Pan card/mobile number
        List<String> messages = new ArrayList<>();
        if (request.getAadharCard() != null) {
            List<Employee> employeesWithAadhaar = employeeRepository.findByAadharCardNumber(request.getAadharCard());
            if (!CollectionUtils.isEmpty(employeesWithAadhaar)) {
                messages.add("Existing Employees found with employeeCode " + employeesWithAadhaar.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Aadhaar-" + request.getAadharCard());
            }
        }
        if (StringUtils.hasText(request.getPancardNo())) {
            List<Employee> employeesWithPan = employeeRepository.findByPancardNumber(request.getPancardNo());
            if (!CollectionUtils.isEmpty(employeesWithPan)) {
                messages.add("Existing Employees found with employeeCode " + employeesWithPan.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and PAN-" + request.getPancardNo());
            }
        }
        if (request.getPersonalMob() != null) {
            List<Employee> employeesWithMobile = employeeRepository.findByPersonalMobileNumber(request.getPersonalMob());
            if (!CollectionUtils.isEmpty(employeesWithMobile)) {
                messages.add("Existing Employees found with employeeCode " + employeesWithMobile.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Mobile-" + request.getPersonalMob());
            }
        }

        return new Response(SUCCESS, messages, HttpStatus.OK);
    }

    @Override
    public Response validateAadhaarPanMobForUpdateEmployee(EmployeeRequest request) {
        //check dedupe by Aadhaar card/Pan card/mobile number
        List<String> messages = new ArrayList<>();
        if (request.getAadharCard() != null) {
            List<Employee> employeesWithAadhaar = employeeRepository.findByAadharCardNumber(request.getAadharCard());
            if (employeesWithAadhaar.size() > 1) {
                messages.add("Existing Employees found with employeeCode " + employeesWithAadhaar.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Aadhaar-" + request.getAadharCard());
            } else if (employeesWithAadhaar.size() == 1) {
                Optional<Employee> first = employeesWithAadhaar.stream().filter(o -> o.getEmployeeId().equals(request.getEmployeeId())).findFirst();
                if (first.isEmpty()) {
                    messages.add("Employee-" + employeesWithAadhaar.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " already exist with Aadhaar-" + request.getAadharCard());
                }
            }
        }
        if (StringUtils.hasText(request.getPancardNo())) {
            List<Employee> employeesWithPan = employeeRepository.findByPancardNumber(request.getPancardNo());
            if (employeesWithPan.size() > 1) {
                messages.add("Existing Employees found with employeeCode " + employeesWithPan.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and PAN-" + request.getPancardNo());
            } else if (employeesWithPan.size() == 1) {
                Optional<Employee> first = employeesWithPan.stream().filter(o -> o.getEmployeeId().equals(request.getEmployeeId())).findFirst();
                if (first.isEmpty()) {
                    messages.add("Employee-" + employeesWithPan.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " already exist with PAN-" + request.getPancardNo());
                }
            }
        }
        if (request.getPersonalMob() != null) {
            List<Employee> employeesWithMobile = employeeRepository.findByPersonalMobileNumber(request.getPersonalMob());
            if (employeesWithMobile.size() > 1) {
                messages.add("Existing Employees found with employeeCode " + employeesWithMobile.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Mobile-" + request.getPersonalMob());
            } else if (employeesWithMobile.size() == 1) {
                Optional<Employee> first = employeesWithMobile.stream().filter(o -> o.getEmployeeId().equals(request.getEmployeeId())).findFirst();
                if (first.isEmpty()) {
                    messages.add("Employee-" + employeesWithMobile.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " already exist with Mobile-" + request.getPersonalMob());
                }
            }
        }
        return new Response(SUCCESS, messages, HttpStatus.OK);
    }

    @Override
    public Response updateEmployeeDetails(EmployeeRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        // fetch employee detail using organizationId and employeeId
        Employee employee;
        if (request.getEmployeeId() != null) {
            try {
                employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), request.getEmployeeId());
            } catch (Exception exception) {
                throw new BadRequestException(exception.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (request.getEmployeeId() != null) {
                if (StringUtils.hasText(request.getStatus()) && !employee.getStatus().equalsIgnoreCase(request.getStatus())) {
                    String id = Long.toString(request.getEmployeeId());
                    List<String> statusList = Stream.of("A", "C", "R", "C2", "G").collect(Collectors.toList());
                    List<CenterMaster> centerMasterList = centerMasterRepository.findByAssignedToAndStatusIn(id, statusList);
                    if (!CollectionUtils.isEmpty(centerMasterList)) {
                        log.info("You can't mark this employee as Inactive because center is active for this employee {} ", employee.getEmployeeId());
                        throw new BadRequestException("You can't mark this employee as Inactive ", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            if (employee != null) {
                //Check for relieving date of employee
                checkRelievingDate(request, employee);

                // save value in employee master table
                saveValueEmployeeMaster(request, employee, request.getEmployeeId(), userSession);

                //save value in user master table
                if (request.getStatus().equals("A") || request.getStatus().equals("Active")) {
                    saveValueInUserMaster(request.getUserId(), request, true);
                } else if (request.getStatus().equals("X") || request.getStatus().equals("Inactive")) {
                    saveValueInUserMaster(request.getUserId(), request, false);
                    Optional<User> user = userRepository.findByUserId(request.getUserId());
                    user.ifPresent(userService::deleteTokenByUserId);
                }
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

    private void checkRelievingDate(EmployeeRequest request, Employee employee) throws BadRequestException {
        if (request.getRelievingDate() != null) {
            List<String> status = new ArrayList<>();
            status.add("A");
            List<CenterMaster> centerMasters = centerMasterRepository.findByBranchIdAndOrgIdAndStatusInAndAssignedTo(employee.getBranchId(), employee.getOrganizationId(), status, employee.getEmployeeCode());
            if (centerMasters != null && !centerMasters.isEmpty()) {
                throw new BadRequestException("Cannot edit relieving date of an employee when active center is assigned!", HttpStatus.BAD_REQUEST);
            }
        }
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
        List<EmployeeDepartmentMaster> employeeDepartmentMasterList = employeeDepartmentRepository.findByOrgIdAndMainEmpDeptIdAndIsMainEmpDept(userSession.getOrganizationId(), empDepartmentId, "N");
        Set<EmployeeDepartmentDto> employeeSubDeptMap = new HashSet<>();
        for (EmployeeDepartmentMaster employeeDepartmentMaster : employeeDepartmentMasterList) {
            EmployeeDepartmentDto employeeDepartmentDto = new EmployeeDepartmentDto();
            BeanUtils.copyProperties(employeeDepartmentMaster, employeeDepartmentDto);
            employeeDepartmentDto.setInsertedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getInsertedOn()));
            employeeDepartmentDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employeeDepartmentMaster.getUpdatedOn()));
            employeeSubDeptMap.add(employeeDepartmentDto);
        }
        return new Response(SUCCESS, employeeSubDeptMap, HttpStatus.OK);
    }

    @Override
    public Response transferEmployee(EmployeeTransferRequest transferRequest) throws BadRequestException {
        ValidationUtils.validateTransferRequest(transferRequest);
        UserSession userSession = userCredentialService.getUserSession();
        log.info("Request received to transfer employee {}", transferRequest.getEmployeeId());
        FilterRequest request = new FilterRequest();
        request.setEmployeeId(transferRequest.getEmployeeId() != null ? transferRequest.getEmployeeId() : 0L);
        request.setEmplDesigType(StringUtils.hasText(transferRequest.getEmpDesignationType()) ? transferRequest.getEmpDesignationType() : "");
        request.setEmplDesigAreaId(transferRequest.getEmpDestAreaId() != null ? transferRequest.getBaseLocationId() : 0L);
        request.setIsManager(StringUtils.hasText(transferRequest.getIsManager()) ? transferRequest.getIsManager() : "");
        request.setIsMeetingTransfer(StringUtils.hasText(transferRequest.getIsMeetingTransfer()) ? transferRequest.getIsMeetingTransfer() : "");
        request.setBasedLocationId(transferRequest.getBaseLocationId() != null ? transferRequest.getBaseLocationId() : 0L);
        return employeeDao.employeeTransferPackageCall(request, userSession.getOrganizationId(), userSession.getUserId());
    }
}