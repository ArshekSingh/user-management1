package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface UserService {

    Response getAllUserDetails();

    Response getUserDetail(String userId) throws BadRequestException;

    Response addUser(UserRequest request) throws BadRequestException;

    Response updateUserDetails(UserRequest request) throws BadRequestException;
}
