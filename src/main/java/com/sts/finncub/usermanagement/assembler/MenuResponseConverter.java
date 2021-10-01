package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.usermanagement.dto.MenuDTO;
import com.sts.finncub.usermanagement.entity.MenuView;
import com.sts.finncub.usermanagement.response.MenuResponse;

import java.util.ArrayList;
import java.util.List;

public class MenuResponseConverter {

    public static MenuResponse convert(List<MenuView> menuList){
        MenuResponse response = new MenuResponse();
        List<MenuDTO> menuDTOList = new ArrayList<>();
        for(MenuView menu: menuList){
            if(menu.getParentId() == null){
                MenuDTO menuDTO = convert(menu);
                List<MenuDTO> subMenuList = new ArrayList<>();
                for(MenuView subMenu:menuList){
                    MenuDTO subMenuDTO = convert(subMenu);

                    if(menu.getMenuId().equals(subMenu.getParentId())){
                        List<MenuDTO> pageList = new ArrayList<>();
                        for(MenuView page: menuList) {
                            if(subMenu.getMenuId().equals(page.getParentId())){
                                pageList.add(convert(page));
                            }
                        }
                        subMenuDTO.setContent(pageList);
                        subMenuList.add(subMenuDTO);
                        menuDTO.setContent(subMenuList);


                    }

                }
                menuDTOList.add(menuDTO);

            }
        }

        response.setMenuDTOList(menuDTOList);
        return response;
    }

    public static MenuDTO convert(MenuView menu){
        MenuDTO dto = new MenuDTO();
        dto.setLabel(menu.getLabel());
        dto.setMenuName(menu.getMenuName());
        dto.setTo(menu.getAction());
        dto.setDisplaySequence(menu.getDisplaySequence());
        dto.setIcon(menu.getIcon());
        dto.setContent(new ArrayList<>());
        return dto;
    }
}
