package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.MiscellService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class MiscellaneousController {

    private final MiscellService miscellService;

    @GetMapping("/getMiscellaneousNames")
    public Response getMiscellaneousNames() {
        log.info("Request initiated to fetch list of miscellaneous keys");
        return miscellService.getMiscellaneousNames();
    }

    @PostMapping("/updateMiscellaneousNames")
    public Response updateMiscellaneousNames(@RequestParam String key, @RequestParam String value) {
        log.info("Request initiated to update email ids for key {}", key);
        return miscellService.updateMiscellaneousNames(key, value);
    }
}
