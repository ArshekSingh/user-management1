package com.sts.finncub.usermanagement.config;

import com.sts.finncub.core.config.RedisConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedisConfig.class)
public class AuthRedisConfig {
}
