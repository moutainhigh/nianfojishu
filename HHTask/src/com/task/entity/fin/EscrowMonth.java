package com.task.entity.fin;

import java.io.Serializable;
import java.util.List;

/**
 * 委托付款月度汇总（ta_fin_EscrowMonth）
 * @author txb
 *
 */
public class EscrowMonth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer id;
	public String payCom;//付款公司 
	public String month;//月份
	public Integer applyUserId;//申请人id
	public String applyUserName;
	public String applyUserCode;
	public String applyTime;//申请时间
	public Integer epId;//审批Id
	public String epStatus;//审批状态
	private String spTime;//审批时间
	public List<Escrow> escrowlist;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPayCom() {
		return payCom;
	}
	public void setPayCom(String payCom) {
		this.payCom = payCom;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	public Integer getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public String getApplyUserCode() {
		return applyUserCode;
	}
	public void setApplyUserCode(String applyUserCode) {
		this.applyUserCode = applyUserCode;
	}
	public String getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
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
	public List<Escrow> getEscrowlist() {
		return escrowlist;
	}
	public void setEscrowlist(List<Escrow> escrowlist) {
		this.escrowlist = escrowlist;
	}
	public String getSpTime() {
		return spTime;
	}
	public void setSpTime(String spTime) {
		this.spTime = spTime;
	}
	
	
	
	
}
