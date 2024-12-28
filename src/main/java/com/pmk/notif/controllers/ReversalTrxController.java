package com.pmk.notif.controllers;


import com.pmk.notif.controllers.payloads.NotifReversalPayload;
import com.pmk.notif.response.ResponseMessage;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.services.ReversalTrxService;
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
public class ReversalTrxController {
    private static final Log log = LogFactory.getLog(ReversalTrxController.class);

    @Autowired
    ReversalTrxService reversalTrxService;

    @PostMapping("/notif-reversal-trx")
    public Map<String, Object> notifReversalTrx(@RequestBody NotifReversalPayload body) {

        ResponseMsg response = reversalTrxService.notifReversalTrx(body);
        if (response.getRc().equals("00")) {
            return new ResponseMessage().success(response.getRc(), 200, response.getRm(), response.getData());
        } else {
            return new ResponseMessage().success(response.getRc(), 400, response.getRm(), null);
        }
    }

}
