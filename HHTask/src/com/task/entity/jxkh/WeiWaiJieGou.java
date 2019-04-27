package com.task.entity.jxkh;

import java.io.Serializable;

/**
 * 委外比结构比表:(ta_WeiWaiJieGou)
 * @author wxf
 *
 */
public class WeiWaiJieGou implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Float changzhi;//产值 （万）
	private Float bgbl;//包工包料 (万)
	private Float gxww;//工序外委(万)
	private Float wwgj;//委外共计(万)
	private Float wwb;//委外比;//%
	private Float wwxs;//委外系数;//
	private Float rkcz;//入库产值(万)
	private Float scBOM;//生产BOM(万)
	private Float jgb;//结构比%
	private Float jgxs;//结构系数
	private Float jgbmb;//结构比目标%
	private Float wwbmb;//委外比目标%
	private String years;//年份(yyyy年)
	private String months;//月份(yyyy-MM)
	private Integer month0;//月
	private String addTime;//添加时间
	private String addUsersName;//添加人
	private Float str;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Float getChangzhi() {
		return changzhi;
	}
	public void setChangzhi(Float changzhi) {
		this.changzhi = changzhi;
	}
	public Float getBgbl() {
		return bgbl;
	}
	public void setBgbl(Float bgbl) {
		this.bgbl = bgbl;
	}
	public Float getGxww() {
		return gxww;
	}
	public void setGxww(Float gxww) {
		this.gxww = gxww;
	}
	public Float getWwgj() {
		return wwgj;
	}
	public void setWwgj(Float wwgj) {
		this.wwgj = wwgj;
	}
	public Float getWwb() {
		return wwb;
	}
	public void setWwb(Float wwb) {
		this.wwb = wwb;
	}
	public Float getWwxs() {
		return wwxs;
	}
	public void setWwxs(Float wwxs) {
		this.wwxs = wwxs;
	}
	public Float getRkcz() {
		return rkcz;
	}
	public void setRkcz(Float rkcz) {
		this.rkcz = rkcz;
	}
	public Float getScBOM() {
		return scBOM;
	}
	public void setScBOM(Float scBOM) {
		this.scBOM = scBOM;
	}
	public Float getJgb() {
		return jgb;
	}
	public void setJgb(Float jgb) {
		this.jgb = jgb;
	}
	public Float getJgxs() {
		return jgxs;
	}
	public void setJgxs(Float jgxs) {
		this.jgxs = jgxs;
	}
	public Float getJgbmb() {
		return jgbmb;
	}
	public void setJgbmb(Float jgbmb) {
		this.jgbmb = jgbmb;
	}
	public Float getWwbmb() {
		return wwbmb;
	}
	public void setWwbmb(Float wwbmb) {
		this.wwbmb = wwbmb;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public Integer getMonth0() {
		return month0;
	}
	public void setMonth0(Integer month0) {
		this.month0 = month0;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUsersName() {
		return addUsersName;
	}
	public void setAddUsersName(String addUsersName) {
		this.addUsersName = addUsersName;
	}
	public Float getStr() {
		return str;
	}
	public void setStr(Float str) {
		this.str = str;
	}
	
	
	
	
	
}
