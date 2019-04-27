package com.task.entity.payment;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.util.FieldMeta;

/**
 * 资金使用申请表:(ta_FundApply)
 * @author wxf
 *
 */
/**
 * @author txb
 *
 */
public class FundApply implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	@FieldMeta(name = "编号")
	private String number;// 编号
	private String unitname;// 资金使用单位名称
	private Integer relationclientId;// 收款单位Id
	@FieldMeta(name = "收款单位")
	private String relationclient;// 收款单位
	private String approvalApplier;// 创建人
	private Integer userId;//创建人人Id;
	private String approvaldept;// 资金使用人所在部门
	private String accreditationnum;// 评审编号
	private String voucherNature;// 支付性质
	@FieldMeta(name = "支付依据")
	private String voucherbasis;// 借款依据(合同、发票、协议、通知、其他依据)
	@FieldMeta(name = "支付方式")
	private String voucherway;// 支付方式
	private String vouchersituation;//支付情况
	@FieldMeta(name = "经办人")
	private String jingbanren;//经办人(是/否)
	@FieldMeta(name = "支付条件")
	private String vouchercondition;// 支付条件
	private String category;// 类别
	@FieldMeta(name = "支付类型")
	private String about;//申请相关（预算、项目、KVP、冲账、行政、合同、其他）
	@FieldMeta(name = "支付性质")
	private String zhifuoryufu;//支付或者预付;
	private String payStyle;//报销方式(现金、银行对公转账、归还借款、其他)
	private String planMonth;//预算月份;
	private String baoxiaoDate;//报销日期
	private Float attachmentsCount;//附件张数
	private String attachmentsFile;//附件文件（文件夹file/fundApply）
	private String oldFileName;//附件原名称
	private String isSelfDept;//是否跨部门报销（新添）（本部门/多部门）
	private String isTax;//是否增税发票
	private String invoiceNum;//发票号
	private String contractnum;//合同号
	private Integer heTongId;//合同id
	@FieldMeta(name = "申请说明")
	private String explain;//说明
	@FieldMeta(name = "合计金额")
	private Double totalMoney;//合计金额
	private Double payMoney;//财务已付款金额
	private Double backMoney;//还款金额
	private Integer epId;//审批流程Id
	private String epStattus;//审批状态
	private String addTime;//申请时间;
	@FieldMeta(name = "币种")
	private String currency;//币种
	private String cwUserCode;//财务经手人工号
	private String cwUserName;//财务经手人名称
	private String status;//状态（申请中/打回,部门确认,财务确认,个人确认,(支付:待还款,未还清),封闭）
	private String sureTime1;//财务确认时间
	private String sureTime2;//个人确认时间
	private String shoukuanZhanghao;//收款账号
	private String haswt;//是否委托(是,否（默认否）)
	private String jituan;//是否集团审批(是,否（默认否）)
	private Set<FundApplyDetailed> fadSet;// 一对多(使用明细)
	private List<FundApplyDetailed> fadList;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJituan() {
		return jituan;
	}
	public void setJituan(String jituan) {
		this.jituan = jituan;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getUnitname() {
		return unitname;
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public String getRelationclient() {
		return relationclient;
	}
	public void setRelationclient(String relationclient) {
		this.relationclient = relationclient;
	}
	public String getApprovalApplier() {
		return approvalApplier;
	}
	public void setApprovalApplier(String approvalApplier) {
		this.approvalApplier = approvalApplier;
	}
	public String getApprovaldept() {
		return approvaldept;
	}
	public void setApprovaldept(String approvaldept) {
		this.approvaldept = approvaldept;
	}
	public String getAccreditationnum() {
		return accreditationnum;
	}
	public void setAccreditationnum(String accreditationnum) {
		this.accreditationnum = accreditationnum;
	}
	public String getVoucherNature() {
		return voucherNature;
	}
	public void setVoucherNature(String voucherNature) {
		this.voucherNature = voucherNature;
	}
	public String getVoucherbasis() {
		return voucherbasis;
	}
	public void setVoucherbasis(String voucherbasis) {
		this.voucherbasis = voucherbasis;
	}
	public String getVoucherway() {
		return voucherway;
	}
	public void setVoucherway(String voucherway) {
		this.voucherway = voucherway;
	}
	public String getVouchersituation() {
		return vouchersituation;
	}
	public void setVouchersituation(String vouchersituation) {
		this.vouchersituation = vouchersituation;
	}
	public String getVouchercondition() {
		return vouchercondition;
	}
	public void setVouchercondition(String vouchercondition) {
		this.vouchercondition = vouchercondition;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPayStyle() {
		return payStyle;
	}
	public void setPayStyle(String payStyle) {
		this.payStyle = payStyle;
	}
	public String getBaoxiaoDate() {
		return baoxiaoDate;
	}
	public void setBaoxiaoDate(String baoxiaoDate) {
		this.baoxiaoDate = baoxiaoDate;
	}
	public Float getAttachmentsCount() {
		return attachmentsCount;
	}
	public void setAttachmentsCount(Float attachmentsCount) {
		this.attachmentsCount = attachmentsCount;
	}
	public String getAttachmentsFile() {
		return attachmentsFile;
	}
	public void setAttachmentsFile(String attachmentsFile) {
		this.attachmentsFile = attachmentsFile;
	}
	public String getIsSelfDept() {
		return isSelfDept;
	}
	public void setIsSelfDept(String isSelfDept) {
		this.isSelfDept = isSelfDept;
	}
	public String getIsTax() {
		return isTax;
	}
	public void setIsTax(String isTax) {
		this.isTax = isTax;
	}
	public String getInvoiceNum() {
		return invoiceNum;
	}
	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public Double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}
	@JSONField(serialize=false)
	public Set<FundApplyDetailed> getFadSet() {
		return fadSet;
	}
	public void setFadSet(Set<FundApplyDetailed> fadSet) {
		this.fadSet = fadSet;
	}
	public List<FundApplyDetailed> getFadList() {
		return fadList;
	}
	public void setFadList(List<FundApplyDetailed> fadList) {
		this.fadList = fadList;
	}
	public String getZhifuoryufu() {
		return zhifuoryufu;
	}
	public void setZhifuoryufu(String zhifuoryufu) {
		this.zhifuoryufu = zhifuoryufu;
	}
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getEpStattus() {
		return epStattus;
	}
	public void setEpStattus(String epStattus) {
		this.epStattus = epStattus;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPlanMonth() {
		return planMonth;
	}
	public void setPlanMonth(String planMonth) {
		this.planMonth = planMonth;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getCwUserCode() {
		return cwUserCode;
	}
	public void setCwUserCode(String cwUserCode) {
		this.cwUserCode = cwUserCode;
	}
	public String getCwUserName() {
		return cwUserName;
	}
	public void setCwUserName(String cwUserName) {
		this.cwUserName = cwUserName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSureTime1() {
		return sureTime1;
	}
	public void setSureTime1(String sureTime1) {
		this.sureTime1 = sureTime1;
	}
	public String getSureTime2() {
		return sureTime2;
	}
	public void setSureTime2(String sureTime2) {
		this.sureTime2 = sureTime2;
	}
	public Double getBackMoney() {
		return backMoney;
	}
	public void setBackMoney(Double backMoney) {
		this.backMoney = backMoney;
	}
	public String getContractnum() {
		return contractnum;
	}
	public void setContractnum(String contractnum) {
		this.contractnum = contractnum;
	}
	public Integer getHeTongId() {
		return heTongId;
	}
	public void setHeTongId(Integer heTongId) {
		this.heTongId = heTongId;
	}
	public String getShoukuanZhanghao() {
		return shoukuanZhanghao;
	}
	public void setShoukuanZhanghao(String shoukuanZhanghao) {
		this.shoukuanZhanghao = shoukuanZhanghao;
	}
	public String getHaswt() {
		return haswt;
	}
	public void setHaswt(String haswt) {
		this.haswt = haswt;
	}
	public String getOldFileName() {
		return oldFileName;
	}
	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}
	public Integer getRelationclientId() {
		return relationclientId;
	}
	public void setRelationclientId(Integer relationclientId) {
		this.relationclientId = relationclientId;
	}
	public String getJingbanren() {
		return jingbanren;
	}
	public void setJingbanren(String jingbanren) {
		this.jingbanren = jingbanren;
	}
	public Double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}
	
	
	
	
	
}