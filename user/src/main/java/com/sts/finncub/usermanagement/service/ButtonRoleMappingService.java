package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;

public interface ButtonRoleMappingService {
    Response mapButtonToRole(ButtonRoleRequest request) throws BadRequestException;
}
