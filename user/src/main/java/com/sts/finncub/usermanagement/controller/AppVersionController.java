package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @GetMapping("/getCurrentVersion")
    public Response getCurrentVersion(@RequestParam String key) {
        return appVersionService.getCurrentVersion(key);
    }

    @PostMapping("/updateAppVersion")
    public Response updateAppVersion(@RequestParam String key, @RequestParam String value) throws BadRequestException {
        return appVersionService.updateAppVersion(key, value);
    }
}
