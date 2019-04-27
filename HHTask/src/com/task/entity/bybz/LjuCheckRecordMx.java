package com.task.entity.bybz;

import java.io.Serializable;

/**
 * 量、检具校验记录明细表(ta_bybz_LjuCheckRecordMx) 和量、检具校验记录表(LjuCheckRecord) 多对一关系
 * 
 * @author 王晓飞
 *
 */
public class LjuCheckRecordMx implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String checks;// 校验项
	private String jymeans;// 校验方法
	private String jyresult;// 校验结果
	private String remarks;// 备注
	private LjuCheckRecord ljuCR;// 量、检具校验记录(多对一)

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChecks() {
		return checks;
	}

	public void setChecks(String checks) {
		this.checks = checks;
	}

	public String getJyresult() {
		return jyresult;
	}

	public void setJyresult(String jyresult) {
		this.jyresult = jyresult;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LjuCheckRecord getLjuCR() {
		return ljuCR;
	}

	public void setLjuCR(LjuCheckRecord ljuCR) {
		this.ljuCR = ljuCR;
	}

	public String getJymeans() {
		return jymeans;
	}

	public void setJymeans(String jymeans) {
		this.jymeans = jymeans;
	}

}
