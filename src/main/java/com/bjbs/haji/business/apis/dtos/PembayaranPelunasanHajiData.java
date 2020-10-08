package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class PembayaranPelunasanHajiData {
	String noRekening;
	String transactionAmount;
	Date settlementDate;
	String merchantType;
	String terminalId;
	String branchCode;
	PembayaranPelunasanHajiRequest pembayaranPelunasanHajiRequest;
}
