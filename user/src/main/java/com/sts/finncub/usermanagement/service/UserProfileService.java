package com.sts.finncub.usermanagement.service;

import com.sts.finncub.usermanagement.response.UserProfileResponse;
import com.sts.finncub.core.exception.ObjectNotFoundException;

public interface UserProfileService {
    UserProfileResponse getProfile() throws ObjectNotFoundException;
}
