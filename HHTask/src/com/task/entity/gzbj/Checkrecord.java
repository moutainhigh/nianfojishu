package com.task.entity.gzbj;

import java.io.Serializable;

public class Checkrecord implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String empno;//校检人工号
	private String empname;//校检人
	private String reportpop;//报检人
	private String reportdate;//报检时间
	private String calibrationstate;//校检状态
	private String calibrationTime;//报检完成时间
	private Integer MeasuringId;//外键ID
	private String fileName;//校验报告文件名
	
	private Measuring measuring;//外键对象
	private Gzstore gzstore;//
	
	
	
	
	public Measuring getMeasuring() {
		return measuring;
	}
	public void setMeasuring(Measuring measuring) {
		this.measuring = measuring;
	}
	public Integer getMeasuringId() {
		return MeasuringId;
	}
	public void setMeasuringId(Integer measuringId) {
		MeasuringId = measuringId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getEmpname() {
		return empname;
	}
	public void setEmpname(String empname) {
		this.empname = empname;
	}
	public String getReportpop() {
		return reportpop;
	}
	public void setReportpop(String reportpop) {
		this.reportpop = reportpop;
	}
	public String getReportdate() {
		return reportdate;
	}
	public void setReportdate(String reportdate) {
		this.reportdate = reportdate;
	}
	public String getCalibrationstate() {
		return calibrationstate;
	}
	public void setCalibrationstate(String calibrationstate) {
		this.calibrationstate = calibrationstate;
	}
	public String getCalibrationTime() {
		return calibrationTime;
	}
	public void setCalibrationTime(String calibrationTime) {
		this.calibrationTime = calibrationTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getEmpno() {
		return empno;
	}
	public void setEmpno(String empno) {
		this.empno = empno;
	}
	public Gzstore getGzstore() {
		return gzstore;
	}
	public void setGzstore(Gzstore gzstore) {
		this.gzstore = gzstore;
	}
	
	
	
	
	
	
	
	
	
	

}
