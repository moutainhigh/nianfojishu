package com.task.entity.sop;
/**
 * 工序提交后半成品临时存储日志
 * @author txb
 *
 */
public class ProcessSaveLog  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String warehouse;//库别
	private String goodHouseName;//仓区
	private String goodsStorePosition;//库位
	private String markId;//件号
	private String selfCard;//批次
	private Integer processNo;//工序号
	private String processName;//工序名称
	private Float ccCount;//存储数量
	private Integer processId;//工序id
	private String addTime;//
	private String addUser;//
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getGoodHouseName() {
		return goodHouseName;
	}
	public void setGoodHouseName(String goodHouseName) {
		this.goodHouseName = goodHouseName;
	}
	public String getGoodsStorePosition() {
		return goodsStorePosition;
	}
	public void setGoodsStorePosition(String goodsStorePosition) {
		this.goodsStorePosition = goodsStorePosition;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getSelfCard() {
		return selfCard;
	}
	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}
	public Float getCcCount() {
		return ccCount;
	}
	public void setCcCount(Float ccCount) {
		this.ccCount = ccCount;
	}
	public Integer getProcessId() {
		return processId;
	}
	public void setProcessId(Integer processId) {
		this.processId = processId;
	}
	public Integer getProcessNo() {
		return processNo;
	}
	public void setProcessNo(Integer processNo) {
		this.processNo = processNo;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUser() {
		return addUser;
	}
	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}
	
	
}
