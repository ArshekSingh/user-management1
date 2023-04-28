package com.sts.finncub.usermanagement.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
}