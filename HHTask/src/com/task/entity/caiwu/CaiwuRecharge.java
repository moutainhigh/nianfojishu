package com.task.entity.caiwu;

import java.io.Serializable;
import java.util.Set;

/**
 * 财务充值或报销明细
 * @author 贾辉辉
 * ta_fin_caiwuRecharge
 *
 */
public class CaiwuRecharge implements Serializable{
	private Integer id;// id
	private Integer sumID;//汇总申请ID
	private String code;//工号
	private String name;//姓名
	private String platenumber;//车牌号码
	private String isChongzhORbaoxiao;//充值或申领
	private Float chongzhiJine;//金额	
	private String operateTime;//操作时间
	private String fujian;//附件
	private String more;//备注
	
	private Integer yingfuId;//应付ID
	private String baoxiaoStatus;//报销状态
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSumID() {
		return sumID;
	}
	public void setSumID(Integer sumID) {
		this.sumID = sumID;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlatenumber() {
		return platenumber;
	}
	public void setPlatenumber(String platenumber) {
		this.platenumber = platenumber;
	}
	public String getIsChongzhORbaoxiao() {
		return isChongzhORbaoxiao;
	}
	public void setIsChongzhORbaoxiao(String isChongzhORbaoxiao) {
		this.isChongzhORbaoxiao = isChongzhORbaoxiao;
	}
	public Float getChongzhiJine() {
		if (null != chongzhiJine) {
			return Float.parseFloat(String.format("%.2f", chongzhiJine));
		}
		return chongzhiJine;
	}
	public void setChongzhiJine(Float chongzhiJine) {
		this.chongzhiJine = chongzhiJine;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public String getFujian() {
		return fujian;
	}
	public void setFujian(String fujian) {
		this.fujian = fujian;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	public Integer getYingfuId() {
		return yingfuId;
	}
	public void setYingfuId(Integer yingfuId) {
		this.yingfuId = yingfuId;
	}
	public String getBaoxiaoStatus() {
		return baoxiaoStatus;
	}
	public void setBaoxiaoStatus(String baoxiaoStatus) {
		this.baoxiaoStatus = baoxiaoStatus;
	}
	
	
	
	
}