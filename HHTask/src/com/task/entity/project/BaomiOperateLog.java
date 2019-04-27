package com.task.entity.project;
/**
 * 保密项目操作日志ta_pro_BaomiOperateLog
 * @author Administrator
 *
 */
public class BaomiOperateLog implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String operateType;//操作类型
	private String operateObject;//操作对象
	private String operateRemark;//
	private String operateTime;//
	private Integer operateUserId;
	private String operateUsername;//
	private String operateCode;//
	private String operateDept;//
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public String getOperateObject() {
		return operateObject;
	}
	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}
	public String getOperateRemark() {
		return operateRemark;
	}
	public void setOperateRemark(String operateRemark) {
		this.operateRemark = operateRemark;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	
	public String getOperateUsername() {
		return operateUsername;
	}
	public void setOperateUsername(String operateUsername) {
		this.operateUsername = operateUsername;
	}
	public String getOperateCode() {
		return operateCode;
	}
	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}
	public String getOperateDept() {
		return operateDept;
	}
	public void setOperateDept(String operateDept) {
		this.operateDept = operateDept;
	}
	public Integer getOperateUserId() {
		return operateUserId;
	}
	public void setOperateUserId(Integer operateUserId) {
		this.operateUserId = operateUserId;
	}
	
	
}
