package com.sts.finncub.usermanagement.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.dto.BranchEmployeeDto;
import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.entity.UserAnnouncement;
import com.sts.finncub.core.entity.UserAnnouncementMapping;
import com.sts.finncub.core.repository.UserAnnouncementMappingRepository;
import com.sts.finncub.core.repository.UserRepository;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.service.impl.FirebaseMessagingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserAnnouncementBranchMappingServiceImpl {

    private final UserRepository userRepository;

    private final FirebaseMessagingService firebaseMessagingService;

    private final UserAnnouncementMappingRepository userAnnouncementMappingRepository;

    @Async
    public void insertUserAnnouncementBranchMapping(UserAnnouncement userAnnouncement, List<BranchEmployeeDto> userBranchMappingList, UserAnnouncementRequest userAnnouncementRequest, List<User> users) throws FirebaseMessagingException {
        if (!CollectionUtils.isEmpty(users)) {
            log.info("Going to create user notification for users {}", userAnnouncementRequest.getUsers());
            for (User user : users) {
                UserAnnouncementMapping userAnnouncementMapping = getUserAnnouncementMapping(userAnnouncement, user.getUserId());
                userAnnouncementMappingRepository.saveAndFlush(userAnnouncementMapping);
                if (StringUtils.hasText(user.getFbToken())) {
                    firebaseMessagingService.sendNotification(userAnnouncement.getTitle(), userAnnouncement.getMessage(), user.getFbToken(), userAnnouncement.getAttachment());
                }
            }
        } else {
            log.info("Going to create user notification for branches {}", userAnnouncementRequest.getBranchId());
            if (!CollectionUtils.isEmpty(userBranchMappingList)) {
                for (BranchEmployeeDto userBranchMapping : userBranchMappingList) {
                    UserAnnouncementMapping userAnnouncementMapping = getUserAnnouncementMapping(userAnnouncement, userBranchMapping.getUserId());
                    userAnnouncementMappingRepository.saveAndFlush(userAnnouncementMapping);
                    Optional<User> user = userRepository.findByUserId(userBranchMapping.getUserId());
                    if (user.isPresent() && StringUtils.hasText(user.get().getFbToken())) {
                        firebaseMessagingService.sendNotification(userAnnouncement.getTitle(), userAnnouncement.getMessage(), user.get().getFbToken(), userAnnouncement.getAttachment());
                    }
                }
            }
        }
    }

    private static UserAnnouncementMapping getUserAnnouncementMapping(UserAnnouncement userAnnouncement, String userBranchMapping) {
        UserAnnouncementMapping userAnnouncementMapping = new UserAnnouncementMapping();
        userAnnouncementMapping.setAnnouncementId(userAnnouncement.getAnnouncementId());
        userAnnouncementMapping.setUserId(userBranchMapping);
        userAnnouncementMapping.setIsRead("N");
        userAnnouncementMapping.setInsertedBy(userAnnouncement.getInsertedBy());
        userAnnouncementMapping.setOrgId(userAnnouncement.getOrgId());
        return userAnnouncementMapping;
    }
}