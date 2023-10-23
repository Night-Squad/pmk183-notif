package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "master_customer", schema = "public")
public class MasterCustomer {

    @EmbeddedId
    private MasterCustomerPKey id;
    @Column(name = "value")
    private String value;
    @Column(name = "code_comp")
    private Integer codeComp;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
