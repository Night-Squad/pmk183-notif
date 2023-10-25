package com.pmk.notif.dtos;

import lombok.*;

import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterApiNotifKontigensiDTO {

    private Long id;
    private String vaAccNo;
    private Long txAmount;
    private String txReferenceNo;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
    private Boolean isActive;
    private Boolean sent;
    private Boolean received;
    private Timestamp receivedAt;
    private Timestamp sentAt;
    private Timestamp trxTime;
    private RefChannelDTO refChannel;
    private Object masterCompany;

}
