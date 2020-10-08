package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class SetoranAwalHajiData {
	String noRekening;
	String transactionAmount;
	String merchantType;
	Date settlementDate;
	String terminalId;
	String branchCode;
	SetoranAwalHajiRequest setoranAwalHajiRequest;
}
