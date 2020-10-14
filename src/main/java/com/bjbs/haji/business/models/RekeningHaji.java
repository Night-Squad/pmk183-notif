package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "rekening_haji", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RekeningHaji implements Serializable {

    @Id
    @Column(name = "rekening_haji_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_rekening_haji_id_seq")
    @SequenceGenerator(name="generator_rekening_haji_id_seq", sequenceName="rekening_haji_id_seq", schema = "public", allocationSize = 1)
    private long rekeningHajiId;

    @Column(name = "noRekening")
    private String noRekening;

    @Column(name = "jenis_kelompok_dana")
    private String jenisKelompokDana;

    @Column(name = "jenis_produk_dana")
    private String jenisProdukDana;

    @Column(name = "currency")
    private String currency;

    @Column(name = "tipe_laporan")
    private String tipeLaporan;

    @Column(name = "tanggal_buka", columnDefinition = "DATE")
    private Date tanggalBuka;

    @Column(name = "tanggal_jatuh_tempo", columnDefinition = "DATE")
    private Date tanggalJatuhTempo;

    @Column(name = "jangka_waktu_deposito")
    private String jangkaWaktuDeposito;

    @Column(name = "nisbah")
    private String nisbah;

    @Column(name = "ekuivalen_rate")
    private String ekuivalenRate;

    @Column(name = "jenis_akad")
    private String jenisAkad;

    @Column(name = "tanggal_tutup", columnDefinition = "DATE")
    private Date tanggalTutup;

    @Column(name = "nominal_nisbah")
    private String nominalNisbah;

    @Column(name = "no_bilyet")
    private String noBilyet;

    @Column(name = "created_date", columnDefinition = "DATE")
    private Date createdDate;

    @Column(name = "updated_date", columnDefinition = "DATE")
    private Date updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_operasional")
    private boolean isOperasional;
}
