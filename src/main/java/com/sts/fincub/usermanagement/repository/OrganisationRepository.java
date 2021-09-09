package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation,Long> {
}
