package com.sts.finncub.usermanagement.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.UserAnnouncementMappingRepository;
import com.sts.finncub.core.repository.UserAnnouncementRepository;
import com.sts.finncub.core.repository.UserBranchMappingRepository;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.service.AnnouncementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final UserAnnouncementRepository userAnnouncementRepository;

    private final UserAnnouncementBranchMapping userAnnouncementBranchMapping;

    private final UserAnnouncementMappingRepository userAnnouncementMappingRepository;

    private final UserBranchMappingRepository userBranchMappingRepository;

    private final UserCredentialService userCredentialService;

    @Override
    public Response createAnnouncement(UserAnnouncement userAnnouncement) throws ObjectNotFoundException, FirebaseMessagingException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_OrgIdAndStatusAndUserBranchMappingPK_BranchIdIn(userSession.getOrganizationId(), "A", userAnnouncement.getBranch());
        if (userBranchMappingList.isEmpty()) {
            log.info("No users are found for branch : {}", userAnnouncement.getBranch());
            throw new ObjectNotFoundException("No users are found for branch : " + userAnnouncement.getBranch(), HttpStatus.NOT_FOUND);
        }
        userAnnouncement.setOrgId(userSession.getOrganizationId());
        userAnnouncement.setInsertedBy(userSession.getUserId());
        UserAnnouncement userAnnouncementResponse = userAnnouncementRepository.saveAndFlush(userAnnouncement);
        userAnnouncementBranchMapping.insertUserAnnouncementBranchMapping(userBranchMappingList, userAnnouncement, userAnnouncementResponse.getAnnouncementId().toString());
        log.info("Announcement created successfully");
        response.setMessage("Announcement created successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        return response;
    }


    @Override
    public Response getAdminAnnouncements(UserAnnouncementRequest userAnnouncementRequest) {
        Response response = new Response();
        List<UserAnnouncement> userAnnouncementList;
        if (userAnnouncementRequest.getStartDate() != null && userAnnouncementRequest.getEndDate() != null) {
            userAnnouncementList = userAnnouncementRepository.findByStartDateAndEndDate(userAnnouncementRequest.getStartDate(), userAnnouncementRequest.getEndDate());
        } else {
            userAnnouncementList = userAnnouncementRepository.findAll();
        }
        for (UserAnnouncement userAnnouncement : userAnnouncementList) {
            List<Long> userAnnouncementMappingList = userAnnouncementMappingRepository.findDistinctBranchIdByAnnouncementId(userAnnouncement.getAnnouncementId().toString());
            userAnnouncement.setBranch(userAnnouncementMappingList);
        }
        log.info("Announcements fetched successfully");
        response.setMessage("Announcements fetched successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userAnnouncementList);
        response.setTotalCount((long) userAnnouncementList.size());
        return response;
    }

    @Override
    public Response getAdminAnnouncement(Long announcementId) throws ObjectNotFoundException {
        Response response = new Response();
        Optional<UserAnnouncement> userAnnouncementResponse = userAnnouncementRepository.findById(announcementId);
        if (userAnnouncementResponse.isEmpty()) {
            log.info("No announcement is found with id : {}", announcementId);
            throw new ObjectNotFoundException("No announcement is found with id : " + announcementId, HttpStatus.NOT_FOUND);
        }
        UserAnnouncement userAnnouncement = userAnnouncementResponse.get();
        List<Long> userAnnouncementMappingList = userAnnouncementMappingRepository.findDistinctBranchIdByAnnouncementId(announcementId.toString());
        userAnnouncement.setBranch(userAnnouncementMappingList);
        log.info("Announcement fetched successfully");
        response.setMessage("Announcement fetched successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(userAnnouncement);
        return response;
    }

    @Override
    public Response updateAnnouncement(Long announcementId, UserAnnouncement userAnnouncement) throws ObjectNotFoundException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        Optional<UserAnnouncement> userAnnouncementResponse = userAnnouncementRepository.findById(announcementId);
        if (userAnnouncementResponse.isEmpty()) {
            log.info("No announcement is found with id : {}", announcementId);
            throw new ObjectNotFoundException("No announcement is found with id : " + announcementId, HttpStatus.NOT_FOUND);
        }
        UserAnnouncement announcement = userAnnouncementResponse.get();
        if (userAnnouncement.getTitle() != null)
            announcement.setTitle(userAnnouncement.getTitle());
        if (userAnnouncement.getMessage() != null)
            announcement.setMessage(userAnnouncement.getMessage());
        if (userAnnouncement.getAttachment() != null)
            announcement.setAttachment(userAnnouncement.getAttachment());
        if (userAnnouncement.getStatus() != null)
            announcement.setStatus(userAnnouncement.getStatus());
        if (userAnnouncement.getStartDate() != null)
            announcement.setStartDate(userAnnouncement.getStartDate());
        if (userAnnouncement.getEndDate() != null)
            announcement.setEndDate(userAnnouncement.getEndDate());
        announcement.setUpdatedBy(userSession.getUserId());
        userAnnouncementRepository.saveAndFlush(announcement);
        log.info("Announcement with id : {} updated", announcementId);
        response.setMessage("Announcement with id : " + announcementId + " updated");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        return response;
    }

    @Override
    public Response getAnnouncements() throws ObjectNotFoundException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<UserAnnouncement> announcements = userAnnouncementRepository.getAnnouncements(userSession.getUserId(), userSession.getOrganizationId());
        if (announcements.isEmpty()) {
            log.info("No announcements are found for user : {}", userSession.getUserId());
            throw new ObjectNotFoundException("No announcements are found for user : " + userSession.getUserId(), HttpStatus.NOT_FOUND);
        }
        log.info("Announcements fetched successfully for user : {}", userSession.getUserId());
        response.setMessage("Announcements fetched successfully for user : " + userSession.getUserId());
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(announcements);
        response.setTotalCount((long) announcements.size());
        return response;
    }

    @Override
    public Response readAnnouncement(String announcementId) throws ObjectNotFoundException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        UserAnnouncementMapping userAnnouncementMappingResponse = null;
        List<UserAnnouncementMapping> userAnnouncementMappingList = userAnnouncementMappingRepository.findByOrgIdAndUserIdAndAnnouncementId(userSession.getOrganizationId(), userSession.getUserId(), announcementId);
        if (userAnnouncementMappingList.isEmpty()) {
            log.info("No announcement is found with userId : {} or announcementId : {}", userSession.getUserId(), announcementId);
            throw new ObjectNotFoundException("No announcement is found with userId : " + userSession.getUserId() + " or announcementId : " + announcementId, HttpStatus.NOT_FOUND);
        }
        for (UserAnnouncementMapping userAnnouncementMapping : userAnnouncementMappingList) {
            userAnnouncementMapping.setIsRead("Y");
            userAnnouncementMapping.setUpdatedBy(userSession.getUserId());
            userAnnouncementMapping.setReadOn(LocalDate.now());
            userAnnouncementMappingResponse = userAnnouncementMappingRepository.saveAndFlush(userAnnouncementMapping);
        }
        log.info("Announcement with userId : {} and announcementId : {} is marked as read", userSession.getUserId(), announcementId);
        response.setMessage("Announcement with userId : " + userSession.getUserId() + " and announcementId : " + announcementId + " is marked as read");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        Map<String, String> map = new HashMap<>();
        map.put("isRead", userAnnouncementMappingResponse.getIsRead());
        response.setData(map);
        return response;
    }

}
