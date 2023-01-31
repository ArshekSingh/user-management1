package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.entity.MiscellaneousService;
import com.sts.finncub.core.repository.MiscellaneousServiceRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private MiscellaneousServiceRepository miscellaneousServiceRepository;

    @Override
    public Response getCurrentVersion() {
        MiscellaneousService currentVersionDetails = miscellaneousServiceRepository.findByKey("APP_VERSION");
        if (currentVersionDetails != null) {
            return new Response("Current version details", currentVersionDetails, HttpStatus.OK);
        } else {
            return new Response("No App version details found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response updateAppVersion(String value) {
        MiscellaneousService versionDetails = miscellaneousServiceRepository.findByKey("APP_VERSION");
        if (versionDetails != null) {
            versionDetails.setValue(value);
            miscellaneousServiceRepository.save(versionDetails);
            return new Response("App Version updated to " + value, HttpStatus.OK);
        } else {
            return new Response("No App version details found", HttpStatus.BAD_REQUEST);
        }

    }
}
