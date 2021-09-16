package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolesRepository extends JpaRepository<Role,Long> {
    List<Role> findByRoleNameIn(List<String> roleNameList);
}
