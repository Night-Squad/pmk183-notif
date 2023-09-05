package com.bjbs.haji.business;

import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import com.io.iona.springboot.storage.StorageLocationProperties;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication(scanBasePackages={"com.bjbs.haji, com.io.iona.springboot.storage"})
@EnableConfigurationProperties(StorageLocationProperties.class)
@EnableEurekaClient
@Configuration
@EnableScheduling
public class HajiSpringbootApplication {

	public static void main(String[] args) {
        System.out.println("Testing cicd..");
		SpringApplication.run(HajiSpringbootApplication.class, args);
	}

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

//     @Bean
//     CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
//         return args -> {
//             System.out.println("let gooo");
//             for (int i = 0; i < 100; i++) {
//                 kafkaTemplate.send("setoran_awal_incoming", "Alhamdulillah " + i);
//             }
//         };
//     }

//     @Bean
//     CommandLineRunner commandLineRunnerAPI(KafkaTemplate<String, SetoranAwalHajiDataKafka> kafkaTemplate) {
//         return args -> {
//             System.out.println("let gooo");
//             SetoranAwalHajiDataKafka message = new SetoranAwalHajiDataKafka();

//             message.setTimestap(LocalDateTime.now().toString());
//             try {
//                 // Sending the message to kafka topic queue
//                 System.out.println("sending chat...");
//                 System.out.println("chat : " + message.toString());
// // 			template.convertAndSend("/topic/setoran_awal_incoming", message);
// //                kafkaTemplate.send("setoran_awal_incoming", message.toString());
//                 kafkaTemplate.send("setoran_awal_incoming", "setoran-awal", message);
//             } catch (Exception exception) {
//                 throw exception;
//             }
//         };
//     }
}