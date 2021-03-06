package com.task.entity.jxkh;

import java.io.Serializable;

/**
 * 项目名
 * 名次系数确定表 :(ta_RankingNums)
 *1.确定某个名次应该有多少人。
 * @author wxf
 */
public class RankingCounts implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer ranking;//名次（正整数，不可重复）
	private Integer counts;//出现人次;()
	private Double fenPeiXIshu;//分配系数
	private String groups;//所属组别
	private String addTime;//添加时间
	private String addUsers;//添加人
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRanking() {
		return ranking;
	}
	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}
	public Integer getCounts() {
		return counts;
	}
	public void setCounts(Integer counts) {
		this.counts = counts;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUsers() {
		return addUsers;
	}
	public void setAddUsers(String addUsers) {
		this.addUsers = addUsers;
	}
	
	public Double getFenPeiXIshu() {
		return fenPeiXIshu;
	}
	public void setFenPeiXIshu(Double fenPeiXIshu) {
		this.fenPeiXIshu = fenPeiXIshu;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	
}
