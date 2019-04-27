package com.task.entity.menjin;

import java.io.Serializable;

/*
 * 
 * @author fy 2017-6-19 09:51:11 表名 ta_mj_AccessSwitch
 */
public class AccessSwitch implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;//
	private String code;// 卡号
	private String name;// 姓名
	private String addTime;// 添加时间
	private String type;// （AF：开门/AE：关门）

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
