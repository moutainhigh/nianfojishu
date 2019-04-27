package com.task.ServerImpl.ess;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.InsuranceGoldServer;
import com.task.Server.WageStandardServer;
import com.task.Server.caiwu.core.CorePayableServer;
import com.task.Server.ess.GoodsStoreServer;
import com.task.Server.sop.ProcardServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.sop.WaigouWaiweiPlanServerImpl;
import com.task.entity.*;
import com.task.entity.android.OsRecord;
import com.task.entity.caiwu.baobiao.AccountsDate;
import com.task.entity.caiwu.core.CorePayable;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.project.QuotedPrice;
import com.task.entity.project.QuotedPriceCost;
import com.task.entity.sop.*;
import com.task.entity.sop.qd.LogoStickers;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.wlWeizhiDt.WlWeizhiDt;
import com.task.util.DateUtil;
import com.task.util.Util;
import com.task.util.UtilTong;
import com.tast.entity.zhaobiao.ZhUser;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
public class GoodsStoreServerImpl implements GoodsStoreServer {
	private StringBuffer strbu = new StringBuffer();
	private static Integer duankou = 8885;
	private TotalDao totalDao;
	private ProcardServer procardServer;
	private WageStandardServer wss; // 工资模板
	private InsuranceGoldServer igs; // 五险一金Server层
	private CorePayableServer corePayableServer;
	private static final Double SECONDS = 619200.0;

	public CorePayableServer getCorePayableServer() {
		return corePayableServer;
	}

	public void setCorePayableServer(CorePayableServer corePayableServer) {
		this.corePayableServer = corePayableServer;
	}

	// 在静态方法调用totalDao
	private static TotalDao createTotol() {
		// 获得totalDao
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		GoodsStoreServerImpl acc = new GoodsStoreServerImpl();
		acc.setTotalDao(totalDao);
		return totalDao;
	}

	// 根据时间查询所对应的入库的信息
	public List findDatetime(String setDate, String endDatetime, int pageNo,
			int pageSize) {
		if (setDate != null && endDatetime != null) {
			String hql = "from GoodsStore where  goodsStoreTime between '"
					+ setDate + " 09:00:00' and '" + endDatetime
					+ " 09:00:00' and goodsStoreWarehouse='成品库'";
			return this.totalDao.findAllByPage(hql, pageNo, pageSize);
		}
		return null;
	}

	// 根据时间查询所对应的入库的信息总数量
	public Integer getCount(String setDate, String endDatetime) {
		if (setDate != null && endDatetime != null) {
			String hql = "from GoodsStore where  goodsStoreTime between '"
					+ setDate + " 09:00:00' and '" + endDatetime
					+ " 09:00:00' and goodsStoreWarehouse='成品库'";
			return this.totalDao.getCount(hql);
		}
		return null;
	}

	/***
	 * 根据时间查询所对应的入库的信息
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Object[] findDatetime(int pageNo, int pageSize, String startDate,
			String endDatetime) {
		String hql = "select goodsStoreMarkId,goodsStoreCompanyName,sum(goodsStoreCount) from GoodsStore where  goodsStoreTime between '"
				+ startDate
				+ "' and '"
				+ endDatetime
				+ "' and goodsStoreWarehouse='成品库' group by goodsStoreMarkId,goodsStoreCompanyName";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.query(hql).size();
		Object[] o = { list, count };
		return o;

	}

	// 根据件号查询所对应的信息
	public List findMarkId(String jianhao, String setDate, String endDate,
			int pageNo, int pageSize) {
		if (jianhao != null && setDate != null && endDate != null) {
			String hql = "from GoodsStore where goodsStoreMarkId='" + jianhao
					+ "'   and goodsStoreTime between '" + setDate
					+ " 09:00:00' and '" + endDate
					+ " 09:00:00' and goodsStoreWarehouse='成品库'";
			return this.totalDao.findAllByPage(hql, pageNo, pageSize);
		}
		return null;
	}

	// 根据件号查询所对应的信息总数量
	public Integer getCountMarkId(String jianhao, String setDate, String endDate) {
		if (jianhao != null && setDate != null && endDate != null) {
			String hql = "from GoodsStore where goodsStoreMarkId='" + jianhao
					+ "'   and goodsStoreTime between '" + setDate
					+ " 09:00:00' and '" + endDate
					+ " 09:00:00' and goodsStoreWarehouse='成品库'";
			return this.totalDao.getCount(hql);
		}
		return null;
	}

	// 根据件号才查询出单价
	public List findjianhao(String jianhao) {
		if (jianhao != null) {
			String hql = "from Tijiangprice where pricemarkId='" + jianhao
					+ "'";
			return this.totalDao.query(hql);
		}
		return null;
	}

	public List findIngoodsStoreList(GoodsStore gs) {
		if (gs == null) {
			gs = new GoodsStore();
		}
		String hql_ = " status='待入库' and goodsStoreWarehouse in('成品库','备货库')";
		String hql = totalDao.criteriaQueries(gs, hql_);
		List<GoodsStore> list = totalDao.find(hql);
		for (GoodsStore g : list) {
			if(g.getYwmarkId() == null || g.getYwmarkId().length() == 0){
				Procard procard = (Procard) totalDao.getObjectByCondition(
						" from Procard where markId = ? and selfCard =?  ", g
								.getGoodsStoreMarkId(), g.getGoodsStoreLot());
				if (procard != null) {
					g.setYwmarkId(procard.getYwMarkId());
				}
			}
			
		}
		return list;
	}

	public Object[] findGoodsStore(GoodsStore goodsStore, String startDate,
			String endDate, Integer cpage, Integer PageSize) {
		String hql = "from GoodsStore where (status<>'待入库' or status is NULL)  and goodsStoreWarehouse in('成品库','备货库') order by goodsStoreDate desc";
		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		if (null != goodsStore) {
			hql = totalDao.criteriaQueries(goodsStore, "goodsStoreTime",
					between, "")
					+ " and (status<>'待入库' or status is NULL ) and goodsStoreWarehouse in('成品库','备货库')  order by goodsStoreDate desc";
		}
		Object[] procardAarr = new Object[2];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		if(list!=null&&list.size()>0){
			for (Object obj : list) {
				GoodsStore g = (GoodsStore) obj;
				if (g.getYwmarkId() == null || g.getYwmarkId().length() == 0) {
					Procard procard = (Procard) totalDao.getObjectByCondition(
							" from Procard where markId = ? and selfCard =?  ", g
							.getGoodsStoreMarkId(), g.getGoodsStoreLot());
					if (procard != null) {
						g.setYwmarkId(procard.getYwMarkId());
					}
				}
			}
		}
		procardAarr[0] = count;
		procardAarr[1] = list;
		return procardAarr;
	}

	@Override
	public Object[] findGoodsStoreQueRen(GoodsStore gs, Integer cpage,
			Integer PageSize) {
		if (gs == null)
			gs = new GoodsStore();
		String hql = totalDao.criteriaQueries(gs, null);
		hql += " and status = '待入库' and goodsStoreWarehouse in('成品库','备货库') order by goodsStoreId desc";// order
		// by
		// goodsStoreId
		// desc
		List<GoodsStore> list = totalDao.findAllByPage(hql, cpage, PageSize);
		for (GoodsStore goo : list) {
			if (goo.getGoodsStorePosition() != null
					&& !"".equals(goo.getGoodsStorePosition())) {
				String kuwei = goo.getGoodsStorePosition().replaceAll(",", "")
						.replace(" ", "");
				WarehouseNumber wn = (WarehouseNumber) totalDao
						.getObjectByCondition(
								"from WarehouseNumber where number = ? ", kuwei);
				if (wn != null && wn.getIp() != null && !"".equals(wn.getIp())) {
					goo.setKuweiId(wn.getId());
				}
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public GoodsStore getOneGoodsStore(Integer id, String tag) {
		if (null != id) {
			return (GoodsStore) totalDao.getObjectById(GoodsStore.class, id);
		}
		return null;
	}

	public String updateGoodsStore(GoodsStore goodsStore, String tag) {
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录!";
		}
		GoodsStore gs = (GoodsStore) totalDao.getObjectById(GoodsStore.class,
				goodsStore.getGoodsStoreId());
		if (gs.getStatus() != null && gs.getStatus().equals("入库")) {
			return "此零件已入库,请勿重新入库!";
		}
		String hadqx =GoodsStoreServerImpl.checkquanxian(user,gs.getGoodsStoreWarehouse(),"in",totalDao);
		if(!hadqx.equals("true")){//判断是否有入库权限
			return hadqx;
		}
		if (gs.getStyle() != null && gs.getStyle().equals("冲销转库")) {
			// 冲销转库
			BeanUtils.copyProperties(goodsStore, gs, new String[] {
					"goodsStoreWarehouse", "goodsStoreCount",
					"goodsStoreAdminName", "goodsStoreArtsCard", "status",
					"planID", "applyTime", "orderId", "izabId", "processNo",
					"sqUsersName", "sqUsrsId", "sqUsersCode", "sqUsersdept",
					"style" });
			gs.setStatus("入库");
			gs.setGoodsStoreTime(Util.getDateTime());
			Float returnCount = 0f;
			if (goodsStore.getGoodsStoreCount() != null
					&& gs.getGoodsStoreCount() != null) {
				returnCount = gs.getGoodsStoreCount()
						- goodsStore.getGoodsStoreCount();
				if (returnCount < 0) {
					return "确认入库数量超过申请入库数量申请失败";
				} else if (returnCount > 0) {// 多出来的返回
					GoodsStore newgs = new GoodsStore();
					BeanUtils.copyProperties(gs, newgs, new String[] { "id" });
					newgs.setGoodsStoreCount(returnCount);
					newgs.setStatus("待入库");
					newgs.setGoodsStoreTime(null);
					totalDao.save(newgs);
				}
			}
			gs.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
			InternalZsAboutBh izab = (InternalZsAboutBh) totalDao
					.getObjectById(InternalZsAboutBh.class, gs.getIzabId());
			if (izab == null) {
				return "对不起没有找到对应的冲销关系!";
			}
			ProductManager pm = (ProductManager) totalDao
					.getObjectByCondition(
							"from ProductManager where id=(select zsProductId from ProductZsAboutBh where id=?)",
							izab.getPzsAboutbhId());
			if (pm == null) {
				return "对不起没有找到对应的冲销订单!";
			}
			izab.setApplyzkCount((izab.getApplyzkCount() - goodsStore
					.getGoodsStoreCount()));
			if (izab.getZkCount() == null) {
				izab.setZkCount(goodsStore.getGoodsStoreCount());
			} else {
				izab.setZkCount((izab.getZkCount() + goodsStore
						.getGoodsStoreCount()));
			}
			pm.setCxzkuApplyCount((pm.getCxzkuApplyCount() - goodsStore
					.getGoodsStoreCount()));
			if (pm.getCxzkuCount() == null) {
				pm.setCxzkuCount(goodsStore.getGoodsStoreCount());
			} else {
				pm.setCxzkuCount((pm.getCxzkuCount() + goodsStore
						.getGoodsStoreCount()));
			}
			if (pm.getAllocationsNum() == null) {
				pm.setAllocationsNum(0f);
			}
			pm.setAllocationsNum((pm.getAllocationsNum() + goodsStore
					.getGoodsStoreCount()));
			totalDao.update(izab);
			totalDao.update(pm);
			// // // 计算订单入库率
			Integer rorderId = pm.getOrderManager().getId();
			String rhql = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
			String rhql2 = "select sum(allocationsNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
			Float rpronum = (Float) totalDao.getObjectByCondition(rhql,
					rorderId);
			Float rproallocationsNum = (Float) totalDao.getObjectByCondition(
					rhql2, rorderId);
			OrderManager rorderManager = (OrderManager) totalDao.getObjectById(
					OrderManager.class, rorderId);
			if (rpronum > 0) {
				rorderManager.setInputRate(rproallocationsNum / rpronum * 100);
			} else {
				rorderManager.setInputRate(0F);
			}
			totalDao.update(rorderManager);
			totalDao.update(gs);
			// 判断库存情况
			String hqlgoods = "from Goods where goodsMarkId='"
					+ goodsStore.getGoodsStoreMarkId() + "' and goodsLotId='"
					+ goodsStore.getGoodsStoreLot()
					+ "' and goodsClass='成品库' and goodsCustomer=?"
					+ " and neiorderId='" + gs.getNeiorderId() + "'";
			String clientName = rorderManager.getClientName() == null ? ""
					: rorderManager.getClientName();
			List listGoods = totalDao.query(hqlgoods, clientName);
			if (listGoods != null && listGoods.size() > 0) {
				Goods goods = (Goods) listGoods.get(0);
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						+ goodsStore.getGoodsStoreCount());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				totalDao.update(goods);
			} else {
				Goods goods = new Goods();
				goods.setNeiorderId(pm.getOrderManager().getOrderNum());
				goods.setWaiorderId(pm.getOrderManager().getOutOrderNumber());
				goods.setGoodsBarcode(gs.getGoodsStoreArtsCard());
				goods.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
				goods.setYwmarkId(goodsStore.getYwmarkId());
				goods.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());
				goods.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				goods.setGoodsCustomer(rorderManager.getClientName());
				goods.setTuhao(gs.getTuhao());
				goods.setBanBenNumber(gs.getBanBenNumber());
				if (null == goodsStore.getGoodsStoreFormat()) {
					goods.setGoodsFormat("");
				} else {
					goods.setGoodsFormat(goodsStore.getGoodsStoreFormat());
				}
				goods.setGoodsLotId(goodsStore.getGoodsStoreLot());
				if (null == goodsStore.getGoodsStoreUnit()) {
					goods.setGoodsUnit("件");
				} else {
					goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
				}
				goods.setGoodsChangeTime(goodsStore.getGoodsStoreDate());
				goods.setGoodsClass("成品库");
				goods.setYwmarkId(gs.getYwmarkId());
				goods.setNeiorderId(gs.getNeiorderId());
				goods.setGoodHouseName(gs.getGoodHouseName());
				goods.setGoodsPosition(gs.getGoodsStorePosition());
				if(gs.getHsPrice()!=null){
					goods.setGoodsBuyPrice(gs.getHsPrice().floatValue());// 批次 含税单价
				}
				if(gs.getGoodsStorePrice()!=null){
					goods.setGoodsBuyBhsPrice(gs.getGoodsStorePrice().floatValue());// 批次 不含税单价
				}
				if (gs.getTaxprice() != null) {
					goods.setTaxprice(gs.getTaxprice().floatValue());// 税率
				}
				Float[] avgprices = getAccountsDate(gs);
				goods.setGoodshsPrice(avgprices[0]);
				goods.setGoodsPrice(avgprices[1]);
				totalDao.save(goods);
			}
			return "转库成功!";
		} else if ("外协退料入库".equals(gs.getStyle())) {
			BeanUtils.copyProperties(goodsStore, gs, new String[] {
					"goodsStoreWarehouse", "goodsStoreCount",
					"goodsStoreAdminName", "goodsStoreArtsCard", "status",
					"planID", "applyTime", "orderId", "izabId", "processNo",
					"sqUsersName", "sqUsrsId", "sqUsersCode", "sqUsersdept",
					"style" });
			gs.setStatus("入库");
			gs.setGoodsStoreTime(Util.getDateTime());
			Float returnCount = 0f;
			if (goodsStore.getGoodsStoreCount() != null
					&& gs.getGoodsStoreCount() != null) {
				returnCount = gs.getGoodsStoreCount()
						- goodsStore.getGoodsStoreCount();
				if (returnCount < 0) {
					return "确认入库数量超过申请入库数量申请失败";
				} else if (returnCount > 0) {// 多出来的返回
					GoodsStore newgs = new GoodsStore();
					BeanUtils.copyProperties(gs, newgs, new String[] { "id" });
					newgs.setGoodsStoreCount(returnCount);
					newgs.setStatus("待入库");
					newgs.setGoodsStoreTime(null);
					totalDao.save(newgs);
				}
			}
			gs.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
			// 判断库存情况
			String hqlgoods = "from Goods where goodsMarkId='"
					+ goodsStore.getGoodsStoreMarkId() + "' and goodsLotId='"
					+ goodsStore.getGoodsStoreLot() + "' and goodsClass='外协库' ";

			List listGoods = totalDao.query(hqlgoods);
			if (listGoods != null && listGoods.size() > 0) {
				Goods goods = (Goods) listGoods.get(0);
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						+ goodsStore.getGoodsStoreCount());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				goods.setGoodsChangeTime(Util.getDateTime());
				totalDao.update(goods);
			} else {
				Goods goods = new Goods();
				goods.setNeiorderId(gs.getNeiorderId());
				goods.setWaiorderId(gs.getWaiorderId());
				goods.setGoodsBarcode(gs.getGoodsStoreArtsCard());
				goods.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
				goods.setYwmarkId(goodsStore.getYwmarkId());
				goods.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());
				goods.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				goods.setTuhao(gs.getTuhao());
				goods.setBanBenNumber(gs.getBanBenNumber());
				if (null == goodsStore.getGoodsStoreFormat()) {
					goods.setGoodsFormat("");
				} else {
					goods.setGoodsFormat(goodsStore.getGoodsStoreFormat());
				}
				goods.setGoodsLotId(goodsStore.getGoodsStoreLot());
				if (null == goodsStore.getGoodsStoreUnit()) {
					goods.setGoodsUnit("件");
				} else {
					goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
				}
				goods.setGoodsChangeTime(goodsStore.getGoodsStoreDate());
				goods.setGoodsClass("外协库");
				goods.setYwmarkId(gs.getYwmarkId());
				goods.setNeiorderId(gs.getNeiorderId());
				goods.setGoodHouseName(gs.getGoodHouseName());
				goods.setGoodsPosition(gs.getGoodsStorePosition());
				goods.setGoodsBuyPrice(gs.getHsPrice().floatValue());// 批次 含税单价
				goods.setGoodsBuyBhsPrice(gs.getGoodsStorePrice().floatValue());// 批次 不含税单价
				goods.setGoodsSupplier(gs.getGoodsStoreSupplier());// 供应商
				goods.setSupplierId(gs.getGysId());// 供应商Id
				if (gs.getTaxprice() != null) {
					goods.setTaxprice(gs.getTaxprice().floatValue());// 税率
				}
				Float[] avgprices = getAccountsDate(gs);
				goods.setGoodshsPrice(avgprices[0]);
				goods.setGoodsPrice(avgprices[1]);
				totalDao.save(goods);
				totalDao.update(gs);
			}
			return "转库成功!";
		}
		Procard procard = null;
		if (goodsStore.getProcardId() != null) {
			procard = (Procard) totalDao.getObjectById(Procard.class,
					goodsStore.getProcardId());
		} else {
			String sql = "from Procard where markId=? and selfCard =?";
//			if (gs.getNeiorderId() != null && gs.getNeiorderId().length() > 0) {
//				sql += " and orderNumber ='" + gs.getNeiorderId() + "'";
//			}
			procard = (Procard) totalDao.getObjectByCondition(sql, goodsStore
					.getGoodsStoreMarkId(), goodsStore.getGoodsStoreLot());
		}
		if (gs.getStyle() != null && gs.getStyle().equals("半成品转库")) {
			if (gs.getStatus().equals("入库")) {
				return "此零件已入库,请勿重新入库!";
			}
			if (procard != null) {
				gs.setStatus("入库");
				gs.setGoodsStoreTime(Util.getDateTime());
				gs.setGoodsStoreDate(goodsStore.getGoodsStoreDate());
				Float returnCount = 0f;
				// if (gs.getGoodsStoreCount() >
				// goodsStore.getGoodsStoreCount()) {
				// return "本次入库不支持分批入库!";
				// }
				if (goodsStore.getGoodsStoreCount() != null
						&& gs.getGoodsStoreCount() != null) {
					Float oldcount = gs.getGoodsStoreCount();
					returnCount = gs.getGoodsStoreCount()
							- goodsStore.getGoodsStoreCount();
					if (returnCount < 0) {
						return "确认入库数量超过申请入库数量申请失败";
					}
					if (returnCount > 0) {// 多出来的返回
						GoodsStore newgs = new GoodsStore();
						BeanUtils.copyProperties(gs, newgs,
								new String[] { "id" });
						newgs.setGoodsStoreCount(returnCount);
						newgs.setStatus("待入库");
						newgs.setGoodsStoreTime(null);
						totalDao.save(newgs);
					}
					gs.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
					// 获取下个工序号
					Integer pno = (Integer) totalDao.getObjectByCondition(
							"select processNO from ProcessInfor where procard.id=? and processNO>"
									+ gs.getProcessNo(), procard.getId());
					// 减去对应的申请数量
					if (procard.getZaizhiApplyZk() != null) {
						procard.setZaizhiApplyZk(procard.getZaizhiApplyZk()
								- gs.getGoodsStoreCount());
					} else {
						procard.setZaizhiApplyZk(0f);
					}
					// 本次入库数
					if (procard.getZaizhizkCount() == null) {
						procard.setZaizhizkCount(goodsStore
								.getGoodsStoreCount());
					} else {
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								+ goodsStore.getGoodsStoreCount());
					}
					totalDao.update(procard);
					if (pno != null) {
						// 找到对应的外委待激活序列待入库数据
						WaigouWaiweiPlan wwplan = (WaigouWaiweiPlan) totalDao
								.getObjectByCondition(
										"from WaigouWaiweiPlan where status='待入库' and markId=? and selfCard=? and number=? and processNo like '"
												+ pno + "%'", goodsStore
												.getGoodsStoreMarkId(),
										goodsStore.getGoodsStoreLot(), oldcount);
						if (wwplan != null) {
							if (wwplan.getNumber() > gs.getGoodsStoreCount()) {
								Float cjCount = wwplan.getNumber()
										- gs.getGoodsStoreCount();
								WaigouWaiweiPlan newwwplan = new WaigouWaiweiPlan();
								BeanUtils.copyProperties(wwplan, newwwplan,
										new String[] { "id", "baoxiaodans" });
								newwwplan.setNumber(cjCount);
								newwwplan.setBeginCount(cjCount);
								totalDao.save(newwwplan);
								wwplan.setNumber(gs.getGoodsStoreCount());
								wwplan.setBeginCount(gs.getGoodsStoreCount());
							}
							wwplan.setZzRukCount(gs.getGoodsStoreCount());
							wwplan.setStatus("待激活");
							totalDao.update(wwplan);
						}

					} else {// 查询上层首工序是否外委
						Procard sc = procard.getProcard();
						if (sc != null) {
							ProcessInfor processInfor = (ProcessInfor) totalDao
									.getObjectByCondition(
											"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and"
													+ " processNO=(select min(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
											sc.getId(), sc.getId());
							if (processInfor != null
									&& processInfor.getProductStyle() != null
									&& processInfor.getProductStyle().equals(
											"外委")) {
								// 查询对应的外委待激活序列
								WaigouWaiweiPlan waigouwaiweiplan = (WaigouWaiweiPlan) totalDao
										.getObjectByCondition(
												"from WaigouWaiweiPlan where status='待入库' and procardId=? and "
														+ "(processNo=? or processNo like '"
														+ processInfor
																.getProcessNO()
														+ "%') and beginCount>="
														+ goodsStore
																.getGoodsStoreCount()
														+ " order by beginCount",
												sc.getId(), processInfor
														.getProcessNO()
														+ "");
								if (waigouwaiweiplan != null) {
									// 查询下层所有生产件最后一道工序是否入库
									List<Procard> sonList = totalDao
											.query(
													"from Procard where procard.id=? and (sbStatus is null or sbStatus!='删除') and (procardStyle='自制' or (procardStyle='外购' and needProcess='yes'))",
													sc.getId());
									boolean b = true;
									for (Procard son : sonList) {
										// 获取最大工序号
										Integer maxProcessno = (Integer) totalDao
												.getObjectByCondition(
														"select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')",
														son.getId());
										// 查看库存数量是否足够
										Float rkCount = (Float) totalDao
												.getObjectByCondition(
														"select sum(goodsCurQuantity) from Goods where goodsClass='委外库' and goodsMarkId=? and goodsLotId=? and processNo=? ",
														son.getMarkId(), son
																.getSelfCard(),
														maxProcessno);
										if (rkCount < waigouwaiweiplan
												.getBeginCount()) {
											b = false;
										}
									}
									if (b) {
										waigouwaiweiplan.setStatus("待激活");
										totalDao.update(waigouwaiweiplan);
									}
								}
							}
						}

					}
					Integer goodsId = null;
					// 判断库存情况
					String hqlgoods = "from Goods where goodsMarkId='"
							+ goodsStore.getGoodsStoreMarkId()
							+ "' and goodsLotId='"
							+ goodsStore.getGoodsStoreLot()
							+ "' and goodsStyle='半成品转库' and processNo="
							+ gs.getProcessNo() + " and goodsClass='"
							+ gs.getGoodsStoreWarehouse()
							+ "' and goodHouseName='" + gs.getGoodHouseName()
							+ "' and goodsPosition='"
							+ gs.getGoodsStorePosition() + "'";
					if (gs.getNeiorderId() != null
							&& gs.getNeiorderId().length() > 0) {
						hqlgoods += " and neiorderId='" + gs.getNeiorderId()
								+ "'";
					} else {
						hqlgoods += " and (neiorderId is null or neiorderId='')";
					}
					List listGoods = totalDao.find(hqlgoods);
					if (listGoods != null && listGoods.size() > 0) {
						Goods goods = (Goods) listGoods.get(0);
						goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
								+ goodsStore.getGoodsStoreCount());
						goods.setGoodsPosition(goodsStore
								.getGoodsStorePosition());
						totalDao.update(goods);
						goodsId = goods.getGoodsId();
					} else {
						Goods goods = new Goods();
						goods.setGoodsBarcode(gs.getGoodsStoreArtsCard());
						goods.setGoodsBeginQuantity(goodsStore
								.getGoodsStoreCount());
						goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
						goods.setGoodsCurQuantity(goodsStore
								.getGoodsStoreCount());
						goods.setGoodsCustomer(goodsStore
								.getGoodsStoreCompanyName());
						goods.setGoodsFullName(goodsStore
								.getGoodsStoreGoodsName());
						goods.setGoodsPosition(goodsStore
								.getGoodsStorePosition());
						goods.setProcessNo(gs.getProcessNo());
						goods.setProcessName(gs.getProcessName());
						goods.setBanBenNumber(gs.getBanBenNumber());
						goods.setGoodsStyle("半成品转库");
						if (null == goodsStore.getGoodsStoreFormat()) {
							goods.setGoodsFormat("");
						} else {
							goods.setGoodsFormat(goodsStore
									.getGoodsStoreFormat());
						}
						goods.setGoodsLotId(goodsStore.getGoodsStoreLot());
						if (null == goodsStore.getGoodsStoreUnit()) {
							goods.setGoodsUnit("件");
						} else {
							goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
						}
						goods
								.setGoodsChangeTime(goodsStore
										.getGoodsStoreDate());
						goods.setGoodsClass(gs.getGoodsStoreWarehouse());
						goods.setGoodHouseName(gs.getGoodHouseName());
						goods.setGoodsPosition(gs.getGoodsStorePosition());
						totalDao.save(goods);
						goodsId = goods.getGoodsId();
					}
					// 添加零件与在制品关系表
					ProcardProductRelation pprelation = new ProcardProductRelation();
					pprelation.setAddTime(Util.getDateTime());
					pprelation.setProcardId(procard.getId());
					pprelation.setGoodsId(goodsId);
					pprelation.setZyCount(goodsStore.getGoodsStoreCount());
					pprelation.setFlagType("本批在制品");
					totalDao.save(pprelation);
					gs.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
				}
			}
			totalDao.update(gs);
			return "转库成功!";
		}

		String goodsClass = gs.getGoodsStoreWarehouse();
		// if (goodsClass != null && procard != null && goodsClass.equals("成品库")
		// && procard.getProductStyle() != null
		// && procard.getProductStyle().equals("试制")) {
		// goodsClass = "试制库";
		// }
		// // 判断自制件和外购件的在制品数量是否足够
		// List<Procard> zizhiList = totalDao
		// .query(
		// "from Procard where (procardStyle='组合' or procardStyle='自制' or procardStyle='外购' or danjiaojian='单交件') and rootId=(select rootId from Procard where markId=? and selfCard=?))",
		// goodsStore.getGoodsStoreMarkId(), goodsStore
		// .getGoodsStoreLot());
		// List<Goods> zaizhiList = new ArrayList<Goods>();
		// if (zizhiList.size() > 0) {
		// Procard totalCard = (Procard) totalDao.getObjectById(Procard.class,
		// zizhiList.get(0).getRootId());// 获取总成零件
		// if (totalCard != null) {
		// for (Procard zzProcard : zizhiList) {
		// if (zzProcard.getProcard() != null
		// && (zzProcard.getProcard().getLingliaostatus() != null && zzProcard
		// .getProcard().getLingliaostatus().equals(
		// "否"))) {
		// break;// 上层不领料
		// }
		// if (zzProcard.getProcardStyle().equals("自制")
		// && zzProcard.getLingliaostatus() != null
		// && zzProcard.getLingliaostatus().equals("否")) {
		// break;// 自制件不领料
		// }
		// if (zzProcard.getDanjiaojian() != null
		// && zzProcard.getDanjiaojian().equals("单交件")
		// && zzProcard.getLingliaostatus() != null
		// && zzProcard.getLingliaostatus().equals("否")) {
		// break;// 单交件不领料
		// }
		// // 计算需要多少在制品
		// Float filnalCount = goodsStore.getGoodsStoreCount()
		// / totalCard.getFilnalCount()
		// * zzProcard.getFilnalCount();
		// List<Goods> zaizhiList2 = totalDao
		// .query(
		// "from Goods where goodsMarkId=? and goodsClass='在制品' and goodsCurQuantity>0 and goodsId not in(select goodsId from ProcardProductRelation)",
		// zzProcard.getMarkId());
		// if (zaizhiList2.size() > 0) {
		// if (goodsStore.getGoodsStoreCount() != null
		// && totalCard.getFilnalCount() != null
		// && zzProcard.getFilnalCount() != null) {
		// for (Goods zaizhiGood : zaizhiList2) {
		// if (filnalCount > zaizhiGood
		// .getGoodsCurQuantity()) {// 表示在制品数量不足
		// filnalCount = filnalCount
		// - zaizhiGood.getGoodsCurQuantity();
		// zaizhiGood.setCkCount(0f);// 临时存储领取后还剩多少在制品数量，在该方法最后在修改数据库
		// zaizhiGood.setGoodsArtsCard(zzProcard
		// .getSelfCard());
		// zaizhiList.add(zaizhiGood);
		// } else {
		// zaizhiGood.setCkCount(zaizhiGood
		// .getGoodsCurQuantity()
		// - filnalCount);// 临时存储领取后还剩多少在制品数量，在该方法最后在修改数据库
		// zaizhiGood.setGoodsArtsCard(zzProcard
		// .getSelfCard());
		// zaizhiList.add(zaizhiGood);
		// filnalCount = 0f;
		// break;
		// }
		// }
		// if (filnalCount > 0) {
		// if (zzProcard.getProcardStyle() == null
		// || !zzProcard.getProcardStyle().equals(
		// "组合")) {
		// return "在制品" + zzProcard.getMarkId()
		// + "数量不足缺少" + filnalCount;
		// }
		// }
		// }
		// } else {
		// if (zzProcard.getProcardStyle() == null
		// || !zzProcard.getProcardStyle().equals("组合")) {
		// return "在制品" + zzProcard.getMarkId() + "数量不足";
		// }
		// }
		// }
		// }
		// }
		BeanUtils.copyProperties(goodsStore, gs, new String[] {
				"goodsStoreWarehouse", "goodsStoreCount",
				"goodsStoreAdminName", "goodsStoreArtsCard", "status",
				"planID", "applyTime", "orderId", "processNo", "sqUsersName",
				"sqUsrsId", "sqUsersCode", "sqUsersdept", "style", "hsPrice",
				"goodsStorePrice", "taxprice" });
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Float returnCount = 0f;// 申请入库数量减去入库确认
		if ("ruku".equals(tag)) {
			gs.setGoodsStoreTime(Util.getDateTime());
			if (goodsStore.getGoodsStoreCount() != null
					&& gs.getGoodsStoreCount() != null) {
				returnCount = gs.getGoodsStoreCount()
						- goodsStore.getGoodsStoreCount();
				if (returnCount < 0) {
					return "确认入库数量超过申请入库数量申请失败";
				}
			}
			gs.setStatus("入库");
			gs.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
			// 判断库存情况

			String hqlgoods = "from Goods where goodsMarkId='"
					+ goodsStore.getGoodsStoreMarkId() + "' and goodsLotId='"
					+ goodsStore.getGoodsStoreLot() + "' and goodsClass='"
					+ goodsClass + "'and goodHouseName='"
					+ goodsStore.getGoodHouseName() + "'  ";
			if (goodsStore.getGoodsStorePosition() != null
					&& goodsStore.getGoodsStorePosition().length() > 0) {
				hqlgoods += " and goodsPosition='"
						+ goodsStore.getGoodsStorePosition() + "'";
			}

			if (goodsStore.getProcessNo() != null) {
				hqlgoods += " and processNo=" + goodsStore.getProcessNo();
			}
			if (gs.getNeiorderId() != null && gs.getNeiorderId().length() > 0) {
				hqlgoods += " and neiorderId='" + gs.getNeiorderId() + "'";
			} else {
				hqlgoods += " and (neiorderId is null or neiorderId='')";
			}
			List listGoods = totalDao.find(hqlgoods);
			Goods goods = null;
			if (listGoods != null && listGoods.size() > 0) {
				goods = (Goods) listGoods.get(0);
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						+ goodsStore.getGoodsStoreCount());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				totalDao.update(goods);
			} else {
				// 如果没有相同goods的添加新库存记录
				goods = addGoods(goodsStore, gs, goodsClass);
			}

			// 判断仓区，成品面积有才更新仓区占地面积
			gengxinMJ(goodsStore);

			// 改变中间表状态，关门
			closeDoor(goodsStore, goods);

			// 对象在同一方法内生成并且其属性有变动，不需要调用更新方法
			String planId = gs.getPlanID();
			if (null != planId && !"".equals(planId)) {
				// && procard.getProductStyle() != null
				// && procard.getProductStyle().equals("批产")) {
				String ywMarkId = gs.getYwmarkId();
				// if(gs.getProcardId()!=null){
				// ywMarkId = (String) totalDao
				// .getObjectByCondition(
				// "select ywMarkId from Procard where id=?",
				// goodsStore.getProcardId());
				// }else{
				// ywMarkId=(String) totalDao
				// .getObjectByCondition(
				// "select ywMarkId from ProcardTemplate where procardStyle='总成' and markId=?",
				// goodsStore.getGoodsStoreMarkId());
				// }

				String addSql = null;
				if (ywMarkId != null && ywMarkId.length() > 0) {
					addSql = "and (pieceNumber=? or pieceNumber='" + ywMarkId
							+ "')";
				} else {
					addSql = "and pieceNumber=?";
				}
				// 处理内部计划数量完成情况
				Integer planI = Integer.parseInt(planId.toString());
				InternalOrder io = (InternalOrder) totalDao.getObjectById(
						InternalOrder.class, planI);
				float stcountf = goodsStore.getGoodsStoreCount();
				Float stcounti = stcountf;// 本次入库数量
				Integer iodId = null;
				if (null != io) {
					String hqlPlanDetail = "from InternalOrderDetail iod where 1=1 "
							+ addSql + " and iod.internalOrder.id=?";
					InternalOrderDetail iod = (InternalOrderDetail) totalDao
							.getObjectByCondition(hqlPlanDetail, goodsStore
									.getGoodsStoreMarkId(), io.getId());
					if(iod.getQuantityCompletion()==null){
						iod.setQuantityCompletion(0f);
					}
					Float sumcount = iod.getQuantityCompletion() + stcounti;
					if (null != iod && (sumcount) <= iod.getNum()) {
						iodId = iod.getId();
						iod.setQuantityCompletion(sumcount);
						if (iod.getNum().floatValue() == iod
								.getQuantityCompletion().floatValue()) {
							String hqlIo = "from InternalOrderDetail iod where num!=quantityCompletion and iod.internalOrder.id=?";
							Integer count = totalDao
									.getCount(hqlIo, io.getId());
							if (0 == count) {
								io.setStatus("完成");
							}
						}
					}
				}
				// 处理订单数量交付情况
				// String hql =
				// "from Users where id in (select u.id from Users u join u. f  where f.id =?)"
				// + " order by dept";
				// 先查出没有Product_Internal表记录的ProductManager（兼容Product_Internal表没有建立之前的记录）
				String hqlnothas = "from ProductManager where orderManager.id in (select o.id from OrderManager o join o.innerOrders i where i.id=?) "
						+ addSql
						+ " and (allocationsNum<num or allocationsNum is null) and id not in(select productId from Product_Internal where ioDetailId in(select id from InternalOrderDetail where internalOrder.id=? "
						+ addSql + ")) order by (num-allocationsNum)";
				List pmnothasList = totalDao.query(hqlnothas, planI, goodsStore
						.getGoodsStoreMarkId(), planI, goodsStore
						.getGoodsStoreMarkId());
				/** 没有Product_Internal表记录的ProductManager **/
				if (pmnothasList != null && pmnothasList.size() > 0) {
					Float nextNum = 0f;
					for (int i = 0; i < pmnothasList.size(); i++) {
						ProductManager pm = (ProductManager) pmnothasList
								.get(i);
						goods.setNeiorderId(pm.getOrderManager().getOrderNum());
						Float num = pm.getNum();// 总数量
						Float allocationsNum = pm.getAllocationsNum();// 已入库数量
						nextNum = num - allocationsNum - stcounti;// 待交付数量=总数量-已入库数量-本次入库数量
						if (stcounti > 0) {
							fuzhiPrice(gs, goods, pm);
							if (nextNum > 0) {// 产品数量尚未交付完成
								pm.setAllocationsNum(allocationsNum + stcounti);
								stcounti = 0f;
							} else {// 产品数量尚已全部交付
								stcounti = stcounti - (num - allocationsNum);
								pm.setAllocationsNum(num);
								// stcounti = -nextNum;
								// /**** 更新订单的交付状态 ***/
								// // 查询订单对应的产品交付情况
								// String hql =
								// "from ProductManager where orderManager.id=? and allocationsNum<num";
								// int count = totalDao.getCount(hql, pm
								// .getOrderManager().getId());
								// if (count == 0) {// 订单对应的产品都已交付
								// OrderManager orderManager = (OrderManager)
								// totalDao
								// .getObjectById(OrderManager.class, pm
								// .getOrderManager().getId());
								// orderManager.setDeliveryStatus("是");
								// totalDao.update(orderManager);
								// }
								// /**** 更新订单的交付状态结束 ***/
							}
							// 计算订单入库率
							orderRukuLv(pm);
						}
						totalDao.update(goods);
					}
				}
				// 再查出有Product_Internal表记录的ProductManager
				String hqlhas = "from ProductManager where orderManager.id in (select o.id from OrderManager o join o.innerOrders i where i.id=?) "
						+ addSql
						+ " and (allocationsNum<num or allocationsNum is null) and id  in(select productId from Product_Internal where allcount>hasruku and status='同意' and ioDetailId in(select id from InternalOrderDetail where internalOrder.id=?  "
						+ addSql + ")) order by (num-allocationsNum)";
				List pmnhasList = totalDao.query(hqlhas, planI, goodsStore
						.getGoodsStoreMarkId(), planI, goodsStore
						.getGoodsStoreMarkId());
				/** 有Product_Internal表记录的ProductManager **/
				if (pmnhasList != null && pmnhasList.size() > 0) {
					for (int i = 0; i < pmnhasList.size(); i++) {
						ProductManager pm = (ProductManager) pmnhasList.get(i);
						goods.setNeiorderId(pm.getOrderManager().getOrderNum());
						Product_Internal pi = (Product_Internal) totalDao
								.getObjectByCondition(
										"from Product_Internal where allcount>hasruku and status='同意' and productId=? and ioDetailId in (select id from InternalOrderDetail where internalOrder.id=? and pieceNumber=?)",
										pm.getId(), planI, goodsStore
												.getGoodsStoreMarkId());
						if (pi != null) {
							if (pi.getHasruku() == null) {
								pi.setHasruku(0f);
							}
							if (stcounti.floatValue() > 0) {
								fuzhiPrice(gs, goods, pm);
								Float thispmcount = 0f;// 此订单本次提交数量
								if ((pi.getAllcount().floatValue() - pi
										.getHasruku().floatValue()) >= stcounti
										.floatValue()) {
									pi.setHasruku(pi.getHasruku() + stcounti);
									pm.setAllocationsNum(Util.jujueNull(pm
											.getAllocationsNum())
											+ stcounti);
									thispmcount = stcounti;
									stcounti = 0f;
									totalDao.update(pi);
									totalDao.update(pm);
								} else {
									stcounti = stcounti
											- (pi.getAllcount() - pi
													.getHasruku());
									pi.setHasruku(pi.getAllcount());
									pm.setAllocationsNum(pm.getAllocationsNum()
											+ (pi.getAllcount() - pi
													.getHasruku()));
									thispmcount = pi.getAllcount()
											- pi.getHasruku();
									totalDao.update(pi);
									totalDao.update(pm);
								}
								if (thispmcount > 0) {
									// 返回冲销
									List<ProductManager> relatePmlist = totalDao
											.query(
													"from ProductManager where orderManager.orderType='正式' and (cxCount>0 and (cxRukuCount is null or cxCount>cxRukuCount)) and  id in(select zsProductId from ProductZsAboutBh where bhProductId=? and id in(select pzsAboutbhId from InternalZsAboutBh where idetailId=?))",
													pm.getId(), iodId);
									if (relatePmlist != null
											&& relatePmlist.size() > 0) {
										for (ProductManager relatepm : relatePmlist) {
											if (relatepm.getCxRukuCount() == null) {
												relatepm.setCxRukuCount(0f);
											}
											if (thispmcount.floatValue() <= (relatepm
													.getCxCount() - relatepm
													.getCxRukuCount()
													.floatValue())) {
												relatepm
														.setCxRukuCount(relatepm
																.getCxRukuCount()
																+ thispmcount);
												// relatepm.setAllocationsNum(relatepm.getAllocationsNum()+thispmcount);
												thispmcount = 0f;
											} else {
												// relatepm.setAllocationsNum(relatepm.getAllocationsNum()+(relatepm.getCxCount()-relatepm.getCxRukuCount()));
												thispmcount = thispmcount
														- (relatepm
																.getCxCount() - relatepm
																.getCxRukuCount());
												relatepm
														.setCxRukuCount(relatepm
																.getCxCount());
											}
											totalDao.update(relatepm);
											if (thispmcount == 0) {
												break;
											}
										}
									}
								}
								// // 计算订单入库率
								orderRukuLv(pm);
							}
						}
						totalDao.update(goods);
					}
				}
			}
			// if (zaizhiList.size() > 0) {
			// for (Goods g : zaizhiList) {
			// if (g.getCkCount() != 0 || g.getGoodsCurQuantity() != 0) {
			// // 添加在制品出库记录
			// Sell sell = new Sell();
			// sell.setSellArtsCard(g.getGoodsArtsCard());
			// sell.setSellSupplier(g.getGoodsSupplier());
			// sell.setSellFormat(g.getGoodsFormat());
			// sell.setSellLot(g.getGoodsLotId());
			// sell.setSellMarkId(g.getGoodsMarkId());
			// sell.setSellAdminName(Util.getLoginUser().getName());
			// sell.setSellGoods(g.getGoodsFullName());
			// sell.setGoodHouseName(g.getGoodHouseName());
			// SimpleDateFormat sdf1 = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// SimpleDateFormat sdf2 = new SimpleDateFormat(
			// "yyyy-MM-dd");
			// sell.setSellDate(sdf2.format(new Date()));
			// sell.setSellTime(sdf1.format(new Date()));
			// sell.setSellWarehouse("在制品");
			// sell.setSellUnit(g.getGoodsUnit());
			// sell.setSellCharger(goodsStore.getGoodsStoreCharger());
			// // --------------------------------
			// sell.setSellProMarkId(g.getGoodsMarkId());
			// sell.setPrintStatus("YES");
			// sell.setStyle("正常出库");
			// sell.setSellPeople("入库");
			// sell.setSellCount(g.getGoodsCurQuantity()
			// - g.getCkCount());
			// totalDao.save(sell);
			// }
			// // 更新自制品的在制品数量
			// g.setGoodsCurQuantity(g.getCkCount());
			// totalDao.update(g);
			// }
			// }
			// 修改procard状态是入库数量
			if (procard.getRukuCount() == null) {
				procard.setRukuCount(0F);
			}
			if (returnCount != 0) {// 入库确认数小于申请数将少的数量还给Procard
				procard.setRukuCount(procard.getRukuCount() - returnCount);
			}
			if (procard.getHasRuku() == null) {
				procard.setHasRuku(goodsStore.getGoodsStoreCount());
			} else if(procard.getHasRuku()<procard.getFilnalCount()){
				procard.setHasRuku(procard.getHasRuku()
						+ goodsStore.getGoodsStoreCount());
			}
			// // 全部入库完成后
			// List<Float> hasRukuCounts = totalDao
			// .query(
			// "select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and status='入库'",
			// gs.getGoodsStoreMarkId(), gs.getGoodsStoreLot());// 次卡已入库数量
			Float hasRukuCount = procard.getHasRuku();
			// if (hasRukuCounts.size() > 0) {
			// hasRukuCount = hasRukuCounts.get(0);
			// }
			// 成本计算
			QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition(
					"from QuotedPrice where markId=? and procardStyle='总成'",
					procard.getMarkId());
			List<Procard> procardList = totalDao.query(
					"from Procard where rootId=?", procard.getId());
			if (procardList != null && procardList.size() > 0) {
				Float rengongfei = 0F;// 人工成本
				Float shebeiZjFei = 0F;// 设备折旧
				Float nyxhFei = 0F;// 设备能耗
				Float clFei_all = 0f;
				Float WgFei_all = 0f;
				for (Procard p : procardList) {
					Set<ProcessInfor> processset = p.getProcessInforSet(); // 此零件需要的工序
					for (ProcessInfor processInfor : processset) {
						if (!"自制".equals(processInfor.getProductStyle())
								|| processInfor.getNowAllJiepai() == null) {
							continue;
						}
						// 遍历所有的工序领取记录,统计所有的领取人员
						String hql_prolog = "from ProcessInforReceiveLog where fk_processInforId=? and fk_pirLId is null and status='提交'";
						List<ProcessInforReceiveLog> list = totalDao.query(
								hql_prolog, processInfor.getId());
						Map<String, String> nameMap = new HashMap<String, String>();
						for (ProcessInforReceiveLog pirl : list) {
							String userCodes = pirl.getUsercodes();
							String[] codes = userCodes.split(",");
							for (String code : codes) {
								String userCode = nameMap.get(code);
								if (userCode == null) {
									nameMap.put(code, code);
								}
							}
						}
						Double workingHoursWages = 0.0; // 工序工时工资
						for (String jobNum : nameMap.keySet()) { // 统计工序中基本工时工资
							WageStandard wageStandard = wss
									.findWSByUser(jobNum); // 根据工号查询工资模板
							if (wageStandard == null) {
								continue;
							}
							InsuranceGold insuranceGold = igs
									.findInsuranceGoldBylc(wageStandard
											.getLocalOrField(), wageStandard
											.getCityOrCountryside(),
											wageStandard.getPersonClass()); // 福利系数（计算公司缴纳的保险成本）
							if (insuranceGold == null) {
								continue;
							}
							// 单工序总成本（当月个人人力成本）
							workingHoursWages += (wageStandard
									.getGangweigongzi()
									+ wageStandard.getSsBase()
									* (insuranceGold.getQYoldageInsurance()
											+ insuranceGold
													.getQYmedicalInsurance()
											+ insuranceGold
													.getQYunemploymentInsurance()
											+ insuranceGold
													.getQYinjuryInsurance() + insuranceGold
											.getQYmaternityInsurance()) / 100 + wageStandard
									.getGjjBase()
									* insuranceGold.getQYhousingFund() / 100);
						}
						Double basicWorkingHoursWages = 0d;
						if (nameMap.size() > 0 && SECONDS > 0) {
							basicWorkingHoursWages = workingHoursWages
									/ nameMap.size() / SECONDS; // 工序中基本工时工资(秒工资=单工序总成本/21.5天)
						}
						Double processWages = 0d;
						if (processInfor.getNowAllJiepai() != null) {
							processWages = basicWorkingHoursWages
									* processInfor.getNowAllJiepai();// 单个工序的人工成本
						}

						processInfor.setRengongfei(processWages.floatValue());// 人工费

						rengongfei += processWages.floatValue();// 累计人工成本

						if (processInfor.getShebeiNo() != null
								&& processInfor.getShebeiNo().length() > 0) {
							/************ 计算设备折旧费 ***/
							// 查询设备价格
							String hql_Machine = "from Machine where no=?";
							Machine machine = (Machine) totalDao
									.getObjectByCondition(hql_Machine,
											processInfor.getShebeiNo());
							if (machine != null) {
								try {
									// 先计算设备净值
									SimpleDateFormat df = new SimpleDateFormat(
											"yyyy-MM-dd 00:00:00");

									String buytime = machine.getBuytime();
									if (buytime != null && buytime.length() > 0
											&& buytime != "") {
										String[] str = buytime.split("-");
										String buy = str[0];
										Integer buy1 = Integer.parseInt(buy)
												+ machine.getDepreciationYear();
										String buy2 = buy1.toString();
										String buytime1 = buy2 + "-" + str[1]
												+ "-" + str[2];
										machine.setEndtime(buytime1);
										Float Buyamount = (Float) machine
												.getBuyamount();// 购买金额
										Timestamp timestamp = Timestamp
												.valueOf(df
														.format(new java.util.Date()));// 取出系统当前时间
										System.out
												.println(timestamp.toString());
										Long date = Util.StringToDate(
												timestamp.toString(),
												"yyyy-MM-dd").getTime();// 当前日期
										Long buydate = Util.StringToDate(
												buytime, "yyyy-MM-dd")
												.getTime();// 购买日期
										Integer depreciationYear = machine
												.getDepreciationYear();// 折旧年限
										Long nYear = 1000 * 60 * 60 * 24 * 365L;// 转化为年
										Long newYear = date - buydate;// 已用折旧时间
										float year1 = newYear / nYear;// 转化为年
										float oldYear = depreciationYear
												- year1;// 剩余折旧时间(转化为年)
										float equipmentworth = (oldYear / depreciationYear)
												* Buyamount;
										DecimalFormat myFormat = new DecimalFormat(
												"0.00");// 设置float类型的小数只能为两位
										String strFloat = myFormat
												.format(equipmentworth);
										machine.setEquipmentworth(strFloat);
										// 计算本次折旧费用
										Float shebeiZhejiu_1 = processInfor
												.getOpshebeijiepai()
												* processInfor
														.getSubmmitCount()
												* equipmentworth
												/ (3600 * 8 * 12 * 22.5f * machine
														.getDepreciationYear());
										processInfor
												.setShebeiZjFei(shebeiZhejiu_1);
										shebeiZjFei += shebeiZhejiu_1;// 累计设备折旧费
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							/************ 计算设备折旧费结束 ***/

							/************ 计算设备能耗 ***/
							Float allnonehao = 0F;
							if (processInfor.getNowAllNenghao() != null) {
								allnonehao += processInfor.getNowAllNenghao();
							}
							if (processInfor.getDaiNeghao() != null) {
								allnonehao += processInfor.getDaiNeghao();
							}
							Float sbnh = allnonehao * 1;// 电价按照1元计费
							processInfor.setNyxhFei(sbnh);
							nyxhFei += sbnh;// 累计设备能耗费用
						}
					}

					// if ("自制".equals(p.getProcardStyle())
					// || "是".equals(p.getDanjiaojian())
					// || ("组合".equals(p.getProcardStyle()) && p
					// .isZhHasYcl())) {
					// /************** 计算材料费 ******************/
					// // String hql_Zhgongxu =
					// //
					// "select jiage from Zhgongxu where paihao=? and guige=? and danwei=?";
					// String hql_Zhgongxu =
					// "select jiage from Zhgongxu where paihao=? and guige=? ";
					// Float jiage = (Float) totalDao.getObjectByCondition(
					// hql_Zhgongxu, p.getTrademark(), p
					// .getSpecification());
					// if (jiage != null) {
					// Float clFei = goodsStore.getGoodsStoreCount()
					// * p.getQuanzi2() / p.getQuanzi1() * jiage;
					// // p.setClFei(clFei);
					// clFei_all += clFei;
					// }
					if ("外购".equals(p.getProcardStyle())) {
						// String nowDate = Util.getDateTime("YYYY-MM-dd");
						// String hql_price =
						// "select bhsPrice from Price where partNumber =? and pricePeriodStart<=? and pricePeriodEnd>=?";
						// Double bhsPrice = (Double) totalDao
						// .getObjectByCondition(hql_price,
						// procard2.getMarkId(), nowDate,
						// nowDate);
						String hql_price = "select bhsPrice from Price where partNumber =? order by  pricePeriodEnd desc";
						Double bhsPrice = (Double) totalDao
								.getObjectByCondition(hql_price, p.getMarkId());
						if (bhsPrice != null) {
							Float wgFei_1 = bhsPrice.floatValue()
									* goodsStore.getGoodsStoreCount();
							// p.setWgFei(wgFei_1);
							WgFei_all += wgFei_1;
						}
					}
				}
				if (qp != null) {
					// 费用消耗计算
					feiyongXH(goodsStore, procard, qp, user, rengongfei,
							shebeiZjFei, nyxhFei, clFei_all, WgFei_all);
				}
			}

			if (hasRukuCount.equals(procard.getFilnalCount())) {
				// 处理流水单
				procard.setStatus("入库");
				// 处理流水卡
				if (procard.getCardId() != null) {
					if ("ruku".equals(tag)) {
						RunningWaterCard runningWaterCard = (RunningWaterCard) totalDao
								.getObjectById(RunningWaterCard.class, procard
										.getCardId());
						if (runningWaterCard != null) {
							runningWaterCard.setCardStatus("入库");
							runningWaterCard.setOwnUsername(user.getName());
							totalDao.update(runningWaterCard);
						}
					} else {
						LogoStickers sticker = (LogoStickers) totalDao
								.getObjectById(LogoStickers.class, procard
										.getCardId());
						if (sticker != null) {
							sticker.setStatus("入库");
							totalDao.update(sticker);
						}
					}
				}
			}
			totalDao.update(procard);
			// if (procard.getProductStyle() != null
			// && procard.getProductStyle().equals("试制")) {
			// // 修改试制项目的零件入库数量
			// // TryMake trymake=(TryMake)
			// //
			// totalDao.getObjectByQuery("from TryMake where markId=? and id in (select tmId from ProjectOrderPart where projectOrder.id=?)",
			// // procard.getMarkId(),procard.getPlanOrderId());
			// TryMake trymake = (TryMake) totalDao
			// .getObjectByCondition(
			// "from TryMake where markId=? and proTryMakeScore.id in (select proTryMakeScore.id from ProjectOrder where id=?)",
			// procard.getMarkId(), procard.getPlanOrderId());
			// if (trymake.getInputNum() == null) {
			// trymake.setInputNum(0);
			// }
			// float count = goodsStore.getGoodsStoreCount();
			// int count_int = (int) count;
			// trymake.setInputNum(trymake.getInputNum() + count_int);
			// totalDao.update(trymake);
			// ProTryMakeScore ptm = (ProTryMakeScore) totalDao
			// .getObjectByCondition(
			// "from ProTryMakeScore where id =(select proTryMakeScore.id from ProjectOrder where id=?)",
			// procard.getPlanOrderId());
			// if (ptm != null) {
			// Set<TryMake> trymakeSet = ptm.getTryMake();
			// if (trymakeSet != null && trymakeSet.size() > 0) {
			// Float all = 0f;
			// Float completion = 0f;
			// for (TryMake tm : trymakeSet) {
			// if (tm.getApprovalNum() != null) {
			// all += tm.getApprovalNum();
			// if (tm.getInputNum() != null) {
			// completion += tm.getInputNum();
			// }
			// }
			// }
			// if (all != 0) {// 修改完成率
			// ptm.setCompletionrate(completion * 100 / all);
			// totalDao.update(ptm);
			// }
			// }
			// }
			// }
			return "true";
		} else {
			/*
			 * return totalDao.update(gs); }else if("update".equals(tag)){
			 */
			// 判断数量修改的信息，以及关联库存信息
			float chazhi = gs.getGoodsStoreCount()
					- goodsStore.getGoodsStoreCount();
			String hql = "from Goods where goodsMarkId='"
					+ gs.getGoodsStoreMarkId() + "' and goodsLotId='"
					+ gs.getGoodsStoreLot() + "' and goodsClass='" + goodsClass
					+ "'";
			if (gs.getNeiorderId() != null && gs.getNeiorderId().length() > 0) {
				hql += " and neiorderId='" + gs.getNeiorderId() + "'";
			} else {
				hql += " and (neiorderId is null or neiorderId='')";
			}
			List listGoods = totalDao.find(hql);
			if (listGoods.size() > 0) {
				Goods goods = (Goods) listGoods.get(0);
				if (goods.getGoodsCurQuantity() >= chazhi) {
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							- chazhi);
					totalDao.update(goods);
					gs.setGoodsStoreTime(goodsStore.getGoodsStoreTime());
					boolean b = totalDao.update(gs);
					if (b) {
						return "true";
					} else {
						return "入库失败";
					}
				} else {
					return "入库失败";
				}
			}
			if (procard.getRukuCount() == null) {
				procard.setRukuCount(0F);
			}
			if (returnCount != 0) {// 入库确认数小于申请数将少的数量还给Procard
				procard.setRukuCount(procard.getRukuCount() - returnCount);
			}
			if (procard.getHasRuku() == null) {
				procard.setHasRuku(goodsStore.getGoodsStoreCount());
			} else {
				procard.setHasRuku(procard.getHasRuku()
						+ goodsStore.getGoodsStoreCount());
			}
			// 全部入库完成后
			// List<Float> hasRukuCounts = totalDao
			// .query(
			// "select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and status='入库'",
			// gs.getGoodsStoreMarkId(), gs.getGoodsStoreLot());// 次卡已入库数量
			Float hasRukuCount = procard.getHasRuku();
			// if (hasRukuCounts.size() > 0) {
			// hasRukuCount = hasRukuCounts.get(0);
			// }
			if (hasRukuCount == procard.getFilnalCount()) {
				// 处理流水单
				procard.setStatus("入库");
				// 处理流水卡
				if (procard.getCardId() != null) {
					RunningWaterCard runningWaterCard = (RunningWaterCard) totalDao
							.getObjectById(RunningWaterCard.class, procard
									.getCardId());
					runningWaterCard.setCardStatus("完成");
					runningWaterCard.setOwnUsername(user.getName());
					totalDao.update(runningWaterCard);
				} else {
					LogoStickers sticker = (LogoStickers) totalDao
							.getObjectById(LogoStickers.class, procard
									.getCardId());
					if (sticker != null) {
						sticker.setStatus("入库");
						totalDao.update(sticker);
					}
				}
			}
			totalDao.update(procard);
			// if (procard.getProductStyle() != null
			// && procard.getProductStyle().equals("试制")) {
			// // 修改试制项目的零件入库数量
			// // TryMake trymake=(TryMake)
			// //
			// totalDao.getObjectByQuery("from TryMake where markId=? and id in (select tmId from ProjectOrderPart where projectOrder.id=?)",
			// // procard.getMarkId(),procard.getPlanOrderId());
			// TryMake trymake = (TryMake) totalDao
			// .getObjectByQuery(
			// "from TryMake where markId=? and proTryMakeScore.id in (select proTryMakeScore.id from projectOrder where id=?)",
			// procard.getMarkId(), procard.getPlanOrderId());
			// if (trymake.getInputNum() == null) {
			// trymake.setInputNum(0);
			// }
			// trymake.setInputNum(Integer
			// .parseInt((trymake.getInputNum() + goodsStore
			// .getGoodsStoreCount())
			// + ""));
			// totalDao.update(trymake);
			// }
			return "修改成功";
		}
	}

	public static String checkquanxian(Users user, String goodsStoreWarehouse,
			String type,TotalDao totalDao) {
		// TODO Auto-generated method stub
		if(user==null){
			return "请先登录!";
		}
		String ckname = (String) totalDao.getObjectByCondition("select code from WareHouse where name=?", goodsStoreWarehouse);
		if(ckname==null){
			return "库别有误";
		}
		String typename ="";
		if(type.equals("in")){
			typename ="入库";
		}else if(type.equals("out")){
			typename ="出库";
		}else if(type.equals("edit")){
			typename = "修改";
		}else if(type.equals("view")){
			typename = "查看";
		}else if(type.equals("del")){
			typename = "删除";
		}else{
			return "权限类型异常!";
		}
		WareHouseAuth whh = (WareHouseAuth) totalDao
		.getObjectByCondition("from WareHouseAuth where usercode=? and auth like '%"+ckname+"_"+type+"%'",user.getCode());
		if(whh==null){
			return "对不起您没有"+goodsStoreWarehouse+"的"+typename+"权限";
		}
		return "true";
	}

	/**
	 * 将订单价格，单号赋值到入库记录和库存
	 * 
	 * @param gs
	 * @param goods
	 * @param pm
	 * @author licong
	 */
	private void fuzhiPrice(GoodsStore gs, Goods goods, ProductManager pm) {
		Float unit = pm.getUnit() == null ? null : pm.getUnit().floatValue();
		Float bhsPrice = pm.getBhsPrice() == null ? null : pm.getBhsPrice()
				.floatValue();
		goods.setGoodsSellPrice(unit);// 给库存表的销售价格赋值
		goods.setWaiorderId(pm.getOrderManager().getOutOrderNumber());// 外部订单号
		gs.setHsPrice(unit.doubleValue());// 给入库历史记录表的含税价格赋值
		gs.setTaxprice(pm.getTaxprice());
		gs.setGoodsStorePrice(bhsPrice.doubleValue());
		gs.setWaiorderId(pm.getOrderManager().getOutOrderNumber());// 订单外部单号
		gs.setNeiorderId(pm.getOrderManager().getOrderNum());// 订单内部单号
	}

	/**
	 * 添加库存表
	 * 
	 * @param goodsStore
	 * @param gs
	 * @param goodsClass
	 * @return
	 */

	private Goods addGoods(GoodsStore goodsStore, GoodsStore gs,
			String goodsClass) {
		Goods goods;
		goods = new Goods();
		goods.setGoodsBarcode(gs.getGoodsStoreArtsCard());
		goods.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
		goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
		goods.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
		goods.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());
		goods.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());
		goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
		goods.setProcessNo(goodsStore.getProcessNo());
		goods.setProcessName(goodsStore.getProcessName());
		if (null == goodsStore.getGoodsStoreFormat()) {
			goods.setGoodsFormat("");
		} else {
			goods.setGoodsFormat(goodsStore.getGoodsStoreFormat());
		}
		goods.setGoodsLotId(goodsStore.getGoodsStoreLot());
		if (null == goodsStore.getGoodsStoreUnit()) {
			goods.setGoodsUnit("件");
		} else {
			goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
		}
		goods.setGoodsChangeTime(goodsStore.getGoodsStoreDate());
		goods.setGoodsClass(goodsClass);
		goods.setYwmarkId(goodsStore.getYwmarkId());// 业务件号
		goods.setNeiorderId(goodsStore.getNeiorderId());// 内部订单号
		goods.setWaiorderId(goodsStore.getWaiorderId());// 外部订单号
		goods.setGoodHouseName(goodsStore.getGoodHouseName());// g
		goods.setGoodsBuyPrice(gs.getHsPrice().floatValue());// 批次 含税单价
		goods.setGoodsBuyBhsPrice(gs.getGoodsStorePrice().floatValue());// 批次 不含税单价
		if (gs.getTaxprice() != null) {
			goods.setTaxprice(gs.getTaxprice().floatValue());// 税率
		}
		Float[] avgprices = getAccountsDate(gs);
		goods.setGoodshsPrice(avgprices[0]);
		goods.setGoodsPrice(avgprices[1]);
		totalDao.save(goods);
		return goods;
	}

	/**
	 * 更新仓区面积
	 * 
	 * @param goodsStore
	 */
	private void gengxinMJ(GoodsStore goodsStore) {
		GoodHouse goodHouse = (GoodHouse) totalDao
				.get(
						"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
						new Object[] { goodsStore.getGoodsStoreWarehouse(),
								goodsStore.getGoodHouseName() });
		// 单件
		ProcardTemplate procardArea = (ProcardTemplate) totalDao
				.get(
						"from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='')  and productStyle='批产' ",
						new Object[] { goodsStore.getGoodsStoreMarkId() });
		if (goodHouse != null && procardArea != null) {
			// 存在面积
			if (goodHouse.getGoodAllArea() > 0
					&& goodHouse.getGoodAllArea() != null
					&& procardArea.getActualUsedProcardArea() != null
					&& procardArea.getActualUsedProcardArea() > 0) {
				/*
				 * if(goodHouse.getGoodAllArea()<=goodHouse.getGoodIsUsedArea
				 * ()){//此仓区已满 //return "此仓区已满"; }else{ //未满更新
				 * 
				 * //单件占地面积goodsStoreCount Double procardActualUsed=procardArea
				 * .getActualUsedProcardArea()*goodsStore .getGoodsStoreCount();
				 * //此次存放仍有剩余 if((procardActualUsed+goodHouse
				 * .getGoodIsUsedArea())<goodHouse.getGoodAllArea()){
				 * goodHouse.setGoodIsUsedArea(procardActualUsed+goodHouse.
				 * getGoodIsUsedArea()); goodHouse.setGoodLeaveArea(goodHouse
				 * .getGoodAllArea()-goodHouse.getGoodIsUsedArea()); //return
				 * "入库成功"; }else
				 * if((procardActualUsed+goodHouse.getGoodIsUsedArea
				 * ())>=goodHouse.getGoodAllArea()){
				 * goodHouse.setGoodIsUsedArea(goodHouse.getGoodAllArea());
				 * goodHouse.setGoodLeaveArea(0D); return "入库成功，此次存放后此仓区已满"; } }
				 */
				// 单件占地面积goodsStoreCount
				Double procardActualUsed = procardArea
						.getActualUsedProcardArea()
						* goodsStore.getGoodsStoreCount();
				procardActualUsed = Double.valueOf(new DecimalFormat("0.00")
						.format(procardActualUsed
								+ goodHouse.getGoodIsUsedArea()));
				goodHouse.setGoodIsUsedArea(procardActualUsed);
				goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()
						- goodHouse.getGoodIsUsedArea());
				totalDao.update(goodHouse);
			}
		}
	}

	/**
	 * 费用消耗表
	 * 
	 * @param goodsStore
	 * @param procard
	 * @param qp
	 * @param user
	 * @param rengongfei
	 * @param shebeiZjFei
	 * @param nyxhFei
	 * @param clFei_all
	 * @param WgFei_all
	 */
	private void feiyongXH(GoodsStore goodsStore, Procard procard,
			QuotedPrice qp, Users user, Float rengongfei, Float shebeiZjFei,
			Float nyxhFei, Float clFei_all, Float WgFei_all) {
		QuotedPriceCost quotedPriceCost1 = new QuotedPriceCost();
		quotedPriceCost1.setProStatus(procard.getProductStyle() + "阶段");
		quotedPriceCost1.setCounts(goodsStore.getGoodsStoreCount());
		quotedPriceCost1.setTzMoney(0d);
		quotedPriceCost1.setUserName(user.getName());
		quotedPriceCost1.setDept(user.getDept());
		quotedPriceCost1.setUserCode(user.getCode());
		quotedPriceCost1.setApplyStatus("同意");
		quotedPriceCost1.setSource("人工申报");
		quotedPriceCost1.setCostType("人工费");
		// quotedPriceCost.setProStatus(qp.getStatus());
		quotedPriceCost1.setMarkId(qp.getMarkId());
		quotedPriceCost1.setSelfCard(procard.getSelfCard());
		quotedPriceCost1.setQpId(qp.getId());
		quotedPriceCost1.setMoney((double) rengongfei);
		quotedPriceCost1.setAddTime(Util.getDateTime());
		totalDao.save(quotedPriceCost1);
		QuotedPriceCost quotedPriceCost2 = new QuotedPriceCost();
		quotedPriceCost2.setProStatus(procard.getProductStyle() + "阶段");
		quotedPriceCost2.setCounts(goodsStore.getGoodsStoreCount());
		quotedPriceCost2.setTzMoney(0d);
		quotedPriceCost2.setUserName(user.getName());
		quotedPriceCost2.setDept(user.getDept());
		quotedPriceCost2.setUserCode(user.getCode());
		quotedPriceCost2.setApplyStatus("同意");
		quotedPriceCost2.setSource("设备折旧申报");
		quotedPriceCost2.setCostType("设备折旧费");
		// quotedPriceCost.setProStatus(qp.getStatus());
		quotedPriceCost2.setMarkId(qp.getMarkId());
		quotedPriceCost2.setSelfCard(procard.getSelfCard());
		quotedPriceCost2.setQpId(qp.getId());
		quotedPriceCost2.setMoney((double) shebeiZjFei);
		quotedPriceCost2.setAddTime(Util.getDateTime());
		totalDao.save(quotedPriceCost2);
		QuotedPriceCost quotedPriceCost3 = new QuotedPriceCost();
		quotedPriceCost3.setProStatus(procard.getProductStyle() + "阶段");
		quotedPriceCost3.setCounts(goodsStore.getGoodsStoreCount());
		quotedPriceCost3.setTzMoney(0d);
		quotedPriceCost3.setUserName(user.getName());
		quotedPriceCost3.setDept(user.getDept());
		quotedPriceCost3.setUserCode(user.getCode());
		quotedPriceCost3.setApplyStatus("同意");
		quotedPriceCost3.setSource("能源申报");
		quotedPriceCost3.setCostType("能源消耗费");
		// quotedPriceCost.setProStatus(qp.getStatus());
		quotedPriceCost3.setMarkId(qp.getMarkId());
		quotedPriceCost3.setSelfCard(procard.getSelfCard());
		quotedPriceCost3.setQpId(qp.getId());
		quotedPriceCost3.setMoney((double) nyxhFei);
		quotedPriceCost3.setAddTime(Util.getDateTime());
		totalDao.save(quotedPriceCost3);
		QuotedPriceCost quotedPriceCost4 = new QuotedPriceCost();
		quotedPriceCost4.setProStatus(procard.getProductStyle() + "阶段");
		quotedPriceCost4.setCounts(goodsStore.getGoodsStoreCount());
		quotedPriceCost4.setTzMoney(0d);
		quotedPriceCost4.setUserName(user.getName());
		quotedPriceCost4.setDept(user.getDept());
		quotedPriceCost4.setUserCode(user.getCode());
		quotedPriceCost4.setApplyStatus("同意");
		quotedPriceCost4.setSource("材料费申报");
		quotedPriceCost4.setCostType("材料费");
		// quotedPriceCost.setProStatus(qp.getStatus());
		quotedPriceCost4.setMarkId(qp.getMarkId());
		quotedPriceCost4.setSelfCard(procard.getSelfCard());
		quotedPriceCost4.setQpId(qp.getId());
		quotedPriceCost4.setMoney((double) clFei_all);
		quotedPriceCost4.setAddTime(Util.getDateTime());
		totalDao.save(quotedPriceCost4);
		QuotedPriceCost quotedPriceCost5 = new QuotedPriceCost();
		quotedPriceCost5.setProStatus(procard.getProductStyle() + "阶段");
		quotedPriceCost5.setCounts(goodsStore.getGoodsStoreCount());
		quotedPriceCost5.setTzMoney(0d);
		quotedPriceCost5.setUserName(user.getName());
		quotedPriceCost5.setDept(user.getDept());
		quotedPriceCost5.setUserCode(user.getCode());
		quotedPriceCost5.setApplyStatus("同意");
		quotedPriceCost5.setSource("外购件申报");
		quotedPriceCost5.setCostType("外购件费用");
		quotedPriceCost5.setMarkId(qp.getMarkId());
		quotedPriceCost5.setSelfCard(procard.getSelfCard());
		quotedPriceCost5.setQpId(qp.getId());
		quotedPriceCost5.setMoney((double) WgFei_all);
		quotedPriceCost5.setAddTime(Util.getDateTime());
		totalDao.save(quotedPriceCost5);
		if (qp.getRealAllfy() != null) {
			qp.setRealAllfy(rengongfei + shebeiZjFei + nyxhFei + clFei_all
					+ WgFei_all + qp.getRealAllfy());
		} else {
			qp.setRealAllfy((double) rengongfei + shebeiZjFei + nyxhFei
					+ clFei_all + WgFei_all);
		}
		if (qp.getYingkui() == null) {
			qp.setYingkui(0 - qp.getRealAllfy());
		} else {
			qp
					.setYingkui(qp.getYingkui()
							- (rengongfei + shebeiZjFei + nyxhFei + clFei_all + WgFei_all));
		}
		totalDao.update(qp);
	}

	/**
	 * 计算订单入库率
	 * 
	 * @param pm
	 */
	private void orderRukuLv(ProductManager pm) {
		Integer orderId = pm.getOrderManager().getId();
		String hql = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
		String hql2 = "select sum(allocationsNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
		Float pronum = (Float) totalDao.query(hql, orderId).get(0);
		Float proallocationsNum = (Float) totalDao.query(hql2, orderId).get(0);
		OrderManager orderManager = (OrderManager) totalDao.getObjectById(
				OrderManager.class, orderId);
		orderManager.setInputRate(proallocationsNum / pronum * 100);
		totalDao.update(orderManager);
	}

	/**
	 * 关闭库位门
	 * 
	 * @param goodsStore
	 * @param goods
	 */
	private void closeDoor(GoodsStore goodsStore, Goods goods) {
		WareBangGoogs bangGoogs = (WareBangGoogs) totalDao
				.getObjectByCondition(
						"from WareBangGoogs where fk_goodsStore_id = ?",
						goodsStore.getGoodsStoreId());
		if (bangGoogs != null) {
			/**
			 * 判断
			 */
			bangGoogs.setNumber(goodsStore.getGoodsStoreCount());
			bangGoogs.setFk_good_id(goods.getGoodsId());
			totalDao.update(bangGoogs);
			// 关门
			WarehouseNumber wn = (WarehouseNumber) totalDao.getObjectById(
					WarehouseNumber.class, bangGoogs.getFk_ware_id());
			if (wn != null) {
				UtilTong.OCKuWei(wn.getIp(), duankou, false, wn.getOneNumber(),
						wn.getNumid());// 关门操作
				AttendanceTowServerImpl.sendPin_1(wn);
			}
		}
	}

	// 休眠n秒
	// public void sleeps(int i) {
	// try {
	// Thread.sleep(i);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public Goods save(GoodsStore goodsStore, OsRecord record) {
		// OsRecord r = (OsRecord) totalDao.get(OsRecord.class, goodsStore
		// .getOsrecordId());
		// r.setRuku(true);
		return save_2(goodsStore, totalDao);
	}

	public static Goods save_2(GoodsStore goodsStore, TotalDao totalDao) {
		if (totalDao == null) {
			totalDao = createTotol();
		}
		String rutype = "外购件入库";
		Integer processNo = null;
		String processName = null;
		// 外委回来的数据需要标记入库类型为半成品转库
		List<Object[]> procardAndcount = new ArrayList<Object[]>();
		if (goodsStore.getWwddId() != null) {
			WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
					.getObjectById(WaigouDeliveryDetail.class, goodsStore
							.getWwddId());
			if (wddOld != null) {
				// 更新采购订单信息
				WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, wddOld.getWaigouPlanId());
				if (waigouPlan.getType().equals("外委")) {
					rutype = "半成品转库2";// 含特殊含义不可修改
					processNo = Util.getSplitNumber(waigouPlan.getProcessNOs(),
							";", "max");
					processName = Util.getSplitString(waigouPlan
							.getProcessNames(), ";", "end");
					Float dhCount = goodsStore.getGoodsStoreCount();
					List<Procard> procardList = totalDao
							.query(
									"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by selfCard",
									waigouPlan.getId());// 为了先填充小批次
					if (procardList != null && procardList.size() > 0) {
						for (Procard procard : procardList) {
							List<ProcardWGCenter> zjbList = totalDao
									.query(
											"from ProcardWGCenter where wgOrderId=? and procardId=? order by id",
											waigouPlan.getId(), procard.getId());
							for (ProcardWGCenter zjb : zjbList) {
								if (zjb.getDhCount() == null) {
									zjb.setDhCount(0f);
								}
								Float kdhCount = zjb.getProcardCount()
										- zjb.getDhCount();
								if (kdhCount > 0) {
									Float jsCount = 0f;
									if (dhCount > kdhCount) {
										jsCount = kdhCount;
										dhCount -= kdhCount;
									} else {
										jsCount = dhCount;
										dhCount = 0f;
									}
									Object[] objs = new Object[] { procard,
											jsCount };
									procardAndcount.add(objs);
								}
								if (dhCount == 0) {
									break;
								}
							}
						}
					}
				} else {
					// 供应商年月日季周合格信息维护
					// saveGysInfor(goodsStore, waigouPlan, wddOld);
				}
			}
		}

		// 匹配条件需要调整

		String hql = "from Goods where goodsMarkId = ?";

		if (goodsStore.getGoodsStoreWarehouse() != null
				&& goodsStore.getGoodsStoreWarehouse().length() > 0) {
			hql += " and goodsClass='" + goodsStore.getGoodsStoreWarehouse()
					+ "'";
		}
		if (goodsStore.getGoodHouseName() != null
				&& goodsStore.getGoodHouseName().length() > 0) {
			hql += " and goodHouseName='" + goodsStore.getGoodHouseName() + "'";
		}
		if (goodsStore.getGoodsStorePosition() != null
				&& goodsStore.getGoodsStorePosition().length() > 0) {
			hql += " and goodsPosition='" + goodsStore.getGoodsStorePosition()
					+ "'";
		}
		if (goodsStore.getBanBenNumber() != null
				&& goodsStore.getBanBenNumber().length() > 0) {
			hql += " and banBenNumber='" + goodsStore.getBanBenNumber() + "'";
		}
		if (goodsStore.getKgliao() != null
				&& goodsStore.getKgliao().length() > 0) {
			hql += " and kgliao='" + goodsStore.getKgliao() + "'";
		}
		if (goodsStore.getGoodsStoreLot() != null
				&& goodsStore.getGoodsStoreLot().length() > 0) {
			hql += " and goodsLotId='" + goodsStore.getGoodsStoreLot() + "'";
		}
		if (goodsStore.getHsPrice() != null && goodsStore.getHsPrice() > 0) {
			hql += " and goodsPrice='" + goodsStore.getHsPrice() + "'";
		}

		Goods g = (Goods) totalDao.getObjectByCondition(hql, goodsStore
				.getGoodsStoreMarkId());

		// 计算单张重量
		jisuanDanZhang(goodsStore);
		// 查询同件号、订单号、批次号的是否已经存在入库记录
		String hql_distinct = "from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and goodsStoreSendId=? and osrecordId=?";
		int gscount = totalDao.getCount(hql_distinct, goodsStore
				.getGoodsStoreMarkId(), goodsStore.getGoodsStoreLot(),
				goodsStore.getGoodsStoreSendId(), goodsStore.getOsrecordId());
		if (gscount > 0) {
			throw new RuntimeException("重复入库,请刷新后重试!");
		}
		totalDao.save(goodsStore);

		Float avghsprice =	goodsStore.getHsPrice() == null?null: goodsStore.getHsPrice().floatValue();
		Float avgbhsprice =goodsStore.getGoodsStorePrice()==null?null: goodsStore.getGoodsStorePrice().floatValue();
		// 查询成本核算方法
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		if (accountsDate != null) {
			String hql_banben = "";
			if (goodsStore.getBanBenNumber() != null
					&& goodsStore.getBanBenNumber().length() > 0) {
				hql_banben = " and banBenNumber='"
						+ goodsStore.getBanBenNumber() + "'";
			}
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben;

				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					avghsprice = Float.parseFloat(obj[0].toString());
					avgbhsprice = Float.parseFloat(obj[1].toString());
				}
			} else if ("monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount) ,sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben
						+ " and goodsStoreTime >'"
						+ Util.getDateTime("yyyy-MM")
						+ "-01' and goodsStoreTime <'"
						+ Util.getDateTime("yyyy-MM")
						+ "-"
						+ accountsDate.getJihao() + "' ";
				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					try {
						avghsprice = Float.parseFloat(obj[0].toString());
						avgbhsprice = Float.parseFloat(obj[1].toString());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		if (avghsprice == null) {
			avghsprice =goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
			avgbhsprice =goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		}

		if (g == null) {
			g = new Goods();
			g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
			g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
			g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
			g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());// 期初量
			g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());// 库存量
			g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
			g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
			g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
			g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
			g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
			g.setGoodsPrice(avgbhsprice);// 含税价格 统一在下面赋值
			g.setGoodsBuyPrice(avghsprice);// 单批次采购单价
			g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应
			g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
			g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 规格
			g.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
			g.setWgType(goodsStore.getWgType());// 物料类别
			g.setKgliao(goodsStore.getKgliao());// 供料属性
			g.setProNumber(goodsStore.getProNumber());// 项目编号
			g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本号
			g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 转换数量
			g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
			g.setGoodsStyle(goodsStore.getStyle());
			g.setLendNeckStatus(goodsStore.getLendNeckStatus());// 借领属性;//综合库使用
			g.setLendNeckCount(goodsStore.getLendNeckCount());
			g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间
			g.setGoodsround(goodsStore.getGoodsStoreround());// 质检周期
			g.setGoodsnexttime(goodsStore.getGoodsStorenexttime());// 下次质检时间
			g.setDemanddept(goodsStore.getGoodsStoreLuId());//需求部门
			if (rutype.equals("半成品转库")) {
				g.setGoodsStyle("半成品转库");
				goodsStore.setStyle("半成品转库");
				g.setProcessNo(processNo);
				g.setProcessName(processName);
				goodsStore.setProcessNo(processNo);
			}
			if (rutype.equals("半成品转库2")) {
				g.setGoodsStyle("采购入库");
				goodsStore.setStyle("采购入库");
				g.setProcessNo(processNo);
				g.setProcessName(processName);
				goodsStore.setProcessNo(processNo);
			}
			totalDao.save(g);
			// System.out.print(goodsStore.getWgType() + "  " + g.getWgType());
		} else {
			g.setGoodsCurQuantity(g.getGoodsCurQuantity()
					+ goodsStore.getGoodsStoreCount());
			g.setGoodsPrice(avgbhsprice);// 含税价格 统一在下面赋值
			g.setGoodsBuyPrice(avghsprice);// 单批次采购单价
			totalDao.update(g);
		}
		if (accountsDate != null) {
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())
					|| "monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_banben = "";
				if (goodsStore.getBanBenNumber() != null
						&& goodsStore.getBanBenNumber().length() > 0) {
					hql_banben = " and banBenNumber='"
							+ goodsStore.getBanBenNumber() + "'";
				}
				// 同步更新现有库存价格
				String hql_allGoods = "from Goods where goodsMarkId=? and goodsClass=? and goodsCurQuantity>0 and goodsPrice is not null "
						+ hql_banben;
				List<Goods> list_goods = totalDao.query(hql_allGoods,
						goodsStore.getGoodsStoreMarkId(), goodsStore
								.getGoodsStoreWarehouse());
				for (Goods goods : list_goods) {
					try {
						goods.setGoodsPrice(avgbhsprice);// 不含税单价（财务做帐用）
						goods.setGoodshsPrice(avghsprice);// 含税价格
						goods.setGoodsBuyPrice(goodsStore.getHsPrice().floatValue());// 单批次含税采购单价
						goods.setGoodsBuyBhsPrice(goodsStore
								.getGoodsStorePrice().floatValue());// 单批次不含税采购单价
						goods
								.setTaxprice(goodsStore.getTaxprice()
										.floatValue());// 税率
					} catch (Exception e) {
						e.printStackTrace();
					}
					totalDao.update(goods);
				}
			}
		}

		if (rutype.equals("半成品转库") || rutype.equals("半成品转库2")) {
			for (Object[] objs : procardAndcount) {
				Procard procard = (Procard) objs[0];
				if (procard != null) {
					Float count = (Float) objs[1];
					if (procard.getZaizhizkCount() == null) {
						procard.setZaizhizkCount(count);
					} else {
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								+ count);
					}
					if (procard.getZaizhikzkCount() != null
							&& procard.getFilnalCount() != null
							&& procard.getZaizhikzkCount() > procard
									.getFilnalCount()) {
						AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
								"件号:" + procard.getMarkId() + "批次:"
										+ procard.getSelfCard() + "半成品转库数量为："
										+ procard.getZaizhikzkCount()
										+ "大于批次数量，系统自动修复为"
										+ procard.getFilnalCount()
										+ "，操作是：半成品 接收入库 ,当前系统时间为"
										+ Util.getDateTime(), "2");
						procard.setZaizhikzkCount(procard.getFilnalCount());
					}
					totalDao.update(procard);
					// 生产件和零件中间表
					// 添加零件与在制品关系表
					ProcardProductRelation pprelation = new ProcardProductRelation();
					pprelation.setAddTime(Util.getDateTime());
					pprelation.setProcardId(procard.getId());
					pprelation.setGoodsId(g.getGoodsId());
					pprelation.setZyCount(count);
					pprelation.setFlagType("本批在制品");
					totalDao.save(pprelation);
					// 查询下到工序是否外委
					String nextWwhql = "from ProcessInfor where processNO>? and procard.id=?  and (dataStatus is null or dataStatus!='删除') order by processNO";
					List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
							.query(nextWwhql, processNo, procard.getId());
					if (nextWwProcessInforList.size() > 0) {
						int n = 0;
						WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
						for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
							if (nextWwProcessInfor != null) {
								if (n == 0
										&& !"外委".equals(nextWwProcessInfor
												.getProductStyle())) {
									// 查询下道工序是否手动外委
									if ((nextWwProcessInfor.getApplyWwCount() != null && nextWwProcessInfor
											.getApplyWwCount() > 0)
											|| (nextWwProcessInfor
													.getSelectWwCount() != null && nextWwProcessInfor
													.getSelectWwCount() > 0)
											|| (nextWwProcessInfor
													.getAgreeWwCount() != null && nextWwProcessInfor
													.getAgreeWwCount() > 0)) {
										g.setDtcFlag("外协调委外");
										break;
									}
								}
								if ("外委".equals(nextWwProcessInfor
										.getProductStyle())
										&& (n == 0 || ("yes")
												.equals(nextWwProcessInfor
														.getProcessStatus()))) {
									if (n == 0) {
										// 外协库存标记为待调仓
										g.setDtcFlag("外协调委外");
										totalDao.update(g);
										wwp.setRootMarkId(procard
												.getRootMarkId());
										wwp.setRootSelfCard(procard
												.getRootSelfCard());
										wwp.setOrderNum(procard
												.getOrderNumber());
										wwp.setYwMarkId(procard.getYwMarkId());
										wwp
												.setBanben(procard
														.getBanBenNumber());
										wwp.setBanci(procard.getBanci());
										wwp.setMarkId(procard.getMarkId());
										wwp.setProcessNo(nextWwProcessInfor
												.getProcessNO()
												+ "");
										wwp.setProName(procard.getProName());
										wwp.setProcessName(nextWwProcessInfor
												.getProcessName());
										wwp.setType("外委");
										Float wwCount = 0f;
										wwp.setNumber(count);
										wwp.setBeginCount(count);
										wwCount = count;
										wwp.setAddTime(Util.getDateTime());
										wwp.setJihuoTime(Util.getDateTime());
										wwp.setShArrivalTime(procard
												.getNeedFinalDate());// 应到货时间在采购确认通知后计算
										wwp.setUnit(procard.getUnit());
										wwp.setCaigouMonth(Util
												.getDateTime("yyyy-MM月"));// 采购月份
										String wwNumber = "";
										String before = null;
										Integer bIndex = 10;
										before = "ww"
												+ Util.getDateTime("yyyyMMdd");
										Integer maxNo = 0;
										String maxNumber = (String) totalDao
												.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
														+ before + "%'");
										if (maxNumber != null) {
											String wwnum = maxNumber.substring(
													bIndex, maxNumber.length());
											try {
												Integer maxNum = Integer
														.parseInt(wwnum);
												if (maxNum > maxNo) {
													maxNo = maxNum;
												}
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										maxNo++;
										wwNumber = before
												+ String.format("%03d", maxNo);
										wwp.setPlanNumber(wwNumber);// 采购计划编号
										wwp.setSelfCard(procard.getSelfCard());// 批次
										// wwp.setGysId(nextWwProcessInfor
										// .getZhuserId());// 供应商id
										// wwp.setGysName(nextWwProcessInfor.getGys());//
										// 供应商名称
										wwp.setAllJiepai(nextWwProcessInfor
												.getAllJiepai());// 单件总节拍
										wwp
												.setDeliveryDuration(nextWwProcessInfor
														.getDeliveryDuration());// 耽误时长
										wwp.setSingleDuration(procard
												.getSingleDuration());// 单班时长(工作时长)
										wwp.setProcardId(procard.getId());
										wwp.setProcard(procard);
										wwp.setStatus("待入库");
										totalDao.save(wwp);
										// wgSet.add(wwp);
									} else {
										wwp.setProcessNo(wwp.getProcessNo()
												+ ";"
												+ nextWwProcessInfor
														.getProcessNO());
										wwp.setProcessName(wwp.getProcessName()
												+ ";"
												+ nextWwProcessInfor
														.getProcessName());
										totalDao.update(wwp);
									}
								} else {
									break;
								}
							} else {
								break;
							}
							n++;
						}
						if (wwp.getId() != null) {
							// 匹配供应商
							Price price = (Price) totalDao
									.getObjectByCondition(
											"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
													+ Util.getDateTime()
													+ "')  order by hsPrice",
											wwp.getMarkId(), wwp.getProcessNo());
							if (price != null) {
								wwp.setPriceId(price.getId());
								wwp.setGysId(price.getGysId());
								ZhUser zhUser = (ZhUser) totalDao
										.getObjectById(ZhUser.class, price
												.getGysId());
								wwp.setGysName(zhUser.getName());
								wwp.setUserCode(zhUser.getUsercode());
								wwp.setUserId(zhUser.getUserid());
								totalDao.update(wwp);
							}
						}
					}
				}
			}
		}

		// 判断仓区，成品面积有才更新仓区占地面积
		// 仓区
		GoodHouse goodHouse = (GoodHouse) totalDao
				.get(
						"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
						new Object[] { goodsStore.getGoodsStoreWarehouse(),
								goodsStore.getGoodHouseName() });
		if (goodHouse != null) {
			// 单件
			Double procardArea = (Double) totalDao
					.getObjectByCondition(
							"select actualUsedProcardArea from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is null) and (banbenStatus='默认' or banbenStatus is null or banbenStatus='') order by actualUsedProcardArea desc",
							new Object[] { goodsStore.getGoodsStoreMarkId() });
			if (procardArea != null) {
				// 存在面积
				if (goodHouse.getGoodAllArea() > 0
						&& goodHouse.getGoodAllArea() != null
						&& procardArea != null && procardArea > 0) {
					// 单件占地面积goodsStoreCount
					Double procardActualUsed = procardArea
							* goodsStore.getGoodsStoreCount();
					procardActualUsed = Double
							.valueOf(new DecimalFormat("0.00")
									.format(procardActualUsed
											+ goodHouse.getGoodIsUsedArea()));
					goodHouse.setGoodIsUsedArea(procardActualUsed);
					goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()
							- goodHouse.getGoodIsUsedArea());
					totalDao.update(goodHouse);
				}
			}
		}
		goodshuizong1("入库", goodsStore, null);
		return g;
	}

	/**
	 * 计算单张重量
	 * 
	 * @param goodsStore
	 */
	private static void jisuanDanZhang(GoodsStore goodsStore) {
		TotalDao totalDao = createTotol();
		String hql_bili = " from YuanclAndWaigj where markId = ? and bili is not null and bili >0";
		YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao.getObjectByCondition(
				hql_bili, goodsStore.getGoodsStoreMarkId());
		if (wgj != null) {
			Double zhishu = Math.ceil(goodsStore.getGoodsStoreCount()
					/ wgj.getBili());
			goodsStore.setGoodsStoreZhishu(zhishu.floatValue());
			goodsStore.setGoodsStoreZHUnit(wgj.getCkUnit());
		}
	}

	/**
	 * 供应商年月日季周合格信息维护
	 */
	// public void saveGysInfor(GoodsStore goodsStore, WaigouPlan waigouPlan,
	// WaigouDeliveryDetail wddOld) {
	// ZhUser zhUser = (ZhUser) totalDao.getObjectById(ZhUser.class,
	// waigouPlan.getGysId());
	// if (zhUser != null) {
	// Float number = 0f;// 合格外购件入库数量
	// if (goodsStore.getGoodsStoreZhishu() != null
	// && goodsStore.getGoodsStoreZhishu() > 0) {
	// number = goodsStore.getGoodsStoreZhishu();
	// } else if (goodsStore.getGoodsStoreCount() != null) {
	// number = goodsStore.getGoodsStoreCount();
	// }
	// /*** 生成供应商年度合格总表 ***/
	// // 查询供应商年度合格总表
	// String year = Util.getDateTime("yyyy");// 当前日期
	// YearHege years = (YearHege) totalDao.getObjectByCondition(
	// "from YearHege where gysid = ? and year = ?", zhUser
	// .getId(), year);
	// if (years != null) {
	// years.setHgCount(years.getHgCount() + number);
	// years.setQualifiedrate(Util.towWei(years.getHgCount()
	// / years.getShCount() * 100));// 计算月度合格率
	// totalDao.update(years);
	// } else {
	// years = new YearHege(year, zhUser.getId(), zhUser.getCmp(),
	// number, wddOld.getShNumber());
	// years.setQualifiedrate(Util.towWei(years.getHgCount()
	// / years.getShCount() * 100));
	// totalDao.save(years);
	// }
	// /*** 生成供应商年度合格总表 ***/
	// /*** 生成供应商季度合格总表 ***/
	// /*** 生成供应商季度合格总表 ***/
	// /*** 生成供应商月度合格总表 ***/
	// String yuefen = Util.getDateTime("yyyy-MM");// 当前月份
	// // 查询供应商月度合格总表
	// MouthHege delivery = (MouthHege) totalDao.getObjectByCondition(
	// "from MouthHege where gysid = ? and mouth = ?", zhUser
	// .getId(), yuefen);
	// if (delivery != null) {
	// delivery.setHgCount(delivery.getHgCount() + number);
	// delivery.setQualifiedrate(Util.towWei(delivery.getHgCount()
	// / delivery.getShCount() * 100));// 计算月度合格率
	// totalDao.update(delivery);
	// } else {
	// delivery = new MouthHege(yuefen, zhUser.getId(), zhUser
	// .getCmp(), number, wddOld.getShNumber());
	// delivery.setQualifiedrate(Util.towWei(delivery.getHgCount()
	// / delivery.getShCount() * 100));
	// totalDao.save(delivery);
	// }
	// /*** 生成供应商月度合格总表 ***/
	// /*** 生成供应商周合格总表 ***/
	// /*** 生成供应商周合格总表 ***/
	// /*** 生成供应商天合格总表 ***/
	// // 查询供应商天合格总表
	// String date = Util.getDateTime("yyyy-MM-dd");// 当前日期
	// DayHege day = (DayHege) totalDao.getObjectByCondition(
	// "from DayHege where gysid = ? and day = ?", zhUser.getId(),
	// date);
	// if (day != null) {
	// day.setHgCount(delivery.getHgCount() + number);
	// day.setQualifiedrate(Util.towWei(delivery.getHgCount()
	// / delivery.getShCount() * 100));// 计算月度合格率
	// totalDao.update(day);
	// } else {
	// day = new DayHege(date, zhUser.getId(), zhUser.getCmp(),
	// number, wddOld.getShNumber());
	// day.setQualifiedrate(Util.towWei(delivery.getHgCount()
	// / delivery.getShCount() * 100));
	// totalDao.save(day);
	// }
	// /*** 生成供应商天合格总表 ***/
	// }
	// }

	@Override
	public void saveSgrk(GoodsStore goodsStore) {
		Goods g = new Goods();
		String nexttime = getNexttime(goodsStore);
		g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
		g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本
		g.setKgliao(goodsStore.getKgliao());// 供料属性
		g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 库别
		g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
		g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
		g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
		g.setWgType(goodsStore.getWgType());// 物料类别

		g.setProNumber(goodsStore.getProNumber());// 项目编号
		g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
		g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 规格
		g.setGoodsStyle(goodsStore.getStyle());// 入库类型（/返修入库/退货入库/批产/试制/中间件）
		g.setInputSource(goodsStore.getInputSource());// 入库来源
		g.setSuodingdanhao(goodsStore.getSuodingdanhao());
		g.setLendNeckStatus(goodsStore.getLendNeckStatus());//借领属性
		if("借".equals(g.getLendNeckStatus())){
			g.setLendNeckCount(g.getGoodsCurQuantity());
		}
		if ("不合格品库".equals(goodsStore.getGoodsStoreWarehouse())
				|| "待检库".equals(goodsStore.getGoodsStoreWarehouse())) {
			g.setBout(false);
			g.setBout(false);
		}
		String hql_yw = "from YuanclAndWaigj where markId=? and  bili>0 and (banbenStatus is null or banbenStatus !='历史')";
		YuanclAndWaigj yw = (YuanclAndWaigj) totalDao.getObjectByCondition(
				hql_yw, goodsStore.getGoodsStoreMarkId());
		if (yw != null
				&& (goodsStore.getGoodsStoreZhishu() == null || goodsStore
						.getGoodsStoreZhishu() <= 0)) {
			Float yushu = goodsStore.getGoodsStoreCount() % yw.getBili();
			Double zhishu = 0d;
			if (yushu <= 0.05) {
				zhishu = Math.floor(goodsStore.getGoodsStoreCount()
						/ yw.getBili());
			} else {
				zhishu = Math.ceil(goodsStore.getGoodsStoreCount()
						/ yw.getBili());
			}
			goodsStore.setGoodsStoreZhishu(zhishu.floatValue());
		}
		totalDao.save(goodsStore);

		Float avghsprice = goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
		Float avgbhsprice =goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		// 查询成本核算方法
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		if (accountsDate != null) {
			String hql_banben = "";
			if (goodsStore.getBanBenNumber() != null
					&& goodsStore.getBanBenNumber().length() > 0) {
				hql_banben = " and banBenNumber='"
						+ goodsStore.getBanBenNumber() + "'";
			}
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben;

				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					avghsprice = Float.parseFloat(obj[0].toString());
					avgbhsprice = Float.parseFloat(obj[1].toString());
				}
			} else if ("monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount) ,sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben
						+ " and goodsStoreTime >'"
						+ Util.getDateTime("yyyy-MM")
						+ "-01' and goodsStoreTime <'"
						+ Util.getDateTime("yyyy-MM")
						+ "-"
						+ accountsDate.getJihao() + "' ";
				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					try {
						avghsprice = Float.parseFloat(obj[0].toString());
						avgbhsprice = Float.parseFloat(obj[1].toString());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		if (avghsprice == null) {
			avghsprice = goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
			avgbhsprice = goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		}

		Goods s = null;
		// 累加库存依据：外购件库/占用库/在途库： 件号+版本+供料属性+库别+仓区+库位+批次
		// 累加库存依据：成品库库： 件号+版本+库别+仓区+库位+批次
		String kgSql = "";
		// 供料属性
		if (goodsStore.getGoodsStoreWarehouse().equals("外购件库")
				|| goodsStore.getGoodsStoreWarehouse().equals("占用库")
				|| goodsStore.getGoodsStoreWarehouse().equals("在途库")) {
			kgSql += " and kgliao='" + goodsStore.getKgliao() + "'";
		}
		// 版本
		String banben_hql = "";
		if (g.getBanBenNumber() != null && g.getBanBenNumber().length() > 0) {
			banben_hql = " and banBenNumber='" + g.getBanBenNumber() + "'";
		}
		// 仓区
		String houseName_hql = "";
		if (g.getGoodHouseName() != null && g.getGoodHouseName().length() > 0) {
			houseName_hql = " and goodHouseName='" + g.getGoodHouseName() + "'";
		}
		// 库位
		String position_hql = "";
		if (g.getGoodsPosition() != null && g.getGoodsPosition().length() > 0) {
			position_hql = " and goodsPosition='" + g.getGoodsPosition() + "'";
		}
		// 批次
		String goodsLotId_hql = "";
		if (g.getGoodsLotId() != null && g.getGoodsLotId().length() > 0) {
			goodsLotId_hql = " and goodsLotId='" + g.getGoodsLotId() + "'";
		}
		String suodingdanhao_hql = "";
		if (g.getSuodingdanhao() != null && g.getSuodingdanhao().length() > 0) {
			suodingdanhao_hql = " and suodingdanhao = '" + g.getSuodingdanhao()
					+ "'";
		}

		String hql = "from Goods where goodsMarkId = ? " + goodsLotId_hql
				+ banben_hql + kgSql + " and goodsClass=? " + houseName_hql
				+ position_hql + suodingdanhao_hql
				+ " and (fcStatus is null or fcStatus='可用')";
		s = (Goods) totalDao.getObjectByCondition(hql, new Object[] {
				g.getGoodsMarkId(), g.getGoodsClass() });
		if (s == null) {
			g.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
			if (goodsStore.getBeginning_num() != null) {
				g.setGoodsBeginQuantity(goodsStore.getBeginning_num());// 起初数量
				g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount()
						+ goodsStore.getBeginning_num());// 数量
			} else {
				g.setGoodsBeginQuantity(0F);// 起初数量
				g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount() + 0F);// 数量
			}

			g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
			g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
			g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
			g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
			g.setShplanNumber(goodsStore.getGoodsStoreSendId());// 送货单号

			try {
				g.setGoodsPrice(avgbhsprice);// 不含税单价（财务做帐用）
				g.setGoodshsPrice(avghsprice);// 含税价格

				g.setGoodsBuyPrice(goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue());// 单批次含税采购单价
				g.setGoodsBuyBhsPrice(goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue());// 单批次不含税采购单价
				g.setTaxprice(goodsStore.getTaxprice()==null?null:goodsStore.getTaxprice().floatValue());// 税率
			} catch (Exception e) {
				e.printStackTrace();
			}

			g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应.
			g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
			g.setGoodHouseName(goodsStore.getGoodHouseName());// 区名
			if (goodsStore.getGoodsStoreZhishu() != null) {
				g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu() == 0 ? null
						: goodsStore.getGoodsStoreZhishu());// 转换数量数
			}
			g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
			g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 規格
			g.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());// 客户
			g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());
			g.setBanBenNumber(goodsStore.getBanBenNumber());
			g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间;
			g.setGoodsnexttime(nexttime);// 下次质检时间;
			g.setGoodsround(goodsStore.getGoodsStoreround());// 质检周期
			g.setTuhao(goodsStore.getTuhao());// 图号
			g.setNeiorderId(goodsStore.getNeiorderId());// 内部订单号
			g.setWaiorderId(goodsStore.getWaiorderId());// 外部订单号
			g.setOrder_Id(goodsStore.getOrder_Id());
			totalDao.save(g);
		} else {
			// s.setGoodsPrice(allAvgprice);// 含税价格
			s.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
			s.setGoodsCurQuantity(s.getGoodsCurQuantity()
					+ goodsStore.getGoodsStoreCount());
			s.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位;
			s.setGoodsnexttime(nexttime);// 下次质检时间;
			s.setTuhao(goodsStore.getTuhao());// 图号
			if (goodsStore.getGoodsStoreZhishu() != null
					&& goodsStore.getGoodsStoreZhishu() > 0F) {
				s.setGoodsZhishu((s.getGoodsZhishu() == null ? 0 : s
						.getGoodsZhishu())
						+ goodsStore.getGoodsStoreZhishu());
			}

			totalDao.update(s);
		}
		if (accountsDate != null) {
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())
					|| "monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_banben = "";
				if (goodsStore.getBanBenNumber() != null
						&& goodsStore.getBanBenNumber().length() > 0) {
					hql_banben = " and banBenNumber='"
							+ goodsStore.getBanBenNumber() + "'";
				}
				// 同步更新现有库存价格
				String hql_allGoods = "from Goods where goodsMarkId=? and goodsClass=? and goodsCurQuantity>0 and goodsPrice is not null "
						+ hql_banben;
				List<Goods> list_goods = totalDao.query(hql_allGoods,
						goodsStore.getGoodsStoreMarkId(), goodsStore
								.getGoodsStoreWarehouse());
				for (Goods goods : list_goods) {
					goods.setGoodsPrice(avgbhsprice);// 不含税单价（财务做帐用）
					goods.setGoodshsPrice(avghsprice);// 含税价格
					totalDao.update(goods);
				}
			}
		}

		// ProductManager
		goodsStore.setGoodsStorenexttime(nexttime);// 下次质检时间;
		g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间;
		goodsStore.setOrder_Id(g.getOrder_Id());
		totalDao.update(goodsStore);
		if (goodsStore.getOrder_Id() != null
				&& !"不合格品库".equals(goodsStore.getGoodsStoreWarehouse())) {
			String hql_pm = "from ProductManager where orderManager.id=? and pieceNumber = ?";
			ProductManager pm = (ProductManager) totalDao.getObjectByCondition(
					hql_pm, goodsStore.getOrder_Id(), goodsStore
							.getGoodsStoreMarkId());
			if (pm != null) {
				Float hasTurn = pm.getHasTurn();
				Float allocationsNum = pm.getAllocationsNum();
				Float totalCount = pm.getNum();
				if (totalCount == null || totalCount < 0) {
					totalCount = 0f;
				}
				if (hasTurn == null || hasTurn < 0) {
					hasTurn = 0f;
				}
				if (allocationsNum == null || allocationsNum < 0) {
					allocationsNum = 0f;
				}
				float count = goodsStore.getGoodsStoreCount();
				if (count > (totalCount.floatValue() - allocationsNum
						.floatValue())) {
					throw new RuntimeException(
							"对不起此订单入库数量最大为:totalCount-allocationsNum");
				}
				pm.setHasTurn(hasTurn + count);
				pm.setAllocationsNum(allocationsNum + (int) count);
				totalDao.update(pm);
				OrderManager o = (OrderManager) totalDao.get(
						OrderManager.class, goodsStore.getOrder_Id());
				if (o != null) {
					String hql5 = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					List list = totalDao.query(hql5, goodsStore.getOrder_Id());
					if (list != null && list.size() > 0 && list.get(0) != null) {
						String inpuSql = "select sum(allocationsNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
						Float pronum = (Float) list.get(0);
						Float inputNum = (Float) totalDao.getObjectByCondition(
								inpuSql, goodsStore.getOrder_Id());
						if (inputNum == null) {
							inputNum = 0f;
						}

						if (pronum == 0 || pronum == null) {
							o.setInputRate(0F);
						} else {
							o.setInputRate(inputNum / pronum * 100);
						}
						if (o.getInputRate() > 0) {
							totalDao.update(o);
						}
					} else {
						o.setInputRate(0F);
					}
				}

			}
		}
	}

	private String getNexttime(GoodsStore goodsStore) {
		String nexttime = "";
		String lasttime = "";
		if (goodsStore.getGoodsStorelasttime() != null
				&& !"".equals(goodsStore.getGoodsStorelasttime())) {
			lasttime = goodsStore.getGoodsStorelasttime();
		} else {
			lasttime = Util.getDateTime("yyyy-MM-dd");
		}
		if (goodsStore.getGoodsStoreround() == null
				|| goodsStore.getGoodsStoreround() <= 0) {
			String baneben_hql = "";
			if (goodsStore.getBanBenNumber() != null
					&& goodsStore.getBanBenNumber().length() > 0) {
				baneben_hql = " and banbenhao = '"
						+ goodsStore.getBanBenNumber() + "'";
			}

			if ("外协库".equals(goodsStore.getGoodsStoreWarehouse())) {
				goodsStore.setGoodsStoreround(120f);
//				YuanclAndWaigj yaw = (YuanclAndWaigj) totalDao
//						.getObjectByCondition(
//								" from YuanclAndWaigj where markId=?  "
//										+ baneben_hql
//										+ " and (banbenStatus is null or banbenStatus <> '历史' )",
//								goodsStore.getGoodsStoreMarkId());
//				if (yaw != null && yaw.getRound() != null && yaw.getRound() > 0) {
//					goodsStore.setGoodsStoreround(yaw.getRound());
//				} else {
//					Category category = (Category) totalDao
//							.getObjectByCondition(
//									" from Category where name =? and type = '物料类别'",
//									goodsStore.getWgType());
//					if (category != null && category.getRound() != null
//							&& category.getRound() > 0) {
//						goodsStore.setGoodsStoreround(category.getRound());
//					} else {
//						goodsStore.setGoodsStoreround(120f);
//					}
//				}
			}
		}
		if (goodsStore.getGoodsStoreround() != null
				&& !"".equals(goodsStore.getGoodsStoreround())) {
			nexttime = Util.getSpecifiedDayAfter(lasttime, goodsStore
					.getGoodsStoreround().intValue());
		}
		return nexttime;
	}

	@Override
	public GoodsStore get(GoodsStore goodsStore) {
		return (GoodsStore) totalDao.get(GoodsStore.class, goodsStore
				.getGoodsStoreId());
	}

	@Override
	public Object[] find(int size, int page, GoodsStore goodsStore,
			List<String> viewList, String pageStatus) {
		Object[] o = null;
		String hql_cq = " and goodHouseName in (";
		String changqu = "";
		if (goodsStore != null && goodsStore.getGoodHouseName() != null
				&& goodsStore.getGoodHouseName().length() > 0) {
			String str = "";
			String[] cangqus = goodsStore.getGoodHouseName().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					if (!"".equals(cangqus[i])) {
						str += ",'" + cangqus[i] + "'";
					}
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			changqu = goodsStore.getGoodHouseName();
			goodsStore.setGoodHouseName(null);
			hql_cq += ")";
		} else {
			hql_cq = "";
		}

		String hql_kubie = " and goodsStoreWarehouse in (";
		String kubie = "";
		if (goodsStore != null && goodsStore.getGoodsStoreWarehouse() != null
				&& goodsStore.getGoodsStoreWarehouse().length() > 0) {
			String str = "";
			String[] kubies = goodsStore.getGoodsStoreWarehouse().split(",");
			if (kubies != null && kubies.length > 0) {
				for (int i = 0; i < kubies.length; i++) {
					if (!"".equals(kubies[i])) {
						str += ",'" + kubies[i] + "'";
					}
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_kubie += str;
			}
			kubie = goodsStore.getGoodsStoreWarehouse();
			goodsStore.setGoodsStoreWarehouse(null);
			hql_kubie += ")";
		} else {
			hql_kubie = "";
		}
		String hql = "from GoodsStore t where 1=1  ";
		Map<String, Object> params = new HashMap<String, Object>();
		hql = addWhere(hql, params, goodsStore, viewList);
		// System.out.println(hql);
		if (goodsStore != null && goodsStore.getYwmarkId() != null
				&& !"".equals(goodsStore.getYwmarkId())) {
			hql += " and ywmarkId like '%" + goodsStore.getYwmarkId() + "%'";
		}
		if (goodsStore != null && goodsStore.getNeiorderId() != null
				&& !"".equals(goodsStore.getNeiorderId())) {
			hql += " and neiorderId like '%" + goodsStore.getNeiorderId()
					+ "%'";
		}
		if (goodsStore != null && goodsStore.getSuodingdanhao() != null
				&& !"".equals(goodsStore.getSuodingdanhao())) {
			hql += " and suodingdanhao like '%" + goodsStore.getSuodingdanhao()
					+ "%'";
		}
		if ("updatePrice".equals(pageStatus)) {
			hql += " and goodsStoreWarehouse in ('外购件库','外协库') and (hsPrice is null or hsPrice =0 ) ";
		}
		String hqlCount = "select count(*) " + hql + hql_cq + hql_kubie;
		hql += hql_cq + hql_kubie + " order by goodsStoreId desc ";
		List<Object[]> query = (List) totalDao.find(hql, params, page, size);
		if (goodsStore != null) {
			goodsStore.setGoodHouseName(changqu);
			goodsStore.setGoodsStoreWarehouse(kubie);
		}
		long count = totalDao.count(hqlCount, params);
		o = new Object[] { query, count };
		return o;
	}

	public List<GoodsStore> find(GoodsStore goodsStore, List<String> viewList,
			String pageStatus) {

		String hql_cq = " and goodHouseName in (";
		String changqu = "";
		if (goodsStore != null && goodsStore.getGoodHouseName() != null
				&& goodsStore.getGoodHouseName().length() > 0) {
			String str = "";
			String[] cangqus = goodsStore.getGoodHouseName().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			changqu = goodsStore.getGoodHouseName();
			goodsStore.setGoodHouseName(null);
			hql_cq += ")";
		} else {
			hql_cq = "";
		}

		String hql_kubie = " and goodsStoreWarehouse in (";
		String kubie = "";
		if (goodsStore != null && goodsStore.getGoodsStoreWarehouse() != null
				&& goodsStore.getGoodsStoreWarehouse().length() > 0) {
			String str = "";
			String[] kubies = goodsStore.getGoodsStoreWarehouse().split(",");
			if (kubies != null && kubies.length > 0) {
				for (int i = 0; i < kubies.length; i++) {
					str += ",'" + kubies[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_kubie += str;
			}
			kubie = goodsStore.getGoodsStoreWarehouse();
			goodsStore.setGoodsStoreWarehouse(null);
			hql_kubie += ")";
		} else {
			hql_kubie = "";
		}
		String hql = "from GoodsStore t where 1=1  ";
		Map<String, Object> params = new HashMap<String, Object>();
		hql = addWhere(hql, params, goodsStore, viewList);
		if (goodsStore != null && goodsStore.getYwmarkId() != null
				&& !"".equals(goodsStore.getYwmarkId())) {
			hql += " and ywmarkId like '%" + goodsStore.getYwmarkId() + "%'";
		}
		if (goodsStore != null && goodsStore.getNeiorderId() != null
				&& !"".equals(goodsStore.getNeiorderId())) {
			hql += " and neiorderId like '%" + goodsStore.getNeiorderId()
					+ "%'";
		}
		hql += hql_cq + hql_kubie + " order by goodsStoreId desc";
		List<GoodsStore> goodsStores = totalDao.find(hql, params,1,40000);

		try {
			// 取得HttpServletResponse
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();// 取得输出流
			response.reset();// 清空输出流
			if(goodsStores.size()>60000){
				response.setHeader("Content-disposition", "attachment; filename="
						+ new String("入库历史记录".getBytes("GB2312"), "8859_1")
						+ ".xlsx");// 设定输出文件头
			}else {
				response.setHeader("Content-disposition", "attachment; filename="
						+ new String("入库历史记录".getBytes("GB2312"), "8859_1")
						+ ".xls");// 设定输出文件头
			}

			response.setContentType("application/msexcel");// 定义输出类型

			SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
			org.apache.poi.ss.usermodel.Sheet sheet = workBook
					.createSheet("入库历史记录");
			Row row = sheet.createRow(2);
			CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 1, 30);
			CellStyle style = workBook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			Font font = workBook.createFont();
			font.setFontHeightInPoints((short) 16);
			font.setBold(true);
			style.setFont(font);
			sheet.addMergedRegion(rangeAddress);
			row = sheet.createRow(0);
			org.apache.poi.ss.usermodel.Cell cell = row.createCell(1);
			cell.setCellValue("入库历史记录");
			cell.setCellStyle(style);

			row = sheet.createRow(1);
			cell = row.createCell(0, CellType.STRING);
			cell.setCellValue("序号");
			cell = row.createCell(1, CellType.STRING);
			cell.setCellValue("收货仓库");
			cell = row.createCell(2, CellType.STRING);
			cell.setCellValue("仓区");
			cell = row.createCell(3, CellType.STRING);
			cell.setCellValue("库位");
			cell = row.createCell(4, CellType.STRING);
			cell.setCellValue("件号");
			cell = row.createCell(5, CellType.STRING);
			cell.setCellValue("批次");
			cell = row.createCell(6, CellType.STRING);
			cell.setCellValue("版本号");
			cell = row.createCell(7, CellType.STRING);
			cell.setCellValue("供料属性");
			cell = row.createCell(8, CellType.STRING);
			cell.setCellValue("物料名称");
			cell = row.createCell(9, CellType.STRING);
			cell.setCellValue("规格");
			cell = row.createCell(10, CellType.STRING);
			cell.setCellValue("物料类别");
			cell = row.createCell(11, CellType.STRING);
			cell.setCellValue("实收数量");
			cell = row.createCell(12, CellType.STRING);
			cell.setCellValue("单位");
			cell = row.createCell(13, CellType.STRING);
			cell.setCellValue("转换数量");
			cell = row.createCell(14, CellType.STRING);
			cell.setCellValue("入库类型");
			cell = row.createCell(15, CellType.STRING);
			cell.setCellValue("入库日期");
			cell = row.createCell(16, CellType.STRING);
			cell.setCellValue("入库时间");
			cell = row.createCell(17, CellType.STRING);
			cell.setCellValue("状态");
			cell = row.createCell(18, CellType.STRING);
			cell.setCellValue("申请单号");
			cell = row.createCell(19, CellType.STRING);
			cell.setCellValue("交货单位");
			cell = row.createCell(20, CellType.STRING);
			cell.setCellValue("供应商");
			cell = row.createCell(21, CellType.STRING);
			cell.setCellValue("内部订单号");
			cell = row.createCell(22, CellType.STRING);
			cell.setCellValue("入库班组");
			cell = row.createCell(23, CellType.STRING);
			cell.setCellValue("入库人");
			cell = row.createCell(24, CellType.STRING);
			cell.setCellValue("入库申请人");
			cell = row.createCell(25, CellType.STRING);
			cell.setCellValue("打印单号");
			cell = row.createCell(26, CellType.STRING);
			cell.setCellValue("送货单号");
			cell = row.createCell(27, CellType.STRING);
			cell.setCellValue("采购订单号");
			cell = row.createCell(28, CellType.STRING);
			cell.setCellValue("业务件号");
			cell = row.createCell(29, CellType.STRING);
			cell.setCellValue("制单");
			cell = row.createCell(30, CellType.STRING);
			cell.setCellValue("锁定单号");
			if ("price".equals(pageStatus)) {
				cell = row.createCell(31, CellType.STRING);
				cell.setCellValue("不含税单价");
				cell = row.createCell(32, CellType.STRING);
				cell.setCellValue("税率");
				cell = row.createCell(33, CellType.STRING);
				cell.setCellValue("含税单价");
				cell = row.createCell(34, CellType.STRING);
				cell.setCellValue("总额");
			}
			// 保留两位小数，
			DecimalFormat df = new DecimalFormat("#.####");
			for (int i = 0, len = goodsStores.size(); i < len; i++) {
				GoodsStore p = goodsStores.get(i);
				if (p.getGoodsStoreMarkId() != null
						&& !"".equals(p.getGoodsStoreMarkId())
						&& p.getGoodsStoreMarkId() != null) {
					if ("成品库".equals(p.getGoodsStoreWarehouse())
							|| "备货库".equals(p.getGoodsStoreWarehouse())) {
						PrintedOut printedout = (PrintedOut) totalDao
								.getObjectByCondition(
										" from PrintedOut "
												+ " where className = 'GoodsStore'  and entiyId = ? ",
										p.getGoodsStoreId());
						if (printedout != null) {
							p
									.setJhdw(printedout.getPrintedOutOrder()
											.getJhdw());
							p.setRukuGroup(printedout.getPrintedOutOrder()
									.getRukuGroup());
						}
					}
				}
				row = sheet.createRow(i + 2);
				cell = row.createCell(0, CellType.STRING);
				cell.setCellValue(i + 1);
				cell = row.createCell(1, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreWarehouse());
				cell = row.createCell(2, CellType.STRING);
				cell.setCellValue(p.getGoodHouseName());
				cell = row.createCell(3, CellType.STRING);
				cell.setCellValue(p.getGoodsStorePosition());
				cell = row.createCell(4, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreMarkId());
				cell = row.createCell(5, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreLot());
				cell = row.createCell(6, CellType.STRING);
				cell.setCellValue(p.getBanBenNumber());
				cell = row.createCell(7, CellType.STRING);
				cell.setCellValue(p.getKgliao());
				cell = row.createCell(8, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreGoodsName());
				cell = row.createCell(9, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreFormat());
				cell = row.createCell(10, CellType.STRING);
				cell.setCellValue(p.getWgType());
				if ("不合格品库".equals(p.getGoodsStoreWarehouse())) {
					cell = row.createCell(11, CellType.STRING);
					cell.setCellValue(Double.parseDouble(df.format(-1
							* p.getGoodsStoreCount())));
				} else {
					cell = row.createCell(11, CellType.STRING);
					cell.setCellValue(Double.parseDouble(df.format(p
							.getGoodsStoreCount())));
				}
				cell = row.createCell(12, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreUnit());
				cell = row.createCell(13, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreZhishu() + "");
				cell = row.createCell(14, CellType.STRING);
				cell.setCellValue(p.getStyle());
				cell = row.createCell(15, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreDate());
				cell = row.createCell(16, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreTime());
				cell = row.createCell(17, CellType.STRING);
				cell.setCellValue(p.getStatus());
				cell = row.createCell(18, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreNumber());
				cell = row.createCell(19, CellType.STRING);
				cell.setCellValue(p.getJhdw());

				cell = row.createCell(20, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreSupplier());
				cell = row.createCell(21, CellType.STRING);
				if (p.getNeiorderId() != null) {
					cell.setCellValue(p.getNeiorderId());
				}

				cell = row.createCell(22, CellType.STRING);
				cell.setCellValue(p.getRukuGroup());
				cell = row.createCell(23, CellType.STRING);
				cell.setCellValue(p.getGoodsStorePlanner());
				cell = row.createCell(24, CellType.STRING);
				cell.setCellValue(p.getSqUsersName());
				cell = row.createCell(25, CellType.STRING);
				cell.setCellValue(p.getPrintNumber());
				cell = row.createCell(26, CellType.STRING);
				cell.setCellValue(p.getGoodsStoreSendId());
				cell = row.createCell(27, CellType.STRING);
				cell.setCellValue(p.getNeiorderId());
				cell = row.createCell(28, CellType.STRING);
				cell.setCellValue(p.getYwmarkId());
				cell = row.createCell(29, CellType.STRING);
				cell.setCellValue(p.getZhidanPerson() == null ? "" : p
						.getZhidanPerson());
				cell = row.createCell(30, CellType.STRING);
				cell.setCellValue(p.getSuodingdanhao() == null ? "" : p
						.getSuodingdanhao());

				if ("price".equals(pageStatus)) {
					if (p.getGoodsStorePrice() == null) {
						cell = row.createCell(31, CellType.STRING);
						cell.setCellValue("");
					} else {
						cell = row.createCell(31, CellType.STRING);
						cell.setCellValue(Double.parseDouble(df.format(p
								.getGoodsStorePrice())));
					}
					if (p.getTaxprice() == null) {
						cell = row.createCell(32, CellType.STRING);
						cell.setCellValue("");
					} else {
						cell = row.createCell(32, CellType.STRING);
						cell.setCellValue(p.getTaxprice());
					}
					if (p.getHsPrice() == null) {
						cell = row.createCell(33, CellType.STRING);
						cell.setCellValue("");
					} else {
						cell = row.createCell(33, CellType.STRING);
						cell.setCellValue(df.format(p.getHsPrice()));
					}
					if (p.getMoney() == null) {
						cell = row.createCell(34, CellType.STRING);
						cell.setCellValue("");
					} else {
						cell = row.createCell(34, CellType.STRING);
						cell.setCellValue(Double.parseDouble(df.format(p
								.getMoney())));
					}
				}
			}
			workBook.write(os);
			workBook.close();// 记得关闭工作簿
		} catch (IOException e) {
			e.printStackTrace();
		}

		goodsStores.clear();
		return null;
	}

	private String addWhere(String hql, Map<String, Object> params,
			GoodsStore goodsStore, List<String> viewList) {
		if (goodsStore == null) {
			if (viewList != null && viewList.size() > 0) {
				hql += " and goodsStoreWarehouse in (:goodsStoreWarehouse) ";
				params.put("goodsStoreWarehouse", viewList);
			}
			return hql;
		}

		// 库别
		if (goodsStore.getGoodsStoreWarehouse() != null
				&& !goodsStore.getGoodsStoreWarehouse().equals("")) {
			hql += " and goodsStoreWarehouse in (:goodsStoreWarehouse) ";

			String[] strArr = goodsStore.getGoodsStoreWarehouse().split(",");
			List<String> wareHouses = new ArrayList<String>();
			for (String string : strArr) {
				wareHouses.add(string.trim());
			}
			params.put("goodsStoreWarehouse", wareHouses);
		} else {
			hql += " and goodsStoreWarehouse in (:goodsStoreWarehouse) ";
			params.put("goodsStoreWarehouse", viewList);
		}

		// 仓区
		if (goodsStore.getGoodHouseName() != null
				&& !goodsStore.getGoodHouseName().equals("")) {
			hql += " and goodHouseName like :goodHouseName ";
			params.put("goodHouseName", "%%" + goodsStore.getGoodHouseName()
					+ "%%");
		}
		// 库位
		if (goodsStore.getGoodsStorePosition() != null
				&& !goodsStore.getGoodsStorePosition().equals("")) {
			hql += " and goodsStorePosition like :goodsStorePosition ";
			params.put("goodsStorePosition", "%%"
					+ goodsStore.getGoodsStorePosition() + "%%");
		}

		// 件号
		if (goodsStore.getGoodsStoreMarkId() != null
				&& !goodsStore.getGoodsStoreMarkId().equals("")) {
			hql += " and goodsStoreMarkId like :goodsStoreMarkId ";
			params.put("goodsStoreMarkId", "%"
					+ goodsStore.getGoodsStoreMarkId() + "%");
		}
		// 版本
		if (goodsStore.getBanBenNumber() != null
				&& !goodsStore.getBanBenNumber().equals("")) {
			hql += " and banBenNumber like :banBenNumber ";
			params.put("banBenNumber", "%%" + goodsStore.getBanBenNumber()
					+ "%%");
		}
		// 供料属性
		if (goodsStore.getKgliao() != null
				&& !goodsStore.getKgliao().equals("")) {
			hql += " and kgliao like :kgliao ";
			params.put("kgliao", "%%" + goodsStore.getKgliao() + "%%");
		}

		// 批次
		if (goodsStore.getGoodsStoreLot() != null
				&& !goodsStore.getGoodsStoreLot().equals("")) {
			hql += " and t.goodsStoreLot like :goodsStoreLot ";
			params.put("goodsStoreLot", "%%" + goodsStore.getGoodsStoreLot()
					+ "%%");
		}
		// 物料类别
		if (goodsStore.getWgType() != null
				&& !goodsStore.getWgType().equals("")) {
			hql += " and wgType like :wgType ";
			params.put("wgType", "%%" + goodsStore.getWgType() + "%%");
		}

		// 品名
		if (goodsStore.getGoodsStoreGoodsName() != null
				&& !goodsStore.getGoodsStoreGoodsName().equals("")) {
			hql += " and goodsStoreGoodsName like :goodsStoreGoodsName ";
			params.put("goodsStoreGoodsName", "%%"
					+ goodsStore.getGoodsStoreGoodsName() + "%%");
		}
		// 规格
		if (goodsStore.getGoodsStoreFormat() != null
				&& !goodsStore.getGoodsStoreFormat().equals("")) {
			hql += " and goodsStoreFormat like :goodsStoreFormat ";
			params.put("goodsStoreFormat", "%%"
					+ goodsStore.getGoodsStoreFormat() + "%%");
		}
		// 客户
		if (goodsStore.getGoodsStoreCompanyName() != null
				&& !goodsStore.getGoodsStoreCompanyName().equals("")) {
			hql += " and goodsStoreCompanyName like :goodsStoreCompanyName ";
			params.put("goodsStoreCompanyName", "%%"
					+ goodsStore.getGoodsStoreCompanyName() + "%%");
		}
		// 供应商
		if (goodsStore.getGoodsStoreSupplier() != null
				&& !goodsStore.getGoodsStoreSupplier().equals("")) {
			hql += " and goodsStoreSupplier like :goodsStoreSupplier ";
			params.put("goodsStoreSupplier", "%%"
					+ goodsStore.getGoodsStoreSupplier() + "%%");
		}
		// 供应商Id
		if (goodsStore.getGysId() != null && !goodsStore.getGysId().equals("")) {
			hql += " and gysId like :gysId ";
			params.put("gysId", goodsStore.getGysId());
		}
		// 客户Id
		if (goodsStore.getCustomerId() != null
				&& !goodsStore.getCustomerId().equals("")) {
			hql += " and customerId like :customerId ";
			params.put("customerId", goodsStore.getCustomerId());
		}

		// 仓库
		// if(goodsStore.getGoodsStoreWarehouse() != null &&
		// !goodsStore.getGoodsStoreWarehouse().equals("")){
		// hql += " and goodsStoreWarehouse like :goodsStoreWarehouse ";
		// params.put("goodsStoreWarehouse","%%" +
		// goodsStore.getGoodsStoreWarehouse() + "%%");
		// }

		// 大于时间
		if (goodsStore.getStartDate() != null
				&& !goodsStore.getStartDate().equals("")) {
			hql += " and date(goodsStoreDate) >=  '"
					+ goodsStore.getStartDate() + " 00:00:00'";
			// params.put("startDate", goodsStore.getStartDate());
		}
		// 小于时间
		if (goodsStore.getEndDate() != null
				&& !goodsStore.getEndDate().equals("")) {
			hql += " and date(goodsStoreDate) <=  '" + goodsStore.getEndDate()
					+ " 23:59:59'";
			// params.put("endDate", goodsStore.getEndDate());
		}

		// 炉位号
		if (goodsStore.getGoodsStoreLuId() != null
				&& !goodsStore.getGoodsStoreLuId().equals("")) {
			hql += " and goodsStoreLuId like :goodsStoreLuId ";
			params.put("goodsStoreLuId", "%%" + goodsStore.getGoodsStoreLuId()
					+ "%%");
		}
		// 申请人部门
		if (goodsStore.getSqUsersdept() != null
				&& goodsStore.getSqUsersdept().length() > 0) {
			hql += " and sqUsersdept like :sqUsersdept ";
			params
					.put("sqUsersdept", "%%" + goodsStore.getSqUsersdept()
							+ "%%");
		}
		// 打印单据
		if (goodsStore.getPrintNumber() != null
				&& goodsStore.getPrintNumber().length() > 0) {
			hql += " and printNumber like :printNumber ";
			params
					.put("printNumber", "%%" + goodsStore.getPrintNumber()
							+ "%%");
		}
		// 送货单号
		if (goodsStore.getGoodsStoreSendId() != null
				&& goodsStore.getGoodsStoreSendId().length() > 0) {
			hql += " and goodsStoreSendId like :goodsStoreSendId ";
			params.put("goodsStoreSendId", "%%"
					+ goodsStore.getGoodsStoreSendId() + "%%");
		}
		// 入库类型
		if (goodsStore.getStyle() != null && goodsStore.getStyle().length() > 0) {
			hql += " and style like :style ";
			params.put("style", "%%" + goodsStore.getStyle() + "%%");
		}
		// 入库类型
		if (goodsStore.getStatus() != null && goodsStore.getStatus().length() > 0) {
			hql += " and status like :style ";
			params.put("style", "%%" + goodsStore.getStyle() + "%%");
		}
		return hql;
	}

	@Override
	public String update(GoodsStore goodsStore) {
		GoodsStore t = (GoodsStore) totalDao.get(GoodsStore.class, goodsStore
				.getGoodsStoreId());
		if (t.getGoodsStoreCount() == null) {
			t.setGoodsStoreCount(0F);
		}
		float i1 = t.getGoodsStoreCount() - goodsStore.getGoodsStoreCount();
		// String hql =
		// "from Goods where goodsMarkId = ? and goodsLotId = ? and goodsFormat = ?";
		// String format = t.getGoodsStoreFormat();
		// Goods s = null;
		// if (null == format || format.length() == 0) {
		// hql = "from Goods where goodsMarkId = ? and goodsLotId = ?";
		// s = (Goods) totalDao.getObjectByCondition(hql, new Object[] {
		// t.getGoodsStoreMarkId(), t.getGoodsStoreLot() });
		// } else {
		// s = (Goods) totalDao.getObjectByCondition(hql, new Object[] {
		// t.getGoodsStoreMarkId(), t.getGoodsStoreLot(),
		// t.getGoodsStoreFormat() });
		// }
		String hql = "from Goods where goodsMarkId = ? ";

		if (t.getGoodsStoreWarehouse() != null
				&& t.getGoodsStoreWarehouse().length() > 0) {
			hql += " and goodsClass='" + t.getGoodsStoreWarehouse() + "'";
		}
		if (t.getGoodHouseName() != null && t.getGoodHouseName().length() > 0) {
			hql += " and goodHouseName='" + t.getGoodHouseName() + "'";
		}
		if (t.getGoodsStorePosition() != null
				&& t.getGoodsStorePosition().length() > 0) {
			hql += " and goodsPosition='" + t.getGoodsStorePosition() + "'";
		}
		if (t.getBanBenNumber() != null && t.getBanBenNumber().length() > 0) {
			hql += " and banBenNumber='" + t.getBanBenNumber() + "'";
		}
		if (t.getKgliao() != null && t.getKgliao().length() > 0) {
			hql += " and kgliao='" + t.getKgliao() + "'";
		}
		if (t.getGoodsStoreLot() != null && t.getGoodsStoreLot().length() > 0) {
			hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
		}
		// if (t.getHsPrice() != null && t.getHsPrice() > 0) {
		// hql += " and goodsPrice='" + t.getHsPrice() + "'";
		// }

		Goods s = (Goods) totalDao.getObjectByCondition(hql, new Object[] { t
				.getGoodsStoreMarkId() });

		if (null != s) {
			if (s.getGoodsCurQuantity() == null) {
				s.setGoodsCurQuantity(0F);
			}
			s.setGoodsCurQuantity(s.getGoodsCurQuantity() - i1);
			s.setGoodHouseName(goodsStore.getGoodHouseName());
			s.setGoodsPosition(goodsStore.getGoodsStorePosition());
			if (goodsStore.getGoodsStoreZhishu() != null
					&& goodsStore.getGoodsStoreZhishu() > 0F) {
				Float zhishu = 0F;
				if (t.getGoodsStoreZhishu() != null) {
					if (!t.getGoodsStoreZhishu().equals(
							goodsStore.getGoodsStoreZhishu())) {
						zhishu = goodsStore.getGoodsStoreZhishu()
								- t.getGoodsStoreZhishu();
					}
				}
				s.setGoodsZhishu((s.getGoodsZhishu() == null ? 0 : s
						.getGoodsZhishu())
						+ zhishu);
			}
			BeanUtils.copyProperties(goodsStore, t, new String[] {
					"goodsStoreId", "goodsStoreTechnologeIf", "printStatus",
					"goodsStoreFapiaoId", "goodsStorePlanner", "status",
					"goodsStoreTime", "wgType", "hsPrice", "kuweiId",
					"printNumber", "wwddId", "neiorderId", "sqUsersName",
					"sqUsrsId", "sqUsersCode", "sqUsersdept",
					"goodsStorePrice", "taxprice", "priceId", "ywmarkId" });
			totalDao.update(t);
			AttendanceTowServerImpl.updateJilu(2, t, t.getGoodsStoreId(), t
					.getGoodsStoreMarkId());
			totalDao.update(s);
			AttendanceTowServerImpl.updateJilu(1, s, s.getGoodsId(), s
					.getGoodsMarkId());
			// 更改送货单明细库位
			if (t != null && t.getWwddId() != null) {
				WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
						.getObjectById(WaigouDeliveryDetail.class, t
								.getWwddId());
				if (wddOld != null) {
					wddOld.setQrWeizhi(t.getGoodsStorePosition());// 入库后位置
					totalDao.update(wddOld);
				}
			}

			// 更改打印单据信息;
			if (t.getPrintNumber() != null && t.getPrintNumber().length() > 0) {
				PrintedOut printedout = (PrintedOut) totalDao
						.getObjectByCondition(
								" from PrintedOut where className = 'GoodsStore' and entiyId =? ",
								t.getGoodsStoreId());
				if (printedout != null) {
					printedout.setCangqu(t.getGoodHouseName());// 仓区
					printedout.setKehuNmae(t.getGoodsStoreCompanyName());// 客户
					totalDao.update(printedout);
				}

			}

			String sql_sum = "SELECT sum(f_num) num ,sum(f_allocationsNum) allocationsNum  FROM TA_Product WHERE fk_orderManager_id = "
					+ goodsStore.getOrder_Id();
			List<Map> list1 = totalDao.findBySql(sql_sum);
			if (list1 != null && list1.size() > 0) {
				Map map = list1.get(0);
				if (map.get("num") != null) {
					int num = (Integer) map.get("num");
					float count = t.getGoodsStoreCount();
					int allocationsNum1 = 0;
					if (map.get("allocationsNum") != null) {
						allocationsNum1 = (Integer) map.get("allocationsNum");
					}
					OrderManager order = (OrderManager) totalDao.get(
							OrderManager.class, t.getOrder_Id());
					allocationsNum1 += count;
					float inputRate = (allocationsNum1 / (float) num) * 100;
					order.setInputRate(inputRate);
					totalDao.update(order);
				}
			}
			return "true";
		}
		return "库存为空，修改失败！";
	}

	@Override
	public String delete(GoodsStore goodsStore) {
		GoodsStore t = (GoodsStore) totalDao.get(GoodsStore.class, goodsStore
				.getGoodsStoreId());
		if (t != null) {
			StringBuffer buffer = new StringBuffer(
					"from Goods where goodsMarkId = ? ");
			if (t.getGoodsStoreWarehouse() != null
					&& t.getGoodsStoreWarehouse().length() > 0) {
				buffer.append(" and goodsClass='" + t.getGoodsStoreWarehouse()
						+ "'");
			}
			if (t.getGoodHouseName() != null
					&& t.getGoodHouseName().length() > 0) {
				buffer.append(" and goodHouseName='" + t.getGoodHouseName()
						+ "'");
			}
			if (t.getGoodsStorePosition() != null
					&& t.getGoodsStorePosition().length() > 0) {
				buffer.append(" and goodsPosition='"
						+ t.getGoodsStorePosition() + "'");
			}
			if (t.getBanBenNumber() != null && t.getBanBenNumber().length() > 0) {
				buffer
						.append(" and banBenNumber='" + t.getBanBenNumber()
								+ "'");
			}
			if (t.getKgliao() != null && t.getKgliao().length() > 0) {
				buffer.append(" and kgliao='" + t.getKgliao() + "'");
			}
			if (t.getGoodsStoreLot() != null
					&& t.getGoodsStoreLot().length() > 0) {
				buffer.append(" and goodsLotId='" + t.getGoodsStoreLot() + "'");
			}
			if (t.getHsPrice() != null && t.getHsPrice() > 0) {
				buffer.append(" and goodsPrice='" + t.getHsPrice() + "'");
			}
			if (t.getSuodingdanhao() != null
					&& t.getSuodingdanhao().length() > 0) {
				buffer.append(" and suodingdanhao = '" + t.getSuodingdanhao()
						+ "'");
			}

			Goods s = (Goods) totalDao.getObjectByCondition(buffer.toString(),
					new Object[] { t.getGoodsStoreMarkId() });
			if (s != null) {
				if (s.getGoodsCurQuantity() < t.getGoodsStoreCount()) {
					return "对不起,该库存已被使用不能删除入库记录!";
				}
				s.setGoodsCurQuantity(s.getGoodsCurQuantity()
						- t.getGoodsStoreCount());
				totalDao.update(s);
			}
			return totalDao.delete(t) + "";
		} else {
			return "删除失败,请刷新重试!";
		}
	}

	private List<String> getAuth(String code, String auth) {
		String hql = "from WareHouseAuth where usercode = ?";
		WareHouseAuth a = (WareHouseAuth) totalDao.getObjectByCondition(hql,
				new Object[] { code });
		if (a == null) {
			return new ArrayList();
		}
		String s = a.getAuth();
		List<String> strList = new ArrayList<String>();
		String[] strArr = s.split(",");
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].endsWith("_" + auth)) {
				strList.add(strArr[i].substring(0, strArr[i].indexOf('_')));
			}
		}
		List<WareHouse> list = totalDao.find("from WareHouse");
		for (WareHouse wareHouse : list) {
			Collections.replaceAll(strList, wareHouse.getCode(), wareHouse
					.getName());
		}
		return strList;
	}

	/***
	 * 更新外购件
	 * 
	 * @param markId
	 *            件号
	 * @param rukuNumber
	 *            入库数量
	 * @return
	 */
	@Override
	public boolean updateWaiProcard(String markId, Float rukuNumber,
			String goodsStoreWarehouse, Integer goodsStoreId) {
		boolean bool = false;
		if (markId != null && markId.length() > 0 && rukuNumber != null
				&& rukuNumber > 0 && goodsStoreId != null && goodsStoreId > 0) {
			GoodsStore gs = (GoodsStore) totalDao.getObjectById(
					GoodsStore.class, goodsStoreId);
			Users user = Util.getLoginUser();
			if (user == null) {
				return false;
			}
			/***
			 * 开始更新数据状态
			 */
			Users loginUser = Util.getLoginUser();
			// 送货单不为空，说明是按照采购流程生成的生产bom
			if (gs != null && gs.getWwddId() != null) {
				WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
						.getObjectById(WaigouDeliveryDetail.class, gs
								.getWwddId());

				wddOld.setRukuTime(Util.getDateTime()); // 入库时间
				String status = "入库";
				wddOld.setChangqu(gs.getGoodHouseName());// 入库仓区
				wddOld.setQrWeizhi(gs.getGoodsStorePosition());// 入库后位置
				wddOld.setStatus(status);
				wddOld.setYrukuNum(rukuNumber);
				totalDao.update(wddOld);

				// 更新送货单状态
				WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
						.getObjectById(WaigouDelivery.class, wddOld
								.getWaigouDelivery().getId());
				// Integer wrkcount0 =
				// totalDao.getCount(" from WaigouDeliveryDetail where waigouDelivery.id =? and status not in ('入库','退回','退货','完成')",waigouDelivery.getId()
				// );
				waigouDelivery.setStatus(status);
				totalDao.update(waigouDelivery);

				// 更新采购订单信息
				WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, wddOld.getWaigouPlanId());
				if (waigouPlan != null) {

					// 存在不合格数量，将采购订单表的未送数量加回去

					if (waigouPlan.getHasruku() != null) {
						waigouPlan.setHasruku(rukuNumber
								+ waigouPlan.getHasruku());// 入库数量
					} else {
						waigouPlan.setHasruku(rukuNumber);// 入库数量
					}
					String wrkstatus = "生产中";
					if (waigouPlan.getHasruku() >= waigouPlan.getNumber()
							&& waigouPlan.getSyNumber() == 0) {
						waigouPlan.setStatus("入库");
					} else {
						WaigouDeliveryDetail wwdd = (WaigouDeliveryDetail) totalDao.getObjectByCondition("from WaigouDeliveryDetail where waigouPlanId=? and status in ('送货中','待检验','待入库')", waigouPlan.getId());
						if(wwdd!=null){
							wrkstatus = wwdd.getStatus();
						}
						waigouPlan.setStatus(wrkstatus);
					}
					waigouPlan.setRukuTime(Util.getDateTime());

					String wlwzdtmes = "仓库入库:<br/>供应商:"
							+ wddOld.getGysName()
							+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
							+ waigouDelivery.getId() + "'>"
							+ waigouDelivery.getPlanNumber() + "</a><br/>入库数量:"
							+ wddOld.getHgNumber() + wddOld.getUnit()
							+ "<br/>入库人:" + loginUser.getName() + ";<br/>入库时间:"
							+ Util.getDateTime() + "<br/>当前位置:"
							+ gs.getGoodsStoreWarehouse() + "-"
							+ gs.getGoodHouseName() + "-"
							+ gs.getGoodsStorePosition() + "<hr/>";
					// waigouPlan.setWlWeizhiDt(waigouPlan.getWlWeizhiDt()
					// + wlwzdtmes);
					// 位置动态
					WlWeizhiDt wlWeizhiDt = new WlWeizhiDt(null, waigouPlan
							.getId(), null, wlwzdtmes);
					totalDao.save(wlWeizhiDt);
					waigouPlan.setWlKw(gs.getGoodsStorePosition());// 位置
					totalDao.update(waigouPlan);
					String wgType = waigouPlan.getType();
					// 查询采购订单，更新状态
					WaigouOrder waigouOrder = (WaigouOrder) totalDao
							.getObjectById(WaigouOrder.class, waigouPlan
									.getWaigouOrder().getId());
					Integer wrkcount1 = totalDao
							.getCount(
									" from WaigouPlan where waigouOrder.id =? and status not in ('入库','退回','删除','取消','关闭','退货','完成')",
									waigouOrder.getId());
					if (wrkcount1 > 0) {
						waigouOrder.setStatus(wrkstatus);
					} else {
						waigouOrder.setStatus(status);
					}
					totalDao.update(waigouOrder);
					if ("外委".equals(wgType)) {
						if (waigouPlan.getWwSource().equals("手动外委")) {
							Float applyCount0 = wddOld.getHgNumber();
							// 获取对应的零件
							List<Procard> procardList = totalDao
									.query(
											"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by selfCard",
											waigouPlan.getId());
							if (procardList != null && procardList.size() > 0) {
								for (Procard procard : procardList) {
									List<ProcardWGCenter> wwcenterList = totalDao
											.query(
													"from ProcardWGCenter where wgOrderId=? and procardId=? order by id",
													waigouPlan.getId(), procard
															.getId());
									if (wwcenterList != null
											&& wwcenterList.size() > 0) {
										for (ProcardWGCenter wwcenter : wwcenterList) {
											if (applyCount0 == 0) {
												break;
											}
											Procard blprocard = null;
											if (procard != null
													&& procard
															.getOldProcardId() != null) {
												blprocard = (Procard) totalDao
														.get(
																Procard.class,
																procard
																		.getOldProcardId());
											}
											if (procard != null) {
												if (wwcenter.getDhCount() == null) {
													wwcenter.setDhCount(0f);
												}
												Float applyCount = wwcenter
														.getProcardCount()
														- wwcenter.getDhCount();
												if (applyCount > applyCount0) {
													applyCount = applyCount0;
													applyCount0 = 0f;
													wwcenter
															.setDhCount(wwcenter
																	.getDhCount()
																	+ applyCount0);
												} else {
													applyCount0 = applyCount0
															- applyCount;
													wwcenter
															.setDhCount(wwcenter
																	.getProcardCount());
												}
												totalDao.update(wwcenter);
												ProcessInforWWApplyDetail pApplyDetail = null;
												if (waigouPlan.getWwDetailId() != null) {
													pApplyDetail = (ProcessInforWWApplyDetail) totalDao
															.getObjectById(
																	ProcessInforWWApplyDetail.class,
																	waigouPlan
																			.getWwDetailId());
												}
												if (pApplyDetail == null) {
													pApplyDetail = (ProcessInforWWApplyDetail) totalDao
															.getObjectById(
																	ProcessInforWWApplyDetail.class,
																	wwcenter
																			.getWwxlId());
												}
												/*****
												 * 位置动态
												 */
												procard.setWlstatus(status);
												if (procard.getWlWeizhiDt() == null) {
													procard.setWlWeizhiDt("");
												}
												// procard.setWlWeizhiDt(procard.getWlWeizhiDt()
												// + wlwzdtmes);
												// 位置动态
												WlWeizhiDt wlWeizhiDt_pro = new WlWeizhiDt(
														procard.getId(), null,
														null, wlwzdtmes);
												totalDao.save(wlWeizhiDt_pro);
												procard
														.setWlKw(gs
																.getGoodsStorePosition());// 位置
												procard.setCangqu(gs
														.getGoodHouseName());// 仓区
												if (procard.getKlNumber() == null) {// 只涨klNumber
													// 不涨hasCount ？
													// 表示对应数量已经被领取?
													procard
															.setKlNumber(applyCount);
												} else {
													procard.setKlNumber(procard
															.getKlNumber()
															+ applyCount);
												}
												if (procard.getKlNumber() > procard
														.getFilnalCount()) {
													// 发送异常消息bgg
													AlertMessagesServerImpl
															.addAlertMessages(
																	"系统维护异常组",
																	"件号:"
																			+ procard
																					.getMarkId()
																			+ "批次:"
																			+ procard
																					.getSelfCard()
																			+ "可领数量("
																			+ procard
																					.getKlNumber()
																			+ ")大于批次数量，系统自动修复可领数量为"
																			+ procard
																					.getFilnalCount()
																			+ "，操作是：外委入库,当前系统时间为"
																			+ Util
																					.getDateTime(),
																	"2");
													// if (procard
													// .getLingliaoType() ==
													// null
													// || !procard
													// .getLingliaoType()
													// .equals(
													// "part")) {
													// throw new
													// RuntimeException(
													// procard.getMarkId()+"对应的接收数量超额,请联系管理员校对数据!");
													// } else {
													procard.setKlNumber(procard
															.getFilnalCount());
													// }
												}
												procard.setJihuoStatua("激活");
												procard.setStatus("领工序");
												totalDao.update(procard);
												// 遍历下层外购
												List<ProcessInforWWProcard> processWWprocardList = totalDao
														.query(
																"from ProcessInforWWProcard where applyDtailId=?",
																pApplyDetail
																		.getId());
												if (processWWprocardList != null
														&& processWWprocardList
																.size() > 0) {
													// 包含外购件
													// 拆分外购件回馈采购
													for (ProcessInforWWProcard processWWProcard : processWWprocardList) {
														if (waigouPlan
																.getWwType()
																.equals("包工包料")) {
															procard
																	.setJihuoStatua("激活");
															Procard wgProcard = (Procard) totalDao
																	.getObjectById(
																			Procard.class,
																			processWWProcard
																					.getProcardId());
															if (wgProcard != null&&"外购".equals(wgProcard.getProcardStyle())) {
																Float outCount = applyCount
																		* wgProcard
																				.getQuanzi2()
																		/ wgProcard
																				.getQuanzi1();
																
																// 修改第一下层外购件的TjNumber
																updateWgTjNumber(
																		wgProcard,
																		outCount);
																if (processWWProcard
																		.getHascount() == null) {
																	processWWProcard
																			.setHascount(processWWProcard
																					.getApplyCount()
																					- outCount);
																} else {
																	processWWProcard
																			.setHascount(processWWProcard
																					.getHascount()
																					- outCount);
																}
																totalDao
																		.update(processWWProcard);
																// 添加外购件在制品在制品
																zaizhiInput(
																		wgProcard,
																		user,
																		applyCount
																				* wgProcard
																						.getQuanzi2()
																				/ wgProcard
																						.getQuanzi1(),
																		"在制品入库(委外接收)");
															}
														}
													}
													if (waigouPlan.getWwType()
															.equals("包工包料")) {// 修改本层的hasCOunt
														Set<Procard> sonProcards = procard
																.getProcardSet();
														Float toxcMaxHasCount = 0f;
														if (sonProcards != null
																&& sonProcards
																		.size() > 0) {
															for (Procard son : sonProcards) {
																if (son
																		.getProcardStyle()
																		.equals(
																				"外购")
																		&& (son
																				.getSbStatus() == null || !son
																				.getSbStatus()
																				.equals(
																						"删除"))) {
																	if (son
																			.getHascount() != null) {
																		Float xcMaxHasCount = son
																				.getHascount()
																				* son
																						.getQuanzi1()
																				/ son
																						.getQuanzi2();
																		if (xcMaxHasCount
																				.equals(son
																						.getFilnalCount())) {
																			toxcMaxHasCount = procard
																					.getKlNumber();
																		} else if (toxcMaxHasCount < xcMaxHasCount) {
																			toxcMaxHasCount = xcMaxHasCount;
																		}
																	}
																}
															}
															procard
																	.setHascount(toxcMaxHasCount);
															if (procard
																	.getHascount() < 0) {
																procard
																		.setHascount(0f);
															}
															if (procard
																	.getHascount() > procard
																	.getKlNumber()) {
																procard
																		.setHascount(procard
																				.getKlNumber());
															}
															totalDao
																	.update(procard);
														}

													}
												}
												if (waigouPlan.getWwType()
														.equals("包工包料")) {
													// 生成自身批次在制品
													zaizhiInput(procard, user,
															applyCount,
															"在制品入库(委外接收)");
													// 修改下层提交数量(第一下层外购件不管之前已修改)
													updateSonTjNumber(procard,
															applyCount, 0, user);

												}
												boolean hasMaxProcess = false;
												if (waigouPlan.getWwType()
														.equals("包工包料")
														|| waigouPlan
																.getWwType()
																.equals("工序外委")) {
													// 填补工序数量
													Integer maxProcessNo = (Integer) totalDao
															.getObjectByCondition(
																	"select processNO from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') order by id desc",
																	procard
																			.getId());
													String processNos = pApplyDetail
															.getProcessNOs();
													String[] processNoStrs = processNos
															.split(";");
													for (String processNoStr : processNoStrs) {
														Integer processNo = 0;
														if (processNoStr != null
																&& !""
																		.equals(processNoStr)) {
															processNo = Integer
																	.parseInt(processNoStr);
														}
														if (processNo
																.equals(maxProcessNo)) {// 最后一道工序提交，提交下层在制品
															List<Procard> allSonlist = totalDao
																	.query(
																			"from Procard where procard.id =?",
																			procard
																					.getId());
															if (allSonlist != null
																	&& allSonlist
																			.size() > 0) {
																for (Procard son : allSonlist) {
																	Float outCount = 0f;
																	if (son
																			.getProcardStyle()
																			.equals(
																					"外购")) {
																		outCount = applyCount
																				* son
																						.getQuanzi2()
																				/ son
																						.getQuanzi1();
																	} else {
																		outCount = applyCount
																				* son
																						.getCorrCount();
																	}
																	zaizhiout(
																			son,
																			user,
																			outCount,
																			"外委工序完成");
																}
															}
														}
														ProcessInfor process = (ProcessInfor) totalDao
																.getObjectByCondition(
																		"from ProcessInfor where processNO=? and (dataStatus is null or dataStatus !='删除')  and procard.id=?",
																		processNo,
																		procard
																				.getId());
														if (process != null) {
															if (process
																	.getWwbackCount() == null) {
																process
																		.setWwbackCount(applyCount);
															} else {
																process
																		.setWwbackCount(process
																				.getWwbackCount()
																				+ applyCount);
															}
															if (process
																	.getTotalCount() == procard
																	.getFilnalCount()
																	.floatValue()) {// 最初情况
																process
																		.setTotalCount(applyCount);
															} else {
																process
																		.setTotalCount(process
																				.getTotalCount()
																				+ applyCount);
															}
															if (process
																	.getTotalCount() > procard
																	.getFilnalCount()
																	.floatValue()) {
																process
																		.setTotalCount(procard
																				.getFilnalCount());
															}
															process
																	.setApplyCount(process
																			.getApplyCount()
																			+ applyCount);
															process
																	.setSubmmitCount(process
																			.getSubmmitCount()
																			+ applyCount);
															if (procard
																	.getFilnalCount()
																	.equals(
																			process
																					.getSubmmitCount())) {
																process
																		.setStatus("完成");
															} else if (process
																	.getStatus() == null
																	|| process
																			.getStatus()
																			.equals(
																					"初始")) {
																process
																		.setStatus("自检");
															}
															totalDao
																	.update(process);
															if (blprocard != null) {// 补料提交的更新之前流水卡片
																ProcessInfor blprocess = (ProcessInfor) totalDao
																		.getObjectByCondition(
																				" from ProcessInfor where procard.id = ? and processNO = ? and (dataStatus is null or dataStatus !='删除') ",
																				blprocard
																						.getId(),
																				process
																						.getProcessNO());
																if (blprocess != null) {
																	blprocess
																			.setSubmmitCount(blprocess
																					.getSubmmitCount()
																					+ process
																							.getSubmmitCount());
																	if (blprocess
																			.getBreakCount() > 0) {
																		blprocess
																				.setBreakCount(blprocess
																						.getBreakCount()
																						- process
																								.getSubmmitCount());
																	}
																	if (blprocess
																			.getTotalCount() == blprocard
																			.getFilnalCount()
																			.floatValue()
																			&& blprocess
																					.getSubmmitCount() == blprocess
																					.getTotalCount()) {
																		blprocess
																				.setStatus("完成");
																	}
																	totalDao
																			.update(blprocess);
																	ProcessInfor finalprocess = (ProcessInfor) totalDao
																			.getObjectByCondition(
																					" from ProcessInfor where procard.id = ? and processNO > ? and (dataStatus is null or dataStatus !='删除') ",
																					blprocard
																							.getId(),
																					process
																							.getProcessNO());
																	if (finalprocess == null
																			&& "完成"
																					.equals(blprocess
																							.getStatus())) {
																		blprocard
																				.setStatus("完成");
																		totalDao
																				.update(blprocard);
																		LogoStickers ls = (LogoStickers) totalDao
																				.getObjectByCondition(
																						" from LogoStickers where procardId =? and status = '已发卡'",
																						blprocard
																								.getId());
																		if (ls != null) {
																			ls
																					.setStatus("完成");
																			totalDao
																					.update(ls);
																		}

																	}
																}
															}

														}
													}

													// 获取工序最小提交量 屏蔽填充procardTjnumber并向上激活代码转移到领料出库时生效
//													Float submitCount = (Float) totalDao
//															.getObjectByCondition(
//																	"select min(submmitCount) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') ",
//																	procard
//																			.getId());
//													if (submitCount != null
//															&& procard
//																	.getFilnalCount()
//																	.equals(
//																			submitCount)) {// 工序全部提交完成
//														procard.setStatus("完成");
//														totalDao
//																.update(procard);
//													}

//													if (submitCount != null
//															&& submitCount > 0
//															&& !submitCount
//																	.equals(procard
//																			.getTjNumber())) {
//														procard
//																.setTjNumber(submitCount);
//														Float minNumber = null;
//														if (procard
//																.getProcardStyle()
//																.equals("外购件")) {
//															minNumber = procard
//																	.getTjNumber()
//																	* procard
//																			.getQuanzi1()
//																	/ procard
//																			.getQuanzi2();
//															procard
//																	.setMinNumber(minNumber);
//														} else if (procard
//																.getProcardStyle()
//																.equals("总成")) {
//														} else {
//															minNumber = procard
//																	.getTjNumber()
//																	/ procard
//																			.getCorrCount();
//															procard
//																	.setMinNumber(minNumber);
//														}
//														// 修改上层激活数量
//														if (procard
//																.getProcard() != null) {
//															Float sonMinNumber = (Float) totalDao
//																	.getObjectByCondition(
//																			"select min(minNumber) from Procard where procard.id=?",
//																			procard
//																					.getProcard()
//																					.getId());
//															if (sonMinNumber != null
//																	&& sonMinNumber > 0) {
//																Float sonMinNumber2 = (float) Math
//																		.floor(sonMinNumber);
//																Procard father = procard
//																		.getProcard();
//																if (father
//																		.getJihuoStatua() == null) {
//																	father
//																			.setJihuoStatua("激活");
//																}
//																if (father
//																		.getKlNumber() == null) {
//																	father
//																			.setHascount(sonMinNumber2);
//																	father
//																			.setKlNumber(sonMinNumber2);
//																} else if (sonMinNumber2 > father
//																		.getKlNumber()) {
//																	if (father
//																			.getHascount() == null) {
//																		father
//																				.setHascount(0f);
//																	}
//																	father
//																			.setHascount(father
//																					.getHascount()
//																					+ (sonMinNumber2 - father
//																							.getKlNumber()));
//																	father
//																			.setKlNumber(sonMinNumber2);
//																}
//																totalDao
//																		.update(father);
//															}
//														}
//													}
													// 工序委外级联下层
													if (pApplyDetail
															.getWwType()
															.equals("工序外委")
															&& pApplyDetail
																	.getRelatDown() != null
															&& pApplyDetail
																	.getRelatDown()
																	.equals("是")) {
														updateDwonProcessNo(
																procard,
																applyCount);
														if (!hasMaxProcess) {// 不含最后一道工序生成第一下层
															List<Procard> sonhasProcessProcardList = totalDao
																	.query(
																			"from Procard where procard.id=? "
																					+ "and (procardStyle='自制' or (needProcess is not null and needProcess='yes'))",
																			procard
																					.getId());
															if (sonhasProcessProcardList != null
																	&& sonhasProcessProcardList
																			.size() > 0) {
																for (Procard son : sonhasProcessProcardList) {
																	Float count = 0f;
																	if (son
																			.getProcardStyle()
																			.equals(
																					"自制")) {
																		count = applyCount
																				* son
																						.getCorrCount();
																	} else {
																		count = applyCount
																				* son
																						.getQuanzi2()
																				/ son
																						.getQuanzi1();
																	}
																	zaizhiInput(
																			son,
																			loginUser,
																			count,
																			"工序委外接收");
																}
															}

														}
													}
												}
											}

										}
									}
								}
							}

						} else if ("BOM外委".equals(waigouPlan.getWwSource())) {
							Float dhCount = wddOld.getHgNumber();
							// 获取工序
							String processNos = waigouPlan.getProcessNOs();
							// 工序分开
							String[] processNoStrs = processNos.split(";");
							// 获取对应的零件
							List<Procard> procardList = totalDao
									.query(
											"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by selfCard",
											waigouPlan.getId());
							for (Procard procard : procardList) {
								/*****
								 * 位置动态
								 */
								procard.setWlstatus(status);
								if (procard.getWlWeizhiDt() == null) {
									procard.setWlWeizhiDt("");
								}
								// procard.setWlWeizhiDt(procard.getWlWeizhiDt()
								// + wlwzdtmes);
								procard.setWlKw(gs.getGoodsStorePosition());// 位置
								procard.setCangqu(gs.getGoodHouseName());// 仓区
								procard.setJihuoStatua("激活");
								procard.setStatus("领工序");
								// 获取对应的中间表
								List<ProcardWGCenter> zjbList = totalDao
										.query(
												"from ProcardWGCenter where wgOrderId=? and procardId=? ",
												waigouPlan.getId(), procard
														.getId());
								for (ProcardWGCenter zjb : zjbList) {
									Float applyCount = 0f;
									if (zjb.getDhCount() == null) {
										zjb.setDhCount(0f);
									}
									Float candelete = zjb.getProcardCount()
											- zjb.getDhCount();
									if (candelete > dhCount) {
										applyCount = dhCount;
										zjb.setDhCount(zjb.getDhCount()
												+ dhCount);
										dhCount = 0f;
									} else {
										applyCount = candelete;
										dhCount -= candelete;
										zjb.setDhCount(zjb.getProcardCount());
									}
									totalDao.update(zjb);
									Integer maxProcessNo = (Integer) totalDao
											.getObjectByCondition(
													"select processNO from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除')  order by id desc",
													procard.getId());
									for (String processNoStr : processNoStrs) {
										Integer processNo = Integer
												.parseInt(processNoStr);
										if (processNo.equals(maxProcessNo)) {// 最后一道工序提交，提交下层在制品
											List<Procard> allSonlist = totalDao
													.query(
															"from Procard where procard.id =?",
															procard.getId());
											if (allSonlist != null
													&& allSonlist.size() > 0) {
												for (Procard son : allSonlist) {
													Float outCount = 0f;
													if (son.getProcardStyle()
															.equals("外购")) {
														outCount = applyCount
																* son
																		.getQuanzi2()
																/ son
																		.getQuanzi1();
													} else {
														outCount = applyCount
																* son
																		.getCorrCount();
													}
													zaizhiout(son, user,
															outCount, "外委工序完成");
												}
											}
										}
										ProcessInfor process = (ProcessInfor) totalDao
												.getObjectByCondition(
														"from ProcessInfor where processNO=? and procard.id=? and (dataStatus is null or dataStatus !='删除') ",
														processNo, procard
																.getId());
										if (process != null) {
											process.setApplyCount(process
													.getApplyCount()
													+ applyCount);
											process.setSubmmitCount(process
													.getSubmmitCount()
													+ applyCount);
											if (procard
													.getFilnalCount()
													.equals(
															process
																	.getSubmmitCount())) {
												process.setStatus("完成");
											} else if (process.getStatus() == null
													|| process.getStatus()
															.equals("初始")) {
												process.setStatus("自检");
											}
											totalDao.update(process);
										}
									}
									// 获取工序最小提交量
//									Float submitCount = (Float) totalDao
//											.getObjectByCondition(
//													"select min(submmitCount) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') ",
//													procard.getId());
//									if (submitCount != null
//											&& procard.getFilnalCount().equals(
//													submitCount)) {// 工序全部提交完成
//										procard.setStatus("完成");
//										totalDao.update(procard);
//									}
//									if (submitCount != null
//											&& submitCount > 0
//											&& !submitCount.equals(procard
//													.getTjNumber())) {
//										procard.setTjNumber(submitCount);
//										Float minNumber = null;
//										if (procard.getProcardStyle().equals(
//												"外购")) {
//											if (procard.getQuanzi2() == null
//													|| procard.getCorrCount() == 0) {
//												throw new RuntimeException(
//														"自制件:"
//																+ procard
//																		.getMarkId()
//																+ " 数据消耗量异常，请及时联系管理员。");
//											}
//											minNumber = procard.getTjNumber()
//													* procard.getQuanzi1()
//													/ procard.getQuanzi2();
//											procard.setMinNumber(minNumber);
//										} else if (procard.getProcardStyle()
//												.equals("总成")) {
//										} else {
//											if (procard.getCorrCount() == null
//													|| procard.getCorrCount() == 0) {
//												throw new RuntimeException(
//														"自制件:"
//																+ procard
//																		.getMarkId()
//																+ " 数据消耗量异常，请及时联系管理员。");
//											}
//											minNumber = procard.getTjNumber()
//													/ procard.getCorrCount();
//											procard.setMinNumber(minNumber);
//
//										}
//										// 修改上层激活数量
//										if (procard.getProcard() != null) {
//											Float sonMinNumber = (Float) totalDao
//													.getObjectByCondition(
//															"select min(minNumber) from Procard where procard.id=?",
//															procard
//																	.getProcard()
//																	.getId());
//											if (sonMinNumber != null
//													&& sonMinNumber > 0) {
//												Float sonMinNumber2 = (float) Math
//														.floor(sonMinNumber);
//												Procard father = procard
//														.getProcard();
//												if (father.getJihuoStatua() == null) {
//													father.setJihuoStatua("激活");
//												}
//												if (father.getKlNumber() == null) {
//													father
//															.setHascount(sonMinNumber2);
//													father
//															.setKlNumber(sonMinNumber2);
//												} else if (sonMinNumber2 > father
//														.getKlNumber()) {
//													if (father.getHascount() == null) {
//														father.setHascount(0f);
//													}
//													father
//															.setHascount(father
//																	.getHascount()
//																	+ (sonMinNumber2 - father
//																			.getKlNumber()));
//													father
//															.setKlNumber(sonMinNumber2);
//												}
//												totalDao.update(father);
//											}
//										}
//									}
									if (dhCount == 0)
										break;
								}
								if (dhCount == 0)
									break;
							}

						}
					} else if (("外购").equals(wgType)) {
						// 回传物料需求汇总入库数量
						if (waigouPlan.getMopId() != null
								&& waigouPlan.getMopId() > 0) {
							ManualOrderPlan mop = (ManualOrderPlan) totalDao
									.get(ManualOrderPlan.class, waigouPlan
											.getMopId());
							if (mop != null) {
								if (mop.getRukuNum() != null) {
									mop.setRukuNum(mop.getRukuNum()
											+ rukuNumber);
								} else {
									mop.setRukuNum(rukuNumber);
								}
								if (mop.getRukuNum() >= mop.getNumber()) {
									mop.setStatus("已入库");
								}
								totalDao.update(mop);
							// 回传物料需求明细入库数量
							List<ManualOrderPlanDetail> modList = totalDao
									.query(
											" from ManualOrderPlanDetail where manualPlan.id =? and  cgnumber>0",
											mop.getId());
							Float num = 0f;
							for (ManualOrderPlanDetail mod : modList) {
								if (mod.getRukuNum() == null
										|| mod.getRukuNum() == 0) {
									if ((wddOld.getHgNumber() - num) >= mod
											.getCgnumber()) {
										mod.setRukuNum(mod.getCgnumber());
										num += mod.getCgnumber();
									} else if ((wddOld.getHgNumber() - num) > 0) {
										mod.setRukuNum(wddOld.getHgNumber()
												- num);
										num += (wddOld.getHgNumber() - num);
									} else {
										mod.setRukuNum(0f);
									}
								} else {
									Float dhNumber = mod.getRukuNum();
									if ((dhNumber + (wddOld.getHgNumber() - num)) <= mod
											.getCgnumber()) {
										mod.setRukuNum(dhNumber
												+ (wddOld.getHgNumber() - num));
										num += wddOld.getHgNumber() - num;
									} else {
										num += (mod.getCgnumber() - mod
												.getRukuNum());
										mod.setRukuNum(mod.getCgnumber());
									}
								}
								totalDao.update(mod);
							}
							}

							// ManualOrderPlan manualPlan = mod
							// .getManualPlan();
							// if (mod.getRukuNum() != null) {
							// if (mod.getRukuNum() >= mod
							// .getCgnumber()) {
							// mod.setStatus("完成");
							// }
							// if (manualPlan.getRukuNum() != null) {
							// manualPlan
							// .setRukuNum(manualPlan
							// .getRukuNum()
							// + mod
							// .getRukuNum());
							// } else {
							// manualPlan.setRukuNum(mod
							// .getRukuNum());
							// }
							// }
							// totalDao.update(manualPlan);
						}

						Float num = 0f;
						// 查询采购明细对应生产批次
						// String hql_procard =
						// "from Procard where id in (select procardId from ProcardWGCenter where wgOrderId=?) "
						// +
						// "and (((tjNumber is null or tjNumber<filnalCount) and (needProcess <> 'yes' or needProcess is null))"
						// +
						// " or (needProcess = 'yes' and klNumber<filnalCount))";
						String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id =? and cgnumber>0)  "
								+ "and (((tjNumber is null or tjNumber<filnalCount) and (needProcess <> 'yes' or needProcess is null)) "
								+ "or (needProcess = 'yes' and klNumber<filnalCount)) order by orderNumber,productStyle";
						List list_procrd = totalDao.query(hql_procard,
								waigouPlan.getMopId());

						// List<ManualOrderPlanDetail> jjmodList = totalDao
						// .query(
						// " from ManualOrderPlanDetail where manualPlan.id =? and isurgent ='YES' and cgnumber>0 ",
						// waigouPlan.getMopId());
						// 优先级 -- （手动紧急）>正式订单>试制订单>预测订单>安全库存>手动添加（正常）
						// 优先级由左到右依次递减;
						// if (jjmodList != null && jjmodList.size() > 0) {
						// for (ManualOrderPlanDetail mod : jjmodList) {
						// if (mod.getRukuNum() == null
						// || mod.getRukuNum() == 0) {
						// if ((wddOld.getHgNumber() - num) >= mod
						// .getCgnumber()) {
						// mod.setRukuNum(mod.getCgnumber());
						// num += mod.getCgnumber();
						// } else if ((wddOld.getHgNumber() - num) > 0) {
						// mod.setRukuNum(wddOld.getHgNumber()
						// - num);
						// num += (wddOld.getHgNumber() - num);
						// } else {
						// mod.setRukuNum(0f);
						// }
						// } else {
						// Float dhNumber = mod.getRukuNum();
						// if ((dhNumber + (wddOld.getHgNumber() - num)) <= mod
						// .getCgnumber()) {
						// mod.setRukuNum(dhNumber
						// + (wddOld.getHgNumber() - num));
						// num += wddOld.getHgNumber() - num;
						// } else {
						// num += (mod.getCgnumber() - mod
						// .getRukuNum());
						// mod.setRukuNum(mod.getCgnumber());
						// }
						// }
						// // ManualOrderPlan manualPlan = mod
						// // .getManualPlan();
						// if (mod.getRukuNum() != null) {
						// if (mod.getRukuNum() >= mod.getCgnumber()) {
						// mod.setStatus("完成");
						// }
						// // if (manualPlan.getRukuNum() != null) {
						// // manualPlan.setRukuNum(manualPlan
						// // .getRukuNum()
						// // + mod.getRukuNum());
						// // } else {
						// // manualPlan.setRukuNum(mod.getRukuNum());
						// // }
						// }
						// // totalDao.update(manualPlan);
						// totalDao.update(mod);
						// }
						// }

						if (wddOld != null && num < wddOld.getHgNumber()) {
							String productStyle = null;
							if (goodsStoreWarehouse.equals("外购件库")) {
								productStyle = "批产";
							} else {
								productStyle = "试制";
							}
							Integer size = list_procrd.size();
							if (size != null && size < 300) {
								for (int j = 0; j < size; j++) {
									if(j==47){
										System.out.println(123);
									}
									System.out.println(j);
									Procard waiProcard = (Procard) list_procrd
											.get(j);
									ManualOrderPlanDetail mod = (ManualOrderPlanDetail) totalDao
											.getObjectByCondition(
													" from ManualOrderPlanDetail where procardId =?",
													waiProcard.getId());
									/*****
									 * 位置动态
									 */
									waiProcard.setWlstatus(status);
									if (waiProcard.getWlWeizhiDt() == null) {
										waiProcard.setWlWeizhiDt("");
									}
									// waiProcard.setWlWeizhiDt(waiProcard
									// .getWlWeizhiDt()
									// + wlwzdtmes);
									WlWeizhiDt pro_wlWeizhiDt = new WlWeizhiDt(
											waiProcard.getId(), null, null,
											wlwzdtmes);
									totalDao.save(pro_wlWeizhiDt);
									waiProcard.setWlKw(gs
											.getGoodsStorePosition());// 位置
									waiProcard.setCangqu(gs.getGoodHouseName());// 仓区
									// 激活生产批次

									// 包工包料的数量
									Float waiweirukuNum = waiProcard
											.getWwblCount();
									if (waiweirukuNum == null) {
										waiweirukuNum = 0F;
									}
									Float procardNumber = 0F;
									Float tjOrKlNumber = waiProcard
											.getTjNumber();
									if ("yes".equals(waiProcard
											.getNeedProcess())) {
										tjOrKlNumber = waiProcard.getKlNumber();// 半成品取可领数量对比
									}
									if (tjOrKlNumber == null) {
										tjOrKlNumber = 0F;
									}
									// 激活数量
									if (waiProcard.getTjNumber() == null) {
										waiProcard.setTjNumber(0f);
									}
									if (waiweirukuNum > 0) {
										procardNumber = waiProcard
												.getFilnalCount()
												- waiweirukuNum - tjOrKlNumber;
									} else {
										procardNumber = waiProcard
												.getFilnalCount()
												- tjOrKlNumber;
									}
									if(procardNumber<0){
										procardNumber=0F;
									}
									if (rukuNumber > procardNumber) {
										rukuNumber -= procardNumber;
									} else {
										procardNumber = rukuNumber;
										rukuNumber = 0F;
									}
									// 外购件到货数量
									Float cgNumber = waiProcard.getCgNumber();
									if (cgNumber != null) {
										// if (waiProcard.getDhNumber() == null)
										// {
										// if ((wddOld.getHgNumber() - num) >=
										// cgNumber) {
										// mod.setRukuNum(cgNumber);
										// num += cgNumber;
										// } else if ((wddOld.getHgNumber() -
										// num) >
										// 0) {
										// mod.setRukuNum(wddOld.getHgNumber()
										// - num);
										// num += (wddOld.getHgNumber() - num);
										// } else {
										// mod.setRukuNum(0f);
										// }
										// } else {
										// Float dhNumber = waiProcard
										// .getDhNumber();
										// if ((dhNumber + (wddOld.getHgNumber()
										// -
										// num)) <= waiProcard
										// .getOutcgNumber()) {
										// waiProcard
										// .setDhNumber(dhNumber
										// + (wddOld
										// .getHgNumber() - num));
										// num += wddOld.getHgNumber() - num;
										// } else {
										// num += (cgNumber - waiProcard
										// .getDhNumber());
										// mod.setRukuNum(cgNumber);
										// }
										// }

										waiProcard
												.setDhNumber(mod.getRukuNum());
										// ManualOrderPlan manualPlan =
										// (ManualOrderPlan) totalDao
										// .getObjectById(
										// ManualOrderPlan.class, mod
										// .getManualPlan()
										// .getId());
										// if (mod.getRukuNum() != null) {
										// if (mod.getRukuNum() >= mod
										// .getCgnumber()) {
										// mod.setStatus("完成");
										// }
										// if (manualPlan.getRukuNum() != null)
										// {
										// manualPlan.setRukuNum(manualPlan
										// .getRukuNum()
										// + num);
										// } else {
										// manualPlan.setRukuNum(mod
										// .getRukuNum());
										// }
										// }
										// totalDao.update(manualPlan);
										totalDao.update(mod);

										// 外购件被包料数量
										Float wwblCount = waiProcard
												.getWwblCount();
										// 外购件被包料接收的数量
										Float wwblreceiveCount = waiProcard
												.getWwblreceiveCount();
										if (wwblCount == null) {
											wwblCount = 0f;
										}
										if (wwblreceiveCount == null) {
											wwblreceiveCount = 0f;
										}
										Float maxTjNumber = waiProcard
												.getFilnalCount()
												- wwblCount + wwblreceiveCount;
										if (tjOrKlNumber > maxTjNumber) {
											tjOrKlNumber = maxTjNumber;
										}
										DecimalFormat df = new DecimalFormat(
												"#.###");
										tjOrKlNumber = Float.valueOf(df
												.format(tjOrKlNumber));
										if ("yes".equals(waiProcard
												.getNeedProcess())) {
											waiProcard
													.setHascount(procardNumber
															+ waiProcard
																	.getHascount());
											waiProcard.setKlNumber(tjOrKlNumber
													+ procardNumber);// 半成品可领数量更新
										} else {
											waiProcard.setTjNumber(tjOrKlNumber
													+ procardNumber);// 外购件提交数量更新
											if (waiProcard.getTjNumber() < 0
													&& waiProcard.getTjNumber() > (-0.05
															* waiProcard
																	.getQuanzi2() / waiProcard
															.getQuanzi1())) {//屏蔽计算小数偏差
												waiProcard.setTjNumber(0f);
											}
										}
										bool = totalDao.update(waiProcard);
										if (bool) {
											/** 开始激活上层数据 */
											// bool =
											// procardServer.jihuoProcard(
											// waiProcard.getRootId(),
											// waiProcard
											// .getBelongLayer() - 1,
											// "外购件入库，件号为:" + markId);
											// bool = procardServer
											// .jihuoSingleProcard(waiProcard
											// .getProcard());

											// String hql =
											// "select belongLayer from Procard where rootId=? and (procardstyle in ('总成','自制') or (needProcess ='yes' and procardstyle='外购')) order by belonglayer desc";
											// Integer maxbelongLayer =
											// (Integer)
											// totalDao
											// .getObjectByCondition(hql,
											// waiProcard.getRootId());
											// // 说明是最后一层的外购件
											// if (maxbelongLayer < waiProcard
											// .getBelongLayer()) {
											// // 查询本层次同一父类下的零件是否存在未提交数据
											// String thisHql =
											// "from Procard where fatherId=? and belongLayer=? and productStyle=?  and (minNumber is null or minNumber=0)";
											// int count =
											// totalDao.getCount(thisHql,
											// waiProcard.getFatherId(),
											// waiProcard.getBelongLayer(),
											// productStyle);
											// if (count == 0) {// 本层都已经提交
											// /** 开始激活上层数据 */
											// bool = procardServer
											// .jihuoProcard(
											// waiProcard
											// .getRootId(),
											// waiProcard
											// .getBelongLayer() - 1,
											// "外购件入库，件号为:"
											// + markId);
											// }
											// } else {
											// // 查询本层是否存在未提交数据（先按照层次激活一遍）
											// String thisHql =
											// "from Procard where rootId=? and belongLayer=? and productStyle=?  and (minNumber is null or minNumber<1)";
											// int count =
											// totalDao.getCount(thisHql,
											// waiProcard.getRootId(),
											// waiProcard.getBelongLayer(),
											// productStyle);
											// /****** 按照层次激活模式 ******/
											// if (count == 0) {// 本层都已经提交
											// /** 开始激活上层数据 */
											// bool = procardServer
											// .jihuoProcard(
											// waiProcard
											// .getRootId(),
											// waiProcard
											// .getBelongLayer() - 1,
											// "外购件入库，件号为:"
											// + markId);
											// } else {
											// /***** 按照自制件激活模式 *********/
											// // 整个层次未全部完成。
											// // 则判断如果当前外购件的上层已经激活，如果激活则单独处理上层
											// String thisFatherHql =
											// "from Procard where id=?  and (id not in (select fatherId from Procard where procardstyle  ='自制' and fatherId=? )  or (procardStyle='外购' and needProcess ='yes' and status='初始'))  ";
											// Procard fatherProcard = (Procard)
											// totalDao
											// .getObjectByCondition(
											// thisFatherHql,
											// waiProcard
											// .getFatherId(),
											// waiProcard
											// .getFatherId());
											// bool = procardServer
											// .jihuoSingleProcard(fatherProcard);
											// }
											// }
										}
									}
									// 如果送货数量还是大于0
									if (rukuNumber > 0) {
										// 更新有在途占用量的外购件 激活
										String hql = "from Procard where markId=? and productStyle=? " +
												"and procardStyle='外购'  and status='已发卡' and tjNumber<filnalCount and ztzyNumber>0 order by selfcard ";
										List<Procard> ztzylist = totalDao
												.query(hql, markId,
														productStyle);
										Float procardNumber1 = 0F;
										if (ztzylist != null) {
											for (Procard waiProcard1 : ztzylist) {
												if (waiProcard1.getTjNumber() == null) {
													waiProcard1.setTjNumber(0f);
												}
												procardNumber1 = waiProcard1
														.getFilnalCount()
														- waiProcard1
																.getTjNumber();
												if (rukuNumber > procardNumber1) {
													rukuNumber -= procardNumber1;
												} else {
													procardNumber1 = rukuNumber;
													rukuNumber = 0F;
												}
												if (waiProcard1
														.getNeedProcess() == null
														|| !waiProcard1
																.getNeedProcess()
																.equals("yes")) {
													waiProcard1
															.setTjNumber(waiProcard1
																	.getTjNumber()
																	+ procardNumber1);// 外购件数量更新
//													waiProcard1
//															.setMinNumber(waiProcard1
//																	.getTjNumber()
//																	/ waiProcard1
//																			.getQuanzi2()
//																	* waiProcard1
//																			.getQuanzi1());// 该件号对应上层最小数量
													waiProcard1
															.setZtzyNumber(waiProcard1
																	.getZtzyNumber()
																	- procardNumber1);
													bool = totalDao
															.update(waiProcard1);
													if (bool) {
														/** 开始激活上层数据 */
														// bool = procardServer
														// .jihuoSingleProcard(waiProcard1
														// .getProcard());
														/*
														 * bool = procardServer
														 * .jihuoProcard(
														 * waiProcard
														 * .getRootId(),
														 * waiProcard
														 * .getBelongLayer() -
														 * 1, "外购件入库，件号为:" +
														 * markId);
														 */
													}
												}
												if (rukuNumber <= 0) {
													break;
												}
											}
										}
									}

								}
							}
							// 预测订单
							// if (wddOld != null && num < wddOld.getHgNumber())
							// {
							// List<ManualOrderPlanDetail> ycmodList = totalDao
							// .query(
							// " from ManualOrderPlanDetail where manualPlan.id =? and  type = '预测订单' and cgnumber>0",
							// waigouPlan.getMopId());
							// if (ycmodList != null && ycmodList.size() > 0) {
							// Float beforesumrkNum = (Float) totalDao
							// .getObjectByCondition(
							// "select sum(rukuNum) from ManualOrderPlanDetail where manualPlan.id =? and  type = '预测订单'",
							// waigouPlan.getMopId());
							// for (ManualOrderPlanDetail mod : ycmodList) {
							// if (mod.getRukuNum() == null
							// || mod.getRukuNum() == 0) {
							// if ((wddOld.getHgNumber() - num) >= mod
							// .getCgnumber()) {
							// mod.setRukuNum(mod
							// .getCgnumber());
							// num += mod.getCgnumber();
							// } else if ((wddOld.getHgNumber() - num) > 0) {
							// mod.setRukuNum(wddOld
							// .getHgNumber()
							// - num);
							// num += (wddOld.getHgNumber() - num);
							// } else {
							// mod.setRukuNum(0f);
							// }
							// } else {
							// Float dhNumber = mod.getRukuNum();
							// if ((dhNumber + (wddOld
							// .getHgNumber() - num)) <= mod
							// .getCgnumber()) {
							// mod
							// .setRukuNum(dhNumber
							// + (wddOld
							// .getHgNumber() - num));
							// num += wddOld.getHgNumber()
							// - num;
							// } else {
							// num += (mod.getCgnumber() - mod
							// .getRukuNum());
							// mod.setRukuNum(mod
							// .getCgnumber());
							// }
							// }
							// // ManualOrderPlan manualPlan = mod
							// // .getManualPlan();
							// // if (mod.getRukuNum() != null) {
							// // if (mod.getRukuNum() >= mod
							// // .getCgnumber()) {
							// // mod.setStatus("完成");
							// // }
							// // if (manualPlan.getRukuNum() != null) {
							// // manualPlan
							// // .setRukuNum(manualPlan
							// // .getRukuNum()
							// // + mod
							// // .getRukuNum());
							// // } else {
							// // manualPlan.setRukuNum(mod
							// // .getRukuNum());
							// // }
							// // }
							// // totalDao.update(manualPlan);
							// // totalDao.update(mod);
							// }
							// // Float sumrkNum = (Float)
							// //
							// totalDao.getObjectByCondition("select sum(rukuNum) from ManualOrderPlanDetail where manualPlan.id =? and  type = '预测订单'",
							// // waigouPlan.getMopId());
							// //
							// if((sumrkNum-beforesumrkNum)>0){//预测订单入库数大于零则将预测订单入库数量封存起来
							// // GoodsStore goodsStore = (GoodsStore)
							// //
							// totalDao.getObjectByCondition(" from GoodsStore where goodsStoreWarehouse = '外购件库' and wwddId = ? and goodsStoreMarkId = ? ",
							// // waigouPlan.getId(),waigouPlan.getMarkId());
							// // String kgSql = "";
							// // // 供料属性
							// // if
							// //
							// (goodsStore.getGoodsStoreWarehouse().equals("外购件库")
							// // ||
							// //
							// goodsStore.getGoodsStoreWarehouse().equals("占用库")
							// // ||
							// //
							// goodsStore.getGoodsStoreWarehouse().equals("在途库"))
							// // {
							// // kgSql += " and kgliao='" +
							// // goodsStore.getKgliao() + "'";
							// // }
							// // // 版本
							// // String banben_hql = "";
							// // if (goodsStore.getBanBenNumber() != null
							// // && goodsStore.getBanBenNumber().length()
							// // > 0)
							// // {
							// // banben_hql = " and banBenNumber='" +
							// // goodsStore.getBanBenNumber()
							// // + "'";
							// // }
							// // // 仓区
							// // String houseName_hql = "";
							// // if (goodsStore.getGoodHouseName() != null
							// // && goodsStore.getGoodHouseName().length()
							// // >
							// // 0) {
							// // houseName_hql = " and goodHouseName='"
							// // + goodsStore.getGoodHouseName() + "'";
							// // }
							// // // 库位
							// // String position_hql = "";
							// // if (goodsStore.getGoodsStorePosition() !=
							// // null
							// // &&
							// // goodsStore.getGoodsStorePosition().length()
							// // >
							// // 0) {
							// // position_hql = " and goodsPosition='"
							// // + goodsStore.getGoodsStorePosition() +
							// // "'";
							// // }
							// // // 批次
							// // String goodsLotId_hql = "";
							// // if (goodsStore.getGoodsStoreLot() != null
							// // && goodsStore.getGoodsStoreLot().length()
							// // >
							// // 0) {
							// // goodsLotId_hql = " and goodsLotId='"
							// // + goodsStore.getGoodsStoreLot() + "'";
							// // }
							// //
							// // String hql =
							// // "from Goods where goodsMarkId = ? " +
							// // goodsLotId_hql
							// // + banben_hql + kgSql +
							// // " and goodsClass=? " +
							// // houseName_hql
							// // + position_hql +
							// //
							// " and (fcStatus is null or fcStatus='可用') and goodsCurQuantity >='"+(sumrkNum-beforesumrkNum)+"' ";
							// // Goods g = (Goods)
							// // totalDao.getObjectByCondition(hql, new
							// // Object[] {
							// // goodsStore.getGoodsStoreMarkId(),
							// // goodsStore.getGoodsStoreWarehouse() });
							// // if(g!=null){
							// // Goods fcgoods = new Goods();
							// // BeanUtils.copyProperties(g, fcgoods, new
							// //
							// String[]{"goodsId,goodsCurQuantity,fcStatus"});
							// // fcgoods.setFcStatus("封存");
							// //
							// fcgoods.setGoodsCurQuantity(sumrkNum-beforesumrkNum);
							// //
							// g.setGoodsCurQuantity(g.getGoodsCurQuantity()-(sumrkNum-beforesumrkNum));
							// // totalDao.update(g);
							// // totalDao.save(fcgoods);
							// // }
							// // }
							// }
							//
							// }
							// 安全库存
							// if (wddOld != null && num < wddOld.getHgNumber())
							// {
							// List<ManualOrderPlanDetail> aqmodList = totalDao
							// .query(
							// " from ManualOrderPlanDetail where manualPlan.id =? and  type = '安全库存' and cgnumber>0 ",
							// waigouPlan.getMopId());
							// if (aqmodList != null && aqmodList.size() > 0) {
							// for (ManualOrderPlanDetail mod : aqmodList) {
							// if (mod.getRukuNum() == null
							// || mod.getRukuNum() == 0) {
							// if ((wddOld.getHgNumber() - num) >= mod
							// .getCgnumber()) {
							// mod.setRukuNum(mod
							// .getCgnumber());
							// num += mod.getCgnumber();
							// } else if ((wddOld.getHgNumber() - num) > 0) {
							// mod.setRukuNum(wddOld
							// .getHgNumber()
							// - num);
							// num += (wddOld.getHgNumber() - num);
							// } else {
							// mod.setRukuNum(0f);
							// }
							// } else {
							// Float dhNumber = mod.getRukuNum();
							// if ((dhNumber + (wddOld
							// .getHgNumber() - num)) <= mod
							// .getCgnumber()) {
							// mod
							// .setRukuNum(dhNumber
							// + (wddOld
							// .getHgNumber() - num));
							// num += wddOld.getHgNumber()
							// - num;
							// } else {
							// num += (mod.getCgnumber() - mod
							// .getRukuNum());
							// mod.setRukuNum(mod
							// .getCgnumber());
							// }
							// }
							// // ManualOrderPlan manualPlan = mod
							// // .getManualPlan();
							// // if (mod.getRukuNum() != null) {
							// // if (mod.getRukuNum() >= mod
							// // .getCgnumber()) {
							// // mod.setStatus("完成");
							// // }
							// // if (manualPlan.getRukuNum() != null) {
							// // manualPlan
							// // .setRukuNum(manualPlan
							// // .getRukuNum()
							// // + mod
							// // .getRukuNum());
							// // } else {
							// // manualPlan.setRukuNum(mod
							// // .getRukuNum());
							// // }
							// // }
							// // totalDao.update(manualPlan);
							// totalDao.update(mod);
							// }
							// }
							// }
							// 手动添加
							// if ((wddOld != null && num <
							// wddOld.getHgNumber())
							// || (waigouPlan != null && num < waigouPlan
							// .getNumber())) {
							// if (wddOld == null) {
							// wddOld = new WaigouDeliveryDetail();
							// wddOld.setHgNumber(waigouPlan.getNumber());
							// }
							// List<ManualOrderPlanDetail> sdmodList = totalDao
							// .query(
							// " from ManualOrderPlanDetail where manualPlan.id =? and  type = '手动添加' and cgnumber>0 ",
							// waigouPlan.getMopId());
							// if (sdmodList != null && sdmodList.size() > 0) {
							// for (ManualOrderPlanDetail mod : sdmodList) {
							// if (mod.getRukuNum() == null
							// || mod.getRukuNum() == 0) {
							// if ((wddOld.getHgNumber() - num) >= mod
							// .getCgnumber()) {
							// mod.setRukuNum(mod
							// .getCgnumber());
							// num += mod.getCgnumber();
							// } else if ((wddOld.getHgNumber() - num) > 0) {
							// mod.setRukuNum(wddOld
							// .getHgNumber()
							// - num);
							// num += (wddOld.getHgNumber() - num);
							// } else {
							// mod.setRukuNum(0f);
							// }
							// } else {
							// Float dhNumber = mod.getRukuNum();
							// if ((dhNumber + (wddOld
							// .getHgNumber() - num)) <= mod
							// .getCgnumber()) {
							// mod
							// .setRukuNum(dhNumber
							// + (wddOld
							// .getHgNumber() - num));
							// num += wddOld.getHgNumber()
							// - num;
							// } else {
							// num += (mod.getCgnumber() - mod
							// .getRukuNum());
							// mod.setRukuNum(mod
							// .getCgnumber());
							// }
							// }
							// // ManualOrderPlan manualPlan = mod
							// // .getManualPlan();
							// // if (mod.getRukuNum() != null) {
							// // if (mod.getRukuNum() >= mod
							// // .getCgnumber()) {
							// // mod.setStatus("完成");
							// // }
							// // if (manualPlan.getRukuNum() != null) {
							// // manualPlan
							// // .setRukuNum(manualPlan
							// // .getRukuNum()
							// // + mod
							// // .getRukuNum());
							// // } else {
							// // manualPlan.setRukuNum(mod
							// // .getRukuNum());
							// // }
							// // }
							// }
							// }
							// }

						}
					}
				} else {
					if (markId != null && markId.length() > 0
							&& rukuNumber != null && rukuNumber > 0) {
						String productStyle = null;
						List<Procard> list = null;
						if (goodsStoreWarehouse != null
								&& goodsStoreWarehouse.equals("外购件库")) {
							productStyle = "批产";
							// 查找对应件号已激活，并且数量不足的外购件（排除半成品），并且不是待采购的物料
							String hql = "from Procard where markId=? and productStyle=? and procardStyle='外购' and jihuoStatua='激活' and status='已发卡' and (tjNumber<klNumber or (tjNumber is null and klNumber>0))  and (needProcess <> 'yes' or needProcess is null)"
									+ " and (cgNumber is null or cgNumber+tjNumber<filnalCount) order by selfcard ";
							list = totalDao.query(hql, markId, productStyle);
						} else if (goodsStoreWarehouse != null
								&& goodsStoreWarehouse.equals("备货库")) {
							// 查找对应件号已激活，并且数量不足的外购件（排除半成品）
							String hql = "from Procard where markId=?  and procardStyle='外购' and jihuoStatua='激活' and status='已发卡' and (tjNumber<klNumber or (tjNumber is null and klNumber>0))  and (needProcess <> 'yes' or needProcess is null)"
									+ " order by selfcard ";
							list = totalDao.query(hql, markId);
						} else {
							productStyle = "试制";
							// 查找对应件号已激活，并且数量不足的外购件（排除半成品），并且不是待采购的物料
							String hql = "from Procard where markId=? and productStyle=? and procardStyle='外购' and jihuoStatua='激活' and status='已发卡' and (tjNumber<klNumber or (tjNumber is null and klNumber>0))  and (needProcess <> 'yes' or needProcess is null)"
									+ " and cgNumber is null order by selfcard ";
							list = totalDao.query(hql, markId, productStyle);
						}

						Float procardNumber = 0F;
						// 从已提交量最多的 开始填充数量
						for (Procard waiProcard : list) {
							if (waiProcard.getTjNumber() == null) {
								waiProcard.setTjNumber(0f);
							}
							procardNumber = waiProcard.getFilnalCount()
									- waiProcard.getTjNumber();
							if (rukuNumber > procardNumber) {
								rukuNumber -= procardNumber;
							} else {
								procardNumber = rukuNumber;
								rukuNumber = 0F;
							}
							if (waiProcard.getNeedProcess() == null
									|| !waiProcard.getNeedProcess().equals(
											"yes")) {
								waiProcard.setTjNumber(waiProcard.getTjNumber()
										+ procardNumber);// 外购件数量更新
								Float minNumber = waiProcard.getTjNumber()
										/ waiProcard.getQuanzi2()
										* waiProcard.getQuanzi1();
								if (waiProcard.getTjNumber().equals(
										waiProcard.getFilnalCount())) {
									minNumber = (float) Math.ceil(minNumber);
								}
								//waiProcard.setMinNumber(minNumber);// 该件号对应上层最小数量
								bool = totalDao.update(waiProcard);
								if (bool) {
									// 查询本层是否存在未提交数据
									String thisHql = "from Procard where rootId=? and belongLayer=? and productStyle=?  and (minNumber is null or minNumber<1)";
									int count = totalDao.getCount(thisHql,
											waiProcard.getRootId(), waiProcard
													.getBelongLayer(),
											productStyle);
									if (count == 0) {// 本层都已经提交
										/** 开始激活上层数据 */
										// bool = procardServer.jihuoProcard(
										// waiProcard.getRootId(), waiProcard
										// .getBelongLayer() - 1,
										// "外购件入库，件号为:" + markId);
										// bool = procardServer
										// .jihuoSingleProcard(waiProcard
										// .getProcard());
									}
								}
							}
							if (rukuNumber <= 0) {
								break;
							}
						}
					}
				}
			}
		}
		return bool;
	}

	/**
	 * 提交下层工序
	 * 
	 * @param procard
	 * @param applyCount
	 */
	private void updateDwonProcessNo(Procard procard, Float applyCount) {
		// TODO Auto-generated method stub
		Set<Procard> sonset = procard.getProcardSet();
		if (sonset != null && sonset.size() > 0) {
			for (Procard son : sonset) {
				if (!son.getProcardStyle().equals("外购")
						|| (son.getNeedProcess() != null && son
								.getNeedProcess().equals("yes"))) {
					Set<ProcessInfor> processSet = son.getProcessInforSet();
					Float count = 0f;
					if (son.getProcardStyle().equals("外购")) {
						count = applyCount * son.getQuanzi2()
								/ son.getQuanzi1();
					} else {
						count = applyCount * son.getCorrCount();
					}
					boolean b = false;
					for (ProcessInfor process : processSet) {
						process.setApplyCount(process.getApplyCount() + count);
						process.setSubmmitCount(process.getSubmmitCount()
								+ count);
						if (process.getSubmmitCount() == son.getFilnalCount()) {
							process.setStatus("完成");
							b = true;
						}
						totalDao.update(process);
					}
					if (son.getTjNumber() == null) {
						son.setTjNumber(count);
					} else {
						son.setTjNumber(son.getTjNumber() + count);
					}
					if (son.getMinNumber() == null) {
						son.setMinNumber(applyCount);
					} else {
						son.setMinNumber(son.getMinNumber() + applyCount);
					}
					if (b) {
						son.setStatus("完成");
						totalDao.update(son);
					}
					// 递归下层
					updateDwonProcessNo(son, count);
				}
			}
		}
	}

	/**
	 * 外委接受修改下层零件提交数量
	 * 
	 * @param procard
	 * @param applyCount
	 * @param type
	 * @return
	 */
	private String updateSonTjNumber(Procard procard, Float applyCount,
			int type, Users user) {
		// TODO Auto-generated method stub
		Set<Procard> sonSet = procard.getProcardSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (Procard son : sonSet) {
				if (son.getWwblCount() != null && son.getWwblCount() > 0) {
					if (!son.getProcardStyle().equals("外购")) {
						// 增加可领数量不变hascount表示已领
						if (son.getKlNumber() != null) {
							son.setKlNumber(son.getKlNumber() + applyCount
									* son.getCorrCount());
						} else {
							son.setKlNumber(applyCount * son.getCorrCount());
						}

						if (son.getTjNumber() == null) {
							son.setTjNumber(applyCount * son.getCorrCount());
						} else {
							son.setTjNumber(son.getTjNumber() + applyCount
									* son.getCorrCount());
						}
						if (son.getMinNumber() == null) {
							son.setMinNumber(applyCount);
						} else {
							son.setMinNumber(son.getMinNumber() + applyCount);
						}
						son.setJihuoStatua("激活");
						totalDao.update(son);
						if (type == 0) {// 生成第一下层在制品
							zaizhiInput(son, user, applyCount
									* son.getCorrCount(), "在制品入库(委外接收)");
						}
						// 提交对应数量工序
						updateProcessTjNumber(son, applyCount
								* son.getCorrCount());
						// 递归下层
						updateSonTjNumber(son, applyCount * son.getCorrCount(),
								1, user);
					} else {
						if (son.getNeedProcess() != null
								&& son.getNeedProcess().equals("yes")) {// 半成品
							son.setKlNumber(son.getKlNumber() + applyCount
									* son.getQuanzi2() / son.getQuanzi1());
							if (son.getTjNumber() == null) {
								son.setTjNumber(applyCount * son.getQuanzi2()
										/ son.getQuanzi1());
							} else {
								son.setTjNumber(son.getTjNumber() + applyCount
										* son.getQuanzi2() / son.getQuanzi1());
							}
							if (son.getMinNumber() == null) {
								son.setMinNumber(applyCount);
							} else {
								son.setMinNumber(son.getMinNumber()
										+ applyCount);
							}
							son.setJihuoStatua("激活");
							totalDao.update(son);
							if (type == 0) {// 生成第一下层在制品
								zaizhiInput(son, user, applyCount
										* son.getCorrCount(), "在制品入库(委外接收)");
							}
							// 提交对应数量工序
							updateProcessTjNumber(son, applyCount
									* son.getQuanzi2() / son.getQuanzi1());
						} else if (type != 0) {
							updateWgTjNumber(son, applyCount * son.getQuanzi2()
									/ son.getQuanzi1());
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * 提交对应数量工序
	 * 
	 * @param procard
	 * @param applyCount
	 * @return
	 */
	private String updateProcessTjNumber(Procard procard, float applyCount) {
		// TODO Auto-generated method stub
		Set<ProcessInfor> processSet = procard.getProcessInforSet();
		if (processSet != null && processSet.size() > 0) {
			for (ProcessInfor process : processSet) {
				applyCount = Math.round(applyCount);
				process.setApplyCount(process.getApplyCount() + applyCount);
				process.setSubmmitCount(process.getSubmmitCount() + applyCount);
				if (procard.getFilnalCount().equals(process.getSubmmitCount())) {
					process.setStatus("完成");
				} else if (process.getStatus() == null
						|| process.getStatus().equals("初始")) {
					process.setStatus("自检");
				}
				totalDao.update(process);
			}
			// 获取工序最小提交量
			Float submitCount = (Float) totalDao
					.getObjectByCondition(
							"select min(submmitCount) from ProcessInfor where procard.id=?",
							procard.getId());
			if (submitCount != null
					&& procard.getFilnalCount().equals(submitCount)) {// 工序全部提交完成
				procard.setStatus("完成");
				totalDao.update(procard);
			}
			if (submitCount != null
					&& !submitCount.equals(procard.getTjNumber())) {
				procard.setTjNumber(submitCount);
				Float minNumber = null;
				if (procard.getProcardStyle().equals("外购件")) {
					minNumber = procard.getTjNumber() * procard.getQuanzi1()
							/ procard.getQuanzi2();
					procard.setMinNumber(minNumber);
				} else if (procard.getProcardStyle().equals("总成")) {
				} else {
					minNumber = procard.getTjNumber() / procard.getCorrCount();
					procard.setMinNumber(minNumber);
				}
				// 修改上层激活数量
				if (procard.getProcard() != null) {
					Float sonMinNumber = (Float) totalDao
							.getObjectByCondition(
									"select min(minNumber) from Procard where procard.id=?",
									procard.getProcard().getId());
					if (sonMinNumber != null && sonMinNumber > 0) {
						Float sonMinNumber2 = (float) Math.floor(sonMinNumber);
						Procard father = procard.getProcard();
						if (father.getJihuoStatua() == null) {
							father.setJihuoStatua("激活");
						}
						if (sonMinNumber2 > father.getKlNumber()) {
							if (father.getHascount() == null) {
								father.setHascount(0f);
							}
							father.setHascount(father.getHascount()
									+ (sonMinNumber2 - father.getKlNumber()));
							father.setKlNumber(sonMinNumber2);
						}
						totalDao.update(father);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 提交对应数量外购件
	 * 
	 * @param wgProcard
	 * @param outCount
	 * @return
	 */
	private String updateWgTjNumber(Procard wgProcard, Float outCount) {
		// TODO Auto-generated method stub
		if (wgProcard.getTjNumber() == null) {
			wgProcard.setTjNumber(outCount);
		} else {
			wgProcard.setTjNumber(wgProcard.getTjNumber() + outCount);
		}
		if (wgProcard.getTjNumber() > wgProcard.getFilnalCount()) {
			wgProcard.setTjNumber(wgProcard.getFilnalCount());
		}
		// 回馈接收外委数量
		if (wgProcard.getWwblreceiveCount() == null) {
			wgProcard.setWwblreceiveCount(outCount);
		} else {
			wgProcard.setWwblreceiveCount(wgProcard.getWwblreceiveCount()
					+ outCount);
		}
		if (wgProcard.getKlNumber() == null) {
			wgProcard.setKlNumber(outCount);
		} else {
			wgProcard.setKlNumber(wgProcard.getKlNumber() + outCount);
		}
		if (wgProcard.getKlNumber() > wgProcard.getFilnalCount()) {
			wgProcard.setKlNumber(wgProcard.getFilnalCount());
		}
		if (wgProcard.getHascount() == null) {
			wgProcard.setHascount(0f);
		}
		if (wgProcard.getHascount() < 0.001
				&& (wgProcard.getHascount() * wgProcard.getQuanzi1() / wgProcard
						.getQuanzi2()) < 0.01) {
			// 判断小数点
			wgProcard.setHascount(0f);
		}
		if (wgProcard.getHascount() == 0
				&& (wgProcard.getKlNumber() / wgProcard.getFilnalCount()) > 0.95) {
			wgProcard.setStatus("完成");
			wgProcard.setJihuoStatua("激活");
		}
		Float minNumber = wgProcard.getTjNumber() * wgProcard.getQuanzi1()
				/ wgProcard.getQuanzi2();
		wgProcard.setMinNumber(minNumber);
		totalDao.update(wgProcard);
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
	@Override
	public List findGoodsStore(String markId, String storeLot) {
		String hql = "from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and goodsStoreWarehouse in('成品库','备货库')";
		return totalDao.query(hql, markId, storeLot);
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public List<GoodsStore> findgoodsStoreNo(GoodsStore goodsStore,
			List<String> viewList) {
		String hql = "from GoodsStore t where  printStatus is NULL";
		Map<String, Object> params = new HashMap<String, Object>();
		hql = addWhere(hql, params, goodsStore, viewList);
		String str = "";
		if (goodsStore != null && goodsStore.getWgType() != null
				&& !"".equals(goodsStore.getWgType())) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", goodsStore.getWgType());
			if (category != null) {
				getWgtype(category);
			}
			str = " and wgType in (" + strbu.toString().substring(1) + ")";
		}
		hql += str;
		String hqlCount = "select count(*) " + hql;
		hql += " order by id desc ";
		List<GoodsStore> query = (List) totalDao.find(hql, params);
		strbu = new StringBuffer();
		return query;
	}

	private void getWgtype(Category category) {
		List<Category> list = totalDao.query(
				" from  Category where fatherId = ?", category.getId());
		if (list != null && list.size() > 0) {
			for (Category category2 : list) {
				getWgtype(category2);
			}
		} else {
			strbu.append(",'" + category.getName() + "'");
		}
	}

	@Override
	public void printInfor(Integer id) {
		GoodsStore goodsStore = (GoodsStore) totalDao.getObjectById(
				GoodsStore.class, id);
		goodsStore.setPrintStatus("YES");
		totalDao.update(goodsStore);

	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	@Override
	public boolean addgoodHouse(GoodHouse goodHouse) {
		boolean b = this.totalDao.save(goodHouse);
		return b;
	}

	@Override
	public Object[] findgoodHouse(GoodHouse goodHouse, int parseInt,
			int pageSize) {
		if (goodHouse == null) {
			goodHouse = new GoodHouse();
		}
		String hql = totalDao.criteriaQueries(goodHouse, null);
		hql += " order by id";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public GoodHouse salgoodHouseByid(GoodHouse goodHouse) {
		GoodHouse goodHouse2 = (GoodHouse) this.totalDao.getObjectById(
				GoodHouse.class, goodHouse.getId());
		return goodHouse2;
	}

	@Override
	public boolean updategoodHouse(GoodHouse goodHouse) {
		GoodHouse goodHouse2 = (GoodHouse) this.totalDao.getObjectById(
				GoodHouse.class, goodHouse.getId());
		String hql_kw = "from WarehouseNumber where warehouseArea = ? and wareHouseName = ?";
		List<WarehouseNumber> kwList = totalDao.query(hql_kw, goodHouse2
				.getGoodHouseName(), goodHouse2.getGoodsStoreWarehouse());
		for (WarehouseNumber kw : kwList) {
			kw.setWarehouseArea(goodHouse.getGoodHouseName());
			kw.setWareHouseName(goodHouse.getGoodsStoreWarehouse());
			totalDao.update(kw);
		}
		goodHouse2.setGoodHouseName(goodHouse.getGoodHouseName());
		goodHouse2.setGoodHouseNum(goodHouse.getGoodHouseNum());
		goodHouse2.setGoodsStoreWarehouse(goodHouse.getGoodsStoreWarehouse());
		// 修改面积

		goodHouse2.setGoodAllArea(goodHouse.getGoodAllArea());
		goodHouse2.setGoodIsUsedArea(goodHouse.getGoodIsUsedArea());
		if (goodHouse.getGoodIsUsedArea() == null) {
			goodHouse.setGoodIsUsedArea(0D);
		}
		goodHouse2.setGoodLeaveArea(goodHouse.getGoodAllArea()
				- goodHouse.getGoodIsUsedArea());
		boolean b = this.totalDao.update(goodHouse2);
		return b;
	}

	@Override
	public List findgoodHouselist() {
		// TODO Auto-generated method stub
		List list = totalDao.query("from GoodHouse order by id desc ");
		return list;
	}

	@Override
	public String getCanInput(String goodsStoreMarkId, String goodsStoreLot) {
		// TODO Auto-generated method stub
		String msg = "true";
		Procard procard = (Procard) totalDao.getObjectByCondition(
				"from Procard where markId=? and selfCard<? order by selfCard",
				goodsStoreMarkId, goodsStoreLot);
		if (procard.getRukuCount() == null || procard.getRukuCount() <= 0) {
			return "请先将" + procard.getSelfCard() + "批次入库!";
		}
		return msg;
	}

	@Override
	public void chuli() {
		// List<GoodsStore> gs =
		// totalDao.query("from GoodsStore where goodsStoreId > 11952 and goodsStoreId< 12686 and  goodsStoreWarehouse in ('研发库')");
		List<CorePayable> gs = totalDao
				.query("from CorePayable where id > 7336 and id < 7604 ");

		for (CorePayable cp : gs) {
			boolean b = corePayableServer.zhuyinPingzheng(cp);

			System.out.println(b + "");
		}
	}

	@Override
	public String addSgrk(File addgoodsStore, GoodsStore goodsStore,
			String status) {
		String msg = "true";
		boolean flag = true;
		String fileName = "ruku_" + Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf2.format(new Date());
		String date = sdf1.format(new Date());
		int i = 0;
		try {
			FileCopyUtils.copy(addgoodsStore, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}

			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 2) {
				List<Integer> strList = new ArrayList<Integer>();
				StringBuffer sberror = new StringBuffer();
				int successcount = 0;
				int errorcount = 0;
				int error_index = 0;
				for (i = 2; i < exclecolums; i++) {
					try {
						Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
						String a = cells[1].getContents();// 所属仓库
						if (!"半成品库".equals(a) && !"外购件库".equals(a)
								&& !"成品库".equals(a) && !"占用库".equals(a)
								&& !"在途库".equals(a) && !"研发库".equals(a)
								&& !"售后库".equals(a) && !"综合库".equals(a)) {
							sberror.append("第" + (i + 1) + "行,库别错误!\\n");
							errorcount++;
							if (error_index == 0) {
								error_index = i + 1;
							}
							continue;
						}
						String af = cells[13].getContents();// 供料属性
						if ("外购件库".equals(a) || "占用库".equals(a)
								|| "在途库".equals(a) || "售后库".equals(a)
								|| "研发库".equals(a)) {
							if (af == null || af == "") {
								sberror.append("第" + (i + 1) + "行,供料属性未填写!\\n");
								errorcount++;
								if (error_index == 0) {
									error_index = i + 1;
								}
								continue;
							}
							if (af.indexOf("CS") >= 0) {
								af = "CS";
							} else if (af.indexOf("TK Price") >= 0) {
								af = "TK Price";
							} else if (af.indexOf("TK AVL") >= 0) {
								af = "TK AVL";
							} else if (af.indexOf("TK") >= 0) {
								af = "TK";
							} else {
								sberror.append("第" + (i + 1) + "行,供料属性错误!\\n");
								errorcount++;
								if (error_index == 0) {
									error_index = i + 1;
								}
								continue;
							}
						}
						if (error_index > 0) {
							continue;
						}

						String ad = cells[2].getContents();// 仓区
						String o = cells[3].getContents();// 库位
						String partNumber = cells[4].getContents();// 件号
						String c = cells[5].getContents();// 名称
						String e = cells[6].getContents();// 规格
						String n = cells[7].getContents();// 版本
						String f = cells[8].getContents();// 数量
						if (null == f || "".equals(f)) {
							f = "0";
						}
						String g = cells[9].getContents();// 单位
						String k = cells[10].getContents();// 转换数量
						String k1 = cells[11].getContents();// 转换单位
						String d = cells[12].getContents();// 批次
						String h = cells[14].getContents();// 入库类型
						String j = cells[15].getContents();// 送货单号
						String aj = cells[16].getContents();// 订单号
						String l = cells[17].getContents();// 税率
						String l2 = cells[18].getContents();// 含税价格
						String l3 = cells[19].getContents();// 不含税价格
						String m = cells[20].getContents();// 总成件号
						if (m == null || "".equals(m)) {
							m = "";
						}
						String p = cells[21].getContents();// 申请单编号
						String q = cells[22].getContents();// 炉批号
						String r = cells[23].getContents();// 工艺卡号
						String s = cells[24].getContents();// 期初数量
						if (null == s || "".equals(s)) {
							s = "0";
						}
						String t = cells[25].getContents();// 经办人
						if (t == null || "".equals(t)) {
							t = "0";
						}
						String u = cells[26].getContents();// 上期质检时间
						String v = cells[27].getContents();// 质检周期
						if (null == v || "".equals(v)) {
							v = "0";
						}
						String w = cells[28].getContents();// 供应商
						if (w == null || "".equals(w)) {
							w = "";
						}
						String x = cells[29].getContents();// 负责人
						String y = cells[30].getContents();// 计划员
						String z = cells[31].getContents();// 客户
						String ab = cells[32].getContents();// 用途
						String ac = cells[33].getContents();// 商品备注
						String ae = Util.getDateTime("yyyy-MM-dd");
						try {
							ae = cells[34].getContents();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						String suodingdanhao = "";
						if (cells.length >= 36) {
							suodingdanhao = cells[35].getContents();
						}
						String lendNeckStatus = "";
						if (cells.length >= 37) {
							lendNeckStatus = cells[36].getContents();
						}
						String banben_hql = "";
						if (n != null && n.length() > 0) {
							banben_hql = " and banbenhao='" + n + "'";
						}
						// 件号+版本+供料属性
						String hql1 = "from YuanclAndWaigj where markId=? and kgliao=? "
								+ banben_hql;
						YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj) totalDao
								.getObjectByCondition(hql1, partNumber, af);
						if (yuanclAndWaigj == null) {
							// 件号+供料属性
							hql1 = "from YuanclAndWaigj where markId=? and kgliao=? ";
							yuanclAndWaigj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql1, partNumber, af);
							if (yuanclAndWaigj != null) {
								// 使用外购件库中的版本覆盖库存数据
								n = yuanclAndWaigj.getBanbenhao();
							}
						}
						if (yuanclAndWaigj == null) {
							// 件号
							hql1 = "from YuanclAndWaigj where markId=?";
							yuanclAndWaigj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql1, partNumber);
							if (yuanclAndWaigj != null) {
								// 使用外购件库中的版本覆盖库存数据
								af = yuanclAndWaigj.getKgliao();
							}
						}
						if(yuanclAndWaigj!=null){
							partNumber =yuanclAndWaigj.getMarkId();//防空格 
						}
						Float bili = 0f;// 单张重量
						String wgType = "";// 物料类别
						if (yuanclAndWaigj != null) {
							bili = yuanclAndWaigj.getBili();
							wgType = yuanclAndWaigj.getWgType();
						} else if ("外购件库".equals(a) || "占用库".equals(a)
								|| "在途库".equals(a)) {
							wgType = "外购件";
						}

						Float count = 0f;
						Float zhishu = 0f;
						if (bili != null && bili > 0) {
							if ((f != null && !"".equals(f))
									&& (k == null || "".equals(k))) {
								count = Float.parseFloat(f);
								zhishu = count / bili;
							} else if ((k != null && !"".equals(k))
									&& (f == null || "".equals(f))) {
								zhishu = Float.parseFloat(k);
								count = zhishu * bili;
							}
						} else {
							count = Float.parseFloat(f);
						}

						GoodsStore gs = new GoodsStore();
						gs.setGoodsStoreWarehouse(a);// 库别
						gs.setGoodHouseName(ad);// 仓库
						gs.setGoodsStorePosition(o);// 库位
						gs.setGoodsStoreMarkId(partNumber);// 件号
						gs.setBanBenNumber(n);// 版本
						gs.setWgType(wgType);// 物料类别
						gs.setKgliao(af);// 供料属性
						gs.setGoodsStoreGoodsName(c);// 名称
						gs.setGoodsStoreLot(d);// 批次
						gs.setGoodsStoreFormat(e);// 规格
						gs.setGoodsStoreCount(count);// 数量
						gs.setGoodsStoreUnit(g);
						gs.setStyle(h);
						gs.setGoodsStoreProMarkId(m);
						gs.setGoodsStoreZhishu(zhishu);
						gs.setGoodsStoreZHUnit(k1);
						gs.setOrderId(aj);
						if (l != null && !l.equals("")) {
							gs.setTaxprice(Double.parseDouble(l));
						}
						if (l2 != null && !l2.equals("")) {
							gs.setHsPrice(Double.parseDouble(l2));
							gs.setMoney(gs.getHsPrice()
									* gs.getGoodsStoreCount());
						}
						if (l3 != null && !l3.equals("")) {
							gs.setGoodsStorePrice(Double.parseDouble(l3));
						}
						gs.setGoodsStoreDate(ae);//
						gs.setGoodsStoreNumber(p);//
						gs.setGoodsStoreLuId(q);//
						gs.setGoodsStoreArtsCard(r);//
						gs.setBeginning_num(Float.parseFloat(s));//
						gs.setGoodsStoreCharger(t);//
						gs.setGoodsStorelasttime(u);//
						gs.setGoodsStoreround(Float.parseFloat(v));//
						gs.setGoodsStoreSupplier(w);//
						ZhUser zhUser = (ZhUser) totalDao.getObjectByCondition(
								"from ZhUser where cmp=? or usercode=?", gs
										.getGoodsStoreSupplier(), gs
										.getGoodsStoreSupplier());
						if (zhUser != null) {
							gs.setGysId(zhUser.getId() + "");
							gs.setGoodsStoreSupplier(zhUser.getCmp());//
						}
						gs.setGoodsStorePerson(x);//
						gs.setGoodsStorePlanner(y);//
						gs.setGoodsStoreCompanyName(z);//
						gs.setGoodsStoreUseful(ab);//
						gs.setGoodsStoreGoodsMore(ac);//
						gs.setGoodsStoreSendId(j);
						gs.setStatus("入库");
						gs.setGoodsStorePlanner(Util.getLoginUser().getName());
						gs.setGoodsStoreTime(Util.getDateTime());// 系统时间
						gs.setPrintStatus("YES");// 默认打印
						gs.setSuodingdanhao(suodingdanhao);// 锁订单号
						gs.setLendNeckStatus(lendNeckStatus);//借领属性，综合库使用
						if("借".equals(lendNeckStatus)){
							gs.setLendNeckCount(gs.getGoodsStoreCount());
						}
						saveSgrk(gs);
						if ("外购件库".equals(a) || "研发库".equals(a)
								|| "售后库".equals(a) || "成品库".equals(a)) {
							try {
								corePayableServer.addCorePayable(gs);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						//
						// if ("外购件库".equals(a)) {
						// // 版本
						// String banben_hql2 = "";
						// if (gs.getBanBenNumber() != null
						// && gs.getBanBenNumber().length() > 0) {
						// banben_hql2 = " and banBenNumber='"
						// + gs.getBanBenNumber() + "'";
						// }
						// // 遍历出库数据
						// List<Goods> ztGList = totalDao
						// .query(
						// "from Goods where goodsClass='在途库' and goodsCurQuantity>0 and goodsMarkId=? and kgliao=?"
						// + banben_hql2, gs
						// .getGoodsStoreMarkId(), gs
						// .getKgliao());
						// if (ztGList != null && ztGList.size() > 0) {//
						// 有对应的在途数据
						// Float rkcount = gs.getGoodsStoreCount();
						// for (Goods zt : ztGList) {
						// Float ckCount = 0f;
						// if (rkcount > zt.getGoodsCurQuantity()) {// 入库数量大于在途
						// ckCount = zt.getGoodsCurQuantity();
						// rkcount -= zt.getGoodsCurQuantity();
						// zt.setGoodsCurQuantity(0f);
						// totalDao.update(zt);
						// } else {
						// ckCount = rkcount;
						// zt.setGoodsCurQuantity(zt
						// .getGoodsCurQuantity()
						// - rkcount);
						// rkcount = 0f;
						// totalDao.update(zt);
						// }
						// // 在途库做出库
						// Sell sell = new Sell();
						// sell.setSellWarehouse("在途库");
						// sell.setSellMarkId(zt.getGoodsMarkId());
						// sell.setBanBenNumber(zt.getBanBenNumber());
						// sell.setKgliao(zt.getKgliao());
						// sell.setSellCount(ckCount);
						//
						// sell.setSellArtsCard(zt.getGoodsArtsCard());
						// sell.setSellSupplier(zt.getGoodsSupplier());
						// sell.setSellFormat(zt.getGoodsFormat());
						// sell.setSellLot(zt.getGoodsLotId());
						// sell.setSellAdminName(Util.getLoginUser()
						// .getName());
						// sell.setSellGoods(zt.getGoodsFullName());
						// sell
						// .setGoodHouseName(zt
						// .getGoodHouseName());
						// sell.setSellDate(date);
						// sell.setSellTime(time);
						// sell.setSellUnit(zt.getGoodsUnit());
						// sell.setSellCharger("admin");
						// // --------------------------------
						// sell.setSellProMarkId(zt.getGoodsMarkId());
						// sell.setPrintStatus("YES");
						// sell.setStyle("正常出库");
						// sell.setSellPeople("入库");
						// totalDao.save(sell);
						// if (rkcount == 0) {
						// break;
						// }
						// }
						// }
						//
						// }

						successcount++;
						System.out.println("导入成功:" + (i - 1) + "_"
								+ gs.getGoodsStoreMarkId());
						// if ("外购件库".equals(gs.getGoodsStoreWarehouse())
						// || ("试制库".equals(gs.getGoodsStoreWarehouse()) && gs
						// .getGoodsStoreMarkId() != null)) {
						// updateWaiProcard(gs.getGoodsStoreMarkId(), gs
						// .getGoodsStoreCount(), gs
						// .getGoodsStoreWarehouse(), gs
						// .getGoodsStoreSendId());
						// } else {
						// strList.add(i - 1);
						// continue;
						// }
						if (i % 200 == 0) {
							totalDao.clear();
						}
					} catch (Exception e) {
						e.printStackTrace();
						sberror.append("第" + (i + 1) + "行,数据格式错误!异常:"
								+ e.getMessage());
						errorcount++;
						if (error_index == 0) {
							error_index = i + 1;
						}
						continue;
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				String errs = "";
				if (errorcount > 0) {
					errs = "从第" + error_index + "行开始出现错误，请核对错误后，从第"
							+ error_index + "行开始重新导入(即删除excel中1-"
							+ (error_index - 1) + "行的数据)!\\n";
				}
				msg = "总条数:" + exclecolums + "\\n已成功导入" + successcount + "个,失败"
						+ errorcount + "个\\n" + errs + "失败的內容如下:\\n" + sberror;
			} else {
				msg = "没有获取到行数";
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "导入失败,出现异常" + e;

		}
		return msg;
	}

	@Override
	public List<GoodsStore> findgoodsStoreDQ() {
		String hql = "from Goods where goodsCurQuantity>0 and goodsClass not in ('待检库','不合格品库')";
		List<Goods> goodsList = totalDao.find(hql);
		List<GoodsStore> gsList = new ArrayList<GoodsStore>();
		for (Goods g : goodsList) {
			String hql_gs = "from GoodsStore where DATEDIFF(goodsStorenexttime,CURRENT_DATE)<7 and goodsStoreMarkId=? and goodsStoreWarehouse = ?";
			// 批次
			if (g.getGoodsLotId() != null && g.getGoodsLotId().length() > 0) {
				hql_gs += " and goodsStoreLot = '" + g.getGoodsLotId() + "'";
			} else {
				hql_gs += " and (goodsStoreLot is null or goodsStoreLot = '')";
			}
			// 仓区
			if (g.getGoodHouseName() != null
					&& g.getGoodHouseName().length() > 0) {
				hql_gs += " and goodHouseName = '" + g.getGoodHouseName() + "'";
			} else {
				hql_gs += " and (goodHouseName is null or goodHouseName = '')";
			}
			// 版本
			if (g.getBanBenNumber() != null && g.getBanBenNumber().length() > 0) {
				hql_gs += " and banBenNumber = '" + g.getBanBenNumber() + "'";
			} else {
				hql_gs += " and (banBenNumber is null or banBenNumber = '')";
			}
			// 供料属性
			if (g.getKgliao() != null && g.getKgliao().length() > 0) {
				hql_gs += " and kgliao = '" + g.getKgliao() + "'";
			} else {
				hql_gs += " and ( kgliao is null or kgliao = '')";
			}
			// 库位
			if (g.getGoodsPosition() != null
					&& g.getGoodsPosition().length() > 0) {
				hql_gs += " and goodsStorePosition = '" + g.getGoodsPosition()
						+ "'";
			} else {
				hql_gs += " and (goodsStorePosition is null or goodsStorePosition = '')";
			}
			GoodsStore gs = (GoodsStore) totalDao.getObjectByCondition(hql_gs,
					g.getGoodsMarkId(), g.getGoodsClass());
			if (gs != null) {
				gsList.add(gs);
			}
		}
		return gsList;
	}

	public WageStandardServer getWss() {
		return wss;
	}

	public void setWss(WageStandardServer wss) {
		this.wss = wss;
	}

	public InsuranceGoldServer getIgs() {
		return igs;
	}

	public void setIgs(InsuranceGoldServer igs) {
		this.igs = igs;
	}

	public static Double getSeconds() {
		return SECONDS;
	}

	@Override
	public List<OrderManager> getorderbymarkId(String markId) {
		if (markId != null && !"".equals(markId)) {
			String hql = " from OrderManager where id in (select orderManager.id from ProductManager where (pieceNumber =? or ywMarkId=?)"
					+ " and (hasTurn is  null or num > hasTurn) )";
			List<OrderManager> list = totalDao.query(hql, markId, markId);
			return list;
		}
		return null;
	}

	@Override
	public String updatezaitumistake() {
		// TODO Auto-generated method stub
		// 查出所有入库
		List<GoodsStore> gsList = totalDao
				.query("from GoodsStore where goodsStoreCount !=null and goodsStoreCount>0 and goodsStoreWarehouse not in('占用库','在制品','在途库') and id>=176838");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf2.format(new Date());
		String date = sdf1.format(new Date());
		if (gsList != null && gsList.size() > 0) {
			for (GoodsStore gs : gsList) {// 遍历出库数据
				List<Goods> ztGList = totalDao
						.query(
								"from Goods where goodsClass='在途库' and goodsCurQuantity !=null and goodsCurQuantity>0 and goodsMarkId=? and kgliao=?",
								gs.getGoodsStoreMarkId(), gs.getKgliao());
				if (ztGList != null && ztGList.size() > 0) {// 有对应的在途数据
					Float rkcount = gs.getGoodsStoreCount();
					for (Goods zt : ztGList) {
						Float ckCount = 0f;
						if (rkcount > zt.getGoodsCurQuantity()) {// 入库数量大于在途
							ckCount = zt.getGoodsCurQuantity();
							rkcount -= zt.getGoodsCurQuantity();
							zt.setGoodsCurQuantity(0f);
							totalDao.update(zt);
						} else {
							ckCount = rkcount;
							zt.setGoodsCurQuantity(zt.getGoodsCurQuantity()
									- rkcount);
							rkcount = 0f;
							totalDao.update(zt);
						}
						// 在途库做出库
						Sell sell = new Sell();
						sell.setSellArtsCard(zt.getGoodsArtsCard());
						sell.setSellSupplier(zt.getGoodsSupplier());
						sell.setSellFormat(zt.getGoodsFormat());
						sell.setSellLot(zt.getGoodsLotId());
						sell.setSellMarkId(zt.getGoodsMarkId());
						sell.setSellAdminName(Util.getLoginUser().getName());
						sell.setSellGoods(zt.getGoodsFullName());
						sell.setGoodHouseName(zt.getGoodHouseName());
						sell.setSellDate(date);
						sell.setSellTime(time);
						sell.setSellWarehouse("在途库");
						sell.setSellUnit(zt.getGoodsUnit());
						sell.setSellCharger("admin");
						// --------------------------------
						sell.setSellProMarkId(zt.getGoodsMarkId());
						sell.setPrintStatus("YES");
						sell.setStyle("正常出库");
						sell.setGoodsId(zt.getGoodsId());
						sell.setSellPeople("入库");
						sell.setSellCount(ckCount);
						sell.setKgliao(zt.getKgliao());
						totalDao.save(sell);
						if (rkcount == 0) {
							break;
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public String ZCsendTow(GoodsStore goodsStore) {
		// TODO Auto-generated method stub
		WarehouseNumber wn = byidWareHN(goodsStore.getGoodsStoreId());
		if (wn == null)
			return "存放库位为空，二维码发送失败！";
		String message = UtilTong.sendTowMa_1(wn.getIp(), wn.getOneNumber(),
				duankou,
				(goodsStore.getGoodsStoreId() + WaigouWaiweiPlanServerImpl.mim)
						+ "");
		if ("true".equals(message)) {
			// 闪烁
			// Util.openColorXin(wn.getFourlightIp(), duankou, true, true, wn
			// .getNumid(), wn.getMarkTypt() == null ? 0 : wn
			// .getMarkTypt().getMarkColor());
			UtilTong.openColorLight(wn.getIp(), duankou, true, false, wn
					.getOneNumber(), wn.getNumid(),
					wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
							.getMarkColor());
			return message;
		}
		return "连接断开，发送失败！";
	}

	@Override
	public String ZcRuKuiBacode(String barCode) {
		// TODO Auto-generated method stub
		GoodsStore goodsStore = byidGoodsStore(Integer.parseInt(barCode)
				- WaigouWaiweiPlanServerImpl.mim);
		if (goodsStore == null)
			return "二维码内容有误,开门失败！";
		WarehouseNumber wN = byidWareHN(goodsStore.getGoodsStoreId());
		if (wN == null)
			return "库位不存在";
		// 结束闪烁
		// Util.openColorXin(wN.getFourlightIp(), duankou, true, false, wN
		// .getNumid(), wN.getMarkTypt() == null ? 0 : wN.getMarkTypt()
		// .getMarkColor());
		// 灭灯
		UtilTong.closeLight(wN.getIp(), duankou, wN.getOneNumber(), wN
				.getNumid());
		// 亮灯的颜色
		UtilTong.openColorLight(wN.getIp(), duankou, true, true, wN
				.getOneNumber(), wN.getNumid(), wN.getMarkTypt() == null ? 0
				: wN.getMarkTypt().getMarkColor());
		// 开门
		String messages = UtilTong.OCKuWei(wN.getIp(), duankou, true, wN
				.getOneNumber(), wN.getNumid());// 开关门操作
		if ("true".equals(messages)) {
			wN.setKwStatus("开");
			wN.setCzUserId(Util.getLoginUser().getId());
			wN.setSczTime(Util.getDateTime());
			if (totalDao.update(wN)) {// 更新库位状态
			}
			return messages;
		}
		return "开门失败";
	}

	private GoodsStore byidGoodsStore(Integer i) {
		// TODO Auto-generated method stub
		return (GoodsStore) totalDao.getObjectById(GoodsStore.class, i);
	}

	private WarehouseNumber byidWareHN(Integer i) {
		// TODO Auto-generated method stub
		return (WarehouseNumber) totalDao
				.getObjectByCondition(
						"from WarehouseNumber where id = (select fk_ware_id from WareBangGoogs where fk_goodsStore_id = ?)",
						i);
	}

	@Override
	public String OpenWNByWNNumber(String barCode) {
		if (barCode != null && !"".equals(barCode)) {
			WarehouseNumber wn = (WarehouseNumber) totalDao
					.getObjectByCondition(
							" from WarehouseNumber where barCode =?", barCode);
			if ("无".equals(wn.getHave()) && !"开".equals(wn.getKwStatus())) {
				// 返回主页面
				Util.backTowMa(wn.getIp(), duankou);
				String message = UtilTong.OCKuWei(wn.getIp(), duankou, true, wn
						.getOneNumber(), wn.getNumid());
				if ("true".equals(message)) {
					wn.setKwStatus("开");
					wn.setCzUserId(Util.getLoginUser().getId());
					if (totalDao.update(wn)) {// 更新库位状态
					} else
						totalDao.update2(wn);
					return "true";
				} else {
					return "连接异常，开门失败！";
				}
			}
			return "库位必须未存放物品，且处于关门状态!";
		}
		return "没有获取到库位码!";
	}

	@Override
	public List findZaizhiApplyInput(String barCode, Procard pageprocard) {
		// TODO Auto-generated method stub

		List<OrderManager> omList = null;
		if (barCode != null && barCode.length() > 0) {
			omList = totalDao
					.query("from OrderManager where orderNum like '%" + barCode
							+ "%' or outOrderNumber like '%" + barCode + "%'");
		}
		if (omList != null && omList.size() > 0) {
			List<ProcardVo> list = new ArrayList<ProcardVo>();
			for (OrderManager om : omList) {
				Set<ProductManager> pmSet = om.getProducts();
				if (pmSet != null && pmSet.size() > 0) {
					for (ProductManager pm : pmSet) {
						// 正常直连的内部订单明细
						List<InternalOrderDetail> detailList = totalDao
								.query(
										"from InternalOrderDetail where id in(select ioDetailId from Product_Internal where productId=?)",
										pm.getId());
						List<Integer> planOrderIdList = new ArrayList<Integer>();
						if (detailList != null && detailList.size() > 0) {
							for (InternalOrderDetail iod : detailList) {
								if (planOrderIdList.contains(iod
										.getInternalOrder().getId())) {
									continue;
								} else {
									planOrderIdList.add(iod.getInternalOrder()
											.getId());

								}
								List<Procard> totalprocardList = totalDao
										.query(
												"from Procard where planOrderId=? and markId=? and procard.status not in('完成','入库')",
												iod.getInternalOrder().getId(),
												iod.getPieceNumber());
								if (totalprocardList != null
										&& totalprocardList.size() > 0) {
									for (Procard totalprocard : totalprocardList) {
										List<Procard> procardList = totalDao
												.query(
														"from Procard where rootId =? and status in('已发料','领工序','完成') and procard.status in('已发卡','已发料','领工序')",
														totalprocard.getId());
										if (procardList != null
												&& procardList.size() > 0) {
											for (Procard procard : procardList) {
												if (pageprocard != null) {
													if (pageprocard.getMarkId() != null
															&& pageprocard
																	.getMarkId()
																	.length() > 0
															&& !procard
																	.getMarkId()
																	.contains(
																			pageprocard
																					.getMarkId())) {
														continue;
													}
													if (pageprocard
															.getSelfCard() != null
															&& pageprocard
																	.getSelfCard()
																	.length() > 0
															&& !procard
																	.getSelfCard()
																	.contains(
																			pageprocard
																					.getSelfCard())) {
														continue;
													}
												}
												if (procard.getTjNumber() == null) {
													procard.setTjNumber(0f);
												}
												if (procard.getZaizhizkCount() == null) {
													procard
															.setZaizhizkCount(0f);
												}
												if (procard.getZaizhiApplyZk() == null) {
													procard
															.setZaizhiApplyZk(0f);
												}
												if (procard.getKlNumber() == null) {
													procard.setKlNumber(procard
															.getFilnalCount());
												}
												if (procard.getHascount() == null) {
													procard.setHascount(procard
															.getKlNumber());
												}
												// procard.getKlNumber()-procard.getHascount()=已生产数量
												// 可转库数量=已生产数量-已完成数量-已转库数量
												procard
														.setZaizhikzkCount(procard
																.getKlNumber()
																- procard
																		.getZaizhizkCount()
																- procard
																		.getZaizhiApplyZk());
												if (procard.getZaizhikzkCount() > 0) {
													List<ProcessInfor> processList = totalDao
															.query(
																	"from ProcessInfor where procard.id=? order by processNO",
																	procard
																			.getId());
													if (processList != null
															&& processList
																	.size() > 0) {
														for (int i = 0; i < processList
																.size(); i++) {
															float zzCount = 0f;
															ProcessInfor process = processList
																	.get(i);
															// if(procard.getMarkId().equals("3.33.00007")&&process.getProcessNO().equals(8)){
															// System.out
															// .println(procard.getMarkId()+"~~~~~~~~~"+process.getProcessNO());
															// }
															// 查询次工序次批次已经入库的数量
															Float historyCount = (Float) totalDao
																	.getObjectByCondition(
																			"select zyCount from ProcardProductRelation where procardId=? and goodsId in(select goodsId from Goods where goodsMarkId=? and goodsLotId=? and processNo=? and goodsClass='半成品库')",
																			procard
																					.getId(),
																			procard
																					.getMarkId(),
																			procard
																					.getSelfCard(),
																			process
																					.getProcessNO());
															if (historyCount != null
																	&& historyCount >= process
																			.getSubmmitCount()) {
																continue;
															}
															if (historyCount == null) {
																historyCount = 0f;
															}
															if ((i + 1) <= (processList
																	.size() - 1)) {
																if (process
																		.getSubmmitCount() > 0) {
																	ProcessInfor process2 = processList
																			.get((i + 1));
																	// 此道工序做完未流转到下道工序的数量
																	if (historyCount > process2
																			.getApplyCount()) {
																		zzCount = process
																				.getSubmmitCount()
																				- historyCount;
																	} else {
																		zzCount = process
																				.getSubmmitCount()
																				- process2
																						.getApplyCount();
																	}
																}
															} else {
																zzCount = process
																		.getSubmmitCount()
																		- historyCount;
															}
															if (zzCount > 0) {// 查询此工序当前的转库半成品数量（原设计按实物流转，改成按账务不能重复申请）
																// Float
																// haszkCount
																// = (Float)
																// totalDao
																// .getObjectByCondition(
																// "select sum(goodsCurQuantity) from Goods where id in(select goodsId from  ProcardProductRelation where procardId =?) and processNo=?",
																// procard
																// .getId(),
																// process
																// .getProcessNO());
																// if
																// (haszkCount
																// !=
																// null) {
																// zzCount -=
																// haszkCount;
																// }
																if (zzCount > 0) {// 可转库的数量
																	ProcardVo procardVo = new ProcardVo();
																	BeanUtils
																			.copyProperties(
																					procard,
																					procardVo);
																	procardVo
																			.setProcessNo(process
																					.getProcessNO());
																	procardVo
																			.setProcessName(process
																					.getProcessName());
																	procardVo
																			.setZaizhikzkCount(zzCount);
																	list
																			.add(procardVo);
																}

															}
														}
													}
													// procard.setOrderNumber(om
													// .getOrderNum());
													// procard
													// .setOutOrderNum(om
													// .getOutOrderNumber());
													// procard
													// .setRootMarkId(totalprocard
													// .getMarkId());
													// procard
													// .setRootSelfCard(totalprocard
													// .getSelfCard());
													// list.add(procard);
												}
											}
										}
									}
								}
							}
						}
						// 正式订单获取关联备货订单
						if (om.getOrderType() != null
								&& om.getOrderType().equals("正式")) {
							List<InternalOrderDetail> detailList2 = totalDao
									.query(
											"from InternalOrderDetail where id in(select idetailId from InternalZsAboutBh where pzsAboutbhId in(select id from ProductZsAboutBh where zsProductId=?))",
											pm.getId());
							if (detailList2 != null && detailList2.size() > 0) {
								for (InternalOrderDetail iod : detailList2) {
									List<Procard> totalprocardList = totalDao
											.query(
													"from Procard where planOrderId=? and markId=? and status not in('完成','入库')",
													iod.getInternalOrder()
															.getId(), iod
															.getPieceNumber());
									if (totalprocardList != null
											&& totalprocardList.size() > 0) {
										for (Procard totalprocard : totalprocardList) {
											List<Procard> procardList = totalDao
													.query(
															"from Procard where rootId =? and  status in('已发料','领工序','完成') and (procard.hascount is null or procard.hascount>0)",
															totalprocard
																	.getId());
											if (procardList != null
													&& procardList.size() > 0) {
												for (Procard procard : procardList) {
													if (pageprocard != null) {
														if (pageprocard
																.getMarkId() != null
																&& pageprocard
																		.getMarkId()
																		.length() > 0
																&& !procard
																		.getMarkId()
																		.contains(
																				pageprocard
																						.getMarkId())) {
															continue;
														}
														if (pageprocard
																.getSelfCard() != null
																&& pageprocard
																		.getSelfCard()
																		.length() > 0
																&& !procard
																		.getSelfCard()
																		.contains(
																				pageprocard
																						.getSelfCard())) {
															continue;
														}
													}
													if (procard
															.getZaizhizkCount() == null) {
														procard
																.setZaizhizkCount(0f);
													}
													if (procard
															.getZaizhiApplyZk() == null) {
														procard
																.setZaizhiApplyZk(0f);
													}
													if (procard.getKlNumber() == null) {
														procard
																.setKlNumber(procard
																		.getFilnalCount());
													}
													if (procard.getHascount() == null) {
														procard
																.setHascount(procard
																		.getKlNumber());
													}
													// procard.getKlNumber()-procard.getHascount()=已生产数量
													// 可转库数量=已生产数量-已转库数量
													procard
															.setZaizhikzkCount(procard
																	.getKlNumber()
																	- procard
																			.getZaizhizkCount()
																	- procard
																			.getZaizhiApplyZk());
													if (procard
															.getZaizhikzkCount() > 0) {

														List<ProcessInfor> processList = totalDao
																.query(
																		"from ProcessInfor where (dataStatus is null or dataStatus!='删除') and procard.id=?",
																		procard
																				.getId());
														if (processList != null
																&& processList
																		.size() > 0) {
															for (int i = 0; i < processList
																	.size(); i++) {
																float zzCount = 0f;
																ProcessInfor process = processList
																		.get(i);
																// 查询次工序次批次已经如果库的数量
																Float historyCount = (Float) totalDao
																		.getObjectByCondition(
																				"select zyCount from ProcardProductRelation where procardId=? and goodsId in(select goodsId from Goods where goodsMarkId=? and goodsLotId=? and processNo=? and goodsClass='半成品库')",
																				procard
																						.getId(),
																				procard
																						.getMarkId(),
																				procard
																						.getSelfCard(),
																				process
																						.getProcessNO());
																if (historyCount != null
																		&& historyCount >= process
																				.getSubmmitCount()) {
																	continue;
																}
																if (historyCount == null) {
																	historyCount = 0f;
																}
																if ((i + 1) <= (processList
																		.size() - 1)) {
																	if (process
																			.getSubmmitCount() > 0) {
																		ProcessInfor process2 = processList
																				.get((i + 1));
																		// 此道工序做完未流转到下道工序的数量
																		if (historyCount > process2
																				.getApplyCount()) {
																			zzCount = process
																					.getSubmmitCount()
																					- historyCount;
																		} else {
																			zzCount = process
																					.getSubmmitCount()
																					- process2
																							.getApplyCount();
																		}
																	}
																} else {
																	zzCount = process
																			.getSubmmitCount()
																			- historyCount;
																}
																if (zzCount > 0) {// 查询此工序当前的转库半成品数量
																	// Float
																	// haszkCount
																	// =
																	// (Float)
																	// totalDao
																	// .getObjectByCondition(
																	// "select sum(goodsCurQuantity) from Goods where id in(select goodsId from  ProcardProductRelation where procardId =?) and processNo=?",
																	// procard
																	// .getId(),
																	// process
																	// .getProcessNO());
																	// if
																	// (haszkCount
																	// != null)
																	// {
																	// zzCount
																	// -=
																	// haszkCount;
																	// }
																	if (zzCount > 0) {// 可转库的数量
																		ProcardVo procardVo = new ProcardVo();
																		BeanUtils
																				.copyProperties(
																						procard,
																						procardVo);
																		procardVo
																				.setProcessNo(process
																						.getProcessNO());
																		procardVo
																				.setProcessName(process
																						.getProcessName());
																		procardVo
																				.setZaizhikzkCount(zzCount);
																		list
																				.add(procardVo);
																	}

																}
															}
														}
														// procard
														// .setOrderNumber(om
														// .getOrderNum());
														// procard
														// .setOutOrderNum(om
														// .getOutOrderNumber());
														// procard
														// .setRootMarkId(totalprocard
														// .getMarkId());
														// procard
														// .setRootSelfCard(totalprocard
														// .getSelfCard());
														// list.add(procard);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return list;
		} else if (pageprocard != null) {
			List<Procard> procardList = null;
			String sql = "";
			if (pageprocard.getMarkId() != null
					&& pageprocard.getMarkId().length() > 0
					&& !pageprocard.getMarkId().contains("update")
					&& !pageprocard.getMarkId().contains("delete")) {
				sql += " and markId like '%" + pageprocard.getMarkId() + "%'";
			}
			if (pageprocard.getSelfCard() != null
					&& pageprocard.getSelfCard().length() > 0
					&& !pageprocard.getSelfCard().contains("update")
					&& !pageprocard.getSelfCard().contains("delete")) {
				sql += " and selfCard like '%" + pageprocard.getSelfCard()
						+ "%'";
			}
			if (sql.length() > 0) {
				sql = "from Procard where status in('已发料','领工序','完成') and procard.status in('初始','已发卡','已发料','领工序') "
						+ sql;
			}
			procardList = totalDao.query(sql);
			if (procardList != null && procardList.size() > 0) {
				List<ProcardVo> list = new ArrayList<ProcardVo>();
				for (Procard procard : procardList) {
					if (procard.getZaizhizkCount() == null) {
						procard.setZaizhizkCount(0f);
					}
					if (procard.getZaizhiApplyZk() == null) {
						procard.setZaizhiApplyZk(0f);
					}
					if (procard.getKlNumber() == null) {
						procard.setKlNumber(procard.getFilnalCount());
					}
					if (procard.getHascount() == null) {
						procard.setHascount(procard.getKlNumber());
					}
					// procard.getKlNumber()-procard.getHascount()=已生产数量
					// 可转库数量=已生产数量-已转库数量
					procard.setZaizhikzkCount(procard.getKlNumber()
							- procard.getZaizhizkCount()
							- procard.getZaizhiApplyZk());
					if (procard.getZaizhikzkCount() > 0) {

						List<ProcessInfor> processList = totalDao
								.query(
										"from ProcessInfor where (dataStatus is null or dataStatus!='删除') and  procard.id=?",
										procard.getId());
						if (processList != null && processList.size() > 0) {
							for (int i = 0; i < processList.size(); i++) {
								float zzCount = 0f;
								ProcessInfor process = processList.get(i);
								if ((i + 1) < processList.size()) {
									if (process.getSubmmitCount() > 0) {
										ProcessInfor process2 = processList
												.get((i + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount = process.getSubmmitCount()
												- process2.getApplyCount();
									}
								} else {
									zzCount = process.getSubmmitCount();
								}
								if (zzCount > 0) {// 查询此工序当前的转库半成品数量
									Float haszkCount = (Float) totalDao
											.getObjectByCondition(
													"select sum(goodsCurQuantity) from Goods where id in(select goodsId from  ProcardProductRelation where procardId =?) and processNo=?",
													procard.getId(), process
															.getProcessNO());
									if (haszkCount != null) {
										zzCount -= haszkCount;
									}
									if (zzCount > 0) {// 可转库的数量
										ProcardVo procardVo = new ProcardVo();
										BeanUtils.copyProperties(procard,
												procardVo);
										procardVo.setProcessNo(process
												.getProcessNO());
										procardVo.setProcessName(process
												.getProcessName());
										procardVo.setZaizhikzkCount(zzCount);
										list.add(procardVo);
									}

								}
							}
						}
					}
				}
				return list;
			}
		}
		return null;
	}

	@Override
	public ProcardVo getZaizhiApplyInputDtial(Integer id, Integer processNo) {
		// TODO Auto-generated method stub
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, id);
		if (procard != null) {
			ProcardVo procardVo = new ProcardVo();
			BeanUtils.copyProperties(procard, procardVo, new String[] {});
			if (procard.getZaizhiApplyZk() == null) {
				procard.setZaizhiApplyZk(0f);
			}
			if (procard.getZaizhizkCount() == null) {
				procard.setZaizhizkCount(0f);
			}
			if (procard.getKlNumber() == null) {
				procard.setKlNumber(procard.getFilnalCount());
			}
			if (procard.getHascount() == null) {
				procard.setHascount(procard.getKlNumber());
			}
			// procard.getKlNumber()-procard.getHascount()=已生产数量
			// 可转库数量=已生产数量-已转库数量-转库申请中数量
			Float kzCount = procard.getKlNumber() - procard.getZaizhizkCount()
					- procard.getZaizhiApplyZk();
			if (kzCount > 0) {
				ProcessInfor process = (ProcessInfor) totalDao
						.getObjectByCondition(
								"from ProcessInfor where procard.id=? and processNo=? and (dataStatus is null or dataStatus !='删除')",
								id, processNo);
				Float ylCcount = (Float) totalDao
						.getObjectByCondition(
								"select applyCount from ProcessInfor where procard.id=? and processNo >? and (dataStatus is null or dataStatus !='删除') order by processNo desc",
								id, processNo);
				// 此道工序做完未流转到下道工序的数量
				Float zzCount = process.getSubmmitCount();
				if (ylCcount != null) {
					zzCount -= ylCcount;
				}
				if (zzCount > 0) {// 查询此工序当前的转库半成品数量
					Float haszkCount = (Float) totalDao
							.getObjectByCondition(
									"select sum(goodsCurQuantity) from Goods where id in(select goodsId from  ProcardProductRelation where procardId =?) and processNo=?",
									procard.getId(), process.getProcessNO());
					if (haszkCount != null) {
						zzCount -= haszkCount;
					}
					if (zzCount > 0) {// 可转库的数量
						procardVo.setProcessNo(process.getProcessNO());
						procardVo.setProcessName(process.getProcessName());
						procardVo.setZaizhikzkCount(zzCount);
						return procardVo;
					}

				}
			}
		}
		return null;
	}

	@Override
	public String applyzaizhiinput(GoodsStore goodsStore, Integer id) {
		// TODO Auto-generated method stub
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, id);
		ProcardVo procardVo = getZaizhiApplyInputDtial(id, goodsStore
				.getProcessNo());
		Users user = Util.getLoginUser();
		if (procardVo != null && procard != null) {
			if (procardVo.getZaizhikzkCount() >= goodsStore
					.getGoodsStoreCount()) {
				procard.setZaizhiApplyZk(procard.getZaizhiApplyZk()
						+ goodsStore.getGoodsStoreCount());
				String orderNum = (String) totalDao.getObjectByCondition(
						"select orderNumber from Procard where id=?", procard
								.getRootId());
				// 成品待入库
				GoodsStore goodsStore2 = new GoodsStore();
				goodsStore2.setNeiorderId(orderNum);
				goodsStore2.setGoodsStoreMarkId(procard.getMarkId());
				goodsStore2.setBanBenNumber(procard.getBanBenNumber());
				goodsStore2.setGoodsStoreLot(procard.getSelfCard());
				goodsStore2.setGoodsStoreGoodsName(procard.getProName());
				goodsStore2.setApplyTime(Util.getDateTime());
				goodsStore2.setGoodsStoreArtsCard((String) totalDao
						.getObjectByCondition(
								"select selfCard from Procard where id=?",
								procard.getRootId()));
				goodsStore2.setGoodsStorePerson(user.getName());
				goodsStore2.setStatus("待入库");
				goodsStore2.setStyle("半成品转库");
				goodsStore2.setGoodsStoreWarehouse(goodsStore
						.getGoodsStoreWarehouse());// 库别
				goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());// 区名
				goodsStore2.setGoodsStorePosition(goodsStore
						.getGoodsStorePosition());// 库位
				goodsStore2.setGoodsStoreUnit(procard.getUnit());
				goodsStore2.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
				goodsStore2.setProcessNo(goodsStore.getProcessNo());
				goodsStore2.setYwmarkId(goodsStore.getYwmarkId());
				goodsStore2.setProcardId(procard.getId());
				totalDao.update(procard);
				return "" + totalDao.save(goodsStore2);
			} else {
				return "对不起超过可申请数量(" + procardVo.getZaizhikzkCount() + ")";
			}
		}
		return null;
	}

	@Override
	public List findzzIngoodsStoreList(GoodsStore goodsStore) {
		String hql = " from GoodsStore where status='待入库' and style ='半成品转库'  order by goodsStoreMarkId,goodsStoreLot,applyTime desc ";
		if (goodsStore != null) {
			hql = totalDao
					.criteriaQueries(
							goodsStore,
							" status='待入库' and style ='半成品转库' order by goodsStoreMarkId,goodsStoreLot,applyTime desc ");
		}
		List list = totalDao.find(hql);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				GoodsStore gs = (GoodsStore) obj;
				Procard procard = (Procard) totalDao.getObjectByCondition(
						" from Procard  where markId =? and selfCard = ?", gs
								.getGoodsStoreMarkId(), gs.getGoodsStoreLot());
				if (procard != null) {
					gs.setYwmarkId(procard.getYwMarkId());
					gs.setBeginning_num(procard.getFilnalCount());
				}
			}
		}
		return list;
	}

	@Override
	public Object[] findzzGoodsStore(GoodsStore goodsStore, String startDate,
			String endDate, Integer cpage, Integer PageSize) {
		String hql = "from GoodsStore where (status<>'待入库' )  and style ='半成品转库' and goodsStoreWarehouse!='外协库' order by goodsStoreDate desc";
		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		if (null != goodsStore) {
			String hql2 = "";
			if (goodsStore.getStatus() == null
					|| goodsStore.getStatus().length() == 0) {
				hql2 = " and status<>'待入库' ";
			}
			hql = totalDao.criteriaQueries(goodsStore, "goodsStoreTime",
					between, "")
					+ hql2
					+ " and style ='半成品转库' and goodsStoreWarehouse!='外协库'   order by goodsStoreDate desc";
		}
		Object[] procardAarr = new Object[2];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				GoodsStore gs = (GoodsStore) obj;
				Procard procard = (Procard) totalDao.getObjectByCondition(
						" from Procard  where markId =? and selfCard = ?", gs
								.getGoodsStoreMarkId(), gs.getGoodsStoreLot());
				if (procard != null) {
					gs.setYwmarkId(procard.getYwMarkId());
					gs.setBeginning_num(procard.getFilnalCount());
				}
			}
		}
		procardAarr[0] = count;
		procardAarr[1] = list;
		return procardAarr;
	}

	/**
	 * 对应生产批次在制品入库
	 * 
	 * @param procard
	 * @param count
	 * @param type
	 * @return 在制品库存,在制品与零件中间表,在制品入库数据
	 */
	public Object[] zaizhiInput(Procard procard, Users user, Float count,
			String type) {
		Object[] obj = new Object[3];
		String hqlzaizhi = "from Goods where goodsMarkId='"
				+ procard.getMarkId()
				+ "' and goodsLotId='"
				+ procard.getSelfCard()
				+ "' and goodsClass='在制品' and goodsStyle!='半成品转库' and (fcStatus is null or fcStatus='可用') ";
		List listzizhi = totalDao.find(hqlzaizhi);
		Integer rgoodsId = null;
		Goods gg = null;
		if (listzizhi != null && listzizhi.size() > 0) {
			gg = (Goods) listzizhi.get(0);
			gg.setGoodsCurQuantity(gg.getGoodsCurQuantity() + count);
			if (gg.getGoodsCurQuantity() < 0) {
				AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
						+ gg.getGoodsMarkId() + "批次:" + gg.getGoodsLotId()
						+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为"
						+ Util.getDateTime(), "2");
				gg.setGoodsCurQuantity(0f);
			}
			totalDao.update(gg);
			rgoodsId = gg.getGoodsId();
		} else {
			gg = new Goods();
			gg.setGoodsMarkId(procard.getMarkId());
			gg.setBanBenNumber(procard.getBanBenNumber());
			gg.setGoodsFormat(procard.getSpecification());
			gg.setTuhao(procard.getTuhao());
			gg.setGoodsLotId(procard.getSelfCard());
			gg.setGoodsFullName(procard.getProName());
			gg.setGoodsClass("在制品");
			gg.setGoodsCurQuantity(count);
			gg.setGoodsBeginQuantity(count);
			gg.setGoodsStyle(type);
			if (gg.getGoodsCurQuantity() < 0) {
				AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
						+ gg.getGoodsMarkId() + "批次:" + gg.getGoodsLotId()
						+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为"
						+ Util.getDateTime(), "2");
				gg.setGoodsCurQuantity(0f);
			}
			gg.setGoodsUnit(procard.getUnit());
			totalDao.save(gg);
			rgoodsId = gg.getGoodsId();

		}
		// 添加零件与在制品关系表
		ProcardProductRelation pprelation = new ProcardProductRelation();
		pprelation.setAddTime(Util.getDateTime());
		pprelation.setProcardId(procard.getId());
		pprelation.setGoodsId(rgoodsId);
		pprelation.setZyCount(count);
		pprelation.setFlagType("本批在制品");
		totalDao.save(pprelation);
		GoodsStore gs = new GoodsStore();// 添加外购件在制品入库记录
		gs.setGoodsStoreMarkId(procard.getMarkId());// 件号
		gs.setGoodsStoreFormat(procard.getSpecification());
		gs.setTuhao(procard.getTuhao());
		gs.setBanBenNumber(procard.getBanBenNumber());
		gs.setGoodsStoreGoodsName(procard.getProName());// 名称
		gs.setGoodsStoreLot(procard.getSelfCard());// 批次
		// gs.setGoodsStoreCount((float) Math.floor(g
		// .getGoodsCurQuantity()));// 数量
		gs.setGoodsStoreCount(count);// 数量
		gs.setPrintStatus("YES");
		gs.setGoodsStoreProMarkId(procard.getMarkId());// 总成件号
		gs.setGoodsStoreWarehouse("在制品");// 库别
		Users jingban = Util.getLoginUser();
		gs.setGoodsStoreCharger(jingban.getName());// 经办人
		gs.setStyle(type);// 入库类型
		if (user != null) {// 负责人
			gs.setGoodsStorePerson(user.getName());
		} else {
			gs.setGoodsStorePerson(procard.getLingliaoren());
		}
		gs.setGoodsStoreDate(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		gs.setGoodsStoreUnit(procard.getUnit());// 单位
		totalDao.save(gs);
		obj[0] = gg;
		obj[1] = pprelation;
		obj[2] = gs;
		return obj;
	}

	/**
	 * 在制品出库
	 * 
	 * @param procard
	 * @param user
	 * @param count
	 * @param type
	 * @return
	 */
	public String zaizhiout(Procard procard, Users user, Float count,
			String type) {
		List<ProcardProductRelation> pprList = totalDao
				.query(
						"from ProcardProductRelation where procardId =? and ylCount>ckCount and flagType in('本批在制品','余额在制品')"
								+ " and goodsId in (select goodsId from Goods where goodsClass='在制品')",
						procard.getId());
		if (pprList != null & pprList.size() > 0) {
			for (ProcardProductRelation ppr : pprList) {
				Goods goods = (Goods) totalDao.getObjectById(Goods.class, ppr
						.getGoodsId());
				// 在制品出库记录
				Sell zzpSell = new Sell();
				zzpSell.setSellArtsCard(procard.getSelfCard());
				zzpSell.setSellSupplier(goods.getGoodsSupplier());
				zzpSell.setSellFormat(goods.getGoodsFormat());
				zzpSell.setSellLot(goods.getGoodsLotId());
				zzpSell.setSellMarkId(goods.getGoodsMarkId());
				zzpSell.setSellAdminName(Util.getLoginUser().getName());
				zzpSell.setSellGoods(goods.getGoodsFullName());
				zzpSell.setSellDate(Util.getDateTime("yyyy-MM-dd"));
				zzpSell.setSellTime(Util.getDateTime());
				zzpSell.setSellWarehouse(goods.getGoodsClass());
				zzpSell.setSellUnit(goods.getGoodsUnit());
				zzpSell.setWgType(goods.getWgType());
				if (ppr.getFlagType().equals("余额在制品")) {
					Float kekouCount = ppr.getYlCount() - ppr.getCkCount();
					if (kekouCount > count) {
						kekouCount = count;
					}
					if (goods != null) {
						if (goods.getGoodsCurQuantity() < kekouCount) {
							// 数据有误
							zzpSell.setSellCount(goods.getGoodsCurQuantity());
							ppr.setCkCount(ppr.getCkCount()
									+ goods.getGoodsCurQuantity());
							totalDao.update(ppr);
							count -= goods.getGoodsCurQuantity();
							goods.setGoodsCurQuantity(0f);
						} else {
							zzpSell.setSellCount(kekouCount);
							ppr.setCkCount(ppr.getCkCount() + kekouCount);
							totalDao.update(ppr);
							count -= kekouCount;
							goods.setGoodsCurQuantity(goods
									.getGoodsCurQuantity()
									- kekouCount);
						}
					}
				} else {
					if (goods != null) {
						if (goods.getGoodsCurQuantity() < count) {
							zzpSell.setSellCount(goods.getGoodsCurQuantity());
							ppr.setCkCount(ppr.getCkCount()
									+ goods.getGoodsCurQuantity());
							totalDao.update(ppr);
							count -= goods.getGoodsCurQuantity();
							goods.setGoodsCurQuantity(0f);
						} else {
							zzpSell.setSellCount(count);
							ppr.setCkCount(ppr.getCkCount() + count);
							totalDao.update(ppr);
							goods.setGoodsCurQuantity(goods
									.getGoodsCurQuantity()
									- count);
							count = 0f;
						}
					}
				}
				totalDao.update(goods);
				totalDao.save(zzpSell);
				if (count == 0) {
					break;
				}
			}
		}
		return true + "";
	}

	public Object[] findFromchengToWai(GoodsStore goodsStore, int parseInt,
			int pageSiz) {
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		String str = "'成品库','备货库'";
		// " goodsStoreWarehouse in ("
		// + str
		// + ")
		String hql = totalDao
				.criteriaQueries(
						goodsStore,
						"(printStatus is null or printStatus <>'YES')and (style is null or style in ('调仓入库'))  and status = '入库'");
		int count = totalDao.getCount(hql);
		if ((goodsStore.getGoodsStoreSendId() != null && goodsStore
				.getGoodsStoreSendId().length() > 0)
				|| (goodsStore.getGoodsStoreMarkId() != null && goodsStore
						.getGoodsStoreMarkId().length() > 0)
				|| (goodsStore.getNeiorderId() != null && goodsStore
						.getNeiorderId().length() > 0)) {
			parseInt = 1;
			pageSiz = count == 0 ? 15 : count;
		}

		List<GoodsStore> goodsStoreList = totalDao.findAllByPage(hql, parseInt,
				pageSiz);
		return new Object[] { goodsStoreList, count, pageSiz };
	}

	@Override
	public Object[] findCPGoodsStoreList(GoodsStore goodsStore, int parseInt,
			int pageSiz, String status, String tag) {
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		String str = "";
		String type = "";
		String style_hql = "";
		if ("CIN".equals(status)) {
			str = "'成品库','备货库','半成品库','华为子库'";
			type = "产品入库单";
		} else if ("WGRK".equals(status)) {
			str = "'外购件库','研发库','售后库'";
			type = "外购入库单";
		} else if ("WWRK".equals(status)) {
			str = "'外协库'";
			type = "外委入库单";
		} else if ("FLRK".equals(status)) {
			str = "'综合库'";
			type = "辅料入库单";
		} else if ("SCTL".equals(status)) {
			str = "'外购件库','外协库'";
			type = "生产退料单";
			style_hql = " and style in ('生产退料入库','外协退料入库')";
		}
		String hql = totalDao
				.criteriaQueries(
						goodsStore,
						" goodsStoreWarehouse in ("
								+ str
								+ ") and (printStatus is null or printStatus <>'YES')and (style is null or style not in  ('冲销转库','调仓入库'))   ");
		if (null != tag && !"".equals(tag)) {
			if ("date".equals(tag)) {
				hql += " order by str_to_date(goodsStoreDate, '%Y-%m-%d %H') desc";
			} else if ("songhuodanhao".equals(tag)) {
				hql += " order by goodsStoreSendId desc";
			} else if ("pici".equals(tag)) {
				hql += " order by goodsStoreLot desc";
			} else {
				hql += " order by goodsStoreId desc,goodsStoreSendId asc";
			}
		} else if ("WGRK".equals(status)) {
			hql += " order by goodsStoreId desc,goodsStoreSendId asc";
		}
		hql += style_hql;
		int count = totalDao.getCount(hql);
		if ((goodsStore.getGoodsStoreSendId() != null && goodsStore
				.getGoodsStoreSendId().length() > 0)
				|| (goodsStore.getGoodsStoreMarkId() != null && goodsStore
						.getGoodsStoreMarkId().length() > 0)
				|| (goodsStore.getNeiorderId() != null && goodsStore
						.getNeiorderId().length() > 0)) {
			parseInt = 1;
			pageSiz = count == 0 ? 15 : count;
		}

		List<GoodsStore> goodsStoreList = totalDao.findAllByPage(hql, parseInt,
				pageSiz);
		if ("WWRK".equals(status)) {
			if (goodsStoreList != null && goodsStoreList.size() > 0) {
				for (GoodsStore goodsStore2 : goodsStoreList) {
					if (goodsStore2.getWwddId() != null) {
						WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao
								.get(WaigouDeliveryDetail.class, goodsStore2
										.getWwddId());
						if (wgdd.getProcessName() != null) {
							goodsStore2.setProcessName(wgdd.getProcessName());
						}
					}
				}
			}
		}

		return new Object[] { goodsStoreList, count, pageSiz };
	}

	@Override
	public void updateYmrarkId() {
		// List<GoodsStore> goodsStoreList = totalDao
		// .query("from GoodsStore where goodsStoreLot is not null and goodsStoreWarehouse='备货库'");
		// for (GoodsStore goodsStore : goodsStoreList) {
		// Procard procard = (Procard) totalDao.getObjectByCondition(
		// "from Procard where markId =? and selfCard=?", goodsStore
		// .getGoodsStoreMarkId(), goodsStore
		// .getGoodsStoreLot());
		// if (procard != null) {
		// goodsStore.setYwmarkId(procard.getYwMarkId());
		// totalDao.update(goodsStore);
		// }
		// }
		List<Sell> sellList = totalDao
				.query("from Sell where sellWarehouse in('备货库','成品库','在制品库') and sellLot is not null and sellLot !='' ");
		for (Sell sell : sellList) {
			Procard procard = (Procard) totalDao.getObjectByCondition(
					"from Procard where markId =? and selfCard=?", sell
							.getSellMarkId(), sell.getSellLot());
			if (procard != null) {
				sell.setYwmarkId(procard.getYwMarkId());
				sell.setOrderNum(procard.getOrderNumber());
				if (procard.getOrderId() != null) {
					OrderManager or = (OrderManager) totalDao.getObjectById(
							OrderManager.class, Integer.parseInt(procard
									.getOrderId()));
					if (or != null) {
						sell.setOrderNum(or.getOrderNum());
						sell.setOutOrderNumer(or.getOutOrderNumber());
					}
				}
				totalDao.update(sell);
			}
		}
	}

	@Override
	public String sbbf(Integer id, GoodsStore goodsStore) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		ProcardSbWasterxc xc = (ProcardSbWasterxc) totalDao.getObjectById(
				ProcardSbWasterxc.class, id);
		if (xc == null) {
			return "没有找到目标设变报废申请记录!";
		}
		if (goodsStore.getGoodsStoreCount() > xc.getBfclCount()) {
			return "对不起数量超额!";
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, xc
				.getProcardId());
		goodsStore.setGoodsStoreMarkId(xc.getMarkId());
		goodsStore.setBanBenNumber(procard.getBanBenNumber());
		goodsStore.setKgliao(procard.getKgliao());
		String before = "sbbf" + Util.getDateTime("yyyy-MM-dd") + "_";
		String maxNumber = (String) totalDao
				.getObjectByCondition("select max(storeLot) from GoodsStore where storeLot like '"
						+ before + "%'");
		Integer maxNo = 0;
		if (maxNumber != null) {
			String wwnum = maxNumber.substring(15, maxNumber.length());
			try {
				Integer maxNum = Integer.parseInt(wwnum);
				if (maxNum > maxNo) {
					maxNo = maxNum;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		maxNo++;
		String storeLot = before + String.format("%04d", maxNo);
		goodsStore.setGoodsStoreLot(storeLot);
		goodsStore.setGoodsStoreGoodsName(procard.getProName());
		goodsStore.setGoodsStoreFormat(procard.getSpecification());
		goodsStore.setWgType(procard.getWgType());
		goodsStore.setGoodsStoreUnit(procard.getUnit());
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		goodsStore.setGoodsStoreTime(Util.getDateTime());
		goodsStore.setGoodsStoreAdminId(user.getId());
		goodsStore.setGoodsStoreAdminName(user.getName());
		goodsStore.setGoodsStoreCharger("");
		goodsStore.setNeiorderId(procard.getOrderNumber());
		goodsStore.setWaiorderId(procard.getOutOrderNum());
		goodsStore.setOrderId(procard.getOrderId());
		goodsStore.setInputSource("设变报废");
		goodsStore.setProcessNo(xc.getProcessNo());
		goodsStore.setStatus("入库");
		goodsStore.setStyle("设变在制品入库");
		goodsStore.setPrintStatus("NO");
		goodsStore.setYwmarkId(procard.getYwMarkId());
		Goods g = new Goods();
		g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
		g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
		g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
		g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());// 数量
		g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
		g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
		g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
		g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
		g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
		g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
		g.setGoodsPrice(goodsStore.getHsPrice().floatValue());// 含税价格
		g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应
		g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
		g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 支数
		g.setGoodsFormat(goodsStore.getGoodsStoreFormat());
		g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
		g.setGoodsUnit(goodsStore.getGoodsStoreUnit());
		g.setWgType(goodsStore.getWgType());// 物料类别
		g.setKgliao(goodsStore.getKgliao());// 供料属性
		g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本号
		g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 转换数量
		g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
		g.setGoodsStyle("设变在制品入库");
		g.setProcessNo(xc.getProcessNo());
		g.setProcessName(xc.getProcessName());
		totalDao.save(goodsStore);
		totalDao.save(g);
		if (xc.getRealBfclCount() == null) {
			xc.setRealBfclCount(goodsStore.getGoodsStoreCount());
		} else {
			xc.setRealBfclCount(xc.getRealBfclCount()
					+ goodsStore.getGoodsStoreCount());
		}
		if (xc.getRealBfclCount().equals(xc.getBfclCount())) {
			Float tkCount = xc.getRealTkclcount() == null ? 0 : xc
					.getRealTkclcount();
			if (xc.getTkclcount().equals(tkCount)) {
				xc.setClStatus("关闭");
				totalDao.update(xc);
				ProcardSbWaster psbw = xc.getProcardSbWaster();
				Float wwcCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardSbWasterxc where procardSbWaster.id=? and clStatus!='关闭'",
								psbw.getId());
				if (wwcCount == null || wwcCount == 0) {
					psbw.setStatus("关闭");
					totalDao.update(psbw);
				}
			} else {
				totalDao.update(xc);
			}
		}
		return "true";
	}

	@Override
	public String sbtk(Integer id, GoodsStore goodsStore) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		ProcardSbWasterxc xc = (ProcardSbWasterxc) totalDao.getObjectById(
				ProcardSbWasterxc.class, id);
		if (xc == null) {
			return "没有找到目标设变报废申请记录!";
		}
		if (goodsStore.getGoodsStoreCount() > xc.getBfclCount()) {
			return "对不起数量超额!";
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, xc
				.getProcardId());
		goodsStore.setGoodsStoreMarkId(xc.getMarkId());
		goodsStore.setBanBenNumber(procard.getBanBenNumber());
		goodsStore.setKgliao(procard.getKgliao());
		String before = "sbkt" + Util.getDateTime("yyyy-MM-dd") + "_";
		String maxNumber = (String) totalDao
				.getObjectByCondition("select max(storeLot) from GoodsStore where storeLot like '"
						+ before + "%'");
		Integer maxNo = 0;
		if (maxNumber != null) {
			String wwnum = maxNumber.substring(15, maxNumber.length());
			try {
				Integer maxNum = Integer.parseInt(wwnum);
				if (maxNum > maxNo) {
					maxNo = maxNum;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		maxNo++;
		String storeLot = before + String.format("%04d", maxNo);
		goodsStore.setGoodsStoreLot(storeLot);
		goodsStore.setGoodsStoreGoodsName(procard.getProName());
		goodsStore.setGoodsStoreFormat(procard.getSpecification());
		goodsStore.setWgType(procard.getWgType());
		goodsStore.setGoodsStoreUnit(procard.getUnit());
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		goodsStore.setGoodsStoreTime(Util.getDateTime());
		goodsStore.setGoodsStoreAdminId(user.getId());
		goodsStore.setGoodsStoreAdminName(user.getName());
		goodsStore.setGoodsStoreCharger("");
		goodsStore.setNeiorderId(procard.getOrderNumber());
		goodsStore.setWaiorderId(procard.getOutOrderNum());
		goodsStore.setOrderId(procard.getOrderId());
		goodsStore.setInputSource("设变退库");
		goodsStore.setProcessNo(xc.getProcessNo());
		goodsStore.setStatus("入库");
		goodsStore.setStyle("设变退库");
		goodsStore.setPrintStatus("NO");
		goodsStore.setYwmarkId(procard.getYwMarkId());
		Goods g = new Goods();
		g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
		g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
		g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
		g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());// 数量
		g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
		g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
		g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
		g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
		g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
		g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
		g.setGoodsPrice(goodsStore.getHsPrice().floatValue());// 含税价格
		g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应
		g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
		g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 支数
		g.setGoodsFormat(goodsStore.getGoodsStoreFormat());
		g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
		g.setGoodsUnit(goodsStore.getGoodsStoreUnit());
		g.setWgType(goodsStore.getWgType());// 物料类别
		g.setKgliao(goodsStore.getKgliao());// 供料属性
		g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本号
		g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 转换数量
		g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
		g.setGoodsStyle("设变退库");
		g.setProcessNo(xc.getProcessNo());
		g.setProcessName(xc.getProcessName());
		totalDao.save(goodsStore);
		totalDao.save(g);
		if (xc.getRealTkclcount() == null) {
			xc.setRealTkclcount(goodsStore.getGoodsStoreCount());
		} else {
			xc.setRealTkclcount(xc.getRealTkclcount()
					+ goodsStore.getGoodsStoreCount());
		}
		if (xc.getRealBfclCount().equals(xc.getBfclCount())) {
			Float bfCount = xc.getRealBfclCount() == null ? 0 : xc
					.getRealBfclCount();
			if (xc.getRealBfclCount().equals(bfCount)) {
				xc.setClStatus("关闭");
				totalDao.update(xc);
				ProcardSbWaster psbw = xc.getProcardSbWaster();
				Float wwcCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardSbWasterxc where procardSbWaster.id=? and clStatus!='关闭'",
								psbw.getId());
				if (wwcCount == null || wwcCount == 0) {
					psbw.setStatus("关闭");
					totalDao.update(psbw);
				}
			} else {
				totalDao.update(xc);
			}
		}
		return "true";
	}

	// 存储入库并添加入库记录
	@Override
	public String storageProducts(GoodsStore goodsStore, OaAppDetail oa) {
		OaAppDetail oaAppDetail = (OaAppDetail) totalDao.getObjectById(
				OaAppDetail.class, oa.getId());
		// float qichuCount = goodsStore.getBeginning_num();
		if (goodsStore.getGoodsStoreCount() > oaAppDetail.getDetailCount()
				- oaAppDetail.getRgdetailCount()) {
			return "物料编码为 ：" + oaAppDetail.getWlcode() + "的物品入库数量大于申请数量,入库失败";
		}
		// 检查库存数据是否和上次录入的数据相同

		// 获得库存表和入库表的批次最大值
		String dateTime = Util.getDateTime("yyyyMM");
		String maxGoodsStore = (String) totalDao
				.getObjectByCondition("select max(goodsStoreLot) from GoodsStore where goodsStoreLot like '"
						+ dateTime + "%'");
		String saveGoodsStoreValue = null;
		if (null == maxGoodsStore) {
			saveGoodsStoreValue = dateTime + "00001";
		} else {
			String t1 = maxGoodsStore.substring(6);
			Integer t2 = Integer.parseInt(t1);
			t2 += 1;
			String t3 = String.valueOf(t2);
			int length = t1.length() - t3.length();
			String t4 = "00000";
			String t5 = t4.substring(0, length);
			System.out.println("PD-P0-" + t5 + t3);
			saveGoodsStoreValue = dateTime + t5 + t3;
		}
		// 判断库存中是否已经入库了这个申请记录
		String goodsStoreExists_hql = "from GoodsStore where oaAppDetailId=?";
		// GoodsStore store = (GoodsStore)
		// totalDao.getObjectByCondition(goodsExists_hql,
		// oaAppDetail.getWlcode());
		// GoodsStore store = (GoodsStore)
		// totalDao.getObjectByCondition(goodsStoreExists_hql,
		// oadetail.getWlcode());
		List<GoodsStore> storeList = totalDao.query(goodsStoreExists_hql,
				oaAppDetail.getId());
		if (null != storeList && storeList.size() > 0) {
			GoodsStore store = storeList.get(0);

			if (store != null) {
				if (null != store.getGoodsStoreZhishu()) { // 历史转换了》数量
					if (null == goodsStore.getGoodsStoreZhishu()) {
						return "物料编码为 ：" + oaAppDetail.getWlcode() + "的物品需要转换";
					}
					if ((float) goodsStore.getGoodsStoreZhishu() != (float) store
							.getGoodsStoreZhishu()) {
						return "物料编码为 ：" + oaAppDetail.getWlcode()
								+ "的物品上次转换数量(" + store.getGoodsStoreZhishu()
								+ ")和本次不一样，入库失败";
					}
					if (!goodsStore.getGoodsStoreZHUnit().equals(
							store.getGoodsStoreZHUnit())) {
						return "物料编码为 ：" + oaAppDetail.getWlcode()
								+ "的物品上次转换单位(" + store.getGoodsStoreZHUnit()
								+ ")和本次不一样，入库失败";
					}
				}
				if (null == goodsStore.getHsPrice()) {
					// 不含税
					if (null == goodsStore.getGoodsStorePrice()) {
						return "物料编码为 ：" + oaAppDetail.getWlcode()
								+ "的物品上次是含税的";
					}
					/*
					 * if(goodsStore.getGoodsStorePrice()>store.getGoodsStorePrice
					 * ()){ return
					 * "物料编码为 ："+oaAppDetail.getWlcode()+"的物品不含税价格大于历史价格"; }
					 */
				} else {
					/*
					 * if(goodsStore.getHsPrice()>store.getHsPrice()){ return
					 * "物料编码为 ："+oaAppDetail.getWlcode()+"的物品价格价格大于历史价格"; }
					 */
				}
			}
		}

		// 不含税
		if (null == goodsStore.getTaxprice() || goodsStore.getTaxprice() == 0.0) {
			goodsStore.setGoodsStorePrice(null);
		}
		Users user = Util.getLoginUser();
		goodsStore.setSqUsersName(user.getName()); // 入库申请人
		goodsStore.setSqUsrsId(user.getId());
		goodsStore.setSqUsersdept(user.getDept());
		goodsStore.setSqUsersCode(user.getCode());
		goodsStore.setApplyTime(Util.getDateTime());// 申请入库时间
		goodsStore.setGoodsStoreTime(Util.getDateTime());// 时间
		goodsStore.setGoodsStoreLot(saveGoodsStoreValue);
		// goodsStore.setGoodsStoreNumber(oa.getId())//申请单编号
		goodsStore.setGoodsStoreNumber(oaAppDetail.getPrepareApply()
				.getAppOrdnumber());
		goodsStore.setGoodsStorePerson(user.getName());// 负责人
		goodsStore.setStatus("入库");
		goodsStore.setOaAppDetailId(oaAppDetail.getId()); // 申请单明细
		if (null == goodsStore.getHsPrice()) {// 不含税
			goodsStore.setMoney(goodsStore.getGoodsStoreCount()
					* goodsStore.getGoodsStorePrice());
		} else {
			goodsStore.setMoney(goodsStore.getGoodsStoreCount()
					* goodsStore.getHsPrice());
		}
		// goodsStore.setMoney(goodsStore.getGoodsStoreCount()*goodsStore)
		if (totalDao.save(goodsStore)) {

			// 判断库存中是否已经入库了这个申请记录
			String goodsExists_hql = "from Goods where goodsMarkId = ? and goodsCurQuantity = ?";
			Goods existsGoods = (Goods) totalDao.getObjectByCondition(
					goodsExists_hql, oaAppDetail.getWlcode(), oaAppDetail
							.getRgdetailCount());// 判断条件为件号和入柜数量相同的
			if (null != existsGoods) { // 库存已经存在
				// goods.setBanBenNumber(banBenNumber)
				existsGoods.setGoodsCurQuantity(existsGoods
						.getGoodsCurQuantity()
						+ goodsStore.getGoodsStoreCount());// 期初入库数量
				existsGoods.setGoodsBeginQuantity(existsGoods
						.getGoodsBeginQuantity()
						+ goodsStore.getGoodsStoreCount());
				existsGoods
						.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());
				if (totalDao.update(existsGoods)) {
					// oaAppDetail.setDetailCount(oaAppDetail.getDetailCount()-qichuCount);
					// //更新申请
					oaAppDetail.setRgdetailCount(oaAppDetail.getRgdetailCount()
							+ goodsStore.getGoodsStoreCount());// 更新入柜数量
					if ((float) oaAppDetail.getDetailCount() == (float) oaAppDetail
							.getRgdetailCount()) {
						oaAppDetail.setStatus("已入完");
						oaAppDetail.setDetailStatus("入库");
					}
					totalDao.update(oaAppDetail);
					return "入库成功！";
				}
				// return "物料编码为 ："+oadetail.getWlcode()+"的物品已经存在，请勿重复添加,谢谢！";
			} else {
				// 第一次入库
				String maxGoodsValue = (String) totalDao
						.getObjectByCondition("select max(goodsLotId) from Goods where goodsLotId like '"
								+ dateTime + "%'");
				String saveGoodsValue = null;
				if (null == maxGoodsValue) {
					saveGoodsValue = dateTime + "00001";
				} else {
					// saveValue = maxValue.substring(8, maxValue.length());
					String t1 = maxGoodsValue.substring(6);
					Integer t2 = Integer.parseInt(t1);
					t2 += 1;
					String t3 = String.valueOf(t2);
					int length = t1.length() - t3.length();
					String t4 = "00000";
					String t5 = t4.substring(0, length);
					System.out.println("PD-P0-" + t5 + t3);
					saveGoodsValue = dateTime + t5 + t3;
				}

				Goods goods = new Goods();
				goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
				goods.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());
				goods.setWgType(goodsStore.getWgType());
				goods.setGoodsFormat(goodsStore.getGoodsStoreFormat());
				goods.setGoodsClass(goodsStore.getGoodsStoreWarehouse());
				goods.setGoodHouseName(goodsStore.getGoodHouseName());
				goods.setGoodsPosition(goodsStore.getGoodsStorePosition());
				goods.setGoodsround(goodsStore.getGoodsStoreround());
				goods.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());
				goods.setGoodsPrice(goodsStore.getHsPrice().floatValue());
				if (null == goodsStore.getHsPrice()) {
					goods.setGoodsPrice(goodsStore.getGoodsStoreCount());// 不含税
				}
				goods.setGoodsZhishu(goodsStore.getGoodsStoreZhishu()); // 转换数量
				goods.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
				goods.setLendNeckStatus(goodsStore.getStyle());// 入库类别、、领用、、可借用
				// goods.setLendNeckCount(goodsStore.getBaoxiao_status());//可借可领数量

				goods.setGoodsChangeTime(Util.getDateTime());
				goods.setInputSource("手动入库");
				goods.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
				goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
				goods.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());
				goods.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());

				/*
				 * //转换 if(null==goodsStore.getGoodsStoreZhishu()){ //没有转换
				 * }else{
				 * goods.setGoodsCurQuantity(goodsStore.getGoodsStoreZhishu());
				 * goods
				 * .setGoodsBeginQuantity(goodsStore.getGoodsStoreZhishu());
				 * goods.setGoodsUnit(goodsStore.getGoodsStoreZHUnit()); }
				 */

				goods.setGoodsLotId(saveGoodsValue);
				// goods.setGoodsCurQuantity(qichuCount);//期初入库数量
				// goods.setGoodsBeginQuantity(qichuCount);
				goods.setLendNeckStatus(goodsStore.getStyle());
				boolean goodsSave = totalDao.save(goods);
				if (goodsSave) {
					// oaAppDetail.setDetailCount(oaAppDetail.getDetailCount()-qichuCount);
					// //更新申请
					oaAppDetail.setRgdetailCount(goodsStore
							.getGoodsStoreCount());
					if ((float) oaAppDetail.getDetailCount() == (float) goodsStore
							.getGoodsStoreCount()) {
						oaAppDetail.setStatus("已入完");
						oaAppDetail.setDetailStatus("入库");
					}
					totalDao.update(oaAppDetail);
					return "入库成功！";
				}
			}
		}
		return null;
	}

	@Override
	public String goodshuizong(String months) {
		// 查询记账日;大于记账日自动记为下一月;
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		String months_last = Util.getLastMonth(months);
		List<GoodsSummary> goodsSumList = totalDao.query(
				" from GoodsSummary where months = ? and qimoNum >0 ",
				months_last);
		if (goodsSumList != null && goodsSumList.size() > 0) {
			for (GoodsSummary gss : goodsSumList) {
				GoodsSummary gs = new GoodsSummary();
				gs.setMarkId(gss.getMarkId());
				gs.setGoodsClass(gss.getGoodsClass());
				gs.setMonths(months);
				gs.setName(gss.getName());
				gs.setUnit(gss.getUnit());
				gs.setQichuNum(gss.getQichuNum());
				gs.setQichuPrice(gss.getQimoPrice());
				gs.setQichuMoney(gss.getQimoMoney());
				totalDao.save(gs);
			}
		}
		if (accountsDate != null) {
			Integer jihao = accountsDate.getJihao();
			// 查询2017年至今的入库记录
			List<GoodsStore> gsList = totalDao
					.query(" from GoodsStore where   goodsStoreWarehouse in ('外购件库')  and goodsStoreDate like '"
							+ months
							+ "%'    order by goodsStoreMarkId ,goodsStoreDate ");
			for (GoodsStore gs : gsList) {
				Integer riqi = Integer.parseInt(gs.getGoodsStoreDate()
						.substring(8));
				Integer months1 = Integer.parseInt(gs.getGoodsStoreDate()
						.substring(5, 7));
				String jzmonths = gs.getGoodsStoreDate().substring(0, 7);
				if (riqi > jihao) {
					jzmonths = gs.getGoodsStoreDate().substring(0, 5)
							+ ((months1 + 101) + "").substring(1);
				}
				GoodsSummary goodsSum = (GoodsSummary) totalDao
						.getObjectByCondition(
								" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
								jzmonths, gs.getGoodsStoreMarkId(), gs
										.getGoodsStoreWarehouse());
				String lastmonths = Util.getLastMonth(jzmonths);
				if (goodsSum == null) {
					goodsSum = new GoodsSummary();
					goodsSum.setMarkId(gs.getGoodsStoreMarkId());
					goodsSum.setGoodsClass(gs.getGoodsStoreWarehouse());
					goodsSum.setMonths(jzmonths);
					goodsSum.setName(gs.getGoodsStoreGoodsName());
					goodsSum.setUnit(gs.getGoodsStoreUnit());
					GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
							.getObjectByCondition(
									" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
									lastmonths, gs.getGoodsStoreMarkId(), gs
											.getGoodsStoreWarehouse());
					if (lastgoodsSum != null) {// 期初
						goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
						goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
						goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
					}
					// 本月入库
					goodsSum.setRukuNum(gs.getGoodsStoreCount().doubleValue());
					goodsSum.setRukuPrice(gs.getGoodsStorePrice() == null ? 0d
							: gs.getGoodsStorePrice().doubleValue());
					goodsSum.setRukuMoney(gs.getGoodsStoreCount()
							* goodsSum.getRukuPrice());
					// /期末库存
					goodsSum.setQimoNum(gs.getGoodsStoreCount().doubleValue());
					goodsSum.setQimoPrice(gs.getGoodsStorePrice() == null ? 0d
							: gs.getGoodsStorePrice().doubleValue());
					goodsSum.setQimoMoney(gs.getGoodsStoreCount()
							* goodsSum.getQimoPrice());
					totalDao.save(goodsSum);
				} else {
					// 本月入库
					goodsSum.setRukuNum((goodsSum.getRukuNum() == null ? 0
							: goodsSum.getRukuNum())
							+ gs.getGoodsStoreCount().doubleValue());
					goodsSum.setRukuPrice(gs.getGoodsStorePrice() == null ? 0d
							: gs.getGoodsStorePrice().doubleValue());
					goodsSum.setRukuMoney(goodsSum.getRukuNum()
							* goodsSum.getRukuPrice());
					// /期末库存
					goodsSum.setQimoNum(goodsSum.getRukuNum()
							+ gs.getGoodsStoreCount().doubleValue());
					goodsSum.setQimoPrice(gs.getGoodsStorePrice() == null ? 0d
							: gs.getGoodsStorePrice().doubleValue());
					goodsSum.setQimoMoney(goodsSum.getQimoNum()
							* goodsSum.getQimoPrice());
					totalDao.update(goodsSum);
				}

			}
			// 查询2017年至今的出库记录
			List<Sell> sellList = totalDao
					.query(" from Sell where sellDate  like '"
							+ months
							+ "%'  and sellWarehouse in ('外购件库')  order by sellMarkId ,sellDate ");
			for (Sell sell : sellList) {
				Integer riqi = Integer
						.parseInt(sell.getSellDate().substring(8));
				Integer months1 = Integer.parseInt(sell.getSellDate()
						.substring(5, 7));
				String jzmonths = sell.getSellDate().substring(0, 7);
				if (riqi > jihao) {
					jzmonths = sell.getSellDate().substring(0, 5)
							+ ((months1 + 101) + "").substring(1);
				}
				GoodsSummary goodsSum = (GoodsSummary) totalDao
						.getObjectByCondition(
								" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
								jzmonths, sell.getSellMarkId(), sell
										.getSellWarehouse());
				String lastmonths = Util.getLastMonth(jzmonths);
				if (goodsSum == null) {
					goodsSum = new GoodsSummary();
					goodsSum.setMarkId(sell.getSellMarkId());
					goodsSum.setGoodsClass(sell.getSellWarehouse());
					goodsSum.setMonths(jzmonths);
					goodsSum.setName(sell.getSellGoods());
					goodsSum.setUnit(sell.getSellUnit());
					GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
							.getObjectByCondition(
									" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
									lastmonths, sell.getSellMarkId(), sell
											.getSellWarehouse());
					if (lastgoodsSum != null) {// 期初
						goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
						goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
						goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
					} else {
						goodsSum.setQichuNum(0d);
						goodsSum.setQichuPrice(0d);
						goodsSum.setQichuMoney(0d);
					}
					/**
					 * 本月出库
					 */
					goodsSum.setChukuNum(sell.getSellCount().doubleValue());
					goodsSum.setChukuPrice(sell.getSellPrice() == null ? 0
							: sell.getSellPrice().doubleValue());
					goodsSum.setChukuMoney(sell.getSellCount()
							* goodsSum.getChukuPrice());
					// /期末库存
					goodsSum.setQimoNum((goodsSum.getQichuNum() == null ? 0
							: goodsSum.getQichuNum())
							- goodsSum.getChukuNum());
					goodsSum.setQimoPrice(goodsSum.getQichuPrice() == null ? 0
							: goodsSum.getQichuPrice());
					goodsSum.setQimoMoney(goodsSum.getQimoNum()
							* goodsSum.getQimoPrice());
					totalDao.save(goodsSum);
				} else {
					GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
							.getObjectByCondition(
									" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
									lastmonths, sell.getSellMarkId(), sell
											.getSellWarehouse());
					if (lastgoodsSum != null) {// 期初
						goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
						goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
						goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
					} else {
						goodsSum.setQichuNum(0d);
						goodsSum.setQichuPrice(0d);
						goodsSum.setQichuMoney(0d);
					}
					/**
					 * 本月出库
					 */
					goodsSum.setChukuNum((goodsSum.getChukuNum() == null ? 0
							: goodsSum.getChukuNum())
							+ sell.getSellCount().doubleValue());
					goodsSum.setChukuPrice(sell.getSellPrice() == null ? 0
							: sell.getSellPrice().doubleValue());
					goodsSum.setChukuMoney(goodsSum.getChukuNum()
							* goodsSum.getChukuPrice());
					// /期末库存
					goodsSum.setQimoNum((goodsSum.getQichuNum() == null ? 0
							: goodsSum.getQichuNum())
							+ (goodsSum.getRukuNum() == null ? 0 : goodsSum
									.getRukuNum())
							- (goodsSum.getChukuNum() == null ? 0 : goodsSum
									.getChukuNum()));
					goodsSum.setQimoMoney(goodsSum.getQimoNum()
							* (goodsSum.getQimoPrice() == null ? 0 : goodsSum
									.getQimoPrice()));
					totalDao.update(goodsSum);
				}

			}

		}

		return null;
	}

	@Override
	public Object[] findGoodsSum(GoodsSummary goodsSum, Integer pageNo,
			Integer pagesize, String pagestatus) {
		if (goodsSum == null) {
			goodsSum = new GoodsSummary();
		}
		String hql = totalDao.criteriaQueries(goodsSum, null);
		List<GoodsSummary> goodsSumList = totalDao.findAllByPage(hql
				+ " order by markId,months", pageNo, pagesize);
		int count = totalDao.getCount(hql);

		return new Object[] { goodsSumList, count };
	}

	@Override
	public void test() {
		String[] monthsArray = { "2017-01", "2017-02", "2017-03", "2017-04",
				"2017-05", "2017-06", "2017-07", "2017-08", "2017-09",
				"2017-10", "2017-11", "2017-12" };
		for (int i = 0; i < monthsArray.length; i++) {
			goodshuizong(monthsArray[i]);
		}
	}

	@Override
	public String goodshuizong0(String status, GoodsStore gs, Sell sell) {
		return goodshuizong1(status, gs, sell);
	}

	private static String goodshuizong1(String status, GoodsStore gs, Sell sell) {
		TotalDao totalDao = createTotol();
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		Integer today = Integer.parseInt(Util.getDateTime("MM"));
		String months = Util.getDateTime("yyyy-MM");
		String lastMonths = Util.getLastMonth(months);
		if (today > accountsDate.getJihao()) {
			months = Util.getNextMonth(months);
			lastMonths = Util.getLastMonth(months);
			List<GoodsSummary> goodsSumList = totalDao.query(
					" from GoodsSummary where months = ? and qimoNum >0 ",
					lastMonths);
			if (goodsSumList != null && goodsSumList.size() > 0) {
				for (GoodsSummary gss : goodsSumList) {
					GoodsSummary gs1 = (GoodsSummary) totalDao
							.getObjectByCondition(
									" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
									months, gss.getMarkId(), gss
											.getGoodsClass());
					if (gs1 == null) {
						gs1 = new GoodsSummary();
						gs1.setMarkId(gss.getMarkId());
						gs1.setGoodsClass(gss.getGoodsClass());
						gs1.setMonths(months);
						gs1.setName(gss.getName());
						gs1.setUnit(gss.getUnit());
						gs1.setQichuNum(gss.getQichuNum());
						gs1.setQichuPrice(gss.getQimoPrice());
						gs1.setQichuMoney(gss.getQimoMoney());
						totalDao.save(gs1);
					}
				}
			}
		}
		if ("入库".equals(status) && gs != null) {
			GoodsSummary goodsSum = (GoodsSummary) totalDao
					.getObjectByCondition(
							" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
							months, gs.getGoodsStoreMarkId(), gs
									.getGoodsStoreWarehouse());
			if (goodsSum == null) {
				goodsSum = new GoodsSummary();
				goodsSum.setMarkId(gs.getGoodsStoreMarkId());
				goodsSum.setGoodsClass(gs.getGoodsStoreWarehouse());
				goodsSum.setMonths(months);
				goodsSum.setName(gs.getGoodsStoreGoodsName());
				goodsSum.setUnit(gs.getGoodsStoreUnit());
				GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
						.getObjectByCondition(
								" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
								lastMonths, gs.getGoodsStoreMarkId(), gs
										.getGoodsStoreWarehouse());
				if (lastgoodsSum != null) {// 期初
					goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
					goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
					goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
				}
				// 本月入库
				goodsSum.setRukuNum(gs.getGoodsStoreCount().doubleValue());
				goodsSum.setRukuPrice(gs.getGoodsStorePrice() == null ? 0d : gs
						.getGoodsStorePrice().doubleValue());
				goodsSum.setRukuMoney(gs.getGoodsStoreCount()
						* goodsSum.getRukuPrice());
				// /期末库存
				goodsSum.setQimoNum(gs.getGoodsStoreCount().doubleValue());
				goodsSum.setQimoPrice(gs.getGoodsStorePrice() == null ? 0d : gs
						.getGoodsStorePrice().doubleValue());
				goodsSum.setQimoMoney(gs.getGoodsStoreCount()
						* goodsSum.getQimoPrice());
				totalDao.save(goodsSum);
			} else {
				// 本月入库
				goodsSum.setRukuNum((goodsSum.getRukuNum() == null ? 0
						: goodsSum.getRukuNum())
						+ gs.getGoodsStoreCount().doubleValue());
				goodsSum.setRukuPrice(gs.getGoodsStorePrice() == null ? 0d : gs
						.getGoodsStorePrice().doubleValue());
				goodsSum.setRukuMoney(goodsSum.getRukuNum()
						* goodsSum.getRukuPrice());
				// /期末库存
				goodsSum.setQimoNum(goodsSum.getRukuNum()
						+ gs.getGoodsStoreCount().doubleValue());
				goodsSum.setQimoPrice(gs.getGoodsStorePrice() == null ? 0d : gs
						.getGoodsStorePrice().doubleValue());
				goodsSum.setQimoMoney(goodsSum.getQimoNum()
						* goodsSum.getQimoPrice());
				totalDao.update(goodsSum);
			}
		} else if ("出库".equals(status) && sell != null) {
			GoodsSummary goodsSum = (GoodsSummary) totalDao
					.getObjectByCondition(
							" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
							months, sell.getSellMarkId(), sell
									.getSellWarehouse());
			if (goodsSum == null) {
				goodsSum = new GoodsSummary();
				goodsSum.setMarkId(sell.getSellMarkId());
				goodsSum.setGoodsClass(sell.getSellWarehouse());
				goodsSum.setMonths(months);
				goodsSum.setName(sell.getSellGoods());
				goodsSum.setUnit(sell.getSellUnit());
				GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
						.getObjectByCondition(
								" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
								lastMonths, sell.getSellMarkId(), sell
										.getSellWarehouse());
				if (lastgoodsSum != null) {// 期初
					goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
					goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
					goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
				} else {
					goodsSum.setQichuNum(0d);
					goodsSum.setQichuPrice(0d);
					goodsSum.setQichuMoney(0d);
				}
				/**
				 * 本月出库
				 */
				goodsSum.setChukuNum(sell.getSellCount().doubleValue());
				goodsSum.setChukuPrice(sell.getSellPrice() == null ? 0 : sell
						.getSellPrice().doubleValue());
				goodsSum.setChukuMoney(sell.getSellCount()
						* goodsSum.getChukuPrice());
				// /期末库存
				goodsSum.setQimoNum((goodsSum.getQichuNum() == null ? 0
						: goodsSum.getQichuNum())
						- goodsSum.getChukuNum());
				goodsSum.setQimoPrice(goodsSum.getQichuPrice() == null ? 0
						: goodsSum.getQichuPrice());
				goodsSum.setQimoMoney(goodsSum.getQimoNum()
						* goodsSum.getQimoPrice());
				totalDao.save(goodsSum);
			} else {
				GoodsSummary lastgoodsSum = (GoodsSummary) totalDao
						.getObjectByCondition(
								" from GoodsSummary where months = ? and markId = ? and goodsClass = ? ",
								lastMonths, sell.getSellMarkId(), sell
										.getSellWarehouse());
				if (lastgoodsSum != null) {// 期初
					goodsSum.setQichuNum(lastgoodsSum.getQimoNum());
					goodsSum.setQichuPrice(lastgoodsSum.getQimoPrice());
					goodsSum.setQichuMoney(lastgoodsSum.getQimoMoney());
				} else {
					goodsSum.setQichuNum(0d);
					goodsSum.setQichuPrice(0d);
					goodsSum.setQichuMoney(0d);
				}
				/**
				 * 本月出库
				 */
				goodsSum.setChukuNum((goodsSum.getChukuNum() == null ? 0
						: goodsSum.getChukuNum())
						+ sell.getSellCount().doubleValue());
				goodsSum.setChukuPrice(sell.getSellPrice() == null ? 0 : sell
						.getSellPrice().doubleValue());
				goodsSum.setChukuMoney(goodsSum.getChukuNum()
						* goodsSum.getChukuPrice());
				// /期末库存
				goodsSum.setQimoNum((goodsSum.getQichuNum() == null ? 0
						: goodsSum.getQichuNum())
						+ (goodsSum.getRukuNum() == null ? 0 : goodsSum
								.getRukuNum())
						- (goodsSum.getChukuNum() == null ? 0 : goodsSum
								.getChukuNum()));
				goodsSum.setQimoMoney(goodsSum.getQimoNum()
						* (goodsSum.getQimoPrice() == null ? 0 : goodsSum
								.getQimoPrice()));
				totalDao.update(goodsSum);
			}
		}
		return null;
	}

	@Override
	public List<GoodsStore> findSameProductPrice(Integer id, String tag) {
		if ("oaApp".equals(tag)) {
			String hql_goods = " from GoodsStore where goodsStoreGoodsName=?"
					+ " and goodsStoreMarkId = ?  and goodsStoreDate>'2011-07-01' ";
			OaAppDetail oaAppDetail = (OaAppDetail) totalDao.getObjectById(
					OaAppDetail.class, id);
			/*
			 * String hql =
			 * "select matetag,format,unit,storageTaxPrice from Storage where matetag=? and format=? and unit=? and storageTaxPrice>0 and number!='"
			 * + oaAppDetail.getDetailSeqNum() +
			 * "' and date>'2011-07-01' group by matetag,format,unit,storageTaxPrice"
			 * ;
			 */
			return totalDao.query(hql_goods, oaAppDetail.getDetailAppName(),
					oaAppDetail.getWlcode());
		}

		return null;
	}

	@Override
	public String saveBothSgrk(List<GoodsStore> listStore) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		String ckqx =null;
		for (GoodsStore goodsStore : listStore) {
			Goods g = new Goods();
			goodsStore.setGoodsStorePlanner(user.getName());// 入库入
			String nexttime = getNexttime(goodsStore);
			g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
			g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本
			g.setKgliao(goodsStore.getKgliao());// 供料属性
			g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 库别
			if(ckqx==null){
				ckqx=checkquanxian(user, goodsStore.getGoodsStoreWarehouse(), "in", totalDao);
				if(!ckqx.equals("true")){
					return ckqx;
				}
			}
			g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
			g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
			g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
			g.setWgType(goodsStore.getWgType());// 物料类别
			g.setSuodingdanhao(goodsStore.getSuodingdanhao());

			g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
			g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 规格
			g.setGoodsStyle(goodsStore.getStyle());// 入库类型（/返修入库/退货入库/批产/试制/中间件）
			g.setInputSource(goodsStore.getInputSource());// 入库来源
			if ("不合格品库".equals(goodsStore.getGoodsStoreWarehouse())
					|| "待检库".equals(goodsStore.getGoodsStoreWarehouse())) {
				g.setBout(false);
				g.setBout(false);
			}
			// 查询所对应的的零件
			String hql_procard = "from Procard where markId = ? and selfCard = ?";
			Procard procard = (Procard) totalDao.getObjectByCondition(
					hql_procard, goodsStore.getGoodsStoreMarkId(), goodsStore
							.getGoodsStoreLot());
			if (procard != null) {
				procard.setDefaultKuwei(goodsStore.getGoodsStorePosition());// 存放默认库位
				procard.setDefaultCangqu(goodsStore.getGoodHouseName());// 存放默认仓区
				totalDao.save(procard);
			}
			String hql_yw = "from YuanclAndWaigj where markId=? and  bili>0 and (banbenStatus is null or banbenStatus !='历史')";
			YuanclAndWaigj yw = (YuanclAndWaigj) totalDao.getObjectByCondition(
					hql_yw, goodsStore.getGoodsStoreMarkId());
			if (yw != null
					&& (goodsStore.getGoodsStoreZhishu() == null || goodsStore
							.getGoodsStoreZhishu() <= 0)) {
				Float yushu = goodsStore.getGoodsStoreCount() % yw.getBili();
				Double zhishu = 0d;
				if (yushu <= 0.05) {
					zhishu = Math.floor(goodsStore.getGoodsStoreCount()
							/ yw.getBili());
				} else {
					zhishu = Math.ceil(goodsStore.getGoodsStoreCount()
							/ yw.getBili());
				}
				goodsStore.setGoodsStoreZhishu(zhishu.floatValue());
			}
			Goods s = null;
			// 累加库存依据：外购件库/占用库/在途库： 件号+版本+供料属性+库别+仓区+库位+批次
			// 累加库存依据：成品库库： 件号+版本+库别+仓区+库位+批次
			String kgSql = "";
			// 供料属性
			if (goodsStore.getGoodsStoreWarehouse().equals("外购件库")
					|| goodsStore.getGoodsStoreWarehouse().equals("占用库")
					|| goodsStore.getGoodsStoreWarehouse().equals("在途库")) {
				kgSql += " and kgliao='" + goodsStore.getKgliao() + "'";
			}
			// 版本
			String banben_hql = "";
			if (g.getBanBenNumber() != null && g.getBanBenNumber().length() > 0) {
				banben_hql = " and banBenNumber='" + g.getBanBenNumber() + "'";
			}
			// 仓区
			String houseName_hql = "";
			if (g.getGoodHouseName() != null
					&& g.getGoodHouseName().length() > 0) {
				houseName_hql = " and goodHouseName='" + g.getGoodHouseName()
						+ "'";
			}
			// 库位
			String position_hql = "";
			if (g.getGoodsPosition() != null
					&& g.getGoodsPosition().length() > 0) {
				position_hql = " and goodsPosition='" + g.getGoodsPosition()
						+ "'";
			}
			// 批次
			String goodsLotId_hql = "";
			if (g.getGoodsLotId() != null && g.getGoodsLotId().length() > 0) {
				goodsLotId_hql = " and goodsLotId='" + g.getGoodsLotId() + "'";
			}
			String sddhSql = "";
			if (g.getSuodingdanhao() == null
					|| g.getSuodingdanhao().length() == 0) {
				sddhSql = " and (suodingdanhao is null or suodingdanhao='')";
			} else {
				sddhSql = " and  suodingdanhao='" + g.getSuodingdanhao() + "'";
			}
			String hql = "from Goods where goodsMarkId = ? " + goodsLotId_hql
					+ banben_hql + kgSql + " and goodsClass=? " + houseName_hql
					+ sddhSql + position_hql
					+ " and (fcStatus is null or fcStatus='可用')";
			s = (Goods) totalDao.getObjectByCondition(hql, new Object[] {
					g.getGoodsMarkId(), g.getGoodsClass() });
			if (s == null) {
				g.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
				if (goodsStore.getBeginning_num() != null) {
					g.setGoodsBeginQuantity(goodsStore.getBeginning_num());// 起初数量
					g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount()
							+ goodsStore.getBeginning_num());// 数量
				} else {
					g.setGoodsBeginQuantity(0F);// 起初数量
					g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount() + 0F);// 数量
				}

				g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
				g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
				g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
				g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
				if(goodsStore.getHsPrice()!=null){
					g.setGoodsPrice(goodsStore.getHsPrice().floatValue());// 价格(含税)
				}
				g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应.
				g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
				g.setGoodHouseName(goodsStore.getGoodHouseName());// 区名
				if (goodsStore.getGoodsStoreZhishu() != null) {
					g
							.setGoodsZhishu(goodsStore.getGoodsStoreZhishu() == 0 ? null
									: goodsStore.getGoodsStoreZhishu());// 转换数量数
				}
				g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
				g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 規格
				g.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());// 客户
				g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());
				g.setBanBenNumber(goodsStore.getBanBenNumber());
				g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间;
				g.setGoodsnexttime(nexttime);// 下次质检时间;
				g.setGoodsround(goodsStore.getGoodsStoreround());// 质检周期
				g.setTuhao(goodsStore.getTuhao());// 图号
				g.setNeiorderId(goodsStore.getNeiorderId());// 内部订单号
				g.setWaiorderId(goodsStore.getWaiorderId());// 外部订单号
				g.setOrder_Id(goodsStore.getOrder_Id());
				g.setSuodingdanhao(goodsStore.getSuodingdanhao());
				totalDao.save(g);
			} else {
				s.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
				s.setGoodsCurQuantity(s.getGoodsCurQuantity()
						+ goodsStore.getGoodsStoreCount());
				s.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位;
				s.setGoodsnexttime(nexttime);// 下次质检时间;
				g.setTuhao(goodsStore.getTuhao());// 图号
				if (goodsStore.getGoodsStoreZhishu() != null
						&& goodsStore.getGoodsStoreZhishu() > 0F) {
					s.setGoodsZhishu((s.getGoodsZhishu() == null ? 0 : s
							.getGoodsZhishu())
							+ goodsStore.getGoodsStoreZhishu());
				}

				totalDao.update(s);
			}
			// ProductManager
			goodsStore.setGoodsStorenexttime(nexttime);// 下次质检时间;
			g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间;
			goodsStore.setOrder_Id(g.getOrder_Id());
			totalDao.save(goodsStore);
			if (goodsStore.getOrder_Id() != null
					&& !"不合格品库".equals(goodsStore.getGoodsStoreWarehouse())) {
				String hql_pm = "from ProductManager where orderManager.id=? and pieceNumber = ?";
				ProductManager pm = (ProductManager) totalDao
						.getObjectByCondition(hql_pm, goodsStore.getOrder_Id(),
								goodsStore.getGoodsStoreMarkId());
				if (pm != null) {
					Float hasTurn = pm.getHasTurn();
					Float allocationsNum = pm.getAllocationsNum();
					Float totalCount = pm.getNum();
					if (totalCount == null || totalCount < 0) {
						totalCount = 0f;
					}
					if (hasTurn == null || hasTurn < 0) {
						hasTurn = 0f;
					}
					if (allocationsNum == null || allocationsNum < 0) {
						allocationsNum = 0f;
					}
					float count = goodsStore.getGoodsStoreCount();
					if (count > (totalCount.floatValue() - allocationsNum
							.floatValue())) {
						throw new RuntimeException("对不起此订单入库数量最大为:"
								+ (totalCount - allocationsNum));
					}
					pm.setHasTurn(hasTurn + count);
					pm.setAllocationsNum(allocationsNum + count);
					totalDao.update(pm);
					OrderManager o = (OrderManager) totalDao.get(
							OrderManager.class, goodsStore.getOrder_Id());
					if (o != null) {
						String hql5 = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
						List list = totalDao.query(hql5, goodsStore
								.getOrder_Id());
						if (list != null && list.size() > 0
								&& list.get(0) != null) {
							String inpuSql = "select sum(allocationsNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
							Float pronum = (Float) list.get(0);
							Float inputNum = (Float) totalDao
									.getObjectByCondition(inpuSql, goodsStore
											.getOrder_Id());
							if (inputNum == null) {
								inputNum = 0f;
							}

							if (pronum == 0 || pronum == null) {
								o.setInputRate(0F);
							} else {
								o.setInputRate(inputNum / pronum * 100);
							}
							if (o.getInputRate() > 0) {
								totalDao.update(o);
							}
						} else {
							o.setInputRate(0F);
						}
					}

				}
			}
		}
		return "true";

	}

	// 根据件号查询仓区和库位
	@Override
	public String[] findHouseNameByMarkId(String markId) {
		if (null != markId) {
			List<GoodsStore> list = totalDao
					.query(
							"from GoodsStore where goodsStoreMarkId =? and goodHouseName is not null"
									+ " and goodsStorePosition is not null order by goodsStoreId desc ",
							markId);
			if (null != list && list.size() > 0) {
				return new String[] { list.get(0).getGoodHouseName(),
						list.get(0).getGoodsStorePosition() };
			}
		}
		return null;
	}

	/**
	 * （待入库）外购件接收所有
	 */

	public Map<Integer, Object> waigouWaiting(GoodsStore goodsStore,
			int pageNo, int pageSize) {
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		String sql = "goodsStoreWarehouse in( '外购件库','备货库') and status ='待入库' and style='调仓入库'";
		String hql = totalDao.criteriaQueries(goodsStore, sql);
		hql += "order by id desc";
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	private Float[] getAccountsDate(GoodsStore goodsStore) {
		Float avghsprice = goodsStore.getHsPrice() == null?null:goodsStore.getHsPrice().floatValue() ;
		Float avgbhsprice =goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		// 查询成本核算方法
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		if (accountsDate != null) {
			String hql_banben = "";
			if (goodsStore.getBanBenNumber() != null
					&& goodsStore.getBanBenNumber().length() > 0) {
				hql_banben = " and banBenNumber='"
						+ goodsStore.getBanBenNumber() + "'";
			}
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben;

				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					avghsprice = Float.parseFloat(obj[0].toString());
					avgbhsprice = Float.parseFloat(obj[1].toString());
				}
			} else if ("monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount) ,sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben
						+ " and goodsStoreTime >'"
						+ Util.getDateTime("yyyy-MM")
						+ "-01' and goodsStoreTime <'"
						+ Util.getDateTime("yyyy-MM")
						+ "-"
						+ accountsDate.getJihao() + "' ";
				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					try {
						avghsprice = Float.parseFloat(obj[0].toString());
						avgbhsprice = Float.parseFloat(obj[1].toString());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		if (avghsprice == null) {
			avghsprice = goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
			avgbhsprice =goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		}
		return new Float[] { avghsprice, avgbhsprice };

	}

	/**
	 * 调仓申请（成品库） 修改库存数据->出库记录->入库记录
	 * 
	 * @param goods
	 * @return
	 */
	public String changeWareHouse(Goods goods, Sell sell_page) {
		if (goods != null) {
			Goods g_old = (Goods) totalDao.get(Goods.class, goods.getGoodsId());
			if ("成品库".equals(g_old.getGoodsClass())
					|| "备货库".equals(g_old.getGoodsClass())) {
				// 修改库存数据
				if (g_old.getGoodsCurQuantity() != null
						&& !"".equals(g_old.getGoodsCurQuantity())
						|| sell_page.getSellCount() != null
						&& !"".equals(sell_page.getSellCount())) {
					g_old.setGoodsCurQuantity(g_old.getGoodsCurQuantity()
							- sell_page.getSellCount());
					if (totalDao.update(g_old)) {
						Sell sell_new = new Sell();
						sell_new.setSellMarkId(g_old.getGoodsMarkId());
						sell_new.setSellFormat(g_old.getGoodsFormat());
						sell_new.setTuhao(g_old.getTuhao());
						sell_new.setBanBenNumber(g_old.getBanBenNumber());
						sell_new.setSellGoods(g_old.getGoodsFullName());
						sell_new.setSellLot(g_old.getGoodsLotId());
						sell_new.setSellCount(sell_page.getSellCount());
						sell_new.setSellProMarkId(g_old.getGoodsProMarkId());
						sell_new.setSellWarehouse(g_old.getGoodsClass());
						sell_new.setGoodHouseName(g_old.getGoodHouseName());
						sell_new.setKuwei(g_old.getGoodsPosition());
						sell_new.setKgliao(g_old.getKgliao());
						sell_new.setStyle("调仓出库");
						sell_new.setGoodsStoreZHUnit(g_old
								.getGoodsStoreZHUnit()); // 转换单位
						sell_new.setSellZhishu(g_old.getGoodsZhishu());// 转换数量
						sell_new.setSellCompanyPeople(sell_page
								.getSellCompanyPeople());
						sell_new.setSellDate(DateUtil.formatDate(new Date(),
								"yyyy-MM-dd"));
						sell_new.setSellTime(DateUtil.formatDate(new Date(),
								"yyyy-MM-dd HH:MM:SS"));
						sell_new.setSellUnit(g_old.getGoodsUnit());// 单位
						if (totalDao.save(sell_new)) {
							// 生成入库记录
							GoodsStore gs = new GoodsStore();
							gs.setGoodsStoreMarkId(sell_page.getSellMarkId());// 件号
							gs.setGoodsStoreFormat(sell_page.getSellFormat());// 规格
							gs.setTuhao(g_old.getTuhao());
							gs.setBanBenNumber(sell_page.getBanBenNumber());
							gs.setGoodsStoreGoodsName(g_old.getGoodsFullName());// 名称
							gs.setGoodsStoreLot(g_old.getGoodsLotId());// 批次
							gs.setGoodsStoreCount(sell_page.getSellCount());// 数量
							gs.setPrintStatus("No");
							gs
									.setGoodsStoreProMarkId(g_old
											.getGoodsProMarkId());// 总成件号
							gs.setGoodsStoreWarehouse(sell_page
									.getSellWarehouse());// 库别
							gs.setGoodHouseName(sell_page.getGoodHouseName());// 仓区
							gs.setGoodsStorePosition(sell_page.getKuwei());
							gs.setKgliao(sell_page.getKgliao());
							Users jingban = Util.getLoginUser();
							gs.setGoodsStoreCharger(jingban.getName());// 经办人
							gs.setStyle("调仓入库");// 入库类型
							gs.setStatus("待入库");
							gs.setGoodsStoreZHUnit(sell_page
									.getGoodsStoreZHUnit()); // 转换单位
							gs.setGoodsStoreZhishu(sell_page.getSellZhishu());// 转换数量
							gs.setGoodsStorePerson(sell_page
									.getSellCompanyPeople());
							gs.setGoodsStoreDate(DateUtil.formatDate(
									new Date(), "yyyy-MM-dd"));
							gs.setGoodsStoreUnit(sell_page.getSellUnit());// 单位
							gs.setMore("老件号：" + g_old.getGoodsMarkId()
									+ "/新件号 ：" + sell_page.getSellMarkId());
							gs.setSellId(sell_new.getSellId());// 出库记录ID
							if (totalDao.save(gs)) {
								GoodsSellRelational gsr = new GoodsSellRelational();
								gsr.setGoodsId_old(g_old.getGoodsId());
								gsr.setSellId(gs.getGoodsStoreId());
								gsr.setOut_Time(DateUtil.formatDate(new Date(),
										"yyyy-MM-dd HH:MM:SS"));
								if (totalDao.save(gsr)) {
									return "调仓成功，请联系仓管进行确认。";
								} else {
									return "调仓失败";
								}
							}
						} else {
							return "数据异常";
						}
					} else {
						return "数据异常";
					}
				} else {
					return "数据异常";
				}
			} else {
				return "非外购件库及备货库的库存无法操作";
			}
		} else {
			return "数据异常";
		}
		return "调仓失败";
	}

	public String waigouQueren(GoodsStore gs_page) {
		if (gs_page != null) {
			GoodsStore goodsStore = (GoodsStore) totalDao.get(GoodsStore.class,
					gs_page.getGoodsStoreId());
			if ("外购件库".equals(goodsStore.getGoodsStoreWarehouse())
					|| "备货库".equals(goodsStore.getGoodsStoreWarehouse())
					&& "待入库".equals(goodsStore.getStatus())) {
				goodsStore.setStatus("入库");
				if (totalDao.update(goodsStore)) {
					Goods g = new Goods();
					g.setGoodsFormat(goodsStore.getGoodsStoreFormat());
					g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
					g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本
					g.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
					g.setKgliao(goodsStore.getKgliao());// 供料属性
					g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 库别
					g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
					g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
					g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
					g.setWgType(goodsStore.getWgType());// 物料类别
					g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
					g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 规格
					g.setGoodsStyle(goodsStore.getStyle());// 入库类型
					g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());// 数量
					g.setGoodsChangeTime(DateUtil.formatDate(new Date(),
							"yyyy-MM-dd"));
					if (totalDao.save(g)) {
						GoodsSellRelational gsr = (GoodsSellRelational) totalDao
								.getObjectByCondition(
										"from GoodsSellRelational where sellId = ?",
										goodsStore.getGoodsStoreId());
						boolean b = false;
						if (gsr != null) {
							gsr.setGoodsId_new(g.getGoodsId());
							gsr.setIn_Time(DateUtil.formatDate(new Date(),
									"yyyy-MM-dd HH:MM:SS"));
							b = totalDao.save(gsr);
						} else {
							GoodsSellRelational gsr_new = new GoodsSellRelational();
							gsr_new.setGoodsId_new(g.getGoodsId());
							gsr_new.setIn_Time(DateUtil.formatDate(new Date(),
									"yyyy-MM-dd HH:MM:SS"));
							b = totalDao.save(gsr_new);
						}
						if (b) {
							return "确认成功";
						} else {
							return "确认失败";
						}
					} else {
						return "确认失败";
					}
				} else {
					return "确认失败";
				}
			} else {
				return "只有外购件库及状态为待入库的库存才能进行该操作";
			}
		} else {
			return "数据异常";
		}
	}

	@Override
	public List<GoodsStore> findAllWxDrk(GoodsStore goodsStore) {
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		String hql = totalDao.criteriaQueries(goodsStore, null);
		hql += " and status = '待入库' and goodsStoreWarehouse = '外协库' and style = '外协退料入库' ";
		List<GoodsStore> gsList = totalDao.query(hql);
		return gsList;
	}

	@Override
	public Object[] findAllWxRk(GoodsStore goodsStore, String startDate,
			String endDate, Integer pageNo, Integer pageSize) {
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		String hql = totalDao.criteriaQueries(goodsStore, "goodsStoreTime",
				between, "");
		hql += " and (status <> '待入库' or status is null )and goodsStoreWarehouse = '外协库' and style = '外协退料入库' ";
		List<GoodsStore> gsList = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { count, gsList };
	}

	@Override
	public Map<String, Object> findChangeGoods(GoodsStore goodsStore,
			Integer pageNo, Integer pageSize, String tag) {
		Map<String, Object> map = new HashMap<String, Object>();
		String select = "select new com.task.entity.GoodsStore(g.goodsStoreId,g.goodsStoreWarehouse,g.goodHouseName,"
				+ "g.goodsStorePosition,g.goodsStoreMarkId,g.banBenNumber,g.kgliao,g.goodsStoreLot,g.goodsStoreGoodsName,"
				+ "g.goodsStoreFormat,g.wgType,g.goodsStoreUnit,g.goodsStoreCount,g.goodsStoreDate,"
				+ "g.style,g.more,g.sellId,g.changePrint,s.sellWarehouse,s.goodHouseName,s.kuwei) ";
		List<GoodsStore> unList = totalDao
				.query(select
						+ " from GoodsStore g,Sell s where g.sellId=s.sellId "
						+ "and (g.style ='调仓入库' or g.style='转仓入库') and g.changePrint is null order by g.goodsStoreId desc");
		map.put("1", unList);
		StringBuffer buffer = new StringBuffer(
				" from GoodsStore g,Sell s where g.sellId=s.sellId ");
		if (goodsStore == null) {
			goodsStore = new GoodsStore();
		}
		if (goodsStore.getGoodsStoreMarkId() != null
				&& !goodsStore.getGoodsStoreMarkId().equals("")) {
			buffer.append(" and g.goodsStoreMarkId like '%"
					+ goodsStore.getGoodsStoreMarkId() + "%'");
		}
		if (goodsStore.getGoodsStoreGoodsName() != null
				&& !goodsStore.getGoodsStoreGoodsName().equals("")) {
			buffer.append(" and g.goodsStoreGoodsName like '%"
					+ goodsStore.getGoodsStoreGoodsName() + "%'");
		}
		if (goodsStore.getStyle() != null && !goodsStore.getStyle().equals("")) {
			buffer.append(" and g.style = '" + goodsStore.getStyle() + "'");
		}
		if (goodsStore.getChangePrint() != null
				&& !goodsStore.getChangePrint().equals("")) {
			buffer.append(" and g.changePrint like '%"
					+ goodsStore.getChangePrint() + "%'");
		}
		buffer
				.append(" and (g.style ='调仓入库' or g.style='转仓入库') and g.changePrint is not null order by g.goodsStoreId desc");
		Integer count = totalDao.getCount(buffer.toString());
		List<GoodsStore> list = totalDao.findAllByPage(select
				+ buffer.toString(), pageNo, pageSize);
		map.put("2", list);
		map.put("3", count);
		return map;
	}

	@Override
	public List<GoodsStore> findChangeGoodsBygsId(int[] selected, String tag) {
		if (selected != null && selected.length > 0) {
			StringBuffer buffer = new StringBuffer();
			for (int string : selected) {
				if (buffer.toString().equals("")) {
					buffer.append(string);
				} else {
					buffer.append("," + string);
				}
			}
			String select = "select new com.task.entity.GoodsStore(g.goodsStoreId,g.goodsStoreWarehouse,g.goodHouseName,"
					+ "g.goodsStorePosition,g.goodsStoreMarkId,g.banBenNumber,g.kgliao,g.goodsStoreLot,g.goodsStoreGoodsName,"
					+ "g.goodsStoreFormat,g.wgType,g.goodsStoreUnit,g.goodsStoreCount,g.goodsStoreDate,"
					+ "g.style,g.more,g.sellId,g.changePrint,s.sellWarehouse,s.goodHouseName,s.kuwei,g.ywmarkId) ";
			List<GoodsStore> list = totalDao.query(select
					+ " from GoodsStore g,Sell s " + "where g.goodsStoreId in("
					+ buffer.toString()
					+ ") and g.sellId=s.sellId order by g.goodsStoreId desc");
			return list;
		}
		return null;
	}

	@Override
	public String updateChangeGoods(String flag, String tag,
			PrintedOutOrder printedOutOrder) {

		if (flag != null && flag.length() > 0 && printedOutOrder != null) {
			PrintedOutOrder order = (PrintedOutOrder) totalDao
					.getObjectByCondition(
							"from PrintedOutOrder where planNum = ?",
							printedOutOrder.getPlanNum());
			if (order == null) {
				printedOutOrder.setAddTime(Util.getDateTime());
				printedOutOrder.setType("调拨入库单");
				printedOutOrder.setClassName("GoodsStore");

				totalDao.save(printedOutOrder);
				boolean update = false;
				List<GoodsStore> list = totalDao
						.query("from GoodsStore where goodsStoreId in (" + flag
								+ ")");
				for (GoodsStore goodsStore : list) {
					goodsStore.setChangePrint(printedOutOrder.getPlanNum());
					update = totalDao.update(goodsStore);
					if (!update) {
						return "更新记录失败";
					}
				}
				return update + "";
			} else {
				if (order.getPrintcount() == null) {
					order.setPrintcount(1);
				} else {
					order.setPrintcount(order.getPrintcount() + 1);
				}
				return totalDao.update(order) + "";
			}

		}
		return "没有要打印的记录";
	}

	@Override
	public Map<String, Object> getCGByPrintedOutOrder(List<GoodsStore> gsList,
			String tag) {

		PrintedOutOrder printedOutOrder = new PrintedOutOrder();
		String printNumber = null;
		if (tag != null && !tag.equals("")) {
			printedOutOrder = (PrintedOutOrder) totalDao.getObjectByCondition(
					"from PrintedOutOrder where planNum = ?", tag);
		} else {
			if (gsList != null && gsList.size() > 0) {
				GoodsStore gs = gsList.get(0);
				printNumber = gs.getChangePrint();
				if (gs.getChangePrint() == null
						|| gs.getChangePrint().equals("")) {
					String prefix = "DBIN";
					printNumber = "000001";
					String hql_maxnum = "select max(planNum) from PrintedOutOrder where planNum like '%"
							+ prefix + "%'";
					String maxnum = (String) totalDao
							.getObjectByCondition(hql_maxnum);
					if (maxnum == null || maxnum.length() == 0) {
						printNumber = prefix + printNumber;
					} else {
						String number = (1000001 + Integer.parseInt(maxnum
								.substring(prefix.length())))
								+ "";
						printNumber = prefix + number.substring(1);
					}
				}
				printedOutOrder.setPlanNum(printNumber);
				printedOutOrder.setRiqi(Util.getDateTime("yyy-MM-dd"));
				printedOutOrder.setDbStyle(gs.getStyle());
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order", printedOutOrder);
		return map;
	}

	@Override
	public String bcpquickreceive(GoodsStore goodsStore) {
		// TODO Auto-generated method stub
		GoodsStore old = (GoodsStore) totalDao.getObjectById(GoodsStore.class,
				goodsStore.getGoodsStoreId());
		if (old == null) {
			return "没有找到对应的待入库数据!";
		}
		if (!old.getStatus().equals("待入库")) {
			return "该申请不是待入库状态，入库失败!";
		}
		old.setGoodHouseName(goodsStore.getGoodHouseName());
		old.setGoodsStorePosition(goodsStore.getGoodsStorePosition());
		old.setGoodsStoreDate(goodsStore.getGoodsStoreDate());
		old.setGoodsStorePlanner(goodsStore.getGoodsStorePlanner());
		old.setGoodsStoreCount(goodsStore.getGoodsStoreCount());
		return updateGoodsStore(old, "ruku");
	}

	@Override
	public GoodsStore getGoodsStoreById(Integer goodsStoreId) {
		// TODO Auto-generated method stub
		return (GoodsStore) totalDao.getObjectById(GoodsStore.class,
				goodsStoreId);
	}

	@Override
	public boolean updatePrice(Integer id, Float hsPrice, Float bhsPrice,
			Float taxPrice) {
		boolean bool = false;
		if (id != null) {
			GoodsStore gs = (GoodsStore) totalDao.get(GoodsStore.class, id);
			gs.setHsPrice(hsPrice.doubleValue());
			gs.setGoodsStorePrice(bhsPrice.doubleValue());
			gs.setTaxprice(taxPrice.doubleValue());
			bool = totalDao.update(gs);
			// 同步修改同件号同批次的库存单价。
			List<Goods> goodsList = totalDao
					.query(
							" from Goods where goodsMarkId=? and goodsLotId =? and goodsClass =? ",
							gs.getGoodsStoreMarkId(), gs.getGoodsStoreLot(), gs
									.getGoodsStoreWarehouse());
			if (goodsList != null && goodsList.size() > 0) {
				for (Goods goods : goodsList) {
					goods.setGoodsBuyBhsPrice(bhsPrice);
					goods.setGoodsBuyPrice(hsPrice);
					goods.setTaxprice(taxPrice);
					// 查询成本核算方法
					Float avghsprice = 0f;
					Float avgbhsprice = 0f;
					AccountsDate accountsDate = (AccountsDate) totalDao
							.getObjectByCondition(" from AccountsDate");
					if (accountsDate != null) {
						String hql_banben = "";
						if (gs.getBanBenNumber() != null
								&& gs.getBanBenNumber().length() > 0) {
							hql_banben = " and banBenNumber='"
									+ gs.getBanBenNumber() + "'";
						}
						// 移动加权平均，计算所有入库记录。求平均价格
						if ("allAgv".equals(accountsDate.getGoodsType())) {
							String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
									+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
									+ hql_banben;

							List<Object[]> list = totalDao.query(hql_allAgv, gs
									.getGoodsStoreMarkId(), gs
									.getGoodsStoreWarehouse());
							Object[] obj = list.get(0);
							if (obj != null && obj[0] != null) {
								avghsprice = Float
										.parseFloat(obj[0].toString());
								avgbhsprice = Float.parseFloat(obj[1]
										.toString());
							}
						} else if ("monthAgv".equals(accountsDate
								.getGoodsType())) {
							String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount) ,sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
									+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
									+ hql_banben
									+ " and goodsStoreTime >'"
									+ Util.getDateTime("yyyy-MM")
									+ "-01' and goodsStoreTime <'"
									+ Util.getDateTime("yyyy-MM")
									+ "-"
									+ accountsDate.getJihao() + "' ";
							List<Object[]> list = totalDao.query(hql_allAgv, gs
									.getGoodsStoreMarkId(), gs
									.getGoodsStoreWarehouse());
							Object[] obj = list.get(0);
							if (obj != null && obj[0] != null) {
								try {
									avghsprice = Float.parseFloat(obj[0]
											.toString());
									avgbhsprice = Float.parseFloat(obj[1]
											.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}
					}
					if (avghsprice == null) {
						avghsprice = hsPrice;
						avgbhsprice = bhsPrice;
					}
					goods.setGoodsPrice(avgbhsprice);
					goods.setGoodshsPrice(avgbhsprice);
					if (!totalDao.update(goods)) {
						return false;
					}
				}
			}
			// 同步修改同件号同批次的出库记录单价。
			List<Sell> sellList = totalDao
					.query(
							" from Sell where sellMarkId =? and sellLot = ? and sellWarehouse =? ",
							gs.getGoodsStoreMarkId(), gs.getGoodsStoreLot(), gs
									.getGoodsStoreWarehouse());
			if (sellList != null && sellList.size() > 0) {
				for (Sell sell : sellList) {
					sell.setSellPrice(hsPrice);
					sell.setSellbhsPrice(bhsPrice);
					sell.setTaxprice(taxPrice);
					Goods g = (Goods) totalDao
							.getObjectByCondition(
									" from Goods where goodsMarkId=? and goodsLotId =? and goodsClass =? ",
									gs.getGoodsStoreMarkId(), gs
											.getGoodsStoreLot(), gs
											.getGoodsStoreWarehouse());
					if (g != null) {
						sell.setGoodsPrice(g.getGoodsPrice());
					}
					if (!totalDao.update(sell)) {
						return false;
					}
				}
			}
		}
		return bool;
	}

	@Override
	public String getwwrk() {
		// TODO Auto-generated method stub
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='手动入委外库'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode == null || !valueCode.equals("是")) {
			return "否";
		} else {
			return "是";
		}
	}

	public void save_xiufu() {
		String hql = "from  GoodsStore where goodsStoreId>=1199994  and style ='合格品入库' ";
		List<GoodsStore> gsList = totalDao.query(hql);
		if (gsList != null && gsList.size() > 0) {
			int count = 0;
			for (int i = 0, length = gsList.size(); i < length; i++) {
				GoodsStore gs = gsList.get(i);
				Goods g = (Goods) totalDao.getObjectByCondition(
						" from Goods where goodsClass=? "
								+ " and goodsLotId =? and goodsMarkId=? ", gs
								.getGoodsStoreWarehouse(), gs
								.getGoodsStoreLot(), gs.getGoodsStoreMarkId());
				if (g == null) {
					save_xiufugoods(gs, totalDao);
					count++;
				}
			}
			System.out.println("修复了" + count + "条数据");
		}

	}

	public static Goods save_xiufugoods(GoodsStore goodsStore, TotalDao totalDao) {
		if (totalDao == null) {
			totalDao = createTotol();
		}
		String rutype = "外购件入库";
		Integer processNo = null;
		String processName = null;
		// 外委回来的数据需要标记入库类型为半成品转库
		List<Object[]> procardAndcount = new ArrayList<Object[]>();
		if (goodsStore.getWwddId() != null) {
			WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
					.getObjectById(WaigouDeliveryDetail.class, goodsStore
							.getWwddId());
			if (wddOld != null) {
				// 更新采购订单信息
				WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, wddOld.getWaigouPlanId());
				if (waigouPlan.getType().equals("外委")) {
					rutype = "半成品转库2";// 含特殊含义不可修改
					processNo = Util.getSplitNumber(waigouPlan.getProcessNOs(),
							";", "max");
					processName = Util.getSplitString(waigouPlan
							.getProcessNames(), ";", "end");
					Float dhCount = goodsStore.getGoodsStoreCount();
					List<Procard> procardList = totalDao
							.query(
									"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by selfCard",
									waigouPlan.getId());// 为了先填充小批次
					if (procardList != null && procardList.size() > 0) {
						for (Procard procard : procardList) {
							List<ProcardWGCenter> zjbList = totalDao
									.query(
											"from ProcardWGCenter where wgOrderId=? and procardId=? order by id",
											waigouPlan.getId(), procard.getId());
							for (ProcardWGCenter zjb : zjbList) {
								if (zjb.getDhCount() == null) {
									zjb.setDhCount(0f);
								}
								Float kdhCount = zjb.getProcardCount()
										- zjb.getDhCount();
								if (kdhCount > 0) {
									Float jsCount = 0f;
									if (dhCount > kdhCount) {
										jsCount = kdhCount;
										dhCount -= kdhCount;
									} else {
										jsCount = dhCount;
										dhCount = 0f;
									}
									Object[] objs = new Object[] { procard,
											jsCount };
									procardAndcount.add(objs);
								}
								if (dhCount == 0) {
									break;
								}
							}
						}
					}
				} else {
					// 供应商年月日季周合格信息维护
					// saveGysInfor(goodsStore, waigouPlan, wddOld);
				}
			}
		}

		// 匹配条件需要调整

		String hql = "from Goods where goodsMarkId = ?";

		if (goodsStore.getGoodsStoreWarehouse() != null
				&& goodsStore.getGoodsStoreWarehouse().length() > 0) {
			hql += " and goodsClass='" + goodsStore.getGoodsStoreWarehouse()
					+ "'";
		}
		if (goodsStore.getGoodHouseName() != null
				&& goodsStore.getGoodHouseName().length() > 0) {
			hql += " and goodHouseName='" + goodsStore.getGoodHouseName() + "'";
		}
		if (goodsStore.getGoodsStorePosition() != null
				&& goodsStore.getGoodsStorePosition().length() > 0) {
			hql += " and goodsPosition='" + goodsStore.getGoodsStorePosition()
					+ "'";
		}
		if (goodsStore.getBanBenNumber() != null
				&& goodsStore.getBanBenNumber().length() > 0) {
			hql += " and banBenNumber='" + goodsStore.getBanBenNumber() + "'";
		}
		if (goodsStore.getKgliao() != null
				&& goodsStore.getKgliao().length() > 0) {
			hql += " and kgliao='" + goodsStore.getKgliao() + "'";
		}
		if (goodsStore.getGoodsStoreLot() != null
				&& goodsStore.getGoodsStoreLot().length() > 0) {
			hql += " and goodsLotId='" + goodsStore.getGoodsStoreLot() + "'";
		}
		if (goodsStore.getHsPrice() != null && goodsStore.getHsPrice() > 0) {
			hql += " and goodsPrice='" + goodsStore.getHsPrice() + "'";
		}

		Goods g = (Goods) totalDao.getObjectByCondition(hql, goodsStore
				.getGoodsStoreMarkId());

		// 计算单张重量
		jisuanDanZhang(goodsStore);
		// 查询同件号、订单号、批次号的是否已经存在入库记录
		String hql_distinct = "from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and goodsStoreSendId=? and osrecordId=?";
		int gscount = totalDao.getCount(hql_distinct, goodsStore
				.getGoodsStoreMarkId(), goodsStore.getGoodsStoreLot(),
				goodsStore.getGoodsStoreSendId(), goodsStore.getOsrecordId());
		if (gscount > 0) {
			throw new RuntimeException("重复入库,请刷新后重试!");
		}
		// totalDao.save(goodsStore);

		Float avghsprice =goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
		Float avgbhsprice = goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		// 查询成本核算方法
		AccountsDate accountsDate = (AccountsDate) totalDao
				.getObjectByCondition(" from AccountsDate");
		if (accountsDate != null) {
			String hql_banben = "";
			if (goodsStore.getBanBenNumber() != null
					&& goodsStore.getBanBenNumber().length() > 0) {
				hql_banben = " and banBenNumber='"
						+ goodsStore.getBanBenNumber() + "'";
			}
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben;

				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					avghsprice = Float.parseFloat(obj[0].toString());
					avgbhsprice = Float.parseFloat(obj[1].toString());
				}
			} else if ("monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount) ,sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
						+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
						+ hql_banben
						+ " and goodsStoreTime >'"
						+ Util.getDateTime("yyyy-MM")
						+ "-01' and goodsStoreTime <'"
						+ Util.getDateTime("yyyy-MM")
						+ "-"
						+ accountsDate.getJihao() + "' ";
				List<Object[]> list = totalDao.query(hql_allAgv, goodsStore
						.getGoodsStoreMarkId(), goodsStore
						.getGoodsStoreWarehouse());
				Object[] obj = list.get(0);
				if (obj != null && obj[0] != null) {
					try {
						avghsprice = Float.parseFloat(obj[0].toString());
						avgbhsprice = Float.parseFloat(obj[1].toString());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		if (avghsprice == null) {
			avghsprice = goodsStore.getHsPrice()==null?null:goodsStore.getHsPrice().floatValue();
			avgbhsprice =goodsStore.getGoodsStorePrice()==null?null:goodsStore.getGoodsStorePrice().floatValue();
		}

		if (g == null) {
			g = new Goods();
			g.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());// 件号
			g.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());// 名称
			g.setGoodsLotId(goodsStore.getGoodsStoreLot());// 批次
			g.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());// 期初量
			g.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());// 库存量
			g.setGoodsArtsCard(goodsStore.getGoodsStoreArtsCard());// 工艺卡号没有
			g.setGoodsProMarkId(goodsStore.getGoodsStoreProMarkId());// 总成件号没有
			g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 所属仓库
			g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
			g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
			g.setGoodsPrice(avgbhsprice);// 含税价格 统一在下面赋值
			g.setGoodsBuyPrice(avghsprice);// 单批次采购单价
			g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());// 供应
			g.setGoodsChangeTime(goodsStore.getGoodsStoreDate());// 日期
			g.setGoodsFormat(goodsStore.getGoodsStoreFormat());// 规格
			g.setGoodsUnit(goodsStore.getGoodsStoreUnit());// 单位
			g.setWgType(goodsStore.getWgType());// 物料类别
			g.setKgliao(goodsStore.getKgliao());// 供料属性
			g.setProNumber(goodsStore.getProNumber());// 项目编号
			g.setBanBenNumber(goodsStore.getBanBenNumber());// 版本号
			g.setGoodsZhishu(goodsStore.getGoodsStoreZhishu());// 转换数量
			g.setGoodsStoreZHUnit(goodsStore.getGoodsStoreZHUnit());// 转换单位
			g.setGoodsStyle(goodsStore.getStyle());
			g.setLendNeckStatus(goodsStore.getLendNeckStatus());// 借领属性;//综合库使用
			g.setLendNeckCount(goodsStore.getLendNeckCount());
			g.setGoodslasttime(goodsStore.getGoodsStorelasttime());// 上次质检时间
			g.setGoodsround(goodsStore.getGoodsStoreround());// 质检周期
			g.setGoodsnexttime(goodsStore.getGoodsStorenexttime());// 下次质检时间
			if (rutype.equals("半成品转库")) {
				g.setGoodsStyle("半成品转库");
				goodsStore.setStyle("半成品转库");
				g.setProcessNo(processNo);
				g.setProcessName(processName);
				goodsStore.setProcessNo(processNo);
			}
			if (rutype.equals("半成品转库2")) {
				g.setGoodsStyle("采购入库");
				goodsStore.setStyle("采购入库");
				g.setProcessNo(processNo);
				g.setProcessName(processName);
				goodsStore.setProcessNo(processNo);
			}
			totalDao.save(g);
			// System.out.print(goodsStore.getWgType() + "  " + g.getWgType());
		} else {
			g.setGoodsCurQuantity(g.getGoodsCurQuantity()
					+ goodsStore.getGoodsStoreCount());
			g.setGoodsPrice(avgbhsprice);// 含税价格 统一在下面赋值
			g.setGoodsBuyPrice(avghsprice);// 单批次采购单价
			totalDao.update(g);
		}
		if (accountsDate != null) {
			// 移动加权平均，计算所有入库记录。求平均价格
			if ("allAgv".equals(accountsDate.getGoodsType())
					|| "monthAgv".equals(accountsDate.getGoodsType())) {
				String hql_banben = "";
				if (goodsStore.getBanBenNumber() != null
						&& goodsStore.getBanBenNumber().length() > 0) {
					hql_banben = " and banBenNumber='"
							+ goodsStore.getBanBenNumber() + "'";
				}
				// 同步更新现有库存价格
				String hql_allGoods = "from Goods where goodsMarkId=? and goodsClass=? and goodsCurQuantity>0 and goodsPrice is not null "
						+ hql_banben;
				List<Goods> list_goods = totalDao.query(hql_allGoods,
						goodsStore.getGoodsStoreMarkId(), goodsStore
								.getGoodsStoreWarehouse());
				for (Goods goods : list_goods) {
					try {
						goods.setGoodsPrice(avgbhsprice);// 不含税单价（财务做帐用）
						goods.setGoodshsPrice(avghsprice);// 含税价格
						goods.setGoodsBuyPrice(goodsStore.getHsPrice().floatValue());// 单批次含税采购单价
						goods.setGoodsBuyBhsPrice(goodsStore
								.getGoodsStorePrice().floatValue());// 单批次不含税采购单价
						goods
								.setTaxprice(goodsStore.getTaxprice()
										.floatValue());// 税率
					} catch (Exception e) {
						e.printStackTrace();
					}
					totalDao.update(goods);
				}
			}
		}

		if (rutype.equals("半成品转库") || rutype.equals("半成品转库2")) {
			for (Object[] objs : procardAndcount) {
				Procard procard = (Procard) objs[0];
				if (procard != null) {
					Float count = (Float) objs[1];
					if (procard.getZaizhizkCount() == null) {
						procard.setZaizhizkCount(count);
					} else {
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								+ count);
					}
					if (procard.getZaizhikzkCount() != null
							&& procard.getFilnalCount() != null
							&& procard.getZaizhikzkCount() > procard
									.getFilnalCount()) {
						AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
								"件号:" + procard.getMarkId() + "批次:"
										+ procard.getSelfCard() + "半成品转库数量为："
										+ procard.getZaizhikzkCount()
										+ "大于批次数量，系统自动修复为"
										+ procard.getFilnalCount()
										+ "，操作是：半成品 接收入库 ,当前系统时间为"
										+ Util.getDateTime(), "2");
						procard.setZaizhikzkCount(procard.getFilnalCount());
					}
					totalDao.update(procard);
					// 生产件和零件中间表
					// 添加零件与在制品关系表
					ProcardProductRelation pprelation = new ProcardProductRelation();
					pprelation.setAddTime(Util.getDateTime());
					pprelation.setProcardId(procard.getId());
					pprelation.setGoodsId(g.getGoodsId());
					pprelation.setZyCount(count);
					pprelation.setFlagType("本批在制品");
					totalDao.save(pprelation);
					// 查询下到工序是否外委
					String nextWwhql = "from ProcessInfor where processNO>? and procard.id=?  and (dataStatus is null or dataStatus!='删除') order by processNO";
					List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
							.query(nextWwhql, processNo, procard.getId());
					if (nextWwProcessInforList.size() > 0) {
						int n = 0;
						WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
						for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
							if (nextWwProcessInfor != null) {
								if (n == 0
										&& !"外委".equals(nextWwProcessInfor
												.getProductStyle())) {
									// 查询下道工序是否手动外委
									if ((nextWwProcessInfor.getApplyWwCount() != null && nextWwProcessInfor
											.getApplyWwCount() > 0)
											|| (nextWwProcessInfor
													.getSelectWwCount() != null && nextWwProcessInfor
													.getSelectWwCount() > 0)
											|| (nextWwProcessInfor
													.getAgreeWwCount() != null && nextWwProcessInfor
													.getAgreeWwCount() > 0)) {
										g.setDtcFlag("外协调委外");
										break;
									}
								}
								if ("外委".equals(nextWwProcessInfor
										.getProductStyle())
										&& (n == 0 || ("yes")
												.equals(nextWwProcessInfor
														.getProcessStatus()))) {
									if (n == 0) {
										// 外协库存标记为待调仓
										g.setDtcFlag("外协调委外");
										totalDao.update(g);
										wwp.setRootMarkId(procard
												.getRootMarkId());
										wwp.setRootSelfCard(procard
												.getRootSelfCard());
										wwp.setOrderNum(procard
												.getOrderNumber());
										wwp.setYwMarkId(procard.getYwMarkId());
										wwp
												.setBanben(procard
														.getBanBenNumber());
										wwp.setBanci(procard.getBanci());
										wwp.setMarkId(procard.getMarkId());
										wwp.setProcessNo(nextWwProcessInfor
												.getProcessNO()
												+ "");
										wwp.setProName(procard.getProName());
										wwp.setProcessName(nextWwProcessInfor
												.getProcessName());
										wwp.setType("外委");
										Float wwCount = 0f;
										wwp.setNumber(count);
										wwp.setBeginCount(count);
										wwCount = count;
										wwp.setAddTime(Util.getDateTime());
										wwp.setJihuoTime(Util.getDateTime());
										wwp.setShArrivalTime(procard
												.getNeedFinalDate());// 应到货时间在采购确认通知后计算
										wwp.setUnit(procard.getUnit());
										wwp.setCaigouMonth(Util
												.getDateTime("yyyy-MM月"));// 采购月份
										String wwNumber = "";
										String before = null;
										Integer bIndex = 10;
										before = "ww"
												+ Util.getDateTime("yyyyMMdd");
										Integer maxNo = 0;
										String maxNumber = (String) totalDao
												.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
														+ before + "%'");
										if (maxNumber != null) {
											String wwnum = maxNumber.substring(
													bIndex, maxNumber.length());
											try {
												Integer maxNum = Integer
														.parseInt(wwnum);
												if (maxNum > maxNo) {
													maxNo = maxNum;
												}
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										maxNo++;
										wwNumber = before
												+ String.format("%03d", maxNo);
										wwp.setPlanNumber(wwNumber);// 采购计划编号
										wwp.setSelfCard(procard.getSelfCard());// 批次
										// wwp.setGysId(nextWwProcessInfor
										// .getZhuserId());// 供应商id
										// wwp.setGysName(nextWwProcessInfor.getGys());//
										// 供应商名称
										wwp.setAllJiepai(nextWwProcessInfor
												.getAllJiepai());// 单件总节拍
										wwp
												.setDeliveryDuration(nextWwProcessInfor
														.getDeliveryDuration());// 耽误时长
										wwp.setSingleDuration(procard
												.getSingleDuration());// 单班时长(工作时长)
										wwp.setProcardId(procard.getId());
										wwp.setProcard(procard);
										wwp.setStatus("待入库");
										totalDao.save(wwp);
										// wgSet.add(wwp);
									} else {
										wwp.setProcessNo(wwp.getProcessNo()
												+ ";"
												+ nextWwProcessInfor
														.getProcessNO());
										wwp.setProcessName(wwp.getProcessName()
												+ ";"
												+ nextWwProcessInfor
														.getProcessName());
										totalDao.update(wwp);
									}
								} else {
									break;
								}
							} else {
								break;
							}
							n++;
						}
						if (wwp.getId() != null) {
							// 匹配供应商
							Price price = (Price) totalDao
									.getObjectByCondition(
											"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
													+ Util.getDateTime()
													+ "')  order by hsPrice",
											wwp.getMarkId(), wwp.getProcessNo());
							if (price != null) {
								wwp.setPriceId(price.getId());
								wwp.setGysId(price.getGysId());
								ZhUser zhUser = (ZhUser) totalDao
										.getObjectById(ZhUser.class, price
												.getGysId());
								wwp.setGysName(zhUser.getName());
								wwp.setUserCode(zhUser.getUsercode());
								wwp.setUserId(zhUser.getUserid());
								totalDao.update(wwp);
							}
						}
					}
				}
			}
		}

		// 判断仓区，成品面积有才更新仓区占地面积
		// 仓区
		GoodHouse goodHouse = (GoodHouse) totalDao
				.get(
						"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
						new Object[] { goodsStore.getGoodsStoreWarehouse(),
								goodsStore.getGoodHouseName() });
		if (goodHouse != null) {
			// 单件
			Double procardArea = (Double) totalDao
					.getObjectByCondition(
							"select actualUsedProcardArea from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is null) and (banbenStatus='默认' or banbenStatus is null or banbenStatus='') order by actualUsedProcardArea desc",
							new Object[] { goodsStore.getGoodsStoreMarkId() });
			if (procardArea != null) {
				// 存在面积
				if (goodHouse.getGoodAllArea() > 0
						&& goodHouse.getGoodAllArea() != null
						&& procardArea != null && procardArea > 0) {
					// 单件占地面积goodsStoreCount
					Double procardActualUsed = procardArea
							* goodsStore.getGoodsStoreCount();
					procardActualUsed = Double
							.valueOf(new DecimalFormat("0.00")
									.format(procardActualUsed
											+ goodHouse.getGoodIsUsedArea()));
					goodHouse.setGoodIsUsedArea(procardActualUsed);
					goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()
							- goodHouse.getGoodIsUsedArea());
					totalDao.update(goodHouse);
				}
			}
		}
		goodshuizong1("入库", goodsStore, null);
		return g;

	}

}
