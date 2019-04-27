package com.task.action.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.WareHouseAuthService;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.WaigouWaiweiPlanServer;
import com.task.entity.Also;
import com.task.entity.Borrow;
import com.task.entity.Goods;
import com.task.entity.OaAppDetail;
import com.task.entity.Price;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.WarehouseNumber;
import com.task.entity.android.OsRecord;
import com.task.entity.android.OsTemplate;
import com.task.entity.gzbj.ProcessAndMeasuring;
import com.task.entity.hegebaobiao.DayHege;
import com.task.entity.hegebaobiao.MouthHege;
import com.task.entity.hegebaobiao.QuarterHege;
import com.task.entity.hegebaobiao.YearHege;
import com.task.entity.hegebaobiao.ZhouHege;
import com.task.entity.menjin.KuweiSongHuoDan;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.qualifiedrate.QualifiedRate;
import com.task.entity.sop.DefectiveProduct;
import com.task.entity.sop.GysMarkIdCkolse;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardCsBl;
import com.task.entity.sop.ReturnSingle;
import com.task.entity.sop.ReturnsDetails;
import com.task.entity.sop.WaigouDailySheet;
import com.task.entity.sop.WaigouDelivery;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouPlanclApply;
import com.task.entity.sop.WgProcardMOQ;
import com.task.util.MKUtil;
import com.task.util.Util;
import com.tast.entity.zhaobiao.Waigoujianpinci;
import com.tast.entity.zhaobiao.WaigoujianpinciZi;
import com.tast.entity.zhaobiao.ZhUser;

/**
 * 外购外委计划Action层
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings("unchecked")
public class WaigouwaiweiPlanAction extends ActionSupport {
	public List<String> getStrList1() {
		return strList1;
	}

	public void setStrList1(List<String> strList1) {
		this.strList1 = strList1;
	}

	private static final long serialVersionUID = 1L;
	private WaigouWaiweiPlanServer wwpServer;
	private ProcardServer procardServer;// 流水卡片对象
	private WaigouPlan waigouPlan;
	private WaigouOrder waigouOrder;// 外购采购订单表
	private WaigouOrder waigouOrder2;
	private WaigouDelivery waigouDelivery;// 送货单
	private WarehouseNumber warehouseNumber;// 库位对象
	private Borrow borrow;// 借用对象
	private Also also;// 归还对象
	private Procard procard;
	private OsRecord osRecord;// 检验记录
	private List procardList;
	private List<WaigouPlan> wwPlanList;
	private List list;
	private List<WaigouOrder> waigouOrderList;
	private WaigouDeliveryDetail waigoudd;// 送货单明细
	private List<WaigouDeliveryDetail> list_wdd;
	private List<OaAppDetail> oaDList;//
	private List<WarehouseNumber> warehouseNumbers;// 库位list
	private List<WareBangGoogs> wareBangGoogs;// 库位&明细中间表list
	private ManualOrderPlan manualPlan;
	private List<ManualOrderPlan> mopList;
	private Goods goods;// 库存对象;
	private Integer id;
	private String needDate;// 到货日期
	private Integer id2;
	private Integer id_wn;// 库位ID
	private Integer id_wdd;// 送货单明细ID
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private String pageStatus;// 页面状态
	private String viewStatus;// 查看状态
	private String tag;//
	private String deltag;// 删除类型(del/close/cong)
	private String bacode;// 条码
	private String planNumber;// 订单编号(OS201702220001)
	private String mxId;// 采购明细ID
	private OsTemplate osTemplate;// 检验模版
	private DefectiveProduct dp;// 不良品处理单
	private ReturnSingle rs;// 退货单
	private ReturnsDetails rds;// 退货单明细
	private ZhUser zhUser;// 供应商
	private Double sumMoney;
	private Float sumNum;
	private Double sumbhsprice;//
	private Float sumbdhNUmber;// 到货数量
	private Float sumwshNum;// 未送货数量
	private String wgTypes;
	private String barCode;// 条形码
	private List<Sell> listSell;
	private List<Goods> goodsList;
	private List<OsTemplate> ostList;
	private WaigouDailySheet wgdSheet;
	private WaigoujianpinciZi xujianpingci;
	private List<WarehouseNumber> wnList;
	private List<ZhUser> zhuserList;
	private List<Waigoujianpinci> xjbzlist;
	private List<QualifiedRate> qrList;
	private List cqList;
	private QualifiedRate qualifiedRate;
	private String markId;
	private String kgliao;
	private String banbenNumber;
	private String specification;
	private Float goodsCount;
	private String strids;
	private List<ProcardCsBl> csblList;
	private List<WaigouPlanclApply> wgclApplyList;
	private List<ProcessAndMeasuring> pamList;
	private GysMarkIdCkolse gysClose;
	private List<GysMarkIdCkolse> gysCloseList;
	private String sunxu;
	private String checkTimes;//检验时间（多个，以,分开）
	private int splitCount1;
	private int splitCount2;
	
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private int pageSize1;

	private String startDate;// 开始时间
	private String endDate;// 截止时间
	private Integer[] processIds;
	private Float[] processNumbers;
	private String[] processCards;
	private Float buhegeNumber;
	private Float hegeNumber;
	private int[] selected;
	private List listAll;
	private String isHege;// 是否合格（yes no）
	private String noOperation;// 是否操作（yes no）
	private List<OsRecord> osRecordList;
	private List<OsRecord> bhgosRecordList;
	private String noPrice;
	private String firsttime;
	private String endtime;
	private List<String> strList;
	private List<String> strList1;
	private WareHouseAuthService wareHouseAuthService;
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;

	private String hegeStatus;
	private List<DayHege> dayHegeList;
	private List<ZhouHege> zhouList;
	private List<MouthHege> mouthHegeList;
	private List<QuarterHege> quarterHegeList;
	private List<YearHege> yearHegeList;
	private DayHege dayHege;
	private ZhouHege zhouHege;
	private MouthHege mouthHege;
	private QuarterHege quarterHege;
	private YearHege yearHege;

	private String[] fileType;

	private WgProcardMOQ wgmoq;
	private int ids[];// 审批id
	private String tuisongId;
	private String count;
	private Float[] caigouNums;// 采购数量
	private String exportTag;
	private Price price;
	private String remark;
	private String type;
	private String months0;
	private String months1;
	private String years0;
	private String years1;

	/**
	 * 获取所有送货中的送货单
	 */
	public void findAllDshJson() {
		waigouOrderList = wwpServer.findAllDsh();
		if (waigouOrderList != null && waigouOrderList.size() > 0) {
			for (WaigouOrder order : waigouOrderList) {
				order.setUser(null);
				order.setZhUser(null);
				order.setWwpSet(null);
			}
		}
		MKUtil.writeJSON(waigouOrderList);
	}

	/****
	 * 待确认物料(总成开头)
	 * 
	 * @return
	 */
	public String findDqrWlList() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("wlProcard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"wlProcard");
		}

		Object[] object = null;
		// 所有产品
		object = wwpServer.findDqrWlList(procard, Integer.parseInt(cpage),
				pageSize, pageStatus);
		if (object != null && object.length > 0) {
			procardList = (List<Procard>) object[0];
		}

		if (procardList != null && procardList.size() > 0) {
			int count = (Integer) object[1];
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			if (pageStatus == null) {
				pageStatus = "";
			}
			this
					.setUrl("WaigouwaiweiPlanAction!findDqrWlList.action?pageStatus="
							+ pageStatus + "&tag=" + tag);
			errorMessage = null;
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "Procard_wlList";
	}

	/****
	 * 待确认物料明细(总成——外购明细)
	 * 
	 * @return
	 */
	public String findDqrWlDetailList() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("wldProcard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"wldProcard");
		}
		Object[] object = wwpServer.findDqrWlDetailList(id, procard);
		if (object != null && object.length > 0) {
			procardList = (List<Procard>) object[0];
			list = new ArrayList();
			list.add(object[1]);// 需求总量
			list.add(object[2]);// 采购数量
			list.add(object[3]);// 总成信息
			List<WgProcardMOQ> wgmoqList = (List<WgProcardMOQ>) object[4];
			if (wgmoqList != null && wgmoqList.size() > 0) {
				wwpServer.saveWgProcardMOQ(wgmoqList);
			}
			return "Procard_WgPlanList";
		}
		errorMessage = "不存在您要确认的物料列表,请刷新后重试!";
		return ERROR;
	}

	/****
	 * 物料清单确认，进入采购流程
	 * 
	 * @param rootId
	 * @return
	 */
	public String updateForWgPlan() {
		if (id != null && date(needDate)) {
			errorMessage = wwpServer.updateForWgPlan(id, needDate);
			if ("true".equals(errorMessage)) {
				errorMessage = "物料清单提交成功!";
				url = "WaigouwaiweiPlanAction!findDqrWlList.action?pageStatus=dqr";
			}
		} else {
			errorMessage = "到货日期(" + needDate + ")不能小于今天("
					+ Util.getDateTime("yyyy-MM-dd") + "),请重新选择!";
		}
		return ERROR;
	}

	public boolean date(String needDate) {
		if (needDate == null || "".equals(needDate))
			return true;
		return Util.getDateTime("yyyy-MM-dd").compareTo(needDate) <= 0;
	}

	/****
	 * 物料追踪管理(总成——外购明细)
	 * 
	 * @return
	 */
	public String findAllWlDetailList() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("wldProcard", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"wldProcard");
		}
		Object[] object = wwpServer.findAllWlDetailList(id, procard);
		if (object != null && object.length > 0) {
			procardList = (List<Procard>) object[0];
			list = new ArrayList();
			list.add(object[1]);// 需求总量
			list.add(object[2]);// 采购数量
			list.add(object[3]);// 总成信息
			list.add(object[4]);// 下单数量
			list.add(object[5]);// 到货数量
			list.add(object[6]);// 入库数量
			list.add(object[7]);// 领料数量
			return "Procard_WgPlanListAll";
		}
		errorMessage = "不存在您要确认的物料列表,请刷新后重试!";
		return ERROR;
	}

	/****
	 * 所有待采购的采购计划
	 * 
	 * @return
	 */
	public String findDqrWgPlanList() {
		if (manualPlan != null) {
			ActionContext.getContext().getSession().put("manualPlan",
					manualPlan);
		} else {
			manualPlan = (ManualOrderPlan) ActionContext.getContext()
					.getSession().get("manualPlan");
		}
		if (startDate != null) {
			ActionContext.getContext().getSession().put("dcgstartDate",
					startDate);
		} else {
			startDate = (String) ActionContext.getContext().getSession().get(
					"dcgstartDate");
		}
		if (endDate != null) {
			ActionContext.getContext().getSession().put("dcgendDate", endDate);
		} else {
			endDate = (String) ActionContext.getContext().getSession().get(
					"dcgendDate");
		}
		// if (wgTypes != null && wgTypes.length() > 0) {
		// ActionContext.getContext().getSession().put("cgwgTypes", wgTypes);
		// } else {
		// wgTypes = (String) ActionContext.getContext().getSession().get(
		// "cgwgTypes");
		// }
		if (count != null) {
			ActionContext.getContext().getSession().put("count", count);
		} else {
			count = (String) ActionContext.getContext().getSession().get(
					"count");
		}

		if (manualPlan == null) {
			manualPlan = new ManualOrderPlan();
		}
		Object[] object = wwpServer.findDqrWgPlanList(manualPlan, Integer
				.parseInt(cpage), pageSize, startDate, endDate, pageStatus,
				manualPlan.getWgType(), count, null);
		if (object != null && object.length > 0) {
			errorMessage = (String) object[0];
			if("true".equals(errorMessage)){
				mopList = (List<ManualOrderPlan>) object[1];
				pageSize = (Integer) object[3];
				tag = (String) object[4];
				sumNum = (Float) object[5];
			}else{
				return "error";
			}
						
		}
		if (mopList != null && mopList.size() > 0) {
			int count = (Integer) object[2];
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this
					.setUrl("WaigouwaiweiPlanAction!findDqrWgPlanList.action?pageStatus="
							+ pageStatus);
			errorMessage = null;
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "Procard_WgPlanList1";
	}

	public String deleteWgOrder() {

		return null;
	}

	public String export() {
		String pa = "wldProcard";
		if ("noPrice".equals(noPrice)) {
			pa = "cgprocard";
		}
		if (procard != null) {
			ActionContext.getContext().getSession().put(pa, procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(pa);
		}
		if (procard == null) {
			procard = new Procard();
		}
		Object[] object = null;
		if ("noPrice".equals(noPrice)) {
			object = wwpServer.findDqrWgPlanList(manualPlan, 0, 0, startDate,
					endDate, pageStatus, null, null, exportTag);
		} else {
			if (exportTag != null) {
				object = wwpServer.findAllWlDetailList2(id, procard);
			} else {
				object = wwpServer.findAllWlDetailList(id, procard);
			}
			// object = wwpServer.findDqrWlDetailList(id, procard);
		}

		if (object != null && object.length > 0) {
			boolean b = false;
			if ("noPrice".equals(noPrice)) {
				errorMessage =  (String) object[0];
				if("true".equals(errorMessage)){
					mopList = (List<ManualOrderPlan>) object[1];
					b = wwpServer.exportFileMop(mopList);
				}
			} else {
				procardList = (List<Procard>) object[0];
				if (exportTag != null) {
					b = wwpServer.exportFile2(procardList);
				} else {
					b = wwpServer.exportFile1(procardList);
				}

			}

			// if ("noPrice".equals(noPrice)) {
			// b = wwpServer.exportFile1(procardList);
			// } else if ("noPrice".equals(noPrice)) {
			// b = wwpServer.exportFile(procardList);
			// }
			if (b) {
				return "findDqrWgPlanList";
			}
		}
		return "error";
	}

	public String findNOPriceWgPlanList() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("nopriceprocard",
					procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"nopriceprocard");
		}
		if (procard == null) {
			procard = new Procard();
		}
		Object[] object = wwpServer.findDqrWgPlanList(manualPlan, Integer
				.parseInt(cpage), 20, startDate, endDate, "noPrice", null,
				null, null);
		if (object != null && object.length > 0) {
			procardList = (List<Procard>) object[0];
			pageSize = (Integer) object[2];

		}
		if (procardList != null && procardList.size() > 0) {
			int count = (Integer) object[1];
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("WaigouwaiweiPlanAction!findNOPriceWgPlanList.action");
			errorMessage = null;
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "findNOPriceWgPlanList";
	}

	public String exportNoPrice() {
		if (wwpServer.exportNoPrice()) {
			// return "tofindNOPriceWgPlanList";
			return "findDqrWgPlanList";
		}
		return "error";
	}

	/*
	 * 跳转调整页面
	 */
	public String toAdjust() {
		if (procard.getId() != null) {
			procard = wwpServer.findById(procard.getId());
		}
		return "adjustNum";
	}

	/*
	 * 调整采购数量
	 */
	public String adjust() {
		String msg = wwpServer.adjust(procard);
		// if(msg.equals("true")){
		// errorMessage="修改成功!";
		// url =
		// "WaigouwaiweiPlanAction!findDqrWlDetailList.action?id="+procard.getRootId();
		// }else{
		// errorMessage=msg;
		// }
		MKUtil.writeJSON(msg);
		return "error";
	}

	/****
	 * 添加采购订单
	 * 
	 * @param procardIds
	 * @return
	 */
	public String addWgOrder() {
		try {
			errorMessage = wwpServer.addWgOrder(processIds,pageStatus);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
		if ("".equals(errorMessage)) {
			errorMessage = "采购订单添加成功!";
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&tag="
					+ tag;
		}
		return ERROR;
	}

	/****
	 * 外购采购订单查询列表
	 * 
	 * @return
	 */
	public String findWgOrderList() {
		if (waigouOrder != null) {
			ActionContext.getContext().getSession().put("waigouOrder",
					waigouOrder);
		} else {
			waigouOrder = (WaigouOrder) ActionContext.getContext().getSession()
					.get("waigouOrder");
		}
		if (pageStatus == null) {
			pageStatus = "";
		}
		Object[] object = wwpServer.findWgOrderList(waigouOrder, Integer
				.parseInt(cpage), pageSize, pageStatus, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus="
								+ pageStatus
								+ "&tag="
								+ tag
								+ "&noOperation="
								+ noOperation);
				this.setTag(tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgOrderList";
	}

	/****
	 * 外购采购订单查询列表(申请预付款单)
	 * 
	 * @return
	 */
	public String findWgOrderListAppli() {
		if (waigouOrder != null) {
			ActionContext.getContext().getSession().put("waigouOrders",
					waigouOrder);
		} else {
			waigouOrder = (WaigouOrder) ActionContext.getContext().getSession()
					.get("waigouOrders");
		}
		if (pageStatus == null) {
			pageStatus = "";
		}
		Object[] object = wwpServer.findWgOrderList(waigouOrder, Integer
				.parseInt(cpage), pageSize, pageStatus, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findWgOrderListAppli.action?pageStatus="
								+ pageStatus + "&tag=" + tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgOrderApplList";
	}

	public String exportWgOrderList() {
		if (waigouOrder != null) {
			ActionContext.getContext().getSession().put("waigouOrder",
					waigouOrder);
		} else {
			waigouOrder = (WaigouOrder) ActionContext.getContext().getSession()
					.get("waigouOrder");
		}
		if (pageStatus == null) {
			pageStatus = "";
		}
		Object[] object = wwpServer.findWgOrderList_export(waigouOrder, 0, 0,
				"findAll", tag);
		boolean b = false;
		if (object != null && object.length > 0) {
			waigouOrderList = (List) object[0];
			b = wwpServer.exportWgOrderList(waigouOrderList);
		}
		if (b) {
			return "Procard_WgOrderList";
		} else {
			return "error";
		}
	}

	/****
	 * 外委采购订单查询列表
	 * 
	 * @return
	 */
	public String findWwOrderList() {
		if (waigouOrder != null) {
			ActionContext.getContext().getSession().put("waigouOrder",
					waigouOrder);
		} else {
			waigouOrder = (WaigouOrder) ActionContext.getContext().getSession()
					.get("waigouOrder");
		}
		if (pageStatus == null || "".equals(pageStatus)) {
			pageStatus = "findAll";
		}
		Object[] object = wwpServer.findWwOrderList(waigouOrder, Integer
				.parseInt(cpage), pageSize, pageStatus, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (Integer) object[2];
				this.setTotal(pageCount + "");

				this
						.setUrl("WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WwOrderList";
	}

	/**
	 * 外委申请审批
	 * 
	 * @return
	 */
	public String applyWWorder() {
		String msg = wwpServer.applyWWorder(id);
		// WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&cpage=2&total=2
		if (msg.equals("true1")) {
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&cpage="
					+ cpage + "&tag=" + tag;
			errorMessage = "订单已申请审批,请等待审批完成后再通知供应商!";
		} else if (msg.equals("true2")) {
			url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dsq&cpage="
					+ cpage + "&tag=" + tag;
			errorMessage = "订单已申请审批,请等待审批完成后再通知供应商!";
		} else {
			errorMessage = msg;
		}
		return ERROR;
	}

	/**
	 * 外委批量申请审批
	 * 
	 * @return
	 */
	public String applyordersq() {
		String msg = wwpServer.applyWWorder(processIds);
		// WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&cpage=2&total=2
		if (msg.equals("true1")) {
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&cpage="
					+ cpage;
			errorMessage = "订单已申请审批,请等待审批完成后再通知供应商!";
		} else if (msg.equals("true2")) {
			url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dsq&cpage="
					+ cpage;
			errorMessage = "订单已申请审批,请等待审批完成后再通知供应商!";
		} else {
			errorMessage = msg;
		}
		// url =
		// "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dsq&cpage="
		// + cpage;
		return ERROR;
	}

	/***
	 * 外购采购明细查询
	 * 
	 * @return
	 */
	public String findWgPlanList() {
		if (waigouPlan != null) {
			ActionContext.getContext().getSession().put("waigouPlan",
					waigouPlan);
		} else {
			waigouPlan = (WaigouPlan) ActionContext.getContext().getSession()
					.get("waigouPlan");
		}
		if (planNumber != null) {
			ActionContext.getContext().getSession().put("planNumber",
					planNumber);
		} else {
			planNumber = (String) ActionContext.getContext().getSession().get(
					"planNumber");
		}

		Object[] object = wwpServer.findWgWwPlanList(waigouPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, id, tag, planNumber);
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouPlan>) object[0];
			if (object[2] != null) {
				waigouOrder2 = (WaigouOrder) object[2];
			}
			sumNum = (Float) object[3];
			sumMoney = (Double) object[4];
			sumbhsprice = (Double) object[5];
			sumbdhNUmber = (Float) object[6];
			sumwshNum = (Float) object[7];
			Users user = Util.getLoginUser();
			if (user != null) {
				strList = wareHouseAuthService.getDdPrice(user.getCode());
				strList1 = wareHouseAuthService.getPrice(user.getCode());
				if (pageStatus != null && pageStatus.indexOf("gys") >= 0) {
					strList = new ArrayList<String>();
					strList.add("gys");
					strList1.add("gys");
				}
			}
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				if (id == null || id == 0) {
					this
							.setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus="
									+ pageStatus
									+ "&noOperation="
									+ noOperation
									+"&tag="+tag);

				} else {
					this
							.setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?id="
									+ id
									+ "&pageStatus="
									+ pageStatus
									+ "&noOperation=" + noOperation
									+"&tag="+tag);
				}
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgOrderPlanList";
	}

	/***
	 * 外委采购明细查询
	 * 
	 * @return
	 */
	public String findWwPlanList() {
		if (waigouPlan != null) {
			ActionContext.getContext().getSession().put("waiweiPlan",
					waigouPlan);
		} else {
			waigouPlan = (WaigouPlan) ActionContext.getContext().getSession()
					.get("waiweiPlan");
		}
		if (startDate != null && startDate.length() > 0) {
			ActionContext.getContext().getSession().put("wwdstartDate",
					startDate);
		} else {
			startDate = (String) ActionContext.getContext().getSession().get(
					"wwdstartDate");
		}
		
		if (endDate != null && endDate.length() > 0) {
			ActionContext.getContext().getSession().put("wwdendDate", endDate);
		} else {
			endDate = (String) ActionContext.getContext().getSession().get(
			"wwdstendDate");
		}
		if (sunxu != null && sunxu.length() > 0) {
			ActionContext.getContext().getSession().put("wpsunxu", sunxu);
		} else {
			sunxu = (String) ActionContext.getContext().getSession().get(
					"wpsunxu");
		}
		Object[] object = wwpServer.findWwPlanList(waigouPlan, Integer
				.parseInt(cpage), pageSize, pageStatus, startDate, endDate, id,sunxu);
		if (object != null && object.length > 0) {
			Users user = Util.getLoginUser();
			if (user != null) {
				strList = wareHouseAuthService.getDdPrice(user.getCode());
				strList1 = wareHouseAuthService.getPrice(user.getCode());
				if (pageStatus != null && pageStatus.indexOf("gys") >= 0) {
					strList = new ArrayList<String>();
					strList.add("gys");
					strList1.add("gys");
				}
			}
			wwPlanList = (List<WaigouPlan>) object[0];
			waigouOrder = (WaigouOrder) object[2];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				if (id != null && id > 0) {
					this.setTotal(1 + "");
				} else {
					int pageCount = (count + pageSize - 1) / pageSize;
					this.setTotal(pageCount + "");
				}
				if (id != null) {
					this
							.setUrl("WaigouwaiweiPlanAction!findWwPlanList.action?id="
									+ id + "&pageStatus=" + pageStatus);
					errorMessage = null;
				} else {
					this
							.setUrl("WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus="
									+ pageStatus);
					errorMessage = null;
				}
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WwOrderPlanList";
	}

	/**
	 * 同意设变取消外委
	 * 
	 * @return
	 */
	public String agreeSbquxiaoww() {
		errorMessage = wwpServer.agreeSbquxiaoww(id);
		if ("true".equals(errorMessage)) {
			errorMessage = "取消成功!";
			url = "WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=gyssbdqr";
		}
		return ERROR;
	}

	/****
	 * 采购订单通知供应商
	 * 
	 */
	public String orderToTzGys() {
		try {
			errorMessage = wwpServer.orderToTzGys(processIds);
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
			return ERROR;
		}
		if ("".equals(errorMessage)) {
			errorMessage = "通知供应商成功!";
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dtz&tag="
					+ tag;
		}
		return ERROR;
	}

	/****
	 * 外委采购订单通知供应商
	 * 
	 */
	public String wworderToTzGys() {
		errorMessage = wwpServer.orderToTzGys(processIds);
		if ("".equals(errorMessage)) {
			errorMessage = "通知供应商成功!";
			url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dtz&tag="
					+ tag;
		}
		return ERROR;
	}

	/****
	 * 采购订单协商交付
	 * 
	 */
	public void gysXsOrder() {
		errorMessage = wwpServer.gysXsOrder(waigouPlan);
		if ("true".equals(errorMessage)) {
			errorMessage = "确认成功!";
			// if ("外购".equals(waigouPlan.getType()))
			// url = "WaigouwaiweiPlanAction!findWgPlanList.action?id=" + id
			// + "&pageStatus=" + pageStatus;
			// else if ("外委".equals(waigouPlan.getType()))
			// url = "WaigouwaiweiPlanAction!findWwPlanList.action?id=" + id
			// + "&pageStatus=" + pageStatus;
		}
		MKUtil.writeJSON(errorMessage);
		// return ERROR;
	}

	/****
	 * 采购订单最终确认
	 * 
	 * @param ids
	 * @return
	 */
	public String orderQueren() {
		errorMessage = wwpServer.orderQueren(processIds);
		if ("wg".equals(pageStatus))
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dqr&tag+"
					+ tag;
		else if ("ww".equals(pageStatus))
			url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dqr&tag+"
					+ tag;
		return ERROR;
	}

	/****
	 * 采购订单最终确认
	 * 
	 * @param ids
	 * @return
	 */
	public String auditOrder() {
		errorMessage = wwpServer.auditOrder(processIds, tag);
		url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=audit&tag+"
				+ tag;
		return ERROR;
	}

	/****
	 * 外委采购订单最终确认
	 * 
	 * @param ids
	 * @return
	 */
	public String wworderQueren() {
		errorMessage = wwpServer.orderQueren(processIds);
		url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dqr&tag+"
				+ tag;
		return ERROR;
	}

	/**
	 * 供应商查看图纸
	 * 
	 * @return
	 */
	public String gysTzview0() {
		list = wwpServer.findCardTemplateTz(id);
		return "ProcardTemplateGysTzs";
	}
	/**
	 * 供应商查看图纸
	 * 
	 * @return
	 */
	public String gysTzview() {
		list = wwpServer.findCardTemplateTz(waigouPlan.getMarkId(), waigouPlan
				.getBanci());
		return "ProcardTemplateGysTzs";
	}

	/**
	 * 供应商查看图纸
	 * 
	 * @return
	 */
	public String gysTzview2() {
		list = wwpServer.findGyswwPlantz(id, pageStatus);
		if ("noshow".equals(pageStatus)) {
			pageStatus = "gysTzview2";
		}
		return "ProcardTemplateGysTzs";
	}

	public String gysTzview3() {
		waigouPlan = wwpServer.findwaiddfordId(id);
		list = wwpServer.findGyswwPlantz(waigouPlan.getId(), pageStatus);
		if ("noshow".equals(pageStatus)) {
			pageStatus = "gysTzview3";
		}
		return "ProcardTemplateGysTzs";
	}

	/***
	 * 供应商待送货列表
	 * 
	 * @return
	 */
	public String findFllScz() {
		// pageSize = 500;
		if (waigouPlan != null) {
			ActionContext.getContext().getSession().put("waigouPlanSh",
					waigouPlan);
		} else {
			waigouPlan = (WaigouPlan) ActionContext.getContext().getSession()
					.get("waigouPlanSh");
		}
		Object[] object = wwpServer.findFllScz(waigouPlan, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			wwPlanList = (List<WaigouPlan>) object[0];
			if (wwPlanList != null && wwPlanList.size() > 0) {
				int count = (Integer) object[1];
				// pageSize = (Integer) object[2];
				sumMoney = (Double) object[3];
				sumNum = (Float) object[4];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findFllScz.action?pageSize="
								+ pageSize);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		if ("sgsh".equals(pageStatus)) {
			this.setUrl("WaigouwaiweiPlanAction!findFllScz.action?pageStatus="
					+ pageStatus + "&pageSize=" + pageSize);
			errorMessage = null;
			if (wwPlanList == null || wwPlanList.size() == 0) {
				errorMessage = "未找到您查询订单信息!请检查后重试!";
			}
			return "Procard_WgPlanShList_sg";
		} else
			return "Procard_WgPlanShList";
	}

	/****
	 * 供应商送货单申请
	 * 
	 * @return
	 */
	public String orderToSonghuo() {
		Object[] object = wwpServer.addOrderToSonghuo(processIds);
		waigouDelivery = (WaigouDelivery) object[0];
		list = (List) object[1];
		errorMessage = (String) object[2];
		if (!"true".equals(errorMessage)) {
			url = "WaigouwaiweiPlanAction!findFllScz.action";
			return "error";
		}
		return "Procard_WgPlanShSqList";
	}

	/****
	 * 仓库添加手工送货单并确认数量
	 * 
	 * @return
	 */
	public String orderToshAndQs() {
		try {
			errorMessage = wwpServer.orderToshAndQs(processIds, processNumbers);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			errorMessage = e.toString();
		}
		return "Procard_WgPlanShList_sg";
	}

	/***
	 * 供应商添加送货单
	 * 
	 * @return
	 */
	public String addDeliveryNote() {
		try {
			Map<Integer, Object> map = wwpServer.addDeliveryNote(
					waigouDelivery, list_wdd);
			if (map != null) {
				if (map.get(0) != null) {
					String msg = map.get(0).toString();
					if (msg.equals("true")) {
						String numbers = map.get(1).toString();
						errorMessage = "送货单添加成功!";
						url = "WaigouwaiweiPlanAction!findDeliveryByNumbers.action?bacode="
								+ numbers;
						// url =
						// "WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
						// + waigouDelivery.getId();
					} else {
						errorMessage = msg;
						url = "WaigouwaiweiPlanAction!findFllScz.action";
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			errorMessage = e.toString();
			errorMessage = errorMessage.replaceAll(
					"java.lang.RuntimeException:", "");
			url = "WaigouwaiweiPlanAction!findFllScz.action";
		}
		return ERROR;
	}

	public String findDeliveryByNumbers() {
		list = wwpServer.findDeliveryByNumbers(bacode);
		return "Procard_WgDeliveryAndDetailList";
	}

	/***
	 * 送货单查询(所有/供应商/待打印/待确认)
	 * 
	 * @return
	 */
	public String findDeliveryNote() {
		if (waigouDelivery != null) {
			ActionContext.getContext().getSession().put("waigouDelivery",
					waigouDelivery);
		} else {
			waigouDelivery = (WaigouDelivery) ActionContext.getContext()
					.getSession().get("waigouDelivery");
		}
		Object[] object = wwpServer.findDeliveryNote(waigouDelivery, Integer
				.parseInt(cpage), pageSize, pageStatus, firsttime, endtime);
		if (object != null && object.length > 0) {
			wwPlanList = (List) object[2];// 待打印送货单
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findDeliveryNote.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgDeliveryList";
	}

	/***
	 * 送货单用户查询
	 * 
	 * @return
	 */
	public String findDeliveryNoteByUser() {
		if (waigouDelivery != null) {
			ActionContext.getContext().getSession().put("waigouDelivery",
					waigouDelivery);
		} else {
			waigouDelivery = (WaigouDelivery) ActionContext.getContext()
					.getSession().get("waigouDelivery");
		}
		Object[] object = wwpServer.findDeliveryNoteByUser(waigouDelivery,
				Integer.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			wwPlanList = (List) object[2];// 待打印送货单
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findDeliveryNoteByUser.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_WgDeliveryList1";
	}

	/***
	 * 送货单合格率查询
	 * 
	 * @return
	 */
	public String findDeliveryNoteForhege() {
		if (waigouDelivery != null) {
			ActionContext.getContext().getSession().put("waigouDelivery",
					waigouDelivery);
		} else {
			waigouDelivery = (WaigouDelivery) ActionContext.getContext()
					.getSession().get("waigouDelivery");
		}
		Object[] object = wwpServer.findDeliveryNoteByUser(waigouDelivery,
				Integer.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			wwPlanList = (List) object[2];// 待打印送货单
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findDeliveryNoteByUser.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "WgDeliveryList_hege";
	}

	/***
	 * 送货单明细查询
	 * 
	 * @return
	 */
	public String findDeliveryNoteDetail() {
		Object[] object = wwpServer.findDeliveryNoteDetail(id);
		if (object != null && object.length > 0) {
			waigouDelivery = (WaigouDelivery) object[0];
			list = (List) object[1];
			sumNum = (Float) object[2];
			sumbdhNUmber = (Float) object[3];
			sumwshNum = (Float) object[4];
			tag = object[5].toString();
			if (waigouDelivery == null) {
				errorMessage = "没有找到你要查询的送货单明细,请检查后重试!";
				return ERROR;
			}
		}
		return "Procard_WgDeliveryDetail";
	}

	/***
	 * 送货单明细查询合格页面
	 * 
	 * @return
	 */
	public String DeliveryNoteDetailforhege() {
		Object[] object = wwpServer.findDeliveryNoteDetail(id);
		if (object != null && object.length > 0) {
			waigouDelivery = (WaigouDelivery) object[0];
			list = (List) object[1];
			sumNum = (Float) object[2];
			sumbdhNUmber = (Float) object[3];
			sumwshNum = (Float) object[4];
			if (waigouDelivery == null) {
				errorMessage = "没有找到你要查询的送货单明细,请检查后重试!";
				return ERROR;
			}
		}
		return "WgDeliveryDetail_hege";
	}

	/***
	 * 送货单打印页面
	 * 
	 * @return
	 */
	public String findDNDetailForPrint() {
		Object[] object = wwpServer.findDeliveryNoteDetail(id);
		if (object != null && object.length > 0) {
			waigouDelivery = (WaigouDelivery) object[0];
			list = (List) object[1];
			sumNum = (Float) object[2];
			sumbdhNUmber = (Float) object[3];
			sumwshNum = (Float) object[4];
			tag = object[5].toString();

			if (waigouDelivery == null) {
				errorMessage = "没有找到你要查询的送货单明细,请检查后重试!";
				return ERROR;
			}
		}
		return "Procard_WgDeliveryPrint";
	}

	/***
	 * 查询采购订单对应送货明细
	 * 
	 * @param waigouDelivery
	 * 
	 * 
	 * @return
	 */
	public String findWaigouPlanDNDetail() {
		Object[] object = wwpServer.findWaigouPlanDNDetail(id);
		if (object != null && object.length > 0) {
			waigouPlan = (WaigouPlan) object[0];
			list = (List) object[1];
		}
		return "Procard_WgPlanDeliveryDetail";
	}

	/****
	 * 送货单打印
	 * 
	 * @return
	 */
	public String updateDeliveryToPrint() {
		errorMessage = wwpServer.updateDeliveryToPrint(id);
		if ("".equals(errorMessage)) {
			errorMessage = "送货单打印完成!";
			url = "WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
					+ id;
		}
		return ERROR;
	}

	public void updateDeliveryToPrintajax() {
		try {
			errorMessage = wwpServer.updateDeliveryToPrint(id);
			if ("".equals(errorMessage)) {
				errorMessage = "送货单打印完成!";
			}
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}

	}

	/****
	 * 查询所有待存柜的送货单
	 * 
	 * @return
	 */
	public String findDqrDeliRuGui() {
		list = wwpServer.findDqrDeliRuGui();
		return "Procard_WgRuGuiDjyList";
	}

	/****
	 * 查询所有待确认的送货单
	 * 
	 * @return
	 */
	public String findDqrDelivery() {
		Object[] object = wwpServer.findDqrDelivery(pageStatus, waigouDelivery,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findDqrDelivery.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有对应送货单!";
			}
		}
		return "Procard_WgDeliveryDqrList";
	}

	/****
	 * 查询所有不合格的送货单明细
	 * 
	 * @return
	 */
	public String findNoPassDelivery() {
		list = wwpServer.findNoPassDelivery();
		return "Procard_WgDeliveryNoPassList";
	}

	/****
	 * 扫描条码查询送货单明细
	 * 
	 * @return
	 */
	public String findDeliveryDeByBacode() {
		Object[] object = wwpServer.findDeliveryDeByBacode(bacode);
		if (object != null && object.length > 0) {
			waigouDelivery = (WaigouDelivery) object[0];
			list = (List) object[1];
			if (waigouDelivery == null) {
				errorMessage = "没有找到需要签收的送货单,请检查后重试!";
				return ERROR;
			} else if (!"送货中".equals(waigouDelivery.getStatus())
					&& !"待存柜".equals(waigouDelivery.getStatus())) {
				errorMessage = "抱歉,该送货单状态为:<font color='red'>"
						+ waigouDelivery.getStatus() + "</font>不能够签收。";
				return ERROR;
			}
		} else {
			errorMessage = "没有找到需要签收的送货单,请检查后重试!";
			return ERROR;
		}
		return "Procard_WgDeliveryDqr";
	}

	/****
	 * 存柜扫描库位码存柜=》跳转到确认数量页面
	 * 
	 * @return
	 */
	public String findDeliveryRuGuiBacode() {
		try {
			if (bacode != null && mxId != null) {
				Object[] object = wwpServer.upfindDeliveryRuGuiBacode(bacode,
						mxId);
				if (object != null && object.length > 0) {
					errorMessage = (String) object[0];
					if (!"true".equals(errorMessage)) {
						return ERROR;
					} else {
						list_wdd = (List) object[1];
						warehouseNumber = (WarehouseNumber) object[2];
						if (list_wdd != null && list_wdd.size() > 0) {
							errorMessage = null;
						}
						return "Procard_WgQueRenList";
					}
				}
			}
			return "error";
		} catch (Exception e) {
			errorMessage = e.getMessage();
			return ERROR;
		}
	}

	/**
	 * 查看个人未关闭的库位
	 * 
	 * @return
	 */
	public String findRuGuiwei() {
		warehouseNumbers = wwpServer.findRuGuiWN();
		return "Procard_WgWeiGuanList";
	}

	/**
	 * 已有待存入信息=》确认数量
	 * 
	 * @return
	 */
	public String findRuGuiQueren() {
		if (id != null && id > 0) {
			Object[] object = wwpServer.findRuGuiQuerenCode(id);
			errorMessage = (String) object[0];
			if ("wg".equals(errorMessage)) {
				list_wdd = (List) object[1];
				warehouseNumber = (WarehouseNumber) object[2];
				return "Procard_WgQueRenList";
			} else if ("oa".equals(errorMessage)) {
				oaDList = (List) object[1];
				warehouseNumber = (WarehouseNumber) object[2];
				return "oaAppDetail_GzQueRenList";
			} else if ("rkqr".equals(errorMessage)) {
				waigoudd = (WaigouDeliveryDetail) object[1];
				warehouseNumber = (WarehouseNumber) object[2];
				return "Procard_WgRuKuList";
			}
		}
		return ERROR;
	}

	/****
	 * 送货单签收确认
	 * 
	 * @return
	 */
	public String updateDeliveryToQr() {
		errorMessage = wwpServer.updateDeliveryToQr(list_wdd);
		if ("".equals(errorMessage)) {
			errorMessage = "您好,本次送货单您已确认完成!";
			url = "WaigouwaiweiPlanAction!findDqrDelivery.action?pageStatus="
					+ pageStatus;
		}
		return ERROR;
	}

	public String findhegelv() {
		if ("day".equals(hegeStatus)) {
			if (dayHege != null) {
				ActionContext.getContext().getSession().put("dayHege", dayHege);
			} else {
				dayHege = (DayHege) ActionContext.getContext().getSession()
						.get("dayHege");
			}
			Map<Integer, Object> map = wwpServer.QueryHege("DayHege", Integer
					.parseInt(cpage), pageSize);
			dayHegeList = (List<DayHege>) map.get(1);
			if (dayHegeList != null & dayHegeList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findhegelv.action?hegeStatus="
								+ hegeStatus);
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			return "DayHege";
		} else if ("week".equals(hegeStatus)) {
			if (zhouHege != null) {
				ActionContext.getContext().getSession().put("zhouHege",
						zhouHege);
			} else {
				zhouHege = (ZhouHege) ActionContext.getContext().getSession()
						.get("zhouHege");
			}
			Map<Integer, Object> map = wwpServer.QueryHege("ZhouHege", Integer
					.parseInt(cpage), pageSize);
			zhouList = (List<ZhouHege>) map.get(1);
			if (zhouList != null & zhouList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("WaigouwaiweiPlanAction!findhegelv.action?"
						+ hegeStatus);
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			return "weekHege";

		} else if ("mouth".equals(hegeStatus)) {
			if (mouthHege != null) {
				ActionContext.getContext().getSession().put("mouthHege",
						mouthHege);
			} else {
				mouthHege = (MouthHege) ActionContext.getContext().getSession()
						.get("mouthHege");
			}
			Map<Integer, Object> map = wwpServer.QueryHege("MouthHege", Integer
					.parseInt(cpage), pageSize);
			mouthHegeList = (List<MouthHege>) map.get(1);
			if (mouthHegeList != null & mouthHegeList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("WaigouwaiweiPlanAction!findhegelv.action?"
						+ hegeStatus);
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			return "mouthHege";

		} else if ("quarter".equals(hegeStatus)) {
			if (quarterHege != null) {
				ActionContext.getContext().getSession().put("quarterHege",
						quarterHege);
			} else {
				quarterHege = (QuarterHege) ActionContext.getContext()
						.getSession().get("quarterHege");
			}
			Map<Integer, Object> map = wwpServer.QueryHege("QuarterHege",
					Integer.parseInt(cpage), pageSize);
			quarterHegeList = (List<QuarterHege>) map.get(1);
			if (quarterHegeList != null & quarterHegeList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("WaigouwaiweiPlanAction!findhegelv.action?"
						+ hegeStatus);
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			return "quarterHege";

		} else if ("year".equals(hegeStatus)) {
			if (yearHege != null) {
				ActionContext.getContext().getSession().put("yearHege",
						yearHege);
			} else {
				yearHege = (YearHege) ActionContext.getContext().getSession()
						.get("yearHege");
			}
			Map<Integer, Object> map = wwpServer.QueryHege("YearHege", Integer
					.parseInt(cpage), pageSize);
			yearHegeList = (List<YearHege>) map.get(1);
			if (yearHegeList != null & yearHegeList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("WaigouwaiweiPlanAction!findhegelv.action?"
						+ hegeStatus);
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
			return "yearHege";
		} else {
			errorMessage = "系统异常";
			return "error";
		}

	}

	/****
	 * 入柜=》确认数量
	 * 
	 * @return
	 */
	public String updateWNToRG() {
		if (warehouseNumber == null)
			return ERROR;
		warehouseNumber = wwpServer.byIdWN(warehouseNumber.getId());
		if (warehouseNumber != null) {
			errorMessage = wwpServer.updateWNToRG(list_wdd, warehouseNumber);
			if ("true".equals(errorMessage)) {
				errorMessage = "确认完成!";
				url = "WaigouwaiweiPlanAction!findDqrDeliRuGui.action";
			}
		}
		return ERROR;
	}

	/****
	 * 入库=》 库管确认数量
	 * 
	 * @return
	 */
	public String updateWNToRK() {
		if (warehouseNumber == null)
			return ERROR;
		warehouseNumber = wwpServer.byIdWN(warehouseNumber.getId());
		if (warehouseNumber != null) {
			errorMessage = wwpServer.updateWNToRK(waigoudd, warehouseNumber);
			if ("true".equals(errorMessage)) {
				errorMessage = "确认完成!";
				url = "WaigouwaiweiPlanAction!findAllDrk.action";
			}
		}
		return ERROR;
	}

	/**
	 * 修改采购订单
	 * 
	 * @return
	 */
	public String updateOrderMsg() {
		String msg = wwpServer.updateOrderMsg(waigouOrder, tag);
		if (msg.equals("true")) {
			errorMessage = "修改成功!";
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/****
	 * 查询所有待检验的产品
	 * 
	 * @return
	 */
	public String findDjyDelivery() {
		pageSize = 10;
		if (waigoudd != null) {
			ActionContext.getContext().getSession().put("waigouddj", waigoudd);
		} else {
			waigoudd = (WaigouDeliveryDetail) ActionContext.getContext()
					.getSession().get("waigouddj");
		}
		Object[] object = wwpServer.findDjyDelivery(waigoudd, pageStatus,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findDjyDelivery.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			}
		}
		return "Procard_WgDeliveryDjyList";
	}

	/****
	 * 审批所有不合格的订单
	 * 
	 * @return
	 */
	public String approvalNoPass() {
		waigoudd = wwpServer.getWaiGouddById(waigoudd.getId());
		if (waigoudd == null) {
			errorMessage = "没有找到对应的不合格待检明细!";
			return "error";
		} else {
			errorMessage = wwpServer.approvalNoPass(waigoudd);
			url = "WaigouwaiweiPlanAction!findNoPassDelivery.action";
		}
		return "error";
	}

	/***
	 * 查找检验明细=》跳转到检验页面
	 * 
	 * @return
	 */
	public String toCheckwgww() {
		waigoudd = wwpServer.getWaiGouddById2(id);
		if (waigoudd == null) {
			errorMessage = "没有找到对应的外购件待检明细!";
			return "error";
		}
		if (!"待检验".equals(waigoudd.getStatus())
				&& !"检验中".equals(waigoudd.getStatus())) {
			errorMessage = "该送货单已经检验过，请勿重复检验!";
			return "error";
		}
		if ("外委".equals(waigoudd.getType())) {
			pamList = wwpServer.pamListByProcessName(waigoudd.getProcessName());
		}
		wwpServer.updatejyz(waigoudd);
		xjbzlist = wwpServer.findAllxjbzlist();
		Integer ostId = null;
		if (osTemplate != null && osTemplate.getId() != null
				&& osTemplate.getId() > 0) {
			ostId = osTemplate.getId();
		}
		Object[] obj = wwpServer.getOsTemplate(waigoudd.getMarkId(), waigoudd
				.getBanben(), waigoudd.getWwType(), waigoudd.getProcessNo(),
				waigoudd, ostId);
		if (obj != null && obj.length > 3 && "类型为空".equals(obj[3])) {
			errorMessage = waigoudd.getWwType() + "类型为空，异常数据!请联系管理员";
			return "error";
		}
		osTemplate = (OsTemplate) obj[0];
		// xujianpingci = wwpServer.findxunjianpici(waigoudd,
		// osTemplate);
		list = (List) obj[1];
		ostList = (List<OsTemplate>) obj[2];
		tag = (String) obj[3];
		errorMessage = (String) obj[4];
		sumNum = (Float) obj[5];

		if (osTemplate == null && (ostList == null || ostList.size() == 0)) {
			errorMessage = "没有找到对应的" + waigoudd.getWwType() + "检验模版!";
			return "error";
		}
		return "Procard_checkwgww";
	}

	// 外购件模板绑定巡检标准
	public String OstbdXjbz() {
		errorMessage = pageStatus;
		if (wwpServer.Ostbdxjbz(osTemplate)) {
			return "toCheckwgww";
		}
		return "error";
	}

	/**
	 * 检验=》接收二维码后开门操作
	 */
	public void redTowWeb() {
		if (bacode == null || "".equals(bacode))
			MKUtil.writeJSON(false, "二维码为空，操作失败！", null);
		errorMessage = wwpServer.oaOpenW(bacode, pageStatus);
		if ("true".equals(errorMessage)) {
			MKUtil.writeJSON(true, "操作成功！", null);
		} else {
			MKUtil.writeJSON(false, errorMessage, null);
		}
	}

	/**
	 * 检验关门操作
	 */
	public void oactoWeb() {
		if (id_wn != null && id_wn > 0) {
			warehouseNumber = wwpServer.byIdWN(id_wn);
			if (warehouseNumber != null) {
				errorMessage = wwpServer.oaCloseW(warehouseNumber);
				if ("true".equals(errorMessage)) {
					MKUtil.writeJSON(true, "操作成功！", null);
				} else {
					MKUtil.writeJSON(false, "操作失败！", null);
				}
			}
		}
		MKUtil.writeJSON(false, "库位信息为空，操作失败！", null);
	}

	/**
	 * 检验给屏幕发送二维码信息&&库管确认数量(多个库位)
	 */
	public void sendTow() {
		if (id_wn == null || id_wn <= 0 || id_wdd == null || id_wdd <= 0)
			MKUtil.writeJSON(false, "库位或明细信息为空，发送失败！", null);
		warehouseNumber = wwpServer.byIdWN(id_wn);
		if (warehouseNumber != null) {
			errorMessage = wwpServer.sendTow(warehouseNumber, id_wdd);
			if ("true".equals(errorMessage))
				MKUtil.writeJSON(true, "发送成功", null);
			else
				MKUtil.writeJSON(false, errorMessage, null);
		}
		MKUtil.writeJSON(false, "库位信息为空，发送失败！", null);
	}

	/**
	 * 库管确认给屏幕发送二维码
	 */
	public void querensendTow() {
		if (id_wdd == null || id_wdd <= 0)
			MKUtil.writeJSON(false, "送货单明细信息为空，发送失败！", null);
		waigoudd = wwpServer.findWaigouPlanById(id_wdd);
		if (waigoudd == null)
			MKUtil.writeJSON(false, "送货单明细信息为空，发送失败！", null);
		errorMessage = wwpServer.queRenSendTow(waigoudd.getId());
		if ("true".equals(errorMessage))
			MKUtil.writeJSON(true, errorMessage, null);
		else
			MKUtil.writeJSON(false, errorMessage, null);
	}

	/**
	 * 库管确认扫描二维码
	 */
	public void querenKu() {
		if (id_wdd == null || id_wdd <= 0)
			MKUtil.writeJSON(false, "送货单明细信息为空，发送失败！", null);
		waigoudd = wwpServer.findWaigouPlanById_1(id_wdd);
		if (waigoudd == null)
			MKUtil.writeJSON(false, "送货单明细信息为空，发送失败！", null);
		else
			MKUtil.writeJSON(true, errorMessage, null);
	}

	/**
	 * 库管确认扫描二维码
	 */
	public void querenflKu() {
		if (id_wdd == null || id_wdd <= 0)
			MKUtil.writeJSON(false, "采购单明细信息为空，发送失败！", null);
		waigoudd = wwpServer.findWaigouPlanById_2(id_wdd);
		if (waigoudd == null)
			MKUtil.writeJSON(false, "采购单明细信息为空，发送失败！", null);
		else
			MKUtil.writeJSON(true, errorMessage, null);
	}

	/**
	 * 库位无IP直接入库
	 * 
	 * @return
	 */
	public String querenKuBacode() {
		// try {
		if (bacode != null && (id != null || mxId != null)) {

			errorMessage = wwpServer.querenKu(bacode, id, mxId,checkTimes);
			if ("true".equals(errorMessage)) {
				errorMessage = "入库成功！";
			}
				url = "WaigouwaiweiPlanAction!findAllDrk.action?pageStatus="
					+ pageStatus + "&tag=" + tag + "&cpage=" + cpage;
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// errorMessage = e.getMessage();
		// }
		return ERROR;
	}
	public void querenKuBacode_ajax() {
		// try {
		if (bacode != null && (id != null || mxId != null)) {
			try {
				errorMessage = wwpServer.querenKu(bacode, id, mxId,checkTimes);
				MKUtil.writeJSON(errorMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * 库位无IP直接入库(已废弃)
	 * 
	 * @return
	 */
	public String querenKuflBacode() {
		if (bacode != null && id != null) {
			errorMessage = wwpServer.querenflKu(bacode, id);
			if ("true".equals(errorMessage)) {
				url = "WaigouwaiweiPlanAction!findAllflDrk.action?pageStatus="
						+ pageStatus + "&tag=" + tag;
				errorMessage = "入库成功！";
			}
		}
		return ERROR;
	}

	/**
	 * 库管确认入库数量二维码操作
	 */
	public String upRuKuBacode() {
		try {
			if (bacode != null) {
				Object[] object = wwpServer.upRuKuiBacode(bacode);
				if (object != null && object.length > 0) {
					errorMessage = (String) object[0];
					if (!"true".equals(errorMessage))
						return ERROR;
					else {
						waigoudd = (WaigouDeliveryDetail) object[1];
						warehouseNumber = (WarehouseNumber) object[2];
						return "Procard_WgRuKuList";
					}
				}
			}
			return "error";
		} catch (Exception e) {
			errorMessage = e.getMessage();
			return ERROR;
		}
	}

	/**
	 * 借工装取发送二维码
	 */
	public void GZsendTow() {
		if (id == null || id <= 0)
			MKUtil.writeJSON(false, "信息为空，发送失败！", null);
		borrow = wwpServer.byIdBorrow(id);
		if (borrow == null)
			MKUtil.writeJSON(false, "借用信息为空，发送失败！", null);
		if (borrow.getWare_id() == null)
			MKUtil.writeJSON(false, "库位信息有误，发送失败！", null);
		warehouseNumber = wwpServer.byIdWN(borrow.getWare_id());
		if (warehouseNumber == null)
			MKUtil.writeJSON(false, "库位信息为空，发送失败！", null);
		errorMessage = wwpServer.sendTowGz(warehouseNumber, id);
		if ("true".equals(errorMessage))
			MKUtil.writeJSON(true, errorMessage, null);
		else
			MKUtil.writeJSON(false, errorMessage, null);
	}

	/**
	 * 取工装接受二维码操作
	 */
	public String upBorrowGzBacode() {
		if (bacode == null)
			return "error";
		errorMessage = wwpServer.upBorrowGzBacode(bacode);
		if ("true".equals(errorMessage)) {
			errorMessage = "借用成功";
			url = "borrow_showCodeBorrow.action?tag=" + tag;
		}
		return "error";
	}

	// 取工装关闭库位
	public String closeGz() {
		if (id == null || id <= 0)
			MKUtil.writeJSON(false, "信息为空，发送失败！", null);
		borrow = wwpServer.byIdBorrow(id);
		if (borrow == null)
			MKUtil.writeJSON(false, "借用信息为空，发送失败！", null);
		if (borrow.getWare_id() == null)
			MKUtil.writeJSON(false, "库位信息有误，发送失败！", null);
		warehouseNumber = wwpServer.byIdWN(borrow.getWare_id());
		if (warehouseNumber == null)
			MKUtil.writeJSON(false, "库位信息为空，发送失败！", null);
		errorMessage = wwpServer.oaCloseW(warehouseNumber, borrow);
		if ("true".equals(errorMessage)) {
			errorMessage = "关闭成功";
			url = "borrow_showCodeBorrow.action?tag=" + tag;
		}
		return "error";
	}

	// 存工装关闭库位
	public String closeGzAlso() {
		if (id == null || id <= 0)
			MKUtil.writeJSON(false, "信息为空，关门失败！", null);
		also = wwpServer.byIdAlso(id, "存中");
		if (also == null)
			MKUtil.writeJSON(false, "归还信息有误，关门失败！", null);
		if (also.getWare_id() == null)
			MKUtil.writeJSON(false, "库位信息有误，发送失败！", null);
		warehouseNumber = wwpServer.byIdWN(also.getWare_id());
		if (warehouseNumber == null)
			MKUtil.writeJSON(false, "库位信息为空，发送失败！", null);
		errorMessage = wwpServer.oaCloseW(warehouseNumber, also);
		if ("true".equals(errorMessage)) {
			errorMessage = "关闭成功";
			url = "also_showCodeAlso.action?tag=" + tag;
		}
		return "error";
	}

	/**
	 * 
	 * @return
	 */
	public String upAlsoBacode() {
		if (bacode != null && mxId != null) {
			also = wwpServer.byIdAlso(Integer.parseInt(mxId), "待存");
			if (also == null)
				return "归还明细状态有误";
			errorMessage = wwpServer.upAlsoBacode(bacode, also, tag);
			if ("true".equals(errorMessage)) {
				errorMessage = "存入成功";
				url = "also_showCodeAlso.action?tag=" + tag;
			}
		}
		return "error";
	}

	// 多个确认数量页面
	public String showKuWei() {
		if (id_wdd == null || id_wdd <= 0)
			MKUtil.writeJSON(false, "采购明细信息为空，发送失败！", null);
		waigoudd = wwpServer.findWaigouPlanById(id_wdd);
		if (waigoudd == null)
			MKUtil.writeJSON(false, "送货单明细信息为空，发送失败！", null);
		warehouseNumbers = wwpServer.findRuGuiWN(id_wdd);
		return "wgWarehouseNumbers_show";
	}

	/***
	 * 提交检验
	 * 
	 * @return
	 */
	public String checkwgww() {
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get("jianyan_" + osRecord.getWwddId());
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					"jianyan_" + osRecord.getWwddId(),
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在检验该零件,请勿重复点击检验或刷新后重试!");
		} else {
			return ERROR;
		}
		try {
			String msg = wwpServer.updateStick2(osRecord, osRecordList,
					pageStatus, sumNum, buhegeNumber, bhgosRecordList);
			ActionContext.getContext().getApplication().put(
					"jianyan_" + osRecord.getWwddId(), null);
			if (msg.equals("true") || "待分检".equals(msg)) {
				errorMessage = "检验完成!";
				url = "WaigouwaiweiPlanAction!findDjyDelivery.action?pageStatus="
						+ pageStatus + "&cpage=" + cpage;
			} else if ("待复检".equals(msg)) {
				id = osRecord.getWwddId();
				return "toCheckwgww";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
			ActionContext.getContext().getApplication().put(
					"jianyan_" + osRecord.getWwddId(), null);
		}
		return "error";
	}

	/**
	 * 展示外委待领料状态(废弃)
	 * 
	 * @return
	 */
	public String showWWRuku() {
		if (waigoudd != null) {
			ActionContext.getContext().getSession().put("waigouddshow",
					waigoudd);
		} else {
			waigoudd = (WaigouDeliveryDetail) ActionContext.getContext()
					.getSession().get("waigouddshow");
		}
		Object[] object = wwpServer.findWwRukuDailingList(waigoudd, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			list_wdd = (List) object[0];
			if (list_wdd != null && list_wdd.size() > 0) {
				sumMoney = (Double) object[2];
				sumNum = (Float) object[3];
				sumbhsprice = (Double) object[4];
				hegeNumber = (Float) object[5];
				goodsCount = (Float) object[6];
				// int count = (Integer) object[1];
				// int pageCount = (count + pageSize - 1) / pageSize;
				// this.setTotal(pageCount + "");
				// this
				// .setUrl("WaigouwaiweiPlanAction!showWWRuku.action?pageStatus="
				// + pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}

		return "Procard_showWWLingliao";
	}

	public String showWWdailing() {
		if (waigoudd != null) {
			ActionContext.getContext().getSession().put("waigouddshow",
					waigoudd);
		} else {
			waigoudd = (WaigouDeliveryDetail) ActionContext.getContext()
					.getSession().get("waigouddshow");
		}
		Object[] object = wwpServer.findWwRukuDailingList2(waigoudd, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			list_wdd = (List) object[0];
			if (list_wdd != null && list_wdd.size() > 0) {
				sumMoney = (Double) object[2];
				sumNum = (Float) object[3];
				sumbhsprice = (Double) object[4];
				hegeNumber = (Float) object[5];
				goodsCount = (Float) object[6];
				// int count = (Integer) object[1];
				// int pageCount = (count + pageSize - 1) / pageSize;
				// this.setTotal(pageCount + "");
				// this
				// .setUrl("WaigouwaiweiPlanAction!showWWdailing.action?pageStatus="
				// + pageStatus);
				// errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		// return "Procard_showWWLingliao";
		return "Procard_showWWdailing";
	}

	// 送货中之后先放进智能仓库;

	public String sqrk() {
		if (id != null && id > 0) {
			waigoudd = wwpServer.findWaigouPlanById(id);
			waigoudd.setClassNames(waigoudd.getClassNames());
			wnList = wwpServer.findAllWNList_1();
			return "warehouseApplication_add1";
		}
		return ERROR;
	}

	// 订单明细打印前Action;
	public String findWaigouPlanListByid() {
		Object[] obj = wwpServer.findWaigouPlanListByid(id);
		if (obj != null) {
			waigouOrder = (WaigouOrder) obj[0];
			wwPlanList = (List<WaigouPlan>) obj[1];
			sumMoney = (Double) obj[2];
			zhUser = (ZhUser) obj[3];
			if ("waigou".equals(pageStatus)) {
				return "waigouOrderprint";
			} else if ("waiwei".equals(pageStatus)) {
				return "waiweiOrderprint";
			}
		}
		return ERROR;

	}

	public String findZaiTuwl() {
		if (procard != null) {
			ActionContext.getContext().getSession().put("procardzt", procard);
		} else {
			procard = (Procard) ActionContext.getContext().getSession().get(
					"procardzt");
		}
		Object[] object = wwpServer.findZaiTuwl(procard, Integer
				.parseInt(cpage), pageSize, pageStatus);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("WaigouwaiweiPlanAction!findZaiTuwl.action");
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}

		return "Procard_showZaiTuwl";
	}

	/**
	 * 跳往选择材质类型
	 * 
	 * @return
	 */
	public String toselectWgType() {
		list = wwpServer.getAllWgType();
		return "waigouwaiwei_wgTypelist";

	}

	/**
	 * 修改供应商
	 */
	public String toxiugaigys() {
		waigouPlan = wwpServer.getWaigouPlanById(id);
		list = wwpServer.findwgListBymarkId(waigouPlan.getMarkId());
		// list = wwpServer.findchangeOrderListByplanId(id);
		// listAll = wwpServer.findPrice(waigouPlan.getMarkId(), waigouPlan
		// .getKgliao(), waigouPlan.getBanben());
		listAll = wwpServer.findPriceBywaigouuPlanId(id);
		return "Procard_xiugaigys";
	}

	/**
	 * 修改订单明细的供应商
	 * 
	 * @return
	 */
	public String xiugaimxgys() {
		errorMessage = wwpServer.xiugaimxgys(id, id2, waigouOrder);
		return "Procard_xiugaigys";
	}

	public String xiugaigys() {// 
		errorMessage = "修改失败";
		boolean bool = wwpServer.xiugaigys(id, id2);
		if (bool) {
			errorMessage = "修改成功";
		}
		return "Procard_xiugaigys";
	}

	/**
	 * 根据送货单Id查询所有相对应的库位
	 * 
	 * @return
	 */
	public void findWNBywaigouddId() {
		try {
			List<KuweiSongHuoDan> khList = wwpServer.findAllWNList_2(id,
					pageStatus);
			if (khList != null && khList.size() > 0) {
				MKUtil.writeJSON(khList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查出所有检验中的送货单明细用于大屏幕显示;
	 * 
	 * @return
	 */
	public String findAlljyz() {
		pageSize = 1;
		Object[] object = wwpServer.findAlljyz(Integer.parseInt(cpage),
				pageSize);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				errorMessage = null;
			} else {
				this.setTotal("1");
				errorMessage = "当前没有待检验件号!";
			}
		}
		// list = wwpServer.findAlljyz();
		return "Procard_showjyz";
	}

	/**
	 * 查出所有待入库的送货单明细;
	 * 
	 * @return
	 */
	public String findAllDrk() {
		if (waigoudd != null) {
			ActionContext.getContext().getSession().put("waigoudd", waigoudd);
		} else {
			waigoudd = (WaigouDeliveryDetail) ActionContext.getContext()
					.getSession().get("waigoudd");
		}
		Object[] object = wwpServer.findAllDrk(waigoudd, pageStatus, Integer
				.parseInt(cpage), pageSize, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findAllDrk.action?pageStatus="
								+ pageStatus + "&tag=" + tag);
				errorMessage = null;
			}
		}
		return "Procard_showdrk";
	}

	/**
	 * 根据id得到库位
	 * 
	 * @return
	 */
	public void byIdWN() {
		try {
			warehouseNumber = wwpServer.byIdWN(id);
			if (warehouseNumber != null) {
				MKUtil.writeJSON(warehouseNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * 交付日期整体更新
	 */
	public String allUpdateJiaoFuTime() {
		if (waigouPlan != null && waigouPlan.getJiaofuTime() != null
				&& waigouPlan.getJiaofuTime().length() > 0) {

			errorMessage = wwpServer.allUpdateJiaoFuTime(id, waigouPlan
					.getJiaofuTime());
			if (tag.equals("ww")) {
				this.setUrl("WaigouwaiweiPlanAction!findWwPlanList.action?id="
						+ id + "&pageStatus=" + pageStatus);
			} else {
				this.setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?id="
						+ id + "&pageStatus=" + pageStatus);
			}
		} else {
			errorMessage = "请先填写统一交付日期后再次提交!";
		}
		return "error";
	}

	/**
	 * 多条确认交期
	 */
	public void querenjiaoqiAll() {
		String msg = wwpServer.querenjiaoqiAll(wwPlanList);
		MKUtil.writeJSON(msg);

	}

	public String finwwOrderwl() {
		Map<Integer, Object> map = wwpServer.getWwclDetail(barCode, id);
		if (map != null) {
			if (map.get(1) != null) {
				waigouOrder = (WaigouOrder) map.get(1);
			}
			if (map.get(2) != null) {
				goodsList = (List<Goods>) map.get(2);
			}
			if (map.get(3) != null) {
				Boolean b = (Boolean) map.get(3);
				if (b != null && b) {
					tag = "true";
				} else {
					tag = "false";
				}
			}
			if (map.get(4) != null) {
				listSell = (List<Sell>) map.get(4);
			}
		}
		return "Procard_finwwOrderwl";
	}

	// 确认送货单;
	public String querenDelivery() {
		errorMessage = wwpServer.querenDelivery(id, list_wdd);
		if ("true".equals(errorMessage)) {
			errorMessage = "确认成功!";
			setUrl("WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
					+ id);
		} else {
			errorMessage = "确认失败!";
		}
		return "error";
	}

	// 刷新价格;

	public String shuaixinPrice() {
		errorMessage = wwpServer.shuaixinPrice(id);
		if (!"提交失败".equals(errorMessage)) {
			if ("ww".equals(tag)) {
				this.setUrl("WaigouwaiweiPlanAction!findWwPlanList.action?id="
						+ waigouOrder.getId() + "&pageStatus=" + pageStatus);
			} else {
				this.setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?id="
						+ waigouOrder.getId() + "&pageStatus=" + pageStatus);
			}

		} else {
			errorMessage = "刷新价格失败!";
		}
		return "error";
	}

	public String updatePricefoeshenpi() {
		Object[] o = wwpServer.findwaigouplanAndNewPrice(id);
		waigouPlan = (WaigouPlan) o[0];
		waigouOrder = waigouPlan.getWaigouOrder();
		price = (Price) o[1];
		return "updatePrice_page";
	}

	// 删除采购订单
	public String delWaigouOrder() {
		errorMessage = "删除失败!";
		if (wwpServer.delWaigouOrder(waigouOrder)) {
			errorMessage = "删除成功!";
		}
		setUrl("WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&cpage="
				+ cpage + "&tag=" + tag);
		return "error";
	}

	// 查询所有送货单明细
	public String findAllWaigouDeliveryDetail() {
		if (waigoudd != null) {
			ActionContext.getContext().getSession().put("waigoudd", waigoudd);
		} else {
			waigoudd = (WaigouDeliveryDetail) ActionContext.getContext()
					.getSession().get("waigoudd");
		}
		if (firsttime != null) {
			ActionContext.getContext().getSession().put("firsttime", firsttime);
		} else {
			firsttime = (String) ActionContext.getContext().getSession().get(
					"firsttime");
		}
		if (endtime != null) {
			ActionContext.getContext().getSession().put("endtime", endtime);
		} else {
			endtime = (String) ActionContext.getContext().getSession().get(
					"endtime");
		}
		Object[] object = wwpServer.findAllWaigouDeliveryDetail(waigoudd,
				Integer.parseInt(cpage), pageSize, firsttime, endtime,
				pageStatus, tag);
		if (object != null && object.length > 0) {
			list_wdd = (List) object[0];
			if (list_wdd != null && list_wdd.size() > 0) {
				int count = (Integer) object[1];
				sumNum = (Float) object[2];
				sumbdhNUmber = (Float) object[3];
				sumwshNum = (Float) object[4];
				cqList = (List) object[5];
				pageSize = (Integer) object[6];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findAllWaigouDeliveryDetail.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "Procard_waigoudd";

	}

	// 导出送货单;
	public String exportExcelWaigouDelivery() {
		wwpServer.exportExcelWaigouDelivery(waigouDelivery, pageStatus,
				firsttime, endtime);
		return "error";
	}

	/**
	 * 订单反审
	 * 
	 * @return
	 */
	public String backApply() {
		String msg = wwpServer.backApply(id);
		if (msg.equals("true")) {
			errorMessage = "反审成功!";
			if (tag.equals("ww")) {
				url = "WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=dsq&tag="
						+ tag;
			} else {
				url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&tag="
						+ tag;
			}
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	public String exportExcelWaigouDeliveryDetail() {
		try {
			wwpServer.exportExcelWaigouDeliveryDetail(waigoudd, firsttime,
					endtime, pageStatus);
		} catch (Exception e) {
			System.out.println("-------------------------------------------");
			e.printStackTrace();
		}

		return "error";
	}

	// 不合格品退货
	public String bhgpth() {
		String filename = "";
		if (attachment != null && attachment.length > 0) {
			filename = MKUtil.saveFile(attachment, attachmentFileName,
					"DefectiveProduct");
		}
		// errorMessage = wwpServer.bhgth(id, buhegeNumber, hegeNumber,
		// filename,
		// ids, tuisongId,waigoudd);
		return "goods_bhgth";
	}

	// 不合格品退货
	public String DefChuLiSq() {
		errorMessage = wwpServer.DefChuLiSq(dp, ids, tuisongId);
		return "goods_bhgth";
	}

	// 查询当日的外购来料日报表
	public String findWgDSheetList() {
		WaigouDailySheet w = wgdSheet;
		if (wgdSheet != null) {
			ActionContext.getContext().getSession().put("wgdSheet", wgdSheet);
		} else {
			wgdSheet = (WaigouDailySheet) ActionContext.getContext()
					.getSession().get("wgdSheet");
		}
		Object[] object = wwpServer.findWgDSheetList(wgdSheet, Integer
				.parseInt(cpage), pageSize, firsttime, endtime, pageStatus);
		zhuserList = wwpServer.findAllZhUser();
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findWgDSheetList.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "procard_wgdSheetList";
	}

	// 导出来料日报表
	public String exportExcelwgdSheet() {
		wwpServer.exportExcelwgdSheet(wgdSheet, firsttime, endtime, pageStatus);
		return "error";
	}

	// 打印领料单
	public void printlingliaodan() {
		errorMessage = wwpServer.printlingliaodan(id);
		MKUtil.writeJSON(errorMessage);
	}

	// 根据送货单明细Id查询送货单明细
	public String findWgddById() {
		waigoudd = wwpServer.findWgddById(id);
		return "Procard_printdl";
	}

	// 根据Id查询日报表
	public String findwgdSheetById() {
		Object[] obj = wwpServer.findwgdSheetById(id);
		wgdSheet = (WaigouDailySheet) obj[0];
		list = (List) obj[1];
		return "procard_wgdSheet";
	}

	// 修改来料日报表
	public String updatewgdSheet() {
		if (attachment != null && attachment.length > 0) {
			String filename = MKUtil.saveFile(attachment, attachmentFileName,
					"WaigouDailySheet");
			if (filename != null && filename.length() > 0) {
				String[] filenameArray = filename.split("\\|");
				for (int i = 0; i < fileType.length; i++) {
					String zdName = fileType[i];
					if ("bxgczfxfile".equals(zdName)) {
						wgdSheet.setBxgczfxfile(filenameArray[i]);
					} else if ("csROHSfile".equals(zdName)) {
						wgdSheet.setCsROHSfile(filenameArray[i]);
					} else if ("saltspraytestfile".equals(zdName)) {
						wgdSheet.setSaltspraytestfile(filenameArray[i]);
					} else if ("flammabilitytestfile".equals(zdName)) {
						wgdSheet.setFlammabilitytestfile(filenameArray[i]);
					}
				}
			}
		}
		boolean bool = wwpServer.updatewgdSheet(wgdSheet);
		errorMessage = "修改失败!";
		if (bool) {
			errorMessage = "修改成功!";
		}
		setUrl("WaigouwaiweiPlanAction!findWgDSheetList.action?cpage=" + cpage);
		return "error";
	}

	// 跳转二次确认页面
	public String tobhgth() {
		dp = wwpServer.findDefectiveProductById(id);
		waigoudd = wwpServer.findWgddById(dp.getWgddId());
		list = wwpServer.findOScopeListBywwddId(dp.getWgddId());
		return "goods_bhgth";
	}

	// 查询所有不良品处理单
	public String findAllDefectiveProduct() {
		if (dp != null) {
			ActionContext.getContext().getSession().put("dp", dp);
		} else {
			dp = (DefectiveProduct) ActionContext.getContext().getSession()
					.get("dp");
		}
		Object[] object = wwpServer
				.findAllDefectiveProduct(dp, Integer.parseInt(cpage), pageSize,
						firsttime, endtime, pageStatus, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findAllDefectiveProduct.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "show_dpList";
	}

	// 查询所有退货单
	public String findReturnSingle() {
		if (rs != null) {
			ActionContext.getContext().getSession().put("rs", rs);
		} else {
			rs = (ReturnSingle) ActionContext.getContext().getSession().get(
					"rs");
		}
		if (firsttime != null) {
			ActionContext.getContext().getSession().put("firsttime", firsttime);
		} else {
			firsttime = (String) ActionContext.getContext().getSession().get(
					"firsttime");
		}
		if (endtime != null) {
			ActionContext.getContext().getSession().put("endtime", endtime);
		} else {
			endtime = (String) ActionContext.getContext().getSession().get(
					"endtime");
		}
		Object[] object = wwpServer
				.findReturnSingle(rs, Integer.parseInt(cpage), pageSize,
						firsttime, endtime, pageStatus, tag);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findReturnSingle.action?pageStatus="
								+ pageStatus + "&tag=" + tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "show_rsList";
	}

	// 查询所有退货单明细
	public String findReturnsDetails() {
		if (rds != null) {
			ActionContext.getContext().getSession().put("rds", rds);
		} else {
			rds = (ReturnsDetails) ActionContext.getContext().getSession().get(
					"rds");
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
		Object[] object = wwpServer
				.findReturnsDetails(rds, Integer.parseInt(cpage), pageSize,
						pageStatus, tag, startDate, endDate);
		if (object != null && object.length > 0) {
			list = (List) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("WaigouwaiweiPlanAction!findReturnsDetails.action?pageStatus="
								+ pageStatus + "&tag=" + tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "show_rdsList";
	}

	// 根据退货单Id查询所有退货单明细
	public String findReturnsDetailsByrsId() {
		if (id != null) {
			Object[] obj = wwpServer.findReturnsDetailsByrsId(id);
			rs = (ReturnSingle) obj[0];
			list = (List) obj[1];
		}
		return "show_printRds";
	}

	// 打印退货单
	public void updateRsToPrint() {
		errorMessage = wwpServer.updateRsToPrint(rs);
		if ("true".equals(errorMessage)) {
			MKUtil.writeJSON("打印成功!");
		}
	}

	// 跳转到库存出库页面
	public String togoodsUpdate() {
		errorMessage = wwpServer.lingliaoBywgddId(id, bacode, sumNum);
		MKUtil.writeJSON(errorMessage);
		return "goods_saveSell";
	}

	// 提醒入库/提醒检验
	public String Sendmsg() {
		wwpServer.Sendmsg(id, tag);
		errorMessage = "提醒成功!";
		return "error";
	}

	// 查询相应价格
	// public String showPrice() {
	// Object[] obj = wwpServer.showPrice(processIds);
	// setUrl("WaigouwaiweiPlanAction!findDqrWgPlanList.action");
	// if (obj != null && obj.length == 2) {
	// manualPlan = (ManualOrderPlan) obj[0];
	// list = (List) obj[1];
	// }
	// return "procard_price";
	// }
	// 手动下单
	public String sdaddwaigouOrder() {
		try {
			errorMessage = wwpServer.sdaddwaigouOrder(processIds, id2,
					caigouNums);
		} catch (Exception e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		}
		if ("true".equals(errorMessage)) {
			errorMessage = "采购订单添加成功!";
			url = "WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=dsq&tag="
					+ tag;
		}
		return ERROR;
	}

	// 多个手动下单
	public String sdxdshowPrice() {
		Object[] obj = wwpServer.showPrice(processIds);
		if (obj != null && obj.length > 0) {
			mopList = (List<ManualOrderPlan>) obj[0];
			zhuserList = (List<ZhUser>) obj[1];
			sumNum = (Float) obj[2];
			errorMessage = (String) obj[3];
			if (!"true".equals(errorMessage)) {
				return "error";
			} else {
				errorMessage = "";
			}
		}
		return "procard_price";
	}

	// 打回重新申请
	public String cxsqwaigouorder() {
		errorMessage = wwpServer.cxsqwaigouorder(id);
		if ("".equals(errorMessage)) {
			errorMessage = "重新申请成功！";
		}
		return "error";
	}

	// 导出采购订单
	public String exportExcelWaigouPlan() {
		wwpServer.exportExcelWaigouPlan(waigouPlan, pageStatus, tag);
		return "error";
	}

	// 导出采购订单
	public String exportExcelWaigouPlan2() {
		if (waigouPlan == null) {
			waigouPlan = new WaigouPlan();
		}
		waigouPlan.setPlanNumber(planNumber);
		wwpServer.exportExcelWaigouPlan2(waigouPlan, pageStatus, tag);
		return "error";
	}

	// 删除送货单明细
	public String delwgdd() {
		errorMessage = wwpServer.delwgdd(id);
		url = "WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id=" + id2+"&pageStatus="+pageStatus;
		return "error";
	}
	//删除送货单
	public String delwaigouDelivery(){
		try {
			errorMessage =	wwpServer.delwaigouDelivery(id);
		} catch (Exception e) {
			errorMessage = e.getMessage();
		}
		url = "WaigouwaiweiPlanAction!findDeliveryNote.action?pageStatus="+pageStatus+"&cpage="+cpage;
		return "error";
	}
	
	// 计算转换数量;
	public void getzhuanhuanNum() {
		try {
			Float zhuanhuanNum = wwpServer.getzhuanhuanNum(id, sumNum);
			MKUtil.writeJSON(zhuanhuanNum);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//
	public String wWorderList() {

		return "Procard_WwOrderList1";
	}

	public String findAllQualifiedRate() {
		if (qualifiedRate != null) {
			ActionContext.getContext().getSession().put("qualifiedRate",
					qualifiedRate);
		} else {
			qualifiedRate = (QualifiedRate) ActionContext.getContext()
					.getSession().get("qualifiedRate");
		}
		Map<Integer, Object> map = wwpServer.findAllqualifiedRate(
				qualifiedRate, Integer.parseInt(cpage), pageSize);
		qrList = (List<QualifiedRate>) map.get(1);
		if (qrList != null & qrList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("WaigouwaiweiPlanAction!findAllQualifiedRate.action");
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "qualifiedRate_list";
	}

	// 是否启用MOQ前页面
	public String toisuseMQQ() {
		manualPlan = wwpServer.findmopById(id);
		if (manualPlan.getEpStatus() != null
				&& manualPlan.getEpStatus().length() > 0
				&& !"打回".equals(manualPlan.getEpStatus())) {
			errorMessage = "该件号的起订量已经提交审批,您可刷新后重试!";
			url = "CircuitRunAction_findAduitPage.action?id="
					+ manualPlan.getEpId();
			return ERROR;
		}
		Object[] obj = wwpServer.findZhuser(manualPlan, mxId);
		errorMessage = (String) obj[0];
		zhuserList = (List<ZhUser>) obj[1];
		if (!"true".equals(errorMessage)) {
			return ERROR;
		}
		return "mop_isUse";
	}

	// 是否启用MOQ前页面
	public String toisuseMQQ0() {
		wgmoq = wwpServer.findWgMOQOne(id, markId, kgliao, specification,
				banbenNumber);
		if (wgmoq.getEpStatus() != null && wgmoq.getEpStatus().length() > 0
				&& !"打回".equals(wgmoq.getEpStatus())) {
			errorMessage = "该件号的起订量已经提交审批,您可刷新后重试!";
			url = "CircuitRunAction_findAduitPage.action?id=" + wgmoq.getEpId();
			return ERROR;
		}
		Object[] obj = wwpServer.findZhuser(manualPlan, mxId);
		errorMessage = (String) obj[0];
		zhuserList = (List<ZhUser>) obj[1];
		if (!"true".equals(errorMessage)) {
			return ERROR;
		}
		return "mop_isUse0";
	}

	public List<QualifiedRate> getQrList() {
		return qrList;
	}

	public void setQrList(List<QualifiedRate> qrList) {
		this.qrList = qrList;
	}

	public QualifiedRate getQualifiedRate() {
		return qualifiedRate;
	}

	public void setQualifiedRate(QualifiedRate qualifiedRate) {
		this.qualifiedRate = qualifiedRate;
	}

	// 是否启用MOQ运算;
	public void isUseMOQ() {
		try {
			boolean bool = wwpServer.isUseMOQ(id, pageStatus, mxId);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			MKUtil.writeJSON(false);
		}

	}

	// 是否启用MOQ运算;
	public void isUseMOQ0() {
		try {
			boolean bool = wwpServer.isUseMOQ(id, pageStatus, mxId);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			MKUtil.writeJSON(false);
		}

	}

	// 是否需要增大检验数量再次检验页面
	public String toisAgainCheck() {
		Object[] obj = wwpServer.toisAgainCheck(id);
		waigoudd = (WaigouDeliveryDetail) obj[0];
		xujianpingci = (WaigoujianpinciZi) obj[1];
		return "isAgainC1heck";
	}

	// 是否需要增大检验数量再次检验
	public String isAgainCheck() {
		// errorMessage = wwpServer.isAgainCheck(waigoudd, ids, tuisongId);
		return "goods_bhgth";
	}

	// 外购订单明细件号反查
	public String findProcardBywgMarkId() {
		Object[] obj = wwpServer.findProcardBywgMarkId(id);
		waigouPlan = (WaigouPlan) obj[0];
		manualPlan = (ManualOrderPlan) obj[1];
		list = (List) obj[2];
		return "wgPlan_showProcard";
	}

	// 件号反查
	public String findAllCgxinxi() {
		Object[] obj = wwpServer.findAllCgxinxi(id);
		manualPlan = (ManualOrderPlan) obj[0];
		list = (List) obj[1];
		wwPlanList = (List<WaigouPlan>) obj[2];
		return "mop_All";
	}

	public String agreesbjd() {
		String msg = "";
		try {
			msg = wwpServer.agreesbjd(id);
		} catch (Exception e) {
			msg = e.getMessage();
			e.printStackTrace();
		}

		if ("true".equals(msg)) {
			errorMessage = "操作成功!";
		} else {
			errorMessage = msg;
		}
		url = "WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=gyssbdqr";
		return "error";
	}

	public WaigouWaiweiPlanServer getWwpServer() {
		return wwpServer;
	}

	public String getwgOrderTz() {
		Users user = Util.getLoginUser();
		if (user == null) {
			errorMessage = "请先登录!";
			return "error";
		}
		waigouOrder = wwpServer.findWaigouOrderById(id);
		if (waigouOrder == null) {
			errorMessage = "没有找打对应的订单!";
			return "error";
		} else {
			if (user.getInternal().equals("否")
					&& !user.getId().equals(waigouOrder.getUserId())) {// 非内部人员只有对应供应商可下载
				errorMessage = "您没有下载权限!";
				return "error";
			}
		}
		Map<String, String> tzwzmap = wwpServer.getwgOrderTz(id);
		if (tzwzmap != null) {
			try {
				Set<String> keys = tzwzmap.keySet();
				if (keys != null && keys.size() > 0) {
					String path = ServletActionContext.getServletContext()
							.getRealPath("/upload/file/processTz");
					// ZIP打包图片
					File zipFile = new File(path + "/"
							+ waigouOrder.getPlanNumber() + ".zip");
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
							zout.putNextEntry(new ZipEntry(tzwzmap
									.get(filename))); // 导出名称
							while ((len = in.read(buf)) > 0) {
								zout.write(buf, 0, len);
							}
							zout.closeEntry();
							in.close();
						} catch (Exception e) {
							// TODO: handle exception
							continue;
						}
					}
					zout.close();

					// 下载图片
					FileInputStream zipInput = new FileInputStream(zipFile);
					HttpServletResponse response = ServletActionContext
							.getResponse();
					OutputStream out = response.getOutputStream();
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition",
							"attachment; filename="
									+ waigouOrder.getPlanNumber() + ".zip");
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
	 * 跳往物料替换页面
	 * 
	 * @return
	 */
	public String toChangeWgj() {
		procard = wwpServer.gettoChangeWgj(id, markId, kgliao);
		return "Procard_wgjth";//
	}

	/**
	 * 跳往物料替换页面
	 * 
	 * @return
	 */
	public String toChangeWgj2() {
		procard = wwpServer.gettoChangeWgj(id, markId, kgliao);
		return "Procard_wgjth2";//
	}

	/**
	 * 物料替换
	 * 
	 * @return
	 */
	public String changeWgj() {
		if (procard.getFilnalCount() == null) {
			errorMessage = "请填写使用数量!";
			return "error";
		}
		String msg = wwpServer.changeWgj(procard, markId, kgliao);
		if (msg.equals("true")) {
			errorMessage = "替换成功!";
			url = "WaigouwaiweiPlanAction!findDqrWlDetailList.action?id="
					+ procard.getRootId();
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/**
	 * 物料替换
	 * 
	 * @return
	 */
	public String changeWgj2() {// 屏蔽等待解封
		// if (procard.getFilnalCount() == null) {
		// errorMessage = "请填写使用数量!";
		// return "error";
		// }
		// String msg = wwpServer.changeWgj2(procard, markId, kgliao);
		// if (msg.equals("true")) {
		// errorMessage = "替换成功!";
		// url = "WaigouwaiweiPlanAction!findDqrWlDetailList.action?id="
		// + procard.getRootId();
		// } else {
		// errorMessage = msg;
		// }
		return "error";
	}

	//
	public String gotoprint() {
		if (processIds != null && processIds.length > 0) {
			strids = "";
			for (int i = 0; i < processIds.length; i++) {
				strids += "," + processIds[i];
			}
			if (strids.length() > 0) {
				strids = strids.substring(1);
			}
		} else if (strids != null && strids.length() > 0) {
			String[] arrids = strids.split(",");
			processIds = new Integer[arrids.length];
			if (arrids != null && arrids.length > 0) {
				for (int i = 0; i < arrids.length; i++) {
					processIds[i] = Integer.parseInt(arrids[i]);
				}
			}
		}
		waigouOrderList = wwpServer.gotoprint(processIds);
		if ("waigou".equals(pageStatus)) {
			return "waigouOrderprint";
		} else if ("waiwei".equals(pageStatus)) {
			return "waiweiOrderprint";
		}
		return "error";
	}

	// 送货单明细修改数量
	public void updatewgddNum() {
		try {
			errorMessage = wwpServer.updatewgddNum(id, sumNum);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}
	}

	/**
	 * 订单明细小数补整
	 * 
	 * @return
	 */
	public String buzheng() {
		String msg = wwpServer.buzheng(id);
		if (msg.equals("true")) {
			errorMessage = "补整成功!";
		} else {
			errorMessage = msg;
		}
		url = "WaigouwaiweiPlanAction!findWgPlanList.action?id=" + id2
				+ "&pageStatus=" + pageStatus;
		return "error";
	}

	// 统一刷新价格;
	public String shuaixinAllPrice() {
		errorMessage = wwpServer.shuaixinAllPrice(processIds);
		if (!"error".equals(errorMessage)) {
			if ("waiwei".equals(tag)) {
				setUrl("WaigouwaiweiPlanAction!findWwPlanList.action?id=" + id
						+ "&pageStatus=" + pageStatus);
			} else {
				setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?id=" + id
						+ "&pageStatus=" + pageStatus);
			}
		} else {
			errorMessage = "刷新价格失败!";
		}
		return "error";
	}

	// 采购单打印;
	public void wgOrderIsPrint() {
		try {
			errorMessage = wwpServer.wgOrderIsPrint(processIds);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送消息给检测室;
	public void sendmsg0() {
		wwpServer.sendmsg(wgdSheet);
	}

	// 添加模具订单
	public String addmujuOrder() {
		errorMessage = wwpServer.addmujuOrder(processIds);
		return "findWwOrderList";
	}

	/***
	 * 供应商综合信息 -产品总量分析
	 * 
	 * @return
	 */
	public String TotalProductAnalysis() {
		WaigouPlan waigouPlan = new WaigouPlan();
		waigouPlan.setGysId(id2);

		pageStatus = "findAll";
		if (waigouPlan != null) {
			ActionContext.getContext().getSession().put("waigouPlan",
					waigouPlan);
		} else {
			waigouPlan = (WaigouPlan) ActionContext.getContext().getSession()
					.get("waigouPlan");
		}

		Object[] object = wwpServer.findTotalProduct(id2);

		List<WaigouPlan> list = (List<WaigouPlan>) object[0];
		Map<String, Object> map = new HashMap<String, Object>();

		for (WaigouPlan wgPlan : list) {
			float f = wgPlan.getHasruku();
			if (map.containsKey(wgPlan.getMarkId())) {
				f += Float.parseFloat(map.get(wgPlan.getMarkId()).toString());
			}
			map.put(wgPlan.getMarkId(), f);
		}

		class TotalProduct {
			public String name;
			public String value;
		}
		List<TotalProduct> totalProducts = new ArrayList<TotalProduct>();
		for (String key : map.keySet()) {
			TotalProduct data = new TotalProduct();
			data.name = key;
			data.value = map.get(key).toString();
			totalProducts.add(data);
		}
		MKUtil.writeJSON(totalProducts);
		return null;
	}

	/**
	 * 查询所有辅料待入库信息
	 * 
	 * @return
	 */
	public String findAllflDrk() {
		list_wdd = wwpServer.findAllflDrk();
		return "Procard_showfldrk";
	}

	/**
	 * 根据总成Id、件号、供料属性、版本、规格、获取外购件明细
	 * 
	 * @param
	 */
	public String showWgProcardList() {
		procardList = wwpServer.showWgProcardList(id, markId, kgliao,
				banbenNumber, specification);
		return "Procard_showWgProcard";
	}

	public String updateMore() {
		if (waigouPlan != null) {
			errorMessage = wwpServer.updateWaigouPlan(waigouPlan);
		}
		this
				.setUrl("WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=findAll");
		return "error";
	}

	// 删除订单明细
	public String delWaiGouPlan() {
		errorMessage = wwpServer.deleteWaiGouPlan(id, deltag);
		if ("true".equals(errorMessage))
			errorMessage = "操作成功！";
		return "error";
	}

	// 待检库出库
	public String djkOut() {
		wwpServer.djkOut();
		return "error";
	}

	public void bomweiwaiwei() {
		List list = wwpServer.findbomweiwaiwei();
		MKUtil.writeJSON(list.toString());
	}

	public void addbomweiwaiwei() {// WaigouwaiweiPlanAction!addbomweiwaiwei.action
		List list = wwpServer.addbomweiwaiwei();
		MKUtil.writeJSON(list.toString());
	}

	// 外委订单明细关闭订单
	public String closeWaiweiPlan() {
		String msg = wwpServer.closeWaiweiPlan(id);
		if (msg.equals("true")) {
			errorMessage = "关闭成功!";
		} else {
			errorMessage = msg;
		}
		return "error";

	}

	// 外委订单明细删除订单
	public String backwwplan() {
		String msg = wwpServer.backwwplan(id);
		if (msg.equals("true")) {
			errorMessage = "取消成功!";
		} else {
			errorMessage = msg;
		}
		return "error";

	}

	//
	public void ajax_findSellList() {
		try {
			List<Sell> sellList = wwpServer.ajax_findSellList(id);
			MKUtil.writeJSON(sellList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}
	}

	// procard添加备注
	public void ajax_addremark() {
		try {
			errorMessage = wwpServer.ajax_addremark(id, markId, kgliao,
					banbenNumber, specification, noPrice);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			MKUtil.writeJSON("error");
			e.printStackTrace();
		}
	}

	// 修复采购单状态
	public String XiuFuOrderStatus() {
		wwpServer.XiuFuOrderStatus();
		return "error";
	}

	// 修改签收数量
	public void UpdateQrNumber() {
		try {
			errorMessage = wwpServer.UpdateQrNumber(id, goodsCount);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 外委供应商补料页面
	public String showtocsbl() {
		procardList = wwpServer.showtocsbl(id);
		csblList = wwpServer.findCsblProcardBywgddId(id);
		return "procard_gyscsbl";
	}

	// 导出退货单明细
	public void exportrds() {
		wwpServer.exprotrds(rds, pageStatus, startDate, endDate, tag);
	}

	// 获取外购订单
	public String findWaigouOrderById() {
		Object[] obj = wwpServer.findWaigouPlanListByid(id);
		if (obj != null) {
			waigouOrder = (WaigouOrder) obj[0];
			wwPlanList = (List<WaigouPlan>) obj[1];
		}
		// waigouOrder = wwpServer.findWaigouOrderById(id);
		// wwPlanList = wwpServer.findWgWwPlanList(waigouPlan, pageNo, pageSize,
		// pageStatus, wgOrderId, deltag)
		return "Procard_WgOrder_addRemark";
	}

	// 添加备注
	public void addwaigouOrderRemark() {
		errorMessage = wwpServer.addwaigouOrderRemark(waigouOrder);
		MKUtil.writeJSON(errorMessage);
	}

	// 根据采购订单明细查询设变减单申请
	public String findWgClApplyList() {
		wgclApplyList = wwpServer.findWgClApplyList(id);
		return "WgClApply_List";
	}

	// 采购明细对账;
	public String WaigouPlanduiZhang() {
		list = wwpServer.WaigouPlanduiZhang(type, months0, months1, years0,
				years1, waigouPlan);
		return "procard_dzWgPlan";
	}

	/***
	 * 零件时间分析
	 */
	public void findmarkIdTime() {
		MKUtil.writeJSON(wwpServer.findmarkIdTime(markId, startDate, endDate));
	}

	/***
	 * 采购入库总时间分析
	 */
	public void findcaigouAllTime() {
		MKUtil.writeJSON(wwpServer.findcaigouAllTime());
	}
	/**
	 * 查询所有供应商件号送货关闭情况
	 * @param wwpServer
	 */
	public String findAllGysMarkIdCkolse(){
		if (gysClose != null) {
			ActionContext.getContext().getSession().put("gysClose",
					gysClose);
		} else {
			gysClose = (GysMarkIdCkolse) ActionContext.getContext()
					.getSession().get("gysClose");
		}
		Object[] obj = wwpServer.findAllGysMarkIdCkolse(gysClose, pageSize, Integer.parseInt(cpage), pageStatus);
		gysCloseList =(List<GysMarkIdCkolse>) obj[0];
			int count = (Integer) obj[1];
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("WaigouwaiweiPlanAction!findAllGysMarkIdCkolse.action?pageStatus="+pageStatus);
		return "gysClose_showList";
	}
	//
	public	String	updateGysMarkIdCkolse(){
		errorMessage =	wwpServer.updateGysMarkIdCkolse(gysClose);
		if(errorMessage.equals("true")){
			errorMessage = "恢复成功!~";
		}
		return "error";
	}
	
	public String sdjs(){
		String msg = wwpServer.sdjs(id);
		if (msg.equals("true")) {
			errorMessage = "取消成功!";
		} else {
			errorMessage = msg;
		}
		return "error";
	}
	/**
	 * waigouwaiweiPlan 拆分
	 * @return
	 */
	public String splitwwpCount(){
		String msg = wwpServer.splitwwpCount(id,splitCount1,splitCount2);
		if (msg.equals("true")) {
			errorMessage = "拆分成功!";
			this
			.setUrl("ProcardAction!findNeedjhWgWwPlanList.action?pageStatus="
					+ pageStatus);
		} else {
			errorMessage = msg;
		}
		return "error";
	}
	public void setWwpServer(WaigouWaiweiPlanServer wwpServer) {
		this.wwpServer = wwpServer;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId2() {
		return id2;
	}

	public void setId2(Integer id2) {
		this.id2 = id2;
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

	public String getViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(String viewStatus) {
		this.viewStatus = viewStatus;
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

	public List getProcardList() {
		return procardList;
	}

	public void setProcardList(List procardList) {
		this.procardList = procardList;
	}

	public List getWwPlanList() {
		return wwPlanList;
	}

	public WaigouOrder getWaigouOrder() {
		return waigouOrder;
	}

	public void setWaigouOrder(WaigouOrder waigouOrder) {
		this.waigouOrder = waigouOrder;
	}

	public WaigouPlan getWaigouPlan() {
		return waigouPlan;
	}

	public void setWaigouPlan(WaigouPlan waigouPlan) {
		this.waigouPlan = waigouPlan;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public WaigouDelivery getWaigouDelivery() {
		return waigouDelivery;
	}

	public void setWaigouDelivery(WaigouDelivery waigouDelivery) {
		this.waigouDelivery = waigouDelivery;
	}

	public List<WaigouDeliveryDetail> getList_wdd() {
		return list_wdd;
	}

	public void setList_wdd(List<WaigouDeliveryDetail> listWdd) {
		list_wdd = listWdd;
	}

	public String getBacode() {
		return bacode;
	}

	public void setBacode(String bacode) {
		this.bacode = bacode;
	}

	public WaigouDeliveryDetail getWaigoudd() {
		return waigoudd;
	}

	public void setWaigoudd(WaigouDeliveryDetail waigoudd) {
		this.waigoudd = waigoudd;
	}

	public OsTemplate getOsTemplate() {
		return osTemplate;
	}

	public void setOsTemplate(OsTemplate osTemplate) {
		this.osTemplate = osTemplate;
	}

	public String getIsHege() {
		return isHege;
	}

	public void setIsHege(String isHege) {
		this.isHege = isHege;
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

	public List<WarehouseNumber> getWnList() {
		return wnList;
	}

	public void setWnList(List<WarehouseNumber> wnList) {
		this.wnList = wnList;
	}

	public Double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(Double sumMoney) {
		this.sumMoney = sumMoney;
	}

	public WarehouseNumber getWarehouseNumber() {
		return warehouseNumber;
	}

	public void setWarehouseNumber(WarehouseNumber warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}

	public List<WarehouseNumber> getWarehouseNumbers() {
		return warehouseNumbers;
	}

	public void setWarehouseNumbers(List<WarehouseNumber> warehouseNumbers) {
		this.warehouseNumbers = warehouseNumbers;
	}

	public List<WareBangGoogs> getWareBangGoogs() {
		return wareBangGoogs;
	}

	public void setWareBangGoogs(List<WareBangGoogs> wareBangGoogs) {
		this.wareBangGoogs = wareBangGoogs;
	}

	public String getMxId() {
		return mxId;
	}

	public void setMxId(String mxId) {
		this.mxId = mxId;
	}

	public List<ZhUser> getZhuserList() {
		return zhuserList;
	}

	public void setZhuserList(List<ZhUser> zhuserList) {
		this.zhuserList = zhuserList;
	}

	public Integer getId_wn() {
		return id_wn;
	}

	public void setId_wn(Integer idWn) {
		id_wn = idWn;
	}

	public Integer getId_wdd() {
		return id_wdd;
	}

	public void setId_wdd(Integer idWdd) {
		id_wdd = idWdd;
	}

	public String getWgTypes() {
		return wgTypes;
	}

	public void setWgTypes(String wgTypes) {
		this.wgTypes = wgTypes;
	}

	public WaigoujianpinciZi getXujianpingci() {
		return xujianpingci;
	}

	public void setXujianpingci(WaigoujianpinciZi xujianpingci) {
		this.xujianpingci = xujianpingci;
	}

	public List<Waigoujianpinci> getXjbzlist() {
		return xjbzlist;
	}

	public void setXjbzlist(List<Waigoujianpinci> xjbzlist) {
		this.xjbzlist = xjbzlist;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public Also getAlso() {
		return also;
	}

	public void setAlso(Also also) {
		this.also = also;
	}

	public List<WaigouOrder> getWaigouOrderList() {
		return waigouOrderList;
	}

	public void setWaigouOrderList(List<WaigouOrder> waigouOrderList) {
		this.waigouOrderList = waigouOrderList;
	}

	public List<OaAppDetail> getOaDList() {
		return oaDList;
	}

	public void setOaDList(List<OaAppDetail> oaDList) {
		this.oaDList = oaDList;
	}

	public String getNoPrice() {
		return noPrice;
	}

	public void setNoPrice(String noPrice) {
		this.noPrice = noPrice;
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	public ZhUser getZhUser() {
		return zhUser;
	}

	public void setZhUser(ZhUser zhUser) {
		this.zhUser = zhUser;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public List<Sell> getListSell() {
		return listSell;
	}

	public void setListSell(List<Sell> listSell) {
		this.listSell = listSell;
	}

	public List<Goods> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}

	public void setWwPlanList(List<WaigouPlan> wwPlanList) {
		this.wwPlanList = wwPlanList;
	}

	public List<OsTemplate> getOstList() {
		return ostList;
	}

	public void setOstList(List<OsTemplate> ostList) {
		this.ostList = ostList;
	}

	public String getFirsttime() {
		return firsttime;
	}

	public void setFirsttime(String firsttime) {
		this.firsttime = firsttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public WaigouDailySheet getWgdSheet() {
		return wgdSheet;
	}

	public void setWgdSheet(WaigouDailySheet wgdSheet) {
		this.wgdSheet = wgdSheet;
	}

	public WaigouOrder getWaigouOrder2() {
		return waigouOrder2;
	}

	public void setWaigouOrder2(WaigouOrder waigouOrder2) {
		this.waigouOrder2 = waigouOrder2;
	}

	public Float getBuhegeNumber() {
		return buhegeNumber;
	}

	public void setBuhegeNumber(Float buhegeNumber) {
		this.buhegeNumber = buhegeNumber;
	}

	public Float getHegeNumber() {
		return hegeNumber;
	}

	public void setHegeNumber(Float hegeNumber) {
		this.hegeNumber = hegeNumber;
	}

	public DefectiveProduct getDp() {
		return dp;
	}

	public void setDp(DefectiveProduct dp) {
		this.dp = dp;
	}

	public ReturnSingle getRs() {
		return rs;
	}

	public void setRs(ReturnSingle rs) {
		this.rs = rs;
	}

	public ReturnsDetails getRds() {
		return rds;
	}

	public void setRds(ReturnsDetails rds) {
		this.rds = rds;
	}

	public Float getSumNum() {
		return sumNum;
	}

	public void setSumNum(Float sumNum) {
		this.sumNum = sumNum;
	}

	public Double getSumbhsprice() {
		return sumbhsprice;
	}

	public void setSumbhsprice(Double sumbhsprice) {
		this.sumbhsprice = sumbhsprice;
	}

	public List<String> getStrList() {
		return strList;
	}

	public void setStrList(List<String> strList) {
		this.strList = strList;
	}

	public WareHouseAuthService getWareHouseAuthService() {
		return wareHouseAuthService;
	}

	public void setWareHouseAuthService(
			WareHouseAuthService wareHouseAuthService) {
		this.wareHouseAuthService = wareHouseAuthService;
	}

	public File[] getAttachment() {
		return attachment;
	}

	public void setAttachment(File[] attachment) {
		this.attachment = attachment;
	}

	public String[] getAttachmentContentType() {
		return attachmentContentType;
	}

	public void setAttachmentContentType(String[] attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}

	public String[] getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String[] attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public List getCqList() {
		return cqList;
	}

	public void setCqList(List cqList) {
		this.cqList = cqList;
	}

	public String[] getFileType() {
		return fileType;
	}

	public String getHegeStatus() {
		return hegeStatus;
	}

	public void setHegeStatus(String hegeStatus) {
		this.hegeStatus = hegeStatus;
	}

	public List<DayHege> getDayHegeList() {
		return dayHegeList;
	}

	public void setDayHegeList(List<DayHege> dayHegeList) {
		this.dayHegeList = dayHegeList;
	}

	public List<ZhouHege> getZhouList() {
		return zhouList;
	}

	public void setZhouList(List<ZhouHege> zhouList) {
		this.zhouList = zhouList;
	}

	public List<MouthHege> getMouthHegeList() {
		return mouthHegeList;
	}

	public void setMouthHegeList(List<MouthHege> mouthHegeList) {
		this.mouthHegeList = mouthHegeList;
	}

	public List<QuarterHege> getQuarterHegeList() {
		return quarterHegeList;
	}

	public void setQuarterHegeList(List<QuarterHege> quarterHegeList) {
		this.quarterHegeList = quarterHegeList;
	}

	public List<YearHege> getYearHegeList() {
		return yearHegeList;
	}

	public void setYearHegeList(List<YearHege> yearHegeList) {
		this.yearHegeList = yearHegeList;
	}

	public DayHege getDayHege() {
		return dayHege;
	}

	public void setDayHege(DayHege dayHege) {
		this.dayHege = dayHege;
	}

	public ZhouHege getZhouHege() {
		return zhouHege;
	}

	public void setZhouHege(ZhouHege zhouHege) {
		this.zhouHege = zhouHege;
	}

	public MouthHege getMouthHege() {
		return mouthHege;
	}

	public void setMouthHege(MouthHege mouthHege) {
		this.mouthHege = mouthHege;
	}

	public QuarterHege getQuarterHege() {
		return quarterHege;
	}

	public void setQuarterHege(QuarterHege quarterHege) {
		this.quarterHege = quarterHege;
	}

	public YearHege getYearHege() {
		return yearHege;
	}

	public void setYearHege(YearHege yearHege) {
		this.yearHege = yearHege;
	}

	public void setFileType(String[] fileType) {
		this.fileType = fileType;
	}

	public ManualOrderPlan getManualPlan() {
		return manualPlan;
	}

	public void setManualPlan(ManualOrderPlan manualPlan) {
		this.manualPlan = manualPlan;
	}

	public List<ManualOrderPlan> getMopList() {
		return mopList;
	}

	public void setMopList(List<ManualOrderPlan> mopList) {
		this.mopList = mopList;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getKgliao() {
		return kgliao;
	}

	public void setKgliao(String kgliao) {
		this.kgliao = kgliao;
	}

	public String getBanbenNumber() {
		return banbenNumber;
	}

	public void setBanbenNumber(String banbenNumber) {
		this.banbenNumber = banbenNumber;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public Float getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(Float goodsCount) {
		this.goodsCount = goodsCount;
	}

	public WgProcardMOQ getWgmoq() {
		return wgmoq;
	}

	public void setWgmoq(WgProcardMOQ wgmoq) {
		this.wgmoq = wgmoq;
	}

	public String getDeltag() {
		return deltag;
	}

	public void setDeltag(String deltag) {
		this.deltag = deltag;
	}

	public Float getSumbdhNUmber() {
		return sumbdhNUmber;
	}

	public void setSumbdhNUmber(Float sumbdhNUmber) {
		this.sumbdhNUmber = sumbdhNUmber;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}

	public String getTuisongId() {
		return tuisongId;
	}

	public void setTuisongId(String tuisongId) {
		this.tuisongId = tuisongId;
	}

	public Float[] getCaigouNums() {
		return caigouNums;
	}

	public void setCaigouNums(Float[] caigouNums) {
		this.caigouNums = caigouNums;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getNeedDate() {
		return needDate;
	}

	public void setNeedDate(String needDate) {
		this.needDate = needDate;
	}

	public String getExportTag() {
		return exportTag;
	}

	public void setExportTag(String exportTag) {
		this.exportTag = exportTag;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public int getPageSize1() {
		return pageSize1;
	}

	public void setPageSize1(int pageSize1) {
		this.pageSize1 = pageSize1;
	}

	public String getStrids() {
		return strids;
	}

	public void setStrids(String strids) {
		this.strids = strids;
	}

	public String getNoOperation() {
		return noOperation;
	}

	public void setNoOperation(String noOperation) {
		this.noOperation = noOperation;
	}

	public Float getSumwshNum() {
		return sumwshNum;
	}

	public void setSumwshNum(Float sumwshNum) {
		this.sumwshNum = sumwshNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPlanNumber() {
		return planNumber;
	}

	public void setPlanNumber(String planNumber) {
		this.planNumber = planNumber;
	}

	public List<OsRecord> getBhgosRecordList() {
		return bhgosRecordList;
	}

	public void setBhgosRecordList(List<OsRecord> bhgosRecordList) {
		this.bhgosRecordList = bhgosRecordList;
	}

	public List<ProcardCsBl> getCsblList() {
		return csblList;
	}

	public void setCsblList(List<ProcardCsBl> csblList) {
		this.csblList = csblList;
	}

	public List<WaigouPlanclApply> getWgclApplyList() {
		return wgclApplyList;
	}

	public void setWgclApplyList(List<WaigouPlanclApply> wgclApplyList) {
		this.wgclApplyList = wgclApplyList;
	}

	public List<ProcessAndMeasuring> getPamList() {
		return pamList;
	}

	public void setPamList(List<ProcessAndMeasuring> pamList) {
		this.pamList = pamList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMonths0() {
		return months0;
	}

	public void setMonths0(String months0) {
		this.months0 = months0;
	}

	public String getMonths1() {
		return months1;
	}

	public void setMonths1(String months1) {
		this.months1 = months1;
	}

	public String getYears0() {
		return years0;
	}

	public void setYears0(String years0) {
		this.years0 = years0;
	}

	public String getYears1() {
		return years1;
	}

	public void setYears1(String years1) {
		this.years1 = years1;
	}

	public GysMarkIdCkolse getGysClose() {
		return gysClose;
	}

	public void setGysClose(GysMarkIdCkolse gysClose) {
		this.gysClose = gysClose;
	}

	public List<GysMarkIdCkolse> getGysCloseList() {
		return gysCloseList;
	}

	public void setGysCloseList(List<GysMarkIdCkolse> gysCloseList) {
		this.gysCloseList = gysCloseList;
	}
	

	public int getSplitCount1() {
		return splitCount1;
	}

	public void setSplitCount1(int splitCount1) {
		this.splitCount1 = splitCount1;
	}

	public int getSplitCount2() {
		return splitCount2;
	}

	public void setSplitCount2(int splitCount2) {
		this.splitCount2 = splitCount2;
	}

	public String getSunxu() {
		return sunxu;
	}

	public void setSunxu(String sunxu) {
		this.sunxu = sunxu;
	}

	public String getCheckTimes() {
		return checkTimes;
	}

	public void setCheckTimes(String checkTimes) {
		this.checkTimes = checkTimes;
	}

}
