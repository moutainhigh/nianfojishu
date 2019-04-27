package com.task.action.ess;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Dao.TotalDao;
import com.task.Server.WareHouseAuthService;
import com.task.Server.ess.GoodsStoreServer;
import com.task.ServerImpl.sop.WaigouWaiweiPlanServerImpl;
import com.task.ServerImpl.yw.ResponseUtil;
import com.task.entity.*;
import com.task.entity.android.OsRecord;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardVo;
import com.task.util.MKUtil;
import com.task.util.Util;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
public class GoodsStoreAction extends ActionSupport implements Serializable {

	/***
	 * 入库Action层
	 * 
	 * @author 钟永林
	 */
	private static final long serialVersionUID = 1L;
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private GoodsStore goodsStore;// 入库表
	private GoodHouse goodHouse;// 仓区表
	private GoodsSummary goodsSum;// 库存汇总
	private List<GoodHouse> goodHouseList;
	private String todayDate;
	private GoodsStoreServer goodsStoreServer;
	private String date;
	private String date2;

	private List<GoodsStore> list = new ArrayList<GoodsStore>();
	private List listIn;// 带入库记录
	private String startDate;// 开始时间
	private String endDate;// 截止时间
	private String tag;// 标识
	private String message;
	private Integer id;
	private Integer processNo;
	private List<String> l;
	private List<GoodsStore> goodsStoreList;
	private File addgoodsStore;
	private String addmachineContentType;// 文件类型
	private String addmachineFileName;// 文件名称
	private String pagestatus;
	private OsRecord record;
	private String markId;
	private Procard procard;//
	private ProcardVo procardVo;//
	private PrintedOutOrder poor;
	private Float zongCount = 0F;
	private Float hsPrice;
	private Float bhsPrice;
	private Float taxPrice;

	private String errorMessage;
	private String successMessage;// 成功信息
	private boolean noLook;
	private WareHouseAuthService wareHouseAuthService;
	private OaAppDetail oadetail;
	private int[] selected;
	private String barCode;
	private Goods goods;
	private Sell sell;
	private String flag;

	public List<GoodHouse> getGoodHouseList() {
		return goodHouseList;
	}

	public void setGoodHouseList(List<GoodHouse> goodHouseList) {
		this.goodHouseList = goodHouseList;
	}

	public GoodHouse getGoodHouse() {
		return goodHouse;
	}

	public void setGoodHouse(GoodHouse goodHouse) {
		this.goodHouse = goodHouse;
	}

	public List<GoodsStore> getGoodsStoreList() {
		return goodsStoreList;
	}

	public void setGoodsStoreList(List<GoodsStore> goodsStoreList) {
		this.goodsStoreList = goodsStoreList;
	}

	public int[] getSelected() {
		return selected;
	}

	public void setSelected(int[] selected) {
		this.selected = selected;
	}

	// 根据时间查询入库数量
	public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (todayDate != null && !todayDate.equals("")) {
			date = todayDate + '-' + "26";
			int mm = Integer.parseInt(todayDate.substring(5, 7));
			int yy = Integer.parseInt(todayDate.substring(0, 4));
			if (mm == 1) {
				mm = 12;
				yy--;
			} else {
				mm--;
			}
			date2 = yy + "-" + mm + '-' + "26";
			if (mm < 10) {
				date2 = todayDate.substring(0, 5) + "0" + (mm) + '-' + "26";
			}
			list = goodsStoreServer.findDatetime(date2, date, Integer
					.parseInt(cpage), pageSize);
			this.setUrl("GoodsStoreAction.action");
			this.cpage = request.getParameter("cpage");
			if ("".equals(cpage) || null == cpage) {
				cpage = 1 + "";
			}

			int count = goodsStoreServer.getCount(date2, date);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			return "execute";
		}
		return null;
	}

	// 查询所有入库
	public String findGoodsStore() {
		date = Util.getDateTime("yyyy-") + "04-" + "26 09:00:00";
		date2 = Util.getLastMonth("yyyy-MM") + "-" + "26 09:00:00";
		goodHouseList = goodsStoreServer.findgoodHouselist();
		Object[] object = goodsStoreServer.findDatetime(
				Integer.parseInt(cpage), pageSize, date, date2);
		if (object != null) {
			list = (List<GoodsStore>) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GoodsStoreAction!findGoodsStore.action");
			}
			return "findAllPrice";
		}
		return ERROR;
	}

	private String jianhao;

	// 根据件号查询所对应的信息
	public String findjianhao() {
		goodHouseList = goodsStoreServer.findgoodHouselist();
		HttpServletRequest request = ServletActionContext.getRequest();
		list = goodsStoreServer.findMarkId(jianhao, date2, date, Integer
				.parseInt(cpage), pageSize);
		this.setUrl("GoodsStoreAction!findjianhao.action");
		this.cpage = request.getParameter("cpage");
		if ("".equals(cpage) || null == cpage) {
			cpage = 1 + "";
		}

		int count = goodsStoreServer.getCountMarkId(jianhao, date2, date);
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		return "execute";
	}

	private List<Tijiangprice> goodmoneylist = new ArrayList<Tijiangprice>();

	// 根据件号才查询出单价
	public String findMakIDmoney() {
		goodmoneylist = goodsStoreServer.findjianhao(jianhao);
		return "findMakIDmoney";
	}

	// 查找要入库处理的条目
	public String findRukuGoodsStore() {
		this.pageSize = 15;
		listIn = goodsStoreServer.findIngoodsStoreList(goodsStore);
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goodsStore) {
			request.getSession().setAttribute("goodsStore", goodsStore);
		} else {
			goodsStore = (GoodsStore) request.getSession().getAttribute(
					"goodsStore");
		}
		if (!"dqr".equals(pagestatus)) {
			goodHouseList = goodsStoreServer.findgoodHouselist();
			Object[] obj = goodsStoreServer.findGoodsStore(goodsStore,
					startDate, endDate, Integer.parseInt(cpage), pageSize);
			int count = (Integer) obj[0];
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("GoodsStoreAction!findRukuGoodsStore.action");
			list = (List) obj[1];
		}
		return "goodsStore_findRuku";
	}

	/**
	 * 成品确认入库给屏幕发送二维码信息
	 */
	public void ZCsendTow() {
		if (id == null || id <= 0)
			MKUtil.writeJSON(false, "待入库信息为空，发送失败！", null);// goodsStorePosition
		goodsStore = goodsStoreServer.getOneGoodsStore(id, "ruku");
		if (goodsStore != null) {
			errorMessage = goodsStoreServer.ZCsendTow(goodsStore);
			if ("true".equals(errorMessage))
				MKUtil.writeJSON(true, "发送成功", null);
			else
				MKUtil.writeJSON(false, errorMessage, null);
		}
		MKUtil.writeJSON(false, "待入库信息为空，发送失败！", null);
	}

	/**
	 * 库管确认入库数量二维码操作
	 */
	public String ZcRuKuBacode() {
		try {
			if (barCode != null) {
				errorMessage = goodsStoreServer.ZcRuKuiBacode(barCode);
				if ("true".equals(errorMessage)) {
					errorMessage = null;
					goodsStore = goodsStoreServer.getOneGoodsStore(Integer
							.parseInt(barCode)
							- WaigouWaiweiPlanServerImpl.mim, tag);
					return "getGoodsStoreOK";
				}
			}
			return "error";
		} catch (Exception e) {
			errorMessage = e.getMessage();
			return ERROR;
		}
	}

	// 查找要入库处理的条目
	public String findRukuGoodsStoreQueRen() {
		if (goodsStore != null)
			ActionContext.getContext().getSession().put("goodsStores",
					goodsStore);
		else
			goodsStore = (GoodsStore) ActionContext.getContext().getSession()
					.get("goodsStores");
		Object[] object = goodsStoreServer.findGoodsStoreQueRen(goodsStore,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			list = (List<GoodsStore>) object[0];
			if (list != null && list.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GoodsStoreAction!findRukuGoodsStoreQueRen.action");
			}
			errorMessage = null;
		} else
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		return "goodsStore_findRukuQueRen";
	}

	// 获取单条入库记录
	public String getOneGoodsStore() {
		goodsStore = goodsStoreServer.getOneGoodsStore(id, tag);
		return "getGoodsStoreOK";// goodsStore_ruku.jsp
	}

	// 更新入库
	public String updateGoodsStore() {

		String msg = "true";
		// if("ruku".equals(tag)){
		// goodsStoreServer.getCanInput(goodsStore.getGoodsStoreMarkId()
		// ,goodsStore.getGoodsStoreLot());
		// }
		if (msg.equals("true")) {
			msg = goodsStoreServer.updateGoodsStore(goodsStore, tag);
			if (msg.equals("true")) {
				return "printGoodsStore";// goodsStore_print.jsp
			} else {
				errorMessage = msg;
				url = "GoodsStoreAction!findRukuGoodsStore.action";
				return ERROR;
			}
		} else {
			errorMessage = msg;
			return "updateERROR";
		}
	}

	/**
	 * 外购入库
	 */
	public String addWgrk() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			goodsStore.setGoodsStoreTime(sdf.format(new Date()));
			goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));

			Map session = ActionContext.getContext().getSession();
			Users user = (Users) session.get(TotalDao.users);
			goodsStore.setGoodsStorePlanner(user.getName());

			goodsStore.setStatus("入库");
			goodsStore.setStyle("批产");
			// goodsStore.setGoodsStoreUnit("件");
			goodsStore.setGoodsStoreMarkId(goodsStore.getGoodsStoreMarkId()
					.replaceAll("\\s", ""));
			goodsStore.setGoodsStoreFormat("");

			// goodsStoreServer.save(goodsStore, record);

			// 更新流水单外购件数量
			// goodsStoreServer.updateWaiProcard(goodsStore.getGoodsStoreMarkId(),
			// goodsStore.getGoodsStoreCount(), "外购件库", goodsStore
			// .getGoodsStoreId());
			MKUtil.writeJSON(true, "入库成功", goodsStore.getGoodsStoreId());
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "入库失败:数据异常", null);
		}
		return null;
	}

	public String sdrkInput() {
		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		l = wareHouseAuthService.getIn(user.getCode());
		goodHouseList = goodsStoreServer.findgoodHouselist();
		if (l.size() == 0) {
			return "noAuth";
		} else if ("pl".equals(pagestatus)) {
			return "plrkInput";// OsRecord_plrkInput.jsp
		}
		if ("multiterm".equals(tag)) {
			return "OsRecord_sdrkInputMultiterm";// OsRecord_sdrkInputMultiterm.jsp
		}
		return "sdrkInput";// OsRecord_sdrkInput.jsp

	}

	/**
	 * 手动入库
	 * 
	 * @return
	 */
	public String addSdrk() {
		try {
			Map session = ActionContext.getContext().getSession();
			Users user = (Users) session.get(TotalDao.users);
			goodsStore.setInputSource("手动入库");
			// goodsStore.setMoney(goodsStore.getHsPrice()
			// * goodsStore.getTaxprice().floatValue());
			if (goodsStoreList == null) {
				goodsStoreList = new ArrayList<GoodsStore>();
				goodsStoreList.add(goodsStore);
			}
			Iterator<GoodsStore> iterator = goodsStoreList.iterator();
			while (iterator.hasNext()) {
				GoodsStore temp = iterator.next();
				if (null == temp) {
					iterator.remove();
				} else if (null == temp.getGoodsStoreMarkId()
						&& null == temp.getGoodsStoreGoodsName()) {
					iterator.remove();
				}
			}
			for (GoodsStore store : goodsStoreList) {
				store.setGoodsStoreTime(Util.getDateTime());
				String markId = store.getGoodsStoreMarkId();
				markId = markId.replaceAll("//s", "");
				for (int n = 10; n < 14; n++) {// 去掉回车和空格
					markId = markId.replaceAll(String.valueOf((char) n), "");
				}
				store.setGoodsStoreMarkId(markId);
				store.setGoodsStorePlanner(user.getName());

				store.setStatus("入库");
				if (store.getGoodsStoreFormat() == null) {
					store.setGoodsStoreMarkFormat("");
				}
				store.setGoodsStoreWarehouse(goodsStore
						.getGoodsStoreWarehouse());
				// store.setKgliao(goodsStore.getKgliao());
				store.setGoodsStoreDate(goodsStore.getGoodsStoreDate());
				store.setGoodsStorelasttime(goodsStore.getGoodsStorelasttime());
				store.setGoodsStoreround(goodsStore.getGoodsStoreround());
				store.setGoodsStoreSupplier(goodsStore.getGoodsStoreSupplier());
				store.setStyle(goodsStore.getStyle());
				store.setGoodsStoreLot(goodsStore.getGoodsStoreLot());
				store.setGoodsStoreProMarkId(goodsStore
						.getGoodsStoreProMarkId());
				store.setGoodsStoreCharger(goodsStore.getGoodsStoreCharger());
				store.setGoodsStorePerson(goodsStore.getGoodsStorePerson());
				store.setOrderId(goodsStore.getOrderId());
				store.setGoodsStoreSendId(goodsStore.getGoodsStoreSendId());
				store.setGoodsStorePlanner(goodsStore.getGoodsStorePlanner());

				store.setGoodsStoreNumber(goodsStore.getGoodsStoreNumber());
				store.setGoodsStoreCompanyName(goodsStore
						.getGoodsStoreCompanyName());
				store.setGoodsStoreLuId(goodsStore.getGoodsStoreLuId());
				store.setBeginning_num(goodsStore.getBeginning_num());
				store.setGoodsStoreArtsCard(goodsStore.getGoodsStoreArtsCard());
				store.setTuhao(goodsStore.getTuhao());
				store.setInputSource(goodsStore.getInputSource());
				// store.setKgliao(goodsStore.getKgliao());
				store.setGoodsStoreUseful(goodsStore.getGoodsStoreUseful()); // 用途
				// store.setGoodsStoreGoodsMore(goodsStore.getGoodsStoreGoodsMore());
				// store.setGoodsStorePosition(goodsStore.getGoodsStorePosition());
			}

			errorMessage = goodsStoreServer.saveBothSgrk(goodsStoreList);
			if ("true".equals(errorMessage)) {
				// 更新流水单外购件数量
				for (GoodsStore store : goodsStoreList) {
					if ("外购件库".equals(store.getGoodsStoreWarehouse())
							|| "备货库".equals(store.getGoodsStoreWarehouse())) {
						goodsStoreServer.updateWaiProcard(store
								.getGoodsStoreMarkId(), store
								.getGoodsStoreCount(), store
								.getGoodsStoreWarehouse(), store
								.getGoodsStoreId());
					}
				}
				MKUtil.writeJSON(true, "入库成功", goodsStore.getGoodsStoreId());
			} else {
				MKUtil.writeJSON(true, errorMessage, goodsStore
						.getGoodsStoreId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "入库失败:" + e.getMessage(), null);
		}
		return null;
	}

	public void text() {
		goodsStoreServer.chuli();
		System.out.println("123");
	}

	// 批量导入;
	public String addSgrk() {
		errorMessage = goodsStoreServer.addSgrk(addgoodsStore, goodsStore,
				pagestatus);
		return ERROR;
	}

	public String editSdrk() {
		try {
			errorMessage = goodsStoreServer.update(goodsStore);
			if ("true".equals(errorMessage))
				MKUtil.writeJSON(true, "修改成功", goodsStore.getGoodsStoreId());
			else
				MKUtil.writeJSON(false, errorMessage, goodsStore
						.getGoodsStoreId());
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "修改失败", goodsStore.getGoodsStoreId());
		}

		return null;
	}

	public String printGoodsStore() {
		goodsStore = goodsStoreServer.get(goodsStore);
		return "findAll";
	}

	public String rukuEdit() {
		goodsStore = goodsStoreServer.get(goodsStore);
		return "rukuEdit";
	}

	/*
	 * 批量打印
	 */
	public String printStorage() {
		noLook = wareHouseAuthService.getAuth1(Util.getLoginUser().getCode());
		if (selected != null && selected.length > 0) {
			list = wareHouseAuthService.printStorage(selected);
			if ("print_goodsStoreList".equals(tag)) {
				Object[] obj = wareHouseAuthService.addPrintedOutOrder(list,
						pagestatus);
				poor = (PrintedOutOrder) obj[0];
				listIn = (List) obj[1];
				errorMessage = (String) obj[2];
				if ("true".equals(errorMessage)) {
					if ("WGRK".equals(pagestatus)) {
						return "findPoorandPo";
					}
					return "print_goodsStore";
				}
				Map<String, Object> session = ActionContext.getContext()
						.getSession();
				session.remove("CPgoodsStore");
				session.remove("goodsStore");
				return "error";
			}
		}
		return "success1";// OsRecord_printStorage.jsp
	}

	public String rukuList() {
		if (goodsStore != null) {
			ActionContext.getContext().getSession().put("GoodsStoreRukuList",
					goodsStore);
		} else {
			goodsStore = (GoodsStore) ActionContext.getContext().getSession()
					.get("GoodsStoreRukuList");
		}

		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		List<String> viewList = wareHouseAuthService.getView(user.getCode());
		// goodsStoreList = goodsStoreServer
		// .findgoodsStoreNo(goodsStore, viewList);// 未打印
		Object[] object = goodsStoreServer.find(pageSize, Integer
				.parseInt(cpage), goodsStore, viewList, pagestatus);

		if (object != null && object.length > 0) {
			list = (List<GoodsStore>) object[0];
			if (list != null && list.size() > 0) {
				for (GoodsStore g : list) {
					zongCount += g.getGoodsStoreCount();
				}
				long sum = (Long) object[1];
				long pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GoodsStoreAction!rukuList.action?pagestatus="
						+ pagestatus);
				errorMessage = null;
			} else {
				errorMessage = "抱歉!您查询的抽检记录不存在!";
			}
		} else {
			errorMessage = "抱歉!没有您查询的抽检记录!";
		}
		List<String> authList = wareHouseAuthService.getEdit(user.getCode());
		List<String> inList = wareHouseAuthService.getIn(user.getCode());
		List<String> delList = wareHouseAuthService.getDel(user.getCode());
		goodHouseList = goodsStoreServer.findgoodHouselist();
		for (GoodsStore g : list) {
			if (authList.contains(g.getGoodsStoreWarehouse())
					&& inList.contains(g.getGoodsStoreWarehouse())) {
				g.setBedit(true);
			}
			if (delList.contains(g.getGoodsStoreWarehouse())
					&& inList.contains(g.getGoodsStoreWarehouse())) {
				g.setIsdel(true);
			}
		}
		if ("updatePrice".equals(pagestatus)) {
			return "rukuList_Price";
		}
		return "rukuList";
	}

	// 打印状态管理
	public String print() {
		goodsStoreServer.printInfor(id);
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", "");
		map.put("message", "");
		MKUtil.writeJSON(map);
		return null;
	}





	/**
	 * 导出Excel
	 * 
	 * @return
	 */
	public String export() {
		if (goodsStore != null) {
			ActionContext.getContext().getSession().put("GoodsStoreRukuList",
					goodsStore);
		} else {
			goodsStore = (GoodsStore) ActionContext.getContext().getSession()
					.get("GoodsStoreRukuList");
		}
		// if(goodsStore==null){
		// goodsStore = new GoodsStore();
		// }
		Map session = ActionContext.getContext().getSession();
		Users user = (Users) session.get(TotalDao.users);
		List<String> viewList = wareHouseAuthService.getView(user.getCode());
		try {
			goodsStoreServer.find(goodsStore, viewList, pagestatus);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage="导出失败:"+e.getMessage();
		}
		return ERROR;

	}

	public String delete() {
		try {
			String msg = goodsStoreServer.delete(goodsStore);
			if (msg.equals("true")) {
				MKUtil.writeJSON(true, "删除成功", null);
			} else {
				MKUtil.writeJSON(false, msg, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "删除失败:" + e.getMessage(), null);
		}
		return null;
	}

	public String getViewAuth() {
		try {
			Map session = ActionContext.getContext().getSession();
			Users user = (Users) session.get(TotalDao.users);
			List<String> viewList = wareHouseAuthService
					.getView(user.getCode());
			MKUtil.writeJSON(true, "获取成功", viewList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "数据获取失败", null);
		}

		return null;
	}

	/***
	 * 根据件号和批次查询入库信息
	 * 
	 * @param markId
	 *            件号
	 * @param storeLot
	 *            批次
	 * @return
	 */
	public void findGoodsStores() {
		List<GoodsStore> list = goodsStoreServer.findGoodsStore(goodsStore
				.getGoodsStoreMarkId(), goodsStore.getGoodsStoreLot());
		MKUtil.writeJSON(list);
	}

	// 添加仓区
	public String addgoodHouse() {
		boolean bool = this.goodsStoreServer.addgoodHouse(goodHouse);
		if (bool) {
			errorMessage = "添加成功!";
		} else {
			errorMessage = "数据异常，请重新添加!";
		}
		return "addgoodHouse";
	}

	public String changeWareHouse() {
		errorMessage = this.goodsStoreServer.changeWareHouse(goods, sell);
		return "error";
	}

	// 查询仓区
	public Object findgoodHouse() {
		if (goodHouse != null) {
			ActionContext.getContext().getSession().put("goodHouse", goodHouse);
		} else {
			goodHouse = (GoodHouse) ActionContext.getContext().getSession()
					.get("goodHouse");
		}
		Object[] object = this.goodsStoreServer.findgoodHouse(goodHouse,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			goodHouseList = (List<GoodHouse>) object[0];
			if (goodHouseList != null && goodHouseList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("GoodsStoreAction!findgoodHouse.action");
			}
			errorMessage = null;
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}

		return "findgoodHouse";

	}

	// 根据id查询仓区
	public String salgoodHouseByid() {
		goodHouse = this.goodsStoreServer.salgoodHouseByid(goodHouse);
		return "salgoodHouseByid";
	}

	// 更新仓区
	public String updategoodHouse() {

		boolean bool = this.goodsStoreServer.updategoodHouse(goodHouse);
		if (bool) {
			this.errorMessage = "修改成功!";
		} else {
			this.errorMessage = "修改失败!";
		}
		return "updategoodHouse";// salgoodHouseByid.jsp
	}

	// 根据件号得到内部、外部订单号
	public void getorder() {
		try {
			List<OrderManager> orderList = goodsStoreServer
					.getorderbymarkId(markId);
			MKUtil.writeJSON(orderList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON("error");
		}

	}

	public void updatezaitumistake() {
		String msg = goodsStoreServer.updatezaitumistake();
		MKUtil.writeJSON(msg);
	}

	/**
	 *扫描库位码开门。
	 * 
	 * @return
	 */
	public void OpenWNByWNNumber() {
		try {
			String msg = goodsStoreServer.OpenWNByWNNumber(barCode);
			if ("true".equals(msg)) {
				MKUtil.writeJSON(true, barCode, null);
			} else {
				MKUtil.writeJSON(false, msg, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String tozaizhiApplyInput() {
		return "goodsStore_zaizhiapplyinput";
	}

	/**
	 * 在制品申请入库获取可入列表
	 * 
	 * @return
	 */
	public String zaizhiApplyInput() {
		listIn = goodsStoreServer.findZaizhiApplyInput(barCode, procard);
		if (listIn == null) {
			errorMessage = "对不起没有找到对应的可入库的在制品!";
		}
		return "goodsStore_zaizhiapplyinput";
	}

	/**
	 * 在制品入库申请明细
	 * 
	 * @return
	 */
	public String zaizhiApplyInputDtial() {
		procardVo = goodsStoreServer.getZaizhiApplyInputDtial(id, processNo);
		return "goodsStore_zaizhiapplyinDetail";
	}

	/**
	 * 申请现场在制品入库
	 * 
	 * @return
	 */
	public String applyzaizhiinput() {
		String wwrk = goodsStoreServer.getwwrk();
		if (wwrk.equals("否")
				&& !goodsStore.getGoodsStoreWarehouse().equals("半成品库")) {
			errorMessage = "手工申请入库只能入半成品库";
			return "error";
		}
		String msg = goodsStoreServer.applyzaizhiinput(goodsStore, id);
		if (msg.equals("true")) {
			errorMessage = "申请成功!";
		} else {
			errorMessage = msg;
		}
		url = "GoodsStoreAction!zaizhiApplyInputDtial.action?id=" + id;
		return "error";
	}

	/**
	 * 在制品待入库确认列表
	 */
	public String sureZaizhiinputApply() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goodsStore) {
			request.getSession().setAttribute("zzgoodsStore", goodsStore);
		} else {
			goodsStore = (GoodsStore) request.getSession().getAttribute(
					"zzgoodsStore");
		}
		listIn = goodsStoreServer.findzzIngoodsStoreList(goodsStore);
		this.pageSize = 15;
		this.setUrl("GoodsStoreAction!sureZaizhiinputApply.action");
		goodHouseList = goodsStoreServer.findgoodHouselist();
		Object[] obj = goodsStoreServer.findzzGoodsStore(goodsStore, startDate,
				endDate, Integer.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		return "GoodsStore_surezzinputapply";
	}

	public String tobcpquickreceiver() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goodsStore) {
			request.getSession().setAttribute("zzgoodsStore", goodsStore);
		} else {
			goodsStore = (GoodsStore) request.getSession().getAttribute(
					"zzgoodsStore");
		}
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		goodsStore.setStatus("待入库");
		// listIn = goodsStoreServer.findzzIngoodsStoreList(goodsStore);
		this.pageSize = 50;
		this.setUrl("GoodsStoreAction!tobcpquickreceiver.action");
		goodHouseList = goodsStoreServer.findgoodHouselist();
		Object[] obj = goodsStoreServer.findzzGoodsStore(goodsStore, startDate,
				endDate, Integer.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		listIn = (List) obj[1];
		return "GoodsStore_bcqquickreceiver";
	}

	public void bcpquickreceive() {
		try {
			GoodsStore old = goodsStoreServer.getGoodsStoreById(goodsStore
					.getGoodsStoreId());
			if (old == null) {
				MKUtil.writeJSON("没有找到对应的待入库数据!");
				return;
			}
			if (!old.getStatus().equals("待入库")) {
				MKUtil.writeJSON("该申请不是待入库状态，入库失败!");
				return;
			}
			old.setGoodHouseName(goodsStore.getGoodHouseName());
			old.setGoodsStorePosition(goodsStore.getGoodsStorePosition());
			if(goodsStore.getGoodsStoreDate()==null||goodsStore.getGoodsStoreDate().length()==0){
				old.setGoodsStoreDate(Util.getDate());
			}
			old.setGoodsStoreDate(goodsStore.getGoodsStoreDate());
			old.setGoodsStorePlanner(goodsStore.getGoodsStorePlanner());
			old.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
			// String msg = goodsStoreServer.bcpquickreceive(goodsStore);
			String msg = goodsStoreServer.updateGoodsStore(old, "ruku");
			;
			if (msg.equals("true")) {
				MKUtil.writeJSON("入库成功!");
			} else {
				MKUtil.writeJSON(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			MKUtil.writeJSON(e.getMessage());
		}
	}

	public String findCPGoodsStoreList() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (null != goodsStore) {
			session.put("CPgoodsStore", goodsStore);
			session.put("pageStatus", pagestatus);
		} else {
			String pagestat = (String) session.get("pageStatus");
			if (pagestatus != null && pagestatus.equals(pagestat)) {
				goodsStore = (GoodsStore) session.get("CPgoodsStore");
			} else {
				session.remove("goodsStore");
			}

		}
		if (null != tag) {
			ActionContext.getContext().getSession().put("tag", tag);
		} else {
			tag = (String) ActionContext.getContext().getSession().get("tag");
		}
		Object[] obj = goodsStoreServer.findCPGoodsStoreList(goodsStore,
				Integer.parseInt(cpage), pageSize, pagestatus, tag);
		list = (List) obj[0];
		int count = (Integer) obj[1];
		pageSize = (Integer) obj[2];
		int pageCount = (count + pageSize - 1) / pageSize;
		setUrl("GoodsStoreAction!findCPGoodsStoreList.action?pagestatus="
				+ pagestatus);
		this.setTotal(pageCount + "");
		return "print_goodsStoreList";
	}

	public String findFromchengToWai() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (null != goodsStore) {
			session.put("CPgoodsStore", goodsStore);
			session.put("pageStatus", pagestatus);
		} else {
			String pagestat = (String) session.get("pageStatus");
			if (pagestatus != null && pagestatus.equals(pagestat)) {
				goodsStore = (GoodsStore) session.get("CPgoodsStore");
			} else {
				session.remove("goodsStore");
			}

		}
		Object[] obj = goodsStoreServer.findFromchengToWai(goodsStore, Integer
				.parseInt(cpage), pageSize);
		list = (List) obj[0];
		int count = (Integer) obj[1];
		pageSize = (Integer) obj[2];
		int pageCount = (count + pageSize - 1) / pageSize;
		setUrl("GoodsStoreAction!findFromchengToWai.action?pagestatus="
				+ pagestatus);
		this.setTotal(pageCount + "");
		return "print_goodsStoreList";
	}

	public void updateGSywmarkId() {
		goodsStoreServer.updateYmrarkId();
	}

	/**
	 * 设变报废入库
	 * 
	 * @return
	 */
	public String sbbf() {
		try {
			String msg = goodsStoreServer.sbbf(id, goodsStore);
			if (msg.equals("true")) {
				errorMessage = "接收成功!";
				url = "procardTemplateGyAction_findProcardsbWasterxcList.action?cpage="
						+ cpage;
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return "error";
	}

	/**
	 * 设变退库
	 * 
	 * @return
	 */
	public String sbtk() {
		try {
			String msg = goodsStoreServer.sbtk(id, goodsStore);
			if (msg.equals("true")) {
				errorMessage = "接收成功!";
				url = "procardTemplateGyAction_findProcardsbWasterxcList.action?cpage="
						+ cpage;
			} else {
				errorMessage = msg;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorMessage = e.getMessage();
		}
		return "error";
	}

	// //goodshuizong（）
	public String goodshuizong() {
		goodsStoreServer.test();
		return null;
	}

	// 查询所有库存汇总信息

	public String findGoodsSum() {
		if (null != goodsSum) {
			ActionContext.getContext().getSession().put("goodsSum", goodsSum);
		} else {
			goodsSum = (GoodsSummary) ActionContext.getContext().getSession()
					.get("goodsSum");
		}
		Object[] obj = goodsStoreServer.findGoodsSum(goodsSum, Integer
				.parseInt(cpage), pageSize, pagestatus);
		list = (List) obj[0];
		int count = (Integer) obj[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		setUrl("GoodsStoreAction!findGoodsSum.action?pagestatus=" + pagestatus);
		this.setTotal(pageCount + "");
		return "GoodsSum";
	}

	// 入库，填写入库详细
	public String storageProducts() {
		// 判断编号是否存在
		String msg = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("oadetail.id", oadetail.getId());
		map.put("storageWay", "single");
		try {
			msg = goodsStoreServer.storageProducts(goodsStore, oadetail);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "入库失败!";
		}
		if ("入库成功！".equals(msg)) {
			ResponseUtil
					.write(
							ServletActionContext.getResponse(),
							msg,
							"oaAppDetailAction!findOADetail.action?tag=oaAppDetail_selectDetail",
							null);// ?storageWay=single
		} else {
			ResponseUtil.write(ServletActionContext.getResponse(), msg,
					"oaAppDetailAction!getOaAppDetail.action", map);// ?storageWay=single
		}
		return null;
	}

	public String findSameProductPrice() {
		list = goodsStoreServer.findSameProductPrice(id, tag);
		return "findSameProductPriceOK";
	}

	/**
	 * 根据件号查询仓区和库位
	 */
	public void findHouseNameByMarkId() {
		String[] strings = goodsStoreServer.findHouseNameByMarkId(markId);
		MKUtil.writeJSON(strings);
	}

	public String waigouWaiting() {
		if (goodsStore != null) {
			ActionContext.getContext().getSession().put("goodsStore",
					goodsStore);
		} else {
			goodsStore = (GoodsStore) ActionContext.getContext().getSession()
					.get("goodsStore");
		}
		Map<Integer, Object> map = goodsStoreServer.waigouWaiting(goodsStore,
				Integer.parseInt(cpage), pageSize);
		goodsStoreList = (List<GoodsStore>) map.get(1);
		if (goodsStoreList != null & goodsStoreList.size() > 0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("GoodsStoreAction!waigouWaiting.action");
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "waigouWaiting_all";
	}

	public String waigouQueren() {
		if (goodsStore.getGoodsStoreId() != null) {
			errorMessage = goodsStoreServer.waigouQueren(goodsStore);
			this.setUrl("GoodsStoreAction!waigouWaiting.action");
			return "error";
		} else {
			errorMessage = "数据异常";
			return "error";
		}
	}

	public String findAllWxRk() {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (null != goodsStore) {
			request.getSession().setAttribute("wxgoodsStore", goodsStore);
		} else {
			goodsStore = (GoodsStore) request.getSession().getAttribute(
					"wxgoodsStore");
		}
		listIn = goodsStoreServer.findAllWxDrk(goodsStore);
		this.pageSize = 15;
		this.setUrl("GoodsStoreAction!findAllWxRk.action");
		goodHouseList = goodsStoreServer.findgoodHouselist();
		Object[] obj = goodsStoreServer.findAllWxRk(goodsStore, startDate,
				endDate, Integer.parseInt(cpage), pageSize);
		int count = (Integer) obj[0];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		list = (List) obj[1];
		return "GoodsStore_wxdrk";
	}

	/**
	 * 调拨单打印(入库)
	 * 
	 * @return
	 */
	public String findChangeGoods() {
		if (goodsStore != null) {
			ActionContext.getContext().getSession().put("gsChange", goodsStore);
		} else {
			goodsStore = (GoodsStore) ActionContext.getContext().getSession()
					.get("gsChange");
		}
		Map<String, Object> map = goodsStoreServer.findChangeGoods(goodsStore,
				Integer.parseInt(cpage), pageSize, tag);
		if (map != null && map.size() > 0) {
			goodsStoreList = (List<GoodsStore>) map.get("1");
			list = (List<GoodsStore>) map.get("2");
			Integer count = (Integer) map.get("3");
			int pageCount = (count + pageSize - 1) / pageSize;
			this.setTotal(pageCount + "");
			setUrl("GoodsStoreAction!findChangeGoods.action");
		}
		return "goodsStore_changeGoods";
	}

	public String toChangeGoodsPage() {
		goodsStoreList = goodsStoreServer.findChangeGoodsBygsId(selected, tag);
		if (selected != null && selected.length > 0) {
			String style = goodsStoreList.get(0).getStyle();
			String changePrintNum = goodsStoreList.get(0).getChangePrint();
			for (GoodsStore store : goodsStoreList) {
				if (!style.equals(store.getStyle())) {
					errorMessage = "请选择相同的入库类型";
					return "error";
				}

				if ((changePrintNum == null && store.getChangePrint() != null)) {
					errorMessage = "请选择相同的打印单号";
					return "error";
				} else if (changePrintNum != null
						&& !changePrintNum.equals(store.getChangePrint())) {
					errorMessage = "请选择相同的打印单号";
					return "error";
				}
			}
			StringBuffer buffer = new StringBuffer();
			for (int sellId : selected) {
				if (buffer.toString().equals("")) {
					buffer.append(sellId);
				} else {
					buffer.append("," + sellId);
				}
			}
			flag = buffer.toString();
			Map<String, Object> map = goodsStoreServer.getCGByPrintedOutOrder(
					goodsStoreList, changePrintNum);
			poor = (PrintedOutOrder) map.get("order");

		}
		return "goodsStore_toChangeGoodsPage";
	}

	public void updateChangeGoods() {
		errorMessage = goodsStoreServer.updateChangeGoods(flag, tag, poor);

		MKUtil.writeJSON(errorMessage);
	}

	public void updatePrice() {
		try {
			boolean bool = goodsStoreServer.updatePrice(id, hsPrice, bhsPrice,
					taxPrice);
			MKUtil.writeJSON(bool);
		} catch (Exception e) {
			MKUtil.writeJSON(false);
		}

	}

	public void xiuFuGoods() {
		goodsStoreServer.save_xiufu();
		System.out.println("修复完了啊 ------------------------");
	}

	// 构造方法
	public GoodsStore getGoodsStore() {
		return goodsStore;
	}

	public void setGoodsStore(GoodsStore goodsStore) {
		this.goodsStore = goodsStore;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(String todayDate) {
		this.todayDate = todayDate;
	}

	public GoodsStoreServer getGoodsStoreServer() {
		return goodsStoreServer;
	}

	public void setGoodsStoreServer(GoodsStoreServer goodsStoreServer) {
		this.goodsStoreServer = goodsStoreServer;
	}

	public List<GoodsStore> getList() {
		return list;
	}

	public void setList(List<GoodsStore> list) {
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getJianhao() {
		return jianhao;
	}

	public void setJianhao(String jianhao) {
		this.jianhao = jianhao;
	}

	public List<Tijiangprice> getGoodmoneylist() {
		return goodmoneylist;
	}

	public void setGoodmoneylist(List<Tijiangprice> goodmoneylist) {
		this.goodmoneylist = goodmoneylist;
	}

	public List getListIn() {
		return listIn;
	}

	public void setListIn(List listIn) {
		this.listIn = listIn;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public WareHouseAuthService getWareHouseAuthService() {
		return wareHouseAuthService;
	}

	public void setWareHouseAuthService(
			WareHouseAuthService wareHouseAuthService) {
		this.wareHouseAuthService = wareHouseAuthService;
	}

	public List<String> getL() {
		return l;
	}

	public void setL(List<String> l) {
		this.l = l;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public OsRecord getRecord() {
		return record;
	}

	public void setRecord(OsRecord record) {
		this.record = record;
	}

	public File getAddgoodsStore() {
		return addgoodsStore;
	}

	public void setAddgoodsStore(File addgoodsStore) {
		this.addgoodsStore = addgoodsStore;
	}

	public String getAddmachineContentType() {
		return addmachineContentType;
	}

	public void setAddmachineContentType(String addmachineContentType) {
		this.addmachineContentType = addmachineContentType;
	}

	public String getAddmachineFileName() {
		return addmachineFileName;
	}

	public void setAddmachineFileName(String addmachineFileName) {
		this.addmachineFileName = addmachineFileName;
	}

	public String getPagestatus() {
		return pagestatus;
	}

	public void setPagestatus(String pagestatus) {
		this.pagestatus = pagestatus;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public Procard getProcard() {
		return procard;
	}

	public void setProcard(Procard procard) {
		this.procard = procard;
	}

	public Integer getProcessNo() {
		return processNo;
	}

	public void setProcessNo(Integer processNo) {
		this.processNo = processNo;
	}

	public ProcardVo getProcardVo() {
		return procardVo;
	}

	public void setProcardVo(ProcardVo procardVo) {
		this.procardVo = procardVo;
	}

	public PrintedOutOrder getPoor() {
		return poor;
	}

	public void setPoor(PrintedOutOrder poor) {
		this.poor = poor;
	}

	public GoodsSummary getGoodsSum() {
		return goodsSum;
	}

	public void setGoodsSum(GoodsSummary goodsSum) {
		this.goodsSum = goodsSum;
	}

	public OaAppDetail getOadetail() {
		return oadetail;
	}

	public void setOadetail(OaAppDetail oadetail) {
		this.oadetail = oadetail;
	}

	public Float getZongCount() {
		return zongCount;
	}

	public void setZongCount(Float zongCount) {
		this.zongCount = zongCount;
	}

	public boolean isNoLook() {
		return noLook;
	}

	public void setNoLook(boolean noLook) {
		this.noLook = noLook;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public Sell getSell() {
		return sell;
	}

	public void setSell(Sell sell) {
		this.sell = sell;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Float getHsPrice() {
		return hsPrice;
	}

	public void setHsPrice(Float hsPrice) {
		this.hsPrice = hsPrice;
	}

	public Float getBhsPrice() {
		return bhsPrice;
	}

	public void setBhsPrice(Float bhsPrice) {
		this.bhsPrice = bhsPrice;
	}

	public Float getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Float taxPrice) {
		this.taxPrice = taxPrice;
	}

}