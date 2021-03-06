package com.task.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.task.entity.sop.ProcardPro;
import com.task.entity.sop.ProcessInfor;

/**
 * 产品核价信息表(ta_sop_tj_ProductProcess)
 * 
 * @author 贾辉辉
 * 
 */
public class ProductProcess implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; // 序号
	private SpareParts spareParts;// 零件号
	private TaSopGongwei sopGongwei;// 工位信息
	// private ProductPrice productPrice;//产品表
	private String processNo; // 工序号*
	private String processName; // 工序*
	private String gongwei; // 工位*
	private String shebeiNo; // 设备编号*
	private Float danjianJIEPAI;// 单件节拍
	private Float fuheJIEPAI;// 复合利用节拍
	private String fuheProcessNo;// 复合工序
	private Float realJIEPAI;// 实际人工节拍****重点
	private String more;// 备注
	private Float biaozhunJIEPAI;// 标准节拍
	private String improvementTarget;// 改进目标
	private Integer operationNum;// 操作人数**
	private String operatorCode;// 操作人工号***
	private String operatorName;// 操作人姓名***
	private Double processMomey;// 工序人工费
	/*
	 * 操作指数
	 */
	private Float OPtechnologyRate;// 技能指数
	private Float OPCouldReplaceRate;// 可替换人数
	private Float OPfuheRate;// 负荷指数
	private Float OPcaozuojiepai;// 操作人工节拍 新增
	private Float OPshebeijiepai;// 操作设备节拍 新增
	private Float OPnoReplaceRate;// 操作不可替换系数 新增
	private Float OPzonghezhishu;// 操作综合指数 新增
	private Float OPzongheqiangdu;// 操作综合强度 新增
	private Float OPjiaofu;// 交付数量
	/*
	 * 工装调试指数 准备过程
	 */
	private Float GZtechnologyRate;// 技术指数
	private Float GZCouldReplaceRate;// 可替换人数
	private Float GZfuheRate;// 负荷指数
	private Float GZzhunbeijiepai;// 准备过程人工节拍 新增
	private Float GZzhunbeicishu;// 准备次数 新增
	private Float GZnoReplaceRate;// 准备过程不可替换系数 新增
	private Float GZzonghezhishu;// 准备过程综合指数 新增
	private Float GZzongheqiangdu;// 综合强度 新增
	private Float picizonge;// 工序总额 新增,操作总额+准备总额
	private String videoFile; // 录像链接
	private String processMore; // 备注

	// 对应关系
	// 同一工序个人的综合系数
	// private Set<ProcardOneRate>procardOneRates=new HashSet<ProcardOneRate>();
	// 对应生产领取的流水卡片信息
	private Set<ProcardPro> procardPros = new HashSet<ProcardPro>();
	// 对应工艺流水卡片模板的工序信息
	private Set<ProcessInfor> processInfors = new HashSet<ProcessInfor>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SpareParts getSpareParts() {
		return spareParts;
	}

	public void setSpareParts(SpareParts spareParts) {
		this.spareParts = spareParts;
	}

	/*
	 * public ProductPrice getProductPrice() { return productPrice; } public
	 * void setProductPrice(ProductPrice productPrice) { this.productPrice =
	 * productPrice; }
	 */
	public TaSopGongwei getSopGongwei() {
		return sopGongwei;
	}

	public void setSopGongwei(TaSopGongwei sopGongwei) {
		this.sopGongwei = sopGongwei;
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

	public String getGongwei() {
		return gongwei;
	}

	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}

	public String getShebeiNo() {
		return shebeiNo;
	}

	public void setShebeiNo(String shebeiNo) {
		this.shebeiNo = shebeiNo;
	}

	public Float getDanjianJIEPAI() {
		return danjianJIEPAI;
	}

	public void setDanjianJIEPAI(Float danjianJIEPAI) {
		this.danjianJIEPAI = danjianJIEPAI;
	}

	public Float getFuheJIEPAI() {
		return fuheJIEPAI;
	}

	public void setFuheJIEPAI(Float fuheJIEPAI) {
		this.fuheJIEPAI = fuheJIEPAI;
	}

	public String getFuheProcessNo() {
		return fuheProcessNo;
	}

	public void setFuheProcessNo(String fuheProcessNo) {
		this.fuheProcessNo = fuheProcessNo;
	}

	public Float getRealJIEPAI() {
		return realJIEPAI;
	}

	public void setRealJIEPAI(Float realJIEPAI) {
		this.realJIEPAI = realJIEPAI;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public Float getBiaozhunJIEPAI() {
		return biaozhunJIEPAI;
	}

	public void setBiaozhunJIEPAI(Float biaozhunJIEPAI) {
		this.biaozhunJIEPAI = biaozhunJIEPAI;
	}

	public String getImprovementTarget() {
		return improvementTarget;
	}

	public void setImprovementTarget(String improvementTarget) {
		this.improvementTarget = improvementTarget;
	}

	public Integer getOperationNum() {
		return operationNum;
	}

	public void setOperationNum(Integer operationNum) {
		this.operationNum = operationNum;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Double getProcessMomey() {
		return processMomey;
	}

	public void setProcessMomey(Double processMomey) {
		this.processMomey = processMomey;
	}

	public Float getOPtechnologyRate() {
		return OPtechnologyRate;
	}

	public void setOPtechnologyRate(Float oPtechnologyRate) {
		OPtechnologyRate = oPtechnologyRate;
	}

	public Float getOPCouldReplaceRate() {
		return OPCouldReplaceRate;
	}

	public void setOPCouldReplaceRate(Float oPCouldReplaceRate) {
		OPCouldReplaceRate = oPCouldReplaceRate;
	}

	public Float getOPfuheRate() {
		return OPfuheRate;
	}

	public void setOPfuheRate(Float oPfuheRate) {
		OPfuheRate = oPfuheRate;
	}

	public Float getGZtechnologyRate() {
		return GZtechnologyRate;
	}

	public void setGZtechnologyRate(Float gZtechnologyRate) {
		GZtechnologyRate = gZtechnologyRate;
	}

	public Float getGZCouldReplaceRate() {
		return GZCouldReplaceRate;
	}

	public void setGZCouldReplaceRate(Float gZCouldReplaceRate) {
		GZCouldReplaceRate = gZCouldReplaceRate;
	}

	public Float getGZfuheRate() {
		return GZfuheRate;
	}

	public void setGZfuheRate(Float gZfuheRate) {
		GZfuheRate = gZfuheRate;
	}

	public String getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}

	public String getProcessMore() {
		return processMore;
	}

	public void setProcessMore(String processMore) {
		this.processMore = processMore;
	}

	public Float getOPcaozuojiepai() {
		return OPcaozuojiepai;
	}

	public void setOPcaozuojiepai(Float oPcaozuojiepai) {
		OPcaozuojiepai = oPcaozuojiepai;
	}

	public Float getOPshebeijiepai() {
		return OPshebeijiepai;
	}

	public void setOPshebeijiepai(Float oPshebeijiepai) {
		OPshebeijiepai = oPshebeijiepai;
	}

	public Float getOPnoReplaceRate() {
		return OPnoReplaceRate;
	}

	public void setOPnoReplaceRate(Float oPnoReplaceRate) {
		OPnoReplaceRate = oPnoReplaceRate;
	}

	public Float getOPzonghezhishu() {
		return OPzonghezhishu;
	}

	public void setOPzonghezhishu(Float oPzonghezhishu) {
		OPzonghezhishu = oPzonghezhishu;
	}

	public Float getOPzongheqiangdu() {
		return OPzongheqiangdu;
	}

	public void setOPzongheqiangdu(Float oPzongheqiangdu) {
		OPzongheqiangdu = oPzongheqiangdu;
	}

	public Float getGZzhunbeijiepai() {
		return GZzhunbeijiepai;
	}

	public void setGZzhunbeijiepai(Float gZzhunbeijiepai) {
		GZzhunbeijiepai = gZzhunbeijiepai;
	}

	public Float getGZzhunbeicishu() {
		return GZzhunbeicishu;
	}

	public void setGZzhunbeicishu(Float gZzhunbeicishu) {
		GZzhunbeicishu = gZzhunbeicishu;
	}

	public Float getGZnoReplaceRate() {
		return GZnoReplaceRate;
	}

	public void setGZnoReplaceRate(Float gZnoReplaceRate) {
		GZnoReplaceRate = gZnoReplaceRate;
	}

	public Float getGZzonghezhishu() {
		return GZzonghezhishu;
	}

	public void setGZzonghezhishu(Float gZzonghezhishu) {
		GZzonghezhishu = gZzonghezhishu;
	}

	public Float getGZzongheqiangdu() {
		return GZzongheqiangdu;
	}

	public void setGZzongheqiangdu(Float gZzongheqiangdu) {
		GZzongheqiangdu = gZzongheqiangdu;
	}

	public Float getPicizonge() {
		return picizonge;
	}

	public void setPicizonge(Float picizonge) {
		this.picizonge = picizonge;
	}

	public Float getOPjiaofu() {
		return OPjiaofu;
	}

	public void setOPjiaofu(Float oPjiaofu) {
		OPjiaofu = oPjiaofu;
	}

	public Set<ProcardPro> getProcardPros() {
		return procardPros;
	}

	public void setProcardPros(Set<ProcardPro> procardPros) {
		this.procardPros = procardPros;
	}

	public Set<ProcessInfor> getProcessInfors() {
		return processInfors;
	}

	public void setProcessInfors(Set<ProcessInfor> processInfors) {
		this.processInfors = processInfors;
	}

}
