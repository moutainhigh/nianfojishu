package com.task.entity.renshi;

import java.io.Serializable;
import java.util.Set;

import com.task.entity.Provision;

/**
 * 离职协议对象
 * 表名ta_hr_lz_dimission_XieYi
 * 
 * @author Li_Cong 2015/8/12/21:49
 */
public class Dimission_XieYi implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String xieyi_number;// 协议编号
	private String _Afang;// 甲方
	private String _Aaddress;
	private String _Bfang;// 乙方
	private String _Buid;// 乙身份证
	private String _Baddress;// 乙方户籍地址
	private String _Btel;// 乙方电话
	private String _BrealAddress;// 乙方函件送达地址

	private String _Afang2;
	private String _AfangRepresentative;// 甲方代表人
	private String _AaddDate;// 甲方填写日期
	private String _Bfang2;// 乙方
	private String _BaddDate;// 乙方填写日期

	private Set<Provision> provision_xieyi;// 离职协议条款

	private Integer epId;// 审批流程
	private String xieyi_Status;// 争议审批状态
	private String addTime;
	private String updateTime;

	// private DimissionLog dimissionexieyi;
	private Integer dim_xieyi_id;// 对应的离职申请单
	private Integer codeId;// 用户id验证非空

	// get set 方法
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String get_Afang() {
		return _Afang;
	}

	public void set_Afang(String afang) {
		_Afang = afang;
	}

	public String get_Aaddress() {
		return _Aaddress;
	}

	public void set_Aaddress(String aaddress) {
		_Aaddress = aaddress;
	}

	public String get_Bfang() {
		return _Bfang;
	}

	public void set_Bfang(String bfang) {
		_Bfang = bfang;
	}

	public String get_Buid() {
		return _Buid;
	}

	public void set_Buid(String buid) {
		_Buid = buid;
	}

	public String get_Baddress() {
		return _Baddress;
	}

	public void set_Baddress(String baddress) {
		_Baddress = baddress;
	}

	public String get_Btel() {
		return _Btel;
	}

	public void set_Btel(String btel) {
		_Btel = btel;
	}

	public String get_BrealAddress() {
		return _BrealAddress;
	}

	public void set_BrealAddress(String brealAddress) {
		_BrealAddress = brealAddress;
	}

	public String get_Afang2() {
		return _Afang2;
	}

	public void set_Afang2(String afang2) {
		_Afang2 = afang2;
	}

	public String get_AfangRepresentative() {
		return _AfangRepresentative;
	}

	public void set_AfangRepresentative(String afangRepresentative) {
		_AfangRepresentative = afangRepresentative;
	}

	public String get_AaddDate() {
		return _AaddDate;
	}

	public void set_AaddDate(String aaddDate) {
		_AaddDate = aaddDate;
	}

	public String get_Bfang2() {
		return _Bfang2;
	}

	public void set_Bfang2(String bfang2) {
		_Bfang2 = bfang2;
	}

	public String get_BaddDate() {
		return _BaddDate;
	}

	public void set_BaddDate(String baddDate) {
		_BaddDate = baddDate;
	}

	public Set<Provision> getProvision_xieyi() {
		return provision_xieyi;
	}

	public void setProvision_xieyi(Set<Provision> provisionXieyi) {
		provision_xieyi = provisionXieyi;
	}

	public Integer getEpId() {
		return epId;
	}

	public void setEpId(Integer epId) {
		this.epId = epId;
	}

	public String getXieyi_Status() {
		return xieyi_Status;
	}

	public void setXieyi_Status(String xieyiStatus) {
		xieyi_Status = xieyiStatus;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getXieyi_number() {
		return xieyi_number;
	}

	public void setXieyi_number(String xieyiNumber) {
		xieyi_number = xieyiNumber;
	}

	public Integer getDim_xieyi_id() {
		return dim_xieyi_id;
	}

	public void setDim_xieyi_id(Integer dimXieyiId) {
		dim_xieyi_id = dimXieyiId;
	}

	public Integer getCodeId() {
		return codeId;
	}

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

}