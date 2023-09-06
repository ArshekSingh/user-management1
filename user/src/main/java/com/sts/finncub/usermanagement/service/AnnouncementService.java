package com.sts.finncub.usermanagement.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.request.UserAnnouncementFilterRequest;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.Response;

import java.io.IOException;

public interface AnnouncementService {

    Response createAnnouncement(UserAnnouncementRequest userAnnouncementRequest) throws FirebaseMessagingException, IOException;

    Response getAdminAnnouncements(UserAnnouncementFilterRequest userAnnouncementFilterRequest);

    Response getAdminAnnouncement(Long AnnouncementId);

    Response updateAnnouncement(Long announcementId, UserAnnouncementRequest userAnnouncementRequest);

    Response getAnnouncements();

    Response readAnnouncement(String announcementId);

}
