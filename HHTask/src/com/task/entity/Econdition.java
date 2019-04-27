package com.task.entity;

public class Econdition implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;//ID
	private String month;//月份
	private String status;//状态
	private Integer quantity;//数量
	private String total;//总设备数
	private float rate;//百分比
    private String timecontact;//时间接点
	
    
    public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public String getTimecontact() {
		return timecontact;
	}
	public void setTimecontact(String timecontact) {
		this.timecontact = timecontact;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
    
}
