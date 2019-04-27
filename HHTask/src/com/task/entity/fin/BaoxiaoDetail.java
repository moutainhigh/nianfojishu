package com.task.entity.fin;

import java.io.Serializable;

/**
 * 报销单对应的明细（ta_fin_baoXiaoDetail）
 * @author jhh
 *与报销单多对一
 */
public class BaoxiaoDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private BaoxiaoDan baoxiaoDan;//对应报销单信息
	private String baoxiaoStyle;//报销科目
	private String baoxiaoContent;//报销内容摘要
	private String markId;//所属总成件号
	private Float money;//金额
	private String currency;//币种
	private String saveTime;//保存时间
	private String more;//备注
	private String budgetDept;//申请部门(承担部门)
	private String status;//状态（confirm/print） 判断是否跨部门审核
	private Integer deptMonthBudgetID;//部门月度申报ID
	private String planMonth;//计划月份(与报销单信息里面的计划月份对应)
	
	private String goodsStoreMarkId;//件号
	private String goodsStoreLot;//批次
	private Float goodsStoreCount;//数量
	private String processesNo;//工序号
	private String processesName;//工序名
	private String invoiceNo;//发票号
	private String content;//内容
	private Integer goodsStoreId;//入库id标识
	
	private String  partName;//零件名称
	
	
	
	
	
	/*跨部门确认字段*/
	private String confirmCode;//确认人工号
	private String confirmTime;//确认时间
	
	
	
	
	public String getConfirmCode() {
		return confirmCode;
	}
	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
 
	public BaoxiaoDan getBaoxiaoDan() {
		return baoxiaoDan;
	}
	public void setBaoxiaoDan(BaoxiaoDan baoxiaoDan) {
		this.baoxiaoDan = baoxiaoDan;
	}
	public String getBaoxiaoStyle() {
		return baoxiaoStyle;
	}
	public void setBaoxiaoStyle(String baoxiaoStyle) {
		this.baoxiaoStyle = baoxiaoStyle;
	}
	public String getBaoxiaoContent() {
		return baoxiaoContent;
	}
	public void setBaoxiaoContent(String baoxiaoContent) {
		this.baoxiaoContent = baoxiaoContent;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public Float getMoney() {
		if(null!=money){
			return Float.parseFloat(String.format("%.2f", money));
		}
		return this.money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	public String getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(String saveTime) {
		this.saveTime = saveTime;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBudgetDept() {
		return budgetDept;
	}
	public void setBudgetDept(String budgetDept) {
		this.budgetDept = budgetDept;
	}
	public Integer getDeptMonthBudgetID() {
		return deptMonthBudgetID;
	}
	public void setDeptMonthBudgetID(Integer deptMonthBudgetID) {
		this.deptMonthBudgetID = deptMonthBudgetID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlanMonth() {
		return planMonth;
	}
	public void setPlanMonth(String planMonth) {
		this.planMonth = planMonth;
	}
	public String getGoodsStoreMarkId() {
		return goodsStoreMarkId;
	}
	public void setGoodsStoreMarkId(String goodsStoreMarkId) {
		this.goodsStoreMarkId = goodsStoreMarkId;
	}
	public String getGoodsStoreLot() {
		return goodsStoreLot;
	}
	public void setGoodsStoreLot(String goodsStoreLot) {
		this.goodsStoreLot = goodsStoreLot;
	}
	public Float getGoodsStoreCount() {
		return goodsStoreCount;
	}
	public void setGoodsStoreCount(Float goodsStoreCount) {
		this.goodsStoreCount = goodsStoreCount;
	}
	public Integer getGoodsStoreId() {
		return goodsStoreId;
	}
	public void setGoodsStoreId(Integer goodsStoreId) {
		this.goodsStoreId = goodsStoreId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProcessesNo() {
		return processesNo;
	}
	public void setProcessesNo(String processesNo) {
		this.processesNo = processesNo;
	}
	public String getProcessesName() {
		return processesName;
	}
	public void setProcessesName(String processesName) {
		this.processesName = processesName;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	
	
}
