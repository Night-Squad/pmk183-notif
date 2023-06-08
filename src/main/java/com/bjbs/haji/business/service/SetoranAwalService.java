package com.bjbs.haji.business.service;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.StatusTransaksiRepository;

@Service
public class SetoranAwalService {

	private static final Log log = LogFactory.getLog(SetoranAwalService.class);
	
	@Autowired
	private SetoranAwalRepository setoranAwalRepository;

	@Autowired
	private StatusTransaksiRepository statusTransaksiRepository;

	public boolean ValidationTrx(LocalTime currentTime) {
		log.info("..validation trx time..");
		log.info(currentTime.toString());
		boolean result = false;

		LocalTime startingTime = LocalTime.parse("21:00");
		LocalTime cutoffTime = LocalTime.parse("15:00");

		log.info("currentTime.isAfter(startingTime) : "+currentTime.isAfter(startingTime));
		if(currentTime.isAfter(startingTime)) {
			result = true;
		} else {
			result = false;
		}

		log.info("currentTime.isBefore(cutoffTime) : "+currentTime.isBefore(cutoffTime));
		if(currentTime.isBefore(cutoffTime)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	@Transactional
	public Response updateDataSetoranAwal(Long setoranAwalId,SetoranAwalDTO body ) {
		
		log.info("=== UPDATE DATA SETORAN AWAL SERVICE ===");
		log.info(body.toString());
		
		Response response = new Response();
		response.setRC("99");
		response.setMessage("ERROR");
		
		
		try {
			Optional<SetoranAwal> exitingSetoranAwal = setoranAwalRepository.findById(setoranAwalId);
			
			if(!exitingSetoranAwal.isPresent()) {
				response.setRC("99");
				response.setMessage("Setoran Awal dengan id : " + setoranAwalId +" tidak ditemukan");
				return response;
			}
			
			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			
			SetoranAwal setoranAwal = exitingSetoranAwal.get();
			setoranAwal.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
//			setoranAwal.setStatusTransaksi(modelMapper.map(body.getStatusTransaksi(),StatusTransaksi.class));
			setoranAwal.setStatusTransaksi(statusTransaksiRepository.findById(body.getStatusTransaksi().getStatusTransaksiId()).orElse(null));
			body.setSetoranAwalId(setoranAwalId);
			modelMapper.map(body, setoranAwal);
			
			
			SetoranAwalDTO setoranAwalDTO = modelMapper.map(setoranAwalRepository.save(setoranAwal),
					SetoranAwalDTO.class);
			
			response.setRC("00");
			response.setMessage("OK");
			response.setData(setoranAwalDTO);
			
		}catch (Exception e) {
			e.printStackTrace();
			response.setRC("99");
			response.setMessage("ERROR");
			// TODO: handle exception
		}

		return response;	
	}
	
	public Response getDropDownStatusTransaksi() {
		log.info("=== GET DROP DOWN ===");

		Response response = new Response();
		response.setRC("99");
		response.setMessage("ERROR");
		
		try {
			List<StatusTransaksi> statusTransaksi = new ArrayList<StatusTransaksi>();
			statusTransaksi = statusTransaksiRepository.findAllStatusTransaksi() ;
			response.setRC("00");
			response.setMessage("Berhasil Menampilkan Dropdown");
			response.setData(statusTransaksi);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setRC("99");
			response.setMessage("ERROR");
		}
		return response;
	}
	
}
