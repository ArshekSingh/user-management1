package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.dto.EmployeeDepartmentDto;
import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.dto.EmployeeMovementLogsRequest;
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
import com.sts.finncub.usermanagement.assembler.EmployeeAssembler;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.EmployeeBankResponse;
import com.sts.finncub.usermanagement.response.EmployeeResponse;
import com.sts.finncub.usermanagement.service.AuthenticationService;
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
    private final ClientMasterRepository clientMasterRepository;
    private final ClientMasterDraftRepository clientMasterDraftRepository;
    private final ClientBankDetailRepository clientBankDetailRepository;
    private final InventoryDetailsRepository inventoryDetailsRepository;
    private final ReferenceDetailRepository referenceDetailRepository;
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
    private final EmployeeAssembler employeeAssembler;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public Response addEmployee(EmployeeRequest request) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        validateRequest(request);
        Response response = validateActiveAadhaarOrPanOrMobOrBankForSaveEmployee(request);
        if (200 == response.getCode()) {
            return new Response(response.getMessage(), response.getData(), response.getStatus());
        }
        Employee employee = new Employee();
        EmployeeResponse employeeResponse = new EmployeeResponse();
        String userEmployeeId = userRepository.getGeneratedUserEmployeeId(userSession.getOrganizationId(), "EMP");
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        employee.setOrganizationId(userSession.getOrganizationId());
        employee.setEmployeeCode(employeeId);
        request.setUserId(userId);
        employee.setEmployeeId(Long.valueOf(employeeId));
        // save value in employee master
        employee = saveValueEmployeeMaster(request, employee, request.getEmployeeId(), userSession);
        if (StringUtils.hasText(request.getIsBranchManager()) && "Y".equalsIgnoreCase(request.getIsBranchManager())) {
            Optional<BranchMaster> branchMaster = branchMasterRepository.findByBranchMasterPK_OrgIdAndBranchMasterPK_BranchId(userSession.getOrganizationId(), employee.getBranchId());
            if (branchMaster.isPresent()) {
                BranchMaster updatedBranchMaster = branchMaster.get();
//                employee.setIsBranchManager(request.getIsBranchManager());
                updatedBranchMaster.setBranchManagerId(String.valueOf(employee.getEmployeeId()));
                branchMasterRepository.save(updatedBranchMaster);
            }
        }
        log.info("Employee save success fully");
        // create  employee user details in user master
        saveValueInUserMaster(userId, request, true);
        employeeResponse.setEmployeeId(employee.getEmployeeId());
        return new Response(SUCCESS, employeeResponse, HttpStatus.OK);
    }

    private void saveValueInUserMaster(String userId, EmployeeRequest employeeRequest, Boolean isActive) throws BadRequestException {
        UserRequest request = new UserRequest();
        request.setPassword(userId);
        request.setUserId(userId);
        request.setEmail(employeeRequest.getOfficialEmail());
        request.setName(employeeRequest.getFirstName());
        request.setMobileNumber(employeeRequest.getPersonalMob() != null ? String.valueOf(employeeRequest.getPersonalMob()) : "");
        request.setType("EMP");
        if (Boolean.TRUE.equals(isActive)) {
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

    private Employee saveValueEmployeeMaster(EmployeeRequest request, Employee employee, Long employeeId, UserSession userSession) {
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
        if (!(request.getBankAccNo().equals(employee.getBankAccNo())) || !(request.getIfscCode().equals(employee.getIfscCode()))) {
            employee.setBankAccNo(request.getBankAccNo());
            employee.setIfscCode(request.getIfscCode());
            employee.setBankAccType(request.getBankAccType());
            employee.setBankName(request.getBankName());
            employee.setBankBranch(request.getBankBranch());
            employee.setIsBankValidated("N");
            employee.setBankResponse("");
            employee.setBankValidationDate(null);
        } else {
            employee.setBankAccNo(request.getBankAccNo());
            employee.setIfscCode(request.getIfscCode());
            employee.setBankAccType(request.getBankAccType());
            employee.setBankName(request.getBankName());
            employee.setBankBranch(request.getBankBranch());
            employee.setIsBankValidated(request.getIsBankValidated());
            employee.setBankResponse(request.getBankResponse());
            employee.setBankValidationDate(DateTimeUtil.stringToDate(request.getBankValidationDate()));
        }
        employee.setNameInBank("Y".equalsIgnoreCase(request.getIsBankValidated()) ? request.getNameInBank() : null);
        employee.setIsNameVerified(StringUtils.hasText(request.getNameInBank()) ? request.getIsNameVerified() : "N");
        employee.setBankMMID(request.getBankMMID());
        employee.setBankVPA(request.getBankVPA());
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
        employee.setBaseLocation(request.getBaseLocation());
        employee.setIsBranchManager(request.getIsBranchManager());
        employee.setRelativeName(request.getRelativeName());
        //Set branch manager id as null when employee has been changed to inactive and branch manager id in branch
        Optional<BranchMaster> branchMaster = branchMasterRepository.findByBranchMasterPK_OrgIdAndBranchMasterPK_BranchId(userSession.getOrganizationId(), employee.getBranchId());
        if (branchMaster.isPresent()) {
            BranchMaster updatedBranchMaster = branchMaster.get();
            if (StringUtils.hasText(request.getStatus()) && ("X".equals(request.getStatus()) || "Inactive".equals(request.getStatus()) && (request.getEmployeeId() != null && StringUtils.hasText(updatedBranchMaster.getBranchManagerId()) && (request.getEmployeeId().equals(Long.valueOf(updatedBranchMaster.getBranchManagerId())))))) {
                updatedBranchMaster.setBranchManagerId(null);
            }
//            if (StringUtils.hasText(request.getIsBranchManager()) && "Y".equalsIgnoreCase(request.getIsBranchManager())) {
//                employee.setIsBranchManager(request.getIsBranchManager());
//                updatedBranchMaster.setBranchManagerId(String.valueOf(request.getEmployeeId()));
//            }
            employee = employeeRepository.save(employee);
            if (StringUtils.hasText(request.getIsBranchManager()) && request.getBranchId() != null && ("Y".equalsIgnoreCase(request.getIsBranchManager()))) {
                updatedBranchMaster.setBranchManagerId(String.valueOf(employee.getEmployeeId()));
            }
            branchMasterRepository.save(updatedBranchMaster);
        }
        return employee;
    }

    private void validateRequest(EmployeeRequest request) throws BadRequestException {
        // validate employee add or update request
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || !StringUtils.hasText(request.getStatus()) || !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getGender())) {
            assert request != null;
            log.warn("Request failed validation, these field are mandatory : Status {} , FirstName {} , Gender {} ", StringUtils.hasText(request.getStatus()), request.getFirstName(), request.getGender());
            throw new BadRequestException("Invalid Request", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.hasText(request.getIfscCode()) && StringUtils.hasText(request.getBankAccNo())) {
            List<ClientBankDetail> bankDetails = clientBankDetailRepository.findByClientBankDetailPk_OrgIdAndBankAccountNumber(userSession.getOrganizationId(), request.getBankAccNo());
            if (!CollectionUtils.isEmpty(bankDetails)) {
                log.warn("With the given bank acc no {} client exist", request.getBankAccNo());
                throw new BadRequestException("This bank account details already associated with another client", HttpStatus.BAD_REQUEST);
            }
        }
        if (request.getPersonalMob() != null) {
            List<ClientMasterDraft> clientMasterDraftList = clientMasterDraftRepository.findByClientMasterDraftPK_OrgIdAndMobileNumber(userSession.getOrganizationId(), String.valueOf(request.getPersonalMob()));
            if (!CollectionUtils.isEmpty(clientMasterDraftList)) {
                log.warn("This mobile number {} is linked with other client", request.getPersonalMob());
                throw new BadRequestException("This mobile number already associated with another client", HttpStatus.BAD_REQUEST);
            }
            List<ClientMaster> clientMaster = clientMasterRepository.findByClientMasterPK_OrgIdAndMobileNumber(userSession.getOrganizationId(), request.getPersonalMob().toString());
            if (!CollectionUtils.isEmpty(clientMaster)) {
                log.warn("Mobile number {} is already registered with client ", request.getPersonalMob());
                throw new BadRequestException("This mobile number already associated with another client", HttpStatus.BAD_REQUEST);
            }
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
            employeeDto.setAadharCard(employee.getAadharCard());
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
            employeeDto.setBaseLocation(employee.getBaseLocation());
            employeeDto.setIsBranchManager(employee.getIsBranchManager());
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
                BranchMaster branchMaster = branchMasterRepository.findByBranchMasterPK_OrgIdAndBranchMasterPK_BranchId(userSession.getOrganizationId(), employee.getBranchId()).orElse(null);
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
            employeeDto.setInsertedOn(DateTimeUtil.dateTimeToString(employee.getInsertedOn(), DD_MM_YYYY));
            employeeDto.setRelativeName(employee.getRelativeName());
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
            employeeDto.setAadharCard(employee.getAadharCard());
            employeeDto.setDob(DateTimeUtil.dateToString(employee.getDob()));
            employeeDto.setJoiningDate(DateTimeUtil.dateToString(employee.getJoiningDate()));
            employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employee.getConfirmationDate()));
            employeeDto.setRelievingDate(DateTimeUtil.dateToString(employee.getRelievingDate()));
            employeeDto.setPromotionDate(DateTimeUtil.dateToString(employee.getPromotionDate()));
            employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employee.getBranchJoinDate()));
            employeeDto.setDepartmentName(employee.getEmployeeDepartmentMaster() == null ? "" : employee.getEmployeeDepartmentMaster().getEmpDepartmentName());
            employeeDto.setDesignationName(employee.getEmployeeDesignationMaster() == null ? "" : employee.getEmployeeDesignationMaster().getEmpDesignationName());
            employeeDto.setBaseLocation(employee.getBaseLocation());
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
                if (accManager != null) {
                    Long accEmployee;
                    String name;
                    String finalName = null;
                    if (accManager.getEmployeeId() != null) {
                        accEmployee = accManager.getEmployeeId();
                        finalName = accEmployee + "-";
                    }
                    if (StringUtils.hasText(accManager.getFirstName())) {
                        name = accManager.getFirstName();
                        finalName = finalName + name;
                    }
                    employeeDto.setAccountManagerName(finalName);
                }
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
            employeeDto.setIsBranchManager(employee.getIsBranchManager());
            employeeDto.setIsBankValidated(employee.getIsBankValidated());
            employeeDto.setBankValidationDate(DateTimeUtil.dateToString(employee.getBankValidationDate()));
            employeeDto.setBankResponse(employee.getBankResponse());
            employeeDto.setValidationAttempts(employee.getValidationAttempts());
            employeeDto.setRelativeName(employee.getRelativeName());
            if ("Y".equals(employee.getIsBankValidated())) {
                employeeDto.setNameInBank(employee.getNameInBank());
            }
            employeeDto.setIsNameVerified(employee.getIsNameVerified());
        }
        return new Response(SUCCESS, employeeDto, HttpStatus.OK);
    }

    @Override
    public Response validateAadhaarPanMobBankForSaveEmployee(EmployeeRequest request) {
        //check dedupe by Aadhaar card/Pan card/mobile number
        List<String> messages = new ArrayList<>();
        if (request.getAadharCard() != null) {
            List<Employee> employeesWithAadhaar = employeeRepository.findByAadharCardNumber(request.getAadharCard());
            if (!CollectionUtils.isEmpty(employeesWithAadhaar)) {
                messages.add(EXISTING_EMPLOYEE_MSG + employeesWithAadhaar.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Aadhaar-" + request.getAadharCard());
            }
        }
        if (StringUtils.hasText(request.getPancardNo())) {
            List<Employee> employeesWithPan = employeeRepository.findByPancardNumber(request.getPancardNo());
            if (!CollectionUtils.isEmpty(employeesWithPan)) {
                messages.add(EXISTING_EMPLOYEE_MSG + employeesWithPan.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and PAN-" + request.getPancardNo());
            }
        }
        if (request.getPersonalMob() != null) {
            List<Employee> employeesWithMobile = employeeRepository.findByPersonalMobileNumber(request.getPersonalMob());
            if (!CollectionUtils.isEmpty(employeesWithMobile)) {
                messages.add(EXISTING_EMPLOYEE_MSG + employeesWithMobile.stream().map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList()) + " and Mobile-" + request.getPersonalMob());
            }
        }
        if (request.getBankAccNo() != null && request.getIfscCode() != null) {
            Employee employeesWithBankNumber = employeeRepository.findByOrganizationIdAndBankAccNoAndIfscCodeAndStatus(userCredentialService.getUserSession().getOrganizationId(), request.getBankAccNo(), request.getIfscCode(), "A");
            if (employeesWithBankNumber != null) {
                messages.add(EXISTING_EMPLOYEE_MSG + " : " + employeesWithBankNumber.getEmployeeCode() + ", EmployeeName : " + employeesWithBankNumber.getFirstName() + ", bank_account_number : " + request.getBankAccNo() + " and ifsc_code : " + request.getIfscCode());
            }
        }
        return new Response(SUCCESS, messages, HttpStatus.OK);
    }

    @Override
    public Response validateAadhaarPanMobBankForUpdateEmployee(EmployeeRequest request) {
        //check dedupe by Aadhaar card/Pan card/mobile number
        List<String> messages = new ArrayList<>();
        String s = " is already mapped with employee- ";
        if (request.getAadharCard() != null) {
            List<Employee> employeesWithAadhaar = employeeRepository.findByAadharCardNumber(request.getAadharCard());
            if (!CollectionUtils.isEmpty(employeesWithAadhaar)) {
                List<Employee> collect = employeesWithAadhaar.stream().filter(o -> !o.getEmployeeId().equals(request.getEmployeeId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    messages.add("AADHAAR-" + request.getAadharCard() + s + collect.stream().map(Employee::getEmployeeId).collect(Collectors.toList()));
                }
            }
        }
        if (StringUtils.hasText(request.getPancardNo())) {
            List<Employee> employeesWithPan = employeeRepository.findByPancardNumber(request.getPancardNo());
            if (!CollectionUtils.isEmpty(employeesWithPan)) {
                List<Employee> collect = employeesWithPan.stream().filter(o -> !o.getEmployeeId().equals(request.getEmployeeId())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    messages.add("PAN-" + request.getPancardNo() + s + collect.stream().map(Employee::getEmployeeId).collect(Collectors.toList()));
                }
            }
        }
        if (request.getPersonalMob() != null) {
            List<Employee> employeesWithMobile = employeeRepository.findByPersonalMobileNumber(request.getPersonalMob());
            if (!CollectionUtils.isEmpty(employeesWithMobile)) {
                List<Employee> collect = employeesWithMobile.stream().filter(o -> o.getEmployeeId().equals(request.getEmployeeId())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    messages.add("MOBILE_NUMBER-" + request.getPancardNo() + s + collect.stream().map(Employee::getEmployeeId).collect(Collectors.toList()));
                }
            }
        }
        if (StringUtils.hasText(request.getBankAccNo()) && StringUtils.hasText(request.getIfscCode())) {
            Employee employeesWithBankAccount = employeeRepository.findByOrganizationIdAndBankAccNoAndIfscCodeAndStatus(userCredentialService.getUserSession().getOrganizationId(), request.getBankAccNo(), request.getIfscCode(), "A");
            if (employeesWithBankAccount != null && (!employeesWithBankAccount.getEmployeeId().equals(request.getEmployeeId()))) {
                messages.add("BANK-" + request.getBankAccNo() + s + request.getEmployeeId());
            }
        }
        return new Response(SUCCESS, messages, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Response updateEmployeeDetails(EmployeeRequest request) throws BadRequestException {
        validateRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        // fetch employee detail using organizationId and employeeId
        Employee employee;
        if (request.getEmployeeId() != null) {
            try {
                employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), request.getEmployeeId());
            } catch (Exception exception) {
                return new Response(exception.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (request.getEmployeeId() != null && (StringUtils.hasText(request.getStatus()) && !employee.getStatus().equalsIgnoreCase(request.getStatus()))) {
                String id = Long.toString(request.getEmployeeId());
                List<String> statusList = Stream.of("A", "C", "R", "C2", "G").collect(Collectors.toList());
                List<CenterMaster> centerMasterList = centerMasterRepository.findByCenterMasterPK_OrgIdAndAssignedToAndStatusIn(userSession.getOrganizationId(), id, statusList);
                if (!CollectionUtils.isEmpty(centerMasterList)) {
                    log.info("You can't mark this employee as Inactive because center is active for this employee {} ", employee.getEmployeeId());
                    return new Response("You can't mark this employee as Inactive ", HttpStatus.BAD_REQUEST);
                }
                List<InventoryDetails> inventoryDetailsList = inventoryDetailsRepository.findByOrgIdAndInventoryStaffId(userSession.getOrganizationId(), employee.getEmployeeId());
                if (!CollectionUtils.isEmpty(inventoryDetailsList)) {
                    List<ReferenceDetail> referenceDetail = referenceDetailRepository.findByReferenceDetailPK_ReferenceDomain(ReferenceDomain.RD_INVENTORY_ASSET_STATUS.name());
                    for (InventoryDetails inventoryDetails : inventoryDetailsList) {
                        if (!"RS".equalsIgnoreCase(inventoryDetails.getAssetStatus())) {
                            Optional<String> assetStatusOptional = referenceDetail.stream().filter(o -> o.getReferenceDetailPK().getKeyValue().equalsIgnoreCase(inventoryDetails.getAssetStatus())).map(ReferenceDetail::getDescription).findFirst();
                            if (assetStatusOptional.isPresent()) {
                                String assetStatus = assetStatusOptional.get();
                                log.info("You can't mark this employee {} as Inactive because asset status is {}", employee.getEmployeeId(), assetStatus);
                                return new Response("You can't mark this employee as Inactive because asset status is " + assetStatus, HttpStatus.BAD_REQUEST);
                            }
                        }
                    }
                }
            }
            if (employee != null) {
                //Check for relieving date of the employee
                checkRelievingDate(request, employee);
//                if (StringUtils.hasText(request.getRelievingDate()) || StringUtils.hasText(request.getStatus())) {
//                    LocalDate relievingDate = DateTimeUtil.stringToDate(request.getRelievingDate());
//                    LocalDate currentDate = LocalDate.now();
//                    if (currentDate.isAfter(relievingDate != null ? relievingDate : currentDate) || "X".equalsIgnoreCase(request.getStatus())) {
//                        log.info("Employee details cannot be updated because either status is inactive or employee is already relieved for employee id {}", request.getEmployeeId());
//                        return new Response("Employee details cannot be updated because either status is inactive or employee is already relieved", HttpStatus.BAD_REQUEST);
//                    }
//                }
                //save employee promotion details in employee_movement_logs
                if (isFieldsUpdated(request, employee)) {
                    employeeAssembler.dtoToEntity(request, userSession);
                }
                // save value in employee master table
                saveValueEmployeeMaster(request, employee, request.getEmployeeId(), userSession);
                //save value in user master table
                if (request.getStatus().equals("A") || request.getStatus().equals("Active")) {
                    saveValueInUserMaster(request.getUserId(), request, true);
                } else if (request.getStatus().equals("X") || request.getStatus().equals("Inactive")) {
                    saveValueInUserMaster(request.getUserId(), request, false);
                    authenticationService.revokeUserSessionFromRedis(userSession.getOrganizationId(), request.getUserId());
                }
                return new Response(SUCCESS, HttpStatus.OK);
            } else {
                throw new BadRequestException("Invalid Employee Id", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean isFieldsUpdated(EmployeeRequest request, Employee employee) {
        return !Objects.equals(request.getPromotionDate(), DateTimeUtil.dateToString(employee.getPromotionDate())) || !Objects.equals(request.getDepartmentId(), employee.getDepartmentId()) || !Objects.equals(request.getSubDepartmentId(), employee.getSubDepartmentId()) || !Objects.equals(request.getDesignationType(), employee.getDesignationType()) || !Objects.equals(request.getDesignationId(), employee.getDesignationId()) || !Objects.equals(request.getPersonalMob(), employee.getPersonalMob());
    }

    private void checkRelievingDate(EmployeeRequest request, Employee employee) throws BadRequestException {
        if (request.getRelievingDate() != null) {
            List<String> status = new ArrayList<>();
            status.add("A");
            List<CenterMaster> centerMasters = centerMasterRepository.findByCenterMasterPK_OrgIdAndBranchIdAndStatusInAndAssignedTo(employee.getOrganizationId(), employee.getBranchId(), status, employee.getEmployeeCode());
            if (centerMasters != null && !centerMasters.isEmpty()) {
                throw new BadRequestException("Cannot edit relieving date of an employee when active center is assigned!", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public Response employeeTransferPackageCall(FilterRequest filterRequest) throws BadRequestException {
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
        return employeeDao.employeeTransferPackageCall(filterRequest, userSession.getOrganizationId(), userSession.getUserId());
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

    @Override
    public Response validateActiveAadhaarOrPanOrMobOrBankForSaveEmployee(EmployeeRequest request) {
        //check dedupe by Aadhaar card/Pan card/mobile number
        List<String> messages = new ArrayList<>();
        String s = " you cannot add existing employee";
        if (request.getAadharCard() != null) {
            List<Employee> employeesWithAadhaar = employeeRepository.findByAadharCardNumber(request.getAadharCard());
            if (!CollectionUtils.isEmpty(employeesWithAadhaar)) {
                List<String> employeeWithAadhar = employeesWithAadhaar.stream().filter(o -> "A".equalsIgnoreCase(o.getStatus())).map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(employeeWithAadhar)) {
                    messages.add(EXISTING_ACTIVE_EMPLOYEE_MSG + employeeWithAadhar + " and Aadhaar-" + request.getAadharCard() + s);
                }
            }
        }
        if (StringUtils.hasText(request.getPancardNo())) {
            List<Employee> employeesWithPan = employeeRepository.findByPancardNumber(request.getPancardNo());
            if (!CollectionUtils.isEmpty(employeesWithPan)) {
                List<String> employeeWithPan = employeesWithPan.stream().filter(o -> "A".equalsIgnoreCase(o.getStatus())).map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(employeeWithPan)) {
                    messages.add(EXISTING_ACTIVE_EMPLOYEE_MSG + employeeWithPan + " and PAN-" + request.getPancardNo() + s);
                }
            }
        }
        if (request.getPersonalMob() != null) {
            List<Employee> employeesWithMobile = employeeRepository.findByPersonalMobileNumber(request.getPersonalMob());
            if (!CollectionUtils.isEmpty(employeesWithMobile)) {
                List<String> employeeWithPersonalMob = employeesWithMobile.stream().filter(o -> "A".equalsIgnoreCase(o.getStatus())).map(o -> o.getEmployeeCode() + "-" + o.getFirstName()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(employeeWithPersonalMob)) {
                    messages.add(EXISTING_ACTIVE_EMPLOYEE_MSG + employeeWithPersonalMob + " and Mobile-" + request.getPersonalMob() + s);
                }
            }
        }
        if (StringUtils.hasText(request.getBankAccNo()) && StringUtils.hasText(request.getIfscCode())) {
            Employee employeesWithBankNumber = employeeRepository.findByOrganizationIdAndBankAccNoAndIfscCodeAndStatus(userCredentialService.getUserSession().getOrganizationId(), request.getBankAccNo(), request.getIfscCode(), "A");
            if (employeesWithBankNumber != null) {
                messages.add(EXISTING_ACTIVE_EMPLOYEE_MSG + ": " + employeesWithBankNumber.getEmployeeCode() + ", employee Name : " + employeesWithBankNumber.getFirstName() + ", " + "bank_account_number : " + request.getBankAccNo() + " and ifsc_code : " + request.getIfscCode() + ", you cannot add existing employee");
            }
        }
        if (!CollectionUtils.isEmpty(messages)) {
            return new Response(SUCCESS, messages, HttpStatus.OK);
        }
        return new Response(SUCCESS, messages, HttpStatus.BAD_REQUEST);
    }

    @Override
    public Response fetchEmployeeMovementLogs(EmployeeMovementLogsRequest request) throws BadRequestException {
        if (request.getEmployeeId() == null) {
            log.error("Employee id cannot be null");
            throw new BadRequestException("Employee id cannot be null", HttpStatus.BAD_REQUEST);
        }
        UserSession userSession = userCredentialService.getUserSession();
        Long count = null;
        try {
            if (request.getStart() == 0) {
                count = employeeDao.getEmployeeDetailsByEmployeeIdCount(userSession, request);
            }
            if ("Y".equalsIgnoreCase(request.getIsCsv()) && count != null) {
                request.setLimit(count.intValue());
            }
            List<EmployeeMovementLogs> employeeMovementLogsList = employeeDao.getEmployeeDetailsByEmployeeId(userSession, request);
            if (CollectionUtils.isEmpty(employeeMovementLogsList)) {
                log.error("No employee logs found against employee id {}", request.getEmployeeId());
                return new Response("No employee movement logs found against employee id " + request.getEmployeeId(), HttpStatus.NOT_FOUND);
            }
            List<EmployeeDto> employeeDtos = employeeAssembler.entityToDtoList(employeeMovementLogsList, userSession);
            log.info("Transaction successful for employee id {}", request.getEmployeeId());
            return new Response(SUCCESS, employeeDtos, count, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("Exception occurred due to {}", exception.getMessage());
            return new Response("Exception occurred due to " + exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateEmployeeBankDetails(EmployeeRequest employeeRequest) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        validateEmployeeBankUpdateRequest(employeeRequest);
        Employee employee = employeeRepository.findByOrganizationIdAndEmployeeId(userSession.getOrganizationId(), employeeRequest.getEmployeeId());
        if (employee == null) {
            throw new BadRequestException("No data found", HttpStatus.BAD_REQUEST);
        }
        employee.setIsBankValidated(employeeRequest.getIsBankValidated());
        employee.setBankAccNo(employeeRequest.getBankAccNo());
        employee.setIfscCode(employeeRequest.getIfscCode());
        employee.setBankAccType(employeeRequest.getBankAccType());
        employee.setBankName(employeeRequest.getBankName());
        employee.setBankBranch(employeeRequest.getBankBranch());
        if (employee.getBankAccNo().equals(employeeRequest.getBankAccNo())) {
            employee.setIsNameVerified(employeeRequest.getIsNameVerified());
        } else {
            employee.setIsNameVerified("N");
        }
        employeeRepository.save(employee);
        EmployeeBankResponse bankResponse = getEmployeeBankResponse(employee);
        return new Response(SUCCESS, bankResponse, HttpStatus.OK);
    }

    private static EmployeeBankResponse getEmployeeBankResponse(Employee employee) {
        EmployeeBankResponse bankResponse = new EmployeeBankResponse();
        bankResponse.setBankBranch(employee.getBankBranch());
        bankResponse.setBankName(employee.getBankName());
        bankResponse.setBankAccNo(employee.getBankAccNo());
        bankResponse.setBankAccType(employee.getBankAccType());
        bankResponse.setIsBankValidated(employee.getIsBankValidated());
        bankResponse.setIfscCode(employee.getIfscCode());
        bankResponse.setIsNameVerified(employee.getIsNameVerified());
        return bankResponse;
    }

    private void validateEmployeeBankUpdateRequest(EmployeeRequest employeeRequest) throws BadRequestException {
        if (employeeRequest == null || !StringUtils.hasText(employeeRequest.getBankAccNo()) || !StringUtils.hasText(employeeRequest.getIfscCode()) || !StringUtils.hasText(employeeRequest.getIsBankValidated()) || employeeRequest.getEmployeeId() == null) {
            log.warn("Request failed validation, these field are mandatory : BankAccNo, Ifsc, IsBankValidated");
            throw new BadRequestException("Invalid Request", HttpStatus.BAD_REQUEST);
        }
    }
}