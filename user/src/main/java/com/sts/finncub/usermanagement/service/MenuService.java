package com.sts.finncub.usermanagement.service;

import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.core.exception.ObjectNotFoundException;

public interface MenuService{

    MenuResponse fetchMenus() throws ObjectNotFoundException;
}
