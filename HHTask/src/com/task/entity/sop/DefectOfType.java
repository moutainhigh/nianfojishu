package com.task.entity.sop;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author wxf
 *	缺陷大类	表名:ta_sop_DefectOfType
 *	与BuHeGePin 一对多
 */
public class DefectOfType implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	private String defCode;//缺陷代码
	private String defName;//缺陷类型
	private String addtime;//添加时间
	private String addUsersName;//添加时间
	private Set<BuHeGePin> bhgpSet;//一对多
	private List<BuHeGePin> bhgList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDefCode() {
		return defCode;
	}
	public void setDefCode(String defCode) {
		this.defCode = defCode;
	}
	public String getDefName() {
		return defName;
	}
	public void setDefName(String defName) {
		this.defName = defName;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getAddUsersName() {
		return addUsersName;
	}
	public void setAddUsersName(String addUsersName) {
		this.addUsersName = addUsersName;
	}
	@JSONField(serialize = false)
	public Set<BuHeGePin> getBhgpSet() {
		return bhgpSet;
	}
	public void setBhgpSet(Set<BuHeGePin> bhgpSet) {
		this.bhgpSet = bhgpSet;
	}
	public List<BuHeGePin> getBhgList() {
		return bhgList;
	}
	public void setBhgList(List<BuHeGePin> bhgList) {
		this.bhgList = bhgList;
	}
	
	
	
	
	
}
