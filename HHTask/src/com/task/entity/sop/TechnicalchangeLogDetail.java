package com.task.entity.sop;
/**
 * ta_sop_w_TechnicalchangeLogDetail
 * @author txb
 *
 */
public class TechnicalchangeLogDetail implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
//	private Integer tclId;//主表Id
	private String tuhao;//图号
	private String markId;//件号
	private String changeLog;//更新内容
	private TechnicalchangeLog technicalchangeLog;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getChangeLog() {
		return changeLog;
	}
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}
	public TechnicalchangeLog getTechnicalchangeLog() {
		return technicalchangeLog;
	}
	public void setTechnicalchangeLog(TechnicalchangeLog technicalchangeLog) {
		this.technicalchangeLog = technicalchangeLog;
	}
	public String getTuhao() {
		return tuhao;
	}
	public void setTuhao(String tuhao) {
		this.tuhao = tuhao;
	}
	
	
	
	
}
