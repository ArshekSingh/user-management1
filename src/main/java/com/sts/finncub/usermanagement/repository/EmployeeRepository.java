package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {

   Employee findByUserId(String userId);
}
