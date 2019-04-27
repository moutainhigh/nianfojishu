package com.task.entity.renshi;

import java.io.Serializable;

import com.task.util.FieldMeta;

/**
 * 
 * @author Li_Cong 表名 ta_hr_lz_dimissionLog
 */
public class DimissionLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer codeId; // 申请人ID
	private String shenqing_number;// 申请单编号
	@FieldMeta(name = "申请人姓名")
	private String name;
	private String cardId;//卡号
	@FieldMeta(name = "申请人部门")
	private String dept;
	@FieldMeta(name = "申请人岗位")
	private String job;// 岗位
	private String year_term;// 工作年限
	private String code;
	private String contract_number;// 合同编号
	@FieldMeta(name = "离职原因")
	private String dimission_Reason;// 离职原因
	@FieldMeta(name = "离职后去向")
	private String dimission_laterGo;// 离职后去向
	private String ruzhi_time;// 入职时间
	@FieldMeta(name = "部门主管确认离职时间")
	private String app_time;// 主管确认离职时间
	private String naowuzhengyi;// 是否遗留劳务争议
	private String zhengyi_content;// 备注内容
	private String zhengyi_Status;// 争议单填写状态（根据naowuzhengyi）状态为（无/待填写/已填写）
	private String confirm;// 本人确定
	private String addTime;// 添加时间
	private Integer epId;// 审批流程
	@FieldMeta(name = "审核状态")
	private String lzsq_status;// 审核状态(待确认、未审批、审批中、同意、打回)
	private String hand_status;// 交接单填写状态（无、未填写、已填写）
	private String updateTime;// 修改时间

	private Dimission_ZhengYi dimissionZhengYi;// 离职争议表
	private Dimission_XieYi dimissionXieYi;// 离职争议表

	private String add_dimissTime_status;// 添加实际离职时间的状态(待填写/已填写)
	private String add_add_dimiss_Time;// 添加实际离职时间状态的添加时间（主管确认申请的时间）

	private String tijian;// 是否需要体检
	private String peixunxieyi;// 是否接受过工厂外培训
	private String baomi;// 是否签有保密协议
	private String buchong;// 是否有补充说明内容
	private String hr_addTime;// 人事确定时间
	private String hr_status;// 人事确定状态
	private String hr_true;// 人事同意
	private String xieyi_addTime;// 协议确定时间
	private String xieyi_status;// 协议确定状态（根据naowuzhengyi）状态为（无/待填写/已填写）

	private String tongzhi_status;//通知单状态(待填写/已填写)
	private String tongzhi_addTime;//通知添加时间
	
	private String uid;// 身份证号码
	private String tel;// 电话
	private String address;// 户籍地址
	private String addUsersName;// 添加人姓名

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the addUsersName
	 */
	public String getAddUsersName() {
		return addUsersName;
	}

	/**
	 * @param addUsersName the addUsersName to set
	 */
	public void setAddUsersName(String addUsersName) {
		this.addUsersName = addUsersName;
	}

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getHand_status() {
		return hand_status;
	}

	public void setHand_status(String handStatus) {
		hand_status = handStatus;
	}

	public String getZhengyi_Status() {
		return zhengyi_Status;
	}

	public void setZhengyi_Status(String zhengyiStatus) {
		zhengyi_Status = zhengyiStatus;
	}

	public Dimission_ZhengYi getDimissionZhengYi() {
		return dimissionZhengYi;
	}

	public void setDimissionZhengYi(Dimission_ZhengYi dimissionZhengYi) {
		this.dimissionZhengYi = dimissionZhengYi;
	}

	public String getLzsq_status() {
		return lzsq_status;
	}

	public void setLzsq_status(String lzsqStatus) {
		lzsq_status = lzsqStatus;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getZhengyi_content() {
		return zhengyi_content;
	}

	public void setZhengyi_content(String zhengyiContent) {
		zhengyi_content = zhengyiContent;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getShenqing_number() {
		return shenqing_number;
	}

	public void setShenqing_number(String shenqingNumber) {
		shenqing_number = shenqingNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getYear_term() {
		return year_term;
	}

	public void setYear_term(String yearTerm) {
		year_term = yearTerm;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContract_number() {
		return contract_number;
	}

	public void setContract_number(String contractNumber) {
		contract_number = contractNumber;
	}

	public String getDimission_Reason() {
		return dimission_Reason;
	}

	public void setDimission_Reason(String dimissionReason) {
		dimission_Reason = dimissionReason;
	}

	public String getDimission_laterGo() {
		return dimission_laterGo;
	}

	public void setDimission_laterGo(String dimissionLaterGo) {
		dimission_laterGo = dimissionLaterGo;
	}

	public String getApp_time() {
		return app_time;
	}

	public void setApp_time(String appTime) {
		app_time = appTime;
	}

	public String getNaowuzhengyi() {
		return naowuzhengyi;
	}

	public void setNaowuzhengyi(String naowuzhengyi) {
		this.naowuzhengyi = naowuzhengyi;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public Integer getCodeId() {
		return codeId;
	}

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public String getAdd_dimissTime_status() {
		return add_dimissTime_status;
	}

	public void setAdd_dimissTime_status(String addDimissTimeStatus) {
		add_dimissTime_status = addDimissTimeStatus;
	}

	public String getAdd_add_dimiss_Time() {
		return add_add_dimiss_Time;
	}

	public void setAdd_add_dimiss_Time(String addAddDimissTime) {
		add_add_dimiss_Time = addAddDimissTime;
	}

	public String getRuzhi_time() {
		return ruzhi_time;
	}

	public void setRuzhi_time(String ruzhiTime) {
		ruzhi_time = ruzhiTime;
	}

	public String getTijian() {
		return tijian;
	}

	public void setTijian(String tijian) {
		this.tijian = tijian;
	}

	public String getPeixunxieyi() {
		return peixunxieyi;
	}

	public void setPeixunxieyi(String peixunxieyi) {
		this.peixunxieyi = peixunxieyi;
	}

	public String getBaomi() {
		return baomi;
	}

	public void setBaomi(String baomi) {
		this.baomi = baomi;
	}

	public String getBuchong() {
		return buchong;
	}

	public void setBuchong(String buchong) {
		this.buchong = buchong;
	}

	public String getHr_addTime() {
		return hr_addTime;
	}

	public void setHr_addTime(String hrAddTime) {
		hr_addTime = hrAddTime;
	}

	public String getHr_status() {
		return hr_status;
	}

	public void setHr_status(String hrStatus) {
		hr_status = hrStatus;
	}

	public String getXieyi_addTime() {
		return xieyi_addTime;
	}

	public void setXieyi_addTime(String xieyiAddTime) {
		xieyi_addTime = xieyiAddTime;
	}

	public String getXieyi_status() {
		return xieyi_status;
	}

	public void setXieyi_status(String xieyiStatus) {
		xieyi_status = xieyiStatus;
	}

	public String getHr_true() {
		return hr_true;
	}

	public void setHr_true(String hrTrue) {
		hr_true = hrTrue;
	}

	public Dimission_XieYi getDimissionXieYi() {
		return dimissionXieYi;
	}

	public void setDimissionXieYi(Dimission_XieYi dimissionXieYi) {
		this.dimissionXieYi = dimissionXieYi;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTongzhi_status() {
		return tongzhi_status;
	}

	public void setTongzhi_status(String tongzhiStatus) {
		tongzhi_status = tongzhiStatus;
	}

	public String getTongzhi_addTime() {
		return tongzhi_addTime;
	}

	public void setTongzhi_addTime(String tongzhiAddTime) {
		tongzhi_addTime = tongzhiAddTime;
	}


}
