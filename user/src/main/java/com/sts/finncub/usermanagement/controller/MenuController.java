package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.MenuService;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*")
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @Autowired
    MenuController(MenuService menuService){
        this.menuService = menuService;
    }

    @GetMapping("menu")
    public ResponseEntity<Response<MenuResponse>> menu() throws ObjectNotFoundException {
        log.info("Request received to fetch menus");
        MenuResponse response  = null;
        try {
             response = menuService.fetchMenus();
        }catch(Exception e){
            log.error("Exception -{}",e);
        }

        return  ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS,response,HttpStatus.OK));
    }
}
