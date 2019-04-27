package com.task.entity;

import java.io.Serializable;

public class Requisition  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer userid;// 用户Id
	private String singleumber;// 编号
	private String name;// 申请人
	private String startdate;// 查询内容开始时间
	private String enddate;// 查询内容结束时间
	private String content;// 查询内容
	private String subjectmatter;// 查询事由
	private String department;// 部门
	private String manager;// 审批状态
	private String managerdate;// 部门经理审批时间
	private String dgmanagerdate;// 副总审批时间
	private String gmanagerdate;// 总经理审批时间
	private String itdate;// 申请时间
	private String replyremarks;// 回复执行备注
	private String executiondate;// 执行申请时间
	private String completedate;// 完成时间

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getExecutiondate() {
		return executiondate;
	}

	public void setExecutiondate(String executiondate) {
		this.executiondate = executiondate;
	}

	public String getCompletedate() {
		return completedate;
	}

	public void setCompletedate(String completedate) {
		this.completedate = completedate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSingleumber() {
		return singleumber;
	}

	public void setSingleumber(String singleumber) {
		this.singleumber = singleumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubjectmatter() {
		return subjectmatter;
	}

	public void setSubjectmatter(String subjectmatter) {
		this.subjectmatter = subjectmatter;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManagerdate() {
		return managerdate;
	}

	public void setManagerdate(String managerdate) {
		this.managerdate = managerdate;
	}

	public String getDgmanagerdate() {
		return dgmanagerdate;
	}

	public void setDgmanagerdate(String dgmanagerdate) {
		this.dgmanagerdate = dgmanagerdate;
	}

	public String getGmanagerdate() {
		return gmanagerdate;
	}

	public void setGmanagerdate(String gmanagerdate) {
		this.gmanagerdate = gmanagerdate;
	}

	public String getItdate() {
		return itdate;
	}

	public void setItdate(String itdate) {
		this.itdate = itdate;
	}

	public String getReplyremarks() {
		return replyremarks;
	}

	public void setReplyremarks(String replyremarks) {
		this.replyremarks = replyremarks;
	}
}
