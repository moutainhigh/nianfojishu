package com.task.entity;

import java.io.Serializable;

import com.task.util.FieldMeta;

/***
 * 出库历史表(表名:sell)
 * 
 */
public class Sell implements java.io.Serializable {

	// 销售信息
	private static final long serialVersionUID = 1L;
	private Integer sellId; // id
	@FieldMeta(name="库别")
	private String sellWarehouse;// 库别
	@FieldMeta(name="仓区")
	private String goodHouseName;// 仓区
	@FieldMeta(name="库位")
	private String kuwei;// 库位;
	@FieldMeta(name="件号")
	private String sellMarkId;// 件号
	@FieldMeta(name="版本号")
	private String banBenNumber;// 版本
	@FieldMeta(name="供料属性")
	private String kgliao;// 供料属性
	@FieldMeta(name="批次")
	private String sellLot; // 批次

	@FieldMeta(name="物料类别")
	private String wgType;// 物料类别
	@FieldMeta(name="品名")
	private String sellGoods; // 品名
	@FieldMeta(name="规格")
	private String sellFormat; // 规格
	@FieldMeta(name="出库数量")
	private Float sellCount; // 数量
	private Float tksellCount; // 退库数量
	@FieldMeta(name="单位")
	private String sellUnit; // 单位
	private Float sellZhishu;// 转换数量
	private String goodsStoreZHUnit;// 转换单位

	private String sellSupplier;// 供应商
	private String sellCompanyName;// 客户
	private String customerId;// 客户ID
	private String supplierId;// 供应商ID

	@FieldMeta(name="申请日期")
	private String sellDate; // 日期

	private String sellCompanyPeople;// 客户方负责人
	private String sellNumber; // 编号
	private String sellFapiao; // 发票号
	private String sellInvoice; // 发票
	private String sellPaydate; // 回款日期
	private String sellLuId; // 炉号/需求部门(辅料使用)
	private String sellUsefull; // 用途
	private Float sellTotal; // 总数z
	private Float goodsPrice;//库存单价(不含税)
	private Float sellPrice; //批次 单价
	private Float sellbhsPrice;//批次 不含税单价
	private Float taxprice;//批次 税率
	private Integer sellReturnCount;// 返回入库数
	private Integer sellAdminId;
	private String sellMore;
	private String sellAdminName;// 管理员
	private String sellGetGoodsMan;// 负责人
	private String sellPlanner; // 计划员
	private String sellPeople; //
	private Integer sellcharId;// 领料人Id
	private String sellchardept;// 领料人部门
	@FieldMeta(name="领料人")
	private String sellCharger; // 领料人
	private String sellMarkFormat; // 合成字段
	private String sellArtsCard; // 工艺卡号(批次)
	private String sellProMarkId; // 总成件号
	private String sellTime; // 时间
	private String sellHkId;// 追款编号
	private String sellSendnum;// 送货编号
	private Float sellSendCost;// 运费（同送货编号总费用）
	private String fproductno;// BTS条码
	private String tuhao;//
	private Integer processNo;// 工序号(外委接受回来的标识为半成品转库的库存记录外委最后一道工序)
	private String processName;// 工序名称
	private Integer goodsId;
	private Integer procardId;//流水卡号ID
	/**
	 * 流水卡片新增字段
	 * 
	 * @return
	 */
	private String planID;// 计划单号
	@FieldMeta(name="出库类型")
	private String style;// 出库类型（正常出库/返修出库/退料出库）
	@FieldMeta(name="备注")
	private String sellGoodsMore; // 备注
	private Boolean bout;// 出库权限
	private Boolean bedit;// 编辑权限
	private Boolean isdel;//删除权限
	private String printStatus;// 打印状态(YES/NO)
	private String printNumber;// 打印单号
	private String orderNum;// 订单号（内部）
	private String outOrderNumer;// 订单号（外部）
	private String rootSelfCard;// 总成批次
	private Integer wgOrderId;// 采购订单id
	private String wgOrdernumber;//采购单号
	private Integer mopdId;//物料需求明细 id  ManualOrderPlanDetail

	private String lingliaocardId;// 领料人Id
	private String lingliaoName;// 领料人
	private Integer lingliaoUserId;// 领料人Id
	private String ywmarkId;// 业务件号

	// 借领属性
	private String lendNeckStatus;// 借领属性:借/领
	private Float lendNeckCount;// 可借数量(领的时候此值不存在)
	private String nectCanChangeStatus;// 领的时候是否可以以旧换新状态：是/否
	private String proNumber;// 项目编号
	private String zhidanPerson;//制单人;
	
	private String planTotalNum;//物料需求单号
	private String cgNumber;//采购单号
	private String handwordSellStatus;
	private Integer handwordSellNumber;
	private Integer handwordSellEpId;
	
	private String suodingdanhao;//
	
	private String changePrint;//调拨打印（是，null）
	
	private String goodsStoreWarehouse;//页面传值
	private String goodsStorehouseName;//页面传值
	private String goodsStorePosition;//页面传值
	private Float money;//金额
	
	private String demanddept;//需求部门(辅料使用)
	
	/**
	 * @return the lendNeckStatus
	 */
	public String getLendNeckStatus() {
		return lendNeckStatus;
	}

	

	public Sell(Integer sellId, String sellWarehouse, String goodHouseName, String kuwei, String sellMarkId,
			String banBenNumber, String kgliao, String sellGoods, String sellFormat,
			Float sellCount, String sellUnit,String sellMore, String sellTime,String style, 
			String changePrint, String goodsStoreWarehouse, String goodsStorehouseName, String goodsStorePosition) {
		super();
		this.sellId = sellId;
		this.sellWarehouse = sellWarehouse;
		this.goodHouseName = goodHouseName;
		this.kuwei = kuwei;
		this.sellMarkId = sellMarkId;
		this.banBenNumber = banBenNumber;
		this.kgliao = kgliao;
		this.sellGoods = sellGoods;
		this.sellFormat = sellFormat;
		this.sellCount = sellCount;
		this.sellUnit = sellUnit;
		this.sellMore = sellMore;
		this.sellTime = sellTime;
		this.style = style;
		this.changePrint = changePrint;
		this.goodsStoreWarehouse = goodsStoreWarehouse;
		this.goodsStorehouseName = goodsStorehouseName;
		this.goodsStorePosition = goodsStorePosition;
	}
	public Sell(Integer sellId, String sellWarehouse, String goodHouseName, String kuwei, String sellMarkId,
			String banBenNumber, String kgliao, String sellGoods, String sellFormat,
			Float sellCount, String sellUnit,String sellMore, String sellTime,String style, 
			String changePrint, String goodsStoreWarehouse, String goodsStorehouseName, String goodsStorePosition,String ywmarkId) {
		super();
		this.sellId = sellId;
		this.sellWarehouse = sellWarehouse;
		this.goodHouseName = goodHouseName;
		this.kuwei = kuwei;
		this.sellMarkId = sellMarkId;
		this.banBenNumber = banBenNumber;
		this.kgliao = kgliao;
		this.sellGoods = sellGoods;
		this.sellFormat = sellFormat;
		this.sellCount = sellCount;
		this.sellUnit = sellUnit;
		this.sellMore = sellMore;
		this.sellTime = sellTime;
		this.style = style;
		this.changePrint = changePrint;
		this.goodsStoreWarehouse = goodsStoreWarehouse;
		this.goodsStorehouseName = goodsStorehouseName;
		this.goodsStorePosition = goodsStorePosition;
		this.ywmarkId = ywmarkId;
	}


	/**
	 * @param lendNeckStatus
	 *            the lendNeckStatus to set
	 */
	public void setLendNeckStatus(String lendNeckStatus) {
		this.lendNeckStatus = lendNeckStatus;
	}

	public String getProNumber() {
		return proNumber;
	}

	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
	}

	/**
	 * @return the lendNeckCount
	 */
	public Float getLendNeckCount() {
		return lendNeckCount;
	}

	/**
	 * @param lendNeckCount
	 *            the lendNeckCount to set
	 */
	public void setLendNeckCount(Float lendNeckCount) {
		this.lendNeckCount = lendNeckCount;
	}

	/**
	 * @return the nectCanChangeStatus
	 */
	public String getNectCanChangeStatus() {
		return nectCanChangeStatus;
	}

	/**
	 * @param nectCanChangeStatus
	 *            the nectCanChangeStatus to set
	 */
	public void setNectCanChangeStatus(String nectCanChangeStatus) {
		this.nectCanChangeStatus = nectCanChangeStatus;
	}

	public String getGoodHouseName() {
		return goodHouseName;
	}

	public void setGoodHouseName(String goodHouseName) {
		this.goodHouseName = goodHouseName;
	}

	public String getSellProMarkId() {
		return sellProMarkId;
	}

	public String getSellSendnum() {
		return sellSendnum;
	}

	public void setSellSendnum(String sellSendnum) {
		this.sellSendnum = sellSendnum;
	}

	public void setSellProMarkId(String sellProMarkId) {
		this.sellProMarkId = sellProMarkId;
	}

	public String getSellArtsCard() {
		return sellArtsCard;
	}

	public void setSellArtsCard(String sellArtsCard) {
		this.sellArtsCard = sellArtsCard;
	}

	/** default constructor */
	public Sell() {
	}

	public Integer getSellId() {
		return sellId;
	}

	public void setSellId(Integer sellId) {
		this.sellId = sellId;
	}

	public String getSellMarkId() {
		return sellMarkId;
	}

	public void setSellMarkId(String sellMarkId) {
		this.sellMarkId = sellMarkId;
	}

	public String getSellLot() {
		return sellLot;
	}

	public void setSellLot(String sellLot) {
		this.sellLot = sellLot;
	}

	public String getSellSupplier() {
		return sellSupplier;
	}

	public void setSellSupplier(String sellSupplier) {
		this.sellSupplier = sellSupplier;
	}

	public String getSellCompanyName() {
		return sellCompanyName;
	}

	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}

	public String getSellDate() {
		return sellDate;
	}

	public void setSellDate(String sellDate) {
		this.sellDate = sellDate;
	}

	public String getSellNumber() {
		return sellNumber;
	}

	public void setSellNumber(String sellNumber) {
		this.sellNumber = sellNumber;
	}

	public String getSellFapiao() {
		return sellFapiao;
	}

	public void setSellFapiao(String sellFapiao) {
		this.sellFapiao = sellFapiao;
	}

	public String getSellWarehouse() {
		return sellWarehouse;
	}

	public void setSellWarehouse(String sellWarehouse) {
		this.sellWarehouse = sellWarehouse;
	}

	public String getSellInvoice() {
		return sellInvoice;
	}

	public void setSellInvoice(String sellInvoice) {
		this.sellInvoice = sellInvoice;
	}

	public String getSellPaydate() {
		return sellPaydate;
	}

	public void setSellPaydate(String sellPaydate) {
		this.sellPaydate = sellPaydate;
	}

	public String getSellGoods() {
		return sellGoods;
	}

	public void setSellGoods(String sellGoods) {
		this.sellGoods = sellGoods;
	}

	public String getSellFormat() {
		return sellFormat;
	}

	public void setSellFormat(String sellFormat) {
		this.sellFormat = sellFormat;
	}

	public String getSellUnit() {
		return sellUnit;
	}

	public void setSellUnit(String sellUnit) {
		this.sellUnit = sellUnit;
	}

	public String getSellLuId() {
		return sellLuId;
	}

	public void setSellLuId(String sellLuId) {
		this.sellLuId = sellLuId;
	}

	public String getSellUsefull() {
		return sellUsefull;
	}

	public void setSellUsefull(String sellUsefull) {
		this.sellUsefull = sellUsefull;
	}

	public Float getSellCount() {
		return sellCount;
	}

	public void setSellCount(Float sellCount) {
		this.sellCount = sellCount;
	}

	public Float getSellTotal() {
		return sellTotal;
	}

	public void setSellTotal(Float sellTotal) {
		this.sellTotal = sellTotal;
	}

	public Float getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Float sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Float getTksellCount() {
		return tksellCount;
	}

	public void setTksellCount(Float tksellCount) {
		this.tksellCount = tksellCount;
	}

	public Integer getSellReturnCount() {
		return sellReturnCount;
	}

	public void setSellReturnCount(Integer sellReturnCount) {
		this.sellReturnCount = sellReturnCount;
	}

	public String getSellGoodsMore() {
		return sellGoodsMore;
	}

	public void setSellGoodsMore(String sellGoodsMore) {
		this.sellGoodsMore = sellGoodsMore;
	}

	public String getSellPeople() {
		return sellPeople;
	}

	public void setSellPeople(String sellPeople) {
		this.sellPeople = sellPeople;
	}

	public Integer getSellAdminId() {
		return sellAdminId;
	}

	public void setSellAdminId(Integer sellAdminId) {
		this.sellAdminId = sellAdminId;
	}

	public String getSellAdminName() {
		return sellAdminName;
	}

	public void setSellAdminName(String sellAdminName) {
		this.sellAdminName = sellAdminName;
	}

	public String getSellMore() {
		return sellMore;
	}

	public void setSellMore(String sellMore) {
		this.sellMore = sellMore;
	}

	public String getSellGetGoodsMan() {
		return sellGetGoodsMan;
	}

	public void setSellGetGoodsMan(String sellGetGoodsMan) {
		this.sellGetGoodsMan = sellGetGoodsMan;
	}

	public String getSellPlanner() {
		return sellPlanner;
	}

	public void setSellPlanner(String sellPlanner) {
		this.sellPlanner = sellPlanner;
	}

	public String getSellCharger() {
		return sellCharger;
	}

	public void setSellCharger(String sellCharger) {
		this.sellCharger = sellCharger;
	}

	public String getSellMarkFormat() {
		return sellMarkFormat;
	}

	public void setSellMarkFormat(String sellMarkFormat) {
		this.sellMarkFormat = sellMarkFormat;
	}

	public String getSellTime() {
		return sellTime;
	}

	public void setSellTime(String sellTime) {
		this.sellTime = sellTime;
	}

	public Float getSellZhishu() {
		return sellZhishu;
	}

	public void setSellZhishu(Float sellZhishu) {
		this.sellZhishu = sellZhishu;
	}

	public String getSellHkId() {
		return sellHkId;
	}

	public void setSellHkId(String sellHkId) {
		this.sellHkId = sellHkId;
	}

	public String getPlanID() {
		return planID;
	}

	public void setPlanID(String planID) {
		this.planID = planID;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getPrintStatus() {
		return printStatus;
	}

	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}

	public Boolean getBout() {
		return bout;
	}

	public void setBout(Boolean bout) {
		this.bout = bout;
	}

	public Boolean getBedit() {
		return bedit;
	}

	public void setBedit(Boolean bedit) {
		this.bedit = bedit;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getSellCompanyPeople() {
		return sellCompanyPeople;
	}

	public void setSellCompanyPeople(String sellCompanyPeople) {
		this.sellCompanyPeople = sellCompanyPeople;
	}

	public String getOutOrderNumer() {
		return outOrderNumer;
	}

	public void setOutOrderNumer(String outOrderNumer) {
		this.outOrderNumer = outOrderNumer;
	}

	public String getGoodsStoreZHUnit() {
		return goodsStoreZHUnit;
	}

	public void setGoodsStoreZHUnit(String goodsStoreZHUnit) {
		this.goodsStoreZHUnit = goodsStoreZHUnit;
	}

	public Float getSellSendCost() {
		return sellSendCost;
	}

	public void setSellSendCost(Float sellSendCost) {
		this.sellSendCost = sellSendCost;
	}

	public String getKgliao() {
		return kgliao;
	}

	public String getFproductno() {
		return fproductno;
	}

	public void setFproductno(String fproductno) {
		this.fproductno = fproductno;
	}

	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

	public String getTuhao() {
		return tuhao;
	}

	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}

	public String getWgType() {
		return wgType;
	}

	public void setWgType(String wgType) {
		this.wgType = wgType;
	}

	public String getKuwei() {
		return kuwei;
	}

	public void setKuwei(String kuwei) {
		this.kuwei = kuwei;
	}

	public String getBanBenNumber() {
		return banBenNumber;
	}

	public void setBanBenNumber(String banBenNumber) {
		this.banBenNumber = banBenNumber;
	}

	public Integer getProcessNo() {
		return processNo;
	}

	public void setProcessNo(Integer processNo) {
		this.processNo = processNo;
	}

	public Integer getWgOrderId() {
		return wgOrderId;
	}

	public void setWgOrderId(Integer wgOrderId) {
		this.wgOrderId = wgOrderId;
	}

	public String getLingliaocardId() {
		return lingliaocardId;
	}

	public void setLingliaocardId(String lingliaocardId) {
		this.lingliaocardId = lingliaocardId;
	}

	public String getLingliaoName() {
		return lingliaoName;
	}

	public void setLingliaoName(String lingliaoName) {
		this.lingliaoName = lingliaoName;
	}

	public Integer getLingliaoUserId() {
		return lingliaoUserId;
	}

	public void setLingliaoUserId(Integer lingliaoUserId) {
		this.lingliaoUserId = lingliaoUserId;
	}

	public String getYwmarkId() {
		return ywmarkId;
	}

	public void setYwmarkId(String ywmarkId) {
		this.ywmarkId = ywmarkId;
	}

	public String getRootSelfCard() {
		return rootSelfCard;
	}

	public void setRootSelfCard(String rootSelfCard) {
		this.rootSelfCard = rootSelfCard;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Integer getSellcharId() {
		return sellcharId;
	}

	public void setSellcharId(Integer sellcharId) {
		this.sellcharId = sellcharId;
	}

	public String getSellchardept() {
		return sellchardept;
	}

	public void setSellchardept(String sellchardept) {
		this.sellchardept = sellchardept;
	}

	public Float getSellbhsPrice() {
		return sellbhsPrice;
	}

	public void setSellbhsPrice(Float sellbhsPrice) {
		this.sellbhsPrice = sellbhsPrice;
	}

	public Float getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Float taxprice) {
		this.taxprice = taxprice;
	}

	public String getPrintNumber() {
		return printNumber;
	}

	public void setPrintNumber(String printNumber) {
		this.printNumber = printNumber;
	}

	public Float getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(Float goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public String getWgOrdernumber() {
		return wgOrdernumber;
	}

	public void setWgOrdernumber(String wgOrdernumber) {
		this.wgOrdernumber = wgOrdernumber;
	}

	public Integer getMopdId() {
		return mopdId;
	}

	public void setMopdId(Integer mopdId) {
		this.mopdId = mopdId;
	}

	public String getZhidanPerson() {
		return zhidanPerson;
	}
	public String getPlanTotalNum() {
		return planTotalNum;
	}
	public void setZhidanPerson(String zhidanPerson) {
		this.zhidanPerson = zhidanPerson;
	}


	public void setPlanTotalNum(String planTotalNum) {
		this.planTotalNum = planTotalNum;
	}

	public String getHandwordSellStatus() {
		return handwordSellStatus;
	}

	public void setHandwordSellStatus(String handwordSellStatus) {
		this.handwordSellStatus = handwordSellStatus;
	}

	public Integer getHandwordSellNumber() {
		return handwordSellNumber;
	}

	public void setHandwordSellNumber(Integer handwordSellNumber) {
		this.handwordSellNumber = handwordSellNumber;
	}

	public Integer getHandwordSellEpId() {
		return handwordSellEpId;
	}

	public void setHandwordSellEpId(Integer handwordSellEpId) {
		this.handwordSellEpId = handwordSellEpId;
	}

	public Boolean getIsdel() {
		return isdel;
	}

	public void setIsdel(Boolean isdel) {
		this.isdel = isdel;
	}

	
	public String getSuodingdanhao() {
		return suodingdanhao;
	}

	public void setSuodingdanhao(String suodingdanhao) {
		this.suodingdanhao = suodingdanhao;
	}

	public String getCgNumber() {
		return cgNumber;
	}

	public void setCgNumber(String cgNumber) {
		this.cgNumber = cgNumber;
	}

	public String getChangePrint() {
		return changePrint;
	}

	public void setChangePrint(String changePrint) {
		this.changePrint = changePrint;
	}

	public String getGoodsStoreWarehouse() {
		return goodsStoreWarehouse;
	}

	public void setGoodsStoreWarehouse(String goodsStoreWarehouse) {
		this.goodsStoreWarehouse = goodsStoreWarehouse;
	}

	public String getGoodsStorehouseName() {
		return goodsStorehouseName;
	}

	public void setGoodsStorehouseName(String goodsStorehouseName) {
		this.goodsStorehouseName = goodsStorehouseName;
	}

	public String getGoodsStorePosition() {
		return goodsStorePosition;
	}

	public void setGoodsStorePosition(String goodsStorePosition) {
		this.goodsStorePosition = goodsStorePosition;
	}

	public Integer getProcardId() {
		return procardId;
	}

	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}



	public String getDemanddept() {
		return demanddept;
	}



	public void setDemanddept(String demanddept) {
		this.demanddept = demanddept;
	}



	
	

}
