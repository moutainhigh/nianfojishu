package com.task.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * @Title: PrintProof.java
 * @Package com.task.entity
 * @Description: 付款凭证
 * @author 曾建森
 * @date 2012-11-8 下午03:08:25
 * @version V1.0
 */
public class PrintProof implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	/** 关联客户 */
	private String correlationCustomer;
	/** 评审单号 */
	private String reviewNumber;
	/** 合同号 */
	private String contractNumber;
	/** 日期 */
	private String date;
	/** 编号 */
	private String numbers;
	/** 收款单位 */
	private String collectionUnit;
	/** 总额 */
	private String total;
	/** 付款方式 */
	private String way;
	/** 付款情况 */
	private String situation;
	/** 付款条件 */
	private String conditions;
	/** 付款性质 */
	private String nature;
	/** 付款类别 */
	private String category;
	/** 经办人 */
	private String agent;
	/** 合计小写金额 */
	private String lowMoney;
	/** 审核状态 */
	private String status;
	/** 业务 */
	private Set<Business> buss;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCorrelationCustomer() {
		return correlationCustomer;
	}

	public void setCorrelationCustomer(String correlationCustomer) {
		this.correlationCustomer = correlationCustomer;
	}

	public String getReviewNumber() {
		return reviewNumber;
	}

	public void setReviewNumber(String reviewNumber) {
		this.reviewNumber = reviewNumber;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNumbers() {
		return numbers;
	}

	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}

	public String getCollectionUnit() {
		return collectionUnit;
	}

	public void setCollectionUnit(String collectionUnit) {
		this.collectionUnit = collectionUnit;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Set<Business> getBuss() {
		return buss;
	}

	public void setBuss(Set<Business> buss) {
		this.buss = buss;
	}

	public String getLowMoney() {
		return lowMoney;
	}

	public void setLowMoney(String lowMoney) {
		this.lowMoney = lowMoney;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}