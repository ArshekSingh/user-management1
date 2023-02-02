package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.entity.MiscellaneousService;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.MiscellaneousServiceRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.util.ValidationUtils;
import com.sts.finncub.usermanagement.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private MiscellaneousServiceRepository miscellaneousServiceRepository;

    @Override
    public Response getCurrentVersion(String key) {
        MiscellaneousService currentVersionDetails = miscellaneousServiceRepository.findByKey(key);
        if (currentVersionDetails != null) {
            return new Response("Current version details", currentVersionDetails, HttpStatus.OK);
        } else {
            return new Response("No details found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateAppVersion(String key, String value) throws BadRequestException {
        ValidationUtils.validateValue(value);
        MiscellaneousService versionDetails = miscellaneousServiceRepository.findByKey(key);
        if (versionDetails != null) {
            versionDetails.setValue(value);
            miscellaneousServiceRepository.save(versionDetails);
            return new Response("Value updated to " + value, HttpStatus.OK);
        } else {
            return new Response("No details found", HttpStatus.BAD_REQUEST);
        }
    }
}
