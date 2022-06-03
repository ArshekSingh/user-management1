package com.sts.finncub.usermanagement.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sts.finncub.core.dto.UserBranchMappingDto;
import com.sts.finncub.core.dto.UserRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.GeoLocationRequest;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "api")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

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

	@PostMapping(value = "assignRolesToUser")
	public Response assignRolesToUser(@RequestBody UserRoleMappingDto userRoleMappingDto) {
		log.info("assignRolesToUser() invoked , userId : {}", userRoleMappingDto.getUserId());
		return userService.assignRolesToUser(userRoleMappingDto);
	}

	@GetMapping(value = "getUserBranchList/{userId}")
	public Response getUserAssignedAndAvailableBranchList(@PathVariable String userId) {
		log.info("getUserAssignedAndAvailableBranchList() invoked , userId : {}", userId);
		return userService.getUserAssignedAndAvailableBranchList(userId);
	}

	@PostMapping(value = "assignBranchesToUser")
	public Response assignBranchesToUser(@RequestBody UserBranchMappingDto userRoleMappingDto) {
		log.info("assignBranchesToUser() invoked , userId : {}", userRoleMappingDto.getUserId());
		return userService.assignBranchesToUser(userRoleMappingDto);
	}

	@PostMapping(value = "postGeoLocationOfUser")
	public Response postGeoLocationOfUser(@Valid @RequestBody GeoLocationRequest geoLocationRequest,
			@RequestHeader String authorization) {
		log.info("postGeoLocationOfUser() invoked");
		return userService.postGeoLocationOfUser(geoLocationRequest, authorization);
	}

	@GetMapping(value = "/getAllUserSearchable/{searchUserKey}/{userType}")
	public Response getAllUserSearchable(@PathVariable String searchUserKey, @PathVariable String userType) {
		log.info("getAllUserSearchable() invoked , searchUserKey : {} , userType : {}", searchUserKey, userType);
		return userService.getAllUserSearchable(searchUserKey, userType);
	}
}