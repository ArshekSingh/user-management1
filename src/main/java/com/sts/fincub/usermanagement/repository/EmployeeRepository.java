package com.sts.fincub.usermanagement.repository;

import com.sts.fincub.usermanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,String> {
}
