package com.task.entity.face;

import java.io.Serializable;

/**
 * 人脸识别摄像头报警人员
 * @author wcy
 *ta_hr_faceAlarm
 */
public class FaceAlarm implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String userName;
	private String dateTime;
	private String userCode;
	private String cameraPosition;
	private String picturePath;//
	
	@Override
	public String toString() {
		return "FaceAlarm [id=" + id + ", picturePath=" + picturePath + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getCameraPosition() {
		return cameraPosition;
	}
	public void setCameraPosition(String cameraPosition) {
		this.cameraPosition = cameraPosition;
	}
	
	
}