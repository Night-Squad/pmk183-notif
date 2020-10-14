package com.bjbs.haji.business.apis.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.io.iona.implementations.data.persistent.DefaultReadAllResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitoring-transaksi")
public class TransaksiMonitoringViewController {

    @Autowired
    SetoranAwalController paCont;

    @Autowired
    PelunasanHajiController phCont;

    private static final int SECTION_PA=1;
    private static final int SECTION_PH=2;

    @GetMapping("/readAll")
    public Object viewAllTransaction(@RequestParam Integer section, @RequestParam String filter, @RequestParam String orderby, @RequestParam("top") Integer pageTotalRows, @RequestParam("skip") Integer pageNumber, @RequestParam Map<String, String> requestParameters, @RequestHeader Map<String, String> requestHeaders) {

        try{
            DefaultReadAllResult readResult;

            switch(section){
                case SECTION_PA:
                    readResult=paCont.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
                    break;
                case SECTION_PH:
                    readResult=phCont.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
                    break;
                default:
                    readResult=null;
                    break;
            }

            if(readResult!=null){
                return readResult;
            }else{
                Map<String, Object> response=new LinkedHashMap<>();

                response.put("message", "gagal mendapatkan data!");
                if(section!=1 || section!=2){
                    response.put("cause", "parameter [section] hanya dapat mengandung angka 1 atau 2");
                }

                return ResponseEntity.badRequest().body(response);
            }
        }catch(Exception e){
            Map<String, Object> response=new LinkedHashMap<>();

            response.put("message", "gagal mendapatkan data!");
            response.put("stack", e);

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
    
}