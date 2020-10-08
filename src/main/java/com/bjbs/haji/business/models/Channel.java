package com.bjbs.haji.business.models;
// Generated Jan 24, 2020 10:09:35 AM by Hibernate Tools 4.3.1.Final

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "channel", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel implements java.io.Serializable {

	private static final long serialVersionUID = -7145394583760995787L;

	@Id
	@Column(name = "channel_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_channel_channel_id_seq")
	@SequenceGenerator(name="generator_channel_channel_id_seq", sequenceName="channel_channel_id_seq", schema = "public", allocationSize = 1)
	private long channelId;

	@Column(name = "kode_merchant", nullable = false, length = 4)
	private String kodeMerchant;

	@Column(name = "tipe_merchant", length = 15)
	private String tipeMerchant;

	@Column(name = "status_active")
	private Boolean statusActive;

	public Channel(long channelId) {
		this.channelId = channelId;
	}
}
