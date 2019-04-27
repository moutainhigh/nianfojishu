package com.task.entity.sop;
/**
 * 工序物料确认记录
 * @author 王晓飞 ta_ProcessWlqr
 *
 */
public class ProcessWlqr implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	
	private Integer id;
	private Integer processId;//ProcessInfor Id
	private float count;//每次确认的数量
	private String addUsers;//确认人
	private String addUsersCode;//确认人工号
	private String addTime;//确认时间
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProcessId() {
		return processId;
	}
	public void setProcessId(Integer processId) {
		this.processId = processId;
	}
	public float getCount() {
		return count;
	}
	public void setCount(float count) {
		this.count = count;
	}
	public String getAddUsers() {
		return addUsers;
	}
	public void setAddUsers(String addUsers) {
		this.addUsers = addUsers;
	}
	public String getAddUsersCode() {
		return addUsersCode;
	}
	public void setAddUsersCode(String addUsersCode) {
		this.addUsersCode = addUsersCode;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
}
