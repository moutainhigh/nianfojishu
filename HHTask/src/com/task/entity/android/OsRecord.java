package com.task.entity.android;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.task.entity.sop.BreakSubmit;

/***
 * 抽检记录(表名:ta_m_OsRecord)
 * 
 * @author
 * 
 */
public class OsRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;

	private String wwdNumber;// 送货单号
	private Integer wwddId;// 送货单明细Id(WaigouDeliveryDetail)

	private String markId;// 件号
	private String proName;// 零件名称
	private String kgliao;// //供料属性（自购、指定、客供）
	private String gysId;// 供应商id
	private String gysName;// 供应商名称
	private String ywmakrId;//业务件号
	private String neiordeNum;//内部订单号
	private String selfCard;//生产批次
	private String tuhao;//图号
	private String gongxuNum;//工序号；
	private String gongxuName;//工序名称；
	
	private String banbenNumber;// 版本号
	private String gongwei;//工位号
	private Integer banci;// 版次
	private String specification;// 规格
	private String unit;// 单位

	
	private String productBarCode;//产品条码(成品终检使用)
	private String jcpc;// 检查批次
	private Float quantity;// 本批数量(进货数量)
	private Float jyNumber;// 检验数量
	private Float hgNumber;// 合格数量

	private String verification;// 是否合格

	private Integer buhegeId;//缺陷id
	private String code;// 缺陷代码(不合格)
	private String type;// 缺陷描述(不合格)
	private String bhgclass;//缺陷分类
	private Integer bhgTypeNum;//某种缺陷类型个数;

	private String userId;// 检验人工号
	private String userCode;// 检验人工号
	private String username;// 检验人
	private String jinhuoDate;// 库房确认日期
	private String nowDate;// 检验完成时间
	private String seeDate;
	

	private String nextDate;// 下次开始时间

	private Integer templateId;
	private Integer ppiId;// 外购件到货单id(PurchasedPartsInput)
	private String wgType;//物料类别
	/*** 量具 ***/
	private String measuringMatetag;// 名称
	private String measuring_no; // 本厂编号
	
	
	private String czg;// 操作工
	private String dateCount;
	private OsTemplate template;
	private String groupDate;
	private List<OsRecordScope> scopes;
	private Set<OsRecordScope> recordScope;
	private boolean ruku;
	private String zhonglei;// 种类 (巡检、外购件检验、首检);
	private Integer kuweiId;//页面传值使用不存入数据库
	private BreakSubmit breaksubmit;//不合格品提交记录 多对
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getJcpc() {
		return jcpc;
	}

	public void setJcpc(String jcpc) {
		this.jcpc = jcpc;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}

	public String getNowDate() {
		return nowDate;
	}

	public void setNowDate(String nowDate) {
		this.nowDate = nowDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public OsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(OsTemplate template) {
		this.template = template;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public List<OsRecordScope> getScopes() {
		return scopes;
	}

	public void setScopes(List<OsRecordScope> scopes) {
		this.scopes = scopes;
	}

	public Set<OsRecordScope> getRecordScope() {
		return recordScope;
	}

	public void setRecordScope(Set<OsRecordScope> recordScope) {
		this.recordScope = recordScope;
	}

	public String getSeeDate() {
		return seeDate;
	}

	public void setSeeDate(String seeDate) {
		this.seeDate = seeDate;
	}

	public boolean isRuku() {
		return ruku;
	}

	public void setRuku(boolean ruku) {
		this.ruku = ruku;
	}

	public Integer getPpiId() {
		return ppiId;
	}

	public void setPpiId(Integer ppiId) {
		this.ppiId = ppiId;
	}

	public String getZhonglei() {
		return zhonglei;
	}

	public void setZhonglei(String zhonglei) {
		this.zhonglei = zhonglei;
	}

	public String getCzg() {
		return czg;
	}

	public void setCzg(String czg) {
		this.czg = czg;
	}

	public String getNextDate() {
		return nextDate;
	}

	public void setNextDate(String nextDate) {
		this.nextDate = nextDate;
	}

	public String getDateCount() {
		return dateCount;
	}

	public void setDateCount(String dateCount) {
		this.dateCount = dateCount;
	}

	public String getGroupDate() {
		return groupDate;
	}

	public void setGroupDate(String groupDate) {
		this.groupDate = groupDate;
	}

	public String getBanbenNumber() {
		return banbenNumber;
	}

	public void setBanbenNumber(String banbenNumber) {
		this.banbenNumber = banbenNumber;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Integer getWwddId() {
		return wwddId;
	}

	public void setWwddId(Integer wwddId) {
		this.wwddId = wwddId;
	}

	public String getWwdNumber() {
		return wwdNumber;
	}

	public void setWwdNumber(String wwdNumber) {
		this.wwdNumber = wwdNumber;
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

	public String getGysId() {
		return gysId;
	}

	public void setGysId(String gysId) {
		this.gysId = gysId;
	}

	public String getGysName() {
		return gysName;
	}

	public void setGysName(String gysName) {
		this.gysName = gysName;
	}

	public Float getJyNumber() {
		return jyNumber;
	}

	public void setJyNumber(Float jyNumber) {
		this.jyNumber = jyNumber;
	}

	public Float getHgNumber() {
		return hgNumber;
	}

	public void setHgNumber(Float hgNumber) {
		this.hgNumber = hgNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getJinhuoDate() {
		return jinhuoDate;
	}

	public void setJinhuoDate(String jinhuoDate) {
		this.jinhuoDate = jinhuoDate;
	}

	public Integer getBuhegeId() {
		return buhegeId;
	}

	public void setBuhegeId(Integer buhegeId) {
		this.buhegeId = buhegeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getKuweiId() {
		return kuweiId;
	}

	public void setKuweiId(Integer kuweiId) {
		this.kuweiId = kuweiId;
	}

	public String getWgType() {
		return wgType;
	}

	public void setWgType(String wgType) {
		this.wgType = wgType;
	}

	public String getYwmakrId() {
		return ywmakrId;
	}

	public void setYwmakrId(String ywmakrId) {
		this.ywmakrId = ywmakrId;
	}

	public String getNeiordeNum() {
		return neiordeNum;
	}

	public void setNeiordeNum(String neiordeNum) {
		this.neiordeNum = neiordeNum;
	}

	public String getSelfCard() {
		return selfCard;
	}

	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}

	public BreakSubmit getBreaksubmit() {
		return breaksubmit;
	}

	public void setBreaksubmit(BreakSubmit breaksubmit) {
		this.breaksubmit = breaksubmit;
	}

	public String getTuhao() {
		return tuhao;
	}

	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}

	public String getBhgclass() {
		return bhgclass;
	}

	public void setBhgclass(String bhgclass) {
		this.bhgclass = bhgclass;
	}

	public Integer getBhgTypeNum() {
		return bhgTypeNum;
	}

	public void setBhgTypeNum(Integer bhgTypeNum) {
		this.bhgTypeNum = bhgTypeNum;
	}

	public String getGongxuNum() {
		return gongxuNum;
	}

	public void setGongxuNum(String gongxuNum) {
		this.gongxuNum = gongxuNum;
	}

	public String getGongxuName() {
		return gongxuName;
	}

	public void setGongxuName(String gongxuName) {
		this.gongxuName = gongxuName;
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

	public String getGongwei() {
		return gongwei;
	}

	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}

	public String getProductBarCode() {
		return productBarCode;
	}

	public void setProductBarCode(String productBarCode) {
		this.productBarCode = productBarCode;
	}


}
