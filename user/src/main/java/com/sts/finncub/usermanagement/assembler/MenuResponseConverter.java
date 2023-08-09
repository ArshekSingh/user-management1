package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.MenuDto;
import com.sts.finncub.core.entity.MenuView;
import com.sts.finncub.usermanagement.response.MenuResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuResponseConverter {

    public static MenuResponse convert(List<MenuView> menuList){
        MenuResponse response = new MenuResponse();
        List<MenuDto> menuDtoList = new ArrayList<>();
        for(MenuView menu: menuList){
            if(menu.getParentId() == null){
                MenuDto menuDTO = convert(menu);
                List<MenuDto> subMenuList = new ArrayList<>();
                for(MenuView subMenu:menuList){
                    MenuDto subMenuDTO = convert(subMenu);

                    if(menu.getMenuId().equals(subMenu.getParentId())){
                        List<MenuDto> pageList = new ArrayList<>();
                        for(MenuView page: menuList) {
                            if(subMenu.getMenuId().equals(page.getParentId())){
                                pageList.add(convert(page));
                            }
                        }
                        pageList.sort(Comparator.comparing(MenuDto::getLabel));
                        subMenuDTO.setContent(pageList);
                        subMenuList.add(subMenuDTO);
                        menuDTO.setContent(subMenuList);


                    }

                }
                menuDtoList.add(menuDTO);

            }
        }

        response.setMenuDTOList(menuDtoList);
        return response;
    }

    public static MenuDto convert(MenuView menu){
        MenuDto dto = new MenuDto();
        dto.setLabel(menu.getLabel());
        dto.setMenuName(menu.getMenuName());
        dto.setTo(menu.getAction());
        dto.setDisplaySequence(menu.getDisplaySequence());
        dto.setIcon(menu.getIcon());
        dto.setContent(new ArrayList<>());
        return dto;
    }
}
