package com.task.action.gzbj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.gzbj.GzstoreServer;
import com.task.entity.DeptNumber;
import com.task.entity.Machine;
import com.task.entity.Store;
import com.task.entity.Users;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.gzbj.Checkrecord;
import com.task.entity.gzbj.GzMsn;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.util.MKUtil;

public class GzstoreAction extends ActionSupport {

	private List list;
	private GzstoreServer gzstoreServer;
	private Gzstore gzstore;
	private Machine machine;
	private String no;
	private String errorMessage;
	private ProcessGzstore processGzstore;
	private Integer process_id;
	private List<Map> maps;
	private List<Gzstore> gzList;
	private Integer id1;
	private Integer del_id;
	private Integer edit_id;
	private File uploadFile;
	private String status;
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private String matetag;
	private String place;
	private Integer id;
	private String ids;
	private Store store;
	private String hql;
	private List<Store> storeList;
	private Integer process_id1;
	private String processName;
	private Users users;
	List<ProcessGzstore> hadList;
	List<ProcessGzstore> unHadList;
	Integer count;
	int[] checkboxs;
	private String variable;
	
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;
	private List<Checkrecord> cdList;
	private GzMsn gzMsn;
	private List<GzMsn> gsMsnList;
	public Integer getEdit_id() {
		return edit_id;
	}

	public void setEdit_id(Integer editId) {
		edit_id = editId;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public Integer getDel_id() {
		return del_id;
	}

	public void setDel_id(Integer delId) {
		del_id = delId;
	}

	public Integer getProcess_id1() {
		return process_id1;
	}

	public void setProcess_id1(Integer processId1) {
		process_id1 = processId1;
	}

	public Integer getId1() {
		return id1;
	}

	public void setId1(Integer id1) {
		this.id1 = id1;
	}

	public List<Map> getMaps() {
		return maps;
	}

	public void setMaps(List<Map> maps) {
		this.maps = maps;
	}

	public Integer getProcess_id() {
		return process_id;
	}

	public void setProcess_id(Integer processId) {
		process_id = processId;
	}

	public ProcessGzstore getProcessGzstore() {
		return processGzstore;
	}

	public void setProcessGzstore(ProcessGzstore processGzstore) {
		this.processGzstore = processGzstore;
	}

	public List<Store> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<Store> storeList) {
		this.storeList = storeList;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getHql() {
		return hql;
	}

	public void setHql(String hql) {
		this.hql = hql;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMatetag() {
		return matetag;
	}

	public void setMatetag(String matetag) {
		this.matetag = matetag;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public Gzstore getGzstore() {
		return gzstore;
	}

	public void setGzstore(Gzstore gzstore) {
		this.gzstore = gzstore;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public GzstoreServer getGzstoreServer() {
		return gzstoreServer;
	}

	public void setGzstoreServer(GzstoreServer gzstoreServer) {
		this.gzstoreServer = gzstoreServer;
	}

	/***
	 * 
	 */

	// ajxa获取部门信息
	public void getdept() {
		List<DeptNumber> list = gzstoreServer.getDept(variable);
		try {
			MKUtil.writeJSON(list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * 通过工位获取设备编号
	 */
	public void getMachineBygongxu() {
		List list = gzstoreServer.getMachineBygongxu(matetag);
		try {
			MKUtil.writeJSON(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ajax通过部门id获取部门下的人员
	public void getusers() {
		List list = gzstoreServer.getUsersByDeptId(id,ids,variable);
		try {
			MKUtil.writeJSON(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 展示人员所拥有操作权限的的工序情况
	 * 
	 * @return
	 */
	public String userProcessView() {
		if (id != null) {
			users = new Users();
			users.setId(id);
		}
		Map<Integer, Object> map = gzstoreServer.getUserProcessMap(users
				.getId(), Integer.parseInt(cpage), pageSize);
		if (map != null) {
			hadList = (List<ProcessGzstore>) map.get(1);
			unHadList = (List<ProcessGzstore>) map.get(2);
			this.users = (Users) map.get(3);
			this.count = hadList.size();
			this.total = map.get(4).toString();
			this.url = "GzstoreAction_userProcessView.action?id="
					+ users.getId();
		} else {
			errorMessage = "查看失败,不存在该人员！";
		}
		return "userprocess";

	}

	/**
	 * 绑定人员所拥有操作权限的的工序情况
	 * 
	 * @return
	 */
	public String linkUserProcess() {
		boolean b = gzstoreServer.linkUserProcess(users.getId(), checkboxs);
		if (b) {
			errorMessage = "绑定成功！";
		} else {
			errorMessage = "绑定失败！";
		}
		return userProcessView();
	}

	// 工装报检显示
	public String findAll() throws Exception {
		if (gzstore != null) {
			ActionContext.getContext().getSession().put("gzstore", gzstore);
		} else {
			gzstore = (Gzstore) ActionContext.getContext().getSession().get(
					"gzstore");
		}
		gzList = gzstoreServer.findAllDjyGz(gzstore,status);
		Object[] object = gzstoreServer.saveGzbjstroe(gzstore, Integer
				.parseInt(cpage), pageSize, this.edit_id,status);
		if (object != null && object.length > 0) {
			// list = (List<Gzstore>) object[0];
			maps = (List<Map>) object[0];
			if (maps != null && maps.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GzstoreAction_findAll.action?status="+status);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "findBygzbj";//gzbj.jsp
	}

	// 查看明细（查询工装信息）
	public String findGgbj_Process() {
		if (this.gzstore != null) {
			ActionContext.getContext().getSession().put("gzstore", gzstore);
		} else {
			gzstore = (Gzstore) ActionContext.getContext().getSession().get(
					"gzstore");
		}
		Object[] object = this.gzstoreServer.saveGzbj_process(id, gzstore,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			maps = (List<Map>) object[0];
			if (maps != null && maps.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GzstoreAction_findGgbj_Process.action?id=" + id);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "findByProcess";
	}

	// 查看对应工设备
	public String findGgbj_Process1() {
		if (this.machine != null) {
			ActionContext.getContext().getSession().put("machine", machine);
		} else {
			machine = (Machine) ActionContext.getContext().getSession().get(
					"machine");
		}
		Object[] object = this.gzstoreServer.save_Machineprocess(id, machine,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			maps = (List<Map>) object[0];
			if (maps != null && maps.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GzstoreAction_findGgbj_Process1.action?id=" + id);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "findByProcess1";//Gzbj_findProcess1.jsp
	}

	// 获得设备集合 根据工序ID
	public String getMachineListByGongxuId() {
		List machineList = this.gzstoreServer.getMachineListByGongxuId(id);
		MKUtil.writeJSON(true, "操作成功", machineList);
		return null;
	}

	// 获得设备集合 根据工序名称
	public String getMachineListByGongxuName() {
		List machineList = this.gzstoreServer
				.getMachinesByProcessName(processName);
		if (machineList != null && machineList.size() > 0) {
			MKUtil.writeJSON(true, "操作成功", machineList);
		}
		return null;
	}

	// 获得工装集合根据型别
	public String getGzstoreListByXingbie() {
		List gzstoreList = this.gzstoreServer
				.getGzstoreListByXingbie(this.gzstore.getXingbie());
		MKUtil.writeJSON(true, "操作成功", gzstoreList);
		return null;
	}

	/**
	 * 通过工装的型别和编号或名字的个别字符获得全称
	 */
	public void getGzstoreAllName() {
		List gzstoreList = this.gzstoreServer.getGzstoreAllName(this.gzstore
				.getXingbie(), this.gzstore.getNumber());
		MKUtil.writeJSON(gzstoreList);
	}

	/**
	 * 对工序修改新工装
	 */
	public void updateqpinfroGz() {
		if (gzstore != null && gzstore.getId() != null && process_id != null) {
			boolean b = gzstoreServer.updateqpinfroGz(process_id, gzstore
					.getId());
			if (b) {
				MKUtil.writeJSON(b, "修改成功!", null);
			} else {
				MKUtil.writeJSON(b, "修改失败!", null);
			}
		} else {
			MKUtil.writeJSON(false, "修改失败,您选中的工序或者工装不存在!", null);
		}
	}

	// 根据编号查询工装对应的工序信息
	public String findProcessById() throws Exception {
		// 根据id查询工序信息
		this.processGzstore = this.gzstoreServer.findProcessById(process_id);
		// this.processGzstore =
		// this.gzstoreServer.findProcessByprocess_id(process_id);
		// 根据id查询工装信息
		// Integer id = processGzstore.getGzstore().getId();
		// this.gzstore = this.gzstoreServer.gzstoreServer(id);
		return "findProcessById";
	}

	public String findGzbjById() {
		this.gzstore = this.gzstoreServer.findGzbjById(process_id);
		return "findGzbjById";
	}

	// 修改工序
	public String toUpdateProcess() {
		this.gzstoreServer.updateProcess(this.processGzstore);
		errorMessage = "修改成功!";
		return "toUpdateProcess";
	}

	public String toUpdateGzbj() {
		this.gzstoreServer.updateGzbj(this.gzstore);
		errorMessage = "修改成功!";
		return "toUpdateProcess";
	}

	// 添加工装时显示工装下拉信息先把工装信息查询一遍
	public String toaddProcess() throws Exception {
		list = this.gzstoreServer.gzstoreServer1(this.process_id);
		return "toaddProcess";
	}

	// 添加设备时显示工装下拉信息先把工装信息查询一遍
	public String toaddProcess1() throws Exception {
		if (no != null) {
			ActionContext.getContext().getSession().put("no", no);
		} else {
			no = (String) ActionContext.getContext().getSession().get(
					"no");
		}
		list = this.gzstoreServer.gzstoreServer2(no,this.process_id);
		return "toaddProcess1";//
	}

	// 添加设备信息
	public String addProcess1() {
		this.gzstoreServer.savemachine_process2(checkboxs,
				this.process_id);
		errorMessage = "添加成功!";
		return "doaddProcess1";
	}

	// 添加工序信息
	public String addProcess() {
		this.gzstoreServer.saveGzbj_process2(this.gzstore.getId(),
				this.process_id);
		errorMessage = "添加成功!";
		return "doaddProcess";
	}

	// 添加工序信息
	public String addProcess4() {
		boolean b = gzstoreServer.isNameExist(processGzstore.getProcessName());
		if (b) {
			errorMessage = "添加失败!该工号名已使用";
			return "doaddProcess";
		}
		this.gzstoreServer.saveGzbj_process4(this.processGzstore, id1);
		errorMessage = "添加成功!";
		return "doaddProcess";
	}

	// 删除工序
	public String delProcess() {
		this.gzstoreServer.delProcessById(process_id1, this.id);
		return "delProcess";
	}

	// 删除工序
	public String delProcess2() {
		this.gzstoreServer.delProcessById2(del_id);
		return "delProcess2";
	}

	// 删除设备
	public String delProcess1() {
		this.gzstoreServer.delProcessById1(process_id1, this.id);
		return "delProcess1";
	}

	// 根据编号查询工装报检信息
	public String initUpdate() {
		gzstore = gzstoreServer.gzstoreServer(this.id);
		if("jy".equals(status)){
			return "gz_jiaoyan";
		}
		return "doupdate";//editgzbj.jsp
	}

	// 修改工装报检信息
	public String toUpdate() {
		if(attachment!=null && attachment.length>0){
			String fileName =	MKUtil.saveFile(attachment, attachmentFileName, "gzstore");
			gzstore.setFileName(fileName);
		}
		gzstoreServer.updateGzstore(gzstore);
		if (this.id1 != null) {
			this.edit_id = 1;
		}
		return "toupdate";
	}

	// 删除报检周期
	public String DelGzbj() {
		gzstoreServer.delGzbj(this.del_id);
		return "delGzbj";
	}

	// 添加工装报检记录
	public String addGzbj() {
		if(attachment!=null && attachment.length>0){
			String fileName =	MKUtil.saveFile(attachment, attachmentFileName, "gzstore");
			gzstore.setFileName(fileName);
		}
		errorMessage =	this.gzstoreServer.saveGzbj(gzstore);
		if("true".equals(errorMessage)){
			errorMessage ="添加成功!~";
		}
		if (this.id1 != null) {
			this.edit_id = 1;
		}
		return "addGzbj";
	}

	// 查询对应工序
	public String toupdateProcess() {
		this.processGzstore = this.gzstoreServer.findProcessById(id);
		return "toupdateProcess1";
	}

	// 修改工序
	public String doupdateProcess() {
		this.gzstoreServer.updateProcess1(processGzstore);
		errorMessage = "修改成功!";
		return "toupdateProcess1";
	}
	public String importFile(){
		errorMessage = gzstoreServer.importFile(uploadFile);
		if("true".equals(errorMessage)){
			return "delGzbj";
		}else{
			return "error";
		}
		
	}
	//添加工装校验记录
	public String gzjiaoyan(){
		String filename = "";
		if (attachment != null && attachment.length > 0) {
			filename = MKUtil.saveFile(attachment, attachmentFileName,
					"DefectiveProduct");
		}
		gzstore.setFileName(filename);
		errorMessage =	gzstoreServer.gzjiaoyan(gzstore, status);
		if("true".equals(errorMessage)){
			errorMessage = "校验成功!~";
			url ="GzstoreAction_findAll.action?cpage="+cpage;
		}
		return "error";
	}
	//查询检验记录
	public String findcdList(){
		Object[] obj =gzstoreServer.findAllcdList(id);
		cdList = (List<Checkrecord>) obj[0];
		Integer count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("GzstoreAction_findcdList.action?id="+id);
		return "cdList_show";
	}
	public String addGzMsn(){
		errorMessage = gzstoreServer.addGzMsn(gzMsn);
		return "error";
	}
	public String findGzMsn() {
		if (gzMsn!= null) {
			ActionContext.getContext().getSession().put("gzMsn", gzMsn);
		} else {
			gzMsn = (GzMsn) ActionContext.getContext().getSession().get(
					"gzMsn");
		}
		Map<Integer, Object> map = gzstoreServer.findGzMsn(gzMsn, Integer
				.parseInt(cpage), pageSize);
		gsMsnList = (List<GzMsn>) map.get(1);
		if (gsMsnList != null & gsMsnList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("GzstoreAction_findGzMsn.action");
		}else{
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "gzMsn_List";
	}

	/**
	 * 通过工装的型别和编号或名字的个别字符获得全称
	 */
	public void getAllGzstore() {
		gzList = gzstoreServer.findgzList();
		MKUtil.writeJSON(gzList);
	}
	public String delGzMsn(){
		errorMessage = gzstoreServer.delGzMsn(gzMsn);
		return "error";
	}
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public List<ProcessGzstore> getHadList() {
		return hadList;
	}

	public void setHadList(List<ProcessGzstore> hadList) {
		this.hadList = hadList;
	}

	public List<ProcessGzstore> getUnHadList() {
		return unHadList;
	}

	public void setUnHadList(List<ProcessGzstore> unHadList) {
		this.unHadList = unHadList;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public int[] getCheckboxs() {
		return checkboxs;
	}

	public void setCheckboxs(int[] checkboxs) {
		this.checkboxs = checkboxs;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public List<Gzstore> getGzList() {
		return gzList;
	}

	public void setGzList(List<Gzstore> gzList) {
		this.gzList = gzList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public File[] getAttachment() {
		return attachment;
	}

	public void setAttachment(File[] attachment) {
		this.attachment = attachment;
	}

	public String[] getAttachmentContentType() {
		return attachmentContentType;
	}

	public void setAttachmentContentType(String[] attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}

	public String[] getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String[] attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public List<Checkrecord> getCdList() {
		return cdList;
	}

	public void setCdList(List<Checkrecord> cdList) {
		this.cdList = cdList;
	}

	public GzMsn getGzMsn() {
		return gzMsn;
	}

	public void setGzMsn(GzMsn gzMsn) {
		this.gzMsn = gzMsn;
	}

	public List<GzMsn> getGsMsnList() {
		return gsMsnList;
	}

	public void setGsMsnList(List<GzMsn> gsMsnList) {
		this.gsMsnList = gsMsnList;
	}
	
	
	
}
