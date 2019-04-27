package com.task.entity.sop.spc;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author 王晓飞  spc控制表表头:(ta_sop_spc_SpcControl)
 *
 */
public class SpcControl  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String gongwei;//工位
	private String shebeiNo;//设备编号
	private String proName;//项目名称
	private String wlName;//物料名称
	private String clcontent;//测量项
	private String markId;//物料编码
	private Float nominalValue;//名义值
	private String tolerance;//公差（±）
	private Float ceilValue;//上限值
	private Float floorValue;//下限值
	private String clinstrument;//测量仪器
	private String  mxNo;//模穴号
	private String clunit;//测量单位
	
	private Float xdbar;//总均值
	private Float rbar;//极差均值
	private Float uclxbar;//上限均值
	private Float lclxbar;//下限均值
	private Float uclr;//极差最大值
	private Float lclr;//极差最小值
	private Float stdev;//标准偏差
	private Float cp;//
	private Float ca;//
	private Float cpk;//
	private Integer groupsize;//群组数大小(每组测量多少数)
	private String years;//年份
	private String status;//（历史；默认;）为null为''都是默认
	private Set<SpcControlGroup> setspcControlGroup;
	private List<SpcControlGroup> spcControlGroupList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getWlName() {
		return wlName;
	}
	public void setWlName(String wlName) {
		this.wlName = wlName;
	}
	public String getClcontent() {
		return clcontent;
	}
	public void setClcontent(String clcontent) {
		this.clcontent = clcontent;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public Float getNominalValue() {
		return nominalValue;
	}
	public void setNominalValue(Float nominalValue) {
		this.nominalValue = nominalValue;
	}
	public String getTolerance() {
		return tolerance;
	}
	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}
	public Float getCeilValue() {
		return ceilValue;
	}
	public void setCeilValue(Float ceilValue) {
		this.ceilValue = ceilValue;
	}
	public Float getFloorValue() {
		return floorValue;
	}
	public void setFloorValue(Float floorValue) {
		this.floorValue = floorValue;
	}
	public String getClinstrument() {
		return clinstrument;
	}
	public void setClinstrument(String clinstrument) {
		this.clinstrument = clinstrument;
	}
	public String getMxNo() {
		return mxNo;
	}
	public void setMxNo(String mxNo) {
		this.mxNo = mxNo;
	}
	public String getClunit() {
		return clunit;
	}
	public void setClunit(String clunit) {
		this.clunit = clunit;
	}
	
	public Integer getGroupsize() {
		return groupsize;
	}
	public void setGroupsize(Integer groupsize) {
		this.groupsize = groupsize;
	}
	public Set<SpcControlGroup> getSetspcControlGroup() {
		return setspcControlGroup;
	}
	public void setSetspcControlGroup(Set<SpcControlGroup> setspcControlGroup) {
		this.setspcControlGroup = setspcControlGroup;
	}
	public List<SpcControlGroup> getSpcControlGroupList() {
		return spcControlGroupList;
	}
	public void setSpcControlGroupList(List<SpcControlGroup> spcControlGroupList) {
		this.spcControlGroupList = spcControlGroupList;
	}
	public Float getStdev() {
		return stdev;
	}
	public void setStdev(Float stdev) {
		this.stdev = stdev;
	}
	public Float getCp() {
		return cp;
	}
	public void setCp(Float cp) {
		this.cp = cp;
	}
	public Float getCa() {
		return ca;
	}
	public void setCa(Float ca) {
		this.ca = ca;
	}
	public Float getCpk() {
		return cpk;
	}
	public void setCpk(Float cpk) {
		this.cpk = cpk;
	}
	public Float getXdbar() {
		return xdbar;
	}
	public void setXdbar(Float xdbar) {
		this.xdbar = xdbar;
	}
	public Float getRbar() {
		return rbar;
	}
	public void setRbar(Float rbar) {
		this.rbar = rbar;
	}
	public Float getUclxbar() {
		return uclxbar;
	}
	public void setUclxbar(Float uclxbar) {
		this.uclxbar = uclxbar;
	}
	public Float getLclxbar() {
		return lclxbar;
	}
	public void setLclxbar(Float lclxbar) {
		this.lclxbar = lclxbar;
	}
	public Float getUclr() {
		return uclr;
	}
	public void setUclr(Float uclr) {
		this.uclr = uclr;
	}
	public Float getLclr() {
		return lclr;
	}
	public void setLclr(Float lclr) {
		this.lclr = lclr;
	}
	public String getGongwei() {
		return gongwei;
	}
	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}
	public String getShebeiNo() {
		return shebeiNo;
	}
	public void setShebeiNo(String shebeiNo) {
		this.shebeiNo = shebeiNo;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}