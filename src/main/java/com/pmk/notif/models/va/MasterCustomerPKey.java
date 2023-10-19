package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MasterCustomerPKey implements Serializable {

    @Column(name = "va_acc_no")
    private String vaAccNo;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ToString.Exclude
    private MasterCompany masterCompany;
    @Column(name = "bit_id")
    private Short bitId;
}
