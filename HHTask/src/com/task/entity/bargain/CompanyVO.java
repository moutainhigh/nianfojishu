package com.task.entity.bargain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.comparator.DetailSeqComparator;
import com.task.entity.Tdetail;

/***
 *公司信息表
 * @表名 ta_company
 * @author 毛小龙 
 */
public class CompanyVO implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String company_name;// 公司名称
	private String contacts;// 联系人
	private String telephone;// 电话
	private String clearing_way;// 结算方式
	private String clearing_date;// 结算时间
	private String selected_status;// 选中状态
	private List<BargainingDetails> bargainingDetails;

	public String getClearing_way() {
		return clearing_way;
	}

	public void setClearing_way(String clearingWay) {
		clearing_way = clearingWay;
	}

	public String getClearing_date() {
		return clearing_date;
	}

	public void setClearing_date(String clearingDate) {
		clearing_date = clearingDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String companyName) {
		company_name = companyName;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<BargainingDetails> getBargainingDetails() {
		return bargainingDetails;
	}

	public void setBargainingDetails(List<BargainingDetails> bargainingDetails) {
		this.bargainingDetails = bargainingDetails;
	}

	public String getSelected_status() {
		return selected_status;
	}

	public void setSelected_status(String selectedStatus) {
		selected_status = selectedStatus;
	}

}
