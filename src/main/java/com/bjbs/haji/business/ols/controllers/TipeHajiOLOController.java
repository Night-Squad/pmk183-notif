package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.TipeHaji;
import com.bjbs.haji.business.ols.TipeHajiOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ol/tipe-haji")
public class TipeHajiOLOController extends HibernateOptionListController<TipeHaji, TipeHajiOLO> {
    
}