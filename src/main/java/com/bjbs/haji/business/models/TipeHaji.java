package com.bjbs.haji.business.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipe_haji", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipeHaji implements Serializable {

    private static final long serialVersionUID = -8333684994059327660L;

    @Id
	@Column(name = "tipe_haji_id", unique = true, nullable = false)
	private long tipeHajiId;

    @Column(name="kode_haji")
    private String kodeHaji;

    @Column(name = "tipe_haji", length = 10)
    private String tipeHaji;

    @Column(name="setoran_awal")
    private BigDecimal setoranAwal;

    @Column(name="biaya_bpih")
    private BigDecimal biayaBpih;

    @Column(name="status_active")
    private boolean statusActive;

    public TipeHaji(long tipeHajiId) {
        this.tipeHajiId = tipeHajiId;
    }
}