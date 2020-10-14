package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.PembatalanDTO;
import com.bjbs.haji.business.models.Pembatalan;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pembatalan")
public class PembatalanController extends HibernateCRUDController<Pembatalan, PembatalanDTO> {
}
