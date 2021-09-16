package com.sts.finncub.usermanagement.service;

import com.sts.finncub.usermanagement.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.response.MenuResponse;

public interface MenuService{

    MenuResponse fetchMenus() throws ObjectNotFoundException;
}
