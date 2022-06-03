package com.sts.finncub.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.dto.MenuRoleMappingDto;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.usermanagement.service.MenuService;

import lombok.extern.slf4j.Slf4j;

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
	public ResponseEntity<Response> menu() {
        log.info("Request received to fetch menus");
        MenuResponse response  = null;
        try {
            response = menuService.fetchMenus();
        } catch(Exception e){
            log.error("Exception -{}",e);
        }
		return ResponseEntity.ok(new Response(RestMappingConstants.SUCCESS, response, HttpStatus.OK));
    }

    @GetMapping(value = "/menu/{menuSearchableKey}")
    public Response getMenuSearchable(@PathVariable String menuSearchableKey) {
    	log.info("getMenuSearchable invoked , menuSearchableKey : {}",menuSearchableKey);
        return menuService.getMenuSearchable(menuSearchableKey);
    }

    @GetMapping(value = "getMenuRoleList/{id}")
    public Response getMenuRoleListAssignedOrAvailable(@PathVariable Long id) {
    	log.info("getMenuRoleListAssignedOrAvailable invoked , menuId : {}",id);
        return menuService.getMenuRoleListAssignedOrAvailable(id);
    }

    @PostMapping(value = "assignMenuToRoles")
    public Response assignMenuToRoles(@RequestBody MenuRoleMappingDto menuRoleMappingDto) {
    	log.info("assignMenuToRoles invoked , menuId : {}",menuRoleMappingDto.getId());
        return menuService.assignMenuToRoles(menuRoleMappingDto);
    }
    
    @GetMapping(value = "getMenuAssignedOrAvailableForRole/{roleId}")
    public Response getMenuAssignedOrAvailableForRole(@PathVariable Long roleId) {
    	log.info("getMenuAssignedOrAvailableForRole() invoked for roleId : {}",roleId);
        return menuService.getMenuAssignedOrAvailableForRole(roleId);
    }
    
    @PostMapping(value = "assignRoleToMenus")
	public Response assignRoleToMenus(@RequestBody MenuRoleMappingDto menuRoleMappingDto) throws BadRequestException {
    	
    	if (menuRoleMappingDto.isValid(menuRoleMappingDto)) {
    		log.info("assignRoleToMenus() invoked for roleId : {}",menuRoleMappingDto.getId());
			return menuService.assignRoleToMenus(menuRoleMappingDto);
		} else {
			log.error("BadRequest passed to assignRoleToMenus()");
			throw new BadRequestException("Required attributes not supplied in request !",HttpStatus.BAD_REQUEST);
		}
	}
}