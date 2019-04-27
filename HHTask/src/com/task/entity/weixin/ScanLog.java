package com.task.entity.weixin;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ScanLog entity. @author MyEclipse Persistence Tools
 */

public class ScanLog implements Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Qr qr;
	private WeChatUser weChatUser;
	private String code;
	private Timestamp scantime;
	private Integer status;

	// Constructors

	/** default constructor */
	public ScanLog() {
	}

	/** minimal constructor */
	public ScanLog(Qr qr, WeChatUser weChatUser, Timestamp scantime,
			Integer status) {
		this.qr = qr;
		this.weChatUser = weChatUser;
		this.scantime = scantime;
		this.status = status;
	}

	/** full constructor */
	public ScanLog(Qr qr, WeChatUser weChatUser, String code,
			Timestamp scantime, Integer status) {
		this.qr = qr;
		this.weChatUser = weChatUser;
		this.code = code;
		this.scantime = scantime;
		this.status = status;
	}

	// Property accessors

	
	public Qr getQr() {
		return this.qr;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setQr(Qr qr) {
		this.qr = qr;
	}

	public WeChatUser getWeChatUser() {
		return this.weChatUser;
	}

	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Timestamp getScantime() {
		return this.scantime;
	}

	public void setScantime(Timestamp scantime) {
		this.scantime = scantime;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}