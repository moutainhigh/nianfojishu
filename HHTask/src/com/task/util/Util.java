package com.task.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.struts2.ServletActionContext;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.entity.Users;
import com.task.entity.sop.Procard;
import com.task.entity.sop.WaigouPlanLock;
import com.task.entity.system.CompanyInfo;
import com.tast.entity.zhaobiao.ZhUser;

/**
 * 工具类
 * 
 * @author 刘培
 */

public class Util {
	// public final static String serverUrl="http://192.168.0.80:8080/HHTask";
	public final static String serverUrl = "http://task.shhhes.com";

	/***
	 * 上班时间
	 * 
	 * @return
	 */
	public static String shangbanTime() {
		if (ActionContext.getContext() != null) {
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext().getApplication().get("companyInfo");
			if (companyInfo != null) {
				return companyInfo.getSbtime();
			}
		}
		return "08:00";
	}

	/***
	 * 下班时间
	 * 
	 * @return
	 */
	public static String xiabanTime() {
		if (ActionContext.getContext() != null) {
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext().getApplication().get("companyInfo");
			if (companyInfo != null) {
				return companyInfo.getXbtime();
			}
		}
		return "18:00:00";
	}

	/***
	 * 午休开始时间
	 * 
	 * @return
	 */
	public static String wxStartTime() {
		if (ActionContext.getContext() != null) {
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext().getApplication().get("companyInfo");
			if (companyInfo != null && companyInfo.getWxstart() != null) {
				return companyInfo.getWxstart();
			}
		}
		return "12:00";
	}

	/***
	 * 午休结束时间
	 * 
	 * @return
	 */
	public static String wxEndTime() {
		if (ActionContext.getContext() != null) {
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext().getApplication().get("companyInfo");
			if (companyInfo != null && companyInfo.getWxend() != null) {
				return companyInfo.getWxend();
			}
		}
		return "14:00:00";
	}

	/**
	 * 获取上班时长
	 * 
	 * @return
	 */
	public static Long getsbtime() throws Exception {
		String sbtime = shangbanTime();
		String xbtime = xiabanTime();
		String wxstart = wxStartTime();
		String wxend = wxEndTime();
		Long sbtimeLong = getTimeToTheDay(sbtime);
		Long xbtimeLong = getTimeToTheDay(xbtime);
		Long wxstartLong = getTimeToTheDay(wxstart);
		Long wxendLong = getTimeToTheDay(wxend);
		long returntime = (xbtimeLong - sbtimeLong) - (wxendLong - wxstartLong);
		return returntime;
	}

	/**
	 * 获取time对于一天的毫秒数
	 * 
	 * @param time
	 *            (hh:mm:ss)
	 * @return
	 */
	public static Long getTimeToTheDay(String time) throws Exception {
		time = time.replace(" ", "");
		String[] times = time.split(":");
		if (times == null || (times.length != 2 && times.length != 3)) {
			throw new RuntimeException("时间格式不正确!");
		}
		try {
			int h = Integer.parseInt(times[0]);
			int m = Integer.parseInt(times[1]);
			int s = 0;
			if (times.length == 3) {
				s = Integer.parseInt(times[2]);
			}
			long returntime = h * 3600 + m * 60 + s;
			return returntime * 1000;
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("时间格式不正确!");
		}

	}

	/**
	 * 获取基准时间减去一定毫秒数的上班时间(除双休不考虑其他放假情况)
	 * 
	 * @param baseTime
	 *            基准时间 日期格式（yyyy-MM-dd HH:mm:ss）
	 * @param befortime
	 *            基准时间提前的毫秒数
	 * @return
	 */
	public static String beforetime(String baseTime, Long befortime) throws Exception {
		Long datime = getsbtime();// 获取一天上班的时间
		int day = (int) (befortime / datime);// 获取提前毫秒数需跨越的天数，r不计当前天
		long timetoday = befortime - day * datime;// 获取提前毫秒数对于当**工作日**剩下的毫秒数
		String[] baseTimes = baseTime.split(" ");
		if (baseTimes == null || baseTimes.length != 2) {
			throw new RuntimeException("时间格式不正确!");
		}
		long basetimetoday = getTimeToTheDay(baseTimes[1]);// 获取基准时间对于当天的毫秒数
		String sbtime = null;
		String xbtime = null;
		String wxstart = null;
		String wxend = null;
		try {
			sbtime = shangbanTime();
			xbtime = xiabanTime();
			wxstart = wxStartTime();
			wxend = wxEndTime();
		} catch (Exception e) {
			// TODO: handle exception
		}
		long t1 = getTimeToTheDay(sbtime);// 上班时间对于一天开始的长度
		long t2 = getTimeToTheDay(xbtime);// 下班时间对于一天开始的长度
		long t3 = getTimeToTheDay(wxstart);// 午休开始时间对于一天开始的长度
		long t4 = getTimeToTheDay(wxend);// 午休结束时间对于一天开始的长度
		boolean isSbTime = true;// 是否为上班时间
		if (basetimetoday > t2 || basetimetoday < t1) {
			basetimetoday = t2;
			isSbTime = false;
		}
		long returnTimeToDay = 0l;// 返回时间相对于一天的时分秒转换的毫秒数
		if (timetoday == 0) {// 表示提前时间刚好是一天
			returnTimeToDay = basetimetoday;
		}
		while (true) {// 计算returnTimeToDay和day
			if (timetoday == 0) {
				break;
			}
			if (basetimetoday >= t1 && basetimetoday <= t3) {// 早上
				long moretime = basetimetoday - t1;
				if (moretime < timetoday) {
					basetimetoday = t2;// 基准时间对于当天的时间不够，所以往前借一天的时间
					day++;// 借了一天
					timetoday = timetoday - moretime;// 提前时间减去基准时间比上午上班时间多出来的时间
				} else {
					returnTimeToDay = t1 + (moretime - timetoday);
					timetoday = 0l;
				}
			} else if (basetimetoday >= t4 && basetimetoday <= t2) {// 下午
				long moretime = basetimetoday - t4;
				if (moretime < timetoday) {
					basetimetoday = t3;// 基准时间对于当天下午的时间不够，所以向上午借时间
					timetoday = timetoday - moretime;// 提前时间减去基准时间比下午上班时间多出来的时间
				} else {
					returnTimeToDay = t4 + (moretime - timetoday);
					timetoday = 0l;
				}
			}
		}
		Calendar cal = Calendar.getInstance();
		if (!isSbTime) {// 不是上班时间往前一天
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		}
		Date baseTimeData = StringToDate(baseTime, "yyyy-MM-dd");
		long returndate = 0l;
		cal.setTime(baseTimeData);
		while (true) {
			if (day == 0) {
				returndate = cal.getTimeInMillis();
				break;
			}
			int week = cal.get(Calendar.DAY_OF_WEEK);
			if (week == 2) {// 前一天为星期天
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 2);

			} else if (week == 1) {// 前一天为星期六
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
			} else {
				day--;
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
			}
		}
		Date returntime = new Date(returndate + returnTimeToDay);
		String returnString = DateToString(returntime, "yyyy-MM-dd HH:mm:ss");
		return returnString;
	}

	/**
	 * 获取基准时间加上一定毫秒数的上班时间(除双休不考虑其他放假情况)
	 * 
	 * @param baseTime
	 *            基准时间 日期格式（yyyy-MM-dd HH:mm:ss）
	 * @param befortime
	 *            基准时间提前的毫秒数
	 * @return
	 */
	public static String aftertime(String baseTime, Long befortime) throws Exception {
		Long datime = getsbtime();// 获取一天上班的时间
		int day = (int) (befortime / datime);// 获取提前毫秒数需跨越的天数，r不计当前天
		long timetoday = befortime - day * datime;// 获取提前毫秒数对于当**工作日**剩下的毫秒数
		String[] baseTimes = baseTime.split(" ");
		if (baseTimes == null || baseTimes.length != 2) {
			throw new RuntimeException("时间格式不正确!");
		}
		long basetimetoday = getTimeToTheDay(baseTimes[1]);// 获取基准时间对于当天的毫秒数
		String sbtime = null;
		String xbtime = null;
		String wxstart = null;
		String wxend = null;
		try {
			sbtime = shangbanTime();
			xbtime = xiabanTime();
			wxstart = wxStartTime();
			wxend = wxEndTime();
		} catch (Exception e) {
			// TODO: handle exception
		}
		long t1 = getTimeToTheDay(sbtime);// 上班时间对于一天开始的长度
		long t2 = getTimeToTheDay(xbtime);// 下班时间对于一天开始的长度
		long t3 = getTimeToTheDay(wxstart);// 午休开始时间对于一天开始的长度
		long t4 = getTimeToTheDay(wxend);// 午休结束时间对于一天开始的长度
		boolean isSbTime = true;
		if (basetimetoday > t2 || basetimetoday < t1) {// 如果是不是在上班时间内就已下一天的时间开始计算
			basetimetoday = t1;
			isSbTime = false;
		}
		long returnTimeToDay = 0l;// 返回时间相对于一天的时分秒转换的毫秒数
		if (timetoday == 0) {// 表示延后时间刚好是一天
			returnTimeToDay = basetimetoday;
		}
		while (true) {// 计算returnTimeToDay和day
			if (timetoday == 0) {
				break;
			}
			if (basetimetoday >= t1 && basetimetoday <= t3) {// 早上
				long moretime = t3 - basetimetoday;
				if (moretime < timetoday) {
					basetimetoday = t4;// 基准时间对于当天下午的时间加上延后对于一天的时间超出上午时间，基准时间向下午累加
					timetoday = timetoday - moretime;
					// basetimetoday=t2;//基准时间对于当天的时间不够，所以往前借一天的时间
					// day++;//借了一天
					// timetoday=timetoday-moretime;//提前时间减去基准时间比上午上班时间多出来的时间
				} else {
					returnTimeToDay = basetimetoday + timetoday;
					timetoday = 0l;
				}
			} else if (basetimetoday >= t4 && basetimetoday <= t2) {// 下午
				long moretime = t2 - basetimetoday;
				if (moretime < timetoday) {
					basetimetoday = t1;// 基准时间对于当天下午的时间加上延后对于一天的时间超出一个工作日的时间，向后累加一天
					day++;// 累加一天
					timetoday = timetoday - moretime;
					// basetimetoday=t3;//基准时间对于当天下午的时间不够，所以向上午借时间
					// timetoday=timetoday-moretime;//提前时间减去基准时间比下午上班时间多出来的时间
				} else {
					returnTimeToDay = basetimetoday + timetoday;
					timetoday = 0l;
				}
			}
		}
		Calendar cal = Calendar.getInstance();
		if (!isSbTime) {// 不是上班时间天数后移
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		}
		Date baseTimeData = StringToDate(baseTime, "yyyy-MM-dd");
		long returndate = 0l;
		cal.setTime(baseTimeData);
		while (true) {
			if (day == 0) {
				returndate = cal.getTimeInMillis();
				break;
			}
			int week = cal.get(Calendar.DAY_OF_WEEK);
			if (week == 6) {// 后一天为星期六
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 2);
			} else if (week == 7) {// 后一天为星期日
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			} else {
				day--;
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			}
		}
		Date returntime = new Date(returndate + returnTimeToDay);
		String returnString = DateToString(returntime, "yyyy-MM-dd HH:mm:ss");
		return returnString;
	}

	/**
	 * 获取有效的工作时间
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long getYouXiaoTime(String start, String end) throws Exception {
		if (start.equals(end)) {
			return 0l;
		}
		Long startLong = StringToDate(start, "yyyy-MM-dd HH:mm:ss").getTime();
		Long endLong = StringToDate(end, "yyyy-MM-dd HH:mm:ss").getTime();
		int dayTemp = (int) ((endLong - startLong) / (24 * 3600 * 1000));// 获取间隔天数
		int day = 0;
		Date startTimeData = StringToDate(start, "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTimeData);
		while (dayTemp > 0) {
			int week = cal.get(Calendar.DAY_OF_WEEK);
			if (week == 6) {// 后一天为星期六
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 2);
				dayTemp = dayTemp - 2;
			} else if (week == 7) {// 后一天为星期日
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
				dayTemp--;
			} else {
				day++;
				dayTemp--;
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			}
		}
		long outDay = 0l;// 超出一个整天的时间
		outDay = getsbtime() * day;
		// outDay = 8*3600*1000 * day;
		String[] baseTimes = start.split(" ");
		if (baseTimes == null || baseTimes.length != 2) {
			throw new RuntimeException("开始时间格式不正确!");
		}
		String[] endTimes = end.split(" ");
		if (endTimes == null || endTimes.length != 2) {
			throw new RuntimeException("结束时间格式不正确!");
		}
		long starttoday = getTimeToTheDay(baseTimes[1]);// 获取开始时间对于当天的毫秒数
		long endtoday = getTimeToTheDay(endTimes[1]);// 获取开始时间对于当天的毫秒数
		String sbtime = null;
		String xbtime = null;
		String wxstart = null;
		String wxend = null;
		try {
			sbtime = shangbanTime();
			xbtime = xiabanTime();
			wxstart = wxStartTime();
			wxend = wxEndTime();
			// sbtime = "07:30:00";
			// xbtime = "11:30:00";
			// wxstart = "12:30:00";
			// wxend = "16:30:00";
		} catch (Exception e) {
			// TODO: handle exception
		}
		long t1 = getTimeToTheDay(sbtime);// 上班时间对于一天开始的长度
		long t4 = getTimeToTheDay(xbtime);// 下班时间对于一天开始的长度
		long t2 = getTimeToTheDay(wxstart);// 午休开始时间对于一天开始的长度
		long t3 = getTimeToTheDay(wxend);// 午休结束时间对于一天开始的长度
		long lessDay = 0l;
		if (starttoday < t1) {// 开始时间上班之前
			if (endtoday < starttoday) {// 结束时间在开始时间之前
				lessDay = (t2 - starttoday) + (t4 - t3);// 表示为第二天提交
			} else if (endtoday < t3) {
				lessDay = endtoday - starttoday;
			} else if (endtoday >= 3 && endtoday < 4) {// 结束时间为下午下班前
				lessDay = endtoday - starttoday - (t3 - t2);
			} else {// 结束时间为下班以后
				lessDay = t4 - starttoday - (t3 - t2);
			}
		} else if (starttoday >= t1 && starttoday <= t2) {// 开始时间为早上
			if (endtoday < t1) {// 结束时间在上班之前
				lessDay = (t2 - starttoday) + (t4 - t3);// 表示为第二天提交
			} else if (endtoday >= t1 && starttoday > endtoday) {
				lessDay = (t2 - starttoday) + (t4 - t3) + (endtoday - t1);// 表示为第二天提交
			} else if (endtoday < t3 && starttoday <= endtoday) {
				lessDay = endtoday - starttoday;
			} else if (endtoday >= t3 && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - starttoday - (t3 - t2);
			} else {
				lessDay = t4 - starttoday - (t3 - t2);//
			}
		} else if (starttoday > t2 && starttoday < t3) {// 开始时间为中午
			if (endtoday < t1) {// 结束时间在上班之前
				lessDay = t4 - t3;// 表示为第二天提交
			} else if (endtoday < t3 && endtoday <= starttoday) {
				lessDay = (t4 - t3) + (endtoday - t1);// 表示为第二天提交
			} else if (endtoday < t3 && endtoday >= starttoday) {
				lessDay = endtoday - starttoday;
			} else if (endtoday >= t3) {// 结束时间为下午下班前
				lessDay = endtoday - t3;
			} else {
				lessDay = t4 - t3;
			}
		} else if (starttoday >= t3 && starttoday <= t4) {// 开始时间为下午
			if (endtoday < t1) {// 结束时间在上班之前
				lessDay = t4 - starttoday;// 表示为第二天提交
			} else if (endtoday < t3) {
				lessDay = t4 - starttoday + (endtoday - t1);// 表示为第二天提交
			} else if (endtoday >= t3 && endtoday < starttoday) {
				lessDay = t4 - starttoday + (endtoday - t3) + (t2 - t1);// 表示为第二天提交
			} else if (endtoday >= starttoday && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - starttoday;
			} else {
				lessDay = t4 - starttoday;
			}
		} else {// 开始时间为晚上
			if (endtoday < t1) {// 结束时间在上班之前
				if (day > 0) {
					lessDay = 0l;// 时差超过一天则不计算夜间时间
				} else {
					lessDay = 24 * 3600 * 1000 - starttoday;// 就算为整晚工作
				}
			} else if (endtoday >= t1 && endtoday < t3) {
				lessDay = endtoday - t1;
			} else if (endtoday >= t3 && endtoday < starttoday) {
				lessDay = endtoday - t3 + (t2 - t1);
			} else {
				lessDay = endtoday - starttoday;
			}
		}
		return outDay + lessDay;
	}

	/**
	 * 获取有效的上班时间
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long getYouXiaoSbTime(String start, String end) throws Exception {
		if (start.equals(end)) {
			return 0l;
		}
		Long startLong = StringToDate(start, "yyyy-MM-dd HH:mm:ss").getTime();
		Long endLong = StringToDate(end, "yyyy-MM-dd HH:mm:ss").getTime();
		int dayTemp = (int) ((endLong - startLong) / (24 * 3600 * 1000));// 获取间隔天数
		int day = 0;
		Date startTimeData = StringToDate(start, "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTimeData);
		while (dayTemp > 0) {
			int week = cal.get(Calendar.DAY_OF_WEEK);
			if (week == 6) {// 后一天为星期六
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 2);
				dayTemp = dayTemp - 2;
			} else if (week == 7) {// 后一天为星期日
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
				dayTemp--;
			} else {
				day++;
				dayTemp--;
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			}
		}
		long outDay = 0l;// 超出一个整天的时间
		outDay = getsbtime() * day;
		String[] baseTimes = start.split(" ");
		if (baseTimes == null || baseTimes.length != 2) {
			throw new RuntimeException("开始时间格式不正确!");
		}
		String[] endTimes = end.split(" ");
		if (endTimes == null || endTimes.length != 2) {
			throw new RuntimeException("结束时间格式不正确!");
		}
		long starttoday = getTimeToTheDay(baseTimes[1]);// 获取开始时间对于当天的毫秒数
		long endtoday = getTimeToTheDay(endTimes[1]);// 获取开始时间对于当天的毫秒数
		String sbtime = null;
		String xbtime = null;
		String wxstart = null;
		String wxend = null;
		try {
			sbtime = shangbanTime();
			xbtime = xiabanTime();
			wxstart = wxStartTime();
			wxend = wxEndTime();
		} catch (Exception e) {
			// TODO: handle exception
		}
		long t1 = getTimeToTheDay(sbtime);// 上班时间对于一天开始的长度
		long t4 = getTimeToTheDay(xbtime);// 下班时间对于一天开始的长度
		long t2 = getTimeToTheDay(wxstart);// 午休开始时间对于一天开始的长度
		long t3 = getTimeToTheDay(wxend);// 午休结束时间对于一天开始的长度
		long lessDay = 0l;
		if (starttoday < t1) {// 开始时间上班之前
			if (endtoday <= t1) {// 结束时间在开始时间之前
				lessDay = 0;// 前一天结束
			} else if (endtoday < t2) {
				lessDay = endtoday - t1;
			} else if (endtoday >= t2 && endtoday <= t3) {// 结束时间为中午休息时间
				lessDay = t2 - t1;
			} else if (endtoday >= t3 && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - t1 - (t3 - t2);
			} else {// 结束时间为下班以后
				lessDay = t4 - t1 - (t3 - t2);
			}
		} else if (starttoday >= t1 && starttoday <= t2) {// 开始时间为早上
			if (endtoday <= starttoday) {// 结束时间在开始时间之前
				lessDay = 0;//
			} else if (endtoday < t2) {
				lessDay = endtoday - starttoday;
			} else if (endtoday >= t2 && endtoday <= t3) {// 结束时间为中午休息时间
				lessDay = t2 - starttoday;
			} else if (endtoday >= t3 && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - starttoday - (t3 - t2);
			} else {// 结束时间为下班以后
				lessDay = t4 - starttoday - (t3 - t2);
			}
		} else if (starttoday > t2 && starttoday < t3) {// 开始时间为中午
			if (endtoday < t3) {// 结束时间在开始时间之前
				lessDay = 0;//
			} else if (endtoday >= t3 && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - t3;
			} else {// 结束时间为下班以后
				lessDay = t4 - t3;
			}
		} else if (starttoday >= t3 && starttoday <= t4) {// 开始时间为下午
			if (endtoday < starttoday) {// 结束时间在上班之前
				lessDay = 0;
			} else if (endtoday >= starttoday && endtoday <= t4) {// 结束时间为下午下班前
				lessDay = endtoday - starttoday;
			} else {
				lessDay = t4 - starttoday;
			}
		} else {// 开始时间为晚上
			if (endtoday < t1) {// 结束时间在上班之前
				lessDay = 0;
			} else if (endtoday >= t1 && endtoday <= t2) {
				lessDay = endtoday - t1;
			} else if (endtoday > t2 && endtoday <= t3) {
				lessDay = t2 - t1;
			} else if (endtoday >= t3 && endtoday < starttoday) {
				lessDay = endtoday - t1 + (t3 - t2);
			} else {
				lessDay = t4 - t1 + (t3 - t2);
			}
		}
		return outDay + lessDay;
	}

	/**
	 * 比较两个时间的前后time1前就返回false否则就返回true
	 * 
	 * @param time1
	 * @param fromart1
	 * @param time2
	 * @param fromart2
	 * @return time1 > time2 返回 true time1 < time2返回false
	 */
	public static boolean compareTime(String time1, String fromart1, String time2, String fromart2) {
		long longtime1 = StringToDate(time1, fromart1).getTime();
		long longtime2 = StringToDate(time2, fromart2).getTime();
		if (longtime1 - longtime2 >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * 将一个对象复制到一个新对象，并将原始对象的所有数据复制到新对象
	 * 
	 * @param object
	 *            对象
	 * @param obj
	 *            不复制值的属性名称(可变参数)
	 * @return Object 新对象
	 * @throws Exception
	 */
	public static Object copyObjectToNewObject(Object object, Object... obj) throws Exception {
		if (object != null) {
			Class<?> demo = Class.forName(object.getClass().getName());// 通过包名找到对象
			Object newObject = demo.newInstance();// 创建一个新的对象

			Field[] fields = object.getClass().getDeclaredFields();
			out: for (Field field : fields) {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), object.getClass());
				Method method = pd.getReadMethod();// 获得get方法
				Object propertyObj = method.invoke(object);// 获得get方法的值
				String methodName = pd.getName();// 获得属性名称
				if (obj != null) {
					for (Object otherName : obj) {
						if (methodName.equals(otherName)) {
							continue out;
						}
					}
				}
				Field field2 = demo.getDeclaredField(methodName);// 根据属性名称创建Field属性
				field2.setAccessible(true);
				field2.set(newObject, propertyObj);// 赋值
			}
			return newObject;
		}
		return null;
	}

	/***
	 * 将String 转换为Date
	 * 
	 * @param dateTime
	 *            String类型的时间
	 * @param timeFormat
	 *            转换格式(默认为:yyyy-MM-dd HH:mm:ss)
	 * @return
	 * @throws ParseException
	 */
	public static Date StringToDate(String dateTime, String timeFormat) {
		if (dateTime != null && dateTime.length() > 0) {
			if (timeFormat == null || timeFormat.length() <= 0) {
				timeFormat = "yyyy-MM-dd HH:mm:ss";
			}
			try {
				return new SimpleDateFormat(timeFormat).parse(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/***
	 * 将Date转换成String
	 * 
	 * @param date
	 *            时间
	 * @param timeFormat
	 *            转换格式(默认为:yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String DateToString(Date date, String timeFormat) {
		if (date != null) {
			if (timeFormat == null || timeFormat.length() <= 0) {
				timeFormat = "yyyy-MM-dd HH:mm:ss";
			}
			return new SimpleDateFormat(timeFormat).format(date);
		}
		return null;
	}

	/***
	 * 获得时间(如果timeFormat为空,返回日期格式为'yyyy-MM-dd HH:mm:ss'的时间)
	 * 
	 * @param G
	 *            年代标志符
	 * @param y
	 *            年
	 * @param M
	 *            月
	 * @param d
	 *            日
	 * @param h
	 *            时 在上午或下午 (1~12)
	 * @param H
	 *            时 在一天中 (0~23)
	 * @param m
	 *            分
	 * @param s
	 *            秒
	 * @param S
	 *            毫秒
	 * @param E
	 *            星期
	 * @param D
	 *            一年中的第几天
	 * @param F
	 *            一月中第几个星期几
	 * @param w
	 *            一年中第几个星期
	 * @param W
	 *            一月中第几个星期
	 * @param a
	 *            上午 / 下午 标记符
	 * @param k
	 *            时 在一天中 (1~24)
	 * @param K
	 *            时 在上午或下午 (0~11)
	 * @param z
	 *            时区
	 */
	public static String getDateTime(String timeFormat) {
		if (timeFormat != null && timeFormat.length() > 0)
			return new SimpleDateFormat(timeFormat).format(new Date());
		else
			return getDateTime();
	}

	/***
	 * 获得时间(返回日期格式为'yyyy-MM-dd HH:ss:mm'的时间)
	 */
	public static String getDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/***
	 * 获得时间(返回日期格式为'yyyy-MM-dd'的时间)
	 */
	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	/***
	 * @author Li_Cong 获得时间(返回日期格式为'yyyy-MM-dd HH:ss:mm'的时间)的前后n分钟
	 */
	public static String getDateTime(int n) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(new Date().getTime() + (n * 60 * 1000)));
	}

	/***
	 * 获得上个月的日期
	 * 
	 * @param timeFormat
	 *            时间(2013-05-16 15:17:00)
	 * @return
	 */
	public static String getLastMonth(String timeFormat) {
		String mouth = new SimpleDateFormat(timeFormat).format(new Date());
		int yy = Integer.parseInt(mouth.substring(0, 4));
		int mm = Integer.parseInt(mouth.substring(5, 7));
		String other = mouth.substring(7);
		if (mm == 1) {
			mm = 12;
			yy--;
		} else {
			mm--;
		}
		if (mm < 10) {
			mouth = yy + "-0" + mm + other;
		} else {
			mouth = yy + "-" + mm + other;
		}
		return mouth;
	}

	/***
	 * 获得下个月的日期
	 * 
	 * @param timeFormat
	 *            时间(2013-05-16 15:17:00)
	 * @return
	 */
	public static String getNextMonth(String timeFormat) {
		String mouth = new SimpleDateFormat(timeFormat).format(new Date());
		try {
			int yy = Integer.parseInt(mouth.substring(0, 4));
			int mm = Integer.parseInt(mouth.substring(5, 7));
			String other = mouth.substring(7);
			if (mm == 12) {
				mm = 1;
				yy++;
			} else {
				mm++;
			}
			if (mm < 10) {
				mouth = yy + "-0" + mm + other;
			} else {
				mouth = yy + "-" + mm + other;
			}
		} catch (Exception e) {
			int yy = Integer.parseInt(mouth.substring(0, 4));
			int mm = Integer.parseInt(mouth.substring(4, 6));
			String other = mouth.substring(6);
			if (mm == 12) {
				mm = 1;
				yy++;
			} else {
				mm++;
			}
			if (mm < 10) {
				mouth = yy + "0" + mm + other;
			} else {
				mouth = yy + "" + mm + other;
			}
		}
		return mouth;
	}

	/***
	 * 月份加一
	 * 
	 * @author Li_Cong
	 * 
	 * @param timeFormat
	 *            时间(2015-11-18 15:17:00)
	 * @return
	 */
	public static String getNextMonth3(String timeFormat) {
		String mouth = new SimpleDateFormat(timeFormat).format(new Date());
		int yy = Integer.parseInt(mouth.substring(0, 4));
		int mm = Integer.parseInt(mouth.substring(5, 7));
		// String other = mouth.substring(7);
		if (mm == 12) {
			mm = 1;
			yy++;
		} else {
			mm++;
		}
		if (mm < 10) {
			mouth = yy + "-0" + mm;
		} else {
			mouth = yy + "-" + mm;
		}
		return mouth;
	}

	/***
	 * 获得下个月的日期
	 * 
	 * @param mouth
	 *            当前月 时间(2013-05-16 15:17:00)
	 * @return
	 */
	public static String getNextMonth2(String mouth) {
		int yy = Integer.parseInt(mouth.substring(0, 4));
		int mm = Integer.parseInt(mouth.substring(5, 7));
		String other = mouth.substring(7);
		if (mm == 12) {
			mm = 1;
			yy++;
		} else {
			mm++;
		}
		if (mm < 10) {
			mouth = yy + "-0" + mm + other;
		} else {
			mouth = yy + "-" + mm + other;
		}
		return mouth;
	}

	/***
	 * 获得本月月份
	 * 
	 * @param mouth
	 *            当前月 时间(2013-05-16 15:17:00)
	 * @return
	 */
	public static String getThisMonth2(String mouth) {
		return mouth.substring(0, 7);
	}

	/**
	 * 根据Calendar获得加减天数
	 * 
	 * @param date
	 *            时间(如果为空返回当前时间)
	 * @param dayCount
	 *            加减天数(如果为空返回当前时间)
	 * @return Date
	 * @throws ParseException
	 */
	public static Date getCalendarDate(Date date, int dayCount) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 日期加一天
		Calendar calendar = Calendar.getInstance();
		if (date == null) {
			date = calendar.getTime();
		}
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dayCount);
		return sdf.parse(sdf.format(calendar.getTime()));
	}

	/***
	 * 计算两个日期之间相差的秒数
	 * 
	 * @param firstDate
	 *            第一个时间
	 * @param startDateFormat
	 *            第一个时间格式
	 * @param lastDate
	 *            另一个时间
	 * @param endDateFormat
	 *            另一个时间格式
	 * @return 想减的毫秒数(负数说明lastDate < firstDate)
	 * @throws ParseException
	 */
	public static Long getDateDiff(String firstDate, String startDateFormat, String lastDate, String endDateFormat)
			throws ParseException {
		if (firstDate == null || startDateFormat == null || lastDate == null || endDateFormat == null) {
			throw new NullPointerException("参数不能为空!");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(startDateFormat);
		Date startDateTime = sdf.parse(firstDate);
		SimpleDateFormat sdf2 = new SimpleDateFormat(endDateFormat);
		Date endDateTime = sdf2.parse(lastDate);
		return (Long) (endDateTime.getTime() - startDateTime.getTime()) / 1000;
	}

	/***
	 * 获得登录用户信息
	 * 
	 * @return Users
	 */
	public static Users getLoginUser() {
		if (ActionContext.getContext() != null) {
			return (Users) ActionContext.getContext().getSession().get(TotalDao.users);// 获得登录用户信息
		}
		return null;
	}

	/***
	 * 
	 * 
	 * @param file
	 *            文件 (必须填写)
	 * @param fileFileName
	 *            上传文件时的名称 (为空时 必须填写新的文件名)
	 * @param newFileName
	 *            新的文件名 (为空时 默认为yyyyMMddHHmmss+上传时的文件格式)
	 * @param uploadPath
	 *            保存地址 (必须填写)
	 * @param backupPath
	 *            备份地址 (为空时 则不对文件进行备份操作)
	 */
	public static String UploadFile(File file, String fileFileName, String newFileName, String uploadPath,
			String backupPath) {
		// 上传文件
		if (file != null && uploadPath != null && uploadPath.length() > 0) {
			if (newFileName == null || newFileName.length() <= 0) {
				if (fileFileName != null && fileFileName.length() > 0) {
					newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
							+ fileFileName.substring(fileFileName.lastIndexOf("."), fileFileName.length());
				} else {
					return "error";
				}
			}

			// 上传文件到服务器
			String fileRealPath = ServletActionContext.getServletContext().getRealPath(uploadPath) + "/" + newFileName;
			File uploadFile = new File(fileRealPath);
			try {
				FileCopyUtils.copy(file, uploadFile);
			} catch (Exception e) {
				return "上传文件失败!";
			}

			if (backupPath != null && backupPath.length() > 0) {
				// 备份文件
				String beiFenfileRealPath = backupPath + "/" + newFileName;
				File beiFenFile = new File(beiFenfileRealPath);
				try {
					FileCopyUtils.copy(file, beiFenFile);
				} catch (IOException e) {
					return "备份文件失败!";
				}
			}
			return newFileName;
		}
		return "error";
	}

	/***
	 * 获得当前时间是第几周
	 * 
	 * @return
	 */
	public static int getNowWeek() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/***
	 * 获得当前时间是一周中的第几天（第0天为周日);
	 * 
	 * @return
	 */
	public static int getNowday() {
		return printFieldDate("WD") - 1;
	}

	/**
	 * 判断指定日期是星期几
	 * 
	 * @param pTime
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static String dayForWeek(String pTime, String format) throws Exception {
		if (pTime == null) {
			pTime = getDateTime();
		} else if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sft = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(sft.parse(pTime));
		String dayForWeek = "";
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = "星期日";
		} else if (week == 1) {
			dayForWeek = "星期一";
		} else if (week == 2) {
			dayForWeek = "星期二";
		} else if (week == 3) {
			dayForWeek = "星期三";
		} else if (week == 4) {
			dayForWeek = "星期四";
		} else if (week == 5) {
			dayForWeek = "星期五";
		} else if (week == 6) {
			dayForWeek = "星期六";
		}
		return dayForWeek;
	}

	/**
	 * 判断指定日期是星期几
	 * 
	 * @param pTime
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static int dayForWeek1(String pTime, String format) throws Exception {
		if (pTime == null) {
			pTime = getDateTime();
		} else if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sft = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(sft.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	/***
	 * 获得当前时间的上一周
	 * 
	 * @return yyyy年xx周
	 */
	public static String getLastWeek() {
		int week = getNowWeek();
		int year = Integer.parseInt(Util.getDateTime("yyyy"));
		if (week == 1) {
			/***** 获得当前时间的上一周 ***/
			Calendar curr = Calendar.getInstance();
			curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) - 7);
			Date weekDate = curr.getTime();
			week = getNowWeek(weekDate);// 获得该周是第几周
			year = Integer.parseInt(Util.DateToString(weekDate, "yyyy"));
		} else {
			week--;
		}
		return year + "年" + week + "周";
	}

	/***
	 * 获得某时间年份第几周
	 * 
	 * @return yyyy年xx周
	 */
	public static String getWeek(String date, String format) {
		int week = 0;
		int year = 0;
		if (date == null || date.length() == 0) {
			week = getNowWeek();
			year = Integer.parseInt(Util.getDateTime("yyyy"));
			String week_ = week + "";
			if (week < 10) {
				week_ = "0" + week;
			}
			return year + "年" + week_ + "周";
		}
		if (format == null || format.length() == 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		Date date1 = StringToDate(date, format);
		week = getNowWeek(date1);
		year = Integer.parseInt(DateToString(date1, "yyyy"));
		String week_ = week + "";
		if (week < 10) {
			week_ = "0" + week;
		}
		return year + "年" + week_ + "周";
	}
	public static String getWeek1(String date, String format) {
		int week = 0;
		int year = 0;
		if (date == null || date.length() == 0) {
			week = getNowWeek();
			year = Integer.parseInt(Util.getDateTime("yyyy"));
			String week_ = week + "";
			return year + "年" + week_ + "周";
		}
		if (format == null || format.length() == 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		Date date1 = StringToDate(date, format);
		week = getNowWeek(date1);
		year = Integer.parseInt(DateToString(date1, "yyyy"));
		String week_ = week + "";
		return year + "年" + week_ + "周";
	}

	/***
	 * 获得当前时间是第几周
	 * 
	 * @return
	 */
	public static int getNowWeek(Date dateTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(dateTime);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/***
	 * 计算两个时间之间的工作时长(s)
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public static Long getWorkTime(Date startDate, Date endDate) {
		if (startDate != null && endDate != null) {
			long datetime = endDate.getTime() - startDate.getTime();
			int startdd = Integer.parseInt(Util.DateToString(startDate, "dd"));
			int enddd = Integer.parseInt(Util.DateToString(endDate, "dd"));
			datetime = (long) (datetime - (enddd - startdd) * (15.5 * 3600 * 1000));
			return datetime;
		}
		return null;
	}

	/***
	 * 计算两个时间之间的工作时长(s)
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public static Long getWorkTime(String startDate, String endDate) {
		if (startDate != null && endDate != null) {
			Date startDateTime = Util.StringToDate(startDate, "yyyy-MM-dd HH:mm:ss");
			Date endDateTime = Util.StringToDate(endDate, "yyyy-MM-dd HH:mm:ss");
			return getWorkTime(startDateTime, endDateTime);
		}
		return null;
	}

	/**
	 * @param startDate
	 *            String 开始时间 (yyyy-MM-dd HH:mm:ss)
	 * @param endDate
	 *            String 结束时间 (yyyy-MM-dd HH:mm:ss)
	 * @return 毫秒数
	 * @author Li_Cong
	 */
	public static Long getWorkTime1(String startDate, String endDate) {
		if (startDate != null && endDate != null) {
			Date startDateTime = Util.StringToDate(startDate, "yyyy-MM-dd HH:mm:ss");
			Date endDateTime = Util.StringToDate(endDate, "yyyy-MM-dd HH:mm:ss");
			return endDateTime.getTime() - startDateTime.getTime();
		}
		return null;
	}

	/**
	 * @param startDate
	 *            开始时间 (HH:mm)
	 * @param endDate
	 *            结束时间 (HH:mm)
	 * @return 毫秒数
	 * @author Li_Cong
	 */
	public static Long getWorkTime2(String startDate, String endDate) {
		if (startDate != null && endDate != null) {
			Date startDateTime = Util.StringToDate(startDate, "HH:mm");
			Date endDateTime = Util.StringToDate(endDate, "HH:mm");
			return endDateTime.getTime() - startDateTime.getTime();
		}
		return null;
	}

	/**
	 * 获取当前登陆的供应商信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ZhUser getCurrzhUser() {
		// 获得totalDao
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		Users loginUser = Util.getLoginUser();
		if (loginUser != null && loginUser.getDept() != null && loginUser.getDept().equals("供应商")) {
			List<ZhUser> zhUserList = totalDao.query("from ZhUser where userid=?", loginUser.getId());
			if (zhUserList.size() > 0) {
				return zhUserList.get(0);
			}
		}
		return null;
	}

	// private static TotalDao createTotol() {
	// // 获得hibernateTemplate对象，并赋值给totalDao
	// HibernateTemplate ht = TotalDaoImpl.findHibernateTemplate();
	// TotalDao totalDao = new TotalDaoImpl();
	// ((HibernateDaoSupport) totalDao).setHibernateTemplate(ht);
	// CircuitRunServerImpl ccs = new CircuitRunServerImpl();
	// ccs.setTotalDao(totalDao);
	// return totalDao;
	// }

	/***
	 * 比较当前时间是否在两个时间中间
	 * 
	 * @param date1
	 *            开始时间
	 * @param date2
	 *            结束时间
	 * @param date3
	 *            比较时间
	 * @return date3 为null时 比较当前时间；不为null时比较指定时间;
	 */
	public static boolean betweenTime(Date date1, Date date2, Date date3) {
		boolean bool1 = false;
		boolean bool2 = false;
		boolean bool = false;

		try {
			if (date3 == null) {
				bool1 = new Date().after(date1);
				bool2 = new Date().before(date2);
			} else {
				bool1 = date3.after(date1);
				bool2 = date3.before(date2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bool1 && bool2) {
			bool = true;
		}
		return bool;
	}

	/***
	 * 根据指定日期获得对应加减值
	 * 
	 * @param specifiedDay
	 *            指定时间
	 * @param dateStyle
	 *            时日调整类型(MONTH：2------DAY:5-----HOUR:10----MINUTE:12----SECOND:13
	 *            )
	 * @param num
	 *            加减值
	 * @return
	 */
	public static String getCalendarModified(String specifiedDay, int dateStyle, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(specifiedDay);
		} catch (ParseException e) {
			try {
				date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
			} catch (ParseException e1) {
				e1.printStackTrace();
				return "";
			}
		}
		c.setTime(date);
		c.add(dateStyle, num);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获得指定时间的前后num分钟
	 * 
	 * @param specifiedDay
	 *            (HH:mm:ss) 起始时间
	 * @param num
	 *            指定是时间 （分）
	 * @return
	 * @author Li_Cong 2016/7/11
	 */
	public static String getminuteAfter(String specifiedDay, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("HH:mm:ss").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		c.add(Calendar.MINUTE, num);

		String dayAfter = new SimpleDateFormat("HH:mm:ss").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获得指定时间的前后num分钟
	 * 
	 * @param specifiedDay
	 *            (HH:mm) 起始时间
	 * @param num
	 *            指定是时间 （分）
	 * @return
	 * @author Li_Cong 2017/11/23
	 */
	public static String getminuteAfter1(String specifiedDay, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("HH:mm").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		c.add(Calendar.MINUTE, num);

		String dayAfter = new SimpleDateFormat("HH:mm").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获得指定日期的后number分钟
	 * 
	 * @param specifiedDay
	 *            起始时间
	 * @param num
	 *            指定是时间 （分）
	 * @return
	 * @author Li_Cong
	 */
	public static String getSpecifiedminuteAfter(String specifiedDay, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		c.add(Calendar.MINUTE, num);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
		return dayAfter;
	}

	/**
	 * @param specifiedDay
	 * @return
	 * @author Li_Cong 获得指定日期的前后mun天 yyyy-MM-dd
	 */
	public static String getSpecifiedDayAfter(String specifiedDay, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + (num));

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	/**
	 * @param specifiedDay
	 * @return
	 * @author Li_Cong 获得指定日期的前后mun天 yyyy-MM-dd HH:mm:ss
	 */
	public static String getSpecifiedDayBeforeday(String specifiedDay, int num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + (num));

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
		return dayAfter;
	}

	/***
	 * 十六进制转换为String类型
	 * 
	 * @param bs
	 * @return
	 */
	public static String byteArrayToHexString(byte[] bs) {
		String HEX_CODE = "0123456789ABCDEF";
		int _byteLen = bs.length;
		StringBuilder _result = new StringBuilder(_byteLen * 2);
		for (int i = 0; i < _byteLen; i++) {
			int n = bs[i] & 0xFF;
			_result.append(HEX_CODE.charAt(n >> 4));
			_result.append(HEX_CODE.charAt(n & 0x0F));
		}
		return String.valueOf(_result);
	}

	/***
	 * 十六进制转换为String类型
	 * 
	 * 
	 * @param bs
	 * @return
	 */
	public static String byteArrayToHexStringK(byte[] bs) {
		String HEX_CODE = "0123456789ABCDEF";
		int _byteLen = bs.length;
		StringBuilder _result = new StringBuilder(_byteLen * 2);
		for (int i = 0; i < _byteLen; i++) {
			int n = bs[i] & 0xFF;
			_result.append(HEX_CODE.charAt(n >> 4));
			_result.append(HEX_CODE.charAt(n & 0x0F));
			_result.append(" ");
		}
		return String.valueOf(_result);
	}

	/***
	 * 十六进制转换为String类型
	 * 
	 * 
	 * @param bs
	 * @return
	 * @throws Exception
	 */
	public static String byteArrayToString(byte[] bs) {
		int _byteLen = bs.length;
		StringBuilder _result = new StringBuilder(_byteLen);
		for (int i = 0; i < _byteLen; i++) {
			int n = bs[i] & 0xFF;
			_result.append(UtilHexString.byteAsciiToChar(n));
		}
		return String.valueOf(_result);
	}

	/**
	 * int _byteLen = bs[i] & 0xFF byte类型的数字要&0xff再赋值给int类型，其本质原因就是想保持二进制补码的一致性。
	 * 当byte要转化为int的时候，高的24位必然会补1，这样，其二进制补码其实已经不一致了，&0xff可以将高的24位置为0，
	 * 低8位保持原样。这样做的目的就是为了保证二进制数据的一致性。
	 */

	/**
	 * 传入一个字符串返回一个带单引号的字符 2015-07-30 li_cong
	 */
	public static String szhuans(String string) {
		String[] s = string.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			String str = s[i].replaceAll(" ", "");
			if (i == 0) {
				sb.append("'" + str + "'");
			} else {
				sb.append(",'" + str + "'");
			}
		}
		String s1 = sb.toString();
		return s1;
	}

	/**
	 * @param time
	 *            传入时间
	 * @return 判断time是否在三个时间段之内(可出门时间)
	 * @author Li_Cong
	 */
	public static boolean outMenTime(String time) {
		// Integer w = printFieldDate("wd");
		// if (w != 1 && w != 7) {
		// boolean a0 = Util.compareTime(time, "HH:mm:ss", "07:30:00",
		// "HH:mm:ss");
		// boolean a1 = Util.compareTime("08:00:00", "HH:mm:ss", time,
		// "HH:mm:ss");
		boolean a2 = Util.compareTime(time, "HH:mm:ss", "12:00:00", "HH:mm:ss");
		boolean a3 = Util.compareTime("13:00:00", "HH:mm:ss", time, "HH:mm:ss");
		boolean a4 = Util.compareTime(time, "HH:mm:ss", "17:00:00", "HH:mm:ss");
		boolean a5 = Util.compareTime("23:59:59", "HH:mm:ss", time, "HH:mm:ss");
		boolean a6 = Util.compareTime(time, "HH:mm:ss", "00:00:00", "HH:mm:ss");
		boolean a7 = Util.compareTime("08:00:00", "HH:mm:ss", time, "HH:mm:ss");
		boolean fan = (a2 && a3) || (a4 && a5) || (a6 && a7);
		return fan;
		// } else {
		// return false;
		// }
	}

	/**
	 * @param time
	 * @return
	 * @author Li_Cong 可进门时间
	 */
	public static boolean InMenTime(String time) {
		// Integer w = printFieldDate("wd");
		// if (w != 1 && w != 7) {
		boolean a0 = Util.compareTime(time, "HH:mm:ss", "03:00:00", "HH:mm:ss");
		boolean a1 = Util.compareTime("17:30:00", "HH:mm:ss", time, "HH:mm:ss");
		boolean fan = a0 && a1;
		return fan;
		// } else {
		// return false;
		// }
	}

	/**
	 * @param month
	 *            (yyyy-MM)
	 * @return 根据日期获取指定月份的天数；date为null则获取当前月份的天数
	 * @author wangxiaofei;
	 */
	public static int getMonthofday(String date) {
		Calendar a = Calendar.getInstance();
		if (date != null && date.length() > 0) {
			int year = Integer.parseInt(date.substring(0, 4));
			int month = Integer.parseInt(date.substring(5, 7));
			a.set(Calendar.YEAR, year);
			a.set(Calendar.MONTH, month - 1);
		}
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * @param str
	 *            获得当前类型("Y/y"--年 "M/m"--月 "D/d"--日 "W/w"--周 "MW/mw"--月周
	 *            "YD/yd"--年天 "WD/wd"--周天 )
	 * @return
	 * @author Li_Cong
	 */
	public static Integer printFieldDate(String str) {
		Integer i = null;
		Calendar cNow = Calendar.getInstance();
		// 先用Date类型输出验证
		// SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		// df.applyPattern("yyyy-MM-dd HH:mm:ss");
		// System.out.println("标准日期:" + df.format(new Date()));
		// System.out.print("日期:" + cNow.get(Calendar.DATE) + "\t");
		// System.out.print("小时:" + cNow.get(Calendar.HOUR) + "\t");
		// System.out.print("分钟:" + cNow.get(Calendar.MINUTE) + "\t");
		// System.out.println("秒钟:" + cNow.get(Calendar.SECOND));
		// System.out.println("一年中的天数:" + cNow.get(Calendar.DAY_OF_YEAR));
		// System.out.println("一年中的周数:" + cNow.get(Calendar.WEEK_OF_YEAR));
		// // 即本月的第几周
		// System.out.println("一月中的周数:" + cNow.get(Calendar.WEEK_OF_MONTH));
		// // 即一周中的第几天(这里是以周日为第一天的)
		// System.out.println("一周中的天数:" + cNow.get(Calendar.DAY_OF_WEEK));
		// 本年的第几天,在计算时间间隔的时候有用
		if ("Y".equals(str) || "y".equals(str)) {
			i = cNow.get(Calendar.YEAR);
		} else if ("M".equals(str) || "m".equals(str)) {
			i = cNow.get(Calendar.MONTH) + 1;
		} else if ("D".equals(str) || "d".equals(str)) {
			i = cNow.get(Calendar.DATE);
		} else if ("W".equals(str) || "w".equals(str)) {
			i = cNow.get(Calendar.WEEK_OF_YEAR);
		} else if ("MW".equals(str) || "mw".equals(str)) {
			i = cNow.get(Calendar.WEEK_OF_MONTH);
		} else if ("YD".equals(str) || "yd".equals(str)) {
			i = cNow.get(Calendar.DAY_OF_YEAR);
		} else if ("WD".equals(str) || "wd".equals(str)) {
			i = cNow.get(Calendar.DAY_OF_WEEK);
		} else {
			i = cNow.get(Calendar.DAY_OF_WEEK);
		}
		return i;
	}

	public static Integer printFieldDate(String str, String time, String timeFormat) {
		Integer i = null;
		Calendar cNow = Calendar.getInstance();
		if (time != null && time.length() > 0) {
			if (timeFormat == null || timeFormat.length() == 0) {
				timeFormat = "yyyy-MM-dd";
			}
			cNow.setTime(Util.StringToDate(time, timeFormat));
		}
		// 先用Date类型输出验证
		// SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		// df.applyPattern("yyyy-MM-dd HH:mm:ss");
		// System.out.println("标准日期:" + df.format(new Date()));
		// System.out.print("日期:" + cNow.get(Calendar.DATE) + "\t");
		// System.out.print("小时:" + cNow.get(Calendar.HOUR) + "\t");
		// System.out.print("分钟:" + cNow.get(Calendar.MINUTE) + "\t");
		// System.out.println("秒钟:" + cNow.get(Calendar.SECOND));
		// System.out.println("一年中的天数:" + cNow.get(Calendar.DAY_OF_YEAR));
		// System.out.println("一年中的周数:" + cNow.get(Calendar.WEEK_OF_YEAR));
		// // 即本月的第几周
		// System.out.println("一月中的周数:" + cNow.get(Calendar.WEEK_OF_MONTH));
		// // 即一周中的第几天(这里是以周日为第一天的)
		// System.out.println("一周中的天数:" + cNow.get(Calendar.DAY_OF_WEEK));
		// 本年的第几天,在计算时间间隔的时候有用
		if ("Y".equals(str) || "y".equals(str)) {
			i = cNow.get(Calendar.YEAR);
		} else if ("M".equals(str) || "m".equals(str)) {
			i = cNow.get(Calendar.MONTH) + 1;
		} else if ("D".equals(str) || "d".equals(str)) {
			i = cNow.get(Calendar.DATE);
		} else if ("W".equals(str) || "w".equals(str)) {
			i = cNow.get(Calendar.WEEK_OF_YEAR);
		} else if ("MW".equals(str) || "mw".equals(str)) {
			i = cNow.get(Calendar.WEEK_OF_MONTH);
		} else if ("YD".equals(str) || "yd".equals(str)) {
			i = cNow.get(Calendar.DAY_OF_YEAR);
		} else if ("WD".equals(str) || "wd".equals(str)) {
			i = cNow.get(Calendar.DAY_OF_WEEK);
		} else {
			i = cNow.get(Calendar.DAY_OF_WEEK);
		}
		return i;
	}

	/**
	 * @param mss
	 *            毫秒数
	 * @return 返回日期时间 天时分秒
	 * @author Li_Cong
	 */
	public static String formatDuring(long mss) {
		long year = mss / (1000 * 60 * 60 * 24 * 365l);
		long month = (mss % (1000 * 60 * 60 * 24 * 365l) / (1000 * 60 * 60 * 24 * 30l));
		long days = (mss % (1000 * 60 * 60 * 24 * 30l) / (1000 * 60 * 60 * 24));
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		if (year > 0) {
			return year + "年" + month + "月" + days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
		} else if (month > 0 && year == 0) {
			return month + "月" + days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
		} else if (days > 0 && year == 0 && month == 0) {
			return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
		} else if (hours > 0 && year == 0 && month == 0 && days == 0) {
			return hours + "小时" + minutes + "分" + seconds + "秒";
		} else if (minutes > 0 && year == 0 && month == 0 && days == 0 && hours == 0) {
			return minutes + "分" + seconds + "秒";
		} else if (seconds > 0 && year == 0 && month == 0 && minutes == 0 && days == 0 && hours == 0) {
			return seconds + "秒";
		} else {
			return "1秒";
		}
	}

	/**
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 时间差(返回日期时间 天时分秒)
	 * @author Li_Cong 计算使用时间
	 *
	 *         bug:开始小于结束 会返回“1秒”
	 */
	public static String timeDifference(String startTime, String endTime) {
		return formatDuring(getWorkTime1(startTime, endTime));
	}

	/**
	 * 检测是否是移动设备访问
	 * 
	 * @param userAgent
	 *            浏览器标识
	 * @return true:移动设备接入，false:pc端接入
	 * @Title: check
	 */
	public static boolean check(String userAgent) {
		// \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
		// 字符串在编译时会被转码一次,所以是 "\\b"
		// \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
		String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i" + "|windows (phone|ce)|blackberry"
				+ "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp" + "|laystation portable)|nokia|fennec|htc[-_]"
				+ "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
		String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

		// 移动设备正则匹配：手机端、平板
		Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
		Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);
		if (null == userAgent) {
			userAgent = "";
		}
		// 匹配
		Matcher matcherPhone = phonePat.matcher(userAgent);
		Matcher matcherTable = tablePat.matcher(userAgent);
		if (matcherPhone.find() || matcherTable.find()) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * 检查记录的登录方式
	 * 
	 * @return true:移动设备接入，false:pc端接入
	 */
	public static boolean chackMobileOrPc() {
		if (ActionContext.getContext() != null) {
			return ActionContext.getContext().getSession().get("mobileOrPc").equals("mobile");
		} else {
			return false;
		}
	}

	/**
	 * @param num
	 *            随机num位数字
	 * @return
	 * @author Li_Cong
	 */
	public static String yanNumber(Integer num) {
		Random ran = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (i == 0 && num == 6)
				builder.append(ran.nextInt(8) + 1 + "");
			else if (i == 0 && num == 10)
				builder.append(ran.nextInt(3) + "");
			else
				builder.append(ran.nextInt(10) + "");
		}
		if ("111111".equals(builder.toString()) || "222222".equals(builder.toString())
				|| "333333".equals(builder.toString()) || "444444".equals(builder.toString())
				|| "555555".equals(builder.toString()) || "666666".equals(builder.toString())
				|| "777777".equals(builder.toString()) || "888888".equals(builder.toString())
				|| "999999".equals(builder.toString()) || "123456".equals(builder.toString())
				|| "456789".equals(builder.toString())) {
			return ran.nextInt(8) + 1 + "" + ran.nextInt(10) + ran.nextInt(10) + ran.nextInt(10) + ran.nextInt(10)
					+ ran.nextInt(10);
		}
		return builder.toString();
	}

	/**
	 * 字符串转16进制字符串
	 * 
	 * @param str
	 * @return
	 * @author Li_Cong
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * @return 访问用户ip
	 * @author Li_Cong
	 */
	public static String hqIp() {
		HttpServletRequest request = ServletActionContext.getRequest();
		// HttpServletResponse response = ServletActionContext.getResponse();
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
		return ip;
	}

	/**
	 * 打开或关闭灯/车位方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令
	 * @return
	 */
	public static String SendZ(String ip, Integer port, int openOrClose) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			bw.write(openOrClose);
			// bw.newLine();
			bw.flush();
			bw.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * Rfid通讯
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令
	 * @return
	 */
	public static String SendRfid(String ip, Integer port) {
		// TODO Auto-generated method stub
		Socket s;
		String mess = "true";
		try {
			s = new Socket(ip, port);
			int i = 0;
			PrintStream out = null;
			InputStream in = null;
			BufferedInputStream bis = null;
			while (i < 20) {
				out = new PrintStream(s.getOutputStream());
				out.write(new byte[] { (byte) 0xAA, 0x00, 0x22, 0x00, 0x00, 0x22, (byte) 0x8E });
				out.flush();
				in = s.getInputStream();
				bis = new BufferedInputStream(in);
				// 先接收接收第一个字符 用做标识u
				i++;
				byte[] one = new byte[1];//
				bis.read(one);// 读取数据
				String mes = Util.byteArrayToHexString(one);
				if ("AA".equals(mes)) {
					byte[] tow = new byte[1];//
					bis.read(tow);// 读取数据
					String mes1 = Util.byteArrayToHexString(tow);
					if ("02".equals(mes1)) {
						byte[] three1 = new byte[6];//
						Thread.sleep(10);
						bis.read(three1);// 读取数据
						byte[] three = new byte[12];//
						Thread.sleep(12);
						bis.read(three);// 读取数据
						mess = Util.byteArrayToHexString(three);
						System.out.println("----------" + mess + "----------");
						break;
					} else {
						byte[] three = new byte[6];//
						Thread.sleep(6);
						bis.read(three);// 读取数据
						System.out.println("----------" + Util.byteArrayToHexString(three) + "----------");
					}
				}
			}
			out.close();
			bis.close();
			in.close();
			s.close();
			return mess;
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 打开
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令
	 * @return
	 * @author 鸿斌
	 */
	public static String OpenDoor(String ip, Integer port, int openOrClose) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			bw.write(openOrClose);
			// if (openOrClose==10) {
			// bw.write(12);
			// bw.write(openOrClose);
			// bw.write(openOrClose);
			// }
			// bw.newLine();
			bw.flush();
			bw.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	public static int guiDing_1(int i) {
		switch (i) {
		case 1:
			i = 2;
			break;
		case 2:
			i = 1;
			break;
		case 3:
			i = 4;
			break;
		case 4:
			i = 3;
			break;
		case 5:
			i = 6;
			break;
		case 6:
			i = 5;
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * 打开/关闭库位方法
	 * 
	 * @param ip
	 * @param b
	 *            打开：true/关闭：false
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令(1~6)
	 * @return String
	 */
	public static String OCKuWei(String ip, Integer port, boolean b, int openOrClose) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			PrintStream p = new PrintStream(s.getOutputStream());
			p.write(10);
			p.write(11);
			p.write(guiDing_1(openOrClose));
			p.write(00);
			p.write(00);
			p.write(00);
			p.write(00);
			p.write(00);
			if (b)
				p.write(0xFE);// 打开门
			else
				p.write(0xFF);// 关闭门
			p.flush();
			p.close();

			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 打开前闪烁方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令(1~6)
	 * @param type
	 *            true: 关 ,false：开
	 * @return String
	 */
	public static String Oshansuo(String ip, Integer port, int openOrClose, boolean type) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 开门先闪灯
			PrintStream p1 = new PrintStream(s.getOutputStream());
			p1.write(10);
			p1.write(11);
			p1.write(openOrClose);
			for (int i = 0; i < 5; i++) {
				p1.write(00);
			}
			if (type)
				p1.write(0xF6);// 闪灯
			else
				p1.write(0xF6);//
			p1.flush();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 关闭库位后刷新灯颜色的方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param color
	 *            灯颜色
	 * @return
	 */
	public static String lightColorKuWei(String ip, Integer port, int[] color) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			PrintStream p = new PrintStream(s.getOutputStream());
			p.write(10);
			p.write(11);
			for (int i : color) {
				p.write(i);
			}
			p.write(0xF0);// 灯颜色
			p.flush();
			p.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 发送二维码的方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param id
	 *            申请凭证ID
	 * @return
	 */
	public static String sendTowMa(String ip, Integer port, int id) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(0x2C);
			p1.write(0x2D);
			p1.write(cardId(id + ""));// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 发送二维码的方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param str
	 *            内容
	 * @return
	 */
	public static String sendTowMa_1(String ip, Integer port, String str) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(0x2C);
			p1.write(0x2D);
			p1.write(code_1(str, 36));// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 返回二维码的方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param color
	 *            灯颜色
	 * @return
	 */
	public static String backTowMa(String ip, Integer port) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintStream p1 = new PrintStream(s.getOutputStream());
			p1.write(0x2A);
			p1.write(0x2A);
			p1.write(0xA2);
			p1.write(0xA2);
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 关闭库位后刷新屏幕的内容
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param pers
	 *            (1~6) 柜子编号
	 * @param str
	 *            屏信息
	 * @return
	 */
	public static String pingKuWei(String ip, Integer port, int pers, String str) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			int guizi = 0;
			int guizi2 = 0;
			PrintStream p = new PrintStream(s.getOutputStream());
			p.write(0x1C);
			p.write(0x1D);
			switch (pers) {
			case 6:
				break;
			case 5:
				guizi2 = 0x40;
				break;
			case 4:
				guizi2 = 0x80;
				break;
			case 3:
				guizi2 = 0xC0;
				break;
			case 2:
				guizi = 0x01;
				break;
			case 1:
				guizi = 0x01;
				guizi2 = 0x40;
				break;
			default:
				break;
			}
			p.write(guizi);
			p.write(guizi2);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(str);// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 屏幕指令
	 * 
	 * @param i
	 * @return
	 */
	public static int guiding(int i) {
		switch (i) {
		case 1:
			i = 6;
			break;
		case 2:
			i = 5;
			break;
		case 3:
			i = 4;
			break;
		case 4:
			i = 3;
			break;
		case 5:
			i = 2;
			break;
		case 6:
			i = 1;
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * 库位码指令
	 * 
	 * @param i
	 * @return
	 */
	public static int guidingCode(int i) {
		switch (i) {
		case 1:
			i = 0x6d;
			break;
		case 2:
			i = 0x5d;
			break;
		case 3:
			i = 0x4d;
			break;
		case 4:
			i = 0x3d;
			break;
		case 5:
			i = 0x2d;
			break;
		case 6:
			i = 0x1d;
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * 确认后刷新屏幕的内容
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param pers
	 *            (1~6) 柜子编号
	 * @param str
	 *            屏信息
	 * @return
	 */
	public static String querenpingKuWei1(String ip, Integer port, int pers, String str) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(0x3c);
			p1.write(0x3d);
			p1.write(guiding(pers));
			p1.write(str);// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 确认后刷新屏幕的内容
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param pers
	 *            (1~6) 柜子编号
	 * @param str
	 *            屏信息第3页
	 * @return
	 */
	public static String querenpingKuWei(String ip, Integer port, int pers, String str) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(0x4c);
			p1.write(0x4d);
			p1.write(guiding(pers));
			p1.write(str);// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 发送库位码
	 * 
	 * @param ip
	 * @param port
	 * @param pers
	 * @param str
	 * @return
	 */
	public static String sendKuWeiCode(String ip, Integer port, int pers, String str) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// 发送屏幕信息
			PrintWriter p1 = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			p1.write(0x6c);
			p1.write(guidingCode(pers));
			p1.write(str);// 要发送的内容
			p1.flush();
			p1.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 批量打开或关闭灯/车位方法
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            数组 指令 会连续发送过个指令给门禁设备
	 * @return
	 */
	public static String SendZ(String ip, Integer port, int[] openOrClose) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			for (int i = 0; i < openOrClose.length; i++) {
				bw.write(openOrClose[i]);
				bw.flush();
				Thread.sleep(100);
			}
			// bw.newLine();
			bw.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 发送灯颜色指令和取消闪灯指令
	 * 
	 * @param ip
	 * @param port
	 *            端口
	 * @param b
	 *            true闪灯/false亮灯
	 * @param b1
	 *            true闪灯/false结束闪灯&亮灯
	 * @param guiNum
	 *            柜号
	 * @param typeNum
	 *            灯类型
	 * @return
	 */
	public static String openColorXin(String ip, Integer port, boolean b, boolean b1, int guiNum, int typeNum) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			PrintStream p = new PrintStream(s.getOutputStream());
			p.write(guiDing_1(guiNum));
			p.write(lightColorXin(b, typeNum));// 灯指令
			if (b1)
				p.write(1);// 闪灯
			else
				p.write(0);// 结束闪灯/亮灯
			p.flush();
			p.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}

	}

	/**
	 * 打开或关闭灯的方法(带返回)
	 * 
	 * @param ip
	 * @param port
	 *            端口号
	 * @param openOrClose
	 *            指令
	 * @return
	 */
	public static String SendAndBack(String ip, Integer port, int openOrClose, int min) {
		// TODO Auto-generated method stub
		Socket s;
		try {
			s = new Socket(ip, port);
			// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s
			// .getOutputStre am()));
			PrintStream bw = new PrintStream(s.getOutputStream());
			bw.write(openOrClose);
			bw.write(min);
			// bw.newLine();
			bw.flush();
			// InputStream in = s.getInputStream();
			// BufferedInputStream bis = new BufferedInputStream(in);
			// byte[] biao_data = new byte[1];// 先接收第1个字符
			// bis.read(biao_data);// 读取数据
			// String mess1 = Util.byteArrayToHexString(biao_data);
			// System.out.println(mess1+"++++++++++++++++++++++++++");

			bw.close();
			s.close();
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
			return "操作失败!";
		}
	}

	/**
	 * 根据柜号1~6和类型1~4
	 * 
	 * @param guiNum
	 *            柜号
	 * @param typeNum
	 *            类型 1:红/2:绿/3:蓝/4:黄
	 * @return 返回对应开门指令
	 */
	public static int lightColor(int guiNum, int typeNum) {
		int i = 0;
		switch (guiNum) {
		case 1:
			switch (typeNum) {
			case 1:
				i = 0xA1;
				break;
			case 2:
				i = 0xA2;
				break;
			case 3:
				i = 0xA3;
				break;
			case 4:
				i = 0xA4;
				break;
			default:
				break;
			}
			break;
		case 2:
			switch (typeNum) {
			case 1:
				i = 0xA5;
				break;
			case 2:
				i = 0xA6;
				break;
			case 3:
				i = 0xA7;
				break;
			case 4:
				i = 0xA8;
				break;
			default:
				break;
			}
			break;
		case 3:
			switch (typeNum) {
			case 1:
				i = 0xA9;
				break;
			case 2:
				i = 0xAA;
				break;
			case 3:
				i = 0xAB;
				break;
			case 4:
				i = 0xAC;
				break;
			default:
				break;
			}
			break;
		case 4:
			switch (typeNum) {
			case 1:
				i = 0xAD;
				break;
			case 2:
				i = 0xAE;
				break;
			case 3:
				i = 0xAF;
				break;
			case 4:
				i = 0xB1;
				break;
			default:
				break;
			}
			break;
		case 5:
			switch (typeNum) {
			case 1:
				i = 0xB2;
				break;
			case 2:
				i = 0xB3;
				break;
			case 3:
				i = 0xB4;
				break;
			case 4:
				i = 0xB5;
				break;
			default:
				break;
			}
			break;
		case 6:
			switch (typeNum) {
			case 1:
				i = 0xB6;
				break;
			case 2:
				i = 0xB7;
				break;
			case 3:
				i = 0xB8;
				break;
			case 4:
				i = 0xB9;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * 根据类型1~4
	 * 
	 * @param b
	 *            类型 true闪灯/false亮灯
	 * @param typeNum
	 *            类型 1:红/2:绿/3:蓝/4:黄/0：所有灯都熄灭掉
	 * @return 返回对应颜色灯的指令
	 * @author LiCong
	 */
	public static int lightColorXin(boolean b, int typeNum) {
		int i = 0;
		switch (typeNum) {
		case 1:
			i = b ? 0x10 : 0x0C;
			break;
		case 2:
			i = b ? 0x11 : 0x0D;
			break;
		case 3:
			i = b ? 0x0F : 0x0B;
			break;
		case 4:
			i = b ? 0x0E : 0x0A;
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * @param s
	 *            对应的开关编号(灯泡)
	 * @param s1
	 *            当前开关状态 当前为开的，发：0关闭。 当前状态为关的发：1打开
	 * @return
	 */
	public static int swiInstruction(int s, int s1) {
		int id = 0;
		switch (s) {
		case 1:
			id = s1 != 0 ? 0x0A : 0x1A;
			break;
		case 2:
			id = s1 != 0 ? 0x2A : 0x3A;
			break;
		case 3:
			id = s1 != 0 ? 0x4A : 0x5A;
			break;
		case 4:
			id = s1 != 0 ? 0x0B : 0x1B;
			break;
		case 5:
			id = s1 != 0 ? 0x2B : 0x3B;
			break;
		case 6:
			id = s1 != 0 ? 0x4B : 0x5B;
			break;
		case 7:
			id = s1 != 0 ? 0x0C : 0x1C;
			break;
		case 8:
			id = s1 != 0 ? 0x2C : 0x3C;
			break;
		case 9:
			id = s1 != 0 ? 0x4C : 0x5C;
			break;
		case 10:
			id = s1 != 0 ? 0x0D : 0x1D;
			break;
		case 11:
			id = s1 != 0 ? 0x2D : 0x3D;
			break;
		case 12:
			id = s1 != 0 ? 0x4D : 0x5D;
			break;
		case 13:
			id = s1 != 0 ? 0x0E : 0x1E;
			break;
		case 14:
			id = s1 != 0 ? 0x2E : 0x3E;
			break;
		case 15:
			id = s1 != 0 ? 0x4E : 0x5E;
			break;
		case 16:
			id = s1 != 0 ? 0x0F : 0x1F;
			break;
		case 17:
			id = s1 != 0 ? 0x2F : 0x3F;
			break;
		case 18:
			id = s1 != 0 ? 0x4F : 0x5F;
			break;
		case 19:
			id = s1 != 0 ? 0x6A : 0x7A;
			break;
		case 20:
			id = s1 != 0 ? 0x8A : 0x9A;
			break;
		case 21:
			id = s1 != 0 ? 0x6B : 0x7B;
			break;
		case 22:
			id = s1 != 0 ? 0x8B : 0x9B;
			break;
		case 23:
			id = s1 != 0 ? 0x6C : 0x7C;
			break;
		case 24:
			id = s1 != 0 ? 0x8C : 0x9C;
			break;
		default:
			break;
		}
		return id;
	}

	/**
	 * 灯泡开关指令(延时)
	 * 
	 * @param s
	 * @return
	 */
	public static int swiInstruction(int s) {
		int id = 0;
		switch (s) {
		case 1:
			id = 0x9D;// 开灯
			break;
		case 2:
			id = 0x9E;// 关灯
			break;
		case 3:
			id = 0x9F;// 开灯
			break;
		case 4:
			id = 0xA0;// 关灯
			break;
		case 5:
			id = 0xA1;// 开灯
			break;
		case 6:
			id = 0xA2;// 关灯
			break;
		case 7:
			id = 0xA3;// 开灯
			break;
		case 8:
			id = 0xA4;// 关灯
			break;
		case 9:
			id = 0xA5;// 开灯
			break;
		case 10:
			id = 0xA6;// 关灯
			break;
		case 11:
			id = 0xA7;// 开灯
			break;
		case 12:
			id = 0xA8;// 关灯
			break;
		default:
			break;
		}
		return id;
	}

	/**
	 * 返回编号对应的开门指令(档案&工具柜号)
	 * 
	 * @param s
	 *            1~26
	 * @return
	 */
	public static String swiInstructionDuiBi(String s) {
		String id = "";
		try {
			switch (Integer.parseInt(s)) {
			case 1:
				id = "B1";
				break;
			case 2:
				id = "B2";
				break;
			case 3:
				id = "B3";
				break;
			case 4:
				id = "B4";
				break;
			case 5:
				id = "B5";
				break;
			case 6:
				id = "B6";
				break;
			case 7:
				id = "B7";
				break;
			case 8:
				id = "B8";
				break;
			case 9:
				id = "B9";
				break;
			case 10:
				id = "BA";
				break;
			case 11:
				id = "BB";
				break;
			case 12:
				id = "BC";
				break;
			case 13:
				id = "BD";
				break;
			case 14:
				id = "BE";
				break;
			case 15:
				id = "BF";
				break;
			case 16:
				id = "C1";
				break;
			case 17:
				id = "C2";
				break;
			case 18:
				id = "C3";
				break;
			case 19:
				id = "C4";
				break;
			case 20:
				id = "C5";
				break;
			case 21:
				id = "C6";
				break;
			case 22:
				id = "C7";
				break;
			case 23:
				id = "C8";
				break;
			case 24:
				id = "C9";
				break;
			case 25:
				id = "CA";
				break;
			case 26:
				id = "CB";
				break;
			default:
				break;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 返回编号对应的开门指令(档案&工具柜号)
	 * 
	 * @param s
	 *            1~26
	 * @return
	 */
	public static int swiInstruction(String s) {
		int id = 0;
		try {
			switch (Integer.parseInt(s)) {
			case 1:
				id = 0xB1;
				break;
			case 2:
				id = 0xB2;
				break;
			case 3:
				id = 0xB3;
				break;
			case 4:
				id = 0xB4;
				break;
			case 5:
				id = 0xB5;
				break;
			case 6:
				id = 0xB6;
				break;
			case 7:
				id = 0xB7;
				break;
			case 8:
				id = 0xB8;
				break;
			case 9:
				id = 0xB9;
				break;
			case 10:
				id = 0xBA;
				break;
			case 11:
				id = 0xBB;
				break;
			case 12:
				id = 0xBC;
				break;
			case 13:
				id = 0xBD;
				break;
			case 14:
				id = 0xBE;
				break;
			case 15:
				id = 0xBF;
				break;
			case 16:
				id = 0xC1;
				break;
			case 17:
				id = 0xC2;
				break;
			case 18:
				id = 0xC3;
				break;
			case 19:
				id = 0xC4;
				break;
			case 20:
				id = 0xC5;
				break;
			case 21:
				id = 0xC6;
				break;
			case 22:
				id = 0xC7;
				break;
			case 23:
				id = 0xC8;
				break;
			case 24:
				id = 0xC9;
				break;
			case 25:
				id = 0xCA;
				break;
			case 26:
				id = 0xCB;
				break;
			default:
				break;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 字符编码
	 * 
	 * @param s
	 * @return INT
	 */
	public static int Instruction(String s) {
		int id = 0;
		if ("A".equals(s) || "a".equals(s)) {
			id = 0x0A;
		} else if ("1A".equals(s) || "1a".equals(s)) {
			id = 0x1A;
		} else if ("2A".equals(s) || "2a".equals(s)) {
			id = 0x2A;
		} else if ("3A".equals(s) || "3a".equals(s)) {
			id = 0x3A;
		} else if ("4A".equals(s) || "4a".equals(s)) {
			id = 0x4A;
		} else if ("5A".equals(s) || "5a".equals(s)) {
			id = 0x5A;
		} else if ("B".equals(s) || "b".equals(s)) {
			id = 0x0B;
		} else if ("1B".equals(s) || "1b".equals(s)) {
			id = 0x1B;
		} else if ("2B".equals(s) || "2b".equals(s)) {
			id = 0x2B;
		} else if ("3B".equals(s) || "3b".equals(s)) {
			id = 0x3B;
		} else if ("4B".equals(s) || "4b".equals(s)) {
			id = 0x4B;
		} else if ("5B".equals(s) || "5b".equals(s)) {
			id = 0x5B;
		} else if ("C".equals(s) || "c".equals(s)) {
			id = 0x0C;
		} else if ("1C".equals(s) || "1c".equals(s)) {
			id = 0x1C;
		} else if ("2C".equals(s) || "2c".equals(s)) {
			id = 0x2C;
		} else if ("3C".equals(s) || "3c".equals(s)) {
			id = 0x3C;
		} else if ("4C".equals(s) || "4c".equals(s)) {
			id = 0x4C;
		} else if ("5C".equals(s) || "5c".equals(s)) {
			id = 0x5C;
		} else if ("D".equals(s) || "d".equals(s)) {
			id = 0x0D;
		} else if ("1D".equals(s) || "1d".equals(s)) {
			id = 0x1D;
		} else if ("2D".equals(s) || "2d".equals(s)) {
			id = 0x2D;
		} else if ("3D".equals(s) || "3d".equals(s)) {
			id = 0x3D;
		} else if ("4D".equals(s) || "4d".equals(s)) {
			id = 0x4D;
		} else if ("5D".equals(s) || "5d".equals(s)) {
			id = 0x5D;
		} else if ("E".equals(s) || "e".equals(s)) {
			id = 0x0E;
		} else if ("1E".equals(s) || "1e".equals(s)) {
			id = 0x1E;
		} else if ("2E".equals(s) || "2e".equals(s)) {
			id = 0x2E;
		} else if ("3E".equals(s) || "3e".equals(s)) {
			id = 0x3E;
		} else if ("4E".equals(s) || "4e".equals(s)) {
			id = 0x4E;
		} else if ("5E".equals(s) || "5e".equals(s)) {
			id = 0x5E;
		} else if ("F".equals(s) || "f".equals(s)) {
			id = 0x0F;
		} else if ("1F".equals(s) || "1f".equals(s)) {
			id = 0x1F;
		}
		return id;
	}

	/**
	 * @param yanzheng
	 * @return 输入一个首位不为0的数字字符串，返回该字符串的char数组
	 * @author Li_Cong 2016/03/29
	 */
	public static char[] yanzhengchar(String yanzheng) {
		int i = yanzheng.length();
		String[] stryz = yanzheng.split("");
		char[] yz = new char[i];
		for (int z = 1; z < stryz.length; z++) {
			int ic = Integer.parseInt(stryz[z]);
			char ch = (char) (ic);
			yz[z - 1] = ch;
		}
		return yz;
	}

	/**
	 * @param yanzheng
	 * @return int[3] 数组
	 * @author Li_Cong 2016/03/29
	 */
	public static int[] yanzhengintSz(String yanzheng) {
		int yanZ = Integer.parseInt(yanzheng);// 验证码转为数字
		// 用0补齐第一位
		// String yanZz = "0" + Integer.toHexString(yanZ);
		// String a1 = yanZz.substring(0, 2);
		// String a2 = yanZz.substring(2, 4);
		// String a3 = yanZz.substring(4, 6);
		// int inum1 = Integer.parseInt(a1, 16);
		// int inum2 = Integer.parseInt(a2, 16);
		// int inum3 = Integer.parseInt(a3, 16);
		// int[] yz = new int[3];
		// yz[0] = inum1;
		// yz[1] = inum2;
		// yz[2] = inum3;

		if (yanzheng != null && yanzheng.length() > 0) {
			String slZzStr = "0" + Integer.toHexString(yanZ);// 得到十六进制的字符
			int count = slZzStr.length() / 2;
			int[] yzInt = new int[count];
			for (int i = 0; i < count; i++) {
				String sl1 = slZzStr.substring(i * 2, i * 2 + 2);
				yzInt[i] = Integer.parseInt(sl1, 16);
			}
			return yzInt;
		}
		return null;
	}

	/**
	 * 将传入字符串转为指定(n)位数的16进制数据
	 * 
	 * @param yanzheng
	 * @param n
	 * @return int[]
	 */
	public static int[] yanzhengintSz2(String yanzheng, int n) {
		if (yanzheng != null && yanzheng.length() > 0) {
			int yanZ = Integer.parseInt(yanzheng);// 验证码转为数字
			String slZzStr = Integer.toHexString(yanZ);// 再将数字转为十六进制字符串
			for (int i = slZzStr.length(); i < n * 2; i++) {// 截取字符串
				slZzStr = "0" + slZzStr;
			}
			int[] yzInt = new int[n];
			for (int i = 0; i < n; i++) {
				String sl1 = slZzStr.substring(i * 2, i * 2 + 2);
				yzInt[i] = Integer.parseInt(sl1, 16);
			}
			return yzInt;
		}
		return null;
	}

	/**
	 * 将传入字符串转为指定(n)位数的16进制数据 2016-06-28
	 * 
	 * @param yanzheng
	 *            数字
	 * @param n
	 *            几位
	 * @return Char[]
	 */
	public static char[] yanzhengchar(String yanzheng, int n) {
		if (yanzheng != null && yanzheng.length() > 0) {
			int yanZ = Integer.parseInt(yanzheng);// 验证码转为数字
			String slZzStr = Integer.toHexString(yanZ);
			for (int i = slZzStr.length(); i < n * 2; i++) {
				slZzStr = "0" + slZzStr;
			}
			char[] yzInt = new char[n];
			for (int i = 0; i < n; i++) {
				String sl1 = slZzStr.substring(i * 2, i * 2 + 2);
				yzInt[i] = (char) Integer.parseInt(sl1, 16);
			}
			return yzInt;
		}
		return null;
	}

	/**
	 * 将传入字符串转为指定(n)位数的16进制数据 2016-06-28
	 * 
	 * @param Li_Cong
	 *            yanzheng 数字
	 * @param n
	 *            几位
	 * @return byte[]
	 */
	public static byte[] jifenByte(String jifen, int n) {
		if (jifen != null && jifen.length() > 0) {
			int yanZ = Integer.parseInt(jifen);// 验证码转为数字
			String slZzStr = Integer.toHexString(yanZ);
			for (int i = slZzStr.length(); i < n * 2; i++) {
				slZzStr = "0" + slZzStr;
			}
			byte[] jfInt = new byte[n];
			for (int i = 0; i < n; i++) {
				String sl1 = slZzStr.substring(i * 2, i * 2 + 2);
				jfInt[i] = (byte) Integer.parseInt(sl1, 16);
			}
			return jfInt;
		}
		return null;
	}

	/**
	 * 字符串转化成为16进制字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String strTo16(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	/**
	 * 合并数组
	 * 
	 * @param abytDest
	 * @param abytSrc
	 * @return
	 */
	public static byte[] ConcateByteArray(byte[] abytDest, byte[] abytSrc) {
		int len_dest = abytDest.length + abytSrc.length;
		if (abytSrc.length == 0)
			return null;
		byte[] bytTmp = new byte[len_dest];
		System.arraycopy(abytDest, 0, bytTmp, 0, abytDest.length);
		System.arraycopy(abytSrc, 0, bytTmp, abytDest.length, abytSrc.length);
		return bytTmp;
	}

	/**
	 * 将数组中的数字按照字典从小到大排序
	 * 
	 * @param ss
	 * @return
	 */
	public static String paixu(String ss) {
		String[] str1 = (ss).split("; ");
		Arrays.sort(str1);
		ss = Arrays.toString(str1).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", ";");
		return ss;
	}

	/**
	 * 将数组中的数字按照字典从小到大排序
	 * 
	 * @param ss
	 * @return
	 */
	public static String paixu1(String ss) {
		String[] str1 = (ss).split(" ");
		Arrays.sort(str1);
		ss = Arrays.toString(str1).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "") + " ";
		return ss;
	}

	/**
	 * Rfid标识处理
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] rfidbiao(String s) {
		int ii = s.length() / 2;
		byte[] rfid = new byte[ii];
		for (int i = 0; i < ii; i++) {
			String sl1 = s.substring(i * 2, i * 2 + 2);
			rfid[i] = (byte) Integer.parseInt(sl1, 16);
		}
		return rfid;
	}

	/**
	 * 指纹标识处理
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] zhiwenbiao(String s) {
		String[] s1 = s.split(" ");
		byte[] zhiwen = new byte[s1.length];
		for (int i = 0; i < zhiwen.length; i++) {
			zhiwen[i] = (byte) Integer.parseInt(s1[i], 16);
		}
		return zhiwen;
	}

	/**
	 * @param cardId
	 *            卡号
	 * @return 不足十位用0补齐
	 * @author Li_Cong 2016/03/29
	 */
	public static String cardId(String cardId) {
		int num = cardId.length();
		for (int i = num; i < 10; i++) {
			cardId = "0" + cardId;
		}
		return cardId;
	}

	/**
	 * @param code
	 *            卡号
	 * @return 不足十位用0补齐
	 * @author Li_Cong 2016/10/21
	 */
	public static String code_1(String code, int num1) {
		int num = code.length();
		for (int i = num; i < num1; i++) {
			code = "0" + code;
		}
		return code;
	}

	/**
	 * 得到卡号
	 * 
	 * @param users
	 * @return
	 * @author Li_Cong 2016/11/04
	 */
	public static String codeIdNum(Users users, int i) {
		String usCodenum = "";
		if (users == null)
			return usCodenum;
		if (users.getCodeIdNum() != null && users.getCodeIdNum() > 0) {
			usCodenum = (users.getCodeIdNum() + i) + "";
		} else {
			if (users != null && "女".equals(users.getSex())) {
				usCodenum = "2001";
			} else {
				usCodenum = "2000";
			}
		}
		return usCodenum;
	}

	/**
	 * 得到姓名8位
	 * 
	 * @param nameN
	 * @return
	 * @author Li_Cong 2016/11/04
	 */
	public static String nameGb2312(String nameN) throws Exception {
		int si = 8 - nameN.getBytes("gb2312").length;
		for (int i = 0; i < si; i++) {
			nameN = nameN + " ";
		}
		return nameN;
	}

	/**
	 * 得到姓名8位
	 * 
	 * @param deptD
	 * @return
	 * @author Li_Cong 2016/11/04
	 */
	public static String deptGb2312(String deptD) throws Exception {
		if (deptD.getBytes("gb2312").length > 8) {
			deptD = deptD.substring(0, 4);
		}
		int sl = 8 - deptD.getBytes("utf-8").length;
		for (int i = 0; i < sl; i++) {
			deptD = deptD + " ";
		}
		return deptD;
	}

	/**
	 * 截取字符串 len为字节长度
	 * 
	 * @param str
	 *            内容
	 * @param len
	 *            大于len 之外的内容删除
	 * @return
	 * @throws UnsupportedEncodingException
	 * @author licong 2016-11-24
	 */
	public static String getLimitLengthString(String str, int len) {
		try {
			int counterOfDoubleByte = 0;
			byte[] b = str.getBytes("gb2312");
			if (b.length <= len)
				return str;
			for (int i = 0; i < len; i++) {
				if (b[i] < 0)
					counterOfDoubleByte++;
			}
			if (counterOfDoubleByte % 2 == 0)
				return new String(b, 0, len, "gb2312");
			else
				return new String(b, 0, len - 1, "gb2312") + " ";
		} catch (Exception ex) {
			// DBTools.error(ex);
			return "11";
		}
	}

	/**
	 * 库位屏幕显示内容，分字节数截取
	 * 
	 * @param s
	 *            内容
	 * @param j
	 *            长度 汉字为2位
	 * @return
	 * @author licong 2016-11-24
	 */
	public static String neirong(String s, int j) {
		try {
			int i = j - s.getBytes("gb2312").length;
			if (i > 0) {
				for (int k = 0; k < i; k++) {
					s += " ";
				}
			} else {
				s = getLimitLengthString(s, j);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * @return 返回一个存时间的数组
	 * @author Li_Cong 2016/04/26
	 */
	public static byte[] nowIntTime() {
		String st = Util.getDateTime();
		byte[] yzInt = new byte[7];
		String y = st.substring(2, 4);// 得到十六进制的字符
		// Integer.toHexString(Integer.parseInt(s.substring(0,
		// 4)))
		yzInt[0] = (byte) Integer.parseInt(y);// 年
		String M = st.substring(5, 7);
		yzInt[1] = (byte) Integer.parseInt(M);// 月
		String d = st.substring(8, 10);
		yzInt[2] = (byte) Integer.parseInt(d);// 日
		String h = st.substring(11, 13);
		yzInt[3] = (byte) Integer.parseInt(h);// 时
		String m = st.substring(14, 16);
		yzInt[4] = (byte) Integer.parseInt(m);// 分
		String s = st.substring(17, 19);
		yzInt[5] = (byte) Integer.parseInt(s);// 秒
		int w = printFieldDate("wd");// 获得一周中的第几天
		if (w == 1)
			yzInt[6] = 7;// 周
		else
			yzInt[6] = (byte) (w - 1);// 周
		return yzInt;
	}

	/**
	 * @param s
	 *            工号
	 * @param j
	 *            占几位数字
	 * @return char数组
	 * @author LiCong 2016-06-27
	 */
	public static char[] huoquChar(String s, int j) {
		int codeNumImage = s.length();
		for (int i = codeNumImage; i < j; i++) {
			s = "0" + s;
		}
		String[] strCodenum = s.split("");
		char[] strCharCodenum = new char[strCodenum.length - 1];
		for (int i = 1; i < strCodenum.length; i++) {
			int ic = Integer.parseInt(strCodenum[i]);
			char ch = (char) (ic);
			strCharCodenum[i - 1] = ch;
		}
		return strCharCodenum;
	}

	/**
	 * @param s
	 *            姓名
	 * @param i
	 *            占几位
	 * @return 字符串不够用 补齐
	 * @author LiCong 2016-06-27
	 */
	public static String buqiString(String s, int i) {
		try {
			int si = i - s.getBytes("gb2312").length;
			for (int i1 = 0; i1 < si; i1++) {
				s = s + " ";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 根据日期得到星期
	 * 
	 * @param sDate
	 *            yyyy-MM-dd
	 * @return 星期?
	 * @author liCong 2016-09-19
	 */
	public static String getFullDateWeekTime(String sDate) {
		try {
			SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formats.parse(sDate);
			formats.applyPattern("E");
			return formats.format(date);
		} catch (Exception ex) {
			System.out.println("TimeUtil getFullDateWeekTime" + ex.getMessage());
			return "星期零";
		}
	}

	/**
	 * @author liCong 2016-09-19 转化十六进制编码为字符串
	 */
	public static String toStringHex_1(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 四舍五入保留2位小数
	 * 
	 * @param f
	 * @return
	 */
	public static Float towWei(Float f) {
		try {
			f = (float) (Math.round(f * 100)) / 100;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * 四舍五入保留5位小数
	 * 
	 * @param f
	 * @return
	 */
	public static Float fiveWei(Float f) {
		try {
			String s = f + "";
			int position = s.length() - s.indexOf(".");
			if (position > 6 && s.indexOf(".") > 0)
				f = (float) (Math.round(f * 100000)) / 100000;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * 得到本周周一
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getMondayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	/**
	 * 得到本周周日
	 * 
	 * @return yyyy-MM-dd
	 */
	public static String getSundayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	public static void main(String[] args) {
		// markImageByIcon(
		// "E:/apache-tomcat-6.0.41-windows-x86/apache-tomcat-6.0.41/webapps/HHTask/upload/file/yz/icon_ytwrq.png",
		// "D:/20160517163651503.jpeg", "D:/20160517163651503_ioc.jpeg");
		// System.out.println(getMinAfter("07:56:05", 30));
		// System.out.println(getminuteAfter("01:06:10", -180));
		// 8、不在时间段内 查询对比记录表

		// Statement sql;
		// ResultSet rs;
		// String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		// // 加载JDBC驱动
		// String dbURL =
		// "jdbc:sqlserver://192.168.18.246:1433;databaseName=toolsManager_2";
		// // 连接服务器和数据库sample
		// String userName = "sa"; // 默认用户名
		// String userPwd = "linju2014"; // 密码
		// Connection dbConn;
		// try {
		// Class.forName(driverName);
		// dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
		// sql = dbConn.createStatement();
		// rs = sql.executeQuery("select name from users ");
		// while (rs.next()) {
		// System.out.println(rs.getString(1));
		// }
		// dbConn.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// System.out.println(getSplitNumber("5;10;15;20", ";", "min"));
		// System.out.println("3".compareTo("10"));
		// Long youxiaoTime = 0L;
		// try {
		// youxiaoTime = Util.getYouXiaoTime("2016-10-13 14:18:47",
		// "2016-10-13 15:54:46");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Float yxTime = youxiaoTime.floatValue() / 1000;
		// System.out.println(neirong("1230nihao你哦哈", 24));

		// List<Date> list = getBetweenDates(new Date(), Util.StringToDate(
		// "2018-01-28", "yyyy-MM-dd"));
		// for (Date date : list) {
		// System.out.println(Util.DateToString(date, "yyyy-MM-dd"));
		// }
		// System.out.println(towWei(7.855f));
		// System.out.println(FomartFloat(7.8994f, 3));

	}

	/**
	 * /** 给图片添加水印、可设置水印图片旋转角度
	 * 
	 * @param iconPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 * @param degree
	 *            水印图片旋转角度
	 * @param ratio
	 *            缩放比例
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree,
			float ratio) {
		// TODO
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));

			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			// g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null),
			// srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, -0, null);
			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					-0, (int) (srcImg.getWidth(null) * ratio), (int) (srcImg.getHeight(null) * ratio), null);

			if (null != degree) {
				// 设置水印旋转
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
			ImageIcon imgIcon = new ImageIcon(iconPath);

			// 得到Image对象。
			Image img = imgIcon.getImage();

			float alpha = 0.5f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

			// 表示水印图片的位置
			g.drawImage(img, 300, 300, null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 给图片添加水印
	 * 
	 * @param iconPath
	 *            水印图片绝对路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath) {
		markImageByIcon(iconPath, srcImgPath, targerPath, null);
	}

	/**
	 * 给PDF添加水印
	 * 
	 * @param iconPath
	 *            水印图片绝对路径
	 * @param srcPDFPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 */
	public static void markPDFByIcon(String iconPath, String srcPDFPath, String targerPath) {
		try {
			File file = new File(srcPDFPath);
			PDDocument doc = PDDocument.load(file);
			for (PDPage page2 : doc.getPages()) {
				// String ControlledImgPath = ServletActionContext
				// .getServletContext().getRealPath(iconPath);
				String ControlledImgPath = iconPath;
				PDImageXObject ControlledImg = PDImageXObject.createFromFile(ControlledImgPath, doc);
				PDPageContentStream contents = new PDPageContentStream(doc, page2, AppendMode.APPEND, false);
				// contents.drawImage(ControlledImg, 10, 700, 210f, 70f);
				float page_h = page2.getMediaBox().getHeight();
				float page_w = page2.getMediaBox().getWidth();
				contents.drawImage(ControlledImg, page_w / 100f, page_h / 1.14f, page_w / 7f, page_h / 9f);
				// page_w / 3f, page_h / 9f);
				contents.close();
			}
			doc.save(targerPath);
			doc.close();
			System.out.println("PDF完成添加Icon印章。。。。。。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @author fy * @date 2018/6/15 15:24 * @Description: 给PDF添加水印 *
	 * 
	 * @param [iconPath 水印图片绝对路径, srcPDFPath 源图片路径, targerPath 目标图片路径, x 坐标, y
	 * 坐标, width ICON宽, height ICON高] * @return void * @throws
	 */
	public static void markPDFByIcon(String iconPath, String srcPDFPath, String targerPath, float x, float y,
			float width, float height) {
		try {
			File file = new File(srcPDFPath);
			PDDocument doc = PDDocument.load(file);
			for (PDPage page2 : doc.getPages()) {
				String ControlledImgPath = iconPath;
				PDImageXObject ControlledImg = PDImageXObject.createFromFile(ControlledImgPath, doc);
				PDPageContentStream contents = new PDPageContentStream(doc, page2, AppendMode.APPEND, false);

				float page_h = page2.getMediaBox().getHeight();
				float page_w = page2.getMediaBox().getWidth();
				// contents.drawImage(ControlledImg, page_w / 100f, page_h /
				// 1.14f, page_w / 7f, page_h / 9f);
				contents.drawImage(ControlledImg, page_w / x, page_h / y, page_w / width, page_h / height);
				contents.close();
			}
			doc.save(targerPath);
			doc.close();
			System.out.println("PDF完成添加Icon印章。。。。。。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给图片添加水印、可设置水印图片旋转角度
	 * 
	 * @param iconPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 * @param degree
	 *            水印图片旋转角度
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));

			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					-0, null);

			if (null != degree) {
				// 设置水印旋转
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
			ImageIcon imgIcon = new ImageIcon(iconPath);

			// 得到Image对象。
			Image img = imgIcon.getImage();

			float alpha = 0.5f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

			// 表示水印图片的位置
			g.drawImage(img, 300, 300, null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);
			// 生成图片
			ImageIO.write(buffImg, "JPG", os);
			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 十六进制累加 获得校验码
	 * 
	 * @param data
	 * @return
	 */
	public static int makeChecksum(String data) {
		if (data == null || data.equals("")) {
			return 0;
		}
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			System.out.println(s);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 256;
		// String hex = Integer.toHexString(mod);
		// len = hex.length();
		// // 如果不够校验位的长度，补0,这里用的是两位校验
		// if (len < 2) {
		// hex = "0" + hex;
		// }
		return mod;
	}

	/**
	 * 生成n位大写英文字母和数字的随机码;
	 */

	public static String getrandomNum(int n) {
		String res = "";
		char[] chararay = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
				'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		for (int i = 0; i < n; i++) {
			int id = (int) Math.ceil(Math.random() * 35);
			res += chararay[id];
		}
		return res;
	}

	public static CompanyInfo getLoginCompanyInfo() {
		CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext().getApplication().get("companyInfo");
		return companyInfo;
	}

	/**
	 * @param str
	 *            分割的字符串
	 * @param split
	 *            以什么符号分割
	 * @param type
	 *            获取什么值（start：第一 end:最后 min:最小 max:最大）
	 */
	public static Integer getSplitNumber(String str, String split, String type) {
		if (str != null && str.length() > 0) {
			String[] strs = str.split(split);
			if (strs != null && str.length() > 0) {
				if (type != null) {
					if (type.equals("start")) {
						return Integer.parseInt(strs[0]);
					} else if (type.equals("end")) {
						return Integer.parseInt(strs[strs.length - 1]);
					} else if (type.equals("min")) {
						Integer min = Integer.parseInt(strs[0]);
						for (String numStr : strs) {
							Integer com = Integer.parseInt(numStr);
							if (min > com) {
								min = com;
							}
						}
						return min;
					} else if (type.equals("max")) {
						Integer max = Integer.parseInt(strs[0]);
						for (String numStr : strs) {
							Integer com = Integer.parseInt(numStr);
							if (max < com) {
								max = com;
							}
						}
						return max;
					}
				}
			}
		}
		return null;
	}

	// public static String getSplitString(String str,String split,String type){
	// if(str!=null&&str.length()>0){
	// String[] strs= str.split(split);
	// if(strs!=null&&str.length()>0){
	// if(type!=null){
	// if(type.equals("start")){
	// return strs[0];
	// }else if(type.equals("end")){
	// return strs[strs.length-1];
	// }else if(type.equals("min")){
	// String min=strs[0];
	// for(String numStr:strs){
	// if(min.compareTo(numStr)<0){
	// min=numStr;
	// }
	// }
	// return min;
	// }else if(type.equals("max")){
	// String max=strs[0];
	// for(String numStr:strs){
	// if(max.compareTo(numStr)>0){
	// max=numStr;
	// }
	// }
	// return max;
	// }
	// }
	// }
	// }
	// return null;
	// }

	public static String getSplitString(String str, String split, String type) {
		if (str != null && str.length() > 0) {
			String[] strs = str.split(split);
			if (strs != null && str.length() > 0) {
				if (type != null) {
					if (type.equals("start")) {
						return strs[0];
					} else if (type.equals("end")) {
						return strs[strs.length - 1];
					}
				}
			}
		}
		return null;
	}

	/**
	 * 把字符数字转换任意格式的字符串 王晓飞
	 * 
	 * @param num
	 * @param format
	 *            format 请用 数字 或者 # 代替数字 其他的肯定不行了（不信你点进来看啊）
	 * @return
	 */

	public static String numberFormat(String num, String format) {
		char[] charArrays = format.toCharArray();
		char[] numchars = num.toCharArray();
		char[] formatArrays = new char[charArrays.length];
		boolean bool = true;
		int numcount = 0;
		Map<Integer, Character> map = new HashMap<Integer, Character>();
		for (int i = 0; i < charArrays.length; i++) {
			if (Character.isDigit(charArrays[i]) || charArrays[i] == '#') {
				formatArrays[i] = numchars[numcount];
				numcount++;
			} else {
				formatArrays[i] = charArrays[i];
			}
		}
		String str = new String(formatArrays);
		return str;
	}

	/**
	 * 把包含数字的任意字符串转换字符数字 王晓飞
	 * 
	 * @param num
	 * @param format
	 *            format 请用 0 或者 # 代替数字 其他的肯定不行了（不信你点进来看啊）
	 * @return
	 */
	public static String Formatnumber(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		String returnstr = "";
		Pattern pattern0 = Pattern.compile("[0-9,A-Z]*");
		Pattern pattern1 = Pattern.compile("[0-9]*");
		for (int i = 0; i < str.length(); i++) {
			String s = str.charAt(i) + "";
			Matcher isNum = null;
			if (i < 2) {
				isNum = pattern0.matcher(s);
			} else {
				isNum = pattern1.matcher(s);
			}
			if (isNum.matches()) {
				returnstr += s;
			}

		}
		return returnstr;
	}

	/**
	 * 不改精度减法数组第一个为被减数其他都为减数
	 * 
	 * @param fs
	 * @return
	 */
	public static float Floatdelete(Float[] fs) {
		if (fs != null && fs.length > 0) {
			float returnf = 0f;
			for (int i = 0; i < fs.length; i++) {
				Float fn = fs[i];
				if (i == 0) {
					if (fn != null) {
						returnf = fn;
					}
				} else {
					if (fn != null) {
						BigDecimal b1 = new BigDecimal(Float.toString(returnf));
						BigDecimal b2 = new BigDecimal(Float.toString(fn));
						returnf = b1.subtract(b2).floatValue();
					}
				}
			}
			return returnf;
		}
		return 0f;
	}

	/**
	 * 不改精度减法
	 * 
	 * @param f1被减数
	 * @param f2减数
	 * @return
	 */
	public static float Floatdelete(Float f1, Float f2) {
		if (f1 == null) {
			f1 = 0f;
		}
		if (f2 == null) {
			f2 = 0f;
		}
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.subtract(b2).floatValue();
	}

	/**
	 * 不改精度加法
	 * 
	 * @param fs
	 * @return
	 */
	public static float Floatadd(Float[] fs) {
		if (fs != null && fs.length > 0) {
			float returnf = 0f;
			for (int i = 0; i < fs.length; i++) {
				Float fn = fs[i];
				if (fn != null) {
					BigDecimal b1 = new BigDecimal(Float.toString(returnf));
					BigDecimal b2 = new BigDecimal(Float.toString(fn));
					returnf = b1.add(b2).floatValue();
				}
			}
			return returnf;
		}
		return 0f;
	}

	/**
	 * 不改精度加法
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static float Floatadd(Float f1, Float f2) {
		if (f1 == null) {
			f1 = 0f;
		}
		if (f2 == null) {
			f2 = 0f;
		}
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.add(b2).floatValue();
	}

	/**
	 * 不改精度乘法
	 * 
	 * @param fs
	 * @return
	 */
	public static float Floatmul(Float[] fs) {
		if (fs != null && fs.length > 0) {
			float returnf = 0f;
			for (int i = 0; i < fs.length; i++) {
				Float fn = fs[i];
				if (i == 0) {
					if (fn != null) {
						returnf = fn;
					}
				} else {
					if (fn != null) {
						BigDecimal b1 = new BigDecimal(Float.toString(returnf));
						BigDecimal b2 = new BigDecimal(Float.toString(fn));
						returnf = b1.multiply(b2).floatValue();
					}
				}
			}
			return returnf;
		}
		return 0f;
	}

	/**
	 * 不改精度乘法
	 * 
	 * @param f1
	 * @param f2
	 * @param scale
	 * @return
	 */
	public static float Floatmul(Float f1, Float f2) {
		if (f1 == null) {
			f1 = 0f;
		}
		if (f2 == null) {
			f2 = 0f;
		}
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.multiply(b2).floatValue();
	}

	/**
	 * 不改精度除法
	 * 
	 * @param fs
	 *            数组第一个为被除数其他都为除数
	 * @param scale
	 *            进度(0-6)
	 * @return
	 * @throws IllegalAccessException
	 */
	public static float Floatdiv(Float[] fs, int scale) throws IllegalAccessException {
		if (fs != null && fs.length > 0) {
			if (scale < 0) {
				throw new IllegalAccessException("精确度不能小于0");
			}
			float returnf = 0f;
			for (int i = 0; i < fs.length; i++) {
				Float fn = fs[i];
				if (i == 0) {
					if (fn != null) {
						returnf = fn;
					}
				} else {
					if (fn != null) {
						if (fn == 0) {
							throw new IllegalAccessException("除数不能为0!");
						}
						BigDecimal b1 = new BigDecimal(Float.toString(returnf));
						BigDecimal b2 = new BigDecimal(Float.toString(fn));
						returnf = b1.divide(b2, scale).floatValue();
					} else {
						throw new IllegalAccessException("除数不能为0!");
					}
				}
			}
			return returnf;
		}
		return 0f;
	}

	/**
	 * 不改精度除法
	 * 
	 * @param f1被除数
	 * @param f2除数
	 * @param scale精度
	 *            (0-6)
	 * @return
	 * @throws IllegalAccessException
	 */
	public static float Floatdiv(Float f1, Float f2, int scale) throws IllegalAccessException {
		if (scale < 0) {
			throw new IllegalAccessException("精确度不能小于0");
		}
		if (f1 == null) {
			f1 = 0f;
		}
		if (f2 == null || f2 == 0) {
			throw new IllegalAccessException("除数不能为0!");
		}
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.divide(b2, scale).floatValue();
	}

	public static boolean isEquals(String str1, String str2) {
		if ((str1 == null || str1.length() == 0) && str2 != null && str2.length() > 0) {
			return false;
		}
		if ((str2 == null || str2.length() == 0) && str1 != null && str1.length() > 0) {
			return false;
		}
		if (str1 != null && str1.length() >= 0 && str2 != null && str2.length() > 0 && !str1.equalsIgnoreCase(str2)) {
			return false;
		}
		return true;
	}

	public static boolean isEquals(Float f1, Float f2) {
		if (f1 == null && f2 != null) {
			return false;
		}
		if (f2 == null && f1 != null) {
			return false;
		}
		if (f1 != null && f2 != null && !f1.equals(f2)) {
			return false;
		}
		return true;
	}

	public static boolean isEquals(Integer i1, Integer i2) {
		if (i1 == null && i2 != null) {
			return false;
		}
		if (i2 == null && i1 != null) {
			return false;
		}
		if (i1 != null && i2 != null && !i1.equals(i2)) {
			return false;
		}
		return true;
	}

	public static boolean isEquals(Double i1, Double i2) {
		if (i1 == null && i2 != null) {
			return false;
		}
		if (i2 == null && i1 != null) {
			return false;
		}
		if (i1 != null && i2 != null && !i1.equals(i2)) {
			return false;
		}
		return true;
	}

	/**
	 * 四舍五入保留几位小数
	 * 
	 * @param f
	 * @param weisu
	 * @return
	 */
	public static float FomartFloat(Float f, int weisu) {
		if (f != null) {
			BigDecimal b = new BigDecimal(f);
			float f1 = b.setScale(weisu, BigDecimal.ROUND_HALF_UP).floatValue();
			return f1;
		} else {
			return 0;
		}

	}

	/**
	 * 四舍五入保留几位小数
	 * 
	 * @param f
	 * @param weisu
	 * @return
	 */
	public static float FomartDouble(Double f, int weisu) {
		if (f != null) {
			BigDecimal b = new BigDecimal(f);
			float f1 = b.setScale(weisu, BigDecimal.ROUND_HALF_DOWN).floatValue();
			float f2 = b.setScale(weisu, BigDecimal.ROUND_HALF_DOWN).floatValue();
			return f1;
		} else {
			return 0;
		}

	}

	/**
	 * 获取两个日期之间的日期
	 * 
	 * @param start
	 *            开始日期
	 * @param end
	 *            结束日期
	 * @return 日期集合
	 */
	public static List<Date> getBetweenDates(Date start, Date end) {
		List<Date> result = new ArrayList<Date>();
		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(start);
		tempStart.add(Calendar.DAY_OF_YEAR, 1);

		Calendar tempEnd = Calendar.getInstance();
		tempEnd.setTime(end);
		while (tempStart.before(tempEnd)) {
			result.add(tempStart.getTime());
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}

	/**
	 * 清除Session
	 * 
	 * @param session
	 * @param sessionNames
	 */
	public static void removeSession(HttpSession session, String[] sessionNames) {
		if (session != null && sessionNames != null && sessionNames.length > 0) {
			for (int i = 0; i < sessionNames.length; i++) {
				session.removeAttribute(sessionNames[i]);
			}
		}
	}

	/**
	 * 将字符串在中的[]和空格去掉。并将,替换为','号 便于数据库查询使用
	 * 
	 * @param markId
	 * @return
	 */
	public static String selectString(String markId) {
		return markId.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "','").replaceAll(" ", "");
	}

	/**
	 * wxf 判断多个集合是否拥有共同的元素，并把共同的元素添加到另一集合中返回
	 * 
	 * @param List
	 *            <List> list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> isPublicElement(List<List> list) {
		if (list != null && list.size() > 0) {
			List list0 = list.get(0);
			List newList = new ArrayList();
			if (list.size() == 0) {
				newList = list0;
			} else {
				for (Object obj : list0) {
					int size = list.size();
					for (List<Object> list_list : list) {
						if (list_list.contains(obj)) {
							size--;
						}
					}
					if (size == 0) {
						if (!newList.contains(obj)) {
							newList.add(obj);
						}
					}
				}
			}
			return newList;
		}
		return null;
	}

	/**
	 * 判断对象中的所有属性是否为""或者为null
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static boolean isAllFieldNull(Object obj) throws Exception {
		Class stuCla = (Class) obj.getClass();// 得到类对象
		Field[] fs = stuCla.getDeclaredFields();// 得到属性集合
		boolean flag = true;// 空
		for (Field f : fs) {// 遍历属性
			if("serialVersionUID".equals(f.getName())){
				continue;
			}
			f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
			Object val = f.get(obj);// 得到此属性的值
			if (val != null) {// 只要有1个属性不为空,那么就不是所有的属性值都为空
				String string = val.toString();
				if (string != null && !"".equals(string)) {
					flag = false;// 非空
					break;

				}
			}
		}
		return flag;
	}

	/**
	 * 压缩文件列表中的文件
	 * 
	 * @param files
	 * @param outputStream
	 * @throws IOException
	 */
	public static void zipFile(List files, List<String> otherNames, ZipOutputStream outputStream)
			throws Exception, ServletException {
		try {
			int size = files.size();
			// 压缩列表中的文件
			for (int i = 0; i < size; i++) {
				File file = (File) files.get(i);
				try {
					zipFile(file, otherNames.get(i), outputStream);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 将文件写入到zip文件中
	 * 
	 * @param inputFile
	 * @param outputstream
	 * @throws Exception
	 */
	public static void zipFile(File inputFile, String otherName, ZipOutputStream outputstream)
			throws IOException, ServletException {
		try {
			if (inputFile.exists()) {
				if (inputFile.isFile()) {
					FileInputStream inStream = new FileInputStream(inputFile);
					BufferedInputStream bInStream = new BufferedInputStream(inStream);
					ZipEntry entry = new ZipEntry(otherName);
					outputstream.putNextEntry(entry);

					final int MAX_BYTE = 100 * 1024 * 1024; // 最大的流为100M
					long streamTotal = 0; // 接受流的容量
					int streamNum = 0; // 流需要分开的数量
					int leaveByte = 0; // 文件剩下的字符数
					byte[] inOutbyte; // byte数组接受文件的数据

					streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
					streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
					leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

					if (streamNum > 0) {
						for (int j = 0; j < streamNum; ++j) {
							inOutbyte = new byte[MAX_BYTE];
							// 读入流,保存在byte数组
							bInStream.read(inOutbyte, 0, MAX_BYTE);
							outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
						}
					}
					// 写出剩下的流数据
					inOutbyte = new byte[leaveByte];
					bInStream.read(inOutbyte, 0, leaveByte);
					outputstream.write(inOutbyte);
					outputstream.closeEntry(); // Closes the current ZIP entry
					// and positions the stream for
					// writing the next entry
					bInStream.close(); // 关闭
					inStream.close();
				}
			} else {
				throw new ServletException("文件不存在！");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 下载打包的文件
	 * 
	 * @param file
	 * @param response
	 */
	public static void downloadZip(File file, HttpServletResponse response) {
		try {
			// 以流的形式下载文件。
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();

			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			file.delete(); // 将生成的服务器端文件删除
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * @author fy
	 * 
	 * @date 2018/6/7 17:20
	 * 
	 * @Description: Poi更改excel打印区域大小 （用于excel->PDF预览调整）
	 * 
	 * @param [inputFile, outputFile, TruePath]
	 * 
	 * @return boolean
	 * 
	 * @throws
	 */
	public static boolean changeExcelPrintsize(String inputFile, String outputFile, Boolean TruePath)
			throws IOException, InvalidFormatException {
		// List temp = new ArrayList();
		FileInputStream fileIn = null;
		FileOutputStream fileOut = null;
		if (TruePath) {
			fileIn = new FileInputStream(inputFile);
		} else {
			fileIn = new FileInputStream(ServletActionContext.getServletContext().getRealPath(inputFile));
		}
		// 根据指定的文件输入流导入Excel从而产生Workbook对象
		// 如果是xls，使用HSSFWorkbook；如果是xlsx，使用XSSFWorkbook

		// 使用 createWorkbook后可以不用这么判断了 未改
		if ((inputFile.substring(inputFile.lastIndexOf("."), inputFile.length()).equalsIgnoreCase(".xls"))) {
			// Workbook wb0 = new HSSFWorkbook(fileIn);
			Workbook wb0 = createWorkbook(fileIn);
			fileIn.close();
			HSSFSheet sht0 = ((HSSFWorkbook) wb0).getSheetAt(0);
			HSSFPrintSetup printSetup = sht0.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setPaperSize(HSSFPrintSetup.A3_PAPERSIZE);
			// fileIn fileOut 冲突
			if (TruePath) {
				fileOut = new FileOutputStream(outputFile);
			} else {
				fileOut = new FileOutputStream(ServletActionContext.getServletContext().getRealPath(outputFile));
			}
			wb0.write(fileOut);
			fileOut.flush();
			fileOut.close();

		} else if ((inputFile.substring(inputFile.lastIndexOf("."), inputFile.length()).equalsIgnoreCase(".xlsx"))) {
			// XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
			Workbook workbook = createWorkbook(fileIn);
			fileIn.close();
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			XSSFPrintSetup printSetup = sheet.getPrintSetup();
			// 横
			printSetup.setLandscape(true);
			// 看了下最大A3
			printSetup.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
			if (TruePath) {
				fileOut = new FileOutputStream(outputFile);
			} else {
				fileOut = new FileOutputStream(ServletActionContext.getServletContext().getRealPath(outputFile));
			}
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();

		} else {
			fileIn.close();
			return false;
		}

		return true;
	}

	/*
	 * @author fy * @date 2018/6/8 14:20 * @Description:Poi区分EXCEL03 07版本
	 * 创建Workbook * @param [in] * @return org.apache.poi.ss.usermodel.Workbook
	 * * @throws
	 */
	public static Workbook createWorkbook(InputStream in) throws IOException, InvalidFormatException {
		if (!in.markSupported()) {
			in = new PushbackInputStream(in, 8);
			Workbook wb = WorkbookFactory.create(in);
			return wb;
		}
		// 过时
		// if (POIFSFileSystem.hasPOIFSHeader(in)) {
		// return new HSSFWorkbook(in);
		// }
		// if (POIXMLDocument.hasOOXMLHeader(in)) {
		// return new XSSFWorkbook(OPCPackage.open(in));
		// }
		throw new IllegalArgumentException("你的excel版本目前poi解析不了");
	}

	public static Float jujueNull(Float ff) {
		return ff == null ? 0f : ff;
	}

	public String getJdbc() {
		Statement sql;
		ResultSet rs;
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
		String dbURL = "jdbc:sqlserver://192.168.18.246:1433;databaseName=toolsManager_2"; // 连接服务器和数据库sample
		String userName = "sa"; // 默认用户名
		String userPwd = "linju2014"; // 密码
		Connection dbConn;
		try {
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			sql = dbConn.createStatement();
			rs = sql.executeQuery("select name from users ");
			while (rs.next()) {
				System.out.println(rs.getString(1));
			}
			dbConn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Double保留相应位数
	 * 
	 * @param d
	 * @param num
	 * @return
	 */
	public static Double MacthRound(double d, int num) {
		String str = "#";
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				str += ".#";
			} else {
				str += "#";
			}
		}
		DecimalFormat df = new DecimalFormat(str);
		return Double.parseDouble(df.format(d));
	}

	/*
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/*
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

	/**
	 * 第一个参数的解释，记得一定要设置为1 signum of the number (-1 for negative, 0 for zero, 1
	 * for positive).
	 */
	public static String bytes2hex01(byte[] bytes) {
		BigInteger bigInteger = new BigInteger(1, bytes);
		return bigInteger.toString(16);
	}

	/**
	 * 获得
	 * 
	 * @param size
	 * @return
	 */
	public static String randomStr(int size) {
		String a = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		char[] rands = new char[size];
		for (int i = 0; i < rands.length; i++) {
			int rand = (int) (Math.random() * a.length());
			rands[i] = a.charAt(rand);
		}
		return new String(rands);
	}

	/**
	 * 删除指定文件夹下的所有文件
	 * 
	 * @param path
	 * @return
	 */

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 删除空文件夹
	 */
	public static void delFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****
	 * 求中位数
	 * 
	 * @param nums
	 * @return
	 */
	public static int median(int[] nums) {
		if (nums.length == 0)
			return 0;
		int start = 0;
		int end = nums.length - 1;
		int index = partition(nums, start, end);
		if (nums.length % 2 == 0) {
			while (index != nums.length / 2 - 1) {
				if (index > nums.length / 2 - 1) {
					index = partition(nums, start, index - 1);
				} else {
					index = partition(nums, index + 1, end);
				}
			}
		} else {
			while (index != nums.length / 2) {
				if (index > nums.length / 2) {
					index = partition(nums, start, index - 1);
				} else {
					index = partition(nums, index + 1, end);
				}
			}
		}
		return nums[index];
	}

	private static int partition(int nums[], int start, int end) {
		int left = start;
		int right = end;
		int pivot = nums[left];
		while (left < right) {
			while (left < right && nums[right] >= pivot) {
				right--;
			}
			if (left < right) {
				nums[left] = nums[right];
				left++;
			}
			while (left < right && nums[left] <= pivot) {
				left++;
			}
			if (left < right) {
				nums[right] = nums[left];
				right--;
			}
		}
		nums[left] = pivot;
		return left;
	}

	/****
	 * 求中位数
	 * 
	 * @param nums
	 * @return
	 */
	public static float median(float[] nums) {
		if (nums.length == 0)
			return 0;
		int start = 0;
		int end = nums.length - 1;
		int index = partition(nums, start, end);
		if (nums.length % 2 == 0) {
			while (index != nums.length / 2 - 1) {
				if (index > nums.length / 2 - 1) {
					index = partition(nums, start, index - 1);
				} else {
					index = partition(nums, index + 1, end);
				}
			}
		} else {
			while (index != nums.length / 2) {
				if (index > nums.length / 2) {
					index = partition(nums, start, index - 1);
				} else {
					index = partition(nums, index + 1, end);
				}
			}
		}
		return nums[index];
	}

	private static int partition(float nums[], int start, int end) {
		int left = start;
		int right = end;
		float pivot = nums[left];
		while (left < right) {
			while (left < right && nums[right] >= pivot) {
				right--;
			}
			if (left < right) {
				nums[left] = nums[right];
				left++;
			}
			while (left < right && nums[left] <= pivot) {
				left++;
			}
			if (left < right) {
				nums[right] = nums[left];
				right--;
			}
		}
		nums[left] = pivot;
		return left;
	}

	public static StringBuffer getIntegersqlData(List<Integer> idList) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		if (idList != null && idList.size() > 0) {
			for (Integer id : idList) {
				if (sb.length() == 0) {
					sb.append(id);
				} else {
					sb.append("," + id);
				}
			}
		}
		return sb;
	}

	public static StringBuffer getStringsqlData(List<String> strList) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		if (strList != null && strList.size() > 0) {
			for (String str : strList) {
				if (sb.length() == 0) {
					sb.append("'" + str + ",");
				} else {
					sb.append(",'" + str + ",");
				}
			}
		}
		return sb;
	}
	/**
	 * 获得当前季度
	 */
	public static Integer getQuarter(String time,String format){
		Date date =	new Date();
		if(time!=null && time.length()>0
				&& format!=null && format.length()>0){
			date =	StringToDate(time, format);
		}
		 Calendar c = Calendar.getInstance();
		 c.setTime(date);
	      int currentMonth = c.get(Calendar.MONTH) + 1;
	      if(currentMonth>=1 && currentMonth<=3 ){
	    	  return 1;
	      }else if(currentMonth>=4 && currentMonth<=6){
	    	  return 2;
	      }else if(currentMonth>=7 && currentMonth<=9){
	    	  return 3;
	      }else if(currentMonth>=10 && currentMonth<=12){
	    	  return 4;
	      }
		return null;
	}
	/**
	 * 比较两个零件和其子件是否有差异
	 * @param nowId 当前零件Id
	 * @param historId 之前零件Id
	 * @param totalCard
	 * @return
	 */
	public static String compareProcardAndSons(Integer nowId, Integer historId,Procard nowp,Procard hp,
			TotalDao totalDao,String entityName,Integer entityId,String markId) {
		// TODO Auto-generated method stub
		String msg ="否";
		Procard procard =null;
		Procard history = null;
		if(nowId!=null){
			procard=(Procard) totalDao.getObjectById(Procard.class, nowId);
			history=(Procard) totalDao.getObjectById(Procard.class, historId);
			
		}else{
			procard=nowp;
			history=hp;
		}
		if(procard==null){
			return "是";
		}
		if(history==null){
			return "是";
		}
		if(!Util.isEquals(procard.getBanci(), history.getBanci())){
			msg ="是";
			WaigouPlanLock wpl = new WaigouPlanLock();
			wpl.setEntityName(entityName);//被锁定表名(WaigouPlan,WaigouWaiweiPlan,ta_sop_w_ProcessInforWWApplyDetail)
			wpl.setEntityId(entityId);//被输定表Id
			wpl.setMarkId(markId);//设变相关零件件号
//			wpl.setsbApplyId;//设变单Id
//			wpl.setsbApplyDetailId;//设变明细Id
//			wpl.setsbProcardId;//设变零件Id
			wpl.setSbmarkId(procard.getMarkId());//件号
//			wpl.setremark;//设变内容说明
			wpl.setZxStatus("已执行");//执行状态(执行中,已执行,不执行)
			wpl.setDataStatus("使用");
			totalDao.save(wpl);
		}
		Set<Procard> nsonSet = procard.getProcardSet();
		Set<Procard> hsonSet = history.getProcardSet();
		if(nsonSet!=null&&nsonSet.size()>0
				&&hsonSet!=null&&hsonSet.size()>0){
			for(Procard nson:nsonSet){
				if(nson.getSbStatus()!=null&&nson.getSbStatus().equals("删除")){
					continue;
				}
				for(Procard hson:hsonSet){
					if(hson.getSbStatus()!=null&&hson.getSbStatus().equals("删除")){
						continue;
					}
					if(nson.getMarkId().equals(hson.getMarkId())){
						String msg2 = compareProcardAndSons(null, null, nson, hson, totalDao, entityName, entityId,markId);
						if(msg2.equals("是")){//有变化
							msg ="是";
						}
					}
				}
			}
		}
		return msg;
	}
	/**
	 * 返回类中  FieldMeta注解的字段
	 * @param c
	 * @param remarks
	 * @return json 字符串
	 */
	public static String[] getFieldAndRemarks(Class c,String remarks){
		Map<String, String> map = new LinkedHashMap<String, String>();
		Field[] field =	c.getDeclaredFields();
		String [] remarksArray =null;
		String showText = "";
		if(remarks!=null && remarks.length()>0){
			remarksArray = remarks.split(",");
		}
		if(remarksArray == null || remarksArray.length == 0){
			remarks = "";
			for (Field f : field){
				FieldMeta meta = f.getAnnotation(FieldMeta.class);
				if(meta!=null ){
					map.put(f.getName(),meta.name());
					remarks+=","+f.getName();
					showText+=","+meta.name();
				}
			}
			if(remarks.length()>0){
				remarks = remarks.substring(1);
				showText = showText.substring(1);
			}
		}else{
			for (Field f : field){
				FieldMeta meta = f.getAnnotation(FieldMeta.class);
				if(meta!=null ){
					for (String str : remarksArray) {
						if(f.getName().equals(str)){
							map.put(f.getName(),meta.name());
							showText+=","+meta.name();
						}
					}
				}
			}
			if(remarks.length()>0){
				showText = showText.substring(1);
			}
		}
		return  new String[] {JSON.toJSONString(map),remarks,showText} ;
	}
	public static Map<String, String> getFieldAndRemarks1(Class c,String remarks){
		Map<String, String> map = new LinkedHashMap<String, String>();
		Field[] field =	c.getDeclaredFields();
		String [] remarksArray =null;
		if(remarks!=null && remarks.length()>0){
			remarksArray = remarks.split(",");
		}
		if(remarksArray == null || remarksArray.length == 0){
			for (Field f : field){
				FieldMeta meta = f.getAnnotation(FieldMeta.class);
				if(meta!=null ){
					map.put(f.getName(),meta.name());
				}
			}
		}else{
			for (Field f : field){
				FieldMeta meta = f.getAnnotation(FieldMeta.class);
				if(meta!=null ){
					for (String str : remarksArray) {
						if(f.getName().equals(str)){
							map.put(f.getName(),meta.name());
						}
					}
				}
			}
		}
		return map;
	}
	
}
