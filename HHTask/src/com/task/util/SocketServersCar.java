package com.task.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.task.Dao.TotalDao;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.DownloadServerImpl;
import com.task.ServerImpl.menjin.AccessRecordsServerImpl;
import com.task.ServerImpl.menjin.AccessServerImpl;
import com.task.ServerImpl.menjin.DoorBangDingServerImpl;
import com.task.ServerImpl.onemark.OneLightServerImpl;
import com.task.entity.AskForLeave;
import com.task.entity.Attendance;
import com.task.entity.Overtime;
import com.task.entity.Users;
import com.task.entity.banci.BanCi;
import com.task.entity.menjin.Access;
import com.task.entity.menjin.AccessEquipment;
import com.task.entity.menjin.AccessRecords;
import com.task.entity.menjin.AccessSwitch;
import com.task.entity.menjin.AccessWebcam;
import com.task.entity.menjin.AttendanceDai;
import com.task.entity.menjin.CarInOutType;
import com.task.entity.menjin.GuardCard;
import com.task.entity.menjin.InEmployeeCarInfor;
import com.task.entity.menjin.SpecialDate;
import com.task.entity.menjin.UrgentAccess;
import com.task.entity.menjin.Visit;
import com.task.entity.menjin.WaterRechargeZhu;
import com.task.entity.menjin.WaterUseRecording;
import com.task.entity.onemark.OneLight;

public class SocketServersCar extends Thread {

	public static final String SERVER_IP = "192.168.0.161";
	public static final int PORT = 8866;
	public static int clientcount = 0;
	public TotalDao toalDao;

	public SocketServersCar(TotalDao toalDao) {
		this.toalDao = toalDao;
	}

	// public static void startServer() {
	public void run() {
		try {
			// int clientcount = 0; // 统计客户端总数
			boolean listening = true; // 是否对客户端进行监听
			ServerSocket server = null; // 服务器端Socket对象

			try {
				// 创建一个ServerSocket在端口2121监听客户请求
				server = new ServerSocket(PORT);

				System.out.println("Car ServerSocket starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}

			while (listening) {
				// 客户端计数
				clientcount++;
				// 监听到客户请求,根据得到的Socket对象和客户计数创建服务线程,并启动之
				new ServerThreadcar(server.accept(), clientcount, toalDao)
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
class ServerThreadcar extends Thread {
	private static int number = 0; // 保存本进程的客户计数
	Socket socket = null; // 保存与本线程相关的Socket对象
	TotalDao totalDao;

	public ServerThreadcar(Socket socket, int clientnum, TotalDao toalDao) {
		this.socket = socket;
		number = clientnum;
		this.totalDao = toalDao;
		System.out.println("当前在线的摄像头数: " + number);
	}

	public void run() {
		BufferedInputStream bis = null;
		InputStream in = null;
		String accessIP = "";// 门禁IP
		String cardIds = "";// 卡号
		String yanzheng = "";//来访人员验证码
		String username = "";//员工姓名
		String accessname = "";// 设备名称
		String inOutType = "";// 进出类型
		Integer accessId = 0;// 设备id
		AccessEquipment accessEquipment = null;
//		String adminCordId = "";// 管理员卡号
//		String adminStatus = "";// 卡绑定状态
		StringBuffer builder = new StringBuffer();
		try {
			accessIP = socket.getInetAddress().getHostAddress();
			System.out.println(accessIP + " 进入服务端了");
			// 由Socket对象得到输入流,并构造相应的BufferedReader对象
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);
			// 先接收接收第一个字符 用做标识u
			byte[] yz_Biaoshi = new byte[1];// 
			bis.read(yz_Biaoshi);// 读取数据
			String mes = Util.byteArrayToHexString(yz_Biaoshi);
			System.out.println("[Client " + number + "]: " + mes);
			builder.append(mes+",");
			/**
			 * 第一步： 根据接收到IP或SIM标识去查找门禁设备
			 */
			String accessType = "";// 门禁类型
			List acElist = totalDao.query("from AccessEquipment where equipmentIP=? order by id desc",accessIP);
			if (acElist != null && acElist.size() > 0) {
				accessEquipment = (AccessEquipment) acElist.get(0);
				accessname = accessEquipment.getEquipmentName();
				accessType = accessEquipment.getEquipmentDaoType();
				accessId = accessEquipment.getId();
//				adminCordId = accessEquipment.getAdminCardId();
//				adminStatus = accessEquipment.getAdminStatus();
			}
			// 确认发送信息
			/*************************** 1、地感线圈测试已通过 ************************/
			/*************************** 地感线圈测试已通过 ************************/
			/*************************** 可以发RTX消息，状态改为已通过或已失效 ************************/
			if(("FF".equals(mes)||"CC".equals(mes)||"EF".equals(mes)||"AA".equals(mes)) && "停车场".equals(accessType)){
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+mes);
				if ("CC".equals(mes)) {//确认通过。
					//查找该设备最后一条通过记录
					AttendanceTowServerImpl.updateStatus(accessId, builder);
				}else if ("AA".equals(mes)){
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
						String hql = "from Access where yanzhengnum = ? and equipmentDaoType = '车行道' and visitstime < ? and useSend = '已失效' and outInDoor = '进门' and entityId = (select id from Visit where visit_laiStatus <> '已出门' and id = entityId) order by id desc";
						List<Access> accessList = totalDao.query(hql, sb.toString(), nowTime);
						if (accessList != null && accessList.size() > 0) {
							Access access = accessList.get(0);
							username = access.getOutInName();
							inOutType = access.getOutInDoor();
							yanzheng = sb.toString();
							getsocket(socket, 1);
							builder.append(",验证码：" + cardIds + "开门成功。");
						} else {
							// 如果没有，查询Access 表
							getsocket(socket, 0);
							builder.append(",不存在该验证码：" + sb.toString() + "开门失败 。");
						}
					}
				}
				else{
					getsocket(socket, 0);
					builder.append("其他，发送0");
				}
			}else if ("FF".equals(mes)) {
				// 标识为地感线圈触发--车辆已进门
				// 通过设备IP去查找开门记录中最迟一条记录，将其设置为已进门
				// 通过开门记录中的（进门/出门）以及开门验证内容来查找出来访表中的来访记录，将其设置为已通过
				String nowTime = Util.getDateTime();
				List accessRecordList = getAccessRecor(accessIP, "已开门");
				if (accessRecordList != null && accessRecordList.size() > 0) {
					AccessRecords accessRecords = (AccessRecords) accessRecordList.get(0);
					builder.append(accessRecords.getId() + "+"+ accessRecords.getRecordContents());
					// 根据车牌号查找CarInOutType表中对象，用于改变状态
					List carInOutTypelist = totalDao.query("from CarInOutType where carPai=? order by id desc",accessRecords.getRecordContents());
					if (carInOutTypelist!=null&&carInOutTypelist.size() > 0) {
						System.out.println(accessRecords.getAsWeam_ip());
						builder.append("重开门"+AccessServerImpl.openDoor(accessRecords.getAsWeam_ip())+"ip"+accessRecords.getAsWeam_ip());//重新开门一次
						if ("内部".equals(accessRecords.getRecordisIn())) {
							// 内部车辆
							accessRecords.setRecordStatus("已通过");
							accessRecords.setEnterTime(Util.getDateTime());
							totalDao.update2(accessRecords);
							
							// 改变车辆状态表的状态
							CarInOutType carInOutType = (CarInOutType) carInOutTypelist.get(0);
							carInOutType.setCarInOut(accessRecords.getOpenType());
							carInOutType.setUpdateTime(Util.getDateTime());
							totalDao.update2(carInOutType);

							//控制灯
							if ("是".equals(accessRecords.getIsKong())) {
								//根据卡号查找管理员办公室设备绑定的所有灯
								List<OneLight> lightOne = totalDao.query("from OneLight where ace_id in (select id from AccessEquipment where adminCardId = ? and equipmentDaoType = '办公室') ", accessRecords.getInmarkId());
								if (lightOne != null && lightOne.size() > 0)
									OneLightServerImpl.staticOCLight_1(lightOne,accessRecords.getInCode(),accessRecords.getInName(),accessRecords.getOpenType());
							}
							
							builder.append(","+ accessRecords.getRecordStatus() + " "+ carInOutType.getCarInOut());
							// 根据车牌查询是否有请假加班记录，如有，设置为已失效
							List accesslist = totalDao.query("from Access where carPai=? and outInDoor=? and failtime > ? and visitstime < ? and useSend <> '已失效' order by id desc",accessRecords.getRecordContents(),accessRecords.getOpenType(),nowTime, nowTime);
							if (accesslist != null && accesslist.size() > 0) {
								Access access = (Access) accesslist.get(0);
								System.out.println("内部Access+++++++++++++++++++++++++++++++" + access.getId());
								access.setUseSend("已失效");
								totalDao.update2(access);
								// 添加内部白名单出借状态为：正常车辆的考勤记录
//								String kao="";
//								try {
//									kao = DownloadServerImpl.AddDownLoads(access.getCardId().trim(),"正常",accessType,accessRecords.getOpenType());
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//									builder.append(","+e+" 车有申请考勤异常");
//								}
								builder.append("," + access.getOutInName()+ access.getCarPai() + " 车辆已"+ access.getOutInDoor() + "(请假/加班)"+ access.getUseSend());
							} else {
								builder.append(accessRecords.getInName()
										+ accessRecords.getRecordContents()
										+ " 车辆" + accessRecords.getOpenType()
										+ " 为正常进出");
							}

							// 添加考勤记录
							List inEmployeeCarInforlist = totalDao
									.query(
											"from InEmployeeCarInfor where nplates = ? and carInCangType ='内部' and carType='个人'",
											accessRecords.getRecordContents());
							if (inEmployeeCarInforlist != null
									&& inEmployeeCarInforlist.size() > 0) {
								InEmployeeCarInfor inEmployeeCarInfor = (InEmployeeCarInfor) inEmployeeCarInforlist
										.get(0);
								if ("是".equals(inEmployeeCarInfor
										.getRtxMessage())) {
									// 给需要发消息的车主发送RTX消息
									List<String> codes1 = new ArrayList<String>();
									codes1.add(accessRecords.getInCode());
									if ("苏E6M6L0".equals(accessRecords.getRecordContents())) {
										codes1.add("479");
										codes1.add("472");
									}
									boolean b = false;
									if (codes1 != null && codes1.size() > 0) {
										b = RtxUtil.sendNoLoginNotify(codes1, "您的车牌号为 "+ accessRecords.getRecordContents() + " 的车辆已 "+ accessRecords.getOpenType() + " !", "系统消息", "0", "0");
									}
									builder.append(",RTX" + b + "");
								}
								builder.append(",卡号" + inEmployeeCarInfor.getNcardId());
								if ("正常".equals(inEmployeeCarInfor.getBorrowStatus())) {
									String kao = "";
//									try {
//										kao = DownloadServerImpl.AddDownLoads(inEmployeeCarInfor.getNcardId(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType());
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//										builder.append(","+e+" 车考勤异常");
//									}
									String kao1 = "无";
									try {
										kao1 = AttendanceTowServerImpl.addAttendanceTow(inEmployeeCarInfor.getNcardId(),accessRecords.getInCode(),accessRecords.getInName(), inEmployeeCarInfor.getNdept(), accessRecords.getInId(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType(),null);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										builder.append(","+e+" 车考勤异常1");
									}
									builder.append(accessRecords.getInName()+ accessRecords.getRecordContents()+ ",车辆考情：" + kao+"kao1:"+kao1);
								}
							}
						} else if ("常访".equals(accessRecords.getRecordisIn())) {
							// 常访车辆
							accessRecords.setRecordStatus("已通过");
							accessRecords.setEnterTime(Util.getDateTime());
							totalDao.update2(accessRecords);

							// 改变车辆状态表的状态
							CarInOutType carInOutType = (CarInOutType) carInOutTypelist.get(0);
							carInOutType.setCarInOut(accessRecords.getOpenType());
							carInOutType.setUpdateTime(Util.getDateTime());
							totalDao.update2(carInOutType);
							builder.append(accessRecords.getInName()+ accessRecords.getRecordContents()+ accessRecords.getRecordisIn() + "已通过");
						} else {
							// 外部的车辆
							accessRecords.setRecordStatus("已通过");
							accessRecords.setEnterTime(Util.getDateTime());
							totalDao.update2(accessRecords);
							// 根据车牌号查找CarInOutType表中对象，改变状态
							CarInOutType carInOutType = (CarInOutType) carInOutTypelist.get(0);
							carInOutType.setCarInOut(accessRecords.getOpenType());
							carInOutType.setUpdateTime(Util.getDateTime());
							totalDao.update2(carInOutType);

							List<Access> accesseList = totalDao.query("from Access where carPai=? and failtime > ? and visitstime < ? and useSend<>'已失效' and outInDoor=? order by id desc",accessRecords.getRecordContents(),nowTime, nowTime, accessRecords.getOpenType());
							if (accesseList != null && accesseList.size() > 0) {
								Access access = accesseList.get(0);
								System.out.println("Access来访+++++++++++++++++++++++++++++++"+ access.getId());
								builder.append(",Access来访+"+access.getId());
								access.setUseSend("已失效");
								totalDao.update2(access);
								List list = totalDao.query("from Visit where id=?", access.getEntityId());
								if (list != null && list.size() > 0) {
									Visit visit = (Visit) list.get(0);
									builder.append(",Visit来访+"+visit.getId());
									if ("进门".equals(access.getOutInDoor())) {
										visit.setVisit_laiStatus("已进门");// 进门后状态变为已进门，此时可以点击来访结束
									} else if ("出门".equals(access.getOutInDoor())) {
										visit.setVisit_laiStatus("已出门");// 出门后状态为已出门，来访结束
									} else {
										visit.setVisit_laiStatus("不出不进");
									}
									totalDao.update2(visit);
								}
								System.out.println(access.getCarPai() + " 车辆已" + access.getOutInDoor() + "(来访)");
								builder.append(","+access.getCarPai() + " 车辆已" + access.getOutInDoor() + "(来访)");
							}
						}
					} else {
						// 验证码
						// 外部的车辆
						accessRecords.setRecordStatus("已通过");
						accessRecords.setEnterTime(Util.getDateTime());
						totalDao.update2(accessRecords);

						List<Access> accesseList = totalDao
								.query(
										"from Access where yanzhengnum=? and failtime > ? and visitstime < ? and useSend<>'已失效' and outInDoor=? order by id desc",
										accessRecords.getRecordContents(),// ***************************??//
										nowTime, nowTime, accessRecords
												.getOpenType());
						if (accesseList != null && accesseList.size() > 0) {
							Access access = accesseList.get(0);
							System.out.println("Access+++++++++++++++++++++++++++++++" + access.getId());
							access.setUseSend("已失效");
							totalDao.update2(access);
							List list = totalDao.query("from Visit where id=?",
									access.getEntityId());
							if (list != null && list.size() > 0) {
								Visit visit = (Visit) list.get(0);
								if ("进门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已进门");// 进门后状态变为已进门，此时可以点击来访结束
								} else if ("出门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已出门");// 出门后状态为已出门，来访结束
								} else {
									visit.setVisit_laiStatus("不出不进");
								}
								totalDao.update2(visit);
							}
							System.out.println("验证码为："+ access.getYanzhengnum() + " 车辆已"+ access.getOutInDoor() + "(来访)");
							builder.append(",验证码为："+ access.getYanzhengnum() + " 车辆已"+ access.getOutInDoor() + "(来访)");
						}
					}
				}
				LedCarPush.ledShow("", "", 7);// 推送LED消息 出门结束
				/********************************* 1、地感线圈流程结束 *********************************/
			} else
			/********************************* 验证码测试已通过 *********************************/
			/********************************* 人行道确认后可已开门 *********************************/
			/********************************* 车行道确认后可已开门 *********************************/
			/********************************* 没网的情况下，线程会卡5秒，提示网络异常，开门失败 *********************************/
			/********************************* 验证码测试已通过 *********************************/
			if ("AA".equals(mes)) {
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
				yanzheng = sb.toString();
				// 根据IP查找 门禁来访记录表
				if (sb != null && sb.length() > 0) {
					String nowTime = Util.getDateTime();
					String hql = "from Access where failtime > ? and yanzhengnum = ? and visitstime < ? and useSend <> '已失效' and equipmentIP=?";
					List<Access> accessList = totalDao.query(hql, nowTime, sb
							.toString(), nowTime, accessIP);
					if (accessList != null && accessList.size() > 0) {
						Access access = accessList.get(0);
						if (access != null) {
							if ("车行道".equals(access.getEquipmentDaoType())) {
								// 车行道，通过设备ip获取摄像头ip
								// 记录不为空 根据设备IP查找对应（进出）的摄像头（ip）开门
								List<AccessWebcam> accessWebcamList = totalDao
										.query(
												"from AccessWebcam where aeqt_ip=? and webcamOutIn=?",
												accessIP, access.getOutInDoor());
								if (accessWebcamList != null && accessWebcamList.size() > 0) {
									AccessWebcam accessWebcam = accessWebcamList
											.get(0);
									if (accessWebcam != null) {
										// 摄像头不为空 (开门(摄像头IP))
										// 添加识别记录
										AccessRecords accessRecords = null;
										accessRecords = AccessRecordsServerImpl.createAccessRecordCar("验证码", sb.toString(), "来访", null, null, null, access.getOutInName(),access.getOutInDoor(), access.getEquipmentDaoType(), access.getEquipmentLocation(), accessWebcam.getId(),accessWebcam.getWebcamIP(), accessWebcam.getAeqt_id(),accessIP, access.getWaitCheck());
										if (accessRecords != null) {
											builder.append(",验证码" + sb.toString() + "记录添加成功．车验。凭证ID："+ access.getId());
										} else {
											builder.append(",验证码" + sb.toString() + "记录添加失败．车验。凭证ID："+ access.getId());
										}
										if ("进门".equals(access.getOutInDoor()) && access.getOutInDoor() != null) {
											if (AccessServerImpl.openDoor(accessWebcam.getWebcamIP())) {
												// 如果开门成功
												accessRecords.setRecordStatus("已开门");
												totalDao.update2(accessRecords);
												LedCarPush.ledShow(sb.toString(),accessWebcam.getWebcamOutIn(), 6);// 推送LED消息
											} else {
												builder.append("验证码" + sb.toString() + "开门失败!请检查网络.车验");
											}
										} else if ("出门".equals(access.getOutInDoor())) {
											LedCarPush.ledShow(sb.toString(), accessWebcam.getWebcamOutIn(), 9);// 推送LED消息
										}
									} else {
										builder.append("没有与设备对应功能的摄像头，请前往门禁设备管理添加．车验");
									}
								} else {
									builder.append("设备对应功能的摄像头为空，请前往门禁设备管理添加．车验List");
								}
							} else if ("人行道".equals(access
									.getEquipmentDaoType())) {
								// 先开门
								getsocket(socket, 1);
								// 如果开门成功
								// 添加开门记录
								if (AccessRecordsServerImpl.createAccessRecordCar("验证码", sb.toString(),"来访", null, null, null, access.getOutInName(), access.getOutInDoor(), access.getEquipmentDaoType(), access.getEquipmentLocation(), null, null, null, accessIP,access.getWaitCheck()) != null) {
									builder.append("验证码" + sb.toString() + "记录添加成功．人行道验。凭证ID："+ access.getId());
								} else {
									builder.append("验证码" + sb.toString() + "记录添加失败．人行道验。凭证ID："+ access.getId());
								}
							} else {
								System.out.println("就两个门，你想进哪个！！！");
								getsocket(socket, 0);
							}
						} else {
							System.out.println("没有进出权限,请先申请!");
							builder.append("没有进出权限,请先申请!");
							getsocket(socket, 0);
						}
					} else {
						System.out.println("来访凭证不存在,请先申请!");
						builder.append("来访凭证不存在,请先申请!");
						getsocket(socket, 0);
						LedCarPush.ledShow(sb.toString(), "", 11);// 推送LED消息
					}
				} else {
					System.out.println("接收验证码数据为空！请重新输入。");
					builder.append("接收验证码数据为空！请重新输入。");
					getsocket(socket, 0);
					LedCarPush.ledShow(sb.toString(), "", 12);// 推送LED消息
				}
				/********************************* 验证码测试已通过 *********************************/
				/********************************* 人行道确认后可已开门 *********************************/
				/********************************* 车行道确认后可已开门 *********************************/
				/********************************* 没网的情况下，线程会卡5秒，提示网络异常，开门失败 *********************************/
				/********************************* 验证码测试已通过 *********************************/
			} else if ("FE".equals(mes)) {
				// 根据IP重新获取触发摄像头
				// 先在记录表中查找到对应ip的最晚一条开门记录，获取到摄像头ip
				List<AccessRecords> accessRecordList = getAccessRecor(accessIP,"已开门");
				if (accessRecordList != null && accessRecordList.size() > 0) {
					AccessRecords accessRecords = accessRecordList.get(0);
					builder.append("FE id=" + accessRecords.getId() + "ip"
							+ accessRecords.getAsWeam_ip());
					// 等待2S再次触发
					try {
						Thread.sleep(2000);
						if (AccessServerImpl.RriggerCmd(accessRecords
								.getAsWeam_ip()) != null) {
							builder.append("触发成功");
						} else {
							builder.append("触发失败");
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("正常开门不可能没有!!!除非是手动开门的...");
				}
				LedCarPush.ledShow("", "", 7);// 推送LED消息
				/********************************* 验证码测试已通过 *********************************/
				/********************************* 人行道确认后可已开门 *********************************/
				/********************************* 车行道确认后可已开门 *********************************/
				/********************************* 没网的情况下，线程会卡5秒，提示网络异常，开门失败 *********************************/
				/********************************* 验证码测试已通过 *********************************/
			}else if ("00".equals(mes) && "人行道".equals(accessType)){
				jiaoshi(accessIP, socket);
				System.out.println("人行道服务端开始接受员工卡号！");
				String nowTime_1 = Util.getDateTime();// 获取当前时间
				String sk_date = Util.getDateTime("yyyy-MM-dd");// 获取当前日期
				String sk_date_one = Util.getSpecifiedDayAfter(sk_date, 1);// 获取当前日期的后一天
				String sk_Time_hour = Util.getDateTime("HH:mm:ss");// 获取当前时间
				Date nowdate = Util.StringToDate(sk_date, "yyyy-MM-dd");
				Date datenow_one = Util.StringToDate(sk_date_one, "yyyy-MM-dd");
				Date nowTime = new Date();
				Integer cardNumber = null;
				String cardId = "";

				byte[] yg_cardId = new byte[3];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据

				byte[] zong_code = new byte[yz_Biaoshi.length + yg_cardId.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < yz_Biaoshi.length) {
						zong_code[i] = yz_Biaoshi[i];
					} else {
						zong_code[i] = yg_cardId[i - yz_Biaoshi.length];
					}
				}
				String card = Util.byteArrayToHexStringK(zong_code).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
				System.out.println("sdasdasd：" + card);
				try {
					cardNumber = Integer.parseInt(card, 16);
					System.out.println("处理之后的cardNumber为：" + cardNumber);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);//得到十位卡号

				System.out.println("处理之后的卡号为：" + cardId);
				builder.append(",处理之后的卡号为：" + cardId);
				
				/******************************** 员工卡号流程开始 ************************************************/
				// 1、判断是否为内部（users表）
				List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
				if (userList != null && userList.size() > 0) {
					// 2是内部员工卡
					Users users = userList.get(0);
					username = users.getName();
					cardIds = cardId;
					// 3根据设备第5个值判断是进门还是出门
					byte[] men_data = new byte[1];// 再接收第5个字符
					bis.read(men_data);// 读取数据
					String mess = Util.byteArrayToHexString(men_data);
					System.out.println("进出标识: " + mess);
					builder.append(", 进出标识" + mess);
					boolean inOut = true;//默认进门
					if ("BB".equals(mess)) {// 出门
						inOutType = "出门";inOut=false;
					}else if ("CC".equals(mess)) {// 进门
						inOutType = "进门";
					}
					//判断是否为上班时间
					boolean b = false;//默认不添加
					BanCi banCi = null;
					if (users.getBanci_id()!=null&&users.getBanci_id()>0) {
						//查询特殊时间表
						List<SpecialDate> listSpecil = totalDao.query("from SpecialDate where banciId = ? and date = ?", users.getBanci_id(),Util.getDateTime("yyyy-MM-dd"));
						if (listSpecil!=null&&listSpecil.size()>0){
							if ("上班".equals(listSpecil.get(0).getSpecialType())) {
								b = isbanci(users.getBanci_id(),banCi); 
							}
						}else {
							b = isbanci(users.getBanci_id(),banCi); 
						}
					}
					accessId = accessEquipment.getId();
					// 4、根据进出门和设备IP去查找记录表
					List accessRecordList = totalDao.query("from AccessRecords where recordContents=? and recordStatus = '已通过' and addTime > ? order by id desc",cardId, Util.getDateTime("yyyy-MM-dd"));
					if (accessRecordList != null && accessRecordList.size() > 0) {
						// 5、有记录
						AccessRecords accessRecords = (AccessRecords) accessRecordList.get(0);
						if (accessRecords.getOpenType().equals(
								inOutType)) {
							// 6、状态相等 不能开门
							getsocket(socket, 0);
							System.out.println(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
							builder.append(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
						} else {
							// 6、不相等 判断是否为白名单
							if ("是".equals(users.getWhiteCard())) {
								// 7、为白名单
								// 开门
								getsocket(socket, 1);
								// 添加进出门记录
								if (b) {
									if (AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null, users.getBanci_id()) != null) {
										builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
									} else {
										builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
									}
								}else {
									if (AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null) != null) {
										builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
									} else {
										builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
									}
								}
							} else {
								// 7、不是白名单
								// 判断时间段 根据（进出）
								if ("进门".equals(inOutType)) {
									if (b) {
										// 8、判断时间段
										if (inWorkTime(banCi, sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id());
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
											} else {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
											}
										} else {
											// 不在时间段内对比（加班请假）表
											// 进门先查加班表
											List overTimelist = totalDao.query(overTimeStr, cardId,users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
											if (overTimelist != null && overTimelist.size() > 0) {
												// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,b);
											} else {
												// 进门加班没有，查找请假表
												List askForLeavelist = totalDao.query(askForLeaveStr, cardId, users.getName(),users.getCode(), sk_date, sk_date_one,sk_date, sk_date_one,nowTime_1,nowTime_1);
												if (askForLeavelist != null && askForLeavelist.size() > 0) {
													// 有请假申请流程
													byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist,b);
												} else {
													// 9、没有记录，不开门
													getsocket(socket, 0);
													builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
												}
											}
										}
									}else {
										if (Util.InMenTime(sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
											} else {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
											}
										} else {
											// 不在时间段内对比（加班请假）表
											// 9、没有记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName()+ "员工 ,您没有班次且当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
										}
									}
								} else if ("出门".equals(inOutType)) {
									if (b) {
										// 8、判断时间段
										if (outWorkTime(banCi, sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id());
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
											} else {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
											}
										} else {
											// 不在时间段内对比（加班请假）表
											// 出门先查请假表
											List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(),sk_date, sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
											if (askForLeavelist != null && askForLeavelist.size() > 0) {
												// 有请假申请流程
												byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist,b);
											} else {
												// 请假没有加班表
												List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
												if (overTimelist != null && overTimelist.size() > 0) {
													// 有加班申请流程
													byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,b);
												} else {
													// 9、没有记录，不开门
													getsocket(socket, 0);
													builder.append(users.getName() + "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
												}
											}
										}
									}else {
										// 8、判断时间段
										if (Util.outMenTime(sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
											} else {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
											}
										} else {
											getsocket(socket, 0);
											builder.append(users.getName() + "员工 ,您班次为空且，当前时间没有刷卡 " + inOutType + " 的权限哦。");
										}
									}
									
								} else {
									getsocket(socket, 0);
									builder.append(users.getName()+ "！！！我也不知道您是想进还是想出了！！！");
								}
							}
						}
					} else {
						// 5、没有记录
						// 6、不相等 判断是否为白名单
						if ("是".equals(users.getWhiteCard())) {
							// 7、为白名单 开门
							// 开门
							getsocket(socket, 1);
							// 添加进出门记录&考勤记录
							if (inOut&&b) {//首次刷卡为进门且有班次
								if (AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null, users.getBanci_id()) != null) {
									builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
								} else {
									builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
								}
							}else {
								if (AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null) != null) {
									builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
								} else {
									builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
								}
							}
						} else {
							// 7、不是白名单
							// 判断时间段 根据（进出）
							if ("进门".equals(inOutType)) {
								// 8、判断时间段
								if (b) {//有班次
									if (inWorkTime(banCi,sk_Time_hour)) {//根据班次判定当前时间是否可以进出
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										if (AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id()) != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段");
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
										}
									} else {
										// 不在时间段内对比（加班请假）表
										// 进门先查加班表
										List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
										if (overTimelist != null && overTimelist.size() > 0) {
											// 有加班申请流程
											byoverTime(socket, builder, users, totalDao, cardId,accessEquipment, accessIP, overTimelist, b);//新考勤加班
										}
										//首次进门不查询加班，没有加班记录不让进
//										else {
//											// 进门加班没有，查找请假表
//											List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(), sk_date,sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
//											if (askForLeavelist != null && askForLeavelist.size() > 0) {
//												// 有请假申请流程
//												byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist);
//											}
//										}
										else {
											// 9、没有记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 "+ inOutType+ " 的权限哦。请先申请。");
										}
									}
								}else{//没有班次的卡号
									if (Util.InMenTime(sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										if (AccessRecordsServerImpl.createAccessRecordCar("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null) != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段");
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
										}
									}else {
										getsocket(socket, 0);
										builder.append(users.getName()+ "员工 ,您没有班次，且当前时间没有刷卡 "+ inOutType+ " 的权限哦。");
									}
								}
							} else if ("出门".equals(inOutType)) {
								//判断是否有班次
								if (b) {
									// 8、判断时间段
									if (outWorkTime(banCi, sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										if (AccessRecordsServerImpl.createAccessRecordCar("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null) != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段）");
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
										}
									} else {
										// 不在时间段内对比（加班请假）表
										// 出门先查请假表
										List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(),sk_date, sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
										if (askForLeavelist != null && askForLeavelist.size() > 0) {
											// 有请假申请流程
											byAskForLeave(socket, builder, users, totalDao, cardId,accessEquipment, accessIP, askForLeavelist);
										} else {
											// 请假没有加班表
											List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
											if (overTimelist != null && overTimelist.size() > 0) {
												// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist);
											} else {
												// 9、没有记录，不开门
												getsocket(socket, 0);
												builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 "+ inOutType+ " 的权限哦。请先申请。");
											}
										}
									}
								}else {
									// 8、判断时间段
									if (Util.outMenTime(sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										if (AccessRecordsServerImpl.createAccessRecordCar("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null) != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段）");
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
										}
									} else {
										getsocket(socket, 0);
										builder.append(users.getName()+ "员工 ,您没有班次，当前时间没有刷卡 "+ inOutType+ " 的权限哦。");
									}
								}
							} else {
								getsocket(socket, 0);
								builder.append(users.getName() + "！！！我也不知道您是想进还是想出了！！！");
							}
						}
					}
				} else {
					// 6、没有查询到卡号
					getsocket(socket, 0);
					builder.append("不是内部卡，没有开门权限");
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表进门 *********************************/
				/********************************* 红外触发是由外向内 *********************************/
				/******************************************************************/
			}else if("0F".equals(mes)){
				String nowTime_1 = Util.getDateTime();// 获取当前时间
				String sk_date = Util.getDateTime("yyyy-MM-dd");// 获取当前日期
				String sk_date_one = Util.getSpecifiedDayAfter(sk_date, 1);// 获取当前日期的后一天
				String sk_Time_hour = Util.getDateTime("HH:mm:ss");// 获取当前时间
				Date nowdate = Util.StringToDate(sk_date, "yyyy-MM-dd");
				Date datenow_one = Util.StringToDate(sk_date_one, "yyyy-MM-dd");
				Date nowTime = new Date();
				byte[] yg_cardId = new byte[4];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据
				String card = Util.byteArrayToHexStringK(yg_cardId).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				Integer cardNumber = null;
				String cardId = "";
				try {
					cardNumber = Integer.parseInt(card, 16);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);//得到十位卡号
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append("处理之后的卡号为：" + cardId);
				
				/******************************** 员工卡号流程开始 ************************************************/
				// 1、判断是否为内部（users表）
				List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
				if (userList != null && userList.size() > 0) {
					// 2是内部员工卡
					Users users = userList.get(0);
					username = users.getName();
					cardIds = cardId;

					// 3根据设备IP判断是进门还是出门
					inOutType = accessEquipment.getEquipmentOutIn();
					accessId = accessEquipment.getId();
					boolean inOut = true;//默认进门
					if ("出门".equals(inOutType))// 出门
						inOut=false;
					//判断是否为上班时间
					boolean b = false;//当天是否为工作时间，默认为否
					boolean bc = false;//班次
					boolean b1 = false;//在班次中需要上班的情况
					boolean b2 = false;//不在班次中但是需要上班的情况
					boolean b3 = false;//在班次中但不需要上班的情况
					boolean b4 = false;//不在班次中也不需要上班的情况
					BanCi banCi = null;
					if (users.getBanci_id()!=null&&users.getBanci_id()>0) {
						//查询特殊时间表
						List<SpecialDate> listSpecil = totalDao.query("from SpecialDate where banciId = ? and date = ?", users.getBanci_id(),Util.getDateTime("yyyy-MM-dd"));
						if (listSpecil!=null&&listSpecil.size()>0){
							if ("上班".equals(listSpecil.get(0).getSpecialType())) {
								b = true;
								if (isbanci(users.getBanci_id(),banCi))//在班次内，特殊时间表中需要上班的日期
									b1 = true;//基本不可能
								else//不在班次内的日期，特殊时间表中需要上班的日期，汇总考勤
									b2 = true;//不查询加班表
							}else if("放假".equals(listSpecil.get(0).getSpecialType())){
								if (isbanci(users.getBanci_id(),banCi))//在班次中不需要上班的情况
									b3 = true;//可能
								else//不在班次中也不需要上班的情况，基本不可能
									b4 = true;//查询加班表
							}else {
								isbanci(users.getBanci_id(),banCi);//得到班次
							}
						}else {
							//b = isbanci(users.getBanci_id(),banCi); 
							List<BanCi> banCiList = totalDao.query("from BanCi where id = ?", users.getBanci_id());
							if (banCiList!=null&&banCiList.size()>0) {
								banCi = banCiList.get(0);
								String week = Util.getDateTime("E");// 当前星期几中文
								String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
								for (int l = 0; l < sbdate.length; l++) {
									if (week.equals(sbdate[l].trim())) {
										b = true;
									}
								}
							}
						}
						if (banCi!=null) //班次不为空
							bc = true;
					}
					// 4、根据进出门和设备IP去查找记录表
					List accessRecordList = totalDao.query("from AccessRecords where recordContents=? and recordStatus = '已通过' and addTime > ? order by id desc",cardId, Util.getDateTime("yyyy-MM-dd"));
					if (accessRecordList != null && accessRecordList.size() > 0) {
						// 5、有记录
						AccessRecords accessRecords = (AccessRecords) accessRecordList.get(0);
						if (accessRecords.getOpenType().equals(inOutType)) {
							// 6、状态相等 不能开门
							getsocket(socket, 0);
							System.out.println(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
							builder.append(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
						} else {
							// 6、不相等 判断是否为白名单
							if ("是".equals(users.getWhiteCard())) {
								// 7、为白名单
								// 开门
								getsocket(socket, 1);
								// 添加进出门记录
								AccessRecords accessRecords2 = null;
								if (b) {
									accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null, users.getBanci_id());
									if (accessRecords2 != null) {
										builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
									} else {
										builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
									}
									//此处需要查询请假加班记录、
									/**********************************************/
								}else {
									//不在上班时间进门，不生成汇总，但如果班次不为空，将查询是否有加班记录
									if (bc) {
										List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
										if (accessOverList!=null&&accessOverList.size()>0) {
											List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
											if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,true);
											}
										}
									}else {
										//没有班次的，或班次不存在的
										accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null);
										if (accessRecords2 != null) {
											builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
										} else {
											builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
										}
									}
								}
							} else {
								// 7、不是白名单
								// 判断时间段 根据（进出）
								if ("进门".equals(inOutType)) {
									if (b) {//在工作日期的
										// 8、判断时间段
										if (inWorkTime(banCi, sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id());
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												/************请假回门比较特殊的情况************/
												if (accessRecords.getEntityName() != null && "AskForLeave".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
												//如果迟到，查询今天是否有请假记录
												if (isNotlate(banCi)) {
													List accesslist = totalDao.query(askForLeaveAccess, cardId, nowTime_1, inOutType);
													if (accesslist != null && accesslist.size() > 0) {
														byAccessAskForLeave(socket, builder, users, totalDao, cardId, accessEquipment, accessIP, accesslist, b);
													}
												}
											} else {
												builder.append(users.getName() + "员工 "+ inOutType+ "记录  添加失败！(在时间段)");
											}
										} else {
											// 不在时间进段内(进门不查请假)   查询（加班）表
											// 只查加班表
											List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
											if (accessOverList!=null&&accessOverList.size()>0) {
												List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
												if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
													byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,b);
												}
											}
//												else {
//													// 没有加班申请，查找请假结束进门凭证
//													noOverForAsk(cardId, nowTime_1, inOutType, builder, users, accessEquipment, accessIP, b);
//												}
//											else {
//												noOverForAsk(cardId, nowTime_1, inOutType, builder, users, accessEquipment, accessIP, b);
//											}
										}
									}else {
										if (bc) {//有班次的员工，查询当天有无加班申请。不在工作时间不用查询请假记录
											List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
											if (accessOverList!=null&&accessOverList.size()>0) {
												List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
												if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
													byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,true);
												}
											}
										}else {//没有班次的员工
											if (Util.InMenTime(sk_Time_hour)) {
												// 9、在时间段内 开门 记录
												// 开门
												getsocket(socket, 1);
												// 添加进出门记录
												AccessRecords accessRecords2 = null;
												accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
												if (accessRecords2 != null)
													builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												else
													builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
											} else {
												// 不在时间段内对比（加班请假）表
												// 9、没有记录，不开门
												getsocket(socket, 0);
												builder.append(users.getName()+ "员工 ,您没有班次且当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
											}
										}
									}
								} else if ("出门".equals(inOutType)) {
									if (b) {
										// 8、判断时间段
										if (outWorkTime(banCi, sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id());
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												/************比较特殊的情况************/
												if (accessRecords.getEntityName() != null && "Overtime".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
													accessRecords2.setEntityId(accessRecords.getEntityId());
													accessRecords2.setEntityName(accessRecords.getEntityName());
													totalDao.update2(accessRecords2);
												}
											}else
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
										} else {
											// 不在时间段内对比（请假）表
											// 出门先查请假表
											List accesslist = totalDao.query(askForLeaveAccess, cardId, nowTime_1, inOutType);
											if (accesslist != null && accesslist.size() > 0) {
												byAccessAskForLeave(socket, builder, users, totalDao, cardId, accessEquipment, accessIP, accesslist, b);
											} else {
												// 在上班时间不能查加班表
												// 9、如果没有请假记录，不开门
												getsocket(socket, 0);
												builder.append(users.getName() + "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
											}
										}
									}else {
										if (bc) {
											List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
											if (accessOverList!=null&&accessOverList.size()>0) {
												List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
												if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
													byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,true);
												}
											}
										}else {
											// 8、判断时间段
											if (Util.outMenTime(sk_Time_hour)) {
												// 9、在时间段内 开门 记录
												// 开门
												getsocket(socket, 1);
												// 添加进出门记录
												AccessRecords accessRecords2 = null;
												accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
												if (accessRecords2 != null) {
													builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
												} else {
													builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
												}
											} else {
												getsocket(socket, 0);
												builder.append(users.getName() + "员工 ,您班次为空且，当前时间没有刷卡 " + inOutType + " 的权限哦。");
											}
										}
									}
									
								} else {
									getsocket(socket, 0);
									builder.append(users.getName()+ "！！！我也不知道您是想进还是想出了！！！");
								}
							}
						}
					} else {
						// 5、没有记录
						if ("是".equals(users.getWhiteCard())) {
							// 7、为白名单
							// 开门
							getsocket(socket, 1);
							// 添加进出门记录
							AccessRecords accessRecords2 = null;
							if (b) {
								if (inOut)
									accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null, users.getBanci_id());
								else
									accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null);
								if (accessRecords2 != null) {
									builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
								} else {
									builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
								}
								//此处需要查询请假加班记录、
								/**********************************************/
							}else {
								//不在上班时间进门，不生成汇总，但如果班次不为空，将查询是否有加班记录
								if (bc) {
									List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
									if (accessOverList!=null&&accessOverList.size()>0) {
										List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
										if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
											byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,true);
										}
									}
								}else {
									accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null);
									if (accessRecords2 != null) {
										builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
									} else {
										builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
									}
								}
							}
						} else {
							// 7、不是白名单
							// 判断时间段 根据（进出）
							if ("进门".equals(inOutType)) {
								if (b) {
									// 8、判断时间段
									if (inWorkTime(banCi, sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										AccessRecords accessRecords2 = null;
										accessRecords2 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null, users.getBanci_id());
										if (accessRecords2 != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
											//如果迟到，查询今天是否有请假记录
											if (isNotlate(banCi)) {
												List accesslist = totalDao.query(askForLeaveAccess, cardId, nowTime_1, inOutType);
												if (accesslist != null && accesslist.size() > 0) {
													byAccessAskForLeave(socket, builder, users, totalDao, cardId, accessEquipment, accessIP, accesslist, b);
												}
											}
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ "记录  添加失败！(在时间段)");
										}
									} else {
										// 不在时间进段内   查询（加班）表
										// 只查加班表
										List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
										if (accessOverList!=null&&accessOverList.size()>0) {
											List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
											if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,b);
											}
										}
									}
								}else {
									if (bc) {//查询当天有无加班申请
										List<Access> accessOverList = totalDao.query(overTimeAccess, cardId);
										if (accessOverList!=null&&accessOverList.size()>0) {
											List overTimelist = totalDao.query("from Overtime where id = ?", accessOverList.get(0).getEntityId());
											if (overTimelist != null && overTimelist.size() > 0) {// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist,true);
											}
										}
									}else {
										if (Util.InMenTime(sk_Time_hour)) {
											// 9、在时间段内 开门 记录
											// 开门
											getsocket(socket, 1);
											// 添加进出门记录
											AccessRecords accessRecords2 = null;
											accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
											if (accessRecords2 != null) {
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
											} else
												builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
										} else {
											// 不在时间段内对比（加班请假）表
											// 9、没有记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName()+ "员工 ,您没有班次且当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
										}
									}
								}
							} else if ("出门".equals(inOutType)) {
								if (b) {
									// 8、判断时间段
									if (outWorkTime(banCi, sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										AccessRecords accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
										if (accessRecords2 != null)
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
										else
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
									} else {
										// 不在时间段内对比（请假）表
										// 出门先查请假表
										List accesslist = totalDao.query(askForLeaveAccess, cardId, nowTime_1, inOutType);
										if (accesslist != null && accesslist.size() > 0) {
											byAccessAskForLeave(socket, builder, users, totalDao, cardId, accessEquipment, accessIP, accesslist, false);
										} else {
											// 在上班时间不能查加班表
											// 9、如果没有请假记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName() + "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
										}
									}
								}else {
									// 8、判断时间段
									if (Util.outMenTime(sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										AccessRecords accessRecords2 = null;
										accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
										if (accessRecords2 != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
										}
									} else {
										getsocket(socket, 0);
										builder.append(users.getName() + "员工 ,您班次为空且，当前时间没有刷卡 " + inOutType + " 的权限哦。");
									}
								}
								
							} else {
								getsocket(socket, 0);
								builder.append(users.getName()+ "！！！我也不知道您是想进还是想出了！！！");
							}
						}
					}
				} else {
					// 6、没有查询到卡号
					getsocket(socket, 0);
					builder.append("不是内部卡，没有开门权限");
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表进门 *********************************/
				/********************************* 红外触发是由外向内 *********************************/
				/******************************************************************/
			
			}
			else if("00".equals(mes) && "停车场".equals(accessType)){
				jiaoshi(accessIP, socket);
				System.out.println("服务端开始接受员工卡号！（停车场）");
				Integer cardNumber = null;
				String cardId = "";

				byte[] yg_cardId = new byte[3];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据

				byte[] zong_code = new byte[yz_Biaoshi.length + yg_cardId.length];
				for (int i = 0; i < zong_code.length; i++) {
					if (i < yz_Biaoshi.length) {
						zong_code[i] = yz_Biaoshi[i];
					} else {
						zong_code[i] = yg_cardId[i - yz_Biaoshi.length];
					}
				}
				String card = Util.byteArrayToHexStringK(zong_code).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
				System.out.println("sdasdasd：" + card);
				try {
					cardNumber = Integer.parseInt(card, 16);
					System.out.println("处理之后的cardNumber为：" + cardNumber);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);//得到十位卡号

				System.out.println("处理之后的卡号为：" + cardId);
				builder.append(",处理之后的卡号为：" + cardId);
				
				/******************************** 员工停车场流程开始 ************************************************/
				// 1、判断是否有车辆（InEmployeeCarInfor表）
				List<InEmployeeCarInfor> userList = totalDao.query("from InEmployeeCarInfor where carInCangType = '内部' and carType = '个人' and ncardId in (select cardId from Users where cardId = ? and onWork <> '离职')" , cardId);
				if (userList != null && userList.size() > 0) {
					if (userList.size() > 1) {//个人有多辆内部车
//						根据卡号查找今天进门已通过记录
						List accessRecordList = totalDao.query("from AccessRecords where addTime > ? and inmarkId=? and recordStatus = '已通过' order by id desc", Util.getDateTime("yyyy-MM-dd"),cardId);
						AccessRecords accessRecords = null;
						if (accessRecordList != null && accessRecordList.size() > 0) {
							accessRecords = (AccessRecords) accessRecordList.get(0);
						}else {
							List accessRecordList2 = totalDao.query("from AccessRecords where addTime > ? and inmarkId=? and recordStatus = '已通过' order by id desc", Util.getSpecifiedDayAfter(Util.getDateTime("yyyy-MM-dd"), -7),cardId);
							if (accessRecordList2 != null && accessRecordList2.size() > 0) {
								accessRecords = (AccessRecords) accessRecordList.get(0);
							}
						}
						if (accessRecords!=null) {
							//有记录，将车牌保存起来=》开门
							yanzheng = accessRecords.getRecordContents();//记录车牌
							username = accessRecords.getInName();
						}else {
							yanzheng = userList.get(0).getNplates();//默认为最后一条的车牌
							username = userList.get(0).getName();
						}
					}else{//只有一辆个人内部车
						// 2是内部员工卡
						InEmployeeCarInfor inEmployee = userList.get(0);
						username = inEmployee.getName();
						yanzheng = inEmployee.getNplates();
					}
					cardIds = cardId;
					// 3根据设备第5个值判断是进门还是出门
					byte[] men_data = new byte[1];// 再接收第5个字符
					bis.read(men_data);// 读取数据
					String mess = Util.byteArrayToHexString(men_data).toString();
					System.out.println("进出标识: " + mess);
					builder.append(", 进出标识" + mess);
					if ("BB".equals(mess)) {// 出门
						inOutType = "出门";
					}else if ("CC".equals(mess)) {// 进门
						inOutType = "进门";
					}
					accessId = accessEquipment.getId();
					// 4、根据车牌号去查找车辆状态表
					if (!"".equals(yanzheng)) {
						List carInOutTypeList = totalDao.query("from CarInOutType where carPai=? order by id desc",yanzheng);
						if (carInOutTypeList != null && carInOutTypeList.size() > 0) {
							CarInOutType carInOutType = (CarInOutType) carInOutTypeList.get(0);
							if (carInOutType!=null && "进门".equals(carInOutType.getCarInOut())) {
								getsocket(socket, 1);
								builder.append(",开门成功。发送1");
							}else {
								getsocket(socket, 0);
								builder.append("车辆大门状态有误，没有开门权限.发送0");
							}
						} else {
							getsocket(socket, 0);
							builder.append("无车辆状态，无法开门，发送0");
						}
					}else{
						getsocket(socket, 0);
						builder.append("本周内无个人车辆进出，无法开门，发送0");
					}
				} else {
					// 6、没有查询到卡号
					getsocket(socket, 0);
					builder.append("没有查询到卡号，没有开门权限，发送0");
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表进门 *********************************/
				/********************************* 红外触发是由外向内 *********************************/
				/******************************************************************/
			}
			else if("00".equals(mes) && "地桩".equals(accessType)){
				String nowTime_1 = Util.getDateTime();// 获取当前时间
				byte[] yg_cardId = new byte[3];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据
				String card = Util.byteArrayToHexStringK(yg_cardId).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				Integer cardNumber = null;
				String cardId = "";
				try {
					cardNumber = Integer.parseInt(card, 16);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);// 得到十位卡号
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append("处理之后的卡号为：" + cardId);
				// 判断权限
//				List<Users> userList = totalDao.query(
//						"from Users where cardId = ? and onWork <>'离职' ",
//						cardId);
				List list = totalDao.query("from GuardCard where cardId =?",
						cardId);
				if (list != null && list.size() > 0) {
					GuardCard guardCard = (GuardCard) list.get(0);
					// 是内部员工卡
					username = guardCard.getName();
					cardIds = cardId;
					// Send "01"
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));
					bw.write(1);
					bw.flush();
					in = socket.getInputStream();
					bis = new BufferedInputStream(in);
					byte[] readbis = new byte[1];// 开始接受AE/AF 升降标识
					bis.read(readbis);// 读取数据
					String identifier_up_down = Util.byteArrayToHexStringK(
							readbis).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格

					if ("AE".equals(identifier_up_down)) {
						System.out.println("上升" + identifier_up_down);
						AccessSwitch accessSwitch = new AccessSwitch();
						accessSwitch.setCode(cardId);
						accessSwitch.setName(username);
						accessSwitch.setAddTime(nowTime_1);
						accessSwitch.setType("关门");
						totalDao.save2(accessSwitch);
					} else if ("AF".equals(identifier_up_down)) {
						System.out.println("下降" + identifier_up_down);
						AccessSwitch accessSwitch = new AccessSwitch();
						accessSwitch.setCode(cardId);
						accessSwitch.setName(username);
						accessSwitch.setAddTime(nowTime_1);
						accessSwitch.setType("开门");
						totalDao.save2(accessSwitch);
					}
				}else{
					getsocket(socket, 2);
				}
			}
			else if("AD".equals(mes) && "地桩".equals(accessType)){
				// TODO Auto-generated catch block
//				String dz="DZ001";
//				List<AccessEquipment> accessEquipmentlist = totalDao.query("from AccessEquipment where equipmentNum=? ",dz);
//				if(accessEquipmentlist!=null){458965
//					AccessEquipment accessEquipment2=(AccessEquipment) accessEquipmentlist.get(0);
					yanzheng=accessEquipment.getAdminCardId();
					if(yanzheng==null||"".equals(yanzheng))
						yanzheng = "666666";
					int[] yz = Util.yanzhengintSz(yanzheng);
					ServerThreadCD.getsocket256wei(socket, yz[0], yz[1], yz[2]);// 发送三个两位十六进制的验证码
//				}
			}
			else if("00".equals(mes) && "智能水阀".equals(accessType)){
				// TODO Auto-generated catch block
				String nowTime_1 = Util.getDateTime();// 获取当前时间
				byte[] yg_cardId = new byte[3];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据
				String card = Util.byteArrayToHexStringK(yg_cardId).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				Integer cardNumber = null;
				String cardId = "";
				try {
					cardNumber = Integer.parseInt(card, 16);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);// 得到十位卡号
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append("处理之后的卡号为：" + cardId);
				// 判断权限
				List<Users> userList = totalDao.query(
						"from Users where cardId = ? and onWork <>'离职' ",
						cardId);
				if (userList != null && userList.size() > 0) {
					Users u = userList.get(0);
					byte[] zhuangtai = new byte[1];// 开始接受1位开关状态
					bis.read(zhuangtai);// 读取数据
					String zhuangtais = Util.byteArrayToHexStringK(zhuangtai).replaceAll(
							" ", "");// 十六进制转换为String类型并去掉空格
					if("AA".equals(zhuangtais)){//状态为打开
						getsocket_1(socket, 4);
						bis = new BufferedInputStream(in);
						byte[] shuiZhi = new byte[5];// 开始接受5位16进制的水流量
						bis.read(shuiZhi);// 读取数据
						Float zhi = 0f;
						StringBuffer buffer1 = new StringBuffer();
						for (int i = 0; i < shuiZhi.length; i++) {buffer1.append(shuiZhi[i]);}
						String shuiZhis = buffer1.toString();
							//Util.byteArrayToHexStringK(shuiZhi).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
						try {
							zhi = Float.valueOf(shuiZhis);
						} catch (Exception e) {
							//cardId = Util.yanNumber(10);
						}
						List<WaterUseRecording> wal = totalDao.query("from WaterUseRecording where status = '使用中' and accessEid = ? and userId = ?", accessId,u.getId());
						if(wal!=null&&wal.size()>0){
							WaterUseRecording re = wal.get(0);
							re.setSheng(zhi/10);
							re.setEndTime(nowTime_1);
							re.setStatus("结束");
							totalDao.update2(re);
							List<WaterRechargeZhu> wrz = totalDao.query("from WaterRechargeZhu where userId = ?", u.getId());
							if(wrz!=null&&wrz.size()>0){
								WaterRechargeZhu wrza = wrz.get(0);
								wrza.setSheng(wrza.getSheng()-(zhi/10));
								totalDao.update2(wrza);
							}
						}else {
							//管理员卡结束放水操作
						}
					}else if ("BB".equals(zhuangtais)) {//状态为关闭
						List<WaterRechargeZhu> wa = totalDao.query("from WaterRechargeZhu where userId = ? and sheng > 0", u.getId());
						if(wa!=null&&wa.size()>0){
							WaterRechargeZhu wa1 = wa.get(0);
							WaterUseRecording re = new WaterUseRecording();
							if(wa1.getSheng()>6000){
								re.setSendSheng(6000f);
							}else {
								re.setSendSheng(wa1.getSheng());
							}
							re.setAddTime(nowTime_1);
							re.setUserCardId(cardId);
							re.setUserCode(u.getCode());
							re.setUserDept(u.getDept());
							re.setUserId(u.getId());
							re.setUserName(u.getName());
							re.setStatus("使用中");
							re.setAccessEid(accessId);
							totalDao.save2(re);
							Float fl = re.getSendSheng()*10;
							Integer send = fl.intValue();
							getsocket_2(socket, 2, send+"");
						}
					}
				}else {
					getsocket(socket, 0);
				}
			}
			else if ("00".equals(mes)) {
				jiaoshi(accessIP, socket);
				System.out.println("人行道服务端开始接受员工卡号！");
				String nowTime_1 = Util.getDateTime();// 获取当前时间
				String sk_date = Util.getDateTime("yyyy-MM-dd");// 获取当前日期
				String sk_date_one = Util.getSpecifiedDayAfter(sk_date, 1);// 获取当前日期的后一天
				String sk_Time_hour = Util.getDateTime("HH:mm:ss");// 获取当前时间
				Date nowdate = Util.StringToDate(sk_date, "yyyy-MM-dd");
				Date datenow_one = Util.StringToDate(sk_date_one, "yyyy-MM-dd");
				Date nowTime = new Date();
				byte[] yg_cardId = new byte[4];// 开始接受4位16进制的卡号
				bis.read(yg_cardId);// 读取数据
				String card = Util.byteArrayToHexStringK(yg_cardId).replaceAll(
						" ", "");// 十六进制转换为String类型并去掉空格
				Integer cardNumber = null;
				String cardId = "";
				try {
					cardNumber = Integer.parseInt(card, 16);
					cardId = cardNumber.toString();
				} catch (Exception e) {
					cardId = Util.yanNumber(10);
				}
				cardId = Util.cardId(cardId);//得到十位卡号
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append("处理之后的卡号为：" + cardId);

				/******************************** 员工卡号流程开始 ************************************************/
				// 1、判断是否为内部（users表）
				List<Users> userList = totalDao.query("from Users where cardId = ? and onWork <>'离职' ",cardId);
				if (userList != null && userList.size() > 0) {
					// 2是内部员工卡
					Users users = userList.get(0);
					username = users.getName();
					cardIds = cardId;
					// 3根据设备IP判断是进门还是出门
					inOutType = accessEquipment.getEquipmentOutIn();
					accessId = accessEquipment.getId();
					// 4、根据进出门和设备IP去查找记录表
					List accessRecordList = totalDao.query("from AccessRecords where recordContents=? and recordStatus = '已通过' and addTime > ? order by id desc",cardId, Util.getDateTime("yyyy-MM-dd"));
					if (accessRecordList != null && accessRecordList.size() > 0) {
						// 5、有记录
						AccessRecords accessRecords = (AccessRecords) accessRecordList.get(0);
						if (accessRecords.getOpenType().equals(
								inOutType)) {
							// 6、状态相等 不能开门
							getsocket(socket, 0);
							System.out.println(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
							builder.append(users.getName() + " 您的 " + cardId + " 卡已 " + accessRecords.getOpenType() + " 无法开门！！！（内部员工卡）");
						} else {
							// 6、不相等 判断是否为白名单
							if ("是".equals(users.getWhiteCard())) {
								// 7、为白名单
								// 开门
								getsocket(socket, 1);
								// 添加进出门记录
								if (AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null) != null) {
									builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
								} else {
									builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
								}
							} else {
								// 7、不是白名单
								// 判断时间段 根据（进出）
								if ("进门".equals(inOutType)) {
									// 8、判断时间段
									if (Util.InMenTime(sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										AccessRecords accessRecords2 = null;
										accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
										if (accessRecords2 != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
											if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
												accessRecords2.setEntityId(accessRecords.getEntityId());
												accessRecords2.setEntityName(accessRecords.getEntityName());
												totalDao.update2(accessRecords2);
											}
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
										}
									} else {
										// 不在时间段内对比（加班请假）表
										// 进门先查加班表
										List overTimelist = totalDao.query(overTimeStr, cardId,users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
										if (overTimelist != null && overTimelist.size() > 0) {
											// 有加班申请流程
											byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist);
										} else {
											// 进门加班没有，查找请假表
											List askForLeavelist = totalDao.query(askForLeaveStr, cardId, users.getName(),users.getCode(), sk_date, sk_date_one,sk_date, sk_date_one,nowTime_1,nowTime_1);
											if (askForLeavelist != null && askForLeavelist.size() > 0) {
												// 有请假申请流程
												byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist);
											} else {
												// 9、没有记录，不开门
												getsocket(socket, 0);
												builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
											}
										}
									}
								} else if ("出门".equals(inOutType)) {
									// 8、判断时间段
									if (Util.outMenTime(sk_Time_hour)) {
										// 9、在时间段内 开门 记录
										// 开门
										getsocket(socket, 1);
										// 添加进出门记录
										AccessRecords accessRecords2 = null;
										accessRecords2 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null);
										if (accessRecords2 != null) {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！(在时间段)");
											if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
												accessRecords2.setEntityId(accessRecords.getEntityId());
												accessRecords2.setEntityName(accessRecords.getEntityName());
												totalDao.update2(accessRecords2);
											}
										} else {
											builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！(在时间段)");
										}
									} else {
										// 不在时间段内对比（加班请假）表
										// 出门先查请假表
										List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(),sk_date, sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
										if (askForLeavelist != null && askForLeavelist.size() > 0) {
											// 有请假申请流程
											byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist);
										} else {
											// 请假没有加班表
											List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
											if (overTimelist != null && overTimelist.size() > 0) {
												// 有加班申请流程
												byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist);
											} else {
												// 9、没有记录，不开门
												getsocket(socket, 0);
												builder.append(users.getName() + "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
											}
										}
									}
								} else {
									getsocket(socket, 0);
									builder.append(users.getName()+ "！！！我也不知道您是想进还是想出了！！！");
								}
							}
						}
					} else {
						// 5、没有记录
						// 6、不相等 判断是否为白名单
						if ("是".equals(users.getWhiteCard())) {
							// 7、为白名单 开门
							// 开门
							getsocket(socket, 1);
							// 添加进出门记录
							if (AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(), inOutType, accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null, accessEquipment.getId(), accessIP, null) != null) {
								builder.append(users.getName() + "员工 " + inOutType + " 添加成功！（白名单）");
							} else {
								builder.append(users.getName() + "员工 " + inOutType + " 添加失败！（白名单）");
							}
						} else {
							// 7、不是白名单
							// 判断时间段 根据（进出）
							if ("进门".equals(inOutType)) {
								// 8、判断时间段
								if (Util.InMenTime(sk_Time_hour)) {
									// 9、在时间段内 开门 记录
									// 开门
									getsocket(socket, 1);
									// 添加进出门记录
									if (AccessRecordsServerImpl.createAccessRecordCar("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null) != null) {
										builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段");
									} else {
										builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
									}
								} else {
									// 不在时间段内对比（加班请假）表
									// 进门先查加班表
									List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
									if (overTimelist != null && overTimelist.size() > 0) {
										// 有加班申请流程
										byoverTime(socket, builder, users, totalDao, cardId,accessEquipment, accessIP, overTimelist);
									} else {
										// 进门加班没有，查找请假表
										List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(), sk_date,sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
										if (askForLeavelist != null && askForLeavelist.size() > 0) {
											// 有请假申请流程
											byAskForLeave(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,askForLeavelist);
										} else {
											// 9、没有记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 "+ inOutType+ " 的权限哦。请先申请。");
										}
									}
								}
							} else if ("出门".equals(inOutType)) {
								// 8、判断时间段
								if (Util.outMenTime(sk_Time_hour)) {
									// 9、在时间段内 开门 记录
									// 开门
									getsocket(socket, 1);
									// 添加进出门记录
									if (AccessRecordsServerImpl.createAccessRecordCar("员工卡",cardId, "内部", users.getCode(), users.getId(), users.getDept(), users.getName(),inOutType,accessEquipment.getEquipmentDaoType(),accessEquipment.getEquipmentLocation(), null, null,accessEquipment.getId(), accessIP, null) != null) {
										builder.append(users.getName() + "员工 "+ inOutType+ " 添加成功！（在时间段）");
									} else {
										builder.append(users.getName() + "员工 "+ inOutType+ " 添加失败！（在时间段）");
									}
								} else {
									// 不在时间段内对比（加班请假）表
									// 出门先查请假表
									List askForLeavelist = totalDao.query(askForLeaveStr,cardId, users.getName(), users.getCode(),sk_date, sk_date_one, sk_date, sk_date_one,nowTime_1,nowTime_1);
									if (askForLeavelist != null && askForLeavelist.size() > 0) {
										// 有请假申请流程
										byAskForLeave(socket, builder, users, totalDao, cardId,accessEquipment, accessIP, askForLeavelist);
									} else {
										// 请假没有加班表
										List overTimelist = totalDao.query(overTimeStr,cardId, users.getName(), users.getCode(), nowdate,datenow_one, nowdate, datenow_one,nowTime,nowTime);
										if (overTimelist != null && overTimelist.size() > 0) {
											// 有加班申请流程
											byoverTime(socket, builder, users, totalDao,cardId, accessEquipment, accessIP,overTimelist);
										} else {
											// 9、没有记录，不开门
											getsocket(socket, 0);
											builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 "+ inOutType+ " 的权限哦。请先申请。");
										}
									}
								}
							} else {
								getsocket(socket, 0);
								builder.append(users.getName() + "！！！我也不知道您是想进还是想出了！！！");
							}
						}
					}
				} else {
					// 6、没有查询到卡号
					getsocket(socket, 0);
					builder.append("不是内部卡，没有开门权限");
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表进门 *********************************/
				/********************************* 红外触发是由外向内 *********************************/
				/******************************************************************/
			}
			else if ("CC".equals(mes)) {// 从外到内的回复
				// 处理接收到红外设备的信号。通过设备IP直接查找到对应开门记录的最后一条，设置为已通过。
				// 接收成功
				// 通过设备IP查找对应的记录表数据，将其设置为已通过
				String nowTime = Util.getDateTime();
				List<AccessRecords> accessRecordList = getAccessRecor(accessIP, "已开门");
				if (accessRecordList.size() > 0 && accessRecordList != null) {
					AccessRecords accessRecords = accessRecordList.get(0);
					accessRecords.setRecordStatus("已通过");
					accessRecords.setRecordPass("由外向内");
					accessRecords.setEnterTime(Util.getDateTime());
					totalDao.update2(accessRecords);
					builder.append("记录表Id:" + accessRecords.getId() + " 通过状态："
							+ accessRecords.getRecordStatus());
					if ("员工卡".equals(accessRecords.getRecordType())) {
						// 添加考勤记录
						try {
							String kao = DownloadServerImpl.AddDownLoads(accessRecords.getRecordContents(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType());
							String kao1 = AttendanceTowServerImpl.addAttendanceTow(accessRecords.getRecordContents(),accessRecords.getInCode(), accessRecords.getInName(), accessRecords.getInDept(), accessRecords.getInId(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType(),null);
							builder.append(",考勤添加：" + kao + ","+"考情2"+kao1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							builder.append("e:"+e);
						}

						Overtime overtime = null;
						// 回调请假加班状态
						if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
							// 有申请记录
							if ("Overtime".equals(accessRecords.getEntityName())) {
								List overtimelist = totalDao.query("from Overtime where id=?", accessRecords.getEntityId());
								if (overtimelist != null && overtimelist.size() > 0) {
									overtime = (Overtime) overtimelist.get(0);
//									if ("进门".equals(accessRecords.getOpenType())) {
//										if (overtime.getFilnalStartDate() == null)
//											overtime.setFilnalStartDate(Util.getDateTime());
//									} else if ("出门".equals(accessRecords.getOpenType())) {
//										if (overtime.getFilnalStartDate() != null) {
//											if (overtime.getFilnalEndDate()==null)//进门后首次出门
//												overtime.setFilnalEndDate(Util.getDateTime());
//											else {
//												if (nowTime.compareTo(overtime.getEndDate())<=0)
//													overtime.setFilnalEndDate(Util.getDateTime());
//												else {
//													if (Util.getWorkTime1(overtime.getEndDate(), overtime.getFilnalEndDate())>0)//如果申请加班结束时间大于实际结束时间，实际加班结束时间为申请时间
//														overtime.setFilnalEndDate(overtime.getEndDate());
//												}
//											}
//										}
//									}
									totalDao.update2(overtime);
									builder.append("加班" + accessRecords.getOpenType()+ "时间回调：" + overtime.getId() + "成功,");
								}
							}
//							else if ("AskForLeave".equals(accessRecords.getEntityName())) {
//								List askForLeavelist = totalDao.query("from AskForLeave where leaveId=?",accessRecords.getEntityId());
//								if (askForLeavelist != null&& askForLeavelist.size() > 0) {
//									askForLeave = (AskForLeave) askForLeavelist.get(0);
//									if ("出门".equals(accessRecords.getOpenType())) {
//										if (askForLeave.getExitTime() == null)
//											askForLeave.setExitTime(nowTime);
//									} else if ("进门".equals(accessRecords.getOpenType())) {
//										if (askForLeave.getExitTime() != null) {//实际出门时间不为空
//											if (askForLeave.getReturnTime() == null)//第一次回门
//												askForLeave.setReturnTime(nowTime);//直接赋值
//											else {//不是第一次进门
//												if (nowTime.compareTo(askForLeave.getLeaveEndDate())<=0)//如果当前时间小于等于请假结束时间
//													askForLeave.setReturnTime(nowTime);//当前时间为请假结束时间
//												else {//如果当前时间大于请假结束时间
//													if (askForLeave.getLeaveEndDate().compareTo(askForLeave.getReturnTime())>0)//当申请请假结束时间大于实际请假时间时，将实际请假时间赋值为申请结束时间。如果实际请假时间大于申请结束时间则不重新赋值。
//														askForLeave.setReturnTime(askForLeave.getLeaveEndDate());
//												}
//											}
//										}
//									}
//									totalDao.update2(askForLeave);
//									builder.append("请假" + accessRecords.getOpenType()+ "时间回调：" + askForLeave.getLeaveId()+ "成功,");
//								}
//							}
						}
					} else if ("验证码".equals(accessRecords.getRecordType())) {
						// 根据验证码查询对比表，查询对比表。
						List accesslist = totalDao.query(yanZAccess,accessRecords.getRecordContents(),accessRecords.getOpenType(), nowTime, nowTime);
						if (accesslist != null && accesslist.size() > 0) {
							Access access = (Access) accesslist.get(0);
							access.setUseSend("已失效");
							totalDao.update2(access);
							System.out.println(accessRecords.getInName()+ accessRecords.getRecordContents() + " 验证码已"+ accessRecords.getOpenType() + "(来访)");
							builder.append(accessRecords.getInName()+ accessRecords.getRecordContents() + " 验证码已"+ accessRecords.getOpenType() + "(来访)," + "凭证ID："+ access.getId());
							// 来访验证码的确认操作
							List list = totalDao.query("from Visit where id=?", access.getEntityId());
							if (list != null && list.size() > 0) {
								Visit visit = (Visit) list.get(0);
								if ("进门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已进门");// 进门后状态变为已进门，此时可以点击来访结束
								} else if ("出门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已出门");// 出门后状态为已出门，来访结束
								} else {
									visit.setVisit_laiStatus("不出不进");
								}
								totalDao.update2(visit);
								builder.append(accessRecords.getRecordContents()+ " 验证码已" + access.getOutInDoor() + "(来访)id"+ visit.getId());
							} else {
								System.out.println("不是验证码。");
							}
						} else {
							System.out.println(accessRecords.getInName() + accessRecords.getId()+ accessRecords.getRecordContents() + " 验证码 无效，数据异常" + " 为异常"+ accessRecords.getOpenType());
							builder.append(accessRecords.getInName() + accessRecords.getId()+ accessRecords.getRecordContents() + " 验证码 无效，数据异常" + " 异常为"+ accessRecords.getOpenType());
						}
					}
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表出门 *********************************/
				/********************************* 红外触发是由内向外 *********************************/
				/******************************************************************/
			} else if ("DD".equals(mes)) {// 从外到内的回复
				// 处理接收到红外设备的信号。通过设备IP直接查找到对应开门记录的最后一条，设置为已通过。
				// 接收成功
				// 通过设备IP查找对应的记录表数据，将其设置为已通过
				String nowTime = Util.getDateTime();
				List<AccessRecords> accessRecordList = getAccessRecor(accessIP, "已开门");
				if (accessRecordList.size() > 0 && accessRecordList != null) {
					AccessRecords accessRecords = accessRecordList.get(0);
					accessRecords.setRecordStatus("已通过");
					accessRecords.setRecordPass("由外向内");
					accessRecords.setEnterTime(Util.getDateTime());
					totalDao.update2(accessRecords);
					builder.append("记录表Id:" + accessRecords.getId() + " 通过状态：" + accessRecords.getRecordStatus());
					if ("员工卡".equals(accessRecords.getRecordType())) {
						// 添加考勤记录
						try {
							//String kao = DownloadServerImpl.AddDownLoads(accessRecords.getRecordContents(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType());
							String kao1 = AttendanceTowServerImpl.addAttendanceTow(accessRecords.getRecordContents(),accessRecords.getInCode(), accessRecords.getInName(), accessRecords.getInDept(), accessRecords.getInId(),"正常",accessEquipment.getEquipmentName(),accessRecords.getOpenType(),null);
							builder.append(","+"考情2:"+kao1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							builder.append("e:"+e);
						}
						// 回调请假加班状态
						boolean bl = false;
						Overtime overtime = null;
						AskForLeave askForLeave = null;
						if (accessRecords.getEntityName() != null && !"".equals(accessRecords.getEntityName()) && accessRecords.getEntityId() != null && accessRecords.getEntityId() > 0) {
							bl = true;
							// 有申请记录
							if ("Overtime".equals(accessRecords.getEntityName())) {
								List overtimelist = totalDao.query("from Overtime where id=?", accessRecords.getEntityId());
								if (overtimelist != null && overtimelist.size() > 0) {
									overtime = (Overtime) overtimelist.get(0);
//									if ("进门".equals(accessRecords.getOpenType())) {
//										if (overtime.getFilnalStartDate() == null)
//											overtime.setFilnalStartDate(Util.getDateTime());
//									} else if ("出门".equals(accessRecords.getOpenType())) {
//										if (overtime.getFilnalStartDate() != null) {
//											if (overtime.getFilnalEndDate()==null)//进门后首次出门
//												overtime.setFilnalEndDate(Util.getDateTime());
//											else {
//												if (nowTime.compareTo(overtime.getEndDate())<=0)
//													overtime.setFilnalEndDate(Util.getDateTime());
//												else {
//													if (Util.getWorkTime1(overtime.getEndDate(), overtime.getFilnalEndDate())>0)//如果申请加班结束时间大于实际结束时间，实际加班结束时间为申请时间
//														overtime.setFilnalEndDate(overtime.getEndDate());
//												}
//											}
//										}
//									}
//									totalDao.update2(overtime);
									builder.append("加班" + accessRecords.getOpenType()+ "时间回调：" + overtime.getId() + "成功,");
								}
							}
//							else if ("AskForLeave".equals(accessRecords.getEntityName())) {
//								List askForLeavelist = totalDao.query("from AskForLeave where leaveId=?",accessRecords.getEntityId());
//								if (askForLeavelist != null&& askForLeavelist.size() > 0) {
//									askForLeave = (AskForLeave) askForLeavelist.get(0);
//									if ("出门".equals(accessRecords.getOpenType())) {
//										if (askForLeave.getExitTime() == null)
//											askForLeave.setExitTime(nowTime);
//									} else if ("进门".equals(accessRecords.getOpenType())) {
//										if (askForLeave.getExitTime() != null) {//实际出门时间不为空
//											if (askForLeave.getReturnTime() == null)//第一次回门
//												askForLeave.setReturnTime(nowTime);//直接赋值
//											else {//不是第一次进门
//												if (nowTime.compareTo(askForLeave.getLeaveEndDate())<=0)//如果当前时间小于等于请假结束时间
//													askForLeave.setReturnTime(nowTime);//当前时间为请假结束时间
//												else {//如果当前时间大于请假结束时间
//													if (askForLeave.getLeaveEndDate().compareTo(askForLeave.getReturnTime())>0)//当申请请假结束时间大于实际请假时间时，将实际请假时间赋值为申请结束时间。如果实际请假时间大于申请结束时间则不重新赋值。
//														askForLeave.setReturnTime(askForLeave.getLeaveEndDate());
//												}
//											}
//										}
//									}
//									totalDao.update2(askForLeave);
//									builder.append("请假" + accessRecords.getOpenType()+ "时间回调：" + askForLeave.getLeaveId()+ "成功,");
//								}
//							}
						}
						//添加汇总记录
						if (accessRecords.getBanciId()!=null&&accessRecords.getBanciId()>0) {
							//查询是否有汇总记录
							List<BanCi> banCilist = totalDao.query("from BanCi where id = ?", accessRecords.getBanciId());
							if (banCilist!=null&&banCilist.size()>0&&banCilist.get(0).getFirsttime()!=null) {//班次信息
								BanCi banCi1 = banCilist.get(0);
								List<Attendance> attendanceList = totalDao.query("from Attendance where dateTime = ? and cardNo = ? and personName = ? and deptName = ?",
										nowTime.substring(0, 10),accessRecords.getRecordContents(),accessRecords.getInName(),accessRecords.getInDept()); 
								String addTimeMS = accessRecords.getAddTime().substring(11, 16);
								if (attendanceList!=null&&attendanceList.size()>0) {//有记录，更新
									Attendance attendance = attendanceList.get(0);
									if ("出门".equals(accessRecords.getOpenType())) {//计算时间差
										long l = 0;
										boolean isnotWX = false;//是否包含午休
										boolean workTime0 = true;//工作时间为0
										String lastTm = attendance.getOperationDate().substring(11, 16);
										if (lastTm.compareTo(banCi1.getFirsttime().substring(0, 5)) <= 0) {//进门时间小于等于上班开始时间
											lastTm = banCi1.getFirsttime().substring(0, 5);isnotWX = true;
										}else if (lastTm.compareTo(banCi1.getWxstarttime().substring(0, 5)) <= 0) {//进门时间小于等于午休开始时间
											isnotWX = true;
										}else if (lastTm.compareTo(banCi1.getWxendtime().substring(0, 5)) <= 0) {//进门时间小于等于午休结束时间
											lastTm = banCi1.getWxendtime().substring(0, 5);//时间为午休结束时间
										}else if (lastTm.compareTo(banCi1.getFinishtime().substring(0, 5)) <= 0) {//进门时间小于等于下班时间
										}else {
											workTime0 = false;
										}
										String outTimes = addTimeMS;
										if (banCi1.getFinishtime().substring(0, 5).compareTo(outTimes) <= 0) {//出门时间大于等于下班时间
											outTimes = banCi1.getFinishtime().substring(0, 5);
										}else if (banCi1.getWxendtime().substring(0, 5).compareTo(outTimes) < 0) {//出门时间大于午休结束时间
											attendance.setAttendanceStatus("早退");
										}else if (banCi1.getWxstarttime().substring(0, 5).compareTo(outTimes) <= 0) {//出门时间大于等于午休开始时间
											outTimes = banCi1.getWxstarttime().substring(0, 5);isnotWX = false;
											attendance.setAttendanceStatus("正常");
										}else if (banCi1.getFirsttime().substring(0, 5).compareTo(outTimes) < 0) {//出门时间大于等于上班开始时间
											isnotWX = false;
											attendance.setAttendanceStatus("早退");
										}else if (banCi1.getWxendtime().substring(0, 5).compareTo(outTimes) == 0) {
										}else if (banCi1.getFirsttime().substring(0, 5).compareTo(outTimes) == 0) {
											isnotWX = false;
										} else {
											workTime0 = false;
										}
										int i = 0;
										if(workTime0){//工作时间不为0
											// 实际请假时间endS-startS isnotWX为true 减去午休时间
											if (isnotWX)
												l = Util.getWorkTime2(lastTm, outTimes)-Util.getWorkTime2(banCi1.getWxstarttime(), banCi1.getWxendtime());
											else
												l = Util.getWorkTime2(lastTm, outTimes);//计算上次进门到出门的有效工作时间
											i = (int) (l/60000);
										}
										attendance.setWorkTime(attendance.getWorkTime()+i);
										attendance.setOperationDate(nowTime);
										attendance.setClosingDateTime(addTimeMS);
										totalDao.update2(attendance);
									}else if("进门".equals(accessRecords.getOpenType())){//不计算,只重新赋值进门时间
										/*************刷卡进门赋值时间，开车进门时查询是否有进门打卡记录*****************/
										boolean zaoTui = false;
										boolean sx = true;//shang
										if (addTimeMS.compareTo(banCi1.getWxstarttime().substring(0, 5)) <= 0) {//进门时间小于等于午休开始时间(上午进门)
											zaoTui = true;
										}else if (addTimeMS.compareTo(banCi1.getWxendtime().substring(0, 5)) <= 0) {//进门时间小于等于午休结束时间
										}else if (addTimeMS.compareTo(banCi1.getFinishtime().substring(0, 5)) <= 0) {//进门时间小于等于下班时间
											zaoTui = true;sx = false;
										}else {
										}
										
										Long earlyTime = Util.getWorkTime2(addTimeMS, banCi1.getFirsttime().substring(0, 5));
										if (zaoTui) {
											if("早退".equals(attendance.getAttendanceStatus())){
												if(sx){
													
												}else {
													
												}
											}
										}
										attendance.setOperationDate(nowTime);//此处再
										totalDao.update2(attendance);
									}
									builder.append(accessRecords.getInName()+accessRecords.getOpenType()+"时间更新成功");
								}else {//如果当天没有考勤记录则添加
									Attendance attendance = new Attendance();
									attendance.setUserId(accessRecords.getInId());
									attendance.setPersonName(accessRecords.getInName());
									attendance.setDeptName(accessRecords.getInDept());
									attendance.setCardNo(accessRecords.getRecordContents());
									attendance.setDateTime(accessRecords.getAddTime().substring(0, 10));
									attendance.setWorkDateTime(addTimeMS);
									attendance.setOperationDate(accessRecords.getAddTime());
									attendance.setTags("1");// 新算法标识
									attendance.setLateTime(0);//
									attendance.setEarlyTime(0);//
									attendance.setJiaBTime(0);//
									attendance.setQueqinTime(0);//
									attendance.setQijiaTime(0);//
									attendance.setWorkTime(0);//
									attendance.setWorkBiaoTime(banCi1.getGzTime().intValue());//
									attendance.setBanci_Id(banCi1.getId());//
									attendance.setBanci_Name(banCi1.getName());//
									Long l = Util.getWorkTime2(addTimeMS, banCi1.getFirsttime().substring(0, 5));
									if (l<0) {
										int i =  0;
										if (bl) {//有请假加班的
											if ("Overtime".equals(accessRecords.getEntityName())) {
												attendance.setMorningStatus("加班");
											}else if ("AskForLeave".equals(accessRecords.getEntityName())) {
												List askForLeavelist = totalDao.query("from AskForLeave where leaveId=?",accessRecords.getEntityId());
												if (askForLeavelist != null&& askForLeavelist.size() > 0) {
													askForLeave = (AskForLeave) askForLeavelist.get(0);
													attendance.setMorningStatus("请假");
													String shangbanTime = Util.getDateTime("yyyy-MM-dd")+" "+banCi1.getFirsttime();
													if(nowTime.compareTo(askForLeave.getLeaveStartDate())<=0){//如果请假时间开始时间晚于等于当前时间
														attendance.setLateTime(i);//迟到时长
													}else if (nowTime.compareTo(askForLeave.getLeaveEndDate())<=0) {//如果请假结束时间晚于等于当前时间
														if (askForLeave.getLeaveStartDate().compareTo(shangbanTime)>0)//请假开始时间早于等于上班时间
															attendance.setLateTime((int)(Util.getWorkTime1(askForLeave.getLeaveStartDate(), shangbanTime)/60000));
													}else if (askForLeave.getLeaveEndDate().compareTo(nowTime)<0) {//如果请假结束时间早于当前时间(请假时间不够，还得计算迟到时间)
														
													}
												}
											}else {
		  										attendance.setQueqinTime(480);//直接是缺勤
												attendance.setMorningStatus("缺勤");
											}
										}else {
											attendance.setMorningStatus("迟到");
											if (addTimeMS.compareTo(banCi1.getWxstarttime().substring(0, 5)) <= 0) {//进门时间小于等于午休开始时间
												i = (int) ((l*-1)/60000);
											}else if (addTimeMS.compareTo(banCi1.getWxendtime().substring(0, 5)) <= 0) {//进门时间小于等于午休结束时间
												i = (int) (Util.getWorkTime2(banCi1.getFirsttime(),banCi1.getWxstarttime())/60000);
											}else if (addTimeMS.compareTo(banCi1.getFinishtime().substring(0, 5)) <= 0) {//进门时间小于等于下班时间
												i = (int) (((l*-1)-Util.getWorkTime2(banCi1.getWxstarttime(),banCi1.getWxendtime()))/60000);
											}else {
												i = 0;
												attendance.setMorningStatus("缺勤");
												attendance.setQueqinTime(banCi1.getGzTime().intValue());
											}
	  										attendance.setLateTime(i);//迟到时长
										}
									}else {
										attendance.setMorningStatus("正常");
									}
									totalDao.save2(attendance);
									builder.append(accessRecords.getInName()+"首次"+accessRecords.getOpenType()+"汇总添加成功");
								}
							}
						}
					} else if ("验证码".equals(accessRecords.getRecordType())) {
						// 根据验证码查询对比表，查询对比表。
						List accesslist = totalDao.query(yanZAccess,accessRecords.getRecordContents(),accessRecords.getOpenType(), nowTime, nowTime);
						if (accesslist != null && accesslist.size() > 0) {
							Access access = (Access) accesslist.get(0);
							access.setUseSend("已失效");
							totalDao.update2(access);
							System.out.println(accessRecords.getInName()+ accessRecords.getRecordContents() + " 验证码已"+ accessRecords.getOpenType() + "(来访)");
							builder.append(accessRecords.getInName()+ accessRecords.getRecordContents() + " 验证码已"+ accessRecords.getOpenType() + "(来访)," + "凭证ID："+ access.getId());
							// 来访验证码的确认操作
							List list = totalDao.query("from Visit where id=?", access.getEntityId());
							if (list != null && list.size() > 0) {
								Visit visit = (Visit) list.get(0);
								if ("进门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已进门");// 进门后状态变为已进门，此时可以点击来访结束
								} else if ("出门".equals(access.getOutInDoor())) {
									visit.setVisit_laiStatus("已出门");// 出门后状态为已出门，来访结束
								} else {
									visit.setVisit_laiStatus("不出不进");
								}
								totalDao.update2(visit);
								builder.append(accessRecords.getRecordContents()+ " 验证码已" + access.getOutInDoor() + "(来访)id"+ visit.getId());
							} else {
								System.out.println("不是验证码。");
							}
						} else {
							System.out.println(accessRecords.getInName() + accessRecords.getId()+ accessRecords.getRecordContents() + " 验证码 无效，数据异常" + " 为异常"+ accessRecords.getOpenType());
							builder.append(accessRecords.getInName() + accessRecords.getId()+ accessRecords.getRecordContents() + " 验证码 无效，数据异常" + " 异常为"+ accessRecords.getOpenType());
						}
					}
				}
				/********************************* 红外测试已通过 *********************************/
				/********************************* 人通过后可改变状态 *********************************/
				/********************************* DD正常情况下代表出门 *********************************/
				/********************************* 红外触发是由内向外 *********************************/
				/******************************************************************/
			} else
			/**
			 * 收到B卡消息 1、对比是否为门卫卡表中的卡号 2、是的话，根据卡的属性判断功能（触发/检急（检车&紧急））
			 * 3、触发方式为先外后内。内部（出门检车）优先 遮蔽内部摄像头可重新触发外部的摄像头 4、检急
			 */
			if ("BB".equals(mes)) {
				// 接收到B卡消息通过
				// 设备IP直接查找到对应开门记录的最后一条，设置为已通过。
				// 接收成功
				// 通过设备IP查找对应的记录表数据，将其设置为已通过
				builder.append(",BB,检车");
				System.out.println("服务端开始接受员工卡号！");
				String sk_Time = Util.getDateTime("yyyy-MM-dd");// 获取当前日期
				byte[] yg_cardId = new byte[4];// 开始接受4位16进制卡号
				bis.read(yg_cardId);// 读取数据
				String card = Util.byteArrayToHexStringK(yg_cardId).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
				Integer cardNumber = null;
				try {
					cardNumber = Integer.parseInt(card, 16);
				} catch (Exception e) {
					cardNumber = -909192939;
				}
				String cardId = cardNumber.toString();
				int num = cardId.length();
				for (int i = num; i < 10; i++) {
					cardId = "0" + cardId;
				}
				System.out.println("处理之后的卡号为：" + cardId);
				builder.append(",卡号：" + cardId);
				/******************************** B卡流程开始 ************************************************/
				// 1、判断是否为内部（GuardCard门卫卡表）
				List list = totalDao.query("from GuardCard where cardId =?",
						cardId);
				if (list != null && list.size() > 0) {
					GuardCard guardCard = (GuardCard) list.get(0);
					if (guardCard != null) {
						// if ("触发".equals(guardCard.getCardType())) {
						// 设备IP得和门禁设备IP绑定起来
						// 查找记录表中的最后一条记录
						List list2 = totalDao.query(jianCheAceR,Util.getDateTime(-3));
						if (list2 != null && list2.size() > 0) {
							AccessRecords accessRecords = (AccessRecords) list2.get(0);
							builder.append(",最后一条记录：id=" + accessRecords.getId() + ","
									+ accessRecords.getRecordStatus());
							if ("已识别".equals(accessRecords.getRecordStatus())) {
								if ("进门".equals(accessRecords.getOpenType())) {
									builder.append(",进门");
									// 紧急流程
									List listUrgentAccess = totalDao
											.query("from UrgentAccess where useStruts='未使用'");
									if (listUrgentAccess != null && listUrgentAccess.size() > 0) {
										UrgentAccess urgentAccess = (UrgentAccess) listUrgentAccess
												.get(0);
										if (urgentAccess != null) {
											// 开门操作
											// 摄像头不为空 (开门(摄像头IP))
											//											
											if (AccessServerImpl.openDoor(accessRecords
													.getAsWeam_ip())) {
												// 如果开门成功
												accessRecords.setRecordStatus("已开门");
												accessRecords.setUrgentCar("紧急");
												totalDao.update2(accessRecords);
												builder.append("," + accessRecords.getOpenType()
														+ guardCard.getName() + "：" + cardId
														+ accessRecords.getUrgentCar());
												/**** 添加车辆进出类型表 *****************************/
												AccessRecordsServerImpl
														.createCarInOutType(accessRecords
																.getRecordContents());
												LedCarPush.ledShow(accessRecords
														.getRecordContents(), accessRecords
														.getOpenType(), 10);
												// 给紧急进门车辆生成出门凭证 （直接生成）
												Access access = new Access();
												access.setOutInName(accessRecords
														.getRecordContents());// 紧急开门车牌
												access.setYanzheng("车牌");
												access.setCarPai(accessRecords.getRecordContents());// 车牌
												access.setNextSend("已生成");
												access.setOutInDoor("出门");
												access.setEquipmentDaoType(accessRecords
														.getOutOfPosition());
												access.setEquipmentDaoType("车行道");
												access.setEntityId(urgentAccess.getId());
												access.setAddTime(Util.getDateTime());
												access.setEntityName("UrgentAccess");
												access.setUseSend("未使用");
												access.setVisitstime(sk_Time);
												// 两天
												access.setFailtime((Util.getSpecifiedDayAfter(
														sk_Time, 2)));// 失效日期
												access.setWaitCheck("紧急");// 紧急
												access.setEquipmentIP(accessRecords.getAsEqt_ip());// 开门设备IP

												List lista = totalDao
														.query(
																"from AccessWebcam where aeqt_ip=? and webcamOutIn = '出门' and webcamLocation=?",
																accessRecords.getAsEqt_ip(),
																accessRecords.getOutOfPosition());
												if (lista != null) {
													AccessWebcam accessWebcam1 = (AccessWebcam) lista
															.get(0);
													if (accessWebcam1 != null) {
														access.setAccess_EquIp(accessWebcam1
																.getWebcamIP());
													}
												}
												totalDao.save2(access);
												// 回调改变紧急申请
												urgentAccess.setUseStruts("已使用");
												urgentAccess.setUrgentCar(accessRecords
														.getRecordContents());
												urgentAccess.setUrgentCarInTime(Util.getDateTime());
												totalDao.update2(urgentAccess);

											} else {
												builder.append("," + "紧急开门失败!请检查网络.（车验）");
											}
										} else {
											builder.append("," + "没有紧急申请");
											// 触发摄像头
											// 暂时定为根据位置和进出门在、查找摄像头
											Chufa(builder);
										}
									} else {
										Chufa(builder);
									}
								} else if ("出门".equals(accessRecords.getOpenType())) {
									builder.append(",出门");
									// 待检流程
									if ("待检查".equals(accessRecords.getWaitCheck())) {
										if (AccessServerImpl.openDoor(accessRecords.getAsWeam_ip())) {
											// 如果开门成功
											accessRecords.setRecordStatus("已开门");
											accessRecords.setWaitCheck("已检查");
											accessRecords.setCheckName(guardCard.getName() + "："
													+ cardId);
											totalDao.update2(accessRecords);
											builder.append("," + accessRecords.getOpenType()
													+ guardCard.getName() + "：" + cardId
													+ accessRecords.getWaitCheck());
											LedCarPush.ledShow(accessRecords.getRecordContents(),
													accessRecords.getOpenType(), 1);
										} else {
											builder.append("," + "检车开门失败!请检查网络.（车验）");
										}
									} else {
										builder.append("," + "此车不用检查");
										// 暂时定为根据位置和进出门在、查找摄像头
										Chufa(builder);
									}
								} else {
									builder.append("," + "出门|进门？");
									// 暂时定为根据位置和进出门在、查找摄像头
									Chufa(builder);
								}
								// } else if ("待修改".equals(accessRecords 门卫刷卡改状态。
								// .getWaitCheck())) {
								// //将车辆状态表中的状态设为空字符串
								// builder.append(","+accessRecords
								// .getRecordType() + "此车状态不对，需修改");
								// List cartypeList =
								// totalDao.query("from CarInOutType where carPai = ?",
								// accessRecords.getRecordType());
								// if (cartypeList!=null&&cartypeList.size()>0) {
								// CarInOutType carInOutType = (CarInOutType) cartypeList.get(0);
								// carInOutType.setCarInOut("");
								// carInOutType.setUpdateTime(Util.getDateTime());
								// totalDao.update2(carInOutType);
								// }
								// // 没有车牌信息
								// Chufa(builder);
							} else {
								// 没有已识别的信息
								Chufa(builder);
							}
						} else {// 不为已识别
							// 触发摄像头
							// 设备IP得和门禁设备IP绑定起来
							// 暂时定为根据位置和进出门在、查找摄像头
							Chufa(builder);
						}
					}
				} else {
					builder.append("," + cardId + "此卡不为门卫卡。");
				}
			}else if("AF".equals(mes) && ("停车场".equals(accessType)||"人行道".equals(accessType))){
				bis = new BufferedInputStream(in);
				byte[] dw = new byte[12*800-1];
				bis.read(dw);//断网数据
				String dwInfor = Util.byteArrayToHexStringK(dw).replaceAll(" ", "");// 十六进制转换为String类型并去掉空格
				dwInfor += "AF";
				String [] allInfor = dwInfor.split(" AB CD EF ");
				for (int i = 0; i < allInfor.length; i++) {//循环处理得到的数据
					int aab = allInfor[i].indexOf("AF");
					if (aab >= 0) {//存在AF
						String oneInfor = allInfor[i].replaceAll(" AF ", "").replaceAll("AF ", "");//去除
						String[] everIn = oneInfor.split(" ");
						String date = Integer.parseInt(everIn[0], 16)+"";//解析得到日期
						String Time = Integer.parseInt(everIn[1], 16)+"";//解析得到小时
						String min = Integer.parseInt(everIn[2], 16)+"";//解析得到分钟
						String cardId = "";
						for (int j = 3; j < 7; j++){cardId += everIn[j];} 
						cardId = Integer.parseInt(cardId, 16)+"";//解析得到卡号
						String outIn = "";//进出门
						if ("CC".equals(everIn[7]))
							outIn = "进门";
						else if("BB".equals(everIn[7]))
							outIn = "出门";
						else
							outIn = "无";
						totalDao.save2(addAtten(accessType, cardId, date, Time, min, outIn));
					}
				}
				//添加进考勤数据表
				try {
					AttendanceTowServerImpl.addDaiAttendance(accessType,accessname,builder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					builder.append(e+"");
				}
				getsocket(socket, 2);//处理完成发2
			}
			else {
				getsocket(socket, 0);
				builder.append("标示错误，发送0");
			}
			System.out.println("服务端关闭 soko");
		} catch (Exception e) {
			e.printStackTrace();
			builder.append(", " + e.toString() + "异常。");
		} finally {
			try {
				if (bis != null) {bis.close();}
				if (socket != null) {
					if (in != null) {in.close();}
					socket.close(); // 关闭Socket
					SocketServersCar.clientcount--;
					System.out.println("服务端关闭,当前连接设备数量为:" + SocketServersCar.clientcount);
					DoorBangDingServerImpl.caeLogInfor(builder, cardIds, yanzheng, username, inOutType, accessname, accessId, accessIP);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // 关闭Socket输入流
		}
	}


	/**
	 * 无加班记录时查找请假记录
	 * @param cardId
	 * @param nowTime_1
	 * @param inOutType
	 * @param builder
	 * @param users
	 * @param accessEquipment
	 * @param accessIP
	 * @param b
	 * @throws IOException
	 */
	public void noOverForAsk(String cardId,String nowTime_1,String inOutType,StringBuffer builder,Users users,AccessEquipment accessEquipment, String accessIP,boolean b) throws IOException{
		// 没有加班申请，查找请假结束进门凭证
		List accesslist = totalDao.query(askForLeaveAccess, cardId, nowTime_1, inOutType);
		if (accesslist != null && accesslist.size() > 0) {
			byAccessAskForLeave(socket, builder, users, totalDao, cardId, accessEquipment, accessIP, accesslist, b);
		}else {
			// 9、没有记录，不开门
			getsocket(socket, 0);
			builder.append(users.getName()+ "员工 ,您当前时间没有刷卡 " + inOutType + " 的权限哦。请先申请。");
		}
	}
	
	/**
	 * 根据班次 得到可出门时间段
	 * @param banCi 班次
	 * @param time  当前时间
	 * @return
	 */
	public boolean outWorkTime(BanCi banCi, String time){
		boolean a0 = Util.compareTime(time, "HH:mm:ss", "00:00:00",
				"HH:mm:ss");
		boolean a1 = Util.compareTime(banCi.getFirsttime(), "HH:mm:ss", time,
				"HH:mm:ss");
		boolean a2 = Util.compareTime(time, "HH:mm:ss", banCi.getWxstarttime(),
				"HH:mm:ss");
		boolean a3 = Util.compareTime(banCi.getWxendtime(), "HH:mm:ss", time,
				"HH:mm:ss");
		boolean a4 = Util.compareTime(time, "HH:mm:ss", banCi.getFinishtime(),
				"HH:mm:ss");
		boolean a5 = Util.compareTime("23:59:59", "HH:mm:ss", time,
				"HH:mm:ss");
		boolean fan = (a0 && a1) || (a2 && a3) || (a4 && a5);
		return fan;
	}

	/**
	 * 根据班次 得到可进门时间段
	 * @param banCi 班次
	 * @param time  当前时间
	 * @return
	 */
	public boolean inWorkTime(BanCi banCi, String time){
		boolean a0 = Util.compareTime(time, "HH:mm:ss", Util.getminuteAfter(banCi.getFirsttime(), -6*30),
		"HH:mm:ss");//晚于上班时间的前3个小时
		boolean a1 = Util.compareTime(Util.getminuteAfter(banCi.getFinishtime(), 30), "HH:mm:ss", time,
				"HH:mm:ss");//早于下班后的半小时
		boolean fan = a0 && a1;
		return fan;
	}
	
	/**
	 * 当天非首次进门 迟到情况
	 * @param banCi
	 * @return
	 */
	public boolean isNotlate(BanCi banCi){
		String now = Util.getDateTime("HH:mm"+":00");
		if (banCi.getFirsttime().compareTo(now)<0&&now.compareTo(banCi.getWxstarttime())<0)
			return true;
		if (banCi.getWxendtime().compareTo(now)<0&&now.compareTo(banCi.getFinishtime())<0)
			return true;
		if (banCi.getFinishtime().compareTo(now)<0)
			return true;
		return false;
	}
	
	/**
	 * 当天首次进门 迟到情况
	 * @param banCi
	 * @return
	 */
	public boolean firstisNotlate(BanCi banCi){
		if (banCi.getSbdate().compareTo(Util.getDateTime("HH:mm"+":00"))<0)
			return true;
		return false;
	}
	
	/**
	 * 查询当前用户今天是否上班
	 * @param users
	 * @return
	 */
	public boolean isbanci(Integer banciId,BanCi banCi){
		boolean bool1 = false;
		List<BanCi> banCiList = totalDao.query("from BanCi where id = ?", banciId);
		if (banCiList!=null&&banCiList.size()>0) {
			banCi = banCiList.get(0);
			String week = Util.getDateTime("E");// 当前星期几中文
			String[] sbdate = banCi.getSbdate().split(",");// 上班日期(星期几);
			for (int l = 0; l < sbdate.length; l++) {
				if (week.equals(sbdate[l].trim())) {
					bool1 = true;
				}
			}
		}
		return bool1;
	}
	
	/**
	 * 启用校时功能
	 * @param ip
	 */
	public void jiaoshi (String ip, Socket socket) throws IOException {
		int i = totalDao.getCount("from AccessLogInfor where aceIp = ? and addTime > ?", ip,Util.getDateTime("yyyy-MM-dd"));
		if (i==0)//当前设备今天第一次连接时，发送校时
			getsocketbyte(socket, Util.nowIntTime());
	}
	
	/**
	 * 发送校时
	 * @param sockets
	 * @param by
	 * @throws IOException
	 */
	public static void getsocketbyte(Socket sockets, byte[] by)
			throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0x03);
		out.write(by);
		out.write(0x0A);
		out.flush();
	}
	
	/**
	 * 添加待考勤数据
	 * @param accessType 设备类型
	 * @param cardId 卡号
	 * @param date 日期
	 * @param Time 时
	 * @param min  分
	 * @param outIn 进出门
	 * @return
	 */
	private AttendanceDai addAtten(String accessType, String cardId, String date
			, String Time, String min, String outIn){
		AttendanceDai dai = new AttendanceDai();
		dai.setAccessType(accessType);
		dai.setAddTime(Util.getDateTime());
		dai.setCardId(cardId);//卡号
		dai.setDadate(Util.getDateTime("yyyy-MM")+" "+date);//打卡日期
		dai.setDatime((Time.length()>1 ? Time:"0"+Time) +":"+ (min.length()>1 ? min:"0"+min));//拼接时分
		dai.setInouttype(outIn);
		dai.setStatus("待处理");
		return dai;
	}
	
	/**
	 * 触发摄像头
	 * @param builder
	 */
	private void Chufa(StringBuffer builder) {
		List list_1 = totalDao
				.query("from AccessWebcam where webcamLocation='大门' order by id desc");
		// 触发摄像头
		if (list_1 != null && list_1.size() > 0) {
			for (int i = 0; i < list_1.size(); i++) {
				AccessWebcam accessWebcam = (AccessWebcam) list_1.get(i);
				if (AccessServerImpl.RriggerCmd(accessWebcam.getWebcamIP()) != null) {
					System.out.println(accessWebcam.getWebcamIP() + "触发成功");
					builder.append("," + accessWebcam.getWebcamIP() + "触发成功");
				} else {
					System.out.println(accessWebcam.getWebcamIP() + "触发失败");
					builder.append("," + accessWebcam.getWebcamIP() + "触发失败");
				}
			}
		}
	}

	// 通过设备IP查找对应的记录表中的最晚一条开门数据，
	public List<AccessRecords> getAccessRecor(String ip, String str) {
		return totalDao
				.query(
						"from AccessRecords where asEqt_ip=? and recordStatus =? and addTime >? order by id desc",
						ip, str, Util.getDateTime(-3));
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
		if (i == 1) {
			bw.flush();
		}
		bw.flush();
		bw.close();
	}
	/**
	 * 打开同时发送最大流量限额
	 * @param sockets
	 * @param i
	 * @throws IOException
	 */
	private static void getsocket_2(Socket sockets, Integer i, String yz) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
				.getOutputStream()));
		bw.write(i);// 发送不开门信号
		String[] stryz = Util.code_1(yz, 5).split("");
		char[] yzc = new char[5];
		for (int z = 1; z < stryz.length; z++) {
			int ic = Integer.parseInt(stryz[z]);
			char ch = (char) (ic);
			yzc[z-1] = ch;
		}
		bw.write(yzc);
		bw.flush();
		bw.close();
	}
	/**
	 * 不断开连接
	 * @author Li_Cong
	 * @param sockets
	 *            连接
	 * @param i
	 *            发送指令
	 * @throws IOException
	 *             异常抛出
	 */
	private static void getsocket_1(Socket sockets, Integer i) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
				.getOutputStream()));
		bw.write(i);// 发送不开门信号
		bw.flush();
	}

	/**
	 * 员工有请假申请流程
	 * @param sockets
	 * @param builder
	 * @param users
	 * @param totalDao
	 * @param cardId
	 * @param accessEquipment
	 * @param accessIP
	 * @param accesslist
	 * @param b
	 * @throws IOException
	 */
	private static void byAccessAskForLeave(Socket sockets, StringBuffer builder,Users users, TotalDao totalDao,
			String cardId,AccessEquipment accessEquipment, String accessIP,
			List<Access> accesslist, boolean b) throws IOException {
		// 9、 有记录 先开门
		getsocket(sockets, 1);
		// 添加进出门记录
		AccessRecords accessRecords1 = null;
		if (b) {
			accessRecords1 = AccessRecordsServerImpl.createAccessRecordCarKaoQin("员工卡", cardId,"内部", users.getCode(), 
					users.getId(), users.getDept(),users.getName(), accessEquipment.getEquipmentOutIn(),
					accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null,users.getBanci_id());
		}else {
			accessRecords1 = AccessRecordsServerImpl.createAccessRecordCar("员工卡", cardId,"内部", users.getCode(), 
					users.getId(), users.getDept(),users.getName(), accessEquipment.getEquipmentOutIn(),
					accessEquipment.getEquipmentDaoType(), accessEquipment.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null);
		}
		if (accessRecords1 != null) {
			// 将设置对比表进入状态为已使用
			Access access = (Access) accesslist.get(0);
			access.setUseSend("已失效");
			totalDao.update2(access);
			List askForLeavelist = totalDao.query("from AskForLeave where leaveId=?",access.getEntityId());
			if (askForLeavelist != null&& askForLeavelist.size() > 0) {
				AskForLeave askForLeave = (AskForLeave) askForLeavelist.get(0);
				if ("出门".equals(access.getOutInDoor()))
					askForLeave.setExitTime(Util.getDateTime());
				else if ("进门".equals(access.getOutInDoor())) {
					askForLeave.setReturnTime(Util.getDateTime());//回门时间直接赋值
					if (askForLeave.getExitTime() == null){//实际出门时间为空 表示先进门了 //将实际请假开始时间设置为申请时间 
						askForLeave.setExitTime(askForLeave.getLeaveStartDate());
						String sql1 = "from Access where cardId = ? and entityName = 'AskForLeave' and entityId = ? and useSend <> '已失效' and outInDoor = '出门'";
						List<Access> accesslist1 = totalDao.query(sql1, cardId,askForLeave.getLeaveId());
						if (accesslist1!=null) {//并将出门时间凭证作废
							accesslist1.get(0).setUseSend("已失效");
							totalDao.update2(accesslist1.get(0));
						}
					}
				}
				if (totalDao.update2(askForLeave)) {
					builder.append("请假" + accessRecords1.getOpenType()+ "时间回调：" + askForLeave.getLeaveId()+ "成功,");
				}else {
					builder.append("请假" + accessRecords1.getOpenType()+ "时间回调：" + askForLeave.getLeaveId()+ "失败,");
				}
				accessRecords1.setEntityName(askForLeave.getClass()
						.getSimpleName());
				accessRecords1.setEntityId(askForLeave.getLeaveId());
				totalDao.update2(accessRecords1);
			}
			builder.append(users.getName() + "员工 "
					+ accessEquipment.getEquipmentOutIn() + " 添加成功！（请假申请凭证）");
		} else {
			builder.append(users.getName() + "员工 "
					+ accessEquipment.getEquipmentOutIn() + " 添加失败！（请假申请凭证）");
		}
	}
	
	
	
	// 请假员工卡流程
	private static void byAskForLeave(Socket sockets, StringBuffer builder,
			Users users, TotalDao totalDao, String cardId,
			AccessEquipment accessEquipment, String accessIP,
			List overAskForLeave) throws IOException {
		// 9、 有记录 先开门
		getsocket(sockets, 1);
		// 添加进出门记录
		AccessRecords accessRecords1 = AccessRecordsServerImpl
				.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(),
						users.getId(), users.getDept(), users.getName(),
						accessEquipment.getEquipmentOutIn(), accessEquipment
								.getEquipmentDaoType(), accessEquipment
								.getEquipmentLocation(), null, null,
						accessEquipment.getId(), accessIP, null);
		if (accessRecords1 != null) {
			// 添加成功
			// 改变请假表状态
			AskForLeave askForLeave = (AskForLeave) overAskForLeave.get(0);
			if (askForLeave != null) {
				accessRecords1.setEntityName(askForLeave.getClass()
						.getSimpleName());
				accessRecords1.setEntityId(askForLeave.getLeaveId());
				totalDao.update2(accessRecords1);
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加成功！（请假申请）（回传时间成功）");
			} else {
				// 添加失败
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加失败！（请假申请）（回传时间失败）");
			}
		} else {
			// 9、添加失败
			builder.append("," + users.getName() + "员工 ,刷卡记录添加失败！ "
					+ accessEquipment.getEquipmentOutIn());
		}
	}

	// 加班员工卡流程
	private static void byoverTime(Socket sockets, StringBuffer builder,
			Users users, TotalDao totalDao, String cardId,
			AccessEquipment accessEquipment, String accessIP, List overTimelist)
			throws IOException {
		// 9、 有记录 先开门
		getsocket(sockets, 1);
		// 添加进出门记录
		AccessRecords accessRecords1 = AccessRecordsServerImpl
				.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(),
						users.getId(), users.getDept(), users.getName(),
						accessEquipment.getEquipmentOutIn(), accessEquipment
								.getEquipmentDaoType(), accessEquipment
								.getEquipmentLocation(), null, null,
						accessEquipment.getId(), accessIP, null);
		if (accessRecords1 != null) {
			// 添加成功
			// 改变加班表状态
			Overtime overtime = (Overtime) overTimelist.get(0);
			if (overtime != null) {
				accessRecords1.setEntityName(overtime.getClass()
						.getSimpleName());
				accessRecords1.setEntityId(overtime.getId());
				totalDao.update2(accessRecords1);
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加成功！（加班申请）（回传时间成功）");
			} else {
				// 添加失败
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加失败！（加班申请）（回传时间失败）");
			}
		} else {
			// 9、添加失败
			getsocket(sockets, 0);
			builder.append("," + users.getName() + "员工 ,刷卡记录添加失败！ "
					+ accessEquipment.getEquipmentOutIn());
		}
	}
	
	// 请假员工卡流程
	private static void byAskForLeave(Socket sockets, StringBuffer builder,
			Users users, TotalDao totalDao, String cardId,
			AccessEquipment accessEquipment, String accessIP,
			List overAskForLeave, boolean b) throws IOException {
		// 9、 有记录 先开门
		getsocket(sockets, 1);
		// 添加进出门记录
		AccessRecords accessRecords1 = null;
		if (b) {
			accessRecords1 = AccessRecordsServerImpl
			.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(),
					users.getId(), users.getDept(), users.getName(),
					accessEquipment.getEquipmentOutIn(), accessEquipment
							.getEquipmentDaoType(), accessEquipment
							.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null, users.getBanci_id());
		}else {
			accessRecords1 = AccessRecordsServerImpl
			.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(),
					users.getId(), users.getDept(), users.getName(),
					accessEquipment.getEquipmentOutIn(), accessEquipment
							.getEquipmentDaoType(), accessEquipment
							.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null);
		}
		if (accessRecords1 != null) {
			// 添加成功
			// 改变加班表状态
			AskForLeave askForLeave = (AskForLeave) overAskForLeave.get(0);
			if (askForLeave != null) {
				accessRecords1.setEntityName(askForLeave.getClass()
						.getSimpleName());
				accessRecords1.setEntityId(askForLeave.getLeaveId());
				totalDao.update2(accessRecords1);
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加成功！（加班申请）（回传时间成功）");
			} else {
				// 添加失败
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加失败！（加班申请）（回传时间失败）");
			}
		} else {
			// 9、添加失败
			builder.append("," + users.getName() + "员工 ,刷卡记录添加失败！ "
					+ accessEquipment.getEquipmentOutIn());
		}
	}

	// 加班员工卡流程
	private static void byoverTime(Socket sockets, StringBuffer builder,
			Users users, TotalDao totalDao, String cardId,
			AccessEquipment accessEquipment, String accessIP, List overTimelist, boolean b)
			throws IOException {
		// 9、 有记录 先开门
		getsocket(sockets, 1);
		// 添加进出门记录
		AccessRecords accessRecords1 = null;
		if (b) {
			accessRecords1 = AccessRecordsServerImpl
			.createAccessRecordCarKaoQin("员工卡", cardId, "内部", users.getCode(),
					users.getId(), users.getDept(), users.getName(),
					accessEquipment.getEquipmentOutIn(), accessEquipment
							.getEquipmentDaoType(), accessEquipment
							.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null, users.getBanci_id());
		}else {
			accessRecords1 = AccessRecordsServerImpl
			.createAccessRecordCar("员工卡", cardId, "内部", users.getCode(),
					users.getId(), users.getDept(), users.getName(),
					accessEquipment.getEquipmentOutIn(), accessEquipment
							.getEquipmentDaoType(), accessEquipment
							.getEquipmentLocation(), null, null,
					accessEquipment.getId(), accessIP, null);
		}
		if (accessRecords1 != null) {
			// 添加成功
			// 改变加班表状态
			Overtime overtime = (Overtime) overTimelist.get(0);
			if (overtime != null) {
				accessRecords1.setEntityName(overtime.getClass()
						.getSimpleName());
				accessRecords1.setEntityId(overtime.getId());
				totalDao.update2(accessRecords1);
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加成功！（加班申请）（回传时间成功）");
			} else {
				// 添加失败
				builder.append("," + users.getName() + "员工 "
						+ accessEquipment.getEquipmentOutIn()
						+ " 添加失败！（加班申请）（回传时间失败）");
			}
		} else {
			// 9、添加失败
			builder.append("," + users.getName() + "员工 ,刷卡记录添加失败！ "
					+ accessEquipment.getEquipmentOutIn());
		}
	}

	// 查询当天是否加班申请
	private String overTimeAccess = "from Access where cardId = ? and entityName = 'OverTime' and visitstime like '"+Util.getDateTime("yyyy-MM-dd")+"%'";
	// 查询加班表语句:当天有加班记录的，全天都能进出，或者是在开始时间和结束时间之间
	private String overTimeStr = "from Overtime where overtimeCardId = ? and overtimeName = ? and overtimeCode = ? and ((? < startDate and startDate < ?) or (? < endDate and endDate < ?) or (startDate < ? and ? < endDate)) and status in ('审批中','同意')";
	// 查询请假表语句
	private String askForLeaveAccess = "from Access where cardId = ? and entityName = 'AskForLeave' and failtime > ? and useSend <> '已失效' and outInDoor = ?";
	// 查询请假表语句:当天有请假记录的，全天都能进出
	private String askForLeaveStr = "from AskForLeave where leaveUserCardId = ? and leavePerson = ? and leavePersonCode = ? and ((? < leaveStartDate and leaveStartDate < ?) or (? < leaveEndDate and leaveEndDate < ?) or (leaveStartDate < ? and ? < leaveEndDate)) and approvalStatus in ('审批中','同意')";
	// 根据验证码查询Access表
	private String yanZAccess = "from Access where yanzhengnum=? and outInDoor=? and failtime > ? and visitstime < ? and useSend <> '已失效' order by id desc";
	//查询门禁记录表中的最后一条记录(查询三分钟以内的数据)
	private String jianCheAceR = "from AccessRecords where addtime>? and recordType <> '员工卡' order by id desc";
}