package com.task.entity;
/**
 * 
 * @author wxf
 *
 *	项目名 :库存汇总表 (ta_GoodsSummary)
 */
public class GoodsSummary  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	
	private Integer id;
	private String goodsClass;//库别
	private String months;//月份
	private String markId;//件号
	private String name;//名称
	private String unit;//单位
	private String specification;//规格
	/**
	 * 期初库存
	 */
	private Double qichuNum;//期初数量
	private Double qichuPrice;//期初单价
	private Double qichuMoney;//期初金额
	/**
	 * 本月入库
	 */
	private Double rukuNum;//入库数量
	private Double rukuPrice;//入库单价
	private Double rukuMoney;//入库金额
	/**
	 * 本月出库
	 */
	private Double chukuNum;//出库数量
	private Double chukuPrice;//出库单价
	private Double chukuMoney;//出库金额
	/**
	 * 期末库存
	 * 
	 */
	private Double qimoNum;//期末数量
	private Double qimoPrice;//期末单价
	private Double qimoMoney;//期末金额
	

	private String  remarks;//备注

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public Double getQichuNum() {
		return qichuNum;
	}

	public void setQichuNum(Double qichuNum) {
		this.qichuNum = qichuNum;
	}

	public Double getQichuPrice() {
		return qichuPrice;
	}

	public void setQichuPrice(Double qichuPrice) {
		this.qichuPrice = qichuPrice;
	}

	public Double getQichuMoney() {
		return qichuMoney;
	}

	public void setQichuMoney(Double qichuMoney) {
		this.qichuMoney = qichuMoney;
	}

	public Double getRukuNum() {
		return rukuNum;
	}

	public void setRukuNum(Double rukuNum) {
		this.rukuNum = rukuNum;
	}

	public Double getRukuPrice() {
		return rukuPrice;
	}

	public void setRukuPrice(Double rukuPrice) {
		this.rukuPrice = rukuPrice;
	}

	public Double getRukuMoney() {
		return rukuMoney;
	}

	public void setRukuMoney(Double rukuMoney) {
		this.rukuMoney = rukuMoney;
	}

	public Double getChukuNum() {
		return chukuNum;
	}

	public void setChukuNum(Double chukuNum) {
		this.chukuNum = chukuNum;
	}

	public Double getChukuPrice() {
		return chukuPrice;
	}

	public void setChukuPrice(Double chukuPrice) {
		this.chukuPrice = chukuPrice;
	}

	public Double getChukuMoney() {
		return chukuMoney;
	}

	public void setChukuMoney(Double chukuMoney) {
		this.chukuMoney = chukuMoney;
	}

	public Double getQimoNum() {
		return qimoNum;
	}

	public void setQimoNum(Double qimoNum) {
		this.qimoNum = qimoNum;
	}

	public Double getQimoPrice() {
		return qimoPrice;
	}

	public void setQimoPrice(Double qimoPrice) {
		this.qimoPrice = qimoPrice;
	}

	public Double getQimoMoney() {
		return qimoMoney;
	}

	public void setQimoMoney(Double qimoMoney) {
		this.qimoMoney = qimoMoney;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getMonths() {
		return months;
	}

	public void setMonths(String months) {
		this.months = months;
	}

	public String getGoodsClass() {
		return goodsClass;
	}

	public void setGoodsClass(String goodsClass) {
		this.goodsClass = goodsClass;
	}


	
	
}
