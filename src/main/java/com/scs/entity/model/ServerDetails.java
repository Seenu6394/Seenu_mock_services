package com.scs.entity.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SERVER_DETAILS", schema = "apidev")
public class ServerDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5867639087269729255L;

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "IP")
	private String ip;

	@Column(name = "DEPLOYMENT_NAME")
	private String name;

	public ServerDetails() {
		//
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
