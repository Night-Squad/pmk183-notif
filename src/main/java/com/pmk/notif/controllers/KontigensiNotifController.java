package com.pmk.notif.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pmk.notif.controllers.payloads.KontigensiNotifPayload;
import com.pmk.notif.kafka.service.KafkaService;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.response.ResponseMessage;
import com.pmk.notif.response.ResponseMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class KontigensiNotifController {

    private static final Log log = LogFactory.getLog(KontigensiNotifController.class);

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private MasterApiNotifRepository masterApiNotifRepository;

    @PostMapping("/resend")
    public Map<String, Object> resendNotifToKafka(@RequestBody KontigensiNotifPayload body) throws JsonProcessingException {

        MasterApiNotif masterApiNotif = masterApiNotifRepository.findFirstById(body.getApiNotifId());

        //handle infinite recursive
        masterApiNotif.setRefChannel(null);
        masterApiNotif.setMasterProduceHists(null);

        ResponseMsg response = kafkaService.resendMessageToKafka(body.getApiNotifId(), masterApiNotif);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), response.getData());
        }
    }
}
