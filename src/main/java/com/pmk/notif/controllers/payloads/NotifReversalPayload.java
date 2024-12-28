package com.pmk.notif.controllers.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotifReversalPayload {

    @JsonProperty("tx_reference_no")
    private String txReferenceNod;

    @JsonProperty("reversal_date")
    private String reversalDate;

    @JsonProperty("va_acc_no")
    private String vaAccNo;

    @JsonProperty("tx_amount")
    private Long txAmount;

    public NotifReversalPayload() {
    }

    public NotifReversalPayload(String txReferenceNod, String reversalDate, String vaAccNo, Long txAmount) {
        this.txReferenceNod = txReferenceNod;
        this.reversalDate = reversalDate;
        this.vaAccNo = vaAccNo;
        this.txAmount = txAmount;
    }

    public String getTxReferenceNod() {
        return txReferenceNod;
    }

    public void setTxReferenceNod(String txReferenceNod) {
        this.txReferenceNod = txReferenceNod;
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

    @Override
    public String toString() {
        return "NotifReversalPayload{" +
                "txReferenceNod='" + txReferenceNod + '\'' +
                ", reversalDate='" + reversalDate + '\'' +
                ", vaAccNo='" + vaAccNo + '\'' +
                ", txAmount=" + txAmount +
                '}';
    }
}
