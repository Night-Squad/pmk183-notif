package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class PembayaranPembatalanHajiData {
	String noRekening;
	Date settlementDate;
	String merchantType;
	String terminalId;
	String branchCode;
	PembayaranPembatalanHajiRequest pembayaranPembatalanHajiRequest;
}
