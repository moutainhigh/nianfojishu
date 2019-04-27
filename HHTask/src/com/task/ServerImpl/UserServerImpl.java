package com.task.ServerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.struts2.ServletActionContext;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.sun.jna.platform.win32.Netapi32Util.User;
import com.task.Dao.TotalDao;
import com.task.Server.UserServer;
import com.task.ServerImpl.ess.ProcardBlServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Borrow;
import com.task.entity.Careertrack;
import com.task.entity.Consuming;
import com.task.entity.DeptNumber;
import com.task.entity.OrderManager;
import com.task.entity.PassReal;
import com.task.entity.Password;
import com.task.entity.Price;
import com.task.entity.UserFacialInfor;
import com.task.entity.Users;
import com.task.entity.UsersCard;
import com.task.entity.UsersLoginLog;
import com.task.entity.banci.BanCi;
import com.task.entity.renshi.InterviewLog;
import com.task.entity.renshi.Rank;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.UserDept;
import com.task.util.AESEnctypeUtil;
import com.task.util.DateUtil;
import com.task.util.MD5;
import com.task.util.Upload;
import com.task.util.Util;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.ZhUser;

@SuppressWarnings( { "unchecked", "deprecation", "unused" })
public class UserServerImpl implements UserServer {
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public boolean login(String usercode, String password) {
		Users u = (Users) totalDao.getObjectByCondition(
				"from Users where code = ?", usercode);
		if (u != null) {
			String ps = u.getPassword().getPassword();
			MD5 md5 = new MD5();
			String mdsPassword = md5.getMD5(password.getBytes());// 密码MD5转换
			if (mdsPassword.equalsIgnoreCase(ps)) {
				return true;
			}
		}
		return false;

	}

	// 添加员工
	public String addUser(Users user, File picture, String pictureFileName,
			InterviewLog interviewLog) {
		if (user != null) {
			Users oldUser = findUserByCode(user.getCode());
			if (oldUser != null) {
				return "工号:" + user.getCode() + "已经存在,请重新输入!";
			}
			oldUser = findUserByCardId(user.getCardId());
			if (oldUser != null) {
				return "卡号:" + user.getCardId() + "已经存在,请重新输入!";
			}
			String dept = user.getDept();
			String[] deptarray = dept.split("_");
			String hql = "from DeptNumber where dept =?";
			DeptNumber deptNumber = null;// 查询部门编号
			if (deptarray != null && deptarray.length == 2) {
				DeptNumber deptNumber1 = (DeptNumber) totalDao
						.getObjectByCondition(hql, deptarray[0]);
				List<DeptNumber> deptList = totalDao.query(hql, deptarray[1]);
				if (deptNumber1 != null) {
					if (deptList != null && deptList.size() > 0) {
						boolean bool = true;
						for (DeptNumber d : deptList) {
							if (d.getFatherId() == deptNumber1.getId()) {
								bool = false;
								deptNumber = d;
								break;
							}
						}
						if (bool) {
							deptNumber = deptNumber1;
						}
					} else {
						deptNumber = deptNumber1;
					}
				} else {
					return "一级部门不存在。";
				}
			} else if (!dept.contains("_")) {
				deptNumber = (DeptNumber) totalDao.getObjectByCondition(hql,
						user.getDept());
			}

			if (deptNumber != null) {
				// 上传
				user.setDept(deptNumber.getDept());
				if (picture != null && pictureFileName != null) {
					pictureFileName = Util.getDateTime("yyyyMMddHHmmss")
							+ user.getCode()
							+ pictureFileName
									.substring(
											pictureFileName.lastIndexOf("."),
											pictureFileName.length());
					Upload upload = new Upload();
					String fileMessage = upload.UploadFile(picture, null,
							pictureFileName, "/upload/user",
							"D:/WorkSpace/HHTask/WebRoot/upload/user");
					// if (!"true".equals(fileMessage)) {
					// return fileMessage;
					// }
				}

				// 为属性赋值
				user.setDeptId(deptNumber.getId());// 部门Id
				user.getPassword().setDeptNumber(deptNumber.getDeptNumber());// 部门编号
				String uid = "";
				if (user.getUid() != null && user.getUid().length() >= 6) {
					MD5 md5 = new MD5();
					uid = user.getUid().substring(user.getUid().length() - 6,
							user.getUid().length());
					String mdsPassword = md5.getMD5(uid.getBytes());// 身份证转换为MD5加密
					user.getPassword().setPassword(mdsPassword);
				} else {
					user.getPassword().setPassword(
							"e10adc3949ba59abbe56e057f20f883e");// 密码
				}
				user.getPassword().setPicture(pictureFileName);// 照片
				user.getPassword().setUser(user);// 用户
				user.getPassword().setUserStatus("上传简历");
				boolean bool = false;
				if (user.getId() != null) {
					bool = totalDao.update(user);
					if (bool) {
						return "true";
					}
				} else {
					bool = totalDao.save(user);
				}
				if (bool) {
					if (interviewLog != null && interviewLog.getId() != null) {
						interviewLog = (InterviewLog) totalDao.getObjectById(
								InterviewLog.class, interviewLog.getId());
						interviewLog.setInter_status("已入职");
					}
					UserFacialInforServerImpl.addUserFacialInfor(user);// 添加面部识别考勤对接表
					if(ProcardBlServerImpl.SystemShezhi("PEBS考勤机")){
						UserFacialInforServerImpl.addUserCode(user);// 添加浩顺考勤对接表
					}
					// 更新职业轨迹
					if (null != user.getUid() && !"".equals(user.getUid())) {
						String hgl_ck = "from Careertrack where cardId = ?";
						Careertrack ck = (Careertrack) totalDao
								.getObjectByCondition(hgl_ck, user.getUid());
						if (ck != null) {
							ck.setRuzhiTime(Util.getDateTime());
							ck.setDept(user.getDept());
							ck.setJob(user.getDuty());
							ck.setNation(user.getNation());
							ck.setBirthplace(user.getBirthplace());
							ck.setStatus(user.getOnWork());
							ck.setUserId(user.getId());
							ck.setCode(user.getCode());
							totalDao.update(ck);
						} else {
							String time = null;
							if (user.getBothday() != null) {
								time = Util.DateToString(user.getBothday(),
										"yyyy-MM-dd");
							}
							String str = "";
							if (interviewLog != null
									&& interviewLog.getInterviewAddTime() != null) {
								str = interviewLog.getInterviewAddTime();
							}
							ck = new Careertrack(user.getName(),
									user.getDept(), user.getEducation(), user
											.getUid(), user.getNation(), user
											.getBirthplace(), time, user
											.getPassword().getPhoneNumber(),
									str, user.getDuty(), "试用", user.getId(),
									user.getCode());
							ck.setRuzhiTime(Util.getDateTime());
							ck.setStatus(user.getOnWork());
							totalDao.save(ck);
						}
					}

					PassReal passReal = new PassReal();
					passReal.setRealPass(uid == "" ? "123456" : uid);// 如果身份证号为空，则初始密码为"123456"
					passReal.setUid(user.getId());
					totalDao.save(passReal);
					return "true";
				}
				return "添加员工: " + user.getName() + "失败!请检查后重试!";
			}
			return "您所选的部门不存在,请检查后重试!";
		}
		return "数据异常!请检查后重试!";
	}

	// 上传照片或者简历
	public boolean uploadResume(Users user, File file, String fileFileName,
			String pictureOrResume) {
		// 上传照片
		if (file != null && fileFileName.length() > 0
				&& pictureOrResume != null && pictureOrResume.length() > 0) {
			// 删除文件
			String fileRealPath = "D:/WorkSpace/HHTask/WebRoot/upload/user/";
			String fileRealPath2 = ServletActionContext.getServletContext()
					.getRealPath("/upload/user")
					+ "/";
			String resume = "";
			String[] arraystr = null;
			if ("picture".equals(pictureOrResume)) {
				fileRealPath += user.getPassword().getPicture();
				fileRealPath2 += user.getPassword().getPicture();
			} else if ("resume".equals(pictureOrResume)) {
				// fileRealPath += user.getPassword().getResume();
				// fileRealPath2 += user.getPassword().getResume();
				resume = user.getPassword().getResume();
				if (resume != null && !"".equals(resume)) {
					arraystr = resume.split("#");
				}
			} else if ("contract".equals(pictureOrResume)) {
				fileRealPath += user.getPassword().getContract();
				fileRealPath2 += user.getPassword().getContract();
			}
			if (!"resume".equals(pictureOrResume)) {
				File oldFile = new File(fileRealPath);
				File oldFile2 = new File(fileRealPath2);
				if (!oldFile.isDirectory()) {
					oldFile.delete();
					if (!oldFile2.isDirectory()) {
						oldFile2.delete();
					}
				}
			}
			// 上传新文件
			fileFileName = Util.getDateTime("yyyyMMddHHmmss")
					+ user.getCode()
					+ fileFileName.substring(fileFileName.lastIndexOf("."),
							fileFileName.length());// 文件名称=工号+格式
			Upload upload = new Upload();// 上传对象
			String fileMessage = upload.UploadFile(file, null, fileFileName,
					"/upload/user", "D:/WorkSpace/HHTask/WebRoot/upload/user");
			if ("error".equals(fileMessage)) {
				return false;
			}

			if ("picture".equals(pictureOrResume)) {
				user.getPassword().setPicture(fileFileName);
			} else if ("resume".equals(pictureOrResume)) {
				if ("上传简历".equals(user.getPassword().getUserStatus())) {
					if ("劳务".equals(user.getPassword().getStaffNature())) {// 如果是劳务员则跳过签订合同
						user.getPassword().setUserStatus("薪资处理");
					} else {
						user.getPassword().setUserStatus("签订合同");
					}
				}
				resume = resume + "#" + fileFileName;

				user.getPassword().setResume(resume);
			} else if ("contract".equals(pictureOrResume)) {
				user.getPassword().setContract(fileFileName);
				user.getPassword().setUserStatus("薪资处理");
			}
			return totalDao.update(user);
		}
		return false;
	}

	// 删除简历
	public String updateResume(String fileFileName, Users user) {
		if (user != null && fileFileName != null && fileFileName.length() > 0) {
			// 删除文件
			String fileRealPath = "D:/WorkSpace/HHTask/WebRoot/upload/user/";
			String fileRealPath2 = ServletActionContext.getServletContext()
					.getRealPath("/upload/user")
					+ "/";
			String resume = "";
			String[] arraystr = null;
			resume = user.getPassword().getResume();
			arraystr = resume.split("#");
			List<String> strlist = new ArrayList<String>();
			for (String str : arraystr) {
				strlist.add(str);
			}
			boolean bool = false;
			if (strlist.contains(fileFileName)) {
				fileRealPath += fileFileName;
				fileRealPath2 += fileFileName;
				File oldFile = new File(fileRealPath);
				File oldFile2 = new File(fileRealPath2);
				if (!oldFile.isDirectory()) {
					oldFile.delete();
					if (!oldFile2.isDirectory()) {
						oldFile2.delete();
						bool = true;
					}
				}
			}
			if (bool) {
				String resume1 = "";
				strlist.remove(fileFileName);
				for (int i = 0; i < strlist.size(); i++) {
					if (i == 0) {
						resume1 += strlist.get(i);
					} else {
						resume1 += "#" + strlist.get(i);
					}
				}
				user.getPassword().setResume(resume1);
				return totalDao.update(user) + "";
			}

		}
		return "删除失败";
	}

	// 登录(检查工号、部门编码、密码)
	public String login(Users user, Password password, String autoLogin) {
		// //处理密码表和用户表的关系
		// String hq1 =
		// "from Users where id not in (select u.id from Users u join u.password)";
		// List list = totalDao.createQuerySelect(hq1, null);
		// for (int i = 0; i < list.size(); i++) {
		// Users newUser = (Users) list.get(i);
		// Password newPass = new Password();
		// newPass.setUser(newUser);
		// newPass.setPassword("e10adc3949ba59abbe56e057f20f883e");
		// newPass.setUserStatus("签订合同");
		// newPass.setPicture(newUser.getCode() + ".jpg");
		// boolean bool = totalDao.save(newPass);
		// if (bool) {
		// PassReal passReal = new PassReal();
		// passReal.setRealPass("123456");
		// passReal.setUid(newUser.getId());
		// totalDao.save(passReal);
		// }
		// }

		/*
		 * // 合同检查处理 String excelRealPath = "D:/hetong"; File folder = new
		 * File(excelRealPath); String filesName[] = folder.list(); List
		 * usersList = new ArrayList(); boolean bool = true; for (int i = 0; i <
		 * filesName.length; i++) { File file = new File(excelRealPath + "\\" +
		 * filesName[i]); if (!file.isDirectory()) { String fileName =
		 * file.getName(); // String stringcode = fileName // .substring(0,
		 * fileName.indexOf("+")); String stringcode = fileName.substring(0,
		 * fileName .lastIndexOf(".")); Users newUser =
		 * findUserByCode(stringcode); if (newUser == null) {
		 * usersList.add(fileName); } else {
		 *//***
		 * 检查已上传合同和数据是否一致
		 */
		/*
		 * if (newUser.getPassword() != null) { String contract =
		 * newUser.getPassword().getContract(); if (contract != null &&
		 * contract.length() > 0) { stringcode += ".pdf"; if
		 * (!stringcode.equals(contract)) { usersList.add(fileName); } } }
		 *//***
		 * 上传合同处理
		 **** 
		 * String userName = fileName.substring( fileName.indexOf("+") + 1,
		 * fileName .lastIndexOf(".")); bool = file.renameTo(new
		 * File(file.getParent() + "\\" + stringcode + ".pdf")); if
		 * (!newUser.getName().equals(userName)) { usersList.add(fileName); }
		 * 
		 * newUser.getPassword().setContract(stringcode + ".pdf"); // String hql
		 * = // "from WageStandard where code=? and standardStatus='默认'"; //
		 * WageStandard wageStandard = (WageStandard) totalDao //
		 * .getObject(hql, newUser.getCode()); // if (wageStandard != null) { //
		 * newUser.getPassword().setUserStatus("完成"); // } else { //
		 * newUser.getPassword().setUserStatus("薪资处理"); // } //
		 * totalDao.update(newUser);
		 */
		/*
		 * } } }
		 */
		if (user != null && password != null) {
			String hql = "from Users where code=?";
			String code_un = "";
			try {
				code_un = URLDecoder.decode(user.getCode(), "UTF-8");
			} catch (UnsupportedEncodingException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			user = (Users) totalDao.getObjectByCondition(hql, code_un);// 查询工号是否存在

			if (user != null) {
				if (user.getOnWork() == null || user.getOnWork().length() <= 0
						|| user.getOnWork().equals("离职")) {
					return "对不起,您已离职,无法登录系统!";
				}

				Password oldPassword = user.getPassword();// 查询部门编号是否存在
				// String deptnumber_un = "";
				// try {
				// deptnumber_un = URLDecoder.decode(password.getDeptNumber(),
				// "UTF-8");
				// } catch (UnsupportedEncodingException e2) {
				// // TODO Auto-generated catch block
				// e2.printStackTrace();
				// }
				// if (oldPassword != null && oldPassword.getDeptNumber() !=
				// null
				// && oldPassword.getDeptNumber().length() > 0
				// && deptnumber_un.equals(oldPassword.getDeptNumber()))
				if (oldPassword != null) {
					String password_un = "";
					try {
						password_un = URLDecoder.decode(password.getPassword(),
								"UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					MD5 md5 = new MD5();
					String mdsPassword = md5.getMD5(password_un.getBytes());// 密码MD5转换
					HttpServletResponse response = ServletActionContext
							.getResponse();
					HttpServletRequest request = ServletActionContext
							.getRequest();
					if (mdsPassword.equals(oldPassword.getPassword())) {
						// 记入session监听器
						HttpSession session = request.getSession();
						session.setAttribute(TotalDao.users, user);

						// // 保存用户到session中
						ActionContext.getContext().getSession().put(
								TotalDao.users, user);

						// 如果登录成功,清空验证码错误记录
						Cookie vcodeCookie = new Cookie("vcode", "");
						vcodeCookie.setMaxAge(0);
						response.addCookie(vcodeCookie);
//						user.setUserStatus("在岗");
//						if(user.getFristTime()==null||!Util.getDateTime("yyyy-MM-dd").equals(user.getFristTime().substring(0, 10))){
//							user.setFristTime(Util.getDateTime());
//						}
						try {
							// 是否需要自动登录 保存到Cookie中
							if (autoLogin != null && "yes".equals(autoLogin)) {
								String code = "";// 员工号
								String deptNum = "";// 部门编号
								String userPsd = "";// 密码
								// 获得所有的cookie,用于判断是否需要为cookie赋值
								Cookie[] cookies = request.getCookies();
								if (cookies != null) {
									for (int i = 0; i < cookies.length; i++) {
										Cookie cookie = cookies[i];
										if ("code".equals(cookie.getName())) {
											code = cookie.getValue();
										} else if ("deptNum".equals(cookie
												.getName())) {
											deptNum = cookie.getValue();
										} else if ("password".equals(cookie
												.getName())) {
											userPsd = cookie.getValue();
										}
									}
								}
								if (code == null || code.length() <= 0
										|| deptNum == null
										|| deptNum.length() <= 0
										|| userPsd == null
										|| userPsd.length() <= 0
										|| !code.equals(user.getCode())) {
									code = URLEncoder.encode(user.getCode(),
											"utf-8");
//									deptNum = URLEncoder.encode(oldPassword
//											.getDeptNumber(), "utf-8");
									userPsd = URLEncoder.encode(password
											.getPassword(), "utf-8");
									// 设置生存时间(一周)
									Cookie codeCookie = new Cookie("code", code);
									codeCookie.setMaxAge(60 * 60 * 24 * 7);
									Cookie deptNumCookie = new Cookie(
											"deptNum", deptNum);
									deptNumCookie.setMaxAge(60 * 60 * 24 * 7);
									Cookie passwordCookie = new Cookie(
											"password", userPsd);
									passwordCookie.setMaxAge(60 * 60 * 24 * 7);
									// 保存cookie
									response.addCookie(codeCookie);
									response.addCookie(deptNumCookie);
									response.addCookie(passwordCookie);
								}

							} else {
								Cookie codeCookie = new Cookie("code", "");
								Cookie deptNumCookie = new Cookie("deptNum", "");
								Cookie passwordCookie = new Cookie("password",
										"");
								codeCookie.setMaxAge(0);// 为0 ，立即删除
								deptNumCookie.setMaxAge(0);// 为0 ，立即删除
								passwordCookie.setMaxAge(0);// 为0 ，立即删除
								response.addCookie(codeCookie);
								response.addCookie(deptNumCookie);
								response.addCookie(passwordCookie);
							}
							Cookie loginTypeCookie = null;
							if (user.getClassrole() != null
									&& user.getClassrole().equals("老师")) {
								loginTypeCookie = new Cookie("loginType",
										"fxlogin.jsp");
							} else {
								// 登录方式
								loginTypeCookie = new Cookie("loginType",
										"login.jsp");
							}
							loginTypeCookie.setMaxAge(60 * 60 * 24 * 7);
							response.addCookie(loginTypeCookie);
						} catch (UnsupportedEncodingException e) {
							return "自动登录功能出现故障,请检查后重试!";
						}
						// /***
						// * 登录用户人数记录处理
						// */
						// ServletContext application = request.getSession()
						// .getServletContext();
						// Map<Integer, Users> loginUserMap = new
						// HashMap<Integer, Users>();
						// Integer loginUsers = 1;
						// // 获得登录用户列表
						// Object loginUserObj = application
						// .getAttribute("loginUserMap");
						// if (loginUserObj != null) {
						// loginUserMap = (Map<Integer, Users>) loginUserObj;
						// // 判断当前登录用户是否存在
						// Object userIdObj = loginUserMap.get(user.getId());
						// if (userIdObj == null) {
						// loginUserMap.put(user.getId(), user);// 添加登录用户
						// // 累加登录用户人数
						// loginUsers = loginUserMap.size() + 1;
						// } else {
						// loginUsers = loginUserMap.size();
						// }
						// } else {
						// loginUserMap.put(user.getId(), user);
						// }
						// application.setAttribute("loginUserMap",
						// loginUserMap);
						// application.setAttribute("loginUsersCount",
						// loginUsers);
						// /***
						// * 登录用户人数处理结束
						// */

						// 获得真实ip
						String ip = request.getHeader("x-forwarded-for");
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getHeader("Proxy-Client-IP");
						}
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getHeader("WL-Proxy-Client-IP");
						}
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getRemoteAddr();
						}
						user.setLoginIp(ip);
						//登录成功时记录一次登录次数和登录日志wxf;
						try {
//							addUsersLoginLog(user);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return "true";
					} else {
						try {
							// 由于密码输入错误,清空密码的cookie
							Cookie passwordCookie = new Cookie("password", "");
							passwordCookie.setMaxAge(0);
							response.addCookie(passwordCookie);
							// 添加验证cookie,用于页面显示验证码
							String vcode = URLEncoder.encode("passwordError",
									"utf-8");
							Cookie vcodeCookie = new Cookie("vcode", vcode);
							vcodeCookie.setMaxAge(60 * 60 * 24);// 生存一天
							response.addCookie(vcodeCookie);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						return "密码错误! ";
					}
				} else {
					return "该部门编号错误! ";
				}
			} else {
				return "不存在该员工号! ";
			}

		}
		return "请输入帐号信息!";
	}

	/****
	 * 检验登录(工号和密码登录)
	 * 
	 * @param code
	 *            工号
	 * @param password
	 *            密码
	 * @param autoLogin
	 *            自动登录
	 * @return
	 */
	@Override
	public String login(String code, Password password, String autoLogin) {
		if (code != null && password != null) {
			String hql = "from Users where code=?";
			String code_un = "";
			try {
				code_un = URLDecoder.decode(code, "UTF-8");
			} catch (UnsupportedEncodingException e3) {
				e3.printStackTrace();
			}
			Users user = (Users) totalDao.getObjectByCondition(hql, code_un);// 查询工号是否存在
			if (user != null) {
				if (user.getOnWork() == null || user.getOnWork().length() <= 0
						|| user.getOnWork().equals("离职")) {
					return "对不起,您已离职,无法登录系统!";
				}

				Password oldPassword = user.getPassword();// 查询密码
				if (oldPassword != null) {
					String password_un = "";
					try {
						password_un = URLDecoder.decode(password.getPassword(),
								"UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					MD5 md5 = new MD5();
					String mdsPassword = md5.getMD5(password_un.getBytes());// 密码MD5转换
					HttpServletResponse response = ServletActionContext
							.getResponse();
					HttpServletRequest request = ServletActionContext
							.getRequest();
					if (mdsPassword.equals(oldPassword.getPassword())) {
						// 记入session监听器
						HttpSession session = request.getSession();
						session.setAttribute(TotalDao.users, user);

						// // 保存用户到session中
						ActionContext.getContext().getSession().put(
								TotalDao.users, user);

						// 如果登录成功,清空验证码错误记录
						Cookie vcodeCookie = new Cookie("vcode", "");
						vcodeCookie.setMaxAge(0);
						response.addCookie(vcodeCookie);

						try {
							// 是否需要自动登录 保存到Cookie中
							if (autoLogin != null && "yes".equals(autoLogin)) {
								String code_login = "";// 员工号
								String deptNum = "";// 部门编号
								String userPsd = "";// 密码
								// 获得所有的cookie,用于判断是否需要为cookie赋值
								Cookie[] cookies = request.getCookies();
								if (cookies != null) {
									for (int i = 0; i < cookies.length; i++) {
										Cookie cookie = cookies[i];
										if ("code".equals(cookie.getName())) {
											code_login = cookie.getValue();
										} else if ("deptNum".equals(cookie
												.getName())) {
											deptNum = cookie.getValue();
										} else if ("password".equals(cookie
												.getName())) {
											userPsd = cookie.getValue();
										}
									}
								}
								if (code_login == null
										|| code_login.length() <= 0
										|| deptNum == null
										|| deptNum.length() <= 0
										|| userPsd == null
										|| userPsd.length() <= 0
										|| !code_login.equals(user.getCode())) {
									code_login = URLEncoder.encode(user
											.getCode(), "utf-8");
									deptNum = URLEncoder.encode(oldPassword
											.getDeptNumber(), "utf-8");
									userPsd = URLEncoder.encode(password
											.getPassword(), "utf-8");
									// 设置生存时间(一周)
									Cookie codeCookie = new Cookie("code",
											code_login);
									codeCookie.setMaxAge(60 * 60 * 24 * 7);
									Cookie deptNumCookie = new Cookie(
											"deptNum", deptNum);
									deptNumCookie.setMaxAge(60 * 60 * 24 * 7);
									Cookie passwordCookie = new Cookie(
											"password", userPsd);
									passwordCookie.setMaxAge(60 * 60 * 24 * 7);
									// 保存cookie
									response.addCookie(codeCookie);
									response.addCookie(deptNumCookie);
									response.addCookie(passwordCookie);
								}

							} else {
								Cookie codeCookie = new Cookie("code", "");
								Cookie deptNumCookie = new Cookie("deptNum", "");
								Cookie passwordCookie = new Cookie("password",
										"");
								codeCookie.setMaxAge(0);// 为0 ，立即删除
								deptNumCookie.setMaxAge(0);// 为0 ，立即删除
								passwordCookie.setMaxAge(0);// 为0 ，立即删除
								response.addCookie(codeCookie);
								response.addCookie(deptNumCookie);
								response.addCookie(passwordCookie);
							}

							// 登录方式
							Cookie loginTypeCookie = null;
							if (user.getClassrole() != null
									&& user.getClassrole().equals("老师")) {
								loginTypeCookie = new Cookie("loginType",
										"fxLogin.jsp");
							} else {
								// 登录方式
								loginTypeCookie = new Cookie("loginType",
										"sjLogin.jsp");
							}
							loginTypeCookie.setMaxAge(60 * 60 * 24 * 7);
							response.addCookie(loginTypeCookie);
						} catch (UnsupportedEncodingException e) {
							return "自动登录功能出现故障,请检查后重试!";
						}
						// 获得真实ip
						String ip = request.getHeader("x-forwarded-for");
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getHeader("Proxy-Client-IP");
						}
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getHeader("WL-Proxy-Client-IP");
						}
						if (ip == null || ip.length() == 0
								|| "unknown".equalsIgnoreCase(ip)) {
							ip = request.getRemoteAddr();
						}
						user.setLoginIp(ip);
						return "true";
					} else {
						try {
							// 由于密码输入错误,清空密码的cookie
							Cookie passwordCookie = new Cookie("password", "");
							passwordCookie.setMaxAge(0);
							response.addCookie(passwordCookie);
							// 添加验证cookie,用于页面显示验证码
							String vcode = URLEncoder.encode("passwordError",
									"utf-8");
							Cookie vcodeCookie = new Cookie("vcode", vcode);
							vcodeCookie.setMaxAge(60 * 60 * 24);// 生存一天
							response.addCookie(vcodeCookie);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						return "密码错误! ";
					}
				} else {
					return "该部门编号错误! ";
				}
			} else {
				return "不存在该员工号! ";
			}

		}
		return "请输入帐号信息!";
	}

	/***
	 * 登录(检查卡号、密码)
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public String login(Users user, Password password) {
		if (user != null && password != null) {
			String hql = "from Users where cardId=?";
			user = (Users) totalDao.getObjectByCondition(hql, user.getCardId());// 查询工号是否存在
			if (user != null) {
				Password oldPassword = user.getPassword();// 查询部门编号是否存在
				if (oldPassword != null && oldPassword.getPassword() != null
						&& oldPassword.getPassword().length() > 0) {
					MD5 md5 = new MD5();
					String mdsPassword = md5.getMD5(password.getPassword()
							.getBytes());// 密码MD5转换
					HttpServletResponse response = ServletActionContext
							.getResponse();
					if (mdsPassword.equals(oldPassword.getPassword())) {
						// 保存用户到session中
						// 记入session监听器
						HttpServletRequest request = ServletActionContext
								.getRequest();
						HttpSession session = request.getSession();
						session.setAttribute(TotalDao.users, user);
						session.setMaxInactiveInterval(1000 * 60 * 60);// 失效时间

						// 如果登录成功,清空验证码错误记录
						Cookie vcodeCookie = new Cookie("vcode", "");
						vcodeCookie.setMaxAge(0);
						response.addCookie(vcodeCookie);

						// 登录方式
						Cookie loginTypeCookie = new Cookie("loginType",
								"otherLogin.jsp");
						loginTypeCookie.setMaxAge(60 * 60 * 24 * 7);
						response.addCookie(loginTypeCookie);

						return "true";
					} else {
						try {
							// 添加验证cookie,用于页面显示验证码
							String vcode = URLEncoder.encode("passwordError",
									"utf-8");
							Cookie vcodeCookie = new Cookie("vcode", vcode);
							vcodeCookie.setMaxAge(60 * 60 * 24);// 生存一天
							response.addCookie(vcodeCookie);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						return "密码不正确! <a href=''>重新登录</a>";
					}
				} else {
					return "密码不能为空! <a href=''>重新登录</a>";
				}
			} else {
				return "不存在该卡号! <a href=''>重新登录</a>";
			}

		}
		return "请输入帐号信息!";
	}

	// 根据工号查询员工信息
	public Users findUserByCode(String code) {
		if (code != null && code.length() > 0) {
			String hql = "from Users where code=?";
			return (Users) totalDao.getObjectByCondition(hql, code);
		}
		return null;
	}

	/****
	 * 查询员工照片
	 * 
	 * @param code
	 * @return
	 */
	@Override
	public String findImageByCode(String code) {
		if (code != null && code.length() > 0) {
			String hql = "select picture from Password where user.id= (select id from Users where code=?)";
			return (String) totalDao.getObjectByCondition(hql, code);
		}
		return null;
	}

	// 根据工号查询员工信息
	public Users findUserByCode1(String code) {
		if (code != null && code.length() > 0) {
			String hql = "from Users where code=?";
			// String sql =
			// "select users.*,ta_password.user_telephone,ta_password.user_mailBox  from users left join ta_password  "
			// +
			// "on ta_password.user_userid=users.id where users.code='"+code+"'";
			// Users users = (Users) this.totalDao.findBySql(sql);

			Users users = (Users) totalDao.getObjectByCondition(hql, code);
			Password pa = users.getPassword();
			users.setSex(pa.getPhoneNumber());// 性别代替手机号
			users.setNation(pa.getMailBox());// 代替邮箱
			return users;
		}
		return null;
	}

	// 根据卡号查询员工信息
	public Users findUserByCardId(String cardId) {
		if (cardId != null && cardId.length() > 0) {
			String hql = "from Users where cardId=?";
			return (Users) totalDao.getObjectByCondition(hql, cardId);
		}
		return null;
	}

	// 根据uId查询密码表信息
	public Password findPasswordByUid(Users user) {
		if (user != null) {
			String hql = "from Password p where p.user.id=?";
			return (Password) totalDao.getObjectByCondition(hql, user.getId());
		}
		return null;
	}

	// 修改密码
	public boolean updatePassword(Password password, String realPassword) {
		if (password != null && realPassword != null
				&& realPassword.length() > 0) {
			boolean bool = totalDao.update(password);
			if (bool) {
				PassReal real = (PassReal) totalDao
						.getObjectByCondition("from PassReal where uid=?",
								password.getUser().getId());
				if (real != null) {
					real.setRealPass(realPassword);
					totalDao.update(real);
				}
				// 更新用户对接表
				UserFacialInforServerImpl
						.addUserFacialInfor(password.getUser());// 添加面部识别考勤对接表
				// String hql = "update PassReal set realPass=? where uid=?";
				// totalDao.createQueryUpdate(hql, null, realPassword,
				// password.getUser().getId());
			}
			return bool;
		}
		return false;
	}

	// 通过功能id查找所有用户(未绑定用户)
	public Object[] findAllUser(String object, int functionId, int pageNo,
			int pageSize) {
		if (object.length() > 0 && functionId > 0) {
			Users user = (Users) ActionContext.getContext().getSession().get(
					"adminusers");
			String hql = null;
			if (user != null) {
				List<String> deptNames = totalDao
						.query("select deptName from UserDept where userId ="
								+ user.getId());
				StringBuffer sb = new StringBuffer();
				if (deptNames.size() > 0) {
					for (int i = 0; i < deptNames.size(); i++) {
						if (i == 0) {
							sb.append("'");
							sb.append(deptNames.get(i));
							sb.append("'");
						} else {
							sb.append(",'");
							sb.append(deptNames.get(i));
							sb.append("'");
						}
					}
				}
				hql = "from Users where onWork not in ('离职','离职中','内退','退休','病休') and dept in ("
						+ sb.toString()
						+ ")"
						+ " and dept not in('內退','病休') and id not in (select u.id from Users u join u."
						+ object + " f  where f.id =?)" + " order by dept";
			} else {
				hql = "from Users where onWork not in ('离职','离职中','内退','退休','病休') and dept not in('內退','病休') and id not in (select u.id from Users u join u."
						+ object + " f  where f.id =?)" + " order by dept";
				// hql="from Users where onWork not in ('离职','离职中','内退','退休','病休') and dept not in('內退','病休') order by dept";
			}

			List list = totalDao.findAllByPage(hql, pageNo, pageSize,
					functionId);
			int count = totalDao.getCount(hql, functionId);
			Object[] o = { list, count };
			return o;
		}
		return null;

	}

	// 通过功能id查找所有已绑定用户
	public List findAllBangUser(String object, int functionId) {
		if (object.length() > 0 && functionId > 0) {
			Users user = (Users) ActionContext.getContext().getSession().get(
					"adminusers");
			String hql = null;
			if (user != null) {
				List<String> deptNames = totalDao
						.query("select deptName from UserDept where userId ="
								+ user.getId());
				StringBuffer sb = new StringBuffer();
				if (deptNames.size() > 0) {
					for (int i = 0; i < deptNames.size(); i++) {
						if (i == 0) {
							sb.append("'");
							sb.append(deptNames.get(i));
							sb.append("'");
						} else {
							sb.append(",'");
							sb.append(deptNames.get(i));
							sb.append("'");
						}
					}
				}
				hql = "from Users where id in (select u.id from Users u join u."
						+ object
						+ " f  where f.id =? and u.onWork not in ('离职','离职中','内退','退休','病休') and u.dept not in('內退','病休') and u.dept in ("
						+ sb.toString() + "))" + " order by dept";
			} else {
				hql = "from Users where id in (select u.id from Users u join u."
						+ object
						+ " f  where f.id =? and u.onWork not in ('离职','离职中','内退','退休','病休') and u.dept not in('內退','病休') )"
						+ " order by dept";
			}

			return totalDao.query(hql, functionId);
		}
		return null;
	}


	/*
	    * @author fy
	　　* @date 2018/8/21 11:19
	　　* @Description:// 通过功能id 分页查找所有已绑定用户
	　　* @param [object, functionId]
	　　* @return java.util.List
	　　* @throws
	　　*/
	@Override
	public Object[] findAllBangUserByPage(String object, int functionId, int pageNo, int pageSize) {
		if (object.length() > 0 && functionId > 0) {
			Users user = (Users) ActionContext.getContext().getSession().get(
					"adminusers");
			String hql = null;
			if (user != null) {
				List<String> deptNames = totalDao.query("select deptName from UserDept where userId =" + user.getId());
				StringBuffer sb = new StringBuffer();
				if (deptNames.size() > 0) {
					for (int i = 0; i < deptNames.size(); i++) {
						if (i == 0) {
							sb.append("'");
							sb.append(deptNames.get(i));
							sb.append("'");
						} else {
							sb.append(",'");
							sb.append(deptNames.get(i));
							sb.append("'");
						}
					}
				}
				hql = "from Users where id in (select u.id from Users u join u."
						+ object
						+ " f  where f.id =? and u.onWork not in ('离职','离职中','内退','退休','病休') and u.dept not in('內退','病休') and u.dept in ("
						+ sb.toString() + "))" + " order by dept";
			} else {
				hql = "from Users where id in (select u.id from Users u join u."
						+ object
						+ " f  where f.id =? and u.onWork not in ('离职','离职中','内退','退休','病休') and u.dept not in('內退','病休') )"
						+ " order by dept";
			}

			List<Users> slist=totalDao.findAllByPage(hql,pageNo,pageSize,functionId);
//			totalDao.query(hql, functionId);
			int count = totalDao.getCount(hql,functionId);
			Object[] o = {slist, count};
			return o;

		}
		return null;
	}

	// 通过id查找用户
	public Users findUserById(int id) {
		if ((Integer) id != null && id > 0) {
			return (Users) totalDao.getObjectById(Users.class, id);
		}
		return null;
	}

	// 用户条件查询
	public Object[] findUsersByCondition(Users user, int pageNo, int pageSize,
			String pageStyle, Integer id) {
		if (user == null) {
			user = new Users();
		}
		String hql = "";
		boolean b = false;
		if (user.getPassword() != null) {
			if (user.getPassword().getUserStatus() != null
					&& user.getPassword().getUserStatus().length() > 0) {
				b = true;
				hql = "from Users where id in (select u.id from Users u join u.password p  where p.userStatus='"
						+ user.getPassword().getUserStatus()
						+ "') and onWork not in ('离职','退休')";
			} else if (user.getPassword().getPresentAddress() != null
					&& user.getPassword().getPresentAddress().length() > 0) {
				b = true;
				hql = "from Users where id in (select u.id from Users u join u.password p  where p.presentAddress like '%"
						+ user.getPassword().getPresentAddress() + "%')";
			}
		}
		if (!b) {
			user.setPassword(null);
			String dept = user.getDept();
			user.setDept(null);
			hql = totalDao.criteriaQueries(user, null);
			if (hql.indexOf(" onWork ") < 0) {
				if (hql.indexOf(" where ") > 0) {
					hql += " and onWork not in ('离职','退休')";
				} else {
					hql += " where onWork not in ('离职','退休')";
				}
			}
			if (dept != null && dept.length() > 0) {
				Integer deptId = (Integer) totalDao.getObjectByCondition(
						"select id from DeptNumber where dept=?", dept);
				if (deptId != null) {
					String sonDeptIds = DeptNumber.getDownAllIds(totalDao,
							deptId);
					if (sonDeptIds == null) {
						sonDeptIds = "" + deptId;
					} else {
						sonDeptIds += "," + deptId;
					}
					hql += " and dept in (select dept from DeptNumber where id in("
							+ sonDeptIds + "))";
				}
			}
			user.setDept(dept);
		}
		// hql +=
		// "and id not in (select u.id from Users u join u.moduleFunction f  where f.id =?)";
		if (pageStyle.equals("否")) {
			hql += " and internal='是' order by dept,id desc";
		}
		if (id != null) {
			hql += " and id not in(select u.id from Users u join u.moduleFunction m  where m.id="
					+ id + ")";
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	// 已绑定用户条件查询
	@Override
	public Object[] findHadBingdingUsersByCondition(Users user, int pageNo, int pageSize,
										 String pageStyle, Integer id) {
		if (user == null) {
			user = new Users();
		}
		String hql = "";
		boolean b = false;
		if (user.getPassword() != null) {
			if (user.getPassword().getUserStatus() != null
					&& user.getPassword().getUserStatus().length() > 0) {
				b = true;
				hql = "from Users where id in (select u.id from Users u join u.password p  where p.userStatus='"
						+ user.getPassword().getUserStatus()
						+ "') and onWork not in ('离职','退休')";
			} else if (user.getPassword().getPresentAddress() != null
					&& user.getPassword().getPresentAddress().length() > 0) {
				b = true;
				hql = "from Users where id in (select u.id from Users u join u.password p  where p.presentAddress like '%"
						+ user.getPassword().getPresentAddress() + "%')";
			}
		}
		if (!b) {
			user.setPassword(null);
			String dept = user.getDept();
			user.setDept(null);
			hql = totalDao.criteriaQueries(user, null);
			if (hql.indexOf(" onWork ") < 0) {
				if (hql.indexOf(" where ") > 0) {
					hql += " and onWork not in ('离职','退休')";
				} else {
					hql += " where onWork not in ('离职','退休')";
				}
			}
			if (dept != null && dept.length() > 0) {
				Integer deptId = (Integer) totalDao.getObjectByCondition(
						"select id from DeptNumber where dept=?", dept);
				if (deptId != null) {
					String sonDeptIds = DeptNumber.getDownAllIds(totalDao,
							deptId);
					if (sonDeptIds == null) {
						sonDeptIds = "" + deptId;
					} else {
						sonDeptIds += "," + deptId;
					}
					hql += " and dept in (select dept from DeptNumber where id in("
							+ sonDeptIds + "))";
				}
			}
			user.setDept(dept);
		}
		// hql +=
		// "and id not in (select u.id from Users u join u.moduleFunction f  where f.id =?)";
		if (pageStyle.equals("否")) {
			hql += " and internal='是' order by dept,id desc";
		}
		if (id != null) {
			hql += " and id in(select u.id from Users u join u.moduleFunction m  where m.id="
					+ id + ")";
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	// 修改用户
	public boolean updateUser(Users user) {
		if (user != null) {
			// 更新密码为身份证后六位
			// String hql = "from Users where onWork='在职'";
			// List<Users> list = totalDao.query(hql);
			// for (Users users : list) {
			// if (users.getUid() != null
			// && users.getUid().length() > 0
			// && users.getPassword() != null
			// && "e10adc3949ba59abbe56e057f20f883e".equals(users
			// .getPassword().getPassword())) {
			// MD5 md5 = new MD5();
			// String uid = users.getUid().substring(
			// users.getUid().length() - 6,
			// users.getUid().length());
			// String mdsPassword = md5.getMD5(uid.getBytes());// 密码MD5转换
			// users.getPassword().setPassword(mdsPassword);
			// boolean bool = totalDao.update(user);
			// if (bool) {
			// String hql2 = "from PassReal where uid=?";
			// PassReal passReal = (PassReal) totalDao
			// .getObjectByCondition(hql2, users.getId());
			// passReal.setRealPass(uid);
			// totalDao.update(passReal);
			// }
			// }
			// }

			return totalDao.update(user);
		}
		return false;
	}

	public boolean updateCareetrack(Users user) {
		String zhuanzhengtime = Util.getDateTime("yyyy-MM-dd");
		user.setZhuanzhengtime(zhuanzhengtime);
		String hql_ck = "from Careertrack where userId=?";
		Careertrack ck = (Careertrack) totalDao.getObjectByCondition(hql_ck,
				user.getId());
		ck.setStatus("在职");
		ck.setZhuanzhengTime(Util.getDateTime());
		return totalDao.update(ck);
	}

	// 查询所有的条款
	public List findAllProvision(String provisionStatus) {
		String hql = "from Provision where provisionStatus=?";
		return totalDao.query(hql, provisionStatus);
	}

	// 删除用户
	public boolean delUser(Users user) {
		if (user != null) {
			// 清除后台绑定关系
			String sql = "delete from ta_usersMF where  ta_userId=?";
			totalDao.createQueryUpdate(null, sql, user.getId());
			//清除和角色绑定关系
			String sql_0 = "delete from ta_usersrole where  ta_userId=?";
			totalDao.createQueryUpdate(null, sql_0, user.getId());
			return totalDao.delete(user);
		}
		return false;
	}

	// 试用期到期以及过期了仍未处理提醒
	public List findTryDaysEnd() {
//		String hql = "from Users where tryDays-getDate()<30  and onWork='试用' and onWork not in ('离职','退休') and dept not in('內退','病休')";// sqlServer
		String hql = "from Users where DATEDIFF(tryDays,CURRENT_DATE)<30  and onWork='试用' and onWork not in ('离职','退休') and dept not in('內退','病休')";// mysql
		return totalDao.query(hql);
	}

	// 合同到期以及过期了仍未处理提醒
	public List findContractEnd() {
//		String hql = "from Users where code in (select code from Contract where endDate-getDate()<30 and contractStatus='contract' and isUse='默认') and onWork not in ('离职','退休') and dept not in('內退','病休') ";// sqlServer
		String hql = "from Users where code in (select code from Contract where  DATEDIFF(endDate,CURRENT_DATE)<30 and contractStatus='contract' and isUse='默认') and onWork not in ('离职','退休') and dept not in('內退','病休') ";// mysql
		return totalDao.query(hql);

	}

	/***
	 * 通过部门查询人员
	 * 
	 * @param deptName
	 *            部门名称
	 * @return Users集合
	 */
	@Override
	public List findUsersByDept(String deptName) {
		if (deptName == null || deptName.length() < 1) {
			throw new NullPointerException("部门名称不能为空!");
		}
		String hql = null;
		if (deptName.indexOf(";") < 0) {
			hql = "from Users where dept=? and onWork not in ('离职','内退','离职中','退休') and dept not in('内退','病休')";
			return totalDao.query(hql, deptName);
		} else {
			String[] deptNames = deptName.split(";");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < deptNames.length; i++) {
				if (i == 0) {
					sb.append("'" + deptNames[i] + "'");
				} else {
					sb.append(",'" + deptNames[i] + "'");
				}
			}
			hql = "from Users where dept in ("
					+ sb.toString()
					+ ") and onWork not in ('离职','内退','离职中','退休') and dept not in('内退','病休')";
			return totalDao.query(hql);
		}
	}

	/***
	 * 根据部门名称查询部门信息
	 * 
	 * @param dept
	 *            部门名称
	 * @return DeptNumber
	 */
	public DeptNumber findDeptNumberByDept(String dept) {
		if (dept == null) {
			throw new NullPointerException("部门名称不能名称不能为空!");
		}
		String hql = "from DeptNumber where dept=?";
		return (DeptNumber) totalDao.getObjectByCondition(hql, dept);
	}

	@Override
	public List getDispatchUsers() {
		// TODO Auto-generated method stub
		List<Integer> list = totalDao
				.query("select userId from UserDept  group by userId");
		List<UserDept> returnList = new ArrayList<UserDept>();
		if (list.size() > 0) {
			for (Integer id : list) {
				Users user = (Users) totalDao.getObjectById(Users.class, id);
				if (user != null) {
					UserDept ud = new UserDept();
					ud.setUserId(user.getId());
					ud.setUserName(user.getName());
					ud.setCode(user.getCode());
					ud.setCardId(user.getCardId());
					ud.setDuty(user.getDuty());
					ud.setDeptName(user.getDept());
					List<String> deptNameList = totalDao.query(
							"select deptName from UserDept where userId=?", id);
					if (deptNameList.size() > 0) {
						StringBuffer sb = new StringBuffer();
						int i = 0;
						for (String deptName : deptNameList) {
							if (i == 0) {
								sb.append(deptName);
							} else {
								sb.append("," + deptName);
							}
							i++;
						}
						ud.setDeptName2(sb.toString());
					}
					ud.setBackStage(1);
					returnList.add(ud);
				}
			}
		}
		return returnList;
	}

	@Override
	public boolean upadteDispatch(Integer userid, Integer backStage,
			String deptIds) {
		// TODO Auto-generated method stub
		Users user = (Users) totalDao.getObjectById(Users.class, userid);

		if (user != null) {
			boolean b = true;
			user.setBackStage(backStage);
			b = b & totalDao.update(user);
			if (backStage == 0) {
				List<UserDept> userDepts = totalDao
						.query("from UserDept where userId =" + userid);
				for (UserDept ud : userDepts) {
					b = b & totalDao.delete(ud);
				}
			} else {
				// 找到此次沒有没有选中而之前已有的该用户的部门,然后删除
				List<UserDept> userDepts = totalDao
						.query("from UserDept where userId =" + userid
								+ " and deptId not in (" + deptIds + ")");
				for (UserDept ud : userDepts) {
					b = b & totalDao.delete(ud);
				}
				// 找到此次选中而之前已有的该用户的部门id
				List<Integer> hadids = totalDao
						.query("select deptId from UserDept where userId ="
								+ userid + " and deptId  in (" + deptIds + ")");
				List<String> hadidStrings = new ArrayList<String>();
				for (Integer hadid : hadids) {
					hadidStrings.add(hadid + "");
				}
				String[] deptIds2 = deptIds.split(",");
				if (deptIds2 != null && deptIds2.length > 0) {
					for (String deptId : deptIds2) {
						if (!hadidStrings.contains(deptId)) {
							DeptNumber dp = (DeptNumber) totalDao
									.getObjectById(DeptNumber.class, Integer
											.parseInt(deptId));
							if (dp != null) {
								UserDept userdept = new UserDept();
								userdept.setDeptId(dp.getId());
								userdept.setDeptName(dp.getDept());
								userdept.setUserId(userid);
								userdept.setUserName(user.getName());
								b = b & totalDao.save(userdept);
							}
						}
					}
				}
			}

			return b;
		}
		return false;
	}

	/*
	 * 查看员工工作记录(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findWorkrecords(int)
	 */
	@Override
	public Object[] findWorkrecords(int id, int parseInt, int pageSize) {
		List<Map> list = new ArrayList<Map>();
		Users users = (Users) this.totalDao.getObjectById(Users.class, id);
		Integer count = null;
		if (users == null) {
			users = new Users();
		} else {
			String sql = "select c.processNO, p.selfCard,c.applyCount, c.id, p.proname,p.markid, p.count,c.usercodes,c.usernames, c.gongwei, c.shebeiNo,c.firstApplyDate,c.submitDate "
					+ "from ta_sop_w_ProcessInfor c inner join ta_sop_w_procard p on  c.fk_procardid=p.id and "
					+ " c.status in('领工序','完成') and c.usercodes='"
					+ users.getCode() + "'";
			sql += " order by c.firstApplyDate  desc";
			list = this.totalDao.findBySql(sql);
			count = list.size();
			list = this.totalDao.findBySql(sql, parseInt, pageSize);
		}
		Object[] o = { list, count };
		return o;
	}

	/*
	 * 查看所有员工工作记录(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findWorkrecords(int, int)
	 */
	@Override
	public Object[] findAllWorkrecords(String name, String code,
			String gongwei, String markId, String firstApplyDate,
			String submitDate, int parseInt, int pageSize) {
		String sql = "select c.processNO, p.selfCard,c.applyCount, c.id, p.proname,p.markid,c.submmitCount, p.count,c.usercodes,c.usernames, c.gongwei,"
				+ " c.shebeiNo,c.firstApplyDate,c.submitDate from ta_sop_w_ProcessInfor c "
				+ "inner join ta_sop_w_procard p on  c.fk_procardid=p.id and  c.status in('领工序','完成')";
		if (name != null && !"".equals(name)) {
			sql += " and c.usernames like'%" + name + "%'";
		}
		if (code != null && !"".equals(code)) {
			sql += " and c.usercodes like '%" + code + "%'";
		}
		if (gongwei != null && !"".equals(gongwei)) {
			sql += " and c.gongwei like '%" + gongwei + "%'";
		}
		if (markId != null && !"".equals(markId)) {
			sql += " and p.markId like '%" + markId + "%'";
		}
		if (firstApplyDate != null && !"".equals(firstApplyDate)) {
			sql += " and c.firstApplyDate like '%" + firstApplyDate + "%'";
		}
		if (submitDate != null && !"".equals(submitDate)) {
			sql += " and c.submitDate like '%" + submitDate + "%'";
		}
		sql += " order by c.firstApplyDate  desc";
		List list = this.totalDao.findBySql(sql);

		Integer count = list.size();
		list = this.totalDao.findBySql(sql, parseInt, pageSize);
		Object[] o = { list, count };
		return o;
	}

	/*
	 * 查询所有件号(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getProcardAllNames()
	 */
	@Override
	public List getProcardAllNames(String markId) {
		List list = totalDao.query("from Procard where markId  like '%"
				+ markId + "%'");
		List<String> markIdList = new ArrayList<String>();
		List list2 = new ArrayList<Object>();
		if (list.size() > 0) {
			int count = 0;
			for (int i = 0; i < list.size(); i++) {
				Procard procard = (Procard) list.get(i);
				if (procard.getMarkId() != null
						&& !markIdList.contains(procard.getMarkId())) {
					markIdList.add(procard.getMarkId());
					list2.add(procard);
					count++;
					if (count == 10) {// 只取前十条
						break;
					}
				}
			}
		}
		return list2;
	}

	/*
	 * 查询所有工位(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getGongweiAllNames(java.lang.String)
	 */
	@Override
	public List getGongweiAllNames(String gongwei) {
		List list = totalDao.query("from ProcessInfor where gongwei  like '%"
				+ gongwei + "%'");
		List<String> markIdList = new ArrayList<String>();
		List list2 = new ArrayList<Object>();
		if (list.size() > 0) {
			int count = 0;
			for (int i = 0; i < list.size(); i++) {
				ProcessInfor processInfor = (ProcessInfor) list.get(i);
				if (processInfor.getGongwei() != null
						&& !markIdList.contains(processInfor.getGongwei())) {
					markIdList.add(processInfor.getGongwei());
					list2.add(processInfor);
					count++;
					if (count == 10) {// 只取前十条
						break;
					}
				}

			}
		}
		return list2;
	}

	/*
	 * 查询所有的件号(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getProcardAllNames1()
	 */
	@Override
	public List getProcardAllNames1() {
		List list = totalDao.query("select distinct(markId) from Procard");
		return list;
	}

	/*
	 * 查询所有的员工(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getProcessInforName()
	 */
	@Override
	public List getProcessInforName() {
		// TODO Auto-generated method stub
		String hql = "select distinct(usernames) from ProcessInfor";
		List list = this.totalDao.query(hql);
		return list;
	}

	/*
	 * 汇总所有的员工工作记录(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findAllWorkrecords1(java.util.List,
	 * java.util.List)
	 */
	@Override
	public Object[] findAllWorkrecords1(String name, String code,
			String markId, int parseInt, int pageSize) {
		String hql = "from ProcessInfor "
				+ "c join c.procard p where  c.status in('领工序','完成')  ";
		if (name != null && !"".equals(name)) {
			hql += " and c.usernames like'%" + name + "%'";
		}
		if (code != null && !"".equals(code)) {
			hql += " and c.usercodes like '%" + code + "%'";
		}
		if (markId != null && !"".equals(markId)) {
			hql += " and p.markId like '%" + markId + "%'";
		}
		String selhql = "select c.usercodes,c.usernames,p.markId,sum(c.submmitCount),c.processNO,c.processName "
				+ hql
				+ " group by p.markId,c.processNO,c.processName,c.usercodes,c.usernames order by c.usernames";
		List newList = this.totalDao.find(selhql, parseInt, pageSize);
		long zongDate = 0;
		int len = newList.size();
		List newList2 = new ArrayList();
		for (int i = 0; i < len; i++) {
			Object[] s = (Object[]) newList.get(i);
			String usercode = "";
			String markid1 = "";
			String processNO = "";
			String hql1 = "";
			if (s[0] != null) {
				usercode = s[0].toString();
			}
			if (s[2] != null) {
				markid1 = s[2].toString();
			}
			if (s[4] != null) {
				processNO = s[4].toString();
			}
			hql1 = "select c.firstApplyDate,c.submitDate from ProcessInfor c "
					+ "join c.procard p where  c.status in('领工序','完成')";
			if (!"".equals(usercode)) {
				hql1 += " and c.usercodes='" + usercode + "'";
			} else {
				hql1 += " and c.usercodes is null";
			}
			if (!"".equals(markid1)) {
				hql1 += " and p.markId='" + markid1 + "'";
			} else {
				hql1 += " and p.markId is null";
			}
			if (!"".equals(processNO)) {
				hql1 += " and c.processNO='" + processNO + "'";
			} else {
				hql1 += " and c.processNO is null";
			}
			List list = this.totalDao.query(hql1);
			for (int j = 0; j < list.size(); j++) {
				Object[] o0 = (Object[]) list.get(j);
				if (o0[0] != null && o0[1] != null) {
					String firstDate = o0[0].toString();
					String endDate = o0[1].toString();
					Date f = DateUtil.parseDate(firstDate,
							"yyyy-MM-dd HH:mm:ss");
					Integer a1 = endDate.lastIndexOf(",");
					endDate = endDate.substring(a1 + 1, endDate.length());
					Date e = DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss");
					zongDate += e.getTime() - f.getTime();
				}
			}
			newList2.add(new Object[] { s[0], s[1], s[2], s[3],
					zongDate / 3600 / 1000, s[4], s[5] });
		}
		Integer count = totalDao.query(selhql).size();
		Object[] o = { newList2, count };
		return o;
	}

	/*
	 * 查询员工明细(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findAllWorkrecords1(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public Object[] findAllWorkrecords1(String name, String code,
			String gongwei, String markId, String processNO,
			String firstApplyDate, String submitDate, int parseInt, int pageSize) {
		String sql = "from ta_sop_w_ProcessInfor c "
				+ "inner join ta_sop_w_procard p on  c.fk_procardid=p.id and  c.status in('领工序','完成') where 1=1";
		if (gongwei != null && !"".equals(gongwei)) {
			sql += " and c.gongwei like '%" + gongwei + "%'";
		}
		if (processNO != null && !"".equals(processNO)) {
			sql += " and c.processNO = '" + processNO + "'";
		}
		if (firstApplyDate != null && !"".equals(firstApplyDate)) {
			sql += " and c.firstApplyDate like '%" + firstApplyDate + "%'";
		}
		if (submitDate != null && !"".equals(submitDate)) {
			sql += " and c.submitDate like '%" + submitDate + "%'";
		}
		sql += " and c.usercodes like '%" + code + "%'";
		String hqlq = "select c.processNO, p.selfCard,c.applyCount, c.id, p.proname,p.markid,c.submmitCount, p.count,c.usercodes,c.usernames, c.gongwei,"
				+ " c.shebeiNo,c.firstApplyDate,c.submitDate,c.processStatus,c.processName  "
				+ sql + " order by c.firstApplyDate  desc";
		List list = this.totalDao.findBySql(hqlq, parseInt, pageSize);
		List list_count = totalDao.createQuerySelect(null, "select count(*) "
				+ sql);
		Integer count =  Integer.parseInt(list_count.get(0).toString());
		Object[] o = { list, count };
		return o;
	}

	/*
	 * 单个员工工作记录(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findAllWorkrecords1(int, int, int)
	 */
	@Override
	public Object[] findAllWorkrecords1(int id, String markId,
			String processName, int parseInt, int pageSize) {
		Users users = (Users) this.totalDao.getObjectById(Users.class, id);
		String hql = " from ProcessInfor where status in('领工序','完成')  and usercodes='"
				+ users.getCode() + "'";
		String hql1 = "select usercodes,usernames,sum(submmitCount),sum(breakCount),sum(gzDateTime) "
				+ hql + " group by usernames,usercodes order by usernames";
		List newList = this.totalDao.find(hql1, parseInt, pageSize);

		List newList2 = new ArrayList();
		int len = newList.size();
		for (int i = 0; i < len; i++) {
			Object[] s = (Object[]) newList.get(i);
			String hql2 = "";
			String hql3 = "";
			hql2 = "select firstApplyDate,submitDate from ProcessInfor where  status in('领工序','完成')";
			if (s[0] != null && !s[0].equals("")) {
				String[] ss = s[0].toString().split(",");
				if (ss != null && ss.length > 0) {
					StringBuffer sb = new StringBuffer();
					for (String s1 : ss) {
						sb.append("'");
						sb.append(s1);
						sb.append("',");
					}
					String s2 = sb.toString();
					s2 = s2.substring(0, s2.length() - 1);
					hql2 += " and usercodes in (" + s2 + ")";
					hql3 = "select distinct(processName) from ProcessInfor  where usercodes in ("
							+ s2 + ") ";
				}
			}
			// List list = this.totalDao.query(hql2);
			List list1 = this.totalDao.query(hql3);
			Integer list1_size = list1.size();
			newList2.add(new Object[] { s[0], s[1], s[2], s[3], list1_size,
					String.format("%.2f", s[4]) });
		}
		Integer count = totalDao.query(hql1).size();
		Object[] o = { newList2, count };
		return o;
	}

	/*
	 * 查询所有工序(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getprocessNONames(java.lang.String)
	 */
	@Override
	public List getprocessNONames(Integer id, String processName) {
		Users users = (Users) this.totalDao.getObjectById(Users.class, id);
		List list = totalDao
				.query("from ProcessInfor where processName  like '%"
						+ processName + "%' and usercodes like '%"
						+ users.getCode() + "%'");
		List<String> processNOList = new ArrayList<String>();
		List list2 = new ArrayList<Object>();
		if (list.size() > 0) {
			int count = 0;
			for (int i = 0; i < list.size(); i++) {
				ProcessInfor processInfor = (ProcessInfor) list.get(i);
				if (processInfor.getProcessName() != null
						&& !processNOList.contains(processInfor
								.getProcessName())) {
					processNOList.add(processInfor.getProcessName().toString());
					list2.add(processInfor);
					count++;
					if (count == 10) {// 只取前十条
						break;
					}
				}
			}
		}
		return list2;
	}

	/*
	 * 查看所有的工作信息(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#findAllWorkrecords3(java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public Object[] findAllWorkrecords3(String name, String code, int parseInt,
			int pageSize) {
		// String hql = " from ProcessInfor where status in('领工序','完成') ";
		// if (name != null && !"".equals(name)) {
		// hql += " and  usernames like'%" + name + "%'";
		// }
		// if (code != null && !"".equals(code)) {
		// hql += " and  usercodes like '%" + code + "%'";
		// }
		// String hql1 =
		// "select usercodes,usernames,sum(submmitCount),sum(breakCount),sum(gzDateTime) "
		// + hql + "  group by usernames,usercodes order by usernames";
		// List newList = this.totalDao.find(hql1, parseInt, pageSize);
		//
		// List newList2 = new ArrayList();
		// int len = newList.size();
		// for (int i = 0; i < len; i++) {
		// Object[] s = (Object[]) newList.get(i);
		// String hql2 = "";
		// String hql3 = "";
		// hql2 =
		// "select sum(submmitCount),sum(breakCount) from ProcessInfor where  status in('领工序','完成')";
		// if (s[0] != null && !s[0].equals("")) {
		// String[] ss = s[0].toString().split(",");
		// if (ss != null && ss.length > 0) {
		// StringBuffer sb = new StringBuffer();
		// for (String s1 : ss) {
		// sb.append("'");
		// sb.append(s1);
		// sb.append("',");
		// }
		// String s2 = sb.toString();
		// s2 = s2.substring(0, s2.length() - 1);
		//
		// hql2 += " and usercodes like '%" + s[0] + "%'";
		// hql3 =
		// "select distinct(processName) from ProcessInfor  where usercodes like '%"
		// + s[0] + "%'";
		// }
		// } else {
		// hql2 += " and usercodes = ''";
		// hql3 =
		// "select distinct(processName) from ProcessInfor  where usercodes =''";
		// }
		// List list = this.totalDao.query(hql2);
		// List list1 = this.totalDao.query(hql3);
		// Integer list1_size = list1.size();
		// float submmitCount = 0F;
		// float breakCount = 0F;
		// for (int j = 0; j < list.size(); j++) {
		// Object[] o0 = (Object[]) list.get(j);
		// if (o0[0] != null && o0[1] != null) {
		// submmitCount = Float.parseFloat(o0[0].toString());
		// breakCount = Float.parseFloat(o0[1].toString());
		// }
		// }
		// newList2.add(new Object[] { s[0], s[1], submmitCount, breakCount,
		// list1_size, String.format("%.2f", s[4]) });
		// }
		// Integer count = totalDao.query(hql1).size();
		// Object[] o = { newList2, count };

		// 获得所有领取过工序的人员工号
		String hql_uid = "select  usercodes from ProcessInfor where "
				+ "status in('领工序','完成') and usercodes is not null and usercodes<>'' group by usercodes order by usercodes";
		List newLists = this.totalDao.query(hql_uid);
		String allcode = newLists.toString();
		allcode = "'"
				+ allcode.replaceAll("\\[", "").replaceAll("\\]", "")
						.replaceAll(" ", "").replaceAll(",", "','") + "'";
		allcode = allcode.replaceAll(",'',", ",");

		String[] test1 = allcode.split(",");
		ArrayList arlist = new ArrayList();
		for (int i = 0; i < test1.length; i++) {
			if (!arlist.contains(test1[i]))
				arlist.add(test1[i]);
		}
		allcode = arlist.toString().replaceAll("\\[", "").replaceAll("\\]", "");
		// 获得所有领取过工序的人员工号结束
		String hql = "from Users where  code in (" + allcode
				+ ") and onwork in ('在职','试用')  order by onwork desc,code ";
		List<Users> list = totalDao.findAllByPage(hql, parseInt, pageSize);
		List newList = new ArrayList();
		for (Users user : list) {
			String hql_sumProName = "select distinct(processName) from ProcessInfor  where usercodes like '%"
					+ user.getCode() + "%'";
			List list1 = this.totalDao.query(hql_sumProName);
			if (list1 != null && list1.size() > 0) {
				Integer sumProName = list1.size();
				if (sumProName != null && sumProName > 0) {
					String hql1 = "select sum(submmitCount),sum(breakCount),sum(gzDateTime) from ProcessInfor"
							+ " where status in('领工序','完成') and usercodes like '%"
							+ user.getCode() + "%'";
					List lsList = totalDao.query(hql1);
					if (lsList != null && lsList.size() > 0
							&& lsList.get(0) != null) {
						Object[] obj = (Object[]) lsList.get(0);
						Double gzDateTime = 0d;
						if(obj[2]!=null){
							gzDateTime = (Double)obj[2]/3600;
						}
						newList.add(new Object[] { user.getCode(),
								user.getName(), user.getDept(),
								user.getOnWork(), obj[0], obj[1], sumProName,
								String.format("%.2f", gzDateTime) });
					}
				}
			}
		}
		Object[] o = { newList, arlist.size() };
		return o;
	}

	/*
	 * 根据员工号查询已干的工序(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#getproNameByCode(java.lang.String)
	 */
	@Override
	public List getproNameByCode(String code) {
		// TODO Auto-generated method stub
		String[] ss = code.toString().split(",");
		List<String> processNOList = new ArrayList<String>();
		List list2 = new ArrayList<Object>();
		if (ss != null && ss.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (String s1 : ss) {
				sb.append("'");
				sb.append(s1);
				sb.append("',");
			}
			String s2 = sb.toString();
			s2 = s2.substring(0, s2.length() - 1);
			List list = totalDao
					.query("select distinct(processName) from ProcessInfor  where usercodes like '%"
							+ code + "%'");
			if (list.size() > 0) {
				int count = 0;
				for (int i = 0; i < list.size(); i++) {
					String proname = (String) list.get(i);
					if (proname != null && !processNOList.contains(proname)) {
						processNOList.add(proname.toString());
						list2.add(proname);
					}

				}
			}
			return list2;
		}
		return null;
	}

	/*
	 * 导出(non-Javadoc)
	 * 
	 * @see com.task.Server.UserServer#exportExcel(com.task.entity.Users)
	 */
	@Override
	public void exportExcel(Users user, String external) {
		if (user == null) {
			user = new Users();
		}
		if (external != null && external.equals("否")) {
			user.setInternal("是");
		} else {
			user.setInternal("否");
		}
		String hql = totalDao.criteriaQueries(user, null, "password");
		hql += " order by dept";
		List list = totalDao.find(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("员工信息汇总".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("员工信息汇总", 0);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 25,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcf.setAlignment(Alignment.CENTRE);
			jxl.write.Label label0 = new Label(0, 0, "员工信息表", wcf);
			ws.addCell(label0);
			ws.mergeCells(0, 0, 13, 0);
			wf = new WritableFont(WritableFont.ARIAL, 18, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);

			// // 定义单元格样式 定义格式 字体 下划线 颜色 斜体 粗体
			WritableCellFormat wcf2 = new WritableCellFormat(new WritableFont(
					WritableFont.ARIAL, 10)); // 单元格定义
			wcf2.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);// 添加细边框样式
			// 创建一个样式
			wc.setAlignment(Alignment.CENTRE);
			ws.addCell(new jxl.write.Label(0, 1, "编号", wcf2));
			ws.addCell(new jxl.write.Label(1, 1, "工号", wcf2));
			ws.addCell(new jxl.write.Label(2, 1, "卡号", wcf2));
			ws.addCell(new jxl.write.Label(3, 1, "姓名", wcf2));
			ws.addCell(new jxl.write.Label(4, 1, "性别", wcf2));

			ws.addCell(new jxl.write.Label(5, 1, "部门", wcf2));
			ws.addCell(new jxl.write.Label(6, 1, "部门编号", wcf2));
			ws.addCell(new jxl.write.Label(7, 1, "职位", wcf2));
			ws.addCell(new jxl.write.Label(8, 1, "学历", wcf2));
			ws.addCell(new jxl.write.Label(9, 1, "状态", wcf2));

			ws.addCell(new jxl.write.Label(10, 1, "籍贯", wcf2));
			ws.addCell(new jxl.write.Label(11, 1, "身份证号", wcf2));
			ws.addCell(new jxl.write.Label(12, 1, "入职时间", wcf2));
			ws.addCell(new jxl.write.Label(13, 1, "手机号", wcf2));

			// 格式
			ws.setRowView(1, 1000);// 第二行的高度
			ws.setColumnView(0, 5);// 第一列的宽度
			ws.setColumnView(1, 8);// 第二列的宽度
			ws.setColumnView(2, 12);// 第三列的宽度
			ws.setColumnView(3, 10);// 第四列的宽度
			ws.setColumnView(4, 8);// 第五列的宽度
			ws.setColumnView(5, 10);// 第六列的宽度
			ws.setColumnView(6, 10);// 第七列的宽度
			ws.setColumnView(7, 15);// 第八列的宽度
			ws.setColumnView(8, 10);// 第九列的宽度
			ws.setColumnView(9, 8);// 第十列的宽度
			ws.setColumnView(10, 20);// 第十列的宽度
			ws.setColumnView(11, 20);// 第十列的宽度
			ws.setColumnView(12, 10);// 第十列的宽度
			ws.setColumnView(13, 15);// 第十列的宽度

			// 把水平对齐方式指定为居中
			WritableFont font1 = new WritableFont(WritableFont.TIMES, 16,
					WritableFont.BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			format1.setAlignment(jxl.format.Alignment.CENTRE);

			DecimalFormat fnum = new DecimalFormat("##0.00"); // 保留两位小数
			for (int i = 0; i < list.size(); i++) {
				ws.setRowView(i + 2, 500);// 循环调整每行的高度（从第二行开始）
				Users users = (Users) list.get(i);

				ws.addCell(new jxl.write.Number(0, i + 2, i + 1, wcf2));
				ws.addCell(new Label(1, i + 2, users.getCode(), wcf2));
				ws.addCell(new Label(2, i + 2, users.getCardId(), wcf2));
				ws.addCell(new Label(3, i + 2, users.getName(), wcf2));

				ws.addCell(new Label(4, i + 2, users.getSex(), wcf2));
				ws.addCell(new Label(5, i + 2, users.getDept(), wcf2));
				if (users.getPassword() == null) {
					ws.addCell(new Label(6, i + 2, "", wcf2));
				} else {
					if (users.getPassword().getDeptNumber() == null) {
						ws.addCell(new Label(6, i + 2, "", wcf2));
					} else {
						ws.addCell(new Label(6, i + 2, users.getPassword()
								.getDeptNumber(), wcf2));
					}
				}
				ws.addCell(new Label(7, i + 2, users.getDuty(), wcf2));
				ws.addCell(new Label(8, i + 2, users.getEducation(), wcf2));
				ws.addCell(new Label(9, i + 2, users.getOnWork(), wcf2));
				ws.addCell(new Label(10, i + 2, users.getBirthplace(), wcf2));
				ws.addCell(new Label(11, i + 2, users.getUid(), wcf2));
				ws.addCell(new Label(12, i + 2, Util.DateToString(users
						.getJoined(), "yyyy-MM-dd"), wcf2));
				if (users.getPassword() == null) {
					ws.addCell(new Label(13, i + 2, "", wcf2));
				} else {
					if (users.getPassword().getPhoneNumber() == null) {
						ws.addCell(new Label(13, i + 2, "", wcf2));
					} else {
						ws.addCell(new Label(13, i + 2, users.getPassword()
								.getPhoneNumber(), wcf2));
					}
				}
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Integer getMaxCode() {
		List<String> maxCodeList = totalDao.query("select code from Users");
		if (maxCodeList.size() > 0) {
			Integer maxCode = 0;
			for (String code : maxCodeList) {
				try {
					Integer intCode = Integer.parseInt(code);
					if (intCode - maxCode > 0) {
						maxCode = intCode;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			return maxCode + 1;
		}
		return null;
	}

	@Override
	public void updatecodeNumber(String oldcodeNmuber, String newNmuber) {
		// TODO Auto-generated method stub
		/*
		 * String hql =
		 * "update "+obj+" set "+objCode+"='"+newNmuber+"' where "+objCode
		 * +"='"+codeNmuber+"'"; totalDao.createQueryUpdate(hql,null);
		 */
		totalDao.createQueryUpdate("update Consuming set cardNum ='"
				+ newNmuber + "' where cardNum='" + oldcodeNmuber + "'", null);
		totalDao.createQueryUpdate("update Also set cardNum ='" + newNmuber
				+ "' where cardNum='" + oldcodeNmuber + "'", null);
		totalDao.createQueryUpdate("update OutLib set cardNum ='" + newNmuber
				+ "' where cardNum='" + oldcodeNmuber + "'", null);
		totalDao.createQueryUpdate("update Borrow set cardNum ='" + newNmuber
				+ "' where cardNum='" + oldcodeNmuber + "'", null);
		//考勤，加班，请假，考勤汇总。
	}

	/***
	 * 根据用户名称查询用户信息
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public Users findUserByName(String name) {
		if (name != null && name.length() > 0) {
			String hql = "from Users where name =?";
			return (Users) totalDao.getObjectByCondition(hql, name);
		}
		return null;
	}

	// 修改个人资料
	@Override
	public boolean grupdateZL(Users users) {
		Users user2 = (Users) totalDao
				.getObjectById(Users.class, users.getId());
		if (user2 != null) {
			Password p = user2.getPassword();
			p.setPhoneNumber(users.getPassword().getPhoneNumber());
			p.setMailBox(users.getPassword().getMailBox());
			if (users.getPassword().getPicture() != null
					&& !users.getPassword().getPicture().equals("")) {
				p.setPicture(users.getPassword().getPicture());
			}
			return totalDao.update(p);
		}
		return false;

	}

	@Override
	public List findBanCi() {
		// TODO Auto-generated method stub
		String hql = "from BanCi where name is not null and name <> ''";
		List list = totalDao.query(hql);
		return list;
	}

	@Override
	public Users logins(String usercode, String password) {
		// TODO Auto-generated method stub
		Users u = (Users) totalDao.getObjectByCondition(
				"from Users where code = ?", usercode);
		if (u != null) {
			String ps = u.getPassword().getPassword();
			MD5 md5 = new MD5();
			String mdsPassword = md5.getMD5(password.getBytes());// 密码MD5转换
			if (mdsPassword.equalsIgnoreCase(ps)) {
				return u;
			}
		}
		return null;
	}

	@Override
	public String updateUsersW(Users user1,File picture,String pictureFileName) {
		// TODO Auto-generated method stub
		if (user1 == null)
			return "用户为空修改失败!";
		Users user = findUserById(user1.getId());
		if (user == null)
			return "用户不存在修改失败!";
		user.getPassword().setPresentAddress(
				user1.getPassword().getPresentAddress());// 现住址
		user.getPassword().setMailBox(user1.getPassword().getMailBox());// 电话
		user.getPassword().setQqNumber(user1.getPassword().getQqNumber());// QQ号码
		user.getPassword().setPhoneNumber(user1.getPassword().getPhoneNumber());// 手机号
		user.getPassword().setWxNumber(user1.getPassword().getWxNumber());// 微信号
		user.getPassword().setRili(user1.getPassword().getRili());// 阴历阳历
		user.getPassword().setShijiBirthDay(
				user1.getPassword().getShijiBirthDay());// 实际生日
		String authorization = user1.getPassword().getAuthorizationCode();//邮箱授权码
		if(null!=authorization && !"".equals(authorization)){
			AESEnctypeUtil util = new AESEnctypeUtil();
			String aeseAuth = util.enctype(authorization);
			user.getPassword().setAuthorizationCode(aeseAuth);//邮箱密码
		}
		
		//上传
		if (picture != null && pictureFileName != null) {
			pictureFileName = Util.getDateTime("yyyyMMddHHmmss")
					+ user.getCode()
					+ pictureFileName
							.substring(
									pictureFileName.lastIndexOf("."),
									pictureFileName.length());
			Upload upload = new Upload();
			String fileMessage = upload.UploadFile(picture, pictureFileName,
					null, "/upload/user",
					"D:/WorkSpace/HHTask/WebRoot/upload/user");
			user.getPassword().setPicture(fileMessage);
			// if (!"true".equals(fileMessage)) {
			// return fileMessage;
			// }
		}
		
		if (totalDao.update(user)){
			ActionContext.getContext().getSession().put(TotalDao.users, user);
			return "修改成功！";
		}
		return "修改失败！";
	}

	@Override
	public String UsersCardbangUser(UsersCard usercard, Integer[] userId,
			String pageStatus) {
		if (usercard != null) {
			UsersCard ud = (UsersCard) totalDao.getObjectByCondition(
					" from UsersCard where cardId=?", usercard.getCardId());
			if (ud == null) {
				if (userId != null && userId.length > 0) {
					Set<Users> userSet = new HashSet<Users>();
					for (int i = 0; i < userId.length; i++) {
						Users user = (Users) totalDao.get(Users.class,
								userId[i]);
						userSet.add(user);
					}
					usercard.setUsersSet(userSet);
					return totalDao.save(usercard) + "";
				} else {
					return "请选择绑定人员!";
				}
			} else {
				Set<Users> userSet = ud.getUsersSet();
				Set<Users> userSet1 = new HashSet<Users>();
				for (int i = 0; i < userId.length; i++) {
					Users user = (Users) totalDao.get(Users.class, userId[i]);
					userSet1.add(user);
				}
				if ("ybd".equals(pageStatus)) {
					userSet.removeAll(userSet1);
				} else {
					userSet.addAll(userSet1);
				}
				ud.setUsersSet(userSet);
				return totalDao.update(ud) + "";
			}
		}
		return "请填写卡号";
	}

	@Override
	public Object[] findAllUsers(Integer id, Users user, int parseInt,
			int pageSize, String pageStatus) {

		String hql = " from Users where onWork in ('在职','实习','试用')";
		if (user != null) {
			if (user.getDept() != null && user.getDept().length() > 0) {
				hql += " and  dept like '%" + user.getDept() + "%'";
			}
			if (user.getName() != null && user.getName().length() > 0) {
				hql += " and name like '%" + user.getName() + "%'";
			}
			if (user.getCode() != null && user.getCode().length() > 0) {
				hql += " and code like '%" + user.getCode() + "%'";
			}
		}
		UsersCard usersCard = null;
		if (id == null) {
			if ("ybd".equals(pageStatus)) {
				hql += " and userscard.id <> '' and userscard.id is not null ";
			} else if ("wbd".equals(pageStatus)) {
				hql += " and (userscard.id is null or userscard.id = '')";
			}
		} else {
			usersCard = (UsersCard) totalDao.get(UsersCard.class, id);
			if ("ybd".equals(pageStatus)) {
				hql += " and userscard.id = " + id;
			} else if ("wbd".equals(pageStatus)) {
				hql += " and  (userscard.id is null or userscard.id = '')";
			}
		}
		List<Users> userList = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] obj = { userList, count, usersCard };
		return obj;
	}

	@Override
	public Object[] findAllUsersCard(UsersCard userscard, int parseInt,
			int pageSize, String pageStatus) {
		if (userscard == null) {
			userscard = new UsersCard();
		}
		String hql = totalDao.criteriaQueries(userscard, null);
		List<UsersCard> usercardList = totalDao.findAllByPage(hql, parseInt,
				pageSize);
		int count = totalDao.getCount(hql);
		Object[] obj = { usercardList, count };
		return obj;
	}

	@Override
	public UsersCard findUsersCardByCardId(String cardId) {
		UsersCard usersCard = null;
		if (cardId != null && cardId.length() > 0) {
			String hql = " from UsersCard where cardId = ?";
			usersCard = (UsersCard) totalDao.getObjectByCondition(hql, cardId);
			if (usersCard == null) {
				Users user = (Users) totalDao.getObjectByCondition(
						" from Users where cardId = ?", cardId);
				if (user != null) {
					usersCard = new UsersCard();
					usersCard.setCardId(cardId);
					usersCard.setCkUserCode(user.getCode());
					usersCard.setCkUserId(user.getId());
					usersCard.setCkUserName(user.getName());
					usersCard.setDept(user.getDept());
					Set<Users> userset = new HashSet<Users>();
					userset.add(user);
					usersCard.setUsersSet(userset);
					totalDao.save(usersCard);
				}
			}
		}
		return usersCard;
	}

	@Override
	public boolean BangCard(Integer id, String cardId) {
		if (id != null && cardId != null && cardId.length() > 0) {
			UsersCard usersCard = (UsersCard) totalDao.get(UsersCard.class, id);
			Users user = (Users) totalDao.getObjectByCondition(
					" from Users where cardId = ?", cardId);
			if (user != null) {
				Set<Users> usersSet = usersCard.getUsersSet();
				usersSet.add(user);
				return totalDao.update(usersCard);
			}
		}
		return false;
	}

	@Override
	public String delUsersCard(Integer id) {
		UsersCard userscard = (UsersCard) totalDao.get(UsersCard.class, id);
		Set<Users> userSer = userscard.getUsersSet();
		for (Users users : userSer) {
			users.setUserscard(null);
			totalDao.update(users);
		}
		return totalDao.delete(userscard) + "";
	}

	/***
	 * 送货码登录
	 * 
	 * @param phone
	 * @return
	 */
	@Override
	public String loginByPhone(String phone, Integer id) {
		String hql_sh = "from WaigouDeliveryGoods where shContactsPhone=? and id=?";
		Integer count = totalDao.getCount(hql_sh, phone, id);
		if (count > 0) {
			return "sh";
		}
		String hql_qs = "from WaigouDeliveryGoods where qsContactsPhone=? and id=?";
		Integer count_qs = totalDao.getCount(hql_qs, phone, id);
		if (count_qs > 0) {
			return "qs";
		}
		String hql_ys = "from WaigouDeliveryGoods where ysContactsPhone=? and id=?";
		Integer count_ys = totalDao.getCount(hql_ys, phone, id);
		if (count_ys > 0) {
			return "ys";
		}
		String hql_nb = "from Password where phoneNumber=?";
		Integer count_nb = totalDao.getCount(hql_nb, phone);
		if (count_nb > 0) {
			String hql = "from Users where password.id in (select id from Password where phoneNumber=?)";
			Users user = (Users) totalDao.getObjectByCondition(hql, phone);// 查询手机号是否存在
			// 记入session监听器
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute(TotalDao.users, user);
			// // 保存用户到session中
			ActionContext.getContext().getSession().put(TotalDao.users, user);
			return "nb";
		}

		return null;
	}

	@Override
	public boolean updateUsersCard(UsersCard userscard) {
		if (userscard != null && userscard.getId() > 0) {
			UsersCard olduserscard = (UsersCard) totalDao.get(UsersCard.class,
					userscard.getId());
			olduserscard.setGroupcalass(userscard.getGroupcalass());
			return totalDao.update(olduserscard);
		}
		return false;
	}

	@Override
	public void daoruLizhi(File lizhiUsers) {
		String msg = "true";
		boolean flag = true;
		String fileName = Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int i = 0;
		try {
			FileCopyUtils.copy(lizhiUsers, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}

			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 2) {
				int count = 0;
				int succsesscount = 0;
				StringBuffer str = new StringBuffer();
				for (i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String code = cells[0].getContents();// 工号
					if (code != null && code.length() > 0) {
						Users users = (Users) totalDao.getObjectByCondition(
								" from Users where code = ? ", code);
						if(users!=null){
							users.setOnWork("离职");
							users.setLeaveDate(Util.StringToDate(Util.getDateTime(), "yyyy-mm-dd HH:MM:ss"));
							users.setCardId(null);
							totalDao.update(users);
							Careertrack ck =	(Careertrack) totalDao.getObjectByCondition(" from Careertrack where userId = ? ", users.getId());
							if(ck!=null){
								ck.setStatus("离职");
								ck.setLizhiTime(Util.getDateTime());
								totalDao.update(ck);
							}
							
						}
						
					}
				}
				is.close();// 关闭流
				wk.close();// 关闭工作薄
				if (count == 0) {
					msg = "导入成功";
				} else {
					msg = "<br>导入成功" + succsesscount + "行,导入失败" + count
							+ "行，失败信息如下:</br>" + str.toString();
				}
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "导入失败,请检查文件格式是否正确!" + e.getMessage();
			e.printStackTrace();
		}

	}

	@Override
	public void addUsersLoginLog(Users user) {
		if(user!=null){
			user.setLoginCount(user.getLoginCount()==null?1:user.getLoginCount()+1);
			totalDao.update(user);
			String time = Util.getDateTime("yyyy-MM-dd");
			UsersLoginLog usersLoginLog =(UsersLoginLog) totalDao.getObjectByCondition(" from UsersLoginLog where userId=? and dataTime =? ", user.getId(),time);
			if(usersLoginLog==null){
				usersLoginLog = new UsersLoginLog();
				usersLoginLog.setUserId(user.getId());
				usersLoginLog.setUserCode(user.getCode());
				usersLoginLog.setUserName(user.getName());
				usersLoginLog.setUserCardId(user.getCardId());
				usersLoginLog.setSex(user.getSex());
				usersLoginLog.setDept(user.getDept());
				usersLoginLog.setDuty(user.getDuty());
				usersLoginLog.setPost(user.getPost());
				usersLoginLog.setJobtitle(user.getJobtitle());
				usersLoginLog.setDataTime(time);
				usersLoginLog.setFirstLoginTime(Util.getDateTime());
				usersLoginLog.setLoginIP(user.getLoginIp());
				totalDao.save2(usersLoginLog);
			}
		}
		
	}

	@Override
	public boolean updateOnlineLong(Integer id) {
		if(id!=null){
			String time = Util.getDateTime("yyyy-MM-dd");
			UsersLoginLog usersLoginLog =(UsersLoginLog) totalDao.getObjectByCondition(" from UsersLoginLog where userId=? and dataTime =? ", id,time);
			if(usersLoginLog!=null){
				try {
					Long onlineLong =Util.getDateDiff(usersLoginLog.getFirstLoginTime(), "yyyy-MM-dd HH:mm:ss", Util.getDateTime(), "yyyy-MM-dd HH:mm:ss");
					int whenOnlineLong =(int) Math.ceil(onlineLong/60);
					usersLoginLog.setWhenOnlineLong(whenOnlineLong);
					Users user =	(Users) totalDao.get(Users.class, id);
					totalDao.update2(usersLoginLog);
					float	whenOnlineLong1 =	(Float) totalDao.getObjectByCondition("select sum(whenOnlineLong) from  UsersLoginLog where userId =? ", id);
					user.setWhenOnlineLong((int)whenOnlineLong1);
					return totalDao.update2(user);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public List<Users> getgetAllName(String name) {
		// TODO Auto-generated method stub
		List<Users> users = totalDao.query("from Users where name like '%"+name+"%'");
		if(users!=null&&users.size()>0){
			List<Users> userList = new ArrayList<Users>();
			for(Users user:users){
				Users u=new Users();
				u.setId(user.getId());
				u.setName(user.getName());
				u.setCode(user.getCode());
				u.setDept(user.getDept());
				userList.add(u);
			}
			return userList;
		}
		return null;
	}

	@Override
	public boolean findadmin() {
		// TODO Auto-generated method stub
		return ProcardBlServerImpl.SystemShezhi("PEBS考勤机");
	}

	@Override
	public boolean updateHSKaoqin(Users user) {
		// TODO Auto-generated method stub
		UserFacialInforServerImpl.addUserCode(user);// 添加浩顺考勤对接表
		return true;
	}

	@Override
	public boolean addRank(Rank rank) {
		if(rank!=null) {
			return totalDao.save(rank);
		}
		return false;
	}

	@Override
	public List<Rank> findRankByCond(Rank rank, String pageStatus) {
		
		if(rank==null) {
			rank = new Rank();
		}else {
			rank.setId(null);
		}
		String hql = totalDao.criteriaQueries(rank, null);
		return totalDao.query(hql);
	}

	@Override
	public boolean deleteRank(Integer id) {
		if(id!=null) {
			Rank rank = (Rank) totalDao.getObjectById(Rank.class, id);
			if(rank!=null) {
				return totalDao.delete(rank);
			}
		}
		return false;
	}

}
