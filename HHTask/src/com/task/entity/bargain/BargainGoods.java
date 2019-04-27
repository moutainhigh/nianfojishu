package com.task.entity.bargain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/***
 * 议价物品表
 * 
 * @author 毛小龙 ta_BargainGoods（与主表议价表多对一）
 */
public class BargainGoods implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String goods_name;// 品名
	private String goods_format;// 规格
	private String goods_amount;// 采购数量
	private String goods_unit;// 单位
	private String purchase_delivery;// 采购交期
	private String quality_requirements;// 质量要求
	private Integer dataId;// 对应数据id(外购外委评审)
	private String remark;// 备注
	private Bargain bargain;// 关联主表（议价表）
	private Float price;// 价格（页面显示使用）

	public String getGoods_unit() {
		return goods_unit;
	}

	public void setGoods_unit(String goodsUnit) {
		goods_unit = goodsUnit;
	}

	@JSONField(serialize = false)
	public Bargain getBargain() {
		return bargain;
	}

	public void setBargain(Bargain bargain) {
		this.bargain = bargain;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goodsName) {
		goods_name = goodsName;
	}

	public String getGoods_format() {
		return goods_format;
	}

	public void setGoods_format(String goodsFormat) {
		goods_format = goodsFormat;
	}

	public String getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(String goodsAmount) {
		goods_amount = goodsAmount;
	}

	public String getPurchase_delivery() {
		return purchase_delivery;
	}

	public void setPurchase_delivery(String purchaseDelivery) {
		purchase_delivery = purchaseDelivery;
	}

	public String getQuality_requirements() {
		return quality_requirements;
	}

	public void setQuality_requirements(String qualityRequirements) {
		quality_requirements = qualityRequirements;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static void main(String[] args) {
		String a = "listcompany[0].bargList[0].amount";
		int count = 2;
		a = a.substring(0, a.indexOf("[") + 1) + count
				+ a.substring(a.indexOf("]"), a.length());
		System.out.println(a);
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

}
