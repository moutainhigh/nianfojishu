package com.task.entity.supplier;

import java.io.Serializable;

public class SupplierEvaluate implements Serializable {
	private static final long serialVersionUID = 1L;
    private Integer id;
    private String timeframe;//考评期
    private Integer supplierid;//供应商Id
    private String supplierName;//供应商名称
    private String supplierCatagory;//供应商类别
    //品质
    private Float qualityBatch;//品质交货批次
    private Float qualityQualifiedBatch;//品质合格批次
    private Float qualityPercent;//品质合格率
    private Float qualityScore;//品质得分
    //交期
    private Float deliveryDateBatch;//交期准时交货批次
    private Float deliveryDatePercent;//交期准时交货率
    private Float deliveryDateScore;//交期得分
    //成本
    private Float costScore;//成本得分
    private String costInfo;//成本原因
    //服务
    private Float serviceScore;//服务得分
    private String serviceinfo;//服务原因
    //
    private Float evaluateScore;//合计得分
    private String evaluateLevel;//评定级别
    private String evaluater;//负责人
    private String evaluateNote;//备注
    private String skipSupplier;//是否忽略


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public Integer getSupplierid() {
        return supplierid;
    }

    public void setSupplierid(Integer supplierid) {
        this.supplierid = supplierid;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCatagory() {
        return supplierCatagory;
    }

    public void setSupplierCatagory(String supplierCatagory) {
        this.supplierCatagory = supplierCatagory;
    }

    public Float getQualityBatch() {
        return qualityBatch;
    }

    public void setQualityBatch(Float qualityBatch) {
        this.qualityBatch = qualityBatch;
    }

    public Float getQualityQualifiedBatch() {
        return qualityQualifiedBatch;
    }

    public void setQualityQualifiedBatch(Float qualityQualifiedBatch) {
        this.qualityQualifiedBatch = qualityQualifiedBatch;
    }

    public Float getQualityPercent() {
        return qualityPercent;
    }

    public void setQualityPercent(Float qualityPercent) {
        this.qualityPercent = qualityPercent;
    }

    public Float getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Float qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Float getDeliveryDateBatch() {
        return deliveryDateBatch;
    }

    public void setDeliveryDateBatch(Float deliveryDateBatch) {
        this.deliveryDateBatch = deliveryDateBatch;
    }

    public Float getDeliveryDatePercent() {
        return deliveryDatePercent;
    }

    public void setDeliveryDatePercent(Float deliveryDatePercent) {
        this.deliveryDatePercent = deliveryDatePercent;
    }

    public Float getDeliveryDateScore() {
        return deliveryDateScore;
    }

    public void setDeliveryDateScore(Float deliveryDateScore) {
        this.deliveryDateScore = deliveryDateScore;
    }

    public Float getCostScore() {
        return costScore;
    }

    public void setCostScore(Float costScore) {
        this.costScore = costScore;
    }

    public String getCostInfo() {
        return costInfo;
    }

    public void setCostInfo(String costInfo) {
        this.costInfo = costInfo;
    }

    public Float getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(Float serviceScore) {
        this.serviceScore = serviceScore;
    }

    public String getServiceinfo() {
        return serviceinfo;
    }

    public void setServiceinfo(String serviceinfo) {
        this.serviceinfo = serviceinfo;
    }

    public Float getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(Float evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public String getEvaluateLevel() {
        return evaluateLevel;
    }

    public void setEvaluateLevel(String evaluateLevel) {
        this.evaluateLevel = evaluateLevel;
    }

    public String getEvaluater() {
        return evaluater;
    }

    public void setEvaluater(String evaluater) {
        this.evaluater = evaluater;
    }

    public String getEvaluateNote() {
        return evaluateNote;
    }

    public void setEvaluateNote(String evaluateNote) {
        this.evaluateNote = evaluateNote;
    }

    public String getSkipSupplier() {
        return skipSupplier;
    }

    public void setSkipSupplier(String skipSupplier) {
        this.skipSupplier = skipSupplier;
    }
}