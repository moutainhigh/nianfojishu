package com.task.action.ess;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.net.httpserver.HttpsConfigurator;
import com.task.Dao.TotalDao;
import com.task.Server.WareHouseAuthService;
import com.task.Server.ess.GoodsServer;
import com.task.entity.BaoFeiGoods;
import com.task.entity.CpGoodsChangeWG;
import com.task.entity.GoodHouse;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.Goods_bzsq;
import com.task.entity.ProductManager;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.WareHouse;
import com.task.entity.YuLiaoApply;
import com.task.entity.sop.BuHeGePin;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.WaigouDelivery;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.sop.WaigouDeliveryGoods;
import com.task.entity.sop.WaigouDeliveryGoodsDetail;
import com.task.util.MKUtil;
import com.task.util.Util;

/**
 * 入库表功能Action层
 * 
 * @author 贾辉辉
 * 
 */
public class GoodsAction extends ActionSupport {
	private WareHouseAuthService wareHouseAuthService;
	private GoodsServer goodsServer;
	private BaoFeiGoods baofeigoods;// 报废库存表;
	private Goods_bzsq goodsBzsq;// 包装申请表;
	private List<Goods_bzsq> goodsBzsq_list;// 包装申请表操作集合
	private List<BaoFeiGoods> bfgList;
	private Goods goods;
	private List<Sell> sellList;
	private YuLiaoApply yuLiaoApply;
	private List<YuLiaoApply> yuLiaoApplyList;
	private List<Goods> list;
	private List lists;
	private List<WareHouse> warehouseList;
	private List<GoodHouse> goodHouseList;
	private List<ProductManager> pmList;
	private ProductManager productManager;
	private String[] goodsAndProcardIds;
	private Float[] lqCounts;

	private List<Goods> dqList;// 将到期
	private List<Goods> ydqList;// 已到期

	private WaigouDeliveryGoods waigouDeliveryGoods;
	private List<WaigouDeliveryGoods> wdgList;
	private WaigouDeliveryGoodsDetail waigoudGoodsD;// 送货单明细
	private List<WaigouDeliveryGoodsDetail> list_wdgd;
	private GoodsStore gs;
	private Sell sell;

	private Float sumMoney;
	private Float sumNum;
	private Float sumbhsprice;//

	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private Integer id;// 主键
	private Integer[] ids;// 主键
	private String startDate;// 开始时间
	private String endDate;// 截止时间
	private String tag;// 标识
	private String message;
	private String role;
	private List listHuizong;// 汇总的list
	private List<String> orderNumberList;// 订单号
	private String isNeed;// 是否需要关联
	private String pagestatus;// 页面状态;
	private double sumcount;
	private boolean isall;
	private String username;//
	private String errorMessage;// 错误消息
	private String successMessage;// 错误消息
	private List<String> cardIds;// 卡号
	private String firsttime;
	private String endtime;
	private String pageStatus;// 页面状态
	private List<String> l; // 仓库
	private String[] tiaojian;
	private String pwsswords;

	private CpGoodsChangeWG cpChangeWg;// 成品库入外购件库调仓对象
	private Users loginUser;
	private String goodsAge;
	private String more;
	private Integer handwordNumber;

	/***
	 * 库存删除操作
	 * 
	 * @return
	 */
	public String deletegs() {
		if (goods != null) {
			goodsServer.deletegs(goods);
		}
		errorMessage = "删除成功！！！";
		url = "goodsAction!findGoods.action";
		return ERROR;
	}

	// 查找Goods，进行出库和管理

	public String findGoods() {
		// dxmsk
		String msg = "findGoodsOK";
		if (pagestatus != null && "zz".equals(pagestatus)) {
			msg = "findGoodsZZ";
		}
		this.pageSize = 15;
		this.setUrl("goodsAction!findGoods.action?pagestatus=" + pagestatus
				+ "&role=" + role+"&pageStatus="+pageStatus);
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("goods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("goods");
			if (pagestatus != null && "zz".equals(pagestatus)) {
				request.getSession().removeAttribute("goods");
			} else if ("bhg".equals(pagestatus)) {
				request.getSession().removeAttribute("goods");
			}
		}
		if (null != startDate) {
			request.getSession().setAttribute("startDate", startDate);
		} else {
			startDate = (String) request.getSession().getAttribute("startDate");
		}
		if (null != endDate) {
			request.getSession().setAttribute("endDate", endDate);
		} else {
			endDate = (String) request.getSession().getAttribute("endDate");
		}
		Integer age = null;
		if (null != goodsAge) {
			request.getSession().setAttribute("goodsAge", goodsAge);
		} else {
			goodsAge = (String) request.getSession().getAttribute("goodsAge");
		}
		if (null != goodsAge && !"".equals(goodsAge)) {
			age = Integer.parseInt(goodsAge);
		}
		//goodsServer.pushkc1();
		
		Object[] obj = goodsServer.findGoods(goods, startDate, endDate, Integer
				.parseInt(cpage), pageSize, role, pagestatus, age);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		sumcount = (Double) obj[2];
		isall = (Boolean) obj[3];
		goodHouseList = wareHouseAuthService.findgoodHouselist();
		
		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		String code = user.getCode();

		for (Goods g : list) {
			String warehouse = g.getGoodsClass();
			if (wareHouseAuthService.getOut(code).contains(warehouse)) {
				g.setBout(true);
			}
			if (wareHouseAuthService.getEdit(code).contains(warehouse)) {
				g.setBedit(true);
			}
			if (wareHouseAuthService.getDel(code).contains(warehouse)) {
				g.setIsdel(true);
			}
		}
		// findGoodsZZ goods_bflistfind.jsp
		return msg; // findGoodsOK goods_listfind.jsp
	}

	@SuppressWarnings("unchecked")
	public String findGoodsPhoen() {
		if (goods != null)
			ActionContext.getContext().getSession().put("Goodss", goods);
		else
			goods = (Goods) ActionContext.getContext().getSession().get(
					"Goodss");
		Object[] object = goodsServer.showGoodsPhoen(goods, Integer
				.parseInt(cpage), pageSize, tag);
		if (object != null && object.length > 0) {
			list = (List<Goods>) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("goodsAction!findGoodsPhoen.action?tag=" + tag);
			}
			errorMessage = null;
		} else
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		return "goods_listfindPhone";
	}

	/**
	 * 余料管理分页显示
	 * 
	 * @return
	 */
	public String ylShowList() {
		this.pageSize = 15;
		this.setUrl("goodsAction!ylShowList.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("goods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("goods");
		}
		if (null != startDate) {
			request.getSession().setAttribute("startDate", startDate);
		} else {
			startDate = (String) request.getSession().getAttribute("startDate");
		}
		if (null != endDate) {
			request.getSession().setAttribute("endDate", endDate);
		} else {
			endDate = (String) request.getSession().getAttribute("endDate");
		}
		Object[] obj = goodsServer.findYlGoods(goods, startDate, endDate,
				Integer.parseInt(cpage), pageSize, role);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];

		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		String code = user.getCode();

		for (Goods g : list) {
			String warehouse = g.getGoodsClass();
			if (wareHouseAuthService.getOut(code).contains(warehouse)) {
				g.setBout(true);
			}
			if (wareHouseAuthService.getEdit(code).contains(warehouse)) {
				g.setBedit(true);
			}
		}
		return "yuGoods_showList";
	}

	/**
	 * 根据ID获得Goods对象，可修改和出库使用
	 * 
	 * @return
	 */
	public String getOneGoods() {
		goods = goodsServer.getGoodsById(id);
		if ("bhgth".equals(pagestatus)) {
			return "goods_bhgth";
		}
		Users user = Util.getLoginUser();
		username = user.getName();
		orderNumberList = goodsServer.getOrderNumbers(goods);
		warehouseList = goodsServer.findgoodHouselist();
		if ("goods_exChangeGoods".equals(tag)) {
			l = wareHouseAuthService.getIn(user.getCode());
		}
		return this.tag;// update --goods_update.jsp
		// 出库 goods_saveSell.jsp
		// 调库 goods_exChangeGoods.jsp
		// baofeishenqin goods_bfsq.jsp
	}

	/**
	 * 调库 从A库到B库，A做出库操作，B做入库操作
	 */
	public String exChangeGoods() {
		// goodsServer.ex
		try {
			errorMessage = goodsServer.exChangeGoods(sell, gs, goods, tag);
			if (errorMessage != null && "true".equals(errorMessage)) {
				errorMessage = "调库成功";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "处理异常：" + e.getMessage();
		}
		return "error";
	}

	public String updateGoods() {
		if ("saveSell".equals(tag)) {
			// 查看该件号是否存在单价信息,订单产品数量检验
			if (sell.getSellWarehouse().equals("成品库") && isNeed != null
					&& isNeed.equals("全")) {
				if (sell.getOrderNum() == null || "".equals(sell.getOrderNum())
						|| sell.getOrderNum().equals("未确定")) {
					errorMessage = "请选择内部订单号！";
					return ERROR;
				}
				if (sell.getOutOrderNumer() == null
						|| sell.getOutOrderNumer().equals("")) {
					errorMessage = "请选择填写外部部订单号！";
					return ERROR;
				}

				String message = goodsServer.findPriceByPartNumber(sell
						.getYwmarkId(), sell.getSellMarkId(), sell
						.getOrderNum(), sell.getSellCount(),sell.getSellLot());

				if (!message.equals("true")) {
					errorMessage = message;
					return ERROR;
				}
				// //通过件号和订单号查到之前该订单开票未通过但是合格的产品数量
				// Integer
				// unpassOkCount=goodsServer.updateAndgetUnpassOkCount(sell.getSellMarkId(),sell.getOrderNum());
				// //此次申请数量加上之前未申请成功的合格品数量为开票数量
				// sell.setSellCount(sell.getSellCount()+unpassOkCount);
			} else if (sell.getSellWarehouse().equals("成品库") && isNeed != null
					&& isNeed.equals("外")) {
				if (sell.getOutOrderNumer() == null
						|| sell.getOutOrderNumer().equals("")) {
					errorMessage = "请选择填写外部部订单号！";
					return ERROR;
				}
			}
		}
		try {
			if (goodsServer.updateGoods(goods, sell, tag, isNeed)) {

				if (tag.equals("dtc")) {
					errorMessage = "调仓成功!";
					url = "goodsAction!getdtcGoods.action?id="
							+ goods.getGoodsId();
					return ERROR;
				}
				if ("saveSell".equals(tag)) {
					successMessage = "出库成功!";
					String status = sell.getHandwordSellStatus();
					if (status == null
							|| (status != null && "同意".equals(status))) {
						errorMessage = "出库成功！";
						return "saveSell";
					} else {
						errorMessage = "出库申请已提交，请等待审批...";
						setUrl("sellAction!findSellByCondition.action?tag=show");
					}
					return "error";
				} else {
					successMessage = "修改成功!";
				}
				return this.tag;// savesell sell_print.jsp
			}
		} catch (Exception e) {
			errorMessage = e.getMessage();
		}
		return ERROR;
	}

	/****
	 * 对接bts系统，扫码出库
	 * 
	 * @return
	 */
	public String outGoodsByBts() {
		Statement sql;
		ResultSet rs;
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
		String dbURL = "jdbc:sqlserver://192.168.2.37:1433;databaseName=yt_BarcodeTrace"; // 连接服务器和数据库sample
		String userName = "tiaomao"; // 默认用户名
		String userPwd = "a123456"; // 密码
		Connection dbConn;
		try {
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			sql = dbConn.createStatement();
			rs = sql
					.executeQuery("select FProductorder,Fproductno from TbDeliveryProducts where FPdbarcode ='"
							+ tag + "'");
			String foriginalPO = "";
			String fproductno = "";
			while (rs.next()) {
				foriginalPO = rs.getString(1);
				fproductno = rs.getString(2);
			}
			dbConn.close();
			// errorMessage = goodsServer.outGoodsByBts(fproductno,
			// foriginalPO,tag);
		} catch (Exception e) {
			e.printStackTrace();
			// String foriginalPO = "DNS-MDO-16-02-08-04";
			// String fproductno = "P913351";
			// goodsServer.outGoodsByBts(fproductno, foriginalPO);
		}

		return "sceanForBTSToPO";
	}

	/**
	 * 重新已出库计算完成率
	 */
	public void reSumCompleteRate() {
		String month = "2015-11";
		boolean b = goodsServer.reSumCompleteRate(month);
		System.out.println(b);

	}

	/**
	 * 下拉list
	 * 
	 * @return
	 */
	public String findSelectList() {
		String message = goodsServer.findSelectList(tag);
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(message);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ERROR;
	}

	/**
	 * 数据汇总
	 * 
	 * @return
	 */
	public String hiuzong() {
		this.pageSize = 15;
		this.setUrl("goodsAction!hiuzong.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("goods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("goods");
		}
		if (null != startDate) {
			request.getSession().setAttribute("startDate", startDate);
		} else {
			startDate = (String) request.getSession().getAttribute("startDate");
		}
		if (null != endDate) {
			request.getSession().setAttribute("endDate", endDate);
		} else {
			endDate = (String) request.getSession().getAttribute("endDate");
		}
		if (null != tiaojian) {
			request.getSession().setAttribute("tiaojian", tiaojian);
		} else {
			tiaojian = (String[]) request.getSession().getAttribute("tiaojian");
		}
		Object[] obj = goodsServer.huizong(goods, startDate, endDate, Integer
				.parseInt(cpage), pageSize, tiaojian);
		if (tiaojian != null && tiaojian.length > 0) {
			for (int i = 0; i < tiaojian.length; i++) {
				errorMessage += "," + tiaojian[i];
			}
			errorMessage = errorMessage.substring(1);
		}
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		listHuizong = (List) obj[1];
		return "huizongOK";// goods_listHuizong.jsp
	}

	// 导出汇总数据
	public String exportEXCEL() {
		if ("goodsDetail".equals(tag)) {
			Map<String, Object> session = ActionContext.getContext()
					.getSession();
			if (goodsAge != null) {
				session.put("goodsAge", goodsAge);
			} else {
				goodsAge = (String) session.get("goodsAge");
			}
			if (goodsAge == null || "".equals(goodsAge)) {
				goodsAge = "0";
			}
			goodsServer.getexportExcel(goods, startDate, endDate, tag, Integer
					.parseInt(goodsAge),pageStatus);
		} else if ("sum".equals(tag)) {
			Object[] obj = goodsServer.huizong(goods, startDate, endDate, 0, 0,
					tiaojian);
			if (obj != null) {
				listHuizong = (List) obj[1];
				goodsServer.exportGoodsPandian(listHuizong, tiaojian);
			}
		}
		return null;
	}

	//
	// 导出月度数据汇总
	public String exportExcelMonth() {
		String[] obj = goodsServer.getseDate();
		startDate = obj[0];
		endDate = obj[1];
		if (goodsAge == null || "".equals(goodsAge)) {
			goodsAge = "0";
		}
		goodsServer.getexportExcel(goods, startDate, endDate, tag, Integer
				.parseInt(goodsAge),pageStatus);
		return null;
	}

	// 重置已入库数量
	public void reSetHasRuKuCount() {
		boolean b = goodsServer.reSetHasRuKuCount();
		MKUtil.writeJSON(b);
	}

	/**
	 * 余料转新原材料入库
	 * 
	 * @return
	 */
	public String toChangeYcl() {
		goods = goodsServer.getGoodsById(id);
		return "yuGoods_changeYcl";
	}

	public String ylchageToYcl() {
		String msg = goodsServer.ylBaoFeiOrChange(id, yuLiaoApply, "change");
		if (msg.equals("true")) {
			errorMessage = "申报成功!";
		} else {
			errorMessage = msg;
		}
		url = "goodsAction!toChangeYcl.action?id=" + id;
		return ERROR;
	}

	/**
	 * 余料申请报废
	 * 
	 * @return
	 */
	public String toBaoFei() {
		goods = goodsServer.getGoodsById(id);
		return "yuGoods_baoFei";
	}

	public String baoFeiYuliao() {
		String msg = goodsServer.ylBaoFeiOrChange(id, yuLiaoApply, "baofei");
		if (msg.equals("true")) {
			errorMessage = "申报成功!";
		} else {
			errorMessage = msg;
		}
		url = "goodsAction!toBaoFei.action?id=" + id;
		return ERROR;
	}

	/**
	 * 余料申请展示
	 * 
	 * @return
	 */
	public String ylApplyShow() {
		if (yuLiaoApply != null) {
			ActionContext.getContext().getSession().put("yuLiaoApply",
					yuLiaoApply);
		} else {// 用来保持分页时带着查询条件
			yuLiaoApply = (YuLiaoApply) ActionContext.getContext().getSession()
					.get("yuLiaoApply");
		}
		Map<Integer, Object> map = goodsServer.findYuLiaoApplysByCondition(
				yuLiaoApply, Integer.parseInt(cpage), pageSize, role);
		yuLiaoApplyList = (List<YuLiaoApply>) map.get(1);// 显示页的技能系数列表
		if (yuLiaoApplyList != null & yuLiaoApplyList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("goodsAction!ylApplyShow.action?role=" + role);
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "yuApply_show";
	}

	/**
	 * 审批余料申请
	 * 
	 * @return
	 */
	public String applyYl() {
		String msg = goodsServer.applyYl(id, tag);
		if (msg.equals("true")) {
			errorMessage = "审批成功";
		} else {
			errorMessage = msg;
		}
		url = "goodsAction!ylApplyShow.action";
		return "error";
	}

	public String deleteYlApply() {
		String msg = goodsServer.deleteYlApply(id);
		if (msg.equals("true")) {
			errorMessage = "删除成功";
		} else {
			errorMessage = msg;
		}
		url = "goodsAction!ylApplyShow.action?role=" + role;
		return "error";
	}

	public String goodsApplyFcStatus() {
		String msg = goodsServer.goodsApplyFcStatus(id);
		if (msg.equals("true")) {
			errorMessage = "申请成功!";
			url = "goodsAction!findGoods.action?cpage=" + cpage;
		} else {
			errorMessage = msg;
		}
		return "error";
	}

	/**
	 * 库存报废申请
	 * 
	 * @return
	 */
	public String addbaofeigoods() {
		try {
			errorMessage = goodsServer.addbaofeigoods(baofeigoods, goods);
			if ("true".equals(errorMessage)) {
				errorMessage = "申请成功!";
				return "baofeishenqin";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}

		return ERROR;
	}

	/**
	 * 显示库存报废表;
	 * 
	 * @return
	 */
	public String showbfgList() {
		int count = goodsServer.getbfgcount();
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		bfgList = goodsServer.showbfgList(Integer.parseInt(cpage), pageSize);
		if (bfgList != null) {
			this.setUrl("goodsAction!showbfgList.action");
			return "showbfgList"; // baofeigoods_listfind.jsp
		}
		errorMessage = "暂时没有报废库存";
		return ERROR;
	}

	public String findbfgList() {
		if (baofeigoods != null) {
			ActionContext.getContext().getSession().put("baofeigoods",
					baofeigoods);
		} else {
			baofeigoods = (BaoFeiGoods) ActionContext.getContext().getSession()
					.get("baofeigoods");
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map = goodsServer.findbfgListByCondition(baofeigoods, Integer
				.parseInt(cpage), pageSize);
		bfgList = (List<BaoFeiGoods>) map.get(1);
		if (bfgList != null && bfgList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("goodsAction!findbfgList.action");
			return "showbfgList";// baofeigoods_listfind.jsp
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
			return ERROR;
		}
	}

	public String fcApplyDetail() {
		goods = goodsServer.getGoodsById(id);
		return "fcApplyDetail";
	}

	public String delbfg() {
		boolean bool = goodsServer.delbfg(baofeigoods);
		if (bool) {
			errorMessage = "删除成功!";
			return "error";
		}
		errorMessage = "删除失败";
		return ERROR;
	}

	// 库存检验管理;
	public String goodsZJ() {
		Map mapgoods = new HashMap();
		if (goods != null) {
			if (goods.getGoodsMarkId() != null) {
				mapgoods.put("goodsMarkId", goods.getGoodsMarkId());
			}
			if (goods.getGoodsLotId() != null) {
				mapgoods.put("goodsLotId", goods.getGoodsLotId());
			}
			if (goods.getGoodHouseName() != null) {
				mapgoods.put("goodHouseName", goods.getGoodHouseName());
			}
		}
		if (mapgoods != null) {
			ActionContext.getContext().getSession().put("mapgoods", mapgoods);
		} else {
			mapgoods = (Map) ActionContext.getContext().getSession().get(
					"mapgoods");
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if ("已到期".equals(tag) || "ydq".equals(pagestatus)) {
			if (tag != null && tag.equals("pingmu")) {
				pageSize = 30;
			}
			map = goodsServer.findgoodsDQ1(goods, role, pageSize, Integer
					.parseInt(cpage));
			ydqList = (List<Goods>) map.get(1);
			if (ydqList != null && ydqList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("goodsAction!goodsZJ.action?role=" + role
						+ "&pagestatus=" + pagestatus + "&tag=" + tag);
			}
			//return "goodsZJ";// goods_zjlist.jsp
		} else if ("将到期".equals(tag) || "jdq".equals(pagestatus)) {
			map = goodsServer.findgoodsDQ(goods, role, pageSize, Integer
					.parseInt(cpage));
			dqList = (List<Goods>) map.get(1);
			if (dqList != null && dqList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("goodsAction!goodsZJ.action?role=" + role
						+ "&pagestatus=" + pagestatus + "&tag=" + tag);
			}
				return "goodsZJ";// goods_zjlist.jsp
		} else if ("未到期".equals(tag) || "wdq".equals(pagestatus)) {
			map = goodsServer.findZJgoodsStore(mapgoods, Integer
					.parseInt(cpage), pageSize, role);
			list = (List<Goods>) map.get(1);
			if (list != null && list.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("goodsAction!goodsZJ.action?role=" + role
						+ "&pagestatus=" + pagestatus + "&tag=" + tag);
			}
		} 
		return "goodsZJ";// goods_zjlist.jsp
	}

	// 确认检验
	public void QRgoodsZJ() {
		errorMessage = goodsServer.QRgoodsZJ(id,null);
		try {
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			MKUtil.writeJSON(e);
			e.printStackTrace();
		}
	}

	// 提醒检验
	public String sendmsg() {
		goodsServer.sendmsg(id);
		errorMessage = "提醒成功!~";
		return ERROR;
	}

	// 备货转库
	public String cxchangeStore() {
		if (productManager != null) {
			ActionContext.getContext().getSession().put("cxpm", productManager);
		} else {
			productManager = (ProductManager) ActionContext.getContext()
					.getSession().get("cxpm");
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map = goodsServer.cxchangeStore(productManager,
				Integer.parseInt(cpage), pageSize);
		pmList = (List<ProductManager>) map.get(1);
		if (pmList != null && pmList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("goodsAction!cxchangeStore.action");
			errorMessage = null;
			return "goods_cxchangeStore";
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
			return "goods_cxchangeStore";
		}
	}

	/**
	 * 查出对应订单可冲销转库的库存
	 * 
	 * @return
	 */
	public String toapplycxzk1() {
		list = goodsServer.findtoapplycxzk(id);
		return "goods_toapplycxzkList";
	}

	/**
	 * 查出对应的冲销转库库存填写冲销数据
	 * 
	 * @return
	 */
	public String toapplycxzk2() {
		Map<Integer, Object> map = goodsServer.gettoapplycxzk(id, goods);
		if (map != null) {
			if (map.get(1) != null) {
				productManager = (ProductManager) map.get(1);
			}
			if (map.get(2) != null) {
				goods = (Goods) map.get(2);
			}
		}
		return "goods_toapplycxzkList2";
	}

	/**
	 * 申请冲销转库
	 * 
	 * @return
	 */
	public String applycxzk() {
		String appmsg = "goodscx_" + id + "_" + goods.getGoodsId();
		errorMessage = (String) ActionContext.getContext().getApplication()
				.get(appmsg);
		if (errorMessage == null) {
			Users loginUser = Util.getLoginUser();
			ActionContext.getContext().getApplication().put(
					appmsg,
					loginUser.getDept() + "的" + loginUser.getName()
							+ "正在冲销此数据，勿重复操作!");
		} else {
			return ERROR;
		}
		try {
			String msg = goodsServer.applycxzk(id, goods);
			if (msg.equals("true")) {
				errorMessage = "申请成功!";
				url = "goodsAction!toapplycxzk2.action?id=" + id
						+ "&&goods.goodsId=" + goods.getGoodsId();
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			errorMessage = e.getMessage();
		}

		ActionContext.getContext().getApplication().put(appmsg, null);
		return ERROR;
	}

	/**
	 * 展示待调仓数据
	 * 
	 * @return
	 */
	public String showDtcGoodsList() {
		this.pageSize = 15;
		this.setUrl("goodsAction!showDtcGoodsList.action");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("dtcgoods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("dtcgoods");
		}
		if (null != startDate) {
			request.getSession().setAttribute("startDate", startDate);
		} else {
			startDate = (String) request.getSession().getAttribute("startDate");
		}

		if (null != endDate) {
			request.getSession().setAttribute("endDate", endDate);
		} else {
			endDate = (String) request.getSession().getAttribute("endDate");
		}
		Object[] obj = goodsServer.findGoods(goods, startDate, endDate, Integer
				.parseInt(cpage), pageSize, role, "dtc", null);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];

		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		String code = user.getCode();

		for (Goods g : list) {
			String warehouse = g.getGoodsClass();
			if (wareHouseAuthService.getOut(code).contains(warehouse)) {
				g.setBout(true);
			}
			if (wareHouseAuthService.getEdit(code).contains(warehouse)) {
				g.setBedit(true);
			}
		}

		return "goods_showDtcList";

	}

	// 在库不良检验申请
	public String bljysq() {
		errorMessage = goodsServer.blsqjy(id);
		if ("true".equals(errorMessage)) {
			errorMessage = "在库不良检验申请成功!";
		}
		return "error";
	}

	/**
	 * 获取待调仓库存
	 * 
	 * @return
	 */
	public String getdtcGoods() {
		goods = goodsServer.getGoodsById(id);
		if(goods.getGoodsCurQuantity()==0){
			errorMessage="此物料已无库存!";
			return "error";
		}
		return "goods_showDtgoods";
	}

	/**
	 * 获取待调仓库存
	 * 
	 * @return
	 */
	public String getdtcChengGoods() {
		goods = goodsServer.getGoodsById(id);
		return "goods_fromChengTowai";
	}

	/**
	 * 跳往半成品领料总页面
	 * 
	 * @return
	 */
	public String toGetbcptotalPage() {
		return "goods_bcptotalPage";
	}

	/**
	 * 半成品领料列表
	 * 
	 * @return
	 */
	public String findBcpllList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("bcpoutgoods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("bcpoutgoods");
		}
		list = goodsServer.findBcpllList(tag, goods);
		return "goods_bcpllList";
	}

	public String findwxzwwList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("bcpoutgoods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("bcpoutgoods");
		}
		list = goodsServer.findBcpllList(tag, goods);
		return "goods_bcpllList2";
	}

	public void bcpOut() {
		try {
			String msg = goodsServer.bcpOut(goods, pwsswords);
			MKUtil.writeJSON(msg);
		} catch (Exception e) {
			// TODO: handle exception
			MKUtil.writeJSON("数据异常!");
		}
	}

	/**
	 * 半成品批量出库
	 * 
	 * @return
	 */
	public String bcpplOut() {
		String msg = goodsServer.bcpplOut(goodsAndProcardIds, lqCounts, tag,
				pwsswords);
		if (msg.equals("true")) {
			errorMessage = "批量出库成功!";
			if (role != null && !role.equals("2")) {
				url = "goodsAction!findBcpllList.action?tag=" + tag;
			} else {
				url = "goodsAction!findwxzwwList.action";
			}
		} else {
			errorMessage = msg;
		}
		return "error";

	}

	/**
	 * 成品可包装列表
	 * 
	 * @return
	 */
	public String findGoodsForBz() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("bzgoods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("bzgoods");
		}
		Object[] obj = goodsServer.findGoods(goods, Integer.parseInt(cpage),
				pageSize);
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		this.url = "goodsAction!findGoodsForBz.action";
		return "goods_bz_list";
	}

	/**
	 * 成品可包装明细
	 * 
	 * @return
	 */
	public String findGoodsDetailForBzSq() {
		goods = goodsServer.getGoodsById(id);
		return "goods_bz_listdetail";
	}

	/****
	 * 添加包装申请
	 * 
	 * @param goods_bzsq
	 * @return
	 */
	public String saveGoodsBzwSq() {
		boolean bool = goodsServer.saveGoodsBzSq(goodsBzsq);
		if (bool) {
			errorMessage = "添加成功!";
			url = "goodsAction!findGoodsbzsq.action";
		} else {
			errorMessage = "添加失败!";
		}
		return ERROR;
	}

	/**
	 * 查询所有包装申请
	 * 
	 * @return
	 */
	public String findGoodsbzsq() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goodsBzsq) {
			request.getSession().setAttribute("goodsBzsq", goodsBzsq);
		} else {
			goodsBzsq = (Goods_bzsq) request.getSession().getAttribute(
					"goodsBzsq");
		}
		Object[] obj = goodsServer.findGoodsbzsq(goodsBzsq, Integer
				.parseInt(cpage), pageSize);
		this.url = "goodsAction!findGoodsbzsq.action";
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		return "goods_bzsq_list";
	}

	/**
	 * 查询所有包装申请
	 * 
	 * @return
	 */
	public String findGoodsbzsqById() {
		goodsBzsq = goodsServer.findGoodsbzsqById(id);
		if (goodsBzsq != null) {
			return "goodsbzsqtoQx";
		} else {
			return ERROR;
		}
	}

	/**
	 * 领取/提交包装申请
	 * 
	 * @return
	 */
	public String updateGoodsBzSqFroLq() {
		String bool = goodsServer.updateGoodsBzSqFroLq(id, cardIds);
		if (bool.equals("true")) {
			errorMessage = "领取/提交成功!";
			url = "goodsAction!findGoodsbzsq.action";
		} else {
			errorMessage = bool;
		}
		return ERROR;
	}

	/**
	 * 查看包装申请详情
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String findGoodsBzSqmx() {
		goodsBzsq_list = goodsServer.findGoodsBzSqmx(id);
		return "goods_sqxq";
	}

	/**
	 * 删除包装申请详情
	 * 
	 * @return
	 */
	public String deletexq() {
		Object[] obj = goodsServer.deletexq(id);
		if (obj[0] != null) {
			url = "goodsAction!findGoodsBzSqmx.action?id="
					+ Integer.parseInt(obj[0].toString());
		}
		this.errorMessage = obj[1].toString();
		return "error";
	}

	/**
	 * 删除包装申请
	 * 
	 * @return
	 */
	public String deleteall() {
		errorMessage = goodsServer.deleteall(id);
		if ("删除成功！".equals(errorMessage)) {
			url = "goodsAction!findGoodsbzsq.action";
		}
		return "error";
	}

	/**
	 * 跳往选择人员页面
	 * 
	 * @return
	 */
	public String toSelectPeople() {
		return "goods_bzsq_people";
	}

	/**
	 * 成品送货列表
	 * 
	 * @return
	 */
	public String findGoodsForSh() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goods) {
			request.getSession().setAttribute("bzgoods", goods);
		} else {
			goods = (Goods) request.getSession().getAttribute("bzgoods");
		}
		pageSize = 50;
		Object[] obj = goodsServer.findGoods(goods, Integer.parseInt(cpage),
				pageSize);
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[0];
		this.url = "goodsAction!findGoodsForSh.action";
		return "goods_shsq_list";
	}

	/****
	 * 送货申请
	 * 
	 * @return
	 */
	public String orderToSonghuo() {
		Object[] object = goodsServer.orderToSonghuo(ids);
		if (object != null) {
			errorMessage = (String) object[2];
			if (errorMessage != null && errorMessage.length() > 0) {
				return ERROR;
			}
			waigouDeliveryGoods = (WaigouDeliveryGoods) object[0];
			lists = (List) object[1];
			return "goods_shsq_list_sq";
		} else {
			errorMessage = "请先选择产品";
			return ERROR;
		}
	}

	/***
	 * 供应商添加送货单
	 * 
	 * @return
	 */
	public String addDeliveryNote() {
		errorMessage = goodsServer.addDeliveryNote(waigouDeliveryGoods,
				list_wdgd);
		if ("".equals(errorMessage)) {
			errorMessage = "送货单添加成功!";
			url = "goodsAction!findDeliveryNoteDetail.action?id="
					+ waigouDeliveryGoods.getId();
		}
		return ERROR;
	}

	/***
	 * 送货单查询(所有/供应商/待打印/待确认)
	 * 
	 * @return
	 */
	public String findDeliveryNote() {
		if (waigouDeliveryGoods != null) {
			ActionContext.getContext().getSession().put("waigouDeliveryGoods",
					waigouDeliveryGoods);
		} else {
			waigouDeliveryGoods = (WaigouDeliveryGoods) ActionContext
					.getContext().getSession().get("waigouDeliveryGoods");
		}
		Object[] object = goodsServer.findDeliveryNote(waigouDeliveryGoods,
				Integer.parseInt(cpage), pageSize, pageStatus, firsttime,
				endtime);
		if (object != null && object.length > 0) {
			wdgList = (List) object[2];// 待打印送货单
			lists = (List) object[0];
			if (lists != null && lists.size() > 0) {
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
		return "goods_shsq_WgDeliveryList";
	}

	/***
	 * 成品送货单明细查询（所有）
	 * 
	 * @return
	 */
	public String findDeliveryNoteDetail_1() {
		if (waigoudGoodsD != null) {
			ActionContext.getContext().getSession().put(
					"WaigouDeliveryGoodsDetail1", waigoudGoodsD);
		} else {
			waigoudGoodsD = (WaigouDeliveryGoodsDetail) ActionContext
					.getContext().getSession()
					.get("WaigouDeliveryGoodsDetail1");
		}
		Object[] object = goodsServer.findDeliveryNoteDetail(waigoudGoodsD,
				Integer.parseInt(cpage), pageSize, pageStatus, firsttime,
				endtime);
		if (object != null && object.length > 0) {
			lists = (List) object[0];
			sumNum = (Float) object[1];
			sumbhsprice = (Float) object[2];
			sumMoney = (Float) object[3];
			if (lists != null && lists.size() > 0) {
				int count = (Integer) object[4];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("goodsAction!findDeliveryNoteDetail_1.action?pageStatus="
								+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "goods_shsq_WgDeliveryListDetail";
	}

	/***
	 * 送货单明细查询
	 * 
	 * @return
	 */
	public String findDeliveryNoteDetail() {
		Object[] object = goodsServer.findDeliveryNoteDetail(id);
		if (object != null && object.length > 0) {
			waigouDeliveryGoods = (WaigouDeliveryGoods) object[0];
			lists = (List) object[1];
			sumNum = (Float) object[2];
			sumbhsprice = (Float) object[3];
			sumMoney = (Float) object[4];
			if (waigouDeliveryGoods == null) {
				errorMessage = "没有找到你要查询的送货单明细,请检查后重试!";
				return ERROR;
			}
		}
		return "goods_shsq_WgDeliveryDetail";
	}

	/***
	 * 送货单打印页面
	 * 
	 * @return
	 */
	public String findDNDetailForPrint() {
		Object[] object = goodsServer.findDeliveryNoteDetail(id);
		if (object != null && object.length > 0) {
			waigouDeliveryGoods = (WaigouDeliveryGoods) object[0];
			lists = (List) object[1];
			sumNum = (Float) object[2];
			sumbhsprice = (Float) object[3];
			sumMoney = (Float) object[4];
			if (waigouDeliveryGoods == null) {
				errorMessage = "没有找到你要查询的送货单明细,请检查后重试!";
				return ERROR;
			}
		}
		return "goods_shsq_WgDeliveryPrint";
	}

	/***
	 * 送货单确认送货
	 * 
	 * @return
	 */
	public String updateDeliveryToPrint() {
		errorMessage = goodsServer.updateDeliveryToPrint(id);
		if ("".equals(errorMessage)) {
			errorMessage = "送货单打印完成!";
		}
		return ERROR;

	}

	/***
	 * 送货单确认送货
	 * 
	 * @return
	 */
	public String updateDeliveryToSh() {
		errorMessage = goodsServer.updateDeliveryToSh(id, pagestatus,
				waigouDeliveryGoods);
		if ("true".equals(errorMessage)) {
			errorMessage = "确认完成,谢谢您的配合!";
			if ("sh".equals(pagestatus)) {
				errorMessage += "祝您一路顺丰!";
			} else if ("qs".equals(pagestatus) || "ys".equals(pagestatus)) {
				errorMessage += "祝您生活愉快!";
			}
		} else {
			errorMessage = "确认失败,请您刷新后重试!谢谢!";
		}
		return ERROR;
	}

	public String getUsersByIds() {
		MKUtil.writeJSON(goodsServer.getUsersByIds(tag));
		return null;
	}

	// 申请锁仓
	public String sqsuocang() {
		errorMessage = goodsServer.sqSuocang(id);
		if ("true".equals(errorMessage)) {
			errorMessage = "申请成功！";
		}
		setUrl("goodsAction!findGoods.action?pagestatus=" + pagestatus);
		return "error";
	}

	// 申请解锁
	public String sqsqjiesuo() {
		errorMessage = goodsServer.sqjiesuo(id);
		if ("true".equals(errorMessage)) {
			errorMessage = "";
		}
		return "error";
	}

	// 进入多条出库
	public String gotoSellGoods() {
		// goods = goodsServer.getGoodsById(id);
		if ("bhgth".equals(pagestatus)) {
			return "goods_bhgth";
		}
		Users user = Util.getLoginUser();
		username = user.getName();
		// orderNumberList = goodsServer.getOrderNumbers(goods);
		// warehouseList = goodsServer.findgoodHouselist();
		l = wareHouseAuthService.getIn(user.getCode());
		return "goods_saveSellMultiterm";
	}

	/**
	 * 通过件号，名称，牌号或规格的少部分字段获取整个外购件原材信息
	 */
	public void getAllNames() {
		if (goods != null
				&& (goods.getGoodsMarkId() != null || goods.getGoodsFullName() != null)) {
			list = goodsServer.getAllNames(goods);
			// YuanclAndWaigjList = yuanclAndWaigjServer
			// .getAllNames(goods);
			MKUtil.writeJSON(list);
		}
	}

	// 根据件号全名库存供料属性查询goods信息
	public void checkGoodsByMarkId() {
		List<Goods> list2 = goodsServer.getGoodsByMarkId(goods);
		MKUtil.writeJSON(list2);
	}

	// 多条手动出库
	public String sellGoodsMultiterm() {
		Iterator<Goods> iterator = list.iterator(); // list保存goods的id
		while (iterator.hasNext()) {
			Goods temp = iterator.next();
			if (null == temp || null == temp.getGoodsId()) {
				iterator.remove();
			}
		}
		Iterator<Sell> inter = sellList.iterator();
		while (inter.hasNext()) {
			Sell sellObj = inter.next();
			if (null == sellObj || null == sellObj.getSellMarkId()) {
				inter.remove();
			}
		}
		for (int i = 0; i < sellList.size(); i++) {
			sellList.get(i).setSellWarehouse(sell.getSellWarehouse());// 库别
			sellList.get(i).setSellSupplier(sell.getSellSupplier());// 供应商
			sellList.get(i).setSellLot(sell.getSellLot()); // 批次

			sellList.get(i).setSellDate(sell.getSellDate());// 出库日期
			sellList.get(i).setStyle(sell.getStyle()); // 出库类型
			sellList.get(i).setSellCompanyName(sell.getSellCompanyName());// 客户
			sellList.get(i).setSellCompanyPeople(sell.getSellCompanyPeople());// 客户负责人
			sellList.get(i).setSellCharger(sell.getSellCharger());// 领料人
			sellList.get(i).setSellPeople(sell.getSellPeople());// ???
			sellList.get(i).setSellSendnum(sell.getSellSendnum());// 送货编号
			sellList.get(i).setOrderNum(sell.getOrderNum());// 订单号（内部）
			sellList.get(i).setOutOrderNumer(sell.getOutOrderNumer());// 订单号（外部）
			sellList.get(i).setSellArtsCard(sell.getSellArtsCard());// 工艺卡号
			sellList.get(i).setSellProMarkId(sell.getSellProMarkId());// 总成件号
			sellList.get(i).setSellSendCost(sell.getSellSendCost());// 运费（同送货编号总费用）
			sellList.get(i).setSellGoodsMore(sell.getSellGoodsMore());// 备注

			// 查看该件号是否存在单价信息,订单产品数量检验
			if (sellList.get(i).getSellWarehouse().equals("成品库")
					&& isNeed != null && isNeed.equals("全")) {
				if (sellList.get(i).getOrderNum() == null
						|| sellList.get(i).getOrderNum().equals("")
						|| sellList.get(i).getOrderNum().equals("未确定")) {
					errorMessage = "请选择内部订单号！";
					return ERROR;
				}
				if (sellList.get(i).getOutOrderNumer() == null
						|| sellList.get(i).getOutOrderNumer().equals("")) {
					errorMessage = "请选择填写外部部订单号！";
					return ERROR;
				}
				String message = goodsServer.findPriceByPartNumber(sellList
						.get(i).getYwmarkId(), sellList.get(i).getSellMarkId(),
						sellList.get(i).getOrderNum(), sellList.get(i)
								.getSellCount(),sell.getSellLot());
				if (!message.equals("true")) {
					errorMessage = message;
					return ERROR;
				}
				// //通过件号和订单号查到之前该订单开票未通过但是合格的产品数量
				// Integer
				// unpassOkCount=goodsServer.updateAndgetUnpassOkCount(sell.getSellMarkId(),sell.getOrderNum());
				// //此次申请数量加上之前未申请成功的合格品数量为开票数量
				// sell.setSellCount(sell.getSellCount()+unpassOkCount);
			} else if (sellList.get(i).getSellWarehouse().equals("成品库")
					&& isNeed != null && isNeed.equals("外")) {
				if (sellList.get(i).getOutOrderNumer() == null
						|| sellList.get(i).getOutOrderNumer().equals("")) {
					errorMessage = "请选择填写外部部订单号！";
					return ERROR;
				}
			}
		}

		try {
			boolean result = goodsServer.sellGoodsMultiterm(list, sellList,
					isNeed);
			if (result) {
				String status = sellList.get(0).getHandwordSellStatus();
				if (status == null || (status != null && "同意".equals(status))) {
					errorMessage = "出库成功！";
					return "sell_printMultiterm";
				} else {
					errorMessage = "出库申请已提交，请等待审批...";
				}
			} else {
				errorMessage = "出库失败";
				setUrl("goodsAction!gotoSellGoods.action");
				return ERROR;
			}
		} catch (Exception e) {
			errorMessage = e.getMessage();

		}
		// if("出库成功!".equals(errorMessage)){
		// return "sell_printMultiterm";
		// }
		setUrl("goodsAction!gotoSellGoods.action");
		return ERROR;
	}

	// 进入手工出库详细
	public String tohangwordSellDetail() {
		// sellList
		sellList = goodsServer.findhandWordByNumber(handwordNumber);
		if (sell == null) {
			sell = sellList.get(0);
		}
		return "sell_printMultiterm";
	}

	// 根据物料需求单出库
	public void sellByManualOrder() {
		if (list != null && list.size() > 0 && sellList != null
				&& sellList.size() > 0 && sell != null) {
			try {
				String result[] = goodsServer.sellByManualOrder(list, sellList,
						sell, null);
				MKUtil.writeJSON(true, result[0], result[1]);
			} catch (Exception e) {
				e.printStackTrace();
				MKUtil.writeJSON(false, "出库失败", e.getMessage());
			}
		}
		MKUtil.writeJSON("参数出错");
	}

	/**
	 * 成品库--》外购件（调仓）
	 * 
	 * @return
	 */
	public String cpChangeWG() {
		goods = goodsServer.getGoodsById(id);
		loginUser = Util.getLoginUser();
		return "cpOneChangeWg";
	}

	/**
	 * 插入调仓记录并进行审批
	 * 
	 * @return
	 */
	public String saveCPChangeWG() {
		cpChangeWg.setEp_status("待审批");
		errorMessage = goodsServer.saveCPOneChangeWG(cpChangeWg);
		return "showAllCPOneChangeWG";
	}

	/**
	 *成品库--》外购件（调仓 )历史记录界面
	 * 
	 * @return
	 */
	public String showAllCPOneChangeWG() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (cpChangeWg != null) {
			request.getSession().setAttribute("cpChangeWg", cpChangeWg);
		} else {
			cpChangeWg = (CpGoodsChangeWG) request.getSession().getAttribute(
					"cpChangeWg");
		}
		if (startDate != null) {
			request.getSession().setAttribute("startDate", startDate);
		} else {
			startDate = (String) request.getSession().getAttribute("startDate");
		}

		if (endDate != null) {
			request.getSession().setAttribute("endDate", endDate);
		} else {
			endDate = (String) request.getSession().getAttribute("endDate");
		}
		Object[] obj = goodsServer.findCPOneChangeWG(cpChangeWg, startDate,
				endDate, Integer.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		sumcount = (Double) obj[2];
		this.setUrl("goodsAction!showAllCPOneChangeWG.action");// 分页跳转地址
		return "goods_HistoryCPChangeWG";
	}

	/**
	 * 导出成品库--》外购件（调仓 )记录
	 * 
	 * @return
	 */
	public String exportCPChangeWG() {
		goodsServer.exportCPChangeWG(cpChangeWg, startDate, endDate);
		return null;
	}

	// 删除（调仓 )记录
	public String deleteOneChange() {
		boolean flag = goodsServer.deleteOneChange(cpChangeWg);
		errorMessage = flag + "";
		return "queryHistoryCPChangeWG";
	}

	// 根据件号、名称、件号、品名 规格 单位 库别 查询入库历史记录
	public void AjaxFindGoodsStore() {
		try {
			List<GoodsStore> gsList = goodsServer.AjaxFindGoodsStore(gs);
			MKUtil.writeJSON(gsList);
		} catch (Exception e) {
			MKUtil.writeJSON("error");
			e.printStackTrace();
		}
	}

	// 根据件号、名称、件号、品名 规格 单位 库别 查询出库历史记录
	public void AjaxFindSell() {
		try {
			List<Sell> sellList = goodsServer.AjaxFindSell(sell);
			MKUtil.writeJSON(sellList);
		} catch (Exception e) {
			MKUtil.writeJSON("error");
			e.printStackTrace();
		}
	}

	// 导出库存质检信息
	public void exportEXCELZj() {
		Map mapgoods = new HashMap();
		if (goods != null) {
			if (goods.getGoodsMarkId() != null) {
				mapgoods.put("goodsMarkId", goods.getGoodsMarkId());
			}
			if (goods.getGoodsLotId() != null) {
				mapgoods.put("goodsLotId", goods.getGoodsLotId());
			}
			if (goods.getGoodHouseName() != null) {
				mapgoods.put("goodHouseName", goods.getGoodHouseName());
			}
		}
		goodsServer.exportEXCELZj(mapgoods, goods, tag, role);
	}

	// 打印单号重新排序
	public String paixuPrintNumber() {
		goodsServer.paixuPrintNumber();
		return "error";
	}

	/**
	 * 查询库存呆滞
	 * 
	 * @return
	 */
	public String findGoodsDull() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (goodsAge != null) {
			session.put("goodsAgeByDell", goodsAge);
		} else {
			goodsAge = (String) session.get("goodsAgeByDell");
		}
		if (goodsAge == null) {
			goodsAge = "0";
		}
		if (goods != null) {
			session.put("ageGoodsByDull", goods);
		} else {
			goods = (Goods) session.get("ageGoodsByDull");
		}
		if (startDate != null) {
			session.put("startDateByDull", startDate);
		} else {
			startDate = (String) session.get("startDateByDull");
		}
		if (endDate != null) {
			session.put("endDateByDull", endDate);
		} else {
			endDate = (String) session.get("endDateByDull");
		}
		Object[] obj = goodsServer.findGoods(goods, startDate, endDate, Integer
				.parseInt(cpage), pageSize, role, pageStatus, Integer
				.parseInt(goodsAge));
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		sumcount = (Double) obj[2];
		this.url = "goodsAction!findGoodsDull.action";
		return "goods_dull";
	}

	// 根据Id获取库存报废申请信息
	public String getbfsqById() {
		baofeigoods = goodsServer.getbfsqById(id);
		return "goods_showbf";
	}

	public List<Goods> getList() {
		return list;
	}

	public void setList(List<Goods> list) {
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GoodsServer getGoodsServer() {
		return goodsServer;
	}

	public void setGoodsServer(GoodsServer goodsServer) {
		this.goodsServer = goodsServer;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Sell getSell() {
		return sell;
	}

	public void setSell(Sell sell) {
		this.sell = sell;
	}

	public WareHouseAuthService getWareHouseAuthService() {
		return wareHouseAuthService;
	}

	public void setWareHouseAuthService(
			WareHouseAuthService wareHouseAuthService) {
		this.wareHouseAuthService = wareHouseAuthService;
	}

	public List getListHuizong() {
		return listHuizong;
	}

	public void setListHuizong(List listHuizong) {
		this.listHuizong = listHuizong;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getOrderNumberList() {
		return orderNumberList;
	}

	public void setOrderNumberList(List<String> orderNumberList) {
		this.orderNumberList = orderNumberList;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getIsNeed() {
		return isNeed;
	}

	public void setIsNeed(String isNeed) {
		this.isNeed = isNeed;
	}

	public YuLiaoApply getYuLiaoApply() {
		return yuLiaoApply;
	}

	public void setYuLiaoApply(YuLiaoApply yuLiaoApply) {
		this.yuLiaoApply = yuLiaoApply;
	}

	public List<YuLiaoApply> getYuLiaoApplyList() {
		return yuLiaoApplyList;
	}

	public void setYuLiaoApplyList(List<YuLiaoApply> yuLiaoApplyList) {
		this.yuLiaoApplyList = yuLiaoApplyList;
	}

	public String getPagestatus() {
		return pagestatus;
	}

	public void setPagestatus(String pagestatus) {
		this.pagestatus = pagestatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public BaoFeiGoods getBaofeigoods() {
		return baofeigoods;
	}

	public void setBaofeigoods(BaoFeiGoods baofeigoods) {
		this.baofeigoods = baofeigoods;
	}

	public List<BaoFeiGoods> getBfgList() {
		return bfgList;
	}

	public void setBfgList(List<BaoFeiGoods> bfgList) {
		this.bfgList = bfgList;
	}

	public List<Goods> getDqList() {
		return dqList;
	}

	public void setDqList(List<Goods> dqList) {
		this.dqList = dqList;
	}

	public List<Goods> getYdqList() {
		return ydqList;
	}

	public void setYdqList(List<Goods> ydqList) {
		this.ydqList = ydqList;
	}

	public List<WareHouse> getWarehouseList() {
		return warehouseList;
	}

	public void setWarehouseList(List<WareHouse> warehouseList) {
		this.warehouseList = warehouseList;
	}

	public List<ProductManager> getPmList() {
		return pmList;
	}

	public void setPmList(List<ProductManager> pmList) {
		this.pmList = pmList;
	}

	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public List<GoodHouse> getGoodHouseList() {
		return goodHouseList;
	}

	public void setGoodHouseList(List<GoodHouse> goodHouseList) {
		this.goodHouseList = goodHouseList;
	}

	public double getSumcount() {
		return sumcount;
	}

	public void setSumcount(double sumcount) {
		this.sumcount = sumcount;
	}

	public boolean isIsall() {
		return isall;
	}

	public void setIsall(boolean isall) {
		this.isall = isall;
	}

	public Goods_bzsq getGoodsBzsq() {
		return goodsBzsq;
	}

	public void setGoodsBzsq(Goods_bzsq goodsBzsq) {
		this.goodsBzsq = goodsBzsq;
	}

	public List<String> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<String> cardIds) {
		this.cardIds = cardIds;
	}

	public Integer[] getIds() {
		return ids;
	}

	public void setIds(Integer[] ids) {
		this.ids = ids;
	}

	public WaigouDeliveryGoods getWaigouDeliveryGoods() {
		return waigouDeliveryGoods;
	}

	public void setWaigouDeliveryGoods(WaigouDeliveryGoods waigouDeliveryGoods) {
		this.waigouDeliveryGoods = waigouDeliveryGoods;
	}

	public WaigouDeliveryGoodsDetail getWaigoudGoodsD() {
		return waigoudGoodsD;
	}

	public void setWaigoudGoodsD(WaigouDeliveryGoodsDetail waigoudGoodsD) {
		this.waigoudGoodsD = waigoudGoodsD;
	}

	public List<WaigouDeliveryGoodsDetail> getList_wdgd() {
		return list_wdgd;
	}

	public void setList_wdgd(List<WaigouDeliveryGoodsDetail> listWdgd) {
		list_wdgd = listWdgd;
	}

	public List getLists() {
		return lists;
	}

	public void setLists(List lists) {
		this.lists = lists;
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

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public List<WaigouDeliveryGoods> getWdgList() {
		return wdgList;
	}

	public void setWdgList(List<WaigouDeliveryGoods> wdgList) {
		this.wdgList = wdgList;
	}

	public Float getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(Float sumMoney) {
		this.sumMoney = sumMoney;
	}

	public Float getSumNum() {
		return sumNum;
	}

	public void setSumNum(Float sumNum) {
		this.sumNum = sumNum;
	}

	public Float getSumbhsprice() {
		return sumbhsprice;
	}

	public void setSumbhsprice(Float sumbhsprice) {
		this.sumbhsprice = sumbhsprice;
	}

	public List<Goods_bzsq> getGoodsBzsq_list() {
		return goodsBzsq_list;
	}

	public void setGoodsBzsq_list(List<Goods_bzsq> goodsBzsqList) {
		goodsBzsq_list = goodsBzsqList;
	}

	public String[] getGoodsAndProcardIds() {
		return goodsAndProcardIds;
	}

	public void setGoodsAndProcardIds(String[] goodsAndProcardIds) {
		this.goodsAndProcardIds = goodsAndProcardIds;
	}

	public Float[] getLqCounts() {
		return lqCounts;
	}

	public void setLqCounts(Float[] lqCounts) {
		this.lqCounts = lqCounts;
	}

	public List<String> getL() {
		return l;
	}

	public void setL(List<String> l) {
		this.l = l;
	}

	public List<Sell> getSellList() {
		return sellList;
	}

	public void setSellList(List<Sell> sellList) {
		this.sellList = sellList;
	}

	public String[] getTiaojian() {
		return tiaojian;
	}

	public void setTiaojian(String[] tiaojian) {
		this.tiaojian = tiaojian;
	}

	public void setCpChangeWg(CpGoodsChangeWG cpChangeWg) {
		this.cpChangeWg = cpChangeWg;
	}

	public CpGoodsChangeWG getCpChangeWg() {
		return cpChangeWg;
	}

	public void setLoginUser(Users loginUser) {
		this.loginUser = loginUser;
	}

	public Users getLoginUser() {
		return loginUser;
	}

	public String getGoodsAge() {
		return goodsAge;
	}

	public void setGoodsAge(String goodsAge) {
		this.goodsAge = goodsAge;
	}

	public GoodsStore getGs() {
		return gs;
	}

	public void setGs(GoodsStore gs) {
		this.gs = gs;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public Integer getHandwordNumber() {
		return handwordNumber;
	}

	public void setHandwordNumber(Integer handwordNumber) {
		this.handwordNumber = handwordNumber;
	}

	public String getPwsswords() {
		return pwsswords;
	}

	public void setPwsswords(String pwsswords) {
		this.pwsswords = pwsswords;
	}

}