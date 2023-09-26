package com.pmk.notif.models.pubsubs;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ref_notif_code", schema = "public")
public class RefNotifCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ref_notif_code_id_seq")
    @SequenceGenerator(name = "generator_ref_notif_code_id_seq", sequenceName = "ref_notif_code_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String name;
    private Boolean isActive;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refNotifCode")
    @ToString.Exclude
    private Set<MasterProduceHist> masterProduceHists = new HashSet<>(0);
}
