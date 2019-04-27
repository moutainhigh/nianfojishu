package com.task.entity;

/***
 * ta_Taskmanager
 * @author 刘培
 *
 */
public class Taskmanager implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;// 描述
	private String taskType;//问题类型(问题点/新需求)
	private String taskCategory;//问题类别
	private String description;// 描述
	private String demand;// 需求
	private String note;// 订单号备注
	private String startTime;// 开始时间
	private String finishTime;// 完成时间
	private String suggestion;// 处理意见
	private Integer applyUsersId;// 申请人id
	private String applyUsersName;// 申请人名称
	private String applyUsersDept;// 申请人部门
	private String applyUsersTel;// 申请人电话
	private String receiver;// 领取人
	private String verifier;// 确认人
	private String taskState;// 任务状态  (待处理/处理中/待确认/反馈/完成)
	private String attachmentPath;// 附件路径
	private String feedback;// 二次反馈意见
	private Integer urgency;// 紧急程度
	private String process;//所属流程（订单/技术/计划/采购/物流/生产/交付/其他 ）
	private String exceptionType;//异常类别（异常/错误/系统延迟/其他 ）
	private String functionType;//功能所属类（查询/录入/计算/其他 ）
	private Integer repeatTime;//重复次数
	private String difficulty;//困难度
	private Integer epId;//
	private String ep_status;//审批状态 （未审批,审批中,同意,打回）

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    public String getEp_status() {
        return ep_status;
    }

    public void setEp_status(String ep_status) {
        this.ep_status = ep_status;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getDemand() {
		return demand;
	}

	public void setDemand(String demand) {
		this.demand = demand;
	}

	public Integer getApplyUsersId() {
		return applyUsersId;
	}

	public void setApplyUsersId(Integer applyUsersId) {
		this.applyUsersId = applyUsersId;
	}

	public String getApplyUsersName() {
		return applyUsersName;
	}

	public void setApplyUsersName(String applyUsersName) {
		this.applyUsersName = applyUsersName;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskCategory() {
		return taskCategory;
	}

	public void setTaskCategory(String taskCategory) {
		this.taskCategory = taskCategory;
	}

	public String getApplyUsersDept() {
		return applyUsersDept;
	}

	public void setApplyUsersDept(String applyUsersDept) {
		this.applyUsersDept = applyUsersDept;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}


	public void setUrgency(Integer urgency) {
		this.urgency = urgency;
	}

	public Integer getUrgency() {
		return urgency;
	}

	public String getApplyUsersTel() {
		return applyUsersTel;
	}

	public void setApplyUsersTel(String applyUsersTel) {
		this.applyUsersTel = applyUsersTel;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public Integer getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(Integer repeatTime) {
		this.repeatTime = repeatTime;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}



}
