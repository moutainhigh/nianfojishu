package com.task.entity.caiwu.core;

import java.io.Serializable;

import com.task.util.FieldMeta;

/**
 * 主营业务应付(ta_fin_caiwu_CorePayable)
 * 
 * @author 刘培
 * 
 */
public class CorePayable implements Serializable{
	private Integer id;
	private Integer fk_CPMId;//月度账单id
	
	@FieldMeta(name = "应付类型")
	private String subjectItem;// 科目，类型（外委、外购、。。。）
	private String fkNumber;//付款编号(FyyyyMMdd0001)
	private String coreType;//付款类型(主营/非主营)

	private String zhaiyao;// 摘要，应付款名称、(件号、件号+工序号)
	private Float number;// 入库数量
	private String unit;// 单位
	private Double hsPrice;// 含税单价
	private Double bhsPrice;// 不含税单价
	private Double zgbhsPrice;// 暂估不含税单价
	private Integer priceId;// 单价id
	private Float taxprice;// 税率
	private String hetongbianhao;// 价格合同编号

	private Double kkpNumber;// 可开票数量
	// private Float number;// 预留多个批次领料 数量

	private Float yufukuanJine;// 预付款金额
	@FieldMeta(name = "应付款金额")
	private Double yingfukuanJine;// 应付款金额
	private Double yipizhunJine;// 已审批金额

	private Double realfukuanJine;// 已付款金额

	@FieldMeta(name = "添加时间")
	private String saveTime;// 添加时间(入库时间)
	private String saveUser;// 添加人(入库操作人员)
	private String jzMonth;// 记账月份(需要结合公司账期表时间内)
	private String fukuanZq;// 付款周期(即付、30天、60天、90天)
	@FieldMeta(name = "应付款日期")
	private String fukuanDate;// 应付款日期(根据添加时间+付款周期自动计算)
	private String lateDate;// 实际付款日期

	@FieldMeta(name = "负责人")
	private String fuzeren;// 负责人(采购员)

	private String status;// 状态(对账、对账复核、可开票、发票复核、供应商验证、待付款、付款申请、付款中、完成)

	/********采购订单************/
	private String cgOrderType;//订单类型
	private String proName;//零件名称
	private String specification;//规格
	private String orderNumber;// 订单号
	private Integer orderId;// 订单明细ID
	private String deliveryNumber;// 送货单号
	private Integer deliveryId;// 送货单明细ID
	private String shDte;//送货日期
	private String rukuNumber;//票据单号
	private String cangqu;//仓区
	private Float shNumber;//送货数量；
	private String kgliao;//供料属性
	private String ywmarkId;//业务件号
	private String demanddept;//需求部门(辅料使用)
	
	/*********员工工资******************/
	private String wageMonth;//工资月份
	
	/*********费用报销(预付/支付)******************/
	private Integer fk_fundApplyId;//资金使用单id
	

	private String supplierName;// 供应商名称(收款单位)
	private Integer supplierId;// 供应商id

	private String markId;// 件号
	private String processNo;// 工序号
	private String processName;// 工序名称

	private String lot;// 库存批次
	private Integer goodsId;// 库存Id

	private String selfCard;// 领料批次
	private Integer procardId;// 产品Id

	private String fapiaoNum;// 发票号
	private String fujian;// 发票附件
	private String randomyzm;// 随机验证码

	private Integer epId;// 发票审批流程id
	@FieldMeta(name = "审批")
	private String auditStatus;// 审批状态(未审批/已审批)

	private String more;// 备注

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectItem() {
		return subjectItem;
	}

	public void setSubjectItem(String subjectItem) {
		this.subjectItem = subjectItem;
	}

	public String getZhaiyao() {
		return zhaiyao;
	}

	public void setZhaiyao(String zhaiyao) {
		this.zhaiyao = zhaiyao;
	}

	public Double getHsPrice() {
		return hsPrice;
	}

	public void setHsPrice(Double hsPrice) {
		this.hsPrice = hsPrice;
	}

	public Double getBhsPrice() {
		return bhsPrice;
	}

	public void setBhsPrice(Double bhsPrice) {
		this.bhsPrice = bhsPrice;
	}

	public String getHetongbianhao() {
		return hetongbianhao;
	}

	public void setHetongbianhao(String hetongbianhao) {
		this.hetongbianhao = hetongbianhao;
	}

	public Float getNumber() {
		return number;
	}

	public void setNumber(Float number) {
		this.number = number;
	}

	public Float getYufukuanJine() {
		return yufukuanJine;
	}

	public void setYufukuanJine(Float yufukuanJine) {
		this.yufukuanJine = yufukuanJine;
	}

	public Double getYingfukuanJine() {
		return yingfukuanJine;
	}

	public void setYingfukuanJine(Double yingfukuanJine) {
		this.yingfukuanJine = yingfukuanJine;
	}

	public Double getRealfukuanJine() {
		return realfukuanJine;
	}

	public void setRealfukuanJine(Double realfukuanJine) {
		this.realfukuanJine = realfukuanJine;
	}

	public String getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(String saveTime) {
		this.saveTime = saveTime;
	}

	public String getSaveUser() {
		return saveUser;
	}

	public void setSaveUser(String saveUser) {
		this.saveUser = saveUser;
	}

	

	public String getFukuanZq() {
		return fukuanZq;
	}

	public void setFukuanZq(String fukuanZq) {
		this.fukuanZq = fukuanZq;
	}

	public String getLateDate() {
		return lateDate;
	}

	public void setLateDate(String lateDate) {
		this.lateDate = lateDate;
	}

	public String getFuzeren() {
		return fuzeren;
	}

	public void setFuzeren(String fuzeren) {
		this.fuzeren = fuzeren;
	}

	public String getFujian() {
		return fujian;
	}

	public void setFujian(String fujian) {
		this.fujian = fujian;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeliveryNumber() {
		return deliveryNumber;
	}

	public void setDeliveryNumber(String deliveryNumber) {
		this.deliveryNumber = deliveryNumber;
	}

	public Integer getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Integer deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getSelfCard() {
		return selfCard;
	}

	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getFukuanDate() {
		return fukuanDate;
	}

	public void setFukuanDate(String fukuanDate) {
		this.fukuanDate = fukuanDate;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public Float getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Float taxprice) {
		this.taxprice = taxprice;
	}

	public Integer getProcardId() {
		return procardId;
	}

	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
	}

	public String getFapiaoNum() {
		return fapiaoNum;
	}

	public void setFapiaoNum(String fapiaoNum) {
		this.fapiaoNum = fapiaoNum;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Double getKkpNumber() {
		return kkpNumber;
	}

	public void setKkpNumber(Double kkpNumber) {
		this.kkpNumber = kkpNumber;
	}

	public String getProcessNo() {
		return processNo;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getJzMonth() {
		return jzMonth;
	}

	public void setJzMonth(String jzMonth) {
		this.jzMonth = jzMonth;
	}

	public String getRandomyzm() {
		return randomyzm;
	}

	public void setRandomyzm(String randomyzm) {
		this.randomyzm = randomyzm;
	}

	public Double getYipizhunJine() {
		return yipizhunJine;
	}

	public void setYipizhunJine(Double yipizhunJine) {
		this.yipizhunJine = yipizhunJine;
	}

	public String getFkNumber() {
		return fkNumber;
	}

	public void setFkNumber(String fkNumber) {
		this.fkNumber = fkNumber;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public void setWageMonth(String wageMonth) {
		this.wageMonth = wageMonth;
	}

	public Integer getFk_fundApplyId() {
		return fk_fundApplyId;
	}

	public void setFk_fundApplyId(Integer fkFundApplyId) {
		fk_fundApplyId = fkFundApplyId;
	}

	public String getCoreType() {
		return coreType;
	}

	public void setCoreType(String coreType) {
		this.coreType = coreType;
	}

	public Double getZgbhsPrice() {
		return zgbhsPrice;
	}

	public void setZgbhsPrice(Double zgbhsPrice) {
		this.zgbhsPrice = zgbhsPrice;
	}

	public Integer getFk_CPMId() {
		return fk_CPMId;
	}

	public void setFk_CPMId(Integer fkCPMId) {
		fk_CPMId = fkCPMId;
	}

	public String getShDte() {
		return shDte;
	}

	public void setShDte(String shDte) {
		this.shDte = shDte;
	}

	public String getRukuNumber() {
		return rukuNumber;
	}

	public void setRukuNumber(String rukuNumber) {
		this.rukuNumber = rukuNumber;
	}

	public String getCangqu() {
		return cangqu;
	}

	public void setCangqu(String cangqu) {
		this.cangqu = cangqu;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getSpecification() {
		return specification;
	}

	public Float getShNumber() {
		return shNumber;
	}

	public void setShNumber(Float shNumber) {
		this.shNumber = shNumber;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getKgliao() {
		return kgliao;
	}

	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

	public String getYwmarkId() {
		return ywmarkId;
	}

	public void setYwmarkId(String ywmarkId) {
		this.ywmarkId = ywmarkId;
	}

	public String getCgOrderType() {
		return cgOrderType;
	}

	public void setCgOrderType(String cgOrderType) {
		this.cgOrderType = cgOrderType;
	}

	public String getDemanddept() {
		return demanddept;
	}

	public void setDemanddept(String demanddept) {
		this.demanddept = demanddept;
	}
	
}
