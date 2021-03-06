package com.task.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author王晓飞
 *表名:(ta_Careertrack)
 */

public class Careertrack implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer userId;//userId;
	//个人信息
	private String userName;//姓名;
	private String dept;//部门(所在部门,应聘部门)
	private String job;//职位(所在职位,应聘职位)
	private String code;//工号
	private String education;// 学历
	private String cardId;// 身份证
	private String nation;// 民族
	private String birthplace;// 籍贯
	private String bothday;// 出生日期
	private String phoneNumber;// 手机号
	
	
	//职业信息
	private String mianshiTime;//面试时间;
	private String ruzhiTime;//入职时间;
	private String zzkhTime;//转正考核时间
	private String zhuanzhengTime;//转正时间;
	private String jixiaoTime;//最近一次绩效考核时间
	private String jiangchengTime;//最近一次奖惩时间
	private String diaodongTime;//最近一次调动时间
	private String jinshengTime;//最近一次晋升时间
	private String lizhiTime;//离职时间;
	
	private String status;//状态(面试,不录用,待入职,试用,实习,在职(转正了),内退,退休,病休,离职)
	
	public Careertrack(){
		
	}
	public Careertrack (String userName,String dept,String education
			,String cardId,String nation,String birthplace, 
			String date,String phoneNumber,String mianshiTime
			,String job,String status,Integer userId,String code ){
		this.userName = userName;
		this.dept = dept;
		this.education =education;
		this.cardId = cardId;
		this.nation = nation;
		this.birthplace = birthplace;
		this.bothday = date;
		this.phoneNumber = phoneNumber;
		this.mianshiTime = mianshiTime;
		this.job = job;
		this.status = status;
		this.userId = userId;
		this.code = code;
	}
	
	public Careertrack(String userName,Integer userId,String
			mianshiTime,String ruzhiTime
			,String zhuanzhengTime,String jixiaoTime,
			String jiangchengTime,String diaodongTime
			,String jinshengTime,String lizhiTime,
			String dept,String education
			,String cardId,String nation,String birthplace, 
			String bothday,String phoneNumber
			,String job,String code){
		this.userName = userName;
		this.userId = userId;
		this.mianshiTime = mianshiTime;
		this.ruzhiTime = ruzhiTime;
		this.zhuanzhengTime = zhuanzhengTime;
		this.jixiaoTime = jixiaoTime;
		this.jiangchengTime = jiangchengTime;
		this.diaodongTime = diaodongTime;
		this.jinshengTime = jinshengTime;
		this.lizhiTime = lizhiTime;
		this.dept = dept;
		this.education =education;
		this.cardId =  cardId;
		this.nation = nation;
		this.birthplace = birthplace;
		this.bothday = bothday;
		this.phoneNumber = phoneNumber;
		this.job = job;
		this.code = code;
	}
	public Careertrack(String userName,Integer uId,String
			mianshiTime,String ruzhiTime,String jixiaoTime,
			String jiangchengTime,String lizhiTime,String dept,String education
			,String cardId,String nation,String birthplace, 
			String bothday,String phoneNumber
			,String job ,String zhuanzhengTime,String status,String code){
		this.userName = userName;
		this.userId = uId;
		this.mianshiTime = mianshiTime;
		this.ruzhiTime = ruzhiTime;
		this.jixiaoTime = jixiaoTime;
		this.jiangchengTime = jiangchengTime;
		this.lizhiTime = lizhiTime;
		this.dept = dept;
		this.education =education;
		this.cardId =  cardId;
		this.nation = nation;
		this.birthplace = birthplace;
		this.bothday = bothday;
		this.phoneNumber = phoneNumber;
		this.job = job;
		this.zhuanzhengTime = zhuanzhengTime;
		this.status = status;
		this.code = code;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getMianshiTime() {
		return mianshiTime;
	}
	public void setMianshiTime(String mianshiTime) {
		this.mianshiTime = mianshiTime;
	}
	public String getRuzhiTime() {
		return ruzhiTime;
	}
	public void setRuzhiTime(String ruzhiTime) {
		this.ruzhiTime = ruzhiTime;
	}
	
	
	public String getJixiaoTime() {
		return jixiaoTime;
	}
	public void setJixiaoTime(String jixiaoTime) {
		this.jixiaoTime = jixiaoTime;
	}
	public String getJiangchengTime() {
		return jiangchengTime;
	}
	public void setJiangchengTime(String jiangchengTime) {
		this.jiangchengTime = jiangchengTime;
	}
	
	
	public String getZhuanzhengTime() {
		return zhuanzhengTime;
	}
	public void setZhuanzhengTime(String zhuanzhengTime) {
		this.zhuanzhengTime = zhuanzhengTime;
	}
	public String getDiaodongTime() {
		return diaodongTime;
	}
	public void setDiaodongTime(String diaodongTime) {
		this.diaodongTime = diaodongTime;
	}
	public String getJinshengTime() {
		return jinshengTime;
	}
	public void setJinshengTime(String jinshengTime) {
		this.jinshengTime = jinshengTime;
	}
	public String getLizhiTime() {
		return lizhiTime;
	}
	public void setLizhiTime(String lizhiTime) {
		this.lizhiTime = lizhiTime;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getBirthplace() {
		return birthplace;
	}
	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}
	
	public String getBothday() {
		return bothday;
	}
	public void setBothday(String bothday) {
		this.bothday = bothday;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getZzkhTime() {
		return zzkhTime;
	}
	public void setZzkhTime(String zzkhTime) {
		this.zzkhTime = zzkhTime;
	}
	
	
}
