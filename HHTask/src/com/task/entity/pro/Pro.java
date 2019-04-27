package com.task.entity.pro;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目管理
 * @表名 p_pro
 * @author 陈曦
 * 
 */
public class Pro implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;//
	private String name;// 项目名称
	private String code;// 项目编码
	private String status;// 项目状态(立项、启动/取消、完成)
	private Integer createUserId;// 创建人ID
	private String createUserName;// 创建人姓名
	private Date createDate;// 创建时间
	private Date finishDate;// 结束时间
	private Integer epId;// 审批ID
	private String epStatus;// 审批状态
	private Integer clientId;// 客户ID
	private String clientName;// 客户姓名
	private Double budget;// 项目预算
	private Double cgMoney;// 采购成本
	private Double peopleMoney;//人员成本

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
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

	public Double getCgMoney() {
		return cgMoney;
	}

	public void setCgMoney(Double cgMoney) {
		this.cgMoney = cgMoney;
	}

	public Double getPeopleMoney() {
		return peopleMoney;
	}

	public void setPeopleMoney(Double peopleMoney) {
		this.peopleMoney = peopleMoney;
	}

}
