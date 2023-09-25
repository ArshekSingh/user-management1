package com.sts.finncub.usermanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPDetails {
    private String type;
    private String ip;
}