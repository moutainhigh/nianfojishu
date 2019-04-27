package com.tast.entity.zhaobiao;

import java.io.Serializable;

/**
 * 预付款明细表
 * @author tx
 * 表名：ta_zh_PrepaymentsApplicationDetails
 */

public class PrepaymentsApplicationDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	 private Integer id;
	 private Integer orderId;//订单Id
	 private Integer prepaymentsApplicationId;//预付款申请单Id
     private String poNumber;//订单编号
     private String yyName;//预付项目名称 ---1
     private Float allMoney;//采购总额----1
     private Float yfbl;//预付比例
     private Float yfMoney;//预付金额
     private String yfMoneyDX;//预付金额(大写)
     private String jbName;//申请人------1
     private String qsName;//签收人---1
     private String expectedTime;//预计报销日期
     private String addDate;//添加日期
     private String addTime;//添加时间
     private String addName;//添加人
     private String addDept;//添加人部门
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getPrepaymentsApplicationId() {
		return prepaymentsApplicationId;
	}
	public void setPrepaymentsApplicationId(Integer prepaymentsApplicationId) {
		this.prepaymentsApplicationId = prepaymentsApplicationId;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getYyName() {
		return yyName;
	}
	public void setYyName(String yyName) {
		this.yyName = yyName;
	}
	public Float getAllMoney() {
		return allMoney;
	}
	public void setAllMoney(Float allMoney) {
		this.allMoney = allMoney;
	}
	public Float getYfbl() {
		return yfbl;
	}
	public void setYfbl(Float yfbl) {
		this.yfbl = yfbl;
	}
	public Float getYfMoney() {
		return yfMoney;
	}
	public void setYfMoney(Float yfMoney) {
		this.yfMoney = yfMoney;
	}
	public String getYfMoneyDX() {
		return yfMoneyDX;
	}
	public void setYfMoneyDX(String yfMoneyDX) {
		this.yfMoneyDX = yfMoneyDX;
	}
	public String getJbName() {
		return jbName;
	}
	public void setJbName(String jbName) {
		this.jbName = jbName;
	}
	public String getQsName() {
		return qsName;
	}
	public void setQsName(String qsName) {
		this.qsName = qsName;
	}

	public String getExpectedTime() {
		return expectedTime;
	}
	public void setExpectedTime(String expectedTime) {
		this.expectedTime = expectedTime;
	}
	public String getAddDate() {
		return addDate;
	}
	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddName() {
		return addName;
	}
	public void setAddName(String addName) {
		this.addName = addName;
	}
	public String getAddDept() {
		return addDept;
	}
	public void setAddDept(String addDept) {
		this.addDept = addDept;
	}
     
     
}