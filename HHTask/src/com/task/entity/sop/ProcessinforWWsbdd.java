package com.task.entity.sop;
/**
 * 工序外委设变待定表
 * @author txb
 *
 */
public class ProcessinforWWsbdd  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String sbNumber;//设变编号
	private String sbId;//设变Id
	private String markId;//件号
	private String proName;//零件名称
	private String processNos;//工序号
	private String ProcessName;//工序名称
	private String source;//来源
	private Integer entityId;//对应外委节点id
	private String status;//状态
	private String addTime;//添加时间
	private Integer addUserId;//添加人
	private String addUserName;//添加人名称
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSbNumber() {
		return sbNumber;
	}
	public void setSbNumber(String sbNumber) {
		this.sbNumber = sbNumber;
	}
	public String getSbId() {
		return sbId;
	}
	public void setSbId(String sbId) {
		this.sbId = sbId;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getProcessNos() {
		return processNos;
	}
	public void setProcessNos(String processNos) {
		this.processNos = processNos;
	}
	public String getProcessName() {
		return ProcessName;
	}
	public void setProcessName(String processName) {
		ProcessName = processName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Integer getEntityId() {
		return entityId;
	}
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
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
	
	
	

}