package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.response.Response;

import java.util.Map;

public interface MiscellService {
    Response getMiscellaneousNames();

    Response updateMiscellaneousNames(String key, String value);

    Response getKeyValue(String key);
}
