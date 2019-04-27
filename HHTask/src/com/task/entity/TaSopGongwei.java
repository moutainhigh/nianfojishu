package com.task.entity;

import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.entity.android.processpush.OneMachine;
import com.task.entity.android.processpush.Push;
import com.task.entity.sop.ProcessTemplate;

/**
 * 工位信息表 (表名:ta_sop_gongwei)
 * 
 * @author MyEclipse Persistence Tools
 */

public class TaSopGongwei implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id; // 编号
	private String banzu; // 班组
	private String gongweihao;// 工位号
	private String shebeiName;// 设备名称
	private String shebeiCode;// 设备编号
	private Integer shebeiId;// 设备Id
	private String caozuoContent;// 可操作工序内容
	private String caozuoFormat;// 可操作规格
	private Float caozJineng;// 操作技能指数
	private Float caoztihuanrenshu;// 操作可替换人数
	private Float caozFuhe;// 操作负荷指数====================laji
	private String more1;// 备注
	private Float caozMinimumMan;// 操作人数
	private Float gongzhuangJineng;// 工装操作技能指数
	private Float gongzhuangtihuanrenshu;// 工装操作课替换人数
	private Float gongzhuangFuhe;// 工装操作负荷指数
	private String more2;// 备注
	private String shebeiXinghao;// 设备型号
	private String gongzhuangStyle;// 工装类型
	private Float minCaozRenshu;// 最低操作人数
	
	private Set<ProductProcess> productProcess;// gongxu

	private Set<ProcessTemplate> processTemplate;// gongxu
	private Set<Push> pushs;
	private Set<OneMachine> machines;//一体机表

	// Constructors
	/** default constructor */
	public TaSopGongwei() {
	}

	public Integer getId() {
		return id;
	}

	/**
	 * @return the shebeiId
	 */
	public Integer getShebeiId() {
		return shebeiId;
	}

	/**
	 * @param shebeiId the shebeiId to set
	 */
	public void setShebeiId(Integer shebeiId) {
		this.shebeiId = shebeiId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBanzu() {
		return banzu;
	}

	public void setBanzu(String banzu) {
		this.banzu = banzu;
	}

	public String getGongweihao() {
		return gongweihao;
	}

	public void setGongweihao(String gongweihao) {
		this.gongweihao = gongweihao;
	}

	public String getShebeiName() {
		return shebeiName;
	}

	public void setShebeiName(String shebeiName) {
		this.shebeiName = shebeiName;
	}

	public String getShebeiCode() {
		return shebeiCode;
	}

	public void setShebeiCode(String shebeiCode) {
		this.shebeiCode = shebeiCode;
	}

	public String getCaozuoContent() {
		return caozuoContent;
	}

	public void setCaozuoContent(String caozuoContent) {
		this.caozuoContent = caozuoContent;
	}

	public String getCaozuoFormat() {
		return caozuoFormat;
	}

	public void setCaozuoFormat(String caozuoFormat) {
		this.caozuoFormat = caozuoFormat;
	}

	public Float getCaozJineng() {
		return caozJineng;
	}

	public void setCaozJineng(Float caozJineng) {
		this.caozJineng = caozJineng;
	}

	public Float getCaoztihuanrenshu() {
		return caoztihuanrenshu;
	}

	public void setCaoztihuanrenshu(Float caoztihuanrenshu) {
		this.caoztihuanrenshu = caoztihuanrenshu;
	}

	public Float getCaozFuhe() {
		return caozFuhe;
	}

	public void setCaozFuhe(Float caozFuhe) {
		this.caozFuhe = caozFuhe;
	}

	public String getMore1() {
		return more1;
	}

	public void setMore1(String more1) {
		this.more1 = more1;
	}

	public Float getCaozMinimumMan() {
		return caozMinimumMan;
	}

	public void setCaozMinimumMan(Float caozMinimumMan) {
		this.caozMinimumMan = caozMinimumMan;
	}

	public Float getGongzhuangJineng() {
		return gongzhuangJineng;
	}

	public void setGongzhuangJineng(Float gongzhuangJineng) {
		this.gongzhuangJineng = gongzhuangJineng;
	}

	public Float getGongzhuangtihuanrenshu() {
		return gongzhuangtihuanrenshu;
	}

	public void setGongzhuangtihuanrenshu(Float gongzhuangtihuanrenshu) {
		this.gongzhuangtihuanrenshu = gongzhuangtihuanrenshu;
	}

	public Float getGongzhuangFuhe() {
		return gongzhuangFuhe;
	}

	public void setGongzhuangFuhe(Float gongzhuangFuhe) {
		this.gongzhuangFuhe = gongzhuangFuhe;
	}

	public String getMore2() {
		return more2;
	}

	public void setMore2(String more2) {
		this.more2 = more2;
	}

	public String getShebeiXinghao() {
		return shebeiXinghao;
	}

	public void setShebeiXinghao(String shebeiXinghao) {
		this.shebeiXinghao = shebeiXinghao;
	}

	public String getGongzhuangStyle() {
		return gongzhuangStyle;
	}

	public void setGongzhuangStyle(String gongzhuangStyle) {
		this.gongzhuangStyle = gongzhuangStyle;
	}

	public Float getMinCaozRenshu() {
		return minCaozRenshu;
	}

	public void setMinCaozRenshu(Float minCaozRenshu) {
		this.minCaozRenshu = minCaozRenshu;
	}

	@JSONField(serialize = false)
	public Set<ProductProcess> getProductProcess() {
		return productProcess;
	}

	public void setProductProcess(Set<ProductProcess> productProcess) {
		this.productProcess = productProcess;
	}

	@JSONField(serialize = false)
	public Set<ProcessTemplate> getProcessTemplate() {
		return processTemplate;
	}

	public void setProcessTemplate(Set<ProcessTemplate> processTemplate) {
		this.processTemplate = processTemplate;
	}

	@JSONField(serialize = false)
	public Set<Push> getPushs() {
		return pushs;
	}

	public void setPushs(Set<Push> pushs) {
		this.pushs = pushs;
	}

	@JSONField(serialize = false)
	public Set<OneMachine> getMachines() {
		return machines;
	}

	public void setMachines(Set<OneMachine> machines) {
		this.machines = machines;
	}

}