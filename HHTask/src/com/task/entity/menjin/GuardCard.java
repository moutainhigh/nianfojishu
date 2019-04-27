package com.task.entity.menjin;

import java.io.Serializable;

/**
 * 门卫卡
 * 
 * @author Li_Cong 11-08 表名 ta_mj_GuardCard 门卫信息
 */
public class GuardCard implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String code;// 工号
	private String cardId;// 卡号
	private String name;// 人名
	private String sex;// 性别
	private String nation;// 民族
	private String birthplace;// 籍贯
	private String dept;// 部门
	private String uid;// 身份证
	private Integer u_Id;// 对应UsersId
	private String cardType;// 卡类型（触发/检急）
	private String addTime;// 添加时间
	private String updateTime;// 修改时间

	// get set
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getU_Id() {
		return u_Id;
	}

	public void setU_Id(Integer uId) {
		u_Id = uId;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

}