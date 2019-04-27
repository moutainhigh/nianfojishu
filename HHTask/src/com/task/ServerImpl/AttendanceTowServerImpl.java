package com.task.ServerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.AttendanceTowServer;
import com.task.ServerImpl.banci.BanCiServerImpl;
import com.task.entity.Attendance;
import com.task.entity.AttendanceFu;
import com.task.entity.AttendanceTow;
import com.task.entity.AttendanceTowCollect;
import com.task.entity.Download;
import com.task.entity.Goods;
import com.task.entity.OaAppDetail;
import com.task.entity.Overtime;
import com.task.entity.OvertimeDetail;
import com.task.entity.Store;
import com.task.entity.Users;
import com.task.entity.WarehouseNumber;
import com.task.entity.banci.BanCi;
import com.task.entity.banci.BanCiTime;
import com.task.entity.banci.SchedulingTable;
import com.task.entity.menjin.AccessLogInfor;
import com.task.entity.menjin.AccessRecords;
import com.task.entity.menjin.AttendanceDai;
import com.task.entity.menjin.CarInOutType;
import com.task.entity.menjin.InEmployeeCarInfor;
import com.task.entity.menjin.RandomPunch;
import com.task.entity.menjin.RechargeAll;
import com.task.entity.menjin.RechargeZhi;
import com.task.entity.menjin.SpecialDate;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.system.UsersUpdateLogging;
import com.task.util.Util;
import com.task.util.UtilTong;

@SuppressWarnings("unchecked")
public class AttendanceTowServerImpl implements AttendanceTowServer {
	private TotalDao totalDao;

	// 在静态方法调用totalDao
	private static TotalDao createTotol() {
		// 获得totalDao
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		AttendanceTowServerImpl acc = new AttendanceTowServerImpl();
		acc.setTotalDao(totalDao);
		return totalDao;
	}

	/**
	 * 添加人行道&停车场的断网考情&状态
	 * 
	 * @param accessType
	 *            门禁类型
	 * @param accessName
	 *            门禁名称
	 */
	public static void addDaiAttendance(String accessType, String accessName,
			StringBuffer buffer) {
		TotalDao totalDao = createTotol();
		// 按卡号和id排序
		List<AttendanceDai> Datlist = totalDao
				.query(
						"from AttendanceDai where accessType = ? and status = '待处理' and inouttype in ('进门','出门') order by cardId,id ace",
						accessType);
		if (Datlist != null && Datlist.size() > 0) {
			if ("人行道".equals(accessType)) {// 添加打卡数据
				for (int i = 0; i < Datlist.size(); i++) {
					AttendanceDai dai = Datlist.get(i);
					try {
						if ("true".equals(DownloadServerImpl.AddDownLoad(dai
								.getCardId(), dai.getDadate(), dai.getDatime(),
								dai.getAccessType(), accessName, dai
										.getInouttype())))
							dai.setStatus("yes");
						else
							dai.setStatus("no");
					} catch (Exception e) {
						dai.setStatus("error");
						buffer.append("," + dai.getId() + "down:" + e + "");
					}
					try {
						addAttendanceTow(dai.getCardId(), dai.getDadate(), dai
								.getDatime(), dai.getAccessType(), accessName,
								dai.getInouttype());
					} catch (Exception e) {
						buffer.append("," + dai.getId() + "attend:" + e + "");
					}
					totalDao.update2(dai);
				}
			} else if ("停车场".equals(accessType)) {
				for (int i = 0; i < Datlist.size(); i++) {
					AttendanceDai dai = Datlist.get(i);
					// 先判断是否为最后一条，如不是，与其后一条作对比，如果卡号相等，跳出循环
					if (i != Datlist.size() - 1) {// 不为最后一条
						if (dai.getCardId().equals(
								Datlist.get(i + 1).getCardId()))
							continue;
					}
					String carPai = "";// 车牌号
					// 根据卡号确定车牌号
					List<InEmployeeCarInfor> userList = totalDao
							.query(
									"from InEmployeeCarInfor where carInCangType = '内部' and carType = '个人' and ncardId in (select cardId from Users where cardId = ? and onWork <> '离职')",
									dai.getCardId());
					if (userList != null && userList.size() > 0) {
						if (userList.size() > 1) {// 个人有多辆内部车
							// 根据卡号查找今天进门已通过记录
							List accessRecordList = totalDao
									.query(
											"from AccessRecords where addTime > ? and inmarkId=? and recordStatus = '已通过' order by id desc",
											Util.getDateTime("yyyy-MM-dd"), dai
													.getCardId());
							AccessRecords accessRecords = null;
							if (accessRecordList != null
									&& accessRecordList.size() > 0)
								accessRecords = (AccessRecords) accessRecordList
										.get(0);
							else {
								List accessRecordList2 = totalDao
										.query(
												"from AccessRecords where addTime > ? and inmarkId=? and recordStatus = '已通过' order by id desc",
												Util
														.getSpecifiedDayAfter(
																Util
																		.getDateTime("yyyy-MM-dd"),
																-7), dai
														.getCardId());
								if (accessRecordList2 != null
										&& accessRecordList2.size() > 0)
									accessRecords = (AccessRecords) accessRecordList
											.get(0);
							}
							if (accessRecords != null)// 有记录，将车牌保存起来=》开门
								carPai = accessRecords.getRecordContents();// 记录车牌
							else
								carPai = userList.get(0).getNplates();// 默认为最后一条的车牌
						} else
							// 只有一辆个人内部车
							carPai = userList.get(0).getNplates();
					} else
						// 6、没有查询到卡号
						buffer.append(dai.getCardId() + "没有车辆，无法开门");
					// 改变车辆状态
					if (!"".equals(carPai))
						updateCarIn(carPai, dai.getInouttype(), buffer);
				}
			} else {// 其他类型门禁
			}
		}
	}

	/**
	 * 改变车辆在停车场的状态(内/外)
	 * 
	 * @param carPai
	 * @param outIn
	 * @param builder
	 */
	public static void updateCarIn(String carPai, String outIn,
			StringBuffer builder) {
		TotalDao totalDao = createTotol();
		List<CarInOutType> carInOutList = totalDao.query(
				"from CarInOutType where carPai = ? order by updateTime desc",
				carPai);// 查询最后一条状态信息。按修改时间排序
		if (carInOutList != null && carInOutList.size() > 0) {
			if ("出门".equals(outIn))
				carInOutList.get(0).setInOut("外");
			else if ("进门".equals(outIn))
				carInOutList.get(0).setInOut("内");
			else
				carInOutList.get(0).setInOut(null);
			if (totalDao.update2(carInOutList.get(0)))
				builder.append("id+" + carInOutList.get(0).getId() + "状态已更新");
			else
				builder.append("id+" + carInOutList.get(0).getId() + "状态更改失败！");
		}
	}

	/**
	 * 确认进出停车场操作(有网)
	 * 
	 * @param accessId
	 * @param builder
	 */
	public static void updateStatus(Integer accessId, StringBuffer builder) {
		TotalDao totalDao = createTotol();
		// 查找该设备最后一条通过记录
		List<AccessLogInfor> inforlist = totalDao
				.query(
						"from AccessLogInfor where aceId = ? and addTime > ? order by id desc",
						accessId, Util.getDateTime(-5));// 查询五分钟内的数据
		if (inforlist != null && inforlist.size() > 0)
			updateCarIn(inforlist.get(0).getYanzheng(), inforlist.get(0)
					.getInOutType(), builder);
		else
			builder.append("没有找到对应车牌！");
	}

	/**
	 * 添加打卡&汇总记录(人行道断网记录)
	 * 
	 * @param cardId
	 * @param date
	 * @param time
	 * @param type
	 *            类型
	 * @param address
	 *            门禁名称
	 * @param outIn
	 *            进门/出门
	 * @return
	 */
	public static String addAttendanceTow(String cardId, String date,
			String time, String type, String address, String outIn) {
		if (!"".equals(cardId) && cardId != null) {
			TotalDao totalDao = createTotol();
			String nowdate = date;
			String nowTime = time;
			if (nowdate == null || "".equals(nowdate))
				nowdate = Util.getDateTime("yyyy-MM-dd");
			if (nowTime == null || "".equals(nowTime))
				nowTime = Util.getDateTime("HH:mm");
			// 查询Users
			List usersList = totalDao.query("from Users where cardId = ?",
					cardId);
			if (usersList != null && usersList.size() > 0) {
				Users users = (Users) usersList.get(0);
				List attenlist = totalDao
						.query(
								"from AttendanceTow where cardId =? and dateTime=? and time = ? and userId = ? and outInDoor = ? order by id desc",
								cardId, nowdate, nowTime, users.getId(), outIn);
				if (attenlist != null && attenlist.size() > 0) {
					totalDao.save2(attendance(date, "", time, cardId, users
							.getName(), users.getDept(), users.getId(), outIn,
							type, address));
					return "true";
				} else {
					if (totalDao.save2(attendance(date, "", time, cardId, users
							.getName(), users.getDept(), users.getId(), outIn,
							type, address))) {
						// 添加汇总表
						List list = totalDao
								.query(
										"from AttendanceTowCollect where cardId=? and dateTime=? order by id desc",
										cardId, date);
						if (list != null && list.size() > 0) {
							totalDao.update2(UpAtC(list, time, outIn));
							return "true";
						} else {
							totalDao.save2(AtC(cardId, "", date, users
									.getDept(), users.getName(), time, users
									.getId(), outIn));
							return "true";
						}
					}
				}
			}
		}
		return "添加失败";
	}

	/**
	 * 扬天考勤数据半路截取
	 * 
	 * @param cardId
	 * @param time
	 * @return
	 */
	public static String addAttendanceTow(String code, String time) {
		if (code != null && !"".equals(code)) {
			TotalDao totalDao = createTotol();
			List list = totalDao.query("from Users where code = ?", code);
			if (list != null && list.size() > 0) {
				Users u = (Users) list.get(0);
				if (u != null) {
					if (time == null || "".equals(time))
						time = Util.getDateTime();
					return addAttendanceTow(u.getCardId(), u.getCode(), u
							.getName(), u.getDept(), u.getId(), "正常", "阳天考勤机",
							"阳天", time);
				}
			}
			return "true";
		}
		return "卡号为空";
	}

	/**
	 * 浩顺考勤机数据对接
	 * 
	 * @param code
	 *            工号
	 * @param time
	 *            时间
	 * @param verify_mode
	 *            打卡类型
	 * @param device_id
	 *            设备编号
	 * @return
	 */
	public static String addAttendanceTow(String code, String time,
			String verify_mode, String device_id) {
		if (code != null && !"".equals(code)) {
			TotalDao totalDao = createTotol();
			List list = totalDao
					.query(
							"from Users where id = (select userId from UsersHSKao where hscode = ? and status = 0)",
							Integer.parseInt(code));
			if (list != null && list.size() > 0) {
				Users u = (Users) list.get(0);
				if (u != null) {
					if (time == null || "".equals(time))
						time = Util.getDateTime();
					String type = "";
					if ("20".equals(verify_mode)) {
						type = "面部";
					} else if ("1".equals(verify_mode)) {
						type = "指纹";
					}
					return addAttendanceTow(u.getCardId(), u.getCode(), u
							.getName(), u.getDept(), u.getId(), "正常", "新考勤机",
							type, time);
				}
			}
			return "true";
		}
		return "卡号为空";
	}

	/**
	 * 浩顺考勤机数据对接
	 * 
	 * @param code
	 *            工号
	 * @param time
	 *            时间
	 * @param verify_mode
	 *            打卡类型
	 * @param device_id
	 *            设备编号
	 * @return
	 */
	public static String addAttendanceTow(String code, Integer ids,
			String time, String verify_mode, String device_id) {
		if (code != null && !"".equals(code)) {
			TotalDao totalDao = createTotol();
			List list = totalDao
					.query(
							"from Users where id = (select userId from UsersHSKao where hscode = ? and status = 0)",
							Integer.parseInt(code));
			if (list != null && list.size() > 0) {
				Users u = (Users) list.get(0);
				if (u != null) {
					if (time == null || "".equals(time))
						time = Util.getDateTime();
					u.setUserStatus("在岗");
					if (u.getFristTime() == null
							|| !Util.getDateTime("yyyy-MM-dd").equals(
									u.getFristTime().substring(0, 10))) {
						u.setFristTime(time);
					}
					totalDao.update2(u);
					String type = "";
					if ("20".equals(verify_mode)) {
						type = "面部";
					} else if ("1".equals(verify_mode)) {
						type = "指纹";
					}
					return addAttendanceTow(u.getCardId(), u.getCode(), u
							.getName(), u.getDept(), u.getId(), ids + "",
							"新考勤机", type, time);
				}
			}
			return "true";
		}
		return "卡号为空";
	}

	/**
	 * （主方法）添加打卡&汇总记录(正常)
	 * 
	 * @param cardId
	 *            卡号
	 * @param name
	 *            姓名
	 * @param dept
	 *            部门
	 * @param userid
	 *            用户ID
	 * @param type
	 *            类型(正常/出差)
	 * @param address
	 *            手机打卡地址/打卡设备名称/等
	 * @param outIn
	 *            (进门/出门/手机)
	 * @param dateTime
	 *            时间
	 * @return
	 */
	public static String addAttendanceTow(String cardId, String code,
			String name, String dept, Integer userid, String type,
			String address, String outIn, String dateTime) {
		if (!"".equals(cardId) && cardId != null) {
			TotalDao totalDao = createTotol();
			if (dateTime == null || "".equals(dateTime))
				dateTime = Util.getDateTime();
			String date = dateTime.substring(0, 10);
			String time = dateTime.substring(11, 16);
			List attenlist = totalDao
					.query(
							"from AttendanceTow where cardId =? and dateTime=? and time = ? and userId = ? and outInDoor = ? and downType = ? order by id desc",
							cardId, date, time, userid, outIn, type);
			if (attenlist != null && attenlist.size() > 0) {
				// totalDao.save2(attendance(date,code, time, cardId, name,
				// dept, userid, outIn, type, address));
				return "true";
			} else {
				if (totalDao.save2(attendance(date, code, time, cardId, name,
						dept, userid, outIn, type, address))) {
					return kaoQinHuiZong(cardId, code, name, dept, userid,
							outIn, totalDao, date, time);
				}
			}
		}
		return "卡号为空！添加失败";
	}

	/**
	 * （主方法）添加打卡&汇总记录(正常)
	 * 
	 * @param cardId
	 *            卡号
	 * @param name
	 *            姓名
	 * @param dept
	 *            部门
	 * @param userid
	 *            用户ID
	 * @param type
	 *            类型(正常/出差)
	 * @param address
	 *            手机打卡地址/打卡设备名称/等
	 * @param outIn
	 *            (进门/出门/手机)
	 * @param dateTime
	 *            时间
	 * @return exists,一分钟内已经存在打卡记录，true打卡成功
	 */
	public static String addAttendanceTow(String cardId, String code,
			String name, String dept, Integer userid, String type,
			String address, String outIn, String dateTime, TotalDao totalDao) {
		//人脸识别专用，一分钟内，可以多次打卡。。
		if (!"".equals(cardId) && cardId != null) {
			if (dateTime == null || "".equals(dateTime))
				dateTime = Util.getDateTime();
			String date = dateTime.substring(0, 10);
			String time = dateTime.substring(11, 16);

//			List attenlist = totalDao
//					.query(
//							"from AttendanceTow where cardId =? and dateTime=? and time = ? and userId = ? and outInDoor = ? and downType = ? order by id desc",
//							cardId, date, time, userid, outIn, type);
//			if (attenlist != null && attenlist.size() > 0) {
//				//totalDao.save2(attendance(date,code, time, cardId, name, dept, userid, outIn, type, address));
//				return "exists";
//			} else {
				if (totalDao.save2(attendance(date, code, time, cardId, name,
						dept, userid, outIn, type, address))) {
					return kaoQinHuiZong(cardId, code, name, dept, userid,
							outIn, totalDao, date, time);
				}
//			}
		}
		return "卡号为空！添加失败";
	}

	/***
	 * 查询摄像头考勤进入播报语音用
	 * @param id
	 * @return
	 */
	@Override
	public List findSxtKq(Integer id) {
		String hql = "from AttendanceTow where downType='人脸识别摄像头' and outInDoor='进门'";
		if (id != null && id > 0) {
			hql += " and id>" + id;
		}
		hql += " ORDER BY id desc";
		List list =new ArrayList<AttendanceTow>();
		if (id != null && id > 0) {
			list= totalDao.query(hql);
		} else {
			AttendanceTow attendanceTow = (AttendanceTow) totalDao
					.getObjectByCondition(hql);
			attendanceTow.setName("maxid");
			list.add(attendanceTow);

		}
		return list;

	}
	
	/***
	 * 查询摄像头考勤进入播报语音用
	 * @param id
	 * @return
	 */
	@Override
	public List findSxtKqLeave(Integer id) {
		String hql = "from AttendanceTow where downType='人脸识别摄像头' and outInDoor='出门'";
		if (id != null && id > 0) {
			hql += " and id>" + id;
		}
		hql += " ORDER BY id desc";
		List list =new ArrayList<AttendanceTow>();
		if (id != null && id > 0) {
			list= totalDao.query(hql);
		} else {
			AttendanceTow attendanceTow = (AttendanceTow) totalDao
					.getObjectByCondition(hql);
			attendanceTow.setName("maxid");
			list.add(attendanceTow);

		}
		return list;

	}


	/**
	 * 考勤汇总主方法
	 * 
	 * @param cardId
	 * @param code
	 * @param name
	 * @param dept
	 * @param userid
	 * @param outIn
	 * @param totalDao
	 * @param date
	 * @param time
	 * @return
	 */
	private static String kaoQinHuiZong(String cardId, String code,
			String name, String dept, Integer userid, String outIn,
			TotalDao totalDao, String date, String time) {
		// 根据用户查询班次
		Users users = (Users) totalDao.getObjectById(Users.class, userid);
		if (users != null) {
			// addPush(dateTime, totalDao, users);
			Integer banciId = null;
			banciId = huoquBanCi(totalDao, users, date);
			if (banciId == null) {
				return "用户没有绑定班次，签到失败！";
			}
			BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, banciId);
			if (banCi != null) {
				boolean b1 = false;// 班次跨夜
				boolean bb = false;// 日期符合
				// 判断班次类型
				if ("门卫班次".equals(banCi.getBanCiType())) {
					return menWei(userid, totalDao, date, time, users, banCi);
				} else {
					// 根据班次得到是否为当天上班日期
					// 查询特殊时间表
					if ("是".equals(banCi.getIsOvernight())) {
						b1 = true;
					} else {
						bb = isbanci1(userid, date, totalDao);
					}
					if (b1) {// 班次为夸天的算法
						return geyeSuanfa(cardId, code, name, dept, userid,
								outIn, totalDao, date, time, users, banCi);
					} else {
						// 不跨天班次计算
						if (bb) {
							return dangtianSuanfa(cardId, code, name, dept,
									userid, outIn, totalDao, date, time, users,
									banCi);
						} else {
							return "不在班次日期内，打卡失败！";
						}
					}
				}
			} else {
				return "班次为空，打卡失败！";
			}
		} else {
			return "班次为空，打卡失败！";
		}
	}

	/**
	 * 班次为当天的算法
	 * 
	 * @param cardId
	 * @param code
	 * @param name
	 * @param dept
	 * @param userid
	 * @param outIn
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @return
	 */
	private static String dangtianSuanfa(String cardId, String code,
			String name, String dept, Integer userid, String outIn,
			TotalDao totalDao, String date, String time, Users users,
			BanCi banCi) {
		try {
			// 生成汇总记录
			List list_attendance = totalDao.query(
					"from Attendance where userId = ? and dateTime = ?",
					userid, date);
			Attendance attence = null;
			if (list_attendance != null && list_attendance.size() > 0) {
				attence = (Attendance) list_attendance.get(0);
			}
			if (attence == null) {
				attence = addAttendandFu(totalDao, users, date, time, banCi);
			}
			// if("2".equals(attence.getExceptTags()))
			// return "true";
			// attence.setTimeAll(null);//临时
			// 直接更新当日考勤汇总信息
			boolean bol = true;// 还未找到范围
			// 判断此次打卡是否在有效时间段之间
			if (bol && banCi.getBanCiTime() != null) {
				for (BanCiTime ban : banCi.getBanCiTime()) {
					if (ban != null && bol) {
						String start = ban.getStartTime().substring(0, 5);// 开始时间
						String end = ban.getEndTime().substring(0, 5);// 结束时间
						String startBefore = ban.getStartBeforeTime()
								.substring(0, 5);// 开始前时间
						String startLater = ban.getStartLaterTime().substring(
								0, 5);// 开始后时间
						String endBefore = ban.getEndBeforeTime().substring(0,
								5);// 结束前时间
						String endLater = ban.getEndLaterTime().substring(0, 5);// 结束后时间

						Integer shichang = (int) (Util.getWorkTime2(start, end) / 60000);// 此段工作时长
						// 查询附表
						AttendanceFu attendanceFu = null;
						List<AttendanceFu> attendanceFuList = totalDao
								.query(
										"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
										ban.getDuan(), attence
												.getAttendanceId(), userid);
						if (attendanceFuList != null
								&& attendanceFuList.size() > 0) {
							attendanceFu = attendanceFuList.get(0);
						}
						if (time.compareTo(startBefore) >= 0
								&& startLater.compareTo(time) >= 0) {// 上班有效打卡时段
							if (attendanceFu == null) {
								attendanceFu = addFu(totalDao, date, time,
										users, banCi, attence, ban);
							}
							if (ban.getStartIsDaka() != null
									&& ban.getStartIsDaka()) {// 开始时间必须打卡
								fuZhiWordDateTime(time, attendanceFu);
							} else {
								if (ban.getEndIsDaka() != null
										&& ban.getEndIsDaka()) {// 结束时间必须打卡
									fuZhiClosingDateTime(time, ban,
											attendanceFu);
								} else {
									fuZhiWordDateTime(time, attendanceFu);// 都不用打卡，给开始时间赋值
								}
							}
							fuZhiTimeAll(time, attendanceFu);
							if (start.compareTo(time) >= 0) {// 打卡时间在上班之前的
								// 计算加班时长
								jisuanOverTime(totalDao, date, time, code,
										banCi);
							}
							bol = false;
						} else if (endLater.compareTo(time) >= 0
								&& time.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
							if (attendanceFu == null) {
								attendanceFu = addFu(totalDao, date, time,
										users, banCi, attence, ban);
							}
							if (ban.getEndIsDaka() != null
									&& ban.getEndIsDaka()) {// 结束时间必须打卡
								fuZhiClosingDateTime(time, ban, attendanceFu);
							}
							if (ban.getStartIsDaka() != null
									&& ban.getStartIsDaka()) {// 结束时间必须打卡
								fuZhiWordDateTime(time, attendanceFu);
							}
							fuZhiTimeAll(time, attendanceFu);
							if (time.compareTo(end) >= 0) {// 打卡时间在下班之后的
								// 计算加班时长
								jisuanOverTime(totalDao, date, time, code,
										banCi);
							}
							bol = false;
						}
						if (!bol) {// 找到对应时段，计算对应时长
							huizongAttendFu(userid, totalDao, date, time,
									users, banCi, attence, ban, start, end,
									shichang, attendanceFu);
						} else {
							// 计算加班时长
							jisuanOverTime(totalDao, date, time, code, banCi);
						}
						totalDao.update2(attendanceFu);
					}
				}
			}
			// 更新汇总表信息
			updateAttend(attence);
			// 无需添加汇总表
			return "true";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 添加汇总表
			addAttendanceTowCollect(cardId, code, name, dept, userid, outIn,
					totalDao, date, time);
			return "true";
		}
	}

	/**
	 * 汇总附表
	 * 
	 * @param userid
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @param attence
	 * @param ban
	 * @param start
	 * @param end
	 * @param shichang
	 * @param attendanceFu
	 */
	public static void huizongAttendFu(Integer userid, TotalDao totalDao,
			String date, String time, Users users, BanCi banCi,
			Attendance attence, BanCiTime ban, String start, String end,
			Integer shichang, AttendanceFu attendanceFu) {
		if (ban.getStartIsDaka() != null && ban.getStartIsDaka()
				&& ban.getEndIsDaka() != null && ban.getEndIsDaka()) {
			// 开始和结束时间都需要打卡
			towDaKa(start, end, shichang, attendanceFu);
		} else if (ban.getStartIsDaka() != null && !ban.getStartIsDaka()
				&& ban.getEndIsDaka() != null && ban.getEndIsDaka()) {
			// 开始时间不需要打卡，结束时间需要打卡
			startNoEndYes(userid, totalDao, date, time, users, banCi, attence,
					ban, start, end, shichang, attendanceFu);
		} else if (ban.getStartIsDaka() != null && ban.getStartIsDaka()
				&& ban.getEndIsDaka() != null && !ban.getEndIsDaka()) {
			// 开始时间需要打卡，结束时间不需要打卡
			startYesEndNo(userid, totalDao, date, time, users, banCi, attence,
					ban, start, end, shichang, attendanceFu);
		} else {// 其他情况不处理
		}
	}

	private static void addAttendanceTowCollect(String cardId, String code,
			String name, String dept, Integer userid, String outIn,
			TotalDao totalDao, String date, String time) {
		List list = totalDao
				.query(
						"from AttendanceTowCollect where cardId=? and dateTime=? order by id desc",
						cardId, date);
		if (list != null && list.size() > 0) {
			totalDao.update2(UpAtC(list, time, outIn));
		} else {
			totalDao.save2(AtC(cardId, code, date, dept, name, time, userid,
					outIn));
		}
	}

	/**
	 * 开始时间需要打卡，结束时间不需要打卡
	 * 
	 * @param userid
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @param attence
	 * @param ban
	 * @param start
	 * @param end
	 * @param shichang
	 * @param attendanceFu
	 */
	private static void startYesEndNo(Integer userid, TotalDao totalDao,
			String date, String time, Users users, BanCi banCi,
			Attendance attence, BanCiTime ban, String start, String end,
			Integer shichang, AttendanceFu attendanceFu) {
		String sstime;
		String extime;
		// 下班时间不用打卡，查询下一时段汇总。
		if (ban.getDuan() != null
				&& ban.getDuan() < banCi.getBanCiTime().size()) {
			// 查询上一班次时段
			BanCiTime banCiTime2 = null;
			List<BanCiTime> ciTimes = totalDao.query(
					"from BanCiTime where banCi.id = ? and duan = ?", banCi
							.getId(), ban.getDuan() - 1);
			if (ciTimes != null && ciTimes.size() > 0) {
				banCiTime2 = ciTimes.get(0);
			}
			if (banCiTime2 != null) {
				if (banCiTime2.getStartIsDaka() != null
						&& !banCiTime2.getStartIsDaka()
						&& ban.getEndIsDaka() != null && ban.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
					// 查询附表
					AttendanceFu attendanceFu1 = null;
					List<AttendanceFu> attendanceFuList1 = totalDao
							.query(
									"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
									ban.getDuan() + 1, attence
											.getAttendanceId(), userid);
					if (attendanceFuList1 != null
							&& attendanceFuList1.size() > 0) {
						attendanceFu1 = attendanceFuList1.get(0);
					}
					String start1 = banCiTime2.getStartTime().substring(0, 5);
					if (attendanceFu1 == null) {
						attendanceFu1 = new AttendanceFu(attence
								.getAttendanceId(), users.getId(), users
								.getName(), users.getDept(), users.getCardId(),
								date, date + " " + time + ":00", "异常", 0, 0, 0,
								0, 0, ban.getXxTimeNumber(), ban
										.getXxTimeNumber(), banCi.getId(),
								banCi.getName(), "6", null, banCiTime2
										.getDuan() + 1, banCiTime2.getDayType());
						totalDao.save2(attendanceFu1);
					}
					boolean chidao = false;// 迟到
					boolean zaotui = false;// 早退
					if (attendanceFu.getWorkDateTime() == null) {
						sstime = null;
					} else {
						if (attendanceFu.getWorkDateTime().compareTo(start1) > 0) {// 迟到情况
							sstime = attendanceFu.getWorkDateTime();
							chidao = true;
						} else
							sstime = start1;
					}
					if (attendanceFu1.getClosingDateTime() == null) {
						extime = null;
					} else {
						if (attendanceFu1.getClosingDateTime().compareTo(end) < 0) {// 早退情况
							extime = attendanceFu1.getClosingDateTime();
							zaotui = true;
						} else
							extime = end;
					}

					if (sstime != null && extime != null) {// 汇总时长
						attendanceFu1.setWorkTime((int) (Util
								.getWorkTime2(sstime, banCiTime2.getEndTime()
										.substring(0, 5)) / 60000));// 计算上段时间的工作时长
						if (chidao)
							attendanceFu1
									.setLateTime((int) (Util.getWorkTime2(
											start1, attendanceFu1
													.getWorkDateTime()) / 60000));
						else
							attendanceFu1.setLateTime(0);
						attendanceFu1.setQueqinTime(attendanceFu1
								.getWorkBiaoTime()
								- attendanceFu1.getWorkTime()
								- attendanceFu1.getLateTime());// 缺勤时长
						if (attendanceFu1.getWorkBiaoTime() == attendanceFu1
								.getWorkTime().intValue()) {
							attendanceFu1.setAttendanceStatus("正常");
						}
						totalDao.update2(attendanceFu1);
						attendanceFu.setWorkTime((int) (Util.getWorkTime2(
								start, extime) / 60000));// 计算本段时间的工作时长
						if (zaotui)
							attendanceFu
									.setEarlyTime((int) (Util.getWorkTime2(
											attendanceFu.getClosingDateTime(),
											end) / 60000));
						else
							attendanceFu.setEarlyTime(0);
						attendanceFu.setQueqinTime(shichang
								- attendanceFu.getWorkTime()
								- attendanceFu.getEarlyTime());// 缺勤时长
						if (shichang.intValue() == attendanceFu.getWorkTime()) {
							attendanceFu.setAttendanceStatus("正常");
						}
					}
				} else {// 表示跟上一时段无关系。
					// 默认认为下班不用打卡。只判断有无迟到时长。
					boolean chidao = false;
					if (attendanceFu.getWorkDateTime() == null) {
						sstime = null;
					} else {
						if (attendanceFu.getWorkDateTime().compareTo(start) > 0) {// 迟到情况
							sstime = attendanceFu.getClosingDateTime();
							chidao = true;
						} else
							sstime = start;
					}
					if (sstime != null) {// 汇总时长
						attendanceFu.setWorkTime((int) (Util.getWorkTime2(
								sstime, end) / 60000));// 工作时长
						if (chidao)
							attendanceFu
									.setLateTime((int) (Util.getWorkTime2(
											start, attendanceFu
													.getWorkDateTime()) / 60000));
						else
							attendanceFu.setLateTime(0);
						attendanceFu.setQueqinTime(shichang
								- attendanceFu.getWorkTime()
								- attendanceFu.getLateTime());// 缺勤时长
						if (shichang.intValue() == attendanceFu.getWorkTime()) {
							attendanceFu.setAttendanceStatus("正常");
						}
					}
				}
			}
		}
	}

	/**
	 * 开始时间不用打卡，结束时间需要打卡
	 * 
	 * @param userid
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @param attence
	 * @param ban
	 * @param start
	 * @param end
	 * @param shichang
	 * @param attendanceFu
	 */
	private static void startNoEndYes(Integer userid, TotalDao totalDao,
			String date, String time, Users users, BanCi banCi,
			Attendance attence, BanCiTime ban, String start, String end,
			Integer shichang, AttendanceFu attendanceFu) {
		String sstime;
		String extime;
		// 上班时间不用打卡，查询上一个时段
		if (ban.getDuan() != null && ban.getDuan() > 1) {
			// 查询上一班次时段
			BanCiTime banCiTime2 = null;
			List<BanCiTime> ciTimes = totalDao.query(
					"from BanCiTime where banCi.id = ? and duan = ?", banCi
							.getId(), ban.getDuan() - 1);
			if (ciTimes != null && ciTimes.size() > 0) {
				banCiTime2 = ciTimes.get(0);
			}
			if (banCiTime2 != null) {
				if (banCiTime2.getStartIsDaka() != null
						&& banCiTime2.getStartIsDaka()
						&& banCiTime2.getEndIsDaka() != null
						&& !banCiTime2.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
					// 查询附表
					AttendanceFu attendanceFu1 = null;
					List<AttendanceFu> attendanceFuList1 = totalDao
							.query(
									"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
									ban.getDuan() - 1, attence
											.getAttendanceId(), userid);
					if (attendanceFuList1 != null
							&& attendanceFuList1.size() > 0) {
						attendanceFu1 = attendanceFuList1.get(0);
					}
					String start1 = banCiTime2.getStartTime().substring(0, 5);
					if (attendanceFu1 == null) {
						attendanceFu1 = addFu1(totalDao, date, time, users,
								banCi, attence, ban, banCiTime2);
					}
					boolean chidao = false;// 迟到
					boolean zaotui = false;// 早退
					if (attendanceFu1.getWorkDateTime() == null) {
						sstime = null;
					} else {
						if (attendanceFu1.getWorkDateTime().compareTo(start1) > 0) {// 迟到情况
							sstime = attendanceFu1.getWorkDateTime();
							chidao = true;
						} else
							sstime = start1;
					}
					if (attendanceFu.getClosingDateTime() == null) {
						extime = null;
					} else {
						if (attendanceFu.getClosingDateTime().compareTo(end) < 0) {// 早退情况
							extime = attendanceFu.getClosingDateTime();
							zaotui = true;
						} else
							extime = end;
					}

					if (sstime != null && extime != null) {// 汇总时长
						attendanceFu1.setWorkTime((int) (Util
								.getWorkTime2(sstime, banCiTime2.getEndTime()
										.substring(0, 5)) / 60000));// 计算上段时间的工作时长
						if (chidao)
							attendanceFu1
									.setLateTime((int) (Util.getWorkTime2(
											start1, attendanceFu1
													.getWorkDateTime()) / 60000));
						else
							attendanceFu1.setLateTime(0);
						attendanceFu1.setQueqinTime(attendanceFu1
								.getWorkBiaoTime()
								- attendanceFu1.getWorkTime()
								- attendanceFu1.getLateTime());// 缺勤时长
						if (attendanceFu1.getWorkBiaoTime() == attendanceFu1
								.getWorkTime().intValue()) {
							attendanceFu1.setAttendanceStatus("正常");
						}
						totalDao.update2(attendanceFu1);
						attendanceFu.setWorkTime((int) (Util.getWorkTime2(
								start, extime) / 60000));// 计算本段时间的工作时长
						if (zaotui)
							attendanceFu
									.setEarlyTime((int) (Util.getWorkTime2(
											attendanceFu.getClosingDateTime(),
											end) / 60000));
						else
							attendanceFu.setEarlyTime(0);
						attendanceFu.setQueqinTime(shichang
								- attendanceFu.getWorkTime()
								- attendanceFu.getEarlyTime());// 缺勤时长
						if (shichang.intValue() == attendanceFu.getWorkTime()) {
							attendanceFu.setAttendanceStatus("正常");
						}
					}
				} else {// 表示跟上一时段无关系。
					// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
					boolean zaotui = false;
					if (attendanceFu.getClosingDateTime() == null) {
						extime = null;
					} else {
						if (attendanceFu.getClosingDateTime().compareTo(end) < 0) {// 早退情况
							extime = attendanceFu.getClosingDateTime();
							zaotui = true;
						} else
							extime = end;
					}
					if (extime != null) {// 汇总时长
						attendanceFu.setWorkTime((int) (Util.getWorkTime2(
								start, extime) / 60000));// 工作时长
						if (zaotui)
							attendanceFu
									.setEarlyTime((int) (Util.getWorkTime2(
											attendanceFu.getClosingDateTime(),
											end) / 60000));
						else
							attendanceFu.setEarlyTime(0);
						attendanceFu.setQueqinTime(shichang
								- attendanceFu.getWorkTime()
								- attendanceFu.getEarlyTime());// 缺勤时长
						if (shichang.intValue() == attendanceFu.getWorkTime()) {
							attendanceFu.setAttendanceStatus("正常");
						}
					}
				}
			}
		}
	}

	/**
	 * 开始和结束时间都需要打卡
	 * 
	 * @param start
	 * @param end
	 * @param shichang
	 * @param attendanceFu
	 */
	private static void towDaKa(String start, String end, Integer shichang,
			AttendanceFu attendanceFu) {
		String sstime;
		String extime;
		boolean chidao = false;// 迟到
		boolean zaotui = false;// 早退
		if (attendanceFu.getWorkDateTime() == null) {
			sstime = null;
		} else {
			if (attendanceFu.getWorkDateTime().compareTo(start) > 0) {// 迟到情况
				sstime = attendanceFu.getWorkDateTime();
				chidao = true;
			} else
				sstime = start;
		}
		if (attendanceFu.getClosingDateTime() == null) {
			extime = null;
		} else {
			if (attendanceFu.getClosingDateTime().compareTo(end) < 0) {// 早退情况
				extime = attendanceFu.getClosingDateTime();
				zaotui = true;
			} else
				extime = end;
		}

		if (sstime != null && extime != null) {// 汇总时长
			attendanceFu
					.setWorkTime((int) (Util.getWorkTime2(sstime, extime) / 60000));// 工作时长
			if (chidao)
				attendanceFu.setLateTime((int) (Util.getWorkTime2(start,
						attendanceFu.getWorkDateTime()) / 60000));
			else
				attendanceFu.setLateTime(0);
			if (zaotui)
				attendanceFu.setEarlyTime((int) (Util.getWorkTime2(attendanceFu
						.getClosingDateTime(), end) / 60000));
			else
				attendanceFu.setEarlyTime(0);
			attendanceFu.setQueqinTime(shichang - attendanceFu.getWorkTime()
					- attendanceFu.getLateTime() - attendanceFu.getEarlyTime());// 缺勤时长
			if (shichang.intValue() == attendanceFu.getWorkTime()) {
				attendanceFu.setAttendanceStatus("正常");
			}
		}
	}

	private static AttendanceFu addFu1(TotalDao totalDao, String date,
			String time, Users users, BanCi banCi, Attendance attence,
			BanCiTime ban, BanCiTime banCiTime2) {
		AttendanceFu attendanceFu1;
		attendanceFu1 = new AttendanceFu(attence.getAttendanceId(), users
				.getId(), users.getName(), users.getDept(), users.getCardId(),
				date, date + " " + time + ":00", "异常", 0, 0, 0, 0, 0, ban
						.getXxTimeNumber(), ban.getXxTimeNumber(), banCi
						.getId(), banCi.getName(), "6", null, banCiTime2
						.getDuan() - 1, banCiTime2.getDayType());
		totalDao.save2(attendanceFu1);
		return attendanceFu1;
	}

	/**
	 * 给结束时间赋值
	 * 
	 * @param time
	 * @param ban
	 * @param attendanceFu
	 */
	private static void fuZhiClosingDateTime(String time, BanCiTime ban,
			AttendanceFu attendanceFu) {
		if (attendanceFu.getClosingDateTime() == null
				|| "".equals(attendanceFu.getClosingDateTime())) {
			attendanceFu.setClosingDateTime(time);
		} else {
			if (ban.getXiaFu() != null && ban.getXiaFu()) {// 下班记录覆盖
				if (attendanceFu.getClosingDateTime().compareTo(time) < 0) {// 当前时间大于记录时间时候才覆盖
					attendanceFu.setClosingDateTime(time);
				}
			} else {// 如果下班时间不覆盖。当前时间小于的时候按照小于时间计算
				if (attendanceFu.getClosingDateTime().compareTo(time) > 0) {
					attendanceFu.setClosingDateTime(time);
				}
			}
		}
	}

	/**
	 * 赋值打卡时间
	 * 
	 * @param time
	 * @param attendanceFu
	 */
	private static void fuZhiTimeAll(String time, AttendanceFu attendanceFu) {
		if (attendanceFu.getTimeAll() == null
				|| "".equals(attendanceFu.getTimeAll()))
			attendanceFu.setTimeAll(time);// 时间
		else
			attendanceFu.setTimeAll(Util.paixu(attendanceFu.getTimeAll() + "; "
					+ time));// 时间
	}

	/**
	 * 赋值该时段开始时间
	 * 
	 * @param time
	 * @param attendanceFu
	 */
	private static boolean fuZhiWordDateTime(String time,
			AttendanceFu attendanceFu) {
		if (attendanceFu.getWorkDateTime() == null
				|| "".equals(attendanceFu.getWorkDateTime())) {// 开始时间为空直接赋值
			attendanceFu.setWorkDateTime(time);
			return true;
		} else {// 开始时间不为空，如果打卡时间早于，将其替换
			if (attendanceFu.getWorkDateTime().compareTo(time) > 0) {
				attendanceFu.setWorkDateTime(time);
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加附表
	 * 
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @param attence
	 * @param ban
	 * @return
	 */
	private static AttendanceFu addFu(TotalDao totalDao, String date,
			String time, Users users, BanCi banCi, Attendance attence,
			BanCiTime ban) {
		AttendanceFu attendanceFu;
		attendanceFu = new AttendanceFu(attence.getAttendanceId(), users
				.getId(), users.getName(), users.getDept(), users.getCardId(),
				date, date + " " + time + ":00", "异常", 0, 0, 0, 0, 0, ban
						.getXxTimeNumber(), ban.getXxTimeNumber(), banCi
						.getId(), banCi.getName(), "6", null, ban.getDuan(),
				ban.getDayType());
		totalDao.save2(attendanceFu);
		return attendanceFu;
	}

	/**
	 * 计算加班时长
	 * 
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param banCi
	 */
	public static void jisuanOverTime(TotalDao totalDao, String date,
			String time, String code, BanCi banCi) {
		banCi = (BanCi) totalDao
				.getObjectByQuery(
						"from BanCi where id= (select banci_id from Users where code=?)",
						code);
		try {
			List<Overtime> list = totalDao.query(
					"from Overtime where overtimeCode = ? and (startDate like '"
							+ date + "%' or endDate like '" + date
							+ "%') order by startDate", code);
			String nowDate16 = date + " " + time;// 当前时间
			// List<OvertimeDetail> list =
			// totalDao.query("from OvertimeDetail where overtime.overtimeCode =? and"
			// +
			// " startTime like '"+date+"%' or endTime like '"+date+"%' order by startTime ",
			// code);
			if (list != null && list.size() > 0) {
				for (Overtime overtime : list) {
					String otStart = overtime.getStartDate().substring(0, 16);// 汇总的开始时间
					String otEnd = overtime.getEndDate().substring(0, 16);// 加班汇总的结束时间
					if (otStart.compareTo(nowDate16) >= 0) {// 在加班开始之前打的卡
						overtime.setStartStatus("已打卡");
					} else if (otEnd.compareTo(nowDate16) <= 0) {// 在加班结束之后打的卡
						overtime.setEndStatus("已打卡");
					} else { // 在加班申请中间打卡 加班迟到或者早退情况
						// 判断时候打了加班开始的卡
						String dateTime = nowDate16 + ":00";
						List<OvertimeDetail> overtimeDetailList = totalDao
								.query(
										"from OvertimeDetail "
												+ "where overtime.id=? and (oldStart is null or oldStart<>'') and startTime is not null order by startTime",
										overtime.getId());
						Integer totalMinutes = 0;// 总分钟数--用于计算时长
						if (overtime.getStartStatus() == null) {// 加班开始还未打卡
							overtime.setStartDate(dateTime);
							overtime.setStartStatus("已打卡");

							if (overtimeDetailList != null
									&& overtimeDetailList.size() > 0) {// 明细中的开始时间处理
								OvertimeDetail firstDetail = overtimeDetailList
										.get(0);
								if (dateTime.compareTo(firstDetail
										.getStartTime()) > 0) {
									// firstDetail.setOldStart(firstDetail.getStartTime());
									firstDetail.setStartTime(dateTime);
									Long diff = Util.getDateDiff(firstDetail
											.getStartTime(),
											"yyyy-MM-dd HH:mm", firstDetail
													.getEndTime(),
											"yyyy-MM-dd HH:mm");
									Integer minutes = diff.intValue() / 60;
									totalMinutes += minutes;
									firstDetail.setHour(minutes / 60);
									firstDetail.setMinutes(minutes % 60);
									totalDao.update(firstDetail);
								}
							}
						} else {
							// 设置加班结束卡
							overtime.setEndDate(dateTime);
							overtime.setEndStatus("已打卡");

							if (overtimeDetailList != null
									&& overtimeDetailList.size() > 0) {// 明细中的结束时间处理
								OvertimeDetail lastDetail = overtimeDetailList
										.get(overtimeDetailList.size() - 1);
								if (dateTime.compareTo(lastDetail.getEndTime()) < 0) {
									// lastDetail.setOldEnd(lastDetail.getEndTime());
									lastDetail.setEndTime(dateTime);

									Long diff = Util.getDateDiff(lastDetail
											.getStartTime(),
											"yyyy-MM-dd HH:mm", lastDetail
													.getEndTime(),
											"yyyy-MM-dd HH:mm");
									Integer minutes = diff.intValue() / 60;
									totalMinutes += minutes;
									lastDetail.setHour(minutes / 60);
									lastDetail.setMinutes(minutes % 60);
									totalDao.update(lastDetail);
								}
							}
						}
						if (overtime.getXiuxi() == null) {
							overtime.setXiuxi(0);
						}
						overtime.setOverTimeLong(totalMinutes / 60f
								- overtime.getXiuxi());
						if (overtime.getHuanxiu() != null
								&& overtime.getHuanxiu().equals("是")) {
							overtime
									.setUsablehxTime(overtime.getOverTimeLong());
						}
					}
					// if (overtime.getStartStatus()!=null &&
					// overtime.getEndStatus()!=null
					// && overtime.getStartStatus().equals("已打卡")
					// && overtime.getEndStatus().equals("已打卡")) {
					// overtime.setStatus("已打卡");
					// }
					totalDao.update(overtime);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * //遍历之前的加班记录，如果开始时间不为空，将结束时间设置为加班申请结束时间
	 * 
	 * @param list
	 * @param i
	 */
	// private static void updateLastOverTime(List<Overtime> list, int i) {
	// int j = i;
	// while (j>=0) {
	// Overtime ot3 = list.get(j-1);//上一个加班时间
	// if(ot3.getFilnalStartDate()!=null){
	// ot3.setFilnalEndDate(ot3.getEndDate());
	// }
	// }
	// }

	/**
	 * 打卡时间在加班申请开始和结束之间
	 * 
	 * @param date
	 *            日期
	 * @param banCi
	 *            班次
	 * @param nowDate16
	 *            当前时间(时分)
	 * @param ot
	 *            符合条件的加班申请
	 * @param start1
	 *            加班开始时间
	 * @param end1
	 *            加班结束时间(时分)
	 */
	private static void oneToTow(String date, BanCi banCi, String nowDate16,
			Overtime ot, String start1, String end1) {
		/************* 如果加班开始时间为下班后，则默认实际开始时间为加班开始时间 ***********/
		if (!ziDongFuzhiStart(date, banCi, ot, start1))
			// ot.setFilnalStartDate(nowDate16+":00");
			ot.setStartDate(nowDate16 + ":00");
		;
		/************* 如果加班结束时间为当天上班前，则默认实际结束时间为加班申请结束时间 ***********/
		if (!ziDongFuzhiEnd(date, banCi, ot, end1)) {
			// ot.setFilnalEndDate(nowDate16+":00");
			ot.setEndDate(nowDate16 + ":00");
		}
		// if(ot.getFilnalStartDate()==null){
		//			
		// }else {
		// ot.setFilnalEndDate(nowDate16+":00");
		// }
	}

	/**
	 * 如果加班开始时间为下班后，则默认实际开始时间为加班开始时间
	 * 
	 * @param date
	 * @param banCi
	 * @param ot
	 * @param end1
	 */
	private static boolean ziDongFuzhiStart(String date, BanCi banCi,
			Overtime ot, String start1) {
		TotalDao totalDao = createTotol();
		List<BanCiTime> banciTimeList = totalDao.query(
				"from BanCiTime where banCi.id = ?", banCi.getId());
		if (banciTimeList != null && banciTimeList.size() > 0) {
			String banCiEnd = date
					+ " "
					+ banciTimeList.get(banciTimeList.size() - 1).getEndTime()
							.substring(0, 5);
			if (start1.compareTo(banCiEnd) >= 0) {
				// ot.setFilnalStartDate(ot.getStartDate());
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 如果加班结束时间为当天上班前，则默认实际结束时间为加班申请结束时间
	 * 
	 * @param date
	 * @param banCi
	 * @param ot
	 * @param end1
	 */
	private static boolean ziDongFuzhiEnd(String date, BanCi banCi,
			Overtime ot, String end1) {
		TotalDao totalDao = createTotol();
		List<BanCiTime> banciTimeList = totalDao.query(
				"from BanCiTime where banCi.id = ?", banCi.getId());
		if (banciTimeList != null && banciTimeList.size() > 0) {
			String banCiStart = date + " "
					+ banciTimeList.get(0).getStartTime().substring(0, 5);
			if (banCiStart.compareTo(end1) >= 0) {
				// ot.setFilnalEndDate(ot.getEndDate());
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 班次为夸天的算法
	 * 
	 * @param cardId
	 * @param code
	 * @param name
	 * @param dept
	 * @param userid
	 * @param outIn
	 * @param totalDao
	 * @param date
	 * @param time
	 * @param users
	 * @param banCi
	 * @return
	 */
	private static String geyeSuanfa(String cardId, String code, String name,
			String dept, Integer userid, String outIn, TotalDao totalDao,
			String date, String time, Users users, BanCi banCi) {
		try {
			// 跨天班次流程
			// 生成汇总记录
			List list_attendance = totalDao.query(
					"from Attendance where userId = ? and dateTime = ?",
					userid, date);
			Attendance attence = null;
			if (list_attendance != null && list_attendance.size() > 0) {
				attence = (Attendance) list_attendance.get(0);
			}
			boolean bol = true;// 未找到范围
			BanCiTime ban = null;// 已找到范围
			String sstime = null;// 上班时间
			String extime = null;// 下班时间
			String startS = "";// 开始时间状态
			String endS = "";// 结束时间状态
			String jiaTime = "";// 增加后的时间
			String startBefore = "";// 开始前时间
			String endLater = "";// 结束后时间
			int kuaNum = 12 * 60;// 跨天机算增加时长
			if (attence == null) {
				// 先判断时段
				for (BanCiTime ban1 : banCi.getBanCiTime()) {
					if (ban1 != null && bol) {
						startBefore = ban1.getStartBeforeTime();// 开始前时间
						endLater = ban1.getEndLaterTime();// 结束后时间
						startS = ban1.getDayType();// 开始时间状态
						endS = ban1.getEdayType();// 结束时间状态
						startBefore = Util.getminuteAfter(startBefore, kuaNum)
								.substring(0, 5);
						endLater = Util.getminuteAfter(endLater, kuaNum)
								.substring(0, 5);
						jiaTime = Util.getminuteAfter(time + ":00", kuaNum)
								.substring(0, 5);
						if (jiaTime.compareTo(startBefore) >= 0
								&& endLater.compareTo(jiaTime) >= 0) {// 在此班次时段内
							if ("次日".equals(startS) && "次日".equals(endS)) {// 班次时间表中开始时间和结束时间都为次日，添加次日考勤
							// 判断前一天有无汇总记录
								date = Util.getSpecifiedDayAfter(date, -1);
								List list_attendanceqian = totalDao
										.query(
												"from Attendance where userId = ? and dateTime = ?",
												userid, date);
								if (list_attendanceqian != null
										&& list_attendanceqian.size() > 0) {
									attence = (Attendance) list_attendanceqian
											.get(0);
								}
								if (attence == null) {// 为空。添加昨天的汇总记录
									if (isbanci1(userid, date, totalDao)) {
										// 添加当日汇总表
										attence = addAttendAndFu(users, date,
												time, banCi);
									} else {
										return "true";
									}
								} else {// 不为空，已有副表
								}
								// 添加前一天的记录
							} else {
								if (endLater.compareTo("12:00") > 0) {
									if (jiaTime.compareTo("12:00") > 0) {
										// 判断前一天有无汇总记录
										date = Util.getSpecifiedDayAfter(date,
												-1);
										List list_attendanceqian = totalDao
												.query(
														"from Attendance where userId = ? and dateTime = ?",
														userid, date);
										if (list_attendanceqian != null
												&& list_attendanceqian.size() > 0) {
											attence = (Attendance) list_attendanceqian
													.get(0);
										}
										if (attence == null) {// 为空。添加昨天的汇总记录
											if (isbanci1(userid, date, totalDao)) {
												// 添加当日汇总表
												attence = addAttendAndFu(users,
														date, time, banCi);
											} else {
												return "true";
											}
										} else {// 不为空，已有副表
										}
									} else {
										if (isbanci1(userid, date, totalDao)) {
											// 添加当日汇总表
											attence = addAttendAndFu(users,
													date, time, banCi);
										} else {
											return "true";
										}
									}
								} else {
									// 当天要在班次日期内
									if (isbanci1(userid, date, totalDao)) {
										// 添加当日汇总表
										attence = addAttendAndFu(users, date,
												time, banCi);
									} else {
										return "true";
									}
								}
							}
							bol = false;
							ban = ban1;
						} else {
							if (ban1.getDuan() != null && ban1.getDuan() == 1
									&& startBefore.compareTo(time) >= 0) {// 查询是否有加班记录
							}
						}
					}
				}

				if (ban != null && !bol) {
					String start = Util.getminuteAfter(ban.getStartTime(),
							kuaNum).substring(0, 5);// 开始时间
					String end = Util.getminuteAfter(ban.getEndTime(), kuaNum)
							.substring(0, 5);// 结束时间
					startBefore = Util.getminuteAfter(ban.getStartBeforeTime(),
							kuaNum).substring(0, 5);// 开始前时间
					String startLater = Util.getminuteAfter(
							ban.getStartLaterTime(), kuaNum).substring(0, 5);// 开始后时间
					String endBefore = Util.getminuteAfter(
							ban.getEndBeforeTime(), kuaNum).substring(0, 5);// 结束前时间
					endLater = Util.getminuteAfter(ban.getEndLaterTime(),
							kuaNum).substring(0, 5);// 结束后时间
					jiaTime = Util.getminuteAfter(time + ":00", kuaNum)
							.substring(0, 5);
					String nnssTime = "";// 上班是否覆盖
					String nnexTime = "";// 下班是否覆盖
					// 查询附表
					AttendanceFu attendanceFu = null;
					List<AttendanceFu> attendanceFuList = totalDao
							.query(
									"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
									ban.getDuan(), attence.getAttendanceId(),
									userid);
					if (attendanceFuList != null && attendanceFuList.size() > 0) {
						attendanceFu = attendanceFuList.get(0);
					}
					if (jiaTime.compareTo(startBefore) >= 0
							&& startLater.compareTo(jiaTime) >= 0) {// 上班有效打卡时段
						if (attendanceFu == null) {
							return "null";
						}
						if (attendanceFu.getWorkDateTime() == null
								|| "".equals(attendanceFu.getWorkDateTime())) {
							attendanceFu.setWorkDateTime(time);
						} else {
							nnssTime = Util.getminuteAfter1(attendanceFu
									.getWorkDateTime(), kuaNum);
							if (nnssTime.compareTo(jiaTime) > 0) {
								attendanceFu.setWorkDateTime(time);
							}
						}
						if (attendanceFu.getTimeAll() == null
								|| "".equals(attendanceFu.getTimeAll()))
							attendanceFu.setTimeAll(time);// 时间
						else
							attendanceFu.setTimeAll(attendanceFu.getTimeAll()
									+ "; " + time);// 时间
						bol = false;
					} else if (endLater.compareTo(jiaTime) >= 0
							&& jiaTime.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
						if (attendanceFu == null) {
							return "null";
						}
						if (attendanceFu.getClosingDateTime() == null
								|| "".equals(attendanceFu.getClosingDateTime())) {
							attendanceFu.setClosingDateTime(time);
						} else {
							nnexTime = Util.getminuteAfter1(attendanceFu
									.getClosingDateTime(), kuaNum);
							if (ban.getXiaFu() != null && ban.getXiaFu()) {// 下班记录覆盖
								if (nnexTime.compareTo(jiaTime) < 0) {// 当前时间大于记录时间时候才覆盖
									attendanceFu.setClosingDateTime(time);
								}
							} else {// 如果下班时间不覆盖。当前时间小于的时候按照小于时间计算
								if (nnexTime.compareTo(jiaTime) > 0) {
									attendanceFu.setClosingDateTime(time);
								}
							}
						}
						if (attendanceFu.getTimeAll() == null
								|| "".equals(attendanceFu.getTimeAll()))
							attendanceFu.setTimeAll(time);// 时间
						else
							attendanceFu.setTimeAll(attendanceFu.getTimeAll()
									+ "; " + time);// 时间
						bol = false;
					}
					if (!bol) {
						String kuasstime = "";// 跨夜计算带入参数(开始)
						String kuaextime = "";// 跨夜计算带入参数(结束)
						// 找到对应时段，计算对应时长
						if (ban.getStartIsDaka() != null
								&& ban.getStartIsDaka()
								&& ban.getEndIsDaka() != null
								&& ban.getEndIsDaka()) {
							// 开始和结束时间都需要打卡的
							boolean chidao = false;// 迟到
							boolean zaotui = false;// 早退
							if (attendanceFu.getWorkDateTime() == null) {
								sstime = null;
							} else {
								kuasstime = Util.getminuteAfter1(attendanceFu
										.getWorkDateTime(), kuaNum);
								if (kuasstime.compareTo(start) > 0) {// 迟到情况
									sstime = kuasstime;
									chidao = true;
								} else
									sstime = start;
							}
							if (attendanceFu.getClosingDateTime() == null) {
								extime = null;
							} else {
								kuaextime = Util.getminuteAfter1(attendanceFu
										.getClosingDateTime(), kuaNum);
								if (kuaextime.compareTo(end) < 0) {// 早退情况
									extime = kuaextime;
									zaotui = true;
								} else
									extime = end;
							}
							if (sstime != null && extime != null) {// 汇总时长
								attendanceFu.setWorkTime((int) (Util
										.getWorkTime2(sstime, extime) / 60000));// 工作时长
								if (chidao)
									attendanceFu
											.setLateTime((int) (Util
													.getWorkTime2(start,
															kuasstime) / 60000));
								else
									attendanceFu.setLateTime(0);
								if (zaotui)
									attendanceFu
											.setEarlyTime((int) (Util
													.getWorkTime2(kuaextime,
															end) / 60000));
								else
									attendanceFu.setEarlyTime(0);
								attendanceFu.setQueqinTime(attendanceFu
										.getWorkBiaoTime()
										- attendanceFu.getWorkTime()
										- attendanceFu.getLateTime()
										- attendanceFu.getEarlyTime());// 缺勤时长
								if (attendanceFu.getWorkBiaoTime().intValue() == attendanceFu
										.getWorkTime()) {
									attendanceFu.setAttendanceStatus("正常");
								}
							}
						} else if (ban.getStartIsDaka() != null
								&& !ban.getStartIsDaka()
								&& ban.getEndIsDaka() != null
								&& ban.getEndIsDaka()) {
							// 上班时间不用打卡，查询上一个时段
							if (ban.getDuan() != null && ban.getDuan() > 1) {
								// 查询上一班次时段
								BanCiTime banCiTime2 = null;
								List<BanCiTime> ciTimes = totalDao
										.query(
												"from BanCiTime where banCi.id = ? and duan = ?",
												banCi.getId(),
												ban.getDuan() - 1);
								if (ciTimes != null && ciTimes.size() > 0) {
									banCiTime2 = ciTimes.get(0);
								}
								if (banCiTime2 != null) {
									if (banCiTime2.getStartIsDaka() != null
											&& banCiTime2.getStartIsDaka()
											&& banCiTime2.getEndIsDaka() != null
											&& !banCiTime2.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
										// 查询附表
										AttendanceFu attendanceFu1 = null;
										List<AttendanceFu> attendanceFuList1 = totalDao
												.query(
														"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
														ban.getDuan() - 1,
														attence
																.getAttendanceId(),
														userid);
										if (attendanceFuList1 != null
												&& attendanceFuList1.size() > 0) {
											attendanceFu1 = attendanceFuList1
													.get(0);
										}
										String start1 = Util.getminuteAfter(
												banCiTime2.getStartTime(),
												kuaNum).substring(0, 5);
										if (attendanceFu1 == null) {
											return "null";
										}
										boolean chidao = false;// 迟到
										boolean zaotui = false;// 早退
										if (attendanceFu1.getWorkDateTime() == null) {
											sstime = null;
										} else {
											kuasstime = Util.getminuteAfter1(
													attendanceFu1
															.getWorkDateTime(),
													kuaNum);
											if (kuasstime.compareTo(start1) > 0) {// 迟到情况
												sstime = kuasstime;
												chidao = true;
											} else
												sstime = start1;
										}
										if (attendanceFu.getClosingDateTime() == null) {
											extime = null;
										} else {
											kuaextime = Util
													.getminuteAfter1(
															attendanceFu
																	.getClosingDateTime(),
															kuaNum);
											if (kuaextime.compareTo(end) < 0) {// 早退情况
												extime = kuaextime;
												zaotui = true;
											} else
												extime = end;
										}

										if (sstime != null && extime != null) {// 汇总时长
											attendanceFu1
													.setWorkTime((int) (Util
															.getWorkTime2(
																	sstime,
																	banCiTime2
																			.getEndTime()
																			.substring(
																					0,
																					5)) / 60000));// 计算上段时间的工作时长
											if (chidao)
												attendanceFu1
														.setLateTime((int) (Util
																.getWorkTime2(
																		start1,
																		kuasstime) / 60000));
											else
												attendanceFu1.setLateTime(0);
											attendanceFu1
													.setQueqinTime(attendanceFu1
															.getWorkBiaoTime()
															- attendanceFu1
																	.getWorkTime()
															- attendanceFu1
																	.getLateTime());// 缺勤时长
											if (attendanceFu1.getWorkBiaoTime() == attendanceFu1
													.getWorkTime().intValue()) {
												attendanceFu1
														.setAttendanceStatus("正常");
											}
											totalDao.update2(attendanceFu1);
											attendanceFu
													.setWorkTime((int) (Util
															.getWorkTime2(
																	start,
																	extime) / 60000));// 计算本段时间的工作时长
											if (zaotui)
												attendanceFu
														.setEarlyTime((int) (Util
																.getWorkTime2(
																		kuaextime,
																		end) / 60000));
											else
												attendanceFu.setEarlyTime(0);
											attendanceFu
													.setQueqinTime(attendanceFu
															.getWorkBiaoTime()
															- attendanceFu
																	.getWorkTime()
															- attendanceFu
																	.getEarlyTime());// 缺勤时长
											if (attendanceFu.getWorkBiaoTime()
													.intValue() == attendanceFu
													.getWorkTime()) {
												attendanceFu
														.setAttendanceStatus("正常");
											}
										}
									} else {// 表示跟上一时段无关系。
										// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
										boolean zaotui = false;
										if (attendanceFu.getClosingDateTime() == null) {
											extime = null;
										} else {
											kuaextime = Util
													.getminuteAfter1(
															attendanceFu
																	.getClosingDateTime(),
															kuaNum);
											if (kuaextime.compareTo(end) < 0) {// 早退情况
												extime = kuaextime;
												zaotui = true;
											} else
												extime = end;
										}
										if (extime != null) {// 汇总时长
											attendanceFu
													.setWorkTime((int) (Util
															.getWorkTime2(
																	start,
																	extime) / 60000));// 工作时长
											if (zaotui)
												attendanceFu
														.setEarlyTime((int) (Util
																.getWorkTime2(
																		kuaextime,
																		end) / 60000));
											else
												attendanceFu.setEarlyTime(0);
											attendanceFu
													.setQueqinTime(attendanceFu
															.getWorkBiaoTime()
															- attendanceFu
																	.getWorkTime()
															- attendanceFu
																	.getEarlyTime());// 缺勤时长
											if (attendanceFu.getWorkBiaoTime()
													.intValue() == attendanceFu
													.getWorkTime()) {
												attendanceFu
														.setAttendanceStatus("正常");
											}
										}
									}
								}
							}
						} else if (ban.getStartIsDaka() != null
								&& ban.getStartIsDaka()
								&& ban.getEndIsDaka() != null
								&& !ban.getEndIsDaka()) {
							// 下班时间不用打卡，查询下一时段汇总。
							if (ban.getDuan() != null
									&& ban.getDuan() < banCi.getBanCiTime()
											.size()) {
								// 查询上一班次时段
								BanCiTime banCiTime2 = null;
								List<BanCiTime> ciTimes = totalDao
										.query(
												"from BanCiTime where banCi.id = ? and duan = ?",
												banCi.getId(),
												ban.getDuan() - 1);
								if (ciTimes != null && ciTimes.size() > 0) {
									banCiTime2 = ciTimes.get(0);
								}
								if (banCiTime2 != null) {
									if (banCiTime2.getStartIsDaka() != null
											&& !banCiTime2.getStartIsDaka()
											&& ban.getEndIsDaka() != null
											&& ban.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
										// 查询附表
										AttendanceFu attendanceFu1 = null;
										List<AttendanceFu> attendanceFuList1 = totalDao
												.query(
														"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
														ban.getDuan() + 1,
														attence
																.getAttendanceId(),
														userid);
										if (attendanceFuList1 != null
												&& attendanceFuList1.size() > 0) {
											attendanceFu1 = attendanceFuList1
													.get(0);
										}
										String start1 = Util.getminuteAfter(
												banCiTime2.getStartTime(),
												kuaNum).substring(0, 5);
										if (attendanceFu1 == null) {
											return "null";
										}
										boolean chidao = false;// 迟到
										boolean zaotui = false;// 早退
										if (attendanceFu.getWorkDateTime() == null) {
											sstime = null;
										} else {
											kuasstime = Util.getminuteAfter1(
													attendanceFu1
															.getWorkDateTime(),
													kuaNum);
											if (kuasstime.compareTo(start1) > 0) {// 迟到情况
												sstime = kuasstime;
												chidao = true;
											} else
												sstime = start1;
										}
										if (attendanceFu1.getClosingDateTime() == null) {
											extime = null;
										} else {
											kuaextime = Util
													.getminuteAfter1(
															attendanceFu1
																	.getClosingDateTime(),
															kuaNum);
											if (kuaextime.compareTo(end) < 0) {// 早退情况
												extime = kuaextime;
												zaotui = true;
											} else
												extime = end;
										}

										if (sstime != null && extime != null) {// 汇总时长
											attendanceFu1
													.setWorkTime((int) (Util
															.getWorkTime2(
																	sstime,
																	banCiTime2
																			.getEndTime()
																			.substring(
																					0,
																					5)) / 60000));// 计算上段时间的工作时长
											if (chidao)
												attendanceFu1
														.setLateTime((int) (Util
																.getWorkTime2(
																		start1,
																		kuasstime) / 60000));
											else
												attendanceFu1.setLateTime(0);
											attendanceFu1
													.setQueqinTime(attendanceFu1
															.getWorkBiaoTime()
															- attendanceFu1
																	.getWorkTime()
															- attendanceFu1
																	.getLateTime());// 缺勤时长
											if (attendanceFu1.getWorkBiaoTime() == attendanceFu1
													.getWorkTime().intValue()) {
												attendanceFu1
														.setAttendanceStatus("正常");
											}
											totalDao.update2(attendanceFu1);
											attendanceFu
													.setWorkTime((int) (Util
															.getWorkTime2(
																	start,
																	extime) / 60000));// 计算本段时间的工作时长
											if (zaotui)
												attendanceFu
														.setEarlyTime((int) (Util
																.getWorkTime2(
																		kuaextime,
																		end) / 60000));
											else
												attendanceFu.setEarlyTime(0);
											attendanceFu
													.setQueqinTime(attendanceFu
															.getWorkBiaoTime()
															- attendanceFu
																	.getWorkTime()
															- attendanceFu
																	.getEarlyTime());// 缺勤时长
											if (attendanceFu.getWorkBiaoTime()
													.intValue() == attendanceFu
													.getWorkTime()) {
												attendanceFu
														.setAttendanceStatus("正常");
											}
										}
									} else {// 表示跟上一时段无关系。
										// 默认认为下班不用打卡。只判断有无迟到时长。
										boolean chidao = false;
										if (attendanceFu.getWorkDateTime() == null) {
											sstime = null;
										} else {
											kuasstime = Util.getminuteAfter1(
													attendanceFu
															.getWorkDateTime(),
													kuaNum);
											if (kuasstime.compareTo(start) > 0) {// 迟到情况
												sstime = kuasstime;
												chidao = true;
											} else
												sstime = start;
										}
										if (sstime != null) {// 汇总时长
											attendanceFu
													.setWorkTime((int) (Util
															.getWorkTime2(
																	sstime, end) / 60000));// 工作时长
											if (chidao)
												attendanceFu
														.setLateTime((int) (Util
																.getWorkTime2(
																		start,
																		kuasstime) / 60000));
											else
												attendanceFu.setLateTime(0);
											attendanceFu
													.setQueqinTime(attendanceFu
															.getWorkBiaoTime()
															- attendanceFu
																	.getWorkTime()
															- attendanceFu
																	.getLateTime());// 缺勤时长
											if (attendanceFu.getWorkBiaoTime()
													.intValue() == attendanceFu
													.getWorkTime()) {
												attendanceFu
														.setAttendanceStatus("正常");
											}
										}
									}
								}
							}
						} else {// 其他情况不处理
						}
					}
					totalDao.update2(attendanceFu);
				}
			} else {
				// if("2".equals(attence.getExceptTags()))
				// return "true";
				// 当天时段不为空 判断时段
				for (BanCiTime ban1 : banCi.getBanCiTime()) {
					if (ban1 != null && bol) {
						ban = ban1;
						String start = Util.getminuteAfter(ban.getStartTime(),
								kuaNum).substring(0, 5);// 开始时间
						String end = Util.getminuteAfter(ban.getEndTime(),
								kuaNum).substring(0, 5);// 结束时间
						startBefore = Util.getminuteAfter(
								ban.getStartBeforeTime(), kuaNum).substring(0,
								5);// 开始前时间
						String startLater = Util.getminuteAfter(
								ban.getStartLaterTime(), kuaNum)
								.substring(0, 5);// 开始后时间
						String endBefore = Util.getminuteAfter(
								ban.getEndBeforeTime(), kuaNum).substring(0, 5);// 结束前时间
						endLater = Util.getminuteAfter(ban.getEndLaterTime(),
								kuaNum).substring(0, 5);// 结束后时间
						jiaTime = Util.getminuteAfter(time + ":00", kuaNum)
								.substring(0, 5);
						// 查询附表
						AttendanceFu attendanceFu = null;
						List<AttendanceFu> attendanceFuList = totalDao
								.query(
										"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
										ban.getDuan(), attence
												.getAttendanceId(), userid);
						if (attendanceFuList != null
								&& attendanceFuList.size() > 0) {
							attendanceFu = attendanceFuList.get(0);
						}
						if (jiaTime.compareTo(startBefore) >= 0
								&& startLater.compareTo(jiaTime) >= 0) {// 上班有效打卡时段
							if (attendanceFu == null) {
								return "null";
							}
							fuZhiWordDateTime(time, attendanceFu);
							if (attendanceFu.getTimeAll() == null
									|| "".equals(attendanceFu.getTimeAll()))
								attendanceFu.setTimeAll(time);// 时间
							else
								attendanceFu.setTimeAll(attendanceFu
										.getTimeAll()
										+ "; " + time);// 时间
							bol = false;
						} else if (endLater.compareTo(jiaTime) >= 0
								&& jiaTime.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
							if (attendanceFu == null) {
								return "null";
							}
							fuZhiClosingDateTime(time, ban, attendanceFu);
							if (attendanceFu.getTimeAll() == null
									|| "".equals(attendanceFu.getTimeAll()))
								attendanceFu.setTimeAll(time);// 时间
							else
								attendanceFu.setTimeAll(attendanceFu
										.getTimeAll()
										+ "; " + time);// 时间
							bol = false;
						}
						if (!bol) {
							String kuasstime = "";// 跨夜计算带入参数
							String kuaextime = "";
							// 找到对应时段，计算对应时长
							if (ban.getStartIsDaka() != null
									&& ban.getStartIsDaka()
									&& ban.getEndIsDaka() != null
									&& ban.getEndIsDaka()) {
								// 开始和结束时间都需要打卡的
								boolean chidao = false;// 迟到
								boolean zaotui = false;// 早退
								if (attendanceFu.getWorkDateTime() == null) {
									sstime = null;
								} else {
									kuasstime = Util.getminuteAfter1(
											attendanceFu.getWorkDateTime(),
											kuaNum);
									if (kuasstime.compareTo(start) > 0) {// 迟到情况
										sstime = kuasstime;
										chidao = true;
									} else
										sstime = start;
								}
								if (attendanceFu.getClosingDateTime() == null) {
									extime = null;
								} else {
									kuaextime = Util.getminuteAfter1(
											attendanceFu.getClosingDateTime(),
											kuaNum);
									if (kuaextime.compareTo(end) < 0) {// 早退情况
										extime = kuaextime;
										zaotui = true;
									} else
										extime = end;
								}

								if (sstime != null && extime != null) {// 汇总时长
									attendanceFu
											.setWorkTime((int) (Util
													.getWorkTime2(sstime,
															extime) / 60000));// 工作时长
									if (chidao)
										attendanceFu
												.setLateTime((int) (Util
														.getWorkTime2(start,
																kuasstime) / 60000));
									else
										attendanceFu.setLateTime(0);
									if (zaotui)
										attendanceFu
												.setEarlyTime((int) (Util
														.getWorkTime2(
																kuaextime, end) / 60000));
									else
										attendanceFu.setEarlyTime(0);
									attendanceFu.setQueqinTime(attendanceFu
											.getWorkBiaoTime()
											- attendanceFu.getWorkTime()
											- attendanceFu.getLateTime()
											- attendanceFu.getEarlyTime());// 缺勤时长
									if (attendanceFu.getWorkBiaoTime()
											.intValue() == attendanceFu
											.getWorkTime()) {
										attendanceFu.setAttendanceStatus("正常");
									}
								}
							} else if (ban.getStartIsDaka() != null
									&& !ban.getStartIsDaka()
									&& ban.getEndIsDaka() != null
									&& ban.getEndIsDaka()) {
								// 上班时间不用打卡，查询上一个时段
								if (ban.getDuan() != null && ban.getDuan() > 1) {
									// 查询上一班次时段
									BanCiTime banCiTime2 = null;
									List<BanCiTime> ciTimes = totalDao
											.query(
													"from BanCiTime where banCi.id = ? and duan = ?",
													banCi.getId(), ban
															.getDuan() - 1);
									if (ciTimes != null && ciTimes.size() > 0) {
										banCiTime2 = ciTimes.get(0);
									}
									if (banCiTime2 != null) {
										if (banCiTime2.getStartIsDaka() != null
												&& banCiTime2.getStartIsDaka()
												&& banCiTime2.getEndIsDaka() != null
												&& !banCiTime2.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
											// 查询附表
											AttendanceFu attendanceFu1 = null;
											List<AttendanceFu> attendanceFuList1 = totalDao
													.query(
															"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
															ban.getDuan() - 1,
															attence
																	.getAttendanceId(),
															userid);
											if (attendanceFuList1 != null
													&& attendanceFuList1.size() > 0) {
												attendanceFu1 = attendanceFuList1
														.get(0);
											}
											String start1 = Util
													.getminuteAfter(
															banCiTime2
																	.getStartTime(),
															kuaNum).substring(
															0, 5);
											if (attendanceFu1 == null) {
												return "null";
											}
											boolean chidao = false;// 迟到
											boolean zaotui = false;// 早退
											if (attendanceFu1.getWorkDateTime() == null) {
												sstime = null;
											} else {
												kuasstime = Util
														.getminuteAfter1(
																attendanceFu1
																		.getWorkDateTime(),
																kuaNum);
												if (kuasstime.compareTo(start1) > 0) {// 迟到情况
													sstime = kuasstime;
													chidao = true;
												} else
													sstime = start1;
											}
											if (attendanceFu
													.getClosingDateTime() == null) {
												extime = null;
											} else {
												kuaextime = Util
														.getminuteAfter1(
																attendanceFu
																		.getClosingDateTime(),
																kuaNum);
												if (kuaextime.compareTo(end) < 0) {// 早退情况
													extime = kuaextime;
													zaotui = true;
												} else
													extime = end;
											}

											if (sstime != null
													&& extime != null) {// 汇总时长
												attendanceFu1
														.setWorkTime((int) (Util
																.getWorkTime2(
																		sstime,
																		banCiTime2
																				.getEndTime()
																				.substring(
																						0,
																						5)) / 60000));// 计算上段时间的工作时长
												if (chidao)
													attendanceFu1
															.setLateTime((int) (Util
																	.getWorkTime2(
																			start1,
																			kuasstime) / 60000));
												else
													attendanceFu1
															.setLateTime(0);
												attendanceFu1
														.setQueqinTime(attendanceFu1
																.getWorkBiaoTime()
																- attendanceFu1
																		.getWorkTime()
																- attendanceFu1
																		.getLateTime());// 缺勤时长
												if (attendanceFu1
														.getWorkBiaoTime() == attendanceFu1
														.getWorkTime()
														.intValue()) {
													attendanceFu1
															.setAttendanceStatus("正常");
												}
												totalDao.update2(attendanceFu1);
												attendanceFu
														.setWorkTime((int) (Util
																.getWorkTime2(
																		start,
																		extime) / 60000));// 计算本段时间的工作时长
												if (zaotui)
													attendanceFu
															.setEarlyTime((int) (Util
																	.getWorkTime2(
																			kuaextime,
																			end) / 60000));
												else
													attendanceFu
															.setEarlyTime(0);
												attendanceFu
														.setQueqinTime(attendanceFu
																.getWorkBiaoTime()
																- attendanceFu
																		.getWorkTime()
																- attendanceFu
																		.getEarlyTime());// 缺勤时长
												if (attendanceFu
														.getWorkBiaoTime()
														.intValue() == attendanceFu
														.getWorkTime()) {
													attendanceFu
															.setAttendanceStatus("正常");
												}
											}
										} else {// 表示跟上一时段无关系。
											// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
											boolean zaotui = false;
											if (attendanceFu
													.getClosingDateTime() == null) {
												extime = null;
											} else {
												kuaextime = Util
														.getminuteAfter1(
																attendanceFu
																		.getClosingDateTime(),
																kuaNum);
												if (kuaextime.compareTo(end) < 0) {// 早退情况
													extime = kuaextime;
													zaotui = true;
												} else
													extime = end;
											}
											if (extime != null) {// 汇总时长
												attendanceFu
														.setWorkTime((int) (Util
																.getWorkTime2(
																		start,
																		extime) / 60000));// 工作时长
												if (zaotui)
													attendanceFu
															.setEarlyTime((int) (Util
																	.getWorkTime2(
																			kuaextime,
																			end) / 60000));
												else
													attendanceFu
															.setEarlyTime(0);
												attendanceFu
														.setQueqinTime(attendanceFu
																.getWorkBiaoTime()
																- attendanceFu
																		.getWorkTime()
																- attendanceFu
																		.getEarlyTime());// 缺勤时长
												if (attendanceFu
														.getWorkBiaoTime()
														.intValue() == attendanceFu
														.getWorkTime()) {
													attendanceFu
															.setAttendanceStatus("正常");
												}
											}
										}
									}
								}
							} else if (ban.getStartIsDaka() != null
									&& ban.getStartIsDaka()
									&& ban.getEndIsDaka() != null
									&& !ban.getEndIsDaka()) {
								// 下班时间不用打卡，查询下一时段汇总。
								if (ban.getDuan() != null
										&& ban.getDuan() < banCi.getBanCiTime()
												.size()) {
									// 查询上一班次时段
									BanCiTime banCiTime2 = null;
									List<BanCiTime> ciTimes = totalDao
											.query(
													"from BanCiTime where banCi.id = ? and duan = ?",
													banCi.getId(), ban
															.getDuan() - 1);
									if (ciTimes != null && ciTimes.size() > 0) {
										banCiTime2 = ciTimes.get(0);
									}
									if (banCiTime2 != null) {
										if (banCiTime2.getStartIsDaka() != null
												&& !banCiTime2.getStartIsDaka()
												&& ban.getEndIsDaka() != null
												&& ban.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
											// 查询附表
											AttendanceFu attendanceFu1 = null;
											List<AttendanceFu> attendanceFuList1 = totalDao
													.query(
															"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
															ban.getDuan() + 1,
															attence
																	.getAttendanceId(),
															userid);
											if (attendanceFuList1 != null
													&& attendanceFuList1.size() > 0) {
												attendanceFu1 = attendanceFuList1
														.get(0);
											}
											String start1 = Util
													.getminuteAfter(
															banCiTime2
																	.getStartTime(),
															kuaNum).substring(
															0, 5);
											if (attendanceFu1 == null) {
												return "null";
											}
											boolean chidao = false;// 迟到
											boolean zaotui = false;// 早退
											if (attendanceFu.getWorkDateTime() == null) {
												sstime = null;
											} else {
												kuasstime = Util
														.getminuteAfter1(
																attendanceFu1
																		.getWorkDateTime(),
																kuaNum);
												if (kuasstime.compareTo(start1) > 0) {// 迟到情况
													sstime = kuasstime;
													chidao = true;
												} else
													sstime = start1;
											}
											if (attendanceFu1
													.getClosingDateTime() == null) {
												extime = null;
											} else {
												kuaextime = Util
														.getminuteAfter1(
																attendanceFu1
																		.getClosingDateTime(),
																kuaNum);
												if (kuaextime.compareTo(end) < 0) {// 早退情况
													extime = kuaextime;
													zaotui = true;
												} else
													extime = end;
											}

											if (sstime != null
													&& extime != null) {// 汇总时长
												attendanceFu1
														.setWorkTime((int) (Util
																.getWorkTime2(
																		sstime,
																		banCiTime2
																				.getEndTime()
																				.substring(
																						0,
																						5)) / 60000));// 计算上段时间的工作时长
												if (chidao)
													attendanceFu1
															.setLateTime((int) (Util
																	.getWorkTime2(
																			start1,
																			kuasstime) / 60000));
												else
													attendanceFu1
															.setLateTime(0);
												attendanceFu1
														.setQueqinTime(attendanceFu1
																.getWorkBiaoTime()
																- attendanceFu1
																		.getWorkTime()
																- attendanceFu1
																		.getLateTime());// 缺勤时长
												if (attendanceFu1
														.getWorkBiaoTime() == attendanceFu1
														.getWorkTime()
														.intValue()) {
													attendanceFu1
															.setAttendanceStatus("正常");
												}
												totalDao.update2(attendanceFu1);
												attendanceFu
														.setWorkTime((int) (Util
																.getWorkTime2(
																		start,
																		extime) / 60000));// 计算本段时间的工作时长
												if (zaotui)
													attendanceFu
															.setEarlyTime((int) (Util
																	.getWorkTime2(
																			kuaextime,
																			end) / 60000));
												else
													attendanceFu
															.setEarlyTime(0);
												attendanceFu
														.setQueqinTime(attendanceFu
																.getWorkBiaoTime()
																- attendanceFu
																		.getWorkTime()
																- attendanceFu
																		.getEarlyTime());// 缺勤时长
												if (attendanceFu
														.getWorkBiaoTime()
														.intValue() == attendanceFu
														.getWorkTime()) {
													attendanceFu
															.setAttendanceStatus("正常");
												}
											}
										} else {// 表示跟上一时段无关系。
											// 默认认为下班不用打卡。只判断有无迟到时长。
											boolean chidao = false;
											if (attendanceFu.getWorkDateTime() == null) {
												sstime = null;
											} else {
												kuasstime = Util
														.getminuteAfter1(
																attendanceFu
																		.getWorkDateTime(),
																kuaNum);
												if (kuasstime.compareTo(start) > 0) {// 迟到情况
													sstime = kuasstime;
													chidao = true;
												} else
													sstime = start;
											}
											if (sstime != null) {// 汇总时长
												attendanceFu
														.setWorkTime((int) (Util
																.getWorkTime2(
																		sstime,
																		end) / 60000));// 工作时长
												if (chidao)
													attendanceFu
															.setLateTime((int) (Util
																	.getWorkTime2(
																			start,
																			kuasstime) / 60000));
												else
													attendanceFu.setLateTime(0);
												attendanceFu
														.setQueqinTime(attendanceFu
																.getWorkBiaoTime()
																- attendanceFu
																		.getWorkTime()
																- attendanceFu
																		.getLateTime());// 缺勤时长
												if (attendanceFu
														.getWorkBiaoTime()
														.intValue() == attendanceFu
														.getWorkTime()) {
													attendanceFu
															.setAttendanceStatus("正常");
												}
											}
										}
									}
								}
							} else {// 其他情况不处理
							}
						}
						totalDao.update2(attendanceFu);
					}
				}
			}
			// 更新汇总表信息
			updateAttend(attence);
			// 无需添加汇总表
			return "true";
		} catch (Exception e) {
			addAttendanceTowCollect(cardId, code, name, dept, userid, outIn,
					totalDao, date, time);
			return "true";
		}
	}

	private static String menWei(Integer userid, TotalDao totalDao,
			String date, String time, Users users, BanCi banCi) {
		// 如果当天没有汇总记录查前一天，前一天有就汇总前一天。如果前一天没有就添加今天的汇总记录
		List list_attendance = totalDao.query(
				"from Attendance where userId = ? and dateTime = ?", userid,
				date);
		Attendance attence = null;
		if (list_attendance != null && list_attendance.size() > 0) {
			attence = (Attendance) list_attendance.get(0);
		}
		boolean b2 = false;// 今天true表示昨天
		if (attence == null) {
			// 判断前一天有无汇总记录
			// date = Util.getSpecifiedDayAfter(date, -1);
			List list_attendanceqian = totalDao.query(
					"from Attendance where userId = ? and dateTime = ?",
					userid, Util.getSpecifiedDayAfter(date, -1));
			if (list_attendanceqian != null && list_attendanceqian.size() > 0) {
				attence = (Attendance) list_attendanceqian.get(0);
			}
			if (attence == null) {// 为空。添今天的汇总记录
				// 添加当日汇总表
				attence = addAttendAndFu(users, date, time, banCi);
			} else {// 不为空，已有副表.汇总昨天班次
				date = Util.getSpecifiedDayAfter(date, -1);
				b2 = true;
			}
		} else {
			// 如果当天有汇总记录，证明是今天打过卡。重复打卡。只记录不汇总
		}
		// if("2".equals(attence.getExceptTags()))
		// return "true";
		// 计算时间
		boolean bol = true;// 未找到范围
		String startBefore = "";// 开始前时间
		String startLater = "";
		String endBefore = "";
		String endLater = "";// 结束后时间
		for (BanCiTime ban1 : banCi.getBanCiTime()) {
			if (ban1 != null && bol) {
				// if ((time.compareTo(startBefore) >= 0 &&
				// startLater.compareTo(time)>=0)||
				// (time.compareTo(endBefore) >= 0 &&
				// endLater.compareTo(time)>=0)) {// 在此班次时段内
				String start = ban1.getStartTime().substring(0, 5);// 开始时间
				String end = ban1.getEndTime().substring(0, 5);// 结束时间
				startBefore = ban1.getStartBeforeTime().substring(0, 5);// 开始前时间
				startLater = ban1.getStartLaterTime().substring(0, 5);// 开始后时间
				endBefore = ban1.getEndBeforeTime().substring(0, 5);// 结束前时间
				endLater = ban1.getEndLaterTime().substring(0, 5);// 结束后时间
				String nnssTime = "";// 上班是否覆盖
				String nnexTime = "";// 下班是否覆盖
				String sstime = null;// 上班时间
				String extime = null;// 下班时间
				// 查询附表
				AttendanceFu attendanceFu = null;
				List<AttendanceFu> attendanceFuList = totalDao
						.query(
								"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
								ban1.getDuan(), attence.getAttendanceId(),
								userid);
				if (attendanceFuList != null && attendanceFuList.size() > 0) {
					attendanceFu = attendanceFuList.get(0);
				}
				if (attendanceFu != null) {
					if (b2) {// 汇总昨天记录
						if (endLater.compareTo(time) >= 0
								&& time.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
							if (attendanceFu.getClosingDateTime() == null
									|| "".equals(attendanceFu
											.getClosingDateTime())) {
								attendanceFu.setClosingDateTime(time);
							} else {
								nnexTime = attendanceFu.getClosingDateTime();
								if (ban1.getXiaFu() != null && ban1.getXiaFu()) {// 下班记录覆盖
									if (nnexTime.compareTo(time) < 0) {// 当前时间大于记录时间时候才覆盖
										attendanceFu.setClosingDateTime(time);
									}
								} else {// 如果下班时间不覆盖。当前时间小于的时候按照小于时间计算
									if (nnexTime.compareTo(time) > 0) {
										attendanceFu.setClosingDateTime(time);
									}
								}
							}
							if (attendanceFu.getTimeAll() == null
									|| "".equals(attendanceFu.getTimeAll()))
								attendanceFu.setTimeAll(time);// 时间
							else
								attendanceFu.setTimeAll(attendanceFu
										.getTimeAll()
										+ "; " + time);// 时间
							bol = false;
						}
					} else {// 汇总当天记录
						if (time.compareTo(startBefore) >= 0
								&& startLater.compareTo(time) >= 0) {// 上班有效打卡时段
							if (attendanceFu.getWorkDateTime() == null
									|| ""
											.equals(attendanceFu
													.getWorkDateTime())) {
								attendanceFu.setWorkDateTime(time);
							} else {
								nnssTime = attendanceFu.getWorkDateTime();
								if (nnssTime.compareTo(time) > 0) {
									attendanceFu.setWorkDateTime(time);
								}
							}
							if (attendanceFu.getTimeAll() == null
									|| "".equals(attendanceFu.getTimeAll()))
								attendanceFu.setTimeAll(time);// 时间
							else
								attendanceFu.setTimeAll(attendanceFu
										.getTimeAll()
										+ "; " + time);// 时间
							bol = false;
						}
					}
					if (!bol) {
						String kuasstime = "";// 跨夜计算带入参数(开始)
						String kuaextime = "";// 跨夜计算带入参数(结束)
						// 找到对应时段，计算对应时长
						if (ban1.getStartIsDaka() != null
								&& ban1.getStartIsDaka()
								&& ban1.getEndIsDaka() != null
								&& ban1.getEndIsDaka()) {
							// 开始和结束时间都需要打卡的
							boolean chidao = false;// 迟到
							boolean zaotui = false;// 早退
							if (attendanceFu.getWorkDateTime() == null) {
								sstime = null;
							} else {
								kuasstime = attendanceFu.getWorkDateTime();
								if (kuasstime.compareTo(start) > 0) {// 迟到情况
									sstime = kuasstime;
									chidao = true;
								} else
									sstime = start;
							}
							if (attendanceFu.getClosingDateTime() == null) {
								extime = null;
							} else {
								kuaextime = attendanceFu.getClosingDateTime();
								if (kuaextime.compareTo(end) < 0) {// 早退情况
									extime = kuaextime;
									zaotui = true;
								} else
									extime = end;
							}
							if (sstime != null && extime != null) {// 汇总时长
								if (chidao)
									attendanceFu
											.setLateTime((int) (Util
													.getWorkTime2(start,
															kuasstime) / 60000));
								else
									attendanceFu.setLateTime(0);
								if (zaotui)
									attendanceFu
											.setEarlyTime((int) (Util
													.getWorkTime2(kuaextime,
															end) / 60000));
								else
									attendanceFu.setEarlyTime(0);
								attendanceFu.setWorkTime(attendanceFu
										.getWorkBiaoTime()
										- attendanceFu.getLateTime()
										- attendanceFu.getEarlyTime());// 工作时长
								attendanceFu.setQueqinTime(0);// 缺勤时长
								// if(attendanceFu.getWorkBiaoTime().intValue()
								// == attendanceFu.getWorkTime()){
								attendanceFu.setAttendanceStatus("正常");
								// }
							}
						}
					}
					totalDao.update2(attendanceFu);
				}
			}
		}
		// 更新汇总表信息
		updateAttend(attence);
		// 无需添加汇总表
		return "true";
	}

	@SuppressWarnings("unused")
	private static void addPush(String dateTime, TotalDao totalDao, Users users) {
		List<RandomPunch> Ran = totalDao
				.query(
						"from RandomPunch where usersId = ? and addTime < ? and punchShiTime >= ?",
						users.getId(), dateTime, dateTime);
		if (Ran != null && Ran.size() > 0) {
			RandomPunch punch = Ran.get(0);
			punch.setPunchTime(dateTime);
			punch.setStatus("已签到");
			totalDao.update2(punch);
		}
	}

	/**
	 * 更新主表
	 * 
	 * @param attence
	 * @return
	 */
	public static Attendance updateAttend(Attendance attence) {
		TotalDao totalDao = createTotol();
		List<AttendanceFu> attendanceFuList1 = totalDao
				.query(
						"from AttendanceFu where attendanceId = ? and userId = ? order by duan asc",
						attence.getAttendanceId(), attence.getUserId());
		if (attendanceFuList1 != null && attendanceFuList1.size() > 0) {
			Integer lateTime = 0;// 迟到时长
			Integer earlyTime = 0;// 早退时长
			Integer workTime = 0;// 工作时长
			Integer queqinTime = 0;// 缺勤时长
			StringBuffer buffer = new StringBuffer();
			for (AttendanceFu attendanceFu2 : attendanceFuList1) {
				lateTime += attendanceFu2.getLateTime();
				earlyTime += attendanceFu2.getEarlyTime();
				workTime += attendanceFu2.getWorkTime();
				queqinTime += attendanceFu2.getQueqinTime();
				if (attendanceFu2.getTimeAll() != null) {
					if (attendanceFu2.getDuan() > 1 && buffer.length() > 0)
						buffer.append("; ");
					buffer.append(attendanceFu2.getTimeAll());
				}
			}
			attence.setLateTime(lateTime);
			attence.setEarlyTime(earlyTime);
			attence.setWorkTime(workTime);
			attence.setQueqinTime(queqinTime);
			attence.setTimeAll(buffer.toString());
			if (workTime.intValue() == attence.getWorkBiaoTime()) {
				attence.setAttendanceStatus("正常");
			} else if (attence.getLateTime() > 0 && attence.getEarlyTime() > 0) {
				attence.setAttendanceStatus("迟到,早退");
			} else if (attence.getLateTime() > 0) {
				attence.setAttendanceStatus("迟到");
			} else if (attence.getEarlyTime() > 0) {
				attence.setAttendanceStatus("早退");
			} else if (attence.getQijiaTime() > 0) {
				attence.setAttendanceStatus("请假");
			} else if (attence.getQueqinTime() > 0) {
				attence.setAttendanceStatus("缺勤");
			}
		}
		totalDao.update2(attence);
		return attence;
	}

	/**
	 * 添加主表和附表
	 * 
	 * @param users
	 * @param date
	 * @param time
	 * @param banCi
	 * @return
	 */
	public static Attendance addAttendAndFu(Users users, String date,
			String time, BanCi banCi) {
		TotalDao totalDao = createTotol();
		// 添加汇总表
		Attendance attence = new Attendance(users.getId(), users.getCode(),
				users.getName(), users.getDept(), users.getCardId(), date, date
						+ " " + time + ":00", "缺勤", 0, 0, 0, 0, 0, banCi
						.getGzTime(), banCi.getGzTime(), banCi.getId(), banCi
						.getName(), "6", null);
		totalDao.save2(attence);
		for (BanCiTime ban_1 : banCi.getBanCiTime()) {
			String dates = date;
			String startS1 = ban_1.getDayType();// 开始时间状态
			String endS1 = ban_1.getEdayType();// 结束时间状态
			if ("次日".equals(startS1) && "次日".equals(endS1)) {// 班次时间表中开始时间和结束时间都为次日，添加次日考勤
				dates = Util.getSpecifiedDayAfter(date, 1);
			}
			AttendanceFu fu = new AttendanceFu(attence.getAttendanceId(), users
					.getId(), users.getName(), users.getDept(), users
					.getCardId(), dates, date + " " + time + ":00", "缺勤", 0, 0,
					0, 0, 0, ban_1.getXxTimeNumber(), ban_1.getXxTimeNumber(),
					banCi.getId(), banCi.getName(), "6", null, ban_1.getDuan(),
					ban_1.getDayType());
			totalDao.save2(fu);
		}
		return attence;
	}

	/**
	 * 查询当前用户今天是否上班
	 * 
	 * @param users
	 * @return
	 */
	public static boolean isbanci(BanCi banCi, TotalDao totalDao) {
		boolean bool1 = false;
		// String week = "星期四";// 当前星期几中文
		// String week = Util.getDateTime("E");// 当前星期几中文
		// String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
		// for (int l = 0; l < sbdate.length; l++) {
		// if (week.equals(sbdate[l].trim())) {
		// bool1 = true;
		// }
		// }
		Map<String, Object> map = BanCiServerImpl.updateORsearchIfyouReWork(
				null, banCi.getId(), Util.getDateTime("yyyy-MM-dd"), totalDao);
		if (map != null && map.get("banciId") != null) {
			bool1 = true;
		}
		return bool1;
	}

	/**
	 * 查询当前用户今天是否上班，结合特殊日期
	 * 
	 * @param users
	 * @return
	 */
	public static boolean isbanci1(Integer usersId, String date,
			TotalDao totalDao) {
		Map<String, Object> map = BanCiServerImpl.updateORsearchIfyouReWork(
				usersId, null, date, totalDao);
		if (map != null && map.get("banciId") != null) {
			return true;
		}
		// TotalDao totalDao = createTotol();
		// boolean bool1 = false;
		// List<SpecialDate> listSpeci2 = totalDao
		// .query("from SpecialDate where banciId = ? and date = ?",banCi.getId(),
		// date);
		// if (listSpeci2 != null && listSpeci2.size() > 0) {
		// if ("上班".equals(listSpeci2.get(0)
		// .getSpecialType())) {
		// bool1 = true;
		// }
		// } else {
		// String week = Util.getFullDateWeekTime(date);
		// String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
		// for (int l = 0; l < sbdate.length; l++) {
		// if (week.equals(sbdate[l].trim())) {
		// bool1 = true;
		// }
		// }
		// }
		return false;
	}

	/**
	 * 重新计算当天缺勤的考勤数据
	 * 
	 * @param date
	 * @param status
	 */
	public static void chongsuan(String date, String status) {
		TotalDao totalDao = createTotol();
		List<String> attendances = totalDao
				.query(
						"select code from Attendance where dateTime = ? and attendanceStatus = ? and code = '800022'",
						date, status);
		if (attendances != null && attendances.size() > 0) {
			String ss = Util.selectString(attendances.toString());
			List<AttendanceTow> attendanceTows = totalDao.query(
					"from AttendanceTow where dateTime = ? and code in ('" + ss
							+ "')", date);
			if (!attendanceTows.isEmpty()) {
				for (AttendanceTow a : attendanceTows) {
					kaoQinHuiZong(a.getCardId(), a.getCode(), a.getName(), a
							.getDept(), a.getUserId(), "指纹", totalDao, date, a
							.getTime());
				}
			}
		}
	}

	// 批量处理数据
	@Deprecated
	public static void pilaing(String yue, String jiezhi) {
		// yue = "2017-12-01";
		TotalDao totalDao = createTotol();
		List<AttendanceTow> attendanceTows = totalDao.query(
				"from AttendanceTow where dateTime >= ? and dateTime < ?", yue,
				jiezhi);
		if (!attendanceTows.isEmpty()) {
			for (AttendanceTow attendanceTow : attendanceTows) {
				// 根据用户查询班次
				Users users = (Users) totalDao.getObjectById(Users.class,
						attendanceTow.getUserId());
				if (users != null) {
					// String dateTime = attendanceTow.getAddTime();
					// if (dateTime == null || "".equals(dateTime))
					// dateTime = Util.getDateTime();
					String date = attendanceTow.getDateTime();
					String time = attendanceTow.getTime();
					Integer banciId = null;
					// 查询排班表 获得班次当天
					banciId = huoquBanCi(totalDao, users, date);
					if (banciId == null) {
						continue;
					}
					BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class,
							banciId);
					if (banCi != null) {
						// boolean b = true;// 符合计算条件
						boolean b1 = false;// 班次跨夜
						boolean bb = false;// 日期符合
						// 判断班次类型
						if ("门卫班次".equals(banCi.getBanCiType())) {
							// 如果当天没有汇总记录查前一天，前一天有就汇总前一天。如果前一天没有就添加今天的汇总记录
							List list_attendance = totalDao
									.query(
											"from Attendance where userId = ? and dateTime = ?",
											users.getId(), date);
							Attendance attence = null;
							if (list_attendance != null
									&& list_attendance.size() > 0) {
								attence = (Attendance) list_attendance.get(0);
							}
							boolean b2 = false;// 今天true表示昨天
							if (attence == null) {
								// 判断前一天有无汇总记录
								// date = Util.getSpecifiedDayAfter(date, -1);
								List list_attendanceqian = totalDao
										.query(
												"from Attendance where userId = ? and dateTime = ?",
												users.getId(), Util
														.getSpecifiedDayAfter(
																date, -1));
								if (list_attendanceqian != null
										&& list_attendanceqian.size() > 0) {
									attence = (Attendance) list_attendanceqian
											.get(0);
								}
								if (attence == null) {// 为空。添今天的汇总记录
									// 添加当日汇总表
									attence = addAttendAndFu(users, date, time,
											banCi);
								} else {// 不为空，已有副表.汇总昨天班次
									date = Util.getSpecifiedDayAfter(date, -1);
									b2 = true;
								}
							} else {
								// 如果当天有汇总记录，证明是今天打过卡。重复打卡。只记录不汇总
							}
							if ("2".equals(attence.getExceptTags()))
								continue;
							// 计算时间
							boolean bol = true;// 未找到范围
							for (BanCiTime ban1 : banCi.getBanCiTime()) {
								bol = menweiJisuan(totalDao, users, time,
										attence, b2, bol, ban1);
							}
							// 更新汇总表信息
							System.out.println(attence.getAttendanceId()
									+ "+++++++++" + attendanceTow.getId());
							updateAttend(attence);
							// 无需添加汇总表
							continue;
						} else {
							// 根据班次得到是否为当天上班日期
							// 查询特殊时间表
							if ("是".equals(banCi.getIsOvernight())) {
								b1 = true;
							} else {
								bb = isbanci1(users.getId(), date, totalDao);
							}
							if (b1) {
								try {
									// 跨天班次流程
									// 生成汇总记录
									List list_attendance = totalDao
											.query(
													"from Attendance where userId = ? and dateTime = ?",
													users.getId(), date);
									Attendance attence = null;
									if (list_attendance != null
											&& list_attendance.size() > 0) {
										attence = (Attendance) list_attendance
												.get(0);
									}
									boolean bol = true;// 未找到范围
									BanCiTime ban = null;// 已找到范围
									String sstime = null;// 上班时间
									String extime = null;// 下班时间
									String startS = "";// 开始时间状态
									String endS = "";// 结束时间状态
									String jiaTime = "";// 增加后的时间
									String startBefore = "";// 开始前时间
									String endLater = "";// 结束后时间
									int kuaNum = 12 * 60;// 跨天机算增加时长
									if (attence == null) {
										// 先判断时段
										for (BanCiTime ban1 : banCi
												.getBanCiTime()) {
											if (ban1 != null && bol) {
												startBefore = ban1
														.getStartBeforeTime();// 开始前时间
												endLater = ban1
														.getEndLaterTime();// 结束后时间
												startS = ban1.getDayType();// 开始时间状态
												endS = ban1.getEdayType();// 结束时间状态
												startBefore = Util
														.getminuteAfter(
																startBefore,
																kuaNum)
														.substring(0, 5);
												endLater = Util.getminuteAfter(
														endLater, kuaNum)
														.substring(0, 5);
												jiaTime = Util.getminuteAfter(
														time + ":00", kuaNum)
														.substring(0, 5);
												if (jiaTime
														.compareTo(startBefore) >= 0
														&& endLater
																.compareTo(jiaTime) >= 0) {// 在此班次时段内
													if ("次日".equals(startS)
															&& "次日"
																	.equals(endS)) {// 班次时间表中开始时间和结束时间都为次日，添加次日考勤
													// 判断前一天有无汇总记录
														date = Util
																.getSpecifiedDayAfter(
																		date,
																		-1);
														List list_attendanceqian = totalDao
																.query(
																		"from Attendance where userId = ? and dateTime = ?",
																		users
																				.getId(),
																		date);
														if (list_attendanceqian != null
																&& list_attendanceqian
																		.size() > 0) {
															attence = (Attendance) list_attendanceqian
																	.get(0);
														}
														if (attence == null) {// 为空。添加昨天的汇总记录
															if (isbanci1(users
																	.getId(),
																	date,
																	totalDao)) {
																// 添加当日汇总表
																attence = addAttendAndFu(
																		users,
																		date,
																		time,
																		banCi);
															} else {
																continue;
															}
														} else {// 不为空，已有副表
														}
														// 添加前一天的记录
													} else {
														if (endLater
																.compareTo("12:00") > 0) {
															if (jiaTime
																	.compareTo("12:00") > 0) {
																// 判断前一天有无汇总记录
																date = Util
																		.getSpecifiedDayAfter(
																				date,
																				-1);
																List list_attendanceqian = totalDao
																		.query(
																				"from Attendance where userId = ? and dateTime = ?",
																				users
																						.getId(),
																				date);
																if (list_attendanceqian != null
																		&& list_attendanceqian
																				.size() > 0) {
																	attence = (Attendance) list_attendanceqian
																			.get(0);
																}
																if (attence == null) {// 为空。添加昨天的汇总记录
																	if (isbanci1(
																			users
																					.getId(),
																			date,
																			totalDao)) {
																		// 添加当日汇总表
																		attence = addAttendAndFu(
																				users,
																				date,
																				time,
																				banCi);
																	} else {
																		continue;
																	}
																} else {// 不为空，已有副表
																}
															} else {
																if (isbanci1(
																		users
																				.getId(),
																		date,
																		totalDao)) {
																	// 添加当日汇总表
																	attence = addAttendAndFu(
																			users,
																			date,
																			time,
																			banCi);
																} else {
																	continue;
																}
															}
														} else {
															// 当天要在班次日期内
															if (isbanci1(users
																	.getId(),
																	date,
																	totalDao)) {
																// 添加当日汇总表
																attence = addAttendAndFu(
																		users,
																		date,
																		time,
																		banCi);
															} else {
																continue;
															}
														}
													}
													bol = false;
													ban = ban1;
												} else {
													if (ban1.getDuan() != null
															&& ban1.getDuan() == 1
															&& startBefore
																	.compareTo(time) >= 0) {// 查询是否有加班记录
													}
												}
											}
										}
										if (ban != null && !bol) {
											String start = Util.getminuteAfter(
													ban.getStartTime(), kuaNum)
													.substring(0, 5);// 开始时间
											String end = Util.getminuteAfter(
													ban.getEndTime(), kuaNum)
													.substring(0, 5);// 结束时间
											startBefore = Util.getminuteAfter(
													ban.getStartBeforeTime(),
													kuaNum).substring(0, 5);// 开始前时间
											String startLater = Util
													.getminuteAfter(
															ban
																	.getStartLaterTime(),
															kuaNum).substring(
															0, 5);// 开始后时间
											String endBefore = Util
													.getminuteAfter(
															ban
																	.getEndBeforeTime(),
															kuaNum).substring(
															0, 5);// 结束前时间
											endLater = Util.getminuteAfter(
													ban.getEndLaterTime(),
													kuaNum).substring(0, 5);// 结束后时间
											jiaTime = Util.getminuteAfter(
													time + ":00", kuaNum)
													.substring(0, 5);
											String nnssTime = "";// 上班是否覆盖
											String nnexTime = "";// 下班是否覆盖
											// 查询附表
											AttendanceFu attendanceFu = null;
											List<AttendanceFu> attendanceFuList = totalDao
													.query(
															"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
															ban.getDuan(),
															attence
																	.getAttendanceId(),
															users.getId());
											if (attendanceFuList != null
													&& attendanceFuList.size() > 0) {
												attendanceFu = attendanceFuList
														.get(0);
											}
											if (jiaTime.compareTo(startBefore) >= 0
													&& startLater
															.compareTo(jiaTime) >= 0) {// 上班有效打卡时段
												if (attendanceFu == null) {
													continue;
												}
												if (attendanceFu
														.getWorkDateTime() == null
														|| ""
																.equals(attendanceFu
																		.getWorkDateTime())) {
													attendanceFu
															.setWorkDateTime(time);
												} else {
													nnssTime = Util
															.getminuteAfter1(
																	attendanceFu
																			.getWorkDateTime(),
																	kuaNum);
													if (nnssTime
															.compareTo(jiaTime) > 0) {
														attendanceFu
																.setWorkDateTime(time);
													}
												}
												if (attendanceFu.getTimeAll() == null
														|| ""
																.equals(attendanceFu
																		.getTimeAll()))
													attendanceFu
															.setTimeAll(time);// 时间
												else
													attendanceFu
															.setTimeAll(attendanceFu
																	.getTimeAll()
																	+ "; "
																	+ time);// 时间
												bol = false;
											} else if (endLater
													.compareTo(jiaTime) >= 0
													&& jiaTime
															.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
												if (attendanceFu == null) {
													continue;
												}
												if (attendanceFu
														.getClosingDateTime() == null
														|| ""
																.equals(attendanceFu
																		.getClosingDateTime())) {
													attendanceFu
															.setClosingDateTime(time);
												} else {
													nnexTime = Util
															.getminuteAfter1(
																	attendanceFu
																			.getClosingDateTime(),
																	kuaNum);
													if (ban.getXiaFu() != null
															&& ban.getXiaFu()) {// 下班记录覆盖
														if (nnexTime
																.compareTo(jiaTime) < 0) {// 当前时间大于记录时间时候才覆盖
															attendanceFu
																	.setClosingDateTime(time);
														}
													} else {// 如果下班时间不覆盖。当前时间小于的时候按照小于时间计算
														if (nnexTime
																.compareTo(jiaTime) > 0) {
															attendanceFu
																	.setClosingDateTime(time);
														}
													}
												}
												if (attendanceFu.getTimeAll() == null
														|| ""
																.equals(attendanceFu
																		.getTimeAll()))
													attendanceFu
															.setTimeAll(time);// 时间
												else
													attendanceFu
															.setTimeAll(attendanceFu
																	.getTimeAll()
																	+ "; "
																	+ time);// 时间
												bol = false;
											}
											if (!bol) {
												String kuasstime = "";// 跨夜计算带入参数(开始)
												String kuaextime = "";// 跨夜计算带入参数(结束)
												// 找到对应时段，计算对应时长
												if (ban.getStartIsDaka() != null
														&& ban.getStartIsDaka()
														&& ban.getEndIsDaka() != null
														&& ban.getEndIsDaka()) {
													// 开始和结束时间都需要打卡的
													boolean chidao = false;// 迟到
													boolean zaotui = false;// 早退
													if (attendanceFu
															.getWorkDateTime() == null) {
														sstime = null;
													} else {
														kuasstime = Util
																.getminuteAfter1(
																		attendanceFu
																				.getWorkDateTime(),
																		kuaNum);
														if (kuasstime
																.compareTo(start) > 0) {// 迟到情况
															sstime = kuasstime;
															chidao = true;
														} else
															sstime = start;
													}
													if (attendanceFu
															.getClosingDateTime() == null) {
														extime = null;
													} else {
														kuaextime = Util
																.getminuteAfter1(
																		attendanceFu
																				.getClosingDateTime(),
																		kuaNum);
														if (kuaextime
																.compareTo(end) < 0) {// 早退情况
															extime = kuaextime;
															zaotui = true;
														} else
															extime = end;
													}
													if (sstime != null
															&& extime != null) {// 汇总时长
														attendanceFu
																.setWorkTime((int) (Util
																		.getWorkTime2(
																				sstime,
																				extime) / 60000));// 工作时长
														if (chidao)
															attendanceFu
																	.setLateTime((int) (Util
																			.getWorkTime2(
																					start,
																					kuasstime) / 60000));
														else
															attendanceFu
																	.setLateTime(0);
														if (zaotui)
															attendanceFu
																	.setEarlyTime((int) (Util
																			.getWorkTime2(
																					kuaextime,
																					end) / 60000));
														else
															attendanceFu
																	.setEarlyTime(0);
														attendanceFu
																.setQueqinTime(attendanceFu
																		.getWorkBiaoTime()
																		- attendanceFu
																				.getWorkTime()
																		- attendanceFu
																				.getLateTime()
																		- attendanceFu
																				.getEarlyTime());// 缺勤时长
														if (attendanceFu
																.getWorkBiaoTime()
																.intValue() == attendanceFu
																.getWorkTime()) {
															attendanceFu
																	.setAttendanceStatus("正常");
														}
													}
												} else if (ban.getStartIsDaka() != null
														&& !ban
																.getStartIsDaka()
														&& ban.getEndIsDaka() != null
														&& ban.getEndIsDaka()) {
													// 上班时间不用打卡，查询上一个时段
													if (ban.getDuan() != null
															&& ban.getDuan() > 1) {
														// 查询上一班次时段
														BanCiTime banCiTime2 = null;
														List<BanCiTime> ciTimes = totalDao
																.query(
																		"from BanCiTime where banCi.id = ? and duan = ?",
																		banCi
																				.getId(),
																		ban
																				.getDuan() - 1);
														if (ciTimes != null
																&& ciTimes
																		.size() > 0) {
															banCiTime2 = ciTimes
																	.get(0);
														}
														if (banCiTime2 != null) {
															if (banCiTime2
																	.getStartIsDaka() != null
																	&& banCiTime2
																			.getStartIsDaka()
																	&& banCiTime2
																			.getEndIsDaka() != null
																	&& !banCiTime2
																			.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
																// 查询附表
																AttendanceFu attendanceFu1 = null;
																List<AttendanceFu> attendanceFuList1 = totalDao
																		.query(
																				"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																				ban
																						.getDuan() - 1,
																				attence
																						.getAttendanceId(),
																				users
																						.getId());
																if (attendanceFuList1 != null
																		&& attendanceFuList1
																				.size() > 0) {
																	attendanceFu1 = attendanceFuList1
																			.get(0);
																}
																String start1 = Util
																		.getminuteAfter(
																				banCiTime2
																						.getStartTime(),
																				kuaNum)
																		.substring(
																				0,
																				5);
																if (attendanceFu1 == null) {
																	continue;
																}
																boolean chidao = false;// 迟到
																boolean zaotui = false;// 早退
																if (attendanceFu1
																		.getWorkDateTime() == null) {
																	sstime = null;
																} else {
																	kuasstime = Util
																			.getminuteAfter1(
																					attendanceFu1
																							.getWorkDateTime(),
																					kuaNum);
																	if (kuasstime
																			.compareTo(start1) > 0) {// 迟到情况
																		sstime = kuasstime;
																		chidao = true;
																	} else
																		sstime = start1;
																}
																if (attendanceFu
																		.getClosingDateTime() == null) {
																	extime = null;
																} else {
																	kuaextime = Util
																			.getminuteAfter1(
																					attendanceFu
																							.getClosingDateTime(),
																					kuaNum);
																	if (kuaextime
																			.compareTo(end) < 0) {// 早退情况
																		extime = kuaextime;
																		zaotui = true;
																	} else
																		extime = end;
																}

																if (sstime != null
																		&& extime != null) {// 汇总时长
																	attendanceFu1
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							sstime,
																							banCiTime2
																									.getEndTime()
																									.substring(
																											0,
																											5)) / 60000));// 计算上段时间的工作时长
																	if (chidao)
																		attendanceFu1
																				.setLateTime((int) (Util
																						.getWorkTime2(
																								start1,
																								kuasstime) / 60000));
																	else
																		attendanceFu1
																				.setLateTime(0);
																	attendanceFu1
																			.setQueqinTime(attendanceFu1
																					.getWorkBiaoTime()
																					- attendanceFu1
																							.getWorkTime()
																					- attendanceFu1
																							.getLateTime());// 缺勤时长
																	if (attendanceFu1
																			.getWorkBiaoTime() == attendanceFu1
																			.getWorkTime()
																			.intValue()) {
																		attendanceFu1
																				.setAttendanceStatus("正常");
																	}
																	totalDao
																			.update2(attendanceFu1);
																	attendanceFu
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							start,
																							extime) / 60000));// 计算本段时间的工作时长
																	if (zaotui)
																		attendanceFu
																				.setEarlyTime((int) (Util
																						.getWorkTime2(
																								kuaextime,
																								end) / 60000));
																	else
																		attendanceFu
																				.setEarlyTime(0);
																	attendanceFu
																			.setQueqinTime(attendanceFu
																					.getWorkBiaoTime()
																					- attendanceFu
																							.getWorkTime()
																					- attendanceFu
																							.getEarlyTime());// 缺勤时长
																	if (attendanceFu
																			.getWorkBiaoTime()
																			.intValue() == attendanceFu
																			.getWorkTime()) {
																		attendanceFu
																				.setAttendanceStatus("正常");
																	}
																}
															} else {// 表示跟上一时段无关系。
																// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
																boolean zaotui = false;
																if (attendanceFu
																		.getClosingDateTime() == null) {
																	extime = null;
																} else {
																	kuaextime = Util
																			.getminuteAfter1(
																					attendanceFu
																							.getClosingDateTime(),
																					kuaNum);
																	if (kuaextime
																			.compareTo(end) < 0) {// 早退情况
																		extime = kuaextime;
																		zaotui = true;
																	} else
																		extime = end;
																}
																if (extime != null) {// 汇总时长
																	attendanceFu
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							start,
																							extime) / 60000));// 工作时长
																	if (zaotui)
																		attendanceFu
																				.setEarlyTime((int) (Util
																						.getWorkTime2(
																								kuaextime,
																								end) / 60000));
																	else
																		attendanceFu
																				.setEarlyTime(0);
																	attendanceFu
																			.setQueqinTime(attendanceFu
																					.getWorkBiaoTime()
																					- attendanceFu
																							.getWorkTime()
																					- attendanceFu
																							.getEarlyTime());// 缺勤时长
																	if (attendanceFu
																			.getWorkBiaoTime()
																			.intValue() == attendanceFu
																			.getWorkTime()) {
																		attendanceFu
																				.setAttendanceStatus("正常");
																	}
																}
															}
														}
													}
												} else if (ban.getStartIsDaka() != null
														&& ban.getStartIsDaka()
														&& ban.getEndIsDaka() != null
														&& !ban.getEndIsDaka()) {
													// 下班时间不用打卡，查询下一时段汇总。
													if (ban.getDuan() != null
															&& ban.getDuan() < banCi
																	.getBanCiTime()
																	.size()) {
														// 查询上一班次时段
														BanCiTime banCiTime2 = null;
														List<BanCiTime> ciTimes = totalDao
																.query(
																		"from BanCiTime where banCi.id = ? and duan = ?",
																		banCi
																				.getId(),
																		ban
																				.getDuan() - 1);
														if (ciTimes != null
																&& ciTimes
																		.size() > 0) {
															banCiTime2 = ciTimes
																	.get(0);
														}
														if (banCiTime2 != null) {
															if (banCiTime2
																	.getStartIsDaka() != null
																	&& !banCiTime2
																			.getStartIsDaka()
																	&& ban
																			.getEndIsDaka() != null
																	&& ban
																			.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
																// 查询附表
																AttendanceFu attendanceFu1 = null;
																List<AttendanceFu> attendanceFuList1 = totalDao
																		.query(
																				"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																				ban
																						.getDuan() + 1,
																				attence
																						.getAttendanceId(),
																				users
																						.getId());
																if (attendanceFuList1 != null
																		&& attendanceFuList1
																				.size() > 0) {
																	attendanceFu1 = attendanceFuList1
																			.get(0);
																}
																String start1 = Util
																		.getminuteAfter(
																				banCiTime2
																						.getStartTime(),
																				kuaNum)
																		.substring(
																				0,
																				5);
																if (attendanceFu1 == null) {
																	continue;
																}
																boolean chidao = false;// 迟到
																boolean zaotui = false;// 早退
																if (attendanceFu
																		.getWorkDateTime() == null) {
																	sstime = null;
																} else {
																	kuasstime = Util
																			.getminuteAfter1(
																					attendanceFu1
																							.getWorkDateTime(),
																					kuaNum);
																	if (kuasstime
																			.compareTo(start1) > 0) {// 迟到情况
																		sstime = kuasstime;
																		chidao = true;
																	} else
																		sstime = start1;
																}
																if (attendanceFu1
																		.getClosingDateTime() == null) {
																	extime = null;
																} else {
																	kuaextime = Util
																			.getminuteAfter1(
																					attendanceFu1
																							.getClosingDateTime(),
																					kuaNum);
																	if (kuaextime
																			.compareTo(end) < 0) {// 早退情况
																		extime = kuaextime;
																		zaotui = true;
																	} else
																		extime = end;
																}

																if (sstime != null
																		&& extime != null) {// 汇总时长
																	attendanceFu1
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							sstime,
																							banCiTime2
																									.getEndTime()
																									.substring(
																											0,
																											5)) / 60000));// 计算上段时间的工作时长
																	if (chidao)
																		attendanceFu1
																				.setLateTime((int) (Util
																						.getWorkTime2(
																								start1,
																								kuasstime) / 60000));
																	else
																		attendanceFu1
																				.setLateTime(0);
																	attendanceFu1
																			.setQueqinTime(attendanceFu1
																					.getWorkBiaoTime()
																					- attendanceFu1
																							.getWorkTime()
																					- attendanceFu1
																							.getLateTime());// 缺勤时长
																	if (attendanceFu1
																			.getWorkBiaoTime() == attendanceFu1
																			.getWorkTime()
																			.intValue()) {
																		attendanceFu1
																				.setAttendanceStatus("正常");
																	}
																	totalDao
																			.update2(attendanceFu1);
																	attendanceFu
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							start,
																							extime) / 60000));// 计算本段时间的工作时长
																	if (zaotui)
																		attendanceFu
																				.setEarlyTime((int) (Util
																						.getWorkTime2(
																								kuaextime,
																								end) / 60000));
																	else
																		attendanceFu
																				.setEarlyTime(0);
																	attendanceFu
																			.setQueqinTime(attendanceFu
																					.getWorkBiaoTime()
																					- attendanceFu
																							.getWorkTime()
																					- attendanceFu
																							.getEarlyTime());// 缺勤时长
																	if (attendanceFu
																			.getWorkBiaoTime()
																			.intValue() == attendanceFu
																			.getWorkTime()) {
																		attendanceFu
																				.setAttendanceStatus("正常");
																	}
																}
															} else {// 表示跟上一时段无关系。
																// 默认认为下班不用打卡。只判断有无迟到时长。
																boolean chidao = false;
																if (attendanceFu
																		.getWorkDateTime() == null) {
																	sstime = null;
																} else {
																	kuasstime = Util
																			.getminuteAfter1(
																					attendanceFu
																							.getWorkDateTime(),
																					kuaNum);
																	if (kuasstime
																			.compareTo(start) > 0) {// 迟到情况
																		sstime = kuasstime;
																		chidao = true;
																	} else
																		sstime = start;
																}
																if (sstime != null) {// 汇总时长
																	attendanceFu
																			.setWorkTime((int) (Util
																					.getWorkTime2(
																							sstime,
																							end) / 60000));// 工作时长
																	if (chidao)
																		attendanceFu
																				.setLateTime((int) (Util
																						.getWorkTime2(
																								start,
																								kuasstime) / 60000));
																	else
																		attendanceFu
																				.setLateTime(0);
																	attendanceFu
																			.setQueqinTime(attendanceFu
																					.getWorkBiaoTime()
																					- attendanceFu
																							.getWorkTime()
																					- attendanceFu
																							.getLateTime());// 缺勤时长
																	if (attendanceFu
																			.getWorkBiaoTime()
																			.intValue() == attendanceFu
																			.getWorkTime()) {
																		attendanceFu
																				.setAttendanceStatus("正常");
																	}
																}
															}
														}
													}
												} else {// 其他情况不处理
												}
											}
											totalDao.update2(attendanceFu);
										}
									} else {
										if ("2".equals(attence.getExceptTags()))
											continue;
										// 当天时段不为空 判断时段
										for (BanCiTime ban1 : banCi
												.getBanCiTime()) {
											if (ban1 != null && bol) {
												ban = ban1;
												String start = Util
														.getminuteAfter(
																ban
																		.getStartTime(),
																kuaNum)
														.substring(0, 5);// 开始时间
												String end = Util
														.getminuteAfter(
																ban
																		.getEndTime(),
																kuaNum)
														.substring(0, 5);// 结束时间
												startBefore = Util
														.getminuteAfter(
																ban
																		.getStartBeforeTime(),
																kuaNum)
														.substring(0, 5);// 开始前时间
												String startLater = Util
														.getminuteAfter(
																ban
																		.getStartLaterTime(),
																kuaNum)
														.substring(0, 5);// 开始后时间
												String endBefore = Util
														.getminuteAfter(
																ban
																		.getEndBeforeTime(),
																kuaNum)
														.substring(0, 5);// 结束前时间
												endLater = Util.getminuteAfter(
														ban.getEndLaterTime(),
														kuaNum).substring(0, 5);// 结束后时间
												jiaTime = Util.getminuteAfter(
														time + ":00", kuaNum)
														.substring(0, 5);
												// 查询附表
												AttendanceFu attendanceFu = null;
												List<AttendanceFu> attendanceFuList = totalDao
														.query(
																"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																ban.getDuan(),
																attence
																		.getAttendanceId(),
																users.getId());
												if (attendanceFuList != null
														&& attendanceFuList
																.size() > 0) {
													attendanceFu = attendanceFuList
															.get(0);
												}
												if (jiaTime
														.compareTo(startBefore) >= 0
														&& startLater
																.compareTo(jiaTime) >= 0) {// 上班有效打卡时段
													if (attendanceFu == null) {
														continue;
													}
													fuZhiWordDateTime(time,
															attendanceFu);
													if (attendanceFu
															.getTimeAll() == null
															|| ""
																	.equals(attendanceFu
																			.getTimeAll()))
														attendanceFu
																.setTimeAll(time);// 时间
													else
														attendanceFu
																.setTimeAll(attendanceFu
																		.getTimeAll()
																		+ "; "
																		+ time);// 时间
													bol = false;
												} else if (endLater
														.compareTo(jiaTime) >= 0
														&& jiaTime
																.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
													if (attendanceFu == null) {
														continue;
													}
													fuZhiClosingDateTime(time,
															ban, attendanceFu);
													if (attendanceFu
															.getTimeAll() == null
															|| ""
																	.equals(attendanceFu
																			.getTimeAll()))
														attendanceFu
																.setTimeAll(time);// 时间
													else
														attendanceFu
																.setTimeAll(attendanceFu
																		.getTimeAll()
																		+ "; "
																		+ time);// 时间
													bol = false;
												}
												if (!bol) {
													String kuasstime = "";// 跨夜计算带入参数
													String kuaextime = "";
													// 找到对应时段，计算对应时长
													if (ban.getStartIsDaka() != null
															&& ban
																	.getStartIsDaka()
															&& ban
																	.getEndIsDaka() != null
															&& ban
																	.getEndIsDaka()) {
														// 开始和结束时间都需要打卡的
														boolean chidao = false;// 迟到
														boolean zaotui = false;// 早退
														if (attendanceFu
																.getWorkDateTime() == null) {
															sstime = null;
														} else {
															kuasstime = Util
																	.getminuteAfter1(
																			attendanceFu
																					.getWorkDateTime(),
																			kuaNum);
															if (kuasstime
																	.compareTo(start) > 0) {// 迟到情况
																sstime = kuasstime;
																chidao = true;
															} else
																sstime = start;
														}
														if (attendanceFu
																.getClosingDateTime() == null) {
															extime = null;
														} else {
															kuaextime = Util
																	.getminuteAfter1(
																			attendanceFu
																					.getClosingDateTime(),
																			kuaNum);
															if (kuaextime
																	.compareTo(end) < 0) {// 早退情况
																extime = kuaextime;
																zaotui = true;
															} else
																extime = end;
														}

														if (sstime != null
																&& extime != null) {// 汇总时长
															attendanceFu
																	.setWorkTime((int) (Util
																			.getWorkTime2(
																					sstime,
																					extime) / 60000));// 工作时长
															if (chidao)
																attendanceFu
																		.setLateTime((int) (Util
																				.getWorkTime2(
																						start,
																						kuasstime) / 60000));
															else
																attendanceFu
																		.setLateTime(0);
															if (zaotui)
																attendanceFu
																		.setEarlyTime((int) (Util
																				.getWorkTime2(
																						kuaextime,
																						end) / 60000));
															else
																attendanceFu
																		.setEarlyTime(0);
															attendanceFu
																	.setQueqinTime(attendanceFu
																			.getWorkBiaoTime()
																			- attendanceFu
																					.getWorkTime()
																			- attendanceFu
																					.getLateTime()
																			- attendanceFu
																					.getEarlyTime());// 缺勤时长
															if (attendanceFu
																	.getWorkBiaoTime()
																	.intValue() == attendanceFu
																	.getWorkTime()) {
																attendanceFu
																		.setAttendanceStatus("正常");
															}
														}
													} else if (ban
															.getStartIsDaka() != null
															&& !ban
																	.getStartIsDaka()
															&& ban
																	.getEndIsDaka() != null
															&& ban
																	.getEndIsDaka()) {
														// 上班时间不用打卡，查询上一个时段
														if (ban.getDuan() != null
																&& ban
																		.getDuan() > 1) {
															// 查询上一班次时段
															BanCiTime banCiTime2 = null;
															List<BanCiTime> ciTimes = totalDao
																	.query(
																			"from BanCiTime where banCi.id = ? and duan = ?",
																			banCi
																					.getId(),
																			ban
																					.getDuan() - 1);
															if (ciTimes != null
																	&& ciTimes
																			.size() > 0) {
																banCiTime2 = ciTimes
																		.get(0);
															}
															if (banCiTime2 != null) {
																if (banCiTime2
																		.getStartIsDaka() != null
																		&& banCiTime2
																				.getStartIsDaka()
																		&& banCiTime2
																				.getEndIsDaka() != null
																		&& !banCiTime2
																				.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
																	// 查询附表
																	AttendanceFu attendanceFu1 = null;
																	List<AttendanceFu> attendanceFuList1 = totalDao
																			.query(
																					"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																					ban
																							.getDuan() - 1,
																					attence
																							.getAttendanceId(),
																					users
																							.getId());
																	if (attendanceFuList1 != null
																			&& attendanceFuList1
																					.size() > 0) {
																		attendanceFu1 = attendanceFuList1
																				.get(0);
																	}
																	String start1 = Util
																			.getminuteAfter(
																					banCiTime2
																							.getStartTime(),
																					kuaNum)
																			.substring(
																					0,
																					5);
																	if (attendanceFu1 == null) {
																		continue;
																	}
																	boolean chidao = false;// 迟到
																	boolean zaotui = false;// 早退
																	if (attendanceFu1
																			.getWorkDateTime() == null) {
																		sstime = null;
																	} else {
																		kuasstime = Util
																				.getminuteAfter1(
																						attendanceFu1
																								.getWorkDateTime(),
																						kuaNum);
																		if (kuasstime
																				.compareTo(start1) > 0) {// 迟到情况
																			sstime = kuasstime;
																			chidao = true;
																		} else
																			sstime = start1;
																	}
																	if (attendanceFu
																			.getClosingDateTime() == null) {
																		extime = null;
																	} else {
																		kuaextime = Util
																				.getminuteAfter1(
																						attendanceFu
																								.getClosingDateTime(),
																						kuaNum);
																		if (kuaextime
																				.compareTo(end) < 0) {// 早退情况
																			extime = kuaextime;
																			zaotui = true;
																		} else
																			extime = end;
																	}

																	if (sstime != null
																			&& extime != null) {// 汇总时长
																		attendanceFu1
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								sstime,
																								banCiTime2
																										.getEndTime()
																										.substring(
																												0,
																												5)) / 60000));// 计算上段时间的工作时长
																		if (chidao)
																			attendanceFu1
																					.setLateTime((int) (Util
																							.getWorkTime2(
																									start1,
																									kuasstime) / 60000));
																		else
																			attendanceFu1
																					.setLateTime(0);
																		attendanceFu1
																				.setQueqinTime(attendanceFu1
																						.getWorkBiaoTime()
																						- attendanceFu1
																								.getWorkTime()
																						- attendanceFu1
																								.getLateTime());// 缺勤时长
																		if (attendanceFu1
																				.getWorkBiaoTime() == attendanceFu1
																				.getWorkTime()
																				.intValue()) {
																			attendanceFu1
																					.setAttendanceStatus("正常");
																		}
																		totalDao
																				.update2(attendanceFu1);
																		attendanceFu
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								start,
																								extime) / 60000));// 计算本段时间的工作时长
																		if (zaotui)
																			attendanceFu
																					.setEarlyTime((int) (Util
																							.getWorkTime2(
																									kuaextime,
																									end) / 60000));
																		else
																			attendanceFu
																					.setEarlyTime(0);
																		attendanceFu
																				.setQueqinTime(attendanceFu
																						.getWorkBiaoTime()
																						- attendanceFu
																								.getWorkTime()
																						- attendanceFu
																								.getEarlyTime());// 缺勤时长
																		if (attendanceFu
																				.getWorkBiaoTime()
																				.intValue() == attendanceFu
																				.getWorkTime()) {
																			attendanceFu
																					.setAttendanceStatus("正常");
																		}
																	}
																} else {// 表示跟上一时段无关系。
																	// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
																	boolean zaotui = false;
																	if (attendanceFu
																			.getClosingDateTime() == null) {
																		extime = null;
																	} else {
																		kuaextime = Util
																				.getminuteAfter1(
																						attendanceFu
																								.getClosingDateTime(),
																						kuaNum);
																		if (kuaextime
																				.compareTo(end) < 0) {// 早退情况
																			extime = kuaextime;
																			zaotui = true;
																		} else
																			extime = end;
																	}
																	if (extime != null) {// 汇总时长
																		attendanceFu
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								start,
																								extime) / 60000));// 工作时长
																		if (zaotui)
																			attendanceFu
																					.setEarlyTime((int) (Util
																							.getWorkTime2(
																									kuaextime,
																									end) / 60000));
																		else
																			attendanceFu
																					.setEarlyTime(0);
																		attendanceFu
																				.setQueqinTime(attendanceFu
																						.getWorkBiaoTime()
																						- attendanceFu
																								.getWorkTime()
																						- attendanceFu
																								.getEarlyTime());// 缺勤时长
																		if (attendanceFu
																				.getWorkBiaoTime()
																				.intValue() == attendanceFu
																				.getWorkTime()) {
																			attendanceFu
																					.setAttendanceStatus("正常");
																		}
																	}
																}
															}
														}
													} else if (ban
															.getStartIsDaka() != null
															&& ban
																	.getStartIsDaka()
															&& ban
																	.getEndIsDaka() != null
															&& !ban
																	.getEndIsDaka()) {
														// 下班时间不用打卡，查询下一时段汇总。
														if (ban.getDuan() != null
																&& ban
																		.getDuan() < banCi
																		.getBanCiTime()
																		.size()) {
															// 查询上一班次时段
															BanCiTime banCiTime2 = null;
															List<BanCiTime> ciTimes = totalDao
																	.query(
																			"from BanCiTime where banCi.id = ? and duan = ?",
																			banCi
																					.getId(),
																			ban
																					.getDuan() - 1);
															if (ciTimes != null
																	&& ciTimes
																			.size() > 0) {
																banCiTime2 = ciTimes
																		.get(0);
															}
															if (banCiTime2 != null) {
																if (banCiTime2
																		.getStartIsDaka() != null
																		&& !banCiTime2
																				.getStartIsDaka()
																		&& ban
																				.getEndIsDaka() != null
																		&& ban
																				.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
																	// 查询附表
																	AttendanceFu attendanceFu1 = null;
																	List<AttendanceFu> attendanceFuList1 = totalDao
																			.query(
																					"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																					ban
																							.getDuan() + 1,
																					attence
																							.getAttendanceId(),
																					users
																							.getId());
																	if (attendanceFuList1 != null
																			&& attendanceFuList1
																					.size() > 0) {
																		attendanceFu1 = attendanceFuList1
																				.get(0);
																	}
																	String start1 = Util
																			.getminuteAfter(
																					banCiTime2
																							.getStartTime(),
																					kuaNum)
																			.substring(
																					0,
																					5);
																	if (attendanceFu1 == null) {
																		continue;
																	}
																	boolean chidao = false;// 迟到
																	boolean zaotui = false;// 早退
																	if (attendanceFu
																			.getWorkDateTime() == null) {
																		sstime = null;
																	} else {
																		kuasstime = Util
																				.getminuteAfter1(
																						attendanceFu1
																								.getWorkDateTime(),
																						kuaNum);
																		if (kuasstime
																				.compareTo(start1) > 0) {// 迟到情况
																			sstime = kuasstime;
																			chidao = true;
																		} else
																			sstime = start1;
																	}
																	if (attendanceFu1
																			.getClosingDateTime() == null) {
																		extime = null;
																	} else {
																		kuaextime = Util
																				.getminuteAfter1(
																						attendanceFu1
																								.getClosingDateTime(),
																						kuaNum);
																		if (kuaextime
																				.compareTo(end) < 0) {// 早退情况
																			extime = kuaextime;
																			zaotui = true;
																		} else
																			extime = end;
																	}

																	if (sstime != null
																			&& extime != null) {// 汇总时长
																		attendanceFu1
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								sstime,
																								banCiTime2
																										.getEndTime()
																										.substring(
																												0,
																												5)) / 60000));// 计算上段时间的工作时长
																		if (chidao)
																			attendanceFu1
																					.setLateTime((int) (Util
																							.getWorkTime2(
																									start1,
																									kuasstime) / 60000));
																		else
																			attendanceFu1
																					.setLateTime(0);
																		attendanceFu1
																				.setQueqinTime(attendanceFu1
																						.getWorkBiaoTime()
																						- attendanceFu1
																								.getWorkTime()
																						- attendanceFu1
																								.getLateTime());// 缺勤时长
																		if (attendanceFu1
																				.getWorkBiaoTime() == attendanceFu1
																				.getWorkTime()
																				.intValue()) {
																			attendanceFu1
																					.setAttendanceStatus("正常");
																		}
																		totalDao
																				.update2(attendanceFu1);
																		attendanceFu
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								start,
																								extime) / 60000));// 计算本段时间的工作时长
																		if (zaotui)
																			attendanceFu
																					.setEarlyTime((int) (Util
																							.getWorkTime2(
																									kuaextime,
																									end) / 60000));
																		else
																			attendanceFu
																					.setEarlyTime(0);
																		attendanceFu
																				.setQueqinTime(attendanceFu
																						.getWorkBiaoTime()
																						- attendanceFu
																								.getWorkTime()
																						- attendanceFu
																								.getEarlyTime());// 缺勤时长
																		if (attendanceFu
																				.getWorkBiaoTime()
																				.intValue() == attendanceFu
																				.getWorkTime()) {
																			attendanceFu
																					.setAttendanceStatus("正常");
																		}
																	}
																} else {// 表示跟上一时段无关系。
																	// 默认认为下班不用打卡。只判断有无迟到时长。
																	boolean chidao = false;
																	if (attendanceFu
																			.getWorkDateTime() == null) {
																		sstime = null;
																	} else {
																		kuasstime = Util
																				.getminuteAfter1(
																						attendanceFu
																								.getWorkDateTime(),
																						kuaNum);
																		if (kuasstime
																				.compareTo(start) > 0) {// 迟到情况
																			sstime = kuasstime;
																			chidao = true;
																		} else
																			sstime = start;
																	}
																	if (sstime != null) {// 汇总时长
																		attendanceFu
																				.setWorkTime((int) (Util
																						.getWorkTime2(
																								sstime,
																								end) / 60000));// 工作时长
																		if (chidao)
																			attendanceFu
																					.setLateTime((int) (Util
																							.getWorkTime2(
																									start,
																									kuasstime) / 60000));
																		else
																			attendanceFu
																					.setLateTime(0);
																		attendanceFu
																				.setQueqinTime(attendanceFu
																						.getWorkBiaoTime()
																						- attendanceFu
																								.getWorkTime()
																						- attendanceFu
																								.getLateTime());// 缺勤时长
																		if (attendanceFu
																				.getWorkBiaoTime()
																				.intValue() == attendanceFu
																				.getWorkTime()) {
																			attendanceFu
																					.setAttendanceStatus("正常");
																		}
																	}
																}
															}
														}
													} else {// 其他情况不处理
													}
												}
												totalDao.update2(attendanceFu);
											}
										}
									}
									// 更新汇总表信息
									System.out.println(attence
											.getAttendanceId()
											+ "+++++++++");
									updateAttend(attence);
									// 无需添加汇总表
									continue;
								} catch (Exception e) {
									// TODO Auto-generated catch block
									// 添加汇总表
									continue;
								}
							} else {
								// 不跨天班次计算
								if (bb) {
									try {
										// 生成汇总记录
										List list_attendance = totalDao
												.query(
														"from Attendance where userId = ? and dateTime = ?",
														users.getId(), date);
										Attendance attence = null;
										if (list_attendance != null
												&& list_attendance.size() > 0) {
											attence = (Attendance) list_attendance
													.get(0);
										}
										if (attence == null) {
											attence = addAttendandFu(totalDao,
													users, date, time, banCi);
										}
										if ("2".equals(attence.getExceptTags()))
											continue;
										// 直接更新当日考勤汇总信息
										boolean bol = true;// 已找到范围
										String sstime = null;// 上班时间
										String extime = null;// 下班时间
										// 判断此次打卡是否在有效时间段之间
										if (bol && banCi.getBanCiTime() != null) {
											for (BanCiTime ban : banCi
													.getBanCiTime()) {
												if (ban != null && bol) {
													String start = ban
															.getStartTime()
															.substring(0, 5);// 开始时间
													String end = ban
															.getEndTime()
															.substring(0, 5);// 结束时间
													String startBefore = ban
															.getStartBeforeTime()
															.substring(0, 5);// 开始前时间
													String startLater = ban
															.getStartLaterTime()
															.substring(0, 5);// 开始后时间
													String endBefore = ban
															.getEndBeforeTime()
															.substring(0, 5);// 结束前时间
													String endLater = ban
															.getEndLaterTime()
															.substring(0, 5);// 结束后时间

													Integer shichang = (int) (Util
															.getWorkTime2(
																	start, end) / 60000);// 此段工作时长
													// 查询附表
													AttendanceFu attendanceFu = null;
													List<AttendanceFu> attendanceFuList = totalDao
															.query(
																	"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																	ban
																			.getDuan(),
																	attence
																			.getAttendanceId(),
																	users
																			.getId());
													if (attendanceFuList != null
															&& attendanceFuList
																	.size() > 0) {
														attendanceFu = attendanceFuList
																.get(0);
													}
													if (time
															.compareTo(startBefore) >= 0
															&& startLater
																	.compareTo(time) >= 0) {// 上班有效打卡时段
														if (attendanceFu == null) {
															attendanceFu = addFu(
																	totalDao,
																	date, time,
																	users,
																	banCi,
																	attence,
																	ban);
														}
														fuZhiWordDateTime(time,
																attendanceFu);
														fuZhiTimeAll(time,
																attendanceFu);
														bol = false;
													} else if (endLater
															.compareTo(time) >= 0
															&& time
																	.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
														if (attendanceFu == null) {
															attendanceFu = addFu(
																	totalDao,
																	date, time,
																	users,
																	banCi,
																	attence,
																	ban);
														}
														fuZhiClosingDateTime(
																time, ban,
																attendanceFu);
														fuZhiTimeAll(time,
																attendanceFu);
														bol = false;
													}
													if (!bol) {// 找到对应时段，计算对应时长
														if (ban
																.getStartIsDaka() != null
																&& ban
																		.getStartIsDaka()
																&& ban
																		.getEndIsDaka() != null
																&& ban
																		.getEndIsDaka()) {
															towDaKa(start, end,
																	shichang,
																	attendanceFu);
														} else if (ban
																.getStartIsDaka() != null
																&& !ban
																		.getStartIsDaka()
																&& ban
																		.getEndIsDaka() != null
																&& ban
																		.getEndIsDaka()) {// 上班时间不用打卡，查询上一个时段
															if (ban.getDuan() != null
																	&& ban
																			.getDuan() > 1) {
																// 查询上一班次时段
																BanCiTime banCiTime2 = null;
																List<BanCiTime> ciTimes = totalDao
																		.query(
																				"from BanCiTime where banCi.id = ? and duan = ?",
																				banCi
																						.getId(),
																				ban
																						.getDuan() - 1);
																if (ciTimes != null
																		&& ciTimes
																				.size() > 0) {
																	banCiTime2 = ciTimes
																			.get(0);
																}
																if (banCiTime2 != null) {
																	if (banCiTime2
																			.getStartIsDaka() != null
																			&& banCiTime2
																					.getStartIsDaka()
																			&& banCiTime2
																					.getEndIsDaka() != null
																			&& !banCiTime2
																					.getEndIsDaka()) {// 时段为开始需打卡，下班不需打卡的。
																		// 查询附表
																		AttendanceFu attendanceFu1 = null;
																		List<AttendanceFu> attendanceFuList1 = totalDao
																				.query(
																						"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																						ban
																								.getDuan() - 1,
																						attence
																								.getAttendanceId(),
																						users
																								.getId());
																		if (attendanceFuList1 != null
																				&& attendanceFuList1
																						.size() > 0) {
																			attendanceFu1 = attendanceFuList1
																					.get(0);
																		}
																		String start1 = banCiTime2
																				.getStartTime()
																				.substring(
																						0,
																						5);
																		if (attendanceFu1 == null) {
																			attendanceFu1 = addFu1(
																					totalDao,
																					date,
																					time,
																					users,
																					banCi,
																					attence,
																					ban,
																					banCiTime2);
																		}
																		boolean chidao = false;// 迟到
																		boolean zaotui = false;// 早退
																		if (attendanceFu1
																				.getWorkDateTime() == null) {
																			sstime = null;
																		} else {
																			if (attendanceFu1
																					.getWorkDateTime()
																					.compareTo(
																							start1) > 0) {// 迟到情况
																				sstime = attendanceFu1
																						.getWorkDateTime();
																				chidao = true;
																			} else
																				sstime = start1;
																		}
																		if (attendanceFu
																				.getClosingDateTime() == null) {
																			extime = null;
																		} else {
																			if (attendanceFu
																					.getClosingDateTime()
																					.compareTo(
																							end) < 0) {// 早退情况
																				extime = attendanceFu
																						.getClosingDateTime();
																				zaotui = true;
																			} else
																				extime = end;
																		}

																		if (sstime != null
																				&& extime != null) {// 汇总时长
																			attendanceFu1
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									sstime,
																									banCiTime2
																											.getEndTime()
																											.substring(
																													0,
																													5)) / 60000));// 计算上段时间的工作时长
																			if (chidao)
																				attendanceFu1
																						.setLateTime((int) (Util
																								.getWorkTime2(
																										start1,
																										attendanceFu1
																												.getWorkDateTime()) / 60000));
																			else
																				attendanceFu1
																						.setLateTime(0);
																			attendanceFu1
																					.setQueqinTime(attendanceFu1
																							.getWorkBiaoTime()
																							- attendanceFu1
																									.getWorkTime()
																							- attendanceFu1
																									.getLateTime());// 缺勤时长
																			if (attendanceFu1
																					.getWorkBiaoTime() == attendanceFu1
																					.getWorkTime()
																					.intValue()) {
																				attendanceFu1
																						.setAttendanceStatus("正常");
																			}
																			totalDao
																					.update2(attendanceFu1);
																			attendanceFu
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									start,
																									extime) / 60000));// 计算本段时间的工作时长
																			if (zaotui)
																				attendanceFu
																						.setEarlyTime((int) (Util
																								.getWorkTime2(
																										attendanceFu
																												.getClosingDateTime(),
																										end) / 60000));
																			else
																				attendanceFu
																						.setEarlyTime(0);
																			attendanceFu
																					.setQueqinTime(shichang
																							- attendanceFu
																									.getWorkTime()
																							- attendanceFu
																									.getEarlyTime());// 缺勤时长
																			if (shichang
																					.intValue() == attendanceFu
																					.getWorkTime()) {
																				attendanceFu
																						.setAttendanceStatus("正常");
																			}
																		}
																	} else {// 表示跟上一时段无关系。
																		// 默认认为上班不用打卡。从迟到时长后开始计算缺勤时长，无迟到时长。
																		boolean zaotui = false;
																		if (attendanceFu
																				.getClosingDateTime() == null) {
																			extime = null;
																		} else {
																			if (attendanceFu
																					.getClosingDateTime()
																					.compareTo(
																							end) < 0) {// 早退情况
																				extime = attendanceFu
																						.getClosingDateTime();
																				zaotui = true;
																			} else
																				extime = end;
																		}
																		if (extime != null) {// 汇总时长
																			attendanceFu
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									start,
																									extime) / 60000));// 工作时长
																			if (zaotui)
																				attendanceFu
																						.setEarlyTime((int) (Util
																								.getWorkTime2(
																										attendanceFu
																												.getClosingDateTime(),
																										end) / 60000));
																			else
																				attendanceFu
																						.setEarlyTime(0);
																			attendanceFu
																					.setQueqinTime(shichang
																							- attendanceFu
																									.getWorkTime()
																							- attendanceFu
																									.getEarlyTime());// 缺勤时长
																			if (shichang
																					.intValue() == attendanceFu
																					.getWorkTime()) {
																				attendanceFu
																						.setAttendanceStatus("正常");
																			}
																		}
																	}
																}
															}
														} else if (ban
																.getStartIsDaka() != null
																&& ban
																		.getStartIsDaka()
																&& ban
																		.getEndIsDaka() != null
																&& !ban
																		.getEndIsDaka()) {// 下班时间不用打卡，查询下一时段汇总。
															if (ban.getDuan() != null
																	&& ban
																			.getDuan() < banCi
																			.getBanCiTime()
																			.size()) {
																// 查询上一班次时段
																BanCiTime banCiTime2 = null;
																List<BanCiTime> ciTimes = totalDao
																		.query(
																				"from BanCiTime where banCi.id = ? and duan = ?",
																				banCi
																						.getId(),
																				ban
																						.getDuan() - 1);
																if (ciTimes != null
																		&& ciTimes
																				.size() > 0) {
																	banCiTime2 = ciTimes
																			.get(0);
																}
																if (banCiTime2 != null) {
																	if (banCiTime2
																			.getStartIsDaka() != null
																			&& !banCiTime2
																					.getStartIsDaka()
																			&& ban
																					.getEndIsDaka() != null
																			&& ban
																					.getEndIsDaka()) {// 时段为开始不需打卡，下班需打卡的。
																		// 查询附表
																		AttendanceFu attendanceFu1 = null;
																		List<AttendanceFu> attendanceFuList1 = totalDao
																				.query(
																						"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
																						ban
																								.getDuan() + 1,
																						attence
																								.getAttendanceId(),
																						users
																								.getId());
																		if (attendanceFuList1 != null
																				&& attendanceFuList1
																						.size() > 0) {
																			attendanceFu1 = attendanceFuList1
																					.get(0);
																		}
																		String start1 = banCiTime2
																				.getStartTime()
																				.substring(
																						0,
																						5);
																		if (attendanceFu1 == null) {
																			attendanceFu1 = new AttendanceFu(
																					attence
																							.getAttendanceId(),
																					users
																							.getId(),
																					users
																							.getName(),
																					users
																							.getDept(),
																					users
																							.getCardId(),
																					date,
																					date
																							+ " "
																							+ time
																							+ ":00",
																					"异常",
																					0,
																					0,
																					0,
																					0,
																					0,
																					ban
																							.getXxTimeNumber(),
																					ban
																							.getXxTimeNumber(),
																					banCi
																							.getId(),
																					banCi
																							.getName(),
																					"6",
																					null,
																					banCiTime2
																							.getDuan() + 1,
																					banCiTime2
																							.getDayType());
																			totalDao
																					.save2(attendanceFu1);
																		}
																		boolean chidao = false;// 迟到
																		boolean zaotui = false;// 早退
																		if (attendanceFu
																				.getWorkDateTime() == null) {
																			sstime = null;
																		} else {
																			if (attendanceFu
																					.getWorkDateTime()
																					.compareTo(
																							start1) > 0) {// 迟到情况
																				sstime = attendanceFu
																						.getWorkDateTime();
																				chidao = true;
																			} else
																				sstime = start1;
																		}
																		if (attendanceFu1
																				.getClosingDateTime() == null) {
																			extime = null;
																		} else {
																			if (attendanceFu1
																					.getClosingDateTime()
																					.compareTo(
																							end) < 0) {// 早退情况
																				extime = attendanceFu1
																						.getClosingDateTime();
																				zaotui = true;
																			} else
																				extime = end;
																		}

																		if (sstime != null
																				&& extime != null) {// 汇总时长
																			attendanceFu1
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									sstime,
																									banCiTime2
																											.getEndTime()
																											.substring(
																													0,
																													5)) / 60000));// 计算上段时间的工作时长
																			if (chidao)
																				attendanceFu1
																						.setLateTime((int) (Util
																								.getWorkTime2(
																										start1,
																										attendanceFu1
																												.getWorkDateTime()) / 60000));
																			else
																				attendanceFu1
																						.setLateTime(0);
																			attendanceFu1
																					.setQueqinTime(attendanceFu1
																							.getWorkBiaoTime()
																							- attendanceFu1
																									.getWorkTime()
																							- attendanceFu1
																									.getLateTime());// 缺勤时长
																			if (attendanceFu1
																					.getWorkBiaoTime() == attendanceFu1
																					.getWorkTime()
																					.intValue()) {
																				attendanceFu1
																						.setAttendanceStatus("正常");
																			}
																			totalDao
																					.update2(attendanceFu1);
																			attendanceFu
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									start,
																									extime) / 60000));// 计算本段时间的工作时长
																			if (zaotui)
																				attendanceFu
																						.setEarlyTime((int) (Util
																								.getWorkTime2(
																										attendanceFu
																												.getClosingDateTime(),
																										end) / 60000));
																			else
																				attendanceFu
																						.setEarlyTime(0);
																			attendanceFu
																					.setQueqinTime(shichang
																							- attendanceFu
																									.getWorkTime()
																							- attendanceFu
																									.getEarlyTime());// 缺勤时长
																			if (shichang
																					.intValue() == attendanceFu
																					.getWorkTime()) {
																				attendanceFu
																						.setAttendanceStatus("正常");
																			}
																		}
																	} else {// 表示跟上一时段无关系。
																		// 默认认为下班不用打卡。只判断有无迟到时长。
																		boolean chidao = false;
																		if (attendanceFu
																				.getWorkDateTime() == null) {
																			sstime = null;
																		} else {
																			if (attendanceFu
																					.getWorkDateTime()
																					.compareTo(
																							start) > 0) {// 迟到情况
																				sstime = attendanceFu
																						.getClosingDateTime();
																				chidao = true;
																			} else
																				sstime = start;
																		}
																		if (sstime != null) {// 汇总时长
																			attendanceFu
																					.setWorkTime((int) (Util
																							.getWorkTime2(
																									sstime,
																									end) / 60000));// 工作时长
																			if (chidao)
																				attendanceFu
																						.setLateTime((int) (Util
																								.getWorkTime2(
																										start,
																										attendanceFu
																												.getWorkDateTime()) / 60000));
																			else
																				attendanceFu
																						.setLateTime(0);
																			attendanceFu
																					.setQueqinTime(shichang
																							- attendanceFu
																									.getWorkTime()
																							- attendanceFu
																									.getLateTime());// 缺勤时长
																			if (shichang
																					.intValue() == attendanceFu
																					.getWorkTime()) {
																				attendanceFu
																						.setAttendanceStatus("正常");
																			}
																		}
																	}
																}
															}
														}
													}
													totalDao
															.update2(attendanceFu);
												}
											}
										}
										// 更新汇总表信息
										updateAttend(attence);
										// 无需添加汇总表
									} catch (Exception e) {
										// TODO Auto-generated catch block
										// 添加汇总表
									}
								}
							}
						}
					}
				}
			}
		}
		return;
	}

	/**
	 * 门卫班次计算算发
	 * 
	 * @param totalDao
	 * @param users
	 * @param time
	 * @param attence
	 * @param b2
	 * @param bol
	 * @param ban1
	 * @return
	 */
	private static boolean menweiJisuan(TotalDao totalDao, Users users,
			String time, Attendance attence, boolean b2, boolean bol,
			BanCiTime ban1) {
		String startBefore;
		String startLater;
		String endBefore;
		String endLater;
		if (ban1 != null && bol) {
			// if ((time.compareTo(startBefore) >= 0 &&
			// startLater.compareTo(time)>=0)||
			// (time.compareTo(endBefore) >= 0 && endLater.compareTo(time)>=0))
			// {// 在此班次时段内
			String start = ban1.getStartTime().substring(0, 5);// 开始时间
			String end = ban1.getEndTime().substring(0, 5);// 结束时间
			startBefore = ban1.getStartBeforeTime().substring(0, 5);// 开始前时间
			startLater = ban1.getStartLaterTime().substring(0, 5);// 开始后时间
			endBefore = ban1.getEndBeforeTime().substring(0, 5);// 结束前时间
			endLater = ban1.getEndLaterTime().substring(0, 5);// 结束后时间
			String nnssTime = "";// 上班是否覆盖
			String nnexTime = "";// 下班是否覆盖
			String sstime = null;// 上班时间
			String extime = null;// 下班时间
			// 查询附表
			AttendanceFu attendanceFu = null;
			List<AttendanceFu> attendanceFuList = totalDao
					.query(
							"from AttendanceFu where duan = ? and attendanceId = ? and userId = ?",
							ban1.getDuan(), attence.getAttendanceId(), users
									.getId());
			if (attendanceFuList != null && attendanceFuList.size() > 0) {
				attendanceFu = attendanceFuList.get(0);
			}
			if (attendanceFu != null) {
				if (b2) {// 汇总昨天记录
					if (endLater.compareTo(time) >= 0
							&& time.compareTo(endBefore) >= 0) {// 下班有效打卡时间段
						if (attendanceFu.getClosingDateTime() == null
								|| "".equals(attendanceFu.getClosingDateTime())) {
							attendanceFu.setClosingDateTime(time);
						} else {
							nnexTime = attendanceFu.getClosingDateTime();
							if (ban1.getXiaFu() != null && ban1.getXiaFu()) {// 下班记录覆盖
								if (nnexTime.compareTo(time) < 0) {// 当前时间大于记录时间时候才覆盖
									attendanceFu.setClosingDateTime(time);
								}
							} else {// 如果下班时间不覆盖。当前时间小于的时候按照小于时间计算
								if (nnexTime.compareTo(time) > 0) {
									attendanceFu.setClosingDateTime(time);
								}
							}
						}
						if (attendanceFu.getTimeAll() == null
								|| "".equals(attendanceFu.getTimeAll()))
							attendanceFu.setTimeAll(time);// 时间
						else
							attendanceFu.setTimeAll(attendanceFu.getTimeAll()
									+ "; " + time);// 时间
						bol = false;
					}
				} else {// 汇总当天记录
					if (time.compareTo(startBefore) >= 0
							&& startLater.compareTo(time) >= 0) {// 上班有效打卡时段
						if (attendanceFu.getWorkDateTime() == null
								|| "".equals(attendanceFu.getWorkDateTime())) {
							attendanceFu.setWorkDateTime(time);
						} else {
							nnssTime = attendanceFu.getWorkDateTime();
							if (nnssTime.compareTo(time) > 0) {
								attendanceFu.setWorkDateTime(time);
							}
						}
						if (attendanceFu.getTimeAll() == null
								|| "".equals(attendanceFu.getTimeAll()))
							attendanceFu.setTimeAll(time);// 时间
						else
							attendanceFu.setTimeAll(attendanceFu.getTimeAll()
									+ "; " + time);// 时间
						bol = false;
					}
				}
				if (!bol) {
					String kuasstime = "";// 跨夜计算带入参数(开始)
					String kuaextime = "";// 跨夜计算带入参数(结束)
					// 找到对应时段，计算对应时长
					if (ban1.getStartIsDaka() != null && ban1.getStartIsDaka()
							&& ban1.getEndIsDaka() != null
							&& ban1.getEndIsDaka()) {
						// 开始和结束时间都需要打卡的
						boolean chidao = false;// 迟到
						boolean zaotui = false;// 早退
						if (attendanceFu.getWorkDateTime() == null) {
							sstime = null;
						} else {
							kuasstime = attendanceFu.getWorkDateTime();
							if (kuasstime.compareTo(start) > 0) {// 迟到情况
								sstime = kuasstime;
								chidao = true;
							} else
								sstime = start;
						}
						if (attendanceFu.getClosingDateTime() == null) {
							extime = null;
						} else {
							kuaextime = attendanceFu.getClosingDateTime();
							if (kuaextime.compareTo(end) < 0) {// 早退情况
								extime = kuaextime;
								zaotui = true;
							} else
								extime = end;
						}
						if (sstime != null && extime != null) {// 汇总时长
							if (chidao)
								attendanceFu
										.setLateTime((int) (Util.getWorkTime2(
												start, kuasstime) / 60000));
							else
								attendanceFu.setLateTime(0);
							if (zaotui)
								attendanceFu.setEarlyTime((int) (Util
										.getWorkTime2(kuaextime, end) / 60000));
							else
								attendanceFu.setEarlyTime(0);
							attendanceFu.setWorkTime(attendanceFu
									.getWorkBiaoTime()
									- attendanceFu.getLateTime()
									- attendanceFu.getEarlyTime());// 工作时长
							attendanceFu.setQueqinTime(0);// 缺勤时长
							// if(attendanceFu.getWorkBiaoTime().intValue() ==
							// attendanceFu.getWorkTime()){
							attendanceFu.setAttendanceStatus("正常");
							// }
						}
					}
				}
				totalDao.update2(attendanceFu);
			}
		}
		return bol;
	}

	/**
	 * 查询排班表 获得班次当天
	 * 
	 * @param totalDao
	 * @param users
	 * @param date
	 * @return
	 */
	private static Integer huoquBanCi(TotalDao totalDao, Users users,
			String date) {
		Integer banciId;
		List<SchedulingTable> tableList = totalDao.query(
				"from SchedulingTable where users.id = ? and dateTime = ?",
				users.getId(), date);
		SchedulingTable table = null;
		if (!tableList.isEmpty()) {
			table = tableList.get(0);
		}
		if (table != null && table.getBanCi() != null) {
			banciId = table.getBanCi().getId();
		} else {
			// 跨夜班次查询前一天的
			List<SchedulingTable> tableLists = totalDao.query(
					"from SchedulingTable where users.id = ? and dateTime = ?",
					users.getId(), Util.getSpecifiedDayAfter(date, -1));
			if (!tableLists.isEmpty()) {
				table = tableLists.get(0);
				if (table.getBanCi() != null) {
					BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class,
							table.getBanCi().getId());
					if (banCi != null) {
						if ("是".equals(banCi.getIsOvernight())) {
							banciId = banCi.getId();
						} else {
							banciId = users.getBanci_id();
						}
					} else {
						banciId = users.getBanci_id();
					}
				} else {
					banciId = users.getBanci_id();
				}
			} else {
				banciId = users.getBanci_id();
			}
		}
		return banciId;
	}

	/**
	 * 添加汇总表和附表
	 * 
	 * @param totalDao
	 * @param users
	 * @param date
	 * @param time
	 * @param banCi
	 * @return
	 */
	private static Attendance addAttendandFu(TotalDao totalDao, Users users,
			String date, String time, BanCi banCi) {
		Attendance attence;
		// 添加
		attence = new Attendance(users.getId(), users.getCode(), users
				.getName(), users.getDept(), users.getCardId(), date, date
				+ " " + time + ":00", "缺勤", 0, 0, 0, 0, 0, banCi.getGzTime(),
				banCi.getGzTime(), banCi.getId(), banCi.getName(), "6", null);
		totalDao.save2(attence);
		for (BanCiTime ban : banCi.getBanCiTime()) {
			AttendanceFu fu = new AttendanceFu(attence.getAttendanceId(), users
					.getId(), users.getName(), users.getDept(), users
					.getCardId(), date, date + " " + time + ":00", "异常", 0, 0,
					0, 0, 0, ban.getXxTimeNumber(), ban.getXxTimeNumber(),
					banCi.getId(), banCi.getName(), "6", null, ban.getDuan(),
					ban.getDayType());
			totalDao.save2(fu);
		}
		return attence;
	}

	/**
	 * 添加汇总信息
	 * 
	 * @param cardId
	 * @param date
	 * @param dept
	 * @param name
	 * @param time
	 * @param userid
	 * @return
	 */
	public static AttendanceTowCollect AtC(String cardId, String code,
			String date, String dept, String name, String time, Integer userid,
			String outIn) {
		AttendanceTowCollect attendanceTowCollect = new AttendanceTowCollect();
		attendanceTowCollect.setCardId(cardId);
		attendanceTowCollect.setCode(code);
		attendanceTowCollect.setDateTime(date);
		attendanceTowCollect.setDept(dept);
		attendanceTowCollect.setName(name);
		attendanceTowCollect.setTime(time + " ");
		attendanceTowCollect.setUserId(userid);
		attendanceTowCollect.setAddTime(Util.getDateTime());
		if ("进门".equals(outIn)) {
			attendanceTowCollect.setInTime(time + " ");
		} else if ("出门".equals(outIn)) {
			attendanceTowCollect.setOutTime(time + " ");
		}
		return attendanceTowCollect;
	}

	/**
	 * 修改汇总表
	 * 
	 * @param list
	 *            汇总
	 * @param time
	 *            打卡时分
	 * @param outIn
	 *            进出类型
	 * @return
	 */
	public static AttendanceTowCollect UpAtC(List list, String time,
			String outIn) {
		AttendanceTowCollect attendanceTowCollect = (AttendanceTowCollect) list
				.get(0);
		String timeNew = attendanceTowCollect.getTime() + time + " ";
		attendanceTowCollect.setTime(timeNew);
		if ("进门".equals(outIn)) {
			if (attendanceTowCollect.getInTime() != null
					&& !"".equals(attendanceTowCollect.getInTime()))
				attendanceTowCollect.setInTime(attendanceTowCollect.getInTime()
						+ time + " ");
			else
				attendanceTowCollect.setInTime(time + " ");
		} else if ("出门".equals(outIn)) {
			if (attendanceTowCollect.getOutTime() != null
					&& !"".equals(attendanceTowCollect.getOutTime()))
				attendanceTowCollect.setOutTime(attendanceTowCollect
						.getOutTime()
						+ time + " ");
			else
				attendanceTowCollect.setOutTime(time + " ");
		}
		return attendanceTowCollect;
	}

	/**
	 * 添加打卡信息
	 * 
	 * @param date
	 * @param time
	 * @param cardId
	 * @param name
	 * @param dept
	 * @param userid
	 * @param outIn
	 * @param type
	 * @param address
	 * @return
	 */
	public static AttendanceTow attendance(String date, String code,
			String time, String cardId, String name, String dept,
			Integer userid, String outIn, String type, String address) {
		AttendanceTow attendanceTow = new AttendanceTow();
		attendanceTow.setDateTime(date);
		attendanceTow.setTime(time);
		attendanceTow.setCode(code);
		attendanceTow.setCardId(cardId);
		attendanceTow.setName(name);
		attendanceTow.setDept(dept);
		attendanceTow.setUserId(userid);
		attendanceTow.setAddTime(date + " " + time + ":"
				+ Util.getDateTime("ss"));
		attendanceTow.setOutInDoor(outIn);
		attendanceTow.setDownType(type);
		attendanceTow.setDownAddress(address);
		return attendanceTow;
	}

	@Override
	public Map<Integer, Object> findAttendanceTow(AttendanceTow attendanceTow,
			int pageNo, int pageSize, String tag) {
		// TODO Auto-generated method stub
		if (attendanceTow == null) {
			attendanceTow = new AttendanceTow();
		}
		String sql = "";
		if ("cardId".equals(tag)) {
			Users users = Util.getLoginUser();
			if (users != null) {
				sql = "cardId = '" + users.getCardId() + "'";
			} else {
				return null;
			}
		}
		String hql = totalDao.criteriaQueries(attendanceTow, sql);
		hql += " order by addTime desc";
		List listInt = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, listInt);
		map.put(2, count);
		return map;
	}

	@Override
	public Map<Integer, Object> findAttendanceTowCollect(
			AttendanceTowCollect attendanceTowCollect, int pageNo,
			int pageSize, String tag) {
		// TODO Auto-generated method stub
		if (attendanceTowCollect == null) {
			attendanceTowCollect = new AttendanceTowCollect();
		}
		String sql = "";
		if ("cardId".equals(tag)) {
			Users users = Util.getLoginUser();
			sql = "cardId = '" + users.getCardId() + "'";
		}
		String hql = totalDao.criteriaQueries(attendanceTowCollect, sql);
		hql += " order by id desc";
		List listInt = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, listInt);
		map.put(2, count);
		return map;
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public List findDownload(String date, String cardId) {
		// TODO Auto-generated method stub
		if (date == null || "".equals(date))
			date = Util.getDateTime("yyyy-MM-dd");
		// return
		// totalDao.query("from Download where brush_Date = ? and card_No = ? order by brush_Time asc",date,
		// Integer.parseInt(cardId)+"");
		return totalDao
				.query(
						"from AttendanceTow where dateTime= ? and cardId = ? order by time asc",
						date, cardId);
	}

	@Override
	public Map<Integer, Object> findDownloads(Download Download, int pageNo,
			int pageSize, String tag) {
		// TODO Auto-generated method stub
		if (Download == null) {
			Download = new Download();
		}
		String sql = "";
		if ("cardId".equals(tag)) {
			Users users = Util.getLoginUser();
			sql = "card_No = '" + Integer.parseInt(users.getCardId()) + ""
					+ "'";
		}
		String hql = totalDao.criteriaQueries(Download, sql);
		hql += " order by id desc";
		List listInt = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, listInt);
		map.put(2, count);
		return map;
	}

	/**
	 * 充值表 充值当天
	 */
	public static void addRechargeZhi(Users users) {
		String nowDate = Util.getDateTime("yyyy-MM-dd");
		TotalDao totalDao = createTotol();
		RechargeZhi rechargeZhi = (RechargeZhi) totalDao
				.getObjectByCondition(
						"from RechargeZhi where userId = ? and dateTime = ? and useWhat = '午餐费'",
						users.getId(), nowDate);
		if (rechargeZhi == null) {
			String nowDateTime = Util.getDateTime();
			rechargeZhi = new RechargeZhi();
			rechargeZhi.setAddTime(nowDateTime);
			rechargeZhi.setName(users.getName());
			rechargeZhi.setDept(users.getDept());
			rechargeZhi.setCode(users.getCode());
			rechargeZhi.setCardId(users.getCardId());
			rechargeZhi.setUseWhat("午餐费");
			rechargeZhi.setUserId(users.getId());
			rechargeZhi.setBalance(1);
			rechargeZhi.setDateTime(nowDate);
			RechargeAll all = (RechargeAll) totalDao.getObjectByCondition(
					"from RechargeAll where userId = ?", users.getId());
			if (all != null) {
				all.setUpdateTime(nowDateTime);
				all.setBalance(all.getBalance() + rechargeZhi.getBalance());
				if (totalDao.update(all))
					rechargeZhi.setAllId(all.getId());
				totalDao.save(rechargeZhi);
			} else {
				all = new RechargeAll();
				all.setAddTime(nowDateTime);
				all.setBalance(rechargeZhi.getBalance() + 0f);
				all.setName(users.getName());
				all.setDept(users.getDept());
				all.setCode(users.getCode());
				all.setUserId(users.getId());
				all.setCardId(users.getCardId());
				if (totalDao.save(all))
					rechargeZhi.setAllId(all.getId());
				totalDao.save(rechargeZhi);
			}
		}
	}

	/**
	 * 充值表 指定时间
	 */
	public static void addRechargeZhi(Users users, String nowDate) {
		if (nowDate == null || "".equals(nowDate))
			nowDate = Util.getDateTime("yyyy-MM-dd");
		TotalDao totalDao = createTotol();
		RechargeZhi rechargeZhi = (RechargeZhi) totalDao
				.getObjectByCondition(
						"from RechargeZhi where userId = ? and dateTime = ? and useWhat = '午餐费'",
						users.getId(), nowDate);
		if (rechargeZhi == null) {
			String nowDateTime = Util.getDateTime();
			rechargeZhi = new RechargeZhi();
			rechargeZhi.setAddTime(nowDateTime);
			rechargeZhi.setName(users.getName());
			rechargeZhi.setDept(users.getDept());
			rechargeZhi.setCode(users.getCode());
			rechargeZhi.setCardId(users.getCardId());
			rechargeZhi.setUseWhat("午餐费");
			rechargeZhi.setUserId(users.getId());
			rechargeZhi.setBalance(1);
			rechargeZhi.setDateTime(nowDate);
			RechargeAll all = (RechargeAll) totalDao.getObjectByCondition(
					"from RechargeAll where userId = ?", users.getId());
			if (all != null) {
				all.setUpdateTime(nowDateTime);
				// all.setBalance(all.getBalance()+rechargeZhi.getBalance());
				if (totalDao.update(all))
					rechargeZhi.setAllId(all.getId());
				totalDao.save(rechargeZhi);
			} else {
				all = new RechargeAll();
				all.setAddTime(nowDateTime);
				all.setBalance(rechargeZhi.getBalance() + 0f);
				all.setName(users.getName());
				all.setDept(users.getDept());
				all.setCode(users.getCode());
				all.setUserId(users.getId());
				all.setCardId(users.getCardId());
				if (totalDao.save(all))
					rechargeZhi.setAllId(all.getId());
				totalDao.save(rechargeZhi);
			}
		}
	}

	/**
	 * 传入时间充值
	 * 
	 * @param nowDate
	 */
	public static void addRechargeAttend(String nowDate) {
		if (nowDate == null || "".equals(nowDate)
				|| nowDate.replaceAll(" ", "").length() < 10)
			nowDate = Util.getDateTime("yyyy-MM-dd");
		TotalDao totalDao = createTotol();
		// 查询班次
		List<BanCi> banCis = totalDao.query("from BanCi");
		if (banCis != null && banCis.size() > 0) {
			for (BanCi banCi : banCis) {
				// 判断是否为上班时间
				boolean b = false;// 默认不计算
				String sbTime = banCi.getFirsttime().substring(0, 5);// 上班时间
				// 实际工作时长
				if (banCi != null && banCi.getFirsttime() != null
						&& banCi.getFinishtime() != null
						&& banCi.getWxstarttime() != null
						&& banCi.getWxendtime() != null) {
					// 查询特殊时间表
					List<SpecialDate> listSpecil = totalDao.query(
							"from SpecialDate where banciId = ? and date = ?",
							banCi.getId(), nowDate);
					if (listSpecil != null && listSpecil.size() > 0) {
						if ("上班".equals(listSpecil.get(0).getSpecialType()))
							b = true;
					} else
						b = isbanci(banCi, nowDate);
					if (b) {// 只汇总需要汇总的班次
						String s = "select id,name,dept,cardId from users where banci_id = "
								+ banCi.getId()
								+ " and onWork not in ('离职','退休','病休','内退') and dept not in('內退','病休','供应商','物业','上海庆霆不锈钢制品有限公司','昆山惠明','上海彤庆') and internal = '是' and len(cardId) = 10 and id in (select userId from ta_kq_AttendanceTowCollect where left(inTime, 6) <= '"
								+ sbTime
								+ "' and dateTime = '"
								+ nowDate
								+ "' and userId is not NULL)";
						// List<Users> userslist =
						// totalDao.query("from users where banci_id = ? and onWork not in ('离职','退休','病休','内退') and dept not in('內退','病休','供应商','物业','上海庆霆不锈钢制品有限公司','昆山惠明','上海彤庆') and internal = '是' and len(cardId) = 10 and id in (select userId from AttendanceTowCollect where left(inTime, 6) = ? and dateTime = ? and userId is not NULL)",banCi.getId(),sbTime,nowDate);
						List<Map> userslist = totalDao.findBySql(s);
						if (userslist != null && userslist.size() > 0) {
							for (Map map : userslist) {
								Users users = new Users();
								users.setId((Integer) map.get("id"));
								users.setName((String) map.get("name"));
								users.setDept((String) map.get("dept"));
								users.setCardId((String) map.get("cardId"));
								addRechargeZhi(users, nowDate);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 传入时间和工号充值
	 * 
	 * @param nowDate
	 *            日期
	 * @param code
	 *            工号
	 */
	public static void addRechargeAttend(String nowDate, String code) {
		if (nowDate == null || "".equals(nowDate)
				|| nowDate.replaceAll(" ", "").length() < 10)
			nowDate = Util.getDateTime("yyyy-MM-dd");
		TotalDao totalDao = createTotol();
		Users users = (Users) totalDao.getObjectByCondition(
				"from Users where code = ?", code);
		if (users != null)
			addRechargeZhi(users, nowDate);
	}

	/**
	 * 批量同步充值汇总表数据
	 */
	public static void rechAll() {
		TotalDao totalDao = createTotol();
		List<RechargeAll> alls = totalDao.query("from RechargeAll");
		for (RechargeAll rechargeAll : alls) {
			Users users = (Users) totalDao.getObjectById(Users.class,
					rechargeAll.getUserId());
			if (users == null)
				continue;
			rechargeAll.setCode(users.getCode());
			rechargeAll.setCardId(users.getCardId());
			totalDao.update2(rechargeAll);
		}
	}

	/**
	 * 查询当前用户今天是否上班
	 * 
	 * @param users
	 * @return
	 */
	public static boolean isbanci(BanCi banCi, String nowDate) {
		boolean bool1 = false;
		String week = Util.getFullDateWeekTime(nowDate);// 当前星期几中文
		String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
		for (int l = 0; l < sbdate.length; l++) {
			if (week.equals(sbdate[l].trim())) {
				bool1 = true;
			}
		}
		return bool1;
	}

	/**
	 * 休眠n毫秒
	 * 
	 * @param i
	 */
	public static void sleeps(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新工装屏幕信息
	 * 
	 * @param wN
	 */
	public static void sendPin(WarehouseNumber wN) {
		TotalDao totalDao = createTotol();
		sleeps(3000);
		// 发送 库位信息
		List<WareBangGoogs> listw = totalDao.query(
				"from WareBangGoogs where fk_ware_id = ?", wN.getId());// and
		// status
		// =
		// '检验中'
		if (listw != null && listw.size() > 0) {// 更新此库位屏幕信息
			int ir = 1;
			StringBuffer buffer = new StringBuffer();
			StringBuffer buffer2 = new StringBuffer("");
			for (WareBangGoogs wbg : listw) {
				if (wbg.getFk_oadetail_id() == null) {
					if (wbg.getFk_store_id() == null)
						continue;
					Store store = (Store) totalDao.getObjectById(Store.class,
							wbg.getFk_store_id());
					if (store == null)
						continue;
					if (ir > 2) {
						buffer2.append("工装号:" + store.getMatetag() + " ");// 件号
						buffer2.append("规格:" + store.getFormat() + " ");// 批次
						buffer2.append("数量:" + wbg.getNumber() + " ");// 数量
						buffer2.append("库位编号:" + wbg.getNumber() + " ");// 库位编号
						buffer2.append("库位状态:" + wN.getMarkTyptName() + " ");// 库位状态
						// buffer2.append(Util.neirong("",30));//供应商
					} else {
						buffer
								.append(Util.neirong(store.getMatetag() + "",
										30));// 件号
						buffer.append(Util.neirong(store.getFormat() + "", 16));// 批次
						buffer.append(Util.neirong(wbg.getNumber() + "", 8));// 数量
						buffer.append(Util.neirong(wN.getNumber() + "", 16));// 库位编号
						buffer.append(Util
								.neirong(wN.getMarkTyptName() + "", 8));// 库位状态
						buffer.append(Util.neirong("无", 30));// 供应商
					}
				} else {
					OaAppDetail deli = (OaAppDetail) totalDao.getObjectById(
							OaAppDetail.class, wbg.getFk_oadetail_id());
					if (deli == null)
						continue;
					if (ir > 2) {
						buffer2.append("件号:" + deli.getDetailAppName() + " ");// 件号
						buffer2.append("批次:" + deli.getDetailFormat() + " ");// 批次
						buffer2.append("数量:" + wbg.getNumber() + " ");// 数量
						buffer2.append("库位编号:" + wN.getNumber() + " ");// 库位编号
						buffer2.append("库位状态:" + wN.getMarkTyptName() + " ");// 库位状态
						buffer2.append("供应商:" + deli.getDetailAppDept() + " ");// 供应商
					} else {
						buffer.append(Util.neirong(
								deli.getDetailAppName() + "", 30));// 件号
						buffer.append(Util.neirong(deli.getDetailFormat() + "",
								16));// 批次
						buffer.append(Util.neirong(wbg.getNumber() + "", 8));// 数量
						buffer.append(Util.neirong(wN.getNumber() + "", 16));// 库位编号
						buffer.append(Util
								.neirong(wN.getMarkTyptName() + "", 8));// 库位状态
						buffer.append(Util.neirong(
								deli.getDetailAppDept() + "", 30));// 供应商
					}
				}
				ir++;
			}
			// 发送详细屏幕信息
			UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN.getOneNumber(),
					wN.getNumid(), buffer.toString(), 1);
			if (ir > 3) {
				sleeps(1000);
				UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN
						.getOneNumber(), wN.getNumid(), buffer2.toString(), 3);
			} else if (ir == 3) {
				sleeps(1000);
				UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN
						.getOneNumber(), wN.getNumid(), buffer2.toString(), 2);
			}

		}
	}

	/**
	 * 更新外购件屏幕信息
	 * 
	 * @param wN
	 */
	public static void sendPin_1(WarehouseNumber wN) {
		TotalDao totalDao = createTotol();
		// 发送 库位信息
		List<WareBangGoogs> listw = totalDao.query(
				"from WareBangGoogs where fk_ware_id = ?", wN.getId());// and
		// status
		// =
		// '检验中'
		if (listw != null && listw.size() > 0) {// 更新此库位屏幕信息
			int ir = 1;
			StringBuffer buffer = new StringBuffer();// 第一页
			StringBuffer buffer1 = new StringBuffer("");// 第二页
			StringBuffer buffer2 = new StringBuffer("");// 第三页
			for (WareBangGoogs wbg : listw) {
				if (wbg.getFk_oadetail_id() == null) {
					if (wbg.getFk_store_id() == null) {// 工装
						if (wbg.getFk_waigouDeliveryDetail_id() != null) {// 外购件
							WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
									.getObjectById(WaigouDeliveryDetail.class,
											wbg.getFk_waigouDeliveryDetail_id());
							if (detail == null)
								continue;
							if (ir == 1) {
								buffer.append(Util.neirong(detail.getMarkId()
										+ "", 30));// 件号
								buffer.append(Util.neirong(detail
										.getExamineLot()
										+ "", 16));// 批次
								buffer.append(Util.neirong(
										wbg.getNumber() + "", 8));// 数量
								buffer.append(Util.neirong(wN.getNumber() + "",
										16));// 库位编号
								buffer.append(Util.neirong(wN.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer.append(Util.neirong(detail.getGysName()
										+ "", 30));// 供应商
							} else if (ir == 2) {
								buffer1.append(Util.neirong(detail.getMarkId()
										+ "", 30));// 件号
								buffer1.append(Util.neirong(detail
										.getExamineLot()
										+ "", 16));// 批次
								buffer1.append(Util.neirong(wbg.getNumber()
										+ "", 8));// 数量
								buffer1.append(Util.neirong(
										wN.getNumber() + "", 16));// 库位编号
								buffer1.append(Util.neirong(wN
										.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer1.append(Util.neirong(detail.getGysName()
										+ "", 30));// 供应商
							} else {
								buffer2.append(Util.neirong("件号:"
										+ detail.getMarkId() + "", 30));// 件号
								buffer2.append(Util.neirong("批次:"
										+ detail.getExamineLot() + "", 16));// 批次
								buffer2.append(Util.neirong("数量:"
										+ wbg.getNumber() + "", 8));// 数量
								buffer2.append(Util.neirong("库位编号:"
										+ wN.getNumber() + "", 16));// 库位编号
								buffer2.append(Util.neirong("库位状态:"
										+ wN.getMarkTyptName() + "", 8));// 库位状态
								buffer2.append(Util.neirong("供应商:"
										+ detail.getGysName() + "", 30));// 供应商
							}
						} else {
							if (wbg.getFk_good_id() == null)
								continue;
							Goods good = (Goods) totalDao.getObjectById(
									Goods.class, wbg.getFk_good_id());
							if (good == null) {
								buffer.append(Util.neirong("", 108));// 件号
							} else {
								if (ir == 1) {
									buffer.append(Util.neirong(good
											.getGoodsMarkId()
											+ "", 30));// 件号
									buffer.append(Util.neirong(good
											.getGoodsLotId()
											+ "", 16));// 批次
									buffer.append(Util.neirong(wbg.getNumber()
											+ "", 8));// 数量
									buffer.append(Util.neirong(wN.getNumber()
											+ "", 16));// 库位编号
									buffer.append(Util.neirong(wN
											.getMarkTyptName()
											+ "", 8));// 库位状态
									buffer.append(Util.neirong(good
											.getGoodsSupplier()
											+ "", 30));// 供应商
								} else if (ir == 2) {
									buffer1.append(Util.neirong(good
											.getGoodsMarkId()
											+ "", 30));// 件号
									buffer1.append(Util.neirong(good
											.getGoodsLotId()
											+ "", 16));// 批次
									buffer1.append(Util.neirong(wbg.getNumber()
											+ "", 8));// 数量
									buffer1.append(Util.neirong(wN.getNumber()
											+ "", 16));// 库位编号
									buffer1.append(Util.neirong(wN
											.getMarkTyptName()
											+ "", 8));// 库位状态
									buffer1.append(Util.neirong(good
											.getGoodsSupplier()
											+ "", 30));// 供应商
								} else {
									buffer2.append(Util.neirong("件号:"
											+ good.getGoodsMarkId() + "", 18));// 件号
									buffer2.append(Util.neirong("批次:"
											+ good.getGoodsLotId() + "", 16));// 批次
									buffer2.append(Util.neirong("数量:"
											+ wbg.getNumber() + "", 10));// 数量
									buffer2.append(Util.neirong("库位编号:"
											+ wN.getNumber() + "", 16));// 库位编号
									buffer2.append(Util.neirong("库位状态:"
											+ wN.getMarkTyptName() + "", 16));// 库位状态
									buffer2
											.append(Util.neirong("供应商:"
													+ good.getGoodsSupplier()
													+ "", 12));// 供应商
								}
							}
						}
					} else {
						Store store = (Store) totalDao.getObjectById(
								Store.class, wbg.getFk_store_id());
						if (store == null)
							continue;
						if (ir == 1) {
							buffer.append(Util.neirong(store.getMatetag() + "",
									30));// 件号
							buffer.append(Util.neirong(store.getFormat() + "",
									16));// 批次
							buffer
									.append(Util.neirong(wbg.getNumber() + "",
											8));// 数量
							buffer
									.append(Util.neirong(wN.getNumber() + "",
											16));// 库位编号
							buffer.append(Util.neirong(wN.getMarkTyptName()
									+ "", 8));// 库位状态
							buffer.append(Util.neirong("无", 30));// 供应商
						} else if (ir == 2) {
							buffer1.append(Util.neirong(
									store.getMatetag() + "", 30));// 件号
							buffer1.append(Util.neirong(store.getFormat() + "",
									16));// 批次
							buffer1.append(Util
									.neirong(wbg.getNumber() + "", 8));// 数量
							buffer1.append(Util
									.neirong(wN.getNumber() + "", 16));// 库位编号
							buffer1.append(Util.neirong(wN.getMarkTyptName()
									+ "", 8));// 库位状态
							buffer1.append(Util.neirong("无", 30));// 供应商
						} else {
							buffer2.append("工装号:" + store.getMatetag() + " ");// 件号
							buffer2.append("规格:" + store.getFormat() + " ");// 批次
							buffer2.append("数量:" + wbg.getNumber() + " ");// 数量
							buffer2.append("库位编号:" + wbg.getNumber() + " ");// 库位编号
							buffer2
									.append("库位状态:" + wN.getMarkTyptName()
											+ " ");// 库位状态
							// buffer2.append(Util.neirong("无",30));//供应商
						}
					}
				} else {
					OaAppDetail deli = (OaAppDetail) totalDao.getObjectById(
							OaAppDetail.class, wbg.getFk_oadetail_id());
					if (deli == null)
						continue;
					if (ir == 1) {
						buffer.append(Util.neirong(
								deli.getDetailAppName() + "", 30));// 件号
						buffer.append(Util.neirong(deli.getDetailFormat() + "",
								16));// 批次
						buffer.append(Util.neirong(wbg.getNumber() + "", 8));// 数量
						buffer.append(Util.neirong(wN.getNumber() + "", 16));// 库位编号
						buffer.append(Util
								.neirong(wN.getMarkTyptName() + "", 8));// 库位状态
						buffer.append(Util.neirong(
								deli.getDetailAppDept() + "", 30));// 供应商
					} else if (ir == 2) {
						buffer1.append(Util.neirong(deli.getDetailAppName()
								+ "", 30));// 件号
						buffer1.append(Util.neirong(
								deli.getDetailFormat() + "", 16));// 批次
						buffer1.append(Util.neirong(wbg.getNumber() + "", 8));// 数量
						buffer1.append(Util.neirong(wN.getNumber() + "", 16));// 库位编号
						buffer1.append(Util.neirong(wN.getMarkTyptName() + "",
								8));// 库位状态
						buffer1.append(Util.neirong(deli.getDetailAppDept()
								+ "", 30));// 供应商
					} else {
						buffer2.append("工装号:" + deli.getDetailAppName() + " ");// 件号
						buffer2.append("规格:" + deli.getDetailFormat() + " ");// 批次
						buffer2.append("数量:" + wbg.getNumber() + " ");// 数量
						buffer2.append("库位编号:" + wN.getNumber() + " ");// 库位编号
						buffer2.append("库位状态:" + wN.getMarkTyptName() + " ");// 库位状态
						buffer2.append("供应商:" + deli.getDetailAppDept() + " ");// 供应商
					}
				}
				ir++;
			}
			// 发送详细屏幕信息
			sleeps(2000);
			UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN.getOneNumber(),
					wN.getNumid(), buffer.toString(), 1);
			if (ir > 2) {
				sleeps(4000);
				UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN
						.getOneNumber(), wN.getNumid(), buffer1.toString(), 2);
				sleeps(1000);
			}
			if (ir > 3) {
				sleeps(4000);
				UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN
						.getOneNumber(), wN.getNumid(), buffer2.toString(), 3);
				sleeps(1000);
			}
		} else {
			UtilTong.querenpingKuWei1(wN.getIp(), duankou, wN.getOneNumber(),
					wN.getNumid(), Util.neirong("", 108), 1);
		}
		sleeps(1000);
	}

	public static Integer duankou = 8885;

	/**
	 * 
	 * @param object
	 * @param id
	 * @param mess
	 */
	public static void add_UsersUpdate(Object object, Integer id, String title,
			String mess, String... strings) {
		UsersUpdateLogging u = new UsersUpdateLogging();
		u.setAddTime(Util.getDateTime());
		u.setLogTitle(title);
		u.setObjectName(object.getClass().getSimpleName());
		u.setObjectId(id);
		if (strings != null && strings.length > 0) {
			u.setStatus(strings[0]);
		} else {
			u.setStatus("修改");
		}
		u.setMore(mess);
		Users us = Util.getLoginUser();
		if (us != null) {
			u.setIp(us.getLoginIp());
			u.setUserId(us.getId());
			u.setUserCode(us.getCode());
			u.setUserDept(us.getDept());
			u.setUserName(us.getName());
		}
		TotalDao totalDao = createTotol();
		totalDao.save2(u);
	}

	/**
	 * add修改记录表
	 * 
	 * @param i
	 *            1: 库存 2：入库历史 3：Bom模板 4：价格合同 5: 工序模板 6：上传图纸 7：删除图纸 8：下载图纸
	 * @param object
	 * @param id
	 * @param mess
	 *            修改详情
	 */
	public static void updateJilu(int i, Object object, Integer id, String mess) {
		switch (i) {
		case 1:// 库存修改记录
			add_UsersUpdate(object, id, "库存", "修改 " + mess + "件号库存信息");
			break;
		case 2:// 入库历史修改记录
			add_UsersUpdate(object, id, "入库历史", "修改 " + mess + "件号库存信息");
			break;
		case 3:// Bom修改记录
			add_UsersUpdate(object, id, "Bom", "修改 " + mess + "件号模板信息");
			break;
		case 4:// price修改记录
			add_UsersUpdate(object, id, "价格", "修改 " + mess + "合同价格信息");
			break;
		case 5:// 工序模板修改记录
			add_UsersUpdate(object, id, "工序", "修改 " + mess + "工序信息");
			break;
		case 6:// 上传图纸
			add_UsersUpdate(object, id, "图纸", "添加 " + mess + "工序图纸信息", "添加");
			break;
		case 7:// 删除图纸
			add_UsersUpdate(object, id, "图纸", "删除 " + mess + "工序图纸信息", "删除");
			break;
		case 8:// 下载图纸
			add_UsersUpdate(object, id, "图纸", "下载 " + mess + "工序图纸信息", "下载");
			break;
		default:
			break;
		}
	}

	/**
	 * 获得各种单号
	 * 
	 * @param ziduan
	 *            编号字段名
	 * @param tableName
	 *            表名
	 * @param id
	 *            表主键名
	 * @param qian
	 *            编号前缀
	 * @return
	 */
	public static String huoquNumber(String ziduan, String tableName,
			String id, String qian) {
		String datetime = Util.getDateTime("yyyyMMdd");
		String hql = "select " + ziduan + " from " + tableName + " order by "
				+ id + " desc";
		TotalDao totalDao = createTotol();
		List list = null;
		try {
			list = totalDao.findAllByPage(hql, 0, 1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException("传值有误");
		}
		String contractNumber = "";
		String sc = qian;
		if (list != null && list.size() > 0) {
			contractNumber = (String) list.get(0);
		}
		if (contractNumber != null && contractNumber.length() > 0) {
			try {
				int num = Integer.parseInt(contractNumber.substring(10,
						contractNumber.length())) + 1;
				if (num >= 1000)
					contractNumber = sc + datetime + num;
				else if ((num >= 100))
					contractNumber = sc + datetime + "0" + num;
				else if ((num >= 10))
					contractNumber = sc + datetime + "00" + num;
				else
					contractNumber = sc + datetime + "000" + num;
			} catch (Exception e) {
				contractNumber = sc + datetime + "0001";
			}
		} else {
			contractNumber = sc + datetime + "0001";
		}
		return contractNumber;
	}

}
