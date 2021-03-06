package com.task.entity.jxkh;

import java.io.Serializable;

/**
 * 指标实际状况:(ta_ZbSjZk)
 * @author wxf
 *
 */
public class ZbSjZk implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String dept;//部门
	private String name;//姓名
	private String rank;//职位
	private Integer rankNo;//职级
	private String years;//年分 (yyyy年)
	private Integer month0;//月份
	private String months;//月份(yyyy-MM)
	private String addTime;//添加时间
	private String addUsersName;//添加人
	/**
	 * 质量指标
	 */
	//LAR
	private String lardyDept;//对应部门
	private String larzhibiao;//实际
	private String larzhimubiao;//目标
	private Integer larscore;//得分
	//RID
	private String riddyDept;//对应部门
	private Float ridzhibiao;//实际
	private Float ridmubiao;//目标
	private Integer ridscore;//得分
	/**
	 * 总销售产值(亿)
	 */
	private String zczdyDept;//对应部门
	private Float zczzhibiao;//实际
	private Float zczmubiao;//目标
	private Integer zczscore;//得分
	/**
	 * 人均销售(万)
	 */
	private String rjxsdyDept;//对应部门
	private Float rjxszhibiao;//实际
	private Float rjxsmubiao;//目标
	private Integer rjxsscore;//得分
	/**
	 * 实际人数
	 */
	private String sjrsdyDept;//对应部门
	private Integer sjrszhibiao;//实际
	private Integer sjrsmubiao;//目标
	private Integer sjrsscore;//得分
	
	/**
	 * 三按两遵守
	 */
	private String salzsdyDept;//对应部门
	private Float  salzszhibiao;//实际
	private Float salzsmubiao;//目标
	private Integer salzsscore;//得分
	/**
	 * 变动费用(万)
	 */
	private String bdfydyDept;//对应部门
	private Float bdfyzhibiao;//实际
	private Float bdfymubiao;//目标
	private Integer bdfyscore;//得分
	
	private Integer sumscocer;//总分
	private Integer ranking;//名次
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	public Integer getMonth0() {
		return month0;
	}
	public void setMonth0(Integer month0) {
		this.month0 = month0;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUsersName() {
		return addUsersName;
	}
	public void setAddUsersName(String addUsersName) {
		this.addUsersName = addUsersName;
	}
	public String getLardyDept() {
		return lardyDept;
	}
	public void setLardyDept(String lardyDept) {
		this.lardyDept = lardyDept;
	}
	public String getLarzhibiao() {
		return larzhibiao;
	}
	public void setLarzhibiao(String larzhibiao) {
		this.larzhibiao = larzhibiao;
	}
	public String getRiddyDept() {
		return riddyDept;
	}
	public void setRiddyDept(String riddyDept) {
		this.riddyDept = riddyDept;
	}
	public Float getRidzhibiao() {
		return ridzhibiao;
	}
	public void setRidzhibiao(Float ridzhibiao) {
		this.ridzhibiao = ridzhibiao;
	}
	public String getZczdyDept() {
		return zczdyDept;
	}
	public void setZczdyDept(String zczdyDept) {
		this.zczdyDept = zczdyDept;
	}
	public Float getZczzhibiao() {
		return zczzhibiao;
	}
	public void setZczzhibiao(Float zczzhibiao) {
		this.zczzhibiao = zczzhibiao;
	}
	public String getRjxsdyDept() {
		return rjxsdyDept;
	}
	public void setRjxsdyDept(String rjxsdyDept) {
		this.rjxsdyDept = rjxsdyDept;
	}
	public Float getRjxszhibiao() {
		return rjxszhibiao;
	}
	public void setRjxszhibiao(Float rjxszhibiao) {
		this.rjxszhibiao = rjxszhibiao;
	}
	public String getSjrsdyDept() {
		return sjrsdyDept;
	}
	public void setSjrsdyDept(String sjrsdyDept) {
		this.sjrsdyDept = sjrsdyDept;
	}
	
	public Integer getSjrszhibiao() {
		return sjrszhibiao;
	}
	public String getSalzsdyDept() {
		return salzsdyDept;
	}
	public void setSalzsdyDept(String salzsdyDept) {
		this.salzsdyDept = salzsdyDept;
	}
	public Float getSalzszhibiao() {
		return salzszhibiao;
	}
	public void setSalzszhibiao(Float salzszhibiao) {
		this.salzszhibiao = salzszhibiao;
	}
	public String getBdfydyDept() {
		return bdfydyDept;
	}
	public void setBdfydyDept(String bdfydyDept) {
		this.bdfydyDept = bdfydyDept;
	}
	public Float getBdfyzhibiao() {
		return bdfyzhibiao;
	}
	public void setBdfyzhibiao(Float bdfyzhibiao) {
		this.bdfyzhibiao = bdfyzhibiao;
	}
	public String getLarzhimubiao() {
		return larzhimubiao;
	}
	public void setLarzhimubiao(String larzhimubiao) {
		this.larzhimubiao = larzhimubiao;
	}
	public Integer getLarscore() {
		return larscore;
	}
	public void setLarscore(Integer larscore) {
		this.larscore = larscore;
	}
	public Float getRidmubiao() {
		return ridmubiao;
	}
	public void setRidmubiao(Float ridmubiao) {
		this.ridmubiao = ridmubiao;
	}
	public Integer getRidscore() {
		return ridscore;
	}
	public void setRidscore(Integer ridscore) {
		this.ridscore = ridscore;
	}
	public Float getZczmubiao() {
		return zczmubiao;
	}
	public void setZczmubiao(Float zczmubiao) {
		this.zczmubiao = zczmubiao;
	}
	public Integer getZczscore() {
		return zczscore;
	}
	public void setZczscore(Integer zczscore) {
		this.zczscore = zczscore;
	}
	public Float getRjxsmubiao() {
		return rjxsmubiao;
	}
	public void setRjxsmubiao(Float rjxsmubiao) {
		this.rjxsmubiao = rjxsmubiao;
	}
	public Integer getRjxsscore() {
		return rjxsscore;
	}
	public void setRjxsscore(Integer rjxsscore) {
		this.rjxsscore = rjxsscore;
	}
	
	public Integer getSjrsmubiao() {
		return sjrsmubiao;
	}
	public void setSjrsmubiao(Integer sjrsmubiao) {
		this.sjrsmubiao = sjrsmubiao;
	}
	public void setSjrszhibiao(Integer sjrszhibiao) {
		this.sjrszhibiao = sjrszhibiao;
	}
	public Integer getSjrsscore() {
		return sjrsscore;
	}
	public void setSjrsscore(Integer sjrsscore) {
		this.sjrsscore = sjrsscore;
	}
	public Integer getSalzsscore() {
		return salzsscore;
	}
	public void setSalzsscore(Integer salzsscore) {
		this.salzsscore = salzsscore;
	}
	public Float getBdfymubiao() {
		return bdfymubiao;
	}
	public void setBdfymubiao(Float bdfymubiao) {
		this.bdfymubiao = bdfymubiao;
	}
	public Integer getBdfyscore() {
		return bdfyscore;
	}
	public void setBdfyscore(Integer bdfyscore) {
		this.bdfyscore = bdfyscore;
	}
	public Integer getSumscocer() {
		return sumscocer;
	}
	public void setSumscocer(Integer sumscocer) {
		this.sumscocer = sumscocer;
	}
	public Integer getRanking() {
		return ranking;
	}
	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}
	public Float getSalzsmubiao() {
		return salzsmubiao;
	}
	public void setSalzsmubiao(Float salzsmubiao) {
		this.salzsmubiao = salzsmubiao;
	}
	public Integer getRankNo() {
		return rankNo;
	}
	public void setRankNo(Integer rankNo) {
		this.rankNo = rankNo;
	}
	
	
	
	
}
