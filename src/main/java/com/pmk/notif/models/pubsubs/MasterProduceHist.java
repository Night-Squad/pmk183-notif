package com.pmk.notif.models.pubsubs;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "master_produce_hist", schema = "public")
public class MasterProduceHist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_master_produce_hist_id_seq")
    @SequenceGenerator(name = "generator_master_produce_hist_id_seq", sequenceName = "master_produce_hist_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private Long apiNotifId;
    private String kafkaHost;
    private String topic;
    private String message;
    private String response;
    private Timestamp createdAt;
    private String createdBy;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "notif_code_id", referencedColumnName = "id")
    @ToString.Exclude
    private RefNotifCode refNotifCode;
}
