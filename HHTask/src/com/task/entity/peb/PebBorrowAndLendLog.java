package com.task.entity.peb;

import java.io.Serializable;

import com.task.util.FieldMeta;

/**
 * 生产效率简报（借入借出记录表）
 * ta_pebUsers
 * @author xs-cy
 *
 */
public class PebBorrowAndLendLog implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String addTime;
	private Integer addUserId;
	@FieldMeta(name="申请人")
	private String addUserName;
	private Integer pebUsersId;
	
	private String lendBanzu;//借出班组
	private Integer lendNum;//借出人数
	
	@FieldMeta(name="申请借出班组")//这个注解是在显示的时候用的
	private String borrowBanzu;//借入班组

	@FieldMeta(name="借入人数")
	private Integer borrowNum;//借入人数

	@FieldMeta(name="申请班组")
	private String sqBanzu;//申请班组
	@FieldMeta(name="申请时间")
	private String sqTime;
	@FieldMeta(name="工作小时数")
	private Float gzHour;//工作小时数

	@FieldMeta(name="备注")
	private String remarks;
	private String epStatus;
	private Integer epId;
	
	//取消流程
	private Integer cancalEpId;
	private String cancalEpStatus;
	
	private Integer borrowJiegouId;//借入班组结构的id
	private Integer lendJiegouId;//借出班组结构的id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Integer getAddUserId() {
		return addUserId;
	}
	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}
	public String getAddUserName() {
		return addUserName;
	}
	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}
	public String getLendBanzu() {
		return lendBanzu;
	}
	public void setLendBanzu(String lendBanzu) {
		this.lendBanzu = lendBanzu;
	}
	public Integer getLendNum() {
		return lendNum;
	}
	public void setLendNum(Integer lendNum) {
		this.lendNum = lendNum;
	}
	public String getBorrowBanzu() {
		return borrowBanzu;
	}
	public void setBorrowBanzu(String borrowBanzu) {
		this.borrowBanzu = borrowBanzu;
	}
	public Integer getBorrowNum() {
		return borrowNum;
	}
	public void setBorrowNum(Integer borrowNum) {
		this.borrowNum = borrowNum;
	}
	public Integer getPebUsersId() {
		return pebUsersId;
	}
	public void setPebUsersId(Integer pebUsersId) {
		this.pebUsersId = pebUsersId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSqTime() {
		return sqTime;
	}
	public void setSqTime(String sqTime) {
		this.sqTime = sqTime;
	}
	public String getEpStatus() {
		return epStatus;
	}
	public void setEpStatus(String epStatus) {
		this.epStatus = epStatus;
	}
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getSqBanzu() {
		return sqBanzu;
	}
	public void setSqBanzu(String sqBanzu) {
		this.sqBanzu = sqBanzu;
	}
	public Float getGzHour() {
		return gzHour;
	}
	public void setGzHour(Float gzHour) {
		this.gzHour = gzHour;
	}
	public Integer getCancalEpId() {
		return cancalEpId;
	}
	public void setCancalEpId(Integer cancalEpId) {
		this.cancalEpId = cancalEpId;
	}
	public String getCancalEpStatus() {
		return cancalEpStatus;
	}
	public void setCancalEpStatus(String cancalEpStatus) {
		this.cancalEpStatus = cancalEpStatus;
	}
	public Integer getBorrowJiegouId() {
		return borrowJiegouId;
	}
	public void setBorrowJiegouId(Integer borrowJiegouId) {
		this.borrowJiegouId = borrowJiegouId;
	}
	public Integer getLendJiegouId() {
		return lendJiegouId;
	}
	public void setLendJiegouId(Integer lendJiegouId) {
		this.lendJiegouId = lendJiegouId;
	}
	
	
	
}