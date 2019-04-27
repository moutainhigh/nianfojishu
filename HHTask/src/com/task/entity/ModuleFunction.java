package com.task.entity;

import java.util.Date;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.task.util.StrutsTreeITreeNode;
import com.task.util.StrutsTreeNode;

/***
 * 模块功能类(表名:ta_sys_moduleFunction)
 * 
 * @author 刘培
 * 
 */
public class ModuleFunction implements StrutsTreeITreeNode , java.io.Serializable{
	private static final long serialVersionUID =1L;

	private Integer id;
	private String functionName;// 功能名称
	private String functionIntro;// 功能描述
	private String functionLink;// 功能链接
	private Integer fatherId;// 父类Id
	private Integer belongLayer;// 当前层
	private Integer rootId;// 根id
	private Set<Users> users;// 用户(多对多)
	private Set<UserRole> userRole;// 角色(多对多)
//	private Set<SubModule> subModule;//子模块(一对多)

	private Integer sequence_id;// 序列ID

	private String timeControl;// 时间控制(yes/no)
	private Date stratDateTime;// 开始时间
	private Date endDateTime;// 结束时间
	private String qximageName;// 图标名称
	private String imageName;// 图标名称
	private String smallImageName;// 小图标名称
	private String dhNoColor;// 导航默认图标
	private String dhHasColor;// 导航变色图标
	
	private String bgColor;// 图标背景
	private String targetNewPage;// 是否跳出当前页新开页面
	private String phoneShow;// 是否只用于手机端显示(yes/no)
	private String targetName;// 新开页面名称
	private String englishName;// 英文名称
	
	private String isSubModule;// 是否子模块
	
	

	// 转换为节点类
	public StrutsTreeNode toTreeNode() {
		StrutsTreeNode treenode = new StrutsTreeNode();
		treenode.setId(getId());// 对象id
		treenode.setParentId(getFatherId());// 父类id
		treenode.setName("<a href='ModuleFunctionAction!findMfById.action?id="
				+ getId() + "'  target='main'>" + getFunctionName() + "</a>");// 对象名称
		treenode.setLayers(getBelongLayer());// 对象所在层
		return treenode;
	}

	public Integer getSequence_id() {
		return sequence_id;
	}

	public void setSequence_id(Integer sequenceId) {
		sequence_id = sequenceId;
	}

	public String getPhoneShow() {
		return phoneShow;
	}

	public void setPhoneShow(String phoneShow) {
		this.phoneShow = phoneShow;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionIntro() {
		return functionIntro;
	}

	public void setFunctionIntro(String functionIntro) {
		this.functionIntro = functionIntro;
	}

	public String getFunctionLink() {
		return functionLink;
	}

	public void setFunctionLink(String functionLink) {
		this.functionLink = functionLink;
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

	/****
	 * 该对象不在序列化
	 * 
	 * @return
	 */
	@JSONField(serialize = false)
	public Set<Users> getUsers() {
		return users;
	}

	public void setUsers(Set<Users> users) {
		this.users = users;
	}

	public String getTimeControl() {
		return timeControl;
	}

	public void setTimeControl(String timeControl) {
		this.timeControl = timeControl;
	}

	public Date getStratDateTime() {
		return stratDateTime;
	}

	public void setStratDateTime(Date stratDateTime) {
		this.stratDateTime = stratDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public Integer getRootId() {
		return rootId;
	}

	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}

	public String getTargetNewPage() {
		return targetNewPage;
	}

	public void setTargetNewPage(String targetNewPage) {
		this.targetNewPage = targetNewPage;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getSmallImageName() {
		return smallImageName;
	}

	public void setSmallImageName(String smallImageName) {
		this.smallImageName = smallImageName;
	}

	public String getQximageName() {
		return qximageName;
	}

	public void setQximageName(String qximageName) {
		this.qximageName = qximageName;
	}

	public String getDhNoColor() {
		return dhNoColor;
	}

	public void setDhNoColor(String dhNoColor) {
		this.dhNoColor = dhNoColor;
	}

	
	public String getDhHasColor() {
		return dhHasColor;
	}

	public void setDhHasColor(String dhHasColor) {
		this.dhHasColor = dhHasColor;
	}

	@JSONField(serialize = false)
	public Set<UserRole> getUserRole() {
		return userRole;
	}

	public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}


	public String getIsSubModule() {
		return isSubModule;
	}

	public void setIsSubModule(String isSubModule) {
		this.isSubModule = isSubModule;
	}
	
	
   
}
