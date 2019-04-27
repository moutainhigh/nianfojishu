package com.task.entity;

import java.io.Serializable;

/***
 * 证件信息表(表名:ta_Credentials)
 * 
 * @author 于勇鸿斌
 * 
 */
public class Credentials implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;// id
	private String cardtype;//证件类型(行驶证/驾驶证)
	private String code;//工号
	private String name;//姓名
	private String sex;//性别
	
	private String validfrom;//有效起始日期
	private String validfor;//有效截止期限
	private String cycle;//周期
	
	private String address;//住址
	private String birthday;//出生日期
	private String issuedate;//初次领证日期
	private String cartype;//驾驶证类型
//	private String drivefile;//驾驶证文件
	
	
	private String platenumber;//车牌号码
	private String vehicletype;//车辆类型
	private String owner;//所有人
	private String model;//品牌型号
	private String usecharacter;//使用性质
	private String vin;//车辆识别代号
	private String enginenumber;//发动机号码
//	private String travelfile;//行驶证文件
	private String credentialsfile;//存放文件
	private Integer cishu;//发送次数
	
	private String isGong;//是否转为公车结果(yes/null)
	
	private String status;//状态
	private Integer epId;//
	private String addTime;//添加时间
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIsGong() {
		return isGong;
	}
	public void setIsGong(String isGong) {
		this.isGong = isGong;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getEpId() {
		return epId;
	}
	public void setEpId(Integer epId) {
		this.epId = epId;
	}
	public Integer getCishu() {
		return cishu;
	}
	public void setCishu(Integer cishu) {
		this.cishu = cishu;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getIssuedate() {
		return issuedate;
	}
	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getValidfrom() {
		return validfrom;
	}
	public void setValidfrom(String validfrom) {
		this.validfrom = validfrom;
	}
	public String getValidfor() {
		return validfor;
	}
	public void setValidfor(String validfor) {
		this.validfor = validfor;
	}
	public String getPlatenumber() {
		return platenumber;
	}
	public void setPlatenumber(String platenumber) {
		this.platenumber = platenumber;
	}
	public String getVehicletype() {
		return vehicletype;
	}
	public void setVehicletype(String vehicletype) {
		this.vehicletype = vehicletype;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUsecharacter() {
		return usecharacter;
	}
	public void setUsecharacter(String usecharacter) {
		this.usecharacter = usecharacter;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getEnginenumber() {
		return enginenumber;
	}
	public void setEnginenumber(String enginenumber) {
		this.enginenumber = enginenumber;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getCredentialsfile() {
		return credentialsfile;
	}
	public void setCredentialsfile(String credentialsfile) {
		this.credentialsfile = credentialsfile;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
}
