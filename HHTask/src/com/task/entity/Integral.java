package com.task.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 积分表
 * 项目表 :ta_integral 
 * @author 王晓飞
 *
 */
public class  Integral   implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer totalIntegral; //个人总积分;
	private String integralName;//积分所属人姓名；
	private String integrdept; //积分所属人部门;
	private String integrcode;//积分所属人编号;
	private Set<XiaoFei> xf;//消费积分表(一对多)；
	private Set<Integralsource> is;//积分来源表(一对多关系)
	private Integer userId; //员工id;
	private Integer sumxf; //月累计消费;
	private String xfmonth;//消费月份
	private String year;//年份;
	private String status;//状态(拉黑/正常/null 拉黑状态下不能抽奖，夺宝活动.)
	private String laheitime;//拉黑时间(一个月之后自动回复正常)
	private List<Integralsource> isList;
	private List<XiaoFei> xfList;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getTotalIntegral() {
		return totalIntegral;
	}
	public void setTotalIntegral(Integer totalIntegral) {
		this.totalIntegral = totalIntegral;
	}
	public String getIntegralName() {
		return integralName;
	}
	public void setIntegralName(String integralName) {
		this.integralName = integralName;
	}
	@JSONField(serialize = false)
	public Set<XiaoFei> getXf() {
		return xf;
	}
	public void setXf(Set<XiaoFei> xf) {
		this.xf = xf;
	}
	public List<Integralsource> getIsList() {
		return isList;
	}
	public void setIsList(List<Integralsource> isList) {
		this.isList = isList;
	}
	public List<XiaoFei> getXfList() {
		return xfList;
	}
	public void setXfList(List<XiaoFei> xfList) {
		this.xfList = xfList;
	}
	@JSONField(serialize = false)
	public Set<Integralsource> getIs() {
		return is;
	}
	public void setIs(Set<Integralsource> is) {
		this.is = is;
	}
	public String getIntegrdept() {
		return integrdept;
	}
	public void setIntegrdept(String integrdept) {
		this.integrdept = integrdept;
	}
	public String getIntegrcode() {
		return integrcode;
	}
	public void setIntegrcode(String integrcode) {
		this.integrcode = integrcode;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getSumxf() {
		return sumxf;
	}
	public void setSumxf(Integer sumxf) {
		this.sumxf = sumxf;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getXfmonth() {
		return xfmonth;
	}
	public void setXfmonth(String xfmonth) {
		this.xfmonth = xfmonth;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLaheitime() {
		return laheitime;
	}
	public void setLaheitime(String laheitime) {
		this.laheitime = laheitime;
	}
	
	
	
	
}
