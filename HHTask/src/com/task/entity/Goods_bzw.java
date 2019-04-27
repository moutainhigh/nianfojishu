package com.task.entity;

/**
 * 包装物(表名:ta_Goods_bzw)
 * 
 *@author 刘培
 */

public class Goods_bzw  implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id; // ID
	private String name;// 名称
	private String format;// 规格
	private Integer count;// 数量
	private String barcode;// 条码
	private String addTime;// 添加时间
	private Goods_bzsq goods_bzsq;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Goods_bzsq getGoods_bzsq() {
		return goods_bzsq;
	}

	public void setGoods_bzsq(Goods_bzsq goodsBzsq) {
		goods_bzsq = goodsBzsq;
	}

	

}