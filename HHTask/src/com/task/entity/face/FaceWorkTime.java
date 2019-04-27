package com.task.entity.face;

import java.io.Serializable;

/**
 * 有效工作
 * @author wcy
 *	ta_hr_FaceWorkTime
 */
public class FaceWorkTime implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String userName;
	private String userCode;
	private Integer userId;
	private String cardId;
	private Integer workTime;//工作时长//分钟
	private String addTime;// 添加时间
	private String dateTime; 
	private String startTime;
	private String endTime;
	
	private String ids;//页面中使用
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public Integer getWorkTime() {
		return workTime;
	}
	public void setWorkTime(Integer workTime) {
		this.workTime = workTime;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
}