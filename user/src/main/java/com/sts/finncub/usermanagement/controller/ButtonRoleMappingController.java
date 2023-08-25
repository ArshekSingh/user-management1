package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.ButtonRoleMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/getButtonToRoleMap")
    public Response getButtonToRoleMap(@RequestBody ButtonRoleRequest request) throws BadRequestException {
        log.info("Request initiated to fetch assigned roles to particular button");
        return buttonRoleMappingService.getButtonToRoleMap(request);
    }

    @GetMapping("/listOfRolesOnButton")
    public Response listOfRolesOnButton(@RequestParam String action) {
        log.info("Request initiated to get list of roles assigned to button");
        return buttonRoleMappingService.listOfRolesOnButton(action);
    }

}
