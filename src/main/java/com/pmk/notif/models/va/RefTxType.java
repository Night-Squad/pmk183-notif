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
@Table(name = "ref_tx_type", schema = "public")
public class RefTxType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_id_tx_type")
    @SequenceGenerator(name = "generator_seq_id_tx_type", sequenceName = "seq_id_tx_type", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private String definition;
    private String simbol;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refTxType")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifs = new HashSet<>(0);
}
