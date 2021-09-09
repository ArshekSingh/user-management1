package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.usermanagement.assembler.UserProfileConverter;
import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.repository.UserRepository;
import com.sts.fincub.usermanagement.response.UserProfileResponse;
import com.sts.fincub.usermanagement.service.UserCredentialService;
import com.sts.fincub.usermanagement.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
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
        User user = userRepository.findByUserId(userId)
                                    .orElseThrow(()-> new ObjectNotFoundException("No user found for Id -"+userId, HttpStatus.NOT_FOUND));

        return UserProfileConverter.convertToProfile(user);
    }
}
