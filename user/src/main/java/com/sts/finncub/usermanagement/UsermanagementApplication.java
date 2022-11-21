package com.sts.finncub.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan({"com.sts"})
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@ComponentScan("com.sts.finncub")
@EntityScan("com.sts.finncub")
@EnableJpaRepositories("com.sts.finncub")
@EnableJpaAuditing
@PropertySources({
    @PropertySource("classpath:application-app-config-${spring.profiles.active}.properties")
})
public class UsermanagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsermanagementApplication.class, args);
    }
}