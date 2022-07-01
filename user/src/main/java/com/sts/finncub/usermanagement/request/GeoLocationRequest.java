package com.sts.finncub.usermanagement.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class GeoLocationRequest {
	
	
//	@NotNull
//	@Size(min = 1,message = "Atleast one geo location set is mandatory")
//	@Valid
	List<UserLocationTrackerRequest> userLocationTrackerRequests;

}
