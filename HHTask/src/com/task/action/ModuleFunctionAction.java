package com.task.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.jna.StringArray;
import com.sun.mail.imap.protocol.Item;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.IntArrayData;
import com.task.Dao.TotalDao;
import com.task.Server.ModuleFunctionServer;
import com.task.Server.ProjectRecordServer;
import com.task.Server.UserServer;
import com.task.Server.sys.UserDeptServer;
import com.task.entity.ModuleFunction;
import com.task.entity.ProjectLogin;
import com.task.entity.ProjectRecord;
import com.task.entity.Users;
import com.task.entity.system.CompanyInfo;
import com.task.entity.system.UserDept;
import com.task.util.MKUtil;
import com.task.util.SessionListener;
import com.task.util.StrutsTreeNode;
import com.task.util.Util;

/**
 * 模块功能Action层
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings( { "serial", "unchecked" })
public class ModuleFunctionAction extends ActionSupport {

	private ModuleFunctionServer moduleFunctionServer;// Server层
	private ProjectRecordServer projectRecordServer;// Server层
	private UserServer userServer;// 用户类
	private UserDeptServer userDeptServer;// 用户部门权限服务
	private ModuleFunction moduleFunction;// 模块功能对象
	private List<ModuleFunction> moduleFunctionList;// 模块功能集合
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private int id;// id
	private StrutsTreeNode root;// tree
	private String pageStatus;// 状态
	private List<Users> userList;// 未绑定用户
	private List<Users> bangUserList;// 已绑定用户
	private List<UserDept> userDeptList;// 用户管理部门
	private Integer[] usersId;
	private Users user;
	private List<ModuleFunction> allModuleList;
	private String mfNames;// 模块功能名称
	private ProjectRecord projectRecord;// 登录网站信息
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;
	private File qxImage;
	private String qxImageContentType;
	private String qxImageFileName;
	private File xkImage;
	private String xkImageContentType;
	private String xkImageFileName;
	private File smallImage;
	private String smallImageContentType;
	private String smallImageFileName;
	private File mrImage;
	private String mrImageContentType;
	private String mrImageFileName;
	private File bsImage;
	private String bsImageContentType;
	private String bsImageFileName;
	private List sessionsList;
	private CompanyInfo companyInfo;
	private Integer[] detailSelect;// 主键
	private Integer[] detailSelect1;// 页面修改
	private String deptIds;// 选中的部门id
	private String fatherMfname;// 父模块名称

	private Integer se_id;// 序列标识
	private Integer Copyuserid;// 拷贝用户功能
	private Integer Pasteuserid;// 拷贝用户功能
	private List<ModuleFunction> listSubModules; // 子模块
	private String a;

	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;

	// 分页
	private String cpage2 = "1";
	private String total2;
	private String url2;
	private int pageSize2 = 15;

	private int count = 0;// 已绑定用户数量

	// 查询所有模块功能
	public String findAllMf() {
		Users user = (Users) ActionContext.getContext().getSession().get(
				"adminusers");
		List<ModuleFunction> mfList = new ArrayList<ModuleFunction>();
		if (user == null) {
			mfList = moduleFunctionServer.findAllMF();
		} else {
			mfList = moduleFunctionServer.findAllMfByUser(user);
		}
		try {
			MKUtil.writeJSON(mfList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 根据编号查询所有子目录
	 */
	public String findMfById1() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			// if (moduleFunction.getFunctionLink() != null &&
			// moduleFunction.getFunctionLink().length() > 0) {
			// errorMessage = "最后一层无需移动!谢谢!";
			// } else {
			moduleFunctionList = moduleFunctionServer.findMfById1(id);
			return "findMfById1";
			// }
		} else {
			errorMessage = "不存在您要查询的功能!";
		}
		return ERROR;
	}

	/** 查询所有模块功能 输出Json */
	public String findAllMfForJson() {
		List<ModuleFunction> mfList = moduleFunctionServer.findUserMF();
		try {
			MKUtil.writeJSON(mfList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 查询所有模块功能 输出Json */
	public void findUserMFByRootId() {
		List<ModuleFunction> mfList = moduleFunctionServer
				.findUserMFByRootId(id);
		try {
			MKUtil.writeJSON(mfList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 1.通过Id查询模块功能 2、查询已绑定用户 3、查询所有未绑定用户

	public String findMfById() {
		
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			// 未绑定人员处理
			Object[] object = userServer.findAllUser("moduleFunction", id,
					Integer.parseInt(cpage), pageSize);// 条件查询所有用户
			if (object != null && object.length > 0) {
				userList = (List<Users>) object[0];
				if (userList != null && userList.size() > 0) {
					int sum = (Integer) object[1];
					int pageCount = (sum + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("ModuleFunctionAction!findMfById.action?id="
							+ id);
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}
			} else {
				errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
			}

			// 已绑定人员处理
			// bangUserList = userServer.findAllBangUserByPage("moduleFunction",
			// id);
			// if (bangUserList != null && bangUserList.size() > 0) {
			// count = bangUserList.size();
			// }
			Object[] object2 = userServer.findAllBangUserByPage(
					"moduleFunction", id, Integer.parseInt(cpage2), pageSize2);
			if (object2 != null && object2.length > 0) {
				bangUserList = (List<Users>) object2[0];
				if (bangUserList != null && bangUserList.size() > 0) {
					int sum2 = (Integer) object2[1];
					int pageCount2 = (sum2 + pageSize2 - 1) / pageSize2;
					this.setTotal2(pageCount2 + "");
					this.setUrl2("ModuleFunctionAction!findMfById.action?id="
							+ id);
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}
			} else {
				errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
			}
			return "findMfByIdSuccess";
		} else {
			errorMessage = "不存在该功能!";
		}
		return ERROR;
	}

	// 用户条件查询

	public String findUsersByCondition() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			if (user != null) {
				ActionContext.getContext().getSession().put("adminUser", user);
			} else {
				user = (Users) ActionContext.getContext().getSession().get(
						"adminUser");
			}
			Object[] object = userServer.findUsersByCondition(user, Integer
					.parseInt(cpage), pageSize, "houtai", id);// 条件查询所有用户
			if (object != null && object.length > 0) {
				userList = (List<Users>) object[0];
				if (userList != null && userList.size() > 0) {
					int sum = (Integer) object[1];
					int pageCount = (sum + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this
							.setUrl("ModuleFunctionAction!findUsersByCondition.action?id="
									+ id);
					errorMessage = null;
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}
				// 已绑定人员处理
				// bangUserList = userServer.findAllBangUser("moduleFunction",
				// id);

				//

				Object[] object2 = userServer.findHadBingdingUsersByCondition(
						null, Integer.parseInt(cpage), pageSize, "houtai", id);
				if (object2 != null && object2.length > 0) {
					bangUserList = (List<Users>) object2[0];
					if (bangUserList != null && userList.size() > 0) {
						int sum = (Integer) object2[1];
						int pageCount = (sum + pageSize - 1) / pageSize;
						this.setTotal2(pageCount + "");
						this
								.setUrl("ModuleFunctionAction!findMfById.action?id="
										+ id);
					} else {
						errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
					}
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}

				// if (bangUserList != null && bangUserList.size() > 0) {
				// count = bangUserList.size();// 绑定人员数量
				// // 去除已绑定人员
				// for (int i = 0; i < userList.size(); i++) {
				// Users listUser = userList.get(i);
				// for (int j = 0; j < bangUserList.size(); j++) {
				// Users binUser = bangUserList.get(j);
				// if (listUser.getId().equals(binUser.getId())) {
				// userList.remove(listUser);
				// i--;
				// }
				// }
				// }
				// }

			} else {
				errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
			}
			return "findMfByIdSuccess";

		} else {
			errorMessage = "该功能不存在，请重试!";
			return ERROR;
		}
	}

	// 取消绑定用户条件查询 查找已绑定的

	public String findhadBindingUsersByCondition() {
		pageStatus = "cancel";
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			if (user != null) {
				ActionContext.getContext().getSession().put("adminUser", user);
			} else {
				user = (Users) ActionContext.getContext().getSession().get(
						"adminUser");
			}
			Object[] object = userServer.findHadBingdingUsersByCondition(user,
					Integer.parseInt(cpage2), pageSize2, "houtai", id);// 条件查询所有用户
			if (object != null && object.length > 0) {
				bangUserList = (List<Users>) object[0];

				if (bangUserList != null && bangUserList.size() > 0) {
					int sum2 = (Integer) object[1];
					int pageCount2 = (sum2 + pageSize2 - 1) / pageSize2;
					this.setTotal2(pageCount2 + "");
					this
							.setUrl("ModuleFunctionAction!findUsersByCondition.action?id="
									+ id);
					errorMessage = null;
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}

				// 查询未绑定的
				Object[] object2 = userServer.findAllUser("moduleFunction", id,
						Integer.parseInt(cpage), pageSize);// 条件查询所有用户
				if (object2 != null && object2.length > 0) {
					userList = (List<Users>) object2[0];
					if (userList != null && userList.size() > 0) {
						int sum = (Integer) object2[1];
						int pageCount = (sum + pageSize - 1) / pageSize;
						this.setTotal(pageCount + "");
						this
								.setUrl("ModuleFunctionAction!findhadBindingUsersByCondition.action?id="
										+ id);
					} else {
						errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
					}
				} else {
					errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
				}
			} else {
				errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
			}
			return "findMfByIdSuccess";

		} else {
			errorMessage = "该功能不存在，请重试!";
			return ERROR;
		}
	}

	// 绑定用户
	public String binDingUsers() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			boolean bool = moduleFunctionServer.binDingUsers(moduleFunction,
					usersId);
			if (bool) {
				successMessage = "绑定用户成功!";
				ActionContext.getContext().getSession().put("successMessage",
						successMessage);
				return "updateMfSuccess";
			}
			errorMessage = "绑定用户失败";
		} else {
			errorMessage = "不存在该功能!请检查后重试!";
		}
		return ERROR;
	}

	/*
	 * @author fy 　　* @date 2018/8/21 15:20 　　* @Description: 绑定用户, 根据ID新增模块绑定人员
	 * 　　* @param [] 　　* @return java.lang.String 　　* @throws 　　
	 */
	public String AddbinDingUsers() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			boolean bool = moduleFunctionServer.AddbinDingUsers(moduleFunction,
					usersId);
			if (bool) {
				successMessage = "绑定用户成功!";
				ActionContext.getContext().getSession().put("successMessage",
						successMessage);
				return "bindingSuccess";
			}
			errorMessage = "绑定用户失败";
		} else {
			errorMessage = "不存在该功能!请检查后重试!";
		}
		return ERROR;
	}

	/*
	 * @author fy
	 * 
	 * @date 2018/8/21 15:20
	 * 
	 * @Description: 取消绑定用户, 根据ID减少模块绑定人员
	 * 
	 * @param []
	 * 
	 * @return java.lang.String
	 * 
	 * @throws
	 */
	public String DeletebinDingUsers() {
		pageStatus = "cancel";
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			boolean bool = moduleFunctionServer.DeletebinDingUsers(
					moduleFunction, usersId);
			if (bool) {
				successMessage = "取消绑定用户成功!";
				ActionContext.getContext().getSession().put("successMessage",
						successMessage);
				return "deletebindingSuccess";
			}
			errorMessage = "绑定用户失败";
		} else {
			errorMessage = "不存在该功能!请检查后重试!";
		}
		return ERROR;
	}

	// 绑定功能
	public String binDingModuleFunction() {
		boolean bool = moduleFunctionServer.binDingModuleFunction(id,
				detailSelect);
		if (bool) {
			errorMessage = "绑定成功！";
		}
		return "findMfByUserId";
	}

	// 修改模块功能
	public String updateMf() {
		String message = moduleFunctionServer.updateMf(moduleFunction, id,
				qxImage, qxImageFileName, xkImage, xkImageFileName, smallImage,
				smallImageFileName, mrImage, mrImageFileName, bsImage,
				bsImageFileName);
		if (message != null && "true".equals(message)) {
			successMessage = "修改 " + moduleFunction.getFunctionName() + " 成功!";
			ActionContext.getContext().getSession().put("successMessage",
					successMessage);
			ActionContext.getContext().getSession().put("moduleFunction",
					moduleFunction);
			pageStatus = "update";
			return "updateMfSuccess";
		} else {
			errorMessage = message;
		}
		return ERROR;
	}

	// 修改模块功能
	public String updateMf1() {
		boolean bool = moduleFunctionServer.updateMf1(moduleFunction, id);
		if (bool) {
			successMessage = "自拟定顺序成功!";
			ActionContext.getContext().getSession().put("successMessage",
					successMessage);
			return "updateMfSuccess";
		} else {
			errorMessage = "自拟定顺序失败!";
		}
		return ERROR;
	}

	// 修改模块功能1
	public String updateMfById1() {
		boolean bool = moduleFunctionServer.updateMfById1(detailSelect,
				detailSelect1);
		if (bool) {
			successMessage = "自拟定顺序成功!";
			ActionContext.getContext().getSession().put("successMessage",
					successMessage);
			return "updateMfSuccess1";
		} else {
			errorMessage = "自拟定顺序失败!";
		}
		return ERROR;
	}

	// 修改模块功能(上移、下移)
	public String updateMfById2() {
		if ("1".equals(a)) {// 上移
			moduleFunctionServer.updateMfById2(id, se_id);
			return "updateMfSuccess1";
		} else {// 下移
			moduleFunctionServer.updateMfById3(id, se_id);
			return "updateMfSuccess1";
		}
	}

	// 删除模块功能
	public String delMf() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			String message = moduleFunctionServer.delMf(moduleFunction);
			if (message != null && "true".equals(message)) {
				successMessage = "删除成功!";
				ActionContext.getContext().getSession().put("successMessage",
						successMessage);
				// 查询父类是否存在,如存在父类则跳转到父类详细页面
				Integer fatherId = moduleFunction.getFatherId();
				if (fatherId != null && fatherId > 0) {
					pageStatus = "delete";
					id = fatherId;
					return "updateMfSuccess";
				} else
					return "findAllMfSuccess";
			} else {
				errorMessage = message;
			}
		} else {
			errorMessage = "数据错误!不存在您要删除的功能!请检查后重试!";
		}
		return ERROR;
	}

	// 添加模块功能
	public String addMf() {
		ModuleFunction oldMf = moduleFunctionServer.findMfById(id);
		if (oldMf != null) {
			/**** 上传图标 */
			// 上传qxImage
			if (qxImage != null) {
				String qxImageName = "qx"
						+ Util.getDateTime("yyyyMMddHHmmss")
						+ qxImageFileName.substring(qxImageFileName
								.lastIndexOf("."), qxImageFileName.length());
				Util.UploadFile(qxImage, qxImageFileName, qxImageName,
						"/upload/file/sysImages",
						"D:/WorkSpace/HHTask/WebRoot/upload/file/sysImages");
				moduleFunction.setQximageName(qxImageName);
			}
			if (xkImage != null) {
				// 上传xkImage
				String xkImageName = "xk"
						+ Util.getDateTime("yyyyMMddHHmmss")
						+ xkImageFileName.substring(xkImageFileName
								.lastIndexOf("."), xkImageFileName.length());
				Util.UploadFile(xkImage, xkImageFileName, xkImageName,
						"/upload/file/sysImages",
						"D:/WorkSpace/HHTask/WebRoot/upload/file/sysImages");
				moduleFunction.setImageName(xkImageName);
			}
			// 上传smallImage
			if (smallImage != null) {
				String smallImageName = "sm"
						+ Util.getDateTime("yyyyMMddHHmmss")
						+ smallImageFileName.substring(smallImageFileName
								.lastIndexOf("."), smallImageFileName.length());
				Util.UploadFile(smallImage, smallImageFileName, smallImageName,
						"/upload/file/sysImages",
						"D:/WorkSpace/HHTask/WebRoot/upload/file/sysImages");
				moduleFunction.setSmallImageName(smallImageName);

			}
			if (mrImage != null) {
				// 上传mrImage
				String mrdhNoColor = "mr"
						+ Util.getDateTime("yyyyMMddHHmmss")
						+ mrImageFileName.substring(mrImageFileName
								.lastIndexOf("."), mrImageFileName.length());
				Util.UploadFile(mrImage, mrImageFileName, mrdhNoColor,
						"/upload/file/sysImages",
						"D:/WorkSpace/HHTask/WebRoot/upload/file/sysImages");
				moduleFunction.setDhNoColor(mrdhNoColor);
			}
			if (bsImage != null) {
				// 上传bsImage
				String bsdhHasColor = "bs"
						+ Util.getDateTime("yyyyMMddHHmmss")
						+ bsImageFileName.substring(bsImageFileName
								.lastIndexOf("."), bsImageFileName.length());
				Util.UploadFile(bsImage, bsImageFileName, bsdhHasColor,
						"/upload/file/sysImages",
						"D:/WorkSpace/HHTask/WebRoot/upload/file/sysImages");
				moduleFunction.setDhHasColor(bsdhHasColor);
			}
			/**** 上传图标 结束 */

			String message = moduleFunctionServer.addMf(moduleFunction, oldMf,
					pageStatus);

			// String message = null;

			if (message != null && "true".equals(message)) {
				successMessage = "添加模块功能 " + moduleFunction.getFunctionName()
						+ " 成功";
				ActionContext.getContext().getSession().put("successMessage",
						successMessage);
				ActionContext.getContext().getSession().put("moduleFunction",
						moduleFunction);
				pageStatus = "add";
				return "updateMfSuccess";
			} else {
				errorMessage = message;
			}
		} else {
			errorMessage = "上层模块功能不存在,无法添加!请检查后重试!";
		}
		return ERROR;
	}

	// 添加首页功能
	public String addMf1() {
		moduleFunctionServer.saveHome(moduleFunction);
		this.successMessage = "修改成功!";
		return "addMf1";
	}

	// 查询用户所对应第一层模块
	public String findMfByUser() {
		// SendMail s = new SendMail("liupei_yx@163.com", "test", "test");
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);
		allModuleList = moduleFunctionServer.findMfByUser(user);
		// 获得登录用户信息
		sessionsList = SessionListener.getSessions();
		count = sessionsList.size();
		// // 获得网站配置信息
		// companyInfo = moduleFunctionServer.findCompanyInfo();
		if (pageStatus == null || pageStatus.length() <= 0) {
			pageStatus = "";
		}

		return "findMfByUserSuccess";// /newIndex${pageStatus}.jsp
	}

	// 查询用户所对应第一层模块
	public String findMfByUser2() {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);
		allModuleList = moduleFunctionServer.findMfByUser(user);

		// 获得登录用户信息
		sessionsList = SessionListener.getSessions();
		count = sessionsList.size();

		// // 获得网站配置信息
		// companyInfo = moduleFunctionServer.findCompanyInfo();

		return "findMfByUserSuccess2";
	}

	
	
	//个人页面ifname()
	public void chageifanme() throws ServletException, IOException {
		moduleFunction = moduleFunctionServer.findMfByIdAndUser(id);
/*		int id=moduleFunction.getId();
		String jsp=moduleFunction.getFunctionLink();
		String name=moduleFunction.getFunctionName();*/
		if(moduleFunction.getFunctionLink()==null || moduleFunction.getFunctionLink()=="" ||moduleFunction.getFunctionLink().equals("")){
			MKUtil.writeJSON("此功能为原有功能，服务器已更改");
		}else {
			MKUtil.writeJSON(moduleFunction);
			//MKUtil.writeJSON(jsp);
			
		}
	}
	
	
	// 根据id查询模块功能 (跳转)
	public String findMfByIdForJump() throws ServletException, IOException {
		// AlertMessagesServerImpl.addAlertMessages("考核明细管理（总经理审核）",
		// "2013-06月的主管级考核成绩已经全部生成,请您处理工资信息!",
		// "2");
//		String b=ServletActionContext.getServletContext().getRealPath("/WEB-INF");
//		String a=ServletActionContext.getServletContext().getRealPath("/upload/file");
//		String nodepath = this.getClass().getClassLoader().getResource("/").getPath();
//		// 项目的根目录路径
//		String filePath = nodepath.substring(1, nodepath.length() - 23);

		if (pageStatus == null || pageStatus == "null") {
			pageStatus = "";
		}
		sessionsList = SessionListener.getSessions();
		count = sessionsList.size();
		moduleFunction = moduleFunctionServer.findMfByIdAndUser(id);
		if (moduleFunction != null) {
			mfNames = moduleFunctionServer.findMfNameForNavigation(
					moduleFunction, pageStatus);

			// // 获得网站配置信息
			// companyInfo = moduleFunctionServer.findCompanyInfo();

			// 登录用户
			Users user = (Users) ActionContext.getContext().getSession().get(
					TotalDao.users);

			// 查询用户所对应第一层模块,用于导航条显示
			allModuleList = moduleFunctionServer.findMfByUser(user);

			// 如果功能链接不为空,则为最后一层。 进行功能跳转
			if (moduleFunction.getFunctionLink() != null
					&& moduleFunction.getFunctionLink().length() > 0) {
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpServletResponse response = ServletActionContext
						.getResponse();
				String functionLink = moduleFunction.getFunctionLink();//
				// 功能跳转地址
				int httpUrl = functionLink.indexOf("http://");
				if(httpUrl<0){
					httpUrl = functionLink.indexOf("https://");
				}
				String targetNewPage = moduleFunction.getTargetNewPage();
				int actionUrl = functionLink.indexOf(".action");
				// ActionContext.getContext().getSession().put("mfNames",
				// mfNames);
				// ActionContext.getContext().getSession().put("moduleFunction",
				// moduleFunction);

				if (httpUrl == 0) {// http://必须在第一位
					List<ProjectRecord> prList = projectRecordServer
							.findAllProRecord();// 查询所有的登录网站信息
					boolean bool = false;

					for (int i = 0; i < prList.size(); i++) {
						ProjectRecord proRecord = prList.get(i);
						if (functionLink.indexOf(proRecord.getProjectName()) >= 0) {// 判断当前将要跳转的网站是否在系统管理中
							bool = true;
							projectRecord = proRecord;
							break;
						}
					}
					if (bool) {// 存在网站 ，查询当前登录用户所对应的帐号信息
						ProjectLogin projectLogin = projectRecordServer
								.findProjectLogin(user.getId(), projectRecord);
						if (projectLogin != null) {
							if (projectRecord.getLoginField() != null
									&& projectRecord.getLoginField().length() > 0) {
								String loginField[] = projectRecord
										.getLoginField().split(",");
								String loginFieldValue[] = projectLogin
										.getLoginFieldValue().split(",");
								if (loginField.length != loginFieldValue.length) {
									errorMessage = "参数不匹配!请检查后重试!";
									return ERROR;
								}
								String parameter = "?";
								for (int i = 0; i < loginField.length; i++) {
									parameter += loginField[i].trim() + "="
											+ loginFieldValue[i].trim() + "&";
								}
								parameter = parameter.substring(0, parameter
										.lastIndexOf("&"));
								response.sendRedirect(projectRecord
										.getLoginAction()
										+ parameter);// 自动登录网站
							} else {
								response.sendRedirect(projectRecord
										.getLoginAction());// 自动登录网站
							}
						} else {
							return "tologin";// 绑定登录信息
						}
					}else{
						response.sendRedirect(moduleFunction.getFunctionLink());
					}
				} else if (targetNewPage != null && "yes".equals(targetNewPage)) {
					if (actionUrl > 0) {
						response.sendRedirect(moduleFunction.getFunctionLink());
					} else {
						request.getRequestDispatcher(
								moduleFunction.getFunctionLink()).forward(
								request, response);
					}
				} else {
					String indexPage = pageStatus;
					pageStatus = "last";
					// 子模块
					// listSubModules =
					// moduleFunctionServer.findSubModule(moduleFunction.getId());
					// 子模块
					listSubModules = moduleFunctionServer
							.findSubModule(moduleFunction.getId());
					//同层模块
					List list = moduleFunctionServer
							.findLayerModule(moduleFunction.getFatherId());
					if (listSubModules != null) {
						if (list != null && list.size() > 0) {
							listSubModules.addAll(list);
						}
					}

					request.getRequestDispatcher("newSon" + indexPage + ".jsp")
							.forward(request, response);
				}

				return null;

			} else {// 否则则存在子层功能,查询所有子层功能
				moduleFunctionList = moduleFunctionServer.findSonMfById(user,
						id);// 查询下层
				// moduleFunctionServer.updateRootId(id);// 更新rootId以及背景色
				// moduleFunctionList =
				// moduleFunctionServer.findMfByUser(user);// 查询第一层
				return "sonMfList" + pageStatus;
			}
		} else {
			errorMessage = "不存在该模块功能或者您没有权限访问!请检查后重试!";
		}
		return ERROR;
	}

	// 搜索功能
	public String searchModuleFunction() {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);
		if (moduleFunction == null || moduleFunction.getFunctionName() == null) {
			errorMessage = "没有找到您的搜索的功能,请重试!";
			return ERROR;
		}
		moduleFunction.setFunctionName(moduleFunction.getFunctionName().trim());// 去除空格
		moduleFunctionList = moduleFunctionServer.searchModuleFunction(
				moduleFunction, user);
		pageStatus = "last";
		if (moduleFunctionList.size() > 0) {
			errorMessage = null;
			return "searchMfSuccess";
		} else {
			errorMessage = "没有找到您的搜索的功能,请重试!";
			return "searchMfSuccess";
		}
	}

	// 搜索功能:自动完成
	public String searchModuleFunction2() throws UnsupportedEncodingException {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);
		if (moduleFunction == null || moduleFunction.getFunctionName() == null) {
			errorMessage = "没有找到您的搜索的功能,请重试!";
			return ERROR;
		}
		String name = URLDecoder.decode(
				moduleFunction.getFunctionName().trim(), "UTF-8");
		moduleFunction.setFunctionName(name);// 去除空格
		moduleFunctionList = moduleFunctionServer.searchModuleFunction(
				moduleFunction, user);
		pageStatus = "last";
		if (moduleFunctionList.size() > 0) {
			class Module {
				public String modulename;
				public int moduleid;
			}
			List<Module> modules = new ArrayList<Module>();
			for (ModuleFunction func : moduleFunctionList) {
				Module module = new Module();
				module.moduleid = func.getId();
				module.modulename = func.getFunctionName();
				modules.add(module);
			}
			MKUtil.writeJSON(modules);
			return null;
		} else {
			errorMessage = "没有找到您的搜索的功能,请重试!";
			return "searchMfSuccess";
		}
	}

	/**
	 * ajax 获取带有拥有权限信息的部门Vo列表
	 */
	public void getDeptVos() {
		MKUtil.writeJSON(userDeptServer.getDeptVos(id));
	}

	/**
	 * 后台权限列表
	 * 
	 * @return
	 */
	public String showdispatcher() {
		userDeptList = userServer.getDispatchUsers();
		return "dispatchuser";
	}

	/**
	 * 后台权限指派
	 * 
	 * @return
	 */
	public String dispatchUsers() {
		if (user.getBackStage() == 1
				&& (deptIds == null || deptIds.equals("0"))) {
			errorMessage = "指派失败,请先选者指派部门！";
			return showdispatcher();
		}
		if (user != null && user.getBackStage() != null) {
			boolean b = userServer.upadteDispatch(user.getId(), user
					.getBackStage(), deptIds);
			if (b && user.getBackStage() == 0) {
				successMessage = "取消指派成功！";
			} else if (b && user.getBackStage() == 1) {
				successMessage = "指派成功！";
			} else if (!b && user.getBackStage() == 0) {
				errorMessage = "取消指派失败！";
			} else if (!b && user.getBackStage() == 1) {
				errorMessage = "指派失败！";
			}
		}
		return showdispatcher();
	}

	/***
	 * 通过父类id和登录用户获得对应功能列表
	 * 
	 * @param fatherId
	 * @return
	 */
	public void findMfByUserAndFId() {
		allModuleList = moduleFunctionServer.findMfByUserAndFId(id);
		MKUtil.writeJSON(allModuleList);
	}

	/***
	 * 查询功能模块to页面跳转
	 */
	public void findMfByIdForJson() {
		moduleFunction = moduleFunctionServer.findMfById(id);
		if (moduleFunction != null) {
			mfNames = moduleFunctionServer.findMfNameForNavigation(
					moduleFunction, pageStatus);

			// 如果功能链接不为空,则为最后一层。 进行功能跳转
			if (moduleFunction.getFunctionLink() != null
					&& moduleFunction.getFunctionLink().length() > 0) {
				MKUtil.writeJSON(new Object[] { mfNames, moduleFunction });
			} else {
				MKUtil.writeJSON(new Object[] { mfNames, moduleFunction });
			}
		}
	}

	// 根据人员查询所绑定的功能
	public String findMfByUserId() {
		if (moduleFunction != null) {
			ActionContext.getContext().getSession().put("moduleFunction",
					moduleFunction);
		} else {
			moduleFunction = (ModuleFunction) ActionContext.getContext()
					.getSession().get("moduleFunction");
		}
		if (id > 0) {
			ActionContext.getContext().getSession().put("id", id);
		} else {
			id = (Integer) ActionContext.getContext().getSession().get("id");
		}

		Object[] obj = moduleFunctionServer.findMfByUserId(id, moduleFunction,
				Integer.parseInt(cpage), pageSize);
		if (obj != null && obj.length > 0) {
			moduleFunctionList = (List<ModuleFunction>) obj[0];
			allModuleList = (List<ModuleFunction>) obj[1];
			if (allModuleList != null && allModuleList.size() > 0) {
				int sum = (Integer) obj[2];
				user = (Users) obj[3];
				int pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ModuleFunctionAction!findMfByUserId.action");
				errorMessage = null;
			} else {
				errorMessage = "抱歉!您查询的用户不存在或者已经与该功能绑定!";
			}
		}
		return "moduleFunction_UserList";
	}

	public String chang2SubModuleFunction() {
		if (fatherMfname != null || "".equals(fatherMfname)) {
			Boolean b = moduleFunctionServer.chang2SubModule(id, fatherMfname);
		}
		return "updateMfSuccess";
	}

	public String copyUsersMf_show() {
		return "copyUsersModuleFunction";
	}

	public String copymf() {
		Boolean bool = moduleFunctionServer.copyModuleFunction(Copyuserid,
				Pasteuserid);
		MKUtil.writeJSON(bool, "成功", null);
		return null;
	}

	// 去除人员功能
	public String delModulByUsers() {
		int count = moduleFunctionServer.delModulByUsers(user);
		errorMessage = "共去除掉 " + user.getName() + "的 " + count + "条功能!~";
		return "error";
	}

	public String exportExcel() {
		try {
			mfNames = new String(mfNames.getBytes("utf-8"), "UTF-8").trim();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		errorMessage = moduleFunctionServer.exportExcel(mfNames);
		return "error";
	}

	// 构造方法
	public ModuleFunctionServer getModuleFunctionServer() {
		return moduleFunctionServer;
	}

	public void setModuleFunctionServer(
			ModuleFunctionServer moduleFunctionServer) {
		this.moduleFunctionServer = moduleFunctionServer;
	}

	public ModuleFunction getModuleFunction() {
		return moduleFunction;
	}

	public void setModuleFunction(ModuleFunction moduleFunction) {
		this.moduleFunction = moduleFunction;
	}

	public List<ModuleFunction> getModuleFunctionList() {
		return moduleFunctionList;
	}

	public void setModuleFunctionList(List<ModuleFunction> moduleFunctionList) {
		this.moduleFunctionList = moduleFunctionList;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StrutsTreeNode getRoot() {
		return root;
	}

	public void setRoot(StrutsTreeNode root) {
		this.root = root;
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

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public UserServer getUserServer() {
		return userServer;
	}

	public void setUserServer(UserServer userServer) {
		this.userServer = userServer;
	}

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public Integer[] getUsersId() {
		return usersId;
	}

	public void setUsersId(Integer[] usersId) {
		this.usersId = usersId;
	}

	public List<ModuleFunction> getAllModuleList() {
		return allModuleList;
	}

	public void setAllModuleList(List<ModuleFunction> allModuleList) {
		this.allModuleList = allModuleList;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Users> getBangUserList() {
		return bangUserList;
	}

	public void setBangUserList(List<Users> bangUserList) {
		this.bangUserList = bangUserList;
	}

	public String getMfNames() {
		return mfNames;
	}

	public void setMfNames(String mfNames) {
		this.mfNames = mfNames;
	}

	public ProjectRecordServer getProjectRecordServer() {
		return projectRecordServer;
	}

	public void setProjectRecordServer(ProjectRecordServer projectRecordServer) {
		this.projectRecordServer = projectRecordServer;
	}

	public ProjectRecord getProjectRecord() {
		return projectRecord;
	}

	public void setProjectRecord(ProjectRecord projectRecord) {
		this.projectRecord = projectRecord;
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

	public List getSessionsList() {
		return sessionsList;
	}

	public void setSessionsList(List sessionsList) {
		this.sessionsList = sessionsList;
	}

	public CompanyInfo getCompanyInfo() {
		return companyInfo;
	}

	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
	}

	public Integer[] getDetailSelect() {
		return detailSelect;
	}

	public void setDetailSelect(Integer[] detailSelect) {
		this.detailSelect = detailSelect;
	}

	public Integer[] getDetailSelect1() {
		return detailSelect1;
	}

	public void setDetailSelect1(Integer[] detailSelect1) {
		this.detailSelect1 = detailSelect1;
	}

	public Integer getSe_id() {
		return se_id;
	}

	public void setSe_id(Integer seId) {
		se_id = seId;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public UserDeptServer getUserDeptServer() {
		return userDeptServer;
	}

	public void setUserDeptServer(UserDeptServer userDeptServer) {
		this.userDeptServer = userDeptServer;
	}

	public String getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(String deptIds) {
		this.deptIds = deptIds;
	}

	public File getQxImage() {
		return qxImage;
	}

	public void setQxImage(File qxImage) {
		this.qxImage = qxImage;
	}

	public String getQxImageContentType() {
		return qxImageContentType;
	}

	public void setQxImageContentType(String qxImageContentType) {
		this.qxImageContentType = qxImageContentType;
	}

	public String getQxImageFileName() {
		return qxImageFileName;
	}

	public void setQxImageFileName(String qxImageFileName) {
		this.qxImageFileName = qxImageFileName;
	}

	public File getXkImage() {
		return xkImage;
	}

	public void setXkImage(File xkImage) {
		this.xkImage = xkImage;
	}

	public String getXkImageContentType() {
		return xkImageContentType;
	}

	public void setXkImageContentType(String xkImageContentType) {
		this.xkImageContentType = xkImageContentType;
	}

	public String getXkImageFileName() {
		return xkImageFileName;
	}

	public void setXkImageFileName(String xkImageFileName) {
		this.xkImageFileName = xkImageFileName;
	}

	public File getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(File smallImage) {
		this.smallImage = smallImage;
	}

	public String getSmallImageContentType() {
		return smallImageContentType;
	}

	public void setSmallImageContentType(String smallImageContentType) {
		this.smallImageContentType = smallImageContentType;
	}

	public String getSmallImageFileName() {
		return smallImageFileName;
	}

	public void setSmallImageFileName(String smallImageFileName) {
		this.smallImageFileName = smallImageFileName;
	}

	public File getMrImage() {
		return mrImage;
	}

	public void setMrImage(File mrImage) {
		this.mrImage = mrImage;
	}

	public String getMrImageContentType() {
		return mrImageContentType;
	}

	public void setMrImageContentType(String mrImageContentType) {
		this.mrImageContentType = mrImageContentType;
	}

	public String getMrImageFileName() {
		return mrImageFileName;
	}

	public void setMrImageFileName(String mrImageFileName) {
		this.mrImageFileName = mrImageFileName;
	}

	public File getBsImage() {
		return bsImage;
	}

	public void setBsImage(File bsImage) {
		this.bsImage = bsImage;
	}

	public String getBsImageContentType() {
		return bsImageContentType;
	}

	public void setBsImageContentType(String bsImageContentType) {
		this.bsImageContentType = bsImageContentType;
	}

	public String getBsImageFileName() {
		return bsImageFileName;
	}

	public void setBsImageFileName(String bsImageFileName) {
		this.bsImageFileName = bsImageFileName;
	}

	public List<UserDept> getUserDeptList() {
		return userDeptList;
	}

	public void setUserDeptList(List<UserDept> userDeptList) {
		this.userDeptList = userDeptList;
	}

	public Integer getCopyuserid() {
		return Copyuserid;
	}

	public void setCopyuserid(Integer copyuserid) {
		Copyuserid = copyuserid;
	}

	public Integer getPasteuserid() {
		return Pasteuserid;
	}

	public void setPasteuserid(Integer pasteuserid) {
		Pasteuserid = pasteuserid;
	}

	public List<ModuleFunction> getListSubModules() {
		return listSubModules;
	}

	public void setListSubModules(List<ModuleFunction> listSubModules) {
		this.listSubModules = listSubModules;
	}

	public String getFatherMfname() {
		return fatherMfname;
	}

	public void setFatherMfname(String fatherMfname) {
		this.fatherMfname = fatherMfname;
	}

	public String getCpage2() {
		return cpage2;
	}

	public void setCpage2(String cpage2) {
		this.cpage2 = cpage2;
	}

	public String getTotal2() {
		return total2;
	}

	public void setTotal2(String total2) {
		this.total2 = total2;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}

	public int getPageSize2() {
		return pageSize2;
	}

	public void setPageSize2(int pageSize2) {
		this.pageSize2 = pageSize2;
	}

}
