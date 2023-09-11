package com.bjbs.haji.business.kafka.dto;

import com.bjbs.haji.business.apis.dtos.SetoranAwalHajiData;

import lombok.Data;

@Data
public class SetoranAwalDataKafkaResponse extends SetoranAwalHajiData {
	
	String userCode;
	String tokenKemenag;
	Long setoranAwalId;
	String timestamp;

}
