package com.pmk.notif.controllers.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotifTrxPayload {

    @JsonProperty("va_acc_no")
    private String vaAccNo;
    @JsonProperty("tx_amount")
    private Long txAmount;
    @JsonProperty("tx_reference_no")
    private String txReferenceNo;
//    @JsonProperty("created_at")
//    private String createdAt;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("trx_time")
    private String trxTime;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("company_id")
    private Integer companyId;
    @JsonProperty("tx_type")
    private Integer txType;
    @JsonProperty("tx_code")
    private Integer txCode;
    @JsonProperty("tx_desc")
    private String txDesc;
    @JsonProperty("channel_code")
    private String channelCode;
    @JsonProperty("trn_code")
    private String trnCode;
}
