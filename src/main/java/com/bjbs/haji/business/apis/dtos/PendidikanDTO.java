package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendidikanDTO {
	private long pendidikanId;
	private String pendidikan;
	private String kodePendidikan;
	private String kodePendidikanCore;
	private Boolean statusActive;
}
