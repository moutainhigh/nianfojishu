package com.task.entity.gongyi.gongxu;

import java.io.Serializable;

public class OperationOrderItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**ID*/
	private Integer id;
	/**检测项目*/
	private String xiangmu;
	/**测量器具*/
	private String qiju;
	/**判定标准*/
	private String pandingBiaozhun;
	/**检查频次*/
	private String jianchaPinci;
	
	/**操作顺序ID*/
	private Integer operationOrderId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getXiangmu() {
		return xiangmu;
	}

	public void setXiangmu(String xiangmu) {
		this.xiangmu = xiangmu;
	}

	public String getQiju() {
		return qiju;
	}

	public void setQiju(String qiju) {
		this.qiju = qiju;
	}

	public String getPandingBiaozhun() {
		return pandingBiaozhun;
	}

	public void setPandingBiaozhun(String pandingBiaozhun) {
		this.pandingBiaozhun = pandingBiaozhun;
	}

	public String getJianchaPinci() {
		return jianchaPinci;
	}

	public void setJianchaPinci(String jianchaPinci) {
		this.jianchaPinci = jianchaPinci;
	}

	public Integer getOperationOrderId() {
		return operationOrderId;
	}

	public void setOperationOrderId(Integer operationOrderId) {
		this.operationOrderId = operationOrderId;
	}
	
}