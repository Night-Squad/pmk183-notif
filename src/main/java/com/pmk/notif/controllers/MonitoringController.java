package com.pmk.notif.controllers;

import com.pmk.notif.response.ResponseMessage;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.services.MonitoringNotifService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/monitoring", produces = "application/json")
public class MonitoringController {

    private static final Log log = LogFactory.getLog(MonitoringController.class);

    @Autowired
    private MonitoringNotifService monitoringNotifService;

    @GetMapping("/notif")
    public Map<String, Object> getMonitoringNotif(@RequestParam Map<String, String> reqParams) {

        ResponseMsg response = monitoringNotifService.getMonitoringNotifs(reqParams);

        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), null);
        }
    }
}
