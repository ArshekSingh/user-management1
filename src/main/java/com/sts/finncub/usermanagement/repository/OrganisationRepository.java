package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation,Long> {
}
