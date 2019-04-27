package com.task.entity;

import java.io.Serializable;

/**
 * 工资明细表(表名:ta_fin_wage)
 * 
 * @author 刘培
 * 
 */

public class Wage implements Serializable {
	// 员工号 卡号 姓名 部门 岗位工资 保密津贴 电话补贴 绩效考核工资 加班费 其他 应发工资
	// 病事旷等 养老保险 医疗保险 失业保险 公积金 午餐费 水电及房租扣款 补发工资 应交税金 实发工资

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer Id;
	private String code;// 工号(固)
	private String cardId;// 卡号(固)
	private String userName;// 姓名(固)
	private String dept;// 部门(固)
	private Float gangweigongzi;// 岗位工资(固)
	private Float baomijintie;// 保密津贴(固)
	private Float dianhuabutie;// 电话补贴(固)
	
	private Float ssBase;// 社保基数
	private Float tongchoujin;// 养老保险(固)个人
	private Float yiliaobaoxian;// 医疗保险(固)个人
	private Float shiyebaoxian;// 失业保险(固)个人
	private Float gongshangbaoxian;// 工伤保险(固)个人
	private Float shengyubaoxian;// 生育保险(固)个人
	
	private Float dwtongchoujin;// 养老保险(固)单位
	private Float dwyiliaobaoxian;// 医疗保险(固)单位
	private Float dwshiyebaoxian;// 失业保险(固)单位
	private Float dwgongshangbaoxian;// 工伤保险(固)单位
	private Float dwshengyubaoxian;// 生育保险(固)单位
	
	private Float gjjBase;// 公积金基数
	private Float gongjijin;// 公积金(固)个人
	private Float dwgongjijin;// 公积金(固)单位
	
	
	private Float jinenggongzi;// 技能工资(固)
	private Float gonglinggongzi;// 特殊补贴(固)
	private String mouth;// 发放月份(动)
	private Float jixiaokaohegongzi;// 绩效考核工资(动)
	private Float jiangjin;// 奖金

	private Float jiabanfei;// 加班费(动)
	private Float other;// 其他(动 正/负)
	private Float yingfagongzi;// 应发工资(动)
	private Float bingshikangdeng;// 病事旷等(动)
	private Float wucanfei;// 午餐费(动)
	private Float shuidianfei;// 水电费(动)
	private Float fangzufei;// 房租费(动)
	private Float bfgongzi;// 补发(补扣)工资(动 正/负)
	private Float yingjiaoshuijin;// 应交税金(动)
	private Float shifagongzi;// 实发工资(动)
	private String addDateTime;// 添加时间(动)
	private String wageStatus;// 工资状态(添加变动,自查、 审核{通过,打回},打款。。。。)
	private Float outsourcing;// 外包工资
	private Float buchagongzi;// 补差工资
	private String isBucha;// 是否补差
	private Float noBucha;// 不补差
	private Float noJishui;// 不计税

	private Integer userId;// 工资所属人员id
	private String addChageTime;// 添加变动时间
	private String auditDateTime;// 审核时间

	private String addUserName;// 添加人员名称
	private Integer addUserId;// 添加人员ID

	private String wageClass;// 工资类别(绩效打分、奖金转换、内退处理、主管互评、承包奖金、离职工资、试用工资)
	private String wageStatue;// 状态状态(正常(没有扣工资)、不正常(反之))
	private String printDate;// 打印时间
	
	private Double zxfjkc;//专项附加扣除总额
	

	// 离职相关列
	private Float leaveBuchang;// 离职补偿
	private String leaveNumber;// 离职申请单编号
	private String directions;// 说明
	
	//关联数据
	private Integer scoreId;//打分明细
	private Integer jiangjinId;//奖金明细
	private Integer gaowenfeiId;//高温费Id
	
	//页面显示 不保存
	private String sfsj;//实发升降
	private String sbsj;//社保升降
	private String gjjsj;//公积金升降
	private String gssj;//个税升降
	

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Float getGangweigongzi() {
		return gangweigongzi;
	}

	public void setGangweigongzi(Float gangweigongzi) {
		this.gangweigongzi = gangweigongzi;
	}

	public Float getBaomijintie() {
		return baomijintie;
	}

	public void setBaomijintie(Float baomijintie) {
		this.baomijintie = baomijintie;
	}

	public Float getDianhuabutie() {
		return dianhuabutie;
	}

	public void setDianhuabutie(Float dianhuabutie) {
		this.dianhuabutie = dianhuabutie;
	}

	public Float getTongchoujin() {
		return tongchoujin;
	}

	public void setTongchoujin(Float tongchoujin) {
		this.tongchoujin = tongchoujin;
	}

	public Float getYiliaobaoxian() {
		return yiliaobaoxian;
	}

	public void setYiliaobaoxian(Float yiliaobaoxian) {
		this.yiliaobaoxian = yiliaobaoxian;
	}

	public Float getShiyebaoxian() {
		return shiyebaoxian;
	}

	public void setShiyebaoxian(Float shiyebaoxian) {
		this.shiyebaoxian = shiyebaoxian;
	}

	public Float getGongjijin() {
		return gongjijin;
	}

	public void setGongjijin(Float gongjijin) {
		this.gongjijin = gongjijin;
	}

	public Float getJinenggongzi() {
		return jinenggongzi;
	}

	public void setJinenggongzi(Float jinenggongzi) {
		this.jinenggongzi = jinenggongzi;
	}

	public Float getGonglinggongzi() {
		return gonglinggongzi;
	}

	public void setGonglinggongzi(Float gonglinggongzi) {
		this.gonglinggongzi = gonglinggongzi;
	}

	public String getMouth() {
		return mouth;
	}

	public void setMouth(String mouth) {
		this.mouth = mouth;
	}

	public Float getJixiaokaohegongzi() {
		return jixiaokaohegongzi;
	}

	public void setJixiaokaohegongzi(Float jixiaokaohegongzi) {
		this.jixiaokaohegongzi = jixiaokaohegongzi;
	}

	public Float getJiangjin() {
		return jiangjin;
	}

	public void setJiangjin(Float jiangjin) {
		this.jiangjin = jiangjin;
	}

	public Float getJiabanfei() {
		return jiabanfei;
	}

	public void setJiabanfei(Float jiabanfei) {
		this.jiabanfei = jiabanfei;
	}

	public Float getOther() {
		return other;
	}

	public void setOther(Float other) {
		this.other = other;
	}

	public Float getYingfagongzi() {
		return yingfagongzi;
	}

	public void setYingfagongzi(Float yingfagongzi) {
		this.yingfagongzi = yingfagongzi;
	}

	public Float getBingshikangdeng() {
		return bingshikangdeng;
	}

	public void setBingshikangdeng(Float bingshikangdeng) {
		this.bingshikangdeng = bingshikangdeng;
	}

	public Float getWucanfei() {
		return wucanfei;
	}

	public void setWucanfei(Float wucanfei) {
		this.wucanfei = wucanfei;
	}

	public Float getShuidianfei() {
		return shuidianfei;
	}

	public void setShuidianfei(Float shuidianfei) {
		this.shuidianfei = shuidianfei;
	}

	public Float getFangzufei() {
		return fangzufei;
	}

	public void setFangzufei(Float fangzufei) {
		this.fangzufei = fangzufei;
	}

	public Float getBfgongzi() {
		return bfgongzi;
	}

	public void setBfgongzi(Float bfgongzi) {
		this.bfgongzi = bfgongzi;
	}

	public Float getYingjiaoshuijin() {
		return yingjiaoshuijin;
	}

	public void setYingjiaoshuijin(Float yingjiaoshuijin) {
		this.yingjiaoshuijin = yingjiaoshuijin;
	}

	public Float getShifagongzi() {
		return shifagongzi;
	}

	public void setShifagongzi(Float shifagongzi) {
		this.shifagongzi = shifagongzi;
	}

	public String getAddDateTime() {
		return addDateTime;
	}

	public void setAddDateTime(String addDateTime) {
		this.addDateTime = addDateTime;
	}

	public String getWageStatus() {
		return wageStatus;
	}

	public void setWageStatus(String wageStatus) {
		this.wageStatus = wageStatus;
	}

	public Float getOutsourcing() {
		return outsourcing;
	}

	public void setOutsourcing(Float outsourcing) {
		this.outsourcing = outsourcing;
	}

	public Float getBuchagongzi() {
		return buchagongzi;
	}

	public void setBuchagongzi(Float buchagongzi) {
		this.buchagongzi = buchagongzi;
	}

	public String getIsBucha() {
		return isBucha;
	}

	public void setIsBucha(String isBucha) {
		this.isBucha = isBucha;
	}

	public Float getNoBucha() {
		return noBucha;
	}

	public void setNoBucha(Float noBucha) {
		this.noBucha = noBucha;
	}

	public Float getNoJishui() {
		return noJishui;
	}

	public void setNoJishui(Float noJishui) {
		this.noJishui = noJishui;
	}

	public String getAddChageTime() {
		return addChageTime;
	}

	public void setAddChageTime(String addChageTime) {
		this.addChageTime = addChageTime;
	}

	public String getAuditDateTime() {
		return auditDateTime;
	}

	public void setAuditDateTime(String auditDateTime) {
		this.auditDateTime = auditDateTime;
	}

	public String getAddUserName() {
		return addUserName;
	}

	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

	public String getWageClass() {
		return wageClass;
	}

	public void setWageClass(String wageClass) {
		this.wageClass = wageClass;
	}

	public String getWageStatue() {
		return wageStatue;
	}

	public void setWageStatue(String wageStatue) {
		this.wageStatue = wageStatue;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getLeaveNumber() {
		return leaveNumber;
	}

	public void setLeaveNumber(String leaveNumber) {
		this.leaveNumber = leaveNumber;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public Float getLeaveBuchang() {
		return leaveBuchang;
	}

	public void setLeaveBuchang(Float leaveBuchang) {
		this.leaveBuchang = leaveBuchang;
	}

	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

	public Float getDwtongchoujin() {
		return dwtongchoujin;
	}

	public void setDwtongchoujin(Float dwtongchoujin) {
		this.dwtongchoujin = dwtongchoujin;
	}

	public Float getDwyiliaobaoxian() {
		return dwyiliaobaoxian;
	}

	public void setDwyiliaobaoxian(Float dwyiliaobaoxian) {
		this.dwyiliaobaoxian = dwyiliaobaoxian;
	}

	public Float getDwshiyebaoxian() {
		return dwshiyebaoxian;
	}

	public void setDwshiyebaoxian(Float dwshiyebaoxian) {
		this.dwshiyebaoxian = dwshiyebaoxian;
	}

	public Float getDwgongjijin() {
		return dwgongjijin;
	}

	public void setDwgongjijin(Float dwgongjijin) {
		this.dwgongjijin = dwgongjijin;
	}

	public Float getSsBase() {
		return ssBase;
	}

	public void setSsBase(Float ssBase) {
		this.ssBase = ssBase;
	}

	public Float getGongshangbaoxian() {
		return gongshangbaoxian;
	}

	public void setGongshangbaoxian(Float gongshangbaoxian) {
		this.gongshangbaoxian = gongshangbaoxian;
	}

	public Float getShengyubaoxian() {
		return shengyubaoxian;
	}

	public void setShengyubaoxian(Float shengyubaoxian) {
		this.shengyubaoxian = shengyubaoxian;
	}

	public Float getDwgongshangbaoxian() {
		return dwgongshangbaoxian;
	}

	public void setDwgongshangbaoxian(Float dwgongshangbaoxian) {
		this.dwgongshangbaoxian = dwgongshangbaoxian;
	}

	public Float getDwshengyubaoxian() {
		return dwshengyubaoxian;
	}

	public void setDwshengyubaoxian(Float dwshengyubaoxian) {
		this.dwshengyubaoxian = dwshengyubaoxian;
	}

	public Float getGjjBase() {
		return gjjBase;
	}

	public void setGjjBase(Float gjjBase) {
		this.gjjBase = gjjBase;
	}

	public Integer getScoreId() {
		return scoreId;
	}

	public void setScoreId(Integer scoreId) {
		this.scoreId = scoreId;
	}

	public Integer getJiangjinId() {
		return jiangjinId;
	}

	public void setJiangjinId(Integer jiangjinId) {
		this.jiangjinId = jiangjinId;
	}

	public Integer getGaowenfeiId() {
		return gaowenfeiId;
	}

	public void setGaowenfeiId(Integer gaowenfeiId) {
		this.gaowenfeiId = gaowenfeiId;
	}

	public String getSfsj() {
		return sfsj;
	}

	public void setSfsj(String sfsj) {
		this.sfsj = sfsj;
	}

	public String getSbsj() {
		return sbsj;
	}

	public void setSbsj(String sbsj) {
		this.sbsj = sbsj;
	}

	public String getGjjsj() {
		return gjjsj;
	}

	public void setGjjsj(String gjjsj) {
		this.gjjsj = gjjsj;
	}

	public String getGssj() {
		return gssj;
	}

	public void setGssj(String gssj) {
		this.gssj = gssj;
	}

	public Double getZxfjkc() {
		return zxfjkc;
	}

	public void setZxfjkc(Double zxfjkc) {
		this.zxfjkc = zxfjkc;
	}

}
