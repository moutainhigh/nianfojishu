package com.task.entity;

import java.io.Serializable;
import java.util.Set;

/***
 * 职位胜任要求(表名:ta_hr_dutyClaim)
 * 
 * @author 刘培
 * 
 */
public class DutyClaim  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String duty;// 职位
	private String eduClaim;// 学历要求
	private String speClaim;// 专业要求
	private String expClaim;// 经验要求
	private String skillClaim;// 技能要求(1、2、3、4、)
	private String quaClaim;// 素质要求 (1、2、3、4、)
	private String posClaim;// 上岗要求
	private String deptClaim;// 要求部门

	private String claimStatus;// 状态(标准、现有人员、备选人员)

	private Integer userId;// 用户id
	private String code;// 工号
	private String userName;// 名称
	private String dept;// 部门

	private Integer floor;// 层数
	private Set<DutyClaim> dutyClaimSet;// (一对多)
	private DutyClaim dutyClaim;// (多对一)

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getEduClaim() {
		return eduClaim;
	}

	public void setEduClaim(String eduClaim) {
		this.eduClaim = eduClaim;
	}

	public String getSpeClaim() {
		return speClaim;
	}

	public void setSpeClaim(String speClaim) {
		this.speClaim = speClaim;
	}

	public String getExpClaim() {
		return expClaim;
	}

	public void setExpClaim(String expClaim) {
		this.expClaim = expClaim;
	}

	public String getSkillClaim() {
		return skillClaim;
	}

	public void setSkillClaim(String skillClaim) {
		this.skillClaim = skillClaim;
	}

	public String getQuaClaim() {
		return quaClaim;
	}

	public void setQuaClaim(String quaClaim) {
		this.quaClaim = quaClaim;
	}

	public String getPosClaim() {
		return posClaim;
	}

	public void setPosClaim(String posClaim) {
		this.posClaim = posClaim;
	}

	public String getDeptClaim() {
		return deptClaim;
	}

	public void setDeptClaim(String deptClaim) {
		this.deptClaim = deptClaim;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Set<DutyClaim> getDutyClaimSet() {
		return dutyClaimSet;
	}

	public void setDutyClaimSet(Set<DutyClaim> dutyClaimSet) {
		this.dutyClaimSet = dutyClaimSet;
	}

	public DutyClaim getDutyClaim() {
		return dutyClaim;
	}

	public void setDutyClaim(DutyClaim dutyClaim) {
		this.dutyClaim = dutyClaim;
	}
}