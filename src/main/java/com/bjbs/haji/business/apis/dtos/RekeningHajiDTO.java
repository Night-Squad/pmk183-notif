package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RekeningHajiDTO {

    private long rekeningHajiId;

    private String noRekening;

    private String jenisKelompokDana;

    private String jenisProdukDana;

    private String currency;

    private String tipeLaporan;

    private Date tanggalBuka;

    private Date tanggalJatuhTempo;

    private String jangkaWaktuDeposito;

    private String nisbah;

    private String ekuivalenRate;

    private String jenisAkad;

    private Date tanggalTutup;

    private String nominalNisbah;

    private String noBilyet;

    private Date createdDate;

    private Date updatedDate;

    private String createdBy;

    private String updatedBy;

    private boolean isOperasional;
}
