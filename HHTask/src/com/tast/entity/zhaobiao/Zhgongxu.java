package com.tast.entity.zhaobiao;

import java.io.Serializable;


public class Zhgongxu implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;			//主键	
	private String yuefen;		//月份		
	private String paihao;		//牌String
	private String guige;		//规格	
	private String danwei;		//单位	
	private Float jiage;		//  含税价格
	private String person;	   //录入人	
	private  String kongxian;   //税率
	private  Float buhanshui;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getYuefen() {
		return yuefen;
	}
	public void setYuefen(String yuefen) {
		this.yuefen = yuefen;
	}
	public String getPaihao() {
		return paihao;
	}
	public void setPaihao(String paihao) {
		this.paihao = paihao;
	}
	public String getGuige() {
		return guige;
	}
	public void setGuige(String guige) {
		this.guige = guige;
	}
	public String getDanwei() {
		return danwei;
	}
	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}
	public Float getJiage() {
		return jiage;
	}
	public void setJiage(Float jiage) {
		this.jiage = jiage;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getKongxian() {
		return kongxian;
	}
	public void setKongxian(String kongxian) {
		this.kongxian = kongxian;
	}
	public Float getBuhanshui() {
		return buhanshui;
	}
	public void setBuhanshui(Float buhanshui) {
		this.buhanshui = buhanshui;
	}
	



}
