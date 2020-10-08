package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.Pekerjaan;
import com.bjbs.haji.business.ols.PekerjaanOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/ol/pekerjaan")
public class PekerjaanOLOController extends HibernateOptionListController<Pekerjaan, PekerjaanOLO> {
    
}