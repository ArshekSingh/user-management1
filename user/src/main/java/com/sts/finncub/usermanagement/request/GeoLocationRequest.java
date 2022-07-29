package com.sts.finncub.usermanagement.request;

import lombok.Data;

import java.util.List;

@Data
public class GeoLocationRequest {
    List<UserLocationTrackerRequest> userLocationTrackerRequests;
}