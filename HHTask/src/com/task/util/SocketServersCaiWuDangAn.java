package com.task.util;

import com.task.Dao.TotalDao;
import com.task.entity.Price;
import com.task.entity.caiwu.AccountCheck;
import com.task.entity.caiwu.receivePayment.Receipt;
import com.task.entity.caiwu.receivePayment.ReceiptLog;
import com.task.entity.dangan.ArchiveUnarchiverAplt;
import com.task.entity.dangan.DangAnBank;
import com.task.entity.menjin.AccessEquipment;
import com.task.entity.menjin.AccessLogInfor;
import com.task.entity.menjin.AccessWebcam;
import com.task.entity.seal.SealLog;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;

public class SocketServersCaiWuDangAn extends Thread {

	public static final int PORT = 8871;
	public static int clientcount = 0;
	public TotalDao toalDao;

	public SocketServersCaiWuDangAn(TotalDao toalDao) {
		this.toalDao = toalDao;
	}
 
	// public static void startServer() {
	public void run() {
		try {
			// int clientcount = 0; // 统计客户端总数
			boolean listening = true; // 是否对客户端进行监听
			ServerSocket server = null; // 服务器端Socket对象

			try {
				// 创建一个ServerSocket在端口8870监听客户请求
				server = new ServerSocket(PORT);
				System.out.println("CaiWuDangAn ServerSocket starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}

			while (listening) {
				// 客户端计数
				clientcount++;
				// 监听到客户请求,根据得到的Socket对象和客户计数创建服务线程,并启动之
				new ServerThreadCwDa(server.accept(), clientcount, toalDao)
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
class ServerThreadCwDa extends Thread {
	private static int number = 0; // 保存本进程的客户计数
	Socket socket = null; // 保存与本线程相关的Socket对象
	TotalDao totalDao;

	public ServerThreadCwDa(Socket socket, int clientnum, TotalDao toalDao) {
		this.socket = socket;
		number = clientnum;
		this.totalDao = toalDao;
		System.out.println("当前在线的CaiWuDangAn档案柜数: " + number);
	}

	public void run() {
		BufferedInputStream bis = null;
		BufferedInputStream bis1 = null;
		InputStream in = null;
		StringBuilder builder = new StringBuilder();
		String accessIP = "";// 门禁IP
		String yanzheng = "";// 验证码
		AccessEquipment accessEquipment = null;
		String nowDateTime = Util.getDateTime();// 系统当前时间
		try {
			accessIP = socket.getInetAddress().getHostAddress();
			System.out.println(accessIP + " 进入服务端了");
			/**
			 * 第一步： 根据接收到IP或SIM标识去查找门禁设备
			 */
			String accessType = "";// 门禁类型
			List acElist = totalDao.query("from AccessEquipment where equipmentIP=? order by id desc",accessIP);
			if (acElist != null && acElist.size() > 0) {
				accessEquipment = (AccessEquipment) acElist.get(0);
				accessType = accessEquipment.getEquipmentDaoType();
			}
			if ("档案柜".equals(accessType)) {
				if(!"已设置".equals(accessEquipment.getAdminStatus())&&accessEquipment.getAdminCardId()!=null){
					xiafa(accessEquipment);
					builder.append("初始，下发二维码："+accessEquipment.getAdminCardId());
				}
			}
			// 由Socket对象得到输入流,并构造相应的BufferedReader对象
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);
			// 先接收接收第一个字符 用做标识
			System.out.println("服务端开始接受标识！");
			byte[] first = new byte[1];
			bis.read(first);
			String ff = Util.byteArrayToHexStringK(first).replaceAll(" ","");// 十六进制转换为String类型并去掉空格
			System.out.println("返回结果：" + ff);
			if ("FF".equals(ff)) {
				xiafa(accessEquipment);
				builder.append(" 接收：" + ff + " 下发二维码："+accessEquipment.getAdminCardId());
			}else {
				//接收二维码
				byte[] second = new byte[9];
				bis.read(second);
				//创建一个first+second长度的result空数组
				byte[] qrCode =new byte[first.length+second.length];
				//将first数组填充到result中
				System.arraycopy(first,0,qrCode,0,first.length);
				//将second数组填充到result中
				System.arraycopy(second,0,qrCode,first.length,second.length);
				//将二维码信息转成字符串
				yanzheng = Util.byteArrayToString(qrCode).toString();
				System.out.println(accessIP+"接收二维码信息: " + yanzheng);
				builder.append(" 二维码信息：" + yanzheng);
				
				builder.append(" 门禁设备：" + accessType);
				if ("档案柜".equals(accessType)) {
					// 不判断存档&取档
					// 查询ArchiveUnarchiverAplt表中有无申请记录
					List<ArchiveUnarchiverAplt> aplt = totalDao
							.query("from ArchiveUnarchiverAplt where inCodes = ? and ace_Ip = ? and shixiaoTime > ? and daGuiposition is not null and daGuiposition <> '' and useType = '未使用' order by id desc",
									 yanzheng, accessIP, nowDateTime);//
					if (aplt != null && aplt.size() > 0) {
						ArchiveUnarchiverAplt aplt1 = aplt.get(0);
						getsocketNoClose(socket, Integer.parseInt(aplt1.getDaGuiposition(), 16));//发送柜号
	//					socket.setSoTimeout(15);
						bis1 = new BufferedInputStream(in);// 等待接收返回值
						byte[] bankZhi = new byte[1];
						// EE:正确开柜
						bis1.read(bankZhi);
						String bank_zhi = Util.byteArrayToHexStringK(bankZhi).replaceAll(" ","");// 十六进制转换为String类型并去掉空格
						System.out.println("返回结果：" + bank_zhi);
						if ("EE".equals(bank_zhi)) {
							// 已开柜，将状态改为已使用 在aplt中除去
							aplt1.setUseType("已使用");
							totalDao.update2(aplt1);
							builder.append(" 开柜成功！返回：" + bank_zhi +"凭证ID："+aplt1.getId());

							//生成对账单
							if ("保险柜钥匙".equals(aplt1.getDaName()) || "U盾".equals(aplt1.getDaName())) {
								if ("取档".equals(aplt1.getCqType())) {
									//检查是否已有对账单
									Receipt receipt = (Receipt) totalDao.getObjectById(Receipt.class, aplt1.getReceipId());
									Set<ReceiptLog> r = receipt.getReceiptLogSet();
									ReceiptLog receiptLog = new ReceiptLog();
									for (ReceiptLog rLog : r) {
										receiptLog = rLog;
									}
									AccountCheck accountCheck = new AccountCheck();
									accountCheck.setReceiptLogid(receiptLog.getId());
									accountCheck.setAccountUnit(receipt.getPayee());
									accountCheck.setAllMoney(receipt.getAllMoney());
									accountCheck.setAccountCategory("付款");
									accountCheck.setAccountNumber("未上传");
									accountCheck.setAccountUsers(receiptLog.getApplyUserName());
									accountCheck.setAccountDescription(receipt.getSummary());
									accountCheck.setState(receiptLog.getStatus());
									accountCheck.setAddTime(Util.getDateTime("yyyy-MM-dd"));
									totalDao.save(accountCheck);
								}
							}

							/***********
							 * 存取档之后的操作 将price表中的状态改变， 将ArchiveUnarchiverAplt表中相同柜号的记录设置为已使用
							 ***********/
							//updatePrice(nowDateTime, aplt1, totalDao, builder);
							builder.append(" aplt2.getDaId()="+aplt1.getDaId());
							if(aplt1.getDaId()!=null&&aplt1.getDaId()>0){
								List<Price> pricel = totalDao.query("from Price where id = ?", aplt1.getDaId()); 
								if(pricel!=null&&pricel.size()>0){
									Price price = pricel.get(0);
									price.setIsGuiDang("yes");
									price.setCunStatus("已存档");
									price.setDanganCunQuStatus("已存");
									price.setIsCunTime(nowDateTime);
									totalDao.update2(price);
									builder.append(" price.Id="+price.getId()+"保存："+totalDao.update2(price));
									List<SealLog> seall = totalDao.query("from SealLog where documentId = ?", price.getId()); 
									if(seall!=null&&seall.size()>0){
										SealLog seal = seall.get(0);
										seal.setFujian2Status("已存档");
										seal.setIsCunTime(nowDateTime);
										totalDao.update2(seal);
										builder.append(" seal.Id="+seal.getId()+"保存"+totalDao.update2(seal));
									}
								}
							}
							List<AccessWebcam> accessWebcam = totalDao.query("from AccessWebcam where id = ?", aplt1.getDaGuiId());
							if(accessWebcam!=null&&accessWebcam.size()>0){
								AccessWebcam accessWebcam2 = accessWebcam.get(0);
								accessWebcam2.setActualNum(accessWebcam2.getActualNum()==null?0:accessWebcam2.getActualNum()+1);
								totalDao.update2(accessWebcam2);
							}
						}else {
							builder.append(" 开柜信息返回异常：" + bank_zhi +"凭证ID："+aplt1.getId());
						}
						// 流程结束，页面返回主界面
					} else {
						// 验证码无效
						getsocket(socket, 1);
					}
				}else {
					// 无操作
					System.out.println("档案标识不正确。。。");
					getsocket(socket, 1);
				}
			}
			System.out.println("服务端关闭 soko");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {bis.close();}
				if (bis1 != null) {bis1.close();}
				if (in != null) {in.close();}
				if (socket != null) {
					socket.close(); // 关闭Socket
					SocketServersCaiWuDangAn.clientcount--;

					System.out.println("服务端关闭,当前连接设备数量为:"
							+ SocketServersCaiWuDangAn.clientcount);
					AccessLogInfor accessLogInfor = new AccessLogInfor();
					accessLogInfor.setYanzheng(yanzheng);
					accessLogInfor.setInfor(builder.toString());
					accessLogInfor.setAceIp(accessIP);// IP
					accessLogInfor.setCardId("dangan");//
					accessLogInfor.setAddTime(Util.getDateTime());
					totalDao.save2(accessLogInfor);
					System.out.println("++" + builder.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // 关闭Socket输入流
		}
	}

	private void xiafa(AccessEquipment accessEquipment) throws IOException, InterruptedException {
		getsocketNoClose(socket, accessEquipment.getAdminCardId());
		accessEquipment.setAdminStatus("已设置");
		totalDao.update2(accessEquipment);
		Thread.sleep(200);
	}

	/**
	 * 添加再次开门记录
	 * @param accessIP
	 * @param cardId
	 * @param aplt2
	 */
	@SuppressWarnings("unused")
	private void addDangAnBank(String accessIP, String cardId,
			ArchiveUnarchiverAplt aplt2) {
		DangAnBank anBank = new DangAnBank();
		anBank.setAddTime(Util.getDateTime());
		anBank.setCardId(cardId);
		anBank.setDaIp(accessIP);
		anBank.setDaNum(aplt2.getDaGuiposition());
		anBank.setUseStatus("未使用");
		totalDao.save2(anBank);
	}

	/**
	 * 存取档之后的操作 将price表中的状态改变
	 * @author licong
	 * @param nowDateTime
	 * @param aplt2
	 */
	private static void updatePrice(String nowDateTime, ArchiveUnarchiverAplt aplt1,TotalDao totalDao, StringBuilder builder) {
		builder.append(" aplt2.getDaId()="+aplt1.getDaId());
		if(aplt1.getDaId()!=null&&aplt1.getDaId()>0){
			List<Price> pricel = totalDao.query("from Price where id = ?", aplt1.getDaId()); 
			if(pricel!=null&&pricel.size()>0){
				Price price = pricel.get(0);
				price.setIsGuiDang("yes");
				price.setCunStatus("已存档");
				price.setDanganCunQuStatus("已存");
				price.setIsCunTime(nowDateTime);
				totalDao.update2(price);
				builder.append(" price.Id="+price.getId()+"保存："+totalDao.update2(price));
				List<SealLog> seall = totalDao.query("from SealLog where documentId = ?", price.getId()); 
				if(seall!=null&&seall.size()>0){
					SealLog seal = seall.get(0);
					seal.setFujian2Status("已存档");
					seal.setIsCunTime(nowDateTime);
					totalDao.update2(seal);
					builder.append(" seal.Id="+seal.getId()+"保存"+totalDao.update2(seal));
				}
			}
		}
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
		bw.write(i);// 发送信号
		// bw.write(new char[] {01});
		bw.flush();
		bw.close();
		
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
	private static void getsocketNoClose(Socket sockets, Integer i)
			throws IOException {
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
//				.getOutputStream()));
//		bw.write(i);// 发送信号
//		bw.flush();
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(i);
		out.flush();
		
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
	private static void getsocketNoClose(Socket sockets, String ss)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
				.getOutputStream()));
		bw.write(ss);// 发送信号
		bw.flush();
		// PrintStream out = new PrintStream(sockets.getOutputStream());
		// out.write(ss);
		// out.flush();

	}

	/**
	 * 将验证码存入一个char数组 发送
	 * @param sockets
	 * @param yz
	 * @throws IOException
	 */
	public static void getsocketChar(Socket sockets, char[] yz)throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sockets
				.getOutputStream()));
		bw.write(0x03);// 发送信号
		bw.write(yz);// 发送信号
		bw.write(0x0A);// 发送信号
		bw.flush();
	}
	
	/**
	 * 发送三个两位十六进制的验证码
	 * @param sockets
	 * @param i
	 * @param i1
	 * @param i2
	 * @throws IOException
	 */
	public static void getsocket256wei(Socket sockets, int i, int i1, int i2)
			throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0x03);
		out.write(i);
		out.write(i1);
		out.write(i2);
		out.write(0x0A);
		out.flush();
	}
	
	/**
	 * 发送12位十六进制的开门编码
	 * @param sockets
	 * @throws IOException
	 */
	public static void getsocket12wei(Socket sockets, byte[] b)
	throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0xAF);
		out.write(b);
		out.flush();
	}
	
	/**
	 * 发送四个两位十六进制的验证码  adminCardId
	 * @param sockets
	 * @param i
	 * @param i1
	 * @param i2
	 * @param i3
	 * @throws IOException
	 */
	public static void getsocket256wei(Socket sockets, int i, int i1, int i2,
			int i3) throws IOException {
		PrintStream out = new PrintStream(sockets.getOutputStream());
		out.write(0x03);
		out.write(i);
		out.write(i1);
		out.write(i2);
		out.write(i3);
		out.write(0x0A);
		out.flush();
	}
	
	/**
	 * 将验证码存入一个byte数组 发送
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
