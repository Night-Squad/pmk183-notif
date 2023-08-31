package com.bjbs.haji.business.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;


@EnableKafka
@Configuration
public class ProcedurConfiguration {

	 @Value("${kafka-host}")
     private String kafkaHost;
	 
	  @Bean
	     public ProducerFactory<String, SetoranAwalHajiDataKafka> producerFactory() {
	         return new DefaultKafkaProducerFactory<>(producerConfigurations());
	     }
	  
	  @Bean
	     public Map<String, Object> producerConfigurations() {
	         Map<String, Object> configurations = new HashMap<>();
	         configurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
	         configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	         configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	         return configurations;
	     }

	     @Bean
	     public KafkaTemplate<String, SetoranAwalHajiDataKafka> kafkaTemplate() {
	         return new KafkaTemplate<>(producerFactory());
	     }
}
