package com.task.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 职工培训
 * 
 * @author 刘培
 * 
 */
public class Train implements Serializable{

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;// 姓名
	private String code;// 工号
	private String cardId;// 卡号
	private String trainPurPose;// 培训目的
	private String trainContent;// 培训内容
	private Date trainDate;// 培训时间
	private String trainManner;// 培训方式
	private String sorce;// 培训成绩
	private String certificateName;// 证书名称
	private String issuingAuthority;// 发证机关
	private String certificateFileName;// 证书存档文件名称
	private String certificateNumber;// 证书编号
	private Date certificateValidity;// 证书有效期
	private Date certificateReview;// 证书复审日期

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getTrainPurPose() {
		return trainPurPose;
	}

	public void setTrainPurPose(String trainPurPose) {
		this.trainPurPose = trainPurPose;
	}

	public String getTrainContent() {
		return trainContent;
	}

	public void setTrainContent(String trainContent) {
		this.trainContent = trainContent;
	}

	public Date getTrainDate() {
		return trainDate;
	}

	public void setTrainDate(Date trainDate) {
		this.trainDate = trainDate;
	}

	public String getTrainManner() {
		return trainManner;
	}

	public void setTrainManner(String trainManner) {
		this.trainManner = trainManner;
	}

	public String getSorce() {
		return sorce;
	}

	public void setSorce(String sorce) {
		this.sorce = sorce;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public String getIssuingAuthority() {
		return issuingAuthority;
	}

	public void setIssuingAuthority(String issuingAuthority) {
		this.issuingAuthority = issuingAuthority;
	}

	public String getCertificateFileName() {
		return certificateFileName;
	}

	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public Date getCertificateValidity() {
		return certificateValidity;
	}

	public void setCertificateValidity(Date certificateValidity) {
		this.certificateValidity = certificateValidity;
	}

	public Date getCertificateReview() {
		return certificateReview;
	}

	public void setCertificateReview(Date certificateReview) {
		this.certificateReview = certificateReview;
	}

}
