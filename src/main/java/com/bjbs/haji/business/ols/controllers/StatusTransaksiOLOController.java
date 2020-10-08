package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.ols.StatusTransaksiOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/ol/status-transaksi")
public class StatusTransaksiOLOController extends HibernateOptionListController<StatusTransaksi, StatusTransaksiOLO> {
    
}