package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.models.AwalReversalHistory;
import com.io.iona.implementations.data.persistent.DefaultReadAllResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring-reversal")
public class ReversalMonitoringViewController {

    @Autowired
    AwalReversalHistoryController awalReversalHistoryController;

    @Autowired
    PelunasanReversalHistoryController pelunasanReversalHistoryController;

    private static final int SECTION_AWAL=1;
    private static final int SECTION_PELUNASAN=2;

    @GetMapping("/readAll")
    public Object viewAllTransaction(@RequestParam Integer section, @RequestParam String filter, @RequestParam String orderby, @RequestParam("top") Integer pageTotalRows, @RequestParam("skip") Integer pageNumber, @RequestParam Map<String, String> requestParameters, @RequestHeader Map<String, String> requestHeaders) {

        try{
            DefaultReadAllResult readResult;

            switch(section){
                case SECTION_AWAL:
                    readResult=awalReversalHistoryController.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
                    break;
                case SECTION_PELUNASAN:
                    readResult=pelunasanReversalHistoryController.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
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
        } catch(Exception e) {
            Map<String, Object> response=new LinkedHashMap<>();

            response.put("message", "gagal mendapatkan data!");
            response.put("stack", e);

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
