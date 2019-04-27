package com.task.entity;

import java.io.Serializable;

public class ProjectProposalFlow  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;//显示名称
	private int level;//等级
	private String about;//说明
	private boolean agree;//是否同意
	private boolean checks;//是否审核过
	private String username;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public boolean isAgree() {
		return agree;
	}
	public void setAgree(boolean agree) {
		this.agree = agree;
	}
	public boolean isChecks() {
		return checks;
	}
	public void setChecks(boolean checks) {
		this.checks = checks;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}