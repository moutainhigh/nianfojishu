/**
 * 
 */
package com.task.entity.lpanclear;

import java.io.Serializable;

/**
 * @author 梁盼
 * 评分记录表实体类
 */
public class Score implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;            //评分记录ID
    private String scoreDate;       //当天评分日期
	private String titleName;       //所属标题
    private Integer fraction;       //评分分数
    private Integer frequency;      //评分次数
    private Integer difference;     //差记录(0分记录)
    private String clearTheDay;     //所属人评分时间(前一次的值日人所属日期)
    private Employee employee;      //人员表外键

       
    

	

	public Score() {
		super();
	}	
	
	public Score(String clearTheDay,String titleName){
		super();
		this.titleName = titleName;
		this.clearTheDay = clearTheDay;

	}
	
	public Score(String titleName, Integer fraction,String scoreDate,String clearTheDay){
		super();
		this.titleName = titleName;
		this.fraction = fraction;
		this.scoreDate = scoreDate;
		this.clearTheDay = clearTheDay;

	}
	

	public Score(Integer id, String scoreDate, String titleName, Integer fraction, Integer frequency, Integer difference,Employee employee) {
		super();
		this.id = id;
		this.scoreDate = scoreDate;
		this.titleName = titleName;
		this.fraction = fraction;
		this.frequency = frequency;
		this.difference = difference;
		this.employee = employee;

	}

	
	
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getScoreDate() {
		return scoreDate;
	}
	public void setScoreDate(String scoreDate) {
		this.scoreDate = scoreDate;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public Integer getFraction() {
		return fraction;
	}
	public void setFraction(Integer fraction) {
		this.fraction = fraction;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	public Integer getDifference() {
		return difference;
	}
	public void setDifference(Integer difference) {
		this.difference = difference;
	}
    public String getClearTheDay() {
		return clearTheDay;
	}
	public void setClearTheDay(String clearTheDay) {
		this.clearTheDay = clearTheDay;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
}