package com.bjbs.haji.business.models;
// Generated Jan 24, 2020 10:09:35 AM by Hibernate Tools 4.3.1.Final

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "pekerjaan", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pekerjaan implements java.io.Serializable {

	private static final long serialVersionUID = 9215891369045191017L;

	@Id
	@Column(name = "pekerjaan_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_pekerjaan_pekerjaan_id_seq")
	@SequenceGenerator(name="generator_pekerjaan_pekerjaan_id_seq", sequenceName="pekerjaan_pekerjaan_id_seq", schema = "public", allocationSize = 1)
	private long pekerjaanId;

	@Column(name = "nama_pekerjaan", length = 50)
	private String namaPekerjaan;

	@Column(name = "status_active")
	private Boolean statusActive;

	@Column(name = "kode_pekerjaan")
	private String kodePekerjaan;

	@Column(name = "kode_pekerjaan_core")
	private String kodePekerjaanCore;

	public Pekerjaan(long pekerjaanId) {
		this.pekerjaanId = pekerjaanId;
	}
}
