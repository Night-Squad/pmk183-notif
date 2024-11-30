package com.pmk.notif;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@SpringBootApplication()
@Configuration
public class PmkNotifApplication {

	public static void main(String[] args) {
        System.out.println("Testing cicd - 20230817 - v4 ");
		SpringApplication.run(PmkNotifApplication.class, args);
	}

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
