package com.bjbs.haji.business.models;
// Generated Jan 24, 2020 10:09:35 AM by Hibernate Tools 4.3.1.Final

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "setoran_awal", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetoranAwal implements java.io.Serializable {

	private static final long serialVersionUID = -8814160981850157807L;

	@Id
	@Column(name = "setoran_awal_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_pembayaran_awal_pembayaran_awal_id_seq")
	@SequenceGenerator(name="generator_pembayaran_awal_pembayaran_awal_id_seq", sequenceName="pembayaran_awal_pembayaran_awal_id_seq", schema = "public", allocationSize = 1)
	private Long setoranAwalId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mata_uang_id", nullable = false)
	private MataUang mataUang;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pekerjaan_id", nullable = false)
	private Pekerjaan pekerjaan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pendidikan_id", nullable = false)
	private Pendidikan pendidikan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_kawin_id", nullable = false)
	private StatusKawin statusKawin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_transaksi_id", nullable = false)
	private StatusTransaksi statusTransaksi;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipe_haji_id", nullable = false)
	private TipeHaji tipeHaji;

	@Column(name = "branch_code")
	private String branchCode;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "nama_jemaah")
	private String namaJemaah;

	@Column(name = "no_identitas")
	private String noIdentitas;

	@Column(name = "tanggal_lahir", columnDefinition = "DATE")
	private Date tanggalLahir;

	@Column(name = "tempat_lahir")
	private String tempatLahir;

	@Column(name = "jenis_kelamin")
	private Short jenisKelamin;

	@Column(name = "alamat")
	private String alamat;

	@Column(name = "kode_pos")
	private String kodePos;

	@Column(name = "kelurahan")
	private String kelurahan;

	@Column(name = "kecamatan")
	private String kecamatan;

	@Column(name = "kabupaten_kota")
	private String kabupatenKota;

	@Column(name = "nama_ayah")
	private String namaAyah;

	@Column(name = "no_rekening")
	private String noRekening;

	@Column(name = "nominal_setoran")
	private BigDecimal nominalSetoran;

	@Column(name = "no_validasi")
	private String noValidasi;

	@Column(name = "tanggal_transaksi", columnDefinition = "DATE")
	private Date tanggalTransaksi;

	@Column(name = "ret_ref_number")
	private String retRefNumber;

	@Column(name = "system_trace_number")
	private String systemTraceNumber;

	@Column(name = "virtual_account")
	private String virtualAccount;

	@Column(name = "embarkasi")
	private String embarkasi;

	@Column(name = "kloter")
	private String kloter;

	@Column(name = "terminal_id")
	private String terminalId;

	@Column(name = "no_arsip")
	private String noArsip;

	@Column(name = "tahun_berangkat")
	private String tahunBerangkat;

	@Column(name = "is_uploaded")
	private Boolean isUploaded;

	@Column(name = "created_date", columnDefinition = "DATE")
	private Date createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_date", columnDefinition = "DATE")
	private Date updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;
}
