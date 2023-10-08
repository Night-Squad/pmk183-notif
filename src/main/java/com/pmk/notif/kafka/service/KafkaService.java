package com.pmk.notif.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pmk.notif.Constants;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.models.pubsubs.MasterProduceHist;
import com.pmk.notif.models.pubsubs.RefNotifCode;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.repositories.pubsubs.MasterProduceHistRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaService {

    private static final Log log = LogFactory.getLog(KafkaService.class);

    @Value("${kafka-topic}")
 	private String kafkaTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
 	private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private GetCurrentTimeService getCurrentTimeService;

    @Autowired
    private MasterProduceHistRepository masterProduceHistRepository;

    @Autowired
        private MasterApiNotifRepository masterApiNotifRepository;

    public ListenableFuture<SendResult<String, String>> sendMessageToKafka(Object message) {
        log.info("Sending message to kafka...");
        log.info(String.format("Kafka Host : %s ; Data sent : %s", bootstrapServers, new Gson().toJson(message)));
        return kafkaTemplate.send(kafkaTopic, Constants.KAFKA_PRODUCER_TOPIC_KEY, new Gson().toJson(message));
    }

    public ResponseMsg resendMessageToKafka(Long id, Object message) throws JsonProcessingException {

        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        log.info("Sending message to kafka...");
        log.info(String.format("Kafka Host : %s ; Data sent : %s", bootstrapServers, new Gson().toJson(message)));
        try {
            kafkaTemplate.send(kafkaTopic, Constants.KAFKA_PRODUCER_TOPIC_KEY, new Gson().toJson(message)).get();

            response.setRc("00");
            response.setRm("Notif sedang dicoba dikirim ulang");

            this.insertMasterProduceHist(null, id, message, true);

            this.updateMasterApiNotifSentStatus(id, getCurrentTimeService.getCurrentTime());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            response.setRc("99");
            response.setRm("Notif gagal dikirim ulang");

            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getLocalizedMessage();

            this.insertMasterProduceHist(errorMessage, id, message, false);

            this.updateMasterApiNotifSentFailedStatus(id);
        }

        response.setData(message);

        return response;
    }

    private void insertMasterProduceHist(String errorMessage, Long id, Object message, Boolean isSuccess) {
        MasterProduceHist masterProduceHist = new MasterProduceHist();
        MasterApiNotif masterApiNotif = new MasterApiNotif();
        masterApiNotif.setId(id);
        masterProduceHist.setMasterApiNotif(masterApiNotif);
        masterProduceHist.setMessage(new Gson().toJson(message));
        masterProduceHist.setTopic(kafkaTopic);
        masterProduceHist.setKafkaHost(bootstrapServers);
        if(isSuccess) {
            masterProduceHist.setResponse("success");
        } else {
            masterProduceHist.setResponse(errorMessage);
        }
        masterProduceHist.setCreatedBy("system");
        masterProduceHist.setCreatedAt(getCurrentTimeService.getCurrentTime());
        RefNotifCode refNotifCode = new RefNotifCode();
        refNotifCode.setId((long) 1);
        masterProduceHist.setRefNotifCode(refNotifCode);
        masterProduceHistRepository.save(masterProduceHist);
    }
    private void updateMasterApiNotifSentStatus(Long id, Timestamp currentTime) {
        masterApiNotifRepository.updateSentStatus(id, currentTime);
    }

    private void updateMasterApiNotifSentFailedStatus(Long id) {
        masterApiNotifRepository.updateSentFailedStatus(id);
    }
}
