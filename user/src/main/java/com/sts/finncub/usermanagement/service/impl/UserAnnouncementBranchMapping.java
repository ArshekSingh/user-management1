package com.sts.finncub.usermanagement.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.entity.UserAnnouncement;
import com.sts.finncub.core.entity.UserAnnouncementMapping;
import com.sts.finncub.core.entity.UserBranchMapping;
import com.sts.finncub.core.repository.UserAnnouncementMappingRepository;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.service.impl.FirebaseMessagingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserAnnouncementBranchMapping {

    private final FirebaseMessagingService firebaseMessagingService;

    private final UserAnnouncementMappingRepository userAnnouncementMappingRepository;

    private final UserRepository userRepository;

    @Async
    public void insertUserAnnouncementBranchMapping(List<UserBranchMapping> userBranchMappingList, UserAnnouncement userAnnouncement, Long announcementId, List<Long> branchId) throws FirebaseMessagingException {
        log.info("Going to create user notification for branches {}", branchId);
        for (UserBranchMapping userBranchMapping : userBranchMappingList) {
            UserAnnouncementMapping userAnnouncementMapping = new UserAnnouncementMapping();
            userAnnouncementMapping.setAnnouncementId(announcementId);
            userAnnouncementMapping.setUserId(userBranchMapping.getUser().getUserId());
            userAnnouncementMapping.setIsRead("N");
            userAnnouncementMapping.setBranchId(userBranchMapping.getUserBranchMappingPK().getBranchId());
            userAnnouncementMapping.setInsertedBy(userAnnouncement.getInsertedBy());
            userAnnouncementMapping.setOrgId(userAnnouncement.getOrgId());
            userAnnouncementMappingRepository.saveAndFlush(userAnnouncementMapping);
            Optional<User> user = userRepository.findByUserId(userBranchMapping.getUser().getUserId());
            if (user.isPresent() && StringUtils.hasText(user.get().getFbToken())) {
                firebaseMessagingService.sendNotification(userAnnouncement.getTitle(), userAnnouncement.getMessage(), user.get().getFbToken(), userAnnouncement.getAttachment());
            }
        }
    }
}
