package com.pmk.notif.models.pubsubs;


import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "master_reversal_notif", schema = "public")
public class MasterReversalNotif {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_master_reversal_notif_id_seq")
    @SequenceGenerator(name = "generator_master_reversal_notif_id_seq", sequenceName = "master_reversal_notif_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "tx_reference_no")
    private String txReferenceNo;

    @Column(name = "reversal_date")
    private Timestamp reversalDate;

    @Column(name = "va_acc_no")
    private String vaAccNo;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_rev_status", referencedColumnName = "id")
    @ToString.Exclude
    private RefRevStatus refRevStatus;

    @Column(name = "result_reason")
    private String resultReason;

    public MasterReversalNotif() {
    }

    public MasterReversalNotif(Long id, String txReferenceNo, Timestamp reversalDate, String vaAccNo, Timestamp createdAt, String createdBy, Timestamp updatedAt, String updatedBy, Boolean isActive, RefRevStatus refRevStatus, String resultReason) {
        this.id = id;
        this.txReferenceNo = txReferenceNo;
        this.reversalDate = reversalDate;
        this.vaAccNo = vaAccNo;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.isActive = isActive;
        this.refRevStatus = refRevStatus;
        this.resultReason = resultReason;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxReferenceNo() {
        return txReferenceNo;
    }

    public void setTxReferenceNo(String txReferenceNo) {
        this.txReferenceNo = txReferenceNo;
    }

    public Timestamp getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(Timestamp reversalDate) {
        this.reversalDate = reversalDate;
    }

    public String getVaAccNo() {
        return vaAccNo;
    }

    public void setVaAccNo(String vaAccNo) {
        this.vaAccNo = vaAccNo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public RefRevStatus getRefRevStatus() {
        return refRevStatus;
    }

    public void setRefRevStatus(RefRevStatus refRevStatus) {
        this.refRevStatus = refRevStatus;
    }

    public String getResultReason() {
        return resultReason;
    }

    public void setResultReason(String resultReason) {
        this.resultReason = resultReason;
    }

    @Override
    public String toString() {
        return "MasterReversalNotif{" +
                "id=" + id +
                ", txReferenceNo='" + txReferenceNo + '\'' +
                ", reversalDate=" + reversalDate +
                ", vaAccNo='" + vaAccNo + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", isActive=" + isActive +
                ", refRevStatus=" + refRevStatus +
                ", resultReason='" + resultReason + '\'' +
                '}';
    }
}
