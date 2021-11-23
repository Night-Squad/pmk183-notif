package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cities", schema = "public")
public class Cities {

    @Id
    @Column(name = "city_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_city_id_city_seq")
    @SequenceGenerator(name="generator_city_id_city_seq", sequenceName="city_id_city_seq", schema = "public", allocationSize = 1)
    private Long cityId;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "statusActive")
    private Boolean statusActive;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "city_code_cbs")
    private String cityCodeCbs;

    @ManyToOne
    @JoinColumn(name = "province_id", referencedColumnName = "province_id")
    private Provinces provinces;

}
