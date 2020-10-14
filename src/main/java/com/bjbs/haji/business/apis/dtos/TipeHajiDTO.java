package com.bjbs.haji.business.apis.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipeHajiDTO {
    long tipeHajiId;
    String kodeHaji;
    String tipeHaji;
    BigDecimal setoranAwal;
    BigDecimal biayaBpih;
    boolean statusActive;
}