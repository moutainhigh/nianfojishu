package com.task.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.task.Dao.TotalDao;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.menjin.DoorBangDingServerImpl;
import com.task.entity.AskForLeave;
import com.task.entity.Overtime;
import com.task.entity.Users;
import com.task.entity.banci.BanCi;
import com.task.entity.dmltry.Zhongjian;
import com.task.entity.menjin.AccessEquipment;
import com.task.entity.menjin.AccessTime;
import com.task.entity.menjin.DoorBangDing;
import com.task.entity.menjin.FingerprintMg;
import com.task.entity.menjin.SpecialDate;
import com.task.entity.menjin.visitor.Visitor;
import com.task.util.serialPort.ByteUtils;

//@ServerEndpoint("/cmd/prog")
public class SocketServersZWKQ extends Thread {

	public static final int PORT = 8881;
	public static int clientcount = 0;
	public TotalDao toalDao;

	public SocketServersZWKQ(TotalDao toalDao) {
		this.toalDao = toalDao;
	} 

	// public static void startServer() {
	public void run() {
		try {
			// int clientcount = 0; // 统计客户端总数
			boolean listening = true; // 是否对客户端进行监听
			ServerSocket server = null; // 服务器端Socket对象

			try {
				// 创建一个ServerSocket在端口8868监听客户请求
				server = new ServerSocket(PORT);
				System.out.println("ZWKQ ServerSocket starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}
			while (listening) {
				// 客户端计数
				clientcount++;
				// 监听到客户请求,根据得到的Socket对象和客户计数创建服务线程,并启动之
				new ServerThreadZWKQ(server.accept(), clientcount, toalDao).start();
			}
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}
	}

	public TotalDao getToalDao() {
		return toalDao;
	}

	public void setToalDao(TotalDao toalDao) {
		this.toalDao = toalDao;
	}
}

@SuppressWarnings("unchecked")
class ServerThreadZWKQ extends Thread {
	private static int number = 0; // 保存本进程的客户计数
	Socket socket = null; // 保存与本线程相关的Socket对象
	TotalDao totalDao;

	public ServerThreadZWKQ(Socket socket, int clientnum, TotalDao toalDao) {
		this.socket = socket;
		number = clientnum;
		this.totalDao = toalDao;
		System.out.println("当前在线的考勤机数: " + number);

	}

	public void run() {

		BufferedInputStream bis = null;
		BufferedInputStream bis1 = null;
		BufferedInputStream bis2 = null;
		InputStream in = null;
		StringBuffer builder = new StringBuffer();
		String accessIP = "";
		String cardIds = "";// 卡号
		String yanzheng = "";// 来访人员验证码
		String username = "";// 员工姓名
		String accessname = "";// 设备名称
		String inOutType = "";// 进出类型
		String istrueKao = "";// 是否允许考勤
		Integer accessId = 0;// 设备id
		Integer iii = 6;
		String doorPort = "192.168.0.197";
		Integer doorIp = 8877;
		AccessEquipment accessEquipment = null;

		try {
			// getsocketbyte_duibi(socket);
			accessIP = socket.getInetAddress().getHostAddress();
			System.out.println(accessIP + " 进入服务端了");
			// 由Socket对象得到输入流,并构造相应的BufferedReader对象
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);

			// 先接收接收第一个字符 用做标识
			System.out.println("服务端开始接受指纹标识！");
			String accessType = "";// 门禁类型
			String equipmentOutIn = "";// 门禁用途（）
			List acElist = totalDao.query("from AccessEquipment where equipmentIP=? order by id desc", accessIP);
			if (acElist != null && acElist.size() > 0) {
				accessEquipment = (AccessEquipment) acElist.get(0);
				accessType = accessEquipment.getEquipmentDaoType();
				accessname = accessEquipment.getEquipmentName();
				accessId = accessEquipment.getId();
				istrueKao = accessEquipment.getIsTrueKao();
				equipmentOutIn = accessEquipment.getEquipmentOutIn();
			}

			List<Zhongjian> zhongjians = totalDao.query("FROM Zhongjian WHERE status=?", "待删除");
			if (zhongjians != null) {
				for (Zhongjian zhongjian : zhongjians) {
					// 发送删除命令删除指定用户
					getsocketbyte_shanchu(socket, zhongjian.getCanshu(), deleteORhuo(zhongjian.getCanshu()));

					bis = new BufferedInputStream(in);
					byte[] identifi = new byte[9];
					bis.read(identifi);

					
					String one=Util.byteArrayToHexStringK(identifi).replaceAll(" ", "");
					 String jieguo= one.substring(one.length()-2, one.length());
					if (jieguo.equals("00")) {
						zhongjian.setStatus("已删除");
						totalDao.update2(zhongjian);
					}
				}
			}
			List<FingerprintMg> list = totalDao.query(
					"from FingerprintMg where id in (SELECT fingerprintMgid from Zhongjian where status='未下发'  and accessEquipmentid =?) and type='0'",
					5);
			if (list != null) {
				for (FingerprintMg fingerprintMg : list) {

					// 特征值
					String tzhen = fingerprintMg.getIdentification();
					if (tzhen != null) {

						// 获取到编号
						List<Zhongjian> liszho = totalDao.query("from Zhongjian WHERE fingerprintMgid = ?",
								fingerprintMg.getId());
						Zhongjian zho = liszho.get(0);
						in = socket.getInputStream();

						getsocketbyte_xiafa(socket, zho.getCanshu(), tzhen);

						
						
						bis = new BufferedInputStream(in);
						byte[] identi = new byte[10];
						bis.read(identi);
						

						
						String one=Util.byteArrayToHexStringK(identi).replaceAll(" ", "");
						 String jieguo= one.substring(one.length()-2, one.length());
						if (jieguo.equals("00")) {
							zho.setStatus("已下发");
							System.out.println("下发成功");
							totalDao.update(zho);
						}
						

						if (jieguo.equals("06")) {
							System.out.println("用户重复录入");
						}

					} else {
						System.out.println("当前特征值为空......");
					}

				}
			}

			// 查询访客待下发列表
			List<Visitor> visitorList = totalDao
					.query("from Visitor where visitorStatus is not null and visitorStatus in ('待打印','待进门','待出门') ");
			if (visitorList != null && visitorList.size() > 0) {
				DecimalFormat df = new DecimalFormat("0000");
				for (Visitor visitor : visitorList) {
					if (visitor.getMjFingerId() == null || visitor.getMjFingerId().equals("")) { // 说明需要下发的
						for (int i = 1; i < 3000; i++) {
							String format = df.format(i);
							Integer zhongjianCount = totalDao
									.getCount("from Zhongjian where canshu is not null and canshu = ?", format);
							Integer doorBangDingCount = totalDao.getCount(
									"from DoorBangDing where fk_security_id is not null and fk_security_id =?", i);
							Integer visitorCount = totalDao
									.getCount("from Visitor where mjFingerId is not null and MjFingerId = ?", format);
							if ((zhongjianCount == null || zhongjianCount <= 0)
									&& (doorBangDingCount == null || doorBangDingCount <= 0)
									&& (visitorCount == null || visitorCount <= 0)) {
								if (visitor.getFingerprint() != null && visitor.getFingerprint().length() > 0) {

									// 下发命令
									in = socket.getInputStream();

									getsocketbyte_xiafa(socket, format, visitor.getFingerprint());

									bis = new BufferedInputStream(in);
									byte[] identi = new byte[10];
									bis.read(identi);
									String one=Util.byteArrayToHexStringK(identi).replaceAll(" ", "");
									 String jieguo= one.substring(one.length()-2, one.length());
									if (jieguo.equals("00")) {
										visitor.setMjFingerId(format);
										visitor.setMjFingerStatus("已下发");
										totalDao.update(visitor);
										System.out.println("访客指纹下发成功");
									}
								}
								break;
							}
						}
					}
				}
			}
			// 删除访客的指纹记录
			List<Visitor> visitorDelList = totalDao
					.query("from Visitor where mjFingerStatus is not null and mjFingerStatus = '待删除'");
			if (visitorDelList != null && visitorDelList.size() > 0) {
				for (Visitor visitor : visitorDelList) {
					in = socket.getInputStream();
					getsocketbyte_shanchu(socket, visitor.getMjFingerId(), deleteORhuo(visitor.getMjFingerId()));
					bis = new BufferedInputStream(in);
					byte[] identifi = new byte[9];
					bis.read(identifi);

					
					String one=Util.byteArrayToHexStringK(identifi).replaceAll(" ", "");
					 String jieguo= one.substring(one.length()-2, one.length());
					if (jieguo.equals("00")) {
						visitor.setMjFingerStatus("已删除");
						totalDao.update(visitor);
					}

				}
			}
			getsocketbyte_duibi(socket);
			bis = new BufferedInputStream(in);
			byte[] idf1 = new byte[2];
			bis.read(idf1);

			byte[] idf2 = new byte[2];
			bis.read(idf2);
			String userFingerId = "";

			byte[] fin1 = new byte[1];
			bis.read(fin1);// 读取数据
			String biduiStatus = Util.byteArrayToHexString(fin1);
			if ("01".equals(biduiStatus)) {
				userFingerId = Util.byteArrayToHexString(idf2);
				System.out.println("指纹ID : ==" + userFingerId);
			}

			byte[] biao_data = new byte[1];// 先接收第1个字符
			bis.read(biao_data);// 读取数据
			String mess1 = Util.byteArrayToHexString(biao_data);
			System.out.println(accessIP + "比对状态: " + biduiStatus);
			builder.append(mess1);

			// 1：N对比
			/**
			 * 第一步： 根据接收到IP或SIM标识去查找门禁设备(进门/出门/其他)
			 */

			// 暂时使用不需要指纹比对成功直接开门
			// try {
			// Socket s = new Socket(doorPort, doorIp);
			// PrintStream p = new PrintStream(s.getOutputStream());
			// p.write(2);
			// p.flush();
			// p.close();
			// s.close();
			// } catch (UnknownHostException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			if ("F5".equals(mess1)) {
				/************************* 采集指纹流程 **********************/
				String tow = readBis(bis);
				if ("0C".equals(tow) && "指纹机".equals(accessType)) {
					iii = 0;
					byte[] yg_cardId = new byte[2];// 开始接受2位16进制的编号
					bis.read(yg_cardId);// 读取数据
					String number = Util.byteArrayToHexStringK(yg_cardId).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
					System.out.println("sdasdasd：" + number);
					Integer cardNumber = 0;
					try {
						cardNumber = Integer.parseInt(number, 16);
						System.out.println("处理之后的cardNumber1为：" + cardNumber);
					} catch (Exception e) {
					}

					System.out.println("处理之后的编号为：" + cardNumber);
					builder.append(",处理之后的编号为：" + cardNumber + " ");
					if (cardNumber == 0) {
						// 有无待更新的数据
						int dbd = totalDao.getCount(
								"from DoorBangDing where status in ('待更新','待下发') and number > 0 and fk_acEq_id = (select id from AccessEquipment where equipmentIP = ?)",
								accessIP);
						if (dbd > 0) {
							getsocket_1(socket, 11);// 发0B
							Thread.sleep(3000);
							xiafa(builder, accessIP, in, bis2);
							return;
						}
					}
					List<DoorBangDing> bangDingNumber = totalDao.query(
							"from DoorBangDing where status = '已下发' and number = ? and fk_acEq_id = ?", cardNumber,
							accessId);
					if (bangDingNumber != null && bangDingNumber.size() > 0) {
						// 查询Users表是否存在。
						// 1、判断是否为内部（users表）
						List<Users> userList = totalDao.query("from Users where id = ? and onWork <>'离职' ",
								bangDingNumber.get(0).getFk_user_id());
						if (userList != null && userList.size() > 0) {
							// 2是内部员工卡
							Users users = userList.get(0);
							username = users.getName();
							// 根据标识判断处理类型。
							String retuatten = AttendanceTowServerImpl.addAttendanceTow(users.getCardId(),
									users.getCode(), users.getName(), users.getDept(), users.getId(), "指纹", accessname,
									"考勤机", null);
							/*************************** 考勤汇总方法废弃 *****************************/
							// if ("true".equals(retuatten)) {
							// builder.append("1、" + retuatten + "，考勤添加：成功。");
							// // 返回员工信息
							// String usCodenum = Util.codeIdNum(users, 259);//
							// 扬天的员工图片从26开始
							// char[] strCharCodenum = Util.huoquChar(usCodenum,
							// 4);// 得到员工图片id数组
							// char[] strChar = Util.huoquChar(users.getCode(),
							// 6);// 得到员工工号char数组
							// String nameN =
							// Util.nameGb2312(users.getName());// 得到员工姓名8位
							// String deptD =
							// Util.deptGb2312(users.getDept());// 得到员工部门8位
							// boolean b = false;// 是否在考勤时间内
							// boolean b1 = false;// 是否有班次
							// int biaoshi = 1;
							// String nowDate = Util.getDateTime("yyyy-MM-dd");
							// String nowTimeH = Util.getDateTime("HH:mm") +
							// ":00";
							// String nowDateTime = nowDate + " " + nowTimeH;//
							// 当前时间
							// String nowDateTime1 = Util.getDateTime();// 当前时间1
							//
							// // 判断时间是否小于跨天班次的最大结束时间
							// if (!"".equals(banciKuaEndTime())) {
							//
							// }
							//
							// // 查找当天是否有排班记录
							// List<Integer> sdt = totalDao.query(
							// "select banCi.id from SchedulingTable where
							// users.id = ? and dateTime = ? order by id desc",
							// users.getId(), nowDate);
							// if (sdt != null && sdt.size() > 0) {// 代表有排班记录
							// Integer schedt = sdt.get(0);
							// List<BanCi> banCis = totalDao.query("from BanCi
							// where id = ?", schedt);
							//
							// } else {// 当天没有排班的
							// // 查找昨天是否有跨天的排班记录
							// List<Integer> sdtT = totalDao.query(
							// "select banCi.id from SchedulingTable where
							// users.id = ? and dateTime = ? order by id desc",
							// users.getId(), nowDate);
							//
							// }
							// // 与当前时间对比
							// List<BanCi> banCis = totalDao.query("from BanCi
							// where id = ?", users.getBanci_id());
							// if (banCis != null && banCis.size() > 0) {
							// BanCi banCi = banCis.get(0);
							// /**
							// * 1、正常的，提示打卡成功。 2、请假的=>打卡提示：请假开始
							// * 3、请假的=>打卡提示：请假结束 4、加班的=>提示：加班开始
							// * 5、加班的=>提示：加班结束 6、迟到的提示，您已经迟到。
							// * 7、早退的，提示当前还未下班。 8、无网络连接 9、无 10、不在上班时间
							// * 11、没有班次
							// */
							// b1 = true;
							// // 查询特殊时间表
							// List<SpecialDate> listSpecil = totalDao.query(
							// "from SpecialDate where banciId = ? and date =
							// ?", banCi.getId(),
							// Util.getDateTime("yyyy-MM-dd"));
							// if (listSpecil != null && listSpecil.size() > 0)
							// {
							// if
							// ("上班".equals(listSpecil.get(0).getSpecialType()))
							// b = true;
							// } else
							// b = isbanci(banCi);
							// // 今天上班日期
							// String askForSelect = "from AskForLeave where
							// leaveUserCardId = ? and leavePerson = ? and
							// leavePersonCode = ? and approvalStatus = '同意' and
							// ((? <= leaveStartDate and leaveStartDate <= ?) or
							// (? <= leaveEndDate and leaveEndDate <= ?))";
							// String attendanceTowSelect = "from AttendanceTow
							// where cardId = ? and dateTime = ? and addTime <=
							// ?";
							// if (b) {
							// if (banCi.getFirsttime().compareTo(nowTimeH) >=
							// 0) {// 早于上班时间。正常上班
							// // 查询加班记录
							// List<Overtime> overtimes = totalDao.query(
							// "from Overtime where overtimeId = ? and
							// convert(varchar,startDate,120) like '"
							// + nowDate
							// + "%' and convert(varchar,startDate,120) <= ?
							// order by id desc",
							// users.getId(), nowDate + banCi.getFirsttime());
							// if (overtimes != null && overtimes.size() > 0) {
							// for (Overtime overtime : overtimes) {
							// if (overtime.getStartDate() != null
							// && overtime.getStartDate().equals("已打卡")) {
							// biaoshi = 4;// 加班开始
							//
							// if (overtime.getEndStatus() != null
							// && overtime.getEndStatus().equals("已打卡")) {
							// biaoshi = 5;// 加班结束
							// }
							// }
							// // if
							// // (overtime.getFilnalStartDate()!=null)
							// // {
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getEndDate())>0)
							// // {
							// //
							// overtime.setFilnalEndDate(overtime.getEndDate());
							// // }else {
							// // overtime.setFilnalEndDate(nowDateTime1);
							// // }
							// // biaoshi = 5;//加班结束
							// // }else {//加班开始时间为空
							// // //上班前的加班直接设置开始时间
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getStartDate())>0)
							// // {
							// // overtime.setFilnalStartDate(nowDateTime1);
							// // }else {
							// //
							// overtime.setFilnalStartDate(overtime.getFilnalStartDate());
							// // }
							// // biaoshi = 4;//加班开始
							// // }
							// // totalDao.update2(overtime);
							// }
							// } else {// 上班正常
							// biaoshi = 1;
							// }
							// } else if
							// (banCi.getWxstarttime().compareTo(nowTimeH) <= 0
							// && nowTimeH.compareTo(banCi.getWxendtime()) <= 0)
							// {// 早于午休开始时间，晚于午休结束时间
							// // 午休
							// // 午休时间
							// biaoshi = 1;
							// } else if
							// (banCi.getFirsttime().compareTo(nowTimeH) < 0
							// && nowTimeH.compareTo(banCi.getWxstarttime()) <
							// 0) {// 早于午休开始时间，晚于上班时间
							// List<AskForLeave> askForLeaveList =
							// totalDao.query(askForSelect,
							// users.getCardId(), users.getName(),
							// users.getCode(),
							// nowDate + " " + banCi.getFirsttime(),
							// nowDate + " " + banCi.getWxstarttime(),
							// nowDate + " " + banCi.getFirsttime(),
							// nowDate + " " + banCi.getWxstarttime());
							// if (askForLeaveList != null &&
							// askForLeaveList.size() > 0) {
							// if (askForLeaveList.size() == 1) {
							// AskForLeave askForLeave = askForLeaveList.get(0);
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow =
							// totalDao.getCount(attendanceTowSelect,
							// users.getCardId(), nowDate,
							// nowDate + " " + banCi.getFirsttime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave
							// .setExitTime(askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// } else if (askForLeaveList.size() > 1) {
							// for (AskForLeave askForLeave : askForLeaveList) {
							// int num = 1;
							// if
							// (askForLeave.getLeaveEndDate().compareTo(nowDateTime)
							// <= 0
							// || (nowDateTime).compareTo(
							// askForLeave.getLeaveStartDate()) <= 0) {//
							// 请假结束时间<当前时间||请假开始时间>当前时间
							// if (num == askForLeaveList.size()) {
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow = totalDao.getCount(
							// attendanceTowSelect, users.getCardId(),
							// nowDate,
							// nowDate + " " + banCi.getFirsttime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// break;
							// } else {
							// continue;
							// }
							// } else {
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow = totalDao.getCount(
							// attendanceTowSelect, users.getCardId(),
							// nowDate,
							// nowDate + " " + banCi.getFirsttime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// break;
							// }
							// }
							// }
							// } else {// 无请假记录
							// // 查询本次之前有无打卡记录
							// int attenTow =
							// totalDao.getCount(attendanceTowSelect,
							// users.getCardId(),
							// nowDate, nowDateTime1);
							// if (attenTow > 0) {
							// biaoshi = 7;// 早退
							// } else {
							// biaoshi = 6;// 迟到
							// }
							// }
							// } else if
							// (banCi.getWxendtime().compareTo(nowTimeH) < 0
							// && nowTimeH.compareTo(banCi.getFinishtime()) < 0)
							// {// 晚于午休结束时间，早于下班时间
							// List<AskForLeave> askForLeaveList =
							// totalDao.query(askForSelect,
							// users.getCardId(), users.getName(),
							// users.getCode(),
							// nowDate + " " + banCi.getWxendtime(),
							// nowDate + " " + banCi.getFinishtime(),
							// nowDate + " " + banCi.getWxendtime(),
							// nowDate + " " + banCi.getFinishtime());
							// if (askForLeaveList != null &&
							// askForLeaveList.size() > 0) {
							// if (askForLeaveList.size() == 1) {
							// AskForLeave askForLeave = askForLeaveList.get(0);
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow =
							// totalDao.getCount(attendanceTowSelect,
							// users.getCardId(), nowDate,
							// nowDate + " " + banCi.getWxendtime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave
							// .setExitTime(askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// } else if (askForLeaveList.size() > 1) {
							// for (AskForLeave askForLeave : askForLeaveList) {
							// int num = 1;
							// if
							// (askForLeave.getLeaveEndDate().compareTo(nowDateTime)
							// <= 0
							// || (nowDateTime).compareTo(
							// askForLeave.getLeaveStartDate()) <= 0) {//
							// 请假结束时间<当前时间||请假开始时间>当前时间
							// if (num == askForLeaveList.size()) {
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow = totalDao.getCount(
							// attendanceTowSelect, users.getCardId(),
							// nowDate,
							// nowDate + " " + banCi.getWxendtime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// break;
							// } else {
							// continue;
							// }
							// } else {
							// if (askForLeave.getExitTime() == null) {
							// if (askForLeave.getLeaveStartDate().substring(0,
							// 10)
							// .equals(nowDate)) {// 请假开始时间为今天
							// // 查询当天是否有<=上班时间的记录
							// int attenTow = totalDao.getCount(
							// attendanceTowSelect, users.getCardId(),
							// nowDate,
							// nowDate + " " + banCi.getWxendtime());
							// if (attenTow > 0) {
							// askForLeave.setExitTime(nowDateTime);
							// // 请假开始
							// biaoshi = 2;
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setExitTime(
							// askForLeave.getLeaveStartDate());
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// } else {
							// askForLeave.setReturnTime(nowDateTime);
							// // 请假结束
							// biaoshi = 3;
							// }
							// totalDao.update2(askForLeave);
							// break;
							// }
							// }
							// }
							// } else {// 无请假记录
							// // 查询午休开始之前到当前打卡时间内有无打卡记录
							// int attenTow =
							// totalDao.getCount(attendanceTowSelect,
							// users.getCardId(),
							// nowDate, nowDateTime1);
							// if (attenTow > 0) {
							// biaoshi = 7;// 早退
							// } else {
							// biaoshi = 6;// 迟到
							// }
							// }
							// } else if
							// (banCi.getFinishtime().compareTo(nowTimeH) <= 0)
							// {// 打卡时间晚于下班时间
							// // 查询加班记录
							// List<Overtime> overtimes = totalDao.query(
							// "from Overtime where overtimeId = ? and
							// convert(varchar,startDate,120) like '"
							// + nowDate
							// + "%' and convert(varchar,startDate,120) >= ?
							// order by id desc",
							// users.getId(), nowDate + " " +
							// banCi.getFinishtime());
							// if (overtimes != null && overtimes.size() > 0)
							// {// 曹安公路4560
							// for (Overtime overtime : overtimes) {
							// if (overtime.getStartDate() != null
							// && overtime.getStartDate().equals("已打卡")) {
							// biaoshi = 4;// 加班开始
							//
							// if (overtime.getEndStatus() != null
							// && overtime.getEndStatus().equals("已打卡")) {
							// biaoshi = 5;// 加班结束
							// }
							// }
							// // if
							// // (overtime.getFilnalStartDate()!=null)
							// // {
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getEndDate())>0)
							// // {
							// //
							// overtime.setFilnalEndDate(overtime.getEndDate());
							// // }else {
							// // overtime.setFilnalEndDate(nowDateTime1);
							// // }
							// // totalDao.update2(overtime);
							// // biaoshi = 5;//加班结束
							// // }else {//加班开始时间为空
							// // //下班后的加班查询今天是否有打卡记录
							// // int attenTowOver =
							// // totalDao.getCount(attendanceTowSelect,
							// // users.getCardId(),nowDate,nowDate+"
							// // "+banCi.getFirsttime());
							// // if (attenTowOver>0)
							// // {//有打卡记录
							// // //当天有打卡记录时，直接赋值加班开始时间为申请开始时间，然后根据当前时间赋值结束时间
							// //
							// overtime.setFilnalStartDate(overtime.getStartDate());
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getEndDate())>0)
							// // {
							// // overtime.setFilnalEndDate(nowDateTime1);
							// // }else {
							// //
							// overtime.setFilnalEndDate(overtime.getEndDate());
							// // }
							// // }else {
							// // //当天有打卡记录时，根据打卡时间赋值加班开始时间
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getStartDate())>0)
							// // {
							// // overtime.setFilnalStartDate(nowDateTime1);
							// // }else {
							// //
							// overtime.setFilnalStartDate(overtime.getStartDate());
							// // }
							// // }
							// // totalDao.update2(overtime);
							// // biaoshi = 4;//加班开始
							// // }
							// }
							// } else {// 下班正常
							// biaoshi = 1;
							// }
							// } else {
							// builder.append(",奇怪情况时间+" + nowTimeH);
							// }
							// } else {// 今天不为上班日期
							// if (b1) {// 班次不为空
							// // 查询加班记录
							// List<Overtime> overtimes = totalDao
							// .query("from Overtime where overtimeId = ? and
							// convert(varchar,startDate,120) like '"
							// + nowDate + "%' order by id desc",
							// users.getId());
							// if (overtimes != null && overtimes.size() > 0) {
							// for (Overtime overtime : overtimes) {
							// if (overtime.getStartDate() != null
							// && overtime.getStartDate().equals("已打卡")) {
							// biaoshi = 4;// 加班开始
							//
							// if (overtime.getEndStatus() != null
							// && overtime.getEndStatus().equals("已打卡")) {
							// biaoshi = 5;// 加班结束
							// }
							// }
							// // if
							// // (overtime.getFilnalStartDate()!=null)
							// // {
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getEndDate())>0)
							// // {
							// //
							// overtime.setFilnalEndDate(overtime.getEndDate());
							// // }else {
							// // overtime.setFilnalEndDate(nowDateTime1);
							// // }
							// // totalDao.update2(overtime);
							// // biaoshi = 5;//加班结束
							// // }else {//加班开始时间为空
							// // //上班前的加班直接设置开始时间
							// // if
							// //
							// (nowDateTime.compareTo(overtime.getStartDate())>0)
							// // {
							// // overtime.setFilnalStartDate(nowDateTime1);
							// // }else {
							// //
							// overtime.setFilnalStartDate(overtime.getFilnalStartDate());
							// // }
							// // totalDao.update2(overtime);
							// // biaoshi = 4;//加班开始
							// // }
							// }
							// } else {// 非上班时间，
							// biaoshi = 10;
							// }
							// }
							// }
							//
							// // 查询是否有汇总记录
							// List<Attendance> attendanceList = totalDao.query(
							// "from Attendance where dateTime = ? and cardNo =
							// ? and personName = ? and deptName = ?",
							// nowDate, cardIds, users.getName(),
							// users.getDept());
							// String addTimeMS = nowDateTime1.substring(12,
							// 16);
							// if (attendanceList != null &&
							// attendanceList.size() > 0) {// 有记录，更新
							// Attendance attendance = attendanceList.get(0);
							// if (banCi.getWxstarttime().compareTo(nowTimeH) <=
							// 0
							// && nowTimeH.compareTo(banCi.getWxendtime()) <= 0)
							// {// 午休时间
							// Integer workTime = (int)
							// (Util.getWorkTime2(banCi.getFirsttime(),
							// banCi.getWxstarttime()) / 60000);// 实际工作时长
							// if
							// ("迟到".equals(attendance.getAttendanceStatus())) {
							// workTime = workTime - attendance.getLateTime();
							// }
							// attendance.setWorkTime(workTime);
							// }
							// // else
							// //
							// if(banCi.getFirsttime().compareTo(nowTimeH)<0&&nowTimeH.compareTo(banCi.getWxstarttime())<0){//上午早退
							// //
							// // }else
							// //
							// if(banCi.getWxendtime().compareTo(nowTimeH)<0&&nowTimeH.compareTo(banCi.getFinishtime())<0){//下午早退
							// //
							// // }
							// else if
							// (banCi.getFinishtime().compareTo(nowTimeH) <= 0)
							// {// 下班
							// Integer workTime = (int)
							// (Util.getWorkTime2(banCi.getFirsttime(),
							// banCi.getWxstarttime()) / 60000);// 实际工作时长
							// attendance.setWorkTime(attendance.getWorkTime() +
							// workTime);
							// }
							// totalDao.update2(attendance);
							// } else {// 如果当天没有考勤记录则添加
							// Attendance attendance = new Attendance();
							// attendance.setUserId(users.getId());
							// attendance.setPersonName(users.getName());
							// attendance.setDeptName(users.getDept());
							// attendance.setCardNo(users.getCode());
							// attendance.setDateTime(nowDate);
							// attendance.setWorkDateTime(addTimeMS);
							// attendance.setOperationDate(nowDateTime1);
							// attendance.setTags("1");// 新算法标识
							// attendance.setLateTime(0);//
							// attendance.setEarlyTime(0);//
							// attendance.setJiaBTime(0);//
							// attendance.setQueqinTime(0);//
							// attendance.setQijiaTime(0);//
							//
							// Long l = Util.getWorkTime2(addTimeMS,
							// banCi.getFirsttime());
							// if (l < 0) {
							// int i = (int) ((l * -1) / 60000);
							// // if (bl) {//有请假加班的
							// // if
							// //
							// ("Overtime".equals(accessRecords.getEntityName()))
							// // {
							// // attendance.setMorningStatus("加班");
							// // }else if
							// //
							// ("AskForLeave".equals(accessRecords.getEntityName()))
							// // {
							// // attendance.setMorningStatus("请假");
							// // String shangbanTime =
							// // Util.getDateTime("yyyy-MM-dd")+"
							// // "+banCi1.getFirsttime();
							// //
							// if(nowTime.compareTo(askForLeave.getLeaveStartDate())<=0){//如果请假时间开始时间晚于等于当前时间
							// // attendance.setLateTime(i);//迟到时长
							// // }else if
							// //
							// (nowTime.compareTo(askForLeave.getLeaveEndDate())<=0)
							// // {//如果请假结束时间晚于等于当前时间
							// // if
							// //
							// (askForLeave.getLeaveStartDate().compareTo(shangbanTime)>0)//请假开始时间早于等于上班时间
							// //
							// attendance.setLateTime((int)(Util.getWorkTime1(askForLeave.getLeaveStartDate(),
							// // shangbanTime)/60000));
							// // }else if
							// //
							// (askForLeave.getLeaveEndDate().compareTo(nowTime)<0)
							// // {//如果请假结束时间早于当前时间(请假时间不够，还得计算迟到时间)
							// //
							// // }
							// // }else {
							// // attendance.setLateTime(i);//迟到时长
							// // attendance.setMorningStatus("迟到");
							// // }
							// // }else {}
							// attendance.setLateTime(i);// 迟到时长
							// attendance.setMorningStatus("迟到");
							// } else {
							// attendance.setMorningStatus("正常");
							// }
							// totalDao.save2(attendance);
							// }
							//
							// } else {
							// System.out.println(",该员工没有班次");
							// builder.append(",该员工没有班次");
							// biaoshi = 11;// 不在班次
							// }
							// // 考勤机显示个人信息
							// sendinfor(socket, strCharCodenum, nameN, deptD,
							// strChar, biaoshi);
							// // 添加汇总记录
							//
							// } else {
							// builder.append("，考勤添加：" + retuatten);
							// getsocket(socket, 0);
							// }
						} else {
							// 2不是内部员工
							System.out.println(",不是内部员工");
							builder.append(",不是内部员工");
							getsocket(socket, 0);
						}
					} else {
						getsocket(socket, 0);// 用户不存在
					}
				} else if ("23".equals(tow) && "取纹机".equals(accessType)) {// 下发单个指纹的信息流程
					/************************ 采集单人指纹流程 **********************/
					iii = 6;
					System.out.println("服务端开始接受指纹编号！");
					byte[] zhiwenBiao_1 = new byte[10];// 10位不需要数据
					bis.read(zhiwenBiao_1);// 读取数据
					byte[] zhiwenBiao = new byte[194];// 开始接受194位指纹标识
					bis.read(zhiwenBiao);// 读取数据
					String zhiwen = Util.byteArrayToHexStringK(zhiwenBiao).trim();
					// 保存至数据库
					List<FingerprintMg> fg = totalDao
							.query("from FingerprintMg where identification is null and type = '0'");
					if (fg != null && fg.size() > 0) {
						fg.get(0).setIdentification(zhiwen);
						builder.append(",指纹添加：" + totalDao.update2(fg.get(0)));
						List<DoorBangDing> bd = totalDao.query(
								"from DoorBangDing where status = '待采集' and fk_security_id = ?", fg.get(0).getId());
						if (bd != null && bd.size() > 0) {
							for (DoorBangDing doorBangDing : bd) {
								doorBangDing.setStatus("待下发");
								totalDao.update2(doorBangDing);
							}
						}
						getsocket(socket, 5);// 成功
					} else {
						builder.append(",没有要添加指纹的信息");
						getsocket(socket, 6);
						Thread.sleep(2000);
						getsocket(socket, 8);
					}
				} else {
					if ("取纹机".equals(accessType))
						getsocket(socket, 6);
					else if ("指纹机".equals(accessType))
						getsocket(socket, 0);
				}
			} else if ("01".equals(biduiStatus) && "指纹机".equals(accessType)) {// 刷卡流程
				// byte[] yg_cardId = new byte[3];// 开始接受3位16进制的卡号
				// bis.read(yg_cardId);// 读取数据
				//
				// byte[] zong_code = new byte[biao_data.length +
				// yg_cardId.length];
				// for (int i = 0; i < zong_code.length; i++) {
				// if (i < biao_data.length) {
				// zong_code[i] = biao_data[i];
				// } else {
				// zong_code[i] = yg_cardId[i - biao_data.length];
				// }
				// }
				// String card =
				// Util.byteArrayToHexStringK(zong_code).replaceAll(" ", "");//
				// 十六进制转换为String类型并去掉空格
				// System.out.println("sdasdasd：" + card);
				// String cardId = "";
				// try {
				// Integer cardNumber = Integer.parseInt(card, 16);
				// System.out.println("处理之后的cardNumber1为：" + cardNumber);
				// cardId = cardNumber.toString();
				// } catch (Exception e) {
				// cardId = Util.yanNumber(10);
				// }
				// cardId = Util.cardId(cardId);
				// System.out.println("处理之后的卡号为：" + cardId);
				// cardIds = cardId;
				// builder.append(",处理之后的卡号为：" + cardIds);
				// 查询用户

				// 查询Users表是否存在。
				// 1、判断是否为内部（users表）
				// List<Users> userList = totalDao.query("from Users where
				// cardId = ? and onWork <>'离职' ", cardId);
				List<Users> userList = totalDao
						.query("from Users where code in (" + "select code from FingerprintMg where id in "
								+ "(select fingerprintMgid from Zhongjian where canshu = ?)) ", userFingerId);
				if (userList != null && userList.size() > 0) {
					// 2是内部员工卡
					Users users = userList.get(0);
					username = users.getName();
					cardIds = users.getCardId();
					// 保存打卡记录
					if (equipmentOutIn != null && !equipmentOutIn.equals("")) {
						// users.setUserStatus("离开");
						// totalDao.update(users);

						// 开门
						try {
							Socket s = new Socket(doorPort, doorIp);
							PrintStream p = new PrintStream(s.getOutputStream());
							p.write(2);
							p.flush();
							p.close();
							s.close();
							System.out.println("开门了");
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					String retuatten = "false";//
					if (istrueKao != null && "是".equals(istrueKao)) {
						retuatten = AttendanceTowServerImpl.addAttendanceTow(users.getCardId(), users.getCode(),
								users.getName(), users.getDept(), users.getId(), "指纹", accessname, "考勤机", null);
						builder.append("1、" + retuatten + "，考勤添加：成功。");
						// 返回员工信息
						String us = users.getCode();
						String usCodenum = Util.codeIdNum(users, 259);// 扬天的员工图片从26开始

						int codeNumImage = usCodenum.length();
						for (int i = codeNumImage; i < 4; i++) {
							usCodenum = "0" + usCodenum;
						}

						int num1 = us.length();
						for (int i = num1; i < 6; i++) {
							us = "0" + us;
						}
						String[] strCodenum = usCodenum.split("");
						char[] strCharCodenum = new char[strCodenum.length - 1];
						for (int i = 1; i < strCodenum.length; i++) {
							int ic = Integer.parseInt(strCodenum[i]);
							char ch = (char) (ic);
							strCharCodenum[i - 1] = ch;
						}
						String[] strs = us.split("");
						char[] strChar = new char[6];
						for (int i = 1; i < strs.length; i++) {
							int ic = Integer.parseInt(strs[i]);
							char ch = (char) (ic);
							strChar[i - 1] = ch;
						}
						String nameN = users.getName();
						String deptD = users.getDept();
						try {
							int si = 8 - nameN.getBytes("gb2312").length;
							for (int i = 0; i < si; i++) {
								nameN = nameN + " ";
							}
							if (deptD.getBytes("gb2312").length > 8) {
								deptD = deptD.substring(0, 4);
							}
							int sl = 8 - deptD.getBytes("utf-8").length;
							for (int i = 0; i < sl; i++) {
								deptD = deptD + " ";
							}
							System.out.println(nameN);
							System.out.println(deptD);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boolean b = false;// 是否在考勤时间内
						boolean b1 = false;// 是否有班次
						int biaoshi = 1;
						// 与当前时间对比
						List<BanCi> banCis = totalDao.query("from BanCi where id = ?", users.getBanci_id());
						if (banCis != null && banCis.size() > 0) {
							BanCi banCi = banCis.get(0);
							/**
							 * 1、正常的，提示打卡成功。 2、请假的=>打卡提示：请假开始 3、请假的=>打卡提示：请假结束
							 * 4、加班的=>提示：加班开始 5、加班的=>提示：加班结束 6、迟到的提示，您已经迟到。
							 * 7、早退的，提示当前还未下班。 8、无网络连接 9、无 10、不在上班时间 11、没有班次
							 */
							b1 = true;
							String nowDate = Util.getDateTime("yyyy-MM-dd");
							String nowTimeH = Util.getDateTime("HH:mm") + ":00";
							String nowDateTime = nowDate + " " + nowTimeH;// 当前时间
							String nowDateTime1 = Util.getDateTime();// 当前时间
							// 查询特殊时间表
							List<SpecialDate> listSpecil = totalDao.query(
									"from SpecialDate where banciId = ? and date = ?", banCi.getId(),
									Util.getDateTime("yyyy-MM-dd"));
							if (listSpecil != null && listSpecil.size() > 0) {
								if ("上班".equals(listSpecil.get(0).getSpecialType()))
									b = true;
							} else
								b = isbanci(banCi);
							// 今天上班日期
							String askForSelect = "from AskForLeave where leaveUserCardId = ? and leavePerson = ? and leavePersonCode = ? and approvalStatus = '同意' and ((? <= leaveStartDate and leaveStartDate <= ?) or (? <= leaveEndDate and leaveEndDate <= ?))";
							String attendanceTowSelect = "from AttendanceTow where cardId = ? and dateTime = ? and addTime <= ?";
							if (b) {
								if (banCi.getFirsttime().compareTo(nowTimeH) >= 0) {// 早于上班时间。正常上班
									// 查询加班记录
									List<Overtime> overtimes = totalDao.query(
											"from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"
													+ nowDate
													+ "%' and convert(varchar,startDate,120) <= ? order by id desc",
											users.getId(), nowDate + banCi.getFirsttime());
									if (overtimes != null && overtimes.size() > 0) {
										for (Overtime overtime : overtimes) {
											if (overtime.getStartDate() != null
													&& overtime.getStartDate().equals("已打卡")) {
												biaoshi = 4;// 加班开始

												if (overtime.getEndStatus() != null
														&& overtime.getEndStatus().equals("已打卡")) {
													biaoshi = 5;// 加班结束
												}
											}
											// if
											// (overtime.getFilnalStartDate()!=null)
											// {
											// if
											// (nowDateTime.compareTo(overtime.getEndDate())>0)
											// {
											// overtime.setFilnalEndDate(overtime.getEndDate());
											// }else {
											// overtime.setFilnalEndDate(nowDateTime1);
											// }
											// biaoshi = 5;//加班结束
											// }else {//加班开始时间为空
											// //上班前的加班直接设置开始时间
											// if
											// (nowDateTime.compareTo(overtime.getStartDate())>0)
											// {
											// overtime.setFilnalStartDate(nowDateTime1);
											// }else {
											// overtime.setFilnalStartDate(overtime.getFilnalStartDate());
											// }
											// biaoshi = 4;//加班开始
											// }
											totalDao.update2(overtime);
										}
									} else {// 上班正常
										biaoshi = 1;
									}
								} else if (banCi.getWxstarttime().compareTo(nowTimeH) <= 0
										&& nowTimeH.compareTo(banCi.getWxendtime()) <= 0) {// 早于午休开始时间，晚于午休结束时间
																							// 午休
									// 午休时间
									biaoshi = 1;
								} else if (banCi.getFirsttime().compareTo(nowTimeH) < 0
										&& nowTimeH.compareTo(banCi.getWxstarttime()) < 0) {// 早于午休开始时间，晚于上班时间
									List<AskForLeave> askForLeaveList = totalDao.query(askForSelect, users.getCardId(),
											users.getName(), users.getCode(), nowDate + " " + banCi.getFirsttime(),
											nowDate + " " + banCi.getWxstarttime(),
											nowDate + " " + banCi.getFirsttime(),
											nowDate + " " + banCi.getWxstarttime());
									if (askForLeaveList != null && askForLeaveList.size() > 0) {
										if (askForLeaveList.size() == 1) {
											AskForLeave askForLeave = askForLeaveList.get(0);
											if (askForLeave.getExitTime() == null) {
												if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {// 请假开始时间为今天
																														// 查询当天是否有<=上班时间的记录
													int attenTow = totalDao.getCount(attendanceTowSelect,
															users.getCardId(), nowDate,
															nowDate + " " + banCi.getFirsttime());
													if (attenTow > 0) {
														askForLeave.setExitTime(nowDateTime);
														// 请假开始
														biaoshi = 2;
													} else {
														askForLeave.setExitTime(askForLeave.getLeaveStartDate());
														askForLeave.setReturnTime(nowDateTime);
														// 请假结束
														biaoshi = 3;
													}
												} else {
													askForLeave.setExitTime(askForLeave.getLeaveStartDate());
													askForLeave.setReturnTime(nowDateTime);
													// 请假结束
													biaoshi = 3;
												}
											} else {
												askForLeave.setReturnTime(nowDateTime);
												// 请假结束
												biaoshi = 3;
											}
											totalDao.update2(askForLeave);
										} else if (askForLeaveList.size() > 1) {
											for (AskForLeave askForLeave : askForLeaveList) {
												int num = 1;
												if (askForLeave.getLeaveEndDate().compareTo(nowDateTime) <= 0
														|| (nowDateTime)
																.compareTo(askForLeave.getLeaveStartDate()) <= 0) {// 请假结束时间<当前时间||请假开始时间>当前时间
													if (num == askForLeaveList.size()) {
														if (askForLeave.getExitTime() == null) {
															if (askForLeave.getLeaveStartDate().substring(0, 10)
																	.equals(nowDate)) {// 请假开始时间为今天
																						// 查询当天是否有<=上班时间的记录
																int attenTow = totalDao.getCount(attendanceTowSelect,
																		users.getCardId(), nowDate,
																		nowDate + " " + banCi.getFirsttime());
																if (attenTow > 0) {
																	askForLeave.setExitTime(nowDateTime);
																	// 请假开始
																	biaoshi = 2;
																} else {
																	askForLeave.setExitTime(
																			askForLeave.getLeaveStartDate());
																	askForLeave.setReturnTime(nowDateTime);
																	// 请假结束
																	biaoshi = 3;
																}
															} else {
																askForLeave
																		.setExitTime(askForLeave.getLeaveStartDate());
																askForLeave.setReturnTime(nowDateTime);
																// 请假结束
																biaoshi = 3;
															}
														} else {
															askForLeave.setReturnTime(nowDateTime);
															// 请假结束
															biaoshi = 3;
														}
														totalDao.update2(askForLeave);
														break;
													} else {
														continue;
													}
												} else {
													if (askForLeave.getExitTime() == null) {
														if (askForLeave.getLeaveStartDate().substring(0, 10)
																.equals(nowDate)) {// 请假开始时间为今天
																					// 查询当天是否有<=上班时间的记录
															int attenTow = totalDao.getCount(attendanceTowSelect,
																	users.getCardId(), nowDate,
																	nowDate + " " + banCi.getFirsttime());
															if (attenTow > 0) {
																askForLeave.setExitTime(nowDateTime);
																// 请假开始
																biaoshi = 2;
															} else {
																askForLeave
																		.setExitTime(askForLeave.getLeaveStartDate());
																askForLeave.setReturnTime(nowDateTime);
																// 请假结束
																biaoshi = 3;
															}
														} else {
															askForLeave.setExitTime(askForLeave.getLeaveStartDate());
															askForLeave.setReturnTime(nowDateTime);
															// 请假结束
															biaoshi = 3;
														}
													} else {
														askForLeave.setReturnTime(nowDateTime);
														// 请假结束
														biaoshi = 3;
													}
													totalDao.update2(askForLeave);
													break;
												}
											}
										}
									} else {// 无请假记录
											// 查询本次之前有无打卡记录
										int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),
												nowDate, nowDateTime);
										if (attenTow > 0) {
											biaoshi = 7;// 早退
										} else {
											biaoshi = 6;// 迟到
										}
									}
								} else if (banCi.getWxendtime().compareTo(nowTimeH) < 0
										&& nowTimeH.compareTo(banCi.getFinishtime()) < 0) {// 早于午休结束时间，晚于下班时间
									List<AskForLeave> askForLeaveList = totalDao.query(askForSelect, users.getCardId(),
											users.getName(), users.getCode(), nowDate + " " + banCi.getWxendtime(),
											nowDate + " " + banCi.getFinishtime(), nowDate + " " + banCi.getWxendtime(),
											nowDate + " " + banCi.getFinishtime());
									if (askForLeaveList != null && askForLeaveList.size() > 0) {
										if (askForLeaveList.size() == 1) {
											AskForLeave askForLeave = askForLeaveList.get(0);
											if (askForLeave.getExitTime() == null) {
												if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {// 请假开始时间为今天
																														// 查询当天是否有<=上班时间的记录
													int attenTow = totalDao.getCount(attendanceTowSelect,
															users.getCardId(), nowDate,
															nowDate + " " + banCi.getWxendtime());
													if (attenTow > 0) {
														askForLeave.setExitTime(nowDateTime);
														// 请假开始
														biaoshi = 2;
													} else {
														askForLeave.setExitTime(askForLeave.getLeaveStartDate());
														askForLeave.setReturnTime(nowDateTime);
														// 请假结束
														biaoshi = 3;
													}
												} else {
													askForLeave.setExitTime(askForLeave.getLeaveStartDate());
													askForLeave.setReturnTime(nowDateTime);
													// 请假结束
													biaoshi = 3;
												}
											} else {
												askForLeave.setReturnTime(nowDateTime);
												// 请假结束
												biaoshi = 3;
											}
											totalDao.update2(askForLeave);
										} else if (askForLeaveList.size() > 1) {
											for (AskForLeave askForLeave : askForLeaveList) {
												int num = 1;
												if (askForLeave.getLeaveEndDate().compareTo(nowDateTime) <= 0
														|| (nowDateTime)
																.compareTo(askForLeave.getLeaveStartDate()) <= 0) {// 请假结束时间<当前时间||请假开始时间>当前时间
													if (num == askForLeaveList.size()) {
														if (askForLeave.getExitTime() == null) {
															if (askForLeave.getLeaveStartDate().substring(0, 10)
																	.equals(nowDate)) {// 请假开始时间为今天
																						// 查询当天是否有<=上班时间的记录
																int attenTow = totalDao.getCount(attendanceTowSelect,
																		users.getCardId(), nowDate,
																		nowDate + " " + banCi.getWxendtime());
																if (attenTow > 0) {
																	askForLeave.setExitTime(nowDateTime);
																	// 请假开始
																	biaoshi = 2;
																} else {
																	askForLeave.setExitTime(
																			askForLeave.getLeaveStartDate());
																	askForLeave.setReturnTime(nowDateTime);
																	// 请假结束
																	biaoshi = 3;
																}
															} else {
																askForLeave
																		.setExitTime(askForLeave.getLeaveStartDate());
																askForLeave.setReturnTime(nowDateTime);
																// 请假结束
																biaoshi = 3;
															}
														} else {
															askForLeave.setReturnTime(nowDateTime);
															// 请假结束
															biaoshi = 3;
														}
														totalDao.update2(askForLeave);
														break;
													} else {
														continue;
													}
												} else {
													if (askForLeave.getExitTime() == null) {
														if (askForLeave.getLeaveStartDate().substring(0, 10)
																.equals(nowDate)) {// 请假开始时间为今天
																					// 查询当天是否有<=上班时间的记录
															int attenTow = totalDao.getCount(attendanceTowSelect,
																	users.getCardId(), nowDate,
																	nowDate + " " + banCi.getWxendtime());
															if (attenTow > 0) {
																askForLeave.setExitTime(nowDateTime);
																// 请假开始
																biaoshi = 2;
															} else {
																askForLeave
																		.setExitTime(askForLeave.getLeaveStartDate());
																askForLeave.setReturnTime(nowDateTime);
																// 请假结束
																biaoshi = 3;
															}
														} else {
															askForLeave.setExitTime(askForLeave.getLeaveStartDate());
															askForLeave.setReturnTime(nowDateTime);
															// 请假结束
															biaoshi = 3;
														}
													} else {
														askForLeave.setReturnTime(nowDateTime);
														// 请假结束
														biaoshi = 3;
													}
													totalDao.update2(askForLeave);
													break;
												}
											}
										}
									} else {// 无请假记录
											// 查询午休开始之前到当前打卡时间内有无打卡记录
										int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),
												nowDate, nowDateTime);
										if (attenTow > 0) {
											biaoshi = 7;// 早退
										} else {
											biaoshi = 6;// 迟到
										}
									}
								} else if (banCi.getFinishtime().compareTo(nowTimeH) <= 0) {// 打卡时间晚于下班时间
									// 查询加班记录
									List<Overtime> overtimes = totalDao.query(
											"from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"
													+ nowDate
													+ "%' and convert(varchar,startDate,120) >= ? order by id desc",
											users.getId(), nowDate + " " + banCi.getFinishtime());
									if (overtimes != null && overtimes.size() > 0) {// 曹安公路4560
										for (Overtime overtime : overtimes) {
											if (overtime.getStartDate() != null
													&& overtime.getStartDate().equals("已打卡")) {
												biaoshi = 4;// 加班开始

												if (overtime.getEndStatus() != null
														&& overtime.getEndStatus().equals("已打卡")) {
													biaoshi = 5;// 加班结束
												}
											}

											// if
											// (overtime.getFilnalStartDate()!=null)
											// {
											// if
											// (nowDateTime.compareTo(overtime.getEndDate())>0)
											// {
											// overtime.setFilnalEndDate(overtime.getEndDate());
											// }else {
											// overtime.setFilnalEndDate(nowDateTime1);
											// }
											// totalDao.update2(overtime);
											// biaoshi = 5;//加班结束
											// }else {//加班开始时间为空
											// //下班后的加班查询今天是否有打卡记录
											// int attenTowOver =
											// totalDao.getCount(attendanceTowSelect,
											// users.getCardId(),nowDate,nowDate+"
											// "+banCi.getFirsttime());
											// if (attenTowOver>0) {//有打卡记录
											// //当天有打卡记录时，直接赋值加班开始时间为申请开始时间，然后根据当前时间赋值结束时间
											// overtime.setFilnalStartDate(overtime.getStartDate());
											// if
											// (nowDateTime.compareTo(overtime.getEndDate())>0)
											// {
											// overtime.setFilnalEndDate(nowDateTime1);
											// }else {
											// overtime.setFilnalEndDate(overtime.getEndDate());
											// }
											// }else {
											// //当天有打卡记录时，根据打卡时间赋值加班开始时间
											// if
											// (nowDateTime.compareTo(overtime.getStartDate())>0)
											// {
											// overtime.setFilnalStartDate(nowDateTime1);
											// }else {
											// overtime.setFilnalStartDate(overtime.getStartDate());
											// }
											// }
											// totalDao.update2(overtime);
											// biaoshi = 4;//加班开始
											// }
										}
									} else {// 下班正常s
										biaoshi = 1;
									}
								} else {
									builder.append(",奇怪情况时间+" + nowTimeH);
								}
							} else {// 今天不为上班日期
								if (b1) {// 班次不为空
									// 查询加班记录
									List<Overtime> overtimes = totalDao
											.query("from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"
													+ nowDate + "%' order by id desc", users.getId());
									if (overtimes != null && overtimes.size() > 0) {
										for (Overtime overtime : overtimes) {
											if (overtime.getStartDate() != null
													&& overtime.getStartDate().equals("已打卡")) {
												biaoshi = 4;// 加班开始

												if (overtime.getEndStatus() != null
														&& overtime.getEndStatus().equals("已打卡")) {
													biaoshi = 5;// 加班结束
												}
											}
											// if
											// (overtime.getFilnalStartDate()!=null)
											// {
											// if
											// (nowDateTime.compareTo(overtime.getEndDate())>0)
											// {
											// overtime.setFilnalEndDate(overtime.getEndDate());
											// }else {
											// overtime.setFilnalEndDate(nowDateTime1);
											// }
											// totalDao.update2(overtime);
											// biaoshi = 5;//加班结束
											// }else {//加班开始时间为空
											// //上班前的加班直接设置开始时间
											// if
											// (nowDateTime.compareTo(overtime.getStartDate())>0)
											// {
											// overtime.setFilnalStartDate(nowDateTime1);
											// }else {
											// overtime.setFilnalStartDate(overtime.getFilnalStartDate());
											// }
											// totalDao.update2(overtime);
											// biaoshi = 4;//加班开始
											// }
										}
									} else {// 非上班时间，
										biaoshi = 10;
									}
								}
							}
						} else {
							System.out.println(",该员工没有班次");
							builder.append(",该员工没有班次");
							biaoshi = 11;// 不在班次
						}
						// 考勤机显示个人信息
						sendinfor(socket, strCharCodenum, nameN, deptD, strChar, biaoshi);
					} else {
						builder.append("，考勤添加：" + retuatten);
						getsocket(socket, 0);
					}
				} else {
					// 2不是内部员工
					System.out.println(",不是内部员工");
					builder.append(",不是内部员工");

					// 查询访客
					Visitor visitor = (Visitor) totalDao.getObjectByQuery(
							"from Visitor where mjFingerId is not null and mjFingerId= ?", userFingerId);
					if (visitor != null) {
						visitor.setMjFingerStatus("待删除");
						visitor.setVisitorStatus("已出门");
						totalDao.update(visitor);
					}
					getsocket(socket, 0);
				}
			} else if ("BB".equals(mess1) && "指纹机".equals(accessType)) {// 下发需要更新的指纹信息
				iii = 0;
				xiafa(builder, accessIP, in, bis2);
			} else if ("取纹机".equals(accessType)) {
				if ("BB".equals(mess1))
					return;
				iii = 3;
				// 输入工号 录取指纹操作
				byte[] code_late2 = new byte[2];// 开始接受工号后2位数
				bis.read(code_late2);// 读取数据
				builder.append(",指纹机收到工号后2位指令:" + mess1);
				byte[] zong_code = new byte[biao_data.length + code_late2.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < biao_data.length) {
						zong_code[i] = biao_data[i];
					} else {
						zong_code[i] = code_late2[i - biao_data.length];
					}
				}
				String code = Util.byteArrayToHexStringK(zong_code).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
				String code_1 = "";// 接受的工号
				try {
					Integer cardNumber = Integer.parseInt(code, 16);
					System.out.println("处理之后的code为：" + cardNumber);
					code_1 = cardNumber.toString();
				} catch (Exception e) {
					code_1 = Util.yanNumber(10);
				}
				code_1 = Util.code_1(code_1, 6);// 工号格式
				builder.append(",接收工号：" + code_1);
				// 根据工号查找用户
				List<Users> userList = totalDao.query("from Users where code = ? and onWork <> '离职'", code_1);
				if (userList != null && userList.size() > 0) {
					Users u = userList.get(0);
					// 发送员工信息
					String nameN = u.getName();
					String deptD = u.getDept();
					try {
						int si = 8 - nameN.getBytes("gb2312").length;
						for (int i = 0; i < si; i++) {
							nameN = nameN + " ";
						}
						if (deptD.getBytes("gb2312").length > 8) {
							deptD = deptD.substring(0, 4);
						}
						int sl = 8 - deptD.getBytes("utf-8").length;
						for (int i = 0; i < sl; i++) {
							deptD = deptD + " ";
						}
						System.out.println(nameN);
						System.out.println(deptD);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendinfor(socket, nameN, deptD);
					bis1 = new BufferedInputStream(in);// 新创建
					String returnZhi = readBis(bis1);
					if ("EE".equals(returnZhi)) {
						bis1 = new BufferedInputStream(in);// 新创建
						byte[] identifi_0_1 = new byte[12];// 10位不需要数据
						bis1.read(identifi_0_1);// 读取数据
						byte[] identifi = new byte[194];// 开始接受194位指纹标识
						bis1.read(identifi);// 读取数据
						String zhiwenBiao = Util.byteArrayToHexStringK(identifi).trim();// 十六进制转换为String类型
						System.out.println("长度：" + zhiwenBiao.length());
						List<FingerprintMg> listFg = totalDao.query(
								"from FingerprintMg where fingerType = '左拇指' and type = '0' and users.id = ?",
								u.getId());
						if (listFg != null && listFg.size() > 0) {// 已经添加过的。重新添加指纹将之前的作废
							FingerprintMg fg1 = new FingerprintMg();
							BeanUtils.copyProperties(listFg.get(0), fg1,
									new String[] { "id", "addTime", "identification" });
							fg1.setIdentification(zhiwenBiao);
							fg1.setAddTime(Util.getDateTime());
							if (totalDao.save2(fg1)) {// 新指纹保存成功后
								// 将下发过的状态更新
								List<DoorBangDing> bd = totalDao.query("from DoorBangDing where fk_security_id = ?",
										listFg.get(0).getId());
								if (bd != null && bd.size() > 0) {
									for (DoorBangDing doorBangDing : bd) {
										doorBangDing.setStatus("待更新");
										doorBangDing.setFk_security_id(fg1.getId());
										totalDao.update2(doorBangDing);
									}
								}
								listFg.get(0).setType("1");
								if (totalDao.update2(listFg.get(0))) {// 将旧指纹作废
									getsocket_1(socket, 2);// 添加成功
								} else {
									getsocket(socket, 3);// 添加失败 结束流程
									// 异常回滚数据
									FingerprintMg fg2 = null;
									totalDao.update2(fg2);
								}
							} else {
								getsocket(socket, 3);// 添加失败 结束流程
							}
						} else {
							FingerprintMg fg = new FingerprintMg();
							fg.setCode(u.getCode());
							fg.setDept(u.getDept());
							fg.setName(u.getName());
							fg.setFingerType("左拇指");
							fg.setType("0");
							fg.setIdentification(zhiwenBiao);
							fg.setAddTime(Util.getDateTime());
							fg.setUsers(u);
							if (totalDao.save2(fg)) {
								getsocket_1(socket, 2);// 添加成功
							} else {
								getsocket(socket, 3);// 添加失败 结束流程
								// 异常回滚数据
								FingerprintMg fg2 = null;
								totalDao.update2(fg2);
							}
						}

						bis2 = new BufferedInputStream(in);// 新创建
						// 接受第二条指纹信息
						byte[] identifi_1_1 = new byte[12];// 10位不需要数据
						bis2.read(identifi_1_1);// 读取数据
						byte[] identifi_2 = new byte[194];// 开始接受194位指纹标识
						bis2.read(identifi_2);// 读取数据
						String zhiwenBiao1 = Util.byteArrayToHexStringK(identifi_2).trim();// 十六进制转换为String类型
						List<FingerprintMg> listFg1 = totalDao.query(
								"from FingerprintMg where fingerType = '右拇指' and type = '0' and users.id = ?",
								u.getId());
						if (listFg1 != null && listFg1.size() > 0) {// 已经添加过的。重新添加指纹将之前的作废
							FingerprintMg fg1 = new FingerprintMg();
							BeanUtils.copyProperties(listFg1.get(0), fg1,
									new String[] { "id", "addTime", "identification" });
							fg1.setIdentification(zhiwenBiao1);
							fg1.setAddTime(Util.getDateTime());
							if (totalDao.save2(fg1)) {// 新指纹保存成功后
								// 将下发过的状态更新
								List<DoorBangDing> bd = totalDao.query("from DoorBangDing where fk_security_id = ?",
										listFg1.get(0).getId());
								if (bd != null && bd.size() > 0) {
									for (DoorBangDing doorBangDing : bd) {
										doorBangDing.setStatus("待更新");
										doorBangDing.setFk_security_id(fg1.getId());
										totalDao.update2(doorBangDing);
									}
								}
								listFg1.get(0).setType("1");
								if (totalDao.update2(listFg1.get(0))) {// 将旧指纹作废
									getsocket(socket, 4);// 添加成功
								} else {
									getsocket(socket, 3);// 添加失败 结束流程
									// 异常回滚数据
									FingerprintMg fg2 = null;
									totalDao.update2(fg2);
								}
							} else {
								getsocket(socket, 3);// 添加失败 结束流程
							}
						} else {
							FingerprintMg fg = new FingerprintMg();
							fg.setCode(u.getCode());
							fg.setDept(u.getDept());
							fg.setName(u.getName());
							fg.setFingerType("右拇指");
							fg.setType("0");
							fg.setIdentification(zhiwenBiao1);
							fg.setAddTime(Util.getDateTime());
							fg.setUsers(u);
							if (totalDao.save2(fg)) {
								getsocket_1(socket, 4);// 添加成功 结束
							} else {
								getsocket(socket, 3);// 添加失败 结束流程
								// 异常回滚数据
								FingerprintMg fg2 = null;
								totalDao.update2(fg2);
							}
						}
					} else {
						getsocket(socket, 9);// 返回09
					}
				} else {
					getsocket(socket, 8);// 工号不存在
					builder.append(",工号：" + code_1 + "不存在");
				}

			} else {
				builder.append(",指纹机收到无效指令:" + mess1);
				getsocket(socket, 0);
			}
			System.out.println("服务端关闭 soko");
		} catch (Exception e) {
			e.printStackTrace();
			builder.append(e);
			try {
				System.out.println("iii:" + iii);
				getsocket(socket, iii);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bis1 != null) {
					bis1.close();
				}
				if (bis2 != null) {
					bis2.close();
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close(); // 关闭Socket
					SocketServersZWKQ.clientcount--;
					System.out.println("服务端关闭,当前连接设备数量为:" + SocketServersZWKQ.clientcount);
					DoorBangDingServerImpl.caeLogInfor(builder, cardIds, yanzheng, username, inOutType, accessname,
							accessId, accessIP);
					System.out.println("断开时间：" + Util.getDateTime());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // 关闭Socket输入流
		}
	}

	/**
	 * 查询班次表中跨天班次的下班时间
	 * 
	 * @return
	 */
	public String banciKuaEndTime() {
		List<String> list = totalDao.query("selece max(finishtime) BanCi where isOvernight = '是'");
		if (list != null && list.size() > 0)
			return Util.getminuteAfter(list.get(0), 60);
		return "";
	}

	// 下发更新信息
	public void xiafa(StringBuffer builder, String accessIP, InputStream in, BufferedInputStream bis2)
			throws Exception {
		// 查找对应关系表中的待下发数据
		List<DoorBangDing> bangDings = totalDao.query(
				"from DoorBangDing where status = '待更新' and number > 0 and fk_acEq_id = (select id from AccessEquipment where equipmentIP = ?)",
				accessIP);
		if (bangDings != null && bangDings.size() > 0) {
			for (DoorBangDing doorBangDing : bangDings) {
				if (doorBangDing.getFk_security_id() != null) {
					List<FingerprintMg> flist = totalDao.query("from FingerprintMg where type = '0' and id = ?",
							doorBangDing.getFk_security_id());
					if (flist != null && flist.size() > 0) {
						getsocketbyte_delete(socket, Util.jifenByte(doorBangDing.getNumber() + "", 2),
								deleteORhuo(Util.code_1(doorBangDing.getNumber() + "", 4)));// 下发数据
						bis2 = new BufferedInputStream(in);
						byte[] identifi_2 = new byte[4];//
						bis2.read(identifi_2);// 读取数据
						byte[] identifi_3 = new byte[1];//
						bis2.read(identifi_3);// 读取数据
						String delzhiwenBack = Util.byteArrayToHexStringK(identifi_3).replaceAll(" ", "");// 十六进制转换为String类型
						System.err.println("---------" + delzhiwenBack);
						if ("00".equals(delzhiwenBack)) {
							doorBangDing.setStatus("待下发");
							totalDao.update2(doorBangDing);
						}
						builder.append("," + flist.get(0).getCode() + "delete：" + delzhiwenBack);
						Thread.sleep(100);
					}
				}
			}
		}
		List<DoorBangDing> bangDingList = totalDao.query(
				"from DoorBangDing where status = '待下发' and number > 0 and fk_acEq_id = (select id from AccessEquipment where equipmentIP = ?)",
				accessIP);
		if (bangDingList != null && bangDingList.size() > 0) {
			for (DoorBangDing doorBangDing : bangDingList) {
				if (doorBangDing.getFk_security_id() != null) {
					List<FingerprintMg> flist = totalDao.query("from FingerprintMg where type = '0' and id = ?",
							doorBangDing.getFk_security_id());
					if (flist != null && flist.size() > 0) {
						int idle = flist.get(0).getIdentification().length();
						getsocketbyte_add(socket, Util.jifenByte(doorBangDing.getNumber() + "", 2),
								Util.zhiwenbiao(flist.get(0).getIdentification().substring(0,
										flist.get(0).getIdentification().length() - 3)),
								andORhuo(Util.code_1(doorBangDing.getNumber() + "", 4),
										flist.get(0).getIdentification().substring(idle - 2, idle), 1));// 下发数据
						bis2 = new BufferedInputStream(in);
						byte[] identifi_2 = new byte[4];//
						bis2.read(identifi_2);// 读取数据
						byte[] identifi_3 = new byte[1];//
						bis2.read(identifi_3);// 读取数据
						String addzhiwenBack = Util.byteArrayToHexStringK(identifi_3).replaceAll(" ", "");// 十六进制转换为String类型
						System.out.println(addzhiwenBack);
						System.err.println("+++++++++" + addzhiwenBack);
						if ("00".equals(addzhiwenBack)) {
							doorBangDing.setStatus("已下发");
							totalDao.update2(doorBangDing);
						}

						builder.append("," + flist.get(0).getCode() + "add：" + addzhiwenBack);
						Thread.sleep(100);
					}
				}
			}
		}
		getsocket(socket, 10);
	}

	/**
	 * 查询当前用户今天是否上班
	 * 
	 * @param users
	 * @return
	 */
	public boolean isbanci(BanCi banCi) {
		boolean bool1 = false;
		// String week = "星期四";// 当前星期几中文
		String week = Util.getDateTime("E");// 当前星期几中文
		String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
		for (int l = 0; l < sbdate.length; l++) {
			if (week.equals(sbdate[l].trim())) {
				bool1 = true;
			}
		}
		return bool1;
	}

	/**
	 * 当前时间是否迟到
	 * 
	 * @param banCi
	 * @return
	 */
	public boolean isNotlate(BanCi banCi) {
		String now = Util.getDateTime("HH:mm") + ":00";
		if (banCi.getFirsttime().compareTo(now) < 0 && now.compareTo(banCi.getWxstarttime()) < 0)
			return true;
		if (banCi.getWxendtime().compareTo(now) < 0 && now.compareTo(banCi.getFinishtime()) < 0)
			return true;
		if (banCi.getFinishtime().compareTo(now) < 0)
			return true;
		return false;
	}

	public static int getMl(int num, int openOrClose) {
		Map<Integer, Integer[]> mp = new HashMap<Integer, Integer[]>();
		mp.put(1, new Integer[] { 1, 2 });
		return mp.get(num)[openOrClose];
	}

	public static boolean InTime(List<AccessTime> time) {
		String nowTime = Util.getDateTime("HH:mm:ss");
		if (time != null && time.size() > 0) {
			boolean fan = false;
			for (AccessTime time2 : time) {
				boolean a0 = Util.compareTime(nowTime, "HH:mm:ss", time2.getStartTime(), "HH:mm:ss");
				boolean a1 = Util.compareTime(time2.getEndTime(), "HH:mm:ss", nowTime, "HH:mm:ss");
				fan = a0 && a1;
				if (fan)
					return fan;
			}
		}
		return false;
	}

	/**
	 * @author Li_Cong
	 * @param sockets
	 *            连接
	 * @param i
	 *            发送指令
	 * @throws IOException
	 *             异常抛出
	 */
	private static void getsocket_1(Socket sockets, Integer i) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets.getOutputStream()));
		bw.write(i);// 发送不开门信号
		bw.flush();
	}

	/**
	 * @author Li_Cong
	 * @param sockets
	 *            连接
	 * @param i
	 *            发送指令
	 * @throws IOException
	 *             异常抛出
	 */
	private static void getsocket(Socket sockets, Integer i) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets.getOutputStream()));
		bw.write(i);// 发送不开门信号
		// bw.write(new char[] {01});
		bw.flush();
		bw.close();
	}

	public static void getsocketbyte(Socket sockets, byte[] by) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0x03);
		out.write(by);
		out.write(0x0A);
		out.flush();
	}

	/**
	 * 下发指纹信息
	 * 
	 * @param sockets
	 * @param usersNum
	 *            编号
	 * @param by
	 *            指纹特征值
	 * @param a
	 *            与值
	 * @throws IOException
	 */
	public static void getsocketbyte_add(Socket sockets, byte[] usersNum, byte[] by, int a) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(new byte[] { (byte) 0xF5, 0x41, 0x00, (byte) 0xC4, 0x00, 0x00, (byte) 0x86, (byte) 0xF5 });
		out.write(0xF5);
		out.write(0x41);
		out.write(0x00);
		out.write(0xC4);
		out.write(0x00);
		out.write(0x00);
		out.write(0x85);
		out.write(0xF5);
		out.write(0xF5);
		out.write(usersNum);
		out.write(1);
		out.write(by);
		out.write(Util.jifenByte(Integer.toHexString(a), 1));
		out.write(0xF5);
		out.flush();
	}

	/**
	 * 下发指纹信息
	 * 
	 * @param sockets
	 * @param usersNum
	 *            编号
	 * @param by
	 *            指纹特征值
	 * @param a
	 *            与值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getsocketbyte_xiafa(Socket sockets, byte[] userinfor) throws Exception {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		// out.write(new byte[] { (byte) 0xF5, 0x41, 0x00, (byte) 0xC4, 0x00,
		// 0x00, (byte) 0x85, (byte) 0xF5 });
		// Thread.sleep(100);
		// out.write(userinfor);
		out.write(ByteUtils.hexStr2Byte(
				"F5 41 00 C4 00 00 85 F5 F5 00 01 01 20 08 08 2B A1 0E 24 C3 A1 10 90 D6 01 35 07 26 61 36 A8 5F 21 38 04 26 41 3D 22 87 C1 41 88 E5 01 46 94 A3 21 4E 1C 49 01 5D 04 64 61 1F 12 2C 62 21 1A C1 22 25 85 69 82 28 1D 41 62 2C 27 06 C2 2D 0D 2A C2 33 10 AA 22 3A 13 68 82 3E 9E 84 A2 47 18 A1 22 49 8B 24 E2 4C 11 E2 C2 4F 27 22 42 50 0E E3 82 5B 08 A3 82 5B 27 62 C2 62 95 5B 22 3B 2D 1F C1 08 2E C6 22 15 32 46 A2 58 2C 5F E2 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 84 F5"
						.replaceAll(" ", "")));
		out.flush();
	}

	/**
	 * 下发指纹信息
	 * 
	 * @param sockets
	 * @param usersNum
	 *            编号
	 * @param by
	 *            指纹特征值
	 * @param a
	 *            与值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getsocketbyte_xiafa(Socket sockets, String userNum, String by)
			throws IOException, InterruptedException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		String a = Integer.toHexString(xiafayuzh(userNum, by, 01));

		if (a.length() == 1) {
			a = "0" + a;
		}

		String baoString = "F5 41 00 C4 00 00 85 F5 F5 " + userNum + "01" + by + a + "F5";
		out.write(ByteUtils.hexStr2Byte(baoString.replaceAll(" ", "")));
		out.flush();
	}

	/**
	 * 删除指纹信息
	 * 
	 * @param sockets
	 * @param usersNum
	 *            编号
	 * @param by
	 *            指纹特征值
	 * @param int
	 *            与值
	 * @throws IOException
	 */
	public static void getsocketbyte_delete(Socket sockets, byte[] usersNum, Integer a) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());

		out.write(0xF5);
		out.write(0x04);
		out.write(usersNum);
		out.write(0x00);
		out.write(0x00);
		out.write(a);
		out.write(0xF5);

		out.flush();
	}

	public static void getsocketbyte_shanchu(Socket sockets, String usersNum, Integer a) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		String delte = null;
		if (a < 10) {
			int b = 0;
			delte = "F5 04 " + usersNum + "00 00" + b + Integer.toHexString(a) + "F5";
		} else if (a > 10) {
			delte = "F5 04 " + usersNum + "00 00" + Integer.toHexString(a) + "F5";
		}

		out.write(ByteUtils.hexStr2Byte(delte.replaceAll(" ", "")));
		out.flush();
	}

	/**
	 * 1：N 手指：指纹机 对比
	 * 
	 * @param sockets
	 * @throws IOException
	 */
	public static void getsocketbyte_duibi(Socket sockets) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(new byte[] { (byte) 0xF5, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x0C, (byte) 0xF5 });
		// out.write(0xF5);
		// out.write(0x0C);
		// out.write(0X00);
		// out.write(0x00);
		// out.write(0x00);
		// out.write(0x00);
		// out.write(0x0C);
		// out.write(0xF5);
		out.flush();
	}

	/**
	 * 发送个人信息
	 * 
	 * @param sockets
	 * @param strCharCodenum
	 * @param nameN
	 * @param deptD
	 * @param strChar
	 * @param biaoshi
	 * @throws IOException
	 */
	public static void sendinfor(Socket sockets, char[] strCharCodenum, String nameN, String deptD, char[] strChar,
			int biaoshi) throws IOException {
		// 考勤机显示个人信息
		PrintWriter bw = new PrintWriter(new OutputStreamWriter(sockets.getOutputStream()));
		bw.write(0x01);//
		bw.write(strCharCodenum);// 头像编码
		bw.write(nameN);// 姓名
		bw.write(deptD);// 部门
		bw.write(strChar);// 工号
		bw.write(biaoshi);// 标识
		bw.write(0x0A);//
		bw.flush();
		bw.close();
	}

	/**
	 * 发送个人信息
	 * 
	 * @param sockets
	 * @param nameN
	 * @param deptD
	 * @throws IOException
	 */
	public static void sendinfor(Socket sockets, String nameN, String deptD) throws IOException {
		// 考勤机显示个人信息
		PrintWriter bw = new PrintWriter(new OutputStreamWriter(sockets.getOutputStream()));
		bw.write(0x07);//
		bw.write(nameN);// 姓名
		bw.write(deptD);// 部门
		bw.write(0x0A);//
		bw.flush();
	}

	/**
	 * 生成与值 添加
	 * 
	 * @param s
	 *            编号
	 * @param se
	 *            指纹标识与值
	 * @param i
	 *            类型（1/2/3）
	 * @return
	 */
	public static int andORhuo(String s, String se, int i) {
		int a = 0;
		a ^= Integer.parseInt(s.substring(0, 2));
		a ^= Integer.parseInt(s.substring(2, 4));
		a ^= i;
		a ^= Integer.parseInt(se, 16);
		return a;
	}

	/**
	 * 
	 * @param s
	 *            参数
	 * @param se
	 *            特征值
	 * @param i
	 *            权限 1
	 * @return
	 */

	public static int xiafayuzh(String s, String se, int i) {
		int a = 0;
		a ^= Integer.parseInt(s.substring(0, 2), 16);
		a ^= Integer.parseInt(s.substring(2, 4), 16);
		a ^= i;

		String[] str = se.split(" ");
		for (String string : str) {
			a ^= Integer.parseInt(string, 16);
		}
		return a;
	}

	/**
	 * 生成与值 删除
	 * 
	 * @param s
	 *            编号
	 * @param se
	 *            指纹标识与值
	 * @param i
	 *            类型（1/2/3）
	 * @return
	 */
	public static int deleteORhuo(String s) {
		int a = 4;
		a ^= Integer.parseInt(s.substring(0, 2));
		a ^= Integer.parseInt(s.substring(2, 4));
		return a;
	}

	/***
	 * 读取数据
	 * 
	 * @param bis
	 * @return
	 * @throws Exception
	 */

	private static String readBis(InputStream is) throws Exception {
		// 再截取后四位
		byte[] nenghao_data = new byte[1];// 先接收第一个字符
		is.read(nenghao_data);// 读取数据
		return Util.byteArrayToHexString(nenghao_data).replaceAll(" ", "");
	}
}
