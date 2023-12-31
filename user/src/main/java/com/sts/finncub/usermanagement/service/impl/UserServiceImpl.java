package com.sts.finncub.usermanagement.service.impl;

import com.google.gson.Gson;
import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.dto.*;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.repository.dao.UserDao;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.request.UserFilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.assembler.RamsonUserSchedulerAssembler;
import com.sts.finncub.usermanagement.assembler.UserAssembler;
import com.sts.finncub.usermanagement.request.*;
import com.sts.finncub.usermanagement.service.AuthenticationService;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
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
    private final UserRedisRepository userRedisRepository;
    private final Gson gson;
    private final VwFoUserExportRepository vwFoUserExportRepository;
    private final RamsonUserSchedulerAssembler ramsonUserSchedulerAssembler;
    private final UserAssembler userAssembler;
    private final AuthenticationService authenticationService;
    private final EmployeeRepository employeeRepository;

    @Override
    public Response getAllUserDetailsByFilterRequest(FilterRequest request) throws BadRequestException {
        List<UserDetailDto> userDetailDtos = new ArrayList<>();
        Long count = null;
        if (request.getStart() == 0) {
            count = userDao.findAllFilterUserDetailsCount(request);
        }
        List<User> userList = userDao.getAllUserDetailsByFilterRequest(request);
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                UserDetailDto userDetailDto = new UserDetailDto();
                BeanUtils.copyProperties(user, userDetailDto);
                userDetailDto.setBcId(user.getBcId());
                userDetailDto.setPasswordResetDate(DateTimeUtil.dateTimeToString(user.getPasswordResetDate()));
                userDetailDto.setDisabledOn(DateTimeUtil.dateTimeToString(user.getDisabledOn()));
                userDetailDto.setApprovedOn(DateTimeUtil.dateTimeToString(user.getApprovedOn()));
                userDetailDto.setInsertedOn(DateTimeUtil.dateTimeToString(user.getInsertedOn()));
                userDetailDto.setUpdatedOn(DateTimeUtil.dateTimeToString(user.getUpdatedOn()));
                userDetailDtos.add(userDetailDto);
            }
        }
        return new Response(SUCCESS, userDetailDtos, count, HttpStatus.OK);
    }

    @Override
    public Response getUserDetail(String userId) throws BadRequestException {
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        UserDetailDto userDetailDto = new UserDetailDto();
        BeanUtils.copyProperties(user.get(), userDetailDto);
        userDetailDto.setBcId(user.get().getBcId());
        userDetailDto.setDisabledOn(DateTimeUtil.dateTimeToString(user.get().getDisabledOn()));
        userDetailDto.setApprovedOn(DateTimeUtil.dateTimeToString(user.get().getApprovedOn()));
        userDetailDto.setInsertedOn(DateTimeUtil.dateTimeToString(user.get().getInsertedOn()));
        userDetailDto.setUpdatedOn(DateTimeUtil.dateTimeToString(user.get().getUpdatedOn()));
        userDetailDto.setImeiNumber(user.get().getImeiNumber());
        userDetailDto.setIsPasswordActive(user.get().getIsPasswordActive());
        return new Response(SUCCESS, userDetailDto, HttpStatus.OK);
    }

    @Override
    public Response addUser(UserRequest request) throws BadRequestException {
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
        user.setPasswordResetDate(LocalDateTime.now());
        user.setType(request.getType());
        user.setUserId(request.getUserId());
        user.setIsPasswordActive("N");
        if (StringUtils.hasText(request.getUserId())) {
            String encode = passwordEncoder.encode(request.getUserId());
            user.setPassword(encode);
            user.setOldPassword(encode);
        }
        user.setInsertedOn(LocalDateTime.now());
        user.setInsertedBy(userSession.getUserId());
        user.setIsTemporaryPassword("Y");
        user.setIsActive(request.getIsActive());
        user.setIsFrozenBookFlag("N");
        user.setImeiNumber(request.getImeiNumber());
        userRepository.save(user);
        //Save in user organization
        try {
            saveValueInUserOrganizationMapping(request.getUserId(), userSession);
        } catch (Exception exception) {
            log.debug("Error while mapping user - {}, to organization - {}", request.getUserId(), userSession.getOrganizationId());
            log.error(exception.getMessage());
        }
        //Save in user branch mapping if branchId is present
        try {
            //TODO Need to handle different designation type as well
            if ("B".equalsIgnoreCase(request.getDesignationType())) {
                saveUserBranchMapping(request.getUserId(), request.getBranchId(), userSession);
            }
        } catch (Exception exception) {
            log.debug("Error while mapping user - {}, to branch - {}", request.getUserId(), request.getBranchId());
            log.error(exception.getMessage());
        }
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void validateRequest(UserRequest request) throws BadRequestException {
        if (request == null || !StringUtils.hasText(request.getName()) || request.getType() == null) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
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

    @Override
    public Response updateUserDetails(UserRequest request) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        if (!StringUtils.hasText(request.getUserId())) {
            return new Response("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(request.getUserId());
        if (user.isEmpty()) {
            return new Response("User Not Found", HttpStatus.BAD_REQUEST);
        }
        updateUser(request, userSession, user.get());
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void updateUser(UserRequest request, UserSession userSession, User userDetail) {
        userDetail.setName(request.getName());
        userDetail.setMobileNumber(request.getMobileNumber());
        userDetail.setType(request.getType());
        userDetail.setExtUserId(request.getExtUserId());
        if (!userDetail.getIsActive().equalsIgnoreCase(request.getIsActive())) {
            if ("Y".equalsIgnoreCase(request.getIsActive())) {
                userDetail.setDisabledOn(null);
            } else {
                userDetail.setDisabledOn(LocalDateTime.now());
            }
        }
        userDetail.setBcId(StringUtils.hasText(request.getBcId()) ? request.getBcId() : "");
        if (StringUtils.hasText(request.getIsActive())) {
            if (request.getIsActive().equals("N")) {
                userDetail.setIsActive(request.getIsActive());
                deleteTokenByUserId(userDetail);
            } else {
                userDetail.setIsActive(request.getIsActive());
            }
        }
        if (StringUtils.hasText(request.getImeiNumber())) {
            userDetail.setImeiNumber(request.getImeiNumber());
        }
        userDetail.setIsFrozenBookFlag(request.getIsFrozenBookFlag());
        userDetail.setUpdatedBy(userSession.getUserId());
        userDetail.setUpdatedOn(LocalDateTime.now());
        userDetail.setIsPasswordActive(request.getIsPasswordActive());
        if ("Y".equalsIgnoreCase(request.getIsPasswordActive())) {
            userDetail.setLoginAttempt(0);
        }
        userRepository.save(userDetail);
    }

    @Override
    public Response getUserSearchable(String userSearchableKey, String userType) {
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
        return new Response(SUCCESS, serverSideDropDownDtoList, HttpStatus.OK);
    }

    @Override
    public Response getUserRoleListAssignedOrAvailable(String userId) {
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
        return new Response(SUCCESS, userRoleMappingDto, HttpStatus.OK);
    }

    @Override
    public Response assignRolesToUser(UserRoleMappingDto userRoleMappingDto) {
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
        return new Response(SUCCESS, HttpStatus.OK);
    }

    @Override
    public Response getUserAssignedAndAvailableBranchList(String userId) {
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_OrgIdAndUserBranchMappingPK_UserIdContainingIgnoreCase(userSession.getOrganizationId(), userId);
        UserBranchMappingDto userBranchMappingDto = new UserBranchMappingDto();
        List<ServerSideDropDownDto> userAssignedBranchesList = new ArrayList<>();
        List<ServerSideDropDownDto> userAvailableBranchesList = new ArrayList<>();
        List<Integer> branchList = new ArrayList<>();
        for (UserBranchMapping userBranchMapping : userBranchMappingList) {
            userBranchMappingDto.setUserId(userId);
            ServerSideDropDownDto userAssignedBranches = new ServerSideDropDownDto();
            userAssignedBranches.setId(userBranchMapping.getBranchMaster().getBranchMasterPK().getBranchId().toString());
            userAssignedBranches.setLabel(userBranchMapping.getBranchMaster().getBranchCode() + "-" + userBranchMapping.getBranchMaster().getBranchName());
            userAssignedBranchesList.add(userAssignedBranches);
            branchList.add(userBranchMapping.getBranchMaster().getBranchMasterPK().getBranchId());
        }
        List<BranchMaster> branchMasterList;
        if (branchList.isEmpty()) {
            branchMasterList = branchMasterRepository.findAllByBranchMasterPK_OrgIdAndBranchType(userSession.getOrganizationId(), "BR");
        } else {
            branchMasterList = branchMasterRepository.findByBranchMasterPK_OrgIdAndBranchMasterPK_BranchIdNotInAndBranchType(userSession.getOrganizationId(), branchList, "BR");
        }
        for (BranchMaster branchMaster : branchMasterList) {
            ServerSideDropDownDto userAvailableBranches = new ServerSideDropDownDto();
            userAvailableBranches.setId(branchMaster.getBranchMasterPK().getBranchId().toString());
            userAvailableBranches.setLabel(branchMaster.getBranchName());
            userAvailableBranches.setLabel(branchMaster.getBranchCode() + "-" + branchMaster.getBranchName());
            userAvailableBranchesList.add(userAvailableBranches);
        }
        userBranchMappingDto.setAssignedBranches(userAssignedBranchesList);
        userBranchMappingDto.setAvailableBranches(userAvailableBranchesList);
        return new Response(SUCCESS, userBranchMappingDto, HttpStatus.OK);
    }

    @Override
    public Response assignBranchesToUser(UserBranchMappingDto userBranchMappingDto) {
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_OrgIdAndUserBranchMappingPK_UserIdContainingIgnoreCase(userSession.getOrganizationId(), userBranchMappingDto.getUserId());
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
        return new Response(SUCCESS, HttpStatus.OK);
    }

    @Override
    public Response postGeoLocationOfUser(GeoLocationRequest geoLocationRequest, String authToken) {
        UserSession userSession = userCredentialService.getUserSession();
        log.info("Adding geo location , userId : {}", userSession.getUserId());
        UserLoginLog userLoginLog = userLoginLogRepository.findByUserIdAndTokenId(userSession.getUserId(), authToken.split(" ")[1]);
        geoLocationRequest.getUserLocationTrackerRequests().forEach(coordinates -> saveGeoLocation(coordinates, userLoginLog, userSession));
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void saveGeoLocation(UserLocationTrackerRequest coordinates, UserLoginLog userLoginLog, UserSession userSession) {
        UserLocationTracker userLocationTracker = new UserLocationTracker();
        userLocationTracker.setDeviceId(userLoginLog.getDeviceId());
        userLocationTracker.setIpAddress(userLoginLog.getIpAddress());
        userLocationTracker.setOrgId(userSession.getOrganizationId());
        if (StringUtils.hasText(coordinates.getTrackDateTime())) {
            userLocationTracker.setTrackDateTime(DateTimeUtil.stringToDateTime(coordinates.getTrackDateTime(), Constant.YYYY_MM_DD_HH_MM_SS));
        }
        userLocationTracker.setUserId(userSession.getUserId());
        userLocationTracker.setLattitude(coordinates.getLattitude());
        userLocationTracker.setLongitude(coordinates.getLongitude());
        userLocationTracker.setInsertedOn(LocalDate.now());
        if (coordinates.getDeviceInfo() != null) {
            userLocationTracker.setDeviceInfo(gson.toJson(coordinates.getDeviceInfo()));
        }
        userLocationTrackerRepository.save(userLocationTracker);
    }

    @Override
    public Response getAllUserSearchable(String searchUserKey, String userType) {
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
        return new Response(SUCCESS, serverSideDropDownDtoList, HttpStatus.OK);
    }

    @Override
    public Response updateFirebaseToken(FirebaseTokenRequest firebaseTokenRequest) {
        try {
            if (firebaseTokenRequest.getUserId() == null || firebaseTokenRequest.getToken() == null)
                throw new BadRequestException("UserId/token cannot be empty!", HttpStatus.BAD_REQUEST);
            Optional<User> optional = userRepository.findByUserId(firebaseTokenRequest.getUserId());
            if (optional.isEmpty()) throw new BadRequestException("Invalid User Id provided!", HttpStatus.BAD_REQUEST);
            userRepository.updateFirebaseTokenByUserId(firebaseTokenRequest.getToken(), firebaseTokenRequest.getUserId());
            return new Response("Firebase token saved successfully!", HttpStatus.OK);
        } catch (Exception exception) {
            log.error("Something went wrong while updating firebase token {}", exception.getMessage());
            return new Response(SOMETHING_WRONG, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteTokenByUserId(User userDetail) {
        Iterable<UserSession> userSessionList = userRedisRepository.findAll();
        for (UserSession userSessionCheckForUser : userSessionList) {
            if (userSessionCheckForUser != null && userDetail != null) {
                if (!StringUtils.hasText(userDetail.getUserId()) || !StringUtils.hasText(userSessionCheckForUser.getUserId())) {
                    continue;
                }
                if (!userSessionCheckForUser.getUserId().equalsIgnoreCase(userDetail.getUserId())) {
                    continue;
                }
                Optional<UserSession> userSessionForParticularUser = userRedisRepository.findById(userSessionCheckForUser.getId());
                userSessionForParticularUser.ifPresent(userRedisRepository::delete);
            }
        }
    }

    @Override
    public List<RamsonUserRequest> getFoForRamson() {
        List<VwFoUserExport> vwFoUserExportList = vwFoUserExportRepository.findAll();
        List<RamsonUserRequest> ramsonUserRequestList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vwFoUserExportList)) {
            for (VwFoUserExport vwFoUserExport : vwFoUserExportList) {
                ramsonUserRequestList.add(ramsonUserSchedulerAssembler.prepareRequest(vwFoUserExport));
            }
        }
        return ramsonUserRequestList;
    }

    @Override
    public Response getUsersOnBranches(UserFilterRequest request) throws BadRequestException {
        UserSession userSession = userCredentialService.getUserSession();
        if (CollectionUtils.isEmpty(request.getIds())) {
            log.info("NO Branch filter is passed by : {}", userSession.getUserId());
            return new Response("Pass at-least one branch", HttpStatus.NOT_FOUND);
        }
        log.info("Request sent to db to fetch data for branches {}", request.getIds());
        List<Long> branchIds = userDao.findBranchesListByFilter(request, userSession);
        List<Integer> branches = branchIds.stream().map(Long::intValue).collect(Collectors.toList());
        List<BranchEmployeeDto> branchEmployeeDto = employeeRepository.findByBranchIdsIn(branches);
        log.info("Data fetched from db for branches {}", request.getIds());
        if (branchEmployeeDto.isEmpty()) {
            log.info("No users mapped on any branches : {}", request.getIds());
            return new Response("No users mapped on any branches", HttpStatus.NOT_FOUND);
        }
        return new Response(SUCCESS, userAssembler.assembleUser(branchEmployeeDto), HttpStatus.OK);
    }

    @Override
    public Response updateUserForEmployee(UserRequest request) {
        UserSession userSession = userCredentialService.getUserSession();
        if (!StringUtils.hasText(request.getUserId())) {
            return new Response("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(request.getUserId());
        if (user.isEmpty()) {
            return new Response("User Not Found", HttpStatus.BAD_REQUEST);
        }
        userDetailsUpdate(request, userSession, user.get());
        return new Response(SUCCESS, HttpStatus.OK);
    }

    private void userDetailsUpdate(UserRequest request, UserSession userSession, User userDetail) {
        userDetail.setName(request.getName());
        userDetail.setMobileNumber(request.getMobileNumber());
        userDetail.setType(request.getType());
        if (!userDetail.getIsActive().equalsIgnoreCase(request.getIsActive())) {
            if ("Y".equalsIgnoreCase(request.getIsActive())) {
                userDetail.setDisabledOn(null);
            } else {
                userDetail.setDisabledOn(LocalDateTime.now());
            }
        }
        if (StringUtils.hasText(request.getIsActive())) {
            if (request.getIsActive().equals("N")) {
                userDetail.setIsActive(request.getIsActive());
                authenticationService.revokeUserSessionFromRedis(userSession.getOrganizationId(), userDetail.getUserId());
            } else {
                userDetail.setIsActive(request.getIsActive());
            }
        }
        userDetail.setUpdatedBy(userSession.getUserId());
        userDetail.setUpdatedOn(LocalDateTime.now());
        userRepository.save(userDetail);
    }
}