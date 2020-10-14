package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.Pendidikan;
import com.bjbs.haji.business.ols.PendidikanOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ol/pendidikan")
public class PendidikanOLOController extends HibernateOptionListController<Pendidikan, PendidikanOLO> {
    
}