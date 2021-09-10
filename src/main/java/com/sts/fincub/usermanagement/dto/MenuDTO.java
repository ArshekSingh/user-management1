package com.sts.fincub.usermanagement.dto;

import com.sts.fincub.usermanagement.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private String menuName;
    private String label;
    private Long displaySequence;
    private String to;
    private List<MenuDTO> content;
}
