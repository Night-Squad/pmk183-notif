 package com.bjbs.haji.business.kafka.controller;

 import java.time.LocalDateTime;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.messaging.handler.annotation.MessageMapping;
 import org.springframework.messaging.handler.annotation.Payload;
 import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;

 import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;

 @RestController
 public class KafkaController {

// 	@Autowired
// 	SimpMessagingTemplate template;

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
// 			template.convertAndSend("/topic/setoran_awal_incoming", message);
// 			kafkaTemplate.send("setoran_awal_incoming", message.toString());
            kafkaTemplate.send("setoran_awal_incoming", "setoran-awal", message.toString());
 			return new ResponseEntity<>(HttpStatus.OK);
 		} catch (Exception exception) {
 			throw exception;
 		}
 	}

 //  -------------- WebSocket API ----------------
 	// @MessageMapping("/sendMessage")
 	// @SendTo("/topic/setoran_awal_incoming")
 	// public SetoranAwalHajiDataKafka broadcastGroupMessage(@Payload SetoranAwalHajiDataKafka message) {
 	// 	// Sending this message to all the subscribers
 	// 	return message;
 	// }
 }
