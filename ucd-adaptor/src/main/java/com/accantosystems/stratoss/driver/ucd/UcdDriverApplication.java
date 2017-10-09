package com.accantosystems.stratoss.driver.ucd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.accantosystems.stratoss.driver.ucd.config.UCDProperties;

@SpringBootApplication
@EnableConfigurationProperties(UCDProperties.class)
public class UcdDriverApplication {

	public static void main(String[] args) {
		SpringApplication.run(UcdDriverApplication.class, args);
	}
}
