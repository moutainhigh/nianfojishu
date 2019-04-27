package com.task.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 曾建森
 * @author Administrator
 * @FileNam Card.java
 * @Date 2012-10-9
 */
public class Card implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	
	private String typeNo;
	
	private String password;
	
	private Date newDate;
	
	private Date validDate;
	
	private float foregift;
	
	private float charge;
	
	private float balance;
	
	private float smallBalance;
	
	private int state;
	
	private String userNo;
	
	private float subsidyFund;
	
	private Date updateTime;
	
	private float icBalance;
	
	private int subsidyFlag;


	public String getTypeNo() {
		return typeNo;
	}

	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getNewDate() {
		return newDate;
	}

	public void setNewDate(Date newDate) {
		this.newDate = newDate;
	}

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public float getForegift() {
		return foregift;
	}

	public void setForegift(float foregift) {
		this.foregift = foregift;
	}

	public float getCharge() {
		return charge;
	}

	public void setCharge(float charge) {
		this.charge = charge;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public float getSmallBalance() {
		return smallBalance;
	}

	public void setSmallBalance(float smallBalance) {
		this.smallBalance = smallBalance;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public float getSubsidyFund() {
		return subsidyFund;
	}

	public void setSubsidyFund(float subsidyFund) {
		this.subsidyFund = subsidyFund;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public float getIcBalance() {
		return icBalance;
	}

	public void setIcBalance(float icBalance) {
		this.icBalance = icBalance;
	}

	public int getSubsidyFlag() {
		return subsidyFlag;
	}

	public void setSubsidyFlag(int subsidyFlag) {
		this.subsidyFlag = subsidyFlag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}