package com.task.entity.shizhi;

import java.io.Serializable;
import java.util.Set;


/**
 * 项目试制评分
 * 
 * @表名：ta_sk_ProTryMakeScore
 * @author 唐晓斌
 */
public class ProTryMakeScore implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String groupName;//项目小组
	private String proName;//项目名称
	private String proNum;//项目编号
	private String cusName;//客户名称
	private String month;//月份
	private Integer poSize;//项目试制单个数
	private Integer approvalId;//审批动态id
	private String approvalStatus;//审批状态（未申请，未审批，审批中，同意，打回）
	private String productStatus;//生产状态（初始，生产中，完成）（当第一个试制单生成卡片时为生产中，当所有的需求单完成是为完成）
	private Float completionrate;//完成率
	private Set<ProjectOrder> projectOrder;//项目试制订单
	private Set<TryMake> tryMake;//试制
	public ProTryMakeScore() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Set<TryMake> getTryMake() {
		return tryMake;
	}
	public void setTryMake(Set<TryMake> tryMake) {
		this.tryMake = tryMake;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	
	public String getProNum() {
		return proNum;
	}
	public void setProNum(String proNum) {
		this.proNum = proNum;
	}

	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Set<ProjectOrder> getProjectOrder() {
		return projectOrder;
	}
	public void setProjectOrder(Set<ProjectOrder> projectOrder) {
		this.projectOrder = projectOrder;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCusName() {
		return cusName;
	}
	public void setCusName(String cusName) {
		this.cusName = cusName;
	}
	public Integer getPoSize() {
		return poSize;
	}
	public void setPoSize(Integer poSize) {
		this.poSize = poSize;
	}
	
	public Integer getApprovalId() {
		return approvalId;
	}
	public void setApprovalId(Integer approvalId) {
		this.approvalId = approvalId;
	}
	public String getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
	public Float getCompletionrate() {
		return completionrate;
	}
	public void setCompletionrate(Float completionrate) {
		this.completionrate = completionrate;
	}
	

}
