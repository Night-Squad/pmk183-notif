package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.MataUangDTO;
import com.bjbs.haji.business.models.MataUang;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/mata-uang")
public class MataUangController extends HibernateCRUDController<MataUang, MataUangDTO> {

}
