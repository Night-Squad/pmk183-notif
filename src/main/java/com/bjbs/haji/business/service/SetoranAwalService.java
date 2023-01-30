package com.bjbs.haji.business.service;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;

@Service
public class SetoranAwalService {

	private static final Log log = LogFactory.getLog(SetoranAwalService.class);
	
	@Autowired
	private SetoranAwalRepository setoranAwalRepository;
	
	
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
			body.setSetoranAwalId(setoranAwalId);
			modelMapper.map(body, setoranAwal);
			setoranAwal.setStatusTransaksi(modelMapper.map(body.getStatusTransaksi(),StatusTransaksi.class));

			setoranAwal.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			
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
	
}
