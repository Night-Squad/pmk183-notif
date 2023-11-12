package com.pmk.notif.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pmk.notif.controllers.payloads.KontigensiNotifPayload;
import com.pmk.notif.kafka.service.KafkaService;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.response.ResponseMessage;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.services.KontigensiNotifService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class KontigensiNotifController {

    private static final Log log = LogFactory.getLog(KontigensiNotifController.class);

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private MasterApiNotifRepository masterApiNotifRepository;

    @Autowired
    private KontigensiNotifService kontigensiNotifService;

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

    @PostMapping("/kontigensi-notif")
    public Map<String, Object> readExcelNotif(@RequestParam("file") MultipartFile file) {

        ResponseMsg response = kontigensiNotifService.readExcelNotif(file);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), response.getData());
        }
    }
}
