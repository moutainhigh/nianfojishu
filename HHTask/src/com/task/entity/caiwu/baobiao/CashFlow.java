package com.task.entity.caiwu.baobiao;

import java.io.Serializable;

public class CashFlow implements Serializable{
/**
 * 现金流动表：（ta_baobiao_CashFlow）
 */
	private Integer id;
	private String months;//月份(yyyy年MM月)
	/**
	 * 一、经营活动产生的现金流量：
	 */
	private Double jymoneyIn1;//销售商品、提供劳务收到的现金
	private Double jymoneyIn2;//收到的税费返还
	private Double jymoneyIn3;//收到的其他与经营活动有关的现金
	private Double jymoneyInSum;// 现金流入小计
	private Double jymoneyOut1;//购买商品、接受劳务支付的现金
	private Double jymoneyOut2;//支付给职工以及为职工支付的现金
	private Double jymoneyOut3;// 支付的各项税费
	private Double jymoneyOut4;// 支付的其他与经营活动有关的现金
	private Double jymoneyOutSum;// 现金流出小计
	private Double jymoneySum;//经营活动产生的现金流量净额
	/**二、投资活动产生的现金流量：
	 */
	private Double tzmoneyIn1;//收回投资所收到的现金
	private Double tzmoneyIn2;//取得投资收益所收到的现金
	private Double tzmoneyIn3;//处置固定资产、无形资产和其他长期资产所收回的现金净额
	private Double tzmoneyIn4;//处置子公司及其他营业单位收到的现金净额
	private Double tzmoneyIn5;//收到的其他与投资活动有关的现金
	private Double tzmoneyInSum;//现金流入小计
	private Double tzmoneyOut1;//购建固定资产、无形资产和其他长期资产所支付的现金
	private Double tzmoneyOut2;//投资所支付的现金
	private Double tzmoneyOut3;//取得子公司及其他营业单位支付的现金净额
	private Double tzmoneyOut4;//支付的其他与投资活动有关的现金
	private Double tzmoneyOutSum;//现金流出小计
	private Double tzmoneySum;//投资活动产生的现金流量净额
	/**
	 * 筹资活动产生的现金流量：
	 */
	private Double czmoneyIn1;//吸收投资所收到的现金
	private Double czmoneyIn2;//借款所收到的现金
	private Double czmoneyIn3;//收到的其他与筹资活动有关的现金
	private Double czmoneyInSum;//现金流入小计
	private Double czmoneyOut1;//偿还债务所支付的现金
	private Double czmoneyOut2;//分配股利、利润或偿付利息所支付的现金
	private Double czmoneyOut3;//支付的其他与筹资活动有关的现
	private Double czmoneyOutSum;// 现金流出小计
	private Double czmoneySum;// 筹资活动产生的现金流量净额
	/**
	 * 四、汇率变动对现金的影响
	 */
	private Double hlyx;//汇率变动对现金的影响
	/**
	 * 五、现金及现金等价物净增加额
	 */
	private Double moneyadd;//现金及现金等价物净增加额
	/**
	 * 补充资料
	 */
	//1、将净利润调节为经营活动现金流量：
	private Double  jtzcjzzb;//加：计提的资产减值准备
	private Double gdzczj;//固定资产折旧
	private Double wxzctx;//无形资产摊销
	private Double chuzizc;//处置固定资产、无形资产和其他长期资产的损失（减；收益）
	private Double gdzcbfss;//固定资产报废损失
	private Double cwfy;// 财务费用
	private Double tzss;//投资损失（减：收益）
	private Double dyskdx;//递延税款贷项（减：借项）
	private Double chjs;//存货的减少（减：增加）
	private Double jyxmjs;//经营性应收项目的减少(减:增加)
	private Double jxyxmzj;//经营性应付项目的增加(减:减少)
	private Double qita;//其他
	private Double jyhdMoney;//经营活动产生的现金流量净额
	//2、不涉及现金收支的投资和筹资活动：
	private Double zwzzb;//债务转为资本
	private Double gzzqyears ;//一年内到期的可转换公司债券
	private Double rzzrgdzc  ;//融资租入固定资产
	//3、现金及现金等价物净增加情况：
	private Double moneyqm;//   现金的期末余额
	private Double moneyqc;// 减：现金的期初余额
	private Double moneydjwqm;//加：现金等价物的期末余额
	private Double moneydjwqc;// 减：现金等价物的期初余额
	private Double moneydj;// 现金及现金等价物净增加额
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public Double getJymoneyIn1() {
		return jymoneyIn1;
	}
	public void setJymoneyIn1(Double jymoneyIn1) {
		this.jymoneyIn1 = jymoneyIn1;
	}
	public Double getJymoneyIn2() {
		return jymoneyIn2;
	}
	public void setJymoneyIn2(Double jymoneyIn2) {
		this.jymoneyIn2 = jymoneyIn2;
	}
	public Double getJymoneyIn3() {
		return jymoneyIn3;
	}
	public void setJymoneyIn3(Double jymoneyIn3) {
		this.jymoneyIn3 = jymoneyIn3;
	}
	public Double getJymoneyInSum() {
		return jymoneyInSum;
	}
	public void setJymoneyInSum(Double jymoneyInSum) {
		this.jymoneyInSum = jymoneyInSum;
	}
	public Double getJymoneyOut1() {
		return jymoneyOut1;
	}
	public void setJymoneyOut1(Double jymoneyOut1) {
		this.jymoneyOut1 = jymoneyOut1;
	}
	public Double getJymoneyOut2() {
		return jymoneyOut2;
	}
	public void setJymoneyOut2(Double jymoneyOut2) {
		this.jymoneyOut2 = jymoneyOut2;
	}
	public Double getJymoneyOut3() {
		return jymoneyOut3;
	}
	public void setJymoneyOut3(Double jymoneyOut3) {
		this.jymoneyOut3 = jymoneyOut3;
	}
	public Double getJymoneyOut4() {
		return jymoneyOut4;
	}
	public void setJymoneyOut4(Double jymoneyOut4) {
		this.jymoneyOut4 = jymoneyOut4;
	}
	public Double getJymoneyOutSum() {
		return jymoneyOutSum;
	}
	public void setJymoneyOutSum(Double jymoneyOutSum) {
		this.jymoneyOutSum = jymoneyOutSum;
	}
	public Double getJymoneySum() {
		return jymoneySum;
	}
	public void setJymoneySum(Double jymoneySum) {
		this.jymoneySum = jymoneySum;
	}
	public Double getTzmoneyIn1() {
		return tzmoneyIn1;
	}
	public void setTzmoneyIn1(Double tzmoneyIn1) {
		this.tzmoneyIn1 = tzmoneyIn1;
	}
	public Double getTzmoneyIn2() {
		return tzmoneyIn2;
	}
	public void setTzmoneyIn2(Double tzmoneyIn2) {
		this.tzmoneyIn2 = tzmoneyIn2;
	}
	public Double getTzmoneyIn3() {
		return tzmoneyIn3;
	}
	public void setTzmoneyIn3(Double tzmoneyIn3) {
		this.tzmoneyIn3 = tzmoneyIn3;
	}
	public Double getTzmoneyIn4() {
		return tzmoneyIn4;
	}
	public void setTzmoneyIn4(Double tzmoneyIn4) {
		this.tzmoneyIn4 = tzmoneyIn4;
	}
	public Double getTzmoneyIn5() {
		return tzmoneyIn5;
	}
	public void setTzmoneyIn5(Double tzmoneyIn5) {
		this.tzmoneyIn5 = tzmoneyIn5;
	}
	public Double getTzmoneyInSum() {
		return tzmoneyInSum;
	}
	public void setTzmoneyInSum(Double tzmoneyInSum) {
		this.tzmoneyInSum = tzmoneyInSum;
	}
	public Double getTzmoneyOut1() {
		return tzmoneyOut1;
	}
	public void setTzmoneyOut1(Double tzmoneyOut1) {
		this.tzmoneyOut1 = tzmoneyOut1;
	}
	public Double getTzmoneyOut2() {
		return tzmoneyOut2;
	}
	public void setTzmoneyOut2(Double tzmoneyOut2) {
		this.tzmoneyOut2 = tzmoneyOut2;
	}
	public Double getTzmoneyOut3() {
		return tzmoneyOut3;
	}
	public void setTzmoneyOut3(Double tzmoneyOut3) {
		this.tzmoneyOut3 = tzmoneyOut3;
	}
	public Double getTzmoneyOut4() {
		return tzmoneyOut4;
	}
	public void setTzmoneyOut4(Double tzmoneyOut4) {
		this.tzmoneyOut4 = tzmoneyOut4;
	}
	public Double getTzmoneyOutSum() {
		return tzmoneyOutSum;
	}
	public void setTzmoneyOutSum(Double tzmoneyOutSum) {
		this.tzmoneyOutSum = tzmoneyOutSum;
	}
	public Double getTzmoneySum() {
		return tzmoneySum;
	}
	public void setTzmoneySum(Double tzmoneySum) {
		this.tzmoneySum = tzmoneySum;
	}
	public Double getCzmoneyIn1() {
		return czmoneyIn1;
	}
	public void setCzmoneyIn1(Double czmoneyIn1) {
		this.czmoneyIn1 = czmoneyIn1;
	}
	public Double getCzmoneyIn2() {
		return czmoneyIn2;
	}
	public void setCzmoneyIn2(Double czmoneyIn2) {
		this.czmoneyIn2 = czmoneyIn2;
	}
	public Double getCzmoneyIn3() {
		return czmoneyIn3;
	}
	public void setCzmoneyIn3(Double czmoneyIn3) {
		this.czmoneyIn3 = czmoneyIn3;
	}
	public Double getCzmoneyInSum() {
		return czmoneyInSum;
	}
	public void setCzmoneyInSum(Double czmoneyInSum) {
		this.czmoneyInSum = czmoneyInSum;
	}
	public Double getCzmoneyOut1() {
		return czmoneyOut1;
	}
	public void setCzmoneyOut1(Double czmoneyOut1) {
		this.czmoneyOut1 = czmoneyOut1;
	}
	public Double getCzmoneyOut2() {
		return czmoneyOut2;
	}
	public void setCzmoneyOut2(Double czmoneyOut2) {
		this.czmoneyOut2 = czmoneyOut2;
	}
	public Double getCzmoneyOut3() {
		return czmoneyOut3;
	}
	public void setCzmoneyOut3(Double czmoneyOut3) {
		this.czmoneyOut3 = czmoneyOut3;
	}
	public Double getCzmoneyOutSum() {
		return czmoneyOutSum;
	}
	public void setCzmoneyOutSum(Double czmoneyOutSum) {
		this.czmoneyOutSum = czmoneyOutSum;
	}
	public Double getCzmoneySum() {
		return czmoneySum;
	}
	public void setCzmoneySum(Double czmoneySum) {
		this.czmoneySum = czmoneySum;
	}
	public Double getHlyx() {
		return hlyx;
	}
	public void setHlyx(Double hlyx) {
		this.hlyx = hlyx;
	}
	public Double getMoneyadd() {
		return moneyadd;
	}
	public void setMoneyadd(Double moneyadd) {
		this.moneyadd = moneyadd;
	}
	public Double getJtzcjzzb() {
		return jtzcjzzb;
	}
	public void setJtzcjzzb(Double jtzcjzzb) {
		this.jtzcjzzb = jtzcjzzb;
	}
	public Double getGdzczj() {
		return gdzczj;
	}
	public void setGdzczj(Double gdzczj) {
		this.gdzczj = gdzczj;
	}
	public Double getWxzctx() {
		return wxzctx;
	}
	public void setWxzctx(Double wxzctx) {
		this.wxzctx = wxzctx;
	}
	public Double getChuzizc() {
		return chuzizc;
	}
	public void setChuzizc(Double chuzizc) {
		this.chuzizc = chuzizc;
	}
	public Double getGdzcbfss() {
		return gdzcbfss;
	}
	public void setGdzcbfss(Double gdzcbfss) {
		this.gdzcbfss = gdzcbfss;
	}
	public Double getCwfy() {
		return cwfy;
	}
	public void setCwfy(Double cwfy) {
		this.cwfy = cwfy;
	}
	public Double getTzss() {
		return tzss;
	}
	public void setTzss(Double tzss) {
		this.tzss = tzss;
	}
	public Double getDyskdx() {
		return dyskdx;
	}
	public void setDyskdx(Double dyskdx) {
		this.dyskdx = dyskdx;
	}
	public Double getChjs() {
		return chjs;
	}
	public void setChjs(Double chjs) {
		this.chjs = chjs;
	}
	public Double getJyxmjs() {
		return jyxmjs;
	}
	public void setJyxmjs(Double jyxmjs) {
		this.jyxmjs = jyxmjs;
	}
	public Double getJxyxmzj() {
		return jxyxmzj;
	}
	public void setJxyxmzj(Double jxyxmzj) {
		this.jxyxmzj = jxyxmzj;
	}
	public Double getQita() {
		return qita;
	}
	public void setQita(Double qita) {
		this.qita = qita;
	}
	public Double getJyhdMoney() {
		return jyhdMoney;
	}
	public void setJyhdMoney(Double jyhdMoney) {
		this.jyhdMoney = jyhdMoney;
	}
	public Double getZwzzb() {
		return zwzzb;
	}
	public void setZwzzb(Double zwzzb) {
		this.zwzzb = zwzzb;
	}
	public Double getGzzqyears() {
		return gzzqyears;
	}
	public void setGzzqyears(Double gzzqyears) {
		this.gzzqyears = gzzqyears;
	}
	public Double getRzzrgdzc() {
		return rzzrgdzc;
	}
	public void setRzzrgdzc(Double rzzrgdzc) {
		this.rzzrgdzc = rzzrgdzc;
	}
	public Double getMoneyqm() {
		return moneyqm;
	}
	public void setMoneyqm(Double moneyqm) {
		this.moneyqm = moneyqm;
	}
	public Double getMoneyqc() {
		return moneyqc;
	}
	public void setMoneyqc(Double moneyqc) {
		this.moneyqc = moneyqc;
	}
	public Double getMoneydjwqm() {
		return moneydjwqm;
	}
	public void setMoneydjwqm(Double moneydjwqm) {
		this.moneydjwqm = moneydjwqm;
	}
	public Double getMoneydjwqc() {
		return moneydjwqc;
	}
	public void setMoneydjwqc(Double moneydjwqc) {
		this.moneydjwqc = moneydjwqc;
	}
	public Double getMoneydj() {
		return moneydj;
	}
	public void setMoneydj(Double moneydj) {
		this.moneydj = moneydj;
	}
	
	
	
	
	
}
