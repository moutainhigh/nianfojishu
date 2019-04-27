/**
 * 
 */
package com.task.entity.lpanclear;

import java.io.Serializable;


/**
 * @author 梁盼
 * 清洁信息表实体类
 * @表名 ta_clearinfo
 */
public class ClearInfo implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;            //清洁表ID
	private String clearDate;      //清洁表当天日期(插入时就默认当天日期)
	private Employee employee;     //人员表对象
	

	


	
	
	public ClearInfo() {
		super();
	}
	public ClearInfo(String clearDate){
		this.clearDate = clearDate;
	}
	public ClearInfo(Integer id, String clearDate,Integer usersId,String usersName,String usersDept,Employee employee) {
		super();
		this.id = id;
		this.clearDate = clearDate;
	    this.employee = employee;
	}
	
		
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getClearDate() {
		return clearDate;
	}
	public void setClearDate(String clearDate) {
		this.clearDate = clearDate;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
}
