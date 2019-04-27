package com.task.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Scrap
 * @Description: 报废
 * @author Damon
 * @date 2013-4-23 下午01:56:13
 * 
 * add relaction Fields
 * private Store store   关系:Scrap(many-to-one) : Store(one-to-many)
 */
public class Scrap implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
	private Integer id;
	private String number; //编号
	private String matetag;//名称
	private String format;//规格
	private Integer amount;//数量
	private String username;//员工
	private String dept;
	private Date badDate;//日期
	private String badView;//损失意见
	private String mix;//合成主码
	private String more1;//损失原因
	private String more2;//备注
	private String more3;
	
	private Integer state;//根据什么报废(借完报废为0或者直接报废1)
	private Store store;
	private Borrow borrow;

	public Scrap() {
	}

	/** minimal constructor */
	public Scrap(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMatetag() {
		return this.matetag;
	}

	public void setMatetag(String matetag) {
		this.matetag = matetag;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDept() {
		return this.dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Date getBadDate() {
		return badDate;
	}

	public void setBadDate(Date badDate) {
		this.badDate = badDate;
	}

	public String getBadView() {
		return this.badView;
	}

	public void setBadView(String badView) {
		this.badView = badView;
	}

	public String getMix() {
		return this.mix;
	}

	public void setMix(String mix) {
		this.mix = mix;
	}

	public String getMore1() {
		return this.more1;
	}

	public void setMore1(String more1) {
		this.more1 = more1;
	}

	public String getMore2() {
		return this.more2;
	}

	public void setMore2(String more2) {
		this.more2 = more2;
	}

	public String getMore3() {
		return this.more3;
	}

	public void setMore3(String more3) {
		this.more3 = more3;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
