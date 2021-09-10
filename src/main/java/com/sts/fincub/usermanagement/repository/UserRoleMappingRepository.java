package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping,Long> {
}
