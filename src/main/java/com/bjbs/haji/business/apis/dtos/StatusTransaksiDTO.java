package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusTransaksiDTO {

	public StatusTransaksiDTO(long statusTransaksiId) {
		this.statusTransaksiId = statusTransaksiId;
	}

	private long statusTransaksiId;
	private String namaStatusTransaksi;
	private Boolean statusActive;
}
