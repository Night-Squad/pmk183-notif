package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MataUangDTO {
	private long mataUangId;
	private String kodeMataUang;
	private String mataUang;
	private String description;
	private Boolean statusActive;
}
