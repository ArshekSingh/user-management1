package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.MenuMasterDto;
import com.sts.finncub.core.dto.MenuRoleMappingDto;
import com.sts.finncub.core.dto.ServerSideDropDownDto;
import com.sts.finncub.core.entity.*;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.MenuRepository;
import com.sts.finncub.core.repository.MenuRoleMappingRepository;
import com.sts.finncub.core.repository.RoleMasterRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.assembler.MenuResponseConverter;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final UserCredentialService userCredentialService;
    private final MenuRepository menuRepository;
    private final MenuRoleMappingRepository menuRoleMappingRepository;
    private final RoleMasterRepository roleMasterRepository;

    @Autowired
    MenuServiceImpl(UserCredentialService userCredentialService, MenuRepository menuRepository,
                    MenuRoleMappingRepository menuRoleMappingRepository, RoleMasterRepository roleMasterRepository){
        this.userCredentialService = userCredentialService;
        this.menuRepository = menuRepository;
        this.menuRoleMappingRepository = menuRoleMappingRepository;
        this.roleMasterRepository = roleMasterRepository;
    }

    @Override
    public MenuResponse fetchMenus() throws ObjectNotFoundException {
        UserSession userSession = userCredentialService.getUserSession();
        log.info("User found with details - {}",userSession.toString());

        log.info("Fetching menus for userId -"+userSession.getUserId() +"and organizationId -"+userSession.getOrganizationId());
        List<MenuView> menuList = menuRepository.findMenuList(userSession.getUserId(), userSession.getOrganizationId());
        if(menuList == null || menuList.isEmpty()){
            throw  new ObjectNotFoundException("No menu found for user -"+userSession.getName(), HttpStatus.NOT_FOUND);
        }
        log.info(menuList.size()+" Menu(s) found");
        return MenuResponseConverter.convert(menuList);
    }

    @Override
    public Response getMenuSearchable(String menuSearchableKey) {
        Response response = new Response();
        List<ServerSideDropDownDto> serverSideDropDownDtoList = new ArrayList<>();
        List<Menu> menuList = menuRepository.findByMenuNameContainingIgnoreCase(menuSearchableKey);
        for(Menu menu : menuList) {
            ServerSideDropDownDto serverSideDropDownDto = new ServerSideDropDownDto();
            serverSideDropDownDto.setId(String.valueOf(menu.getId()));
            serverSideDropDownDto.setLabel(menu.getId() + "-" + menu.getMenuName());
            serverSideDropDownDtoList.add(serverSideDropDownDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(serverSideDropDownDtoList);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response getMenuRoleListAssignedOrAvailable(Long id) {
        Response response = new Response();
        List<MenuRoleMapping> menuRoleMappingList = menuRoleMappingRepository.findByMenuRoleMappingPK_MenuId(id);
        MenuRoleMappingDto menuRoleMappingDto = new MenuRoleMappingDto();
        List<ServerSideDropDownDto> menuAssignedRolesList = new ArrayList<>();
        List<ServerSideDropDownDto> menuAvailableRolesList = new ArrayList<>();
        List<Long> roleList = new ArrayList<>();
        for(MenuRoleMapping menuRoleMapping : menuRoleMappingList) {
            menuRoleMappingDto.setId(id);
            ServerSideDropDownDto menuAssignedRoles = new ServerSideDropDownDto();
            menuAssignedRoles.setId(menuRoleMapping.getRoleMaster().getRoleId().toString());
            menuAssignedRoles.setLabel(menuRoleMapping.getRoleMaster().getRoleName());
            menuAssignedRolesList.add(menuAssignedRoles);
            roleList.add(menuRoleMapping.getRoleMaster().getRoleId());
        }
        List<RoleMaster> roleMasterList;
        if (roleList.isEmpty()) {
            roleMasterList = roleMasterRepository.findAll();
        } else {
            roleMasterList = roleMasterRepository.findByRoleIdNotIn(roleList);
        }
        for(RoleMaster roleMaster : roleMasterList) {
            ServerSideDropDownDto menuAvailableRoles = new ServerSideDropDownDto();
            menuAvailableRoles.setId(roleMaster.getRoleId().toString());
            menuAvailableRoles.setLabel(roleMaster.getRoleName());
            menuAvailableRolesList.add(menuAvailableRoles);
        }
        menuRoleMappingDto.setAssignedRoles(menuAssignedRolesList);
        menuRoleMappingDto.setAvailableRoles(menuAvailableRolesList);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(menuRoleMappingDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response assignMenuToRoles(MenuRoleMappingDto menuRoleMappingDto) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<MenuRoleMapping> menuRoleMappingList = menuRoleMappingRepository.findByMenuRoleMappingPK_MenuId(menuRoleMappingDto.getId());
        if (!CollectionUtils.isEmpty(menuRoleMappingList)) {
            menuRoleMappingRepository.deleteAll(menuRoleMappingList);
        }
        List<ServerSideDropDownDto> assignedRoleList = menuRoleMappingDto.getAssignedRoles();
        for (ServerSideDropDownDto assignedRole : assignedRoleList) {
            MenuRoleMapping menuRoleMapping = new MenuRoleMapping();
            MenuRoleMappingPK menuRoleMappingPK = new MenuRoleMappingPK();
            menuRoleMappingPK.setMenuId(menuRoleMappingDto.getId());
            menuRoleMappingPK.setRoleId(Long.valueOf(assignedRole.getId()));
            menuRoleMapping.setMenuRoleMappingPK(menuRoleMappingPK);
            menuRoleMapping.setInsertedOn(LocalDateTime.now());
            menuRoleMapping.setInsertedBy(userSession.getUserId());
            menuRoleMappingRepository.save(menuRoleMapping);
        }

        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
    
    @Override
    public Response getMenuAssignedOrAvailableForRole(Long roleId) {
        Response response = new Response();
        List<Menu> menuIdAndMenuNameList = menuRepository.findMenuListForRole(roleId);
        MenuRoleMappingDto menuRoleMappingDto = new MenuRoleMappingDto();
        List<ServerSideDropDownDto> menuAssignedRolesList = new ArrayList<>();
        List<ServerSideDropDownDto> menuAvailableRolesList = new ArrayList<>();
        List<Long> assignedMenuList = new ArrayList<>();
        for(Menu menu : menuIdAndMenuNameList) {
            menuRoleMappingDto.setId(roleId);
            ServerSideDropDownDto menuAssignedToRole = new ServerSideDropDownDto();
            menuAssignedToRole.setId(menu.getId().toString());
            menuAssignedToRole.setLabel(menu.getMenuName());
            menuAssignedRolesList.add(menuAssignedToRole);
            assignedMenuList.add(menu.getId());
        }
        List<Menu> unassignedMenuList;
        if (assignedMenuList.isEmpty()) {
            unassignedMenuList = menuRepository.findAll();
        } else {
            unassignedMenuList = menuRepository.findByIdNotIn(assignedMenuList);
        }
        for(Menu menu : unassignedMenuList) {
            ServerSideDropDownDto menuAvailableRoles = new ServerSideDropDownDto();
            menuAvailableRoles.setId(menu.getId().toString());
            menuAvailableRoles.setLabel(menu.getMenuName());
            menuAvailableRolesList.add(menuAvailableRoles);
        }
        menuRoleMappingDto.setAssignedMenus(menuAssignedRolesList);
        menuRoleMappingDto.setAvailableMenus(menuAvailableRolesList);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setData(menuRoleMappingDto);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
    
    @Override
    public Response assignRoleToMenus(MenuRoleMappingDto menuRoleMappingDto) {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        List<MenuRoleMapping> menuRoleMappingList = menuRoleMappingRepository.findByRoleMaster_RoleId(menuRoleMappingDto.getId());
        if (!CollectionUtils.isEmpty(menuRoleMappingList)) {
        	log.info("Deleting previously assigned menus , roleId : {}",menuRoleMappingDto.getId());
            menuRoleMappingRepository.deleteAll(menuRoleMappingList);
        }
        
        log.info("Adding new assigned menus , roleId : {}",menuRoleMappingDto.getId());
        
        menuRoleMappingDto.getAssignedMenus().stream()
				.forEach(assignedMenu -> saveNewMenuRoleMapping(assignedMenu, userSession, menuRoleMappingDto));
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
    void saveNewMenuRoleMapping(ServerSideDropDownDto assignedMenu,UserSession userSession,MenuRoleMappingDto menuRoleMappingDto)
    {

        MenuRoleMapping menuRoleMapping = new MenuRoleMapping();
        MenuRoleMappingPK menuRoleMappingPK = new MenuRoleMappingPK();
        menuRoleMappingPK.setMenuId(Long.valueOf(assignedMenu.getId()));
        menuRoleMappingPK.setRoleId(menuRoleMappingDto.getId());
        menuRoleMapping.setMenuRoleMappingPK(menuRoleMappingPK);
        menuRoleMapping.setInsertedOn(LocalDateTime.now());
        menuRoleMapping.setInsertedBy(userSession.getUserId());
        menuRoleMappingRepository.save(menuRoleMapping);
    
    }
}