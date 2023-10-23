package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "param_company", schema = "public")
public class ParamCompany implements Serializable {

    @EmbeddedId
    private ParamCompanyPKey id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefBitStatus refBitStatus;
}
