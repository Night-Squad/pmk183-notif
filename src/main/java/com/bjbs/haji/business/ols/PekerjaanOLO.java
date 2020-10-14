package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PekerjaanOLO {
    
    @OptionListKey
    private long pekerjaanId;
    private String namaPekerjaan;
    private String kodePekerjaan;
    private String kodePekerjaanCore;

}