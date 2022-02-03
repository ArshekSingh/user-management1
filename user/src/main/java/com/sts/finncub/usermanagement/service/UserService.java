package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.core.response.Response;

public interface UserService {

    Response getAllUserDetails(FilterRequest request) throws BadRequestException;

    Response getUserDetail(String userId) throws BadRequestException;

    Response addUser(UserRequest request) throws BadRequestException;

    Response updateUserDetails(UserRequest request) throws BadRequestException;

    Response getUserSearchable(String userSearchableKey);

    Response getUserRoleListAssignedOrAvailable(String userId);

    Response assignRolesToUser(UserRoleMappingDto userRoleMappingDto);

    Response getUserAssignedAndAvailableBranchList(String userId);

    Response assignBranchesToUser(UserBranchMappingDto userRoleMappingDto);
}