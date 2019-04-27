package com.tast.entity.zhaobiao;



import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.task.entity.ClientManagement;
import com.task.entity.OrderManager;

/***
 * 招标内部计划 表(表名:ta_crm_internalOrderzhaobiao)
 * 
 * @author 张玉山
 * 
 */
public class InternalOrderzhaobiao implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String num;
	private String genertorDate; // 生产计划年月
	private String newDate; // 生成生产计划的时间
	private String documentaryPeople;// 跟单人
	private String status;// (审核/经理审核/同意/完成)
	private String cardStatus;// (已生成/未生成)
	private String zhStatus;// 是否转完 (已转完/未转成)
	private String flow;
	private String whetherPurchase;
	private ClientManagement custome;
	private Integer epId;
	
	
	private Set orderManagers=new HashSet();
	
	
	public ClientManagement getCustome() {
		return custome;
	}

	public void setCustome(ClientManagement custome) {
		this.custome = custome;
	}

	private Set<OrderManager> outerOrderszhaobiao = new HashSet<OrderManager>();

	
	
	private Set<InternalOrderDetailzhaobiao> interOrderDetailszhaobiao = new HashSet<InternalOrderDetailzhaobiao>();

	private int isVali;

	public InternalOrderzhaobiao() {
		super();
	}

	public InternalOrderzhaobiao(String num, String genertorDate, String newDate) {
		super();
		this.num = num;
		this.genertorDate = genertorDate;
		this.newDate = newDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getGenertorDate() {
		return genertorDate;
	}

	public void setGenertorDate(String genertorDate) {
		this.genertorDate = genertorDate;
	}

	public String getNewDate() {
		return newDate;
	}

	public void setNewDate(String newDate) {
		this.newDate = newDate;
	}


	public Set<OrderManager> getOuterOrderszhaobiao() {
		return outerOrderszhaobiao;
	}

	public void setOuterOrderszhaobiao(Set<OrderManager> outerOrderszhaobiao) {
		this.outerOrderszhaobiao = outerOrderszhaobiao;
	}

	public Set<InternalOrderDetailzhaobiao> getInterOrderDetailszhaobiao() {
		return interOrderDetailszhaobiao;
	}

	public void setInterOrderDetailszhaobiao(
			Set<InternalOrderDetailzhaobiao> interOrderDetailszhaobiao) {
		this.interOrderDetailszhaobiao = interOrderDetailszhaobiao;
	}

	public Set<InternalOrderDetailzhaobiao> getInterOrderDetails() {
		return interOrderDetailszhaobiao;
	}

	public void setInterOrderDetails(Set<InternalOrderDetailzhaobiao> interOrderDetails) {
		this.interOrderDetailszhaobiao = interOrderDetailszhaobiao;
	}

//	public ClientManagement getCustome() {
//		return custome;
//	}
//
//	public void setCustome(ClientManagement custome) {
//		this.custome = custome;
//	}

	public String getDocumentaryPeople() {
		return documentaryPeople;
	}

	public void setDocumentaryPeople(String documentaryPeople) {
		this.documentaryPeople = documentaryPeople;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFlow() {
		return flow;
	}

	public void setFlow(String flow) {
		this.flow = flow;
	}

	public String getWhetherPurchase() {
		return whetherPurchase;
	}

	public void setWhetherPurchase(String whetherPurchase) {
		this.whetherPurchase = whetherPurchase;
	}

	public int getIsVali() {
		return isVali;
	}

	public void setIsVali(int isVali) {
		this.isVali = isVali;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getZhStatus() {
		return zhStatus;
	}

	public void setZhStatus(String zhStatus) {
		this.zhStatus = zhStatus;
	}

	public Set getOrderManagers() {
		return orderManagers;
	}

	public void setOrderManagers(Set orderManagers) {
		this.orderManagers = orderManagers;
	}

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

}
