package com.task.entity.rtx;

import java.io.Serializable;



/**
 * Rtx 连接配置表(ta_RtxConnect)
 * 
 * @author 王晓飞
 * 
 */
public class RtxConnect implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	public String rtxIp;// rtx服务器地址
	public String rtxPort;
	public String sender;// rtx发送统一账号
	public String pwd;// 登录密码

	public String driverName; // 加载JDBC驱动
	public String dbURL; //
	// 连接服务器和数据库rtxdb地址
	public String userName; // 默认用户名
	public String userPwd; // 密码
	public String pebsURL;
	public String ledURL;//
	//
	public String spareMail;//发送邮件地址;
	
	public String agentId;// = "1000003"; // 1000003 企业号 -> 应用中心 -> 点开应用中 -> 应用ID
	// 企业号id
	public String corpId;//= "wwf25a9529aedd36ee"; // wwf25a9529aedd36ee 企业号 -> 设置 -> 企业号信息
	// 应用id
	public String secret;// = "21GbQQEraZAPCs9SREJ3RV9kGUTmxeNCbkXZRrzhQVo"; // 21GbQQEraZAPCs9SREJ3RV9kGUTmxeNCbkXZRrzhQVo
	
	//
	public String jdpUserName;//jdp 用户名
	public String jdpPassWord;// jdp 密码

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRtxIp() {
		return rtxIp;
	}

	public void setRtxIp(String rtxIp) {
		this.rtxIp = rtxIp;
	}

	public String getRtxPort() {
		return rtxPort;
	}

	public void setRtxPort(String rtxPort) {
		this.rtxPort = rtxPort;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getPebsURL() {
		return pebsURL;
	}

	public void setPebsURL(String pebsURL) {
		this.pebsURL = pebsURL;
	}

	public String getSpareMail() {
		return spareMail;
	}

	public void setSpareMail(String spareMail) {
		this.spareMail = spareMail;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getLedURL() {
		return ledURL;
	}

	public void setLedURL(String ledURL) {
		this.ledURL = ledURL;
	}

	public String getJdpUserName() {
		return jdpUserName;
	}

	public void setJdpUserName(String jdpUserName) {
		this.jdpUserName = jdpUserName;
	}

	public String getJdpPassWord() {
		return jdpPassWord;
	}

	public void setJdpPassWord(String jdpPassWord) {
		this.jdpPassWord = jdpPassWord;
	}
	
	

}
