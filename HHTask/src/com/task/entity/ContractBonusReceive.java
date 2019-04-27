package com.task.entity;

import java.io.Serializable;

/***
 * 部留领取(表名:ta_fin_ContractBonusReceive)
 * 
 * @author 刘培
 * 
 */
public class ContractBonusReceive implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;// id
	private String receivePeople;// 领取人
	private Float receiveMoney;// 领取金额
	private String receiveMonth;// 领取月份
	private String receiveDept;// 领取部门
	private String receiveTime;// 领取时间
	private int cbId;// 部留信息Id

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReceivePeople() {
		return receivePeople;
	}

	public void setReceivePeople(String receivePeople) {
		this.receivePeople = receivePeople;
	}

	public Float getReceiveMoney() {
		return receiveMoney;
	}

	public void setReceiveMoney(Float receiveMoney) {
		this.receiveMoney = receiveMoney;
	}

	public String getReceiveMonth() {
		return receiveMonth;
	}

	public void setReceiveMonth(String receiveMonth) {
		this.receiveMonth = receiveMonth;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	public int getCbId() {
		return cbId;
	}

	public void setCbId(int cbId) {
		this.cbId = cbId;
	}

}