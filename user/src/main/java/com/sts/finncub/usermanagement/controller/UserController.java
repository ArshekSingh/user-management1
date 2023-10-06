package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.request.UserFilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.FirebaseTokenRequest;
import com.sts.finncub.usermanagement.request.GeoLocationRequest;
import com.sts.finncub.usermanagement.request.RamsonUserRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/fetchAllUsers")
    public Response getAllUserDetailsByFilter(@RequestBody FilterRequest request) throws BadRequestException {
        log.info("fetchAllUsers() invoked");
        return userService.getAllUserDetailsByFilterRequest(request);
    }

    @GetMapping("/getUserDetail/{userId}")
    public Response getUserDetail(@PathVariable String userId) throws BadRequestException {
        log.info("getUserDetail() invoked , userId : {}", userId);
        return userService.getUserDetail(userId);
    }

    @PostMapping("/add")
    public Response addUser(@RequestBody UserRequest request) throws BadRequestException {
        log.info("addUser() invoked , userId : {} , type : {}", request.getUserId(), request.getType());
        return userService.addUser(request);
    }

    @PostMapping("/update")
    public Response updateUserDetails(@RequestBody UserRequest request) throws BadRequestException {
        log.info("updateUserDetails() invoked , userId : {} , type : {}", request.getUserId(), request.getType());
        return userService.updateUserDetails(request);
    }

    @GetMapping(value = "/{userSearchableKey}/{userType}")
    public Response getUserSearchable(@PathVariable String userSearchableKey, @PathVariable String userType) {
        log.info("getUserSearchable() invoked , userSearchableKey : {} , userType : {}", userSearchableKey, userType);
        return userService.getUserSearchable(userSearchableKey, userType);
    }

    @GetMapping(value = "getUserRoleList/{userId}")
    public Response getUserRoleListAssignedOrAvailable(@PathVariable String userId) {
        log.info("getUserRoleListAssignedOrAvailable() invoked , userId : {}", userId);
        return userService.getUserRoleListAssignedOrAvailable(userId);
    }

    @PostMapping(value = "/assignRolesToUser")
    public Response assignRolesToUser(@RequestBody UserRoleMappingDto userRoleMappingDto) {
        log.info("assignRolesToUser() invoked , userId : {}", userRoleMappingDto.getUserId());
        return userService.assignRolesToUser(userRoleMappingDto);
    }

    @GetMapping(value = "/getUserBranchList/{userId}")
    public Response getUserAssignedAndAvailableBranchList(@PathVariable String userId) {
        log.info("getUserAssignedAndAvailableBranchList() invoked , userId : {}", userId);
        return userService.getUserAssignedAndAvailableBranchList(userId);
    }

    @PostMapping(value = "/assignBranchesToUser")
    public Response assignBranchesToUser(@RequestBody UserBranchMappingDto userRoleMappingDto) {
        log.info("assignBranchesToUser() invoked , userId : {}", userRoleMappingDto.getUserId());
        return userService.assignBranchesToUser(userRoleMappingDto);
    }

    @PostMapping(value = "/postGeoLocationOfUser")
    public Response postGeoLocationOfUser(@RequestBody GeoLocationRequest geoLocationRequest, @RequestHeader String authorization) {
        log.info("postGeoLocationOfUser() invoked");
        return userService.postGeoLocationOfUser(geoLocationRequest, authorization);
    }

    @GetMapping(value = "/getAllUserSearchable/{searchUserKey}/{userType}")
    public Response getAllUserSearchable(@PathVariable String searchUserKey, @PathVariable String userType) {
        log.info("getAllUserSearchable() invoked , searchUserKey : {} , userType : {}", searchUserKey, userType);
        return userService.getAllUserSearchable(searchUserKey, userType);
    }

    @PostMapping(value = "/postFirebaseToken")
    public Response updateFirebaseToken(@RequestBody FirebaseTokenRequest firebaseTokenRequest) throws BadRequestException {
        log.info("Updating firebase token for userId: {}", firebaseTokenRequest.getToken());
        return userService.updateFirebaseToken(firebaseTokenRequest);
    }

    @GetMapping("/getFoForRamson")
    private List<RamsonUserRequest> getFoForRamson() {
        return userService.getFoForRamson();
    }

    /**
     * THIS API IS USED TO LIST BRANCH MAPPED USER FROM NOTIFICATION SCREEN
     */
    @PostMapping("/getUsersOnBranches")
    private ResponseEntity<Response> getUsersOnBranches(@RequestBody UserFilterRequest request) throws BadRequestException {
        Response response = userService.getUsersOnBranches(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}