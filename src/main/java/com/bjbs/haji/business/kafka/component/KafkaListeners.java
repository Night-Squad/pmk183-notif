package com.bjbs.haji.business.kafka.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {
    @KafkaListener(
            topics = "setoran_awal_incoming",
            groupId = "setoran-awal",
            topicPartitions = {
                    @TopicPartition(topic = "setoran_awal_incoming", partitions = { "0", "1", "2","3","4","5","6","7" })
            }
    )
    void listener(String data) {
        System.out.println("Listener received: " + data);
    }
}
