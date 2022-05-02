package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.UserLocationTrackerRequest;
import com.sts.finncub.usermanagement.request.UserRequest;

import javax.validation.Valid;

public interface UserService {

    Response getAllUserDetailsByFilterRequest(FilterRequest request) throws BadRequestException;

    Response getUserDetail(String userId) throws BadRequestException;

    Response addUser(UserRequest request) throws BadRequestException;

    Response updateUserDetails(UserRequest request) throws BadRequestException;

    Response getUserSearchable(String userSearchableKey, String userType);

    Response getUserRoleListAssignedOrAvailable(String userId);

    Response assignRolesToUser(UserRoleMappingDto userRoleMappingDto);

    Response getUserAssignedAndAvailableBranchList(String userId);

    Response assignBranchesToUser(UserBranchMappingDto userRoleMappingDto);

    Response postGeoLocationOfUser(@Valid UserLocationTrackerRequest userLocationTrackerRequest, String authorization);

    Response getAllUserSearchable(String searchUserKey, String userType);
}