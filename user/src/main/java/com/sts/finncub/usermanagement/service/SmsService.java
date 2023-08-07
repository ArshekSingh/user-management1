package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.response.Response;

public interface SmsService {
    Response sendSms(String mobile, String userId, Long organizationId, String message);
}