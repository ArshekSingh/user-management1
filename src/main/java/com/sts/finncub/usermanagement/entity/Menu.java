package com.sts.finncub.usermanagement.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = Menu.Columns.TABLE_NAME)
public class Menu {
    interface Columns {
        String MENU_ID = "MENU_ID";
        String MENU_NAME = "MENU_NAME";
        String ACTION = "ACTION";
        String LABEL = "LABEL";
        String DISPLAY_SEQUENCE = "DISPLAY_SEQUENCE";
        String PARENT_ID = "PARENT_ID";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON = "UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
        String ICON = "ICON";
        String TABLE_NAME = "MENU_MASTER";
    }

    @Id
    @Column(name = Columns.MENU_ID)
    private Long id;


    @Column(name = Columns.ACTION)
    private String action;
    @Column(name = Columns.MENU_NAME)
    private String menuName;
    @Column(name = Columns.LABEL)
    private String label;
    @Column(name = Columns.DISPLAY_SEQUENCE)
    private Long displaySequence;
    @Column(name = Columns.PARENT_ID)
    private Long parentId;
    @Column(name = Columns.INSERTED_ON)
    private LocalDate insertedOn;
    @Column(name = Columns.INSERTED_BY)
    private String insertedBy;
    @Column(name = Columns.UPDATED_ON)
    private LocalDate updatedOn;
    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;
    @Column(name = Columns.ICON)
    private String icon;

}
