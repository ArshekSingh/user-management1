package com.sts.fincub.usermanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = Organisation.Columns.TABLE_NAME)
public class Organisation {
    public interface Columns{
        String TABLE_NAME = "ORGANIZATION_MASTER";
        String GROUP_ID = "GROUP_ID";
        String ORG_ID = "ORG_ID";
        String ORG_CODE = "ORG_CODE";
        String ORG_NAME = "ORG_NAME";
        String HO_ADDRESS_1 = "HO_ADDRESS_1";
        String HO_ADDRESS_2 ="HO_ADDRESS_2";
        String HO_ADDRESS_3 = "HO_ADDRESS_3";
        String HO_PINCODE = "HO_PINCODE";
        String HO_STATE_ID = "HO_STATE_ID";
        String HO_COUNTRY = "HO_COUNTRY";
        String REG_ADDRESS_1 = "REG_ADDRESS_1";
        String REG_ADDRESS_2 = "REG_ADDRESS_2";
        String REG_ADDRESS_3 = "REG_ADDRESS_3";
        String REG_PINCODE = "REG_PINCODE";
        String REG_STATE_ID = "REG_STATE_ID";
        String LANDLINE_1 = "LANDLINE_1";
        String LANDLINE_2 = "LANDLINE_2";
        String MOBILE_1 = "MOBILE_1";
        String MOBILE_2 = "MOBILE_2";
        String FAX = "FAX";
        String EMAIL  = "EMAIL";
        String WEBSITE = "WEBSITE";
        String STATUS = "STATUS";
        String FY_START_MONTH = "FY_START_MONTH";
        String PROVISIONAL_COMPANY = "PROVISIONAL_COMPANY";
        String LANGUAGE = "LANGUAGE";
        String CURRENCY_CODE = "CURRENCY_CODE";
        String PAN_NUMBER = "PAN_NUMBER";
        String VAT_NUMBER = "VAT_NUMBER";
        String GST_NUMBER = "GST_NUMBER";
        String TAX_REG_NUMBER = "TAX_REG_NUMBER";
        String TERMS_CODE = "TERMS_CODE";
        String PMTTYPE_LOCAL = "PMTTYPE_LOCAL";
        String PMTTYPE_FOREIGN = "PMTTYPE_FOREIGN";
        String LOGO_PATH = "LOGO_PATH";
        String WEB_APP_THEME = "WEB_APP_THEME";
        String MOB_APP_THEME = "MOB_APP_THEME";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON = "UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
    }


    @Column(name = Columns.GROUP_ID)
    private Long groupId;

    @Id
    @Column(name = Columns.ORG_ID)
    private Long orgId;

    @Column(name = Columns.ORG_CODE)
    private String orgCode;

    @Column(name = Columns.ORG_NAME)
    private String orgName;

    @Column(name = Columns.HO_ADDRESS_1)
    private String hoAddress1;

    @Column(name = Columns.HO_ADDRESS_2)
    private String hoAddress2;

    @Column(name = Columns.HO_ADDRESS_3)
    private String hoAddress3;

    @Column(name = Columns.HO_PINCODE)
    private Integer hoPinCode;

    @Column(name = Columns.HO_STATE_ID)
    private String hoStateId;

    @Column(name = Columns.HO_COUNTRY)
    private String hoCountry;

    @Column(name = Columns.REG_ADDRESS_1)
    private String regAddress1;

    @Column(name = Columns.REG_ADDRESS_2)
    private String regAddress2;

    @Column(name = Columns.REG_ADDRESS_3)
    private String regAddress3;

    @Column(name = Columns.REG_PINCODE)
    private Long regPincode;

    @Column(name = Columns.REG_STATE_ID)
    private String regStateId;

    @Column(name = Columns.LANDLINE_1)
    private String landline1;

    @Column(name = Columns.LANDLINE_2)
    private String landline2;

    @Column(name = Columns.MOBILE_1)
    private String mobile1;

    @Column(name = Columns.MOBILE_2)
    private String mobile2;

    @Column(name = Columns.FAX)
    private String fax;

    @Column(name = Columns.EMAIL)
    private String email;

    @Column(name = Columns.WEBSITE)
    private Long website;

    @Column(name = Columns.STATUS)
    private String status;

    @Column(name = Columns.FY_START_MONTH)
    private String fyStartMonth;

    @Column(name = Columns.PROVISIONAL_COMPANY)
    private Long provisionalCompany;

    @Column(name = Columns.LANGUAGE)
    private String language;

    @Column(name = Columns.CURRENCY_CODE)
    private String currencyCode;

    @Column(name = Columns.PAN_NUMBER)
    private String panNumber;

    @Column(name = Columns.VAT_NUMBER)
    private String vatNumber;

    @Column(name = Columns.GST_NUMBER)
    private String gstNumber;

    @Column(name = Columns.TAX_REG_NUMBER)
    private String taxRegNumber;

    @Column(name = Columns.TERMS_CODE)
    private String termsCode;

    @Column(name = Columns.PMTTYPE_LOCAL)
    private String pmtTypeLocal;

    @Column(name = Columns.PMTTYPE_FOREIGN)
    private String pmtTypeForeign;

    @Column(name = Columns.LOGO_PATH)
    private String logoPath;

    @Column(name = Columns.WEB_APP_THEME)
    private String webAppTheme;

    @Column(name = Columns.MOB_APP_THEME)
    private String mobAppTheme;

    @Column(name = User.Columns.INSERTED_ON)
    private LocalDate insertedOn;

    @Column(name = User.Columns.INSERTED_BY)
    private String insertedBy;

    @Column(name = User.Columns.UPDATED_ON)
    private LocalDate updatedOn;

    @Column(name = User.Columns.UPDATED_BY)
    private String updatedBy;









}
