package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CetakSetoranAwalHajiData {
	String noRekening;
	String merchantType;
	Date settlementDate;
	String jenisHaji;
	String tanggalTransaksi;
	String nomorValidasi;
	String terminalId;
	String branchCode;
}
