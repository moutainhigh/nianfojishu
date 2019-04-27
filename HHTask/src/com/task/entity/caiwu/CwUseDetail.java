package com.task.entity.caiwu;

import java.io.Serializable;

/**
 * 辅助明细(ta_fin_CwUseDetail)
 * 
 * @author txb
 * 
 */
public class CwUseDetail implements Serializable{
	private Integer id;
	private String payee;//收款单位
	private String useFor;// 摘要
	private Double usemoney;// 使用金额

	private String payNum;//支付流水号
	private String aboutNum;// 关联单号
	private Integer fk_fundApplyId;// 费用报销单Id
	private Integer fk_monthlyBillsId;// 采购订单月度对账单Id
	private Integer fk_goodsStoreId;// 入库历史记录Id
	private Integer fk_sellId;// 出库历史记录Id
	private String payType;// 凭证类型(采购订单/费用报销/入库暂估/计提)

	private CwVouchersDetail cwVouchersDetail;// 财务凭证明细

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUseFor() {
		return useFor;
	}

	public void setUseFor(String useFor) {
		this.useFor = useFor;
	}

	public Double getUsemoney() {
		return usemoney;
	}

	public void setUsemoney(Double usemoney) {
		this.usemoney = usemoney;
	}

	public CwVouchersDetail getCwVouchersDetail() {
		return cwVouchersDetail;
	}

	public void setCwVouchersDetail(CwVouchersDetail cwVouchersDetail) {
		this.cwVouchersDetail = cwVouchersDetail;
	}

	public String getAboutNum() {
		return aboutNum;
	}

	public void setAboutNum(String aboutNum) {
		this.aboutNum = aboutNum;
	}

	public Integer getFk_fundApplyId() {
		return fk_fundApplyId;
	}

	public void setFk_fundApplyId(Integer fkFundApplyId) {
		fk_fundApplyId = fkFundApplyId;
	}

	public Integer getFk_monthlyBillsId() {
		return fk_monthlyBillsId;
	}

	public void setFk_monthlyBillsId(Integer fkMonthlyBillsId) {
		fk_monthlyBillsId = fkMonthlyBillsId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getPayNum() {
		return payNum;
	}

	public void setPayNum(String payNum) {
		this.payNum = payNum;
	}

	public Integer getFk_goodsStoreId() {
		return fk_goodsStoreId;
	}

	public void setFk_goodsStoreId(Integer fkGoodsStoreId) {
		fk_goodsStoreId = fkGoodsStoreId;
	}

	public Integer getFk_sellId() {
		return fk_sellId;
	}

	public void setFk_sellId(Integer fkSellId) {
		fk_sellId = fkSellId;
	}

}
