package com.task.entity.fin.budget;

import java.io.Serializable;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 月度科目预算总金额表（ta_fin_subMonthMoney）
 * 
 * @author jhh
 * 
 */
public class SubMonthMoney implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private String name;// 名称
	private Float subjectRate;// 科目所占比例 保存数据计算时除以100，保留四位小数
	private String more;

	private String budgetMonth;// 预算月份
	private Float monthBudgetMoney;// 月度预算金额

	private Float monthRealMoney;// 计划内月度实际花费金额
	private Float waiMonthRealMoney;// 计划外月度实际花费金额
	private Float realSubjectRate;// 实际所占比例

	private Integer rootId;// 第一层父类Id
	private Integer fatherId;// 上层父类Id
	private String fatherName;// 上层名称(方便查询)
	private Integer belongLayer;// 当前层

	private Integer sbRateId;// 科目预算比例id(方便数据调用)

	private Set<DeptMonthBudget> dmBudgetSet;// 预算明细
	private Set<SubMonthMoney> smMoneySet;// 自身树形关系
	private SubMonthMoney subMonthMoney;// 自身树形关系

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

	public Float getSubjectRate() {
		return subjectRate;
	}

	public void setSubjectRate(Float subjectRate) {
		this.subjectRate = subjectRate;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getBudgetMonth() {
		return budgetMonth;
	}

	public void setBudgetMonth(String budgetMonth) {
		this.budgetMonth = budgetMonth;
	}

	public Float getMonthBudgetMoney() {
		return monthBudgetMoney;
	}

	public void setMonthBudgetMoney(Float monthBudgetMoney) {
		this.monthBudgetMoney = monthBudgetMoney;
	}

	public Float getMonthRealMoney() {
		return monthRealMoney;
	}

	public void setMonthRealMoney(Float monthRealMoney) {
		this.monthRealMoney = monthRealMoney;
	}

	public Float getRealSubjectRate() {
		return realSubjectRate;
	}

	public void setRealSubjectRate(Float realSubjectRate) {
		this.realSubjectRate = realSubjectRate;
	}

	public Integer getRootId() {
		return rootId;
	}

	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}

	public Integer getFatherId() {
		return fatherId;
	}

	public void setFatherId(Integer fatherId) {
		this.fatherId = fatherId;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public Integer getBelongLayer() {
		return belongLayer;
	}

	public void setBelongLayer(Integer belongLayer) {
		this.belongLayer = belongLayer;
	}
	@JSONField(serialize = false)
	public Set<DeptMonthBudget> getDmBudgetSet() {
		return dmBudgetSet;
	}

	public void setDmBudgetSet(Set<DeptMonthBudget> dmBudgetSet) {
		this.dmBudgetSet = dmBudgetSet;
	}

	public Set<SubMonthMoney> getSmMoneySet() {
		return smMoneySet;
	}

	public void setSmMoneySet(Set<SubMonthMoney> smMoneySet) {
		this.smMoneySet = smMoneySet;
	}

	public SubMonthMoney getSubMonthMoney() {
		return subMonthMoney;
	}

	public void setSubMonthMoney(SubMonthMoney subMonthMoney) {
		this.subMonthMoney = subMonthMoney;
	}

	public Integer getSbRateId() {
		return sbRateId;
	}

	public void setSbRateId(Integer sbRateId) {
		this.sbRateId = sbRateId;
	}

	public Float getWaiMonthRealMoney() {
		return waiMonthRealMoney;
	}

	public void setWaiMonthRealMoney(Float waiMonthRealMoney) {
		this.waiMonthRealMoney = waiMonthRealMoney;
	}

}
