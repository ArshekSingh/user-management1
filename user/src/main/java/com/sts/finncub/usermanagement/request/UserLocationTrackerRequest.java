package com.sts.finncub.usermanagement.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLocationTrackerRequest {

	@NotNull
	@Digits(integer = 3,fraction = 8)
	private BigDecimal lattitude;

	@NotNull
	@Digits(integer = 3,fraction = 8)
	private BigDecimal longitude;
}
