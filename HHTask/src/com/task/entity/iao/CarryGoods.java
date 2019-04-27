package com.task.entity.iao;

import java.io.Serializable;

/**
 * 贾辉辉
 * 携带物品表（ta_iao_carryGoods）
 * */
public class CarryGoods implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String goodsName;//物品名称
	private Float count;     //数量
	private String more;//备注
	private IAOApply iaoApply;//对应外出申请单
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public Float getCount() {
		return count;
	}
	public void setCount(Float count) {
		this.count = count;
	}
	public IAOApply getIaoApply() {
		return iaoApply;
	}
	public void setIaoApply(IAOApply iaoApply) {
		this.iaoApply = iaoApply;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	
}
