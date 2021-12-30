package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/menu/menuByParentId")
    public Response getMenuByParentId() {
        return menuService.getMenuByParentId();
    }
}