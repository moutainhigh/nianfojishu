package com.task.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.Dao.TotalDao;
import com.task.entity.fin.budget.SubBudgetRate;
import com.task.entity.system.AuditNode;
import com.task.entity.system.CircuitCustomize;
import com.task.util.StrutsTreeITreeNode;
import com.task.util.StrutsTreeNode;

/**
 * 部门编号表(表名:ta_deptNumber)
 * 
 * @author 刘培
 */
public class DeptNumber  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String dept;// 名称
	private String deptNumber;// 编号
	private Integer fatherId;// 父类Id
	private Integer belongLayer;// 当前层
	private Integer xuhao;//排序使用
	private String isVisitor;//是否允许访客（是，否）

	private Set<SubBudgetRate> sbRateSet;
	private Set<Users> user;//管理人员
	private Set<CircuitCustomize> CircuitCustomizeSet;//流程名称表(多对多)
	private Set<AuditNode> auditNodeSet;//审批节点表(多对多)

	// 转换为节点类
	public StrutsTreeNode toTreeNode() {
		StrutsTreeNode treenode = new StrutsTreeNode();
		treenode.setId(getId());// 对象id
		treenode.setParentId(getFatherId());// 父类id
		treenode.setName(getDept());// 对象名称
		treenode.setLayers(getBelongLayer());// 对象所在层
		return treenode;
	}
	/**
	 * 获取其下所有的部门Id用,隔开
	 * @return
	 */
	public static  String getDownAllIds(TotalDao totalDao,Integer id){
		List<DeptNumber> sonDpList = totalDao.query("from DeptNumber where fatherId=?", id);
		if(sonDpList!=null&&sonDpList.size()>0){
			StringBuffer sb = new StringBuffer();
			for(DeptNumber son:sonDpList){
				if(sb.length()==0){
					sb.append(son.getId());
				}else{
					sb.append(","+son.getId());
				}
				String sonsonIds = getDownAllIds(totalDao,son.getId());
				if(sonsonIds!=null&&sonsonIds.length()>0){
					sb.append(","+sonsonIds);
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getDeptNumber() {
		return deptNumber;
	}

	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}

	public Integer getFatherId() {
		return fatherId;
	}

	public void setFatherId(Integer fatherId) {
		this.fatherId = fatherId;
	}

	public Integer getBelongLayer() {
		return belongLayer;
	}

	public void setBelongLayer(Integer belongLayer) {
		this.belongLayer = belongLayer;
	}

	public Set<SubBudgetRate> getSbRateSet() {
		return sbRateSet;
	}

	public void setSbRateSet(Set<SubBudgetRate> sbRateSet) {
		this.sbRateSet = sbRateSet;
	}
	@JSONField(serialize = false)
	public Set<Users> getUser() {
		return user;
	}
	public void setUser(Set<Users> user) {
		this.user = user;
	}
	@JSONField(serialize = false)
	public Set<CircuitCustomize> getCircuitCustomizeSet() {
		return CircuitCustomizeSet;
	}
	public void setCircuitCustomizeSet(Set<CircuitCustomize> circuitCustomizeSet) {
		CircuitCustomizeSet = circuitCustomizeSet;
	}
	@JSONField(serialize = false)
	public Set<AuditNode> getAuditNodeSet() {
		return auditNodeSet;
	}
	public void setAuditNodeSet(Set<AuditNode> auditNodeSet) {
		this.auditNodeSet = auditNodeSet;
	}
	public Integer getXuhao() {
		return xuhao;
	}
	public void setXuhao(Integer xuhao) {
		this.xuhao = xuhao;
	}
	public String getIsVisitor() {
		return isVisitor;
	}
	public void setIsVisitor(String isVisitor) {
		this.isVisitor = isVisitor;
	}
	
}
