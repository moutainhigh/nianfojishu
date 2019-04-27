package com.task.entity.pmi;

import java.io.Serializable;
import java.util.Set;

import com.task.entity.Machine;

/***
 * pmi表：ta_PmiManagement
 * 
 * @author 毛小龙
 * 
 */
public class PmiManagement implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String pmi_name;// 名称
	private String pmi_ip;// ip(客户端)
	private String pmi_serverIp;// ip(服务端)
	private Integer min_num;// 子模块对应网口模块的序号（1~10）
	private String pmi_port;// 端口
	private String pmi_date;// 时间
	private String pmi_type;// pmi类型(强控/弱控)(q/r)
	private String status;// (空闲/生产中)
	private Float rated_Current;//额定电流
	private Float alert_Percentage;//警报百分比
	private Set<Machine> machineSet;// 设备明细

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPmi_serverIp() {
		return pmi_serverIp;
	}

	public void setPmi_serverIp(String pmiServerIp) {
		pmi_serverIp = pmiServerIp;
	}

	public Integer getMin_num() {
		return min_num;
	}

	public void setMin_num(Integer minNum) {
		min_num = minNum;
	}

	public String getPmi_name() {
		return pmi_name;
	}

	public void setPmi_name(String pmiName) {
		pmi_name = pmiName;
	}

	public String getPmi_ip() {
		return pmi_ip;
	}

	public void setPmi_ip(String pmiIp) {
		pmi_ip = pmiIp;
	}

	public String getPmi_port() {
		return pmi_port;
	}

	public void setPmi_port(String pmiPort) {
		pmi_port = pmiPort;
	}

	public Set<Machine> getMachineSet() {
		return machineSet;
	}

	public void setMachineSet(Set<Machine> machineSet) {
		this.machineSet = machineSet;
	}

	public String getPmi_date() {
		return pmi_date;
	}

	public void setPmi_date(String pmiDate) {
		pmi_date = pmiDate;
	}

	public String getPmi_type() {
		return pmi_type;
	}

	public void setPmi_type(String pmiType) {
		pmi_type = pmiType;
	}

	

	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Float getAlert_Percentage() {
		return alert_Percentage;
	}

	public void setAlert_Percentage(Float alertPercentage) {
		alert_Percentage = alertPercentage;
	}

	public Float getRated_Current() {
		return rated_Current;
	}

	public void setRated_Current(Float ratedCurrent) {
		rated_Current = ratedCurrent;
	}

	

}