package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.entity.MenuView;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.core.repository.MenuRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.assembler.MenuResponseConverter;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.usermanagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final UserCredentialService userCredentialService;
    private final MenuRepository menuRepository;

    @Autowired
    MenuServiceImpl(UserCredentialService userCredentialService, MenuRepository menuRepository) {
        this.userCredentialService = userCredentialService;
        this.menuRepository = menuRepository;
    }

    @Override
    public MenuResponse fetchMenus() throws ObjectNotFoundException {
        UserSession userSession = userCredentialService.getUserSession();
        log.info("User found with details - {}", userSession.toString());
        log.info("Fetching menus for userId -" + userSession.getUserId() + "and organizationId -" + userSession.getOrganizationId());
        List<MenuView> menuList = menuRepository.findMenuList(userSession.getUserId(), userSession.getOrganizationId());
        if (menuList == null || menuList.isEmpty()) {
            throw new ObjectNotFoundException("No menu found for user -" + userSession.getName(), HttpStatus.NOT_FOUND);
        }
        log.info(menuList.size() + " Menu(s) found");
        return MenuResponseConverter.convert(menuList);
    }
}