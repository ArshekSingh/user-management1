package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.UserOrganisationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrganisationMappingRepository extends JpaRepository<UserOrganisationMapping,Long> {
}
