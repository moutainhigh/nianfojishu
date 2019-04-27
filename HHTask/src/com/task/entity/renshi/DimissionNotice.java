package com.task.entity.renshi;

import java.io.Serializable;


/**
 * 离职通知单
 * 表名ta_hr_lz_DimissionNotice
 * 
 * @author Li_Cong 2015/9/2/11:52
 */
public class DimissionNotice implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String noticeNumber;//编号 
	private String chaosong;//抄送部门
	private String name;// 
	private String dept;// 
	private String code;
	private String lizhi;// 离职原因
	private String banli_time;// 办理离职日期
	private String zhixin_time;// 止薪日期
	private String shebao_time;// 社保截止日期
	private String gongzijiesuan;// 工资结算方式
	
	private String banliren;//经办人
	private String addTime;
	private String updateTime;

	private Integer dim_tongzhi_id;// 对应的离职申请单


	// get set 方法
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

	public String getLizhi() {
		return lizhi;
	}

	public void setLizhi(String lizhi) {
		this.lizhi = lizhi;
	}

	public String getBanli_time() {
		return banli_time;
	}

	public void setBanli_time(String banliTime) {
		banli_time = banliTime;
	}

	public String getZhixin_time() {
		return zhixin_time;
	}

	public void setZhixin_time(String zhixinTime) {
		zhixin_time = zhixinTime;
	}

	public String getShebao_time() {
		return shebao_time;
	}

	public void setShebao_time(String shebaoTime) {
		shebao_time = shebaoTime;
	}

	public String getGongzijiesuan() {
		return gongzijiesuan;
	}

	public void setGongzijiesuan(String gongzijiesuan) {
		this.gongzijiesuan = gongzijiesuan;
	}

	public String getBanliren() {
		return banliren;
	}

	public void setBanliren(String banliren) {
		this.banliren = banliren;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDim_tongzhi_id() {
		return dim_tongzhi_id;
	}

	public void setDim_tongzhi_id(Integer dimTongzhiId) {
		dim_tongzhi_id = dimTongzhiId;
	}

	public String getNoticeNumber() {
		return noticeNumber;
	}

	public void setNoticeNumber(String noticeNumber) {
		this.noticeNumber = noticeNumber;
	}

	public String getChaosong() {
		return chaosong;
	}

	public void setChaosong(String chaosong) {
		this.chaosong = chaosong;
	}


}