package com.task.entity;

import java.io.Serializable;
/**
 * 工序
 * @author 马凯
 *
 */
public class Tprocess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer xuhao;//工序号
	private String name;//工序名
	private Ttooling tooling;//工装
	private Tequipment equipment;// 设备
	private Tdetail detail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getXuhao() {
		return xuhao;
	}

	public void setXuhao(Integer xuhao) {
		this.xuhao = xuhao;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ttooling getTooling() {
		return tooling;
	}

	public void setTooling(Ttooling tooling) {
		this.tooling = tooling;
	}

	public Tequipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Tequipment equipment) {
		this.equipment = equipment;
	}

	public Tdetail getDetail() {
		return detail;
	}

	public void setDetail(Tdetail detail) {
		this.detail = detail;
	}
}
