package com.task.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 电子屏幕类
 * @表名 ta_Screen
 * @author 马凯
 * 
 */
public class Screen implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;// 屏幕名称
	private String description;// 屏幕简介
	private Date createDateTime;// 创建时间
	private Date modifyDateTime;// 修改时间
	private String screenUrl;// 地址
	private Set<TaSopGongwei> gongweis;
	private Set<ScreenContent> screencontents;
	private List<TaSopGongwei> gongweiList;
	private List<ScreenContent> screencontentList;
	private String desc;//不用blob类型了.麻烦
//	private Set<ScreenContent> screencontent; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getModifyDateTime() {
		return modifyDateTime;
	}

	public void setModifyDateTime(Date modifyDateTime) {
		this.modifyDateTime = modifyDateTime;
	}

	public String getScreenUrl() {
		return screenUrl;
	}

	public void setScreenUrl(String screenUrl) {
		this.screenUrl = screenUrl;
	}

	public Set<TaSopGongwei> getGongweis() {
		return gongweis;
	}

	public void setGongweis(Set<TaSopGongwei> gongweis) {
		this.gongweis = gongweis;
	}

	public List<TaSopGongwei> getGongweiList() {
		return gongweiList;
	}

	public void setGongweiList(List<TaSopGongwei> gongweiList) {
		this.gongweiList = gongweiList;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Set<ScreenContent> getScreencontents() {
		return screencontents;
	}

	public void setScreencontents(Set<ScreenContent> screencontents) {
		this.screencontents = screencontents;
	}

	public List<ScreenContent> getScreencontentList() {
		return screencontentList;
	}

	public void setScreencontentList(List<ScreenContent> screencontentList) {
		this.screencontentList = screencontentList;
	}

//	public Set<ScreenContent> getScreencontent() {
//		return screencontent;
//	}
//
//	public void setScreencontent(Set<ScreenContent> screencontent) {
//		this.screencontent = screencontent;
//	}

}
