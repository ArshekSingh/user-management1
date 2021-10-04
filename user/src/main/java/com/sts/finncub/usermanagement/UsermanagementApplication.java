package com.sts.finncub.usermanagement;

//import com.sts.fincub.authentication.validation.RedisRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@ComponentScan("com.sts")
@EntityScan("com.sts")
@EnableJpaRepositories("com.sts")
public class UsermanagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(UsermanagementApplication.class, args);
	}

}
