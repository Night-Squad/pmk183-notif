package com.pmk.notif.dtos;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KontigensiNotifDTO {
    private String vaAccNo;
    private String status;
    private String reason;
    private int no;
}
