package com.task.entity.sop;
/**
 * ta_ProcardTemplateBanBenJudgesFile
 * @author txb
 *
 */
public class ProcardTemplateBanBenJudgesFile  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String addTime;//
	private Integer addUserId;
	private String addUserName;//
	private String addUserCode;//
	private Integer ptbbjId;//ProcardTemplateBanBenJudges
	private Integer ptbbapplyId;//ProcardTemplateBanBenApply
	private String filename;
	private String oldFileName;//uplode/flie/pcn
	private String dataStatus;//文件状态（使用,删除）
	
	private String spStatus;//审批状态
	private String spRemark;//评语
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getAddUserCode() {
		return addUserCode;
	}
	public void setAddUserCode(String addUserCode) {
		this.addUserCode = addUserCode;
	}
	public Integer getPtbbjId() {
		return ptbbjId;
	}
	public void setPtbbjId(Integer ptbbjId) {
		this.ptbbjId = ptbbjId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getOldFileName() {
		return oldFileName;
	}
	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}
	public Integer getPtbbapplyId() {
		return ptbbapplyId;
	}
	public void setPtbbapplyId(Integer ptbbapplyId) {
		this.ptbbapplyId = ptbbapplyId;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getSpStatus() {
		return spStatus;
	}
	public void setSpStatus(String spStatus) {
		this.spStatus = spStatus;
	}
	public String getSpRemark() {
		return spRemark;
	}
	public void setSpRemark(String spRemark) {
		this.spRemark = spRemark;
	}
	
	
}
