package com.task.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.DeptNumberServer;
import com.task.entity.DeptNumber;
import com.task.entity.Users;
import com.task.util.MKUtil;
import com.task.util.StrutsTreeNode;

/**
 * 部门编码Action层
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings("serial")
public class DeptNumberAction extends ActionSupport {

	private DeptNumberServer deptNumberServer;// Server层

	private DeptNumber deptNumber;// 对象
	private List<DeptNumber> deptNumberList;// 集合
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private int id;// id
	private int id1;
	private Integer xuhao1;
	private Integer xuhao2;
	private String moveType;
	private StrutsTreeNode root;
	private Integer belongLayer;
	
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;

	// 查询所有部门为页面Select使用
	public String finAllDeptNumberForSetlect() {
		String message = deptNumberServer.finAllDeptNumberForSetlect(belongLayer);
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(message);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ERROR;
	}

	// 查询所有的部门输出json
	@SuppressWarnings("unchecked")
	public String findAllDept() {
		List list = deptNumberServer.findAllDeptForJson();
		if(list == null || list.size() == 0){
			if(deptNumberServer.addfirst()){
				list = deptNumberServer.findAllDeptForJson();
			}
		}
		List<DeptNumber> newList = new ArrayList<DeptNumber>();
		for (int i = 0; i < list.size(); i++) {
			DeptNumber dept = (DeptNumber) list.get(i);
			dept.setSbRateSet(null);
			newList.add(dept);
		}
		MKUtil.writeJSON(newList);
		return null;
	}

	// 查询树形 部门结构
	@SuppressWarnings("unchecked")
	public String getById() {
		try {
			if (successMessage != null) {
				successMessage = URLDecoder.decode(successMessage, "utf-8");
			}
			if(id>0){
				deptNumber = deptNumberServer.findDeptNumberById(id);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		List treeList = deptNumberServer.findAllDept();
		//this.root = StrutsTreeUtil.getTreeRoot(treeList);
		return "getTreeSuccess";
	}

	// 通过ID查询部门编号 (为页面部门以及部门编号赋值)
	public String findDeptNumberByIdForTree() throws IOException {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		deptNumber = deptNumberServer.findDeptNumberById(id);
		if (deptNumber != null) {
			if (deptNumber.getDeptNumber() == null) {
				deptNumber.setDeptNumber("");
			}
			String message = deptNumber.getDept() + "|"
					+ deptNumber.getDeptNumber();
			response.getWriter().write(message);
		} else {
			response.getWriter().write("");
		}
		response.getWriter().close();
		return null;
	}
	public String getdeptbyId(){
		if(id>0){
			deptNumber = deptNumberServer.findDeptNumberById(id);
		}
		if(deptNumber!=null){
			
		}
		return "getdeptbyId";
	}
	
	// 添加部门
	public String addDeptNumber() {
		DeptNumber fatherDeptNumber = deptNumberServer.findDeptNumberById(id);
		errorMessage = deptNumberServer.addDeptNumber(deptNumber,
				fatherDeptNumber);
		if (errorMessage.equals("true")) {
			try {
				successMessage = URLEncoder.encode(URLEncoder.encode("添加部门  "
						+ deptNumber.getDept() + " 成功!", "utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "addDeptNumberSuccess";
		}
		return ERROR;
	}

	// 删除部门
	@SuppressWarnings("unchecked")
	public String delDeptNumber() {
		deptNumber = deptNumberServer.findDeptNumberById(id);// 查询部门
		if (deptNumber != null) {
			List list = deptNumberServer
					.findLowerDeptNumber(deptNumber.getId());// 查询下层所有部门
			if (list != null && list.size() <= 0) {
				List userList = deptNumberServer.findAllUsersByDept(deptNumber
						.getId());
				if (userList != null && userList.size() > 0) {
					errorMessage = "该部门下存在员工" + userList.size() + "人,无法删除该部门!";
				} else {
					if (deptNumberServer.delDept(deptNumber)) {
						try {
							successMessage = URLEncoder.encode(URLEncoder
									.encode("删除部门 " + deptNumber.getDept()
											+ " 成功!", "utf-8"), "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						return "addDeptNumberSuccess";
					}
				}
			} else {
				errorMessage = "请先删除该部门下分部门,再删除该部门!";
			}

		} else {
			errorMessage = "不存在该部门,请检查后重试!";
		}
		return ERROR;

	}
	public void findAlldept(){
		deptNumberList=deptNumberServer.findAllDept();
		if(deptNumberList!=null&&deptNumberList.size()>0){
			try {
				MKUtil.writeJSON(deptNumberList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void findAlldeptforhege(){
		deptNumberList=deptNumberServer.findAllDeptNogongyingshang();
		if(deptNumberList!=null&&deptNumberList.size()>0){
			try {
				MKUtil.writeJSON(deptNumberList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void addfirst(){
		boolean bool = deptNumberServer.addfirst();
		try {
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void findAlldept1(){
		deptNumberList=deptNumberServer.getDept();
		if(deptNumberList!=null&&deptNumberList.size()>0){
			try {
				MKUtil.writeJSON(deptNumberList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	// 修改部门信息
	public String updateDeptNumber() {
		DeptNumber oldDeptNumber = deptNumberServer.findDeptNumberById(id);
		errorMessage = deptNumberServer.updateDept(deptNumber, oldDeptNumber);
		if ("true".equals(errorMessage)) {
			try {
				successMessage = URLEncoder.encode(URLEncoder.encode("修改部门 "
						+ oldDeptNumber.getDept() + " 成功!", "utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "addDeptNumberSuccess";
		}
		return ERROR;

	}
	public String paixu(){
		deptNumberServer.paixu(deptNumber);
		return "";
	}
	//判断是否可被拖拽;
	public void istuozhuai(){
		try {
			boolean bool =	deptNumberServer.istuozhuai(id, id1, moveType);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//拖拽后更新数据;
	public void tuozhuaiAfterUpdate(){
		try {
		errorMessage =	deptNumberServer.tuozhuaiAfterUpdate(id, id1, xuhao1, xuhao2, moveType);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			MKUtil.writeJSON("error");
		}
		
		
	}
	
	public void findUsersBydept(){
		try {
			List<Users> userList =	deptNumberServer.findUsersBydept(moveType);
			MKUtil.writeJSON(userList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DeptNumberServer getDeptNumberServer() {
		return deptNumberServer;
	}

	public void setDeptNumberServer(DeptNumberServer deptNumberServer) {
		this.deptNumberServer = deptNumberServer;
	}

	public DeptNumber getDeptNumber() {
		return deptNumber;
	}

	public void setDeptNumber(DeptNumber deptNumber) {
		this.deptNumber = deptNumber;
	}

	public List<DeptNumber> getDeptNumberList() {
		return deptNumberList;
	}

	public void setDeptNumberList(List<DeptNumber> deptNumberList) {
		this.deptNumberList = deptNumberList;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		try {
			this.errorMessage = URLDecoder.decode(errorMessage, "utf_8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCpage() {
		return cpage;
	}

	public void setCpage(String cpage) {
		this.cpage = cpage;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public StrutsTreeNode getRoot() {
		return root;
	}

	public void setRoot(StrutsTreeNode root) {
		this.root = root;
	}

	public Integer getBelongLayer() {
		return belongLayer;
	}

	public void setBelongLayer(Integer belongLayer) {
		this.belongLayer = belongLayer;
	}

	public int getId1() {
		return id1;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public Integer getXuhao1() {
		return xuhao1;
	}

	public void setXuhao1(Integer xuhao1) {
		this.xuhao1 = xuhao1;
	}

	public Integer getXuhao2() {
		return xuhao2;
	}

	public void setXuhao2(Integer xuhao2) {
		this.xuhao2 = xuhao2;
	}

	
	

}