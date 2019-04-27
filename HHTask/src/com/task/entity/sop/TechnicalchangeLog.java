package com.task.entity.sop;

import java.util.List;
import java.util.Set;

/**
 * EC ta_sop_w_TechnicalchangeLog
 * @author txb
 *
 */
public class TechnicalchangeLog  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private Integer addUserId;//添加人Id
	private String addUserName;//
	private String addUserCode;//
	private String addTime;//添加时间
	private String recriveDate;//接收日期
	private Integer applyUserId;//发起人ID
	private String applyUserName;//
	private String applyUserCode;//
	private String aboutPlace;//涉及车间
	private String ywMarkId;//涉及编码
	private String markId;//件号
	private String producttype;//产品类别
	private String sbNumber;//设变单号
	private String outbsNumber;//外部设变单号
	private String tuhao;//图号
	private String describetion;//描述
	private String aboutcb;//成本影响
	private String zzpcl;//在制品处理
	private String wcpcl;//完成品处理
	private String wtdcl;//未投单处理
	private String gcb;//工程部(yes,no)
	private String scb;//生产部(yes,no)
	private String pzb;//品质部(yes,no)
	private String pmcwk;//PMC物控(yes,no)
	private String pmcsg;//PMC生管(yes,no)
	private String cgwx;//采购外协(yes,no)
	private String cgwg;//采购外购(yes,no)
	private String ck;//仓库(yes,no)
	private String remark;//备注(yes,no)
	
	private Set<TechnicalchangeLogDetail> tclDetailSet; 
	private List<TechnicalchangeLogDetail> tclDetailList; 
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAddUserId() {
		return addUserId;
	}
	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}
	public String getAddUserName() {
		return addUserName;
	}
	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}
	public String getAddUserCode() {
		return addUserCode;
	}
	public void setAddUserCode(String addUserCode) {
		this.addUserCode = addUserCode;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getRecriveDate() {
		return recriveDate;
	}
	public void setRecriveDate(String recriveDate) {
		this.recriveDate = recriveDate;
	}
	
	public Integer getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public String getApplyUserCode() {
		return applyUserCode;
	}
	public void setApplyUserCode(String applyUserCode) {
		this.applyUserCode = applyUserCode;
	}
	public String getAboutPlace() {
		return aboutPlace;
	}
	public void setAboutPlace(String aboutPlace) {
		this.aboutPlace = aboutPlace;
	}
	public String getYwMarkId() {
		return ywMarkId;
	}
	public void setYwMarkId(String ywMarkId) {
		this.ywMarkId = ywMarkId;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getProducttype() {
		return producttype;
	}
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	public String getSbNumber() {
		return sbNumber;
	}
	public void setSbNumber(String sbNumber) {
		this.sbNumber = sbNumber;
	}
	public String getOutbsNumber() {
		return outbsNumber;
	}
	public void setOutbsNumber(String outbsNumber) {
		this.outbsNumber = outbsNumber;
	}
	public String getTuhao() {
		return tuhao;
	}
	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}
	
	public String getDescribetion() {
		return describetion;
	}
	public void setDescribetion(String describetion) {
		this.describetion = describetion;
	}
	public String getAboutcb() {
		return aboutcb;
	}
	public void setAboutcb(String aboutcb) {
		this.aboutcb = aboutcb;
	}
	public String getZzpcl() {
		return zzpcl;
	}
	public void setZzpcl(String zzpcl) {
		this.zzpcl = zzpcl;
	}
	public String getWcpcl() {
		return wcpcl;
	}
	public void setWcpcl(String wcpcl) {
		this.wcpcl = wcpcl;
	}
	public String getWtdcl() {
		return wtdcl;
	}
	public void setWtdcl(String wtdcl) {
		this.wtdcl = wtdcl;
	}
	public String getGcb() {
		return gcb;
	}
	public void setGcb(String gcb) {
		this.gcb = gcb;
	}
	public String getScb() {
		return scb;
	}
	public void setScb(String scb) {
		this.scb = scb;
	}
	public String getPzb() {
		return pzb;
	}
	public void setPzb(String pzb) {
		this.pzb = pzb;
	}
	public String getPmcwk() {
		return pmcwk;
	}
	public void setPmcwk(String pmcwk) {
		this.pmcwk = pmcwk;
	}
	public String getPmcsg() {
		return pmcsg;
	}
	public void setPmcsg(String pmcsg) {
		this.pmcsg = pmcsg;
	}
	public String getCgwx() {
		return cgwx;
	}
	public void setCgwx(String cgwx) {
		this.cgwx = cgwx;
	}
	public String getCk() {
		return ck;
	}
	public void setCk(String ck) {
		this.ck = ck;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCgwg() {
		return cgwg;
	}
	public void setCgwg(String cgwg) {
		this.cgwg = cgwg;
	}
	public Set<TechnicalchangeLogDetail> getTclDetailSet() {
		return tclDetailSet;
	}
	public void setTclDetailSet(Set<TechnicalchangeLogDetail> tclDetailSet) {
		this.tclDetailSet = tclDetailSet;
	}
	public List<TechnicalchangeLogDetail> getTclDetailList() {
		return tclDetailList;
	}
	public void setTclDetailList(List<TechnicalchangeLogDetail> tclDetailList) {
		this.tclDetailList = tclDetailList;
	}
	
	
	
	
	
	
	
	
	
	
	
}
