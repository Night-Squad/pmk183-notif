package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "provinces", schema = "public")
public class Provinces {

    @Id
    @Column(name = "province_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_province_id_province_seq")
    @SequenceGenerator(name="generator_province_id_province_seq", sequenceName="province_id_province_seq", schema = "public", allocationSize = 1)
    private Long provinceId;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "statusActive")
    private Boolean statusActive;

    @Column(name = "province_code")
    private String provinceCode;

}
