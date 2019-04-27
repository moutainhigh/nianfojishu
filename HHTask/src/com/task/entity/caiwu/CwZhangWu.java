package com.task.entity.caiwu;

import java.io.Serializable;

/****
 * 财务账务表
 * 
 * @表名 ta_fin_CwZhangWu
 * @author 刘培
 * 
 */
public class CwZhangWu implements Serializable{

	private Integer id;// id主键
	private String bankNC;// 账务名称
	private Double money;// 总额
	private String addDateTime;// 添加时间
	private String addUsers;// 添加人

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBankNC() {
		return bankNC;
	}

	public void setBankNC(String bankNC) {
		this.bankNC = bankNC;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getAddDateTime() {
		return addDateTime;
	}

	public void setAddDateTime(String addDateTime) {
		this.addDateTime = addDateTime;
	}

	public String getAddUsers() {
		return addUsers;
	}

	public void setAddUsers(String addUsers) {
		this.addUsers = addUsers;
	}

}
