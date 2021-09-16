package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.usermanagement.assembler.UserProfileConverter;
import com.sts.finncub.usermanagement.entity.User;
import com.sts.finncub.usermanagement.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.repository.UserRepository;
import com.sts.finncub.usermanagement.response.UserProfileResponse;
import com.sts.finncub.usermanagement.service.UserCredentialService;
import com.sts.finncub.usermanagement.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;

    UserProfileServiceImpl(UserRepository userRepository,UserCredentialService userCredentialService){
        this.userRepository = userRepository;
        this.userCredentialService = userCredentialService;
    }

    @Override
    public UserProfileResponse getProfile() throws ObjectNotFoundException {
        String userId = userCredentialService.getUserData().getUserId();
        log.info("Fetching user profile for Id -"+userId);
        User user = userRepository.findByUserId(userId)
                                    .orElseThrow(()-> new ObjectNotFoundException("No user found for Id -"+userId, HttpStatus.NOT_FOUND));
        log.info("User details found");
        return UserProfileConverter.convertToProfile(user);
    }
}
