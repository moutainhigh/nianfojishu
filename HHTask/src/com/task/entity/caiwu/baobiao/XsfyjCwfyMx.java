package com.task.entity.caiwu.baobiao;

import java.io.Serializable;

/**
 * 
 * @author 王晓飞 
 * 销售费用及财务费用明细表 ：(ta_baobiao_XsfyjCwfyMx)
 *
 */
public class XsfyjCwfyMx implements Serializable{
	
	private  Integer id;
	
	private String companyName;//公司名称
	private String months;//月份
	/******一、销售费用合计****/
	private Double xsfy1;//销售费用合计
	private Double xsfy2;//销售费用合计
	private Double xsfy3;//销售费用合计
	// 运输费
	private Double ysf1;
	private Double ysf2;
	private Double ysf3;
	//装卸费
	private Double zxf1;
	private Double zxf2;
	private Double zxf3;
	//产品保险费
	private Double cpbxf1;
	private Double cpbxf2;
	private Double cpbxf3;
	//委托代销手续费(包装物)
	private Double wtdxsxf1;//
	private Double wtdxsxf2;//
	private Double wtdxsxf3;//
	//广告费
	private Double ggf1;
	private Double ggf2;
	private Double ggf3;
	//展览费
	private Double zlf1;
	private Double zlf2;
	private Double zlf3;
	// 租赁费
	private Double zulinf1;
	private Double zulinf2;
	private Double zulinf3;
	//包装费
	private Double bzf1;
	private Double bzf2;
	private Double bzf3;
	//销售服务费用
	private Double xsfwf1;
	private Double xsfwf2;
	private Double xsfwf3;
	//职工薪酬
	private Double zgxc1;
	private Double zgxc2;
	private Double zgxc3;
	//其中：工资
	private Double gz1;
	private Double gz2;
	private Double gz3;
	// 差旅费
	private Double clf1;
	private Double clf2;
	private Double clf3;
	//办公费
	private Double bgf1;
	private Double bgf2;
	private Double bgf3;
	//招待费
	private Double zdf1;//
	private Double zdf2;//
	private Double zdf3;//
	//折旧费
	private Double zjf1;
	private Double zjf2;
	private Double zjf3;
	//取暖费
	private Double qnf1;
	private Double qnf2;
	private Double qnf3;
	//水电费
	private Double sdf1;
	private Double sdf2;
	private Double sdf3;
	//修理费
	private Double xlf1;
	private Double xlf2;
	private Double xlf3;
	//物料消耗
	private Double wlxh1;	
	private Double wlxh2;	
	private Double wlxh3;
	//低值易耗品摊销(包装物)
	private Double dzyhptx1;
	private Double dzyhptx2;
	private Double dzyhptx3;
	//产品“三包”损失
	private Double cpsbss1;
	private Double cpsbss2;
	private Double cpsbss3;
	// 其  它
	private Double qita1;
	private Double qita2;
	private Double qita3;
	/** 二、财务费用合计：**/
	private Double cwfy1;//财务费用
	private Double cwfy2;//财务费用
	private Double cwfy3;//财务费用
	//利息支出
	private Double lxzc1;//利息支出
	private Double lxzc2;//利息支出
	private Double lxzc3;//利息支出
	//减：财政贴息
	private Double cztx1;//
	private Double cztx2;//
	private Double cztx3;//
	//减：利息收入
	private Double lxsr1;
	private Double lxsr2;
	private Double lxsr3;
	//折扣损失
	private Double zkss1;//
	private Double zkss2;//
	private Double zkss3;//
	// 汇兑净损失
	private Double hdjss1;
	private Double hdjss2;
	private Double hdjss3;
	//其中：汇兑收益
	private Double hdsy1;
	private Double hdsy2;
	private Double hdsy3;
	//金融机构手续费
	private Double jrjgsxf1;
	private Double jrjgsxf2;
	private Double jrjgsxf3;
	//其  它
	private Double qt1;
	private Double qt2;
	private Double qt3;
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
	public Double getXsfy1() {
		return xsfy1;
	}
	public void setXsfy1(Double xsfy1) {
		this.xsfy1 = xsfy1;
	}
	public Double getXsfy2() {
		return xsfy2;
	}
	public void setXsfy2(Double xsfy2) {
		this.xsfy2 = xsfy2;
	}
	public Double getXsfy3() {
		return xsfy3;
	}
	public void setXsfy3(Double xsfy3) {
		this.xsfy3 = xsfy3;
	}
	public Double getYsf1() {
		return ysf1;
	}
	public void setYsf1(Double ysf1) {
		this.ysf1 = ysf1;
	}
	public Double getYsf2() {
		return ysf2;
	}
	public void setYsf2(Double ysf2) {
		this.ysf2 = ysf2;
	}
	public Double getYsf3() {
		return ysf3;
	}
	public void setYsf3(Double ysf3) {
		this.ysf3 = ysf3;
	}
	public Double getZxf1() {
		return zxf1;
	}
	public void setZxf1(Double zxf1) {
		this.zxf1 = zxf1;
	}
	public Double getZxf2() {
		return zxf2;
	}
	public void setZxf2(Double zxf2) {
		this.zxf2 = zxf2;
	}
	public Double getZxf3() {
		return zxf3;
	}
	public void setZxf3(Double zxf3) {
		this.zxf3 = zxf3;
	}
	public Double getCpbxf1() {
		return cpbxf1;
	}
	public void setCpbxf1(Double cpbxf1) {
		this.cpbxf1 = cpbxf1;
	}
	public Double getCpbxf2() {
		return cpbxf2;
	}
	public void setCpbxf2(Double cpbxf2) {
		this.cpbxf2 = cpbxf2;
	}
	public Double getCpbxf3() {
		return cpbxf3;
	}
	public void setCpbxf3(Double cpbxf3) {
		this.cpbxf3 = cpbxf3;
	}
	public Double getWtdxsxf1() {
		return wtdxsxf1;
	}
	public void setWtdxsxf1(Double wtdxsxf1) {
		this.wtdxsxf1 = wtdxsxf1;
	}
	public Double getWtdxsxf2() {
		return wtdxsxf2;
	}
	public void setWtdxsxf2(Double wtdxsxf2) {
		this.wtdxsxf2 = wtdxsxf2;
	}
	public Double getWtdxsxf3() {
		return wtdxsxf3;
	}
	public void setWtdxsxf3(Double wtdxsxf3) {
		this.wtdxsxf3 = wtdxsxf3;
	}
	public Double getGgf1() {
		return ggf1;
	}
	public void setGgf1(Double ggf1) {
		this.ggf1 = ggf1;
	}
	public Double getGgf2() {
		return ggf2;
	}
	public void setGgf2(Double ggf2) {
		this.ggf2 = ggf2;
	}
	public Double getGgf3() {
		return ggf3;
	}
	public void setGgf3(Double ggf3) {
		this.ggf3 = ggf3;
	}
	public Double getZlf1() {
		return zlf1;
	}
	public void setZlf1(Double zlf1) {
		this.zlf1 = zlf1;
	}
	public Double getZlf2() {
		return zlf2;
	}
	public void setZlf2(Double zlf2) {
		this.zlf2 = zlf2;
	}
	public Double getZlf3() {
		return zlf3;
	}
	public void setZlf3(Double zlf3) {
		this.zlf3 = zlf3;
	}
	public Double getZulinf1() {
		return zulinf1;
	}
	public void setZulinf1(Double zulinf1) {
		this.zulinf1 = zulinf1;
	}
	public Double getZulinf2() {
		return zulinf2;
	}
	public void setZulinf2(Double zulinf2) {
		this.zulinf2 = zulinf2;
	}
	public Double getZulinf3() {
		return zulinf3;
	}
	public void setZulinf3(Double zulinf3) {
		this.zulinf3 = zulinf3;
	}
	public Double getBzf1() {
		return bzf1;
	}
	public void setBzf1(Double bzf1) {
		this.bzf1 = bzf1;
	}
	public Double getBzf2() {
		return bzf2;
	}
	public void setBzf2(Double bzf2) {
		this.bzf2 = bzf2;
	}
	public Double getBzf3() {
		return bzf3;
	}
	public void setBzf3(Double bzf3) {
		this.bzf3 = bzf3;
	}
	public Double getXsfwf1() {
		return xsfwf1;
	}
	public void setXsfwf1(Double xsfwf1) {
		this.xsfwf1 = xsfwf1;
	}
	public Double getXsfwf2() {
		return xsfwf2;
	}
	public void setXsfwf2(Double xsfwf2) {
		this.xsfwf2 = xsfwf2;
	}
	public Double getXsfwf3() {
		return xsfwf3;
	}
	public void setXsfwf3(Double xsfwf3) {
		this.xsfwf3 = xsfwf3;
	}
	public Double getZgxc1() {
		return zgxc1;
	}
	public void setZgxc1(Double zgxc1) {
		this.zgxc1 = zgxc1;
	}
	public Double getZgxc2() {
		return zgxc2;
	}
	public void setZgxc2(Double zgxc2) {
		this.zgxc2 = zgxc2;
	}
	public Double getZgxc3() {
		return zgxc3;
	}
	public void setZgxc3(Double zgxc3) {
		this.zgxc3 = zgxc3;
	}
	public Double getGz1() {
		return gz1;
	}
	public void setGz1(Double gz1) {
		this.gz1 = gz1;
	}
	public Double getGz2() {
		return gz2;
	}
	public void setGz2(Double gz2) {
		this.gz2 = gz2;
	}
	public Double getGz3() {
		return gz3;
	}
	public void setGz3(Double gz3) {
		this.gz3 = gz3;
	}
	public Double getClf1() {
		return clf1;
	}
	public void setClf1(Double clf1) {
		this.clf1 = clf1;
	}
	public Double getClf2() {
		return clf2;
	}
	public void setClf2(Double clf2) {
		this.clf2 = clf2;
	}
	public Double getClf3() {
		return clf3;
	}
	public void setClf3(Double clf3) {
		this.clf3 = clf3;
	}
	public Double getBgf1() {
		return bgf1;
	}
	public void setBgf1(Double bgf1) {
		this.bgf1 = bgf1;
	}
	public Double getBgf2() {
		return bgf2;
	}
	public void setBgf2(Double bgf2) {
		this.bgf2 = bgf2;
	}
	public Double getBgf3() {
		return bgf3;
	}
	public void setBgf3(Double bgf3) {
		this.bgf3 = bgf3;
	}
	public Double getZdf1() {
		return zdf1;
	}
	public void setZdf1(Double zdf1) {
		this.zdf1 = zdf1;
	}
	public Double getZdf2() {
		return zdf2;
	}
	public void setZdf2(Double zdf2) {
		this.zdf2 = zdf2;
	}
	public Double getZdf3() {
		return zdf3;
	}
	public void setZdf3(Double zdf3) {
		this.zdf3 = zdf3;
	}
	public Double getZjf1() {
		return zjf1;
	}
	public void setZjf1(Double zjf1) {
		this.zjf1 = zjf1;
	}
	public Double getZjf2() {
		return zjf2;
	}
	public void setZjf2(Double zjf2) {
		this.zjf2 = zjf2;
	}
	public Double getZjf3() {
		return zjf3;
	}
	public void setZjf3(Double zjf3) {
		this.zjf3 = zjf3;
	}
	public Double getQnf1() {
		return qnf1;
	}
	public void setQnf1(Double qnf1) {
		this.qnf1 = qnf1;
	}
	public Double getQnf2() {
		return qnf2;
	}
	public void setQnf2(Double qnf2) {
		this.qnf2 = qnf2;
	}
	public Double getQnf3() {
		return qnf3;
	}
	public void setQnf3(Double qnf3) {
		this.qnf3 = qnf3;
	}
	public Double getSdf1() {
		return sdf1;
	}
	public void setSdf1(Double sdf1) {
		this.sdf1 = sdf1;
	}
	public Double getSdf2() {
		return sdf2;
	}
	public void setSdf2(Double sdf2) {
		this.sdf2 = sdf2;
	}
	public Double getSdf3() {
		return sdf3;
	}
	public void setSdf3(Double sdf3) {
		this.sdf3 = sdf3;
	}
	public Double getXlf1() {
		return xlf1;
	}
	public void setXlf1(Double xlf1) {
		this.xlf1 = xlf1;
	}
	public Double getXlf2() {
		return xlf2;
	}
	public void setXlf2(Double xlf2) {
		this.xlf2 = xlf2;
	}
	public Double getXlf3() {
		return xlf3;
	}
	public void setXlf3(Double xlf3) {
		this.xlf3 = xlf3;
	}
	public Double getWlxh1() {
		return wlxh1;
	}
	public void setWlxh1(Double wlxh1) {
		this.wlxh1 = wlxh1;
	}
	public Double getWlxh2() {
		return wlxh2;
	}
	public void setWlxh2(Double wlxh2) {
		this.wlxh2 = wlxh2;
	}
	public Double getWlxh3() {
		return wlxh3;
	}
	public void setWlxh3(Double wlxh3) {
		this.wlxh3 = wlxh3;
	}
	public Double getDzyhptx1() {
		return dzyhptx1;
	}
	public void setDzyhptx1(Double dzyhptx1) {
		this.dzyhptx1 = dzyhptx1;
	}
	public Double getDzyhptx2() {
		return dzyhptx2;
	}
	public void setDzyhptx2(Double dzyhptx2) {
		this.dzyhptx2 = dzyhptx2;
	}
	public Double getDzyhptx3() {
		return dzyhptx3;
	}
	public void setDzyhptx3(Double dzyhptx3) {
		this.dzyhptx3 = dzyhptx3;
	}
	public Double getCpsbss1() {
		return cpsbss1;
	}
	public void setCpsbss1(Double cpsbss1) {
		this.cpsbss1 = cpsbss1;
	}
	public Double getCpsbss2() {
		return cpsbss2;
	}
	public void setCpsbss2(Double cpsbss2) {
		this.cpsbss2 = cpsbss2;
	}
	public Double getCpsbss3() {
		return cpsbss3;
	}
	public void setCpsbss3(Double cpsbss3) {
		this.cpsbss3 = cpsbss3;
	}
	public Double getQita1() {
		return qita1;
	}
	public void setQita1(Double qita1) {
		this.qita1 = qita1;
	}
	public Double getQita2() {
		return qita2;
	}
	public void setQita2(Double qita2) {
		this.qita2 = qita2;
	}
	public Double getQita3() {
		return qita3;
	}
	public void setQita3(Double qita3) {
		this.qita3 = qita3;
	}
	public Double getCwfy1() {
		return cwfy1;
	}
	public void setCwfy1(Double cwfy1) {
		this.cwfy1 = cwfy1;
	}
	public Double getCwfy2() {
		return cwfy2;
	}
	public void setCwfy2(Double cwfy2) {
		this.cwfy2 = cwfy2;
	}
	public Double getCwfy3() {
		return cwfy3;
	}
	public void setCwfy3(Double cwfy3) {
		this.cwfy3 = cwfy3;
	}
	public Double getLxzc1() {
		return lxzc1;
	}
	public void setLxzc1(Double lxzc1) {
		this.lxzc1 = lxzc1;
	}
	public Double getLxzc2() {
		return lxzc2;
	}
	public void setLxzc2(Double lxzc2) {
		this.lxzc2 = lxzc2;
	}
	public Double getLxzc3() {
		return lxzc3;
	}
	public void setLxzc3(Double lxzc3) {
		this.lxzc3 = lxzc3;
	}
	public Double getCztx1() {
		return cztx1;
	}
	public void setCztx1(Double cztx1) {
		this.cztx1 = cztx1;
	}
	public Double getCztx2() {
		return cztx2;
	}
	public void setCztx2(Double cztx2) {
		this.cztx2 = cztx2;
	}
	public Double getCztx3() {
		return cztx3;
	}
	public void setCztx3(Double cztx3) {
		this.cztx3 = cztx3;
	}
	public Double getLxsr1() {
		return lxsr1;
	}
	public void setLxsr1(Double lxsr1) {
		this.lxsr1 = lxsr1;
	}
	public Double getLxsr2() {
		return lxsr2;
	}
	public void setLxsr2(Double lxsr2) {
		this.lxsr2 = lxsr2;
	}
	public Double getLxsr3() {
		return lxsr3;
	}
	public void setLxsr3(Double lxsr3) {
		this.lxsr3 = lxsr3;
	}
	public Double getZkss1() {
		return zkss1;
	}
	public void setZkss1(Double zkss1) {
		this.zkss1 = zkss1;
	}
	public Double getZkss2() {
		return zkss2;
	}
	public void setZkss2(Double zkss2) {
		this.zkss2 = zkss2;
	}
	public Double getZkss3() {
		return zkss3;
	}
	public void setZkss3(Double zkss3) {
		this.zkss3 = zkss3;
	}
	public Double getHdjss1() {
		return hdjss1;
	}
	public void setHdjss1(Double hdjss1) {
		this.hdjss1 = hdjss1;
	}
	public Double getHdjss2() {
		return hdjss2;
	}
	public void setHdjss2(Double hdjss2) {
		this.hdjss2 = hdjss2;
	}
	public Double getHdjss3() {
		return hdjss3;
	}
	public void setHdjss3(Double hdjss3) {
		this.hdjss3 = hdjss3;
	}
	public Double getHdsy1() {
		return hdsy1;
	}
	public void setHdsy1(Double hdsy1) {
		this.hdsy1 = hdsy1;
	}
	public Double getHdsy2() {
		return hdsy2;
	}
	public void setHdsy2(Double hdsy2) {
		this.hdsy2 = hdsy2;
	}
	public Double getHdsy3() {
		return hdsy3;
	}
	public void setHdsy3(Double hdsy3) {
		this.hdsy3 = hdsy3;
	}
	public Double getJrjgsxf1() {
		return jrjgsxf1;
	}
	public void setJrjgsxf1(Double jrjgsxf1) {
		this.jrjgsxf1 = jrjgsxf1;
	}
	public Double getJrjgsxf2() {
		return jrjgsxf2;
	}
	public void setJrjgsxf2(Double jrjgsxf2) {
		this.jrjgsxf2 = jrjgsxf2;
	}
	public Double getJrjgsxf3() {
		return jrjgsxf3;
	}
	public void setJrjgsxf3(Double jrjgsxf3) {
		this.jrjgsxf3 = jrjgsxf3;
	}
	public Double getQt1() {
		return qt1;
	}
	public void setQt1(Double qt1) {
		this.qt1 = qt1;
	}
	public Double getQt2() {
		return qt2;
	}
	public void setQt2(Double qt2) {
		this.qt2 = qt2;
	}
	public Double getQt3() {
		return qt3;
	}
	public void setQt3(Double qt3) {
		this.qt3 = qt3;
	}
	
	
	
	

}