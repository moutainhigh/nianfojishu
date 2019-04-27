package com.task.entity.fin.budget;

import java.io.Serializable;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.entity.DeptNumber;

/**
 * 科目明细历史记账表
 * ta_fin_SubBudgetRateMonth
 * @author 刘培
 * 
 */
public class SubBudgetRateMonth implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private Integer id;// 主键
	private String bookKDate;// 记账时间(yyyy-MM)
	private String name;// 名称
	/***
	 * 类别用于计算期末余额方向 **** 资产类期末余额=期初余额借方余额+本期借方发生额-本期贷方发生额=正数，余额在借方，负数则余额在贷方。
	 * 负债类期末余额=期初余额贷方余额+本期贷方发生额-本期借方发生额=正数，余额在贷方，负数则余额在借方
	 */
	private String type;// 科目类别(资产类、负债类、所有者权益类、损益类、成本类)

	private Double borrowYearBegingMoney;//年初借方金额
	private Double lendYearBegingMoney;//年初贷方金额
	private Double borrowYearSumMoney;//本年借方累计金额
	private Double lendYearSumMoney;//本年贷方累计金额
	private Double borrowQichuMoney;// 借方-期初金额
	private Double lendQichuMoney;// 贷方-期初金额
	private Double borrowMoney;// 借方-本期发生金额
	private Double lendMoney;// 贷方-本期发生金额
	private Double borrowJieyuMoney;// 借方-期末金额
	private Double lendJieyuMoney;// 贷方-期末金额
	private String addTimeDate;//添加时间
	private String fathrName;//父类名字
	
	private String subNumber;//科目编号
	private Integer fk_SubBudgetRateId;// 科目id
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBookKDate() {
		return bookKDate;
	}

	public void setBookKDate(String bookKDate) {
		this.bookKDate = bookKDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getBorrowQichuMoney() {
		return borrowQichuMoney;
	}

	public void setBorrowQichuMoney(Double borrowQichuMoney) {
		this.borrowQichuMoney = borrowQichuMoney;
	}

	public Double getLendQichuMoney() {
		return lendQichuMoney;
	}

	public void setLendQichuMoney(Double lendQichuMoney) {
		this.lendQichuMoney = lendQichuMoney;
	}

	public Double getBorrowMoney() {
		return borrowMoney;
	}

	public void setBorrowMoney(Double borrowMoney) {
		this.borrowMoney = borrowMoney;
	}

	public Double getLendMoney() {
		return lendMoney;
	}

	public void setLendMoney(Double lendMoney) {
		this.lendMoney = lendMoney;
	}

	public Double getBorrowJieyuMoney() {
		return borrowJieyuMoney;
	}

	public void setBorrowJieyuMoney(Double borrowJieyuMoney) {
		this.borrowJieyuMoney = borrowJieyuMoney;
	}

	public Double getLendJieyuMoney() {
		return lendJieyuMoney;
	}

	public void setLendJieyuMoney(Double lendJieyuMoney) {
		this.lendJieyuMoney = lendJieyuMoney;
	}

	public Integer getFk_SubBudgetRateId() {
		return fk_SubBudgetRateId;
	}

	public void setFk_SubBudgetRateId(Integer fkSubBudgetRateId) {
		fk_SubBudgetRateId = fkSubBudgetRateId;
	}

	public String getAddTimeDate() {
		return addTimeDate;
	}

	public void setAddTimeDate(String addTimeDate) {
		this.addTimeDate = addTimeDate;
	}

	public Double getBorrowYearBegingMoney() {
		return borrowYearBegingMoney;
	}

	public void setBorrowYearBegingMoney(Double borrowYearBegingMoney) {
		this.borrowYearBegingMoney = borrowYearBegingMoney;
	}

	public Double getLendYearBegingMoney() {
		return lendYearBegingMoney;
	}

	public void setLendYearBegingMoney(Double lendYearBegingMoney) {
		this.lendYearBegingMoney = lendYearBegingMoney;
	}

	public Double getBorrowYearSumMoney() {
		return borrowYearSumMoney;
	}

	public void setBorrowYearSumMoney(Double borrowYearSumMoney) {
		this.borrowYearSumMoney = borrowYearSumMoney;
	}

	public Double getLendYearSumMoney() {
		return lendYearSumMoney;
	}

	public void setLendYearSumMoney(Double lendYearSumMoney) {
		this.lendYearSumMoney = lendYearSumMoney;
	}

	public String getSubNumber() {
		return subNumber;
	}

	public void setSubNumber(String subNumber) {
		this.subNumber = subNumber;
	}

	public String getFathrName() {
		return fathrName;
	}

	public void setFathrName(String fathrName) {
		this.fathrName = fathrName;
	}

}