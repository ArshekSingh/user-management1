package com.sts.finncub.usermanagement.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeBankResponse {

    private String bankAccNo;
    private String ifscCode;
    private String isBankValidated;
    private String bankValidationDate;
    private String bankName;
    private String bankAccType;
    private String bankResponse;
    private String bankBranch;
    private String isNameVerified;
}