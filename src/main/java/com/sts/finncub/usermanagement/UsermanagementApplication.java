package com.sts.finncub.usermanagement;

//import com.sts.fincub.authentication.validation.RedisRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class UsermanagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(UsermanagementApplication.class, args);
	}

}
