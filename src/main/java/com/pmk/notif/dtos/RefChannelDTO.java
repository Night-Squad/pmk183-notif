package com.pmk.notif.dtos;

import lombok.*;

import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefChannelDTO {

    private Long id;
    private String channelName;
    private Timestamp createdAt;

}
