package com.task.entity.renshi;

import java.io.Serializable;

/**
 * 面试人家庭信息表
 * ta_hr_rz_inter_family
 * @author Li_Cong
 *
 */
public class Inter_Family implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String familyName;//家属姓名
    private String familyRelation;//与家属关系
    private String familySex;//家属性别
    private String familyAge;//家属年龄
    private String familyAddress;//家属住址或工作单位
    private String familyTel;//家属联系方式
    private String addTime;
    private InterviewLog interviewLog;//关联面试单主表
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public InterviewLog getInterviewLog() {
		return interviewLog;
	}
	public void setInterviewLog(InterviewLog interviewLog) {
		this.interviewLog = interviewLog;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getFamilyRelation() {
		return familyRelation;
	}
	public void setFamilyRelation(String familyRelation) {
		this.familyRelation = familyRelation;
	}
	public String getFamilySex() {
		return familySex;
	}
	public void setFamilySex(String familySex) {
		this.familySex = familySex;
	}
	public String getFamilyAge() {
		return familyAge;
	}
	public void setFamilyAge(String familyAge) {
		this.familyAge = familyAge;
	}
	public String getFamilyAddress() {
		return familyAddress;
	}
	public void setFamilyAddress(String familyAddress) {
		this.familyAddress = familyAddress;
	}
	public String getFamilyTel() {
		return familyTel;
	}
	public void setFamilyTel(String familyTel) {
		this.familyTel = familyTel;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
}
