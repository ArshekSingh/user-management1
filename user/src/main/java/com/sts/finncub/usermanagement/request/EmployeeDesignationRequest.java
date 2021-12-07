package com.sts.finncub.usermanagement.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDesignationRequest {

    private Long empDesignationId;
    private String empDesignationName;
    private String empDesignationType;
    private String status;
    private Long confNoticePeriod;
    private Long onProbNoticePeriod;


}
