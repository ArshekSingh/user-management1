package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolesRepository extends JpaRepository<Role,Long> {
    List<Role> findByRoleNameIn(List<String> roleNameList);
}
