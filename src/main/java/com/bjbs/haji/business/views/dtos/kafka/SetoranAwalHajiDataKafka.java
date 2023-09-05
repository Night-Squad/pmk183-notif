package com.bjbs.haji.business.views.dtos.kafka;

import lombok.Data;

@Data
public class SetoranAwalHajiDataKafka {
	
	private Long setoranAwalId;
	private String userCode;
	private String branchCode;
	private String userBranchCode;
	private String cityId;
	private String provinceId;
	private String tokenKemenag;
	private String timestap;
	private String responseMessage;

}
