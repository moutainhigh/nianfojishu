package com.task.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.entity.diaoyan.ResearchReport;

/**
 * 在线智能测试公司名称表:(ta_CampanyName)
 * @author 
 *
 */
public class CampanyName implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String guimo;//公司规模
	private String type;//公司性质
	private String lianxiren;//联系人;负责人
	private String phone;//联系方式
	private Float total;//总收入
	private Float totalman;//总人数
	private Float totalpaci;//总平均
	private String ceshiNo;//测试编号
//	private Float jscb;//每年节省成本;
	private String campanyname;//公司名称
	private Integer userId;
	private String zyyw;//主营业务
	private String groups;//组别(诊断,调研)
	private Set<IntelligentDiagnosis> isset;// 一对多
	private List<IntelligentDiagnosis> islist;
	private Set<ResearchReport> rrSet;//调研报告 一对多
	private List<ResearchReport> rrList;//调研报告 一对多
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Float getTotal() {
		return total;
	}
	public void setTotal(Float total) {
		this.total = total;
	}
	
	public Float getTotalman() {
		return totalman;
	}
	public void setTotalman(Float totalman) {
		this.totalman = totalman;
	}
	public Float getTotalpaci() {
		return totalpaci;
	}
	public void setTotalpaci(Float totalpaci) {
		this.totalpaci = totalpaci;
	}
	public String getCeshiNo() {
		return ceshiNo;
	}
	public void setCeshiNo(String ceshiNo) {
		this.ceshiNo = ceshiNo;
	}
	public String getCampanyname() {
		return campanyname;
	}
	public void setCampanyname(String campanyname) {
		this.campanyname = campanyname;
	}
	@JSONField(serialize = false)
	public Set<IntelligentDiagnosis> getIsset() {
		return isset;
	}
	public void setIsset(Set<IntelligentDiagnosis> isset) {
		this.isset = isset;
	}
	public List<IntelligentDiagnosis> getIslist() {
		return islist;
	}
	public void setIslist(List<IntelligentDiagnosis> islist) {
		this.islist = islist;
	}
	public String getGuimo() {
		return guimo;
	}
	public void setGuimo(String guimo) {
		this.guimo = guimo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLianxiren() {
		return lianxiren;
	}
	public void setLianxiren(String lianxiren) {
		this.lianxiren = lianxiren;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
//	public Float getJscb() {
//		return jscb;
//	}
//	public void setJscb(Float jscb) {
//		this.jscb = jscb;
//	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public Set<ResearchReport> getRrSet() {
		return rrSet;
	}
	public void setRrSet(Set<ResearchReport> rrSet) {
		this.rrSet = rrSet;
	}
	public List<ResearchReport> getRrList() {
		return rrList;
	}
	public void setRrList(List<ResearchReport> rrList) {
		this.rrList = rrList;
	}
	public String getZyyw() {
		return zyyw;
	}
	public void setZyyw(String zyyw) {
		this.zyyw = zyyw;
	}
	
	
	
}
