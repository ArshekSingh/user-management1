package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.UserOrganisationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrganisationMappingRepository extends JpaRepository<UserOrganisationMapping,Long> {
}
