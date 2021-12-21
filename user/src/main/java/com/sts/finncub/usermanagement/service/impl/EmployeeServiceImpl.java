package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeDto;
import com.sts.finncub.core.entity.Employee;
import com.sts.finncub.core.entity.EmployeePK;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeRepository;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.repository.dao.EmployeeDao;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.EmployeeService;
import com.sts.finncub.usermanagement.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Author Sumit kumar

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final UserCredentialService userCredentialService;

    private final EmployeeRepository employeeRepository;

    private final EmployeeDao employeeDao;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserService userService;


    @Autowired
    public EmployeeServiceImpl(UserCredentialService userCredentialService,
                               EmployeeRepository employeeRepository, EmployeeDao employeeDao
            , UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserService userService) {
        this.userCredentialService = userCredentialService;
        this.employeeRepository = employeeRepository;
        this.employeeDao = employeeDao;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Response addEmployee(EmployeeRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        validateRequest(request);
        Employee employee = new Employee();
        EmployeePK employeePK = new EmployeePK();
        String userEmployeeId = userRepository.getGeneratedUserEmployeeId
                (userCredentialService.getUserSession().getOrganizationId(), "EMP");
        final String userId = userEmployeeId.split(",")[0];
        final String employeeId = userEmployeeId.split(",")[1];
        employeePK.setOrganizationId(userSession.getOrganizationId());
        employeePK.setEmployeeCode(employeeId);
        employee.setEmployeePK(employeePK);
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
        request.setMobileNumber(request.getMobileNumber());
        request.setType("EMP");
        request.setIsActive("Y");
        request.setEmployeeCreate(true);
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
        employee.setJoiningDate(StringUtils.hasText(request.getJoiningDate()) ? DateTimeUtil.stringToDate
                (request.getJoiningDate()) : null);
        employee.setConfirmationDate(StringUtils.hasText(request.getConfirmationDate()) ?
                DateTimeUtil.stringToDate(request.getConfirmationDate()) : null);
        employee.setPromotionDate(StringUtils.hasText(request.getPromotionDate()) ?
                DateTimeUtil.stringToDate(request.getPromotionDate()) : null);
        employee.setRelievingDate(StringUtils.hasText(request.getRelievingDate()) ?
                DateTimeUtil.stringToDate(request.getRelievingDate()) : null);
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
        employee.setBranchJoinDate(StringUtils.hasText(request.getBranchJoinDate()) ?
                DateTimeUtil.stringToDate(request.getBranchJoinDate()) : null);
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
        if (request == null || !StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getDob())
                || !StringUtils.hasText(request.getGender()) || !StringUtils.hasText(request.getStatus()) ||
                !StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getFatherName())
                || !StringUtils.hasText(request.getLanguageKnown()) || request.getPersonalMob() == null
                || !StringUtils.hasText(request.getCurrentAdd()) || !StringUtils.hasText(request.getCurrentCity())
                || !StringUtils.hasText(request.getCurrentState()) || request.getCurrentPincode() == null
                || !StringUtils.hasText(request.getPermanentAdd()) || !StringUtils.hasText(request.getPermanentCity())
                || !StringUtils.hasText(request.getPermanentState()) || request.getPermanentPincode() == null
                || !StringUtils.hasText(request.getDesignationType()) || request.getDesignationId() == null
                || request.getDepartmentId() == null || request.getSubDepartmentId() == null) {
            throw new BadRequestException("Invalid Request", HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public Response getAllEmployeeDetails() throws BadRequestException {
        Response response = new Response();
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        // fetch employee detail list using organizationId
        List<Employee> employeeList = employeeDao.fetchAllEmployeeDetails(
                userCredentialService.getUserSession().getOrganizationId());
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
            employeeDto.setEmployeeCode(employee.getEmployeePK().getEmployeeCode());
            employeeDto.setInsertedOn(DateTimeUtil.dateTimeToString(employee.getInsertedOn()));
            employeeDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employee.getUpdatedOn()));
            employeeDtoList.add(employeeDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeDtoList);
        return response;
    }

    @Override
    public Response getEmployeeDetail(Long employeeId) throws BadRequestException {
        Response response = new Response();
        EmployeeDto employeeDto = new EmployeeDto();
        // fetch employee detail using organizationId and employeeId
        Employee employee = employeeRepository.findByEmployeePK_OrganizationIdAndEmployeeId
                (userCredentialService.getUserSession().getOrganizationId(), employeeId);
        if (employee == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(employee, employeeDto);
        employeeDto.setJoiningDate(DateTimeUtil.dateToString(employee.getJoiningDate()));
        employeeDto.setConfirmationDate(DateTimeUtil.dateToString(employee.getConfirmationDate()));
        employeeDto.setRelievingDate(DateTimeUtil.dateToString(employee.getRelievingDate()));
        employeeDto.setPromotionDate(DateTimeUtil.dateToString(employee.getPromotionDate()));
        employeeDto.setBranchJoinDate(DateTimeUtil.dateToString(employee.getBranchJoinDate()));
        employeeDto.setInsertedOn(DateTimeUtil.dateTimeToString(employee.getInsertedOn()));
        employeeDto.setUpdatedOn(DateTimeUtil.dateTimeToString(employee.getUpdatedOn()));
        employeeDto.setEmployeeCode(employee.getEmployeePK().getEmployeeCode());
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
        // fetch employee detail using organizationId and employeeId
        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findByEmployeePK_OrganizationIdAndEmployeeId
                    (userCredentialService.getUserSession().getOrganizationId(), request.getEmployeeId());
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
}
