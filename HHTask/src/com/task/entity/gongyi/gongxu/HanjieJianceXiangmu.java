package com.task.entity.gongyi.gongxu;

import java.io.Serializable;

public class HanjieJianceXiangmu implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**ID*/
	private Integer id;
	/**序号*/
	private String numb;
	/**检测项目*/
	private String xiangmu;
	/**测量器具*/
	private String qiju;
	/**操作者测定频次*/
	private String caozuoPinci;
	/**巡检测定频次*/
	private String xunjianPinci;
	/**工序数据ID*/
	private Integer processDataId;
	/**获取参数*/
	private String params;
	
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
	public String getXunjianPinci() {
		return xunjianPinci;
	}
	public void setXunjianPinci(String xunjianPinci) {
		this.xunjianPinci = xunjianPinci;
	}
	public String getCaozuoPinci() {
		return caozuoPinci;
	}
	public void setCaozuoPinci(String caozuoPinci) {
		this.caozuoPinci = caozuoPinci;
	}
	public Integer getProcessDataId() {
		return processDataId;
	}
	public void setProcessDataId(Integer processDataId) {
		this.processDataId = processDataId;
	}
	public String getParams() {
		if(params!=null){
			return params.replace("\\t", "").replace("\\r","").replace("\\n","").replace("\\f","").replace("\\","");
		}
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getNumb() {
		return numb;
	}
	public void setNumb(String numb) {
		this.numb = numb;
	}
}
