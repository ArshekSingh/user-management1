package com.sts.finncub.usermanagement.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.entity.UserAnnouncement;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.Response;

public interface AnnouncementService {

    Response createAnnouncement(UserAnnouncement userAnnouncement) throws ObjectNotFoundException, FirebaseMessagingException;

    Response getAdminAnnouncements(UserAnnouncementRequest userAnnouncementRequest);

    Response getAdminAnnouncement(Long AnnouncementId) throws ObjectNotFoundException;

    Response updateAnnouncement(Long announcementId, UserAnnouncement userAnnouncement) throws ObjectNotFoundException;

    Response getAnnouncements() throws ObjectNotFoundException;

    Response readAnnouncement(String announcementId) throws ObjectNotFoundException;

}
