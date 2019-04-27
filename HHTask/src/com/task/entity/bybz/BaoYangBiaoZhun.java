package com.task.entity.bybz;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.entity.Machine;

/**
 * 
 * @author 王晓飞 表名:ta_bybz_BaoYangBiaoZhun(保养/校验、标准表 ;) 用于设备保养; 和设备表 (Machine)
 *         多对一关系 和 量具、检具表(CheckoutAndGages)多对一关系
 *
 */
public class BaoYangBiaoZhun implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;//
	private Integer baoyangCycle;// 保养周期(天)
	private String baoyangCondition;// 保养条件
	private String baoyangMeans;// 保养方法
	private String nextbyTime;// 下次保养时间;
	private Machine machine;// 设备表 多对一
	private CheckoutAndGages checkoutAndGages;// 量、检具表 多对一

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBaoyangCycle() {
		return baoyangCycle;
	}

	public void setBaoyangCycle(Integer baoyangCycle) {
		this.baoyangCycle = baoyangCycle;
	}

	public String getBaoyangCondition() {
		return baoyangCondition;
	}

	public void setBaoyangCondition(String baoyangCondition) {
		this.baoyangCondition = baoyangCondition;
	}

	public String getBaoyangMeans() {
		return baoyangMeans;
	}

	public void setBaoyangMeans(String baoyangMeans) {
		this.baoyangMeans = baoyangMeans;
	}

	public String getNextbyTime() {
		return nextbyTime;
	}

	public void setNextbyTime(String nextbyTime) {
		this.nextbyTime = nextbyTime;
	}

	@JSONField(serialize = false)
	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	@JSONField(serialize = false)
	public CheckoutAndGages getCheckoutAndGages() {
		return checkoutAndGages;
	}

	public void setCheckoutAndGages(CheckoutAndGages checkoutAndGages) {
		this.checkoutAndGages = checkoutAndGages;
	}

}
