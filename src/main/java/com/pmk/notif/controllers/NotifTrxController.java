package com.pmk.notif.controllers;

import com.pmk.notif.controllers.payloads.NotifTrxPayload;
import com.pmk.notif.controllers.payloads.ResendNotifTrxPayload;
import com.pmk.notif.response.ResponseMessage;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.services.MonitoringNotifService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class NotifTrxController {

    private static final Log log = LogFactory.getLog(NotifTrxController.class);

    @Autowired
    private MonitoringNotifService monitoringNotifService;

    @PostMapping("/notif-trx")
    public Map<String, Object> saveNotifTrx(@RequestBody NotifTrxPayload body) {

        ResponseMsg response = monitoringNotifService.saveNotifTrx(body);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), null);
        }
    }

    @PostMapping("/notif-trx/re-push")
    public Map<String, Object> repushNotifTrx(@RequestBody ResendNotifTrxPayload body) {

        ResponseMsg response = monitoringNotifService.resendNotifTrx(body);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), null);
        }
    }

    @GetMapping("/master-api-notif")
    public Map<String, Object> getNotifTrxs(@RequestParam Map<String, String> reqParams) {

        ResponseMsg response = monitoringNotifService.getNotifTrxs(reqParams);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), null);
        }
    }

}
