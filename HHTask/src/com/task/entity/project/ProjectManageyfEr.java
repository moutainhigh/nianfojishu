package com.task.entity.project;
/**
 * 报选子研发项目审批记录 ta_pro_ProjectManageyfEr
 * @author wcy
 *
 */
public class ProjectManageyfEr implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id; 
	private Integer projectId; //项目id
	private String addDate; //添加时间
	private Integer addUserId; //添加人
	private String addUserName;//添加人
	private String status; //审批状态

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	
	
}
