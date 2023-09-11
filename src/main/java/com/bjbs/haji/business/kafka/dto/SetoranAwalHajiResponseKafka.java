package com.bjbs.haji.business.kafka.dto;

import lombok.Data;

@Data
public class SetoranAwalHajiResponseKafka {
    String nomorValidasi;
	String namaJemaah;
	String noIdentitas;
	String stan;
	String retrievalReferenceNumber;
	String alamat;
	String tahunBerangkat;
	String embarkasi;
	String kloter;
	String virtualAccount;
	String nomorRekening;
    String setoranAwalId;
    String rc;
    String userCode;
    String tokenKemenag;
}
