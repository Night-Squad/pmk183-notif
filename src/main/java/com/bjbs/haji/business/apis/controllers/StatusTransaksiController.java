package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.StatusTransaksiDTO;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/status-transaksi")
public class StatusTransaksiController extends HibernateCRUDController<StatusTransaksi, StatusTransaksiDTO> {

}
