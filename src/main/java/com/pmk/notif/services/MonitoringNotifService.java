package com.pmk.notif.services;

import com.google.gson.Gson;
import com.pmk.notif.Constants;
import com.pmk.notif.controllers.payloads.NotifTrxPayload;
import com.pmk.notif.dtos.MasterApiNotifDTO;
import com.pmk.notif.kafka.service.KafkaService;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.models.pubsubs.MasterProduceHist;
import com.pmk.notif.models.pubsubs.RefChannel;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.repositories.pubsubs.MasterProduceHistRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MonitoringNotifService {

    private static final Log log = LogFactory.getLog(MonitoringNotifService.class);

    @Autowired
    private MasterApiNotifRepository masterApiNotifRepository;

    @Autowired
    private MasterProduceHistRepository masterProduceHistRepository;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private GetCurrentTimeService getCurrentTimeService;

    @Value("${kafka-topic}")
    private String kafkaTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Transactional
    public ResponseMsg saveNotifTrx(NotifTrxPayload body) {

        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        try {

            MasterApiNotif masterApiNotif = new MasterApiNotif();
            masterApiNotif.setVaAccNo(body.getVaAccNo());
            masterApiNotif.setTxAmount(body.getTxAmount());
            masterApiNotif.setTxReferenceNo(body.getTxReferenceNo());
            masterApiNotif.setCreatedAt(Timestamp.valueOf(body.getCreatedAt()));
            masterApiNotif.setCreatedBy(body.getCreatedBy());
            masterApiNotif.setTrxTime(Timestamp.valueOf(body.getTrxTime()));
            RefChannel refChannel = new RefChannel();
            refChannel.setId((long) 1);
            masterApiNotif.setRefChannel(refChannel);
            masterApiNotif.setCompanyId(body.getCompanyId());
            masterApiNotif.setIsActive(true);
            masterApiNotif.setSent(null);
            masterApiNotif.setSentAt(null);
            masterApiNotif.setReceived(null);
            masterApiNotif.setReceivedAt(null);
            masterApiNotif.setSentFailed(null);

            MasterApiNotif savedMasterApiNotif = masterApiNotifRepository.save(masterApiNotif);

            //send data to kafka
            try {
                ListenableFuture<SendResult<String, String>> kafkaResponse = kafkaService.sendMessageToKafka(masterApiNotif);

                kafkaResponse.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        //update sent false & sent_failed true
                        savedMasterApiNotif.setSent(false);
                        savedMasterApiNotif.setSentFailed(true);
                        MasterApiNotif updatedMasterApiNotif = masterApiNotifRepository.save(savedMasterApiNotif);

                        //insert to masterProduceHist
                        insertMasterProduceHist(ex, updatedMasterApiNotif.getId(), masterApiNotif, false);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        //update sent true && sent_at jam terkirim
                        savedMasterApiNotif.setSent(true);
                        savedMasterApiNotif.setSentAt(getCurrentTimeService.getCurrentTime());
                        MasterApiNotif updatedMasterApiNotif = masterApiNotifRepository.save(savedMasterApiNotif);

                        //insert to masterProduceHist
                        insertMasterProduceHist(null, updatedMasterApiNotif.getId(), masterApiNotif, true);
                    }
                });
            } catch (Exception e) {
                log.error("Error occured when sending message to kafka : " + e.getMessage());
            }

            response.setRc("00");
            response.setRm("OK");
            response.setData(body);

        } catch (Exception e) {
            e.printStackTrace();
            response.setRc("99");
            response.setRm("Error Occured while insert notif trx data");
        }

        return response;
    }

    private void insertMasterProduceHist(Throwable ex, Long id, MasterApiNotif masterApiNotif, Boolean isSuccess) {
        MasterProduceHist masterProduceHist = new MasterProduceHist();
        masterProduceHist.setApiNotifId(id);
        masterProduceHist.setMessage(new Gson().toJson(masterApiNotif));
        masterProduceHist.setTopic(kafkaTopic);
        masterProduceHist.setKafkaHost(bootstrapServers);
        if(isSuccess) {
            masterProduceHist.setResponse("success");
        } else {
            masterProduceHist.setResponse(ex.getMessage() != null ? ex.getMessage() : ex.getLocalizedMessage());
        }
        masterProduceHist.setCreatedBy("system");
        masterProduceHist.setCreatedAt(getCurrentTimeService.getCurrentTime());
        masterProduceHistRepository.save(masterProduceHist);
    }

    public ResponseMsg getMonitoringNotifs(Map<String, String> params) {

        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        int pageNo = Integer.parseInt(params.get(Constants.PAGINATION_PAGE_NO) != null ? params.get(Constants.PAGINATION_PAGE_NO) : "0" );
        int pageSize = Integer.parseInt(params.get(Constants.PAGINATION_PAGE_SIZE) != null ? params.get(Constants.PAGINATION_PAGE_SIZE) : "100");
        String orderBy = params.get(Constants.PAGINATION_ORDER_BY);
        String orderDirection = params.get(Constants.PAGINATION_ORDER_DIRECTION);
        String startDate = params.get(Constants.PAGINATION_FILTER_START_DATE);
        String endDate = params.get(Constants.PAGINATION_FILTER_END_DATE);
        String searchBy = params.get(Constants.PAGINATION_FILTER_SEARCH_BY);
        String searchValue = params.get(Constants.PAGINATION_FILTER_SEARCH_VALUE);

        try {

            List<MasterApiNotif> masterApiNotifs = new ArrayList<>();
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            if (orderBy != null && orderDirection != null) {
                Sort.Direction direction = null;
                switch (orderDirection) {
                    case "asc":
                        direction = Sort.Direction.ASC;
                        break;
                    case "desc":
                        direction = Sort.Direction.DESC;
                        break;
                }
                pageable = PageRequest.of(pageNo, pageSize, direction, orderBy);
            }

            Page<MasterApiNotif> resultDataSet = null;

            if (startDate != null && endDate != null) {

                // format date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date tglAwalFormated = formatter.parse(startDate + " 00:00:00");
                Date tglAkhirFormated = formatter.parse(endDate + " 23:59:59");

                if (searchBy != null && searchValue != null) {

                    switch (searchBy) {
                        case "vaAccNo":
                            resultDataSet = masterApiNotifRepository.findByVaAccNoContainingAndTrxTimeBetweenAndSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, true, pageable);
                            break;
                        case "txAmount":
                            resultDataSet = masterApiNotifRepository.findByTxAmountAndTrxTimeBetweenAndSentAndReceived(Long.parseLong(searchValue),
                                    tglAwalFormated, tglAkhirFormated, true, pageable);
                            break;
                        case "txReferenceNo":
                            resultDataSet = masterApiNotifRepository.findByTxReferenceNoContainingAndTrxTimeBetweenAndSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, true, pageable);
                            break;
                        case "companyId":
                            resultDataSet = masterApiNotifRepository.findByCompanyIdAndTrxTimeBetweenAndSentAndReceived(Integer.parseInt(searchValue),
                                    tglAwalFormated, tglAkhirFormated, true, pageable);
                            break;
                    }
                    // add case to add another filter

                } else {
                    resultDataSet = masterApiNotifRepository.findByTrxTimeBetweenAndSentAndReceived(tglAwalFormated, tglAkhirFormated,
                            true, pageable);
                }
            } else {
                resultDataSet = masterApiNotifRepository.findBySentAndReceived(true, pageable);
            }

            if (resultDataSet != null) {
                masterApiNotifs = resultDataSet.getContent();
            }

            ModelMapper modelMapper = new ModelMapper();

            List<MasterApiNotifDTO> masterApiNotifDTOS = new ArrayList<>();
            for(MasterApiNotif masterApiNotif : masterApiNotifs) {
                MasterApiNotifDTO masterApiNotifDTO = new MasterApiNotifDTO();
                masterApiNotifDTO = modelMapper.map(masterApiNotif, MasterApiNotifDTO.class);
                masterApiNotifDTOS.add(masterApiNotifDTO);
            }

            HashMap<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("content", masterApiNotifDTOS);
            responseData.put("currentPage", resultDataSet != null ? resultDataSet.getNumber() : 0);
            responseData.put("totalItems", resultDataSet != null ? resultDataSet.getTotalElements() : 0);
            responseData.put("totalPages", resultDataSet != null ? resultDataSet.getTotalPages() : 0);

            response.setRc("00");
            response.setRm("OK");
            response.setData(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            response.setRc("99");
            response.setRm("Error Occured while fetching monitoring notif data");
        }

        return response;
    }

    public ResponseMsg getMonitoringSubscribers(Map<String, String> params) {

        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        int pageNo = Integer.parseInt(params.get(Constants.PAGINATION_PAGE_NO) != null ? params.get(Constants.PAGINATION_PAGE_NO) : "0" );
        int pageSize = Integer.parseInt(params.get(Constants.PAGINATION_PAGE_SIZE) != null ? params.get(Constants.PAGINATION_PAGE_SIZE) : "100");
        String orderBy = params.get(Constants.PAGINATION_ORDER_BY);
        String orderDirection = params.get(Constants.PAGINATION_ORDER_DIRECTION);
        String startDate = params.get(Constants.PAGINATION_FILTER_START_DATE);
        String endDate = params.get(Constants.PAGINATION_FILTER_END_DATE);
        String searchBy = params.get(Constants.PAGINATION_FILTER_SEARCH_BY);
        String searchValue = params.get(Constants.PAGINATION_FILTER_SEARCH_VALUE);

        try {

            List<MasterApiNotif> masterApiNotifs = new ArrayList<>();
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            if (orderBy != null && orderDirection != null) {
                Sort.Direction direction = null;
                switch (orderDirection) {
                    case "asc":
                        direction = Sort.Direction.ASC;
                        break;
                    case "desc":
                        direction = Sort.Direction.DESC;
                        break;
                }
                pageable = PageRequest.of(pageNo, pageSize, direction, orderBy);
            }

            Page<MasterApiNotif> resultDataSet = null;

            if (startDate != null && endDate != null) {

                // format date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date tglAwalFormated = formatter.parse(startDate + " 00:00:00");
                Date tglAkhirFormated = formatter.parse(endDate + " 23:59:59");

                if (searchBy != null && searchValue != null) {

                    switch (searchBy) {
                        case "vaAccNo":
                            resultDataSet = masterApiNotifRepository.findByVaAccNoContainingAndTrxTimeBetweenAndSentTrueAndReceivedTrue(searchValue,
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txAmount":
                            resultDataSet = masterApiNotifRepository.findByTxAmountAndTrxTimeBetweenAndSentTrueAndReceivedTrue(Long.parseLong(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txReferenceNo":
                            resultDataSet = masterApiNotifRepository.findByTxReferenceNoContainingAndTrxTimeBetweenAndSentTrueAndReceivedTrue(searchValue,
                                    tglAwalFormated, tglAkhirFormated,pageable);
                            break;
                        case "companyId":
                            resultDataSet = masterApiNotifRepository.findByCompanyIdAndTrxTimeBetweenAndSentTrueAndReceivedTrue(Integer.parseInt(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                    }
                    // add case to add another filter

                } else {
                    resultDataSet = masterApiNotifRepository.findByTrxTimeBetweenAndSentTrueAndReceivedTrue(tglAwalFormated, tglAkhirFormated, pageable);
                }
            } else {
                resultDataSet = masterApiNotifRepository.findBySentTrueAndReceivedTrue(pageable);
            }

            if (resultDataSet != null) {
                masterApiNotifs = resultDataSet.getContent();
            }

            ModelMapper modelMapper = new ModelMapper();

            List<MasterApiNotifDTO> masterApiNotifDTOS = new ArrayList<>();
            for(MasterApiNotif masterApiNotif : masterApiNotifs) {
                MasterApiNotifDTO masterApiNotifDTO = new MasterApiNotifDTO();
                masterApiNotifDTO = modelMapper.map(masterApiNotif, MasterApiNotifDTO.class);
                masterApiNotifDTOS.add(masterApiNotifDTO);
            }

            HashMap<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("content", masterApiNotifDTOS);
            responseData.put("currentPage", resultDataSet != null ? resultDataSet.getNumber() : 0);
            responseData.put("totalItems", resultDataSet != null ? resultDataSet.getTotalElements() : 0);
            responseData.put("totalPages", resultDataSet != null ? resultDataSet.getTotalPages() : 0);

            response.setRc("00");
            response.setRm("OK");
            response.setData(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            response.setRc("99");
            response.setRm("Error Occured while fetching monitoring subscriber data");
        }

        return response;
    }

}
