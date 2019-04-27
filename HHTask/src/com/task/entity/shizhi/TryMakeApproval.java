package com.task.entity.shizhi;

import java.io.Serializable;

/**
 * 试制评审状态表（表名:ta_sk_TryMakeApproval）
 * @author txb
 *
 */
public class TryMakeApproval implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer epId;
	private String month;
	private String status;//（当前）
	private String approvalStatus;//审批状态
	private String addTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
  
}
