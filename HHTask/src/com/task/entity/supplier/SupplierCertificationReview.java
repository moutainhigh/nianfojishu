package com.task.entity.supplier;

import java.io.Serializable;

/*
    * @author fy
　　* @date 2018/7/24 11:55
　　* @Description: 供应商认证审核等级表 每级审批人员 ta_supplierCertificationReview
　　* @param
　　* @return
　　* @throws
　　*/
public class SupplierCertificationReview implements Serializable {
	private static final long serialVersionUID = 1L;
    private Integer id;
    private String reviewUser;//审核人员ID
    private String reviewLevel;//审核等级

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReviewUser() {
        return reviewUser;
    }

    public void setReviewUser(String reviewUser) {
        this.reviewUser = reviewUser;
    }

    public String getReviewLevel() {
        return reviewLevel;
    }

    public void setReviewLevel(String reviewLevel) {
        this.reviewLevel = reviewLevel;
    }

}