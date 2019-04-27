package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 门禁使用记录表 2016-01-19
 * 
 * @author Li_Cong 表名 ta_mj_DoorUseRecording 门禁使用记录
 */
public class DoorUseRecording implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer accessId;// 门禁设备id
	private String cardNum;// 卡号
	private String name;// 姓名
	private String security_num;// 验证码
	private String useStatus;// 使用状态(使用中/空闲)
	private String startTime;// 开始时间
	private String endTime;// 结束时间
	private String useDateNum;// 使用时长（毫秒）
	private String useDate;// 使用时长

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
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

	public String getUseDate() {
		return useDate;
	}

	public void setUseDate(String useDate) {
		this.useDate = useDate;
	}

	public String getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSecurity_num(String securityNum) {
		security_num = securityNum;
	}

	public String getName() {
		return name;
	}

	public String getSecurity_num() {
		return security_num;
	}

	public String getUseDateNum() {
		return useDateNum;
	}

	public void setUseDateNum(String useDateNum) {
		this.useDateNum = useDateNum;
	}

	public Integer getAccessId() {
		return accessId;
	}

	public void setAccessId(Integer accessId) {
		this.accessId = accessId;
	}
}