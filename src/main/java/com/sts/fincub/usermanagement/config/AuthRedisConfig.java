package com.sts.fincub.usermanagement.config;

//import com.sts.fincub.authentication.config.RedisConfig;
import com.sts.fincub.authentication.config.RedisConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

@Configuration
@Import(RedisConfig.class)
public class AuthRedisConfig {
}
