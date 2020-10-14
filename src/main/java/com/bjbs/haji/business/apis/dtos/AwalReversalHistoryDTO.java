package com.bjbs.haji.business.apis.dtos;

import com.bjbs.haji.business.models.SetoranAwal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwalReversalHistoryDTO {

    private long awalReversalHistoryId;

    private long setoranAwalId;

    private String noRekening;

    private String namaJemaah;

    private BigDecimal nominalSetoran;

    private Date tanggalReversal;

    private String noArsip;

    private String branchCode;

    private String createdBy;

    private Date createdDate;

    private String updatedBy;

    private Date updatedDate;

    private boolean statusActive;
}
