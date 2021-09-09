package com.sts.fincub.usermanagement.service;

import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getProfile() throws ObjectNotFoundException;
}
