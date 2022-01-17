package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.dto.MenuRoleMappingDto;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @Autowired
    MenuController(MenuService menuService){
        this.menuService = menuService;
    }

    @GetMapping("menu")
    public ResponseEntity<Response<MenuResponse>> menu() {
        log.info("Request received to fetch menus");
        MenuResponse response  = null;
        try {
            response = menuService.fetchMenus();
        } catch(Exception e){
            log.error("Exception -{}",e);
        }
        return  ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS,response,HttpStatus.OK));
    }

    @GetMapping(value = "/menu/{menuSearchableKey}")
    public Response getMenuSearchable(@PathVariable String menuSearchableKey) {
        return menuService.getMenuSearchable(menuSearchableKey);
    }

    @GetMapping(value = "getMenuRoleList/{id}")
    public Response getMenuRoleListAssignedOrAvailable(@PathVariable Long id) {
        return menuService.getMenuRoleListAssignedOrAvailable(id);
    }

    @PostMapping(value = "assignMenuToRoles")
    public Response assignMenuToRoles(@RequestBody MenuRoleMappingDto menuRoleMappingDto) {
        return menuService.assignMenuToRoles(menuRoleMappingDto);
    }
}