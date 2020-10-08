package com.bjbs.haji.business.apis.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryPelunasanDTO {
    private String noPorsi;
    private ChannelDTO channel;
    private String noRekening;
}
