package com.pmk.notif.controllers.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KontigensiNotifPayload {

    @JsonProperty("api_notif_id")
    private Long apiNotifId;
}
