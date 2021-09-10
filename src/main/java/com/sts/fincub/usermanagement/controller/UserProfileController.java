package com.sts.fincub.usermanagement.controller;

import com.sts.fincub.usermanagement.constants.RestMappingConstants;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.response.MenuResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.response.UserProfileResponse;
import com.sts.fincub.usermanagement.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api")
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    @GetMapping("profile")
    public ResponseEntity<Response<UserProfileResponse>> getProfile() throws ObjectNotFoundException {
        log.info("Request to fetch user profile recived");
        UserProfileResponse response = userProfileService.getProfile();
        return  ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS,response, HttpStatus.OK));
    }
}
