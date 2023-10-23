package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reff_tx_code", schema = "public")
public class ReffTxCode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_id_tx_code")
    @SequenceGenerator(name = "generator_seq_id_tx_code", sequenceName = "seq_id_tx_code", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Short id;
    @Column(name = "definition")
    private String definition;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "trn_code")
    private String trnCode;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reffTxCode")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifs = new HashSet<>(0);
}
