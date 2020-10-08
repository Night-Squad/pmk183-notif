package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "setting_tbl", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingTbl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_setting_tbl_setting_id_seq")
    @SequenceGenerator(name="generator_setting_tbl_setting_id_seq", sequenceName="setting_tbl_setting_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "setting_id", nullable = false, unique = true)
    private long settingId;

    @Column(name = "setting_code")
    private String settingCode;

    @Column(name = "setting_name")
    private String settingName;

    @Column(name = "value")
    private String value;

    @Column(name = "active")
    private boolean active;

    @Column(name = "information")
    private String information;

    @Column(name = "created_date", columnDefinition = "DATE")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date", columnDefinition = "DATE")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
