package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "network_management", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkManagement {

    @Id
    @Column(name = "network_management_id", unique = true, nullable = false)
    public Integer networkManagementId;

    @Column(name = "network_management_status")
    public String networkManagementStatus;

    @Column(name = "last_sign_on")
    public Date lastSignOn;

    @Column(name = "last_sign_off")
    public Date lastSignOff;

    @Column(name = "updated_by")
    public String updatedBy;

}
