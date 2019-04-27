package com.task.entity;

import java.util.List;
import java.util.Set;

/**
 * 包装申请(表名:ta_Goods_bzsq)
 * 
 *@author 刘培
 */

public class Goods_bzsq  implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id; // ID
	private Integer goodsId;// 库存id
	private String markId;// 件号
	private String procardName;// 名称
	private String selfCard;// 批次
	private Float count;// 数量
	private String bzDate;// 包装日期
	private String zpbzUserId;//指派人员的id
	private String zpbzUserCode;// 指派包装人员工号
	private String zpbzUserName;// 指派包装人员名称
	private String yjStartTime;// 预计开始时间
	private String yjEndTime;// 预计结束时间
	private String status;// 待领、已领、完成
	private String lqbzUserCode;// 领取包装人员工号
	private String lqbzUserName;// 领取包装人员名称
	private String sjStartTime;// 实际开始时间
	private String sjEndTime;// 实际结束时间
	private String addTime;// 添加时间
	private String addUserCode;// 添加人员工号
	private String addUserName;// 添加人员名称
	private Set<Goods_bzw> goods_bzwSet; // 包装物明细
	private List<Goods_bzw> goods_bzwList; // 包装物明细
	private String neiorderId;//内部订单

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getProcardName() {
		return procardName;
	}

	public void setProcardName(String procardName) {
		this.procardName = procardName;
	}

	public String getSelfCard() {
		return selfCard;
	}

	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}

	public Float getCount() {
		return count;
	}

	public void setCount(Float count) {
		this.count = count;
	}

	public String getBzDate() {
		return bzDate;
	}

	public void setBzDate(String bzDate) {
		this.bzDate = bzDate;
	}

	public String getZpbzUserCode() {
		return zpbzUserCode;
	}

	public void setZpbzUserCode(String zpbzUserCode) {
		this.zpbzUserCode = zpbzUserCode;
	}

	public String getZpbzUserName() {
		return zpbzUserName;
	}

	public void setZpbzUserName(String zpbzUserName) {
		this.zpbzUserName = zpbzUserName;
	}

	public String getYjStartTime() {
		return yjStartTime;
	}

	public void setYjStartTime(String yjStartTime) {
		this.yjStartTime = yjStartTime;
	}

	public String getYjEndTime() {
		return yjEndTime;
	}

	public void setYjEndTime(String yjEndTime) {
		this.yjEndTime = yjEndTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLqbzUserCode() {
		return lqbzUserCode;
	}

	public void setLqbzUserCode(String lqbzUserCode) {
		this.lqbzUserCode = lqbzUserCode;
	}

	public String getLqbzUserName() {
		return lqbzUserName;
	}

	public void setLqbzUserName(String lqbzUserName) {
		this.lqbzUserName = lqbzUserName;
	}

	public String getSjStartTime() {
		return sjStartTime;
	}

	public void setSjStartTime(String sjStartTime) {
		this.sjStartTime = sjStartTime;
	}

	public String getSjEndTime() {
		return sjEndTime;
	}

	public void setSjEndTime(String sjEndTime) {
		this.sjEndTime = sjEndTime;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getAddUserCode() {
		return addUserCode;
	}

	public void setAddUserCode(String addUserCode) {
		this.addUserCode = addUserCode;
	}

	public String getAddUserName() {
		return addUserName;
	}

	public void setAddUserName(String addUserName) {
		this.addUserName = addUserName;
	}

	public Set<Goods_bzw> getGoods_bzwSet() {
		return goods_bzwSet;
	}

	public void setGoods_bzwSet(Set<Goods_bzw> goodsBzwSet) {
		goods_bzwSet = goodsBzwSet;
	}

	public List<Goods_bzw> getGoods_bzwList() {
		return goods_bzwList;
	}

	public void setGoods_bzwList(List<Goods_bzw> goodsBzwList) {
		goods_bzwList = goodsBzwList;
	}

	public String getZpbzUserId() {
		return zpbzUserId;
	}

	public void setZpbzUserId(String zpbzUserId) {
		this.zpbzUserId = zpbzUserId;
	}

	public String getNeiorderId() {
		return neiorderId;
	}

	public void setNeiorderId(String neiorderId) {
		this.neiorderId = neiorderId;
	}

}