package com.bjbs.haji.business;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import com.io.iona.springboot.storage.StorageLocationProperties;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.bjbs.haji, com.io.iona.springboot.storage"})
@EnableConfigurationProperties(StorageLocationProperties.class)
@EnableEurekaClient
@Configuration
@EnableScheduling
public class HajiSpringbootApplication {

	public static void main(String[] args) {
        System.out.println("Testing cicd...");
		SpringApplication.run(HajiSpringbootApplication.class, args);
	}

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}