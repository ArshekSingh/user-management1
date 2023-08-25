package com.sts.finncub.usermanagement.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.request.UserAnnouncementFilterRequest;
import com.sts.finncub.core.request.UserAnnouncementRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.AnnouncementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping("/admin/announcement")
    public Response createAnnouncement(@RequestBody UserAnnouncementRequest userAnnouncementRequest) throws FirebaseMessagingException {
        log.info("Request initiated to create announcement");
        return announcementService.createAnnouncement(userAnnouncementRequest);
    }

    @PostMapping("/admin/announcements")
    public Response getAdminAnnouncements(@RequestBody UserAnnouncementFilterRequest userAnnouncementFilterRequest) {
        log.info("Request initiated to fetch announcements");
        return announcementService.getAdminAnnouncements(userAnnouncementFilterRequest);
    }

    @GetMapping("/admin/announcement/{announcementId}")
    public Response getAdminAnnouncement(@PathVariable("announcementId") Long announcementId) {
        log.info("Request initiated to fetch announcement with id : {}", announcementId);
        return announcementService.getAdminAnnouncement(announcementId);
    }

    @PutMapping("/admin/announcement/{announcementId}")
    public Response updateAnnouncement(@PathVariable("announcementId") Long announcementId, @RequestBody UserAnnouncementRequest userAnnouncementRequest) {
        log.info("Request initiated to update announcement with id : {}", announcementId);
        return announcementService.updateAnnouncement(announcementId, userAnnouncementRequest);
    }

    @GetMapping("/announcement")
    public Response getAnnouncements() {
        log.info("Request initiated to fetch announcements");
        return announcementService.getAnnouncements();
    }

    @PutMapping("/announcement/{announcementId}")
    public Response readAnnouncement(@PathVariable("announcementId") String announcementId) {
        log.info("Request initiated to update announcement status with id : {}", announcementId);
        return announcementService.readAnnouncement(announcementId);
    }

}
