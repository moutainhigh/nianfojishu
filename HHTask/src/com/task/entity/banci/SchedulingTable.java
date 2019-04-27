package com.task.entity.banci;

import java.io.Serializable;

import com.task.entity.Users;

/**
 * 排班表 
 * 表名： ta_SchedulingTable
 * @author Li_Cong
 * 2016-11-04
 *
 */
public class SchedulingTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Users users;//用户信息
	private String name;//员工姓名
	private String dept;//部门
	private String code;//工号
	private String cardId;//卡号
	private String dateTime;//日期
	private BanCi banCi;//班次
	private String banci_name;//班次名称
	private String addtime;//添加时间
	private String updatetime;//修改时间
	private Integer dataType;//节日类型 (0:工作日，1.公休日,2.节假日)
	private String remarks;//备注
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public BanCi getBanCi() {
		return banCi;
	}
	public void setBanCi(BanCi banCi) {
		this.banCi = banCi;
	}
	public String getBanci_name() {
		return banci_name;
	}
	public void setBanci_name(String banciName) {
		banci_name = banciName;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	
	
}
