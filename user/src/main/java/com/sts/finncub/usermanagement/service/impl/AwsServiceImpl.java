package com.sts.finncub.usermanagement.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sts.finncub.usermanagement.config.CloudProperties;
import com.sts.finncub.usermanagement.service.AwsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class AwsServiceImpl implements AwsService {

    private final AmazonS3 amazonS3;

    private final CloudProperties cloudProperties;

    /**
     * THIS METHOD IS USED TO GENERATE SIGNED URL TO DISPLAY IMAGE ON FRONT-END
     */

    @Override
    public String signedDocumentUrl(String fileName) {
        try {
            if (!amazonS3.doesObjectExist(cloudProperties.getBucketName(), fileName)) {
                log.info("File doesn't exist on specified path {}", fileName);
                return "";
            }
            log.info("Generating signed URL for file {}", fileName);
            return generateSignedUrl(fileName, HttpMethod.GET);
        } catch (Exception exception) {
            log.info("Exception occurred while generating signed URL for document {}", fileName);
            return null;
        }
    }

    private String generateSignedUrl(String filePath, HttpMethod httpMethod) {
        if (StringUtils.hasText(filePath)) {
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + 1000 * 60 * cloudProperties.getSignedUrlExpiryTime());
            return amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(cloudProperties.getBucketName(), filePath).withMethod(httpMethod).withExpiration(expiration)).toString();
        }
        return "";
    }
}
