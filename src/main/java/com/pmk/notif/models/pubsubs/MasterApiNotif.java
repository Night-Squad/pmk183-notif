package com.pmk.notif.models.pubsubs;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "master_api_notif", schema = "public")
public class MasterApiNotif {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_master_api_notif_id_seq")
    @SequenceGenerator(name = "generator_master_api_notif_id_seq", sequenceName = "master_api_notif_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefChannel refChannel;

    private Integer companyId;
}
