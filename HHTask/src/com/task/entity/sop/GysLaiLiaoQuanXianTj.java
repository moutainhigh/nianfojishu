package com.task.entity.sop;
/**
 * ta_GysLaiLiaoQuanXianTj
 * 供应商来料缺陷统计。
 * @author wxf
 *
 */
public class GysLaiLiaoQuanXianTj implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	private String gysCmp;//供应商全称
	private String gysName;//供应商简称
	private String gysCode;//供应商编号
	private String markId;//件号
	private Integer wddId;//送货明细Id
	private Integer defId;//不良处理单Id
	private Integer count;//出现次数
	private String qxCode;//缺陷代码
	private String qxType;//缺陷类型
	private String updateTime;//更新时间
	private Integer jidu;//季度
	private Integer tuCount;//退货次数
	private String years;//年份
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public Integer getWddId() {
		return wddId;
	}
	public void setWddId(Integer wddId) {
		this.wddId = wddId;
	}
	public Integer getDefId() {
		return defId;
	}
	public void setDefId(Integer defId) {
		this.defId = defId;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getQxCode() {
		return qxCode;
	}
	public void setQxCode(String qxCode) {
		this.qxCode = qxCode;
	}
	public String getQxType() {
		return qxType;
	}
	public void setQxType(String qxType) {
		this.qxType = qxType;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getGysCode() {
		return gysCode;
	}
	public void setGysCode(String gysCode) {
		this.gysCode = gysCode;
	}
	public String getGysCmp() {
		return gysCmp;
	}
	public void setGysCmp(String gysCmp) {
		this.gysCmp = gysCmp;
	}
	public String getGysName() {
		return gysName;
	}
	public void setGysName(String gysName) {
		this.gysName = gysName;
	}
	public Integer getJidu() {
		return jidu;
	}
	public void setJidu(Integer jidu) {
		this.jidu = jidu;
	}
	public Integer getTuCount() {
		return tuCount;
	}
	public void setTuCount(Integer tuCount) {
		this.tuCount = tuCount;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	
	
	
}
