package com.sts.finncub.usermanagement.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class UserRoleOrganisationLinkId implements Serializable {

    @Column(name = UserRoleMapping.Columns.USER_ID)
    private String userId;

    @Column(name = UserRoleMapping.Columns.ORG_ID)
    private Long organisationId;

    @Column(name = UserRoleMapping.Columns.ROLE_ID)
    private Long roleId;
}
