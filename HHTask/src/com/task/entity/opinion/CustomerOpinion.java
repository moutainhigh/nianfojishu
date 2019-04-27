package com.task.entity.opinion;

import java.io.Serializable;
import java.util.List;

import com.task.entity.Users;

/**
 * 客户投诉 表名(ta_CustomerOpinion)
 * 
 * @author txb
 * 
 */

public class CustomerOpinion implements Serializable {
	private static final long serialVersionUID =1L;
	private Integer id;
	private String otherPerson;// 对方负责人
	private String ourPerson;// 已方负责人
	private String otherPhone;// 对方手机号
	private String otherCompany;// 对方公司名
	private String addTime;// 登记时间
	private String title;// 投诉标题
	private String context;// 投诉原因
	private Integer orderId;
	private Integer procardId;
	private Integer processInforId;
	private String status;// 状态
	private String opinionNumber;// 投诉单号
	private String username;// 谁添加的这个索赔单
	private String fileName;// 附件名称
	private String analystDept;// 分析人员部门
	private String analystName;// 分析人员名称
	private String analystCode;// 分析人员工号
	private String analysisText;// 分析内容
	private String analysisFile;// 分析附件
	private String analysisTime;// 指派分析人员时间
	private String executiveCodes;// 执行人员工号
	private String executiveTime;// 指派执行人员时间
	private String executiveText;// 执行内容
	private String executiveFile;// 执行附件
	private String approvalperson1;// 分析审批人员
	private String approvalperson2;// 改进审批人员
	private String approvaltime1;// 分析审批时间
	private String approvaltime2;// 改进审批时间
	private Double applyMoney;//
	private List<Users> executives;// 执行人员（不入数据库，页面显示是使用）

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOurPerson() {
		return ourPerson;
	}

	public void setOurPerson(String ourPerson) {
		this.ourPerson = ourPerson;
	}

	public String getOtherPerson() {
		return otherPerson;
	}

	public void setOtherPerson(String otherPerson) {
		this.otherPerson = otherPerson;
	}

	public String getOtherPhone() {
		return otherPhone;
	}

	public void setOtherPhone(String otherPhone) {
		this.otherPhone = otherPhone;
	}

	public String getOtherCompany() {
		return otherCompany;
	}

	public void setOtherCompany(String otherCompany) {
		this.otherCompany = otherCompany;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOpinionNumber() {
		return opinionNumber;
	}

	public void setOpinionNumber(String opinionNumber) {
		this.opinionNumber = opinionNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAnalystDept() {
		return analystDept;
	}

	public void setAnalystDept(String analystDept) {
		this.analystDept = analystDept;
	}

	public String getAnalystName() {
		return analystName;
	}

	public void setAnalystName(String analystName) {
		this.analystName = analystName;
	}

	public String getAnalystCode() {
		return analystCode;
	}

	public void setAnalystCode(String analystCode) {
		this.analystCode = analystCode;
	}

	public String getExecutiveCodes() {
		return executiveCodes;
	}

	public void setExecutiveCodes(String executiveCodes) {
		this.executiveCodes = executiveCodes;
	}

	public List<Users> getExecutives() {
		return executives;
	}

	public void setExecutives(List<Users> executives) {
		this.executives = executives;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getAnalysisText() {
		return analysisText;
	}

	public void setAnalysisText(String analysisText) {
		this.analysisText = analysisText;
	}

	public String getAnalysisFile() {
		return analysisFile;
	}

	public void setAnalysisFile(String analysisFile) {
		this.analysisFile = analysisFile;
	}

	public String getAnalysisTime() {
		return analysisTime;
	}

	public void setAnalysisTime(String analysisTime) {
		this.analysisTime = analysisTime;
	}

	public String getExecutiveTime() {
		return executiveTime;
	}

	public void setExecutiveTime(String executiveTime) {
		this.executiveTime = executiveTime;
	}

	public String getExecutiveText() {
		return executiveText;
	}

	public void setExecutiveText(String executiveText) {
		this.executiveText = executiveText;
	}

	public String getExecutiveFile() {
		return executiveFile;
	}

	public void setExecutiveFile(String executiveFile) {
		this.executiveFile = executiveFile;
	}

	public String getApprovalperson1() {
		return approvalperson1;
	}

	public void setApprovalperson1(String approvalperson1) {
		this.approvalperson1 = approvalperson1;
	}

	public String getApprovalperson2() {
		return approvalperson2;
	}

	public void setApprovalperson2(String approvalperson2) {
		this.approvalperson2 = approvalperson2;
	}

	public String getApprovaltime1() {
		return approvaltime1;
	}

	public void setApprovaltime1(String approvaltime1) {
		this.approvaltime1 = approvaltime1;
	}

	public String getApprovaltime2() {
		return approvaltime2;
	}

	public void setApprovaltime2(String approvaltime2) {
		this.approvaltime2 = approvaltime2;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getProcardId() {
		return procardId;
	}

	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
	}

	public Integer getProcessInforId() {
		return processInforId;
	}

	public void setProcessInforId(Integer processInforId) {
		this.processInforId = processInforId;
	}

	public Double getApplyMoney() {
		return applyMoney;
	}

	public void setApplyMoney(Double applyMoney) {
		this.applyMoney = applyMoney;
	}


	
	
	

}
