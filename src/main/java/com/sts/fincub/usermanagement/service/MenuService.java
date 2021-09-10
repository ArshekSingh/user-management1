package com.sts.fincub.usermanagement.service;

import com.sts.fincub.usermanagement.exception.ObjectNotFoundException;
import com.sts.fincub.usermanagement.response.MenuResponse;

import java.util.List;

public interface MenuService{

    MenuResponse fetchMenus() throws ObjectNotFoundException;
}
