package com.pmk.notif.controllers.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotifReversalPayload {

    @JsonProperty("tx_reference_no")
    private String txReferenceNo;

    @JsonProperty("reversal_date")
    private String reversalDate;

    @JsonProperty("va_acc_no")
    private String vaAccNo;

    @JsonProperty("tx_amount")
    private Long txAmount;

    public NotifReversalPayload() {
    }

    public NotifReversalPayload(String txReferenceNo, String reversalDate, String vaAccNo, Long txAmount) {
        this.txReferenceNo = txReferenceNo;
        this.reversalDate = reversalDate;
        this.vaAccNo = vaAccNo;
        this.txAmount = txAmount;
    }

    public String getTxReferenceNo() {
        return txReferenceNo;
    }

    public void setTxReferenceNo(String txReferenceNo) {
        this.txReferenceNo = txReferenceNo;
    }

    public String getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(String reversalDate) {
        this.reversalDate = reversalDate;
    }

    public String getVaAccNo() {
        return vaAccNo;
    }

    public void setVaAccNo(String vaAccNo) {
        this.vaAccNo = vaAccNo;
    }

    public Long getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(Long txAmount) {
        this.txAmount = txAmount;
    }


}
