package com.bjbs.haji.business.ols;

import com.io.iona.core.data.annotations.OptionListKey;

/**
 * Created By Aristo Pacitra
 * Example Option List Hardcode
 */

public class JenisKelaminOLO {
	@OptionListKey
	private int keys;
	private String filterName;
	
	public JenisKelaminOLO(int keys, String filterName) {
		this.keys = keys;
		this.filterName = filterName;
	}
	
	public int getKeys() {
		return keys;
	}
	public void setKeys(int keys) {
		this.keys = keys;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

}
