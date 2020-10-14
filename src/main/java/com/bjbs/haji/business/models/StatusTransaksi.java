package com.bjbs.haji.business.models;
// Generated Jan 24, 2020 10:09:35 AM by Hibernate Tools 4.3.1.Final

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * StatusTransaksi generated by hbm2java
 */
@Entity
@Table(name = "status_transaksi", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusTransaksi implements java.io.Serializable {

	private static final long serialVersionUID = -6990043285186611561L;

	@Id
	@Column(name = "status_transaksi_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_status_transaksi_status_id_seq")
	@SequenceGenerator(name="generator_status_transaksi_status_id_seq", sequenceName="status_transaksi_status_id_seq", schema = "public", allocationSize = 1)
	private long statusTransaksiId;

	@Column(name = "nama_status_transaksi")
	private String namaStatusTransaksi;

	@Column(name = "status_active")
	private Boolean statusActive;

	public StatusTransaksi(long statusTransaksiId) {
		this.statusTransaksiId = statusTransaksiId;
	}
}
