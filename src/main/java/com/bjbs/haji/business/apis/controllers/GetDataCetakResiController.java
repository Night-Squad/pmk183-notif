package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.CetakSetoranAwalHajiResponse;
import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/get-data")
public class GetDataCetakResiController {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/resi")
    public Object getDataResi(@RequestParam("noValidasi") String noValidasi) {
        try {
            SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoValidasi(noValidasi);

            SetoranAwalDTO data = modelMapper.map(setoranAwal, SetoranAwalDTO.class);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
