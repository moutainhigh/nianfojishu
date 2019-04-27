package com.task.entity.sop;
/****
 * 送货单明细
 * 
 * @author 刘培
 * @表名 ta_sop_w_WaigouDeliveryDetail
 * 
 * 当有转换数量时 	private Float jyNumber;//检验数量
				private Float jyhgNumber;//检验合格数量
				private Float jybhgNumber;//检验不合格数量
以上几个数量都为转换过的数量。
	private Float hgNumber;// 合格数量
	private Float ycNumber;// 已存入数量
	private Float bhgNumber;// 不合格数量
以上几个数量还为原来的数量(公斤数或米数)
 * 
 */
public class WaigouDeliveryDetail implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String cgOrderNum;// 采购订单编号
	private String markId;// 件号
	private String ywmarkId;//业务件号 
	private String kgliao;// //供料属性（自购、指定、客供）
	private String banben;// 版本
	private Integer banci;// 版次
	private String tuhao;//图号
	private String proName;// 零件名称
	private String processNo;// 工序号 
	private String processName;//工序名称
	private String specification;// 规格
	private String unit;// 单位
	private Double hsPrice;// 含税单价
	private Double price;// 不含税单价
	private Double taxprice; // 税率
	private Integer priceId;// 价格id
	private Integer gysId;// 供应商id
	private String gysName;// 供应商名称
	private String gysjc;//供应商简称
	private String type;// 计划类型(外购/外委/模具)
	private String wwType;//外委类型（工序外委,包工包料,零件外购）
	private Float shNumber;// 送货数量
	private Float qrNumber;// 确认数量
	private String qrWeizhi;// 确认位置
	private String qrUsersName;//确认人(签收人);
	private Integer qrUsersId;//确认人(签收人Id);
	private Float hgNumber;// 合格数量
	private Float ycNumber;// 已存入数量
	private Float bhgNumber;// 不合格数量
	private Float lingliaoNum;//领料数量
	private Float yrukuNum;//已入库数量;
	private Float ctn;// 箱单数
	private Float oneCtnNum;// 单箱数量
	//检验信息
	private String examineLot;//检验批次(仓库确认后生成检验批次:)
	private Integer userId;//检验员id
	private String userCode;//检验元工号
	private String userName;//检验员名称
	private String checkTime;//检验时间
	private String demandExamContent;// 要求检查内容
	private String realExamContent;// 实际检查内容记录
	private String isHege;//是否合格（是，否，null表示没有检验）
	private String changqu;//仓区
	private String kuwei;//库位
	private String caigouUserName;//采购员
	private Float jyNumber;//检验数量
	private Float jyhgNumber;//检验合格数量
	private Float jybhgNumber;//检验不合格数量
	private Float kcCount;//库存数量（页面显示）
	
	//外委代领展示
	private String rootMarkId;//总成件号（页面传值使用不存入数据库）
	private String rootSlfCard;//总成批次（页面传值使用不存入数据库）
	private String selfCard;// 生产批次
	private String neiorderNum;//内部订单号（页面传值使用不存入数据库）
	private Float filnalCount;//需求数量（页面传值使用不存入数据库）
	private String wgType;//物料类别 
	

	/******** 供应商相关 *******/
	private String addTime;// 添加时间
	private String printTime;// 打印时间
	private String jinmenTime;// 进门时间
	private String querenTime;// 库房签收确认时间
	private String jianyanTime;// 检验时间
	private String rukuTime;// 入库时间

	private String remarks;// 备注

	private String status;// 状态(待打印、送货中、待存柜、待检验、检验中、待入库、入库)
	private String isprint;//是否打印;(YES/NO)
	/*	1.默认情况下不查作废送货明细，不导出作废送货明细。
		2.只有该送货单明细全部退货时，该送货单明细变为作废。
		3.所对应的采购单明细状态变为生产中，剩余送货数量加上该送货单明细的数量。
	 * 
	 */
	private String datastatus;//（作废）//
	
	private Integer waigouPlanId;// 对应采购明细（多对一(WaigouPlan)）

	private WaigouDelivery waigouDelivery;// 送货单（多对一）
	private String classNames;//前端显示
	private String 	jyuserName;//检验人姓名;
	private Integer wbdId;//绑定中间表ID_LC(判断是否有中间关系)
	private String thStatus;//退货状态（待领,已领） (禁用)
	private Float zhuanhuanNum;//转换数量;
	private String zhuanhuanUnit;//转换单位
	private Float hegeRate;//合格率
	private Float quejianRate;//缺件率
	private String againcheck;// 挑选、退货、特采、报废
	private String mujuNumber;//开模单号
	
	private Integer avgProductionTakt;//生产节拍（秒）
	private Float avgDeliveryTime;//配送时长 (天)
	private Float allJiepai;// 总节拍
	private Float cgperiod;//配送周期 (天)
	
	// private WaigouPlan waigouPlan;// 采购明细（多对一）
	private Float rangeOfReceipt;//收货浮动范围
	private String printNumber;//打印单号
	private String strs;
	private String proNumber;//项目编号
	private String nextProcessName;//下工序名称(页面传值)
	private Float tuihuoNum;//退货数量（用于在库不良，发生退货使用）
	
	private String sbNumber;//设变单号(页面传值)
	private Integer sbId;//设变Id(页面传值)
	private String sbRemark;//设变备注(页面传值)
	
	private String demanddept;//需求部门
	
	private String jytixing;//传值;
		
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getProNumber() {
		return proNumber;
	}

	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
	}

	public Integer getWbdId() {
		return wbdId;
	}

	public void setWbdId(Integer wbdId) {
		this.wbdId = wbdId;
	}

	public Float getYcNumber() {
		return ycNumber;
	}

	public void setYcNumber(Float ycNumber) {
		this.ycNumber = ycNumber;
	}

	public Float getBhgNumber() {
		return bhgNumber;
	}

	public void setBhgNumber(Float bhgNumber) {
		this.bhgNumber = bhgNumber;
	}

	public String getCgOrderNum() {
		return cgOrderNum;
	}

	public void setCgOrderNum(String cgOrderNum) {
		this.cgOrderNum = cgOrderNum;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getKgliao() {
		return kgliao;
	}

	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

 	public String getBanben() {
		return banben;
	}

	public void setBanben(String banben) {
		this.banben = banben;
	}

	public Integer getBanci() {
		return banci;
	}

	public void setBanci(Integer banci) {
		this.banci = banci;
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

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getHsPrice() {
		return hsPrice;
	}

	public void setHsPrice(Double hsPrice) {
		this.hsPrice = hsPrice;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTaxprice() {
		return taxprice;
	}

	public void setTaxprice(Double taxprice) {
		this.taxprice = taxprice;
	}

	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public Integer getGysId() {
		return gysId;
	}

	public void setGysId(Integer gysId) {
		this.gysId = gysId;
	}

	public String getGysName() {
		return gysName;
	}

	public void setGysName(String gysName) {
		this.gysName = gysName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public Float getQrNumber() {
		return qrNumber;
	}

	public void setQrNumber(Float qrNumber) {
		this.qrNumber = qrNumber;
	}

	public Float getHgNumber() {
		return hgNumber;
	}

	public void setHgNumber(Float hgNumber) {
		this.hgNumber = hgNumber;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getJinmenTime() {
		return jinmenTime;
	}

	public void setJinmenTime(String jinmenTime) {
		this.jinmenTime = jinmenTime;
	}

	public String getQuerenTime() {
		return querenTime;
	}

	public void setQuerenTime(String querenTime) {
		this.querenTime = querenTime;
	}

	public String getJianyanTime() {
		return jianyanTime;
	}

	public void setJianyanTime(String jianyanTime) {
		this.jianyanTime = jianyanTime;
	}

	public String getRukuTime() {
		return rukuTime;
	}

	public void setRukuTime(String rukuTime) {
		this.rukuTime = rukuTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WaigouDelivery getWaigouDelivery() {
		return waigouDelivery;
	}

	public void setWaigouDelivery(WaigouDelivery waigouDelivery) {
		this.waigouDelivery = waigouDelivery;
	}

	public Float getOneCtnNum() {
		return oneCtnNum;
	}

	public void setOneCtnNum(Float oneCtnNum) {
		this.oneCtnNum = oneCtnNum;
	}

	public Float getCtn() {
		return ctn;
	}

	public void setCtn(Float ctn) {
		this.ctn = ctn;
	}

	public Integer getWaigouPlanId() {
		return waigouPlanId;
	}

	public void setWaigouPlanId(Integer waigouPlanId) {
		this.waigouPlanId = waigouPlanId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPrintTime() {
		return printTime;
	}

	public void setPrintTime(String printTime) {
		this.printTime = printTime;
	}

	public String getQrWeizhi() {
		return qrWeizhi;
	}

	public void setQrWeizhi(String qrWeizhi) {
		this.qrWeizhi = qrWeizhi;
	}

	public String getExamineLot() {
		return examineLot;
	}

	public void setExamineLot(String examineLot) {
		this.examineLot = examineLot;
	}

	public String getIsHege() {
		return isHege;
	}

	public void setIsHege(String isHege) {
		this.isHege = isHege;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getDemandExamContent() {
		return demandExamContent;
	}

	public void setDemandExamContent(String demandExamContent) {
		this.demandExamContent = demandExamContent;
	}

	public String getRealExamContent() {
		return realExamContent;
	}

	public void setRealExamContent(String realExamContent) {
		this.realExamContent = realExamContent;
	}

	public String getRootMarkId() {
		return rootMarkId;
	}

	public void setRootMarkId(String rootMarkId) {
		this.rootMarkId = rootMarkId;
	}

	public String getRootSlfCard() {
		return rootSlfCard;
	}

	public void setRootSlfCard(String rootSlfCard) {
		this.rootSlfCard = rootSlfCard;
	}

	public String getSelfCard() {
		return selfCard;
	}

	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}

	public String getWwType() {
		return wwType;
	}

	public void setWwType(String wwType) {
		this.wwType = wwType;
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

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}

	public String getJyuserName() {
		return jyuserName;
	}

	public void setJyuserName(String jyuserName) {
		this.jyuserName = jyuserName;
	}

	public String getWgType() {
		return wgType;
	}

	public void setWgType(String wgType) {
		this.wgType = wgType;
	}

	public String getYwmarkId() {
		return ywmarkId;
	}

	public void setYwmarkId(String ywmarkId) {
		this.ywmarkId = ywmarkId;
	}

	public String getTuhao() {
		return tuhao;
	}

	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}

	public String getChangqu() {
		return changqu;
	}

	public void setChangqu(String changqu) {
		this.changqu = changqu;
	}

	public String getKuwei() {
		return kuwei;
	}

	public void setKuwei(String kuwei) {
		this.kuwei = kuwei;
	}

	public Float getJyNumber() {
		return jyNumber;
	}

	public void setJyNumber(Float jyNumber) {
		this.jyNumber = jyNumber;
	}

	public Float getJyhgNumber() {
		return jyhgNumber;
	}

	public void setJyhgNumber(Float jyhgNumber) {
		this.jyhgNumber = jyhgNumber;
	}

	public Float getJybhgNumber() {
		return jybhgNumber;
	}

	public void setJybhgNumber(Float jybhgNumber) {
		this.jybhgNumber = jybhgNumber;
	}

	public String getThStatus() {
		return thStatus;
	}

	public void setThStatus(String thStatus) {
		this.thStatus = thStatus;
	}

	public String getCaigouUserName() {
		return caigouUserName;
	}

	public void setCaigouUserName(String caigouUserName) {
		this.caigouUserName = caigouUserName;
	}

	public Float getFilnalCount() {
		return filnalCount;
	}

	public void setFilnalCount(Float filnalCount) {
		this.filnalCount = filnalCount;
	}

	public Float getLingliaoNum() {
		return lingliaoNum;
	}

	public void setLingliaoNum(Float lingliaoNum) {
		this.lingliaoNum = lingliaoNum;
	}

	public String getNeiorderNum() {
		return neiorderNum;
	}

	public void setNeiorderNum(String neiorderNum) {
		this.neiorderNum = neiorderNum;
	}

	public Float getYrukuNum() {
		return yrukuNum;
	}

	public void setYrukuNum(Float yrukuNum) {
		this.yrukuNum = yrukuNum;
	}

	
	public String getIsprint() {
		return isprint;
	}

	public void setIsprint(String isprint) {
		this.isprint = isprint;
	}

	public Float getZhuanhuanNum() {
		return zhuanhuanNum;
	}

	public void setZhuanhuanNum(Float zhuanhuanNum) {
		this.zhuanhuanNum = zhuanhuanNum;
	}

	public String getZhuanhuanUnit() {
		return zhuanhuanUnit;
	}

	public void setZhuanhuanUnit(String zhuanhuanUnit) {
		this.zhuanhuanUnit = zhuanhuanUnit;
	}

	public String getQrUsersName() {
		return qrUsersName;
	}

	public void setQrUsersName(String qrUsersName) {
		this.qrUsersName = qrUsersName;
	}

	public Float getHegeRate() {
		return hegeRate;
	}

	public void setHegeRate(Float hegeRate) {
		this.hegeRate = hegeRate;
	}

	public Float getQuejianRate() {
		return quejianRate;
	}

	public void setQuejianRate(Float quejianRate) {
		this.quejianRate = quejianRate;
	}
	public String getAgaincheck() {
		return againcheck;
	}

	public void setAgaincheck(String againcheck) {
		this.againcheck = againcheck;
	}
	public String getGysjc() {
		return gysjc;
	}

	public void setGysjc(String gysjc) {
		this.gysjc = gysjc;
	}

	public String getMujuNumber() {
		return mujuNumber;
	}

	public void setMujuNumber(String mujuNumber) {
		this.mujuNumber = mujuNumber;
	}

	public Integer getAvgProductionTakt() {
		return avgProductionTakt;
	}

	public void setAvgProductionTakt(Integer avgProductionTakt) {
		this.avgProductionTakt = avgProductionTakt;
	}

	public Float getAvgDeliveryTime() {
		return avgDeliveryTime;
	}

	public void setAvgDeliveryTime(Float avgDeliveryTime) {
		this.avgDeliveryTime = avgDeliveryTime;
	}

	public Float getCgperiod() {
		return cgperiod;
	}

	public void setCgperiod(Float cgperiod) {
		this.cgperiod = cgperiod;
	}

	public Float getAllJiepai() {
		return allJiepai;
	}

	public void setAllJiepai(Float allJiepai) {
		this.allJiepai = allJiepai;
	}

	public Float getKcCount() {
		return kcCount;
	}

	public void setKcCount(Float kcCount) {
		this.kcCount = kcCount;
	}

	public String getDatastatus() {
		return datastatus;
	}

	public void setDatastatus(String datastatus) {
		this.datastatus = datastatus;
	}

	public Float getRangeOfReceipt() {
		return rangeOfReceipt;
	}

	public void setRangeOfReceipt(Float rangeOfReceipt) {
		this.rangeOfReceipt = rangeOfReceipt;
	}

	public String getStrs() {
		return strs;
	}

	public void setStrs(String strs) {
		this.strs = strs;
	}

	public Float getShNumber() {
		return shNumber;
	}

	public void setShNumber(Float shNumber) {
		this.shNumber = shNumber;
	}

	public Integer getQrUsersId() {
		return qrUsersId;
	}

	public void setQrUsersId(Integer qrUsersId) {
		this.qrUsersId = qrUsersId;
	}

	public String getNextProcessName() {
		return nextProcessName;
	}

	public void setNextProcessName(String nextProcessName) {
		this.nextProcessName = nextProcessName;
	}

	public String getPrintNumber() {
		return printNumber;
	}

	public void setPrintNumber(String printNumber) {
		this.printNumber = printNumber;
	}

	public Float getTuihuoNum() {
		return tuihuoNum;
	}

	public void setTuihuoNum(Float tuihuoNum) {
		this.tuihuoNum = tuihuoNum;
	}

	public String getSbNumber() {
		return sbNumber;
	}

	public void setSbNumber(String sbNumber) {
		this.sbNumber = sbNumber;
	}

	public Integer getSbId() {
		return sbId;
	}

	public void setSbId(Integer sbId) {
		this.sbId = sbId;
	}

	public String getSbRemark() {
		return sbRemark;
	}

	public void setSbRemark(String sbRemark) {
		this.sbRemark = sbRemark;
	}

	public String getDemanddept() {
		return demanddept;
	}

	public void setDemanddept(String demanddept) {
		this.demanddept = demanddept;
	}

	public String getJytixing() {
		return jytixing;
	}

	public void setJytixing(String jytixing) {
		this.jytixing = jytixing;
	}

	

	
}
