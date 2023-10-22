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
@Table(name = "master_company", schema = "public")
public class MasterCompany implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_id_company")
    @SequenceGenerator(name = "generator_seq_id_company", sequenceName = "seq_id_company", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private String companyName;
    private String address;
    private Boolean isPermanent;
    private Boolean isActive;
    private Boolean isMinusAllowed;
    private String picName;
    @Column(name = "is_h2h")
    private Boolean isH2h;
    private Long expiryTimeSec;
    private String giroAccNo;
    private String giroPenampungNo;
    private Integer splitId;
    private Boolean isHost;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer kdComp;
    private Boolean isExceedAllowed;
    private String idBranch;
    private Integer groupId;
    private String ipPort;
    private String username;
    private String password;
    private String accSource;
    private String userCreate;
    private String userDelete;
    private Short limitProduk;
    private Boolean isTopUp;
    @Column(name = "notif_v2")
    private Boolean notifV2;
    private Boolean isPmk183;
    @Column(name = "KDDEPT")
    private String KDDEPT;
    @Column(name = "KDUNIT")
    private String KDUNIT;
    private Long creditLimit;
    @Column(name = "notif_v3")
    private Boolean notifV3;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterCompany")
    @ToString.Exclude
    private Set<MasterTx> masterApiNotifs = new HashSet<>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.masterCompany")
    @ToString.Exclude
    private Set<MasterCustomer> masterCustomers = new HashSet<>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.masterCompany")
    @ToString.Exclude
    private Set<ParamCompany> paramCompanies = new HashSet<>(0);
}
