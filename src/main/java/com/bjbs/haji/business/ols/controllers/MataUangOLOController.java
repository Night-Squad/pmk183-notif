package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.MataUang;
import com.bjbs.haji.business.ols.MataUangOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/ol/mata-uang")
public class MataUangOLOController extends HibernateOptionListController<MataUang, MataUangOLO> {
    
}