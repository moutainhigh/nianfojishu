package com.task.entity.system;

import java.io.Serializable;

/**
 * LicenseMsg
 * 
 * @表名：ta_sys_LicenseMsg
 * @author 唐晓斌
 */
public class LicenseMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String companyName;//公司全称
	private String shortName;//公司简称
	private String address;//公司地址
	private String companyUrl;//公司网址
	private String companyPeople;//负责人
	private String email;//邮件地址
	private String tel;//联系电话
	private String fax;//传真
	private String zip;//邮编
	private String business;//行业
	private String description;//描述
	private String Location;//公司所在地
	private String startTime;//最初使用时间
	private String notAfter;//license证书的有效期
	private Integer onLineConut;//同时在线人数
	private Integer oneMackConut;//允许添加一体机数量
	private Integer oneScreenConut;//允许添加看板数量
	private Integer onLEDConut;//允许添加LED数量
	
	public Integer getOneMackConut() {
		return oneMackConut;
	}
	public void setOneMackConut(Integer oneMackConut) {
		this.oneMackConut = oneMackConut;
	}
	public Integer getOneScreenConut() {
		return oneScreenConut;
	}
	public void setOneScreenConut(Integer oneScreenConut) {
		this.oneScreenConut = oneScreenConut;
	}
	public Integer getOnLEDConut() {
		return onLEDConut;
	}
	public void setOnLEDConut(Integer onLEDConut) {
		this.onLEDConut = onLEDConut;
	}
	public LicenseMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getCompanyUrl() {
		return companyUrl;
	}
	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}
	public String getCompanyPeople() {
		return companyPeople;
	}
	public void setCompanyPeople(String companyPeople) {
		this.companyPeople = companyPeople;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getNotAfter() {
		return notAfter;
	}
	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}
	public Integer getOnLineConut() {
		return onLineConut;
	}
	public void setOnLineConut(Integer onLineConut) {
		this.onLineConut = onLineConut;
	}
	
	

}
