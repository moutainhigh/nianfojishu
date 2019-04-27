package com.task.entity.sop;

/**
 * 同一工序个人的综合系数表（ta_sop_monthOneBonus）
 * 
 * @author 贾辉辉
 * 
 */
public class MonthOneBonus implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;// 主键
	private String code;// 员工号
	private String username;// 员工姓名
	private String bonusMonth;// 奖金月份
	private String bonusArea;// 提奖区间
	private String tjtime;// 提取奖金时间
	private String TJusername;// 提取操作人
	private Float money;// 奖金额
	private String more;// 备注

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBonusMonth() {
		return bonusMonth;
	}

	public void setBonusMonth(String bonusMonth) {
		this.bonusMonth = bonusMonth;
	}

	public String getBonusArea() {
		return bonusArea;
	}

	public void setBonusArea(String bonusArea) {
		this.bonusArea = bonusArea;
	}

	public String getTjtime() {
		return tjtime;
	}

	public void setTjtime(String tjtime) {
		this.tjtime = tjtime;
	}

	public String getTJusername() {
		return TJusername;
	}

	public void setTJusername(String tJusername) {
		TJusername = tJusername;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

}
