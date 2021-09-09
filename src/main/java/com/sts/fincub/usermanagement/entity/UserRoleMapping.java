package com.sts.fincub.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

   @EmbeddedId
   private UserRoleOrganisationLinkId id;

    @Column(name = Columns.INSERTED_ON)
    private LocalDate insertedOn;

    @Column(name = Columns.INSERTED_BY)
    private String insertedBy;

    @Column(name = Columns.UPDATED_ON)
    private LocalDate updatedOn;

    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;


    public UserRoleMapping(String userId,Long organisationId,Long roleId,String userName){
        UserRoleOrganisationLinkId id = new UserRoleOrganisationLinkId();
        id.setRoleId(roleId);
        id.setOrganisationId(organisationId);
        id.setUserId(userId);
        this.id = id;
        this.insertedOn = LocalDate.now();
        this.updatedOn = LocalDate.now();
        this.insertedBy = userName;
        this.updatedBy = userName;
    }

}
