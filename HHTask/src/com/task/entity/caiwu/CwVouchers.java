package com.task.entity.caiwu;

import java.io.Serializable;
import java.util.Set;

/**
 * 财务凭证 （ta_fin_CwVouchers）
 * @author txb
 *
 */
public class CwVouchers implements Serializable{
	private Integer id;
	private String number;//凭证号
	private String vouchermonth;//凭证月份
	private String voucherdate;//凭证日期
	private String createtime;//创建时间
	private String userName;//制单人
	private String createCode;//创建人工号
	private String createName;//创建人
	private String applyStatus;//审批状态
	private String zzStatus;//做账状态
	private String zzPizNumber;//做账凭证编号
	private String zzFile;//做账附件名称
	private String zzTime;//做账确认时间
	private Integer epId;//审批id
	private Double jieMoney;//借总额
	private Double daiMoney;//贷总额
	private Integer fk_receiptLogId;//付款明细id
	private Set<CwVouchersDetail> cwVouchersDetails;//明细
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getZzStatus() {
		return zzStatus;
	}
	public void setZzStatus(String zzStatus) {
		this.zzStatus = zzStatus;
	}
	public String getZzPizNumber() {
		return zzPizNumber;
	}
	public void setZzPizNumber(String zzPizNumber) {
		this.zzPizNumber = zzPizNumber;
	}
	public String getZzFile() {
		return zzFile;
	}
	public void setZzFile(String zzFile) {
		this.zzFile = zzFile;
	}
	public String getZzTime() {
		return zzTime;
	}
	public void setZzTime(String zzTime) {
		this.zzTime = zzTime;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getVouchermonth() {
		return vouchermonth;
	}
	public void setVouchermonth(String vouchermonth) {
		this.vouchermonth = vouchermonth;
	}
	public String getVoucherdate() {
		return voucherdate;
	}
	public void setVoucherdate(String voucherdate) {
		this.voucherdate = voucherdate;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateCode() {
		return createCode;
	}
	public void setCreateCode(String createCode) {
		this.createCode = createCode;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public Double getJieMoney() {
		return jieMoney;
	}
	public void setJieMoney(Double jieMoney) {
		this.jieMoney = jieMoney;
	}
	public Double getDaiMoney() {
		return daiMoney;
	}
	public void setDaiMoney(Double daiMoney) {
		this.daiMoney = daiMoney;
	}
	public Set<CwVouchersDetail> getCwVouchersDetails() {
		return cwVouchersDetails;
	}
	public void setCwVouchersDetails(Set<CwVouchersDetail> cwVouchersDetails) {
		this.cwVouchersDetails = cwVouchersDetails;
	}
	public Integer getFk_receiptLogId() {
		return fk_receiptLogId;
	}
	public void setFk_receiptLogId(Integer fkReceiptLogId) {
		fk_receiptLogId = fkReceiptLogId;
	}
	
	
	
}
