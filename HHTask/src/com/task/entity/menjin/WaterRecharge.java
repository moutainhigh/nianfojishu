package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 自来水充值记录表
 * 
 * @author LiCong 表名 ta_WaterRecharge
 *
 */
public class WaterRecharge implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Float sheng;// 水数量(升)
	private Float shengDun;// 当期充值水数量(吨)
	private Float price;// 水费单价
	private Float allPrice;// 总水费
	private String userDept;// 部门
	private String userCode;// 工号
	private String userCardId;// 卡号
	private String userName;// 名称
	private Integer userId;// ID
	private String addTime;// 添加时间打开时间
	private String addUserName;// 添加人名称
	private String addUserId;// 添加人ID

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAddUserName() {
		return addUserName;
	}

	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}

	public String getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(String addUserId) {
		this.addUserId = addUserId;
	}

	public Float getShengDun() {
		return shengDun;
	}

	public void setShengDun(Float shengDun) {
		this.shengDun = shengDun;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(Float allPrice) {
		this.allPrice = allPrice;
	}

	public String getUserCardId() {
		return userCardId;
	}

	public void setUserCardId(String userCardId) {
		this.userCardId = userCardId;
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