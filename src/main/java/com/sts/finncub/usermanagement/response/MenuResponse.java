package com.sts.finncub.usermanagement.response;

import com.sts.finncub.usermanagement.dto.MenuDTO;
import lombok.Data;

import java.util.List;

@Data
public class MenuResponse {
    private List<MenuDTO> menuDTOList;
}
