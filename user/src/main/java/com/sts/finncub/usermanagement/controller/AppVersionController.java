package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.AppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @GetMapping("/getCurrentVersion")
    public Response getCurrentVersion(@RequestParam String key) {
        log.info("Request initiated to get {}",key);
        return appVersionService.getCurrentVersion(key);
    }

    @PostMapping("/updateAppVersion")
    public Response updateAppVersion(@RequestParam String key, @RequestParam String value) throws BadRequestException {
        return appVersionService.updateAppVersion(key, value);
    }
}
