package com.pmk.notif.kafka.service;

import com.google.gson.Gson;
import com.pmk.notif.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaService {

    private static final Log log = LogFactory.getLog(KafkaService.class);

    @Value("${kafka-topic}")
 	private String kafkaTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
 	private KafkaTemplate<String, String> kafkaTemplate;

    public ListenableFuture<SendResult<String, String>> sendMessageToKafka(Object message) {
        log.info("Sending message to kafka...");
        log.info(String.format("Kafka Host : %s ; Data sent : %s", bootstrapServers, new Gson().toJson(message)));
        return kafkaTemplate.send(kafkaTopic, Constants.KAFKA_PRODUCER_TOPIC_KEY, new Gson().toJson(message));
    }
}
