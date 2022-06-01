package com.sts.finncub.usermanagement.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLocationTrackerRequest {

	private static final String YYYY_MM_DD_HH_MM_SS = "((((19|20)([2468][048]|[13579][26]|0[48])|2000)-02-29|((19|20)[0-9]{2}-(0[4678]|1[02])-(0[1-9]|[12][0-9]|30)|(19|20)[0-9]{2}-(0[1359]|11)-(0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}-02-(0[1-9]|1[0-9]|2[0-8])))\\s([01][0-9]|2[0-3]):([012345][0-9]):([012345][0-9]))";

	@NotNull
	@Digits(integer = 3,fraction = 8)
	private BigDecimal lattitude;

	@NotNull
	@Digits(integer = 3,fraction = 8)
	private BigDecimal longitude;
	
	@NotEmpty
	@Pattern(regexp = YYYY_MM_DD_HH_MM_SS, message = "Time stamp should be in yyyy-MM-dd HH:mm:ss format")
	private String trackDateTime;
}
