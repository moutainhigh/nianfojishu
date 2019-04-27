package com.task.entity.caiwu.pz;

import java.io.Serializable;


/**
 * 财务档案表(凭证)(ta_fin_caiwu_CwCertificate)
 * 2017-08-21
 * @author 李聪
 * 
 */
public class CwCertificate implements Serializable{
	private Integer id;
//	@FieldMeta(name = "一级科目")
//	private String oneItem;// 一级科目
//	private String itemMore;// 科目明细
//	private String zhaiyao;// 摘要，应付款名称、(件号、件号+工序号)
//	private Float wbMoney;// 外币金额
//	private Float hrate;// 汇  率
//	private Float totalMoney;// 合计
	private String number;// 凭证编号 No
	private String introduction;// 凭证简介
	private String pzDate;// 凭证日期
	private String danganguiNum;// 档案柜编号
	private Integer danganguiId;// 档案柜id
	private Integer fujianNum;// 附件张数(发票份数)
	
	private String pzType;// 发票类型(凭证/null  发票/fp)
	private String addTime;// 添加时间
	private String addUser;// 添加人(制单人)
	private Integer addUserId;//  添加人Id
	private String status;// 状态(待存入，已存入，已取出)


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getPzType() {
		return pzType;
	}

	public void setPzType(String pzType) {
		this.pzType = pzType;
	}

	public String getDanganguiNum() {
		return danganguiNum;
	}

	public void setDanganguiNum(String danganguiNum) {
		this.danganguiNum = danganguiNum;
	}

	public Integer getDanganguiId() {
		return danganguiId;
	}

	public void setDanganguiId(Integer danganguiId) {
		this.danganguiId = danganguiId;
	}

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPzDate() {
		return pzDate;
	}

	public void setPzDate(String pzDate) {
		this.pzDate = pzDate;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getAddUser() {
		return addUser;
	}

	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}

	public Integer getFujianNum() {
		return fujianNum;
	}

	public void setFujianNum(Integer fujianNum) {
		this.fujianNum = fujianNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}