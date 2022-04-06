package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.MenuRoleMappingDto;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.core.response.Response;

public interface MenuService{

    MenuResponse fetchMenus() throws ObjectNotFoundException;

    Response getMenuSearchable(String menuSearchableKey);

    Response getMenuRoleListAssignedOrAvailable(Long id);

    Response assignMenuToRoles(MenuRoleMappingDto menuRoleMappingDto);

	Response getMenuAssignedOrAvailableForRole(Long roleId);

	Response assignRoleToMenus(MenuRoleMappingDto menuRoleMappingDto);
}