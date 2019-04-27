package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 档案柜类型表 2018-07-28
 * 
 * @author Li_Cong 表名 ta_mj_AccessWebcamType 档案类型。输入下拉添加
 */
public class AccessWebcamType implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String type;// 类型(dag/pz/fp/yz)
	private String name;// 名称()

	public AccessWebcamType() {
		super();
	}

	public AccessWebcamType(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	// get set
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
