package com.sts.finncub.usermanagement.service.impl;

import com.google.gson.Gson;
import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.dto.ServerSideDropDownDto;
import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserDetailDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.repository.dao.UserDao;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.GeoLocationRequest;
import com.sts.finncub.usermanagement.request.UserLocationTrackerRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService, Constant {

    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserOrganizationMappingRepository userOrganizationMappingRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RoleMasterRepository roleMasterRepository;
    private final UserBranchMappingRepository userBranchMappingRepository;
    private final BranchMasterRepository branchMasterRepository;
    private final UserDao userDao;
    private final UserLocationTrackerRepository userLocationTrackerRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    @Autowired
    Gson gson;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserCredentialService userCredentialService, BCryptPasswordEncoder passwordEncoder, UserOrganizationMappingRepository userOrganizationMappingRepository, UserRoleMappingRepository userRoleMappingRepository, RoleMasterRepository roleMasterRepository, UserBranchMappingRepository userBranchMappingRepository, BranchMasterRepository branchMasterRepository, UserDao userDao, UserLoginLogRepository userLoginLogRepository, UserLocationTrackerRepository userLocationTrackerRepository) {
        this.userRepository = userRepository;
        this.userCredentialService = userCredentialService;
        this.passwordEncoder = passwordEncoder;
        this.userOrganizationMappingRepository = userOrganizationMappingRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.roleMasterRepository = roleMasterRepository;
        this.userBranchMappingRepository = userBranchMappingRepository;
        this.branchMasterRepository = branchMasterRepository;
        this.userDao = userDao;
        this.userLocationTrackerRepository = userLocationTrackerRepository;
        this.userLoginLogRepository = userLoginLogRepository;
    }

    @Override
    public Response getAllUserDetailsByFilterRequest(FilterRequest request) throws BadRequestException {
        Response response = new Response();
        List<UserDetailDto> userDetailDtos = new ArrayList<>();
        List<User> userList = userDao.getAllUserDetailsByFilterRequest(request);
        long count = 0L;
        if (!CollectionUtils.isEmpty(userList)) {
            count = userList.size();
        }
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                UserDetailDto userDetailDto = new UserDetailDto();
                BeanUtils.copyProperties(user, userDetailDto);
                userDetailDto.setPasswordResetDate(DateTimeUtil.dateToString(user.getPasswordResetDate()));
                userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.getDisabledOn()));
                userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.getApprovedOn()));
                userDetailDto.setInsertedOn(DateTimeUtil.dateTimeToString(user.getInsertedOn()));
                userDetailDto.setUpdatedOn(DateTimeUtil.dateTimeToString(user.getUpdatedOn()));
                userDetailDtos.add(userDetailDto);
            }
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setData(userDetailDtos);
            response.setTotalCount(count);
            response.setMessage("Transaction completed successfully.");
        }
        return response;
    }

    @Override
    public Response getUserDetail(String userId) throws BadRequestException {
        Response response = new Response();
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        UserDetailDto userDetailDto = new UserDetailDto();
        BeanUtils.copyProperties(user.get(), userDetailDto);
        userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.get().getDisabledOn()));
        userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.get().getApprovedOn()));
        userDetailDto.setInsertedOn(DateTimeUtil.dateTimeToString(user.get().getInsertedOn()));
        userDetailDto.setUpdatedOn(DateTimeUtil.dateTimeToString(user.get().getUpdatedOn()));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userDetailDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response addUser(UserRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        if (!request.isEmployeeCreate()) {
            if (request.getType().equalsIgnoreCase("EMP")) {
                throw new BadRequestException("Please create Employee", HttpStatus.BAD_REQUEST);
            }
        }

        if (!request.isEmployeeCreate()) {
            String userEmployeeId = userRepository.getGeneratedUserEmployeeId(userSession.getOrganizationId(), request.getType());
            final String userId = userEmployeeId.split(",")[0];
            request.setUserId(userId);
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setBcId(request.getBcId());
        user.setPasswordResetDate(LocalDate.now());
        user.setType(request.getType());
        user.setUserId(request.getUserId());
        if (StringUtils.hasText(request.getUserId())) {
            user.setPassword(passwordEncoder, request.getUserId());
        }
        user.setInsertedOn(LocalDateTime.now());
        user.setInsertedBy(userSession.getUserId());
        user.setIsTemporaryPassword("Y");
        user.setIsActive(request.getIsActive());
        user.setIsFrozenBookFlag("N");
        userRepository.save(user);
        //Save in user organization
        try {
            saveValueInUserOrganizationMapping(request.getUserId(), userSession);
        } catch (Exception exception) {
            log.debug("Error while mapping user - {}, to organization - {}", request.getUserId(), userSession.getOrganizationId());
            log.error(exception.getMessage());
        }
        //Save in user branch mapping if branch Id is present
        try {
            //TODO Need to handle different designation type as well
            if ("B".equalsIgnoreCase(request.getDesignationType())) {
                saveUserBranchMapping(request.getUserId(), request.getBranchId(), userSession);
            }
        } catch (Exception exception) {
            log.debug("Error while mapping user - {}, to branch - {}", request.getUserId(), request.getBranchId());
            log.error(exception.getMessage());
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    private void saveValueInUserOrganizationMapping(String userId, UserSession userSession) {
        UserOrganizationLinkId userOrganizationLinkId = new UserOrganizationLinkId();
        userOrganizationLinkId.setOrganizationId(userSession.getOrganizationId());
        userOrganizationLinkId.setUserId(userId);
        UserOrganizationMapping userOrganizationMapping = new UserOrganizationMapping();
        userOrganizationMapping.setId(userOrganizationLinkId);
        userOrganizationMapping.setActive("Y");
        userOrganizationMapping.setInsertedOn(LocalDateTime.now());
        userOrganizationMapping.setInsertedBy(userSession.getUserId());
        userOrganizationMappingRepository.save(userOrganizationMapping);
    }

    private void saveUserBranchMapping(String userId, Long branchId, UserSession userSession) {
        UserBranchMapping userBranchMapping = new UserBranchMapping();
        UserBranchMappingPK userBranchMappingPK = new UserBranchMappingPK();
        userBranchMappingPK.setUserId(userId);
        userBranchMappingPK.setBranchId(branchId);
        userBranchMappingPK.setOrgId(userSession.getOrganizationId());
        userBranchMapping.setUserBranchMappingPK(userBranchMappingPK);
        userBranchMapping.setInsertedOn(LocalDateTime.now());
        userBranchMapping.setInsertedBy(userSession.getUserId());
        userBranchMappingRepository.save(userBranchMapping);
    }

    private void validateRequest(UserRequest request) throws BadRequestException {
        if (request == null || !StringUtils.hasText(request.getName()) || request.getType() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateUserDetails(UserRequest request) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        if (!StringUtils.hasText(request.getUserId())) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(request.getUserId());
        if (user.isEmpty()) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        updateUser(request, userSession, user.get());
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void updateUser(UserRequest request, UserSession userSession, User userDetail) {
        userDetail.setName(request.getName());
        userDetail.setMobileNumber(request.getMobileNumber());
        userDetail.setType(request.getType());
        if (!userDetail.getIsActive().equalsIgnoreCase(request.getIsActive())) {
            if ("Y".equalsIgnoreCase(request.getIsActive())) {
                userDetail.setDisabledOn(null);
            } else {
                userDetail.setDisabledOn(LocalDate.now());
            }
        }
        userDetail.setIsActive(request.getIsActive());
        userDetail.setUpdatedBy(userSession.getUserId());
        userDetail.setUpdatedOn(LocalDateTime.now());
        userRepository.save(userDetail);
    }

    @Override
    public Response getUserSearchable(String userSearchableKey, String userType) {
        Response response = new Response();
        List<ServerSideDropDownDto> serverSideDropDownDtoList = new ArrayList<>();
        List<User> userList;
        if ("ALL".equalsIgnoreCase(userType)) {
            userList = userRepository.findByIsActiveAndUserIdIsContainingIgnoreCaseOrNameIsContainingIgnoreCase("Y", userSearchableKey, userSearchableKey);
        } else {
            userSearchableKey = "%" + userSearchableKey + "%";
            userList = userRepository.getUsers(userType, userSearchableKey, userSearchableKey);
        }
        for (User user : userList) {
            ServerSideDropDownDto serverSideDropDownDto = new ServerSideDropDownDto();
            serverSideDropDownDto.setId(user.getUserId());
            serverSideDropDownDto.setLabel(user.getUserId() + "-" + user.getName());
            serverSideDropDownDtoList.add(serverSideDropDownDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(serverSideDropDownDtoList);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response getUserRoleListAssignedOrAvailable(String userId) {
        Response response = new Response();
        List<UserRoleMapping> userRoleMappingList = userRoleMappingRepository.findById_UserIdContainingIgnoreCase(userId);
        UserRoleMappingDto userRoleMappingDto = new UserRoleMappingDto();
        List<ServerSideDropDownDto> userAssignedRolesList = new ArrayList<>();
        List<ServerSideDropDownDto> userAvailableRolesList = new ArrayList<>();
        List<Long> roleList = new ArrayList<>();

        for (UserRoleMapping userRoleMapping : userRoleMappingList) {
            userRoleMappingDto.setUserId(userId);
            ServerSideDropDownDto userAssignedRoles = new ServerSideDropDownDto();
            userAssignedRoles.setId(userRoleMapping.getRoleMaster().getRoleId().toString());
            userAssignedRoles.setLabel(userRoleMapping.getRoleMaster().getRoleName());
            userAssignedRolesList.add(userAssignedRoles);
            roleList.add(userRoleMapping.getRoleMaster().getRoleId());
        }
        List<RoleMaster> roleMasterList;
        if (roleList.isEmpty()) {
            roleMasterList = roleMasterRepository.findAll();
        } else {
            roleMasterList = roleMasterRepository.findByRoleIdNotIn(roleList);
        }
        for (RoleMaster roleMaster : roleMasterList) {
            ServerSideDropDownDto userAvailableRoles = new ServerSideDropDownDto();
            userAvailableRoles.setId(roleMaster.getRoleId().toString());
            userAvailableRoles.setLabel(roleMaster.getRoleName());
            userAvailableRolesList.add(userAvailableRoles);
        }
        userRoleMappingDto.setAssignedRoles(userAssignedRolesList);
        userRoleMappingDto.setAvailableRoles(userAvailableRolesList);

        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userRoleMappingDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response assignRolesToUser(UserRoleMappingDto userRoleMappingDto) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<UserRoleMapping> userRoleMappingList = userRoleMappingRepository.findById_UserIdContainingIgnoreCase(userRoleMappingDto.getUserId());
        if (!CollectionUtils.isEmpty(userRoleMappingList)) {
            userRoleMappingRepository.deleteAll(userRoleMappingList);
        }
        List<ServerSideDropDownDto> assignedRoleList = userRoleMappingDto.getAssignedRoles();
        for (ServerSideDropDownDto assignedRole : assignedRoleList) {
            UserRoleMapping userRoleMapping = new UserRoleMapping();
            UserRoleOrganizationLinkId userRoleOrganizationLinkId = new UserRoleOrganizationLinkId();
            userRoleOrganizationLinkId.setUserId(userRoleMappingDto.getUserId());
            userRoleOrganizationLinkId.setRoleId(Long.valueOf(assignedRole.getId()));
            userRoleOrganizationLinkId.setOrganizationId(userSession.getOrganizationId());
            userRoleMapping.setId(userRoleOrganizationLinkId);
            userRoleMapping.setInsertedOn(LocalDateTime.now());
            userRoleMapping.setInsertedBy(userSession.getUserId());
            userRoleMappingRepository.save(userRoleMapping);
        }

        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response getUserAssignedAndAvailableBranchList(String userId) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_UserIdContainingIgnoreCase(userId);
        UserBranchMappingDto userBranchMappingDto = new UserBranchMappingDto();
        List<ServerSideDropDownDto> userAssignedBranchesList = new ArrayList<>();
        List<ServerSideDropDownDto> userAvailableBranchesList = new ArrayList<>();
        List<Integer> branchList = new ArrayList<>();
        for (UserBranchMapping userBranchMapping : userBranchMappingList) {
            userBranchMappingDto.setUserId(userId);
            ServerSideDropDownDto userAssignedBranches = new ServerSideDropDownDto();
            userAssignedBranches.setId(userBranchMapping.getBranchMaster().getBranchId().toString());
            userAssignedBranches.setLabel(userBranchMapping.getBranchMaster().getBranchCode() + "-" + userBranchMapping.getBranchMaster().getBranchName());
            userAssignedBranchesList.add(userAssignedBranches);
            branchList.add(userBranchMapping.getBranchMaster().getBranchId());
        }
        List<BranchMaster> branchMasterList;
        if (branchList.isEmpty()) {
            branchMasterList = branchMasterRepository.findAllByOrgIdAndBranchType(userSession.getOrganizationId(), "BR");
        } else {
            branchMasterList = branchMasterRepository.findByBranchIdNotInAndOrgIdAndBranchType(branchList, userSession.getOrganizationId(), "BR");
        }
        for (BranchMaster branchMaster : branchMasterList) {
            ServerSideDropDownDto userAvailableBranches = new ServerSideDropDownDto();
            userAvailableBranches.setId(branchMaster.getBranchId().toString());
            userAvailableBranches.setLabel(branchMaster.getBranchName());
            userAvailableBranches.setLabel(branchMaster.getBranchCode() + "-" + branchMaster.getBranchName());
            userAvailableBranchesList.add(userAvailableBranches);
        }
        userBranchMappingDto.setAssignedBranches(userAssignedBranchesList);
        userBranchMappingDto.setAvailableBranches(userAvailableBranchesList);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userBranchMappingDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response assignBranchesToUser(UserBranchMappingDto userBranchMappingDto) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_UserIdContainingIgnoreCase(userBranchMappingDto.getUserId());
        if (!CollectionUtils.isEmpty(userBranchMappingList)) {
            userBranchMappingRepository.deleteAll(userBranchMappingList);
        }
        List<ServerSideDropDownDto> assignedBranchesList = userBranchMappingDto.getAssignedBranches();
        for (ServerSideDropDownDto assignedBranches : assignedBranchesList) {
            UserBranchMapping userBranchMapping = new UserBranchMapping();
            UserBranchMappingPK userBranchMappingPK = new UserBranchMappingPK();
            userBranchMappingPK.setUserId(userBranchMappingDto.getUserId());
            userBranchMappingPK.setBranchId(Long.valueOf(assignedBranches.getId()));
            userBranchMappingPK.setOrgId(userSession.getOrganizationId());
            userBranchMapping.setUserBranchMappingPK(userBranchMappingPK);
            userBranchMapping.setInsertedOn(LocalDateTime.now());
            userBranchMapping.setInsertedBy(userSession.getUserId());
            userBranchMappingRepository.save(userBranchMapping);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response postGeoLocationOfUser(GeoLocationRequest geoLocationRequest, String authToken) {
        UserSession userSession = userCredentialService.getUserSession();
        log.info("Adding geo location , userId : {}", userSession.getUserId());
        UserLoginLog userLoginLog = userLoginLogRepository.findByTokenId(authToken.split(" ")[1]);
        geoLocationRequest.getUserLocationTrackerRequests().forEach(coordinates -> saveGeoLocation(coordinates, userLoginLog, userSession));
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void saveGeoLocation(UserLocationTrackerRequest coordinates, UserLoginLog userLoginLog, UserSession userSession) {

        UserLocationTracker userLocationTracker = new UserLocationTracker();
        userLocationTracker.setDeviceId(userLoginLog.getDeviceId());
        userLocationTracker.setIpAddress(userLoginLog.getIpAddress());
        userLocationTracker.setOrgId(userSession.getOrganizationId());
		if (StringUtils.hasText(coordinates.getTrackDateTime()))
			userLocationTracker.setTrackDateTime(
					DateTimeUtil.stringToDateTime(coordinates.getTrackDateTime(), Constant.YYYY_MM_DD_HH_MM_SS));
        userLocationTracker.setUserId(userSession.getUserId());
        userLocationTracker.setLattitude(coordinates.getLattitude());
        userLocationTracker.setLongitude(coordinates.getLongitude());
        userLocationTracker.setInsertedOn(LocalDate.now());
		if (coordinates.getDeviceInfo() != null)
			userLocationTracker.setDeviceInfo(gson.toJson(coordinates.getDeviceInfo()));
        userLocationTrackerRepository.save(userLocationTracker);
    }

    @Override
    public Response getAllUserSearchable(String searchUserKey, String userType) {
        Response response = new Response();
        List<ServerSideDropDownDto> serverSideDropDownDtoList = new ArrayList<>();
        List<User> userList;
        if ("ALL".equalsIgnoreCase(userType)) {
            userList = userRepository.findByUserIdIsContainingIgnoreCaseOrNameIsContainingIgnoreCase(searchUserKey, searchUserKey);
        } else {
            searchUserKey = "%" + searchUserKey + "%";
            userList = userRepository.getAllUsers(userType, searchUserKey, searchUserKey);
        }
        for (User user : userList) {
            ServerSideDropDownDto serverSideDropDownDto = new ServerSideDropDownDto();
            serverSideDropDownDto.setId(user.getUserId());
            serverSideDropDownDto.setLabel(user.getUserId() + "-" + user.getName());
            serverSideDropDownDtoList.add(serverSideDropDownDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(serverSideDropDownDtoList);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}