package com.task.entity;

import java.io.Serializable;

/**
 * @author Li_Cong
 * @FileNam Download.java
 * @Date 2015-11-23
 * 添加打卡信息表
 * STCard_ENP.dbo.KQ_Download
 */
public class Download  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer person_ID; //卡片信息ID
	private String card_No; //卡号
	private String brush_Date;//打卡日期
	private String brush_Time;//打卡时间
	private String moc_No;
	private String data_Flag;
	private String brush_DateTime;//打卡日期时间
	private Integer is_Falsity;
	private String fileName;
	private String downType;//打卡类型(正常/出差)
	private String downAddress;//打卡地址(手机发送地址/门禁设备)
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPerson_ID() {
		return person_ID;
	}
	public void setPerson_ID(Integer personID) {
		person_ID = personID;
	}
	public String getCard_No() {
		return card_No;
	}
	public void setCard_No(String cardNo) {
		card_No = cardNo;
	}
	public String getBrush_Date() {
		return brush_Date;
	}
	public void setBrush_Date(String brushDate) {
		brush_Date = brushDate;
	}
	public String getBrush_Time() {
		return brush_Time;
	}
	public void setBrush_Time(String brushTime) {
		brush_Time = brushTime;
	}
	public String getData_Flag() {
		return data_Flag;
	}
	public void setData_Flag(String dataFlag) {
		data_Flag = dataFlag;
	}
	public Integer getIs_Falsity() {
		return is_Falsity;
	}
	public void setIs_Falsity(Integer isFalsity) {
		is_Falsity = isFalsity;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMoc_No() {
		return moc_No;
	}
	public void setMoc_No(String mocNo) {
		moc_No = mocNo;
	}
	public String getBrush_DateTime() {
		return brush_DateTime;
	}
	public void setBrush_DateTime(String brushDateTime) {
		brush_DateTime = brushDateTime;
	}
	public String getDownType() {
		return downType;
	}
	public void setDownType(String downType) {
		this.downType = downType;
	}
	public String getDownAddress() {
		return downAddress;
	}
	public void setDownAddress(String downAddress) {
		this.downAddress = downAddress;
	}
}
