package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;

public interface AppVersionService {

    Response getCurrentVersion(String key);

    Response updateAppVersion(String key, String value) throws BadRequestException;

}
