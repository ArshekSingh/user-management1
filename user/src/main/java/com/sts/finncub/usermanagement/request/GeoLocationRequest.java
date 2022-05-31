package com.sts.finncub.usermanagement.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class GeoLocationRequest {
	
	@Valid
	@NotNull
	@Size(min = 0,message = "Atleast one geo location set is mandatory")
	List<UserLocationTrackerRequest> userLocationTrackerRequests;

}
