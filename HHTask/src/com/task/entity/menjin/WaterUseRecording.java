package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 自来水使用记录表
 * 
 * @author LiCong 表名 ta_WaterUseRecording
 *
 */
public class WaterUseRecording implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Float sendSheng;// 发送可用水数量(升)
	private Float sheng;// 水数量(升)
	private String userDept;// 部门
	private String userCode;// 工号
	private String userCardId;// 卡号
	private String userName;// 名称
	private Integer userId;// ID
	private String addTime;// 添加时间打开
	private String endTime;// 使用结束时间
	private String status;// 状态(使用中/结束)
	private Integer accessEid;// 设备Id

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccessEid() {
		return accessEid;
	}

	public void setAccessEid(Integer accessEid) {
		this.accessEid = accessEid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUserCardId() {
		return userCardId;
	}

	public void setUserCardId(String userCardId) {
		this.userCardId = userCardId;
	}

	public Float getSendSheng() {
		return sendSheng;
	}

	public void setSendSheng(Float sendSheng) {
		this.sendSheng = sendSheng;
	}

	public Float getSheng() {
		return sheng;
	}

	public void setSheng(Float sheng) {
		this.sheng = sheng;
	}

	public String getUserDept() {
		return userDept;
	}

	public void setUserDept(String userDept) {
		this.userDept = userDept;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

}
