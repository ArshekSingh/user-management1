package com.sts.finncub.usermanagement.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class UserOrganisationLinkId  implements Serializable {

    @Column(name = UserOrganisationMapping.Columns.USER_ID)
    private String userId;
    @Column(name = UserOrganisationMapping.Columns.ORG_ID)
    private Long organisationId;
}
