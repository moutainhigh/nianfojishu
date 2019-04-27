package com.task.entity.sop.spc;

import java.io.Serializable;

/**
 * 
 * @author 王晓飞 SPC控制测量明细 (ta_sop_spc_SpcControlClDetail)
 *
 */
public class SpcControlClDetail  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Float clValue;//测量值
	private String clDate;//测量日期
	private Integer groupsNO;//组数
	private Integer xuhao;//序号
	private Integer spcControlId;//spc控制表表头Id
	private String addTime;//添加时间
	private SpcControlGroup spcControlGroup;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Float getClValue() {
		return clValue;
	}
	public void setClValue(Float clValue) {
		this.clValue = clValue;
	}
	public String getClDate() {
		return clDate;
	}
	public void setClDate(String clDate) {
		this.clDate = clDate;
	}
	public Integer getGroupsNO() {
		return groupsNO;
	}
	public void setGroupsNO(Integer groupsNO) {
		this.groupsNO = groupsNO;
	}
	public Integer getSpcControlId() {
		return spcControlId;
	}
	public void setSpcControlId(Integer spcControlId) {
		this.spcControlId = spcControlId;
	}
	public SpcControlGroup getSpcControlGroup() {
		return spcControlGroup;
	}
	public void setSpcControlGroup(SpcControlGroup spcControlGroup) {
		this.spcControlGroup = spcControlGroup;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public Integer getXuhao() {
		return xuhao;
	}
	public void setXuhao(Integer xuhao) {
		this.xuhao = xuhao;
	}
	
	
}	
