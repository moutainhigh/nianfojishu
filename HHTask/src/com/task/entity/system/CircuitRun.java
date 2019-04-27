package com.task.entity.system;

import java.io.Serializable;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/***
 * 流程执行(表名:ta_sys_CircuitRun)
 * 
 * @author 刘培
 * 
 */
public class CircuitRun implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private String name;// 流程名称
	private String more;// 备注(如XXX功能的审批流程)
	private String auditType;// 审批类型(oneBack(一次打回)/lastBack(最终审核)/oneAudit(任一审核)即一个打回就重新开始或者最终打回重新开始)
	private String isVerification;// 是否验证(是/否)
	private String isopinion;//是否必填审批意见;
	private String isspoption;//是否增加审批选项;
	private String danxuanorduoxuan;//单选或者多选
	private String entityName;// 对应实体类名称
	private String entityStatusName;// 对应实体类状态(字段名称)
	private String entityIdName;// 对应实体类状态(字段名称)
	private Integer entityId;// 对应实体类id
	private String detailUrl;// 明细地址
	private String message;// 消息内容
	private String allStatus;// 总状态(未审批、审批中、同意、打回)
	private String auditStatus;// 审核状态(当前审批动态)
	private Integer auditLevel;// 审核等级
	private String addDateTime;// 添加时间
	private String hdStatus;// 回调状态(全部审批通过(同意)以后的实体状态；比如:"打分"、"可提奖"、)
	private String zzopinion;//最终意见
	private String messageIds;// 消息提醒id(在审批同意后,更新消息状态为已读)
	private Integer addUserId;// 添加人id
	private String addUserName;// 添加人姓名
	private String addUserDept;// 添加人部门

	private Integer ccId;// 定制流程id(CircuitCustomizeId 用于更新流程)
	private Integer xgepId;//相关epId页面显示使用

	private Set<ExecutionNode> exNodeSet;// 审批节点
	private Set<OptionRun> setoptionrun;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getAllStatus() {
		return allStatus;
	}

	public void setAllStatus(String allStatus) {
		this.allStatus = allStatus;
	}

	public String getAddDateTime() {
		return addDateTime;
	}

	public void setAddDateTime(String addDateTime) {
		this.addDateTime = addDateTime;
	}

	@JSONField(serialize = false)
	public Set<ExecutionNode> getExNodeSet() {
		return exNodeSet;
	}

	public void setExNodeSet(Set<ExecutionNode> exNodeSet) {
		this.exNodeSet = exNodeSet;
	}

	public String getAuditType() {
		return auditType;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	public Integer getCcId() {
		return ccId;
	}

	public void setCcId(Integer ccId) {
		this.ccId = ccId;
	}

	public Integer getAuditLevel() {
		return auditLevel;
	}

	public void setAuditLevel(Integer auditLevel) {
		this.auditLevel = auditLevel;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getEntityStatusName() {
		return entityStatusName;
	}

	public void setEntityStatusName(String entityStatusName) {
		this.entityStatusName = entityStatusName;
	}

	public String getEntityIdName() {
		return entityIdName;
	}

	public void setEntityIdName(String entityIdName) {
		this.entityIdName = entityIdName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public String getHdStatus() {
		return hdStatus;
	}

	public void setHdStatus(String hdStatus) {
		this.hdStatus = hdStatus;
	}

	public String getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(String messageIds) {
		this.messageIds = messageIds;
	}

	public String getIsVerification() {
		return isVerification;
	}

	public void setIsVerification(String isVerification) {
		this.isVerification = isVerification;
	}

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

	public String getAddUserName() {
		return addUserName;
	}

	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}

	public String getAddUserDept() {
		return addUserDept;
	}

	public void setAddUserDept(String addUserDept) {
		this.addUserDept = addUserDept;
	}

	public Integer getXgepId() {
		return xgepId;
	}

	public void setXgepId(Integer xgepId) {
		this.xgepId = xgepId;
	}

	public String getIsopinion() {
		return isopinion;
	}

	public void setIsopinion(String isopinion) {
		this.isopinion = isopinion;
	}

	public String getIsspoption() {
		return isspoption;
	}

	public void setIsspoption(String isspoption) {
		this.isspoption = isspoption;
	}

	public String getDanxuanorduoxuan() {
		return danxuanorduoxuan;
	}

	public void setDanxuanorduoxuan(String danxuanorduoxuan) {
		this.danxuanorduoxuan = danxuanorduoxuan;
	}
	
	@JSONField(serialize = false)
	public Set<OptionRun> getSetoptionrun() {
		return setoptionrun;
	}

	public void setSetoptionrun(Set<OptionRun> setoptionrun) {
		this.setoptionrun = setoptionrun;
	}

	public String getZzopinion() {
		return zzopinion;
	}

	public void setZzopinion(String zzopinion) {
		this.zzopinion = zzopinion;
	}
 
}