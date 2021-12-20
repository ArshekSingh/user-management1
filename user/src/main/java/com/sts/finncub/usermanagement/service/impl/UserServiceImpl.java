package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.UserDetailDto;
import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.UserRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserCredentialService userCredentialService) {
        this.userRepository = userRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public Response getAllUserDetails() {
        Response response = new Response();
        List<UserDetailDto> userDetailDtos = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                UserDetailDto userDetailDto = new UserDetailDto();
                BeanUtils.copyProperties(user, userDetailDto);
                userDetailDto.setPasswordResetDate(DateTimeUtil.dateToString(user.getPasswordResetDate()));
                userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.getDisabledOn()));
                userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.getApprovedOn()));
                userDetailDto.setInsertedOn(DateTimeUtil.dateToString(user.getInsertedOn()));
                userDetailDto.setUpdatedOn(DateTimeUtil.dateToString(user.getUpdatedOn()));
                userDetailDtos.add(userDetailDto);
            }
            response.setCode(HttpStatus.OK.value());
            response.setStatus(HttpStatus.OK);
            response.setData(userDetailDtos);
            response.setMessage("Transaction completed successfully.");
        }
        return response;
    }

    @Override
    public Response getUserDetail(String userId) throws BadRequestException {
        Response response = new Response();
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        UserDetailDto userDetailDto = new UserDetailDto();

        BeanUtils.copyProperties(user.get(), userDetailDto);
        userDetailDto.setDisabledOn(DateTimeUtil.dateToString(user.get().getDisabledOn()));
        userDetailDto.setApprovedOn(DateTimeUtil.dateToString(user.get().getApprovedOn()));
        userDetailDto.setInsertedOn(DateTimeUtil.dateToString(user.get().getInsertedOn()));
        userDetailDto.setUpdatedOn(DateTimeUtil.dateToString(user.get().getUpdatedOn()));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userDetailDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response addUser(UserRequest request) throws BadRequestException {
        Response response = new Response();
        validateRequest(request);
        User user = new User();
        user.setPasswordResetDate(DateTimeUtil.stringToDate(request.getPasswordResetDate()));
        user.setDisabledOn(DateTimeUtil.stringToDate(request.getDisabledOn()));
        user.setApprovedOn(DateTimeUtil.stringToDate(request.getApprovedOn()));
        user.setInsertedOn(DateTimeUtil.stringToDate(request.getInsertedOn()));
        user.setInsertedBy(userCredentialService.getUserSession().getUserId());
        BeanUtils.copyProperties(request, user);
        userRepository.save(user);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    private void validateRequest(UserRequest request) throws BadRequestException {
        if (request == null || !StringUtils.hasText(request.getName())
                || !StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getIsFrozenBookFlag())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateUserDetails(UserRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (!StringUtils.hasText(request.getUserId())) {
            throw new BadRequestException("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(request.getUserId());
        if (user.get() == null) {
            throw new BadRequestException("Data Not Found", HttpStatus.BAD_REQUEST);
        }
        User userDetail = user.get();
        userDetail.setPasswordResetDate(DateTimeUtil.stringToDate(request.getPasswordResetDate()));
        userDetail.setDisabledOn(DateTimeUtil.stringToDate(request.getDisabledOn()));
        userDetail.setApprovedOn(DateTimeUtil.stringToDate(request.getApprovedOn()));
        userDetail.setUpdatedBy(userSession.getUserId());
        userDetail.setUpdatedOn(LocalDate.now());
        BeanUtils.copyProperties(request, userDetail);
        userRepository.save(userDetail);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}
