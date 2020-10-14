package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.PendidikanDTO;
import com.bjbs.haji.business.models.Pendidikan;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/pendidikan")
public class PendidikanController extends HibernateCRUDController<Pendidikan, PendidikanDTO> {

}
