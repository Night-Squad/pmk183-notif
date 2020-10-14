package com.bjbs.haji.business.apis.dtos;

import lombok.Data;

@Data
public class PembayaranPelunasanHajiResponse {
	private String namaJemaah;
	private String jenisKelamin;
	private String namaOrangTua;
	private String tempatLahir;
	private String tanggalLahir;
	private String embarkasi;
	private String kodeMataUang;
	private String biayaBpih;
	private String bpihDalamRupiah;
	private String sisaPelunasan;
	private String flagLunasTunda;
	private String tahunTunda;
	private String tahunPelunasanHijriah;
	private String kodePihk;
	private String namaPihk;
	private String alamat;
	private String desa;
	private String kecamatan;
	private String kabupatenKota;
	private String provinsi;
	private String kodePos;
	private String namaBank;
	private String namaCabangBank;
	private String alamatCabangBank;
	private String umurTahun;
	private String umurBulan;
	private String tahunPelunasanMasehi;
	private String checksum;
	private String stan;
	private String retrievalReferenceNumber;
}
