package com.thinhreal.applestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource("classpath:application-security.properties")
@EnableScheduling
public class ApplestoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplestoreApplication.class, args);
	}

}
