package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.constants.Constant;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.core.util.SmsUtil;
import com.sts.finncub.usermanagement.service.SmsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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
    public Response sendSms(String mobileNumber, String messageType, Long centerId, BigDecimal amount, String userId, Long organizationId) {
        String message = "";
        CenterMaster centerMaster = centerMasterRepository.findByCenterMasterPK_OrgIdAndCenterMasterPK_CenterId(organizationId, centerId);
        if (centerMaster != null) {
            VillageMaster villageMaster = villageMasterRepository.findByVillageId(centerMaster.getVillageId());
            if (villageMaster != null) {
                Optional<PincodeMaster> pincodeMasterOptional = pincodeRepository.findByPincodeMasterPK_Pincode(villageMaster.getPincode());
                if (pincodeMasterOptional.isPresent()) {
                    PincodeMaster pincodeMaster = pincodeMasterOptional.get();
                    Optional<StateMaster> stateMasterOptional = stateRepository.findByStateId(pincodeMaster.getStateId());
                    if (stateMasterOptional.isPresent()) {
                        StateMaster stateMaster = stateMasterOptional.get();
                        Integer stateId = stateMaster.getStateId();
                        if ("A".equalsIgnoreCase(messageType)) {
                            if (19 == stateId) {
                                message = SER_DOC_APPROVE_BEN;
                            } else {
                                message = SER_DOC_APPROVE_HIN;
                            }
                        }
                        if ("COLL".equalsIgnoreCase(messageType)) {
                            if (19 == stateId) {
                                message = SER_COLLECTION_BEN;
                            } else {
                                message = SER_COLLECTION_HIN;
                            }
                        }
                    }
                }
            }
            message = message.replace("{date}", DateTimeUtil.dateToString(LocalDate.now()));
            message = message.replace("{amount}", "Rs." + amount.toString());
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
            Response response = smsUniversalUtil.sendSms(mobileNumber, message);
            // check status code and update response id in VendorSmsLog returned from API
            if (response.getCode() == 200) {
                if (Objects.equals("2019", response.getData())) {
                    log.info("Invalid user name or password!");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("API response error", HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (Objects.equals("2020", response.getData())) {
                    log.info("Message is blank!");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("API response error", HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (Objects.equals("2021", response.getData())) {
                    log.info("Sender ID is blank!");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("API response error", HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (Objects.equals("2022", response.getData())) {
                    log.info("Mobile number is blank!");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("Mobile number is blank!", HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (Objects.equals("2023", response.getData())) {
                    log.info("Low balance or credit!");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("API response error", HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    log.info("SMS sent successfully.");
                    vendorSmsLogData.setStatus("D"); // D is for DELIVERED status
                    vendorSmsLogData.setSmsResponse(String.valueOf(response.getCode()));
                    vendorSmsLogData.setUpdatedBy(userId);
                    vendorSmsLogData.setUpdatedOn(LocalDateTime.now());
                    vendorSmsLogRepository.save(vendorSmsLogData);
                    return new Response("SMS sent successfully.", HttpStatus.OK);
                }
            } else {
                log.info("Please try after sometime - {}", HttpStatus.BAD_GATEWAY);
                return new Response("Please try after sometime", HttpStatus.BAD_GATEWAY);
            }
        }
        return new Response("No center is found", HttpStatus.BAD_REQUEST);
    }
}