package com.sts.finncub.usermanagement.config;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@ConfigurationProperties("mobile.app")
public class MobileAppConfig {
	
	@Getter
	private final HashMap<String, String> config = new HashMap<>();
}
