package com.task.entity.sop;

import java.util.Set;

import com.task.util.FieldMeta;

/**
 * 超损补料申请单
 * @author 表名:（ta_ProcardCsBlOrder）
 *
 */

public class ProcardCsBlOrder implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	@FieldMeta(name="订单号(内部)")
	private String orderNumber;//订单号(内部)
	@FieldMeta(name="业务件号")
	private String ywMarkId;//业务件号
	@FieldMeta(name="总成件号")
	private String rootMarkId;//总成件号
	@FieldMeta(name="总成批次")
	private String rootSelfCard;//总成批次
	@FieldMeta(name="订单数量")
	private Float orderCount;//订单数量
	@FieldMeta(name="总成件号")
	private Integer rootId;//总成件号
	@FieldMeta(name="申请部门")
	private String sqdept;//申请部门
	@FieldMeta(name="申请人姓名")
	private String sqUsersName;//申请人姓名
	@FieldMeta(name="申请人工号")
	private String sqUsersCode;//申请人工号
	@FieldMeta(name="申请日期")
	private String sqdate;//申请日期;
	private String addTime;//添加时间
	private String reason;//原因
	private Integer epId;
	private String epStatus;
	@FieldMeta(name="补料类型")
	private String type;//外委补料,超损补料
	private Float sumPrice;//总额
	private Set<ProcardCsBl> csblSet;//超损补料表 一对多
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getYwMarkId() {
		return ywMarkId;
	}
	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}
	public String getRootMarkId() {
		return rootMarkId;
	}
	public void setRootMarkId(String rootMarkId) {
		this.rootMarkId = rootMarkId;
	}
	public String getRootSelfCard() {
		return rootSelfCard;
	}
	public void setRootSelfCard(String rootSelfCard) {
		this.rootSelfCard = rootSelfCard;
	}
	public Float getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(Float orderCount) {
		this.orderCount = orderCount;
	}
	public String getSqdept() {
		return sqdept;
	}
	public void setSqdept(String sqdept) {
		this.sqdept = sqdept;
	}
	public String getSqUsersName() {
		return sqUsersName;
	}
	public void setSqUsersName(String sqUsersName) {
		this.sqUsersName = sqUsersName;
	}
	public String getSqUsersCode() {
		return sqUsersCode;
	}
	public void setSqUsersCode(String sqUsersCode) {
		this.sqUsersCode = sqUsersCode;
	}
	public String getSqdate() {
		return sqdate;
	}
	public void setSqdate(String sqdate) {
		this.sqdate = sqdate;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Set<ProcardCsBl> getCsblSet() {
		return csblSet;
	}
	public void setCsblSet(Set<ProcardCsBl> csblSet) {
		this.csblSet = csblSet;
	}
	
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getEpStatus() {
		return epStatus;
	}
	public void setEpStatus(String epStatus) {
		this.epStatus = epStatus;
	}
	public Integer getRootId() {
		return rootId;
	}
	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}
	public Float getSumPrice() {
		return sumPrice;
	}
	public void setSumPrice(Float sumPrice) {
		this.sumPrice = sumPrice;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
