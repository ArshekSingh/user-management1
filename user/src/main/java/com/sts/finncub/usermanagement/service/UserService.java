package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.request.UserFilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.FirebaseTokenRequest;
import com.sts.finncub.usermanagement.request.GeoLocationRequest;
import com.sts.finncub.usermanagement.request.RamsonUserRequest;
import com.sts.finncub.usermanagement.request.UserRequest;

import java.util.List;

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

    Response postGeoLocationOfUser(GeoLocationRequest geoLocationRequest, String authorization);

    Response getAllUserSearchable(String searchUserKey, String userType);

    Response updateFirebaseToken(FirebaseTokenRequest firebaseTokenRequest) throws BadRequestException;

    void deleteTokenByUserId(User userDetail);

    List<RamsonUserRequest> getFoForRamson();

    Response getUsersOnBranches(UserFilterRequest request) throws BadRequestException;

    Response updateUserForEmployee(UserRequest request);
}