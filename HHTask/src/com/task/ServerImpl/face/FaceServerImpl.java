package com.task.ServerImpl.face;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.face.FaceServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.entity.Users;
import com.task.entity.dmltry.Conditioning;
import com.task.entity.face.FaceAlarm;
import com.task.entity.face.FaceCamera;
import com.task.entity.face.FaceUsers;
import com.task.entity.face.FaceWorkTime;
import com.task.util.FaceBaiDuUtil;
import com.task.util.Util;
import com.task.util.UtilTong;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class FaceServerImpl extends Thread implements FaceServer {

	static FaceUtil faceUtil = null;
	// static PlayCtrl playControl = PlayCtrl.INSTANCE;
	public static String basePath;
	public static String basePath2;
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public static String getBasePath() {
		return basePath;
	}

	public static void setBasePath(String basePath) {
		FaceServerImpl.basePath = basePath;
	}

	public static String getBasePath2() {
		return basePath2;
	}

	public static void setBasePath2(String basePath2) {
		FaceServerImpl.basePath2 = basePath2;
	}

	public static boolean initFace() {
		boolean net_DVR_Init = faceUtil.NET_DVR_Init();
		return net_DVR_Init;
	}

	/**
	 * 登录摄像头，返回
	 * 
	 * @return
	 */
	public static NativeLong loginCamera(FaceCamera camera) {
		boolean initFace = initFace();
		if (camera != null && initFace) {
			NET_DVR_DEVICEINFO_V30 lpDeviceInfo = new NET_DVR_DEVICEINFO_V30();
			lpDeviceInfo.byAlarmInPortNum = 1;
			lpDeviceInfo.byAlarmOutPortNum = 1;
			lpDeviceInfo.byDiskNum = 0;
			lpDeviceInfo.byDVRType = 1;
			lpDeviceInfo.byChanNum = 15;
			lpDeviceInfo.byStartChan = 1;
			lpDeviceInfo.byIPChanNum = 15;
			NativeLong loginUserId = faceUtil.NET_DVR_Login_V30(camera.getIp(),
					Short.parseShort(camera.getPort().toString()), camera
							.getUserName(), camera.getPassword(), lpDeviceInfo);
			System.out.println("用户ID：" + loginUserId);
			if (loginUserId != null && loginUserId.intValue() != -1) {
				return loginUserId;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void run() {
		if (faceUtil == null) {
			try {
				faceUtil = (FaceUtil) Native.loadLibrary(basePath
						+ "\\HCNetSDK", FaceUtil.class);
			} catch (Exception e) {
				e.printStackTrace();
				basePath = basePath2;
				faceUtil = (FaceUtil) Native.loadLibrary(basePath
						+ "\\HCNetSDK", FaceUtil.class);
			}
		}
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		@SuppressWarnings("unchecked")
		List<FaceCamera> cameraList = totalDao.query("from FaceCamera");
		if (cameraList != null && cameraList.size() > 0) {
			for (FaceCamera faceCamera : cameraList) {
				String filePath = basePath + "\\picture\\face_"
						+ faceCamera.getId() + ".jpg";

				RunCameraFace runCameraFace = new RunCameraFace();
				runCameraFace.setFaceUtil(faceUtil);
				runCameraFace.setFILE_PATH(filePath);
				runCameraFace.setFaceCamera(faceCamera);
				runCameraFace.start();

				String filePath1 = basePath + "\\picture\\face_"
						+ faceCamera.getId() + "_1.jpg";

				RunCameraFace runCameraFace1 = new RunCameraFace();
				runCameraFace1.setFaceUtil(faceUtil);
				runCameraFace1.setFILE_PATH(filePath1);
				runCameraFace1.setFaceCamera(faceCamera);
				runCameraFace1.start();
			}
		}

	}

	public static void main(String[] args) {
		// FaceServerImpl face = new FaceServerImpl();
		String FILE_PATH = "D:\\Tomcat\\apache-tomcat-8.0.53\\webapps\\HHTask\\upload\\file\\dll\\face\\picture\\face_2.jpg";
		if (faceUtil == null) {
			faceUtil = (FaceUtil) Native
					.loadLibrary(
							"D:\\Tomcat\\apache-tomcat-8.0.53\\webapps\\HHTask\\upload\\file\\dll\\face\\HCNetSDK",
							FaceUtil.class);
		}
		FaceCamera faceCamera = new FaceCamera();
		faceCamera.setId(3);
		faceCamera.setIp("192.168.0.43");
		faceCamera.setName("测试摄像头");
		faceCamera.setPassword("cy19960822");
		faceCamera.setPort(8000);
		faceCamera.setUserName("admin");
		RunCameraFace cameraFace = new RunCameraFace();
		cameraFace.setFILE_PATH(FILE_PATH);
		cameraFace.setFaceCamera(faceCamera);
		cameraFace.setFaceUtil(faceUtil);
		cameraFace.start();
		// face.start();
	}

	@SuppressWarnings("unchecked")
	public List<FaceUsers> findFaceUsersByUserId(Integer userId,
			String pageStatus) {
		if (userId != null && userId > 0) {
			List<FaceUsers> list = totalDao.query(
					"from FaceUsers where userId is not null and  userId = ?",
					userId);
			return list;
		} else if (userId == null && pageStatus != null
				&& pageStatus.equals("alarm")) {
			List<FaceUsers> list = totalDao
					.query("from FaceUsers where groupName is not null and groupName = 'alarm'");
			return list;
		} else {
			return null;
		}
	}

	public String addFaceUsers(FaceUsers faceUsers, String pageStatus) {
		if (faceUsers != null && faceUsers.getCode() != null
				&& faceUsers.getPicturePath() != null
				&& faceUsers.getUserId() != null) {
			faceUsers.setFaceCode(faceUsers.getCode() + faceUsers.getUserId());
			faceUsers.setGroupName("Inner");
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("group_id", faceUsers.getGroupName());
			map.put("user_id", faceUsers.getFaceCode());
			map.put("user_info", faceUsers.getCode());
			map.put("liveness_control", "NORMAL");
			map.put("image_type", "BASE64");
			map.put("quality_control", "NONE");
			String fileUrl = ServletActionContext.getServletContext()
					.getRealPath("/upload/file/face");

			try {
				// URL urls = new URL("file:///" + fileUrl +
				// faceUsers.getPicturePath());
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// Image image = Toolkit.getDefaultToolkit().getImage(urls);
				// BufferedImage biOut = toBufferedImage(image);
				// ImageIO.write(biOut, "jpeg", baos);
				// String base64Str = Base64Util.encode(baos.toByteArray());
				String base64Str = getImageStr(fileUrl + "\\"
						+ faceUsers.getPicturePath());
				map.put("image", base64Str);
				Map<String, Object> resultMap = FaceBaiDuUtil.add(map);
				if (resultMap != null) {
					String msg = (String) resultMap.get("error_msg");
					if (msg != null && msg.equals("SUCCESS")) {
						faceUsers.setFace_token(resultMap.get("face_token")
								.toString());
						boolean save = totalDao.save(faceUsers);
						if (save) {
							return "添加成功";
						} else {
							return "添加失败";
						}
					} else {
						return "添加失败，异常信息：" + msg;
					}
				}
				return "系统异常";
			} catch (Exception e) {
				e.printStackTrace();
				return "图片转换失败" + e.getMessage();
			}

		} else {
			return "参数错误";
		}
	}

	public static boolean generateImage(String imgStr, String path) {
		if (imgStr == null)
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// 解密
			byte[] b = decoder.decodeBuffer(imgStr);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getImageStr(String imgFile) {
		InputStream inputStream = null;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(imgFile);
			data = new byte[inputStream.available()];
			// data = imgFile.getBytes();
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 加密
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

	public BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image
					.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen

		}
		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image
					.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	@Override
	public String deleteFaceUsersById(Integer id) {
		if (id != null) {
			FaceUsers faceUsers = (FaceUsers) totalDao.getObjectById(
					FaceUsers.class, id);
			if (faceUsers != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user_id", faceUsers.getFaceCode());
				map.put("group_id", faceUsers.getGroupName());
				map.put("face_token", faceUsers.getFace_token());
				map.put("image_type", "BASE64");
				String fileUrl = ServletActionContext.getServletContext()
						.getRealPath("/upload/file/face");

				try {
					// URL urls = new URL("file:///" + fileUrl +
					// faceUsers.getPicturePath());
					// ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// Image image = Toolkit.getDefaultToolkit().getImage(urls);
					// BufferedImage biOut = toBufferedImage(image);
					// ImageIO.write(biOut, "jpeg", baos);
					// String base64Str = Base64Util.encode(baos.toByteArray());
					String base64Str = getImageStr(fileUrl + "\\"
							+ faceUsers.getPicturePath());
					map.put("image", base64Str);
					// String msg = FaceBaiDuUtil.detect(map);
					String msg = FaceBaiDuUtil.delete(map);
					if (msg != null && msg.equals("SUCCESS")) {
						boolean save = totalDao.delete(faceUsers);
						if (save) {
							return "删除成功";
						} else {
							return "删除失败";
						}
					} else {
						return "删除失败，异常信息：" + msg;
					}

				} catch (Exception e) {
					e.printStackTrace();
					return "图片转换失败";
				}
			}
		}
		return "没有找到人脸信息";
	}

	// 根据Users.id查找Users
	@Override
	public Users getUsersById(Integer id) {
		if (id != null) {
			return (Users) totalDao.getObjectById(Users.class, id);
		}
		return null;
	}

	@Override
	public Map<String, Object> findFaceCameraByCon(FaceCamera camera,
			Integer pageNo, Integer pageSize, String pageStatus) {
		if (camera == null) {
			camera = new FaceCamera();
		}
		String hql = totalDao.criteriaQueries(camera, null);
		List<FaceCamera> list = totalDao.findAllByPage(hql, pageNo, pageSize,
				null);
		if (list != null && list.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			Integer count = totalDao.getCount(hql);
			map.put("count", count);
			map.put("list", list);
			return map;
		}

		return null;
	}

	@Override
	public String addFaceCamera(FaceCamera camera, String pageStatus) {
		if (camera != null) {
			if (camera.getIp() == null || camera.getIp().equals("")) {
				return "请输入摄像头IP地址";
			}
			if (camera.getUserName() == null || camera.getUserName().equals("")) {
				return "请输入摄像头登录名称";
			}

			// 登录摄像头确认信息是否正确
			NativeLong loginCamera = loginCamera(camera);
			if (loginCamera != null) {
				boolean net_DVR_Cleanup = faceUtil.NET_DVR_Cleanup();
				System.out.println("关闭资源" + net_DVR_Cleanup);
				boolean save = totalDao.save(camera);
				if (save) {
					return "添加成功";
				}
			}
			return "添加失败";
		} else {
			return "对象为空";
		}
	}

	@Override
	public String updateFaceCamera(FaceCamera camera, String pageStatus) {

		if (camera != null && camera.getId() != null) {
			if (camera.getIp() == null || camera.getIp().equals("")) {
				return "请输入摄像头IP地址";
			}
			if (camera.getUserName() == null || camera.getUserName().equals("")) {
				return "请输入摄像头登录名称";
			}
			FaceCamera faceCamera = (FaceCamera) totalDao.getObjectById(
					FaceCamera.class, camera.getId());
			BeanUtils.copyProperties(camera, faceCamera);
			// 登录摄像头确认信息是否正确
			NativeLong loginCamera = loginCamera(camera);
			if (loginCamera != null) {
				boolean net_DVR_Cleanup = faceUtil.NET_DVR_Cleanup();
				System.out.println("关闭资源" + net_DVR_Cleanup);
				boolean update = totalDao.update(faceCamera);
				if (update) {
					return "修改成功";
				}
			} else {
				return "登录失败";
			}

		} else {
			return "对象为空";
		}
		return "修改失败";
	}

	@Override
	public String deleteFaceCamera(Integer id) {
		FaceCamera faceCamera = (FaceCamera) totalDao.getObjectById(
				FaceCamera.class, id);
		if (faceCamera != null) {
			boolean delete = totalDao.delete(faceCamera);
			if (delete) {
				return "删除成功";
			}
		} else {
			return "没查询到该摄像头";
		}
		return "删除失败";
	}

	@Override
	public FaceCamera getFaceCameraById(Integer id) {
		if (id != null) {
			return (FaceCamera) totalDao.getObjectById(FaceCamera.class, id);
		}
		return null;
	}

	// 改变成二进制码
	public static int compareImage(byte[] oldByte, byte[] newByte)
			throws IOException {
		if (oldByte == null || newByte == null) {
			return 100;
		}
		int xiangsi = 0;
		int busi = 0;
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		try {
			inputStream1 = new ByteArrayInputStream(oldByte);
			inputStream2 = new ByteArrayInputStream(newByte);
			BufferedImage bi1 = ImageIO.read(inputStream1);
			BufferedImage bi2 = ImageIO.read(inputStream2);
			int width = bi1.getWidth();
			int height = bi1.getHeight();
			int minx = bi1.getMinX();
			int miny = bi1.getMinY();
			for (int i = minx; i < width; i += 5) {
				for (int j = miny; j < height; j += 5) {
					int rgb1 = bi1.getRGB(i, j);
					int read1 = (rgb1 & 0xff0000) >> 16;
					int green1 = (rgb1 & 0xff00) >> 8;
					int blue1 = (rgb1 & 0xff);

					int rgb2 = bi2.getRGB(i, j);
					int read2 = (rgb2 & 0xff0000) >> 16;
					int green2 = (rgb2 & 0xff00) >> 8;
					int blue2 = (rgb2 & 0xff);

					if (Math.abs(read1 - read2) < 20
							&& Math.abs(green1 - green2) < 20
							&& Math.abs(blue1 - blue2) < 20) {
						xiangsi++;
					} else {
						busi++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 100;
		} finally {
			if (inputStream1 != null) {
				inputStream1.close();
			}
			if (inputStream2 != null) {
				inputStream2.close();
			}
		}

		String baifen = "";
		try {
			baifen = ((Double.parseDouble(xiangsi + "") / Double
					.parseDouble((busi + xiangsi) + "")) + "");
			baifen = baifen.substring(baifen.indexOf(".") + 1, baifen
					.indexOf(".") + 3);
		} catch (Exception e) {
			baifen = "0";
		}
		if (baifen.length() <= 0) {
			baifen = "0";
		}
		if (busi == 0) {
			baifen = "100";
		}
		return Integer.parseInt(baifen);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findWorkTimeLongByCon(FaceWorkTime workTime,
			Integer pageNo, Integer pageSize, String pageStatus) {
		if (workTime == null) {
			workTime = new FaceWorkTime();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String otherAppend = "";
		String dateTime = Util.getDateTime("yyyy-MM-dd");

		String startTime = dateTime;
		String endTime = dateTime;
		List<FaceWorkTime> list = new ArrayList<FaceWorkTime>();
		if ((pageStatus != null && pageStatus.equals("dateTime"))) {
			if (StringUtils.isNotEmpty(workTime.getStartTime())) {
				otherAppend = " and dateTime >='" + workTime.getStartTime()
						+ "'";
				startTime = workTime.getStartTime();
			}
			if (StringUtils.isNotEmpty(workTime.getEndTime())) {
				otherAppend = otherAppend + " and dateTime <='"
						+ workTime.getEndTime() + "'";
				endTime = workTime.getEndTime();
			}
		} else if (StringUtils.isEmpty(pageStatus) || pageStatus.equals("day")) {
			otherAppend = " and dateTime ='" + dateTime + "'";
		} else if (pageStatus.equals("week")) {
			Calendar instance = Calendar.getInstance();
			int dayWeek = instance.get(Calendar.DAY_OF_WEEK);
			if (1 == dayWeek) {
				instance.add(Calendar.DAY_OF_MONTH, -1);
			}
			// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
			instance.setFirstDayOfWeek(Calendar.MONDAY);
			// 获得当前日期是一个星期的第几天
			int day = instance.get(Calendar.DAY_OF_WEEK);
			// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
			instance.add(Calendar.DATE, instance.getFirstDayOfWeek() - day);
			startTime = new SimpleDateFormat("yyyy-MM-dd").format(instance
					.getTime());
			otherAppend = " and dateTime >='" + startTime + "' and dateTime <'"
					+ dateTime + "'";
		} else if (pageStatus.equals("month")) {
			Calendar instance = Calendar.getInstance();
			instance.set(Calendar.DAY_OF_MONTH, 1);
			instance.set(Calendar.HOUR, 0);
			instance.set(Calendar.MINUTE, 0);
			instance.set(Calendar.SECOND, 0);
			startTime = new SimpleDateFormat("yyyy-MM-dd").format(instance
					.getTime());
			otherAppend = " and dateTime >='" + startTime + "' and dateTime<='"
					+ dateTime + "' ";
		} else if (pageStatus.equals("year")) {
			Calendar instance = Calendar.getInstance();
			instance.set(Calendar.DAY_OF_YEAR, 1);
			instance.set(Calendar.HOUR, 0);
			instance.set(Calendar.MINUTE, 0);
			instance.set(Calendar.SECOND, 0);
			startTime = new SimpleDateFormat("yyyy-MM-dd").format(instance
					.getTime());
			otherAppend = " and dateTime >='" + startTime + "' and dateTime<='"
					+ dateTime + "' ";
		}
		workTime.setStartTime(null);
		workTime.setEndTime(null);
		String hql = totalDao.criteriaQueries(workTime, null);
		workTime.setStartTime(startTime);
		workTime.setEndTime(endTime);

		List<Integer> userIdList = totalDao.findAllByPage("select userId "
				+ hql + otherAppend + " group by userId", pageNo, pageSize);
		for (Integer userId : userIdList) {
			List<FaceWorkTime> workList = totalDao.query(
					"from FaceWorkTime where userId=? " + otherAppend, userId);
			Integer totalMinutes = 0;
			StringBuffer idString = new StringBuffer();
			for (FaceWorkTime faceWorkTime : workList) {
				if (idString.toString().equals("")) {
					idString.append("" + faceWorkTime.getId());
				} else {
					idString.append("," + faceWorkTime.getId());
				}
				if (faceWorkTime != null
						&& (faceWorkTime.getEndTime() == null
								|| faceWorkTime.getEndTime().equals("") || faceWorkTime
								.getWorkTime() == null)) {
					if (dateTime.equals(faceWorkTime.getDateTime())) {
						// 当天的是现在的时间
						try {
							Long dateDiff = Util.getDateDiff(faceWorkTime
									.getStartTime(), "HH:mm:ss", Util
									.getDateTime("HH:mm:ss"), "HH:mm:ss") / 60 + 1;
							totalMinutes += dateDiff.intValue();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						// 不是今天的为23:59:59
						try {
							Long dateDiff = Util.getDateDiff(faceWorkTime
									.getStartTime(), "HH:mm:ss", "23:59:59",
									"HH:mm:ss") / 60 + 1;
							totalMinutes += dateDiff.intValue();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				} else {
					totalMinutes += faceWorkTime.getWorkTime();
				}
			}
			FaceWorkTime faceWorkTime = workList.get(0);
			faceWorkTime.setWorkTime(totalMinutes);
			faceWorkTime.setIds(idString.toString());
			list.add(faceWorkTime);
			// Float floatTotal = (Float) totalDao.getObjectByCondition("select
			// sum(workTime) from FaceWorkTime where userId = ? "+otherAppend,
			// userId);
		}
		if (!list.isEmpty()) {
			map.put("list", list);
			Integer count = totalDao.getCount(hql + otherAppend
					+ " group by userId");
			map.put("count", count);
		}
		// }else {
		// String dateTimeAppend="";
		// if(StringUtils.isEmpty(pageStatus) || pageStatus.equals("day")) {
		// dateTimeAppend = " and dateTime ='"+dateTime+"'";
		// workTime.setStartTime(dateTime);
		// workTime.setEndTime(dateTime);
		// }else if(pageStatus.equals("week")){
		// Calendar instance = Calendar.getInstance();
		// instance.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		// instance.set(Calendar.HOUR, 0);
		// instance.set(Calendar.MINUTE, 0);
		// instance.set(Calendar.SECOND, 0);
		// String startTime = new
		// SimpleDateFormat("yyyy-MM-dd").format(instance.getTime());
		// dateTimeAppend = " and dateTime >='"+startTime+"' and endTime
		// <'"+dateTime+"'
		// ";
		// workTime.setStartTime(startTime);
		// workTime.setEndTime(dateTime);
		// }else if(pageStatus.equals("month")) {
		// Calendar instance = Calendar.getInstance();
		// instance.set(Calendar.DAY_OF_MONTH, 1);
		// instance.set(Calendar.HOUR, 0);
		// instance.set(Calendar.MINUTE, 0);
		// instance.set(Calendar.SECOND, 0);
		// String startTime = new
		// SimpleDateFormat("yyyy-MM-dd").format(instance.getTime());
		// dateTimeAppend = " and dateTime >='"+startTime+"' and endTime
		// <='"+dateTime+"' ";
		// workTime.setStartTime(startTime);
		// workTime.setEndTime(dateTime);
		// }else if(pageStatus.equals("year")) {
		// Calendar instance = Calendar.getInstance();
		// instance.set(Calendar.DAY_OF_YEAR, 1);
		// instance.set(Calendar.HOUR, 0);
		// instance.set(Calendar.MINUTE, 0);
		// instance.set(Calendar.SECOND, 0);
		// String startTime = new
		// SimpleDateFormat("yyyy-MM-dd").format(instance.getTime());
		// dateTimeAppend = " and dateTime >='"+startTime+"' and endTime
		// <='"+dateTime+"' ";
		// workTime.setStartTime(startTime);
		// workTime.setEndTime(dateTime);
		// }
		// String userIdHql="select userId from FaceWorkTime where 1=1
		// "+dateTimeAppend+" group by userId";
		// List<Integer> userIdList = totalDao.findAllByPage(userIdHql, pageNo,
		// pageSize);
		// List<FaceWorkTime> list = new ArrayList<FaceWorkTime>();
		// FaceWorkTime copyWorkTime = null;
		// for (Integer userId : userIdList) {
		// String hql = "select sum(workTime) from FaceWorkTime where 1=1
		// "+dateTimeAppend+" and userId = ?";
		// Float minutes = (Float) totalDao.getObjectByCondition(hql, userId);
		// String userHql = "from FaceWorkTime where id = (select max(id) from
		// FaceWorkTime where 1=1 "+dateTimeAppend+" and userId = ?) ";
		// FaceWorkTime faceWorkTime = (FaceWorkTime)
		// totalDao.getObjectByCondition(userHql, userId);
		// copyWorkTime = new FaceWorkTime();
		// BeanUtils.copyProperties(faceWorkTime, copyWorkTime);
		// copyWorkTime.setWorkTime(minutes.intValue());
		// list.add(copyWorkTime);
		// }
		// if (!list.isEmpty()) {
		// map.put("list", list);
		// Integer count = totalDao.getCount(userIdHql);
		// map.put("count", count);
		// }
		// }
		map.put("workTime", workTime);

		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FaceWorkTime> getFaceWorkTimeByIds(String ids) {
		if (ids != null && !ids.equals("")) {
			List<FaceWorkTime> list = totalDao
					.query("from FaceWorkTime where id in (" + ids + ")");
			String diff = "HH:mm:ss";
			String time = Util.getDateTime(diff);
			String dateTime = Util.getDateTime("yyyy-MM-dd");
			if (list != null && list.size() > 0) {
				for (FaceWorkTime faceWorkTime : list) {
					if (faceWorkTime.getEndTime() == null
							|| faceWorkTime.getEndTime().equals("")) {
						try {
							Long dateDiff = 0l;
							if (faceWorkTime.getDateTime() != null
									&& faceWorkTime.getDateTime().equals(
											dateTime)) {
								dateDiff = (Util.getDateDiff(faceWorkTime
										.getStartTime(), diff, time, diff) / 60) + 1;
							} else {
								dateDiff = (Util
										.getDateDiff(faceWorkTime
												.getStartTime(), diff,
												"23:59:59", diff) / 60) + 1;
							}
							faceWorkTime.setWorkTime(dateDiff.intValue());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						faceWorkTime.setEndTime("-");
					}
				}
			}

			return list;
		}
		return null;
	}

	@Override
	public Map<String, Object> searchLineChart(String userCode,
			String startTime, String endTime) {
		if (userCode != null && !userCode.equals("")) {
			String hql = "from FaceWorkTime where userCode = ?";
			if (startTime != null && !startTime.equals("")) {
				hql += " and dateTime >='" + startTime + "'";
			}
			if (endTime != null && !endTime.equals("")) {
				hql += " and dateTime <= '" + endTime + "'";
			}
			String diff = "HH:mm:ss";
			String time = Util.getDateTime(diff);
			String dateTime = Util.getDateTime("yyyy-MM-dd");
			// List<FaceWorkTime> list = totalDao.query(hql, userCode);
			List<String> dateTimeList = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			// 开始时间无值处理
			if (startTime == null || startTime.equals("")) {
				startTime = (String) totalDao
						.getObjectByCondition("select min(dateTime) from FaceWorkTime");
				if (startTime == null) {
					return null;
				}
			}
			// 结束时间无值处理
			if (endTime == null || endTime.equals("")) {
				endTime = Util.getDateTime("yyyy-MM-dd");
			}
			// 开始时间大于结束时间处理
			long endTimeLong = 0l;
			long startTimeLong = 0l;
			try {
				startTimeLong = simpleDateFormat.parse(startTime).getTime();
				endTimeLong = simpleDateFormat.parse(endTime).getTime();
				if (startTimeLong > endTimeLong) {
					return null;
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<Integer> hoursList = new ArrayList<Integer>();
			List<String> endTimeList = new ArrayList<String>();
			List<String> startTimeList = new ArrayList<String>();
			do {
				dateTimeList.add(startTime);
				List<FaceWorkTime> list = totalDao
						.query(
								"from FaceWorkTime where dateTime = ? and userCode = ?",
								startTime, userCode);
				Integer totalMinutes = 0;
				if (list != null && list.size() > 0) {
					for (FaceWorkTime faceWorkTime : list) {
						startTimeList.add(faceWorkTime.getStartTime());
						if (faceWorkTime.getEndTime() == null
								|| faceWorkTime.getEndTime().equals("")) {
							try {
								Long dateDiff = 0l;
								if (faceWorkTime.getDateTime() != null
										&& faceWorkTime.getDateTime().equals(
												dateTime)) {
									dateDiff = (Util.getDateDiff(faceWorkTime
											.getStartTime(), diff, time, diff) / 60) + 1;
								} else {
									dateDiff = (Util.getDateDiff(faceWorkTime
											.getStartTime(), diff, "23:59:59",
											diff) / 60) + 1;
								}
								faceWorkTime.setWorkTime(dateDiff.intValue());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							faceWorkTime.setEndTime("-");
							endTimeList.add("23:59:59");
						} else {
							endTimeList.add(faceWorkTime.getEndTime());
						}
						totalMinutes += faceWorkTime.getWorkTime();
					}
				} else {
					startTimeList.add("-");
					endTimeList.add("-");
				}
				int hour = 0;
				if (totalMinutes != 0) {
					hour = new BigDecimal(totalMinutes).divide(
							new BigDecimal(60), 5, BigDecimal.ROUND_HALF_UP)
							.intValue();
				}
				hoursList.add(hour);

				try {
					calendar.setTime(simpleDateFormat.parse(startTime));
					calendar.add(Calendar.DATE, 1);
					startTime = simpleDateFormat.format(calendar.getTime());
					startTimeLong = simpleDateFormat.parse(startTime).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			} while (startTimeLong <= endTimeLong);
			map.put("hoursList", hoursList);
			map.put("dateTimeList", dateTimeList);
			map.put("endTimeList", endTimeList);
			map.put("startTimeList", startTimeList);

			return map;
		}
		return null;
	}

	@Override
	public String addAlarmFaceUsers(FaceUsers faceUsers, String pageStatus) {
		if (faceUsers != null) {
			// && faceUsers.getCode() != null && faceUsers.getPicturePath() !=
			// null && faceUsers.getUserId() != null
			if (faceUsers.getCode() == null || faceUsers.getCode().equals("")) {
				String before = "facealarm";
				String maxnumber = (String) totalDao
						.getObjectByCondition("select max(code) from FaceUsers where groupName is not null and groupName = 'alarm'"
								+ " and code like '" + before + "%'");
				DecimalFormat df = new DecimalFormat("00000");
				if (maxnumber != null && !"".equals(maxnumber)) {
					// String number1 = paymentVoucher2.getNumber();
					String now_number = maxnumber.split(before)[1];
					Integer number2 = Integer.parseInt(now_number) + 1;
					String number3 = df.format(number2);
					faceUsers.setCode(before + number3);
					faceUsers.setUserId(number2);
				} else {
					faceUsers.setCode(before + "00001");
				}
			} else {
				String code = (String) totalDao.getObjectByCondition(
						"select code from FaceUsers where faceCode =?",
						faceUsers.getCode());
				if (code != null && code.equals("")) {
					return "识别用户已经存在，添加失败";
				}
			}
			faceUsers.setFaceCode(faceUsers.getCode());
			faceUsers.setGroupName("alarm");
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("group_id", faceUsers.getGroupName());
			Object put = map.put("user_id", faceUsers.getFaceCode());
			map.put("user_info", faceUsers.getCode());
			map.put("liveness_control", "NORMAL");
			map.put("image_type", "BASE64");
			map.put("quality_control", "NONE");
			String fileUrl = ServletActionContext.getServletContext()
					.getRealPath("/upload/file/face");

			try {
				String base64Str = getImageStr(fileUrl + "\\"
						+ faceUsers.getPicturePath());
				map.put("image", base64Str);
				Map<String, Object> resultMap = FaceBaiDuUtil.add(map);
				if (resultMap != null) {
					String msg = (String) resultMap.get("error_msg");
					if (msg != null && msg.equals("SUCCESS")) {
						faceUsers.setFace_token(resultMap.get("face_token")
								.toString());
						boolean save = totalDao.save(faceUsers);
						if (save) {
							return "添加成功";
						} else {
							return "添加失败";
						}
					} else {
						return "添加失败，异常信息：" + msg;
					}
				}
				return "系统异常";
			} catch (Exception e) {
				e.printStackTrace();
				return "图片转换失败" + e.getMessage();
			}

		} else {
			return "参数错误";
		}
	}

	@Override
	public List<FaceAlarm> findSxtFaceAlarm(Integer id) {
		String hql = "from FaceAlarm";
		if (id != null && id > 0) {
			hql += " and id>" + id;
		}
		hql += " ORDER BY id desc";
		List<FaceAlarm> list = new ArrayList<FaceAlarm>();
		if (id != null && id > 0) {
			list = totalDao.query(hql);
		} else {
			FaceAlarm faceAlarm = (FaceAlarm) totalDao
					.getObjectByCondition(hql);
			// attendanceTow.setName("maxid");
			list.add(faceAlarm);

		}
		return list;
	}

}

class RunCameraFace extends Thread {

	private FaceCamera faceCamera;
	private FaceUtil faceUtil;
	private String FILE_PATH = null;

	public void setFaceCamera(FaceCamera faceCamera) {
		this.faceCamera = faceCamera;
	}

	public void setFILE_PATH(String fILE_PATH) {
		FILE_PATH = fILE_PATH;
	}

	public void setFaceUtil(FaceUtil faceUtil) {
		this.faceUtil = faceUtil;
	}

	@Override
	public void run() {
		NativeLong loginCamera = FaceServerImpl.loginCamera(faceCamera);// loginCamera("192.168");
		if (loginCamera != null) {
			NET_DVR_JPEGPARA lpJpegPara = new NET_DVR_JPEGPARA();
			lpJpegPara.wPicQuality = (short) 2;
			lpJpegPara.wPicSize = (short) 0;
			boolean b = false;
			try {
				byte[] oldByte = null;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("liveness_control", "NORMAL");// 活体检测控制默认NONE
				// NONE: 不进行控制
				// LOW:较低的活体要求(高通过率 低攻击拒绝率)
				// NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)
				// HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
				// 若活体检测结果不满足要求，则返回结果中会提示活体检测失败
				map.put("group_id_list", "Inner,visitor,alarm");// 从指定的group中进行查找
				// 用逗号分隔，上限20个
				map.put("image_type", "BASE64");
				map.put("face_type", "LIVE");// 默认LIVE
				// LIVE：表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等，
				// IDCARD：表示身份证芯片照：二代身份证内置芯片中的人像照片，
				// WATERMARK：表示带水印证件照：一般为带水印的小图，如公安网小图
				// CERT：表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片
				// 默认LIVE
				map.put("quality_control", "NORMAL");// NONE: 不进行控制LOW:较低的质量要求
				// NORMAL: 一般的质量要求
				// HIGH: 较高的质量要求 默认 NONE
				map.put("max_face_num", 10);// 最多处理人脸的数目默认值为1(仅检测图片中面积最大的那个人脸)
				// 最大值10
				map.put("match_threshold", 75);// 此阈值设置得越高，检索速度将会越快，推荐使用默认阈值80
				map.put("max_user_num", 10);// 识别返回的最大用户数，默认为1，最大20个
				map.put("max_face_num", 10);// 识别返回的最大用户数，默认为1，最大20个
				Integer similarity = faceCamera.getSimilarity();
				while (true) {
					byte[] captureJPEGPicture_NEW = CaptureJPEGPicture_NEW(loginCamera);
					if (captureJPEGPicture_NEW != null) {
						// 图片保存成功，比对人脸
						String base64Str = byte2Base64StringFun(captureJPEGPicture_NEW);
						// long currentTimeMillis = System.currentTimeMillis();
						// int compareImage =
						// FaceServerImpl.compareImage(oldByte,
						// captureJPEGPicture_NEW);
						// long currentTimeMillis2 = System.currentTimeMillis();
						// long l = currentTimeMillis2 - currentTimeMillis;
						// System.out.println(faceCamera.getIp() +
						// "~~~~~~~~~~~~~~~~~~~~~~~~~~~~人脸识别毫秒时间差：" + l);
						if (similarity == null) {
							similarity = 100;
						}
						if (-1 <= similarity) {// 本地计算图片相似度小于85获取比对-----建议根据实际环境测试一遍。、。
							// System.out.print(base64Str);
							map.put("image", base64Str);
							try {
								List<String> userList = FaceBaiDuUtil
										.searchMulti(map);
								if (userList != null) {
									for (String userCode : userList) {
										if (faceCamera != null
												&& faceCamera.getCameraUse()
														.equals("进门")) {
											if (userCode != null
													&& userCode.equals("alarm")) {
												throw new RuntimeException(
														"非法人员闯入");
											}
										}
										RunClock runClock = new RunClock(
												userCode);
										runClock.setFaceCamera(faceCamera);
										runClock.start();// 为了加快执行速度使用异步(线程)执行打卡
									}
								}
							} catch (Exception e) {
								try {
									if (faceCamera != null
											&& faceCamera.getCameraUse()
													.equals("进门")) {
										String fileName = "face_call_"
												+ Util
														.getDateTime("yyyyMMddHHmmss")
												+ ".jpg";
										String uploadPath = FILE_PATH
												.substring(0, FILE_PATH
														.lastIndexOf("\\"))
												+ "\\call\\" + fileName;

										byteToFile(captureJPEGPicture_NEW,
												uploadPath);
										String url = "faceAction!callAction.action?pageStatus="
												+ fileName;// 1241,
										String cameraPos = "";
										cameraPos = faceCamera.getPosition();
										AlertMessagesServerImpl
												.addAlertMessages(
														"布防报警提示",
														cameraPos + "检测不明人员进入",// +
																				// e.getMessage()
														new Integer[] { 211 },
														url, true, true);
									}
								} catch (Exception e2) {
									e2.printStackTrace();
								}
							}
						} else {
							// System.out.println(faceCamera.getIp() + "图片相似度："
							// + compareImage);
						}
						// oldByte = captureJPEGPicture_NEW;
						// long l2 = System.currentTimeMillis() -
						// currentTimeMillis2;
						// System.out.println("百度比对返回时间差：" + l2);
					}
				}
			} catch (Exception e) {
				System.out.println(faceCamera.getIp() + "获取视频图片异常！");
				e.printStackTrace();
			}
		} else {
			System.out.println(faceCamera.getIp() + "摄像头初始化失败");
		}
	}

	public static void byteToFile(byte[] contents, String filePath) {
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream output = null;
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(
					contents);
			bis = new BufferedInputStream(byteInputStream);
			File file = new File(filePath);
			// 获取文件的父路径字符串
			File path = file.getParentFile();
			if (!path.exists()) {
				System.out.println("文件夹不存在，创建。path=" + path);
				boolean isCreated = path.mkdirs();
				if (!isCreated) {
					System.out.println("创建文件夹失败，path={}" + path);
				}
			}
			fos = new FileOutputStream(file);
			// 实例化OutputString 对象
			output = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			int length = bis.read(buffer);
			while (length != -1) {
				output.write(buffer, 0, length);
				length = bis.read(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
				fos.close();
				output.close();
			} catch (IOException e0) {
			}
		}
	}

	// 抓取到内存
	public byte[] CaptureJPEGPicture_NEW(NativeLong loginCamera) {
		byte[] bytes = null;
		boolean returnboll = false;
		if (loginCamera.longValue() > -1) {
			NET_DVR_JPEGPARA jpeginfo = new NET_DVR_JPEGPARA();
			jpeginfo.wPicQuality = 2;
			jpeginfo.wPicSize = 0;
			int dwPicSize = 400 * 1024;
			IntByReference lpSizeReturned = new IntByReference();
			lpSizeReturned.setValue(0);
			NativeLong DVRChannel = new NativeLong();
			DVRChannel.setValue(1);
			Pointer p = new Memory(400 * 1024);
			returnboll = faceUtil.NET_DVR_CaptureJPEGPicture_NEW(loginCamera,
					DVRChannel, jpeginfo, p, dwPicSize, lpSizeReturned);
			if (returnboll) {
				bytes = p.getByteArray(0, lpSizeReturned.getValue());
			} else {
				int err = faceUtil.NET_DVR_GetLastError();
				if (err == 34) { // 34为创建文件出错，可以继续运行.
					System.out.println(faceCamera.getIp() + "摄像头返回图片异常,路径："
							+ FILE_PATH);
				} else if (err == 43) {// 缓冲区太小。接收设备数据的缓冲区或存放图片缓冲区不足。

				}
			}
		}
		return bytes;
	}

	// byte[]转base64
	public static String byte2Base64StringFun(byte[] b) {
		// Base64 base64 = new Base64();
		// return base64.encodeToString(b);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(b);
	}
}

/**
 * 执行打卡等操作
 * 
 * @author admin
 * 
 */
class RunClock extends Thread {
	private String userCode;
	static List<String> userCodeList = new ArrayList<String>();
	private FaceCamera faceCamera;
	private String facePath;
	private Byte[] pictureBuffer;

	public RunClock(String userCode) {
		this.userCode = userCode;
	}

	public RunClock(String userCode, String facePath, Byte[] pictureBuffer) {
		this.userCode = userCode;
		this.facePath = facePath;
		this.pictureBuffer = pictureBuffer;
	}

	public void setFaceCamera(FaceCamera faceCamera) {
		this.faceCamera = faceCamera;
	}

	@Override
	public void run() {
		String dateTime = Util.getDateTime("yyyy-MM-dd HH:mm:ss");
		String dayTime = Util.getDateTime("yyyy-MM-dd");
		String timeDiff = "HH:mm:ss";
		String time = Util.getDateTime(timeDiff);

		TotalDao totalDao = TotalDaoImpl.findTotalDao();

		FaceUsers faceUsers = (FaceUsers) totalDao.getObjectByQuery(
				"from FaceUsers where faceCode = ?", userCode);
		if (faceUsers != null) {
			try {
				if (faceUsers != null
						&& faceUsers.getGroupName().equals("alarm")) {// 报警人员
					// String fileName = "face_alarm_" +
					// Util.getDateTime("yyyyMMddHHmmss") + ".jpg";
					// String uploadPath = FILE_PATH.substring(0,
					// FILE_PATH.lastIndexOf("\\")) + "\\call\\"
					// + fileName;
					// FaceServerImpl.byteToFile(pictureBuffer, uploadPath);

					// alarm.setPicturePath(fileName);
					// 10秒内多次出现只提示一次
					Integer count = totalDao.getCount(
							"from FaceAlarm where userName =? and dateTime like '"
									+ dateTime.substring(0,
											dateTime.length() - 1) + "%'",
							faceUsers.getUserName());
					if (count > 0) {

					} else {
						FaceAlarm alarm = new FaceAlarm();
						alarm.setUserName(faceUsers.getUserName());
						alarm.setCameraPosition(faceCamera.getPosition());
						alarm.setDateTime(dateTime);
						totalDao.save(alarm);
					}
				} else {

					// 正常员工计时打卡
					synchronized (userCodeList) {
						FaceWorkTime workTime = (FaceWorkTime) totalDao
								.getObjectByQuery(
										"from FaceWorkTime where dateTime=? and userId=? "
												+ "and (startTime is null or endTime is null)",
										dayTime, faceUsers.getUserId());
						if (workTime == null
								&& faceCamera.getCameraUse().equals("进门")) {
							workTime = new FaceWorkTime();
							workTime.setAddTime(dateTime);
							workTime.setDateTime(dayTime);
							workTime.setCardId(faceUsers.getCardId());
							if (faceCamera.getCameraUse().equals("进门")) {
								workTime.setStartTime(time);
							}
							workTime.setUserCode(faceUsers.getCode());
							workTime.setUserId(faceUsers.getUserId());
							workTime.setUserName(faceUsers.getUserName());
							workTime.setWorkTime(0);
							totalDao.save(workTime);

							addDaka(dateTime, faceUsers, totalDao, workTime);
						} else if (workTime != null) {
							if ((workTime.getStartTime() == null || workTime
									.getStartTime().equals(""))
									&& faceCamera.getCameraUse().equals("进门")) {
								workTime.setStartTime(time);

								// addDaka(dateTime, faceUsers, totalDao,
								// workTime);
							} else if ((workTime.getStartTime() != null && !workTime
									.getStartTime().equals(""))
									&& (workTime.getEndTime() == null || workTime
											.getEndTime().equals(""))
									&& faceCamera.getCameraUse().equals("出门")) {
								workTime.setEndTime(time);
								if (workTime.getStartTime() != null
										&& workTime.getEndTime() != null) {
									try {
										Long dateDiff = (Util
												.getDateDiff(workTime
														.getStartTime(),
														timeDiff, workTime
																.getEndTime(),
														timeDiff) / 60) + 1;
										workTime.setWorkTime(dateDiff
												.intValue());
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
								totalDao.update(workTime);

								addDaka(dateTime, faceUsers, totalDao, workTime);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int getIndex(String str, String s, int count) {
		if (count == 0)
			return -1;
		int len = s.length();
		int index = -1 - len;
		while (count > 0 && (index = str.indexOf(s, index + len)) != -1) {
			count--;
		}
		if(index==-1){
			index=str.length();
		}
		return index;
	}

	/**
	 * 添加打卡记录
	 * 
	 * @param dateTime
	 * @param faceUsers
	 * @param totalDao
	 * @param workTime
	 */
	public void addDaka(String dateTime, FaceUsers faceUsers,
			TotalDao totalDao, FaceWorkTime workTime) {
		Users u = (Users) totalDao.getObjectById(Users.class, faceUsers
				.getUserId());
		String accessname = "人脸识别摄像头";// 设备名称
		String outIn = "";// 类型。
		if (faceCamera != null) {
			accessname = faceCamera.getName();
			outIn = faceCamera.getCameraUse();
		}
		String retuatten = AttendanceTowServerImpl.addAttendanceTow(faceUsers
				.getCardId(), faceUsers.getCode(), faceUsers.getUserName(),
				faceUsers.getDept(), faceUsers.getUserId(), "人脸识别摄像头",
				accessname, outIn, null, totalDao);

		if ("true".equals(retuatten) && u != null) {
			String sendmessage = null;
			if (outIn != null && outIn.equals("进门")) {
				u.setUserStatus("在岗");
				if (u.getFristTime() == null
						|| !dateTime.equals(u.getFristTime().substring(0, 10))) {
					u.setFristTime(dateTime);
				}

				// 获取上个工作日的工作时长
				Calendar calendar = Calendar.getInstance();
				calendar.add(java.util.Calendar.DATE, -1);
				String formatDateTime = new SimpleDateFormat("yyyy-MM-dd")
						.format(calendar.getTime());
				List<FaceWorkTime> workList = totalDao.query(
						"from FaceWorkTime where userId=? and dateTime =?", u
								.getId(), formatDateTime);
				Integer totalMinutes = 0;
				for (FaceWorkTime faceWorkTime : workList) {
					if (faceWorkTime != null
							&& (faceWorkTime.getEndTime() == null
									|| faceWorkTime.getEndTime().equals("") || faceWorkTime
									.getWorkTime() == null)) {
						// 当天的是现在的时间
						try {
							Long dateDiff = Util.getDateDiff(faceWorkTime
									.getStartTime(), "HH:mm:ss", "23:59:59",
									"HH:mm:ss") / 60 + 1;
							totalMinutes += dateDiff.intValue();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						totalMinutes += faceWorkTime.getWorkTime();
					}
				}
				sendmessage = "您于"
						+ dateTime
						+ "打卡成功，上个工作日工作时长："
						+ new BigDecimal(totalMinutes)
								.divide(new BigDecimal(60), 3,
										BigDecimal.ROUND_HALF_UP).setScale(3,
										BigDecimal.ROUND_HALF_UP).floatValue()
						+ "小时，祝您工作愉快！";
			} else if (outIn != null && outIn.equals("出门")) {
				u.setUserStatus("离开");
				sendmessage = "您于" + dateTime + "打卡成功，本次工作时长："
						+ workTime.getWorkTime() + "分钟";
			}
			totalDao.update2(u);
			List<Conditioning> conlist = totalDao.query("from Conditioning");
			// 循环人员
			for (Conditioning conditioning : conlist) {
				String useridstrString = conditioning.getUserid();
				int zu = useridstrString.length()
						- useridstrString.replaceAll(",", "").length();
				int count = 0;
				for (int i = 0; i < zu + 1; i++) {
					String userid = useridstrString.substring(getIndex(
							useridstrString, ",", i) + 1, getIndex(
							useridstrString, ",", i + 1));
					int id = Integer.parseInt(userid);

					// 根据id查询这个人
					List<Users> users = totalDao.query("from Users WHERE id=?",
							id);
					for (Users users2 : users) {
						if (users2.getUserStatus() == null) {

						} else {

							if (users2.getUserStatus().equals("离开")) {
								count++;
							}
							if (count == zu + 1) {
								for (int j = 0; j < 3; j++) {
									UtilTong.cenl("192.168.0.199", 8888,
											conditioning.getConditioningip()
													+ "3");
								}
							}

						}

					}

				}

			}

			// 发送消息提醒
			Integer uids[] = new Integer[] { faceUsers.getUserId() };
			AlertMessagesServerImpl.addAlertMessages("打卡成功提示", sendmessage,
					uids, "", true, "yes");
		}
	}

	/**
	 * 计算有效工作时长
	 */
	public void compEffectiveWorkTime(FaceUsers faceUsers, String dateTime,
			TotalDao totalDao) {

	}
}

interface FaceUtil extends StdCallLibrary {

	boolean NET_DVR_Init();// 设备初始化，必须调用

	boolean NET_DVR_Cleanup();// 回收资源

	boolean NET_DVR_CapturePictureBlock(long lRealHandle, String sPicFileName,
			long dwTimeOut);

	NativeLong NET_DVR_Login_V30(String sDVRIP, short wDVRPort,
			String sUserName, String sPassword,
			NET_DVR_DEVICEINFO_V30 lpDeviceInfo);// 登录用户获取ID

	boolean NET_DVR_CaptureJPEGPicture(NativeLong lUserID, NativeLong lChannel,
			NET_DVR_JPEGPARA lpJpegPara, String sPicFileName);// 获取帧图片

	// JPEG抓图到内存
	boolean NET_DVR_CaptureJPEGPicture_NEW(NativeLong lUserID,
			NativeLong lChannel, NET_DVR_JPEGPARA lpJpegPara,
			String sJpegPicBuffer, int dwPicSize, IntByReference lpSizeReturned);

	boolean NET_DVR_CaptureJPEGPicture_NEW(NativeLong lUserID,
			NativeLong lChannel, NET_DVR_JPEGPARA lpJpegPara, Pointer p,
			int dwPicSize, IntByReference lpSizeReturned);

	int NET_DVR_GetLastError();// 查看错误码

	boolean NET_DVR_GetDeviceAbility(NativeLong lUserID, short dwAbilityType,
			String pInBuf, short dwInLength, String pOutBuf, short dwOutLength);// 判断设备是否支持人脸侦测模块

}

/**
 * 用户登录信息
 * 
 * @author admin
 * 
 */
class UserLoginInfo {
	String sDeviceAddress;// 设备地址，IP 或者普通域名
	String sUserName = "admin";// 登录用户名，例如：admin
	String sPassword;// 登录密码，例如：12345

	Integer byUseTransport = 0;// 是否启用能力集透传：0- 不启用透传，默认；1- 启用透传
	Integer wPort = 8000;// 设备端口号，例如：8000
	String cbLoginResult;// 登录状态回调函数，bUseAsynLogin 为1时有效
	String pUser;// 用户数据
	Integer bUseAsynLogin = 0;// 是否异步登录：0- 否，1- 是
	Integer byProxyType = 0;// 代理服务器类型：0- 不使用代理，1- 使用标准代理，2- 使用EHome代理
	Integer byUseUTCTime = 0;// 是否使用UTC时间：0- 不进行转换，默认；1-
	// 输入输出UTC时间，SDK进行与设备时区的转换；2-
	// 输入输出平台本地时间，SDK进行与设备时区的转换
	Integer byLoginMode = 0;// 登录模式(不同模式具体含义详见“Remarks”说明)：0- SDK私有协议，1-
	// ISAPI协议，2- 自适应（设备支持协议类型未知时使用，一般不建议）
	Integer byHttps = 1;;// ISAPI协议登录时是否启用HTTPS(byLoginMode为1时有效)：0- 不启用，1-
	// 启用，2-
	// 自适应（设备支持协议类型未知时使用，一般不建议）
	Integer iProxyID = 0;// 代理服务器序号，添加代理服务器信息时相对应的服务器数组下表值
	Integer byRes3 = 0;// 保留，置为0
}
