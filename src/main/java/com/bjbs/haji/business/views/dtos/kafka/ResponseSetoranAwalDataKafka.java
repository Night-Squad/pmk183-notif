package com.bjbs.haji.business.views.dtos.kafka;

import com.bjbs.haji.business.apis.dtos.SetoranAwalHajiData;

import lombok.Data;

@Data
public class ResponseSetoranAwalDataKafka {
    private String responseCode;
    private String responseMessage;
    private SetoranAwalHajiData data;
}
