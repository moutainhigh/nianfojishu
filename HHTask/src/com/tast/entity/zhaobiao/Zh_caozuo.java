package com.tast.entity.zhaobiao;

import java.io.Serializable;

public class Zh_caozuo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;   
	private String  gongweiId;	//工位编号	
	private String shebeiId;	//设备编号		
	private String shebeiname;	//设备名称	
	private String shebeigongxuName;	//设备工序名称
	
	private String nameId ;  //可操作元工工号
	private String name  ;//可操作员工名称
	
	
	private String caozuodengji;   //操作登记说明

	private String caozuoshichang;   //操作时长
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getGongweiId() {
		return gongweiId;
	}


	public void setGongweiId(String gongweiId) {
		this.gongweiId = gongweiId;
	}


	public String getShebeiId() {
		return shebeiId;
	}


	public void setShebeiId(String shebeiId) {
		this.shebeiId = shebeiId;
	}


	public String getShebeiname() {
		return shebeiname;
	}


	public void setShebeiname(String shebeiname) {
		this.shebeiname = shebeiname;
	}


	public String getShebeigongxuName() {
		return shebeigongxuName;
	}


	public void setShebeigongxuName(String shebeigongxuName) {
		this.shebeigongxuName = shebeigongxuName;
	}


	public String getNameId() {
		return nameId;
	}


	public void setNameId(String nameId) {
		this.nameId = nameId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCaozuodengji() {
		return caozuodengji;
	}


	public void setCaozuodengji(String caozuodengji) {
		this.caozuodengji = caozuodengji;
	}


	public String getCaozuoshichang() {
		return caozuoshichang;
	}


	public void setCaozuoshichang(String caozuoshichang) {
		this.caozuoshichang = caozuoshichang;
	}
	
	
	
	
	
	
}
