package com.task.entity.jxkh;

import java.io.Serializable;

/**
 * 各部门从各车间提取情况表:(ta_WorkShopTiQu)
 * @author wxf
 *
 */
public class WorkShopTiQu implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String dept;//部门/车间
	private Double xiaolv;//（由 WorkShopXiaoLvZz:xiaolv 获取）
	private Double a;//绩效包;//（由 WorkShopTiQuMx : tiquMoney 获取）
	private Double buMenTqMoney;//(a*xiaolv*k*(b/c)*d1*d2*d3*z ); 本表看不到的参数皆在: FenPeiQingKuang
	private String months;//月份;
	private FenPeiQingKuang fpqk;//各部门分配情况表
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public Double getXiaolv() {
		return xiaolv;
	}
	public void setXiaolv(Double xiaolv) {
		this.xiaolv = xiaolv;
	}
	public Double getA() {
		return a;
	}
	public void setA(Double a) {
		this.a = a;
	}
	public Double getBuMenTqMoney() {
		return buMenTqMoney;
	}
	public void setBuMenTqMoney(Double buMenTqMoney) {
		this.buMenTqMoney = buMenTqMoney;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public FenPeiQingKuang getFpqk() { 
		return fpqk;
	}
	public void setFpqk(FenPeiQingKuang fpqk) {
		this.fpqk = fpqk;
	}
	
	
	
	
}
