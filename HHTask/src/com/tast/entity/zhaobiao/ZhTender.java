package com.tast.entity.zhaobiao;

import java.io.Serializable;



public class ZhTender implements Serializable {
	private static final long serialVersionUID = 1L;


	private Integer id;
	private String fid;
	private String mid;
	private String title ;
	private String url;
	private String classs;
	private String des;
	private String creattime;
	private String repairtime;
	private String creator;
	private String repairtor;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getClasss() {
		return classs;
	}
	public void setClasss(String classs) {
		this.classs = classs;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getCreattime() {
		return creattime;
	}
	public void setCreattime(String creattime) {
		this.creattime = creattime;
	}
	public String getRepairtime() {
		return repairtime;
	}
	public void setRepairtime(String repairtime) {
		this.repairtime = repairtime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getRepairtor() {
		return repairtor;
	}
	public void setRepairtor(String repairtor) {
		this.repairtor = repairtor;
	}
	
	

}