 package com.bjbs.haji.business.kafka.controller;

 import java.time.LocalDateTime;

 import com.bjbs.haji.business.views.dtos.kafka.ResponseSetoranAwalDataKafka;
 import com.google.gson.Gson;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;

 @RestController
 public class KafkaController {

 	@Value("${kafka-topic}")
 	private String kafkaTopic;

 	@Autowired
 	private KafkaTemplate<String, String> kafkaTemplate;

 	@PostMapping(value = "/repo/setoran-awal/v2/pembayaran", consumes = "application/json", produces = "application/json")
 	public Object sendMessage(@RequestBody SetoranAwalHajiDataKafka message) throws Exception {
        ResponseSetoranAwalDataKafka responseSetoranAwalDataKafka = new ResponseSetoranAwalDataKafka();
        responseSetoranAwalDataKafka.setResponseCode("00");
        responseSetoranAwalDataKafka.setResponseMessage("Request setoran awal sedang di proses");

 		message.setTimestap(LocalDateTime.now().toString());

        responseSetoranAwalDataKafka.setData(message);
 		try {
 			// Sending the message to kafka topic queue
 			System.out.println("sending chat...");
 			System.out.println("chat : " + message.toString());
            kafkaTemplate.send("setoran_awal_incoming", "setoran-awal", new Gson().toJson(responseSetoranAwalDataKafka));
			return ResponseEntity.ok().body(new Response("00", message, "Request setoran awal sedang di proses"));
		} catch (Exception exception) {
 			throw exception;
 		}
 	}
 }
