package com.task.entity;

import java.io.Serializable;

/***
 * 基本类
 * 
 * @author 刘培
 * 
 */
public class BtcBasic implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Float avgPrice;// 均价
	private Float buyFee;// 买入手续费
	private Float sellFee;// 卖出手续费
	private Float RechargeFee;// 充值手续费
	private Float ReflectFee;// 提现手续费

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Float getBuyFee() {
		return buyFee;
	}

	public void setBuyFee(Float buyFee) {
		this.buyFee = buyFee;
	}

	public Float getSellFee() {
		return sellFee;
	}

	public void setSellFee(Float sellFee) {
		this.sellFee = sellFee;
	}

	public Float getRechargeFee() {
		return RechargeFee;
	}

	public void setRechargeFee(Float rechargeFee) {
		RechargeFee = rechargeFee;
	}

	public Float getReflectFee() {
		return ReflectFee;
	}

	public void setReflectFee(Float reflectFee) {
		ReflectFee = reflectFee;
	}

}