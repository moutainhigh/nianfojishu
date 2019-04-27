package com.task.entity;

import java.io.Serializable;

public class Stylebook implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer qid; // 检验表ID
	private String nature; // 样品性质
	private String place; // 检测地点
	private String method; // 检测方法
	private String packing1; // 包装完好度
	private String packing2; // 包装完好度
	private String packing3; // 包装完好度
	private String sizeHeat1; // 隔热罩间隙
	private String sizeHeat2; // 隔热罩间隙
	private String checkx; // 检验销
	private String decree; // 方规
	private String tapped; // 内螺纹
	private String totalgrowth; // 总成长度
	private String orificesize; // 管口尺寸
	private String trend; // 走向
	private String surface1; // 外观
	private String surface2; // 外观
	private String surface3; // 外观
	private String surface4; // 外观
	private String sexfunction; // 性能
	private String numb;      //样品编号

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQid() {
		return qid;
	}

	public void setQid(Integer qid) {
		this.qid = qid;
	}

	public String getPacking1() {
		return packing1;
	}

	public void setPacking1(String packing1) {
		this.packing1 = packing1;
	}

	public String getPacking2() {
		return packing2;
	}

	public void setPacking2(String packing2) {
		this.packing2 = packing2;
	}

	public String getPacking3() {
		return packing3;
	}

	public void setPacking3(String packing3) {
		this.packing3 = packing3;
	}

	public String getSizeHeat1() {
		return sizeHeat1;
	}

	public void setSizeHeat1(String sizeHeat1) {
		this.sizeHeat1 = sizeHeat1;
	}

	public String getSizeHeat2() {
		return sizeHeat2;
	}

	public void setSizeHeat2(String sizeHeat2) {
		this.sizeHeat2 = sizeHeat2;
	}

	public String getCheckx() {
		return checkx;
	}

	public void setCheckx(String checkx) {
		this.checkx = checkx;
	}

	public String getDecree() {
		return decree;
	}

	public void setDecree(String decree) {
		this.decree = decree;
	}

	public String getTapped() {
		return tapped;
	}

	public void setTapped(String tapped) {
		this.tapped = tapped;
	}

	public String getTotalgrowth() {
		return totalgrowth;
	}

	public void setTotalgrowth(String totalgrowth) {
		this.totalgrowth = totalgrowth;
	}

	public String getOrificesize() {
		return orificesize;
	}

	public void setOrificesize(String orificesize) {
		this.orificesize = orificesize;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	public String getSurface1() {
		return surface1;
	}

	public void setSurface1(String surface1) {
		this.surface1 = surface1;
	}

	public String getSurface2() {
		return surface2;
	}

	public void setSurface2(String surface2) {
		this.surface2 = surface2;
	}

	public String getSurface3() {
		return surface3;
	}

	public void setSurface3(String surface3) {
		this.surface3 = surface3;
	}

	public String getSurface4() {
		return surface4;
	}

	public void setSurface4(String surface4) {
		this.surface4 = surface4;
	}

	public String getSexfunction() {
		return sexfunction;
	}

	public void setSexfunction(String sexfunction) {
		this.sexfunction = sexfunction;
	}

	public void setNumb(String numb) {
		this.numb = numb;
	}

	public String getNumb() {
		return numb;
	}

	
	

	
}
