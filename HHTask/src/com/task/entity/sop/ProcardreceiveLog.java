package com.task.entity.sop;
/**
 * 生产领料记录(ta_ProcardreceiveLog)
 * @author txb
 *
 */
public class ProcardreceiveLog  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private Integer procardId;//零件Id
	private Integer sellId;//出库记录Id
	private String markId;//件号
	private String selfCard;//生产批次
	private String sell_lot;//出库批次
	private Float sellCount;//出库数量
	private String unit;//单位
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
	public Integer getSellId() {
		return sellId;
	}
	public void setSellId(Integer sellId) {
		this.sellId = sellId;
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
	public String getSell_lot() {
		return sell_lot;
	}
	public void setSell_lot(String sellLot) {
		sell_lot = sellLot;
	}
	public Float getSellCount() {
		return sellCount;
	}
	public void setSellCount(Float sellCount) {
		this.sellCount = sellCount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	
}
