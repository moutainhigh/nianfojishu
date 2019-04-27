package com.task.entity.system;

import java.io.Serializable;
import java.util.Set;

/**
 * 系统需求表
 * @author 王传运
 *
 */
public class SystemDemand implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer userId;
	private String userName;
	private String userDept;
	private String sdNum;//需求编号
	private String functionName;//功能名称
	private String sdShortName;//需求简称
	private String sdType;//功能类型  系统新需求、系统问题
	private String sdLeave;//需求级别 优先级
	private String sdDesc;//问题描述
	private String status;//问题状态 审核中  、 解决中 、待确认、确认关闭
	private Integer epId;	//
	private String epStatus;//
	private String addTime;//添加时间
	private String demandFile;//需求文件 
	private String solveDate;//解决时间
	private String solvePerson;//解决人员
	private String affirmDate;//确认时间
	
	private Set<SystemDemandDetail> sDetails;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserDept() {
		return userDept;
	}
	public void setUserDept(String userDept) {
		this.userDept = userDept;
	}
	public String getSdNum() {
		return sdNum;
	}
	public void setSdNum(String sdNum) {
		this.sdNum = sdNum;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getSdShortName() {
		return sdShortName;
	}
	public void setSdShortName(String sdShortName) {
		this.sdShortName = sdShortName;
	}
	public String getSdType() {
		return sdType;
	}
	public void setSdType(String sdType) {
		this.sdType = sdType;
	}
	public String getSdDesc() {
		return sdDesc;
	}
	public void setSdDesc(String sdDesc) {
		this.sdDesc = sdDesc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getEpStatus() {
		return epStatus;
	}
	public void setEpStatus(String epStatus) {
		this.epStatus = epStatus;
	}
	public Set<SystemDemandDetail> getsDetails() {
		return sDetails;
	}
	public void setsDetails(Set<SystemDemandDetail> sDetails) {
		this.sDetails = sDetails;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getSdLeave() {
		return sdLeave;
	}
	public void setSdLeave(String sdLeave) {
		this.sdLeave = sdLeave;
	}
	public String getDemandFile() {
		return demandFile;
	}
	public void setDemandFile(String demandFile) {
		this.demandFile = demandFile;
	}
	public String getSolveDate() {
		return solveDate;
	}
	public void setSolveDate(String solveDate) {
		this.solveDate = solveDate;
	}
	public String getSolvePerson() {
		return solvePerson;
	}
	public void setSolvePerson(String solvePerson) {
		this.solvePerson = solvePerson;
	}
	public String getAffirmDate() {
		return affirmDate;
	}
	public void setAffirmDate(String affirmDate) {
		this.affirmDate = affirmDate;
	}
	
	
}
