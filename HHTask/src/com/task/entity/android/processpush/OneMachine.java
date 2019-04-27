package com.task.entity.android.processpush;

import java.io.Serializable;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.entity.TaSopGongwei;
import com.task.entity.onemark.OneLight;


/****
 * 悬挂一体机表  表名:ta_OneMachine
 * @author Li_Cong
 * 
 */
 
public class OneMachine implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private Integer id;
	private String omNum;//机器编号
	private String omStation;//对应工位
	private String ipAddress;//IP地址
	private String omAddress;//对应位置
	private String addTime;//时间
	private String updateTime;//时间
	private Set<TaSopGongwei> taSopGongweis;//工位信息表
	private Set<OneLight> oneLights;//灯位表
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOmNum() {
		return omNum;
	}
	public void setOmNum(String omNum) {
		this.omNum = omNum;
	}
	public String getOmStation() {
		return omStation;
	}
	public void setOmStation(String omStation) {
		this.omStation = omStation;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getOmAddress() {
		return omAddress;
	}
	public void setOmAddress(String omAddress) {
		this.omAddress = omAddress;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	@JSONField(serialize = false)
	public Set<TaSopGongwei> getTaSopGongweis() {
		return taSopGongweis;
	}
	public void setTaSopGongweis(Set<TaSopGongwei> taSopGongweis) {
		this.taSopGongweis = taSopGongweis;
	}
	@JSONField(serialize = false)
	public Set<OneLight> getOneLights() {
		return oneLights;
	}
	public void setOneLights(Set<OneLight> oneLights) {
		this.oneLights = oneLights;
	}
}
