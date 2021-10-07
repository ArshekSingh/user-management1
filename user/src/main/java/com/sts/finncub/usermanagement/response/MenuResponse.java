package com.sts.finncub.usermanagement.response;

import com.sts.finncub.core.dto.MenuDto;
import lombok.Data;

import java.util.List;

@Data
public class MenuResponse {
    private List<MenuDto> menuDTOList;
}
