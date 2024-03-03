package com.kevin.ahorcado.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "VISIT_COUNTER")
public class VisitCounter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	Long id;
	
	@Column(name = "IP")
	String ip;
	
	@Column(name = "Count")
	Integer count;
	
	@Column(name = "Cookie")
	String cookie;
	
	public VisitCounter() {
	}

	public VisitCounter(Long id, String ip, Integer count, String cookie) {
		this.id = id;
		this.ip = ip;
		this.count = count;
		this.cookie = cookie;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	public String toString() {
		return "VisitCounter [Id=" + id + ", Ip=" + ip + ", Count=" + count + ", Cookie=" + cookie + "]";
	}
	
}
