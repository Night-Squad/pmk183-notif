package com.pmk.notif.services;

import com.google.gson.Gson;
import com.pmk.notif.Constants;
import com.pmk.notif.controllers.payloads.NotifTrxPayload;
import com.pmk.notif.dtos.MasterApiNotifDTO;
import com.pmk.notif.dtos.MasterApiNotifKontigensiDTO;
import com.pmk.notif.dtos.RefChannelDTO;
import com.pmk.notif.kafka.service.KafkaService;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.models.pubsubs.MasterProduceHist;
import com.pmk.notif.models.pubsubs.RefNotifCode;
import com.pmk.notif.models.va.MasterCompany;
import com.pmk.notif.models.va.MasterCustomer;
import com.pmk.notif.models.va.ReffChannel;
import com.pmk.notif.models.va.ReffTxCode;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.repositories.pubsubs.MasterProduceHistRepository;
import com.pmk.notif.repositories.va.MasterCompanyRepository;
import com.pmk.notif.repositories.va.MasterCustomerRepository;
import com.pmk.notif.repositories.va.ReffChannelRepository;
import com.pmk.notif.repositories.va.ReffTxCodeRepository;
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

    @Autowired
    private MasterCustomerRepository masterCustomerRepository;

    @Autowired
    private ReffChannelRepository reffChannelRepository;

    @Autowired
    private ReffTxCodeRepository reffTxCodeRepository;

    @Value("${kafka-topic}")
    private String kafkaTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private MasterCompanyRepository masterCompanyRepository;

    @Transactional
    public ResponseMsg saveNotifTrx(NotifTrxPayload body) {

        ResponseMsg response = new ResponseMsg();

        response.setRc("99");
        response.setRm("ERROR");

        try {

            //validate trncode and channelcode and master company
            log.info("Validate input");
            log.info("Channel code : " + body.getChannelCode());
            log.info("Trn code : " + body.getTrnCode());
            log.info("Kd Comp : " + body.getVaAccNo().trim().substring(3,6));
            Optional<ReffChannel> reffChannel = reffChannelRepository.findFirstByChannelCode(body.getChannelCode());
            Optional<ReffTxCode> reffTxCode = reffTxCodeRepository.findFirstByTrnCode(body.getTrnCode());
            Optional<MasterCompany> masterCompany = masterCompanyRepository.findFirstByKdComp(body.getVaAccNo().trim().substring(3,6));

            if(!masterCompany.isPresent()) {
                response.setRc("99");
                response.setRm("company_id pada nomor va : " + body.getVaAccNo() + " tidak ditemukan");
                return response;
            }

            if(!reffChannel.isPresent()) {
                response.setRc("99");
                response.setRm("Channel code : " + body.getChannelCode() + " tidak ditemukan");
                return response;
            }

            if(!reffTxCode.isPresent()) {
                response.setRc("99");
                response.setRm("Trn code : " + body.getTrnCode() + " tidak ditemukan");
                return response;
            }

            //validate balance
            MasterCustomer masterCustomer = masterCustomerRepository
                    .findByVaAccNoAndBitId(body.getVaAccNo(), (short) 8).orElse(null);

            if(masterCustomer == null) {
                response.setRc("99");
                response.setRm("Customer dengan va_acc_no : " + body.getVaAccNo() + " tidak ditemukan");
                return response;
            } else {
                if(body.getTxType() == 0) {
                    Long currentBalance = Long.parseLong(masterCustomer.getValue());
                    Long finalBalance = currentBalance - body.getTxAmount();

                    if(finalBalance < 0) {
                        response.setRc("99");
                        response.setRm("Saldo tidak mencukupi");
                        return response;
                    }
                }
            }

            MasterApiNotif masterApiNotif = new MasterApiNotif();
            masterApiNotif.setVaAccNo(body.getVaAccNo());
            masterApiNotif.setTxAmount(body.getTxAmount());
            masterApiNotif.setTxReferenceNo(body.getTxReferenceNo());
//            masterApiNotif.setCreatedAt(Timestamp.valueOf(body.getCreatedAt()));
            masterApiNotif.setCreatedAt(getCurrentTimeService.getCurrentTime());
            masterApiNotif.setCreatedBy("system");
            masterApiNotif.setTrxTime(Timestamp.valueOf(body.getTrxTime()));
            //disabled
//            RefChannel refChannel = new RefChannel();
//            refChannel.setId(body.getChannelId());
//            masterApiNotif.setRefChannel(refChannel);
            masterApiNotif.setCompanyId(masterCompany.get().getId());
            masterApiNotif.setIsActive(true);
            masterApiNotif.setSent(null);
            masterApiNotif.setSentAt(null);
            masterApiNotif.setReceived(null);
            masterApiNotif.setReceivedAt(null);
            masterApiNotif.setSentFailed(null);
            masterApiNotif.setTxType(body.getTxType());
            //disabled
//            masterApiNotif.setTxCode(body.getTxCode());
            masterApiNotif.setTxDesc(body.getTxDesc());
            masterApiNotif.setTrnCode(body.getTrnCode());
            masterApiNotif.setChannelCode(body.getChannelCode());
            masterApiNotif.setTxType(body.getTxType());
            masterApiNotif.setTxDesc(body.getTxDesc());

            MasterApiNotif savedMasterApiNotif = masterApiNotifRepository.save(masterApiNotif);

            //send data to kafka
            try {
                ListenableFuture<SendResult<String, String>> kafkaResponse = kafkaService.sendMessageToKafka(masterApiNotif);

                kafkaResponse.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        //update sent false & sent_failed true
                        MasterApiNotif dataWillBeUpdated = savedMasterApiNotif;
                        dataWillBeUpdated.setSent(false);
                        dataWillBeUpdated.setSentFailed(true);
                        MasterApiNotif updatedMasterApiNotif = masterApiNotifRepository.save(dataWillBeUpdated);

                        //insert to masterProduceHist
                        insertMasterProduceHist(ex, updatedMasterApiNotif, masterApiNotif, false);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        //update sent true && sent_at jam terkirim
                        MasterApiNotif dataWillBeUpdated = savedMasterApiNotif;
                        dataWillBeUpdated.setSent(true);
                        dataWillBeUpdated.setSentAt(getCurrentTimeService.getCurrentTime());
                        MasterApiNotif updatedMasterApiNotif = masterApiNotifRepository.save(dataWillBeUpdated);

                        //insert to masterProduceHist
                        insertMasterProduceHist(null, updatedMasterApiNotif, masterApiNotif, true);
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

    private void insertMasterProduceHist(Throwable ex, MasterApiNotif updatedMasterApiNotif, MasterApiNotif masterApiNotif, Boolean isSuccess) {
        MasterProduceHist masterProduceHist = new MasterProduceHist();
        masterProduceHist.setMasterApiNotif(updatedMasterApiNotif);
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
        RefNotifCode refNotifCode = new RefNotifCode();
        refNotifCode.setId((long) 1);
        masterProduceHist.setRefNotifCode(refNotifCode);
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

                log.info(startDate.toString());
                log.info(endDate.toString());
                log.info(tglAwalFormated.toString());
                log.info(tglAkhirFormated.toString());

                if (searchBy != null && searchValue != null) {

                    log.info(searchBy);
                    log.info(searchValue);

                    switch (searchBy) {
                        case "vaAccNo":
                            resultDataSet = masterApiNotifRepository.findByVaAccNoContainingAndTrxTimeBetweenAndSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txAmount":
                            resultDataSet = masterApiNotifRepository.findByTxAmountAndTrxTimeBetweenAndSentAndReceived(Long.parseLong(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txReferenceNo":
                            resultDataSet = masterApiNotifRepository.findByTxReferenceNoContainingAndTrxTimeBetweenAndSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "companyId":
                            resultDataSet = masterApiNotifRepository.findByCompanyIdAndTrxTimeBetweenAndSentAndReceived(Integer.parseInt(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                    }
                    // add case to add another filter

                } else {
                    resultDataSet = masterApiNotifRepository.findByTrxTimeBetweenAndSentAndReceived(tglAwalFormated, tglAkhirFormated,
                            pageable);
                }
            } else {
                resultDataSet = masterApiNotifRepository.findBySentAndReceived(pageable);
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

    public ResponseMsg getNotifTrxs(Map<String, String> params) {

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
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "createdAt");

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

                    log.info(searchBy);
                    log.info(searchValue);

                    switch (searchBy) {
                        case "vaAccNo":
                            resultDataSet = masterApiNotifRepository.findByVaAccNoContainingAndTrxTimeBetween(searchValue,
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txAmount":
                            resultDataSet = masterApiNotifRepository.findByTxAmountAndTrxTimeBetween(Long.parseLong(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "txReferenceNo":
                            resultDataSet = masterApiNotifRepository.findByTxReferenceNoContainingAndTrxTimeBetween(searchValue,
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                        case "companyId":
                            resultDataSet = masterApiNotifRepository.findByCompanyIdAndTrxTimeBetween(Integer.parseInt(searchValue),
                                    tglAwalFormated, tglAkhirFormated, pageable);
                            break;
                    }
                    // add case to add another filter

                } else {
                    resultDataSet = masterApiNotifRepository.findByTrxTimeBetween(tglAwalFormated, tglAkhirFormated,
                            pageable);
                }
            } else {
                resultDataSet = masterApiNotifRepository.findAll(pageable);
            }

            if (resultDataSet != null) {
                masterApiNotifs = resultDataSet.getContent();
            }

            ModelMapper modelMapper = new ModelMapper();

            List<MasterApiNotifKontigensiDTO> masterApiNotifDTOS = new ArrayList<>();
            for(MasterApiNotif masterApiNotif : masterApiNotifs) {
                MasterApiNotifKontigensiDTO masterApiNotifDTO = new MasterApiNotifKontigensiDTO();
                masterApiNotifDTO.setId(masterApiNotif.getId());
                masterApiNotifDTO.setVaAccNo(masterApiNotif.getVaAccNo());
                masterApiNotifDTO.setTxAmount(masterApiNotif.getTxAmount());
                masterApiNotifDTO.setTxReferenceNo(masterApiNotif.getTxReferenceNo());
                masterApiNotifDTO.setCreatedAt(masterApiNotif.getCreatedAt());
                masterApiNotifDTO.setCreatedBy(masterApiNotif.getCreatedBy());
                masterApiNotifDTO.setUpdatedAt(masterApiNotif.getUpdatedAt());
                masterApiNotifDTO.setUpdatedBy(masterApiNotif.getUpdatedBy());
                masterApiNotifDTO.setIsActive(masterApiNotif.getIsActive());
                masterApiNotifDTO.setSent(masterApiNotif.getSent());
                masterApiNotifDTO.setReceived(masterApiNotif.getReceived());
                masterApiNotifDTO.setReceivedAt(masterApiNotif.getReceivedAt());
                masterApiNotifDTO.setSentAt(masterApiNotif.getSentAt());
                masterApiNotifDTO.setTrxTime(masterApiNotif.getTrxTime());
                masterApiNotifDTO.setRefChannel(null);
                masterApiNotifDTO.setMasterCompany(masterCompanyRepository.findByCompanyId(masterApiNotif.getCompanyId()).orElse(null));
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
            response.setRm("Error Occured while fetching master api notif data");
        }

        return response;
    }

}
