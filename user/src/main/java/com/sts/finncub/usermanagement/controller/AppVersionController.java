package com.sts.finncub.usermanagement.controller;

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
    public Response getCurrentVersion() {
        return appVersionService.getCurrentVersion();
    }

    @PostMapping("/updateAppVersion")
    public Response updateAppVersion(@RequestParam String value) {
        return appVersionService.updateAppVersion(value);
    }
}
