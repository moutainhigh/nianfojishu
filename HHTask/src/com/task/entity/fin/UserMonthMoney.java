package com.task.entity.fin;

import java.io.Serializable;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/***
 * 用户月度奖金
 * 
 * @author liupei
 * @表名 ta_fin_UserMonthMoney
 * 
 */
public class UserMonthMoney implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private String code;// 工号
	private String username;// 用户姓名
	private String dept;// 部门
	private Integer userId;// 用户id
	private String month;// 月份
	private Float money;// 奖金
	private Float caozhanbi;// 超占比
	private Float sumWorkingHours;//总工时
	private Float avgHoursMoney;//时均工资
	private Float excitation;//激励
	private Float debit;//扣款
	private String addTime;// 添加时间
	private String sqstatus;// 申请奖金状态(已发放/未发放)
	private Set<UserMoneyDetail> userdetailSet;

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

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	@JSONField(serialize = false)
	public Set<UserMoneyDetail> getUserdetailSet() {
		return userdetailSet;
	}

	public void setUserdetailSet(Set<UserMoneyDetail> userdetailSet) {
		this.userdetailSet = userdetailSet;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getSqstatus() {
		return sqstatus;
	}

	public void setSqstatus(String sqstatus) {
		this.sqstatus = sqstatus;
	}

	public Float getCaozhanbi() {
		return caozhanbi;
	}

	public void setCaozhanbi(Float caozhanbi) {
		this.caozhanbi = caozhanbi;
	}

	public Float getSumWorkingHours() {
		return sumWorkingHours;
	}

	public void setSumWorkingHours(Float sumWorkingHours) {
		this.sumWorkingHours = sumWorkingHours;
	}

	public Float getAvgHoursMoney() {
		return avgHoursMoney;
	}

	public void setAvgHoursMoney(Float avgHoursMoney) {
		this.avgHoursMoney = avgHoursMoney;
	}

	public Float getExcitation() {
		return excitation;
	}

	public void setExcitation(Float excitation) {
		this.excitation = excitation;
	}

	public Float getDebit() {
		return debit;
	}

	public void setDebit(Float debit) {
		this.debit = debit;
	}

}