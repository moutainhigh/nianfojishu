/**
 * 
 */
package com.task.entity.lpanclear;
import java.io.Serializable;
import java.util.Set;
/**
 * @author 梁盼
 * 卫生清洁值班人员信息实体类
 */
public class Employee implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;                   //员工编号  
    private String employeeName;          //员工信息
    private String password;              //员工密码
    private Integer userId;               //外键
	private Set<ClearInfo> clearInfo;     //清洁表set对象
    private Set<Score> score;             //评分表set对象     



	public Employee() {
		super();
	}	
	public Employee(Integer id, String employeeName,String password,Set<ClearInfo> clearInfo,Set<Score> score,Integer userId) {
		super();
		this.id = id;
		this.employeeName = employeeName;
		this.password = password;
		this.clearInfo = clearInfo;
		this.score = score;
	}
    
	
	
	
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public String getPassword() {
	    return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Set<ClearInfo> getClearInfo() {
		return clearInfo;
	}
	public void setClearInfo(Set<ClearInfo> clearInfo) {
		this.clearInfo = clearInfo;
	}
	public Set<Score> getScore() {
		return score;
	}
	public void setScore(Set<Score> score) {
		this.score = score;
	}
}