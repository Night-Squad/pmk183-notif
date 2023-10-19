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
@Table(name = "reff_channel", schema = "public")
public class ReffChannel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_id_chanel")
    @SequenceGenerator(name = "generator_seq_id_chanel", sequenceName = "seq_id_chanel", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Short id;
    @Column(name = "definition")
    private String definition;
    @Column(name = "acc_no")
    private String accNo;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "channel_code")
    private String channelCode;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reffChannel")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifs = new HashSet<>(0);
}
