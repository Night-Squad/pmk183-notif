package com.pmk.notif.models.pubsubs;

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
@Table(name = "ref_channel", schema = "public")
public class RefChannel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ref_channel_id_seq")
    @SequenceGenerator(name = "generator_ref_channel_id_seq", sequenceName = "ref_chanel_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "refChannel")
    @ToString.Exclude
    private Set<MasterApiNotif> masterApiNotifs = new HashSet<MasterApiNotif>(0);
}
