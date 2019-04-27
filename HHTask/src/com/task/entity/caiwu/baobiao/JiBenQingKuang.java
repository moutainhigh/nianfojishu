package com.task.entity.caiwu.baobiao;

import java.io.Serializable;

/**
 * 
 * @author 王晓飞
 *快报 -- 基本情况表 ；（ta_baobiao_JiBenQingKuang）
 *
 */
public class JiBenQingKuang implements Serializable{
	
	private  Integer id;
	
	private String companyName;//公司名称
	private String months;//月份
	
	/*****一、财务指标*****/
	//计提的固定资产折旧总额
	private Double  gdzczj1;
	private Double  gdzczj2;
	private Double  gdzczj3;
	//（二）投资收益
	private Double tzsy1;
	private Double tzsy2;
	private Double tzsy3;
	//长期股权投资
	private Double cqgqtz1;
	private Double cqgqtz2;
	private Double cqgqtz3;
	//以公允价值计量且其变动计入当期损益的金融资产
	private Double gyzjrzc1;
	private Double gyzjrzc2;
	private Double gyzjrzc3;
	//以公允价值计量且其变动计入当期损益的金融负债
	private Double gyzjrfz1;
	private Double gyzjrfz2;
	private Double gyzjrfz3;
	//持有至到期投资
	private Double cyzdqtz1;
	private Double cyzdqtz2;
	private Double cyzdrtz3;
	//可供出售金融资产
	private Double kcsjrzc1;
	private Double kcsjrzc2;
	private Double kcsjrzc3;
	// 其他收益项目
	private Double qtsyxm1;
	private Double qtsyxm2;
	private Double qtsyxm3;
	//（三）债务成本
	private Double zwcb1;
	private Double zwcb2;
	private Double zwcb3;
	//其中：费用化利息
	private Double fyhlx1;
	private Double fyhlx2;
	private Double fyhlx3;
	//资本化利息
	private Double zbhlx1;
	private Double zbhlx2;
	private Double zbhlx3;
	//（四）国拨资金及自筹项目情况
	private Double gbzjzcxm1;
	private Double gbzjzcxm2;
	private Double gbzjzcxm3;
	// 收到国拨资金总额
	private Double gbzjze1;
	private Double gbzjze2;
	private Double gbzjze3;
	// 其中：基本条件建设拨款
	private Double jbtjjsbk1;
	private Double jbtjjsbk2;
	private Double jbtjjsbk3;
	// 科研试制费拨款
	private Double kyszfbk1;
	private Double kyszfbk2;
	private Double kyszfbk3;
	//国拨资金支出总额
	private Double gbzjzcze1;
	private Double gbzjzcze2;
	private Double gbzjzcze3;
	// 其中：基本条件建设支出
	private Double jbtjjszc1;
	private Double jbtjjszc2;
	private Double jbtjjszc3;
	//科研试制费支出
	private Double kyszfzc1;
	private Double kyszfzc2;
	private Double kyszfzc3;
	//企业自筹资本性支出
	private Double qyzczbxzc1;
	private Double qyzczbxzc2;
	private Double qyzczbxzc3;
	//其中：基本条件建设企业配套
	private Double jbtjjs1;
	private Double jbtjjs2;
	private Double jbtjjs3;
	//其他自筹项目
	private Double qtzc1;
	private Double qtzc2;
	private Double qtzc3;
	///（五）对外捐赠支出总额
	private Double qzzc1;
	private Double qzzc2;
	private Double qzzc3;
	//（六）职工培训费用
	private Double zgpxfy1;
	private Double zgpxfy2;
	private Double zgpxfy3;
	//（七）隐性债务
	private Double yxzw1;
	private Double yxzw2;
	private Double yxzw3;
	// 1.对外担保
	private Double dwdb1;
	private Double dwdb2;
	private Double dwdb3;
	//2.抵质押资产
	private Double dzyzc1;
	private Double dzyzc2;
	private Double dzyzc3;
	// 3.或有负债
	private Double hyfz1;
	private Double hyfz2;
	private Double hyfz3;
	//4.优先股
	private Double yxg1;
	private Double yxg2;
	private Double yxg3;
	//5.永续债券
	private Double yxzq1;
	private Double yxzq2;
	private Double yxzq3;
	//（八）逾期债务
	private Double yqzw1;
	private Double yqzw2;
	private Double yqzw3;
	//1.逾期银行借款
	private Double yqyhjk1;
	private Double yqyhjk2;
	private Double yqyhjk3;
	//  2.逾期应付账款
	private Double yqyfzk1;
	private Double yqyfzk2;
	private Double yqyfzk3;
	//3.逾期对外担保
	private Double yqdwdb1;
	private Double yqdwdb2;
	private Double yqdwdb3;
	//（九）资金情况
	private Double zjqk1;
	private Double zjqk2;
	private Double zjqk3;
	// 1.资金集中度（%）
	private Double zjjzd1;
	private Double zjjzd2;
	private Double zjjzd3;
	//其中：通过财务公司归集的资金
	private Double cwgsgjzj1;
	private Double cwgsgjzj2;
	private Double cwgsgjzj3;
	//通过资金结算中心归集的资金
	private Double zjjsgjzj1;
	private Double zjjsgjzj2;
	private Double zjjsgjzj3;
	// 2.银行抽贷总额
	private Double yhcdze1;
	private Double yhcdze2;
	private Double yhcdze3;
	/****二、其他指标****/
	//（一）工业总产值（现行价格）
	private Double gyzcz1;
	private Double gyzcz2;
	private Double gyzcz3;
	//（二）劳动生产总值（现行价格）
	private Double ldsczz1;
	private Double ldsczz2;
	private Double ldsczz3;
	//（三）工业销售产值
	private Double gyxscz1;
	private Double gyxscz2;
	private Double gyxscz3;
	//（四）新产品产值
	private Double xcpcz1;
	private Double xcpcz2;
	private Double xcpcz3;
	//（五）本期支出的节能减排费用
	private Double jnjpf1;
	private Double jnjpf2;
	private Double jnjpf3;
	//（六）手持订单额
	private Double scdde1;
	private Double scdde2;
	private Double scdde3;
	//其中：国内订单
	private Double gndd1;
	private Double gndd2;
	private Double gndd3;
	//其中：军品订单
	private Double jpdd1;
	private Double jpdd2;
	private Double jpdd3;
	// 民品订单
	private Double mpdd1;
	private Double mpdd2;
	private Double mpdd3;
	// 国外订单
	private Double gwdd1;
	private Double gwdd2;
	private Double gwdd3;
	//其中：军贸订单
	private Double jmdd1;
	private Double jmdd2;
	private Double jmdd3;
	//民品订单
	private Double gwmpdd1;
	private Double gwmpdd2;
	private Double gwmpdd3;
	//（七）固定资产投资额
	private Double gdzctze1;
	private Double gdzctze2;
	private Double gdzctze3;
	//（八）出口产品销售收入
	private Double ckcpxssr1;
	private Double ckcpxssr2;
	private Double ckcpxssr3;
	//（九）银行借款
	private Double yhjk1;
	private Double yhjk2;
	private Double yhjk3;
	//（十）应交税费
	private Double yjsf1;
	private Double yjsf2;
	private Double yjsf3;
	// 1.应交增值税
	private Double yjzzs1;
	private Double yjzzs2;
	private Double yjzzs3;
	// 2.应交消费税
	private Double yjxfs1;
	private Double yjxfs2;
	private Double yjxfs3;
	//3.应交所得税
	private Double yjsds1;
	private Double yjsds2;
	private Double yjsds3;
	//（十一）已交税费
	private Double yijsf1;
	private Double yijsf2;
	private Double yijsf3;
	//1.已交增值税
	private Double yijzzs1;
	private Double yijzzs2;
	private Double yijzzs3;
	// 2.已交消费税
	private Double yijxfs1;
	private Double yijxfs2;
	private Double yijxfs3;
	//3.已交所得税
	private Double yijsds1;
	private Double yijsds2;
	private Double yijsds3;
	//（十二）投资额
	private Double tze1;
	private Double tze2;
	private Double tze3;
	//其中：科研生产投资额
	private Double kysctz1;
	private Double kysctz2;
	private Double kysctz3;
	//其中：军品科研生产投资额
	private Double jpkysctz1;
	private Double jpkysctz2;
	private Double jpkysctz3;
	//（十三）战略性新兴产业产品销售收入
	private Double zlxcpsr1;
	private Double zlxcpsr2;
	private Double zlxcpsr3;
	//（十四）国防工业维护与服务市场收入
	private Double gywhyfwsc1;
	private Double gywhyfwsc2;
	private Double gywhyfwsc3;
	//（十五）利润总额预算数
	private Double lrzeyss1;
	private Double lrzeyss2;
	private Double lrzeyss3;
	//（十六）净利润预算数
	private Double jlryss1;
	private Double jlryss2;
	private Double jlryss3;
	//（十七）企业研发投入
	private Double qyyftr1;
	private Double qyyftr2;
	private Double qyyftr3;
	//其中：政府拨款
	private Double zfbk1;
	private Double zfbk2;
	private Double zfbk3;
	// 企业自筹
	private Double qyzc1;
	private Double qyzc2;
	private Double qyzc3;
	//（十八）节能减排指标
	private Double jnjpzb1;
	private Double jnjpzb2;
	private Double jnjpzb3;
	// 1.节能减排投入总额
	private Double jnjptrze1;
	private Double jnjptrze2;
	private Double jnjptrze3;
	//其中：政府拨款
	private Double jnzfbk1;
	private Double jnzfbk2;
	private Double jnzfbk3;
	//企业自筹
	private Double jnqyzc1;
	private Double jnqyzc2;
	private Double jnqyzc3;
	// 2.万元产值综合能耗（吨标煤）
	private Double wrczzhnh1;
	private Double wrczzhnh2;
	private Double wrczzhnh3;
	//3.吨钢综合能耗（千克标煤／吨）
	private Double dgzhnh1;
	private Double dgzhnh2;
	private Double dgzhnh3;
	//4.供电煤耗（克／千瓦时）
	private Double gdhf1;
	private Double gdhf2;
	private Double gdhf3;
	//（十九）军用固定资产
	private Double jygdzc1;
	private Double jygdzc2;
	private Double jygdzc3;
	//（二十）商业保险费用全年预计
	private Double sybxf1;
	private Double sybxf2;
	private Double sybxf3;
	// 商业保险费用实际支出
	private Double sybxfsjzc1;
	private Double sybxfsjzc2;
	private Double sybxfsjzc3;
	//其中：财产险费用
	private Double ccxfy1;
	private Double ccxfy2;
	private Double ccxfy3;
	// 车险费用
	private Double cxfy1;
	private Double cxfy2;
	private Double cxfy3;
	//人身险费用（不含社保五险）
	private Double rsxfy1;
	private Double rsxfy2;
	private Double rsxfy3;
	//（二十一）零余额账户用款额度
	private Double lyezwyked1;
	private Double lyezwyked2;
	private Double lyezwyked3;
	// 年初结余（财政应返还额度年初数）
	private Double ncjy1;
	private Double ncjy2;
	private Double ncjy3;
	// 当年新增
	private Double dnxz1;
	private Double dnxz2;
	private Double dnxz3;
	// 当年支付
	private Double dnzf1;
	private Double dnzf2;
	private Double dnzf3;
	//当年结余
	private Double dnjy1;
	private Double dnjy2;
	private Double dnjy3;
	//预计下月支付
	private Double yjxyzf1;
	private Double yjxyzf2;
	private Double yjxyzf3;
	//（二十二）出口额
	private Double cke1;
	private Double cke2;
	private Double cke3;
	//其中：军品出口额
	private Double jpcke1;
	private Double jpcke2;
	private Double jpcke3;
	//民品出口额
	private Double mpcke1;
	private Double mpcke2;
	private Double mpcke3;
	//（二十三）军品应收账款
	private Double jpyszk1;
	private Double jpyszk2;
	private Double jpyszk3;
	//（二十四）军品应付账款
	private Double jpyfzk1;
	private Double jpyfzk2;
	private Double jpyfzk3;
	//（二十五）军品存货
	private Double jpch1;
	private Double jpch2;
	private Double jpch3;
	//其中：产成品
	private Double ccp1;
	private Double ccp2;
	private Double ccp3;
	//（二十六）事业单位结余（科研事业单位填列）
	private Double sydwjy1;
	private Double sydwjy2;
	private Double sydwjy3;
	//其中：科研项目阶段完成结余
	private Double kyxmjdwcjy1;
	private Double kyxmjdwcjy2;
	private Double kyxmjdwcjy3;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public Double getGdzczj1() {
		return gdzczj1;
	}
	public void setGdzczj1(Double gdzczj1) {
		this.gdzczj1 = gdzczj1;
	}
	public Double getGdzczj2() {
		return gdzczj2;
	}
	public void setGdzczj2(Double gdzczj2) {
		this.gdzczj2 = gdzczj2;
	}
	public Double getGdzczj3() {
		return gdzczj3;
	}
	public void setGdzczj3(Double gdzczj3) {
		this.gdzczj3 = gdzczj3;
	}
	public Double getTzsy1() {
		return tzsy1;
	}
	public void setTzsy1(Double tzsy1) {
		this.tzsy1 = tzsy1;
	}
	public Double getTzsy2() {
		return tzsy2;
	}
	public void setTzsy2(Double tzsy2) {
		this.tzsy2 = tzsy2;
	}
	public Double getTzsy3() {
		return tzsy3;
	}
	public void setTzsy3(Double tzsy3) {
		this.tzsy3 = tzsy3;
	}
	public Double getCqgqtz1() {
		return cqgqtz1;
	}
	public void setCqgqtz1(Double cqgqtz1) {
		this.cqgqtz1 = cqgqtz1;
	}
	public Double getCqgqtz2() {
		return cqgqtz2;
	}
	public void setCqgqtz2(Double cqgqtz2) {
		this.cqgqtz2 = cqgqtz2;
	}
	public Double getCqgqtz3() {
		return cqgqtz3;
	}
	public void setCqgqtz3(Double cqgqtz3) {
		this.cqgqtz3 = cqgqtz3;
	}
	public Double getGyzjrzc1() {
		return gyzjrzc1;
	}
	public void setGyzjrzc1(Double gyzjrzc1) {
		this.gyzjrzc1 = gyzjrzc1;
	}
	public Double getGyzjrzc2() {
		return gyzjrzc2;
	}
	public void setGyzjrzc2(Double gyzjrzc2) {
		this.gyzjrzc2 = gyzjrzc2;
	}
	public Double getGyzjrzc3() {
		return gyzjrzc3;
	}
	public void setGyzjrzc3(Double gyzjrzc3) {
		this.gyzjrzc3 = gyzjrzc3;
	}
	public Double getGyzjrfz1() {
		return gyzjrfz1;
	}
	public void setGyzjrfz1(Double gyzjrfz1) {
		this.gyzjrfz1 = gyzjrfz1;
	}
	public Double getGyzjrfz2() {
		return gyzjrfz2;
	}
	public void setGyzjrfz2(Double gyzjrfz2) {
		this.gyzjrfz2 = gyzjrfz2;
	}
	public Double getGyzjrfz3() {
		return gyzjrfz3;
	}
	public void setGyzjrfz3(Double gyzjrfz3) {
		this.gyzjrfz3 = gyzjrfz3;
	}
	public Double getCyzdqtz1() {
		return cyzdqtz1;
	}
	public void setCyzdqtz1(Double cyzdqtz1) {
		this.cyzdqtz1 = cyzdqtz1;
	}
	public Double getCyzdqtz2() {
		return cyzdqtz2;
	}
	public void setCyzdqtz2(Double cyzdqtz2) {
		this.cyzdqtz2 = cyzdqtz2;
	}
	public Double getCyzdrtz3() {
		return cyzdrtz3;
	}
	public void setCyzdrtz3(Double cyzdrtz3) {
		this.cyzdrtz3 = cyzdrtz3;
	}
	public Double getKcsjrzc1() {
		return kcsjrzc1;
	}
	public void setKcsjrzc1(Double kcsjrzc1) {
		this.kcsjrzc1 = kcsjrzc1;
	}
	public Double getKcsjrzc2() {
		return kcsjrzc2;
	}
	public void setKcsjrzc2(Double kcsjrzc2) {
		this.kcsjrzc2 = kcsjrzc2;
	}
	public Double getKcsjrzc3() {
		return kcsjrzc3;
	}
	public void setKcsjrzc3(Double kcsjrzc3) {
		this.kcsjrzc3 = kcsjrzc3;
	}
	public Double getQtsyxm1() {
		return qtsyxm1;
	}
	public void setQtsyxm1(Double qtsyxm1) {
		this.qtsyxm1 = qtsyxm1;
	}
	public Double getQtsyxm2() {
		return qtsyxm2;
	}
	public void setQtsyxm2(Double qtsyxm2) {
		this.qtsyxm2 = qtsyxm2;
	}
	public Double getQtsyxm3() {
		return qtsyxm3;
	}
	public void setQtsyxm3(Double qtsyxm3) {
		this.qtsyxm3 = qtsyxm3;
	}
	public Double getZwcb1() {
		return zwcb1;
	}
	public void setZwcb1(Double zwcb1) {
		this.zwcb1 = zwcb1;
	}
	public Double getZwcb2() {
		return zwcb2;
	}
	public void setZwcb2(Double zwcb2) {
		this.zwcb2 = zwcb2;
	}
	public Double getZwcb3() {
		return zwcb3;
	}
	public void setZwcb3(Double zwcb3) {
		this.zwcb3 = zwcb3;
	}
	public Double getFyhlx1() {
		return fyhlx1;
	}
	public void setFyhlx1(Double fyhlx1) {
		this.fyhlx1 = fyhlx1;
	}
	public Double getFyhlx2() {
		return fyhlx2;
	}
	public void setFyhlx2(Double fyhlx2) {
		this.fyhlx2 = fyhlx2;
	}
	public Double getFyhlx3() {
		return fyhlx3;
	}
	public void setFyhlx3(Double fyhlx3) {
		this.fyhlx3 = fyhlx3;
	}
	public Double getZbhlx1() {
		return zbhlx1;
	}
	public void setZbhlx1(Double zbhlx1) {
		this.zbhlx1 = zbhlx1;
	}
	public Double getZbhlx2() {
		return zbhlx2;
	}
	public void setZbhlx2(Double zbhlx2) {
		this.zbhlx2 = zbhlx2;
	}
	public Double getZbhlx3() {
		return zbhlx3;
	}
	public void setZbhlx3(Double zbhlx3) {
		this.zbhlx3 = zbhlx3;
	}
	public Double getGbzjzcxm1() {
		return gbzjzcxm1;
	}
	public void setGbzjzcxm1(Double gbzjzcxm1) {
		this.gbzjzcxm1 = gbzjzcxm1;
	}
	public Double getGbzjzcxm2() {
		return gbzjzcxm2;
	}
	public void setGbzjzcxm2(Double gbzjzcxm2) {
		this.gbzjzcxm2 = gbzjzcxm2;
	}
	public Double getGbzjzcxm3() {
		return gbzjzcxm3;
	}
	public void setGbzjzcxm3(Double gbzjzcxm3) {
		this.gbzjzcxm3 = gbzjzcxm3;
	}
	public Double getGbzjze1() {
		return gbzjze1;
	}
	public void setGbzjze1(Double gbzjze1) {
		this.gbzjze1 = gbzjze1;
	}
	public Double getGbzjze2() {
		return gbzjze2;
	}
	public void setGbzjze2(Double gbzjze2) {
		this.gbzjze2 = gbzjze2;
	}
	public Double getGbzjze3() {
		return gbzjze3;
	}
	public void setGbzjze3(Double gbzjze3) {
		this.gbzjze3 = gbzjze3;
	}
	public Double getJbtjjsbk1() {
		return jbtjjsbk1;
	}
	public void setJbtjjsbk1(Double jbtjjsbk1) {
		this.jbtjjsbk1 = jbtjjsbk1;
	}
	public Double getJbtjjsbk2() {
		return jbtjjsbk2;
	}
	public void setJbtjjsbk2(Double jbtjjsbk2) {
		this.jbtjjsbk2 = jbtjjsbk2;
	}
	public Double getJbtjjsbk3() {
		return jbtjjsbk3;
	}
	public void setJbtjjsbk3(Double jbtjjsbk3) {
		this.jbtjjsbk3 = jbtjjsbk3;
	}
	public Double getKyszfbk1() {
		return kyszfbk1;
	}
	public void setKyszfbk1(Double kyszfbk1) {
		this.kyszfbk1 = kyszfbk1;
	}
	public Double getKyszfbk2() {
		return kyszfbk2;
	}
	public void setKyszfbk2(Double kyszfbk2) {
		this.kyszfbk2 = kyszfbk2;
	}
	public Double getKyszfbk3() {
		return kyszfbk3;
	}
	public void setKyszfbk3(Double kyszfbk3) {
		this.kyszfbk3 = kyszfbk3;
	}
	public Double getGbzjzcze1() {
		return gbzjzcze1;
	}
	public void setGbzjzcze1(Double gbzjzcze1) {
		this.gbzjzcze1 = gbzjzcze1;
	}
	public Double getGbzjzcze2() {
		return gbzjzcze2;
	}
	public void setGbzjzcze2(Double gbzjzcze2) {
		this.gbzjzcze2 = gbzjzcze2;
	}
	public Double getGbzjzcze3() {
		return gbzjzcze3;
	}
	public void setGbzjzcze3(Double gbzjzcze3) {
		this.gbzjzcze3 = gbzjzcze3;
	}
	public Double getJbtjjszc1() {
		return jbtjjszc1;
	}
	public void setJbtjjszc1(Double jbtjjszc1) {
		this.jbtjjszc1 = jbtjjszc1;
	}
	public Double getJbtjjszc2() {
		return jbtjjszc2;
	}
	public void setJbtjjszc2(Double jbtjjszc2) {
		this.jbtjjszc2 = jbtjjszc2;
	}
	public Double getJbtjjszc3() {
		return jbtjjszc3;
	}
	public void setJbtjjszc3(Double jbtjjszc3) {
		this.jbtjjszc3 = jbtjjszc3;
	}
	public Double getKyszfzc1() {
		return kyszfzc1;
	}
	public void setKyszfzc1(Double kyszfzc1) {
		this.kyszfzc1 = kyszfzc1;
	}
	public Double getKyszfzc2() {
		return kyszfzc2;
	}
	public void setKyszfzc2(Double kyszfzc2) {
		this.kyszfzc2 = kyszfzc2;
	}
	public Double getKyszfzc3() {
		return kyszfzc3;
	}
	public void setKyszfzc3(Double kyszfzc3) {
		this.kyszfzc3 = kyszfzc3;
	}
	public Double getQyzczbxzc1() {
		return qyzczbxzc1;
	}
	public void setQyzczbxzc1(Double qyzczbxzc1) {
		this.qyzczbxzc1 = qyzczbxzc1;
	}
	public Double getQyzczbxzc2() {
		return qyzczbxzc2;
	}
	public void setQyzczbxzc2(Double qyzczbxzc2) {
		this.qyzczbxzc2 = qyzczbxzc2;
	}
	public Double getQyzczbxzc3() {
		return qyzczbxzc3;
	}
	public void setQyzczbxzc3(Double qyzczbxzc3) {
		this.qyzczbxzc3 = qyzczbxzc3;
	}
	public Double getJbtjjs1() {
		return jbtjjs1;
	}
	public void setJbtjjs1(Double jbtjjs1) {
		this.jbtjjs1 = jbtjjs1;
	}
	public Double getJbtjjs2() {
		return jbtjjs2;
	}
	public void setJbtjjs2(Double jbtjjs2) {
		this.jbtjjs2 = jbtjjs2;
	}
	public Double getJbtjjs3() {
		return jbtjjs3;
	}
	public void setJbtjjs3(Double jbtjjs3) {
		this.jbtjjs3 = jbtjjs3;
	}
	public Double getQtzc1() {
		return qtzc1;
	}
	public void setQtzc1(Double qtzc1) {
		this.qtzc1 = qtzc1;
	}
	public Double getQtzc2() {
		return qtzc2;
	}
	public void setQtzc2(Double qtzc2) {
		this.qtzc2 = qtzc2;
	}
	public Double getQtzc3() {
		return qtzc3;
	}
	public void setQtzc3(Double qtzc3) {
		this.qtzc3 = qtzc3;
	}
	public Double getQzzc1() {
		return qzzc1;
	}
	public void setQzzc1(Double qzzc1) {
		this.qzzc1 = qzzc1;
	}
	public Double getQzzc2() {
		return qzzc2;
	}
	public void setQzzc2(Double qzzc2) {
		this.qzzc2 = qzzc2;
	}
	public Double getQzzc3() {
		return qzzc3;
	}
	public void setQzzc3(Double qzzc3) {
		this.qzzc3 = qzzc3;
	}
	public Double getZgpxfy1() {
		return zgpxfy1;
	}
	public void setZgpxfy1(Double zgpxfy1) {
		this.zgpxfy1 = zgpxfy1;
	}
	public Double getZgpxfy2() {
		return zgpxfy2;
	}
	public void setZgpxfy2(Double zgpxfy2) {
		this.zgpxfy2 = zgpxfy2;
	}
	public Double getZgpxfy3() {
		return zgpxfy3;
	}
	public void setZgpxfy3(Double zgpxfy3) {
		this.zgpxfy3 = zgpxfy3;
	}
	public Double getYxzw1() {
		return yxzw1;
	}
	public void setYxzw1(Double yxzw1) {
		this.yxzw1 = yxzw1;
	}
	public Double getYxzw2() {
		return yxzw2;
	}
	public void setYxzw2(Double yxzw2) {
		this.yxzw2 = yxzw2;
	}
	public Double getYxzw3() {
		return yxzw3;
	}
	public void setYxzw3(Double yxzw3) {
		this.yxzw3 = yxzw3;
	}
	public Double getDwdb1() {
		return dwdb1;
	}
	public void setDwdb1(Double dwdb1) {
		this.dwdb1 = dwdb1;
	}
	public Double getDwdb2() {
		return dwdb2;
	}
	public void setDwdb2(Double dwdb2) {
		this.dwdb2 = dwdb2;
	}
	public Double getDwdb3() {
		return dwdb3;
	}
	public void setDwdb3(Double dwdb3) {
		this.dwdb3 = dwdb3;
	}
	public Double getDzyzc1() {
		return dzyzc1;
	}
	public void setDzyzc1(Double dzyzc1) {
		this.dzyzc1 = dzyzc1;
	}
	public Double getDzyzc2() {
		return dzyzc2;
	}
	public void setDzyzc2(Double dzyzc2) {
		this.dzyzc2 = dzyzc2;
	}
	public Double getDzyzc3() {
		return dzyzc3;
	}
	public void setDzyzc3(Double dzyzc3) {
		this.dzyzc3 = dzyzc3;
	}
	public Double getHyfz1() {
		return hyfz1;
	}
	public void setHyfz1(Double hyfz1) {
		this.hyfz1 = hyfz1;
	}
	public Double getHyfz2() {
		return hyfz2;
	}
	public void setHyfz2(Double hyfz2) {
		this.hyfz2 = hyfz2;
	}
	public Double getHyfz3() {
		return hyfz3;
	}
	public void setHyfz3(Double hyfz3) {
		this.hyfz3 = hyfz3;
	}
	public Double getYxg1() {
		return yxg1;
	}
	public void setYxg1(Double yxg1) {
		this.yxg1 = yxg1;
	}
	public Double getYxg2() {
		return yxg2;
	}
	public void setYxg2(Double yxg2) {
		this.yxg2 = yxg2;
	}
	public Double getYxg3() {
		return yxg3;
	}
	public void setYxg3(Double yxg3) {
		this.yxg3 = yxg3;
	}
	public Double getYxzq1() {
		return yxzq1;
	}
	public void setYxzq1(Double yxzq1) {
		this.yxzq1 = yxzq1;
	}
	public Double getYxzq2() {
		return yxzq2;
	}
	public void setYxzq2(Double yxzq2) {
		this.yxzq2 = yxzq2;
	}
	public Double getYxzq3() {
		return yxzq3;
	}
	public void setYxzq3(Double yxzq3) {
		this.yxzq3 = yxzq3;
	}
	public Double getYqzw1() {
		return yqzw1;
	}
	public void setYqzw1(Double yqzw1) {
		this.yqzw1 = yqzw1;
	}
	public Double getYqzw2() {
		return yqzw2;
	}
	public void setYqzw2(Double yqzw2) {
		this.yqzw2 = yqzw2;
	}
	public Double getYqzw3() {
		return yqzw3;
	}
	public void setYqzw3(Double yqzw3) {
		this.yqzw3 = yqzw3;
	}
	public Double getYqyhjk1() {
		return yqyhjk1;
	}
	public void setYqyhjk1(Double yqyhjk1) {
		this.yqyhjk1 = yqyhjk1;
	}
	public Double getYqyhjk2() {
		return yqyhjk2;
	}
	public void setYqyhjk2(Double yqyhjk2) {
		this.yqyhjk2 = yqyhjk2;
	}
	public Double getYqyhjk3() {
		return yqyhjk3;
	}
	public void setYqyhjk3(Double yqyhjk3) {
		this.yqyhjk3 = yqyhjk3;
	}
	public Double getYqyfzk1() {
		return yqyfzk1;
	}
	public void setYqyfzk1(Double yqyfzk1) {
		this.yqyfzk1 = yqyfzk1;
	}
	public Double getYqyfzk2() {
		return yqyfzk2;
	}
	public void setYqyfzk2(Double yqyfzk2) {
		this.yqyfzk2 = yqyfzk2;
	}
	public Double getYqyfzk3() {
		return yqyfzk3;
	}
	public void setYqyfzk3(Double yqyfzk3) {
		this.yqyfzk3 = yqyfzk3;
	}
	public Double getYqdwdb1() {
		return yqdwdb1;
	}
	public void setYqdwdb1(Double yqdwdb1) {
		this.yqdwdb1 = yqdwdb1;
	}
	public Double getYqdwdb2() {
		return yqdwdb2;
	}
	public void setYqdwdb2(Double yqdwdb2) {
		this.yqdwdb2 = yqdwdb2;
	}
	public Double getYqdwdb3() {
		return yqdwdb3;
	}
	public void setYqdwdb3(Double yqdwdb3) {
		this.yqdwdb3 = yqdwdb3;
	}
	public Double getZjqk1() {
		return zjqk1;
	}
	public void setZjqk1(Double zjqk1) {
		this.zjqk1 = zjqk1;
	}
	public Double getZjqk2() {
		return zjqk2;
	}
	public void setZjqk2(Double zjqk2) {
		this.zjqk2 = zjqk2;
	}
	public Double getZjqk3() {
		return zjqk3;
	}
	public void setZjqk3(Double zjqk3) {
		this.zjqk3 = zjqk3;
	}
	public Double getZjjzd1() {
		return zjjzd1;
	}
	public void setZjjzd1(Double zjjzd1) {
		this.zjjzd1 = zjjzd1;
	}
	public Double getZjjzd2() {
		return zjjzd2;
	}
	public void setZjjzd2(Double zjjzd2) {
		this.zjjzd2 = zjjzd2;
	}
	public Double getZjjzd3() {
		return zjjzd3;
	}
	public void setZjjzd3(Double zjjzd3) {
		this.zjjzd3 = zjjzd3;
	}
	public Double getCwgsgjzj1() {
		return cwgsgjzj1;
	}
	public void setCwgsgjzj1(Double cwgsgjzj1) {
		this.cwgsgjzj1 = cwgsgjzj1;
	}
	public Double getCwgsgjzj2() {
		return cwgsgjzj2;
	}
	public void setCwgsgjzj2(Double cwgsgjzj2) {
		this.cwgsgjzj2 = cwgsgjzj2;
	}
	public Double getCwgsgjzj3() {
		return cwgsgjzj3;
	}
	public void setCwgsgjzj3(Double cwgsgjzj3) {
		this.cwgsgjzj3 = cwgsgjzj3;
	}
	public Double getZjjsgjzj1() {
		return zjjsgjzj1;
	}
	public void setZjjsgjzj1(Double zjjsgjzj1) {
		this.zjjsgjzj1 = zjjsgjzj1;
	}
	public Double getZjjsgjzj2() {
		return zjjsgjzj2;
	}
	public void setZjjsgjzj2(Double zjjsgjzj2) {
		this.zjjsgjzj2 = zjjsgjzj2;
	}
	public Double getZjjsgjzj3() {
		return zjjsgjzj3;
	}
	public void setZjjsgjzj3(Double zjjsgjzj3) {
		this.zjjsgjzj3 = zjjsgjzj3;
	}
	public Double getYhcdze1() {
		return yhcdze1;
	}
	public void setYhcdze1(Double yhcdze1) {
		this.yhcdze1 = yhcdze1;
	}
	public Double getYhcdze2() {
		return yhcdze2;
	}
	public void setYhcdze2(Double yhcdze2) {
		this.yhcdze2 = yhcdze2;
	}
	public Double getYhcdze3() {
		return yhcdze3;
	}
	public void setYhcdze3(Double yhcdze3) {
		this.yhcdze3 = yhcdze3;
	}
	public Double getGyzcz1() {
		return gyzcz1;
	}
	public void setGyzcz1(Double gyzcz1) {
		this.gyzcz1 = gyzcz1;
	}
	public Double getGyzcz2() {
		return gyzcz2;
	}
	public void setGyzcz2(Double gyzcz2) {
		this.gyzcz2 = gyzcz2;
	}
	public Double getGyzcz3() {
		return gyzcz3;
	}
	public void setGyzcz3(Double gyzcz3) {
		this.gyzcz3 = gyzcz3;
	}
	public Double getLdsczz1() {
		return ldsczz1;
	}
	public void setLdsczz1(Double ldsczz1) {
		this.ldsczz1 = ldsczz1;
	}
	public Double getLdsczz2() {
		return ldsczz2;
	}
	public void setLdsczz2(Double ldsczz2) {
		this.ldsczz2 = ldsczz2;
	}
	public Double getLdsczz3() {
		return ldsczz3;
	}
	public void setLdsczz3(Double ldsczz3) {
		this.ldsczz3 = ldsczz3;
	}
	public Double getGyxscz1() {
		return gyxscz1;
	}
	public void setGyxscz1(Double gyxscz1) {
		this.gyxscz1 = gyxscz1;
	}
	public Double getGyxscz2() {
		return gyxscz2;
	}
	public void setGyxscz2(Double gyxscz2) {
		this.gyxscz2 = gyxscz2;
	}
	public Double getGyxscz3() {
		return gyxscz3;
	}
	public void setGyxscz3(Double gyxscz3) {
		this.gyxscz3 = gyxscz3;
	}
	public Double getXcpcz1() {
		return xcpcz1;
	}
	public void setXcpcz1(Double xcpcz1) {
		this.xcpcz1 = xcpcz1;
	}
	public Double getXcpcz2() {
		return xcpcz2;
	}
	public void setXcpcz2(Double xcpcz2) {
		this.xcpcz2 = xcpcz2;
	}
	public Double getXcpcz3() {
		return xcpcz3;
	}
	public void setXcpcz3(Double xcpcz3) {
		this.xcpcz3 = xcpcz3;
	}
	public Double getJnjpf1() {
		return jnjpf1;
	}
	public void setJnjpf1(Double jnjpf1) {
		this.jnjpf1 = jnjpf1;
	}
	public Double getJnjpf2() {
		return jnjpf2;
	}
	public void setJnjpf2(Double jnjpf2) {
		this.jnjpf2 = jnjpf2;
	}
	public Double getJnjpf3() {
		return jnjpf3;
	}
	public void setJnjpf3(Double jnjpf3) {
		this.jnjpf3 = jnjpf3;
	}
	public Double getScdde1() {
		return scdde1;
	}
	public void setScdde1(Double scdde1) {
		this.scdde1 = scdde1;
	}
	public Double getScdde2() {
		return scdde2;
	}
	public void setScdde2(Double scdde2) {
		this.scdde2 = scdde2;
	}
	public Double getScdde3() {
		return scdde3;
	}
	public void setScdde3(Double scdde3) {
		this.scdde3 = scdde3;
	}
	public Double getGndd1() {
		return gndd1;
	}
	public void setGndd1(Double gndd1) {
		this.gndd1 = gndd1;
	}
	public Double getGndd2() {
		return gndd2;
	}
	public void setGndd2(Double gndd2) {
		this.gndd2 = gndd2;
	}
	public Double getGndd3() {
		return gndd3;
	}
	public void setGndd3(Double gndd3) {
		this.gndd3 = gndd3;
	}
	public Double getJpdd1() {
		return jpdd1;
	}
	public void setJpdd1(Double jpdd1) {
		this.jpdd1 = jpdd1;
	}
	public Double getJpdd2() {
		return jpdd2;
	}
	public void setJpdd2(Double jpdd2) {
		this.jpdd2 = jpdd2;
	}
	public Double getJpdd3() {
		return jpdd3;
	}
	public void setJpdd3(Double jpdd3) {
		this.jpdd3 = jpdd3;
	}
	public Double getMpdd1() {
		return mpdd1;
	}
	public void setMpdd1(Double mpdd1) {
		this.mpdd1 = mpdd1;
	}
	public Double getMpdd2() {
		return mpdd2;
	}
	public void setMpdd2(Double mpdd2) {
		this.mpdd2 = mpdd2;
	}
	public Double getMpdd3() {
		return mpdd3;
	}
	public void setMpdd3(Double mpdd3) {
		this.mpdd3 = mpdd3;
	}
	public Double getGwdd1() {
		return gwdd1;
	}
	public void setGwdd1(Double gwdd1) {
		this.gwdd1 = gwdd1;
	}
	public Double getGwdd2() {
		return gwdd2;
	}
	public void setGwdd2(Double gwdd2) {
		this.gwdd2 = gwdd2;
	}
	public Double getGwdd3() {
		return gwdd3;
	}
	public void setGwdd3(Double gwdd3) {
		this.gwdd3 = gwdd3;
	}
	public Double getJmdd1() {
		return jmdd1;
	}
	public void setJmdd1(Double jmdd1) {
		this.jmdd1 = jmdd1;
	}
	public Double getJmdd2() {
		return jmdd2;
	}
	public void setJmdd2(Double jmdd2) {
		this.jmdd2 = jmdd2;
	}
	public Double getJmdd3() {
		return jmdd3;
	}
	public void setJmdd3(Double jmdd3) {
		this.jmdd3 = jmdd3;
	}
	public Double getGwmpdd1() {
		return gwmpdd1;
	}
	public void setGwmpdd1(Double gwmpdd1) {
		this.gwmpdd1 = gwmpdd1;
	}
	public Double getGwmpdd2() {
		return gwmpdd2;
	}
	public void setGwmpdd2(Double gwmpdd2) {
		this.gwmpdd2 = gwmpdd2;
	}
	public Double getGwmpdd3() {
		return gwmpdd3;
	}
	public void setGwmpdd3(Double gwmpdd3) {
		this.gwmpdd3 = gwmpdd3;
	}
	public Double getGdzctze1() {
		return gdzctze1;
	}
	public void setGdzctze1(Double gdzctze1) {
		this.gdzctze1 = gdzctze1;
	}
	public Double getGdzctze2() {
		return gdzctze2;
	}
	public void setGdzctze2(Double gdzctze2) {
		this.gdzctze2 = gdzctze2;
	}
	public Double getGdzctze3() {
		return gdzctze3;
	}
	public void setGdzctze3(Double gdzctze3) {
		this.gdzctze3 = gdzctze3;
	}
	public Double getCkcpxssr1() {
		return ckcpxssr1;
	}
	public void setCkcpxssr1(Double ckcpxssr1) {
		this.ckcpxssr1 = ckcpxssr1;
	}
	public Double getCkcpxssr2() {
		return ckcpxssr2;
	}
	public void setCkcpxssr2(Double ckcpxssr2) {
		this.ckcpxssr2 = ckcpxssr2;
	}
	public Double getCkcpxssr3() {
		return ckcpxssr3;
	}
	public void setCkcpxssr3(Double ckcpxssr3) {
		this.ckcpxssr3 = ckcpxssr3;
	}
	public Double getYhjk1() {
		return yhjk1;
	}
	public void setYhjk1(Double yhjk1) {
		this.yhjk1 = yhjk1;
	}
	public Double getYhjk2() {
		return yhjk2;
	}
	public void setYhjk2(Double yhjk2) {
		this.yhjk2 = yhjk2;
	}
	public Double getYhjk3() {
		return yhjk3;
	}
	public void setYhjk3(Double yhjk3) {
		this.yhjk3 = yhjk3;
	}
	public Double getYjsf1() {
		return yjsf1;
	}
	public void setYjsf1(Double yjsf1) {
		this.yjsf1 = yjsf1;
	}
	public Double getYjsf2() {
		return yjsf2;
	}
	public void setYjsf2(Double yjsf2) {
		this.yjsf2 = yjsf2;
	}
	public Double getYjsf3() {
		return yjsf3;
	}
	public void setYjsf3(Double yjsf3) {
		this.yjsf3 = yjsf3;
	}
	public Double getYjzzs1() {
		return yjzzs1;
	}
	public void setYjzzs1(Double yjzzs1) {
		this.yjzzs1 = yjzzs1;
	}
	public Double getYjzzs2() {
		return yjzzs2;
	}
	public void setYjzzs2(Double yjzzs2) {
		this.yjzzs2 = yjzzs2;
	}
	public Double getYjzzs3() {
		return yjzzs3;
	}
	public void setYjzzs3(Double yjzzs3) {
		this.yjzzs3 = yjzzs3;
	}
	public Double getYjxfs1() {
		return yjxfs1;
	}
	public void setYjxfs1(Double yjxfs1) {
		this.yjxfs1 = yjxfs1;
	}
	public Double getYjxfs2() {
		return yjxfs2;
	}
	public void setYjxfs2(Double yjxfs2) {
		this.yjxfs2 = yjxfs2;
	}
	public Double getYjxfs3() {
		return yjxfs3;
	}
	public void setYjxfs3(Double yjxfs3) {
		this.yjxfs3 = yjxfs3;
	}
	public Double getYjsds1() {
		return yjsds1;
	}
	public void setYjsds1(Double yjsds1) {
		this.yjsds1 = yjsds1;
	}
	public Double getYjsds2() {
		return yjsds2;
	}
	public void setYjsds2(Double yjsds2) {
		this.yjsds2 = yjsds2;
	}
	public Double getYjsds3() {
		return yjsds3;
	}
	public void setYjsds3(Double yjsds3) {
		this.yjsds3 = yjsds3;
	}
	public Double getYijsf1() {
		return yijsf1;
	}
	public void setYijsf1(Double yijsf1) {
		this.yijsf1 = yijsf1;
	}
	public Double getYijsf2() {
		return yijsf2;
	}
	public void setYijsf2(Double yijsf2) {
		this.yijsf2 = yijsf2;
	}
	public Double getYijsf3() {
		return yijsf3;
	}
	public void setYijsf3(Double yijsf3) {
		this.yijsf3 = yijsf3;
	}
	public Double getYijzzs1() {
		return yijzzs1;
	}
	public void setYijzzs1(Double yijzzs1) {
		this.yijzzs1 = yijzzs1;
	}
	public Double getYijzzs2() {
		return yijzzs2;
	}
	public void setYijzzs2(Double yijzzs2) {
		this.yijzzs2 = yijzzs2;
	}
	public Double getYijzzs3() {
		return yijzzs3;
	}
	public void setYijzzs3(Double yijzzs3) {
		this.yijzzs3 = yijzzs3;
	}
	public Double getYijxfs1() {
		return yijxfs1;
	}
	public void setYijxfs1(Double yijxfs1) {
		this.yijxfs1 = yijxfs1;
	}
	public Double getYijxfs2() {
		return yijxfs2;
	}
	public void setYijxfs2(Double yijxfs2) {
		this.yijxfs2 = yijxfs2;
	}
	public Double getYijxfs3() {
		return yijxfs3;
	}
	public void setYijxfs3(Double yijxfs3) {
		this.yijxfs3 = yijxfs3;
	}
	public Double getYijsds1() {
		return yijsds1;
	}
	public void setYijsds1(Double yijsds1) {
		this.yijsds1 = yijsds1;
	}
	public Double getYijsds2() {
		return yijsds2;
	}
	public void setYijsds2(Double yijsds2) {
		this.yijsds2 = yijsds2;
	}
	public Double getYijsds3() {
		return yijsds3;
	}
	public void setYijsds3(Double yijsds3) {
		this.yijsds3 = yijsds3;
	}
	public Double getTze1() {
		return tze1;
	}
	public void setTze1(Double tze1) {
		this.tze1 = tze1;
	}
	public Double getTze2() {
		return tze2;
	}
	public void setTze2(Double tze2) {
		this.tze2 = tze2;
	}
	public Double getTze3() {
		return tze3;
	}
	public void setTze3(Double tze3) {
		this.tze3 = tze3;
	}
	public Double getKysctz1() {
		return kysctz1;
	}
	public void setKysctz1(Double kysctz1) {
		this.kysctz1 = kysctz1;
	}
	public Double getKysctz2() {
		return kysctz2;
	}
	public void setKysctz2(Double kysctz2) {
		this.kysctz2 = kysctz2;
	}
	public Double getKysctz3() {
		return kysctz3;
	}
	public void setKysctz3(Double kysctz3) {
		this.kysctz3 = kysctz3;
	}
	public Double getJpkysctz1() {
		return jpkysctz1;
	}
	public void setJpkysctz1(Double jpkysctz1) {
		this.jpkysctz1 = jpkysctz1;
	}
	public Double getJpkysctz2() {
		return jpkysctz2;
	}
	public void setJpkysctz2(Double jpkysctz2) {
		this.jpkysctz2 = jpkysctz2;
	}
	public Double getJpkysctz3() {
		return jpkysctz3;
	}
	public void setJpkysctz3(Double jpkysctz3) {
		this.jpkysctz3 = jpkysctz3;
	}
	public Double getZlxcpsr1() {
		return zlxcpsr1;
	}
	public void setZlxcpsr1(Double zlxcpsr1) {
		this.zlxcpsr1 = zlxcpsr1;
	}
	public Double getZlxcpsr2() {
		return zlxcpsr2;
	}
	public void setZlxcpsr2(Double zlxcpsr2) {
		this.zlxcpsr2 = zlxcpsr2;
	}
	public Double getZlxcpsr3() {
		return zlxcpsr3;
	}
	public void setZlxcpsr3(Double zlxcpsr3) {
		this.zlxcpsr3 = zlxcpsr3;
	}
	public Double getGywhyfwsc1() {
		return gywhyfwsc1;
	}
	public void setGywhyfwsc1(Double gywhyfwsc1) {
		this.gywhyfwsc1 = gywhyfwsc1;
	}
	public Double getGywhyfwsc2() {
		return gywhyfwsc2;
	}
	public void setGywhyfwsc2(Double gywhyfwsc2) {
		this.gywhyfwsc2 = gywhyfwsc2;
	}
	public Double getGywhyfwsc3() {
		return gywhyfwsc3;
	}
	public void setGywhyfwsc3(Double gywhyfwsc3) {
		this.gywhyfwsc3 = gywhyfwsc3;
	}
	public Double getLrzeyss1() {
		return lrzeyss1;
	}
	public void setLrzeyss1(Double lrzeyss1) {
		this.lrzeyss1 = lrzeyss1;
	}
	public Double getLrzeyss2() {
		return lrzeyss2;
	}
	public void setLrzeyss2(Double lrzeyss2) {
		this.lrzeyss2 = lrzeyss2;
	}
	public Double getLrzeyss3() {
		return lrzeyss3;
	}
	public void setLrzeyss3(Double lrzeyss3) {
		this.lrzeyss3 = lrzeyss3;
	}
	public Double getJlryss1() {
		return jlryss1;
	}
	public void setJlryss1(Double jlryss1) {
		this.jlryss1 = jlryss1;
	}
	public Double getJlryss2() {
		return jlryss2;
	}
	public void setJlryss2(Double jlryss2) {
		this.jlryss2 = jlryss2;
	}
	public Double getJlryss3() {
		return jlryss3;
	}
	public void setJlryss3(Double jlryss3) {
		this.jlryss3 = jlryss3;
	}
	public Double getQyyftr1() {
		return qyyftr1;
	}
	public void setQyyftr1(Double qyyftr1) {
		this.qyyftr1 = qyyftr1;
	}
	public Double getQyyftr2() {
		return qyyftr2;
	}
	public void setQyyftr2(Double qyyftr2) {
		this.qyyftr2 = qyyftr2;
	}
	public Double getQyyftr3() {
		return qyyftr3;
	}
	public void setQyyftr3(Double qyyftr3) {
		this.qyyftr3 = qyyftr3;
	}
	public Double getZfbk1() {
		return zfbk1;
	}
	public void setZfbk1(Double zfbk1) {
		this.zfbk1 = zfbk1;
	}
	public Double getZfbk2() {
		return zfbk2;
	}
	public void setZfbk2(Double zfbk2) {
		this.zfbk2 = zfbk2;
	}
	public Double getZfbk3() {
		return zfbk3;
	}
	public void setZfbk3(Double zfbk3) {
		this.zfbk3 = zfbk3;
	}
	public Double getQyzc1() {
		return qyzc1;
	}
	public void setQyzc1(Double qyzc1) {
		this.qyzc1 = qyzc1;
	}
	public Double getQyzc2() {
		return qyzc2;
	}
	public void setQyzc2(Double qyzc2) {
		this.qyzc2 = qyzc2;
	}
	public Double getQyzc3() {
		return qyzc3;
	}
	public void setQyzc3(Double qyzc3) {
		this.qyzc3 = qyzc3;
	}
	public Double getJnjpzb1() {
		return jnjpzb1;
	}
	public void setJnjpzb1(Double jnjpzb1) {
		this.jnjpzb1 = jnjpzb1;
	}
	public Double getJnjpzb2() {
		return jnjpzb2;
	}
	public void setJnjpzb2(Double jnjpzb2) {
		this.jnjpzb2 = jnjpzb2;
	}
	public Double getJnjpzb3() {
		return jnjpzb3;
	}
	public void setJnjpzb3(Double jnjpzb3) {
		this.jnjpzb3 = jnjpzb3;
	}
	public Double getJnjptrze1() {
		return jnjptrze1;
	}
	public void setJnjptrze1(Double jnjptrze1) {
		this.jnjptrze1 = jnjptrze1;
	}
	public Double getJnjptrze2() {
		return jnjptrze2;
	}
	public void setJnjptrze2(Double jnjptrze2) {
		this.jnjptrze2 = jnjptrze2;
	}
	public Double getJnjptrze3() {
		return jnjptrze3;
	}
	public void setJnjptrze3(Double jnjptrze3) {
		this.jnjptrze3 = jnjptrze3;
	}
	public Double getJnzfbk1() {
		return jnzfbk1;
	}
	public void setJnzfbk1(Double jnzfbk1) {
		this.jnzfbk1 = jnzfbk1;
	}
	public Double getJnzfbk2() {
		return jnzfbk2;
	}
	public void setJnzfbk2(Double jnzfbk2) {
		this.jnzfbk2 = jnzfbk2;
	}
	public Double getJnzfbk3() {
		return jnzfbk3;
	}
	public void setJnzfbk3(Double jnzfbk3) {
		this.jnzfbk3 = jnzfbk3;
	}
	public Double getJnqyzc1() {
		return jnqyzc1;
	}
	public void setJnqyzc1(Double jnqyzc1) {
		this.jnqyzc1 = jnqyzc1;
	}
	public Double getJnqyzc2() {
		return jnqyzc2;
	}
	public void setJnqyzc2(Double jnqyzc2) {
		this.jnqyzc2 = jnqyzc2;
	}
	public Double getJnqyzc3() {
		return jnqyzc3;
	}
	public void setJnqyzc3(Double jnqyzc3) {
		this.jnqyzc3 = jnqyzc3;
	}
	public Double getWrczzhnh1() {
		return wrczzhnh1;
	}
	public void setWrczzhnh1(Double wrczzhnh1) {
		this.wrczzhnh1 = wrczzhnh1;
	}
	public Double getWrczzhnh2() {
		return wrczzhnh2;
	}
	public void setWrczzhnh2(Double wrczzhnh2) {
		this.wrczzhnh2 = wrczzhnh2;
	}
	public Double getWrczzhnh3() {
		return wrczzhnh3;
	}
	public void setWrczzhnh3(Double wrczzhnh3) {
		this.wrczzhnh3 = wrczzhnh3;
	}
	public Double getDgzhnh1() {
		return dgzhnh1;
	}
	public void setDgzhnh1(Double dgzhnh1) {
		this.dgzhnh1 = dgzhnh1;
	}
	public Double getDgzhnh2() {
		return dgzhnh2;
	}
	public void setDgzhnh2(Double dgzhnh2) {
		this.dgzhnh2 = dgzhnh2;
	}
	public Double getDgzhnh3() {
		return dgzhnh3;
	}
	public void setDgzhnh3(Double dgzhnh3) {
		this.dgzhnh3 = dgzhnh3;
	}
	public Double getGdhf1() {
		return gdhf1;
	}
	public void setGdhf1(Double gdhf1) {
		this.gdhf1 = gdhf1;
	}
	public Double getGdhf2() {
		return gdhf2;
	}
	public void setGdhf2(Double gdhf2) {
		this.gdhf2 = gdhf2;
	}
	public Double getGdhf3() {
		return gdhf3;
	}
	public void setGdhf3(Double gdhf3) {
		this.gdhf3 = gdhf3;
	}
	public Double getJygdzc1() {
		return jygdzc1;
	}
	public void setJygdzc1(Double jygdzc1) {
		this.jygdzc1 = jygdzc1;
	}
	public Double getJygdzc2() {
		return jygdzc2;
	}
	public void setJygdzc2(Double jygdzc2) {
		this.jygdzc2 = jygdzc2;
	}
	public Double getJygdzc3() {
		return jygdzc3;
	}
	public void setJygdzc3(Double jygdzc3) {
		this.jygdzc3 = jygdzc3;
	}
	public Double getSybxf1() {
		return sybxf1;
	}
	public void setSybxf1(Double sybxf1) {
		this.sybxf1 = sybxf1;
	}
	public Double getSybxf2() {
		return sybxf2;
	}
	public void setSybxf2(Double sybxf2) {
		this.sybxf2 = sybxf2;
	}
	public Double getSybxf3() {
		return sybxf3;
	}
	public void setSybxf3(Double sybxf3) {
		this.sybxf3 = sybxf3;
	}
	public Double getSybxfsjzc1() {
		return sybxfsjzc1;
	}
	public void setSybxfsjzc1(Double sybxfsjzc1) {
		this.sybxfsjzc1 = sybxfsjzc1;
	}
	public Double getSybxfsjzc2() {
		return sybxfsjzc2;
	}
	public void setSybxfsjzc2(Double sybxfsjzc2) {
		this.sybxfsjzc2 = sybxfsjzc2;
	}
	public Double getSybxfsjzc3() {
		return sybxfsjzc3;
	}
	public void setSybxfsjzc3(Double sybxfsjzc3) {
		this.sybxfsjzc3 = sybxfsjzc3;
	}
	public Double getCcxfy1() {
		return ccxfy1;
	}
	public void setCcxfy1(Double ccxfy1) {
		this.ccxfy1 = ccxfy1;
	}
	public Double getCcxfy2() {
		return ccxfy2;
	}
	public void setCcxfy2(Double ccxfy2) {
		this.ccxfy2 = ccxfy2;
	}
	public Double getCcxfy3() {
		return ccxfy3;
	}
	public void setCcxfy3(Double ccxfy3) {
		this.ccxfy3 = ccxfy3;
	}
	public Double getCxfy1() {
		return cxfy1;
	}
	public void setCxfy1(Double cxfy1) {
		this.cxfy1 = cxfy1;
	}
	public Double getCxfy2() {
		return cxfy2;
	}
	public void setCxfy2(Double cxfy2) {
		this.cxfy2 = cxfy2;
	}
	public Double getCxfy3() {
		return cxfy3;
	}
	public void setCxfy3(Double cxfy3) {
		this.cxfy3 = cxfy3;
	}
	public Double getRsxfy1() {
		return rsxfy1;
	}
	public void setRsxfy1(Double rsxfy1) {
		this.rsxfy1 = rsxfy1;
	}
	public Double getRsxfy2() {
		return rsxfy2;
	}
	public void setRsxfy2(Double rsxfy2) {
		this.rsxfy2 = rsxfy2;
	}
	public Double getRsxfy3() {
		return rsxfy3;
	}
	public void setRsxfy3(Double rsxfy3) {
		this.rsxfy3 = rsxfy3;
	}
	public Double getLyezwyked1() {
		return lyezwyked1;
	}
	public void setLyezwyked1(Double lyezwyked1) {
		this.lyezwyked1 = lyezwyked1;
	}
	public Double getLyezwyked2() {
		return lyezwyked2;
	}
	public void setLyezwyked2(Double lyezwyked2) {
		this.lyezwyked2 = lyezwyked2;
	}
	public Double getLyezwyked3() {
		return lyezwyked3;
	}
	public void setLyezwyked3(Double lyezwyked3) {
		this.lyezwyked3 = lyezwyked3;
	}
	public Double getNcjy1() {
		return ncjy1;
	}
	public void setNcjy1(Double ncjy1) {
		this.ncjy1 = ncjy1;
	}
	public Double getNcjy2() {
		return ncjy2;
	}
	public void setNcjy2(Double ncjy2) {
		this.ncjy2 = ncjy2;
	}
	public Double getNcjy3() {
		return ncjy3;
	}
	public void setNcjy3(Double ncjy3) {
		this.ncjy3 = ncjy3;
	}
	public Double getDnxz1() {
		return dnxz1;
	}
	public void setDnxz1(Double dnxz1) {
		this.dnxz1 = dnxz1;
	}
	public Double getDnxz2() {
		return dnxz2;
	}
	public void setDnxz2(Double dnxz2) {
		this.dnxz2 = dnxz2;
	}
	public Double getDnxz3() {
		return dnxz3;
	}
	public void setDnxz3(Double dnxz3) {
		this.dnxz3 = dnxz3;
	}
	public Double getDnzf1() {
		return dnzf1;
	}
	public void setDnzf1(Double dnzf1) {
		this.dnzf1 = dnzf1;
	}
	public Double getDnzf2() {
		return dnzf2;
	}
	public void setDnzf2(Double dnzf2) {
		this.dnzf2 = dnzf2;
	}
	public Double getDnzf3() {
		return dnzf3;
	}
	public void setDnzf3(Double dnzf3) {
		this.dnzf3 = dnzf3;
	}
	public Double getDnjy1() {
		return dnjy1;
	}
	public void setDnjy1(Double dnjy1) {
		this.dnjy1 = dnjy1;
	}
	public Double getDnjy2() {
		return dnjy2;
	}
	public void setDnjy2(Double dnjy2) {
		this.dnjy2 = dnjy2;
	}
	public Double getDnjy3() {
		return dnjy3;
	}
	public void setDnjy3(Double dnjy3) {
		this.dnjy3 = dnjy3;
	}
	public Double getYjxyzf1() {
		return yjxyzf1;
	}
	public void setYjxyzf1(Double yjxyzf1) {
		this.yjxyzf1 = yjxyzf1;
	}
	public Double getYjxyzf2() {
		return yjxyzf2;
	}
	public void setYjxyzf2(Double yjxyzf2) {
		this.yjxyzf2 = yjxyzf2;
	}
	public Double getYjxyzf3() {
		return yjxyzf3;
	}
	public void setYjxyzf3(Double yjxyzf3) {
		this.yjxyzf3 = yjxyzf3;
	}
	public Double getCke1() {
		return cke1;
	}
	public void setCke1(Double cke1) {
		this.cke1 = cke1;
	}
	public Double getCke2() {
		return cke2;
	}
	public void setCke2(Double cke2) {
		this.cke2 = cke2;
	}
	public Double getCke3() {
		return cke3;
	}
	public void setCke3(Double cke3) {
		this.cke3 = cke3;
	}
	public Double getJpcke1() {
		return jpcke1;
	}
	public void setJpcke1(Double jpcke1) {
		this.jpcke1 = jpcke1;
	}
	public Double getJpcke2() {
		return jpcke2;
	}
	public void setJpcke2(Double jpcke2) {
		this.jpcke2 = jpcke2;
	}
	public Double getJpcke3() {
		return jpcke3;
	}
	public void setJpcke3(Double jpcke3) {
		this.jpcke3 = jpcke3;
	}
	public Double getMpcke1() {
		return mpcke1;
	}
	public void setMpcke1(Double mpcke1) {
		this.mpcke1 = mpcke1;
	}
	public Double getMpcke2() {
		return mpcke2;
	}
	public void setMpcke2(Double mpcke2) {
		this.mpcke2 = mpcke2;
	}
	public Double getMpcke3() {
		return mpcke3;
	}
	public void setMpcke3(Double mpcke3) {
		this.mpcke3 = mpcke3;
	}
	public Double getJpyszk1() {
		return jpyszk1;
	}
	public void setJpyszk1(Double jpyszk1) {
		this.jpyszk1 = jpyszk1;
	}
	public Double getJpyszk2() {
		return jpyszk2;
	}
	public void setJpyszk2(Double jpyszk2) {
		this.jpyszk2 = jpyszk2;
	}
	public Double getJpyszk3() {
		return jpyszk3;
	}
	public void setJpyszk3(Double jpyszk3) {
		this.jpyszk3 = jpyszk3;
	}
	public Double getJpyfzk1() {
		return jpyfzk1;
	}
	public void setJpyfzk1(Double jpyfzk1) {
		this.jpyfzk1 = jpyfzk1;
	}
	public Double getJpyfzk2() {
		return jpyfzk2;
	}
	public void setJpyfzk2(Double jpyfzk2) {
		this.jpyfzk2 = jpyfzk2;
	}
	public Double getJpyfzk3() {
		return jpyfzk3;
	}
	public void setJpyfzk3(Double jpyfzk3) {
		this.jpyfzk3 = jpyfzk3;
	}
	public Double getJpch1() {
		return jpch1;
	}
	public void setJpch1(Double jpch1) {
		this.jpch1 = jpch1;
	}
	public Double getJpch2() {
		return jpch2;
	}
	public void setJpch2(Double jpch2) {
		this.jpch2 = jpch2;
	}
	public Double getJpch3() {
		return jpch3;
	}
	public void setJpch3(Double jpch3) {
		this.jpch3 = jpch3;
	}
	public Double getCcp1() {
		return ccp1;
	}
	public void setCcp1(Double ccp1) {
		this.ccp1 = ccp1;
	}
	public Double getCcp2() {
		return ccp2;
	}
	public void setCcp2(Double ccp2) {
		this.ccp2 = ccp2;
	}
	public Double getCcp3() {
		return ccp3;
	}
	public void setCcp3(Double ccp3) {
		this.ccp3 = ccp3;
	}
	public Double getSydwjy1() {
		return sydwjy1;
	}
	public void setSydwjy1(Double sydwjy1) {
		this.sydwjy1 = sydwjy1;
	}
	public Double getSydwjy2() {
		return sydwjy2;
	}
	public void setSydwjy2(Double sydwjy2) {
		this.sydwjy2 = sydwjy2;
	}
	public Double getSydwjy3() {
		return sydwjy3;
	}
	public void setSydwjy3(Double sydwjy3) {
		this.sydwjy3 = sydwjy3;
	}
	public Double getKyxmjdwcjy1() {
		return kyxmjdwcjy1;
	}
	public void setKyxmjdwcjy1(Double kyxmjdwcjy1) {
		this.kyxmjdwcjy1 = kyxmjdwcjy1;
	}
	public Double getKyxmjdwcjy2() {
		return kyxmjdwcjy2;
	}
	public void setKyxmjdwcjy2(Double kyxmjdwcjy2) {
		this.kyxmjdwcjy2 = kyxmjdwcjy2;
	}
	public Double getKyxmjdwcjy3() {
		return kyxmjdwcjy3;
	}
	public void setKyxmjdwcjy3(Double kyxmjdwcjy3) {
		this.kyxmjdwcjy3 = kyxmjdwcjy3;
	}
	
	
	
	
	
}
