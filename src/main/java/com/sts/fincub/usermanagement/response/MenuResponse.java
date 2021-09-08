package com.sts.fincub.usermanagement.response;

import com.sts.fincub.usermanagement.dto.MenuDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class MenuResponse {
    private List<MenuDTO> menuDTOList;
}
