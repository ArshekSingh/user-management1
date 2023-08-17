package com.sts.finncub.usermanagement.listner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sts.finncub.core.dto.SmsQueueRequest;
import com.sts.finncub.usermanagement.service.SmsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class QueueConsumer {

    private final SmsService smsService;

    @Value("${queue.name}")
    private String queueName;

    @SqsListener(value = queueName)
    public void loadMessageFromSQS(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SmsQueueRequest smsQueueRequest = mapper.readValue(message, SmsQueueRequest.class);
        if (smsQueueRequest != null) {
            log.info("API to send SMS for collection is triggered for mobile {}", smsQueueRequest.getMobileNo());
            smsService.sendSms(smsQueueRequest.getMobileNo(), smsQueueRequest.getUserId(), smsQueueRequest.getOrganizationId(), smsQueueRequest.getMessage());
            log.info("SMS sent successfully of collection for mobile {}", smsQueueRequest.getMobileNo());
        }
    }
}