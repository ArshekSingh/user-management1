package com.sts.finncub.usermanagement.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserLocationTrackerRequest {

    private static final String YYYY_MM_DD_HH_MM_SS = "((((19|20)([2468][048]|[13579][26]|0[48])|2000)-02-29|((19|20)[0-9]{2}-(0[4678]|1[02])-(0[1-9]|[12][0-9]|30)|(19|20)[0-9]{2}-(0[1359]|11)-(0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}-02-(0[1-9]|1[0-9]|2[0-8])))\\s([01][0-9]|2[0-3]):([012345][0-9]):([012345][0-9]))";
    private BigDecimal lattitude;
    private BigDecimal longitude;
    private String trackDateTime;
    private Object deviceInfo;
    private String trackType;
}