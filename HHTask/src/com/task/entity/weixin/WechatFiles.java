package com.task.entity.weixin;

import java.io.Serializable;

/**
 * Files entity. @author MyEclipse Persistence Tools
 */

public class WechatFiles implements Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private Integer id;
	private WechatCard wechatCard;
	private Integer sort;
	private String path;
	private String type;

	// Constructors

	/** default constructor */
	public WechatFiles() {
	}

	/** full constructor */
	public WechatFiles(WechatCard wechatCard, Integer sort, String path, String type) {
		this.wechatCard = wechatCard;
		this.sort = sort;
		this.path = path;
		this.type = type;
	}

	// Property accessors

	


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public WechatCard getWechatCard() {
		return wechatCard;
	}

	public void setWechatCard(WechatCard wechatCard) {
		this.wechatCard = wechatCard;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}