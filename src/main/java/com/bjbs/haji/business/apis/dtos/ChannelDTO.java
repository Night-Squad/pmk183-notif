package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO {
	private long channelId;
	private String kodeMerchant;
	private String tipeMerchant;
	private Boolean statusActive;
}
