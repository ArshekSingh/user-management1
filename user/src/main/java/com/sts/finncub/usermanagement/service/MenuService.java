package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.ObjectNotFoundException;
import com.sts.finncub.usermanagement.response.MenuResponse;
import com.sts.finncub.usermanagement.response.Response;

public interface MenuService{

    MenuResponse fetchMenus() throws ObjectNotFoundException;

    Response getMenuByParentId();
}