package com.task.entity;

import java.io.Serializable;

/**
 * 设备
 * @author 马凯
 * 
 */
public class Tequipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;// 设备名称
	private String productionYear;// 生产年份
	private String factory;// 制造厂
	private String equipmentType;// 设备型号
	private String deviceNumber;// 设备编号
	private String location;// 所在地
	private int equipmentValue;// 设备价值
	private int residual;// 设备残值
	private double power;// 功率
	private Project project;//关键在哪个项目

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

	public String getProductionYear() {
		return productionYear;
	}

	public void setProductionYear(String productionYear) {
		this.productionYear = productionYear;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getEquipmentValue() {
		return equipmentValue;
	}

	public void setEquipmentValue(int equipmentValue) {
		this.equipmentValue = equipmentValue;
	}

	public int getResidual() {
		return residual;
	}

	public void setResidual(int residual) {
		this.residual = residual;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
