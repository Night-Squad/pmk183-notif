package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "master_tx", schema = "public")
public class MasterTx implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_tx")
    @SequenceGenerator(name = "generator_seq_tx", sequenceName = "seq_tx", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ToString.Exclude
    private MasterCompany masterCompany;

    @Column(name = "va_acc_no", nullable = false)
    private String vaAccNo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tx_type", referencedColumnName = "id")
    @ToString.Exclude
    private RefTxType refTxType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tx_code", referencedColumnName = "id")
    @ToString.Exclude
    private ReffTxCode reffTxCode;

    @Column(name = "current_os", nullable = false)
    private Long currentOs;

    @Column(name = "tx_amount", nullable = false)
    private Long txAmount;

    @Column(name = "last_os", nullable = false)
    private Long lastOs;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "tx_desc")
    private String txDesc;

    @Column(name = "archive_no")
    private String archiveNo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @ToString.Exclude
    private ReffChannel reffChannel;

    @Column(name = "is_settlement")
    private Integer isSettlement;

    @Column(name = "reffacc")
    private String reffacc;

    @Column(name = "is_reversal")
    private Boolean isReversal;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_pmk_klasifikasi_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefPmkKlasifikasi refPmkKlasifikasi;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_pmk_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefPmkKlasifikasi refPmk;
}
