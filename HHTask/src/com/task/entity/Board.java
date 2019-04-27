package com.task.entity;

import java.io.Serializable;
import java.sql.Clob;
import java.util.Date;

/**
 * 实体类
 * 
 * @author 马凯
 *
 */
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Clob content;
	private Integer scrnId;
	private Integer createUserId;
	private Integer parentId;
	private Date createDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Clob getContent() {
		return content;
	}

	public void setContent(Clob content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScrnId() {
		return scrnId;
	}

	public void setScrnId(Integer scrnId) {
		this.scrnId = scrnId;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}