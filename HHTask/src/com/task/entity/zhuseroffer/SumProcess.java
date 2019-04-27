package com.task.entity.zhuseroffer;
/**
 * 件号报价 
 * @author Administrator
 *
 */
public class SumProcess implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;//标题
	private String rootMark;//总成件号
	private String ywmarkId;
	private String markId;//件号(1213;12121;222)
	private String addTime;
	private String count;//数量
	private String stutas;
	private String bjStartDate;
	private String bjEndDate;
	private String cycle;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRootMark() {
		return rootMark;
	}
	public void setRootMark(String rootMark) {
		this.rootMark = rootMark;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getStutas() {
		return stutas;
	}
	public void setStutas(String stutas) {
		this.stutas = stutas;
	}
	public String getBjStartDate() {
		return bjStartDate;
	}
	public void setBjStartDate(String bjStartDate) {
		this.bjStartDate = bjStartDate;
	}
	public String getBjEndDate() {
		return bjEndDate;
	}
	public void setBjEndDate(String bjEndDate) {
		this.bjEndDate = bjEndDate;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getYwmarkId() {
		return ywmarkId;
	}
	public void setYwmarkId(String ywmarkId) {
		this.ywmarkId = ywmarkId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
