package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.RekeningHajiDTO;
import com.bjbs.haji.business.models.RekeningHaji;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rekening-haji")
public class RekeningHajiController extends HibernateCRUDController<RekeningHaji, RekeningHajiDTO> {
}
