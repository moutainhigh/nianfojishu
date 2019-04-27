package com.task.entity.sop;

/***
 * 区块表(表名:ta_sop_Block)
 * 
 * @author 刘培
 * 
 */
public class Block implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	private String blockName;// 区块名称
	private String blockMore;// 区块描述
	private String status;// 状态(初始、领取、完成)
	private String addDateTime;// 添加时间
	private Integer userCount;// 用户数量
	private Integer jihuoCount;// 激活数量
	private Integer finalCount;// 完成数量

	// /区块划分
	private Float topDistance;// 上部距离(百分比)
	private Float leftDistance;// 上部距离(百分比)
	private Float width;// 宽度(百分比)
	private Float hight;// 高度(百分比)
	private String blockStatus;// 自定义区块标识(可多处使用)(xcws(现场卫生)、。。。)
	private String yibangding;// 已绑定人员用于页面显示
	private Boolean daiqueren;// 是否有待确认

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getDaiqueren() {
		return daiqueren;
	}

	public void setDaiqueren(Boolean daiqueren) {
		this.daiqueren = daiqueren;
	}

	public String getYibangding() {
		return yibangding;
	}

	public void setYibangding(String yibangding) {
		this.yibangding = yibangding;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getBlockMore() {
		return blockMore;
	}

	public void setBlockMore(String blockMore) {
		this.blockMore = blockMore;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddDateTime() {
		return addDateTime;
	}

	public void setAddDateTime(String addDateTime) {
		this.addDateTime = addDateTime;
	}

	public Float getTopDistance() {
		return topDistance;
	}

	public void setTopDistance(Float topDistance) {
		this.topDistance = topDistance;
	}

	public Float getLeftDistance() {
		return leftDistance;
	}

	public void setLeftDistance(Float leftDistance) {
		this.leftDistance = leftDistance;
	}

	public String getBlockStatus() {
		return blockStatus;
	}

	public void setBlockStatus(String blockStatus) {
		this.blockStatus = blockStatus;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHight() {
		return hight;
	}

	public void setHight(Float hight) {
		this.hight = hight;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Integer getJihuoCount() {
		return jihuoCount;
	}

	public void setJihuoCount(Integer jihuoCount) {
		this.jihuoCount = jihuoCount;
	}

	public Integer getFinalCount() {
		return finalCount;
	}

	public void setFinalCount(Integer finalCount) {
		this.finalCount = finalCount;
	}

}
