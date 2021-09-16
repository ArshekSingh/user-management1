package com.sts.finncub.usermanagement.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String id;
    private String name;
    private String mobileNumber;
    private String email;
    private OrganisationDTO organisation;
}
