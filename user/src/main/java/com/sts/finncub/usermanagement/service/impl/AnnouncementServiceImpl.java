package com.sts.finncub.usermanagement.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.repository.*;
import com.sts.finncub.core.request.UserAnnouncementFilterRequest;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.response.UserAnnouncementResponse;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.CommonUtil;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.assembler.UserAnnouncementAssembler;
import com.sts.finncub.usermanagement.service.AnnouncementService;
import com.sts.finncub.usermanagement.service.AwsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final BranchMasterRepository branchMasterRepository;

    private final UserAnnouncementRepository userAnnouncementRepository;

    private final UserAnnouncementBranchMapping userAnnouncementBranchMapping;

    private final UserAnnouncementMappingRepository userAnnouncementMappingRepository;

    private final UserBranchMappingRepository userBranchMappingRepository;

    private final UserCredentialService userCredentialService;

    private final UserAnnouncementAssembler userAnnouncementAssembler;

    private final AwsService awsService;

    private final CommonUtil commonUtil;

    private final ReferenceDetailRepository referenceDetailRepository;

    @Override
    public Response createAnnouncement(UserAnnouncementRequest userAnnouncementRequest) throws FirebaseMessagingException, IOException {
        UserSession userSession = userCredentialService.getUserSession();
        List<UserBranchMapping> userBranchMappingList = userBranchMappingRepository.findByUserBranchMappingPK_OrgIdAndStatusAndUserBranchMappingPK_BranchIdIn(userSession.getOrganizationId(), "A", userAnnouncementRequest.getBranchId());
        if (userBranchMappingList.isEmpty()) {
            log.info("No users are found for branch : {}", userAnnouncementRequest.getBranchId());
            return new Response("No users are found for branch : " + userAnnouncementRequest.getBranchId(), HttpStatus.NOT_FOUND);
        }
        UserAnnouncement userAnnouncement = userAnnouncementAssembler.convertToUserAnnouncement(userAnnouncementRequest, userSession);
        userAnnouncementRepository.saveAndFlush(userAnnouncement);
        userAnnouncementBranchMapping.insertUserAnnouncementBranchMapping(userBranchMappingList, userAnnouncement, userAnnouncement.getAnnouncementId(), userAnnouncementRequest.getBranchId());
        log.info("Announcement created successfully for branches {}", userAnnouncementRequest.getBranchId());
        return new Response("Announcement created successfully", userAnnouncement.getAnnouncementId(), HttpStatus.OK);
    }

    @Override
    public Response getAdminAnnouncements(UserAnnouncementFilterRequest userAnnouncementFilterRequest) {
        List<UserAnnouncement> userAnnouncementList;
        List<UserAnnouncementResponse> userAnnouncementResponseList = new ArrayList<>();
        if (StringUtils.hasText(userAnnouncementFilterRequest.getStartDate()) && StringUtils.hasText(userAnnouncementFilterRequest.getEndDate()))
            userAnnouncementList = userAnnouncementRepository.findByStartDateBetweenOrderByAnnouncementIdDesc(DateTimeUtil.stringToDate(userAnnouncementFilterRequest.getStartDate()), DateTimeUtil.stringToDate(userAnnouncementFilterRequest.getEndDate()));
        else
            userAnnouncementList = userAnnouncementRepository.findAllByOrderByAnnouncementIdDesc();
        if (userAnnouncementList.isEmpty()) {
            log.info("No announcements are present");
            return new Response("No announcements are present", HttpStatus.NOT_FOUND);
        }
        for (UserAnnouncement userAnnouncement : userAnnouncementList) {
            List<Long> branchIds = userAnnouncementMappingRepository.findDistinctBranchIdByAnnouncementId(userAnnouncement.getAnnouncementId().toString());
            List<String> branchNames = branchMasterRepository.findByBranchName(userAnnouncement.getOrgId(), branchIds);
            UserAnnouncementResponse userAnnouncementResponse = userAnnouncementAssembler.convertToResponse(userAnnouncement);
            userAnnouncementResponse.setType(getType(userAnnouncementResponse.getAttachment()));
            userAnnouncementResponse.setAttachment(awsService.signedDocumentUrl(userAnnouncementResponse.getAttachment()));
            userAnnouncementResponse.setBranchId(branchIds);
            userAnnouncementResponse.setBranchName(branchNames);
            userAnnouncementResponseList.add(userAnnouncementResponse);
        }
        log.info("Announcements fetched successfully");
        return new Response("Announcements fetched successfully", userAnnouncementResponseList, (long) userAnnouncementList.size(), HttpStatus.OK);
    }

    @Override
    public Response getAdminAnnouncement(Long announcementId) {
        Optional<UserAnnouncement> userAnnouncement = userAnnouncementRepository.findById(announcementId);
        if (userAnnouncement.isEmpty()) {
            log.info("No announcement is found with id : {}", announcementId);
            return new Response("No announcement is found with id : " + announcementId, HttpStatus.NOT_FOUND);
        }
        UserAnnouncementResponse userAnnouncementResponse = userAnnouncementAssembler.convertToResponse(userAnnouncement.get());
        userAnnouncementResponse.setType(getType(userAnnouncementResponse.getAttachment()));
        userAnnouncementResponse.setAttachment(awsService.signedDocumentUrl(userAnnouncementResponse.getAttachment()));
        List<Long> branchIds = userAnnouncementMappingRepository.findDistinctBranchIdByAnnouncementId(announcementId.toString());
        List<String> branchNames = branchMasterRepository.findByBranchName(userAnnouncementResponse.getOrgId(), branchIds);
        userAnnouncementResponse.setBranchId(branchIds);
        userAnnouncementResponse.setBranchName(branchNames);
        log.info("Announcement fetched successfully with id {}", announcementId);
        return new Response("Announcement fetched successfully", userAnnouncementResponse, HttpStatus.OK);
    }

    private String getType(String attachment) {
        List<ReferenceDetail> rdNotificationType = referenceDetailRepository.findByReferenceDetailPK_ReferenceDomain("RD_NOTIFICATION_TYPE");
        if (CollectionUtils.isEmpty(rdNotificationType)) {
            return "";
        }
        Optional<ReferenceDetail> first = rdNotificationType.stream().filter(o -> o.getReferenceDetailPK().getKeyValue().equalsIgnoreCase(commonUtil.getExtention(attachment))).findFirst();
        if (first.isPresent() && StringUtils.hasText(first.get().getValue1())) {
            return first.get().getValue1();
        }
        return null;
    }

    @Override
    public Response updateAnnouncement(Long announcementId, UserAnnouncementRequest userAnnouncementRequest) {
        UserSession userSession = userCredentialService.getUserSession();
        Optional<UserAnnouncement> userAnnouncementResponse = userAnnouncementRepository.findById(announcementId);
        if (userAnnouncementResponse.isEmpty()) {
            log.info("No announcement is found with id : {}", announcementId);
            return new Response("No announcement is found with id : " + announcementId, HttpStatus.NOT_FOUND);
        }
        UserAnnouncement announcement = userAnnouncementAssembler.prepareUpdateRequest(userAnnouncementResponse.get(), userAnnouncementRequest, userSession);
        userAnnouncementRepository.saveAndFlush(announcement);
        log.info("Announcement with id : {} updated", announcementId);
        return new Response("Announcement with id : " + announcementId + " updated", HttpStatus.OK);
    }

    @Override
    public Response getAnnouncements() {
        UserSession userSession = userCredentialService.getUserSession();
        List<Object[]> announcements = userAnnouncementRepository.getAnnouncements(userSession.getUserId(), userSession.getOrganizationId());
        List<UserAnnouncementResponse> userAnnouncementResponse = announcements.stream().map(this::toResponse).collect(Collectors.toList());
        if (announcements.isEmpty()) {
            log.info("No announcements are found for user : {}", userSession.getUserId());
            return new Response("No announcements are found for user : " + userSession.getUserId(), HttpStatus.NOT_FOUND);
        }
        log.info("Announcements fetched successfully for user : {}", userSession.getUserId());
        return new Response("Announcements fetched successfully for user : " + userSession.getUserId(), userAnnouncementResponse, (long) announcements.size(), HttpStatus.OK);
    }

    private UserAnnouncementResponse toResponse(Object[] announcement) {
        UserAnnouncementResponse userAnnouncementResponse = userAnnouncementAssembler.populateUserAnnouncementResponseData(announcement);
        userAnnouncementResponse.setType(getType(userAnnouncementResponse.getAttachment()));
        userAnnouncementResponse.setAttachment(awsService.signedDocumentUrl(userAnnouncementResponse.getAttachment()));
        return userAnnouncementResponse;
    }

    @Override
    public Response readAnnouncement(Long announcementId,UserAnnouncementRequest request) {
        UserSession userSession = userCredentialService.getUserSession();
        Optional<UserAnnouncementMapping> optional = userAnnouncementMappingRepository.findByOrgIdAndUserIdAndAnnouncementId(userSession.getOrganizationId(), userSession.getUserId(), announcementId);
        if (optional.isEmpty()) {
            log.info("No announcement is found with userId : {} or announcementId : {}", userSession.getUserId(), announcementId);
            return new Response("No announcement is found with userId : " + userSession.getUserId() + " or announcementId : " + announcementId, HttpStatus.NOT_FOUND);
        }
        UserAnnouncementMapping userAnnouncementMapping = optional.get();
        userAnnouncementMapping.setIsRead("Y");
        userAnnouncementMapping.setUpdatedBy(userSession.getUserId());
        userAnnouncementMapping.setReadOn(LocalDate.now());
        userAnnouncementMappingRepository.save(userAnnouncementMapping);
        log.info("Announcement with userId : {} and announcementId : {} is marked as read", userSession.getUserId(), announcementId);
        Map<String, String> map = new HashMap<>();
        map.put("isRead", userAnnouncementMapping.getIsRead());
        return new Response("Announcement with userId : " + userSession.getUserId() + " and announcementId : " + announcementId + " is marked as read", map, HttpStatus.OK);
    }
    
}
