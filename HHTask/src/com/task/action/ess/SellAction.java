package com.task.action.ess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Dao.TotalDao;
import com.task.Server.WareHouseAuthService;
import com.task.Server.ess.GoodsServer;
import com.task.Server.ess.SellNewServer;
import com.task.Server.ess.SellServer;
import com.task.Server.sop.RunningWaterCardServer;
import com.task.entity.ChengPinTuiHuo;
import com.task.entity.Goods;
import com.task.entity.PrintedOutOrder;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.sop.Procard;
import com.task.entity.sop.RunningWaterCard;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.qd.LogoStickers;
import com.task.util.MKUtil;
import com.task.util.Util;

/**
 * 出库历史表功能Action层
 * 
 * @author 贾辉辉
 * 
 */
@SuppressWarnings("unchecked")
public class SellAction extends ActionSupport {

	private Sell sell;
	private RunningWaterCard runningWaterCard;
	private LogoStickers sticker;// 补料单
	private SellServer sellServer;
	private SellNewServer sellNewServer;
	private RunningWaterCardServer runningWaterCardServer;
	private WareHouseAuthService wareHouseAuthService;
	private GoodsServer goodsServer;
	private List<Sell> list;
	private List<Sell> listSell;
	private List<Goods> goodsList;
	private List list2;
	private Procard procard;
	private List list1;
	private Float getCount;// 本次领料数量
	private WaigouOrder order;
	private List<WaigouPlan> wwPlanList;
	private ChengPinTuiHuo cptu;
	private List<ChengPinTuiHuo> cptuList;
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private Integer id;// 主键
	private String startDate;// 开始时间
	private String endDate;// 截止时间
	private String tag;// 标识
	private String message;
	private String barCode;//条形码
	private String flag;//

	private float qingling;// 请领数量
	private float shifa;// 实发数量
	private int[] selected;
	private String goodsMarkId;// 库存件号
	private String goodsFormat;// 库存规格
	private String errorMessage;// 错误信息
	private String ckType;// 仓库类型
	private File addsell;
	private String status;
	private String cardNumber;//领料人卡号
	private Float num;
	private PrintedOutOrder printedOutOrder;
	private String tags;
	private int ids;
	private Float zongCount=0F;
	public List getList1() {
		return list1;
	}

	public void setList1(List list1) {
		this.list1 = list1;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public int[] getSelected() {
		return selected;
	}

	public void setSelected(int[] selected) {
		this.selected = selected;
	}
	
	public void setSelectedString(String str) {
		if(str!=null && str.length()>0){
			String[] split = str.split(",");
			
			int[] sz = new int[split.length];
			for (int i=0 ; i < split.length; i++) {
				try {
					String value = split[i];
					Integer parseInt = Integer.parseInt(value);
					sz[i] = parseInt;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.selected = sz;
		}
	}

	/***
	 * 更具工号查询个人信息
	 */
	public void getNameByCard() {
		Users us = runningWaterCardServer.getNameByCard(tag);
		MKUtil.writeJSON(us);
	}

	/**
	 * 添加流水卡片信息
	 * 
	 * @return
	 */
	// 查找所有需要领料的记录
	public String findLingliaoList() {
		this.pageSize = 15;
		this.setUrl("sellAction!findLingliaoLIst.action");
		if (message != null && message.length() > 0
				&& "sendSuccess".equals(message)) {
			message = "领料成功";
		}
		runningWaterCard = new RunningWaterCard();
		runningWaterCard.setCardStatus("已发卡");
		runningWaterCard.setReceiveStatus("no");
		Object[] obj = runningWaterCardServer.findRunningWaterCard(
				runningWaterCard, startDate, endDate, Integer.parseInt(cpage),
				pageSize, "");
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		return "findLingliaoListOK";
	}

	// 根据卡找到需要出库的记录
	public String getCradInfor() {
		if ("id".equals(tag)) {
			runningWaterCard = runningWaterCardServer.getCard(id);
			this.id = runningWaterCard.getProcardId();
		} else if ("card".equals(tag)) {
			String rumNumber = runningWaterCard.getCardNum();
			runningWaterCard = runningWaterCardServer.getCardBycardNum(
					rumNumber, "out");
			if (null == runningWaterCard) {// 补料单领料
				sticker = runningWaterCardServer.getLogoSticker(rumNumber,
						"out");
				if (null != sticker) {
					this.id = sticker.getProcardId();
					this.tag = "barcode";
				} else {
					this.message = "该流水卡已领过料或卡绑定信息有误！！";
					return "InputERROR";
				}
			} else {
				if (rumNumber.equals(runningWaterCard.getCardNum())) {
					this.id = runningWaterCard.getProcardId();
				} else {
					this.message = "请先领取该件号的最小批次,更换卡号为"
							+ runningWaterCard.getCardNum() + "的生产周转卡！！";
					return "InputERROR";
				}

			}

		}
		if (null != runningWaterCard || null != sticker) {
			/*
			 * if(runningWaterCard.getCardNum().length()>10){//补料单,根据补料单获取Procard
			 * sticker
			 * =runningWaterCardServer.getLogoSticker(runningWaterCard.getCardNum
			 * (), "out");
			 * procard=runningWaterCardServer.getProcardBySticker(runningWaterCard
			 * .getCardNum(),"out"); //list=sellServer.findSellOutlist(procard);
			 * this.tag="barcode"; }else{
			 * procard=runningWaterCardServer.getProcard
			 * (runningWaterCard,"out");
			 * 
			 * } if(null!=sticker || null!=runningWaterCard.getId()){
			 * list=sellServer.findSellOutlist(procard); }
			 */
			procard = runningWaterCardServer.getProcardById(id);
			Float hasCount = procard.getHascount();
			if (getCount != null) {
				if ((procard.getHascount() != null && getCount > procard
						.getHascount())
						|| (getCount > procard.getFilnalCount())) {
					return null;
				}
				procard.setHascount(getCount);
			}
			Map<Integer, Object> map = sellServer.findSellOutlist(procard,
					ckType,status);
			if (map != null) {
				if (map.get(1) != null) {
					procard = (Procard) map.get(1);
				}
				if (map.get(2) != null) {
					list = (List<Sell>) map.get(2);
				}
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			request.getSession().setAttribute("list", list);
			return "findOutListOK";
		} else {
			message = "该流水卡已领过料或卡绑定信息有误！！";
			return "InputERROR";
		}
	}

	/***
	 * 领料
	 * 
	 * @return
	 */
	public String procardLingliao() {
		procard = runningWaterCardServer.getProcardById(id);
		Float hascount = procard.getHascount();
		if(hascount!=null&&hascount==0){
			//获取下层是否已全部被领出来
			boolean hasOut= sellServer.getHasOut(id);
			if(hasOut){
				message = "该生产周转卡已领料!请勿重复领料!";
				return "sell_ScanGoodsOutList2";
			}
		}
		String ckquanxian = sellServer.getCkTypeByLoginUser("out");
		if (ckType == null) {
			ckType = ckquanxian;
//			ckType = "原材料库外购件库中间库成品库珠海红湖在制品电子产品（总成）电子产品（配件）外库试制库余料库";
			if (ckType == null) {
				ckType = "";
			}
			
		} else if (!ckquanxian.contains(ckType)) {
			if (getCount != null) {
				MKUtil.writeJSON("对不起,您没有" + ckType + "的出库权限");
				return null;
			} else {
				message = "对不起,您没有" + ckType + "的出库权限";
				return "sell_ScanGoodsOutList2";
			}

		} 
		if (getCount != null) {
			if (getCount < 0) {
				return null;
			}
			getCount = (float) Math.floor(getCount);
//			if (procard.isZhHasYcl()) {
//				if (((procard.getKlNumber() - procard.getYhascount()) < getCount)
//						&& (!ckType.contains("原材料库") && ckType.contains("外购件库"))) {
//					getCount = procard.getHascount() - procard.getYhascount();
//					if (getCount == 0) {
//						MKUtil.writeJSON("对不起，请先领取足够的原材料!");
//						return null;
//					}
//				}
//			}
			if ((procard.getHascount() != null && getCount > procard
					.getHascount())
					|| (getCount > procard.getFilnalCount())
					|| (getCount > procard.getKlNumber())) {
				if ((procard.getProcardStyle().equals("总成") || procard
						.getProcardStyle().equals("组合"))
						&& procard.getLingliaoType() != null
						&& procard.getLingliaoType().equals("part")) {
					// 部分领料
				} else {
					return null;
				}
			}
			procard.setGetCount(getCount);
		}
		message = runningWaterCardServer.findMinSelfCard(procard);
		if (procard.getProcardStyle() != null
				&& procard.getProcardStyle().equals("自制")
				&& (ckType == null || !ckType.contains("原材料库"))) {
			message = "对不起，您没有原材料出库权限";
			return "sell_ScanGoodsOutList2";
		}
//		if (procard.isZhHasYcl()
//				&& (procard.getYhascount() != null && procard.getYhascount() >= procard
//						.getHascount()) && !ckType.contains("原材料库")) {
//			
//			message = "对不起，请先领取足够的原材料!";
//			return "sell_ScanGoodsOutList2";
//		}
		if ("true".equals(message)) {
			Map<Integer, Object> map = null;
			if ((procard.getProcardStyle().equals("总成") || procard
					.getProcardStyle().equals("自制"))
					&& procard.getLingliaoType() != null
					&& procard.getLingliaoType().equals("part")) {
				map = sellServer.findSellOutlist2(procard, ckType,status);
			} else {
				map = sellServer.findSellOutlist(procard, ckType,status);
			}
			if (map != null) {
				if (map.get(3) != null) {
					message = map.get(3).toString();
				} else {
					if (map.get(1) != null) {
						procard = (Procard) map.get(1);
						if (procard.getGetCount() != null) {
							procard.setGetCount((float) Math.floor(procard
									.getGetCount()));
						}
					}
					if (map.get(2) != null) {
						list = (List<Sell>) map.get(2);
						if((list == null || list.size() ==0) && ("phone".equals(status) || "code".equals(status))){
							errorMessage = "智能仓储仓库没有存放需要领取的件号!";
							//return "error";
						}
					}
				}

			}
			HttpServletRequest request = ServletActionContext.getRequest();
			request.getSession().setAttribute("list", list);
			if (getCount != null) {// 表示为页面传来为了重新计算领料数量
				getCount = (float) Math.floor(getCount);
				if (map.get(3) != null) {
					MKUtil.writeJSON(map.get(3).toString());
				} else {
					MKUtil.writeJSON(list);
				}
				return null;
			}
		}
		if("phone".equals(status)||"code".equals(status)){
			return "sell_ScanGoodsOutListCode";
		}
		return "sell_ScanGoodsOutList2";
	}
	/**
	 * 原材料下移
	 */
	public void changeycl(){
		sellNewServer.changeycl();
	}
	/**
	 * 弥补新在制品
	 */
	public void newzaizhipin(){
		sellNewServer.newzaizhipin();
	}
	/***
	 * 领料(新原材料->外购件管理模式)
	 * 
	 * @return
	 */
	public String procardLingliaonew() {
		procard = runningWaterCardServer.getProcardById(id);
		Float hascount = procard.getHascount();
		if(hascount!=null&&hascount==0){
			//获取下层是否已全部被领出来
			boolean hasOut= sellServer.getHasOut(id);
			if(hasOut){
				message = "该生产周转卡已领料!请勿重复领料!";
				return "sell_ScanGoodsOutList2";
			}
		}
//		ckType=sellServer.getCkTypeByLoginUser("view");//获取拥有查看权限的仓库r
		if (getCount != null) {
			if (getCount < 0) {
				return null;
			}
			getCount = (float) Math.floor(getCount);//预领数量取整
			procard.setGetCount(getCount);
		}
		message = runningWaterCardServer.findMinSelfCard(procard);
		if ("true".equals(message)) {
			Map<Integer, Object> map = null;
			map = sellNewServer.findSellOutlist(procard, ckType,status);
			if (map != null) {
				if (map.get(3) != null) {
					message = map.get(3).toString();
				} else {
					if (map.get(1) != null) {
						procard = (Procard) map.get(1);
						if (procard.getGetCount() != null) {
							procard.setGetCount((float) Math.floor(procard.getGetCount()));//预领数量取整
						}
					}
					if (map.get(2) != null) {
						list = (List<Sell>) map.get(2);
						if((list == null || list.size() ==0) && "phone".equals(status)){
							errorMessage = "智能仓储仓库没有存放需要领取的件号!";
							return "error";
						}
					}
				}
				
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			request.getSession().setAttribute("list", list);
			if (getCount != null) {// 表示为页面传来为了重新计算领料数量
				getCount = (float) Math.floor(getCount);
				if (map.get(3) != null) {
					MKUtil.writeJSON(map.get(3).toString());
				} else {
					MKUtil.writeJSON(list);
				}
				return null;
			}
		}
		if("phone".equals(status)){
			return "sell_ScanGoodsOutListphone";
		}else if("code".equals(status)){
			return "sell_ScanGoodsOutListCode";
		}
		return "sell_newScanGoodsOutList2";
	}

	/**
	 * 核对实发数量
	 */
	public void checkshifacount() {
		boolean b = sellServer.checkshifacount(goodsMarkId, goodsFormat, shifa);
		MKUtil.writeJSON(b, null, null);
	}
	
	// 领料成功
	public String saveSellByCard() {
//		System.out.println(selected);
//		errorMessage = "领料成功!";
//		return ERROR;
		if (procard != null && procard.getHascount() != null
				&& getCount != null && getCount <= procard.getHascount()) {
			String ckquanxian = sellServer.getCkTypeByLoginUser("out");
			if (ckType == null) {
				ckType = ckquanxian;
//				ckType = "原材料库外购件库中间库成品库珠海红湖在制品电子产品（总成）电子产品（配件）外库试制库余料库";
			} else if (!ckquanxian.contains(ckType)) {
				message = "对不起,您没有" + ckType + "的出库权限";
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Goods> li = (ArrayList) request.getSession().getAttribute(
					"list");
			String lingliaoPeople = procard.getLingliaoren();
			if (lingliaoPeople != null && lingliaoPeople.length() > 0) {
				Procard pr = runningWaterCardServer.getProcardById(procard
						.getId());
				pr.setLingliaoren(lingliaoPeople);
				if ("自制".equals(pr.getProcardStyle())
						|| "单交件".equals(pr.getDanjiaojian())) {
					Float zaizhiCount = sellServer.getZaiZhiCount(pr.getId());// 获取领取之前可用在制品数量
					if (zaizhiCount < 0) {
						errorMessage = "之前批次的在制品不足!";
						return ERROR;
					}
					if (ckType.contains("原材料库")) {
						ckType = "原材料库";
					}
					try{
						// 领料处理
						listSell = sellServer.saveSellListZZ(id, pr, li, tag,
								qingling, shifa, getCount);
						// 存在在制品领料处理工序
						// sellServer.updateProcessNumber(zaizhiCount, getCount, pr
						// .getId());
					}catch (Exception e) {
						// TODO: handle exception
						errorMessage = e.getMessage();
						return ERROR;
					}
				} else {
					try {
						if ((pr.getProcardStyle().equals("总成") || pr
								.getProcardStyle().equals("组合"))
								&& pr.getLingliaoType() != null
								&& pr.getLingliaoType().equals("part")) {// 领料方式为部分领料
							listSell = sellServer.saveSellList2(id, pr, li,
									tag, getCount);
						} else {
							if (pr.getProcardStyle().equals("外购")
									&& ckType.contains("外购件库")) {
								ckType = "外购件库";
							}
							listSell = sellServer.saveSellList(id, pr, li, tag,
									qingling, shifa, getCount, ckType,selected);
						}
					} catch (Exception e) {
						// TODO: handle exception
						errorMessage = e.getMessage();
						return ERROR;
					}
				}
				List l = new ArrayList();
				request.getSession().setAttribute("list", l);
				List<WareBangGoogs>	bangList =	(List<WareBangGoogs>) request.getSession().getAttribute("bangList"+Util.getLoginUser().getId());
				if(bangList!=null && bangList.size()>0){
					sellServer.updateWareBangGoogs(bangList);
				}
				request.getSession().removeAttribute("bangList"+Util.getLoginUser().getId());
				message = "该流水卡领料成功";
			} else {
				errorMessage = "请先确认领料人!";
				return ERROR;
			}
		} else {
			errorMessage = "领料超额!";
			return ERROR;
		}
		return "saveSellListOK";
	}
	// 领料成功
	public String saveSellByCardnew() {
		if (procard != null && procard.getHascount() != null
				&& getCount != null && getCount <= procard.getHascount()) {
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Goods> li = (ArrayList) request.getSession().getAttribute(
			"list");
			String lingliaoPeople = procard.getLingliaoren();
			if (lingliaoPeople != null && lingliaoPeople.length() > 0) {
				Procard pr = runningWaterCardServer.getProcardById(procard
						.getId());
				pr.setLingliaoren(lingliaoPeople);
				try {
					listSell = sellNewServer.saveSellList(id, pr, li, tag, getCount, ckType,selected,goodsList, tags);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					errorMessage = e.getMessage();
					return ERROR;
				}
				List l = new ArrayList();
				request.getSession().setAttribute("list", l);
//				List<WareBangGoogs>	bangList =	(List<WareBangGoogs>) request.getSession().getAttribute("bangList"+Util.getLoginUser().getId());
//				if(bangList!=null && bangList.size()>0){
//					sellServer.updateWareBangGoogs(bangList);
//				}
//				request.getSession().removeAttribute("bangList"+Util.getLoginUser().getId());
				message = "该流水卡领料成功";
			} else {
				errorMessage = "请先确认领料人!";
				return ERROR;
			}
		} else {
			errorMessage = "领料超额!";
			return ERROR;
		}
		return "saveSellListOK";
	}
	
	//仓库领料
	public void saveSellByCode() {
		HttpServletRequest request = ServletActionContext.getRequest();
		List<Goods> li = (ArrayList) request.getSession().getAttribute("list");
		goodsList = li;
		int [] sele = {ids};
		String lingliaoPeople = procard.getLingliaoren();
		if (lingliaoPeople != null && lingliaoPeople.length() > 0) {
			Procard pr = runningWaterCardServer.getProcardById(procard
					.getId());
			pr.setLingliaoren(lingliaoPeople);
			try {
				listSell = sellNewServer.saveSellList(id, pr, li, tag, getCount, ckType, sele, goodsList, tags);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				errorMessage = e.getMessage();
				if ("code".equals(tags))
					MKUtil.writeJSON(true, "领料失败", null);
			}
			if ("code".equals(tags))
				MKUtil.writeJSON(true, "领料成功", null);
		} else {
			errorMessage = "请先确认领料人!";
		}
		MKUtil.writeJSON(true, "领料成功", null);
	}

	// 查看一条工序为外委却未生成外委计划的情况，并生成外委计划
	public void unCreateWaiWei() {
		sellServer.unCreateWaiWei();
	}

	/*
	 * 批量打印
	 */
	public String printStorage() {
		if (selected != null && selected.length > 0) {
			list1 = sellServer.printStorage(selected);
		}
		if("print_sell".equals(tag)){
			Object[] obj = sellServer.addPrintedOutOrder(list1,status);
			printedOutOrder = (PrintedOutOrder) obj[0];
			list1 = (List) obj[1];
			return "print_sell";
		}
		return "success1";
	}

	// 查找和管理出库信息
	public String findSellByCondition() {
		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		String code = user.getCode();
		HttpServletRequest request = ServletActionContext.getRequest();
		list2 = sellServer.getAlllingliaodept();
		if (null != sell) {
			request.getSession().setAttribute("sell", sell);
			request.getSession().setAttribute("status",status);
		} else {
			sell = (Sell) request.getSession().getAttribute("sell");
			String stat = (String) session.get("status");
			if(status != null &&status.equals(stat)){
				sell = (Sell) session.get("sell");
			}else{
				session.remove("sell");
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
//		listSell = sellServer.findOutSellList(sell);
//		for (Sell se : listSell) {
//			String warehouse = se.getSellWarehouse();
//			if (wareHouseAuthService.getOut(code).contains(warehouse)
//					|| wareHouseAuthService.getEdit(code).contains(warehouse)) {
//				se.setBedit(true);
//			}
//		}
		this.pageSize = 15;
		
		Object[] obj = sellServer.findSellByCondition(sell, startDate, endDate,
				Integer.parseInt(cpage), pageSize);
		if(!"".equals(obj[2])){
			errorMessage = (String)obj[2];
			return "error";
		}
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("sellAction!findSellByCondition.action?tag="+tag+"&status=selllog");
		list = (List) obj[1];
		list1=wareHouseAuthService.findgoodHouselist();
		List<String> OutList = wareHouseAuthService.getOut(code);
		List<String> editList = wareHouseAuthService.getEdit(code);
		List<String> delList = wareHouseAuthService.getDel(user.getCode());
		for (Sell se : list) {
			if(se.getSellCount()!=null){
				zongCount +=se.getSellCount();
			}
			String warehouse = se.getSellWarehouse();
			if (OutList.contains(warehouse)
					|| editList.contains(warehouse)) {
				se.setBedit(true);
			}
			if(delList.contains(warehouse)){
				se.setIsdel(true);
			}
		}
		return "findSellByConditionOK";//sell_findList.jsp
	}

	public String getOneSell() { 
		sell = sellServer.getOneSellById(id, tag);
		if ("deleteThisSell".equals(this.tag)) {
			sellServer.deleteSellById(sell);
		}
		return tag;//sell_updateSell.jsp
	}

	public String updateSell() {
		try {
			if (sellServer.updateSell(sell)) {
				return "deleteThisSell";
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return ERROR;
	}
	
	public String toTuikuSell(){
		sell = sellServer.getOneSellById(id, tag);
		return "sell_TuikuSell";
	}
	
	public String tuiKuSell() {
		try {
			errorMessage = sellServer.tuiKuSell(sell);
			if ("true".equals(errorMessage)) {
				return "deleteThisSell";//sellAction!findSellByCondition.action?cpage=${cpage}
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return ERROR;
	}
	

	// 导出数据
	public String exportEXCEL() {
//		sellServer.explorExcel(sell, startDate, endDate, message,tag);
		sellServer.explorExcelByPoi(sell, startDate, endDate, message,tag);
		return null;
	}

	// 打印状态管理
	public String print() {
		sellServer.printInfor(id);
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", "");
		map.put("message", "");
		MKUtil.writeJSON(map);
		return null;
	}

	/***
	 * 根据件号和批次查询出库信息
	 * 
	 * @return
	 */
	public void findSell() {
		List list = sellServer
				.findSell(sell.getSellMarkId(), sell.getSellLot());
		MKUtil.writeJSON(list);
	}
	
	public void daoru(){
		sellServer.daoru();
		System.out.println("90");
	}
	
	//批量出库导入;
	public String PladdSell(){
		errorMessage = sellServer.PladdSell(addsell, status);
		if(errorMessage.equals("true")){
			errorMessage ="导入成功!";
		}
		return "error";
	}
	/**
	 * 库管确认给屏幕发送二维码
	 */
	public void querensendTow(){
		if(id!=null && id>0){
			errorMessage = 	sellServer.queRenSendTow(id);
			if("true".equals(errorMessage)) {
				MKUtil.writeJSON(true, errorMessage, null);  
			}else {
				MKUtil.writeJSON(false, errorMessage, null);  
			}
		}
	}
	/**
	 * 传值，暂时存起来;
	 * @return
	 */
	public void chuanzhi(){
		HttpServletRequest request = ServletActionContext.getRequest();
		WareBangGoogs bang =	sellServer.findWareBangBygoodsId(id);
		bang.setLqNumber(num);
		List<WareBangGoogs>	bangList = (List<WareBangGoogs>) request.getSession().getAttribute("bangList"+Util.getLoginUser().getId());
		if(bangList == null ){
			bangList  = new ArrayList<WareBangGoogs>();
		}
		if(bangList.size()>0){
			boolean bool = true;
			for (WareBangGoogs bang1 : bangList) {
				if(bang1.getId() ==bang.getId()){
					bang1.setLqNumber(num);
					bool = false;
					break;
				}
			}
			if(bool){
				bangList.add(bang);
			}
		}else{
			bangList.add(bang);
		}
		request.getSession().setAttribute("bangList"+Util.getLoginUser().getId(), bangList);
		errorMessage =	sellServer.oaCloseWN(bang);
		if("true".equals(errorMessage)){
			MKUtil.writeJSON(true, errorMessage, null);
		}else{
			MKUtil.writeJSON(false, errorMessage, null);
		}
	}
	/**
	 * 外委扫码领料
	 * @return
	 */
	public String tofindWwclDetailByCode(){
		return "sell_findWwclDetailByCode";
	}
	
	/**
	 * 展示外委领料配套清单
	 * @return
	 */
	public String showWwPlanoutDetail(){
		Map<Integer, Object> map;
		try {
			map = sellServer.showWwPlanoutDetail(barCode,id);
		} catch (Exception e) {
			errorMessage=e.getMessage();
			return ERROR;
		}
		if(map!=null){
			if(map.get(1)!=null){
				order =(WaigouOrder)map.get(1);
				if(order!=null&&order.getStatus().equals("待核对")
						||order.getStatus().equals("待通知")
						||order.getStatus().equals("待确认")
						||order.getStatus().equals("待审核")){
					errorMessage="订单状态为"+order.getStatus()+",不可领料!";
					return "error";
				}
			}
			if(map.get(2)!=null){
				wwPlanList =  (List<WaigouPlan>) map.get(2);
			}
			if(map.get(3)!=null){
				listSell =(List<Sell>) map.get(3);
			}
		}
		return "sell_showWwPlanoutDetail";
	}
	
	/**
	 * 展示外委领料清单
	 * @return
	 */
	public String showWwclDetail2(){
		Map<Integer,Object> map = sellServer.getWwclDetail2(wwPlanList,id);
		if(map!=null){
			if(map.get(1)!=null){
				order =(WaigouOrder)map.get(1);
			}
			if(map.get(2)!=null){
				goodsList = (List<Goods>) map.get(2);
			}
			if(map.get(3)!=null){
				Boolean b  = (Boolean)map.get(3);
				if(b!=null&&b){
					tag="true";
				}else{
					tag="false";
				}
			}
			if(map.get(4)!=null){
				listSell =(List<Sell>) map.get(4);
			}
		}
		return "sell_showwwclDetail";
	}
	/**
	 * 展示外委领料清单
	 * @return
	 */
	public String showWwclDetail(){
		Map<Integer,Object> map = sellServer.getWwclDetail(barCode,id);
		if(map!=null){
			if(map.get(1)!=null){
				order =(WaigouOrder)map.get(1);
			}
			if(map.get(2)!=null){
				goodsList = (List<Goods>) map.get(2);
			}
			if(map.get(3)!=null){
				Boolean b  = (Boolean)map.get(3);
				if(b!=null&&b){
					tag="true";
				}else{
					tag="false";
				}
			}
			if(map.get(4)!=null){
				listSell =(List<Sell>) map.get(4);
			}
		}
		return "sell_showwwclDetail";
	}
	
	public String outwwcl(){
		try {
			String msg = sellServer.outwwcl(order,goodsList,selected);
			if(msg.equals("true")){
				errorMessage ="出库成功!";//showWwPlanoutDetail|showWwclDetail2
				url = "sellAction!showWwPlanoutDetail.action?barCode="+barCode;
			}else{
				errorMessage=msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage =e.getMessage();
			e.printStackTrace();
		}
		return ERROR;
	}
	
	public void backwxSell(){//sellAction!backwxSell.action
		List<Sell> sellList = sellServer.getSellList();
		StringBuffer sb=new StringBuffer();
		int i=0;
		if(sellList!=null&&sellList.size()>0){
			for(Sell sell:sellList){
				try {
					String msg=sellServer.backwxSell(sell);
					if(msg.length()>0){
						if(sb.length()==0){
							sb.append(msg);
						}else{
							sb.append("<br/>"+msg);
						}
					}else{
						i++;
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(sb.length()==0){
						sb.append(e.getMessage());
					}else{
						sb.append("<br/>"+e.getMessage());
					}
				}
				
			}
		}
		MKUtil.writeJSON("成功"+i+"个<br/>"+sb.toString());
	}
	
	/**
	 * 查询调拨出库记录
	 * @return
	 */
	public String findChangeGoods(){
		if(sell!=null) {
			ActionContext.getContext().getSession().put("changeSell", sell);
		}else {
			sell = (Sell) ActionContext.getContext().getSession().get("changeSell");
		}
		
		Map<Integer, Object> map = sellServer.findChangeGoods(null,Integer.parseInt(cpage),pageSize,sell);
		list = (List<Sell>) map.get(1);//未打印
		listSell = (List<Sell>) map.get(2);//已打印--分页
		Integer count = (Integer) map.get(3);
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		setUrl("sellAction!findChangeGoods.action");
		return "goods_findChangePrint";
	}
	/**
	 * 删除异常出库
	 * @return
	 */
	public void deleteYcSell(){//sellAction!deleteYcSell.action
		List<Sell> sellList = sellServer.getycSellList();
		if(sellList!=null&&sellList.size()>0){
			List<Sell> deleteSellList = new ArrayList<Sell>();
			String markId="";
			String selfCard="";
			String orderNumber ="";
			String backMsg="";
			for(Sell sell :sellList){
				boolean iseuals1 = false;
				boolean iseuals2 = false;
				boolean iseuals3 = false;
				if(!markId.equals(sell.getSellMarkId())){
					markId=sell.getSellMarkId();
					iseuals1=true;
				}
				if(!selfCard.equals(sell.getSellArtsCard())){
					selfCard=sell.getSellArtsCard();
					iseuals2=true;
				}
				if(!orderNumber.equals(sell.getOrderNum())){
					orderNumber=sell.getOrderNum();
					iseuals3=true;
				}
				if(iseuals1||iseuals2||iseuals3){
					backMsg+= sellServer.deleteYcSell(deleteSellList);
					deleteSellList = new ArrayList<Sell>();
				}
				deleteSellList.add(sell);
			}
			backMsg+= sellServer.deleteYcSell(deleteSellList);
		
			MKUtil.writeJSON(backMsg);
		}
	}
	
	/**
	 * 前往调拨单打印
	 * @return
	 */
	public String toPrintChangeGoods() {
		if(selected==null || selected.length<=0) {
			setErrorMessage("请选择调拨记录");
			return "error";
		}else {
			List<Integer> list = new ArrayList<Integer>();
			StringBuffer buffer = new StringBuffer();
			for (int integer : selected) {
				list.add(integer);
				if (buffer.toString().equals("")) {
					buffer.append(integer);
				}else {
					buffer.append(","+integer);
				}
			}
			listSell = sellServer.findchangeGoodsBySellIds(list, tag);
			String printNum=null;
			String style=null;
			if(listSell!=null && listSell.size()>0) {
				printNum = listSell.get(0).getChangePrint();
				style = listSell.get(0).getStyle();
				for (Sell sell : listSell) {
					if(!sell.getStyle().equals(style)) {
						errorMessage = "请选择相同的出库类型";
						return "error";
					}
					if(printNum==null && sell.getChangePrint()!=null) {
						errorMessage = "请选择相同的打印单号";
						return "error";
					}else if(printNum!=null && !printNum.equals(sell.getChangePrint())){
						errorMessage = "请选择相同的打印单号";
						return "error";
					}
				}
			}
			flag = buffer.toString();
			printedOutOrder = sellServer.getPOOByCond(flag, printNum);
			printedOutOrder.setDbStyle(style);
		}
		return "goods_printChangePage";
	}
	
	public void updateChangeSellPrintStatus() {
		errorMessage = sellServer.updateChangeSellPrintStatus(flag, tag,printedOutOrder);
		MKUtil.writeJSON(errorMessage);
	}
	//成品退货申请
	public String ChengPinTuiHuoSq(){
		errorMessage =	sellServer.ChengPinTuiHuoSq(sell);
		if("true".equals(errorMessage)){
			errorMessage ="成品退货申请成功!~";
			url = "sellAction!getOneSell.action?id="+sell.getSellId()+"&tag=updateThisSell&status=tuihuo";
		}
		return "error";
	}
	public String findAllChengPinTuiHuo(){
		if(cptu!=null) {
			ActionContext.getContext().getSession().put("cptu", cptu);
		}else {
			cptu = (ChengPinTuiHuo) ActionContext.getContext().getSession().get("cptu");
		}
		Object[] obj = sellServer.findAllChengPinTuiHuo(cptu, pageSize, Integer.parseInt(cpage), status,startDate,endDate);
		cptuList =(List<ChengPinTuiHuo>) obj[0];
		int count =(Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		setUrl("sellAction!findAllChengPinTuiHuo.action");
		return "cpth_showList";
	}
	public String toCpthPrint(){
		cptu = sellServer.toCpthPrint(id);
		return "cpth_print";
	}
	public void exprotcpth(){
		sellServer.exprotcpth(cptu, status, startDate, endDate);
	}
	public List<Sell> getList() {
		return list;
	}

	public void setList(List<Sell> list) {
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

	public Sell getSell() {
		return sell;
	}

	public void setSell(Sell sell) {
		this.sell = sell;
	}

	public RunningWaterCard getRunningWaterCard() {
		return runningWaterCard;
	}

	public void setRunningWaterCard(RunningWaterCard runningWaterCard) {
		this.runningWaterCard = runningWaterCard;
	}

	public SellServer getSellServer() {
		return sellServer;
	}

	public void setSellServer(SellServer sellServer) {
		this.sellServer = sellServer;
	}

	public RunningWaterCardServer getRunningWaterCardServer() {
		return runningWaterCardServer;
	}

	public void setRunningWaterCardServer(
			RunningWaterCardServer runningWaterCardServer) {
		this.runningWaterCardServer = runningWaterCardServer;
	}

	public Procard getProcard() {
		return procard;
	}

	public void setProcard(Procard procard) {
		this.procard = procard;
	}

	public GoodsServer getGoodsServer() {
		return goodsServer;
	}

	public void setGoodsServer(GoodsServer goodsServer) {
		this.goodsServer = goodsServer;
	}

	public WareHouseAuthService getWareHouseAuthService() {
		return wareHouseAuthService;
	}

	public void setWareHouseAuthService(
			WareHouseAuthService wareHouseAuthService) {
		this.wareHouseAuthService = wareHouseAuthService;
	}

	public LogoStickers getSticker() {
		return sticker;
	}

	public void setSticker(LogoStickers sticker) {
		this.sticker = sticker;
	}

	public void setListSell(List<Sell> listSell) {
		this.listSell = listSell;
	}

	public List<Sell> getListSell() {
		return listSell;
	}

	public float getQingling() {
		return qingling;
	}

	public void setQingling(float qingling) {
		this.qingling = qingling;
	}

	public float getShifa() {
		return shifa;
	}

	public void setShifa(float shifa) {
		this.shifa = shifa;
	}

	public Float getGetCount() {
		return getCount;
	}

	public void setGetCount(Float getCount) {
		this.getCount = getCount;
	}

	public String getGoodsMarkId() {
		return goodsMarkId;
	}

	public void setGoodsMarkId(String goodsMarkId) {
		this.goodsMarkId = goodsMarkId;
	}

	public String getGoodsFormat() {
		return goodsFormat;
	}

	public void setGoodsFormat(String goodsFormat) {
		this.goodsFormat = goodsFormat;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCkType() {
		return ckType;
	}

	public void setCkType(String ckType) {
		this.ckType = ckType;
	}

	public File getAddsell() {
		return addsell;
	}

	public void setAddsell(File addsell) {
		this.addsell = addsell;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WaigouOrder getOrder() {
		return order;
	}

	public void setOrder(WaigouOrder order) {
		this.order = order;
	}

	public Float getNum() {
		return num;
	}

	public void setNum(Float num) {
		this.num = num;
	}

	public List<Goods> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public SellNewServer getSellNewServer() {
		return sellNewServer;
	}

	public void setSellNewServer(SellNewServer sellNewServer) {
		this.sellNewServer = sellNewServer;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public PrintedOutOrder getPrintedOutOrder() {
		return printedOutOrder;
	}

	public void setPrintedOutOrder(PrintedOutOrder printedOutOrder) {
		this.printedOutOrder = printedOutOrder;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getIds() {
		return ids;
	}

	public void setIds(int ids) {
		this.ids = ids;
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public List<WaigouPlan> getWwPlanList() {
		return wwPlanList;
	}

	public void setWwPlanList(List<WaigouPlan> wwPlanList) {
		this.wwPlanList = wwPlanList;
	}
	

	public Float getZongCount() {
		return zongCount;
	}

	public void setZongCount(Float zongCount) {
		this.zongCount = zongCount;
	}

	public ChengPinTuiHuo getCptu() {
		return cptu;
	}

	public void setCptu(ChengPinTuiHuo cptu) {
		this.cptu = cptu;
	}

	public List<ChengPinTuiHuo> getCptuList() {
		return cptuList;
	}

	public void setCptuList(List<ChengPinTuiHuo> cptuList) {
		this.cptuList = cptuList;
	}
	
}
