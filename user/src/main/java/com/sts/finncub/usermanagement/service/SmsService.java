package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.response.Response;

import java.math.BigDecimal;

public interface SmsService {
    Response sendSms(String mobile, String messageType, Long centerId, BigDecimal amount, String userId, Long organizationId);
}