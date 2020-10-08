package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "pelunasan_reversal_history", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PelunasanReversalHistory implements Serializable {

    @Id
    @Column(name = "pelunasan_reversal_history_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_pelunasan_reversal_history_id_sequence")
    @SequenceGenerator(name="generator_pelunasan_reversal_history_id_sequence", sequenceName="pelunasan_reversal_history_id_sequence", schema = "public", allocationSize = 1)
    private long pelunasanReversalHistoryId;

    @Column(name = "setoran_pelunasan_id", nullable = false)
    private long setoranPelunasanId;

    @Column(name = "no_rekening")
    private String noRekening;

    @Column(name = "nama_jemaah")
    private String namaJemaah;

    @Column(name = "nominal_setoran")
    private BigInteger nominalSetoran;

    @Column(name = "tanggal_reversal")
    private Date tanggalReversal;

    @Column(name = "no_arsip")
    private String noArsip;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date", columnDefinition = "DATE")
    private Date createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date", columnDefinition = "DATE")
    private Date updatedDate;

    @Column(name = "status_active")
    private boolean statusActive;
}
