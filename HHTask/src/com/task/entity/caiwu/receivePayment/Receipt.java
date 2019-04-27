package com.task.entity.caiwu.receivePayment;

import java.io.Serializable;
import java.util.Set;

/****
 * 付款账单 表名 ta_fin_Receipt
 * 
 * @author liupei
 * 
 */
public class Receipt implements Serializable{

	private Integer id;
	private String pkNumber;// 付款账单流水号(APByyyyMMdd0001)
	private String payee;// 收款单位
	private Integer zhUserId;// 供应商Id
	private Integer payeeId;// 收款单位Id
	private Integer userId;// UserId
	private String summary;// 摘要
	private String subBudgetName;// 科目
	private Integer fk_SubBudgetRateId;// 科目id

	private String payType;// 付款类型(采购订单/费用报销/职工薪酬/社保/公积金)
	private String aboutNum;// 关联单号
	private Integer fk_fundApplyId;// 费用报销单Id
	private Integer fk_monthlyBillsId;// 采购订单月度对账单Id

	private Float allMoney;// 总金额（应付）
	private Float accountPaid;// 已付
	private Float unPay;// 未付
	private Float payIng;// 付款中
	private Float payOn;// 申请付款金额
	private Float payLast;// 剩余待付款

	private Integer paymentCycle;// 付款周期
	private String fukuanDate;// 应付款日期
	private String fukuanMonth;// 付款月份
	private String status;// 状态(待付款、同意、付款中、完成)

	private String addTime;// 添加时间
	private String addUserCode;// 添加人员工号
	private String addUserName;// 添加人员名称

	private String epId;// 审批流程

	private Set<ReceiptLog> receiptLogSet;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getFk_fundApplyId() {
		return fk_fundApplyId;
	}

	public void setFk_fundApplyId(Integer fkFundApplyId) {
		fk_fundApplyId = fkFundApplyId;
	}

	public Integer getFk_monthlyBillsId() {
		return fk_monthlyBillsId;
	}

	public void setFk_monthlyBillsId(Integer fkMonthlyBillsId) {
		fk_monthlyBillsId = fkMonthlyBillsId;
	}

	public Float getAllMoney() {
		return allMoney;
	}

	public void setAllMoney(Float allMoney) {
		this.allMoney = allMoney;
	}

	public Float getAccountPaid() {
		return accountPaid;
	}

	public void setAccountPaid(Float accountPaid) {
		this.accountPaid = accountPaid;
	}

	public Float getUnPay() {
		return unPay;
	}

	public void setUnPay(Float unPay) {
		this.unPay = unPay;
	}

	public Float getPayIng() {
		return payIng;
	}

	public void setPayIng(Float payIng) {
		this.payIng = payIng;
	}

	public Float getPayOn() {
		return payOn;
	}

	public void setPayOn(Float payOn) {
		this.payOn = payOn;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getAddUserCode() {
		return addUserCode;
	}

	public void setAddUserCode(String addUserCode) {
		this.addUserCode = addUserCode;
	}

	public String getAddUserName() {
		return addUserName;
	}

	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}

	public String getEpId() {
		return epId;
	}

	public void setEpId(String epId) {
		this.epId = epId;
	}

	public String getFukuanDate() {
		return fukuanDate;
	}

	public void setFukuanDate(String fukuanDate) {
		this.fukuanDate = fukuanDate;
	}

	public String getSubBudgetName() {
		return subBudgetName;
	}

	public void setSubBudgetName(String subBudgetName) {
		this.subBudgetName = subBudgetName;
	}

	public Integer getFk_SubBudgetRateId() {
		return fk_SubBudgetRateId;
	}

	public void setFk_SubBudgetRateId(Integer fkSubBudgetRateId) {
		fk_SubBudgetRateId = fkSubBudgetRateId;
	}

	public String getPkNumber() {
		return pkNumber;
	}

	public void setPkNumber(String pkNumber) {
		this.pkNumber = pkNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPayeeId() {
		return payeeId;
	}

	public void setPayeeId(Integer payeeId) {
		this.payeeId = payeeId;
	}

	public String getFukuanMonth() {
		return fukuanMonth;
	}

	public void setFukuanMonth(String fukuanMonth) {
		this.fukuanMonth = fukuanMonth;
	}

	public String getAboutNum() {
		return aboutNum;
	}

	public void setAboutNum(String aboutNum) {
		this.aboutNum = aboutNum;
	}

	public Integer getPaymentCycle() {
		return paymentCycle;
	}

	public void setPaymentCycle(Integer paymentCycle) {
		this.paymentCycle = paymentCycle;
	}

	public Float getPayLast() {
		return payLast;
	}

	public void setPayLast(Float payLast) {
		this.payLast = payLast;
	}

	public Set<ReceiptLog> getReceiptLogSet() {
		return receiptLogSet;
	}

	public void setReceiptLogSet(Set<ReceiptLog> receiptLogSet) {
		this.receiptLogSet = receiptLogSet;
	}

	public Integer getZhUserId() {
		return zhUserId;
	}

	public void setZhUserId(Integer zhUserId) {
		this.zhUserId = zhUserId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}