package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.ButtonRoleMappingResponse;
import com.sts.finncub.core.dto.ButtonRoleRequest;
import com.sts.finncub.core.dto.RoleMasterDto;
import com.sts.finncub.core.dto.ServerSideDropDownDto;
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
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sts.finncub.core.constants.Constant.FAILED;
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
            Optional<ButtonRoleMapping> buttonRoleMappingOptional = buttonRoleMappingRepository.findByOrgIdAndMenuIdAndButtonNameContainingIgnoreCase(userSession.getOrganizationId(), request.getMenuId(), request.getButtonName());
            if (buttonRoleMappingOptional.isPresent()) {
                buttonRoleMappingRepository.deleteAll();
            }
            List<RoleMasterDto> assignedRoles = request.getAssignedRoles();
            List<Long> roleIds = assignedRoles.stream().map(RoleMasterDto::getId).collect(Collectors.toList());
            List<String> stringList = roleIds.stream().map(String::valueOf).collect(Collectors.toList());
            ButtonRoleMapping mapping = new ButtonRoleMapping();
            mapping.setMenuId(request.getMenuId());
            mapping.setOrgId(userSession.getOrganizationId());
            mapping.setButtonName(request.getButtonName().toUpperCase());
            mapping.setRoleNames(String.join(",", stringList));
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

    @Override
    public Response getButtonToRoleMap(ButtonRoleRequest request) throws BadRequestException {
        ValidationUtils.validateGetButtonRoleMapRequest(request);
        UserSession userSession = userCredentialService.getUserSession();
        Optional<ButtonRoleMapping> buttonRoleMappingOptional = buttonRoleMappingRepository.findByOrgIdAndMenuIdAndButtonNameContainingIgnoreCase(userSession.getOrganizationId(), request.getMenuId(), request.getButtonName());
        if (buttonRoleMappingOptional.isPresent()) {
            ButtonRoleMapping mapping = buttonRoleMappingOptional.get();
            List<String> roleIds = Arrays.asList(mapping.getRoleNames().split(","));
            List<Long> roleIdList = roleIds.stream().map(Long::valueOf).collect(Collectors.toList());
            List<RoleMaster> availableRoles = roleMasterRepository.findByRoleIdNotIn(roleIdList);
            List<RoleMaster> assignedRoles = roleMasterRepository.findByRoleIdIn(roleIdList);
            List<ServerSideDropDownDto> availableRolesArray = new ArrayList<>();
            List<ServerSideDropDownDto> assignedRolesArray = new ArrayList<>();
            if (!CollectionUtils.isEmpty(availableRoles)) {
                availableRoles.forEach(o -> {
                    ServerSideDropDownDto availableRolesList = new ServerSideDropDownDto();
                    availableRolesList.setId(o.getRoleId().toString());
                    availableRolesList.setLabel(o.getRoleName());
                    availableRolesArray.add(availableRolesList);
                });
            }
            if (!CollectionUtils.isEmpty(assignedRoles)) {
                assignedRoles.forEach(o -> {
                    ServerSideDropDownDto assignedRolesList = new ServerSideDropDownDto();
                    assignedRolesList.setId(o.getRoleId().toString());
                    assignedRolesList.setLabel(o.getRoleName());
                    assignedRolesArray.add(assignedRolesList);
                });
            }
            ButtonRoleMappingResponse response = new ButtonRoleMappingResponse();
            response.setMenuId(mapping.getMenuId());
            response.setButtonName(mapping.getButtonName());
            response.setAssignedRoles(assignedRolesArray);
            response.setAvailableRoles(availableRolesArray);
            log.info("Response received for menu Id {} and button Name {}", request.getMenuId(), request.getButtonName());
            return new Response(SUCCESS, response, HttpStatus.OK);
        } else {
            log.info("No records found for menu Id {} and button Name {}", request.getMenuId(), request.getButtonName());
            return new Response(FAILED, HttpStatus.BAD_REQUEST);
        }
    }
}
