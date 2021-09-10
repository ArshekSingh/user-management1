package com.sts.fincub.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Employee.Columns.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {
    public interface Columns{
        String ORG_ID = "ORG_ID";
        String EMPLOYEE_CODE= "EMPLOYEE_CODE";
        String USER_ID = "USER_ID";
        String TABLE_NAME= "EMPLOYEE_MASTER";
    }


    @Column(name = Columns.ORG_ID)
    private Long organisationId;

    @Id
    @Column(name = Columns.EMPLOYEE_CODE)
    private String employeeCode;

    @Column(name = Columns.USER_ID)
    private String userId;

}
