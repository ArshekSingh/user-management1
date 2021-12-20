package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.Response;
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

    @GetMapping("/fetchAllUsers")
    public Response getAllUserDetails() throws BadRequestException {
        return userService.getAllUserDetails();
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

}
