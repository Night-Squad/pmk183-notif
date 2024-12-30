package com.pmk.notif.services;


import com.pmk.notif.controllers.payloads.NotifReversalPayload;
import com.pmk.notif.repositories.va.MasterTxRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReversalTrxService {

    private static final Log log = LogFactory.getLog(ReversalTrxService.class);

    @Autowired
    MasterTxRepository masterTxRepository;

    @Autowired
    GetCurrentTimeService getCurrentTimeService;

    public ResponseMsg notifReversalTrx(NotifReversalPayload body) {
        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        try {

            // Check if tx_reference_no is available


            masterTxRepository.findMasterTxBetween(body.getTxReferenceNod(), "", "");

        } catch (Exception e) {
            log.error("Error message : "+e.getLocalizedMessage());
            return response;
        }



        return response;
    }

}
