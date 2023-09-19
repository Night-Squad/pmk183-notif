package com.pmk.notif.kafka.service;

import com.google.gson.Gson;
import com.pmk.notif.Constants;
import com.pmk.notif.services.MonitoringNotifService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final Log log = LogFactory.getLog(KafkaService.class);

    @Value("${kafka-topic}")
 	private String kafkaTopic;

    @Autowired
 	private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageToKafka(Object message) {
        log.info(String.format("Sending message to kafka : %s", message.toString()));
        kafkaTemplate.send(kafkaTopic, Constants.KAFKA_PRODUCER_TOPIC_KEY, new Gson().toJson(message));
    }
}
