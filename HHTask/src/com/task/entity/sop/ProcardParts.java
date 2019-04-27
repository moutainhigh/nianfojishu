package com.task.entity.sop;

/**
 * 领料信息表（ta_sop_ProcardParts）
 * @author 贾辉辉
 *
 */
public class ProcardParts implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;//主键
	private Procard procard;//流水卡片信息
	private String style;//类别（外购件，原材料）
	private String markId;//零件号
	private String goodsName;//材料名
	private String luhao;//炉号
	private String bianhao;//编号
	private float count;//数量
	private String faliaoren;//发料人
	private String LotId;//批次
	private String format;//规格
	private Float applyCount;//公斤（常规单位）
	private Float realReceive;//实发（支数或块数）
	private String receiveDate;//领料时间
	private String more;//备注
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Procard getProcard() {
		return procard;
	}
	public void setProcard(Procard procard) {
		this.procard = procard;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getLuhao() {
		return luhao;
	}
	public void setLuhao(String luhao) {
		this.luhao = luhao;
	}
	public String getBianhao() {
		return bianhao;
	}
	public void setBianhao(String bianhao) {
		this.bianhao = bianhao;
	}
	public float getCount() {
		return count;
	}
	public void setCount(float count) {
		this.count = count;
	}
	public String getFaliaoren() {
		return faliaoren;
	}
	public void setFaliaoren(String faliaoren) {
		this.faliaoren = faliaoren;
	}
	public String getLotId() {
		return LotId;
	}
	public void setLotId(String lotId) {
		LotId = lotId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Float getApplyCount() {
		return applyCount;
	}
	public void setApplyCount(Float applyCount) {
		this.applyCount = applyCount;
	}
	public Float getRealReceive() {
		return realReceive;
	}
	public void setRealReceive(Float realReceive) {
		this.realReceive = realReceive;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
}