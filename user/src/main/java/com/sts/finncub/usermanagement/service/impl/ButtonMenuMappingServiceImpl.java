package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.assembler.ButtonMenuMapAssembler;
import com.sts.finncub.core.dto.MenuButtonMappingResponse;
import com.sts.finncub.core.dto.MenuButtonRequest;
import com.sts.finncub.core.entity.MenuButtonMapping;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.repository.ButtonMenuMapRepository;
import com.sts.finncub.core.repository.dao.ButtonMenuMappingDao;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.service.ButtonMenuMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.sts.finncub.core.constants.Constant.FAILED;
import static com.sts.finncub.core.constants.Constant.SUCCESS;

@Service
@Slf4j
@AllArgsConstructor
public class ButtonMenuMappingServiceImpl implements ButtonMenuMappingService {

    private final ButtonMenuMapRepository buttonMenuMapRepository;
    private final UserCredentialService userCredentialService;
    private final ButtonMenuMappingDao buttonMenuMappingDao;
    private final ButtonMenuMapAssembler buttonMenuMapAssembler;

    @Override
    public Response addButtonsInMenu(MenuButtonRequest request) {
        try {
            if (request.getMenuId() == null || !StringUtils.hasText(request.getButtonName())) {
                return new Response("Menu name and Button name are mandatory", HttpStatus.BAD_REQUEST);
            }
            UserSession userSession = userCredentialService.getUserSession();
            MenuButtonMapping mapping = new MenuButtonMapping();
            mapping.setMenuId(request.getMenuId());
            mapping.setButtonName(request.getButtonName());
            mapping.setInsertedBy(userSession.getUserId());
            mapping.setInsertedOn(LocalDateTime.now());
            buttonMenuMapRepository.save(mapping);
            log.info("button {} added in Menu Id {}", request.getButtonName(), request.getMenuId());
            return new Response(SUCCESS, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception occurred due to {}", e.getMessage());
            return new Response(FAILED, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response getButtonsOnMenu(MenuButtonRequest request) {
        try {
            UserSession userSession = userCredentialService.getUserSession();
            List<MenuButtonMapping> menuButtonMappingList = buttonMenuMappingDao.getButtonsOnMenu(request, userSession);
            if (!CollectionUtils.isEmpty(menuButtonMappingList)) {
                List<MenuButtonMappingResponse> mappingResponses = buttonMenuMapAssembler.mapEntityListToDto(menuButtonMappingList);
                return new Response(SUCCESS, mappingResponses, HttpStatus.OK);
            } else {
                log.info("No menu buttons details found");
                return new Response(FAILED, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Exception occurred due to {}", e.getMessage());
            return new Response(FAILED, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Response deleteButton(MenuButtonRequest request) {
        UserSession userSession = userCredentialService.getUserSession();
        try {
            if (request.getMenuId() != null && StringUtils.hasText(request.getButtonName())) {
                buttonMenuMapRepository.deleteByOrgIdAndMenuIdAndButtonName(userSession.getOrganizationId(), request.getMenuId(), request.getButtonName());
                return new Response(SUCCESS, HttpStatus.OK);
            } else {
                return new Response("Please enter the valid menu id and button name", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            log.error("Exception occurred due to {}", exception.getMessage());
            return new Response(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
