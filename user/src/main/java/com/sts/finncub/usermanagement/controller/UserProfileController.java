package com.sts.finncub.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.response.UserProfileResponse;
import com.sts.finncub.usermanagement.service.UserProfileService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(value = "api")
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("profile")
	public ResponseEntity<Response> getProfile() throws ObjectNotFoundException {
        log.info("Request to fetch user profile received");
        UserProfileResponse response = userProfileService.getProfile();
		return ResponseEntity.ok(new Response(RestMappingConstants.SUCCESS, response, HttpStatus.OK));
    }
}