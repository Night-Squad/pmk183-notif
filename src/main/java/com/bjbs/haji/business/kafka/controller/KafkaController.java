 package com.bjbs.haji.business.kafka.controller;

 import java.time.LocalDateTime;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;

 import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;

 @RestController
 public class KafkaController {

 	@Value("${kafka-topic}")
 	private String kafkaTopic;

 	@Autowired
 	private KafkaTemplate<String, String> kafkaTemplate;

 	@PostMapping(value = "/repo/setoran-awal/v2/pembayaran", consumes = "application/json", produces = "application/json")
 	public ResponseEntity<Void> sendMessage(@RequestBody SetoranAwalHajiDataKafka message) throws Exception {
 		message.setTimestap(LocalDateTime.now().toString());
 		try {
 			// Sending the message to kafka topic queue
 			System.out.println("sending chat...");
 			System.out.println("chat : " + message.toString());
            kafkaTemplate.send("setoran_awal_incoming", "setoran-awal", message.toString());
 			return new ResponseEntity<>(HttpStatus.OK);
 		} catch (Exception exception) {
 			throw exception;
 		}
 	}
 }
