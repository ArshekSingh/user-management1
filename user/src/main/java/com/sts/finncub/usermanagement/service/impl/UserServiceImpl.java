package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.ServerSideDropDownDto;
import com.sts.finncub.core.dto.UserDetailDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.RoleMasterRepository;
import com.sts.finncub.core.repository.UserOrganizationMappingRepository;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.repository.UserRoleMappingRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.Response;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserOrganizationMappingRepository userOrganizationMappingRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RoleMasterRepository roleMasterRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserCredentialService userCredentialService
            , BCryptPasswordEncoder passwordEncoder, UserOrganizationMappingRepository userOrganizationMappingRepository,
                           UserRoleMappingRepository userRoleMappingRepository, RoleMasterRepository roleMasterRepository) {
        this.userRepository = userRepository;
        this.userCredentialService = userCredentialService;
        this.passwordEncoder = passwordEncoder;
        this.userOrganizationMappingRepository = userOrganizationMappingRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.roleMasterRepository = roleMasterRepository;
    }

    @Override
    public Response getAllUserDetails() {
        Response response = new Response();
        List<UserDetailDto> userDetailDtos = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                UserDetailDto userDetailDto = new UserDetailDto();
                BeanUtils.copyProperties(user, userDetailDto);
                userDetailDto.setPasswordResetDate(DateTimeUtil.dateToString(user.getPasswordResetDate()));
                userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.getDisabledOn()));
                userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.getApprovedOn()));
                userDetailDto.setInsertedOn(DateTimeUtil.dateToString(user.getInsertedOn()));
                userDetailDto.setUpdatedOn(DateTimeUtil.dateToString(user.getUpdatedOn()));
                userDetailDtos.add(userDetailDto);
            }
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setData(userDetailDtos);
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
        if (user == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        UserDetailDto userDetailDto = new UserDetailDto();
        BeanUtils.copyProperties(user.get(), userDetailDto);
        userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.get().getDisabledOn()));
        userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.get().getApprovedOn()));
        userDetailDto.setInsertedOn(DateTimeUtil.dateToString(user.get().getInsertedOn()));
        userDetailDto.setUpdatedOn(DateTimeUtil.dateToString(user.get().getUpdatedOn()));
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
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isEmpty()) {
            if (!request.isEmployeeCreate()) {
                String userEmployeeId = userRepository.getGeneratedUserEmployeeId(
                        userSession.getOrganizationId(), request.getType());
                final String userId = userEmployeeId.split(",")[0];
                request.setUserId(userId);
            }
            User user = new User();
            BeanUtils.copyProperties(request, user);
            user.setPasswordResetDate(LocalDate.now());
            user.setType(request.getType());
            user.setUserId(request.getUserId());
            user.setPassword(passwordEncoder, request.getPassword());
            user.setInsertedOn(LocalDate.now());
            user.setInsertedBy(userSession.getUserId());
            user.setIsTemporaryPassword("Y");
            user.setIsFrozenBookFlag('N');
            userRepository.save(user);
            saveValueInUserOrganizationMapping(request.getUserId(), userSession.getOrganizationId(), "Y");
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Transaction completed successfully.");
        } else {
            throw new BadRequestException("User Email Id Already Exists in Our System", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    private void saveValueInUserOrganizationMapping(String userId, Long organizationId, String isActive) {
        UserOrganizationLinkId userOrganizationLinkId = new UserOrganizationLinkId();
        userOrganizationLinkId.setOrganizationId(organizationId);
        userOrganizationLinkId.setUserId(userId);
        UserOrganizationMapping userOrganizationMapping = new UserOrganizationMapping();
        userOrganizationMapping.setId(userOrganizationLinkId);
        userOrganizationMapping.setActive(isActive);
        userOrganizationMapping.setInsertedOn(LocalDateTime.now());
        userOrganizationMapping.setInsertedBy(userCredentialService.getUserSession().getUserId());
        userOrganizationMappingRepository.save(userOrganizationMapping);
    }

    private void validateRequest(UserRequest request) throws BadRequestException {
        if (request == null || !StringUtils.hasText(request.getName())
                || !StringUtils.hasText(request.getEmail()) || request.getType() == null ||
                !StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateUserDetails(UserRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (!StringUtils.hasText(request.getUserId())) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(request.getUserId());
        if (user.get() == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        if (user.get().getEmail().equalsIgnoreCase(request.getEmail())) {
            updateUser(request, response, userSession, user);
        } else {
            updateUser(request, response, userSession, user);
        }
        return response;
    }

    private void updateUser(UserRequest request, Response response,
                            UserSession userSession, Optional<User> user) throws BadRequestException {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isEmpty()) {
            User userDetail = user.get();
            userDetail.setName(request.getName());
            userDetail.setMobileNumber(request.getMobileNumber());
            userDetail.setType(request.getType());
            userDetail.setIsActive(request.getIsActive());
            userDetail.setUpdatedBy(userSession.getUserId());
            userDetail.setUpdatedOn(LocalDate.now());
            userRepository.save(userDetail);
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Transaction completed successfully.");
        } else {
            throw new BadRequestException("User Email Id Already Exists in Our System", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response getUserSearchable(String userSearchableKey) {
        Response response = new Response();
        List<ServerSideDropDownDto> serverSideDropDownDtoList = new ArrayList<>();
        List<User> userList = userRepository.findByUserIdContainingIgnoreCaseOrNameContainingIgnoreCase(userSearchableKey, userSearchableKey);
        for(User user : userList) {
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

        for(UserRoleMapping userRoleMapping : userRoleMappingList) {
            userRoleMappingDto.setUserId(userId);
            ServerSideDropDownDto userAssignedRoles = new ServerSideDropDownDto();
            userAssignedRoles.setId(userRoleMapping.getRoleMaster().getId().toString());
            userAssignedRoles.setLabel(userRoleMapping.getRoleMaster().getRoleName());
            userAssignedRolesList.add(userAssignedRoles);
            roleList.add(userRoleMapping.getRoleMaster().getId());
        }
        List<RoleMaster> roleMasterList = roleMasterRepository.findByIdNotIn(roleList);
        for(RoleMaster roleMaster : roleMasterList) {
            ServerSideDropDownDto userAvailableRoles = new ServerSideDropDownDto();
            userAvailableRoles.setId(roleMaster.getId().toString());
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
        for(ServerSideDropDownDto assignedRole : assignedRoleList) {
            UserRoleMapping userRoleMapping = new UserRoleMapping();
            UserRoleOrganizationLinkId userRoleOrganizationLinkId = new UserRoleOrganizationLinkId();
            userRoleOrganizationLinkId.setUserId(userRoleMappingDto.getUserId());
            userRoleOrganizationLinkId.setRoleId(Long.valueOf(assignedRole.getId()));
            userRoleOrganizationLinkId.setOrganizationId(userSession.getOrganizationId());
            userRoleMapping.setId(userRoleOrganizationLinkId);
            userRoleMapping.setInsertedOn(LocalDate.now());
            userRoleMapping.setInsertedBy(userSession.getUserId());
            userRoleMappingRepository.save(userRoleMapping);
        }

        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}