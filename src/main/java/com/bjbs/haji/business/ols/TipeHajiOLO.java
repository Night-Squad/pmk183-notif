package com.bjbs.haji.business.ols;

import java.math.BigDecimal;

import com.io.iona.core.data.annotations.OptionListKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipeHajiOLO {

    @OptionListKey
    private long tipeHajiId;
    private Character kodeHaji;
    private String tipeHaji;
    private BigDecimal setoranAwal;
    private BigDecimal biayaBpih;
    private boolean statusActive;
    
}