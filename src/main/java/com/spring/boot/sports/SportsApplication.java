package com.spring.boot.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@EnableEurekaClient
@EnableScheduling
@EnableFeignClients(basePackages = {"com.spring.boot"})
@SpringBootApplication(scanBasePackages = "com.spring.boot")
@MapperScan("com.spring.boot.sports.mapper")
public class SportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsApplication.class, args);
	}
}
