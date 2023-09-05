package com.bjbs.haji.business.views.dtos.kafka;

import lombok.Data;

@Data
public class ResponseSetoranAwalDataKafka {
    private String responseCode;
    private String responseMessage;
    private SetoranAwalHajiDataKafka data;
}
