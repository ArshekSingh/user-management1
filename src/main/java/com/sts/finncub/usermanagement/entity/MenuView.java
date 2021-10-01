package com.sts.finncub.usermanagement.entity;

import org.springframework.beans.factory.annotation.Value;

public interface MenuView {
    @Value("#{target.MENU_ID}")
    Long getMenuId();

    @Value("#{target.MENU_NAME}")
    String getMenuName();

    @Value("#{target.ACTION}")
    String getAction();

    @Value("#{target.LABEL}")
    String getLabel();

    @Value("#{target.DISPLAY_SEQUENCE}")
    Long getDisplaySequence();

    @Value("#{target.ICON}")
    String getIcon();

    @Value("#{target.PARENT_ID}")
    Long getParentId();





}
