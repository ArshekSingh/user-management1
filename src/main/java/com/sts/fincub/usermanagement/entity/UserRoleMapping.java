package com.sts.fincub.usermanagement.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = UserRoleMapping.Columns.TABLE_NAME)
public class UserRoleMapping {
    public interface Columns{
        String TABLE_NAME = "USER_ROLE_MAPPING";
        String ORG_ID  = "ORG_ID";
        String USER_ID = "USER_ID";
        String ROLE_ID = "ROLE_ID";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON = "UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
    }

    @Id
    @Column(name = Columns.ORG_ID)
    private Long organisationId;

    @Column(name = Columns.USER_ID)
    private String userId;

    @Column(name = Columns.ROLE_ID)
    private String roleId;

    @Column(name = Columns.INSERTED_ON)
    private LocalDate insertedOn;

    @Column(name = Columns.INSERTED_BY)
    private String insertedBy;

    @Column(name = Columns.UPDATED_ON)
    private LocalDate updatedOn;

    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;
}
