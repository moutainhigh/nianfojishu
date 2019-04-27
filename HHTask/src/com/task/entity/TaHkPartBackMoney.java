package com.task.entity;

/**
 * TaHkHuikuan generated by MyEclipse Persistence Tools
 * 回款零件单（表：ta_hk_PartBackMoney）
 */

public class TaHkPartBackMoney implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String hkbmNum;//发票单号
	private String hkbmClientCom;//客户名称
	private String hkSellOrderId;//订单号
    private Float hkbmMoney;//回款金额
    private String hkmarkId;//件号
    private Float hkSellPrice;//单价
    private Float hkbmCount;//回款数量
    private String hkbmMoneyUnit;//币种
    private String hkTime;//回款时间
    private Integer backMoneyId;//回款单Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHkbmClientCom() {
		return hkbmClientCom;
	}
	public void setHkbmClientCom(String hkbmClientCom) {
		this.hkbmClientCom = hkbmClientCom;
	}
	public Float getHkbmMoney() {
		return hkbmMoney;
	}
	public void setHkbmMoney(Float hkbmMoney) {
		this.hkbmMoney = hkbmMoney;
	}
	public Float getHkbmCount() {
		return hkbmCount;
	}
	public void setHkbmCount(Float hkbmCount) {
		this.hkbmCount = hkbmCount;
	}
	public String getHkbmMoneyUnit() {
		return hkbmMoneyUnit;
	}
	public void setHkbmMoneyUnit(String hkbmMoneyUnit) {
		this.hkbmMoneyUnit = hkbmMoneyUnit;
	}
	public Integer getBackMoneyId() {
		return backMoneyId;
	}
	public void setBackMoneyId(Integer backMoneyId) {
		this.backMoneyId = backMoneyId;
	}
	public String getHkmarkId() {
		return hkmarkId;
	}
	public void setHkmarkId(String hkmarkId) {
		this.hkmarkId = hkmarkId;
	}
	public String getHkSellOrderId() {
		return hkSellOrderId;
	}
	public void setHkSellOrderId(String hkSellOrderId) {
		this.hkSellOrderId = hkSellOrderId;
	}
	public Float getHkSellPrice() {
		return hkSellPrice;
	}
	public void setHkSellPrice(Float hkSellPrice) {
		this.hkSellPrice = hkSellPrice;
	}
	public String getHkbmNum() {
		return hkbmNum;
	}
	public void setHkbmNum(String hkbmNum) {
		this.hkbmNum = hkbmNum;
	}
	public String getHkTime() {
		return hkTime;
	}
	public void setHkTime(String hkTime) {
		this.hkTime = hkTime;
	}
	
    
	// Constructors
    
    

	
	

}