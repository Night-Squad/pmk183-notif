package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tipe_pembatalan", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipePembatalan implements Serializable {

    @Id
    @Column(name = "tipe_pembatalan_id", nullable = false)
    private long tipePembatalanId;

    @Column(name = "tipe_pembatalan_code")
    private String tipePembatalanCode;

    @Column(name = "tipe_pembatalan_name")
    private String tipePembatalanName;

    @Column(name = "status_active")
    private boolean statusActive;

    public TipePembatalan(long tipePembatalanId) {
        this.tipePembatalanId = tipePembatalanId;
    }
}
