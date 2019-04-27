package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 充值表
 * 
 * @author Li_Cong 2016-09-17 表名 ta_mj_RechargeZhi 添加充值消费记录表
 */
public class RechargeZhi implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;// 员工姓名
	private String dept;// 部门名称
	private String code;// 员工工号
	private String cardId;// 员工卡号
	private String useWhat;// 用途
	private Integer userId;// userID
	private Integer balance;// 增减数额
	private Float balanceMoney;// 增减金额
	private Integer sumNumber;// 当月充值总数(页面显示)
	private Float sumNumberZong;// 当月充值总金额(页面显示)
	private String dateTime;// 日期
	private String addTime;// 添加时间
	private Integer allId;// 汇总表ID
	// get set

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getSumNumber() {
		return sumNumber;
	}

	public void setSumNumber(Integer sumNumber) {
		this.sumNumber = sumNumber;
	}

	public Float getBalanceMoney() {
		return balanceMoney;
	}

	public void setBalanceMoney(Float balanceMoney) {
		this.balanceMoney = balanceMoney;
	}

	public Float getSumNumberZong() {
		return sumNumberZong;
	}

	public void setSumNumberZong(Float sumNumberZong) {
		this.sumNumberZong = sumNumberZong;
	}

	public String getUseWhat() {
		return useWhat;
	}

	public void setUseWhat(String useWhat) {
		this.useWhat = useWhat;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Integer getAllId() {
		return allId;
	}

	public void setAllId(Integer allId) {
		this.allId = allId;
	}

}
