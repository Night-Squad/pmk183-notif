package com.pmk.notif.services;

import com.pmk.notif.dtos.KontigensiNotifDTO;
import com.pmk.notif.models.pubsubs.MasterApiNotif;
import com.pmk.notif.models.va.*;
import com.pmk.notif.repositories.pubsubs.MasterApiNotifRepository;
import com.pmk.notif.repositories.va.MasterCustomerRepository;
import com.pmk.notif.repositories.va.MasterTxRepository;
import com.pmk.notif.repositories.va.ReffChannelRepository;
import com.pmk.notif.repositories.va.ReffTxCodeRepository;
import com.pmk.notif.response.ResponseMsg;
import com.pmk.notif.utils.GetCurrentTimeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KontigensiNotifService {

    private static final Log log = LogFactory.getLog(KontigensiNotifService.class);

    @Autowired
    private ReffChannelRepository reffChannelRepository;

    @Autowired
    private ReffTxCodeRepository reffTxCodeRepository;

    @Autowired
    private MasterApiNotifRepository masterApiNotifRepository;

    @Autowired
    private MasterTxRepository masterTxRepository;

    @Autowired
    private GetCurrentTimeService getCurrentTimeService;

    @Autowired
    private MasterCustomerRepository masterCustomerRepository;

    public ResponseMsg readExcelNotif(MultipartFile file) {

        ResponseMsg response = new ResponseMsg();
        response.setRc("99");
        response.setRm("ERROR");

        try {

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);

            log.info("Jumlah data excel : " + worksheet.getPhysicalNumberOfRows());

            if(worksheet.getPhysicalNumberOfRows() > 201) {
                response.setRm("Data yang di-upload tidak boleh lebih dari 200 baris");
                return response;
            }

            List<KontigensiNotifDTO> results = new ArrayList<>();
            //read from 2nd row
            for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++){

                XSSFRow row = worksheet.getRow(i);

                //set fields from excel
                MasterApiNotif masterApiNotif = getCellValues(row);

                KontigensiNotifDTO kontigensiNotifDTO = new KontigensiNotifDTO();
                kontigensiNotifDTO.setNo(i);
                kontigensiNotifDTO.setVaAccNo(masterApiNotif.getVaAccNo());

                //validate trn code & channel code & balance
                Optional<ReffChannel> reffChannel = reffChannelRepository.findFirstByChannelCode(masterApiNotif.getChannelCode());
                Optional<ReffTxCode> reffTxCode = reffTxCodeRepository.findFirstByTrnCode(masterApiNotif.getTrnCode());
                Optional<MasterCustomer> masterCustomer = masterCustomerRepository.findByVaAccNoAndBitId(masterApiNotif.getVaAccNo(), (short) 8);

                if (isDataInvalid(masterApiNotif, kontigensiNotifDTO, results, reffChannel, reffTxCode, masterCustomer)) continue;

                //insert into master_api_notif & master_tx
                try {
                    insertDataToDB(masterApiNotif, reffTxCode, reffChannel, masterCustomer);

                    kontigensiNotifDTO.setStatus("success");
                    kontigensiNotifDTO.setReason("success");
                    results.add(kontigensiNotifDTO);
                } catch (Exception e) {
                    kontigensiNotifDTO.setStatus("failed");
                    kontigensiNotifDTO.setReason(e.getMessage() != null ? e.getMessage() : e.getLocalizedMessage());
                    results.add(kontigensiNotifDTO);
                }
            }

            response.setRc("00");
            response.setRm("OK");
            response.setData(results);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void insertDataToDB(MasterApiNotif masterApiNotif, Optional<ReffTxCode> reffTxCode, Optional<ReffChannel> reffChannel, Optional<MasterCustomer> masterCustomer) {
        masterApiNotifRepository.save(masterApiNotif);

        MasterTx masterTx = new MasterTx();
        MasterCompany masterCompany = new MasterCompany();
        masterCompany.setId(masterApiNotif.getCompanyId());
        masterTx.setMasterCompany(masterCompany);
        masterTx.setVaAccNo(masterApiNotif.getVaAccNo());

        RefTxType refTxType = new RefTxType();
        refTxType.setId(Integer.valueOf(masterApiNotif.getTxType().toString()));
        masterTx.setRefTxType(refTxType);

        masterTx.setReffTxCode(reffTxCode.get());
        masterTx.setReffChannel(reffChannel.get());

        MasterCustomer masterCustomerExist = masterCustomer.get();
        Long currentOs = Long.valueOf(masterCustomerExist.getValue());
        Long txAmount = masterApiNotif.getTxAmount();
        Long lastOs = 0L;

        switch(masterApiNotif.getTxType()) {
            case 1:
                lastOs = currentOs + txAmount;
                break;
            case 0:
                lastOs = currentOs - txAmount;
                break;
        }

        // update balance
        masterCustomerExist.setValue(lastOs.toString());
        masterCustomerRepository.save(masterCustomerExist);

        masterTx.setCurrentOs(currentOs);
        masterTx.setTxAmount(txAmount);
        masterTx.setLastOs(lastOs);
        masterTx.setCreatedAt(masterApiNotif.getTrxTime());
        masterTx.setTxDesc(masterApiNotif.getTxDesc());
        masterTx.setArchiveNo(masterApiNotif.getTxReferenceNo());
        masterTxRepository.save(masterTx);
    }

    private boolean isDataInvalid(MasterApiNotif masterApiNotif, KontigensiNotifDTO kontigensiNotifDTO,
                                  List<KontigensiNotifDTO> results, Optional<ReffChannel> reffChannel,
                                  Optional<ReffTxCode> reffTxCode, Optional<MasterCustomer> masterCustomer) {

        if(!reffChannel.isPresent()) {
            kontigensiNotifDTO.setStatus("failed");
            kontigensiNotifDTO.setReason("channel_code tidak ditemukan");
            results.add(kontigensiNotifDTO);
            return true;
        }

        if(!reffTxCode.isPresent()) {
            kontigensiNotifDTO.setStatus("failed");
            kontigensiNotifDTO.setReason("trn_code tidak ditemukan");
            results.add(kontigensiNotifDTO);
            return true;
        }

        if(!masterCustomer.isPresent()) {
            kontigensiNotifDTO.setStatus("failed");
            kontigensiNotifDTO.setReason("customer tidak ditemukan");
            results.add(kontigensiNotifDTO);
            return true;
        } else {
            if(masterApiNotif.getTxType() == 0) {
                Long currentBalance = Long.parseLong(masterCustomer.get().getValue());
                Long finalBalance = currentBalance - masterApiNotif.getTxAmount();

                if(finalBalance < 0) {
                    kontigensiNotifDTO.setStatus("failed");
                    kontigensiNotifDTO.setReason("Saldo tidak mencukupi");
                    results.add(kontigensiNotifDTO);
                    return true;
                }
            }
        }
        return false;
    }

    private MasterApiNotif getCellValues(XSSFRow row) {
        DataFormatter formatter = new DataFormatter();
        MasterApiNotif masterApiNotif = new MasterApiNotif();
        masterApiNotif.setVaAccNo(formatter.formatCellValue(row.getCell(0)).trim());
        masterApiNotif.setTxAmount(Long.valueOf(formatter.formatCellValue(row.getCell(1)).trim()));
        masterApiNotif.setTxReferenceNo(formatter.formatCellValue(row.getCell(2)).trim());
        masterApiNotif.setTrxTime(Timestamp.valueOf(formatter.formatCellValue(row.getCell(3)).trim()));
        masterApiNotif.setChannelCode(formatter.formatCellValue(row.getCell(4)).trim());
        masterApiNotif.setCompanyId(Integer.valueOf(formatter.formatCellValue(row.getCell(5)).trim()));
        masterApiNotif.setTxType(Short.valueOf(formatter.formatCellValue(row.getCell(6)).trim()));
        masterApiNotif.setTxDesc(formatter.formatCellValue(row.getCell(7)).trim());
        masterApiNotif.setTrnCode(formatter.formatCellValue(row.getCell(8)).trim());
        masterApiNotif.setCreatedAt(getCurrentTimeService.getCurrentTime());
        masterApiNotif.setCreatedBy("system");
        return masterApiNotif;
    }

}
