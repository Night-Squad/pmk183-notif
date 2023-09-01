package com.bjbs.haji.business.kafka.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {
    @KafkaListener(
            topics = "setoran_awal_incoming",
            groupId = "setoran-awal"
    )
    void listener(String data) {
        System.out.println("Listener received: " + data);
    }
}
