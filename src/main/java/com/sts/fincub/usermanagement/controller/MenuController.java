package com.sts.fincub.usermanagement.controller;

import com.sts.fincub.usermanagement.constants.RestMappingConstants;
import com.sts.fincub.usermanagement.dto.MenuDTO;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.request.LoginRequest;
import com.sts.fincub.usermanagement.response.LoginResponse;
import com.sts.fincub.usermanagement.response.MenuResponse;
import com.sts.fincub.usermanagement.response.Response;
import com.sts.fincub.usermanagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    MenuController(MenuService menuService){
        this.menuService = menuService;
    }

    @GetMapping("menu")
    public ResponseEntity<Response<MenuResponse>> menu() throws  ObjectNotFoundException {
        MenuResponse response = menuService.fetchMenus();
        return  ResponseEntity.ok(new Response<>(RestMappingConstants.SUCCESS,response,HttpStatus.OK));
    }
}
