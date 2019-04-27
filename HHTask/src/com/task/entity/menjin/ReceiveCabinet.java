package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 取物品表 2016-06-04
 * 
 * @author Li_Cong 表名 ta_mj_ReceiveCabinet 取物品表(页面选择添加)
 */
public class ReceiveCabinet implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer receiveQuantity;// 总取物品数量
	private String receiveWuName;// 取物品名称
	private String receiveFormat;// 取物品规格
	private Integer realReceiveQuantity;// 实际取物品数量
	private String receiveVerificationCode;// 取物品验证码

	private Integer receiveUserId;// 领取人Id
	private String receiveName;// 领取人名称
	private String receiveDept;// 领取人部门
	private String receiveCardId;// 领取人卡号
	private String receiveStatus;// 取物状态（待领取/领取中/已领取）
	private String verificationCodeStatus;// 验证码使用状态(已使用/未使用)
	private String returnZhi;// 返回信息

	private String failTime;// 失效时间
	private String addTime;// 添加时间
	private String receiveTime;// 取物时间

	private Integer posId;

	// get set

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getReceiveQuantity() {
		return receiveQuantity;
	}

	public void setReceiveQuantity(Integer receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
	}

	public String getReceiveVerificationCode() {
		return receiveVerificationCode;
	}

	public void setReceiveVerificationCode(String receiveVerificationCode) {
		this.receiveVerificationCode = receiveVerificationCode;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	public String getReceiveCardId() {
		return receiveCardId;
	}

	public void setReceiveCardId(String receiveCardId) {
		this.receiveCardId = receiveCardId;
	}

	public String getReceiveStatus() {
		return receiveStatus;
	}

	public void setReceiveStatus(String receiveStatus) {
		this.receiveStatus = receiveStatus;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Integer getRealReceiveQuantity() {
		return realReceiveQuantity;
	}

	public void setRealReceiveQuantity(Integer realReceiveQuantity) {
		this.realReceiveQuantity = realReceiveQuantity;
	}

	public Integer getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public String getVerificationCodeStatus() {
		return verificationCodeStatus;
	}

	public void setVerificationCodeStatus(String verificationCodeStatus) {
		this.verificationCodeStatus = verificationCodeStatus;
	}

	public String getReceiveWuName() {
		return receiveWuName;
	}

	public void setReceiveWuName(String receiveWuName) {
		this.receiveWuName = receiveWuName;
	}

	public String getReceiveFormat() {
		return receiveFormat;
	}

	public void setReceiveFormat(String receiveFormat) {
		this.receiveFormat = receiveFormat;
	}

	public Integer getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Integer receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getFailTime() {
		return failTime;
	}

	public void setFailTime(String failTime) {
		this.failTime = failTime;
	}

	public String getReturnZhi() {
		return returnZhi;
	}

	public void setReturnZhi(String returnZhi) {
		this.returnZhi = returnZhi;
	}

}