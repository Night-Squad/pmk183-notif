package com.bjbs.haji.business.apis.dtos;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetoranAwalDTO {
	private long setoranAwalId;
	private ChannelDTO channel;
	private MataUangDTO mataUang;
	private PekerjaanDTO pekerjaan;
	private PendidikanDTO pendidikan;
	private StatusKawinDTO statusKawin;
	private TipeHajiDTO tipeHaji;
	private StatusTransaksiDTO statusTransaksi;
	private String namaJemaah;
	private String noIdentitas;
	private Date tanggalLahir;
	private String tempatLahir;
	private Short jenisKelamin;
	private String alamat;
	private String kodePos;
	private String kelurahan;
	private String kecamatan;
	private String kabupatenKota;
	private String kabupatenKotaId;
	private String namaAyah;
	private String noRekening;
	private BigDecimal nominalSetoran;
	private String noValidasi;
	private Date tanggalTransaksi;
	private String retRefNumber;
	private String systemTraceNumber;
	private String virtualAccount;
	private String embarkasi;
	private String kloter;
	private String terminalId;
	private String noArsip;
	private String tahunBerangkat;
	private String branchCode;
	private String transactionId;
	private Boolean isUploaded;
	private Date createdDate;
	private String createdBy;
	private Date updatedDate;
	private String updatedBy;
}
