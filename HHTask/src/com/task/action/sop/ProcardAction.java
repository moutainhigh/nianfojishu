package com.task.action.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.MachineDayYZSJServer;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.RunningWaterCardServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.entity.Goods;
import com.task.entity.InternalOrderDetail;
import com.task.entity.Machine;
import com.task.entity.MachineDayYZSJ;
import com.task.entity.OrderManager;
import com.task.entity.Price;
import com.task.entity.ProductManager;
import com.task.entity.Provision;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.android.OsRecord;
import com.task.entity.android.OsTemplate;
import com.task.entity.caigou.MonthlySummary;
import com.task.entity.fin.UserMoneyDetail;
import com.task.entity.fin.UserMonthMoney;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.sop.BreakSubmit;
import com.task.entity.sop.BuHeGePin;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardCsBl;
import com.task.entity.sop.ProcardCsBlOrder;
import com.task.entity.sop.ProcardMaterialHead;
import com.task.entity.sop.ProcardSpecification;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardVo;
import com.task.entity.sop.ProcardWxTuiLiao;
import com.task.entity.sop.ProcessAndWgProcardTem;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforReceiveLog;
import com.task.entity.sop.ProcessInforWWApply;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessSaveLog;
import com.task.entity.sop.ProcessinforFuLiao;
import com.task.entity.sop.ProcessinforPeople;
import com.task.entity.sop.RunningWaterCard;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.qd.LogoStickers;
import com.task.util.DateUtil;
import com.task.util.MKUtil;
import com.task.util.Util;

/**
 * 流水卡片Action层
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings("unchecked")
public class ProcardAction extends ActionSupport {
	private Procard procard;
	private ProcessInfor process;
	private WaigouWaiweiPlan wwPlan;
	private ProcardServer procardServer;// 流水卡片对象
	private RunningWaterCardServer runningWCServer;// 卡片Server
	private MachineDayYZSJServer mdyServer;// 设备日常点检sever;
	private MachineDayYZSJ mdy;// 设备日点检;
	private ProcardSpecification procardSpecification;// 设备日点检;
	private OrderManager orderManager;// 订单信息
	private List<Procard> procardList;// 集合
	private List<ProcessInfor> processList;
	private List<WaigouWaiweiPlan> wwPlanList;// 集合
	private List<Machine> machineList;
	private List<Price> priceList;// 外委合同
	private List<ProcardSpecification> procardSpecificationList;
	private List<ProcessInforWWApply> pwwApplyList;
	private List<ProcessInforWWApplyDetail> pwwApplyDetailList;
	private List<ProcessInforReceiveLog> pgList;// 工序领取记录
	private ProcessInforWWApply pwwApply;
	private ProcessInforWWApplyDetail pwwApplyDetail;
	private ProcardCsBl csbl;
	private List<ProcardCsBl> csblList;
	private ProcardCsBlOrder csblorder;
	private List<ProcardCsBlOrder> csblorderList;
	private Users user;
	private List list;
	private Integer id;
	private Integer id2;
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private String pageStatus;// 页面状态
	private String viewStatus;// 查看状态
	private String wwxd;// 合同核对完下单
	private String tc;// 无弹窗
	private UserMonthMoney umm;
	private UserMoneyDetail umd;
	private Integer num;// 数量
	private UserMoneyDetail umd1;
	private ProcardMaterialHead pmHead;// 叫料母表
	private List<ProcardMaterialHead> pmHeadList;//
	private List<ProcessAndWgProcardTem> processAndWgProcardList; // 
	private Integer banci;// 版次
	private String jhStatus;
	private ProcessSaveLog processSaveLog;//
	private List<ProcardVo> procardVoList;
	private String processIds_str;
	private	List<Goods> goodsList;
	private List<Sell> sellList;
	private boolean isbcpqx;
	private OsTemplate ot;
	private Integer jyNumber;
	private OrderManager order;
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;

	private String startDate;// 开始时间
	private String endDate;// 截止时间
	private Integer allDay;
	private String cardNumber;// 卡号
	private Integer[] processIds;
	private Float[] processNumbers;
	private String[] processCards;
	private String responsible;// 负责人
	private Integer maxBelongLayer;// 最大层
	private String dateTime;// 日期
	private List<String> contentList;// 自检内容
	private List<String> isQualifiedList;// 自检结果
	private List<String> allMarkId;// 所有生产件号
	private String markid;
	private Integer processNO;
	private String fileName;
	private ProcardTemplate procardTemplate;
	private List<ProcessinforFuLiao> processinforFuLiaoList;// 工序辅料
	private List<ProcardWxTuiLiao> wxtuiliaoList;
	private Provision provision;
	private int[] selected;
	private Float[] lqCounts;
	private List listAll;
	private String markId;// 件号
	private String selfCard;// 批次
	private String processNos;
	private String processNames;
	private String[] markIds;//
	private Float[] breakscount;//
	private float nums;
	private float minnums;
	private Integer[] detailSelect;// 选择补打数组,审批数组
	private Integer[] detailSelect1;
	private String tag;// 标识
	private String weekds;
	private MonthlySummary ms;
	private MonthlySummary ms2;
	private MonthlySummary ms3;
	private MonthlySummary ms4;
	private Object[] obj;
	private List<Map<String, Object>> listPMaterHe = null;
	private String operation;// 操作页面i标识
	private String barcode;
	private BreakSubmit breaksubmit;// 不合格品提交记录
	private Integer flag;//
	private List<BreakSubmit> bsList;//
	private String code1;
	private String pwsswords;
	private Integer idd;
	private int pageSize1 = 5, pageNo1 = 1;
	private String firstTime;
	private String endTime;
	private String noZhuser;
	private Integer page;
	private Integer rows;
	private String remark;

	private Float sum1;
	private Float sum2;
	private Float sum3;
	
	private OsRecord osRecord;
	private List<OsRecord> osRecordList;
	private List<OsRecord> bugOsRecordList;
	private int bugNumber;

	public String execute() {
		pageStatus = "noCardLingGx";
		return findProcardByRunCard2();
	}

	/***
	 * TODO 确认采购 value="[${pageWgww.id},pageWgww.markId]" />
	 * 
	 * @return
	 */
	public String querencaigou() {
		if (selected != null && selected.length > 0) {
			try {
				String msg = procardServer.jihuowwPlanList(selected);
				if (msg.equals("true")) {
					errorMessage = "外委激活成功，请前往核对并申请审批!";
					url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dsq";
					return ERROR;
				}
			} catch (Exception e) {
				// TODO: handle exception
				errorMessage = e.getMessage();
				return ERROR;
			}
		}
		errorMessage = "请先选中要激活的外委序列!";
		// 判断是否件号相同
		// String markId1 = "";
		// String pici1 = "";
		// for (int i = 0; i < mrkIds.length; i++) {
		// String a = mrkIds[i];
		// String[] strarray = a.split(",");
		// if (i == 0) {
		// markId1 = markId1 + strarray[1];
		// pici1 = pici1 + strarray[2];
		// }
		// if (!markId1.equals(strarray[1])) {
		// errorMessage = "请选择相同件号的外委工序！！";
		// // return "querencaigouFailure";
		// }
		// if (!pici1.equals(strarray[2])) {
		// errorMessage = "请选择相同的批次！！";
		// // return "querencaigouFailure";
		// }
		// }
		// if (errorMessage != null) {
		// url =
		// "ProcardAction!findNeedjhWgWwPlanList.action?pageStatus=wwPlan";
		// } else {
		// procard = procardServer.listMarkIdselfCard(mrkIds);
		// // listAll=procardServer.listByIdAll(mrkIds);
		// listAll = procardServer
		// .getProcessInforById(procard.getId(), mrkIds);
		// // url="/System/SOP/produce/WaigouWaiweiPlan_list.jsp";
		// processNumbers = new Float[mrkIds.length];
		// processIds = new Integer[mrkIds.length];
		// nums = 0F;
		// for (int i = 0; i < mrkIds.length; i++) {
		// String a = mrkIds[i];
		// String[] strarray = a.split(",");
		// processIds[i] = Integer.parseInt(strarray[4].replace("]", "")
		// .toString());
		// processNumbers[i] = Float.parseFloat(strarray[3].toString());
		// nums = nums + Float.parseFloat(strarray[3].toString());
		// }
		// markid = "";
		// for (int i = 0; i < mrkIds.length; i++) {
		// String a = mrkIds[i];
		// String[] strarray = a.split(",");
		// String selfCard1 = strarray[4].replace("]", "");
		// markid += Integer.parseInt(selfCard1.toString()) + ",";
		// }
		// markid += "0";
		// return "querencaigou";
		// }
		return ERROR;
	}

	/*
	 * public String querencaigou() { wwPlan =
	 * procardServer.ByIdWaigouWaiweiPlan(wwPlan.getId());
	 * wwPlan.setStatus("已激活"); procardServer.updateWaigouWaiweiPlan(wwPlan);
	 * OutSourcingWorkList newos = new OutSourcingWorkList();
	 * 
	 * 
	 * 
	 * // private String outScourceComp;// 外委厂家
	 * newos.setMarkID(wwPlan.getMarkId());// 件号
	 * newos.setPartName(wwPlan.getProName());// 零件名称
	 * newos.setLotId(wwPlan.getSelfCard());// 批次号 // private String number;//
	 * 外委工作单编号（条码） // private String unit;// 单位
	 * newos.setOutSourceCount(wwPlan.getNumber());// 外委数量
	 * newos.setReceiveCount(wwPlan.getNumber());// 接收数量 // private Float
	 * breakCount;//损坏数量 Users user = (Users) Util.getLoginUser();
	 * newos.setCode(user.getCode());// 外委人工号
	 * newos.setUsername(user.getName());// 外委人
	 * newos.setReceivePerson(user.getName());// 接收人
	 * newos.setOutSourceTime(Util.getDateTime("yyyy-MM-dd HH:ss:mm"));// 外委时间
	 * newos.setStatus("结算");// 状态（外委，出厂，入厂，接收，结算）
	 * newos.setLeaveOutTime(Util.getDateTime("yyyy-MM-dd HH:ss:mm"));// 出厂时间
	 * newos.setGetIntoTime(Util.getDateTime("yyyy-MM-dd HH:ss:mm"));// 进厂时间
	 * newos.setJiesuanTime(Util.getDateTime("yyyy-MM-dd HH:ss:mm"));// 结算时间 //
	 * private String explain;// 外委说明 // private String dept;// 外委班组
	 * procardServer.addOutSourcingWorkList(newos);
	 * 
	 * errorMessage = "操作成成功!!!"; url =
	 * "ProcardAction!findWgWwPlanList2.action?pageStatus=wwPlan"; return ERROR;
	 * }
	 */
	/*
	 * 
	 */
	public String bangding() {
		procardServer.addMarkIdZijian(procardTemplate.getId(), selected);
		errorMessage = "操作成功！！！";
		url = "ProcardAction!chakan.action?procardTemplate.id="
				+ procardTemplate.getId();
		return ERROR;
	}

	public String chakan() {
		listAll = procardServer.listbandingZijian(procardTemplate.getId());
		if (provision != null) {
			ActionContext.getContext().getSession().put("provision", provision);
		} else {
			provision = (Provision) ActionContext.getContext().getSession()
					.get("provision");
		}
		Object[] object = procardServer.listprovision(provision, Integer
				.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List<Provision>) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("ProcardAction!chakan.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "chakan";
	}

	/*
	 * 件号绑定自检项 列表
	 */
	public String listmarkId() {
		if (procardTemplate != null) {
			ActionContext.getContext().getSession().put("procardTemplate",
					procardTemplate);
		} else {
			procardTemplate = (ProcardTemplate) ActionContext.getContext()
					.getSession().get("procardTemplate");
		}
		Object[] object = procardServer.listmarkId(procardTemplate, Integer
				.parseInt(cpage), pageSize);

		if (object != null && object.length > 0) {
			list = (List<ProcardTemplate>) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this.setUrl("ProcardAction!listmarkId.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "listmarkId";
	}

	/**
	 * 查询流水卡片信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findAllProcard() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!findAllProcard.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != procard) {
			request.getSession().setAttribute("procard", procard);
		} else {
			procard = (Procard) request.getSession().getAttribute("procard");
		}
		Object[] obj = procardServer.findProcard(procard, startDate, endDate,
				Integer.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		return "findProcard";
	}

	/***************************************************************************
	 * 生成流水卡片
	 * 
	 * @return
	 */
	public String addProCard() {
		try {
			// BOM效验
			// errorMessage = procardServer.checkProcard(processIds,
			// ,tag);
			errorMessage = "true";
			if (errorMessage == null || !errorMessage.equals("true")) {
				url = "internalOrder_queryInternalOrderDetail.action?id=" + id
						+ "&pageStatus=" + pageStatus;
				return ERROR;
			}
				Object[] obj = procardServer.addProCard(id, processIds,
						processNumbers, processCards, pageStatus, tag, id2);
				errorMessage = (String) obj[0];
				procardList = (List) obj[1];
			
			
			// if (procardList.size() > 0) {
			// // 开始激活生产批次
			// for (Procard procard : procardList) {
			// procardServer.sendRunCard(procard, "绑定激活生产批次");
			// }
			// }
		} catch (Exception e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		}
		url = "internalOrder_queryInternalOrderDetail.action?id=" + id
				+ "&pageStatus=" + pageStatus;
		return "error";
	}

	/***************************************************************************
	 * 查询所有总成流水卡片(分页)
	 * 
	 * @param procardTemplate
	 * @return
	 */
	public String findAllProcards() {
		Users user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请登录!";
			return "error";
		}
		if (procard != null) {
			ActionContext.getContext().getSession().put("procard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"procard");
		}
		if (startDate != null) {
			ActionContext.getContext().getSession().put("startDate", startDate);
		} else {
			startDate = (String) ActionContext.getContext().getSession().get(
					"startDate");
		}
		if (endDate != null) {
			ActionContext.getContext().getSession().put("endDate", endDate);
		} else {
			endDate = (String) ActionContext.getContext().getSession().get(
					"endDate");
		}
		if (startDate != null && endDate != null && !startDate.equals("")
				&& !endDate.equals("")) {
			Date starttime = DateUtil.parseDate(startDate,
					"yyyy-MM-dd HH:mm:ss");
			Date endtime = DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss");
			if ((endtime.getTime() - starttime.getTime()) < 0) {
				errorMessage = "结束时间需要在起始时间之后";
				return "Procard_findProcardList";
			}
		}
		Object[] object = null;
		if (pageStatus != null && pageStatus.equals("noCardPlan")) {
			// 查询出所有待激活的产品
			pageSize = 30;
			// Procard jihuo_procard = new Procard();
			// jihuo_procard.setBelongLayer(1);
			// jihuo_procard.setStatus("初始");
			object = procardServer.findAllProcard(procard, Integer
					.parseInt(cpage), pageSize, startDate, endDate, pageStatus);
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				nums = (Float) object[2];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!findAllProcards.action?viewStatus="
						+ viewStatus + "&pageStatus=" + pageStatus
						+ "&operation=" + operation);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}

		} else {
			// 所有产品
			object = procardServer.findAllProcard(procard, Integer
					.parseInt(cpage), pageSize, startDate, endDate, pageStatus);
			if (object != null && object.length > 0) {
				procardList = (List<Procard>) object[0];
			}
			if (procardList != null && procardList.size() > 0) {
				int count = (Integer) object[1];
				nums = (Float) object[2];
				pageSize = (Integer) object[3] == 0 ? 15 : (Integer) object[3];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!findAllProcards.action?viewStatus="
						+ viewStatus + "&operation=" + operation
						+ "&pageStatus=" + pageStatus + "&tag=" + tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_findProcardList";
	}

	/***
	 * 查找已绑定的人员信息
	 */
	public String findPeople() {
		if ("jhBd".equals(pageStatus)) {
			errorMessage = procardServer.toChagePeoBefor(id);
			if (!errorMessage.equals("true")) {
				return ERROR;
			}
		}
		if ("listUser".equals(tag)) {
			list = procardServer.findManByProcardID(id, user);
			listAll = procardServer.chagePeople(id, user, pageStatus);
		} else if ("listwbUser".equals(tag)) {
			list = procardServer.findManByProcardID(id);
			listAll = procardServer.chagePeople(id, user, tag);
		} else {
			list = procardServer.findManByProcardID(id);
			listAll = procardServer.chagePeople(id);
		}
		// for (int i = 0; i < list.size(); i++) {
		// ProcessinforPeople pp = (ProcessinforPeople) list.get(i);
		// String code = pp.getCode();
		// for (int j = 0; j < listAll.size(); j++) {
		// Users u = (Users) listAll.get(j);
		// if (code.equals(u.getCode())) {
		// listAll.remove(j);
		// }
		// }
		// }
		if (list != null) {
			return "procardpeopleman";
		} else {
			return null;
		}
	}

	/***************************************************************************
	 * 根据首层父类id查询流水卡片(页面生成树形结构)
	 * 
	 * @param procard
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcardByRootId() {

		List<Procard> proList = procardServer.findProcardByRootId(id,
				pageStatus);
		try {
			MKUtil.writeJSON(proList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***************************************************************************
	 * 根据首层父类id查询流水卡片
	 * 
	 * @param procard
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcardForCard() {
		procardList = procardServer.findProcardByRootId(id, pageStatus);
		if ("gxjd".equals(pageStatus)) {
			return "Procard_gxjdck";
		}
		return "Procard_sendCard";
	}

	/**
	 * 发卡（激活程序）
	 * 
	 * @return
	 */
	public String bangCard() {
		// 查询卡号对应的最小批次的卡片
		Procard oldProcard = procardServer.findMinProcard(cardNumber);
		String message = "";
		if (oldProcard != null) {
			try {
				message = runningWCServer.sendRunCard(cardNumber, oldProcard);
				MKUtil.writeJSON(true, message, null);
			} catch (Exception e) {
				e.printStackTrace();
				MKUtil.writeJSON(false, "发卡失败!原因:" + e.getMessage(), null);
			}
		} else {
			MKUtil.writeJSON(false, "无法发卡!谢谢!", null);
		}
		return null;
	}

	/**
	 * 导出单个总成BOM excel
	 * 
	 * @return
	 */
	public void geExcel_1() {
		if (id != null && id > 0) {
			procardServer.findDaoChuBomByRootId(id, "");
		} else {
			errorMessage = "信息有误，请检查！";
		}
	}

	public void geExcel_2() {
		procardServer.exportforpage(procard, startDate, endDate);
	}

	/**
	 * 导出单个总成 生产零件 Excel
	 */
	public void exportProcard() {
		if (id != null && id > 0) {
			procardServer.exportProcard(id);
		} else {
			errorMessage = "信息有误，请检查！";
		}
	}

	/**
	 * 导出excel
	 * 
	 * @return
	 */
	public String geExcel() {
		if (procard != null) {
			if (procard.getProcardStyle() == null
					|| "".equals(procard.getProcardStyle())) {
				errorMessage = "请选择卡片类型";
				return "error";
			}
			ActionContext.getContext().getSession().put("procard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"procard");
		}
		// if (startDate != null && endDate != null) {
		// Date starttime = DateUtil.parseDate(startDate,
		// "yyyy-MM-dd HH:mm:ss");
		// Date endtime = DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss");
		// if ((endtime.getTime() - starttime.getTime()) < 0) {
		// errorMessage = "结束时间需要在起始时间之后";
		// return findAllProcard();
		// }
		String result = procardServer.getExcelpath(procard, startDate, endDate,
				id);
		// TODO 这还没改2007
		if (result != null && result.indexOf("xls") > 0) {
			String path = ServletActionContext.getServletContext().getRealPath(
					"/upload/procardExcel");
			HttpServletResponse response = ServletActionContext.getResponse();
			response.addHeader("Content-Disposition", "filename=\"" + result
					+ "\"");
			try {

				File file = new File(path);
				if (file.exists()) {
					java.io.OutputStream os = response.getOutputStream();
					java.io.FileInputStream fis = new FileInputStream(path
							+ "/" + result);
					byte[] b = new byte[1024];
					int i = 0;
					while ((i = fis.read(b)) > 0) {
						os.write(b, 0, i);
					}
					fis.close();
					os.flush();
					os.close();
				}
			} catch (Exception e) {
			} finally {
			}
			return null;
		}

		// } else {
		// errorMessage = "请输入起始和结束时间";
		// }
		return findAllProcards();
	}
	/**
	 * 导出在制品详情
	 * @return
	 */
	public String getzzpExcel() {
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get("Daochuzzp");
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"Daochuzzp",
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在导出在制品请稍后再导!");
		} else {
			return ERROR;
		}
		try {
		String result = procardServer.getzzpExcel();
		// TODO 这还没改2007
		if (result != null && result.indexOf("xls") > 0) {
			String path = ServletActionContext.getServletContext().getRealPath(
					"/upload/procardExcel");
			HttpServletResponse response = ServletActionContext.getResponse();
			response.addHeader("Content-Disposition", "filename=\"" + result
					+ "\"");

				File file = new File(path);
				if (file.exists()) {
					java.io.OutputStream os = response.getOutputStream();
					java.io.FileInputStream fis = new FileInputStream(path
							+ "/" + result);
					byte[] b = new byte[1024];
					int i = 0;
					while ((i = fis.read(b)) > 0) {
						os.write(b, 0, i);
					}
					fis.close();
					os.flush();
					os.close();
				}
			ActionContext.getContext().getApplication().put(
					"Daochuzzp",null);
			return null;
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ActionContext.getContext().getApplication().put(
					"Daochuzzp",null);
		}
		// } else {
		// errorMessage = "请输入起始和结束时间";
		// }
		return findAllProcards();
	}
	
	/**
	 * 导出生产进度
	 * @return
	 */
	public String getscjdExcel() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("procarddc", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"procarddc");
		}
		 if (startDate != null && endDate != null) {
		 Date starttime = DateUtil.parseDate(startDate,"yyyy-MM-dd HH:mm:ss");
		 Date endtime = DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss");
		 if ((endtime.getTime() - starttime.getTime()) < 0) {
			 errorMessage = "结束时间需要在起始时间之后";
			 return findAllProcard();
		 }
		 }
//		String result = procardServer.getExcelpath(procard, startDate, endDate,
//				id);
		String result = procardServer.getscjdExcel(procard, startDate, endDate,
				id);
		// TODO 这还没改2007
		if (result != null && result.indexOf("xls") > 0) {
			String path = ServletActionContext.getServletContext().getRealPath(
					"/upload/procardExcel");
			HttpServletResponse response = ServletActionContext.getResponse();
			response.addHeader("Content-Disposition", "filename=\"" + result
					+ "\"");
			try {

				File file = new File(path);
				if (file.exists()) {
					java.io.OutputStream os = response.getOutputStream();
					java.io.FileInputStream fis = new FileInputStream(path
							+ "/" + result);
					byte[] b = new byte[1024];
					int i = 0;
					while ((i = fis.read(b)) > 0) {
						os.write(b, 0, i);
					}
					fis.close();
					os.flush();
					os.close();
				}
			} catch (Exception e) {
			} finally {
			}
			return null;
		}

		return toDaochualltypedata();
	}
	/**
	 * 前往导出页面
	 * @return
	 */
	public String toDaochualltypedata(){
		return "Procard_AllTypeData";
	}
	
	/***
	 * 删除生产周转单明细
	 * 
	 * @return
	 */
	public String deleteprocardtree() {
		boolean b = procardServer.deleteprocardtree(id);
		if (b) {
			successMessage = "删除成功!";
		} else {
			successMessage = "删除失败!";
		}
		if (procard != null && procard.getRootId() != null
				&& procard.getRootId() > 0) {
			errorMessage = successMessage;
			url = "ProcardAction!findProcardView.action?pageStatus=history&viewStatus=update&id="
					+ procard.getRootId();
			return ERROR;
		}
		return findAllProcards();
	}

	/**
	 * 发卡
	 * 
	 * @return
	 */
	public String saveRunWC() {
		// 查询卡号对应的最小批次的卡片
		Procard oldProcard = procardServer.findMinProcard(cardNumber);
		String message = "";
		if (oldProcard != null) {
			try {
				message = runningWCServer.saveRunWC(cardNumber, oldProcard);
				MKUtil.writeJSON(true, message, null);
			} catch (Exception e) {
				MKUtil.writeJSON(false, e.getMessage(), null);
			}
		} else {
			MKUtil.writeJSON(false, "无法发卡!谢谢!", null);
		}
		return null;
	}

	/***************************************************************************
	 * 显示流水卡片详细
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findCardForShow() {
		Object[] obj = procardServer.findCardForShow(id);
		if (obj != null) {
			procard = (Procard) obj[0];
			procardList = (List<Procard>) obj[1];
			list = (List) obj[2];
			return "ProcardDetails";
		}
		return ERROR;
	}

	/** ============领工序===================== */
	/***************************************************************************
	 * 通过流水卡id查询对应工艺卡片（老的）
	 * 
	 * @param cardId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcardByRunCard() {
		String status = "";
		String receiveStatus = "";
		Integer procarId = null;
		if (pageStatus != null && "history".equals(pageStatus)) {
			procarId = id;
			status = "完成";
			receiveStatus = "yes";
		} else {
			RunningWaterCard rwc = runningWCServer.findRunWC(cardNumber);
			if (rwc == null) {
				LogoStickers ls = procardServer.findLogoSBynumber(cardNumber);
				if (ls != null) {
					status = ls.getStatus();
					receiveStatus = "yes";
					procarId = ls.getProcardId();
				} else {
					errorMessage = "系统中不存在您的要查询的信息!";
					return ERROR;
				}
			} else {
				status = rwc.getCardStatus();
				procarId = rwc.getProcardId();
				receiveStatus = rwc.getReceiveStatus();
			}
		}

		// if ("已发卡".equals(status) || "完成".equals(rwc.getCardStatus())
		// || "入库".equals(rwc.getCardStatus())) {
		if ("yes".equals(receiveStatus) && "已发料".equals(status)
				|| "完成".equals(status) || "领工序".equals(status)) {
			Object[] obj = procardServer.findProcardByRunCard(procarId,
					pageStatus);
			if (obj != null) {
				if (obj.length > 5) {
					errorMessage = (String) obj[3];
				} else {
					procard = (Procard) obj[0];
					procardList = (List<Procard>) obj[1];
					list = (List) obj[2];
					if (pageStatus != null && "out".equals(pageStatus)) {
						return "Process_Outside";
					} else if ("receive2".equals(pageStatus)) {
						return "Process_Receive2";
					}
					return "Process_Receive2";
				}
			} else {
				errorMessage = "该流水卡片错误!无法找到对应工艺信息!请更换!";
			}
		} else {
			if ("初始".equals(status)) {
				errorMessage = "该生产周转卡尚未绑定工艺流水单!无法使用!";
			} else if ("已发卡".equals(status)) {
				errorMessage = "该生产周转卡尚未领料!请等待领料后再刷卡!";
			}
		}
		return ERROR;
	}

	/***
	 * 进度查看、领取工序
	 * 
	 * @return
	 */
	public String findProcardByRunCard2() {
		String status = "";
		String receiveStatus = "";
		Integer procarId = id;
		Users user = Util.getLoginUser();
		if (user == null) {
			return "login";
		}
		if (pageStatus != null
				&& ("history".equals(pageStatus) || "viewUpdate"
						.equals(pageStatus))) {
			status = "完成";
			receiveStatus = "yes";
		} else if (pageStatus != null && "cardNumber".equals(pageStatus)
				|| "out".equals(pageStatus)
				|| "noCardLingGx".equals(pageStatus)) {
			Procard procard = procardServer.findProcardById(id);
			if (procard != null) {
				if (!"激活".equals(procard.getJihuoStatua())) {
					errorMessage = procard.getMarkId() + "尚未激活,无法领取!";
					return ERROR;
				}

				status = procard.getStatus();
				if ("初始".equals(status) || "已发卡".equals(status)) {
					receiveStatus = "no";
				} else {
					receiveStatus = "yes";
				}
			}
		}
		if (("yes".equals(receiveStatus) && "已发料".equals(status))
				|| "完成".equals(status) || "领工序".equals(status)
				|| "入库".equals(status) || "待入库".equals(status)) {
			Object[] obj = procardServer.findProcardByRunCard(procarId,
					pageStatus);
			if (obj != null) {
				// if (obj.length > 4) {
				// errorMessage = (String) obj[4];
				// } else {
				procard = (Procard) obj[0];
				procardList = (List<Procard>) obj[1];
				list = (List) obj[2];
				wwPlanList = (List) obj[3];
				pgList = (List) obj[4];
				isbcpqx = procardServer.isbcpqx(user, procard);
				if (pageStatus != null && "out".equals(pageStatus)) {
					return "Process_Outside";
				}
				return "Process_Receive2";
				// }
			} else {
				errorMessage = "该流水卡片错误!无法找到对应工艺信息!请更换!";
			}
		} else {
			if ("初始".equals(status)) {
				errorMessage = "该生产周转卡尚未绑定工艺流水单!无法使用!";
			} else if ("已发卡".equals(status)) {
				errorMessage = "该生产周转卡尚未领料!请等待领料后再刷卡!";
			} else {
				errorMessage = "该生产周转卡已关闭!";
			}
		}
		return ERROR;
	}

	public void android_findProcardByRunCard2() {
		String status = "";
		String receiveStatus = "";
		Integer procarId = id;
		if (pageStatus != null && "history".equals(pageStatus)) {
			status = "完成";
			receiveStatus = "yes";
		} else if (pageStatus != null && "cardNumber".equals(pageStatus)
				|| "out".equals(pageStatus)
				|| "noCardLingGx".equals(pageStatus)) {
			Procard procard = procardServer.findProcardById(id);
			if (procard != null) {
				status = procard.getStatus();
				if ("初始".equals(status) || "已发卡".equals(status)) {
					receiveStatus = "no";
				} else {
					receiveStatus = "yes";
				}
			}
		}
		if ("yes".equals(receiveStatus) && "已发料".equals(status)
				|| "完成".equals(status) || "领工序".equals(status)) {
			Object[] obj = procardServer.findProcardByRunCard(procarId,
					pageStatus);
			if (obj != null) {
				if (obj.length > 3) {
					errorMessage = (String) obj[3];
				} else {
					procard = (Procard) obj[0];
					procardList = (List<Procard>) obj[1];
					list = (List) obj[2];
					if (pageStatus != null && "out".equals(pageStatus)) {
						return;
					}
					MKUtil.writeJSON(true, "领工序", procard, list);
				}
			} else {
				errorMessage = "该流水卡片错误!无法找到对应工艺信息!请更换!";
			}
		} else {
			if ("初始".equals(status)) {
				errorMessage = "该生产周转卡尚未绑定工艺流水单!无法使用!";
			} else if ("已发卡".equals(status)) {
				errorMessage = "该生产周转卡尚未领料!请等待领料后再刷卡!";
			}
		}
	}

	/***************************************************************************
	 * 查询工序明细
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcess() {
		Object[] obj = procardServer.findProcess(id);
		List list = (List) obj[0];
		String message = (String) obj[1];
		Boolean bool = (Boolean) obj[2];
		MKUtil.writeJSON(bool, message, list);
//		MKUtil.writeJSON(message);
		return null;
	}

	public String findProcessByProcard() {
		list = procardServer.findProcessByProcard(id);
		MKUtil.writeJSON(list);
		return null;
	}

	/***************************************************************************
	 * 查询工序明细接口
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void Android_findProcess() {
		Object[] obj = procardServer.findProcess(id);
		List list = (List) obj[0];
		String message = (String) obj[1];
		Boolean bool = (Boolean) obj[2];
		MKUtil.writeJSON(bool, message, list);
	}

	/**************************************************************************
	 * 现场自检表
	 * 
	 * @return
	 */
	public String showZj() {
		list = procardServer.listProvisionByMarkId(procardTemplate.getMarkId());
		// list = procardServer.findZjXiang();
		machineList = procardServer.getPrcocessMachine(id);
		process = procardServer.getObjectByIdProcessInfor(id);
		if (list != null)
			pageSize = list.size();
		return "Process_zj";
	}

	/**************************************************************************
	 * 现场自检表接口
	 * 
	 * @return
	 */
	public void Android_showZj() {
		list = procardServer.listProvisionByMarkId(procardTemplate.getMarkId());
		if (list != null) {
			pageSize = list.size();
			MKUtil.writeJSON(true, "", list, pageSize);
		} else {
			MKUtil.writeJSON(false, "", null);
		}
	}

	/****
	 * 添加自检
	 * 
	 * @return
	 */
	public String addZj() {
		// for (int i = 1; i < contentList.size(); i++) {
		// ProcessZj processZj = (ProcessZj) contentList.get(i);
		// System.out.println("第"+i+"个数组(内容)"+processZj.getZjItem());
		//			
		// }
		// for (int i = 0; i < isQualifiedList.size(); i++) {
		// ProcessZj processZj = (ProcessZj) contentList.get(i);
		// System.out.println("第"+i+"个数组(结果)"+processZj.getIsQualified());
		//			
		// }
		if (id != null) {
			String msg = procardServer.saveZj(contentList, isQualifiedList,
					process.getId(), id);
			if (msg.equals("true")) {
				successMessage = "自检完成";
				return "Process_zj";
			} else {
				successMessage = msg;
				return "Process_zj";
			}
		} else {
			successMessage = "请先选择一个工位设备,谢谢!";
			return "Process_zj";
		}
	}

	/****
	 * 添加自检
	 * 
	 * @return
	 */
	public void Android_addZj() {
		contentList = JSON.parseArray(successMessage, String.class);
		isQualifiedList = JSON.parseArray(errorMessage, String.class);
		if (contentList != null || isQualifiedList != null) {
			String msg = procardServer.saveZj(contentList, isQualifiedList, id,
					id2);
			if (msg.equals("true")) {
				successMessage = "自检完成";
				MKUtil.writeJSON(true, successMessage, null);
			} else {
				successMessage = "自检失败";
				MKUtil.writeJSON(false, successMessage, null);
			}
		} else {
			MKUtil.writeJSON(false, "数据传输异常，请重新访问!", null);
		}

	}

	/***************************************************************************
	 * 领取工序
	 * 
	 * @return
	 */
	public String collorProcess() {
		// MKUtil.writeJSON("刀具已使用200次，请换更换刀具后再领取 !");
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			MKUtil.writeJSON("请登录后再提交数据!");
			return null;
		}
		String message;
		try {
			if (processIds != null && processIds.length > 0) {
				for (Integer i : processIds) {
					errorMessage = (String) ActionContext.getContext()
							.getApplication().get("lqProcessInfor" + i);
					if (errorMessage != null) {
						MKUtil.writeJSON(errorMessage);
						return null;
					}
				}
				for (Integer i : processIds) {
					ActionContext.getContext().getApplication().put(
							"lqProcessInfor" + i,
							loginUser.getDept() + "的" + loginUser.getName()
									+ "正在领取此工序，请稍后再领取!");
				}
			}
			message = procardServer.collorProcess(processIds, processNumbers,
					list);
			// if ("领取成功".equals(message)) {
			// // 推送led信息
			// procardServer.processToLedSend(processIds);
			// }
			for (Integer i : processIds) {
				ActionContext.getContext().getApplication().put(
						"lqProcessInfor" + i, null);
			}
			MKUtil.writeJSON(message);
		} catch (Exception e) {
			e.printStackTrace();
			for (Integer i : processIds) {
				ActionContext.getContext().getApplication().put(
						"lqProcessInfor" + i, null);
			}
			MKUtil.writeJSON("领取工序失败!");
		}
		return null;
	}

	/***
	 * led信息推送
	 */
	public void sendLedMs() {
		if ("submit".equals(pageStatus)) {
			procardServer.processToBanChenPin(processIds);// 半成品自动入库申请
			procardServer.processToupdate(processIds);// 工序节拍
			procardServer.processToShebei(processIds);// 设备点检
			// procardServer.processToPebProduction(processIds);//总成最后一道工序提交和车间报得不一致所以不使用了
		}
		procardServer.processToLedSend(processIds);// led推送
	}

	/***************************************************************************
	 * 领取工序接口
	 * 
	 * @return
	 */
	public void Android_collorProcess() {
		list = JSON.parseArray(successMessage, String[].class);
		processIds = JSON.parseObject(errorMessage, Integer[].class);
		processNumbers = JSON.parseObject(viewStatus, Float[].class);
		try {
			String message = procardServer.collorProcess(processIds,
					processNumbers, list);
			// 推送led信息
			// procardServer.processToLedSend(processIds);
			MKUtil.writeJSON(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * 提交工序
	 * 
	 * @return
	 */
	public String submitProcess() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			errorMessage = "请先登录后再提交工序,谢谢!";
			MKUtil.writeJSON(false, errorMessage, null);
			return null;
		}

		String message = "";
		Map<String, Object> maps = null;
		try {
			// --------------------------------------工装------------------------------------
			if (process != null) {
				ProcessInfor oldP = procardServer
						.getObjectByIdProcessInfor(process.getId());
				if ("是".equals(oldP.getGongzhuangstatus())) {
					if (oldP.getGzstoreId() != null) {
						Gzstore old = procardServer.getObjectByIdGzstore(oldP
								.getGzstoreId());

						if (old.getSybjcs() == null || old.getSybjcs() == 0) {
							message = "报检数量不足,请去报检!";
							MKUtil.writeJSON(false, message, maps);
							return null;
						}
					}
				}
				// message="提交工序成功";
				message = procardServer.updateProcess(process, oldP, barcode,
						processSaveLog, markIds, breakscount, breaksubmit);
				if ("提交工序成功".equals(message)) {
					// 提交最后一道工序后激活待干产品
					procardServer.updateJihuo(process.getId(), id, "提交工序:"
							+ process.getId());
					// // 推送led信息
					// procardServer.processToLedSend(new Integer[] { process
					// .getId() });
				}
				procard = procardServer.findProcardById(id);
				Procard rootprocard = procardServer.findProcardById(procard
						.getRootId());
				procard.setYwMarkId(rootprocard.getYwMarkId());
				maps = new HashMap<String, Object>();
				maps.put("process", process);
				maps.put("procard", procard);
				MKUtil.writeJSON(true, message, maps);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, message + e.getMessage(), maps);
			AlertMessagesServerImpl
					.addAlertMessages("系统维护异常组", "工序提交操作失败  --操作方式:提交工序;工序号:"
							+ process.getProcessNO() + ",件号:"
							+ procard.getMarkId() + ",批次:"
							+ procard.getSelfCard() + ",异常:" + e, "提交工序失败", "2");

		}
		return null;
	}

	public void mibushoujian() {
		int n = procardServer.mibushoujian();
		MKUtil.writeJSON(n);
	}

	/***
	 * Pmi自动提交程序
	 * 
	 * @return
	 */
	public String pmiAutosubPro() {
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			errorMessage = "请重新登录后再提交工序!";
			return ERROR;
		}
		String message = null;
		try {
			// --------------------------------------工装------------------------------------
			ProcessInfor oldP = procardServer.getObjectByIdProcessInfor(process
					.getId());
			if (oldP != null) {
				message = procardServer.updateProcessForPMI(oldP);
				if ("提交工序成功".equals(message)) {
					// 提交最后一道工序后激活待干产品
					procardServer.updateJihuo(oldP.getId(), id, "pmi提交工序:"
							+ oldP.getId());
				}
				procard = procardServer.findProcardById(id);
				Map<String, Object> maps = null;
				maps = new HashMap<String, Object>();
				maps.put("process", process);
				maps.put("procard", procard);
				MKUtil.writeJSON(true, message, maps);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, message + e.getMessage(), null);

		}
		return null;
	}

	/***************************************************************************
	 * 提交工序接口
	 * 
	 * @return
	 */
	public String Android_submitProcess() {
		String message = null;
		Map<String, Object> maps = null;
		try {
			// --------------------------------------工装------------------------------------
			if (process != null) {
				ProcessInfor oldP = procardServer
						.getObjectByIdProcessInfor(process.getId());
				if ("是".equals(oldP.getGongzhuangstatus())) {
					if (oldP.getGzstoreId() != null) {
						Gzstore old = procardServer.getObjectByIdGzstore(oldP
								.getGzstoreId());
						if (old.getSybjcs() == null || old.getSybjcs() == 0) {
							message = "报检数量不足,请去报检!";
							MKUtil.writeJSON(false, message, maps);
							return null;
						}
					}
				}

				message = procardServer.updateProcess(process, oldP, barcode,
						processSaveLog, markIds, breakscount, breaksubmit);
				if ("提交工序成功".equals(message)
						&& (oldP.getNeedSave() == null || !oldP.getNeedSave()
								.equals("是"))) {
					// 提交最后一道工序后激活待干工序
					procardServer.updateJihuo(process.getId(), id, "安卓端提交工序:"
							+ oldP.getId());
				}

				procard = procardServer.findProcardById(id);
				maps = new HashMap<String, Object>();
				maps.put("process", process);
				maps.put("procard", procard);
				MKUtil.writeJSON(true, message, maps);

			}
		} catch (Exception e) {
			MKUtil.writeJSON(false, message + e.getMessage(), maps);

		}
		return null;
	}

	/***
	 * 工序补打
	 */
	public void printProcess() {
		String message = null;
		Map<String, Object> maps = null;
		try {
			maps = procardServer.findProcessForBd(process);
			if (maps != null) {
				MKUtil.writeJSON(true, message, maps);
			} else {
				MKUtil.writeJSON(false, "补打数量不能大于总提交量", maps);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, e.getMessage(), maps);

		}
	}

	/***************************************************************************
	 * 通过废品单Id查询流水单
	 * 
	 * @return
	 */
	public String findProcardForzj() {
		procardList = procardServer.findProcardForzj(id);
		return "Procard_logoStickers";

	}

	/***************************************************************************
	 * 添加新的流水单和报废卡
	 * 
	 * @return
	 */
	public String addNewzj() {
		try {
			procardServer.addNewzj(id, processIds, responsible);
			errorMessage = "处理完成!~";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "处理失败!~";
		}
		return "addNewzj";
	}

	/***************************************************************************
	 * 交卡管理
	 * 
	 * @param cardNum
	 *            卡号
	 * @return
	 */
	public String postCard() {
		// 强制交卡
		if (pageStatus != null && "yes".equals(pageStatus)) {
			procardServer.updateQzsk(cardNumber);
		}

		Object[] obj = procardServer.postCard(cardNumber);
		Boolean bool = (Boolean) obj[0];
		String message = (String) obj[1];
		MKUtil.writeJSON(bool, message, null);
		return null;
	}

	/***
	 * 更新工序模板工位信息
	 * 
	 * @return
	 */
	public String updateProcess() {
		procardServer.updateProcess();
		return ERROR;
	}

	/***
	 * 流水单以及流水卡直接跳到"已领料"状态
	 * 
	 * @param cardNum
	 * @return
	 */
	public String updateFkToLl() {
		boolean bool = procardServer.updateFkToLl(cardNumber);
		if (bool) {
			errorMessage = "处理成功";
		} else {
			errorMessage = "处理失败";
		}
		return ERROR;
	}

	/***
	 * 流水单以及流水卡直接跳到"已发卡"状态
	 * 
	 * @param cardNum
	 * @return
	 */
	public String updateLlToFk() {
		boolean bool = procardServer.updateLlToFk(cardNumber);
		if (bool) {
			errorMessage = "处理成功";
		} else {
			errorMessage = "处理失败";
		}
		return "procard_lgx";
	}

	/***
	 * 通过卡号查询流水单信息
	 * 
	 * @param cardNumber
	 *            卡号
	 * @return
	 */
	public String findProcardByCardNum() {
		// 如果是无卡(员工卡)领料
		if (pageStatus != null && pageStatus.equals("noCardLingliao")
				|| "noCardLingGx".equals(pageStatus)
				|| "gongweicardLing".equals(pageStatus)
				|| "gongweitijiao".equals(pageStatus)
				|| "noCardLingGx".equals(pageStatus)
				|| "noCardHadYlGx".equals(pageStatus)
				|| "loginLingGx".equals(pageStatus)) {
			// 根据卡号查询人员，得到人员对应工序的对应最小批次、已发卡的件号
			procardList = procardServer.findProcardListByUserCard(cardNumber,
					pageStatus, tag);
			if ((procardList == null || procardList.size() == 0)
					&& "phone".equals(tag) && "code".equals(tag)) {
				errorMessage = "智能仓储仓库，没有您需要领的料。";
			}
			return "Procard_noCardList";
		} else {
			// 领工序
			procard = procardServer.findProcardByCardNum(cardNumber);
			if (procard != null && "总成".equals(procard.getProcardStyle())) {
				return "findProcardView";
			} else {
				errorMessage = "您的刷卡信息错误!";
			}
		}
		return ERROR;
	}

	/**
	 * 手机上个人领料；
	 */
	public String PersonLingLiao() {
		if (Util.getLoginUser() == null) {
			errorMessage = "请先登录！";
			return ERROR;
		}
		cardNumber = Util.getLoginUser().getCardId();
		pageStatus = "noCardLingliao";
		tag = "phone";
		return "findProcardByCardNum";
	}

	/***
	 * 通过卡号查询流水单信息
	 * 
	 * @param cardNumber
	 *            卡号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findProcardByCardNumFl() {
		procardList = procardServer.findFlProcardListByUserCard(cardNumber);
		return "Procard_noCardFlList";
	}

	/**
	 * 获取总成下的所有的辅料信息
	 * 
	 * @return
	 */
	public String getProcessInForFuLiao() {
		processinforFuLiaoList = procardServer.getProcessInForFuLiao(id,
				cardNumber);
		procard = procardServer.findProcardById(id);
		return "Procard_lingFuLiao";
	}

	/**
	 * 领取辅料
	 * 
	 * @return
	 */
	public String submitLingFL() {
		try {
			String msg = procardServer.submitLingFL(procard,
					processinforFuLiaoList, cardNumber);
			if (msg.equals("true")) {
				errorMessage = "领取成功";
				url = "ProcardAction!getProcessInForFuLiao.action?id="
						+ procard.getId();
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
			e.printStackTrace();
		}
		return "error";
	}

	/***
	 * 通过卡号查询流水单信息接口
	 * 
	 * @param cardNumber
	 *            卡号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void Android_findProcardByCardNum() {
		// 如果是无卡(员工卡)领料
		if (pageStatus != null && pageStatus.equals("noCardLingliao")
				|| pageStatus.equals("noCardLingGx")) {
			// 根据卡号查询人员，得到人员对应工序的对应最小批次、已发卡的件号
			procardList = procardServer.findProcardListByUserCard(cardNumber,
					pageStatus, tag);
			MKUtil
					.writeJSON(true, "",
							new Object[] { procardList, pageStatus });
		} else {
			// 生产周转卡领料
			procard = procardServer.findProcardByCardNum(cardNumber);
			if (procard != null && "总成".equals(procard.getProcardStyle())) {
				MKUtil.writeJSON(true, "", procard);
			} else {
				MKUtil.writeJSON(false, "您的刷卡信息错误", null);
			}
		}
	}

	/***
	 * 生产进度查看
	 * 
	 * @return
	 */
	public String findProcardView() {
		procard = procardServer.findProcardById(id);
		if (procard != null) {
			if (procard.getProcardStyle().equals("总成")) {
				maxBelongLayer = procardServer.findMaxbelongLayer(procard
						.getRootId());
				// 领料
				if (pageStatus != null && "lingliao".equals(pageStatus)
						|| "noCardLingliao".equals(pageStatus)) {
					return "Procard_viewGoods";
				} else if (pageStatus != null && "cb".equals(pageStatus)) {
					return "Procard_viewCbBom";
					// }else if ("viewUpdate".equals(pageStatus)){//更新零件
					// return "Procard_viewUpdate";
				}
				return "Procard_viewProcard";
			} else {
				errorMessage = "请直接发总成流水卡!!";
			}
		} else {
			errorMessage = "不存在您要查看的流水单信息!请检查或重试!";
		}
		return ERROR;
	}

	/***
	 * 生产进度查看(库位)
	 * 
	 * @return
	 */
	public String findProcardViewkuwei() {
		procard = procardServer.findProcardById(id);
		if (procard != null) {
			if (procard.getProcardStyle().equals("总成")) {
				maxBelongLayer = procardServer.findMaxbelongLayer(procard
						.getRootId());
				// 领料
				if (pageStatus != null && "lingliao".equals(pageStatus)
						|| "noCardLingliao".equals(pageStatus)) {
					return "Procard_viewGoods";
				}
				return "Procard_viewProcardkuwei";
			} else {
				errorMessage = "请直接发总成流水卡!!";
			}
		} else {
			errorMessage = "不存在您要查看的流水单信息!请检查或重试!";
		}
		return ERROR;
	}

	/***
	 * 生产进度查看接口
	 * 
	 * @return
	 */
	public void Android_findProcardView() {
		List<Procard> procardList = procardServer.findProcardById1(id);
		if (procardList.size() > 0) {
			MKUtil.writeJSON(true, "", procardList);
		} else {
			MKUtil.writeJSON(false, "", null);
		}
	}

	/***
	 * 查询节点(输出json)
	 */
	@SuppressWarnings("unchecked")
	public void findProByBel() {
		List<Procard> list = procardServer.findProByBel(id, maxBelongLayer,
				pageStatus);
		MKUtil.writeJSON(list);
	}

	/***
	 * 根据首层父类id查询不包含外购件流水卡片
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String waiwei() {
		RunningWaterCard rwc = runningWCServer.findRunWC(cardNumber);
		if (rwc != null) {
			procardList = procardServer.findNoWaiProcardByRootId(rwc
					.getProcardId());
			return "Process_OutsideList";
		}

		return ERROR;
	}

	/***
	 * 根据内部计划id查询对应的生产周转单
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void findProcardByPlanOrderId() {
		List<Procard> list = procardServer.findProcardByPlanOrderId(id, markid);
		MKUtil.writeJSON(list);
	}

	/***
	 * 查询工艺规范
	 * 
	 * @return
	 */
	public String findGongyiGuifan() {
		list = procardServer.findGongyiGuifan(markid, processNO, id, banci);
		if (list != null && list.size() > 0) {
			return "findGongyiGuifan";
		} else {
			errorMessage = "不存在该工序的工艺规范信息!";
		}
		return ERROR;
	}

	/***
	 * 查询工艺规范
	 * 
	 * @return
	 */
	public String showProcessinforTz() {
		process = procardServer.findProcessById(id);
		if (process != null) {
			procard = procardServer.findProcardById(process.getProcard()
					.getId());
		}
		if (procard != null) {
			list = procardServer.showProcessinforTz(id, null);
			if (list != null && list.size() > 0) {
				return "findGongyiGuifan";// Process_showGygf.jsp
			} else {
				errorMessage = "不存在该工序的工艺规范信息!";
			}
		} else {
			errorMessage = "未查到对应零件信息，无法查找图纸!";
		}
		return ERROR;
	}

	/***
	 * 查询工艺规范
	 * 
	 * @return
	 */
	public String showProcesstzforsc() {
		tag = "gx";
		list = procardServer.showProcessinforTz(id, "sc");
		if (list != null && list.size() > 0) {
			return "findGongyiGuifan2";// Process_showGygf2.jsp
		} else {
			errorMessage = "不存在该工序的工艺规范信息!";
		}
		return ERROR;
	}

	/***
	 * 查询工艺规范
	 * 
	 * @return
	 */
	public String showProcardtzforsc() {
		tag = "lj";
		procard = procardServer.findProcardById(id);
		if (procard != null) {
			list = procardServer.showProcardtzforsc(id, "sc");
		}
		if (list != null && list.size() > 0) {
			return "findGongyiGuifan2";// Process_showGygf2.jsp
		} else {
			errorMessage = "不存在该工序的工艺规范信息!";
		}
		return ERROR;
	}

	/***
	 * BOM激活------查可领工序人员列表
	 * 
	 * @return
	 */
	public String chagePeople() {
		// 暂停批次验证
		errorMessage = procardServer.toChagePeoBefor(id);
		if (errorMessage.equals("true")) {
			list = procardServer.chagePeople(id);
			return "Procard_chagePeople";
		}
		return ERROR;
		// list = procardServer.chagePeople(id);
		// return "Procard_chagePeople";
	}

	/***
	 * 绑定领取成员、激活本批次 、计算生产周期、生成外购、外委的采购计划
	 * 
	 * @param id
	 *            总成id
	 * @param userIds
	 *            用户id
	 * @return
	 */
	public String bangAndJihuo() {
		try {
			// 绑定
			procardServer.bangAndJihuo(id, selected);
			// 激活
			procard = procardServer.findProcardById(id);
			procardServer.sendRunCard(procard, "绑定激活生产批次");
			procard = new Procard();
			procard.setId(id);
			pageStatus = "history";
			return "findProcardView";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "非常抱歉,激活失败!可联系管理员。错误原因:" + e;
			return ERROR;
		}

	}

	/***
	 * 
	 * @return
	 */
	public String bangAndad() {
		procardServer.bangAnd(id, selected);
		list = procardServer.findManByProcardID(id);
		listAll = procardServer.chagePeople(id);

		for (int i = 0; i < list.size(); i++) {
			ProcessinforPeople pp = (ProcessinforPeople) list.get(i);
			String code = pp.getCode();
			for (int j = 0; j < listAll.size(); j++) {
				Users u = (Users) listAll.get(j);
				if (code.equals(u.getCode())) {
					listAll.remove(j);
				}
			}
		}
		if (list != null) {
			return "procardpeopleman";
		} else {
			return null;
		}
	}

	/***
	 * 查找待激活外购外委采购计划
	 * 
	 * @return
	 */
	public String findWgWwPlan() {
		list = procardServer.findWgWwPlan(id);
		return "Procard_WgwwPlan";
	}

	/***
	 * 查找外委未激活采购计划
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findNeedjhWgWwPlanList() {
		if (wwPlan != null) {
			ActionContext.getContext().getSession().put("wwPlan", wwPlan);
		} else {
			wwPlan = (WaigouWaiweiPlan) ActionContext.getContext().getSession()
					.get("wwPlan");
		}
		if (noZhuser != null) {
			ActionContext.getContext().getSession().put("noZhuser", noZhuser);
		} else {
			noZhuser = (String) ActionContext.getContext().getSession().get(
					"noZhuser");
		}
		jhStatus = "needjh";
		// 查询之前先弥补之前的外委工序
		// procardServer.unCreateWaiWei();
		Object[] object = procardServer.findWgWwPlanList(wwPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, ms, jhStatus, noZhuser);
		// list = procardServer.findAllzZhUsers();
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouWaiweiPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				pageSize = (Integer) object[2];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				if (ms == null)
					this
							.setUrl("ProcardAction!findNeedjhWgWwPlanList.action?pageStatus="
									+ pageStatus);
				else
					this
							.setUrl("ProcardAction!findNeedjhWgWwPlanList.action?pageStatus="
									+ pageStatus + "&ms.id=" + ms.getId());
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		// 所有待激活、确认的采购计划
		// if (ms == null)
		// list = procardServer.findWgWwPlanList(pageStatus);
		return "Procard_WgwwPlanList";
	}

	/***
	 * 查找外委已激活采购计划
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findHadjhWgWwPlanList() {
		if (wwPlan != null) {
			ActionContext.getContext().getSession().put("wwPlan", wwPlan);
		} else {
			wwPlan = (WaigouWaiweiPlan) ActionContext.getContext().getSession()
					.get("wwPlan");
		}
		jhStatus = "hadjh";
		// 查询之前先弥补之前的外委工序
		// procardServer.unCreateWaiWei();
		Object[] object = procardServer.findWgWwPlanList(wwPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, ms, jhStatus, noZhuser);
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouWaiweiPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				if (ms == null)
					this
							.setUrl("ProcardAction!findHadjhWgWwPlanList.action?pageStatus="
									+ pageStatus);
				else
					this
							.setUrl("ProcardAction!findHadjhWgWwPlanList.action?pageStatus="
									+ pageStatus + "&ms.id=" + ms.getId());
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		// 所有待激活、确认的采购计划
		// if (ms == null)
		// list = procardServer.findWgWwPlanList(pageStatus);
		return "Procard_WgwwPlanList";
	}

	/****
	 * 所有待激活、确认的采购计划
	 * 
	 * @return
	 */
	public String findDqrWgPlanList() {
		if (wwPlan != null) {
			ActionContext.getContext().getSession().put("wwPlan", wwPlan);
		} else {
			wwPlan = (WaigouWaiweiPlan) ActionContext.getContext().getSession()
					.get("wwPlan");
		}
		Object[] object = procardServer.findWgWwPlanList(wwPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, ms, null, null);
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouWaiweiPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				if (ms == null)
					this
							.setUrl("ProcardAction!findWgWwPlanList2.action?pageStatus="
									+ pageStatus);
				else
					this
							.setUrl("ProcardAction!findWgWwPlanList2.action?pageStatus="
									+ pageStatus + "&ms.id=" + ms.getId());
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		list = procardServer.findWgWwPlanList(pageStatus);
		return "Procard_WgwwPlanList";
	}

	public String findGysWgPlanList() {
		Users user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请先登录!";
			return "Procard_findGysWgPlanList";
		}
		if (wwPlan != null) {
			ActionContext.getContext().getSession().put("gyswwPlan", wwPlan);
		} else {
			wwPlan = (WaigouWaiweiPlan) ActionContext.getContext().getSession()
					.get("gyswwPlan");
		}
		Object[] object = procardServer.findGysWgPlanList(wwPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, user.getId());
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouWaiweiPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!findGysWgPlanList.action");
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_findGysWgPlanList";
	}

	/**
	 * 供应商查看图纸
	 * 
	 * @return
	 */
	public String gysTzview() {
		procardTemplate = procardServer.getProcardTemplateBywgplanId(id);
		if (procardTemplate == null) {
			errorMessage = "对不起没有找到目标卡片!";
			return ERROR;
		}
		list = procardServer.findCardTemplateTz(procardTemplate.getId());
		// return "ProcardTemplateTzs";
		return "ProcardTemplateGysTzs";
	}

	/***
	 * 查找外购外委采购计划用于外购件入库
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findWgWwPlanList2() {
		if (wwPlan != null) {
			ActionContext.getContext().getSession().put("wwPlan2", wwPlan);
		} else {
			wwPlan = (WaigouWaiweiPlan) ActionContext.getContext().getSession()
					.get("wwPlan2");
		}
		Object[] object = procardServer.findWgWwPlanList(wwPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, ms, null, null);
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouWaiweiPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("ProcardAction!findWgWwPlanList2.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgwwPlanList2";
	}

	/***
	 * 月度考核率汇总
	 * 
	 * @return
	 */
	public String showDaohuoLv() {
		if (ms == null)
			ms = new MonthlySummary();
		if (ms2 == null)
			ms2 = new MonthlySummary();
		if (ms3 == null)
			ms3 = new MonthlySummary();
		if (ms4 == null)
			ms4 = new MonthlySummary();
		if (weekds == null) {
			weekds = Util.getDateTime("yyyy-MM月");// 获得当前月份
		}
		ms.setMonth(weekds);
		ms.setNumber("dh");
		// ms2.setMonth(weekds);
		// ms2.setNumber("dh");
		ms3.setMonth(weekds);
		ms3.setNumber("kpjsr");
		ms4.setMonth(weekds);
		ms4.setNumber("hkwcr");
		ms = procardServer.showMsLv(ms);
		// ms2 = procardServer.showMsLv(ms2);
		ms3 = procardServer.showMsLv(ms3);
		ms4 = procardServer.showMsLv(ms4);
		return "showMonthLv";
	}

	// 根据编号查询外购外委采购
	public String findWaigouWaiweiPlanByid() {
		if (tag != null && !"".equals(tag)) {
			if (tag.equals("wwPlan")) {
				// 根据编号查询(外委)
				wwPlan = this.procardServer.findWaigouWaiweiPlanByid1(this.id);
			} else {
				// 根据编号查询(外购)
				wwPlan = this.procardServer.findWaigouWaiweiPlanByid(this.id);
			}
		}
		// 根据件号查询价格
		this.list = this.procardServer.findPrice(wwPlan.getMarkId());
		// Object[] o = { wwPlan, list};
		pageStatus = "";
		return "findWaigouWaiweiPlanByid";
	}

	// 批量申请付款
	public String salWaigouWaiweiPlanByid() {
		if (tag != null && !"".equals(tag)) {
			if (tag.equals("wgPlan")) {
				// 查询所有被选中的付款明细(外购)
				this.list = procardServer
						.salWaigouWaiweiPlanByid(detailSelect1);
			}
			if (tag.equals("wwPlan")) {
				// 查询所有被选中的付款明细(外委)
				this.list = procardServer
						.salWaigouWaiweiPlanByid1(detailSelect1);
			}
		}

		// if(list.size()>0){
		// for (int i = 0; i < list.size(); i++) {
		// WaigouWaiweiPlan waiweiPlan = (WaigouWaiweiPlan) list.get(i);
		// list2 = this.procardServer.findPrice(waiweiPlan.getMarkId());
		// }
		// }
		pageStatus = "all";
		return "findWaigouWaiweiPlanByid";
	}

	// 查询对应价格
	public void findWaigouWaiweiPlanPrice() {
		this.list = this.procardServer.findPrice(this.markId);
		MKUtil.writeJSON(list);
	}

	/***
	 * 外购采购计划确认
	 * 
	 * @param id
	 * @return
	 */
	public String jihuoWgPlan() {
		try {
			procardServer.jihuoWgPlan(id);
			// 整个bom确认采购计划激活完成后，设置bom的激活时间
			procardServer.setJihuoTime(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errorMessage = "确认成功!";
		url = "ProcardAction!findWgWwPlanList2.action?pageStatus=wgPlan";
		return ERROR;
	}

	/***
	 * 查询看板对应的工位的 所有待生产计划(展示)
	 * 
	 * @return
	 */
	public String viewProcardList() {
		// TODO:
		// TODO:
		// TODO:
		// TODO:
		// TODO:

		// list = procardServer.findViewList(id);
		// successMessage = Util.getDateTime("yyyy-MM-dd");
		return "Procard_viewProcardList";
	}

	/***
	 * 根据件号查批次(卡片类型=外购 状态=激活)
	 * 
	 * @return
	 */
	public void Android_findSelfCardBymarkId() {
		list = procardServer.Android_findSelfCardBymarkId(markId);
		if (list.size() > 0) {
			MKUtil.writeJSON(true, "", list);
		} else {
			MKUtil.writeJSON(false, "", null);
		}
	}

	/***
	 * 根据件号、批次查找数量
	 * 
	 * @return
	 */
	public void Android_findnumBymarkIdAndCard() {
		wwPlan = procardServer.Android_findnumBymarkIdAndCard(markId, selfCard);
		if (wwPlan != null) {
			MKUtil.writeJSON(true, "", wwPlan);
		} else {
			MKUtil.writeJSON(false, "", null);
		}
	}

	/***
	 * 更新所有的工作时长
	 */
	public String updateGzDateTime() {
		Date d1 = new Date();
		procardServer.updateGzDateTime();
		Date d2 = new Date();
		// System.out.println(d1);
		// System.out.println(d2);
		// System.out.println(d2.getTime()-d1.getTime());
		// System.out.println((d2.getTime()-d1.getTime())/1000/60);
		errorMessage = "计算完成!";
		return ERROR;
	}

	/****
	 * 查询出存在三检记录、巡检记录的已提交产品,用户入库
	 * 
	 * @return
	 */
	public String findNeedRukuPro() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("procard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"procard");
		}
		procardList = procardServer.findNeedRukuPro(procard);
		return "procard_rukuList";
	}

	// 根据编号查询
	public String openJdq() {
		errorMessage = procardServer.openJdq("192.168.0.32", 8801, Integer
				.parseInt(pageStatus));
		return "jdq";
	}

	public void getPMIData() {
		System.out.println("真的进来了。。。");
	}

	/***
	 * 打开或关闭PMI
	 * 
	 * @param pmiId
	 * @param openOrClose
	 *            1/0
	 * @return
	 */
	public String openOrClosePmi() {
		errorMessage = procardServer.openOrClosePmi(id, Integer
				.parseInt(pageStatus));
		return ERROR;
	}

	/****
	 * 计算批产员工工资
	 * 
	 * @return
	 */
	public String jisunLpPeoPleMoney() {
		try {
			errorMessage = procardServer.jisunLpPeoPleJiepai(startDate,
					endDate, markid, dateTime);
			// errorMessage = "";
			if ("".equals(errorMessage)) {
				obj = procardServer.jisunLpPeoPleMoney(startDate, endDate,
						markid, dateTime);
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "jisunLpPeoPleMoney";
	}

	/***
	 * 月度生产奖金分配查询
	 * 
	 * @return
	 */
	public String findUMMoneyByCondition() {
		if (umm != null) {
			ActionContext.getContext().getSession().put("umm", umm);
		} else {
			umm = (UserMonthMoney) ActionContext.getContext().getSession().get(
					"umm");
		}
		Object[] object = procardServer.findUMMoneyByCondition(umm, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				sum1 = (Float) object[2];
				sum2 = (Float) object[3];
				sum3 = (Float) object[4];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				if (pageStatus != null)
					this.setUrl("ProcardAction!findUMMoneyByCondition.action");
				else
					this
							.setUrl("ProcardAction!findUMMoneyByCondition.action?pageStatus="
									+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "UserMonthMoney_show";
	}

	/***
	 * Android根据code查询个人奖金
	 * 
	 * @return
	 */
	public void androidfindbonus() {
		Object[] object = procardServer.androidfindbonus(code1, pageNo1,
				pageSize1);
		if (object != null && object.length > 0) {
			List list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize1 - 1) / pageSize1;
				this.setTotal(pageCount + "");
				MKUtil.writeJSON(true, "加载成功", new Object[] { list, pageCount,
						cpage });
			} else {
				MKUtil.writeJSON(false, "数据异常！", null);
			}
		} else {
			MKUtil.writeJSON(false, "加载您的消息失败!", null);
		}
	}

	/***
	 * Android根据id查询个人奖金
	 * 
	 * @return
	 */
	public void androidfindById() {
		Object[] object = procardServer
				.androidfindById(idd, pageNo1, pageSize1);
		if (object != null && object.length > 0) {
			List list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize1 - 1) / pageSize1;
				this.setTotal(pageCount + "");
				MKUtil.writeJSON(true, "加载成功", new Object[] { list, pageCount,
						cpage });
			} else {
				MKUtil.writeJSON(false, "数据异常！", null);
			}
		} else {
			MKUtil.writeJSON(false, "加载您的消息失败!", null);
		}
	}

	/***
	 * 月度生产奖金申请转换为工资
	 * 
	 * @return
	 */
	public String findUMMoneyForJJ() {
		if (umm == null) {
			umm = new UserMonthMoney();
		}
		if (umm.getMonth() == null)
			umm.setMonth(Util.getLastMonth("yyyy-MM月"));// 默认获取上个月
		Object[] object = procardServer.findUMMoneyByCondition(umm, Integer
				.parseInt("0"), 0, pageStatus);
		if (object != null && object.length > 0) {
			list = (List) object[0];
		}
		return "UserMonthMoney_jjsq";
	}

	/***
	 * 月度生产奖金分配明细查询
	 * 
	 * @return
	 */
	public String findUserMoneyDetailById() {
		if (id != null && id > 0) {
			umm = procardServer.findUserMonthMoneyById(id);
		}
		if (umd != null) {
			ActionContext.getContext().getSession().put("umd", umd);
		} else {
			umd = (UserMoneyDetail) ActionContext.getContext().getSession()
					.get("umd");
		}
		if (umm != null || "gc".equals(pageStatus)) {
			pageSize = 30;
			Object[] object = procardServer.findUserMoneyDetailByFkId(id,
					Integer.parseInt(cpage), pageSize, pageStatus, umd,
					firstTime, endTime, tag);
			listAll = procardServer.findAllgongwei();
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					umd1 = (UserMoneyDetail) object[2];
					int pageCount = (count + pageSize - 1) / pageSize;
					if ("noPage".equals(tag)) {
						pageCount = 1;
					}
					this.setTotal(pageCount + "");
					if (pageStatus == null) {
						pageStatus = "";
					}
					if (id != null) {
						this
								.setUrl("ProcardAction!findUserMoneyDetailById.action?id="
										+ id + "&pageStatus=" + pageStatus);
					} else {
						this
								.setUrl("ProcardAction!findUserMoneyDetailById.action?pageStatus="
										+ pageStatus);
					}
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}

			return "UserMoneyDetail_show";
		} else {
			errorMessage = "您查询的月度奖金不存在!请刷新重试!";
			return ERROR;
		}
	}

	/**
	 * 给工序选择机器
	 */
	public void toChangeMachine() {
		list = procardServer.getPrcocessMachine(id);
		process = procardServer.getObjectByIdProcessInfor(id);
		MKUtil.writeJSON(true, null, process, list);
	}

	/**
	 * 给工序选择机器
	 */
	public void changeMachine() {
		String msg = procardServer.updateProcessMachine(process.getId(), id);
		MKUtil.writeJSON(msg);
	}

	/***
	 * 按照月份查询汇总奖金分配
	 * 
	 * @param month
	 *            查询月份
	 * @return
	 */
	public String findUMMAll() {
		if (weekds == null) {
			weekds = Util.getLastMonth("yyyy-MM月");// 默认获取上个月
		}
		list = procardServer.findUMMAll(weekds);
		return "UserMoneyAll_show";
	}

	/**
	 * 焊接统计(高温费)
	 * 
	 * @return
	 */
	public String hjtj() {
		list = procardServer.hjtj("2017-08-31", "2017-09-30", 22);
		return "procard_hjtj";
	}

	public String materialsParocardList() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!materialsParocardList.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != procard) {
			request.getSession().setAttribute("procard", procard);
		} else {
			procard = (Procard) request.getSession().getAttribute("procard");
		}
		if (procard == null) {
			procard = new Procard();
		}
		procard.setProcardStyle("总成");
		Object[] obj = procardServer.findAllProcard(procard, Integer
				.parseInt(cpage), pageSize, startDate, endDate, "materials");
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		return "procard_materialsCardList";
	}

	/**
	 * 物流物料备货通知
	 * 
	 * @return
	 */
	public String toMaterialsNotice() {
		if (markId != null && markId.length() > 0 && selfCard != null
				&& selfCard.length() > 0 && num != null && num > 0) {
			Map<Integer, Object> map = procardServer.getSonMaterial(markId,
					selfCard, num);
			try {
				if (map != null) {
					procardList = (List<Procard>) map.get(1);
					num = Integer.parseInt(map.get(2).toString());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return "procard_materialsNotice";
	}

	/**
	 * 发送配料需求并保存
	 * 
	 * @return
	 */
	public String saveSonMaterial() {
		try {
			String msg = procardServer.sendMaterialsNotify(pmHead, procardList);
			if (msg.equals("true")) {
				errorMessage = "发送配料提醒成功!";
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return "error";
	}

	public void getusers() {
		List list = procardServer.getUsersByDeptId(id, markId, selfCard);
		try {
			MKUtil.writeJSON(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查找备货通知
	 * 
	 * @return
	 */
	public String findpmList() {
		if (pmHead != null) {
			ActionContext.getContext().getSession().put("pmHead", pmHead);
		} else {
			pmHead = (ProcardMaterialHead) ActionContext.getContext()
					.getSession().get("pmHead");
		}
		Object[] object = procardServer.findPmHeadByCondition(pmHead, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!findpmList.action");
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "procard_materialsshowList";
	}

	public String getProcardMatrialDetail() {
		pmHead = procardServer.getProcardMatrialHead(id);
		list = procardServer.getProcardMatrialDetail(id);
		return "procard_materialdetailList";
	}

	/**
	 * 确认备齐
	 */
	public void sureMatrialajx() {
		try {
			String msg = procardServer.sureMatrial(id);
			if (msg.equals("true")) {
				MKUtil.writeJSON("true");
				return;
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		MKUtil.writeJSON(errorMessage);
	}

	/**
	 * 确认备齐
	 * 
	 * @return
	 */
	public String sureMatrial() {
		try {
			String msg = procardServer.sureMatrial(id);
			if (msg.equals("true")) {
				errorMessage = "配料备齐提醒发送成功!";
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return "error";
	}

	// 实时显示备料信息
	public String showProcardMaterialHead() {
		pageSize = 2;
		Object[] object = procardServer.findProcardMaterialHeadCondition(
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			listPMaterHe = new ArrayList<Map<String, Object>>();
			listPMaterHe = (List) object[0];
			if (listPMaterHe != null && listPMaterHe.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!showProcardMaterialHead.action");
				errorMessage = null;
			} else {
				this.setTotal("1");
				errorMessage = "当前没有待领叫料!";
			}
		}
		return "showProcardMaterialHead";
	}

	// /**
	// * 通过总成件号获取其下当前要备的货
	// * @return
	// */
	// public void getSonMaterial(){
	// procardList= procardServer.getSonMaterial(markId,selfCard,num);
	// MKUtil.writeJSON(procardList);
	// }
	/**
	 * 塞规测量件号尺寸
	 * 
	 * @return
	 */
	public String jianche() {
		// allMarkId = procardServer.findAllMarkId();
		return "jiancheshaigui";
	}

	/**
	 * 获取所有件号接口
	 */
	public void mmMarkId() {
		allMarkId = procardServer.findAllMarkId();
		MKUtil.writeJSON(allMarkId);
	}

	/**
	 * 获取该件号所有批次接口
	 */
	public void mmPiCi() {
		procardList = procardServer.findAllPiCi(markId);
		MKUtil.writeJSON(procardList);
	}

	public String addProcardSpeci() {
		if (procardSpecification != null) {
			procardSpecification = procardServer
					.addProcardSpeci(procardSpecification);
			if (procardSpecification != null) {
				errorMessage = "添加成功！";
			} else {
				errorMessage = "添加失败！请重试！！！";
			}
		} else {
			errorMessage = "对象为空,添加失败！";
		}
		// MKUtil.writeJSON(true,errorMessage,null);
		return "jiancheshaigui";
	}

	/***
	 * 展示订单数据和批次详情
	 * 
	 * @return
	 */
	public String showOrderAndProcard() {
		// Object[] object = procardServer.showOrderAndProcard(markId, page,
		// rows);
		// markId = (String) object[0];
		// // 订单信息
		// list = (List) object[1];
		// // 批次信息
		// procardList = (List) object[2];
		// allMarkId = (List) object[3];
		return "showScreen4_order";
	}

	public String showScreen() {
		Object[] data = procardServer.showOrderAndProcard(markId, page, rows);
		MKUtil.writeJSON(data);
		return null;
	}

	public String showScreen2() {
		Object[] data = procardServer.findViewList(id, page, rows);
		MKUtil.writeJSON(data);
		return null;
	}

	/**
	 * 删选人员技能
	 * 
	 * @return
	 */
	public void sxPeopleSkill() {
		String msg = procardServer.sxPeopleSkill();
		MKUtil.writeJSON(msg);
	}

	/**
	 * 工序委外申请挑选
	 * 
	 * @return
	 */
	public String forwaiweiParocardList() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!forwaiweiParocardList.action?pageStatus="
				+ pageStatus);
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != procard) {
			request.getSession().setAttribute("procardyxww", procard);
		} else {
			procard = (Procard) request.getSession()
					.getAttribute("procardyxww");
		}
		if (procard == null) {
			procard = new Procard();
		}
		procard.setProcardStyle("总成");
		procardList = procardServer.findAllProcarddcl(procard, startDate,
				endDate, pageStatus);// 待定的总成
		Object[] obj = procardServer.findAllProcard(procard, Integer
				.parseInt(cpage), pageSize, startDate, endDate, "forwaiwei");
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		return "procard_forwaiweiCardList";
	}

	/***
	 * 读取上一批次的委外记录，自动添加
	 * 
	 * @param id
	 * @return
	 */
	public String addHistoryWW() {
		errorMessage = procardServer.addHistoryWW(id);
		url = "ProcardAction!towwyx.action?id=" + id;
		return ERROR;
	}

	// 忧伤。。。又不要了
	// public String towwsqTree(){
	// return "process_toSetWaiWeiTree";
	// }
	// public void findProcardTemByRootId() {
	// procardList = procardServer.findProcardTemByRootId(id,1);
	// MKUtil.writeJSON(procardList);
	// }
	/**
	 * 跳往外委预选BOM
	 */
	public String towwyx() {
		Map<Integer,Object> map = procardServer.findtowwyxDataByrootId(id);
		List<ProcessInforWWApplyDetail> wwApplyDetailList =null;
		List<ProcessInfor> processList =null;
		if(map!=null&&map.get(1)!=null){
			procardList = (List<Procard>) map.get(1);
		}
		if(map!=null&&map.get(2)!=null){
			wwApplyDetailList = (List<ProcessInforWWApplyDetail>) map.get(2);
		}
		if(map!=null&&map.get(3)!=null){
			processList = (List<ProcessInfor>) map.get(3);
		}
		if(procardList!=null&&procardList.size()>0){
			for (Procard procard : procardList) {
				if (wwApplyDetailList != null
						&& wwApplyDetailList.size() > 0) {
					List<ProcessInforWWApplyDetail> wwApplyDetailList2 =new ArrayList<ProcessInforWWApplyDetail>();
					for (ProcessInforWWApplyDetail wwApplyDetail : wwApplyDetailList) {
						boolean sameId=false;
						if(wwApplyDetail.getProcardId().equals(procard.getId())){
							wwApplyDetail.setApplyStatus(wwApplyDetail
									.getProcessInforWWApply().getStatus());
							wwApplyDetailList2.add(wwApplyDetail);
							sameId =true;
						}else{
							if(sameId){
								break;
							}
						}
					}
					procard.setWwApplyDetailList(wwApplyDetailList2);
				}
				if(processList!=null&&processList.size()>0){
					List<ProcessInfor> processList2 = new ArrayList<ProcessInfor>();
					boolean sameId=false;
					for(ProcessInfor process:processList){
					if(process.getProcardId().equals(procard.getId())){
						Float wwblCount = 0f;
						if (procard.getWwblCount() != null) {
							wwblCount = procard.getWwblCount();
						}
						Float syCount = procard.getFilnalCount()
						- wwblCount - process.getScyzCount()
						- process.getSelectWwCount()
						- process.getApplyWwCount()
						- process.getAgreeWwCount();
						process.setApplyWwCount(syCount);
						processList2.add(process);
						sameId =true;
					}else{
						if(sameId){
							break;
						}
					}
				}
				procard.setProcessList(processList2);
				}
			}
		}
//		procardList = procardServer.findProcardTemByRootId(id, 1);
		return "process_toWaiWei";
	}

	/**
	 * 设定没有外委预选
	 */
	public void notowwyx() {
		try {
			errorMessage = procardServer.nowwyx(id, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MKUtil.writeJSON("数据异常:" + e.getMessage());
		}
		url = "ProcardAction!forwaiweiParocardList.action?cpage=" + cpage;
		MKUtil.writeJSON(errorMessage);
	}

	/**
	 * 设定没有外委预选
	 */
	public void yulan_nowwyx() {
		try {
			Object[] obj = procardServer.yulan_nowwyx(id);
			errorMessage = (String) obj[0];
			procardList = (List<Procard>) obj[1];
			procard = (Procard) obj[2];
			boolean bool = false;
			if ("true".equals(errorMessage)) {
				bool = true;
			}
			MKUtil.writeJSON(bool, errorMessage, procardList, procard);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 跳往申请外委预选操作
	 * 
	 * @return
	 */
	public String towwyxOption() {
		procard = procardServer.findProcardById(id);
		processNO = procardServer.getFisrtProcessNo(id);// 第一道工序
		processList = procardServer.findProcessListForWWByCardId(id);
		if (procard != null) {
			priceList = procardServer.getwwPriceByMarkId(procard.getMarkId());
			if (priceList != null && priceList.size() > 0) {
				int i = 0;
				for (Price price : priceList) {
					String wwType = null;
					if (price.getWwType() == null) {
						wwType = "工序外委";
					} else {
						wwType = price.getWwType();
					}
					if (i == 0) {
						tag = price.getGongxunum() + "_" + wwType;
					} else {
						tag += "," + price.getGongxunum() + "_" + wwType;
					}
					i++;
				}
			}
		}
		return "process_toSetWaiWei";
	}

	/**
	 * 预选外委申请
	 * 
	 * @return
	 */
	public void wwyx() {
		try {
			Map<Integer, Object> map= procardServer.wwyx(pwwApplyDetailList, id);
			String msg = map.get(1).toString();
			if (msg.equals("true")) {
				ProcessInforWWApplyDetail wwd= (ProcessInforWWApplyDetail) map.get(2);
				List<Integer> glIdList = (List<Integer>) map.get(3);
				MKUtil.writeJSON(true,"预选成功",wwd,glIdList);
			} else {
				MKUtil.writeJSON(msg);
			}
		} catch (Exception e) {
			MKUtil.writeJSON(e.getMessage());
		}
	}

	public void tclwwyx() {
		try {
			String msg = procardServer.tclwwyx(id);
			if (msg.equals("true")) {
				MKUtil.writeJSON("true");
			} else {
				MKUtil.writeJSON(msg);
			}
		} catch (Exception e) {
			MKUtil.writeJSON(e.getMessage());
		}
	}

	/**
	 * 获取外委预选对象与明细
	 * 
	 * @return
	 */
	public String processApplyDetail() {
		pwwApply = procardServer.getProcesswwApplyById(id);
		return "procard_wwsqDetail";
	}

	/**
	 * 外委申请列表展示
	 * 
	 * @return
	 */
	public String findProcessWWApplyList() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!findProcessWWApplyList.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != pwwApply) {
			request.getSession().setAttribute("pwwApply", pwwApply);
		} else {
			pwwApply = (ProcessInforWWApply) request.getSession().getAttribute(
					"pwwApply");
		}
		if (pwwApply == null) {
			pwwApply = new ProcessInforWWApply();
		}
		pwwApplyList = procardServer.finddclPwwApplyList(pwwApply);
		Object[] obj = procardServer.findAllPwwApplyList(pwwApply, Integer
				.parseInt(cpage), pageSize, null);
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		return "procard_processWWApplyList";
	}

	/**
	 * 外委申请明细
	 * 
	 * @return
	 */
	public String toShowApplyDetail() {
		pwwApply = procardServer.getProcesswwApplyById(id);
		return "procard_wwsqDetailop";
	}

	public void exportWwApplyDetail() {
		procardServer.exportWwApplyDetail(id);
	}

	/**
	 * 审批人查询外委未审批的明细
	 */

	public String showWeiApplyDetail() {
		pwwApplyDetailList = procardServer.showWeiApplyDetail();
		return "procard_wwsqDetailop1";
	}

	/**
	 * 批量审批外委明细
	 */
	public String shenPiApplyDetail() {
		procardServer.shenPiApplyDetail(processIds, tag);
		return "showWeiApplyDetail";
	}

	// /**
	// * 工序委外申请
	// * @return
	// */
	// public String forwaiweiSqParocardList() {
	// this.pageSize = 15;
	// this.setUrl("ProcardAction!forwaiweiSqParocardList.action");
	// HttpServletRequest request = ServletActionContext.getRequest();
	// if (null != procard) {
	// request.getSession().setAttribute("procardsqww", procard);
	// } else {
	// procard = (Procard) request.getSession().getAttribute("procardsqww");
	// }
	// if (procard == null) {
	// procard = new Procard();
	// }
	// procard.setProcardStyle("总成");
	// Object[] obj = procardServer.findAllProcard(procard, Integer
	// .parseInt(cpage), pageSize, startDate, endDate, "forwaiweisq");
	// int count = (Integer) obj[1];
	// int pageCount = (count + pageSize - 1) / pageSize;
	// this.setTotal(pageCount + "");
	// list = (List) obj[0];
	// return "procard_forwaiweiSqCardList";
	// }
	/**
	 * 工序外委明细
	 * 
	 * @return
	 */
	public String towwsqDetailList() {
		// procardList = procardServer.findWwsqDetailList(id);
		// return "procard_wwsqDetailList";
		pwwApplyList = procardServer.findProcesswwApplyList(id);
		if (pwwApplyList == null || pwwApplyList.size() == 0) {
			errorMessage = "没有预选记录";
			return "error";
		}
		return "procard_wwsqDetailList2";
	}

	/**
	 * 查询下层零件
	 */
	public void findSonMarkId() {
		List<String[]> sonMarkIdList = procardServer.findSonMarkId(id,
				processNos, processNames);
		MKUtil.writeJSON(sonMarkIdList);
	}

	/**
	 * 外委申请
	 * 
	 * @return
	 */
	public String wwsq() {
		try {
			Map<Integer, String> map = procardServer.wwsq(pwwApply);
			String msg = map.get(1).toString();
			String rootId = map.get(0).toString();
			if (msg.equals("true")) {
				errorMessage = "申请成功!";
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		this.setUrl("ProcardAction!toShowApplyDetail.action?id="
				+ pwwApply.getId());
		return "error";
	}

	/**
	 * 删除外委申请明细
	 * 
	 * @return
	 */
	public String deleteWwApplyDetail() {
		Map<Integer, String> map = procardServer.deleteWwApplyDetail(id);
		String msg = map.get(1).toString();
		if (msg.equals("true")) {
			errorMessage = "删除成功!";
			this.setUrl("ProcardAction!towwsqDetailList.action?id="
					+ map.get(2).toString());
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/**
	 * 批量删除外委申请明细
	 * 
	 * @return
	 */
	public String deleteWwApplyDetails() {
		try {
			errorMessage = procardServer.deleteWwApplyDetails(id,processIds,remark);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
		if ("true".equals(errorMessage)) {
			errorMessage = "删除成功!";
			this.setUrl("ProcardAction!toShowApplyDetail.action?id=" + id);
		}
		return "error";
	}

	/**
	 * 删除外委申请
	 * 
	 * @return
	 */
	public String deleteWwApply() {
		String msg = procardServer.deleteWwApply(id);
		if (msg.equals("true")) {
			errorMessage = "删除成功!";
			this.setUrl("ProcardAction!findProcessWWApplyList.action?");
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	public String totzwgj() {
		procardList = procardServer.getwwWgj(id);
		return "procard_wwtzwgj";
	}

	public String wwtzwgj() {
		String msg = procardServer.wwtzwgj(selected, id);
		if (msg.equals("true")) {
			errorMessage = "调整成功!";
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/**
	 * 查找所有没有价格合同的外委申请
	 * 
	 * @return
	 */
	public String findUnhasPrice() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!findUnhasPrice.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != pwwApplyDetail) {
			request.getSession().setAttribute("pwwApplyDetail", pwwApplyDetail);
		} else {
			pwwApplyDetail = (ProcessInforWWApplyDetail) request.getSession()
					.getAttribute("pwwApplyDetail");
		}
		Object[] obj = procardServer.findUnhasPriceList(pwwApplyDetail, Integer
				.parseInt(cpage), pageSize);
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		pwwApplyDetailList = (List) obj[0];
		return "procard_findUnhasPrice";
	}

	/**
	 * 跳往核对合同页面(额外外委)
	 * 
	 * @return
	 */
	public String toCheckHtList() {
		pwwApply = procardServer.getPwwApplyById(id);
		pwwApplyDetailList = procardServer.checkDetailByPwwApplyId(id);
		// priceList = procardServer.findPriceByWWApplyDetail(id);
		wwxd = procardServer.getWwxdStatus();
		return "procard_checkHtList";
	}

	/**
	 * 跳往核对合同页面(手动外委)
	 * 
	 * @return
	 */
	public String toCheckHt() {
		pwwApplyDetail = procardServer.getPwwApplyDetailById(id);
		priceList = procardServer.findPriceByWWApplyDetail(id);
		return "procard_checkHt";
	}

	/**
	 * 跳往核对合同页面(BOM外委)
	 * 
	 * @return
	 */
	public String toCheckHt2() {
		wwPlan = procardServer.findWaigouWaiweiPlanByid1(id);
		priceList = procardServer.findPriceBywwplan(id);
		return "procard_checkHt2";
	}

	/**
	 * 前往拆分外委工序数量
	 * 
	 * @return
	 */
	public String towwgxcf() {
		pwwApplyDetail = procardServer.getPwwApplyDetailById(id);
		return "procard_wwgxcf";
	}

	/**
	 * 拆分外委工序数量
	 * 
	 * @return
	 */
	public String wwgxcf() {
		String msg = procardServer.wwgxcf(id, selected);
		if (msg.equals("true")) {
			errorMessage = "拆分成功!";
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/**
	 * 确认外委工序列表(手动外委)
	 * 
	 * @return
	 */
	public String surewwapplyht() {
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get("surewwapplyht" + id);
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"surewwapplyht" + id,
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在操作改外委单请稍后再试");
		} else {
			return ERROR;
		}
		String msg = "";
		try {
			msg = procardServer.surewwapplyht(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMessage = e.toString();
			ActionContext.getContext().getApplication().put("surewwapplyht" + id, null);
			return "error";
		}
		if (msg.equals("true")) {
			errorMessage = "操作成功!";
			// this.setUrl("ProcardAction!toCheckHtList.action?id=" + id);
		} else {
			errorMessage = msg;
		}
		ActionContext.getContext().getApplication().put("surewwapplyht" + id, null);
		return "error";
	}

	/***
	 * 工序外委申请同意后，执行采购物料以及生产激活信息
	 * 
	 * @param id
	 * @return
	 */
	public String wwAuditToJihuo() {
		errorMessage = procardServer.wwAuditToJihuo(id);
		MKUtil.writeJSON(errorMessage);
		return null;
	}

	/**
	 * 根据外委申请反馈修改价格（手动外委）
	 * 
	 * @return
	 */
	public String updatePrice() {
		Map<Integer, String> map = procardServer.updatePrice(id, id2);
		String msg = map.get(1);
		if (msg.equals("true")) {
			errorMessage = "完成选定!";
			url = "ProcardAction!toCheckHtList.action?id=" + map.get(2);
		} else {
			errorMessage = msg;
			url = "ProcardAction!toCheckHt.action?id=" + map.get(2);
		}
		return "error";
	}

	/**
	 * 根据外委待激活序列反馈修改价格（BOM外委）
	 * 
	 * @return
	 */
	public String updatePrice2() {
		Map<Integer, String> map = procardServer.updatePrice2(id, id2);
		String msg = map.get(1);
		if (msg.equals("true")) {
			errorMessage = "完成选定!";
			// url = "ProcardAction!findNeedjhWgWwPlanList.action?cpage=" +
			// cpage;
		} else {
			errorMessage = msg;
			url = "ProcardAction!toCheckHt2.action?id=" + map.get(2);
		}
		return "error";
	}

	/**
	 * 外委核对合同
	 * 
	 * @return
	 */
	public String forwaiweiCheckList() {
		this.pageSize = 15;
		this.setUrl("ProcardAction!forwaiweiCheckList.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != pwwApply) {
			request.getSession().setAttribute("pwwApplyCheck", pwwApply);
		} else {
			pwwApply = (ProcessInforWWApply) request.getSession().getAttribute(
					"pwwApplyCheck");
		}
		if (pwwApply == null) {
			pwwApply = new ProcessInforWWApply();
		}
		Object[] obj = procardServer.findAllPwwApplyList(pwwApply, Integer
				.parseInt(cpage), pageSize, "forwaiweiCheck");
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		return "procard_forwaiweiCheckList";
	}

	/**
	 * 导出未录入价格外委
	 * 
	 * @return
	 */
	public String outDetailByPwwApply() {
		errorMessage = procardServer.OutDetailByPwwApply();
		this.setUrl("ProcardAction!findAllWei.action");
		return "error";
	}

	public String findAllWei() {
		Map<Integer, Object> map = procardServer.findAllWei(pwwApplyDetail,
				Integer.parseInt(cpage), pageSize, tag);
		pwwApplyDetailList = (List<ProcessInforWWApplyDetail>) map.get(1);
		if (pwwApplyDetailList != null & pwwApplyDetailList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("ProcardAction_findAllWei.action");
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "findAllWei";
	}

	/**
	 * 打回
	 * 
	 * @return
	 */
	public String backwwapplyht() {
		String msg = procardServer.backwwApply(pwwApply);
		if (msg.equals("true")) {
			errorMessage = "打回成功!";
		} else {
			errorMessage = msg;
		}
		url = "ProcardAction!toCheckHt.action?id=" + id;
		return "error";
	}

	/**
	 * 弥补包工包料外委零件
	 * 
	 * @return
	 */
	public void mibuProcessinforWWProcard() {
		procardServer.mibuProcessinforWWProcard();
		MKUtil.writeJSON("true");
	}

	public void getProcessInforByprocardId() {
		List<ProcessInfor> processList = procardServer
				.getProcessInforByprocardId(id, pageStatus, tc);
		try {
			MKUtil.writeJSON(processList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getprocardById() {
		try {
			procard = procardServer.findProcardById(id);
			if (procard != null) {
				MKUtil.writeJSON(procard);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 导出
	public void exportExcel() {
		procardServer.exportExcel(id);
	}

	// 根据父层id查询
	public void findProcardByfatherId() {
		procardList = procardServer.findProcardByfatherId(id, pageStatus);
		MKUtil.writeJSON(procardList);
	}

	// 扫描二维码领取工序;
	public String code() {
		procardList = procardServer.findprocardBybarcode(barcode);
		return "Procard_noCardList";
	}

	// 根据小组卡卡号和工序ID查询有考勤记录和有工序技能的员工;
	public void findUserByGroupCard() {
		try {
			List<String> strList = procardServer.findUserByGroupCard(
					cardNumber, id);
			MKUtil.writeJSON(strList);
		} catch (Exception e) {
			MKUtil.writeJSON("error");
		}

	}

	// 刷个人卡时自动去掉绑定的小组卡
	public void qxUserscardBd() {
		boolean bool = procardServer.qxUserscardBd(cardNumber);
		MKUtil.writeJSON(bool);
	}

	/**
	 * 更新第一步
	 */
	public void procardUpdateFirst() {
		try {
			String msg = procardServer.procardUpdateFirst(id);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 更新第二步
	 */
	public void procardUpdateSecond() {
		String msg = procardServer.procardUpdateSecond(id);
		MKUtil.writeJSON(msg);
	}

	// 查看包工包料自制件所关联的外购件件号
	public String showwgjmarkId() {
		Object[] obj = procardServer.showwgjmarkId(id);
		if (obj != null && obj.length == 2) {
			if (obj[0] != null) {
				procardList = (List<Procard>) obj[0];
			}
			if (obj[1] != null) {
				list = (List) obj[1];
			}
		}
		procard = procardServer.findProcardById(id);
		return "process_showwgj";
	}

	/***
	 * 查询产品信息
	 * 
	 * @return
	 */
	public String findProcardForQx() {
		if (id != null) {
			pageStatus = "bzsq";
		}
		procard = procardServer.findProcardForQx(procard, id);
		if (procard != null) {
			// 订单信息
			if (procard.getOrderId() != null) {
				orderManager = procardServer.findOrderManagerById(Integer
						.parseInt(procard.getOrderId()));
			} else {
				orderManager = procardServer.findOrderManagerByIoId(procard
						.getPlanOrderId());
			}
			// 出入库信息

		}
		if (procard != null && "总成".equals(procard.getProcardStyle())) {
			id = procard.getId();
		} else {
			id = null;
		}
		return "Procard_showQxm";
	}

	/**
	 * @Title: display
	 * @Description: 产品全息信息汇总
	 * @return String
	 * @throws
	 */
	public String initTrial() {
		String jsonStr = procardServer.packageData(id, null);
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(jsonStr);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void jihuoAgain() {
		String msg = procardServer.jihuoAgain(id);
		MKUtil.writeJSON(msg);
	}

	// 该工序所关联的所有外购件
	public void findProcessAndwgProcard() {
		try {
			processAndWgProcardList = procardServer.findProcessAndwgProcard(id);
			MKUtil.writeJSON(processAndWgProcardList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 提交不合格品
	public void submintBreak() {
		try {
			errorMessage = procardServer.submintBreak(breaksubmit, markIds,
					breakscount, pageStatus);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 不合格品数量品判断
	public void bhgNumPd() {
		try {
			Object[] obj = procardServer.bhgNumPd(breaksubmit);
			errorMessage = (String) obj[0];
			float tjbreakcount = (Float) obj[1];
			MKUtil.writeJSON(true, errorMessage, tjbreakcount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据功序Id获得 不合格品提交记录;
	public void findbreaksubmitByprocesId() {
		try {
			Object[] object = procardServer.findbreaksubmitByprocesId(id);
			breaksubmit = (BreakSubmit) object[0];
			boolean bool = (Boolean) object[1];
			MKUtil.writeJSON(bool, "", breaksubmit);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 查询所有提交不合格品记录；
	public String findbreaksubmitList() {
		if (breaksubmit != null) {
			ActionContext.getContext().getSession().put("breaksubmit",
					breaksubmit);
		} else {
			breaksubmit = (BreakSubmit) ActionContext.getContext().getSession()
					.get("breaksubmit");
		}
		Object[] object = procardServer.findbreaksubmitList(breaksubmit,
				pageStatus, Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			bsList = (List<BreakSubmit>) object[0];
			if (bsList != null && bsList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("ProcardAction!findbreaksubmitList.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			if ("zzsj".equals(pageStatus)) {
				return "InsRecord_tobreak";
			} else if ("wgsj".equals(pageStatus)) {
				return "InsRecord_towgbreak";
			}
			return "breaksubmit_List";
		}
		return "breaksubmit_List";
	}

	// 根据Id查询不合格品提交记录;
	public String findbreaksubmitById() {
		breaksubmit = procardServer.findBreakSubmitById(id);
		if ("QC".equals(pageStatus)) {
			list = procardServer.findpersonLiableList(breaksubmit);
		}
		return "breaksubmit_show";
	}

	// 选择责任人;
	public String choseUsers() {
		errorMessage = procardServer.choseUsers(breaksubmit);
		if ("true".equals(errorMessage)) {
			errorMessage = "选择成功";
		} else {
			errorMessage = "选择失败，请重新选择!";
		}
		return "findbreaksubmitById";
	}

	// 不合格品返修
	public String fanxiu() {
		errorMessage = procardServer.fanxiu(breaksubmit);
		return "findbreaksubmitById";
	}

	// 列出某个总成下所有外购件/半成品清单（带仓区）
	public String finddllProcard() {
		Object[] obj = procardServer
				.finddllProcard(id, Integer.parseInt(cpage));
		if (obj != null && obj.length > 0) {
			procardList = (List<Procard>) obj[0];
			if (bsList != null && bsList.size() > 0) {
				int count = (Integer) obj[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("ProcardAction!finddllProcard.action?pageStatus="
						+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "procard_dllList";
	}

	/****
	 * 查询产品明细--调整生产时间
	 * 
	 * @return
	 */
	public String findProcard() {
		procard = procardServer.findProcardById(id);
		return "Procard_updateTime";
	}

	/***
	 * 调整生产及激活时间
	 * 
	 * @param pageProcard
	 * @return
	 */
	public String updateProcardForTime() {
		boolean bool = procardServer.updateProcardForTime(procard);
		if (bool) {
			errorMessage = "调整完成";
		} else {
			errorMessage = "调整失败";
		}
		url = "ProcardAction!findProcard.action?id=" + procard.getId();
		return ERROR;
	}

	/***
	 * 调整领料状态
	 * 
	 * @param pageProcard
	 * @return
	 */
	public String updateProcardForLingliao() {
		boolean bool = procardServer.updateProcardForLingliao(procard);
		if (bool) {
			errorMessage = "修改成功!";
		} else {
			errorMessage = "修改失败!";
		}
		url = "ProcardAction!findProcardByRunCard2.action?pageStatus=history&viewStatus=update&id="
				+ procard.getId();
		return ERROR;
	}

	/**
	 * 同步送货明显的仓区
	 */
	public void copyWaigouDeliveryDetailcq() {
		procardServer.copyWaigouDeliveryDetailcq();
	}

	public void setMachineJdl() {
		procardServer.setMachineJdl();
	}

	/**
	 * 给总成添加入库工序
	 */
	public void setRuKuProcess() {
		procardServer.setRuKuProcess();
	}

	// 导出奖金明细
	public void exportExcelumd() {
		procardServer.exportExcelumd(umd, pageStatus, firstTime, endTime);
	}

	/***
	 * 生产计划管理 （入库计划、组装计划、日排产工序计划）
	 * 
	 * @return
	 */
	public void findAllPlan() {
		Map<String, String> map = procardServer.findAllPlan(pageStatus,
				firstTime, endTime, procard);
		MKUtil.writeJSON(map);
		// System.out.println(map.toString());
	}

	/***
	 * 生产计划管理 （入库计划、组装计划、日排产工序计划）导出至Excel
	 * 
	 * @return
	 */
	public void findAllPlanDaochu() {
		Map<String, String> map = procardServer.findAllPlan(pageStatus,
				firstTime, endTime, procard);
		// if(map!=null&&!map.isEmpty())
		procardServer.findAllPlanDaochu(map, firstTime, endTime);
		// else {
		// errorMessage = "内容为空";
		// }
	}

	public String backsdWwdetail() {
		Object[] objs = procardServer.backsdWwdetail(id);
		if (objs != null) {
			id = Integer.parseInt(objs[0].toString());
			String msg = objs[1].toString();
			if (msg.equals("true")) {
				errorMessage = "取消成功!";
			} else {
				errorMessage = msg;
				return "error";
			}
		}
		return null;
	}

	public String bdPeopleByRootId() {// ProcardAction!bdPeopleByRootId.action
		procardServer.bdPeopleByRootId(id);
		return null;
	}

	/**
	 * 查看BOM中所有有需要半成品转库的数据已经手动另外申请的半成品转库记录
	 * 
	 * @return
	 */
	public String bcprkManager() {
		procardVoList = procardServer.findBcprkDate(id, "show");
		return "procardbcp_rkshow";
	}

	public String showtzBywwapplyDetail() {
		list = procardServer.findtzBywwapplyDetail(id);
		if (list != null && list.size() > 0) {
			return "findGongyiGuifan";
		} else {
			errorMessage = "不存在该工序的工艺规范信息!";
		}
		return ERROR;
	}

	public String daochutzBywwapplyDetail() {
		Users user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请先登录!";
			return "error";
		}
		pwwApplyDetail = procardServer.getPwwApplyDetailById(id);
		Map<String, String> tzwzmap = procardServer.findtzBywwapplyDetail2(id);
		if (tzwzmap != null) {
			try {
				Set<String> keys = tzwzmap.keySet();
				if (keys != null && keys.size() > 0) {
					String path = ServletActionContext.getServletContext()
							.getRealPath("/upload/file/processTz");
					// ZIP打包图片
					String zipName = pwwApplyDetail.getMarkId() + "("
							+ pwwApplyDetail.getSelfCard() + ")";
					File zipFile = new File(path + "/" + zipName + ".zip");
					byte[] buf = new byte[1024];
					int len;
					ZipOutputStream zout = new ZipOutputStream(
							new FileOutputStream(zipFile));
					for (String filename : keys) {
						FileInputStream in = null;
						try {
							in = new FileInputStream(new File(path + "/"
									+ filename));
							if (in == null) {
								continue;
							}
						} catch (Exception e) {
							// TODO: handle exception
							continue;
						}
						zout.putNextEntry(new ZipEntry(tzwzmap.get(filename))); // 导出名称
						while ((len = in.read(buf)) > 0) {
							zout.write(buf, 0, len);
						}
						zout.closeEntry();
						in.close();
					}
					zout.close();

					// 下载图片
					FileInputStream zipInput = new FileInputStream(zipFile);
					HttpServletResponse response = ServletActionContext
							.getResponse();
					OutputStream out = response.getOutputStream();
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=" + zipName + ".zip");
					while ((len = zipInput.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					zipInput.close();
					out.flush();
					out.close();
					// 删除压缩包
					zipFile.delete();
					return null;
				} else {
					errorMessage = "对不起没有找到图纸!";
					return "error";
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		errorMessage = "对不起没有找到图纸!";
		return "error";
	}

	public String daochutzBywwapply() {
		Users user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请先登录!";
			return "error";
		}
		pwwApply = procardServer.getProcesswwApplyById(id);
		Map<String, String> tzwzmap = procardServer.findtzBywwapply2(id);
		if (tzwzmap != null) {
			try {
				Set<String> keys = tzwzmap.keySet();
				if (keys != null && keys.size() > 0) {
					String path = ServletActionContext.getServletContext()
							.getRealPath("/upload/file/processTz");
					// ZIP打包图片
					String zipName = pwwApply.getMarkId() + "("
							+ pwwApply.getSelfCard() + ")";
					File zipFile = new File(path + "/" + zipName + ".zip");
					byte[] buf = new byte[1024];
					int len;
					ZipOutputStream zout = new ZipOutputStream(
							new FileOutputStream(zipFile));
					for (String filename : keys) {
						FileInputStream in = null;
						try {
							in = new FileInputStream(new File(path + "/"
									+ filename));
							if (in == null) {
								continue;
							}
						} catch (Exception e) {
							// TODO: handle exception
							continue;
						}
						zout.putNextEntry(new ZipEntry(tzwzmap.get(filename))); // 导出名称
						while ((len = in.read(buf)) > 0) {
							zout.write(buf, 0, len);
						}
						zout.closeEntry();
						in.close();
					}
					zout.close();

					// 下载图片
					FileInputStream zipInput = new FileInputStream(zipFile);
					HttpServletResponse response = ServletActionContext
							.getResponse();
					OutputStream out = response.getOutputStream();
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename=" + zipName + ".zip");
					while ((len = zipInput.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					zipInput.close();
					out.flush();
					out.close();
					// 删除压缩包
					zipFile.delete();
					return null;
				} else {
					errorMessage = "对不起没有找到图纸!";
					return "error";
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		errorMessage = "对不起没有找到图纸!";
		return "error";
	}

	/**
	 * 修改领料状态
	 */
	public void updateLingliaoStatus() {
		// String msg = procardServer.updateLingliaoStatus();
		List<Procard> procardList = procardServer.findLingliaoStatus();
		Integer errorcount = 0;
		Integer allcount = procardList.size();
		if (procardList != null && procardList.size() > 0) {
			for (Procard procard : procardList) {
				boolean bool = procardServer.updateLingliaoStatus(procard
						.getId());
				if (bool) {
					errorcount++;
					System.out.println("修复领料状态:" + errorcount + "/" + allcount);
				}
			}
		}
		MKUtil.writeJSON(errorcount);
	}

	public void exportbcprkData() {
		procardServer.findBcprkDate(id, "export");
	}

	// 根据rootId查出总成外购件欠料情况;
	public String findLackWgProcardByRootId() {
		procardList = procardServer.findLackWgProcardByRootId(id);
		procard = procardServer.findProcardById(id);
		return "procard_LackWg";
	}

	// 跳转到调整工序的页面
	public String totzgongxu() {
		Object[] obj = procardServer.totzgongxu(id);
		pwwApplyDetail = (ProcessInforWWApplyDetail) obj[0];
		processList = (List<ProcessInfor>) obj[1];
		return "procard_tzgx";
	}

	// 调整工序
	public String tzgongxu() {
		errorMessage = procardServer.tzgongxu(id, processIds);
		url = "ProcardAction!totzgongxu.action?id=" + id;
		if ("true".equals(errorMessage)) {
			errorMessage = "调整成功!~";
		}
		return "error";
	}

	/**
	 * 批量生成BOM外委待报价数据
	 */
	public void pladddbjBomww() {
		String msg = procardServer.pladddbjBomww(id);
		MKUtil.writeJSON(msg);
	}

	/**
	 * 通过Id查询工序信息
	 */
	public String getProcessById() {
		process = procardServer.getProcessById(id);
		return "wlqueren";
	}

	/**
	 *工序确认物料数量
	 */
	public void wlqueren() {
		try {
			errorMessage = procardServer.wlqueren(id, num);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}

	}

	public void isCheck() {
		String msg = procardServer.isCheck(processNos, processNames, markId);
		MKUtil.writeJSON(msg);
	}

	public void linshi() {
		procardServer.linshi();
	}

	// 前往超损补料
	public String tocsbl() {
		procardList = procardServer.tocsbl(id, procard);
		return "procard_csbl";
	}

	public String csbl() {
		procardServer.csbl(id, processIds, processNumbers, null, processCards);
		return "findAllCsblOrderList";
	}

	/**
	 * 查询所有超损补料单
	 * 
	 * @return
	 */
	public String findAllCsblOrderList() {
		if (csblorder != null) {
			ActionContext.getContext().getSession().put("csblorder", csblorder);
		} else {
			csblorder = (ProcardCsBlOrder) ActionContext.getContext()
					.getSession().get("csblorder");
		}
		Object[] object = procardServer.findAllCsblOrderList(csblorder, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			csblorderList = (List<ProcardCsBlOrder>) object[0];
			if (csblorderList != null && csblorderList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("ProcardAction!findAllCsblOrderList.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "procard_csblOrderList";
	}

	/**
	 * 根据id查询超损补料明细
	 */
	public String findCsblListById() {
		Object[] obj = procardServer.findCsblListById(id, csbl);
		csblorder = (ProcardCsBlOrder) obj[0];
		csblList = (List<ProcardCsBl>) obj[1];
		if ("print".equals(pageStatus)) {
			return "csbl_print";
		}
		return "procard_csblList";
	}

	/**
	 * 删除超损补料
	 */
	public String delCsblOrder() {
		procardServer.delCsblOrder(csblorder);
		return "findAllCsblOrderList";
	}

	/**
	 * 供应商 超损补料
	 */
	public String gysCsbl() {
		try {
			errorMessage = procardServer
					.gysCsbl(processIds, processNumbers, id);
		} catch (Exception e) {
			errorMessage = e.getMessage();
		}
		return "procard_gyscsbl";
	}

	/**
	 * 获取总成BOM列表用来显示外委数据
	 * 
	 * @return
	 */
	public String findRootProcardListForww() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("rootprocardww",
					procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"rootprocardww");
		}
		Object[] object = procardServer.findRootProcardListForww(procard,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List<Provision>) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this
							.setUrl("ProcardAction!findRootProcardListForww.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "procard_showrootforww";
	}

	/**
	 * 获取总成BOM列表用来显示半成品转库数据
	 * 
	 * @return
	 */
	public String findRootProcardListForbcp() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("rootprocardww",
					procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"rootprocardww");
		}
		Object[] object = procardServer.findRootProcardListForww(procard,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List<Provision>) object[0];
			if (object != null && object.length > 0) {
				list = (List) object[0];
				if (list != null && list.size() > 0) {
					int count = (Integer) object[1];
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
					this
							.setUrl("ProcardAction!findRootProcardListForbcp.action");
					errorMessage = null;
				} else {
					errorMessage = "没有找到你要查询的内容,请检查后重试!";
				}
			}
		}
		return "procard_showrootforbcp";
	}

	/**
	 * 展示BOM中外委进度
	 * 
	 * @return
	 */
	public String showProcardwwList() {
		procard = procardServer.findProcardById(id);
		list = procardServer.showProcardwwList(id);
		return "procard_showwwlist";
	}

	/**
	 * 展示BOM中半成品转库进度
	 * 
	 * @return
	 */
	public String showProcardbcpList() {
		procard = procardServer.findProcardById(id);
		list = procardServer.showProcardbcpList(id);
		return "procard_showbcplist";
	}

	/**
	 * 展示BOM中外委进度
	 * 
	 * @return
	 */
	public String findProcardwwList() {
		procard = procardServer.findProcardById(id);
		list = procardServer.showProcardwwList(id);
		if (tag != null && tag.equals("export")) {
			return "procard_showwwlist2export";
		}
		return "procard_showwwlist2";
	}

	/**
	 * 外委领料
	 * 
	 * @return
	 */
	public String procardOutww() {
		if (lqCounts == null || lqCounts.length == 0) {
			errorMessage = "未输入领料数量，请刷新后重试!";
		} else {
			String msg = procardServer.procardOutww(selected, lqCounts,
					processIds, code1, pwsswords);
			if (msg.equals("true") || msg.length() == 0) {
				errorMessage = "领取成功!";
			} else {
				errorMessage = msg;
			}
		}
		url = "ProcardAction!showProcardwwList.action?id=" + id;
		return "error";
	}

	/**
	 * 半成品领料
	 * 
	 * @return
	 */
	public String procardOutbcp() {
		String msg = procardServer.procardOutbcp(selected, lqCounts,
				processIds, code1, pwsswords);
		if (msg.equals("true") || msg.length() == 0) {
			errorMessage = "领取成功!";
		} else {
			errorMessage = msg;
		}
		url = "ProcardAction!showProcardbcpList.action?id=" + id;
		return "error";
	}

	/**
	 * 一建设置不采购
	 * 
	 * @return
	 */
	public String OneUpdateCgStatus() {
		errorMessage = procardServer.OneUpdateCgStatus(processIds, procard);
		if ("true".equals(errorMessage)) {
			errorMessage = "设置成功!~";
		}
		url = "ProcardAction!findAllProcards.action?pageStatus=nocaigou";
		return "error";
	}

	public String xiufuTotalCount() {
		if (procard != null) {
			errorMessage = procardServer.xiufuTotalCount(procard);
		} else {
			errorMessage = "数据好像不太对劲啊，输入啊";
		}
		return "error";
	}

	public void findAllProcessInfor() {
		try {
			processList = procardServer.findAllProcessInfor(procard);
			MKUtil.writeJSON(processList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}

	}

	public String passProcessInfor() {
		errorMessage = procardServer.passProcessInfor(procard, processIds);
		return "error";
	}
	public String delProcessInfor() {
		errorMessage = procardServer.delProcessInfor(procard, processIds);
		return "error";
	}
	public String backProcessInfor() {
		errorMessage = procardServer.backProcessInfor(procard, processIds);
		return "error";
	}

	/**
	 * 根据rootId获取设变单号，设变Id
	 */
	public void getSbNumByRootId() {
		try {
			procard = procardServer.getSbNumByRootId(id);
			MKUtil.writeJSON(procard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据rootId查询所有可以退料的外协件
	 */
	public String findWxTuiLiaoByRootId() {
		wxtuiliaoList = procardServer.findWxTUILiaoByRootId(id);
		return "procard_wxtuiliao";
	}

	/**
	 * 外协退料申请
	 */
	public String WxTuiLiaoSq() {
		procardServer.WxTuiLiaoSq(wxtuiliaoList);
		return "findAllWxTuiliao";// procardBlAction_findAllWxTuiliao.action
	}

	public void waigouforPrice() {
		procardServer.waigouforPrice(procard, startDate, endDate);
	}

	// 查询所有状态为初始的各零件总和数量
	public String findAllWtcProcard() {
		this.pageSize = 15;
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != procard) {
			request.getSession().setAttribute("procard_wtc", procard);
		} else {
			procard = (Procard) request.getSession()
					.getAttribute("procard_wtc");
		}
		Object[] obj = procardServer.findAllWtcProcard(procard, Integer
				.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		this.setUrl("ProcardAction!findAllWtcProcard.action");
		return "procard_wtcList";
	}
	//根据外购procard Id 查询所有相关的领料批次和供应商
	public void findLingLiaoLotById(){
		try {
			 sellList =	procardServer.findLingLiaoLotById(id);
			MKUtil.writeJSON(sellList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON("error");
		}
	}
	
	
	public void waiweiforPrice() {
		procardServer.waiweiforPrice();
	}

	public void findOorderInventory() {
		procardServer.findOorderInventory(null);
	}
	
	/**
	 * 将原被删除的BOM的外委单转移到新的BOM上（包工包料）
	 */
	public void thwaiwei() {
		// 540591,1133
		// 542400,1134
		procardServer.thwaiwei(542400, 1134);
	}

	public String passbgbl(){
		if(procard!=null){
			errorMessage =procardServer.passbgbl(procard);
		}else{
			errorMessage="修改失败！";
		}
		return "error";
	}
	/**
	 * 可领数量为0的异常数据排查
	 * @return
	 */
	public void findAllKlNumExceptionList(){
		try {
			processList = procardServer.huoquAllKlNumExceptionList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 查询所有需要检验的总成。
	 * @return
	 */
	public String finddjyRootProcard(){
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != procard) {
			request.getSession().setAttribute("procard_djy", procard);
		} else {
			procard = (Procard) request.getSession().getAttribute("procard_djy");
		}
		Object obj[] =procardServer.finddjyRootProcard(procard, pageSize, 
				Integer.parseInt(cpage), pageStatus);
		procardList = (List<Procard>) obj[0];
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.url ="ProcardAction!finddjyRootProcard.action?pageStatus="+pageStatus;
		return "RootProcard_ToCheck";
	}
	public String toCheckwgww(){
		procard = procardServer.findProcardById(id);
		 order = procardServer.findOrderManagerById(Integer.parseInt(procard.getOrderId()));
		Object[] obj =	procardServer.getOsTemplate(procard.getMarkId(), procard.getBanBenNumber()
			, procard.getTjNumber()-(procard.getHgNumber()==null?0:procard.getHgNumber()));
		ot =  (OsTemplate) obj[0];
		list = (List) obj[1];
		tag = (String) obj[2];
		return "Procard_checkRoot";
	}
	public String checkwgww(){
		errorMessage =	procardServer.checkwgww(id, osRecord, jyNumber, osRecordList,
				bugOsRecordList, bugNumber, tag);
		if("true".equals(errorMessage)){
			errorMessage = "检验完成!~";
		}
		this.url="ProcardAction!finddjyRootProcard.action";
		return "error";
	}
	public void findProcessLogByProcessId(){
		try {
			ProcessInforReceiveLog processLog = procardServer.findProcessLogByProcessId(id);
			MKUtil.writeJSON(processLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getcjNumber(){
		try {
			Float cjNum = 	procardServer.getcjNumber(jyNumber, id);
			MKUtil.writeJSON(cjNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Procard getProcard() {
		return procard;
	}

	public void setProcard(Procard procard) {
		this.procard = procard;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getCpage() {
		return cpage;
	}

	public void setCpage(String cpage) {
		this.cpage = cpage;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	public List<Procard> getProcardList() {
		return procardList;
	}

	public void setProcardList(List<Procard> procardList) {
		this.procardList = procardList;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public RunningWaterCardServer getRunningWCServer() {
		return runningWCServer;
	}

	public void setRunningWCServer(RunningWaterCardServer runningWCServer) {
		this.runningWCServer = runningWCServer;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer[] getProcessIds() {
		return processIds;
	}

	public void setProcessIds(Integer[] processIds) {
		this.processIds = processIds;
	}

	public Float[] getProcessNumbers() {
		return processNumbers;
	}

	public void setProcessNumbers(Float[] processNumbers) {
		this.processNumbers = processNumbers;
	}

	public String[] getProcessCards() {
		return processCards;
	}

	public void setProcessCards(String[] processCards) {
		this.processCards = processCards;
	}

	public ProcessInfor getProcess() {
		return process;
	}

	public void setProcess(ProcessInfor process) {
		this.process = process;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public Integer getMaxBelongLayer() {
		return maxBelongLayer;
	}

	public void setMaxBelongLayer(Integer maxBelongLayer) {
		this.maxBelongLayer = maxBelongLayer;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public List getContentList() {
		return contentList;
	}

	public void setContentList(List contentList) {
		this.contentList = contentList;
	}

	public List getIsQualifiedList() {
		return isQualifiedList;
	}

	public void setIsQualifiedList(List isQualifiedList) {
		this.isQualifiedList = isQualifiedList;
	}

	public String getViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(String viewStatus) {
		this.viewStatus = viewStatus;
	}

	public String getMarkid() {
		return markid;
	}

	public void setMarkid(String markid) {
		this.markid = markid;
	}

	public Integer getProcessNO() {
		return processNO;
	}

	public void setProcessNO(Integer processNO) {
		this.processNO = processNO;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ProcardTemplate getProcardTemplate() {
		return procardTemplate;
	}

	public void setProcardTemplate(ProcardTemplate procardTemplate) {
		this.procardTemplate = procardTemplate;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public int[] getSelected() {
		return selected;
	}

	public void setSelected(int[] selected) {
		this.selected = selected;
	}

	public List getListAll() {
		return listAll;
	}

	public void setListAll(List listAll) {
		this.listAll = listAll;
	}

	public WaigouWaiweiPlan getWwPlan() {
		return wwPlan;
	}

	public void setWwPlan(WaigouWaiweiPlan wwPlan) {
		this.wwPlan = wwPlan;
	}

	public List<WaigouWaiweiPlan> getWwPlanList() {
		return wwPlanList;
	}

	public void setWwPlanList(List<WaigouWaiweiPlan> wwPlanList) {
		this.wwPlanList = wwPlanList;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getSelfCard() {
		return selfCard;
	}

	public void setSelfCard(String selfCard) {
		this.selfCard = selfCard;
	}

	public String[] getMarkIds() {
		return markIds;
	}

	public void setMarkIds(String[] markIds) {
		this.markIds = markIds;
	}

	public float getNums() {
		return nums;
	}

	public void setNums(float nums) {
		this.nums = nums;
	}

	public Integer[] getDetailSelect() {
		return detailSelect;
	}

	public void setDetailSelect(Integer[] detailSelect) {
		this.detailSelect = detailSelect;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer[] getDetailSelect1() {
		return detailSelect1;
	}

	public void setDetailSelect1(Integer[] detailSelect1) {
		this.detailSelect1 = detailSelect1;
	}

	public String getWeekds() {
		return weekds;
	}

	public void setWeekds(String weekds) {
		this.weekds = weekds;
	}

	public MonthlySummary getMs() {
		return ms;
	}

	public void setMs(MonthlySummary ms) {
		this.ms = ms;
	}

	public MonthlySummary getMs2() {
		return ms2;
	}

	public void setMs2(MonthlySummary ms2) {
		this.ms2 = ms2;
	}

	public MonthlySummary getMs3() {
		return ms3;
	}

	public void setMs3(MonthlySummary ms3) {
		this.ms3 = ms3;
	}

	public MonthlySummary getMs4() {
		return ms4;
	}

	public void setMs4(MonthlySummary ms4) {
		this.ms4 = ms4;
	}

	public List<ProcessinforFuLiao> getProcessinforFuLiaoList() {
		return processinforFuLiaoList;
	}

	public void setProcessinforFuLiaoList(
			List<ProcessinforFuLiao> processinforFuLiaoList) {
		this.processinforFuLiaoList = processinforFuLiaoList;
	}

	public Object[] getObj() {
		return obj;
	}

	public void setObj(Object[] obj) {
		this.obj = obj;
	}

	public UserMonthMoney getUmm() {
		return umm;
	}

	public void setUmm(UserMonthMoney umm) {
		this.umm = umm;
	}

	public Integer getId2() {
		return id2;
	}

	public void setId2(Integer id2) {
		this.id2 = id2;
	}

	public List<Machine> getMachineList() {
		return machineList;
	}

	public void setMachineList(List<Machine> machineList) {
		this.machineList = machineList;
	}

	public MachineDayYZSJServer getMdyServer() {
		return mdyServer;
	}

	public void setMdyServer(MachineDayYZSJServer mdyServer) {
		this.mdyServer = mdyServer;
	}

	public MachineDayYZSJ getMdy() {
		return mdy;
	}

	public void setMdy(MachineDayYZSJ mdy) {
		this.mdy = mdy;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public List<String> getAllMarkId() {
		return allMarkId;
	}

	public void setAllMarkId(List<String> allMarkId) {
		this.allMarkId = allMarkId;
	}

	public List<ProcardSpecification> getProcardSpecificationList() {
		return procardSpecificationList;
	}

	public void setProcardSpecificationList(
			List<ProcardSpecification> procardSpecificationList) {
		this.procardSpecificationList = procardSpecificationList;
	}

	public ProcardSpecification getProcardSpecification() {
		return procardSpecification;
	}

	public void setProcardSpecification(
			ProcardSpecification procardSpecification) {
		this.procardSpecification = procardSpecification;
	}

	public ProcardMaterialHead getPmHead() {
		return pmHead;
	}

	public void setPmHead(ProcardMaterialHead pmHead) {
		this.pmHead = pmHead;
	}

	public List<ProcardMaterialHead> getPmHeadList() {
		return pmHeadList;
	}

	public void setPmHeadList(List<ProcardMaterialHead> pmHeadList) {
		this.pmHeadList = pmHeadList;
	}

	public List<ProcessInfor> getProcessList() {
		return processList;
	}

	public void setProcessList(List<ProcessInfor> processList) {
		this.processList = processList;
	}

	public List<Price> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Price> priceList) {
		this.priceList = priceList;
	}

	public List<ProcessInforWWApply> getPwwApplyList() {
		return pwwApplyList;
	}

	public void setPwwApplyList(List<ProcessInforWWApply> pwwApplyList) {
		this.pwwApplyList = pwwApplyList;
	}

	public List<ProcessInforWWApplyDetail> getPwwApplyDetailList() {
		return pwwApplyDetailList;
	}

	public void setPwwApplyDetailList(
			List<ProcessInforWWApplyDetail> pwwApplyDetailList) {
		this.pwwApplyDetailList = pwwApplyDetailList;
	}

	public ProcessInforWWApply getPwwApply() {
		return pwwApply;
	}

	public void setPwwApply(ProcessInforWWApply pwwApply) {
		this.pwwApply = pwwApply;
	}

	public ProcessInforWWApplyDetail getPwwApplyDetail() {
		return pwwApplyDetail;
	}

	public void setPwwApplyDetail(ProcessInforWWApplyDetail pwwApplyDetail) {
		this.pwwApplyDetail = pwwApplyDetail;
	}

	public Integer getBanci() {
		return banci;
	}

	public void setBanci(Integer banci) {
		this.banci = banci;
	}

	public List<Map<String, Object>> getListPMaterHe() {
		return listPMaterHe;
	}

	public void setListPMaterHe(List<Map<String, Object>> listPMaterHe) {
		this.listPMaterHe = listPMaterHe;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getJhStatus() {
		return jhStatus;
	}

	public void setJhStatus(String jhStatus) {
		this.jhStatus = jhStatus;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public List<ProcessInforReceiveLog> getPgList() {
		return pgList;
	}

	public void setPgList(List<ProcessInforReceiveLog> pgList) {
		this.pgList = pgList;
	}

	public ProcessSaveLog getProcessSaveLog() {
		return processSaveLog;
	}

	public void setProcessSaveLog(ProcessSaveLog processSaveLog) {
		this.processSaveLog = processSaveLog;
	}

	public BreakSubmit getBreaksubmit() {
		return breaksubmit;
	}

	public void setBreaksubmit(BreakSubmit breaksubmit) {
		this.breaksubmit = breaksubmit;
	}

	public Float[] getBreakscount() {
		return breakscount;
	}

	public void setBreakscount(Float[] breakscount) {
		this.breakscount = breakscount;
	}

	public OrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public List<BreakSubmit> getBsList() {
		return bsList;
	}

	public void setBsList(List<BreakSubmit> bsList) {
		this.bsList = bsList;
	}

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

	public Integer getIdd() {
		return idd;
	}

	public void setIdd(Integer idd) {
		this.idd = idd;
	}

	public int getPageSize1() {
		return pageSize1;
	}

	public void setPageSize1(int pageSize1) {
		this.pageSize1 = pageSize1;
	}

	public int getPageNo1() {
		return pageNo1;
	}

	public void setPageNo1(int pageNo1) {
		this.pageNo1 = pageNo1;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public UserMoneyDetail getUmd() {
		return umd;
	}

	public void setUmd(UserMoneyDetail umd) {
		this.umd = umd;
	}

	public String getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public UserMoneyDetail getUmd1() {
		return umd1;
	}

	public void setUmd1(UserMoneyDetail umd1) {
		this.umd1 = umd1;
	}

	public String getWwxd() {
		return wwxd;
	}

	public void setWwxd(String wwxd) {
		this.wwxd = wwxd;
	}

	public List<ProcardVo> getProcardVoList() {
		return procardVoList;
	}

	public void setProcardVoList(List<ProcardVo> procardVoList) {
		this.procardVoList = procardVoList;
	}

	public Float getSum1() {
		return sum1;
	}

	public void setSum1(Float sum1) {
		this.sum1 = sum1;
	}

	public Float getSum2() {
		return sum2;
	}

	public void setSum2(Float sum2) {
		this.sum2 = sum2;
	}

	public Float getSum3() {
		return sum3;
	}

	public void setSum3(Float sum3) {
		this.sum3 = sum3;
	}

	/**
	 * @return the tc
	 */
	public String getTc() {
		return tc;
	}

	/**
	 * @param tc
	 *            the tc to set
	 */
	public void setTc(String tc) {
		this.tc = tc;
	}

	public String getProcessNos() {
		return processNos;
	}

	public void setProcessNos(String processNos) {
		this.processNos = processNos;
	}

	public String getProcessNames() {
		return processNames;
	}

	public void setProcessNames(String processNames) {
		this.processNames = processNames;
	}

	public String getNoZhuser() {
		return noZhuser;
	}

	public void setNoZhuser(String noZhuser) {
		this.noZhuser = noZhuser;
	}

	public Float[] getLqCounts() {
		return lqCounts;
	}

	public void setLqCounts(Float[] lqCounts) {
		this.lqCounts = lqCounts;
	}

	public List<ProcardCsBlOrder> getCsblorderList() {
		return csblorderList;
	}

	public void setCsblorderList(List<ProcardCsBlOrder> csblorderList) {
		this.csblorderList = csblorderList;
	}

	public ProcardCsBl getCsbl() {
		return csbl;
	}

	public void setCsbl(ProcardCsBl csbl) {
		this.csbl = csbl;
	}

	public List<ProcardCsBl> getCsblList() {
		return csblList;
	}

	public void setCsblList(List<ProcardCsBl> csblList) {
		this.csblList = csblList;
	}

	public ProcardCsBlOrder getCsblorder() {
		return csblorder;
	}

	public void setCsblorder(ProcardCsBlOrder csblorder) {
		this.csblorder = csblorder;
	}

	public String getProcessIds_str() {
		return processIds_str;
	}

	public void setProcessIds_str(String processIdsStr) {
		processIds_str = processIdsStr;
	}

	public List<ProcardWxTuiLiao> getWxtuiliaoList() {
		return wxtuiliaoList;
	}

	public void setWxtuiliaoList(List<ProcardWxTuiLiao> wxtuiliaoList) {
		this.wxtuiliaoList = wxtuiliaoList;
	}

	public String getPwsswords() {
		return pwsswords;
	}

	public void setPwsswords(String pwsswords) {
		this.pwsswords = pwsswords;
	}

	public boolean isIsbcpqx() {
		return isbcpqx;
	}

	public void setIsbcpqx(boolean isbcpqx) {
		this.isbcpqx = isbcpqx;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<Goods> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}

	public List<Sell> getSellList() {
		return sellList;
	}

	public void setSellList(List<Sell> sellList) {
		this.sellList = sellList;
	}

	public OsTemplate getOt() {
		return ot;
	}

	public void setOt(OsTemplate ot) {
		this.ot = ot;
	}

	public Integer getJyNumber() {
		return jyNumber;
	}

	public void setJyNumber(Integer jyNumber) {
		this.jyNumber = jyNumber;
	}

	public OsRecord getOsRecord() {
		return osRecord;
	}

	public void setOsRecord(OsRecord osRecord) {
		this.osRecord = osRecord;
	}

	public List<OsRecord> getOsRecordList() {
		return osRecordList;
	}

	public void setOsRecordList(List<OsRecord> osRecordList) {
		this.osRecordList = osRecordList;
	}

	public List<OsRecord> getBugOsRecordList() {
		return bugOsRecordList;
	}

	public void setBugOsRecordList(List<OsRecord> bugOsRecordList) {
		this.bugOsRecordList = bugOsRecordList;
	}

	public int getBugNumber() {
		return bugNumber;
	}

	public void setBugNumber(int bugNumber) {
		this.bugNumber = bugNumber;
	}

	public OrderManager getOrder() {
		return order;
	}

	public void setOrder(OrderManager order) {
		this.order = order;
	}

	
	

}
