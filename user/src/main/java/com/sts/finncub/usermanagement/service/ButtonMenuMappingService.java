package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.MenuButtonRequest;
import com.sts.finncub.core.response.Response;

public interface ButtonMenuMappingService {
    Response addButtonsInMenu(MenuButtonRequest request);

    Response getButtonsOnMenu(MenuButtonRequest request);

    Response deleteButton(MenuButtonRequest request);
}
