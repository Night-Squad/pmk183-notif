package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created By Aristo Pacitra
 * Example Option List From DB
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusKawinOLO {
	@OptionListKey
	private long statusKawinId;
	private String namaStatus;
	private int statusKawinCore;
	private int statusKawinKemenag;
}
