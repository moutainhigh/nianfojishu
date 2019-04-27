package com.task.entity.sop;
/**
 * 设变外购增减明细ta_sop_w_ProcardSbWgDetail
 * @author txb
 *
 */
public class ProcardSbWgDetail  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private Integer procardId;
	private String markId;
	private String selfCard;
	private String proName;
	private String banbenNumber;
	private Integer banci;
	private Float sbCount;
	private Float clCount;
	private String addTime;
	private Integer orderId;//订单Id;
	private String orderNumber;//订单号（内）
	private String orderOutNumber;//订单号(外)
	private String rootMarkId;//总成件号
	private String rootSelfCard;//总成批次
	private String ywMarkId;//业务件号
	private ProcardSbWg procardSbWg;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProcardId() {
		return procardId;
	}
	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
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
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getBanbenNumber() {
		return banbenNumber;
	}
	public void setBanbenNumber(String banbenNumber) {
		this.banbenNumber = banbenNumber;
	}
	public Integer getBanci() {
		return banci;
	}
	public void setBanci(Integer banci) {
		this.banci = banci;
	}
	public Float getSbCount() {
		return sbCount;
	}
	public void setSbCount(Float sbCount) {
		this.sbCount = sbCount;
	}
	public Float getClCount() {
		return clCount;
	}
	public void setClCount(Float clCount) {
		this.clCount = clCount;
	}
	public ProcardSbWg getProcardSbWg() {
		return procardSbWg;
	}
	public void setProcardSbWg(ProcardSbWg procardSbWg) {
		this.procardSbWg = procardSbWg;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderOutNumber() {
		return orderOutNumber;
	}
	public void setOrderOutNumber(String orderOutNumber) {
		this.orderOutNumber = orderOutNumber;
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
	public String getYwMarkId() {
		return ywMarkId;
	}
	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}
	
	
}
