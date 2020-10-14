package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

@Data
public class PembayaranPembatalanHajiRequest {
	String nomorPorsi;
	String jenisPembatalan;
	String alasanPembatalan;
	String namaJemaah;
	String kodeMataUang;
	String nilaiSetoranAwal;
	String tanggalSetoranAwal;
	String kodeBankSetoranAwal;
	String nilaiSetoranPelunasan;
	String tanggalSetoranPelunasan;
	String kodeBankSetoranPelunasan;
	String nominalPembatalan;
	String nominalPembatalanBiayaOperasional;
	String jenisHaji;
	
}
