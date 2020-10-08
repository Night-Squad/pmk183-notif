package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "pembatalan", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pembatalan implements Serializable {

    @Id
    @Column(name = "pembatalan_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_transaksi_pembatalan_transaksi_pembatalan_id_seq")
    @SequenceGenerator(name="generator_transaksi_pembatalan_transaksi_pembatalan_id_seq", sequenceName="transaksi_pembatalan_transaksi_pembatalan_id_seq", schema = "public", allocationSize = 1)
    private long pembatalanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipe_pembatalan_id", nullable = false)
    private TipePembatalan tipePembatalan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mata_uang_id", nullable = false)
    private MataUang mataUang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipe_haji_id", nullable = false)
    private TipeHaji tipeHaji;

    @Column(name = "no_rekening")
    private String noRekening;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "nama_jemaah")
    private String namaJemaah;

    @Column(name = "nomor_porsi")
    private String nomorPorsi;

    @Column(name = "nilai_setoran_awal")
    private BigInteger nilaiSetoranAwal;

    @Column(name = "tanggal_setoran_awal")
    private Date tanggalSetoranAwal;

    @Column(name = "kode_bps_bpih_awal")
    private String kodeBpsBpihAwal;

    @Column(name = "nilai_setoran_pelunasan")
    private BigInteger nilaiSetoranPelunasan;

    @Column(name = "tanggal_setoran_pelunasan")
    private Date tanggalSetoranPelunasan;

    @Column(name = "kode_bps_bpih_pelunasan")
    private String kodeBpsBpihPelunasan;

    @Column(name = "nominal_pembatalan")
    private BigInteger nominalPembatalan;

    @Column(name = "nominal_pembatalan_biaya_operasional")
    private BigInteger nominalPembatalanBiayaOperasional;

    @Column(name = "nomor_surat_pembatalan")
    private String nomorSuratPembatalan;

    @Column(name = "alasan_pembatalan")
    private String alasanPembatalan;

    @Column(name = "tanggal_pembatalan", columnDefinition = "DATE")
    private Date tanggalPembatalan;

    @Column(name = "created_date", columnDefinition = "DATE")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date", columnDefinition = "DATE")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
