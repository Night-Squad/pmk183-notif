package com.bjbs.haji.business.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    
     @Bean
     public NewTopic setoranAwalIncomingTopic() {
		return TopicBuilder.name("setoran_awal_incoming").build();
     }
}
