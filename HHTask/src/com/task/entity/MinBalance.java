package com.task.entity;
/*
 * 
 * 相对最小余额表
 * 钟永林
 */
public class MinBalance implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id; //ID
	private String minType; //型别
    private String minPartnumber;//件号
	private Float minBalancenumber;//余额数
    private String minMonth;//月份
    private String mindatatime;//时间锥
    private String minRemarks;//备注
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMinType() {
		return minType;
	}
	public void setMinType(String minType) {
		this.minType = minType;
	}
	public String getMinPartnumber() {
		return minPartnumber;
	}
	public void setMinPartnumber(String minPartnumber) {
		this.minPartnumber = minPartnumber;
	}
	public Float getMinBalancenumber() {
		return minBalancenumber;
	}
	public void setMinBalancenumber(Float minBalancenumber) {
		this.minBalancenumber = minBalancenumber;
	}
	public String getMinMonth() {
		return minMonth;
	}
	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}
	public String getMinRemarks() {
		return minRemarks;
	}
	public void setMinRemarks(String minRemarks) {
		this.minRemarks = minRemarks;
	}
	public String getMindatatime() {
		return mindatatime;
	}
	public void setMindatatime(String mindatatime) {
		this.mindatatime = mindatatime;
	}
	
	
}
