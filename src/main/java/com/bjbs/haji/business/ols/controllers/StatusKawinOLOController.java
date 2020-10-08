package com.bjbs.haji.business.ols.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.models.StatusKawin;
import com.bjbs.haji.business.ols.StatusKawinOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

/**
 * Created By Aristo Pacitra
 * Example Option List Controller From DB
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/ol/status-kawin")
public class StatusKawinOLOController extends HibernateOptionListController<StatusKawin, StatusKawinOLO>{

}
