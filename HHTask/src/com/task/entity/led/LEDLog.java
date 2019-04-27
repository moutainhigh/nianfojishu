package com.task.entity.led;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/****
 * led发送日志表
 * 
 * @表名 ta_LEDLog
 * @author liupei
 * 
 */
public class LEDLog implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String gongwei;// 工位
	private Integer procardId;// 件号id
	private Integer processNo;// 工序号
	private Integer processInforId;// 工序id
	private String productStatus;// 生产状态（生产、完成）
	private String context;// 内容1 (64*32)
	private String context2;// 内容2 (192*64)
	private String context3;// 内容3 (128*64)
	private String status;// 状态（默认/历史）
	private String minFinishTime;// 最小完成时间
	private String addTime;// 添加时间
	private LED led;// LED对象

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Integer getProcessNo() {
		return processNo;
	}

	public void setProcessNo(Integer processNo) {
		this.processNo = processNo;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public Integer getProcardId() {
		return procardId;
	}

	public void setProcardId(Integer procardId) {
		this.procardId = procardId;
	}

	@JSONField(serialize = false)
	public LED getLed() {
		return led;
	}

	public void setLed(LED led) {
		this.led = led;
	}

	public String getMinFinishTime() {
		return minFinishTime;
	}

	public void setMinFinishTime(String minFinishTime) {
		this.minFinishTime = minFinishTime;
	}

	public String getGongwei() {
		return gongwei;
	}

	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}

	public Integer getProcessInforId() {
		return processInforId;
	}

	public void setProcessInforId(Integer processInforId) {
		this.processInforId = processInforId;
	}

	public String getContext2() {
		return context2;
	}

	public void setContext2(String context2) {
		this.context2 = context2;
	}

	public String getContext3() {
		return context3;
	}

	public void setContext3(String context3) {
		this.context3 = context3;
	}

}
