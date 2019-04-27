package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 车辆内外状态表 20151101
 * 
 * @author Li_Cong 表名 ta_mj_CarInOutType 记录车辆是否在内部
 */
public class CarInOutType implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String carPai;// 车牌号
	private String carInOut;// 在进门/出门
	private String inOut;// 在停车场内/外
	private String addTime;// 添加时间
	private String updateTime;// 修改时间
	// get set

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInOut() {
		return inOut;
	}

	public void setInOut(String inOut) {
		this.inOut = inOut;
	}

	public String getCarPai() {
		return carPai;
	}

	public void setCarPai(String carPai) {
		this.carPai = carPai;
	}

	public String getCarInOut() {
		return carInOut;
	}

	public void setCarInOut(String carInOut) {
		this.carInOut = carInOut;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
