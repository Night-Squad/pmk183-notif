package com.bjbs.haji.business.apis.dtos;

import com.bjbs.haji.business.models.MataUang;
import com.bjbs.haji.business.models.TipeHaji;
import com.bjbs.haji.business.models.TipePembatalan;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class PembatalanDTO {
    private long pembatalanId;
    private TipePembatalan tipePembatalan;
    private MataUang mataUang;
    private TipeHaji tipeHaji;
    private String noRekening;
    private String nomorPorsi;
    private String branchCode;
    private String namaJemaah;
    private BigInteger nilaiSetoranAwal;
    private Date tanggalSetoranAwal;
    private String kodeBpsBpihAwal;
    private BigInteger nilaiSetoranPelunasan;
    private Date tanggalSetoranPelunasan;
    private String kodeBpsBpihPelunasan;
    private BigInteger nominalPembatalan;
    private BigInteger nominalPembatalanBiayaOperasional;
    private String nomorSuratPembatalan;
    private String alasanPembatalan;
    private Date tanggalPembatalan;
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;
}
