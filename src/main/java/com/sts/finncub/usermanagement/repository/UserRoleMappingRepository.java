package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping,Long> {
}
