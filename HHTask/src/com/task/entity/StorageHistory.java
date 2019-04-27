package com.task.entity;

import java.io.Serializable;
import java.util.Date;

/***
 * 打印任务表(表名：ta_StorageHistory)
 * @author work
 * 
 */
public class StorageHistory implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private Date date; // 入库日期
	private String matetag;// 名称
	private String format;// 规格
	private String carModel;// 车型
	private String unit;// 单位
	private String storehouse;// 仓库
	private String parClass;// 分类
	private String position;// 位置
	private Float num;// 数量
	private Float price;// 价格
	private Float money;// 金额
	private String more;// 备注
	private Float storageTaxPrice;
	private Float storageTaxMoney;
	private String jinbanren;// 经办人
	private String number;// 编号
	private String dept;//部门
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMatetag() {
		return matetag;
	}

	public void setMatetag(String matetag) {
		this.matetag = matetag;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(String storehouse) {
		this.storehouse = storehouse;
	}

	public String getParClass() {
		return parClass;
	}

	public void setParClass(String parClass) {
		this.parClass = parClass;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Float getNum() {
		return num;
	}

	public void setNum(Float num) {
		this.num = num;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public Float getStorageTaxPrice() {
		return storageTaxPrice;
	}

	public void setStorageTaxPrice(Float storageTaxPrice) {
		this.storageTaxPrice = storageTaxPrice;
	}

	public Float getStorageTaxMoney() {
		return storageTaxMoney;
	}

	public void setStorageTaxMoney(Float storageTaxMoney) {
		this.storageTaxMoney = storageTaxMoney;
	}

	public String getJinbanren() {
		return jinbanren;
	}

	public void setJinbanren(String jinbanren) {
		this.jinbanren = jinbanren;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

}