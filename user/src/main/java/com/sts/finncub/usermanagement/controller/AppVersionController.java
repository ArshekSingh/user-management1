package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.AppVersionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping("/getCurrentVersion")
    public Response getCurrentVersion(@RequestParam String key) {
        log.info("Request initiated to get {}", key);
        return appVersionService.getCurrentVersion(key);
    }

    @PostMapping("/updateAppVersion")
    public Response updateAppVersion(@RequestParam String key, @RequestParam String value) throws BadRequestException {
        log.info("Request initiated to update {} to {}", key, value);
        return appVersionService.updateAppVersion(key, value);
    }
}
