package com.tookscan.tookscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories(basePackages = "com.tookscan.tookscan.*.repository.mysql")
@EnableRedisRepositories(basePackages = "com.tookscan.tookscan.*.repository.redis")
@EnableScheduling
@SpringBootApplication
public class TookscanApplication {

	public static void main(String[] args) {
		SpringApplication.run(TookscanApplication.class, args);
	}

}
