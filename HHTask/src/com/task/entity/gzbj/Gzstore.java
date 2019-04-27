package com.task.entity.gzbj;

import java.util.Date; 
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
/*
 * 
 * 工装/模具（表名：Gzstore）
 */
public class Gzstore implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;// 主键
	private Integer fk_stoid;// 与goods做标识
//	private Integer fk_progzid;// 与ProcessGzstore做标识
	private String number;// 编号
	private String markId;//件号(采购/使用)
	private String matetag;// 名称
	private Float total;// 总数
	private String unit;// 单位
//	private String format;// 规格
	private String storehouse;// 仓库
	private String mix;// 　合成主码
	private String parClass;// 　分类(模具/工装)
	private String place;// 位置
	private Integer period;// 报检周期
	private Date startDate;// 上次保养
	private Float curAmount;// 当前量
	private Integer maxBorrowNum;// 　最大可借量
	private String more;// 　备注
	private Integer bjcs;//报检次数
	private Integer sybjcs;//剩余报检次数;
	private String fzr;//负责人
	
	private String more1;// 备注1
	private Float price;// 价格
	private Integer carePeriod;// 保养周期
	private Integer curworkAmount;// 当前工作量
	private Date lastCareDate;// 最后维修日期
	private String serverCardId;// 加工件号
	private String carModel;// 车型
	private String duizhang;// 对账
	private Float minStore;// 最低库存，低于此最低库存就要申请采购
	private String appDept;// 申报部门
	private Float totMoney;// 合计金额
	private String classify; // (可借用/领用)
	private String xingbie;//型别
	private Double sumshuliang;//累计使用次数 
	private String status;//状态（待校验、正常、报废）
	private String fileName;//文件名
	
	private Set gzstores=new HashSet();
	private Set<Checkrecord> checkrecordSet;//校验记录

	private Set<ProcessGzstore> processGzstores;
	


	public String getXingbie() {
		return xingbie;
	}

	public void setXingbie(String xingbie) {
		this.xingbie = xingbie;
	}
	@JSONField(serialize = false)
	public Set getGzstores() {
		return gzstores;
	}

	public void setGzstores(Set gzstores) {
		this.gzstores = gzstores;
	}
	@JSONField(serialize = false)
	public Set<ProcessGzstore> getProcessGzstores() {
		return processGzstores;
	}

	public void setProcessGzstores(Set<ProcessGzstore> processGzstores) {
		this.processGzstores = processGzstores;
	}

	
 
	public Integer getFk_stoid() {
		return fk_stoid;
	}

	public void setFk_stoid(Integer fkStoid) {
		fk_stoid = fkStoid;
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

	public String getMatetag() {
		return matetag;
	}

	public void setMatetag(String matetag) {
		this.matetag = matetag;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

//	public String getFormat() {
//		return format;
//	}
//
//	public void setFormat(String format) {
//		this.format = format;
//	}

	public String getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(String storehouse) {
		this.storehouse = storehouse;
	}

	public String getMix() {
		return mix;
	}

	public void setMix(String mix) {
		this.mix = mix;
	}

	public String getParClass() {
		return parClass;
	}

	public void setParClass(String parClass) {
		this.parClass = parClass;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Float getCurAmount() {
		return curAmount;
	}

	public void setCurAmount(Float curAmount) {
		this.curAmount = curAmount;
	}

	public Integer getMaxBorrowNum() {
		return maxBorrowNum;
	}

	public void setMaxBorrowNum(Integer maxBorrowNum) {
		this.maxBorrowNum = maxBorrowNum;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getMore1() {
		return more1;
	}

	public void setMore1(String more1) {
		this.more1 = more1;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getCarePeriod() {
		return carePeriod;
	}

	public void setCarePeriod(Integer carePeriod) {
		this.carePeriod = carePeriod;
	}

	public Integer getCurworkAmount() {
		return curworkAmount;
	}

	public void setCurworkAmount(Integer curworkAmount) {
		this.curworkAmount = curworkAmount;
	}

	public Date getLastCareDate() {
		return lastCareDate;
	}

	public void setLastCareDate(Date lastCareDate) {
		this.lastCareDate = lastCareDate;
	}

	public String getServerCardId() {
		return serverCardId;
	}

	public void setServerCardId(String serverCardId) {
		this.serverCardId = serverCardId;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getDuizhang() {
		return duizhang;
	}

	public void setDuizhang(String duizhang) {
		this.duizhang = duizhang;
	}

	public Float getMinStore() {
		return minStore;
	}

	public void setMinStore(Float minStore) {
		this.minStore = minStore;
	}

	public String getAppDept() {
		return appDept;
	}

	public void setAppDept(String appDept) {
		this.appDept = appDept;
	}

	public Float getTotMoney() {
		return totMoney;
	}

	public void setTotMoney(Float totMoney) {
		this.totMoney = totMoney;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}


	public Integer getBjcs() {
		return bjcs;
	}

	public void setBjcs(Integer bjcs) {
		this.bjcs = bjcs;
	}

	public Double getSumshuliang() {
		return sumshuliang;
	}

	public void setSumshuliang(Double sumshuliang) {
		this.sumshuliang = sumshuliang;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSybjcs() {
		return sybjcs;
	}

	public void setSybjcs(Integer sybjcs) {
		this.sybjcs = sybjcs;
	}
	@JSONField(serialize = false)
	public Set<Checkrecord> getCheckrecordSet() {
		return checkrecordSet;
	}

	public void setCheckrecordSet(Set<Checkrecord> checkrecordSet) {
		this.checkrecordSet = checkrecordSet;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getFzr() {
		return fzr;
	}

	public void setFzr(String fzr) {
		this.fzr = fzr;
	}
	

}
