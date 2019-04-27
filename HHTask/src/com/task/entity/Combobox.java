package com.task.entity;

import java.io.Serializable;

public class Combobox implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String text;
	private String desc;
	private Boolean selected;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

}