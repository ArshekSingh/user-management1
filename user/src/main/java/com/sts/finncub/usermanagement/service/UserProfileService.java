package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getProfile() throws ObjectNotFoundException;
}
