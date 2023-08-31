package com.bjbs.haji.business.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;

public class MessageListener {

	@Value("${kafka-topic}")
    private String kafkaTopic;

    @Value("${kafka-group-id}")
    private String kafkaGroupId;

   @Autowired
   SimpMessagingTemplate template;

    @KafkaListener(
            topics = "setoran_awal_incoming",
            groupId = "setoran-awal"
    )
   public void listen(SetoranAwalHajiDataKafka message) {
       System.out.println("sending via kafka listener..");
       template.convertAndSend("/topic/group-setoran-awal-incoming", message);
   }
}
