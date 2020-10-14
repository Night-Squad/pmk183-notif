package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class InquiryPembatalanHajiData {
	String noRekening;
	Date settlementDate;
	String merchantType;
	String terminalId;
	String nomorPorsi;
	String jenisPembatalan;
	String branchCode;
}
