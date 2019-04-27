package com.task.entity;

import java.io.Serializable;
import java.util.Set;

public class ProjectProposal  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String customerName;//客户名称
	private String customerAbout;//客户介绍
	private String serialNumber;//流水号
	private String pricePosition;//价格定位
	private String productionBase;//生产基地
	private String forecast;//产品预测
	private String confidentiality;//保密协议
	private String crafts;//工艺
	private String digifax;//数模
	private String drawing;//图纸
	private String descriptions;//项目建议
	private String createPerson;//创建人的名字
	private String createDate;//创建时间
	private String marketcar;//市场对标车型
	private String technologycar;//技术对标车型
	private boolean closed;//是否开启了审核 false为开启
	private Set<ProjectProposalFlow> check;//审核
	private Project root;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAbout() {
		return customerAbout;
	}
	public void setCustomerAbout(String customerAbout) {
		this.customerAbout = customerAbout;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getPricePosition() {
		return pricePosition;
	}
	public void setPricePosition(String pricePosition) {
		this.pricePosition = pricePosition;
	}
	public String getProductionBase() {
		return productionBase;
	}
	public void setProductionBase(String productionBase) {
		this.productionBase = productionBase;
	}
	public String getForecast() {
		return forecast;
	}
	public void setForecast(String forecast) {
		this.forecast = forecast;
	}
	public String getConfidentiality() {
		return confidentiality;
	}
	public void setConfidentiality(String confidentiality) {
		this.confidentiality = confidentiality;
	}
	public String getCrafts() {
		return crafts;
	}
	public void setCrafts(String crafts) {
		this.crafts = crafts;
	}
	public String getDigifax() {
		return digifax;
	}
	public void setDigifax(String digifax) {
		this.digifax = digifax;
	}
	public String getDrawing() {
		return drawing;
	}
	public void setDrawing(String drawing) {
		this.drawing = drawing;
	}
	public String getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}
	public String getCreatePerson() {
		return createPerson;
	}
	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getMarketcar() {
		return marketcar;
	}
	public void setMarketcar(String marketcar) {
		this.marketcar = marketcar;
	}
	public String getTechnologycar() {
		return technologycar;
	}
	public void setTechnologycar(String technologycar) {
		this.technologycar = technologycar;
	}
	public boolean isClosed() {
		return closed;
	}
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	public Set<ProjectProposalFlow> getCheck() {
		return check;
	}
	public void setCheck(Set<ProjectProposalFlow> check) {
		this.check = check;
	}
	public Project getRoot() {
		return root;
	}
	public void setRoot(Project root) {
		this.root = root;
	}
	


}