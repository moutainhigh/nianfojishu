package com.task.ServerImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.beans.BeanUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.LeaveServer;
import com.task.Server.banci.BanCiServer;
import com.task.Server.sys.CircuitRunServer;
import com.task.ServerImpl.banci.BanCiServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.AnnualLeave;
import com.task.entity.AskForLeave;
import com.task.entity.Credentials;
import com.task.entity.InsuranceGold;
import com.task.entity.Overtime;
import com.task.entity.QxAskForLeave;
import com.task.entity.RewardPunish;
import com.task.entity.Users;
import com.task.entity.WageStandard;
import com.task.entity.banci.BanCi;
import com.task.entity.banci.BanCiTime;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.menjin.InEmployeeCarInfor;
import com.task.entity.project.QuotedPrice;
import com.task.entity.project.QuotedPriceCost;
import com.task.entity.singlecar.SingleCar;
import com.task.entity.sop.Procard;
import com.task.entity.system.CircuitRun;
import com.task.util.Util;

@SuppressWarnings("unchecked")
public class LeaveServerImpl implements LeaveServer {
	private TotalDao totalDao;
//	private int day;
//	private float hours;
	private CircuitRunServer circuitRunServer;
	private String timeFormat;
	private static final Float SECONDS = 619200f;

	/***
	 * 换休协议
	 */
	public List listhuanxiuxieyi(String nams) {
		// s=mobanname.substring(0,2);//截取s中从begin开始至end结束时的字符串，并将其赋值给s;
		String hql = "  from ClauseSon where  fatherId in ("
				+ " select id from ClauseFather where name  like  '" + nams
				+ "' )";
		List list = totalDao.find(hql);
		return list;

	}

	/*
	 * 根据工号查询换休记录
	 */
	public AnnualLeave Bynamehuanxiu(String personcode) {

		// 换休有效期→查询系统设置
		String queryOverTime = "from Overtime where overtimeCode = ? and huanxiu='是' and status='同意'";
		CodeTranslation codeTranslation = (CodeTranslation) totalDao
				.getObjectByCondition("from CodeTranslation where type='sys' and valueName='换休有效期'");
		List<Overtime> list = null;
		if (null == codeTranslation) {
			list = totalDao.query(queryOverTime, personcode);
		} else {
			if (Integer.parseInt(codeTranslation.getValueCode()) > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.MONTH, -Integer.parseInt(codeTranslation
						.getValueCode()));
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String passDate = format.format(calendar.getTime());
				list = totalDao.query(queryOverTime + " and startDate>?",
						personcode, passDate);
			}
		}

		float overTimeLong = 0.0f;// 可用换休时间（小时）
		for (Overtime overtime : list) {
			if (null == overtime.getUsablehxTime()) {
				overtime.setUsablehxTime(overtime.getOverTimeLong()==null? 0f:overtime.getOverTimeLong());
			}
			overTimeLong += overtime.getUsablehxTime();
		}

		String hql1 = "from  AnnualLeave  where status ='换休' and  jobNum=?";
		AnnualLeave annualLeave = (AnnualLeave) totalDao.getObjectByCondition(
				hql1, personcode);
		Users users = (Users) totalDao
				.getObjectByCondition("from Users where code='" + personcode
						+ "'");
		if (null == annualLeave) {
			annualLeave = new AnnualLeave();
			annualLeave.setDept(users.getDept());
			annualLeave.setJobNum(users.getCode());
			annualLeave.setName(users.getName());
			annualLeave.setStatus("换休");
			annualLeave.setSurplus(0f);
		}
		BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users
				.getBanci_id());
		Float gzTime = 8f; // 每天工作时长
		if (null != banCi && null != banCi.getGzTime()) {
			gzTime = banCi.getGzTime() / 60f;
		}
		if (gzTime == null || gzTime == 0) {
			gzTime = 8f;
		}
		Float surplus = overTimeLong / gzTime;
		annualLeave.setSurplus(surplus);// 可用换休 /单位（天）
		if (null == annualLeave.getId()) {
			totalDao.save(annualLeave);
		} else {
			totalDao.update(annualLeave);
		}

		return annualLeave;
	}

	/**
	 * 更新换休记录
	 * 
	 * @param code
	 */
	public void gengxinhuanxiu(String code) {
	}

	/*
	 * 根据工号查询年休
	 */
	public AnnualLeave BynameNianxiu(String personcode) {
		String hql1 = " from  AnnualLeave  where status ='年休' and  jobNum=?";
		AnnualLeave annualLeave = (AnnualLeave) totalDao.getObjectByCondition(
				hql1, personcode);
		return annualLeave;
	}

	public CircuitRunServer getCircuitRunServer() {
		return circuitRunServer;
	}

	public void setCircuitRunServer(CircuitRunServer circuitRunServer) {
		this.circuitRunServer = circuitRunServer;
	}

	// 保存请假
	@Override
	public String saveLeave(AskForLeave askForLeave, int deptIds[],
			int userIds[])throws Exception{
		
		if (askForLeave != null) {
			Users loginUser = Util.getLoginUser();// 登陆用户信息获得
			if ("代理请假".equals(askForLeave.getLeaveType())) {
				String hql2 = "from Users where code=?";
				Users lUser = (Users) totalDao.getObjectByCondition(hql2,
						askForLeave.getLeavePersonCode());// 代理用户信息获得
				if (lUser != null) {
					// // 保存用户到session中
					ActionContext.getContext().getSession().put(TotalDao.users,
							lUser);// 讲请假人临时存入Session
				} else {
					return "用户不存在";
				}
			}
			boolean boo = true;
			SingleCar singleCar = null;
			String uIds = null;// 前置审批人员
			
			if ("公出".equals(askForLeave.getLeaveTypeOf())) {
				boolean b = false;
				InEmployeeCarInfor carInfor = null;
				Credentials credentials = null;
				if (askForLeave.getNeedCar() != null
						&& askForLeave.getNeedCar().equals("是"))
					b = true;
				if (b) {
					singleCar = askForLeave.getSingleCar();
					if (singleCar.getKilometers() == null) {
						return "请填写预估公里数!";
					}
					if (singleCar.getCar_number() == null
							|| singleCar.getCar_number().length() == 0) {
						return "请填写车牌号！";
					}
					// 个人车辆不参与判断
					carInfor = (InEmployeeCarInfor) totalDao
							.getObjectByCondition(
									"from InEmployeeCarInfor where nplates=? and carType = '公车'",
									singleCar.getCar_number());
					if (carInfor != null) {
						// String carType = (String)
						// totalDao.getObjectByCondition("select carType from InEmployeeCarInfor where nplates=?",
						// singleCar.getCar_number());
						if (carInfor.getCarType() != null
								&& carInfor.getCarType().equals("公车")) {
							SingleCar unback = (SingleCar) totalDao
									.getObjectByCondition(
											"from SingleCar where car_userCode=? and status='同意' and useStatus='使用中'",
											askForLeave.getLeavePerson());
							if (unback != null) {
								return "您有未归还的车辆，请先归还在借用车辆!";
							}
							SingleCar has = (SingleCar) totalDao
									.getObjectByCondition(
											"from SingleCar where car_number=? and status='同意' and useStatus='使用中'",
											singleCar.getCar_number());
							if (has != null) {
								return "该车辆已被" + has.getCar_usename()
										+ "占用尚未归还,请申请其他车辆!";
							}
						}
					} else {
						credentials = (Credentials) totalDao
								.getObjectByCondition(
										"from Credentials where platenumber = ? and cardtype = '行驶证'",
										singleCar.getCar_number());
					}
					singleCar.setCar_date(askForLeave.getLeaveStartDate());
					singleCar.setCar_content(askForLeave.getLeaveReason());
					singleCar.setVar_dept(askForLeave.getLeavePersonDept());
					singleCar.setCar_usename(askForLeave.getLeavePerson());
					singleCar.setCar_userCode(askForLeave.getLeavePersonCode());
					String createDate = Util.getDateTime();
					singleCar.setCreate_date(createDate);
					singleCar.setProcessingstatus("未处理");
					singleCar.setStatus("未审核");
					singleCar.setSinglecarType(askForLeave.getGongchuPlace());
				}
				// 出差人工费
				Float workingHoursWages = 0f;
				WageStandard wageStandard = findWSByUser(askForLeave
						.getLeavePersonCode()); // 根据工号查询工资模板
				if (wageStandard == null) {
				} else {
					InsuranceGold insuranceGold = findInsuranceGoldBylc(
							wageStandard.getLocalOrField(), wageStandard
									.getCityOrCountryside(), wageStandard
									.getPersonClass()); // 福利系数（计算公司缴纳的保险成本）
					if (insuranceGold == null) {
						// AlertMessagesServerImpl.addAlertMessages("保险缴纳比例管理",
						// "本年度福利系数尚未填写,请及时填写", "1");
						// return "本年度福利系数尚未填写，已通知相关责任人!";
					}else {
						// 当月个人人力成本
						workingHoursWages = (wageStandard.getGangweigongzi()
								+ wageStandard.getSsBase()
								* (insuranceGold.getQYoldageInsurance()
										+ insuranceGold.getQYmedicalInsurance()
										+ insuranceGold
										.getQYunemploymentInsurance()
										+ insuranceGold.getQYinjuryInsurance() + insuranceGold
										.getQYmaternityInsurance()) / 100 + wageStandard
										.getGjjBase()
										* insuranceGold.getQYhousingFund() / 100);
					}
				}

				Float basicWorkingHoursWages = workingHoursWages / SECONDS; // 工序中基本工时工资(秒工资=单工序总成本/21.5天)
				Float time = 0f;
				long timelong = 0l;
				try {
					timelong = Util
							.getYouXiaoSbTime(askForLeave.getLeaveStartDate(),
									askForLeave.getLeaveEndDate());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				time = (float) timelong;
				Float rgfei = basicWorkingHoursWages * time * 2 / 1000;
				askForLeave.setRgcost(rgfei);
				if (carInfor != null) {
					Float price = 0.35f;// 每公里单价
					if (carInfor != null) {
						if (carInfor.getPrice() != null
								&& carInfor.getZjDistance() != null) {
							price = carInfor.getPrice()
									/ carInfor.getZjDistance();
						}
						if (carInfor.getDrivePrice() != null) {
							price += carInfor.getDrivePrice();
						}
					}
					Double cost = (double) singleCar.getKilometers() * price;
					singleCar.setReCost(cost);
				} else if (credentials != null) {
					// 计算用车补贴

				}
				// if(singleCar.getBeforeodometer()==null){
				// return "请填写出车前里程表！";
				// }
				// 责任承担部门和审批人
				StringBuffer deptIdSb = new StringBuffer();
				StringBuffer userIdSb = new StringBuffer();
				List<Integer> didlist = new ArrayList<Integer>();
				List<Integer> uidlist = new ArrayList<Integer>();
				if(deptIds!=null&&deptIds.length>0){
					
					for (int n = 0; n < deptIds.length; n++) {
						if (!didlist.contains(deptIds[n])) {
							didlist.add(deptIds[n]);
							if (n == 0) {
								deptIdSb.append(deptIds[n]);
							} else {
								deptIdSb.append("," + deptIds[n]);
							}
						}
						if (userIds != null &&userIds.length>0 && !uidlist.contains(userIds[n])) {
							uidlist.add(userIds[n]);
							if (n == 0) {
								userIdSb.append(userIds[n]);
							} else {
								userIdSb.append("," + userIds[n]);
							}
						}
					}
				}
				String dpIds = deptIdSb.toString();
				if (dpIds != null && dpIds.length() > 0) {
					uIds = userIdSb.toString();
					List<String> dpNameList = totalDao
							.query("select dept from DeptNumber where id in("
									+ dpIds + ")");
					if (dpNameList.size() > 0) {
						StringBuffer dpNamesb = new StringBuffer();
						for (int n = 0; n < dpNameList.size(); n++) {
							if (n == 0) {
								dpNamesb.append(dpNameList.get(n));
							} else {
								dpNamesb.append("," + dpNameList.get(n));

							}
						}
						askForLeave.setFreeDeptIds(dpIds);
						askForLeave.setFreeDepts(dpNamesb.toString());
						if (b) {
							singleCar.setFreeDeptIds(dpIds);
							singleCar.setFreeDepts(dpNamesb.toString());
						}
					}
				}
				if (b) {
					boo = this.totalDao.save(singleCar);
					if (boo) {
						askForLeave.setSingleCarId(singleCar.getId());
						Integer epId;
						String processName = "";
						String dept = "";
						if ("省内".equals(singleCar.getSinglecarType())) {
							if (singleCar.getCar_useType() != null
									&& singleCar.getCar_useType().equals(
											"处理质量事故")) {
								processName = dept + "质量事故省内用车审核";
							} else {
								processName = dept + "省内用车审核";
							}
						} else {
							if (singleCar.getCar_useType() != null
									&& singleCar.getCar_useType().equals(
											"处理质量事故")) {
								processName = dept + "质量事故省外用车审核";
							} else {
								processName = dept + "省外用车审核";
							}
						}

						if (singleCar.getCar_number() != null) {
							if (credentials != null
									&& credentials.getCode() != null) {
								Users u = (Users) totalDao
										.getObjectByCondition(
												"from Users where code = ?",
												credentials.getCode());
								if (u != null) {
									uIds = u.getId() + "";// + "," + uIds
								}
								processName = "公车使用申请";
							}
						}

						try {
							epId = CircuitRunServerImpl.createProcessbf(
									processName, SingleCar.class, singleCar
											.getId(), "status", "id",
									"AskForLeaveAction!carDetail.action?id="
											+ singleCar.getId(), loginUser
											.getDept()
											+ "部门的用车申请 请您审核!", true, uIds);
							if (epId != null && epId > 0) {
								singleCar.setEpId(epId);
								totalDao.update(singleCar);
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(processName+"审批流程有误!");
						}
					}
				}
			}
			
			Float gzTime = 8f;
			Integer banciid = Util.getLoginUser().getBanci_id();
			if(banciid==null){
				throw new Exception("对不起，您没有班次，请联系管理员添加班次");
			}
			// 根据请假开始时间和结束时间获得天数和小时数
			float [] dayandHour = computeDayAndHourByTime(askForLeave.getLeaveStartDate(), askForLeave
					.getLeaveEndDate(), Util.getLoginUser().getId());
			Integer day = (int)dayandHour[0];
			Float hours = dayandHour[1];
			askForLeave.setLeaveDays((int)dayandHour[0]);// 保存天数
			askForLeave.setLeaveHours(dayandHour[1]);// 保存小时数
			askForLeave.setAccessStatus("未生成");// Lc_add
			askForLeave.setSubmitPerson(loginUser.getName());// 提交人(登录人姓名)
			askForLeave.setSubmitPersonCode(loginUser.getCode());// 保存提交人工号

			askForLeave.setSubmitPersonDept(loginUser.getDept());// 保存提交人所在部门
			askForLeave.setOperationDate(Util.getDateTime());// 保存当前系统操作时间
			if ("D".equals(askForLeave.getAppayTag())) {
				askForLeave.setApprovalStatus("同意");
			} else {
				askForLeave.setApprovalStatus("未审批");
			}

			askForLeave.setApprovalResult("未出门");
			String userCode = askForLeave.getLeavePersonCode();
			String hql = "from Users where code=?";
			Users user = (Users) totalDao.getObjectByCondition(hql, userCode);
			askForLeave.setLeaveUserCardId(user.getCardId());
			boo = boo & totalDao.save(askForLeave);

			
					
			// float house = banCi
			if ("事假".equals(askForLeave.getLeaveTypeOf())) {

				BanCi banci = null;
				if (user.getBanci_id() != null && user.getBanci_id() > 0) {
					banci = (BanCi) totalDao.get(BanCi.class, user
							.getBanci_id());
				}
				if (banci != null) {
					gzTime = banci.getGzTime() / 60f;// 小时

					BigDecimal bg = new BigDecimal(day + hours / gzTime);// 请假天数
					float qjday = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					// Float qjTime = askForLeave.getLeaveHours();
					// Float qjday = 0f;
					// if (qjTime > 0 && qjTime < gzTime) {
					// qjday = qjTime / gzTime;
					// } else if (qjTime == gzTime) {
					// qjday = 1f;
					// } else {
					// qjday = askForLeave.getLeaveDays() + 0f;
					// }
					WageStandard wageStandard = (WageStandard) totalDao
							.getObjectByCondition(
									" from WageStandard where code = ? order by id desc",
									user.getCode());
					if (wageStandard != null) {
						Float money = -(wageStandard.getGangweigongzi() / 21.75f)
								* qjday;
						DecimalFormat df = new DecimalFormat("#.##");
						money = Float.valueOf(df.format(money));
						RewardPunish rewardpunish = new RewardPunish();
						rewardpunish.setCode(user.getCode());// 工号
						rewardpunish.setUserId(user.getId());// 员工Id
						rewardpunish.setName(user.getName());// 姓名
						rewardpunish.setDate(new Date());
						rewardpunish.setDept(user.getDept());
						rewardpunish.setType("请假");
						rewardpunish.setMoney(money.doubleValue());
						String str = user.getName() + "请事假始于"
								+ askForLeave.getLeaveStartDate() + "止于"
								+ askForLeave.getLeaveEndDate() + " 共计" + day
								+ "天" + hours + "小时，扣除工资" + money + "元。";
						rewardpunish.setExplain(str);
						totalDao.save(rewardpunish);
					}
				}
			}
			// ------------------------------------------------------------------------------------------------
			/*
			 * 添加年休记录;
			 */
			// house;//每天工作时长
			if ("换休".equals(askForLeave.getLeaveTypeOf())) {
				String hql1 = " from  AnnualLeave where jobNum=? and status='换休'";
				AnnualLeave newannualLeave = (AnnualLeave) totalDao
						.getObjectByCondition(hql1, user.getCode());
				float surplus = newannualLeave.getSurplus()
						- askForLeave.getLeaveDays()
						- askForLeave.getLeaveHours() / gzTime;
				newannualLeave.setSurplus(surplus);
				totalDao.update(newannualLeave);

				// 换休有效期
				String queryOverTime = "from Overtime where applyCode = ? and huanxiu='是' and status='同意'";
				CodeTranslation codeTranslation = (CodeTranslation) totalDao
						.getObjectByCondition("from CodeTranslation where type='sys' and valueName='换休有效期'");
				List<Overtime> list = null;
				if (null == codeTranslation) {
					list = totalDao.query(queryOverTime, askForLeave
							.getLeavePersonCode());
				} else {
					if (Integer.parseInt(codeTranslation.getValueCode()) > 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.MONTH, -Integer
								.parseInt(codeTranslation.getValueCode()));
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String passDate = format.format(calendar.getTime());
						list = totalDao.query(queryOverTime
								+ " and startDate>?", askForLeave
								.getLeavePersonCode(), passDate);
					}
				}
				float subHour = askForLeave.getLeaveDays() * gzTime
						+ askForLeave.getLeaveHours();// 减去的时间
				if (null != list && list.size() > 0) {
					for (Overtime overtime : list) {
						if (null != overtime) {

							if (null == overtime.getUsablehxTime()) {
								overtime.setUsablehxTime(overtime
										.getOverTimeLong());
							}
							Float hoursTime = overtime.getUsablehxTime();// 小时(可用时间)

							if (subHour > 0) {
								if (subHour >= hoursTime) {
									//overtime.setHuanxiustatus("换休中");
									overtime.setUsablehxTime(0f);
									subHour = subHour - hoursTime;
								} else {
									overtime.setHuanxiustatus("换休中");
									overtime.setUsablehxTime(hoursTime
											- subHour);
									subHour = 0;
								}
							}
							totalDao.update(overtime);
							if (subHour == 0) {
								break;
							}
						}
					}
				}
			} else if ("年休".equals(askForLeave.getLeaveTypeOf())) {
				// 根据工号查询年休表
				String hql1 = " from  AnnualLeave  where jobNum=? and status='年休'";
				AnnualLeave newannualLeave = (AnnualLeave) totalDao
						.getObjectByCondition(hql1, user.getCode());
				// AnnualLeave saveaAnnualLeave=newannualLeave;
				// banci
				newannualLeave.setSurplus(newannualLeave.getSurplus()
						- askForLeave.getLeaveDays()
						- askForLeave.getLeaveHours() / gzTime);
				totalDao.update(newannualLeave);
			}
			// ------------------------------------------------------------------------------------------------
			/*************主管、经理请假流程(壮龙需求)**************/
			int code = totalDao.getCount("from CodeTranslation where type='sys' and valueName='主管请假加班'");
			Users ss= Util.getLoginUser();
			if(ss!=null&&code > 0&&("DM".equals(ss.getPost())||("DGM".equals(ss.getPost())))){
				String processName = "主管请假审批流程";
						String messa = askForLeave.getLeavePersonDept() + "部门 "
						+ askForLeave.getLeavePerson() + "请假" + day + "天,"
						+ hours + "小时。请您审批!";
				Integer epId;
				try {
					epId = CircuitRunServerImpl.createProcessbf(processName,
							AskForLeave.class, askForLeave.getLeaveId(),
							"approvalStatus", "leaveId", messa, true, uIds);
					if (epId != null && epId > 0) {
						askForLeave.setEpId(epId);
						totalDao.update(askForLeave);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(processName+"审批流程有误!");
				}
			}else {
				/**
				 * 请假审批流程 A、个人请假 B、经理级别请假 C、副总请假 D、总经理及以上级别 请假 一级： 一天之内 二级：两天之内 三级
				 * ：三天之内 公出：A 三级公出、B二级公出、C三级省内公出
				 * 
				 * 
				 */
				if ("公出".equals(askForLeave.getLeaveTypeOf())) {
					// 公出审批流程
					String shengnw = "";// 省内，省外
					if(askForLeave.getLeaveDays()!=null&&askForLeave.getLeaveDays()>=15&&!"国外".equals(askForLeave.getGongchuPlace())){
						shengnw = "二级";
					}
					String processName = "";// askForLeave.getLeavePersonDept();
//				if ("省内".equals(shengnw)) {
					// if ("A".equals(askForLeave.getAppayTag())) {
					// processName += "个人省内公出审批流程";
					//
					// } else if ("B".equals(askForLeave.getAppayTag())) {
					// processName += "经理省内公出审批流程";
					// } else if ("C".equals(askForLeave.getAppayTag())) {
					// processName += "副总省内公出审批流程";
					// }
//					processName += "省内";
					
//				} else if("省外".equals(shengnw)){// 省外
					// if ("A".equals(askForLeave.getAppayTag())) {
					// processName += "个人省外公出审批流程";
					// } else if ("B".equals(askForLeave.getAppayTag())) {
					// processName += "经理省外公出审批流程";
					// } else if ("C".equals(askForLeave.getAppayTag())) {
					// processName += "副总省外公出审批流程";
					// }
//					processName += "省外公出审批流程";
//				}else if("国外".equals(shengnw)){
//					processName += "国外公出审批流程";
//				}
					processName = askForLeave.getGongchuPlace()+askForLeave.getLeaveTypeOf()+shengnw+"审批流程";
					String messa = askForLeave.getLeavePersonDept() + "部门 "
					+ askForLeave.getLeavePerson() + "公出" + day + "天,"
					+ hours + "小时。请您审批!";
					Integer epId;
					try {
						epId = CircuitRunServerImpl.createProcessbf(processName,
								AskForLeave.class, askForLeave.getLeaveId(),
								"approvalStatus", "leaveId", messa, true, uIds);
						if (epId != null && epId > 0) {
							askForLeave.setEpId(epId);
							totalDao.update(askForLeave);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException(e+"审批流程有误!");
					}
				} else {
					if (boo && !"D".equals(askForLeave.getAppayTag())) {
						// 审批流程 部门+期限+A\B\C\D
						String processName = "请假审批流程";// askForLeave.getLeavePersonDept();
						String jishu = "";
						// 普通申购，项目申购
						String month = null;
						if (askForLeave.getLeaveStartDate() != null
								&& askForLeave.getLeaveStartDate().length() >= 7) {
							month = askForLeave.getLeaveStartDate().substring(0, 7);
						}
						String outfive = "";// 本月事病假超过5天
						boolean isoutfive = true;
						if ("A".equals(askForLeave.getAppayTag())) {
							// if (day > 2) {
							// processName += "三级";
							// } else if (day == 2) {
							// if (hours > 0) {
							// processName += "三级";
							// } else {
							// processName += "二级";
							// }
							// } else
							if (day >= 3) {
								jishu = "二级";
							}
//						else if (day == 3) {
//							if (hours > 0) {
//								jishu = "二级";
//							} else {
//								if (oneMonthOut(askForLeave
//										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
//									jishu = "二级";
//									outfive = "(本月事病假已达五天或五天以上)";
//								} else {
//									// processName += "一级";
//								}
//							}
//						} 
							else {
								if (oneMonthOut(askForLeave.getLeavePersonCode(),
										month, 5)) {// 该月累计天数达到或者超过五天
									jishu = "二级";
									outfive = "(本月事病假已达五天或五天以上)";
								} else {
									// processName += "一级";
								}
							}
							processName = jishu + processName;
						} else if ("B".equals(askForLeave.getAppayTag())) {
							if (day > 1) {
								jishu = "二级";
							} else if (day == 1) {
								if (hours > 0) {
									jishu = "二级";
								} else {
									if (oneMonthOut(askForLeave
											.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
										jishu = "二级";
										outfive = "(本月事病假已达五天或五天以上)";
									} else {
										// processName += "一级";
									}
								}
							} else {
								if (oneMonthOut(askForLeave.getLeavePersonCode(),
										month, 5)) {// 该月累计天数达到或者超过五天
									jishu = "二级";
									outfive = "(本月事病假已达五天或五天以上)";
								} else {
									// processName += "一级";
								}
							}
							processName = jishu + "经理" + processName;
						}
						// else if ("C".equals(askForLeave.getAppayTag())) {// C类已不用
						// if (oneMonthOut(askForLeave.getLeavePersonCode(),
						// month, 5)) {// 该月累计天数达到或者超过五天
						// processName += "一级";
						// outfive = "(本月事病假已达五天或五天以上)";
						// } else {
						// processName += "一级";
						// }
						// processName += "副总经理请假审批流程";
						// }
						String messa = askForLeave.getLeavePersonDept() + "部门 "
						+ askForLeave.getLeavePerson() + "请假" + day + "天,"
						+ hours + "小时。请您审批!" + outfive;
						Integer epId;
						try {
							epId = CircuitRunServerImpl.createProcess(processName,
									AskForLeave.class, askForLeave.getLeaveId(),
									"approvalStatus", "leaveId", messa, true);
							if (epId != null && epId > 0) {
								askForLeave.setEpId(epId);
								totalDao.update(askForLeave);
							}
						} catch (Exception e) {
							throw new RuntimeException(e.toString());
						}
					}
				}
			}
			if ("代理请假".equals(askForLeave.getLeaveType())) {
				ActionContext.getContext().getSession().put(TotalDao.users,
						loginUser);// 当前登录人存入Session
			}
			return boo + "";
		}
		return "未填写请假内容!";
	}

	@Override
	public boolean saveLeave1(AskForLeave askForLeave) {
		if (askForLeave != null) {
			String hql2 = "from Users where code=?";
			Users loginUser = (Users) totalDao.getObjectByCondition(hql2,
					askForLeave.getLeavePersonCode());
			// 保存用户到session中
			ActionContext.getContext().getSession().put(TotalDao.users,
					loginUser);

			//			Calendar nowDate = Calendar.getInstance(), oldDate = Calendar
//					.getInstance();
//			nowDate.setTime(Util.StringToDate(askForLeave.getLeaveEndDate(),
//					"yyyy-MM-dd HH:mm:ss"));// 设置为结束时间
//			oldDate.setTime(Util.StringToDate(askForLeave.getLeaveStartDate(),
//					"yyyy-MM-dd HH:mm:ss"));// 设置为开始时间
//			long timeNow = nowDate.getTimeInMillis();
//			long timeOld = oldDate.getTimeInMillis();
//			float timess = timeNow - timeOld;
//			float allHour = timess / (1000 * 60 * 60);// 化为小时
//			day = (int) (allHour / 24);
//			hours = 0;
//			if (allHour % 24 >= 8) {
//				day++;
//			} else {
//				hours = allHour % 24;
//			}
			float[] dayAndHour=null;
			try {
				dayAndHour = computeDayAndHourByTime(askForLeave.getLeaveStartDate(), askForLeave.getLeaveEndDate(), loginUser.getId());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Integer day= (int)dayAndHour[0];
			Float hours = dayAndHour[1];
			askForLeave.setLeaveDays(day);// 保存天数
			askForLeave.setLeaveHours(hours);// 保存小时数
			askForLeave.setAccessStatus("未生成");// Lc_add
			askForLeave.setSubmitPerson(askForLeave.getLeavePerson());// 提交人(登录人姓名)
			askForLeave.setSubmitPersonCode(askForLeave.getLeavePersonCode());// 保存提交人工号
			askForLeave.setSubmitPersonDept(askForLeave.getLeavePersonDept());// 保存提交人所在部门
			askForLeave.setOperationDate(Util.getDateTime());// 保存当前系统操作时间
			if ("D".equals(askForLeave.getAppayTag())) {
				askForLeave.setApprovalStatus("同意");
			} else {
				askForLeave.setApprovalStatus("未审批");
			}

			askForLeave.setApprovalResult("未出门");
			String userCode = askForLeave.getLeavePersonCode();
			String hql = "from Users where code=?";
			Users user = (Users) totalDao.getObjectByCondition(hql, userCode);
			askForLeave.setLeaveUserCardId(user.getCardId());
			Boolean boo = false;
			try {
				boo = totalDao.save(askForLeave);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			/*
			 * 
			 * 
			 */
			// ------------------------------------------------------------------------------------------------
			/*
			 * 添加年休记录;
			 */
			/*
			 * //根据工号查询年休表 String hql1 = " from  AnnualLeave  where jobNum=?";
			 * AnnualLeave newannualLeave = (AnnualLeave)
			 * totalDao.getObjectByCondition(hql1, user.getCode());
			 * 
			 * AnnualLeave saveaAnnualLeave=newannualLeave;
			 * saveaAnnualLeave.setSurplus
			 * (newannualLeave.getSurplus()-askForLeave
			 * .getLeaveDays()-askForLeave.getLeaveHours()/8);
			 * totalDao.update(saveaAnnualLeave);
			 */
			// ------------------------------------------------------------------------------------------------

			/**
			 * 请假审批流程 A、个人请假 B、经理级别请假 C、副总请假 D、总经理及以上级别 请假 一级： 一天之内 二级：两天之内 三级
			 * ：三天之内 公出：A 三级公出、B二级公出、C三级省内公出
			 * 
			 * 
			 */
			if ("公出".equals(askForLeave.getLeaveTypeOf())) {
				// 公出审批流程
				String shengnw = askForLeave.getGongchuPlace();// 省内，省外
				// String processName = askForLeave.getLeavePersonDept();
				String processName = "";
				if ("省内".equals(shengnw)) {
					if ("A".equals(askForLeave.getAppayTag())) {
						processName += "个人省内公出审批流程";

					} else if ("B".equals(askForLeave.getAppayTag())) {
						processName += "经理省内公出审批流程";
					} else if ("C".equals(askForLeave.getAppayTag())) {
						processName += "副总省内公出审批流程";
					}
				} else {// 省外
					if ("A".equals(askForLeave.getAppayTag())) {
						processName += "个人省外公出审批流程";
					} else if ("B".equals(askForLeave.getAppayTag())) {
						processName += "经理省外公出审批流程";
					} else if ("C".equals(askForLeave.getAppayTag())) {
						processName += "副总省外公出审批流程";
					}
				}
				String messa = askForLeave.getLeavePersonDept() + "部门 "
						+ askForLeave.getLeavePerson() + "公出" + day + "天,"
						+ hours + "小时。请您审批!";
				Integer epId;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							AskForLeave.class, askForLeave.getLeaveId(),
							"approvalStatus", "leaveId", messa, true);
					if (epId != null && epId > 0) {
						askForLeave.setEpId(epId);
						totalDao.update(askForLeave);

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (boo && !"D".equals(askForLeave.getAppayTag())) {
					// 审批流程 部门+期限+A\B\C\D
					String processName = askForLeave.getLeavePersonDept();

					// 普通申购，项目申购
					String month = null;
					if (askForLeave.getLeaveStartDate() != null
							&& askForLeave.getLeaveStartDate().length() >= 7) {
						month = askForLeave.getLeaveStartDate().substring(0, 7);
					}
					String outfive = "";// 本月事病假超过5天
					if ("A".equals(askForLeave.getAppayTag())) {
						if (day > 2) {
							processName += "三级";
						} else if (day == 2) {
							if (hours > 0) {
								processName += "三级";
							} else {
								processName += "二级";
							}
						} else if (day > 1) {
							processName += "二级";
						} else if (day == 1) {
							if (hours > 0) {
								processName += "二级";
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName += "二级";
									outfive = "(本月事病假已达五天或五天以上)";
								} else {
									processName += "一级";
								}
							}
						} else {
							if (oneMonthOut(askForLeave.getLeavePersonCode(),
									month, 5)) {// 该月累计天数达到或者超过五天
								processName += "二级";
								outfive = "(本月事病假已达五天或五天以上)";
							} else {
								processName += "一级";
							}
						}
						processName += "个人请假审批流程";
					} else if ("B".equals(askForLeave.getAppayTag())) {
						if (day > 1) {
							processName += "二级";
						} else if (day == 1) {
							if (hours > 0) {
								processName += "二级";
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName += "二级";
									outfive = "(本月事病假已达五天或五天以上)";
								} else {
									processName += "一级";
								}
							}
						} else {
							if (oneMonthOut(askForLeave.getLeavePersonCode(),
									month, 5)) {// 该月累计天数达到或者超过五天
								processName += "二级";
								outfive = "(本月事病假已达五天或五天以上)";
							} else {
								processName += "一级";
							}
						}
						processName += "经理请假审批流程";
					} else if ("C".equals(askForLeave.getAppayTag())) {// C类已不用
						if (oneMonthOut(askForLeave.getLeavePersonCode(),
								month, 5)) {// 该月累计天数达到或者超过五天
							processName += "一级";
							outfive = "(本月事病假已达五天或五天以上)";
						} else {
							processName += "一级";
						}
						processName += "副总经理请假审批流程";
					}
					String messa = askForLeave.getLeavePersonDept() + "部门 "
							+ askForLeave.getLeavePerson() + "请假" + day + "天,"
							+ hours + "小时。请您审批!" + outfive;
					Integer epId;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								AskForLeave.class, askForLeave.getLeaveId(),
								"approvalStatus", "leaveId", messa, true);
						if (epId != null && epId > 0) {
							askForLeave.setEpId(epId);
							totalDao.update(askForLeave);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return boo;

		}
		return false;
	}

	// 查询请假信息列表
	@SuppressWarnings("unchecked")
	@Override
	public List<AskForLeave> selectAll() {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		String hql = "from AskForLeave as afl where afl.leavePerson=?";
		return totalDao.query(hql, users.getName());
	}

	// 根据Id查询 请假信息
	@Override
	public AskForLeave selectOne(int id) {
		// TODO Auto-generated method stub
		AskForLeave askForLeave = (AskForLeave) this.totalDao.getObjectById(
				AskForLeave.class, id);
		return askForLeave;
	}

	// 修改请假信息
	@Override
	public void updateLeave(AskForLeave askForLeave, AskForLeave oldAskForLeave) {
		oldAskForLeave = (AskForLeave) totalDao.getObjectById(
				AskForLeave.class, oldAskForLeave.getLeaveId());
		String oldStatus = oldAskForLeave.getApprovalStatus();
		if (askForLeave.getLeaveStartDate().equals(
				oldAskForLeave.getLeaveStartDate())
				&& askForLeave.getLeaveEndDate().equals(
						oldAskForLeave.getLeaveEndDate())) {
			askForLeave.setAppayTag(oldAskForLeave.getAppayTag());
			BeanUtils.copyProperties(askForLeave, oldAskForLeave, new String[] {
					"leaveId", "leaveType", "leavePerson", "leavePersonDept",
					"accessStatus", "leaveHours", "leaveDays",
					"leavePersonCode", "submitPerson", "submitPersonDept",
					"approvalStatus", "submitPersonCode", "approvalResult",
					"epId", "appayTag", "operationDate" });
			oldAskForLeave.setUpdateTime(Util.getDateTime());
			boolean boo = totalDao.update(oldAskForLeave);
			if (boo && "未审批".equals(oldStatus) && askForLeave.getEpId() == null) {
//				Calendar nowDate = Calendar.getInstance(), oldDate = Calendar
//						.getInstance();
//				nowDate.setTime(Util.StringToDate(
//						askForLeave.getLeaveEndDate(), "yyyy-MM-dd HH:mm:ss"));// 设置为结束时间
//				oldDate.setTime(Util.StringToDate(askForLeave
//						.getLeaveStartDate(), "yyyy-MM-dd HH:mm:ss"));// 设置为开始时间
//				long timeNow = nowDate.getTimeInMillis();
//				long timeOld = oldDate.getTimeInMillis();
//				float timess = timeNow - timeOld;
//				float allHour = timess / (1000 * 60 * 60);// 化为小时
//				day = (int) (allHour / 24);
				float[] dayAndHour=null;
				try {
					Integer usersId =  (Integer) totalDao.getObjectByCondition("select id from Users where code = ?",oldAskForLeave.getLeavePersonCode());
					dayAndHour = computeDayAndHourByTime(askForLeave.getLeaveStartDate(), 
							askForLeave.getLeaveEndDate(), usersId);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Integer day= (int)dayAndHour[0];
				Float hours = dayAndHour[1];
				/**
				 * 请假审批流程 A、个人请假 B、经理级别请假 C、副总请假 D、总经理及以上级别 请假 一级： 一天之内 二级：两天之内
				 * 三级 ：三天之内 公出：A 三级公出、B二级公出、C三级省内公出
				 * 
				 * 
				 */
				if ("公出".equals(askForLeave.getLeaveTypeOf())) {
					// 公出审批流程
					String shengnw = askForLeave.getGongchuPlace();// 省内，省外
					// String processName2 = askForLeave.getLeavePersonDept();
					String processName2 = "";
					if ("省内".equals(shengnw)) {
						if ("A".equals(askForLeave.getAppayTag())) {
							processName2 += "个人省内公出审批流程";

						} else if ("B".equals(askForLeave.getAppayTag())) {
							processName2 += "经理省内公出审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {
							processName2 += "副总省内公出审批流程";
						}
					} else {// 省外
						if ("A".equals(askForLeave.getAppayTag())) {
							processName2 += "个人省外公出审批流程";
						} else if ("B".equals(askForLeave.getAppayTag())) {
							processName2 += "经理省外公出审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {
							processName2 += "副总省外公出审批流程";
						}
					}
					String messa = askForLeave.getLeavePersonDept() + "部门 "
							+ askForLeave.getLeavePerson() + "公出" + day + "天,"
							+ hours + "小时。请您审批!";
					Integer epId;
					try {
						epId = CircuitRunServerImpl.createProcess(processName2,
								AskForLeave.class, askForLeave.getLeaveId(),
								"approvalStatus", "leaveId", messa, true);
						if (epId != null && epId > 0) {
							askForLeave.setEpId(epId);
							totalDao.update(oldAskForLeave);

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (!"D".equals(askForLeave.getAppayTag())) {
						// 审批流程 部门+期限+A\B\C\D
						String processName2 = askForLeave.getLeavePersonDept();

						// 普通申购，项目申购
						String month = null;
						if (askForLeave.getLeaveStartDate() != null
								&& askForLeave.getLeaveStartDate().length() >= 7) {
							month = askForLeave.getLeaveStartDate().substring(
									0, 7);
						}
						String outfive2 = "";// 本月事病假超过5天
						boolean isoutfive = true;
						if ("A".equals(askForLeave.getAppayTag())) {
							if (day > 2) {
								processName2 += "三级";
							} else if (day == 2) {
								if (hours > 0) {
									processName2 += "三级";
								} else {
									processName2 += "二级";
								}
							} else if (day > 1) {
								processName2 += "二级";
							} else if (day == 1) {
								if (hours > 0) {
									processName2 += "二级";
								} else {
									if (oneMonthOut(askForLeave
											.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
										processName2 += "二级";
										outfive2 = "(本月事病假已达五天或五天以上)";
									} else {
										processName2 += "一级";
									}
								}
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName2 += "二级";
									outfive2 = "(本月事病假已达五天或五天以上)";
								} else {
									processName2 += "一级";
								}
							}
							processName2 += "个人请假审批流程";
						} else if ("B".equals(askForLeave.getAppayTag())) {
							if (day > 1) {
								processName2 += "二级";
							} else if (day == 1) {
								if (hours > 0) {
									processName2 += "二级";
								} else {
									if (oneMonthOut(askForLeave
											.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
										processName2 += "二级";
										outfive2 = "(本月事病假已达五天或五天以上)";
									} else {
										processName2 += "一级";
									}
								}
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName2 += "二级";
									outfive2 = "(本月事病假已达五天或五天以上)";
								} else {
									processName2 += "一级";
								}
							}
							processName2 += "经理请假审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {// C类已不用
							if (oneMonthOut(askForLeave.getLeavePersonCode(),
									month, 5)) {// 该月累计天数达到或者超过五天
								processName2 += "一级";
								outfive2 = "(本月事病假已达五天或五天以上)";
							} else {
								processName2 += "一级";
							}
							processName2 += "副总经理请假审批流程";
						}
						String messa = askForLeave.getLeavePersonDept() + "部门 "
								+ askForLeave.getLeavePerson() + "请假" + day
								+ "天," + hours + "小时。请您审批!" + outfive2;
						Integer epId;
						try {
							epId = CircuitRunServerImpl.createProcess(
									processName2, AskForLeave.class,
									askForLeave.getLeaveId(), "approvalStatus",
									"leaveId", messa, true);
							if (epId != null && epId > 0) {
								oldAskForLeave.setEpId(epId);
								totalDao.update(oldAskForLeave);
								return;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
			if (boo && "打回".equals(oldStatus)) {
				CircuitRunServerImpl.updateCircuitRun(oldAskForLeave.getEpId());
			}
		} else {
			BeanUtils.copyProperties(askForLeave, oldAskForLeave, new String[] {
					"leaveId", "leaveType", "leavePerson", "leavePersonDept",
					"accessStatus", "leaveHours", "leaveDays",
					"leavePersonCode", "submitPerson", "submitPersonDept",
					"approvalStatus", "submitPersonCode", "approvalResult",
					"epId", "appayTag", "operationDate" });
			float[] dayAndHour=null;
			try {
				Integer usersId =  (Integer) totalDao.getObjectByCondition("select id from Users where code = ?",oldAskForLeave.getLeavePersonCode());
				dayAndHour = computeDayAndHourByTime(askForLeave.getLeaveStartDate(), 
						askForLeave.getLeaveEndDate(), usersId);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Integer day= (int)dayAndHour[0];
			Float hours = dayAndHour[1];
			String outfive = "";// 本月事病假超过5天
			boolean needChange = false;// 是否需要改变流程级别
			// 审批流程 部门+期限+A\B\C\D
			String processName = askForLeave.getLeavePersonDept();
			CircuitRun cr = null;
			if (oldAskForLeave.getEpId() != null) {
				cr = (CircuitRun) totalDao.getObjectById(CircuitRun.class,
						oldAskForLeave.getEpId());
			} else {
				/**
				 * 请假审批流程 A、个人请假 B、经理级别请假 C、副总请假 D、总经理及以上级别 请假 一级： 一天之内 二级：两天之内
				 * 三级 ：三天之内 公出：A 三级公出、B二级公出、C三级省内公出
				 * 
				 * 
				 */
				if ("公出".equals(askForLeave.getLeaveTypeOf())) {
					// 公出审批流程
					String shengnw = askForLeave.getGongchuPlace();// 省内，省外
					// String processName2 = askForLeave.getLeavePersonDept();
					String processName2 = "";
					if ("省内".equals(shengnw)) {
						if ("A".equals(askForLeave.getAppayTag())) {
							processName += "个人省内公出审批流程";

						} else if ("B".equals(askForLeave.getAppayTag())) {
							processName += "经理省内公出审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {
							processName += "副总省内公出审批流程";
						}
					} else {// 省外
						if ("A".equals(askForLeave.getAppayTag())) {
							processName += "个人省外公出审批流程";
						} else if ("B".equals(askForLeave.getAppayTag())) {
							processName += "经理省外公出审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {
							processName += "副总省外公出审批流程";
						}
					}
					String messa = askForLeave.getLeavePersonDept() + "部门 "
							+ askForLeave.getLeavePerson() + "公出" + day + "天,"
							+ hours + "小时。请您审批!";
					Integer epId;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								AskForLeave.class, askForLeave.getLeaveId(),
								"approvalStatus", "leaveId", messa, true);
						if (epId != null && epId > 0) {
							askForLeave.setEpId(epId);
							totalDao.update(oldAskForLeave);

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (!"D".equals(askForLeave.getAppayTag())) {
						// 审批流程 部门+期限+A\B\C\D
						String processName2 = askForLeave.getLeavePersonDept();

						// 普通申购，项目申购
						String month = null;
						if (askForLeave.getLeaveStartDate() != null
								&& askForLeave.getLeaveStartDate().length() >= 7) {
							month = askForLeave.getLeaveStartDate().substring(
									0, 7);
						}
						String outfive2 = "";// 本月事病假超过5天
						boolean isoutfive = true;
						if ("A".equals(askForLeave.getAppayTag())) {
							if (day > 2) {
								processName2 += "三级";
							} else if (day == 2) {
								if (hours > 0) {
									processName2 += "三级";
								} else {
									processName2 += "二级";
								}
							} else if (day > 1) {
								processName2 += "二级";
							} else if (day == 1) {
								if (hours > 0) {
									processName2 += "二级";
								} else {
									if (oneMonthOut(askForLeave
											.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
										processName2 += "二级";
										outfive2 = "(本月事病假已达五天或五天以上)";
									} else {
										processName2 += "一级";
									}
								}
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName2 += "二级";
									outfive2 = "(本月事病假已达五天或五天以上)";
								} else {
									processName2 += "一级";
								}
							}
							processName2 += "个人请假审批流程";
						} else if ("B".equals(askForLeave.getAppayTag())) {
							if (day > 1) {
								processName2 += "二级";
							} else if (day == 1) {
								if (hours > 0) {
									processName2 += "二级";
								} else {
									if (oneMonthOut(askForLeave
											.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
										processName2 += "二级";
										outfive2 = "(本月事病假已达五天或五天以上)";
									} else {
										processName2 += "一级";
									}
								}
							} else {
								if (oneMonthOut(askForLeave
										.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天
									processName2 += "二级";
									outfive2 = "(本月事病假已达五天或五天以上)";
								} else {
									processName2 += "一级";
								}
							}
							processName2 += "经理请假审批流程";
						} else if ("C".equals(askForLeave.getAppayTag())) {// C类已不用
							if (oneMonthOut(askForLeave.getLeavePersonCode(),
									month, 5)) {// 该月累计天数达到或者超过五天
								processName2 += "一级";
								outfive2 = "(本月事病假已达五天或五天以上)";
							} else {
								processName2 += "一级";
							}
							processName2 += "副总经理请假审批流程";
						}
						String messa = askForLeave.getLeavePersonDept() + "部门 "
								+ askForLeave.getLeavePerson() + "请假" + day
								+ "天," + hours + "小时。请您审批!" + outfive2;
						Integer epId;
						try {
							epId = CircuitRunServerImpl.createProcess(
									processName2, AskForLeave.class,
									askForLeave.getLeaveId(), "approvalStatus",
									"leaveId", messa, true);
							if (epId != null && epId > 0) {
								oldAskForLeave.setEpId(epId);
								totalDao.update(oldAskForLeave);
								return;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			if ((day == 1 && hours == 0) || (day < 1)) {// 请假天数没有超过一天
				String month = null;
				if (askForLeave.getLeaveStartDate() != null
						&& askForLeave.getLeaveStartDate().length() >= 7) {
					month = askForLeave.getLeaveStartDate().substring(0, 7);
				}
				if (cr != null && cr.getName() != null
						&& cr.getName().contains("一级")) {// 原来是一级审批
					if (oneMonthOut(askForLeave.getLeavePersonCode(), month, 5)) {// 该月累计天数达到或者超过五天原一级要变成二级
						totalDao.delete(cr);
						processName += "二级";
						needChange = true;
					}
				} else if (cr != null && cr.getName() != null
						&& !cr.getName().contains("一级")) {// 原不是一级审批
					if (!oneMonthOut(askForLeave.getLeavePersonCode(), month, 5)) {// 该月累计天数不足五天变一级审批
						totalDao.delete(cr);
						processName += "一级";
						needChange = true;
					}
				}
			} else {// 请假天数超过二天
				if (cr != null && cr.getName() != null
						&& cr.getName().contains("一级")) {// 原来是一级审批变二级或三级审批
					totalDao.delete(cr);
				}
			}
			if (oldAskForLeave.getEpId() == null) {
				needChange = true;
			}
			if (needChange) {
				outfive = "(本月事病假已达五天或五天以上)";
				if ("A".equals(oldAskForLeave.getAppayTag())) {
					if (day > 2) {
						processName += "三级";
					} else if (day == 2) {
						if (hours > 0) {
							processName += "三级";
						} else {
							processName += "二级";
						}
					} else if (day > 1) {
						processName += "二级";
					}
					// day <= 1不用加processName += "一级";，在上面已经加过了
					processName += "个人请假审批流程";
				} else if ("B".equals(oldAskForLeave.getAppayTag())) {
					if (day > 1) {
						processName += "二级";
					} else if (day == 1) {
						if (hours > 0) {
							processName += "二级";
						}
					}
					// day <= 1不用加processName += "一级";，在上面已经加过了
					processName += "经理请假审批流程";
				} else if ("C".equals(oldAskForLeave.getAppayTag())) {// C类已不用
					if (day > 1) {
						processName += "一级";
					} else if (day == 1) {
						if (hours > 0) {
							processName += "一级";
						}
					} else {
						processName = processName.replace("二级", "一级");// 副总经理请假只有一级
					}
					processName += "副总经理请假审批流程";
				}
				String messa = askForLeave.getLeavePersonDept() + "部门 "
						+ askForLeave.getLeavePerson() + "请假" + day + "天,"
						+ hours + "小时。请您审批!" + outfive;
				Integer epId;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							AskForLeave.class, askForLeave.getLeaveId(),
							"approvalStatus", "leaveId", messa, true);
					if (epId != null && epId > 0) {
						oldAskForLeave.setEpId(epId);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			oldAskForLeave.setLeaveDays(day);
			oldAskForLeave.setLeaveHours(hours);
			oldAskForLeave.setUpdateTime(Util.getDateTime());
			boolean boo = totalDao.update(oldAskForLeave);
			if (boo && "打回".equals(oldStatus)) {
				if (!needChange) {
					CircuitRunServerImpl.updateCircuitRun(oldAskForLeave
							.getEpId());
				}
			}
		}
	}

	// 删除请假信息
	@Override
	public void deleteLeave(Integer id) throws Exception {
		AskForLeave askForLeave = (AskForLeave) totalDao.getObjectById(
				AskForLeave.class, id);
		if (askForLeave != null) {
			
			if("事假".equals(askForLeave.getLeaveTypeOf())){
				BanCi banci = null;
				Users user = Util.getLoginUser();
				if (user.getBanci_id() != null && user.getBanci_id() > 0) {
					banci = (BanCi) totalDao.get(BanCi.class, user
							.getBanci_id());
				}
				if (banci != null) {
					Float gzTime = banci.getGzTime() / 60f;//小时
					float[] dayAndHour=null;
					try {
						dayAndHour = computeDayAndHourByTime(askForLeave.getLeaveStartDate(), 
								askForLeave.getLeaveEndDate(), user.getId());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Integer day= (int)dayAndHour[0];
					Float hours = dayAndHour[1];
					
					BigDecimal bg = new BigDecimal(day+ hours/gzTime);//请假天数
					float qjday = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
//					Float qjTime = askForLeave.getLeaveHours();
//					Float qjday = 0f;
//					if (qjTime > 0 && qjTime < gzTime) {
//						qjday = qjTime / gzTime;
//					} else if (qjTime == gzTime) {
//						qjday = 1f;
//					} else {
//						qjday = askForLeave.getLeaveDays() + 0f;
//					}
					//查询工资
					WageStandard wageStandard = (WageStandard) totalDao
							.getObjectByCondition(
									" from WageStandard where code = ? order by id desc",
									user.getCode());
					if (wageStandard != null) {
						Float money = (wageStandard.getGangweigongzi() / 21.75f)
								* qjday;
						DecimalFormat df = new DecimalFormat("#.##");
						money = Float.valueOf(df.format(money));
						RewardPunish rewardpunish = new RewardPunish();
						rewardpunish.setCode(user.getCode());// 工号
						rewardpunish.setUserId(user.getId());// 员工Id
						rewardpunish.setName(user.getName());// 姓名
						rewardpunish.setDate(new Date());
						rewardpunish.setDept(user.getDept());
						rewardpunish.setType("请假取消");
						rewardpunish.setMoney(money.doubleValue());
						String str = user.getName() + "删除事假始于"
								+ askForLeave.getLeaveStartDate() + "止于"
								+ askForLeave.getLeaveEndDate() + " 共计" + day
								+ "天"+hours+"小时，补工资" + money + "元。";
						rewardpunish.setExplain(str);
						totalDao.save(rewardpunish);
					}
				}
			}
			if ("换休".equals(askForLeave.getLeaveTypeOf())) {
				String hql = "from  AnnualLeave  where jobNum=? and status='换休'";
				AnnualLeave newannualLeave = (AnnualLeave) totalDao
						.getObjectByCondition(hql, askForLeave
								.getLeavePersonCode());

				Users users = (Users) totalDao.getObjectByCondition(
						"from Users where code =?", askForLeave
								.getLeavePersonCode());

				BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users
						.getBanci_id());
				float gzTime = banCi.getGzTime() / 60f;
				Integer leaveDays = askForLeave.getLeaveDays();
				Float leaveHours = askForLeave.getLeaveHours();
				float qjTime = (float) leaveDays + leaveHours / gzTime;//天数
				newannualLeave.setSurplus(newannualLeave.getSurplus() - qjTime);
				totalDao.update(newannualLeave);

				// Calendar calendar = Calendar.getInstance();
				// calendar.setTime(new Date());
				// calendar.add(Calendar.MONTH, -3);
				// SimpleDateFormat format = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// String passDate = format.format(calendar.getTime());
				// String hql1 =
				// "from Overtime where applyCode=? and huanxiu='是' and startDate>=? order by id desc";
				// List<Overtime> list = totalDao.query(hql1, askForLeave
				// .getLeavePersonCode(), passDate);
				// 换休有效期→查询系统设置
				String queryOverTime = "from Overtime where applyCode = ? and huanxiu='是' and status='同意'";
				CodeTranslation codeTranslation = (CodeTranslation) totalDao
						.getObjectByCondition("from CodeTranslation where type='sys' and valueName='换休有效期'");
				List<Overtime> list = null;
				if (null == codeTranslation) {
					list = totalDao.query(queryOverTime, askForLeave
							.getLeavePersonCode());
				} else {
					if (Integer.parseInt(codeTranslation.getValueCode()) > 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.MONTH, -Integer
								.parseInt(codeTranslation.getValueCode()));
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String passDate = format.format(calendar.getTime());
						list = totalDao.query(queryOverTime
								+ " and startDate>? order by id", askForLeave
								.getLeavePersonCode(), passDate);
					}
				}
				if (null != list && list.size() > 0) {
					Float qjHour = qjTime*60;
					for (Overtime overtime : list) {
						if (null == overtime.getUsablehxTime()) {
							overtime
									.setUsablehxTime(overtime.getOverTimeLong());
						}
						if (overtime != null
								&& overtime.getOverTimeLong() != null
								&& overtime.getUsablehxTime() != null) {
							if (overtime.getOverTimeLong() > overtime
									.getUsablehxTime()) {
								if (qjHour > overtime.getOverTimeLong()) {
									overtime.setUsablehxTime(overtime
											.getOverTimeLong());
									overtime.setHuanxiustatus(null);
									qjHour -= overtime.getUsablehxTime();
								} else {
									overtime.setUsablehxTime(overtime
											.getUsablehxTime()
											+ qjHour);
									qjHour = 0f;
								}
								totalDao.update(overtime);
							}
						}
					}
				}
				// banci-askForLeave.getLeaveDays()-askForLeave.getLeaveHours()/house
				// saveaAnnualLeave.setSurplus(newannualLeave.getSurplus());
				// totalDao.update(saveaAnnualLeave);
				// totalDao.query("from OverTime", agr)
			}
			Integer ii = askForLeave.getEpId();
			if (totalDao.delete(askForLeave)&&ii != null) {
				CircuitRunServerImpl.deleteCircuitRun(ii);
			}
		}
	}

	/***
	 * 按条件查询分页
	 * 
	 * @param askForLeave
	 *            实体类对象
	 * @param pageNo
	 *            每页显示条数
	 * @param pageSize
	 *            总记录数
	 * @param pageStatus
	 *            (audit：审批查询，findALL：查找全部，self：个人)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object[] selectAllByLeavePage(AskForLeave askForLeave, int pageNo,
			int pageSize, String pageStatus, String startDate, String endDate) {
		Users user = Util.getLoginUser();
		String hql = "from AskForLeave  where 1=1";
		Map<String, Object> map = null;
		if ("audit".equals(pageStatus)) {
			map = CircuitRunServerImpl.findAuditExeNode(AskForLeave.class,
					false);
			if (map != null) {
				hql += " and leaveId in(:entityId)";
			} else {
				return null;
			}
		} else if ("self".equals(pageStatus)) {
			hql += " and leavePersonCode='" + user.getCode() + "'";
		} else if ("history".equals(pageStatus)) {
			map = CircuitRunServerImpl
					.findAuditExeNode(AskForLeave.class, true);
			if (map != null) {
				hql += " and leaveId in(:entityId)";
			} else {
				return null;
			}
		}

		if (null != askForLeave) {
			if ("dept".equals(askForLeave.getSubmitPersonDept())) {
				String subDept = ((Users) Util.getLoginUser()).getDept();
				hql += " and leavePersonDept='" + subDept + "'";
			}
			if (askForLeave.getLeavePerson() != null
					&& !askForLeave.getLeavePerson().equals("")) {
				hql += " and leavePerson='" + askForLeave.getLeavePerson()
						+ "'";
			}
			if (askForLeave.getLeavePersonCode() != null
					&& !askForLeave.getLeavePersonCode().equals("")) {
				hql += " and leavePersonCode='"
						+ askForLeave.getLeavePersonCode() + "'";
			}
			if (askForLeave.getLeavePersonDept() != null
					&& !askForLeave.getLeavePersonDept().equals("")) {
				hql += " and leavePersonDept='"
						+ askForLeave.getLeavePersonDept() + "'";
			}
			if (askForLeave.getLeaveDays() != null) {
				hql += " and leaveDays='" + askForLeave.getLeaveDays() + "'";
			}
			if (askForLeave.getLeaveType() != null
					&& !askForLeave.getLeaveType().equals("")) {
				hql += " and leaveType='" + askForLeave.getLeaveType() + "'";
			}
			if (askForLeave.getLeaveStartDate() != null
					&& !askForLeave.getLeaveStartDate().equals("")) {
				hql += " and leaveStartDate='"
						+ askForLeave.getLeaveStartDate() + "'";
			}
			if (askForLeave.getLeaveTypeOf() != null
					&& !askForLeave.getLeaveTypeOf().equals("")) {
				hql += " and leaveTypeOf='" + askForLeave.getLeaveTypeOf()
						+ "'";
			}
		}
		// 判断时间
		if (null != startDate && !"".equals(startDate) && null != endDate
				&& !"".equals(endDate)) {
			hql += " and operationDate between '" + startDate + "' and '"
					+ endDate + "'";
		}
		int count = 0;
		List list = null;
		if ("audit".equals(pageStatus)) {
			list = totalDao.find(hql, map, pageNo, pageSize);
			Long countLong = totalDao.count("select count(*) " + hql, map);
			count = Integer.parseInt(countLong.toString());
		} else if ("history".equals(pageStatus)) {
			list = totalDao.find(hql, map, pageNo, pageSize);
			Long countLong = totalDao.count("select count(*) " + hql, map);
			count = Integer.parseInt(countLong.toString());
		} else {
			hql += " order by operationDate desc";
			list = totalDao.findAllByPage(hql, pageNo, pageSize);
			count = totalDao.getCount(hql);
		}
		Object[] o = { list, count };
		return o;

	}

	/**
	 * 个人打印队列
	 */
	@Override
	public List findPrintList() {
		Users users = (Users) Util.getLoginUser();
		String hql = " from AskForLeave  where leavePersonCode='"
				+ users.getCode() + "'";// and approvalResult='未出门' and
		// leaveDays>0
		return totalDao.query(hql);
	}

	/**
	 * 根据ID获取请假对象
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public AskForLeave findAskLeaveById(Integer id) {
		return (AskForLeave) totalDao.getObjectById(AskForLeave.class, id);
	}

	/**
	 * 门卫待出门当天记录
	 * 
	 * @param cpage
	 *            当前页
	 * @param pageSize
	 * @return
	 */
	@Override
	public Object[] findExitList(Integer cpage, Integer pageSize) {

		String curTime = Util.getDateTime("yyyy-MM-dd");
		String hql = " from AskForLeave where approvalStatus='同意' and approvalResult in('未出门','请假出门') and leaveStartDate like'%"
				+ curTime + "%'";
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		int count = totalDao.getCount(hql);
		Object[] obj = { list, count };
		return obj;
	}

	/**
	 * 出门刷卡
	 * 
	 * @param barcode
	 *            员工卡号
	 * @return
	 */
	@Override
	public String updateExit(String barcode) {
		String curTime = Util.getDateTime("yyyy-MM-dd");
		String hql = "from AskForLeave where leaveUserCardId=? and approvalResult=? and approvalStatus='同意' and leaveStartDate like'%"
				+ curTime + "%'";
		AskForLeave askFor = (AskForLeave) totalDao.getObjectByCondition(hql,
				barcode, "未出门");
		AskForLeave askForRetu = (AskForLeave) totalDao.getObjectByCondition(
				hql, barcode, "请假出门");
		if (null != askFor) {
			askFor.setExitTime(Util.getDateTime());
			askFor.setApprovalResult("请假出门");
			totalDao.update(askFor);
			return "exit";
		} else {
			if (null != askForRetu) {
				askForRetu.setReturnTime(Util.getDateTime());
				askForRetu.setApprovalResult("请假结束");
				totalDao.update(askForRetu);
				return "retu";
			}
			return null;
		}

	}

	// 根据Id查询审核信息
	@Override
	public String updateLeaveApproval(Integer id, String status) {
		if (id != null && status != null && status.length() > 0) {
			AskForLeave askForLeave = (AskForLeave) totalDao.getObjectById(
					AskForLeave.class, id);
			if ("ok".equals(status)) {
				CircuitRun circuitRun = circuitRunServer
						.findCircuitRunById(askForLeave.getEpId());

				// 处理审批流程
				String audit = circuitRunServer.updateExeNodeByCirId(
						askForLeave.getEpId(), true, "", false, null, true);
				status = "同意";

				// /--------------------------------------------------------------------
				// -----------------------年休-------------------------------------------
				circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, askForLeave.getEpId());
				if (circuitRun.getAllStatus().equals("同意")) {
					// 更新请假为"同意"
					askForLeave.setApprovalStatus(status);
					if ("年休".equals(askForLeave.getLeaveTypeOf())) {
						// 根据工号查询年休表
						String hql12 = " from  Users  where code=?";
						Users useers = (Users) totalDao.getObjectByCondition(
								hql12, askForLeave.getLeavePersonCode());// leavePersonCode

						String hql1 = " from  AnnualLeave  where status='年休' and  jobNum=?";
						AnnualLeave newannualLeave = (AnnualLeave) totalDao
								.getObjectByCondition(hql1, useers.getCode());
						AnnualLeave saveaAnnualLeave = newannualLeave;
						saveaAnnualLeave.setSurplus(newannualLeave.getSurplus()
								- askForLeave.getLeaveDays()
								- askForLeave.getLeaveHours() / 8);
						totalDao.update(saveaAnnualLeave);
					}
				}
				// ----------------------------换休-----------------------------------------
				/*
				 * if (circuitRun.getAllStatus().equals("同意") &&
				 * "换休".equals(askForLeave.getLeaveTypeOf())) { // 根据工号查询年休表
				 * String hql13 = " from  Users  where code=?"; Users useers =
				 * (Users) totalDao.getObjectByCondition(hql13,
				 * askForLeave.getLeavePersonCode());// leavePersonCode String
				 * hql1 = " from  AnnualLeave  where status='换休' and  jobNum=?";
				 * AnnualLeave huanxiu = (AnnualLeave) totalDao
				 * .getObjectByCondition(hql1, useers.getCode()); AnnualLeave
				 * saveaAnnualLeave1 = huanxiu; if (huanxiu!=null) {
				 * saveaAnnualLeave1.setSurplus(huanxiu.getSurplus() -
				 * askForLeave.getLeaveDays() - askForLeave.getLeaveHours() /
				 * 8); totalDao.update(saveaAnnualLeave1); } else { AnnualLeave
				 * newA=new AnnualLeave(); newA.setJobNum(useers.getCode());
				 * newA.setName(useers.getName());
				 * newA.setDept(useers.getDept());
				 * newA.setSurplus(0-askForLeave.getLeaveDays() -
				 * askForLeave.getLeaveHours() / 8); newA.setStatus("换休"); }
				 * 
				 * }
				 */
				// ----------------------------------------------------------------------
			} else if ("back".equals(status)) {
				circuitRunServer.updateExeNodeByCirId(askForLeave.getEpId(),
						false, "", false, null, true);
				status = "打回";
			} else {
				return "非法字符";
			}

			// askForLeave.setApprovalStatus(status);
			// totalDao.update(askForLeave);
		}
		return status;
	}

	public void exportExcel(AskForLeave askforleave) {
		String hql = totalDao.criteriaQueries(askforleave, null);
		hql += " order by id";
		List list = totalDao.find(hql);
		try {
			// HttpServletResponse response = (HttpServletResponse)
			// ActionContext
			// .getContext().get(ServletActionContext.HTTP_RESPONSE);
			// OutputStream os = response.getOutputStream();
			// response.reset();
			// response.setHeader("Content-disposition", "attachment; filename="
			// + new String("请假汇总".getBytes("GB2312"), "8859_1")
			// + ".xls");
			// response.setContentType("application/msexcel");
			// WritableWorkbook wwb = Workbook.createWorkbook(os);
			// WritableSheet ws = wwb.createSheet( "请假汇总", 0);
			// ws.setColumnView(4, 20);
			// ws.setColumnView(3, 10);
			// ws.setColumnView(2, 20);
			// ws.setColumnView(1, 12);
			// ws.setColumnView(5, 25);
			// ws.setColumnView(6, 25);
			// ws.setColumnView(10, 40);
			// WritableFont wf = new WritableFont(WritableFont.ARIAL, 25,
			// WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
			// Colour.BLACK);
			// WritableCellFormat wcf = new WritableCellFormat(wf);
			// wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			// wcf.setAlignment(Alignment.CENTRE);
			// jxl.write.Label label0 = new Label(0, 0, "请假汇总", wcf);
			// ws.addCell(label0);
			// ws.mergeCells(0, 0, 18, 0);
			// wf = new WritableFont(WritableFont.ARIAL, 8,
			// WritableFont.NO_BOLD,
			// false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			// WritableCellFormat wc = new WritableCellFormat(wf);
			//
			// // 定义单元格样式 定义格式 字体 下划线 颜色 斜体 粗体
			// // WritableCellFormat wcf1 = new WritableCellFormat(
			// // new WritableFont(WritableFont.ARIAL, 10)); // 单元格定义
			//
			// // 创建一个样式
			// wc.setAlignment(Alignment.CENTRE);
			// ws.addCell(new jxl.write.Label(0, 1, "编号", wc));
			// ws.addCell(new jxl.write.Label(1, 1, "请假类型"));
			// ws.addCell(new jxl.write.Label(2, 1, "请假人"));
			// ws.addCell(new jxl.write.Label(3, 1, "请假人工号"));
			// ws.addCell(new jxl.write.Label(4, 1, "部门"));
			//
			// ws.addCell(new jxl.write.Label(5, 1, "开始时间", wc));
			// ws.addCell(new jxl.write.Label(6, 1, "结束时间", wc));
			// ws.addCell(new jxl.write.Label(7, 1, "请假天数"));
			// ws.addCell(new jxl.write.Label(8, 1, "请假小时"));
			// ws.addCell(new jxl.write.Label(9, 1, "请假类型"));
			//
			// ws.addCell(new jxl.write.Label(10, 1, "请假原因", wc));
			// ws.addCell(new jxl.write.Label(11, 1, "提交人"));
			// ws.addCell(new jxl.write.Label(12, 1, "出门时间"));
			// ws.addCell(new jxl.write.Label(13, 1, "返回时间"));
			//
			// // 格式
			// ws.setRowView(1, 800);// 第二行的高度
			// ws.setColumnView(0, 10);// 第一列的宽度
			//
			// for (int i = 0; i < list.size(); i++) {
			// AskForLeave go = (AskForLeave) list.get(i);
			// ws.addCell(new jxl.write.Number(0, i + 2, i + 1));
			// ws.addCell(new Label(1, i + 2, go.getLeaveType()));
			// ws.addCell(new Label(2, i + 2, go.getLeavePerson()));
			// ws.addCell(new Label(3, i + 2, go.getLeavePersonCode()));
			// ws.addCell(new Label(4, i + 2, go.getLeavePersonDept()));
			//
			// ws.addCell(new Label(5, i + 2, go.getLeaveStartDate()));
			// ws.addCell(new Label(6, i + 2, go.getLeaveEndDate()));
			// ws.addCell(new Label(7, i + 2, Integer.toString(go
			// .getLeaveDays())));
			// ws
			// .addCell(new jxl.write.Number(8, i + 2, Float
			// .parseFloat(String.format("%.2f", go
			// .getLeaveHours()))));
			// ws.addCell(new Label(9, i + 2, go.getLeavePersonDept()));
			// // ws.addCell(new jxl.write.Number(5, i +
			// //
			// 2,Float.parseFloat(String.format("%.2f",go.getYingchuqin()))));
			// ws.addCell(new Label(10, i + 2, go.getLeaveReason()));
			// ws.addCell(new Label(11, i + 2, go.getSubmitPerson()));
			// ws.addCell(new Label(12, i + 2, go.getExitTime()));
			// ws.addCell(new Label(13, i + 2, go.getReturnTime()));
			// }
			// wwb.write();
			// wwb.close();
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("请假汇总".getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("请假汇总", 0);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			ws.setColumnView(5, 25);
			ws.setColumnView(6, 25);
			ws.setColumnView(10, 40);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 25,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcf.setAlignment(Alignment.CENTRE);
			jxl.write.Label label0 = new Label(0, 0, "请假汇总", wcf);
			ws.addCell(label0);
			ws.mergeCells(0, 0, 18, 0);
			wf = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);

			// // 定义单元格样式 定义格式 字体 下划线 颜色 斜体 粗体
			WritableCellFormat wcf2 = new WritableCellFormat(new WritableFont(
					WritableFont.ARIAL, 10)); // 单元格定义
			wcf2.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);// 添加细边框样式
			// 创建一个样式
			wc.setAlignment(Alignment.CENTRE);
			ws.addCell(new jxl.write.Label(0, 1, "编号", wc));
			ws.addCell(new jxl.write.Label(1, 1, "请假类型"));
			ws.addCell(new jxl.write.Label(2, 1, "请假人"));
			ws.addCell(new jxl.write.Label(3, 1, "请假人工号"));
			ws.addCell(new jxl.write.Label(4, 1, "部门"));

			ws.addCell(new jxl.write.Label(5, 1, "开始时间", wc));
			ws.addCell(new jxl.write.Label(6, 1, "结束时间", wc));
			ws.addCell(new jxl.write.Label(7, 1, "请假天数"));
			ws.addCell(new jxl.write.Label(8, 1, "请假小时"));
			ws.addCell(new jxl.write.Label(9, 1, "假事类型"));

			ws.addCell(new jxl.write.Label(10, 1, "请假原因", wc));
			ws.addCell(new jxl.write.Label(11, 1, "提交人"));
			ws.addCell(new jxl.write.Label(12, 1, "出门时间"));
			ws.addCell(new jxl.write.Label(13, 1, "返回时间"));

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
				AskForLeave go = (AskForLeave) list.get(i);

				ws.addCell(new jxl.write.Number(0, i + 2, i + 1));
				ws.addCell(new Label(1, i + 2, go.getLeaveType()));
				ws.addCell(new Label(2, i + 2, go.getLeavePerson()));
				ws.addCell(new Label(3, i + 2, go.getLeavePersonCode()));
				ws.addCell(new Label(4, i + 2, go.getLeavePersonDept()));

				ws.addCell(new Label(5, i + 2, go.getLeaveStartDate()));
				ws.addCell(new Label(6, i + 2, go.getLeaveEndDate()));
				ws.addCell(new Label(7, i + 2, Integer.toString(go
						.getLeaveDays())));

				ws
						.addCell(new jxl.write.Number(8, i + 2, Float
								.parseFloat(String.format("%.2f", go
										.getLeaveHours()))));
				ws.addCell(new Label(9, i + 2, go.getLeaveTypeOf()));
				ws.addCell(new Label(10, i + 2, go.getLeaveReason()));
				ws.addCell(new Label(11, i + 2, go.getSubmitPerson()));
				ws.addCell(new Label(12, i + 2, go.getExitTime()));
				ws.addCell(new Label(13, i + 2, go.getReturnTime()));

			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	// getter和setter
	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public boolean oneMonthOut(String code, String month, int outday) {
		// TODO Auto-generated method stub
		List<AskForLeave> leaveList = totalDao
				.query(" from AskForLeave where approvalStatus='同意' and leaveTypeOf !='公出' and leavePersonCode='"
						+ code + "' and leaveStartDate like '" + month + "-%'");
		if (leaveList.size() > 0) {// 该月所有请假
			int day = 0;
			int hour = 0;
			for (AskForLeave leave : leaveList) {
				if (leave.getLeaveDays() != null) {
					day += leave.getLeaveDays();
				}
				if (leave.getLeaveHours() != null) {
					hour += leave.getLeaveHours();
				}
			}
			day += hour / 8;
			if (hour % 8 > 0) {
				day++;
			}
			if (day >= outday) {// 该月累计天数超过五天提交
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public String checkTime(String leaveStartDate, String leaveEndDate,
			String leavePersonCode, Integer LeaveId, String tag) {
		// TODO Auto-generated method stub
		if (!"noTime".equals(tag)) {
			long starttime = Util.StringToDate(leaveStartDate,
					"yyyy-MM-dd HH:mm:ss").getTime();
			long nowtime = Util.StringToDate(Util.getDateTime(),
					"yyyy-MM-dd HH:mm:ss").getTime()-1000*60*10;//十分钟前开始
			if (nowtime - starttime > 0) {
				return "请假开始时间不能早于当前时间!";
			} else {
				long l = Util.getWorkTime1(leaveEndDate, leaveStartDate);
				if (l >= 0) {
					return "请假结束时间不能早于或等于开始时间！";
				}else if (l > -600000) {
					return "请假结束时间间隔不能小于10分钟！";
				}
			}
		}
		String sql = null;
		List list = null;
		if (LeaveId != null) {
			sql = "from AskForLeave where ((leaveStartDate between ? and ?) or (leaveEndDate between ? and ?)) and leavePersonCode=? and leaveId!=? and approvalStatus!='打回'";
			list = totalDao.query(sql, leaveStartDate, leaveEndDate,
					leaveStartDate, leaveEndDate, leavePersonCode, LeaveId);
		} else {
			sql = "from AskForLeave where ((leaveStartDate between ? and ?) or (leaveEndDate between ? and ?)) and leavePersonCode=? and approvalStatus!='打回'";
			list = totalDao.query(sql, leaveStartDate, leaveEndDate,
					leaveStartDate, leaveEndDate, leavePersonCode);
		}
		if (list.size() > 0) {
			return "您的请假时间已经存在申请记录,请检查后重试!谢谢!";
		}
		return "true";
	}

	@Override
	public Map<Integer, Object> findSingleCarsByCondition(SingleCar singleCar,
			int pageNo, int pageSize, String pageStatus) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (singleCar == null) {
			singleCar = new SingleCar();
		}
		String hql = totalDao.criteriaQueries(singleCar, null, null);
		if (pageStatus == null || pageStatus.equals("single")) {
			Users user = Util.getLoginUser();
			hql += " and car_userCode='" + user.getCode() + "' or pilotname='"
					+ user.getName() + "'";
		}
		hql += " order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public SingleCar getSingleCarById(int id) {
		// TODO Auto-generated method stub

		return (SingleCar) totalDao.getObjectById(SingleCar.class, id);
	}

	@Override
	public String backCar(SingleCar singleCar) {
		// TODO Auto-generated method stub
		SingleCar old = getSingleCarById(singleCar.getId());
		// old.setBeforeodometer(singleCar.getBeforeodometer());
		// old.setEndodometer(singleCar.getEndodometer());
		// old.setKilometers(singleCar.getEndodometer()-singleCar.getBeforeodometer());
		old.setKilometers(singleCar.getKilometers());
		old.setUseStatus("已归还");
		totalDao.update(old);
		InEmployeeCarInfor carInfor = (InEmployeeCarInfor) totalDao
				.getObjectByCondition(
						"from InEmployeeCarInfor where nplates=?", old
								.getCar_number());
		Float price = 0.35f;// 每公里单价
		if (carInfor != null) {
			if (carInfor.getPrice() != null && carInfor.getZjDistance() != null) {
				price = carInfor.getPrice() / carInfor.getZjDistance();
			}
			if (carInfor.getDrivePrice() != null) {
				price += carInfor.getDrivePrice();
			}
		}
		Double cost = (double) price * old.getKilometers();
		old.setCost(cost);
		AskForLeave askForLeave = (AskForLeave) totalDao.getObjectByCondition(
				"from AskForLeave where singleCarId=?", singleCar.getId());
		if (askForLeave != null && askForLeave.getLeaveObjectType() != null) {
			if (askForLeave.getRgcost() != null) {
				cost += askForLeave.getRgcost();
			}
			if (askForLeave.getLeaveObjectType().equals("生产")) {
				String neirong = askForLeave.getLeaveObjectNeirong();
				if (neirong != null && neirong.length() > 0) {
					String str[] = neirong.split(" /");
					String markId = str[0].substring(3, str[0].length() - 1);
					String selfCard = null;
					if (str[1].contains(",")) {
						selfCard = str[1].substring(3, str[1].length() - 6);
					} else {
						selfCard = str[1].substring(3, str[1].length());
					}
					Procard procard = (Procard) totalDao.getObjectByCondition(
							"from Procard where markId=? and selfCard=?",
							markId, selfCard);
					if (procard != null) {
						String proStatus = null;
						if (procard.getClvFei() == null) {
							procard.setClvFei(Float.parseFloat(cost + ""));
						} else {
							procard.setClvFei(Float.parseFloat((procard
									.getClvFei() + cost)
									+ ""));
						}
						totalDao.update(procard);
						if (procard.getProductStyle().equals("批产")) {
							proStatus = "批产阶段";
						} else {
							String type = (String) totalDao
									.getObjectByCondition(
											"select type from ProjectOrderPart where markId=? and  projectOrder.id =?",
											procard.getMarkId(), procard
													.getPlanOrderId());
							if (type != null && type.equals("首件")) {
								proStatus = "首件阶段";
							} else {
								proStatus = "试制阶段";
							}
						}
						QuotedPrice qp = (QuotedPrice) totalDao
								.getObjectByCondition(
										"from QuotedPrice where markId=? and procardStyle='总成'",
										markId);
						if (qp != null) {
							QuotedPriceCost quotedPriceCost = new QuotedPriceCost();
							quotedPriceCost.setProStatus(proStatus);
							quotedPriceCost.setTzMoney(0d);
							quotedPriceCost.setUserName(old.getCar_usename());
							quotedPriceCost.setUserCode(old.getCar_userCode());
							quotedPriceCost.setDept(old.getVar_dept());
							quotedPriceCost.setApplyStatus("同意");
							quotedPriceCost.setSource("公出申报");
							quotedPriceCost.setAddTime(Util.getDateTime());
							quotedPriceCost.setSinleCarId(singleCar.getId());
							quotedPriceCost.setMoney((double) cost);
							// quotedPriceCost.setProStatus(qp.getStatus());
							quotedPriceCost.setMarkId(qp.getMarkId());
							quotedPriceCost.setQpId(qp.getId());
							quotedPriceCost.setCostType("差旅费");
							// 没有审批结点直接同意
							// 重新计算零件总费用
							if (qp.getRealAllfy() != null) {
								qp.setRealAllfy(qp.getRealAllfy()
										+ quotedPriceCost.getMoney());
							} else {
								qp.setRealAllfy(quotedPriceCost.getMoney());
							}
							if (qp.getYingkui() == null) {
								qp.setYingkui(0d - qp.getRealAllfy());
							} else {
								qp.setYingkui(qp.getYingkui()
										- quotedPriceCost.getMoney());
							}
							totalDao.update(qp);
							quotedPriceCost.setApplyStatus("同意");
							totalDao.save(quotedPriceCost);
						}
					}

				}
			} else if (askForLeave.getLeaveObjectType().equals("项目")) {
				String neirong = askForLeave.getLeaveObjectNeirong();
				String str[] = neirong.split(" /");
				String proNumber = null;
				if (str != null && str.length > 0) {
					proNumber = str[0];
				}
				if (proNumber != null && proNumber.length() > 0) {
					List<QuotedPrice> qpList = totalDao
							.query(
									"from QuotedPrice where proId=(select id from ProjectManage where projectNum=?)",
									proNumber);
					if (qpList != null && qpList.size() > 0) {
						Double singleCost = cost / qpList.size();
						for (QuotedPrice qp : qpList) {
							QuotedPriceCost quotedPriceCost = new QuotedPriceCost();
							quotedPriceCost.setProStatus(qp.getStatus());
							quotedPriceCost.setTzMoney(0d);
							quotedPriceCost.setUserName(old.getCar_usename());
							quotedPriceCost.setUserCode(old.getCar_userCode());
							quotedPriceCost.setDept(old.getVar_dept());
							quotedPriceCost.setApplyStatus("同意");
							quotedPriceCost.setSource("公出申报");
							quotedPriceCost.setAddTime(Util.getDateTime());
							quotedPriceCost.setSinleCarId(singleCar.getId());
							quotedPriceCost.setMoney(singleCost);
							// quotedPriceCost.setProStatus(qp.getStatus());
							quotedPriceCost.setMarkId(qp.getMarkId());
							quotedPriceCost.setQpId(qp.getId());
							quotedPriceCost.setCostType("差旅费");
							// 没有审批结点直接同意
							// 重新计算零件总费用
							if (qp.getRealAllfy() != null) {
								qp.setRealAllfy(qp.getRealAllfy()
										+ quotedPriceCost.getMoney());
							} else {
								qp.setRealAllfy(quotedPriceCost.getMoney());
							}
							if (qp.getYingkui() == null) {
								qp.setYingkui(0 - qp.getRealAllfy());
							} else {
								qp.setYingkui(qp.getYingkui()
										- quotedPriceCost.getMoney());
							}
							totalDao.update(qp);
							quotedPriceCost.setApplyStatus("同意");
							totalDao.save(quotedPriceCost);
						}
					}
				}

			}
		}
		return "true";
	}

	@Override
	public AskForLeave getLeaveByCarId(int id) {
		// TODO Auto-generated method stub
		return (AskForLeave) totalDao.getObjectByCondition(
				"from AskForLeave where singleCarId=?", id);
	}

	// 查找百分比通过户籍
	@SuppressWarnings("unchecked")
	public InsuranceGold findInsuranceGoldBylc(String localOrField,
			String cityOrCountryside, String personClass) {
		if (localOrField != null && cityOrCountryside != null) {
			String dateTime = totalDao.getDateTime("yyyy-MM-dd");
			String hql = "from InsuranceGold where localOrField=? and cityOrCountryside=? and personClass=? and validityStartDate<=? and validityEndDate>=?";
			List list = totalDao.query(hql, localOrField, cityOrCountryside,
					personClass, dateTime, dateTime);
			if (list != null && list.size() > 0) {
				return (InsuranceGold) list.get(0);
			}
		}
		return null;
	}

	// 根据工号和卡号查找该用户当前使用的工资信息
	@SuppressWarnings("unchecked")
	public WageStandard findWSByUser(String code) {
		String hql = "from WageStandard where code=? and standardStatus='默认'";
		List list = totalDao.query(hql, code);
		WageStandard ws = null;
		if (list != null && list.size() > 0) {
			ws = (WageStandard) list.get(0);
		}
		return ws;
	}

	@Override
	public List getDeptUsers(int id) {
		// TODO Auto-generated method stub
		return totalDao.query("from UserDept where deptId=?", id);
	}

	@Override
	public List getCarNumber() {
		// TODO Auto-generated method stub
		List<String> carNumberList = totalDao
				.query("select nplates from InEmployeeCarInfor where carType='公车'");// or
		// (carType='个人'
		// and
		// ncode=?)
		// ,Util.getLoginUser().getCode()
		List<String> s = totalDao
				.query("select platenumber from Credentials where cardtype = '行驶证' and isGong = 'yes' and status = '同意'");
		carNumberList.addAll(s);
		return carNumberList;
	}

	// 申请销假
	@Override
	public boolean addqxAskForLeave(QxAskForLeave qxAskForLeave)throws Exception {
		if (qxAskForLeave != null) {
			Users user = Util.getLoginUser();
			qxAskForLeave.setUserName(user.getName());// 申请销假人姓名
			qxAskForLeave.setUserCode(user.getCode());// 申请销假人工号
			if (qxAskForLeave.getLeaveId() != null
					&& qxAskForLeave.getLeaveId() > 0) {
				AskForLeave askForLeave = (AskForLeave) totalDao.get(
						AskForLeave.class, qxAskForLeave.getLeaveId());

				// Calendar nowDate = Calendar.getInstance(), oldDate = Calendar
				// .getInstance();
				// nowDate.setTime(Util.StringToDate(qxAskForLeave.getEndDate(),
				// "yyyy-MM-dd HH:mm:ss"));// 设置为结束时间
				// oldDate.setTime(Util.StringToDate(qxAskForLeave.getStartDate(),
				// "yyyy-MM-dd HH:mm:ss"));// 设置为开始时间
				// long timeNow = nowDate.getTimeInMillis();
				// long timeOld = oldDate.getTimeInMillis();
				// float timess = timeNow - timeOld;
				// float allHour = timess / (1000 * 60 * 60);// 化为小时
				BanCi banci = (BanCi) totalDao.get(BanCi.class, user
						.getBanci_id());
				Float gzTime = banci.getGzTime() / 60f;
				if (gzTime == null || gzTime == 0) {
					gzTime = 8F;
				}
				// Float day = (Float) (allHour / 24);
				// hours = 0;
				// if (allHour % 24 >= gzTime) {
				// day++;
				// } else {
				// hours = allHour % 24;
				// day = hours / gzTime;
				// }
				// 获得请假天数和小时数
				float[] qxTime = computeDayAndHourByTime(qxAskForLeave.getStartDate(), qxAskForLeave
						.getEndDate(), user.getId());

				qxAskForLeave.setDays(qxTime[0]);
				qxAskForLeave.setHours(qxTime[1]);
				boolean bool = totalDao.save(qxAskForLeave);
				Integer epId = 0;
				String processName = "销假审批流程";
				// if(askForLeave.getAppayTag().equals("A")){
				// processName = "一级个人"+processName;
				// }else if(askForLeave.getAppayTag().equals("B")){
				// processName = "一级经理"+processName;
				// }else if(askForLeave.getAppayTag().equals("C")){
				// processName = "二级经理"+processName;
				// }else if(askForLeave.getAppayTag().equals("D")){
				// processName = "三级经理"+processName;
				// }
				if (bool) {
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								QxAskForLeave.class, qxAskForLeave.getId(),
								"epStatus", "id",
								"AskForLeaveAction!showqxAskForLeaveById.action?id="
										+ qxAskForLeave.getId()
										+ "&status=mingxi", user.getDept()
										+ "部门 " + user.getName() + "销假申请，请您审批",
								true);
						if (epId != null && epId > 0) {
							qxAskForLeave.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								qxAskForLeave.setEpStatus("同意");
//								float[] askTime = updateDayAndHourByTime(askForLeave.getLeaveStartDate(), askForLeave
//										.getLeaveEndDate(), user.getBanci_id());
//								float askDay = askTime[0]+askTime[1]/gzTime;
//								float qxDay = qxTime[0]+qxTime[1]/gzTime;
//								float gjDay = askDay-qxDay;
								//Float day1 = 0f;
//								if (askForLeave.getLeaveDays() != null
//										&& askForLeave.getLeaveDays() > 0) {
//									day1 = askForLeave.getLeaveDays() + 0f;
//								} else if (askForLeave.getLeaveHours() != null
//										&& askForLeave.getLeaveHours() > 0) {
//									day1 = askForLeave.getLeaveHours() / gzTime;
//								}
//								Float bjday = day1 - day;
//								WageStandard wageStandard = (WageStandard) totalDao
//										.getObjectByCondition(
//												" from WageStandard where code = ? order by id desc",
//												user.getCode());
//								if (wageStandard != null) {
//
//									Float gongzi = wageStandard
//											.getGangweigongzi();
//									Float money = (gongzi / 21.75f) * gjDay;
//									DecimalFormat df = new DecimalFormat("#.##");
//									money = Float.valueOf(df.format(money));
//									RewardPunish rewardpunish = new RewardPunish();
//									rewardpunish.setCode(user.getCode());// 工号
//									rewardpunish.setUserId(user.getId());// 员工Id
//									rewardpunish.setName(user.getName());// 姓名
//									rewardpunish.setDate(new Date());
//									rewardpunish.setDept(user.getDept());
//									rewardpunish.setType("销假");
//									rewardpunish.setMoney(money.doubleValue());
//									String str = user.getName() + "请事假始于"
//											+ qxAskForLeave.getStartDate()
//											+ "止于" + qxAskForLeave.getEndDate()
//											+ " 共计" + qxDay + "天，补工资" + money
//											+ "元。";
//									rewardpunish.setExplain(str);
//									totalDao.save(rewardpunish);
//									askForLeave.setLeaveStartDate(qxAskForLeave
//											.getStartDate());
//									askForLeave.setLeaveEndDate(qxAskForLeave
//											.getEndDate());
//								}
							} else {
								qxAskForLeave.setEpStatus("未审批");
							}
							askForLeave.setSqStatus("已申请");
							totalDao.update(askForLeave);
							return totalDao.update(qxAskForLeave);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		}
		return false;
	}

	@Override
	public List<QxAskForLeave> showQxAskForLeave(Integer leaveId) {
		if (leaveId != null && leaveId > 0) {
			String userCode = Util.getLoginUser().getCode();
			String hql = " from QxAskForLeave where leaveId = ? and  userCode =?";
			return totalDao.query(hql, leaveId, userCode);
		}
		return null;
	}

	@Override
	public QxAskForLeave QxAskForLeaveByid(Integer id) {
		if (id != null && id > 0) {
			return (QxAskForLeave) totalDao.get(QxAskForLeave.class, id);
		}
		return null;
	}

	@Override
	public boolean delQxAskForLeave(QxAskForLeave qxAskForLeave) {
		if (qxAskForLeave != null && qxAskForLeave.getId() != null) {
			if (qxAskForLeave.getLeaveId() != null
					&& qxAskForLeave.getLeaveId() > 0) {
				AskForLeave askForLeave = (AskForLeave) totalDao.get(
						AskForLeave.class, qxAskForLeave.getLeaveId());
				// askForLeave.setSqStatus("");
				totalDao.update(askForLeave);
			}
			return totalDao.delete(qxAskForLeave);
		}
		return false;
	}

	// 获取个人工作时长，单位（小时）
	@Override
	public Float banciGzTime(String userCode) {
		Users users = (Users) totalDao
				.getObjectByCondition("from Users where code = '" + userCode
						+ "'");

		BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users
				.getBanci_id());
		Float gzTime = 8f; // 每天工作时长
		if (null != banCi && null != banCi.getGzTime()) {
			gzTime = banCi.getGzTime() / 60f;
		}
		if (gzTime == null || gzTime == 0) {
			gzTime = 8f;
		}
		return gzTime;
	}

	// 根据请假开始时间和结束时间获得天数和小时数
	@Override
	public float[] computeDayAndHourByTime(String askForLeaveStartTime,
			String askForLeaveEndTime, Integer usersId)throws Exception {
		if (usersId != null) {
			Integer gzTime = null; //工作时间（分钟）
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(Util.StringToDate(askForLeaveStartTime, "yyyy-MM-dd HH:mm:ss"));
				int dayStart = calendar.get(Calendar.DAY_OF_YEAR);

				calendar.setTime(Util.StringToDate(askForLeaveEndTime, "yyyy-MM-dd HH:mm:ss"));
				int dayEnd = calendar.get(Calendar.DAY_OF_YEAR);
				int dayTime = dayEnd - dayStart;// 相隔天数
				dayTime += 1;

				// 开始时间和结束时间
				long startTime = Util.StringToDate(askForLeaveStartTime.substring(11, 19), "HH:mm:ss")
						.getTime();// 当天开始时间
				long endTime = Util.StringToDate(askForLeaveEndTime.substring(11, 19), "HH:mm:ss")
						.getTime();// 当天结束时间
				long fStartTime = startTime;
				long fEndTime = endTime;

				long millisecond = 0;// 请假总秒数
				
				// 循环天数
				for (int i = 0; i < dayTime; i++) {
					startTime = fStartTime;
					endTime = fEndTime;
					// 设置开始时间，天数累加
					calendar.setTime(Util.StringToDate(askForLeaveStartTime, "yyyy-MM-dd HH:mm:ss"));
					calendar.add(Calendar.DAY_OF_YEAR, i);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String dateTime = format.format(calendar.getTime());

					Map<String, Object> map = BanCiServerImpl.updateORsearchIfyouReWork(usersId,null, dateTime,totalDao);
					if(map!=null) {
						Integer banciId = (Integer) map.get("banciId");
						if(banciId==null) {
							continue;//没有班次
						}
						
						BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, banciId);
						if(banCi==null) {
							continue;
						}
						gzTime = banCi.getGzTime();
						Set<BanCiTime> banCiTimeSet = banCi.getBanCiTime();
						if(banCiTimeSet!=null &&  banCiTimeSet.size()>0){
							// 循环时间段
							for (BanCiTime banCiTime : banCiTimeSet) {
								startTime = fStartTime;
								endTime = fEndTime;

								long banCiTimeStart = Util.StringToDate(
										banCiTime.getStartTime(), "HH:mm:ss").getTime(); // 一天中时间段开始
								long banCiTimeEnd = Util.StringToDate(
										banCiTime.getEndTime(), "HH:mm:ss").getTime(); // 一天中时间段结束

								// 设置每天的开始时间和结束时间
								if (dayTime > 1) {// 请假1天或以上
									if (i == 0) {
										endTime = banCiTimeEnd;
									}
									if (i == dayTime - 1) {
										startTime = banCiTimeStart;
									}
									if (i > 0 && i < dayTime - 1) {
										endTime = banCiTimeEnd;
										startTime = banCiTimeStart;
									}
								}

								// 设置时间段有效时间
								if (endTime >= banCiTimeEnd) {
									endTime = banCiTimeEnd;
								}
								if (startTime <= banCiTimeStart) {
									startTime = banCiTimeStart;
								}

								// 时间相加
								if (startTime < endTime) {
									millisecond += (endTime - startTime);
								}
							}
						}else{
							//在班次表中查询
							long banCiStart = Util.StringToDate(banCi.getFirsttime(), "HH:mm:ss").getTime();
							long banCiEnd = Util.StringToDate(banCi.getFinishtime(), "HH:mm:ss").getTime();
							if (dayTime > 1) {// 请假1天或以上
								if (i == 0) {
									endTime = banCiEnd;
								}
								if (i == dayTime - 1) {
									startTime = banCiStart;
								}
								if (i > 0 && i < dayTime - 1) {
									endTime = banCiEnd;
									startTime = banCiStart;
								}
							}
							
							// 设置时间段有效时间
							if (endTime >= banCiEnd) {
								endTime = banCiEnd;
							}
							if (startTime <= banCiStart) {
								startTime = banCiStart;
							}

							// 时间相加
							if (startTime < endTime) {
								millisecond += (endTime - startTime);
							}
						}
					}
				}

				Float hours=0f;//按分钟取余
				Integer day=0;
				if(millisecond==0 && gzTime==null) {
				}else {
					day = (int) (millisecond / gzTime / 60 /1000);
					hours = millisecond / (1000 * 60f) % gzTime/60f;
				}
				return new float[] { day, hours };
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.toString());
			}
		}
 		return new float[] { 0f, 0f };
	}
}
