package com.tast.entity.zhaobiao;

import java.io.Serializable;

public class Zh_CaozuoDengji implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;   
	private Integer shebeiId;    //Zh_shebei 的id
	private String caozuodengji;   //操作登记说明
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getShebeiId() {
		return shebeiId;
	}
	public void setShebeiId(Integer shebeiId) {
		this.shebeiId = shebeiId;
	}
	public String getCaozuodengji() {
		return caozuodengji;
	}
	public void setCaozuodengji(String caozuodengji) {
		this.caozuodengji = caozuodengji;
	}
    
	
	
	
}
