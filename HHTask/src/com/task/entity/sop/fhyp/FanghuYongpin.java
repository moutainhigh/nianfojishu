package com.task.entity.sop.fhyp;

import java.io.Serializable;
import java.util.Date;

import com.task.entity.Users;

public class FanghuYongpin   implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**ID*/
	private Integer id;
	
	/**防护用品名字*/
	private String fanghuYongpinName;

	/**防护用品规格*/
	private String fanghuYongpinGuige;
	/**所属防护用品ID*/
	private Integer parentId;
	
	/**客户端参数*/
	private String params;

	public String getParams() {
		if(params!=null){
			return params.replace("\\t", "").replace("\\r","").replace("\\n","").replace("\\f","").replace("\\","").replace(" ","");
		}
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFanghuYongpinName() {
		return fanghuYongpinName;
	}

	public void setFanghuYongpinName(String fanghuYongpinName) {
		this.fanghuYongpinName = fanghuYongpinName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getFanghuYongpinGuige() {
		return fanghuYongpinGuige;
	}

	public void setFanghuYongpinGuige(String fanghuYongpinGuige) {
		this.fanghuYongpinGuige = fanghuYongpinGuige;
	}
	
}
