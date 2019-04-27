package com.task.entity;

import java.io.Serializable;

public class DTOProcess  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // 工序ID
	private String jobNum; // 人员工号
	private Double OPLabourBeat = 0.0; // 人工节拍
	private Double OPEquipmentBeat = 0.0; // 设备节拍
	private Double PRLabourBeat = 0.0; // 人工节拍
	private Double PRPrepareTIme = 0.0; // 准备次数
	private String handlers; // 操作者
	private Double sumMoney = 0.0; // 工序分配额
	private Double unitPrice = 0.0; // 单个工序分配额

	public DTOProcess(int id, String jobNum, Double oPLabourBeat,
			Double oPEquipmentBeat, Double pRLabourBeat, Double pRPrepareTIme,
			String handlers) {
		this.id = id;
		this.jobNum = jobNum;
		OPLabourBeat = oPLabourBeat;
		OPEquipmentBeat = oPEquipmentBeat;
		PRLabourBeat = pRLabourBeat;
		PRPrepareTIme = pRPrepareTIme;
		this.handlers = handlers;
	}

	public DTOProcess(int id, String jobNum, Double oPLabourBeat,
			Double oPEquipmentBeat, Double pRLabourBeat, Double pRPrepareTIme,
			String handlers, Double sumMoney, Double unitPrice) {
		this.id = id;
		this.jobNum = jobNum;
		OPLabourBeat = oPLabourBeat;
		OPEquipmentBeat = oPEquipmentBeat;
		PRLabourBeat = pRLabourBeat;
		PRPrepareTIme = pRPrepareTIme;
		this.handlers = handlers;
		this.sumMoney = sumMoney;
		this.unitPrice = unitPrice;
	}

	public DTOProcess() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getOPLabourBeat() {
		return OPLabourBeat;
	}

	public void setOPLabourBeat(Double oPLabourBeat) {
		OPLabourBeat = oPLabourBeat;
	}

	public Double getOPEquipmentBeat() {
		return OPEquipmentBeat;
	}

	public void setOPEquipmentBeat(Double oPEquipmentBeat) {
		OPEquipmentBeat = oPEquipmentBeat;
	}

	public Double getPRLabourBeat() {
		return PRLabourBeat;
	}

	public void setPRLabourBeat(Double pRLabourBeat) {
		PRLabourBeat = pRLabourBeat;
	}

	public Double getPRPrepareTIme() {
		return PRPrepareTIme;
	}

	public void setPRPrepareTIme(Double pRPrepareTIme) {
		PRPrepareTIme = pRPrepareTIme;
	}

	public String getJobNum() {
		return jobNum;
	}

	public void setJobNum(String jobNum) {
		this.jobNum = jobNum;
	}

	public String getHandlers() {
		return handlers;
	}

	public void setHandlers(String handlers) {
		this.handlers = handlers;
	}

	public Double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(Double sumMoney) {
		this.sumMoney = sumMoney;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
}
