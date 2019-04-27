package com.task.entity.paymonth;

import java.io.Serializable;

/**
 * 月度结算 ta_PayMonthDetail
 * @author tx
 *
 */
public class PayMonthDetail implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id ;
	private String illustrate;//说明
	private String jine;//金额
	private String addTime;//添加时间
	private String addUser;//添加人
	private Integer payMonthId;//主表ID (PayMonth)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIllustrate() {
		return illustrate;
	}
	public void setIllustrate(String illustrate) {
		this.illustrate = illustrate;
	}
	public String getJine() {
		return jine;
	}
	public void setJine(String jine) {
		this.jine = jine;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUser() {
		return addUser;
	}
	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}
	public Integer getPayMonthId() {
		return payMonthId;
	}
	public void setPayMonthId(Integer payMonthId) {
		this.payMonthId = payMonthId;
	}
	
	
	
}