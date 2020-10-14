package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CetakSetoranPelunasanHajiData {
	String noRekening;
	Date settlementDate;
	String merchantType;
	String terminalId;
	String jenisHaji;
	String nomorPorsi;
	String branchCode;
}
