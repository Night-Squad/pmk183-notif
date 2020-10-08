package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MataUangOLO {

    @OptionListKey
    private long mataUangId;
	private String kodeMataUang;
	private String mataUang;
    
}