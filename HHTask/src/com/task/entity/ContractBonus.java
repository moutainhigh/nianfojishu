package com.task.entity;

import java.io.Serializable;

import com.task.util.FieldMeta;

/***
 * 承包奖金总额(表名:ta_hr_cb_contractBonus)
 * 
 * @author 刘培
 * 
 */
public class ContractBonus implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer userId;// (FK)人员id
	@FieldMeta(name="工号")
	private String code;// 工号
	@FieldMeta(name="卡号")
	private String cardId;// 卡号
	@FieldMeta(name="用户名称")
	private String userName;// 用户名称
	@FieldMeta(name="部门名称")
	private String deptName;// 部门名称
	@FieldMeta(name=" 奖金月份")
	private String bonusMouth;// 奖金月份
	@FieldMeta(name="总金额")
	private Float totalMoney;// 总金额
	@FieldMeta(name="添加时间")
	private String addDate;// 添加时间
	private String bonusStatus;// 状态(审核、同意)
	private String status;// 总额所属(chengbao/bumen)
	private String ifReceive;// 是否领取(yes/no)
	private Integer epId;// 审批流程Id
	

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getBonusMouth() {
		return bonusMouth;
	}

	public void setBonusMouth(String bonusMouth) {
		this.bonusMouth = bonusMouth;
	}

	public Float getTotalMoney() {
		if (totalMoney != null) {
			// 保留两位小数
			return Float.parseFloat(String.format("%.2f", totalMoney));
		}
		return totalMoney;
	}

	public void setTotalMoney(Float totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getBonusStatus() {
		return bonusStatus;
	}

	public void setBonusStatus(String bonusStatus) {
		this.bonusStatus = bonusStatus;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getIfReceive() {
		return ifReceive;
	}

	public void setIfReceive(String ifReceive) {
		this.ifReceive = ifReceive;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}