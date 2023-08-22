package com.sts.finncub.usermanagement.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sts.finncub.core.entity.UserAnnouncement;
import com.sts.finncub.core.exception.ObjectNotFoundException;
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
    public Response createAnnouncement(@RequestBody UserAnnouncement userAnnouncement) throws ObjectNotFoundException, FirebaseMessagingException {
        log.info("Request initiated to create announcement");
        return announcementService.createAnnouncement(userAnnouncement);
    }

    @PostMapping("/admin/announcements")
    public Response getAdminAnnouncements(@RequestBody UserAnnouncementRequest userAnnouncementRequest) {
        log.info("Request initiated to fetch announcements");
        return announcementService.getAdminAnnouncements(userAnnouncementRequest);
    }

    @PutMapping("/admin/announcement/{announcementId}")
    public Response updateAnnouncement(@PathVariable("announcementId") Long announcementId, @RequestBody UserAnnouncement userAnnouncement) throws ObjectNotFoundException {
        log.info("Request initiated to update announcement");
        return announcementService.updateAnnouncement(announcementId, userAnnouncement);
    }

    @GetMapping("/admin/announcement/{announcementId}")
    public Response getAdminAnnouncement(@PathVariable("announcementId") Long announcementId) throws ObjectNotFoundException {
        log.info("Request initiated to update announcement");
        return announcementService.getAdminAnnouncement(announcementId);
    }

    @GetMapping("/announcement")
    public Response getAnnouncements() throws ObjectNotFoundException {
        log.info("Request initiated to fetch announcements");
        return announcementService.getAnnouncements();
    }

    @PutMapping("/announcement/{announcementId}")
    public Response readAnnouncement(@PathVariable("announcementId") String announcementId) throws ObjectNotFoundException {
        log.info("Request initiated to update user announcement status");
        return announcementService.readAnnouncement(announcementId);
    }

}
