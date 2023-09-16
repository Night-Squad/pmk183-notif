package com.pmk.notif.services;

import com.pmk.notif.Constants;
import com.pmk.notif.controllers.payloads.NotifTrxPayload;
import com.pmk.notif.dtos.MasterApiNotifDTO;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.models.pubsubs.RefChannel;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.response.ResponseMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MonitoringNotifService {

    private static final Log log = LogFactory.getLog(MonitoringNotifService.class);

    @Autowired
    private MasterApiNotifRepository masterApiNotifRepository;

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
            masterApiNotif.setSent(false);
            masterApiNotif.setSentAt(null);
            masterApiNotif.setReceived(null);
            masterApiNotif.setReceivedAt(null);

            masterApiNotifRepository.save(masterApiNotif);

            response.setRc("00");
            response.setRm("OK");
            response.setData(null);

        } catch (Exception e) {
            e.printStackTrace();
            response.setRc("99");
            response.setRm("Error Occured while insert notif trx data");
        }

        return response;
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
                            resultDataSet = masterApiNotifRepository.findByVaAccNoContainingAndCreatedAtBetweenAndSentOrSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, null, true, null, pageable);
                            break;
                        case "txAmount":
                            resultDataSet = masterApiNotifRepository.findByTxAmountAndCreatedAtBetweenAndSentOrSentAndReceived(Long.parseLong(searchValue),
                                    tglAwalFormated, tglAkhirFormated, null, true, null, pageable);
                            break;
                        case "txReferenceNo":
                            resultDataSet = masterApiNotifRepository.findByTxReferenceNoContainingAndCreatedAtBetweenAndSentOrSentAndReceived(searchValue,
                                    tglAwalFormated, tglAkhirFormated, null, true, null, pageable);
                            break;
                        case "companyId":
                            resultDataSet = masterApiNotifRepository.findByCompanyIdAndCreatedAtBetweenAndSentOrSentAndReceived(Integer.parseInt(searchValue),
                                    tglAwalFormated, tglAkhirFormated, null, true, null, pageable);
                            break;
                    }
                    // add case to add another filter

                } else {
                    resultDataSet = masterApiNotifRepository.findByCreatedAtBetweenAndSentOrSentAndReceived(tglAwalFormated, tglAkhirFormated,
                            null, true, null ,pageable);
                }
            } else {
                resultDataSet = masterApiNotifRepository.findBySentOrSentAndReceived(null, true, null, pageable);
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
            response.setRm("Error Occured while fetching monitoring api notif data");
        }

        return response;
    }

}
