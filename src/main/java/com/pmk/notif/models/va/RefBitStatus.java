package com.pmk.notif.models.va;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ref_bit_status", schema = "public")
public class RefBitStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_id_bit_status")
    @SequenceGenerator(name = "generator_seq_id_bit_status", sequenceName = "seq_id_bit_status", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Short id;
    private String definition;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
