package com.task.entity.system;

import java.io.Serializable;

/**
 * 系统需求明细→
 * @author 王传运
 *
 */
public class SystemDemandDetail implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String toUserId;
	private String toUserName;
	private String replyTime;//回复时间
	private String question;//问题
	private SystemDemand systemDemand;//问题表
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public SystemDemand getSystemDemand() {
		return systemDemand;
	}
	public void setSystemDemand(SystemDemand systemDemand) {
		this.systemDemand = systemDemand;
	}
	public String getReplyTime() {
		return replyTime;
	}
	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}
	
	
}
