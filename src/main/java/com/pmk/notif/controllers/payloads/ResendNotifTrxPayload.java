package com.pmk.notif.controllers.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendNotifTrxPayload {

    @JsonProperty("tx_reference_no")
    private String txReferenceNo;
    @JsonProperty("created_by")
    private String createdBy;

}
