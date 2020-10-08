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

@SpringBootApplication(scanBasePackages={"com.bjbs.haji, com.io.iona.springboot.storage"})
@EnableConfigurationProperties(StorageLocationProperties.class)
@EnableEurekaClient
@Configuration
public class HajiSpringbootApplication {

	public static void main(String[] args) {
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