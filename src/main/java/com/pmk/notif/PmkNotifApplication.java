package com.pmk.notif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication()
//@EnableEurekaClient
@Configuration
public class PmkNotifApplication {

	public static void main(String[] args) {
        System.out.println("Testing cicd..");
		SpringApplication.run(PmkNotifApplication.class, args);
	}

}