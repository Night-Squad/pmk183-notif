package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.PekerjaanDTO;
import com.bjbs.haji.business.models.Pekerjaan;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
@RequestMapping("/api/pekerjaan")
public class PekerjaanController extends HibernateCRUDController<Pekerjaan, PekerjaanDTO> {

}
