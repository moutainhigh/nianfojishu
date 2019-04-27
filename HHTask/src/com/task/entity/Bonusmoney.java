package com.task.entity;

import java.io.Serializable;

import com.task.util.FieldMeta;

/***
 * 
 * 奖金总额表(表名:ta_hr_jjfp_bonusmoney)
 * 
 * @author 钟永林
 */
public class Bonusmoney implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; // 序号 int 4
	@FieldMeta(name = "月份")
	private String bonusmoneymonth; // 月份 Varchar 200
	@FieldMeta(name = "总金额")
	private Float bonusmoneytotalmoney; // 总金额 Float 8
	@FieldMeta(name = "班组")
	private String bonusmoneyteam; // 班组 Varchar 200
	@FieldMeta(name = "状态")
	private String bonusmoneystatus; // 状态 varchar 100 (审核中、总经理同意)
	@FieldMeta(name = "添加人姓名")
	private String bonusmoneyname;// 添加人姓名
	@FieldMeta(name = "时间")
	private String bonusmoneydatatime;// 时间 Varchar 200
	private Integer addUserId;// 添加人id
	private Integer epId;// 审批流程Id

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBonusmoneymonth() {
		return bonusmoneymonth;
	}

	public void setBonusmoneymonth(String bonusmoneymonth) {
		this.bonusmoneymonth = bonusmoneymonth;
	}

	public Float getBonusmoneytotalmoney() {
		return bonusmoneytotalmoney;
	}

	public void setBonusmoneytotalmoney(Float bonusmoneytotalmoney) {
		this.bonusmoneytotalmoney = bonusmoneytotalmoney;
	}

	public String getBonusmoneyteam() {
		return bonusmoneyteam;
	}

	public void setBonusmoneyteam(String bonusmoneyteam) {
		this.bonusmoneyteam = bonusmoneyteam;
	}

	public String getBonusmoneystatus() {
		return bonusmoneystatus;
	}

	public void setBonusmoneystatus(String bonusmoneystatus) {
		this.bonusmoneystatus = bonusmoneystatus;
	}

	public String getBonusmoneydatatime() {
		return bonusmoneydatatime;
	}

	public void setBonusmoneydatatime(String bonusmoneydatatime) {
		this.bonusmoneydatatime = bonusmoneydatatime;
	}

	public String getBonusmoneyname() {
		return bonusmoneyname;
	}

	public void setBonusmoneyname(String bonusmoneyname) {
		this.bonusmoneyname = bonusmoneyname;
	}

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

}
