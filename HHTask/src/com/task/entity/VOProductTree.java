package com.task.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.support.DaoSupport;

public class VOProductTree implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** ID */
	private int id;
	/** 件号 */
	private String pieceNum;
	/** 名字 */
	private String name;
	/** 日产量 */
	private Double dayOutput;
	/** 型别 */
	private String productType;
	/** 车型 */
	private String carType;
	/** 提奖单价 */
	private Double oilPaymentPrice;
	/** 零件总节拍 */
	private Double totalBeat;
	/** 工位号 */
	private String StationNum;
	/** 设备号 */
	private String equipmentNum;
	/** 设备节拍 */
	private Double OPEquipmentBeat;
	/** 设备折旧 */
	private Double OPEquipmentZhejiu;
	/** 人工节拍 */
	private Double OPLabourBeat;
	/** 人工节拍 */
	private Double PRLabourBeat;
	/** 准备次数 */
	private Double PRPrepareIndex;
	/** 工号 */
	private String jobNum;
	/** 操作者 */
	private String handlers;
	/** 工序单价 */
	private Double processUnitPrice;
	/** 上级节点ID */
	private Integer _parentId;
	/** 集合 */
	private List<VOProductTree> children = new ArrayList<VOProductTree>();
	/** 真实ID */
	private Integer realId;
	/** 类型 */
	private String type;
	/** 总额 */
	private Double sumMoney;
	/** 单价 */
	private Double unitPrice;
	/** 对应数量(权值) **/
	private Float corrCount;
	/** 卡片类型 **/
	private String procardStyle;
	/** 材质 **/
	private String caizhi;
	/** 原材料名称 **/
	private String yuanName;
	/** 编号 **/
	private String number;
	/** 数量 **/
	private Float maxCount;
	/** 工艺状态 **/
	private String bzStatus;
	private String trademark;// 牌号(件号)
	private String specification;// 规格
	private String luhao;// // 炉号
	private Float allJiepai;// 总节拍
	private String productStyle;// 生产类型
	private String processStatus;// 是否并行
	private Integer processNO;// 工序号;
	private String gdJiepai;// 是否固定节拍
	private String isSpecial;// 是否特殊工序;（特殊,普通,打回 默认普通）
	private Float capacity;// 产能
	private Float capacitySurplus;// 产能盈余
	private Float capacityRatio;// 产能比
	private Float alldeliveryDuration;// 延误时长
	private Integer deliveryPeriod;// 配送周期
	private Float deliveryAmount;// 送货量
	private Float proSingleDuration;// 总成单班生产时长
	private String unit;// 单位(自制)
	private String tuhao;// 图号
	private String biaochu;// 表处
	private String clType;// 材料类型
	private String banBenNumber;// 版本号
	private String loadMarkId;// 初始成品件号
	private String firstDateTime;// 开始时间
	private String endDateTime;// 结束时间
	private String jymes;// 检验信息
	private String wwmes;// 外委信息
	private String kgliao;//供料属性

	public VOProductTree(int id, String name, Float allJiepai,
			String productStyle, String processStatus, Float capacity,
			Float capacitySurplus, Float capacityRatio,
			Float alldeliveryDuration, Integer deliveryPeriod,
			Float deliveryAmount, Float proSingleDuration) {
		super();
		this.id = id;
		this.name = name;
		this.allJiepai = allJiepai;
		this.productStyle = productStyle;
		this.processStatus = processStatus;
		this.capacity = capacity;
		this.capacitySurplus = capacitySurplus;
		this.capacityRatio = capacityRatio;
		this.alldeliveryDuration = alldeliveryDuration;
		this.deliveryPeriod = deliveryPeriod;
		this.deliveryAmount = deliveryAmount;
		this.proSingleDuration = proSingleDuration;
	}

	public VOProductTree() {
		super();
	}

	/**
	 * 成品信息
	 */
	public VOProductTree(int id, String pieceNum, String name,
			Double dayOutput, String productType, String carType,
			Double oilPaymentPrice, Integer realId) {
		this.id = id;
		this.pieceNum = pieceNum;
		this.name = name;
		this.dayOutput = dayOutput;
		this.productType = productType;
		this.carType = carType;
		this.oilPaymentPrice = oilPaymentPrice;
		this.realId = realId;
	}

	/**
	 * 零件
	 */
	public VOProductTree(int id, String pieceNum, String name,
			Double totalBeat, Integer _parentId, Integer realId) {
		this.id = id;
		this.pieceNum = pieceNum;
		this.name = name;
		this.totalBeat = totalBeat;
		this._parentId = _parentId;
		this.realId = realId;

	}

	/**
	 * 零件1
	 */
	public VOProductTree(int id, String pieceNum, String name,
			String procardStyle, String caizhi, String yuanName, String number,
			Float maxCount, String trademark, String luhao,
			String specification, Float corrCount, String unit, String tuhao,
			String biaochu, String clType, String banBenNumber,
			String loadMarkId,String kgliao) {
		this.id = id;// id
		this.pieceNum = pieceNum;// 件号
		this.name = name;// 名称
		this.caizhi = caizhi;// 材质
		this.yuanName = yuanName;// 原材料名称
		this.number = number;// 编号
		this.procardStyle = procardStyle;// 物料属性
		this.maxCount = maxCount;// 权值
		this.trademark = trademark;// 牌号
		this.luhao = luhao;// 炉号
		this.specification = specification;// 规格
		this.corrCount = corrCount;// 权值
		this.unit = unit;
		this.tuhao = tuhao;
		this.biaochu = biaochu;
		this.clType = clType;
		this.banBenNumber = banBenNumber;
		this.loadMarkId = loadMarkId;
		this.kgliao =kgliao;

	}

	/**
	 * 工序1
	 */
	public VOProductTree(int id, String name, Float allJiepai,
			String processStatus, String productStyle, Integer processNO) {
		this.id = id;
		this.name = name;
		this.allJiepai = allJiepai;// 总节拍
		this.processStatus = processStatus;// 是否并行
		this.productStyle = productStyle;// 物料属性
		this.processNO = processNO;// 工序号

	}

	/**
	 * 工序
	 */
	public VOProductTree(int id, String name, String stationNum,
			String equipmentNum, Double oPLabourBeat, Double oPEquipmentBeat,
			Double pRLabourBeat, Double pRPrepareIndex, String handlers,
			Double processUnitPrice, Integer _parentId, Integer realId,
			String type, Double sumMoney, Double unitPrice, String jobNum) {
		this.id = id;
		this.name = name;
		StationNum = stationNum;
		this.equipmentNum = equipmentNum;
		OPLabourBeat = oPLabourBeat;
		OPEquipmentBeat = oPEquipmentBeat;
		PRLabourBeat = pRLabourBeat;
		PRPrepareIndex = pRPrepareIndex;
		this.handlers = handlers;
		this.processUnitPrice = processUnitPrice;
		this._parentId = _parentId;
		this.realId = realId;
		this.type = type;
		this.sumMoney = sumMoney;
		this.unitPrice = unitPrice;
		this.jobNum = jobNum;
	}

	/**
	 * 工序
	 */
	public VOProductTree(int id, String name, String stationNum,
			String equipmentNum, Double oPLabourBeat, Double oPEquipmentBeat,
			Double pRLabourBeat, Double pRPrepareIndex, String handlers,
			Double processUnitPrice, Integer _parentId, Integer realId,
			String type, Double sumMoney, Double unitPrice, String jobNum,
			String firstDateTime, String endDateTime, String jymes, String wwmes) {
		this.id = id;
		this.name = name;
		StationNum = stationNum;
		this.equipmentNum = equipmentNum;
		OPLabourBeat = oPLabourBeat;
		OPEquipmentBeat = oPEquipmentBeat;
		PRLabourBeat = pRLabourBeat;
		PRPrepareIndex = pRPrepareIndex;
		this.handlers = handlers;
		this.processUnitPrice = processUnitPrice;
		this._parentId = _parentId;
		this.realId = realId;
		this.type = type;
		this.sumMoney = sumMoney;
		this.unitPrice = unitPrice;
		this.jobNum = jobNum;
		this.firstDateTime = firstDateTime;
		this.endDateTime = endDateTime;
		this.jymes = jymes;
		this.wwmes = wwmes;
	}

	/**
	 * 工序
	 */
	public VOProductTree(int id, String name, String stationNum,
			String equipmentNum, Double oPLabourBeat, Double oPEquipmentBeat,
			Double pRLabourBeat, Double pRPrepareIndex, String handlers,
			Double processUnitPrice, Integer _parentId, Integer realId,
			String type, Double sumMoney, Double unitPrice, String jobNum,
			Double oPEquipmentZhejiu) {
		this.id = id;
		this.name = name;
		StationNum = stationNum;
		this.equipmentNum = equipmentNum;
		OPLabourBeat = oPLabourBeat;
		OPEquipmentBeat = oPEquipmentBeat;
		PRLabourBeat = pRLabourBeat;
		PRPrepareIndex = pRPrepareIndex;
		this.handlers = handlers;
		this.processUnitPrice = processUnitPrice;
		this._parentId = _parentId;
		this.realId = realId;
		this.type = type;
		this.sumMoney = sumMoney;
		this.unitPrice = unitPrice;
		this.jobNum = jobNum;
		this.OPEquipmentZhejiu = oPEquipmentZhejiu;
	}

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

	public String getPieceNum() {
		return pieceNum;
	}

	public void setPieceNum(String pieceNum) {
		this.pieceNum = pieceNum;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public Double getOilPaymentPrice() {
		return oilPaymentPrice;
	}

	public void setOilPaymentPrice(Double oilPaymentPrice) {
		this.oilPaymentPrice = oilPaymentPrice;
	}

	public Double getTotalBeat() {
		return totalBeat;
	}

	public void setTotalBeat(Double totalBeat) {
		this.totalBeat = totalBeat;
	}

	public Integer get_parentId() {
		return _parentId;
	}

	public void set_parentId(Integer parentId) {
		_parentId = parentId;
	}

	public String getStationNum() {
		return StationNum;
	}

	public void setStationNum(String stationNum) {
		StationNum = stationNum;
	}

	public String getEquipmentNum() {
		return equipmentNum;
	}

	public void setEquipmentNum(String equipmentNum) {
		this.equipmentNum = equipmentNum;
	}

	public Double getOPLabourBeat() {
		return OPLabourBeat;
	}

	public void setOPLabourBeat(Double oPLabourBeat) {
		OPLabourBeat = oPLabourBeat;
	}

	public Double getOPEquipmentBeat() {
		return OPEquipmentBeat;
	}

	public void setOPEquipmentBeat(Double oPEquipmentBeat) {
		OPEquipmentBeat = oPEquipmentBeat;
	}

	public Double getPRLabourBeat() {
		return PRLabourBeat;
	}

	public void setPRLabourBeat(Double pRLabourBeat) {
		PRLabourBeat = pRLabourBeat;
	}

	public Double getPRPrepareIndex() {
		return PRPrepareIndex;
	}

	public void setPRPrepareIndex(Double pRPrepareIndex) {
		PRPrepareIndex = pRPrepareIndex;
	}

	public String getHandlers() {
		return handlers;
	}

	public void setHandlers(String handlers) {
		this.handlers = handlers;
	}

	public Double getProcessUnitPrice() {
		return processUnitPrice;
	}

	public void setProcessUnitPrice(Double processUnitPrice) {
		this.processUnitPrice = processUnitPrice;
	}

	public Double getDayOutput() {
		return dayOutput;
	}

	public void setDayOutput(Double dayOutput) {
		this.dayOutput = dayOutput;
	}

	public List<VOProductTree> getChildren() {
		return children;
	}

	public void setChildren(List<VOProductTree> children) {
		this.children = children;
	}

	public Integer getRealId() {
		return realId;
	}

	public void setRealId(Integer realId) {
		this.realId = realId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(Double sumMoney) {
		this.sumMoney = sumMoney;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getJobNum() {
		return jobNum;
	}

	public void setJobNum(String jobNum) {
		this.jobNum = jobNum;
	}

	public Double getOPEquipmentZhejiu() {
		return OPEquipmentZhejiu;
	}

	public void setOPEquipmentZhejiu(Double oPEquipmentZhejiu) {
		OPEquipmentZhejiu = oPEquipmentZhejiu;
	}

	public Float getAllJiepai() {
		return allJiepai;
	}

	public void setAllJiepai(Float allJiepai) {
		this.allJiepai = allJiepai;
	}

	public String getProductStyle() {
		return productStyle;
	}

	public void setProductStyle(String productStyle) {
		this.productStyle = productStyle;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public Float getCapacity() {
		return capacity;
	}

	public void setCapacity(Float capacity) {
		this.capacity = capacity;
	}

	public Float getCapacitySurplus() {
		return capacitySurplus;
	}

	public void setCapacitySurplus(Float capacitySurplus) {
		this.capacitySurplus = capacitySurplus;
	}

	public Float getCapacityRatio() {
		return capacityRatio;
	}

	public void setCapacityRatio(Float capacityRatio) {
		this.capacityRatio = capacityRatio;
	}

	public Float getAlldeliveryDuration() {
		return alldeliveryDuration;
	}

	public void setAlldeliveryDuration(Float alldeliveryDuration) {
		this.alldeliveryDuration = alldeliveryDuration;
	}

	public Integer getDeliveryPeriod() {
		return deliveryPeriod;
	}

	public void setDeliveryPeriod(Integer deliveryPeriod) {
		this.deliveryPeriod = deliveryPeriod;
	}

	public Float getDeliveryAmount() {
		return deliveryAmount;
	}

	public void setDeliveryAmount(Float deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}

	public Float getProSingleDuration() {
		return proSingleDuration;
	}

	public void setProSingleDuration(Float proSingleDuration) {
		this.proSingleDuration = proSingleDuration;
	}

	public Float getCorrCount() {
		return corrCount;
	}

	public void setCorrCount(Float corrCount) {
		this.corrCount = corrCount;
	}

	public String getProcardStyle() {
		return procardStyle;
	}

	public void setProcardStyle(String procardStyle) {
		this.procardStyle = procardStyle;
	}

	public String getCaizhi() {
		return caizhi;
	}

	public void setCaizhi(String caizhi) {
		this.caizhi = caizhi;
	}

	public String getYuanName() {
		return yuanName;
	}

	public void setYuanName(String yuanName) {
		this.yuanName = yuanName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getProcessNO() {
		return processNO;
	}

	public void setProcessNO(Integer processNO) {
		this.processNO = processNO;
	}

	public String getGdJiepai() {
		return gdJiepai;
	}

	public void setGdJiepai(String gdJiepai) {
		this.gdJiepai = gdJiepai;
	}

	public String getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(String isSpecial) {
		this.isSpecial = isSpecial;
	}

	public Float getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Float maxCount) {
		this.maxCount = maxCount;
	}

	public String getBzStatus() {
		return bzStatus;
	}

	public void setBzStatus(String bzStatus) {
		this.bzStatus = bzStatus;
	}

	public String getTrademark() {
		return trademark;
	}

	public void setTrademark(String trademark) {
		this.trademark = trademark;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getLuhao() {
		return luhao;
	}

	public void setLuhao(String luhao) {
		this.luhao = luhao;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getTuhao() {
		return tuhao;
	}

	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}

	public String getBiaochu() {
		return biaochu;
	}

	public void setBiaochu(String biaochu) {
		this.biaochu = biaochu;
	}

	public String getClType() {
		return clType;
	}

	public void setClType(String clType) {
		this.clType = clType;
	}

	public String getBanBenNumber() {
		return banBenNumber;
	}

	public void setBanBenNumber(String banBenNumber) {
		this.banBenNumber = banBenNumber;
	}

	public String getLoadMarkId() {
		return loadMarkId;
	}

	public void setLoadMarkId(String loadMarkId) {
		this.loadMarkId = loadMarkId;
	}

	public String getFirstDateTime() {
		return firstDateTime;
	}

	public void setFirstDateTime(String firstDateTime) {
		this.firstDateTime = firstDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getJymes() {
		return jymes;
	}

	public void setJymes(String jymes) {
		this.jymes = jymes;
	}

	public String getWwmes() {
		return wwmes;
	}

	public void setWwmes(String wwmes) {
		this.wwmes = wwmes;
	}

	public String getKgliao() {
		return kgliao;
	}

	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

}