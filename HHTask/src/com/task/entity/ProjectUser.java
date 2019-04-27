package com.task.entity;

import java.io.Serializable;

/**
 * 项目管理_成员 表名:ta_Project_User
 * 
 * @author 马凯
 */
public class ProjectUser  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String telphone;// 手机
	private String email;// 邮箱
	private String responsibilities;// 职责
	private String pGroup;// 成员类型
	private Project root;
	private Users user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getResponsibilities() {
		return responsibilities;
	}

	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
	}

	public String getpGroup() {
		return pGroup;
	}

	public void setpGroup(String pGroup) {
		this.pGroup = pGroup;
	}

	public Project getRoot() {
		return root;
	}

	public void setRoot(Project root) {
		this.root = root;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}