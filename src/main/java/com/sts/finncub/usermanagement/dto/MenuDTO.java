package com.sts.finncub.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
