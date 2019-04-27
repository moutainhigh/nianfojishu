package com.task.ServerImpl.ess;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.SmsService;
import com.task.Server.caiwu.core.CorePayableServer;
import com.task.Server.ess.GoodsServer;
import com.task.Server.ess.GoodsStoreServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.WareHouseAuthServiceImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.*;
import com.task.entity.android.OsRecord;
import com.task.entity.project.QuotedPrice;
import com.task.entity.project.QuotedPriceFenhong;
import com.task.entity.project.QuotedPriceUserCost;
import com.task.entity.sop.*;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.CompanyInfo;
import com.task.entity.unpasskp.ProductUnPassKp;
import com.task.util.DateUtil;
import com.task.util.MD5;
import com.task.util.Util;
import com.tast.entity.zhaobiao.ZhUser;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.VerticalAlignment;
import jxl.write.biff.RowsExceededException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Boolean;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

@SuppressWarnings("unchecked")
public class GoodsServerImpl implements GoodsServer {

	private TotalDao totalDao;
	private StringBuffer strbu = new StringBuffer();
	private GoodsStoreServer goodsStoreServer;
	private YuanclAndWaigj wgj;
	private SmsService smsService;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	private CorePayableServer corePayableServer;

	public CorePayableServer getCorePayableServer() {
		return corePayableServer;
	}

	public void setCorePayableServer(CorePayableServer corePayableServer) {
		this.corePayableServer = corePayableServer;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public YuanclAndWaigj getWgj() {
		return wgj;
	}

	public void setWgj(YuanclAndWaigj wgj) {
		this.wgj = wgj;
	}

	@Override
	public Goods getGoodsById(Integer id) {
		Goods goods = (Goods) totalDao.getObjectById(Goods.class, id);
		if (goods != null) {
			if (goods.getNeiorderId() == null && goods.getGoodsLotId() != null) {
				String orderNumber = (String) totalDao
						.getObjectByCondition(
								"select o.orderNum from OrderManager o join o.innerOrders i where i.id in(select planOrderId from Procard where markId=? and selfCard=?) ",
								goods.getGoodsMarkId(), goods.getGoodsLotId());
				if (orderNumber == null) {
					orderNumber = (String) totalDao
							.getObjectByCondition(
									"select orderNO from ProjectOrder where id in(select planOrderId from Procard where markId=? and selfCard=?)",
									goods.getGoodsMarkId(), goods
											.getGoodsLotId());
				}
				goods.setNeiorderId(orderNumber);
			}
		}
		return goods;
	}

	public GoodsStoreServer getGoodsStoreServer() {
		return goodsStoreServer;
	}

	public void setGoodsStoreServer(GoodsStoreServer goodsStoreServer) {
		this.goodsStoreServer = goodsStoreServer;
	}

	public void deletegs(Goods goods) {
		Goods newgoods = (Goods) totalDao.getObjectById(Goods.class, goods
				.getGoodsId());
		if (newgoods != null) {
			if(newgoods.getGoodsClass().equals("委外库")){
				List<ProcardProductRelation> pprList =  totalDao.query(" from ProcardProductRelation where goodsId =?", newgoods.getGoodsId());
				if(pprList!=null&&pprList.size()>0){
					for(ProcardProductRelation ppr:pprList){
						if(ppr!=null){
							Procard procard = (Procard) totalDao.getObjectById(Procard.class, ppr.getProcardId());
							if(procard!=null){
								if(procard.getZaizhizkCount()!=null
										&&procard.getZaizhizkCount()>ppr.getZyCount()){
									procard.setZaizhizkCount(procard.getZaizhizkCount()-ppr.getZyCount());
								}else{
									procard.setZaizhizkCount(0f);
								}
								if(procard.getZaizhiApplyZk()!=null){
									procard.setZaizhiApplyZk(procard.getZaizhiApplyZk()+ppr.getZyCount());
								}else{
									procard.setZaizhiApplyZk(ppr.getZyCount());
								}
								if(procard.getZaizhiApplyZk()>procard.getFilnalCount()){
									procard.setZaizhiApplyZk(procard.getFilnalCount());
								}
								totalDao.update(procard);
							}
							totalDao.delete(ppr);
						}
					}
				}
				GoodsStore goodsStore = (GoodsStore) totalDao.getObjectByCondition(" from GoodsStore where goodsStoreMarkId =? " +
						"and goodsStoreLot=? and goodsStoreCount=? and goodsStoreWarehouse='委外库' and status='入库'", newgoods.getGoodsMarkId(),
						newgoods.getGoodsLotId(),newgoods.getGoodsCurQuantity());
				if(goodsStore!=null){
					goodsStore.setStatus("待入库");
					totalDao.update(goodsStore);
				}else{
					throw new RuntimeException("没有找到对应的入库记录!删除失败!");
				}
				
			}else{
				// 删除入库记录
				String hql1 = "delete GoodsStore where 1=1 ";
				String hql2 = "delete Sell where 1=1 ";
				// 件号
				if (newgoods.getGoodsMarkId() != null
						&& !newgoods.getGoodsMarkId().equals("")) {
					hql1 = hql1 + " and  goodsStoreMarkId='"
					+ newgoods.getGoodsMarkId() + "'";
					hql2 = hql2 + " and  sellMarkId='" + newgoods.getGoodsMarkId()
					+ "'";
				} else {
					hql1 = hql1
					+ " and  (goodsStoreMarkId='' or goodsStoreMarkId is null)";
					hql2 = hql2 + " and  (sellMarkId='' or sellMarkId is null)";
				}
				// 规格
				if (newgoods.getGoodsFormat() != null
						&& !newgoods.getGoodsFormat().equals("")) {
					hql1 = hql1 + " and  goodsStoreMarkFormat='"
					+ newgoods.getGoodsFormat() + "'";
					hql2 = hql2 + " and  sellFormat='" + newgoods.getGoodsFormat()
					+ "'";
				} else {
					hql1 = hql1
					+ " and (goodsStoreMarkFormat='' or goodsStoreMarkFormat is null)";
					hql2 = hql2 + " and  (sellFormat='' or sellFormat is null)";
				}
				// 批次
				if (newgoods.getGoodsLotId() != null
						&& !newgoods.getGoodsLotId().equals("")) {
					hql1 = hql1 + " and  goodsStoreLot='"
					+ newgoods.getGoodsLotId() + "'";
					hql2 = hql2 + " and   sellLot='" + newgoods.getGoodsLotId()
					+ "'";
				} else {
					hql1 = hql1
					+ " and  (goodsStoreLot='' or goodsStoreLot is null)";
					hql2 = hql2 + " and   (sellLot='' or sellLot is null)";
				}
				// +
				// "' and  goodsStoreMarkFormat='"+newgoods.getGoodsFormat()+"'  and goodsStoreLot='"+newgoods.getGoodsLotId()+"'";
				// totalDao.createQueryUpdate(hql1,null);
				//			
				// 删除出库记录 Sell
				// sellMarkId='"+newgoods.getGoodsMarkId()
				// +"' and sellFormat='"+newgoods.getGoodsFormat()+"'  and sellLot='"+newgoods.getGoodsLotId()+"'";
				totalDao.createQueryUpdate(hql1, null);
				totalDao.createQueryUpdate(hql2, null);
			}
		}
		totalDao.delete(newgoods);
	}
	//public Object[] fin
	@Override
	public Object[] findGoods(Goods goods, String startDate, String endDate,
			Integer cpage, Integer pageSize, String role, String pagestatus,
			Integer goodsAge) {

		String strif = " and goodsCurQuantity>0";
		String changqu = "";
		String kubie = "";
		if (goods == null) {
			goods = new Goods();
		}
		Users user = (Users) Util.getLoginUser();
		String hqlwarehouse = "from WareHouseAuth where usercode='"
				+ user.getCode() + "'";
		List warehoustlist = totalDao.find(hqlwarehouse);
		boolean isall = false;
		if (warehoustlist != null && warehoustlist.size() > 0) {
			WareHouseAuth whh = (WareHouseAuth) warehoustlist.get(0);
			if (("管理员".equals(whh.getGroup()) || "查看".equals(whh.getGroup()))) {
				isall = true;
				if ("all".equals(pagestatus)) {
					strif = "";
				}
			}
		}

		String hql = "from Goods ";
		String hql_cq = " and goodHouseName in (";
		if (goods != null && goods.getGoodHouseName() != null
				&& goods.getGoodHouseName().length() > 0) {
			String str = "";
			String[] cangqus = goods.getGoodHouseName().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			changqu = goods.getGoodHouseName();
			goods.setGoodHouseName(null);
			hql_cq += ")";
		} else {
			hql_cq = "";
		}

		String hql_kb = " and  goodsClass in (";
		if (goods != null && goods.getGoodsClass() != null
				&& goods.getGoodsClass().length() > 0) {
			String str = "";
			String[] kubies = goods.getGoodsClass().split(",");
			if (kubies != null && kubies.length > 0) {
				for (int i = 0; i < kubies.length; i++) {
					str += ",'" + kubies[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_kb += str;
			}
			kubie = goods.getGoodsClass();
			goods.setGoodsClass(null);
			hql_kb += ")";
		} else {
			hql_kb = "";
		}
		if ("wg".equals(pagestatus)) {
			hql_kb = " and goodsClass = '外购件库'";
		}

		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		String str = "";
		String wgType = "";
		if (null != goods) {
			if (goods.getWgType() != null && !"".equals(goods.getWgType())) {
				wgType = goods.getWgType();
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name =? ", wgType);
				if (category != null) {
					getWgtype(category);
				}
				str = "  wgType in (" + strbu.toString().substring(1) + ")";
				goods.setWgType(null);
			}
			String bw = "";
			if (goods.getGoodsClass() != null
					&& !"".equals(goods.getGoodsClass())) {
				bw = goods.getGoodsClass();
				goods.setGoodsClass(null);
			}
			hql = totalDao.criteriaQueries(goods, "goodsChangeTime", between,
					str);
			hql += hql_cq + hql_kb;

			if (!"".equals(bw)) {
				hql += " and goodsClass = '" + bw + "'";
				goods.setGoodsClass(bw);
			}
			// hql +=" and goodsCurQuantity>0 "; //查询所有是把库存等于0的也查出来； 查询是只查库存>0的
			if (null != goodsAge && 0 != goodsAge) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_YEAR, -goodsAge);
				String oldDate = format.format(calendar.getTime());
				hql += " and goodsChangeTime< '" + oldDate
						+ "' and goodsCurQuantity>0 ";
			}
			goods.setWgType(wgType);
			if (pagestatus != null && "zz".equals(pagestatus)) {
				hql += " and goodsCurQuantity>0 order by goodsChangeTime desc";// and
				// goodsClass
				// ='在制品'改为全部库存
			} else if ("dtc".equals(pagestatus)) {
				hql += " and dtcFlag is not null and dtcFlag !='' " + strif
						+ " order by goodsChangeTime desc";
			} else {
				hql += " and goodsClass in(" + getWarehouseList(null) + ") "
						+ strif + " order by goodsChangeTime desc";
			}
			// if (hql.contains("where")) {
			// hql = hql + " and goodsClass in(" + getWarehouseList(null) + ") "
			// + strif + " order by goodsChangeTime desc";
			// } else {
			// hql = hql + " where goodsClass in(" + getWarehouseList(null) +
			// ") "
			// + strif + " order by goodsChangeTime desc";
			// }

		}
		Object[] goodsAarr = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;
		for (Object obj : list) {
			Goods g = (Goods) obj;
			// // 查询生产批次
			// Procard procard = (Procard) totalDao
			// .getObjectByCondition(
			// " from Procard where id in(select procardId from ProcardProductRelation  where goodsId=? and zyCount>ckCount)",
			// g.getGoodsId());
			// if (procard == null) {
			// procard = (Procard) totalDao
			// .getObjectByCondition(
			// " from Procard where id in(select procardId from ProcardProductRelation  where goodsId=? )",
			// g.getGoodsId());
			// }
			// if (procard != null) {
			// g.setWxselfCard(procard.getSelfCard());
			// g.setYwmarkId(procard.getYwMarkId());
			// g.setNeiorderId(procard.getOrderNumber());
			// }

			sumcount += g.getGoodsCurQuantity();
		}
		goodsAarr[0] = count;
		goodsAarr[1] = list;
		goodsAarr[2] = sumcount;
		goodsAarr[3] = isall;
		strbu = new StringBuffer();
		goods.setGoodHouseName(changqu);
		goods.setGoodsClass(kubie);
		return goodsAarr;
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
	public Object[] findYlGoods(Goods goods, String startDate, String endDate,
			Integer cpage, Integer pageSize, String role) {
		String strif = " and goodsCurQuantity>0";
		Users user = (Users) Util.getLoginUser();
		String hqlwarehouse = "from WareHouseAuth where usercode='"
				+ user.getCode() + "'";
		List warehoustlist = totalDao.find(hqlwarehouse);
		if (warehoustlist != null && warehoustlist.size() > 0) {
			WareHouseAuth whh = (WareHouseAuth) warehoustlist.get(0);
			if ("管理员".equals(whh.getGroup()) || "查看".equals(whh.getGroup())) {
				strif = "";
			}
		}
		String hql = "from Goods where goodsClass in(";
		hql += getWarehouseList(null) + ") " + strif
				+ " order by goodsChangeTime desc";
		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		if (goods == null) {
			goods = new Goods();
		}
		goods.setGoodsClass("余料库");
		if (null != goods) {
			hql = totalDao.criteriaQueries(goods, "goodsChangeTime", between,
					"");
			if (hql.contains("where")) {
				hql = hql + " and goodsClass in(" + getWarehouseList(null)
						+ ") " + strif + " order by goodsChangeTime desc";
			} else {
				hql = hql + " where goodsClass in(" + getWarehouseList(null)
						+ ") " + strif + " order by goodsChangeTime desc";
			}
		}
		Object[] goodsAarr = new Object[2];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		goodsAarr[0] = count;
		goodsAarr[1] = list;
		return goodsAarr;
	}
 
	@Override
	public boolean updateGoods(Goods goods, Sell sell, String tag, String isNeed)
			throws Exception {

		boolean b = true;
		Goods g = null;
		if ("saveSell".equals(tag) || "dtc".equals(tag)) {
			Users user = (Users) Util.getLoginUser();
			String nowTime = Util.getDateTime();
			g = (Goods) totalDao.getObjectById(Goods.class, goods.getGoodsId());

			// 判断库存中的数量是否够用
			Float goodsCurQuantity = g.getGoodsCurQuantity();
			List<Sell> historySellList = totalDao
					.query(
							"from Sell where goodsId = ?"
									+ " and handwordSellEpId is not null and handwordSellStatus in ('未审批','审批中')",
							g.getGoodsId());
			if (historySellList != null && historySellList.size() > 0) {
				float historyNumber = 0;
				for (Sell sell2 : historySellList) {
					historyNumber += sell2.getSellCount();
				}
				if (goodsCurQuantity - historyNumber - sell.getSellCount() < 0) {
					throw new RuntimeException("当前库存数量不足，已有被申请数量，本次最大可出库数量为："
							+ (goodsCurQuantity - historyNumber));
				}
			}

			if (g.getBzApplyCount() != null) {// 包装申请数量
				g.setBzApplyCount(g.getBzApplyCount() - sell.getSellCount());
				if (g.getBzApplyCount() < 0) {
					g.setBzApplyCount(0f);
				}
			}
			sell.setSellAdminName(user.getName());
			sell.setSellAdminId(user.getId());
			sell.setSellTime(nowTime);
			sell.setGoodsId(g.getGoodsId());
			sell.setSellLot(g.getGoodsLotId());
			sell.setProcessNo(g.getProcessNo());
			sell.setProcessName(g.getProcessName());
			if ("dtc".equals(tag)) {
				if ("外协调委外".equals(g.getDtcFlag())) {
					if (g.getGoodsCurQuantity() == 0) {
						throw new RuntimeException("此物料已无库存,调仓失败!");
					}
					// 在制品待入库
					Goods newgoods = new Goods();
					List<Procard> procardList = totalDao
							.query(
									"from Procard where id in(select procardId from ProcardProductRelation where goodsId=?) order by selfCard",
									goods.getGoodsId());
					Float dtcCount = sell.getSellCount();
					if (procardList != null) {
						for (Procard procard : procardList) {
							// 1.查出最后一道工序号
							Integer maxNo = (Integer) totalDao
									.getObjectByCondition(
											"select max(processNO) from ProcessInfor where procard.id=? ",
											procard.getId());
							// 2.查出最后一个工序卡住数量
							List<Procard> wgjList = totalDao
									.query(
											"from Procard where procard.id=? and procardStyle='外购'  and hascount>0",
											procard.getId());
							if (procard.getHascount() == null) {
								procard.setHascount(procard.getKlNumber());
							}
							Float lastCount = procard.getKlNumber()
									- procard.getHascount();
							if (wgjList != null && wgjList.size() > 0) {
								for (Procard wgj : wgjList) {
									float scCount = (wgj.getKlNumber() - wgj
											.getHascount())
											* wgj.getQuanzi1()
											/ wgj.getQuanzi2();
									scCount = (float) Math.floor(scCount);
									if (scCount < lastCount) {
										lastCount = scCount;
									}
								}
							}
							List<ProcardProductRelation> pprList = totalDao
									.query(
											"from ProcardProductRelation where procardId=? and goodsId=?",
											procard.getId(), goods.getGoodsId());
							for (ProcardProductRelation ppr : pprList) {
								int count = 0;
								if ((ppr.getZyCount() - ppr.getCkCount()) > dtcCount) {
									procard.setZaizhizkCount(procard
											.getZaizhizkCount()
											- dtcCount);
									procard.setZaizhiApplyZk(procard
											.getZaizhiApplyZk()
											+ dtcCount);
									ppr.setCkCount(ppr.getCkCount() + dtcCount);
									count = dtcCount.intValue();
									dtcCount = 0f;
								} else {
									procard.setZaizhizkCount(procard
											.getZaizhizkCount()
											- (ppr.getZyCount() - ppr
													.getCkCount()));
									count = (int) (ppr.getZyCount() - ppr
											.getCkCount());
									Float zaizhiApplyZk = 0f;
									if (procard.getZaizhiApplyZk() != null) {
										zaizhiApplyZk = procard
												.getZaizhiApplyZk();
									}
									procard.setZaizhiApplyZk(zaizhiApplyZk
											+ (ppr.getZyCount() - ppr
													.getCkCount()));
									dtcCount -= (ppr.getZyCount() - ppr
											.getCkCount());
									ppr.setCkCount(count);
								}
								// 调整之后外委工序的可领数量
								List<ProcessInfor> nextList = totalDao
										.query(
												"from ProcessInfor where processNO>? and procard.id=?",
												g.getProcessNo(), procard
														.getId());
								if (nextList != null && nextList.size() > 0) {
									String upProcesstype = "";
									String upNeedSave = null;
									boolean b2 = true;// 半成品转库之后工序限制可领数量，半成品转库或者领取之后解开限制数量
									float lastTotalCount = -1;// 上道工序可领数量
									for (int i = 0; i < nextList.size(); i++) {
										ProcessInfor processInfor = nextList
												.get(i);
										if (b2) {
											if ((upNeedSave != null && upNeedSave
													.equals("是"))
													&& (processInfor
															.getNeedSave() == null || !processInfor
															.getNeedSave()
															.equals("是"))) {
												b2 = false;
											}
											if (upProcesstype.equals("外委")
													&& processInfor
															.getProductStyle() != null
													&& processInfor
															.getProductStyle()
															.equals("自制")) {
												b2 = false;
											}
											if (upProcesstype.equals("外委")
													&& processInfor
															.getProductStyle() != null
													&& processInfor
															.getProductStyle()
															.equals("外委")
													&& processInfor
															.getProcessStatus() != null
													&& processInfor
															.getProcessStatus()
															.equals("no")) {
												b2 = false;
											}
										}
										upProcesstype = processInfor
												.getProductStyle();
										upNeedSave = processInfor.getNeedSave();
										if (i == (nextList.size() - 1)
												&& ((procard.getKlNumber() - procard
														.getHascount()) > lastCount)) {// 部分领料最后一道工序的可领数量为最小minNumber-
											maxNo = processInfor.getProcessNO();// 最大工序号
											if (!b2) {
												continue;
											} else {
												if ((processInfor
														.getTotalCount() + count) < lastCount) {
													processInfor
															.setTotalCount(processInfor
																	.getTotalCount()
																	+ count);
												} else {
													processInfor
															.setTotalCount((int) Math
																	.floor(lastCount));
												}
											}
										} else {
											if (!b2) {
												continue;
											} else {
												processInfor
														.setTotalCount(processInfor
																.getTotalCount()
																+ count);
												if (processInfor
														.getTotalCount() > procard
														.getFilnalCount()) {
													// 发送异常消息bgg
													// AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
													// "件号:" +
													// procard.getMarkId() +
													// "批次:"
													// +
													// procard.getSelfCard()+"第"+processInfor.getProcessNO()
													// + "工序可领数量为："
													// +
													// processInfor.getTotalCount()
													// +
													// "大于批次数量，系统自动修复为批次数量"+procard.getFilnalCount()+"，操作是：半成品转库,当前系统时间为"
													// + Util.getDateTime(),
													// "2");
													processInfor
															.setTotalCount(procard
																	.getFilnalCount()
																	.intValue());
												}
												if (lastTotalCount > 0
														&& lastTotalCount < processInfor
																.getTotalCount()) {// 不能超过伤到工序的可领数量
													processInfor
															.setTotalCount((int) lastTotalCount);
												}
											}
										}
										lastTotalCount = processInfor
												.getTotalCount();
										totalDao.update(processInfor);
									}
								}

								totalDao.update(procard);
								// 半成品待入库
								GoodsStore goodsStore2 = new GoodsStore();
								goodsStore2.setGoodsStoreMarkId(procard
										.getMarkId());
								goodsStore2.setBanBenNumber(procard
										.getBanBenNumber());
								goodsStore2.setGoodsStoreLot(procard
										.getSelfCard());
								goodsStore2.setGoodsStoreGoodsName(procard
										.getProName());
								goodsStore2.setApplyTime(Util.getDateTime());
								goodsStore2
										.setGoodsStoreArtsCard((String) totalDao
												.getObjectByCondition(
														"select selfCard from Procard where id=?",
														procard.getRootId()));
								goodsStore2.setGoodsStorePerson(Util
										.getLoginUser().getName());
								goodsStore2.setStatus("待入库");
								goodsStore2.setStyle("半成品转库");
								goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
								goodsStore2.setGoodHouseName(sell
										.getGoodHouseName());// 仓区
								goodsStore2.setGoodsStorePosition(sell
										.getKuwei());// 库位
								goodsStore2
										.setGoodsStoreUnit(procard.getUnit());
								goodsStore2.setGoodsStoreCount(count + 0f);// 数量
								goodsStore2.setProcessNo(g.getProcessNo());
								goodsStore2.setProcessName(g.getProcessName());
								goodsStore2.setGoodsStoreDate(Util
										.getDateTime("yyyy-MM-dd"));
								totalDao.update(procard);
								totalDao.save(goodsStore2);
								if (dtcCount == 0) {
									break;
								}
							}
							if (dtcCount == 0) {
								break;
							}
						}
					}
					sell.setSellWarehouse(g.getGoodsClass());
					sell.setGoodHouseName(g.getGoodHouseName());
					sell.setKuwei(g.getGoodsPosition());
					g.setGoodsCurQuantity(g.getGoodsCurQuantity()
							- sell.getSellCount());
				}
			} else {
			}
			if (null == sell.getSellFormat()) {
				sell.setSellFormat("");
			}

			// if ((sell.getSellWarehouse().equals("成品库")) && isNeed != null
			// && (isNeed.equals("全") || isNeed.equals("外"))) {
			// additionalproductSell(sell,isNeed); 审批同意后再操作
			// }
			sell.setSellAdminName(Util.getLoginUser().getName());
			if ("成品库".equals(sell.getSellWarehouse())) {

			}

			sell.setGoodsPrice(g.getGoodsPrice());// 平均单价（库存成本）
			sell.setSellPrice(g.getGoodsBuyPrice());
			sell.setSellbhsPrice(g.getGoodsBuyBhsPrice());
			sell.setTaxprice(g.getTaxprice());

			if ((g.getKgliao() == null || !g.getKgliao().equals("CS"))
					&& ("领料出库".equals(sell.getStyle()) || "报废出库".equals(sell
							.getStyle()))) {
				Integer maxNumber = (Integer) totalDao
						.getObjectByCondition("select max(handwordSellNumber) from Sell ");
				if (maxNumber != null) {
					maxNumber += 1;
				} else {
					maxNumber = 1;
				}
				sell.setHandwordSellNumber(maxNumber);
				b = b & totalDao.save(sell);

				try {
					Integer epId = CircuitRunServerImpl.createProcess(
							"手工出库单审核", Sell.class, sell.getSellId(),
							"handwordSellStatus", "sellId",
							"sellAction!printStorage.action?tag=show&selected="
									+ sell.getSellId(), "部门：" + user.getDept()
									+ ",姓名：" + user.getName() + ",件号:"
									+ sell.getSellMarkId() + "申请手工出库，请您审批。",
							true);
					if (epId != null && epId > 0) {
						sell.setHandwordSellEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							sell.setHandwordSellStatus("同意");
						} else {
							sell.setHandwordSellStatus("未审批");
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			} else {
				// 成品库、备货库、待调仓不走审批流程
				g.setGoodsCurQuantity(g.getGoodsCurQuantity()
						- sell.getSellCount());

				// if ("成品库".equals(sell.getSellWarehouse())
				// && "销售出库".equals(sell.getStyle())) {毫无作用的代码不知道是谁写的
				// // 处理订单的封闭
				// String orderNum = sell.getOrderNum();// 订单号，都是单个订单
				// String hql = "from OrderManager where orderNum='" +
				// sell.getOrderNum() + "'";
				// List listOrder = totalDao.find(hql);
				// if (listOrder.size() > 0) {
				// OrderManager order = (OrderManager) listOrder.get(0);
				// String hqlorderProduct =
				// "from ProductManager where orderManager.id="
				// + order.getId() + " and pieceNumber='" + sell.getSellMarkId()
				// + "'";
				// List listOP = totalDao.find(hqlorderProduct);
				// if (listOP.size() > 0) {
				// ProductManager pm = (ProductManager) listOP.get(0);
				// float sellcountF = sell.getSellCount();
				// int sellCount = (int) sellcountF;
				// int wancheng = sellCount + pm.getSellCount();
				// if(wancheng>pm.getAllocationsNum()){//出库数量超出
				// throw new
				// RuntimeException("出库数量超出订单已入库数量("+(wancheng-pm.getNum())+")");
				// }if (wancheng > pm.getNum()) {// 出库总量超过订单数量
				// throw new
				// RuntimeException("出库数量超出订单数量("+(wancheng-pm.getNum())+")");
				// } else {
				// // pm.setSellCount(wancheng);
				// // totalDao.update(pm);
				// // String hqlop =
				// "from ProductManager where orderManager.id="
				// // + order.getId();
				// // if (totalDao.find(hqlop).size() == 0) {
				// // order.setOrderFil("已交付");
				// // }
				// }
				// }
				// }
				// }
				b = b & totalDao.save(sell);
				b = b & additionalproductSell(sell);
				if (b) {

					corePayableServer.goodsSell(sell);// 生成出库凭证
				}
				// 出库面积更新---判断仓区，成品面积有才更新仓区占地面积
				updateGoodHouseBySell(sell);
			}
			// 出库面积更新移至审批同意后
			goodsStoreServer.goodshuizong0("出库", null, sell);

			// 库存小于0.0001强制设置为0
			pushkc(null, g);
			b = b & totalDao.update(g);
			totalDao.update(sell);
			boolean bool = isneedcg(sell.getSellMarkId(), sell.getSellFormat(),
					sell.getBanBenNumber());
			if (bool) {
				// 添加手动下单信息;
				ManualOrderPlanDetail mod = new ManualOrderPlanDetail();
				mod.setMarkId(wgj.getMarkId());// 件号
				mod.setProName(wgj.getName());// 名称
				mod.setSpecification(wgj.getSpecification());// 规格
				mod.setBanben(wgj.getBanbenhao());// 版本号
				mod.setKgliao(wgj.getKgliao());// 供料属性
				mod.setTuhao(wgj.getTuhao());// 图号
				mod.setWgType(wgj.getWgType());// 物料类别
				mod.setUnit(wgj.getUnit());// 单位
				mod.setCgnumber(wgj.getCgcount());// 采购数量
				mod.setType("安全库存");// 添加方式
				mod.setAddTime(Util.getDateTime());// 添加时间
				mod.setRukuNum(0f);
				mod.setRemarks("件号" + wgj.getMarkId() + "零件名称" + wgj.getName()
						+ "总库存量低于安全库存" + wgj.getMinkc() + "系统自动下单;");
				totalDao.save(mod);
			}
			return true;
		} else {
			g = (Goods) totalDao.getObjectById(Goods.class, goods.getGoodsId());
			// 更换仓区-----判断成品库下是否更改仓区
			if ("成品库".equals(goods.getGoodsClass())) {
				if (goods.getGoodsClass().equals(g.getGoodsClass())
						&& goods.getGoodHouseName()
								.equals(g.getGoodHouseName())) {

				} else {// 仓区改变

					// 判断仓区，成品面积有才更新仓区占地面积

					// 更改前仓区
					GoodHouse goodHouseO = (GoodHouse) totalDao
							.get(
									"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
									new Object[] { g.getGoodsClass(),
											g.getGoodHouseName() });
					// 更改后仓区
					GoodHouse goodHouseN = (GoodHouse) totalDao
							.get(
									"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
									new Object[] { goods.getGoodsClass(),
											goods.getGoodHouseName() });

					// 单件
					ProcardTemplate procardArea = (ProcardTemplate) totalDao
							.get(
									"from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='') and productStyle='批产' ",
									new Object[] { goods.getGoodsMarkId() });

					// 前仓区
					if (goodHouseO != null && procardArea != null) {

						// 存在面积
						if (goodHouseO.getGoodAllArea() > 0
								&& goodHouseO.getGoodAllArea() != null
								&& procardArea.getActualUsedProcardArea() != null
								&& procardArea.getActualUsedProcardArea() > 0) {

							// 此次已用面积减少
							/*
							 * DoubleprocardActualUsed=procardArea.
							 * getActualUsedProcardArea
							 * ()*goods.getGoodsCurQuantity();
							 * 
							 * procardActualUsed=Double.valueOf(new
							 * DecimalFormat("0.00").format(procardActualUsed));
							 * 
							 * if(
							 * procardActualUsed>=goodHouseO.getGoodIsUsedArea
							 * ()){//占用大于或等于已满 goodHouseO.setGoodIsUsedArea(0D);
							 * goodHouseO
							 * .setGoodLeaveArea(goodHouseO.getGoodAllArea
							 * ()-goodHouseO.getGoodIsUsedArea()); }else{
							 * goodHouseO
							 * .setGoodIsUsedArea(goodHouseO.getGoodIsUsedArea
							 * ()-procardActualUsed);
							 * goodHouseO.setGoodLeaveArea
							 * (goodHouseO.getGoodAllArea
							 * ()-goodHouseO.getGoodIsUsedArea()); }
							 */
							Double procardActualUsed = procardArea
									.getActualUsedProcardArea()
									* goods.getGoodsCurQuantity();
							procardActualUsed = Double
									.valueOf(new DecimalFormat("0.00")
											.format(procardActualUsed));

							if (procardActualUsed >= goodHouseO
									.getGoodIsUsedArea()) {
								goodHouseO.setGoodIsUsedArea(0D);
								goodHouseO.setGoodLeaveArea(goodHouseO
										.getGoodAllArea()
										- goodHouseO.getGoodIsUsedArea());
							} else {
								Double use = Double.valueOf(new DecimalFormat(
										"0.00").format(goodHouseO
										.getGoodIsUsedArea()
										- procardActualUsed));
								goodHouseO.setGoodIsUsedArea(use);
								goodHouseO.setGoodLeaveArea(goodHouseO
										.getGoodAllArea()
										- goodHouseO.getGoodIsUsedArea());
							}
							totalDao.update(goodHouseO);
						}
					}
					// 后仓区面积减少
					if (goodHouseN != null && procardArea != null) {

						// 存在面积
						if (goodHouseN.getGoodAllArea() > 0
								&& goodHouseN.getGoodAllArea() != null
								&& procardArea.getActualUsedProcardArea() != null
								&& procardArea.getActualUsedProcardArea() > 0) {
							// 已用面积增加
							/*
							 * DoubleprocardActualUsed=procardArea.
							 * getActualUsedProcardArea
							 * ()*goods.getGoodsCurQuantity();
							 * procardActualUsed=Double.valueOf(new
							 * DecimalFormat("0.00").format(procardActualUsed));
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * 
							 * if((procardActualUsed+goodHouseN.getGoodIsUsedArea
							 * ())>=goodHouseN.getGoodAllArea()){//占用大于或等于已满
							 * goodHouseN
							 * .setGoodIsUsedArea(goodHouseN.getGoodAllArea
							 * ());//满
							 * goodHouseN.setGoodLeaveArea(goodHouseN.getGoodAllArea
							 * ()-goodHouseN.getGoodIsUsedArea()); }else{
							 * goodHouseN
							 * .setGoodIsUsedArea(goodHouseN.getGoodIsUsedArea
							 * ()+procardActualUsed);
							 * goodHouseN.setGoodLeaveArea
							 * (goodHouseN.getGoodAllArea
							 * ()-goodHouseN.getGoodIsUsedArea()); }
							 */
							Double procardActualUsed = procardArea
									.getActualUsedProcardArea()
									* goods.getGoodsCurQuantity();
							procardActualUsed = Double
									.valueOf(new DecimalFormat("0.00")
											.format(procardActualUsed
													+ goodHouseN
															.getGoodIsUsedArea()));

							goodHouseN.setGoodIsUsedArea(procardActualUsed);
							goodHouseN.setGoodLeaveArea(goodHouseN
									.getGoodAllArea()
									- goodHouseN.getGoodIsUsedArea());

							totalDao.update(goodHouseN);

						}
					}
				}
			}
			if (tag != null && tag.equals("updateGoodsInfor")) {
				BeanUtils.copyProperties(goods, g, new String[] {
						"goodsChangeTime", "goodsBeginQuantity",
						"goodsBarcode", "goodsMax", "goodsMin", "wgType",
						"goodsCurQuantity", "goodsPrice", "ywmarkId",
						"neiorderId", "waiorderId" });
			} else {
				BeanUtils.copyProperties(goods, g, new String[] {
						"goodsChangeTime", "goodsBeginQuantity",
						"goodsBarcode", "goodsMax", "goodsMin", "wgType",
						"goodsCurQuantity", "goodsPrice" });
			}
			GoodsStore goodsStore = 	(GoodsStore) totalDao.getObjectByCondition("from GoodsStore where goodsStoreMarkId = ? and goodsStoreLot =? " +
					" and goodsStoreWarehouse = ? ", g.getGoodsMarkId(),g.getGoodsLotId(),g.getGoodsClass());
			goodsStore.setGoodsStorenexttime(g.getGoodsnexttime());
			totalDao.update(goodsStore);
			b = totalDao.update(g);
			
		}
		AttendanceTowServerImpl.updateJilu(1, goods, goods.getGoodsId(), g
				.getGoodsMarkId());
		return b;

	}

	/**
	 * 成品库出库额外流程
	 * 
	 * @return
	 */
	@Override
	public boolean additionalproductSell(Sell sell) {
		Users user = Util.getLoginUser();
		boolean b = false;
		String isNeed = null;
		if (sell.getOrderNum() == null && sell.getOutOrderNumer() == null) {
			isNeed = "否";
		} else if (sell.getOrderNum() == null
				&& sell.getOutOrderNumer() != null) {
			isNeed = "外";
		} else if (sell.getOrderNum() != null
				&& sell.getOutOrderNumer() == null) {
			isNeed = "内";
		} else {
			isNeed = "全";
		}
		if ((sell.getSellWarehouse().equals("成品库")) && isNeed != null
				&& (isNeed.equals("全") || isNeed.equals("外"))) {
			// 成品出库
			TaHkHuikuan hk = null;
			List<TaHkHuikuan> hkList = totalDao
					.query(
							" from TaHkHuikuan where hkStatus='未核对' and id in("
									+ "select taHkHuikuan.id from TaHkSellSta where hkSellSendId=?)",
							sell.getSellSendnum());
			Set<TaHkSellSta> taHkSellStas = null;
			if (hkList.size() > 0) {
				hk = hkList.get(0);
				taHkSellStas = hk.getTaHkSellStas();
			} else {
				hk = new TaHkHuikuan();
				taHkSellStas = new HashSet<TaHkSellSta>();
				String curMon = Util.getDateTime("yyyyMM");
				String hql = "select max(hkNum) from TaHkHuikuan where hkNum like '%"
						+ curMon + "%'";
				String huNum = "";
				List<String> list = totalDao.query(hql);
				if (null != list && null != list.get(0) && list.size() > 0) {
					String maxNum = list.get(0);
					int newNum = Integer.parseInt(maxNum.substring(4)) + 1;
					huNum = "shLD" + String.valueOf(newNum);
				} else {
					huNum = "shLD" + curMon + "001";
				}
				hk.setHkNum(huNum);
				sell.setSellHkId(hk.getHkNum());
			}
			// 为属性赋值
			hk.setHkApplyDate(Util.getDateTime());
			hk.setHkApplier(user.getName());
			// 开票钱审核流程
			hk.setHkPreNotPath("LD");
			hk.setHkRelNotPath("FD");
			// 开票后审核流程
			hk.setHkInvoPrePath("FD");
			hk.setHkInvoRelPath("MD");
			// 汇款追踪审核流程
			hk.setHkBackAuditA("MD");
			hk.setHkBackAuditB("FD");
			hk.setHkStatus("未核对");
			// hk.setHkNoticeFile(attachmentName);
			hk.setHkTrackRate(0f);
			hk.setHkClientName(sell.getSellCompanyPeople());// 客户负责人
			hk.setHkZhuikuanren(sell.getSellCompanyPeople());// 设置追款人=客户负责人
			// 添加关联的明细表

			b = totalDao.save(hk);
			boolean hasSta = false;
			if (taHkSellStas != null && taHkSellStas.size() > 0) {
				for (TaHkSellSta hkSell : taHkSellStas) {
					if (isNeed.equals("全")) {
						if (hkSell.getHkSellMarkId().equals(
								sell.getSellMarkId())
								&& hkSell.getHkSellSendId().equals(
										sell.getSellSendnum())
								&& hkSell.getHkSellOrderId() != null
								&& hkSell.getHkSellOrderId().equals(
										sell.getOrderNum())
								&& hkSell.getHkSellOutOrderId().equals(
										sell.getOutOrderNumer())) {
							hasSta = true;
							if (hkSell.getApplyCount() == null) {
								hkSell.setApplyCount(sell.getSellCount());
							} else {
								hkSell.setApplyCount(hkSell.getApplyCount()
										+ sell.getSellCount());
							}
							if (hkSell.getHkSelfCard() == null) {
								hkSell.setHkSelfCard(sell.getSellLot());
							} else {
								hkSell.setHkSelfCard(hkSell.getHkSelfCard()
										+ "," + sell.getSellLot());
							}
							break;
						}
					} else {
						if (hkSell.getHkSellMarkId().equals(
								sell.getSellMarkId())
								&& hkSell.getHkSellSendId().equals(
										sell.getSellSendnum())
								&& hkSell.getHkSellOrderId() == null
								&& sell.getOrderNum() == null
								&& hkSell.getHkSellOutOrderId().equals(
										sell.getOutOrderNumer())) {
							hasSta = true;
							if (hkSell.getApplyCount() == null) {
								hkSell.setApplyCount(sell.getSellCount());
							} else {
								hkSell.setApplyCount(hkSell.getApplyCount()
										+ sell.getSellCount());
							}
							if (hkSell.getHkSelfCard() == null) {
								hkSell.setHkSelfCard(sell.getSellLot());
							} else {
								hkSell.setHkSelfCard(hkSell.getHkSelfCard()
										+ "," + sell.getSellLot());
							}
							break;
						}
					}

				}
			}
			if (!hasSta) {
				TaHkSellSta hkSell = new TaHkSellSta();
				hkSell.setTaHkHuikuan(hk);
				hkSell.setHkSellNum(hk.getHkNum());
				hkSell.setHkSellSendId(sell.getSellSendnum());// 送货单号
				hkSell.setHkSellMarkId(sell.getSellMarkId());// 件号
				hkSell.setHkSelfCard(sell.getSellLot());// 批次
				hkSell.setHkSellGoods(sell.getSellGoods());// 品名
				hkSell.setHkSellCumpanyName(sell.getSellCompanyName());// 客户
				hk.setHkClientComp(sell.getSellCompanyName());
				if (isNeed.equals("全")) {
					hkSell.setHkSellOrderId(sell.getOrderNum());// 内部订单号
				}
				hkSell.setHkSellOutOrderId(sell.getOutOrderNumer());// 外部订单号
				hkSell.setHksellTime(Util.getDateTime());// 添加送货单时间
				hkSell.setHksellUsername(user.getName());// 添加人员姓名
				hkSell.setApplyCount(sell.getSellCount());// 申请开票数量
				// hkSell.setHkSellCount(sell.getSellCount());//数量
				taHkSellStas.add(hkSell);
				hk.setTaHkSellStas(taHkSellStas);
			}
			b = b & totalDao.update(hk);
			String addSql2 = null;
			String ywMarkId = sell.getYwmarkId();
			if (ywMarkId == null || "".equals(ywMarkId)) {
				ywMarkId = (String) totalDao
						.getObjectByCondition(
								"select ywMarkId from ProcardTemplate where procardStyle='总成' and markId=?",
								sell.getSellMarkId());
			}
			String addSql = null;
			if (ywMarkId != null && ywMarkId.length() > 0) {
				addSql = "and (pieceNumber=? or pieceNumber='" + ywMarkId
						+ "')";
				addSql2 = "and (partNumber=? or partNumber='" + ywMarkId + "')";
			} else {
				addSql = "and pieceNumber=?";
				addSql2 = "and partNumber=?";
			}

			// 内部计划的出库数量
			InternalOrderDetail iod2 = (InternalOrderDetail) totalDao
					.getObjectByCondition(
							"from InternalOrderDetail where 1=1 "
									+ addSql
									+ " and internalOrder in (select planOrderId from Procard where markId=?"
									+ " and selfCard=? and productStyle='批产')",
							sell.getSellMarkId(), sell.getSellMarkId(), sell
									.getSellLot());
			if (iod2 != null) {
				if (iod2.getSellCount() == null) {
					iod2.setSellCount(0f);
				}
				iod2.setSellCount((iod2.getSellCount() + sell.getSellCount()));
				totalDao.update(iod2);
			}
			// 有内部订单号,根据内部订单号和件号去填补
			if (sell.getSellWarehouse().equals("成品库")) {
				ProductManager product = (ProductManager) totalDao
						.getObjectByQuery(
								"from ProductManager where orderManager.orderNum=? and allocationsNum is not null and status <> '取消' "
										+ addSql, sell.getOrderNum(), sell
										.getSellMarkId());
				if (product != null) {
					if (product.getApplyNumber() == null) {
						product.setApplyNumber(sell.getSellCount());
					} else {
						product.setApplyNumber((product.getApplyNumber() + sell
								.getSellCount()));
					}
					if (product.getSellCount() == null) {
						product.setSellCount(0f);
					}
					product.setSellCount((product.getSellCount() + sell
							.getSellCount()));// 订单完成数量
					if (product.getAllocationsNum().floatValue() == product
							.getNum().floatValue()) {// 订单产品完成
						String hql = "from ProductManager where orderManager.id=? and allocationsNum<num";
						int count = totalDao.getCount(hql, product
								.getOrderManager().getId());
						if (count == 0) {// 订单对应的产品都已交付
							OrderManager orderManager = (OrderManager) totalDao
									.getObjectById(OrderManager.class, product
											.getOrderManager().getId());
							orderManager.setDeliveryStatus("是");
							totalDao.update(orderManager);
						}

					}
					String paymentDate = product.getOrderManager()
							.getPaymentDate();
					if (paymentDate != null) {// 订单及时率
						if (Util.compareTime(paymentDate, "yyyy-MM-dd", sell
								.getSellDate(), "yyyy-MM-dd")) {
							// 完成时间在前
							if (product.getTimeNum() == null) {
								product.setTimeNum(0f);
							}
							product.setTimeNum((product.getTimeNum() + sell
									.getSellCount()));
						}
					}
					// 计算订单交付率
					Integer orderId = product.getOrderManager().getId();
					String hqljfr = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					String hqljfr2 = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					Float pronum = (Float) totalDao.getObjectByCondition(
							hqljfr, orderId);
					Float proallocationsNum = (Float) totalDao
							.getObjectByCondition(hqljfr2, orderId);
					OrderManager orderManager = (OrderManager) totalDao
							.getObjectById(OrderManager.class, orderId);
					orderManager.setCompletionrate(proallocationsNum / pronum
							* 100);
					totalDao.update(orderManager);
					b = b & totalDao.update(product);
				}
			}

			// 计算项目交付数量计算项目盈亏
			QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition(
					"from QuotedPrice where markId=? and procardStyle='总成'",
					sell.getSellMarkId());
			Float haschukuCount = (Float) totalDao.getObjectByCondition(
					"select sum(sellCount) from Sell where sellMarkId=? ", sell
							.getSellMarkId());
			if (haschukuCount == null) {
				haschukuCount = 0f;
			}
			if (qp != null) {
				// if(qp.getJfcount()==null){
				// qp.setJfcount( sell.getSellCount());
				// }else{
				// qp.setJfcount(qp.getJfcount()+ sell.getSellCount());
				// }
				// 不确定
				String date = Util.getDateTime("yyyy-MM-dd");
				Float singSell = null;
				Double d = (Double) totalDao
						.getObjectByCondition(
								"select hsPrice from Price where 1=1 "
										+ addSql2
										+ " and pricePeriodStart<=? and (pricePeriodEnd is null or pricePeriodEnd>=?)",
								qp.getMarkId(), date, date);
				if (d != null) {// 有销售价格
					singSell = Float.parseFloat(d.toString());
					if (singSell == null) {
						singSell = 0f;
					}
					Float singBj = (Float) totalDao
							.getObjectByCondition(
									"select sum(money) from ProjectTime where quoId=? and classNumber !='gzf' ",
									qp.getId());
					if (singBj == null) {
						singBj = 0f;
					}
					Float singlr = singSell - singBj;// 获取单间利润
					String sqlstatus = " 1=1";
					Float money1 = (Float) totalDao.getObjectByCondition(
							"select sum(money) from ProjectTime where quoId=?",
							qp.getId());
					if (money1 == null) {
						money1 = 0f;// 公司报价投入
					}
					Procard procard = (Procard) totalDao.getObjectByCondition(
							"from Procard where markId=? and selfCard=?", sell
									.getSellMarkId(), sell.getSellLot());
					if (procard != null) {
						if (procard.getProductStyle().equals("试制")) {
							// 判断是否为首件
							String type = (String) totalDao
									.getObjectByCondition(
											"select type from ProjectOrderPart where markId=? and  projectOrder.id =?",
											procard.getMarkId(), procard
													.getPlanOrderId());
							if (type != null && type.equals("首件")) {
								sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段')";
								if (qp.getStatus() != null
										&& !qp.getStatus().equals("批产申请中")
										&& !qp.getStatus().equals("批产阶段")) {
									qp.setStatus("试制阶段");
								}
							} else {
								sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段','试制申请中','试制阶段')";
								if (qp.getStatus() != null) {
									qp.setStatus("批产阶段");
								}
							}
						} else {
							sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段','试制申请中','试制阶段','批产申请中','批产阶段') and addTime<='"
									+ procard.getProcardTime() + "'";
							qp.setStatus("批产阶段");
						}
						// （单间成本*累计数量-公司当期总投入+个人总投入）*个人当期总投入/（个人当期总投入+公司当期总投入）
						// Float gsmoney = (Float)
						// totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where "+sqlstatus+" and qpId=?  and applyStatus='同意'",
						// qp.getId());
						// if(gsmoney==null){//公司当期总投入
						// gsmoney = money1;
						// }else{
						// gsmoney+=money1;
						// }
						Double gsmoney = qp.getRealAllfy();
						if (gsmoney == null) {
							gsmoney = 0d;
						}
						Float grmoney = (Float) totalDao
								.getObjectByCondition(
										"select sum(tzMoney) from QuotedPriceUserCost where "
												+ sqlstatus
												+ " and qpId=? and applyStatus='同意' and tzStatus!='撤资'",
										qp.getId());
						if (grmoney == null) {// 个人当期总投入
							grmoney = 0f;
						}
						Double hlmoney = (singlr * haschukuCount - gsmoney + grmoney)
								* grmoney / (gsmoney + grmoney);// 项目累计收益
						Double xmflmoney = 0d;// 分红
						qp.setYingkui(hlmoney);
						if (hlmoney > 0) {
							xmflmoney = hlmoney;
							if (qp.getFhmoney() != null && qp.getFhmoney() > 0) {
								xmflmoney = xmflmoney - qp.getFhmoney();// 总收益-之前分红金额（包含公司）
								qp.setFhmoney(Float.parseFloat((xmflmoney + qp
										.getFhmoney())
										+ ""));

							} else {
								qp.setFhmoney(Float.parseFloat(xmflmoney + ""));
							}
							List<QuotedPriceUserCost> grCostList = totalDao
									.query(
											"from QuotedPriceUserCost where "
													+ sqlstatus
													+ " and qpId=? and applyStatus='同意' and tzStatus!='撤资'",
											qp.getId());
							if (grCostList.size() > 0) {
								for (QuotedPriceUserCost grCost : grCostList) {
									QuotedPriceFenhong qpfh = new QuotedPriceFenhong();
									qpfh.setQpucId(grCost.getId());// 对应投资id
									qpfh.setProStatus(grCost.getProStatus());// 项目阶段
									qpfh.setMoney(Float.parseFloat(xmflmoney
											* grCost.getTzMoney() / gsmoney
											+ ""));// 分红金额
									qpfh.setAddTime(Util.getDateTime());// 分红时间
									qpfh.setUserName(grCost.getUserName());// 名称
									qpfh.setUserCode(grCost.getUserCode());// 工号
									qpfh.setDept(grCost.getDept());// 部门
									qpfh.setMarkId(qp.getMarkId());// 件号
									totalDao.save(qpfh);
									if (grCost.getKlMoney() == null) {
										grCost.setKlMoney(qpfh.getMoney());
									} else {
										grCost.setKlMoney(qpfh.getMoney()
												+ grCost.getKlMoney());
									}
									totalDao.update(grCost);
								}
							}
						}
						totalDao.update(qp);
					}
				}
			}
			// 计算项目交付数量计算项目盈亏结束
		}
		return b;
	}

	public String getWarehouseList(String type) {
		String typeSql = null;
		if (type == null || type.equals("")) {
			typeSql = " and (type is null or type='仓库')";
		} else {
			typeSql = " and type='" + type + "'";
		}
		Users user = (Users) Util.getLoginUser();
		String hql = "from WareHouseAuth where usercode = ?" + typeSql;
		WareHouseAuth a = (WareHouseAuth) totalDao.getObjectByCondition(hql,
				new Object[] { user.getCode() });
		if (a == null) {
			return "''";
		}
		String s = a.getAuth();
		List<String> strList = new ArrayList<String>();
		String[] strArr = s.split(",");
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].endsWith("_" + WareHouseAuthServiceImpl.VIEW)) {
				strList.add(strArr[i].substring(0, strArr[i].indexOf('_')));
			}
		}
		List<WareHouse> list = totalDao.find("from WareHouse");
		for (WareHouse wareHouse : list) {
			Collections.replaceAll(strList, wareHouse.getCode(), wareHouse
					.getName());
		}
		String warehouse = "";
		for (String str : strList) {
			warehouse += ",'" + str + "'";
		}
		if (warehouse.length() > 0) {
			return warehouse.substring(1);
		}
		return "'test'";

	}

	public String findSelectList(String tag) {
		String message = "";
		List<String> li = new ArrayList<String>();
		if (null != tag && !"".equals(tag)) {
			if ("goodsClass".equals(tag)) {
				Users user = (Users) Util.getLoginUser();
				String hql = "from WareHouseAuth where usercode = ? and (type is null or type !='价格')";
				WareHouseAuth a = (WareHouseAuth) totalDao
						.getObjectByCondition(hql, new Object[] { user
								.getCode() });
				if (a == null) {
					return "";
				}
				String s = a.getAuth();
				List<String> strList = new ArrayList<String>();
				String[] strArr = s.split(",");
				for (int i = 0; i < strArr.length; i++) {
					if (strArr[i].endsWith("_" + WareHouseAuthServiceImpl.VIEW)) {
						strList.add(strArr[i].substring(0, strArr[i]
								.indexOf('_')));
					}
				}
				List<WareHouse> list = totalDao.find("from WareHouse");
				for (WareHouse wareHouse : list) {
					Collections.replaceAll(strList, wareHouse.getCode(),
							wareHouse.getName());
				}
				li = strList;
			}
			for (String d : li) {
				message += d.toString() + "|";
			}
		}
		return message;
	}

	public Object[] huizong(Goods goods, String startDate, String endDate,
			Integer cpage, Integer pageSize, String[] tiaojian) {
		String kubie = getWarehouseList(null);
		String select_hql = "";
		String select_hql_group = "";
		String select_goodsstore_hql = "";
		String select_sell_hql = "";
		if (tiaojian != null && tiaojian.length > 0) {
			for (int i = 0; i < tiaojian.length; i++) {
				if (null != tiaojian[i] && tiaojian[i].length() > 0) {
					if ("qichu".equals(tiaojian[i])) {
						String lastmonth=Util.getLastMonth("yyyy-MM-31 23:59:59");
						select_hql += ",((select sum(goodsStoreCount) from GoodsStore where  goodsStoreMarkId=g.goodsMarkId and  goodsStoreWarehouse=g.goodsClass "
								+ "and (status is null or status = '入库') and goodsStoreTime <= '"+lastmonth+"' "
								+ select_goodsstore_hql
								+ ") -(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellWarehouse=g.goodsClass"
								+ " and sellTime <= '"+lastmonth+"'   "
								+ select_sell_hql
								+ " and (handwordSellStatus is null or handwordSellStatus not in ('打回','未审批','审批中')))) as sumqichu";
					} else {
						select_hql += "," + tiaojian[i];
						select_hql_group += ",IFNULL(" + tiaojian[i] + ",'')";
						if ("goodsPosition".equals(tiaojian[i])) {
							select_goodsstore_hql += " and ifnull(goodsStorePosition,'')=ifnull(g."
									+ tiaojian[i] + ",'')";
							select_sell_hql += " and ifnull(kuwei,'')=ifnull(g."
									+ tiaojian[i] + ",'')";
						} else {
							select_goodsstore_hql += " and ifnull("
									+ tiaojian[i] + ",'')=ifnull(g."
									+ tiaojian[i] + ",'')";
							select_sell_hql += " and ifnull(" + tiaojian[i]
									+ ",'')=ifnull(g." + tiaojian[i] + ",'')";
						}
					}
				}
			}
		}
		String hql = "";
		String hqlSumIn = "";
		String hqlSumOut = "";
		String hqlSumG = "";
		String hqlCount = "";
		if (null != goods) {
			if (null != goods.getGoodsMarkId()
					&& !"".equals(goods.getGoodsMarkId())) {
				hqlSumG += " and goodsMarkId like'%" + goods.getGoodsMarkId()
						+ "%'";
				hqlCount += " and goodsMarkId like'%" + goods.getGoodsMarkId()
						+ "%'";
			}
			if (null != goods.getGoodsFormat()
					&& !"".equals(goods.getGoodsFormat())) {
				hqlSumG += " and goodsFormat like'%" + goods.getGoodsFormat()
						+ "%'";
				hqlCount += " and goodsFormat like'%" + goods.getGoodsFormat()
						+ "%'";
			}
			if (null != goods.getGoodsClass()
					&& !"".equals(goods.getGoodsClass())) {
				hqlSumG += " and goodsClass ='" + goods.getGoodsClass() + "'";
				hqlCount += " and goodsClass ='" + goods.getGoodsClass() + "'";
			}
			if (null != goods.getGoodHouseName()
					&& !"".equals(goods.getGoodHouseName())) {
				hqlSumG += " and goodHouseName like'%"
						+ goods.getGoodHouseName() + "%'";
				hqlCount += " and goodHouseName like'%"
						+ goods.getGoodHouseName() + "%'";
			}
			if (null != goods.getGoodsPosition()
					&& !"".equals(goods.getGoodsPosition())) {
				hqlSumG += " and goodsPosition like'%"
						+ goods.getGoodsPosition() + "%'";
				hqlCount += " and goodsPosition like'%"
						+ goods.getGoodsPosition() + "%'";
			}
			if (null != goods.getGoodsFullName()
					&& !"".equals(goods.getGoodsFullName())) {
				hqlSumG += " and goodsFullName like '%"
						+ goods.getGoodsFullName() + "%'";
				hqlCount += " and goodsFullname like '%"
						+ goods.getGoodsFullName() + "%'";
			}
			if (goods.getGoodHouseName() != null
					&& goods.getGoodHouseName().length() > 0) {
				hqlSumG += " and goodHouseName like'%"
						+ goods.getGoodHouseName() + "%'";
				hqlCount += " and goodHouseName like'%"
						+ goods.getGoodHouseName() + "%'";
			}
			if (null != goods.getYwmarkId() && !"".equals(goods.getYwmarkId())) {
				hqlSumG += " and ywmarkId like'%" + goods.getYwmarkId() + "%'";
				hqlCount += " and ywmarkId like'%" + goods.getYwmarkId() + "%'";
			}
		}

		/****** 日期查询 ********/
		if (null != startDate && !"".equals(startDate)) {
			hqlSumIn += " and goodsStoreTime >= '" + startDate + "'";
			hqlSumOut += " and sellTime >= '" + startDate + "'";
		}
		if (null != endDate && !"".equals(endDate)) {
			hqlSumIn += " and goodsStoreTime <= '" + endDate + "'";
			hqlSumOut += " and sellTime <= '" + endDate + "'";
		}

		hql = "select goodsMarkId,"
				+ "(select goodsFullName from Goods where goodsId=(select max(goodsId) from Goods where goodsMarkId=g.goodsMarkId)),"
				+ "(select goodsFormat from Goods where goodsId=(select max(goodsId) from Goods where goodsMarkId=g.goodsMarkId)),"
				+ "(select goodsUnit from Goods where goodsId=(select max(goodsId) from Goods where goodsMarkId=g.goodsMarkId)),"
				+ "goodsClass,sum(goodsCurQuantity),"
				+ "(select sum(goodsStoreCount) from GoodsStore where  goodsStoreMarkId=g.goodsMarkId and  goodsStoreWarehouse=g.goodsClass and (status is null or status = '入库')"
				+ hqlSumIn
				+ select_goodsstore_hql
				+ ") as sumOut,"
				+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellWarehouse=g.goodsClass    "
				+ hqlSumOut
				+ select_sell_hql
				+ " and (handwordSellStatus is null or handwordSellStatus not in ('打回','未审批','审批中'))) as sumIn"
				+ select_hql + " from Goods g where goodsClass in(" + kubie
				+ ")" + hqlSumG + "  GROUP BY goodsMarkId,goodsClass"
				+ select_hql_group;

		Object[] huizong = new Object[2];
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		// List list0 = totalDao.query(hql);
		List list0 = null;
		int count = 0;
		if (list0 != null && list0.size() > 0) {
			count = list0.size();
		}
		huizong[0] = count;
		huizong[1] = list;
		return huizong;
	}

	/**
	 * 导出月度汇总数据
	 * 
	 * @return
	 */
	@Override
	public String[] getseDate() {
		String startDate = "";
		String endDate = Util.getDateTime("yyyy-MM-") + "25 09:00:00";
		String year = Util.getDateTime("yyyy");
		String MM = Util.getDateTime("MM");
		int yearInt = Integer.parseInt(year);
		int MMint = Integer.parseInt(MM);
		if (1 == MMint) {
			yearInt -= 1;
			MMint = 12;
		} else {
			MMint -= 1;
		}

		if (MMint < 10) {
			startDate = yearInt + "-0" + MMint + "-25 24:00:00";
		} else {
			startDate = yearInt + "-" + MMint + "-25 24:00:00";
		}
		String[] obj = { startDate, endDate };
		return obj;
	}
	public void exportExcel(Goods goods, String startDate, String endDate,
			String tag) {
		String kubie = getWarehouseList(null);
		String hql= "";
		String hql2 = "from Goods s where goodsClass in(" + kubie + ")";
		String hqlSumIn = "";
		String hqlSumOut = "";
		String hqlSumG = "";
		String hqlCount = "";
		if (null != goods) {
			if (null != goods.getGoodsMarkId()
					&& !"".equals(goods.getGoodsMarkId())) {
				/*
				 * hqlSumIn += " and goodsStoreMarkId='" +
				 * goods.getGoodsMarkId() + "'"; hqlSumOut +=
				 * " and sellMarkId='" + goods.getGoodsMarkId() + "'";
				 */
				hqlSumG += " and goodsMarkId like'%"
						+ goods.getGoodsMarkId() + "%'";
				hqlCount += " and goodsMarkId like'%"
						+ goods.getGoodsMarkId() + "%'";
			}
			if (null != goods.getGoodsFormat()
					&& !"".equals(goods.getGoodsFormat())) {
				/*
				 * hqlSumIn += " and goodsStoreFormat='" +
				 * goods.getGoodsFormat() + "'"; hqlSumOut +=
				 * " and sellFormat='" + goods.getGoodsFormat() + "'";
				 */hqlSumG += " and goodsFormat like'%"
						+ goods.getGoodsFormat() + "%'";
				hqlCount += " and goodsFormat like'%"
						+ goods.getGoodsFormat() + "%'";
			}
			if (null != goods.getGoodsClass()
					&& !"".equals(goods.getGoodsClass())) {
				/*
				 * hqlSumIn += " and goodsStoreWarehouse='" +
				 * goods.getGoodsClass() + "'"; hqlSumOut +=
				 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
				 */hqlSumG += " and goodsClass='" + goods.getGoodsClass()
						+ "'";
				hqlCount += " and goodsClass='" + goods.getGoodsClass()
						+ "'";
			}
			if (null != goods.getGoodHouseName()
					&& !"".equals(goods.getGoodHouseName())) {
				/*
				 * hqlSumIn += " and goodsStoreWarehouse='" +
				 * goods.getGoodsClass() + "'"; hqlSumOut +=
				 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
				 */hqlSumG += " and goodHouseName='"
						+ goods.getGoodHouseName() + "'";
				hqlCount += " and goodHouseName='"
						+ goods.getGoodHouseName() + "'";
			}
			if (null != goods.getGoodsPosition()
					&& !"".equals(goods.getGoodsPosition())) {
				/*
				 * hqlSumIn += " and goodsStoreWarehouse='" +
				 * goods.getGoodsClass() + "'"; hqlSumOut +=
				 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
				 */hqlSumG += " and goodsPosition='"
						+ goods.getGoodsPosition() + "'";
				hqlCount += " and goodsPosition='"
						+ goods.getGoodsPosition() + "'";
			}
			if (null != goods.getGoodsFullName()
					&& !"".equals(goods.getGoodsFullName())) {
				/*
				 * hqlSumIn += " and goodsStoreGoodsName='" +
				 * goods.getGoodsFullName() + "'"; hqlSumOut +=
				 * " and sellGoods='" + goods.getGoodsFullName()+ "'";
				 */
				hqlSumG += " and goodsFullName='"
						+ goods.getGoodsFullName() + "'";
				hqlCount += " and goodsFullname='"
						+ goods.getGoodsFullName() + "'";
			}
			if (goods.getGoodHouseName() != null
					&& goods.getGoodHouseName().length() > 0) {
				hqlSumG += " and goodHouseName='"
						+ goods.getGoodHouseName() + "'";
				hqlCount += " and goodHouseName='"
						+ goods.getGoodHouseName() + "'";
			}
			hql = "select goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName,sum(goodsCurQuantity),"
					+ "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.goodsMarkId and  goodsStoreWarehouse=g.goodsClass   and goodsStoreFormat = g.goodsFormat"
					+ hqlSumIn
					+ " and goodsStoreWarehouse not in ('待检库','不合格品库') ) as sumOut,"
					+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit=g.goodsUnit and sellWarehouse=g.goodsClass    "
					+ hqlSumOut
					+ "  and sellWarehouse not in ('待检库','不合格品库') ) as sumIn"
					+ " from Goods g where goodsClass in("
					+ kubie
					+ ")"
					+ hqlSumG
					+ "  and goodsClass not in ('待检库','不合格品库')  GROUP BY goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName";
			hql2 += hqlCount;
		}
		if (null != startDate && !"".equals(startDate) && null != endDate
				&& !"".equals(endDate)) {
			hqlSumIn += " and goodsStoreTime between '" + startDate
					+ "' and '" + endDate + "'";
			hqlSumOut += " and sellTime between '" + startDate + "' and '"
					+ endDate + "'";
			//入库
			String hql_In = "select  from goodsStore,sell where ";
			hql = "select goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName,sum(goodsCurQuantity),"
					+ "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.goodsMarkId and goodsStoreWarehouse=g.goodsClass  and goodsStoreFormat = g.goodsFormat"
					+ hqlSumIn
					+ " and goodsStoreWarehouse not in ('待检库','不合格品库') ) as sumOut,"
					+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit=g.goodsUnit and sellWarehouse=g.goodsClass "
					+ hqlSumOut
					+ "  and sellWarehouse not in ('待检库','不合格品库') ) as sumIn"
					+ " from Goods g where goodsClass in("
					+ kubie
					+ ")  "
					+ hqlSumG
					+ "  and goodsClass not in ('待检库','不合格品库') GROUP BY goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName";
		}
		
	}
	/****
	 * 库存盘点导出
	 * 
	 * @param list
	 */
	@Override
	public void exportGoodsPandian(List list, String[] tiaojian) {// 汇总数据导出
		try {

			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			if (list.size() > 60000) {
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String("库存汇总".getBytes("GB2312"),
										"8859_1") + ".xlsx");
			} else {
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String("库存汇总".getBytes("GB2312"),
										"8859_1") + ".xls");
			}
			response.setContentType("application/msexcel");

			SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
			org.apache.poi.ss.usermodel.Sheet sheet = workBook
					.createSheet("库存盘点表");
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
			cell.setCellValue("库存盘点表");
			cell.setCellStyle(style);

			row = sheet.createRow(1);
			cell = row.createCell(0, CellType.STRING);
			cell.setCellValue("序号");
			cell = row.createCell(1, CellType.STRING);
			cell.setCellValue("件号");
			cell = row.createCell(2, CellType.STRING);
			cell.setCellValue("品名");
			cell = row.createCell(3, CellType.STRING);
			cell.setCellValue("规格");
			cell = row.createCell(4, CellType.STRING);
			cell.setCellValue("单位");
			cell = row.createCell(5, CellType.STRING);
			cell.setCellValue("库别");

			Integer len = 0;
			if (tiaojian != null && tiaojian.length > 0) {
				len = tiaojian.length;
				Map<String, String> tiaojianmap = new HashMap<String, String>();
				tiaojianmap.put("ywmarkId", "业务件号");
				tiaojianmap.put("goodHouseName", "仓区");
				tiaojianmap.put("goodsPosition", "库位");
				tiaojianmap.put("banBenNumber", "版本");
				tiaojianmap.put("kgliao", "供料属性");
				tiaojianmap.put("qichu", "期初");
				for (int i = 0; i < len; i++) {
					cell = row.createCell(5 + (i + 1), CellType.STRING);
					cell.setCellValue(tiaojianmap.get(tiaojian[i]));
				}
			}
			cell = row.createCell(6 + len, CellType.STRING);
			cell.setCellValue("入库累计(A)");
			cell = row.createCell(7 + len, CellType.STRING);
			cell.setCellValue("出库累计(B)");
			cell = row.createCell(8 + len, CellType.STRING);
			cell.setCellValue("理论期末结余(C=A-B)");
			cell = row.createCell(9 + len, CellType.STRING);
			cell.setCellValue("即时库存结余(D)");
			cell = row.createCell(10 + len, CellType.STRING);
			cell.setCellValue("异常 (C≠D)");

			int lineNumber = 0;
			for (int i = 0; i < list.size(); i++) {
				Object[] o = (Object[]) list.get(i);
				lineNumber++;

				row = sheet.createRow(lineNumber + 1);
				cell = row.createCell(0, CellType.STRING);
				cell.setCellValue(lineNumber);
				cell = row.createCell(1, CellType.STRING);
				cell.setCellValue((String) o[0]);
				cell = row.createCell(2, CellType.STRING);
				cell.setCellValue((String) o[1]);
				cell = row.createCell(3, CellType.STRING);
				cell.setCellValue((String) o[2]);
				cell = row.createCell(4, CellType.STRING);
				cell.setCellValue((String) o[3]);
				cell = row.createCell(5, CellType.STRING);
				cell.setCellValue((String) o[4]);
				if (tiaojian != null && tiaojian.length > 0) {
					for (int j = 0; j < len; j++) {
						try {
							cell = row.createCell(5 + (j + 1), CellType.STRING);
							Object oo=o[7 + j];
							cell.setCellValue(oo.toString() );
						} catch (Exception e) {
							cell = row.createCell(5 + (j + 1), CellType.STRING);
							cell.setCellValue("");
						}
					}
				}
				// 计算理论库存
				Double rukucount = 0D;
				try {
					rukucount = (double) Math.round(Double.parseDouble(o[6]
							.toString()) * 10000) / 10000;
				} catch (Exception e) {
				}
				Double chukucount = 0D;
				try {
					chukucount = (double) Math.round(Double.parseDouble(o[7]
							.toString()) * 10000) / 10000;
				} catch (Exception e) {
				}
				Double kucuncount = 0D;
				try {
					kucuncount = (double) Math.round(Double.parseDouble(o[5]
							.toString()) * 10000) / 10000;
				} catch (Exception e) {
				}
				Double lljieyu = (double) Math
						.round((rukucount - chukucount) * 10000) / 10000;// 理论库存
				Double chayicount = (double) Math
						.round((lljieyu - kucuncount) * 10000) / 10000;
				cell = row.createCell(6 + len, CellType.STRING);
				cell.setCellValue(rukucount);
				cell = row.createCell(7 + len, CellType.STRING);
				cell.setCellValue(chukucount);
				cell = row.createCell(8 + len, CellType.STRING);
				cell.setCellValue(lljieyu);
				cell = row.createCell(9 + len, CellType.STRING);
				cell.setCellValue(kucuncount);
				cell = row.createCell(10 + len, CellType.STRING);
				cell.setCellValue(chayicount);

			}
			workBook.write(os);
			workBook.close();// 记得关闭工作簿
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void getexportExcel(Goods goods, String startDate, String endDate,
			String tag, Integer goodsAge, String pageStatus) {
		if ("sum".equals(tag)) {// 汇总数据导出
			String kubie = getWarehouseList(null);
			String hql = "select goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName,sum(goodsCurQuantity),"
					+ "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.goodsMarkId and goodsStoreFormat=g.goodsFormat"
					+ " and goodsStoreUnit=g.goodsUnit and goodsStoreWarehouse=g.goodsClass   and goodsStoreWarehouse not in ('待检库','不合格品库') ) as sumOut,"
					+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit="
					+ "g.goodsUnit and sellWarehouse=g.goodsClass  and sellWarehouse not in ('待检库','不合格品库') ) as sumIn "
					+ "from Goods g where goodsClass in("
					+ kubie
					+ ") and goodsClass not in ('待检库','不合格品库')"
					+ " GROUP BY goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName";

			/*
			 * Stringhql=
			 * "select id.goodsMarkId,id.goodsFullname,id.goodsFormat,id.goodsUnit,id.goodsClass,id.sumCount,"
			 * +
			 * "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.id.goodsMarkId and goodsStoreFormat=g.goodsFormat"
			 * +
			 * " and goodsStoreUnit=g.goodsUnit and goodsStoreWarehouse=g.goodsClass ) as sumIn,"
			 * +
			 * "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit="
			 * + "g.goodsUnit and sellWarehouse=g.goodsClass  ) as sumOut " +
			 * "from SumGoods g where id.goodsClass in("+ kubie+ ") ";
			 */
			String hql2 = "from Goods s where goodsClass in(" + kubie + ")";
			String hqlSumIn = "";
			String hqlSumOut = "";
			String hqlSumG = "";
			String hqlCount = "";
			if (null != goods) {
				if (null != goods.getGoodsMarkId()
						&& !"".equals(goods.getGoodsMarkId())) {
					/*
					 * hqlSumIn += " and goodsStoreMarkId='" +
					 * goods.getGoodsMarkId() + "'"; hqlSumOut +=
					 * " and sellMarkId='" + goods.getGoodsMarkId() + "'";
					 */
					hqlSumG += " and goodsMarkId like'%"
							+ goods.getGoodsMarkId() + "%'";
					hqlCount += " and goodsMarkId like'%"
							+ goods.getGoodsMarkId() + "%'";
				}
				if (null != goods.getGoodsFormat()
						&& !"".equals(goods.getGoodsFormat())) {
					/*
					 * hqlSumIn += " and goodsStoreFormat='" +
					 * goods.getGoodsFormat() + "'"; hqlSumOut +=
					 * " and sellFormat='" + goods.getGoodsFormat() + "'";
					 */hqlSumG += " and goodsFormat like'%"
							+ goods.getGoodsFormat() + "%'";
					hqlCount += " and goodsFormat like'%"
							+ goods.getGoodsFormat() + "%'";
				}
				if (null != goods.getGoodsClass()
						&& !"".equals(goods.getGoodsClass())) {
					/*
					 * hqlSumIn += " and goodsStoreWarehouse='" +
					 * goods.getGoodsClass() + "'"; hqlSumOut +=
					 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
					 */hqlSumG += " and goodsClass='" + goods.getGoodsClass()
							+ "'";
					hqlCount += " and goodsClass='" + goods.getGoodsClass()
							+ "'";
				}
				if (null != goods.getGoodHouseName()
						&& !"".equals(goods.getGoodHouseName())) {
					/*
					 * hqlSumIn += " and goodsStoreWarehouse='" +
					 * goods.getGoodsClass() + "'"; hqlSumOut +=
					 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
					 */hqlSumG += " and goodHouseName='"
							+ goods.getGoodHouseName() + "'";
					hqlCount += " and goodHouseName='"
							+ goods.getGoodHouseName() + "'";
				}
				if (null != goods.getGoodsPosition()
						&& !"".equals(goods.getGoodsPosition())) {
					/*
					 * hqlSumIn += " and goodsStoreWarehouse='" +
					 * goods.getGoodsClass() + "'"; hqlSumOut +=
					 * " and sellWarehouse='" + goods.getGoodsClass()+ "'";
					 */hqlSumG += " and goodsPosition='"
							+ goods.getGoodsPosition() + "'";
					hqlCount += " and goodsPosition='"
							+ goods.getGoodsPosition() + "'";
				}
				if (null != goods.getGoodsFullName()
						&& !"".equals(goods.getGoodsFullName())) {
					/*
					 * hqlSumIn += " and goodsStoreGoodsName='" +
					 * goods.getGoodsFullName() + "'"; hqlSumOut +=
					 * " and sellGoods='" + goods.getGoodsFullName()+ "'";
					 */
					hqlSumG += " and goodsFullName='"
							+ goods.getGoodsFullName() + "'";
					hqlCount += " and goodsFullname='"
							+ goods.getGoodsFullName() + "'";
				}
				if (goods.getGoodHouseName() != null
						&& goods.getGoodHouseName().length() > 0) {
					hqlSumG += " and goodHouseName='"
							+ goods.getGoodHouseName() + "'";
					hqlCount += " and goodHouseName='"
							+ goods.getGoodHouseName() + "'";
				}
				hql = "select goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName,sum(goodsCurQuantity),"
						+ "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.goodsMarkId and  goodsStoreWarehouse=g.goodsClass   and goodsStoreFormat = g.goodsFormat"
						+ hqlSumIn
						+ " and goodsStoreWarehouse not in ('待检库','不合格品库') ) as sumOut,"
						+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit=g.goodsUnit and sellWarehouse=g.goodsClass    "
						+ hqlSumOut
						+ "  and sellWarehouse not in ('待检库','不合格品库') ) as sumIn"
						+ " from Goods g where goodsClass in("
						+ kubie
						+ ")"
						+ hqlSumG
						+ "  and goodsClass not in ('待检库','不合格品库')  GROUP BY goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName";
				hql2 += hqlCount;
			}
			if (null != startDate && !"".equals(startDate) && null != endDate
					&& !"".equals(endDate)) {
				hqlSumIn += " and goodsStoreTime between '" + startDate
						+ "' and '" + endDate + "'";
				hqlSumOut += " and sellTime between '" + startDate + "' and '"
						+ endDate + "'";

				hql = "select goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName,sum(goodsCurQuantity),"
						+ "(select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=g.goodsMarkId and goodsStoreWarehouse=g.goodsClass  and goodsStoreFormat = g.goodsFormat"
						+ hqlSumIn
						+ " and goodsStoreWarehouse not in ('待检库','不合格品库') ) as sumOut,"
						+ "(select sum(sellCount) from Sell where sellMarkId=g.goodsMarkId and sellFormat=g.goodsFormat and sellUnit=g.goodsUnit and sellWarehouse=g.goodsClass "
						+ hqlSumOut
						+ "  and sellWarehouse not in ('待检库','不合格品库') ) as sumIn"
						+ " from Goods g where goodsClass in("
						+ kubie
						+ ")  "
						+ hqlSumG
						+ "  and goodsClass not in ('待检库','不合格品库') GROUP BY goodsMarkId,goodsFullName,goodsFormat,goodsUnit,goodsClass,goodHouseName";
			}
			List list = totalDao.find(hql);
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(ServletActionContext.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				if (list.size() > 60000) {
					response.setHeader("Content-disposition",
							"attachment; filename="
									+ new String("库存汇总".getBytes("GB2312"),
											"8859_1") + ".xlsx");
				} else {
					response.setHeader("Content-disposition",
							"attachment; filename="
									+ new String("库存汇总".getBytes("GB2312"),
											"8859_1") + ".xls");
				}

				response.setContentType("application/msexcel");

				SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
				org.apache.poi.ss.usermodel.Sheet sheet = workBook
						.createSheet("库存进出数据汇总");
				Row row = sheet.createRow(2);
				CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 1,
						16);
				CellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				Font font = workBook.createFont();
				font.setFontHeightInPoints((short) 16);
				font.setBold(true);
				style.setFont(font);
				sheet.addMergedRegion(rangeAddress);
				row = sheet.createRow(0);
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(1);
				cell.setCellValue("库存进出数据汇总");
				cell.setCellStyle(style);

				row = sheet.createRow(1);
				cell = row.createCell(0, CellType.STRING);
				cell.setCellValue("序号");
				cell = row.createCell(1, CellType.STRING);
				cell.setCellValue("件号");
				cell = row.createCell(2, CellType.STRING);
				cell.setCellValue("品名");
				cell = row.createCell(3, CellType.STRING);
				cell.setCellValue("规格");
				cell = row.createCell(4, CellType.STRING);
				cell.setCellValue("单位");
				cell = row.createCell(5, CellType.STRING);
				cell.setCellValue("库别");
				cell = row.createCell(6, CellType.STRING);
				cell.setCellValue("单价");
				cell = row.createCell(7, CellType.STRING);
				cell.setCellValue("期初库存");
				cell = row.createCell(8, CellType.STRING);
				cell.setCellValue("期初金额");
				cell = row.createCell(9, CellType.STRING);
				cell.setCellValue("累计收入");
				cell = row.createCell(10, CellType.STRING);
				cell.setCellValue("收入金额");
				cell = row.createCell(11, CellType.STRING);
				cell.setCellValue("累计支出");
				cell = row.createCell(12, CellType.STRING);
				cell.setCellValue("支出金额");
				cell = row.createCell(13, CellType.STRING);
				cell.setCellValue("库存结余");
				cell = row.createCell(14, CellType.STRING);
				cell.setCellValue("期末金额");
				cell = row.createCell(15, CellType.STRING);
				cell.setCellValue("备注");

				int lineNumber = 0;
				for (int i = 0; i < list.size(); i++) {

					Object[] o = (Object[]) list.get(i);
					String p_markId = (String) o[0];// 件号
					String p_format = (String) o[2];// 规格
					if (p_markId.equals("84C603852P1")) {
						System.out.println(p_markId + ":" + p_format);
					}
					double countIn = 0.0;
					double countOut = 0.0;
					if (null != o[7]) {
						countIn = (Double) o[7];// 入库累计
					}
					if (null != o[8]) {
						countOut = (Double) o[8];// 出库累计
					}
					double countB = 0.0;
					if (null != o[6]) {
						countB = (Double) o[6];// 库存结余;
					}
					float couIn = (float) countIn;
					float couOut = (float) countOut;
					float couB = (float) countB;
					float fIn = Float.parseFloat(String.valueOf(couIn));// 入库量
					float fOut = Float.parseFloat(String.valueOf(couOut));// 入
					// 库量
					float fB = Float.parseFloat(String.valueOf(couB));// 入库量
					float fQichu = fB + fOut - fIn;// 期初库存

					String warehouse = (String) o[4];
					if (null != warehouse && !"".equals(warehouse)) {
					} else {
						warehouse = (String) o[4];
					}
					// 查找单价
					String time = Util.getDateTime("yyyy-MM-dd");
					String hql_p1 = " from Price where partNumber=? and specification=? order by writeDate desc";

					if ("成品库".equals(goods.getGoodsClass())) {
						hql_p1 = "from Price where productCategory = '总成'"
								+ " and '"
								+ time
								+ "'>=pricePeriodStart and ('"
								+ time
								+ "'<=pricePeriodEnd"
								+ " or pricePeriodEnd is null or  pricePeriodEnd = '') and partNumber=? and specification=? order by writeDate desc";

					} else if ("外购件库".equals(goods.getGoodsClass())) {
						hql_p1 = " from Price where produceType = '外购'"
								+ " and '"
								+ time
								+ "'>=pricePeriodStart and ('"
								+ time
								+ "'<=pricePeriodEnd"
								+ " or pricePeriodEnd is null or  pricePeriodEnd = '') and partNumber=? and specification=? order by writeDate desc";
					}
					List listP = totalDao.query(hql_p1, p_markId, p_format);
					float price = 0f;// 单价
					if (listP.size() > 0 && null != list) {
						Price _p = (Price) listP.get(0);
						price = Float.parseFloat(String.valueOf(_p
								.getBhsPrice()));
					}

					if (couIn > 0 || couOut > 0 || couB > 0) {
						lineNumber++;

						row = sheet.createRow(lineNumber + 1);
						cell = row.createCell(0, CellType.STRING);
						cell.setCellValue(lineNumber);
						cell = row.createCell(1, CellType.STRING);
						cell.setCellValue((String) o[0]);
						cell = row.createCell(2, CellType.STRING);
						cell.setCellValue((String) o[1]);
						cell = row.createCell(3, CellType.STRING);
						cell.setCellValue((String) o[2]);
						cell = row.createCell(4, CellType.STRING);
						cell.setCellValue((String) o[3]);
						cell = row.createCell(5, CellType.STRING);
						cell.setCellValue(warehouse);
						cell = row.createCell(6, CellType.STRING);
						cell.setCellValue(String.format("%.2f", price));
						cell = row.createCell(7, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fQichu));
						cell = row.createCell(8, CellType.STRING);
						cell
								.setCellValue(String.format("%.2f", fQichu
										* price));
						cell = row.createCell(9, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fIn));
						cell = row.createCell(10, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fIn * price));
						cell = row.createCell(11, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fOut));
						cell = row.createCell(12, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fOut * price));
						cell = row.createCell(13, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fB));
						cell = row.createCell(14, CellType.STRING);
						cell.setCellValue(String.format("%.2f", fB * price));
					}
				}
				workBook.write(os);
				workBook.close();// 记得关闭工作簿
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if ("goodsDetail".equals(tag) || "dtc".equals(tag)) {// 导出明细
			String strif = " and goodsCurQuantity>0";
			if (null != goodsAge && 0 != goodsAge) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_YEAR, -goodsAge);
				String oldDate = format.format(calendar.getTime());
				strif += " and goodsChangeTime< '" + oldDate + "' ";
			}
			Users user = (Users) Util.getLoginUser();
			String hqlwarehouse = "from WareHouseAuth where usercode='"
					+ user.getCode() + "'";
			List warehoustlist = totalDao.find(hqlwarehouse);
			// if (warehoustlist.size() > 0) {
			// WareHouseAuth whh = (WareHouseAuth) warehoustlist.get(0);
			// if ("管理员".equals(whh.getGroup()) || "查看".equals(whh.getGroup()))
			// {
			// strif = "";
			// }
			// }
			String[] between = new String[2];
			if (null != startDate && null != endDate && !"".equals(endDate)
					&& !"".equals(startDate)) {
				between[0] = startDate;
				between[1] = endDate;
			}
			if (goods == null) {
				goods = new Goods();
			}
			String changqu = "";
			String hql_cq = " and goodHouseName in (";
			if (goods != null && goods.getGoodHouseName() != null
					&& goods.getGoodHouseName().length() > 0) {
				String str = "";
				String[] cangqus = goods.getGoodHouseName().split(",");
				if (cangqus != null && cangqus.length > 0) {
					for (int i = 0; i < cangqus.length; i++) {
						str += ",'" + cangqus[i] + "'";
					}
					if (str.length() >= 1) {
						str = str.substring(1);
					}
					hql_cq += str;
				}
				changqu = goods.getGoodHouseName();
				goods.setGoodHouseName(null);
				hql_cq += ")";
			} else {
				hql_cq = "";
			}
			String kubie = "";
			String hql_kb = " and  goodsClass in (";
			if (goods != null && goods.getGoodsClass() != null
					&& goods.getGoodsClass().length() > 0) {
				String str = "";
				String[] kubies = goods.getGoodsClass().split(",");
				if (kubies != null && kubies.length > 0) {
					for (int i = 0; i < kubies.length; i++) {
						str += ",'" + kubies[i] + "'";
					}
					if (str.length() >= 1) {
						str = str.substring(1);
					}
					hql_kb += str;
				}
				kubie = goods.getGoodsClass();
				goods.setGoodsClass(null);
				hql_kb += ")";
			} else {
				hql_kb = "";
			}
			String wgType ="";
			String str="";
			if (goods.getWgType() != null && !"".equals(goods.getWgType())) {
				wgType = goods.getWgType();
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name =? ", wgType);
				if (category != null) {
					getWgtype(category);
				}
				str = "  and wgType in (" + strbu.toString().substring(1) + ")";
				goods.setWgType(null);
			}
			String hql = totalDao.criteriaQueries(goods, "goodsChangeTime",
					between, "");
			hql += hql_cq + hql_kb+str;
			hql += " and goodsClass in(" + getWarehouseList(null) + ")";
			// if(null!=goods.getGoodsClass()){
			// hql += " and goodsClass ='"+goods.getGoodsClass()+"'";
			// }
			if ("dtc".equals(tag)) {
				hql += " and dtcFlag is not null and dtcFlag !='' ";
			}
			hql += strif + "  order by goodsChangeTime desc";
			List list = totalDao.find(hql);
			goods.setGoodHouseName(changqu);
			goods.setGoodsClass(kubie);
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(ServletActionContext.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String("库存数据".getBytes("GB2312"),
										"8859_1") + ".xlsx");
				response.setContentType("application/msexcel");

				SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
				org.apache.poi.ss.usermodel.Sheet sheet = workBook
						.createSheet("库存数据");
				Row row = sheet.createRow(2);
				CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 1,
						14);
				CellStyle style = workBook.createCellStyle();
				style.setAlignment(HorizontalAlignment.CENTER);
				Font font = workBook.createFont();
				font.setFontHeightInPoints((short) 16);
				font.setBold(true);
				style.setFont(font);
				sheet.addMergedRegion(rangeAddress);
				row = sheet.createRow(0);
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(1);
				cell.setCellValue("库存数据");
				cell.setCellStyle(style);

				row = sheet.createRow(1);
				cell = row.createCell(0, CellType.STRING);
				cell.setCellValue("序号");
				cell = row.createCell(1, CellType.STRING);
				cell.setCellValue("库别");
				cell = row.createCell(2, CellType.STRING);
				cell.setCellValue("仓区");
				cell = row.createCell(3, CellType.STRING);
				cell.setCellValue("件号");
				cell = row.createCell(4, CellType.STRING);
				cell.setCellValue("批次");
				cell = row.createCell(5, CellType.STRING);
				cell.setCellValue("业务件号");
				cell = row.createCell(6, CellType.STRING);
				cell.setCellValue("版本号");
				cell = row.createCell(7, CellType.STRING);
				cell.setCellValue("供料属性");
				cell = row.createCell(8, CellType.STRING);
				cell.setCellValue("品名");
				cell = row.createCell(9, CellType.STRING);
				cell.setCellValue("规格");
				cell = row.createCell(10, CellType.STRING);
				cell.setCellValue("物料类别");
				cell = row.createCell(11, CellType.STRING);
				cell.setCellValue("数量");
				cell = row.createCell(12, CellType.STRING);
				cell.setCellValue("单位");
				cell = row.createCell(13, CellType.STRING);
				cell.setCellValue("转换数量");
				cell = row.createCell(14, CellType.STRING);
				cell.setCellValue("内部订单号");
				cell = row.createCell(15, CellType.STRING);
				cell.setCellValue("库位");
				cell = row.createCell(16, CellType.STRING);
				cell.setCellValue("锁定单号");
				cell = row.createCell(17, CellType.STRING);
				cell.setCellValue("送货单单号");
				cell = row.createCell(18, CellType.STRING);
				cell.setCellValue("供应商");
				cell = row.createCell(19, CellType.STRING);
				cell.setCellValue("入库时间");
				if (pageStatus != null && pageStatus.equals("price")) {
					cell = row.createCell(20, CellType.STRING);
					cell.setCellValue("单价（含税）");
					cell = row.createCell(21, CellType.STRING);
					cell.setCellValue("单价（不含税）");
					cell = row.createCell(22, CellType.STRING);
					cell.setCellValue("单价（税率）");
				}
				for (int i = 0; i < list.size(); i++) {
					Goods go = (Goods) list.get(i);

					row = sheet.createRow(i + 2);
					cell = row.createCell(0, CellType.STRING);
					cell.setCellValue(i + 1);
					cell = row.createCell(1, CellType.STRING);
					cell.setCellValue(go.getGoodsClass());
					cell = row.createCell(2, CellType.STRING);
					cell.setCellValue(go.getGoodHouseName());
					cell = row.createCell(3, CellType.STRING);
					cell.setCellValue(go.getGoodsMarkId());
					cell = row.createCell(4, CellType.STRING);
					cell.setCellValue(go.getGoodsLotId());
					cell = row.createCell(5, CellType.STRING);
					cell.setCellValue(go.getYwmarkId());
					cell = row.createCell(6, CellType.STRING);
					cell.setCellValue(go.getBanBenNumber());
					cell = row.createCell(7, CellType.STRING);
					cell.setCellValue(go.getKgliao());
					cell = row.createCell(8, CellType.STRING);
					cell.setCellValue(go.getGoodsFullName());
					cell = row.createCell(9, CellType.STRING);
					cell.setCellValue(go.getGoodsFormat());
					cell = row.createCell(10, CellType.STRING);
					cell.setCellValue(go.getWgType());
					cell = row.createCell(11, CellType.STRING);
					cell.setCellValue(go.getGoodsCurQuantity());
					cell = row.createCell(12, CellType.STRING);
					cell.setCellValue(go.getGoodsUnit());
					if (go.getGoodsZhishu() != null) {
						cell = row.createCell(13, CellType.STRING);
						cell.setCellValue(go.getGoodsZhishu());
					}
					if (go.getNeiorderId() != null) {
						cell = row.createCell(14, CellType.STRING);
						cell.setCellValue(go.getNeiorderId());
					} else {
						cell = row.createCell(14, CellType.STRING);
						cell.setCellValue("");
					}
					if (go.getGoodsPosition() != null) {
						cell = row.createCell(15, CellType.STRING);
						cell.setCellValue(go.getGoodsPosition());
					} else {
						cell = row.createCell(15, CellType.STRING);
						cell.setCellValue("");
					}
					if (go.getSuodingdanhao() != null) {
						cell = row.createCell(16, CellType.STRING);
						cell.setCellValue(go.getSuodingdanhao());
					}
					if (go.getShplanNumber() != null) {
						cell = row.createCell(17, CellType.STRING);
						cell.setCellValue(go.getShplanNumber());
					}
					if (go.getGoodsSupplier() != null) {
						cell = row.createCell(18, CellType.STRING);
						cell.setCellValue(go.getGoodsSupplier());
					}
					if (go.getGoodsChangeTime() != null) {
						cell = row.createCell(19, CellType.STRING);
						cell.setCellValue(go.getGoodsChangeTime());
					}
					if (pageStatus != null && pageStatus.equals("price")) {
						if (go.getGoodsBuyPrice() != null) {
							cell = row.createCell(20, CellType.STRING);
							cell.setCellValue(go.getGoodsBuyPrice());
						}
						if (go.getGoodsBuyBhsPrice() != null) {
							cell = row.createCell(21, CellType.STRING);
							cell.setCellValue(go.getGoodsBuyBhsPrice());
						}
						if (go.getTaxprice() != null) {
							cell = row.createCell(22, CellType.STRING);
							cell.setCellValue(go.getTaxprice());
						}
					}

				}
				workBook.write(os);
				workBook.close();// 记得关闭工作簿
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	// 查询成品库中库存是否存在单价信息
	@Override
	public String findPriceByPartNumber(String ywMarkId, String sellMarkId,
			String orderNumber, Float count, String selfcard) {
		// TODO Auto-generated method stub
		String addSql = null;
		String hql = null;
		if (ywMarkId != null && ywMarkId.length() > 0) {
			addSql = "and (pieceNumber=? or pieceNumber='" + ywMarkId + "')";
			hql = " from Price where (partNumber=? or partNumber='" + ywMarkId
					+ "')";
		} else {
			addSql = "and pieceNumber=?";
			hql = " from Price where partNumber=?";
		}
		List list = this.totalDao.query(hql, sellMarkId);
		if (list == null || list.size() == 0) {
			return "件号为:" + sellMarkId + "的单价信息不存在,请前往存档!";
		}
		if (orderNumber != null && !orderNumber.equals("未确定")) {
			ProductManager product = (ProductManager) totalDao
					.getObjectByQuery(
							"from ProductManager where pieceNumber = ? and orderManager.orderNum=? and allocationsNum is not null and allocationsNum>0"
									+ " and  status <> '取消' and (sellCount is null or  allocationsNum-sellCount>0) ",
							sellMarkId, orderNumber);
			Float canSellCount = 0f;
			if (product != null) {
				if (product.getApplyNumber() == null) {
					canSellCount = product.getAllocationsNum();
				} else {
					canSellCount = product.getAllocationsNum()
							- product.getApplyNumber();
				}
			}
			if (canSellCount >= count) {
				return "true";
			} else {
				return "出库数量超过该订单的此件号可出库数量，该订单的此件号可出库数量为：" + canSellCount;
			}
		}
		return "true";
	}

	@Override
	public List<String> getOrderNumbers(Goods goods) {
		// TODO Auto-generated method stub
		List<String> orderNumberList = new ArrayList<String>();
		if (goods.getGoodsLotId() != null) {
			String ywMarkId = (String) totalDao
					.getObjectByCondition(
							"select ywMarkId from ProcardTemplate where procardStyle='总成' and markId=?",
							goods.getGoodsMarkId());
			String addSql = null;
			if (ywMarkId != null && ywMarkId.length() > 0) {
				addSql = " (pieceNumber=? or pieceNumber='" + ywMarkId + "')";
			} else {
				addSql = " pieceNumber=?";
			}
			List<String> orderNumberList1 = totalDao
					.query(
							"select orderNum from OrderManager where orderType in('正式','试制') and  orderNum is not null and orderNum!='' "
									+ "and id in (select orderManager.id from ProductManager where "
									+ addSql
									+ " and (allocationsNum>=applyNumber or (allocationsNum>0 and applyNumber is null))) "
									+ "and id in (select orderManager.id from ProductManager where id in(select productId from Product_Internal where ioDetailId in (select planOrderDetailId from Procard where markId =? and selfCard=?))  )",
							goods.getGoodsMarkId(), goods.getGoodsMarkId(),
							goods.getGoodsLotId());
			if (orderNumberList1 != null && orderNumberList1.size() > 0) {
				for (String on : orderNumberList1) {
					if (!orderNumberList.contains(on)) {
						orderNumberList.add(on);
					}
				}
			}
			List<String> orderNumberList2 = totalDao
					.query(
							"select orderNum from OrderManager where orderType in('正式','试制') and  orderNum is not null and orderNum!='' "
									+ "and id in (select orderManager.id from ProductManager where "
									+ addSql
									+ " and (allocationsNum>applyNumber or (allocationsNum>0 and applyNumber is null))"
									+ "and id in(select zsProductId from ProductZsAboutBh where  id in(select pzsAboutbhId from InternalZsAboutBh  where idetailId in(select planOrderDetailId from Procard where markId =? and selfCard=?)))) ",
							goods.getGoodsMarkId(), goods.getGoodsMarkId(),
							goods.getGoodsLotId());
			if (orderNumberList2 != null && orderNumberList2.size() > 0) {
				for (String on : orderNumberList2) {
					if (!orderNumberList.contains(on)) {
						orderNumberList.add(on);
					}
				}
			}
			// } else {
			// orderNumberList = totalDao
			// .query(
			// "select outOrderNumber from OrderManager where outOrderNumber is not null and outOrderNumber!='' and id in (select orderManager.id from ProductManager where pieceNumber=? and (allocationsNum>applyNumber or (allocationsNum>0 and applyNumber is null)))",
			// goods.getGoodsMarkId());
		}
		return orderNumberList;
	}

	@Override
	public Integer updateAndgetUnpassOkCount(String sellMarkId, String orderNum) {
		// TODO Auto-generated method stub
		List<ProductUnPassKp> productUnPassKps = (List<ProductUnPassKp>) totalDao
				.query(
						" from ProductUnPassKp where odrerNumber=? and markId=? and okCount is not null and okCount>0",
						sellMarkId, orderNum);
		int count = 0;
		if (productUnPassKps.size() > 0) {
			for (ProductUnPassKp product : productUnPassKps) {
				count += product.getOkCount();
				product.setOkCount(0f);
				totalDao.update(product);
			}
		}
		return count;
	}

	@Override
	public boolean reSumCompleteRate(String month) {
		// TODO Auto-generated method stub
		String lastMonth = Util.getLastMonth("yyyy-MM");
		List<Sell> sellList = totalDao
				.query("from Sell where sellLot is not null and sellLot!='' and sellWarehouse='成品库' and (sellDate like'"
						+ month
						+ "%' or sellDate like'"
						+ lastMonth
						+ "%') order By sellMarkId");
		// 清空需要当月交付的订单的完成率和及时率，以及内部计划的出库数量
		List<OrderManager> omList = totalDao
				.query("from OrderManager where paymentDate like'" + month
						+ "%'");
		if (omList != null && omList.size() > 0) {
			for (OrderManager om : omList) {
				om.setDeliveryStatus(null);
				om.setCompletionrate(0f);
				om.setTimeRate(0f);
				Set<ProductManager> productSet = om.getProducts();
				if (productSet != null && productSet.size() > 0) {
					for (ProductManager product : productSet) {
						product.setSellCount(0f);
						product.setTimeNum(0f);
						totalDao.update(product);
					}
				}
				totalDao.update(om);
				List<InternalOrderDetail> iodList = totalDao
						.query(
								"from InternalOrderDetail where internalOrder.id in (select io.id from InternalOrder io join io.outerOrders om where om.id =?)",
								om.getId());
				if (iodList != null && iodList.size() > 0) {
					for (InternalOrderDetail iod : iodList) {
						iod.setSellCount(0f);
						totalDao.update(iod);
					}
				}
			}
		}
		if (sellList != null && sellList.size() > 0) {
			for (Sell sell : sellList) {
				List<OrderManager> omList2 = totalDao
						.query(
								"from OrderManager where id in(select om.id from OrderManager om join om.innerOrders io where io.id in (select planOrderId from Procard where markId=? and selfCard=? and productStyle='批产')))",
								sell.getSellMarkId(), sell.getSellLot());
				if (omList != null && omList.size() > 0) {
					for (OrderManager om : omList) {
						om.setDeliveryStatus(null);
						om.setCompletionrate(0f);
						om.setTimeRate(0f);
						Set<ProductManager> productSet = om.getProducts();
						if (productSet != null && productSet.size() > 0) {
							for (ProductManager product : productSet) {
								product.setSellCount(0f);
								product.setTimeNum(0f);
								totalDao.update(product);
							}
						}
						totalDao.update(om);
						List<InternalOrderDetail> iodList = totalDao
								.query(
										"from InternalOrderDetail where internalOrder.id in (select io.id from InternalOrder io join io.outerOrders om where om.id =?)",
										om.getId());
						if (iodList != null && iodList.size() > 0) {
							for (InternalOrderDetail iod : iodList) {
								iod.setSellCount(0f);
								totalDao.update(iod);
							}
						}
					}
				}
			}
		}

		// 重新计算该月出货的订单的完成率和及时率
		if (sellList != null && sellList.size() > 0) {
			for (Sell sell : sellList) {
				Float sellCount = sell.getSellCount();
				InternalOrderDetail iod2 = (InternalOrderDetail) totalDao
						.getObjectByCondition(
								"from InternalOrderDetail where pieceNumber=? and internalOrder.id in (select planOrderId from Procard where markId=? and selfCard=? and productStyle='批产')",
								sell.getSellMarkId(), sell.getSellMarkId(),
								sell.getSellLot());
				if (iod2 != null) {
					if (iod2.getSellCount() == null) {
						iod2.setSellCount(0f);
					}
					iod2.setSellCount((iod2.getSellCount() + sellCount));
					totalDao.update(iod2);
				}
				List<OrderManager> omList2 = totalDao
						.query(
								"from OrderManager where id in(select om.id from OrderManager om join om.innerOrders io where om.paymentDate like'"
										+ month
										+ "%' and io.id in (select planOrderId from Procard where markId=? and selfCard=? and productStyle='批产')))",
								sell.getSellMarkId(), sell.getSellLot());
				if (omList2 != null && omList2.size() > 0 && sellCount > 0) {
					for (OrderManager om2 : omList2) {
						Set<ProductManager> psSet = om2.getProducts();
						if (psSet != null && psSet.size() > 0 && sellCount > 0) {
							for (ProductManager pm : psSet) {
								if (pm.getPieceNumber() != null
										&& pm.getPieceNumber().equals(
												sell.getSellMarkId())) {
									if (pm.getSellCount() == null) {
										pm.setSellCount(0f);
									}
									if ((pm.getNum().floatValue()
											- pm.getSellCount().floatValue() > 0)) {
										if ((pm.getNum().floatValue() - pm
												.getSellCount().floatValue()) > sellCount) {
											pm
													.setSellCount((pm
															.getSellCount() + sellCount));
											String paymentDate = om2
													.getPaymentDate();
											if (paymentDate != null) {// 订单及时率
												if (Util.compareTime(
														paymentDate,
														"yyyy-MM-dd", sell
																.getSellDate(),
														"yyyy-MM-dd")) {
													// 完成时间在前
													if (pm.getTimeNum() == null) {
														pm.setTimeNum(0f);
													}
													pm
															.setTimeNum((pm
																	.getTimeNum() + sellCount));
												}
											}
											sellCount = 0f;
											totalDao.update(om2);
										} else {
											pm.setSellCount(pm.getNum());
											String paymentDate = om2
													.getPaymentDate();
											if (paymentDate != null) {// 订单及时率
												if (Util.compareTime(
														paymentDate,
														"yyyy-MM-dd", sell
																.getSellDate(),
														"yyyy-MM-dd")) {
													// 完成时间在前
													if (pm.getTimeNum() == null) {
														pm.setTimeNum(0f);
													}
													pm
															.setTimeNum(pm
																	.getTimeNum()
																	+ (pm
																			.getNum() - pm
																			.getSellCount()));
												}
											}
											sellCount = sellCount
													- (pm.getNum() - pm
															.getSellCount());
											String hql = "from ProductManager where orderManager.id=? and allocationsNum<num";
											int count = totalDao.getCount(hql,
													om2.getId());
											if (count == 0) {// 订单对应的产品都已交付
												om2.setDeliveryStatus("是");
											}
										}
										// 计算订单交付率
										String hqljfr = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
										String hqljsr = "select sum(timeNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
										String hqljfr2 = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
										Float pronum = (Float) totalDao.query(
												hqljfr, om2.getId()).get(0);
										Float timeCount = (Float) totalDao
												.getObjectByCondition(hqljsr,
														om2.getId());
										Float proallocationsNum = (Float) totalDao
												.getObjectByCondition(hqljfr2,
														om2.getId());
										if (timeCount == null) {
											timeCount = 0f;
										}
										if (proallocationsNum == null) {
											proallocationsNum = 0f;
										}
										om2.setCompletionrate(proallocationsNum
												/ pronum * 100);
										om2.setTimeRate(timeCount / pronum
												* 100);
										totalDao.update(om2);

									}
									break;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean reSetHasRuKuCount() {
		// TODO Auto-generated method stub
		List<Procard> procardList = totalDao
				.query("from Procard where status <>'入库' and procardStyle='总成'");
		if (procardList.size() > 0) {
			for (Procard p : procardList) {
				Float hasRukuCounts = (Float) totalDao
						.getObjectByCondition(
								"select sum(goodsStoreCount) from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and status='入库'",
								p.getMarkId(), p.getSelfCard());// 次卡已入库数量
				p.setHasRuku(hasRukuCounts);
				totalDao.update(p);
			}
		}
		return false;
	}

	@Override
	public String ylBaoFeiOrChange(Integer id, YuLiaoApply yuLiaoApply,
			String type) {
		// TODO Auto-generated method stub
		Goods goods = getGoodsById(id);
		if (goods == null) {
			return "没有找到目标余料,申请失败!";
		} else {
			if (goods.getYlApplyCount() == null) {
				goods.setYlApplyCount(0f);
			}
			if (yuLiaoApply.getApplyCount() > (goods.getGoodsCurQuantity() - goods
					.getYlApplyCount())) {
				return "对不起申请数量超额，申请失败!";
			}
			Users user = Util.getLoginUser();
			if (user == null) {
				return "请登录后在申请!";
			}
			yuLiaoApply.setUserId(user.getId());
			yuLiaoApply.setUserName(user.getName());
			yuLiaoApply.setUserCode(user.getCode());
			yuLiaoApply.setAddTime(Util.getDateTime());
			yuLiaoApply.setYlId(goods.getGoodsId());
			yuLiaoApply.setYlMarkId(goods.getYlMarkId());
			yuLiaoApply.setYlSelfCard(goods.getYlSelfCard());
			yuLiaoApply.setGoodsMarkId(goods.getGoodsMarkId());
			yuLiaoApply.setGoodsFormat(goods.getGoodsFormat());
			yuLiaoApply.setGoodsUnit(goods.getGoodsUnit());
			yuLiaoApply.setStatus("可用");
			yuLiaoApply.setApplyStatus("审批中");
			if (type != null && type.equals("change")) {
				yuLiaoApply.setApplyType("转原材料");
			} else {
				yuLiaoApply.setApplyType("报废");
			}
			totalDao.save(yuLiaoApply);
			goods.setYlApplyCount(goods.getYlApplyCount()
					+ yuLiaoApply.getApplyCount());
			return totalDao.update(goods) + "";
		}
	}

	@Override
	public Map<Integer, Object> findYuLiaoApplysByCondition(
			YuLiaoApply yuLiaoApply, int pageNo, int pageSize, String role) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (yuLiaoApply == null) {
			yuLiaoApply = new YuLiaoApply();
		}
		String hql = totalDao.criteriaQueries(yuLiaoApply, null, null);
		if (role == null || role.length() == 0 || role.equals("all")) {// 查询所有

		} else if (role.equals("single")) {
			Users user = Util.getLoginUser();
			hql += " and userId=" + user.getId();
		} else if (role.equals("apply")) {
			hql += " and status='可用' and applyStatus='审批中'";
		}
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public String applyYl(Integer id, String tag) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		YuLiaoApply ylApply = (YuLiaoApply) totalDao.getObjectById(
				YuLiaoApply.class, id);
		if (ylApply == null) {
			return "没有找到申请目标,审批失败";
		}
		if (tag == null) {
			return "审批异常，审批失败!";
		}
		Goods goods = null;
		if (ylApply.getYlId() != null) {
			goods = getGoodsById(ylApply.getYlId());
		}
		if (tag.equals("back")) {// 打回
			if (goods != null) {
				if (goods.getYlApplyCount() != null
						&& goods.getYlApplyCount() > ylApply.getApplyCount()) {
					goods.setYlApplyCount((goods.getYlApplyCount() - ylApply
							.getApplyCount()));
				} else {
					goods.setYlApplyCount(0f);
				}
				totalDao.update(goods);
			}
			ylApply.setApplyStatus("打回");
			return totalDao.update(ylApply) + "";
		} else if (tag.equals("agree")) {// 同意
			if (goods != null) {
				if (goods.getGoodsCurQuantity() != null
						&& goods.getGoodsCurQuantity() >= ylApply
								.getApplyCount()) {
					goods
							.setGoodsCurQuantity((goods.getGoodsCurQuantity() - ylApply
									.getApplyCount()));// 减去库存
					if (goods.getYlApplyCount() != null
							&& goods.getYlApplyCount() > ylApply
									.getApplyCount()) {// 减去批准数量
						goods
								.setYlApplyCount((goods.getYlApplyCount() - ylApply
										.getApplyCount()));
					} else {
						goods.setYlApplyCount(0f);
					}
					totalDao.update(goods);
					// 余料出库记录
					Sell sellYl = new Sell();
					sellYl.setSellArtsCard(goods.getYlSelfCard());
					sellYl.setSellSupplier(goods.getGoodsSupplier());
					sellYl.setSellFormat(goods.getGoodsFormat());
					sellYl.setSellLot(goods.getGoodsLotId());
					sellYl.setSellMarkId(goods.getGoodsMarkId());
					sellYl.setSellAdminName(user.getName());
					sellYl.setSellGoods(goods.getGoodsFullName());
					sellYl.setSellDate(Util.getDateTime("yyyy-MM-dd"));
					sellYl.setSellTime(Util.getDateTime());
					sellYl.setSellWarehouse(goods.getGoodsClass());
					sellYl.setSellUnit(goods.getGoodsUnit());
					sellYl.setSellCount(ylApply.getApplyCount());
					if (ylApply.getApplyType() != null
							&& ylApply.getApplyType().equals("转原材料")) {
						sellYl.setStyle("余料转原材料");
					} else {
						sellYl.setStyle("余料报废");
					}
					totalDao.save(sellYl);
					if (ylApply.getApplyType() != null
							&& ylApply.getApplyType().equals("转原材料")) {
						// 原材料入库记录
						GoodsStore ruku = new GoodsStore();
						ruku.setGoodsStoreMarkId(ylApply.getNewGoodsMarkId());// 件号
						ruku.setGoodsStoreFormat(ylApply.getNewGoodsFormat());// 规格
						// ruku.setGoodsStoreGoodsName(pro.getProName());// 名称
						ruku.setGoodsStoreLot(ylApply.getYlSelfCard());// 批次
						ruku.setGoodsStoreCount(ylApply.getChangeCount());// 数量
						ruku.setPrintStatus("YES");
						ruku.setGoodsStoreWarehouse("余料库");// 库别
						ruku.setGoodsStoreCharger(user.getName());// 经办人
						ruku.setStyle("余料转库");// 入库类型
						ruku.setGoodsStorePerson(ylApply.getUserName());
						ruku.setGoodsStoreDate(DateUtil.formatDate(new Date(),
								"yyyy-MM-dd"));
						ruku.setGoodsStoreUnit(ylApply.getNewGoodsUnit());// 单位
						totalDao.save(ruku);

						// 原材料入库
						Goods old = (Goods) totalDao
								.getObjectByCondition(
										"from Goods where goodsLotId=? and goodsMarkId=? and goodsFormat=?",
										ylApply.getYlSelfCard(), ylApply
												.getNewGoodsMarkId(), ylApply
												.getNewGoodsFormat());
						if (old != null) {
							if (old.getGoodsCurQuantity() == null) {
								old.setGoodsCurQuantity(ylApply
										.getChangeCount());
							} else {
								old.setGoodsCurQuantity(old
										.getGoodsCurQuantity()
										+ ylApply.getChangeCount());
							}
							totalDao.update(old);
						} else {
							Goods ycl = new Goods();
							ycl.setGoodsLotId(ylApply.getYlSelfCard());
							ycl.setGoodsMarkId(ylApply.getNewGoodsMarkId());
							ycl.setGoodsFormat(ylApply.getNewGoodsFormat());// 規格
							ycl.setGoodsUnit(ylApply.getNewGoodsUnit());// 单位
							ycl.setGoodsBeginQuantity(ylApply.getChangeCount());// 起初数量
							ycl.setGoodsCurQuantity(ylApply.getChangeCount());// 数量
							ycl.setGoodsClass("原材料库");// 所属仓库
							ycl.setGoodsChangeTime(Util.getDateTime());// 日期
							// ycl.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());//
							// 名称
							// ycl.setGoodsStyle(goodsStore.getStyle());
							// ycl.setGoodsPosition(goodsStore.getGoodsStorePosition());//
							// 库位
							// ycl.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());//
							// 客户
							totalDao.save(ycl);
						}
					}
					ylApply.setApplyStatus("同意");
					return "" + totalDao.update(ylApply);

				} else {
					if (goods != null) {
						if (goods.getYlApplyCount() != null
								&& goods.getYlApplyCount() > ylApply
										.getApplyCount()) {// 减去批准数量
							goods
									.setYlApplyCount((goods.getYlApplyCount() - ylApply
											.getApplyCount()));
						} else {
							goods.setYlApplyCount(0f);
						}
					}
					ylApply.setStatus("作废");
					totalDao.update(ylApply);
					return "余料数量已不足,该申请作废,审批失败!";
				}
			}
			return "没有找到对应的余料数据，审批失败!";
		}
		return null;
	}

	@Override
	public String deleteYlApply(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		YuLiaoApply ylApply = (YuLiaoApply) totalDao.getObjectById(
				YuLiaoApply.class, id);
		if (ylApply == null) {
			return "没有找到申请目标,审批失败";
		}
		Goods goods = null;
		if (ylApply.getYlId() != null) {
			goods = getGoodsById(ylApply.getYlId());
		}
		if (goods != null) {
			if (goods.getYlApplyCount() != null
					&& goods.getYlApplyCount() > ylApply.getApplyCount()) {
				goods.setYlApplyCount((goods.getYlApplyCount() - ylApply
						.getApplyCount()));
			} else {
				goods.setYlApplyCount(0f);
			}
			totalDao.update(goods);
		}
		return "" + totalDao.delete(ylApply);
	}

	@Override
	public String goodsApplyFcStatus(Integer id) {
		// TODO Auto-generated method stub
		Goods goods = (Goods) totalDao.getObjectById(Goods.class, id);
		if (goods != null) {
			try {
				if (goods.getFcStatus() != null
						&& goods.getFcStatus().equals("封存")) {// 封存时申请解封
					if (goods.getFcApplyStatus() != null
							&& goods.getFcApplyStatus().equals("审批中")) {
						return "已经在审批中,请勿重复申请!";
					}
					Integer epId = CircuitRunServerImpl.createProcess(
							"仓库物料封存或解封申请", Goods.class, goods.getGoodsId(),
							"fcApplyStatus", "id",
							"goodsAction!fcApplyDetail.action?id="
									+ goods.getGoodsId(), "仓库物料解封申请审批", true);
					if (epId != null) {
						goods.setEpId(epId);
						CircuitRun cr = (CircuitRun) totalDao.getObjectById(
								CircuitRun.class, epId);
						if (cr.getAllStatus() != null
								&& cr.getAllStatus().equals("同意")
								&& cr.getAuditStatus() != null
								&& cr.getAuditStatus().equals("审批完成")) {
							// 没有审批结点直接同意
							goods.setFcApplyStatus("同意");
							goods.setFcStatus("可用");
						} else {
							goods.setFcApplyStatus("审批中");
						}
						return totalDao.update(goods) + "";
					} else {
						return "审批流程有误,请联系管理员!";
					}
				} else {// 可用时申请封存
					if (goods.getFcApplyStatus() != null
							&& goods.getFcApplyStatus().equals("审批中")) {
						return "已经在审批中,请勿重复申请!";
					}
					Integer epId = CircuitRunServerImpl.createProcess(
							"仓库物料封存或解封申请", Goods.class, goods.getGoodsId(),
							"fcApplyStatus", "id",
							"goodsAction!fcApplyDetail.action?id="
									+ goods.getGoodsId(), "仓库物料封存申请审批", true);
					if (epId != null) {
						goods.setEpId(epId);
						CircuitRun cr = (CircuitRun) totalDao.getObjectById(
								CircuitRun.class, epId);
						if (cr.getAllStatus() != null
								&& cr.getAllStatus().equals("同意")
								&& cr.getAuditStatus() != null
								&& cr.getAuditStatus().equals("审批完成")) {
							// 没有审批结点直接同意
							goods.setFcApplyStatus("同意");
							goods.setFcStatus("封存");
						} else {
							goods.setFcApplyStatus("审批中");
						}
						return totalDao.update(goods) + "";
					} else {
						return "审批流程有误,请联系管理员!";
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "审批流程有误,请联系管理员!";
			}
		}
		return "对不起没有找到目标库存物料,申请失败!";
	}

	@Override
	public String addbaofeigoods(BaoFeiGoods baofeigoods, Goods newGoods) {
		if (baofeigoods != null) {
			 if (baofeigoods.getId() != null && baofeigoods.getId() > 0) {
			 BaoFeiGoods bfg = null;
			 bfg = (BaoFeiGoods) totalDao.get(BaoFeiGoods.class, baofeigoods
			 .getId());
			 if ("打回".equals(bfg.getEp_status())) {
			 if (CircuitRunServerImpl.updateCircuitRun(bfg.getId())) {
			 bfg.setEp_status("未审批");
			 totalDao.update(bfg);
			 return "重新申请成功";
			 }
			 }
			 }
			Goods goods = (Goods) totalDao.get(Goods.class, baofeigoods
					.getGoodsId());
			Float sum = (Float) totalDao.getObjectByCondition(
					"select sum(sq_num) from BaoFeiGoods"
							+ " where goodsId=? and ep_status <>'同意'", goods
							.getGoodsId());

			if (sum == null) {
				sum = 0f;
			}
			if ((float) goods.getGoodsCurQuantity() - sum
					- baofeigoods.getSq_num() < 0) {
				return "已申请数量大于库存数量";
			}
			String hql_status = "from BaoFeiGoods where ep_status='审批中' and goodsId="
					+ baofeigoods.getGoodsId();
			List<BaoFeiGoods> baofeigoodsList = totalDao.find(hql_status);
			float sq_num = baofeigoods.getSq_num();
			if (baofeigoodsList != null && baofeigoodsList.size() > 0) {
				for (BaoFeiGoods baoFeiGoods2 : baofeigoodsList) {
					sq_num += baoFeiGoods2.getSq_num();
				}
			}
			if (sq_num > (float) goods.getGoodsCurQuantity()) {
				return "所申请的报废数量总和不能大于当前量";
			}
			Users user = Util.getLoginUser();
			baofeigoods.setDept(user.getDept());
			baofeigoods.setDate(Util.getDateTime("yyyy-MM-dd"));
			totalDao.save(baofeigoods);
			if (newGoods == null) {
				newGoods = new Goods();
			}
			BeanUtils.copyProperties(goods, newGoods, new String[] { "id",
					"goodsClass", "goodHouseName", "goodsPosition",
					"goodsCurQuantity", "goodsBeginQuantity" });
			newGoods.setGoodsClass(baofeigoods.getKubie());
			newGoods.setGoodHouseName(baofeigoods.getCangqu());
			newGoods.setGoodsPosition(baofeigoods.getKuwei());
			newGoods.setGoodsStyle("报废入库");
			newGoods.setGoodsChangeTime(Util.getDateTime());
			newGoods.setGoodsCurQuantity(0f);
			totalDao.save(newGoods);
			baofeigoods.setNewGoodsId(newGoods.getGoodsId());
			String processName = "库存报废申请审核";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						BaoFeiGoods.class, baofeigoods.getId(), "ep_status",
						"id", "goodsAction!getOneGoods.action?id="
								+ baofeigoods.getGoodsId()
								+ "&tag=update&pagestatus=show", user.getDept()
								+ "部门 " + baofeigoods.getUsername()
								+ "库存报废申请审核，请您审批", true);
				if (epId != null && epId > 0) {
					baofeigoods.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						// baofeigoods.setGoodsCurQuantity(baofeigoods
						// .getGoodsCurQuantity()
						// - baofeigoods.getSq_num());
						// if (baofeigoods.getGoodsCurQuantity() < 0) {
						// baofeigoods.setGoodsCurQuantity(0f);
						// }
						baofeigoods.setEp_status("同意");
						// goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						// - sq_num);
						// totalDao.update(goods);
					} else {
						baofeigoods.setEp_status("未审批");
					}
					totalDao.update(baofeigoods);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			}
			return "true";
		}
		return null;
	}

	@Override
	public List<BaoFeiGoods> showbfgList(int pageNo, int pageSize) {
		String hql = "from BaoFeiGoods order by id desc";
		return (List<BaoFeiGoods>) totalDao
				.findAllByPage(hql, pageNo, pageSize);
	}

	@Override
	public Map<Integer, Object> findbfgListByCondition(BaoFeiGoods baofeigoods,
			int pageNo, int pageSize) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if (baofeigoods == null) {
			baofeigoods = new BaoFeiGoods();
		}
		String hql = totalDao.criteriaQueries(baofeigoods, null);
		int count = totalDao.getCount(hql);
		List<BaoFeiGoods> bfgList = (List<BaoFeiGoods>) totalDao.findAllByPage(
				hql + " order by id desc", pageNo, pageSize);
		map.put(1, bfgList);
		map.put(2, count);
		return map;
	}

	@Override
	public int getbfgcount() {
		String hql = "from BaoFeiGoods";
		return totalDao.getCount(hql);
	}

	@Override
	public boolean delbfg(BaoFeiGoods bfg) {
		if (bfg != null) {
			BaoFeiGoods baoFeiGoods = (BaoFeiGoods) totalDao.getObjectById(
					BaoFeiGoods.class, bfg.getId());

			if (baoFeiGoods.getEp_status() != null
					&& baoFeiGoods.getEp_status().equals("同意")) {
				Goods goods = (Goods) totalDao.getObjectById(Goods.class,
						baoFeiGoods.getGoodsId());
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						+ baoFeiGoods.getSq_num());
				totalDao.update(goods);
			}

			if (baoFeiGoods.getEpId() != null) {
				CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, baoFeiGoods.getEpId());
				if (circuitRun != null) {
					totalDao.delete(circuitRun);
				}
			}

			Goods newGoods = (Goods) totalDao.getObjectById(Goods.class,
					baoFeiGoods.getNewGoodsId());
			totalDao.delete(newGoods);
			return totalDao.delete(baoFeiGoods);
		}
		return false;
	}

	@Override
	public Map<Integer, Object> findZJgoodsStore(Map mapgoods, int pageNo,
			int pageSize, String type) {
		String hql = "from Goods where 1=1";
		if (mapgoods != null && mapgoods.size() > 0) {
			if (mapgoods.get("goodsMarkId") != null) {
				hql += " and goodsMarkId like '%" + mapgoods.get("goodsMarkId")
						+ "%'";
			}
			if (mapgoods.get("goodsLotId") != null) {
				hql += " and goodsLotId like '%" + mapgoods.get("goodsLotId")
						+ "%'";
			}
			if (mapgoods.get("goodHouseName") != null) {
				hql += " and goodHouseName like '%"
						+ mapgoods.get("goodHouseName") + "%'";
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if ("CS".equals(type)) {
			hql += " and  goodsCurQuantity>0 "
					+ " and goodsClass in ( '外购件库','外协库','委外库')  "
					+ " and kgliao = 'CS' order by goodsnexttime desc desc";
		} else {
			hql += " and  goodsCurQuantity>0 "
					+ " and goodsClass in ( '外购件库','外协库','委外库') "
					+ " and ( kgliao is null or kgliao <> 'CS' ) order by goodsnexttime desc desc";
		}

		Integer count = totalDao.getCount(hql);
		List<Goods> gList = totalDao.findAllByPage(hql, pageNo, pageSize);
		map.put(1, gList);
		map.put(2, count);
		return map;
	}

	@Override
	public List<Goods> findgoodsDQ(Goods goods, String type) {
		if (goods == null) {
			goods = new Goods();
		}
		String hql = totalDao
				.criteriaQueries(goods, null, "ckCount", "ylshifa");
		String hql_other = "";
		if ("CS".equals(type)) {
			hql_other = "and goodsCurQuantity>0 and "
					+ " DATEDIFF(goodsnexttime,CURRENT_DATE)<=60 and DATEDIFF(goodsnexttime,CURRENT_DATE)>0"
					+ " and goodsClass in('外协库','外购件库','委外库') and kgliao = 'CS' "
					+ "order by goodsnexttime desc";
		} else {
			hql_other = "and goodsCurQuantity>0 and "
					+ " DATEDIFF(goodsnexttime,CURRENT_DATE)<=15 and DATEDIFF(goodsnexttime,CURRENT_DATE)>0"
					+ " and goodsClass in('外协库','外购件库','委外库') and (kgliao is null or kgliao <> 'CS') "
					+ "order by goodsnexttime desc";
		}

		return totalDao.find(hql + hql_other);
	}

	@Override
	public String QRgoodsZJ(Integer id, String status) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!~";
		}
		String msg = "";
		if (id != null && id > 0) {
			Goods goods = (Goods) totalDao.get(Goods.class, id);
			if (goods.getGoodsround() == null || goods.getGoodsround() == 0) {
				String hql = "from YuanclAndWaigj where markId=?";
				if (goods.getGoodsFormat() != null
						&& goods.getGoodsFormat().length() > 0) {
					hql += "  and specification= '" + goods.getGoodsFormat()
							+ "'";
				} else {
					hql += " and (specification is not null or specification = '')";
				}
				if (goods.getBanBenNumber() != null
						&& goods.getBanBenNumber().length() > 0) {
					hql += " and banbenhao = '" + goods.getBanBenNumber() + "'";
				} else {
					hql += " and (banbenhao is null or banbenhao = '')";
				}
				if (goods.getKgliao() != null && goods.getKgliao().length() > 0) {
					hql += " and kgliao = '" + goods.getKgliao() + "'";
				} else {
					hql += " and (kgliao is null or kgliao = '')";
				}

				YuanclAndWaigj yw = (YuanclAndWaigj) totalDao
						.getObjectByCondition(hql, goods.getGoodsMarkId());
				if (yw != null) {
					goods.setGoodsround(yw.getRound());
				}
			}
			if ((goods.getGoodsround() == null || goods.getGoodsround() == 0)
					&& !"zkbl".equals(status)) {
				msg = "该库存没有质检周期或质检周期为0";
			} else if (goods.getGoodsround() != null) {
				String nexttime = Util.getSpecifiedDayAfter(Util.getDateTime(),
						goods.getGoodsround().intValue());
				goods.setGoodsnexttime(nexttime);
				goods.setGoodslasttime(Util.getDateTime());
				// 生成一条合格的检验记录.
				OsRecord or = new OsRecord();
				or.setVerification("合格");
				or.setMarkId(goods.getGoodsMarkId());
				or.setProName(goods.getGoodsFullName());
				or.setGysId(goods.getSupplierId());
				or.setGysName(goods.getGoodsSupplier());
				or.setBanbenNumber(goods.getBanBenNumber());
				or.setGongxuName(goods.getProcessName());
				or.setSpecification(goods.getGoodsFormat());
				or.setUnit(goods.getGoodsUnit());
				or.setJcpc(goods.getGoodsLotId());
				or.setQuantity(goods.getGoodsCurQuantity());
				or.setJyNumber(goods.getGoodsCurQuantity());
				or.setHgNumber(goods.getGoodsCurQuantity());
				or.setUserCode(user.getCode());
				or.setUserId(user.getId() + "");
				or.setUsername(user.getName());
				or.setNowDate(Util.getDateTime());
				or.setWgType(goods.getWgType());
				or.setZhonglei("超期复检");
				totalDao.save(or);
				msg = totalDao.update(goods) + "";
			}

		}
		return msg;
	}

	@Override
	public List<Goods> findgoodsDQ1(Goods goods, String type) {
		if (goods == null) {
			goods = new Goods();
		}
		String hql = totalDao
				.criteriaQueries(goods, null, "ckCount", "ylshifa");
		String hql_other = "";
		if ("CS".equals(type)) {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=0 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and kgliao ='CS' order by goodsnexttime desc";
		} else {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=0 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and (kgliao is null or kgliao <> 'CS') order by goodsnexttime desc";
		}
		return totalDao.query(hql + hql_other);
	}

	@Override
	public Map<Integer, Object> findgoodsDQ1(Goods goods, String type,
			Integer pageSize, Integer pageNo) {
		if (goods == null) {
			goods = new Goods();
		}
		String hql = totalDao
				.criteriaQueries(goods, null, "ckCount", "ylshifa");
		String hql_other = "";
		if ("CS".equals(type)) {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=0 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and kgliao ='CS' order by goodsnexttime desc";
		} else {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=0 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and (kgliao is null or kgliao <> 'CS') order by goodsnexttime desc";
		}
		List<Goods> list = totalDao.findAllByPage(hql + hql_other, pageNo,
				pageSize);
		Integer count = totalDao.getCount(hql + hql_other);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, list);
		map.put(2, count);
		return map;
	}

	/****
	 * 扫码出库(对接BTS，订单和库存自动扣除1件)
	 * 
	 * @param ywMarkId
	 * @param orderNumber
	 * @return
	 */
	@Override
	public String outGoodsByBts(String ywMarkId, String orderNumber,
			String fproductno) {
		if (orderNumber != null && orderNumber.length() > 0) {
			String hql = "from OrderManager where orderNum=? or outOrderNumber=?";
			OrderManager orderManager = (OrderManager) totalDao
					.getObjectByCondition(hql, orderNumber, orderNumber);
			if (orderManager != null) {
				String addSql = null;
				if (ywMarkId != null && ywMarkId.length() > 0) {
					addSql = "and ywMarkId=?";
				}
				// 内部计划的出库数量
				// InternalOrderDetail iod2 = (InternalOrderDetail) totalDao
				// .getObjectByCondition(
				// "from InternalOrderDetail where 1=1 "
				// + addSql
				// +
				// " and internalOrder in (select planOrderId from Procard where markId=? and selfCard=? and productStyle='批产')",
				// markId, markId, sell.getSellLot());
				// if (iod2 != null) {
				// if (iod2.getSellCount() == null) {
				// iod2.setSellcount(*);
				// }
				// iod2.setSellCount((int) (iod2.getSellCount() + sell
				// .getSellCount()));
				// totalDao.update(iod2);
				// }
				// 有内部订单号,根据内部订单号和件号去填补

				ProductManager product = (ProductManager) totalDao
						.getObjectByQuery(
								"from ProductManager where (orderManager.orderNum=? or orderManager.outOrderNumber=?) "
										+ addSql, orderNumber, orderNumber,
								ywMarkId);
				if (product == null) {
					return "抱歉,未查找到该条码对应的PO信息!";
				}
				// if (product.getApplyNumber() == null) {
				// product.setApplyNumber((int) (float) sell
				// .getSellCount());
				// } else {
				// product
				// .setApplyNumber((int) (product.getApplyNumber() + sell
				// .getSellCount()));
				// }

				/*****
				 * 开始扣除库存
				 */
				String markId = product.getPieceNumber();
				Goods g = (Goods) totalDao
						.getObjectByCondition(
								"from Goods where goodsMarkId=? and goodsClass='成品库' and goodsCurQuantity>0",
								markId);
				if (g != null) {
					Integer noCount = totalDao.getCount(
							"from Sell where fproductno=? ", fproductno);
					if (noCount > 0) {
						return "抱歉,该条码已经扫描过!";
					}
					Users loginUser = Util.getLoginUser();
					Sell sell = new Sell();
					sell.setFproductno(fproductno);
					sell.setSellMarkId(g.getGoodsMarkId());
					sell.setSellLot(g.getGoodsLotId());
					sell.setSellGoods(g.getGoodsFullName());
					sell.setSellAdminName(loginUser.getName());
					sell.setSellWarehouse("成品库");
					sell.setSellAdminId(loginUser.getId());
					sell.setSellTime(Util.getDateTime());
					sell.setSellCount(1F);
					g.setGoodsCurQuantity(g.getGoodsCurQuantity()
							- sell.getSellCount());
					totalDao.save(sell);// 出库记录
					totalDao.update(g);// 库存

					// 更新订单信息
					if (product.getSellCount() == null) {
						product.setSellCount(0f);
					}
					product.setSellCount((product.getSellCount() + 1));// 订单完成数量+1

					if (product.getAllocationsNum().floatValue() == product
							.getNum().floatValue()) {// 订单产品完成
						String hql1 = "from ProductManager where orderManager.id=? and allocationsNum<num";
						int count = totalDao.getCount(hql1, product
								.getOrderManager().getId());
						if (count == 0) {// 订单对应的产品都已交付
							OrderManager orderManager1 = (OrderManager) totalDao
									.getObjectById(OrderManager.class, product
											.getOrderManager().getId());
							orderManager1.setDeliveryStatus("是");
							totalDao.update(orderManager1);
						}
					}

					String paymentDate = product.getOrderManager()
							.getPaymentDate();
					if (paymentDate != null) {// 订单及时率
						if (Util.compareTime(paymentDate, "yyyy-MM-dd", Util
								.getDateTime(), "yyyy-MM-dd")) {
							// 完成时间在前
							if (product.getTimeNum() == null) {
								product.setTimeNum(0f);
							}
							product.setTimeNum((product.getTimeNum() + 1));
						}
					}
					// 计算订单交付率
					Integer orderId = product.getOrderManager().getId();
					String hqljfr = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					String hqljfr2 = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					Float pronum = (Float) totalDao.query(hqljfr, orderId).get(
							0);
					Float proallocationsNum = (Float) totalDao.query(hqljfr2,
							orderId).get(0);
					OrderManager orderManager1 = (OrderManager) totalDao
							.getObjectById(OrderManager.class, orderId);
					orderManager1.setCompletionrate(proallocationsNum / pronum
							* 100);
					totalDao.update(orderManager1);// 订单
					totalDao.update(product);// 订单产品
					return "true";
				} else {
					return markId + "(" + ywMarkId + ")尚未入库,请先完成入库后,才能扫码出库!谢谢!";
				}

			} else {
				return "数据异常,PO号:" + orderNumber + ",未找到订单信息!";
			}
		} else {
			return "数据异常,该条码无对应PO信息!";
		}
	}

	@Override
	public List findgoodHouselist() {
		// TODO Auto-generated method stub
		List list = totalDao.query("from WareHouse order by id desc ");
		return list;
	}

	@Override
	public Map<Integer, Object> cxchangeStore(ProductManager productManager,
			int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (productManager == null) {
			productManager = new ProductManager();
		}
		String hql = totalDao.criteriaQueries(productManager, null, "cutNum",
				"orderNumber");
		hql += " and orderManager.orderType='正式' and orderManager.ep_statuts='同意'"
				+ " and cxCount>0 and ((cxzkuCount is not null and cxzkuApplyCount is not null and cxCount>(cxzkuCount+cxzkuApplyCount)) "
				+ "or cxzkuApplyCount is null or(cxzkuCount is null  and cxCount>cxzkuApplyCount) )";
		if (productManager.getOrderNumber() != null
				&& productManager.getOrderNumber().length() > 0) {
			hql += " and orderManager.orderNum like '%"
					+ productManager.getOrderNumber() + "%'";
		}
		if (productManager.getOutOrderNumber() != null
				&& productManager.getOutOrderNumber().length() > 0) {
			hql += " and orderManager.outOrderNumber like '%"
					+ productManager.getOutOrderNumber() + "%'";
		}
		int count = totalDao.getCount(hql);
		List<ProductManager> pmList = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		if (pmList != null) {
			for (ProductManager pm : pmList) {
				if (pm.getOrderManager() != null) {
					pm.setOrderNumber(pm.getOrderManager().getOrderNum());
					pm.setOutOrderNumber(pm.getOrderManager()
							.getOutOrderNumber());
				}
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, pmList);
		map.put(2, count);
		return map;
	}

	@Override
	public List<Goods> findtoapplycxzk(Integer id) {
		// TODO Auto-generated method stub
		ProductManager pm = (ProductManager) totalDao.getObjectById(
				ProductManager.class, id);
		if (pm == null) {
			return null;
		}
		List<InternalZsAboutBh> izaList = totalDao
				.query(
						" from InternalZsAboutBh where pzsAboutbhId "
								+ "in(select id from ProductZsAboutBh where zsProductId=?)",
						id);
		List<Goods> goodsList = new ArrayList<Goods>();
		if (izaList != null && izaList.size() > 0) {
			for (InternalZsAboutBh iza : izaList) {
				if (iza.getApplyzkCount() == null) {
					iza.setApplyzkCount(0f);
				}
				if (iza.getZkCount() == null) {
					iza.setZkCount(0f);
				}
				if ((iza.getCount().floatValue()
						- iza.getZkCount().floatValue() - iza.getApplyzkCount()
						.floatValue()) > 0) {// 有需要转库的
					InternalOrderDetail iod = (InternalOrderDetail) totalDao
							.getObjectById(InternalOrderDetail.class, iza
									.getIdetailId());
					if (iod != null) {
						List<Goods> goodsList2 = totalDao
								.query(
										"from Goods where goodsCurQuantity>0 and goodsClass='备货库' and (goodsMarkId =? or goodsMarkId=?) and goodsMarkId+goodsLotId in(select markId+selfCard from Procard where planOrderId =?)",
										iod.getPieceNumber(),
										iod.getYwMarkId(), iod
												.getInternalOrder().getId());
						if (goodsList2 != null && goodsList2.size() > 0) {
							for (Goods goods : goodsList2) {
								if (goods.getGoodsCurQuantity().floatValue() > (iza
										.getCount().floatValue()
										- iza.getZkCount().floatValue() - iza
										.getApplyzkCount().floatValue())) {
									goods.setKzCount((iza.getCount()
											- iza.getZkCount() - iza
											.getApplyzkCount()));
								} else {
									goods.setKzCount((float) goods
											.getGoodsCurQuantity());
								}
							}
							goodsList.addAll(goodsList2);
						}

					}
				}
			}
		}
		return goodsList;
	}

	@Override
	public Map<Integer, Object> gettoapplycxzk(Integer id, Goods goods) {
		// TODO Auto-generated method stub
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		ProductManager pm = (ProductManager) totalDao.getObjectById(
				ProductManager.class, id);
		if (pm.getOrderManager() != null) {
			pm.setOrderNumber(pm.getOrderManager().getOrderNum());
			pm.setOutOrderNumber(pm.getOrderManager().getOutOrderNumber());
		} else {
			return null;
		}
		map.put(1, pm);
		List<InternalZsAboutBh> izaList = totalDao
				.query(
						" from InternalZsAboutBh where pzsAboutbhId "
								+ "in(select id from ProductZsAboutBh where zsProductId=?)",
						id);
		Goods g = new Goods();
		if (izaList != null && izaList.size() > 0) {
			for (InternalZsAboutBh iza : izaList) {
				if (iza.getApplyzkCount() == null) {
					iza.setApplyzkCount(0f);
				}
				if (iza.getZkCount() == null) {
					iza.setZkCount(0f);
				}
				if ((iza.getCount().floatValue()
						- iza.getZkCount().floatValue() - iza.getApplyzkCount()
						.floatValue()) > 0) {// 有需要转库的
					InternalOrderDetail iod = (InternalOrderDetail) totalDao
							.getObjectById(InternalOrderDetail.class, iza
									.getIdetailId());
					if (iod != null) {
						g = (Goods) totalDao
								.getObjectByCondition(
										"from Goods where goodsId=? and goodsCurQuantity>0 and goodsClass='备货库' and goodsMarkId+goodsLotId in(select markId+selfCard from Procard where planOrderId =?)",
										goods.getGoodsId(), iod
												.getInternalOrder().getId());
						if (g != null) {
							if (g.getGoodsCurQuantity().floatValue() > (iza
									.getCount().floatValue()
									- iza.getZkCount().floatValue() - iza
									.getApplyzkCount().floatValue())) {
								g
										.setKzCount((iza.getCount()
												- iza.getZkCount() - iza
												.getApplyzkCount()));
							} else {
								g.setKzCount((float) g.getGoodsCurQuantity());
							}
							map.put(2, g);
							return map;
						}

					}
				}
			}
		}
		return map;
	}

	@Override
	public String applycxzk(Integer id, Goods goods) {
		// TODO Auto-generated method stub
		ProductManager pm = (ProductManager) totalDao.getObjectById(
				ProductManager.class, id);
		if (pm == null) {
			return "对不起,对应的订单没有查到!";
		}
		Goods g = (Goods) totalDao.getObjectById(Goods.class, goods
				.getGoodsId());
		if (g == null) {
			return "对不起,没有找到对应的库存!";
		}
		Users user = Util.getLoginUser();
		if (user == null) {
			return "对不起,请先登录!";
		}
		if (g.getGoodsCurQuantity() < goods.getKzCount()) {
			return "对不起,可转数量超额!";
		}
		// 找到对应的正式预备关系
		List<InternalZsAboutBh> izabList = totalDao
				.query(
						"from InternalZsAboutBh where idetailId in(select id from InternalOrderDetail where pieceNumber=? and internalOrder.id in(select planOrderId from Procard where markId=? and selfCard=? ))"
								+ " and pzsAboutbhId in(select id from ProductZsAboutBh where zsProductId=?) ",
						g.getGoodsMarkId(), g.getGoodsMarkId(), g
								.getGoodsLotId(), id);
		if (izabList == null||izabList.size()==0) {
			return "对不起没有知道到对应的冲销关系!";
		}
		Float zkCount=goods.getKzCount();
		for(InternalZsAboutBh izab:izabList){
			if (izab.getApplyzkCount() == null) {
				izab.setApplyzkCount(0f);
			}
			if (izab.getZkCount() == null) {
				izab.setZkCount(0f);
			}
			Float thiszkCount = izab.getCount() - izab.getZkCount() - izab.getApplyzkCount();
			if (thiszkCount>0) {
				if(thiszkCount>=zkCount){
					thiszkCount = zkCount;
					zkCount=0f;
				}else{
					zkCount=zkCount-thiszkCount;
				}
				izab.setApplyzkCount(izab.getApplyzkCount() + thiszkCount);
				if (pm.getCxzkuApplyCount() == null) {
					pm.setCxzkuApplyCount(thiszkCount);
				} else {
					pm.setCxzkuApplyCount(pm.getCxzkuApplyCount()
							+ thiszkCount);
				}
				totalDao.update(izab);
				totalDao.update(pm);
				// 成品待入库
				GoodsStore goodsStore = new GoodsStore();
				goodsStore.setGoodsStoreMarkId(g.getGoodsMarkId());
				goodsStore.setYwmarkId(g.getYwmarkId());
				goodsStore.setBanBenNumber(g.getBanBenNumber());
				goodsStore.setGoodsStoreLot(g.getGoodsLotId());
				goodsStore.setGoodsStoreGoodsName(g.getGoodsFullName());
				goodsStore.setApplyTime(Util.getDateTime());
				goodsStore.setTuhao(g.getTuhao());
				goodsStore.setGoodsStoreArtsCard(g.getGoodsLotId());
				goodsStore.setGoodsStorePerson(user.getName());
				goodsStore.setStatus("待入库");
				goodsStore.setStyle("冲销转库");
				goodsStore.setGoodsStoreWarehouse("成品库");
				goodsStore.setGoodsStorePosition(goods.getGoodsPosition());
				goodsStore.setGoodHouseName(goods.getGoodHouseName());
				goodsStore.setGoodsStoreUnit(g.getGoodsUnit());
				goodsStore.setGoodsStoreCount(thiszkCount);
				// goodsStore.setPlanID(procard.getPlanOrderId().toString());// 内部计划id
				goodsStore.setNeiorderId(pm.getOrderManager().getOrderNum());
				goodsStore.setWaiorderId(pm.getOrderManager().getOutOrderNumber());
				goodsStore.setIzabId(izab.getId());
				goodsStore.setGoodsStoreCompanyName(pm.getOrderManager()
						.getClientName());
				if (pm.getUnit() != null) {
					goodsStore.setHsPrice(pm.getUnit());// 单价(含税)
				}
				if (pm.getBhsPrice() != null) {
					goodsStore.setGoodsStorePrice(pm.getBhsPrice());// 单价(不含税)
				}
				if (g.getTaxprice() != null) {
					goodsStore.setTaxprice((double) g.getTaxprice()); // 税率
				}
				if (g.getGoodsBuyBhsPrice() != null) {
					Float money = Util.FomartFloat(goodsStore.getGoodsStoreCount()
							* g.getGoodsBuyBhsPrice(), 4);
					goodsStore.setMoney(money.doubleValue());// 总额
				}
				// goodsStore.setPriceId(priceId);// 价格id
				totalDao.save(goodsStore);
			} 
		}
		if(zkCount>0){
			throw new RuntimeException("对不起,可转数量超额!") ;
		}
		// 出库记录
		Sell sellbh = new Sell();
		sellbh.setSellArtsCard(g.getGoodsLotId());
		sellbh.setSellLot(g.getGoodsLotId());
		sellbh.setYwmarkId(g.getYwmarkId());
		sellbh.setSellMarkId(g.getGoodsMarkId());
		sellbh.setSellAdminName(user.getName());
		sellbh.setSellGoods(g.getGoodsFullName());
		sellbh.setSellDate(Util.getDateTime("yyyy-MM-dd"));
		sellbh.setSellTime(Util.getDateTime());
		sellbh.setSellWarehouse("备货库");
		sellbh.setSellUnit(g.getGoodsUnit());
		sellbh.setSellCount((float) goods.getKzCount());
		sellbh.setOrderNum(pm.getOrderManager().getOrderNum());
		sellbh.setOutOrderNumer(pm.getOrderManager().getOutOrderNumber());
		sellbh.setSellAdminName(user.getName());
		totalDao.save(sellbh);
		// 扣除库存
		g.setGoodsCurQuantity(g.getGoodsCurQuantity() - goods.getKzCount());
		totalDao.update(g);
		return "true";
	}

	@Override
	public Object[] showGoodsPhoen(Goods goods, int parseInt, int pageSize,
			String tag) {
		// TODO Auto-generated method stub
		if (goods == null)
			goods = new Goods();
		String hql = totalDao
				.criteriaQueries(goods, null, "ckCount", "ylshifa");
		// if ("code".equals(tag)) hql +=
		// " and cardNum = '"+Util.getLoginUser().getCardId()+"'";
		hql += "  and goodsCurQuantity>0 and goodsClass = '成品库' order by goodsId desc";
		List<Goods> list = totalDao.findAllByPage(hql, parseInt, pageSize);
		for (Goods goo : list) {
			if (goo.getGoodsPosition() != null
					&& !"".equals(goo.getGoodsPosition())) {
				String kuwei = goo.getGoodsPosition().replaceAll(",", "")
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

	@Override
	public String blsqjy(Integer id) {
		if (id != null) {
			Goods goods = (Goods) totalDao.get(Goods.class, id);
			if (goods.getGoodsCurQuantity() <= 0) {
				return "该库存量为0无需申请在库不良检验";
			}
			String hql = " from Goods where goodsMarkId = ? ";
			if (goods.getGoodsLotId() != null
					&& !"".equals(goods.getGoodsLotId())) {
				hql += " and goodsLotId = '" + goods.getGoodsLotId()
						+ "' and goodsClass = '" + goods.getGoodsClass()
						+ "' and goodsCurQuantity >0 ";
			} else {
				return "该库存没有批次无法申请在库不良检验";
			}
			Users user = Util.getLoginUser();
			// 同件号同批次下所有库存出库，然后入到不合格品库
			List<Goods> goodsList = totalDao.query(hql, goods.getGoodsMarkId());
			Float number = 0f;
			WaigouDeliveryDetail wddOld = null;
			for (Goods g : goodsList) {
				number += g.getGoodsCurQuantity();
				// 合格品出库 添加出库记录
				Float num = g.getGoodsCurQuantity();
				Sell sell = new Sell();
				sell.setSellMarkId(g.getGoodsMarkId());// 件号
				sell.setSellWarehouse(g.getGoodsClass());// 库别
				sell.setGoodHouseName(g.getGoodHouseName());// 仓区
				sell.setKuwei(g.getGoodsPosition());// 库位
				sell.setBanBenNumber(g.getBanBenNumber());// 版本号
				sell.setKgliao(g.getKgliao());// 供料属性
				sell.setWgType(g.getWgType());// 物料类别
				sell.setSellGoods(g.getGoodsFullName());// 品名
				sell.setSellFormat(g.getGoodsFormat());// 规格
				if (g.getGoodsCurQuantity() < 0) {// 强制修正，使库存不为负
					g.setGoodsCurQuantity(0f);
					sell.setSellCount(0f);// 出库数量
				} else {
					sell.setSellCount(g.getGoodsCurQuantity());// 出库数量
				}
				sell.setSellLot(g.getGoodsLotId());// 批次
				sell.setStyle("不良出库");// 出库类型
				sell.setSellUnit(g.getGoodsUnit());// 单位
				sell.setSellSupplier(g.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setSellDate(Util.getDateTime("yyyy-MM-dd"));// 出库日期
				sell.setOrderNum(g.getNeiorderId());// 内部订单号
				sell.setSellZhishu(g.getGoodsZhishu());
				sell.setSellCharger(user.getName());// 出库人
				sell.setSellchardept(user.getDept());// 出库人部门
				sell.setTuhao(g.getTuhao());// 图号
				sell.setBout(false);// 出库权限
				sell.setBedit(false);// 编辑权限
				sell.setPrintStatus("YES");// 打印状态
				sell.setSellCharger(user.getName());
				totalDao.save(sell);
				g.setGoodsCurQuantity(0f);
				g.setGoodsZhishu(0f);
				QRgoodsZJ(g.getGoodsId(), "zkbl");
				totalDao.update(g);
				// 入到不合格品库;
				GoodsStore goodsStore = new GoodsStore();
				goodsStore.setGoodsStoreTime(Util.getDateTime());// 入库时间;
				goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));// 入库日期
				goodsStore.setGoodsStorePlanner(Util.getLoginUser().getName());// 操作人
				goodsStore.setStatus("入库");
				goodsStore.setStyle("不合格品入库");
				goodsStore.setGoodsStoreFormat("");
				goodsStore.setGoodsStoreWarehouse("不合格品库");// 库别
				goodsStore.setGoodsStoreMarkId(g.getGoodsMarkId());// 件号
				goodsStore.setGoodsStoreGoodsName(g.getGoodsFullName());// 名称
				goodsStore.setGoodsStoreUnit(g.getGoodsUnit());// 单位
				goodsStore.setKgliao(g.getKgliao());// 供料属性
				goodsStore.setBanBenNumber(g.getBanBenNumber());// 版本号;
				goodsStore.setGoodsStoreSupplier(g.getGoodsSupplier());// 供应商名称
				goodsStore.setWgType(g.getWgType());// 物料类别
				goodsStore.setGoodHouseName(g.getGoodHouseName());// 所属仓区;
				goodsStore.setGoodsStorePosition(g.getGoodsPosition());// 所属库位
				goodsStore.setGoodsStoreZhishu(g.getGoodsZhishu());
				goodsStore.setGoodsStoreZHUnit(g.getGoodsStoreZHUnit());
				goodsStore.setGoodsStoreLot(g.getGoodsLotId());// 批次
				goodsStore.setNeiorderId(g.getNeiorderId());
				goodsStore.setGoodsStoreCount(num);// 入库数量
				String lotId = goods.getGoodsLotId().replaceAll("T", "");
				String hql_wddOld = " from WaigouDeliveryDetail where markId = ? and examineLot = ?";
				wddOld = (WaigouDeliveryDetail) totalDao.getObjectByCondition(
						hql_wddOld, goods.getGoodsMarkId(), lotId);
				if (wddOld != null) {
					goodsStore.setWwddId(wddOld.getId());
					goodsStore.setHsPrice(wddOld.getHsPrice().doubleValue());// 含税单价
					goodsStore.setGoodsStorePrice(wddOld.getPrice()
							.doubleValue());// 不含税单价
					goodsStore.setGoodsStoreSendId(wddOld.getWaigouDelivery()
							.getPlanNumber());
					goodsStore.setTaxprice(wddOld.getTaxprice());
				} else {
					return "";
				}
				goodsStoreServer.saveSgrk(goodsStore);
			}
			// 创建不良品处理单\
			DefectiveProduct dp = new DefectiveProduct();
			if (wddOld != null) {
				dp.setCgOrderNum(wddOld.getCgOrderNum());// 采购订单号
				dp.setShOrderNum(wddOld.getWaigouDelivery().getPlanNumber());// 送货单号;
				dp.setMarkId(wddOld.getMarkId());
				;// 件号
				dp.setYwmarkId(wddOld.getYwmarkId());// 业务件号
				dp.setKgliao(wddOld.getKgliao());// 供料属性（自购、指定、客供）
				dp.setBanben(wddOld.getBanben());// 版本
				dp.setBanci(wddOld.getBanci());// 版次
				dp.setTuhao(wddOld.getTuhao());// 图号
				dp.setProName(wddOld.getProName());// 零件名称、
				dp.setWgType(wddOld.getWgType());// 物料类别
				dp.setSpecification(wddOld.getSpecification());// 规格
				dp.setUnit(wddOld.getUnit());// 单位
				dp.setHsPrice(wddOld.getHsPrice().floatValue());// 含税单价
				dp.setPrice(wddOld.getPrice().floatValue());// 不含税单价
				dp.setTaxprice(wddOld.getTaxprice());// 税率
				dp.setPriceId(wddOld.getPriceId());// 价格Id
				dp.setGysName(wddOld.getGysName());// 供应商名称
				dp.setGysId(wddOld.getGysId());// 供应商Id;
				dp.setZhuanhuanUit(wddOld.getZhuanhuanUnit());
				dp.setGysUsersId(wddOld.getWaigouDelivery().getUserId());// 供应商UserId
				dp.setExamineLot(goods.getGoodsLotId());// 检验批次
				dp.setWgddId(wddOld.getId());// 送货单Id
			} else {
				throw new RuntimeException("未找到相对应的送货单，无法申请在库不良检验。");
			}
			dp.setJyuserName(user.getName());
			dp.setJyuserCode(user.getCode());
			dp.setJyuserId(user.getId());
			dp.setLlNumber(wddOld.getQrNumber());// 来料数量
			dp.setJyNumber(number);// 检验数量
			dp.setJybhgNumber(number);// 检验不合格数量
			dp.setJyhgNumber(0f);// 检验合格数量
			dp.setDbNumber(number);// 调拨数量
			if ("外购件库".equals(goods.getGoodsClass())) {
				dp.setType("外购在库不良");
				dp.setWlType("外购");
				// 外购件修改物料需求表上的入库数量
				ManualOrderPlan mop = (ManualOrderPlan) totalDao
						.getObjectByCondition(
								" from ManualOrderPlan where id in( select mopId from WaigouPlan where id = ? )"
										+ " ", wddOld.getWaigouPlanId());
				mop.setRukuNum(mop.getRukuNum() - number);
				mop.setQsCount(mop.getQsCount() - number);
				mop.setHgCount(mop.getHgCount() - number);
				Float flagNumber = number;
				Set<ManualOrderPlanDetail> modset = mop.getModSet();
				for (ManualOrderPlanDetail mod : modset) {
					if (flagNumber > 0) {
						if (flagNumber >= mod.getRukuNum()) {
							flagNumber -= mod.getRukuNum();
							mod.setRukuNum(0f);
						} else if (flagNumber < mod.getRukuNum()) {
							mod.setRukuNum(mod.getRukuNum() - flagNumber);
							flagNumber = 0f;
						}

					}
					totalDao.update(mod);
				}
				totalDao.update(mop);

			} else if ("外协库".equals(goods.getGoodsClass())) {
				dp.setType("外委在库不良");
				dp.setWlType("外委");
			}
			dp.setStatus("待确认");
			dp.setAddTime(Util.getDateTime());// 添加时间
			dp.setAddUser(Util.getLoginUser().getName());// 添加人
			// 发消息给添加人品质
			if ("外购件库".equals(goods.getGoodsClass())) {
				AlertMessagesServerImpl.addAlertMessages("外购待确认不良品管理", user
						.getName()
						+ "申请的件号:"
						+ goods.getGoodsMarkId()
						+ ",批次:"
						+ goods.getGoodsLotId() + ",已生成，" + "请前往确认不合格数量。", "1");
			} else if ("外协库".equals(goods.getGoodsClass())) {
				AlertMessagesServerImpl.addAlertMessages("外委待确认不良品管理", user
						.getName()
						+ "申请的件号:"
						+ goods.getGoodsMarkId()
						+ ",批次:"
						+ goods.getGoodsLotId() + ",已生成，" + "请前往确认不合格数量。", "1");
			}

			return totalDao.save(dp) + "";
		}
		return "";
	}

	@Override
	public void sendmsg(Integer id) {
		if (id != null) {
			Goods goods = (Goods) totalDao.get(Goods.class, id);
			String hql_user = "SELECT u.id from Category c join c.userSet u where c.name=?";
			List<Integer> userIdList = totalDao.query(hql_user, goods
					.getWgType());
			Integer[] userIds = null;
			if (userIdList != null && userIdList.size() > 0) {
				userIds = new Integer[userIdList.size()];
				for (int i = 0; i < userIdList.size(); i++) {
					userIds[i] = userIdList.get(i);
				}
			}
			String hql_ = " select DATEDIFF(goodsnexttime,CURRENT_DATE) from Goods";
			Integer count = (Integer) totalDao.getObjectByCondition(hql_);
			AlertMessagesServerImpl.addAlertMessages("检验提醒", "<p>件号:"
					+ goods.getGoodsMarkId() + ",名称:"
					+ goods.getGoodsFullName() + ",库别:" + goods.getGoodsClass()
					+ ",仓区:" + goods.getGoodHouseName() + ",库位:"
					+ goods.getGoodsPosition() + "检验日期为:"
					+ goods.getGoodsnexttime() + ",还有" + count
					+ "天到期，请及时前往检验。</p>", userIds, "", true);
		} else {
			// String hql_goods = "from Goods where goodsCurQuantity>0 and "
			// +
			// " DATEDIFF(goodsnexttime,CURRENT_DATE)<=15 and DATEDIFF(goodsnexttime,CURRENT_DATE)>0"
			// + " and goodsClass in('外协库','外购件库','委外库') "
			// + "order by goodsnexttime desc";
			// List<Goods> goodsList = totalDao.find(hql_goods);
			// if (goodsList != null && goodsList.size() > 0) {
			// AlertMessagesServerImpl.addAlertMessagesxt("库存质检管理", "有"
			// + goodsList.size() + "条库存质检周期已到，请及时检验", "库存质检管理提醒");
			// }
		}
	}

	@Override
	public String bcpOut(Goods goods, String pwsswords) {
		// TODO Auto-generated method stub
		Users login = Util.getLoginUser();
		if(login==null){
			return "请先登录!";
		}
		Goods old = (Goods) totalDao.getObjectById(Goods.class, goods
				.getGoodsId());
		Users user = (Users) totalDao
				.getObjectByCondition(
						"from Users where cardId=? and onWork not in('离职','离职中','内退','退休')",
						goods.getLingliaocardId());
		if (user == null) {
			return "请刷有效员工卡!";
		}
		MD5 md5 = new MD5();
		String mdsPassword = md5.getMD5(pwsswords.getBytes());// 密码MD5转换
		Password oldpassword = user.getPassword();
		if (!oldpassword.getPassword().equals(mdsPassword)) {
			return "刷卡人密码有误!";
		}
		if (old == null) {
			return "没有找到对应的库存信息!";
		}
		if (old.getGoodsCurQuantity() == null
				|| old.getGoodsCurQuantity() < goods.getGoodsCurQuantity()) {
			return "对不起领取数量超过库存数量!";
		}
		String  checkckqx = GoodsStoreServerImpl.checkquanxian(login, old.getGoodsClass(), "out", totalDao);
		if(!checkckqx.equals("true")){
			return checkckqx;
		}
		// List<ProcardProductRelation> pprList = (List<ProcardProductRelation>)
		// totalDao
		// .query("from ProcardProductRelation where goodsId=?", goods
		// .getGoodsId());
		List<ProcardProductRelation> pprList = (List<ProcardProductRelation>) totalDao
				.query(
						"from ProcardProductRelation where goodsId=?  and procardId=?",
						goods.getGoodsId(), goods.getProcardId());
		Float outCount = goods.getGoodsCurQuantity();
		String rootSelfCard = "";
		String ywMarkId = "";
		String neiOrderNum = "";
		String sellTime = Util.getDateTime();
		if (pprList != null && pprList.size() > 0) {
			for (ProcardProductRelation ppr : pprList) {
				if (ppr != null && ppr.getZyCount() > ppr.getCkCount()) {
					Float thistCount = null;
					Procard procard = (Procard) totalDao.getObjectByCondition(
							"from Procard where id =?", goods.getProcardId());
					rootSelfCard = procard.getRootSelfCard();
					ywMarkId = procard.getYwMarkId();
					neiOrderNum = procard.getOrderNumber();
					if (procard == null) {
						return "对不起没有找到对应的生产件!";
					}
					// if(procard.getZaizhizkCount()==null||procard.getZaizhizkCount()<goods.getGoodsCurQuantity()){
					// return
					// "对不起，对应的生产件在库半成品数量为"+procard.getZaizhizkCount()+procard.getUnit()+",领取数量超额!";
					// }
					Float zkCount = ppr.getZyCount() - ppr.getCkCount();// 关联表中总数量-已出库数量就是需要出库的数量
					if (zkCount < outCount) {
						outCount = outCount - zkCount;
						thistCount = zkCount;
						if (thistCount < 0
								|| thistCount > procard.getZaizhizkCount()) {// 说明数据有问题
							throw new RuntimeException(procard.getMarkId()
									+ "的第" + procard.getSelfCard()
									+ "批次半成品在库数量异常请管理员核查!");
						}
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								- zkCount);
						ppr.setCkCount(ppr.getCkCount() + zkCount);
						totalDao.update(procard);
						totalDao.update(ppr);
					} else {
						thistCount = outCount;
						if (thistCount < 0
								|| thistCount > procard.getZaizhizkCount()) {// 说明数据有问题
							throw new RuntimeException(procard.getMarkId()
									+ "的第" + procard.getSelfCard()
									+ "批次半成品在库数量异常请管理员核查!");
						}
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								- outCount);
						ppr.setCkCount(ppr.getCkCount() + outCount);
						outCount = 0f;
						totalDao.update(procard);
						totalDao.update(ppr);
					}
					// 填充工序
					// 1.查出最后一道工序号
					Integer maxNo = (Integer) totalDao
							.getObjectByCondition(
									"select max(processNO) from ProcessInfor where procard.id=? ",
									procard.getId());
					// 2.查出最后一个工序卡住数量
					List<Procard> wgjList = totalDao
							.query(
									"from Procard where procard.id=? and procardStyle='外购'  and hascount>0",
									procard.getId());
					if (procard.getHascount() == null) {
						procard.setHascount(procard.getKlNumber());
					}
					Float lastCount = procard.getKlNumber()
							- procard.getHascount();
					if (wgjList != null && wgjList.size() > 0) {
						for (Procard wgj : wgjList) {
							float scCount = (wgj.getKlNumber() - wgj
									.getHascount())
									* wgj.getQuanzi1() / wgj.getQuanzi2();
							scCount = (float) Math.floor(scCount);
							if (scCount < lastCount) {
								lastCount = scCount;
							}
						}
					}
					List<ProcessInfor> nextList = totalDao
							.query(
									"from ProcessInfor where processNO>? and procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO",
									old.getProcessNo(), procard.getId());
					if (nextList != null && nextList.size() > 0) {
						String upProcesstype = "";
						String upNeedSave = null;
						boolean b = true;// 半成品转库之后工序限制可领数量，半成品转库或者领取之后解开限制数量
						float lastTotalCount = -1;// 上道工序可领数量
						for (int i = 0; i < nextList.size(); i++) {
							ProcessInfor processInfor = nextList.get(i);
							if (b) {
								if ((upNeedSave != null && upNeedSave
										.equals("是"))
										&& (processInfor.getNeedSave() == null || !processInfor
												.getNeedSave().equals("是"))) {
									b = false;
								}
								if (upProcesstype.equals("外委")
										&& processInfor.getProductStyle() != null
										&& processInfor.getProductStyle()
												.equals("自制")) {
									b = false;
								}
								if (upProcesstype.equals("外委")
										&& processInfor.getProductStyle() != null
										&& processInfor.getProductStyle()
												.equals("外委")
										&& processInfor.getProcessStatus() != null
										&& processInfor.getProcessStatus()
												.equals("no")) {
									b = false;
								}
							}
							upProcesstype = processInfor.getProductStyle();
							upNeedSave = processInfor.getNeedSave();
							if (i == (nextList.size() - 1)
									&& ((procard.getKlNumber() - procard
											.getHascount()) > lastCount)) {// 部分领料最后一道工序的可领数量为最小minNumber-
								maxNo = processInfor.getProcessNO();// 最大工序号
								if (!b) {
									continue;
								} else {
									if ((processInfor.getTotalCount() + thistCount) < lastCount) {
										processInfor.setTotalCount(processInfor
												.getTotalCount()
												+ thistCount.intValue());
									} else {
										processInfor.setTotalCount((int) Math
												.floor(lastCount));
									}
								}
							} else {
								if (!b) {
									continue;
								} else {
									processInfor.setTotalCount(processInfor
											.getTotalCount()
											+ thistCount.intValue());
									if (processInfor.getTotalCount() > procard
											.getFilnalCount()) {
										// 发送异常消息bgg
										// AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
										// "件号:" + procard.getMarkId() + "批次:"
										// +
										// procard.getSelfCard()+"第"+processInfor.getProcessNO()
										// + "工序可领数量为："
										// + processInfor.getTotalCount()
										// +
										// "大于批次数量，系统自动修复为批次数量"+procard.getFilnalCount()+"，操作是：半成品转库,当前系统时间为"
										// + Util.getDateTime(), "2");
										processInfor.setTotalCount(procard
												.getFilnalCount().intValue());
									}
									if (lastTotalCount > 0
											&& lastTotalCount < processInfor
													.getTotalCount()) {// 不能超过伤到工序的可领数量
										processInfor
												.setTotalCount((int) lastTotalCount);
									}
								}
							}
							lastTotalCount = processInfor.getTotalCount();
							totalDao.update(processInfor);
						}
						// 如果下道工序为外委则做外委入库申请
						int n = 0;
						WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
						// // 查看是否有委外库
						// Float wwckCount = (Float) totalDao
						// .getObjectByCondition("select count(*) from WareHouse where name='委外库'");
						for (ProcessInfor nextWwProcessInfor : nextList) {
							if (nextWwProcessInfor != null) {
								if ("外委".equals(nextWwProcessInfor
										.getProductStyle())
										&& (n == 0 || ("yes")
												.equals(nextWwProcessInfor
														.getProcessStatus()))) {
									if (n == 0) {
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
										wwp.setNumber(thistCount);
										wwp.setBeginCount(thistCount);
										wwCount = thistCount;
										wwp.setAddTime(Util.getDateTime());
										wwp.setJihuoTime(Util.getDateTime());
										wwp.setShArrivalTime(procard
												.getNeedFinalDate());// 应到货时间在采购确认通知后计算
										wwp.setCaigouMonth(Util
												.getDateTime("yyyy-MM月"));// 采购月份
										wwp.setUnit(procard.getUnit());
										String wwNumber = "";
										String before = null;
										Integer bIndex = 10;
										before = "ww"
												+ Util.getDateTime("yyyyMMdd");
										Integer maxNo2 = 0;
										String maxNumber = (String) totalDao
												.getObjectByCondition("select max(planNumber) from WaigouWaiweiPlan where planNumber like '"
														+ before + "%'");
										if (maxNumber != null) {
											String wwnum = maxNumber.substring(
													bIndex, maxNumber.length());
											try {
												Integer maxNum = Integer
														.parseInt(wwnum);
												if (maxNum > maxNo2) {
													maxNo2 = maxNum;
												}
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										maxNo2++;
										wwNumber = before
												+ String.format("%03d", maxNo2);
										wwp.setPlanNumber(wwNumber);// 采购计划编号
										wwp.setSelfCard(procard.getSelfCard());// 批次
										// wwp.setGysId(nextWwProcessInfor
										// .getZhuserId());// 供应商id
										// wwp.setGysName(nextWwProcessInfor
										// .getGys());// 供应商名称
										wwp.setAllJiepai(nextWwProcessInfor
												.getAllJiepai());// 单件总节拍
										wwp
												.setDeliveryDuration(nextWwProcessInfor
														.getDeliveryDuration());// 耽误时长
										wwp.setSingleDuration(procard
												.getSingleDuration());// 单班时长(工作时长)
										wwp.setProcardId(procard.getId());
										wwp.setProcard(procard);
										// if (wwckCount != null
										// && wwckCount > 0) {
										wwp.setStatus("待入库");
										// 在制品待入库
										if (procard.getZaizhiApplyZk() == null) {
											procard.setZaizhiApplyZk(0f);
										}
										if (procard.getZaizhizkCount() == null) {
											procard.setZaizhizkCount(0f);
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
										// 可转库数量=已生产数量-已转库数量-转库申请中数量
										procard.setZaizhikzkCount(procard
												.getFilnalCount()
												- procard.getZaizhizkCount()
												- procard.getZaizhiApplyZk());
										if (procard.getZaizhikzkCount() >= wwCount) {
											procard.setZaizhiApplyZk(procard
													.getZaizhiApplyZk()
													+ wwCount);
											String orderNum = (String) totalDao
													.getObjectByCondition(
															"select orderNumber from Procard where id=?",
															procard.getRootId());
											// 成品待入库
											GoodsStore goodsStore2 = new GoodsStore();
											goodsStore2.setNeiorderId(orderNum);
											goodsStore2
													.setGoodsStoreMarkId(procard
															.getMarkId());
											goodsStore2.setBanBenNumber(procard
													.getBanBenNumber());
											goodsStore2
													.setGoodsStoreLot(procard
															.getSelfCard());
											goodsStore2
													.setGoodsStoreGoodsName(procard
															.getProName());
											goodsStore2.setApplyTime(Util
													.getDateTime());
											goodsStore2
													.setGoodsStoreArtsCard((String) totalDao
															.getObjectByCondition(
																	"select selfCard from Procard where id=?",
																	procard
																			.getRootId()));
											goodsStore2
													.setGoodsStorePerson(Util
															.getLoginUser()
															.getName());
											goodsStore2.setStatus("待入库");
											goodsStore2.setStyle("半成品转库");
											goodsStore2
													.setGoodsStoreWarehouse("委外库");// 库别
											// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
											// 区名
											// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
											// 库位
											goodsStore2
													.setGoodsStoreUnit(procard
															.getUnit());
											goodsStore2
													.setGoodsStoreCount(wwCount);
											goodsStore2.setProcessNo(old
													.getProcessNo());
											goodsStore2.setProcessName(old
													.getProcessName());
											totalDao.update(procard);
											totalDao.save(goodsStore2);
											// 判断外委进委外入库是否要做
											String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='委外库接收半成品' and valueName='委外库接收半成品'";
											String valueCode = (String) totalDao
													.getObjectByCondition(hql1);
											if (valueCode != null
													&& valueCode.equals("否")) {
												// 入库记录直接通过
												goodsStore2.setStatus("入库");
												goodsStore2
														.setPrintStatus("YES");
												totalDao.update(goodsStore2);
												// 增加库存记录
												String hqlgoods = "from Goods where goodsMarkId='"
														+ procard.getMarkId()
														+ "' and goodsLotId='"
														+ procard.getSelfCard()
														+ "' and goodsStyle='半成品转库' and processNo="
														+ old.getProcessNo()
														+ " and goodsClass='委外库'";
												Goods wwgoods = (Goods) totalDao
														.getObjectByCondition(hqlgoods);
												if (wwgoods != null) {
													wwgoods
															.setGoodsCurQuantity(wwgoods
																	.getGoodsCurQuantity()
																	+ goodsStore2
																			.getGoodsStoreCount());
													totalDao.update(wwgoods);
												} else {
													wwgoods = new Goods();
													wwgoods
															.setGoodsMarkId(goodsStore2
																	.getGoodsStoreMarkId());
													wwgoods
															.setGoodsFormat(goodsStore2
																	.getGoodsStoreFormat());
													wwgoods
															.setBanBenNumber(goodsStore2
																	.getBanBenNumber());
													wwgoods
															.setGoodsFullName(goodsStore2
																	.getGoodsStoreGoodsName());
													wwgoods
															.setGoodsClass("委外库");
													wwgoods
															.setGoodsBeginQuantity(goodsStore2
																	.getGoodsStoreCount());
													wwgoods
															.setGoodsCurQuantity(goodsStore2
																	.getGoodsStoreCount());
													totalDao.save(wwgoods);
												}
												// 添加零件与在制品关系表
												ProcardProductRelation pprelation = new ProcardProductRelation();
												pprelation.setAddTime(Util
														.getDateTime());
												pprelation.setProcardId(procard
														.getId());
												pprelation.setGoodsId(wwgoods
														.getGoodsId());
												pprelation
														.setZyCount(goodsStore2
																.getGoodsStoreCount());
												pprelation.setFlagType("本批在制品");
												totalDao.save(pprelation);
												// 将外购外委激活序列状态改为待激活
												wwp.setStatus("待激活");
												// totalDao.save(wwp);
											}
										} else {
											return "对不起超过可申请数量("
													+ procard
															.getZaizhikzkCount()
													+ ")";
										}
										// } else {
										// wwp.setStatus("待激活");
										// }
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
						if (wwp.getStatus() != null
								&& wwp.getStatus().equals("待激活")) {// 说明自动跳过了半成品入委外库操作
							// 下一步操作
							zijihuoww(wwp);
						}
					} else {
						if (procard.getTjNumber() == null) {
							procard.setTjNumber(thistCount);
						} else {
							procard.setTjNumber(procard.getTjNumber()
									+ thistCount);
						}
						if (procard.getTjNumber() > procard.getFilnalCount()) {
							procard.setTjNumber(procard.getFilnalCount());
						}
						if (procard.getTjNumber().equals(
								procard.getFilnalCount())) {
							procard.setStatus("完成");
						}
						totalDao.update(procard);
						// 没有下道工序就激活上次流水卡
						Procard father = procard.getProcard();
						if (father != null) {
							if (father.getLingliaoType() != null
									&& father.getLingliaoType().equals("part")) {
								Float maxMinNumber = (Float) totalDao
										.getObjectByCondition(
												"select max(minNumber) from Procard where procard.id=? and (sbStatus is null or sbStatus!='删除')",
												father.getId());
								if (maxMinNumber != null) {
									if (father.getHascount() == null) {
										father.setHascount(maxMinNumber);
										father.setKlNumber(maxMinNumber);
									} else {
										if (father.getKlNumber() > maxMinNumber) {
											father
													.setHascount(father
															.getHascount()
															+ (father
																	.getKlNumber() - maxMinNumber));
										} else {
											father.setHascount(father
													.getHascount()
													- (maxMinNumber - father
															.getKlNumber()));
											father.setKlNumber(maxMinNumber);
										}
									}
								}
								if (father.getJihuoStatua() == null
										|| father.getJihuoStatua().length() == 0) {
									father.setJihuoStatua("激活");
									father.setStatus("已发卡");
									totalDao.update(father);
								}
							} else {
								Float nullMinNumber = (Float) totalDao
										.getObjectByCondition(
												"select count(*) from Procard where procard.id=? and minNumber is null and (sbStatus is null or sbStatus!='删除')",
												father.getId());
								if (nullMinNumber == null || nullMinNumber == 0) {
									Float minMinNumber = (Float) totalDao
											.getObjectByCondition(
													"select min(minNumber) from Procard where procard.id=? and (sbStatus is null or sbStatus!='删除')",
													father.getId());
									if (minMinNumber != null) {
										if (father.getHascount() == null) {
											father.setHascount(minMinNumber);
											father.setKlNumber(minMinNumber);
										} else {
											if (father.getKlNumber() > minMinNumber) {
												father
														.setHascount(father
																.getHascount()
																+ (father
																		.getKlNumber() - minMinNumber));
											} else {
												father
														.setHascount(father
																.getHascount()
																- (minMinNumber - father
																		.getKlNumber()));
												father
														.setKlNumber(minMinNumber);
											}
										}
										if (father.getJihuoStatua() == null
												|| father.getJihuoStatua()
														.length() == 0) {
											father.setJihuoStatua("激活");
											father.setStatus("已发卡");
											totalDao.update(father);
										}
									}
								}
							}
						}

					}
				}
				if (outCount == 0) {
					break;
				}
			}
		}
		if (outCount > 0) {
			return "对不起，领取数量超过对应的生产件在库半成品数量为" + outCount + old.getGoodsUnit();
		}
		old.setGoodsCurQuantity(old.getGoodsCurQuantity()
				- goods.getGoodsCurQuantity());
		totalDao.update(old);
		Sell sell = new Sell();
		sell.setSellMarkId(old.getGoodsMarkId());// 件号
		sell.setSellWarehouse(old.getGoodsClass());// 库别
		sell.setGoodHouseName(old.getGoodHouseName());// 仓区
		sell.setKuwei(old.getGoodsPosition());// 库位
		sell.setBanBenNumber(old.getBanBenNumber());// 版本号
		sell.setKgliao(old.getKgliao());// 供料属性
		sell.setWgType(old.getWgType());// 物料类别
		sell.setSellGoods(old.getGoodsFullName());// 品名
		sell.setSellFormat(old.getGoodsFormat());// 规格
		sell.setSellCount(goods.getGoodsCurQuantity());// 出库数量
		sell.setSellUnit(old.getGoodsUnit());// 单位
		sell.setSellSupplier(old.getGoodsSupplier());// 供应商
		sell.setSellTime(sellTime);// 出库时间
		sell.setSellDate(Util.getDateTime("yyyy-MM-dd"));// 出库时间
		sell.setTuhao(old.getTuhao());// 图号
		sell.setPrintStatus("NO");// 打印状态
		sell.setProcessNo(old.getProcessNo());
		sell.setProcessName(old.getProcessName());
		sell.setYwmarkId(ywMarkId);// 业务件号
		sell.setOrderNum(neiOrderNum);// 内部订单号
		sell.setRootSelfCard(rootSelfCard);
		sell.setSellLot(old.getGoodsLotId());
		if (old.getGoodsClass().equals("外协库")) {
			sell.setStyle("外协刷卡出库(全)");
		} else {
			sell.setStyle("半成品出库(全)");
		}
		sell.setSellCharger(user.getName());// 领料人
		sell.setSellAdminName(login.getName());// 管理人员
		sell.setGoodsId(old.getGoodsId());
		sell.setGoodsPrice(old.getGoodsPrice());// 库存单价(不含税)
		sell.setSellPrice(old.getGoodsBuyPrice()); // 批次 单价
		sell.setSellbhsPrice(old.getGoodsBuyBhsPrice());// 批次 不含税单价
		sell.setTaxprice(old.getTaxprice());// 批次 税率
		// String printNumber = updatMaxSellPrintNumber(sell, sellTime);
		// sell.setPrintNumber(printNumber);
		return totalDao.save(sell) + "";
	}

	public String zijihuoww(WaigouWaiweiPlan wwp) {
		// 查询激活外委计划是否需要
		String needJihuo = (String) totalDao
				.getObjectByCondition("select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM未激活外委工序计划' and valueName='BOM未激活外委计划'");
		if (needJihuo != null && needJihuo.equals("否")) {
			if (wwp.getPriceId() != null) {// 匹配到了价格直接跳过
				WaigouOrder waigouORder = new WaigouOrder();
				// 订单明细
				Set<WaigouPlan> wwpSet = null;
				ZhUser gys = null;
				Users gysUser = null;
				gys = (ZhUser) totalDao.getObjectById(ZhUser.class, wwp
						.getGysId());

				if (gys != null) {
					gysUser = (Users) totalDao.getObjectById(Users.class, gys
							.getUserid());
				}
				if (gys != null) {
					waigouORder.setZhUser(gys);
					waigouORder.setUser(gysUser);
					waigouORder.setWwSource("BOM外委");
					waigouORder.setRootMarkId(wwp.getRootMarkId());
					waigouORder.setYwMarkId(wwp.getYwMarkId());
					waigouORder.setUserId(gys.getUserid());
					waigouORder.setUserCode(gysUser.getCode());
					waigouORder.setGysId(gys.getId());//
					waigouORder.setGysName(gys.getCmp());// 供应商名称
					waigouORder.setLxPeople(gys.getCperson());// 联系人
					waigouORder.setGysPhone(gys.getCtel());// 电话
					waigouORder.setFax(gys.getCfax());// 传真
					waigouORder.setType("外委");
					// gysWWApply.settype;//票据类型
					// private String condition;//付款条件
					String wwNumber = "";
					String before = null;
					Integer bIndex = 10;
					before = "ww" + Util.getDateTime("yyyyMMdd");
					Integer maxNo = 0;
					String maxNumber = (String) totalDao
							.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
									+ before + "%'");
					if (maxNumber != null) {
						String num = maxNumber.substring(bIndex, maxNumber
								.length());
						try {
							Integer maxNum = Integer.parseInt(num);
							if (maxNum > maxNo) {
								maxNo = maxNum;
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					maxNo++;
					wwNumber = before + String.format("%03d", maxNo);
					waigouORder.setPlanNumber(wwNumber);// 外委订单号码
					waigouORder.setAddUserCode("system");// 采购员工号
					waigouORder.setAddUserName("system");// 采购员姓名
					waigouORder.setAddUserPhone("");// 采购员电话
					waigouORder.setAddTime(Util.getDateTime());// 添加时间
					waigouORder.setRootId(null);// 生产总成id
					waigouORder.setGhAddress(gys.getCompanydz());// 送货地址
					waigouORder.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
					// gysWWApply.setshAddress;//送货地址
					waigouORder.setProcessApplyId(null);// 工序外委表Id
					waigouORder.setStatus("待核对");// 状态
					waigouORder.setApplystatus("未申请");//
				}
				WaigouPlan wgPlan = null;
				if (wwpSet == null || wwpSet.size() == 0) {
					wwpSet = new HashSet<WaigouPlan>();
				} else {
					for (WaigouPlan had : wwpSet) {
						if (had.getMarkId().equals(wwp.getMarkId())
								&& had.getProcessNOs().equals(
										wwp.getProcessNo())) {
							if (had.getBanci() == null) {
								had.setBanci(0);
							}
							if (wwp.getBanci() == null) {
								wwp.setBanci(0);
							}
							if (had.getBanci().equals(wwp.getBanci())) {
								wgPlan = had;
							}
						}
					}
				}
				// 供应商外委订单明细
				if (wgPlan == null) {
					wgPlan = new WaigouPlan();
					wgPlan.setMarkId(wwp.getMarkId());// 件号
					wgPlan.setBanben(wwp.getBanben()); // 版本号
					wgPlan.setProName(wwp.getProName());// 零件名称
					wgPlan.setBanci(wwp.getBanci());
					wgPlan.setProcessNOs(wwp.getProcessNo());// 工序号
					wgPlan.setProcessNames(wwp.getProcessName());// 工序名称
					wgPlan.setWwType("工序外委");// 外委类型（工序外委,包工包料）
					wgPlan.setType("外委");// 外委类型（工序外委,包工包料）
					// wgPlan.setUnit(wwp.getUnit());// 单位
					wgPlan.setPriceId(wwp.getPriceId());// 合同Id
					wgPlan.setUserId(gys.getUserid());// 供应商信息
					wgPlan.setUserCode(gysUser.getCode());
					wgPlan.setGysId(gys.getId());//
					wgPlan.setGysName(gys.getCmp());// 供应商名称
					wgPlan.setNumber(wwp.getNumber());// 数量
					wgPlan.setSyNumber(wwp.getNumber());// 采购数量*供应商采购比例
					wgPlan.setStatus("待核对");
					wgPlan.setUnit(wwp.getUnit());
					// wgPlan.setWwwwpId(wwp.getId());
					Price price = (Price) totalDao.getObjectById(Price.class,
							wwp.getPriceId());
					if (price.getHsPrice() != null) {
						wgPlan.setHsPrice(price.getHsPrice());// 含税单价
						wgPlan.setMoney(price.getHsPrice()
								* wwp.getNumber() );// 总金额
						if (price.getBhsPrice() != null) {
							wgPlan.setPrice(price
									.getBhsPrice());// 不含税单价
						} else {
							wgPlan.setPrice(0d);
						}
						wgPlan.setTaxprice(price.getTaxprice()); // 税率
					} else {
						wgPlan.setHsPrice(0d);// 含税单价
						wgPlan.setPrice(0d);
						wgPlan.setMoney(0d);// 总金额
						wgPlan.setPrice(0d);// 不含税单价
						wgPlan.setTaxprice(0d); // 税率
					}
					// gyswwwwp.setpayDate;//交付日期
					wgPlan.setRemark(wwp.getYwMarkId());// 备注
					wgPlan.setWaigouOrder(waigouORder);
					wgPlan.setWwSource("BOM外委");
					totalDao.save(wgPlan);
					// 中间表
					ProcardWGCenter zjb = new ProcardWGCenter();
					zjb.setWgOrderId(wgPlan.getId());
					zjb.setProcardId(wwp.getProcardId());
					zjb.setProcardCount(wwp.getNumber());
					zjb.setConnectionType("mTom");
					totalDao.save(zjb);
					wwpSet.add(wgPlan);
					waigouORder.setWwpSet(wwpSet);
					waigouORder.setType("外委");
					waigouORder.setWwType("工序外委");
					if (waigouORder.getId() == null) {
						totalDao.save(waigouORder);
					} else {
						totalDao.update(waigouORder);
					}
				} else {
					wgPlan.setNumber(wgPlan.getNumber() + wwp.getNumber());
					wgPlan.setSyNumber(wgPlan.getSyNumber() + wwp.getNumber());
					wgPlan.setMoney(wgPlan.getNumber() * wgPlan.getHsPrice());
					// 中间表
					ProcardWGCenter zjb = new ProcardWGCenter();
					zjb.setWgOrderId(wgPlan.getId());
					zjb.setProcardId(wwp.getProcardId());
					zjb.setProcardCount(wwp.getNumber());
					zjb.setConnectionType("mTom");
					totalDao.save(zjb);
					totalDao.update(wgPlan);
				}
				wwp.setStatus("待出库");
				totalDao.update(wwp);
				// 申请审批流程是否跳过
				passWWapply(waigouORder);
			}
		}
		return "true";
	}

	private void passWWapply(WaigouOrder waigouOrder) {
		// TODO Auto-generated method stub
		// 查询激活外委计划是否需要
		String needJihuo = (String) totalDao
				.getObjectByCondition("select valueCode from CodeTranslation where type = 'sys' and keyCode='外委待申请订单' and valueName='待申请外委订单'");
		if (needJihuo != null && needJihuo.equals("否")) {
			try {
				String processName = "BOM外委采购订单申请";
				Integer epId = CircuitRunServerImpl.createProcess(processName,
						WaigouOrder.class, waigouOrder.getId(), "applystatus",
						"id",
						"WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=findAll&id="
								+ waigouOrder.getId(), processName, true);
				if (epId != null && epId > 0) {
					waigouOrder.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						waigouOrder.setApplystatus("同意");
						waigouOrder.setStatus("待通知");
						// Set<WaigouPlan> wgpList = waigouOrder.getWwpSet();
						// if (wgpList != null && wgpList.size() > 0) {
						// for (WaigouPlan wp : wgpList) {
						// wp.setStatus("待通知");
						// totalDao.update(wp);
						// }
						// }
					} else {
						waigouOrder.setApplystatus("未审批");
					}
				}
				CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, epId);
				if (circuitRun.getAllStatus().equals("同意")) {
					waigouOrder.setApplystatus("同意");
					waigouOrder.setEpId(epId);
					totalDao.update(waigouOrder);
					// 判断是否需要通知供应商
					String tz = (String) totalDao
							.getObjectByCondition("select valueCode from CodeTranslation where type = 'sys' and keyCode='待通知外委采购订单管理' and valueName='待通知外委订单'");
					if (tz != null && tz.equals("否")) {
						waigouOrder.setStatus("待确认");
						waigouOrder.setTongzhiTime(Util.getDateTime());
						// waigouOrder.setTzUserCode(loginUSer.getCode());//
						// 通知人员作为联系人
						// waigouOrder.setTzUserName(loginUSer.getName());//
						// 通知人员作为联系人
						// waigouOrder.setTzUserPhone(loginUSer.getPassword()
						// .getPhoneNumber());// 通知人员作为联系人
						// waigouOrder.setAddUserYx(loginUSer.getPassword()
						// .getMailBox());

						Set<WaigouPlan> planSet = waigouOrder.getWwpSet();
						for (WaigouPlan waigouPlan : planSet) {
							waigouPlan.setStatus("待确认");
						}
						if (waigouOrder.getLxPeople() == null) {
							// 供应商信息查询
							ZhUser zhuser = (ZhUser) totalDao.getObjectById(
									ZhUser.class, waigouOrder.getGysId());
							waigouOrder.setLxPeople(zhuser.getCperson());
							waigouOrder.setGysPhone(zhuser.getCmobile());
						}
						boolean bool = totalDao.update(waigouOrder);
						if (bool) {
							CompanyInfo companyInfo = (CompanyInfo) ActionContext
									.getContext().getApplication().get(
											"companyInfo");
							AlertMessagesServerImpl.addAlertMessages(
									waigouOrder.getType() + "采购订单确认(供应商)",
									"尊敬的【" + waigouOrder.getLxPeople()
											+ "】,您好:\n您有新的订单需要确认交付日期,订单号:【"
											+ waigouOrder.getPlanNumber()
											+ "】。详询"
											+ waigouOrder.getTzUserName() + "("
											+ waigouOrder.getTzUserPhone()
											+ ")。\n以下地址可以直接登录:\n用户名:"
											+ waigouOrder.getUserCode()
											+ "\n初始密码:" + 123456
											+ "(请及时修改密码)\n["
											+ companyInfo.getName() + "]", "1",
									waigouOrder.getUserCode());// 待取消
							// "helper");

							/****** 推送邮件 ******/
							// 查询供应商信息
							// ZhUser zhuser = (ZhUser) totalDao.getObjectById(
							// ZhUser.class, waigouOrder.getGysId());
							// if (zhuser != null && zhuser.getYx() != null
							// && zhuser.getYx().length() > 0) {
							// zhuser.setYx(zhuser.getYx().replaceAll("；",
							// ";"));
							// String[] yxs = zhuser.getYx().split(";");
							// Set<WaigouPlan> planSets =
							// waigouOrder.getWwpSet();
							// String caigouDetail = "";
							// for (WaigouPlan waigouPlan : planSets) {
							// if (waigouPlan.getSpecification() == null
							// || waigouPlan.getSpecification()
							// .length() == 0
							// || "null".equals(waigouPlan
							// .getSpecification())) {
							// waigouPlan.setSpecification("");
							// }
							// caigouDetail += "<tr>" + "<th>"
							// + waigouPlan.getMarkId()
							// + "</th>"
							// + "<th align='left' style='width: 150px;'>"
							// + waigouPlan.getProName() + "</th>"
							// + "<th>"
							// + waigouPlan.getSpecification()
							// + "</th>" + "<th>"
							// + waigouPlan.getBanben() + "</th>"
							// + "<th align='right'>" + "</th>"
							// + "<th>" + waigouPlan.getNumber()
							// + "</th>" + "<th>"
							// + waigouPlan.getUnit() + "</th>"
							// + "<th>" + waigouPlan.getHsPrice()
							// + "</th>" + "<th>"
							// + waigouPlan.getMoney() + "</th>"
							// + "<th>" + "</th>" + "<th>" + "</th>"
							// + "</tr>";
							// }
							// String maildetail = "<style>.table {"
							// + "font-size: 14px;" + "padding: 0px;"
							// + "margin: 0px;"
							// + "border-collapse: collapse;"
							// + "border: solid #999;"
							// + "border-width: 1px 0 0 1px;"
							// + "width: 99%;" + "}"
							// + ".table th,.table td {"
							// + "border: solid #999;"
							// + "border-width: 1 1px 1px 1;"
							// + "padding: 2px;" + "}</style>尊敬的【"
							// + waigouOrder.getLxPeople()
							// + "】先生/女士,您好:<br/>您有新的订单需要确认交付日期,订单号:【"
							// + waigouOrder.getPlanNumber()
							// + "】。<br/><div id='printdiv'>"
							// +
							// "<div align='center' style='font-size: 20px; height: 25px;'>"
							// + companyInfo.getName()
							// + "</div>"
							// +
							// "<div align='center' style='font-size: 14px; height: 25px;'>"
							// + companyInfo.getEnglishName()
							// + "</div>"
							// + "<div align='right' style='font-size: 12px;'>"
							// + "电话:"
							// + companyInfo.getTel()
							// +
							// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							// + "传真:"
							// + companyInfo.getFax()
							// +
							// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							// +
							// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第 1 页/共 1"
							// +
							// "页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							// + "</div>"
							// +
							// "<table class='table' style='line-height: 15px;'>"
							// + "<tbody><tr>"
							// +
							// "<th colspan='11' style='border: hidden; font-size: x-large;' align='center'>"
							// + "采购订单"
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden; width: 100px;' align='left' colspan='8'>"
							// + "REY:01"
							// + "</th>"
							// +
							// "<th style='border: hidden;' align='center' colspan='4'>"
							// + "QP140900-C"
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden; width: 100px;' align='left' colspan='8'>"
							// + "供应商编号:"
							// + zhuser.getUsercode()
							// + "</th>"
							// +
							// "<th style='border: hidden;' align='left' colspan='4'>"
							// + "订单编号:"
							// + waigouOrder.getPlanNumber()
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden;' align='left' '='' colspan='8'>"
							// + "供应商名称:"
							// + zhuser.getCmp()
							// + "</th>"
							// +
							// "<th style='border: hidden;' colspan='4' align='left'>"
							// + "制单日期:"
							// + waigouOrder.getTongzhiTime()
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden;' colspan='8' align='left'>"
							// +
							// "地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址:"
							// + zhuser.getCompanydz()
							// + "</th>"
							// +
							// "<th style='border: hidden;' colspan='4' align='left'>"
							// + "采&nbsp;&nbsp;购&nbsp;&nbsp;员:"
							// + waigouOrder.getTzUserName()
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden;' colspan='8' align='left'>"
							// + "联&nbsp;&nbsp;&nbsp;系&nbsp;&nbsp;&nbsp;人:"
							// + zhuser.getCperson()
							// + "</th>"
							// +
							// "<th style='border: hidden;' colspan='4' align='left'>"
							// + "付款方式:"
							// + zhuser.getFkfs()
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// +
							// "<th style='border: hidden; border-bottom: inherit;' colspan='8' align='left'>"
							// + "电话&nbsp;/&nbsp;手机:"
							// + zhuser.getCfax()
							// + "/"
							// + zhuser.getCtel()
							// + "</th>"
							// +
							// "<th style='border: hidden; border-bottom: inherit;' colspan='4' align='left'>"
							// + "票据类型:增值税发票"
							// + zhuser.getZzsl()
							// + "</th>"
							// + "</tr>"
							// + "<tr>"
							// + "<th>"
							// + "件号"
							// + "</th>"
							// + "<th>"
							// + "物料名称"
							// + "</th>"
							// + "<th>"
							// + "规格型号"
							// + "</th>"
							// + "<th>"
							// + "版本"
							// + "</th>"
							// + "<th>"
							// + "材质"
							// + "</th>"
							// + "<th>"
							// + "订单数量"
							// + "</th>"
							// + "<th>"
							// + "单位"
							// + "</th>"
							// + "<th>"
							// + "含税单价"
							// + "</th>"
							// + "<th>"
							// + "含税金额"
							// + "</th>"
							// + "<th style='width: 80px;'>"
							// + "交货日期"
							// + "</th>"
							// + "<th>"
							// + "备注"
							// + "</th>"
							// + "</tr>"
							// + caigouDetail
							// + "<tr>"
							// + "<td colspan='11'></td>"
							// + "</tr>"
							// + "<tr style=''>"
							// +
							// "<th align='right' style='border: hidden; border-top: inset;'>"
							// + "送货地址:"
							// + "</th>"
							// +
							// "<th colspan='12' align='left' style='border: hidden; border-top: inherit;'>"
							// + companyInfo.getAddress()
							// + "</th>"
							// + "</tr>"
							// + "<tr style='border: hidden;'>"
							// + "<th align='right' style='border: hidden;'>"
							// +
							// "备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:"
							// + "</th>"
							// +
							// "<th colspan='12' align='left' style='border: hidden;'></th>"
							// + "</tr>"
							// + "<tr style='border: hidden;'>"
							// +
							// "<th align='right' style='border: hidden; vertical-align: top;'>"
							// +
							// "条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;款:"
							// + "</th>"
							// +
							// "<th style='border: hidden; line-height: 25px' colspan='12' align='left'>"
							// + "<ul>"
							// + "<li>"
							// +
							// "1.供方必须遵循本订购单之交货期或需方采购部电话及书面通知调整之交期，若有延误，每延误一日扣除该批款3%。"
							// + "</li>"
							// + "<li>"
							// +
							// "2.按工程图纸要求、品质保证期限为一年，供方交货之料件必须符合需方之品质要求，否则需方在一年内有权退货或要求赔偿供方不得拒绝。"
							// + "</li>"
							// + "<li>"
							// +
							// "3.检验后如发现品质不良供方在接到通知后3日内应将退货取回，并尽快补料，延期需方自行处理，若急用料需挑选所需人工费由供方负责。"
							// + "</li>"
							// + "</ul>"
							// + "</th>"
							// + "</tr>"
							// + "</tbody></table>"
							// +
							// "</div><br/><font color='red'>以下地址可以登录系统填写交付日期:<a href='http://pebs.i-brights.cn/'>PEBS(请点击)</a><br/>用户名:"
							// + waigouOrder.getUserCode()
							// + "<br/>初始密码:123456(请及时修改密码)<br/>["
							// + companyInfo.getName()
							// + "]<br/>"
							// + "注意:本邮件由系统发送,请勿直接回复。<br/>具体事项请联系"
							// + waigouOrder.getTzUserName()
							// + ",手机号:"
							// + waigouOrder.getTzUserPhone()
							// + "/邮箱:"
							// + waigouOrder.getAddUserYx() + "</font>";
							// for (String yx : yxs) {
							// // 发送邮件提醒
							// SendMail sendMail = new SendMail();
							//
							// sendMail.sendMail(yx, "阳天电子科技-采购订单确认(供应商)",
							// maildetail);
							// }
							// }
							// 短信通知
						}
						String qr = (String) totalDao
								.getObjectByCondition("select valueCode from CodeTranslation where type = 'sys' and keyCode='待确认外委采购订单管理' and valueName='待确认外委订单'");
						if (qr != null && qr.equals("否")) {
							// CompanyInfo companyInfo = (CompanyInfo)
							// ActionContext
							// .getContext().getApplication().get(
							// "companyInfo");
							// AlertMessagesServerImpl.addAlertMessages(
							// waigouOrder.getType() + "采购订单确认(供应商)",
							// "尊敬的【" + waigouOrder.getLxPeople()
							// + "】,您好:\n您有新的订单已经确认签订,订单号:【"
							// + waigouOrder.getPlanNumber()
							// + "】。详询"
							// + waigouOrder.getTzUserName() + "("
							// + waigouOrder.getTzUserPhone()
							// + ")。\n以下地址可以直接登录:\n用户名:"
							// + waigouOrder.getUserCode()
							// + "\n初始密码:" + 123456
							// + "(请及时修改密码)\n["
							// + companyInfo.getName() + "]", "1",
							// waigouOrder.getUserCode());// 待取消
							// "helper");
							waigouOrder.setApplystatus("同意");
							waigouOrder.setStatus("生产中");
							if (planSet != null && planSet.size() > 0) {
								for (WaigouPlan waigouPlan : planSet) {
									if (waigouPlan.getWwSource()
											.equals("BOM外委")) {
										waigouPlan.setStatus("供应商领料");
										totalDao.update(waigouPlan);
									}
								}
							}
							totalDao.update(waigouOrder);

						}
					}
				} else {
					waigouOrder.setEpId(epId);
					totalDao.update(waigouOrder);
				}

			} catch (Exception e) {
				// return "审批流程有误,请联系管理员!";
			}

		}
	}

	@Override
	public List<Goods> findBcpllList(String tag, Goods goods) {
		// TODO Auto-generated method stub
		String hql = "from Procard where  zaizhizkCount>0 and zaizhizkCount is not null "
				+ " and id in(select procardId from ProcardProductRelation)";
		String str = "";
		if (goods != null) {
			if (goods.getGoodsMarkId() != null
					&& goods.getGoodsMarkId().length() > 0) {
				hql += " and markId like '%" + goods.getGoodsMarkId() + "%'";
			}
			if (goods.getWxselfCard() != null
					&& goods.getWxselfCard().length() > 0) {
				hql += " and selfCard like '%" + goods.getWxselfCard() + "%'";
			}
			if (goods.getYwmarkId() != null && goods.getYwmarkId().length() > 0) {
				hql += " and ywmarkId like '%" + goods.getYwmarkId() + "%'";
			}
			if (goods.getNeiorderId() != null
					&& goods.getNeiorderId().length() > 0) {
				hql += " and orderNumber like '%" + goods.getNeiorderId()
						+ "%'";
			}
			if (goods.getGoodHouseName() != null
					&& goods.getGoodHouseName().length() > 0) {
				str = " and goodHouseName like '%" + goods.getGoodHouseName()
						+ "%'";
			}
		}
		// String
		// selfcard="and id in (select procard.id from  ProcessinforPeople where userId =(select id from Users where cardId=?))";
		String selfcard = " and id in "
				+ "(select pi.procard.id from ProcessInfor pi where "
				+ " pi.processName in (select p.processName from ProcessGzstore p join p.users u where u.cardId=?))";
		List<Procard> procardList = totalDao.query(hql + selfcard
				+ " order by markId", tag);
		// List<Procard> procardList = totalDao.query(hql+" order by markId");

		List<Goods> returnList = new ArrayList<Goods>();
		if (procardList != null && procardList.size() > 0) {
			for (Procard procard : procardList) {
				String hql_goods = "from Goods where goodsClass in('外协库','半成品库') and  goodsCurQuantity>0 and goodsId in (select goodsId from ProcardProductRelation where procardId=?)";
				List<Goods> GoodsList = totalDao.query(hql_goods + str, procard
						.getId());
				if (GoodsList != null && GoodsList.size() > 0) {
					for (Goods goods1 : GoodsList) {
						Goods newg = new Goods();
						BeanUtils.copyProperties(goods1, newg, new String[] {});
						// 设置数量
						Float outCount = 0f;
						List<ProcardProductRelation> pprList = totalDao
								.query(
										"from ProcardProductRelation where goodsId=? and procardId=?",
										goods1.getGoodsId(), procard.getId());
						if (pprList != null && pprList.size() > 0) {
							for (ProcardProductRelation ppr : pprList) {
								if (ppr == null
										|| (ppr.getZyCount() - ppr.getCkCount() <= 0)) {
									continue;
								} else {
									outCount = outCount
											+ (ppr.getZyCount() - ppr
													.getCkCount());
								}
							}
						}
						if (outCount == 0) {
							continue;
						}
						if (outCount < newg.getGoodsCurQuantity()) {
							newg.setGoodsCurQuantity(outCount);
						}
						newg.setGoodsProMarkId(procard.getRootMarkId());
						newg.setGoodsArtsCard(procard.getRootSelfCard());
						newg.setWxselfCard(procard.getSelfCard());
						newg.setGoodsProMarkId(procard.getRootMarkId());
						newg.setYwmarkId(procard.getYwMarkId());
						newg.setNeiorderId(procard.getOrderNumber());
						newg.setProcardId(procard.getId());
						Integer pno = newg.getProcessNo();
						String nextProcessName = (String) totalDao
								.getObjectByCondition(
										"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') "
												+ " and procard.id =? and processNO >? order by processNO",
										procard.getId(), pno);
						if (nextProcessName == null) {
							if (procard.getProcard() != null) {
								nextProcessName = (String) totalDao
										.getObjectByCondition(
												"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') and procard.id=? order by processNO",
												procard.getProcard().getId());
							} else {
								nextProcessName = "无";
							}
						}
						newg.setNextProcessname(nextProcessName);
						returnList.add(newg);
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public List<Goods> findBcpllList(Goods goods) {
		// TODO Auto-generated method stub
		String hql = "from Goods where goodsClass in('外协库') "
				+ " and  goodsCurQuantity>0 ";
		if (goods != null) {
			boolean b = false;
			String str = "and goodsId in (select goodsId from ProcardProductRelation where procardId in(select id from Proard where 1=1 ";
			if (goods.getGoodsMarkId() != null
					&& goods.getGoodsMarkId().length() > 0) {
				b = true;
				str += " and markId like '%" + goods.getGoodsMarkId() + "%'";
			}
			if (goods.getWxselfCard() != null
					&& goods.getWxselfCard().length() > 0) {
				b = true;
				str += " and selfCard like '%" + goods.getWxselfCard() + "%'";
			}
			if (goods.getYwmarkId() != null && goods.getYwmarkId().length() > 0) {
				b = true;
				str += " and ywmarkId like '%" + goods.getYwmarkId() + "%'";
			}
			if (goods.getNeiorderId() != null
					&& goods.getNeiorderId().length() > 0) {
				b = true;
				str += " and orderNumber like '%" + goods.getNeiorderId()
						+ "%'";
			}
			if (goods.getGoodHouseName() != null
					&& goods.getGoodHouseName().length() > 0) {
				hql += " and goodHouseName like '%" + goods.getGoodHouseName()
						+ "%'";
			}
			str += ")";
			if (b) {
				hql += str;
			}
		}
		List<Goods> returnList = new ArrayList<Goods>();
		List<Goods> goodsList = totalDao.query(hql);
		if (goodsList != null && goodsList.size() > 0) {
			for (Goods goods2 : goodsList) {
				List<Procard> proList = totalDao
						.query(
								"from Procard where id in (select procardId from ProcardProductRelation where goodsId=?)",
								goods2.getGoodsId());
				if (proList != null && proList.size() > 0) {
					for (Procard p : proList) {
						ProcessInfor processInfor = (ProcessInfor) totalDao
								.getObjectByCondition(
										"from ProcessInfor where procard.id=? and processNO>? and (dataStatus is null or dataStatus!='删除' ) order by processNO",
										goods2.getProcessNo());
						if (processInfor.getProductStyle().equals("外委")) {
							Goods newg = new Goods();
							BeanUtils.copyProperties(goods2, newg,
									new String[] {});
							// 设置数量
							Float outCount = 0f;
							List<ProcardProductRelation> pprList = totalDao
									.query(
											"from ProcardProductRelation where goodsId=? and procardId=?",
											goods2.getGoodsId(), p.getId());
							if (pprList != null && pprList.size() > 0) {
								for (ProcardProductRelation ppr : pprList) {
									if (ppr == null
											|| (ppr.getZyCount()
													- ppr.getCkCount() <= 0)) {
										continue;
									} else {
										outCount = outCount
												+ (ppr.getZyCount() - ppr
														.getCkCount());
									}
								}
							}
							if (outCount == 0) {
								continue;
							}
							if (outCount < newg.getGoodsCurQuantity()) {
								newg.setGoodsCurQuantity(outCount);
							}
							newg.setGoodsProMarkId(p.getRootMarkId());
							newg.setGoodsArtsCard(p.getRootSelfCard());
							newg.setWxselfCard(p.getSelfCard());
							newg.setGoodsProMarkId(p.getRootMarkId());
							newg.setYwmarkId(p.getYwMarkId());
							newg.setNeiorderId(p.getOrderNumber());
							newg.setProcardId(p.getId());
							returnList.add(newg);
						}
					}
				}
			}
		}
		return returnList;
	}

	/****
	 * 查询所有有库存的成品
	 * 
	 * @return
	 */
	@Override
	public Object[] findGoods(Goods goods, Integer cpage, Integer pageSize) {
		if (goods == null) {
			goods = new Goods();
		}
		String hql_goods = totalDao.criteriaQueries(goods, null, "ckCount",
				"ylshifa");
		hql_goods += " and goodsClass='成品库' and  goodsCurQuantity>0 ";
		Integer count = totalDao.getCount(hql_goods);
		List<Goods> list = totalDao.findAllByPage(hql_goods, cpage, pageSize);
		if (list != null && list.size() > 0) {
			for (Goods g : list) {
				if (g.getNeiorderId() == null) {
					String orderNumber = (String) totalDao
							.getObjectByCondition(
									"select o.orderNum from OrderManager o join o.innerOrders i where i.id in(select planOrderId from Procard where markId=? and selfCard=?) ",
									g.getGoodsMarkId(), g.getGoodsLotId());
					if (orderNumber == null) {
						orderNumber = (String) totalDao
								.getObjectByCondition(
										"select orderNO from ProjectOrder where id in(select planOrderId from Procard where markId=? and selfCard=?)",
										g.getGoodsMarkId(), g.getGoodsLotId());
					}
					g.setNeiorderId(orderNumber);
				}
			}
		}
		return new Object[] { list, count };
	}

	/****
	 * 添加包装申请
	 * 
	 * @param goods_bzsq
	 * @return
	 */
	@Override
	public boolean saveGoodsBzSq(Goods_bzsq goods_bzsq) {
		if (goods_bzsq != null) {
			Users loginUser = Util.getLoginUser();
			String time = Util.getDateTime();
			goods_bzsq.setAddTime(time);
			goods_bzsq.setAddUserCode(loginUser.getCode());
			goods_bzsq.setAddUserName(loginUser.getName());
			goods_bzsq.setStatus("待领");
			if (goods_bzsq.getGoods_bzwList() != null) {
				Set<Goods_bzw> goods_bzwSet = new HashSet<Goods_bzw>();
				for (Goods_bzw goodsBzw : goods_bzsq.getGoods_bzwList()) {
					goodsBzw.setAddTime(time);
				}
				goods_bzwSet.addAll(goods_bzsq.getGoods_bzwList());
				goods_bzsq.setGoods_bzwSet(goods_bzwSet);
			}
			Goods goods = (Goods) totalDao.getObjectById(Goods.class,
					goods_bzsq.getGoodsId());
			if (goods == null) {
				return false;
			} else {
				if (goods.getBzApplyCount() == null) {// goods.getBzApplyCount()前几次领取的数量
					goods.setBzApplyCount(0f);// 首次领取是赋值上次领取数量为0
				}
				if ((goods_bzsq.getCount() + goods.getBzApplyCount()) > goods
						.getGoodsCurQuantity()) {// 本次领取数量+上次领取数量>库存数量
					return false;
				} else {
					goods.setBzApplyCount(goods.getBzApplyCount()
							+ goods_bzsq.getCount());// 重新赋值上次数量=本次数量+上次数量
				}
				totalDao.update(goods);
			}
			return totalDao.save(goods_bzsq);
		}
		return false;
	}

	/****
	 * 查询所有包装申请
	 * 
	 * @return
	 */
	@Override
	public Object[] findGoodsbzsq(Goods_bzsq goods_bzsq, Integer cpage,
			Integer pageSize) {
		if (goods_bzsq == null) {
			goods_bzsq = new Goods_bzsq();
		}
		String hql_goods = totalDao.criteriaQueries(goods_bzsq, null);
		Integer count = totalDao.getCount(hql_goods);
		List list = totalDao.findAllByPage(hql_goods + " order by id desc",
				cpage, pageSize);
		return new Object[] { list, count };
	}

	/****
	 * 通过id查询包装申请
	 * 
	 * @return
	 */
	@Override
	public Goods_bzsq findGoodsbzsqById(Integer id) {
		return (Goods_bzsq) totalDao.getObjectById(Goods_bzsq.class, id);
	}

	/****
	 * 领取包装申请
	 * 
	 * @param goods_bzsq
	 * @return
	 */
	@Override
	public String updateGoodsBzSqFroLq(Integer id, List<String> cards) {
		Goods_bzsq goods_bzsq = (Goods_bzsq) totalDao.getObjectById(
				Goods_bzsq.class, id);
		if ("待领".equals(goods_bzsq.getStatus())) {
			if (cards == null || cards.size() == 0 || cards.get(0) == null
					|| cards.get(0).toString().length() == 0) {
				return "请刷卡!";
			}
		}
		if (goods_bzsq != null) {
			Goods goods = (Goods) totalDao.getObjectById(Goods.class,
					goods_bzsq.getGoodsId());
			if ("待领".equals(goods_bzsq.getStatus())) {
				StringBuffer sbcards = new StringBuffer();
				StringBuffer sbnames = new StringBuffer();
				for (int i = 0; i < cards.size(); i++) {
					String card = cards.get(i);
					Users user = (Users) totalDao.getObjectByCondition(
							"from Users where cardId=?", card);
					if (user != null) {
						if (!goods_bzsq.getZpbzUserCode().contains(
								user.getCode())) {
							return "卡号:" + card + "的人员不在指派人员中";
						}
						if (i == 0) {
							sbcards.append(user.getCode());
							sbnames.append(user.getName());
						} else {
							sbcards.append(";" + user.getCode());
							sbnames.append(";" + user.getName());
						}
					}
				}
				if (sbcards.length() == 0) {
					return "没有找到对应的领取人员!";
				}
				goods_bzsq.setLqbzUserCode(sbcards.toString());
				goods_bzsq.setLqbzUserName(sbnames.toString());
				goods_bzsq.setSjStartTime(Util.getDateTime());
				goods_bzsq.setStatus("已领");
				if (goods != null) {
					if (goods.getBzyiCount() == null) {
						goods.setBzyiCount(goods_bzsq.getCount());
					} else {
						goods.setBzyiCount(goods.getBzyiCount()
								+ goods_bzsq.getCount());
					}
					totalDao.update(goods);
				}
			} else if ("已领".equals(goods_bzsq.getStatus())) {
				goods_bzsq.setSjEndTime(Util.getDateTime());
				goods_bzsq.setStatus("完成");
				if (goods != null) {
					if (goods.getBztjCount() == null) {
						goods.setBztjCount(goods_bzsq.getCount());
					} else {
						goods.setBztjCount(goods.getBztjCount()
								+ goods_bzsq.getCount());
					}
					totalDao.update(goods);
				}
			}
			return totalDao.update(goods_bzsq) + "";
		}
		return "没有找到对应的包装申请!";
	}

	@Override
	public Object getUsersByIds(String tag) {
		// TODO Auto-generated method stub
		String hql = "from Users where dept in( select dept from DeptNumber where id in("
				+ tag + ")) and onWork in ('实习','试用','在职')";
		List users = totalDao.query(hql);
		return users;
	}

	/****
	 * 送货申请
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public Object[] orderToSonghuo(Integer[] ids) {
		List list = new ArrayList();
		Users loginUser = Util.getLoginUser();
		WaigouDeliveryGoods wdv = new WaigouDeliveryGoods();
		String errmes = "";
		if (loginUser != null) {
			String contacts = "";
			String contactsPhone = "";
			ClientManagement clientManagement = null;
			if (ids != null && ids.length > 0) {
				for (Integer id : ids) {
					Goods goods = (Goods) totalDao.getObjectById(Goods.class,
							id);
					// 未送货的数量大于0，可以继续申请送货
					if (goods != null && goods.getGoodsCurQuantity() > 0) {
						ClientManagement newclientManagement = null;
						if (goods.getNeiorderId() != null
								&& !"".equals(goods.getNeiorderId())) {
							newclientManagement = (ClientManagement) totalDao
									.getObjectByCondition(
											"from ClientManagement where id = (select custome.id from OrderManager where orderNum = ?)",
											goods.getNeiorderId());
						} else {
							newclientManagement = (ClientManagement) totalDao
									.getObjectByCondition("from ClientManagement where clientcompanyname like '%"
											+ goods.getGoodsCustomer() + "%'");
						}
						if (newclientManagement == null) {
							errmes += goods.getGoodsMarkId() + "未找到客户信息。";
							continue;
						} else if (clientManagement != null
								&& !clientManagement.getId().equals(
										newclientManagement.getId())) {
							errmes += "本次选择件号的客户不一致，无法申请送货!";
							continue;
						} else {
							clientManagement = newclientManagement;
						}
						WaigouDeliveryGoodsDetail wdd = new WaigouDeliveryGoodsDetail();
						wdd.setMarkId(goods.getGoodsMarkId());
						wdd.setSelfCard(goods.getGoodsLotId());
						wdd.setYwmarkId(goods.getYwmarkId());
						wdd.setBanben(goods.getBanBenNumber());
						wdd.setTuhao(goods.getTuhao());
						wdd.setProName(goods.getGoodsFullName());
						wdd.setSpecification(goods.getGoodsFormat());
						wdd.setUnit(goods.getGoodsUnit());
						wdd.setHsPrice(goods.getGoodsPrice());
						wdd.setKhId(newclientManagement.getId());
						wdd.setKhName(newclientManagement
								.getClientcompanyname());

						if (goods.getWaiorderId() == null) {
							String orderNumber = (String) totalDao
									.getObjectByCondition(
											"select o.outOrderNumber from OrderManager o join o.innerOrders i where i.id in(select planOrderId from Procard where markId=? and selfCard=?) ",
											goods.getGoodsMarkId(), goods
													.getGoodsLotId());
							if (orderNumber == null) {
								orderNumber = (String) totalDao
										.getObjectByCondition(
												"select orderNO from ProjectOrder where id in(select planOrderId from Procard where markId=? and selfCard=?)",
												goods.getGoodsMarkId(), goods
														.getGoodsLotId());
							}
							wdd.setCgOrderNum(orderNumber);
						}
						wdd.setShNumber(goods.getGoodsCurQuantity());

						// 根据历史数据计算箱数
						String hql_ctn = "select max(oneCtnNum) from WaigouDeliveryGoodsDetail where markId=?";
						Float maxoneCtnNum = (Float) totalDao
								.getObjectByCondition(hql_ctn, wdd.getMarkId());
						if (maxoneCtnNum == null || maxoneCtnNum <= 0) {
							wdd.setCtn(1F);
						} else {
							wdd.setCtn(Float.parseFloat(Math.ceil(wdd
									.getShNumber()
									/ maxoneCtnNum)
									+ ""));
						}
						wdd.setOneCtnNum(maxoneCtnNum);
						wdd.setGoodsId(goods.getGoodsId());
						// 根据历史数据计算箱数结束
						list.add(wdd);
					}
				}

				if (clientManagement == null || errmes.length() > 0) {
					return new Object[] { wdv, list, errmes };
				}
				// 送货人历史数据查询***** 开始
				String hql_shContact = "select shContacts,shContactsPhone from WaigouDeliveryGoods where shContacts is not null "
						+ "and shContacts<>'' order by addTime desc";
				Object[] obj_shc = (Object[]) totalDao
						.getObjectByCondition(hql_shContact);
				if (obj_shc != null) {
					wdv.setShContacts(obj_shc[0].toString());
					wdv.setShContactsPhone(obj_shc[1].toString());
				}
				// 签收人历史数据查询***** 开始
				String hql_qsContact = "select qsContacts,qsContactsPhone from WaigouDeliveryGoods where shContacts is not null "
						+ "and shContacts<>'' order by addTime desc";
				Object[] obj_qsc = (Object[]) totalDao
						.getObjectByCondition(hql_qsContact);
				if (obj_qsc != null) {
					wdv.setQsContacts(obj_qsc[0].toString());
					wdv.setQsContactsPhone(obj_qsc[1].toString());
				}
				// 车牌
				String hql_chepai = "select chepai from WaigouDeliveryGoods where  chepai is not null "
						+ "and chepai<>'' order by addTime desc";
				String chepai = (String) totalDao
						.getObjectByCondition(hql_chepai);
				wdv.setChepai(chepai);

				// 送货人历史数据查询***** 结束
				wdv.setShsqDate(Util.getDateTime("yyyy-MM-dd"));
				wdv.setUserCode(loginUser.getCode());
				wdv.setUserId(loginUser.getId());

				wdv.setKhId(clientManagement.getId());
				wdv.setCustomerCode(clientManagement.getNumber());
				wdv.setCustomer(clientManagement.getClientcompanyname());
				wdv.setDaodaDizhi(clientManagement.getClientdz());
				wdv.setContacts(clientManagement.getClientname());
				wdv.setContactsPhone(clientManagement.getClientmobilenumber());

				if (ActionContext.getContext() != null) {
					CompanyInfo companyInfo = (CompanyInfo) ActionContext
							.getContext().getApplication().get("companyInfo");
					wdv.setChufaDizhi(companyInfo.getAddress());
					wdv.setGysName(companyInfo.getName());
					wdv.setGysPhone(loginUser.getPassword().getPhoneNumber());
					wdv.setGysContacts(loginUser.getName());
				}
				wdv.setContacts(contacts);
				wdv.setContactsPhone(contactsPhone);

				// 得到最大的采购计划编号
				String maxNumber = clientManagement.getNumber()
						+ Util.getDateTime("yyyyMMdd");
				String hql_maxnumber = "select max(planNumber) from WaigouDeliveryGoods where planNumber like '%"
						+ maxNumber + "%'";
				Object obj = totalDao.getObjectByCondition(hql_maxnumber);
				if (obj != null) {
					int num2 = 0;
					String maxNumber2 = obj.toString();
					num2 = Integer.parseInt(maxNumber2.substring(maxNumber2
							.length() - 3, maxNumber2.length()));
					num2++;
					if (num2 < 10) {
						maxNumber += "00" + num2;
					} else if (num2 < 100) {
						maxNumber += "0" + num2;
					} else {
						maxNumber += num2 + "";
					}
				} else {
					maxNumber += "001";
				}
				wdv.setPlanNumber(maxNumber);// 采购计划编号
			}
			return new Object[] { wdv, list, errmes };
		}
		return null;
	}

	/****
	 * 添加送货单
	 * 
	 * @param waigouDelivery
	 * @param list
	 * @return
	 */
	@Override
	public String addDeliveryNote(WaigouDeliveryGoods waigouDeliveryGoods,
			List list) {
		if (waigouDeliveryGoods != null) {
			// 得到最大的采购计划编号
			String maxNumber = waigouDeliveryGoods.getCustomerCode()
					+ Util.getDateTime("yyyyMMdd");
			String hql_maxnumber = "select max(planNumber) from WaigouDeliveryGoods where planNumber like '%"
					+ maxNumber + "%'";
			Object obj = totalDao.getObjectByCondition(hql_maxnumber);
			if (obj != null) {
				int num2 = 0;
				String maxNumber2 = obj.toString();
				num2 = Integer.parseInt(maxNumber2.substring(maxNumber2
						.length() - 4, maxNumber2.length()));
				num2++;
				if (num2 < 10) {
					maxNumber += "00" + num2;
				} else if (num2 < 100) {
					maxNumber += "0" + num2;
				} else {
					maxNumber += num2 + "";
				}
			} else {
				maxNumber += "001";
			}
			Set<WaigouDeliveryGoodsDetail> wddSet = new HashSet<WaigouDeliveryGoodsDetail>();
			List<String> codeList = new ArrayList<String>();// 装采购员工号
			for (int i = 0; i < list.size(); i++) {
				WaigouDeliveryGoodsDetail wdd = (WaigouDeliveryGoodsDetail) list
						.get(i);
				Goods goods = (Goods) totalDao.getObjectById(Goods.class, wdd
						.getGoodsId());
				if (goods != null) {
					// if (goods.getWaigouOrder().getAddUserCode() != null
					// && !codeList.contains(goods.getWaigouOrder()
					// .getAddUserCode())) {
					// codeList.add(goods.getWaigouOrder()
					// .getAddUserCode());
					// }

					BeanUtils.copyProperties(goods, wdd, new String[] { "id",
							"querenTime", "hgNumber" });

					// 计算剩余可送货数量
					Float syNumber = goods.getGoodsCurQuantity()
							- wdd.getShNumber();
					if (syNumber < 0) {
						return goods.getGoodsMarkId() + "的本次送货数量不能超过"
								+ goods.getGoodsCurQuantity();
					}
					if (goods.getBzApplyCount() != null
							&& goods.getBzApplyCount() > wdd.getShNumber()) {
						goods.setBzApplyCount(goods.getBzApplyCount()
								- wdd.getShNumber());
					} else {
						goods.setBzApplyCount(0f);
					}

					// 出货数量
					Float outNumber = wdd.getShNumber();
					// 添加出库记录并关联订单
					Sell sell = new Sell();
					Users user = (Users) Util.getLoginUser();
					String nowTime = Util.getDateTime();
					sell.setSellAdminName(user.getName());
					sell.setSellAdminId(user.getId());
					sell.setSellTime(nowTime);
					sell.setSellCount(outNumber);
					sell.setSellSendnum(maxNumber);
					sell.setSellMarkId(goods.getGoodsMarkId());
					sell.setSellLot(goods.getGoodsLotId());
					sell.setSellWarehouse(goods.getGoodsClass());
					sell.setGoodHouseName(goods.getGoodHouseName());
					sell.setKuwei(goods.getGoodsPosition());
					sell.setSellDate(Util.getDateTime("yyyy-MM-dd"));
					String orderNumber = (String) totalDao
							.getObjectByCondition(
									"select o.orderNum from OrderManager o join o.innerOrders i where i.id in(select planOrderId from Procard where markId=? and selfCard=?) ",
									goods.getGoodsMarkId(), goods
											.getGoodsLotId());
					sell.setOrderNum(orderNumber);
					sell.setOutOrderNumer(wdd.getCgOrderNum());
					sell.setSellUnit(goods.getGoodsUnit());
					// 成品出库
					TaHkHuikuan hk = null;
					List<TaHkHuikuan> hkList = totalDao
							.query(
									" from TaHkHuikuan where hkStatus='未核对' and id in(select taHkHuikuan.id from TaHkSellSta where hkSellSendId=?)",
									sell.getSellSendnum());
					Set<TaHkSellSta> taHkSellStas = null;
					if (hkList.size() > 0) {
						hk = hkList.get(0);
						taHkSellStas = hk.getTaHkSellStas();
					} else {
						hk = new TaHkHuikuan();
						taHkSellStas = new HashSet();
						String curMon = totalDao.getDateTime("yyyyMM");
						String hql = "select max(hkNum) from TaHkHuikuan where hkNum like '%"
								+ curMon + "%'";
						String huNum = "";
						if (null != totalDao.query(hql)
								&& null != totalDao.query(hql).get(0)
								&& totalDao.query(hql).size() > 0) {
							String maxNum = (String) totalDao.query(hql).get(0);
							int newNum = Integer.parseInt(maxNum.substring(4)) + 1;
							huNum = "shLD" + String.valueOf(newNum);
						} else {
							huNum = "shLD" + curMon + "001";
						}
						hk.setHkNum(huNum);
						sell.setSellHkId(hk.getHkNum());
					}
					// 为属性赋值
					hk.setHkApplyDate(totalDao
							.getDateTime("yyyy-MM-dd HH:mm:ss"));
					hk.setHkApplier(user.getName());
					// 开票钱审核流程
					hk.setHkPreNotPath("LD");
					hk.setHkRelNotPath("FD");
					// 开票后审核流程
					hk.setHkInvoPrePath("FD");
					hk.setHkInvoRelPath("MD");
					// 汇款追踪审核流程
					hk.setHkBackAuditA("MD");
					hk.setHkBackAuditB("FD");
					hk.setHkStatus("未核对");
					// hk.setHkNoticeFile(attachmentName);
					hk.setHkTrackRate(0f);
					hk.setHkClientName(sell.getSellCompanyPeople());// 客户负责人
					hk.setHkZhuikuanren(sell.getSellCompanyPeople());// 设置追款人=客户负责人
					// 添加关联的明细表
					totalDao.save(hk);
					boolean hasSta = false;
					if (taHkSellStas != null && taHkSellStas.size() > 0) {
						for (TaHkSellSta hkSell : taHkSellStas) {
							if (hkSell.getHkSellMarkId().equals(
									sell.getSellMarkId())
									&& hkSell.getHkSellSendId().equals(
											sell.getSellSendnum())
									&& hkSell.getHkSellOrderId() != null
									&& hkSell.getHkSellOrderId().equals(
											sell.getOrderNum())
									&& hkSell.getHkSellOutOrderId().equals(
											sell.getOutOrderNumer())) {
								hasSta = true;
								if (hkSell.getApplyCount() == null) {
									hkSell.setApplyCount(sell.getSellCount());
								} else {
									hkSell.setApplyCount(hkSell.getApplyCount()
											+ sell.getSellCount());
								}
								if (hkSell.getHkSelfCard() == null) {
									hkSell.setHkSelfCard(sell.getSellLot());
								} else {
									hkSell.setHkSelfCard(hkSell.getHkSelfCard()
											+ "," + sell.getSellLot());
								}
								break;
							}
						}
					}
					if (!hasSta) {
						TaHkSellSta hkSell = new TaHkSellSta();
						hkSell.setTaHkHuikuan(hk);
						hkSell.setHkSellNum(hk.getHkNum());
						hkSell.setHkSellSendId(sell.getSellSendnum());// 送货单号
						hkSell.setHkSellMarkId(sell.getSellMarkId());// 件号
						hkSell.setHkSelfCard(sell.getSellLot());// 批次
						hkSell.setHkSellGoods(sell.getSellGoods());// 品名
						hkSell.setHkSellCumpanyName(sell.getSellCompanyName());// 客户
						hk.setHkClientComp(sell.getSellCompanyName());
						hkSell.setHkSellOrderId(sell.getOrderNum());// 内部订单号
						hkSell.setHkSellOutOrderId(sell.getOutOrderNumer());// 外部订单号
						hkSell.setHksellTime(nowTime);// 添加送货单时间
						hkSell.setHksellUsername(user.getName());// 添加人员姓名
						hkSell.setApplyCount(sell.getSellCount());// 申请开票数量
						// hkSell.setHkSellCount(sell.getSellCount());//数量
						taHkSellStas.add(hkSell);
						hk.setTaHkSellStas(taHkSellStas);
					}
					totalDao.update(hk);
					if (orderNumber != null) {
						String addSql2 = null;
						String ywMarkId = sell.getYwmarkId();
						if (ywMarkId == null || "".equals(ywMarkId)) {
							ywMarkId = (String) totalDao
									.getObjectByCondition(
											"select ywMarkId from ProcardTemplate where procardStyle='总成' and markId=?",
											sell.getSellMarkId());
						}
						String addSql = null;
						if (ywMarkId != null && ywMarkId.length() > 0) {
							addSql = "and (pieceNumber=? or pieceNumber='"
									+ ywMarkId + "')";
							addSql2 = "and (partNumber=? or partNumber='"
									+ ywMarkId + "')";
						} else {
							addSql = "and pieceNumber=?";
							addSql2 = "and partNumber=?";
						}
						// 内部计划的出库数量
						List<InternalOrderDetail> iodList = totalDao
								.query(
										"from InternalOrderDetail where 1=1 "
												+ addSql
												+ " and internalOrder in (select planOrderId from Procard where markId=? and selfCard=? and productStyle='批产')",
										sell.getSellMarkId(), sell
												.getSellMarkId(), sell
												.getSellLot());
						if (iodList != null && iodList.size() > 0) {
							float sellCount = sell.getSellCount();
							for (InternalOrderDetail iod2 : iodList) {
								if (iod2.getSellCount() == null) {
									iod2.setSellCount(0f);
								}
								if ((iod2.getNum().floatValue() - iod2
										.getSellCount().floatValue()) >= sellCount) {
									iod2
											.setSellCount((iod2.getSellCount() + sellCount));
									sellCount = 0;
									break;
								} else {
									sellCount = sellCount
											- (iod2.getNum() - iod2
													.getSellCount());
									iod2.setSellCount(iod2.getNum());
								}
								totalDao.update(iod2);
							}
						}
						// 有内部订单号,根据内部订单号和件号去填补
						ProductManager product = (ProductManager) totalDao
								.getObjectByQuery(
										"from ProductManager where orderManager.orderNum=? "
												+ addSql, sell.getOrderNum(),
										sell.getSellMarkId());
						if (product.getApplyNumber() == null) {
							product.setApplyNumber((float) sell.getSellCount());
						} else {
							product
									.setApplyNumber((product.getApplyNumber() + sell
											.getSellCount()));
						}
						if (product.getSellCount() == null) {
							product.setSellCount(0f);
						}
						product.setSellCount((product.getSellCount() + sell
								.getSellCount()));// 订单完成数量
						if (product.getAllocationsNum().floatValue() == product
								.getNum().floatValue()) {// 订单产品完成
							String hql = "from ProductManager where orderManager.id=? and allocationsNum<num";
							int count = totalDao.getCount(hql, product
									.getOrderManager().getId());
							if (count == 0) {// 订单对应的产品都已交付
								OrderManager orderManager = (OrderManager) totalDao
										.getObjectById(OrderManager.class,
												product.getOrderManager()
														.getId());
								orderManager.setDeliveryStatus("是");
								totalDao.update(orderManager);
							}

						}
						String paymentDate = product.getOrderManager()
								.getPaymentDate();
						if (paymentDate != null) {// 订单及时率
							if (Util.compareTime(paymentDate, "yyyy-MM-dd",
									sell.getSellDate(), "yyyy-MM-dd")) {
								// 完成时间在前
								if (product.getTimeNum() == null) {
									product.setTimeNum(0f);
								}
								product.setTimeNum((product.getTimeNum() + sell
										.getSellCount()));
							}
						}
						// 计算订单交付率
						Integer orderId = product.getOrderManager().getId();
						String hqljfr = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
						String hqljfr2 = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
						Float pronum = (Float) totalDao.query(hqljfr, orderId)
								.get(0);
						Float proallocationsNum = (Float) totalDao.query(
								hqljfr2, orderId).get(0);
						OrderManager orderManager = (OrderManager) totalDao
								.getObjectById(OrderManager.class, orderId);
						orderManager.setCompletionrate(proallocationsNum
								/ pronum * 100);
						totalDao.update(orderManager);
						totalDao.update(product);

						// 计算项目交付数量计算项目盈亏
						QuotedPrice qp = (QuotedPrice) totalDao
								.getObjectByCondition(
										"from QuotedPrice where markId=? and procardStyle='总成'",
										sell.getSellMarkId());
						Float haschukuCount = (Float) totalDao
								.getObjectByCondition(
										"select sum(sellCount) from Sell where sellMarkId=? ",
										sell.getSellMarkId());
						if (haschukuCount == null) {
							haschukuCount = 0f;
						}
						if (qp != null) {
							// if(qp.getJfcount()==null){
							// qp.setJfcount( sell.getSellCount());
							// }else{
							// qp.setJfcount(qp.getJfcount()+
							// sell.getSellCount());
							// }
							// 不确定
							String date = Util.getDateTime("yyyy-MM-dd");
							Float singSell = null;
							Double d = (Double) totalDao
									.getObjectByCondition(
											"select hsPrice from Price where 1=1 "
													+ addSql2
													+ " and pricePeriodStart<=? and (pricePeriodEnd is null or pricePeriodEnd>=?)",
											qp.getMarkId(), date, date);
							if (d != null) {// 有销售价格
								singSell = Float.parseFloat(d.toString());
								if (singSell == null) {
									singSell = 0f;
								}
								Float singBj = (Float) totalDao
										.getObjectByCondition(
												"select sum(money) from ProjectTime where quoId=? and classNumber !='gzf' ",
												qp.getId());
								if (singBj == null) {
									singBj = 0f;
								}
								Float singlr = singSell - singBj;// 获取单间利润
								String sqlstatus = " 1=1";
								Float money1 = (Float) totalDao
										.getObjectByCondition(
												"select sum(money) from ProjectTime where quoId=?",
												qp.getId());
								if (money1 == null) {
									money1 = 0f;// 公司报价投入
								}
								Procard procard = (Procard) totalDao
										.getObjectByCondition(
												"from Procard where markId=? and selfCard=?",
												sell.getSellMarkId(), sell
														.getSellLot());
								if (procard != null) {
									if (procard.getProductStyle().equals("试制")) {
										// 判断是否为首件
										String type = (String) totalDao
												.getObjectByCondition(
														"select type from ProjectOrderPart where markId=? and  projectOrder.id =?",
														procard.getMarkId(),
														procard
																.getPlanOrderId());
										if (type != null && type.equals("首件")) {
											sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段')";
											if (qp.getStatus() != null
													&& !qp.getStatus().equals(
															"批产申请中")
													&& !qp.getStatus().equals(
															"批产阶段")) {
												qp.setStatus("试制阶段");
											}
										} else {
											sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段','试制申请中','试制阶段')";
											if (qp.getStatus() != null) {
												qp.setStatus("批产阶段");
											}
										}
									} else {
										sqlstatus += " and proStatus in('核算完成','首件申请中','首件阶段','试制申请中','试制阶段','批产申请中','批产阶段') and addTime<='"
												+ procard.getProcardTime()
												+ "'";
										qp.setStatus("批产阶段");
									}
									// （单间成本*累计数量-公司当期总投入+个人总投入）*个人当期总投入/（个人当期总投入+公司当期总投入）
									// Float gsmoney = (Float)
									// totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where "+sqlstatus+" and qpId=?  and applyStatus='同意'",
									// qp.getId());
									// if(gsmoney==null){//公司当期总投入
									// gsmoney = money1;
									// }else{
									// gsmoney+=money1;
									// }
									Double gsmoney = qp.getRealAllfy();
									if (gsmoney == null) {
										gsmoney = 0d;
									}
									Float grmoney = (Float) totalDao
											.getObjectByCondition(
													"select sum(tzMoney) from QuotedPriceUserCost where "
															+ sqlstatus
															+ " and qpId=? and applyStatus='同意' and tzStatus!='撤资'",
													qp.getId());
									if (grmoney == null) {// 个人当期总投入
										grmoney = 0f;
									}
									Double hlmoney = (singlr * haschukuCount
											- gsmoney + grmoney)
											* grmoney / (gsmoney + grmoney);// 项目累计收益
									Double xmflmoney = 0d;// 分红
									qp.setYingkui(hlmoney);
									if (hlmoney > 0) {
										xmflmoney = hlmoney;
										if (qp.getFhmoney() != null
												&& qp.getFhmoney() > 0) {
											xmflmoney = xmflmoney
													- qp.getFhmoney();// 总收益-之前分红金额（包含公司）
											qp.setFhmoney(Float
													.parseFloat((xmflmoney + qp
															.getFhmoney())
															+ ""));

										} else {
											qp
													.setFhmoney(Float
															.parseFloat(xmflmoney
																	+ ""));
										}
										List<QuotedPriceUserCost> grCostList = totalDao
												.query(
														"from QuotedPriceUserCost where "
																+ sqlstatus
																+ " and qpId=? and applyStatus='同意' and tzStatus!='撤资'",
														qp.getId());
										if (grCostList.size() > 0) {
											for (QuotedPriceUserCost grCost : grCostList) {
												QuotedPriceFenhong qpfh = new QuotedPriceFenhong();
												qpfh.setQpucId(grCost.getId());// 对应投资id
												qpfh.setProStatus(grCost
														.getProStatus());// 项目阶段
												qpfh
														.setMoney(Float
																.parseFloat(xmflmoney
																		* grCost
																				.getTzMoney()
																		/ gsmoney
																		+ ""));// 分红金额
												qpfh.setAddTime(Util
														.getDateTime());// 分红时间
												qpfh.setUserName(grCost
														.getUserName());// 名称
												qpfh.setUserCode(grCost
														.getUserCode());// 工号
												qpfh.setDept(grCost.getDept());// 部门
												qpfh.setMarkId(qp.getMarkId());// 件号
												totalDao.save(qpfh);
												if (grCost.getKlMoney() == null) {
													grCost.setKlMoney(qpfh
															.getMoney());
												} else {
													grCost
															.setKlMoney(qpfh
																	.getMoney()
																	+ grCost
																			.getKlMoney());
												}
												totalDao.update(grCost);
											}
										}
									}
									totalDao.update(qp);
								}
							}
						}
						// 计算项目交付数量计算项目盈亏结束
					}

					// waigouPlan.setStatus("送货中");
					goods.setGoodsCurQuantity(syNumber);
					wdd.setOneCtnNum(wdd.getShNumber() / wdd.getCtn());
					wdd.setAddTime(Util.getDateTime());
					wdd.setGoodsId(goods.getGoodsId());
					wdd.setStatus("待打印");
					wdd.setWaigouDeliveryGoods(waigouDeliveryGoods);
					wdd.setIsprint("NO");
					wddSet.add(wdd);
					totalDao.update(goods);
					totalDao.save(sell);
				}
			}
			waigouDeliveryGoods.setWddSet(wddSet);
			Users loginUser = Util.getLoginUser();
			waigouDeliveryGoods.setPlanNumber(maxNumber);// 采购计划编号
			waigouDeliveryGoods.setAddTime(Util.getDateTime());
			waigouDeliveryGoods.setIsprint("NO");
			waigouDeliveryGoods.setStatus("待打印");
			totalDao.save(waigouDeliveryGoods);
			// 发送RTX提醒
			if (codeList.size() > 0) {
				for (String code : codeList) {
					// RtxUtil.sendNotify(codeList,
					// "您采购单有送货申请前前往核对,地址:http://""/WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="+waigouDelivery.getId(),
					// "送货申请提醒","0", "0");
					AlertMessagesServerImpl.addAlertMessages("待打印送货单管理",
							"您的采购订单有送货申请,申请送货的供应商为:" + loginUser.getName()
									+ "(" + loginUser.getCode() + "),送货单号为:"
									+ maxNumber + ".", "1", code);
				}
			}
		}
		return "";
	}

	/****
	 * 查询送货单
	 * 
	 * @param waigouDelivery
	 * @param list
	 * @return
	 */
	@Override
	public Object[] findDeliveryNote(WaigouDeliveryGoods waigouDeliveryGoods,
			int pageNo, int pageSize, String pageStatus, String firsttime,
			String endtime) {
		if (waigouDeliveryGoods == null) {
			waigouDeliveryGoods = new WaigouDeliveryGoods();
		}
		String hql = totalDao.criteriaQueries(waigouDeliveryGoods, "addTime",
				new String[] { firsttime, endtime }, "");

		String str = "";
		if ("dqr".equals(pageStatus)) {
			str = " and status = '待确认'";
		} else if ("gys".equals(pageStatus)) {
			str = " and status='待打印'";
			Users loginUser = Util.getLoginUser();
			hql += " and userId=" + loginUser.getId();
		} else if ("bhg".equals(pageStatus)) {
			str = " and status='退货待核对'";
		}
		// 当前供应商所遇的送货单
		List list = totalDao.findAllByPage(hql + " order by addTime desc",
				pageNo, pageSize);
		List list_sh = null;
		// 待打印送货单
		list_sh = totalDao.query(hql
				+ " and status='待打印' order by addTime desc");
		int count = totalDao.getCount(hql);
		Object[] o = { list, count, list_sh };
		return o;
	}

	/****
	 * 查询送货单明细
	 * 
	 * @param Integer
	 *            id
	 * @param list
	 * @return
	 */
	@Override
	public Object[] findDeliveryNoteDetail(Integer id) {
		if (id != null) {
			WaigouDeliveryGoods waigouDeliveryGoods = (WaigouDeliveryGoods) totalDao
					.getObjectById(WaigouDeliveryGoods.class, id);
			String hql = "from WaigouDeliveryGoodsDetail where waigouDeliveryGoods.id=? ";
			List list = totalDao.query(hql, id);
			Float sumNum = 0f;
			Float sumqrNum = 0f;
			Float sumheNum = 0f;
			if (list != null) {
				for (Object obj : list) {
					WaigouDeliveryGoodsDetail w2 = (WaigouDeliveryGoodsDetail) obj;
					sumNum += w2.getShNumber();
					if (w2.getQrNumber() != null) {
						sumqrNum += w2.getQrNumber();
					}
					if (w2.getHgNumber() != null) {
						sumheNum += w2.getHgNumber();
					}
				}
			}
			Object[] o = { waigouDeliveryGoods, list, sumNum, sumqrNum,
					sumheNum };
			return o;
		}
		return null;
	}

	/***
	 * 送货单打印
	 * 
	 * @param waigouDelivery
	 * 
	 * 
	 * @return
	 */
	@Override
	public String updateDeliveryToPrint(Integer deliveryId) {
		if (deliveryId != null) {
			WaigouDeliveryGoods waigouDeliveryGoods = (WaigouDeliveryGoods) totalDao
					.getObjectById(WaigouDeliveryGoods.class, deliveryId);
			if (waigouDeliveryGoods != null
					&& ("待打印".equals(waigouDeliveryGoods.getStatus()))) {
				String time = Util.getDateTime();
				waigouDeliveryGoods.setStatus("待送货");
				waigouDeliveryGoods.setIsprint("YES");
				waigouDeliveryGoods.setPrintTime(time);
				Set<WaigouDeliveryGoodsDetail> wddSet = waigouDeliveryGoods
						.getWddSet();
				for (WaigouDeliveryGoodsDetail waigouDeliveryGoodsDetail : wddSet) {
					waigouDeliveryGoodsDetail.setStatus("待送货");
					waigouDeliveryGoodsDetail.setPrintTime(time);
					waigouDeliveryGoodsDetail.setIsprint("YES");
					totalDao.update(waigouDeliveryGoodsDetail);
				}
				totalDao.update(waigouDeliveryGoods);
				// 向物流公司发送提醒
				smsService.send(waigouDeliveryGoods.getShContactsPhone(), "尊敬的"
						+ waigouDeliveryGoods.getShContacts() + "您好,有一批产品需要您在"
						+ waigouDeliveryGoods.getShsqDate() + "前来装运,送货单号:"
						+ waigouDeliveryGoods.getPlanNumber());
				return "";
			}
		}
		return null;
	}

	/***
	 * 送货单确认送货
	 * 
	 * @param waigouDelivery
	 * 
	 * 
	 * @return
	 */
	@Override
	public String updateDeliveryToSh(Integer deliveryId, String pageStatus,
			WaigouDeliveryGoods pagewaigouDeliveryGoods) {
		if (deliveryId != null) {
			WaigouDeliveryGoods waigouDeliveryGoods = (WaigouDeliveryGoods) totalDao
					.getObjectById(WaigouDeliveryGoods.class, deliveryId);
			if (waigouDeliveryGoods != null) {
				if ("sh".equals(pageStatus)
						&& ("待送货".equals(waigouDeliveryGoods.getStatus()))) {
					String time = Util.getDateTime();
					waigouDeliveryGoods.setStatus("送货中");
					waigouDeliveryGoods.setIsprint("YES");
					waigouDeliveryGoods.setPrintTime(time);
					Set<WaigouDeliveryGoodsDetail> wddSet = waigouDeliveryGoods
							.getWddSet();

					String procard = "(";
					for (WaigouDeliveryGoodsDetail waigouDeliveryGoodsDetail : wddSet) {
						waigouDeliveryGoodsDetail.setStatus("待送货");
						waigouDeliveryGoodsDetail.setPrintTime(time);
						waigouDeliveryGoodsDetail.setIsprint("YES");
						totalDao.update(waigouDeliveryGoodsDetail);
						procard += waigouDeliveryGoodsDetail.getMarkId()
								+ waigouDeliveryGoodsDetail.getShNumber();
					}
					procard += ")";
					totalDao.update(waigouDeliveryGoods);
					smsService.send(waigouDeliveryGoods.getQsContactsPhone(),
							"尊敬的" + waigouDeliveryGoods.getQsContacts()
									+ "您好,贵公司的产品" + procard
									+ "已经从上海红湖出发，预计2小时到达。配送员:"
									+ waigouDeliveryGoods.getShContacts()
									+ " 车牌号:" + waigouDeliveryGoods.getChepai()
									+ " 联系方式:"
									+ waigouDeliveryGoods.getShContactsPhone());
					return "true";
				} else if ("qs".equals(pageStatus)
						&& ("送货中".equals(waigouDeliveryGoods.getStatus()))) {
					String status = "";
					if (pagewaigouDeliveryGoods != null) {
						// 等待验收
						status = "已签收";
						waigouDeliveryGoods
								.setYsContacts(pagewaigouDeliveryGoods
										.getYsContacts());
						waigouDeliveryGoods
								.setYsContactsPhone(pagewaigouDeliveryGoods
										.getYsContactsPhone());
					} else {
						status = "已验收";
					}
					waigouDeliveryGoods.setStatus(status);

					Set<WaigouDeliveryGoodsDetail> wddSet = waigouDeliveryGoods
							.getWddSet();
					String procard = "(";
					for (WaigouDeliveryGoodsDetail waigouDeliveryGoodsDetail : wddSet) {
						waigouDeliveryGoodsDetail.setStatus(status);
						totalDao.update(waigouDeliveryGoodsDetail);
						procard += waigouDeliveryGoodsDetail.getMarkId()
								+ waigouDeliveryGoodsDetail.getShNumber();
					}
					procard += ")";
					totalDao.update(waigouDeliveryGoods);
					// 向供应商发送提醒
					smsService.send(waigouDeliveryGoods.getGysPhone(), "尊敬的"
							+ waigouDeliveryGoods.getGysContacts()
							+ "您好,贵公司的产品" + status + "。送货单号:"
							+ waigouDeliveryGoods.getPlanNumber());
					if (pagewaigouDeliveryGoods != null) {
						smsService
								.send(
										pagewaigouDeliveryGoods
												.getYsContactsPhone(),
										"尊敬的"
												+ pagewaigouDeliveryGoods
														.getYsContacts()
												+ "您好,贵公司的产品"
												+ procard
												+ "等待您验收。送货单号:"
												+ waigouDeliveryGoods
														.getPlanNumber()
												+ "http://dgjf.pebs.com.cn/loginforPhone.jsp?id="
												+ waigouDeliveryGoods.getId());
					}
					return "true";
				} else if ("ys".equals(pageStatus)
						&& ("已签收".equals(waigouDeliveryGoods.getStatus()))) {
					String status = "已验收";
					waigouDeliveryGoods.setStatus(status);
					Set<WaigouDeliveryGoodsDetail> wddSet = waigouDeliveryGoods
							.getWddSet();
					String procard = "(";
					for (WaigouDeliveryGoodsDetail waigouDeliveryGoodsDetail : wddSet) {
						waigouDeliveryGoodsDetail.setStatus(status);
						totalDao.update(waigouDeliveryGoodsDetail);
						procard += waigouDeliveryGoodsDetail.getMarkId()
								+ waigouDeliveryGoodsDetail.getShNumber();
					}
					procard += ")";
					totalDao.update(waigouDeliveryGoods);
					// 向供应商发送提醒
					smsService.send(waigouDeliveryGoods.getGysPhone(), "尊敬的"
							+ waigouDeliveryGoods.getGysContacts()
							+ "您好,贵公司的产品" + status + "。送货单号:"
							+ waigouDeliveryGoods.getPlanNumber());
					return "true";
				}
			}
		}
		return null;
	}

	@Override
	public String sqSuocang(Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		if (id != null) {
			Goods goods = (Goods) totalDao.get(Goods.class, id);
			String processName = "库存锁仓申请";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						Goods.class, id, "epStatus", "goodsId",
						"goodsAction!getOneGoods.action?id=" + id
								+ "&tag=update&pagestatus=mingxi", user
								.getDept()
								+ "部门 " + user.getName() + "库存锁仓申请，请您审批", true);
				if (epId != null && epId > 0) {
					goods.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						goods.setEpStatus("同意");
						goods.setIslock("YES");
					} else {
						goods.setEpStatus("未审批");
					}
					return totalDao.update(goods) + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String sqjiesuo(Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		if (id != null) {
			Goods goods = (Goods) totalDao.get(Goods.class, id);
			String processName = "库存解锁申请";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						Goods.class, id, "epStatus", "goodsId",
						"goodsAction!getOneGoods.action?id=" + id
								+ "&tag=update&pagestatus=mingxi", user
								.getDept()
								+ "部门 " + user.getName() + "库存解锁申请，请您审批", true);
				if (epId != null && epId > 0) {
					goods.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						goods.setEpStatus("同意");
						goods.setIslock("NO");
					} else {
						goods.setEpStatus("未审批");
					}
					return totalDao.update(goods) + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 查看包装申请详情
	public List findGoodsBzSqmx(Integer id) {
		// TODO Auto-generated method stub
		List<Goods_bzw> gbList = totalDao.query(
				"from Goods_bzw where goods_bzsq.id= ?", id);
		return gbList;
	}

	// 删除包装申请详情
	public Object[] deletexq(Integer id) {
		// TODO Auto-generated method stub
		Object[] obj = new Object[2];
		Goods_bzw goods_bzw = (Goods_bzw) totalDao.get(Goods_bzw.class, id);
		obj[0] = goods_bzw.getGoods_bzsq().getId();
		if (goods_bzw != null) {
			goods_bzw.setGoods_bzsq(null);
			if (totalDao.delete(goods_bzw)) {
				obj[1] = "删除成功!";
			}
		} else {
			obj[1] = "删除失败，没有找到对应的数据!";
		}
		return obj;
	}

	public String deleteall(Integer id) {
		Goods_bzsq goods_bzsq = (Goods_bzsq) totalDao.get(Goods_bzsq.class, id);
		if (goods_bzsq != null) {
			if (totalDao.delete(goods_bzsq)) {
				return "删除成功！";
			}
		}
		return "删除失败！";
	}

	// 判断是否满足最低库存采购；
	private boolean isneedcg(String markId, String specification,
			String banbenhao) {
		if (markId != null && markId.length() > 0) {
			String hql_wgj = " from YuanclAndWaigj where markId = ? and minkc is not null ";
			String hql_minkc = " select sum(goodsCurQuantity) from Goods where  markId = ?";
			if (banbenhao != null && banbenhao.length() > 0) {
				hql_wgj += " and banbenhao = '" + markId + "'";
				hql_minkc += " and banBenNumber = '" + markId + "'";
			} else {
				hql_wgj += " and (banbenhao = '' or  banbenhao is null)";
				hql_minkc += " and (banBenNumber = '' or banBenNumber is null )";
			}
			if (specification != null && specification.length() > 0) {
				hql_wgj += " and specification = '" + specification + "'";
				hql_minkc += " and goodsFormat = '" + specification + "'";
			} else {
				hql_wgj += " and (specification = '' or specification is null)";
				hql_minkc += " and (goodsFormat = '' or goodsFormat is null )";
			}
			wgj = (YuanclAndWaigj) totalDao.getObjectByCondition(hql_wgj,
					markId);
			if (wgj != null && wgj.getMinkc() != null && wgj.getMinkc() > 0) {
				hql_minkc += " HAVING   sum(goods_CurQuantity)<= ?";
				Float kcCount = (Float) totalDao.getObjectByCondition(
						hql_minkc, markId, wgj.getMinkc());
				if (kcCount != null && kcCount > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 当库存小于0.0001时;自动设置为0;
	 * 
	 * @param goodsList
	 * @param goods
	 */
	public static void pushkc(List<Goods> goodsList, Goods goods) {
		if (goodsList != null && goodsList.size() > 0) {
			for (Goods g : goodsList) {
				if (g.getGoodsCurQuantity() < 0.0001) {
					g.setGoodsCurQuantity(0f);
				}
			}
		}
		if (goods != null) {
			if (goods.getGoodsCurQuantity() < 0.0001) {
				goods.setGoodsCurQuantity(0f);
			}
		}
	}

	// 当库存小于0.0001时;自动设置为0;
	public void pushkc1() {
		// String hql_min =
		// " from Goods where goodsCurQuantity<0.0001 and goodsCurQuantity>0";
		// List<Goods> gList = totalDao.query(hql_min);
		// for (Goods goods2 : gList) {
		// goods2.setGoodsCurQuantity(0f);
		// totalDao.update(goods2);
		// }
	}

	@Override
	public String bcpplOut(String[] goodsAndProcardIds, Float[] lqCounts,
			String tag, String pwsswords) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (goodsAndProcardIds != null && goodsAndProcardIds.length > 0) {
			StringBuffer errmsg = new StringBuffer();
			for (int i = 0; i < goodsAndProcardIds.length; i++) {
				String goodsAndProcardId = goodsAndProcardIds[i];
				if (goodsAndProcardId == null
						|| goodsAndProcardId.length() == 0) {
					return "含有异常数据,请重新勾选!";
				} else {
					try {
						String[] strs = goodsAndProcardId.split(",");
						Integer goodsId = Integer.parseInt(strs[0].toString());
						Integer procardId = Integer
								.parseInt(strs[1].toString());
						if (lqCounts[i] == null) {
							return "含有异常数据,请重新勾选!";
						}
						Goods goods = new Goods();
						goods.setGoodsId(goodsId);
						goods.setProcardId(procardId);
						goods.setGoodsCurQuantity(lqCounts[i]);
						goods.setLingliaocardId(tag);
						try {
							String msg = bcpOut(goods, pwsswords);
							if (!msg.equals("true")) {
								errmsg.append("\n第" + (i + 1) + "条数据" + msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							errmsg.append("\n第" + (i + 1) + "条数据"
									+ e.getMessage());
						}
					} catch (Exception e) {
						// TODO: handle exception
						return "含有异常数据,请重新勾选!";
					}

				}
			}
			if (errmsg.length() == 0) {
				return errmsg.toString();
			} else {
				return "true";
			}
		} else {
			return "请先选择要出库的半成品!";
		}
	}

	@Override
	public List<Goods> getAllNames(Goods goods) {
		if (goods.getGoodsMarkId() != null) {
			String[] strs = goods.getGoodsMarkId().split(":");
			String goodsClass = "";
			if (null == goods.getWgType() || "".equals(goods.getWgType())) {
				goods.setWgType(goods.getGoodsClass());
				goodsClass = " and goodsClass = '" + goods.getGoodsClass()
						+ "'";
			}
			String kgliao = "";
			if (null != goods.getKgliao() && !"".equals(goods.getKgliao())) {
				kgliao = " and kgliao = '" + goods.getKgliao() + "' ";
			}
			if (null != goods.getIds() && !"".equals(goods.getIds())) {
				kgliao += " and goodsId not in (" + goods.getIds() + ") ";
			}
			if (strs.length >= 2) {
				String markId = strs[0];
				String name = strs[1];
				String sql = "from Goods where  goodsMarkId like '%" + markId
						+ "%' and name like '%" + name + "%' " + kgliao
						+ goodsClass;
				return (List<Goods>) totalDao.query(sql
						+ " and goodsCurQuantity>0");
			} else {
				String sql = "from Goods where (goodsMarkId like '%"
						+ goods.getGoodsMarkId()
						+ "%' or goodsFullName like '%"
						+ goods.getGoodsMarkId() + "%') " + kgliao + goodsClass;
				return (List<Goods>) totalDao.findAllByPage(sql
						+ " and goodsCurQuantity>0", 1, 15);

			}
		}
		return null;
	}

	@Override
	public List<Goods> getGoodsByMarkId(Goods goods) {
		if (null != goods && null != goods.getGoodsMarkId()
				&& goods.getGoodsMarkId().length() > 0) {
			String hql = "from Goods where goodsMarkId =? and goodsCurQuantity >0";
			if (null != goods.getGoodsClass()
					&& !"".equals(goods.getGoodsClass())) {
				hql += " and goodsClass ='" + goods.getGoodsClass() + "' ";
			}
			if (null != goods.getKgliao() && !"".equals(goods.getKgliao())) {
				hql += " and kgliao = '" + goods.getKgliao() + "'";
			}

			if (null != goods.getIds() && !"".equals(goods.getIds())) {
				hql += " and goodsId not in (" + goods.getIds() + ")";
			}
			List<Goods> list = totalDao.query(hql + " order by goodsMarkId",
					goods.getGoodsMarkId());
			return list;
			// 去除重复数据 数量累加
			// Set<Goods> set = new TreeSet<Goods>(new Comparator<Goods>() {
			// @Override
			// public int compare(Goods o1, Goods o2) {
			// //字符串,则按照asicc码升序排列
			// // return o1.getUserId().compareTo(o2.getUserId());
			// if(o1.getGoodsId().equals(o2.getGoodsId())){
			// return 0;
			// }
			// int compareTo =0;
			// //判断仓区
			// if(null != o1.getGoodHouseName() &&
			// !"".equals(o1.getGoodHouseName()) &&
			// null != o2.getGoodHouseName() &&
			// !"".equals(o2.getGoodHouseName())){
			// compareTo =
			// o1.getGoodHouseName().compareTo(o2.getGoodHouseName()); //0相等
			// if(compareTo!=0){
			// return compareTo;
			// }
			// }
			// //判断库位
			// if(null!= o1.getGoodsPosition() &&
			// !"".equals(o1.getGoodsPosition()) &&
			// null!= o2.getGoodsPosition() &&
			// !"".equals(o2.getGoodsPosition())){
			// compareTo =
			// o1.getGoodsPosition().compareTo(o2.getGoodsPosition()); //0相等
			// if(compareTo!=0){
			// return compareTo;
			// }
			// }
			// //判断品名
			// if(null!= o1.getGoodsFullName() &&
			// !"".equals(o1.getGoodsFullName()) &&
			// null!= o2.getGoodsFullName() &&
			// !"".equals(o2.getGoodsFullName())){
			// compareTo =
			// o1.getGoodsFullName().compareTo(o2.getGoodsFullName()); //0相等
			// if(compareTo!=0){
			// return compareTo;
			// }
			// }
			// //判断单位
			// if(null!= o1.getGoodsUnit() && !"".equals(o1.getGoodsUnit()) &&
			// null!= o2.getGoodsUnit() && !"".equals(o2.getGoodsUnit())){
			// compareTo = o1.getGoodsUnit().compareTo(o2.getGoodsUnit()); //0相等
			// if(compareTo!=0){
			// return compareTo;
			// }
			// }
			// //判断规格
			// if(null!= o1.getGoodsFormat() && !"".equals(o1.getGoodsFormat())
			// &&
			// null!= o2.getGoodsFormat() && !"".equals(o2.getGoodsFormat())){
			// compareTo = o1.getGoodsFormat().compareTo(o2.getGoodsFormat());
			// //0相等
			// if(compareTo!=0){
			// return compareTo;
			// }
			// }
			// if(compareTo==0){
			// o2.setGoodsCurQuantity(o1.getGoodsCurQuantity()+o2.getGoodsCurQuantity());
			// }
			// return compareTo;
			// //return 0;
			// //return o1.getGoodHouseName().compareTo(o2.getGoodHouseName());
			// }
			// });
			// set.addAll(list);
			// List<Goods> returnList = new ArrayList<Goods>(set);
			// //returnList.get(0).setGoodsCurQuantity(returnList.get(0).getGoodsCurQuantity()-list.get(0).getGoodsCurQuantity());
			// return returnList;
		}
		return null;
	}

	// 插入一条调仓记录+调仓审批
	@Override
	public String saveCPOneChangeWG(CpGoodsChangeWG cpChangeWg) {
		totalDao.save(cpChangeWg);
		String processName = "成品库调仓申请";
		Integer epId = null;
		try {
			epId = CircuitRunServerImpl.createProcess(processName,
					CpGoodsChangeWG.class, cpChangeWg.getId(), "ep_status",
					"id", "成品库调仓申请", true);
			if (epId != null && epId > 0) {
				cpChangeWg.setEp_Id(epId);
				CircuitRun circuitRun = (CircuitRun) totalDao.get(
						CircuitRun.class, epId);
				if ("同意".equals(circuitRun.getAllStatus())
						&& "审批完成".equals(circuitRun.getAuditStatus())) {

				} else {
					cpChangeWg.setEp_status("未审批");
				}
				return totalDao.update(cpChangeWg) + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	// 查询 成品库入外购件库调仓 记录
	@Override
	public Object[] findCPOneChangeWG(CpGoodsChangeWG cpChangeWg,
			String startDate, String endDate, int cpage, int pageSize) {
		if (cpChangeWg == null) {
			cpChangeWg = new CpGoodsChangeWG();
		}

		String hql = "from CpGoodsChangeWG";

		String goodHouseName = "";// 仓区
		// 日期范围搜索
		String[] between = new String[2];
		if (startDate != null && endDate != null && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}

		if (cpChangeWg != null) {
			hql = totalDao.criteriaQueries(cpChangeWg, "changeDate", between,
					"");// 时间仓区搜索
		}
		hql += " order by id desc";
		Object[] changes = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;// 所有库存量累加
		for (Object obj : list) {
			CpGoodsChangeWG g = (CpGoodsChangeWG) obj;
			sumcount += g.getChangeCount();
		}
		changes[0] = count;// 共有多少条数据
		changes[1] = list;// 每页库存数据
		changes[2] = sumcount;// 所有库存量累加
		changes[3] = true;
		return changes;

	}

	// 成品库入外购件库调仓 出入库操作
	public String changeEP(Integer id) {
		String message = "";
		CpGoodsChangeWG change = (CpGoodsChangeWG) totalDao.get(
				CpGoodsChangeWG.class, id);
		// 查找库存记录
		Goods goods = (Goods) totalDao.get(Goods.class, change.getGoodsId());
		// 审批同意 出库，更新库存 ，入库
		Sell sell = new Sell();
		Boolean chukuFlag = false, updateFlag = false, rukuFlag = false;
		// 1)出库: 出库记录表插值
		sell.setSellWarehouse(change.getCpGoodsClass());
		sell.setGoodHouseName(change.getCpGoodHouseName());
		sell.setKuwei(change.getCpGoodsPosition());
		sell.setSellMarkId(change.getGoodsMarkId());
		sell.setSellLot(change.getGoodsLotId());
		sell.setSellGoods(change.getGoodsFullName());
		sell.setSellFormat(change.getGoodsFormat());
		sell.setSellCount(change.getChangeCount());// 数量
		// *************************************************设置出库日期
		Date now = new Date();
		String time = Util.DateToString(now, "yyyy-MM-dd HH:mm:ss");
		String date = Util.DateToString(now, "yyyy-MM-dd");
		sell.setSellDate(date);
		sell.setSellTime(time);
		sell.setSellAdminId(change.getChangePeopleId());
		sell.setSellAdminName(change.getChangePeoleName());
		sell.setStyle("调仓出库");
		sell.setBanBenNumber(goods.getBanBenNumber());
		chukuFlag = totalDao.save(sell);

		if (chukuFlag) {
			// 2)更新库存
			if (goods.getGoodsCurQuantity() - change.getChangeCount() < 0) {
				message = "库存不足，调仓失败";
				// 调仓失败 ，更新调仓表实际调仓数量
				change.setActualChangeCount(0F);
				change.setEp_status("库存不足，调仓失败");
				totalDao.update(change);
				return message;
			} else {
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						- change.getChangeCount());
				updateFlag = totalDao.update(goods);
				if (updateFlag) {
					// 3)入库
					GoodsStore goodStore = new GoodsStore();
					goodStore.setGoodsStoreWarehouse(change.getWgGoodsClass());
					goodStore.setGoodHouseName(change.getWgGoodHouseName());
					goodStore
							.setGoodsStorePosition(change.getWgGoodsPosition());
					goodStore.setKuweiId(null);
					goodStore.setGoodsStoreCount(sell.getSellCount());
					goodStore.setGoodsStoreTime(sell.getSellTime());
					goodStore.setGoodsStorePerson(sell.getSellAdminName());
					goodStore.setGoodsStoreGoodsMore("成品库入外购件库调仓");
					goodStore.setGoodsStoreAdminId(sell.getSellAdminId());
					goodStore.setGoodsStoreAdminName(sell.getSellAdminName());
					goodStore.setInputSource("调仓入库");
					goodStore.setStatus("入库");
					goodStore.setGoodsStoreMarkId(change.getGoodsMarkId());
					goodsStoreServer.saveSgrk(goodStore);
					change.setActualChangeCount(change.getChangeCount());
					change.setEp_status("调仓成功");
					totalDao.update(change);
					message = "调仓成功";
					return message;
				}
			}
		}
		message = "调仓失败";
		// AlertMessagesServerImpl.addAlertMessages("成品库调仓结果反馈", message,new
		// Integer[]{},"",true);
		return message;
	}

	// 导出成品库--》外购件（调仓 )记录
	@Override
	public void exportCPChangeWG(CpGoodsChangeWG cpChangeWg, String startDate,
			String endDate) {
		try {
			String hql = " from CpGoodsChangeWG where 1=1 ";
			String goodHouseName = "";
			if (cpChangeWg == null) {
				cpChangeWg = new CpGoodsChangeWG();
			}
			if (cpChangeWg != null) {
				// 日期范围搜索
				String[] between = new String[2];
				if (startDate != null && endDate != null && !"".equals(endDate)
						&& !"".equals(startDate)) {
					between[0] = startDate;
					between[1] = endDate;
				}

				// 仓区条件goodHouseName
				// String hql_cq = "  goodHouse in (";
				// if (nectHistory.getGoodHouse() != null &&
				// nectHistory.getGoodHouse() != null
				// && nectHistory.getGoodHouse().length() > 0) {
				// String str = "";
				// String[] cangqus = nectHistory.getGoodHouse().split(",");
				// if (cangqus != null && cangqus.length > 0) {
				// for (int i = 0; i < cangqus.length; i++) {
				// str += ",'" + cangqus[i] + "'";
				// }
				// if (str.length() >= 1) {
				// str = str.substring(1);
				// }
				// hql_cq += str;
				// }
				// goodHouseName = nectHistory.getGoodHouse();
				// nectHistory.setGoodHouse(null);//?
				// hql_cq += ")";
				// } else {
				// hql_cq = "";
				// }

				if (cpChangeWg != null) {
					hql = totalDao.criteriaQueries(cpChangeWg, "changeDate",
							between, "");// 时间仓区搜索
				}
			}

			List<CpGoodsChangeWG> cpChangeWgs = totalDao.find(hql);
			HttpServletResponse response = ServletActionContext.getResponse();
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("成品库 入 外购件库 调仓历史记录".getBytes("GB2312"),
							"8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("调仓数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			ws.setColumnView(9, 20);

			ws.addCell(new Label(0, 0, "件号"));
			ws.addCell(new Label(1, 0, "批次"));
			ws.addCell(new Label(2, 0, "品名"));
			ws.addCell(new Label(3, 0, "规格"));
			ws.addCell(new Label(4, 0, "单位"));
			ws.addCell(new Label(5, 0, "申请调仓数量"));
			ws.addCell(new Label(6, 0, "实际调仓数量"));
			ws.addCell(new Label(7, 0, "申请时间"));
			ws.addCell(new Label(8, 0, "调仓人姓名"));
			ws.addCell(new Label(9, 0, "审批状态"));

			for (int i = 0; i < cpChangeWgs.size(); i++) {
				CpGoodsChangeWG change = cpChangeWgs.get(i);
				ws.addCell(new Label(0, i + 1, change.getGoodsMarkId()));
				ws.addCell(new Label(1, i + 1, change.getGoodsLotId()));
				ws.addCell(new Label(2, i + 1, change.getGoodsFullName()));
				ws.addCell(new Label(3, i + 1, change.getGoodsFormat()));
				ws.addCell(new Label(4, i + 1, change.getGoodsUnit()));
				ws.addCell(new Label(5, i + 1, change.getChangeCount()
						.toString()));
				if (change.getActualChangeCount() == null) {
					change.setActualChangeCount(0F);
				}
				ws.addCell(new Label(6, i + 1, change.getActualChangeCount()
						.toString()));
				ws.addCell(new Label(7, i + 1, change.getChangeDate()));
				ws.addCell(new Label(8, i + 1, change.getChangePeoleName()));
				ws.addCell(new Label(9, i + 1, change.getEp_status()));

			}
			wwb.write();
			wwb.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean deleteOneChange(CpGoodsChangeWG cpChangeWg) {
		if (cpChangeWg != null) {
			return totalDao.delete(cpChangeWg);
		}
		return false;
	}

	@Override
	public Object[] findDeliveryNoteDetail(
			WaigouDeliveryGoodsDetail waigouDeliveryGoodsDetail, int pageNo,
			int pageSize, String pageStatus, String firsttime, String endtime) {
		// TODO Auto-generated method stub
		if (waigouDeliveryGoodsDetail == null) {
			waigouDeliveryGoodsDetail = new WaigouDeliveryGoodsDetail();
		}
		String hql = totalDao.criteriaQueries(waigouDeliveryGoodsDetail,
				"addTime", new String[] { firsttime, endtime }, "");

		List list = totalDao.findAllByPage(hql + " order by addTime desc",
				pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Float sumNum = 0f;
		Float sumqrNum = 0f;
		Float sumheNum = 0f;
		if (list != null) {
			for (Object obj : list) {
				WaigouDeliveryGoodsDetail w2 = (WaigouDeliveryGoodsDetail) obj;
				sumNum += w2.getShNumber();
				if (w2.getQrNumber() != null) {
					sumqrNum += w2.getQrNumber();
				}
				if (w2.getHgNumber() != null) {
					sumheNum += w2.getHgNumber();
				}
			}
		}
		Object[] o = { list, sumNum, sumqrNum, sumheNum, count };
		return o;
	}

	@Override
	public List<GoodsStore> AjaxFindGoodsStore(GoodsStore gs) {
		if (gs != null) {
			String hql = totalDao.criteriaQueries(gs, null);
			return totalDao.query(hql);
		}
		return null;
	}

	@Override
	public List<Sell> AjaxFindSell(Sell sell) {
		if (sell != null) {
			String hql = totalDao.criteriaQueries(sell, null);
			return totalDao.query(hql);
		}
		return null;
	}

	@Override
	public void exportEXCELZj(Map mapgoods, Goods goods, String tag, String type) {
		if (goods == null) {
			goods = new Goods();
		}
		List<Goods> goodsList = new ArrayList<Goods>();
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if ("已到期".equals(tag)) {
			goodsList = findgoodsDQ1(goods, type);
		} else if ("将到期".equals(tag)) {
			goodsList = findgoodsDQ(goods, type);
		} else if ("未到期".equals(tag)) {
			map = findZJgoodsStore(mapgoods, 0, 0, type);
			goodsList = (List<Goods>) map.get(1);
		} else {
			goodsList.addAll(findgoodsDQ1(goods, type));
			goodsList.addAll(findgoodsDQ(goods, type));
			map = findZJgoodsStore(mapgoods, 0, 0, type);
			goodsList.addAll((List<Goods>) map.get(1));
		}

		try {

			HttpServletResponse response = ServletActionContext.getResponse();
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("库存质检信息".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("调仓数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			ws.setColumnView(9, 20);

			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "件号"));
			ws.addCell(new Label(2, 0, "品名"));
			ws.addCell(new Label(3, 0, "规格"));
			ws.addCell(new Label(4, 0, "版本"));
			ws.addCell(new Label(5, 0, "数量"));
			ws.addCell(new Label(6, 0, "转换数量"));
			ws.addCell(new Label(7, 0, "入库时间"));
			ws.addCell(new Label(8, 0, "质检周期(天)"));
			ws.addCell(new Label(9, 0, "上次质检时间"));
			ws.addCell(new Label(10, 0, "到期时间"));
			ws.addCell(new Label(11, 0, "库别"));
			ws.addCell(new Label(12, 0, "仓区"));
			ws.addCell(new Label(13, 0, "库位"));
			ws.addCell(new Label(14, 0, "计划单号"));

			for (int i = 0; i < goodsList.size(); i++) {
				Goods goods0 = goodsList.get(i);
				ws.addCell(new Label(0, i + 1, (i + 1) + ""));
				ws.addCell(new Label(1, i + 1, goods0.getGoodsMarkId()));
				ws.addCell(new Label(2, i + 1, goods0.getGoodsFullName()));
				ws.addCell(new Label(3, i + 1, goods0.getGoodsFormat()));
				ws.addCell(new Label(4, i + 1, goods0.getBanBenNumber()));
				ws.addCell(new jxl.write.Number(5, i + 1, goods0
						.getGoodsCurQuantity()));
				ws.addCell(new jxl.write.Number(6, i + 1, goods0
						.getGoodsZhishu()));
				ws.addCell(new Label(7, i + 1, goods0.getGoodsChangeTime()));
				ws.addCell(new jxl.write.Number(8, i + 1, goods0
						.getGoodsround()));
				ws.addCell(new Label(9, i + 1, goods0.getGoodslasttime()));
				ws.addCell(new Label(10, i + 1, goods0.getGoodsnexttime()));
				ws.addCell(new Label(11, i + 1, goods0.getGoodsClass()));
				ws.addCell(new Label(12, i + 1, goods0.getGoodHouseName()));
				ws.addCell(new Label(13, i + 1, goods0.getGoodsPosition()));
				ws.addCell(new Label(14, i + 1, goods0.getGoodsArtsCard()));
			}
			wwb.write();
			wwb.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 调库
	@Override
	public String exChangeGoods(Sell sell, GoodsStore goodsStore, Goods goods,
			String tag) throws Exception {
		Users user = (Users) Util.getLoginUser();
		String nowTime = Util.getDateTime();
		String time = Util.getDateTime("yyyy-MM-dd");
		if (sell == null || goodsStore == null) {
			return "数据异常";
		} else {
			goods = (Goods) totalDao.getObjectById(Goods.class, goods
					.getGoodsId());
			if (goods.getGoodsCurQuantity() <= 0) {
				return "库存量为0不能进行调库申请!~";
			}
			if (sell.getSellCount() > goods.getGoodsCurQuantity()) {
				return "申请调库数量:" + sell.getSellCount() + ">现有库存量:"
						+ goods.getGoodsCurQuantity();
			}
			if (goods.getGoodsClass().equals("备货库")
					&& goodsStore.getGoodsStoreWarehouse().equals("成品库")) {
				return "备货库不能转入成品库，谢谢";
			}
			// if (goods.getBzApplyCount() != null) {//包装申请数量
			// goods.setBzApplyCount(goods.getBzApplyCount() -
			// sell.getSellCount());
			// if (goods.getBzApplyCount() < 0) {
			// goods.setBzApplyCount(0f);
			// }
			// }
			sell.setSellAdminName(user.getName());
			sell.setSellAdminId(user.getId());
			sell.setSellTime(nowTime);
			sell.setSellDate(nowTime);
			sell.setGoodsId(goods.getGoodsId());
			sell.setSellLot(goods.getGoodsLotId());
			sell.setSellMarkId(goods.getGoodsMarkId());
			sell.setSellGoods(goods.getGoodsFullName());// 品名
			sell.setSellFormat(goods.getGoodsFormat());
			sell.setKgliao(goods.getKgliao());
			sell.setSellWarehouse(goods.getGoodsClass());
			sell.setGoodHouseName(goods.getGoodHouseName());
			sell.setKuwei(goods.getGoodsPosition());
			// sell.setSellWarehouse(goodsStore.getGoodsStoreWarehouse());
			// sell.setGoodHouseName(goodsStore.getGoodHouseName());
			// sell.setKuwei(goodsStore.getGoodsStorePosition());
			sell.setSellUnit(goods.getGoodsUnit());
			sell.setSellSupplier(goods.getGoodsSupplier());
			sell.setSellArtsCard(goods.getGoodsArtsCard());
			sell.setSellCompanyName(goods.getGoodsCustomer());
			sell.setBanBenNumber(goods.getBanBenNumber());

			sell.setSellAdminName(Util.getLoginUser().getName());
			sell.setGoodsPrice(goods.getGoodsPrice());// 平均单价（库存成本）库存单价不含税
			sell.setSellPrice(goods.getGoodsBuyPrice());
			sell.setSellbhsPrice(goods.getGoodsBuyBhsPrice());
			sell.setTaxprice(goods.getTaxprice());
			sell.setSellSupplier(goods.getGoodsSupplier());
			sell.setSupplierId(goods.getSupplierId());
			sell.setCustomerId(goods.getCustomerId());
			sell.setSellCompanyName(goods.getGoodsCustomer());
			sell.setSellCompanyPeople(goods.getCustomerId());
			sell.setYwmarkId(goods.getYwmarkId());
			sell.setSellGoodsMore(goodsStore.getGoodsStoreGoodsMore());// 备注
			if (goods.getNeiorderId() != null
					&& !goods.getNeiorderId().equals("")) {
				sell.setOrderNum(goods.getNeiorderId());
			}
			if (goods.getWaiorderId() != null
					&& !goods.getWaiorderId().equals("")) {
				sell.setOutOrderNumer(goods.getWaiorderId());
			}
			// sell.setSellLot(goods.getGoodsLotId());
			// sell.setSellWarehouse(goods.getGoodsClass());
			// sell.setGoodHouseName(goods.getGoodHouseName());
			// sell.setKuwei(goods.getGoodsPosition());
			if (null != goods.getGoodsZhishu() && goods.getGoodsZhishu() > 0) {// 转换数量
				// if (goods.getGoodsZhishu().equals(sell.getSellZhishu())) {
				// sell.setSellCount(goods.getGoodsCurQuantity());
				// goods.setGoodsCurQuantity(0f);
				// } else {
				// goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
				// * (1 - sell.getSellZhishu() /goods.getGoodsZhishu()));
				// if (null == sell.getSellCount()) {
				// sell.setSellCount(goods.getGoodsCurQuantity()
				// * sell.getSellZhishu() /goods.getGoodsZhishu());
				// }
				// goods.setGoodsZhishu(goods.getGoodsZhishu() -
				// sell.getSellZhishu());
				// }
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						- sell.getSellCount());
			} else {
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						- sell.getSellCount());
			}
			if (null == sell.getSellFormat()) {
				sell.setSellFormat("");
			}
			if (totalDao.save(sell) && totalDao.update(goods)) {

				// 生成入库记录
				goodsStore.setApplyTime(nowTime);
				// goodsStore.setBanBenNumber(goods.getBanBenNumber());//加入可修改版本页面传进来
				goodsStore.setGoodsStoreLot(goods.getGoodsLotId());
				goodsStore.setGoodsStoreGoodsName(goods.getGoodsFullName());
				goodsStore.setGoodsStoreFormat(goods.getGoodsFormat());
				goodsStore.setWgType(goods.getWgType());
				goodsStore.setGoodsStoreUnit(goods.getGoodsUnit());
				goodsStore.setGoodsStoreDate(nowTime);
				goodsStore.setGoodsStoreTime(nowTime);
				goodsStore.setGoodsStoreAdminId(user.getId());
				goodsStore.setGoodsStoreAdminName(user.getName());
				goodsStore.setInputSource("调库");
				goodsStore.setStatus("入库");
				goodsStore.setPrintStatus("NO");
				goodsStore.setSellId(sell.getSellId());
				// goodsStore.setGoodHouseName(sell.getGoodHouseName());
				// goodsStore.setGoodsStoreWarehouse(sell.getSellWarehouse());
				// goodsStore.setGoodsStorePosition(sell.getKuwei());

				goodsStore.setGoodsStoreNumber(sell.getSellNumber());// 数量
				goodsStore.setGoodsStoreZhishu(sell.getSellZhishu());
				goodsStore.setGoodsStoreZHUnit(sell.getGoodsStoreZHUnit());

				goodsStore.setGoodsStoreMarkId(sell.getSellMarkId());
				goodsStore.setGoodsStoreCount(sell.getSellCount());

				goodsStore.setSqUsersCode(user.getCode());
				goodsStore.setSqUsersdept(user.getDept());
				goodsStore.setSqUsersName(user.getName());
				goodsStore.setSqUsrsId(user.getId());
				goodsStore.setGoodsStorePlanner(user.getName());

				goodsStore.setYwmarkId(sell.getYwmarkId());
				goodsStore.setCustomerId(sell.getCustomerId());
				goodsStore
						.setGoodsStoreAdminName(Util.getLoginUser().getName());
				goodsStore.setGoodsStoreAdminId(Util.getLoginUser().getId());
				if (sell.getSellbhsPrice() != null) {
					goodsStore.setGoodsStorePrice(sell.getSellbhsPrice()
							.doubleValue());
				}
				if (sell.getSellPrice() != null) {
					goodsStore.setHsPrice(sell.getSellPrice().doubleValue());
				}
				if (sell.getTaxprice() != null) {
					BigDecimal decimal = new BigDecimal(sell.getTaxprice());
					goodsStore.setTaxprice(decimal.doubleValue());
				}
				// goodsStore.setTaxprice((Double)sell.getTaxprice());
				goodsStore.setGoodsStoreSupplier(sell.getSellSupplier());
				goodsStore.setGysId(sell.getSupplierId());
				goodsStore.setCustomerId(sell.getCustomerId());
				goodsStore.setGoodsStoreCompanyName(sell.getSellCompanyName());

				// style; -- hidden
				if (totalDao.save(goodsStore)) {
					// 检查库存是否存在
					String kgSql = "";
					// 供料属性
					if (goodsStore.getKgliao() != null
							&& !"".equals(goodsStore.getKgliao())) {
						kgSql += " and kgliao='" + goodsStore.getKgliao() + "'";
					}
					// 版本
					String banben_hql = "";
					if (goodsStore.getBanBenNumber() != null
							&& goodsStore.getBanBenNumber().length() > 0) {
						banben_hql = " and banBenNumber='"
								+ goodsStore.getBanBenNumber() + "'";
					}
					String unit_hql = "";
					if (sell.getSellUnit() != null
							&& sell.getSellUnit().length() > 0) {
						unit_hql = " and goodsUnit ='" + sell.getSellUnit()
								+ "'";
					}
					// 库别
					String warehouse_hql = "";
					if (goodsStore.getGoodsStoreWarehouse() != null
							&& goodsStore.getGoodsStoreWarehouse().length() > 0) {
						warehouse_hql = " and goodsClass = '"
								+ goodsStore.getGoodsStoreWarehouse() + "'";
					}
					// 仓区
					String houseName_hql = "";
					if (goodsStore.getGoodHouseName() != null
							&& goodsStore.getGoodHouseName().length() > 0) {
						houseName_hql = " and goodHouseName='"
								+ goodsStore.getGoodHouseName() + "'";
					}
					// 库位
					String position_hql = "";
					if (goodsStore.getGoodsStorePosition() != null
							&& goodsStore.getGoodsStorePosition().length() > 0) {
						position_hql = " and goodsPosition='"
								+ goodsStore.getGoodsStorePosition() + "'";
					}
					// 批次
					String goodsLotId_hql = "";
					if (sell.getSellLot() != null
							&& sell.getSellLot().length() > 0) {
						goodsLotId_hql = " and goodsLotId='"
								+ sell.getSellLot() + "'";
					}
					String orderHql = "";// 内部订单号和外部订单号
					if (goodsStore.getNeiorderId() != null
							&& goodsStore.getNeiorderId().length() > 0) {
						orderHql = " and neiorderId='"
								+ goodsStore.getNeiorderId() + "' ";
					}
					if (goodsStore.getWaiorderId() != null
							&& goodsStore.getWaiorderId().length() > 0) {
						orderHql += " and waiorderId = '"
								+ goodsStore.getWaiorderId() + "'";
					}
					String ywmarkId = "";// 业务件号
					if (goodsStore.getYwmarkId() != null
							&& goodsStore.getYwmarkId().length() > 0) {
						ywmarkId = " and ywmarkId = '"
								+ goodsStore.getYwmarkId() + "'";
					}
					String hql = "from Goods where goodsMarkId = ? "
							+ goodsLotId_hql + banben_hql + kgSql
							+ " and goodsClass=? " + warehouse_hql
							+ houseName_hql + position_hql + unit_hql
							+ orderHql
							+ " and (fcStatus is null or fcStatus='可用')";
					Goods s = (Goods) totalDao.getObjectByCondition(hql,
							new Object[] { sell.getSellMarkId(),
									goodsStore.getGoodsStoreWarehouse() });
					if (s == null) {
						// 新保存
						Goods g = new Goods();
						BeanUtils.copyProperties(goods, g);
						g.setGoodsId(null);
						g.setGoodsClass(goodsStore.getGoodsStoreWarehouse());// 库别
						g.setGoodHouseName(goodsStore.getGoodHouseName());// 仓区
						g.setGoodsPosition(goodsStore.getGoodsStorePosition());// 库位
						g.setGoodsChangeTime(time);
						g.setGoodsCurQuantity(sell.getSellCount());// 数量
						g.setGoodsZhishu(sell.getSellZhishu());// 转换数量
						g.setGoodsStoreZHUnit(sell.getGoodsStoreZHUnit());// 转换单位
						g.setGoodsStyle(goodsStore.getStyle());
						g.setBanBenNumber(goodsStore.getBanBenNumber());
						g.setKgliao(goodsStore.getKgliao());

						g.setYwmarkId(goodsStore.getYwmarkId());
						g.setGoodsPrice(goods.getGoodsPrice());
						g.setGoodsBuyPrice(goods.getGoodsBuyPrice());
						g.setGoodsBuyBhsPrice(goods.getGoodsBuyBhsPrice());
						g.setTaxprice(goods.getTaxprice());
						g.setCustomerId(goodsStore.getCustomerId());
						g.setSupplierId(goodsStore.getGysId());
						g.setGoodsSupplier(goodsStore.getGoodsStoreSupplier());
						g.setGoodsCustomer(goodsStore
								.getGoodsStoreCompanyName());
						g.setGoodsMore2(goodsStore.getGoodsStoreGoodsMore());
						if (goodsStore.getNeiorderId() != null) {
							g.setNeiorderId(goodsStore.getNeiorderId());
						}
						if (goodsStore.getWaiorderId() != null) {
							g.setWaiorderId(goodsStore.getWaiorderId());
						}
						return "" + totalDao.save(g);
					} else {
						// 修改数量
						// goods
						s.setGoodsCurQuantity(s.getGoodsCurQuantity()
								+ sell.getSellCount());
						return "" + totalDao.update(s);
					}

				}
			}
		}
		return "调库失败";
	}

	// 根据物料需求单出库
	@Override
	public String[] sellByManualOrder(List<Goods> goodsList,
			List<Sell> sellList, Sell sellParam, String tag) throws Exception {

		if (sellList != null && goodsList != null && sellList.size() > 0
				&& goodsList.size() > 0) {
			StringBuffer buffer = new StringBuffer();
			boolean flag = false;
			Users user = Util.getLoginUser();
			String nowTime = Util.getDateTime();
			ManualOrderPlanTotal total = null;
			int successCount = 0;
			int haveCount = 0;
			for (int i = 0; i < sellList.size(); i++) {
				Goods goods = (Goods) totalDao.getObjectById(Goods.class,
						goodsList.get(i).getGoodsId());
				Sell sell = sellList.get(i);// 需求单ID 出库数量
				if (goods.getGoodsCurQuantity() <= 0
						|| sell.getSellCount() <= 0) {
					continue;
				}
				sell.setSellAdminName(user.getName());
				sell.setSellAdminId(user.getId());
				sell.setSellTime(nowTime);
				sell.setSellDate(sellParam.getSellDate());
				sell.setGoodsId(goods.getGoodsId());
				sell.setSellLot(goods.getGoodsLotId());
				sell.setSellMarkId(goods.getGoodsMarkId());
				sell.setSellGoods(goods.getGoodsFullName());// 品名
				sell.setSellFormat(goods.getGoodsFormat());
				sell.setSellUnit(goods.getGoodsUnit());
				sell.setKgliao(goods.getKgliao());
				sell.setSellWarehouse(goods.getGoodsClass());
				sell.setGoodHouseName(goods.getGoodHouseName());
				sell.setKuwei(goods.getGoodsPosition());
				sell.setSellSupplier(goods.getGoodsSupplier());
				sell.setSellArtsCard(goods.getGoodsArtsCard());
				sell.setSellCompanyName(goods.getGoodsCustomer());
				sell.setStyle(sellParam.getStyle());
				sell.setSellCompanyPeople(sellParam.getSellCompanyPeople());
				sell.setSellCharger(sellParam.getSellCharger());
				sell.setSellGoodsMore(sellParam.getSellGoodsMore());
				flag = totalDao.save(sell);
				if (!flag) {
					throw new Exception("出库失败。");
				} else {
					buffer.append("," + sell.getSellId());
					// 出库成功--修改库存数量
					float sellCount = sell.getSellCount();
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							- sellCount);
					flag = totalDao.update(goods);
					if (!flag) {
						throw new Exception("库存修改失败");
					}

					// ------
					// 出库面积更新---判断仓区，成品面积有才更新仓区占地面积
					// 仓区
					GoodHouse goodHouse = (GoodHouse) totalDao
							.get(
									"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
									new Object[] { sell.getSellWarehouse(),
											sell.getGoodHouseName() });
					// 单件
					ProcardTemplate procardArea = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and "
											+ "(dataStatus!='删除' or dataStatus is  null)  "
											+ "and (banbenStatus='默认' or banbenStatus is null  "
											+ "or banbenStatus='') and productStyle='批产' ",
									sell.getSellMarkId());
					if (goodHouse != null && procardArea != null) {
						// 存在面积
						if (goodHouse.getGoodAllArea() > 0
								&& goodHouse.getGoodAllArea() != null
								&& procardArea.getActualUsedProcardArea() != null
								&& procardArea.getActualUsedProcardArea() > 0) {
							// 已用面积减少
							Double procardActualUsed = procardArea
									.getActualUsedProcardArea()
									* sell.getSellCount();
							procardActualUsed = Double
									.valueOf(new DecimalFormat("0.00")
											.format(procardActualUsed));
							if (procardActualUsed >= goodHouse
									.getGoodIsUsedArea()) {
								goodHouse.setGoodIsUsedArea(0D);
								goodHouse.setGoodLeaveArea(goodHouse
										.getGoodAllArea()
										- goodHouse.getGoodIsUsedArea());
							} else {
								Double use = Double.valueOf(new DecimalFormat(
										"0.00").format(goodHouse
										.getGoodIsUsedArea()
										- procardActualUsed));
								goodHouse.setGoodIsUsedArea(use);
								goodHouse.setGoodLeaveArea(goodHouse
										.getGoodAllArea()
										- goodHouse.getGoodIsUsedArea());
							}
							totalDao.update(goodHouse);

						}
					}

					boolean bool = isneedcg(sell.getSellMarkId(), sell
							.getSellFormat(), sell.getBanBenNumber());
					if (bool) {
						// 添加手动下单信息;
						ManualOrderPlanDetail mod = new ManualOrderPlanDetail();
						mod.setMarkId(wgj.getMarkId());// 件号
						mod.setProName(wgj.getName());// 名称
						mod.setSpecification(wgj.getSpecification());// 规格
						mod.setBanben(wgj.getBanbenhao());// 版本号
						mod.setKgliao(wgj.getKgliao());// 供料属性
						mod.setTuhao(wgj.getTuhao());// 图号
						mod.setWgType(wgj.getWgType());// 物料类别
						mod.setUnit(wgj.getUnit());// 单位
						mod.setCgnumber(wgj.getCgcount());// 采购数量
						mod.setType("安全库存");// 添加方式
						mod.setAddTime(Util.getDateTime());// 添加时间
						mod.setRukuNum(0f);
						mod.setRemarks("件号" + wgj.getMarkId() + "零件名称"
								+ wgj.getName() + "总库存量低于安全库存" + wgj.getMinkc()
								+ "系统自动下单;");
						totalDao.save(mod);
					}

					// ------修改需求明细
					ManualOrderPlanDetail detail = (ManualOrderPlanDetail) totalDao
							.getObjectById(ManualOrderPlanDetail.class, sell
									.getMopdId());
					total = detail.getPlanTotal();

					Float yilingnumber = detail.getPickingNumber();// 已领料数量
					if (yilingnumber == null) {
						float cgnumber = detail.getCgnumber();
						if (sellCount == cgnumber) {
							detail.setPickingNumber(cgnumber);
							detail.setPickingStatus("已领完");
							successCount++;
						} else {
							detail.setPickingNumber(sellCount);
							detail.setPickingStatus("未领完");
							haveCount++;
						}
					} else {
						float cgnumber = detail.getCgnumber() - yilingnumber;
						if (sellCount == cgnumber) {
							detail.setPickingNumber(yilingnumber + cgnumber);
							detail.setPickingStatus("已领完");
							successCount++;
						} else {
							detail.setPickingNumber(yilingnumber + sellCount);
							detail.setPickingStatus("未领完");
							haveCount++;
						}
					}

					totalDao.update(detail);
				}

			}

			// 设置汇总是否领完料
			if (goodsList.size() == successCount) {
				total.setPickingStatus("已领完");
			} else if (haveCount > 0) {
				total.setPickingStatus("未领完");
			}
			totalDao.update(total);
			return new String[] { "出库成功", buffer.toString() };
		}

		return null;
	}

	@Override
	public List<Sell> findhandWordByNumber(Integer number) {

		if (number != null) {
			return totalDao.query("from Sell where handwordSellNumber=?",
					number);
		}

		return null;
	}

	@Override
	public boolean updateGoodHouseBySell(Sell sell) {
		// 出库面积更新---判断仓区，成品面积有才更新仓区占地面积
		// 仓区
		GoodHouse goodHouse = (GoodHouse) totalDao
				.get(
						"from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",
						new Object[] { sell.getSellWarehouse(),
								sell.getGoodHouseName() });
		// 单件
		ProcardTemplate procardArea = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='') and productStyle='批产' ",
						sell.getSellMarkId());
		if (goodHouse != null && procardArea != null) {
			// 存在面积
			if (goodHouse.getGoodAllArea() > 0
					&& goodHouse.getGoodAllArea() != null
					&& procardArea.getActualUsedProcardArea() != null
					&& procardArea.getActualUsedProcardArea() > 0) {
				/*
				 * //此次占用面积 Double
				 * procardActualUsed=procardArea.getActualUsedProcardArea
				 * ()*sell.getSellCount(); procardActualUsed=Double.valueOf(new
				 * DecimalFormat("0.00").format(procardActualUsed));
				 * if(procardActualUsed
				 * >=goodHouse.getGoodAllArea()){//占用大于或等于已满
				 * goodHouse.setGoodIsUsedArea(0D); goodHouse.setGoodLeaveArea
				 * (goodHouse.getGoodAllArea()-goodHouse .getGoodIsUsedArea());
				 * }else{ goodHouse.setGoodIsUsedArea(
				 * goodHouse.getGoodIsUsedArea()-procardActualUsed); goodHouse
				 * .setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse
				 * .getGoodIsUsedArea()); } totalDao.update(goodHouse);
				 */

				// 已用面积减少
				Double procardActualUsed = procardArea
						.getActualUsedProcardArea()
						* sell.getSellCount();
				procardActualUsed = Double.valueOf(new DecimalFormat("0.00")
						.format(procardActualUsed));
				if (procardActualUsed >= goodHouse.getGoodIsUsedArea()) {
					goodHouse.setGoodIsUsedArea(0D);
					goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()
							- goodHouse.getGoodIsUsedArea());
				} else {
					Double use = Double.valueOf(new DecimalFormat("0.00")
							.format(goodHouse.getGoodIsUsedArea()
									- procardActualUsed));
					goodHouse.setGoodIsUsedArea(use);
					goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()
							- goodHouse.getGoodIsUsedArea());
				}
				return totalDao.update(goodHouse);

			}
		}
		return true;
	}

	// 多条手工出库
	@Override
	public boolean sellGoodsMultiterm(List<Goods> goodsList,
			List<Sell> sellList, String isNeed) throws Exception {
		boolean b = true;
		Users user = (Users) Util.getLoginUser();
		String nowTime = Util.getDateTime();
		Integer maxNumber = 0;
		for (int i = 0; i < sellList.size(); i++) {
			Goods goods = goodsList.get(i);
			Sell sell = sellList.get(i);

			Goods g = (Goods) totalDao.getObjectById(Goods.class, goods
					.getGoodsId());
			if (g.getBzApplyCount() != null) {
				g.setBzApplyCount(g.getBzApplyCount() - sell.getSellCount());
				if (g.getBzApplyCount() < 0) {
					g.setBzApplyCount(0f);
				}
			}
			sell.setSellAdminName(user.getName());
			sell.setSellAdminId(user.getId());
			sell.setSellTime(nowTime);
			sell.setGoodsId(g.getGoodsId());
			sell.setSellLot(g.getGoodsLotId());
			if (null == sell.getSellFormat()) {
				sell.setSellFormat("");
			}

			// if ((sell.getSellWarehouse().equals("成品库")) && isNeed != null
			// && (isNeed.equals("全") || isNeed.equals("外"))) { }
			sell.setGoodsPrice(g.getGoodsPrice());// 平均单价（库存成本）
			sell.setSellPrice(g.getGoodsBuyPrice());
			sell.setSellbhsPrice(g.getGoodsBuyBhsPrice());
			sell.setTaxprice(g.getTaxprice());

			// 判断库存中的数量是否够用
			if ((g.getKgliao() == null || !g.getKgliao().equals("CS"))
					&& ("领料出库".equals(sell.getStyle()) || "报废出库".equals(sell
							.getStyle()))) {
				Float goodsCurQuantity = g.getGoodsCurQuantity();
				List<Sell> historySellList = totalDao
						.query(
								"from Sell where goodsId = ?"
										+ " and handwordSellEpId is not null and handwordSellStatus in ('未审批','审批中')",
								g.getGoodsId());
				if (historySellList != null && historySellList.size() > 0) {
					float historyNumber = 0;
					for (Sell sell2 : historySellList) {
						historyNumber += sell2.getSellCount();
					}
					if (goodsCurQuantity - historyNumber - sell.getSellCount() < 0) {
						throw new RuntimeException("件号：" + g.getGoodsMarkId()
								+ ",当前库存数量不足，已有被申请数量，" + "本次最大可出库数量为："
								+ (goodsCurQuantity - historyNumber));
					}
				}

				if (i == 0) {
					maxNumber = (Integer) totalDao
							.getObjectByCondition("select max(handwordSellNumber) from Sell ");
					if (maxNumber != null) {
						maxNumber += 1;
					} else {
						maxNumber = 1;
					}
				}

				sell.setHandwordSellNumber(maxNumber);
				if ("外购件库".equals(sell.getSellWarehouse())) {
					// String printNumber = updatMaxSellPrintNumber(sell,
					// nowTime);
					// sell.setPrintNumber(printNumber);
				}
				b = b & totalDao.save(sell);
				// 出库面积更新---判断仓区，成品面积有才更新仓区占地面积--------移至审批同意后
			} else {

				g.setGoodsCurQuantity(g.getGoodsCurQuantity()
						- sell.getSellCount());
				b = b & totalDao.save(sell);
				// b = b & additionalproductSell(sell);
				// if(b){
				//					
				// corePayableServer.goodsSell(sell);//生成出库凭证
				// }
				// 出库面积更新---判断仓区，成品面积有才更新仓区占地面积
				updateGoodHouseBySell(sell);

			}

			goodsStoreServer.goodshuizong0("出库", null, sell);

			// 库存小于0.0001强制设置为0
			pushkc(null, g);
			b = b & totalDao.update(g);
			boolean bool = isneedcg(sell.getSellMarkId(), sell.getSellFormat(),
					sell.getBanBenNumber());
			if (bool) {
				// 添加手动下单信息;
				ManualOrderPlanDetail mod = new ManualOrderPlanDetail();
				mod.setMarkId(wgj.getMarkId());// 件号
				mod.setProName(wgj.getName());// 名称
				mod.setSpecification(wgj.getSpecification());// 规格
				mod.setBanben(wgj.getBanbenhao());// 版本号
				mod.setKgliao(wgj.getKgliao());// 供料属性
				mod.setTuhao(wgj.getTuhao());// 图号
				mod.setWgType(wgj.getWgType());// 物料类别
				mod.setUnit(wgj.getUnit());// 单位
				mod.setCgnumber(wgj.getCgcount());// 采购数量
				mod.setType("安全库存");// 添加方式
				mod.setAddTime(Util.getDateTime());// 添加时间
				mod.setRukuNum(0f);
				mod.setRemarks("件号" + wgj.getMarkId() + "零件名称" + wgj.getName()
						+ "总库存量低于安全库存" + wgj.getMinkc() + "系统自动下单;");
				totalDao.save(mod);
			}
			// 添加修改记录
			AttendanceTowServerImpl.updateJilu(1, goods, goods.getGoodsId(), g
					.getGoodsMarkId());
		}

		if ((sellList.get(0).getKgliao() == null || !sellList.get(0)
				.getKgliao().equals("CS"))
				&& ("领料出库".equals(sellList.get(0).getStyle()) || "报废出库"
						.equals(sellList.get(0).getStyle()))) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < sellList.size(); i++) {
				if (i == 0) {
					buffer.append(sellList.get(i).getSellId());
				} else {
					buffer.append("," + sellList.get(i).getSellId());
				}
			}
			Sell sell = sellList.get(0);
			// 添加审批流程
			try {
				Integer epId = CircuitRunServerImpl.createProcess("手工出库单审核",
						Sell.class, sell.getSellId(), "handwordSellStatus",
						"sellId",
						"sellAction!printStorage.action?tag=show&selectedString="
								+ buffer.toString(), "部门：" + user.getDept()
								+ ",姓名：" + user.getName() + "申请手工出库，请您审批。",
						true);
				if (epId != null && epId > 0) {
					sell.setHandwordSellEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						for (Sell sell2 : sellList) {
							sell.setHandwordSellEpId(epId);
							sell2.setHandwordSellEpId(sell
									.getHandwordSellEpId());
							sell2.setHandwordSellStatus("同意");
							totalDao.update(sell2);
						}
					} else {
						for (Sell sell2 : sellList) {
							sell.setHandwordSellEpId(epId);
							sell2.setHandwordSellEpId(sell
									.getHandwordSellEpId());
							sell2.setHandwordSellStatus("未审批");
							totalDao.update(sell2);
						}
					}
				}

				return true;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			return true;
		}
	}

	@Override
	public String paixuPrintNumber() {
		List<GoodsStore> gsList = totalDao
				.query(" from GoodsStore where printNumber = 'WGRK005721'  ");
		for (GoodsStore gs : gsList) {
			String beforestr = "WGRK";
			String printNumber = "";
			String maxplanNumber = (String) totalDao
					.getObjectByCondition(" select max(printNumber) from GoodsStore where "
							+ "  printNumber like '" + beforestr + "0%' ");
			if (maxplanNumber != null && !"".equals(maxplanNumber)) {
				maxplanNumber = (1000001 + Integer.parseInt(maxplanNumber
						.substring(beforestr.length())))
						+ "";
				maxplanNumber = maxplanNumber.substring(1);
			} else {
				maxplanNumber = "000001";
			}
			printNumber = beforestr + maxplanNumber;
			PrintedOutOrder printedoutOrder = (PrintedOutOrder) totalDao
					.getObjectByCondition(
							" from PrintedOutOrder where planNum = ? ", gs
									.getPrintNumber());
			if (printedoutOrder != null) {
				printedoutOrder.setPlanNum(printNumber);
				Set<PrintedOut> printedOutSet = printedoutOrder
						.getPrintedOutSet();
				for (PrintedOut printedOut : printedOutSet) {
					printedOut.setPlanNum(printNumber);
					totalDao.update(printedOut);
				}
				totalDao.update(printedoutOrder);
			}
			gs.setPrintNumber(printNumber);
			totalDao.update(gs);
		}
		// List<PrintedOutOrder> printOrderList =
		// totalDao.query(" from PrintedOutOrder where planNum = ? and id>13770",
		// "WGRK005721");
		// if(printOrderList!=null && printOrderList.size()>0){
		// for (PrintedOutOrder printedOutOrder : printOrderList) {
		// String beforestr = "WGRK";
		// String printNumber ="";
		// String maxplanNumber = (String)
		// totalDao.getObjectByCondition(" select max(planNum) from PrintedOutOrder where "
		// +
		// "  planNum like '"+beforestr+"%' ");
		// if(maxplanNumber!=null && !"".equals(maxplanNumber)){
		// maxplanNumber
		// =(1000001+Integer.parseInt(maxplanNumber.substring(beforestr.length())))+"";
		// maxplanNumber = maxplanNumber.substring(1);
		// }else{
		// maxplanNumber = "000001";
		// }
		// printNumber = beforestr+maxplanNumber;
		// printedOutOrder.setPlanNum(printNumber);
		// Set<PrintedOut> printedOutSet = printedOutOrder.getPrintedOutSet();
		// for (PrintedOut printedOut : printedOutSet) {
		// printedOut.setPlanNum(printNumber);
		// if(printedOut.getEntiyId()!=null){
		// GoodsStore gs = (GoodsStore) totalDao.get(GoodsStore.class,
		// printedOut.getEntiyId());
		// if(gs!=null){
		// gs.setPrintNumber(printNumber);
		// totalDao.update(gs);
		// }
		// }
		// totalDao.update(printedOut);
		// }
		// totalDao.update(printedOutOrder);
		// }
		// }
		return null;
	}

	@Override
	public Map<String, Object> findMonthByGoods(String tag, String startMonth,
			String endMonth) {
		// findMonthByGoods
		if (startMonth != null && !startMonth.equals("")) {

		}
		ArrayList<String> mouthList = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			if (i > 0) {
				mouthList.add(Util.getLastMonth(mouthList.get(i - 1)));
			} else {
				mouthList.add(null);
			}
		}
		if (startMonth == null || startMonth.equals("")) {

		}

		if (endMonth == null || endMonth.equals("")) {

		}

		return null;
	}

	@Override
	public BaoFeiGoods getbfsqById(Integer id) {
		BaoFeiGoods bfgoods = (BaoFeiGoods) totalDao.get(BaoFeiGoods.class, id);
		return bfgoods;
	}

	@Override
	public Map<Integer, Object> findgoodsDQ(Goods goods, String type,
			Integer pageSize, Integer pageNo) {
		if (goods == null) {
			goods = new Goods();
		}
		String hql = totalDao
				.criteriaQueries(goods, null, "ckCount", "ylshifa");
		String hql_other = "";
		if ("CS".equals(type)) {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=60 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and kgliao ='CS' order by goodsnexttime desc";
		} else {
			hql_other = " and goodsCurQuantity>0 and"
					+ "  DATEDIFF(goodsnexttime,CURRENT_DATE)<=15 and goodsClass in( '外协库','外购件库','委外库') "
					+ " and (kgliao is null or kgliao <> 'CS') order by goodsnexttime desc";
		}
		List<Goods> list = totalDao.findAllByPage(hql + hql_other, pageNo,
				pageSize);
		Integer count = totalDao.getCount(hql + hql_other);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, list);
		map.put(2, count);
		return map;
	}

	private String updatMaxSellPrintNumber(Sell sell, String time) {
		Integer year = Integer.parseInt(Util.getDateTime("yyyy"));
		String month = "";
		if (year > 2018) {
			month = Util.getDateTime("yyyyMM");
		}
		String cangqu_hql = "";
		if (sell.getGoodHouseName() != null
				&& sell.getGoodHouseName().length() > 0) {
			cangqu_hql += " and goodHouseName = '" + sell.getGoodHouseName()
					+ "'";
		} else {
			cangqu_hql += " and (goodHouseName is null or goodHouseName = '')";
		}
		String printNumber = (String) totalDao.getObjectByCondition(
				"select printNumber from Sell where orderNum =? and printNumber like 'SOUT"
						+ month + "%' "
						+ " and sellTime =?  and sellCharger =? " + cangqu_hql,
				sell.getOrderNum(), time, sell.getSellCharger());
		String maxprintNumber = (String) totalDao
				.getObjectByCondition(" select max(printNumber) from Sell where printNumber like 'SOUT"
						+ month
						+ "%' and sellWarehouse in ('外购件库','外协库','委外库')");
		String plannumber = "000001";
		PrintedOutOrder printorder = null;
		if (printNumber != null && printNumber.length() > 0) {
			plannumber = printNumber;
			printorder = (PrintedOutOrder) totalDao.getObjectByCondition(
					" from PrintedOutOrder where planNum =? ", plannumber);
		}
		if (printorder != null) {
			Set<PrintedOut> printedOutSet = printorder.getPrintedOutSet();
			if (printedOutSet == null) {
				printedOutSet = new HashSet<PrintedOut>();
			}
			PrintedOut printedOut = new PrintedOut();
			printedOut.setClassName("Sell");
			printedOut.setEntiyId(sell.getSellId());
			printedOut.setYwmarkId(sell.getYwmarkId());
			printedOut.setMarkId(sell.getSellMarkId());
			printedOut.setProNmae(sell.getSellGoods());
			printedOut.setFormat(sell.getSellFormat());
			printedOut.setUnit(sell.getSellUnit());
			printedOut.setNum(sell.getSellCount());
			printedOut.setNeiOrderNum(sell.getOrderNum());
			printedOut.setWaiOrderNum(sell.getOutOrderNumer());
			printedOut.setKubie(sell.getSellWarehouse());
			printedOut.setCangqu(sell.getGoodHouseName());
			printedOut.setSellTime(sell.getSellDate());
			printedOut.setSelfCard(sell.getSellLot());
			printedOut.setTuhao(sell.getTuhao());
			printedOut.setPlanNum(plannumber);
			printedOutSet.add(printedOut);
			printorder.setPrintedOutSet(printedOutSet);
			totalDao.update(printorder);
		} else {
			printorder = new PrintedOutOrder();
			printorder.setPlanNum(plannumber);
			printorder.setAddTime(Util.getDateTime());
			printorder.setKehuNmae(sell.getSellCompanyName());
			printorder.setClassName("Sell");
			printorder.setRiqi(sell.getSellDate());
			printorder.setAddUsers(Util.getLoginUser().getName());
			printorder.setShPlanNum(sell.getSellSendnum());
			printorder.setType("生产领料单");
			Set<PrintedOut> printedOutSet = new HashSet<PrintedOut>();
			PrintedOut printedOut = new PrintedOut();
			printedOut.setClassName("Sell");
			printedOut.setEntiyId(sell.getSellId());
			printedOut.setYwmarkId(sell.getYwmarkId());
			printedOut.setMarkId(sell.getSellMarkId());
			printedOut.setProNmae(sell.getSellGoods());
			printedOut.setFormat(sell.getSellFormat());
			printedOut.setUnit(sell.getSellUnit());
			printedOut.setNum(sell.getSellCount());
			printedOut.setNeiOrderNum(sell.getOrderNum());
			printedOut.setWaiOrderNum(sell.getOutOrderNumer());
			printedOut.setKubie(sell.getSellWarehouse());
			printedOut.setCangqu(sell.getGoodHouseName());
			printedOut.setSellTime(sell.getSellDate());
			printedOut.setSelfCard(sell.getSellLot());
			printedOut.setTuhao(sell.getTuhao());
			maxprintNumber = (String) totalDao
					.getObjectByCondition(" select max(printNumber) from Sell where printNumber like 'SOUT"
							+ month
							+ "%' and sellWarehouse in ('外购件库','外协库','委外库')");
			if (maxprintNumber == null || maxprintNumber.length() == 0) {
				plannumber = "SOUT" + plannumber;
			} else {
				String number = (1000001 + Integer.parseInt(maxprintNumber
						.substring(4)))
						+ "";
				plannumber = "SOUT" + number.substring(1);
			}
			printorder.setPlanNum(plannumber);
			printedOut.setPlanNum(plannumber);
			printedOutSet.add(printedOut);
			printorder.setPrintedOutSet(printedOutSet);
			totalDao.save(printorder);
		}
		sell.setPrintStatus("YES");
		return plannumber;
	}

}