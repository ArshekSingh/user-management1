package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.assembler.UserProfileConverter;
import com.sts.finncub.usermanagement.response.UserProfileResponse;
import com.sts.finncub.usermanagement.service.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;

    @Override
    public UserProfileResponse getProfile() throws ObjectNotFoundException {
        String userId = userCredentialService.getUserSession().getUserId();
        log.info("Fetching user profile for Id -" + userId);
        User user = userRepository.findByUserIdIgnoreCase(userId).orElseThrow(() -> new ObjectNotFoundException("No user found for Id -" + userId, HttpStatus.NOT_FOUND));
        log.info("User details found for userId {}", user.getUserId());
        return UserProfileConverter.convertToProfile(user);
    }
}