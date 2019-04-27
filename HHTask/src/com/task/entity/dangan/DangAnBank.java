package com.task.entity.dangan;

import java.io.Serializable;


/**
 * 还档案表
 * @author Li_Cong 2017-05-16
 * 表名 ta_da_DangAnBank
 * 关柜子后生成再次开门凭据
 */
public class DangAnBank implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String cardId;//卡号
	private String daNum;//档案柜开柜指令
	private String daIp;//档案柜IP
	private String useStatus;// 验证码使用状态(未使用/已失效)
	private String addTime;//添加时间
	
//	get set 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getDaNum() {
		return daNum;
	}
	public void setDaNum(String daNum) {
		this.daNum = daNum;
	}
	public String getDaIp() {
		return daIp;
	}
	public void setDaIp(String daIp) {
		this.daIp = daIp;
	}
	public String getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
}