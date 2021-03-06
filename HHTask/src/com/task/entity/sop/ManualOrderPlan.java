package com.task.entity.sop;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.util.FieldMeta;



/**
 * 物料需求表(ta_sop_w_ManualOrderPlan)
 * （1）审核通过之后显示到待采购页面哪里
 * （2）用于非正常采购模式如:安全库存/无BOM
 * @author 王晓飞
 *
 */

public class ManualOrderPlan  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	@FieldMeta(name="件号")
	private String markId;// 件号
	@FieldMeta(name="零件名称")
	private String proName;// 零件名称
	@FieldMeta(name="规格")
	private String specification;// 规格
	@FieldMeta(name="版本")
	private String banben;//版本
	private Float needNumber;//实际需求量
	@FieldMeta(name="需求量")
	private Float number;//采购量 可能存在MOQ/MPQ（审批时显示的是需求量）
	private Float maxxdNum;//最大下单量根据最高库存计算得来.
	@FieldMeta(name="图号")
	private String tuhao;//图号
	@FieldMeta(name="单位")
	private String unit;//单位
	@FieldMeta(name="供料属性")
	private String kgliao;// //供料属性（外购件使用：是,否，null代表否）
	@FieldMeta(name="版次")
	private Integer banci;// 版次
	@FieldMeta(name="物料类别")
	private String wgType;//物料类别
	@FieldMeta(name="已采购数量")
	private Float outcgNumber;//已采购数量
	private Float cgbl;
	private Float price;
	@FieldMeta(name="MOQ/MPQ数量")
	private Float moqNum;//MOQ数量
	private Float mpqNum;
	@FieldMeta(name="本次分配")
	private String moqGysCode;//
	private String gyscodeAndNum;//供应商编码与(将要)分配数量  gys001:50;gys002:80;gys003:90
	private String isuse;//是启用MOQ MPQ; YES/NO
	private Float qsCount;//签收数量
	private Float hgCount;//合格数量
	private Float wshCount;//未送货数量
	private Float rukuNum;//入库数量
	private Integer epId;
	private String epStatus;
	private Float ylNumber;//已领数量
	
	private Set<ManualOrderPlanDetail> modSet;//明细 一对多
	private List<ManualOrderPlanDetail> modLst;//
	private String addtime;//添加时间
	private String caigouTime;//采购下单时间
	private String needFinalDate;// 送货日期(取最近的一个时间)
	private String category;//申请类别   （外购、辅料）
	private String rootMarkId;
	private String ywMarkId;
	private List<WaigouPlan> wgPlanList;
	private String status;//状态
	@FieldMeta(name="取消数量")
	private Float cancalNum;//取消数量
	private Integer epCancalId;//
	private String epCancalStatus;//取消状态
	private String demanddept;//需求部门
	private Float oldNumber;//原需求数量
	
	//查询条件
	private String gysName;// 供应商名称
	private String addUsername;//采购员
	private String startTime;//开始 下单时间
	private String endTime;//结束 下单时间
	private String totalNum;//物料申请单流水单号
	private String proNumber;//项目编号
	
	
	
	
	
	
	public String getProNumber() {
		return proNumber;
	}
	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public ManualOrderPlan(String markId, String proName, String specification,
			String banben, Float number, String tuhao, String unit,
			String kgliao,  String wgType,
			 Set<ManualOrderPlanDetail> modSet) {
		super();
		this.markId = markId;
		this.proName = proName;
		this.specification = specification;
		this.banben = banben;
		this.number = number;
		this.tuhao = tuhao;
		this.unit = unit;
		this.kgliao = kgliao;
		this.wgType = wgType;
		this.modSet = modSet;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
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

	
	public ManualOrderPlan() {
		super();
	}
	public Float getNumber() {
		return number;
	}
	public void setNumber(Float number) {
		this.number = number;
	}
	public String getKgliao() {
		return kgliao;
	}
	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

	public Integer getBanci() {
		return banci;
	}
	public void setBanci(Integer banci) {
		this.banci = banci;
	}
	public String getBanben() {
		return banben;
	}
	public void setBanben(String banben) {
		this.banben = banben;
	}
	
	public String getWgType() {
		return wgType;
	}
	public void setWgType(String wgType) {
		this.wgType = wgType;
	}
	
	public Float getOutcgNumber() {
		return outcgNumber;
	}
	public void setOutcgNumber(Float outcgNumber) {
		this.outcgNumber = outcgNumber;
	}
	@JSONField(serialize = false)
	public Set<ManualOrderPlanDetail> getModSet() {
		return modSet;
	}
	public void setModSet(Set<ManualOrderPlanDetail> modSet) {
		this.modSet = modSet;
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
	public Float getCgbl() {
		return cgbl;
	}
	public void setCgbl(Float cgbl) {
		this.cgbl = cgbl;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Float getMoqNum() {
		return moqNum;
	}
	public void setMoqNum(Float moqNum) {
		this.moqNum = moqNum;
	}
	public Float getMpqNum() {
		return mpqNum;
	}
	public void setMpqNum(Float mpqNum) {
		this.mpqNum = mpqNum;
	}
	
	public String getGyscodeAndNum() {
		return gyscodeAndNum;
	}
	public void setGyscodeAndNum(String gyscodeAndNum) {
		this.gyscodeAndNum = gyscodeAndNum;
	}
	public String getIsuse() {
		return isuse;
	}
	public void setIsuse(String isuse) {
		this.isuse = isuse;
	}
	public String getMoqGysCode() {
		return moqGysCode;
	}
	public void setMoqGysCode(String moqGysCode) {
		this.moqGysCode = moqGysCode;
	}
	
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public String getEpStatus() {
		return epStatus;
	}
	public void setEpStatus(String epStatus) {
		this.epStatus = epStatus;
	}
	public List<ManualOrderPlanDetail> getModLst() {
		return modLst;
	}
	public void setModLst(List<ManualOrderPlanDetail> modLst) {
		this.modLst = modLst;
	}
	public Float getRukuNum() {
		return rukuNum;
	}
	public void setRukuNum(Float rukuNum) {
		this.rukuNum = rukuNum;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public Float getQsCount() {
		return qsCount;
	}
	public void setQsCount(Float qsCount) {
		this.qsCount = qsCount;
	}
	public Float getHgCount() {
		return hgCount;
	}
	public void setHgCount(Float hgCount) {
		this.hgCount = hgCount;
	}
	public Float getWshCount() {
		return wshCount;
	}
	public void setWshCount(Float wshCount) {
		this.wshCount = wshCount;
	}
	public List<WaigouPlan> getWgPlanList() {
		return wgPlanList;
	}
	public void setWgPlanList(List<WaigouPlan> wgPlanList) {
		this.wgPlanList = wgPlanList;
	}
	public String getNeedFinalDate() {
		return needFinalDate;
	}
	public void setNeedFinalDate(String needFinalDate) {
		this.needFinalDate = needFinalDate;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Float getNeedNumber() {
		return needNumber;
	}
	public void setNeedNumber(Float needNumber) {
		this.needNumber = needNumber;
	}
	public String getRootMarkId() {
		return rootMarkId;
	}
	public void setRootMarkId(String rootMarkId) {
		this.rootMarkId = rootMarkId;
	}
	public String getYwMarkId() {
		return ywMarkId;
	}
	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}
	public void setGysName(String gysName) {
		this.gysName = gysName;
	}
	public String getGysName() {
		return gysName;
	}
	public void setAddUsername(String addUsername) {
		this.addUsername = addUsername;
	}
	public String getAddUsername() {
		return addUsername;
	}
	public Float getYlNumber() {
		return ylNumber;
	}
	public void setYlNumber(Float ylNumber) {
		this.ylNumber = ylNumber;
	}
	public String getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Float getCancalNum() {
		return cancalNum;
	}
	public void setCancalNum(Float cancalNum) {
		this.cancalNum = cancalNum;
	}
	public Integer getEpCancalId() {
		return epCancalId;
	}
	public void setEpCancalId(Integer epCancalId) {
		this.epCancalId = epCancalId;
	}
	public String getEpCancalStatus() {
		return epCancalStatus;
	}
	public void setEpCancalStatus(String epCancalStatus) {
		this.epCancalStatus = epCancalStatus;
	}
	public String getCaigouTime() {
		return caigouTime;
	}
	public void setCaigouTime(String caigouTime) {
		this.caigouTime = caigouTime;
	}
	public String getDemanddept() {
		return demanddept;
	}
	public void setDemanddept(String demanddept) {
		this.demanddept = demanddept;
	}
	public Float getOldNumber() {
		return oldNumber;
	}
	public void setOldNumber(Float oldNumber) {
		this.oldNumber = oldNumber;
	}
	public Float getMaxxdNum() {
		return maxxdNum;
	}
	public void setMaxxdNum(Float maxxdNum) {
		this.maxxdNum = maxxdNum;
	}
	
}
