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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.task.Dao.TotalDao;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.DownloadServerImpl;
import com.task.ServerImpl.IntegralServerDaoImpl;
import com.task.ServerImpl.menjin.DoorBangDingServerImpl;
import com.task.ServerImpl.onemark.OneLightServerImpl;
import com.task.entity.AskForLeave;
import com.task.entity.Integral;
import com.task.entity.Overtime;
import com.task.entity.Users;
import com.task.entity.XiaoFei;
import com.task.entity.banci.BanCi;
import com.task.entity.menjin.Access;
import com.task.entity.menjin.AccessEquipment;
import com.task.entity.menjin.AccessLogInfor;
import com.task.entity.menjin.AccessTime;
import com.task.entity.menjin.DoorBangDing;
import com.task.entity.menjin.DoorUseRecording;
import com.task.entity.menjin.GuardCard;
import com.task.entity.menjin.SpecialDate;
import com.task.entity.menjin.XungengRecord;
import com.task.entity.menjin.XungengTime;
import com.task.entity.onemark.OneLight;

public class SocketServersKaoQing extends Thread {

	public static final int PORT = 8868;
	public static int clientcount = 0;
	public TotalDao toalDao;

	public SocketServersKaoQing(TotalDao toalDao) {
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
				System.out.println("KaoQing ServerSocket starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}

			while (listening) {
				// 客户端计数
				clientcount++;
				// 监听到客户请求,根据得到的Socket对象和客户计数创建服务线程,并启动之
				new ServerThreadKQ(server.accept(), clientcount, toalDao)
						.start();
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
class ServerThreadKQ extends Thread {
	private static int number = 0; // 保存本进程的客户计数
	Socket socket = null; // 保存与本线程相关的Socket对象
	TotalDao totalDao;

	public ServerThreadKQ(Socket socket, int clientnum, TotalDao toalDao) {

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
		String adminCordId = "";// 管理员卡号
		String adminStatus = "";// 卡绑定状态
		String username = "";// 员工姓名
		String accessname = "";// 设备名称
		String inOutType = "";// 进出类型
		Integer accessId = 0;// 设备id
		AccessEquipment accessEquipment = null;

		try {
			accessIP = socket.getInetAddress().getHostAddress();
			System.out.println(accessIP + " 进入服务端了");
			// 由Socket对象得到输入流,并构造相应的BufferedReader对象
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);
			// 先接收接收第一个字符 用做标识
			System.out.println("服务端开始接受标识！");
			byte[] biao_data = new byte[1];// 先接收第1个字符
			bis.read(biao_data);// 读取数据
			String mess1 = Util.byteArrayToHexString(biao_data);
			System.out.println(accessIP + "标识: " + mess1);
			builder.append(mess1);
			/**
			 * 第一步： 根据接收到IP或SIM标识去查找门禁设备
			 */
			String accessType = "";// 门禁类型
			List acElist = totalDao.query("from AccessEquipment where equipmentIP=? order by id desc",accessIP);
			if (acElist != null && acElist.size() > 0) {
				accessEquipment = (AccessEquipment) acElist.get(0);
				accessType = accessEquipment.getEquipmentDaoType();
				adminCordId = accessEquipment.getAdminCardId();
				adminStatus = accessEquipment.getAdminStatus();
				accessname = accessEquipment.getEquipmentName();
				accessId = accessEquipment.getId();
			}
			if ("AA".equals(mess1)
					&& ("卫生间".equals(accessType) || "男".equals(accessType)
							|| "女".equals(accessType)
							|| "办公楼".equals(accessType) || "办公室"
							.equals(accessType))) {
				/************************ 验证码流程 **********************/
				System.out.println("服务端开始接受验证码数据！");
				byte[] yanzhengNum = new byte[6];// 开始接受6位验证码
				bis.read(yanzhengNum);// 读取数据
				int _byteLen = yanzhengNum.length;
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < _byteLen; i++) {
					int n = yanzhengNum[i];
					sb.append(n);
				}
				System.out.println(sb.toString());// 接收的验证码
				builder.append(",接收验证码：" + sb.toString());
				// 根据IP查找 门禁来访记录表
				if (sb != null && sb.length() > 0) {
					String nowTime = Util.getDateTime();
					String hql = "";
					List<Access> accessList = null;
					if ("办公室".equals(accessType)) {
						hql = "from Access where failtime > ? and yanzhengnum = ? and addTime < ? and equipmentID = ? and useSend = '未使用' and outInDoor = '进出' order by id desc";
						accessList = totalDao.query(hql, nowTime,sb.toString(), nowTime, accessId);
					} else {
						hql = "from Access where failtime > ? and yanzhengnum = ? and visitstime < ? and useSend = '已失效' and outInDoor = '进门' and entityId = (select id from Visit where visit_laiStatus <> '已出门' and id = entityId) order by id desc";
						accessList = totalDao.query(hql, nowTime,sb.toString(), nowTime);
					}
					if (accessList != null && accessList.size() > 0) {
						Access access = accessList.get(0);
						username = access.getOutInName();
						inOutType = access.getOutInDoor();
						yanzheng = sb.toString();
						getsocket(socket, 2);
						builder.append(",验证码：" + cardIds + "开门成功。");
					} else {
						// 如果没有，查询Access 表
						getsocket(socket, 0);
						builder.append(",不存在该验证码：" + sb.toString() + "开门失败 。");
					}
				}
			} else if ("00".equals(mess1)
					&& ("卫生间".equals(accessType) || "男".equals(accessType)
							|| "女".equals(accessType)
							|| "考勤机".equals(accessType)
							|| "办公室".equals(accessType) || "办公楼"
							.equals(accessType))) {
				/************************* 刷卡流程 **********************/
				byte[] yg_cardId = new byte[3];// 开始接受3位16进制的卡号
				bis.read(yg_cardId);// 读取数据

				byte[] zong_code = new byte[biao_data.length + yg_cardId.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < biao_data.length) {
						zong_code[i] = biao_data[i];
					} else {
						zong_code[i] = yg_cardId[i - biao_data.length];
					}
				}
				String card = Util.byteArrayToHexStringK(zong_code).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				System.out.println("sdasdasd：" + card);
				String cardId = "";
				try {
					Integer cardNumber = Integer.parseInt(card, 16);
					System.out.println("处理之后的cardNumber1为：" + cardNumber);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);
				System.out.println("处理之后的卡号为：" + cardId);
				cardIds = cardId;
				builder.append(",处理之后的卡号为：" + cardIds);

				// 对比卡号是否为管理员卡号
				if (adminCordId!=null&&!"".equals(adminCordId) && adminCordId.equals(cardId)) {
					getsocketbyte(socket, zong_code);
					bis2 = new BufferedInputStream(in);
					byte[] admin_data = new byte[1];// 再接收第EE字符
					bis2.read(admin_data);// 读取数据
					String messs = Util.byteArrayToHexString(admin_data);
					System.out.println("管理员卡号添加结果返回: " + messs);
					builder.append(", 管理员卡号添加结果返回:" + messs);
					inOutType = "管理";
					if ("FF".equals(messs)) {
						// 添加成功
						if (!"已设置".equals(adminStatus)) {
							getsocket(socket, 2);
							accessEquipment.setAdminStatus("已设置");
							totalDao.update2(accessEquipment);
						}
						builder.append(", 管理员添加成功");
					}
					if ("办公楼".equals(accessType)) {
						List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
						if (userList!=null&&userList.size()>0) {
							boolean b = true;
							boolean b1 = true;
							Users users = userList.get(0);
							byte[] men_data = new byte[1];// 再接收第5个字符
							bis.read(men_data);// 读取数据
							String mess = Util.byteArrayToHexString(men_data);
							System.out.println("刷卡类型标识: " + mess);
							if ("CC".equals(mess)) {
								inOutType = "进门";
							}else if ("BB".equals(mess)) {
								inOutType = "出门";b1=false;
							}else {
								b = false;
							} 
							if (b) {
								//判定是否为管理员卡 执行关灯操作
								// 检查是否还有人在门内，如果没有人了。关闭所有灯。
								String oneLig = "";
								if (b1)
									oneLig = "from OneLight where lightStatus = '关闭' and ace_id in (select id from AccessEquipment where adminCardId = ? and equipmentDaoType = '办公室') ";
								else
									oneLig = "from OneLight where lightStatus = '打开' and ace_id in (select id from AccessEquipment where adminCardId = ? and equipmentDaoType = '办公室') ";
								List<OneLight> lightList = totalDao.query(oneLig, cardId);
								if (lightList != null&& lightList.size() > 0){
									if (b1) OneLightServerImpl.staticOCLight_1(lightList,cardId,users.getName());
									else OneLightServerImpl.staticOCLight(lightList,cardId,users.getName());
								}
								try {
									DownloadServerImpl.AddDownLoads(cardId,"正常",accessname,inOutType);
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								// 保存打卡记录
								AttendanceTowServerImpl.addAttendanceTow(cardId, users.getCode(), users.getName(), users.getDept(), users.getId(),"正常",accessname, inOutType,null);
							}
						}
					}
				} else {
					// 查询Users表是否存在。
					// 1、判断是否为内部（users表）
					List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
					if (userList != null && userList.size() > 0) {
						// 2是内部员工卡
						Users users = userList.get(0);
						username = users.getName();
						cardIds = cardId;
						byte[] men_data = new byte[1];// 再接收第5个字符
						bis.read(men_data);// 读取数据
						String mess = Util.byteArrayToHexString(men_data);
						System.out.println("刷卡类型标识: " + mess);
						// 根据标识判断处理类型。
						if ("CC".equals(mess) || "BB".equals(mess)) {
							// 2、判断此门是否有权限开
							// 根据ip 查设备编号，卡号判断是否绑定过。
							List listdbd = totalDao.query("from DoorBangDing where fk_user_id = ? and fk_acEq_id = (select id from AccessEquipment where equipmentIP = ?)",users.getId(), accessIP);
							if (listdbd != null && listdbd.size() > 0) {
								DoorBangDing bangDing = (DoorBangDing) listdbd.get(0);
								// 判断时间段 如果timeIsTrue 为 是 && 此时段 不在 可进时间段范围内的
								// ，不能进
								if ("是".equals(bangDing.getTimeIsTrue())&& !timeIsTrue(bangDing.getFk_acEq_id())) {
									// 不开门返回0 记录
									builder.append("," + mess + "，此时没有进"+ accessname + "门权限，进门失败。");
									inOutType = "进门失败";
									getsocket(socket, 0);
								} else {
									if ("CC".equals(mess)) {
										inOutType = "进门";
										// 进门=>直接开门
										// 如果是厕所设备将状态改为已进门 记录ID填充 如果是办公室，只存记录
										if ("卫生间".equals(accessType)
												|| "男".equals(accessType)
												|| "女".equals(accessType)) {
											// 判断今天是否有打卡记录
											if (nowDatecardId(cardIds)) {// 如果有进门记录
												if (DoorBangDingServerImpl.addInDoorUseRecording(users, bangDing)) {
													// 设置状态为：内 并添加记录
													builder.append("," + mess+ "，进门成功。");
													getsocket(socket, 2);
												} else {
													builder.append("," + mess+ "，进门记录添加失败。");
													getsocket(socket, 2);
												}
											} else {
												builder.append("," + mess+ "，没有当天进门记录，进门失败。");
												getsocket(socket, 0);
											}
										} else {
											// 在此判断是否拥有时间权限
											builder.append("," + mess+ "，进门成功。");
											getsocket(socket, 2);
											if ("办公楼".equals(accessType)) {
												// 保存打卡记录
												AttendanceTowServerImpl.addAttendanceTow(cardId, users.getCode(), users.getName(), users.getDept(), users.getId(),"正常",accessname, inOutType,null);
											}
										}
									} else {
										inOutType = "出门";
										// 出门=>状态为内，开门，状态为外，不开门
										if ("卫生间".equals(accessType)
												|| "男".equals(accessType)
												|| "女".equals(accessType)) {
											if ("外".equals(bangDing.getStatus())) {
												// 状态为外，但是人在里面，说明是门开时候进去的，先查找当天，该门禁设备的最后一条进门数据的进门时间定为他的进门时间
												// 计算
												List acLog = totalDao.query("from AccessLogInfor where aceId = ? and addTime > ? and inOutType <>'' order by id desc",accessId,Util.getDateTime("yyyy-MM-dd"));
												if (acLog != null && acLog.size() > 0) {
													AccessLogInfor infor = (AccessLogInfor) acLog.get(0);
													DoorBangDingServerImpl.addOutDoorUseRecording(users,bangDing,infor.getAddTime());
												}
												getsocket(socket, 2);
												builder.append("," + mess+ "，状态为外，为非正常出门。");
											} else if ("内".equals(bangDing.getStatus())&& bangDing.getFk_security_id() != null) {
												if (DoorBangDingServerImpl.addOutDoorUseRecording(users, bangDing)) {
													builder.append("," + mess+ "，出门成功。");
													getsocket(socket, 2);
												} else {
													builder.append("," + mess+ "，出门记录添加失败。");
													getsocket(socket, 2);
												}
											} else {
												// 不为外 内或null
												builder.append("," + mess + "，首次出门，状态为空，开门成功。");
												bangDing.setStatus("外");
												totalDao.update2(bangDing);
												getsocket(socket, 2);
											}
										} else {
											// 在此判断是否拥有时间权限
											builder.append("," + mess + "，出门成功。");
											getsocket(socket, 2);
											if ("办公楼".equals(accessType)) {
												// 保存打卡记录
												AttendanceTowServerImpl.addAttendanceTow(cardId,users.getCode(), users.getName(), users.getDept(), users.getId(),"正常",accessname, inOutType,null);
											}
										}
										// 设置状态为：外 并添加记录
									}
								}
							} else {
								// 不开门
								builder.append("," + users.getName() + "，没有绑定权限。");
								getsocket(socket, 0);
							}
							//判断是否为门卫卡，如果是门卫卡就根据时间段查询夜巡记录，变为已巡更
							xungeng(cardIds, accessEquipment, builder);
						} else if ("DD".equals(mess)) {
							// 保存打卡记录
							String retuatten = AttendanceTowServerImpl.addAttendanceTow(users.getCardId(),users.getCode(), users.getName(), users.getDept(), users.getId(),"正常",accessname,"考勤机",null);
							if ("true".equals(retuatten)) {
								builder.append("1、"+retuatten+"，考勤添加：成功。");
								// 返回员工信息
								String us = users.getCode();
								String usCodenum = "";
								if (users.getCodeIdNum() != null
										&& users.getCodeIdNum() > 0) {
									usCodenum = (users.getCodeIdNum()+119)+"";
								} else {
									if (users != null && "女".equals(users.getSex())) {
										usCodenum = "2001";
									} else {
										usCodenum = "2000";
									}
								}

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
									int sl = 8 - deptD.getBytes("gb2312").length;
									for (int i = 0; i < sl; i++) {
										deptD = deptD + " ";
									}
									System.out.println(nameN);
									System.out.println(deptD);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								boolean b = false;//是否在考勤时间内
								int biaoshi = 1;
								biaoshi = jisuankaoqin(builder, users, b,
										biaoshi);
								// 考勤机显示个人信息
								PrintWriter bw = new PrintWriter(
										new OutputStreamWriter(socket
												.getOutputStream()));
								bw.write(0x01);//
								bw.write(strCharCodenum);//头像编码
								bw.write(nameN);//姓名
								bw.write(deptD);//部门
								bw.write(strChar);//工号
								bw.write(biaoshi);//标识
								bw.write(0x0A);//
								bw.flush();
								bw.close();
								// bis1 = new BufferedInputStream(in);
								// byte[] yg_return = new byte[22];//
								// 开始接受4位16进制的卡号
								// bis1.read(yg_return);// 读取数据
								// String return1 =
								// Util.byteArrayToHexStringK(yg_return);
								// //.replace(" ","")
								// System.out.println("return+" + return1);
								// bis1.close();
								/************************** 发送图片 *********************************/
								// if ("李聪".equals(users.getName())) {
								//
								// int[] rgb = new int[3];
								// File file = new File("D://Temp//hehe0.bmp");
								// BufferedImage bi = null;
								// try {
								// bi = ImageIO.read(file);
								// } catch (Exception e) {
								// e.printStackTrace();
								// }
								// int width = bi.getWidth();
								// int height = bi.getHeight();
								// int minx = bi.getMinX();
								// int miny = bi.getMinY();
								// System.out.println("width=" + width
								// + ",height=" + height + ".");
								// System.out.println("minx=" + minx + ",miniy="
								// + miny + ".");
								// int i1 = 0;
								// PrintStream out = null;
								// out = new
								// PrintStream(socket.getOutputStream());
								//
								// for (int i = minx; i < width; i++) {
								// for (int j = miny; j < height; j++) {
								// int pixel = bi.getRGB(i, j); //
								// 下面三行代码将一个数字转换为RGB数字
								// rgb[0] = (pixel & 0xff0000) >> 16;
								// rgb[1] = (pixel & 0xff00) >> 8;
								// rgb[2] = (pixel & 0xff);
								// // System.out.println("i=" + i + ",j=" +
								// // j
								// // + ":(" + rgb[0] + "," + rgb[1]
								// // + "," + rgb[ 2] + ")" + i1);
								// i1++;
								// out.write(rgb[0]);
								// out.write(rgb[1]);
								// out.write(rgb[2]);
								// out.flush();
								// }
								// }
								// out.close();
								// }
								/************************************/
							} else {
								builder.append("，考勤添加：" + retuatten);
								getsocket(socket, 0);
							}
						} else {
							// nn??
							System.out.println(",BB,CC,DD.???");
							builder.append(",BB,CC,DD.???");
							getsocket(socket, 0);
						}
						// 打卡
					} else {
						// 2不是内部员工
						System.out.println(",不是内部员工");
						builder.append(",不是内部员工");
						getsocket(socket, 0);
					}
				}
			} else if ("00".equals(mess1) && "咖啡机".equals(accessType)) {
				byte[] yg_cardId = new byte[3];// 开始接受3位16进制的卡号
				bis.read(yg_cardId);// 读取数据

				byte[] zong_code = new byte[biao_data.length + yg_cardId.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < biao_data.length) {
						zong_code[i] = biao_data[i];
					} else {
						zong_code[i] = yg_cardId[i - biao_data.length];
					}
				}
				String card = Util.byteArrayToHexStringK(zong_code).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				System.out.println("sdasdasd：" + card);
				String cardId = "";
				try {
					Integer cardNumber = Integer.parseInt(card, 16);
					System.out.println("处理之后的cardNumber1为：" + cardNumber);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append(",处理之后的卡号为：" + cardId);

				List<Integral> listJF = totalDao.query("from Integral where userId = (select id from Users where cardId=?)",cardId);
				if (listJF != null && listJF.size() > 0) {
					Integral integral = listJF.get(0);
					if (integral.getTotalIntegral() > 0) {
						getsocket(socket, 2);
						XiaoFei xf = new XiaoFei();
						xf.setNeirong(accessType);
						xf.setXiaofeijifen(10);
						List<XiaoFei> xflList = new ArrayList<XiaoFei>();
						xflList.add(xf);
						integral.setXfList(xflList);
						IntegralServerDaoImpl.updateIntegral1(integral);
					} else {
						getsocket(socket, 0);
					}
				} else {
					getsocket(socket, 0);
				}
			} else if ("00".equals(mess1) && "阅览室".equals(accessType)) {
				byte[] yg_cardId = new byte[3];// 开始接受3位16进制的卡号
				bis.read(yg_cardId);// 读取数据

				byte[] zong_code = new byte[biao_data.length + yg_cardId.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < biao_data.length) {
						zong_code[i] = biao_data[i];
					} else {
						zong_code[i] = yg_cardId[i - biao_data.length];
					}
				}
				String card = Util.byteArrayToHexStringK(zong_code).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				System.out.println("sdasdasd：" + card);
				String cardId = "";
				try {
					Integer cardNumber = Integer.parseInt(card, 16);
					System.out.println("处理之后的cardNumber1为：" + cardNumber);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);
				System.out.println("阅览室处理之后的卡号为：" + cardId);
				builder.append(",阅览室处理之后的卡号为：" + cardId);

				// 查询Users表是否存在。
				// 1、判断是否为内部（users表）
				List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
				if (userList != null && userList.size() > 0) {
					// 2是内部员工卡
					Users users = userList.get(0);
					username = users.getName();
					cardIds = cardId;
					// 3是内部员工卡判断有无积分
					List<Integral> listJF = totalDao.query("from Integral where userId = (select id from Users where cardId=?)",cardId);
					if (listJF != null && listJF.size() > 0) {
						Integral integral = listJF.get(0);
						if (integral.getTotalIntegral() != null && integral.getTotalIntegral() >= 0) {
							byte[] men_data = new byte[1];// 再接收第5个字符
							bis.read(men_data);// 读取数据
							String mess = Util.byteArrayToHexString(men_data);
							System.out.println("刷卡类型标识: " + mess);
							// 4、根据标识判断处理类型。
							if ("CC".equals(mess) || "BB".equals(mess)) {
								// 5、判断此门是否有权限开
								// 根据IP查设备编号，卡号判断是否绑定过。
								List listdbd = totalDao.query("from DoorBangDing where fk_user_id = ? and fk_acEq_id = (select id from AccessEquipment where equipmentIP = ?)",users.getId(), accessIP);
								if (listdbd != null && listdbd.size() > 0) {
									DoorBangDing bangDing = (DoorBangDing) listdbd.get(0);
									if ("CC".equals(mess)) {
										inOutType = "进门";
										// 进门
										// 如果是厕所设备将状态改为已进门 记录ID填充 如果是办公室，只存记录
										// 判断今天是否有打卡记录
										// if (nowDatecardId(cardIds)) {//
										// 如果有进门记录
										if ("内".equals(bangDing.getStatus()) && bangDing.getFk_security_id() != null) {
											// 状态为内部，上次出门未刷卡
											List<DoorUseRecording> doorUseRecordingList = totalDao.query("from DoorUseRecording where id = ? ",bangDing.getFk_security_id());
											if (doorUseRecordingList != null && doorUseRecordingList.size() > 0) {
												DoorUseRecording dooruse = doorUseRecordingList.get(0);
												if (dooruse != null && dooruse.getStartTime() != null&& !"".equals(dooruse.getStartTime())) {
													// 状态为内，证明上次出门未刷卡。
													// 首先根据id得到上次的进门时间，如果日期相同，将此时刷卡时间定为上次出门时间。
													// 如果日期非今天。将结束时间设置为当天23点59分59秒。
													String nowTime = Util.getDateTime();
													String entTime = "";
													if (nowTime.substring(0, 10).equals(dooruse.getStartTime().substring(0,10)))// 为当天记录
														entTime = nowTime;// 默认出门时间为下一次进门刷卡时间
													else// 不为当天记录
														entTime = dooruse.getStartTime().substring(0,10)+ " 23:59:59";// 默认结束时间为当天凌晨
													Long longs = Util.getWorkTime1(dooruse.getStartTime(),entTime);
													dooruse.setUseDateNum(longs.toString());// 使用时长毫秒
													dooruse.setUseDate(Util.formatDuring(longs));// 使用时长
													dooruse.setEndTime(entTime);
													dooruse.setUseStatus("空闲");
													totalDao.update2(dooruse);
													// 根据时间段扣除相应积分
													// 积分扣除规则30分钟扣除50积分
													XiaoFei xf = new XiaoFei();
													xf.setNeirong(accessType+ " 阅览室消耗积分");
													int jfxh = (Integer.parseInt(dooruse.getUseDateNum()) / 1000 / 60 / 30 + 1) * 50;
													xf.setXiaofeijifen(jfxh);// 消耗积分
													List<XiaoFei> xflList = new ArrayList<XiaoFei>();
													xflList.add(xf);
													integral.setXfList(xflList);
													IntegralServerDaoImpl.updateIntegral1(integral);
													integral.setTotalIntegral(integral.getTotalIntegral()- jfxh);
												} else {// 数据异常，不扣积分
													bangDing.setStatus(null);
													totalDao.update2(bangDing);
												}
											}
										}
										// 判断积分是否足够
										if (integral.getTotalIntegral() > 0) {// 正常流程
											if (DoorBangDingServerImpl.addInDoorUseRecording(users, bangDing)) {// 设置状态为：内 并添加记录
												builder.append("," + mess+ "，进门成功。");
												getsocket(socket, 2);
											} else {
												builder.append("," + mess+ "，进门记录添加失败。需查明原因");
												getsocket(socket, 0);
											}
										} else {// 积分不足
											//如果积分小余0无法进门,将状态改为外
											bangDing.setStatus("外");
											bangDing.setFk_security_id(null);
											totalDao.update2(bangDing);
											getsocket(socket, 0);
											builder.append(",进门状态异常，扣除积分后积分不足。");
										}
										// } else {
										// builder.append("," + mess
										// + "，没有当天进门记录，进门失败。");
										// getsocket(socket, 0);
										// }
									} else {
										inOutType = "出门";
										// 出门=>状态为内，开门，状态为外，不开门
										if (bangDing.getStatus() != null) {
											Long l = null;
											if ("外".equals(bangDing.getStatus())) {
												// 状态为外，但是人在里面，说明是门开时候进去的，先查找当天，该门禁设备的最后一条进门数据的进门时间定为他的进门时间
												// 计算
												List acLog = totalDao.query("from AccessLogInfor where aceId = ? and addTime > ? and inOutType <>'' order by id desc",accessId,Util.getDateTime("yyyy-MM-dd"));
												if (acLog != null&& acLog.size() > 0) {
													AccessLogInfor infor = (AccessLogInfor) acLog.get(0);
													l = DoorBangDingServerImpl.addOutDoorUseRecording_1(users,bangDing,infor.getAddTime());
													builder.append(",状态为外，为非正常出门。开始时间默认为上一次门禁开门时间");
												} else {
													l = 1l;// 如果当天没有开门记录，设定时间使用时间为1分钟
													builder.append(",状态为外，为非正常出门。开门记录，开始时间默认为1m");
												}
											} else {
												l = DoorBangDingServerImpl.addOutDoorUseRecording_1(users, bangDing);
												builder.append(",正常出门");
											}
											// 根据使用时间扣除相应积分
											XiaoFei xf = new XiaoFei();
											xf.setNeirong(accessType
													+ " 阅览室消耗积分");
											int jfxh = (int) ((l / 1000 / 60 / 30 + 1) * 50);
											xf.setXiaofeijifen(jfxh);// 消耗积分
											List<XiaoFei> xflList = new ArrayList<XiaoFei>();
											xflList.add(xf);
											integral.setXfList(xflList);
											IntegralServerDaoImpl.updateIntegral1(integral);
										} else {
											// 不为外 内或null
											builder.append("," + mess+ "，首次出门，状态为空，开门成功。");
											bangDing.setStatus("外");
											totalDao.update2(bangDing);
										}
										// 开门
										getsocket(socket, 2);
										// 检查是否还有人在门内，如果没有人了。关闭所有灯。
										int bangDingSize = totalDao.getCount("from DoorBangDing where status = '内' and fk_acEq_id = ?",accessId);
										if (bangDingSize == 0) {
											List<OneLight> lightList = totalDao.query("from OneLight where ace_id = ? and lightStatus = '打开'",accessId);
											if (lightList != null&& lightList.size() > 0)
												OneLightServerImpl.staticOCLight(lightList,users.getCode(),users.getName());
										}
									}
								} else {
									// 不开门
									builder.append("," + users.getName()+ "，没有绑定权限。");
									getsocket(socket, 0);
								}
							} else {
								// nn??
								builder.append(",BB,CC.???");
								getsocket(socket, 0);
							}
							// 打卡
						} else {
							builder.append(",此员工剩余积分数小于0");
							getsocket(socket, 0);
						}
					} else {
						// 没有积分
						builder.append(",此员工没有积分");
						getsocket(socket, 0);
					}
				} else {
					// 2不是内部员工
					builder.append(",不是内部员工");
					getsocket(socket, 0);
				}
				//判断是否为门卫卡，如果是门卫卡就根据时间段查询夜巡记录，变为已巡更
				xungeng(cardIds, accessEquipment, builder);
			} else {
				// 无操作
				builder.append(",收到无效指令:" + mess1);
				getsocket(socket, 0);
			}
			System.out.println("服务端关闭 soko");
		} catch (Exception e) {
			e.printStackTrace();
			builder.append(e);
		} finally {
			try {
				if (bis != null) {bis.close();}
				if (bis1 != null) {bis1.close();}
				if (bis2 != null) {bis2.close();}
				if (in != null) {in.close();}
				if (socket != null) {
					socket.close(); // 关闭Socket
					SocketServersKaoQing.clientcount--;
					System.out.println("服务端关闭,当前连接设备数量为:"
							+ SocketServersKaoQing.clientcount);
					DoorBangDingServerImpl.caeLogInfor(builder, cardIds,
							yanzheng, username, inOutType, accessname,
							accessId, accessIP);
					System.out.println("断开时间："+Util.getDateTime());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // 关闭Socket输入流
		}
	}

	/**
	 * 巡更操作
	 * @param cardIds
	 * @param accessEquipment
	 * 判断是否为门卫卡，如果是门卫卡就根据时间段查询夜巡记录，变为已巡更
	 */
	private void xungeng(String cardIds, AccessEquipment accessEquipment, StringBuffer buffer) {
		String hql_guardCard = "from GuardCard where cardId =?";
		List<GuardCard> guardCardList =totalDao.query(hql_guardCard,cardIds);
		if(guardCardList != null && guardCardList.size() > 0){
			//得到门禁卡
			GuardCard guardCard=guardCardList.get(0);
			if(guardCard!=null){
				//得到门禁设备
				//accessEquipment = (AccessEquipment) acElist.get(0);
				if("是".equals(accessEquipment.getIsXungeng())){//门禁设备需要巡更
					//巡更
					buffer.append("_巡更");
					String nowTime = Util.getDateTime("HH:mm:ss");//获取当前时间
					List<XungengTime> xungengTime = totalDao.query("from XungengTime where startTime <= ? and endTime > ?",nowTime,nowTime);//所有巡更时段
					if(xungengTime==null||(xungengTime!=null&&xungengTime.size()==0)){
						xungengTime = totalDao.query("from XungengTime where startTime > endTime and (startTime <= ? or endTime >= ?)", nowTime, nowTime);
					}
					if(xungengTime!=null&&xungengTime.size()>0){
						if(xungengTime.get(0)!=null){
						//查询该设备在该时间段的未巡更的记录
						String hql_xungeng = "from XungengRecord where xungengTimeId =? and equipmentId = ? and status = ? and dateNow = ?";
						List<XungengRecord> xungengList = totalDao.query(hql_xungeng, xungengTime.get(0).getId(),accessEquipment.getId(),"未巡更",Util.getDateTime("yyyy-MM-dd"));
							if(xungengList!=null&&xungengList.size()>0){
								for(XungengRecord xr : xungengList){
									xr.setGuardCardid(guardCard.getId());
									xr.setUserName(guardCard.getName());
									xr.setUserCode(guardCard.getCode());
									xr.setDakaTime(Util.getDateTime());
									xr.setStatus("已巡更");
									totalDao.update2(xr);
									buffer.append("_已巡更");
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据班次提示当前打卡的状态
	 */
	private int jisuankaoqin(StringBuffer builder, Users users, boolean b,int biaoshi) {
		boolean b1 = false;//是否有班次
		//与当前时间对比
		List<BanCi> banCis = totalDao.query("from BanCi where id = ?", users.getBanci_id()); 
		if (banCis!=null&&banCis.size()>0) {
			BanCi banCi = banCis.get(0);
			/**
			 *  1、正常的，提示打卡成功。
				2、请假的=>打卡提示：请假开始
				3、请假的=>打卡提示：请假结束
				4、加班的=>提示：加班开始
				5、加班的=>提示：加班结束
				6、迟到的提示，您已经迟到。
				7、早退的，提示当前还未下班。
				8、无网络连接
				9、无
				10、不在上班时间
				11、没有班次
			 */
			b1 = true;
			String nowDate = Util.getDateTime("yyyy-MM-dd");
			String nowTimeH = Util.getDateTime("HH:mm")+":00";
			String nowDateTime = nowDate+" "+nowTimeH;//当前时间
			//查询特殊时间表
			List<SpecialDate> listSpecil = totalDao.query("from SpecialDate where banciId = ? and date = ?", banCi.getId(),Util.getDateTime("yyyy-MM-dd"));
			if (listSpecil!=null&&listSpecil.size()>0){
				if ("上班".equals(listSpecil.get(0).getSpecialType()))
					b = true;
			}else
				b = isbanci(banCi);
			//今天上班日期
			String askForSelect = "from AskForLeave where leaveUserCardId = ? and leavePerson = ? and leavePersonCode = ? and approvalStatus = '同意' and ((? <= leaveStartDate and leaveStartDate <= ?) or (? <= leaveEndDate and leaveEndDate <= ?))";
			String attendanceTowSelect = "from AttendanceTow where cardId = ? and dateTime = ? and addTime <= ?";
			if (b) {
				if (banCi.getFirsttime().compareTo(nowTimeH)>=0){//早于上班时间。正常上班
					//查询加班记录
					List<Overtime> overtimes = totalDao.query("from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"+nowDate+"%' and convert(varchar,startDate,120) <= ? order by id desc",users.getId(),nowDate+banCi.getFirsttime());
					if (overtimes!=null&&overtimes.size()>0) {
						for (Overtime overtime : overtimes) {
							if(overtime.getStartDate()!=null && overtime.getStartDate().equals("已打卡")) {
								biaoshi = 4;//加班开始
								
								if(overtime.getEndStatus()!=null && overtime.getEndStatus().equals("已打卡")) {
									biaoshi = 5;//加班结束
								}
							}
//							if (overtime.getFilnalStartDate()!=null) {
//								if (nowDateTime.compareTo(overtime.getEndDate().toString())>0) {
//									overtime.setFilnalEndDate(overtime.getEndDate());
//								}else {
//									overtime.setFilnalEndDate(Util.getDateTime());
//								}
//								biaoshi = 5;//加班结束
//							}else {//加班开始时间为空
//								//上班前的加班直接设置开始时间
//								if (nowDateTime.compareTo(overtime.getStartDate().toString())>0) {
//									overtime.setFilnalStartDate(Util.getDateTime());
//								}else {
//									overtime.setFilnalStartDate(overtime.getFilnalStartDate());
//								}
//								biaoshi = 4;//加班开始
//							}
//							totalDao.update2(overtime);
						}
					}else {//上班正常
						biaoshi=1;
					}
				}else if (banCi.getWxstarttime().compareTo(nowTimeH)<=0&&nowTimeH.compareTo(banCi.getWxendtime())<=0){//早于午休开始时间，晚于午休结束时间 午休
					//午休时间
					biaoshi=1;
				}else if (banCi.getFirsttime().compareTo(nowTimeH)<0&&nowTimeH.compareTo(banCi.getWxstarttime())<0){//早于午休开始时间，晚于上班时间
					List<AskForLeave> askForLeaveList = totalDao.query(askForSelect, 
							users.getCardId(),users.getName(),users.getCode(),nowDate+" "+banCi.getFirsttime(), nowDate+" "+banCi.getWxstarttime(),nowDate+" "+banCi.getFirsttime(), nowDate+" "+banCi.getWxstarttime());
					if (askForLeaveList!=null&&askForLeaveList.size()>0) {
						if (askForLeaveList.size() == 1) {
							AskForLeave askForLeave = askForLeaveList.get(0);
							if (askForLeave.getExitTime()==null) {
								if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
									int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getFirsttime());
									if (attenTow > 0) {
										askForLeave.setExitTime(nowDateTime);
										//请假开始
										biaoshi=2;
									}else {
										askForLeave.setExitTime(askForLeave.getLeaveStartDate());
										askForLeave.setReturnTime(nowDateTime);
										//请假结束
										biaoshi=3;
									}
								}else {
									askForLeave.setExitTime(askForLeave.getLeaveStartDate());
									askForLeave.setReturnTime(nowDateTime);
									//请假结束
									biaoshi=3;
								}
							}else {
								askForLeave.setReturnTime(nowDateTime);
								//请假结束
								biaoshi=3;
							}
							totalDao.update2(askForLeave);
						}else if (askForLeaveList.size() > 1) {
							for (AskForLeave askForLeave : askForLeaveList) {
								int num = 1;
								if (askForLeave.getLeaveEndDate().compareTo(nowDateTime)<=0 || (nowDateTime).compareTo(askForLeave.getLeaveStartDate())<=0) {//请假结束时间<当前时间||请假开始时间>当前时间
									if (num==askForLeaveList.size()) {
										if (askForLeave.getExitTime()==null) {
											if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
												int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getFirsttime());
												if (attenTow > 0) {
													askForLeave.setExitTime(nowDateTime);
													//请假开始
													biaoshi=2;
												}else {
													askForLeave.setExitTime(askForLeave.getLeaveStartDate());
													askForLeave.setReturnTime(nowDateTime);
													//请假结束
													biaoshi=3;
												}
											}else {
												askForLeave.setExitTime(askForLeave.getLeaveStartDate());
												askForLeave.setReturnTime(nowDateTime);
												//请假结束
												biaoshi=3;
											}
										}else {
											askForLeave.setReturnTime(nowDateTime);
											//请假结束
											biaoshi=3;
										}
										totalDao.update2(askForLeave);
										break;
									}else {
										continue;
									}
								}else {
									if (askForLeave.getExitTime()==null) {
										if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
											int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getFirsttime());
											if (attenTow > 0) {
												askForLeave.setExitTime(nowDateTime);
												//请假开始
												biaoshi=2;
											}else {
												askForLeave.setExitTime(askForLeave.getLeaveStartDate());
												askForLeave.setReturnTime(nowDateTime);
												//请假结束
												biaoshi=3;
											}
										}else {
											askForLeave.setExitTime(askForLeave.getLeaveStartDate());
											askForLeave.setReturnTime(nowDateTime);
											//请假结束
											biaoshi=3;
										}
									}else {
										askForLeave.setReturnTime(nowDateTime);
										//请假结束
										biaoshi=3;
									}
									totalDao.update2(askForLeave);
									break;
								}
							}
						}
					}else {//无请假记录
						//查询本次之前有无打卡记录
						int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDateTime);
						if (attenTow>0) {
							biaoshi=7;//早退
						}else {
							biaoshi=6;//迟到
						}
					}
				}else if (banCi.getWxendtime().compareTo(nowTimeH)<0&&nowTimeH.compareTo(banCi.getFinishtime())<0){//早于午休结束时间，晚于下班时间
					List<AskForLeave> askForLeaveList = totalDao.query(askForSelect, 
							users.getCardId(),users.getName(),users.getCode(),nowDate+" "+banCi.getWxendtime(), nowDate+" "+banCi.getFinishtime(),nowDate+" "+banCi.getWxendtime(), nowDate+" "+banCi.getFinishtime());
					if (askForLeaveList!=null&&askForLeaveList.size()>0) {
						if (askForLeaveList.size() == 1) {
							AskForLeave askForLeave = askForLeaveList.get(0);
							if (askForLeave.getExitTime()==null) {
								if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
									int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(), nowDate, nowDate+" "+banCi.getWxendtime());
									if (attenTow > 0) {
										askForLeave.setExitTime(nowDateTime);
										//请假开始
										biaoshi=2;
									}else {
										askForLeave.setExitTime(askForLeave.getLeaveStartDate());
										askForLeave.setReturnTime(nowDateTime);
										//请假结束
										biaoshi=3;
									}
								}else {
									askForLeave.setExitTime(askForLeave.getLeaveStartDate());
									askForLeave.setReturnTime(nowDateTime);
									//请假结束
									biaoshi=3;
								}
							}else {
								askForLeave.setReturnTime(nowDateTime);
								//请假结束
								biaoshi=3;
							}
							totalDao.update2(askForLeave);
						}else if (askForLeaveList.size() > 1) {
							for (AskForLeave askForLeave : askForLeaveList) {
								int num = 1;
								if (askForLeave.getLeaveEndDate().compareTo(nowDateTime)<=0 || (nowDateTime).compareTo(askForLeave.getLeaveStartDate())<=0) {//请假结束时间<当前时间||请假开始时间>当前时间
									if (num==askForLeaveList.size()) {
										if (askForLeave.getExitTime()==null) {
											if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
												int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getWxendtime());
												if (attenTow > 0) {
													askForLeave.setExitTime(nowDateTime);
													//请假开始
													biaoshi=2;
												}else {
													askForLeave.setExitTime(askForLeave.getLeaveStartDate());
													askForLeave.setReturnTime(nowDateTime);
													//请假结束
													biaoshi=3;
												}
											}else {
												askForLeave.setExitTime(askForLeave.getLeaveStartDate());
												askForLeave.setReturnTime(nowDateTime);
												//请假结束
												biaoshi=3;
											}
										}else {
											askForLeave.setReturnTime(nowDateTime);
											//请假结束
											biaoshi=3;
										}
										totalDao.update2(askForLeave);
										break;
									}else {
										continue;
									}
								}else {
									if (askForLeave.getExitTime()==null) {
										if (askForLeave.getLeaveStartDate().substring(0, 10).equals(nowDate)) {//请假开始时间为今天   查询当天是否有<=上班时间的记录
											int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getWxendtime());
											if (attenTow > 0) {
												askForLeave.setExitTime(nowDateTime);
												//请假开始
												biaoshi=2;
											}else {
												askForLeave.setExitTime(askForLeave.getLeaveStartDate());
												askForLeave.setReturnTime(nowDateTime);
												//请假结束
												biaoshi=3;
											}
										}else {
											askForLeave.setExitTime(askForLeave.getLeaveStartDate());
											askForLeave.setReturnTime(nowDateTime);
											//请假结束
											biaoshi=3;
										}
									}else {
										askForLeave.setReturnTime(nowDateTime);
										//请假结束
										biaoshi=3;
									}
									totalDao.update2(askForLeave);
									break;
								}
							}
						}
					}else {//无请假记录
						//查询午休开始之前到当前打卡时间内有无打卡记录
						int attenTow = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDateTime);
						if (attenTow>0) {
							biaoshi=7;//早退
						}else {
							biaoshi=6;//迟到
						}
					}
				}else if (banCi.getFinishtime().compareTo(nowTimeH)<=0){//打卡时间晚于下班时间
					//查询加班记录
					List<Overtime> overtimes = totalDao.query("from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"+nowDate+"%' and convert(varchar,startDate,120) >= ? order by id desc",users.getId(),nowDate+" "+banCi.getFinishtime());
					if (overtimes!=null&&overtimes.size()>0) {//曹安公路4560
						for (Overtime overtime : overtimes) {
							if(overtime.getStartDate()!=null && overtime.getStartDate().equals("已打卡")) {
								biaoshi = 4;//加班开始
								
								if(overtime.getEndStatus()!=null && overtime.getEndStatus().equals("已打卡")) {
									biaoshi = 5;//加班结束
								}
							}
							
//							if (overtime.getFilnalStartDate()!=null) {
//								if (nowDateTime.compareTo(overtime.getEndDate().toString())>0) {
//									overtime.setFilnalEndDate(overtime.getEndDate());
//								}else {
//									overtime.setFilnalEndDate(Util.getDateTime());
//								}
//								totalDao.update2(overtime);
//								biaoshi = 5;//加班结束
//							}else {//加班开始时间为空
//								//下班后的加班查询今天是否有打卡记录
//								int attenTowOver = totalDao.getCount(attendanceTowSelect, users.getCardId(),nowDate,nowDate+" "+banCi.getFirsttime());
//								if (attenTowOver>0) {//有打卡记录
//									//当天有打卡记录时，直接赋值加班开始时间为申请开始时间，然后根据当前时间赋值结束时间
//									overtime.setFilnalStartDate(overtime.getStartDate());
//									if (nowDateTime.compareTo(overtime.getEndDate().toString())>0) {
//										overtime.setFilnalEndDate(Util.getDateTime());
//									}else {
//										overtime.setFilnalEndDate(overtime.getEndDate());
//									}
//								}else {
//									//当天有打卡记录时，根据打卡时间赋值加班开始时间
//									if (nowDateTime.compareTo(overtime.getStartDate().toString())>0) {
//										overtime.setFilnalStartDate(Util.getDateTime());
//									}else {
//										overtime.setFilnalStartDate(overtime.getStartDate());
//									}
//								}
//								totalDao.update2(overtime);
//								biaoshi = 4;//加班开始
//							}
						}
					}else {//下班正常
						biaoshi=1;
					}
				}else {
					builder.append(",奇怪情况时间+"+nowTimeH);
				}
			}else {//今天不为上班日期
				if (b1) {//班次不为空 
					//查询加班记录
					List<Overtime> overtimes = totalDao.query("from Overtime where overtimeId = ? and convert(varchar,startDate,120) like '"+nowDate+"%' order by id desc",users.getId());
					if (overtimes!=null&&overtimes.size()>0) {
						for (Overtime overtime : overtimes) {
							if(overtime.getStartDate()!=null && overtime.getStartDate().equals("已打卡")) {
								biaoshi = 4;//加班开始
								
								if(overtime.getEndStatus()!=null && overtime.getEndStatus().equals("已打卡")) {
									biaoshi = 5;//加班结束
								}
							}
//							if (overtime.getFilnalStartDate()!=null) {
//								if (nowDateTime.compareTo(overtime.getEndDate().toString())>0) {
//									overtime.setFilnalEndDate(overtime.getEndDate());
//								}else {
//									overtime.setFilnalEndDate(Util.getDateTime());
//								}
//								totalDao.update2(overtime);
//								biaoshi = 5;//加班结束
//							}else {//加班开始时间为空
//								//上班前的加班直接设置开始时间
//								if (nowDateTime.compareTo(overtime.getStartDate().toString())>0) {
//									overtime.setFilnalStartDate(Util.getDateTime());
//								}else {
//									overtime.setFilnalStartDate(overtime.getFilnalStartDate());
//								}
//								totalDao.update2(overtime);
//								biaoshi = 4;//加班开始
//							}
						}
					}else {//非上班时间，
						biaoshi=10;
					}
				}
			}
		}else {
			System.out.println(",该员工没有班次");
			builder.append(",该员工没有班次");
			biaoshi = 11;//不在班次
		}
		return biaoshi;
	}

	
	/**
	 * 查询当前用户今天是否上班
	 * @param users
	 * @return
	 */
	public boolean isbanci(BanCi banCi){
		boolean bool1 = false;
//		String week = "星期四";// 当前星期几中文
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
	 * @param banCi
	 * @return
	 */
	public boolean isNotlate(BanCi banCi){
		String now = Util.getDateTime("HH:mm")+":00";
		if (banCi.getFirsttime().compareTo(now)<0&&now.compareTo(banCi.getWxstarttime())<0)
			return true;
		if (banCi.getWxendtime().compareTo(now)<0&&now.compareTo(banCi.getFinishtime())<0)
			return true;
		if (banCi.getFinishtime().compareTo(now)<0)
			return true;
		return false;
	}
	
	
	public static int getMl(int num, int openOrClose) {
		Map<Integer, Integer[]> mp = new HashMap<Integer, Integer[]>();
		mp.put(1, new Integer[] { 1, 2 });
		return mp.get(num)[openOrClose];
	}

	/**
	 * 是否在此时间段 i 设备id
	 */
	private boolean timeIsTrue(int i) {
		// TODO Auto-generated method stub
		// 查询该设备对应的时间表
		List<AccessTime> accessTimesList = totalDao.query(
				"from AccessTime where ta_acTime = ?", i);
		if (accessTimesList != null && accessTimesList.size() > 0)
			return InTime(accessTimesList);
		else
			return false;
	}

	public static boolean InTime(List<AccessTime> time) {
		String nowTime = Util.getDateTime("HH:mm:ss");
		if (time != null && time.size() > 0) {
			boolean fan = false;
			for (AccessTime time2 : time) {
				boolean a0 = Util.compareTime(nowTime, "HH:mm:ss", time2
						.getStartTime(), "HH:mm:ss");
				boolean a1 = Util.compareTime(time2.getEndTime(), "HH:mm:ss",
						nowTime, "HH:mm:ss");
				fan = a0 && a1;
				if (fan)
					return fan;
			}
		}
		return false;
	}

	/**
	 * 验证今天是否进门？
	 * 
	 * @param cardId
	 * @return
	 */
	private boolean nowDatecardId(String cardId) {
		// TODO Auto-generated method stub
		int e = totalDao
				.getCount(
						"from AccessRecords where (recordContents = ? or inmarkId = ?) and addTime > ?",
						cardId, cardId, Util.getDateTime("yyyy-MM-dd"));
		if (e > 0) {
			return true;
		}
		int i = totalDao.getCount(
				"from BrushCard where cardNo = ? and brushDate = ?", Integer
						.parseInt(cardId)
						+ "", Util.getDateTime("yyyy-MM-dd"));
		if (i > 0) {
			return true;
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
	private static void getsocket(Socket sockets, Integer i) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
				.getOutputStream()));
		bw.write(i);// 发送不开门信号
		// bw.write(new char[] {01});
		bw.flush();
		bw.close();
	}

	public static void getsocketbyte(Socket sockets, byte[] by)
			throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0x03);
		out.write(by);
		out.write(0x0A);
		out.flush();
	}

	/***
	 * 读取数据
	 * 
	 * @param bis
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static String readBis(InputStream is) throws Exception {
		// 再截取后四位
		byte[] nenghao_data = new byte[1];// 先接收第一个字符
		is.read(nenghao_data);// 读取数据
		return Util.byteArrayToHexString(nenghao_data);
	}
}
