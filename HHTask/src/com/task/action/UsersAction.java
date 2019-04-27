package com.task.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Dao.TotalDao;
import com.task.Server.ContractServer;
import com.task.Server.IAlsoService;
import com.task.Server.IBorrowService;
import com.task.Server.ModuleFunctionServer;
import com.task.Server.SmsService;
import com.task.Server.UserServer;
import com.task.Server.WageStandardServer;
import com.task.Server.fin.BaoXiaoDanServer;
import com.task.Server.payment.PaymentDetailServer;
import com.task.ServerImpl.UserFacialInforServerImpl;
import com.task.ServerImpl.ess.ProcardBlServerImpl;
import com.task.entity.Contract;
import com.task.entity.DeptNumber;
import com.task.entity.ModuleFunction;
import com.task.entity.Password;
import com.task.entity.Provision;
import com.task.entity.Users;
import com.task.entity.UsersCard;
import com.task.entity.WageStandard;
import com.task.entity.banci.BanCi;
import com.task.entity.fin.BaoxiaoDan;
import com.task.entity.payment.PaymentDetail;
import com.task.entity.renshi.InterviewLog;
import com.task.entity.renshi.Rank;
import com.task.entity.system.CompanyInfo;
import com.task.util.HttpRequest;
import com.task.util.HttpResponse;
import com.task.util.MD5;
import com.task.util.MKUtil;
import com.task.util.SessionListener;
import com.task.util.StrutsTreeNode;
import com.task.util.Upload;
import com.task.util.Util;
import com.task.util.license.VerifyLicense;

@SuppressWarnings( { "unchecked", "unused", "serial" })
public class UsersAction extends ActionSupport {
	public static final String mainUrl = "http://116.228.66.246:8099";// 主项目网址
	public static String selfUrl = null;// 本项目url
	public static String loginIp = null;// 登录人ip
	private UserServer userServer;
	private SmsService smsService;
	private WageStandardServer wageStandardServer;// 工资模板Server层
	private ModuleFunctionServer moduleFunctionServer;// Server层
	private List<ModuleFunction> moduleFunctionList;// 模块功能集合
	private ContractServer contractServer;// 合同
	private List<Provision> provisionList;// 合同条款集合（改）
	private Users user;// 用户
	private boolean bool;
	private Users loginUser;
	private int id;// id
	private List<Users> userList;
	private List<BanCi> banciList;
	private Password password;// 密码
	private String deptNumber;// 部门编号;
	private InterviewLog interviewLog;// 面试单
	private String vcode;// 验证码
	private String autoLogin;// 自动登录
	private String password1;// 新密码
	private String password2;// 新密码
	private String pageStatus;// 状态
	private String cardNumber;// 卡号
	private String contractNumber;// 合同编号

	private String errorMessage;// 错误信息
	private String successMessage;// 成功信息

	private File picture;// 照片
	private String pictureContentType;// 文件类型
	private String pictureFileName;// 文件名称
	private List<Users> tryDaysEndList;// 试用期到期提醒人员
	private List<Users> contractEndList;// 合同到期提醒人员
	private String deptName;// 部门名称
	private String test;// 标识
	private List list;
	private List borrowlist;
	private List alsolist;
	private List baoxiaolist;
	private List<String> workrecords;// 工作记录
	private List<Map> paymentmaps;// 借款
	private String external = "否";// 是否显示外部人员

	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;

	private StrutsTreeNode userRoot;// tree
	private String markId;// 件号
	private String gongwei;// 件号
	private String name;// 件号
	private String code;// 件号
	private List markList;
	private List usersList;
	private List gongweiList;
	private List processNOList;
	private String firstApplyDate;
	private String submitDate;
	private String processNO;
	private String tag;
	private String processName;
	private Integer[] arrayId;
	private UsersCard userscard;
	private Rank rank;
	private List<Rank> rankList;
	private List<UsersCard> usercardList;

	private IBorrowService borrowService;
	private IAlsoService alsoService;
	private BaoXiaoDanServer baoXiaoDanServer;
	// private PaymentVoucherServer paymentVoucherServer;
	private PaymentDetailServer paymentDetailServer;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public void getMaxCode() {
		Integer maxCode = userServer.getMaxCode();
		MKUtil.writeJSON(maxCode);
	}

	public String findByCode() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		Users u = userServer.findUserByCode(user.getCode());
		if (u == null) {
			MKUtil.writeJSON(false, "查询失败!不存在该工号", null);
		} else {
			Users u1 = new Users();
			BeanUtils.copyProperties(u, u1, new String[] { "moduleFunction",
					"worklogClass", "template" });
			u1.getPassword().setUser(null);
			MKUtil.writeJSON(true, "查询成功", u1);
		}
		return null;
	}

	public String findImageByCode() {
		// Users loginUser=Util.getLoginUser();
		// if (loginUser == null) {
		// return "loginError";
		// }
		String image = userServer.findImageByCode(code);
		if (image == null) {
			MKUtil.writeJSON(null);
		} else {
			MKUtil.writeJSON(image);
		}
		return null;
	}

	public String findNameByCode() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		Users u = userServer.findUserByCode(user.getCode());
		if (u == null) {
			MKUtil.writeJSON(false, "查询失败!不存在该工号", null);
			return null;
		}
		MKUtil.writeJSON(true, "查询成功", u.getName());
		return null;

	}

	// 添加新员工
	public String addUser() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		errorMessage = userServer.addUser(user, picture, pictureFileName,
				interviewLog);
		if ("true".equals(errorMessage)) {
			successMessage = "添加员工: " + user.getName() + "成功!";
			errorMessage = null;
			if ("fx".equals(pageStatus)) {
				errorMessage = "添加成功";
				return "lesson_addstudent";
			}
			return "addUserSuccess";
		}

		return ERROR;
	}

	// 通过Id查询用户for员工状态处理跳转
	public String findUserById() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			if (pageStatus == null || pageStatus.length() <= 0) {
				pageStatus = null;
				String userStatus = user.getPassword().getUserStatus();// 用户状态
				if ("上传简历".equals(userStatus)) {
					return "uploadResume";
				} else if ("签订合同".equals(userStatus)) {
					provisionList = userServer.findAllProvision("public");
					contractNumber = contractServer.findContractNumber();// 生成合同编号
					return "contract";
				} else if ("薪资处理".equals(userStatus) || "完成".equals(userStatus)) {
					return "wageAddOrUpdate";
				} else {
					return "userDettails";
				}
			} else {
				if ("1".equals(pageStatus)) {
					return "uploadResume";
				} else if ("2".equals(pageStatus)) {
					provisionList = userServer.findAllProvision("public");
					contractNumber = contractServer.findContractNumber();// 生成合同编号
					return "contract";
				} else if ("3".equals(pageStatus)) {
					return "Salary";
				} else {
					return "userDettails";
				}
			}
		} else {
			errorMessage = "该员工不存在!请检查后重试!";
		}
		return ERROR;
	}

	// 签订薪资调整协议签订以及打印
	public String wageSignedAndPrint() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			Contract contract = contractServer.findContractByCode(user
					.getCode());
			if (contract != null) {
				errorMessage = user.getName()
						+ "存在尚未处理完成的薪资协议,无法继续添加薪资协议!可到薪资调整历史查看处理状态!";
			}
			provisionList = userServer.findAllProvision("wage");
			return "wageSignedAndPrint";
		}
		return ERROR;
	}

	// 上传简历
	public String uploadResume() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			boolean bool = userServer.uploadResume(user, picture,
					pictureFileName, pageStatus);
			if (bool) {
				pageStatus = null;
				if ("sc".equals(tag)) {
					pageStatus = "1";
				}
				successMessage = "上传文件成功!";
				return "addUserSuccess";
			} else {
				errorMessage = "上传文件失败!";
			}
		} else {
			errorMessage = "不存在该用户";
		}
		return ERROR;
	}

	// 跳过上传简历 (转正)
	public String skipUploadResume() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			if ("劳务".equals(user.getPassword().getStaffNature())) {// 如果是劳务员则跳过签订合同
				user.getPassword().setUserStatus("薪资处理");
			} else {
				user.getPassword().setUserStatus("签订合同");
			}
			boolean bool = userServer.updateUser(user);
			if (bool) {
				return "addUserSuccess";
			} else {
				errorMessage = "跳过上传简历失败!请检查后您重试!";
			}
		} else {
			errorMessage = "不存在该员工!请检查后重试!";
		}
		return ERROR;
	}

	/**
	 * 得到所有班次信息
	 * 
	 * @return
	 */
	public void findAllBanci() {
		MKUtil.writeJSON(userServer.findBanCi());
	}

	// 通过Id查询用户for用户明细
	public String findUserByIdForDetails() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		bool = userServer.findadmin();
		user = userServer.findUserById(id);
		if (user != null) {
			try {
				if (successMessage != null && successMessage.length() > 0) {
					successMessage = URLDecoder.decode(successMessage, "utf-8");
				} else {
					successMessage = null;
				}
				if (errorMessage != null && errorMessage.length() > 0) {
					errorMessage = URLDecoder.decode(errorMessage, "utf-8");
				} else {
					errorMessage = null;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if ("zz".equals(tag)) {
				return "becoming_add";
			}
			return "userDettails";// hr_userDetails
		}
		errorMessage = "不存在该用户!请检查后重试!";
		return ERROR;
	}

	// 通过Id查询用户for用户明细
	public String findUserByIdForSummary() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);

		// 卡号-物品借用归还
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cardId", user.getCardId());
		map.put("person", user.getName());
		Object[] borrowobject = borrowService.queryBorrow(map, Integer
				.parseInt(cpage), pageSize);
		Object[] alsoobject = alsoService.queryAlso(map, Integer
				.parseInt(cpage), pageSize);

		if (borrowobject != null && borrowobject.length > 0) {
			borrowlist = (List) borrowobject[0];
		}
		if (alsoobject != null && alsoobject.length > 0) {
			alsolist = (List) alsoobject[0];
		}

		// 工作记录
		if (processName != null && !"".equals(processName)) {
			int a = processName.indexOf("(");
			int b = processName.indexOf(")");
			processName = processName.substring(a + 1, b);
		}
		Object[] Workrecordsobject = userServer.findAllWorkrecords1(id, markId,
				processName, Integer.parseInt(cpage), pageSize);
		ArrayList<Object> WorkrecordsArr = (ArrayList<Object>) Workrecordsobject[0];
		if (WorkrecordsArr.size() != 0) {
			Object[] WorkrecordsObjs = (Object[]) WorkrecordsArr.get(0);
			workrecords = new ArrayList<String>();
			for (int i = 0; i < WorkrecordsObjs.length; i++) {
				String aString = String.valueOf(WorkrecordsObjs[i]);
				workrecords.add(aString);
			}
		}
		// 财务明细报销
		BaoxiaoDan baoxiaodan = new BaoxiaoDan();
		baoxiaodan.setBaoxiaoren(user.getName());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Object[] baoxiaoob = baoXiaoDanServer.findBaoXiaoDanDetail(baoxiaodan,
				null, df.format(new Date(2000)), df.format(new Date()), Integer
						.parseInt(cpage), pageSize, this.tag);
		baoxiaolist = (List) baoxiaoob[1];
		// 财务明细借款
		PaymentDetail detail = new PaymentDetail();
		detail.setUnitname1(user.getName());
		Object[] object = this.paymentDetailServer.findPaymentVoucher(detail,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			paymentmaps = (List<Map>) object[0];
		}

		// errorMessage = "不存在该用户!请检查后重试!";
		// return ERROR;
		return "userSummary";
	}

	// 维护员工信息
	public String updateUser() throws UnsupportedEncodingException {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		Users oldUser = userServer.findUserById(user.getId());
		if (oldUser != null) {
			if (oldUser.getPassword() != null) {
				user.getPassword().setId(oldUser.getPassword().getId());
			}
			user.getPassword().setUser(user);
			user.setModuleFunction(oldUser.getModuleFunction());
			user.setUserRole(oldUser.getUserRole());
			user.setWorklogClass(oldUser.getWorklogClass());
			user.setJimileixing(oldUser.getJimileixing());
			user.setProcessGzstore(oldUser.getProcessGzstore());
			user.setAccessEquipments(oldUser.getAccessEquipments());
			user.setProcessGzstore(oldUser.getProcessGzstore());
			user.setToolCabines(oldUser.getToolCabines());
			user.setMachine(oldUser.getMachine());
			user.setCategorySet(oldUser.getCategorySet());
			user.setDeptNumber(oldUser.getDeptNumber());
			user.setFingerprintMg(oldUser.getFingerprintMg());
			user.setSchedulingTable(oldUser.getSchedulingTable());
			user.setPayableTypes(oldUser.getPayableTypes());
			user.setUserscard(oldUser.getUserscard());
			user.setUserStatus(oldUser.getUserStatus());
			user.setFristTime(oldUser.getFristTime());

			boolean bool = true;

			// 如果员工卡号改变或者部门改变,更新工资模板
			if (!user.getCardId().equals(oldUser.getCardId())
					|| !user.getCode().equals(oldUser.getCode())
					|| !user.getDept().equals(oldUser.getDept())) {
				WageStandard ws = wageStandardServer.findWSByUser(oldUser
						.getCode());
				if (ws != null) {
					ws.setCode(user.getCode());// 工号
					ws.setCardId(user.getCardId());// 卡号
					ws.setDept(user.getDept());// 部门
					bool = wageStandardServer.updateWS(ws);
					if (bool == false) {
						errorMessage = "更新工资模版失败!请检查后重试!";
						return ERROR;
					}
				}
			}
			if (user.getName() == null || user.getName().equals("")) {
				errorMessage = "姓名不能为空";
				return ERROR;
			}
			// 当需要同步考勤机信息时
			boolean boo = userServer.findadmin();
			if (boo) {
				if (!oldUser.getName().equals(user.getName())
						|| (user.getUser_privilege() != null
								&& !"".equals(user.getUser_privilege()) && !user
								.getUser_privilege().equals(
										oldUser.getUser_privilege()))) {// 姓名有更改或者权限有更改时。
					if (user.getUser_privilege() == null
							|| user.getUser_privilege().equals(""))// 姓名有更改时判断
						user.setUser_privilege("USER");
					userServer.updateHSKaoqin(user);
				}
			}
			// 更新领用、借、还和出库表中相同的卡号。
			if (!user.getCardId().equals(oldUser.getCardId())) {
				/*
				 * userServer.getcodeNumber("Consuming",
				 * "cardNum",oldUser.getCardId(), user.getCardId());
				 * userServer.getcodeNumber("Also",
				 * "cardNum",oldUser.getCardId(), user.getCardId());
				 * userServer.getcodeNumber("OutLib",
				 * "cardNum",oldUser.getCardId(), user.getCardId());
				 * userServer.getcodeNumber("Borrow",
				 * "cardNum",oldUser.getCardId(), user.getCardId());
				 */
				userServer.updatecodeNumber(oldUser.getCardId(), user
						.getCardId());// 将新工号和旧工号一起传过去
			}

			// 更新部门编号
			if (!user.getDept().equals(oldUser.getDept())) {
				DeptNumber deptNumber = userServer.findDeptNumberByDept(user
						.getDept());
				if (deptNumber != null) {
					user.setDeptId(deptNumber.getId());
					user.getPassword()
							.setDeptNumber(deptNumber.getDeptNumber());
				}
			}

			// 如果人员转正 使用转正工资模板
			if ("试用".equals(oldUser.getOnWork())
					&& "正式".equals(user.getOnWork())) {

			}

			// 如果人员离职则将其状态改为"离职中",此状态是做中转作用的
			if ("离职".equals(user.getOnWork())) {
				user.setOnWork("离职中");
				user.setModuleFunction(null);
			} else if ("直接离职".equals(user.getOnWork())) {
				user.setOnWork("离职");
				user.setModuleFunction(null);
			}

			if (picture != null) {// 如果重新上传照片
				bool = userServer.uploadResume(user, picture, pictureFileName,
						"picture");
			} else {
				bool = userServer.updateUser(user);
			}
			if (bool) {
				// 更新 职业轨迹信息
				if ((("试用").equals(oldUser.getOnWork()) || ("实习")
						.equals(oldUser.getOnWork()))
						&& "在职".equals(user.getOnWork())
						&& (null == oldUser.getZhuanzhengtime() || ""
								.equals(oldUser.getZhuanzhengtime()))) {
					user.setZhuanzhengtime(Util.getDateTime());
					bool = userServer.updateUser(user);
					if (bool) {
						userServer.updateCareetrack(user);
					}

				}
				successMessage = URLEncoder.encode(URLEncoder.encode("维护员工 "
						+ user.getName() + "成功!", "utf-8"), "utf-8");

				return "findUser";
			} else {
				errorMessage = "维护员工 " + user.getName() + "失败!";
			}

		} else {
			errorMessage = "不存在该用户!请检查后重试!";
		}
		return ERROR;
	}

	public String tiaozhuan() {// 页面间跳转使用；
		return "qita_login";

	}

	// 完善信息页面
	public String perfectUsers() {
		user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请先登录";
			return "error";
		}
		user = userServer.findUserById(user.getId());
		return "per_User";
	}

	public String updateUsersW() {
		if (user == null) {
			return "error";
		}
		if (user.getId() != null) {
			errorMessage = userServer.updateUsersW(user, picture,
					pictureFileName);
			user = userServer.findUserById(user.getId());
			return "per_User";
		}
		return "error";
	}

	// 登录
	public String login() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 获得真实ip
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.length() > 15) { // "***.***.***.***".length() = 15
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		System.out.println(ip);
		loginIp = ip;
		// selfUrl = UsersAction.getSelfUrl(request);
		// boolean b = UsersAction.validateLic(selfUrl);
		// if (b) {
		// // errorMessage = UsersAction.validateCount(selfUrl);
		// // if (!"通过".equals(errorMessage)) {
		// // return "loginError";
		// // }
		// } else {
		// errorMessage = "证书没有上传，或者已过期！";
		// request.getSession().setAttribute("uploadLicense", "0");
		// return "uploadlic";
		// }

		if (user != null && user.getCode().length() > 0
				&& password.getPassword().length() > 0) {
			// 验证码处理
			String sRand = (String) request.getSession().getAttribute("sRand");
			sRand = "";
			if (sRand != null && sRand.length() > 0) {
				if (vcode != null && vcode.length() > 4) {
					errorMessage = "请输入前四位验证码!";
					request.setAttribute("sRand", sRand);
					return "loginError";
				} else {
					if (!sRand.subSequence(0, 4).equals(vcode)) {
						errorMessage = "验证码错误";
						request.setAttribute("sRand", sRand);
						return "loginError";
					}
				}
				request.getSession().removeAttribute("sRand");// 清除验证码
			}

			// 登录处理
			errorMessage = userServer.login(user, password, autoLogin);
			if ("true".equals(errorMessage)
					|| (tag != null && tag.equals("sjlogin") && "该部门编号错误! "
							.equals(errorMessage))) {

				/****************** 记录本次的访问方式 *****************/

				if ("qita".equals(password1)) {
					MKUtil.writeJSON(errorMessage);
					return null;
				}
//				if (Util.getDateTime("yyyy-MM-dd").equals("2018-08-17")) {
//					Cookie[] cookies = request.getCookies();
//					String qixilogin = "";
//					if (cookies != null) {
//						for (int i = 0; i < cookies.length; i++) {
//							Cookie cookie = cookies[i];
//							if ("qixilogin".equals(cookie.getName())) {
//								qixilogin = cookie.getValue();
//								break;
//							}
//						}
//					}
//					if ("".equals(qixilogin)) {
//						Cookie qixiloginCookie = new Cookie("qixilogin",
//								"qixilogin");
//						qixiloginCookie.setMaxAge(60 * 60 * 24);
//						response.addCookie(qixiloginCookie);
//						try {
//							response.sendRedirect("/test/qixi/index.html");
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						return "loginError";
//					}
//				}
				boolean isFromMobile = false;
				// 检查是否已经记录访问方式（移动端或pc端）
				if (null == request.getSession().getAttribute("mobileOrPc")) {
					try {
						// 获取ua，用来判断是否为移动端访问
						String userAgent = ((HttpServletRequest) request)
								.getHeader("USER-AGENT").toLowerCase();
						if (null == userAgent) {
							userAgent = "";
						}
						isFromMobile = Util.check(userAgent);
						// 判断是否为移动端访问
						if (isFromMobile) {
							request.getSession().setAttribute("mobileOrPc",
									"mobile");
						} else {
							request.getSession().setAttribute("mobileOrPc",
									"pc");
						}
					} catch (Exception e) {
					}
				}
				/****************** 记录本次的访问方式结束 *****************/

				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}
				// 供应商登录直接到投标系统
				if (pageStatus != null && "gys".equals(pageStatus)) {
					// 登录方式
					Cookie loginTypeCookie = new Cookie("loginType",
							"gysLogin.jsp");
					loginTypeCookie.setMaxAge(60 * 60 * 24 * 7);
					response.addCookie(loginTypeCookie);
					pageStatus = "qx";
				}
				// 正常登录处理
				String host = null;// 上一个页面的地址
				Object uploadLicense = request.getSession().getAttribute(
						"uploadLicense");
				if (uploadLicense == null
						|| !uploadLicense.toString().equals("0")) {
					host = request.getHeader("Referer");// 上一个页面的地址
				} else {
					request.getSession().removeAttribute("uploadLicense");
				}
				if (host != null && host.length() > 0
						&& host.indexOf("UsersAction!login.action") < 0) {
					try {
						if (host.indexOf("login.jsp") >= 0
								|| host.indexOf("otherLogin.jsp") >= 0
								|| host.indexOf("gysLogin.jsp") >= 0) {
							response
									.sendRedirect("ModuleFunctionAction!findMfByUser.action?pageStatus="
											+ pageStatus);
						} else if (host.indexOf("sjLogin.jsp") >= 0) {
							if (tag != null && tag.equals("xjlogin")) {
								response
										.sendRedirect("InsRecord_getToXjList.action");
							} else {
								response
										.sendRedirect("LogoStickerAction!findSjList.action");
							}
							return "sjloginSuccess";
						} else if (host.indexOf("fxLogin.jsp") >= 0) {
							Users user = Util.getLoginUser();
							if (user != null && user.getClassrole() != null
									&& user.getClassrole().equals("老师")) {
								response
										.sendRedirect("attendClassAction_initaddLesson.action");
								return "saloginSuccess";
							} else {
								errorMessage = "工号或密码不正确";
							}
						} else {
							response.sendRedirect(host);
						}
					} catch (IOException e) {
						return "loginSuccess";
					}
				} else {
					return "loginSuccess";
				}
			}
		} else {
			errorMessage = "员工号或部门编号或者密码不能为空!";
		}
		if ("qita".equals(password1)) {
			MKUtil.writeJSON(errorMessage);
			return null;
		}
		if (pageStatus != null && "gys".equals(pageStatus)) {
			return "gysLoginError";
		} else {
			return "loginError";

		}
	}

	/**
	 * 档案信息查看
	 * 
	 * @return
	 */
	public String login_dangan() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 获得真实ip
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		loginIp = ip;
		// selfUrl = UsersAction.getSelfUrl(request);
		// boolean b = UsersAction.validateLic(selfUrl);
		// if (b) {
		// // errorMessage = UsersAction.validateCount(selfUrl);
		// // if (!"通过".equals(errorMessage)) {
		// // return "loginError";
		// // }
		// } else {
		// errorMessage = "证书没有上传，或者已过期！";
		// request.getSession().setAttribute("uploadLicense", "0");
		// return "uploadlic";
		// }

		if (user != null && user.getCode().length() > 0
				&& password.getPassword().length() > 0) {
			// 验证码处理
			String sRand = (String) request.getSession().getAttribute("sRand");
			sRand = "";
			if (sRand != null && sRand.length() > 0) {
				if (vcode != null && vcode.length() > 4) {
					errorMessage = "请输入前四位验证码!";
					request.setAttribute("sRand", sRand);
					return "loginError";
				} else {
					if (!sRand.subSequence(0, 4).equals(vcode)) {
						errorMessage = "验证码错误";
						request.setAttribute("sRand", sRand);
						return "loginError";
					}
				}
				request.getSession().removeAttribute("sRand");// 清除验证码
			}
			// 登录处理
			errorMessage = userServer.login(user, password, autoLogin);

			if ("true".equals(errorMessage)) {
				/****************** 记录本次的访问方式 *****************/
				boolean isFromMobile = false;
				// 检查是否已经记录访问方式（移动端或pc端）
				if (null == request.getSession().getAttribute("mobileOrPc")) {
					try {
						// 获取ua，用来判断是否为移动端访问
						String userAgent = ((HttpServletRequest) request)
								.getHeader("USER-AGENT").toLowerCase();
						if (null == userAgent) {
							userAgent = "";
						}
						isFromMobile = Util.check(userAgent);
						// 判断是否为移动端访问
						if (isFromMobile) {
							request.getSession().setAttribute("mobileOrPc",
									"mobile");
						} else {
							request.getSession().setAttribute("mobileOrPc",
									"pc");
						}
					} catch (Exception e) {
					}
				}
				/****************** 记录本次的访问方式结束 *****************/

				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}
				// 正常登录处理
				try {
					response
							.sendRedirect("AccessEquipmentAction_selectPrice.action?id="
									+ id);
					return null;
				} catch (IOException e) {
					errorMessage = "信息异常！";
					return "error";
				}
			} else {
				errorMessage = "工号或者密码有误!";
			}
		} else {
			errorMessage = "工号或者密码不能为空!";
		}
		return "error";
	}

	/***
	 * 检验登录（工号和密码登录）
	 * 
	 * @return
	 */
	public String jyLogin() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 获得真实ip
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		loginIp = ip;
		// selfUrl = UsersAction.getSelfUrl(request);
		// boolean b = UsersAction.validateLic(selfUrl);
		// if (b) {
		// // errorMessage = UsersAction.validateCount(selfUrl);
		// // if (!"通过".equals(errorMessage)) {
		// // return "loginError";
		// // }
		// } else {
		// errorMessage = "证书没有上传，或者已过期！";
		// request.getSession().setAttribute("uploadLicense", "0");
		// return "uploadlic";
		// }

		if (user != null && user.getCode().length() > 0
				&& password.getPassword().length() > 0) {
			// 验证码处理
			String sRand = (String) request.getSession().getAttribute("sRand");
			if (sRand != null && sRand.length() > 0) {
				if (vcode != null && vcode.length() > 4) {
					errorMessage = "请输入前四位验证码!";
					request.setAttribute("sRand", sRand);
					return "loginError";
				} else {
					if (!sRand.subSequence(0, 4).equals(vcode)) {
						errorMessage = "验证码错误";
						request.setAttribute("sRand", sRand);
						return "loginError";
					}
				}
				request.getSession().removeAttribute("sRand");// 清除验证码
			}
			// 登录处理
			errorMessage = userServer
					.login(user.getCode(), password, autoLogin);

			if ("true".equals(errorMessage) && tag != null
					&& (tag.equals("sjlogin") || tag.equals("xjlogin"))) {
				/****************** 记录本次的访问方式 *****************/
				boolean isFromMobile = false;
				// 检查是否已经记录访问方式（移动端或pc端）
				if (null == request.getSession().getAttribute("mobileOrPc")) {
					try {
						// 获取ua，用来判断是否为移动端访问
						String userAgent = ((HttpServletRequest) request)
								.getHeader("USER-AGENT").toLowerCase();
						if (null == userAgent) {
							userAgent = "";
						}
						isFromMobile = Util.check(userAgent);
						// 判断是否为移动端访问
						if (isFromMobile) {
							request.getSession().setAttribute("mobileOrPc",
									"mobile");
						} else {
							request.getSession().setAttribute("mobileOrPc",
									"pc");
						}
					} catch (Exception e) {
					}
				}
				/****************** 记录本次的访问方式结束 *****************/

				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}

				// 正常登录处理
				String host = null;// 上一个页面的地址
				Object uploadLicense = request.getSession().getAttribute(
						"uploadLicense");
				if (uploadLicense == null
						|| !uploadLicense.toString().equals("0")) {
					host = request.getHeader("Referer");// 上一个页面的地址
				} else {
					request.getSession().removeAttribute("uploadLicense");
				}
				if (host != null && host.length() > 0
						&& host.indexOf("UsersAction!jyLogin2.action") < 0
						&& host.indexOf("UsersAction!jyLogin.action") < 0) {
					try {
						if (host.indexOf("sjLogin.jsp") >= 0) {
							response
									.sendRedirect("UsersAction!sczzIndex.action");
							// if (tag != null && tag.equals("xjlogin")) {
							// response
							// .sendRedirect("InsRecord_getToXjList.action");
							// } else {
							// response
							// .sendRedirect("LogoStickerAction!findSjList.action");
							// }
							return "sjloginSuccess";
						} else if (host.indexOf("fxLogin.jsp") >= 0) {
							Users user = Util.getLoginUser();
							if (user != null && user.getClassrole() != null
									&& user.getClassrole().equals("老师")) {
								response
										.sendRedirect("attendClassAction_initaddLesson.action");
								return "saloginSuccess";
							} else {
								errorMessage = "工号或密码不正确";
							}
						} else {
							response.sendRedirect(host);
						}
					} catch (IOException e) {
						return "sjloginSuccess";
					}
				} else {
					return "sjloginSuccess";
				}
			}
		} else {
			errorMessage = "员工号或部门编号或者密码不能为空!";
		}
		return "jyloginError";
	}

	public String sheBeiLogin() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 获得真实ip
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		loginIp = ip;
		if (user != null && user.getCode().length() > 0
				&& password.getPassword().length() > 0) {
			// 登录处理
			errorMessage = userServer
					.login(user.getCode(), password, autoLogin);
			// errorMessage = userServer.login(user, password);
			if ("true".equals(errorMessage)
					&& tag != null
					&& (tag.equals("sjlogin") || tag.equals("xjlogin") || tag
							.equals("sheBeilogin"))) {
				/****************** 记录本次的访问方式 *****************/
				boolean isFromMobile = false;
				// 检查是否已经记录访问方式（移动端或pc端）
				if (null == request.getSession().getAttribute("mobileOrPc")) {
					try {
						// 获取ua，用来判断是否为移动端访问
						String userAgent = ((HttpServletRequest) request)
								.getHeader("USER-AGENT").toLowerCase();
						if (null == userAgent) {
							userAgent = "";
						}
						isFromMobile = Util.check(userAgent);
						// 判断是否为移动端访问
						if (isFromMobile) {
							request.getSession().setAttribute("mobileOrPc",
									"mobile");
						} else {
							request.getSession().setAttribute("mobileOrPc",
									"pc");
						}
					} catch (Exception e) {
					}
				}
				/****************** 记录本次的访问方式结束 *****************/

				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}
				// 正常登录处理
				String host = null;// 上一个页面的地址
				Object uploadLicense = request.getSession().getAttribute(
						"uploadLicense");
				if (uploadLicense == null
						|| !uploadLicense.toString().equals("0")) {
					host = request.getHeader("Referer");// 上一个页面的地址
				} else {
					request.getSession().removeAttribute("uploadLicense");
				}
				if (host != null && host.length() > 0
						&& host.indexOf("UsersAction!jyLogin2.action") < 0
						&& host.indexOf("UsersAction!jyLogin.action") < 0) {
					try {
						if (host.indexOf("sheBeiLogin.jsp") >= 0) {
							response
									.sendRedirect("ProdEquipmentAction!checkMachiner.action?id="
											+ id);
							// if (tag != null && tag.equals("xjlogin")) {
							// response
							// .sendRedirect("InsRecord_getToXjList.action");
							// } else {
							// response
							// .sendRedirect("LogoStickerAction!findSjList.action");
							// }
							return "sheBeiloginSuccess";
						} else {
							response.sendRedirect(host);
						}
					} catch (IOException e) {
						return "sheBeiloginSuccess";
					}
				} else {
					return "sheBeiloginSuccess";
				}
			}
		} else {
			errorMessage = "员工号或部门编号或者密码不能为空!";
		}
		return "sheBeiloginSuccess";
	}

	/***
	 * 生产制造主页面
	 * 
	 * @return
	 */
	public String sczzIndex() {
		if (Util.getLoginUser() == null) {
			errorMessage = "请先登录";
			return ERROR;
		}
		moduleFunctionList = moduleFunctionServer.findSonMfByIdPhone_one();//
		// 查询下层
		// HttpServletRequest request = ServletActionContext.getRequest();
		// HttpServletResponse response = ServletActionContext.getResponse();
		// try {
		// response.sendRedirect("/HHTask/System/SOP/qd/sczzIndex.jsp");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return "sczzIndex_1";
	}

	public String shouchiZD() {
		if (Util.getLoginUser() == null) {
			errorMessage = "请先登录";
			return ERROR;
		}
		moduleFunctionList = moduleFunctionServer.findSonMfByIdPhone(id);// 查询下层
		return "sczzIndex_1";
	}

	/**
	 * 跳转到输入工号领料页面
	 */
	public String noCodelingliao() {
		return "process_shuakall_code";
	}

	/**
	 * 跳转到领料页面
	 * 
	 * @return
	 */
	public String codelingliao() {
		Users users = userServer.findUserByCode(code);
		if (users == null) {
			errorMessage = "工号不存在";
			return ERROR;
		}
		cardNumber = users.getCardId();
		return "codeLingliao";
	}

	/***
	 * 检验登录（工号和密码登录）
	 * 
	 * @return
	 */
	public String jyLogin2() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 获得真实ip
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		loginIp = ip;
		selfUrl = UsersAction.getSelfUrl(request);
		boolean b = UsersAction.validateLic(selfUrl);
		if (b) {
			// errorMessage = UsersAction.validateCount(selfUrl);
			// if (!"通过".equals(errorMessage)) {
			// return "loginError";
			// }
		} else {
			errorMessage = "证书没有上传，或者已过期！";
			request.getSession().setAttribute("uploadLicense", "0");
			return "uploadlic";
		}

		if (user != null && user.getCode().length() > 0
				&& password.getPassword().length() > 0) {
			// 验证码处理
			String sRand = (String) request.getSession().getAttribute("sRand");
			if (sRand != null && sRand.length() > 0) {
				if (vcode != null && vcode.length() > 4) {
					errorMessage = "请输入前四位验证码!";
					request.setAttribute("sRand", sRand);
					return "loginError";
				} else {
					if (!sRand.subSequence(0, 4).equals(vcode)) {
						errorMessage = "验证码错误";
						request.setAttribute("sRand", sRand);
						return "loginError";
					}
				}
				request.getSession().removeAttribute("sRand");// 清除验证码
			}
			// 登录处理
			errorMessage = userServer
					.login(user.getCode(), password, autoLogin);

			if ("true".equals(errorMessage) && tag != null
					&& tag.equals("fxlogin")) {
				/****************** 记录本次的访问方式 *****************/
				boolean isFromMobile = false;
				// 检查是否已经记录访问方式（移动端或pc端）
				if (null == request.getSession().getAttribute("mobileOrPc")) {
					try {
						// 获取ua，用来判断是否为移动端访问
						String userAgent = ((HttpServletRequest) request)
								.getHeader("USER-AGENT").toLowerCase();
						if (null == userAgent) {
							userAgent = "";
						}
						isFromMobile = Util.check(userAgent);
						// 判断是否为移动端访问
						if (isFromMobile) {
							request.getSession().setAttribute("mobileOrPc",
									"mobile");
						} else {
							request.getSession().setAttribute("mobileOrPc",
									"pc");
						}
					} catch (Exception e) {
					}
				}
				/****************** 记录本次的访问方式结束 *****************/

				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}

				// 正常登录处理
				String host = null;// 上一个页面的地址
				Object uploadLicense = request.getSession().getAttribute(
						"uploadLicense");
				if (uploadLicense == null
						|| !uploadLicense.toString().equals("0")) {
					host = request.getHeader("Referer");// 上一个页面的地址
				} else {
					request.getSession().removeAttribute("uploadLicense");
				}
				Users user = Util.getLoginUser();
				if (host != null && host.length() > 0
						&& host.indexOf("UsersAction!jyLogin2.action") < 0
						&& host.indexOf("UsersAction!jyLogin.action") < 0) {
					try {
						if (host.indexOf("sjLogin.jsp") >= 0) {
							if (tag != null && tag.equals("xjlogin")) {
								response
										.sendRedirect("InsRecord_getToXjList.action");
							} else {
								response
										.sendRedirect("LogoStickerAction!findSjList.action");
							}
							return "sjloginSuccess";
						} else if (host.indexOf("fxLogin.jsp") >= 0) {
							if (user != null && user.getClassrole() != null
									&& user.getClassrole().equals("老师")) {
								response
										.sendRedirect("attendClassAction_initaddLesson.action");
								return "saloginSuccess";
							} else if (user != null
									&& user.getClassrole() != null
									&& user.getClassrole().equals("学生")) {
								response
										.sendRedirect("attendClassAction_lessonattend.action?code="
												+ user.getCode());
								return "xsloginSuccess";
							} else {
								errorMessage = "您不是老师，无法登录!";
							}
						} else {
							response.sendRedirect(host);
						}
					} catch (IOException e) {
						return "saloginSuccess";
					}
				} else if (user != null && user.getClassrole() != null
						&& user.getClassrole().equals("老师")) {
					return "saloginSuccess";
				} else if (user != null && user.getClassrole() != null
						&& user.getClassrole().equals("学生")) {
					return "xsloginSuccess";
				}
			}
		} else {
			errorMessage = "员工号或部门编号或者密码不能为空!";
		}
		return "jyloginError2";
	}

	// 登录（卡号、密码）
	public String otherLogin() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		selfUrl = UsersAction.getSelfUrl(request);
		// boolean b = UsersAction.validateLic(selfUrl);
		// if (b) {
		// errorMessage = UsersAction.validateCount(selfUrl);
		// if (!"通过".equals(errorMessage)) {
		// return "otherLoginError";
		// }
		// } else {
		// errorMessage = "证书没有上传，或者已过期！";
		// request.getSession().setAttribute("uploadLicense", "0");
		// return "uploadlic";
		// }
		if (user != null && user.getCardId().length() > 0
				&& password.getPassword().length() > 0) {
			// 验证码处理
			String sRand = (String) request.getSession().getAttribute("sRand");
			if (sRand != null && sRand.length() > 0) {
				if (vcode.length() > 4) {
					errorMessage = "请输入前四位验证码!";
					request.setAttribute("sRand", sRand);
					return "loginError";
				} else {
					if (!sRand.subSequence(0, 4).equals(vcode)) {
						errorMessage = "验证码错误";
						request.setAttribute("sRand", sRand);
						return "otherLoginError";
					}
				}
				request.getSession().removeAttribute("sRand");// 清除验证码
			}

			// 登录处理
			errorMessage = userServer.login(user, password);
			if ("true".equals(errorMessage)) {
				// 获得网站配置信息
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				if (companyInfo == null) {
					companyInfo = moduleFunctionServer.findCompanyInfo();
					ActionContext.getContext().getApplication().put(
							"companyInfo", companyInfo);
				}
				if ("qx".equals(pageStatus)) {
					return "loginSuccess";
				} else if ("fl".equals(pageStatus)) {
					errorMessage = "ok";
					return "loginOk";
				} else {
					try {
						// response
						// .sendRedirect("ProcardAction!findProcardByCardNum.action?pageStatus=noCardLingGx&cardNumber="
						// + user.getCardId());
						response
								.sendRedirect("/System/SOP/produce/Procard_noCardList.jsp?pageStatus=lingqu");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				if ("fl".equals(pageStatus)) {
					errorMessage = "登录失败！";
					return "loginOk";
				}
			}
		} else {
			errorMessage = "员工号或者密码不能为空!";
		}
		return "otherLoginError";
	}

	/***
	 * 安卓登录测试接口
	 */
	public void loginAndroid() {
		Users loginUser = userServer.findUserByCode(user.getCode());
		if (loginUser != null) {
			Password oldPassword = loginUser.getPassword();// 查询密码
			MD5 md5 = new MD5();
			String mdsPassword = md5.getMD5(password.getPassword().getBytes());// 密码MD5转换
			if (mdsPassword.equals(oldPassword.getPassword())) {
				loginUser.setPassword(null);
				loginUser.setWorklogClass(null);
				loginUser.setModuleFunction(null);
				MKUtil.writeJSON(true, "登陆成功", loginUser);
				return;
			}
		}
		MKUtil.writeJSON(false, "登陆失败", null);
	}

	// 是否在线检测接口
	public void onlineTest() {
		MKUtil.writeJSON(true);
	}

	// 是否在线检测接口
	public void userOnlineTest() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			MKUtil.writeJSON(false);
		} else {
			MKUtil.writeJSON(true);
		}
	}

	// 修改密码
	public String updatePassword() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		if (password1.equals(password2)) {
			Users user = (Users) ActionContext.getContext().getSession().get(
					TotalDao.users);// 获得当前用户的登录信息
			Password oldPassword = user.getPassword();// 查询旧密码
			if (oldPassword != null) {
				MD5 md5 = new MD5();
				String oldPasswordMd5 = md5.getMD5(password.getPassword()
						.getBytes());// 将原密码转换为MD5加密
				if (oldPasswordMd5.equals(oldPassword.getPassword())) {// 判断输入的原密码是否正确
					String mdsPassword = md5.getMD5(password1.getBytes());// 新密码MD5转换
					oldPassword.setPassword(mdsPassword);
					boolean bool = userServer.updatePassword(oldPassword,
							password1);
					if (bool) {
						successMessage = "修改密码成功!";

					} else {
						errorMessage = "修改密码未成功!请重试";
					}
				} else {
					errorMessage = "原密码输入不正确!请重新输入!";
				}
			} else {
				errorMessage = "您的密码信息不存在,请与管理员联系！";
			}
		} else {
			errorMessage = "两次输入的密码不一致,请重新输入";
		}
		return "updatePassword";
	}

	// 修改密码Android 端
	public void updatePassword_1() {
		if (password1 != null && password2 != null && !"".equals(password1)
				&& !"".equals(password2)) {
			if (user.getCode() != null && !"".equals(user.getCode())) {
				Users users = userServer.logins(user.getCode(), password1);// 获得当前用户的登录信息
				if (users != null) {
					Password oldPassword = users.getPassword();// 查询旧密码
					if (oldPassword != null) {
						MD5 md5 = new MD5();
						String password_1 = md5.getMD5(password1.getBytes());
						if (password_1.equals(oldPassword.getPassword())) {// 判断输入的原密码是否正确
							String mdsPassword = md5.getMD5(password2
									.getBytes());// 新密码MD5转换
							oldPassword.setPassword(mdsPassword);
							boolean bool = userServer.updatePassword(
									oldPassword, password2);
							if (bool) {
								MKUtil.writeJSON(true, "密码修改成功!", null);

							} else {
								MKUtil.writeJSON(false, "修改密码未成功!请重试", null);
							}
						} else {
							MKUtil.writeJSON(false, "原密码输入不正确!请重新输入!", null);
						}
					} else {
						MKUtil.writeJSON(false, "您的密码信息不存在,请与管理员联系！", null);
					}
				} else {
					MKUtil.writeJSON(false, "原密码有误！", null);
				}
			} else {
				MKUtil.writeJSON(false, "工号不能为空,请重新输入", null);
			}
		} else {
			MKUtil.writeJSON(false, "输入的密码不能为空,请重新输入", null);
		}
	}

	// 根据工号查询卡号 (人事录入工资模板)
	public String findCardIdBCode() throws IOException {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		user = userServer.findUserByCode(user.getCode().trim());
		WageStandard newWs = new WageStandard();
		newWs.setCode(user.getCode());
		Object[] object = wageStandardServer.findWSByCondition(newWs, 0, 0);// 查询该员工工资是否存在
		String message = "";
		if ((Integer) object[1] > 0) {
			message = "该员工的薪资模板已经存在,无需添加!";
		} else {
			if (user != null) {
				if (user.getPassword() != null) {// 如果存在入职流程
					message = (new StringBuilder(String.valueOf(user
							.getCardId()))).append("|").append(user.getName())
							.append("|").append(user.getDept()).toString();
				} else {
					message = "该用户数据异常,请联系管理员!";
				}
			} else {
				message = "不存在该工号!";
			}
		}
		response.getWriter().write(message);
		response.getWriter().close();
		return null;
	}

	// 根据工号查询人员
	public void findUsersBCode() throws IOException {
		user = userServer.findUserByCode(user.getCode().trim());
		if (user != null) {
			MKUtil.writeJSON(user);
		}
	}

	// 根据工号查询卡号 (主管考核添加人员)
	public String findCardIdBCodeForzgkh() throws IOException {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		if ("1".equals(test)) {
			user = userServer.findUserByCode1(user.getCode().trim());
		} else {
			user = userServer.findUserByCode(user.getCode().trim());
		}
		String message = "";
		if (user != null) {
			if (user.getOnWork() == null || user.getOnWork().length() <= 0
					|| user.getOnWork().equals("离职")) {
				message = "该人员已离职!无法添加";
			} else {
				message = user.getCardId() + "|" + user.getName() + "|"
						+ user.getDept() + "|" + user.getId() + "|"
						+ user.getSex() + "|" + user.getNation();
			}
		} else {
			message = "不存在该工号!";
		}
		response.getWriter().write(message);
		response.getWriter().close();
		return null;
	}

	// 查询所有用户+条件查询+分页
	public String findAllUsers() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		if (user != null) {
			ActionContext.getContext().getSession().put("userManage", user);
		} else {
			user = (Users) ActionContext.getContext().getSession().get(
					"userManage");
		}
		 tryDaysEndList = userServer.findTryDaysEnd();// 试用期到期提醒人员
		 contractEndList = userServer.findContractEnd(); // 合同到期提醒人员

		Object[] object = userServer.findUsersByCondition(user, Integer
				.parseInt(cpage), pageSize, external, null);// 条件查询所有用户
		if (object != null && object.length > 0) {
			userList = (List<Users>) object[0];
			if (userList != null && userList.size() > 0) {
				int sum = (Integer) object[1];
				int pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("UsersAction!findAllUsers.action?tag=" + tag);
				errorMessage = null;
			} else {
				errorMessage = "抱歉!您查询的用户不存在!";
			}
		} else {
			errorMessage = "抱歉!没有您查询的员工信息!";
		}
		return "findAllUsersSuccess";// hr_usersManage.jsp
	}

	// 删除用户
	public String delUser() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			boolean bool = userServer.delUser(user);
			if (bool) {
				successMessage = "已删除员工 :" + user.getName();
				return "delUserSuccess";
			} else {
				errorMessage = "删除员工:" + user.getName() + "失败!";
			}
		} else {
			errorMessage = "不存在该用户!请检查后重试!";
		}
		return ERROR;
	}

	// 上传离职协议单
	public String upload() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		user = userServer.findUserById(id);
		if (user != null) {
			Upload upload = new Upload();// 上传对象
			String fileMessage = upload.UploadFile(picture, pictureFileName,
					null, "/upload/user",
					"D:/WorkSpace/HHTask/WebRoot/upload/user");
			if (!"error".equals(fileMessage)) {
				user.setLeaveAgreement(fileMessage);
				userServer.updateUser(user);
				errorMessage = "上传离职协议单成功";
				return ERROR;
			}
		}
		return ERROR;
	}

	/***
	 * 通过id查询用户(输出json)
	 */
	public void findUserByIdForJson() {
		user = userServer.findUserById(id);
		MKUtil.writeJSON(user);
	}

	// 通过部门查询人员(输出json)
	public String findUsersByDept() {
		try {
			List<Users> list = (List<Users>) userServer
					.findUsersByDept(deptName);
			MKUtil.writeJSON(list);
		} catch (Exception e) {
			MKUtil.writeJSON(e);
		}
		return null;
	}

	// 通过部门查询人员(输出json)
	public String findUsersByDept1() {
		try {
			String keyWord = URLDecoder.decode(deptName, "utf-8");
			List<Users> list = userServer.findUsersByDept(keyWord);
			List<Users> userLsit = new ArrayList<Users>();
			for (Users users : list) {
				users.setPassword(null);
				users.setWorklogClass(null);
				users.setModuleFunction(null);
				userLsit.add(users);
			}
			MKUtil.writeJSON(true, "", userLsit);
		} catch (Exception e) {
			MKUtil.writeJSON(e);
		}
		return null;
	}

	// 单个员工工作记录
	public String findWorkrecords() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		pageSize = 20;
		Object[] object = userServer.findWorkrecords(id, Integer
				.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!findWorkrecords.action?id=" + id);
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "findWorkrecords";
	}

	// 单个员工工作记录
	public String findWorkrecords2() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		// if (markId != null) {
		// ActionContext.getContext().getSession().put("markId", markId);
		// } else {
		// markId = (String) ActionContext.getContext().getSession().get(
		// "markId");
		// }
		pageSize = 20;
		if (processName != null && !"".equals(processName)) {
			int a = processName.indexOf("(");
			int b = processName.indexOf(")");
			processName = processName.substring(a + 1, b);
		}
		Object[] object = userServer.findAllWorkrecords1(id, markId,
				processName, Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!findWorkrecords2.action?id=" + id);
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "findWorkrecords";
	}

	// 查询所有员工工作记录
	public String findAllWorkrecords() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		if (name != null) {
			ActionContext.getContext().getSession().put("name", name);
		} else {
			name = (String) ActionContext.getContext().getSession().get("name");
		}
		if (code != null) {
			ActionContext.getContext().getSession().put("code", code);
		} else {
			code = (String) ActionContext.getContext().getSession().get("code");
		}
		if (gongwei != null) {
			ActionContext.getContext().getSession().put("gongwei", gongwei);
		} else {
			gongwei = (String) ActionContext.getContext().getSession().get(
					"gongwei");
		}
		if (markId != null) {
			ActionContext.getContext().getSession().put("markId", markId);
		} else {
			markId = (String) ActionContext.getContext().getSession().get(
					"markId");
		}
		if (firstApplyDate != null) {
			ActionContext.getContext().getSession().put("firstApplyDate",
					firstApplyDate);
		} else {
			firstApplyDate = (String) ActionContext.getContext().getSession()
					.get("firstApplyDate");
		}
		if (submitDate != null) {
			ActionContext.getContext().getSession().put("submitDate",
					submitDate);
		} else {
			submitDate = (String) ActionContext.getContext().getSession().get(
					"submitDate");
		}
		pageSize = 20;
		Object[] object = userServer.findAllWorkrecords(name, code, gongwei,
				markId, firstApplyDate, submitDate, Integer.parseInt(cpage),
				pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!findAllWorkrecords.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "findAllWorkrecords";
	}

	// 汇总所有的员工工作记录
	public String getProcardAllNames1() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		// //查询所有件号
		// markList = userServer.getProcardAllNames1();
		// //查询所有员工信息
		// usersList = userServer.getProcessInforName();
		// 根据件号和姓名汇总
		if (name != null) {
			ActionContext.getContext().getSession().put("name", name);
		} else {
			name = (String) ActionContext.getContext().getSession().get("name");
		}
		if (code != null) {
			ActionContext.getContext().getSession().put("code", code);
		} else {
			code = (String) ActionContext.getContext().getSession().get("code");
		}
		if (markId != null) {
			ActionContext.getContext().getSession().put("markId", markId);
		} else {
			markId = (String) ActionContext.getContext().getSession().get(
					"markId");
		}
		pageSize = 20;
		Object[] object = userServer.findAllWorkrecords1(name, code, markId,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!getProcardAllNames1.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "hr_findAllWorkrecords";
	}

	// 汇总所有的员工工作记录
	public String getProcardAllNames3() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		// 查询所有件号
		// markList = userServer.getProcardAllNames1();
		// //查询所有员工信息
		// usersList = userServer.getProcessInforName();
		// 根据件号和姓名汇总
		if (name != null) {
			ActionContext.getContext().getSession().put("name", name);
		} else {
			name = (String) ActionContext.getContext().getSession().get("name");
		}
		if (code != null) {
			ActionContext.getContext().getSession().put("code", code);
		} else {
			code = (String) ActionContext.getContext().getSession().get("code");
		}
		pageSize = 20;
		Object[] object = userServer.findAllWorkrecords3(name, code, Integer
				.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!getProcardAllNames3.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "hr_findAllWorkrecords";//hr_findAllWorkrecords1.jsp
	}

	// 查询员工明细
	public String getProcardAllNames2() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		if (markId != null) {
			ActionContext.getContext().getSession().put("markId", markId);
		} else {
			markId = (String) ActionContext.getContext().getSession().get(
					"markId");
		}
		if (firstApplyDate != null) {
			ActionContext.getContext().getSession().put("firstApplyDate",
					firstApplyDate);
		} else {
			firstApplyDate = (String) ActionContext.getContext().getSession()
					.get("firstApplyDate");
		}
		if (submitDate != null) {
			ActionContext.getContext().getSession().put("submitDate",
					submitDate);
		} else {
			submitDate = (String) ActionContext.getContext().getSession().get(
					"submitDate");
		}
		if (code != null) {
			ActionContext.getContext().getSession().put("code", code);
		} else {
			code = (String) ActionContext.getContext().getSession().get("code");
		}
		if (processNO != null) {
			ActionContext.getContext().getSession().put("processNO", processNO);
		} else {
			processNO = (String) ActionContext.getContext().getSession().get(
					"processNO");
		}
		if (tag != null) {
			ActionContext.getContext().getSession().put("tag", tag);
		} else {
			tag = (String) ActionContext.getContext().getSession().get("tag");
		}
		pageSize = 20;
		Object[] object = userServer.findAllWorkrecords1(name, code, gongwei,
				markId, this.processNO, firstApplyDate, submitDate, Integer
						.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("UsersAction!getProcardAllNames2.action");
					// this.setUrl("UsersAction!getProcardAllNames2.action?code='"+code+"'");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		if (tag != null) {
			return "findSelfWorkrecords";
		} else {
			return "findAllWorkrecords";
		}
	}

	/**
	 * 获取本项目的域名
	 * 
	 * @param request
	 * @return
	 */
	public static String getSelfUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String tempContextUrl = url.delete(
				url.length() - request.getRequestURI().length(), url.length())
				.toString();
		if (tempContextUrl.indexOf("http://") >= 0) {
			tempContextUrl = tempContextUrl.replace("http://", "");
		}
		tempContextUrl = tempContextUrl.replace(":", "");
		return tempContextUrl;
	}

	/**
	 * 验证证书
	 * 
	 * @param selfUrl
	 * @return
	 */
	public static boolean validateLic(String selfUrl) {
		// 证书验证
		VerifyLicense vLicense = new VerifyLicense();
		try {
			// 获取参数
			vLicense.setParam("./param.properties", ServletActionContext
					.getServletContext().getRealPath("/")
					+ "upload/file/myliclib/" + selfUrl + ".lic");
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		// 生成证书
		return vLicense.verify();
		// return "licError";
	}

	/**
	 * 验证登陆点数
	 * 
	 * @param selfUrl
	 * @return
	 */
	public static String validateCount(String selfUrl) {
		List sessionsList = SessionListener.getSessions();
		int count = sessionsList.size();
		HttpRequest httpRequest = new HttpRequest();
		Map<String, String> map = new HashMap<String, String>();
		map.put("companyUrl", selfUrl);
		String result = null;
		try {
			HttpResponse httpResponse = httpRequest.sendHttpPost(mainUrl
					+ "/companyInfoAction_getOnlineCount.action", map);
			result = httpResponse.getDataString();
		} catch (IOException e1) {
			// return "服务器连接失败,请稍候重试!";
			return "通过";
		}
		if (result != null) {
			Integer onlineCount = Integer.parseInt(result);
			if (onlineCount <= count) {
				return "对不起！同时在线点数目前已达到上限，您目前不能上线！";
			} else {
				return "通过";
			}
		}
		return "对不起！同时在线点数目前已达到上限，您目前不能上线！";
	}

	// 查询所有件号(模糊匹配)
	public void getProcardAllNames() {
		markList = userServer.getProcardAllNames(markId);
		try {
			MKUtil.writeJSON(markList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 查询所有工位(模糊匹配)
	public void getGongweiAllNames() {
		gongweiList = userServer.getGongweiAllNames(gongwei);
		try {
			MKUtil.writeJSON(gongweiList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 查询所有工序(模糊匹配)根据名字匹配
	public void getprocessNONames() {
		processNOList = userServer.getprocessNONames(id, processName);
		try {
			MKUtil.writeJSON(processNOList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据工号获得已干的工序名
	public void getproNameByCode() {
		processNOList = userServer.getproNameByCode(code);
		try {
			MKUtil.writeJSON(processNOList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 导出EXEC
	public void daochuExec() {
		userServer.exportExcel(user, external);
	}

	/***
	 * 通过名称查询用户
	 * 
	 * @return
	 */
	public String findUserByName() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "loginError";
		}
		try {
			name = URLDecoder.decode(name, "utf-8");
			user = userServer.findUserByName(name);
			if (user != null)
				return "userDettails";
			else
				errorMessage = "不存在您要查询的用户信息,请您核查!谢谢!";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ERROR;
	}

	// 查看个人资料
	public String toupdateZL() {
		Users loginUser = Util.getLoginUser();
		user = loginUser;
		return "updateZl";
	}

	// 修改个人资料
	public String updateZL() {
		if (picture != null) {
			// 文件路径
			String realFileName = Util.getDateTime("yyyyMMddHHmmss");
			// 打开存放文件的位置
			String realFilePath = "upload/user";
			String path = ServletActionContext.getServletContext().getRealPath(
					realFilePath);
			String path1 = ServletActionContext.getServletContext()
					.getRealPath("");
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();// 如果不存在文件夹就新建
			}
			Upload upload = new Upload();// 文件上传工具类
			// 删除原文件
			Users user1 = userServer.findUserById(user.getId());
			if (user1 != null && user1.getPassword().getPicture() != null) {
				File oldFile = new File(path1 + "/"
						+ user1.getPassword().getPicture());
				if (oldFile.exists()) {
					oldFile.delete();
				}
			}
			realFileName = upload.UploadFile(picture, pictureFileName, null,
					realFilePath, null);
			user.getPassword().setPicture(realFileName);// 文件新名称
		}
		boolean bool = this.userServer.grupdateZL(user);
		if (bool) {
			user = userServer.findUserById(user.getId());
			ActionContext.getContext().getSession().put("Users", user);
			this.errorMessage = "修改成功!";
		} else {
			this.errorMessage = "修改失败!";
		}
		url = "UsersAction!updateZL.action";
		return "error";
	}

	// 删除学历证书;

	public String updateResume() throws UnsupportedEncodingException {
		user = userServer.findUserById(id);
		pictureFileName = URLEncoder.encode(URLEncoder.encode(pictureFileName,
				"utf-8"), "utf-8");
		errorMessage = userServer.updateResume(pictureFileName, user);
		if ("true".equals(errorMessage)) {
			errorMessage = "删除学历证书成功";
		}
		return "addUserSuccess";
	}

	// 产线小组生产卡绑定员工查询页
	public String findUsersAll() {
		if (user != null) {
			ActionContext.getContext().getSession().put("user", user);
		} else {
			user = (Users) ActionContext.getContext().getSession().get("user");
		}
		Object[] obj = userServer.findAllUsers(id, user, Integer
				.parseInt(cpage), pageSize, pageStatus);
		usersList = (List<Users>) obj[0];
		int count = (Integer) obj[1];
		userscard = (UsersCard) obj[2];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("UsersAction!findUsersAll.action?pageStatus=" + pageStatus
				+ "&id=" + id);
		return "UsersCard_user";
	}

	// 绑定或者解绑
	public String UsersCardbangUser() {
		errorMessage = userServer.UsersCardbangUser(userscard, arrayId,
				pageStatus);
		if ("true".equals(errorMessage)) {
			errorMessage = "成功";
			setUrl("UsersAction!findUsersAll.action?pageStatus=" + pageStatus
					+ "&id=" + userscard.getId());
		}
		return "error";
	}

	// 查询所有产线小组卡
	public String findAllUsersCard() {
		if (userscard != null) {
			ActionContext.getContext().getSession().put("userscard", userscard);
		} else {
			userscard = (UsersCard) ActionContext.getContext().getSession()
					.get("userscard");
		}
		Object[] obj = userServer.findAllUsersCard(userscard, Integer
				.parseInt(cpage), pageSize, pageStatus);
		usercardList = (List<UsersCard>) obj[0];
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("UsersAction!findAllUsersCard.action");
		return "UsersCard_show";
	}

	// 根据卡号查询产线小组卡
	public String findUsersCardByCardId() {
		userscard = userServer.findUsersCardByCardId(cardNumber);
		if (userscard != null) {
			id = userscard.getId();
			return "UsersCard_card1";
		} else {
			errorMessage = "未找到该小组卡！";
		}
		return "UsersCard_card";
	}

	public String BangCard() {
		boolean bool = userServer.BangCard(id, cardNumber);
		if (bool) {
			errorMessage = "刷卡成功!";
		} else {
			errorMessage = "刷卡失败!";
		}
		return "UsersCard_card1";
	}

	public String delUsersCard() {
		errorMessage = userServer.delUsersCard(id);
		if ("true".equals(errorMessage)) {
			errorMessage = "删除成功!";
		}
		setUrl("UsersAction!findAllUsersCard.action");
		return "error";
	}

	public void updateUsersCard() {
		try {
			boolean bool = userServer.updateUsersCard(userscard);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}

	}

	// 获取验证码
	public void send() {
		pageStatus = userServer.loginByPhone(cardNumber, id);
		if (pageStatus != null) {
			Random ran = new Random();
			String number = "" + ran.nextInt(10) + ran.nextInt(10)
					+ ran.nextInt(10) + ran.nextInt(10) + ran.nextInt(10)
					+ ran.nextInt(10);
			String xuhao = "" + ran.nextInt(10) + ran.nextInt(10)
					+ ran.nextInt(10) + ran.nextInt(10) + ran.nextInt(10)
					+ ran.nextInt(10);
			HttpServletResponse response = ServletActionContext.getResponse();
			Cookie numberCookie = new Cookie(xuhao, number);
			numberCookie.setMaxAge(60 * 10);// 存入cookie一分钟
			response.addCookie(numberCookie);
			smsService.send(cardNumber, "序列号:" + xuhao + "  验证码为：" + number
					+ "(登录验证依据，切勿告知他人),请在文本框中输入已完成验证，有问题请致电021-59567057【PEBS】");
			MKUtil.writeJSON(true, "", xuhao);
		} else {
			MKUtil.writeJSON(false, "未查询到您的手机号,请确认后再登录!", null);
		}
	}

	/***
	 * 送货码登录
	 * 
	 * @param phone
	 * @return
	 */
	public String loginByPhone() {
		pageStatus = userServer.loginByPhone(cardNumber, id);
		if (pageStatus != null) {
			if (id == 0 && "nb".equals(pageStatus)) {
				pageStatus = "qx";
				return "loginSuccess";
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			Users newUser = new Users();
			newUser.setId(99999999);
			// 记入session监听器
			HttpSession session = request.getSession();
			session.setAttribute(TotalDao.users, newUser);
			// // 保存用户到session中
			ActionContext.getContext().getSession()
					.put(TotalDao.users, newUser);

			return "phonelogin";
		} else {
			errorMessage = "未查询到您的手机号,请确认后再登录!";
		}
		return ERROR;
	}

	// 导入离职
	public void daoruLizhi() {
		try {
			userServer.daoruLizhi(picture);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// ajxa每五分钟访问一次更新在线时长
	public void updateOnlineLong() {
		try {
			boolean bool = userServer.updateOnlineLong(id);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取人员整体信息
	 */
	public void getAllName() {
		userList = userServer.getgetAllName(name);
		MKUtil.writeJSON(userList);
	}
	
	public String addRank() {
		
		try {
			boolean addRank = userServer.addRank(rank);
			if(addRank) {
				errorMessage = "添加成功";
			}
		} catch (Exception e) {
			errorMessage = e.toString();
		}
		setUrl("UsersAction!findRank.action");
		return ERROR;
	}
	
	public String findRank() {
		rankList = userServer.findRankByCond(rank, pageStatus);
		if(pageStatus!=null && pageStatus.equals("ajax")) {
			MKUtil.writeJSON(rankList);
		}
		setUrl("UsersAction!findRank.action");
		return "findRank";
	}
	
	public void deleteRank() {
		boolean deleteRank = userServer.deleteRank(id);
		MKUtil.writeJSON(deleteRank);
	}

	public UserServer getUserServer() {
		return userServer;
	}

	public void setUserServer(UserServer userServer) {
		this.userServer = userServer;
	}

	public InterviewLog getInterviewLog() {
		return interviewLog;
	}

	public void setInterviewLog(InterviewLog interviewLog) {
		this.interviewLog = interviewLog;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Password getPassword() {
		return password;
	}

	public void setPassword(Password password) {
		this.password = password;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(String autoLogin) {
		this.autoLogin = autoLogin;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
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

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public File getPicture() {
		return picture;
	}

	public void setPicture(File picture) {
		this.picture = picture;
	}

	public String getPictureContentType() {
		return pictureContentType;
	}

	public void setPictureContentType(String pictureContentType) {
		this.pictureContentType = pictureContentType;
	}

	public String getPictureFileName() {
		return pictureFileName;
	}

	public void setPictureFileName(String pictureFileName) {
		this.pictureFileName = pictureFileName;
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

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> userList) {
		this.userList = userList;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Provision> getProvisionList() {
		return provisionList;
	}

	public void setProvisionList(List<Provision> provisionList) {
		this.provisionList = provisionList;
	}

	public List<Users> getTryDaysEndList() {
		return tryDaysEndList;
	}

	public void setTryDaysEndList(List<Users> tryDaysEndList) {
		this.tryDaysEndList = tryDaysEndList;
	}

	public List<Users> getContractEndList() {
		return contractEndList;
	}

	public void setContractEndList(List<Users> contractEndList) {
		this.contractEndList = contractEndList;
	}

	public WageStandardServer getWageStandardServer() {
		return wageStandardServer;
	}

	public void setWageStandardServer(WageStandardServer wageStandardServer) {
		this.wageStandardServer = wageStandardServer;
	}

	public ContractServer getContractServer() {
		return contractServer;
	}

	public void setContractServer(ContractServer contractServer) {
		this.contractServer = contractServer;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public ModuleFunctionServer getModuleFunctionServer() {
		return moduleFunctionServer;
	}

	public void setModuleFunctionServer(
			ModuleFunctionServer moduleFunctionServer) {
		this.moduleFunctionServer = moduleFunctionServer;
	}

	public StrutsTreeNode getUserRoot() {
		return userRoot;
	}

	public void setUserRoot(StrutsTreeNode userRoot) {
		this.userRoot = userRoot;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getGongwei() {
		return gongwei;
	}

	public void setGongwei(String gongwei) {
		this.gongwei = gongwei;
	}

	public List getMarkList() {
		return markList;
	}

	public void setMarkList(List markList) {
		this.markList = markList;
	}

	public List getGongweiList() {
		return gongweiList;
	}

	public void setGongweiList(List gongweiList) {
		this.gongweiList = gongweiList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFirstApplyDate() {
		return firstApplyDate;
	}

	public void setFirstApplyDate(String firstApplyDate) {
		this.firstApplyDate = firstApplyDate;
	}

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}

	public List getUsersList() {
		return usersList;
	}

	public void setUsersList(List usersList) {
		this.usersList = usersList;
	}

	public String getProcessNO() {
		return processNO;
	}

	public void setProcessNO(String processNO) {
		this.processNO = processNO;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List getProcessNOList() {
		return processNOList;
	}

	public void setProcessNOList(List processNOList) {
		this.processNOList = processNOList;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getSelfUrl() {
		return selfUrl;
	}

	@SuppressWarnings("static-access")
	public void setSelfUrl(String selfUrl) {
		this.selfUrl = selfUrl;
	}

	public static String getMainurl() {
		return mainUrl;
	}

	public String getExternal() {
		return external;
	}

	public void setExternal(String external) {
		this.external = external;
	}

	public static String getLoginIp() {
		return loginIp;
	}

	public static void setLoginIp(String loginIp) {
		UsersAction.loginIp = loginIp;
	}

	public Users getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(Users loginUser) {
		this.loginUser = loginUser;
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public String getDeptNumber() {
		return deptNumber;
	}

	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}

	public List<BanCi> getBanciList() {
		return banciList;
	}

	public void setBanciList(List<BanCi> banciList) {
		this.banciList = banciList;
	}

	public List<ModuleFunction> getModuleFunctionList() {
		return moduleFunctionList;
	}

	public void setModuleFunctionList(List<ModuleFunction> moduleFunctionList) {
		this.moduleFunctionList = moduleFunctionList;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer[] getArrayId() {
		return arrayId;
	}

	public void setArrayId(Integer[] arrayId) {
		this.arrayId = arrayId;
	}

	public UsersCard getUserscard() {
		return userscard;
	}

	public void setUserscard(UsersCard userscard) {
		this.userscard = userscard;
	}

	public List<UsersCard> getUsercardList() {
		return usercardList;
	}

	public void setUsercardList(List<UsersCard> usercardList) {
		this.usercardList = usercardList;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public IBorrowService getBorrowService() {
		return borrowService;
	}

	public void setBorrowService(IBorrowService borrowService) {
		this.borrowService = borrowService;
	}

	public List getBorrowlist() {
		return borrowlist;
	}

	public void setBorrowlist(List borrowlist) {
		this.borrowlist = borrowlist;
	}

	public IAlsoService getAlsoService() {
		return alsoService;
	}

	public void setAlsoService(IAlsoService alsoService) {
		this.alsoService = alsoService;
	}

	public List getAlsolist() {
		return alsolist;
	}

	public void setAlsolist(List alsolist) {
		this.alsolist = alsolist;
	}

	public List getBaoxiaolist() {
		return baoxiaolist;
	}

	public void setBaoxiaolist(List baoxiaolist) {
		this.baoxiaolist = baoxiaolist;
	}

	public BaoXiaoDanServer getBaoXiaoDanServer() {
		return baoXiaoDanServer;
	}

	public void setBaoXiaoDanServer(BaoXiaoDanServer baoXiaoDanServer) {
		this.baoXiaoDanServer = baoXiaoDanServer;
	}

	public void setWorkrecords(ArrayList<String> workrecords) {
		this.workrecords = workrecords;
	}

	public List<String> getWorkrecords() {
		return workrecords;
	}

	public void setWorkrecords(List<String> workrecords) {
		this.workrecords = workrecords;
	}

	public List<Map> getPaymentmaps() {
		return paymentmaps;
	}

	public void setPaymentmaps(List<Map> paymentmaps) {
		this.paymentmaps = paymentmaps;
	}

	public PaymentDetailServer getPaymentDetailServer() {
		return paymentDetailServer;
	}

	public void setPaymentDetailServer(PaymentDetailServer paymentDetailServer) {
		this.paymentDetailServer = paymentDetailServer;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public List<Rank> getRankList() {
		return rankList;
	}

	public void setRankList(List<Rank> rankList) {
		this.rankList = rankList;
	}

}