package com.pmk.notif.services;


import com.pmk.notif.controllers.payloads.NotifReversalPayload;
import com.pmk.notif.kafka.service.KafkaService;
import com.pmk.notif.models.pubsubs.MasterReversalNotif;
import com.pmk.notif.models.pubsubs.RefRevStatus;
import com.pmk.notif.models.va.MasterTx;
import com.pmk.notif.models.va.RefTxType;
import com.pmk.notif.repositories.pubsubs.MasterReversalNotifRepository;
import com.pmk.notif.repositories.va.MasterTxRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReversalTrxService {

    private static final Log log = LogFactory.getLog(ReversalTrxService.class);

    @Autowired
    private MasterTxRepository masterTxRepository;

    @Autowired
    private MasterReversalNotifRepository masterReversalNotifRepo;

    @Autowired
    private GetCurrentTimeService getCurrentTimeService;

    @Autowired
    private KafkaService kafkaService;

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
               //  this.reversalJournal(masterTxChecked);

                // send to kafka




                // update is reversal
            }

        } catch (Exception e) {
            log.error("Error message : "+e.getLocalizedMessage());
            return response;
        }



        return response;
    }

    // send to kafka
    @Transactional
    public ResponseMsg sendToKafka(NotifReversalPayload payload) {
        ResponseMsg response = new ResponseMsg();
        response.setRc("99");
        response.setRm("default error..");

        // initiate save the data
        Timestamp currentTime = getCurrentTimeService.getCurrentTime();
        MasterReversalNotif masterReversalNotif = new MasterReversalNotif();
        masterReversalNotif.setTxReferenceNo(payload.getTxReferenceNo());
        masterReversalNotif.setReversalDate(Timestamp.valueOf(payload.getReversalDate()));
        masterReversalNotif.setVaAccNo(payload.getVaAccNo());
        masterReversalNotif.setCreatedAt(currentTime);
        masterReversalNotif.setCreatedBy("system");
        masterReversalNotif.setActive(true);
        masterReversalNotif.setRefRevStatus(new RefRevStatus(1L));

        try {
            ListenableFuture<SendResult<String, String>> kafkaResponse = kafkaService.sendMessageToKafka(payload);

            kafkaResponse.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    log.error("Data to kafka error send.. "+ex.getMessage());

                    // set status into 2
                    masterReversalNotif.setRefRevStatus(new RefRevStatus(2L));
                    masterReversalNotif.setResultReason(ex.getMessage());


                }

                @Override
                public void onSuccess(SendResult<String, String> stringStringSendResult) {
                    log.info("Data sent to kafka successful...");
                    response.setRm("failed sent to kafka...");

                    response.setRc("00");
                    response.setRm("success sent to kafka...");

                }
            });

            masterReversalNotifRepo.save(masterReversalNotif);

        } catch (Exception e) {
            log.error("Error occurred during sending message to kafka : "+e.getMessage());
            response.setRm(e.getMessage());
        }

        return response;
    }

    public ResponseMsg reversalJournal(List<MasterTx> allMasterTx) {
        ResponseMsg response = new ResponseMsg<>();
        response.setRc("99");
        response.setMessage("default error...");
        try {

            // looping all to journal
            log.info("===doing reversalJurnal...");
            for(int i=0;i<allMasterTx.size();i++) {
                log.info("insert journal seq : "+i);
                log.info("master_tx : "+allMasterTx.get(i));

                MasterTx newJournalMasterTx = allMasterTx.get(i);

                newJournalMasterTx.setCurrentOs(newJournalMasterTx.getLastOs());


                if(newJournalMasterTx.getRefTxType().getId() == 0) {
                    log.info("if debit, than last os will be added by tx amount...");
                    newJournalMasterTx.setLastOs(newJournalMasterTx.getCurrentOs() + newJournalMasterTx.getTxAmount());
                    RefTxType newTxType = new RefTxType();
                    newTxType.setId(1);
                    newJournalMasterTx.setRefTxType(newTxType);
                }

                if(newJournalMasterTx.getRefTxType().getId() == 1) {
                    log.info("if credit, than last os will be deducted by tx amount...");
                    newJournalMasterTx.setLastOs(newJournalMasterTx.getCurrentOs() - newJournalMasterTx.getTxAmount());
                    RefTxType newTxType = new RefTxType();
                    newTxType.setId(0);
                    newJournalMasterTx.setRefTxType(newTxType);
                }

                newJournalMasterTx.setIsReversal(true);

                masterTxRepository.save(newJournalMasterTx);
            }

            log.info("===/doing reversalJurnal");



        } catch (Exception e) {
            log.error("Error in reversal jurnal : "+e.getLocalizedMessage());
        }

        return response;
    }



}
