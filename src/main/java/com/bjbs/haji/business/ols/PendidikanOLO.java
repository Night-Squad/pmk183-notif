package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendidikanOLO {

    @OptionListKey
    private long pendidikanId;
    private String pendidikan;
    private String kodePendidikan;
    private String kodePendidikanCore;
    
}