package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.response.Response;

public interface AppVersionService {

    Response getCurrentVersion();

    Response updateAppVersion(String value);

}
