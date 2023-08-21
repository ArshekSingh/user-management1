package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.dto.MenuButtonRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.ButtonMenuMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class ButtonMenuMappingController {

    private final ButtonMenuMappingService buttonMenuMappingService;

    @PostMapping("/addButtonsInMenu")
    public Response addButtonsInMenu(@RequestBody MenuButtonRequest request) {
        log.info("Request initiated to add buttons on menu");
       return buttonMenuMappingService.addButtonsInMenu(request);
    }

    @PostMapping("/getButtonsOnMenu")
    public Response getButtonsOnMenu(@RequestBody MenuButtonRequest request) {
        log.info("Request initiated to get button details based on menu");
        return buttonMenuMappingService.getButtonsOnMenu(request);
    }

    @DeleteMapping("/deleteButton")
    public Response deleteButton(@RequestBody MenuButtonRequest request) {
        log.info("Request initiated to delete button {} in menu Id {}", request.getButtonName(), request.getButtonName());
        return buttonMenuMappingService.deleteButton(request);
    }
}
