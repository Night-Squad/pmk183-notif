package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

@Data
public class PembayaranPelunasanHajiRequest {
	private String nomorPorsi;
	private String namaJemaah;
	private String embarkasi;
	private String kodeMataUang;
	private String nilaiSetoranAwal;
	private String biayaBpih;
	private String kurs;
	private String bpihDalamRupiah;
	private String sisaPelunasan;
	private String flagLunasTunda;
	private String tahunTunda;
	private String jenisHaji;
	private String kodePihk;
	private String namaPihk;
}
