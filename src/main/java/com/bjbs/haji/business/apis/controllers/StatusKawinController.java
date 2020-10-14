package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.StatusKawinDTO;
import com.bjbs.haji.business.models.StatusKawin;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/status-kawin")
public class StatusKawinController extends HibernateCRUDController<StatusKawin, StatusKawinDTO> {

}
