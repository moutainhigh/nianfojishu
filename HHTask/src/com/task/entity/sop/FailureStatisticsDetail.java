package com.task.entity.sop;

import com.alibaba.fastjson.annotation.JSONField;

/***
 * 检验不良明细表(表名:ta_sop_FailureStatisticsDetail)
 * 
 * @author 刘培
 * 
 */
public class FailureStatisticsDetail implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	private Integer buhegeId;//缺陷id
	private String code;// 故障代码
	private String type;// 故障描述
	private Float badNumber;// 不合格数量
	private FailureStatistics failureStatistics;// 对应检验信息
	private FailureSSOnDay failureSSOnDay;// 对应检日报表
	private FailureSSOnWeek failureSSOnWeek;// 对应周报表
	private FailureSSOnMonth failureSSOnMonth;// 对应月报表

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getBadNumber() {
		return badNumber;
	}

	public void setBadNumber(Float badNumber) {
		this.badNumber = badNumber;
	}
	@JSONField(serialize = false)
	public FailureStatistics getFailureStatistics() {
		return failureStatistics;
	}

	public void setFailureStatistics(FailureStatistics failureStatistics) {
		this.failureStatistics = failureStatistics;
	}

	public Integer getBuhegeId() {
		return buhegeId;
	}

	public void setBuhegeId(Integer buhegeId) {
		this.buhegeId = buhegeId;
	}
	@JSONField(serialize = false)
	public FailureSSOnDay getFailureSSOnDay() {
		return failureSSOnDay;
	}

	public void setFailureSSOnDay(FailureSSOnDay failureSSOnDay) {
		this.failureSSOnDay = failureSSOnDay;
	}
	@JSONField(serialize = false)
	public FailureSSOnWeek getFailureSSOnWeek() {
		return failureSSOnWeek;
	}
	
	public void setFailureSSOnWeek(FailureSSOnWeek failureSSOnWeek) {
		this.failureSSOnWeek = failureSSOnWeek;
	}
	@JSONField(serialize = false)
	public FailureSSOnMonth getFailureSSOnMonth() {
		return failureSSOnMonth;
	}

	public void setFailureSSOnMonth(FailureSSOnMonth failureSSOnMonth) {
		this.failureSSOnMonth = failureSSOnMonth;
	}

}
