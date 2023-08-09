package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.entity.ButtonRoleMapping;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.ButtonRoleMappingRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.ValidationUtils;
import com.sts.finncub.usermanagement.service.ButtonRoleMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class ButtonRoleMappingServiceImpl implements ButtonRoleMappingService {

    private final ButtonRoleMappingRepository buttonRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    @Override
    public Response mapButtonToRole(ButtonRoleRequest request) throws BadRequestException {
        ValidationUtils.validateButtonRoleMappingRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        ButtonRoleMapping mapping = new ButtonRoleMapping();
        mapping.setMenuId(request.getMenuId());
        mapping.setOrgId(userSession.getOrganizationId());
        mapping.setButtonName(request.getButtonName());
        mapping.setRoleNames(String.join(",", request.getAssignedRoles()));
        mapping.setInsertedBy(userSession.getUserId());
        mapping.setInsertedOn(LocalDateTime.now());


    }
}
