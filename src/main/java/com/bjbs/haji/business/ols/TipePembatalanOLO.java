package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipePembatalanOLO {

    @OptionListKey
    private long tipePembatalanId;
    private String tipePembatalanCode;
    private String tipePembatalanName;
    private boolean statusActive;
}
