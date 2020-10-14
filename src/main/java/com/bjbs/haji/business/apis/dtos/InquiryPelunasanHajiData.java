package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class InquiryPelunasanHajiData {
	String noRekening;
	String transactionAmount;
	Date settlementDate;
	String merchantType;
	String terminalId;
	String nomorPorsi;
	String branchCode;
}
