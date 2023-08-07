package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.entity.VendorSmsLog;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.util.SmsUtil;
import com.sts.finncub.usermanagement.service.SmsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class SmsServiceImpl implements SmsService, Constant {
    private final PincodeRepository pincodeRepository;

    private final VillageMasterRepository villageMasterRepository;

    private final CenterMasterRepository centerMasterRepository;

    private final SmsUtil smsUniversalUtil;

    private final VendorSmsLogRepository vendorSmsLogRepository;

    private final StateRepository stateRepository;

    @Override
    public Response sendSms(String mobileNumber, String userId, Long organizationId, String message) {
        //insert message data in database
        log.info("Insert values in database for record of mobile {}", mobileNumber);
        VendorSmsLog vendorSmsLogData = new VendorSmsLog();
        vendorSmsLogData.setOrgId(organizationId);
        vendorSmsLogData.setSmsMobile(mobileNumber);
        vendorSmsLogData.setSmsText(message);
        vendorSmsLogData.setSmsType("U"); // U is for UNICODE type
        vendorSmsLogData.setStatus("S"); // S is for SENT status
        vendorSmsLogData.setSmsVendor("UNIVERSAL SMS");
        vendorSmsLogData.setInsertedBy(userId);
        vendorSmsLogData.setInsertedOn(LocalDateTime.now());
        vendorSmsLogData = vendorSmsLogRepository.save(vendorSmsLogData);
        log.info("Values inserted successfully in database for record of mobile {}", mobileNumber);
        // hit sms API
        log.info("Universal SMS API invoked {}", mobileNumber);
        String responseId = smsUniversalUtil.sendSms(mobileNumber, message);
        // check status code and update response id in VendorSmsLog returned from API
        if (StringUtils.hasText(responseId)) {
            vendorSmsLogData.setStatus("D");
            vendorSmsLogData.setSmsResponse(responseId);
            vendorSmsLogRepository.save(vendorSmsLogData);
        } else {
            return new Response("Please try after sometime", HttpStatus.BAD_GATEWAY);
        }
        return new Response("Message sent successfully.", HttpStatus.OK);
    }
}
