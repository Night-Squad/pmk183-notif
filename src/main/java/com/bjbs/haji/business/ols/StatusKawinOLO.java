package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;

/**
 * Created By Aristo Pacitra
 * Example Option List From DB
 */

public class StatusKawinOLO {
	@OptionListKey
	private long statusKawinId;
	private String namaStatus;
	
	public long getStatusKawinId() {
		return statusKawinId;
	}
	public void setStatusKawinId(long statusKawinId) {
		this.statusKawinId = statusKawinId;
	}
	public String getNamaStatus() {
		return namaStatus;
	}
	public void setNamaStatus(String namaStatus) {
		this.namaStatus = namaStatus;
	}

}
