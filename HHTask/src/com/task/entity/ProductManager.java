package com.task.entity;

import java.io.Serializable;

import com.task.util.FieldMeta;

/**
 * @ClassName: TA_Product
 * @Description:
 * @author 订单明细表
 * @表名 TA_Product
 * @date 2012-11-28 上午10:57:11
 */
public class ProductManager implements Serializable {
	private static final long serialVersionUID = 1L;
	/** ID */
	private int id;
	/** 产品名称 */
	@FieldMeta(name = "产品名称")
	private String name;
	/** 件号 */
	@FieldMeta(name = "件号")
	private String pieceNumber;
	/**
	 * 业务件号
	 */
	@FieldMeta(name = "业务件号")
	private String ywMarkId;
	/** 数量 */
	@FieldMeta(name = "数量")
	private Float num;
	/** 总价 */
	private Double unitPrice;
	/** 备注 */
	private String remark;
	/** 车型 */
	private String carType;
	/** 型别 */
	private String type;
	/** 单价 */
	@FieldMeta(name = "单价")
	private Double unit;
	/** 不含税价 */
	@FieldMeta(name = "不含税价")
	private Double bhsPrice;
	/**
	 * 税率
	 */
	private Double taxprice;
	/**
	 * 在途订单新增字段
	 */
	private String banben;// 版本
	private String danwei;// 单位

	private Float cxApplyCount;// 冲销申请数量（未通过审批的预冲销数量）
	private Float cxCount;// 冲销数量（对预测或备货订单,指冲销给正式订单数量，对正式订单通过冲销方式生产的数量）
	// 正式订单使用，
	private Float cxHasTurn;// 冲销已转数量（标记正式订单中冲销的已经转换的数量，正式订单中已转数量应为：cxHasTurn+hasTurn）
	// 当cxRukuCount>cxzkuCount，对于正式订单来说还有待转库的成品
	// 正式订单使用
	private Float cxRukuCount;// （关联备货订单入库）冲销入库数量(正式订单记录冲销入库的数量,表示已生产完成不管是在备货库中还是在成品库中)
	// 正式订单使用
	private Float cxzkuCount;// 冲销转库数量（从备货转入成品冲销的入库数量,转库之后增加正式订单的allocationsNum）
	// 正式订单使用
	private Float cxzkuApplyCount;// 冲销转库申请数量
	/**
	 * 订单明细完成量
	 */
	private Float allocationsNum;// 已入库数量（包含正式订单中冲销入库数量）
	private Float hasTurn;// 已转换数量（不包含正式订单中冲销已转数量）(对预测或备货订单:num-hasTrun为可转数量，对正式订单num-hasTrun-cxCount为可转数量)
	private Float sellCount;// 出库数量
	private Float timeNum;// 及时完成数量
	private Float applyNumber;// 申请开票数量
	// 开票数量
	private Float kpNumber;
	// 回款数量
	private Float hkNumber;
	// 回款金额
	private Float hkMoney;
	// 废单数量
	private Float cutNum;
	private Integer priceId;

	// 页面传值
	private String orderNumber;// 订单编号
	private String outOrderNumber;// 外部订单号
	private String pmids;// 订单id
	private String bhnumber;// 备货订单号（页面传值）
	// 正式和备货订单关联表（ta_ProductZsAboutBh）
	// 页面传值

	private String status;// 状态(计划完善、计划转换、外委确认、物料采购、生产加工、入库、出库、开票、完成、取消)
	@FieldMeta(name = "申请取消时间")
	private String removeSqDate;// 申请取消时间
	@FieldMeta(name = "申请取消时间")
	private Float qxNum;// 取消数量;
	private String removeDate;// 取消时间
	private String paymentDate;// 交付日期;
	private String fmarkid;// 父件号
	private String isPeiJian;// 是否是配件
	/** 所属订单 */
	private OrderManager orderManager;

	private String qx_epstatus;// 取消审批时间
	private Integer qx_epId;//

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPieceNumber() {
		return pieceNumber;
	}

	public void setPieceNumber(String pieceNumber) {
		this.pieceNumber = pieceNumber;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public OrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public Double getUnit() {
		return unit;
	}

	public void setUnit(Double unit) {
		this.unit = unit;
	}

	public Float getKpNumber() {
		return kpNumber;
	}

	public void setKpNumber(Float kpNumber) {
		this.kpNumber = kpNumber;
	}

	public Float getHkNumber() {
		return hkNumber;
	}

	public void setHkNumber(Float hkNumber) {
		this.hkNumber = hkNumber;
	}

	public Float getHkMoney() {
		return hkMoney;
	}

	public void setHkMoney(Float hkMoney) {
		this.hkMoney = hkMoney;
	}

	public Float getCutNum() {
		if (cutNum == null) {
			return 0f;
		}
		return cutNum;
	}

	public void setCutNum(Float cutNum) {
		if (cutNum == null) {
			this.cutNum = 0f;
		} else {
			this.cutNum = cutNum;
		}
	}

	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getYwMarkId() {
		return ywMarkId;
	}

	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}

	public Double getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Double taxprice) {
		this.taxprice = taxprice;
	}

	public Double getBhsPrice() {
		return bhsPrice;
	}

	public void setBhsPrice(Double bhsPrice) {
		this.bhsPrice = bhsPrice;
	}

	public String getBanben() {
		return banben;
	}

	public void setBanben(String banben) {
		this.banben = banben;
	}

	public String getDanwei() {
		return danwei;
	}

	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPmids() {
		return pmids;
	}

	public void setPmids(String pmids) {
		this.pmids = pmids;
	}

	public Float getCxHasTurn() {
		return cxHasTurn;
	}

	public void setCxHasTurn(Float cxHasTurn) {
		this.cxHasTurn = cxHasTurn;
	}

	public Float getCxRukuCount() {
		return cxRukuCount;
	}

	public void setCxRukuCount(Float cxRukuCount) {
		this.cxRukuCount = cxRukuCount;
	}

	public Float getCxzkuCount() {
		return cxzkuCount;
	}

	public void setCxzkuCount(Float cxzkuCount) {
		this.cxzkuCount = cxzkuCount;
	}

	public Float getCxzkuApplyCount() {
		return cxzkuApplyCount;
	}

	public void setCxzkuApplyCount(Float cxzkuApplyCount) {
		this.cxzkuApplyCount = cxzkuApplyCount;
	}

	public String getOutOrderNumber() {
		return outOrderNumber;
	}

	public void setOutOrderNumber(String outOrderNumber) {
		this.outOrderNumber = outOrderNumber;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getBhnumber() {
		return bhnumber;
	}

	public void setBhnumber(String bhnumber) {
		this.bhnumber = bhnumber;
	}

	public String getFmarkid() {
		return fmarkid;
	}

	public void setFmarkid(String fmarkid) {
		this.fmarkid = fmarkid;
	}

	public String getIsPeiJian() {
		return isPeiJian;
	}

	public void setIsPeiJian(String isPeiJian) {
		this.isPeiJian = isPeiJian;
	}

	public String getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(String removeDate) {
		this.removeDate = removeDate;
	}

	public Float getNum() {
		return num;
	}

	public void setNum(Float num) {
		this.num = num;
	}

	/**
	 * @return the cxApplyCount
	 */
	public Float getCxApplyCount() {
		return cxApplyCount;
	}

	/**
	 * @param cxApplyCount
	 *            the cxApplyCount to set
	 */
	public void setCxApplyCount(Float cxApplyCount) {
		this.cxApplyCount = cxApplyCount;
	}

	/**
	 * @return the cxCount
	 */
	public Float getCxCount() {
		return cxCount;
	}

	/**
	 * @param cxCount
	 *            the cxCount to set
	 */
	public void setCxCount(Float cxCount) {
		this.cxCount = cxCount;
	}

	/**
	 * @return the allocationsNum
	 */
	public Float getAllocationsNum() {
		return allocationsNum;
	}

	/**
	 * @param allocationsNum
	 *            the allocationsNum to set
	 */
	public void setAllocationsNum(Float allocationsNum) {
		this.allocationsNum = allocationsNum;
	}

	/**
	 * @return the hasTurn
	 */
	public Float getHasTurn() {
		return hasTurn;
	}

	/**
	 * @param hasTurn
	 *            the hasTurn to set
	 */
	public void setHasTurn(Float hasTurn) {
		this.hasTurn = hasTurn;
	}

	/**
	 * @return the sellCount
	 */
	public Float getSellCount() {
		return sellCount;
	}

	/**
	 * @param sellCount
	 *            the sellCount to set
	 */
	public void setSellCount(Float sellCount) {
		this.sellCount = sellCount;
	}

	/**
	 * @return the timeNum
	 */
	public Float getTimeNum() {
		return timeNum;
	}

	/**
	 * @param timeNum
	 *            the timeNum to set
	 */
	public void setTimeNum(Float timeNum) {
		this.timeNum = timeNum;
	}

	/**
	 * @return the applyNumber
	 */
	public Float getApplyNumber() {
		return applyNumber;
	}

	/**
	 * @param applyNumber
	 *            the applyNumber to set
	 */
	public void setApplyNumber(Float applyNumber) {
		this.applyNumber = applyNumber;
	}

	public String getRemoveSqDate() {
		return removeSqDate;
	}

	public void setRemoveSqDate(String removeSqDate) {
		this.removeSqDate = removeSqDate;
	}

	public String getQx_epstatus() {
		return qx_epstatus;
	}

	public void setQx_epstatus(String qxEpstatus) {
		qx_epstatus = qxEpstatus;
	}

	public Integer getQx_epId() {
		return qx_epId;
	}

	public void setQx_epId(Integer qxEpId) {
		qx_epId = qxEpId;
	}

	public Float getQxNum() {
		return qxNum;
	}

	public void setQxNum(Float qxNum) {
		this.qxNum = qxNum;
	}

}
