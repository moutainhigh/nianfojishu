package com.task.entity.sop.qd;

import java.io.Serializable;

/**
 * 品质标识贴（ta_sop_qd_LogoStickers）
 * 
 * @author 贾辉辉
 * 
 */
public class LogoStickers  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private String number;// 编号
	private String stickStyle;// 标识类型（报废品/待处理品/首检样品）
	private String markId;// 件号
	private String ywMarkId;// 业务件号
	private String lotId;// 批次
	private String partsName;// 零件名称
	private String processNO;// 工序号
	private String processName;//工序名称
	private String operator;// 操作者
	private String code;// 操作者工号
	private String billDate;// 时间
	private String examinerCode;// 检验员工号
	private String examinerName;// 检验员姓名
	private String examinerDate;// 检验时间
	private String lastUpdateTime;// 最后一次更新时间
	private String treatAdvice;// 处理意见
	private float count;// 数量
	private String more;// 备注(报废原因)
	private String demandExamContent;// 要求检查内容
	private String realExamContent;// 实际检查内容记录
	private String isPrint;// 是否已经打印
	private String workingGroup;// 班组编码
	private String isHege;//是否合格（合格,不合格）
	private String clMarkId;//材料件号
	private String clSelfCard;//材料批次
	private String wgType;//材料材质类型
	// 报废新添
	private String status;// 状态(报废/已发卡/跟踪/完成/入库)
	private Integer oldProcardId;// 老流水单ID
	private Integer procardId;// 流水单ID
	private String isGys;//是否为供应商(null或者no表示不是数字表示供应商id为表ZhUser的id)
	private String gongwei;//工位
	private String machineNo;//设备编号（显示使用）
	
	private Integer buhegeId;//缺陷id
	private String  bhgcode;// 缺陷代码(不合格)
	private String type;// 缺陷描述(不合格)
	
	private Integer sbId;//设变Id
	private String sbNumber;//设变单号
	private String sbmsg;//设变信息
	
	/*** 量具 ***/
	private String measuringMatetag;// 名称
	private String measuring_no; // 本厂编号
	
	//
	private String leixing;//报废类型
	public String getLeixing() {
		return leixing;
	}

	public void setLeixing(String leixing) {
		this.leixing = leixing;
	}

	private String responsible;//责任人
	
	
	//
	private String gongyinshang;
	
	
	public String getGongyinshang() {
		return gongyinshang;
	}

	public void setGongyinshang(String gongyinshang) {
		this.gongyinshang = gongyinshang;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStickStyle() {
		return stickStyle;
	}

	public void setStickStyle(String stickStyle) {
		this.stickStyle = stickStyle;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getPartsName() {
		return partsName;
	}

	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}

	public String getProcessNO() {
		return processNO;
	}

	public void setProcessNO(String processNO) {
		this.processNO = processNO;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExaminerCode() {
		return examinerCode;
	}

	public void setExaminerCode(String examinerCode) {
		this.examinerCode = examinerCode;
	}

	public String getExaminerName() {
		return examinerName;
	}

	public void setExaminerName(String examinerName) {
		this.examinerName = examinerName;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getTreatAdvice() {
		return treatAdvice;
	}

	public void setTreatAdvice(String treatAdvice) {
		this.treatAdvice = treatAdvice;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
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

	public String getIsPrint() {
		return isPrint;
	}

	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}

	public String getWorkingGroup() {
		return workingGroup;
	}

	public void setWorkingGroup(String workingGroup) {
		this.workingGroup = workingGroup;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getOldProcardId() {
		return oldProcardId;
	}

	public void setOldProcardId(Integer oldProcardId) {
		this.oldProcardId = oldProcardId;
	}

	public Integer getProcardId() {
		return procardId;
	}

	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getIsGys() {
		return isGys;
	}

	public void setIsGys(String isGys) {
		this.isGys = isGys;
	}

	public String getGongwei() {
		return gongwei;
	}

	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}

	public String getMachineNo() {
		return machineNo;
	}

	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	public String getIsHege() {
		return isHege;
	}

	public void setIsHege(String isHege) {
		this.isHege = isHege;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getYwMarkId() {
		return ywMarkId;
	}

	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}

	public String getExaminerDate() {
		return examinerDate;
	}

	public void setExaminerDate(String examinerDate) {
		this.examinerDate = examinerDate;
	}

	public String getClMarkId() {
		return clMarkId;
	}

	public void setClMarkId(String clMarkId) {
		this.clMarkId = clMarkId;
	}

	public String getClSelfCard() {
		return clSelfCard;
	}

	public void setClSelfCard(String clSelfCard) {
		this.clSelfCard = clSelfCard;
	}

	public String getWgType() {
		return wgType;
	}

	public void setWgType(String wgType) {
		this.wgType = wgType;
	}

	public Integer getBuhegeId() {
		return buhegeId;
	}

	public void setBuhegeId(Integer buhegeId) {
		this.buhegeId = buhegeId;
	}

	public String getBhgcode() {
		return bhgcode;
	}

	public void setBhgcode(String bhgcode) {
		this.bhgcode = bhgcode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSbId() {
		return sbId;
	}

	public void setSbId(Integer sbId) {
		this.sbId = sbId;
	}

	public String getSbNumber() {
		return sbNumber;
	}

	public void setSbNumber(String sbNumber) {
		this.sbNumber = sbNumber;
	}

	public String getSbmsg() {
		return sbmsg;
	}

	public void setSbmsg(String sbmsg) {
		this.sbmsg = sbmsg;
	}

	public String getMeasuringMatetag() {
		return measuringMatetag;
	}

	public void setMeasuringMatetag(String measuringMatetag) {
		this.measuringMatetag = measuringMatetag;
	}

	public String getMeasuring_no() {
		return measuring_no;
	}

	public void setMeasuring_no(String measuringNo) {
		measuring_no = measuringNo;
	}
	

}
