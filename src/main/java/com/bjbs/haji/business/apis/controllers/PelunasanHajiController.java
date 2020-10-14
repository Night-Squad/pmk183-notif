package com.bjbs.haji.business.apis.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bjbs.haji.business.apis.dtos.SetoranPelunasanDTO;
import com.bjbs.haji.business.apis.dtos.StatusTransaksiDTO;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/pelunasan-haji")
public class PelunasanHajiController extends HibernateCRUDController<SetoranPelunasan, SetoranPelunasanDTO> {

    @Override
    public Object ionaInsertEndpoint(@RequestBody SetoranPelunasanDTO requestBody, @RequestHeader Map<String, String> requestHeaders) throws Exception {
        boolean isAllCorrect=true;
//            requestBody.getBranchId()!=null &&
//            requestBody.getPembayaranAwal()!=null &&
//            requestBody.getNoPorsi()!=null &&
//            requestBody.getCreatedBy()!=null &&
//            requestBody.getUpdatedBy()!=null;

        if(isAllCorrect){
            //LocalDate nowDate=LocalDate.now();
            LocalDateTime nowDateTime=LocalDateTime.now();
            Date nowDateOldFashioned=Date.from(nowDateTime.atZone(ZoneId.systemDefault()).toInstant());

//            requestBody.setTipeHaji(requestBody.getPembayaranAwal().getTipeHaji());
//            requestBody.setStatusTransaksi(new StatusTransaksiDTO(1));

            requestBody.setCreatedDate(nowDateOldFashioned);
            requestBody.setUpdatedBy(null);

            //return requestBody;
            return super.ionaInsertEndpoint(requestBody, requestHeaders);
        }else{
            LinkedHashMap<String, Object> response=new LinkedHashMap<>();

            response.put("message", "data gagal disimpan!");
            response.put("cause", "data yang diterima tidak lengkap!");
            response.put("body", requestBody);

            return ResponseEntity.badRequest().body(response);
        }
    }

}
