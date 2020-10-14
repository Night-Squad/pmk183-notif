package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusKawinDTO {
	private long statusKawinId;
	private String namaStatus;
	private Boolean statusActive;
}
