package com.bjbs.haji.business.apis.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.apis.dtos.StatusTransaksiDTO;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.service.SetoranAwalService;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/status-transaksi")
public class StatusTransaksiController extends HibernateCRUDController<StatusTransaksi, StatusTransaksiDTO> {
	
	private static final Log log = LogFactory.getLog(StatusTransaksiController.class);

	@Autowired
	private SetoranAwalService awalService;
	
	@GetMapping("/dropdown-status-transaksi")
	public ResponseEntity<Response>getDropDownStatusTransaksi(HttpServletRequest url){
		log.info("DO GET DropDwon Status Transaksi  ");
		log.info("URL = "+ url.getRequestURI());
		
		Response response = awalService.getDropDownStatusTransaksi();
		log.info("Response = "+response);
		
		if(response.getRC().equals("00")) {
			return ResponseEntity.ok(response);
		}else {
			return ResponseEntity.badRequest().body(response);
		}	
	}

}
