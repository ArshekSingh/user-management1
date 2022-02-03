package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/fetchAllUsers")
    public Response getAllUserDetails(@RequestBody FilterRequest request) throws BadRequestException {
        return userService.getAllUserDetails(request);
    }

    @GetMapping("/getUserDetail/{userId}")
    public Response getUserDetail(@PathVariable String userId) throws BadRequestException {
        return userService.getUserDetail(userId);
    }

    @PostMapping("/add")
    public Response addUser(@RequestBody UserRequest request) throws BadRequestException {
        return userService.addUser(request);
    }

    @PostMapping("/update")
    public Response updateUserDetails(@RequestBody UserRequest request) throws BadRequestException {
        return userService.updateUserDetails(request);
    }

    @GetMapping(value = "/{userSearchableKey}")
    public Response getUserSearchable(@PathVariable String userSearchableKey) {
        return userService.getUserSearchable(userSearchableKey);
    }

    @GetMapping(value = "getUserRoleList/{userId}")
    public Response getUserRoleListAssignedOrAvailable(@PathVariable String userId) {
        return userService.getUserRoleListAssignedOrAvailable(userId);
    }

    @PostMapping(value = "assignRolesToUser")
    public Response assignRolesToUser(@RequestBody UserRoleMappingDto userRoleMappingDto) {
        return userService.assignRolesToUser(userRoleMappingDto);
    }

    @GetMapping(value = "getUserBranchList/{userId}")
    public Response getUserAssignedAndAvailableBranchList(@PathVariable String userId) {
        return userService.getUserAssignedAndAvailableBranchList(userId);
    }

    @PostMapping(value = "assignBranchesToUser")
    public Response assignBranchesToUser(@RequestBody UserBranchMappingDto userRoleMappingDto) {
        return userService.assignBranchesToUser(userRoleMappingDto);
    }
}