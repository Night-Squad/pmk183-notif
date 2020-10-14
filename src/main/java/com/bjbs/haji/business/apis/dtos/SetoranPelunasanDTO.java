package com.bjbs.haji.business.apis.dtos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.bjbs.haji.business.models.MataUang;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.models.TipeHaji;
import lombok.*;

import javax.persistence.*;

 @AllArgsConstructor
 @NoArgsConstructor
 @Data
public class SetoranPelunasanDTO {
 	private long setoranPelunasanId;
 	private ChannelDTO channel;
	private MataUangDTO mataUang;
	private StatusTransaksiDTO statusTransaksi;
	private TipeHajiDTO tipeHaji;
	private String branchCode;
	private String transactionId;
	private String noPorsi;
	private String noRekening;
	private String namaJemaah;
	private String embarkasi;
	private Date tanggalTransaksi;
	private BigInteger nilaiSetoranAwal;
	private BigInteger nominalSetoran;
	private BigInteger biayaBpih;
	private BigInteger kurs;
	private BigInteger bpihDalamRupiah;
	private BigInteger sisaPelunasan;
	private String flagLunasTunda;
	private String tahunTunda;
	private String noPihk;
	private String namaPihk;
	private short jenisKelamin;
	private String namaAyah;
	private String tempatLahir;
	private String tanggalLahir;
	private String tahunPelunasanHijriah;
	private String alamat;
	private String kelurahan;
	private String kecamatan;
	private String kabupatenKota;
	private String provinsi;
	private String kodePos;
	private String namaBank;
	private String namaCabang;
	private String alamatCabang;
	private String umurTahun;
	private String umurBulan;
	private String tahunPelunasanMasehi;
	private String checksum;
	private String retRefNumber;
	private String systemTraceNumber;
	private String namaNasabah;
	private Date createdDate;
	private String createdBy;
	private Date updatedDate;
	private String updatedBy;
}
