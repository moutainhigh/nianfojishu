package com.task.entity;

import java.io.Serializable;

import com.task.util.FieldMeta;

/**
 * 表名:(ta_baofeigoods)
 * 
 * @author 王晓飞;
 */
public class BaoFeiGoods implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;// id;
	@FieldMeta(name = "件号")
	private String goodsMarkId;// 件号
	@FieldMeta(name = "品名")
	private String goodsFullName;// 品名;
	@FieldMeta(name = "批次")
	private String goodsLotId;// 批次;
	@FieldMeta(name = "当前量")
	private Float goodsCurQuantity;// 当前量
	@FieldMeta(name = "库别")
	private String goodsClass;// 库别
	@FieldMeta(name = "来源")
	private String laiyuan;// 来源;
	@FieldMeta(name = "报废说明")
	private String more;// 报废说明;
	private Integer goodsId;// 库存Id;
	private Integer epId;// 审批Id;
	@FieldMeta(name = "审批状态")
	private String ep_status;// 审批状态;
	@FieldMeta(name = "申请人")
	private String username;// 申请人;
	@FieldMeta(name = "申请人部门")
	private String dept;// 申请人部门;
	@FieldMeta(name = "申请日期")
	private String date;// 申请日期;
	@FieldMeta(name = "申请报废数量")
	private Float sq_num;// 申请报废数量;
	private Integer newGoodsId;
	@FieldMeta(name = "库别")
	private String kubie;// 库别
	@FieldMeta(name = "仓区")
	private String cangqu;// 仓区
	@FieldMeta(name = "库位")
	private String kuwei;// 库位

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGoodsMarkId() {
		return goodsMarkId;
	}

	public void setGoodsMarkId(String goodsMarkId) {
		this.goodsMarkId = goodsMarkId;
	}

	public String getGoodsFullName() {
		return goodsFullName;
	}

	public void setGoodsFullName(String goodsFullName) {
		this.goodsFullName = goodsFullName;
	}

	public String getGoodsLotId() {
		return goodsLotId;
	}

	public void setGoodsLotId(String goodsLotId) {
		this.goodsLotId = goodsLotId;
	}

	public Float getGoodsCurQuantity() {
		return goodsCurQuantity;
	}

	public void setGoodsCurQuantity(Float goodsCurQuantity) {
		this.goodsCurQuantity = goodsCurQuantity;
	}

	public String getGoodsClass() {
		return goodsClass;
	}

	public void setGoodsClass(String goodsClass) {
		this.goodsClass = goodsClass;
	}

	public String getLaiyuan() {
		return laiyuan;
	}

	public void setLaiyuan(String laiyuan) {
		this.laiyuan = laiyuan;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public String getEp_status() {
		return ep_status;
	}

	public void setEp_status(String epStatus) {
		ep_status = epStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Float getSq_num() {
		return sq_num;
	}

	public void setSq_num(Float sqNum) {
		sq_num = sqNum;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getNewGoodsId() {
		return newGoodsId;
	}

	public void setNewGoodsId(Integer newGoodsId) {
		this.newGoodsId = newGoodsId;
	}

	public String getKubie() {
		return kubie;
	}

	public void setKubie(String kubie) {
		this.kubie = kubie;
	}

	public String getCangqu() {
		return cangqu;
	}

	public void setCangqu(String cangqu) {
		this.cangqu = cangqu;
	}

	public String getKuwei() {
		return kuwei;
	}

	public void setKuwei(String kuwei) {
		this.kuwei = kuwei;
	}

}