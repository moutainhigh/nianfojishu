package com.task.entity.caiwu;

import java.io.Serializable;

/**
 * 资产类别(ta_fin_AssetsType)
 * @author txb
 *
 */
public class AssetsType implements Serializable{
	private Integer id;//
	private String type;//类别名称
	private String jie;//(+-)
	private String dai;//(+-)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getJie() {
		return jie;
	}
	public void setJie(String jie) {
		this.jie = jie;
	}
	public String getDai() {
		return dai;
	}
	public void setDai(String dai) {
		this.dai = dai;
	}
	
}