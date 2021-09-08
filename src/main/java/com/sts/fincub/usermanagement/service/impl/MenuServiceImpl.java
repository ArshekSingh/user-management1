package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.usermanagement.assembler.MenuResponseConverter;
import com.sts.fincub.usermanagement.entity.MenuView;
import com.sts.fincub.usermanagement.entity.UserSession;
import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.repository.MenuRepository;
import com.sts.fincub.usermanagement.response.MenuResponse;
import com.sts.fincub.usermanagement.service.MenuService;
import com.sts.fincub.usermanagement.service.UserCredentialService;
import com.sts.fincub.usermanagement.utils.JSONUtils;
import io.micrometer.core.instrument.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final UserCredentialService userCredentialService;
    private final MenuRepository menuRepository;

    @Autowired
    MenuServiceImpl(UserCredentialService userCredentialService,MenuRepository menuRepository){
        this.userCredentialService = userCredentialService;
        this.menuRepository = menuRepository;
    }

    @Override
    public MenuResponse fetchMenus() throws ObjectNotFoundException {
        UserSession userSession = userCredentialService.getUserData();
        log.info("User found with details - {}",userSession.toString());
        List<MenuView> menuList = menuRepository.findMenuList("0002",1L);
        if(menuList == null || menuList.isEmpty()){
            throw  new ObjectNotFoundException("No menu found for user -"+userSession.getName(), HttpStatus.NOT_FOUND);
        }
        return MenuResponseConverter.convert(menuList);

    }
}
