package com.pmk.notif.models.pubsubs;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "va_acc_no")
    private String vaAccNo;

    @Column(name = "tx_amount")
    private Long txAmount;

    @Column(name = "tx_reference_no")
    private String txReferenceNo;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "sent")
    private Boolean sent;

    @Column(name = "received")
    private Boolean received;

    @Column(name = "received_at")
    private Timestamp receivedAt;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    @Column(name = "trx_time")
    private Timestamp trxTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefChannel refChannel;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "sent_failed")
    private Boolean sentFailed;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "trn_code")
    private String trnCode;

    @Column(name = "tx_type")
    private Short txType;

    @Transient
    private Integer txCode;

    @Column(name = "tx_desc")
    private String txDesc;

    @Column(name = "error_reason")
    private String errorReason;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterApiNotif")
    @ToString.Exclude
    private Set<MasterProduceHist> masterProduceHists = new HashSet<MasterProduceHist>(0);
}
