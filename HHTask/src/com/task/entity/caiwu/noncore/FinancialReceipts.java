package com.task.entity.caiwu.noncore;

import java.io.Serializable;

/**
 * 财务收款明细
 * @author 李聪
 * 2017-09-05
 * 表名：ta_fin_caiwu_FinancialReceipts
 */

public class FinancialReceipts implements Serializable{
	private Integer id;
	private Integer coreReceivablesDetailId;
	private String kemu;//科目（水，电，设备，房租，管理）
	private String zhangqi;//收款月份
	private String jiluTime;//收款日期
	private String jiezTime;//费用截止日期(账期至)
	private String tiJiaoTime;//提交时间
	private String photoPath;//附件照片
	
	private Float querenMoney;//确认金额
	
	private String saveCode;//提交人工号
	private String saveUser;//提交人
	private Integer saveUserId;//提交人Id
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJiezTime() {
		return jiezTime;
	}
	public void setJiezTime(String jiezTime) {
		this.jiezTime = jiezTime;
	}
	public Integer getCoreReceivablesDetailId() {
		return coreReceivablesDetailId;
	}
	public void setCoreReceivablesDetailId(Integer coreReceivablesDetailId) {
		this.coreReceivablesDetailId = coreReceivablesDetailId;
	}
	public String getKemu() {
		return kemu;
	}
	public void setKemu(String kemu) {
		this.kemu = kemu;
	}
	public String getZhangqi() {
		return zhangqi;
	}
	public void setZhangqi(String zhangqi) {
		this.zhangqi = zhangqi;
	}
	public String getJiluTime() {
		return jiluTime;
	}
	public void setJiluTime(String jiluTime) {
		this.jiluTime = jiluTime;
	}
	public String getTiJiaoTime() {
		return tiJiaoTime;
	}
	public void setTiJiaoTime(String tiJiaoTime) {
		this.tiJiaoTime = tiJiaoTime;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public Float getQuerenMoney() {
		return querenMoney;
	}
	public void setQuerenMoney(Float querenMoney) {
		this.querenMoney = querenMoney;
	}
	public String getSaveCode() {
		return saveCode;
	}
	public void setSaveCode(String saveCode) {
		this.saveCode = saveCode;
	}
	public String getSaveUser() {
		return saveUser;
	}
	public void setSaveUser(String saveUser) {
		this.saveUser = saveUser;
	}
	public Integer getSaveUserId() {
		return saveUserId;
	}
	public void setSaveUserId(Integer saveUserId) {
		this.saveUserId = saveUserId;
	}
}

