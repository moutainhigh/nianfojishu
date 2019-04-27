package com.task.action.sop;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.task.Server.sop.ProcardTemplateGyServer;
import com.task.Server.sop.ProcardTemplateSbServer;
import com.task.Server.sop.ProcardTemplateServer;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.entity.Users;
import com.task.entity.UsersGroup;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardAboutBanBenApply;
import com.task.entity.sop.ProcardBanBenJudge;
import com.task.entity.sop.ProcardReProduct;
import com.task.entity.sop.ProcardReProductFile;
import com.task.entity.sop.ProcardSbWaster;
import com.task.entity.sop.ProcardSbWasterxc;
import com.task.entity.sop.ProcardSbWg;
import com.task.entity.sop.ProcardSbWw;
import com.task.entity.sop.ProcardTBanbenRelation;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateAboutsbcltype;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardTemplateBanBenApply;
import com.task.entity.sop.ProcardTemplateBanBenJudges;
import com.task.entity.sop.ProcardTemplateChangeLog;
import com.task.entity.sop.ProcardTemplatesb;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFilesb;
import com.task.entity.sop.ProcessTemplatesb;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.UserDept;
import com.task.entity.zgkh.AssessPersonnel;
import com.task.util.MKUtil;
import com.task.util.Upload;
import com.task.util.Util;

/**
 * @author txb
 *
 */
public class ProcardTemplateSbAction {

	private ProcardTemplateGyServer procardTemplateGyServer;
	private ProcardTemplateSbServer procardTemplateSbServer;
	private List<ProcessTemplate> processList;
	private String processListString;
	private List<String> processStringList;
	private ProcardTemplateServer procardTemplateServer;// 流水卡片模板
	// use2findProcessByFkId
	private ProcardTemplatesb procardTemplatesb;// 对象
	private List<ProcardTemplatesb> procardTemplatesbList;// 对象
	private ProcessTemplate processTemplate;// 对象
	private ProcessTemplatesb processTemplatesb;// 对象
	private ProcardTemplateBanBenApply bbAply;// 版本升级申请
	private ProcardTemplateBanBen ptbb;// 版本升级明细
	private ProcardTBanbenRelation ptbbRelation;// 版本关系
	private List<ProcardTemplatesb> pageprocardTemplatesbList;// 流水卡模板列表
	private List<ProcessTemplate> processTemplateList;// 工序模板列表
	private List<ProcardTemplateBanBenApply> bbAplyList;// 模板版本升级申请列表
	private List<ProcardTemplateBanBen> ptbbList;// 模板版本升级申请明细列表
	private List<ProcardTBanbenRelation> ptbbRelationList;// 版本关系列表
	private List<ProcardAboutBanBenApply> pabbList;// 设变涉及生产任务
	private List<ProcardAboutBanBenApply> pabbList2;// 设变涉及生产任务
	private List<UserDept> userDeptList;// 部门负责人
	private List<UsersGroup> userGroupList;//成员组别列表
	private ProcardAboutBanBenApply pabb;//
	private ProcardTemplateBanBenJudges ptbbJudges;// 设变评审人员
	private List<ProcardTemplateBanBenJudges> ptbbJudgeslist;
	private List<ProcardBanBenJudge> pbbJudgeList;//
	private List<ProcardBanBenJudge> pbbJudgeList2;//
	private ProcardBanBenJudge pbbJudge;// 生产件评论
	private List<ProcardTemplateChangeLog> ptchangeLogList;//
	private ProcessTemplateFilesb processTemplateFile;// 工序工艺规范图纸
	private ProcardSbWg procardsbwg;// 设变影响外购情况
	private ProcardSbWw procardSbWw;// 设变影响外委情况
	private List<ProcardSbWg> procardsbwgList;//
	private List<ProcardSbWw> procardSbWwList;//
	private ProcessInforWWApplyDetail wwApplyDetail;
	private WaigouWaiweiPlan waigouWaiweiPlan;
	private List<WaigouWaiweiPlan> waigouWaiweiPlanList;
	private List<WaigouPlan> waigouPlanList;//
	private List<ManualOrderPlan> manyalOrderPlanList;// 物料需求
	private WaigouPlan waigouplan;//
	private ManualOrderPlan mop;// 物料需求明细
	private ProcessInfor processInfor;// 生产工序
	private List<ProcessInfor> processInforList;
	private List<ProcardReProductFile> prpFileList;// 返修图纸
	private ProcardReProductFile prpFile;// 返修图纸
	private Procard procard;// 生产件
	private ProcardSbWaster procardSbWaster;
	private List<ProcardSbWaster> procardSbWasterList;
	private ProcardSbWasterxc procardSbWasterxc;
	private List<ProcardSbWasterxc> procardSbWasterxcList;
	private List<ProcardTemplateAboutsbcltype> ptasbclTypeList;
	
	private Integer nextNo;// 下个工序号
	private List list;
	private Integer id;
	private Integer id2;
	private Integer rootId;
	private String ids;
	private Integer maxBelongLayer;// 最大层
	private YuanclAndWaigj yclAndWgj;// 原材料外购件
	private String type;// 类型
	private String remark;// 备注
	private String start;// 开始时间
	private String end;// 结束时间
	private String ytRadio;// 原图标记
	private String markId;
	private String bzjdCount;
	private String idname1;
	private String idname2;
	private String idname3;
	private String jobNum;
	private String messagePower;// 共用属性

	private String pageStatus;// 页面状态
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private File gygf;
	private String gygfFileName;
	private String gygfFileContentType;
	private File bomTree;
	private String bomTreeFileName;
	private String bomTreeFileContentType;
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;
	private String tag;// 查看图纸或上传缺陷图纸标识
	private int[] checkboxs;
	private String realPath;
	private List<ProcardReProduct> preProductList;// 返修单
	private ProcardReProduct preProduct;
	private String status;
	public List<String> file_uploadFileName;
	public List<String> file_uploadContentType;
	public List<File> file_upload;
	
	
	
	public void upptlvNew() {
		// MKUtil.writeJSON("test!");
		String errmsg = (String) ActionContext.getContext().getApplication()
		.get("upptlvNewtotal");
		if (errmsg == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"upptlvNewtotal",
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在提交设变零件请等他（她）提交之后再继续!");
		} else {
			MKUtil.writeJSON(errmsg);
			return;
		}
		
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get("upptlvNew" + bbAply.getId());
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"upptlvNew" + bbAply.getId(),
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在提交此设变单的设变零件，请勿重复处理!");
		} else {
			MKUtil.writeJSON(errorMessage);
			return;
		}
		Object[] objs = null;
		try {
			 objs = procardTemplateSbServer.upptlvnew(ptbbList, rootId,
					bbAply, ptbbJudges);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
			ActionContext.getContext().getApplication().put("upptlvNew" + bbAply.getId(),
					null);
			ActionContext.getContext().getApplication().put("upptlvNewtotal",
					null);
			return ;
		}
		if (objs != null) {
			ActionContext.getContext().getApplication().put("upptlvNew" + bbAply.getId(),
					null);
			ActionContext.getContext().getApplication().put("upptlvNewtotal",
					null);
			try {
				String msg = objs[0].toString();
				if (msg.equals("true")) {
					id = Integer.parseInt(objs[1].toString());
					bbAply = new ProcardTemplateBanBenApply();
					bbAply.setId(id);
					MKUtil.writeJSON("版本升级申请成功!");
				} else {
					MKUtil.writeJSON(msg);
				}
			} catch (Exception e) {
				// TODO: handle exception
				MKUtil.writeJSON(e.getMessage());
			}
		}
	}
	/**
	 * 根据件号和设变单Id或对应设变BOM的rootId
	 * @return
	 */
	public String showbzProcardTemplatesb(){
		rootId = procardTemplateSbServer.getBomrootId(id);
		return "procardTemplatesb_showBom";
	}
	
	/***
	 * 根据首层父类id查询流水卡片模板(页面生成树形结构)
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcardTemByRootId() {
		List<ProcardTemplate> proList = null;
		proList = procardTemplateSbServer.findProcardTemByRootId(id);
		MKUtil.writeJSON(proList);
//		if (pageStatus == null || !pageStatus.equals("shenyue")) {
//		} else {
//			proList = new ArrayList<ProcardTemplate>();
//			procardTemplate = procardTemplateServer.findProcardTemById(id);
//			proList.add(procardTemplate);
//		}
//		try {
//			MKUtil.writeJSON(proList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return null;
	}
	
	public String addProcardTemplate(){
		// 查询父类流水卡片
		if (procardTemplatesb.getMarkId() == null
				|| procardTemplatesb.getMarkId().length() == 0) {
			MKUtil.writeJSON(false, "件号不能为空,添加失败!", null);
			return null;
		}
		procardTemplatesb.setMarkId(procardTemplatesb.getMarkId().replace(" ", ""));
//		procardTemplate.setMarkId(procardTemplate.getMarkId().replace("	", ""));
		if (procardTemplateSbServer.checkHasSonMarkId(procardTemplatesb
				.getFatherId(), procardTemplatesb.getId(), procardTemplatesb
				.getMarkId())) {
			MKUtil.writeJSON(false, "该父卡下已有此件号,添加失败!", null);
			return null;
		}
		ProcardTemplatesb fatherProTem = procardTemplateSbServer
				.getProcardsbTemById(procardTemplatesb.getFatherId());
		if (fatherProTem != null) {
			if(fatherProTem.getBzStatus().equals("已批准")){
				MKUtil.writeJSON(false, "该父卡编制状态为已批准不能添加下层零件,请先去申请上层设变!", null);
				return null;
			}
			if (procardTemplatesb.getProcardStyle() != null
					&& procardTemplatesb.getProcardStyle().equals("外购")) {
				Float maxCount = 0f;
				if (procardTemplatesb.getQuanzi1() != null
						&& procardTemplatesb.getQuanzi2() != null
						&& (procardTemplatesb.getQuanzi2() - 0) != 0) {
					if (fatherProTem.getMaxCount() != null) {
						double count = Math.ceil(fatherProTem.getMaxCount()
								* procardTemplatesb.getQuanzi2()
								/ procardTemplatesb.getQuanzi1());
						// maxCount = fatherProTem.getMaxCount()
						maxCount = (float) count * procardTemplatesb.getQuanzi2()
								/ procardTemplatesb.getQuanzi1();
					}
				} else {
					MKUtil.writeJSON(false, "添加失败,请填写正确的外购件比例!", null);
					return null;
				}
				procardTemplatesb.setMaxCount(maxCount);
			} else if (procardTemplatesb.getProcardStyle() != null
					&& procardTemplatesb.getProcardStyle().equals("自制")) {
				Float maxCount = 0f;
				if (procardTemplatesb.getCorrCount() != null
						&& procardTemplatesb.getCorrCount() != null) {
					if (fatherProTem.getMaxCount() != null) {
						double count = Math.ceil(fatherProTem.getMaxCount()
								* procardTemplatesb.getCorrCount());
						maxCount = (float) count;
					}
					// 总成件号
					procardTemplatesb.setRootMarkId(fatherProTem.getRootMarkId());
				}
				procardTemplatesb.setMaxCount(maxCount);
			}

			procardTemplatesb.setProcardTemplatesb(fatherProTem);
			procardTemplatesb.setMarkId(procardTemplatesb.getMarkId().replaceAll(
					" ", ""));// 去除件号中的所有空格
			// 添加
			try {
				String msg = procardTemplateSbServer.addProcardTemplatesb(procardTemplatesb);
				if (msg.equals("true")) {
					MKUtil.writeJSON(true, "添加成功!", null);
				} else {
					MKUtil.writeJSON(true, msg, null);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				MKUtil.writeJSON(false, e.getMessage(), null);
			}
		}
		return null;
	
		
	}
	
	/***
	 * 删除流水卡片
	 * 
	 * @return
	 */
	public String delProcard() {
		ProcardTemplatesb oldProCard = procardTemplateSbServer.getProcardsbTemById(id);
		if (oldProCard != null) {
			try {
				String msg = procardTemplateSbServer
				.delProcardTemplate(oldProCard);
				if(msg.equals("true")){
//					msg="删除成功!";
				}else{
					errorMessage=msg;
				}
				MKUtil.writeJSON(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				errorMessage = e.getMessage();
			}
			
		}
		return null;
	}
	
	
	/**
	 * 设变零件图纸
	 * 
	 * @return
	 */
	public String showCardTz() {
		procardTemplatesb = procardTemplateSbServer.getProcardsbTemById(id);
		errorMessage = pageStatus;
		if (procardTemplatesb == null) {
			errorMessage = "对不起没有找到目标卡片!";
			return "error";
		}
		list = procardTemplateSbServer.findCardTemplatesbTz(id);
		return "ProcardTemplatesbTzs";
	}
	/**
	 * 设变工序图纸
	 * @return
	 */
	public String showProcessTz(){
		processTemplatesb = procardTemplateSbServer.findProcessT(id);
		list = procardTemplateSbServer.getProcesssbTz(id);
		if ("quexian".equals(tag))
			return "Processsb_showQXtz";
		else
			return "ProcessTemplatesbTzs";
	}
	
	/**
	 * 上传零件图纸
	 * @return
	 */
	public String updateProcardTz(){
		if (this.file_upload != null) {
			String fileName = file_uploadFileName.get(0);
			int index = fileName.lastIndexOf(".");
			String fileType = fileName.substring(index);
			/* set upload path */
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS")
					+ fileType;
			String realFileName2 = null;
			if (fileType.equalsIgnoreCase(".bmp")
					|| fileType.equalsIgnoreCase(".dib")
					|| fileType.equalsIgnoreCase(".gif")
					|| fileType.equalsIgnoreCase(".jfif")
					|| fileType.equalsIgnoreCase(".jpe")
					|| fileType.equalsIgnoreCase(".jpeg")
					|| fileType.equalsIgnoreCase(".jpg")
					|| fileType.equalsIgnoreCase(".png")
					|| fileType.equalsIgnoreCase(".tif")
					|| fileType.equalsIgnoreCase(".tiff")
					|| fileType.equalsIgnoreCase(".ico")
					|| fileType.equalsIgnoreCase(".pdf")
					|| fileType.equalsIgnoreCase(".PDF")) {
				realFileName2 = "jz_" + Util.getDateTime("yyyyMMddHHmmssSSS")
						+ fileType;
			}

			String realFilePath = "/upload/file/processTz/"
					+ Util.getDateTime("yyyy-MM");
			String path = ServletActionContext.getServletContext().getRealPath(
					realFilePath);
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();// 如果不存在文件夹就创建
			}
			// 保存文件
			Upload upload = new Upload();
			upload.UploadFile(file_upload.get(0), fileName, realFileName,
					realFilePath, null);
			if (realFileName2 != null) {
				if (fileType.equalsIgnoreCase(".bmp")
						|| fileType.equalsIgnoreCase(".dib")
						|| fileType.equalsIgnoreCase(".gif")
						|| fileType.equalsIgnoreCase(".jfif")
						|| fileType.equalsIgnoreCase(".jpe")
						|| fileType.equalsIgnoreCase(".jpeg")
						|| fileType.equalsIgnoreCase(".jpg")
						|| fileType.equalsIgnoreCase(".png")
						|| fileType.equalsIgnoreCase(".tif")
						|| fileType.equalsIgnoreCase(".tiff")
						|| fileType.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, path + "/"
							+ realFileName, path + "/" + realFileName2);
					/* set processTemplateFile */
				} else if (fileType.equalsIgnoreCase(".pdf")
						|| fileType.equalsIgnoreCase(".PDF")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					Util.markPDFByIcon(icon_fileRealPath, path + "/"
							+ realFileName, path + "/" + realFileName2);
				}
			}
			processTemplateFile.setFileName(realFileName);
			processTemplateFile.setFileName2(realFileName2);
			processTemplateFile.setMonth(Util.getDateTime("yyyy-MM"));
			fileName = fileName.replaceAll("-ZKT", "-展开图");
			fileName = fileName.replaceAll("-YT", "-原图");
			fileName = fileName.replaceAll("-GYK", "-工艺卡");
			processTemplateFile.setOldfileName(fileName);
			String msg = this.procardTemplateSbServer.saveProcardTemplateFile(
					this.processTemplateFile, id, ytRadio, tag);
			if (!msg.equals("true")) {
				errorMessage = msg;
				return "error";
			}
		} else {
			return "error";
		}
		return "error";

		/*
		 * if (this.attachment != null && attachment.length > 0) {
		 * 
		 * Users user = Util.getLoginUser(); if (user == null) { errorMessage =
		 * "请先登录"; return ERROR; } for (int i = 0; i < attachment.length; i++) {
		 * 
		 * String fileName = attachmentFileName[i]; // 文件路径 String fileType =
		 * fileName.substring(fileName.lastIndexOf("."), fileName.length());
		 * String realFileName = null; realFileName =
		 * Util.getDateTime("yyyyMMddHHmmss_") + i + fileType; String
		 * realFilePath = "/upload/file/processTz/" +
		 * Util.getDateTime("yyyy-MM"); // 打开存放上传文件的位置 String path =
		 * ServletActionContext.getServletContext() .getRealPath(realFilePath);
		 * File file = new File(path); if (!file.exists()) { file.mkdirs();//
		 * 如果不存在文件夹就创建 } this.processTemplateFile.setFileName(realFileName);
		 * processTemplateFile.setMonth(Util.getDateTime("yyyy-MM"));
		 * processTemplateFile.setOldfileName(fileName); String msg =
		 * this.procardTemplateGyServer
		 * .saveProcessTemplateFile(this.processTemplateFile, id, ytRadio); if
		 * (!msg.equals("true")) { errorMessage = msg; return ERROR; } Upload
		 * upload = new Upload(); upload.UploadFile(attachment[i], fileName,
		 * realFileName, realFilePath, null); } } errorMessage = "添加成功!"; url =
		 * "procardTemplateGyAction_showCardTz.action?id=" + id; return ERROR;
		 */

	}
	/**
	 * 上传工序图纸
	 * @return
	 */
	public String updateProcessTz() {
		if (this.attachment != null && attachment.length > 0) {
			Users user = Util.getLoginUser();
			if (user == null) {
				errorMessage = "请先登录";
				return "error";
			}
			for (int i = 0; i < attachment.length; i++) {
				String fileName = attachmentFileName[i];
				// 文件路径
				String fileType = fileName.substring(fileName.lastIndexOf("."),
						fileName.length());
				String realFileName  = Util.getDateTime("yyyyMMddHHmmssSSS_") + i
						+ fileType;
				String realFileName2  = null;
				if(fileType.equalsIgnoreCase(".bmp")||fileType.equalsIgnoreCase(".dib")
						||fileType.equalsIgnoreCase(".gif")||fileType.equalsIgnoreCase(".jfif")
						||fileType.equalsIgnoreCase(".jpe")||fileType.equalsIgnoreCase(".jpeg")
						||fileType.equalsIgnoreCase(".jpg")||fileType.equalsIgnoreCase(".png")
						||fileType.equalsIgnoreCase(".tif")||fileType.equalsIgnoreCase(".tiff")
						||fileType.equalsIgnoreCase(".ico")){
					realFileName2="jz_"+Util.getDateTime("yyyyMMddHHmmssSSS_") + i
					+ fileType;
				}
				
				String realFilePath = "/upload/file/processTz/"
						+ Util.getDateTime("yyyy-MM");
				// 打开存放上传文件的位置
				String path = ServletActionContext.getServletContext()
						.getRealPath(realFilePath);
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();// 如果不存在文件夹就创建
				}
				processTemplateFile.setFileName(realFileName);
				processTemplateFile.setFileName2(realFileName2);
				processTemplateFile.setMonth(Util.getDateTime("yyyy-MM"));
				fileName = fileName.replaceAll("-ZKT", "-展开图");
				fileName = fileName.replaceAll("-YT", "-原图");
				fileName = fileName.replaceAll("-GYK", "-工艺卡");
				processTemplateFile.setOldfileName(fileName);
				processTemplateFile.setAddTime(Util.getDateTime());
				processTemplateFile.setUserCode(user.getCode());
				processTemplateFile.setUserName(user.getName());
				
				Upload upload = new Upload();
				upload.UploadFile(attachment[i], fileName, realFileName,
						realFilePath, null);
				 //将图纸加盖印章
				 String icon_fileRealPath = ServletActionContext
				 .getServletContext().getRealPath(
				 "/upload/file/yz/icon_ytwrq.png");
				//生成加章文件
				Util.markImageByIcon(icon_fileRealPath, path+"/"+realFileName, path+"/"+realFileName2);
				
				String msg = this.procardTemplateSbServer
						.saveProcessTemplateFile(this.processTemplateFile, id);
				if (!msg.equals("true")) {
					errorMessage = msg;
					return "error";
				}
			}
		}
		errorMessage = "添加成功!";
		url = "procardTemplateSbAction_showProcessTz.action?id=" + id + "&tag="
				+ tag;
		return "error";
	}
	
	
	/***
	 * 添加工序信息
	 * 
	 * @return
	 */
	public String addProcessTemplate() {
		// 查询对应流水卡片
		ProcardTemplatesb proTem = procardTemplateSbServer.getProcardsbTemById(id);
		if(processTemplatesb.getProcessNO()==null || "".equals(processTemplatesb.getProcessName())){
			if(id2!=null){
				errorMessage = "添加失败!";
//				url = "procardTemplateGyAction_showBOMasExlAndProcedure.action?id=" + id2;
			}
			return "error";
		}
		if (proTem != null) {
			// processTemplate.setProcardTemplate(proTem);
			if (processTemplatesb.getOpcaozuojiepai() == null) {
				processTemplatesb.setOpcaozuojiepai(3f);
			}
			if (processTemplatesb.getOpshebeijiepai() == null) {
				processTemplatesb.setOpshebeijiepai(1f);
			}
			if (processTemplatesb.getGzzhunbeijiepai() == null) {
				processTemplatesb.setGzzhunbeijiepai(1f);
			}
			if (processTemplatesb.getGzzhunbeicishu() == null) {
				processTemplatesb.setGzzhunbeicishu(1f);
			}
			processTemplatesb.setAllJiepai(processTemplatesb.getOpcaozuojiepai()
					+ processTemplatesb.getOpshebeijiepai()
					+ processTemplatesb.getGzzhunbeijiepai()
					* processTemplatesb.getGzzhunbeicishu());
			String msg = procardTemplateSbServer.addProcessTemplate(
					processTemplatesb, id);
			if (msg.equals("true")) {
				if(id2!=null){
					errorMessage = "添加成功!";
//					url = "procardTemplateGyAction_showBOMasExlAndProcedure.action?id=" + id2;
				}else {
					errorMessage = "添加成功!";
					url = "procardTemplateSbAction_toAddProcess.action?id=" + id;
				}
				
			} else {
				errorMessage = msg;
			}
		}
		return "error";
	}
	
	/***
	 * 更新工序模板信息
	 * 
	 * @param processT
	 * @return
	 */
	public String updateProcessT() {
		errorMessage = procardTemplateSbServer.updateProcessT(processTemplatesb);
		if ("true".equals(errorMessage)) {
			return "updateProcessTsb";
		}
		return "error";
	}
	
	/***
	 * 删除工序信息
	 */
	public void deleteProcess() {
		try {
			ProcessTemplatesb process = procardTemplateSbServer.findProcessT(id);
			if (process != null) {
				String msg = procardTemplateSbServer.delProcessT(process);
				if(msg.equals("true")){
					MKUtil.writeJSON(true, "删除成功!", null);
				}else{
					MKUtil.writeJSON(false, msg, null);
				}
			} else
				MKUtil.writeJSON(false, "不存在您要删除的工序!", null);
		} catch (Exception e) {
			MKUtil.writeJSON(false, "删除失败!", null);
		}
	}
	
	/***
	 * 显示设变零件详情
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findCardTemForShow() {
		Object[] obj = procardTemplateSbServer.findCardTemForShow(id);
		if (obj != null) {
			procardTemplatesb = (ProcardTemplatesb) obj[0];
			procardTemplatesbList = (List<ProcardTemplatesb>) obj[1];
			list = (List) obj[2];
			if(obj[3]!=null){
				jobNum = obj[3].toString();
			}else{
				jobNum = "4";
			}
			if(obj[4]!=null){
				messagePower = obj[4].toString();
			}
			
//			// 精益控制
//			if (pageStatus != null && "jy".equals(pageStatus)) {
//				return "jy_ProcardTemDetails";
//			}
			return "ProcardTemsbDetails";
		}
		return "error";
	}
	
	public String updateProcardTem(){
		String bool =null;
		try {
			if(procardTemplatesb!=null){
				procardTemplatesb.setId(id);
			}
			 bool = procardTemplateSbServer.updateProcardTemplate2(procardTemplatesb);// 保存修改的页面直接获得的数据
			 if (bool.equals("true")) {
					errorMessage = "修改成功!";
					MKUtil.writeJSON(errorMessage);
					return null;
			}else{
				errorMessage = bool;
				MKUtil.writeJSON(errorMessage);
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			errorMessage = e.getMessage();
			MKUtil.writeJSON(errorMessage);
			return null;
		}
	}
	/**
	 * 下载图纸
	 * @return
	 */
	public String DownloadProcessTz() {
		ProcessTemplateFilesb processFile = procardTemplateSbServer
				.findGyTzById(id);
		if (processFile != null) {
			String fileRealPath = ServletActionContext.getServletContext()
					.getRealPath("/upload/file/processTz")
					+ "/"
					+ processFile.getMonth()
					+ "/"
					+ processFile.getFileName();
			File file = new File(fileRealPath);
			realPath = "/upload/file/processTz" + "/" + processFile.getMonth()
					+ "/" + processFile.getFileName();
			if (file.exists()) {
				// 返回时的名字
				try {
					gygfFileName = java.net.URLEncoder.encode(processFile
							.getOldfileName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				AttendanceTowServerImpl.updateJilu(8, processFile, processFile
						.getId(), "件号:" + processFile.getMarkId() + "第"
						+ processFile.getProcessNO() + "工序"
						+ processFile.getProcessName() + " "
						+ processFile.getType() + " 图纸名:"
						+ processFile.getOldfileName() + "(下载)");
				return "download";
			} else {
				String fileRealPath2 = ServletActionContext.getServletContext()
						.getRealPath("/upload/file/processTz")
						+ "/"
						+ processFile.getMonth()
						+ "/"
						+ processFile.getFileName2();
				file = new File(fileRealPath2);
				realPath = "/upload/file/processTz" + "/"
						+ processFile.getMonth() + "/"
						+ processFile.getFileName2();
				if (file.exists()) {
					// 返回时的名字
					try {
						gygfFileName = java.net.URLEncoder.encode(processFile
								.getOldfileName(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					AttendanceTowServerImpl.updateJilu(8, processFile,
							processFile.getId(), "件号:"
									+ processFile.getMarkId() + "第"
									+ processFile.getProcessNO() + "工序"
									+ processFile.getProcessName() + " "
									+ processFile.getType() + " 图纸名:"
									+ processFile.getOldfileName() + "(下载)");
					return "download";
				} else {
					errorMessage = "您要下载的文件已经被删除,或者不存在此文件!请重试";
					return "error";
				}
			}
		}
		errorMessage = "您要下载的文件已经被删除,或者不存在此文件!请重试";
		return "error";
	}
	/**
	 * 删除设变BOM图纸
	 * @return
	 */
	public String deletesbBomTz() {
		processTemplateFile = procardTemplateSbServer
				.findGyTzById(processTemplateFile.getId());
		String month = null;
		String fileName = null;
		String fileName2 = null;
		if (processTemplateFile != null) {
			fileName = processTemplateFile.getFileName();
			fileName2 = processTemplateFile.getFileName2();
			month = processTemplateFile.getMonth();
		}
		boolean b = procardTemplateSbServer.deletesbBomTz(processTemplateFile
				.getId(), type);
		if (b) {
			errorMessage = "删除成功!";
			if (!"sop".equals(type)) {
				String path = ServletActionContext.getServletContext()
						.getRealPath("/upload/file/processTz");
				if (month != null && month.length() > 0) {
					path += "/" + month;
				}
				// File file = new File(path + "/" + fileName);
				// File file2 = new File(path + "/" + fileName2);
				// if (file.exists()) {
				// file.delete();
				// }
				// if (file2.exists()) {
				// file2.delete();
				// }
			}
		} else {
			errorMessage = "删除失败!";
		}
		if ("quexian".equals(tag)) {
			url = "ProcardTemplateAction!showProcessTz.action?id=" + id
					+ "&tag=" + tag;
		} else {
			if (pageStatus != null && pageStatus.equals("card")) {
				url = "procardTemplateSbAction_showCardTz.action?id=" + id
						+ "&tag=" + type;
			} else {
				url = "procardTemplateSbAction_showProcessTz.action?id=" + id;
			}
		}
		return "error";
	}
	
	public InputStream getInputStream() throws Exception {
		// return new FileInputStream(dir);
		// 如果dir是绝对路径
		// return
		return ServletActionContext.getServletContext().getResourceAsStream(
				realPath);
		// 如果dir是Resource下的相对路径
	}
	/**
	 * 通过设变零件id来获取对应的工序
	 * @return
	 */
	public String findProcessByFkId() {
		List<ProcessTemplatesb> processList = procardTemplateSbServer
				.findProcessByFkId(id);
		try {
			MKUtil.writeJSON(processList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 显示下阶层
	 * 
	 * @return
	 */
	public String moveStatus() {
		procardTemplatesb = procardTemplateSbServer.getProcardsbTemById(id);
		procardTemplatesbList = procardTemplateSbServer.findPtsbListByFatherId(id);
		return "Templatesb_moveStatus";
	}
	/**
	 * 删除下阶层
	 * @return
	 */
	public void deleteSons(){
		try {
			String msg = procardTemplateSbServer.deleteSons(checkboxs, id);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	
	/**
	 * 下阶层排序
	 * 
	 * @return
	 */
	public String sonMoveStatus() {
		try {
			String msg = procardTemplateSbServer.sonMoveStatus(procardTemplatesbList,
					id);
			url = "procardTemplateSbAction_moveStatus.action?id=" + id;
			errorMessage = "设置成功";
			MKUtil.writeJSON(errorMessage);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
		return null;
	}
	/**
	 * 前往添加工序
	 * @return
	 */
	public String toAddProcess(){
		procardTemplatesb = procardTemplateSbServer.getProcardsbTemById(id); 
		nextNo = procardTemplateSbServer.nextNoProcessNo(id);
		return "ProcessTemplatesbadd";
	}
	/**
	 * 展示工序明细，并前往修改
	 * @return
	 */
	public String showProcess(){
		processTemplatesb = procardTemplateSbServer.findProcesssbT(id);
		if (processTemplatesb != null) {
			return "ProcessTemplatesbDetails";
		} else {
			errorMessage = "不存在您要查询的工序信息,请核对!";
		}
		return "error";
	}
	/**
	 * 设计变更完成
	 */
	public void surechange(){
		try {
			String  msg = procardTemplateSbServer.surechange(id);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
		
	}
	/**
	 * 通过设变组获取对应人员列表
	 */
	public void findsbrylistbyzb(){
		List<AssessPersonnel> personList = procardTemplateSbServer.findsbrylistbyzb(id);
		MKUtil.writeJSON(personList);
	}
	/**
	 * 获取设变组
	 */
	public void findsbzbList(){
		List<UsersGroup> ugList = procardTemplateSbServer.findUsersGroupBytag("sb");
		MKUtil.writeJSON(ugList);
	}
	
	/**
	 * 工程师评审
	 */
	public void submitgcsps(){
		try {
			String msg = procardTemplateSbServer.submitgcsps(bbAply,ptasbclTypeList,pbbJudgeList,
					pbbJudgeList2, remark);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	
	public void submitzpgbm(){
		try {
			String msg = procardTemplateSbServer.submitzpgbm(bbAply, ptbbJudgeslist,remark);
			if(msg.equals("true")){
				MKUtil.writeJSON("指派成功");
			}else{
				MKUtil.writeJSON(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	
	
	/**
	 * 成本审核
	 */
	public void submitcbsh(){
		try {
			String msg = procardTemplateSbServer.updatecbsh(ptbbJudges);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	
	/**
	 * 打回
	 */
	public void backsbApply(){
		try {
			String msg = procardTemplateSbServer.backsbApply(id,remark);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	/**
	 * 提交内审
	 */
	public void submitnp(){
		String msg = procardTemplateSbServer.submitnp(bbAply,ptbbJudges,ptasbclTypeList,pbbJudgeList,pbbJudgeList2, remark);
		MKUtil.writeJSON(msg);
	}
	/**
	 * 提交内审
	 */
	public void noprocardns(){
		String msg = procardTemplateSbServer.noprocardns(bbAply,ptbbJudges,ptasbclTypeList, remark);
		MKUtil.writeJSON(msg);
	}
	
	/**
	 * 选择人员
	 * @return
	 */
	public String findAboutDept(){
		userGroupList = procardTemplateSbServer.finduserDeptList("sb");
		return "sbApply_showDept2";
	}
	/**
	 * 通过组别获取人员
	 */
	public void getusersBygroup(){
		List<Users> users = procardTemplateSbServer.getusersBygroup(id,ids);
		MKUtil.writeJSON(users);
	}
	
	/**
	 * 确认设变
	 */
	public void suresb() {
		try {
			 String msg = procardTemplateSbServer.suresb(id, ptbbJudges,
			 ptbbList);
			// 修复有fatherId 没有procardId的数据
			procardTemplateSbServer.mibufatherAndsonrealction();
			 MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON(e.getMessage());
		}
	}
	
	/**
	 * 跳往导入子件页面
	 * @return
	 */
	public String todaoruson() {
		return "procardTemsb_daoruson";
	}
	
	public String daoRusonbom() {
		try {
			errorMessage = (String) ActionContext.getContext().getApplication()
					.get("sbBOMDaoru");
			if (errorMessage == null) {
				Users loginUser = Util.getLoginUser();
				ActionContext.getContext().getApplication().put(
						"sbBOMDaoru",
						loginUser.getDept() + "的" + loginUser.getName()
								+ "正在导入设变子件BOM,请稍等片刻~~或者去他那喝杯茶吧~~");
			} else {
				return "error";
			}
			ProcardTemplatesb fatherPt = procardTemplateSbServer
					.getProcardsbTemById(id);
			String msg = null;
			if(id==null){
				errorMessage = "请选择父零件";
				return "error";
			}
			if (fatherPt.getProductStyle().equals("批产")) {
				msg = procardTemplateSbServer.daoRuHwBom(bomTree,
						bomTreeFileName, id);
			} else {
				msg = procardTemplateSbServer.daoRuHwSZBom(bomTree,
						bomTreeFileName, id);
			}
			procardTemplateSbServer.mibufatherAndsonrealction();
			if (msg == null || msg.length() == 0 || msg.equals("true")) {
				successMessage = "导入成功";
			} else {
				successMessage = msg;
			}
			ActionContext.getContext().getApplication().put("sbBOMDaoru", null);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			successMessage = e.getMessage();
		}
		ActionContext.getContext().getApplication().put("sbBOMDaoru", null);
		return "procardTemsb_daoruson";

	}
	
	/**
	 * 导入工艺图纸内容
	 */
	public void checkAndUpdateTz() {
		try {
			errorMessage = (String) ActionContext.getContext().getApplication()
					.get("sbBOMDaoru");
			if (errorMessage == null) {
				Users loginUser = Util.getLoginUser();
				ActionContext.getContext().getApplication().put(
						"sbBOMDaoru",
						loginUser.getDept() + "的" + loginUser.getName()
								+ "正在导设变BOM图纸,请稍等片刻~~或者去他那喝杯茶~~");
			} else {
				MKUtil.writeJSON(errorMessage);
				return;
			}
			String path = ServletActionContext.getServletContext().getRealPath(
					"/upload/gytz/");
			String path2 = ServletActionContext.getServletContext()
					.getRealPath("");
			String msg = procardTemplateSbServer.checkAndUpdateTz(id, path,
					path2);
			ActionContext.getContext().getApplication().put("sbBOMDaoru", null);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			e.printStackTrace();
			ActionContext.getContext().getApplication().put("sbBOMDaoru", null);
			MKUtil.writeJSON("导入失败!" + e);
		}
	}
	
	/****
	 * 一键导入工序
	 * 
	 * @param rootId
	 * @return
	 */
	public String daoRuProcessTempalte() {
		Users user = Util.getLoginUser();
		if (user == null) {
			MKUtil.writeJSON("请先登录!");
			return null;
		}
//		List<AssessPersonnel> apList = procardTemplateGyServer
//				.getGyPeople("gy","工序导入");
//		boolean b = false;
//		for (AssessPersonnel ap : apList) {
//			if (ap.getUserId().equals(user.getId())) {
//				b = true;
//			}
//		}
//		if (!b) {
//			MKUtil.writeJSON("对不起您没有导入工序权限");
//			return null;
//		}
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get("sbBOMDaoru");
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"sbBOMDaoru",

					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在导设变BOM工序,请稍等片刻~~或者去他那喝杯茶~~");
		} else {
			MKUtil.writeJSON(errorMessage);
			return null;
		}
		List<ProcardTemplatesb> ptList = procardTemplateSbServer.getGongxuPt(id);
		if (ptList != null && ptList.size() > 0) {
			int cgcount = 0;
			int cwcount = 0;
			StringBuffer sb = new StringBuffer();
			List<String> passmarkIdLIst = new ArrayList<String>();
			Statement sql = null;
			ResultSet rs = null;
			String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
			String dbURL = "jdbc:sqlserver://192.168.2.37:1433;databaseName=yyf_ytDB"; // 连接服务器和数据库sample
			String userName = "tiaomao"; // 默认用户名
			String userPwd = "a123456"; // 密码
			Connection dbConn = null;
			try {
				Class.forName(driverName);
				dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
				sql = dbConn.createStatement();
			} catch (Exception e1) {
				e1.printStackTrace();
				ActionContext.getContext().getApplication().put("sbBOMDaoru",
						null);
				MKUtil
						.writeJSON("导入失败，无法连接数据源(192.168.2.37:1433;databaseName=yyf_ytDB)!");
				return null;
			}
			try {
				// Object[] obj2 = procardTemplateGyServer
				// .updatedaoRuProcessTemplate(null, sql, rs);
				for (ProcardTemplatesb pt : ptList) {
					if (passmarkIdLIst.contains(pt.getMarkId())) {
						continue;
					} else {
						passmarkIdLIst.add(pt.getMarkId());
					}
					Object[] obj = procardTemplateSbServer
							.updatedaoRuProcessTemplate(pt, sql, rs);
					String m1 = obj[0].toString();
					String m2 = obj[1].toString();
					if (m1.equals("false")) {
						errorMessage = m2;
						break;
					} else {
						if (m1.equals("1")) {
							cgcount++;
						} else {
							cwcount++;
							sb.append(cwcount + "、" + m2);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = "导入成功" + cgcount + "个</br>导入失败" + cwcount
						+ "个</br>";
				if (cwcount > 0) {
					errorMessage += sb.toString();
				}
				ActionContext.getContext().getApplication().put("BOMDaoru",
						null);
				MKUtil.writeJSON(errorMessage + ",出现异常导入中断!");
				return null;
			}
			try {
				dbConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				ActionContext.getContext().getApplication().put("BOMDaoru",
						null);
				MKUtil.writeJSON(errorMessage + ",出现异常导入中断!");
				return null;
			}

			errorMessage = "导入成功" + cgcount + "个</br>导入失败" + cwcount + "个</br>";
			if (cwcount > 0) {
				errorMessage += sb.toString();
			}

		} else {
			errorMessage = "没有可以导入工序的零件!";
		}

		ActionContext.getContext().getApplication().put("sbBOMDaoru", null);
		MKUtil.writeJSON(errorMessage);
		return null;
	}
	
	
	/**
	 * 修改图纸名称
	 */
	public void updateUnuploadTzname() {
		try {
			String path = ServletActionContext.getServletContext().getRealPath(
					"/upload/gytz/");
			String path2 = ServletActionContext.getServletContext()
					.getRealPath("");
			String msg = procardTemplateSbServer.updateUnuploadTzname(id, path);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON("修改失败!" + e);
		}
	}
	
	public String totransferWork(){
		userGroupList = procardTemplateSbServer.finduserDeptList("sb");
		return "sbApply_transferWork";
	}
	
	public void transferWork(){
		String msg = procardTemplateSbServer.transferWork(id,id2);
		MKUtil.writeJSON(msg);
	}
	
	
	public void daoChuBom() {
		procardTemplateSbServer.findDaoChuBom(id);
	}
	
	public ProcardTemplateGyServer getProcardTemplateGyServer() {
		return procardTemplateGyServer;
	}
	public void setProcardTemplateGyServer(
			ProcardTemplateGyServer procardTemplateGyServer) {
		this.procardTemplateGyServer = procardTemplateGyServer;
	}
	public List<ProcessTemplate> getProcessList() {
		return processList;
	}
	public void setProcessList(List<ProcessTemplate> processList) {
		this.processList = processList;
	}
	public String getProcessListString() {
		return processListString;
	}
	public void setProcessListString(String processListString) {
		this.processListString = processListString;
	}
	public List<String> getProcessStringList() {
		return processStringList;
	}
	public void setProcessStringList(List<String> processStringList) {
		this.processStringList = processStringList;
	}
	public ProcardTemplateServer getProcardTemplateServer() {
		return procardTemplateServer;
	}
	public void setProcardTemplateServer(ProcardTemplateServer procardTemplateServer) {
		this.procardTemplateServer = procardTemplateServer;
	}
	public ProcessTemplate getProcessTemplate() {
		return processTemplate;
	}
	public void setProcessTemplate(ProcessTemplate processTemplate) {
		this.processTemplate = processTemplate;
	}
	public ProcardTemplateBanBenApply getBbAply() {
		return bbAply;
	}
	public void setBbAply(ProcardTemplateBanBenApply bbAply) {
		this.bbAply = bbAply;
	}
	public ProcardTemplateBanBen getPtbb() {
		return ptbb;
	}
	public void setPtbb(ProcardTemplateBanBen ptbb) {
		this.ptbb = ptbb;
	}
	public ProcardTBanbenRelation getPtbbRelation() {
		return ptbbRelation;
	}
	public void setPtbbRelation(ProcardTBanbenRelation ptbbRelation) {
		this.ptbbRelation = ptbbRelation;
	}
	public List<ProcessTemplate> getProcessTemplateList() {
		return processTemplateList;
	}
	public void setProcessTemplateList(List<ProcessTemplate> processTemplateList) {
		this.processTemplateList = processTemplateList;
	}
	public List<ProcardTemplateBanBenApply> getBbAplyList() {
		return bbAplyList;
	}
	public void setBbAplyList(List<ProcardTemplateBanBenApply> bbAplyList) {
		this.bbAplyList = bbAplyList;
	}
	public List<ProcardTemplateBanBen> getPtbbList() {
		return ptbbList;
	}
	public void setPtbbList(List<ProcardTemplateBanBen> ptbbList) {
		this.ptbbList = ptbbList;
	}
	public List<ProcardTBanbenRelation> getPtbbRelationList() {
		return ptbbRelationList;
	}
	public void setPtbbRelationList(List<ProcardTBanbenRelation> ptbbRelationList) {
		this.ptbbRelationList = ptbbRelationList;
	}
	public List<ProcardAboutBanBenApply> getPabbList() {
		return pabbList;
	}
	public void setPabbList(List<ProcardAboutBanBenApply> pabbList) {
		this.pabbList = pabbList;
	}
	public List<ProcardAboutBanBenApply> getPabbList2() {
		return pabbList2;
	}
	public void setPabbList2(List<ProcardAboutBanBenApply> pabbList2) {
		this.pabbList2 = pabbList2;
	}
	public List<UserDept> getUserDeptList() {
		return userDeptList;
	}
	public void setUserDeptList(List<UserDept> userDeptList) {
		this.userDeptList = userDeptList;
	}
	public ProcardAboutBanBenApply getPabb() {
		return pabb;
	}
	public void setPabb(ProcardAboutBanBenApply pabb) {
		this.pabb = pabb;
	}
	public ProcardTemplateBanBenJudges getPtbbJudges() {
		return ptbbJudges;
	}
	public void setPtbbJudges(ProcardTemplateBanBenJudges ptbbJudges) {
		this.ptbbJudges = ptbbJudges;
	}
	public List<ProcardBanBenJudge> getPbbJudgeList() {
		return pbbJudgeList;
	}
	public void setPbbJudgeList(List<ProcardBanBenJudge> pbbJudgeList) {
		this.pbbJudgeList = pbbJudgeList;
	}
	public List<ProcardBanBenJudge> getPbbJudgeList2() {
		return pbbJudgeList2;
	}
	public void setPbbJudgeList2(List<ProcardBanBenJudge> pbbJudgeList2) {
		this.pbbJudgeList2 = pbbJudgeList2;
	}
	public ProcardBanBenJudge getPbbJudge() {
		return pbbJudge;
	}
	public void setPbbJudge(ProcardBanBenJudge pbbJudge) {
		this.pbbJudge = pbbJudge;
	}
	public List<ProcardTemplateChangeLog> getPtchangeLogList() {
		return ptchangeLogList;
	}
	public void setPtchangeLogList(List<ProcardTemplateChangeLog> ptchangeLogList) {
		this.ptchangeLogList = ptchangeLogList;
	}
	
	public ProcessTemplateFilesb getProcessTemplateFile() {
		return processTemplateFile;
	}
	public void setProcessTemplateFile(ProcessTemplateFilesb processTemplateFile) {
		this.processTemplateFile = processTemplateFile;
	}
	public ProcardSbWg getProcardsbwg() {
		return procardsbwg;
	}
	public void setProcardsbwg(ProcardSbWg procardsbwg) {
		this.procardsbwg = procardsbwg;
	}
	public ProcardSbWw getProcardSbWw() {
		return procardSbWw;
	}
	public void setProcardSbWw(ProcardSbWw procardSbWw) {
		this.procardSbWw = procardSbWw;
	}
	public List<ProcardSbWg> getProcardsbwgList() {
		return procardsbwgList;
	}
	public void setProcardsbwgList(List<ProcardSbWg> procardsbwgList) {
		this.procardsbwgList = procardsbwgList;
	}
	public List<ProcardSbWw> getProcardSbWwList() {
		return procardSbWwList;
	}
	public void setProcardSbWwList(List<ProcardSbWw> procardSbWwList) {
		this.procardSbWwList = procardSbWwList;
	}
	public ProcessInforWWApplyDetail getWwApplyDetail() {
		return wwApplyDetail;
	}
	public void setWwApplyDetail(ProcessInforWWApplyDetail wwApplyDetail) {
		this.wwApplyDetail = wwApplyDetail;
	}
	public WaigouWaiweiPlan getWaigouWaiweiPlan() {
		return waigouWaiweiPlan;
	}
	public void setWaigouWaiweiPlan(WaigouWaiweiPlan waigouWaiweiPlan) {
		this.waigouWaiweiPlan = waigouWaiweiPlan;
	}
	public List<WaigouWaiweiPlan> getWaigouWaiweiPlanList() {
		return waigouWaiweiPlanList;
	}
	public void setWaigouWaiweiPlanList(List<WaigouWaiweiPlan> waigouWaiweiPlanList) {
		this.waigouWaiweiPlanList = waigouWaiweiPlanList;
	}
	public List<WaigouPlan> getWaigouPlanList() {
		return waigouPlanList;
	}
	public void setWaigouPlanList(List<WaigouPlan> waigouPlanList) {
		this.waigouPlanList = waigouPlanList;
	}
	public List<ManualOrderPlan> getManyalOrderPlanList() {
		return manyalOrderPlanList;
	}
	public void setManyalOrderPlanList(List<ManualOrderPlan> manyalOrderPlanList) {
		this.manyalOrderPlanList = manyalOrderPlanList;
	}
	public WaigouPlan getWaigouplan() {
		return waigouplan;
	}
	public void setWaigouplan(WaigouPlan waigouplan) {
		this.waigouplan = waigouplan;
	}
	public ManualOrderPlan getMop() {
		return mop;
	}
	public void setMop(ManualOrderPlan mop) {
		this.mop = mop;
	}
	public ProcessInfor getProcessInfor() {
		return processInfor;
	}
	public void setProcessInfor(ProcessInfor processInfor) {
		this.processInfor = processInfor;
	}
	public List<ProcessInfor> getProcessInforList() {
		return processInforList;
	}
	public void setProcessInforList(List<ProcessInfor> processInforList) {
		this.processInforList = processInforList;
	}
	public List<ProcardReProductFile> getPrpFileList() {
		return prpFileList;
	}
	public void setPrpFileList(List<ProcardReProductFile> prpFileList) {
		this.prpFileList = prpFileList;
	}
	public ProcardReProductFile getPrpFile() {
		return prpFile;
	}
	public void setPrpFile(ProcardReProductFile prpFile) {
		this.prpFile = prpFile;
	}
	public Procard getProcard() {
		return procard;
	}
	public void setProcard(Procard procard) {
		this.procard = procard;
	}
	public ProcardSbWaster getProcardSbWaster() {
		return procardSbWaster;
	}
	public void setProcardSbWaster(ProcardSbWaster procardSbWaster) {
		this.procardSbWaster = procardSbWaster;
	}
	public List<ProcardSbWaster> getProcardSbWasterList() {
		return procardSbWasterList;
	}
	public void setProcardSbWasterList(List<ProcardSbWaster> procardSbWasterList) {
		this.procardSbWasterList = procardSbWasterList;
	}
	public ProcardSbWasterxc getProcardSbWasterxc() {
		return procardSbWasterxc;
	}
	public void setProcardSbWasterxc(ProcardSbWasterxc procardSbWasterxc) {
		this.procardSbWasterxc = procardSbWasterxc;
	}
	public List<ProcardSbWasterxc> getProcardSbWasterxcList() {
		return procardSbWasterxcList;
	}
	public void setProcardSbWasterxcList(
			List<ProcardSbWasterxc> procardSbWasterxcList) {
		this.procardSbWasterxcList = procardSbWasterxcList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId2() {
		return id2;
	}
	public void setId2(Integer id2) {
		this.id2 = id2;
	}
	public Integer getRootId() {
		return rootId;
	}
	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public Integer getMaxBelongLayer() {
		return maxBelongLayer;
	}
	public void setMaxBelongLayer(Integer maxBelongLayer) {
		this.maxBelongLayer = maxBelongLayer;
	}
	public YuanclAndWaigj getYclAndWgj() {
		return yclAndWgj;
	}
	public void setYclAndWgj(YuanclAndWaigj yclAndWgj) {
		this.yclAndWgj = yclAndWgj;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getYtRadio() {
		return ytRadio;
	}
	public void setYtRadio(String ytRadio) {
		this.ytRadio = ytRadio;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getBzjdCount() {
		return bzjdCount;
	}
	public void setBzjdCount(String bzjdCount) {
		this.bzjdCount = bzjdCount;
	}
	public String getIdname1() {
		return idname1;
	}
	public void setIdname1(String idname1) {
		this.idname1 = idname1;
	}
	public String getIdname2() {
		return idname2;
	}
	public void setIdname2(String idname2) {
		this.idname2 = idname2;
	}
	public String getIdname3() {
		return idname3;
	}
	public void setIdname3(String idname3) {
		this.idname3 = idname3;
	}
	public String getPageStatus() {
		return pageStatus;
	}
	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
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
	public File getGygf() {
		return gygf;
	}
	public void setGygf(File gygf) {
		this.gygf = gygf;
	}
	public String getGygfFileName() {
		return gygfFileName;
	}
	public void setGygfFileName(String gygfFileName) {
		this.gygfFileName = gygfFileName;
	}
	public String getGygfFileContentType() {
		return gygfFileContentType;
	}
	public void setGygfFileContentType(String gygfFileContentType) {
		this.gygfFileContentType = gygfFileContentType;
	}
	public File getBomTree() {
		return bomTree;
	}
	public void setBomTree(File bomTree) {
		this.bomTree = bomTree;
	}
	public String getBomTreeFileName() {
		return bomTreeFileName;
	}
	public void setBomTreeFileName(String bomTreeFileName) {
		this.bomTreeFileName = bomTreeFileName;
	}
	public String getBomTreeFileContentType() {
		return bomTreeFileContentType;
	}
	public void setBomTreeFileContentType(String bomTreeFileContentType) {
		this.bomTreeFileContentType = bomTreeFileContentType;
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int[] getCheckboxs() {
		return checkboxs;
	}
	public void setCheckboxs(int[] checkboxs) {
		this.checkboxs = checkboxs;
	}
	public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	public List<ProcardReProduct> getPreProductList() {
		return preProductList;
	}
	public void setPreProductList(List<ProcardReProduct> preProductList) {
		this.preProductList = preProductList;
	}
	public ProcardReProduct getPreProduct() {
		return preProduct;
	}
	public void setPreProduct(ProcardReProduct preProduct) {
		this.preProduct = preProduct;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getFile_uploadFileName() {
		return file_uploadFileName;
	}
	public void setFile_uploadFileName(List<String> fileUploadFileName) {
		file_uploadFileName = fileUploadFileName;
	}
	public List<String> getFile_uploadContentType() {
		return file_uploadContentType;
	}
	public void setFile_uploadContentType(List<String> fileUploadContentType) {
		file_uploadContentType = fileUploadContentType;
	}
	public List<File> getFile_upload() {
		return file_upload;
	}
	public void setFile_upload(List<File> fileUpload) {
		file_upload = fileUpload;
	}
	public ProcardTemplateSbServer getProcardTemplateSbServer() {
		return procardTemplateSbServer;
	}
	public void setProcardTemplateSbServer(
			ProcardTemplateSbServer procardTemplateSbServer) {
		this.procardTemplateSbServer = procardTemplateSbServer;
	}
	public ProcardTemplatesb getProcardTemplatesb() {
		return procardTemplatesb;
	}
	public void setProcardTemplatesb(ProcardTemplatesb procardTemplatesb) {
		this.procardTemplatesb = procardTemplatesb;
	}
	public List<ProcardTemplatesb> getPageprocardTemplatesbList() {
		return pageprocardTemplatesbList;
	}
	public void setPageprocardTemplatesbList(
			List<ProcardTemplatesb> pageprocardTemplatesbList) {
		this.pageprocardTemplatesbList = pageprocardTemplatesbList;
	}
	public ProcessTemplatesb getProcessTemplatesb() {
		return processTemplatesb;
	}
	public void setProcessTemplatesb(ProcessTemplatesb processTemplatesb) {
		this.processTemplatesb = processTemplatesb;
	}
	public List<ProcardTemplatesb> getProcardTemplatesbList() {
		return procardTemplatesbList;
	}
	public void setProcardTemplatesbList(
			List<ProcardTemplatesb> procardTemplatesbList) {
		this.procardTemplatesbList = procardTemplatesbList;
	}
	public String getJobNum() {
		return jobNum;
	}
	public void setJobNum(String jobNum) {
		this.jobNum = jobNum;
	}
	public String getMessagePower() {
		return messagePower;
	}
	public void setMessagePower(String messagePower) {
		this.messagePower = messagePower;
	}
	public Integer getNextNo() {
		return nextNo;
	}
	public void setNextNo(Integer nextNo) {
		this.nextNo = nextNo;
	}
	public List<ProcardTemplateBanBenJudges> getPtbbJudgeslist() {
		return ptbbJudgeslist;
	}
	public void setPtbbJudgeslist(List<ProcardTemplateBanBenJudges> ptbbJudgeslist) {
		this.ptbbJudgeslist = ptbbJudgeslist;
	}
	public List<ProcardTemplateAboutsbcltype> getPtasbclTypeList() {
		return ptasbclTypeList;
	}
	public void setPtasbclTypeList(
			List<ProcardTemplateAboutsbcltype> ptasbclTypeList) {
		this.ptasbclTypeList = ptasbclTypeList;
	}
	public List<UsersGroup> getUserGroupList() {
		return userGroupList;
	}
	public void setUserGroupList(List<UsersGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}
	
	
	
	
	

}
