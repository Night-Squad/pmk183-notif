package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.TipePembatalan;
import com.bjbs.haji.business.ols.TipePembatalanOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ol/tipe-pembatalan")
public class TipePembatalanOLOController extends HibernateOptionListController<TipePembatalan, TipePembatalanOLO> {
}
