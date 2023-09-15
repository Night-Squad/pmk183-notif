package com.pmk.notif.dtos;

import com.pmk.notif.models.pubsubs.RefChannel;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterApiNotifDTO {

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
    private Integer companyId;
}
