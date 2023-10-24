package com.pmk.notif.dtos;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KontigensiNotifDTO {
    private String vaAccNo;
    private Long txAmount;
    private String txReferenceNo;
    private String trxTime;
    private String channelCode;
    private Integer companyId;
    private Short txType;
    private String txDesc;
    private String trnCode;
}
