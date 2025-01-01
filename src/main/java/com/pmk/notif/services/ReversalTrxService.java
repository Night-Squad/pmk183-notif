package com.pmk.notif.services;


import com.pmk.notif.controllers.payloads.NotifReversalPayload;
import com.pmk.notif.models.va.MasterTx;
import com.pmk.notif.repositories.va.MasterTxRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
            LocalDateTime[] getCurrentDate = GetCurrentTimeService.getCurrentDayRange();
            List<MasterTx> masterTxChecked = masterTxRepository.findMasterTxBetween(body.getTxReferenceNo(), getCurrentDate[0], getCurrentDate[1]);

//            log.info("master_tx find master tx between: ");
//            log.info("size : "+masterTxChecked.size());
//            log.info(masterTxChecked);

            if(masterTxChecked.isEmpty()) {
                response.setRc("50");
                response.setStatusId("404");
                response.setMessage("Data transaksi dengan tx_reference_no : "+body.getTxReferenceNo()+" tidak ditemukan");
            }

            if(!masterTxChecked.isEmpty()) {
                // do jurnal


                // update is reversal
            }

        } catch (Exception e) {
            log.error("Error message : "+e.getLocalizedMessage());
            return response;
        }



        return response;
    }

}
