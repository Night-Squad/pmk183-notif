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
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "address")
    private String address;
    @Column(name = "is_permanent")
    private Boolean isPermanent;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_minus_allowed")
    private Boolean isMinusAllowed;
    @Column(name = "pic_name")
    private String picName;
    @Column(name = "is_h2h")
    private Boolean isH2h;
    @Column(name = "expiry_time_sec")
    private Long expiryTimeSec;
    @Column(name = "giro_acc_no")
    private String giroAccNo;
    @Column(name = "giro_penampung_no")
    private String giroPenampungNo;
    @Column(name = "split_id")
    private Integer splitId;
    @Column(name = "is_host")
    private Boolean isHost;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "kd_comp")
    private Integer kdComp;
    @Column(name = "is_exceed_allowed")
    private Boolean isExceedAllowed;
    @Column(name = "id_branch")
    private String idBranch;
    @Column(name = "group_id")
    private Integer groupId;
    @Column(name = "ip_port")
    private String ipPort;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "acc_source")
    private String accSource;
    @Column(name = "user_create")
    private String userCreate;
    @Column(name = "user_delete")
    private String userDelete;
    @Column(name = "limit_produk")
    private Short limitProduk;
    @Column(name = "is_top_up")
    private Boolean isTopUp;
    @Column(name = "notif_v2")
    private Boolean notifV2;
    @Column(name = "is_pmk183")
    private Boolean isPmk183;
    @Column(name = "`KDDEPT`")
    private String KDDEPT;
    @Column(name = "`KDUNIT`")
    private String KDUNIT;
    @Column(name = "credit_limit")
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
