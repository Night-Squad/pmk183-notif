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
@Table(name = "ref_pmk_klasifikasi", schema = "public")
public class RefPmkKlasifikasi implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ref_pmk_klasifikasi_id_seq")
    @SequenceGenerator(name = "generator_ref_pmk_klasifikasi_id_seq", sequenceName = "ref_pmk_klasifikasi_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String namaKlasifikasi;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refPmkKlasifikasi")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifs = new HashSet<>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refPmk")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifss = new HashSet<>(0);

}
