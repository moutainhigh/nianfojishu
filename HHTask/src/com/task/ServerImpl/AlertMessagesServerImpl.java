package com.task.ServerImpl;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.AlertMessagesServer;
import com.task.entity.AlertMessages;
import com.task.entity.ModuleFunction;
import com.task.entity.Users;
import com.task.entity.system.Logging;
import com.task.entity.system.UsersUpdateLogging;
import com.task.util.RtxUtil;
import com.task.util.SendMail;
import com.task.util.Util;

/***
 * 系统提醒消息Server层实现类
 * 
 * @author 刘培
 * 
 */
public class AlertMessagesServerImpl implements AlertMessagesServer {

	// private static String pebsUrl = "http://pebs.i-brights.cn/";
	public static String pebsUrl = "";// http://task.shhhes.com/
	public static String shepi = "点击进入审批";
//	public static String shepi = "零参科技";// http://task.shhhes.com/
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public AlertMessagesServerImpl() {

	}

	/**
	 * 添加系统提醒消息 (调用AlertMessagesServer alertMessagesServer = new
	 * AlertMessagesServerImpl( "工资审核 ", "绩效考核成绩审核", "1");)
	 * 
	 * @author 刘培
	 * 
	 * @param functionName
	 *            功能名称
	 * @param content
	 *            消息内容
	 * @param messageType
	 *            消息类型("1"="您有新的审核提醒消息"， 其他=消息标题)
	 * 
	 */
	public AlertMessagesServerImpl(String functionName, String content,
			String messageType) {
		addAlertMessages(functionName, content, messageType);
	}

	// 发送提醒消息统一处理
	@SuppressWarnings("unchecked")
	private static void sendMessage(ModuleFunction mf, String content,
			String messageType, ShortMessageServiceImpl sms, List list,
			TotalDao totalDao) {
		Users loginUser = Util.getLoginUser();// 获得登录用户信息
		if (loginUser == null) {
			try {
				loginUser = (Users) totalDao
						.getObjectByQuery("from Users where code='admin'");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List codeList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Users user = (Users) list.get(i);
			AlertMessages alertMessages = new AlertMessages();
			alertMessages.setReceiveuserId(user.getId());// 接收用户id
			alertMessages.setSendUserId(loginUser.getId());// 发送用户id
			alertMessages.setSendUserName(loginUser.getName());// 发送用户名称
			if (loginUser.getPassword() != null)// 如果存在头像，则保存
				alertMessages.setSendUserImg("upload/user/"
						+ loginUser.getPassword().getPicture());// 发送用户头像
			alertMessages.setContent(content);// 内容
			alertMessages.setFunctionId(mf.getId());// 功能id
			alertMessages.setFunctionName(mf.getFunctionName());// 功能名称
			alertMessages.setFunctionUrl(mf.getFunctionLink());// 功能地址
			alertMessages.setAddTime(Util.getDateTime(null));// 添加时间
			alertMessages.setReadStatus("no");// 阅读状态设置为未读
			if ("1".equals(messageType)) {
				alertMessages.setTitle(mf.getFunctionName());
				alertMessages.setContent(content + "  发送人:"
						+ loginUser.getName());// 内容
				alertMessages.setMessageType(messageType);// 消息类型

			} else if ("2".equals(messageType)) {
				alertMessages.setTitle("系统消息");
				alertMessages.setSendUserName("系统消息");// 发送用户名称
				alertMessages.setSendUserImg("images/logo.jpg");// 发送用户头像
				alertMessages.setSendUserId(000);// 发送用户id
				alertMessages.setMessageType(messageType);// 消息类型
			} else {
				alertMessages.setTitle(messageType);
				alertMessages.setMessageType("other");// 消息类型
			}
			boolean bool = totalDao.save(alertMessages);// 保存系统消息
			if (bool) {
				codeList.add(user.getCode());
			}
		}
		if (codeList.size() > 0) {
			RtxUtil.sendNotify(codeList, content + "  \n\t\t\t 发送人:"
					+ loginUser.getName() + " \n\t PEBS系统("+shepi+"):" + pebsUrl
					+ mf.getFunctionLink(), loginUser.getName()
					+ "给您发送了一条提醒消息:" + content, "0", "0");
		}
	}

	// 系统发送提醒消息统一处理
	@SuppressWarnings("unchecked")
	private static void sendMessage1(ModuleFunction mf, String content,
			String messageType, ShortMessageServiceImpl sms, List list,
			TotalDao totalDao) {
		List codeList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Users user = (Users) list.get(i);
			AlertMessages alertMessages = new AlertMessages();
			alertMessages.setReceiveuserId(user.getId());// 接收用户id
			alertMessages.setSendUserId(0);// 发送用户id
			alertMessages.setSendUserName("系统");// 发送用户名称
			alertMessages.setContent(content);// 内容
			alertMessages.setFunctionId(mf.getId());// 功能id
			alertMessages.setFunctionName(mf.getFunctionName());// 功能名称
			alertMessages.setFunctionUrl(mf.getFunctionLink());// 功能地址
			alertMessages.setAddTime(Util.getDateTime(null));// 添加时间
			alertMessages.setReadStatus("no");// 阅读状态设置为未读
			if ("1".equals(messageType)) {
				alertMessages.setTitle("您有新的审核提醒消息");
				alertMessages.setContent(content + "  发送人:" + "系统");// 内容
				alertMessages.setMessageType(messageType);// 消息类型

			} else if ("2".equals(messageType)) {
				alertMessages.setTitle("系统消息");
				alertMessages.setSendUserName("系统消息");// 发送用户名称
				alertMessages.setSendUserImg("images/logo.jpg");// 发送用户头像
				alertMessages.setSendUserId(000);// 发送用户id
				alertMessages.setMessageType(messageType);// 消息类型
			} else {
				alertMessages.setTitle(messageType);
				alertMessages.setMessageType("other");// 消息类型
			}
			boolean bool = totalDao.save(alertMessages);// 保存系统消息
			if (bool) {
				// codeList.add(user.getCode());
				RtxUtil.sendNotify(user.getCode(), content + "  \n\t\t\t 发送人:"
						+ "系统" + " \n\t PEBS系统("+shepi+"):" + pebsUrl
						+ mf.getFunctionLink(),
						"系统" + "给您发送了一条提醒消息:" + content, "0", "0");
			}
		}
		// if (codeList.size() > 0) {
		// RtxUtil.sendNotify(codeList, content + "  \n\t\t\t 发送人:" + "系统"
		// + " \n\t PEBS系统("+shepi+"):" + pebsUrl + mf.getFunctionLink(),
		// "系统" + "给您发送了一条提醒消息:" + content, "0", "0");
		// }
	}

	/**
	 * 添加系统提醒消息 (调用String message=AlertMessagesServerImpl.addAlertMessages(
	 * "工资审核 ", "绩效考核成绩审核", "1");)
	 * 
	 * @author 刘培
	 * 
	 * @param functionName
	 *            功能名称
	 * @param content
	 *            消息内容
	 * @param messageType
	 *            消息类型("1"="您有新的审核提醒消息"， 其他=消息标题)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String addAlertMessages(String functionName, String content,
			String messageType) {
		try {
			// 获得totalDao
			TotalDao totalDao = TotalDaoImpl.findTotalDao();
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			sms.setTotalDao(totalDao);
			// 开始处理逻辑
			String message = "";
			if (functionName != null && functionName.length() > 0) {// 检查功能名称
				if (content != null && content.length() > 0) {// 检查内容
					if (messageType != null && messageType.length() > 0) {// 检查消息类型
						String hql = "from ModuleFunction where functionName=?";
						ModuleFunction mf = (ModuleFunction) totalDao
								.getObjectByQuery(hql, functionName);// 根据功能名称查询所属功能
						if (mf != null) {
							String hql2 = "from Users where id in (select u.id from Users u join u.moduleFunction f  where f.id =?)";
							List<Users> list = totalDao.query(hql2, mf.getId());
							if (list != null && list.size() > 0) {
								sendMessage(mf, content, messageType, sms,
										list, totalDao);
							} else {
								message = "功能名称:" + functionName + "无绑定人员";
							}
						} else {
							message = "功能名称:" + functionName + "不存在!";
						}
					} else {
						message = "消息类型错误!";
					}
				} else {
					message = "消息内容不能空!";
				}
			} else {
				message = "请填写功能名字";
			}
			if (message.length() > 0) {
				addAlertMessages("系统维护异常组", message, "1");
			}
			return message;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 系统发送 添加系统提醒消息 (调用String message=AlertMessagesServerImpl.addAlertMessages(
	 * "工资审核 ", "绩效考核成绩审核", "1");)
	 * 
	 * @author 刘培
	 * 
	 * @param functionName
	 *            功能名称
	 * @param content
	 *            消息内容
	 * @param messageType
	 *            消息类型("1"="您有新的审核提醒消息"， 其他=消息标题)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String addAlertMessagesxt(String functionName,
			String content, String messageType) {
		try {
			// 获得totalDao
			TotalDao totalDao = TotalDaoImpl.findTotalDao();
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			sms.setTotalDao(totalDao);
			// 开始处理逻辑
			String message = "";
			if (functionName != null && functionName.length() > 0) {// 检查功能名称
				if (content != null && content.length() > 0) {// 检查内容
					if (messageType != null && messageType.length() > 0) {// 检查消息类型
						String hql = "from ModuleFunction where functionName=?";
						ModuleFunction mf = (ModuleFunction) totalDao
								.getObjectByQuery(hql, functionName);// 根据功能名称查询所属功能
						if (mf != null) {
							String hql2 = "from Users where id in (select u.id from Users u join u.moduleFunction f  where f.id =?)  ";
							List<Users> list = totalDao.query(hql2, mf.getId());
							if (list != null && list.size() > 0) {
								sendMessage1(mf, content, messageType, sms,
										list, totalDao);
							} else {
								message = "功能名称:" + functionName + "无绑定人员";
							}
						} else {
							message = "功能名称:" + functionName + "不存在!";
						}
					} else {
						message = "消息类型错误!";
					}
				} else {
					message = "消息内容不能空!";
				}
			} else {
				message = "请填写功能名字";
			}
			if (message.length() > 0) {
				addAlertMessages("系统维护异常组", message, "1");
			}
			return message;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 对单个人添加系统提醒消息
	 * 
	 * @author 刘培
	 * 
	 * @param functionName
	 *            功能名称
	 * @param content
	 *            消息内容
	 * @param messageType
	 *            消息类型("1"="您有新的审核提醒消息"， 其他=消息标题)
	 * @param code
	 *            单独发送人的工号
	 */
	@SuppressWarnings("unchecked")
	public static String addAlertMessages(String functionName, String content,
			String messageType, String code) {
		try {
			// 获得totalDao
			TotalDao totalDao = TotalDaoImpl.findTotalDao();
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			sms.setTotalDao(totalDao);
			// 开始处理逻辑
			String message = "";
			if (functionName != null && functionName.length() > 0) {// 检查功能名称
				if (content != null && content.length() > 0) {// 检查内容
					if (messageType != null && messageType.length() > 0) {// 检查消息类型
						String hql = "from ModuleFunction where functionName=?";
						ModuleFunction mf = (ModuleFunction) totalDao
								.getObjectByQuery(hql, functionName);// 根据功能名称查询所属功能
						if (mf != null) {
							String hql2 = "from Users where code=?";
							List<Users> list = totalDao.query(hql2, code);
							if (list != null && list.size() > 0) {
								sendMessage("1", content, sms, list, totalDao,
										mf.getFunctionLink());
							} else {
								message = "功能名称:" + functionName + "无绑定人员";
							}
						} else {
							message = "功能名称:" + functionName + "不存在!";
						}
					} else {
						message = "消息类型错误!";
					}
				} else {
					message = "消息内容不能空!";
				}
			} else {
				message = "请填写功能名字";
			}
			if (message.length() > 0) {
				addAlertMessages("系统维护异常组", message, "1");
			}

			return message;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 对单个人添加系统提醒消息并且指定手机号
	 * 
	 * @author 刘培
	 * 
	 * @param functionName
	 *            功能名称
	 * @param content
	 *            消息内容
	 * @param messageType
	 *            消息类型("1"="您有新的审核提醒消息"， 其他=消息标题)
	 * @param code
	 *            单独接收短信人的工号
	 * @param phone
	 *            指定手机号
	 */
	@SuppressWarnings("unchecked")
	public static String addAlertMessages(String functionName, String content,
			String messageType, String code, String phone) {
		try {
			// 获得totalDao
			TotalDao totalDao = TotalDaoImpl.findTotalDao();
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			sms.setTotalDao(totalDao);
			// 开始处理逻辑
			String message = "";
			if (functionName != null && functionName.length() > 0) {// 检查功能名称
				if (content != null && content.length() > 0) {// 检查内容
					if (messageType != null && messageType.length() > 0) {// 检查消息类型
						String hql = "from ModuleFunction where functionName=?";
						ModuleFunction mf = (ModuleFunction) totalDao
								.getObjectByQuery(hql, functionName);// 根据功能名称查询所属功能
						if (mf != null) {
							String hql2 = "from Users where code=?";
							List<Users> list = totalDao.query(hql2, code);
							if (list != null && list.size() > 0) {
								Users loginUser = Util.getLoginUser();// 获得登录用户信息
								for (int i = 0; i < list.size(); i++) {
									Users user = list.get(i);
									AlertMessages alertMessages = new AlertMessages();
									alertMessages
											.setReceiveuserId(user.getId());// 接收用户id
									alertMessages.setSendUserId(loginUser
											.getId());// 发送用户id
									alertMessages.setSendUserName(loginUser
											.getName());// 发送用户名称
									if (loginUser.getPassword() != null)// 如果存在头像，则保存
										alertMessages
												.setSendUserImg("upload/user/"
														+ loginUser
																.getPassword()
																.getPicture());// 发送用户头像
									alertMessages.setContent(content);// 内容
									alertMessages.setFunctionId(mf.getId());// 功能id
									alertMessages.setFunctionName(mf
											.getFunctionName());// 功能名称
									alertMessages.setFunctionUrl(mf
											.getFunctionLink());// 功能地址
									alertMessages.setAddTime(Util
											.getDateTime(null));// 添加时间
									alertMessages.setReadStatus("no");// 阅读状态设置为未读
									if ("1".equals(messageType)) {
										alertMessages.setTitle("您有新的审核提醒消息");
										alertMessages.setContent(content
												+ "  发送人:"
												+ loginUser.getName());// 内容
										alertMessages
												.setMessageType(messageType);// 消息类型

									} else if ("2".equals(messageType)) {
										alertMessages.setTitle("系统消息");
										alertMessages.setSendUserName("系统消息");// 发送用户名称
										alertMessages
												.setSendUserImg("images/logo.jpg");// 发送用户头像
										alertMessages.setSendUserId(000);// 发送用户id
										alertMessages
												.setMessageType(messageType);// 消息类型
									} else {
										alertMessages.setTitle(messageType);
										alertMessages.setMessageType("other");// 消息类型
									}
									boolean bool = totalDao.save(alertMessages);// 保存消息
									if (bool) {
										// if (user.getPassword() != null)
										// sms.send(phone, content + "  发送人:"
										// + loginUser.getName());
										// 发送rtx消息
										if (user.getPassword() != null) {
											RtxUtil
													.sendNotify(
															user.getCode(),
															content
																	+ "  \n\t\t\t 发送人:"
																	+ loginUser
																			.getName()
																	+ " \n\t PEBS系统("+shepi+"):"
																	+ pebsUrl
																	+ alertMessages
																			.getFunctionUrl(),
															loginUser.getName()
																	+ "给您发送了一条提醒消息:"
																	+ content,
															"0", "0");
										}
									}
								}
							} else {
								message = "功能名称:" + functionName + "无绑定人员";
							}
						} else {
							message = "功能名称:" + functionName + "不存在!";
						}
					} else {
						message = "消息类型错误!";
					}
				} else {
					message = "消息内容不能空!";
				}
			} else {
				message = "请填写功能名字";
			}
			if (message.length() > 0) {
				Users user = (Users) ActionContext.getContext().getSession()
						.get(TotalDao.users);
				addAlertMessages("系统维护异常组", user.getName() + "在发送提醒时出错!出错内容:"
						+ message + "<br/>出错时间:" + Util.getDateTime(null), "1");
			}

			return message;
		} catch (Exception e) {
			return null;
		}

	}

	// 发送提醒消息统一处理(不和功能绑定)
	@SuppressWarnings("unchecked")
	private static void sendMessage(String messageType, String content,
			ShortMessageServiceImpl sms, List list, TotalDao totalDao,
			String url) {
		Users loginUser = Util.getLoginUser();// 获得登录用户信息
		for (int i = 0; i < list.size(); i++) {
			Users user = (Users) list.get(i);
			AlertMessages alertMessages = new AlertMessages();
			alertMessages.setReceiveuserId(user.getId());// 接收用户id
			if (loginUser != null) {
				alertMessages.setSendUserId(loginUser.getId());// 发送用户id
				alertMessages.setSendUserName(loginUser.getName());// 发送用户名称
				if (loginUser.getPassword() != null)// 如果存在头像，则保存
					alertMessages.setSendUserImg("upload/user/"
							+ loginUser.getPassword().getPicture());// 发送用户头像
			}else{
				loginUser=new Users();
				loginUser.setName("系统");
			}
			alertMessages.setContent(content);// 内容
			alertMessages.setFunctionUrl(url);// 功能地址
			alertMessages.setAddTime(Util.getDateTime(null));// 添加时间
			alertMessages.setReadStatus("no");// 阅读状态设置为未读
			if ("1".equals(messageType)) {
				alertMessages.setTitle("您有新的审核提醒消息");
				alertMessages.setContent(content + "  发送人:"
						+ loginUser.getName());// 内容
				alertMessages.setMessageType(messageType);// 消息类型

			} else if ("2".equals(messageType)) {
				alertMessages.setTitle("系统消息");
				alertMessages.setSendUserName("系统消息");// 发送用户名称
				alertMessages.setSendUserImg("images/logo.jpg");// 发送用户头像
				alertMessages.setSendUserId(000);// 发送用户id
				alertMessages.setMessageType(messageType);// 消息类型
			} else {
				alertMessages.setTitle(messageType);
				alertMessages.setMessageType("other");// 消息类型
			}
			boolean bool = totalDao.save(alertMessages);// 保存消息
			if (bool) {
				// if (user.getPassword() != null && sms != null) {
				// sms.send(user.getPassword().getPhoneNumber(), content
				// + "  发送人:" + loginUser.getName());
				// }
				// 发送rtx消息
				if (user.getPassword() != null && loginUser != null) {
					RtxUtil.sendNotify(user.getCode(), content
							+ "  \n\t\t\t 发送人:" + loginUser.getName()
							+ " \n\tPEBS系统("+shepi+"):" + pebsUrl
							+ alertMessages.getFunctionUrl(), loginUser
							.getName()
							+ "给您发送了一条提醒消息:" + content, "0", "0");
				}
			}
		}
	}

	/***
	 * // 发送提醒消息统一处理(RTX消息)(不和功能绑定)
	 */
	@SuppressWarnings("unchecked")
	private static String sendMessage(String messageType, String content,
			List list, TotalDao totalDao, String url, String readStatus) {
		Users loginUser = Util.getLoginUser();// 获得登录用户信息
		String messageIds = "0";
		for (int i = 0; i < list.size(); i++) {
			Users user = (Users) list.get(i);
			AlertMessages alertMessages = new AlertMessages();
			alertMessages.setReceiveuserId(user.getId());// 接收用户id
			if (loginUser != null) {
				alertMessages.setSendUserId(loginUser.getId());// 发送用户id
				alertMessages.setSendUserName(loginUser.getName());// 发送用户名称
				if (loginUser.getPassword() != null)// 如果存在头像，则保存
					alertMessages.setSendUserImg("upload/user/"
							+ loginUser.getPassword().getPicture());// 发送用户头像
			}
			if(content.length()>=500){
				content = content.substring(0, 55);
			}
			alertMessages.setContent(content);// 内容
			alertMessages.setFunctionUrl(url);// 功能地址
			alertMessages.setAddTime(Util.getDateTime(null));// 添加时间
			alertMessages.setReadStatus(readStatus);// 阅读状态设置为未读
			if ("1".equals(messageType)) {
				alertMessages.setTitle(content);
				alertMessages.setContent(content + "  发送人:"
						+ loginUser.getName());// 内容
				alertMessages.setMessageType(messageType);// 消息类型

			} else if ("2".equals(messageType)) {
				alertMessages.setTitle("系统消息");
				alertMessages.setSendUserName("系统消息");// 发送用户名称
				alertMessages.setSendUserImg("images/logo.jpg");// 发送用户头像
				alertMessages.setSendUserId(000);// 发送用户id
				alertMessages.setMessageType(messageType);// 消息类型
			} else {
				alertMessages.setTitle(messageType);
				alertMessages.setMessageType("other");// 消息类型
			}
			boolean bool = totalDao.save(alertMessages);// 保存消息
			// 发送RTX系统消息
			if (bool) {
				if (user.getPassword() != null) {
					String sendName = "";
					if (loginUser == null) {
						sendName = "系统";
					} else {
						sendName = loginUser.getName();
					}
					if("1".equals(messageType)||"2".equals(messageType)){
						RtxUtil.sendNotify(user.getCode(), content
								+ "  \n\t\t\t 发送人:" + sendName
								+ " \n\tPEBS系统("+shepi+"):" + pebsUrl
								+ alertMessages.getFunctionUrl(), sendName
								+ "给您发送了一条提醒消息:" + content, "0", "0");
					}else{
						RtxUtil.sendNotify(user.getCode(), content
								+ "  \n\t\t\t 发送人:" + sendName
								+ " \n\tPEBS系统("+shepi+"):" + pebsUrl
								+ alertMessages.getFunctionUrl(),  messageType, "0", "0");
					}
				}
			}
			messageIds += "," + alertMessages.getId();
		}
		return messageIds;
	}

	/***
	 * 添加一次性提醒消息(rtx+系统提醒)
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param userId
	 *            用户id
	 * @param url
	 *            访问地址
	 * @param isMessage
	 *            是否发送短信息
	 * @return
	 */
	public static String addAlertMessages(String title, String content,
			Integer[] userId, String url, boolean isMessage) {
		if (isMessage) {
			try {
				// 获得totalDao
				TotalDao totalDao = TotalDaoImpl.findTotalDao();
				// 短信接口，暂时封闭
				// ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
				// if (isMessage) {
				// sms.setTotalDao(totalDao);
				// } else {
				// sms = null;
				// }
				// 开始处理逻辑
				String message = "";
				List<Users> list = new ArrayList<Users>();
				for (int i = 0; i < userId.length; i++) {
					if (userId[i] != null) {
						Users user = (Users) totalDao.getObjectById(
								Users.class, userId[i]);
						if (user != null) {
							list.add(user);
						}
					}
				}
				if (list != null && list.size() > 0) {
					// sendMessage(title, content, sms, list, totalDao,
					// url);//短消息接口
					String messageIds = sendMessage(title, content, list,
							totalDao, url, "no");// RTX接口
					return messageIds;
				}
				if (message.length() > 0) {
					Users user = (Users) ActionContext.getContext()
							.getSession().get(TotalDao.users);
					// 发送邮件提醒
					SendMail sendMail = new SendMail();
					sendMail.sendMail(sendMail.spareMail2, "提醒消息出错    --红湖作业网",
							user.getName() + "在发送提醒时出错!出错内容:" + message
									+ "<br/>出错时间:" + Util.getDateTime(null));
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	/***
	 * 添加提醒消息(rtx+系统提醒)
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param userId
	 *            用户id
	 * @param url
	 *            访问地址
	 * @param isMessage
	 *            是否发送短信息
	 * @return
	 */
	public static String addAlertMessages(String title, String content,
			Integer[] userId, String url, boolean isMessage, String readStatus) {
		if (isMessage) {
			try {
				// 获得totalDao
				TotalDao totalDao = TotalDaoImpl.findTotalDao();
				// 短信接口，暂时封闭
				// ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
				// if (isMessage) {
				// sms.setTotalDao(totalDao);
				// } else {
				// sms = null;
				// }
				// 开始处理逻辑
				String message = "";
				List<Users> list = new ArrayList<Users>();
				for (int i = 0; i < userId.length; i++) {
					if (userId[i] != null) {
						Users user = (Users) totalDao.getObjectById(
								Users.class, userId[i]);
						if (user != null) {
							list.add(user);
						}
					}
				}
				if (list != null && list.size() > 0) {
					// sendMessage(title, content, sms, list, totalDao,
					// url);//短消息接口
					String messageIds = sendMessage(title, content, list,
							totalDao, url, readStatus);// RTX接口
					return messageIds;
				}
				if (message.length() > 0) {
					Users user = (Users) ActionContext.getContext()
							.getSession().get(TotalDao.users);
					// 发送邮件提醒
					SendMail sendMail = new SendMail();
					sendMail.sendMail(sendMail.spareMail2, "提醒消息出错    --红湖作业网",
							user.getName() + "在发送提醒时出错!出错内容:" + message
									+ "<br/>出错时间:" + Util.getDateTime(null));
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	/***
	 * 添加提醒消息
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param userId
	 *            用户id
	 * @param url
	 *            访问地址
	 * @param isMessage
	 *            是否发送短信息
	 * @param isEntry
	 *            是否跟踪消息，后续自动已读
	 * @return
	 */
	public static Integer addAlertMessages(String title, String content,
			Integer[] userId, String url, boolean isMessage, boolean isEntry) {
		try {
			// 获得totalDao
			TotalDao totalDao = TotalDaoImpl.findTotalDao();
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			if (isMessage) {
				sms.setTotalDao(totalDao);
			} else {
				sms = null;
			}
			// 开始处理逻辑
			String message = "";
			List<Users> list = new ArrayList<Users>();
			for (int i = 0; i < userId.length; i++) {
				Users user = (Users) totalDao.getObjectById(Users.class,
						userId[i]);
				if (user != null) {
					list.add(user);
				} else {
					message = "用户id错误";
				}
			}
			if (list != null && list.size() > 0) {
				sendMessage(title, content, sms, list, totalDao, url);
			}
			if (message.length() > 0) {
				Users user = (Users) ActionContext.getContext().getSession()
						.get(TotalDao.users);
				// 发送邮件提醒
				SendMail sendMail = new SendMail();
				sendMail.sendMail(sendMail.spareMail2, "提醒消息出错    --红湖作业网",
						user.getName() + "在发送提醒时出错!出错内容:" + message
								+ "<br/>出错时间:" + Util.getDateTime(null));
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/***
	 * 添加提醒消息
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param userId
	 *            用户id
	 * @param url
	 *            访问地址
	 * @param isMessage
	 *            是否发送短信息
	 * @param isEntry
	 *            是否跟踪消息，后续自动已读
	 * @return
	 */
	public static Integer addAlertMessages(String title, String content,
			Integer[] userId, String url, boolean isMessage, boolean isEntry,TotalDao totalDao) {
		try {
			ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
			if (isMessage) {
				sms.setTotalDao(totalDao);
			} else {
				sms = null;
			}
			// 开始处理逻辑
			String message = "";
			List<Users> list = new ArrayList<Users>();
			for (int i = 0; i < userId.length; i++) {
				Users user = (Users) totalDao.getObjectById(Users.class,
						userId[i]);
				if (user != null) {
					list.add(user);
				} else {
					message = "用户id错误";
				}
			}
			if (list != null && list.size() > 0) {
				sendMessage(title, content, sms, list, totalDao, url);
			}
			if (message.length() > 0) {
				Users user = (Users) ActionContext.getContext().getSession()
						.get(TotalDao.users);
				// 发送邮件提醒
				SendMail sendMail = new SendMail();
				sendMail.sendMail(sendMail.spareMail2, "提醒消息出错    --红湖作业网",
						user.getName() + "在发送提醒时出错!出错内容:" + message
								+ "<br/>出错时间:" + Util.getDateTime(null));
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***
	 *查询个人提醒消息
	 * 
	 * @param countOrList
	 *            查询个数或集合(count/list)
	 * @param readStatus
	 *            查询已读或未读(yes/no)
	 * @return
	 */
	public Object findAMByUserId(String countOrList, String readStatus) {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);
		if (user != null) {
			String hql = "from AlertMessages where receiveuserId=? and readStatus=?";
			if (countOrList != null && countOrList.length() > 0
					&& readStatus != null && readStatus.length() > 0
					&& "yes".equals(readStatus) || "no".equals(readStatus)) {
				if ("count".equals(countOrList)) {
					return totalDao.getCount(hql, user.getId(), readStatus);// 查询所有未读消息个数
				} else if ("list".equals(countOrList)) {
					return totalDao.query(hql, user.getId(), readStatus);// 查询所有未读消息
				}
			}
		}
		return null;
	}

	/****
	 * 查询登录信息
	 * 
	 * @param alertMessages
	 *            提醒消息
	 * @param pageNo
	 *            页数
	 * @param pageSize
	 *            每页个数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object[] findAlertMessagesByCondition(AlertMessages alertMessages,
			int pageNo, int pageSize) {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);// 获得登录信息
		if (user != null) {
			if (alertMessages == null) {
				alertMessages = new AlertMessages();
			}
			// alertMessages.setReceiveuserId(user.getId());// 查询个人信息
			String hql = totalDao.criteriaQueries(alertMessages, null);
			if (hql.indexOf(" where ") > 0) {
				hql += " and  receiveuserId=?  order by addTime desc";
			} else {
				hql += " where  receiveuserId=?  order by addTime desc";
			}

			List list = totalDao.findAllByPage(hql, pageNo, pageSize, user
					.getId());
			int count = totalDao.getCount(hql, user.getId());
			Object[] o = { list, count };
			return o;
		}
		return null;
	}
	public Object[] findAlertMessagesByCondition1(AlertMessages alertMessages,
			int pageNo, int pageSize) {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);// 获得登录信息
		if (user != null) {
			if (alertMessages == null) {
				alertMessages = new AlertMessages();
			}
			String hql ="from AlertMessages where readStatus ='no' and  receiveuserId=?  order by addTime desc";

			List<AlertMessages> list = totalDao.findAllByPage(hql, pageNo, pageSize, user
					.getId());
			if(list.size()==0){
				AlertMessages messages=new AlertMessages();
				messages.setFunctionUrl("javascripit:void(0)");
				messages.setContent("今日没有任务");
				list.add(messages);
			}
			
			
			Object[] o = { list};
			return o;
		}
		return null;
	}
	/****
	 * 查询登录人的消息提醒(android接口)
	 * 
	 * @param alertMessages
	 *            提醒消息
	 * @param pageNo
	 *            页数
	 * @param pageSize
	 *            每页个数
	 * @return
	 */
	@Override
	public Object[] findAMForAndroid(AlertMessages alertMessages, int pageNo,
			int pageSize) {
		if (alertMessages == null) {
			alertMessages = new AlertMessages();
		}
		String hql = totalDao.criteriaQueries(alertMessages, null)
				+ " order by addTime desc";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/***
	 * 将所有未读信息设为已读
	 */
	public boolean updateAlertMessagesByWeidu() {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);// 获得登录信息
		if (user != null) {
			AlertMessages alertMessages = new AlertMessages();
			// alertMessages.setReceiveuserId(user.getId());// 查询个人信息
			String hql = totalDao.criteriaQueries(alertMessages, null);
			hql += " and  receiveuserId=? and readStatus='no'";
			List list = totalDao.query(hql, user.getId());
			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					alertMessages = (AlertMessages) list.get(i);
					if (alertMessages != null) {
						if (alertMessages.getReadStatus().equals("no")) {
							alertMessages.setReadStatus("yes");// 将消息状态更新为已读
							this.updateAlertMessages(alertMessages);
						}
					}
				}
			}

		}
		return false;
	}

	/***
	 * 根据id查询提醒消息
	 * 
	 * @param id
	 * @return
	 */
	public AlertMessages findAlertMessagesById(int id) {
		if ((Object) id != null && id > 0)
			return (AlertMessages) totalDao.getObjectById(AlertMessages.class,
					id);
		return null;
	}

	/***
	 * 修改提醒消息
	 * 
	 * @param alertMessages
	 * @return
	 */
	public boolean updateAlertMessages(AlertMessages alertMessages) {
		if (alertMessages != null) {
			return totalDao.update(alertMessages);
		}
		return false;
	}

	/***
	 * 删除提醒消息
	 * 
	 * @param alertMessages
	 * @return
	 */
	public boolean delAlertMessages(AlertMessages alertMessages) {
		if (alertMessages != null) {
			return totalDao.delete(alertMessages);
		}
		return false;
	}

	/***
	 * 清空个人消息
	 * 
	 * @return 删除个数
	 */
	public int delAllAlertMessagesByUid() {
		Users user = (Users) ActionContext.getContext().getSession().get(
				TotalDao.users);// 获得登录信息
		String sql = "delete from ta_sys_AlertMessages where  receiveuserId=?";
		return totalDao.createQueryUpdate(null, sql, user.getId());
	}

	/***
	 * 系统日志查询
	 * 
	 * @param logging
	 *            日志对象
	 * @param date
	 *            日期数组
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页个数
	 * @return
	 */
	@Override
	public Object[] findLoggingByCondition(Logging logging, String[] date,
			int pageNo, int pageSize) {
		if (logging == null) {
			logging = new Logging();
		}
		String hql = totalDao.criteriaQueries(logging, "addTime", date, null);
		if (logging.getObjectName() != null
				&& logging.getObjectName().length() > 0) {
			hql += " and objectName = '" + logging.getObjectName() + "'";
		}
		hql += " order by id desc";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/***
	 * 系统个人修改日志查询
	 * 
	 * @param logging
	 *            日志对象
	 * @param date
	 *            日期数组
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页个数
	 * @return
	 */
	@Override
	public Object[] findUserUpdateLogging(UsersUpdateLogging updateLogging,
			String[] date, int pageNo, int pageSize, String tag) {
		if (updateLogging == null) {
			updateLogging = new UsersUpdateLogging();
		}
		String sql = "";
		if ("code".equals(tag)) {
			Users u = Util.getLoginUser();
			sql = "userId = " + u.getId();
		} else if ("all".equals(tag)) {
		} else {
			sql = "userId = 0";
		}
		String hql = totalDao.criteriaQueries(updateLogging, "addTime", date,
				sql);
		if (updateLogging.getObjectName() != null
				&& updateLogging.getObjectName().length() > 0) {
			hql += " and objectName = '" + updateLogging.getObjectName() + "'";
		}
		hql += " order by id desc";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/***
	 * 根据id查询日志
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Logging findLogging(Integer id) {
		if (id != null && id > 0) {
			return (Logging) totalDao.getObjectById(Logging.class, id);
		}
		return null;
	}

	@Override
	public String serUpdateLoggingType() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		List<String> list = totalDao
				.query("select distinct(logTitle) from UsersUpdateLogging");
		for (String d : list) {
			if (d != null) {
				buffer.append(d.toString() + "|");
			}
		}
		return buffer.toString();
	}

	// private static String getpebsurl() {
	// // 获得totalDao
	// TotalDao totalDao1 = TotalDaoImpl.findTotalDao();
	// IntegralServerDaoImpl acc = new IntegralServerDaoImpl();
	// acc.setTotalDao(totalDao1);
	// List<String> pebsurlList = totalDao1
	// .query("select valueCode  from CodeTranslation where type = 'sys' and keycode = 'PEBSURL' ");
	// if (pebsurlList != null && pebsurlList.size() > 0) {
	// pebsUrl = pebsurlList.get(0);
	// }
	// return pebsUrl;
	// }

}
