package com.sts.finncub.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = Employee.Columns.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {

    public interface Columns {
        String ORG_ID = "ORG_ID";
        String EMPLOYEE_ID = "EMPLOYEE_ID";
        String EMPLOYEE_CODE = "EMPLOYEE_CODE";
        String STATUS = "STATUS";
        String TITLE = "TITLE";
        String FIRST_NAME = "FIRST_NAME";
        String MIDDLE_NAME = "MIDDLE_NAME";
        String LAST_NAME = "LAST_NAME";
        String MARITAL_STATUS = "MARITAL_STATUS";
        String GENDER = "GENDER";
        String DATE_OF_BIRTH = "DATE_OF_BIRTH";
        String FATHER_NAME = "FATHER_NAME";
        String MOTHER_NAME = "MOTHER_NAME";
        String SPOUSE_NAME = "SPOUSE_NAME";
        String LANGUAGE_KNOWN = "LANGUAGE_KNOWN";
        String QUALIFICATION = "QUALIFICATION";
        String BLOOD_GROUP = "BLOOD_GROUP";
        String PERSONAL_MOBILE_NUMBER = "PERSONAL_MOBILE_NUMBER";
        String CUG_MOBILE_NUMBER = "CUG_MOBILE_NUMBER";
        String PERSONAL_EMAIL_ID = "PERSONAL_EMAIL_ID";
        String OFFICIAL_EMAIL_ID = "OFFICIAL_EMAIL_ID";
        String LANDLINE_NUMBER = "LANDLINE_NUMBER";
        String CURRENT_ADDRESS = "CURRENT_ADDRESS";
        String CURRENT_CITY = "CURRENT_CITY";
        String CURRENT_STATE = "CURRENT_STATE";
        String CURRENT_PINCODE = "CURRENT_PINCODE";
        String PERMANENT_ADDRESS = "PERMANENT_ADDRESS";
        String PERMANENT_CITY = "PERMANENT_CITY";
        String PERMANENT_STATE = "PERMANENT_STATE";
        String PERMANENT_PINCODE = "PERMANENT_PINCODE";
        String NATIONALITY = "NATIONALITY";
        String ALTERNATIVE_CONTACT = "ALTERNATIVE_CONTACT";
        String EMERGENCY_CONTACT = "EMERGENCY_CONTACT";
        String EMPLOYMENT_TYPE = "EMPLOYMENT_TYPE";
        String JOINING_DATE = "JOINING_DATE";
        String CONFIRMATION_DATE = "CONFIRMATION_DATE";
        String PROMOTION_DATE = "PROMOTION_DATE";
        String RELIEVING_DATE = "RELIEVING_DATE";
        String AADHAR_CARD_NUMBER = "AADHAR_CARD_NUMBER";
        String PANCARD_NUMBER = "PANCARD_NUMBER";
        String PF_NUMBER = "PF_NUMBER";
        String UAN_NUMBER = "UAN_NUMBER";
        String DRIVING_LICENCE_NUMBER = "DRIVING_LICENCE_NUMBER";
        String PASSPORT_NUMBER = "PASSPORT_NUMBER";
        String ESIC_NUMBER = "ESIC_NUMBER";
        String BANK_NAME = "BANK_NAME";
        String IFSC_CODE = "IFSC_CODE";
        String BANK_MMID = "BANK_MMID";
        String BANK_VPA = "BANK_VPA";
        String BANK_ACCOUNT_TYPE = "BANK_ACCOUNT_TYPE";
        String BANK_ACCOUNT_NUMBER = "BANK_ACCOUNT_NUMBER";
        String BANK_BRANCH = "BANK_BRANCH";
        String USER_ID = "USER_ID";
        String PROFILE_IMAGE_PATH = "PROFILE_IMAGE_PATH";
        String SIGNATURE_IMAGE_PATH = "SIGNATURE_IMAGE_PATH";
        String BRANCH_ID = "BRANCH_ID";
        String BRANCH_JOINING_DATE = "BRANCH_JOINING_DATE";
        String DEPARTMENT_ID = "DEPARTMENT_ID";
        String DEPARTMENT_ROLE_ID = "DEPARTMENT_ROLE_ID";
        String SUB_DEPARTMENT_ID = "SUB_DEPARTMENT_ID";
        String DESIGNATION_TYPE = "DESIGNATION_TYPE";
        String DESIGNATION_ID = "DESIGNATION_ID";
        String FUNCTIONAL_TITLE_ID = "FUNCTIONAL_TITLE_ID";
        String ORGANIZATION_BAND = "ORGANIZATION_BAND";
        String REPORTING_MANAGER_ID = "REPORTING_MANAGER_ID";
        String HR_MANAGER_ID = "HR_MANAGER_ID";
        String ACCOUNT_MANAGER_ID = "ACCOUNT_MANAGER_ID";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON = "UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
        String TABLE_NAME = "EMPLOYEE_MASTER";
    }


    @Column(name = Columns.ORG_ID)
    private Long organisationId;

    @Id
    @Column(name = Columns.EMPLOYEE_ID)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer employeeId;

    @Column(name = Columns.EMPLOYEE_CODE)
    private String employeeCode;

    @Column(name = Columns.STATUS)
    private String status;

    @Column(name = Columns.TITLE)
    private String title;

    @Column(name = Columns.FIRST_NAME)
    private String firstName;

    @Column(name = Columns.MIDDLE_NAME)
    private String middleName;

    @Column(name = Columns.LAST_NAME)
    private String lastName;

    @Column(name = Columns.MARITAL_STATUS)
    private String maritalStatus;

    @Column(name = Columns.GENDER)
    private String gender;

    @Column(name = Columns.DATE_OF_BIRTH)
    private LocalDateTime dob;

    @Column(name = Columns.FATHER_NAME)
    private String fatherName;

    @Column(name = Columns.MOTHER_NAME)
    private String motherName;

    @Column(name = Columns.SPOUSE_NAME)
    private String spouseName;

    @Column(name = Columns.LANGUAGE_KNOWN)
    private String languageName;

    @Column(name = Columns.QUALIFICATION)
    private String qualification;

    @Column(name = Columns.BLOOD_GROUP)
    private String bloodGroup;

    @Column(name = Columns.PERSONAL_MOBILE_NUMBER)
    private Integer personalNumber;

    @Column(name = Columns.CUG_MOBILE_NUMBER)
    private Integer cugMobileNo;

    @Column(name = Columns.PERSONAL_EMAIL_ID)
    private String personalEmail;

    @Column(name = Columns.OFFICIAL_EMAIL_ID)
    private String officialEmail;

    @Column(name = Columns.LANDLINE_NUMBER)
    private String landlineNo;

    @Column(name = Columns.CURRENT_ADDRESS)
    private String currentAddress;

    @Column(name = Columns.CURRENT_CITY)
    private String currentCity;

    @Column(name = Columns.CURRENT_STATE)
    private String currentState;

    @Column(name = Columns.CURRENT_PINCODE)
    private Integer currentPinCode;

    @Column(name = Columns.PERMANENT_ADDRESS)
    private String permanentAddress;

    @Column(name = Columns.PERMANENT_CITY)
    private String permanentCity;

    @Column(name = Columns.PERMANENT_STATE)
    private String permanentState;

    @Column(name = Columns.PERMANENT_PINCODE)
    private Integer permanentPinCode;

    @Column(name = Columns.NATIONALITY)
    private String nationality;

    @Column(name = Columns.ALTERNATIVE_CONTACT)
    private String alternativeContact;

    @Column(name = Columns.EMERGENCY_CONTACT)
    private String emergencyContact;

    @Column(name = Columns.EMPLOYMENT_TYPE)
    private String emergencyType;

    @Column(name = Columns.JOINING_DATE)
    private LocalDateTime joiningDate;

    @Column(name = Columns.CONFIRMATION_DATE)
    private LocalDateTime confirmationDate;

    @Column(name = Columns.PROMOTION_DATE)
    private LocalDateTime promotionDate;

    @Column(name = Columns.RELIEVING_DATE)
    private LocalDateTime relievingDate;

    @Column(name = Columns.AADHAR_CARD_NUMBER)
    private Integer aadharNo;

    @Column(name = Columns.PANCARD_NUMBER)
    private String panCardNo;

    @Column(name = Columns.PF_NUMBER)
    private String pfNumber;

    @Column(name = Columns.DRIVING_LICENCE_NUMBER)
    private String drivingLicenceNo;

    @Column(name = Columns.PASSPORT_NUMBER)
    private String passportNo;

    @Column(name = Columns.ESIC_NUMBER)
    private String esicNo;

    @Column(name = Columns.BANK_NAME)
    private String bankName;

    @Column(name = Columns.IFSC_CODE)
    private String ifscCode;

    @Column(name = Columns.BANK_MMID)
    private String bankMMID;

    @Column(name = Columns.BANK_VPA)
    private String bankVPA;

    @Column(name = Columns.BANK_ACCOUNT_TYPE)
    private String bankAccType;

    @Column(name = Columns.BANK_ACCOUNT_NUMBER)
    private String bankAccNo;

    @Column(name = Columns.BANK_BRANCH)
    private String bankBranch;

    @Column(name = Columns.USER_ID)
    private String userId;

    @Column(name = Columns.PROFILE_IMAGE_PATH)
    private String profileImagePath;

    @Column(name = Columns.SIGNATURE_IMAGE_PATH)
    private String signatureImagePath;

    @Column(name = Columns.BRANCH_ID)
    private Integer branchId;

    @Column(name = Columns.BRANCH_JOINING_DATE)
    private LocalDateTime branchJoiningDate;

    @Column(name = Columns.DEPARTMENT_ID)
    private Integer departmentId;

    @Column(name = Columns.DEPARTMENT_ROLE_ID)
    private Integer departmentRoleId;

    @Column(name = Columns.SUB_DEPARTMENT_ID)
    private Integer subDepartmentId;

    @Column(name = Columns.DESIGNATION_TYPE)
    private String designationType;

    @Column(name = Columns.DESIGNATION_ID)
    private Integer designationId;

    @Column(name = Columns.FUNCTIONAL_TITLE_ID)
    private Integer functionalTitleId;

    @Column(name = Columns.ORGANIZATION_BAND)
    private Integer organizationBand;

    @Column(name = Columns.REPORTING_MANAGER_ID)
    private Integer reportingManagerId;

    @Column(name = Columns.HR_MANAGER_ID)
    private Integer hrManagerId;

    @Column(name = Columns.ACCOUNT_MANAGER_ID)
    private Integer accManagerId;

    @Column(name = Columns.INSERTED_ON)
    private LocalDateTime insertionOn;

    @Column(name = Columns.INSERTED_BY)
    private String insertionBy;

    @Column(name = Columns.UPDATED_ON)
    private LocalDateTime updatedOn;

    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;


}
