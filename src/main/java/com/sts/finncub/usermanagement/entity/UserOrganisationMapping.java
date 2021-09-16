package com.sts.finncub.usermanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = UserOrganisationMapping.Columns.TABLE_NAME)
public class UserOrganisationMapping {
    public interface Columns{
        String TABLE_NAME = "USER_ORG_MAPPING";
        String ORG_ID = "ORG_ID";
        String USER_ID = "USER_ID";
        String ACTIVE = "ACTIVE";
        String IS_ADMIN = "IS_ADMIN";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON = "UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
    }

    @EmbeddedId
    private UserOrganisationLinkId id;

    @Column(name = Columns.ACTIVE)
    private String active;

    @Column(name = Columns.IS_ADMIN)
    private String isAdmin;

    @Column(name = Columns.INSERTED_ON)
    private LocalDate insertedOn;

    @Column(name = Columns.INSERTED_BY)
    private String insertedBy;

    @Column(name = Columns.UPDATED_ON)
    private LocalDate updatedOn;

    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;

    public UserOrganisationMapping(Long organisationId,String userId,String userName){
        UserOrganisationLinkId id = new UserOrganisationLinkId();
        id.setOrganisationId(organisationId);
        id.setUserId(userId);
        this.id= id;
        insertedOn = LocalDate.now();
        updatedOn = LocalDate.now();
        active = "Y";
        insertedBy= userName;
        updatedBy = userName;
    }

}
