package com.sts.fincub.usermanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = Role.Columns.TABLE_NAME)
public class Role implements Serializable {

    private final static long serialVersionUID = -6524768694427900621L;

    interface Columns {
        String TABLE_NAME = "ROLE_MASTER";
        String ID = "ROLE_ID";
        String ROLE_NAME="ROLE_NAME";
    }
    @Id
    @Column(name =  Columns.ID)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = Columns.ROLE_NAME)
    private String roleName;




}
