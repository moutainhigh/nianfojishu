package com.task.entity;

import java.io.Serializable;


/**
 * @author 曾建森
 * @FileNam SupplyResult.java
 * @Date 2012-10-15
 */
public class SupplyResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	
	private String deptName;
	
	private String copies;
	
	private String supplyDate;
	
	public SupplyResult() {
		super();
	}

	public SupplyResult(String name, String deptName, String copies,
			String supplyDate) {
		super();
		this.name = name;
		this.deptName = deptName;
		this.copies = copies;
		this.supplyDate = supplyDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getCopies() {
		return copies;
	}

	public void setCopies(String copies) {
		this.copies = copies;
	}

	public String getSupplyDate() {
		return supplyDate;
	}

	public void setSupplyDate(String supplyDate) {
		this.supplyDate = supplyDate;
	}

}