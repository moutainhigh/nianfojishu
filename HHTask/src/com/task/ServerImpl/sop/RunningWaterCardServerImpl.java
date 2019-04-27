package com.task.ServerImpl.sop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.task.Dao.TotalDao;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.RunningWaterCardServer;
import com.task.entity.GoodsStore;
import com.task.entity.OrderManager;
import com.task.entity.ProductManager;
import com.task.entity.Users;
import com.task.entity.WarehouseNumber;
import com.task.entity.menjin.MarkStatusType;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.sop.Procard;
import com.task.entity.sop.RunningWaterCard;
import com.task.entity.sop.qd.LogoStickers;
import com.task.util.Util;
import com.task.util.UtilTong;

/**
 * 流水卡片操作
 * 
 * @author jhh
 * 
 */
public class RunningWaterCardServerImpl implements RunningWaterCardServer {
	private static Integer duankou = 8885;
	private TotalDao totalDao;
	private ProcardServer procardServer;

	public Users getNameByCard(String card) {
		String hql1 = "from Users where cardId=?";
		Users us = (Users) totalDao.getObjectByCondition(hql1, card);
		return us;
	}

	/** * 查询流水卡片信息 */
	public Object[] findRunningWaterCard(RunningWaterCard runningWaterCard,
			String startDate, String endDate, Integer cpage, Integer PageSize,
			String tag) {
		String hql = "from RunningWaterCard order by followCardId desc";
		String[] between = new String[2];
		if (null != startDate && null != endDate && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}

		if ("inApply".equals(tag)) {
			if (null != runningWaterCard.getCardNum()) {// 有指定卡查询
				hql = " from RunningWaterCard r where cardStatus='完成' and cardNum='"
						+ runningWaterCard.getCardNum()
						+ "' and followCardId in(select distinct(jcpc) from InsRecord i where i.root.id in(select distinct(id) from InsTemplate t where "
						+ "t.partNumber=r.markId)) and followCardId in (select distinct(lotId) from LogoStickers l where l.markId=r.markId ) order by"
						+ " followCardId desc";
			} else {// 无指定卡查询
				hql = " from RunningWaterCard r where cardStatus='完成' and followCardId in(select distinct(jcpc) from InsRecord i where i.root.id in"
						+ "(select distinct(id) from InsTemplate t where t.partNumber=r.markId))  and followCardId in (select distinct(lotId) from LogoStickers"
						+ " l where l.markId=r.markId ) order by followCardId desc";
			}

		} else {
			if (null != runningWaterCard) {
				hql = totalDao.criteriaQueries(runningWaterCard,
						"createCardTime", between, "")
						+ " order by markId desc";
			}
		}
		Object[] procardAarr = new Object[2];
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		Integer count = totalDao.getCount(hql);
		procardAarr[0] = count;
		procardAarr[1] = list;
		return procardAarr;
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public RunningWaterCard getCard(Integer id) {
		return (RunningWaterCard) totalDao.getObjectById(
				RunningWaterCard.class, id);
	}

	/*** 制作生产周转卡 ***/
	@Override
	public boolean save(RunningWaterCard runningWaterCard) {
		if (null != runningWaterCard) {
			String cardNum = runningWaterCard.getCardNum();
			String hql = "from RunningWaterCard where cardNum='" + cardNum
					+ "'";
			if (totalDao.find(hql).size() > 0) {
				return false;
			} else {
				Users user = (Users) Util.getLoginUser();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				runningWaterCard.setCreator(user.getName());
				runningWaterCard.setCreateCardTime(sdf.format(new Date()));
				runningWaterCard.setCardStatus("初始");
				runningWaterCard.setReceiveStatus("初始");
				// 判断制卡类型
				if ("周转单".equals(runningWaterCard.getRwStyle())) {
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyMM");
					String subStart = sdf2.format(new Date());
					String hqlMax = "select max(cardNum) from RunningWaterCard where cardNum like'"
							+ subStart + "%'";
					List listMax = totalDao.find(hqlMax);
					String maxCardNum = "";
					if (listMax != null && listMax.size() > 0
							&& null != listMax.get(0)) {
						String maxdb = (String) listMax.get(0);
						Integer intHao = Integer.parseInt(maxdb.substring(4));
						intHao++;
						String strHao = intHao.toString();
						while (strHao.length() < 4) {
							strHao = "0" + strHao;
						}
						maxCardNum = subStart + strHao;
					} else {
						maxCardNum = subStart + "0001";
					}
					runningWaterCard.setCardNum(maxCardNum);
				}
				return totalDao.save(runningWaterCard);
			}
		}
		return false;
	}

	/*** 根据属性查找生产周转卡属性下拉列表 ***/
	@Override
	public String findCardList(String tag) {
		String message = "";
		if (null != tag && !"".equals(tag)) {
			String hql = "select distinct(" + tag + ") from RunningWaterCard";
			List<String> list = totalDao.query(hql);
			for (String d : list) {
				message += d.toString() + "|";
			}
		}
		return message;

	}

	public String findCardByCard(String tag) {
		String message = "true";
		if (null != tag && !"".equals(tag)) {
			String hql = "from RunningWaterCard where cardNum='" + tag + "'";
			if (totalDao.find(hql).size() > 0) {
				message = "false";
			}
		}
		return message;
	}

	/**
	 * 根据补料单获取流水卡片对象
	 * 
	 * @param barcode
	 * @param tag
	 * @return
	 */
	public Procard getProcardBySticker(String barcode, String tag) {
		String hql = " from LogoStickers where number='" + barcode + "'";
		List list = totalDao.find(hql);
		if (list.size() > 0 && null != list) {
			LogoStickers ls = (LogoStickers) list.get(0);
			return (Procard) totalDao.getObjectById(Procard.class, ls
					.getProcardId());
		}
		return null;
	}

	/*** 根据生产周转卡查找工艺流水单 ***/
	public Procard getProcard(RunningWaterCard runningWaterCard, String tag) {
		Procard procard = new Procard();
		if (null != runningWaterCard) {

			procard = (Procard) totalDao.getObjectById(Procard.class,
					runningWaterCard.getProcardId());
			if ("out".equals(tag)) {
				if (!"已发卡".equals(procard.getStatus())) {
					procard = new Procard();
				}
			} else if ("in".equals(tag)) {
				if (!"完成".equals(procard.getStatus())) {
					procard = new Procard();
				}
			}
		}
		return procard;
	}

	/*** 根据ID查找工艺流水单 ***/
	public Procard getProcardById(Integer id) {
		Procard p = (Procard) totalDao.getObjectById(Procard.class, id);
		if (p != null) {
			String yemarkid = (String) totalDao.getObjectByCondition(
					"select ywMarkId from Procard where id=?", p.getRootId());
			if (p.getOrderId() != null) {
				OrderManager order = (OrderManager) totalDao.get(
						OrderManager.class, Integer.parseInt(p.getOrderId()));
				if (order != null) {
					p.setGys(order.getClientName());
				}
			}
			p.setYwMarkId(yemarkid);
		}
		return p;
	}

	/***
	 * 查询是否是最小批次
	 * 
	 * @param procard
	 *            流水单
	 * @return
	 */
	@Override
	public String findMinSelfCard(Procard procard) {
		if (procard != null) {
			// 判断是否是最小批次
			String hql = null;
			if (procard.getProcardStyle() != null
					&& procard.getProcardStyle().equals("外购")
					&& procard.getNeedProcess() != null
					&& procard.getNeedProcess().equals("yes")) {
				hql = "select selfCard from Procard where jihuoStatua='激活' and productStyle=? and markId=? and status in ('初始','已发卡','已发料','领工序') and"
						+ " (hascount>0 or hascount is null) and needProcess='yes'  and rootId in "
						+ "(select id from Procard where status not in('完成','入库') and markId in "
						+ "(select markId from Procard where rootId=?)) order by selfCard";
			} else {
				hql = "select selfCard from Procard fp where jihuoStatua='激活' and productStyle=? and markId=? and lingliaoType!='part'"
						+ "and status in ('初始','已发卡','已发料','领工序') "
						+ "and (hascount>0 or hascount is null or "
						+ "(procardStyle in('自制','总成') and lingliaoType is not null and lingliaoType!='part' and hascount=0 and id in (select fatherId from Procard where fatherId=fp.id and  procardStyle='外购' and (hascount is null or hascount>0) )"
						+ ")"
						+ ")"
						+ " and rootId in "
						+ "(select id from Procard where status not in('完成','入库') and markId in "
						+ "(select markId from Procard where rootId=?)) order by selfCard";
			}
			Object obj = totalDao.getObjectByCondition(hql, procard
					.getProductStyle(), procard.getMarkId(), procard
					.getRootId());
			if (obj != null) {
				String selfCard = (String) obj;
				if (!selfCard.equals(procard.getSelfCard())) {
					return "请先领取该件号(" + procard.getMarkId() + ")的" + selfCard
							+ "批次";
				}
			}
			return "true";
		}
		return "数据异常!请检查!";
	}

	@Override
	public String checkZaizhi(Float rkCount, Procard pro) {
		// TODO Auto-generated method stub
		// 判断自制件的在制品数量是否足够
		String msg = null;
		List<Procard> zizhiList = totalDao
				.query(
						"from Procard where (procardStyle='自制' or procardStyle='外购' or danjiaojian='单交件') and rootId=?",
						pro.getRootId());
		if (zizhiList.size() > 0) {
			for (Procard zzProcard : zizhiList) {
				if (zzProcard.getProcard() != null
						&& (zzProcard.getProcard().getLingliaostatus() != null && zzProcard
								.getProcard().getLingliaostatus().equals("否"))) {
					break;// 上层不领料
				}
				if (zzProcard.getProcardStyle().equals("自制")
						&& zzProcard.getLingliaostatus() != null
						&& zzProcard.getLingliaostatus().equals("否")) {
					break;// 自制件不领料
				}
				if (zzProcard.getDanjiaojian() != null
						&& zzProcard.getDanjiaojian().equals("单交件")
						&& zzProcard.getLingliaostatus() != null
						&& zzProcard.getLingliaostatus().equals("否")) {
					break;// 单交件不领料
				}
				List<Float> zaizhiList = totalDao
						.query(
								"select goodsCurQuantity from Goods where goodsMarkId=? and goodsClass='在制品' and goodsCurQuantity>0",
								zzProcard.getMarkId());
				if (zaizhiList.size() > 0) {
					Float filnalCount = null;
					filnalCount = rkCount / pro.getFilnalCount()
							* zzProcard.getFilnalCount();// 此次申请入库所需要的在制品数量
					if (pro.getRukuCount() != null) {
						List<GoodsStore> goodsStoreList = totalDao
								.query(
										"from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and status='待入库'",
										pro.getMarkId(), pro.getSelfCard());
						if (goodsStoreList.size() > 0) {
							for (GoodsStore gs : goodsStoreList) {
								filnalCount += gs.getGoodsStoreCount()
										/ pro.getFilnalCount()
										* zzProcard.getFilnalCount();// 已申请入库但没有入库所占的在制品数量
							}
						}
					}
					for (Float zaizhiCount : zaizhiList) {
						if (zzProcard.getFilnalCount() == null
								|| rkCount == null
								|| pro.getFilnalCount() == null) {
							if (msg == null) {
								msg = "对不起,";
							}
							msg += zzProcard.getMarkId() + "数量异常.";
						} else {

						}
						if (filnalCount == null) {
							if (msg == null) {
								msg = "对不起,";
							}
							msg += zzProcard.getMarkId() + "数量异常.";
						}
						if (zaizhiCount != null && filnalCount > zaizhiCount) {
							filnalCount = filnalCount - zaizhiCount;
						} else if (zaizhiCount != null
								&& filnalCount <= zaizhiCount) {
							filnalCount = 0f;
							break;
						}
					}
					if (filnalCount > 0) {
						if (msg == null) {
							msg = "对不起,";
						}
						msg += zzProcard.getMarkId() + "在制品数量不足！缺少"
								+ filnalCount + "<br/>";
					}
				} else {
					if (msg == null) {
						msg = "对不起,";
					}
					msg += zzProcard.getMarkId() + "没有在制品" + ".";
				}
			}
		}
		if (msg == null) {
			return "true";
		} else {
			return msg;
		}
	}

	/*** 根据生产周转办理入库 ***/
	@Override
	public String saveGoodsStore(GoodsStore goodsStore, Integer id,
			Procard procard, String tag, String barCode) {
		// 入库数量大于0
		if (goodsStore.getGoodsStoreCount() == null
				|| goodsStore.getGoodsStoreCount() <= 0) {
			return "该流转卡已办理过入库！！";
		}
		// String hql = "from GoodsStore where goodsStoreMarkId='"
		// + procard.getMarkId() + "' and goodsStoreLot='"
		// + procard.getSelfCard()+ "'";
		// GoodsStore oldGoodsStore = (GoodsStore) totalDao
		// .getObjectByCondition(hql);
		Users user = (Users) Util.getLoginUser();
		// if (oldGoodsStore != null) {
		// oldGoodsStore.setGoodsStoreCount(oldGoodsStore.getGoodsStoreCount()
		// + goodsStore.getGoodsStoreCount());// 累加入库数量
		// oldGoodsStore.setApplyTime(Util.getDateTime());
		// totalDao.update(oldGoodsStore);
		//
		// } else {
		// 分批入库(多次入库信息，累计库存信息)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 保存入库记录
		// 保存入库记录
		// 入库信息
		goodsStore.setApplyTime(sdf.format(new Date()));// 入库申请日期
		goodsStore.setGoodsStoreArtsCard(procard.getBarcode());
		goodsStore.setGoodsStorePerson(user.getName());
		goodsStore.setSqUsersCode(user.getCode());// 入库申请人工号
		goodsStore.setSqUsersdept(user.getDept());// 入库申请人部门
		goodsStore.setSqUsersName(user.getName());// 入库申请人姓名
		goodsStore.setSqUsrsId(user.getId());
		goodsStore.setStatus("待入库");
		goodsStore.setStyle("刷卡入库");
		String orderType = (String) totalDao.getObjectByCondition(
				"select orderType from InternalOrder where id=?", procard
						.getPlanOrderId());
		if ("备货".equals(orderType)) {
			goodsStore.setGoodsStoreWarehouse("备货库");
		} else {
			goodsStore.setGoodsStoreWarehouse("成品库");
		}
		goodsStore.setProcardId(procard.getId());
		goodsStore.setGoodsStoreUnit(procard.getUnit());
		goodsStore.setPlanID(procard.getPlanOrderId().toString());// 内部计划id
		goodsStore.setYwmarkId(procard.getYwMarkId());// 业务件号
		goodsStore.setNeiorderId(procard.getOrderNumber());// 内部订单号
		goodsStore.setBanBenNumber(procard.getBanBenNumber());
		if (procard.getOrderId() != null && !"".equals(procard.getOrderId())) {
			OrderManager order = (OrderManager) totalDao.get(
					OrderManager.class, Integer.parseInt(procard.getOrderId()));
			goodsStore.setWaiorderId(order.getOutOrderNumber());// 外部订单号
			ProductManager product = (ProductManager) totalDao
					.getObjectByCondition(
							" from ProductManager where orderManager.id = ? and pieceNumber = ? ",
							order.getId(), procard.getMarkId());
			if (product == null) {
				product = (ProductManager) totalDao
						.getObjectByCondition(
								" from ProductManager where orderManager.id = ? and ywMarkId = ? ",
								order.getId(), procard.getYwMarkId());
				if (product != null) {
					product.setPieceNumber(procard.getMarkId());
					totalDao.update(product);
				}
			}
			goodsStore.setOrderId(order.getId() + "");
			goodsStore.setHsPrice(product.getUnit());
			goodsStore.setGoodsStorePrice(product.getBhsPrice());
			goodsStore.setTaxprice(product.getTaxprice());
			goodsStore.setMoney(goodsStore.getHsPrice()
					* goodsStore.getGoodsStoreCount());

		}

		if (null == goodsStore.getGoodsStoreFormat()) {
			goodsStore.setGoodsStoreFormat("");
		}
		if (null == goodsStore.getGoodsStoreUnit()
				|| "".equals(goodsStore.getGoodsStoreUnit())) {
			goodsStore.setGoodsStoreUnit("件");
		}

		totalDao.save(goodsStore);
		/*
		 * // 判断库存情况 String hqlgoods = "from Goods where goodsMarkId='" +
		 * goodsStore.getGoodsStoreMarkId() + "' and goodsLotId='" +
		 * goodsStore.getGoodsStoreLot() + "'"; List listGoods =
		 * totalDao.find(hqlgoods); if (listGoods.size() > 0) { Goods goods =
		 * (Goods) listGoods.get(0);
		 * goods.setGoodsCurQuantity(goods.getGoodsCurQuantity() +
		 * goodsStore.getGoodsStoreCount()); totalDao.update(goods); } else {
		 * Goods goods = new Goods();
		 * goods.setGoodsBarcode(procard.getBarcode());
		 * goods.setGoodsBeginQuantity(goodsStore.getGoodsStoreCount());
		 * goods.setGoodsMarkId(goodsStore.getGoodsStoreMarkId());
		 * goods.setGoodsCurQuantity(goodsStore.getGoodsStoreCount());
		 * goods.setGoodsCustomer(goodsStore.getGoodsStoreCompanyName());
		 * goods.setGoodsFullName(goodsStore.getGoodsStoreGoodsName());
		 * goods.setGoodsLotId(goodsStore.getGoodsStoreLot());
		 * goods.setGoodsUnit(goodsStore.getGoodsStoreUnit());
		 * goods.setGoodsChangeTime(goodsStore.getGoodsStoreDate());
		 * goods.setGoodsClass("成品库"); totalDao.save(goods);
		 * 
		 * }
		 */
		// 对象在同一方法内生成并且其属性有变动，不需要调用更新方法
		/*
		 * InternalOrder io = (InternalOrder) totalDao.getObjectById(
		 * InternalOrder.class, procard.getPlanOrderId()); if (null != io) {
		 * String hqlPlanDetail =
		 * "from InternalOrderDetail iod where pieceNumber=? and iod.internalOrder.id=?"
		 * ; InternalOrderDetail iod = (InternalOrderDetail) totalDao
		 * .getObjectByCondition(hqlPlanDetail, procard .getMarkId(),
		 * io.getId()); float stcountf = goodsStore.getGoodsStoreCount(); int
		 * stcounti = (int) stcountf; Integer sumcount =
		 * iod.getQuantityCompletion() + stcounti; if (null != iod && (sumcount)
		 * <= iod.getNum()) { iod.setQuantityCompletion(sumcount); if
		 * (iod.getNum() == iod.getQuantityCompletion()) { String hqlIo =
		 * "from InternalOrderDetail iod where num!=quantityCompletion and iod.internalOrder.id=?"
		 * ; Integer count = totalDao.getCount(hqlIo, io.getId()); if (0 ==
		 * count) { io.setStatus("完成"); } }
		 * 
		 * } }
		 */
		if (procard.getRukuCount() == null) {
			procard.setRukuCount(0F);
		}
		Float rukuCount = procard.getRukuCount()
				+ goodsStore.getGoodsStoreCount();
		procard.setRukuCount(rukuCount);
		// 全部入库完成后
		if (rukuCount.equals(procard.getFilnalCount())) {
			// 处理流水单
			procard.setStatus("待入库");
			if (!"noCard".equals(tag)) {
				// 处理流水卡
				if (id != null) {
					if ("byCardNum".equals(tag)) {
						RunningWaterCard runningWaterCard = (RunningWaterCard) totalDao
								.getObjectById(RunningWaterCard.class, id);
						runningWaterCard.setCardStatus("待入库");
						runningWaterCard.setOwnUsername(user.getName());
						totalDao.update(runningWaterCard);
					} else {
						LogoStickers sticker = (LogoStickers) totalDao
								.getObjectById(LogoStickers.class, id);
						sticker.setStatus("待入库");
						totalDao.update(sticker);
					}
				}
			}
		}
		totalDao.update(procard);
		if (barCode != null && !"".equals(barCode)) {
			// 和库位建立对应关系;
			WarehouseNumber wn = (WarehouseNumber) totalDao
					.getObjectByCondition(
							" from WarehouseNumber where barCode =?", barCode);
			if (wn != null && wn.getIp() != null && !"".equals(wn.getIp())) {
				WareBangGoogs bang = new WareBangGoogs();
				bang.setFk_ware_id(wn.getId());
				bang.setNumber(goodsStore.getGoodsStoreCount());
				bang.setFk_goodsStore_id(goodsStore.getGoodsStoreId());
				bang.setStatus("待确认");
				totalDao.save(bang);
				// 放入库位成功关门；
				String message = UtilTong.OCKuWei(wn.getIp(), duankou, false,
						wn.getOneNumber(), wn.getNumid());// 关门操作
				if ("true".equals(message)) {
					wn.setKwStatus("关");
					wn.setHave("有");
					wn.setCzUserId(null);
					updateWN(wn, "成品");
					// wn.setWarehouseArea("成品库");
					totalDao.update(wn);// 更新库位状态
				}
			}
		}
		return true + "";
	}

	/**
	 * 更改状态
	 */
	public WarehouseNumber updateWN(WarehouseNumber wn, String s) {
		// 改变库位中物品的状态
		MarkStatusType mst = (MarkStatusType) totalDao.getObjectByCondition(
				"from MarkStatusType where markTypt = ?", s);
		if (mst != null) {
			wn.setMarkTyptName(mst.getMarkTypt());
			wn.setMarkTypt(mst);
		} else {
			wn.setMarkTyptName(s);
			wn.setMarkTypt(null);
		}
		return wn;
	}

	/***
	 * 通过卡号查询卡信息
	 * 
	 * @param cardNumber
	 *            卡号
	 * @return
	 */
	@Override
	public RunningWaterCard findRunWC(String cardNumber) {
		String hql = "from RunningWaterCard where cardNum=?";
		return (RunningWaterCard) totalDao
				.getObjectByCondition(hql, cardNumber);
	}

	/***
	 * 通过卡号查询卡信息并绑定卡
	 * 
	 * @param cardNumber
	 *            卡号
	 * @param procard
	 *            流水卡片
	 * @return
	 */
	@Override
	/**保存生产周转卡（制卡）**/
	public String saveRunWC(String cardNumber, Procard procard) {
		// 判断已绑定卡是否可以更换(为"发卡"状态的可以再更换)
		RunningWaterCard oldRwc = null;
		if (procard != null && procard.getCardId() != null) {
			oldRwc = findRunWC(procard.getCardNum());
			// 如果没有领料
			if (oldRwc != null && oldRwc.getReceiveStatus().equals("yes")) {
				return "已发卡号:" + procard.getCardNum() + "已开始使用,不能被更换!";
			}
		}
		String message = "";
		RunningWaterCard rwc = findRunWC(cardNumber);
		if (rwc != null) {
			if ("初始".equals(rwc.getCardStatus())) {
				String carMarkId = rwc.getMarkId().trim();
				String procarMarkId = procard.getMarkId();
				if (carMarkId.equals(procarMarkId)) {
					// 判断材料是否足够
					Boolean bool = false;
					String hql3 = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?";
					String hql4 = "select sum(goodsZhishu) from Goods where goodsMarkId=?";
					if ("自制".equals(procard.getProcardStyle())) {
						hql3 += " and goodsFormat=?";
						// 根据单位查询
						Object yuanobj = totalDao.getObjectByCondition(hql3,
								procard.getTrademark(), procard.getYuanUnit(),
								procard.getSpecification());
						Float sumCount = 0F;
						if (yuanobj != null) {
							sumCount = Float.parseFloat(yuanobj.toString());
						} else {
							// 根据支数查询
							hql4 += " and goodsFormat=?";
							Object obj = totalDao.getObjectByCondition(hql4,
									procard.getTrademark(), procard
											.getSpecification());
							if (obj != null) {
								sumCount = Float.parseFloat(obj.toString());
							} else {
								// 根据件号和规格查询原材料数量
								Object sumobj = totalDao
										.getObjectByCondition(
												"select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsFormat like ?",
												procard.getTrademark(),
												"%"
														+ procard
																.getSpecification()
														+ "%");
								if (sumobj != null) {
									sumCount = Float.parseFloat(sumobj
											.toString());
									if (sumCount > 0) {
										bool = true;// 当不存在单位数据和支数数据时并存在原材料信息时直接跳过数量验证
									}
								} else {
									message = "未找到本工艺卡片对应的原材料信息,无法进行发卡操作!";
								}
							}
						}
						if (bool == false) {
							if (sumCount != null && sumCount > 0F) {
								// 查询已发卡但未领料的数据
								String SumhqlNeed = "select sum(needCount) from Procard where trademark=? and specification=? and status = '已发卡'";
								Object sumObj = totalDao.getObjectByCondition(
										SumhqlNeed, procard.getTrademark(),
										procard.getSpecification());
								Float sumNeed = 0F;
								if (sumObj != null) {
									sumNeed = Float.parseFloat(sumObj
											.toString());
								}
								// 已发卡的数量+本卡片数量
								Float sumAll = procard.getNeedCount();
								if (null != sumNeed) {
									sumAll = procard.getNeedCount() + sumNeed;
								}
								// 判断数量是否足够
								if (sumAll > sumCount) {
									message = "该流水卡片下的原材料"
											+ procard.getTrademark()
											+ " 库存数量不足,不能发卡!";
								} else {
									bool = true;
								}
							} else {
								message = "未找到本工艺卡片对应的原材料信息或库存数量不足,无法进行发卡操作!";
							}
						}
					} else if ("总成".equals(procard.getProcardStyle())
							|| "组合".equals(procard.getProcardStyle())) {
						// 查询下层是否存在外购件
						String hql2 = "from Procard where fatherId=? and procardStyle='外购'";
						List list = totalDao.query(hql2, procard.getId());
						if (list != null && list.size() > 0) {
							// 存在外购件，判断库存是否充足
							for (int i = 0; i < list.size(); i++) {
								Procard waiProcar = (Procard) list.get(i);
								// 查询状态
								String sumOld = "select sum(filnalCount) from Procard p where p.procard.status ='已发卡' and markId=?";
								Object sumobj = null;
								sumobj = totalDao.getObjectByCondition(sumOld,
										waiProcar.getMarkId());
								Float sumoldCount = 0F;
								if (sumobj != null) {
									sumoldCount = Float.parseFloat(sumobj
											.toString());
								}
								Float sumAll = sumoldCount;
								// 查询库存数量
								Double sumCount = (Double) totalDao
										.getObjectByCondition(hql3, waiProcar
												.getMarkId(), waiProcar
												.getUnit());
								if (sumCount == null) {
									sumCount = (Double) totalDao
											.getObjectByCondition(hql4,
													waiProcar.getMarkId());
								}
								// 判断数量是否足够
								if (sumCount != null && sumCount > 0
										&& sumAll <= sumCount) {
									if (i == 0) {
										bool = true;
									}
								} else {
									message += "该流水卡片下的外购件"
											+ waiProcar.getMarkId()
											+ " 库存数量不足,不能发卡!\n";
									bool = false;
								}
							}
						} else {
							bool = true;
						}
					}

					// 绑定卡片
					if (bool) {
						if (oldRwc != null
								&& oldRwc.getReceiveStatus().equals("初始")) {
							// 将老卡更新为"初始"
							oldRwc.setCardStatus("初始");
							oldRwc.setReceiveStatus("初始");
							totalDao.update(oldRwc);
						}
						String sql = "update ta_sop_w_procard set cardId=? , cardNum=? , status='已发卡' where id=?";
						int count = totalDao.createQueryUpdate(null, sql, rwc
								.getId(), rwc.getCardNum(), procard.getId());
						if (count > 0) {
							rwc.setCardStatus("已发卡");
							rwc.setReceiveStatus("no");
							rwc.setProcardId(procard.getId());// 流水单id
							rwc.setFollowCardId(procard.getSelfCard());// 批次号
							rwc.setFollowTime(Util.getDateTime());// 发卡时间
							Users loginUser = Util.getLoginUser();
							rwc.setOwnUsername(loginUser.getName());// 持卡人为交卡处理登录人员
							totalDao.update(rwc);
							message = "发卡成功!\n本次发卡对应流水单件号: "
									+ procard.getMarkId() + " 批次号: "
									+ procard.getSelfCard();
						}
					}
				} else {
					message = "您要发的卡不能与该工艺卡片绑定,原因: 件号不符合!";
				}
			} else {
				message = "本卡正在使用中,暂时无法绑定!请换另一张卡!";
			}
		} else {
			message = "您要发的卡尚未制作!请联系管理员!";
		}
		return message;
	}

	/***
	 * 发卡(只发总成的卡片)
	 * 
	 * @param cardNumber
	 *            卡号
	 * @param procard2
	 *            流水卡片
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	/**保存生产周转卡（制卡）**/
	public String sendRunCard(String cardNumber, Procard procard) {
		// 判断已绑定卡是否可以更换(为"发卡"状态的可以再更换)
		RunningWaterCard oldRwc = null;
		if (procard != null && procard.getCardId() != null) {
			oldRwc = findRunWC(procard.getCardNum());
			// 如果没有领料
			if (oldRwc != null && oldRwc.getReceiveStatus().equals("yes")) {
				return "已发卡号:" + procard.getCardNum() + "已开始使用,不能被更换!";
			}
		}
		String message = "";
		RunningWaterCard rwc = findRunWC(cardNumber);
		if (rwc != null) {
			if ("初始".equals(rwc.getCardStatus())) {
				String carMarkId = rwc.getMarkId().trim();
				String procarMarkId = procard.getMarkId();
				if (carMarkId.equals(procarMarkId)) {

					/*** 开始判断最后一层的的已有材料信息 ***/
					// 查询最后一层信息
					String hql = "select belongLayer from Procard where rootId=? and (procardstyle in ('总成','自制','组合') or (needProcess ='yes' and procardstyle='外购')) order by belonglayer desc";
					Object belonglayerObj = totalDao.getObjectByCondition(hql,
							procard.getId());
					int belonglayer = Integer.parseInt(belonglayerObj
							.toString());
					Boolean bool = true;

					if ("初始".equals(procard.getStatus())) {
						// 是否需要领料
						if (procard.getLingliaostatus() != null
								&& "否".equals(procard.getLingliaostatus())) {
							procard.setStatus("已发料");
							procard.setHascount(0F);
						} else {
							procard.setStatus("已发卡");
						}
					}

					// 先按照层次激活一遍
					bool = procardServer.jihuoProcard(procard.getRootId(),
							belonglayer, "发卡");

					// 如果类型是自制件激活,则激活最后一层自制件
					if (bool && procard.getJihuoType() != null
							&& procard.getJihuoType().equals("zzj")) {
						/** 将所有的自制件全部激活(因为自制件肯定是最后一层的数据) 开始 **/
						String hql2 = " from Procard where rootId=? and procardstyle in ('自制') ";
						List<Procard> zizhiList = totalDao.query(hql2, procard
								.getId());
						for (Procard procard2 : zizhiList) {
							// 自制件全部激活
							procard2.setJihuoStatua("激活");
							procard2.setKlNumber(procard2.getFilnalCount());
							procard2.setYlNumber(0F);
							procard2.setStatus("已发卡");
							bool = totalDao.update(procard2);
						}
						/** 将所有的自制件全部激活(因为自制件肯定是最后一层的数据) 完成 **/
					}

					// 绑定卡片
					if (bool) {
						if (oldRwc != null
								&& oldRwc.getReceiveStatus().equals("初始")) {
							// 将老卡更新为"初始"
							oldRwc.setCardStatus("初始");
							oldRwc.setReceiveStatus("初始");
							totalDao.update(oldRwc);
						}
						// if (count > 0) {
						rwc.setCardStatus("已发卡");
						rwc.setReceiveStatus("no");
						rwc.setProcardId(procard.getId());// 流水单id
						rwc.setFollowCardId(procard.getSelfCard());// 批次号
						rwc.setFollowTime(Util.getDateTime());// 发卡时间
						Users loginUser = Util.getLoginUser();
						rwc.setOwnUsername(loginUser.getName());//
						// 持卡人为交卡处理登录人员
						totalDao.update(rwc);
						List<Procard> all = totalDao.query(
								"from Procard where rootId=?", procard.getId());
						for (Procard p : all) {
							p.setCardId(rwc.getId());
							p.setCardNum(rwc.getCardNum());
							totalDao.update(p);
						}
						message = "发卡成功!\n本次发卡对应流水单件号: " + procard.getMarkId()
								+ " 批次号: " + procard.getSelfCard();
						// }
					}
				} else {
					message = "您要发的卡不能与该工艺卡片绑定,原因: 件号不符合!";
				}
			} else {
				message = "本卡正在使用中,暂时无法绑定!请换另一张卡!";
			}
		} else {
			message = "您要发的卡尚未制作!请联系管理员!";
		}
		return message;
	}

	@SuppressWarnings("unchecked")
	public String sendRunCard(Procard procard) {
		String message = "";
		/*** 开始判断最后一层的的已有材料信息 ***/
		// 查询最后一层信息
		String hql = "select belongLayer from Procard where rootId=? and procardstyle in ('总成','自制','组合') order by belonglayer desc";
		Object belonglayerObj = totalDao.getObjectByCondition(hql, procard
				.getId());
		int belonglayer = Integer.parseInt(belonglayerObj.toString());
		String belonglayerHql = "from Procard where rootId=? and  belonglayer = ?";
		List<Procard> procardList = totalDao.query(belonglayerHql, procard
				.getRootId(), belonglayer);

		Boolean bool = false;
		for (Procard procard2 : procardList) {
			// 判断材料是否足够
			String hql3 = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?";
			String hql4 = "select sum(goodsZhishu) from Goods where goodsMarkId=?";
			// 默认按照层次激活
			if (procard.getJihuoType() == null
					|| procard.getJihuoType().equals("cc")) {
				if ("自制".equals(procard2.getProcardStyle())) {
					hql3 += " and goodsFormat=?";
					// 根据单位查询
					Object yuanobj = totalDao.getObjectByCondition(hql3,
							procard2.getTrademark(), procard2.getYuanUnit(),
							procard2.getSpecification());
					Float sumCount = 0F;
					if (yuanobj != null) {
						sumCount = Float.parseFloat(yuanobj.toString());
					} else {
						// 根据支数查询
						hql4 += " and goodsFormat=?";
						Object obj = totalDao.getObjectByCondition(hql4,
								procard2.getTrademark(), procard2
										.getSpecification());
						if (obj != null) {
							sumCount = Float.parseFloat(obj.toString());
						} else {
							// 根据件号和规格查询原材料数量
							Object sumobj = totalDao
									.getObjectByCondition(
											"select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsFormat like ?",
											procard2.getTrademark(), "%"
													+ procard2
															.getSpecification()
													+ "%");
							if (sumobj != null) {
								sumCount = Float.parseFloat(sumobj.toString());
								if (sumCount > 0) {
									// 激活卡片所有可领
									procard2.setJihuoStatua("激活");
									procard2.setKlNumber(procard2
											.getFilnalCount());
									procard2.setYlNumber(0F);
									bool = true;// 当不存在单位数据和支数数据时并存在原材料信息时直接跳过数量验证
								}
							} else {
								message = "未找到本工艺卡片对应的原材料信息,无法进行发卡操作!";
							}
						}
					}
					if (bool == false) {
						if (sumCount != null && sumCount > 0F) {
							// 查询已发卡但未领料的数据
							String SumhqlNeed = "select sum(needCount) from Procard where trademark=? and specification=? and status = '已发卡'";
							Object sumObj = totalDao.getObjectByCondition(
									SumhqlNeed, procard2.getTrademark(),
									procard2.getSpecification());
							Float sumNeed = 0F;
							if (sumObj != null) {
								sumNeed = Float.parseFloat(sumObj.toString());
							}
							// 已发卡的数量+本卡片数量
							Float sumAll = procard2.getNeedCount();
							if (null != sumNeed) {
								sumAll = procard2.getNeedCount() + sumNeed;
							}
							// 反向推算出可领数量数量
							// 激活卡片所有可领
							procard2.setJihuoStatua("激活");
							procard2.setKlNumber(procard2.getFilnalCount());
							procard2.setYlNumber(0F);
							bool = true;
						} else {
							procard2.setJihuoStatua("激活");
							message = "未找到本工艺卡片对应的原材料信息或库存数量不足,无法进行发卡操作!";
						}
					}
					// 自制件全部激活
					procard2.setJihuoStatua("激活");
					procard2.setKlNumber(procard2.getFilnalCount());
					procard2.setYlNumber(0F);
					bool = true;
				}
				continue;
			}
			if ("总成".equals(procard2.getProcardStyle())
					|| "组合".equals(procard2.getProcardStyle())) {
				// 查询下层是否存在外购件
				String hql2 = "from Procard where fatherId=? and procardStyle='外购'";
				List list = totalDao.query(hql2, procard2.getId());
				if (list != null && list.size() > 0) {
					// 存在外购件，判断库存是否充足
					for (int i = 0; i < list.size(); i++) {
						Procard waiProcar = (Procard) list.get(i);
						Float nowCount = waiProcar.getFilnalCount();// 批次数量
						// 查询状态
						String sumOld = "select sum(filnalCount) from Procard where status in ('初始','已发卡') and markId=?";
						Object sumobj = null;
						sumobj = totalDao.getObjectByCondition(sumOld,
								waiProcar.getMarkId());
						Float sumoldCount = 0F;
						if (sumobj != null) {
							sumoldCount = Float.parseFloat(sumobj.toString());
						}
						Float sumAll = sumoldCount;
						// 查询库存数量
						Object obj = totalDao.getObjectByCondition(hql3,
								waiProcar.getMarkId(), waiProcar.getUnit());
						Float sumCount = 0F;
						if (obj != null) {
							sumCount = Float.parseFloat(obj.toString());
						}
						if (sumCount == null) {
							sumCount = Float.parseFloat(totalDao
									.getObjectByCondition(hql4,
											waiProcar.getMarkId()).toString());
						}

						// 判断数量是否足够
						waiProcar.setJihuoStatua("激活");
						waiProcar.setKlNumber(waiProcar.getFilnalCount());
						waiProcar.setTjNumber(0F);
						waiProcar.setMinNumber(0F);
						if (sumCount != null && sumCount > 0) {
							// 如果库存数量比本批次需求数量多时
							if (sumCount - sumoldCount >= 0) {
								if (sumCount - sumAll >= 0) {
									// 如果库存数量比总数量还多时，本次件号全部提交
									waiProcar.setTjNumber(waiProcar
											.getFilnalCount());
								} else {
									// 比如： 800(4批)-300>200(本批)
									// 400(2批)-300<200(本批)
									if (sumAll - sumCount < nowCount) {
										waiProcar
												.setTjNumber(sumAll - sumCount);
									}
								}

							} else {
								// 数量不足，但是本批刚好只有一批
								// 比如： 800(4批)-100(库存)>200(本批)
								// 200(1批)-100(库存)<200(本批)
								if (sumAll - sumCount < nowCount) {
									waiProcar.setTjNumber(sumCount);
								}
							}
							Float minNumber = waiProcar.getTjNumber()
									/ waiProcar.getQuanzi2()
									* waiProcar.getQuanzi1();
							if (waiProcar.getTjNumber() == waiProcar
									.getFilnalCount()) {
								minNumber = (float) Math.ceil(minNumber);
							}
							waiProcar.setMinNumber(minNumber);
							bool = true;
						} else {
							// message += "该流水卡片下的外购件" +
							// waiProcar.getMarkId()
							// + " 库存数量不足,不能发卡!\n";
							bool = true;
						}
						totalDao.update(waiProcar);
					}
				} else {
					bool = true;
				}
				procard2.setJihuoStatua("未激活");
				procard2.setYlNumber(0F);
				procard2.setKlNumber(0F);
				if (bool) {
					// 激活卡片可领数量
					// 获得最小激活数量
					String fatherHql = "select min(minNumber) from Procard where rootId=? and belongLayer=? ";
					Object fatherObj = totalDao
							.getObjectByCondition(fatherHql, procard2
									.getRootId(), procard2.getBelongLayer() + 1);
					Float minNumber = 0F;
					if ("总成".equals(procard2.getProcardStyle())) {
						minNumber = procard2.getFilnalCount();
					} else if (fatherObj != null) {
						minNumber = Float.parseFloat(fatherObj.toString());
					}
					if (minNumber > 0) {
						procard2.setJihuoStatua("激活");
					}
					if (procard2.getFilnalCount() > minNumber) {
						minNumber = (float) Math.floor(minNumber);
						procard2.setKlNumber(minNumber);// 可领数量
					} else {
						procard2.setKlNumber(procard2.getFilnalCount());
					}
				}
			}
			// else if ("外购".equals(procard2.getProcardStyle())) {
			// Procard waiProcar = procard2;
			// // 查询状态
			// String sumOld =
			// "select sum(filnalCount) from Procard where status ='已发卡' and markId=?";
			// Object sumobj = null;
			// sumobj = totalDao.getObjectByCondition(sumOld,
			// waiProcar.getMarkId());
			// Float sumoldCount = 0F;
			// if (sumobj != null) {
			// sumoldCount = Float.parseFloat(sumobj
			// .toString());
			// }
			// Float sumAll = sumoldCount;
			// // 查询库存数量
			// Object obj = totalDao.getObjectByCondition(hql3,
			// waiProcar.getMarkId(), waiProcar.getUnit());
			// Float sumCount = 0F;
			// if (obj != null) {
			// sumCount = Float.parseFloat(obj.toString());
			// }
			// if (sumCount == null) {
			// sumCount = Float.parseFloat(totalDao
			// .getObjectByCondition(hql4,
			// waiProcar.getMarkId())
			// .toString());
			// }
			//
			// // 判断数量是否足够
			// waiProcar.setJihuoStatua("激活");
			// waiProcar.setKlNumber(waiProcar.getFilnalCount());
			// waiProcar.setTjNumber(0F);
			// waiProcar.setMinNumber(0F);
			// if (sumCount != null && sumCount > 0) {
			// // 数量充足
			// if (sumCount - sumoldCount >= 0) {
			// if (sumCount - sumAll >= 0) {
			// waiProcar.setTjNumber(waiProcar
			// .getFilnalCount());
			// } else {
			// // 数量不足，
			// waiProcar
			// .setTjNumber(sumAll - sumCount);
			// }
			//
			// }
			// waiProcar.setMinNumber(waiProcar.getTjNumber()
			// / waiProcar.getQuanzi1()
			// * waiProcar.getQuanzi2());
			// } else {
			// // message += "该流水卡片下的外购件" +
			// // waiProcar.getMarkId()
			// // + " 库存数量不足,不能发卡!\n";
			// bool = true;
			// }
			// totalDao.update(waiProcar);
			//
			// }
			// // 激活卡片所有可领
			// procard2.setJihuoStatua("激活");
			// procard2.setKlNumber(procard2.getFilnalCount());
			// procard2.setYlNumber(0F);
			// bool = true;
		}
		// 最后一层自制件激活
		if (procard.getJihuoType() != null
				&& procard.getJihuoType().equals("zzj")) {
			/** 将所有的自制件全部激活(因为自制件肯定是最后一层的数据) 开始 **/
			String hql2 = " from Procard where rootId=? and procardstyle in ('自制') ";
			List<Procard> zizhiList = totalDao.query(hql2, procard.getId());
			for (Procard procard2 : zizhiList) {
				// 判断材料是否足够
				String hql3 = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?";
				String hql4 = "select sum(goodsZhishu) from Goods where goodsMarkId=?";
				hql3 += " and goodsFormat=?";
				// 根据单位查询
				Object yuanobj = totalDao.getObjectByCondition(hql3, procard2
						.getTrademark(), procard2.getYuanUnit(), procard2
						.getSpecification());
				Float sumCount = 0F;
				if (yuanobj != null) {
					sumCount = Float.parseFloat(yuanobj.toString());
				} else {
					// 根据支数查询
					hql4 += " and goodsFormat=?";
					Object obj = totalDao.getObjectByCondition(hql4, procard2
							.getTrademark(), procard2.getSpecification());
					if (obj != null) {
						sumCount = Float.parseFloat(obj.toString());
					} else {
						// 根据件号和规格查询原材料数量
						Object sumobj = totalDao
								.getObjectByCondition(
										"select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsFormat like ?",
										procard2.getTrademark(), "%"
												+ procard2.getSpecification()
												+ "%");
						if (sumobj != null) {
							sumCount = Float.parseFloat(sumobj.toString());
							if (sumCount > 0) {
								// 激活卡片所有可领
								procard2.setJihuoStatua("激活");
								procard2.setKlNumber(procard2.getFilnalCount());
								procard2.setYlNumber(0F);
								bool = true;// 当不存在单位数据和支数数据时并存在原材料信息时直接跳过数量验证
							}
						} else {
							message = "未找到本工艺卡片对应的原材料信息,无法进行发卡操作!";
						}
					}
				}
				if (bool == false) {
					if (sumCount != null && sumCount > 0F) {
						// 查询已发卡但未领料的数据
						String SumhqlNeed = "select sum(needCount) from Procard where trademark=? and specification=? and status = '已发卡'";
						Object sumObj = totalDao.getObjectByCondition(
								SumhqlNeed, procard2.getTrademark(), procard2
										.getSpecification());
						Float sumNeed = 0F;
						if (sumObj != null) {
							sumNeed = Float.parseFloat(sumObj.toString());
						}
						// 已发卡的数量+本卡片数量
						Float sumAll = procard2.getNeedCount();
						if (null != sumNeed) {
							sumAll = procard2.getNeedCount() + sumNeed;
						}
						// 反向推算出可领数量数量
						// 激活卡片所有可领
						procard2.setJihuoStatua("激活");
						procard2.setKlNumber(procard2.getFilnalCount());
						procard2.setYlNumber(0F);
						bool = true;

						// // 判断数量是否足够
						// if (sumAll > sumCount) {
						// message = "该流水卡片下的原材料"
						// + procard2.getTrademark()
						// + " 库存数量不足,不能发卡!";
						// } else {
						// }
					} else {
						procard2.setJihuoStatua("激活");
						message = "未找到本工艺卡片对应的原材料信息或库存数量不足,无法进行发卡操作!";
					}
				}
				// 自制件全部激活
				procard2.setJihuoStatua("激活");
				procard2.setKlNumber(procard2.getFilnalCount());
				procard2.setYlNumber(0F);
				bool = true;
			}
			/** 将所有的自制件全部激活(因为自制件肯定是最后一层的数据) 完成 **/
		}
		// 更新卡片状态
		if (bool) {
			String hql5 = "update Procard set status='已发卡' where rootId=?";
			totalDao.createQueryUpdate(hql5, null, procard.getId());
		}
		return message;
	}

	/*** 根据生产周转卡编号查找工艺流水单 ***/
	@Override
	public RunningWaterCard getCardBycardNum(String num, String tag) {
		// TODO Auto-generated method stub
		String hql = " from RunningWaterCard where cardNum='" + num + "'";
		if ("out".equals(tag)) {
			hql += "and receiveStatus='no'";
		} else if ("in".equals(tag)) {
			hql += "and cardStatus='完成'";
		}
		List list = totalDao.find(hql);
		if (list.size() > 0) {
			RunningWaterCard rwc = (RunningWaterCard) list.get(0);
			String hqlMinLot = "select min(followCardId) from RunningWaterCard where markId='"
					+ rwc.getMarkId()
					+ "' and cardStatus='"
					+ rwc.getCardStatus() + "'";
			List listMin = totalDao.find(hqlMinLot);
			if (null != listMin && listMin.size() > 0) {
				String LotId = (String) listMin.get(0);
				if (LotId.equals(rwc.getFollowCardId())) {
					return rwc;
				} else {
					String hqlMinFollow = "from RunningWaterCard where markId='"
							+ rwc.getMarkId()
							+ "' and cardStatus='"
							+ rwc.getCardStatus()
							+ "' and followCardId='"
							+ LotId + "'";
					return (RunningWaterCard) totalDao.find(hqlMinFollow)
							.get(0);
				}
			}
		}
		return null;
	}

	/*** 根据条码查找补料单 ***/
	@Override
	public LogoStickers getLogoSticker(String barcode, String tag) {
		// TODO Auto-generated method stub
		String hql = "";
		if ("in".equals(tag)) {
			hql = "from LogoStickers where number='" + barcode
					+ "' and status='完成'";
		} else if ("out".equals(tag)) {
			hql = "from LogoStickers where number='" + barcode
					+ "' and status='已发卡'";
		}
		List list = totalDao.find(hql);
		if (null != list && list.size() > 0) {
			return (LogoStickers) list.get(0);
		}
		return null;
	}

	/** 根据工艺流水单查找三检和巡检记录 **/
	public String findExamTitle(Procard procard) {
		if ("总成".equals(procard.getProcardStyle())) {
			// 检查上一批次是否有入库申请数量
			// String selfCard = (String) totalDao
			// .getObjectByCondition(
			// "select selfCard from Procard where markId=? and productStyle=? and selfCard<? and procardStyle='总成' and status not in ('取消') order by selfCard desc",
			// procard.getMarkId(), procard.getProductStyle(),
			// procard.getSelfCard());
			// if (selfCard != null && selfCard.length() > 0) {
			// GoodsStore g = (GoodsStore) totalDao
			// .getObjectByCondition(
			// "from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=?",
			// procard.getMarkId(), selfCard);
			// if (g == null || g.getGoodsStoreCount() <= 0) {
			// return "请先申请第" + selfCard + "批次!";
			// }
			// }
			if (procard.getProductStyle().equals("批产")) {
				Integer zjCount = totalDao
						.getCount(
								"from ProcessInfor where (zjStatus is not null and zjStatus!='no') and procard.id=? and productStyle ='自制' and (agreeWwCount is null or agreeWwCount<totalCount)",
								procard.getId());
				if (zjCount != null && zjCount > 0) {
					// 查询首检记录
					String hql_sj = "from LogoStickers where markId=? and lotId=?";
					Integer sjcount = totalDao.getCount(hql_sj, procard
							.getMarkId(), procard.getSelfCard());
					// -测试屏蔽
					if (sjcount == 0) {
						return "件号" + procard.getMarkId() + " 批次"
								+ procard.getSelfCard() + "无首检记录,无法申请入库";
					}
				}

				// String hql =
				// "from Procard where status='入库' order by markId";
				// List<Procard> procardlist = totalDao.query(hql);
				// StringBuffer sb = new StringBuffer();
				// for (Procard procard2 : procardlist) {
				// // 查询巡检记录
				// String hql_xj =
				// "from InsRecord i where i.root.id in (select distinct(id) from InsTemplate t where t.partNumber=?) and i.jcpc=?";
				// Integer xjcount = totalDao.getCount(hql_xj, procard2
				// .getMarkId(), procard2.getSelfCard());
				// if (xjcount == 0) {
				// sb.append(procard2.getMarkId() + ","
				// + procard2.getSelfCard()+"\n");
				// }
				// }
				// System.out.println(sb.toString());
				if (procard.getFilnalCount() > 1
						&& procard.getProductStyle().equals("批产")) {// 大于一件才巡检
					String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='入库巡检验证' and valueName='入库巡检验证'";
					String valueCode = (String) totalDao
							.getObjectByCondition(hql1);
					if (valueCode == null || !valueCode.equals("否")) {
						// 查询巡检记录
						Integer xjCount = totalDao
								.getCount(
										"from ProcessInfor where  procard.id=? and productStyle ='自制' and (agreeWwCount is null or agreeWwCount<totalCount)",
										procard.getId());
						if (xjCount != null && xjCount > 0) {
							String hql_xj = "from OsRecord i where i.template.id in (select distinct(id) from OsTemplate t where t.partNumber=?) and i.jcpc=?";
							Integer xjcount = totalDao.getCount(hql_xj, procard
									.getMarkId(), procard.getSelfCard());
							if (xjcount == 0) {
								// -测试屏蔽
								return "件号" + procard.getMarkId() + " 批次"
										+ procard.getSelfCard()
										+ "无巡检记录,无法申请入库";
							}
						}
					}
				}
			}
			return "true";
		}
		return "申请入库数据异常!";
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	@Override
	public WarehouseNumber findWNBybarCode(String barCode) {
		if (barCode != null && !"".equals(barCode)) {
			WarehouseNumber wn = (WarehouseNumber) totalDao
					.getObjectByCondition(
							"from WarehouseNumber where barCode =?", barCode);
			return wn;
		}
		return null;
	}

	@Override
	public String OpenWNByWNNumber(WarehouseNumber wn) {
		// TODO Auto-generated method stub
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
}
