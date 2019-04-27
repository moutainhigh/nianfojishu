package com.task.entity;

import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;

/**
 * 异常处理表 (ta_ExceptionHandle)
 * @author mdd
 *
 */
public class ExceptionHandle implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String code;// 发送人工号
	private String username;//姓名
	private String dept;//部门
	private String company;//公司
	private String errorMessage;//错误信息
	private String lastUrl;//上一页面地址
	
	
	/**
	 * 保存一条异常消息
	 * @param exception
	 * @return
	 */
//	public static boolean  save(ExceptionHandle exception){
//		// 获得totalDao
//		TotalDao totalDao = TotalDaoImpl.findTotalDao();
//		return totalDao.save(exception);
//		
//	} 
	public ExceptionHandle(){
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getLastUrl() {
		return lastUrl;
	}
	public void setLastUrl(String lastUrl) {
		this.lastUrl = lastUrl;
	}
	
	
	
	
}
