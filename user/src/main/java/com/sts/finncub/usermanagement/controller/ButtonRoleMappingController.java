package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.ButtonRoleMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class ButtonRoleMappingController {

    private final ButtonRoleMappingService buttonRoleMappingService;

    @PostMapping("/mapButtonToRole")
    public Response mapButtonToRole(@RequestBody ButtonRoleRequest request) throws BadRequestException {
        log.info("Request initiated to map button to roles");
        return buttonRoleMappingService.mapButtonToRole(request);
    }

}
