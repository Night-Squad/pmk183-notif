package com.pmk.notif.services;


import com.pmk.notif.controllers.payloads.NotifReversalPayload;
import com.pmk.notif.response.ResponseMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class ReversalTrxService {

    private static final Log log = LogFactory.getLog(ReversalTrxService.class);

    public ResponseMsg notifReversalTrx(NotifReversalPayload body) {
        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        return response;
    }

}
