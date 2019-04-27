package com.task.entity;

/*
 * 收藏夹 
 * ta_sys_favorite
 */
public class Favorite implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String name;
	private String path;
	private String bgcolor;
	private String mfid;// 模块ID
	private Integer usersid;
	private String updateTime;


	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getUsersid() {
		return usersid;
	}

	public void setUsersid(Integer usersid) {
		this.usersid = usersid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMfid() {
		return mfid;
	}

	public void setMfid(String mfid) {
		this.mfid = mfid;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

}
