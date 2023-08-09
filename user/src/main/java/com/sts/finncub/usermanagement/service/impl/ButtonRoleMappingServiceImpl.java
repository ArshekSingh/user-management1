package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.dto.RoleMasterDto;
import com.sts.finncub.core.entity.ButtonRoleMapping;
import com.sts.finncub.core.entity.RoleMaster;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.ButtonRoleMappingRepository;
import com.sts.finncub.core.repository.RoleMasterRepository;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.core.util.ValidationUtils;
import com.sts.finncub.usermanagement.service.ButtonRoleMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.sts.finncub.core.constants.Constant.SUCCESS;

@Service
@Slf4j
@AllArgsConstructor
public class ButtonRoleMappingServiceImpl implements ButtonRoleMappingService {

    private final ButtonRoleMappingRepository buttonRoleMappingRepository;
    private final UserCredentialService userCredentialService;
    private final RoleMasterRepository roleMasterRepository;
    @Override
    public Response mapButtonToRole(ButtonRoleRequest request) throws BadRequestException {
        ValidationUtils.validateButtonRoleMappingRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        try {
            List<RoleMasterDto> assignedRoles = request.getAssignedRoles();
            List<Long> roleIds = assignedRoles.stream().map(RoleMasterDto::getId).collect(Collectors.toList());
            List<RoleMaster> roleMasterList = roleMasterRepository.findByRoleIdIn(roleIds);
            List<String> roleNames = roleMasterList.stream().map(RoleMaster::getRoleName).collect(Collectors.toList());
            ButtonRoleMapping mapping = new ButtonRoleMapping();
            mapping.setMenuId(request.getMenuId());
            mapping.setOrgId(userSession.getOrganizationId());
            mapping.setButtonName(request.getButtonName());
            mapping.setRoleNames(String.join(",", roleNames));
            mapping.setInsertedBy(userSession.getUserId());
            mapping.setInsertedOn(LocalDateTime.now());
            buttonRoleMappingRepository.save(mapping);
            log.info("roles saved for menu Id {} and button Name {}", request.getMenuId(), request.getButtonName());
            return new Response(SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred due to {}", e.getMessage());
            return new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
