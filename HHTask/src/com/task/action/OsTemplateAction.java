package com.task.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.MapKey;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import com.task.Dao.TotalDao;
import com.task.Server.OsTemplateService;
import com.task.Server.UserServer;
import com.task.Server.gzbj.ProcessGzstoreServer;
import com.task.entity.android.OsScope;
import com.task.entity.android.OsTemplate;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.PurchasedPartsInput;
import com.task.util.MKUtil;

public class OsTemplateAction extends ActionSupport {
	private OsTemplateService osTemplateService;
	private ProcessGzstoreServer processGzstoreServer;
	private UserServer userService;
	private OsTemplate t;
	private OsScope tt;
	private ProcessGzstore pg;
	private String status;
	private Integer xjbzId;
	
	public OsScope getTt() {
		return tt;
	}

	public void setTt(OsScope tt) {
		this.tt = tt;
	}

	private List<OsTemplate> ts;
	private List<OsScope> sc;
	private int size;
	 
	private String attachment1;
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;
	
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	
	private String errorMessage;
	private String successMessage;// 成功信息
	private List<String> markIdList;
	private String usercode;
	private String password;
	private String jsonText;
	private String markIds;//多个件号用","隔开
	private String paramMsg;//参数字符串
	private String partNumber;
	private Integer id;
	private String gongxuNum;
	private ProcardTemplate procardTemplate;
	
	public String addOsScope() {
		osTemplateService.addOsScope(tt,t);
		errorMessage="添加成功！！！";
		return "addOsScope";
	}
	public String addOsScope1() {
		OsTemplate t1=osTemplateService.byIdOsTemplate(t.getId());
		sc=t.getScope();
		osTemplateService.addOsScope1(sc, t);
		//后面的为添加到工序库上面的；
		errorMessage="添加成功！！！";
		return "showScopeAction";
	}
	public String updateOsScope1(){
		if(pg!=null){
			sc = processGzstoreServer.getOsListbyId(pg.getId());
			osTemplateService.shuaxinsScope(sc, ts);
			pg.setStatus("已更新");
			if(processGzstoreServer.update(pg)){
				errorMessage="添加成功！！！";
				return "addOsScope";
			}
		}
		errorMessage="更新失败！！！";
		return ERROR;
	}
	
	public String addInput(){
		if(id!=null ){
			procardTemplate=osTemplateService.getGongjianxinxi(id);
			t=osTemplateService.getOsTemplate1(procardTemplate.getMarkId(), gongxuNum);
			pg = osTemplateService.getProcessGzstorebyid(id);
			if(pg!=null){
				sc = processGzstoreServer.getOsListbyId(pg.getId());
			}
			size = sc.size();
			return  "addInput1";//OsTemplate_addInput1.jsp
		}
		return "addInput";//OsTemplate_addInput.jsp
	}
	public String add(){
		File file  =null;
		try {
			if(attachment1!=null && !"".equals(attachment1)){
				String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/processTz")
				+ "/";
				fileRealPath +=attachment1;
				file = new File(fileRealPath);
				attachment = new File[]{file};
				String[] str=	attachment1.split("/");
				if(str!=null && str.length==2){
					attachmentFileName = new String[]{str[1]};
				}
			}
			String filename = MKUtil.saveFile(attachment, attachmentFileName,"OsTemplate" );
			t.setFilename(filename);
		} catch (Exception e) {
			errorMessage = "请上传图纸";
			return "error";
		}
		
		
		errorMessage = "添加失败!";
		if(osTemplateService.add(t)){
			errorMessage ="添加成功!";
		}	
			sc = t.getScope();
		return "error";
	}
	public String deleteOsTemplate() {
		t=osTemplateService.byIdOsTemplate(t.getId());
		osTemplateService.deleteOsTemplate(t);
		//errorMessage="删除成功！！！";
		//url="OsTemplate_list.action";
		if("xj".equals(status)){
			return "InsTemplate_listdel";
		}
		return "deleteOsTemplate";
	}
	public void findAllmarkelist(){
		try {
			markIdList = osTemplateService.findAllmarkidlist();
			if(markIdList!=null && markIdList.size()>0){
				MKUtil.writeJSON(markIdList);
			}
		} catch (Exception e) {
			MKUtil.writeJSON("error");
			e.printStackTrace();
	}	
		
	}
	public String toUpdateOsTemplate() {
		if("update_error".equals(errorMessage)){
			errorMessage = "修改失败";
		}else if("update_true".equals(errorMessage)){
			errorMessage = "修改成功!";
		}
		t=osTemplateService.byIdOsTemplate(t.getId());
		if("xj".equals(status)){
			sc = osTemplateService.getScopes(t);
			if(sc!=null && sc.size()>0){
				size = sc.size();
			}
			return "InsTemplate_updateInput";
		}
		return "toUpdateOsTemplate";
	}
	public String updateOsTemplate() {
		OsTemplate newOsTemplate=osTemplateService.byIdOsTemplate(t.getId());
		if (attachment!=null) {
			String filename = MKUtil.saveFile(attachment, attachmentFileName,"OsTemplate" );
			t.setFilename(filename);
		} else {
			t.setFilename(newOsTemplate.getFilename());
		}
		sc=osTemplateService.getScopes(t);
		Set<OsScope> set = new HashSet(sc);
		t.setScopes(set);
		osTemplateService.updateOsTemplate(t);
		errorMessage = "修改成功！";
		return "updateOsTemplate";
	}
	public String updateOsTemplatexj() {
		File file  =null;
		if(attachment1!=null && !"".equals(attachment1)){
			String fileRealPath = ServletActionContext.getServletContext()
			.getRealPath("/upload/file/processTz")
			+ "/";
			fileRealPath +=attachment1;
			file = new File(fileRealPath);
			attachment = new File[]{file};
			String[] str=	attachment1.split("/");
			if(str!=null && str.length==2){
				attachmentFileName = new String[]{str[1]};
			}
		}
		if(attachment!=null){
			String filename = MKUtil.saveFile(attachment, attachmentFileName,"OsTemplate" );
			t.setFilename(filename);
		}
		errorMessage = "update_error";
		sc =  t.getScope();
		Set<OsScope> set = new HashSet(sc);
		t.setScopes(set);
		if(osTemplateService.updateOsTemplate(t)){
			errorMessage = "update_true";
		}
		return	"updatexjsuccess";
	}

	
	public String list(){
		if (t != null) {
			ActionContext.getContext().getSession().put("t", t);
		} else {
			t = (OsTemplate) ActionContext.getContext().getSession()
					.get("t");
		}
		Object[] object = osTemplateService.list(t,Integer.parseInt(cpage), pageSize,status);
		if (object != null && object.length > 0) {
			ts = (List<OsTemplate>) object[0];
			if (ts != null && ts.size() > 0) {
				int sum = (Integer) object[1];
				int pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("OsTemplate_list.action?status="+status);
				errorMessage = null;
			} else {
				errorMessage = "抱歉!您查询的模版不存在!";
			}
		} else {
			errorMessage = "抱歉!没有您查询的模版!";
		}
		return "list";//OsTemplate_list.jsp
	}
	
	public String getData(){
		
		if(!userService.login(usercode, password)){
			MKUtil.writeJSON(false, "工号或者密码错误", null);
			return null;
		}
		try {
			ts = osTemplateService.getData();
			MKUtil.writeJSON(true, "数据下载成功", ts);
			
//			Map<String, Object> maps = new HashMap<String, Object>();
//			maps.put("success", true);
//			maps.put("message", "数据下载成功");
//			maps.put("data", ts);
//			String JSONStr = JSON.toJSONString(maps, SerializerFeature.WriteDateUseDateFormat);
//
//			HttpServletResponse response = ServletActionContext.getResponse();
//			response.setCharacterEncoding("UTF-8");
//			response.setContentType("text/html;charset=UTF-8");
//			response.getWriter().write(JSONStr);
//			response.getWriter().close();
			
		} catch (Exception e) {
			MKUtil.writeJSON(false, "数据下载失败", null);
			e.printStackTrace();
		}
		return null;
	}
	public void cheackMarkId(){
		List<PurchasedPartsInput> ppiList=null;
		try{
			StringBuffer markIds2=new StringBuffer();
			if(markIds!=null&&markIds.length()>0){
				markIds = markIds.replaceAll(" ", "").replaceAll("\\[", "")
						.replaceAll("\\]", "");
				String []markIdStrings=markIds.split(",");
				if(markIdStrings.length>0){
					int i=0;
					for(String markId:markIdStrings){
						if(i==0){
							markIds2.append("'"+markId+"'");
						}else{
							markIds2.append(",'"+markId+"'");
						}
						i++;
					}
				}
			}else{
				MKUtil.writeJSON(false, "数据异常", null);
				return;
			}
			ppiList=osTemplateService.cheackMarkId(markIds2.toString());
		}catch (Exception e) {
			// TODO: handle exception
			MKUtil.writeJSON(false, "数据异常", null);
			return;
		}
		if(ppiList!=null&&ppiList.size()!=0){
			MKUtil.writeJSON(true,null,ppiList);
			return;
		}else{
			MKUtil.writeJSON(false,"无",null);
			return;
		}
	}
	/**
	 * 获得要申请检查的模板
	 */
	public void getApplyMarkId(){
		List<OsTemplate> osiList=null;
		try{
			osiList=osTemplateService.getApplyMarkId();
		}catch (Exception e) {
			// TODO: handle exception
			MKUtil.writeJSON(false, "数据异常", null);
			return;
		}
		if(osiList!=null&&osiList.size()!=0){
			MKUtil.writeJSON(true,null,osiList);
			return;
		}else{
			MKUtil.writeJSON(false,"无",null);
			return;
		}
	}
	public String showScope(){
		sc = osTemplateService.getScopes(t);
		if("xj".equals(status)){
			return "InsScope_get";
		}
		return "showScope";
	}
	public String deleteOsScope() {
		//tt=osTemplateService.byIdOsScope()
		//osTemplateService.deleteOsScope(tt);
		if(osTemplateService.deleteOsScope1(t,tt)){
			errorMessage="删除成功！！！";
			url="OsTemplate_showScope.action?t.id="+t.getId();
		};
		return ERROR;
	}
	public String toUpdateOsScope() {
		tt=osTemplateService.byIdOsScope(tt.getId());
		if("xj".equals(status)){
			sc = osTemplateService.getScopes(t);
			return "InsScope_get";
		}
		return "UpdateOsScope";
	}
	public String UpdateOsScope() {
	//	OsScope newOsScopete=osTemplateService.byIdOsScope(tt.getId());
		osTemplateService.UpdateOsScope(tt);
		errorMessage="修改成功！！！";
		if("xj".equals(status)){
			return "showScopeAction";
		}
		return   "UpdateOsScope";
	}

	//把之前InsTemplate表上的数据更新到OsTemplate这个表上去;
	
	public void addAll(){
		boolean bool=osTemplateService.addAll();
		if(bool){
			try {
				MKUtil.writeJSON("true");
			} catch (Exception e) {
				MKUtil.writeJSON(e);
				e.printStackTrace();
			}
		}
	}
	//更新图纸；
	public String updatetuzi(){
		errorMessage =	osTemplateService.updatetuzhi();
		if("true".equals(errorMessage)){
			errorMessage = "OK!";
		}
		return "error";
	}
	//根据件号查询没有模板的版本号;
	public void findbanbenBymarkId(){
		try {
			List<ProcardTemplate> ptList =	osTemplateService.findbanbenBymarkId(markIds);
			MKUtil.writeJSON(ptList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//修改巡检标准
	public void updateXjbz(){
		try {
			errorMessage =	osTemplateService.updateXjbz(id, xjbzId);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OsTemplateService getOsTemplateService() {
		return osTemplateService;
	}

	public void setOsTemplateService(OsTemplateService osTemplateService) {
		this.osTemplateService = osTemplateService;
	}

	public OsTemplate getT() {
		return t;
	}

	public void setT(OsTemplate t) {
		this.t = t;
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

	public List<OsTemplate> getTs() {
		return ts;
	}

	public void setTs(List<OsTemplate> ts) {
		this.ts = ts;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public UserServer getUserService() {
		return userService;
	}

	public void setUserService(UserServer userService) {
		this.userService = userService;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJsonText() {
		return jsonText;
	}

	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
	}

	public List<OsScope> getSc() {
		return sc;
	}

	public void setSc(List<OsScope> sc) {
		this.sc = sc;
	}

	public String getMarkIds() {
		return markIds;
	}

	public void setMarkIds(String markIds) {
		this.markIds = markIds;
	}

	public String getParamMsg() {
		return paramMsg;
	}

	public void setParamMsg(String paramMsg) {
		this.paramMsg = paramMsg;
	}

	public void setProcardTemplate(ProcardTemplate procardTemplate) {
		this.procardTemplate = procardTemplate;
	}

	public ProcardTemplate getProcardTemplate() {
		return procardTemplate;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setGongxuNum(String gongxuNum) {
		this.gongxuNum = gongxuNum;
	}

	public String getGongxuNum() {
		return gongxuNum;
	}

	public ProcessGzstoreServer getProcessGzstoreServer() {
		return processGzstoreServer;
	}

	public void setProcessGzstoreServer(ProcessGzstoreServer processGzstoreServer) {
		this.processGzstoreServer = processGzstoreServer;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public ProcessGzstore getPg() {
		return pg;
	}

	public void setPg(ProcessGzstore pg) {
		this.pg = pg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<String> getMarkIdList() {
		return markIdList;
	}

	public void setMarkIdList(List<String> markIdList) {
		this.markIdList = markIdList;
	}

	public String getAttachment1() {
		return attachment1;
	}

	public void setAttachment1(String attachment1) {
		this.attachment1 = attachment1;
	}

	public Integer getXjbzId() {
		return xjbzId;
	}

	public void setXjbzId(Integer xjbzId) {
		this.xjbzId = xjbzId;
	}

	

}
