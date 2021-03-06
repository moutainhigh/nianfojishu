package com.task.ServerImpl.ess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.caiwu.core.CorePayableServer;
import com.task.Server.ess.SellServer;
import com.task.Server.sop.ProcardServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.WareHouseAuthServiceImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.ChengPinTuiHuo;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.InternalOrderDetail;
import com.task.entity.OrderManager;
import com.task.entity.Price;
import com.task.entity.PrintedOut;
import com.task.entity.PrintedOutOrder;
import com.task.entity.ProcardBl;
import com.task.entity.ProductManager;
import com.task.entity.Sell;
import com.task.entity.SellDelete;
import com.task.entity.Users;
import com.task.entity.WareHouse;
import com.task.entity.WareHouseAuth;
import com.task.entity.WarehouseNumber;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.ManualOrderPlanTotal;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardMaterial;
import com.task.entity.sop.ProcardMaterialHead;
import com.task.entity.sop.ProcardProductRelation;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessInforWWProcard;
import com.task.entity.sop.RunningWaterCard;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.qd.LogoStickers;
import com.task.entity.system.CircuitRun;
import com.task.util.DateUtil;
import com.task.util.Util;
import com.task.util.UtilTong;
import com.tast.entity.zhaobiao.ZhUser;

@SuppressWarnings("unchecked")
public class SellServerImpl implements SellServer {
	private static Integer duankou = 8885;
	private TotalDao totalDao;
	private ProcardServer procardServer;
	private CorePayableServer corePayableServer;

	public CorePayableServer getCorePayableServer() {
		return corePayableServer;
	}

	public void setCorePayableServer(CorePayableServer corePayableServer) {
		this.corePayableServer = corePayableServer;
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public Map<Integer, Object> findSellOutlist(Procard procard, String ckType,
			String status) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List listGoods = new ArrayList();
		if (null != procard) {
			String phoneSql = "";
			if ("phone".equals(status)) {
				phoneSql = " and goodsId in (select fk_good_id from WareBangGoogs )";
			}
			// 查找到工艺卡片
			if (procard.getHascount() == null) {
				procard.setHascount(procard.getKlNumber());
			}
			if (procard.isZhHasYcl()
					&& ckType.equals("原材料库")
					&& (procard.getGetCount() == null || procard.getYhascount() < procard
							.getGetCount())) {
				procard.setGetCount(procard.getYhascount());
			} else if (procard.isZhHasYcl()
					&& !ckType.equals("原材料库")
					&& (procard.getGetCount() == null || (procard.getHascount() - procard
							.getYhascount()) < procard.getGetCount())) {
				procard.setGetCount(procard.getHascount()
						- procard.getYhascount());
			} else if (procard.getGetCount() == null
					|| procard.getHascount() < procard.getGetCount()) {
				if (procard.getHascount() == null) {
					procard.setGetCount(procard.getKlNumber());
				}
				procard.setGetCount(procard.getHascount());
			}
			String procardSyle = procard.getProcardStyle();
			float ylshifa = 0f;// 余料抵扣实发数量
			if ("自制".equals(procardSyle) || procard.getDanjiaojian() != null
					&& "单交件".equals(procard.getDanjiaojian())) {
				// float count = procard.getKlNumber();// 可领数量

				float count = procard.getGetCount() * procard.getQuanzi2()
						/ procard.getQuanzi1();// 请领数量
				float countfloat = count;
				// 在制品计算

				float zaizhiCount = getZaiZhiCount(procard.getId());// 计算可用在制品
				if (zaizhiCount < 0) {
					// zaizhiCount = 0;// 平仓使用0
					map.put(3, "对不起,之前有批次的在制品不足请先完成之前批次的在制品");
				} else {
				}
				String hqlZaizhi = "from Goods where goodsMarkId='"
						+ procard.getMarkId()
						+ "' and goodsClass='在制品' and goodsCurQuantity>0  and (fcStatus is null or fcStatus='可用')"
						+ phoneSql;
				List listzaizhi = totalDao.query(hqlZaizhi);
				Goods goodszaizhi = null;
				if (null != listzaizhi && listzaizhi.size() > 0) {
					goodszaizhi = (Goods) listzaizhi.get(0);
				}
				float lingliao = zaizhiCount * procard.getQuanzi2()
						/ procard.getQuanzi1();
				if (lingliao >= count && goodszaizhi != null
						&& goodszaizhi.getGoodsCurQuantity() != null) {// 不用发料，在制品够用
					// goodszaizhi.setGoodsCurQuantity(goodszaizhi
					// .getGoodsCurQuantity()
					// - procard.getHascount());
					count = 0;
					// totalDao.update(goodszaizhi);
				} else {//
					float t = count - lingliao;// 需要领料数量
					float ylCount = getYlCount(procard);
					if (ylCount >= t) {
						ylshifa = t;
						t = 0;
					} else {
						ylshifa = ylCount;
						t = t - ylCount;
					}
					countfloat = t;
					// 取整
					Double d = Math.ceil(t);
					count = Float.parseFloat(d.toString());
				}
				// countfloat = count;
				Double d2 = Math.ceil(count);
				count = Float.parseFloat(d2.toString());
				float shifa = 0f;// 实发公斤数
				String shifaUnit = "";
				String cangku = "";
				String goodsName = "";
				String goodsClass = "原材料库";
				// if(procard.getProductStyle()!=null&&procard.getProductStyle().equals("试制")){
				// goodsClass="试制库";
				// }//原材料不去试制库取
				String hqlGoods = " from Goods where goodsMarkId='"
						+ procard.getTrademark()
						+ "' and goodsFormat='"
						+ procard.getSpecification()
						+ "' and goodsClass='"
						+ goodsClass
						+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')"
						+ phoneSql + " order by goodsLotId asc";
				List<Goods> listG = totalDao.find(hqlGoods);
				float lsCount = count;
				float toUseCount = count;
				String goodtype = null;
				String qlUnit = procard.getYuanUnit();
				float ckCount = 0f;
				if (listG.size() > 0) {
					int i = 0;
					Float bizhong = null;
					for (Goods goo : listG) {
						i++;
						if (procard.getYuanUnit().equals(goo.getGoodsUnit())
								&& null != goo.getGoodsZhishu()
								&& goo.getGoodsZhishu() > 0) {// 单位一致有支数
							if (i == 1) {
								bizhong = (Float) totalDao
										.getObjectByCondition(
												"select bili from YuanclAndWaigj where clClass='原材料' and trademark=? and specification=?",
												goo.getGoodsMarkId(), goo
														.getGoodsFormat());
								if (bizhong != null && bizhong > 0) {
									Double d = Math.ceil(countfloat / bizhong);
									lsCount = Float.parseFloat(d.toString());
								}
								count = 0;
							}

							if (bizhong != null && bizhong > 0) {
								if (goo.getGoodsZhishu() >= lsCount) {
									count += lsCount;
									shifa += lsCount * bizhong;
									ckCount = shifa * procard.getQuanzi1()
											/ procard.getQuanzi2();
									ckCount = (float) Math.ceil(ckCount);
									toUseCount = 0;
									shifaUnit = goo.getGoodsUnit();
									qlUnit = "块";
									break;
								} else {
									shifaUnit = goo.getGoodsUnit();
									count += goo.getGoodsZhishu();
									shifa += goo.getGoodsZhishu() * bizhong;
									ckCount = shifa / bizhong
											* procard.getQuanzi1()
											/ procard.getQuanzi2();
									ckCount = (float) Math.ceil(ckCount);
									qlUnit = "块";
								}
							}
						} else if (procard.getYuanUnit().equals(
								goo.getGoodsUnit())
								&& null == goo.getGoodsZhishu()) {// 单位一致按外购件发
							goodtype = "nogl";
							if (goo.getGoodsCurQuantity() >= lsCount) {
								goo.setGoodsCurQuantity(lsCount);
								shifaUnit = goo.getGoodsUnit();
								cangku = goo.getGoodsClass();
								goodsName = goo.getGoodsFullName();
								toUseCount = 0;
								shifa += lsCount;
								ckCount = lsCount * procard.getQuanzi1()
										/ procard.getQuanzi2();
								ckCount = (float) Math.ceil(ckCount);
								// listGoods.add(goo);
								break;
							} else {
								shifaUnit = goo.getGoodsUnit();
								lsCount -= goo.getGoodsCurQuantity();
								shifa += goo.getGoodsCurQuantity();
								ckCount = shifa;
								toUseCount -= goo.getGoodsCurQuantity();
								// listGoods.add(goo);
							}
						} else if (null != goo.getGoodsZhishu()
								&& goo.getGoodsZhishu() > 0) {// 有支数单位
							goodtype = "管料";
							if (i == 1) {
								bizhong = (Float) totalDao
										.getObjectByCondition(
												"select bili from YuanclAndWaigj where clClass='原材料' and trademark=? and specification=?",
												goo.getGoodsMarkId(), goo
														.getGoodsFormat());
							}
							if (bizhong == null || bizhong == 0) {
								bizhong = goo.getGoodsCurQuantity()
										/ goo.getGoodsZhishu();
							}
							if (goo.getGoodsZhishu() >= lsCount) {
								shifa += lsCount * bizhong;
								ckCount += lsCount * procard.getQuanzi1()
										/ procard.getQuanzi2();
								ckCount = (float) Math.ceil(ckCount);
								// goo.setGoodsCurQuantity(lsCount
								// * bizhong);
								// goo.setGoodsZhishu(lsCount);
								shifaUnit = goo.getGoodsUnit();
								cangku = goo.getGoodsClass();
								goodsName = goo.getGoodsFullName();
								toUseCount = 0;
								// listGoods.add(goo);
								break;
							} else {
								// 有支数
								shifa += goo.getGoodsCurQuantity();
								ckCount = shifa / bizhong
										* procard.getQuanzi1()
										/ procard.getQuanzi2();
								ckCount = (float) Math.ceil(ckCount);
								lsCount -= goo.getGoodsZhishu();
								toUseCount -= goo.getGoodsZhishu();
								// goo.setGoodsZhishu(0F);
								// goo.setGoodsCurQuantity(0F);
								shifaUnit = goo.getGoodsUnit();
								// listGoods.add(goo);
							}

						} else {// 无支数，无法计算实发数量
							goodtype = "nogl";
							shifa = 0f;
							shifaUnit = ((Goods) listG.get(0)).getGoodsUnit();
							cangku = ((Goods) listG.get(0)).getGoodsClass();
							goodsName = ((Goods) listG.get(0))
									.getGoodsFullName();
							toUseCount = 0;
							count = (float) Math.ceil(count);
							ckCount += procard.getGetCount();
						}
						if (goo.getGoodsPosition() != null
								&& !"".equals(goo.getGoodsPosition())) {
							String kuwei = goo.getGoodsPosition().replaceAll(
									",", "").replace(" ", "");
							WarehouseNumber wn = (WarehouseNumber) totalDao
									.getObjectByCondition(
											"from WarehouseNumber where number = ? and wareHouseName = ?",
											kuwei, goo.getGoodsClass());
							if (wn != null && wn.getIp() != null
									&& !"".equals(wn.getIp())) {
								goo.setKuweiId(wn.getId());
							}
						}
					}
					if (toUseCount >= 1) {
						count = -1;
					}
				} else {
					if (zaizhiCount < procard.getGetCount()) {
						count = -1;
					}
				}
				Goods goodsZ = new Goods();
				goodsZ.setGoodsMarkId(procard.getTrademark());
				goodsZ.setGoodsFormat(procard.getSpecification());
				goodsZ.setGoodsUnit(shifaUnit);
				goodsZ.setQlUnit(qlUnit);
				goodsZ.setGoodsCurQuantity(Float.parseFloat(String.format(
						"%.2f", shifa)));
				goodsZ.setGoodsZhishu(count);
				goodsZ.setGoodsClass(cangku);
				goodsZ.setGoodsFullName(goodsName);
				goodsZ.setGoodsBeginQuantity(zaizhiCount);
				goodsZ.setGoodstype(goodtype);
				goodsZ.setYlshifa(ylshifa);
				if (!"phone".equals(status)) {
					listGoods.add(goodsZ);
				}

				ckCount = ckCount + zaizhiCount;
				if (ckCount > procard.getHascount()) {
					ckCount = procard.getHascount();
				}
				procard.setCkCount(ckCount);

			} else {// 组合键和总成
				if ("外购".equals(procardSyle)
						&& (procard.getNeedProcess() == null || procard
								.getNeedProcess().equals("no"))) {
					map.put(3, "对不起，该外购件不可领料");
					return map;
				}
				String hql2 = "from Procard where fatherId=? and procardStyle='外购' and (needProcess !='yes' or needProcess is null)";
				String needprocesswgj = "from Procard where id=? and procardStyle='外购' and needProcess ='yes'";
				List list = null;
				List listneedprocesswgj = null;
				if ("外购".equals(procardSyle)) {
					Procard wgj = byProcardid(procard);
					list = new ArrayList();
					list.add(wgj);
				} else {
					if (ckType.contains("外购件库")) {
						// 有外购件权限，并且不需要领原材料或者领的原材料比外购件多
						list = totalDao.query(hql2, procard.getId());
						if (!ckType.contains("原材料库")
								&& procard.isZhHasYcl()
								&& procard.getYhascount() >= procard
										.getHascount()) {
							// 当组合的hascount=yhasCount时，应先去领原材料，但是组合可能是没领齐对应数量的下层外购件
							boolean b = false;
							if (list != null && list.size() > 0) {
								for (int i = 0; i < list.size(); i++) {
									Procard wgj = (Procard) list.get(i);
									if (wgj.getHascount() == null) {
										wgj.setHascount(wgj.getKlNumber());
									}
									Float xdHascount = wgj.getHascount()
											* wgj.getQuanzi1()
											/ wgj.getQuanzi2();
									if (xdHascount > procard.getHascount()) {// 外购件没有配齐领完对应数量
										b = true;
										break;
									}
								}
							}
							if (!b) {
								map.put(3, "对不起，请先领取足够的原材料!目前领取的原材料可生产组合"
										+ (procard.getKlNumber() - procard
												.getYhascount())
										+ procard.getUnit() + "。");
								return map;
							}
						}

					}
					listneedprocesswgj = totalDao.query(needprocesswgj, procard
							.getId());
				}
				float hiddenCount = 0f;
				float qwCount = procard.getGetCount();
				float yclzaizhiCount = 0f;
				Goods goodsYclZaizhi = null;
				if (procard.isZhHasYcl() && ckType.equals("原材料库")) {
					yclzaizhiCount = getYclZaiZhiCount(procard.getId());
					if (yclzaizhiCount < 0) {
						map.put(3, "对不起，之前批次的原材料在制品数不足请先生成之前批次的在制品");
					}
					goodsYclZaizhi = (Goods) totalDao
							.getObjectByCondition(
									"from Goods where goodsMarkId=? and goodsClass='在制品' and goodsFormat = '原材料' and goodsCurQuantity>0  and (fcStatus is null or fcStatus='可用')",
									procard.getMarkId());
					if (goodsYclZaizhi != null) {
						goodsYclZaizhi.setGoodsMarkId(procard.getMarkId());
						goodsYclZaizhi.setGoodsLotId("在制品");
						goodsYclZaizhi.setGoodsFormat("原材料");
						goodsYclZaizhi.setGoodsUnit(procard.getUnit());
						goodsYclZaizhi.setGoodsPosition("在制品");
						goodsYclZaizhi.setGoodsFullName(procard.getProName());
						goodsYclZaizhi.setQlCount(0f);
						goodsYclZaizhi.setGoodsBeginQuantity(yclzaizhiCount);
						if (qwCount <= yclzaizhiCount) {
							goodsYclZaizhi.setGoodsCurQuantity(qwCount);
							hiddenCount = qwCount;
							qwCount = 0;
						} else {
							goodsYclZaizhi.setGoodsCurQuantity(yclzaizhiCount);
							hiddenCount = yclzaizhiCount;
							qwCount = qwCount - yclzaizhiCount;
						}

						if (yclzaizhiCount > 0) {
							listGoods.add(goodsYclZaizhi);
						}
					}
				}
				float zaizhiCount = 0f;
				if (qwCount != 0 && !procard.getProcardStyle().equals("总成")) {// 不为0时计算在制品，总成不算在制品
					zaizhiCount = getZaiZhiCount(procard.getId());// 计算可用在制品
					Goods goodsZ = new Goods();
					goodsZ.setGoodsId(0);// 外购件领取部分时当在制品足够时不会因为selected为空而被判断没有点击一个确定
					goodsZ.setGoodsMarkId(procard.getMarkId());
					goodsZ.setGoodsLotId("在制品");
					goodsZ.setGoodsFormat("成品");
					goodsZ.setGoodsUnit(procard.getUnit());
					goodsZ.setGoodsPosition("在制品");
					goodsZ.setGoodsFullName(procard.getProName());
					goodsZ.setQlCount(0f);
					goodsZ.setGoodsBeginQuantity(zaizhiCount);
					if (zaizhiCount < 0) {
						map.put(3, "对不起，之前批次的在制品数不足请先生成之前批次的在制品");
					} else {
						if (qwCount <= zaizhiCount) {
							goodsZ.setGoodsCurQuantity(qwCount);
							hiddenCount += qwCount;
							qwCount = 0;
						} else {
							goodsZ.setGoodsCurQuantity(zaizhiCount);
							hiddenCount += zaizhiCount;
							qwCount = qwCount - zaizhiCount;
						}

					}
					if (zaizhiCount > 0) {
						listGoods.add(goodsZ);
					}
				}
				// 存在外购件，判断库存是否充足
				float ckCount = qwCount;// 组合或总成仓库可领数量
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						float ckCount1 = 0f;// 组合或总成仓库可领数量，用来与ckCount比较将较小的数据放入ckCount中
						Procard p = (Procard) list.get(i);
						float ckCount2 = 0f;// 本张卡片仓库剩余数量
						// float count = pro.getKlNumber();//上层父类可领取的数量
						if (p.getMarkId().equals("21100041")) {
							System.out.println(p.getMarkId());
						}
						float count = 0f;
						if (p.getNeedProcess() != null
								&& p.getNeedProcess().equals("yes")) {
							count = qwCount;
						} else {
							count = qwCount * p.getQuanzi2() / p.getQuanzi1();// 下层可领取的数量
						}
						float chaling = p.getHascount() - procard.getHascount()
								* p.getQuanzi2() / p.getQuanzi1();// 差领数量之前领料时未被配套领取的数量
						float countTem = count;
						count += chaling;

						String goodsClass = "外购件库','中间库";
						String goodsClassSql = null;
						// if (procard.getProductStyle() != null
						// && procard.getProductStyle().equals("试制")) {
						// goodsClass = "试制库";// 试制的外购件去试制库取
						// goodsClassSql = " and goodsClass ='" + goodsClass
						// + "'";
						// } else {
						String kgsql = " and 1=1";
						if (procard.getKgliao() != null
								&& procard.getKgliao().length() > 0) {
							kgsql += " and kgliao ='" + procard.getKgliao()
									+ "'";
						}
						goodsClassSql = " and ((goodsClass in ('" + goodsClass
								+ "') " + kgsql + " ) or goodsClass = '备货库')";
						// }

						String hqlGoods = " from Goods where goodsMarkId='"
								+ p.getMarkId()
								+ "'"
								+ goodsClassSql
								+ " and goodsCurQuantity>0  and (fcStatus is null or fcStatus='可用') "
								+ phoneSql + "order by goodsLotId asc";
						List<Goods> listG = totalDao.find(hqlGoods);
						// Double d2 = Math.ceil(count);
						// count = Float.parseFloat(d2.toString());
						if (listG.size() > 0) {
							List listGoodsTemp = new ArrayList();
							for (Goods goo : listG) {
								goo.setGoodsBeginQuantity(0f);
								if (goo.getGoodsCurQuantity() >= count) {
									goo.setGoodsCurQuantity(count);
									ckCount2 += count;
									count = 0;
									listGoodsTemp.add(goo);
									break;
								} else {
									ckCount2 += goo.getGoodsCurQuantity();
									goo.setCkCount(goo.getGoodsCurQuantity());
									count -= goo.getGoodsCurQuantity();
									listGoodsTemp.add(goo);
								}
							}
							if (count > 0) {// 库存不够
								Goods gd = listG.get(0);
								gd.setGoodsCurQuantity(-1f);
								listGoods.add(gd);
							} else {
								listGoods.addAll(listGoodsTemp);
							}
						} else {
							String hqlGoods2 = " from Goods where goodsMarkId='"
									+ p.getMarkId()
									+ "'"
									+ goodsClassSql
									+ " and goodsCurQuantity=0 and (fcStatus is null or fcStatus='可用')"
									+ phoneSql + " order by goodsLotId asc";
							List<Goods> listG2 = totalDao.find(hqlGoods2);
							if (listG2.size() > 0) {
								listGoods.add(listG2.get(0));
							} else {
								Goods goo = new Goods();
								goo.setGoodsFullName(p.getProName());
								goo.setGoodsMarkId(p.getMarkId());
								goo.setGoodsId(0);
								goo.setGoodsCurQuantity(-1f);
								goo.setGoodsUnit(p.getUnit());
								if (!"phone".equals(status)) {
									listGoods.add(goo);
								}

							}
						}
						if (p.getNeedProcess() != null
								&& p.getNeedProcess().equals("yes")) {
							ckCount1 = ckCount2;
						} else {
							if (ckCount2 > countTem) {// 剔除差领数量
								ckCount1 = (float) Math.floor(countTem
										* p.getQuanzi1() / p.getQuanzi2());
							} else {
								ckCount1 = (float) Math.floor(ckCount2
										* p.getQuanzi1() / p.getQuanzi2());
							}
						}
						if (ckCount1 < 0) {
							ckCount1 = 0f;
						}
						if (ckCount1 > ckCount) {
							ckCount = ckCount1;
						}
					}
				}
				if (procard.getYhascount() != null
						&& zaizhiCount < procard.getYhascount()
						&& procard.isZhHasYcl() && ckType.contains("原材料库")
						&& qwCount > 0) {// 组合其下有原材料
					float shifa = 0f;// 实发公斤数
					String shifaUnit = "";
					String cangku = "";
					String goodsName = "";
					String goodsClass = "原材料库";
					// if(procard.getProductStyle()!=null&&procard.getProductStyle().equals("试制")){
					// goodsClass="试制库";
					// }//原材料不去试制库取
					String hqlGoods = " from Goods where goodsMarkId='"
							+ procard.getTrademark()
							+ "' and goodsFormat='"
							+ procard.getSpecification()
							+ "' and goodsClass='"
							+ goodsClass
							+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')"
							+ phoneSql + " order by goodsLotId asc";
					List<Goods> listG = totalDao.find(hqlGoods);
					float ylCount = getYlCount(procard);
					float lsCount = 0f;
					// float toUseCount = 0f;
					float yclCount = qwCount;
					String qlUnit = procard.getYuanUnit();
					if (ylCount >= qwCount) {
						ylshifa = qwCount;
					} else {
						ylshifa = ylCount;
						// toUseCount = qwCount - ylCount;
						lsCount = qwCount - ylCount;
					}
					// float lsCount = qwCount;
					// float toUseCount = qwCount;
					String goodtype = null;
					if (listG.size() > 0) {
						int i = 0;
						Float bizhong = null;
						for (Goods goo : listG) {
							i++;
							if (procard.getYuanUnit()
									.equals(goo.getGoodsUnit())
									&& null != goo.getGoodsZhishu()
									&& goo.getGoodsZhishu() > 0) {// 单位一致有支数
								if (i == 1) {
									bizhong = (Float) totalDao
											.getObjectByCondition(
													"select bili from YuanclAndWaigj where clClass='原材料' and trademark=? and specification=?",
													goo.getGoodsMarkId(), goo
															.getGoodsFormat());
									if (bizhong != null && bizhong > 0) {
										Double d = Math
												.ceil(lsCount
														* procard.getQuanzi2()
														/ (procard.getQuanzi1() * bizhong));
										lsCount = Float
												.parseFloat(d.toString());
									}
									yclCount = 0;
								}
								if (bizhong != null && bizhong > 0) {
									if (goo.getGoodsZhishu() >= lsCount) {
										shifaUnit = goo.getGoodsUnit();
										yclCount += lsCount;
										shifa += yclCount * bizhong;
										// toUseCount = 0;
										qlUnit = "块";
										lsCount = 0;
										break;
									} else {
										shifaUnit = goo.getGoodsUnit();
										yclCount += goo.getGoodsZhishu();
										shifa += goo.getGoodsZhishu() * bizhong;
										lsCount = lsCount
												- goo.getGoodsZhishu();
										qlUnit = "块";
									}
								}
							} else if (procard.getYuanUnit().equals(
									goo.getGoodsUnit())
									&& (null == goo.getGoodsZhishu() || 0 == goo
											.getGoodsZhishu())) {// 单位一致按外购件发
								goodtype = "nogl";
								if (goo.getGoodsCurQuantity() >= lsCount) {
									goo.setGoodsCurQuantity(lsCount);
									shifaUnit = goo.getGoodsUnit();
									cangku = goo.getGoodsClass();
									goodsName = goo.getGoodsFullName();
									// toUseCount = 0;
									// listGoods.add(goo);
									shifa = lsCount;
									lsCount = 0;
									break;
								} else {
									shifaUnit = goo.getGoodsUnit();
									lsCount -= goo.getGoodsCurQuantity();
									// toUseCount -= goo.getGoodsCurQuantity();
									// listGoods.add(goo);
								}
								shifa = lsCount;
							} else if (null != goo.getGoodsZhishu()
									&& goo.getGoodsZhishu() > 0) {// 有支数单位
								if (i == 1) {
									bizhong = (Float) totalDao
											.getObjectByCondition(
													"select bili from YuanclAndWaigj where  trademark=? and specification=?",
													goo.getGoodsMarkId(), goo
															.getGoodsFormat());
									if (bizhong != null && bizhong > 0) {
										Double d = Math
												.ceil(lsCount
														* procard.getQuanzi2()
														/ (procard.getQuanzi1() * bizhong));
										lsCount = Float
												.parseFloat(d.toString());
										shifaUnit = goo.getGoodsUnit();
									} else {
										Double d = Math.ceil(lsCount
												* procard.getQuanzi2()
												/ procard.getQuanzi1());
										lsCount = Float
												.parseFloat(d.toString());
										shifaUnit = goo.getGoodsUnit();
									}
									yclCount = 0;
								}
								goodtype = "管料";
								if (goo.getGoodsZhishu() >= lsCount) {
									if (bizhong == null || bizhong == 0) {
										bizhong = goo.getGoodsCurQuantity()
												/ goo.getGoodsZhishu();
									}
									shifa += lsCount * bizhong;
									// goo.setGoodsCurQuantity(lsCount
									// * goo.getGoodsCurQuantity()
									// / goo.getGoodsZhishu());
									// goo.setGoodsZhishu(lsCount);
									shifaUnit = goo.getGoodsUnit();
									cangku = goo.getGoodsClass();
									goodsName = goo.getGoodsFullName();
									yclCount += lsCount;
									// toUseCount = 0;
									lsCount = 0;
									// listGoods.add(goo);
									break;
								} else {
									shifa += goo.getGoodsCurQuantity();
									lsCount -= goo.getGoodsZhishu();
									yclCount += goo.getGoodsZhishu();
									// toUseCount -= goo.getGoodsZhishu();
									// goo.setGoodsZhishu(0F);
									// goo.setGoodsCurQuantity(0F);
									shifaUnit = goo.getGoodsUnit();
									// listGoods.add(goo);
								}
							} else {// 无支数，无法计算实发数量
								shifaUnit = goo.getGoodsUnit();
								goodtype = "nogl";
								shifa = 0f;
								shifaUnit = ((Goods) listG.get(0))
										.getGoodsUnit();
								cangku = ((Goods) listG.get(0)).getGoodsClass();
								goodsName = ((Goods) listG.get(0))
										.getGoodsFullName();
								// toUseCount = 0;
							}
							if (goo.getGoodsPosition() != null
									&& !"".equals(goo.getGoodsPosition())) {
								String kuwei = goo.getGoodsPosition()
										.replaceAll(",", "").replace(" ", "");
								WarehouseNumber wn = (WarehouseNumber) totalDao
										.getObjectByCondition(
												"from WarehouseNumber where number = ? and wareHouseName = ?",
												kuwei, goo.getGoodsClass());
								if (wn != null && wn.getIp() != null
										&& !"".equals(wn.getIp())) {
									goo.setKuweiId(wn.getId());
								}
							}
						}
						if (bizhong == null || bizhong == 0) {
							bizhong = 1f;
						}
						float yclckCount = (yclCount - lsCount) * bizhong
								* procard.getQuanzi1() / procard.getQuanzi2();
						Double d3 = Math.floor(yclckCount);
						yclckCount = Float.parseFloat(d3.toString());
						if (yclckCount < ckCount) {
							ckCount = yclckCount;
						}
					} else {
						// if ((yclCount - toUseCount) < ckCount) {
						ckCount = 0f;
						// }
					}
					Goods goodsZ = new Goods();
					goodsZ.setGoodsMarkId(procard.getTrademark());
					goodsZ.setGoodsFormat(procard.getSpecification());
					goodsZ.setGoodsUnit(shifaUnit);
					goodsZ.setGoodsCurQuantity(shifa);
					goodsZ.setGoodsZhishu(yclCount);
					goodsZ.setGoodsClass(cangku);
					if (goodsName == null) {
						goodsName = procard.getYuanName();
						if (goodsName == null) {
							goodsName = "";
						}
					}
					goodsZ.setGoodsFullName(goodsName);
					goodsZ.setGoodsBeginQuantity(0f);
					goodsZ.setGoodstype(goodtype);
					goodsZ.setYlshifa(ylshifa);
					goodsZ.setQlUnit(qlUnit);
					goodsZ.setGoodsClass("原材料库");
					if (!"phone".equals(status)) {
						listGoods.add(goodsZ);
					}

				}
				if (listneedprocesswgj != null) {
					for (int i = 0; i < listneedprocesswgj.size(); i++) {
						float ckCount1 = 0f;// 组合或总成仓库可领数量，用来与ckCount比较将较小的数据放入ckCount中
						Procard p = (Procard) listneedprocesswgj.get(i);
						float ckCount2 = 0f;// 本张卡片已提交数量
						if (p.getTjNumber() != null) {
							ckCount2 = p.getTjNumber();
						}
						ckCount1 = (float) Math.floor(ckCount2 * p.getQuanzi2()
								/ p.getQuanzi1());
						if (ckCount1 < 0) {
							ckCount1 = 0f;
						}
						if (ckCount1 < ckCount) {
							ckCount = ckCount1;
						}
					}

				}
				procard.setCkCount(ckCount + hiddenCount);
			}
		}
		map.put(1, procard);
		map.put(2, listGoods);
		return map;
	}

	private float getYlCount(Procard procard) {
		// TODO Auto-generated method stub
		Float goodsCurQuantity = (Float) totalDao
				.getObjectByCondition(
						"select sum(goodsCurQuantity) from Goods where goodsClass='余料库' and goodsCurQuantity>0 and goodsMarkId=? and goodsFormat=? "
								+ "and (yllock is null or yllock='' or yllock='no' or (yllock = 'yes' and ylMarkId=?) and (fcStatus is null or fcStatus='可用'))",
						procard.getTrademark(), procard.getSpecification(),
						procard.getMarkId());
		if (goodsCurQuantity != null) {
			return goodsCurQuantity;
		}
		return 0;
	}

	@Override
	public Map<Integer, Object> findSellOutlist2(Procard procard,
			String ckType, String status) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List listGoods = new ArrayList();
		List listGoodsTmp = new ArrayList();
		if (null != procard) {
			// 查找到工艺卡片
			String phoneSql = "";
			if ("phone".equals(status)) {
				phoneSql = " and goodsId in (select fk_good_id from WareBangGoogs)";
			}
			if (procard.getHascount() == null) {
				procard.setHascount(procard.getKlNumber());
			}
			String procardSyle = procard.getProcardStyle();
			String hql2 = "from Procard where fatherId=? and procardStyle='外购' and (needProcess !='yes' or needProcess is null)";
			List list = null;
			List listneedprocesswgj = null;
			// 最大的已领外购件对应组合或总成数量
			Float maxYlCount = 0f;
			// 存在外购件，判断库存是否充足（取最大值）
			float ckCount = 0f;// 组合或总成仓库可领数量
			float qwCount = 0f;
			if (procard.getGetCount() != null) {
				qwCount = procard.getGetCount() < procard.getHascount() ? procard
						.getGetCount()
						: procard.getHascount();// 期望领取外购件的组合或总成数量

			} else {
				qwCount = procard.getHascount();
				procard.setGetCount(procard.getHascount());
			}
			// float ckCount = procard.getHascount();// 组合或总成仓库可领数量
			if ("外购".equals(procardSyle)) {
				Procard wgj = byProcardid(procard);
				list = new ArrayList();
				list.add(wgj);
			} else {
				list = totalDao.query(hql2, procard.getId());
				// 查找对应组合已领最大数量的外购件
				Procard maxProcard = (Procard) totalDao
						.getObjectByCondition(
								"from Procard where procard.id=? and hascount is not null and procardStyle='外购' and (needProcess <> 'yes' or needProcess is null) order by hascount",
								procard.getId());
				if (maxProcard != null && maxProcard.getFilnalCount() != null) {
					maxYlCount = (maxProcard.getFilnalCount() - maxProcard
							.getHascount())
							* maxProcard.getQuanzi1() / maxProcard.getQuanzi2();
				}
			}
			float zaizhiCount = 0f;
			float hiddenCount = 0f;
			if (!procard.getProcardStyle().equals("总成")) {// 总成不算在制品
				zaizhiCount = getZaiZhiCount(procard.getId());// 计算可用在制品
				Goods goodsZ = new Goods();
				goodsZ.setGoodsMarkId(procard.getMarkId());
				goodsZ.setGoodsLotId("在制品");
				goodsZ.setGoodsFormat("");
				goodsZ.setGoodsUnit(procard.getUnit());
				goodsZ.setGoodsPosition("在制品");
				goodsZ.setGoodsFullName(procard.getProName());
				goodsZ.setQlCount(0f);
				goodsZ.setGoodsBeginQuantity(zaizhiCount);
				if (zaizhiCount < 0) {
					map.put(3, "对不起，之前批次的在制品数不足请先生成之前批次的在制品");
				} else {
					if (qwCount <= zaizhiCount) {
						goodsZ.setGoodsCurQuantity(qwCount);
						hiddenCount = qwCount;
						qwCount = 0;
					} else {
						goodsZ.setGoodsCurQuantity(zaizhiCount);
						qwCount = qwCount - zaizhiCount;
						hiddenCount = zaizhiCount;
					}

				}
				if (zaizhiCount > 0) {
					listGoods.add(goodsZ);
					listGoodsTmp.add(goodsZ);
				}
			}
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					float ckCount1 = 0f;// 组合或总成仓库可领数量，用来与ckCount比较将较小的数据放入ckCount中
					float qlCount = 0f;// 缺领数量（在组合领料时没有被领取的外购件的数量）
					float qlCountzh = 0f;// 缺领数量（在组合领料时没有被领取的外购件对应组合或总成的数量）
					Procard p = (Procard) list.get(i);
					if (p.getHascount() != null) {
						qlCountzh = maxYlCount
								- ((p.getFilnalCount() - p.getHascount())
										* p.getQuanzi1() / p.getQuanzi2());
					}
					qlCount = qlCountzh * p.getQuanzi2() / p.getQuanzi1();
					float ckCount2 = 0f;// 本张卡片仓库剩余数量
					// float count = pro.getKlNumber();//上层父类可领取的数量
					float count = qwCount * p.getQuanzi2() / p.getQuanzi1();// 下层可领取的数量
					count = count + qlCount;// 加上之前缺领的数量
					String goodsClass = "外购件库','中间库','备货库";
					// if (procard.getProductStyle() != null
					// && procard.getProductStyle().equals("试制")) {
					// goodsClass = "试制库";// 试制的外购件去试制库取
					// }
					String hqlGoods = " from Goods where goodsMarkId='"
							+ p.getMarkId()
							+ "' and goodsClass in ('"
							+ goodsClass
							+ "') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')"
							+ phoneSql + " order by goodsLotId asc";
					String ckAllCounthql = "select sum(goodsCurQuantity) from Goods where goodsMarkId='"
							+ p.getMarkId()
							+ "' and goodsClass in ('"
							+ goodsClass
							+ "') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')"
							+ phoneSql;
					List<Goods> listG = totalDao.find(hqlGoods);
					Double d2 = Math.ceil(count);
					count = Float.parseFloat(d2.toString());
					if (listG.size() > 0) {
						Float ckAllCount = (Float) totalDao
								.getObjectByCondition(ckAllCounthql);
						if (ckAllCount == null) {
							ckAllCount = 0f;
						}
						if (count > ckAllCount) {
							// Float count2 = ckAllCount * p.getQuanzi1()
							// / p.getQuanzi2();
							// count2 = (float) Math.floor(count2);
							qlCount = count - ckAllCount;
						} else {
							qlCount = 0f;
						}
						List listGoodsTemp = new ArrayList();
						for (Goods goo : listG) {
							goo.setGoodsBeginQuantity(0f);
							goo.setQlCount(qlCount);
							if (goo.getGoodsPosition() != null
									&& !"".equals(goo.getGoodsPosition())) {
								String kuwei = goo.getGoodsPosition()
										.replaceAll(",", "").replace(" ", "");
								WarehouseNumber wn = (WarehouseNumber) totalDao
										.getObjectByCondition(
												"from WarehouseNumber where number = ? and wareHouseName = ?",
												kuwei, goo.getGoodsClass());
								if (wn != null && wn.getIp() != null
										&& !"".equals(wn.getIp())) {
									goo.setKuweiId(wn.getId());
								}
							}
							// goo.setQlCountZh(qlCountzh);
							if (goo.getGoodsCurQuantity() >= count) {
								goo.setGoodsCurQuantity(count);
								ckCount2 += count;
								count = 0;
								listGoodsTemp.add(goo);
								break;
							} else {
								ckCount2 += goo.getGoodsCurQuantity();
								goo.setCkCount(goo.getGoodsCurQuantity());
								count -= goo.getGoodsCurQuantity();
								listGoodsTemp.add(goo);
							}
						}
						// if (count > 0) {// 库存不够
						// Goods gd = listG.get(0);
						// gd.setGoodsCurQuantity(-1f);
						// listGoods.add(gd);
						// } else {
						listGoods.addAll(listGoodsTemp);
						// }
					} else {
						String hqlGoods2 = " from Goods where goodsMarkId='"
								+ p.getMarkId()
								+ "' and goodsClass in ('"
								+ goodsClass
								+ "') and goodsCurQuantity=0 and (fcStatus is null or fcStatus='可用') "
								+ phoneSql + "order by goodsLotId asc";
						List<Goods> listG2 = totalDao.find(hqlGoods2);
						if (listG2.size() > 0) {
							Goods goo = listG2.get(0);
							if (goo.getGoodsPosition() != null
									&& !"".equals(goo.getGoodsPosition())) {
								String kuwei = goo.getGoodsPosition()
										.replaceAll(",", "").replace(" ", "");
								WarehouseNumber wn = (WarehouseNumber) totalDao
										.getObjectByCondition(
												"from WarehouseNumber where kuwei = ? and goodsClass = ?",
												kuwei, goo.getGoodsClass());
								if (wn != null && wn.getIp() != null
										&& !"".equals(wn.getIp())) {
									goo.setKuweiId(wn.getId());
								}
							}
							goo.setQlCount(count);
							goo.setGoodsBeginQuantity(0f);
							listGoods.add(goo);
							listGoodsTmp.add(goo);
						} else {
							Goods goo = new Goods();
							goo.setGoodsMarkId(p.getMarkId());
							goo.setGoodsId(0);
							goo.setQlCount(count);
							goo.setGoodsCurQuantity(0f);
							goo.setGoodsUnit(p.getUnit());
							if (!"phone".equals(status)) {
								listGoods.add(goo);
								listGoodsTmp.add(goo);
							}

						}
					}
					ckCount1 = (float) Math.floor(ckCount2 * p.getQuanzi1()
							/ p.getQuanzi2());
					if (ckCount1 < 0) {
						ckCount1 = 0f;
					}
					if (ckCount1 > ckCount) {
						ckCount = ckCount1;
					}
				}
			}
			if (listneedprocesswgj != null) {
				for (int i = 0; i < listneedprocesswgj.size(); i++) {
					float ckCount1 = 0f;// 外购件半成品可领数量，用来与ckCount比较将较小的数据放入ckCount中
					Procard p = (Procard) listneedprocesswgj.get(i);
					float ckCount2 = 0f;// 本张卡片已提交数量
					if (p.getTjNumber() != null) {
						ckCount2 = p.getTjNumber();
					}
					ckCount1 = (float) Math.floor(ckCount2);
					if (ckCount1 < 0) {
						ckCount1 = 0f;
					}
					if (ckCount1 > ckCount) {
						ckCount = ckCount1;
					}
				}

			}
			procard.setCkCount(ckCount + hiddenCount);
			if (procard.getGetCount() != null
					&& procard.getCkCount() > procard.getGetCount()) {
				procard.setCkCount(procard.getGetCount());
			}
			if (ckCount == 0) {
				listGoods.clear();
				listGoods.addAll(listGoodsTmp);
			}
		}
		map.put(1, procard);
		map.put(2, listGoods);
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * 自制件领料
	 */
	public List saveSellListZZ(Integer id, Procard pro, List<Goods> li,
			String tag, float qingling, float shifa, float getCount) {
		/***
		 * TODO //根据卡号查选
		 */
		String hql1 = "from Users where cardId=? and onWork not in('离职','离职中','内退','病休')";
		Users us = (Users) totalDao.getObjectByCondition(hql1, pro
				.getLingliaoren());// 领料人
		if (us == null) {
			throw new RuntimeException("领料人不存在,领料失败!");
		} else {
			Float czcount = (Float) totalDao
					.getObjectByCondition(
							"select count(id) from ProcessinforPeople where procard.id=? and userId =?",
							pro.getId(), us.getId());
			if (czcount == null || czcount == 0) {
				throw new RuntimeException("领料人对此零件无领料权限,领料失败!");
			}
		}
		List listSell = new ArrayList();
		StringBuffer lingliaoDetailProSb = new StringBuffer();
		if (li.size() > 0) {
			Users user = (Users) Util.getLoginUser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			Goods goo = li.get(0);
			// 在制品计算
			if (qingling > 0) {// 有发料
				String hqlzaizhi = "from Goods where goodsMarkId='"
						+ pro.getMarkId()
						+ "' and goodsLotId='"
						+ pro.getSelfCard()
						+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用')";
				List listzizhi = totalDao.find(hqlzaizhi);
				Goods goodsu = li.get(0);
				double addzaizhi = 0;
				if (goodsu.getGoodsUnit().equals(pro.getYuanUnit())) {
					Float bizhong = bizhong = (Float) totalDao
							.getObjectByCondition(
									"select bili from YuanclAndWaigj where clClass='原材料' and trademark=? and specification=?",
									pro.getTrademark(), pro.getSpecification());
					if (bizhong == null || bizhong == 0) {
						bizhong = 1f;
					}
					addzaizhi = qingling * bizhong * pro.getQuanzi1()
							/ pro.getQuanzi2();
				} else {
					addzaizhi = qingling * pro.getQuanzi1() / pro.getQuanzi2();
				}
				if (listzizhi != null && listzizhi.size() > 0) {
					Goods g1 = (Goods) listzizhi.get(0);
					g1.setGoodsCurQuantity(g1.getGoodsCurQuantity()
							+ (float) Math.floor(addzaizhi));
					if (g1.getGoodsCurQuantity() < 0) {
						sendMessage(g1);
						g1.setGoodsCurQuantity(0f);
					}
					totalDao.update(g1);
				} else {
					Goods gg = new Goods();
					gg.setGoodsMarkId(pro.getMarkId());
					gg.setGoodsLotId(pro.getSelfCard());
					gg.setGoodsFullName(pro.getProName());
					gg.setGoodsClass("在制品");
					gg.setGoodsCurQuantity((float) Math.floor(addzaizhi));
					if (gg.getGoodsCurQuantity() < 0) {
						sendMessage(gg);
						gg.setGoodsCurQuantity(0f);
					}
					gg.setGoodsUnit(pro.getUnit());
					totalDao.save(gg);

				}
				if (addzaizhi != 0) {// 添加在制品入库记录
					GoodsStore gs = new GoodsStore();
					gs.setGoodsStoreMarkId(pro.getMarkId());// 件号
					gs.setGoodsStoreGoodsName(pro.getProName());// 名称
					gs.setGoodsStoreLot(pro.getSelfCard());// 批次
					gs.setGoodsStoreCount((float) Math.floor(addzaizhi));// 数量
					gs.setPrintStatus("YES");
					List<String> totalMarkId = totalDao.query(
							"select markId from Procard where id=?", pro
									.getRootId());
					if (totalMarkId.size() > 0) {
						gs.setGoodsStoreProMarkId(totalMarkId.get(0));// 总成件号
					}
					gs.setGoodsStoreWarehouse("在制品");// 库别
					Users jingban = Util.getLoginUser();
					gs.setGoodsStoreCharger(jingban.getName());// 经办人
					gs.setStyle("正常（成品）");// 入库类型
					gs.setGoodsStorePerson(us.getName());
					gs.setGoodsStoreDate(DateUtil.formatDate(new Date(),
							"yyyy-MM-dd"));
					gs.setGoodsStoreUnit(pro.getUnit());// 单位
					totalDao.save(gs);

				}
			} else {// 无发料
				// String hqlzaizhi = "from Goods where goodsMarkId='"
				// + pro.getMarkId()
				// +
				// "' and goodsClass='在制品' and goodsLotId='在制品' and goodsLotId='在制品'";
				// List listzizhi = totalDao.find(hqlzaizhi);
				// if (listzizhi != null && listzizhi.size() > 0) {
				// Goods g1 = (Goods) listzizhi.get(0);
				// g1.setGoodsCurQuantity(g1.getGoodsCurQuantity() - getCount);
				// totalDao.update(g1);
				// }

			}
			// 余料抵发
			if (goo.getYlshifa() > 0) {
				float ylshifa = goo.getYlshifa();
				float ckyl = getYlCount(pro);
				if (ckyl < ylshifa) {
					throw new RuntimeException("对不起余料抵发数量不足,领料失败!");
				}
				List<Goods> ylGoodsList = (List<Goods>) totalDao
						.query(
								"from Goods where goodsClass='余料库' and goodsCurQuantity>0 and goodsMarkId=? and goodsFormat=? "
										+ "and (yllock is null or yllock='' or yllock='no' or (yllock = 'yes' and ylMarkId=?)) and (fcStatus is null or fcStatus='可用')",
								pro.getTrademark(), pro.getSpecification(), pro
										.getMarkId());
				for (Goods ckylg : ylGoodsList) {
					if (ckylg.getGoodsCurQuantity() >= ylshifa) {
						// 余料数量修改
						ckylg.setGoodsCurQuantity(ckylg.getGoodsCurQuantity()
								- ylshifa);
						if (ckylg.getGoodsCurQuantity() < 0) {
							sendMessage(ckylg);
							ckylg.setGoodsCurQuantity(0f);
						}
						totalDao.update(ckylg);
						// 余料出库
						Sell sellYl = new Sell();
						sellYl.setSellArtsCard(pro.getSelfCard());
						sellYl.setSellSupplier(ckylg.getGoodsSupplier());
						sellYl.setSellFormat(ckylg.getGoodsFormat());
						sellYl.setSellLot(ckylg.getGoodsLotId());
						sellYl.setSellMarkId(ckylg.getGoodsMarkId());
						sellYl.setSellAdminName(user.getName());
						sellYl.setSellGoods(ckylg.getGoodsFullName());
						sellYl.setSellDate(sdf2.format(new Date()));
						sellYl.setSellTime(sdf.format(new Date()));
						sellYl.setSellWarehouse(ckylg.getGoodsClass());
						sellYl.setSellUnit(ckylg.getGoodsUnit());
						sellYl.setSellCount(ylshifa);
						ylshifa = 0;
						totalDao.save(sellYl);
						break;
					} else {
						// 余料出库
						Sell sellYl = new Sell();
						sellYl.setSellArtsCard(pro.getSelfCard());
						sellYl.setSellSupplier(ckylg.getGoodsSupplier());
						sellYl.setSellFormat(ckylg.getGoodsFormat());
						sellYl.setSellLot(ckylg.getGoodsLotId());
						sellYl.setSellMarkId(ckylg.getGoodsMarkId());
						sellYl.setSellAdminName(user.getName());
						sellYl.setSellGoods(ckylg.getGoodsFullName());
						sellYl.setSellDate(sdf2.format(new Date()));
						sellYl.setSellTime(sdf.format(new Date()));
						sellYl.setSellWarehouse(ckylg.getGoodsClass());
						sellYl.setSellUnit(ckylg.getGoodsUnit());
						sellYl.setSellCount(ckylg.getGoodsCurQuantity());
						totalDao.save(sellYl);
						// 余料数量修改
						ylshifa = ylshifa - ckylg.getGoodsCurQuantity();
						ckylg.setGoodsCurQuantity(0f);
						totalDao.update(ckylg);
					}
				}

			}
			String goodsClass = "原材料库";
			// if(pro.getProductStyle()!=null&&
			// pro.getProductStyle().equals("试制")){
			// goodsClass="试制库";
			// }//原材料还是在原材料库里取
			String hql = "from Goods where goodsMarkId='"
					+ pro.getTrademark()
					+ "' and goodsFormat='"
					+ pro.getSpecification()
					+ "' and goodsClass='"
					+ goodsClass
					+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用') order by goodsChangeTime asc";
			List<Goods> listG = totalDao.find(hql);
			if (listG.size() > 0 || qingling == 0) {
				if ((qingling > 0 || shifa > 0) && listG.size() > 0) {// 需要原材料
					for (Goods g : listG) {
						Sell sell = new Sell();
						sell.setSellArtsCard(pro.getSelfCard());
						sell.setSellSupplier(g.getGoodsSupplier());
						sell.setSellFormat(g.getGoodsFormat());
						sell.setSellLot(g.getGoodsLotId());
						sell.setSellMarkId(g.getGoodsMarkId());
						sell.setSellAdminName(user.getName());
						sell.setSellGoods(g.getGoodsFullName());
						sell.setSellDate(sdf2.format(new Date()));
						sell.setSellTime(sdf.format(new Date()));
						sell.setSellWarehouse(g.getGoodsClass());
						sell.setSellUnit(g.getGoodsUnit());

						if (us != null) {
							sell.setSellCharger(us.getName());
						} else {
							sell.setSellCharger(pro.getLingliaoren());

						}
						// --------------------------------
						if ("总成".equals(pro.getProcardStyle())) {
							sell.setSellProMarkId(pro.getMarkId());
						} else {
							int prorootId = pro.getRootId();
							sell.setSellProMarkId((byProcardid(prorootId))
									.getMarkId());
						}

						sell.setPrintStatus("NO");
						sell.setStyle("刷卡出库");
						sell.setSellPeople("待确认");
						sell.setSellAdminName(user.getName());
						String allzhishuCountHql = "select sum(goodsZhishu) from Goods where goodsMarkId='"
								+ pro.getTrademark()
								+ "' and goodsFormat='"
								+ pro.getSpecification()
								+ "' and goodsClass='"
								+ goodsClass
								+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
						Float allzhishuCount = 0f;
						List<Float> allzhishuCountList = totalDao
								.query(allzhishuCountHql);
						if (allzhishuCountList.size() > 0) {
							allzhishuCount = allzhishuCountList.get(0);
						}
						if (pro.getYuanUnit().equals(g.getGoodsUnit())
								&& allzhishuCount == 0) {// 单位一致并且支数为0
							if (shifa > qingling) {// 实发大于请领多余的以余料入库
								Goods ylGoods = null;
								String yllock = "no";
								if (pro.getJgyl() == null
										|| pro.getJgyl().equals("")
										|| pro.getJgyl().equals("no")) {
									ylGoods = (Goods) totalDao
											.getObjectByCondition(
													"from Goods where goodsClass='余料库' and goodsCurQuantity>=0 and goodsMarkId=? and goodsFormat=? "
															+ "and ylMarkId=? and ylSelfCard=? and (yllock is null or yllock='' or yllock='no') and (fcStatus is null or fcStatus='可用')",
													pro.getTrademark(),
													pro.getSpecification(), pro
															.getMarkId(), pro
															.getSelfCard());

								} else {
									ylGoods = (Goods) totalDao
											.getObjectByCondition(
													"from Goods where goodsClass='余料库' and goodsCurQuantity>=0 and goodsMarkId=? and goodsFormat=? "
															+ "and ylMarkId=? and ylSelfCard=? and yllock = 'yes' and ylMarkId=? and (fcStatus is null or fcStatus='可用')",
													pro.getTrademark(),
													pro.getSpecification(), pro
															.getMarkId(), pro
															.getSelfCard());
									yllock = "yes";
								}
								if (ylGoods == null) {
									ylGoods = new Goods();
									ylGoods.setGoodsClass("余料库");
									ylGoods.setGoodsBeginQuantity(0f);
									ylGoods.setGoodsCurQuantity(0f);
									ylGoods.setYlMarkId(pro.getMarkId());
									ylGoods.setYlSelfCard(pro.getSelfCard());
									ylGoods.setGoodsMarkId(pro.getTrademark());
									ylGoods.setGoodsFormat(pro
											.getSpecification());
									ylGoods.setGoodsChangeTime(Util
											.getDateTime("yyyy-MM-dd"));
									ylGoods.setYllock(yllock);
									// ylGoods.set
									ylGoods.setGoodsBeginQuantity(shifa
											- qingling);
									ylGoods.setGoodsCurQuantity(shifa
											- qingling);

									if (ylGoods.getGoodsCurQuantity() < 0) {
										sendMessage(ylGoods);
										ylGoods.setGoodsCurQuantity(0f);
									}
									totalDao.save(ylGoods);
								} else {
									ylGoods.setGoodsBeginQuantity(ylGoods
											.getGoodsBeginQuantity()
											+ shifa - qingling);
									ylGoods.setGoodsCurQuantity(ylGoods
											.getGoodsCurQuantity()
											+ shifa - qingling);
									if (ylGoods.getGoodsCurQuantity() < 0) {
										sendMessage(ylGoods);
										ylGoods.setGoodsCurQuantity(0f);
									}
									totalDao.update(ylGoods);
								}
								// 余料入库记录
								GoodsStore ylgs = new GoodsStore();
								ylgs.setGoodsStoreMarkId(ylGoods
										.getGoodsMarkId());// 件号
								ylgs.setGoodsStoreFormat(ylGoods
										.getGoodsFormat());// 规格
								ylgs.setGoodsStoreGoodsName(pro.getProName());// 名称
								ylgs.setGoodsStoreLot(pro.getSelfCard());// 批次
								ylgs.setGoodsStoreCount(shifa - qingling);// 数量
								ylgs.setPrintStatus("YES");
								List<String> totalMarkId = totalDao
										.query(
												"select markId from Procard where id=?",
												pro.getRootId());
								if (totalMarkId.size() > 0) {
									ylgs.setGoodsStoreProMarkId(totalMarkId
											.get(0));// 总成件号
								}
								ylgs.setGoodsStoreWarehouse("余料库");// 库别
								Users jingban = Util.getLoginUser();
								ylgs.setGoodsStoreCharger(jingban.getName());// 经办人
								ylgs.setStyle("领料(余料)");// 入库类型
								if (us != null) {// 负责人
									ylgs.setGoodsStorePerson(us.getName());
								} else {
									ylgs.setGoodsStorePerson(pro
											.getLingliaoren());
								}
								ylgs.setGoodsStoreDate(DateUtil.formatDate(
										new Date(), "yyyy-MM-dd"));
								ylgs.setGoodsStoreUnit(pro.getUnit());// 单位
								totalDao.save(ylgs);
							}
							if (g.getGoodsCurQuantity() >= shifa) {
								// 处理材料批次
								if (lingliaoDetailProSb.length() == 0) {
									lingliaoDetailProSb.append(g
											.getGoodsLotId()
											+ ":" + shifa);
								} else {
									lingliaoDetailProSb.append(","
											+ g.getGoodsLotId() + ":" + shifa);
								}

								sell.setSellCount(shifa);
								g.setGoodsCurQuantity(g.getGoodsCurQuantity()
										- shifa);
								if (g.getGoodsCurQuantity() < 0) {
									sendMessage(g);
									g.setGoodsCurQuantity(0f);
								}
								listSell.add(sell);
								totalDao.update(g);
								totalDao.save(sell);
								break;
							} else {
								// 处理材料批次
								if (lingliaoDetailProSb.length() == 0) {
									lingliaoDetailProSb.append(g
											.getGoodsLotId()
											+ ":" + g.getGoodsCurQuantity());
								} else {
									lingliaoDetailProSb.append(","
											+ g.getGoodsLotId() + ":"
											+ g.getGoodsCurQuantity());
								}

								sell.setSellCount(g.getGoodsCurQuantity());
								listSell.add(sell);
								shifa -= g.getGoodsCurQuantity();
								g.setGoodsCurQuantity(0f);
								totalDao.update(g);
								totalDao.save(sell);
							}
						} else {// 单位不一致或者有支数
							if (g.getGoodsCurQuantity() >= shifa && shifa != 0) {
								// 处理材料批次
								if (lingliaoDetailProSb.length() == 0) {
									lingliaoDetailProSb.append(g
											.getGoodsLotId()
											+ ":" + shifa);
								} else {
									lingliaoDetailProSb.append(","
											+ g.getGoodsLotId() + ":" + shifa);
								}

								sell.setSellCount(shifa);
								g.setGoodsCurQuantity(g.getGoodsCurQuantity()
										- shifa);
								if (g.getGoodsCurQuantity() < 0) {
									sendMessage(g);
									g.setGoodsCurQuantity(0f);
								}
								shifa = 0f;
							} else if (shifa != 0) {
								// 处理材料批次
								if (lingliaoDetailProSb.length() == 0) {
									lingliaoDetailProSb.append(g
											.getGoodsLotId()
											+ ":" + g.getGoodsCurQuantity());
								} else {
									lingliaoDetailProSb.append(","
											+ g.getGoodsLotId() + ":"
											+ g.getGoodsCurQuantity());
								}

								sell.setSellCount(g.getGoodsCurQuantity());
								shifa -= g.getGoodsCurQuantity();
								g.setGoodsCurQuantity(0f);
							}
							if (g.getGoodsZhishu() == null) {
								g.setGoodsZhishu(0F);
							}
							if (g.getGoodsZhishu() >= qingling && qingling != 0) {
								sell.setSellZhishu(qingling);
								g.setGoodsZhishu(g.getGoodsZhishu() - qingling);
								qingling = 0f;
							} else if (qingling != 0) {
								sell.setSellZhishu(g.getGoodsZhishu());
								qingling -= g.getGoodsZhishu();
								g.setGoodsZhishu(0f);
							}

							totalDao.update(g);
							totalDao.save(sell);
							if (shifa == 0) {
								break;
							}
						}

					}
				}

				if ("barcode".equals(tag)) {// 补料单
					LogoStickers sticker = (LogoStickers) totalDao
							.getObjectById(LogoStickers.class, id);
					sticker.setStatus("已发料");
					totalDao.update(sticker);
				} else {
					Procard oldProcard = byProcardid(pro);
					if (oldProcard.getHascount() == null) {
						oldProcard.setHascount(oldProcard.getKlNumber());
					}
					oldProcard.setStatus("已发料");
					if (oldProcard.getCardId() != null
							&& oldProcard.getCardId() > 0) {
						RunningWaterCard rw = (RunningWaterCard) totalDao
								.getObjectById(RunningWaterCard.class,
										oldProcard.getCardId());
						rw.setReceiveStatus("yes");
						rw.setCardStatus("已发料");
						rw.setOwnUsername(pro.getLingliaoren());
						totalDao.update(rw);
					}
				}
			}
		}
		pro = (Procard) totalDao.getObjectById(Procard.class, pro.getId());
		if (pro.getLingliaoDetail() == null
				|| pro.getLingliaoDetail().length() == 0) {
			pro.setLingliaoDetail(lingliaoDetailProSb.toString());
		} else {
			pro.setLingliaoDetail(pro.getLingliaoDetail() + ","
					+ lingliaoDetailProSb.toString());
		}
		if (pro.getHascount() == null) {
			pro.setHascount(pro.getKlNumber());
		}
		pro.setHascount(pro.getHascount() - getCount);
		if (pro.getHascount() < 0) {
			// 发送异常消息bgg
			AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
					+ pro.getMarkId() + "批次:" + pro.getSelfCard()
					+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为" + Util.getDateTime(),
					"2");
			pro.setHascount(0f);
		}
		// 同步备料通知信息
		List<ProcardMaterial> pmList = totalDao
				.query(
						"from ProcardMaterial where procardId=? and (lingliaoStatus is null or lingliaoStatus!='已领')",
						pro.getId());
		if (pmList != null && pmList.size() > 0) {
			for (ProcardMaterial pm : pmList) {
				pm.setLingliaoStatus("已领");
				totalDao.update(pm);
				Float noylcount = (Float) totalDao
						.getObjectByCondition(
								"select count(id) from ProcardMaterial "
										+ "where (lingliaoStatus is null or lingliaoStatus!='已领') and headId=?",
								pm.getHeadId());
				if (noylcount == null || noylcount == 0) {
					ProcardMaterialHead pmHead = (ProcardMaterialHead) totalDao
							.getObjectById(ProcardMaterialHead.class, pm
									.getHeadId());
					if (pmHead != null) {
						pmHead.setLingliaoStatus("已领");
						totalDao.update(pmHead);
					}
				}
			}
		}
		totalDao.update(pro);
		// 处理本层工序的可领数量
		// String hql_update =
		// "update ProcessInfor set totalCount=? where procard.id=?";
		// totalDao.createQueryUpdate(hql_update, null, (pro.getKlNumber() - pro
		// .getHascount()), pro.getId());
		String hql_update = "from ProcessInfor where procard.id=?";
		List<ProcessInfor> sonProcessinfor = totalDao.query(hql_update, pro
				.getId());
		for (ProcessInfor processInfor : sonProcessinfor) {
			float totalCount = (float) Math.floor(pro.getKlNumber()
					- pro.getHascount());
			processInfor.setTotalCount(totalCount);
			totalDao.update(processInfor);
		}
		/********************************** 如果第一道工序就是外委，则生成外委采购计划 *****************************************/
		/**** 生成外委工序计划 ***/
		String nextWwhql = "from ProcessInfor where  procard.id=? order by processNO";
		List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
				.query(nextWwhql, pro.getId());
		if (nextWwProcessInforList.size() > 0) {
			int n = 0;
			WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
			Float wwckCount = (Float) totalDao
					.getObjectByCondition("select count(*) from WareHouse where name='委外库'");
			for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
				if (nextWwProcessInfor != null) {
					if ("外委".equals(nextWwProcessInfor.getProductStyle())
							&& (n == 0 || ("yes").equals(nextWwProcessInfor
									.getProcessStatus()))) {
						// 第一道外委工序和之后的并行外委合并外委
						if (n == 0) {
							wwp.setRootMarkId(pro.getRootMarkId());
							wwp.setRootSelfCard(pro.getRootSelfCard());
							wwp.setOrderNum(pro.getOrderNumber());
							wwp.setYwMarkId(pro.getYwMarkId());
							wwp.setMarkId(pro.getMarkId());
							wwp.setProcessNo(nextWwProcessInfor.getProcessNO()
									+ "");
							wwp.setProcessName(nextWwProcessInfor
									.getProcessName());
							wwp.setProName(pro.getProName());
							wwp.setType("外委");
							wwp.setBeginCount(getCount);
							wwp.setNumber(getCount);
							wwp.setBanben(pro.getBanBenNumber());
							wwp.setBanci(pro.getBanci());
							wwp.setUnit(pro.getUnit());
							// if (nextWwProcessInfor.getProcessNO().equals(
							// maxProcessNo)) {
							// // 最后一道外委激活数量=下层最小minNumber - 已生成过的外委数量
							// // 当beginCount=null时为改版之前数据
							// Float wwCount1 = (Float) totalDao
							// .getObjectByCondition(
							// "select sum(number) from WaigouWaiweiPlan where processNO=? and beginCount is null and procardId=?",
							// maxProcessNo, pro.getId());
							// // 当beginCount>0时以beginCount为标准
							// Float wwCount2 = (Float) totalDao
							// .getObjectByCondition(
							// "select sum(beginCount) from WaigouWaiweiPlan where processNO=? and beginCount>0 and procardId=?",
							// maxProcessNo, pro.getId());
							// Float beginCount = lasttotalCount - wwCount1
							// - wwCount2;
							// if (beginCount <= 0) {
							// beginCount = 0f;
							// }
							// wwp.setBeginCount(beginCount);
							// wwp.setNumber(beginCount);
							// } else {
							// wwp.setBeginCount(getCount);
							// wwp.setNumber(getCount);
							// }
							wwp.setAddTime(Util.getDateTime());
							wwp.setJihuoTime(Util.getDateTime());
							wwp.setShArrivalTime(pro.getNeedFinalDate());// 应到货时间在采购确认通知后计算
							wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
							String wwNumber = "";
							String before = null;
							Integer bIndex = 10;
							before = "ww" + Util.getDateTime("yyyyMMdd");
							Integer maxNo = 0;
							String maxNumber = (String) totalDao
									.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
											+ before + "%'");
							if (maxNumber != null) {
								String wwnum = maxNumber.substring(bIndex,
										maxNumber.length());
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
							wwNumber = before + String.format("%03d", maxNo);
							wwp.setPlanNumber(wwNumber);// 采购计划编号
							wwp.setSelfCard(pro.getSelfCard());// 批次
							// wwp.setGysId(nextWwProcessInfor.getZhuserId());//
							// 供应商id
							// wwp.setGysName(nextWwProcessInfor.getGys());//
							// 供应商名称
							wwp.setAllJiepai(nextWwProcessInfor.getAllJiepai());// 单件总节拍
							wwp.setDeliveryDuration(nextWwProcessInfor
									.getDeliveryDuration());// 耽误时长
							wwp.setSingleDuration(pro.getSingleDuration());// 单班时长(工作时长)
							wwp.setProcardId(pro.getId());
							wwp.setProcard(pro);
							if (wwckCount != null && wwckCount > 0) {
								Float wwCount = pro.getFilnalCount();
								wwp.setStatus("待在制入库");
								// 在制品待入库
								if (pro.getZaizhiApplyZk() == null) {
									pro.setZaizhiApplyZk(0f);
								}
								if (pro.getZaizhizkCount() == null) {
									pro.setZaizhizkCount(0f);
								}
								if (pro.getKlNumber() == null) {
									pro.setKlNumber(pro.getFilnalCount());
								}
								if (pro.getHascount() == 0) {
									pro.setHascount(pro.getKlNumber());
								}
								// procard.getKlNumber()-procard.getHascount()=已生产数量
								// 可转库数量=已生产数量-已转库数量-转库申请中数量
								pro.setZaizhikzkCount(pro.getKlNumber()
										- pro.getHascount()
										- pro.getZaizhizkCount()
										- pro.getZaizhiApplyZk());
								if (pro.getZaizhikzkCount() >= wwCount) {
									pro.setZaizhiApplyZk(pro.getZaizhiApplyZk()
											+ wwCount);
									String orderNum = (String) totalDao
											.getObjectByCondition(
													"select orderNumber from Procard where id=?",
													pro.getRootId());
									// 成品待入库
									GoodsStore goodsStore2 = new GoodsStore();
									goodsStore2.setNeiorderId(orderNum);
									goodsStore2.setGoodsStoreMarkId(pro
											.getMarkId());
									goodsStore2.setBanBenNumber(pro
											.getBanBenNumber());
									goodsStore2.setGoodsStoreLot(pro
											.getSelfCard());
									goodsStore2.setGoodsStoreGoodsName(pro
											.getProName());
									goodsStore2
											.setApplyTime(Util.getDateTime());
									goodsStore2
											.setGoodsStoreArtsCard((String) totalDao
													.getObjectByCondition(
															"select selfCard from Procard where id=?",
															pro.getRootId()));
									goodsStore2.setGoodsStorePerson(Util
											.getLoginUser().getName());
									goodsStore2.setStatus("待入库");
									goodsStore2.setStyle("半成品转库");
									goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
									goodsStore2.setNeiorderId(pro
											.getOrderNumber());
									goodsStore2.setWaiorderId(pro
											.getOutOrderNum());
									goodsStore2.setProcardId(pro.getId());
									// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
									// 区名
									// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
									// 库位
									goodsStore2
											.setGoodsStoreUnit(pro.getUnit());
									goodsStore2.setGoodsStoreCount(wwCount);
									totalDao.update(pro);
									totalDao.save(goodsStore2);
								} else {
									throw new RuntimeException("对不起超过可申请数量("
											+ pro.getZaizhikzkCount() + ")");
								}
							} else {
								wwp.setStatus("待激活");
							}
							totalDao.save(wwp);
						} else {
							wwp.setProcessNo(wwp.getProcessNo() + ";"
									+ nextWwProcessInfor.getProcessNO());
							wwp.setProcessName(wwp.getProcessName() + ";"
									+ nextWwProcessInfor.getProcessName());
							totalDao.update(wwp);
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (wwp.getId() != null) {
				// 匹配供应商
				Price price = (Price) totalDao
						.getObjectByCondition(
								"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
										+ Util.getDateTime()
										+ "')  order by hsPrice", wwp
										.getMarkId(), wwp.getProcessNo());
				if (price != null) {
					wwp.setPriceId(price.getId());
					wwp.setGysId(price.getGysId());
					ZhUser zhUser = (ZhUser) totalDao.getObjectById(
							ZhUser.class, price.getGysId());
					wwp.setGysName(zhUser.getName());
					wwp.setUserCode(zhUser.getUsercode());
					wwp.setUserId(zhUser.getUserid());
					totalDao.update(wwp);
				}
			}
		}

		return listSell;
	}

	/**
	 * 外购件领料
	 */
	@SuppressWarnings("unchecked")
	public List saveSellList(Integer id, Procard pro, List<Goods> li,
			String tag, float qingling, float shifa, float getCount,
			String ckType, int[] selected) {
		Users user = (Users) Util.getLoginUser();
		String hql1 = "from Users where cardId=? and onWork not in('离职','离职中','内退','病休')";
		Users us = (Users) totalDao.getObjectByCondition(hql1, pro
				.getLingliaoren());// 领料人
		if (us == null) {
			throw new RuntimeException("领料人不存在,领料失败!");
		} else {
			Float czcount = (Float) totalDao
					.getObjectByCondition(
							"select count(id) from ProcessinforPeople where procard.id=? and userId =?",
							pro.getId(), us.getId());
			if (czcount == null || czcount == 0) {
				throw new RuntimeException("领料人对此零件无领料权限,领料失败!");
			}
		}
		float zaizhiCount = 0f;
		float yzaizhiCount = 0f;
		float deletehasCount = 0f;
		float ydeltehasCount = 0f;// 组合领原材料时有成品在制品减去对应的hascount
		if (!pro.getProcardStyle().equals("总成")) {// 总成无在制品
			zaizhiCount = getZaiZhiCount(pro.getId());// 计算可用在制品
		}
		if (pro.isZhHasYcl() && ckType.equals("原材料库")) {
			yzaizhiCount = getYclZaiZhiCount(pro.getId());
		}
		float addZaihi = 0f;
		if (zaizhiCount >= 0 && yzaizhiCount >= 0) {
			if (zaizhiCount >= getCount || yzaizhiCount >= getCount) {// 在制品够
				deletehasCount = getCount;
				if (pro.isZhHasYcl() && ckType.equals("原材料库")
						&& zaizhiCount >= getCount) {
					ydeltehasCount = getCount;
				} else {
					ydeltehasCount = zaizhiCount;

				}
			} else {
				if (pro.isZhHasYcl() && ckType.equals("原材料库")) {
					ydeltehasCount = zaizhiCount;
				}
				deletehasCount = zaizhiCount + yzaizhiCount;
				addZaihi = getCount - (zaizhiCount + yzaizhiCount);
				boolean isaddZaizhi = true;
				if (ckType.equals("原材料库") && qingling <= 0) {
					isaddZaizhi = false;
				}
				if (ckType.equals("外购件库")) {
					// float xsHasCount =0f;
					// if(pro.getHascount()==null){
					// xsHasCount=pro.getKlNumber();
					// }else{
					// xsHasCount=pro.getHascount();
					// }
					// xsHasCount =(float) (xsHasCount- Math.floor(xsHasCount));
					// float xsgetCount =(float) (getCount-
					// Math.floor(getCount));
					// if(xsgetCount>=xsHasCount&&xsHasCount!=0){
					// addZaihi = (float) Math.ceil(addZaihi);
					// }else{
					// addZaihi = (float) Math.floor(addZaihi);
					// }
				}
				if (addZaihi > 0 && isaddZaizhi) {
					// 添加在制品
					String hqlzaizhi = "from Goods where goodsMarkId='"
							+ pro.getMarkId()
							+ "' and goodsLotId='"
							+ pro.getSelfCard()
							+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用') ";
					if (pro.isZhHasYcl() && ckType.equals("原材料库")) {
						hqlzaizhi += "and goodsFormat='原材料'";
						Goods goodsu = li.get(0);
						if (goodsu.getGoodsUnit().equals(pro.getYuanUnit())) {
							Float bizhong = (Float) totalDao
									.getObjectByCondition(
											"select bili from YuanclAndWaigj where clClass='原材料' and trademark=? and specification=?",
											pro.getTrademark(), pro
													.getSpecification());
							if (bizhong == null || bizhong == 0) {
								bizhong = 1f;
							}
							addZaihi = qingling * bizhong * pro.getQuanzi1()
									/ pro.getQuanzi2();
						} else {
							addZaihi = qingling * pro.getQuanzi1()
									/ pro.getQuanzi2();
						}
					} else {
						hqlzaizhi += "and (goodsFormat is null or goodsFormat!='原材料')";
					}
					List listzizhi = totalDao.find(hqlzaizhi);
					if (listzizhi != null && listzizhi.size() > 0) {
						Goods g1 = (Goods) listzizhi.get(0);
						g1.setGoodsCurQuantity(g1.getGoodsCurQuantity()
								+ (float) Math.floor(addZaihi));
						if (g1.getGoodsCurQuantity() < 0) {
							sendMessage(g1);
							g1.setGoodsCurQuantity(0f);
						}
						totalDao.update(g1);
					} else {
						Goods gg = new Goods();
						gg.setGoodsMarkId(pro.getMarkId());
						gg.setGoodsLotId(pro.getSelfCard());
						gg.setGoodsFullName(pro.getProName());
						gg.setGoodsClass("在制品");
						if (pro.isZhHasYcl() && ckType.equals("原材料库")) {
							gg.setGoodsFormat("原材料");
						}

						gg.setGoodsCurQuantity((float) Math.floor(addZaihi));
						if (gg.getGoodsCurQuantity() < 0) {
							sendMessage(gg);
							gg.setGoodsCurQuantity(0f);
						}
						gg.setGoodsUnit(pro.getUnit());
						gg.setWgType(pro.getWgType());
						totalDao.save(gg);

					}
					// 添加在制品入库记录
					GoodsStore gs = new GoodsStore();
					gs.setGoodsStoreMarkId(pro.getMarkId());// 件号
					gs.setBanBenNumber(pro.getBanBenNumber());
					gs.setGoodsStoreGoodsName(pro.getProName());// 名称
					gs.setGoodsStoreLot(pro.getSelfCard());// 批次
					gs.setGoodsStoreCount((float) Math.floor(addZaihi));// 数量
					if (pro.isZhHasYcl() && ckType.contains("原材料")) {
						gs.setGoodsStoreFormat("原材料");
					}
					gs.setPrintStatus("YES");
					List<String> totalMarkId = totalDao.query(
							"select markId from Procard where id=?", pro
									.getRootId());
					if (totalMarkId.size() > 0) {
						gs.setGoodsStoreProMarkId(totalMarkId.get(0));// 总成件号
					}
					gs.setGoodsStoreWarehouse("在制品");// 库别
					Users jingban = Util.getLoginUser();
					gs.setGoodsStoreCharger(jingban.getName());// 经办人
					gs.setStyle("正常（成品）");// 入库类型
					gs.setGoodsStorePerson(us.getName());
					gs.setGoodsStoreDate(DateUtil.formatDate(new Date(),
							"yyyy-MM-dd"));
					gs.setGoodsStoreUnit(pro.getUnit());// 单位
					gs.setWgType(pro.getWgType());
					totalDao.save(gs);
					if (pro.isZhHasYcl() && ckType.equals("外购件库")) {
						// 组合有原材料,领外购件时要同时领出原材料在制品
						String hqlyzaizhi = "from Goods where goodsMarkId='"
								+ pro.getMarkId()
								+ "' and goodsLotId='"
								+ pro.getSelfCard()
								+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用') and goodsFormat='原材料'";
						List<Goods> yclZaiZhiGoodsList = totalDao
								.query(hqlyzaizhi);
						Float youtZaizhi = addZaihi;
						for (Goods yclZaiZhiGoods : yclZaiZhiGoodsList) {
							if (yclZaiZhiGoods.getGoodsCurQuantity() >= youtZaizhi) {
								// 原材料在制品量修改
								yclZaiZhiGoods
										.setGoodsCurQuantity(yclZaiZhiGoods
												.getGoodsCurQuantity()
												- youtZaizhi);
								if (yclZaiZhiGoods.getGoodsCurQuantity() < 0) {
									sendMessage(yclZaiZhiGoods);
									yclZaiZhiGoods.setGoodsCurQuantity(0f);
								}
								totalDao.update(yclZaiZhiGoods);
								// 原材料在制品出库
								Sell sellYzz = new Sell();
								sellYzz.setSellArtsCard(pro.getSelfCard());
								sellYzz.setSellSupplier(yclZaiZhiGoods
										.getGoodsSupplier());
								sellYzz.setSellFormat(yclZaiZhiGoods
										.getGoodsFormat());
								sellYzz.setSellLot(yclZaiZhiGoods
										.getGoodsLotId());
								sellYzz.setSellMarkId(yclZaiZhiGoods
										.getGoodsMarkId());
								sellYzz.setSellAdminName(user.getName());
								sellYzz.setSellGoods(yclZaiZhiGoods
										.getGoodsFullName());
								sellYzz.setSellDate(Util
										.getDateTime("yyyy-MM-dd"));
								sellYzz.setSellTime(Util.getDateTime());
								sellYzz.setSellWarehouse(yclZaiZhiGoods
										.getGoodsClass());
								sellYzz.setSellUnit(yclZaiZhiGoods
										.getGoodsUnit());
								sellYzz.setSellCount(youtZaizhi);
								sellYzz.setWgType(yclZaiZhiGoods.getWgType());
								youtZaizhi = 0f;
								totalDao.save(sellYzz);
								break;
							} else {
								// 原材料在制品出库
								Sell sellYzz = new Sell();
								sellYzz.setSellArtsCard(pro.getSelfCard());
								sellYzz.setSellSupplier(yclZaiZhiGoods
										.getGoodsSupplier());
								sellYzz.setSellFormat(yclZaiZhiGoods
										.getGoodsFormat());
								sellYzz.setSellLot(yclZaiZhiGoods
										.getGoodsLotId());
								sellYzz.setSellMarkId(yclZaiZhiGoods
										.getGoodsMarkId());
								sellYzz.setSellAdminName(user.getName());
								sellYzz.setSellGoods(yclZaiZhiGoods
										.getGoodsFullName());
								sellYzz.setSellDate(Util
										.getDateTime("yyyy-MM-dd"));
								sellYzz.setSellTime(Util.getDateTime());
								sellYzz.setSellWarehouse(yclZaiZhiGoods
										.getGoodsClass());
								sellYzz.setSellUnit(yclZaiZhiGoods
										.getGoodsUnit());
								sellYzz.setSellCount(yclZaiZhiGoods
										.getGoodsCurQuantity());
								sellYzz.setWgType(yclZaiZhiGoods.getWgType());
								totalDao.save(sellYzz);
								// 原材料在制品量修改
								youtZaizhi = youtZaizhi
										- yclZaiZhiGoods.getGoodsCurQuantity();
								yclZaiZhiGoods.setGoodsCurQuantity(0f);
								totalDao.update(yclZaiZhiGoods);
							}
						}

					}

				}
			}
		} else if (yzaizhiCount < 0) {
			throw new RuntimeException("对不起,之前批次的原材料在制品不足!");
		} else {
			throw new RuntimeException("对不起,之前批次的在制品不足!");
		}
		String lingliaoPeople = pro.getLingliaoren();
		pro = (Procard) totalDao.getObjectById(Procard.class, pro.getId());
		pro.setLingliaoren(lingliaoPeople);
		if (pro.isZhHasYcl() && ckType.equals("原材料库")) {
			if (pro.getYhascount() == null) {
				pro.setYhascount(pro.getKlNumber());
			}
			pro.setYhascount(pro.getYhascount() - getCount);
			if (pro.getYhascount() < 0) {
				return null;
			}
			pro.setHascount(pro.getHascount() - ydeltehasCount);
		} else {
			if (pro.getHascount() == null) {
				pro.setHascount(pro.getKlNumber());
			}
			pro.setHascount(pro.getHascount() - getCount);
			if (pro.getHascount() < 0) {
				AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
						+ pro.getMarkId() + "批次:" + pro.getSelfCard()
						+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为"
						+ Util.getDateTime(), "2");
				pro.setHascount(0f);
				return null;
			}
		}

		List listSell = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		List<Procard> wgprocardSonList = totalDao.query(
				"from Procard where procard.id=? and procardStyle='外购'", pro
						.getId());
		float lasttotalCount = pro.getFilnalCount();// 最后一道工序的可领数量
		for (Goods g : li) {
			if (g.getGoodsPosition() != null
					&& g.getGoodsPosition().equals("在制品")) {
				continue;
			}
			Sell sell = new Sell();
			sell.setSellArtsCard(pro.getSelfCard());
			sell.setSellSupplier(g.getGoodsSupplier());
			sell.setSellFormat(g.getGoodsFormat());
			sell.setSellLot(g.getGoodsLotId());
			sell.setSellMarkId(g.getGoodsMarkId());
			sell.setSellAdminName(user.getName());
			sell.setSellGoods(g.getGoodsFullName());
			sell.setSellDate(sdf2.format(new Date()));
			sell.setSellTime(sdf.format(new Date()));
			sell.setSellWarehouse(g.getGoodsClass());
			sell.setWgType(g.getWgType());
			// ---------------------------------------------
			/***
			 * TODO //更具卡号查选
			 */
			sell.setSellCharger(us.getName());
			// -----------------------------------------
			sell.setSellUnit(g.getGoodsUnit());
			if ("总成".equals(pro.getProcardStyle())) {
				sell.setSellProMarkId(pro.getMarkId());
			} else {
				int prorootId = pro.getRootId();
				sell.setSellProMarkId((byProcardid(prorootId)).getMarkId());
			}
			sell.setPrintStatus("NO");
			sell.setStyle("刷卡出库");
			sell.setSellPeople("待确认");
			sell.setSellAdminName(user.getName());
			// 数量处理
			if (g.getGoodsClass() != null && g.getGoodsClass().equals("原材料库")) {// 原材料领料
				// 同步备料通知信息
				List<ProcardMaterial> pmList = totalDao
						.query(
								"from ProcardMaterial where procardId=? and (lingliaoStatus is null or lingliaoStatus!='已领')",
								pro.getId());
				if (pmList != null && pmList.size() > 0) {
					for (ProcardMaterial pm : pmList) {
						pm.setLingliaoStatus("已领");
						totalDao.update(pm);
						Float noylcount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from ProcardMaterial "
												+ "where (lingliaoStatus is null or lingliaoStatus!='已领') and headId=?",
										pm.getHeadId());
						if (noylcount == null || noylcount == 0) {
							ProcardMaterialHead pmHead = (ProcardMaterialHead) totalDao
									.getObjectById(ProcardMaterialHead.class,
											pm.getHeadId());
							if (pmHead != null) {
								pmHead.setLingliaoStatus("已领");
								totalDao.update(pmHead);
							}
						}
					}
				}
				if (!ckType.contains("原材料库")) {
					throw new RuntimeException("对不起您没有原材料库的出库权限!");
				}
				// 余料抵发
				if (g.getYlshifa() > 0) {
					float ylshifa = g.getYlshifa();
					float ckyl = getYlCount(pro);
					if (ckyl < ylshifa) {
						throw new RuntimeException("对不起余料抵发数量不足,领料失败!");
					}
					List<Goods> ylGoodsList = (List<Goods>) totalDao
							.query(
									"from Goods where goodsClass='余料库' and goodsCurQuantity>0 and goodsMarkId=? and goodsFormat=? "
											+ "and (yllock is null or yllock='' or yllock='no' or (yllock = 'yes' and ylMarkId=?)) and (fcStatus is null or fcStatus='可用') ",
									pro.getTrademark(), pro.getSpecification(),
									pro.getMarkId());
					for (Goods ckylg : ylGoodsList) {
						if (ckylg.getGoodsCurQuantity() >= ylshifa) {
							// 余料数量修改
							ckylg.setGoodsCurQuantity(ckylg
									.getGoodsCurQuantity()
									- ylshifa);

							if (ckylg.getGoodsCurQuantity() < 0) {
								sendMessage(ckylg);
								ckylg.setGoodsCurQuantity(0f);
							}
							totalDao.update(ckylg);
							// 余料出库
							Sell sellYl = new Sell();
							sellYl.setSellArtsCard(pro.getSelfCard());
							sellYl.setSellSupplier(ckylg.getGoodsSupplier());
							sellYl.setSellFormat(ckylg.getGoodsFormat());
							sellYl.setSellLot(ckylg.getGoodsLotId());
							sellYl.setSellMarkId(ckylg.getGoodsMarkId());
							sellYl.setSellAdminName(user.getName());
							sellYl.setSellGoods(ckylg.getGoodsFullName());
							sellYl.setSellDate(sdf2.format(new Date()));
							sellYl.setSellTime(sdf.format(new Date()));
							sellYl.setSellWarehouse(ckylg.getGoodsClass());
							sellYl.setSellUnit(ckylg.getGoodsUnit());
							sellYl.setSellCount(ylshifa);
							sellYl.setWgType(ckylg.getWgType());
							ylshifa = 0;
							totalDao.save(sellYl);
							break;
						} else {
							// 余料出库
							Sell sellYl = new Sell();
							sellYl.setSellArtsCard(pro.getSelfCard());
							sellYl.setSellSupplier(ckylg.getGoodsSupplier());
							sellYl.setSellFormat(ckylg.getGoodsFormat());
							sellYl.setSellLot(ckylg.getGoodsLotId());
							sellYl.setSellMarkId(ckylg.getGoodsMarkId());
							sellYl.setSellAdminName(user.getName());
							sellYl.setSellGoods(ckylg.getGoodsFullName());
							sellYl.setSellDate(sdf2.format(new Date()));
							sellYl.setSellTime(sdf.format(new Date()));
							sellYl.setSellWarehouse(ckylg.getGoodsClass());
							sellYl.setSellUnit(ckylg.getGoodsUnit());
							sellYl.setSellCount(ckylg.getGoodsCurQuantity());
							sellYl.setWgType(ckylg.getWgType());
							totalDao.save(sellYl);
							// 余料数量修改
							ylshifa = ylshifa - ckylg.getGoodsCurQuantity();
							ckylg.setGoodsCurQuantity(0f);
							totalDao.update(ckylg);
						}
					}

				}

				String goodsClass = "原材料库";
				// if(pro.getProductStyle()!=null&&
				// pro.getProductStyle().equals("试制")){
				// goodsClass="试制库";
				// }//原材料还是在原材料库里取
				String hql = "from Goods where goodsMarkId='"
						+ pro.getTrademark()
						+ "' and goodsFormat='"
						+ pro.getSpecification()
						+ "' and goodsClass='"
						+ goodsClass
						+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用') order by goodsChangeTime asc";
				List<Goods> listG = totalDao.find(hql);
				if (listG.size() > 0 || qingling == 0) {
					if (qingling > 0 && listG.size() > 0) {// 需要原材料
						for (Goods gck : listG) {
							sell = new Sell();
							sell.setSellArtsCard(pro.getSelfCard());
							sell.setSellSupplier(gck.getGoodsSupplier());
							sell.setSellFormat(gck.getGoodsFormat());
							sell.setSellLot(gck.getGoodsLotId());
							sell.setSellMarkId(gck.getGoodsMarkId());
							sell.setSellAdminName(user.getName());
							sell.setSellGoods(gck.getGoodsFullName());
							sell.setSellDate(sdf2.format(new Date()));
							sell.setSellTime(sdf.format(new Date()));
							sell.setSellWarehouse(gck.getGoodsClass());
							sell.setSellUnit(gck.getGoodsUnit());
							sell.setWgType(gck.getWgType());
							if (us != null) {
								sell.setSellCharger(us.getName());
							} else {
								sell.setSellCharger(pro.getLingliaoren());

							}
							// --------------------------------
							if ("总成".equals(pro.getProcardStyle())) {
								sell.setSellProMarkId(pro.getMarkId());
							} else {
								int prorootId = pro.getRootId();
								sell.setSellProMarkId((byProcardid(prorootId))
										.getMarkId());
							}

							sell.setPrintStatus("NO");
							sell.setStyle("刷卡出库");
							sell.setSellPeople("待确认");
							sell.setSellAdminName(user.getName());
							String allzhishuCountHql = "select sum(goodsZhishu) from Goods where goodsMarkId='"
									+ pro.getTrademark()
									+ "' and goodsFormat='"
									+ pro.getSpecification()
									+ "' and goodsClass='"
									+ goodsClass
									+ "' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
							Float allzhishuCount = 0f;
							List<Float> allzhishuCountList = totalDao
									.query(allzhishuCountHql);
							if (allzhishuCountList.size() > 0) {
								allzhishuCount = allzhishuCountList.get(0);
							}
							if (pro.getYuanUnit().equals(g.getGoodsUnit())
									&& allzhishuCount == 0) {// 单位一致并且支数为0
								if (shifa > qingling) {// 实发大于请领多余的以余料入库
									Goods ylGoods = null;
									String yllock = "no";
									if (pro.getJgyl() == null
											|| pro.getJgyl().equals("")
											|| pro.getJgyl().equals("no")) {
										ylGoods = (Goods) totalDao
												.getObjectByCondition(
														"from Goods where goodsClass='余料库' and goodsCurQuantity>=0 and goodsMarkId=? and goodsFormat=? "
																+ "and ylMarkId=? and ylSelfCard=? and (yllock is null or yllock='' or yllock='no') and (fcStatus is null or fcStatus='可用') ",
														pro.getTrademark(),
														pro.getSpecification(),
														pro.getMarkId(), pro
																.getSelfCard());

									} else {
										ylGoods = (Goods) totalDao
												.getObjectByCondition(
														"from Goods where goodsClass='余料库' and goodsCurQuantity>=0 and goodsMarkId=? and goodsFormat=? "
																+ "and ylMarkId=? and ylSelfCard=? and yllock = 'yes' and ylMarkId=? and (fcStatus is null or fcStatus='可用') ",
														pro.getTrademark(),
														pro.getSpecification(),
														pro.getMarkId(), pro
																.getSelfCard());
										yllock = "yes";
									}
									if (ylGoods == null) {
										ylGoods = new Goods();
										ylGoods.setGoodsClass("余料库");
										ylGoods.setGoodsBeginQuantity(0f);
										ylGoods.setGoodsCurQuantity(0f);
										ylGoods.setYlMarkId(pro.getMarkId());
										ylGoods
												.setYlSelfCard(pro
														.getSelfCard());
										ylGoods.setGoodsMarkId(pro
												.getTrademark());
										ylGoods.setGoodsFormat(pro
												.getSpecification());
										ylGoods.setGoodsUnit(g.getGoodsUnit());
										ylGoods.setGoodsChangeTime(Util
												.getDateTime("yyyy-MM-dd"));
										ylGoods.setYllock(yllock);
										// ylGoods.set
										ylGoods.setGoodsBeginQuantity(shifa
												- qingling);
										ylGoods.setGoodsCurQuantity(shifa
												- qingling);
										ylGoods.setWgType(pro.getWgType());
										if (ylGoods.getGoodsCurQuantity() < 0) {
											sendMessage(ylGoods);
											ylGoods.setGoodsCurQuantity(0f);
										}
										totalDao.save(ylGoods);
									} else {
										ylGoods.setGoodsBeginQuantity(ylGoods
												.getGoodsBeginQuantity()
												+ shifa - qingling);
										ylGoods.setGoodsCurQuantity(ylGoods
												.getGoodsCurQuantity()
												+ shifa - qingling);
										if (ylGoods.getGoodsCurQuantity() < 0) {
											sendMessage(ylGoods);
											ylGoods.setGoodsCurQuantity(0f);
										}
										totalDao.update(ylGoods);
									}
									// 余料入库记录
									GoodsStore ylgs = new GoodsStore();
									ylgs.setGoodsStoreMarkId(ylGoods
											.getGoodsMarkId());// 件号
									ylgs.setGoodsStoreFormat(ylGoods
											.getGoodsFormat());// 规格
									ylgs.setGoodsStoreGoodsName(pro
											.getProName());// 名称
									ylgs.setGoodsStoreLot(pro.getSelfCard());// 批次
									ylgs.setGoodsStoreCount(shifa - qingling);// 数量
									ylgs.setPrintStatus("YES");
									ylgs.setWgType(pro.getWgType());
									List<String> totalMarkId = totalDao
											.query(
													"select markId from Procard where id=?",
													pro.getRootId());
									if (totalMarkId.size() > 0) {
										ylgs.setGoodsStoreProMarkId(totalMarkId
												.get(0));// 总成件号
									}
									ylgs.setGoodsStoreWarehouse("余料库");// 库别
									Users jingban = Util.getLoginUser();
									ylgs
											.setGoodsStoreCharger(jingban
													.getName());// 经办人
									ylgs.setStyle("余料");// 入库类型
									if (us != null) {// 负责人
										ylgs.setGoodsStorePerson(us.getName());
									} else {
										ylgs.setGoodsStorePerson(pro
												.getLingliaoren());
									}
									ylgs.setGoodsStoreDate(DateUtil.formatDate(
											new Date(), "yyyy-MM-dd"));
									ylgs.setGoodsStoreUnit(pro.getUnit());// 单位
									totalDao.save(ylgs);
								}
								if (gck.getGoodsCurQuantity() >= shifa) {
									// 记录材料批次和数量
									String lingliaoDetail = pro
											.getLingliaoDetail();
									if (lingliaoDetail == null) {
										lingliaoDetail = gck.getGoodsLotId()
												+ ":" + shifa;
									} else {
										lingliaoDetail += ","
												+ gck.getGoodsLotId() + ":"
												+ shifa;
									}
									pro.setLingliaoDetail(lingliaoDetail);

									sell.setSellCount(shifa);
									gck.setGoodsCurQuantity(gck
											.getGoodsCurQuantity()
											- shifa);
									if (gck.getGoodsCurQuantity() < 0) {
										sendMessage(gck);
										gck.setGoodsCurQuantity(0f);
									}
									listSell.add(sell);
									totalDao.update(gck);
									totalDao.save(sell);
									break;
								} else {
									// 记录材料批次和数量
									String lingliaoDetail = pro
											.getLingliaoDetail();
									if (lingliaoDetail == null) {
										lingliaoDetail = gck.getGoodsLotId()
												+ ":"
												+ gck.getGoodsCurQuantity();
									} else {
										lingliaoDetail += ","
												+ gck.getGoodsLotId() + ":"
												+ gck.getGoodsCurQuantity();
									}
									pro.setLingliaoDetail(lingliaoDetail);

									sell
											.setSellCount(gck
													.getGoodsCurQuantity());
									listSell.add(sell);
									shifa -= gck.getGoodsCurQuantity();
									gck.setGoodsCurQuantity(0f);
									totalDao.update(gck);
									totalDao.save(sell);
								}
							} else {// 单位不一致或者有支数
								if (gck.getGoodsCurQuantity() >= shifa
										&& shifa != 0) {
									// 记录材料批次和数量
									String lingliaoDetail = pro
											.getLingliaoDetail();
									if (lingliaoDetail == null) {
										lingliaoDetail = gck.getGoodsLotId()
												+ ":" + shifa;
									} else {
										lingliaoDetail += ","
												+ gck.getGoodsLotId() + ":"
												+ shifa;
									}
									pro.setLingliaoDetail(lingliaoDetail);

									sell.setSellCount(shifa);
									gck.setGoodsCurQuantity(gck
											.getGoodsCurQuantity()
											- shifa);
									if (gck.getGoodsCurQuantity() < 0) {
										sendMessage(gck);
										gck.setGoodsCurQuantity(0f);
									}
									shifa = 0f;
								} else if (shifa != 0) {
									sell
											.setSellCount(gck
													.getGoodsCurQuantity());
									shifa -= gck.getGoodsCurQuantity();
									gck.setGoodsCurQuantity(0f);
								}
								if (gck.getGoodsZhishu() == null) {
									gck.setGoodsZhishu(0F);
								}
								if (gck.getGoodsZhishu() >= qingling
										&& qingling != 0) {
									sell.setSellZhishu(qingling);
									gck.setGoodsZhishu(gck.getGoodsZhishu()
											- qingling);
									qingling = 0f;
								} else if (qingling != 0) {
									sell.setSellZhishu(gck.getGoodsZhishu());
									qingling -= gck.getGoodsZhishu();
									gck.setGoodsZhishu(0f);
								}

								totalDao.update(gck);
								totalDao.save(sell);
								if (shifa == 0) {
									break;
								}
							}

						}
					}

					if ("barcode".equals(tag)) {// 补料单
						LogoStickers sticker = (LogoStickers) totalDao
								.getObjectById(LogoStickers.class, id);
						sticker.setStatus("已发料");
						totalDao.update(sticker);
					} else {
						Procard oldProcard = byProcardid(pro);
						if (oldProcard.getHascount() == null) {
							oldProcard.setHascount(oldProcard.getKlNumber());
						}
						oldProcard.setStatus("已发料");
						if (oldProcard.getCardId() != null
								&& oldProcard.getCardId() > 0) {
							RunningWaterCard rw = (RunningWaterCard) totalDao
									.getObjectById(RunningWaterCard.class,
											oldProcard.getCardId());
							rw.setReceiveStatus("yes");
							rw.setCardStatus("已发料");
							rw.setOwnUsername(pro.getLingliaoren());
							totalDao.update(rw);
						}
					}
				}

			} else {// 外购件领料
				if (!g.getGoodsMarkId().equals(pro.getMarkId())) {
					if (selected != null && selected.length > 0) {
						boolean b = false;
						for (int goodsId : selected) {
							if (g.getGoodsId().equals(goodsId)) {
								b = true;
								break;
							}
						}
						if (!b) {// 此外购件没有被选中
							continue;
						}
					} else {
						throw new RuntimeException("请至少选中一个");
					}
				}
				Goods goodsData = (Goods) totalDao.getObjectById(Goods.class, g
						.getGoodsId());
				if (wgprocardSonList != null && wgprocardSonList.size() > 0) {
					for (Procard swgon : wgprocardSonList) {
						if (!swgon.getMarkId().equals(
								goodsData.getGoodsMarkId())) {
							continue;
						}
						// 更新外购件hasCount;
						if (swgon.getHascount() == null) {
							swgon.setHascount(swgon.getKlNumber());
						}
						swgon.setHascount(swgon.getHascount()
								- g.getGoodsCurQuantity());
						if (swgon.getHascount() < 0) {
							// 发送异常消息bgg
							AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
									"件号:" + swgon.getMarkId() + "批次:"
											+ swgon.getSelfCard()
											+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为"
											+ Util.getDateTime(), "2");
							swgon.setHascount(0f);
						}
						if (swgon.getHascount() == 0) {
							swgon.setStatus("完成");
						}
						totalDao.update(swgon);
						// 同步备料通知信息
						List<ProcardMaterial> pmList = totalDao
								.query(
										"from ProcardMaterial where procardId=? and (lingliaoStatus is null or lingliaoStatus!='已领')",
										swgon.getId());
						if (pmList != null && pmList.size() > 0) {
							for (ProcardMaterial pm : pmList) {
								pm.setLingliaoStatus("已领");
								totalDao.update(pm);
								Float noylcount = (Float) totalDao
										.getObjectByCondition(
												"select count(id) from ProcardMaterial "
														+ "where (lingliaoStatus is null or lingliaoStatus!='已领') and headId=?",
												pm.getHeadId());
								if (noylcount == null || noylcount == 0) {
									ProcardMaterialHead pmHead = (ProcardMaterialHead) totalDao
											.getObjectById(
													ProcardMaterialHead.class,
													pm.getHeadId());
									if (pmHead != null) {
										pmHead.setLingliaoStatus("已领");
										totalDao.update(pmHead);
									}
								}
							}
						}
						if (swgon.getMarkId()
								.equals(goodsData.getGoodsMarkId())) {
							String lingliaoDetail = swgon.getLingliaoDetail();
							if (lingliaoDetail == null) {
								lingliaoDetail = goodsData.getGoodsLotId()
										+ ":" + g.getGoodsCurQuantity();
							} else {
								lingliaoDetail += ","
										+ goodsData.getGoodsLotId() + ":"
										+ g.getGoodsCurQuantity();
							}
							swgon.setLingliaoDetail(lingliaoDetail);
							totalDao.update(swgon);
							break;
						}
					}
					for (Procard procard : wgprocardSonList) {
						if (procard.getHascount() == null) {
							procard.setHascount(procard.getKlNumber());
						}
						float totalHasCount2 = (procard.getFilnalCount() - procard
								.getHascount())
								* procard.getQuanzi1() / procard.getQuanzi2();
						if (totalHasCount2 < lasttotalCount) {
							lasttotalCount = totalHasCount2;
						}
						totalDao.update(procard);
					}
				}
				sell.setSellCount(g.getGoodsCurQuantity());
				goodsData.setGoodsCurQuantity(goodsData.getGoodsCurQuantity()
						- g.getGoodsCurQuantity());
				if (goodsData.getGoodsCurQuantity() < 0) {
					sendMessage(goodsData);
					goodsData.setGoodsCurQuantity(0f);
				}
				// 添加外购件在制品数量
				if (g.getGoodsMarkId().equals(pro.getMarkId())) {
					// 与领料件号相同，说明是半成品领料，在制品已在之前处理。
					// 同步备料通知信息
					List<ProcardMaterial> pmList = totalDao
							.query(
									"from ProcardMaterial where procardId=? and (lingliaoStatus is null or lingliaoStatus!='已领')",
									pro.getId());
					if (pmList != null && pmList.size() > 0) {
						for (ProcardMaterial pm : pmList) {
							pm.setLingliaoStatus("已领");
							totalDao.update(pm);
							Float noylcount = (Float) totalDao
									.getObjectByCondition(
											"select count(id) from ProcardMaterial "
													+ "where (lingliaoStatus is null or lingliaoStatus!='已领') and headId=?",
											pm.getHeadId());
							if (noylcount == null || noylcount == 0) {
								ProcardMaterialHead pmHead = (ProcardMaterialHead) totalDao
										.getObjectById(
												ProcardMaterialHead.class, pm
														.getHeadId());
								if (pmHead != null) {
									pmHead.setLingliaoStatus("已领");
									totalDao.update(pmHead);
								}
							}
						}
					}
					// 处理材料批次
					String lingliaoDetail = pro.getLingliaoDetail();
					if (lingliaoDetail == null) {
						lingliaoDetail = goodsData.getGoodsLotId() + ":"
								+ g.getGoodsCurQuantity();
					} else {
						lingliaoDetail += "," + goodsData.getGoodsLotId() + ":"
								+ g.getGoodsCurQuantity();
					}
					pro.setLingliaoDetail(lingliaoDetail);
				} else {
					String hqlzaizhi = "from Goods where goodsMarkId='"
							+ g.getGoodsMarkId()
							+ "' and goodsLotId='"
							+ pro.getSelfCard()
							+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用') ";
					List listzizhi = totalDao.find(hqlzaizhi);
					if (listzizhi != null && listzizhi.size() > 0) {
						Goods g1 = (Goods) listzizhi.get(0);
						g1.setGoodsCurQuantity(g1.getGoodsCurQuantity()
								+ g.getGoodsCurQuantity());
						if (g1.getGoodsCurQuantity() < 0) {
							sendMessage(g1);
							g1.setGoodsCurQuantity(0f);
						}
						totalDao.update(g1);
					} else {
						Goods gg = new Goods();
						gg.setGoodsMarkId(g.getGoodsMarkId());
						gg.setBanBenNumber(pro.getBanBenNumber());
						gg.setGoodsLotId(pro.getSelfCard());
						gg.setGoodsFullName(g.getGoodsFullName());
						gg.setBanBenNumber(pro.getBanBenNumber());
						gg.setGoodsClass("在制品");
						gg.setGoodsCurQuantity(g.getGoodsCurQuantity());
						if (gg.getGoodsCurQuantity() < 0) {
							sendMessage(gg);
							gg.setGoodsCurQuantity(0f);
						}
						gg.setGoodsUnit(pro.getUnit());
						totalDao.save(gg);

					}

					GoodsStore gs = new GoodsStore();// 添加外购件在制品入库记录
					gs.setGoodsStoreMarkId(g.getGoodsMarkId());// 件号
					gs.setBanBenNumber(pro.getBanBenNumber());
					gs.setGoodsStoreGoodsName(g.getGoodsFullName());// 名称
					gs.setGoodsStoreLot(pro.getSelfCard());// 批次
					// gs.setGoodsStoreCount((float) Math.floor(g
					// .getGoodsCurQuantity()));// 数量
					gs.setGoodsStoreCount(g.getGoodsCurQuantity());// 数量
					gs.setPrintStatus("YES");
					List<String> totalMarkId = totalDao.query(
							"select markId from Procard where id=?", pro
									.getRootId());
					if (totalMarkId.size() > 0) {
						gs.setGoodsStoreProMarkId(totalMarkId.get(0));// 总成件号
					}
					gs.setGoodsStoreWarehouse("在制品");// 库别
					Users jingban = Util.getLoginUser();
					gs.setGoodsStoreCharger(jingban.getName());// 经办人
					gs.setStyle("正常（成品）");// 入库类型
					if (us != null) {// 负责人
						gs.setGoodsStorePerson(us.getName());
					} else {
						gs.setGoodsStorePerson(pro.getLingliaoren());
					}
					gs.setGoodsStoreDate(DateUtil.formatDate(new Date(),
							"yyyy-MM-dd"));
					gs.setGoodsStoreUnit(pro.getUnit());// 单位
					totalDao.save(gs);
					totalDao.update(goodsData);
				}
			}
			if (null == sell.getSellFormat()) {
				sell.setSellFormat("");
			}
			totalDao.save(sell);
			listSell.add(sell);
		}
		if ("barcode".equals(tag)) {// 补料单
			LogoStickers sticker = (LogoStickers) totalDao.getObjectById(
					LogoStickers.class, id);
			sticker.setStatus("已发料");
			totalDao.update(sticker);
		} else {
			if ("已发卡".equals(pro.getStatus()))
				pro.setStatus("已发料");
			if (pro.getProcardStyle().equals("组合")
					|| pro.getProcardStyle().equals("总成")) {
				// 计算剩余未领数量
				Float count = pro.getFilnalCount() - pro.getKlNumber()
						+ pro.getHascount();
				// String wghql = "update Procard set "
				// + status
				// + " hascount=("
				// + count
				// +
				// "*quanzi2/quanzi1) where fatherId=? and procardStyle='外购'  and (needProcess <> 'yes' or needProcess is null)";
				// 如果有周转卡id(即有卡操作)，则更新
				if (pro.getCardId() != null) {
					RunningWaterCard rw = (RunningWaterCard) totalDao
							.getObjectById(RunningWaterCard.class, pro
									.getCardId());
					rw.setReceiveStatus("yes");
					rw.setCardStatus("已发料");
					rw.setOwnUsername(pro.getLingliaoren());
					totalDao.update(rw);
				}
			}
		}
		totalDao.update(pro);
		// 处理工序的可领数量
		// String hql_update =
		// "update ProcessInfor set totalCount=? where procard.id=?";
		// totalDao.createQueryUpdate(hql_update, null, (pro.getKlNumber() - pro
		// .getHascount()), pro.getId());
		String hql_update = "from ProcessInfor where procard.id=? order by processNO";
		List<ProcessInfor> sonProcessinfor = totalDao.query(hql_update, pro
				.getId());
		Integer maxProcessNo = -1;
		for (int i = 0; i < sonProcessinfor.size(); i++) {
			ProcessInfor processInfor = sonProcessinfor.get(i);
			if (i == (sonProcessinfor.size() - 1)
					&& ((pro.getKlNumber() - pro.getHascount()) > lasttotalCount)) {// 部分领料最后一道工序的可领数量为最小minNumber-
				processInfor.setTotalCount((float) Math.floor(lasttotalCount));
				maxProcessNo = processInfor.getProcessNO();// 最大工序号
			} else {
				processInfor.setTotalCount((float) Math.floor(pro.getKlNumber()
						- pro.getHascount()));
			}
			totalDao.update(processInfor);
		}
		/********************************** 如果第一道工序就是外委，则生成外委采购计划 *****************************************/

		/**** 生成外委工序计划 ***/
		String nextWwhql = "from ProcessInfor where  procard.id=? order by processNO";
		List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
				.query(nextWwhql, pro.getId());
		if (nextWwProcessInforList.size() > 0) {
			int n = 0;
			WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
			// 查看是否有委外库
			Float wwckCount = (Float) totalDao
					.getObjectByCondition("select count(*) from WareHouse where name='委外库'");
			for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
				if (nextWwProcessInfor != null) {
					if ("外委".equals(nextWwProcessInfor.getProductStyle())
							&& (n == 0 || ("yes").equals(nextWwProcessInfor
									.getProcessStatus()))) {
						// 第一道外委工序和之后的并行外委合并外委
						if (n == 0) {
							wwp.setRootMarkId(pro.getRootMarkId());
							wwp.setRootSelfCard(pro.getRootSelfCard());
							wwp.setOrderNum(pro.getOrderNumber());
							wwp.setYwMarkId(pro.getYwMarkId());
							wwp.setMarkId(pro.getMarkId());
							wwp.setProcessNo(nextWwProcessInfor.getProcessNO()
									+ "");
							wwp.setProName(pro.getProName());
							wwp.setProcessName(nextWwProcessInfor
									.getProcessName());
							wwp.setType("外委");
							wwp.setUnit(pro.getUnit());
							wwp.setBanben(pro.getBanBenNumber());
							wwp.setBanci(pro.getBanci());
							if (nextWwProcessInfor.getProcessNO().equals(
									maxProcessNo)) {
								// 最后一道外委激活数量=下层最小minNumber - 已生成过的外委数量
								// 当beginCount=null时为改版之前数据
								Float wwCount1 = (Float) totalDao
										.getObjectByCondition(
												"select sum(number) from WaigouWaiweiPlan where processNO=? and beginCount is null and procardId=?",
												maxProcessNo, pro.getId());
								// 当beginCount>0时以beginCount为标准
								Float wwCount2 = (Float) totalDao
										.getObjectByCondition(
												"select sum(beginCount) from WaigouWaiweiPlan where processNO=? and beginCount>0 and procardId=?",
												maxProcessNo, pro.getId());
								Float beginCount = lasttotalCount - wwCount1
										- wwCount2;
								if (beginCount <= 0) {
									beginCount = 0f;
								}
								wwp.setBeginCount(beginCount);
								wwp.setNumber(beginCount);
							} else {
								wwp.setBeginCount(getCount);
								wwp.setNumber(getCount);
							}

							wwp.setAddTime(Util.getDateTime());
							wwp.setJihuoTime(Util.getDateTime());
							wwp.setShArrivalTime(pro.getNeedFinalDate());// 应到货时间在采购确认通知后计算
							wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
							String wwNumber = "";
							String before = null;
							Integer bIndex = 10;
							before = "ww" + Util.getDateTime("yyyyMMdd");
							Integer maxNo = 0;
							String maxNumber = (String) totalDao
									.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
											+ before + "%'");
							if (maxNumber != null) {
								String wwnum = maxNumber.substring(bIndex,
										maxNumber.length());
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
							wwNumber = before + String.format("%03d", maxNo);
							wwp.setPlanNumber(wwNumber);// 采购计划编号
							wwp.setSelfCard(pro.getSelfCard());// 批次
							// wwp.setGysId(nextWwProcessInfor.getZhuserId());//
							// 供应商id
							// wwp.setGysName(nextWwProcessInfor.getGys());//
							// 供应商名称
							wwp.setAllJiepai(nextWwProcessInfor.getAllJiepai());// 单件总节拍
							wwp.setDeliveryDuration(nextWwProcessInfor
									.getDeliveryDuration());// 耽误时长
							wwp.setSingleDuration(pro.getSingleDuration());// 单班时长(工作时长)
							wwp.setProcardId(pro.getId());
							wwp.setProcard(pro);
							if (wwckCount != null && wwckCount > 0) {
								Float wwCount = pro.getFilnalCount();
								wwp.setStatus("待在制入库");
								// 在制品待入库
								if (pro.getZaizhiApplyZk() == null) {
									pro.setZaizhiApplyZk(0f);
								}
								if (pro.getZaizhizkCount() == null) {
									pro.setZaizhizkCount(0f);
								}
								if (pro.getKlNumber() == null) {
									pro.setKlNumber(pro.getFilnalCount());
								}
								if (pro.getHascount() == 0) {
									pro.setHascount(pro.getKlNumber());
								}
								// procard.getKlNumber()-procard.getHascount()=已生产数量
								// 可转库数量=已生产数量-已转库数量-转库申请中数量
								pro.setZaizhikzkCount(pro.getKlNumber()
										- pro.getHascount()
										- pro.getZaizhizkCount()
										- pro.getZaizhiApplyZk());
								if (pro.getZaizhikzkCount() >= wwCount) {
									pro.setZaizhiApplyZk(pro.getZaizhiApplyZk()
											+ wwCount);
									String orderNum = (String) totalDao
											.getObjectByCondition(
													"select orderNumber from Procard where id=?",
													pro.getRootId());
									// 成品待入库
									GoodsStore goodsStore2 = new GoodsStore();
									goodsStore2.setNeiorderId(orderNum);
									goodsStore2.setGoodsStoreMarkId(pro
											.getMarkId());
									goodsStore2.setBanBenNumber(pro
											.getBanBenNumber());
									goodsStore2.setGoodsStoreLot(pro
											.getSelfCard());
									goodsStore2.setGoodsStoreGoodsName(pro
											.getProName());
									goodsStore2
											.setApplyTime(Util.getDateTime());
									goodsStore2
											.setGoodsStoreArtsCard((String) totalDao
													.getObjectByCondition(
															"select selfCard from Procard where id=?",
															pro.getRootId()));
									goodsStore2.setGoodsStorePerson(Util
											.getLoginUser().getName());
									goodsStore2.setStatus("待入库");
									goodsStore2.setStyle("半成品转库");
									goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
									goodsStore2.setNeiorderId(pro
											.getOrderNumber());
									goodsStore2.setWaiorderId(pro
											.getOutOrderNum());
									goodsStore2.setProcardId(pro.getId());
									// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
									// 区名
									// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
									// 库位
									goodsStore2
											.setGoodsStoreUnit(pro.getUnit());
									goodsStore2.setGoodsStoreCount(wwCount);
									totalDao.update(pro);
									totalDao.save(goodsStore2);
								} else {
									throw new RuntimeException("对不起超过可申请数量("
											+ pro.getZaizhikzkCount() + ")");
								}
							} else {
								wwp.setStatus("待激活");
							}
							totalDao.save(wwp);
						} else {
							wwp.setProcessNo(wwp.getProcessNo() + ";"
									+ nextWwProcessInfor.getProcessNO());
							wwp.setProcessName(wwp.getProcessName() + ";"
									+ nextWwProcessInfor.getProcessName());
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
										+ "')  order by hsPrice", wwp
										.getMarkId(), wwp.getProcessNo());
				if (price != null) {
					wwp.setPriceId(price.getId());
					wwp.setGysId(price.getGysId());
					ZhUser zhUser = (ZhUser) totalDao.getObjectById(
							ZhUser.class, price.getGysId());
					wwp.setGysName(zhUser.getName());
					wwp.setUserCode(zhUser.getUsercode());
					wwp.setUserId(zhUser.getUserid());
					totalDao.update(wwp);
				}
			}
			if (wwp.getId() != null) {
				// 匹配供应商
				Price price = (Price) totalDao
						.getObjectByCondition(
								"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
										+ Util.getDateTime()
										+ "')  order by hsPrice", wwp
										.getMarkId(), wwp.getProcessNo());
				if (price != null) {
					wwp.setPriceId(price.getId());
					wwp.setGysId(price.getGysId());
					ZhUser zhUser = (ZhUser) totalDao.getObjectById(
							ZhUser.class, price.getGysId());
					wwp.setGysName(zhUser.getName());
					wwp.setUserCode(zhUser.getUsercode());
					wwp.setUserId(zhUser.getUserid());
					totalDao.update(wwp);
				}
			}
		}

		return listSell;
	}

	public void unCreateWaiWei() {
		/**
		 * 查看漏掉生成外委计划的的工序
		 */
		String nextWwhql = "from ProcessInfor where  procard.id in (select id from Procard where status in ('已发料','领工序')) and productStyle='外委' and status !='完成'"
				+ " and  procard.markId+convert(varchar(100),processNO)+procard.selfCard not in(select markId+processNo+selfCard from WaigouWaiweiPlan where processNo is not null) order by processNO";
		List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
				.query(nextWwhql);
		List<Integer> needCreateIdList = new ArrayList<Integer>();
		List<ProcessInfor> needCreateProcessList = new ArrayList<ProcessInfor>();
		if (nextWwProcessInforList.size() > 0) {
			int i = 0;
			for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
				if (nextWwProcessInfor != null) {
					if ("外委".equals(nextWwProcessInfor.getProductStyle())) {
						// 查询上道工序是完成的工序或者是不存在或者是需要生成计划的外委工序
						ProcessInfor upProcess = (ProcessInfor) totalDao
								.getObjectByCondition(
										"from ProcessInfor where  procard.id=? and processNO<? order by processNO desc",
										nextWwProcessInfor.getProcard().getId(),
										nextWwProcessInfor.getProcessNO());
						boolean b = false;
						if (upProcess == null) {// 判断这道工序是否应该生成外委采购计划
							b = true;
						} else if (upProcess.getStatus().equals("完成")) {
							b = true;
						} else if (needCreateIdList.contains(upProcess.getId())) {
							b = true;
						}
						if (b) {
							needCreateIdList.add(nextWwProcessInfor.getId());
							needCreateProcessList.add(nextWwProcessInfor);
							WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
							wwp.setRootMarkId(nextWwProcessInfor.getProcard()
									.getRootMarkId());
							wwp.setRootSelfCard(nextWwProcessInfor.getProcard()
									.getRootSelfCard());
							wwp.setOrderNum(nextWwProcessInfor.getProcard()
									.getOrderNumber());
							wwp.setYwMarkId(nextWwProcessInfor.getProcard()
									.getYwMarkId());
							wwp.setBanben(nextWwProcessInfor.getProcard()
									.getBanBenNumber());
							wwp.setBanci(nextWwProcessInfor.getProcard()
									.getBanci());
							wwp.setMarkId(nextWwProcessInfor.getProcard()
									.getMarkId());
							wwp.setProcessNo(nextWwProcessInfor.getProcessNO()
									+ "");
							wwp.setProName(nextWwProcessInfor.getProcard()
									.getProName());
							wwp.setProcessName(nextWwProcessInfor
									.getProcessName());
							wwp.setType("外委");
							wwp.setNumber(nextWwProcessInfor.getProcard()
									.getKlNumber()
									- nextWwProcessInfor.getProcard()
											.getHascount());
							wwp.setBeginCount(nextWwProcessInfor.getProcard()
									.getKlNumber()
									- nextWwProcessInfor.getProcard()
											.getHascount());
							wwp.setAddTime(Util.getDateTime());
							wwp.setJihuoTime(Util.getDateTime());
							wwp.setShArrivalTime(nextWwProcessInfor
									.getProcard().getNeedFinalDate());// 应到货时间在采购确认通知后计算
							wwp.setStatus("待激活");
							wwp.setUnit(nextWwProcessInfor.getProcard()
									.getUnit());
							wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
							String wwNumber = "";
							String before = null;
							Integer bIndex = 10;
							before = "ww" + Util.getDateTime("yyyyMMdd");

							Integer maxNo = 0;
							String maxNumber = (String) totalDao
									.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
											+ before + "%'");
							if (maxNumber != null) {
								String wwnum = maxNumber.substring(bIndex,
										maxNumber.length());
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
							wwNumber = before + String.format("%03d", maxNo);
							wwp.setPlanNumber(wwNumber);// 采购计划编号
							wwp.setSelfCard(nextWwProcessInfor.getProcard()
									.getSelfCard());// 批次
							// wwp.setGysId(nextWwProcessInfor.getZhuserId());//
							// 供应商id
							// wwp.setGysName(nextWwProcessInfor.getGys());//
							// 供应商名称
							wwp.setAllJiepai(nextWwProcessInfor.getAllJiepai());// 单件总节拍
							wwp.setDeliveryDuration(nextWwProcessInfor
									.getDeliveryDuration());// 耽误时长
							wwp.setSingleDuration(nextWwProcessInfor
									.getProcard().getSingleDuration());// 单班时长(工作时长)
							wwp.setProcardId(nextWwProcessInfor.getProcard()
									.getId());
							wwp.setProcard(nextWwProcessInfor.getProcard());
							totalDao.save(wwp);
						}
					} else {
						// i++;
						// int k = 0;
						// if (i > k) {// 之前k道自制工序不考虑
						// break;
						// }
					}
				} else {
					break;
				}
			}
		}
		System.out.println(needCreateIdList.size());
		// List<Procard> procardList = totalDao
		// .query("from Procard where id=41893");
		// .query("from Procard where status='已发料' and procardStyle='自制'");
		// List<Procard> procardList2 = new ArrayList<Procard>();
		// if (procardList.size() > 0) {
		// for (Procard p : procardList) {
		// String nextWwhql =
		// "from ProcessInfor where  procard.id=? order by processNO";
		// ProcessInfor process=(ProcessInfor)
		// totalDao.getObjectByCondition(nextWwhql, p.getId());
		// if(process!=null&&"外委".equals(process.getProductStyle())){
		// procardList2.add(p);
		// }
		/********************************** 生成外委采购计划 *****************************************/

		// /**** 生成外委工序计划 ***/
		// String nextWwhql =
		// "from ProcessInfor where  procard.id=? and processNO>=40 order by processNO";
		// List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>)
		// totalDao
		// .query(nextWwhql, p.getId());
		// if (nextWwProcessInforList.size() > 0) {
		// int i = 0;
		// for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
		// if (nextWwProcessInfor != null) {
		// if ("外委".equals(nextWwProcessInfor
		// .getProductStyle())) {
		// WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
		// wwp.setMarkId(p.getMarkId());
		// wwp.setProcessNo(nextWwProcessInfor
		// .getProcessNO()
		// + "");
		// wwp.setProName(p.getProName());
		// wwp.setProcessName(nextWwProcessInfor
		// .getProcessName());
		// wwp.setType("外委");
		// wwp
		// .setNumber(p.getKlNumber()
		// - p.getHascount());
		// wwp.setAddTime(Util.getDateTime());
		// wwp.setJihuoTime(Util.getDateTime());
		// wwp.setShArrivalTime(p.getNeedFinalDate());// 应到货时间在采购确认通知后计算
		// wwp.setStatus("待激活");
		// wwp
		// .setCaigouMonth(Util
		// .getDateTime("yyyy-MM月"));// 采购月份
		// wwp.setPlanNumber("ww201412001");// 采购计划编号
		// wwp.setSelfCard(p.getSelfCard());// 批次
		// wwp.setGysId(nextWwProcessInfor.getZhuserId());// 供应商id
		// wwp.setGysName(nextWwProcessInfor.getGys());// 供应商名称
		// wwp.setAllJiepai(nextWwProcessInfor
		// .getAllJiepai());// 单件总节拍
		// wwp.setDeliveryDuration(nextWwProcessInfor
		// .getDeliveryDuration());// 耽误时长
		// wwp.setSingleDuration(p.getSingleDuration());// 单班时长(工作时长)
		// wwp.setProcardId(p.getId());
		// wwp.setProcard(p);
		// totalDao.save(wwp);
		// } else {
		// i++;
		// int k = 0;
		// if (i > k) {// 之前k道自制工序不考虑
		// break;
		// }
		// }
		// } else {
		// break;
		// }
		// }
		// }
		// }
		// }
		// System.out.println(procardList2.size());

	}

	/**
	 * 外购件领料
	 */
	@SuppressWarnings("unchecked")
	public List saveSellList2(Integer id, Procard pro, List<Goods> li,
			String tag, float getCount) {
		String hql1 = "from Users where cardId=? and onWork not in('离职','离职中','内退','病休')";
		Users us = (Users) totalDao.getObjectByCondition(hql1, pro
				.getLingliaoren());// 领料人
		if (us == null) {
			throw new RuntimeException("领料人不存在,领料失败!");
		} else {
			Float czcount = (Float) totalDao
					.getObjectByCondition(
							"select count(id) from ProcessinforPeople where procard.id=? and userId =?",
							pro.getId(), us.getId());
			if (czcount == null || czcount == 0) {
				throw new RuntimeException("领料人对此零件无领料权限,领料失败!");
			}
		}
		float totalHasCount = 0f;// 剩余未领数量（总成和组合使用,以下层外购件对应已领最大数量为标准）
		float zaizhiCount = 0f;
		float deletehasCount = 0f;
		if (!pro.getProcardStyle().equals("总成")) {// 总成无在制品
			zaizhiCount = getZaiZhiCount(pro.getId());// 计算可用在制品
		}
		float addZaihi = 0f;
		if (zaizhiCount >= getCount) {// 在制品够
			deletehasCount = getCount;
		} else {
			deletehasCount = zaizhiCount;
			addZaihi = getCount - zaizhiCount;
			String hqlzaizhi = "from Goods where goodsMarkId='"
					+ pro.getMarkId()
					+ "' and goodsLotId='"
					+ pro.getSelfCard()
					+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用') ";
			List listzizhi = totalDao.find(hqlzaizhi);
			if (listzizhi != null && listzizhi.size() > 0) {
				Goods g1 = (Goods) listzizhi.get(0);
				g1.setGoodsCurQuantity(g1.getGoodsCurQuantity() + addZaihi);

				if (g1.getGoodsCurQuantity() < 0) {
					sendMessage(g1);
					g1.setGoodsCurQuantity(0f);
				}
				totalDao.update(g1);
			} else {
				Goods gg = new Goods();
				gg.setGoodsMarkId(pro.getMarkId());
				gg.setGoodsLotId(pro.getSelfCard());
				gg.setGoodsFullName(pro.getProName());
				gg.setBanBenNumber(pro.getBanBenNumber());
				gg.setGoodsClass("在制品");
				gg.setGoodsCurQuantity(addZaihi);
				if (gg.getGoodsCurQuantity() < 0) {
					sendMessage(gg);
					gg.setGoodsCurQuantity(0f);
				}
				gg.setGoodsUnit(pro.getUnit());
				totalDao.save(gg);

			}
			if (addZaihi > 0) {// 添加在制品入库记录
				GoodsStore gs = new GoodsStore();
				gs.setGoodsStoreMarkId(pro.getMarkId());// 件号
				gs.setBanBenNumber(pro.getBanBenNumber());
				gs.setGoodsStoreGoodsName(pro.getProName());// 名称
				gs.setGoodsStoreLot(pro.getSelfCard());// 批次
				gs.setGoodsStoreCount((float) Math.floor(addZaihi));// 数量
				gs.setPrintStatus("YES");
				List<String> totalMarkId = totalDao.query(
						"select markId from Procard where id=?", pro
								.getRootId());
				if (totalMarkId.size() > 0) {
					gs.setGoodsStoreProMarkId(totalMarkId.get(0));// 总成件号
				}
				gs.setGoodsStoreWarehouse("在制品");// 库别
				Users jingban = Util.getLoginUser();
				gs.setGoodsStoreCharger(jingban.getName());// 经办人
				gs.setStyle("正常（成品）");// 入库类型
				gs.setGoodsStorePerson(us.getName());
				gs.setGoodsStoreDate(DateUtil.formatDate(new Date(),
						"yyyy-MM-dd"));
				gs.setGoodsStoreUnit(pro.getUnit());// 单位
				totalDao.save(gs);

			}
		}
		String lingliaoPeople = pro.getLingliaoren();
		pro = byProcardid(pro.getId());
		pro.setLingliaoren(lingliaoPeople);
		if (pro.getHascount() == null) {
			pro.setHascount(pro.getKlNumber());
		}
		pro.setHascount(pro.getHascount() - getCount);
		if (pro.getHascount() < 0) {
			// 发送异常消息bgg
			AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
					+ pro.getMarkId() + "批次:" + pro.getSelfCard()
					+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为" + Util.getDateTime(),
					"2");
			pro.setHascount(0f);
		}

		List listSell = new ArrayList();
		Users user = (Users) Util.getLoginUser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		List<Procard> wgprocardSonList = totalDao.query(
				"from Procard where procard.id=? and procardStyle='外购'", pro
						.getId());
		for (Goods g : li) {
			if (g.getGoodsPosition() != null
					&& g.getGoodsPosition().equals("在制品")) {
				continue;
			}
			Sell sell = new Sell();
			sell.setSellArtsCard(pro.getSelfCard());
			sell.setSellSupplier(g.getGoodsSupplier());
			sell.setSellFormat(g.getGoodsFormat());
			sell.setSellLot(g.getGoodsLotId());
			sell.setSellMarkId(g.getGoodsMarkId());
			sell.setSellAdminName(user.getName());
			sell.setSellGoods(g.getGoodsFullName());
			sell.setSellDate(sdf2.format(new Date()));
			sell.setSellTime(sdf.format(new Date()));
			sell.setSellWarehouse(g.getGoodsClass());
			// ---------------------------------------------
			/***
			 * TODO //更具卡号查选
			 */
			sell.setSellCharger(us.getName());
			// -----------------------------------------
			sell.setSellUnit(g.getGoodsUnit());
			if ("总成".equals(pro.getProcardStyle())) {
				sell.setSellProMarkId(pro.getMarkId());
			} else {
				int prorootId = pro.getRootId();
				sell.setSellProMarkId((byProcardid(prorootId)).getMarkId());
			}
			sell.setPrintStatus("NO");
			sell.setStyle("刷卡出库");
			sell.setSellPeople("待确认");
			sell.setSellAdminName(user.getName());
			Goods goodsData = (Goods) totalDao.getObjectById(Goods.class, g
					.getGoodsId());
			// 数量处理
			if ("自制".equals(pro.getProcardStyle())) {// 原材料领料
				if (g.getGoodsUnit().equals(pro.getYuanUnit())) {// 正常单位领出
					sell.setSellCount(g.getGoodsCurQuantity());
					goodsData.setGoodsCurQuantity(goodsData
							.getGoodsCurQuantity()
							- g.getGoodsCurQuantity());
					if (goodsData.getGoodsCurQuantity() < 0) {
						sendMessage(goodsData);
						goodsData.setGoodsCurQuantity(0f);
					}
				} else {// 换算出领
					sell.setSellZhishu(g.getGoodsZhishu());
					float sellCount = goodsData.getGoodsCurQuantity()
							* g.getGoodsZhishu() / goodsData.getGoodsZhishu();
					sell.setSellCount(sellCount);

					if (g.getGoodsZhishu() == goodsData.getGoodsZhishu()) {
						goodsData.setGoodsCurQuantity(0f);
					} else {
						goodsData.setGoodsCurQuantity(goodsData
								.getGoodsCurQuantity()
								- sellCount);
						if (goodsData.getGoodsCurQuantity() < 0) {
							sendMessage(goodsData);
							goodsData.setGoodsCurQuantity(0f);
						}
					}
					goodsData.setGoodsZhishu(goodsData.getGoodsZhishu()
							- g.getGoodsZhishu());
				}

			} else {// 外购件领料
				if (wgprocardSonList != null && wgprocardSonList.size() > 0) {// 记录材料批次
					for (Procard swgon : wgprocardSonList) {
						// 同步备料通知信息
						List<ProcardMaterial> pmList = totalDao
								.query(
										"from ProcardMaterial where procardId=? and (lingliaoStatus is null or lingliaoStatus!='已领')",
										swgon.getId());
						if (pmList != null && pmList.size() > 0) {
							for (ProcardMaterial pm : pmList) {
								pm.setLingliaoStatus("已领");
								totalDao.update(pm);
								Float noylcount = (Float) totalDao
										.getObjectByCondition(
												"select count(id) from ProcardMaterial "
														+ "where (lingliaoStatus is null or lingliaoStatus!='已领') and headId=?",
												pm.getHeadId());
								if (noylcount == null || noylcount == 0) {
									ProcardMaterialHead pmHead = (ProcardMaterialHead) totalDao
											.getObjectById(
													ProcardMaterialHead.class,
													pm.getHeadId());
									if (pmHead != null) {
										pmHead.setLingliaoStatus("已领");
										totalDao.update(pmHead);
									}
								}
							}
						}
						if (swgon.getMarkId()
								.equals(goodsData.getGoodsMarkId())) {
							String lingliaoDetail = swgon.getLingliaoDetail();
							if (lingliaoDetail == null) {
								lingliaoDetail = goodsData.getGoodsLotId()
										+ ":" + g.getGoodsCurQuantity();
							} else {
								lingliaoDetail += ","
										+ goodsData.getGoodsLotId() + ":"
										+ g.getGoodsCurQuantity();
							}
							swgon.setLingliaoDetail(lingliaoDetail);
							totalDao.update(swgon);
							break;
						}
					}
				}
				sell.setSellCount(g.getGoodsCurQuantity());
				if (goodsData.getGoodsCurQuantity() < g.getGoodsCurQuantity()) {
					throw new RuntimeException("对不起,"
							+ goodsData.getGoodsLotId() + "批次的外购件"
							+ goodsData.getGoodsMarkId() + "数量不足!");
				}
				goodsData.setGoodsCurQuantity(goodsData.getGoodsCurQuantity()
						- g.getGoodsCurQuantity());
				if (goodsData.getGoodsCurQuantity() < 0) {
					sendMessage(goodsData);
					goodsData.setGoodsCurQuantity(0f);
				}
				// 添加外购件在制品数量
				String hqlzaizhi = "from Goods where goodsMarkId='"
						+ g.getGoodsMarkId()
						+ "' and goodsLotId='"
						+ pro.getSelfCard()
						+ "' and goodsClass='在制品' and (fcStatus is null or fcStatus='可用')";
				List listzizhi = totalDao.find(hqlzaizhi);
				if (listzizhi != null && listzizhi.size() > 0) {
					Goods g1 = (Goods) listzizhi.get(0);
					g1.setGoodsCurQuantity(g1.getGoodsCurQuantity()
							+ (float) Math.floor(g.getGoodsCurQuantity()));
					if (g1.getGoodsCurQuantity() < 0) {
						sendMessage(g1);
						g1.setGoodsCurQuantity(0f);
					}

					totalDao.update(g1);
				} else {
					Goods gg = new Goods();
					gg.setGoodsMarkId(g.getGoodsMarkId());
					gg.setGoodsLotId(pro.getSelfCard());
					gg.setGoodsFullName(g.getGoodsFullName());
					gg.setBanBenNumber(pro.getBanBenNumber());
					gg.setGoodsClass("在制品");
					gg.setGoodsCurQuantity((float) Math.floor(g
							.getGoodsCurQuantity()));
					if (gg.getGoodsCurQuantity() < 0) {
						sendMessage(gg);
						gg.setGoodsCurQuantity(0f);
					}
					gg.setGoodsUnit(pro.getUnit());
					totalDao.save(gg);

				}

				GoodsStore gs = new GoodsStore();// 添加外购件在制品入库记录
				gs.setGoodsStoreMarkId(g.getGoodsMarkId());// 件号
				gs.setBanBenNumber(pro.getBanBenNumber());
				gs.setGoodsStoreGoodsName(g.getGoodsFullName());// 名称
				gs.setGoodsStoreLot(pro.getSelfCard());// 批次
				gs.setGoodsStoreCount((float) Math.floor(g
						.getGoodsCurQuantity()));// 数量
				gs.setPrintStatus("YES");
				List<String> totalMarkId = totalDao.query(
						"select markId from Procard where id=?", pro
								.getRootId());
				if (totalMarkId.size() > 0) {
					gs.setGoodsStoreProMarkId(totalMarkId.get(0));// 总成件号
				}
				gs.setGoodsStoreWarehouse("在制品");// 库别
				Users jingban = Util.getLoginUser();
				gs.setGoodsStoreCharger(jingban.getName());// 经办人
				gs.setStyle("正常（成品）");// 入库类型
				if (us != null) {// 负责人
					gs.setGoodsStorePerson(us.getName());
				} else {
					gs.setGoodsStorePerson(pro.getLingliaoren());
				}
				gs.setGoodsStoreDate(DateUtil.formatDate(new Date(),
						"yyyy-MM-dd"));
				gs.setGoodsStoreUnit(pro.getUnit());// 单位
				totalDao.save(gs);

			}
			if (null == sell.getSellFormat()) {
				sell.setSellFormat("");
			}
			totalDao.save(sell);
			listSell.add(sell);
			totalDao.update(goodsData);
		}
		float lasttotalCount = pro.getFilnalCount();// 最后一道工序的可领数量
		if ("barcode".equals(tag)) {// 补料单
			LogoStickers sticker = (LogoStickers) totalDao.getObjectById(
					LogoStickers.class, id);
			sticker.setStatus("已发料");
			totalDao.update(sticker);
		} else {
			pro.setStatus("已发料");
			if (pro.getProcardStyle().equals("组合")
					|| pro.getProcardStyle().equals("总成")) {
				// // 计算剩余未领数量
				// Float count = pro.getFilnalCount() - pro.getKlNumber()
				// + pro.getHascount();
				// String wghql = "update Procard set "
				// + status
				// + " hascount=("
				// + count
				// +
				// "*quanzi2/quanzi1) where fatherId=? and procardStyle='外购'  and (needProcess <> 'yes' or needProcess is null)";
				// 更新组合/总成 对应的外购件状态以及剩余数量
				String wghql = "from Procard  where fatherId=? and procardStyle='外购'  and (needProcess <> 'yes' or needProcess is null)";
				List<Procard> sonProcar = totalDao.query(wghql, pro.getId());
				for (Procard procard : sonProcar) {
					// 如果全部领取完成
					// if (count == 0F) {
					// procard.setStatus("完成");
					// }
					// procard.setHascount(count * procard.getQuanzi2()
					// / procard.getQuanzi1());
					float thisCount = 0f;
					for (Goods thisG : li) {// 查询此件号此次要领多少件
						if (thisG.getGoodsMarkId().equals(procard.getMarkId())) {
							thisCount = thisCount + thisG.getGoodsCurQuantity();
						}
					}
					float deletehasCount2 = deletehasCount
							* procard.getQuanzi2() / procard.getQuanzi1();
					if (procard.getHascount() == null) {
						procard.setHascount(procard.getFilnalCount()
								- thisCount - deletehasCount2);
					} else {
						procard.setHascount(procard.getHascount() - thisCount
								- deletehasCount2);
					}
					if (procard.getHascount() < 0) {
						// 发送异常消息bgg
						AlertMessagesServerImpl.addAlertMessages("系统维护异常组",
								"件号:" + pro.getMarkId() + "批次:"
										+ pro.getSelfCard()
										+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为"
										+ Util.getDateTime(), "2");
						procard.setHascount(0f);
					}
					if (procard.getHascount() == 0) {
						procard.setStatus("完成");
					}
					float totalHasCount2 = (procard.getFilnalCount() - procard
							.getHascount())
							* procard.getQuanzi1() / procard.getQuanzi2();
					if (totalHasCount2 < lasttotalCount) {
						lasttotalCount = totalHasCount2;
					}
					if (totalHasCount2 > totalHasCount) {
						totalHasCount = totalHasCount2;
					}
					totalDao.update(procard);
				}
				// 如果有周转卡id(即有卡操作)，则更新
				if (pro.getCardId() != null) {
					RunningWaterCard rw = (RunningWaterCard) totalDao
							.getObjectById(RunningWaterCard.class, pro
									.getCardId());
					rw.setReceiveStatus("yes");
					rw.setCardStatus("已发料");
					rw.setOwnUsername(pro.getLingliaoren());
					totalDao.update(rw);
				}
			}
		}
		pro.setHascount(pro.getKlNumber() - totalHasCount);
		if (pro.getHascount() < 0) {
			// 发送异常消息bgg
			AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
					+ pro.getMarkId() + "批次:" + pro.getSelfCard()
					+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为" + Util.getDateTime(),
					"2");
			pro.setHascount(0f);
		}
		totalDao.update(pro);
		// 处理工序的可领数量
		// String hql_update =
		// "update ProcessInfor set totalCount=? where procard.id=?";
		// totalDao.createQueryUpdate(hql_update, null, (pro.getKlNumber() - pro
		// .getHascount()), pro.getId());
		String hql_update = "from ProcessInfor where procard.id=? order by processNO";
		List<ProcessInfor> sonProcessinfor = totalDao.query(hql_update, pro
				.getId());
		Integer maxNo = -1;
		for (int i = 0; i < sonProcessinfor.size(); i++) {
			ProcessInfor processInfor = sonProcessinfor.get(i);
			if (i == (sonProcessinfor.size() - 1)
					&& ((pro.getKlNumber() - pro.getHascount()) > lasttotalCount)) {// 部分领料最后一道工序的可领数量为最小minNumber-
				processInfor.setTotalCount(lasttotalCount);
				maxNo = processInfor.getProcessNO();// 最大工序号
			} else {
				processInfor.setTotalCount((float) Math.floor(pro.getKlNumber()
						- pro.getHascount()));
			}
			totalDao.update(processInfor);
		}

		/********************************** 如果第一道工序就是外委，则生成外委采购计划 *****************************************/

		/**** 生成外委工序计划 ***/
		String nextWwhql = "from ProcessInfor where  procard.id=? order by processNO";
		List<ProcessInfor> nextWwProcessInforList = (List<ProcessInfor>) totalDao
				.query(nextWwhql, pro.getId());
		if (nextWwProcessInforList.size() > 0) {
			int n = 0;
			WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
			Float wwckCount = (Float) totalDao
					.getObjectByCondition("select count(*) from WareHouse where name='委外库'");
			for (ProcessInfor nextWwProcessInfor : nextWwProcessInforList) {
				if (nextWwProcessInfor != null) {
					if ("外委".equals(nextWwProcessInfor.getProductStyle())
							&& (n == 0 || ("yes").equals(nextWwProcessInfor
									.getProcessStatus()))) {
						// 第一道外委工序和之后的并行外委合并外委
						if (n == 0) {
							wwp.setRootMarkId(pro.getRootMarkId());
							wwp.setRootSelfCard(pro.getRootSelfCard());
							wwp.setOrderNum(pro.getOrderNumber());
							wwp.setYwMarkId(pro.getYwMarkId());
							wwp.setMarkId(pro.getMarkId());
							wwp.setProcessNo(nextWwProcessInfor.getProcessNO()
									+ "");
							wwp.setProcessName(nextWwProcessInfor
									.getProcessName());
							wwp.setProName(pro.getProName());
							wwp.setType("外委");
							wwp.setBeginCount(getCount);
							wwp.setUnit(pro.getUnit());
							wwp.setNumber(getCount);
							// if (nextWwProcessInfor.getProcessNO().equals(
							// maxProcessNo)) {
							// // 最后一道外委激活数量=下层最小minNumber - 已生成过的外委数量
							// // 当beginCount=null时为改版之前数据
							// Float wwCount1 = (Float) totalDao
							// .getObjectByCondition(
							// "select sum(number) from WaigouWaiweiPlan where processNO=? and beginCount is null and procardId=?",
							// maxProcessNo, pro.getId());
							// // 当beginCount>0时以beginCount为标准
							// Float wwCount2 = (Float) totalDao
							// .getObjectByCondition(
							// "select sum(beginCount) from WaigouWaiweiPlan where processNO=? and beginCount>0 and procardId=?",
							// maxProcessNo, pro.getId());
							// Float beginCount = lasttotalCount - wwCount1
							// - wwCount2;
							// if (beginCount <= 0) {
							// beginCount = 0f;
							// }
							// wwp.setBeginCount(beginCount);
							// wwp.setNumber(beginCount);
							// } else {
							// wwp.setBeginCount(getCount);
							// wwp.setNumber(getCount);
							// }
							wwp.setAddTime(Util.getDateTime());
							wwp.setJihuoTime(Util.getDateTime());
							wwp.setShArrivalTime(pro.getNeedFinalDate());// 应到货时间在采购确认通知后计算
							wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
							String wwNumber = "";
							String before = null;
							Integer bIndex = 10;
							before = "ww" + Util.getDateTime("yyyyMMdd");
							Integer maxNo2 = 0;
							String maxNumber = (String) totalDao
									.getObjectByCondition("select max(planNumber) from WaigouOrder where planNumber like '"
											+ before + "%'");
							if (maxNumber != null) {
								String wwnum = maxNumber.substring(bIndex,
										maxNumber.length());
								try {
									Integer maxNum = Integer.parseInt(wwnum);
									if (maxNum > maxNo2) {
										maxNo2 = maxNum;
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							maxNo2++;
							wwNumber = before + String.format("%03d", maxNo2);
							wwp.setPlanNumber(wwNumber);// 采购计划编号
							wwp.setSelfCard(pro.getSelfCard());// 批次
							// wwp.setGysId(nextWwProcessInfor.getZhuserId());//
							// 供应商id
							// wwp.setGysName(nextWwProcessInfor.getGys());//
							// 供应商名称
							wwp.setAllJiepai(nextWwProcessInfor.getAllJiepai());// 单件总节拍
							wwp.setDeliveryDuration(nextWwProcessInfor
									.getDeliveryDuration());// 耽误时长
							wwp.setSingleDuration(pro.getSingleDuration());// 单班时长(工作时长)
							wwp.setProcardId(pro.getId());
							wwp.setProcard(pro);
							if (wwckCount != null && wwckCount > 0) {
								Float wwCount = pro.getFilnalCount();
								wwp.setStatus("待在制入库");
								// 在制品待入库
								if (pro.getZaizhiApplyZk() == null) {
									pro.setZaizhiApplyZk(0f);
								}
								if (pro.getZaizhizkCount() == null) {
									pro.setZaizhizkCount(0f);
								}
								if (pro.getKlNumber() == null) {
									pro.setKlNumber(pro.getFilnalCount());
								}
								if (pro.getHascount() == 0) {
									pro.setHascount(pro.getKlNumber());
								}
								// procard.getKlNumber()-procard.getHascount()=已生产数量
								// 可转库数量=已生产数量-已转库数量-转库申请中数量
								pro.setZaizhikzkCount(pro.getKlNumber()
										- pro.getHascount()
										- pro.getZaizhizkCount()
										- pro.getZaizhiApplyZk());
								if (pro.getZaizhikzkCount() >= wwCount) {
									pro.setZaizhiApplyZk(pro.getZaizhiApplyZk()
											+ wwCount);
									String orderNum = (String) totalDao
											.getObjectByCondition(
													"select orderNumber from Procard where id=?",
													pro.getRootId());
									// 成品待入库
									GoodsStore goodsStore2 = new GoodsStore();
									goodsStore2.setNeiorderId(orderNum);
									goodsStore2.setGoodsStoreMarkId(pro
											.getMarkId());
									goodsStore2.setBanBenNumber(pro
											.getBanBenNumber());
									goodsStore2.setGoodsStoreLot(pro
											.getSelfCard());
									goodsStore2.setGoodsStoreGoodsName(pro
											.getProName());
									goodsStore2
											.setApplyTime(Util.getDateTime());
									goodsStore2
											.setGoodsStoreArtsCard((String) totalDao
													.getObjectByCondition(
															"select selfCard from Procard where id=?",
															pro.getRootId()));
									goodsStore2.setGoodsStorePerson(Util
											.getLoginUser().getName());
									goodsStore2.setStatus("待入库");
									goodsStore2.setStyle("半成品转库");
									goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
									goodsStore2.setNeiorderId(pro
											.getOrderNumber());
									goodsStore2.setWaiorderId(pro
											.getOutOrderNum());
									goodsStore2.setProcardId(pro.getId());
									// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
									// 区名
									// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
									// 库位
									goodsStore2
											.setGoodsStoreUnit(pro.getUnit());
									goodsStore2.setGoodsStoreCount(wwCount);
									totalDao.update(pro);
									totalDao.save(goodsStore2);
								} else {
									throw new RuntimeException("对不起超过可申请数量("
											+ pro.getZaizhikzkCount() + ")");
								}
							} else {
								wwp.setStatus("待激活");
							}
							totalDao.save(wwp);
						} else {
							wwp.setProcessNo(wwp.getProcessNo() + ";"
									+ nextWwProcessInfor.getProcessNO());
							wwp.setProcessName(wwp.getProcessName() + ";"
									+ nextWwProcessInfor.getProcessName());
							totalDao.update(wwp);
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (wwp.getId() != null) {
				// 匹配供应商
				Price price = (Price) totalDao
						.getObjectByCondition(
								"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
										+ Util.getDateTime()
										+ "')  order by hsPrice", wwp
										.getMarkId(), wwp.getProcessNo());
				if (price != null) {
					wwp.setPriceId(price.getId());
					wwp.setGysId(price.getGysId());
					ZhUser zhUser = (ZhUser) totalDao.getObjectById(
							ZhUser.class, price.getGysId());
					wwp.setGysName(zhUser.getName());
					wwp.setUserCode(zhUser.getUsercode());
					wwp.setUserId(zhUser.getUserid());
					totalDao.update(wwp);
				}
			}
		}

		return listSell;
	}

	@Override
	public List<Sell> findOutSellList(Sell sell) {
		if (sell == null) {
			sell = new Sell();
		}
		String sellWarehouse = "";
		if (null != sell.getSellWarehouse()) {
			sellWarehouse = " sellWarehouse = '" + sell.getSellWarehouse()
					+ "'";
		}
		String hql = totalDao.criteriaQueries(sell, sellWarehouse);
		String str = " and  printStatus='NO' and sellWarehouse in("
				+ getWarehouse() + ") order by sellDate desc";
		return totalDao.find(hql + str);
	}

	@Override
	public Object[] findSellByCondition(Sell sell, String startDate,
			String endDate, Integer cpage, Integer pageSize) {
		String s = getWarehouse();
		if (sell == null) {
			sell = new Sell();
		}
		String tishi = "";
		String hql = "";
		String planTotalNum = sell.getPlanTotalNum();
		sell.setPlanTotalNum(null);
		if (null != sell) {
			// 拼接仓区
			String changqu = "";
			String hql_cq = "";
			if (sell.getGoodHouseName() != null
					&& sell.getGoodHouseName().length() > 0) {
				hql_cq = " and goodHouseName in (";
				String[] cangqus = sell.getGoodHouseName().split(",");
				if (cangqus != null && cangqus.length > 0) {
					for (int i = 0; i < cangqus.length; i++) {
						changqu += ",'" + cangqus[i] + "'";
					}
					if (changqu.length() >= 1) {
						changqu = changqu.substring(1);
					}
					hql_cq += changqu;
				}
				hql_cq += ")";
			}
			String hql_kuwei = "";
			if (sell.getSellWarehouse() != null
					&& sell.getSellWarehouse().length() > 0) {

				hql_kuwei = " and sellWarehouse = '" + sell.getSellWarehouse()
						+ "'";
			}
			hql = totalDao.criteriaQueries(sell, null, "goodHouseName",
					"sellWarehouse");
			hql += hql_cq + hql_kuwei;
			if (startDate != null && !"".equals(startDate)) {
				hql += " and sellDate >= '" + startDate + "' ";
			}

			// if (hql.contains("where")) {
			// hql += "  " + sellWarehouse + " and sellWarehouse in(" + s
			// + ") " + " order by sellId desc";
			// } else {
			//
			// hql += " where  " + sellWarehouse + " and sellWarehouse in("
			// + s + ") " + " order by sellId desc";

			if (endDate != null && !"".equals(endDate)) {
				hql += " and sellDate <= '" + endDate + "'";
			}
			// 添加权限
			if (!"".equals(s)) {
				hql += " and sellWarehouse in(" + s + ") "
						+ " order by sellId desc";
			} else {
				hql += " order by sellId desc";
				tishi = "没有查看的权限";
			}
		}
		Object[] procardAarr = new Object[3];

		// 根据物料需求单查询的sql
		if (planTotalNum != null && !"".equals(planTotalNum)) {
			sell.setPlanTotalNum(planTotalNum);
			ManualOrderPlanTotal total = (ManualOrderPlanTotal) totalDao
					.getObjectByCondition(
							"from ManualOrderPlanTotal where totalNum=?",
							planTotalNum);
			if (total != null) {
				StringBuffer buffer = new StringBuffer();
				Set<ManualOrderPlanDetail> details = total.getDetails();
				Iterator<ManualOrderPlanDetail> iterator = details.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					ManualOrderPlanDetail next = iterator.next();
					if (i == 0) {
						buffer.append(next.getId());
					} else {
						buffer.append("," + next.getId());
					}
					i++;
				}
				hql += " and moptId in (" + buffer.toString() + ")";
			}
		}

		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		for (Object obj : list) {
			Sell s1 = (Sell) obj;
			if (s1.getOrderNum() != null && s1.getOrderNum().length() > 0) {
				String orderNum = s1.getOrderNum().replaceAll(",", "<br/>");
				s1.setOrderNum(orderNum);
			}

		}
		procardAarr[0] = count;
		procardAarr[1] = list;
		procardAarr[2] = tishi;
		return procardAarr;
	}

	/**
	 * 接收返回的仓库拼接字符串
	 * 
	 * @return
	 */
	private String getWarehouse() {
		Users user = (Users) Util.getLoginUser();
		String hql = "from WareHouseAuth where usercode = ? and (type is null or type=?)";
		WareHouseAuth a = (WareHouseAuth) totalDao.getObjectByCondition(hql,
				new Object[] { user.getCode(), "仓库" });
		if (a == null) {
			return "";
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

	public Sell getOneSellById(Integer id, String tag) {
		return (Sell) totalDao.getObjectById(Sell.class, id);
	}

	public boolean deleteSellById(Sell sell) {
		if (sell.getGoodsId() != null) {
			Goods goods = (Goods) totalDao.getObjectById(Goods.class, sell
					.getGoodsId());
			if (goods != null) {
				if(sell.getHandwordSellStatus()==null || sell.getHandwordSellStatus().equals("")
						|| sell.getHandwordSellStatus().equals("同意")) {
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							+ (sell.getSellCount() == null ? 0F : sell
									.getSellCount()));
					if (null != sell.getSellZhishu()
							&& null != goods.getGoodsZhishu()) {
						goods.setGoodsZhishu(goods.getGoodsZhishu()
								+ sell.getSellZhishu());
					}
					totalDao.update(goods);
				}
				
				return totalDao.delete(sell);
			}
		}
		
			String hql = "from Goods where goodsMarkId='" + sell.getSellMarkId()
					+ "'";
			if (sell.getSellWarehouse() != null
					&& sell.getSellWarehouse().length() > 0) {
				hql += " and goodsClass='" + sell.getSellWarehouse() + "'";
			}
			if (sell.getGoodHouseName() != null
					&& sell.getGoodHouseName().length() > 0) {
				hql += " and goodHouseName='" + sell.getGoodHouseName() + "'";
			}
			if (sell.getKuwei() != null && sell.getKuwei().length() > 0) {
				hql += " and goodsPosition='" + sell.getKuwei() + "'";
			}
			if (sell.getBanBenNumber() != null
					&& sell.getBanBenNumber().length() > 0) {
				hql += " and banBenNumber='" + sell.getBanBenNumber() + "'";
			}
			if (sell.getKgliao() != null && sell.getKgliao().length() > 0) {
				hql += " and kgliao='" + sell.getKgliao() + "'";
			}
			if (sell.getSellLot() != null && sell.getSellLot().length() > 0) {
				hql += " and goodsLotId='" + sell.getSellLot() + "'";
			}
			List list = totalDao.find(hql);
			if (list.size() > 0 && null != list) {
				if(sell.getHandwordSellStatus()==null || sell.getHandwordSellStatus().equals("")
						|| sell.getHandwordSellStatus().equals("同意")) {
					Goods goods = (Goods) list.get(0);
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							+ (sell.getSellCount() == null ? 0F : sell.getSellCount()));
					if (null != sell.getSellZhishu() && null != goods.getGoodsZhishu()) {
						goods.setGoodsZhishu(goods.getGoodsZhishu()
								+ sell.getSellZhishu());
					}
					totalDao.update(goods);
				}
				return totalDao.delete(sell);
		}
		
		
		return false;
	}

	public boolean updateSell(Sell sell) {
		Sell se = (Sell) totalDao.getObjectById(Sell.class, sell.getSellId());
		// sellSendnum
		// 考虑到sell中加新字段，所以使用笨方法吧
		// BeanUtils.copyProperties(sell, se, new String[] { "sellCount",
		// "sellZhishu", "printStatus", "sellSendnum", "sellAdminName",
		// "sellReturnCount", "sellMore", "sellNumber",
		// "sellTime","printNumber"});

		// 页面传递一个值在这里写一个
		int oldsellCount = se.getSellCount().intValue();
		se.setSellCount(sell.getSellCount());// 出库量
		se.setSellUnit(sell.getSellUnit());// 计量单位
		se.setSellZhishu(sell.getSellZhishu());// 转换数量
		se.setSellArtsCard(sell.getSellArtsCard());// 工艺卡号
		se.setSellDate(sell.getSellDate());// 出库日期
		se.setSellCompanyName(sell.getSellCompanyName());// 客户
		se.setSellPeople(sell.getSellPeople());// 返回状态
		se.setSellSendnum(sell.getSellSendnum());// 送货单号
		se.setOutOrderNumer(sell.getOutOrderNumer());// 外部订单号
		se.setSellGoodsMore(sell.getSellGoodsMore());// 备注

		String hql = "from Goods where goodsMarkId='" + se.getSellMarkId()
				+ "' and goodsLotId='" + se.getSellLot()
				+ "' and goodsFormat='" + se.getSellFormat()
				+ "' and goodsClass='" + se.getSellWarehouse() + "'";
		List<Goods> list = totalDao.find(hql);
		if (list != null && list.size() > 0) {
			Goods goods = (Goods) list.get(0);
			Float sellCount = sell.getSellCount();
			Float cbCount = sellCount - oldsellCount;
			goods.setGoodsCurQuantity(goods.getGoodsCurQuantity() - cbCount);
			if (goods.getGoodsCurQuantity() < 0) {
				sendMessage(goods);
				goods.setGoodsCurQuantity(0f);
			}
			if (null != sell.getSellZhishu() && null != goods.getGoodsZhishu()) {
				if (se.getSellZhishu() == null) {
					se.setSellZhishu(0f);
				}
				goods.setGoodsZhishu(goods.getGoodsZhishu()
						+ se.getSellZhishu() - sell.getSellZhishu());
			}
			se.setSellCount(sell.getSellCount());
			se.setSellZhishu(sell.getSellZhishu());
			se.setSellSendnum(sell.getSellSendnum());
			if ("成品库".equals(se.getSellWarehouse())
					&& "销售出库".equals(se.getStyle())) {
				// 处理订单的封闭
				String orderNum = se.getOrderNum();// 订单号，都是单个订单
				String hqlod = "from OrderManager where orderNum='" + orderNum
						+ "'";
				List listOrder = totalDao.find(hqlod);
				if (cbCount != 0 && listOrder.size() > 0) {
					OrderManager order = (OrderManager) listOrder.get(0);
					String hqlorderProduct = "from ProductManager where orderManager.id="
							+ order.getId()
							+ " and pieceNumber='"
							+ se.getSellMarkId() + "'";
					List listOP = totalDao.find(hqlorderProduct);
					if (listOP.size() > 0) {
						ProductManager pm = (ProductManager) listOP.get(0);
						// int sellCount = Integer.parseInt(sell.getSellCount()
						// .toString());
						Float wancheng = (pm.getSellCount() - oldsellCount)
								+ sellCount;
						if (wancheng.floatValue() > pm.getNum().floatValue()) {// 出库总量超过订单数量
							return false;
						} else {
							pm.setSellCount(wancheng);
							List<InternalOrderDetail> ioDetails = totalDao
									.query(
											"from InternalOrderDetail where id in(select ioDetailId from Product_Internal where productId=? and status='同意')",
											pm.getId());
							if (ioDetails != null && ioDetails.size() > 0) {
								for (InternalOrderDetail iodetail : ioDetails) {
									if (cbCount.floatValue() < 0) {
										if ((iodetail.getSellCount()
												.floatValue() + cbCount
												.floatValue()) >= 0) {
											iodetail.setSellCount(iodetail
													.getSellCount()
													+ cbCount);
											cbCount = 0f;
										} else {
											cbCount = cbCount
													+ iodetail.getSellCount();
											iodetail.setSellCount(0f);
										}
										totalDao.update(iodetail);
									} else {
										if ((iodetail.getSellCount()
												.floatValue() + cbCount
												.floatValue()) <= iodetail
												.getQuantityCompletion()
												.floatValue()) {
											iodetail.setSellCount(iodetail
													.getSellCount()
													+ cbCount);
											cbCount = 0f;
										} else {
											cbCount = cbCount
													- (iodetail
															.getQuantityCompletion() - iodetail
															.getSellCount());
											iodetail.setSellCount(iodetail
													.getQuantityCompletion());
										}
										totalDao.update(iodetail);
									}
								}
							}
							totalDao.update(pm);
							String hqlop = "from ProductManager where orderManager.id="
									+ order.getId();
							if (totalDao.find(hqlop).size() == 0) {
								order.setOrderFil("已交付");
							} else {
								order.setOrderFil("否");
							}
						}
					}
				}
				if (cbCount != 0) {
					throw new RuntimeException("订单出库数据异常!");
				}
			}
			totalDao.update(goods);
			if (se.getPrintNumber() != null && se.getPrintNumber().length() > 0) {
				PrintedOut printedout = (PrintedOut) totalDao
						.getObjectByCondition(
								" from PrintedOut where className = 'Sell' and entiyId =? ",
								sell.getSellId());
				PrintedOutOrder printedoutOrder = (PrintedOutOrder) totalDao
						.getObjectByCondition(
								" from PrintedOutOrder where planNum =? ", se
										.getPrintNumber());
				if (printedoutOrder != null) {
					printedoutOrder.setShPlanNum(se.getSellSendnum());
					printedoutOrder.setRiqi(se.getSellDate());
					totalDao.update(printedoutOrder);
				}
				if (printedout != null) {
					printedout.setSellTime(se.getSellDate());// 出库日期
					printedout.setShPlanNum(se.getSellSendnum());// 送货单号
					printedout.setNum(se.getSellCount());// 出库数量
					totalDao.update(printedout);
				}
			}
			return totalDao.update(se);
		}
		return false;

	}

	public void explorExcelByPoi(Sell sell, String startDate, String endDate,
			String tag, String status) {

		{
			if ("sellDetail".equals(tag)) {
				String hql = "from Sell where sellWarehouse in("
						+ getWarehouse() + ") order by sellDate desc";
				String[] between = new String[2];
				between[0] = "";
				between[1] = "";
				if (null != startDate && !"".equals(startDate)) {
					between[0] = startDate;
				}

				if (null != endDate && !"".equals(endDate)) {
					between[1] = endDate;
				}
				if (null != sell) {
					String str = "";
					String str_1 = "";
					String GoodHouseName = "";
					// 拼接仓区
					String changqu = "";
					String hql_cq = "";
					if (sell.getGoodHouseName() != null
							&& sell.getGoodHouseName().length() > 0) {
						hql_cq = " and goodHouseName in (";
						String[] cangqus = sell.getGoodHouseName().split(",");
						if (cangqus != null && cangqus.length > 0) {
							for (int i = 0; i < cangqus.length; i++) {
								changqu += ",'" + cangqus[i] + "'";
							}
							if (changqu.length() >= 1) {
								changqu = changqu.substring(1);
							}
							hql_cq += changqu;
						}
						hql_cq += ")";
						GoodHouseName = sell.getGoodHouseName();
						sell.setGoodHouseName(null);
					}
					if (sell.getSellWarehouse() != null
							&& sell.getSellWarehouse().length() > 0) {
						str_1 = " and sellWarehouse = '"
								+ sell.getSellWarehouse() + "'";
						sell.setSellWarehouse(null);
					}
					hql = totalDao.criteriaQueries(sell, "sellDate", between,
							"");
					hql += str + str_1 + hql_cq;
					sell.setGoodHouseName(GoodHouseName);
					if (hql.contains("where")) {
						hql += " and sellWarehouse in(" + getWarehouse() + ")"
								+ " order by sellDate desc";
					} else {
						hql += " where sellWarehouse in(" + getWarehouse()
								+ ")" + " order by sellDate desc";
					}
				}
				List list = totalDao.find(hql,1,40000);
				try {
					HttpServletResponse response = (HttpServletResponse) ActionContext
							.getContext().get(
									ServletActionContext.HTTP_RESPONSE);
					OutputStream os = response.getOutputStream();
					response.reset();
					if(list.size()>60000){
						response.setHeader("Content-disposition",
								"attachment; filename="
										+ new String("出库历史记录".getBytes("GB2312"),
										"8859_1") + ".xlsx");
					} else{ response.setHeader("Content-disposition",
								"attachment; filename="
										+ new String("出库历史记录".getBytes("GB2312"),
										"8859_1") + ".xls");
					}

					response.setContentType("application/msexcel");

					SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
					org.apache.poi.ss.usermodel.Sheet sheet = workBook
							.createSheet("出库历史记录");
					Row row = sheet.createRow(2);
					CellRangeAddress rangeAddress = new CellRangeAddress(0, 0,
							1, 29);
					CellStyle style = workBook.createCellStyle();
					style.setAlignment(HorizontalAlignment.CENTER);
					Font font = workBook.createFont();
					font.setFontHeightInPoints((short) 16);
					font.setBold(true);
					style.setFont(font);
					sheet.addMergedRegion(rangeAddress);
					row = sheet.createRow(0);
					org.apache.poi.ss.usermodel.Cell cell = row.createCell(1);
					cell.setCellValue("出库历史记录");
					cell.setCellStyle(style);
					row = sheet.createRow(1);

					cell = row.createCell(0,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("序号");
					cell = row.createCell(1,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("库别");
					cell = row.createCell(2,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("仓区");
					cell = row.createCell(3,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("库位");
					cell = row.createCell(4,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("件号");
					cell = row.createCell(5,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("版本");
					cell = row.createCell(6,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("供料属性");
					cell = row.createCell(7,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("批次");
					cell = row.createCell(8,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("物料类别");
					cell = row.createCell(9,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("品名");
					cell = row.createCell(10,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("规格");
					cell = row.createCell(11,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("数量");
					cell = row.createCell(12,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("单位");
					cell = row.createCell(13,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("客户");
					cell = row.createCell(14,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("供应商");
					cell = row.createCell(15,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("转换数量");
					cell = row.createCell(16,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("转换单位");
					cell = row.createCell(17,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("领料人");
					cell = row.createCell(18,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("领料部门");
					cell = row.createCell(19,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("总成件号");
					cell = row.createCell(20,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("日期");
					cell = row.createCell(21,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("业务件号");
					cell = row.createCell(22,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("内部订单号");
					cell = row.createCell(23,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("外部订单号");
					cell = row.createCell(24,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("打印单号");
					cell = row.createCell(25,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("出库类型");
					cell = row.createCell(26,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("送货单号");

					int count = 0;
					if ("price".equals(status)) {
						cell = row.createCell(26 + ++count,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue("不含税单价");
						cell = row.createCell(26 + ++count,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue("含税单价");
						cell = row.createCell(26 + ++count,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue("总额(含税)");
						cell = row.createCell(26 + ++count,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue("总额(不含税)");
						cell = row.createCell(26 + ++count,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue("税率");
					}
					cell = row.createCell(26 + ++count,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("采购单号");
					cell = row.createCell(26 + ++count,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("备注");
					cell = row.createCell(26 + ++count,
							org.apache.poi.ss.usermodel.CellType.STRING);
					cell.setCellValue("审批状态");

					DecimalFormat df = new DecimalFormat("#.####");
					DecimalFormat df1 = new DecimalFormat("#.##");
					for (int i = 0; i < list.size(); i++) {


						Sell se = (Sell) list.get(i);
						float zhishu = 0;
						if (null != se.getSellZhishu()) {
							zhishu = se.getSellZhishu();
						}

						row = sheet.createRow(i + 2);
						cell = row.createCell(0,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(i + 1);
						cell = row.createCell(1,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellWarehouse());
						cell = row.createCell(2,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getGoodHouseName());
						cell = row.createCell(3,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getKuwei());
						cell = row.createCell(4,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellMarkId());
						cell = row.createCell(5,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getBanBenNumber());
						cell = row.createCell(6,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getKgliao());
						cell = row.createCell(7,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellLot());
						cell = row.createCell(8,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getWgType());
						cell = row.createCell(9,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellGoods());
						cell = row.createCell(10,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellFormat());
						cell = row.createCell(11,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(Float.parseFloat(String.format(
								"%.4f", se.getSellCount())));
						cell = row.createCell(12,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellUnit());
						cell = row.createCell(13,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellCompanyName());
						cell = row.createCell(14,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellSupplier());
						cell = row.createCell(15,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(zhishu);
						cell = row.createCell(16,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getGoodsStoreZHUnit());
						cell = row.createCell(17,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellCharger());
						cell = row.createCell(18,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellchardept());
						cell = row.createCell(19,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellProMarkId());
						cell = row.createCell(20,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellDate());
						cell = row.createCell(21,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getYwmarkId());
						cell = row.createCell(22,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getOrderNum());
						cell = row.createCell(23,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getOutOrderNumer());
						cell = row.createCell(24,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getPrintNumber());
						cell = row.createCell(25,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getStyle());
						if (se.getSellSendnum() != null
								&& !"".equals(se.getSellSendnum())) {
							cell = row
									.createCell(
											26,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getSellSendnum());
						} else {
							cell = row
									.createCell(
											26,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue("");
						}
						int count1 = 0;
						if ("price".equals(status)) {
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getSellbhsPrice() == null ? 0
									: Double.parseDouble(df.format(se
											.getSellbhsPrice())));
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getSellPrice() == null ? 0
									: Double.parseDouble(df.format(se
											.getSellPrice())));
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getSellPrice() == null ? 0
									: Double.parseDouble(df1.format(se
											.getSellPrice()
											* se.getSellCount())));
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getSellbhsPrice() == null ? 0
									: Double.parseDouble(df1.format(se
											.getSellbhsPrice()
											* se.getSellCount())));
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							if(se.getTaxprice() == null){
								cell.setCellValue("0%");
							}else{
								cell.setCellValue(se.getTaxprice()+"%");
							}
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue(se.getCgNumber());
						} else {
							cell = row
									.createCell(
											26 + ++count1,
											org.apache.poi.ss.usermodel.CellType.STRING);
							cell.setCellValue("");
						}
						cell = row.createCell(26 + ++count1,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getSellGoodsMore());
						cell = row.createCell(26 + ++count1,
								org.apache.poi.ss.usermodel.CellType.STRING);
						cell.setCellValue(se.getHandwordSellStatus());
					}
					workBook.write(os);
					workBook.close();// 记得关闭工作簿
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void explorExcel(Sell sell, String startDate, String endDate,
			String tag, String status) {
		if ("sellDetail".equals(tag)) {
			String hql = "from Sell where sellWarehouse in(" + getWarehouse()
					+ ") order by sellDate desc";
			String[] between = new String[2];
			if (null != startDate && null != endDate && !"".equals(endDate)
					&& !"".equals(startDate)) {
				between[0] = startDate;
				between[1] = endDate;
			}
			if (null != sell) {
				String str = "";
				String str_1 = "";
				String GoodHouseName = "";
				// 拼接仓区
				String changqu = "";
				String hql_cq = "";
				if (sell.getGoodHouseName() != null
						&& sell.getGoodHouseName().length() > 0) {
					hql_cq = " and goodHouseName in (";
					String[] cangqus = sell.getGoodHouseName().split(",");
					if (cangqus != null && cangqus.length > 0) {
						for (int i = 0; i < cangqus.length; i++) {
							changqu += ",'" + cangqus[i] + "'";
						}
						if (changqu.length() >= 1) {
							changqu = changqu.substring(1);
						}
						hql_cq += changqu;
					}
					hql_cq += ")";
					GoodHouseName = sell.getGoodHouseName();
					sell.setGoodHouseName(null);
				}
				if (sell.getSellWarehouse() != null
						&& sell.getSellWarehouse().length() > 0) {
					str_1 = " and sellWarehouse = '" + sell.getSellWarehouse()
							+ "'";
					sell.setSellWarehouse(null);
				}
				hql = totalDao.criteriaQueries(sell, "sellDate", between, "");
				hql += str + str_1 + hql_cq;
				sell.setGoodHouseName(GoodHouseName);
				if (hql.contains("where")) {
					hql += " and sellWarehouse in(" + getWarehouse() + ")"
							+ " order by sellDate desc";
				} else {
					hql += " where sellWarehouse in(" + getWarehouse() + ")"
							+ " order by sellDate desc";
				}
			}
			List list = totalDao.find(hql);
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(ServletActionContext.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String("出库历史记录".getBytes("GB2312"),
										"8859_1") + ".xls");
				response.setContentType("application/msexcel");
				WritableWorkbook wwb = Workbook.createWorkbook(os);
				WritableSheet ws = wwb.createSheet("出库历史记录", 0);
				ws.setColumnView(1, 12);
				ws.setColumnView(2, 20);
				ws.setColumnView(3, 10);
				ws.setColumnView(4, 20);
				ws.setColumnView(13, 15);
				ws.setColumnView(14, 25);
				NumberFormat fivedps1 = new NumberFormat("0.0000");
				WritableCellFormat fivedpsFormat1 = new WritableCellFormat(
						fivedps1);
				WritableFont wf = new WritableFont(WritableFont.ARIAL, 14,
						WritableFont.NO_BOLD, false,
						UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
				WritableCellFormat wcf = new WritableCellFormat(wf);
				wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
				wcf.setAlignment(Alignment.CENTRE);
				jxl.write.Label label0 = new Label(0, 0, "出库历史记录", wcf);
				ws.addCell(label0);
				ws.mergeCells(0, 0, 19, 0);
				wf = new WritableFont(WritableFont.ARIAL, 12,
						WritableFont.NO_BOLD, false,
						UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
				WritableCellFormat wc = new WritableCellFormat(wf);
				wc.setAlignment(Alignment.CENTRE);
				ws.addCell(new jxl.write.Label(0, 1, "序号", wc));
				ws.addCell(new jxl.write.Label(1, 1, "库别", wc));
				ws.addCell(new jxl.write.Label(2, 1, "仓区", wc));
				ws.addCell(new jxl.write.Label(3, 1, "库位", wc));
				ws.addCell(new jxl.write.Label(4, 1, "件号", wc));
				ws.addCell(new jxl.write.Label(5, 1, "版本", wc));
				ws.addCell(new jxl.write.Label(6, 1, "供料属性", wc));
				ws.addCell(new jxl.write.Label(7, 1, "批次", wc));
				ws.addCell(new jxl.write.Label(8, 1, "物料类别", wc));
				ws.addCell(new jxl.write.Label(9, 1, "品名", wc));
				ws.addCell(new jxl.write.Label(10, 1, "规格", wc));
				ws.addCell(new jxl.write.Label(11, 1, "数量", wc));
				ws.addCell(new jxl.write.Label(12, 1, "单位", wc));
				ws.addCell(new jxl.write.Label(13, 1, "客户", wc));
				ws.addCell(new jxl.write.Label(14, 1, "供应商", wc));
				ws.addCell(new jxl.write.Label(15, 1, "转换数量", wc));
				ws.addCell(new jxl.write.Label(16, 1, "转换单位", wc));
				ws.addCell(new jxl.write.Label(17, 1, "领料人", wc));
				ws.addCell(new jxl.write.Label(18, 1, "领料部门", wc));
				ws.addCell(new jxl.write.Label(19, 1, "总成件号", wc));
				ws.addCell(new jxl.write.Label(20, 1, "日期", wc));
				ws.addCell(new jxl.write.Label(21, 1, "业务件号", wc));
				ws.addCell(new jxl.write.Label(22, 1, "内部订单号", wc));
				ws.addCell(new jxl.write.Label(23, 1, "外部订单号", wc));
				ws.addCell(new jxl.write.Label(24, 1, "打印单号", wc));
				ws.addCell(new jxl.write.Label(25, 1, "出库类型", wc));
				ws.addCell(new jxl.write.Label(26, 1, "送货单号", wc));
				int count = 0;
				if ("price".equals(status)) {
					ws
							.addCell(new jxl.write.Label(26 + ++count, 1,
									"不含税单价", wc));
					ws
							.addCell(new jxl.write.Label(26 + ++count, 1,
									"含税单价", wc));
					ws.addCell(new jxl.write.Label(26 + ++count, 1, "总额(含税)",
							wc));
					ws.addCell(new jxl.write.Label(26 + ++count, 1, "总额(不含税)",
							wc));
					ws.addCell(new jxl.write.Label(26 + ++count, 1, "税率", wc));
				}
				ws.addCell(new jxl.write.Label(26 + ++count, 1, "采购单号", wc));
				ws.addCell(new jxl.write.Label(26 + ++count, 1, "备注", wc));
				ws.addCell(new jxl.write.Label(26 + ++count, 1, "审批状态", wc));
				DecimalFormat df = new DecimalFormat("#.####");
				DecimalFormat df1 = new DecimalFormat("#.##");
				for (int i = 0; i < list.size(); i++) {
					Sell se = (Sell) list.get(i);
					float zhishu = 0;
					if (null != se.getSellZhishu()) {
						zhishu = se.getSellZhishu();
					}
					ws.addCell(new jxl.write.Number(0, i + 2, i + 1, wc));
					ws.addCell(new Label(1, i + 2, se.getSellWarehouse(), wc));
					ws.addCell(new Label(2, i + 2, se.getGoodHouseName(), wc));
					ws.addCell(new Label(3, i + 2, se.getKuwei(), wc));
					ws.addCell(new Label(4, i + 2, se.getSellMarkId(), wc));
					ws.addCell(new Label(5, i + 2, se.getBanBenNumber(), wc));
					ws.addCell(new Label(6, i + 2, se.getKgliao(), wc));
					ws.addCell(new Label(7, i + 2, se.getSellLot(), wc));
					ws.addCell(new Label(8, i + 2, se.getWgType(), wc));
					ws.addCell(new Label(9, i + 2, se.getSellGoods(), wc));
					ws.addCell(new Label(10, i + 2, se.getSellFormat(), wc));
					ws.addCell(new jxl.write.Number(11, i + 2, Float
							.parseFloat(String
									.format("%.4f", se.getSellCount())),
							fivedpsFormat1));
					ws.addCell(new Label(12, i + 2, se.getSellUnit(), wc));
					ws
							.addCell(new Label(13, i + 2, se
									.getSellCompanyName(), wc));
					ws.addCell(new Label(14, i + 2, se.getSellSupplier(), wc));
					ws.addCell(new jxl.write.Number(15, i + 2, zhishu, wc));
					ws.addCell(new Label(16, i + 2, se.getGoodsStoreZHUnit(),
							wc));
					ws.addCell(new Label(17, i + 2, se.getSellCharger(), wc));
					ws.addCell(new Label(18, i + 2, se.getSellchardept(), wc));
					ws.addCell(new Label(19, i + 2, se.getSellProMarkId(), wc));
					ws.addCell(new Label(20, i + 2, se.getSellDate(), wc));
					ws.addCell(new Label(21, i + 2, se.getYwmarkId(), wc));
					ws.addCell(new Label(22, i + 2, se.getOrderNum(), wc));
					ws.addCell(new Label(23, i + 2, se.getOutOrderNumer(), wc));
					ws.addCell(new Label(24, i + 2, se.getPrintNumber(), wc));
					ws.addCell(new Label(25, i + 2, se.getStyle(), wc));
					if (se.getSellSendnum() != null
							&& !"".equals(se.getSellSendnum())) {
						ws
								.addCell(new Label(26, i + 2, se
										.getSellSendnum(), wc));
					} else {
						ws.addCell(new Label(26, i + 2, "", wc));
					}
					int count1 = 0;
					if ("price".equals(status)) {
						ws.addCell(new jxl.write.Number(26 + ++count1, i + 2,
								se.getSellbhsPrice() == null ? 0 : Double
										.parseDouble(df.format(se
												.getSellbhsPrice())), wc));
						ws.addCell(new jxl.write.Number(26 + ++count1, i + 2,
								se.getSellPrice() == null ? 0 : Double
										.parseDouble(df.format(se
												.getSellPrice())), wc));
						ws.addCell(new jxl.write.Number(26 + ++count1, i + 2,
								se.getSellPrice() == null ? 0 : Double
										.parseDouble(df1.format(se
												.getSellPrice()
												* se.getSellCount())), wc));
						ws.addCell(new jxl.write.Number(26 + ++count1, i + 2,
								se.getSellbhsPrice() == null ? 0 : Double
										.parseDouble(df1.format(se
												.getSellbhsPrice()
												* se.getSellCount())), wc));
						ws
								.addCell(new jxl.write.Number(26 + ++count1,
										i + 2, se.getTaxprice() == null ? 0
												: se.getTaxprice(), wc));
						ws.addCell(new Label(26 + ++count1, i + 2, se
								.getCgNumber(), wc));
					} else {
						ws.addCell(new Label(26 + ++count1, i + 2, se
								.getCgNumber(), wc));
					}
					ws.addCell(new Label(26 + ++count1, i + 2, se
							.getSellGoodsMore(), wc));
					ws.addCell(new Label(26 + ++count1, i + 2, se
							.getHandwordSellStatus(), wc));
				}
				wwb.write();
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	public void printInfor(Integer id) {
		Sell sell = (Sell) totalDao.getObjectById(Sell.class, id);
		sell.setPrintStatus("YES");
		totalDao.update(sell);
	}

	/***
	 * 根据件号和批次查询出库信息
	 * 
	 * @param sellMarkId
	 *            件号
	 * @param sellLot
	 *            批次
	 * @return
	 */
	@Override
	public List findSell(String sellMarkId, String sellLot) {
		String hql = "from Sell where sellMarkId=? and sellLot=? and sellWarehouse ='成品库'";
		return totalDao.query(hql, sellMarkId, sellLot);
	}

	@Override
	public List printStorage(int[] selected) {
		List<Sell> l = new ArrayList<Sell>();
		for (int id : selected) {
			Sell sh = (Sell) totalDao.getObjectById(Sell.class, id);
			if ("成品库".equals(sh.getSellWarehouse())
					|| "备货库".equals(sh.getSellWarehouse())) {
				Procard procard = (Procard) totalDao
						.getObjectByCondition(
								" from Procard where markId = ? and selfCard = ? and procardStyle='总成' and id=rootId",
								sh.getSellMarkId(), sh.getSellLot());
				if (procard != null) {
					sh.setYwmarkId(procard.getYwMarkId());
				}
			}
			l.add(sh);
		}
		return l;
	}

	@Override
	public boolean checkshifacount(String goodsMarkId, String goodsFormat,
			float shifa) {
		// TODO Auto-generated method stub
		Float curQuantity = (Float) totalDao
				.getObjectByCondition(
						"select sum(goodsCurQuantity) from Goods where goodsClass='外购件库' and goodsMarkId=?  and (fcStatus is null or fcStatus='可用')",
						goodsMarkId);
		if (curQuantity != null) {
			if (curQuantity >= shifa) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Float getZaiZhiCount(Integer id) {
		// TODO Auto-generated method stub
		Procard procard = byProcardid(id);
		// 是否为需要原材料的组合(此处只查组合的成品在制品不查组合的原材料在制品)
		boolean iszhycl = procard.isZhHasYcl();
		String hqlZaizhi = "from Goods where goodsMarkId='"
				+ procard.getMarkId()
				+ "' and goodsClass='在制品' and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用') ";
		if (iszhycl) {// 组合分成品在制品(goodsFormat 为空)和原材料在制品(goodsFormat 为'原材料')
			hqlZaizhi = hqlZaizhi
					+ " and (goodsFormat is null or goodsFormat='')";
		}
		List<Goods> listzaizhi = totalDao.query(hqlZaizhi);
		float zaizhiCount = 0f;
		if (null != listzaizhi && listzaizhi.size() > 0) {
			for (Goods goodszaizhi : listzaizhi) {
				zaizhiCount += goodszaizhi.getGoodsCurQuantity();// 在制品数量
			}
		}
		// 查询所有同总成件号没有完成的所需的在制品量，用在制品数减去(同总成除外，同总成的将在下面计算)
		String filnalCountSql = "from Procard where markId =? and id !=? and hasCount is not null and klNumber>hasCount and rootId!=?  and rootId in (select rootId from Procard where markId =(select markId from Procard where id=?)  and status not in('入库'))";
		// if (procard.getProcardStyle() != null
		// && procard.getProcardStyle().equals("外购")) {
		// filnalCountSql += " and needprocess='yes'";
		// }
		List<Procard> filnalCountList = totalDao.query(filnalCountSql, procard
				.getMarkId(), procard.getId(), procard.getRootId(), procard
				.getRootId());
		if (filnalCountList.size() > 0) {
			for (Procard zaizhiProcard : filnalCountList) {
				if (zaizhiProcard != null) {
					Procard totalCard = byProcardRootId(zaizhiProcard);
					if (totalCard.getHasRuku() == null) {
						totalCard.setHasRuku(0f);
					}
					if (zaizhiProcard.getKlNumber() == null) {
						zaizhiProcard.setKlNumber(0f);
					}
					if (zaizhiProcard.getHascount() == null) {
						zaizhiProcard.setHascount(zaizhiProcard.getKlNumber());
					}
					// 已入库数量*该件号对总成的比例数量就是需要在制品数量
					Float hasRuKu = totalCard.getHasRuku()
							* zaizhiProcard.getFilnalCount()
							/ totalCard.getFilnalCount();
					// 已领减去已入库
					float hsaCount = (float) Math.ceil(zaizhiProcard
							.getHascount());
					Float needCount = (zaizhiProcard.getKlNumber() - hsaCount)
							- hasRuKu;
					zaizhiCount -= needCount;
				}
			}

		}
		// 查询其他含有该自制件的BOM的已领却没有入库所需要的在制品数
		String otherFilnalCountSql = "from Procard where status in('已发料','领工序','完成','待入库') and markId=?  and rootId in(select id from Procard where id in (select rootId from Procard where markId=?) and markId!=(select markId from Procard where id=?) and status not in('入库'))";
		// if (procard.getProcardStyle() != null
		// && procard.getProcardStyle().equals("外购")) {
		// otherFilnalCountSql += " and needprocess='yes'";
		// }
		List<Procard> otherProcardList = totalDao.query(otherFilnalCountSql,
				procard.getMarkId(), procard.getMarkId(), procard.getRootId());
		if (otherProcardList.size() > 0) {
			for (Procard p : otherProcardList) {
				if (p.getHascount() == null || p.getFilnalCount() == null
						|| (p.isZhHasYcl() && p.getYhascount() == null)) {
					break;
				} else {
					Procard totalCard = byProcardRootId(p);
					if (totalCard.getHasRuku() == null) {
						totalCard.setHasRuku(0f);
					}
					// （总成总数量-已入库数量）*该件号对总成的比例数量就是需要在制品数量-未领料的
					float hsaCount = (float) Math.ceil(p.getHascount());
					zaizhiCount -= (totalCard.getFilnalCount() - totalCard
							.getHasRuku())
							* p.getFilnalCount()
							/ totalCard.getFilnalCount()
							- hsaCount;
				}
			}
		}
		Procard totalCard = byProcardRootId(procard);
		if (totalCard.getHasRuku() == null) {
			totalCard.setHasRuku(0f);
		}
		// 查询本批次同件号已领的其他件号
		Procard sameMarkId = (Procard) totalDao
				.getObjectByCondition(
						"from Procard where status in('已发料','领工序','完成','待入库') and rootId=? and markId=? and id!=?",
						procard.getRootId(), procard.getMarkId(), procard
								.getId());
		if (sameMarkId != null) {
			if (sameMarkId.getHascount() == null
					|| sameMarkId.getFilnalCount() == null
					|| (sameMarkId.isZhHasYcl() && sameMarkId.getYhascount() == null)) {
			} else {
				// 已领减去已入库
				// （总成总数量-已入库数量）*该件号对总成的比例数量就是需要在制品数量-未领料的
				if (sameMarkId.getHascount() != null) {
					float hsaCount = (float) Math
							.ceil(sameMarkId.getHascount());
					zaizhiCount -= (sameMarkId.getKlNumber() - hsaCount);// 本批次生成的在制品

					zaizhiCount += totalCard.getHasRuku()
							* sameMarkId.getFilnalCount()
							/ totalCard.getFilnalCount();// 入库扣去的在制品
				}
			}
		}
		// 查询本批次已占用的在制品，用在制品数减去
		if (procard.getHascount() != null) {
			if (procard.getKlNumber() > procard.getHascount()) {

				// 已生成的在制品-总成已入库扣去的在制品数量
				float hsaCount = (float) Math.ceil(procard.getHascount());
				zaizhiCount -= (procard.getKlNumber() - hsaCount);// 本批次生成的在制品

				zaizhiCount += totalCard.getHasRuku()
						* procard.getFilnalCount() / totalCard.getFilnalCount();// 入库扣去的在制品
			}
		}

		return zaizhiCount;
	}

	/**
	 * 获取原材料在制品数量
	 * 
	 * @param id
	 * @param count
	 * @return
	 */
	public Float getYclZaiZhiCount(Integer id) {
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, id);
		boolean isZhHasYcl = procard.isZhHasYcl();
		if (isZhHasYcl) {
			String hqlZaizhi = "from Goods where goodsMarkId='"
					+ procard.getMarkId()
					+ "' and goodsClass='在制品' and goodsCurQuantity>0 and goodsFormat = '原材料' and (fcStatus is null or fcStatus='可用') ";
			List<Goods> listzaizhi = totalDao.query(hqlZaizhi);
			float zaizhiCount = 0f;
			if (null != listzaizhi && listzaizhi.size() > 0) {
				for (Goods goodszaizhi : listzaizhi) {
					zaizhiCount += goodszaizhi.getGoodsCurQuantity();// 在制品数量
				}
			}
			// 查询所有同总成件号没有完成的所需的在制品量，用在制品数减去
			String filnalCountSql = "from Procard where markId =? and ((yhascount is not null and klNumber>yhascount) or (hasCount is not null and klNumber>hasCount)) and rootId !=?  and rootId in (select rootId from Procard where markId =(select markId from Procard where id=?)  and status not in('入库'))";
			// if (procard.getProcardStyle() != null
			// && procard.getProcardStyle().equals("外购")) {
			// filnalCountSql += " and needprocess='yes'";
			// }
			List<Procard> filnalCountList = totalDao.query(filnalCountSql,
					procard.getMarkId(), procard.getRootId(), procard
							.getRootId());
			if (filnalCountList.size() > 0) {
				for (Procard zaizhiProcard : filnalCountList) {
					if (zaizhiProcard.getYhascount() == null) {
						zaizhiProcard.setYhascount(zaizhiProcard.getKlNumber());
					}
					if (zaizhiProcard != null) {
						if (zaizhiProcard.getKlNumber() == null) {
							zaizhiProcard.setKlNumber(0f);
						}
						if (zaizhiProcard.getHascount() == null) {
							zaizhiProcard.setHascount(zaizhiProcard
									.getKlNumber());
						}
						if (zaizhiProcard.getYhascount() == null) {
							zaizhiProcard.setYhascount(zaizhiProcard
									.getKlNumber());
						}
						if (zaizhiProcard.getYhascount() < zaizhiProcard
								.getHascount()) {
							// 原材料比外购件多领的数量就是占用原材料在制品的数量
							zaizhiCount -= (zaizhiProcard.getHascount() - zaizhiProcard
									.getYhascount());
						}
					}
				}

			}
			// 查询其他含有该自制件的BOM的已领却没有入库所需要的在制品数
			String otherFilnalCountSql = "from Procard where status in('已发料','领工序','完成','待入库') and markId=?  and rootId in(select id from Procard where id in (select rootId from Procard where markId=?) and markId!=(select markId from Procard where id=?) and status not in('入库'))";
			// if (procard.getProcardStyle() != null
			// && procard.getProcardStyle().equals("外购")) {
			// otherFilnalCountSql += " and needprocess='yes'";
			// }
			List<Procard> otherProcardList = totalDao.query(
					otherFilnalCountSql, procard.getMarkId(), procard
							.getMarkId(), procard.getRootId());
			if (otherProcardList.size() > 0) {
				for (Procard p : otherProcardList) {
					if (p.getKlNumber() == null) {
						p.setKlNumber(0f);
					}
					if (p.getHascount() == null) {
						p.setHascount(p.getKlNumber());
					}
					if (p.getYhascount() == null) {
						p.setYhascount(p.getKlNumber());
					}
					if (p.getYhascount() < p.getHascount()) {
						// 原材料比外购件多领的数量就是占用原材料在制品的数量
						zaizhiCount -= (p.getHascount() - p.getYhascount());
					}
				}
			}
			// 查询本批次同件号已领的其他件号
			Procard sameMarkId = (Procard) totalDao
					.getObjectByCondition(
							"from Procard where status in('已发料','领工序','完成','待入库') and rootId=? and markId=? and id!=?",
							procard.getRootId(), procard.getMarkId(), procard
									.getId());
			if (sameMarkId != null) {
				if (sameMarkId.getKlNumber() == null) {
					sameMarkId.setKlNumber(0f);
				}
				if (sameMarkId.getHascount() == null) {
					sameMarkId.setHascount(sameMarkId.getKlNumber());
				}
				if (sameMarkId.getYhascount() == null) {
					sameMarkId.setYhascount(sameMarkId.getKlNumber());
				}
				if (sameMarkId.getYhascount() < sameMarkId.getHascount()) {
					// 原材料比外购件多领的数量就是占用原材料在制品的数量
					zaizhiCount -= (sameMarkId.getHascount() - sameMarkId
							.getYhascount());
				}
			}
			// 查询本批次已占用的在制品，用在制品数减去
			if (procard.getKlNumber() == null) {
				procard.setKlNumber(0f);
			}
			if (procard.getHascount() == null) {
				procard.setHascount(procard.getKlNumber());
			}
			if (procard.getYhascount() == null) {
				procard.setYhascount(procard.getKlNumber());
			}
			if (procard.getYhascount() < procard.getHascount()) {
				// 原材料比外购件多领的数量就是占用原材料在制品的数量
				zaizhiCount -= (procard.getHascount() - procard.getYhascount());
			}

			return zaizhiCount;
		}
		return 0f;
	}

	@Override
	public boolean updateProcessNumber(Float zaizhiCount, Float getCount,
			Integer id) {
		if (zaizhiCount != null && zaizhiCount > 0F) {
			Procard procard = (Procard) totalDao.getObjectById(Procard.class,
					id);
			if (procard != null) {
				Set<ProcessInfor> processSet = procard.getProcessInforSet();
				Float ylCount = 0f;
				Float tjCount = 0f;
				if (getCount <= zaizhiCount) {
					ylCount = getCount;
					tjCount = getCount;
				} else {
					ylCount = zaizhiCount;
					tjCount = zaizhiCount;
				}
				if (procard.getTjNumber() != null) {
					procard.setTjNumber(tjCount + procard.getTjNumber());
				} else {
					procard.setTjNumber(tjCount);
				}
				Integer maxNo = 0;
				Integer maxNoid = 0;
				for (ProcessInfor process : processSet) {
					if (process.getProcessNO() != null
							&& process.getProcessNO() > maxNo) {
						maxNo = process.getProcessNO();
						maxNoid = process.getId();
					}
					process.setApplyCount(process.getApplyCount() + ylCount);
					process
							.setSubmmitCount(process.getSubmmitCount()
									+ tjCount);
					if (procard.getFilnalCount() - process.getSubmmitCount() == 0) {
						process.setStatus("完成");
					} else {
						process.setStatus("初始");
					}
					totalDao.update(process);
				}
				procardServer.updateJihuo(maxNoid, procard.getId(),
						"领料时有在制品同步修改工序已提交数量.");
			}
			return true;
		}
		return true;
	}

	public String getWarehouseList() {
		Users user = (Users) Util.getLoginUser();
		String hql = "from WareHouseAuth where usercode = ?";
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

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	@Override
	public String getCkTypeByLoginUser(String type) {
		// TODO Auto-generated method stub
		Users user = (Users) Util.getLoginUser();
		String hqlwarehouse = "from WareHouseAuth where usercode='"
				+ user.getCode() + "'";
		WareHouseAuth whh = (WareHouseAuth) totalDao
				.getObjectByCondition(hqlwarehouse);
		if (whh != null && whh.getAuth() != null && whh.getAuth().length() > 0) {
			String[] auths = whh.getAuth().split(",");
			StringBuffer sb = new StringBuffer();
			if (auths != null && auths.length > 0) {
				List<String> authList = new ArrayList<String>();
				for (String auth : auths) {
					authList.add(auth);
				}
				List<WareHouse> whList = totalDao.query("from WareHouse");
				if (whList != null && whList.size() > 0) {
					int i = 0;
					for (WareHouse wh : whList) {
						if (wh.getCode() != null
								&& authList.contains(wh.getCode() + "_" + type)) {
							if (i == 0) {
								sb.append(wh.getName());
							} else {
								sb.append("," + wh.getName());
							}
						}
					}
					return sb.toString();
				}
			}
		}
		return null;
	}

	@Override
	public void daoru() {
		List<Sell> sell1 = totalDao
				.query("from Sell where sellId>2214 and sellId < 3137 and sellWarehouse in ('研发库','外购件库','成品库','综合库','半成品库')");
		System.out.println("123");
		StringBuffer buffer = new StringBuffer();
		for (Sell sell2 : sell1) {
			// try {
			// String c = sell2.getSellWarehouse();// 库别 成品库，外购件库
			// if (c == null || c.length() == 0
			// || (!c.equals("成品库") && !c.equals("外购件库")&& !c.equals("研发库")&&
			// !c.equals("售后库")&& !c.equals("综合库")&& !c.equals("半成品库"))) {
			// continue;
			// }
			// String kgliao = sell2.getKgliao();
			// Float cknumber = sell2.getSellCount();// 出库数量
			// String a1 = sell2.getGoodHouseName();// 仓区
			// String a2 = sell2.getKuwei();// 库位
			// String a = sell2.getSellMarkId();// 件号
			// String a3 = sell2.getBanBenNumber();// 版本
			//
			// String h = sell2.getSellLot();// 批次
			//
			// String partNumber = a;
			// // 查询库存数量;
			// String hql_goods = "from Goods where goodsMarkId =? ";
			//
			// // 供料属性
			// if ("外购件库".equals(c)|| c.equals("研发库")|| c.equals("售后库")) {
			// hql_goods += " and kgliao='" + kgliao + "'";
			// }
			// // 版本
			// if (a3 != null && a3.length() > 0) {
			// hql_goods += " and banBenNumber='" + a3 + "'";
			// }
			// // 仓区
			// if (a1 != null && a1.length() > 0) {
			// hql_goods += " and goodHouseName='" + a1 + "'";
			// }
			// // 库位
			// if (a2 != null && a2.length() > 0) {
			// hql_goods += " and goodsPosition='" + a2 + "'";
			// }
			// // 批次
			// if (h != null && h.length() > 0) {
			// hql_goods += " and goodsLotId='" + h + "'";
			// }
			//
			// hql_goods += " order by goodsLotId";
			// List goods_list = totalDao.query(hql_goods, partNumber);
			// Float goods_quantity = (Float) totalDao
			// .getObjectByCondition(
			// "select sum(goodsCurQuantity) " + hql_goods,
			// partNumber);
			//
			// if (goods_list == null || goods_list.size() == 0) {
			// buffer.append(","+sell2.getSellId());
			// }
			//
			// String b = sell2.getSellFormat();// 规格
			// String g = sell2.getSellGoods();// 品名
			// String j = sell2.getSellUnit();// 单位
			// Float l = sell2.getSellZhishu();// 转换数量
			// String k = sell2.getGoodsStoreZHUnit();// 转换单位
			// String p = sell2.getSellGetGoodsMan();// 负责人
			// String q = sell2.getSellCharger();// 领料人
			// String w = sell2.getSellSendnum();// 送货单号
			// String x = sell2.getOrderNum();// 订单号（内部）
			// String y = sell2.getOutOrderNumer();// 订单号（外部）
			// if (goods_list != null && goods_list.size() > 0) {
			// for (int m = 0; m < goods_list.size(); m++) {
			// if (cknumber <= 0) {
			// break;
			// }
			// Goods goods = (Goods) goods_list.get(m);
			// Sell sell = new Sell();
			// try {
			// Float kcnumber = goods.getGoodsCurQuantity();
			// if (kcnumber != null) {
			// kcnumber = (float) (Math
			// .round(kcnumber * 1000)) / 1000;
			// }
			// if (cknumber >= kcnumber) {
			// if (cknumber <= goods_quantity) {
			// // sell.setSellCount(kcnumber);
			// // cknumber = cknumber - kcnumber;
			// // goods_quantity -= cknumber;
			// // sell.setSellCount(kcnumber);
			// } else {
			// // if (cknumber > 0) {
			// // errorcount++;
			// // errorMes += "第" + (i + 1) + "行错误，"
			// // + partNumber
			// // + "库存数量小于出库数量!请注意！！！！\\n";
			// // if (error_index == 0) {
			// // error_index = i + 1;
			// // }
			// // break;
			// // }
			// }
			// } else {
			// // sell.setSellCount(cknumber);
			// cknumber = 0F;
			// }
			// if (l != null && !"".equals(l)) {
			// // sell.setSellZhishu(Float.parseFloat(l));
			// }
			// } catch (Exception e1) {
			// continue;
			// }
			//
			// try {
			// sell2.setGoodsPrice(goods.getGoodsPrice());// 平均单价（库存成本）
			//
			// sell2.setSellPrice(goods.getGoodsBuyPrice());
			// sell2.setSellbhsPrice(goods.getGoodsBuyBhsPrice());
			// sell2.setTaxprice(goods.getTaxprice());
			// // sell.setSellbhsPrice(goods.getGoodsPrice());
			// } catch (Exception e1) {
			// e1.printStackTrace();
			// }
			//							
			//
			// if (totalDao.update(sell2)) {
			// // Float num = goods.getGoodsCurQuantity();
			// // 更新库存
			// // 更新订单完成率 ;
			// gengxinDingdan(cknumber, partNumber, x, y,
			// cknumber);
			// } else {
			// }
			// }
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			boolean b = corePayableServer.goodsSell(sell2);
		}
		System.out.println(buffer.toString());
	}

	private void gengxinDingdan(Float cknumber, String partNumber, String x,
			String y, Float num) {
		if (x != null && !"".equals(x) && y != null && !"".equals(y)) {
			String hql_order = "from OrderManager where orderNum=? and outOrderNumber = ?";
			OrderManager order = (OrderManager) totalDao.getObjectByCondition(
					hql_order, x, y);
			if (order != null) {
				String hql_pm = "from ProductManager where pieceNumber = ? and orderManager.id=?";
				ProductManager pm = (ProductManager) totalDao
						.getObjectByCondition(hql_pm, partNumber, order.getId());
				if (pm != null) {
					Float sellCount = cknumber;
					Float sellcount1 = pm.getSellCount();
					if (sellcount1 == null) {
						sellcount1 = 0f;
					}
					pm.setSellCount(sellcount1 + sellCount);
					String hql_sellCount = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					Float sumsellCount = (Float) totalDao.getObjectByCondition(
							hql_sellCount, order.getId());
					if (sumsellCount == null) {
						sumsellCount = 0f;
					}
					sumsellCount += sellCount;
					String hql_sum = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
					Float num_pro = (Float) totalDao.getObjectByCondition(
							hql_sum, order.getId());
					if (num_pro != null && num_pro > 0) {
						order.setCompletionrate((sumsellCount / num) * 100);
						totalDao.update(order);
					}

				}
			}

		}
	}

	@Override
	public String PladdSell(File addSell, String statue) {

		String msg = "true";
		boolean flag = true;
		String fileName = "sell_" + Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int i = 0;
		InputStream is = null;
		try {
			FileCopyUtils.copy(addSell, file);
			// 开始读取excle表格
			is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Sheet st = wk.getSheet(0);// 获得第一张sheet表;
		int exclecolums = st.getRows();// 获得excle总行数
		if (exclecolums > 2) {
			List<Integer> strList = new ArrayList<Integer>();
			String errorMes = "";
			int succescount = 0;
			int errorcount = 0;
			int error_index = 0;
			for (i = 2; i < exclecolums; i++) {
				try {
					Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String c = cells[1].getContents();// 库别 成品库，外购件库
					if (c == null
							|| c.length() == 0
							|| (!c.equals("成品库") && !c.equals("外购件库")
									&& !c.equals("研发库") && !c.equals("售后库")
									&& !c.equals("综合库") && !c.equals("半成品库"))) {
						errorcount++;
						errorMes += "第" + (i + 1) + "行,库别错误\\n";
						if (error_index == 0) {
							error_index = i + 1;
						}
						continue;
					}
					String kgliao = "";
					try {
						kgliao = cells[19].getContents();// 供料属性
					} catch (Exception e) {
					}
					if ("外购件库".equals(c) || c.equals("研发库") || c.equals("售后库")) {
						if (kgliao == null || kgliao == "") {
							errorMes += "第" + (i + 1) + "行,供料属性未填写!\\n";
							errorcount++;
							if (error_index == 0) {
								error_index = i + 1;
							}
						}
						if (kgliao.indexOf("CS") >= 0) {
							kgliao = "CS";
						} else if (kgliao.indexOf("TK Price") >= 0) {
							kgliao = "TK Price";
						} else if (kgliao.indexOf("TK AVL") >= 0) {
							kgliao = "TK AVL";
						} else if (kgliao.indexOf("TK") >= 0) {
							kgliao = "TK";
						} else {
							errorMes += "第" + (i + 1) + "行,供料属性错误!\\n";
							errorcount++;
							if (error_index == 0) {
								error_index = i + 1;
							}
							continue;
						}
					}
					String f = cells[9].getContents();// 出库数量
					if (cells[9].getType() == CellType.NUMBER) {// jxl
						// 导入小数时会自动格式化保留小数点后三位的模式；所以
						NumberCell numberCell = (NumberCell) cells[9];
						double value = numberCell.getValue();
						f = value + "";
					}
					Float cknumber = 0F;
					try {
						cknumber = Float.parseFloat(f);
						cknumber = (float) (Math.round(cknumber * 1000)) / 1000;
					} catch (Exception e) {
						errorcount++;
						errorMes += "第" + (i + 1) + "行,出库数量异常,请正确填写数值\\n";
						if (error_index == 0) {
							error_index = i + 1;
						}
					}
					String a1 = cells[2].getContents();// 仓区
					String a2 = cells[3].getContents();// 库位
					String a = cells[4].getContents();// 件号
					String a3 = cells[5].getContents();// 版本

					String h = cells[8].getContents();// 批次

					String partNumber = a;
					// 查询库存数量;
					String hql_goods = "from Goods where goodsMarkId =? and goodsCurQuantity>0 ";

					// 供料属性
					if ("外购件库".equals(c) || c.equals("研发库") || c.equals("售后库")) {
						hql_goods += " and kgliao='" + kgliao + "'";
					}
					// 版本
					if (a3 != null && a3.length() > 0) {
						hql_goods += " and banBenNumber='" + a3 + "'";
					}
					// 仓区
					if (a1 != null && a1.length() > 0) {
						hql_goods += " and goodHouseName='" + a1 + "'";
					}
					// 库位
					if (a2 != null && a2.length() > 0) {
						hql_goods += " and goodsPosition='" + a2 + "'";
					}
					// 批次
					if (h != null && h.length() > 0) {
						hql_goods += " and goodsLotId='" + h + "'";
					}

					hql_goods += " order by goodsLotId";
					List goods_list = totalDao.query(hql_goods, partNumber);
					Float goods_quantity = (Float) totalDao
							.getObjectByCondition(
									"select sum(goodsCurQuantity) " + hql_goods,
									partNumber);

					if (goods_list == null || goods_list.size() == 0) {
						errorcount++;
						errorMes += "第" + (i + 1) + "行,件号" + partNumber
								+ "未找到库存量,请检查库存在件号/库别/供料属性/版本/仓库/库位是否正确!\\n";
					}
					if (errorcount > 0) {
						if (error_index == 0) {
							error_index = i + 1;
						}
						continue;
					}

					String b = cells[6].getContents();// 规格
					String g = cells[7].getContents();// 品名
					String j = cells[10].getContents();// 单位
					String l = cells[11].getContents();// 转换数量
					String k = cells[12].getContents();// 转换单位
					String p = cells[13].getContents();// 负责人
					String q = cells[14].getContents();// 领料人
					String w = cells[15].getContents();// 送货单号
					String x = cells[16].getContents();// 订单号（内部）
					String y = cells[17].getContents();// 订单号（外部）
					String n = null;
					try {
						n = cells[18].getContents();// 出库日期
					} catch (Exception e) {
						n = Util.getDateTime();
					}
					if (goods_list != null && goods_list.size() > 0) {
						for (int m = 0; m < goods_list.size(); m++) {
							if (cknumber <= 0) {
								break;
							}
							Goods goods = (Goods) goods_list.get(m);
							Sell sell = new Sell();
							try {
								Float kcnumber = goods.getGoodsCurQuantity();
								if (kcnumber != null) {
									kcnumber = (float) (Math
											.round(kcnumber * 1000)) / 1000;
								}
								if (cknumber >= kcnumber) {
									if (cknumber <= goods_quantity) {
										sell.setSellCount(kcnumber);
										cknumber = cknumber - kcnumber;
										goods_quantity -= cknumber;
										sell.setSellCount(kcnumber);
									} else {
										if (cknumber > 0) {
											errorcount++;
											errorMes += "第" + (i + 1) + "行错误，"
													+ partNumber
													+ "库存数量小于出库数量!请注意！！！！\\n";
											if (error_index == 0) {
												error_index = i + 1;
											}
											break;
										}
									}
								} else {
									sell.setSellCount(cknumber);
									cknumber = 0F;
								}
								if (l != null && !"".equals(l)) {
									// sell.setSellZhishu(Float.parseFloat(l));
								}
							} catch (Exception e1) {
								errorcount++;
								errorMes += "第" + (i + 1) + "行,出库数据异常\\n";
								continue;
							}
							sell.setSellWarehouse(c);
							sell.setGoodHouseName(goods.getGoodHouseName());
							sell.setKuwei(goods.getGoodsPosition());

							sell.setSellMarkId(partNumber);
							sell.setBanBenNumber(a3);
							sell.setKgliao(goods.getKgliao());
							sell.setSellLot(h);

							sell.setSellFormat(b);
							sell.setSellGoods(g);
							sell.setSellUnit(j);
							sell.setGoodsStoreZHUnit(k);
							sell.setStyle("正常出库");
							sell.setGoodsId(goods.getGoodsId());
							sell.setSellDate(n);
							sell.setSellCompanyName(goods.getGoodsCustomer());
							sell.setSellCompanyPeople(p);
							sell.setSellCharger(q);
							sell.setSellSupplier(goods.getGoodsSupplier());
							sell.setSellArtsCard(goods.getGoodsArtsCard());
							sell.setSellProMarkId(goods.getGoodsProMarkId());
							sell.setSellPeople("已确认");
							sell.setSellSendnum(w);
							sell.setOrderNum(x);
							sell.setOutOrderNumer(y);
							sell.setPrintStatus("YES");

							try {
								sell.setGoodsPrice(goods.getGoodsPrice());// 平均单价（库存成本）

								sell.setSellPrice(goods.getGoodsBuyPrice());
								sell.setSellbhsPrice(goods
										.getGoodsBuyBhsPrice());
								sell.setTaxprice(goods.getTaxprice());
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							sell.setSellbhsPrice(goods.getGoodsPrice());

							if (totalDao.save(sell)) {
								Float num = goods.getGoodsCurQuantity();
								// 更新库存
								if (num != null && num > 0) {
									goods.setGoodsCurQuantity(num
											- sell.getSellCount());
									totalDao.update(goods);
								}

								// if ("外购件库".equals(c)) {
								// // 更新占用量
								// // 更新库存数量;
								// String hql_zygoods =
								// "from Goods where goodsMarkId =? and goodsCurQuantity>0 and goodsClass in('占用库','外购件库') ";
								// if (a1 != null && a1.length() > 0) {
								// hql_zygoods += " and goodHouseName='"
								// + a1 + "'";
								// }
								// // 版本
								// if (a3 != null && a3.length() > 0) {
								// hql_zygoods += " and banBenNumber='"
								// + a3 + "'";
								// }
								// // 供料属性
								// hql_zygoods += " and kgliao='" + kgliao
								// + "'";
								// hql_zygoods += " order by goodsLotId";
								//
								// List zygoods_list = totalDao.query(
								// hql_zygoods, partNumber);
								// if (zygoods_list != null
								// && zygoods_list.size() > 0) {
								// Float nowcknumber = sell.getSellCount();
								// for (int n1 = 0; n1 < zygoods_list
								// .size(); n1++) {
								// if (nowcknumber <= 0) {
								// break;
								// }
								// Goods zygoods = (Goods) zygoods_list
								// .get(n1);
								// Float ckCount = zygoods
								// .getGoodsCurQuantity()
								// - nowcknumber;// 占用库存-本次出库数=剩余占用库存
								// // if (ckCount >= 0) {
								// // zygoods
								// // .setGoodsCurQuantity(ckCount);
								// // ckCount = nowcknumber;
								// // nowcknumber = 0F;
								// // } else {
								// // nowcknumber = nowcknumber
								// // - zygoods
								// // .getGoodsCurQuantity();
								// // ckCount = nowcknumber;
								// // zygoods.setGoodsCurQuantity(0F);
								// // }
								// totalDao.update(zygoods);
								// // 在途库做出库记录
								// Sell sell1_zy = new Sell();
								// sell1_zy.setSellWarehouse(zygoods
								// .getGoodsClass());
								// sell1_zy.setOrderNum(x);
								// sell1_zy.setSellMarkId(zygoods
								// .getGoodsMarkId());
								// sell1_zy.setBanBenNumber(zygoods
								// .getBanBenNumber());
								// sell1_zy.setKgliao(zygoods
								// .getKgliao());
								// sell1_zy.setSellCount(ckCount);
								//
								// sell1_zy.setSellArtsCard(zygoods
								// .getGoodsArtsCard());
								// sell1_zy.setSellSupplier(zygoods
								// .getGoodsSupplier());
								// sell1_zy.setSellFormat(zygoods
								// .getGoodsFormat());
								// sell1_zy.setSellLot(zygoods
								// .getGoodsLotId());
								// sell1_zy.setSellAdminName(Util
								// .getLoginUser().getName());
								// sell1_zy.setSellGoods(zygoods
								// .getGoodsFullName());
								// sell1_zy.setGoodHouseName(zygoods
								// .getGoodHouseName());
								// sell1_zy.setSellDate(Util
								// .getDateTime());
								// sell1_zy.setSellTime(Util
								// .getDateTime());
								// sell1_zy.setSellUnit(zygoods
								// .getGoodsUnit());
								// sell1_zy.setSellCharger("admin");
								// // --------------------------------
								// sell1_zy.setSellProMarkId(zygoods
								// .getGoodsMarkId());
								// sell1_zy.setPrintStatus("YES");
								// sell1_zy.setStyle("正常出库");
								// sell1_zy.setSellPeople("已确认");
								// totalDao.save(sell1_zy);
								// }
								// } else {
								// errorcount++;
								// errorMes += "第" + (i + 1) + "行错误，"
								// + partNumber
								// + "没有找到对应的库存!请注意！！！！\\n";
								// if (error_index == 0) {
								// error_index = i + 1;
								// }
								// }
								// }
								// 更新订单完成率 ;
								if (x != null && !"".equals(x) && y != null
										&& !"".equals(y)) {
									String hql_order = "from OrderManager where orderNum=? and outOrderNumber = ?";
									OrderManager order = (OrderManager) totalDao
											.getObjectByCondition(hql_order, x,
													y);
									if (order != null) {
										String hql_pm = "from ProductManager where pieceNumber = ? and orderManager.id=?";
										ProductManager pm = (ProductManager) totalDao
												.getObjectByCondition(hql_pm,
														partNumber, order
																.getId());
										if (pm != null) {
											Float sellCount = Float.valueOf(f);
											Float sellcount1 = pm
													.getSellCount();
											if (sellcount1 == null) {
												sellcount1 = 0f;
											}
											pm.setSellCount(sellcount1
													+ sellCount);
											String hql_sellCount = "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
											Float sumsellCount = (Float) totalDao
													.getObjectByCondition(
															hql_sellCount,
															order.getId());
											if (sumsellCount == null) {
												sumsellCount = 0f;
											}
											sumsellCount += sellCount;
											String hql_sum = "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
											Float num_pro = (Float) totalDao
													.getObjectByCondition(
															hql_sum, order
																	.getId());
											if (num_pro != null && num_pro > 0) {
												order
														.setCompletionrate((sumsellCount / num) * 100);
												totalDao.update(order);
											}

										}
									}

								}
								try {
									corePayableServer.goodsSell(sell);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								strList.add(i);
							}
							succescount++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorcount++;
					errorMes += "第" + (i + 1) + "行,数据异常!" + e.getMessage()
							+ "\\n";
				}
				if (exclecolums % 200 == 0) {
					totalDao.clear();
				}
			}

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}// 关闭流
			wk.close();// 关闭工作薄
			String errs = "";
			if (errorcount > 0) {
				errs = "从第" + error_index + "行开始出现错误，请核对错误后，从第" + error_index
						+ "行开始重新导入(即删除excel中1-" + (error_index - 1)
						+ "行的数据)!\\n";
			}
			msg = "已成功导入" + succescount + "个，失败" + errorcount + "个，" + errs
					+ "失败的行数为：" + errorMes;
		} else {
			msg = "没有获取到行数";
		}
		return msg;

	}

	@Override
	public boolean getHasOut(Integer id) {
		// TODO Auto-generated method stub
		Float count = (Float) totalDao
				.getObjectByCondition(
						"select count(id) from Procard where hasCount is null or hasCount>0 and procard.id=?",
						id);
		if (count == null || count == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Map<Integer, Object> showWwPlanoutDetail(String wwOrderNumber,
			Integer id) {
		WaigouOrder order = null;
		Users user = Util.getLoginUser();
		boolean totalEnough = true;
		Map<String, Integer> flagMap = new HashMap<String, Integer>();
		Integer maxFlag = 0;
		String gysSql = " and 1=1";
		if (user == null) {
			throw new RuntimeException("请先登录");
		} else if (user.getDept().equals("供应商")) {
			totalEnough = false;// 供应商无出库权限
			gysSql = " and userId=" + user.getId();
		}

		if (wwOrderNumber == null || wwOrderNumber.length() == 0) {
			if (id == null || id == 0) {
				throw new RuntimeException("无效的访问!");
			}
			totalEnough = false;
			order = (WaigouOrder) totalDao.getObjectByCondition(
					"from WaigouOrder where id=?" + gysSql, id);
		} else {
			order = (WaigouOrder) totalDao.getObjectByCondition(
					"from WaigouOrder where planNumber=? and type='外委'"
							+ gysSql, wwOrderNumber);
		}
		if (order != null) {
			List<WaigouPlan> wgPlanList = totalDao.query(
					"from WaigouPlan where waigouOrder.id=? and status not in('删除','取消')", order.getId());
			// 获取已出库的明细
			List<Sell> sellList = totalDao.query("from Sell where wgOrderId=?",
					order.getId());
			Map<Integer, Object> map = new HashMap<Integer, Object>();
			map.put(1, order);
			map.put(2, wgPlanList);
			map.put(3, sellList);
			return map;
		} else {
			throw new RuntimeException("没有找到对应的订单!");
		}
	}

	/**
	 * 查询外委需求材料明细
	 */
	@Override
	public Map<Integer, Object> getWwclDetail(String wwOrderNumber, Integer id) {
		// TODO Auto-generated method stub
		WaigouOrder order = null;
		Users user = Util.getLoginUser();
		boolean totalEnough = true;
		Map<String, Integer> flagMap = new HashMap<String, Integer>();
		Integer maxFlag = 0;
		String gysSql = " and 1=1";
		if (user == null) {
			throw new RuntimeException("请先登录");
		} else if (user.getDept().equals("供应商")) {
			totalEnough = false;// 供应商无出库权限
			gysSql = " and userId=" + user.getId();
		}

		if (wwOrderNumber == null || wwOrderNumber.length() == 0) {
			if (id == null || id == 0) {
				throw new RuntimeException("无效的访问!");
			}
			totalEnough = false;
			order = (WaigouOrder) totalDao.getObjectByCondition(
					"from WaigouOrder where id=?" + gysSql, id);
		} else {
			order = (WaigouOrder) totalDao.getObjectByCondition(
					"from WaigouOrder where planNumber=? and type='外委'"
							+ gysSql, wwOrderNumber);
		}
		if (order != null) {
			Map<Integer, Object> map = new HashMap<Integer, Object>();
			map.put(1, order);
			// 获取已出库的明细
			List<Sell> sellList = totalDao.query("from Sell where wgOrderId=?",
					order.getId());
			map.put(4, sellList);
			Set<WaigouPlan> wwpSet = order.getWwpSet();
			if (wwpSet != null && wwpSet.size() > 0) {
				// List<Sell> sellList = new ArrayList<Sell>();
				List<Goods> returngoodsList = new ArrayList<Goods>();
				Map<String, Procard> wgjmap = new HashMap();
				if (order.getWwSource().equals("BOM外委")) {
					for (WaigouPlan plan : wwpSet) {
						// if(plan.getApplyCount()!=null&&plan.getApplyCount()>0){
						// //已领
						// continue;
						// }
						List<Procard> procardList = totalDao
								.query(
										"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by markId",
										plan.getId());
						if (procardList != null && procardList.size() > 0) {
							for (Procard procard : procardList) {
								// 获取第一道工序号,非第一道工序外委需要领在制品
								Integer minProNo = (Integer) totalDao
										.getObjectByCondition(
												"select min(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')",
												procard.getId());
								if (minProNo == null) {
									throw new RuntimeException("外委数据异常,没有找到零件"
											+ procard.getMarkId() + "的工序");
								}
								// 判断外委工序是否包含第一道
								boolean b = false;
								String[] processNos = plan.getProcessNOs()
										.split(";");
								if (processNos != null && processNos.length > 0) {
									for (String s : processNos) {
										if (s.equals(minProNo + "")) {
											b = true;
										}
									}
								}
								String addSql = "";
								Integer lastProcessNo = null;
								String lastProcessName = null;
								Float needCount = (Float) totalDao
										.getObjectByCondition(
												"select sum(procardCount) from ProcardWGCenter where wgOrderId=? and procardId=? ",
												plan.getId(), procard.getId());
								List<Goods> goodsTempList = new ArrayList<Goods>();
								if (!b) {
									lastProcessNo = (Integer) totalDao
											.getObjectByCondition(
													"select processNO from ProcessInfor where procard.id=? and processNO<'"
															+ processNos[0]
															+ "' and (dataStatus is null or dataStatus!='删除') order by processNO desc",
													procard.getId());
									if (lastProcessNo != null) {
										lastProcessName = (String) totalDao
												.getObjectByCondition(
														"select processName from ProcessInfor where procard.id=? and processNO ='"
																+ lastProcessNo
																+ "' and (dataStatus is null or dataStatus!='删除') order by processNO desc",
														procard.getId());
									}
									addSql = " and processNo=" + lastProcessNo;
									for (Sell sell : sellList) {
										if (sell.getSellMarkId().equals(
												procard.getMarkId())
												&& sell.getSellLot() != null
												&& sell.getSellLot().equals(
														procard.getSelfCard())
												&& sell.getProcessNo() != null
												&& sell.getProcessNo().equals(
														lastProcessNo)) {
											needCount -= sell.getSellCount();
										}
									}
									if (needCount != null && needCount > 0) {
										List<Goods> goodsList = totalDao
												.query(
														"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																+ addSql,
														procard.getMarkId(),
														procard.getSelfCard());
										if (goodsList != null
												&& goodsList.size() > 0) {
											for (Goods g : goodsList) {
												if (flagMap.get(procard
														.getMarkId()) != null) {
													g
															.setFlag(flagMap
																	.get(procard
																			.getMarkId()));
												} else {
													flagMap.put(procard
															.getMarkId(),
															maxFlag);
													g.setFlag(maxFlag);
													maxFlag++;
												}
												g.setOrder_Id(procard.getId());
												g.setIsEnough(true);
												if (g.getGoodsCurQuantity() >= needCount) {
													g
															.setGoodsCurQuantity(needCount);// 记录请领
													g
															.setGoodsBeginQuantity(needCount);// 记录实发（页面传回来的实发大于请领时为余料）
													needCount = 0f;
													goodsTempList.add(g);
													// returngoodsList.addAll(goodsTempList);
													break;
												} else {
													g
															.setGoodsBeginQuantity(g
																	.getGoodsCurQuantity());
													needCount -= g
															.getGoodsCurQuantity();
													goodsTempList.add(g);
													// returngoodsList.add(g);
												}
											}
											if (needCount > 0) {
												// throw new
												// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
												totalEnough = false;
												Goods g = goodsList.get(0);
												g.setProcessNo(lastProcessNo);
												g.setTqlCount(needCount);
												g.setGoodsCurQuantity(0f);
												g.setIsEnough(false);
												for (Goods gtmp : goodsTempList) {
													gtmp.setIsEnough(false);
												}
												goodsTempList.add(g);
											}
											returngoodsList
													.addAll(goodsTempList);
										} else {
											Goods g = new Goods();
											g.setProcessNo(lastProcessNo);
											g.setProcessName(lastProcessName);
											g.setOrder_Id(procard.getId());
											g.setGoodsMarkId(procard
													.getMarkId());
											g.setGoodsFullName(procard
													.getProName());
											g.setGoodsFormat(procard
													.getSpecification());
											g.setGoodsUnit(procard.getUnit());
											g.setIsEnough(false);
											g.setTqlCount(needCount);
											returngoodsList.add(g);
											totalEnough = false;
										}
									}
								} else {
									List<Procard> sonProcardList = totalDao
											.query(
													"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除') and id in(select procardId from ProcessInforWWProcard where (status is null or status not in ('删除','取消')) and "
															+ "wwxlId in(select wwxlId from ProcardWGCenter where procardId=? and wgOrderId=?))",
													procard.getId(), procard
															.getId(), plan
															.getId());
									if (sonProcardList != null
											&& sonProcardList.size() > 0) {
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")
													&& (son.getNeedProcess() == null || !son
															.getNeedProcess()
															.equals("yes"))) {
												needCount2 = needCount
														* son.getQuanzi2()
														/ son.getQuanzi1();
												String key = son.getMarkId()
														+ son.getKgliao();
												if (son.getBanBenNumber() != null) {
													key += son
															.getBanBenNumber();
												}
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													p.setFilnalCount(p
															.getFilnalCount()
															+ needCount2);
												} else {
													son
															.setFilnalCount(needCount2);
													wgjmap.put(key, son);
												}

											} else {
												ProcessInfor maxsonprocess = (ProcessInfor) totalDao
														.getObjectByCondition(
																"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																		+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
																son.getId(),
																son.getId());
												needCount2 = needCount
														* son.getCorrCount();
												if (maxsonprocess != null) {
													for (Sell sell : sellList) {
														if (sell
																.getSellMarkId()
																.equals(
																		son
																				.getMarkId())
																&& sell
																		.getSellLot() != null
																&& sell
																		.getSellLot()
																		.equals(
																				son
																						.getSelfCard())
																&& sell
																		.getProcessNo() != null
																&& sell
																		.getProcessNo()
																		.equals(
																				maxsonprocess
																						.getProcessNO())) {
															needCount2 -= sell
																	.getSellCount();
														}
													}
													if (needCount2 > 0) {
														List<Goods> goodsList = totalDao
																.query(
																		"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																				+ " and processNo=?",
																		son
																				.getMarkId(),
																		son
																				.getSelfCard(),
																		maxsonprocess
																				.getProcessNO());
														if (goodsList != null
																&& goodsList
																		.size() > 0) {
															for (Goods g : goodsList) {
																if (flagMap
																		.get(son
																				.getMarkId()) != null) {
																	g
																			.setFlag(flagMap
																					.get(son
																							.getMarkId()));
																} else {
																	flagMap
																			.put(
																					son
																							.getMarkId(),
																					maxFlag);
																	g
																			.setFlag(maxFlag);
																	maxFlag++;
																}
																g
																		.setOrder_Id(son
																				.getId());
																g
																		.setIsEnough(true);
																if (g
																		.getGoodsCurQuantity() >= needCount2) {
																	g
																			.setGoodsCurQuantity(needCount2);// 记录请领
																	g
																			.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																	needCount2 = 0f;
																	goodsTempList2
																			.add(g);
																	break;
																} else {
																	g
																			.setGoodsBeginQuantity(g
																					.getGoodsCurQuantity());
																	needCount2 -= g
																			.getGoodsCurQuantity();
																	goodsTempList2
																			.add(g);
																	// returngoodsList.add(g);
																}
															}
															if (needCount2 > 0) {
																// throw new
																// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
																totalEnough = false;
																Goods g = goodsList
																		.get(0);
																g
																		.setProcessNo(maxsonprocess
																				.getProcessNO());
																g
																		.setTqlCount(needCount2);
																g
																		.setGoodsCurQuantity(0f);
																g
																		.setIsEnough(false);
																for (Goods gtmp : goodsTempList) {
																	gtmp
																			.setIsEnough(false);
																}
																goodsTempList2
																		.add(g);
															}
															returngoodsList
																	.addAll(goodsTempList2);
														} else {
															Goods g = new Goods();
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setProcessName(maxsonprocess
																			.getProcessName());
															g.setOrder_Id(son
																	.getId());
															g
																	.setGoodsMarkId(son
																			.getMarkId());
															g
																	.setGoodsFullName(son
																			.getProName());
															g
																	.setGoodsFormat(son
																			.getSpecification());
															g.setGoodsUnit(son
																	.getUnit());
															g
																	.setGoodsLotId(son
																			.getSelfCard());
															g
																	.setIsEnough(false);
															g
																	.setTqlCount(needCount2);
															returngoodsList
																	.add(g);
															totalEnough = false;
														}
													}
												}
											}
										}
									} else {
										// 领取下层委外零件
										sonProcardList = totalDao
												.query(
														"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and (needProcess is null or needProcess!='yes'))) order by markId",
														procard.getId());
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")) {
												needCount2 = needCount
														* son.getQuanzi2()
														/ son.getQuanzi1();
											} else {
												needCount2 = needCount
														* son.getCorrCount();
											}
											if (maxsonprocess != null) {
												for (Sell sell : sellList) {
													if (sell
															.getSellMarkId()
															.equals(
																	son
																			.getMarkId())
															&& sell
																	.getSellLot() != null
															&& sell
																	.getSellLot()
																	.equals(
																			son
																					.getSelfCard())
															&& sell
																	.getProcessNo() != null
															&& sell
																	.getProcessNo()
																	.equals(
																			maxsonprocess
																					.getProcessNO())) {
														needCount2 -= sell
																.getSellCount();
													}
												}
												if (needCount2 > 0) {
													List<Goods> goodsList = totalDao
															.query(
																	"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																			+ " and processNo=?",
																	son
																			.getMarkId(),
																	son
																			.getSelfCard(),
																	maxsonprocess
																			.getProcessNO());
													if (goodsList != null
															&& goodsList.size() > 0) {
														for (Goods g : goodsList) {
															if (flagMap
																	.get(son
																			.getMarkId()) != null) {
																g
																		.setFlag(flagMap
																				.get(son
																						.getMarkId()));
															} else {
																flagMap
																		.put(
																				son
																						.getMarkId(),
																				maxFlag);
																g
																		.setFlag(maxFlag);
																maxFlag++;
															}
															g.setOrder_Id(son
																	.getId());
															g.setIsEnough(true);
															if (g
																	.getGoodsCurQuantity() >= needCount2) {
																g
																		.setGoodsCurQuantity(needCount2);// 记录请领
																g
																		.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																needCount2 = 0f;
																goodsTempList2
																		.add(g);
																break;
															} else {
																g
																		.setGoodsBeginQuantity(g
																				.getGoodsCurQuantity());
																needCount2 -= g
																		.getGoodsCurQuantity();
																goodsTempList2
																		.add(g);
																// returngoodsList.add(g);
															}
														}
														if (needCount2 > 0) {
															// throw new
															// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
															totalEnough = false;
															Goods g = goodsList
																	.get(0);
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setTqlCount(needCount2);
															g
																	.setGoodsCurQuantity(0f);
															g
																	.setIsEnough(false);
															for (Goods gtmp : goodsTempList) {
																gtmp
																		.setIsEnough(false);
															}
															goodsTempList2
																	.add(g);
														}
														returngoodsList
																.addAll(goodsTempList2);
													} else {
														Goods g = new Goods();
														g
																.setProcessNo(maxsonprocess
																		.getProcessNO());
														g
																.setProcessName(maxsonprocess
																		.getProcessName());
														g.setOrder_Id(son
																.getId());
														g.setGoodsMarkId(son
																.getMarkId());
														g.setGoodsFullName(son
																.getProName());
														g
																.setGoodsFormat(son
																		.getSpecification());
														g.setGoodsUnit(son
																.getUnit());
														g.setGoodsLotId(son
																.getSelfCard());
														g.setIsEnough(false);
														g
																.setTqlCount(needCount2);
														returngoodsList.add(g);
														totalEnough = false;
													}
												}
											}
										}
									}

								}

							}
						}

					}
				} else {
					for (WaigouPlan plan : wwpSet) {
						if (plan.getWwType().equals("工序外委")) {
							ProcessInforWWApplyDetail applyDetail = (ProcessInforWWApplyDetail) totalDao
									.getObjectById(
											ProcessInforWWApplyDetail.class,
											plan.getWwDetailId());
							if (applyDetail != null) {
								Procard procard = byProcardId(applyDetail);
								boolean b = false;
								if (procard != null) {
									// 获取s第一道工序号,非第一道工序外委需要领在制品
									Integer minProNo = (Integer) totalDao
											.getObjectByCondition(
													"select min(processNO) from ProcessInfor where procard.id=?",
													procard.getId());
									if (minProNo == null) {
										throw new RuntimeException(
												"外委数据异常,没有找到零件"
														+ procard.getMarkId()
														+ "的工序");
									}
									String[] processNos = applyDetail
											.getProcessNOs().split(";");
									if (processNos != null
											&& processNos.length > 0) {
										for (String s : processNos) {
											if (s.equals(minProNo + "")) {
												b = true;
											}
										}
									}
									if (!b) {
										List<Goods> goodsTempList = new ArrayList<Goods>();
										// 需要在制品出库
										Float needCount = applyDetail
												.getApplyCount();
										if (needCount != null && needCount > 0) {
											List<Goods> goodsList = totalDao
													.query(
															"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0",
															procard.getMarkId(),
															procard
																	.getSelfCard());
											if (goodsList != null
													&& goodsList.size() > 0) {
												for (Goods g : goodsList) {
													if (flagMap.get(procard
															.getMarkId()) != null) {
														g
																.setFlag(flagMap
																		.get(procard
																				.getMarkId()));
													} else {
														flagMap.put(procard
																.getMarkId(),
																maxFlag);
														g.setFlag(maxFlag);
														maxFlag++;
													}
													g.setOrder_Id(procard
															.getId());
													g.setIsEnough(true);
													if (g.getGoodsCurQuantity() >= needCount) {
														g
																.setGoodsBeginQuantity(needCount);
														g
																.setGoodsCurQuantity(needCount);
														g.setTqlCount(0f);
														needCount = 0f;
														goodsTempList.add(g);
														// returngoodsList.addAll(goodsTempList);
														break;
													} else {
														g.setTqlCount(0f);
														g
																.setGoodsBeginQuantity(g
																		.getGoodsCurQuantity());
														needCount -= g
																.getGoodsCurQuantity();
														// returngoodsList.add(g);
														goodsTempList.add(g);
													}
												}
												if (needCount > 0) {
													totalEnough = false;
													// throw new
													// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
													Goods g = goodsList.get(0);
													g.setTqlCount(needCount);
													g.setGoodsCurQuantity(0f);
													g.setIsEnough(false);
													for (Goods gTemp : goodsTempList) {
														gTemp
																.setIsEnough(false);
														;
													}
													goodsTempList.add(g);
												}
												returngoodsList
														.addAll(goodsTempList);
											} else {
												Goods g = new Goods();
												if (flagMap.get(procard
														.getMarkId()) != null) {
													g
															.setFlag(flagMap
																	.get(procard
																			.getMarkId()));
												} else {
													flagMap.put(procard
															.getMarkId(),
															maxFlag);
													g.setFlag(maxFlag);
													maxFlag++;
												}
												g.setOrder_Id(procard.getId());
												g.setGoodsMarkId(procard
														.getMarkId());
												g.setGoodsFullName(procard
														.getProName());
												g.setGoodsFormat(procard
														.getSpecification());
												g.setGoodsUnit(procard
														.getUnit());
												g.setIsEnough(false);
												g.setTqlCount(needCount);
												returngoodsList.add(g);
												totalEnough = false;
											}
										}
									}
								}
								// 合并要领的外购件
								// 出外购件
								List<ProcessInforWWProcard> processwwprocardList = totalDao
										.query(
												"from ProcessInforWWProcard where applyDtailId=? and (status is null or status not in ('删除','取消'))",
												applyDetail.getId());
								if (processwwprocardList != null
										&& processwwprocardList.size() > 0) {
									for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
										// 存在外购件，判断库存是否充足
										// 第三步计算外购件需领数量
										// 外购件需领数量
										// float count = applyDetail
										// .getApplyCount();
										if (processInforWWProcard.getHascount() == null) {
											processInforWWProcard
													.setHascount(processInforWWProcard
															.getApplyCount());
										}
										float count = processInforWWProcard
												.getHascount();
										if (count == 0) {
											// 已经领过了
											continue;
										}
										Procard son = byProcardId(processInforWWProcard);
										boolean need=true;
										if(b&&son.getProcardStyle().equals("自制")){
											//如果是首工序外委下层自制件判断是否为尾工序外委给了同一个供应商
											Integer xcmaxProcessno = (Integer) totalDao.getObjectByCondition("select max(processNO) from ProcessInfor " +
													"where procard.id=? and (dataStatus is null or dataStatus!='删除')", son.getId());
											Float twwCount = (Float) totalDao.getObjectByCondition("select count(*) from WaigouPlan where gysId=?" +
													" and id in(select wgOrderId from ProcardWGCenter where procardId=?) and status not in('删除','取消')" +
													" and (processNOs='"+xcmaxProcessno+"' or processNOs like '%;"+xcmaxProcessno+"' )",
													order.getGysId(), son.getId());
											if(twwCount!=null&&twwCount>0){
												need=false;
											}
										}
										if(need){
											// count = count * son.getQuanzi2()
											// / son.getQuanzi1();
											String key = son.getMarkId()
											+ son.getKgliao();
											if (son.getBanBenNumber() != null) {
												key += son.getBanBenNumber();
											}
											if (wgjmap.get(key) != null) {
												Procard p = wgjmap.get(key);
												p.setFilnalCount(p.getFilnalCount()
														+ count);
											} else {
												son.setFilnalCount(count);
												wgjmap.put(key, son);
											}
										}

									}
								}

							}
						} else {
							List<ProcessInforWWApplyDetail> wwDetaiList = totalDao
									.query(
											"from ProcessInforWWApplyDetail where id in( select wwxlId from ProcardWGCenter where wgOrderId=?)",
											plan.getId());
							if (wwDetaiList != null && wwDetaiList.size() > 0) {
								for (ProcessInforWWApplyDetail applyDetail : wwDetaiList) {
									Procard procard = byProcardId(applyDetail);
									if (procard != null) {
										// 领取下层委外零件
										List<Procard> sonProcardList = totalDao
												.query(
														"from Procard where (wwblCount is null or wwblCount=0) and procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and needProcess='yes')) order by markId",
														procard.getId());
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")) {
												needCount2 = applyDetail
														.getApplyCount()
														* son.getQuanzi2()
														/ son.getQuanzi1();
											} else {
												needCount2 = applyDetail
														.getApplyCount()
														* son.getCorrCount();
											}
											if (maxsonprocess != null) {
												for (Sell sell : sellList) {
													if (sell
															.getSellMarkId()
															.equals(
																	son
																			.getMarkId())
															&& sell
																	.getSellLot() != null
															&& sell
																	.getSellLot()
																	.equals(
																			son
																					.getSelfCard())
															&& sell
																	.getProcessNo() != null
															&& sell
																	.getProcessNo()
																	.equals(
																			maxsonprocess
																					.getProcessNO())) {
														needCount2 -= sell
																.getSellCount();
													}
												}
												if (needCount2 > 0) {
													List<Goods> goodsList = totalDao
															.query(
																	"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																			+ " and processNo=?",
																	son
																			.getMarkId(),
																	son
																			.getSelfCard(),
																	maxsonprocess
																			.getProcessNO());
													if (goodsList != null
															&& goodsList.size() > 0) {
														for (Goods g : goodsList) {
															if (flagMap
																	.get(son
																			.getMarkId()) != null) {
																g
																		.setFlag(flagMap
																				.get(son
																						.getMarkId()));
															} else {
																flagMap
																		.put(
																				son
																						.getMarkId(),
																				maxFlag);
																g
																		.setFlag(maxFlag);
																maxFlag++;
															}
															g.setOrder_Id(son
																	.getId());
															g.setIsEnough(true);
															if (g
																	.getGoodsCurQuantity() >= needCount2) {
																g
																		.setGoodsCurQuantity(needCount2);// 记录请领
																g
																		.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																needCount2 = 0f;
																goodsTempList2
																		.add(g);
																break;
															} else {
																g
																		.setGoodsBeginQuantity(g
																				.getGoodsCurQuantity());
																needCount2 -= g
																		.getGoodsCurQuantity();
																goodsTempList2
																		.add(g);
																// returngoodsList.add(g);
															}
														}
														if (needCount2 > 0) {
															// throw new
															// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
															totalEnough = false;
															Goods g = goodsList
																	.get(0);
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setTqlCount(needCount2);
															g
																	.setGoodsCurQuantity(0f);
															g
																	.setIsEnough(false);
															for (Goods gtmp : goodsTempList2) {
																gtmp
																		.setIsEnough(false);
															}
															goodsTempList2
																	.add(g);
														}
														returngoodsList
																.addAll(goodsTempList2);
													} else {
														Goods g = new Goods();
														g
																.setProcessNo(maxsonprocess
																		.getProcessNO());
														g
																.setProcessName(maxsonprocess
																		.getProcessName());
														g.setOrder_Id(son
																.getId());
														g.setGoodsMarkId(son
																.getMarkId());
														g.setGoodsFullName(son
																.getProName());
														g
																.setGoodsFormat(son
																		.getSpecification());
														g.setGoodsUnit(son
																.getUnit());
														g.setGoodsLotId(son
																.getSelfCard());
														g.setIsEnough(false);
														g
																.setTqlCount(needCount2);
														returngoodsList.add(g);
														totalEnough = false;
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
				if (wgjmap.size() > 0) {
					for (String wgj : wgjmap.keySet()) {
						Procard p = wgjmap.get(wgj);
						List<Goods> goodsTempList = new ArrayList<Goods>();
						// 外购件需领数量
						float count = p.getFilnalCount();// 第一遍循环累计在这个属性中
						float tqlcount = 0f;
						String banbenSql = null;
						if (p.getBanBenNumber() == null
								|| p.getBanBenNumber().length() == 0) {
							banbenSql = " and (banBenNumber is null or banBenNumber ='')";
						} else {
							banbenSql = " and banBenNumber ='"
									+ p.getBanBenNumber() + "'";
						}
						// 单零件重量（一般为原材料使用）
						Float bizhong = (Float) totalDao
								.getObjectByCondition(
										"select bili from YuanclAndWaigj where markId =? ",
										p.getMarkId());
						Goods googsTemp = (Goods) totalDao
								.getObjectByCondition(
										"from Goods where goodsCurQuantity>0 and  goodsZhishu>0 and goodsMarkId=? "
												+ banbenSql, p.getMarkId());
						if (bizhong == null) {
							if (googsTemp != null) {
								bizhong = googsTemp.getGoodsCurQuantity()
										/ googsTemp.getGoodsZhishu();
							}
						}

						float countTem = count;
						if (count == 0) {
							continue;
						}
						// 余料查询
						if (bizhong != null && bizhong > 0) {// 有比重才会有余料
							Float single = null;// 单间需要重量
							if (googsTemp != null
									&& p.getUnit().equals(
											googsTemp.getGoodsUnit())) {// 单位一致
								single = p.getQuanzi2() / p.getQuanzi1();
							} else {
								single = bizhong * p.getQuanzi2()
										/ p.getQuanzi1();
							}
							List<Goods> yuliaoList = totalDao
									.query(
											" from Goods where goodsClass='余料库' and goodsCurQuantity>"
													+ single
													+ " and goodsMarkId=? and llGysId=? and kgliao=? and (yllock is null or yllock='' or yllock='no' or (yllock = 'yes' and ylMarkId=?)) and (fcStatus is null or fcStatus='可用')",
											p.getMarkId(), order.getGysId(), p
													.getKgliao(), p.getMarkId());
							if (yuliaoList != null && yuliaoList.size() > 0) {
								for (Goods goods : yuliaoList) {
									goods.setOrder_Id(p.getId());
									if (flagMap.get(p.getMarkId()) != null) {
										goods.setFlag(flagMap
												.get(p.getMarkId()));
									} else {
										flagMap.put(p.getMarkId(), maxFlag);
										goods.setFlag(maxFlag);
										maxFlag++;
									}
									goods.setShowType("余料");
									goods.setHqlCount(0f);
									goods.setTqlCount(0f);
									goods.setIsEnough(true);
									goods.setIsChangeSf(false);
									if (p.getUnit().equals(
											googsTemp.getGoodsUnit())) {
										if (googsTemp.getGoodsStoreZHUnit() != null
												&& googsTemp
														.getGoodsStoreZHUnit()
														.length() > 0) {
											goods.setQlUnit(googsTemp
													.getGoodsStoreZHUnit());
										} else {
											goods.setQlUnit("张");
										}
									} else {
										goods.setQlUnit(p.getUnit());
									}
									goods.setGoodsZhishu(1f);// 余料以单张保存
									// goods.setGoodsCurQuantity();//余料重量即领取重量
									Float singleCount = (float) Math
											.floor(goods.getGoodsCurQuantity()
													/ single);
									if (googsTemp != null
											&& p.getUnit().equals(
													googsTemp.getGoodsUnit())) {// 单位一致
										if ((singleCount * single) >= count) {// 余料足够
											goods.setGoodsBeginQuantity(count);// 余料需要重量
											goodsTempList.add(goods);
											// returngoodsList.addAll(goodsTempList);
											count = 0;
											break;
										} else {
											goods
													.setGoodsBeginQuantity(singleCount
															* single);// 余料需要重量
											// returngoodsList.add(goods);
											count = count - singleCount
													* single;
											goodsTempList.add(goods);
										}
									} else {
										if (singleCount * single >= count
												* bizhong) {// 余料足够
											goods.setGoodsBeginQuantity(count
													* bizhong);// 余料需要重量
											count = 0;
											goodsTempList.add(goods);
											// returngoodsList.addAll(goodsTempList);
											break;
										} else {
											goods
													.setGoodsBeginQuantity(singleCount
															* single);// 余料需要重量
											// returngoodsList.add(goods);
											count = count - singleCount;
											goodsTempList.add(goods);
										}
									}
								}
								// returngoodsList.addAll(goodsTempList);
							}

						}
						if (count == 0) {
							continue;
						}
						// 请领数量
						float lsCount = count;// 张数
						float surlsCount = count;// 实际所需
						String goodsClass = "外购件库','中间库";
						String goodsClassSql = null;
						// if ((bizhong == null || bizhong == 0)
						// && p.getProductStyle() != null
						// && p.getProductStyle().equals("试制")) {
						// goodsClass = "试制库";// 试制的外购件去试制库取
						// goodsClassSql = " and goodsClass ='"
						// + goodsClass + "'";
						// } else {
						String kgsql = " and 1=1";
						if (p.getKgliao() != null && p.getKgliao().length() > 0) {
							kgsql += " and kgliao ='" + p.getKgliao() + "'";
						}
						goodsClassSql = " and ((goodsClass in ('" + goodsClass
								+ "') " + kgsql + " ) or goodsClass = '备货库')";
						// }

						String hqlGoods = " from Goods where goodsMarkId='"
								+ p.getMarkId()
								+ "'"
								+ goodsClassSql
								+ " and  goodsCurQuantity>0  and (fcStatus is null or fcStatus='可用') "
								+ banbenSql + "order by goodsLotId asc";
						List<Goods> listG = totalDao.find(hqlGoods);
						// Double d2 = Math.ceil(count);
						// count =
						// Float.parseFloat(d2.toString());
						if (listG.size() > 0) {
							int n = 0;
							for (Goods goo : listG) {
								n++;
								goo.setOrder_Id(p.getId());
								if (flagMap.get(p.getMarkId()) != null) {
									goo.setFlag(flagMap.get(p.getMarkId()));
								} else {
									flagMap.put(p.getMarkId(), maxFlag);
									goo.setFlag(maxFlag);
									maxFlag++;
								}
								goo.setShowType("外购件");
								goo.setHqlCount(0f);
								goo.setTqlCount(0f);
								goo.setGoodsBeginQuantity(0f);
								goo.setIsEnough(true);
								goo.setIsChangeSf(false);
								if (bizhong != null && bizhong > 0) {// 有比重需要换算单位
									if (p.getUnit().equals(goo.getGoodsUnit())
											&& null != goo.getGoodsZhishu()
											&& goo.getGoodsZhishu() > 0) {// 单位一致有支数,用lsCount计数
										if (n == 1) {
											lsCount = (float) Math.ceil(lsCount
													/ bizhong);
										}
										if (bizhong != null && bizhong > 0) {
											if (goo.getGoodsZhishu() > lsCount) {
												goo.setGoodsZhishu(lsCount);
												// 计算重量
												if (lsCount * bizhong <= goo
														.getGoodsCurQuantity()) {
													goo
															.setGoodsBeginQuantity(lsCount
																	* bizhong);
												}
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												lsCount = 0;
												count = 0;
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
												// returngoodsList.addAll(goodsTempList);
												break;
											} else if (goo.getGoodsZhishu() == lsCount) {
												goo.setGoodsZhishu(lsCount);
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												// 支数刚好情况重量，不重新计算重量
												// goo.setGoodsCurQuantity(lsCount
												// * bizhong);
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												lsCount = 0;
												count = 0;
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
												// returngoodsList.addAll(goodsTempList);
												break;
											} else {
												lsCount = lsCount
														- goo.getGoodsZhishu();
												goo.setGoodsBeginQuantity(goo
														.getGoodsZhishu()
														* bizhong);
												if (goo.getGoodsZhishu()
														* bizhong <= goo
														.getGoodsCurQuantity()) {
													goo.setGoodsCurQuantity(goo
															.getGoodsZhishu()
															* bizhong);
												}
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
											}
										}
									} else if (p.getUnit().equals(
											goo.getGoodsUnit())
											&& (null == goo.getGoodsZhishu() || goo
													.getGoodsZhishu() == 0)) {// 单位一致无支数
										// 此情况不需要比重,数据有误
									} else if (null != goo.getGoodsZhishu()
											&& goo.getGoodsZhishu() > 0) {// 单位不一致,
										// 有支数用count计数
										if (bizhong == null || bizhong == 0) {// 用当前库存重新计算比重
											bizhong = goo.getGoodsCurQuantity()
													/ goo.getGoodsZhishu();
										}
										if (n == 1) {
											surlsCount = count * bizhong;
										}
										if (goo.getGoodsZhishu() >= count) {
											goo.setGoodsZhishu(count);
											// 计算重量
											goo.setGoodsCurQuantity(count
													* bizhong);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											// returngoodsList.add(goo);
											goodsTempList.add(goo);
											// returngoodsList.addAll(goodsTempList);
											count = 0;
											break;
										} else if (goo.getGoodsZhishu() == count) {
											goo.setGoodsZhishu(count);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											// 支数刚好情况重量，不重新计算重量
											// goo.setGoodsCurQuantity(lsCount
											// * bizhong);
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											// returngoodsList.add(goo);
											goodsTempList.add(goo);
											// returngoodsList.addAll(goodsTempList);
											count = 0;
											break;
										} else {
											goo.setGoodsCurQuantity(goo
													.getGoodsZhishu()
													* bizhong);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											returngoodsList.add(goo);
											count = count
													- goo.getGoodsZhishu();
											goodsTempList.add(goo);
										}

									} else {// 单位不一致无支数,无法计算

									}
								} else {// 无比重
									if (goo.getGoodsCurQuantity() >= count) {
										goo.setGoodsCurQuantity(count);
										goo.setGoodsBeginQuantity(count);
										// goo.setGoodsZhishu(count);
										count = 0;
										goo.setQlUnit(p.getUnit());
										// returngoodsList.add(goo);
										goodsTempList.add(goo);
										// returngoodsList.addAll(goodsTempList);
										break;
									} else {
										goo.setCkCount(goo
												.getGoodsCurQuantity());
										count -= goo.getGoodsCurQuantity();
										goo.setGoodsBeginQuantity(goo
												.getGoodsCurQuantity());
										// goo.setGoodsZhishu(goo
										// .getGoodsCurQuantity());
										goo.setQlUnit(p.getUnit());
										// returngoodsList.add(goo);
										goodsTempList.add(goo);
									}
								}

							}
							if (count > 0) {// 库存不够
								Goods gd = new Goods();
								gd.setOrder_Id(p.getId());
								gd.setGoodsMarkId(p.getMarkId());
								gd.setGoodsLotId(p.getSelfCard());
								gd.setBanBenNumber(p.getBanBenNumber());
								gd.setGoodsUnit(p.getUnit());
								gd.setGoodsCurQuantity(0f);
								gd.setHqlCount(0f);
								gd.setTqlCount(count);
								gd.setIsEnough(false);
								gd.setKgliao(p.getKgliao());
								// returngoodsList.add(gd);
								for (Goods gsTemp : goodsTempList) {
									gsTemp.setIsEnough(false);
								}
								goodsTempList.add(gd);
								totalEnough = false;
							}
							returngoodsList.addAll(goodsTempList);
						} else {
							String hqlGoods2 = " from Goods where goodsMarkId='"
									+ p.getMarkId()
									+ "'"
									+ goodsClassSql
									+ " and goodsCurQuantity=0 and (fcStatus is null or fcStatus='可用') order by goodsLotId asc";
							List<Goods> listG2 = totalDao.find(hqlGoods2);
							if (listG2.size() > 0) {
								Goods goo = listG2.get(0);
								goo.setOrder_Id(p.getId());
								goo.setHqlCount(0f);
								goo.setTqlCount(count);
								goo.setIsEnough(false);
								goo.setQlUnit(p.getUnit());
								returngoodsList.add(goo);
								totalEnough = false;
							} else {
								totalEnough = false;
								Goods goo = new Goods();
								goo.setOrder_Id(p.getId());
								goo.setHqlCount(0f);
								goo.setTqlCount(count);
								goo.setIsEnough(false);
								goo.setGoodsFullName(p.getProName());
								goo.setGoodsMarkId(p.getMarkId());
								goo.setGoodsId(0);
								goo.setQlUnit(p.getUnit());
								goo.setGoodsCurQuantity(0f);
								goo.setGoodsUnit(p.getUnit());
								goo.setKgliao(p.getKgliao());
								returngoodsList.add(goo);
							}
						}
					}
				}

				map.put(2, returngoodsList);
			}
			map.put(3, totalEnough);

			return map;
		}
		return null;
	}

	/**
	 * 查询外委需求材料明细
	 */
	@Override
	public Map<Integer, Object> getWwclDetail2(List<WaigouPlan> wwPlanList,
			Integer id) {
		// TODO Auto-generated method stub
		WaigouOrder order = null;
		Users user = Util.getLoginUser();
		boolean totalEnough = true;
		Map<String, Integer> flagMap = new HashMap<String, Integer>();
		Integer maxFlag = 0;
		String gysSql = " and 1=1";
		if (user == null) {
			throw new RuntimeException("请先登录");
		} else if (user.getDept().equals("供应商")) {
			totalEnough = false;// 供应商无出库权限
			gysSql = " and userId=" + user.getId();
		}
		order = (WaigouOrder) totalDao.getObjectByCondition(
				"from WaigouOrder where id=?" + gysSql, id);
		if (order != null) {
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='关闭余料库' and valueName='关闭余料库'";
			String valueCode = (String) totalDao.getObjectByCondition(hql1);
			Boolean useyl = true;
			if (valueCode != null && valueCode.equals("是")) {
				useyl = false;
			}
			Map<Integer, Object> map = new HashMap<Integer, Object>();
			map.put(1, order);
			// 获取已出库的明细
			List<Sell> sellList = totalDao.query(
					"from Sell where wgOrderId=? order by sellMarkId", order
							.getId());
			map.put(4, sellList);
			Set<WaigouPlan> wwpSet = order.getWwpSet();
			if (wwpSet != null && wwpSet.size() > 0) {
				// List<Sell> sellList = new ArrayList<Sell>();
				List<Goods> returngoodsList = new ArrayList<Goods>();
				Map<String, Procard> wgjmap = new HashMap();
				if (order.getWwSource().equals("BOM外委")) {
					for (WaigouPlan plan : wwpSet) {
						Float selectcount = 0f;
						if (plan.getStatus().equals("待核对")
								|| plan.getStatus().equals("待通知")
								|| plan.getStatus().equals("待确认")
								|| plan.getStatus().equals("协商确认")
								|| plan.getStatus().equals("删除")
								|| plan.getStatus().equals("取消")
								|| plan.getStatus().equals("待审核")) {
							continue;
						}
						for (WaigouPlan select : wwPlanList) {
							if (select.getId() != null
									&& select.getId().equals(plan.getId())) {
								selectcount = select.getNumber()
										+ select.getBlNum();
								break;
							}
						}
						if (selectcount == 0) {
							continue;
						}
						List<Procard> procardList = totalDao
								.query(
										"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) order by markId",
										plan.getId());
						if (procardList != null && procardList.size() > 0) {
							for (Procard procard : procardList) {
								// 获取第一道工序号,非第一道工序外委需要领在制品
								Integer minProNo = (Integer) totalDao
										.getObjectByCondition(
												"select min(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')",
												procard.getId());
								if (minProNo == null) {
									throw new RuntimeException("外委数据异常,没有找到零件"
											+ procard.getMarkId() + "的工序");
								}
								// 判断外委工序是否包含第一道
								boolean b = false;
								String[] processNos = plan.getProcessNOs()
										.split(";");
								if (processNos != null && processNos.length > 0) {
									for (String s : processNos) {
										if (s.equals(minProNo + "")) {
											b = true;
										}
									}
								}
								String addSql = "";
								Integer lastProcessNo = null;
								String lastProcessName = null;
								Float needCount = (Float) totalDao
										.getObjectByCondition(
												"select sum(procardCount) from ProcardWGCenter where wgOrderId=? and procardId=? ",
												plan.getId(), procard.getId());
								Float blCount = (Float) totalDao
										.getObjectByCondition(
												"select sum(blNum) from "
														+ "ProcardCsBl where wgplanId=? and procardId=?",
												plan.getId(), procard.getId());
								if (blCount != null) {
									needCount = needCount + blCount;
								}
								List<Goods> goodsTempList = new ArrayList<Goods>();
								if (!b) {
									lastProcessNo = (Integer) totalDao
											.getObjectByCondition(
													"select processNO from ProcessInfor where procard.id=? and processNO<'"
															+ processNos[0]
															+ "' and (dataStatus is null or dataStatus!='删除') order by processNO desc",
													procard.getId());
									if (lastProcessNo != null) {
										lastProcessName = (String) totalDao
												.getObjectByCondition(
														"select processName from ProcessInfor where procard.id=? and processNO ='"
																+ lastProcessNo
																+ "' and (dataStatus is null or dataStatus!='删除') order by processNO desc",
														procard.getId());
									}
									addSql = " and processNo=" + lastProcessNo;
									for (Sell sell : sellList) {
										if (sell.getSellMarkId().equals(
												procard.getMarkId())
												&& sell.getSellLot() != null
												&& sell.getSellLot().equals(
														procard.getSelfCard())
												&& sell.getProcessNo() != null
												&& sell.getProcessNo().equals(
														lastProcessNo)) {
											needCount -= sell.getSellCount();
										}
									}
									if (needCount != null && needCount > 0) {
										if (needCount > selectcount) {
											needCount = selectcount;
										}
										List<Goods> goodsList = totalDao
												.query(
														"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0 and processNo=? "
																+ addSql,
														procard.getMarkId(),
														procard.getSelfCard(),
														lastProcessNo);
										if (goodsList != null
												&& goodsList.size() > 0) {
											for (Goods g : goodsList) {
												if (flagMap.get(procard
														.getMarkId()) != null) {
													g
															.setFlag(flagMap
																	.get(procard
																			.getMarkId()));
												} else {
													flagMap.put(procard
															.getMarkId(),
															maxFlag);
													g.setFlag(maxFlag);
													maxFlag++;
												}
												g.setOrder_Id(procard.getId());
												g.setIsEnough(true);
												if (g.getGoodsCurQuantity() >= needCount) {
													g
															.setGoodsCurQuantity(needCount);// 记录请领
													g
															.setGoodsBeginQuantity(needCount);// 记录实发（页面传回来的实发大于请领时为余料）
													needCount = 0f;
													goodsTempList.add(g);
													// returngoodsList.addAll(goodsTempList);
													break;
												} else {
													g
															.setGoodsBeginQuantity(g
																	.getGoodsCurQuantity());
													needCount -= g
															.getGoodsCurQuantity();
													goodsTempList.add(g);
													// returngoodsList.add(g);
												}
											}
											if (needCount > 0) {
												// throw new
												// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
												totalEnough = false;
												Goods g = goodsList.get(0);
												g.setProcessNo(lastProcessNo);
												g.setTqlCount(needCount);
												g.setGoodsCurQuantity(0f);
												g.setIsEnough(false);
//												for (Goods gtmp : goodsTempList) {
//													gtmp.setIsEnough(false);
//												}
												goodsTempList.add(g);
											}
											returngoodsList
													.addAll(goodsTempList);
										} else {
											Goods g = new Goods();
											g.setProcessNo(lastProcessNo);
											g.setProcessName(lastProcessName);
											g.setGoodsLotId(procard
													.getSelfCard());
											g.setBanBenNumber(procard
													.getBanBenNumber());
											g.setOrder_Id(procard.getId());
											g.setGoodsMarkId(procard
													.getMarkId());
											g.setGoodsFullName(procard
													.getProName());
											g.setGoodsLotId(procard
													.getSelfCard());
											g.setBanBenNumber(procard
													.getBanBenNumber());
											g.setGoodsFormat(procard
													.getSpecification());
											g.setGoodsUnit(procard.getUnit());
											g.setIsEnough(false);
											g.setTqlCount(needCount);
											returngoodsList.add(g);
											totalEnough = false;
										}
									}
								} else {
									List<Procard> sonProcardList = totalDao
											.query(
													"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除') and id in(select procardId from ProcessInforWWProcard where (status is null or status not in ('删除','取消')) and "
															+ "wwxlId in(select wwxlId from ProcardWGCenter where procardId=? and wgOrderId=?))",
													procard.getId(), procard
															.getId(), plan
															.getId());
									if (sonProcardList != null
											&& sonProcardList.size() > 0) {
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")
													&& (son.getNeedProcess() == null || !son
															.getNeedProcess()
															.equals("yes"))) {
												ProcessInforWWProcard wwprocard = (ProcessInforWWProcard) totalDao
														.getObjectByCondition(
																"from ProcessInforWWProcard where wwxlId in (select wwxlId from ProcardWGCenter where procardId=? and wgOrderId=?) and (status is null or status not in ('删除','取消')) and procardId=?",
																procard.getId(),
																plan.getId(),
																son.getId());
												String key = son.getMarkId()
														+ son.getKgliao();
												Float selectcount2 = selectcount;
												if (son.getBanBenNumber() != null) {
													key += son
															.getBanBenNumber();
												}
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													if (p != null) {
														Float yscount = p
																.getFilnalCount()
																* son
																		.getQuanzi1()
																/ son
																		.getQuanzi2();// 已算数量
														selectcount2 -= yscount;
													}
												}
												if (selectcount2 <= 0) {
													continue;
												}
												needCount2 = selectcount2
														* son.getQuanzi2()
														/ son.getQuanzi1();
												if (wwprocard.getHascount() == null) {
													wwprocard
															.setHascount(wwprocard
																	.getApplyCount());
												}
												if (needCount2 > wwprocard
														.getHascount()) {
													needCount2 = wwprocard
															.getHascount();
												}
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													p.setFilnalCount(p
															.getFilnalCount()
															+ needCount2);
												} else {
													son
															.setFilnalCount(needCount2);
													wgjmap.put(key, son);
												}

											} else {
												ProcessInfor maxsonprocess = (ProcessInfor) totalDao
														.getObjectByCondition(
																"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																		+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
																son.getId(),
																son.getId());
												if (son.getProcardStyle()
														.equals("外购")) {
													needCount2 = needCount
															* son.getQuanzi2()
															/ son.getQuanzi1();
												} else {
													needCount2 = needCount
															* son
																	.getCorrCount();
												}
												if (maxsonprocess != null) {
													for (Sell sell : sellList) {
														if (sell
																.getSellMarkId()
																.equals(
																		son
																				.getMarkId())
																&& sell
																		.getSellLot() != null
																&& sell
																		.getSellLot()
																		.equals(
																				son
																						.getSelfCard())
																&& sell
																		.getProcessNo() != null
																&& sell
																		.getProcessNo()
																		.equals(
																				maxsonprocess
																						.getProcessNO())) {
															needCount2 -= sell
																	.getSellCount();
														}
													}
													if (needCount2 > 0) {
														Float needCount3 = 0f;
														if (son
																.getProcardStyle()
																.equals("外购")) {
															needCount3 = selectcount
																	* son
																			.getQuanzi2()
																	/ son
																			.getQuanzi1();
														} else {
															needCount3 = selectcount
																	* son
																			.getCorrCount();
														}
														if (needCount2 > needCount3) {
															needCount2 = needCount3;
														}
														List<Goods> goodsList = totalDao
																.query(
																		"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																				+ " and processNo=?",
																		son
																				.getMarkId(),
																		son
																				.getSelfCard(),
																		maxsonprocess
																				.getProcessNO());
														if (goodsList != null
																&& goodsList
																		.size() > 0) {
															for (Goods g : goodsList) {
																if (flagMap
																		.get(son
																				.getMarkId()) != null) {
																	g
																			.setFlag(flagMap
																					.get(son
																							.getMarkId()));
																} else {
																	flagMap
																			.put(
																					son
																							.getMarkId(),
																					maxFlag);
																	g
																			.setFlag(maxFlag);
																	maxFlag++;
																}
																g
																		.setOrder_Id(son
																				.getId());
																g
																		.setIsEnough(true);
																if (g
																		.getGoodsCurQuantity() >= needCount2) {
																	g
																			.setGoodsCurQuantity(needCount2);// 记录请领
																	g
																			.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																	needCount2 = 0f;
																	goodsTempList2
																			.add(g);
																	break;
																} else {
																	g
																			.setGoodsBeginQuantity(g
																					.getGoodsCurQuantity());
																	needCount2 -= g
																			.getGoodsCurQuantity();
																	goodsTempList2
																			.add(g);
																	// returngoodsList.add(g);
																}
															}
															if (needCount2 > 0) {
																// throw new
																// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
																totalEnough = false;
																Goods g = goodsList
																		.get(0);
																g
																		.setProcessNo(maxsonprocess
																				.getProcessNO());
																g
																		.setTqlCount(needCount2);
																g
																		.setGoodsCurQuantity(0f);
																g
																		.setIsEnough(false);
//																for (Goods gtmp : goodsTempList) {
//																	gtmp
//																			.setIsEnough(false);
//																}
																goodsTempList2
																		.add(g);
															}
															returngoodsList
																	.addAll(goodsTempList2);
														} else {
															Goods g = new Goods();
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setProcessName(maxsonprocess
																			.getProcessName());
															g
																	.setBanBenNumber(son
																			.getBanBenNumber());
															g.setOrder_Id(son
																	.getId());
															g
																	.setGoodsMarkId(son
																			.getMarkId());
															g
																	.setGoodsFullName(son
																			.getProName());
															g
																	.setGoodsFormat(son
																			.getSpecification());
															g.setGoodsUnit(son
																	.getUnit());
															g
																	.setGoodsLotId(son
																			.getSelfCard());
															g
																	.setIsEnough(false);
															g
																	.setTqlCount(needCount2);
															returngoodsList
																	.add(g);
															totalEnough = false;
														}
													}
												}
											}
										}
									} else {
										// 领取下层委外零件
										sonProcardList = totalDao
												.query(
														"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and (needProcess is null or needProcess!='yes'))) order by markId",
														procard.getId());
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")) {
												needCount2 = needCount
														* son.getQuanzi2()
														/ son.getQuanzi1();
											} else {
												needCount2 = needCount
														* son.getCorrCount();
											}
											if (maxsonprocess != null) {
												for (Sell sell : sellList) {
													if (sell
															.getSellMarkId()
															.equals(
																	son
																			.getMarkId())
															&& sell
																	.getSellLot() != null
															&& sell
																	.getSellLot()
																	.equals(
																			son
																					.getSelfCard())
															&& sell
																	.getProcessNo() != null
															&& sell
																	.getProcessNo()
																	.equals(
																			maxsonprocess
																					.getProcessNO())) {
														needCount2 -= sell
																.getSellCount();
													}
												}
												if (needCount2 > 0) {
													Float needCount3 = 0f;
													if (son.getProcardStyle()
															.equals("外购")) {
														needCount3 = selectcount
																* son
																		.getQuanzi2()
																/ son
																		.getQuanzi1();
													} else {
														needCount3 = selectcount
																* son
																		.getCorrCount();
													}
													if (needCount2 > needCount3) {
														needCount2 = needCount3;
													}
													List<Goods> goodsList = totalDao
															.query(
																	"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																			+ " and processNo=?",
																	son
																			.getMarkId(),
																	son
																			.getSelfCard(),
																	maxsonprocess
																			.getProcessNO());
													if (goodsList != null
															&& goodsList.size() > 0) {
														for (Goods g : goodsList) {
															if (flagMap
																	.get(son
																			.getMarkId()) != null) {
																g
																		.setFlag(flagMap
																				.get(son
																						.getMarkId()));
															} else {
																flagMap
																		.put(
																				son
																						.getMarkId(),
																				maxFlag);
																g
																		.setFlag(maxFlag);
																maxFlag++;
															}
															g.setOrder_Id(son
																	.getId());
															g.setIsEnough(true);
															if (g
																	.getGoodsCurQuantity() >= needCount2) {
																g
																		.setGoodsCurQuantity(needCount2);// 记录请领
																g
																		.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																needCount2 = 0f;
																goodsTempList2
																		.add(g);
																break;
															} else {
																g
																		.setGoodsBeginQuantity(g
																				.getGoodsCurQuantity());
																needCount2 -= g
																		.getGoodsCurQuantity();
																goodsTempList2
																		.add(g);
																// returngoodsList.add(g);
															}
														}
														if (needCount2 > 0) {
															// throw new
															// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
															totalEnough = false;
															Goods g = goodsList
																	.get(0);
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setTqlCount(needCount2);
															g
																	.setGoodsCurQuantity(0f);
															g
																	.setIsEnough(false);
//															for (Goods gtmp : goodsTempList) {
//																gtmp
//																		.setIsEnough(false);
//															}
															goodsTempList2
																	.add(g);
														}
														returngoodsList
																.addAll(goodsTempList2);
													} else {
														Goods g = new Goods();
														g
																.setProcessNo(maxsonprocess
																		.getProcessNO());
														g
																.setProcessName(maxsonprocess
																		.getProcessName());
														g
																.setBanBenNumber(son
																		.getBanBenNumber());
														g.setOrder_Id(son
																.getId());
														g.setGoodsMarkId(son
																.getMarkId());
														g.setGoodsFullName(son
																.getProName());
														g
																.setGoodsFormat(son
																		.getSpecification());
														g.setGoodsUnit(son
																.getUnit());
														g.setGoodsLotId(son
																.getSelfCard());
														g.setIsEnough(false);
														g
																.setTqlCount(needCount2);
														returngoodsList.add(g);
														totalEnough = false;
													}
												}
											}
										}
									}

								}

							}
						}

					}
				} else {
					for (WaigouPlan plan : wwpSet) {
						Float selectcount = 0f;
						if (plan.getStatus().equals("待核对")
								|| plan.getStatus().equals("待通知")
								|| plan.getStatus().equals("待确认")
								|| plan.getStatus().equals("协商确认")
								|| plan.getStatus().equals("删除")
								|| plan.getStatus().equals("取消")
								|| plan.getStatus().equals("待审核")) {
							continue;
						}

						for (WaigouPlan select : wwPlanList) {
							if (select.getId() != null
									&& select.getId().equals(plan.getId())) {
								selectcount = select.getNumber()
										+ select.getBlNum();
								break;
							}
						}
						if (selectcount == 0) {
							continue;
						}
						if (plan.getMarkId().equals("YT6.100.2199")) {
							System.out.println("abc");
						}
						if (plan.getWwType().equals("工序外委")) {
							List<ProcessInforWWApplyDetail> applyDetailList = totalDao
									.query(
											"from ProcessInforWWApplyDetail where id in(select wwxlId from ProcardWGCenter where wgOrderId=? and (dataStatus is null or dataStatus not in('取消','删除')))",
											plan.getId());
							if (applyDetailList != null
									&& applyDetailList.size() > 0) {
								for (ProcessInforWWApplyDetail applyDetail : applyDetailList) {
									Procard procard = byProcardId(applyDetail);
									if (procard != null) {
										// 获取s第一道工序号,非第一道工序外委需要领在制品
										Integer minProNo = (Integer) totalDao
												.getObjectByCondition(
														"select min(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除')",
														procard.getId());
										if (minProNo == null) {
											throw new RuntimeException(
													"外委数据异常,没有找到零件"
															+ procard
																	.getMarkId()
															+ "的工序");
										}
										boolean b = false;
										String[] processNos = applyDetail
												.getProcessNOs().split(";");
										if (processNos != null
												&& processNos.length > 0) {
											for (String s : processNos) {
												if (s.equals(minProNo + "")) {
													b = true;
												}
											}
										}
										if (!b) {
											Integer sdProcessno = (Integer) totalDao
													.getObjectByCondition(
															"select max(processNO) from ProcessInfor where procard.id=? and processNO<?  and (dataStatus is null or dataStatus !='删除')",
															procard.getId(),
															Integer
																	.parseInt(processNos[0]));
											List<Goods> goodsTempList = new ArrayList<Goods>();
											// 需要在制品出库
											Float needCount = applyDetail
													.getApplyCount();
											Float blCount = (Float) totalDao
													.getObjectByCondition(
															"select sum(blNum) from "
																	+ "ProcardCsBl where wgplanId=? and procardId=?",
															plan.getId(),
															procard.getId());
											if (blCount != null) {
												needCount = needCount + blCount;
											}
											for (Sell sell : sellList) {
												if (sell
														.getSellMarkId()
														.equals(
																procard
																		.getMarkId())
														&& sell.getSellLot() != null
														&& sell
																.getSellLot()
																.equals(
																		procard
																				.getSelfCard())
														&& sell.getProcessNo() != null) {
													needCount -= sell
															.getSellCount();
												}
											}
											if (needCount != null
													&& needCount > 0) {
												if (needCount > selectcount) {
													needCount = selectcount;
												}
												List<Goods> goodsList = totalDao
														.query(
																"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0 and processNo=?",
																procard
																		.getMarkId(),
																procard
																		.getSelfCard(),
																sdProcessno);
												if (goodsList != null
														&& goodsList.size() > 0) {
													for (Goods g : goodsList) {
														if (flagMap.get(procard
																.getMarkId()) != null) {
															g
																	.setFlag(flagMap
																			.get(procard
																					.getMarkId()));
														} else {
															flagMap
																	.put(
																			procard
																					.getMarkId(),
																			maxFlag);
															g.setFlag(maxFlag);
															maxFlag++;
														}
														g.setOrder_Id(procard
																.getId());
														g.setIsEnough(true);
														if (g
																.getGoodsCurQuantity() >= needCount) {
															g
																	.setGoodsBeginQuantity(needCount);
															g
																	.setGoodsCurQuantity(needCount);
															g.setTqlCount(0f);
															needCount = 0f;
															goodsTempList
																	.add(g);
															// returngoodsList.addAll(goodsTempList);
															break;
														} else {
															g.setTqlCount(0f);
															g
																	.setGoodsBeginQuantity(g
																			.getGoodsCurQuantity());
															needCount -= g
																	.getGoodsCurQuantity();
															// returngoodsList.add(g);
															goodsTempList
																	.add(g);
														}
													}
													if (needCount > 0) {
														totalEnough = false;
														// throw new
														// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
														Goods g = goodsList
																.get(0);
														g
																.setTqlCount(needCount);
														g
																.setGoodsCurQuantity(0f);
														g.setIsEnough(false);
//														for (Goods gTemp : goodsTempList) {
//															gTemp
//																	.setIsEnough(false);
//															;
//														}
														goodsTempList.add(g);
													}
													returngoodsList
															.addAll(goodsTempList);
												} else {
													Goods g = new Goods();
													if (flagMap.get(procard
															.getMarkId()) != null) {
														g
																.setFlag(flagMap
																		.get(procard
																				.getMarkId()));
													} else {
														flagMap.put(procard
																.getMarkId(),
																maxFlag);
														g.setFlag(maxFlag);
														maxFlag++;
													}
													g.setProcessNo(sdProcessno);
													g.setGoodsLotId(procard
															.getSelfCard());
													g.setBanBenNumber(procard
															.getBanBenNumber());
													g.setOrder_Id(procard
															.getId());
													g.setGoodsMarkId(procard
															.getMarkId());
													g.setGoodsFullName(procard
															.getProName());
													g
															.setGoodsFormat(procard
																	.getSpecification());
													g.setGoodsUnit(procard
															.getUnit());
													g.setIsEnough(false);
													g.setTqlCount(needCount);
													returngoodsList.add(g);
													totalEnough = false;
												}
											}
										}
									}
									// 合并要领的外购件
									// 出外购件
									List<ProcessInforWWProcard> processwwprocardList = totalDao
											.query(
													"from ProcessInforWWProcard where applyDtailId=? and (status is null or status not in ('删除','取消'))",
													applyDetail.getId());
									if (processwwprocardList != null
											&& processwwprocardList.size() > 0) {
										for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
											// 存在外购件，判断库存是否充足
											// 第三步计算外购件需领数量
											// 外购件需领数量
											// float count = applyDetail
											// .getApplyCount();
											if (processInforWWProcard
													.getHascount() == null) {
												processInforWWProcard
														.setHascount(processInforWWProcard
																.getApplyCount());
											}
											float count = processInforWWProcard
													.getHascount();
											if (count == 0) {
												// 已经领过了
												continue;
											}
											Float selectcount2 = selectcount;
											Procard son = byProcardId(processInforWWProcard);
											if (son.getProcardStyle().equals(
													"外购")) {
												String key = son.getMarkId()
														+ son.getKgliao();
												if (son.getBanBenNumber() != null) {
													key += son
															.getBanBenNumber();
												}
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													if (p != null) {
														Float yscount = p
																.getFilnalCount()
																* procard
																		.getFilnalCount()
																/ son
																		.getFilnalCount();// 已算数量
														// selectcount2
														// -=yscount;
													}
												}
												if (selectcount2 <= 0) {
													continue;
												}
												float scount = selectcount2
														* son.getFilnalCount()
														/ procard
																.getFilnalCount();

												if (count > scount) {
													count = scount;
												}
												// count = count *
												// son.getQuanzi2()
												// / son.getQuanzi1();
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													p.setFilnalCount(p
															.getFilnalCount()
															+ count);
												} else {
													son.setFilnalCount(count);
													wgjmap.put(key, son);
												}
											} else {

												List<Goods> goodsTempList2 = new ArrayList<Goods>();
												ProcessInfor maxsonprocess = (ProcessInfor) totalDao
														.getObjectByCondition(
																"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																		+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
																son.getId(),
																son.getId());
												Float needCount2 = null;
												if (son.getProcardStyle()
														.equals("外购")) {
													needCount2 = applyDetail
															.getApplyCount()
															* son.getQuanzi2()
															/ son.getQuanzi1();
												} else {
													needCount2 = applyDetail
															.getApplyCount()
															* son
																	.getCorrCount();
												}
												if (maxsonprocess != null) {
													for (Sell sell : sellList) {
														if (sell
																.getSellMarkId()
																.equals(
																		son
																				.getMarkId())
																&& sell
																		.getSellLot() != null
																&& sell
																		.getSellLot()
																		.equals(
																				son
																						.getSelfCard())
																&& sell
																		.getProcessNo() != null
																&& sell
																		.getProcessNo()
																		.equals(
																				maxsonprocess
																						.getProcessNO())) {
															needCount2 -= sell
																	.getSellCount();
														}
													}
													if (needCount2 > 0) {
														Float needCount3 = 0f;
														if (son
																.getProcardStyle()
																.equals("外购")) {
															needCount3 = selectcount
																	* son
																			.getQuanzi2()
																	/ son
																			.getQuanzi1();
														} else {
															needCount3 = selectcount
																	* son
																			.getCorrCount();
														}
														if (needCount2 > needCount3) {
															needCount2 = needCount3;
														}
														List<Goods> goodsList = totalDao
																.query(
																		"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																				+ " and processNo=?",
																		son
																				.getMarkId(),
																		son
																				.getSelfCard(),
																		maxsonprocess
																				.getProcessNO());
														if (goodsList != null
																&& goodsList
																		.size() > 0) {
															for (Goods g : goodsList) {
																if (flagMap
																		.get(son
																				.getMarkId()) != null) {
																	g
																			.setFlag(flagMap
																					.get(son
																							.getMarkId()));
																} else {
																	flagMap
																			.put(
																					son
																							.getMarkId(),
																					maxFlag);
																	g
																			.setFlag(maxFlag);
																	maxFlag++;
																}
																g
																		.setOrder_Id(son
																				.getId());
																g
																		.setIsEnough(true);
																if (g
																		.getGoodsCurQuantity() >= needCount2) {
																	g
																			.setGoodsCurQuantity(needCount2);// 记录请领
																	g
																			.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																	needCount2 = 0f;
																	goodsTempList2
																			.add(g);
																	break;
																} else {
																	g
																			.setGoodsBeginQuantity(g
																					.getGoodsCurQuantity());
																	needCount2 -= g
																			.getGoodsCurQuantity();
																	goodsTempList2
																			.add(g);
																	// returngoodsList.add(g);
																}
															}
															if (needCount2 > 0) {
																// throw new
																// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
																totalEnough = false;
																Goods g = goodsList
																		.get(0);
																g
																		.setProcessNo(maxsonprocess
																				.getProcessNO());
																g
																		.setTqlCount(needCount2);
																g
																		.setGoodsCurQuantity(0f);
																g
																		.setIsEnough(false);
//																for (Goods gtmp : goodsTempList2) {
//																	gtmp
//																			.setIsEnough(false);
//																}
																goodsTempList2
																		.add(g);
															}
															returngoodsList
																	.addAll(goodsTempList2);
														} else {
															Goods g = new Goods();
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setProcessName(maxsonprocess
																			.getProcessName());
															g.setOrder_Id(son
																	.getId());
															g
																	.setGoodsMarkId(son
																			.getMarkId());
															g
																	.setGoodsFullName(son
																			.getProName());
															g
																	.setGoodsLotId(son
																			.getSelfCard());
															g
																	.setBanBenNumber(son
																			.getBanBenNumber());
															g
																	.setGoodsFormat(son
																			.getSpecification());
															g.setGoodsUnit(son
																	.getUnit());
															g
																	.setIsEnough(false);
															g
																	.setTqlCount(needCount2);
															returngoodsList
																	.add(g);
															totalEnough = false;
														}
													}
												}

											}

										}
									}

								}
							}
						} else {
							List<ProcessInforWWApplyDetail> wwDetaiList = totalDao
									.query(
											"from ProcessInforWWApplyDetail where id in( select wwxlId from ProcardWGCenter where wgOrderId=?)",
											plan.getId());
							if (wwDetaiList != null && wwDetaiList.size() > 0) {
								for (ProcessInforWWApplyDetail applyDetail : wwDetaiList) {
									Procard procard = byProcardId(applyDetail);
									if (procard != null) {
										// 领取下层委外零件
										List<Procard> sonProcardList = totalDao
												.query(
														"from Procard where (wwblCount is null or wwblCount=0) and procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and needProcess='yes')) order by markId",
														procard.getId());
										for (Procard son : sonProcardList) {
											List<Goods> goodsTempList2 = new ArrayList<Goods>();
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											Float needCount2 = null;
											if (son.getProcardStyle().equals(
													"外购")) {
												needCount2 = applyDetail
														.getApplyCount()
														* son.getQuanzi2()
														/ son.getQuanzi1();
											} else {
												needCount2 = applyDetail
														.getApplyCount()
														* son.getCorrCount();
											}
											if (maxsonprocess != null) {
												for (Sell sell : sellList) {
													if (sell
															.getSellMarkId()
															.equals(
																	son
																			.getMarkId())
															&& sell
																	.getSellLot() != null
															&& sell
																	.getSellLot()
																	.equals(
																			son
																					.getSelfCard())
															&& sell
																	.getProcessNo() != null
															&& sell
																	.getProcessNo()
																	.equals(
																			maxsonprocess
																					.getProcessNO())) {
														needCount2 -= sell
																.getSellCount();
													}
												}
												if (needCount2 > 0) {
													Float needCount3 = 0f;
													if (son.getProcardStyle()
															.equals("外购")) {
														needCount3 = selectcount
																* son
																		.getQuanzi2()
																/ son
																		.getQuanzi1();
													} else {
														needCount3 = selectcount
																* son
																		.getCorrCount();
													}
													if (needCount2 > needCount3) {
														needCount2 = needCount3;
													}
													List<Goods> goodsList = totalDao
															.query(
																	"from Goods where goodsStyle='半成品转库' and goodsMarkId=? and goodsLotId=? and goodsCurQuantity>0"
																			+ " and processNo=?",
																	son
																			.getMarkId(),
																	son
																			.getSelfCard(),
																	maxsonprocess
																			.getProcessNO());
													if (goodsList != null
															&& goodsList.size() > 0) {
														for (Goods g : goodsList) {
															if (flagMap
																	.get(son
																			.getMarkId()) != null) {
																g
																		.setFlag(flagMap
																				.get(son
																						.getMarkId()));
															} else {
																flagMap
																		.put(
																				son
																						.getMarkId(),
																				maxFlag);
																g
																		.setFlag(maxFlag);
																maxFlag++;
															}
															g.setOrder_Id(son
																	.getId());
															g.setIsEnough(true);
															if (g
																	.getGoodsCurQuantity() >= needCount2) {
																g
																		.setGoodsCurQuantity(needCount2);// 记录请领
																g
																		.setGoodsBeginQuantity(needCount2);// 记录实发（页面传回来的实发大于请领时为余料）
																needCount2 = 0f;
																goodsTempList2
																		.add(g);
																break;
															} else {
																g
																		.setGoodsBeginQuantity(g
																				.getGoodsCurQuantity());
																needCount2 -= g
																		.getGoodsCurQuantity();
																goodsTempList2
																		.add(g);
																// returngoodsList.add(g);
															}
														}
														if (needCount2 > 0) {
															// throw new
															// RuntimeException("零件:"+procard.getMarkId()+"的在制品入库数量不足，差"+needCount+procard.getUnit());
															totalEnough = false;
															Goods g = goodsList
																	.get(0);
															g
																	.setProcessNo(maxsonprocess
																			.getProcessNO());
															g
																	.setTqlCount(needCount2);
															g
																	.setGoodsCurQuantity(0f);
															g
																	.setIsEnough(false);
//															for (Goods gtmp : goodsTempList2) {
//																gtmp
//																		.setIsEnough(false);
//															}
															goodsTempList2
																	.add(g);
														}
														returngoodsList
																.addAll(goodsTempList2);
													} else {
														Goods g = new Goods();
														g
																.setProcessNo(maxsonprocess
																		.getProcessNO());
														g
																.setProcessName(maxsonprocess
																		.getProcessName());
														g.setOrder_Id(son
																.getId());
														g.setGoodsMarkId(son
																.getMarkId());
														g.setGoodsFullName(son
																.getProName());
														g.setGoodsLotId(son
																.getSelfCard());
														g
																.setBanBenNumber(son
																		.getBanBenNumber());
														g
																.setGoodsFormat(son
																		.getSpecification());
														g.setGoodsUnit(son
																.getUnit());
														g.setIsEnough(false);
														g
																.setTqlCount(needCount2);
														returngoodsList.add(g);
														totalEnough = false;
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
				if (wgjmap.size() > 0) {
					for (String wgj : wgjmap.keySet()) {
						Procard p = wgjmap.get(wgj);
						List<Goods> goodsTempList = new ArrayList<Goods>();
						if (p.getMarkId().equals("1.01.18007")) {
							System.out.println("1.01.18007");
						}
						// 外购件需领数量
						float count = p.getFilnalCount();// 第一遍循环累计在这个属性中
						float tqlcount = 0f;
						String banbenSql = null;
						if (p.getBanBenNumber() == null
								|| p.getBanBenNumber().length() == 0) {
							banbenSql = " and (banBenNumber is null or banBenNumber ='')";
						} else {
							banbenSql = " and banBenNumber ='"
									+ p.getBanBenNumber() + "'";
						}
						Goods googsTemp = (Goods) totalDao
								.getObjectByCondition(
										"from Goods where goodsCurQuantity>0 and  goodsZhishu>0 and goodsMarkId=? "
												+ banbenSql, p.getMarkId());
						// 单零件重量（一般为原材料使用）
						Float bizhong = null;
						if (useyl) {
							bizhong = (Float) totalDao
									.getObjectByCondition(
											"select bili from YuanclAndWaigj where markId =? ",
											p.getMarkId());
							if (bizhong == null) {
								if (googsTemp != null) {
									bizhong = googsTemp.getGoodsCurQuantity()
											/ googsTemp.getGoodsZhishu();
								}
							}
						}

						float countTem = count;
						if (count == 0) {
							continue;
						}
						// 余料查询
						if (bizhong != null && bizhong > 0) {// 有比重才会有余料
							Float single = null;// 单间需要重量
							if (googsTemp != null
									&& p.getUnit().equals(
											googsTemp.getGoodsUnit())) {// 单位一致
								single = p.getQuanzi2() / p.getQuanzi1();
							} else {
								single = bizhong * p.getQuanzi2()
										/ p.getQuanzi1();
							}
							List<Goods> yuliaoList = totalDao
									.query(
											" from Goods where goodsClass='余料库' and goodsCurQuantity>"
													+ single
													+ " and goodsMarkId=? and llGysId=? and kgliao=? and (yllock is null or yllock='' or yllock='no' or (yllock = 'yes' and ylMarkId=?)) and (fcStatus is null or fcStatus='可用')",
											p.getMarkId(), order.getGysId(), p
													.getKgliao(), p.getMarkId());
							if (yuliaoList != null && yuliaoList.size() > 0) {
								for (Goods goods : yuliaoList) {
									goods.setOrder_Id(p.getId());
									if (flagMap.get(p.getMarkId()) != null) {
										goods.setFlag(flagMap
												.get(p.getMarkId()));
									} else {
										flagMap.put(p.getMarkId(), maxFlag);
										goods.setFlag(maxFlag);
										maxFlag++;
									}
									goods.setShowType("余料");
									goods.setHqlCount(0f);
									goods.setTqlCount(0f);
									goods.setIsEnough(true);
									goods.setIsChangeSf(false);
									if (p.getUnit().equals(
											googsTemp.getGoodsUnit())) {
										if (googsTemp.getGoodsStoreZHUnit() != null
												&& googsTemp
														.getGoodsStoreZHUnit()
														.length() > 0) {
											goods.setQlUnit(googsTemp
													.getGoodsStoreZHUnit());
										} else {
											goods.setQlUnit("张");
										}
									} else {
										goods.setQlUnit(p.getUnit());
									}
									goods.setGoodsZhishu(1f);// 余料以单张保存
									// goods.setGoodsCurQuantity();//余料重量即领取重量
									Float singleCount = (float) Math
											.floor(goods.getGoodsCurQuantity()
													/ single);
									if (googsTemp != null
											&& p.getUnit().equals(
													googsTemp.getGoodsUnit())) {// 单位一致
										if ((singleCount * single) >= count) {// 余料足够
											goods.setGoodsBeginQuantity(count);// 余料需要重量
											goodsTempList.add(goods);
											returngoodsList
													.addAll(goodsTempList);
											count = 0;
											break;
										} else {
											goods
													.setGoodsBeginQuantity(singleCount
															* single);// 余料需要重量
											// returngoodsList.add(goods);
											count = count - singleCount
													* single;
											goodsTempList.add(goods);
										}
									} else {
										if (singleCount * single >= count
												* bizhong) {// 余料足够
											goods.setGoodsBeginQuantity(count
													* bizhong);// 余料需要重量
											count = 0;
											goodsTempList.add(goods);
											// returngoodsList.addAll(goodsTempList);
											break;
										} else {
											goods
													.setGoodsBeginQuantity(singleCount
															* single);// 余料需要重量
											// returngoodsList.add(goods);
											count = count - singleCount;
											goodsTempList.add(goods);
										}
									}
								}
								// returngoodsList.addAll(goodsTempList);
							}

						}
						if (count == 0) {
							continue;
						}
						// 请领数量 bizhong
						float allcount = count;//总数量全标记
						float lsCount = count;// 张数
						float surlsCount = count;// 实际所需
						String goodsClass = "外购件库','中间库";
						String goodsClassSql = null;
						// if ((bizhong == null || bizhong == 0)
						// && p.getProductStyle() != null
						// && p.getProductStyle().equals("试制")) {
						// goodsClass = "试制库";// 试制的外购件去试制库取
						// goodsClassSql = " and goodsClass ='"
						// + goodsClass + "'";
						// } else {
						String kgsql = " and 1=1";
						if (p.getKgliao() != null && p.getKgliao().length() > 0) {
							kgsql += " and kgliao ='" + p.getKgliao() + "'";
						}
						goodsClassSql = " and ((goodsClass in ('" + goodsClass
								+ "') " + kgsql + " ) or goodsClass = '备货库')";
						// }
						String hqlGoods = " from Goods where goodsMarkId='"
								+ p.getMarkId()
								+ "'"
								+ goodsClassSql
								+ " and  goodsCurQuantity>0  and (fcStatus is null or fcStatus='可用') "
								+ banbenSql + "order by goodsLotId asc";
						List<Goods> listG = totalDao.find(hqlGoods);
						// Double d2 = Math.ceil(count);
						// count =
						// Float.parseFloat(d2.toString());
						if (listG.size() > 0) {
							int n = 0;
							for (Goods goo : listG) {
								n++;
								goo.setOrder_Id(p.getId());
								if (flagMap.get(p.getMarkId()) != null) {
									goo.setFlag(flagMap.get(p.getMarkId()));
								} else {
									flagMap.put(p.getMarkId(), maxFlag);
									goo.setFlag(maxFlag);
									maxFlag++;
								}
								goo.setShowType("外购件");
								goo.setHqlCount(0f);
								goo.setTqlCount(0f);
								goo.setGoodsBeginQuantity(0f);
								goo.setIsEnough(true);
								goo.setIsChangeSf(false);
								if (bizhong != null && bizhong > 0) {// 有比重需要换算单位
									if (p.getUnit().equals(goo.getGoodsUnit())
											&& null != goo.getGoodsZhishu()
											&& goo.getGoodsZhishu() > 0) {// 单位一致有支数,用lsCount计数
										if (n == 1) {
											lsCount = (float) Math.ceil(lsCount
													/ bizhong);
										}
										if (bizhong != null && bizhong > 0) {
											if (goo.getGoodsZhishu() > lsCount) {
												goo.setGoodsZhishu(lsCount);
												// 计算重量
												if (lsCount * bizhong <= goo
														.getGoodsCurQuantity()) {
													goo
															.setGoodsBeginQuantity(lsCount
																	* bizhong);
												}
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												lsCount = 0;
												count = 0;
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
												// returngoodsList.addAll(goodsTempList);
												break;
											} else if (goo.getGoodsZhishu() == lsCount) {
												goo.setGoodsZhishu(lsCount);
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												// 支数刚好情况重量，不重新计算重量
												// goo.setGoodsCurQuantity(lsCount
												// * bizhong);
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												lsCount = 0;
												count = 0;
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
												// returngoodsList.addAll(goodsTempList);
												break;
											} else {
												lsCount = lsCount
														- goo.getGoodsZhishu();
												goo.setGoodsBeginQuantity(goo
														.getGoodsZhishu()
														* bizhong);
												if (goo.getGoodsZhishu()
														* bizhong <= goo
														.getGoodsCurQuantity()) {
													goo.setGoodsCurQuantity(goo
															.getGoodsZhishu()
															* bizhong);
												}
												// 计算实际需要的重量
												if (goo.getGoodsCurQuantity() >= surlsCount) {
													goo
															.setGoodsCurQuantity(surlsCount);
													surlsCount = 0f;
												} else {
													goo
															.setGoodsCurQuantity(goo
																	.getGoodsCurQuantity());
													surlsCount -= goo
															.getGoodsCurQuantity();
												}
												if (goo.getGoodsStoreZHUnit() == null
														|| goo
																.getGoodsStoreZHUnit()
																.length() == 0) {
													goo.setQlUnit("张");
												} else {
													goo
															.setQlUnit(goo
																	.getGoodsStoreZHUnit());
												}
												goo.setIsChangeSf(true);
												// returngoodsList.add(goo);
												goodsTempList.add(goo);
											}
										}
									} else if (p.getUnit().equals(
											goo.getGoodsUnit())
											&& (null == goo.getGoodsZhishu() || goo
													.getGoodsZhishu() == 0)) {// 单位一致无支数
										// 此情况不需要比重,数据有误
									} else if (null != goo.getGoodsZhishu()
											&& goo.getGoodsZhishu() > 0) {// 单位不一致,
										// 有支数用count计数
										if (bizhong == null || bizhong == 0) {// 用当前库存重新计算比重
											bizhong = goo.getGoodsCurQuantity()
													/ goo.getGoodsZhishu();
										}
										if (n == 1) {
											surlsCount = count * bizhong;
										}
										if (goo.getGoodsZhishu() >= count) {
											goo.setGoodsZhishu(count);
											// 计算重量
											goo.setGoodsCurQuantity(count
													* bizhong);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											// returngoodsList.add(goo);
											goodsTempList.add(goo);
											// returngoodsList.addAll(goodsTempList);
											count = 0;
											break;
										} else if (goo.getGoodsZhishu() == count) {
											goo.setGoodsZhishu(count);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											// 支数刚好情况重量，不重新计算重量
											// goo.setGoodsCurQuantity(lsCount
											// * bizhong);
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											// returngoodsList.add(goo);
											goodsTempList.add(goo);
											// returngoodsList.addAll(goodsTempList);
											count = 0;
											break;
										} else {
											goo.setGoodsCurQuantity(goo
													.getGoodsZhishu()
													* bizhong);
											// 计算实际需要的重量
											if (goo.getGoodsCurQuantity() >= surlsCount) {
												goo
														.setGoodsCurQuantity(surlsCount);
												surlsCount = 0f;
											} else {
												goo.setGoodsCurQuantity(goo
														.getGoodsCurQuantity());
												surlsCount -= goo
														.getGoodsCurQuantity();
											}
											goo.setQlUnit(goo
													.getGoodsStoreZHUnit());
											returngoodsList.add(goo);
											count = count
													- goo.getGoodsZhishu();
											goodsTempList.add(goo);
										}

									} else {// 单位不一致无支数,无法计算

									}
								} else {// 无比重
									if (goo.getGoodsCurQuantity() >= count) {
										goo.setGoodsCurQuantity(count);
										goo.setGoodsBeginQuantity(count);
										// goo.setGoodsZhishu(count);
										count = 0;
										goo.setQlUnit(p.getUnit());
										// returngoodsList.add(goo);
										goodsTempList.add(goo);
										// returngoodsList.addAll(goodsTempList);
										break;
									} else {
										goo.setCkCount(goo
												.getGoodsCurQuantity());
										count -= goo.getGoodsCurQuantity();
										goo.setGoodsBeginQuantity(goo
												.getGoodsCurQuantity());
										// goo.setGoodsZhishu(goo
										// .getGoodsCurQuantity());
										goo.setQlUnit(p.getUnit());
										// returngoodsList.add(goo);
										goodsTempList.add(goo);
									}
								}

							}
							if (count > 0) {// 库存不够
								Goods gd = new Goods();
								gd.setGoodsLotId(p.getSelfCard());
								gd.setBanBenNumber(p.getBanBenNumber());
								gd.setOrder_Id(p.getId());
								gd.setGoodsMarkId(p.getMarkId());
								gd.setGoodsUnit(p.getUnit());
								gd.setGoodsCurQuantity(0f);
								gd.setHqlCount(0f);
								gd.setTqlCount(count);
								gd.setIsEnough(false);
								gd.setKgliao(p.getKgliao());
								// returngoodsList.add(gd);
//								if(count>0.5||count/allcount>0.01){
//									for (Goods gsTemp : goodsTempList) {
//										gsTemp.setIsEnough(false);
//									}
//								}
								goodsTempList.add(gd);
								totalEnough = false;
							}
							returngoodsList.addAll(goodsTempList);
						} else {
							String hqlGoods2 = " from Goods where goodsMarkId='"
									+ p.getMarkId()
									+ "'"
									+ goodsClassSql
									+ " and goodsCurQuantity=0 and (fcStatus is null or fcStatus='可用') order by goodsLotId asc";
							List<Goods> listG2 = totalDao.find(hqlGoods2);
							if (listG2.size() > 0) {
								Goods goo = listG2.get(0);
								goo.setGoodsLotId(p.getSelfCard());
								goo.setBanBenNumber(p.getBanBenNumber());
								goo.setOrder_Id(p.getId());
								goo.setHqlCount(0f);
								goo.setTqlCount(count);
								goo.setIsEnough(false);
								goo.setQlUnit(p.getUnit());
								returngoodsList.add(goo);
								totalEnough = false;
							} else {
								totalEnough = false;
								Goods goo = new Goods();
								goo.setGoodsLotId(p.getSelfCard());
								goo.setBanBenNumber(p.getBanBenNumber());
								goo.setOrder_Id(p.getId());
								goo.setHqlCount(0f);
								goo.setTqlCount(count);
								goo.setIsEnough(false);
								goo.setGoodsFullName(p.getProName());
								goo.setGoodsMarkId(p.getMarkId());
								goo.setGoodsId(0);
								goo.setQlUnit(p.getUnit());
								goo.setGoodsCurQuantity(0f);
								goo.setGoodsUnit(p.getUnit());
								goo.setKgliao(p.getKgliao());
								returngoodsList.add(goo);
							}
						}
					}
				}

				map.put(2, returngoodsList);
			}
			map.put(3, totalEnough);

			return map;
		}
		return null;
	}

	@Override
	public Goods findGoodsById(Integer id) {
		if (id != null && id > 0) {
			return (Goods) totalDao.get(Goods.class, id);
		}
		return null;
	}

	@Override
	public String queRenSendTow(Integer id) {
		Goods goods = findGoodsById(id);
		String hql = " from WareBangGoogs where fk_good_id = ? and status = '入库'";
		List<WareBangGoogs> bangList = (List<WareBangGoogs>) totalDao.query(
				hql, goods.getGoodsId());
		if (bangList != null && bangList.size() > 1) {
			return "error";
		} else if (bangList.size() == 1) {
			WareBangGoogs bang = bangList.get(0);
			WarehouseNumber wn = (WarehouseNumber) totalDao
					.getObjectByCondition("from WarehouseNumber where id = ?",
							bang.getFk_ware_id());
			if (wn == null)
				return "库位不存在";
			// 闪烁
			// Util.openColorXin(wn.getFourlightIp(), duankou, true, true, wn
			// .getNumid(), wn.getMarkTypt() == null ? 0 : wn
			// .getMarkTypt().getMarkColor());
			UtilTong.openColorLight(wn.getIp(), duankou, true, false, wn
					.getOneNumber(), wn.getNumid(),
					wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
							.getMarkColor());
			String message = UtilTong.sendTowMa_1(wn.getIp(),
					wn.getOneNumber(), duankou, bang.getTowCode());
			if ("true".equals(message)) {
				return message;
			}
			return "连接异常，确认码发送失败！";
		}
		return "没有找到库存!";
	}

	@Override
	public WareBangGoogs findWareBangBygoodsId(Integer id) {
		if (id != null && id > 0) {
			String hql = " from WareBangGoogs where fk_good_id = ? and status = '入库'";
			return (WareBangGoogs) totalDao.getObjectByCondition(hql, id);
		}
		return null;
	}

	@Override
	public String updateWareBangGoogs(List<WareBangGoogs> bangList) {
		if (bangList != null && bangList.size() > 0) {
			for (WareBangGoogs bang : bangList) {
				if (bang.getLqNumber() > 0
						&& bang.getLqNumber() < bang.getNumber()) {
					bang.setNumber(bang.getNumber() - bang.getLqNumber());
					totalDao.update(bang);
					WarehouseNumber wn = (WarehouseNumber) totalDao.get(
							WarehouseNumber.class, bang.getFk_ware_id());
					wn.setStatus("未满");
					totalDao.update(wn);
				} else if (bang.getLqNumber().equals(bang.getNumber())) {
					totalDao.delete(bang);
					WarehouseNumber wn = (WarehouseNumber) totalDao.get(
							WarehouseNumber.class, bang.getFk_ware_id());
					wn.setHave("无");
					wn.setStatus("未满");
					totalDao.update(wn);
				}
			}
		}
		return null;
	}

	public String oaCloseW(WarehouseNumber wn) {
		// TODO Auto-generated method stub
		String message = UtilTong.OCKuWei(wn.getIp(), duankou, false, wn
				.getOneNumber(), wn.getNumid());// 关门操作
		if ("true".equals(message)) {
			wn.setKwStatus("关");
			wn.setCzUserId(null);
			if (totalDao.update(wn)) {// 更新库位状态
			} else
				totalDao.update2(wn);
			return "true";
		}
		return "连接异常，关门失败！";
	}

	@Override
	public String oaCloseWN(WareBangGoogs bang) {
		if (bang != null && bang.getFk_ware_id() != null) {
			WarehouseNumber wn = (WarehouseNumber) totalDao.get(
					WarehouseNumber.class, bang.getFk_ware_id());
			return oaCloseW(wn);
		}
		return "没有得到中间表信息!";
	}

	@Override
	public String outwwcl(WaigouOrder order, List<Goods> goodsList,
			int[] selected) {
		// TODO Auto-generated method stub
		WaigouOrder oldOrder = (WaigouOrder) totalDao.getObjectById(
				WaigouOrder.class, order.getId());
		// if (oldOrder.getWlStatus() != null
		// && !oldOrder.getWlStatus().equals("待出库")) {
		// return "当前订单状态为:" + oldOrder.getWlStatus() + "不能出库";
		// }
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (selected == null || selected.length == 0) {
			return "请先选择!";
		}
		Map<String, Float> outMarkIdMap = new HashMap<String, Float>();
		Boolean useyl = !ProcardBlServerImpl.SystemShezhi("关闭余料库", "关闭余料库");
		List<Sell> sellList = new ArrayList<Sell>();
		Map<String, List<String>> markIdAndOrdernumbermap = new HashMap<String, List<String>>();
		Map<String, List<String>> markIdAndywMarkId = new HashMap<String, List<String>>();
		String time = Util.getDateTime();
		if ("BOM外委".equals(oldOrder.getWwSource())) {
			Set<WaigouPlan> wwplanSet = oldOrder.getWwpSet();
			for (Goods goods : goodsList) {
				boolean b = false;
				if (goods.getGoodsId() != null) {
					for (int sId : selected) {
						if (goods.getGoodsId().equals(sId)) {
							b = true;
							break;
						}
					}
				}
				if (!b) {// 没有被选中
					continue;
				}
				Goods oldGoods = (Goods) totalDao.getObjectById(Goods.class,
						goods.getGoodsId());
				if (goods.getGoodsCurQuantity() > 0) {
					boolean pp = false;
					for (WaigouPlan wgplan : wwplanSet) {
						if (wgplan.getMarkId()
								.equals(oldGoods.getGoodsMarkId())) {
							pp = true;
							if (wgplan.getStatus().equals("供应商领料")) {
								wgplan.setStatus("生产中");
								totalDao.update(wgplan);
							}
						}
					}
					if (!pp && !oldGoods.getGoodsClass().equals("外购件库")) {// 上层领取下层生产件
						String scmarkId = (String) totalDao
								.getObjectByCondition(
										"select procard.markId from Procard where id =?",
										goods.getOrder_Id());
						if (scmarkId != null) {
							for (WaigouPlan wgplan : wwplanSet) {
								if (wgplan.getMarkId().equals(scmarkId)
										&& wgplan.getStatus().equals("供应商领料")) {
									wgplan.setStatus("生产中");
									totalDao.update(wgplan);
								}
							}
						}
					}
					String kg = oldGoods.getKgliao() == null ? "" : oldGoods
							.getKgliao();
					String outMarkId = oldGoods.getGoodsMarkId() + kg;
					Float yl = outMarkIdMap.get(outMarkId);
					if (yl == null) {
						yl = goods.getGoodsCurQuantity();
					} else {
						yl = yl + goods.getGoodsCurQuantity();
					}
					outMarkIdMap.put(outMarkId, yl);
				} else {
					continue;
				}
				if (oldGoods.getGoodsCurQuantity() < goods
						.getGoodsCurQuantity()) {
					throw new RuntimeException("数据异常:"
							+ oldGoods.getGoodsMarkId() + "第"
							+ oldGoods.getGoodsLotId() + "批次的"
							+ oldGoods.getGoodsStoreZHUnit() + "数不足出库");
				} else {
					oldGoods.setGoodsCurQuantity(oldGoods.getGoodsCurQuantity()
							- goods.getGoodsCurQuantity());
				}
				// 出库记录
				Sell zzpSell = new Sell();
				if (goods.getOrder_Id() != null) {
					Procard procard = (Procard) totalDao.getObjectById(
							Procard.class, goods.getOrder_Id());
					if (goods.getGoodsZhishu() != null
							&& goods.getGoodsZhishu() > 0) {
						if (oldGoods.getGoodsZhishu() < goods.getGoodsZhishu()) {
							throw new RuntimeException("数据异常:"
									+ oldGoods.getGoodsMarkId() + "第"
									+ oldGoods.getGoodsLotId() + "批次的"
									+ oldGoods.getGoodsStoreZHUnit() + "数不足出库");
						} else {
							oldGoods.setGoodsZhishu(oldGoods.getGoodsZhishu()
									- goods.getGoodsZhishu());
						}
					}
					if (procard.getZaizhizkCount() == null) {
						procard.setZaizhizkCount(0f);
					} else {
						procard.setZaizhizkCount(procard.getZaizhizkCount()
								- goods.getGoodsCurQuantity());
					}
					totalDao.update(procard);
					Float outCount = goods.getGoodsCurQuantity();
					List<ProcardProductRelation> pprList = totalDao
							.query(
									"from ProcardProductRelation where procardId=? and goodsId=?",
									procard.getId(), oldGoods.getGoodsId());
					for (ProcardProductRelation ppr : pprList) {
						if ((ppr.getYlCount() - ppr.getCkCount()) >= outCount) {
							ppr.setCkCount(ppr.getCkCount() + outCount);
							outCount = 0f;
						} else {
							outCount = ppr.getYlCount() - ppr.getCkCount();
							ppr.setCkCount(ppr.getYlCount());
						}
						totalDao.update(ppr);
						if (outCount == null) {
							break;
						}
					}
					zzpSell.setSellArtsCard(procard.getRootSelfCard());
				}
				GoodsServerImpl.pushkc(null, oldGoods);
				totalDao.update(oldGoods);
				if (!oldGoods.getGoodsClass().equals("外购件库")) {// 关联订单号
					String orderNumber = (String) totalDao
							.getObjectByCondition(
									"select orderNumber from Procard where markId=? and selfCard=? and (sbStatus is null or sbStatus!='删除')",
									oldGoods.getGoodsMarkId(), oldGoods
											.getGoodsLotId());
					zzpSell.setOrderNum(orderNumber);
				}
				setSellZZ(order, oldOrder, user, goods, oldGoods, zzpSell, time);
				sellList.add(zzpSell);
				if (wwplanSet != null && wwplanSet.size() > 0) {
					for (WaigouPlan wwp : wwplanSet) {
						if (wwp.getMarkId().equals(oldGoods.getGoodsMarkId())
								&& "供应商领料".equals(wwp.getStatus())) {
							wwp.setStatus("生产中");
							totalDao.update(wwp);
						}
					}
				}
			}
			if (outMarkIdMap.size() > 0) {
				if (wwplanSet != null && wwplanSet.size() > 0) {
					for (WaigouPlan wwp : wwplanSet) {
						// 领下层外购件
						boolean cl = false;
						List<ProcessInforWWProcard> processwwprocardList = totalDao
								.query(
										"  from ProcessInforWWProcard where (status is null or status not in ('删除','取消')) and procardId in(select id from Procard where (sbStatus is null or sbStatus!='删除') and procard.id in(select procardId from ProcardWGCenter where  wgOrderId=?) )"
												+ " and wwxlId in(select wwxlId from ProcardWGCenter where  wgOrderId=?)",
										wwp.getId(), wwp.getId());
						if (processwwprocardList != null
								&& processwwprocardList.size() > 0) {
							for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
								Procard son = byProcardId(processInforWWProcard);
								if (son == null)
									throw new RuntimeException("对不起id为"
											+ processInforWWProcard
													.getProcardId()
											+ "的流水卡片不存在 ！请检查");
								String kg = son.getKgliao() == null ? "TK"
										: son.getKgliao();
								String outMarkId = son.getMarkId() + kg;
								Float yl = outMarkIdMap.get(outMarkId);
								Float scCount = 0f;
								if (processInforWWProcard.getHascount() == null) {
									processInforWWProcard
											.setHascount(processInforWWProcard
													.getApplyCount());
								}
								float count = processInforWWProcard
										.getHascount();
								if (yl == null || yl == 0 || count == 0) {// 此外购件没有可领数量了
									continue;
								} else {
									if (yl > count) {
										yl = yl - count;
									} else {
										count = yl;
										yl = 0f;
									}
									cl = true;
								}
								if (wwp.getStatus().equals("供应商领料")) {
									wwp.setStatus("生产中");
									totalDao.update(wwp);
								}
								outMarkIdMap.put(outMarkId, yl);
								List<String> ordernumberList = markIdAndOrdernumbermap
										.get(son.getMarkId());
								if (ordernumberList == null) {
									ordernumberList = new ArrayList<String>();
									ordernumberList.add(son.getOrderNumber());
								} else if (!ordernumberList.contains(son
										.getOrderNumber())) {
									ordernumberList.add(son.getOrderNumber());
								}
								markIdAndOrdernumbermap.put(son.getMarkId(),
										ordernumberList);
								List<String> ywmarkIdList = markIdAndywMarkId
										.get(son.getMarkId());
								if (ywmarkIdList == null) {
									ywmarkIdList = new ArrayList<String>();
									ywmarkIdList.add(son.getYwMarkId());
								} else if (!ywmarkIdList.contains(son
										.getYwMarkId())) {
									ywmarkIdList.add(son.getYwMarkId());
								}
								markIdAndywMarkId.put(son.getMarkId(),
										ywmarkIdList);
								scCount = count * son.getQuanzi1()
										/ son.getQuanzi2();
								if (scCount % 1 > 0.9) {
									scCount = (float) Math.ceil(scCount);
								} else {
									scCount = (float) Math.floor(scCount);
								}
								// float count = applyDetail.getApplyCount();
								float tqlcount = 0f;
								// count = count * son.getQuanzi2() /
								// son.getQuanzi1();
								if (son.getKlNumber() == null) {
									son.setKlNumber(son.getFilnalCount());
								}
								if (son.getHascount() == null) {
									son.setHascount(son.getKlNumber());
								}
								if (son.getHascount() < count) {
									throw new RuntimeException("对不起此"
											+ son.getMarkId() + "的可领数量为"
											+ son.getHascount()
											+ ",数据异常请联系管理员！");
								}
								son.setHascount(son.getHascount() - count);
								if(son.getHascount()<0||(son.getHascount()*son.getQuanzi1()/son.getQuanzi2())<0.005){
									son.setHascount(0f);
								}
								if (son.getHascount() == 0) {
									son.setStatus("完成");
								}
								if (son.getTjNumber() == null
										|| son.getTjNumber() < (son
												.getKlNumber() - son
												.getHascount())) {
									son.setTjNumber(son.getKlNumber()
											- son.getHascount());

								}
								totalDao.update(son);
								processInforWWProcard
										.setHascount(processInforWWProcard
												.getHascount()
												- count);
								totalDao.update(processInforWWProcard);
								// 添加外购件在制品数量
								String hqlzaizhi = "from Goods where goodsMarkId='"
										+ son.getMarkId()
										+ "' and goodsLotId='"
										+ son.getSelfCard()
										+ "' and goodsClass='在制品' and goodsStyle!='半成品转库' and (fcStatus is null or fcStatus='可用') ";
								List listzizhi = totalDao.query(hqlzaizhi);
								Integer rgoodsId = null;
								if (listzizhi != null && listzizhi.size() > 0) {
									Goods g1 = (Goods) listzizhi.get(0);
									g1.setGoodsCurQuantity(g1
											.getGoodsCurQuantity()
											+ count);
									if (g1.getGoodsCurQuantity() < 0) {
										sendMessage(g1);
										g1.setGoodsCurQuantity(0f);
									}
									totalDao.update(g1);
									rgoodsId = g1.getGoodsId();
								} else {
									Goods gg = addZzpGoods(son, count);
									rgoodsId = gg.getGoodsId();
								}
								// 添加零件与在制品关系表
								addProcardProductRelation(son, count, rgoodsId);
								// 添加外购件在制品入库记录
								Users jingban = addZzpGoodsStore_1(user, son,
										count);

								// 首工序外委要生成零件在制品
								Procard procard = (Procard) totalDao
										.getObjectById(Procard.class, son
												.getProcard().getId());
								Integer minProcessNo = (Integer) totalDao
										.getObjectByCondition(
												"select processNO from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')  order by processNO",
												son.getProcard().getId());
								String[] processNos = wwp.getProcessNOs()
										.split(";");
								if (processNos != null && processNos.length > 0) {
									for (String s : processNos) {
										if (s.equals(minProcessNo + "")) {
											// 减去可伶数量
											if (procard.getKlNumber() == null) {
												procard.setKlNumber(procard
														.getFilnalCount());
											}
											Float thishacount = 0f;
											Set<Procard> sontohascountset = procard.getProcardSet();
											for(Procard sontohascount:sontohascountset){
												if(sontohascount.getProcardStyle().equals("外购")&&sontohascount.getSbStatus()==null
														||!sontohascount.getSbStatus().equals("删除")){
													Float thishacount2 =null;
													if(sontohascount.getHascount()!=null){
														thishacount2 = sontohascount.getHascount()*sontohascount.getQuanzi1()/sontohascount.getQuanzi2();
													}else{
														thishacount2  = procard.getKlNumber();
													}
													if(thishacount2>thishacount){
														thishacount= thishacount2;
													}
												}
											}
											procard
													.setHascount(thishacount);
											// 添加在制品数量
											String hqlpzaizhi = "from Goods where goodsMarkId='"
													+ procard.getMarkId()
													+ "' and goodsLotId='"
													+ procard.getSelfCard()
													+ "' and goodsClass='在制品' and goodsStyle!='半成品转库' and (fcStatus is null or fcStatus='可用') ";
											List listpzizhi = totalDao
													.query(hqlpzaizhi);
											Integer prgoodsId = null;
											if (listpzizhi != null
													&& listpzizhi.size() > 0) {
												Goods g1 = (Goods) listpzizhi
														.get(0);
												g1.setGoodsCurQuantity(g1
														.getGoodsCurQuantity()
														+ scCount);
												if (g1.getGoodsCurQuantity() < 0) {
													sendMessage(g1);
													g1.setGoodsCurQuantity(0f);
												}
												totalDao.update(g1);
												prgoodsId = g1.getGoodsId();
											} else {
												Goods gg = addZzpGoods(scCount,
														procard);
												rgoodsId = gg.getGoodsId();
											}
											// 添加零件与在制品关系表
											addProcardProductRelation(procard,
													scCount, rgoodsId);
											// 添加外购件在制品入库记录
											addZzpGoodsStore(user, scCount,
													jingban, procard);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		} else {// 手工挑选外委
			Set<WaigouPlan> wgplanSet = oldOrder.getWwpSet();
			for (Goods goods : goodsList) {
				boolean b = false;
				if (goods.getGoodsId() != null) {
					for (int sId : selected) {
						if (goods.getGoodsId().equals(sId)) {
							b = true;
							break;
						}
					}
				}
				if (!b) {// 没有被选中
					continue;
				}
				Goods oldGoods = (Goods) totalDao.getObjectById(Goods.class,
						goods.getGoodsId());

				if (goods.getGoodsCurQuantity() > 0) {
					boolean pp = false;
					for (WaigouPlan wgplan : wgplanSet) {
						if (wgplan.getMarkId()
								.equals(oldGoods.getGoodsMarkId())) {
							//是否为零件尾工序外委
							Integer maxProcessno = (Integer) totalDao.getObjectByCondition("select max(processNO) " +
									"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') ", goods.getOrder_Id());
							if(maxProcessno!=null&&wgplan.getProcessNOs().equals(maxProcessno+"")
									||wgplan.getProcessNOs().startsWith(";"+maxProcessno)){
								List<WaigouPlan> scwpList =  totalDao.query("from WaigouPlan where gysId=?" +
										" and status='供应商领料' and id in(select wgOrderId from ProcardWGCenter where " +
												"procardId=(select procard.id from Procard where id =?))", oldOrder.getGysId(),goods.getOrder_Id());
								if(scwpList!=null&&scwpList.size()>0){
									Integer scminProcessno = (Integer) totalDao.getObjectByCondition("select min(processNO) " +
											"from ProcessInfor where procard.id=(select procard.id from Procard where id =?)" +
											" and (dataStatus is null or dataStatus !='删除') ", goods.getOrder_Id());
									for(WaigouPlan scwp:scwpList){
										if(scwp.getProcessNOs().equals(scminProcessno+"")
												||scwp.getProcessNOs().startsWith(scminProcessno+";")){
											wgplan.setStatus("生产中");
											totalDao.update(wgplan);
										}
									}
								}
							}
							pp = true;
							if (wgplan.getStatus().equals("供应商领料")) {
								wgplan.setStatus("生产中");
								totalDao.update(wgplan);
							}
						}
					}
					if (!pp && !oldGoods.getGoodsClass().equals("外购件库")) {// 上层领取下层生产件
						String scmarkId = (String) totalDao
								.getObjectByCondition(
										"select procard.markId from Procard where id =?",
										goods.getOrder_Id());
						if (scmarkId != null) {
							for (WaigouPlan wgplan : wgplanSet) {
								if (wgplan.getMarkId().equals(scmarkId)
										&& wgplan.getStatus().equals("供应商领料")) {
									wgplan.setStatus("生产中");
									totalDao.update(wgplan);
								}
							}
						}
					}

					String kg = oldGoods.getKgliao() == null ? "" : oldGoods
							.getKgliao();
					String outMarkId = oldGoods.getGoodsMarkId() + kg;
					Float yl = outMarkIdMap.get(outMarkId);
					if (yl == null) {
						yl = goods.getGoodsCurQuantity();
					} else {
						yl = yl + goods.getGoodsCurQuantity();
					}
					outMarkIdMap.put(outMarkId, yl);
				}
				if (goods.getGoodsZhishu() != null
						&& goods.getGoodsZhishu() > 0) {
					if (oldGoods.getGoodsZhishu() < goods.getGoodsZhishu()) {
						throw new RuntimeException("数据异常:"
								+ oldGoods.getGoodsMarkId() + "第"
								+ oldGoods.getGoodsLotId() + "批次的"
								+ oldGoods.getGoodsStoreZHUnit() + "数不足出库");
					} else {
						oldGoods.setGoodsZhishu(oldGoods.getGoodsZhishu()
								- goods.getGoodsZhishu());
					}
				}
				if (oldGoods.getGoodsCurQuantity() < goods
						.getGoodsCurQuantity()) {
					throw new RuntimeException("数据异常:"
							+ oldGoods.getGoodsMarkId() + "第"
							+ oldGoods.getGoodsLotId() + "批次的"
							+ oldGoods.getGoodsStoreZHUnit() + "数不足出库");
				} else {
					oldGoods.setGoodsCurQuantity(oldGoods.getGoodsCurQuantity()
							- goods.getGoodsCurQuantity());
					if (useyl
							&& goods.getGoodsCurQuantity() > goods
									.getGoodsBeginQuantity()) {// 实发数量大于需求数量
						// 多余为余料，余料入库
						yLiaoKuRuku(oldOrder, user, goods, oldGoods);
					}
				}
				GoodsServerImpl.pushkc(null, oldGoods);
				totalDao.update(oldGoods);
				// 出库记录
				Sell zzpSell = new Sell();
				// zzpSell.setSellArtsCard(order.get);
				if (!oldGoods.getGoodsClass().equals("外购件库")) {// 关联订单号
					String orderNumber = (String) totalDao
							.getObjectByCondition(
									"select orderNumber from Procard where markId=? and selfCard=? and (sbStatus is null or sbStatus!='删除')",
									oldGoods.getGoodsMarkId(), oldGoods
											.getGoodsLotId());
					zzpSell.setOrderNum(orderNumber);
				}
				setSellZZ(order, oldOrder, user, goods, oldGoods, zzpSell, time);
				sellList.add(zzpSell);
			}
			if (wgplanSet != null && wgplanSet.size() > 0) {
				for (WaigouPlan wgplan : wgplanSet) {
					if (wgplan.getWwType().equals("包工包料")) {
						continue;
					}
					boolean cl = false;
					List<ProcessInforWWApplyDetail> applyDetailList = totalDao
							.query(
									"from ProcessInforWWApplyDetail where id in(select wwxlId from ProcardWGCenter where wgOrderId=?)",
									wgplan.getId());
					if (applyDetailList != null && applyDetailList.size() > 0) {
						for (ProcessInforWWApplyDetail applyDetail : applyDetailList) {
							// 出外购件
							List<ProcessInforWWProcard> processwwprocardList = totalDao
									.query(
											"from ProcessInforWWProcard where applyDtailId=? and (status is null or status not in ('删除','取消')) ",
											applyDetail.getId());
							if (processwwprocardList != null
									&& processwwprocardList.size() > 0) {
								for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
									// 存在外购件，判断库存是否充足
									// 第三步计算外购件需领数量
									// 外购件需领数量
									Procard son = byProcardId(processInforWWProcard);
									String kg = son.getKgliao() == null ? "TK"
											: son.getKgliao();
									String outMarkId = son.getMarkId() + kg;
									Float yl = outMarkIdMap.get(outMarkId);
									if (processInforWWProcard.getHascount() == null) {
										processInforWWProcard
												.setHascount(processInforWWProcard
														.getApplyCount());
									}
									float count = processInforWWProcard
											.getHascount();
									if (yl == null || yl == 0 || count == 0) {// 此外购件没有领
										continue;
									} else {
										if (yl > count) {
											yl = yl - count;
										} else {
											count = yl;
											yl = 0f;
										}
										cl = true;
									}
									outMarkIdMap.put(outMarkId, yl);
									List<String> ordernumberList = markIdAndOrdernumbermap
											.get(son.getMarkId());
									if (ordernumberList == null) {
										ordernumberList = new ArrayList<String>();
										ordernumberList.add(son
												.getOrderNumber());
									} else if (!ordernumberList.contains(son
											.getOrderNumber())) {
										ordernumberList.add(son
												.getOrderNumber());
									}
									markIdAndOrdernumbermap.put(
											son.getMarkId(), ordernumberList);
									// float count =
									// applyDetail.getApplyCount();
									List<String> ywmarkIdList = markIdAndywMarkId
											.get(son.getMarkId());
									if (ywmarkIdList == null) {
										ywmarkIdList = new ArrayList<String>();
										ywmarkIdList.add(son.getYwMarkId());
									} else if (!ywmarkIdList.contains(son
											.getYwMarkId())) {
										ywmarkIdList.add(son.getYwMarkId());
									}
									markIdAndywMarkId.put(son.getMarkId(),
											ywmarkIdList);
									float tqlcount = 0f;
									// count = count * son.getQuanzi2() /
									// son.getQuanzi1();
									if (son.getKlNumber() == null) {
										son.setKlNumber(son.getFilnalCount());
									}
									if (son.getHascount() == null) {
										son.setHascount(son.getKlNumber());
									}
									Float ff = son.getHascount() - count;
									if (ff < 0) {
										ff = 0f;
										// throw new
										// RuntimeException("对不起此"+son.getMarkId()+"的可领数量为"
										// + son.getHascount() +
										// ",数据异常请联系管理员！");
									}
									son.setHascount(ff);
									if(son.getHascount()<0||(son.getHascount()*son.getQuanzi1()/son.getQuanzi2())<0.005){
										son.setHascount(0f);
									}
									if (son.getHascount() == 0) {
										son.setStatus("完成");
									}
									if (son.getTjNumber() == null
											|| son.getTjNumber() < (son
													.getKlNumber() - son
													.getHascount())) {
										son.setTjNumber(son.getKlNumber()
												- son.getHascount());

									}
									totalDao.update(son);
									processInforWWProcard
											.setHascount(processInforWWProcard
													.getHascount()
													- count);
									totalDao.update(processInforWWProcard);
									// 添加外购件在制品数量
									String hqlzaizhi = "from Goods where goodsMarkId='"
											+ son.getMarkId()
											+ "' and goodsLotId='"
											+ son.getSelfCard()
											+ "' and goodsClass='在制品' and goodsStyle!='半成品转库' and (fcStatus is null or fcStatus='可用') ";
									List listzizhi = totalDao.query(hqlzaizhi);
									Integer rgoodsId = null;
									if (listzizhi != null
											&& listzizhi.size() > 0) {
										Goods g1 = (Goods) listzizhi.get(0);
										g1.setGoodsCurQuantity(g1
												.getGoodsCurQuantity()
												+ count);
										if (g1.getGoodsCurQuantity() < 0) {
											sendMessage(g1);
											g1.setGoodsCurQuantity(0f);
										}
										totalDao.update(g1);
										rgoodsId = g1.getGoodsId();
									} else {
										Goods gg = addZzpGoods(son, count);
										rgoodsId = gg.getGoodsId();
									}
									// 添加零件与在制品关系表
									addProcardProductRelation(son, count,
											rgoodsId);
									// 添加外购件在制品入库记录
									Users jingban = addZzpGoodsStore_1(user,
											son, count);

									// 手动外委首工序外委要生成零件在制品
									Integer minProcessNo = (Integer) totalDao
											.getObjectByCondition(
													"select processNO from ProcessInfor where procard.id=? order by processNO",
													applyDetail.getProcardId());
									Procard procard = byProcardId(applyDetail);
									String[] processNos = wgplan
											.getProcessNOs().split(";");
									if (processNos != null
											&& processNos.length > 0) {
										for (String s : processNos) {
											if (s.equals(minProcessNo + "")) {
												// 减去可领数量
												if (procard.getKlNumber() == null) {
													procard.setKlNumber(procard
															.getFilnalCount());
												}
//												if (procard.getHascount() == null) {
//													procard.setHascount(procard
//															.getKlNumber());
//												}
												Float thishacount = 0f;
												Set<Procard> sontohascountset = procard.getProcardSet();
												for(Procard sontohascount:sontohascountset){
													if(sontohascount.getProcardStyle().equals("外购")
															&&!"删除".equals(sontohascount.getSbStatus())){
														Float thishacount2 =null;
														if(sontohascount.getHascount()!=null){
															thishacount2 = sontohascount.getHascount()*sontohascount.getQuanzi1()/sontohascount.getQuanzi2();
														}else{
															thishacount2  = procard.getKlNumber();
														}
														if(thishacount2>thishacount){
															thishacount= thishacount2;
														}
													}
												}
												procard
														.setHascount(thishacount);
												// 添加在制品数量
												String hqlpzaizhi = "from Goods where goodsMarkId='"
														+ procard.getMarkId()
														+ "' and goodsLotId='"
														+ procard.getSelfCard()
														+ "' and goodsClass='在制品' and goodsStyle!='半成品转库' and (fcStatus is null or fcStatus='可用') ";
												List listpzizhi = totalDao
														.query(hqlpzaizhi);
												Integer prgoodsId = null;
												if (listpzizhi != null
														&& listpzizhi.size() > 0) {
													Goods g1 = (Goods) listpzizhi
															.get(0);
													g1
															.setGoodsCurQuantity(g1
																	.getGoodsCurQuantity()
																	+ applyDetail
																			.getApplyCount());
													if (g1
															.getGoodsCurQuantity() < 0) {
														sendMessage(g1);
														g1
																.setGoodsCurQuantity(0f);
													}
													totalDao.update(g1);
													prgoodsId = g1.getGoodsId();
												} else {
													Goods gg = addZzpGoods(
															applyDetail
																	.getApplyCount(),
															procard);
													rgoodsId = gg.getGoodsId();
												}
												// 添加零件与在制品关系表
												addProcardProductRelation(
														procard,
														applyDetail
																.getApplyCount(),
														rgoodsId);
												// 添加外购件在制品入库记录
												addZzpGoodsStore(
														user,
														applyDetail
																.getApplyCount(),
														jingban, procard);
												break;
											}
										}
									}
								}
							}
						}
					}
					// Float noOUtCount = (Float)
					// totalDao.getObjectByCondition("select count(*) from ProcessInforWWProcard where applyDtailId =? where hascount is null or hascount>0",
					// wgplan.getWwDetailId());
					if (cl && wgplan.getStatus().equals("供应商领料")) {
						wgplan.setStatus("生产中");
						totalDao.update(wgplan);
					}
				}
			}
		}
		oldOrder.setWlStatus("已出库");
		oldOrder.setStatus("生产中");
		totalDao.update(oldOrder);
		if (sellList != null && sellList.size() > 0) {
			for (Sell sell : sellList) {
				boolean chage = false;
				List<String> orderNumberList = markIdAndOrdernumbermap.get(sell
						.getSellMarkId());
				if (orderNumberList != null && orderNumberList.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (String orderNumber : orderNumberList) {
						if (sb.length() == 0) {
							sb.append(orderNumber);
						} else {
							sb.append("," + orderNumber);
						}
					}
					sell.setOrderNum(sb.toString());
					chage = true;
				}
				List<String> ywMarkIdList = markIdAndywMarkId.get(sell
						.getSellMarkId());
				if (ywMarkIdList != null && ywMarkIdList.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (String ywMarkId : ywMarkIdList) {
						if (sb.length() == 0) {
							sb.append(ywMarkId);
						} else {
							sb.append("," + ywMarkId);
						}
					}
					sell.setYwmarkId(sb.toString());
					chage = true;
				}
				if (chage) {
					totalDao.update(sell);
				}
			}
		}
		return "true";
	}

	/**
	 * 根据Id到procard
	 * 
	 * @param processInforWWProcard
	 * @return
	 */
	private Procard byProcardId(ProcessInforWWProcard processInforWWProcard) {
		Procard son = (Procard) totalDao.getObjectById(Procard.class,
				processInforWWProcard.getProcardId());
		return son;
	}

	private Procard byProcardId(ProcessInforWWApplyDetail applyDetail) {
		Procard procard = (Procard) totalDao.getObjectById(Procard.class,
				applyDetail.getProcardId());
		return procard;
	}

	private Procard byProcardid(Procard pro) {
		Procard oldProcard = (Procard) totalDao.getObjectById(Procard.class,
				pro.getId());
		return oldProcard;
	}

	private Procard byProcardid(int prorootId) {
		return (Procard) totalDao.getObjectById(Procard.class, prorootId);
	}

	private Procard byProcardRootId(Procard procard) {
		Procard totalCard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getRootId());
		return totalCard;
	}

	/**
	 * 异常发送消息
	 * 
	 * @param g1
	 */
	private void sendMessage(Goods g1) {
		AlertMessagesServerImpl.addAlertMessages("系统维护异常组", "件号:"
				+ g1.getGoodsMarkId() + "批次:" + g1.getGoodsLotId()
				+ "可领数量小于零，系统自动修复为0，操作是：领料,当前系统时间为" + Util.getDateTime(), "2");
	}

	/**
	 * 添加在制品
	 * 
	 * @param son
	 * @param count
	 * @return
	 */
	private Goods addZzpGoods(Procard son, float count) {
		Goods gg = new Goods();
		gg.setGoodsMarkId(son.getMarkId());
		gg.setBanBenNumber(son.getBanBenNumber());
		gg.setGoodsFormat(son.getSpecification());
		gg.setTuhao(son.getTuhao());
		gg.setGoodsLotId(son.getSelfCard());
		gg.setGoodsFullName(son.getProName());
		gg.setBanBenNumber(son.getBanBenNumber());
		gg.setGoodsClass("在制品");
		gg.setGoodsCurQuantity(count);
		gg.setGoodsBeginQuantity(count);
		gg.setGoodsChangeTime(Util.getDateTime());
		if (gg.getGoodsCurQuantity() < 0) {
			sendMessage(gg);
			gg.setGoodsCurQuantity(0f);
		}
		gg.setGoodsUnit(son.getUnit());
		totalDao.save(gg);
		return gg;
	}

	/**
	 * 添加外购件在制品入库记录
	 * 
	 * @param user
	 * @param son
	 * @param count
	 * @return
	 */
	private Users addZzpGoodsStore_1(Users user, Procard son, float count) {
		GoodsStore gs = new GoodsStore();
		gs.setGoodsStoreMarkId(son.getMarkId());// 件号
		gs.setGoodsStoreFormat(son.getSpecification());
		gs.setTuhao(son.getTuhao());
		gs.setBanBenNumber(son.getBanBenNumber());
		gs.setGoodsStoreGoodsName(son.getProName());// 名称
		gs.setGoodsStoreLot(son.getSelfCard());// 批次
		gs.setGoodsStoreCount(count);// 数量
		gs.setPrintStatus("YES");
		gs.setGoodsStoreProMarkId(son.getRootMarkId());// 总成件号
		gs.setGoodsStoreWarehouse("在制品");// 库别
		Users jingban = Util.getLoginUser();
		gs.setGoodsStoreCharger(jingban.getName());// 经办人
		gs.setStyle("正常（成品）");// 入库类型
		if (user != null) {// 负责人
			gs.setGoodsStorePerson(user.getName());
		} else {
			gs.setGoodsStorePerson(son.getLingliaoren());
		}
		gs.setGoodsStoreDate(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		gs.setGoodsStoreTime(Util.getDateTime());
		gs.setGoodsStoreUnit(son.getUnit());// 单位
		totalDao.save(gs);
		return jingban;
	}

	/**
	 * 添加外购件在制品入库记录
	 * 
	 * @param user
	 * @param scCount
	 * @param jingban
	 * @param procard
	 */
	private void addZzpGoodsStore(Users user, Float scCount, Users jingban,
			Procard procard) {
		GoodsStore gs2 = new GoodsStore();
		gs2.setGoodsStoreMarkId(procard.getMarkId());// 件号
		gs2.setGoodsStoreFormat(procard.getSpecification());
		gs2.setTuhao(procard.getTuhao());
		gs2.setBanBenNumber(procard.getBanBenNumber());
		gs2.setGoodsStoreGoodsName(procard.getProName());// 名称
		gs2.setGoodsStoreLot(procard.getSelfCard());// 批次
		gs2.setGoodsStoreCount(scCount);// 数量
		gs2.setPrintStatus("YES");
		gs2.setGoodsStoreProMarkId(procard.getRootMarkId());// 总成件号
		gs2.setGoodsStoreWarehouse("在制品");// 库别
		gs2.setGoodsStoreCharger(jingban.getName());// 经办人
		gs2.setStyle("正常（成品）");// 入库类型
		if (user != null) {// 负责人
			gs2.setGoodsStorePerson(user.getName());
		} else {
			gs2.setGoodsStorePerson(procard.getLingliaoren());
		}
		gs2.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		gs2.setGoodsStoreTime(Util.getDateTime());
		gs2.setGoodsStoreUnit(procard.getUnit());// 单位
		totalDao.save(gs2);
	}

	/**
	 * 添加零件与在制品关系表
	 * 
	 * @param scCount
	 * @param rgoodsId
	 * @param pprelation
	 * @param procard
	 */
	private void addProcardProductRelation(Procard procard, Float scCount,
			Integer rgoodsId) {
		ProcardProductRelation pprelation2 = new ProcardProductRelation();
		pprelation2.setAddTime(Util.getDateTime());
		pprelation2.setProcardId(procard.getId());
		pprelation2.setGoodsId(rgoodsId);
		pprelation2.setZyCount(scCount);
		pprelation2.setFlagType("本批在制品");
		totalDao.save(pprelation2);
	}

	/**
	 * 在制品入库
	 * 
	 * @param scCount
	 * @param procard
	 * @return
	 */
	private Goods addZzpGoods(Float scCount, Procard procard) {
		Goods gg = new Goods();
		gg.setGoodsMarkId(procard.getMarkId());
		gg.setBanBenNumber(procard.getBanBenNumber());
		gg.setGoodsFormat(procard.getSpecification());
		gg.setTuhao(procard.getTuhao());
		gg.setGoodsLotId(procard.getSelfCard());
		gg.setGoodsFullName(procard.getProName());
		gg.setBanBenNumber(procard.getBanBenNumber());
		gg.setGoodsClass("在制品");
		gg.setGoodsCurQuantity(scCount);
		gg.setGoodsBeginQuantity(scCount);
		gg.setGoodsChangeTime(Util.getDateTime());
		if (gg.getGoodsCurQuantity() < 0) {
			sendMessage(gg);
			gg.setGoodsCurQuantity(0f);
		}
		gg.setGoodsUnit(procard.getUnit());
		totalDao.save(gg);
		return gg;
	}

	/**
	 * 余料入库
	 * 
	 * @param oldOrder
	 * @param user
	 * @param goods
	 * @param oldGoods
	 */
	private void yLiaoKuRuku(WaigouOrder oldOrder, Users user, Goods goods,
			Goods oldGoods) {
		Goods ylGoods = new Goods();
		ylGoods.setGoodsClass("余料库");
		ylGoods.setLlGysId(oldOrder.getGysId());
		ylGoods.setGoodsBeginQuantity(goods.getGoodsCurQuantity()
				- goods.getGoodsBeginQuantity());
		ylGoods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
				- goods.getGoodsBeginQuantity());
		ylGoods.setGoodsZhishu(1f);
		ylGoods.setYlMarkId(oldGoods.getGoodsMarkId());
		// ylGoods.setYlSelfCard(ol);
		ylGoods.setGoodsMarkId(oldGoods.getGoodsMarkId());
		ylGoods.setGoodsFormat(oldGoods.getGoodsFormat());
		ylGoods.setGoodsUnit(oldGoods.getGoodsUnit());
		ylGoods.setGoodsChangeTime(Util.getDateTime("yyyy-MM-dd"));
		ylGoods.setKgliao(oldGoods.getKgliao());
		// ylGoods.setYllock(procard.getIsycl());
		ylGoods.setWgType(oldGoods.getWgType());
		totalDao.save(ylGoods);
		// 余料入库记录
		GoodsStore ylgs = new GoodsStore();
		ylgs.setGoodsStoreMarkId(oldGoods.getGoodsMarkId());// 件号
		ylgs.setGoodsStoreFormat(oldGoods.getGoodsFormat());// 规格
		ylgs.setGoodsStoreGoodsName(oldGoods.getGoodsFullName());// 名称
		// ylgs.setGoodsStoreLot(procard.getSelfCard());// 批次
		ylgs.setGoodsStoreCount(goods.getGoodsCurQuantity()
				- goods.getGoodsBeginQuantity());// 数量
		ylgs.setPrintStatus("YES");
		ylgs.setWgType(oldGoods.getWgType());
		ylgs.setGoodsStoreProMarkId(oldOrder.getRootMarkId());// 总成件号
		ylgs.setGoodsStoreWarehouse("余料库");// 库别
		Users jingban = Util.getLoginUser();
		ylgs.setGoodsStoreCharger(jingban.getName());// 经办人
		ylgs.setStyle("余料");// 入库类型
		if (user != null) {// 负责人
			ylgs.setGoodsStorePerson(user.getName());
		} else {
			// ylgs.setGoodsStorePerson(procard.getLingliaoren());
		}
		ylgs.setGoodsStoreDate(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		ylgs.setGoodsStoreUnit(oldGoods.getGoodsUnit());// 单位
		ylgs.setKgliao(oldGoods.getKgliao());
		totalDao.save(ylgs);
	}

	/**
	 * 供应商领料 添加出库记录
	 * 
	 * @param order
	 * @param oldOrder
	 * @param user
	 * @param goods
	 * @param oldGoods
	 * @param zzpSell
	 */
	private void setSellZZ(WaigouOrder order, WaigouOrder oldOrder, Users user,
			Goods goods, Goods oldGoods, Sell zzpSell, String time) {
		zzpSell.setBanBenNumber(oldGoods.getBanBenNumber());
		zzpSell.setProcessNo(oldGoods.getProcessNo());
		zzpSell.setProcessName(oldGoods.getProcessName());
		zzpSell.setSellSupplier(oldGoods.getGoodsSupplier());
		zzpSell.setSellFormat(oldGoods.getGoodsFormat());
		zzpSell.setSellLot(oldGoods.getGoodsLotId());
		zzpSell.setSellMarkId(oldGoods.getGoodsMarkId());
		zzpSell.setSellAdminName(user.getName());
		zzpSell.setSellGoods(oldGoods.getGoodsFullName());
		zzpSell.setSellDate(Util.getDateTime("yyyy-MM-dd"));
		zzpSell.setSellTime(time);
		zzpSell.setSellWarehouse(oldGoods.getGoodsClass());
		zzpSell.setGoodHouseName(oldGoods.getGoodHouseName());// 仓区
		zzpSell.setKuwei(oldGoods.getGoodsPosition());// 库位;
		zzpSell.setSellUnit(oldGoods.getGoodsUnit());
		zzpSell.setSellCount(goods.getGoodsCurQuantity());
		zzpSell.setSellZhishu(goods.getGoodsZhishu());
		zzpSell.setWgType(oldGoods.getWgType());
		zzpSell.setWgOrderId(order.getId());
		zzpSell.setWgOrdernumber(oldOrder.getPlanNumber());
		zzpSell.setProcessNo(oldGoods.getProcessNo());
		zzpSell.setProcessName(oldGoods.getProcessName());
		zzpSell.setStyle("供应商领料");
		zzpSell.setKgliao(oldGoods.getKgliao());
		zzpSell.setSellPrice(oldGoods.getGoodsBuyPrice());// 批次单价(含税)
		zzpSell.setSellbhsPrice(oldGoods.getGoodsBuyBhsPrice());// 批次单价(不含税)
		zzpSell.setSellCharger(Util.getLoginUser().getName());
		if (zzpSell.getSellPrice() != null) {
			zzpSell.setMoney(Util.FomartFloat(zzpSell.getSellPrice()
					* zzpSell.getSellCount(), 2));// 金额（含税）
		}
		// String printNumber = updatMaxSellPrintNumber(zzpSell,time);
		// zzpSell.setPrintNumber(printNumber);
		zzpSell.setGoodsId(oldGoods.getGoodsId());
		zzpSell.setProcardId(goods.getOrder_Id());
		totalDao.save(zzpSell);
	}

	@Override
	public Object[] addPrintedOutOrder(List<Sell> sellList, String status) {
		if (sellList != null && sellList.size() > 0) {
			Sell s = sellList.get(0);
			String plannumber = "";
			PrintedOutOrder printedOutOrder = new PrintedOutOrder();
			if (s.getPrintNumber() == null || s.getPrintNumber().length() == 0) {
				String str = status;
				plannumber = "000001";
				String hql_maxnum = "select max(planNum) from PrintedOutOrder where planNum  like '%"
						+ status + "%'";
				String maxnum = (String) totalDao
						.getObjectByCondition(hql_maxnum);
				if (maxnum == null || maxnum.length() == 0) {
					plannumber = str + plannumber;
				} else {
					String number = (1000001 + Integer.parseInt(maxnum
							.substring(status.length())))
							+ "";
					plannumber = str + number.substring(1);
				}
			} else {
				plannumber = s.getPrintNumber();
			}
			if ("XOUT".equals(status)) {
				printedOutOrder.setType("销售出库单");
			} else if ("SOUT".equals(status)) {
				printedOutOrder.setType("生产领料单");
				printedOutOrder.setReviewers(s.getSellCharger());
			} else if ("QOUT".equals(status)) {
				printedOutOrder.setType("其他出库单");
			}
			printedOutOrder.setPlanNum(plannumber);
			printedOutOrder.setAddTime(Util.getDateTime());
			printedOutOrder.setKehuNmae(s.getSellCompanyName());
			printedOutOrder.setClassName("Sell");
			printedOutOrder.setRiqi(s.getSellDate());
			printedOutOrder.setLldept(s.getSellchardept());
			printedOutOrder.setLlyt(s.getSellLuId());
			printedOutOrder.setAddUsers(Util.getLoginUser().getName());
			printedOutOrder.setShPlanNum(s.getSellSendnum());
			Set<PrintedOut> printedOutSet = new HashSet<PrintedOut>();
			Map<String, PrintedOut> map0 = null;
			if ("SOUT".equals(status)) {
				map0 = new HashMap<String, PrintedOut>();
			}
			List<PrintedOut> list = new ArrayList<PrintedOut>();

			for (Sell sell : sellList) {
				if ("YES".equals(sell.getPrintStatus())) {
					continue;
				}
				if (map0 != null) {
					PrintedOut printedOut = map0.get(sell.getSellMarkId() + "_"
							+ sell.getOrderNum() + "_" + sell.getSellCharger()
							+ "_" + sell.getGoodHouseName()+"_"+sell.getBanBenNumber());
					if (printedOut != null) {
						printedOut.setNum(sell.getSellCount()
								+ printedOut.getNum());
						printedOut.setEntiyIds(printedOut.getEntiyIds() + ";"
								+ sell.getSellId());
						map0.put(sell.getSellMarkId() + "_"
								+ sell.getOrderNum() + "_" + sell.getSellDate()
								+ "_" + sell.getGoodHouseName()+"_"+sell.getBanBenNumber(), printedOut);
					} else {
						printedOut = new PrintedOut();
						printedOut.setClassName("Sell");
						printedOut.setYwmarkId(sell.getYwmarkId());
						printedOut.setEntiyIds(sell.getSellId() + "");
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
						printedOut.setBanbenNum(sell.getBanBenNumber());
						printedOut.setSuodingdanhao(sell.getSuodingdanhao());
						printedOutSet.add(printedOut);
						list.add(printedOut);
						map0.put(sell.getSellMarkId() + "_"
								+ sell.getOrderNum() + "_" + sell.getSellCharger()
								+ "_" + sell.getGoodHouseName()+"_"+sell.getBanBenNumber(), printedOut);
					}

				} else {
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
					printedOut.setSuodingdanhao(sell.getSuodingdanhao());
					printedOutSet.add(printedOut);
					list.add(printedOut);
				}
			}
			printedOutOrder.setPrintedOutSet(printedOutSet);
			return new Object[] { printedOutOrder, list };
		}
		return null;
	}

	@Override
	public List getAlllingliaodept() {
		// TODO Auto-generated method stub
		return totalDao.query("select distinct sellchardept from Sell");
	}

	@Override
	public String tuiKuSell(Sell sell) {
		// TODO Auto-generated method stub
		if (sell != null) {
			if (sell.getSellCount() == null
					|| (sell.getSellCount() != null && sell.getSellCount() <= 0)) {
				return "数量异常，退库失败！";
			}
			Sell sell2 = getOneSellById(sell.getSellId(), "");
			if (sell2 != null) {
				/*** 开始入库 **/
				GoodsStore goodsStore = ProcardBlServerImpl.addGoodsStore(
						sell2, sell.getStyle(), sell.getSellCount());
				/*** 添加入库记录、库存记录 ****/
				Goods goodss = GoodsStoreServerImpl
						.save_2(goodsStore, totalDao);
				if (goodss != null) {
					sell2.setTksellCount(sell2.getTksellCount() == null ? sell
							.getSellCount() : sell2.getTksellCount()
							+ sell.getSellCount());
					totalDao.update(sell2);
					return "true";
				}
			} else {
				return "出库记录为空，退库失败！";
			}
		}
		return "出库记录为空，退库失败！";
	}

	@Override
	public List<Sell> getSellList() {
		// TODO Auto-generated method stub
		return totalDao
				.query("from Sell where sellWarehouse='外协库' and sellTime BETWEEN '2018-04-29 08:54:57' and  '2018-04-29 09:01:58' ");
	}

	@Override
	public String backwxSell(Sell sell) {
		// TODO Auto-generated method stub
		Sell oldSell = (Sell) totalDao.getObjectById(Sell.class, sell
				.getSellId());
		try {
			Goods goods = (Goods) totalDao.getObjectById(Goods.class, sell
					.getGoodsId());
			if (goods != null) {
				Float sellCount = oldSell.getSellCount();
				Procard procard = (Procard) totalDao
						.getObjectByCondition(
								"from Procard where id in(select procardId from ProcardProductRelation where goodsId=? and ckCount is not null and ckCount >=? ) order by selfCard desc",
								goods.getGoodsId(), sellCount);
				if (procard == null) {
					return oldSell.getSellMarkId() + "第" + oldSell.getSellLot()
							+ "批产(" + oldSell.getSellTime()
							+ ")没有找到做够数量的对应关系生产件(" + oldSell.getSellId() + ")";
				}
				procard
						.setZaizhizkCount(procard.getZaizhizkCount()
								+ sellCount);
				ProcardProductRelation pr = (ProcardProductRelation) totalDao
						.getObjectByCondition(
								"from ProcardProductRelation where procardId=? and  goodsId=? and ckCount is not null and ckCount >=?",
								procard.getId(), goods.getGoodsId(), sellCount);
				pr.setCkCount(pr.getCkCount() - sellCount);
				goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
						+ sellCount);
				totalDao.update(procard);
				totalDao.update(pr);
				totalDao.update(goods);
				totalDao.delete(oldSell);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(oldSell.getSellMarkId() + "第"
					+ oldSell.getSellLot() + "批产(" + oldSell.getSellTime()
					+ ")关联数据异常(" + oldSell.getSellId() + "");
		}

		return "";
	}

	@Override
	public Map<Integer, Object> findChangeGoods(String tag, Integer pageNo,
			Integer pageSize, Sell sellSelect) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		String select = "select new com.task.entity.Sell(s.sellId, s.sellWarehouse, s.goodHouseName,"
				+ "s.kuwei,s.sellMarkId, s.banBenNumber,s.kgliao,s.sellGoods,s.sellFormat,"
				+ "s.sellCount,s.sellUnit,s.sellMore,s.sellTime,s.style,"
				+ "s.changePrint,g.goodsStoreWarehouse,g.goodHouseName,g.goodsStorePosition) ";

		List<Sell> unList = totalDao
				.query(select
						+ "from Sell s,GoodsStore g where g.sellId=s.sellId and"
						+ " s.changePrint is null and (s.style='转仓出库' or s.style='调仓出库') order by s.sellId desc");
		map.put(1, unList);// 调出未打印
		if (sellSelect == null) {
			sellSelect = new Sell();
		}
		StringBuffer hql = new StringBuffer(
				" from Sell s,GoodsStore g where g.sellId=s.sellId ");
		if (sellSelect.getSellMarkId() != null
				&& !sellSelect.getSellMarkId().equals("")) {
			hql.append(" and s.sellMarkId like '%" + sellSelect.getSellMarkId()
					+ "%'");
		}
		if (sellSelect.getSellGoods() != null
				&& !sellSelect.getSellGoods().equals("")) {
			hql.append(" and s.sellGoods like '%" + sellSelect.getSellGoods()
					+ "%' ");
		}
		if (sellSelect.getStyle() != null && !sellSelect.getStyle().equals("")) {
			hql.append(" and s.style = '" + sellSelect.getStyle() + "'");
		}
		if (sellSelect.getChangePrint() != null
				&& !sellSelect.getChangePrint().equals("")) {
			hql.append(" and s.changePrint like '%"
					+ sellSelect.getChangePrint() + "%'");
		}
		hql
				.append(" and s.changePrint is not null and s.changePrint is not null"
						+ " and (s.style='转仓出库' or s.style='调仓出库') order by s.sellId desc");
		Integer count = totalDao.getCount(hql.toString());
		List pageList = totalDao.findAllByPage(select + hql.toString(), pageNo,
				pageSize);
		map.put(2, pageList);
		map.put(3, count);
		return map;
	}

	@Override
	public String deleteYcSell(List<Sell> sellList) {
		// TODO Auto-generated method stub
		if (sellList != null && sellList.size() > 0) {
			String markId = "";
			String selfCard = "";
			String orderNumber = "";
			Float backCount = 0f;
			;
			for (Sell sell : sellList) {
				if (markId.length() == 0) {
					markId = sell.getSellMarkId();
					selfCard = sell.getSellArtsCard();
					orderNumber = sell.getOrderNum();
				}
				if (!markId.equals(sell.getSellMarkId())) {
					throw new RuntimeException(markId + "与"
							+ sell.getSellMarkId() + "件号不同逻辑出现问题");
				}
				if (!selfCard.equals(sell.getSellArtsCard())) {
					selfCard = sell.getSellArtsCard();
					throw new RuntimeException(markId + "的批次" + selfCard + "与"
							+ sell.getSellArtsCard() + "不同逻辑出现问题");
				}
				if (!orderNumber.equals(sell.getOrderNum())) {
					throw new RuntimeException(markId + "的订单号" + orderNumber
							+ "与" + sell.getOrderNum() + "不同逻辑出现问题");
				}
				SellDelete sd = new SellDelete();
				BeanUtils.copyProperties(sell, sd, new String[] { "id" });
				Goods goods = (Goods) totalDao.getObjectById(Goods.class, sell
						.getGoodsId());
				Float nowcount = Util.Floatadd(goods.getGoodsCurQuantity(),
						sell.getSellCount());
				backCount = Util.Floatadd(backCount, sell.getSellCount());
				goods.setGoodsCurQuantity(nowcount);
				totalDao.update(goods);
				totalDao.save(sd);
				totalDao.delete(sell);
			}
			List<Procard> procardList = totalDao
					.query(
							"from  Procard where markId=? and selfCard=? and orderNumber=? and klNumber is not null and hascount is not null "
									+ " and  (wwblCount is null or wwblCount =0)",
							markId, selfCard, orderNumber);
			if (procardList != null && procardList.size() > 0) {
				for (Procard procard : procardList) {
					Float singlebackcount = procard.getKlNumber()
							- procard.getHascount();
					if (singlebackcount > 0) {
						if (backCount >= singlebackcount) {
							backCount = Util.Floatdelete(backCount,
									singlebackcount);
						} else {
							singlebackcount = backCount;
							backCount = 0f;
						}
						procard.setHascount(Util.Floatadd(singlebackcount,
								procard.getHascount()));
						List<ProcardBl> procardblList = totalDao.query(
								"from ProcardBl where procardId=?", procard
										.getId());
						if (procardblList != null && procardblList.size() > 0) {
							for (ProcardBl pbl : procardblList) {
								if (pbl.getYlCount() == null) {
									pbl.setYlCount(0f);
								}
								Float blbackcount = pbl.getYlCount();
								if (blbackcount > 0) {
									if (singlebackcount >= blbackcount) {
										singlebackcount = Util.Floatdelete(
												singlebackcount, blbackcount);
									} else {
										blbackcount = singlebackcount;
										blbackcount = 0f;
									}
									pbl.setYlCount(Util.Floatdelete(pbl
											.getYlCount(), blbackcount));
									pbl.setStatus("未领完");
									totalDao.update(pbl);
								}
								if (blbackcount == 0) {
									break;
								}
							}
						}
						if (backCount == 0) {
							break;
						}

					}
				}
			}

			return markId + "  " + selfCard + "  " + orderNumber + "  退库完成;";
		}
		return "";
	}

	@Override
	public List<Sell> getycSellList() {
		// TODO Auto-generated method stub
		List<Sell> sellList = totalDao
				.query("from Sell where sellDate<='2018-06-25' and sellDate>='2018-06-23' and sellCharger='熊清云' "
						+ " and sellWarehouse ='外购件库' and style='生产刷卡(多)'  and goodHouseName!='板材仓'  and id not in(select sellId from SellDelete)"
						+
						// " and sellMarkId in('03022JSTCS','04070037CS','04080143CS')"+
						"order by sellMarkId,sellArtsCard,orderNum ");
		return sellList;
	}

	@Override
	public List<Sell> findchangeGoodsBySellIds(List<Integer> sellIds, String tag) {
		if (sellIds != null && sellIds.size() > 0) {
			String select = "select new com.task.entity.Sell(s.sellId, s.sellWarehouse, s.goodHouseName,"
					+ "s.kuwei,s.sellMarkId, s.banBenNumber,s.kgliao,s.sellGoods,s.sellFormat,"
					+ "s.sellCount,s.sellUnit,s.sellMore,s.sellTime,s.style,"
					+ "s.changePrint,g.goodsStoreWarehouse,g.goodHouseName,g.goodsStorePosition,s.ywmarkId) ";
			StringBuffer buffer = new StringBuffer();
			for (Integer integer : sellIds) {
				if (buffer.toString().equals("")) {
					buffer.append(integer);
				} else {
					buffer.append("," + integer);
				}
			}
			String hql = select
					+ " from Sell s,GoodsStore g where g.sellId=s.sellId "
					+ "and sellId in (" + buffer.toString()
					+ ") order by s.sellId desc";
			List<Sell> list = totalDao.query(hql);
			return list;
		}

		return null;
	}

	@Override
	public String updateChangeSellPrintStatus(String selected, String tag,
			PrintedOutOrder poor) {
		if (selected != null && selected.length() > 0 && poor != null) {
			PrintedOutOrder order = (PrintedOutOrder) totalDao
					.getObjectByCondition(
							"from PrintedOutOrder where planNum = ?", poor
									.getPlanNum());
			if (order == null) {
				String[] sellIds = selected.split(",");

				poor.setAddTime(Util.getDateTime());
				poor.setType("调拨出库单");
				poor.setClassName("Sell");

				totalDao.save(poor);

				Sell sell = null;
				boolean update = false;
				for (String sellId : sellIds) {
					sell = (Sell) totalDao.getObjectById(Sell.class, Integer
							.parseInt(sellId));
					sell.setChangePrint(poor.getPlanNum());
					update = totalDao.update(sell);
					if (!update) {
						return "更新记录失败";
					}
				}
				return "" + update;
			} else {
				if (order.getPrintcount() == null) {
					order.setPrintcount(1);
				} else {
					order.setPrintcount(order.getPrintcount() + 1);
				}
				return totalDao.update(order) + "";
			}

		}
		return "参数错误";
	}

	@Override
	public PrintedOutOrder getPOOByCond(String selected, String changeNum) {
		PrintedOutOrder order = null;
		if (changeNum != null && !changeNum.equals("")) {
			order = (PrintedOutOrder) totalDao.getObjectByCondition(
					"from PrintedOutOrder where planNum = ?", changeNum);
		} else {
			String prefix = "DBOUT";
			String printNumber = "000001";
			String maxNumber = (String) totalDao
					.getObjectByCondition("select max(planNum) "
							+ "from PrintedOutOrder where planNum like '"
							+ prefix + "%'");
			order = new PrintedOutOrder();
			if (maxNumber == null) {
				order.setPlanNum(prefix + printNumber);
			} else {
				String number = (1000001 + Integer.parseInt(maxNumber
						.substring(prefix.length())))
						+ "";
				printNumber = prefix + number.substring(1);
				order.setPlanNum(printNumber);
			}
			order.setRiqi(Util.getDateTime("yyyy-MM-dd"));
		}

		return order;
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

	@Override
	public String ChengPinTuiHuoSq(Sell sell) {
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!!~";
		}
		if (sell != null) {
			Sell oldSell = (Sell) totalDao.get(Sell.class, sell.getSellId());
			Float sqtuNum = sell.getTksellCount();
			if (oldSell.getTksellCount() == null) {
				oldSell.setTksellCount(sqtuNum);
			} else {
				oldSell.setTksellCount(sqtuNum + oldSell.getTksellCount());
			}
			if (oldSell.getTksellCount() > oldSell.getSellCount()) {
				return "累计申请退货数量:" + oldSell.getTksellCount() + ">出库数量:"
						+ oldSell.getSellCount() + "，请重新申请。";
			}
			Integer orderId = (Integer) totalDao.getObjectByCondition(
					"select id  from OrderManager where orderNum =?", oldSell
							.getOrderNum());
			if (orderId == null) {
				return "未查询到相关订单的信息!~";
			}
			Integer productId = (Integer) totalDao
					.getObjectByCondition(
							"select id from  ProductManager where orderManager.id =? and pieceNumber =? ",
							orderId, oldSell.getSellMarkId());
			if (productId == null) {
				return "未查询到相关订单产品的信息!~";
			}
			ChengPinTuiHuo cptu = new ChengPinTuiHuo(sell.getSellId(), orderId,
					productId, oldSell.getSellMarkId(), oldSell.getSellLot(),
					oldSell.getOrderNum(), oldSell.getSellWarehouse(), oldSell
							.getGoodHouseName(), oldSell.getKuwei(), sqtuNum,
					users.getName(), Util.getDateTime());
			cptu.setProname(oldSell.getSellGoods());
			cptu.setSqUserCode(users.getCode());
			cptu.setSqUsersId(users.getId());
			cptu.setYwmarkId(oldSell.getYwmarkId());
			cptu.setDept(users.getDept());
			cptu.setShPlanNum(oldSell.getSellSendnum());
			totalDao.save(cptu);
			String processName = "成品退货申请";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						ChengPinTuiHuo.class, cptu.getId(), "epStatus", "id",
						"", users.getDept() + "部门 " + users.getName()
								+ "成品退货申请，请您审批", true);
				if (epId != null && epId > 0) {
					cptu.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						cptu.setEpStatus("同意");
					} else {
						cptu.setEpStatus("未审批");
					}

					totalDao.update(oldSell);
					return totalDao.update(cptu) + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Object[] findAllChengPinTuiHuo(ChengPinTuiHuo cpth,
			Integer pageSize, Integer pageNo, String status,String startDate,String endDate) {
		if (cpth == null) {
			cpth = new ChengPinTuiHuo();
		}
		String hql = totalDao.criteriaQueries(cpth, null);
		if (startDate != null && !"".equals(startDate)) {
			hql += " and addTime >= '" + startDate + "' ";
		}

		if (endDate != null && !"".equals(endDate)) {
			hql += " and addTime <= '" + endDate + "'";
		}
		List<ChengPinTuiHuo> cpthList = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { cpthList, count };
	}
	public void exprotcpth(ChengPinTuiHuo cpth, String status,String startDate,String endDate){
		if (cpth == null) {
			cpth = new ChengPinTuiHuo();
		}
		String hql = totalDao.criteriaQueries(cpth, null);
		if (startDate != null && !"".equals(startDate)) {
			hql += " and addTime >= '" + startDate + "' ";
		}

		if (endDate != null && !"".equals(endDate)) {
			hql += " and addTime <= '" + endDate + "'";
		}
		List<ChengPinTuiHuo> cpthList = totalDao.find(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition",
					"attachment; filename="
							+ new String("成品退货记录".getBytes("GB2312"),
									"8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("成品退货记录", 0);
			ws.setColumnView(1, 12);
			ws.setColumnView(2, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(4, 20);
			ws.setColumnView(13, 15);
			ws.setColumnView(14, 25);
			NumberFormat fivedps1 = new NumberFormat("0.0000");
			WritableCellFormat fivedpsFormat1 = new WritableCellFormat(
					fivedps1);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 14,
					WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcf.setAlignment(Alignment.CENTRE);
			jxl.write.Label label0 = new Label(0, 0, "成品退货记录", wcf);
			ws.addCell(label0);
			ws.mergeCells(0, 0, 19, 0);
			wf = new WritableFont(WritableFont.ARIAL, 12,
					WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.CENTRE);
			ws.addCell(new jxl.write.Label(0, 1, "序号", wc));
			ws.addCell(new jxl.write.Label(1, 1, "订单号", wc));
			ws.addCell(new jxl.write.Label(2, 1, "件号", wc));
			ws.addCell(new jxl.write.Label(3, 1, "业务件号", wc));
			ws.addCell(new jxl.write.Label(4, 1, "批次", wc));
			ws.addCell(new jxl.write.Label(5, 1, "名称", wc));
			ws.addCell(new jxl.write.Label(6, 1, "库别", wc));
			ws.addCell(new jxl.write.Label(7, 1, "仓区", wc));
			ws.addCell(new jxl.write.Label(8, 1, "库位", wc));
			ws.addCell(new jxl.write.Label(9, 1, "申请数量", wc));
			ws.addCell(new jxl.write.Label(10, 1, "申请人", wc));
			ws.addCell(new jxl.write.Label(11, 1, "申请时间", wc));
			for (int i = 0; i < cpthList.size(); i++) {
				ChengPinTuiHuo se = (ChengPinTuiHuo) cpthList.get(i);
				ws.addCell(new jxl.write.Number(0, i + 2, i + 1, wc));
				ws.addCell(new Label(1, i + 2, se.getOrderNo(), wc));
				ws.addCell(new Label(2, i + 2, se.getMarkId(), wc));
				ws.addCell(new Label(3, i + 2, se.getYwmarkId(), wc));
				ws.addCell(new Label(4, i + 2, se.getSelfCard(), wc));
				ws.addCell(new Label(5, i + 2, se.getProname(), wc));
				ws.addCell(new Label(6, i + 2, se.getKubie(), wc));
				ws.addCell(new Label(7, i + 2, se.getCangqu(), wc));
				if(se.getKuwei()!=null){
					ws.addCell(new Label(8, i + 2, se.getKuwei(), wc));
				}else{
					ws.addCell(new Label(8, i + 2, "", wc));
				}
				
				ws.addCell(new Label(9, i + 2, se.getSqNum()+"", wc));
				ws.addCell(new Label(10, i + 2, se.getSqUsers(), wc));
				ws.addCell(new Label(10, i + 2, se.getAddTime(), wc));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ChengPinTuiHuo toCpthPrint(Integer id) {
		if (id != null) {
			ChengPinTuiHuo cpth = (ChengPinTuiHuo) totalDao.get(
					ChengPinTuiHuo.class, id);
			Sell sell = (Sell) totalDao.get(Sell.class, cpth.getSellId());
			cpth.setSell(sell);
			return cpth;
		}
		return null;
	}
}
