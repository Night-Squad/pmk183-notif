package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PekerjaanDTO {
	private long pekerjaanId;
	private String namaPekerjaan;
	private String kodePekerjaan;
	private String kodePekerjaanCore;
	private Boolean statusActive;
}
