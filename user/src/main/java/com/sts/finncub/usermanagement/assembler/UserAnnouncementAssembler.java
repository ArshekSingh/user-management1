package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.entity.UserAnnouncement;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.UserAnnouncementResponse;
import com.sts.finncub.core.util.DateTimeUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserAnnouncementAssembler {

    public UserAnnouncementResponse convertToResponse(UserAnnouncement userAnnouncement) {
        UserAnnouncementResponse userAnnouncementResponse = new UserAnnouncementResponse();
        userAnnouncementResponse.setAnnouncementId(userAnnouncement.getAnnouncementId());
        userAnnouncementResponse.setOrgId(userAnnouncement.getOrgId());
        userAnnouncementResponse.setTitle(userAnnouncement.getTitle());
        userAnnouncementResponse.setMessage(userAnnouncement.getMessage());
        userAnnouncementResponse.setStatus(userAnnouncement.getStatus());
        userAnnouncementResponse.setAttachment(userAnnouncement.getAttachment());
        userAnnouncementResponse.setStartDate(DateTimeUtil.dateToString(userAnnouncement.getStartDate()));
        userAnnouncementResponse.setEndDate(DateTimeUtil.dateToString(userAnnouncement.getEndDate()));
        userAnnouncementResponse.setInsertedBy(userAnnouncement.getInsertedBy());
        userAnnouncementResponse.setInsertedOn(userAnnouncement.getInsertedOn());
        userAnnouncementResponse.setUpdatedBy(userAnnouncement.getUpdatedBy());
        userAnnouncementResponse.setUpdatedOn(userAnnouncement.getUpdatedOn());
        return userAnnouncementResponse;
    }

    public UserAnnouncement convertToUserAnnouncement(UserAnnouncementRequest userAnnouncementRequest, UserSession userSession) {
        UserAnnouncement userAnnouncement = new UserAnnouncement();
        userAnnouncement.setTitle(userAnnouncementRequest.getTitle());
        userAnnouncement.setMessage(userAnnouncementRequest.getMessage());
        userAnnouncement.setStatus(userAnnouncementRequest.getStatus());
        userAnnouncement.setAttachment(userAnnouncementRequest.getAttachment());
        userAnnouncement.setStartDate(DateTimeUtil.stringToDate(userAnnouncementRequest.getStartDate()));
        userAnnouncement.setEndDate(DateTimeUtil.stringToDate(userAnnouncementRequest.getEndDate()));
        userAnnouncement.setOrgId(userSession.getOrganizationId());
        userAnnouncement.setInsertedBy(userSession.getUserId());
        return userAnnouncement;
    }

    public UserAnnouncement prepareUpdateRequest(UserAnnouncement userAnnouncement, UserAnnouncementRequest userAnnouncementRequest, UserSession userSession) {
        UserAnnouncement announcement = userAnnouncement;
        if (StringUtils.hasText(userAnnouncementRequest.getTitle()))
            announcement.setTitle(userAnnouncementRequest.getTitle());
        if (StringUtils.hasText(userAnnouncementRequest.getMessage()))
            announcement.setMessage(userAnnouncementRequest.getMessage());
        if (StringUtils.hasText(userAnnouncementRequest.getAttachment()))
            announcement.setAttachment(userAnnouncementRequest.getAttachment());
        if (StringUtils.hasText(userAnnouncementRequest.getStatus()))
            announcement.setStatus(userAnnouncementRequest.getStatus());
        if (StringUtils.hasText(userAnnouncementRequest.getStartDate()))
            announcement.setStartDate(DateTimeUtil.stringToDate(userAnnouncementRequest.getStartDate()));
        if (StringUtils.hasText(userAnnouncementRequest.getEndDate()))
            announcement.setEndDate(DateTimeUtil.stringToDate(userAnnouncementRequest.getEndDate()));
        announcement.setUpdatedBy(userSession.getUserId());
        return announcement;
    }

}
