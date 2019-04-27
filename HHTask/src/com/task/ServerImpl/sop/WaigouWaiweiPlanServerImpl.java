package com.task.ServerImpl.sop;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import net.sf.morph.wrap.Bean;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.BeanUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.caiwu.core.CorePayableServer;
import com.task.Server.ess.GoodsStoreServer;
import com.task.Server.sop.ManualOrderPlanServer;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.WaigouWaiweiPlanServer;
import com.task.Server.sys.CircuitRunServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.menjin.DoorBangDingServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Also;
import com.task.entity.Borrow;
import com.task.entity.Category;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.OaAppDetail;
import com.task.entity.OaAppDetailTemplate;
import com.task.entity.OrderManager;
import com.task.entity.Price;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.WarehouseNumber;
import com.task.entity.android.OsRecord;
import com.task.entity.android.OsRecordScope;
import com.task.entity.android.OsScope;
import com.task.entity.android.OsTemplate;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.Measuring;
import com.task.entity.gzbj.ProcessAndMeasuring;
import com.task.entity.hegebaobiao.DayHege;
import com.task.entity.hegebaobiao.MouthHege;
import com.task.entity.hegebaobiao.QuarterHege;
import com.task.entity.hegebaobiao.YearHege;
import com.task.entity.hegebaobiao.ZhouHege;
import com.task.entity.menjin.KuweiSongHuoDan;
import com.task.entity.menjin.MarkStatusType;
import com.task.entity.menjin.WareBangGoogs;
import com.task.entity.qualifiedrate.QualifiedRate;
import com.task.entity.sop.BuHeGePin;
import com.task.entity.sop.DefectiveProduct;
import com.task.entity.sop.DuiZhangWgPlan;
import com.task.entity.sop.FailureSSOnDay;
import com.task.entity.sop.FailureSSOnMonth;
import com.task.entity.sop.FailureSSOnWeek;
import com.task.entity.sop.FailureStatistics;
import com.task.entity.sop.FailureStatisticsDetail;
import com.task.entity.sop.GysLaiLiaoQuanXianTj;
import com.task.entity.sop.GysMarkIdCkolse;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardCsBl;
import com.task.entity.sop.ProcardForProcess;
import com.task.entity.sop.ProcardProductRelation;
import com.task.entity.sop.ProcardSbWgLog;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardWGCenter;
import com.task.entity.sop.ProcessAndWgProcardTem;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessInforWWProcard;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ReturnSingle;
import com.task.entity.sop.ReturnsDetails;
import com.task.entity.sop.WaigouDailySheet;
import com.task.entity.sop.WaigouDelivery;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouPlanclApply;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.WgProcardMOQ;
import com.task.entity.sop.YcProduct;
import com.task.entity.sop.muju.MouldApplyOrder;
import com.task.entity.sop.muju.MouldDetail;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.CompanyInfo;
import com.task.entity.wlWeizhiDt.WlWeizhiDt;
import com.task.util.SendMail;
import com.task.util.Util;
import com.task.util.UtilTong;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.Waigoujianpinci;
import com.tast.entity.zhaobiao.WaigoujianpinciZi;
import com.tast.entity.zhaobiao.ZhUser;

/***
 * 外购外委server层实现类
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings("unchecked")
public class WaigouWaiweiPlanServerImpl implements WaigouWaiweiPlanServer {
	private static final Object WaigouOrder = null;
	private StringBuffer strbu = new StringBuffer();
	public static Integer duankou = 8885;
	public static Integer mim = 1314;
	private TotalDao totalDao;
	private GoodsStoreServer goodsStoreServer;
	private CorePayableServer corePayableServer;
	private ManualOrderPlanServer manualPlanServer;
	private float hgNumber;
	private WaigouDeliveryDetail wgdd;
	private CircuitRunServer circuitRunServer;
	private ProcardServer procardServer;

	public CircuitRunServer getCircuitRunServer() {
		return circuitRunServer;
	}

	public void setCircuitRunServer(CircuitRunServer circuitRunServer) {
		this.circuitRunServer = circuitRunServer;
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public GoodsStoreServer getGoodsStoreServer() {
		return goodsStoreServer;
	}

	public void setGoodsStoreServer(GoodsStoreServer goodsStoreServer) {
		this.goodsStoreServer = goodsStoreServer;
	}

	public ManualOrderPlanServer getManualPlanServer() {
		return manualPlanServer;
	}

	public void setManualPlanServer(ManualOrderPlanServer manualPlanServer) {
		this.manualPlanServer = manualPlanServer;
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	/****
	 * 待确认物料列表(总成明细)
	 * 
	 * @return
	 */
	@Override
	public Object[] findDqrWlList(Procard procard, int pageNo, int pageSize,
			String pageStatus) {
		if (procard == null) {
			procard = new Procard();
		}
		String hql = totalDao.criteriaQueries(procard, null)
				+ " and procardStyle='总成' and status <> '取消' and wlstatus is not null and wlstatus<>'' ";
		// 所有待采购产品
		if ("dqr".equals(pageStatus)) {
			hql += " and wlstatus='待确认'";
		} else {
			// hql += " and wlstatus = '待采购' and status<>'初始'";
			hql += " and (status<>'初始' or wlstatus<>'待定') ";
		}
		// List list = totalDao.query(hql + " order by wlqrtime desc");
		List list = totalDao.findAllByPage(hql + " order by id desc", pageNo,
				pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/****
	 * 待确认物料明细(总成——外购明细)
	 * 
	 * @return
	 */
	@Override
	public Object[] findDqrWlDetailList(Integer rootId, Procard procard) {
		Procard rootProcard = (Procard) totalDao.getObjectById(Procard.class,
				rootId);
		if (rootProcard != null) {
			if (procard == null) {
				procard = new Procard();
			}
			String hql_sum = "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,"
					+ "sum(filnalCount),sum(cgNumber),min(zzNumber),min(ztNumber),min(kcNumber),remark,wgjihuoTime,"
					+ "needFinalDate ";
			String hql = totalDao.criteriaQueries(procard, null);
			String hql_all = hql_sum
					+ hql
					+ " and rootid=? and procardStyle='外购' and cgNumber is not null and (sbStatus is null or sbStatus!='删除') "
					+ " and id not in "
					+ "(select procardId from ManualOrderPlanDetail where procardId in (select id from Procard where rootId=? and procardStyle='外购') ) "
					+ "group by wgType,markId,banBenNumber,kgliao,proName,specification,unit,remark ";
			List<Object[]> list = totalDao.query(hql_all, rootId, rootId);
			List newList = new ArrayList();
			List<WgProcardMOQ> wgmoqList = new ArrayList<WgProcardMOQ>();
			for (int i = 0, len = list.size(); i < len; i++) {
				Object[] obj = list.get(i);
				if (obj != null) {
					Procard procard_new = new Procard();
					procard_new.setWgType(str(obj, 0));
					procard_new.setMarkId(str(obj, 1));
					if (obj[2] != null) {
						procard_new.setBanBenNumber(str(obj, 2));
					}
					procard_new.setKgliao(str(obj, 3));
					procard_new.setProName(str(obj, 4));
					procard_new.setSpecification(str(obj, 5));
					procard_new.setUnit(str(obj, 6));
					procard_new.setFilnalCount(Float.parseFloat(flo(obj, 7)));
					procard_new.setCgNumber(Float.parseFloat(flo(obj, 8)));
					if (obj[9] == null) {
						obj[9] = "0";
					}
					procard_new.setZzNumber(Float.parseFloat(flo(obj, 9)));
					if (obj[10] == null) {
						obj[10] = "0";
					}
					procard_new.setZtNumber(Float.parseFloat(flo(obj, 10)));
					if (obj[11] == null) {
						obj[11] = "0";
					}
					procard_new.setKcNumber(Float.parseFloat(flo(obj, 11)));
					procard_new.setRemark(str(obj, 12));
					procard_new.setWgjihuoTime(str(obj, 13));
					procard_new.setNeedFinalDate(str(obj, 14));
					String gysxu = "";
					String pricexu = "";
					if (procard_new.getBanBenNumber() != null
							&& procard_new.getBanBenNumber().length() > 0) {
						gysxu += " and banBenNumber = '"
								+ procard_new.getBanBenNumber() + "'";
						pricexu += " and banbenhao = '"
								+ procard_new.getBanBenNumber() + "'";
					} else {
						gysxu += " and (banBenNumber = '' or banBenNumber is null)";
						pricexu += " and ( banbenhao = '' or banbenhao is null)";
					}
					if (procard_new.getSpecification() != null
							&& procard_new.getSpecification().length() > 0) {
						gysxu += " and specification = '"
								+ procard_new.getSpecification() + "'";
						pricexu += " and specification = '"
								+ procard_new.getSpecification() + "'";
					} else {
						gysxu += " and (specification = '' or specification is null)";
						pricexu += " and (specification = '' or specification is null )";
					}
					newList.add(procard_new);
				}
			}
			// 需求数量
			String hql_filnalCount = "select sum(filnalCount) "
					+ hql
					+ " and rootid=? and procardStyle='外购' and cgNumber is not null and id not in "
					+ "(select procardId from ManualOrderPlanDetail where procardId in (select id from Procard where rootId=? and procardStyle='外购') ) ";
			Float filnalCount = (Float) totalDao.getObjectByCondition(
					hql_filnalCount, rootId, rootId);
			// 采购数量
			String hql_cgNumber = "select sum(cgNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null and id not in "
					+ "(select procardId from ManualOrderPlanDetail where procardId in (select id from Procard where rootId=? and procardStyle='外购') ) ";
			Float cgNumber = (Float) totalDao.getObjectByCondition(
					hql_cgNumber, rootId, rootId);
			Object[] o = { newList, filnalCount, cgNumber, rootProcard,
					wgmoqList };

			return o;
		}
		return null;
	}

	/**
	 * 如果为空，返回""
	 * 
	 * @author licong
	 * @param obj
	 * @param i
	 * @return
	 */
	public String str(Object obj[], int i) {
		if (obj[i] == null)
			return "";
		else
			return obj[i].toString();
	}

	/**
	 *@author licong 如果为空，返回0
	 */
	public String flo(Object obj[], int i) {
		if (obj[i] == null)
			return "0";
		else
			return obj[i].toString();
	}

	/****
	 * 物料清单确认，进入采购流程
	 * 
	 * @param rootId
	 * @return
	 */
	@Override
	public String updateForWgPlan(Integer rootId, String needDate) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		Procard rootProcard = (Procard) totalDao.getObjectById(Procard.class,
				rootId);
		boolean bool = true;
		boolean date = false;
		if (needDate != null && needDate.length() > 9) {
			date = true;
		}
		if (rootProcard != null) {
			StringBuffer errormsg = new StringBuffer();
			rootProcard.setWlqrtime(Util.getDateTime());// 物料确认时间
			rootProcard.setWlstatus("待采购");
			bool = totalDao.update(rootProcard);
			List<Procard> wgprocardList = totalDao
					.query(
							" from Procard where rootId=? and procardStyle ='外购' and (sbStatus is null or sbStatus!='删除') and cgNumber is not null and cgNumber>0 "
									+ "and id not in "
									+ "(select procardId from ManualOrderPlanDetail where procardId in (select id from Procard where rootId=? and procardStyle='外购') )",
							rootId, rootId);
			if (wgprocardList != null && wgprocardList.size() > 0) {
				for (Procard procard : wgprocardList) {
					String type = "正式订单";
					if (procard.getOldProcardId() != null) {
						type = "补料订单";
					}
					ManualOrderPlanDetail mod = new ManualOrderPlanDetail(
							procard.getMarkId(), procard.getProName(), procard
									.getSpecification(), procard
									.getBanBenNumber(), procard.getUnit(),
							procard.getKgliao(), procard.getTuhao(), procard
									.getWgType(), procard.getCgNumber(),
							procard.getId(), Util.getDateTime(),
							user.getName(), user.getCode(), type, 0, "同意", null);
					mod.setRukuNum(0f);
					if (mod.getUnit() == null || "".equals(mod.getUnit())) {
						mod.setUnit(waigoujianKu(mod.getMarkId()));
					}
					mod.setNeedFinalDate(date ? needDate : procard
							.getNeedFinalDate());
					mod.setRootMarkId(procard.getRootMarkId());
					mod.setRootSelfCard(procard.getRootSelfCard());
					mod.setYwMarkId(procard.getYwMarkId());
					mod.setOrderNumber(procard.getOrderNumber());
					mod.setBanci(procard.getBanci());
					mod.setStatus("未采购");
					mod.setCategory("外购");
					if (procard.getOrderId() != null) {
						mod.setOrderId(Integer.parseInt(procard.getOrderId()));
					}
					procard.setWlqrtime(Util.getDateTime());
					totalDao.update(procard);
					bool = totalDao.save(mod);
					if (bool) {
						if (!manualPlanServer.addmaualPlan1(mod)) {
							return "确认失败";
						}
					}
				}
			}
		}
		return bool + "";
	}

	public String waigoujianKu(String markId) {
		String ss = "";
		try {
			String ss1 = (String) totalDao.getObjectByCondition(
					"select unit from YuanclAndWaigj where markId = ?", markId);
			if (ss1 != null && !"".equals(ss1)) {
				ss = ss1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss;
	}

	/****
	 * 物料追踪管理(总成——外购明细)
	 * 
	 * @return
	 */
	@Override
	public Object[] findAllWlDetailList(Integer rootId, Procard procard) {
		Procard rootProcard = (Procard) totalDao.getObjectById(Procard.class,
				rootId);
		if (rootProcard != null) {
			if (procard == null) {
				procard = new Procard();
			}
			String hql_sum = "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,sum(filnalCount),"
					+ "sum(cgNumber),sum(outcgNumber),sum(dhNumber),sum(tjNumber),sum(filnalCount-hascount),min(zzNumber),min(ztNumber),min(kcNumber),remark,wlqrtime ";
			// sum(tjNumber-(filnalCount-cgNumber)) == 入库
			String hql = totalDao.criteriaQueries(procard, null, "wgType",
					"wlstatus", "jihuoStatua");
			if (procard.getWgType() != null && procard.getWgType().length() > 0) {
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name=? ", procard.getWgType());
				getWgtype(category);
				hql += " and wgType in (" + strbu.toString().substring(1) + ")";
				strbu.setLength(0);
			}
			if (procard.getJihuoStatua() != null) {
				if ("未下单".equals(procard.getJihuoStatua())) {
					hql += " and  id   in ( select procardId from ManualOrderPlanDetail where 1=1 and( outcgNumber =0 or outcgNumber is null ) "
							+ "and procardId in (select id from Procard where rootId ="
							+ rootId + "))";
				} else if ("已下单".equals(procard.getJihuoStatua())) {
					hql += " and  id  in ( select procardId from ManualOrderPlanDetail where 1=1 and outcgNumber >0  "
							+ "and procardId in (select id from Procard where rootId ="
							+ rootId + "))";
				}
			}

			String hql_all = hql_sum
					+ hql
					+ " and rootid=? and procardStyle='外购' "
					+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit,IFNULL(remark,'') order by wgType desc";
			List<Object[]> list = totalDao.query(hql_all, rootId);
			List newList = new ArrayList();
			for (int i = 0, len = list.size(); i < len; i++) {
				Object[] obj = list.get(i);
				String hql_nowgoods = " select sum(goodsCurQuantity)  from Goods where goodsClass = '外购件库' and  goodsMarkId = ? and kgliao =? "
						+ " and goodsCurQuantity >0 ";
				Procard procard_new = new Procard();
				if (obj[0] != null) {
					procard_new.setWgType(obj[0].toString());
				}
				procard_new.setMarkId(obj[1].toString());
				if (obj[2] != null) {
					procard_new.setBanBenNumber(obj[2].toString());
					hql_nowgoods += " and banBenNumber = '" + obj[2].toString()
							+ "'";
				} else {
					hql_nowgoods += " and (banBenNumber = '' or banBenNumber is null)";
				}
				if (obj[3] != null) {
					procard_new.setKgliao(obj[3].toString());
				}
				procard_new.setProName(obj[4].toString());
				if (obj[5] != null) {
					procard_new.setSpecification(obj[5].toString());
				}
				procard_new.setUnit(obj[6].toString());
				try {
					procard_new.setFilnalCount(Float.parseFloat(obj[7]
							.toString()));
				} catch (Exception e2) {
					procard_new.setFilnalCount(0f);
				}
				try {
					procard_new
							.setCgNumber(Float.parseFloat(obj[8].toString()));
				} catch (Exception e1) {
					procard_new.setCgNumber(0f);
				}

				try {
					procard_new.setOutcgNumber(Float.parseFloat(obj[9]
							.toString()));
				} catch (Exception e) {
					procard_new.setOutcgNumber(0f);
				}
				try {
					procard_new.setDhNumber(Float
							.parseFloat(obj[10].toString()));
				} catch (Exception e) {
					procard_new.setDhNumber(0F);
				}
				try {
					procard_new.setTjNumber(Float
							.parseFloat(obj[11].toString()));
				} catch (Exception e) {
					procard_new.setTjNumber(0F);
				}
				try {
					procard_new.setHascount(Float
							.parseFloat(obj[12].toString()));
				} catch (Exception e) {
					procard_new.setHascount(0F);
				}
				if (obj[13] == null) {
					obj[13] = "0";
				}
				procard_new.setZzNumber(Float.parseFloat(obj[13].toString()));
				if (obj[14] == null) {
					obj[14] = "0";
				}
				procard_new.setZtNumber(Float.parseFloat(obj[14].toString()));
				if (obj[15] == null) {
					obj[15] = "0";
				}
				procard_new.setKcNumber(Float.parseFloat(obj[15].toString()));
				if (obj[16] != null) {
					procard_new.setRemark(obj[16].toString());
				}
				if (null != obj[17]) {
					procard_new.setWlqrtime(obj[17].toString());
				}
				// wgType,markId,banBenNumber,kgliao,proName,specification,unit
				// String hql_getProId = "from WaigouPlan where id in "
				// +
				// "(select wgOrderId from ProcardWGCenter where procardId in "
				// + "(select id from Procard where markId=? and rootId=?))";
				// String hql_getProId =
				// " from WaigouPlan where mopId in (select  A.id from ManualOrderPlan A join A.modSet B   where  B.procardId in(select id from  Procard where rootId = ? and procardStyle = '外购' )  and A.arkId = ?  )";
				String hql_getProId = " FROM WaigouPlan where mopId in (SELECT A.id FROM ManualOrderPlan A JOIN  A.modSet B   where   A.markId = ? and   B.procardId IN (select id from Procard where rootId = ? )  ) ";
				String str = "";
				if (procard.getWlstatus() != null
						&& procard.getWlstatus().length() > 0) {
					String[] strarray = procard.getWlstatus().split(",");
					if (strarray != null && strarray.length > 0) {
						String str1 = "";
						for (int j = 0; j < strarray.length; j++) {
							str1 += "," + "'" + strarray[j] + "'";
						}
						if (str1 != null && str1.length() > 1) {
							str1 = str1.substring(1);
							str += " and status in (" + str1 + ")";
						}
					}
					hql_getProId += str;
				}

				String hql_modId = "SELECT A.id FROM ManualOrderPlan A JOIN  A.modSet B   where   A.markId = ? and   B.procardId IN (select id from Procard where rootId = ? )";
				Integer modId = (Integer) totalDao.getObjectByCondition(
						hql_modId, procard_new.getMarkId(), rootId);
				// totalDao.query(hql_getProId,procard_new.getMarkId(),rootId);
				procard_new.setModId(modId);
				List<WaigouPlan> wgplanList = totalDao.query(hql_getProId,
						procard_new.getMarkId(), rootId);
				procard_new.setWaigouPlanList(wgplanList);
				Double nowgoodsCount = (Double) totalDao.getObjectByCondition(
						hql_nowgoods, procard_new.getMarkId(), procard_new
								.getKgliao());
				procard_new.setNowgoodsCount(nowgoodsCount);
				// 查询关联工序信息
				List<Procard> wgProcardList = totalDao
						.query(
								" from Procard where markId =? and rootId = ? and (sbStatus is null or sbStatus <> '删除') ",
								procard_new.getMarkId(), rootId);
				Set<String> processNoAndProcessNameSet = new HashSet<String>();
				for (Procard wgProcard : wgProcardList) {
					ProcessAndWgProcardTem pawp = null;
					if(wgProcard.getProcard()!=null){
						 pawp = (ProcessAndWgProcardTem) totalDao
							.getObjectByCondition(
									" from ProcessAndWgProcardTem where  wgprocardMardkId =? and  procardMarkId =? ",
									wgProcard.getMarkId(), wgProcard
											.getProcard().getMarkId());
					}
					if (pawp != null) {
						processNoAndProcessNameSet.add(pawp.getProcessNo()
								+ "-" + pawp.getProcessName());
					}
				}
				if (processNoAndProcessNameSet.size() > 0) {
					String processNames = processNoAndProcessNameSet.toString();
					processNames = processNames.substring(1, processNames
							.length() - 1);
					procard_new.setProcessNames(processNames);
				} else {
					procard_new.setProcessNames("无关联工序信息");
				}

				newList.add(procard_new);
			}
			// 需求数量
			String hql_filnalCount = "select sum(filnalCount) "
					+ hql
					+ " and rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float filnalCount = (Float) totalDao.getObjectByCondition(
					hql_filnalCount, rootId);
			// 采购数量
			String hql_cgNumber = "select sum(cgNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float cgNumber = (Float) totalDao.getObjectByCondition(
					hql_cgNumber, rootId);
			// 下单数量
			String hql_xdNumber = "select sum(outcgNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float xdNumber = (Float) totalDao.getObjectByCondition(
					hql_xdNumber, rootId);
			// 入库数量
			String hql_dhNumber = "select sum(dhNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null and dhNumber is not null ";
			Float dhNumber = (Float) totalDao.getObjectByCondition(
					hql_dhNumber, rootId);
			// 激活数量
			String hql_rkNumber = "select sum(tjNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float rkNumber = (Float) totalDao.getObjectByCondition(
					hql_rkNumber, rootId);
			// 领料数量
			String hql_llNumber = "select sum(filnalCount-hascount)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and hascount is not null ";
			Float llNumber = (Float) totalDao.getObjectByCondition(
					hql_llNumber, rootId);
			Object[] o = { newList, filnalCount, cgNumber, rootProcard,
					xdNumber, dhNumber, rkNumber, llNumber };
			return o;
		}
		return null;
	}

	@Override
	public Object[] findAllWlDetailList2(Integer rootId, Procard procard) {
		Procard rootProcard = (Procard) totalDao.getObjectById(Procard.class,
				rootId);
		if (rootProcard != null) {
			if (procard == null) {
				procard = new Procard();
			}
			String hql_sum = "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,sum(filnalCount),"
					+ "sum(cgNumber),sum(outcgNumber),sum(dhNumber),sum(tjNumber),sum(filnalCount-hascount),min(zzNumber),min(ztNumber),min(kcNumber),remark,wlqrtime ";
			// sum(tjNumber-(filnalCount-cgNumber)) == 入库
			String hql = totalDao.criteriaQueries(procard, null, "wgType",
					"wlstatus", "jihuoStatua");
			if (procard.getWgType() != null && procard.getWgType().length() > 0) {
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name=? ", procard.getWgType());
				getWgtype(category);
				hql += " and wgType in (" + strbu.toString().substring(1) + ")";
				strbu.setLength(0);
			}
			if (procard.getJihuoStatua() != null) {
				if ("未下单".equals(procard.getJihuoStatua())) {
					hql += " and  id   in ( select procardId from ManualOrderPlanDetail where 1=1 and( outcgNumber =0 or outcgNumber is null ) "
							+ "and procardId in (select id from Procard where rootId ="
							+ rootId + "))";
				} else if ("已下单".equals(procard.getJihuoStatua())) {
					hql += " and  id  in ( select procardId from ManualOrderPlanDetail where 1=1 and outcgNumber >0  "
							+ "and procardId in (select id from Procard where rootId ="
							+ rootId + "))";
				}
			}

			String hql_all = hql_sum
					+ hql
					+ " and rootid=? and procardStyle='外购' "
					+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit,remark order by wgType desc";
			List<Object[]> list = totalDao.query(hql_all, rootId);
			List newList = new ArrayList();
			for (int i = 0, len = list.size(); i < len; i++) {
				Object[] obj = list.get(i);
				String hql_nowgoods = " select sum(goodsCurQuantity)  from Goods where goodsClass = '外购件库' and  goodsMarkId = ? and kgliao =? "
						+ " and goodsCurQuantity >0 ";
				Procard procard_new = new Procard();
				if (obj[0] != null) {
					procard_new.setWgType(obj[0].toString());
				}
				procard_new.setMarkId(obj[1].toString());
				if (obj[2] != null) {
					procard_new.setBanBenNumber(obj[2].toString());
					hql_nowgoods += " and banBenNumber = '" + obj[2].toString()
							+ "'";
				} else {
					hql_nowgoods += " and (banBenNumber = '' or banBenNumber is null)";
				}
				if (obj[3] != null) {
					procard_new.setKgliao(obj[3].toString());
				}
				procard_new.setProName(obj[4].toString());
				if (obj[5] != null) {
					procard_new.setSpecification(obj[5].toString());
				}
				procard_new.setUnit(obj[6].toString());
				try {
					procard_new.setFilnalCount(Float.parseFloat(obj[7]
							.toString()));
				} catch (Exception e2) {
					procard_new.setFilnalCount(0f);
				}
				try {
					procard_new
							.setCgNumber(Float.parseFloat(obj[8].toString()));
				} catch (Exception e1) {
					procard_new.setCgNumber(0f);
				}

				try {
					procard_new.setOutcgNumber(Float.parseFloat(obj[9]
							.toString()));
				} catch (Exception e) {
					procard_new.setOutcgNumber(0f);
				}
				try {
					procard_new.setDhNumber(Float
							.parseFloat(obj[10].toString()));
				} catch (Exception e) {
					procard_new.setDhNumber(0F);
				}
				try {
					procard_new.setTjNumber(Float
							.parseFloat(obj[11].toString()));
				} catch (Exception e) {
					procard_new.setTjNumber(0F);
				}
				try {
					procard_new.setHascount(Float
							.parseFloat(obj[12].toString()));
				} catch (Exception e) {
					procard_new.setHascount(0F);
				}
				if (obj[13] == null) {
					obj[13] = "0";
				}
				procard_new.setZzNumber(Float.parseFloat(obj[13].toString()));
				if (obj[14] == null) {
					obj[14] = "0";
				}
				procard_new.setZtNumber(Float.parseFloat(obj[14].toString()));
				if (obj[15] == null) {
					obj[15] = "0";
				}
				procard_new.setKcNumber(Float.parseFloat(obj[15].toString()));
				if (obj[16] != null) {
					procard_new.setRemark(obj[16].toString());
				}
				if (null != obj[17]) {
					procard_new.setWlqrtime(obj[17].toString());
				}
				// wgType,markId,banBenNumber,kgliao,proName,specification,unit
				// String hql_getProId = "from WaigouPlan where id in "
				// +
				// "(select wgOrderId from ProcardWGCenter where procardId in "
				// + "(select id from Procard where markId=? and rootId=?))";
				// String hql_getProId =
				// " from WaigouPlan where mopId in (select  A.id from ManualOrderPlan A join A.modSet B   where  B.procardId in(select id from  Procard where rootId = ? and procardStyle = '外购' )  and A.arkId = ?  )";
				String hql_getProId = " FROM WaigouPlan where mopId in (SELECT A.id FROM ManualOrderPlan A JOIN  A.modSet B   where   A.markId = ? and   B.procardId IN (select id from Procard where rootId = ? )  )  and status!='取消'";
				String str = "";
				if (procard.getWlstatus() != null
						&& procard.getWlstatus().length() > 0) {
					String[] strarray = procard.getWlstatus().split(",");
					if (strarray != null && strarray.length > 0) {
						String str1 = "";
						for (int j = 0; j < strarray.length; j++) {
							str1 += "," + "'" + strarray[j] + "'";
						}
						if (str1 != null && str1.length() > 1) {
							str1 = str1.substring(1);
							str += " and status in (" + str1 + ")";
						}
					}
					hql_getProId += str;
				}

				String hql_modId = "SELECT A.id FROM ManualOrderPlan A JOIN  A.modSet B   where   A.markId = ? and   B.procardId IN (select id from Procard where rootId = ? )";
				Integer modId = (Integer) totalDao.getObjectByCondition(
						hql_modId, procard_new.getMarkId(), rootId);
				// totalDao.query(hql_getProId,procard_new.getMarkId(),rootId);
				procard_new.setModId(modId);
				List<WaigouPlan> wgplanList = totalDao.query(hql_getProId,
						procard_new.getMarkId(), rootId);
				procard_new.setWaigouPlanList(wgplanList);
				Double nowgoodsCount = (Double) totalDao.getObjectByCondition(
						hql_nowgoods, procard_new.getMarkId(), procard_new
								.getKgliao());
				procard_new.setNowgoodsCount(nowgoodsCount);
				if (procard_new.getCgNumber() != 0) {
					newList.add(procard_new);
				}
			}

			// 需求数量
			String hql_filnalCount = "select sum(filnalCount) "
					+ hql
					+ " and rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float filnalCount = (Float) totalDao.getObjectByCondition(
					hql_filnalCount, rootId);
			// 采购数量
			String hql_cgNumber = "select sum(cgNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float cgNumber = (Float) totalDao.getObjectByCondition(
					hql_cgNumber, rootId);
			// 下单数量
			String hql_xdNumber = "select sum(outcgNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float xdNumber = (Float) totalDao.getObjectByCondition(
					hql_xdNumber, rootId);
			// 入库数量
			String hql_dhNumber = "select sum(dhNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null and dhNumber is not null ";
			Float dhNumber = (Float) totalDao.getObjectByCondition(
					hql_dhNumber, rootId);
			// 激活数量
			String hql_rkNumber = "select sum(tjNumber)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and cgNumber is not null ";
			Float rkNumber = (Float) totalDao.getObjectByCondition(
					hql_rkNumber, rootId);
			// 领料数量
			String hql_llNumber = "select sum(filnalCount-hascount)  "
					+ hql
					+ " and  rootid=? and procardStyle='外购' and hascount is not null ";
			Float llNumber = (Float) totalDao.getObjectByCondition(
					hql_llNumber, rootId);
			Object[] o = { newList, filnalCount, cgNumber, rootProcard,
					xdNumber, dhNumber, rkNumber, llNumber };
			return o;
		}
		return null;
	}

	/****
	 * 查询待采购物料清单
	 * 
	 * @return
	 */
	@Override
	public Object[] findDqrWgPlanList(ManualOrderPlan manualPlan, int pageNo,
			int pageSize, String startDate, String endDate, String pageStatus,
			String wgTypes, String count1, String exportTag) {
		Users user = Util.getLoginUser();
		if(user == null){
			return new	Object[] { "请先登录！~",null, null, null, null, null, };
		}
		if (manualPlan == null) {
			manualPlan = new ManualOrderPlan();
		}
		String hql = totalDao.criteriaQueries(manualPlan, null, "wgType",
				"kgliao", "rootMarkId", "ywMarkId", "totalNum");
		if (count1 != null && !"".equals(count1)) {
			hql += " and ((outcgNumber is null and number >'" + count1
					+ "') or (number-outcgNumber) > '" + count1 + "')";
		}
		if (exportTag != null && !"".equals(exportTag)) {
			hql += " and cgNumber>0";
		}
		hql += " and ((outcgNumber is not null and number>1 and (number-outcgNumber) >0.05) or outcgNumber is null or number<=1)";
		String kgliao = manualPlan.getKgliao();
		if (kgliao != null && kgliao.length() > 0) {
			hql += " and kgliao='" + kgliao + "'";
		}
		String yclSql = "";
		String tag = "sdxd";
		if (wgTypes != null && wgTypes.length() > 0) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgTypes);
			if (category != null) {
				getWgtype(category);
			}
			if (strbu.length() > 1) {
				strbu = new StringBuffer(strbu.toString().substring(1));
			}
			yclSql = " and wgType in ('" + wgTypes + "'," + strbu.toString()
					+ ")";
			pageNo = 0;
			pageSize = 0;
			tag = "sdxd";
		}

		hql += yclSql
				+ " and number is not null and number >0 and (outcgNumber is null or number-outcgNumber>0.005) and (status is null or status <> '取消' )";
		String sonhql = "";
		if (manualPlan.getRootMarkId() != null
				&& manualPlan.getRootMarkId().length() > 0) {
			sonhql += " and id in (select manualPlan.id from ManualOrderPlanDetail where rootMarkId like '%"
					+ manualPlan.getRootMarkId() + "%' ";
		}
		if (manualPlan.getYwMarkId() != null
				&& manualPlan.getYwMarkId().length() > 0) {
			if (sonhql != null && sonhql.length() > 0) {
				sonhql += " and ywMarkId like '%" + manualPlan.getYwMarkId()
						+ "%'";
			} else {
				sonhql += " and id in (select manualPlan.id from ManualOrderPlanDetail where ywMarkId like '%"
						+ manualPlan.getYwMarkId() + "%' ";
			}
		}
		if (sonhql != null && sonhql.length() > 0) {
			sonhql += ")";
		}
		if (startDate != null && startDate.length() > 0) {
			sonhql += " and addtime>='" + startDate + "'";
		}
		if (endDate != null && endDate.length() > 0) {
			sonhql += " and addtime<='" + endDate + "'";
		}
		hql += sonhql;
		if ("fl".equals(pageStatus)) {
			hql += " and category = '辅料'";
		} else {
			hql += " and (category <> '辅料' or category is null)";
		}
		if (manualPlan.getTotalNum() != null
				&& manualPlan.getTotalNum().length() > 0) {
			String TotalNum2 = manualPlan.getTotalNum().replaceAll("；", ";");
			if (TotalNum2.indexOf(";") > 0) {// 证明有多条
				String TotalNum1 = "'" + TotalNum2.replaceAll(";", "','") + "'";
				hql += " and id in (select manualPlan.id from ManualOrderPlanDetail where planTotal.id in (select id from ManualOrderPlanTotal where totalNum in ("
						+ TotalNum1 + ")))";
			} else {
				hql += " and id in (select manualPlan.id from ManualOrderPlanDetail where planTotal.id in (select id from ManualOrderPlanTotal where totalNum like '%"
						+ manualPlan.getTotalNum() + "%'))";
			}
		}
		hql += " order by markId,number desc";
		int count = totalDao.getCount(hql);
		if ((wgTypes != null && wgTypes.length() > 0)
				|| (manualPlan.getRootMarkId() != null && manualPlan
						.getRootMarkId().length() > 0)
				|| (manualPlan.getYwMarkId() != null && manualPlan
						.getYwMarkId().length() > 0)
				|| (manualPlan.getMarkId() != null && manualPlan.getMarkId()
						.length() > 0)
				|| (manualPlan.getProName() != null && manualPlan.getProName()
						.length() > 0)
				|| (startDate != null && startDate.length() > 0)
				|| (endDate != null && endDate.length() > 0)
				|| (manualPlan.getKgliao() != null && manualPlan.getKgliao()
						.length() > 0)
				|| (manualPlan.getTotalNum() != null && manualPlan
						.getTotalNum().length() > 0)) {
			pageNo = 1;
			pageSize = count;
		}

		List list = totalDao.findAllByPage(hql, pageNo, pageSize);

		List list_new = new ArrayList();
		if (list != null) {
			for (int i = 0, len = list.size(); i < len; i++) {
				ManualOrderPlan nowmop = (ManualOrderPlan) list.get(i);
				if (nowmop == null
						|| (nowmop != null && nowmop.getKgliao() == null)) {// 供料属性为空sum查询会报错
					continue;
				}
				// 查询是否存在单价
				String time = Util.getDateTime("yyyy-MM-dd");
				String hql_price = "from Price where partNumber=? and kgliao=? and '"
						+ time
						+ "'>= pricePeriodStart and ('"
						+ time
						+ "'< pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
				if (nowmop.getBanben() != null
						&& nowmop.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + nowmop.getBanben() + "'";
				} else {
					hql_price += " and(banbenhao='' or banbenhao is null)";
				}
				Integer pricecount = totalDao.getCount(hql_price, nowmop
						.getMarkId(), nowmop.getKgliao());
				if (pricecount == null) {
					pricecount = 0;
				} else {
					// 存在单价，查询是否存在采购比例(并且拉黑供应商剔除)
					String hql_gys = " from GysMarkIdjiepai where markId =? and kgliao=? and cgbl>0 and zhuserid in (select gysId "
							+ hql_price
							+ ") and zhuserid not in (select id from ZhUser where blackliststauts='拉黑')";
					if (nowmop.getBanben() != null
							&& nowmop.getBanben().length() > 0) {
						hql_gys += " and banBenNumber='" + nowmop.getBanben()
								+ "'";
					} else {
						hql_gys += " and (banBenNumber = '' or banBenNumber is null)";
					}
					// Integer ghblCount = totalDao.getCount(hql_gys, nowProcard
					// .getMarkId(), nowProcard.getKgliao(), nowProcard
					// .getMarkId(), nowProcard.getKgliao());
					Float cgbl = (Float) totalDao.getObjectByCondition(
							"select sum(cgbl) " + hql_gys, nowmop.getMarkId(),
							nowmop.getKgliao(), nowmop.getMarkId(), nowmop
									.getKgliao());
					if (cgbl == null) {
						cgbl = 0F;
					}
					Price flprice = null;
					if ("辅料".equals(nowmop.getCategory())) {
						cgbl = 100f;
						hql_price += " and  productCategory ='辅料'";
						flprice = (Price) totalDao.getObjectByCondition(
								hql_price, nowmop.getMarkId(), nowmop
										.getKgliao());
					}
					nowmop.setCgbl(cgbl);
					Float nowNum = (nowmop.getNumber() - (nowmop
							.getOutcgNumber() == null ? 0 : nowmop
							.getOutcgNumber()));
					String gyscodeAndNum = "";// gys配额Id:分配数量;gysId:分配数量
					nowmop.setNumber(nowNum);
					if (cgbl > 0
							&& cgbl == 100
							&& (nowmop.getIsuse() == null
									|| "".equals(nowmop.getIsuse()) || "待"
									.equals(nowmop.getIsuse()))) {//
						List<GysMarkIdjiepai> gysList = totalDao.query(hql_gys,
								nowmop.getMarkId(), nowmop.getKgliao(), nowmop
										.getMarkId(), nowmop.getKgliao());
						if (gysList != null && gysList.size() > 0) {
							for (int j = 0; j < gysList.size(); j++) {
								GysMarkIdjiepai gys = gysList.get(j);
								Float fpNum = nowNum * (gys.getCgbl() / 100);
								Price price = (Price) totalDao
										.getObjectByCondition(hql_price
												+ " and gysId = "
												+ gys.getZhuserId(), nowmop
												.getMarkId(), nowmop
												.getKgliao());
								ZhUser zhuser = (ZhUser) totalDao.get(
										ZhUser.class, gys.getZhuserId());
								if (price != null) {
									Float zdqdl = 0F;
									if (price.getZdqdl() != null
											&& price.getZdqdl() > 0) {
										zdqdl = price.getZdqdl();
									}
									Float zdzxl = 0F;
									if (price.getZdzxl() != null
											&& price.getZdzxl() > 0) {
										Double counts = Math.ceil(fpNum
												.doubleValue()
												/ price.getZdzxl()
														.doubleValue());
										zdzxl = counts.floatValue()
												* price.getZdzxl();
									}
									Float temp = zdqdl;
									if (zdqdl < zdzxl) {
										temp = zdzxl;
									}

									if (temp != null) {
										if (fpNum < temp) {
											nowmop.setMoqNum(temp);
											gyscodeAndNum += ";"
													+ zhuser.getUsercode()
													+ ":" + temp;
										} else {
											gyscodeAndNum += ";"
													+ zhuser.getUsercode()
													+ ":" + fpNum;
										}
										if (temp > 0) {
											nowmop.setIsuse("待");
										}
									}
								}
							}
						} else if (flprice != null) {
							ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class,
									flprice.getGysId());
							Float zdqdl = 0F;
							if (flprice.getZdqdl() != null
									&& flprice.getZdqdl() > 0) {
								zdqdl = flprice.getZdqdl();
							}
							Float zdzxl = 0F;
							if (flprice.getZdzxl() != null
									&& flprice.getZdzxl() > 0) {
								Double counts = Math.ceil(nowNum.doubleValue()
										/ flprice.getZdzxl().doubleValue());
								zdzxl = counts.floatValue()
										* flprice.getZdzxl();
							}
							Float temp = zdqdl;
							if (zdqdl < zdzxl) {
								temp = zdzxl;
							}

							if (temp != null) {
								if (nowNum < temp) {
									nowmop.setMoqNum(temp);
									gyscodeAndNum += ";" + zhuser.getUsercode()
											+ ":" + temp;
								} else {
									gyscodeAndNum += ";" + zhuser.getUsercode()
											+ ":" + nowNum;
								}
								if (temp > 0) {
									nowmop.setIsuse("待");
								}
							}
						}
						if (gyscodeAndNum != null && gyscodeAndNum.length() > 0) {
							gyscodeAndNum = gyscodeAndNum.substring(1);
							nowmop.setGyscodeAndNum(gyscodeAndNum);
						}
					}
				}
				// 判断是否拥有单张重量
				mopbili(nowmop);
				nowmop.setPrice(pricecount.floatValue());
				if (nowmop.getNumber() > 0) {
					list_new.add(nowmop);
				}
				List<Float> allCount = new ArrayList<Float>();
				// // 占用量
				// allCount.add(zyCount);
				// // 在途量
				// allCount.add(ztCount);
				// // 查询库存数量
				// allCount.add(kcCount);
				// allCount.add(kcCountAVl);
				// allCount.add(kcCountPrics);
				// allCount.add(kcCountCs);
				// 单价
				allCount.add(nowmop.getPrice());
				allCount.add(nowmop.getCgbl());
			}
		}
		Float sumCgNumber = 0f;
		if (list_new != null && list_new.size() > 0) {
			for (Object obj : list_new) {
				ManualOrderPlan manualPlan0 = (ManualOrderPlan) obj;
				sumCgNumber += manualPlan0.getNumber();
			}
		}
		strbu = new StringBuffer();
		Object[] o = { "true",list_new, count, pageSize, tag, sumCgNumber};
		return o;
	}

	/****
	 * 添加采购订单
	 * 
	 * @param procardIds
	 * @return
	 */
	@Override
	public String addWgOrder(Integer[] procardIds,String pageStatus) {
		String message = "";
		if (procardIds != null && procardIds.length > 0) {
			String ids = "0";
			// 存储汇总采购数量 由ManualOrderPlan这个汇总采购数量
			// Map<String, Float> map_allWgNum = new HashMap<String, Float>();
			// for (Integer procardId : procardIds) {
			// Procard procard = (Procard) totalDao.getObjectById(
			// Procard.class, procardId);
			// ManualOrderPlan manualPlan = null;
			// if(procard == null){
			// manualPlan = (ManualOrderPlan)
			// totalDao.get(ManualOrderPlan.class, procardId);
			// if(manualPlan!=null){
			// procard = new Procard();
			// procard.setMarkId(manualPlan.getMarkId());//件号
			// procard.setProName(manualPlan.getProName());//零件名称
			// procard.setSpecification(manualPlan.getSpecification());//规格
			// procard.setWgType(manualPlan.getWgType());//物料类别
			// procard.setKgliao(manualPlan.getKgliao());//供料属性
			// procard.setBanBenNumber(manualPlan.getBanben());//版本号
			// procard.setBanci(manualPlan.getBanci());//版次
			// procard.setUnit(manualPlan.getUnit());//单位
			// procard.setCgNumber(manualPlan.getNumber());//采购数量
			// procard.setManualPlanId(manualPlan.getId());//手动下单Id
			// procard.setOutOrderNum(manualPlan.getGroups());//来源
			// procard.setOutcgNumber(manualPlan.getOutcgNumber());
			// }
			// }
			// if (procard != null) {
			// Float outcgNumber = 0f;
			// if(procard.getOutcgNumber()!=null){
			// outcgNumber = procard.getOutcgNumber();
			// }
			// if ((procard.getCgNumber()-outcgNumber) > 0 ) {
			// if (procard.getBanBenNumber() == null
			// || "".equals(procard.getBanBenNumber())
			// || "null".equals(procard.getBanBenNumber())) {
			// procard.setBanBenNumber("");
			// }
			// if (procard.getBanci() == null
			// || "".equals(procard.getBanci())) {
			// procard.setBanci(0);
			// }
			// if (procard.getProName() == null
			// || "".equals(procard.getProName())) {
			// procard.setProName("");
			// }
			// if (procard.getSpecification() == null
			// || "".equals(procard.getSpecification())
			// || "null".equals(procard.getSpecification())) {
			// procard.setSpecification("");
			// }
			// if (procard.getTuhao() == null
			// || "".equals(procard.getTuhao())
			// || "null".equals(procard.getTuhao())) {
			// procard.setTuhao("");
			// }
			//
			// // 件号+供货属性+版次 作为区分键值
			// String keys = procard.getMarkId() + ";"
			// + procard.getKgliao() + ";"
			// + procard.getBanBenNumber() + ";"
			// + procard.getBanci() + ";" + procard.getUnit()
			// + ";" + procard.getWgType() + ";"
			// + procard.getProName() + ";"
			// + procard.getSpecification() + ";"
			// + procard.getTuhao();
			// Float allnum = map_allWgNum.get(keys);
			// if (allnum == null) {
			// allnum = 0F;
			// }
			// if (procard.getOutcgNumber() == null) {
			// procard.setOutcgNumber(procard.getCgNumber());
			// allnum += procard.getCgNumber();
			// } else {
			// allnum += procard.getCgNumber()
			// - procard.getOutcgNumber();
			// procard.setOutcgNumber(procard.getCgNumber());
			// }
			//
			// map_allWgNum.put(keys, allnum);
			// } else {
			// if(procard.getId() == null && manualPlan!=null){
			// manualPlan.setCgstatus("入库");
			// manualPlan.setNumber(0f);
			// totalDao.update(manualPlan);
			// }else{
			// procard.setCgNumber(0F);
			// procard.setTjNumber(procard.getNeedCount());
			// procard.setWlstatus("入库");
			// totalDao.update(procard);
			// }
			//						
			// }
			// }
			// }
			Users user = Util.getLoginUser();
			if(user == null){
				return "请先登录!~";
			}
			String functionName = "待采购物料管理";
			if("fl".equals(pageStatus)){
				functionName = "辅料待采购管理";
			}
			if(!ispower(null, functionName)){
				return  "抱歉,"+user.getName()+"无此操作权限!~";
			}
			StringBuffer errormsg = new StringBuffer();
			Map<Integer, WaigouOrder> wgOrderMap = new HashMap<Integer, WaigouOrder>();
			for (Integer mopId : procardIds) {
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, mopId);
				String gysCode = "";
				// 判断是否拥有单张重量
				mopbili(mop);
				String markId = mop.getMarkId();// 件号
				String kgliao = mop.getKgliao();// 供料属性
				String banben = mop.getBanben();// 版本
				Integer banci = mop.getBanci();// 产品版次
				String unit = mop.getUnit();// 单位
				String wgType = mop.getWgType();// 物料类别
				// String ywmarkId = key[6];// 业务件号
				String proName = "";// 名称
				String specification = "";
				try {
					proName = mop.getProName();
					specification = mop.getSpecification();
				} catch (Exception e) {
				}
				String tuhao = "";
				try {
					tuhao = mop.getTuhao();
				} catch (Exception e) {
				}
				Float outcgNumber = mop.getOutcgNumber();
				if (outcgNumber == null) {
					outcgNumber = 0f;
				}
				Float wgNum = mop.getNumber() - outcgNumber;// 采购数量
				Float bjNum = mop.getNumber() - outcgNumber;// 标记数量;

				if (wgNum <= 0) {
					continue;
				}
				String time = Util.getDateTime("yyyy-MM-dd");
				String hql_price = "from Price where partNumber=? and kgliao=? and '"
						+ time
						+ "'>= pricePeriodStart and ('"
						+ time
						+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
				if (mop.getBanben() != null && mop.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + mop.getBanben() + "'";
				}
				// 查询件号对应供应商采购比例
				String hql_gys = " from GysMarkIdjiepai where markId =? and kgliao=? and cgbl>0 and zhuserid in (select gysId "
						+ hql_price + ")";
				if (mop.getBanben() != null && mop.getBanben().length() > 0) {
					hql_gys += " and banBenNumber='" + mop.getBanben() + "'";
				}

				Float cgbl = (Float) totalDao.getObjectByCondition(
						"select sum(cgbl) " + hql_gys, markId, kgliao, markId,
						kgliao);
				if (cgbl != 100F) {
					message += markId + "的供货比例总数不等于100%!请调整配额后再进行下单!\\n";
					continue;
				}
				List gys_list = totalDao.query(hql_gys + " order by cgbl desc",
						markId, kgliao, markId, kgliao);

				if (gys_list != null && gys_list.size() > 0) {
					for (int i = 0; i < gys_list.size(); i++) {
						if (bjNum <= 0) {
							break;
						}
						GysMarkIdjiepai gysMarkid = (GysMarkIdjiepai) gys_list
								.get(i);
						Float cgNumber = wgNum * gysMarkid.getCgbl() / 100;
						// 是否启用MOQ MPQ
						ZhUser zhuser = (ZhUser) totalDao.getObjectById(
								ZhUser.class, gysMarkid.getZhuserId());
						Users gysUser = (Users) totalDao.getObjectById(
								Users.class, zhuser.getUserid());
						if ("YES".equals(mop.getIsuse())) {
							boolean bool = true;
							if (mop.getGyscodeAndNum() != null
									&& mop.getGyscodeAndNum().length() > 0) {
								String[] strs = mop.getGyscodeAndNum().split(
										";");
								for (int j = 0; j < strs.length; j++) {
									String[] gyscodeAndNum = strs[j].split(":");
									if (zhuser.getUsercode().equals(
											gyscodeAndNum[0])) {
										cgNumber = Float
												.parseFloat(gyscodeAndNum[1]);
										wgNum -= cgNumber;
										bool = false;
										break;
									}
								}
								if (bool) {
									continue;
								}
							}
						}

						// 供应商信息查询
						Users loginUser = Util.getLoginUser();
						if(loginUser == null){
							return "请先登录!~";
						}
						if (gysUser != null && gysUser != null) {
							WaigouOrder waiGouOrder = wgOrderMap.get(zhuser
									.getId());
							/***************** 采购订单处理 ********/
							Set<WaigouPlan> wwpSet = new HashSet<WaigouPlan>();
							if (waiGouOrder == null) {
								waiGouOrder = addWaigouOrder(mop, zhuser,
										gysUser, loginUser);
							} else {
								wwpSet = waiGouOrder.getWwpSet();
							}
							/************* 订单明细处理 ************/
							/************** 查询产品单价 ********/
							List<Price> list_price = totalDao.query(hql_price
									+ "  and gysId=?   order by firstnum",
									markId, kgliao, zhuser.getId());
							Price price = null;
							for (int j = 0; j < list_price.size(); j++) {
								price = list_price.get(j);
								// if (j == 0) {
								// // 0，0的表示没有阶梯单价
								// if (price.getFirstnum() == price
								// .getEndnum()
								// && price.getFirstnum() == 0) {
								// break;
								// } else if (price.getFirstnum() >= cgNumber) {
								// // 第一条的起始数量大于本次采购数量,自动升级为起始数量
								// cgNumber = price.getFirstnum()
								// .floatValue();
								// break;
								// }
								// }
								if (price.getEndnum() != null
										&& price.getEndnum() >= cgNumber) {
									break;
								}
							}
							if (price == null) {
								return "未找到件号:" + mop.getMarkId() + "供应商为"
										+ zhuser.getName() + "的价格。";
							}

							if ("pcs".equals(unit) || "PCS".equals(unit)
									|| "个".equals(unit)) {
								Double newnum = Math.ceil(cgNumber);
								cgNumber = newnum.floatValue();
								if (i == gys_list.size() - 1
										&& gysMarkid.getCgbl() < 100) {
									cgNumber = bjNum;
								}
							}
							bjNum -= cgNumber;

							// if ("KG".equals(unit) || "G".equals(unit)
							// || "kg".equals(unit)
							// || "g".equals(unit)
							// || "千克".equals(unit)
							// || "克".equals(unit)
							// || "公斤".equals(unit)
							// || "斤".equals(unit)) {
							// } else {
							// Double newnum = Math.ceil(cgNumber);
							// cgNumber = newnum.floatValue();
							// }
							String msg = isOverflowMaxkc(markId, banben,
									kgliao, cgNumber);
							if (!"true".equals(msg)) {
								throw new RuntimeException(msg);
							}
							// 2、添加订单明细
							WaigouPlan waigouPlan = addWaigouPlan(mop, markId,
									kgliao, banben, banci, unit, wgType,
									proName, specification, tuhao, gysMarkid,
									cgNumber, zhuser, gysUser, waiGouOrder,
									wwpSet, price);
							wgOrderMap.put(zhuser.getId(), waiGouOrder);
							// 1、 采购订单和物料需求表关系维护（一对多）
							// String hql_procard =
							// "from Procard where markId=? and kgliao=? and id in("
							// + ids + ") order by selfCard";
							// List list_procard = totalDao.query(hql_procard,
							// markId, kgliao);
							// for (int j = 0; j < list_procard.size(); j++) {
							// Procard procard = (Procard) list_procard
							// .get(j);
							// ProcardWGCenter pwg = new ProcardWGCenter();
							// pwg.setConnectionType("mTom");
							// pwg.setProcardId(procard.getId());
							// pwg.setWgOrderId(waigouPlan.getId());
							// pwg.setProcardCount(procard.getCgNumber());
							// totalDao.save(pwg);
							// procard.setWlstatus("待确定");
							// procard.setOutcgNumber(procard
							// .getCgNumber());
							// totalDao.update(procard);
							// }
							// 1、 采购订单和物料需求表关系维护（一对多）
							if (mop.getOutcgNumber() != null) {
								mop.setOutcgNumber(mop.getOutcgNumber()
										+ cgNumber);
							} else {
								mop.setOutcgNumber(cgNumber);
							}
							mop.setStatus("已采购");
							mop.setCaigouTime(Util.getDateTime());
							if (mop.getWshCount() != null) {
								mop.setWshCount(mop.getWshCount() + cgNumber);
							} else {
								mop.setWshCount(cgNumber);
							}
							totalDao.update(mop);

							Set<ManualOrderPlanDetail> modSet = mop.getModSet();
							Float fenpeiNum = cgNumber;
							for (ManualOrderPlanDetail mod : modSet) {
								if (mod.getCgnumber() > 0) {
									if (fenpeiNum <= 0) {
										break;
									}
									if (mod.getOutcgNumber() == null
											|| mod.getOutcgNumber() == 0) {
										if (mod.getCgnumber() >= fenpeiNum) {
											mod.setOutcgNumber(fenpeiNum);
											fenpeiNum = 0f;
										} else {
											mod.setOutcgNumber(mod
													.getCgnumber());
											fenpeiNum = fenpeiNum
													- mod.getCgnumber();
										}
									} else {
										if (mod.getCgnumber() > mod
												.getOutcgNumber()) {
											if (fenpeiNum > (mod.getCgnumber() - mod
													.getOutcgNumber())) {
												fenpeiNum = fenpeiNum
														- (mod.getCgnumber() - mod
																.getOutcgNumber());
												mod.setOutcgNumber(mod
														.getCgnumber());
											} else {
												mod.setOutcgNumber(mod
														.getOutcgNumber()
														+ fenpeiNum);
												fenpeiNum = 0f;
											}
										}
									}
									if (mod.getProcardId() != null) {
										Procard procard = (Procard) totalDao
												.get(Procard.class, mod
														.getProcardId());
										if (procard != null) {
											procard.setOutcgNumber(mod
													.getOutcgNumber());
											procard.setWlstatus("待确定");
											totalDao.update(procard);
										} else {
											errormsg
													.append("<span style='color:red'>"
															+ "业务件号："
															+ mod.getYwMarkId()
															+ " 总成批次:"
															+ mod
																	.getRootSelfCard()
															+ "下，外购件:"
															+ mod.getMarkId()
															+ "已设变删除，该需求明细未处理，请联系相关人员处理，谢谢!~ </span><br/>");
										}
									}
									mod.setStatus("已采购");
									totalDao.update(mod);
								}
							}
							List<Integer> productIdList = totalDao
									.query(
											"select productId from ManualOrderPlanDetail where manualPlan.id =? and type = '预测订单' group by productId ",
											mop.getId());
							/** 如果物料需求明细中有预测订单部分的，处理预测订单回传状态 */
							if (productIdList != null
									&& productIdList.size() > 0) {
								for (Integer productId : productIdList) {
									YcProduct ycproduct = (YcProduct) totalDao
											.getObjectByCondition(
													" from YcProduct where productId = ?",
													productId);
									Float sum_zcNum = (Float) totalDao
											.getObjectByCondition(
													"select min(A.sum_zcNum)  from (select sum(zcNum)  sum_zcNum from YcWaiGouProcrd where ycProductId = ? and mopdId in(select id from  ManualOrderPlanDetail where outcgNumber >0 and type = '预测订单' ) group by procardTId )A ",
													ycproduct.getId());
									if (sum_zcNum == ycproduct.getNum()) {
										ycproduct.setStatus("完成");
									}
									ycproduct.setYcgNum(sum_zcNum);
									totalDao.update(ycproduct);
								}
							}

							// waiGouOrder.setStatus("待审核");
							waiGouOrder.setApplystatus("未申请");
							Integer epId = null;
							
							// if (waiGouOrder.getType().equals("外购")) {
							// // 外购采购订单进入审批
							// try {
							// epId = CircuitRunServerImpl
							// .createProcess(
							// "外购采购订单申请",
							// WaigouOrder.class,
							// waiGouOrder.getId(),
							// "applystatus",
							// "id",
							// "WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=findAll&id="
							// + waiGouOrder
							// .getId(),
							// "外购采购订单申请", true);
							// if (epId != null && epId > 0) {
							// waiGouOrder.setEpId(epId);
							// CircuitRun circuitRun = (CircuitRun) totalDao
							// .get(CircuitRun.class, epId);
							// if ("同意".equals(circuitRun
							// .getAllStatus())
							// && "审批完成".equals(circuitRun
							// .getAuditStatus())) {
							// waiGouOrder
							// .setApplystatus("同意");
							// } else {
							// waiGouOrder
							// .setApplystatus("未审批");
							// }
							// totalDao.update(waiGouOrder);
							// }
							// } catch (Exception e) {
							// message += "审批流程有误,请联系管理员!\\n";
							// }
							// } else {
							// 外委采购订单进入审批
							// try {
							// epId = CircuitRunServerImpl
							// .createProcess(
							// "外委采购订单申请",
							// WaigouOrder.class,
							// waiGouOrder.getId(),
							// "applystatus",
							// "id",
							// "WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=findAll&id="
							// + waiGouOrder
							// .getId(),
							// "外委采购订单申请", true);
							// } catch (Exception e) {
							// message = "审批流程有误,请联系管理员!";
							// }
							// }
							// CircuitRun circuitRun = (CircuitRun) totalDao
							// .getObjectById(CircuitRun.class, epId);
							// waiGouOrder.setEpId(circuitRun.getId());
							totalDao.update(waigouPlan);
						}
					}
				} else {
					message += markId + "没有供应商信息\\n";
				}
			}
		} else {
			message = "请至少选择一项!";
		}
		return message;
	}

	/**
	 * 添加订单明细
	 * 
	 * @param mop
	 * @param markId
	 * @param kgliao
	 * @param banben
	 * @param banci
	 * @param unit
	 * @param wgType
	 * @param proName
	 * @param specification
	 * @param tuhao
	 * @param gysMarkid
	 * @param cgNumber
	 * @param zhuser
	 * @param gysUser
	 * @param waiGouOrder
	 * @param wwpSet
	 * @param price
	 * @return
	 */
	private WaigouPlan addWaigouPlan(ManualOrderPlan mop, String markId,
			String kgliao, String banben, Integer banci, String unit,
			String wgType, String proName, String specification, String tuhao,
			GysMarkIdjiepai gysMarkid, Float cgNumber, ZhUser zhuser,
			Users gysUser, WaigouOrder waiGouOrder, Set<WaigouPlan> wwpSet,
			Price price) {
		WaigouPlan waigouPlan = new WaigouPlan();
		waigouPlan.setMarkId(markId);
		waigouPlan.setBanben(banben);
		waigouPlan.setBanci(banci);
		waigouPlan.setUnit(unit);
		waigouPlan.setProName(proName);
		waigouPlan.setSpecification(specification);
		waigouPlan.setTuhao(tuhao);
		waigouPlan.setType(mop.getCategory());
		waigouPlan.setWwType(mop.getCategory());
		waigouPlan.setProNumber(mop.getProNumber());// 项目编号
		waigouPlan.setKgliao(kgliao);
		waigouPlan.setNumber(cgNumber);// 采购数量*供应商采购比例
		waigouPlan.setSyNumber(cgNumber);// 送货数量*供应商采购比例
		waigouPlan.setGysId(zhuser.getId());// 供应商id
		waigouPlan.setGysName(zhuser.getCmp());// 供应商名称
		waigouPlan.setUserId(gysUser.getId());
		waigouPlan.setUserCode(gysUser.getCode());
		waigouPlan.setDemanddept(mop.getDemanddept());// 需求部门 (辅料使用)
		// 可能出现供应商简称于与供应商全称不等情况，所以再查一次。
		if (waiGouOrder.getGysId() != null) {
			ZhUser zz = (ZhUser) totalDao.getObjectById(ZhUser.class,
					waiGouOrder.getGysId());
			waigouPlan.setGysjc(zz.getName());// 供应商简称
			waigouPlan.setWlWeizhiDt("cgbl");// 表示按采购比例下单
		} else {
			waigouPlan.setGysjc(zhuser.getName());// 供应商简称
		}
		try {
			waigouPlan.setPrice(price.getBhsPrice());
			waigouPlan.setHsPrice(price.getHsPrice());
			waigouPlan.setTaxprice(price.getTaxprice());
			waigouPlan.setMoney(waigouPlan.getHsPrice()
					* waigouPlan.getNumber());
			waigouPlan.setPriceId(price.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("价格异常,请联系管理员!");
		}

		/************** 查询产品单价 **********/
		waigouPlan.setAddTime(Util.getDateTime());
		// wwp.setShArrivalTime(procard_wg
		// .getNeedFinalDate());//应到货时间在采购确认通知后计算
		waigouPlan.setStatus("待通知");
		waigouPlan.setAllJiepai(gysMarkid.getAllJiepai());// 单件总节拍
		waigouPlan.setDeliveryDuration(gysMarkid.getDeliveryDuration());// 耽误时长
		waigouPlan.setSingleDuration(gysMarkid.getSingleDuration());// 单班时长(工作时长)
		waigouPlan.setWaigouOrder(waiGouOrder);
		waigouPlan.setWgType(wgType);// 物料类别
		// waigouPlan.setYwmarkId(ywmarkId);// 业务件号
		waigouPlan.setShArrivalTime(mop.getNeedFinalDate());
		waigouPlan.setJiaofuTime(mop.getNeedFinalDate());
		waigouPlan.setMopId(mop.getId());
		if (waiGouOrder.getAllMoney() != null) {
			waiGouOrder.setAllMoney(Util.MacthRound(
					waigouPlan.getMoney() + waiGouOrder.getAllMoney(), 2));
		} else {
			waiGouOrder.setAllMoney(waigouPlan.getMoney());// 
		}
		waiGouOrder.setWwpSet(wwpSet);
		// 得到最大的采购订单编号
		waiGouOrder.setPlanNumber(osNumber());// 采购订单编号

		// 采购价格二次校验功能
		Float moeny = (float) (waigouPlan.getHsPrice() * cgNumber);
//		if (!moeny.equals(waigouPlan.getMoney())) {
//			throw new RuntimeException("件号:" + waigouPlan.getMarkId()
//					+ "含税总额计算异常。单价:" + waigouPlan.getHsPrice() + "+采购数量:"
//					+ cgNumber + "=总额:" + waigouPlan.getMoney()
//					+ "请验证并立即联系管理员!~。");
//		}
		wwpSet.add(waigouPlan);
		totalDao.save(waiGouOrder);
		return waigouPlan;
	}

	/**
	 * add订单对象
	 * 
	 * @param mop
	 * @param zhuser
	 * @param gysUser
	 * @param loginUser
	 * @return
	 */
	private WaigouOrder addWaigouOrder(ManualOrderPlan mop, ZhUser zhuser,
			Users gysUser, Users loginUser) {
		WaigouOrder waiGouOrder;
		waiGouOrder = new WaigouOrder();
		waiGouOrder.setGysId(zhuser.getId());// 供应商id
		waiGouOrder.setUserId(gysUser.getId());
		waiGouOrder.setPayType(zhuser.getFkfs());// 付款方式
		/****** 订单表头 **********/
		waiGouOrder.setUserCode(zhuser.getUsercode());
		waiGouOrder.setGysName(zhuser.getCmp());
		waiGouOrder.setGhAddress(zhuser.getCompanydz());
		waiGouOrder.setLxPeople(zhuser.getCperson());
		waiGouOrder.setGysPhone(zhuser.getCmobile() + "/" + zhuser.getCfax()
				+ "/" + zhuser.getCtel());
		waiGouOrder.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
		waiGouOrder.setAddTime(Util.getDateTime());// 添加时间
		waiGouOrder.setAddUserCode(loginUser.getCode());
		waiGouOrder.setAddUserName(loginUser.getName());
		waiGouOrder.setPayType(zhuser.getFkfs());
		waiGouOrder.setPiaojuType(zhuser.getZzsl());
		// 获得网站配置信息
		CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
				.getApplication().get("companyInfo");
		if (companyInfo != null) {
			waiGouOrder.setShAddress(companyInfo.getAddress());
		}

		waiGouOrder.setStatus("待核对");
		waiGouOrder.setType(mop.getCategory());
		waiGouOrder.setWwType(mop.getCategory());
		waiGouOrder.setAddUserPhone(loginUser.getPassword().getPhoneNumber());
		return waiGouOrder;
	}

	/****
	 * 外购订单查询列表
	 * 
	 * @param WaigouOrder
	 * @param pageNo
	 * @param pageSize
	 * @param pageStatus
	 * @return
	 */
	@Override
	public Object[] findWgOrderList(WaigouOrder wgOrder, int pageNo,
			int pageSize, String pageStatus, String tag) {
		if (wgOrder == null) {
			wgOrder = new WaigouOrder();
		}
		String status = "";
		if ("sbdqr".equals(pageStatus)) {
			status = " and id in (select waigouOrder.id from WaigouPlan where sbjdApplyCount is not null and sbjdApplyCount>0)";
		} else if ("gyssbdqr".equals(pageStatus)) {
			status = " and id in (select waigouOrder.id from WaigouPlan where sbjdApplyCount is not null and sbjdApplyCount>0) and userId="
					+ Util.getLoginUser().getId();
		} else if ("dsq".equals(pageStatus)) {
			status = " and status in ('待核对','待审批','审批中','待通知') and applystatus in('未申请','打回')";
		} else if ("dtz".equals(pageStatus)) {
			status = " and status='待通知' and applystatus = '同意'";
		} else if ("dqr".equals(pageStatus)) {
			status = " and status='待确认' and addUserCode='"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("findAllself".equals(pageStatus)) {
			status = " and addUserCode='" + Util.getLoginUser().getCode() + "'";
		} else if ("findAll".equals(pageStatus)) {
			// 查询所有
		} else if ("gysnew".equals(pageStatus)) {
			status = " and userId=" + Util.getLoginUser().getId()
					+ " and status='待确认'";
		} else if ("gysall".equals(pageStatus)) {
			status = " and userId=" + Util.getLoginUser().getId()
					+ " and status not in ('待核对','待审批','审批中','待通知') ";
		} else if ("audit".equals(pageStatus)) {
			Map<String, Object> map = CircuitRunServerImpl.findAuditExeNode(
					WaigouOrder.class, false);
			if (map != null) {
				List<Integer> idList = (List<Integer>) map.get("entityId");
				StringBuffer sb = new StringBuffer();
				if (idList != null && idList.size() > 0) {
					for (Integer orderid : idList) {
						if (sb.length() == 0) {
							sb.append(orderid);
						} else {
							sb.append("," + orderid);
						}
					}
					if (sb.length() == 0) {
						return new Object[] { null, 0 };
					} else {
						status = " and id in(" + sb.toString() + ")";
					}
				} else {
					return new Object[] { null, 0 };
				}
				// status =hql+ " id in (:entityId)";
				// String hql = "from WaigouOrder where id in (:entityId)";
				// List list = totalDao.find(hql, map, pageNo, pageSize);
				// Object[] exam = new Object[2];
				// Long countLong = totalDao.count("select count(*) " + hql,
				// map);
				// int count = Integer.parseInt(countLong.toString());
				// exam[0] = list;
				// exam[1] = count;
				// return exam;
			} else {
				return new Object[] { null, 0 };
			}
		} else {
			return new Object[] { null, 0 };
		}
		String other = "";
		if (wgOrder.getStatus() != null && wgOrder.getStatus().length() > 0) {
			String[] strarray = wgOrder.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str = "";
				for (int i = 0; i < strarray.length; i++) {
					str += "," + "'" + strarray[i] + "'";
				}
				if (str != null && str.length() > 1) {
					str = str.substring(1);
					other += " and status in (" + str + ")";
				}
			}
		}
		String hql = totalDao.criteriaQueries(wgOrder, null, "addUserYx",
				"status")
				+ status + other;
		if (pageStatus.indexOf("gys") == -1) {
			if ("fl".equals(tag)) {
				hql += " and  type in('辅料')";
			} else if ("yufukuan".equals(tag)) {
				hql += " and  type in('外购','研发','辅料')";
			} else {
				hql += " and  type in('外购','研发')";
			}
		} else {
			hql += " and  type in('外购','研发','辅料')";
		}
		if (wgOrder.getAddUserYx() != null
				&& wgOrder.getAddUserYx().length() > 0) {
			hql += " and id in (select waigouOrder.id from WaigouPlan where markId like '%"
					+ wgOrder.getAddUserYx() + "%')";
		}
		hql += " order by id desc";
		int count = totalDao.getCount(hql);
		if ("findAll".equals(pageStatus)
				&& ((wgOrder.getGysName() != null && !"".equals(wgOrder
						.getGysName())) || (wgOrder.getGysId() != null && !""
						.equals(wgOrder.getGysId())))) {
			pageNo = 1;
			pageSize = count;
		} else {
			pageSize = 15;
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);

		Object[] o = { list, count };
		return o;
	}

	@Override
	public Object[] findWgOrderList_export(WaigouOrder wgOrder, int pageNo,
			int pageSize, String pageStatus, String tag) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		if (wgOrder == null) {
			wgOrder = new WaigouOrder();
		}
		String status = "";
		if ("sbdqr".equals(pageStatus)) {
			status = " and id in (select waigouOrder.id from WaigouPlan where sbjdApplyCount is not null and sbjdApplyCount>0)";
		} else if ("gyssbdqr".equals(pageStatus)) {
			status = " and id in (select waigouOrder.id from WaigouPlan where sbjdApplyCount is not null and sbjdApplyCount>0) and userId="
					+ Util.getLoginUser().getId();
		} else if ("dsq".equals(pageStatus)) {
			status = " and status in ('待核对','待审批','审批中','待通知') and applystatus in('未申请','打回')";
		} else if ("dtz".equals(pageStatus)) {
			status = " and status='待通知' and applystatus = '同意'";
		} else if ("dqr".equals(pageStatus)) {
			status = " and status='待确认' and addUserCode='"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("findAllself".equals(pageStatus)) {
			status = " and addUserCode='" + Util.getLoginUser().getCode() + "'";
		} else if ("findAll".equals(pageStatus)) {
			// 查询所有
		} else if ("gysnew".equals(pageStatus)) {
			status = " and userId=" + Util.getLoginUser().getId()
					+ " and status='待确认'";
		} else if ("gysall".equals(pageStatus)) {
			status = " and userId=" + Util.getLoginUser().getId()
					+ " and status not in ('待核对','待审批','审批中','待通知') ";
		} else if ("audit".equals(pageStatus)) {
			Map<String, Object> map = CircuitRunServerImpl.findAuditExeNode(
					WaigouOrder.class, false);
			if (map != null) {
				List<Integer> idList = (List<Integer>) map.get("entityId");
				StringBuffer sb = new StringBuffer();
				if (idList != null && idList.size() > 0) {
					for (Integer orderid : idList) {
						if (sb.length() == 0) {
							sb.append(orderid);
						} else {
							sb.append("," + orderid);
						}
					}
					if (sb.length() == 0) {
						return new Object[] { null, 0 };
					} else {
						status = " and id in(" + sb.toString() + ")";
					}
				} else {
					return new Object[] { null, 0 };
				}
			} else {
				return new Object[] { null, 0 };
			}
		} else {
			return new Object[] { null, 0 };
		}
		String other = "";
		if (wgOrder.getStatus() != null && wgOrder.getStatus().length() > 0) {
			String[] strarray = wgOrder.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str = "";
				for (int i = 0; i < strarray.length; i++) {
					str += "," + "'" + strarray[i] + "'";
				}
				if (str != null && str.length() > 1) {
					str = str.substring(1);
					other += " and status in (" + str + ")";
				}
			}
		}
		String hql = totalDao.criteriaQueries(wgOrder, null, "addUserYx",
				"status")
				+ status + other;
		if ("fl".equals(tag)) {
			hql += " and  type in('辅料')";
		} else {
			hql += " and  type in('外购','研发')";
		}

		if (wgOrder.getAddUserYx() != null
				&& wgOrder.getAddUserYx().length() > 0) {
			hql += " and id in (select waigouOrder.id from WaigouPlan where markId like '%"
					+ wgOrder.getAddUserYx() + "%')";
		}
		if (user.getInternal() != null && user.getInternal().equals("否")) {
			hql += " and waigouOrder.userId=" + user.getId();
		}
		hql += " order by id desc";
		int count = totalDao.getCount(hql);
		if ("findAll".equals(pageStatus)) {
			pageNo = 1;
			pageSize = count;
		} else {
			pageSize = 15;
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		Object[] o = { list, count };
		return o;
	}

	public boolean updateStatus(Integer id) {
		WaigouOrder w = (WaigouOrder) totalDao.get(WaigouOrder.class, id);
		w.setApplystatus("打回");
		w.setStatus("待核对");
		return totalDao.update(w);
	}

	/****
	 * 外购订单明细查询列表
	 * 
	 * @param wwPlan
	 * @param pageNo
	 * @param pageSize
	 * @param pageStatus
	 * @return
	 */
	@Override
	public Object[] findWgWwPlanList(WaigouPlan waigouPlan, int pageNo,
			int pageSize, String pageStatus, Integer wgOrderId, String tag,
			String planNumber) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		if (waigouPlan == null) {
			waigouPlan = new WaigouPlan();
		}
		String other = "";
		String wgType = "";
		if (waigouPlan.getWgType() != null
				&& !"".equals(waigouPlan.getWgType())) {
			wgType = waigouPlan.getWgType();
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgType);
			if (category != null) {
				getWgtype(category);
			}
			other = " and wgType in (" + strbu.toString().substring(1) + ")";
		}
		if (waigouPlan.getStatus() != null
				&& waigouPlan.getStatus().length() > 0) {
			String[] strarray = waigouPlan.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str = "";
				for (int i = 0; i < strarray.length; i++) {
					str += "," + "'" + strarray[i] + "'";
				}
				if (str != null && str.length() > 1) {
					str = str.substring(1);
					other += " and status in (" + str + ")";
				}
			}
		}
		String fatherhql = "";
		if (waigouPlan != null && waigouPlan.getWaigouOrder() != null) {
			fatherhql = totalDao.criteriaQueries(waigouPlan.getWaigouOrder(),
					null);
			if (fatherhql != "" && fatherhql.length() > 0) {
				fatherhql = " and waigouOrder.id in ( select id " + fatherhql
						+ ")";
			}
		}
		other += fatherhql;
		String hql = totalDao
				.criteriaQueries(waigouPlan, null, "status", "wgType",
						"addTime", "acArrivalTime", "waigouOrder", "isshowBl")
				+ other;

		// 存在采购订单时，显示订单明细
		WaigouOrder waigouOrder = null;
		if (wgOrderId != null && wgOrderId > 0) {
			waigouOrder = (WaigouOrder) totalDao.getObjectById(
					WaigouOrder.class, wgOrderId);
			hql = " from WaigouPlan where waigouOrder.id=" + wgOrderId;
		}
		String status = "";
		if ("sbdqr".equals(pageStatus)) {
			status = " and sbjdApplyCount is not null and sbjdApplyCount>0";
		} else if ("gyssbdqr".equals(pageStatus)) {
			status = " and sbjdApplyCount is not null and sbjdApplyCount>0 and (userId="
					+ user.getId()
					+ " or waigouOrder.addUserCode='"
					+ user.getCode() + "')";
		} else if ("dsq".equals(pageStatus)) {
			status = " and (status in ('待核对','待审批','审批中','待通知') or (status='减单申请中' and oldStatus in('待核对','待审批','审批中','待通知')))"
					+ " and waigouOrder.addUserCode='" + user.getCode() + "'";
		} else if ("dtz".equals(pageStatus)) {
			status = " and (status='待通知' or (status='减单申请中' and oldStatus in('待通知'))) and waigouOrder.addUserCode='"
					+ user.getCode() + "'";
		} else if ("dqr".equals(pageStatus)) {
			status = " and (status in ('待确认','协商确认') or (status='减单申请中' and oldStatus in('待确认','协商确认'))) and waigouOrder.addUserCode='"
					+ user.getCode() + "'";
		} else if ("findAllself".equals(pageStatus)) {
			status = "and waigouOrder.addUserCode='" + user.getCode() + "'";
		} else if ("findAll".equals(pageStatus)
				|| "findAlljg".equals(pageStatus)) {
		} else if ("gysnew".equals(pageStatus)) {
			status = " and userId="
					+ user.getId()
					+ " and (status in ('待确认','协商确认') or (status='减单申请中' and oldStatus in('待确认','协商确认')))";
		} else if ("gysall".equals(pageStatus)) {
			status = " and userId="
					+ user.getId()
					+ " and (status not in('待核对','待审批','审批中','待通知') or (status='减单申请中' and oldStatus in('待核对','待审批','审批中','待通知')))";
		} else if ("audit".equals(pageStatus)) {
		} else {
			return new Object[] { null, 0 };
		}
		if (waigouPlan.getAddTime() != null
				&& waigouPlan.getAddTime().length() > 0) {
			String[] strarray = waigouPlan.getAddTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].trim().length() > 0) {
					hql += " and addTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and addTime < '" + strarray[1].trim() + "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAddTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAddTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if (waigouPlan.getAcArrivalTime() != null
				&& waigouPlan.getAcArrivalTime().length() > 0) {
			String[] strarray = waigouPlan.getAcArrivalTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].length() > 0
						&& strarray[0].trim().length() > 0) {
					hql += " and acArrivalTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and acArrivalTime < '" + strarray[1].trim()
								+ "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAcArrivalTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAcArrivalTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if (pageStatus.indexOf("gys") == -1) {
			if ("fl".equals(tag)) {
				hql += " and  waigouOrder.type in ('辅料')";
			} else {
				hql += " and  waigouOrder.type in ('外购','研发')";
			}
		} else {
			hql += " and  waigouOrder.type in ('辅料','外购','研发')";
		}
		if (planNumber != null && !"".equals(planNumber)) {
			hql += " and  waigouOrder.planNumber = '" + planNumber + "'";
		}
		hql += status + " order by addTime desc";

		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		Double sumMoney = 0d;
		Float sumNum = 0f;
		Double sumbhsprice = 0d;
		Float sumbdhNUmber = 0f;
		Float sumwshNum = 0f;

		if (list != null && list.size() > 0) {
			DecimalFormat numformat = new DecimalFormat("#,###.####");
			for (Object obj : list) {
				WaigouPlan wgp = (WaigouPlan) obj;
				if (wgp.getNumber() != null) {
					sumNum += wgp.getNumber();
				}
				if (wgp.getMoney() != null) {
					sumMoney += wgp.getMoney();
				}
				if (wgp.getHsPrice() != null) {
					sumbhsprice += wgp.getHsPrice();
				}
				if (wgp.getQsNum() != null) {
					sumbdhNUmber += wgp.getQsNum();
				}
				if (wgp.getSyNumber() != null) {
					sumwshNum += wgp.getSyNumber();
				}

			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count, waigouOrder, sumNum, sumMoney, sumbhsprice,
				sumbdhNUmber, sumwshNum };
		return o;
	}

	/****
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteWgOrder(Integer id) {
		if (id != null && id > 0) {
			WaigouOrder wgOrder = (WaigouOrder) totalDao.getObjectById(
					WaigouOrder.class, id);
			if (wgOrder != null) {
				// 遍历下层零件，还回采购数量
				Set<WaigouPlan> waigouplanSet = wgOrder.getWwpSet();
				for (WaigouPlan waigouPlan : waigouplanSet) {

				}
			}
		}
		return false;
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

	/****
	 * 外委订单查询列表
	 * 
	 * @param WaigouOrder
	 * @param pageNo
	 * @param pageSize
	 * @param pageStatus
	 * @return
	 */
	@Override
	public Object[] findWwOrderList(WaigouOrder wgOrder, int pageNo,
			int pageSize, String pageStatus, String tag) {
		if (wgOrder == null) {
			wgOrder = new WaigouOrder();
		}
		Users user = Util.getLoginUser();
		String status = "";
		if (user == null) {
			Object[] o = { null, 0 };
			return o;
		} else {
			// 屏蔽供应商修改pageStatus
			if (user.getDept().equals("供应商")) {
				status = " and userId=" + user.getId();
			}
		}
		String str = "";
		String ywMarkId = wgOrder.getYwMarkId();
		// wgOrder.setYwMarkId(null);
		if (wgOrder.getStatus() != null && wgOrder.getStatus().length() > 0) {
			String[] strarray = wgOrder.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str1 = "";
				for (int i = 0; i < strarray.length; i++) {
					str1 += "," + "'" + strarray[i] + "'";
				}
				if (str1 != null && str1.length() > 1) {
					str1 = str1.substring(1);
					str += " and status in (" + str1 + ")";
				}
			}
		}
		if ("dsq".equals(pageStatus)) {
			status = " and status in ('待核对','待审批','审批中','待通知') and applystatus in('未申请','打回')";
		} else if ("dtz".equals(pageStatus)) {
			status = " and status='待通知' and applystatus='同意'";
		} else if ("dqr".equals(pageStatus)) {
			status = " and status in ('待确认','协商确认') and applystatus='同意'";
		} else if ("gysnew".equals(pageStatus)) {
			status = " and userId=" + Util.getLoginUser().getId()
					+ " and status in ('待确认','协商确认') and applystatus='同意'";
		} else if ("findAllself".equals(pageStatus)) {
			status = " and addUserCode = '" + user.getCode() + "'";
		} else if ("findAll".equals(pageStatus)) {
		} else if ("gysall".equals(pageStatus)) {
			status = " and applystatus='同意' and userId=" + user.getId()
					+ " and status not in ('待核对','待审批','审批中','待通知')";
		} else if ("sbdqr".equals(pageStatus)) {
			status = "  and  addUserCode='"
					+ Util.getLoginUser().getCode()
					+ "'  and id in(select waigouOrder.id from WaigouPlan where status in('协商取消','取消'))";
		} else if ("gyssbdqr".equals(pageStatus)) {
			status = "  and (userId="
					+ Util.getLoginUser().getId()
					+ "  or addUserCode='"
					+ Util.getLoginUser().getCode()
					+ "') and id in(select waigouOrder.id from WaigouPlan where status in('协商取消','取消'))";
		} else {
			return new Object[] { null, 0 };
		}
		if (ywMarkId != null && ywMarkId.length() > 0
				&& !ywMarkId.contains("delete") && !ywMarkId.contains("select")
				&& !ywMarkId.contains("update")) {
			status += " and id in (select waigouOrder.id from WaigouPlan where id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ ywMarkId
					+ "%' or rootMarkId like '%"
					+ ywMarkId
					+ "%'))))";
		}
		// SELECT * FROM ta_sop_w_waigouorder where id IN (
		// SELECT fk_wgOrderId FROM ta_sop_w_waigouplan where id IN(
		// SELECT wgOrderId FROM ta_sop_w_ProcardWGCenter WHERE procardId IN (
		// SELECT id FROM ta_sop_w_procard where orderNumber = 'PZS201801310004'
		// )
		// )
		// )
		if (wgOrder.getNeiorderNum() != null
				&& wgOrder.getNeiorderNum().length() > 0) {
			status += " and id in (select waigouOrder.id FROM WaigouPlan where id  in( "
					+ " select wgOrderId from ProcardWGCenter where procardId in ( select id FROM Procard where orderNumber  like  '%"
					+ wgOrder.getNeiorderNum() + "%' ) )) ";
		}
		String hql = totalDao.criteriaQueries(wgOrder, null, "ywMarkId",
				"neiorderNum","status")
				+ status;
		String type = "外委";
		if ("muju".equals(tag)) {
			type = "模具";
		}
		hql += " and  type  = '" + type + "'" + str;
		// if ("gys".equals(pageStatus)) {
		hql += " order by id desc";
		// }
		int pageCount = 0;
		int count = totalDao.getCount(hql);
		if (wgOrder.getYwMarkId() != null && !"".equals(wgOrder.getYwMarkId())) {
			pageNo = 1;
			pageSize = count;
			pageCount = 1;
		} else {
			pageCount = (count + pageSize - 1) / pageSize;

		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		for (Object obj : list) {
			WaigouOrder waigouorder = (WaigouOrder) obj;
			List<WaigouPlan> wwpList = new ArrayList<WaigouPlan>();
			wwpList.addAll(waigouorder.getWwpSet());
			// for (WaigouPlan wp : wwpList) {
			// Float rukuNum = 0f;// 入库数量
			// List<WaigouDeliveryDetail> wdList = totalDao.query(
			// "from WaigouDeliveryDetail where waigouPlanId=?", wp
			// .getId());
			// if (wdList != null && wdList.size() > 0) {
			// StringBuffer sb = new StringBuffer();
			// for (WaigouDeliveryDetail wd : wdList) {
			// // if(wd.getQuerenTime()!=null&&wd.getQuerenTime().length()>0){
			// // if(sb.length()==0){
			// // sb.append(wd.getQuerenTime()+":"+wd.getShNumber());
			// // }else{
			// // sb.append(";"+wd.getQuerenTime()+":"+wd.getShNumber());
			// // }
			// // }
			// if (wd.getYrukuNum() != null) {
			// rukuNum += wd.getYrukuNum();
			// }
			// }
			// wp.setAcArrivalTime(sb.toString());
			// }
			// wp.setRukuNum(rukuNum);
			// }
			waigouorder.setWwpList(wwpList);
			if (waigouorder.getRootId() != null) {
				Procard procard = (Procard) totalDao.get(Procard.class,
						waigouorder.getRootId());
				if (procard != null) {
					waigouorder.setNeiorderNum(procard.getOrderNumber());// 内部订单号
					waigouorder.setRootSlfCard(procard.getSelfCard());// 总成批次
					waigouorder.setFilnalCount(procard.getFilnalCount());
				}
			} else {
				List<Object[]> procardList = totalDao
						.query(
								"select orderNumber,selfCard,filnalCount from Procard where id in( select rootId from Procard where id in "
										+ "(select procardId from ProcardWGCenter where wgOrderId in "
										+ "(select id from WaigouPlan where waigouOrder.id=?) ))",
								waigouorder.getId());
				if (procardList != null && procardList.size() > 0) {
					for (Object[] object : procardList) {
						Procard procard = new Procard();
						procard.setOrderNumber(object[0].toString());
						procard.setSelfCard(object[1].toString());
						procard.setFilnalCount(Float.parseFloat(object[2]
								.toString()));
						if (waigouorder.getNeiorderNum() == null) {
							waigouorder
									.setNeiorderNum(procard.getOrderNumber());// 内部订单号
							waigouorder.setRootSlfCard(procard.getSelfCard());// 总成批次
							waigouorder
									.setFilnalCount(procard.getFilnalCount());
						} else {
							if (procard.getOrderNumber() != null) {
								if (!waigouorder.getNeiorderNum().contains(
										procard.getOrderNumber())) {
									waigouorder.setNeiorderNum(waigouorder
											.getNeiorderNum()
											+ "、" + procard.getOrderNumber());// 内部订单号
								}
							}
							if (!waigouorder.getRootSlfCard().contains(
									procard.getSelfCard())) {
								waigouorder.setRootSlfCard(waigouorder
										.getRootSlfCard()
										+ "、" + procard.getSelfCard());// 总成批次
							}
							waigouorder.setFilnalCount(waigouorder
									.getFilnalCount()
									+ procard.getFilnalCount());
						}
					}
				}
			}
		}
		Object[] o = { list, count, pageCount };
		return o;
	}

	/****
	 * 采购订单通知供应商
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public String orderToTzGys(Integer[] ids) {
		String mess = "";
		if (ids != null && ids.length > 0) {
			for (Integer id : ids) {
				Users loginUSer = Util.getLoginUser();
				WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
						WaigouOrder.class, id);
				if (waigouOrder != null) {
					waigouOrder.setStatus("待确认");
					waigouOrder.setTongzhiTime(Util.getDateTime());
					waigouOrder.setTzUserCode(loginUSer.getCode());// 通知人员作为联系人
					waigouOrder.setTzUserName(loginUSer.getName());// 通知人员作为联系人
					waigouOrder.setTzUserPhone(loginUSer.getPassword()
							.getPhoneNumber());// 通知人员作为联系人
					waigouOrder.setAddUserYx(loginUSer.getPassword()
							.getMailBox());

					Set<WaigouPlan> planSet = waigouOrder.getWwpSet();
					for (WaigouPlan waigouPlan : planSet) {
						if("设变锁定".equals(waigouPlan.getStatus())){
							throw new RuntimeException(waigouOrder.getPlanNumber()+"的"+
									waigouPlan.getMarkId()
											+ "零件设变锁定无法通知!");
						}else if ("待通知".equals(waigouPlan.getStatus())) {
							waigouPlan.setStatus("待确认");
						} else if (!("关闭".equals(waigouPlan.getStatus()) || "取消"
								.equals(waigouPlan.getStatus()))) {
							throw new RuntimeException(
									waigouPlan.getMarkId()
											+ ":waigouPlan'Status <> waigouOrder'Status");
						}
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
						String type = waigouOrder.getType();
						if ("研发".equals(type)) {
							type = "外购";
						}
						AlertMessagesServerImpl.addAlertMessages(type
								+ "采购订单确认(供应商)", "尊敬的【"
								+ waigouOrder.getLxPeople()
								+ "】,您好:\n您有新的订单需要确认交付日期,订单号:【"
								+ waigouOrder.getPlanNumber() + "】。详询"
								+ waigouOrder.getTzUserName() + "("
								+ waigouOrder.getTzUserPhone()
								+ ")。\n以下地址可以直接登录:\n用户名:"
								+ waigouOrder.getUserCode() + "\n初始密码:"
								+ 123456 + "(请及时修改密码)\n["
								+ companyInfo.getName() + "]", "1", waigouOrder
								.getUserCode());// 待取消
						// "helper");

						/****** 推送邮件 ******/
						// 查询供应商信息
						ZhUser zhuser = (ZhUser) totalDao.getObjectById(
								ZhUser.class, waigouOrder.getGysId());
						String pebsUrl = (String) totalDao
								.getObjectByCondition("select valueCode from CodeTranslation where type = 'sys' and keyCode = 'PEBSURL'");
						if (zhuser != null && zhuser.getYx() != null
								&& zhuser.getYx().length() > 0) {
							zhuser.setYx(zhuser.getYx().replaceAll("；", ";"));
							String[] yxs = zhuser.getYx().split(";");
							Set<WaigouPlan> planSets = waigouOrder.getWwpSet();
							String caigouDetail = "";
							 DecimalFormat df = new DecimalFormat("#.0000");
							for (WaigouPlan waigouPlan : planSets) {
								if (waigouPlan.getSpecification() == null
										|| waigouPlan.getSpecification()
												.length() == 0
										|| "null".equals(waigouPlan
												.getSpecification())) {
									waigouPlan.setSpecification("");
								}
								caigouDetail += "<tr>" + "<th>"
										+ waigouPlan.getMarkId()
										+ "</th>"
										+ "<th align='left' style='width: 150px;'>"
										+ waigouPlan.getProName()
										+ "</th>"
										+ "<th>"
										+ waigouPlan.getSpecification()
										+ "</th>"
										+ "<th>"
										+ (waigouPlan.getBanben() == null ? ""
												: waigouPlan.getBanben())
										+ "</th>" + "<th align='right'>"
										+ "</th>" + "<th>"
										+ waigouPlan.getNumber() + "</th>"
										+ "<th>" + waigouPlan.getUnit()
										+ "</th>" + "<th>"
										+ df.format(waigouPlan.getHsPrice()) + "</th>"
										+ "<th>" + df.format(waigouPlan.getMoney()) 
										+ "</th>" + "<th>" + "</th>" + "<th>"
										+ "</th>" + "</tr>";
							}
							String maildetail = "<style>.table {"
									+ "font-size: 14px;" + "padding: 0px;"
									+ "margin: 0px;"
									+ "border-collapse: collapse;"
									+ "border: solid #999;"
									+ "border-width: 1px 0 0 1px;"
									+ "width: 99%;" + "}"
									+ ".table th,.table td {"
									+ "border: solid #999;"
									+ "border-width: 1 1px 1px 1;"
									+ "padding: 2px;" + "}</style>尊敬的【"
									+ waigouOrder.getLxPeople()
									+ "】先生/女士,您好:<br/>您有新的订单需要确认交付日期,订单号:【"
									+ waigouOrder.getPlanNumber()
									+ "】。<br/><div id='printdiv'>"
									+ "<div align='center' style='font-size: 20px; height: 25px;'>"
									+ companyInfo.getName()
									+ "</div>"
									+ "<div align='center' style='font-size: 14px; height: 25px;'>"
									+ companyInfo.getEnglishName()
									+ "</div>"
									+ "<div align='right' style='font-size: 12px;'>"
									+ "电话:"
									+ companyInfo.getTel()
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
									+ "传真:"
									+ companyInfo.getFax()
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
									+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第 1 页/共 1"
									+ "页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
									+ "</div>"
									+ "<table class='table' style='line-height: 15px;'>"
									+ "<tbody><tr>"
									+ "<th colspan='11' style='border: hidden; font-size: x-large;' align='center'>"
									+ "采购订单"
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden; width: 100px;' align='left' colspan='8'>"
									+ "REY:01"
									+ "</th>"
									+ "<th style='border: hidden;' align='center' colspan='4'>"
									+ "QP140900-C"
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden; width: 100px;' align='left' colspan='8'>"
									+ "供应商编号:"
									+ zhuser.getUsercode()
									+ "</th>"
									+ "<th style='border: hidden;' align='left' colspan='4'>"
									+ "订单编号:"
									+ waigouOrder.getPlanNumber()
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden;' align='left' '='' colspan='8'>"
									+ "供应商名称:"
									+ zhuser.getCmp()
									+ "</th>"
									+ "<th style='border: hidden;' colspan='4' align='left'>"
									+ "制单日期:"
									+ waigouOrder.getTongzhiTime()
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden;' colspan='8' align='left'>"
									+ "地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址:"
									+ zhuser.getCompanydz()
									+ "</th>"
									+ "<th style='border: hidden;' colspan='4' align='left'>"
									+ "采&nbsp;&nbsp;购&nbsp;&nbsp;员:"
									+ waigouOrder.getTzUserName()
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden;' colspan='8' align='left'>"
									+ "联&nbsp;&nbsp;&nbsp;系&nbsp;&nbsp;&nbsp;人:"
									+ zhuser.getCperson()
									+ "</th>"
									+ "<th style='border: hidden;' colspan='4' align='left'>"
									+ "付款方式:"
									+ zhuser.getFkfs()
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th style='border: hidden; border-bottom: inherit;' colspan='8' align='left'>"
									+ "电话&nbsp;/&nbsp;手机:"
									+ zhuser.getCfax()
									+ "/"
									+ zhuser.getCtel()
									+ "</th>"
									+ "<th style='border: hidden; border-bottom: inherit;' colspan='4' align='left'>"
									+ "票据类型:增值税发票"
									+ zhuser.getZzsl()
									+ "</th>"
									+ "</tr>"
									+ "<tr>"
									+ "<th>"
									+ "件号"
									+ "</th>"
									+ "<th>"
									+ "物料名称"
									+ "</th>"
									+ "<th>"
									+ "规格型号"
									+ "</th>"
									+ "<th>"
									+ "版本"
									+ "</th>"
									+ "<th>"
									+ "材质"
									+ "</th>"
									+ "<th>"
									+ "订单数量"
									+ "</th>"
									+ "<th>"
									+ "单位"
									+ "</th>"
									+ "<th>"
									+ "含税单价"
									+ "</th>"
									+ "<th>"
									+ "含税金额"
									+ "</th>"
									+ "<th style='width: 80px;'>"
									+ "交货日期"
									+ "</th>"
									+ "<th>"
									+ "备注"
									+ "</th>"
									+ "</tr>"
									+ caigouDetail
									+ "<tr>"
									+ "<td colspan='11'></td>"
									+ "</tr>"
									+ "<tr style=''>"
									+ "<th align='right' style='border: hidden; border-top: inset;'>"
									+ "送货地址:"
									+ "</th>"
									+ "<th colspan='12' align='left' style='border: hidden; border-top: inherit;'>"
									+ companyInfo.getAddress()
									+ "</th>"
									+ "</tr>"
									+ "<tr style='border: hidden;'>"
									+ "<th align='right' style='border: hidden;'>"
									+ "备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注:"
									+ "</th>"
									+ "<th colspan='12' align='left' style='border: hidden;'></th>"
									+ "</tr>"
									+ "<tr style='border: hidden;'>"
									+ "<th align='right' style='border: hidden; vertical-align: top;'>"
									+ "条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;款:"
									+ "</th>"
									+ "<th style='border: hidden; line-height: 25px' colspan='12' align='left'>"
									+ "<ul>"
									+ "<li>"
									+ "1.供方必须遵循本订购单之交货期或需方采购部电话及书面通知调整之交期，若有延误，每延误一日扣除该批款3%。"
									+ "</li>"
									+ "<li>"
									+ "2.按工程图纸要求、品质保证期限为一年，供方交货之料件必须符合需方之品质要求，否则需方在一年内有权退货或要求赔偿供方不得拒绝。"
									+ "</li>"
									+ "<li>"
									+ "3.检验后如发现品质不良供方在接到通知后3日内应将退货取回，并尽快补料，延期需方自行处理，若急用料需挑选所需人工费由供方负责。"
									+ "</li>"
									+ "</ul>"
									+ "</th>"
									+ "</tr>"
									+ "</tbody></table>"
									+ "</div><br/><font color='red'>以下地址可以登录系统填写交付日期:<a href='"
									+ pebsUrl
									+ "'>PEBS系统登录(请点击)</a><br/>用户名:"
									+ waigouOrder.getUserCode()
									+ "<br/>初始密码:123456(请及时修改密码)<br/>["
									+ companyInfo.getName()
									+ "]<br/>"
									+ "注意:本邮件由系统发送,请勿直接回复。<br/>具体事项请联系"
									+ waigouOrder.getTzUserName()
									+ ",手机号:"
									+ waigouOrder.getTzUserPhone()
									+ "/邮箱:"
									+ waigouOrder.getAddUserYx() + "</font>";
							for (String yx : yxs) {
								yx = yx.replaceAll("", "");
								// 发送邮件提醒
								SendMail sendMail = new SendMail();
								sendMail.sendMail(yx, "新订单确认("
										+ waigouOrder.getPlanNumber() + ")--"
										+ companyInfo.getShortName() + "--"
										+ loginUSer.getName(), maildetail);
							}
						}
						// 短信通知
					}
				}
			}
		}
		return mess;
	}

	/****
	 * 采购订单协商交付
	 * 
	 * @param waigouPlan
	 * @return
	 */
	@Override
	public String gysXsOrder(WaigouPlan waigouPlan) {
		if (waigouPlan != null && waigouPlan.getId() != null) {
			WaigouPlan oldWaigouPlan = (WaigouPlan) totalDao.getObjectById(
					WaigouPlan.class, waigouPlan.getId());
			if (oldWaigouPlan != null) {
			}
			if (oldWaigouPlan != null
					&& (oldWaigouPlan.getStatus().equals("待确认") || oldWaigouPlan
							.getStatus().equals("协商确认"))) {
				oldWaigouPlan.setKuCunCount(waigouPlan.getKuCunCount());
				oldWaigouPlan.setJiaofuTime(waigouPlan.getJiaofuTime());
				oldWaigouPlan.setQuerenTime(Util.getDateTime());
				oldWaigouPlan.setStatus("协商确认");
				totalDao.update(oldWaigouPlan);

				String hql_wgPlan = "from WaigouPlan where (jiaofuTime is null or jiaofuTime ='')and waigouOrder.id=?";
				WaigouOrder waigouOrder = oldWaigouPlan.getWaigouOrder();
				Integer count = totalDao.getCount(hql_wgPlan, waigouOrder
						.getId());
				if (count <= 0) {
					// 通知采购员确认采购订单
					CompanyInfo companyInfo = (CompanyInfo) ActionContext
							.getContext().getApplication().get("companyInfo");
					AlertMessagesServerImpl.addAlertMessages("待确认"
							+ oldWaigouPlan.getType() + "订单管理", "尊敬的【"
							+ waigouOrder.getAddUserName() + "】,您好:\n订单号:【"
							+ waigouOrder.getPlanNumber()
							+ "】的交付日期已全部录入,请您确认。详询" + waigouOrder.getLxPeople()
							+ "(" + waigouOrder.getGysPhone() + ")。\n【"
							+ waigouOrder.getGysName() + "】\n["
							+ companyInfo.getName() + "]", "1", waigouOrder
							.getAddUserCode());
					// "helper");
				}
				waigouPlan.setType(oldWaigouPlan.getType());
				return "true";
			} else {
				return "该采购明细的状态为:" + oldWaigouPlan.getStatus() + "不能修改交付时间！";
			}
		}
		return "数据异常!提交失败,请稍候重试!";
	}

	/****
	 * 采购订单最终确认
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public String orderQueren(Integer[] ids) {
		String mess = "";
		if (ids != null && ids.length > 0) {
			for (Integer id : ids) {
				WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
						WaigouOrder.class, id);
				if (waigouOrder != null) {
					// 查询明细是否全部确认
					String hql = "from WaigouPlan where waigouOrder.id =? and status not in('删除','取消') and (jiaofuTime is null or jiaofuTime ='')";
					int count = totalDao.getCount(hql, waigouOrder.getId());
					if (count > 0) {
						mess += "订单:" + waigouOrder.getPlanNumber()
								+ " 交付日期尚未全部协商完毕,无法确认采购!\\n";
					} else {
						CircuitRun circuitRun = (CircuitRun) totalDao
								.getObjectById(CircuitRun.class, waigouOrder
										.getEpId());
						if ("同意".equals(circuitRun.getAllStatus())) {
							CompanyInfo companyInfo = (CompanyInfo) ActionContext
									.getContext().getApplication().get(
											"companyInfo");
							String type = waigouOrder.getType();
							if ("研发".equals(type)) {
								type = "外购";
							}
							AlertMessagesServerImpl.addAlertMessages(type
									+ "采购订单确认(供应商)", "尊敬的【"
									+ waigouOrder.getLxPeople()
									+ "】,您好:\n您有新的订单已经确认签订,订单号:【"
									+ waigouOrder.getPlanNumber() + "】。详询"
									+ waigouOrder.getTzUserName() + "("
									+ waigouOrder.getTzUserPhone()
									+ ")。\n以下地址可以直接登录:\n用户名:"
									+ waigouOrder.getUserCode() + "\n初始密码:"
									+ 123456 + "(请及时修改密码)\n["
									+ companyInfo.getName() + "]", "1",
									waigouOrder.getUserCode());// 待取消
							// "helper");
							waigouOrder.setApplystatus("同意");
							if ("辅料".equals(waigouOrder.getType())) {
								waigouOrder.setStatus("生产中");
							} else {
								waigouOrder.setStatus("生产中");
							}
							Set<WaigouPlan> planSet = waigouOrder.getWwpSet();
							if (planSet != null && planSet.size() > 0) {
								for (WaigouPlan waigouPlan : planSet) {
									if ("关闭".equals(waigouPlan.getStatus())
											|| "取消".equals(waigouPlan
													.getStatus())) {
										continue;
									}
									if (waigouPlan.getType().equals("外委")
											&& !waigouPlan.getWwType().equals(
													"包工包料")) {
										waigouPlan.setStatus("供应商领料");
									} else {
										if ("辅料".equals(waigouPlan.getType())) {
											waigouPlan.setStatus("生产中");
										} else {
											waigouPlan.setStatus("生产中");
										}

									}
									totalDao.update(waigouPlan);
								}
							}
							totalDao.update(waigouOrder);
							mess += "订单:" + waigouOrder.getPlanNumber()
									+ "确认完成!\\n";
						} else {
							mess += "订单未审批同意。";
						}
						// waigouOrder.setEpId(epId);
						// totalDao.update(waigouOrder);
					}
				}
			}
		} else {
			return "数据异常!";
		}
		return mess;
	}

	/****
	 * 采购订单批量审批
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public String auditOrder(Integer[] ids, String tag) {
		String mess = "";
		if (ids != null && ids.length > 0) {
			for (Integer id : ids) {
				WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
						WaigouOrder.class, id);
				if (waigouOrder != null) {
					if ("ok".equals(tag)) {// 同意
						mess = circuitRunServer.updateExeNodeByCirId(
								waigouOrder.getEpId(), true, "", true, null,
								true);
					} else if ("ng".equals(tag)) {// 打回
						mess = circuitRunServer.updateExeNodeByCirId(
								waigouOrder.getEpId(), false, "", true, null,
								true);
					}
					CircuitRun circuitRun = (CircuitRun) totalDao
							.getObjectById(CircuitRun.class, waigouOrder
									.getEpId());
					if (circuitRun.getAllStatus().equals("同意")) {
						waigouOrder.setApplystatus("同意");
					} else {
						waigouOrder.setApplystatus("打回");
					}
					totalDao.update(waigouOrder);
				}
			}
		} else {
			return "数据异常!";
		}
		return mess;
	}

	/****
	 * 根据件号以及版次查询图纸
	 * 
	 * @param markId
	 * @param banci
	 * @return
	 */
	@Override
	public List findCardTemplateTz(String markId, Integer banci) {
		List<ProcessTemplateFile> processfileList = null;
		String bancisql = "";
		if (banci == null || banci == 0) {
			bancisql =" and (banci is null or banci =0)";
		} else {
			bancisql= " and banci is not null and banci="+banci;
		}
		// 查询工艺规程
		List<Integer> maxIdList= totalDao.query("select max(id) from ProcessTemplateFile where  markId=? "+bancisql+" group by oldfileName", markId);
		if(maxIdList!=null&&maxIdList.size()>0){
			StringBuffer maxidsb = new StringBuffer();
			for(Integer maxId:maxIdList){
				if(maxidsb.length()==0){
					maxidsb.append(maxId);
				}else{
					maxidsb.append(","+maxId);
				}
			}
			processfileList=totalDao
			.query(
					"from ProcessTemplateFile where id in ("+maxidsb.toString()+")");
		}
		return processfileList;
	}
	/****
	 * 根据件号mopId查看图纸
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public List findCardTemplateTz(Integer id) {
		List<ProcessTemplateFile> processfileList = null;
		ManualOrderPlanDetail mp = (ManualOrderPlanDetail) totalDao.getObjectById(ManualOrderPlanDetail.class, id);
		if(mp!=null){
			String banciSql = null;
			if(mp.getBanci()==null||mp.getBanci()==0){
				banciSql = " and (banci is null or banci=0)";
			}else{
				banciSql = " and  banci="+mp.getBanci();
			}
			String banbenSql = null;
			if(mp.getBanben()==null||mp.getBanben().length()==0){
				banbenSql = " and banBenNumber is null or banBenNumber=0";
			}else{
				banbenSql = " and  banBenNumber='"+mp.getBanben()+"'";
			}
			String productyle = null;
			if(mp.getProcardId()!=null){
				productyle = (String) totalDao.getObjectByCondition("select productStyle from Procard where id=?", mp.getProcardId());
			}
			if(productyle==null){
				productyle= (String) totalDao.getObjectByCondition("select productStyle from ProcardTemplate where markId=?" +
						" and (banbenStatus is null or banbenStatus !='历史') and  (dataStatus is null or dataStatus !='删除') "+banbenSql+banciSql, mp.getMarkId());
			}
			if(productyle==null){
				String banbenSql2 = null;
				if(mp.getBanben()==null||mp.getBanben().length()==0){
					banbenSql2 = " and banbenhao is null or banbenhao=0";
				}else{
					banbenSql2 = " and  banbenhao='"+mp.getBanben()+"'";
				}
				productyle= (String) totalDao.getObjectByCondition("select productStyle from YuanclAndWaigj where markId=?" +
						" and (status is null or status!='禁用') "+banbenSql2, mp.getMarkId());
			}
//			processfileList = totalDao
//			.query(
//					"from ProcessTemplateFile where id in(select max(id) from ProcessTemplateFile where  markId=? and productStyle=?"+ banciSql +" group by oldfileName)",
//					mp.getMarkId(),productyle);//此方法经测试太费时，采用下面的分段方式查询
			List<Integer> idList= totalDao.query("select max(id) from ProcessTemplateFile where  markId=? and productStyle=?"+ banciSql +" group by oldfileName", mp.getMarkId(),productyle);
			StringBuffer sb = Util.getIntegersqlData(idList);
			if(sb.length()>0){
				processfileList = totalDao.query("from  ProcessTemplateFile where id in("+sb.toString()+")");
			}
			
		}
		
		
		
		return processfileList;
	}

	/****
	 * 查询所有的待送货订单（供应商）
	 * 
	 * @return
	 */
	@Override
	public Object[] findFllScz(WaigouPlan waigouPlan, int pageNo, int pageSize,
			String pageStatus) {
			Users loginUser = Util.getLoginUser();
		// boolean b = false;
		String other = " and  syNumber>0 and status  in ('退回','退货','生产中','送货中','待检验','待分检','待入库','入库')";
		if (!"sgsh".equals(pageStatus)) {
			other += " and userId=" + loginUser.getId();
			//关闭的件号不允许送货。
			other += " and markId not in (select markId from GysMarkIdCkolse where gysId ="+loginUser.getId()+" and isClose = 0 )";
		}
		boolean bool = false;
		String planNumber_hql = "";
		if (waigouPlan == null) {
			waigouPlan = new WaigouPlan();
		} else if (waigouPlan.getWaigouOrder() != null
				&& waigouPlan.getWaigouOrder().getPlanNumber() != null
				&& !"".equals(waigouPlan.getWaigouOrder().getPlanNumber())) {
			// other += " and waigouOrder.planNumber like '%"
			// + waigouPlan.getWaigouOrder().getPlanNumber() + "%'";
			String planNumbers = waigouPlan.getWaigouOrder().getPlanNumber()
					.trim();
			planNumbers = planNumbers.replaceAll("'", "");
			String ragex = "[\\r\\n\\t\040:：;；&$!~@^#,]+";
			bool = true;
			String[] planNumberArry = planNumbers.split(ragex);
			planNumber_hql = " and waigouOrder.planNumber in (";
			String str = "";
			for (int i = 0; i < planNumberArry.length; i++) {
				str += ",'" + planNumberArry[i] + "'";
			}
			if (str != null && str.length() > 0) {
				planNumber_hql += str.substring(1) + ")";
			}
		}
		other += planNumber_hql;
		String markId_hql = "";

		if (waigouPlan.getMarkId() != null
				&& waigouPlan.getMarkId().trim().length() > 0) {
			String markIds = waigouPlan.getMarkId().trim();
			markIds = markIds.replaceAll("'", "");
			String ragex = "[\\r\\n\\t\040:：;；&$!~@^#,]+";
			bool = true;
			String[] markIdArry = markIds.split(ragex);
			markId_hql = " and markId in (";
			String str = "";
			for (int i = 0; i < markIdArry.length; i++) {
				str += ",'" + markIdArry[i] + "'";
			}
			if (str != null && str.length() > 0) {
				markId_hql += str.substring(1) + ")";
			}
		}
		other += markId_hql;

		// if (waigouPlan.getMarkId() != null
		// && waigouPlan.getMarkId().length() > 0) {
		// b = true;
		// }
		if ("sgsh".equals(pageStatus)
				&& (waigouPlan.getWaigouOrder() == null
						|| waigouPlan.getWaigouOrder().getPlanNumber() == null || waigouPlan
						.getWaigouOrder().getPlanNumber().length() <= 0)) {
			Object[] o = { new ArrayList(), 0 };
			return o;
		}

		String hql = totalDao.criteriaQueries(waigouPlan, null, "waigouOrder",
				"markId")
				+ other + " order by markId,addTime asc";
		int count = totalDao.getCount(hql);
		if (bool) {
			pageNo = 1;
			pageSize = count;
		}
		List<WaigouPlan> list = totalDao.findAllByPage(hql, pageNo, pageSize);
		Double totalMoney = 0d;
		Float djfSum = 0f;
		for (WaigouPlan plan : list) {
			if (plan.getHsPrice() != null) {
				totalMoney += plan.getHsPrice() * plan.getNumber();
			}
			djfSum += plan.getSyNumber();
			List<String> ywmarkIdList = totalDao
					.query(
							"select distinct(ywMarkId) from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) ",
							plan.getId());
			if (ywmarkIdList != null && ywmarkIdList.size() > 0) {
				String str = "";
				for (String ywmarkId : ywmarkIdList) {
					str += ywmarkId + "<br/>";
				}
				plan.setYwmarkId(str);
			}
		}
		totalMoney = (double)Util.FomartDouble(totalMoney, 4);
		djfSum = Util.FomartFloat(djfSum, 3);
		Object[] o = { list, count, pageSize, totalMoney, djfSum };
		return o;
	}

	/****
	 * 采购订单申请送货(供应商)
	 * 
	 * @param ids
	 * @return
	 */
	@Override
	public Object[] addOrderToSonghuo(Integer[] ids) {
		List list = new ArrayList();
		Users loginUser = Util.getLoginUser();
		ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
				"from ZhUser where userid=?", loginUser.getId());
		WaigouDelivery wdv = new WaigouDelivery();

		if (zhuser != null) {
			String contacts = "";
			String contactsPhone = "";
			String gysname = "";
			if (ids != null && ids.length > 0) {
				Set<String> strSet = new HashSet<String>();
				Set<String> gysSet = new HashSet<String>();
				for (Integer id : ids) {
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class, id);
					String planNumber = waigouPlan.getWaigouOrder()
							.getPlanNumber();
					String str = planNumber.replaceAll("[0-9]*", "");
					String type = waigouPlan.getType();
					gysSet.add(waigouPlan.getGysName());
					strSet.add(str+type);
					gysname = waigouPlan.getGysName();
					// 未送货的数量大于0，可以继续申请送货
					if (waigouPlan != null && waigouPlan.getSyNumber() > 0) {
						WaigouDeliveryDetail wdd = new WaigouDeliveryDetail();
						BeanUtils.copyProperties(waigouPlan, wdd);
						wdd.setCgOrderNum(planNumber);
						wdd.setShNumber(waigouPlan.getSyNumber());
						if (contacts.indexOf(waigouPlan.getWaigouOrder()
								.getAddUserName()) < 0) {
							contacts += waigouPlan.getWaigouOrder()
									.getAddUserName()
									+ ";";
							contactsPhone += waigouPlan.getWaigouOrder()
									.getAddUserPhone()
									+ ";";
						}
						// 根据历史数据计算箱数
						String hql_ctn = "select max(oneCtnNum) from WaigouDeliveryDetail where markId=? and gysId=?";
						Float maxoneCtnNum = (Float) totalDao
								.getObjectByCondition(hql_ctn, wdd.getMarkId(),
										zhuser.getId());
						if (maxoneCtnNum == null || maxoneCtnNum <= 0) {
							wdd.setCtn(1F);
						} else {
							wdd.setCtn(Float.parseFloat(Math.ceil(wdd
									.getShNumber()
									/ maxoneCtnNum)
									+ ""));
						}
						wdd.setOneCtnNum(maxoneCtnNum);
						wdd.setWaigouPlanId(waigouPlan.getId());
						if ("外购".equals(waigouPlan.getType())) {
							String hql = "select rangeOfReceipt from Category where name =  ?";
							Float rangeOfReceipt = (Float) totalDao
									.getObjectByCondition(hql, waigouPlan
											.getWgType());
							wdd.setRangeOfReceipt(rangeOfReceipt == null ? 0f
									: rangeOfReceipt);
						}
						// 根据历史数据计算箱数结束
						list.add(wdd);
					}
				}
				if (strSet.size() > 1) {
					return new Object[] { null, null,
							strSet.toString() + "订单类型不是同一个，请选择同一订单类型进行送货" };
				}
				if (gysSet.size() > 1) {
					return new Object[] { null, null,
							gysSet.toString() + "所属不同供应商，请选择同一供应商订单类型进行送货" };
				}
			}

			// 送货人历史数据查询***** 开始
			String hql_shContact = "select shContacts,shContactsPhone from WaigouDelivery where gysId=? and shContacts is not null "
					+ "and shContacts<>'' order by id desc";
			Object[] obj_shc = (Object[]) totalDao.getObjectByCondition(
					hql_shContact, zhuser.getId());
			if (obj_shc != null) {
				wdv.setShContacts(obj_shc[0].toString());
				wdv.setShContactsPhone(obj_shc[1].toString());
			}
			// 车牌
			String hql_chepai = "select chepai from WaigouDelivery where gysId=? and chepai is not null "
					+ "and chepai<>'' order by id desc";
			String chepai = (String) totalDao.getObjectByCondition(hql_chepai,
					zhuser.getId());
			wdv.setChepai(chepai);
			// 送货人历史数据查询***** 结束
			wdv.setUserCode(loginUser.getCode());
			wdv.setUserId(loginUser.getId());
			wdv.setGysId(zhuser.getId());
			wdv.setGysName(gysname);
			wdv.setGysPhone(zhuser.getCmobile());
			wdv.setGysContacts(zhuser.getCperson());
			if (ActionContext.getContext() != null) {
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				wdv.setCustomer(companyInfo.getName());
				wdv.setDaodaDizhi(companyInfo.getAddress());
			}
			wdv.setContacts(contacts);
			wdv.setContactsPhone(contactsPhone);
			wdv.setChufaDizhi(zhuser.getCompanydz());

			String maxNumber = shNumber(loginUser);
			wdv.setPlanNumber(maxNumber);// 采购计划编号
		}
		return new Object[] { wdv, list, "true" };
	}

	/****
	 * 仓库添加手工送货单并确认数量
	 * 
	 * @return
	 */
	@Override
	public String orderToshAndQs(Integer[] ids, Float[] qrnum) {
		Users loginUser = Util.getLoginUser();
		if (loginUser != null) {
			WaigouDelivery waigouDelivery = new WaigouDelivery();
			waigouDelivery.setStatus("待检验");
			if (ActionContext.getContext() != null) {
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				waigouDelivery.setCustomer(companyInfo.getName());
				waigouDelivery.setDaodaDizhi(companyInfo.getAddress());
			}

			String type = null;
			String maxNumber = null;
			String contacts = "";
			String contactsPhone = "";
			Set<WaigouDeliveryDetail> wddSet = new HashSet<WaigouDeliveryDetail>();
			Integer gysid = 0;
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Integer id = ids[i];
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class, id);
					// 未送货的数量大于0，可以继续申请送货
					if (waigouPlan != null) {
						if (waigouPlan.getSyNumber() <= 0) {
							continue;
						}
						gysid = waigouPlan.getGysId();
						WaigouDeliveryDetail wdd = new WaigouDeliveryDetail();
						BeanUtils.copyProperties(waigouPlan, wdd, new String[] {
								"id", "querenTime", "hgNumber" });
						/******************** 赋值送货单号和类型 *********************/
						if (maxNumber == null && waigouPlan.getGysId() != null) {
							maxNumber = shdNunber(waigouPlan.getGysId());
							waigouDelivery.setPlanNumber(maxNumber);// 送货单号
						}
						if (type == null && waigouPlan.getType() != null)
							type = waigouPlan.getType();
						/******************** 赋值送货单号和类型 *********************/

						wdd.setCgOrderNum(waigouPlan.getWaigouOrder()
								.getPlanNumber());
						wdd.setShNumber(qrnum[i]);// 送货数量
						wdd.setQrNumber(qrnum[i]);// 确认数量
						if (contacts.indexOf(waigouPlan.getWaigouOrder()
								.getAddUserName()) < 0) {
							contacts += waigouPlan.getWaigouOrder()
									.getAddUserName()
									+ ";";
							contactsPhone += waigouPlan.getWaigouOrder()
									.getAddUserPhone()
									+ ";";
						}
						wdd.setWaigouPlanId(waigouPlan.getId());
						// 根据历史数据计算箱数结束

						// 计算剩余可送货数量
						Float syNumber = waigouPlan.getSyNumber()
								- wdd.getShNumber();
						if (syNumber < 0) {
							throw new RuntimeException(waigouPlan.getMarkId()
									+ "的本次送货数量不能超过" + waigouPlan.getSyNumber());
						}
						if (waigouPlan.getQsNum() != null) {
							waigouPlan.setQsNum(waigouPlan.getQsNum()
									+ qrnum[i]);
						} else {
							waigouPlan.setQsNum(qrnum[i]);
						}
						waigouPlan.setSyNumber(syNumber);
						totalDao.update(waigouPlan);

						wdd.setStatus("待检验");
						wdd.setAddTime(Util.getDateTime());
						wdd.setProNumber(waigouPlan.getProNumber());// 项目编号
						wdd.setPrintTime(Util.getDateTime());
						wdd.setQuerenTime(Util.getDateTime());
						wdd.setWaigouPlanId(waigouPlan.getId());
						wdd.setWaigouDelivery(waigouDelivery);
						wdd.setType(waigouPlan.getWaigouOrder().getType());// 采购类型
						wdd.setWwType(waigouPlan.getWaigouOrder().getWwType());// 采购类型
						wdd.setProcessNo(waigouPlan.getProcessNOs());// 工序号
						wdd.setProcessName(waigouPlan.getProcessNames());// 工序号
						wdd.setQrUsersName(loginUser.getName());
						wdd.setQrUsersId(loginUser.getId());
						// wdd.setQrWeizhi("待检区");
						// 生成检验批次
						jianyanPici(wdd);
						wddSet.add(wdd);
						mustNotOverflowWgPlanNum(id, qrnum[i], waigouPlan
								.getNumber(), null, waigouPlan);
					}
				}
			} else {
				return "送货明细为空";
			}
			if (gysid > 0 && wddSet.size() > 0) {
				ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
						"from ZhUser where id=?", gysid);
				waigouDelivery.setContacts(contacts);
				waigouDelivery.setContactsPhone(contactsPhone);
				waigouDelivery.setGysId(zhuser.getId());
				waigouDelivery.setGysName(zhuser.getCmp());
				waigouDelivery.setGysPhone(zhuser.getCmobile());
				waigouDelivery.setGysContacts(zhuser.getCperson());
				waigouDelivery.setChufaDizhi(zhuser.getCompanydz());
				waigouDelivery.setWddSet(wddSet);
				waigouDelivery.setUserCode(zhuser.getUsercode());
				waigouDelivery.setUserId(zhuser.getUserid());
				waigouDelivery.setAddTime(Util.getDateTime());
				waigouDelivery.setPrintTime(Util.getDateTime());
				totalDao.save(waigouDelivery);

				for (WaigouDeliveryDetail wdd : wddSet) {
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class, wdd
									.getWaigouPlanId());
					// 查询采购订单，更新状态
					String wlwzdtmes = updateWaigouPlan(loginUser,
							waigouDelivery, wdd, waigouPlan);

					// 查询采购明细对应生产批次
					updateProcardStatus(waigouPlan, wlwzdtmes, "待检验");
					// 存放到待检库;
					daiJIanRuku(waigouDelivery, wdd);
					// 生成外购件来料日报表;
					laiLiao(wdd);

				}
				if (type.equals("研发")) {
					type = "外购";
				}
				AlertMessagesServerImpl.addAlertMessages(type + "检验",
						"您有送货单编号为" + waigouDelivery.getPlanNumber() + "的"
								+ wddSet.size() + "件物品待检验", type + "待检验");
				return "申请送货成功!";
			}
			return "数据异常!请刷新后重试";
		}
		return "";
	}

	/**
	 * 更新采购订单、订单明细状态
	 * 
	 * @param loginUser
	 * @param waigouDelivery
	 * @param wdd
	 * @param waigouPlan
	 * @return
	 */
	private String updateWaigouPlan(Users loginUser,
			WaigouDelivery waigouDelivery, WaigouDeliveryDetail wdd,
			WaigouPlan waigouPlan) {
		WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
				WaigouOrder.class, waigouPlan.getWaigouOrder().getId());
		waigouOrder.setStatus("待检验");
		totalDao.update(waigouOrder);

		String qjnumberMes = "(缺件：0" + wdd.getUnit() + ")";
		waigouPlan.setStatus("待检验");
		if (waigouPlan.getWlWeizhiDt() == null) {
			waigouPlan.setWlWeizhiDt("");
		}
		String wlwzdtmes = "仓库确认:<br/>供应商:"
				+ waigouDelivery.getGysName()
				+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
				+ waigouDelivery.getId() + "'>"
				+ waigouDelivery.getPlanNumber() + "</a><br/>确认数量:"
				+ wdd.getQrNumber() + wdd.getUnit() + qjnumberMes + "<br/>确认人:"
				+ loginUser.getName() + ";<br/>确认时间:" + Util.getDateTime()
				+ "<br/>当前位置:" + wdd.getQrWeizhi() + "<hr/>";
		// waigouPlan.setWlWeizhiDt(wlwzdtmes);
		WlWeizhiDt wlWeizhiDt = new WlWeizhiDt(null, waigouPlan.getId(), null,
				wlwzdtmes);
		totalDao.save(wlWeizhiDt);
		if (waigouPlan.getQsNum() != null) {
			waigouPlan.setQsNum(waigouPlan.getQsNum() + wdd.getQrNumber());
		} else {
			waigouPlan.setQsNum(wdd.getQrNumber());
		}
		totalDao.update(waigouPlan);
		return wlwzdtmes;
	}

	/**
	 * 更新生产流水卡状态
	 * 
	 * @param waigouPlan
	 * @param wlwzdtmes
	 */
	private void updateProcardStatus(WaigouPlan waigouPlan, String wlwzdtmes,
			String status) {
		String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id = ?)";
		if (waigouPlan.getMopId() != null) {
			List list_procrd = totalDao.query(hql_procard, waigouPlan
					.getMopId());
			for (int j = 0; j < list_procrd.size(); j++) {
				Procard procard = (Procard) list_procrd.get(j);
				procard.setWlstatus(status);
				if (procard.getWlWeizhiDt() == null) {
					procard.setWlWeizhiDt("");
				}
				WlWeizhiDt wlWeizhiDt1 = new WlWeizhiDt(procard.getId(), null,
						null, wlwzdtmes);
				totalDao.save(wlWeizhiDt1);
			}
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, waigouPlan.getMopId());
			if (mop != null) {
				if (mop.getQsCount() != null) {
					mop.setQsCount(waigouPlan.getQsNum() + mop.getQsCount());
				} else {
					mop.setQsCount(waigouPlan.getQsNum());
				}
				if (mop.getWshCount() != null) {
					mop.setWshCount(mop.getWshCount()
							+ waigouPlan.getSyNumber());
				} else {
					mop.setWshCount(waigouPlan.getSyNumber());
				}
				totalDao.update(mop);
			}
		}

	}

	/**
	 * 手动送货添加待检入库记录
	 * 
	 * @param waigouDelivery
	 * @param wdd
	 */
	private void daiJIanRuku(WaigouDelivery waigouDelivery,
			WaigouDeliveryDetail wdd) {
		GoodsStore goodsStore = new GoodsStore();
		goodsStore.setGoodsStoreMarkId(wdd.getMarkId());// 件号
		goodsStore.setGoodsStoreWarehouse("待检库");// 库别
		goodsStore.setGoodHouseName("待检仓");// 仓区
		goodsStore.setGoodsStorePosition(wdd.getKuwei());// 库位
		goodsStore.setBanBenNumber(wdd.getBanben());// 版本
		goodsStore.setKgliao(wdd.getKgliao());// 供料属性
		goodsStore.setGoodsStoreLot(wdd.getExamineLot());// 批次
		goodsStore.setHsPrice(wdd.getHsPrice()==null?null:wdd.getHsPrice().doubleValue());// 含税单价
		goodsStore.setGoodsStoreGoodsName(wdd.getProName());// 名称
		goodsStore.setGoodsStoreFormat(wdd.getSpecification());// 规格
		goodsStore.setWgType(wdd.getWgType());// 物料类别
		goodsStore.setGoodsStoreUnit(wdd.getUnit());// 单位
		goodsStore.setGoodsStoreCount(wdd.getQrNumber());// 数量
		goodsStore.setGoodsStorePrice(wdd.getPrice()==null?null:wdd.getPrice().doubleValue());// 不含税单价
		goodsStore.setTaxprice(wdd.getTaxprice());// 税率
		// 总额
		goodsStore.setPriceId(wdd.getPriceId());// 价格Id
		goodsStore.setGoodsStoreSupplier(wdd.getGysName());// 供应商名称
		goodsStore.setGysId(wdd.getGysId() + "");// 供应商Id
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		goodsStore.setGoodsStoreTime(Util.getDateTime());
		goodsStore.setWwddId(wdd.getId());// 送货单明细Id
		goodsStore.setGoodsStoreSendId(waigouDelivery.getPlanNumber());// 送货单号
		goodsStore.setTuhao(wdd.getTuhao());// 图号
		goodsStore.setStyle("待检入库");// 入库类型
		goodsStoreServer.saveSgrk(goodsStore);
	}

	/**
	 * 添加来料日报记录
	 * 
	 * @param wdd
	 */
	private void laiLiao(WaigouDeliveryDetail wdd) {
		WaigouDailySheet wds = new WaigouDailySheet();
		wds.setMarkId(wdd.getMarkId());// 件号
		wds.setProName(wdd.getProName());// 产品名称
		wds.setGysName(wdd.getGysName());// 供应商名称
		ZhUser zhusers = (ZhUser) totalDao.get(ZhUser.class, wdd.getGysId());
		if (zhusers != null) {
			wds.setGysjc(zhusers.getName());// 供应商简称
		}
		wds.setGysId(wdd.getGysId());
		;// 供应商Id
		wds.setTuhao(wdd.getTuhao());// 图号;
		wds.setLlNumber(wdd.getQrNumber());// 来料数量
		wds.setExamineLot(wdd.getExamineLot());// 检验批次
		wds.setCgOrderNum(wdd.getCgOrderNum());// 采购订单号
		String caizi = "";
		String hql = " from YuanclAndWaigj where markId = ?";
		// 版本
		if (wdd.getBanben() != null && wdd.getBanben().length() > 0) {
			hql += " and banbenhao = '" + wdd.getBanben() + "'";
		} else {
			hql += " and ( banbenhao = '' or banbenhao is null)";
		}
		// 规格
		if (wdd.getSpecification() != null
				&& wdd.getSpecification().length() > 0) {
			hql += " and specification = '" + wdd.getSpecification() + "'";
		} else {
			hql += " and (specification ='' or specification is null)";
		}
		// 供料属性
		if (wdd.getKgliao() != null && wdd.getKgliao().length() > 0) {
			hql += " and kgliao = '" + wdd.getKgliao() + "'";
		} else {
			hql += " and(kgliao = '' or  kgliao is null)";
		}
		// 物料类别
		if (wdd.getWgType() != null && wdd.getWgType().length() > 0) {
			hql += " and wgType = '" + wdd.getWgType() + "'";
		} else {
			hql += " and (wgType = '' or wgType is null)";
		}
		YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj) totalDao
				.getObjectByCondition(hql, wdd.getMarkId());
		if (yuanclAndWaigj != null) {
			caizi = yuanclAndWaigj.getCaizhi();
			wds.setCaizi(caizi);
		}
		wds.setWgddId(wdd.getId());
		wds.setYear(Util.getDateTime("yyyy") + "年");
		wds.setMonth(Util.getDateTime("yyyy-MM") + "月");
		wds.setWeek(Util.getWeek(null, null));
		wds.setAddTime(Util.getDateTime());
		wds.setAddUser(Util.getLoginUser().getName());
		wds.setYwmarkId(wdd.getYwmarkId());
		totalDao.save(wds);
	}

	private void jianyanPici(WaigouDeliveryDetail wdd) {
		String maxexamineLot;
		String mouth = new SimpleDateFormat("yyyyMM").format(new Date());
		String hql_examineLot = "select max(examineLot) from WaigouDeliveryDetail where examineLot like '%"
				+ mouth + "%'";
		Object object = (Object) totalDao.getObjectByCondition(hql_examineLot);
		if (object != null) {
			Long selfCard = Long.parseLong(object.toString()) + 1;// 当前最大流水卡片
			maxexamineLot = selfCard.toString();
		} else {
			maxexamineLot = mouth + "00001";
		}
		wdd.setExamineLot(maxexamineLot);
	}

	/**
	 * 根据供应商ID得到该供应商最大的送货单号
	 * 
	 * @param id
	 * @return
	 */
	private String shdNunber(Integer id) {
		// 得到最大的送货计划编号
		String code = "";
		ZhUser zhUser = (ZhUser) totalDao.getObjectById(ZhUser.class, id);
		if (zhUser != null) {
			code = zhUser.getUsercode();
		} else {
			code = Util.getLoginUser().getCode();
		}
		String maxNumber = "DN" + code + Util.getDateTime("yyyyMMdd");
		String hql_maxnumber = "select max(planNumber) from WaigouDelivery where planNumber like '%"
				+ maxNumber + "%'";
		Object obj = totalDao.getObjectByCondition(hql_maxnumber);
		if (obj != null) {
			int num2 = 0;
			String maxNumber2 = obj.toString();
			num2 = Integer.parseInt(maxNumber2.substring(
					maxNumber2.length() - 3, maxNumber2.length()));
			num2++;
			if (num2 < 10) {
				maxNumber += "00" + num2;
			} else if (num2 < 100) {
				maxNumber += "0" + num2;
			} else if (num2 < 1000) {
				maxNumber += num2;
			} else {
				maxNumber += num2 + 1;
			}
		} else {
			maxNumber += "001";
		}
		return maxNumber;
	}

	/****
	 * 添加送货单
	 * 
	 * @param waigouDelivery
	 * @param list
	 * @return
	 */
	@Override
	public Map<Integer, Object> addDeliveryNote(WaigouDelivery waigouDelivery,
			List list) {
		StringBuffer msg = new StringBuffer("");
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		// List<WaigouDelivery> wdList = new ArrayList<WaigouDelivery>();
		Users loginUser = Util.getLoginUser();
		if (waigouDelivery != null) {
			List<WaigouDeliveryDetail> wddSet = new ArrayList();
			List<String> codeList = new ArrayList<String>();// 装采购员工号
			List<Integer> idList = new ArrayList<Integer>();
			// int count = 0;
			ZhUser zuser = null;
			if (list == null || list.size() <= 0) {
				map.put(0, "送货单明细为空！请检查");
				return map;
			}
			String ids = "";
			for (int i = 0; i < list.size(); i++) {
				WaigouDeliveryDetail wdd = (WaigouDeliveryDetail) list.get(i);
				if (i == 0) {
					ids += wdd.getWaigouPlanId();
				} else {
					ids += "," + wdd.getWaigouPlanId();
				}
			}

			for (int i = 0; i < list.size(); i++) {
				WaigouDeliveryDetail wdd = (WaigouDeliveryDetail) list.get(i);
				WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, wdd.getWaigouPlanId());
				if (zuser == null) {
					zuser = (ZhUser) totalDao.getObjectById(ZhUser.class,
							waigouPlan.getGysId());
				}
				idList.add(waigouPlan.getId());
				if (waigouPlan != null && waigouPlan.getSyNumber() > 0) {
					Float shNumber = wdd.getShNumber();
					Float syNumber0 = waigouPlan.getSyNumber();
					Float ysNumber0 = waigouPlan.getSyNumber()
							+ (wdd.getRangeOfReceipt() == null ? 0 : wdd
									.getRangeOfReceipt());
					if (shNumber <= 0) {
						msg.append("订单编号:"
								+ waigouPlan.getWaigouOrder().getPlanNumber()
								+ " 件号:" + waigouPlan.getMarkId()
								+ " 的送货数量必须大于0");
					} else if (shNumber > ysNumber0) {
						msg.append("订单编号:"
								+ waigouPlan.getWaigouOrder().getPlanNumber()
								+ " 件号:" + waigouPlan.getMarkId() + " 的送货数量:"
								+ shNumber + " 大于订单未送货数量:"
								+ waigouPlan.getSyNumber() + " 超出了："
								+ (shNumber - ysNumber0) + "，不在该零件的收货浮动范围±"
								+ wdd.getRangeOfReceipt() + "内。");
					}

					boolean bool = false;
					if ("外购".equals(waigouPlan.getType())) {
						String beforhql = " from WaigouPlan where markId = ? and gysId =? and addTime<? "
								+ " and kgliao=? and status not"
								+ "  in ('待核对', '待审核','待确认','协商确认','待通知','取消','关闭','删除') and syNumber>0 and waigouOrder.addUserCode = ? "
								+ " and id not in (" + ids + ")";
						if(waigouPlan.getBanben()!=null && waigouPlan.getBanben().length()>0){
							beforhql+= " and banben = '"+waigouPlan.getBanben()+"'";
						}else{
							beforhql+= " and (banben is null or banben = '')";
						}
						List<WaigouPlan> beforList = totalDao.query(beforhql,
								waigouPlan.getMarkId(), waigouPlan.getGysId(),
								waigouPlan.getAddTime(),
								waigouPlan.getKgliao(), waigouPlan
										.getWaigouOrder().getAddUserCode());
						if (beforList != null && beforList.size() > 0) {
							int count = 0;
							for (WaigouPlan w2 : beforList) {
								if (!idList.contains(w2.getId())) {
									String hql = "from WaigouDeliveryDetail where waigouPlanId = ?";
									WaigouDeliveryDetail wgdd1 = (WaigouDeliveryDetail) totalDao
											.getObjectByCondition(hql, w2
													.getId());
									if (wgdd1 == null) {
										if (count == 0) {
											msg
													.append("请按照订单先后顺序交货,顺序如下:<br/>");
										}
										msg.append(count++
												+ ":订单号"
												+ w2.getWaigouOrder()
														.getPlanNumber()
												+ "件号:" + w2.getMarkId()
												+ " 。<br/>");
										bool = true;
									}
								}
							}
						}
					} else if ("外委".equals(waigouPlan.getType())
							&& !"BOM外委".equals(waigouPlan.getWwSource())) {
						Procard procard = (Procard) totalDao
								.getObjectByCondition(
										" from Procard where id in( select procardId from ProcardWGCenter where wgOrderId = ? )",
										waigouPlan.getId());
						String hql = " from WaigouPlan where syNumber>0 and id in (select wgOrderId "
								+ " from ProcardWGCenter where procardId in ( select id from Procard where markId = ? and rootSelfCard <? and ywMarkId = ?)) "
								+ " and id not in ("
								+ ids
								+ ")"
								+ " and status not in ('待审核','待确认','协商确认','待通知','取消','供应商领料','取消','关闭','删除') and gysId =? "
								+ " and (blNum is null or blNum =0)";
						if (procard != null) {
							List<WaigouPlan> wgList = totalDao.query(hql,
									procard.getMarkId(), procard
											.getRootSelfCard(), procard
											.getYwMarkId(), waigouPlan
											.getGysId());
							if (wgList != null && wgList.size() > 0) {
								bool = true;
								int count = 0;
								for (WaigouPlan w2 : wgList) {
//									Procard p = (Procard) totalDao
//											.getObjectByCondition(
//													" from Procard where id in( select procardId from ProcardWGCenter where wgOrderId = ? )",
//													w2.getId());
//									String str = "";
//									if (p != null) {
//										str = "所对应的业务件号:<b>"
//												+ p.getYwMarkId()
//												+ "</b>生产批次号:<b>"
//												+ p.getSelfCard()
//												+ "</b>订单编号:<b>"
//												+ waigouPlan.getWaigouOrder()
//														.getPlanNumber()
//												+ "</b>件号:<b>"
//												+ waigouPlan.getMarkId()
//												+ "</b>所对应的业务件号:<b>"
//												+ procard.getYwMarkId()
//												+ "</b>生产批次号:<b>"
//												+ procard.getSelfCard()
//												+ "</b>";
//									}
										if(count==0){
											msg.append("请按照订单先后顺序交货,顺序如下:<br/>");
										}
										msg.append(count++ +":"+ "订单号<b>"
											+ w2.getWaigouOrder()
													.getPlanNumber()
											+ "</b>件号:<b>" + w2.getMarkId()
											+ "<br/>");
								}
							}
						}
					}
					if (bool) {
						continue;
					}
					if (waigouPlan.getWaigouOrder().getAddUserCode() != null
							&& !codeList.contains(waigouPlan.getWaigouOrder()
									.getAddUserCode())) {
						codeList.add(waigouPlan.getWaigouOrder()
								.getAddUserCode());
					}
					BeanUtils.copyProperties(waigouPlan, wdd, new String[] {
							"id", "querenTime", "hgNumber", "isshowBl",
							"rukuTime" });
					wdd.setHsPrice(waigouPlan.getHsPrice());
					// 计算剩余可送货数量
					Float syNumber = waigouPlan.getSyNumber()
							- wdd.getShNumber();
					// if (syNumber < 0) {
					// throw new RuntimeException("订单号为："
					// + waigouPlan.getWaigouOrder().getPlanNumber()
					// + "的" + waigouPlan.getMarkId() + "产品，最多的可送"
					// + waigouPlan.getSyNumber() + ",无法送货"
					// + wdd.getShNumber() + ",请确认后重试!");
					// }
					// waigouPlan.setStatus("送货中");
					if (syNumber < 0) {
						syNumber = 0f;
					}
					if (waigouPlan.getNumber() > 1 && syNumber < 0.05) {
						syNumber = 0f;
					}
					// 判断该订单明细所有对应的送货明细总数量加上本次送货有没有超过订单数量wxf。
					String errormsg = mustNotOverflowWgPlanNum(waigouPlan
							.getId(), shNumber, waigouPlan.getNumber(), wdd
							.getRangeOfReceipt(), waigouPlan);
					msg.append(errormsg);
					waigouPlan.setSyNumber(syNumber);
					wdd.setOneCtnNum(wdd.getShNumber() / wdd.getCtn());
					wdd.setAddTime(Util.getDateTime());
					wdd.setWaigouPlanId(waigouPlan.getId());
					if (zuser.getSelfSend() != null
							&& zuser.getSelfSend().equals("yes")) {
						wdd.setStatus("待打印");
					} else {
						wdd.setStatus("待确认");
					}
					totalDao.update(waigouPlan);
					// 更新物料需求汇总上的未送货数量
					if (waigouPlan.getMopId() != null
							&& waigouPlan.getMopId() > 0) {
						Float sumSyNumber = (Float) totalDao
								.getObjectByCondition(
										"select sum(syNumber) from WaigouPlan where mopId=?",
										waigouPlan.getMopId());
						ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
								ManualOrderPlan.class, waigouPlan.getMopId());
						if (mop != null) {
							mop.setWshCount(sumSyNumber);
							totalDao.update(mop);
						}

					}
					wdd.setGysjc(waigouPlan.getGysjc());// 供应商简称
					// wdd.setWaigouDelivery(waigouDelivery);
					wdd.setWwType(waigouPlan.getWaigouOrder().getWwType());// 采购类型
					wdd.setProcessNo(waigouPlan.getProcessNOs());// 工序号
					wdd.setProcessName(waigouPlan.getProcessNames());// 工序号
					wdd.setCaigouUserName(waigouPlan.getWaigouOrder()
							.getAddUserName());
					wdd.setIsprint("NO");
					wdd.setProNumber(waigouPlan.getProNumber());// 项目编号
					try {
						if (waigouPlan.getQuerenTime() == null) {
							waigouPlan.setQuerenTime(Util.getDateTime());
						}
						Long time = Util.getDateDiff(
								waigouPlan.getQuerenTime(),
								"yyyy-MM-dd HH:mm:ss", wdd.getAddTime(),
								"yyyy-MM-dd HH:mm:ss");
						Integer avgProductionTakt = time.intValue();
						wdd.setAvgProductionTakt(avgProductionTakt);
						if ("外委".equals(wdd.getType())) {
							wdd.setAllJiepai(avgProductionTakt.floatValue());
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Float overflowNumber = shNumber - syNumber0;
					if (overflowNumber > 0) {
						wdd.setRemarks("原订单送货数量:" + syNumber0 + "，"
								+ wdd.getWgType() + "收货浮动范围为:"
								+ wdd.getRangeOfReceipt() + wdd.getUnit()
								+ "超出了:" + overflowNumber + wdd.getUnit()
								+ "先送货数量为:" + shNumber);
						AlertMessagesServerImpl.addAlertMessages("待确认送货单管理",
								"您的采购订单有送货申请,"
										+ wdd.getRemarks()
										+ "申请送货的供应商为:"
										+ loginUser.getName()
										+ "("
										+ loginUser.getCode()
										+ "),采购单号为:"
										+ waigouPlan.getWaigouOrder()
												.getPlanNumber() + ".", "1",
								waigouPlan.getWaigouOrder().getAddUserCode());

					}

					wddSet.add(wdd);
				} else {
					msg.append("您有订单编号为:"
							+ waigouPlan.getWaigouOrder().getPlanNumber()
							+ "下件号:" + waigouPlan.getMarkId()
							+ " 未送货数量为0，请勿重新申请送货!<br/>");
					continue;
				}
			}

			StringBuffer shdh = new StringBuffer();
			if (wddSet != null && wddSet.size() > 0) {
				// waigouDelivery.setWddSet(wddSet);
				// Users loginUser = Util.getLoginUser();
				// waigouDelivery.setPlanNumber(shNumber(loginUser));// 采购计划编号
				// waigouDelivery.setAddTime(Util.getDateTime());
				// waigouDelivery.setIsprint("NO");
				// if (zuser.getSelfSend() != null
				// && zuser.getSelfSend().equals("yes")) {
				// waigouDelivery.setStatus("待打印");
				// } else {
				// waigouDelivery.setStatus("待确认");
				// }
				// totalDao.save(waigouDelivery);
				int i = 0;
				WaigouDelivery newwd = null;
				Set<WaigouDeliveryDetail> newwddSet = null;
				for (WaigouDeliveryDetail wdd : wddSet) {
					if (i == 0) {
						newwd = new WaigouDelivery();
						newwddSet = new HashSet<WaigouDeliveryDetail>();
						BeanUtils.copyProperties(waigouDelivery, newwd,
								new String[] { "id", "wddSet" });
						newwd.setPlanNumber(shNumber(loginUser));// 采购计划编号
						newwd.setAddTime(Util.getDateTime());
						newwd.setIsprint("NO");
						if (zuser.getSelfSend() != null
								&& zuser.getSelfSend().equals("yes")) {
							newwd.setStatus("待打印");
						} else {
							newwd.setStatus("待确认");
						}
						if (shdh.length() == 0) {
							shdh.append(newwd.getPlanNumber());
						} else {
							shdh.append("," + newwd.getPlanNumber());
						}
					}
					wdd.setWaigouDelivery(newwd);
					newwddSet.add(wdd);
					i++;
					if (i == 8) {
						newwd.setWddSet(newwddSet);
						totalDao.save(newwd);
						i = 0;
					}
				}
				if (i > 0 && i < 8) {
					newwd.setWddSet(newwddSet);
					totalDao.save(newwd);
				}
				// 发送RTX提醒
				if (codeList.size() > 0) {
					for (String code : codeList) {
						// RtxUtil.sendNotify(codeList,
						// "您采购单有送货申请前前往核对,地址:http://""/WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="+waigouDelivery.getId(),
						// "送货申请提醒","0", "0");
						AlertMessagesServerImpl.addAlertMessages("待确认送货单管理",
								"您的采购订单有送货申请,申请送货的供应商为:" + loginUser.getName()
										+ "(" + loginUser.getCode()
										+ "),送货单号为:" + shdh.toString() + ".",
								"1", code);
					}
				}
				map.put(0, "true");
				map.put(1, shdh.toString());
			}
		}
		if (msg != null && msg.length() > 0) {
			throw new RuntimeException(msg.toString());
		}
		if (map.size() == 0)
			map.put(0, "没有生成送货单");
		return map;
	}

	/**
	 * 获取送货单
	 * 
	 * @param loginUser
	 * @return
	 */
	private String shNumber(Users loginUser) {
		// 得到最大的采购计划编号
		String maxNumber = "DN" + loginUser.getCode()
				+ Util.getDateTime("yyyyMMdd");
		String hql_maxnumber = "select max(planNumber) from WaigouDelivery where planNumber like '%"
				+ maxNumber + "%'";
		Object obj = totalDao.getObjectByCondition(hql_maxnumber);
		if (obj != null) {
			int num2 = 0;
			String maxNumber2 = obj.toString();
			num2 = Integer.parseInt(maxNumber2.substring(
					maxNumber2.length() - 3, maxNumber2.length()));
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
		return maxNumber;
	}

	public List findDeliveryByNumbers(String bacode) {
		if (bacode != null && bacode.length() > 0) {
			bacode = bacode.replaceAll(",", "','");
			bacode = "'" + bacode + "'";
			// System.out.println(bacode);
			List list = totalDao
					.query("select id from WaigouDelivery where planNumber in("
							+ bacode + ")");
			return list;
		}
		return null;
	}

	/****
	 * 查询送货单
	 * 
	 * @param waigouDelivery
	 * @param list
	 * @return
	 */
	@Override
	public Object[] findDeliveryNote(WaigouDelivery waigouDelivery, int pageNo,
			int pageSize, String pageStatus, String firsttime, String endtime) {
		if (waigouDelivery == null) {
			waigouDelivery = new WaigouDelivery();
		}

		WaigouDeliveryDetail waigouDeliveryDetail = null;
		// StringBuffer buffer = new StringBuffer();
		String other = "";
		if (waigouDelivery.getDeliveryDetail() != null) {
			waigouDeliveryDetail = waigouDelivery.getDeliveryDetail();
			other = totalDao.criteriaQueries(waigouDeliveryDetail, "", "");
			other = " id in (select waigouDelivery.id " + other
					+ " and status not in ('取消','删除','退回') )";
			// List<WaigouDeliveryDetail> list = totalDao.query(dHql);
			// for (WaigouDeliveryDetail detail : list) {
			// if("".equals(buffer.toString())){
			// buffer.append(detail.getWaigouDelivery().getId());
			// }else{
			// buffer.append(","+detail.getWaigouDelivery().getId());
			// }
			// }
			waigouDelivery.setDeliveryDetail(null);
		}
		// String other = "";
		// if (!"".equals(buffer.toString())) {
		// other = " id in(" + buffer.toString() + ")";
		// }

		String hql = totalDao.criteriaQueries(waigouDelivery, "addTime",
				new String[] { firsttime, endtime }, other);

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
		if (!"".equals(str)) {
			// 待打印送货单
			list_sh = totalDao.query(hql + str + " order by addTime desc");
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count, list_sh };
		waigouDelivery.setDeliveryDetail(waigouDeliveryDetail);
		return o;
	}

	/****
	 * 查询送货单(供应商)
	 * 
	 * @param waigouDelivery
	 * @param list
	 * @return
	 */
	@Override
	public Object[] findDeliveryNoteByUser(WaigouDelivery waigouDelivery,
			int pageNo, int pageSize, String pageStatus) {
		if (waigouDelivery == null) {
			waigouDelivery = new WaigouDelivery();
		}
		Users loginUser = Util.getLoginUser();
		String hql = totalDao.criteriaQueries(waigouDelivery, null);
		// 当前供应商所遇的送货单
		List list = totalDao.findAllByPage(hql + " order by id desc", pageNo,
				pageSize);
		// 待打印送货单
		List list_sh = totalDao.query(hql + " and status='待打印'");
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
			WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
					.getObjectById(WaigouDelivery.class, id);
			Integer gysId = 0;
			if (waigouDelivery != null) {
				gysId = waigouDelivery.getGysId();
			}
			String hql = "from WaigouDeliveryDetail where waigouDelivery.id=? and gysId="
					+ gysId + " order by markId asc,id desc";
			List list = totalDao.query(hql, id);
			Float sumNum = 0f;
			Float sumqrNum = 0f;
			Float sumheNum = 0f;
			String tag = "";
			if (list != null) {
				for (Object obj : list) {
					WaigouDeliveryDetail w2 = (WaigouDeliveryDetail) obj;
					sumNum += w2.getShNumber();
					if (w2.getQrNumber() != null) {
						sumqrNum += w2.getQrNumber();
					}
					if (w2.getHgNumber() != null) {
						sumheNum += w2.getHgNumber();
					}
					List<String> ywmarkIdList = totalDao
							.query(
									"select distinct(ywMarkId) from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) ",
									w2.getWaigouPlanId());
					if (ywmarkIdList != null && ywmarkIdList.size() > 0) {
						String str = "";
						for (String ywmarkId : ywmarkIdList) {
							str += ywmarkId + "<br/>";
						}
						w2.setStrs(str);
					}
					String inOderNum_hql = "select distinct(orderNumber) from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) ";
					List<String> oderNumList = totalDao.query(inOderNum_hql, w2
							.getWaigouPlanId());
					if (oderNumList != null && oderNumList.size() > 0) {
						StringBuffer sb = new StringBuffer();
						for (String norder : oderNumList) {
							if (sb.length() == 0) {
								sb.append(norder);
							} else {
								sb.append("，" + norder);
							}
						}
						w2.setNeiorderNum(sb.toString());
					}
					if (w2.getType() != null && w2.getType().equals("外委")) {
						Integer pno = Util.getSplitNumber(w2.getProcessNo(),
								";", "max");
						String nextProcessName = (String) totalDao
								.getObjectByCondition(
										"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') "
												+ " and procard.id in(select procardId from ProcardWGCenter where wgOrderId=? ) and processNO >? order by processNO",
										w2.getWaigouPlanId(), pno);
						if (nextProcessName == null) {
							nextProcessName = "无";
						}
						w2.setNextProcessName(nextProcessName);
						tag = "外委";
					}

				}
			}
			Object[] o = { waigouDelivery, list, sumNum, sumqrNum, sumheNum,
					tag };
			return o;
		}
		return null;
	}

	/***
	 * 查询采购订单送货明细
	 * 
	 * @param waigouDelivery
	 * 
	 * 
	 * @return
	 */
	@Override
	public Object[] findWaigouPlanDNDetail(Integer wpId) {
		if (wpId != null) {
			WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
					WaigouPlan.class, wpId);
			// 获取物料动态
			String hql_1 = "from WlWeizhiDt where waigouPlanId = ?";
			List<WlWeizhiDt> wlList = totalDao.query(hql_1, wpId);
			String str_WlWeizhiDt = "";
			if (wlList.size() > 0 && wlList != null) {
				for (WlWeizhiDt wld : wlList) {
					str_WlWeizhiDt += wld.getAddDate() + ":" + wld.getDetail();
				}
			}
			waigouPlan.setWlWeizhiDt(str_WlWeizhiDt);// 用于显示 不存储
			String hql = "from WaigouDeliveryDetail where waigouPlanId=?";
			List list = totalDao.query(hql, wpId);
			Object[] o = { waigouPlan, list };
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
			WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
					.getObjectById(WaigouDelivery.class, deliveryId);
			if (waigouDelivery != null
					&& ("待打印".equals(waigouDelivery.getStatus()) || "送货中"
							.equals(waigouDelivery.getStatus()))) {
				String status = waigouDelivery.getStatus();
				String time = Util.getDateTime();
				waigouDelivery.setStatus("送货中");
				waigouDelivery.setIsprint("YES");
				waigouDelivery.setPrintTime(time);
				Set<WaigouDeliveryDetail> wddSet = waigouDelivery.getWddSet();
				for (WaigouDeliveryDetail waigouDeliveryDetail : wddSet) {
					waigouDeliveryDetail.setStatus("送货中");
					waigouDeliveryDetail.setPrintTime(time);
					waigouDeliveryDetail.setIsprint("YES");
					// 查询对应订单明细
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class,
									waigouDeliveryDetail.getWaigouPlanId());
					if (waigouPlan != null) {
						// String wlwzdtmes = waigouPlan.getWlWeizhiDt();
						// if (wlwzdtmes == null) {
						// wlwzdtmes = "";
						// }
						String wlwzdtmes = "";
						if ("送货中".equals(status) && wlwzdtmes.length() > 0) {
							wlwzdtmes = wlwzdtmes.substring(0, wlwzdtmes
									.lastIndexOf("出发时间:") + 5)
									+ Util.getDateTime()
									+ wlwzdtmes.substring(wlwzdtmes
											.lastIndexOf("<br/>当前位置:"),
											wlwzdtmes.length());
						} else {
							wlwzdtmes = "送货中:<br/>供应商:"
									+ waigouDelivery.getGysName()
									+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
									+ waigouDelivery.getId() + "'>"
									+ waigouDelivery.getPlanNumber()
									+ "</a><br/>送货数量:"
									+ waigouDeliveryDetail.getShNumber()
									+ waigouDeliveryDetail.getUnit()
									+ ";<br/>出发时间:" + Util.getDateTime()
									+ "<br/>当前位置:"
									+ waigouDelivery.getChufaDizhi() + " 至 "
									+ waigouDelivery.getDaodaDizhi() + ";<hr/>";
						}

						// 更新Procard状态
						// String hql_procard =
						// "from Procard where id in (select procardId from ProcardWGCenter where wgOrderId=?) ";
						String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id = ?)";
						List list_procrd = totalDao.query(hql_procard,
								waigouPlan.getMopId());
						for (int j = 0; j < list_procrd.size(); j++) {
							Procard procard = (Procard) list_procrd.get(j);
							procard.setWlstatus("送货中");
							// String wlwzdtmes="";
							if (procard.getWlWeizhiDt() == null) {
								procard.setWlWeizhiDt("");
							}

							if ("送货中".equals(status)) {
								// wlwzdtmes = procard.getWlWeizhiDt();
								wlwzdtmes = wlwzdtmes.substring(0, wlwzdtmes
										.lastIndexOf("出发时间:") + 5)
										+ Util.getDateTime()
										+ wlwzdtmes.substring(wlwzdtmes
												.lastIndexOf("<br/>当前位置:"),
												wlwzdtmes.length());
							} else {
								wlwzdtmes = "送货中:<br/>供应商:"
										+ waigouDelivery.getGysName()
										+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
										+ waigouDelivery.getId() + "'>"
										+ waigouDelivery.getPlanNumber()
										+ "</a><br/>送货数量:"
										+ waigouDeliveryDetail.getShNumber()
										+ waigouDeliveryDetail.getUnit()
										+ ";<br/>出发时间:" + Util.getDateTime()
										+ "<br/>当前位置:"
										+ waigouDelivery.getChufaDizhi()
										+ " 至 "
										+ waigouDelivery.getDaodaDizhi()
										+ ";<hr/>";
							}
							// procard.setWlWeizhiDt(wlwzdtmes);
							totalDao.update(procard);
							WlWeizhiDt wDt = new WlWeizhiDt(procard.getId(),
									null, null, wlwzdtmes);
							totalDao.save(wDt);
						}
						// 更新采购订单明细
						// waigouPlan.setWlWeizhiDt(wlwzdtmes);
						waigouPlan.setStatus("送货中");
						totalDao.update(waigouPlan);
						// 更新物料位置
						WlWeizhiDt wDt1 = new WlWeizhiDt(null, waigouPlan
								.getId(), null, wlwzdtmes);
						totalDao.save(wDt1);
						// 查询采购订单,更新状态
						WaigouOrder waigouOrder = (WaigouOrder) totalDao
								.getObjectById(WaigouOrder.class, waigouPlan
										.getWaigouOrder().getId());
						waigouOrder.setStatus("送货中");
						totalDao.update(waigouOrder);
					}
				}
				totalDao.update(waigouDelivery);
				return "";
			}
		}
		return null;
	}

	/****
	 * 查询所有待确认的送货单
	 * 
	 * @return
	 */
	@Override
	public Object[] findDqrDelivery(String pageStatus,
			WaigouDelivery waigouDelivery, int pageNo, int pageSize) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		String appendType = "";
		String categoryName = ",";
		List list1 = null;
		if ("ww".equals(pageStatus)) {
			appendType = " and type = '外委'";
		} else if ("wg".equals(pageStatus)) {
			appendType = " and type in ('外购','研发')";
			String hql = "SELECT c.name from Category c join c.userSet u where u.id="
					+ user.getId();
			List<String> strList = totalDao.query(hql);
			if (strList == null || strList.size() == 0) {
				categoryName = " and 1=1";
			} else {
				categoryName = " and wgType in(";
				int i = 0;
				for (String str : strList) {
					if (i == 0) {
						categoryName += "'" + str + "'";
					} else {
						categoryName += ",'" + str + "'";
					}
					i++;
				}
				categoryName += ")";
			}
		} else if ("fl".equals(pageStatus)) {
			appendType = " and type= '辅料' ";
		}
		if (categoryName.length() > 0 && categoryName != null) {
			categoryName = categoryName.substring(1);
			if (waigouDelivery != null
					&& waigouDelivery.getPlanNumber() != null
					&& !"".equals(waigouDelivery.getPlanNumber())) {
				String hql1 = "select waigouDelivery.id from WaigouDeliveryDetail where status in ('待打印','送货中','待检验','待入库','入库') "
						+ appendType + categoryName;
				List<String> list = totalDao.query(hql1);
				if (list != null && !list.isEmpty()) {
					String hqls = "from WaigouDelivery where planNumber like '%"
							+ waigouDelivery.getPlanNumber()
							+ "%' and id in ("
							+ list.toString().replaceAll("\\[", "").replaceAll(
									"\\]", "").replaceAll(" ", "")
							+ ") order by id desc";
					list1 = totalDao.findAllByPage(hqls, pageNo, pageSize);
					int count = totalDao.getCount(hqls);
					Object[] o = { list1, count };
					return o;
				}
			} else {
				String hql1 = "select waigouDelivery.id from WaigouDeliveryDetail where status='送货中'  "
						+ appendType + categoryName;
				List<String> list = totalDao.query(hql1);
				if (list != null && !list.isEmpty()) {
					String hqls = "from WaigouDelivery where id in ("
							+ list.toString().replaceAll("\\[", "").replaceAll(
									"\\]", "").replaceAll(" ", "")
							+ ") order by id desc";
					list1 = totalDao.findAllByPage(hqls, pageNo, pageSize);
					int count = totalDao.getCount(hqls);
					Object[] o = { list1, count };
					return o;
				}
			}
		}
		return null;
	}

	/****
	 * 查询所有待确认的送货单
	 * 
	 * @return
	 */
	public List findNoPassDelivery() {
		String hql = "from WaigouDeliveryDetail where isHege='不合格'";
		return totalDao.query(hql);
	}

	@Override
	public List findDqrDeliRuGui() {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		// List list1 = totalDao
		// .query("from WaigouDeliveryDetail where status='待存柜' and (qrNumber > ycNumber or ycNumber is null)");
		// String hql =
		// "SELECT c.name from Category c join c.userSet u where u.id="
		// + user.getId();
		// List<String> strList = totalDao.query(hql);
		// String categoryName = "";
		// if (strList == null || strList.size() == 0) {
		// categoryName = " and 1=1";
		// } else {
		// categoryName = " and wgType in(";
		// int i = 0;
		// for (String str : strList) {
		// if (i == 0) {
		// categoryName += "'" + str + "'";
		// } else {
		// categoryName += ",'" + str + "'";
		// }
		// i++;
		// }
		// categoryName += ")";
		// }
		// if (categoryName.length() > 0 && categoryName != null) {
		// }
		// categoryName = categoryName.substring(1);
		String hql1 = "from WaigouDeliveryDetail where status='待存柜' and (qrNumber > ycNumber or ycNumber is null) ";// and
		// type='外购'
		// + categoryName;
		List list2 = totalDao.query(hql1);
		// list1.addAll(list2);
		return list2;
		// String hql =
		// "from WaigouDeliveryDetail where status='待存柜' and (qrNumber > ycNumber or ycNumber is null)";
		// return totalDao.query(hql);
	}

	/****
	 * 扫描条码查询送货单明细
	 * 
	 * @return
	 */
	@Override
	public Object[] findDeliveryDeByBacode(String bacode) {
		if (bacode != null) {
			WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
					.getObjectByCondition(
							"from WaigouDelivery where planNumber=?  ", bacode);
			if (waigouDelivery != null) {
				String hql = "from WaigouDeliveryDetail where waigouDelivery.id=? order by markId asc,id desc";
				List<WaigouDeliveryDetail> list = totalDao.query(hql,
						waigouDelivery.getId());
				// 查询最新的物料确认位置
				String hql_wlWz = "select qrWeizhi from WaigouDeliveryDetail where qrWeizhi is not null and qrWeizhi<>'' order by querenTime desc";
				String qrWeizhi = (String) totalDao
						.getObjectByCondition(hql_wlWz);
				if (qrWeizhi == null || "".equals(qrWeizhi)) {
					qrWeizhi = "待检区";
				}
				for (WaigouDeliveryDetail waigouDeliveryDetail : list) {
					waigouDeliveryDetail.setQrWeizhi(qrWeizhi);
				}
				for (WaigouDeliveryDetail wgddList : list) {
					String hql1 = " select  bili from YuanclAndWaigj where markId = ? and bili is not null and bili>0 ";
					Float bili = (Float) totalDao.getObjectByCondition(hql1,
							wgddList.getMarkId());
					if (bili != null) {
						float zhuanhuanNum = (float) Math.floor(wgddList
								.getShNumber()
								/ bili);
						wgddList.setZhuanhuanNum(zhuanhuanNum);
					}
					Goods goods = (Goods) totalDao
							.getObjectByCondition(
									" from Goods where goodsMarkId = ? and goodsZhishu is not null and goodsZhishu <> '' ",
									wgddList.getMarkId());
					if (goods != null) {
						wgddList.setZhuanhuanUnit(goods.getGoodsStoreZHUnit());
					}

				}
				Object[] o = { waigouDelivery, list };
				return o;
			}
		}
		return null;
	}

	/***
	 * 送货单确认数量
	 * 
	 * @param waigouDelivery
	 * 
	 * 
	 * @return
	 */
	@Override
	public String updateDeliveryToQr(List list) {
		if (list != null) {
			String mess = "";
			for (int i = 0; i < list.size(); i++) {
				WaigouDeliveryDetail waigouDeliveryDetail = (WaigouDeliveryDetail) list
						.get(i);
				if (waigouDeliveryDetail.getQrNumber() > waigouDeliveryDetail
						.getShNumber()) {
					mess += waigouDeliveryDetail.getMarkId() + "的确认数量不能超过送货数量!";
				}
			}
			if (!"".equals(mess)) {
				return mess;
			}
			Users loginUser = Util.getLoginUser();
			for (int i = 0; i < list.size(); i++) {
				WaigouDeliveryDetail waigouDeliveryDetail = (WaigouDeliveryDetail) list
						.get(i);
				WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
						.getObjectById(WaigouDeliveryDetail.class,
								waigouDeliveryDetail.getId());
				if (wddOld.getShNumber() == 0) {
					wddOld.setStatus("退回");
					wddOld.setQrNumber(0f);
					totalDao.update(wddOld);
					continue;
				}
				if (wddOld != null) {
					if (!"送货中".equals(wddOld.getStatus())
							&& !"待存柜".equals(wddOld.getStatus())) {
						continue;
					}
					// if(!"送货中".equals(wddOld.getWaigouDelivery().getStatus())){
					// return "该送货单已签收确认过数量，请勿重复确认!";
					// }
					// if(!"送货中".equals(wddOld.getWaigouDelivery().getStatus())
					// && !"待存柜".equals(wddOld.getWaigouDelivery().getStatus())
					// ){
					// return "该送货单已签收确认过数量，请勿重复确认!";
					// }
					String shStatus = "";
					boolean b = true;
					if (waigouDeliveryDetail.getQrNumber() <= 0) {
						waigouDeliveryDetail.setQrNumber(0F);
						shStatus = "退回";
					} else {
						CodeTranslation codetr = (CodeTranslation) totalDao
								.getObjectByCondition(" from CodeTranslation where type = 'sys' and keyCode = 'WAREHOUSE' and valueCode = 'PEBS柔性仓库' ");
						if (codetr != null) {
							shStatus = "待存柜";
							b = false;
						} else {
							shStatus = "待检验";
						}
					}
					boolean isfl = false;
					if ("辅料".equals(wddOld.getType())) {
						isfl = true;
						// shStatus = "待入库";
						wddOld.setHgNumber(waigouDeliveryDetail.getQrNumber());
						wddOld.setQrWeizhi("综合库-"
								+ waigouDeliveryDetail.getChangqu() + "-"
								+ waigouDeliveryDetail.getKuwei());
					} else {
						wddOld.setQrWeizhi("待检库-"
								+ waigouDeliveryDetail.getChangqu() + "-"
								+ waigouDeliveryDetail.getKuwei());
					}
					wddOld.setStatus(shStatus);
					wddOld.setQuerenTime(Util.getDateTime());
					wddOld.setQrNumber(waigouDeliveryDetail.getQrNumber());
					// 生成检验批次
					wddOld.setZhuanhuanNum(waigouDeliveryDetail
							.getZhuanhuanNum());
					wddOld.setZhuanhuanUnit(waigouDeliveryDetail
							.getZhuanhuanUnit());
					String maxexamineLot = "";
					String mouth = new SimpleDateFormat("yyyyMM")
							.format(new Date());
					String hql_examineLot = "select max(examineLot) from WaigouDeliveryDetail where examineLot like '"
							+ mouth + "%'";
					Object object = (Object) totalDao
							.getObjectByCondition(hql_examineLot);
					if (object != null) {
						Long selfCard = Long.parseLong(object.toString()) + 1;// 当前最大流水卡片
						maxexamineLot = selfCard.toString();
					} else {
						maxexamineLot = mouth + "00001";
					}
					wddOld.setExamineLot(maxexamineLot);
					wddOld.setQrUsersName(Util.getLoginUser().getName());// 签收人
					wddOld.setQrUsersId(Util.getLoginUser().getId());// 签收人ID
					try {
						wddOld.setQuejianRate(wddOld.getQrNumber()
								/ wddOld.getShNumber());
					} catch (Exception e) {
						wddOld.setQuejianRate(0f);// 缺件率
					}
					try {
						Long time = Util.getDateDiff(wddOld.getAddTime(),
								"yyyy-MM-dd HH:mm:ss", wddOld.getQuerenTime(),
								"yyyy-MM-dd HH:mm:ss");
						Float avgDeliveryTime = (time.floatValue()) / 60 / 60 / 24;
						wddOld.setAvgDeliveryTime(avgDeliveryTime);
						Float cgperiod = wddOld.getAvgDeliveryTime()
								+ ((wddOld.getAvgProductionTakt() * wddOld
										.getQrNumber()) / 60 / 60 / 24);
						wddOld.setCgperiod(cgperiod);
					} catch (Exception e) {
						e.printStackTrace();
					}
					totalDao.update(wddOld);
					YuanclAndWaigj ycl = null;
					ProcardTemplate pt = null;
					GysMarkIdjiepai gysjiepai = null;
					List<ProcessTemplate> processList = null;
					String hql = "";
					String hql1 = "";
					if (wddOld.getBanben() != null
							&& wddOld.getBanben().length() > 0) {
						hql += " and banbenhao = '" + wddOld.getBanben() + "'";
						hql1 += " and banBenNumber = '" + wddOld.getBanben()
								+ "'";
					} else {
						hql += " and (banbenhao is null or banbenhao = '')";
						hql1 += " and (banBenNumber is null or banBenNumber = '')";
					}
					if ("外购".equals(wddOld.getType())) {
						// 查询出外购件库信息
						ycl = (YuanclAndWaigj) totalDao
								.getObjectByCondition(
										" from YuanclAndWaigj where markId =? and kgliao =? and (banbenStatus = '默认'  or banbenStatus = '' or banbenStatus is null)"
												+ hql, wddOld.getMarkId(),
										wddOld.getKgliao());
						pt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										" from ProcardTemplate where markId = ? and kgliao = ? and (banbenStatus = '默认'  or banbenStatus = '' or banbenStatus is null) "
												+ hql1, wddOld.getMarkId(),
										wddOld.getKgliao());
						gysjiepai = (GysMarkIdjiepai) totalDao
								.getObjectByCondition(
										" from GysMarkIdjiepai where markId =? and kgliao =? and zhuserId = ? "
												+ hql1, wddOld.getMarkId(),
										wddOld.getKgliao(), wddOld.getGysId());
					} else if ("外委".equals(wddOld.getType())) {
						processList = totalDao
								.query(
										" from ProcessTemplate where procardTemplate.id in (select id from ProcardTemplate "
												+ " where markId = ?  and (banbenStatus = '默认'  or banbenStatus = '' or banbenStatus is null)"
												+ hql1
												+ ") "
												+ " and productStyle='外委'  ",
										wddOld.getMarkId());
					}

					// 按从小到大排列查询改件号之前所有的采购周期--单个供应商
					String hql_allProcess = "from WaigouDeliveryDetail where markId = ? and cgperiod is not null and cgperiod > 0 order by cgperiod";
					Integer processcount = totalDao.getCount(hql_allProcess,
							wddOld.getMarkId());
					if (processcount != null && processcount > 0) {
						List getzws = new ArrayList();
						if (processcount % 2 == 0) {// 偶数 （1/2+1）
							getzws = totalDao.findAllByPage(hql_allProcess,
									(int) Math.ceil(processcount / 2 / 2), 2,
									wddOld.getMarkId());
						} else {// 奇数
							getzws = totalDao.findAllByPage(hql_allProcess,
									(int) Math.ceil(processcount / 2), 1,
									wddOld.getMarkId());
						}
						Integer avgProductionTakt = 0;// 生产节拍(s)
						Float avgDeliveryTime = 0F;
						Float cgperiod = 0F;
						int getzwsize = getzws.size();
						Float allJiepai = 0F;
						for (int j = 0; j < getzwsize; j++) {
							WaigouDeliveryDetail wdds = (WaigouDeliveryDetail) getzws
									.get(j);
							if (wdds.getAvgProductionTakt() != null) {
								avgProductionTakt += wdds
										.getAvgProductionTakt();
							}
							if (wdds.getAvgDeliveryTime() != null) {
								avgDeliveryTime += wdds.getAvgDeliveryTime();
							}
							if (wdds.getCgperiod() != null) {
								cgperiod += wdds.getCgperiod();
							}
							if (wdds.getAllJiepai() != null) {
								allJiepai += wdds.getAllJiepai();
							}
						}
						avgDeliveryTime = avgDeliveryTime / getzwsize;
						avgProductionTakt = avgProductionTakt / getzwsize;
						cgperiod = cgperiod / getzwsize;
						allJiepai = allJiepai / getzwsize;

						if ("外购".equals(wddOld.getType()) && ycl != null
								&& pt != null) {
							ycl.setAvgDeliveryTime(avgDeliveryTime);
							ycl.setAvgProductionTakt(avgProductionTakt);
							ycl.setCgperiod(cgperiod);
							pt.setAvgDeliveryTime(avgDeliveryTime);
							pt.setAvgProductionTakt(avgProductionTakt);
						} else if ("外委".equals(wddOld.getType())) {
							if (processList != null && processList.size() > 0) {
								for (ProcessTemplate process : processList) {
									process.setAllJiepai(allJiepai);
									process
											.setDeliveryDuration(avgDeliveryTime);
									totalDao.update(process);
								}
							}
						}
					}
					if (gysjiepai != null) {
						// 按从小到大排列查询改件号之前所有的采购周期
						String hql_allProcess_gys = "from WaigouDeliveryDetail where markId = ? and gysId =? and cgperiod is not null and cgperiod > 0 order by cgperiod";
						Integer processcount_gys = totalDao.getCount(
								hql_allProcess_gys, wddOld.getMarkId(), wddOld
										.getGysId());
						if (processcount_gys != null && processcount_gys > 0) {
							List getzws = new ArrayList();
							if (processcount % 2 == 0) {// 偶数 （1/2+1）
								getzws = totalDao.findAllByPage(hql_allProcess,
										(int) Math.ceil(processcount / 2 / 2),
										2, wddOld.getMarkId());
							} else {// 奇数
								getzws = totalDao.findAllByPage(hql_allProcess,
										(int) Math.ceil(processcount / 2), 1,
										wddOld.getMarkId());
							}
							Integer avgProductionTakt = 0;// 生产节拍(s)
							Float avgDeliveryTime = 0F;
							int getzwsize = getzws.size();
							for (int j = 0; j < getzwsize; j++) {
								WaigouDeliveryDetail wdds = (WaigouDeliveryDetail) getzws
										.get(j);
								if (wdds.getAvgProductionTakt() != null) {
									avgProductionTakt += wdds
											.getAvgProductionTakt();
								}
								if (wdds.getAvgDeliveryTime() != null) {
									avgDeliveryTime += wdds
											.getAvgDeliveryTime();
								}
							}
							avgDeliveryTime = avgDeliveryTime / getzwsize;
							avgProductionTakt = avgProductionTakt / getzwsize;

							gysjiepai.setAvgDeliveryTime(avgDeliveryTime);
							gysjiepai.setAvgProductionTakt(avgProductionTakt);
							gysjiepai
									.setAvgCount((gysjiepai.getAvgCount() == null) ? 1
											: gysjiepai.getAvgCount() + 1);
						}
					}
					if (ycl != null)
						totalDao.update(ycl);
					if (pt != null)
						totalDao.update(pt);
					if (gysjiepai != null)
						totalDao.update(gysjiepai);
					// 更新送货单状态
					WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
							.getObjectById(WaigouDelivery.class, wddOld
									.getWaigouDelivery().getId());
					if (!"退回".equals(shStatus)) {
						waigouDelivery.setStatus(shStatus);
					}
					totalDao.update(waigouDelivery);

					// 更新采购订单信息
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class, wddOld
									.getWaigouPlanId());
					if (waigouPlan != null) {
						if (waigouPlan.getWwSource() != null
								&& waigouPlan.getWwSource().equals("BOM外委")) {
							List<WaigouWaiweiPlan> wwpList = totalDao
									.query(
											"from WaigouWaiweiPlan where id in(select wwxlId from ProcardWGCenter where wgOrderId=?)",
											waigouPlan.getId());
							if (wwpList != null && wwpList.size() > 0) {
								for (WaigouWaiweiPlan wwp : wwpList) {
									wwp.setAcArrivalTime(Util.getDateTime());
									totalDao.update(wwp);
								}
							}
						}
						// 查询采购订单，更新状态
						WaigouOrder waigouOrder = (WaigouOrder) totalDao
								.getObjectById(WaigouOrder.class, waigouPlan
										.getWaigouOrder().getId());
						if (!"退回".equals(shStatus)) {
							waigouOrder.setStatus(shStatus);
						}
						totalDao.update(waigouOrder);

						// 存在缺件，将采购订单表的未送数量加回去
						// 缺件数量
						Float qjNumber = wddOld.getShNumber()
								- wddOld.getQrNumber();
						if (wddOld.getQrNumber() >= waigouPlan.getNumber()) {
							qjNumber = 0f;
						}
						String qjnumberMes = "";
						if (qjNumber > 0) {
							waigouPlan.setSyNumber(waigouPlan.getSyNumber()
									+ qjNumber);
							qjnumberMes = "(缺件：" + qjNumber + wddOld.getUnit()
									+ ")";
						}
						if (!"退回".equals(shStatus)) {
							waigouPlan.setStatus(shStatus);
						} else {
							waigouPlan.setStatus("生产中");
						}
						String wlwzdtmes = "仓库确认:<br/>供应商:"
								+ waigouDelivery.getGysName()
								+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
								+ waigouDelivery.getId() + "'>"
								+ waigouDelivery.getPlanNumber()
								+ "</a><br/>确认数量:" + wddOld.getQrNumber()
								+ wddOld.getUnit() + qjnumberMes + "<br/>确认人:"
								+ loginUser.getName() + ";<br/>确认时间:"
								+ Util.getDateTime() + "<br/>当前位置:"
								+ wddOld.getQrWeizhi() + "<hr/>";
						// waigouPlan.setWlWeizhiDt(waigouPlan.getWlWeizhiDt()
						// + wlwzdtmes);
						// System.out.println(wlwzdtmes);
						WlWeizhiDt wldt = new WlWeizhiDt(null, waigouPlan
								.getId(), null, wlwzdtmes);

						totalDao.save(wldt);
						if (waigouPlan.getQsNum() != null) {
							waigouPlan.setQsNum(waigouPlan.getQsNum()
									+ wddOld.getQrNumber());
						} else {
							waigouPlan.setQsNum(wddOld.getQrNumber());
						}
						if (isfl) {
							if (waigouPlan.getHgNumber() != null) {
								waigouPlan.setHgNumber(waigouPlan.getHgNumber()
										+ wddOld.getHgNumber());
							} else {
								waigouPlan.setHgNumber(wddOld.getHgNumber());
							}
						}
						waigouPlan.setAcArrivalTime(Util.getDateTime());// 实际到货时间
						totalDao.update(waigouPlan);
						// 更新物料需求汇总上的签收数量
						if (waigouPlan.getMopId() != null
								&& waigouPlan.getMopId() > 0) {
							// Float sumSyNumber = (Float) totalDao
							// .getObjectByCondition(
							// waigouPlan.getMopId());
							// "select sum(qsNum) from WaigouPlan where mopId=?",
							ManualOrderPlan mop = (ManualOrderPlan) totalDao
									.get(ManualOrderPlan.class, waigouPlan
											.getMopId());
							if (mop != null) {
								if (mop.getQsCount() != null) {
									mop.setQsCount(mop.getQsCount()
											+ wddOld.getQrNumber());
								} else {
									mop.setQsCount(wddOld.getQrNumber());
								}
								totalDao.update(mop);
							}

						}

						// 查询采购明细对应生产批次
						// String hql_procard =
						// "from Procard where id in (select procardId from ProcardWGCenter where wgOrderId=?) ";
						String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id = ?)";
						List list_procrd = totalDao.query(hql_procard,
								waigouPlan.getMopId());
						for (int j = 0; j < list_procrd.size(); j++) {
							Procard procard = (Procard) list_procrd.get(j);
							procard.setWlstatus(shStatus);
							if (procard.getWlWeizhiDt() == null) {
								procard.setWlWeizhiDt("");
							}
							// procard.setWlWeizhiDt(procard.getWlWeizhiDt()
							// + wlwzdtmes);
							totalDao.update(procard);
							WlWeizhiDt wldt_procard = new WlWeizhiDt(procard
									.getId(), null, null, wlwzdtmes);
							totalDao.save(wldt_procard);
						}
					}
					// 存放到待检库;
					// if (!"辅料".equals(wddOld.getType())) {
					if (wddOld.getQrNumber() > 0) {
						GoodsStore goodsStore = (GoodsStore) totalDao
								.getObjectByCondition(
										" from GoodsStore where goodsStoreWarehouse = '待检库' and  wwddId = ?",
										wddOld.getId());
						if (goodsStore == null) {
							// 生成待检库入库历史记录
							addGsdaijian(waigouDeliveryDetail, wddOld,
									waigouDelivery);
							// 生成外购件来料日报表;
							comeRibao(wddOld, ycl, waigouDelivery);
						} else {
							// 更新库存 检验批次
							updateGoodsStore(wddOld, hql, waigouDelivery,
									goodsStore);
						}
					}

					// }
					if (wddOld != null) {
						String hql_qualifiedRate = "From QualifiedRate where gysid =?";
						ZhUser zhUser = (ZhUser) totalDao.get(ZhUser.class,
								wddOld.getGysId());
						waigouPlan = (WaigouPlan) totalDao.get(
								WaigouPlan.class, wddOld.getWaigouPlanId());
						if (waigouPlan.getShArrivalTime() == null
								|| "".equals(waigouPlan.getShArrivalTime())) {
							waigouPlan.setShArrivalTime(Util.getDateTime());
						}
						boolean zhunshi = waigouPlan.getShArrivalTime()
								.compareTo(Util.getDateTime()) < 0
								|| waigouPlan.getShArrivalTime().compareTo(
										Util.getDateTime()) == 0;// 是否准时到达
						QualifiedRate qr = (QualifiedRate) totalDao
								.getObjectByCondition(hql_qualifiedRate, wddOld
										.getGysId());
						if (qr != null) {
							// 更新供应商合格率
							updateQfr(wddOld, zhUser, zhunshi, qr);
						} else {
							// 添加供应商合格信息
							addQfr(wddOld, zhUser);
						}
						totalDao.update(zhUser);
						// 更新供应商送货的各种率
						updateLv(wddOld, zhunshi);

					}

					// // 发消息给检验员;
					// String hql_user =
					// "SELECT u.id from Category c join c.userSet u where c.name=?";
					// List<Integer> userIdList = totalDao.query(hql_user,
					// wddOld
					// .getWgType());
					// Integer[] userIds = null;
					// if (userIdList != null && userIdList.size() > 0) {
					// userIds = new Integer[userIdList.size()];
					// for (int j = 0; j < userIdList.size(); j++) {
					// userIds[j] = userIdList.get(j);
					if (b) {
						// 发消息给检验员;
						String hql_user = "SELECT u.id from Category c join c.userSet u where c.name=?";
						List<Integer> userIdList = totalDao.query(hql_user,
								wddOld.getWgType());
						Integer[] userIds = null;
						if (userIdList != null && userIdList.size() > 0) {
							userIds = new Integer[userIdList.size()];
							for (int j = 0; j < userIdList.size(); j++) {
								userIds[j] = userIdList.get(j);
							}
						}
						// AlertMessagesServerImpl.addAlertMessages("检验提醒",
						// "送货单号:"
						// + wddOld.getWaigouDelivery()
						// .getPlanNumber() + ",件号:"
						// + wddOld.getMarkId() + "" + ",名称:"
						// + wddOld.getProName() + ",检验批次:"
						// + wddOld.getExamineLot() + ","
						// + "已签收。确认数量:" + wddOld.getQrNumber()
						// + "(" + wddOld.getUnit() + ")。请及时检验",
						// userIds, "", true, "false");
					}
				}
			}
			return "";
		}
		return null;
	}

	/**
	 * 更新供应商送货的各种率
	 * 
	 * @param wddOld
	 * @param zhunshi
	 */
	private void updateLv(WaigouDeliveryDetail wddOld, boolean zhunshi) {
		String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());// 当前时间
		String year = nowDate.substring(0, 4);// 获取年份
		// 计算日合格率
		String hql_Day = "from DayHege where day =? and gysid = ?";
		String yearmouthday = nowDate.substring(0, 10);// 获取当前年月日
		DayHege dy = (DayHege) totalDao.getObjectByCondition(hql_Day,
				yearmouthday, wddOld.getGysId());
		if (dy != null) {// 是否存在记录
			dy.setShijiyunsongCount(dy.getShijiyunsongCount()
					+ wddOld.getQrNumber());// 实际送货数量=总送货数量
			dy.setShenqinCount(dy.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
			try {
				dy.setQuejianrate(dy.getShijiyunsongCount()
						/ dy.getShenqinCount());// 缺件率
			} catch (Exception e) {
				dy.setQuejianrate(0f);// 缺件率
			}
			if (zhunshi) {// 判断是否准时
				dy.setZhunshijiaofuCount(dy.getZhunshijiaofuCount() + 1);
				dy.setJiaofuCount(dy.getJiaofuCount() + 1);
				try {
					dy.setJiaofuhege(dy.getZhunshijiaofuCount()
							/ dy.getJiaofuCount());
				} catch (Exception e) {
					dy.setJiaofuhege(0f);// 缺件率
				}
			} else {
				dy.setZhunshijiaofuCount(dy.getZhunshijiaofuCount());
				dy.setJiaofuCount(dy.getJiaofuCount() + 1);
			}
			try {
				dy.setJiaofuhege(dy.getZhunshijiaofuCount()
						/ dy.getJiaofuCount());
			} catch (Exception e) {
				dy.setJiaofuhege(0f);// 缺件率
			}
			totalDao.update(dy);
		} else {
			DayHege dy1 = new DayHege();
			dy1.setGysid(wddOld.getGysId());// 供应商名字
			dy1.setGysname(wddOld.getGysName());// 供应商名字
			dy1.setDay(yearmouthday);
			// 总合格率数据
			dy1.setBhgCount(0F);// 不合格数量
			dy1.setShCount(0F);// 总送货数量
			dy1.setQualifiedrate(0F);// 合格率
			// 不良品
			dy1.setCheckNopass(0F);// 检验不合格
			dy1.setNoProduce(0F);
			// 批次合格率数据
			dy1.setPiciCount(0F);// 总批次
			dy1.setPicihgCount(0F);// 合格批次
			dy1.setPiciQualifiedrate(0F);// 批次率
			// 交付及时率数据
			dy1.setJiaofuCount(1F);// 交付总次数
			dy1.setZhunshijiaofuCount(1F);// 准时交付次数
			dy1.setJiaofuhege(dy1.getZhunshijiaofuCount()
					/ dy1.getJiaofuCount());
			// 缺件率数据
			dy1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
			dy1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
			try {
				dy1.setQuejianrate(dy1.getShijiyunsongCount()
						/ dy1.getShenqinCount());// 缺件率
			} catch (Exception e) {
				dy1.setQuejianrate(0f);// 缺件率
			}

			totalDao.save(dy1);
		}
		/* 周合格率计算 */
		String hql_week = "from ZhouHege where week =? and gysid = ?";
		String week = year + "-" + Integer.toString(Util.getNowWeek()) + "周";// 获取年周数
		ZhouHege zh = (ZhouHege) totalDao.getObjectByCondition(hql_week, week,
				wddOld.getGysId());
		if (zh != null) {
			zh.setShijiyunsongCount(zh.getShijiyunsongCount()
					+ wddOld.getQrNumber());// 实际送货数量=总送货数量
			zh.setShenqinCount(zh.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
			try {
				zh.setQuejianrate(zh.getShijiyunsongCount()
						/ zh.getShenqinCount());// 缺件率
			} catch (Exception e) {
				zh.setQuejianrate(0f);// 缺件率
			}
			if (zhunshi) {// 判断是否准时
				zh.setZhunshijiaofuCount(zh.getZhunshijiaofuCount() + 1);
				zh.setJiaofuCount(zh.getJiaofuCount() + 1);
				zh.setJiaofuhege(zh.getZhunshijiaofuCount()
						/ zh.getJiaofuCount());
			} else {
				zh.setZhunshijiaofuCount(zh.getZhunshijiaofuCount());
				zh.setJiaofuCount(zh.getJiaofuCount() + 1);
			}
			try {
				zh.setJiaofuhege(zh.getZhunshijiaofuCount()
						/ zh.getJiaofuCount());// 缺件率
			} catch (Exception e) {
				zh.setJiaofuhege(0f);// 缺件率
			}
			totalDao.update(zh);
		} else {
			ZhouHege zh1 = new ZhouHege();
			zh1.setGysid(wddOld.getGysId());// 供应商名字
			zh1.setGysname(wddOld.getGysName());// 供应商名字
			zh1.setWeek(week);
			// 总合格率数据
			zh1.setBhgCount(0F);// 不合格数量
			zh1.setShCount(0F);// 总送货数量
			zh1.setQualifiedrate(0F);// 合格率
			// 不良品
			zh1.setCheckNopass(0F);// 检验不合格
			zh1.setNoProduce(0F);
			// 批次合格率数据
			zh1.setPiciCount(0F);// 总批次
			zh1.setPicihgCount(0F);// 合格批次
			zh1.setPiciQualifiedrate(0F);// 批次率
			// 交付及时率数据
			// if (zhunshi) {// 判断是否准时
			zh1.setJiaofuCount(1F);// 交付总次数
			zh1.setZhunshijiaofuCount(1F);// 准时交付次数
			// } else {
			// zh1.setJiaofuCount(0F);// 交付总次数
			// zh1.setZhunshijiaofuCount(1F);// 准时交付次数
			// }
			zh1.setJiaofuhege(zh1.getZhunshijiaofuCount()
					/ zh1.getJiaofuCount());
			// 缺件率数据
			zh1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
			zh1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
			try {
				zh1.setQuejianrate(zh1.getShijiyunsongCount()
						/ zh1.getShenqinCount());// 缺件率
			} catch (Exception e) {
				zh1.setQuejianrate(0f);// 缺件率
			}
			totalDao.save(zh1);
		}
		/* 月合格率 */
		String hql_mouth = "from MouthHege where mouth =? and gysid = ?";
		String mouth1 = nowDate.substring(0, 7);// 获取年月份
		MouthHege mH = (MouthHege) totalDao.getObjectByCondition(hql_mouth,
				mouth1, wddOld.getGysId());
		if (mH != null) {
			mH.setShijiyunsongCount(mH.getShijiyunsongCount()
					+ wddOld.getQrNumber());// 实际送货数量=总送货数量
			mH.setShenqinCount(mH.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
			try {
				mH.setQuejianrate(mH.getShijiyunsongCount()
						/ mH.getShenqinCount());// 缺件率
			} catch (Exception e) {
				mH.setQuejianrate(0f);// 缺件率
			}
			if (zhunshi) {// 判断是否准时
				mH.setZhunshijiaofuCount(mH.getZhunshijiaofuCount() + 1);
				mH.setJiaofuCount(mH.getJiaofuCount() + 1);
				mH.setJiaofuhege(mH.getZhunshijiaofuCount()
						/ mH.getJiaofuCount());
			} else {
				mH.setZhunshijiaofuCount(mH.getZhunshijiaofuCount());
				mH.setJiaofuCount(mH.getJiaofuCount() + 1);
			}
			mH.setJiaofuhege(mH.getZhunshijiaofuCount() / mH.getJiaofuCount());
			totalDao.update(mH);
		} else {
			MouthHege mH1 = new MouthHege();
			mH1.setGysid(wddOld.getGysId());// 供应商名字
			mH1.setGysname(wddOld.getGysName());// 供应商名字
			mH1.setMouth(mouth1);
			// 总合格率数据
			mH1.setBhgCount(0F);// 不合格数量
			mH1.setShCount(0F);// 总送货数量
			mH1.setQualifiedrate(0F);// 合格率
			// 不良品
			mH1.setCheckNopass(0F);// 检验不合格
			mH1.setNoProduce(0F);
			// 批次合格率数据
			mH1.setPiciCount(0F);// 总批次
			mH1.setPicihgCount(0F);// 合格批次
			mH1.setPiciQualifiedrate(0F);// 批次率
			// 交付及时率数据
			if (zhunshi) {// 判断是否准时
				mH1.setJiaofuCount(1F);// 交付总次数
				mH1.setZhunshijiaofuCount(1F);// 准时交付次数
			} else {
				mH1.setJiaofuCount(1F);// 交付总次数
				mH1.setZhunshijiaofuCount(0F);// 准时交付次数
			}
			mH1.setJiaofuhege(mH1.getZhunshijiaofuCount()
					/ mH1.getJiaofuCount());
			// 缺件率数据
			mH1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
			mH1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
			try {
				mH1.setQuejianrate(mH1.getShijiyunsongCount()
						/ mH1.getShenqinCount());// 缺件率
			} catch (Exception e) {
				mH1.setQuejianrate(0f);// 缺件率
			}
			totalDao.save(mH1);
		}
		/* 季度汇总 */
		String quarter = "";// 计算季度
		String mouthStr = nowDate.substring(5, 7);
		if (Integer.parseInt(mouthStr) >= 1 && Integer.parseInt(mouthStr) <= 3) {
			quarter = year + "年1季";
		} else if (Integer.parseInt(mouthStr) >= 4
				&& Integer.parseInt(mouthStr) <= 6) {
			quarter = year + "年2季";
		} else if (Integer.parseInt(mouthStr) >= 7
				&& Integer.parseInt(mouthStr) <= 9) {
			quarter = year + "年3季";
		} else if (Integer.parseInt(mouthStr) >= 10
				&& Integer.parseInt(mouthStr) <= 12) {
			quarter = year + "年4季";
		}
		String hql_quarter = "from QuarterHege where quarter =? and gysid = ?";
		QuarterHege qh = (QuarterHege) totalDao.getObjectByCondition(
				hql_quarter, quarter, wddOld.getGysId());
		if (qh != null) {
			qh.setShijiyunsongCount(qh.getShijiyunsongCount()
					+ wddOld.getQrNumber());// 实际送货数量=总送货数量
			qh.setShenqinCount(qh.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
			try {
				qh.setQuejianrate(qh.getShijiyunsongCount()
						/ qh.getShenqinCount());// 缺件率
			} catch (Exception e) {
				qh.setQuejianrate(0f);// 缺件率
			}
			if (zhunshi) {// 判断是否准时
				qh.setZhunshijiaofuCount(qh.getZhunshijiaofuCount() + 1);
				qh.setJiaofuCount(qh.getJiaofuCount() + 1);
				qh.setJiaofuhege(qh.getZhunshijiaofuCount()
						/ qh.getJiaofuCount());
			} else {
				qh.setZhunshijiaofuCount(qh.getZhunshijiaofuCount());
				qh.setJiaofuCount(qh.getJiaofuCount() + 1);
			}
			qh.setJiaofuhege(qh.getZhunshijiaofuCount() / qh.getJiaofuCount());
			totalDao.update(qh);
		} else {
			QuarterHege qh1 = new QuarterHege();
			qh1.setGysid(wddOld.getGysId());// 供应商名字
			qh1.setGysname(wddOld.getGysName());// 供应商名字
			qh1.setQuarter(quarter);
			// 总合格率数据
			qh1.setBhgCount(0F);// 不合格数量
			qh1.setShCount(0F);// 总送货数量
			qh1.setQualifiedrate(0F);// 合格率
			// 不良品
			qh1.setCheckNopass(0F);// 检验不合格
			qh1.setNoProduce(0F);
			// 批次合格率数据
			qh1.setPiciCount(0F);// 总批次
			qh1.setPicihgCount(0F);// 合格批次
			qh1.setPiciQualifiedrate(0F);// 批次率
			// 交付及时率数据
			if (zhunshi) {// 判断是否准时
				qh1.setJiaofuCount(1F);// 交付总次数
				qh1.setZhunshijiaofuCount(1F);// 准时交付次数
			} else {
				qh1.setJiaofuCount(1F);// 交付总次数
				qh1.setZhunshijiaofuCount(0F);// 准时交付次数
			}
			qh1.setJiaofuhege(qh1.getZhunshijiaofuCount()
					/ qh1.getJiaofuCount());
			// 缺件率数据
			qh1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
			qh1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
			try {
				qh1.setQuejianrate(qh.getShijiyunsongCount()
						/ qh.getShenqinCount());// 缺件率
			} catch (Exception e) {
				qh1.setQuejianrate(0f);// 缺件率
			}
			totalDao.save(qh1);
		}
		/* 年度汇总 */
		String hql_year = "from YearHege where year =? and gysid = ?";
		YearHege yh = (YearHege) totalDao.getObjectByCondition(hql_year, year,
				wddOld.getGysId());
		if (yh != null) {
			yh.setShijiyunsongCount(yh.getShijiyunsongCount()
					+ wddOld.getQrNumber());// 实际送货数量=总送货数量
			yh.setShenqinCount(yh.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
			try {
				yh.setQuejianrate(yh.getShijiyunsongCount()
						/ yh.getShenqinCount());// 缺件率
			} catch (Exception e) {
				yh.setQuejianrate(0f);// 缺件率
			}
			if (zhunshi) {// 判断是否准时
				yh.setZhunshijiaofuCount(yh.getZhunshijiaofuCount() + 1);
				yh.setJiaofuCount(yh.getJiaofuCount() + 1);
				yh.setJiaofuhege(yh.getZhunshijiaofuCount()
						/ yh.getJiaofuCount());
			} else {
				yh.setZhunshijiaofuCount(yh.getZhunshijiaofuCount());
				yh.setJiaofuCount(yh.getJiaofuCount() + 1);
			}
			yh.setJiaofuhege(yh.getZhunshijiaofuCount() / yh.getJiaofuCount());
			totalDao.update(yh);
		} else {
			YearHege yh1 = new YearHege();
			yh1.setGysid(wddOld.getGysId());// 供应商名字
			yh1.setGysname(wddOld.getGysName());// 供应商名字
			yh1.setYear(year);
			// 总合格率数据
			yh1.setBhgCount(0F);// 不合格数量
			yh1.setShCount(0F);// 总送货数量
			yh1.setQualifiedrate(0F);// 合格率
			// 不良品
			yh1.setCheckNopass(0F);// 检验不合格
			yh1.setNoProduce(0F);
			// 批次合格率数据
			yh1.setPiciCount(0F);// 总批次
			yh1.setPicihgCount(0F);// 合格批次
			yh1.setPiciQualifiedrate(0F);// 批次率
			// 交付及时率数据
			if (zhunshi) {// 判断是否准时
				yh1.setJiaofuCount(1F);// 交付总次数
				yh1.setZhunshijiaofuCount(1F);// 准时交付次数
			} else {
				yh1.setJiaofuCount(1F);// 交付总次数
				yh1.setZhunshijiaofuCount(1F);// 准时交付次数
			}
			yh1.setJiaofuhege(yh1.getZhunshijiaofuCount()
					/ yh1.getJiaofuCount());
			// 缺件率数据
			yh1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
			yh1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
			try {
				yh1.setQuejianrate(yh.getShijiyunsongCount()
						/ yh.getShenqinCount());// 缺件率
			} catch (Exception e) {
				yh1.setQuejianrate(0f);// 缺件率
			}
			totalDao.save(yh1);
		}
	}

	/**
	 * 更新供应商合格率
	 * 
	 * @param wddOld
	 * @param zhUser
	 * @param zhunshi
	 * @param qr
	 */
	private void updateQfr(WaigouDeliveryDetail wddOld, ZhUser zhUser,
			boolean zhunshi, QualifiedRate qr) {
		// 是否存在记录
		qr.setShijiyunsongCount(qr.getShijiyunsongCount()
				+ wddOld.getQrNumber());// 实际送货数量=总送货数量
		qr.setShenqinCount(qr.getShenqinCount() + wddOld.getShNumber());// 申请数量（要货数量）
		try {
			qr.setQuejianrate(qr.getShijiyunsongCount() / qr.getShenqinCount());// 缺件率
		} catch (Exception e) {
			qr.setQuejianrate(0f);// 缺件率
		}
		if (zhunshi) {
			qr.setZhunshijiaofuCount(qr.getZhunshijiaofuCount() + 1);
			qr.setJiaofuCount(qr.getJiaofuCount() + 1);
			try {
				qr.setJiaofuhege(qr.getZhunshijiaofuCount()
						/ qr.getJiaofuCount());
			} catch (Exception e) {
				qr.setJiaofuhege(0f);// 缺件率
			}
		} else {
			qr.setZhunshijiaofuCount(qr.getZhunshijiaofuCount());
			qr.setJiaofuCount(qr.getJiaofuCount() + 1);
		}
		try {
			qr.setJiaofuhege(qr.getZhunshijiaofuCount() / qr.getJiaofuCount());
		} catch (Exception e) {
			qr.setJiaofuhege(0f);// 缺件率
		}
		zhUser.setGonghuolv(qr.getJiaofuhege());
		zhUser.setQuejianlv(qr.getQuejianrate());
		totalDao.update(qr);
	}

	/**
	 * 添加供应商合格信息
	 * 
	 * @param wddOld
	 * @param zhUser
	 */
	private void addQfr(WaigouDeliveryDetail wddOld, ZhUser zhUser) {
		QualifiedRate qualifiedRate1 = new QualifiedRate();
		qualifiedRate1.setGysname(wddOld.getGysName());// 供应商名字
		qualifiedRate1.setGysid(wddOld.getGysId());// 供应商名字
		// 总合格率数据
		qualifiedRate1.setBhgCount(0F);// 不合格数量
		qualifiedRate1.setShCount(0F);// 总送货数量
		qualifiedRate1.setQualifiedrate(0F);// 合格率
		// 不良品
		qualifiedRate1.setCheckNopass(0F);// 检验不合格
		qualifiedRate1.setNoProduce(0F);
		// 批次合格率数据
		qualifiedRate1.setPiciCount(0F);// 总批次
		qualifiedRate1.setPicihgCount(0F);// 合格批次
		qualifiedRate1.setPiciQualifiedrate(0F);// 批次率
		// 交付及时率数据
		qualifiedRate1.setJiaofuCount(1F);// 交付总次数
		qualifiedRate1.setZhunshijiaofuCount(1F);// 准时交付次数
		try {
			qualifiedRate1.setJiaofuhege(qualifiedRate1.getZhunshijiaofuCount()
					/ qualifiedRate1.getJiaofuCount());
		} catch (Exception e) {
			qualifiedRate1.setJiaofuhege(0f);// 缺件率
		}
		// 缺件率数据
		qualifiedRate1.setShijiyunsongCount(wddOld.getQrNumber());// 实际送货数量=总送货数量
		qualifiedRate1.setShenqinCount(wddOld.getShNumber());// 申请数量（要货数量）
		try {
			qualifiedRate1.setQuejianrate(qualifiedRate1.getShijiyunsongCount()
					/ qualifiedRate1.getShenqinCount());// 缺件率
		} catch (Exception e) {
			qualifiedRate1.setQuejianrate(0f);// 缺件率
		}
		zhUser.setGonghuolv(qualifiedRate1.getJiaofuhege());
		zhUser.setQuejianlv(qualifiedRate1.getQuejianrate());
		totalDao.save(qualifiedRate1);
	}

	private void updateGoodsStore(WaigouDeliveryDetail wddOld, String hql,
			WaigouDelivery waigouDelivery, GoodsStore goodsStore) {
		// 更新库存 检验批次
		String hql_goods = "from Goods where goodsMarkId = ? and goodsClass = ? and goodsCurQuantity >0";
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
		Goods s = (Goods) totalDao.getObjectByCondition(hql_goods,
				new Object[] { goodsStore.getGoodsStoreMarkId(), "待检库" });
		if (s != null) {
			s.setGoodsLotId(wddOld.getExamineLot());
			// 更新库存批次
			totalDao.update(s);
		}
		goodsStore.setGoodsStoreLot(wddOld.getExamineLot());// 批次.setGoodsStoreLot(wddOld.getExamineLot());//
		// 更新入库记录 批次
		totalDao.update(goodsStore);

		WaigouDailySheet wds = (WaigouDailySheet) totalDao
				.getObjectByCondition(
						" from WaigouDailySheet where markId = ? and shOrderNum = ?",
						wddOld.getMarkId(), waigouDelivery.getPlanNumber());
		if (wds != null) {
			wds.setExamineLot(wddOld.getExamineLot());// 检验批次
			totalDao.update(wds);
		}
	}

	/**
	 * 生成待检库入库历史记录
	 * 
	 * @param waigouDeliveryDetail
	 * @param wddOld
	 * @param waigouDelivery
	 */
	private void addGsdaijian(WaigouDeliveryDetail waigouDeliveryDetail,
			WaigouDeliveryDetail wddOld, WaigouDelivery waigouDelivery) {
		GoodsStore goodsStore;
		goodsStore = new GoodsStore();
		goodsStore.setGoodsStoreMarkId(wddOld.getMarkId());// 件号
		goodsStore.setGoodsStoreWarehouse("待检库");// 库别
		goodsStore.setGoodHouseName("待检仓");// 仓区
		goodsStore.setGoodsStorePosition(wddOld.getKuwei());// 库位
		goodsStore.setBanBenNumber(wddOld.getBanben());// 版本
		goodsStore.setKgliao(wddOld.getKgliao());// 供料属性
		goodsStore.setProNumber(wddOld.getProNumber());// 项目编号
		goodsStore.setGoodsStoreLot(wddOld.getExamineLot());// 批次
		goodsStore.setHsPrice(wddOld.getHsPrice()==null?null:wddOld.getHsPrice().doubleValue());// 含税单价
		goodsStore.setGoodsStoreGoodsName(wddOld.getProName());// 名称
		goodsStore.setGoodsStoreFormat(wddOld.getSpecification());// 规格
		goodsStore.setWgType(wddOld.getWgType());// 物料类别
		goodsStore.setGoodsStoreUnit(wddOld.getUnit());// 单位
		goodsStore.setGoodsStoreCount(wddOld.getQrNumber());// 数量
		goodsStore.setGoodsStorePrice(wddOld.getPrice()==null?null:wddOld.getPrice().doubleValue());// 不含税单价
		goodsStore.setTaxprice(wddOld.getTaxprice());// 税率
		// 总额
		goodsStore.setPriceId(wddOld.getPriceId());// 价格Id
		goodsStore.setGoodsStoreSupplier(wddOld.getGysName());// 供应商名称
		goodsStore.setGysId(wddOld.getGysId() + "");// 供应商Id
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		goodsStore.setGoodsStoreTime(Util.getDateTime());
		goodsStore.setWwddId(wddOld.getId());// 送货单明细Id
		goodsStore.setGoodsStoreSendId(waigouDelivery.getPlanNumber());// 送货单号
		goodsStore.setTuhao(wddOld.getTuhao());// 图号
		goodsStore.setStyle("待检入库");// 入库类型
		goodsStore.setInputSource("待检验");//
		goodsStore.setPrintStatus("YES");
		goodsStore.setGoodsStoreZhishu(waigouDeliveryDetail.getZhuanhuanNum());
		goodsStoreServer.saveSgrk(goodsStore);
	}

	/**
	 * 生成来料日报
	 * 
	 * @param wddOld
	 * @param ycl
	 * @param waigouDelivery
	 */
	private void comeRibao(WaigouDeliveryDetail wddOld, YuanclAndWaigj ycl,
			WaigouDelivery waigouDelivery) {
		WaigouDailySheet wds = new WaigouDailySheet();
		wds.setMarkId(wddOld.getMarkId());// 件号
		wds.setProName(wddOld.getProName());// 产品名称
		wds.setGysName(wddOld.getGysName());// 供应商名称
		ZhUser zhusers = (ZhUser) totalDao.get(ZhUser.class, wddOld.getGysId());
		if (zhusers != null) {
			wds.setGysjc(zhusers.getName()); // 供应商简称
		}
		wds.setGysId(wddOld.getGysId()); // 供应商Id
		wds.setTuhao(wddOld.getTuhao());// 图号;
		wds.setLlNumber(wddOld.getQrNumber());// 来料数量
		wds.setExamineLot(wddOld.getExamineLot());// 检验批次
		wds.setCgOrderNum(wddOld.getCgOrderNum());// 采购订单号
		wds.setWgType(wddOld.getWgType());// 物料类别
		wds.setBanbenNumber(wddOld.getBanben());// 版本
		wds.setKgliao(wddOld.getKgliao());// 供料属性
		wds.setShOrderNum(waigouDelivery.getPlanNumber());// 送货单号
		wds.setSpecification(wddOld.getSpecification());// 规格
		wds.setStatus("待初检");// 状态
		String caizi = "";
		if (ycl != null) {
			caizi = ycl.getCaizhi();
		} else {
			caizi = wddOld.getWgType();
		}
		wds.setCaizi(caizi);
		wds.setWgddId(wddOld.getId());
		wds.setYear(Util.getDateTime("yyyy") + "年");
		wds.setMonth(Util.getDateTime("yyyy-MM") + "月");
		wds.setLiaodate(Util.getDateTime("yyyy-MM-dd"));
		wds.setWeek(Util.getWeek(null, null));
		wds.setAddTime(Util.getDateTime());
		wds.setAddUser(Util.getLoginUser().getName());
		totalDao.save(wds);
	}

	public Map<Integer, Object> QueryHege(String type, int pageNo, int pageSize) {
		String hql = "from " + type + " where 1= 1";
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	/****
	 * 查询所有待检验的送货单
	 * 
	 * @return
	 */
	@Override
	public Object[] findDjyDelivery(WaigouDeliveryDetail waigoudd,
			String pageStatus, int parseInt, int pageSize) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		if (waigoudd == null) {
			waigoudd = new WaigouDeliveryDetail();
		}
		String hql = "SELECT c.name from Category c join c.userSet u where u.id="
				+ user.getId();
		List<String> strList = totalDao.query(hql);
		String categoryName = "";
		if (strList == null || strList.size() == 0) {
			categoryName = " and 1=1";
		} else {
			categoryName = " and wgType in(";
			int i = 0;
			for (String str : strList) {
				if (i == 0) {
					categoryName += "'" + str + "'";
				} else {
					categoryName += ",'" + str + "'";
				}
				i++;
			}
			categoryName += ")";
		}
		if (categoryName.length() > 0 && categoryName != null) {
			categoryName = categoryName.substring(1);
		}
		String type = "'外购','研发'";
		if ("ww".equals(pageStatus)) {
			type = "'外委'";
			categoryName = "";
		} else if ("muju".equals(pageStatus)) {
			type = "'模具'";
			categoryName = "";
		} else if ("fl".equals(pageStatus)) {
			type = "'辅料'";
			categoryName = "";
		}
		// else if ("yf".equals(pageStatus)) {
		// type = "'研发'";
		// categoryName = "";
		// }
		String hql1 = "status in ('待检验','检验中') and type in(" + type + ") "
				+ categoryName;
		String strCache = waigoudd.getStrs();
		waigoudd.setStrs(null);
		// 产品编码 --外委
		if (pageStatus != null && pageStatus.equals("ww") && strCache != null
				&& !strCache.equals("")) {
			hql1 += " and waigouPlanId in (select wgOrderId from  ProcardWGCenter where procardId in(select id from Procard where ywMarkId = '"
					+ strCache + "'))";
		}
		String hql2 = totalDao.criteriaQueries(waigoudd, hql1,
				"nextProcessName")
				+ " order by querenTime asc,gysId asc";
		if (strCache != null && strCache.length() > 0) {
			waigoudd.setStrs(strCache);
		}
		boolean nexprocess = false;
		String nexprocesshql = "";
		if (waigoudd.getNextProcessName() != null
				&& waigoudd.getNextProcessName().length() > 0) {
			nexprocess = true;
			nexprocesshql = " and processName = '"
					+ waigoudd.getNextProcessName() + "'";
		}
		List<WaigouDeliveryDetail> list2 = null;
		if (nexprocess) {
			list2 = totalDao.query(hql2);
		} else {
			list2 = totalDao.findAllByPage(hql2, parseInt, pageSize);
		}
		if (list2 != null && list2.size() > 0) {
			for (int i = (list2.size() - 1); i >= 0; i--) {
				WaigouDeliveryDetail w = list2.get(i);
				if (w.getType() != null && "外委".equals(w.getType())) {
					Integer pno = Util.getSplitNumber(w.getProcessNo(), ";",
							"max");
					String nextProcessName = (String) totalDao
							.getObjectByCondition(
									"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') "
											+ " and procard.id in(select procardId from ProcardWGCenter where wgOrderId=? ) and processNO >? "
											+ nexprocesshql
											+ " order by processNO", w
											.getWaigouPlanId(), pno);
					if (nexprocess && nextProcessName == null) {
						list2.remove(i);
						continue;
					}
					if (nextProcessName == null) {
						nextProcessName = "无";
					}
					w.setNextProcessName(nextProcessName);

					List<String> ywmarkIdList = totalDao
							.query(
									"select distinct(ywMarkId) from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) ",
									w.getWaigouPlanId());
					if (ywmarkIdList != null && ywmarkIdList.size() > 0) {
						String str = "";
						for (String ywmarkId : ywmarkIdList) {
							str += ywmarkId + "<br/>";
						}
						w.setStrs(str);
					}
				}
			}
		}
		int count = totalDao.getCount(hql2);
		Object[] o = { list2, count };
		return o;
	}

	/**
	 * 修改采购订单信息
	 * 
	 * @param waigouOrder
	 * @param tag
	 * @return
	 */
	@Override
	public String updateOrderMsg(WaigouOrder waigouOrder, String tag) {
		// TODO Auto-generated method stub
		WaigouOrder old = (WaigouOrder) totalDao.getObjectById(
				WaigouOrder.class, waigouOrder.getId());
		if (old != null) {
			if (tag != null && tag.equals("payType")) {
				old.setPayType(waigouOrder.getPayType());
				// old.setApplystatus("待确认");
				return totalDao.update(old) + "";
			}
		}
		return "没有找到目标采购订单!";
	}

	/**
	 * 外委查询列表
	 * 
	 * @param waigouPlan
	 * @param parseInt
	 * @param pageSize
	 * @param pageStatus
	 * @param id
	 * @return
	 */
	@Override
	public Object[] findWwPlanList(WaigouPlan waigouPlan, int pageNo,
			int pageSize, String pageStatus, String startDate, String endDate,
			Integer wgOrderId, String sunxu) {
		// TODO Auto-generated method stub
		if (waigouPlan == null) {
			waigouPlan = new WaigouPlan();
		}
		String rootMarkId = waigouPlan.getRootMarkId();
		waigouPlan.setRootMarkId(null);
		String planNumber = waigouPlan.getPlanNumber();
		waigouPlan.setPlanNumber(null);
		String rootSlfCard = waigouPlan.getRootSlfCard();
		waigouPlan.setRootSlfCard(null);
		// if ("yclPlan".equals(pageStatus)) {
		// pageStatus = "原材料";
		// } else {
		// pageStatus = "外购";
		// }
		//		
		String other = "";

		// waigouPlan.setType(pageStatus);
		// String jihuos = " and status<>'待激活'";
		if (wgOrderId != null && wgOrderId > 0) {
			waigouPlan = new WaigouPlan();
		}
		String hql = totalDao.criteriaQueries(waigouPlan, null, "addTime",
				"acArrivalTime", "waigouOrder", "isshowBl")
				+ other;
		// 存在采购订单时，显示订单明细
		WaigouOrder waigouOrder = null;
		if (wgOrderId != null && wgOrderId > 0) {
			waigouOrder = (WaigouOrder) totalDao.getObjectById(
					WaigouOrder.class, wgOrderId);
			waigouPlan = new WaigouPlan();
			hql += " and waigouOrder.id=" + wgOrderId;
		} else {
			if (startDate != null && startDate.length() > 0) {
				// startDate +=" 00:00:00";
				hql += " and addTime>='" + startDate + "'";
			}
			if (endDate != null && endDate.length() > 0) {
				// startDate +=" 24:00:00";
				hql += " and addTime<='" + startDate + "'";
			}
		}
		if (planNumber != null && planNumber.length() > 0
				&& !planNumber.contains("delete")
				&& !planNumber.contains("select")
				&& !planNumber.contains("update")) {
			hql += " and waigouOrder.planNumber like '%" + planNumber + "%'";
		}
		String status = "";
		if ("dsq".equals(pageStatus)) {
			status = " and (status in ('待核对','待审批','审批中','待通知') or (status ='设变锁定' and oldStatus in ('待核对','待审批','审批中','待通知'))) and waigouOrder.applystatus in('未申请','打回') and waigouOrder.addUserCode='"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("dtz".equals(pageStatus)) {
			status = " and (status='待通知' or (status ='设变锁定' and oldStatus in ('待通知'))) and waigouOrder.addUserCode='"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("dqr".equals(pageStatus)) {
			status = " and (status in ('待确认','协商确认') or (status ='设变锁定' and oldStatus in ('待确认','协商确认'))) and waigouOrder.addUserCode='"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("findAllself".equals(pageStatus)) {
			status = " and waigouOrder.addUserCode = '"
					+ Util.getLoginUser().getCode() + "'";
		} else if ("findAll".equals(pageStatus)
				|| "findAlljg".equals(pageStatus)) {
		} else if ("gysnew".equals(pageStatus)) {
			status = " and userId="
					+ Util.getLoginUser().getId()
					+ " and ( status in ('待确认','协商确认') or (status ='设变锁定' and oldStatus in ('待确认','协商确认'))) ";
		} else if ("gysall".equals(pageStatus)) {
			status = " and userId="
					+ Util.getLoginUser().getId()
					+ " and (status not in ('待核对','待审批','审批中','待通知') or (status ='设变锁定' and oldStatus not in ('待核对','待审批','审批中','待通知')))";
		} else if ("sbdqr".equals(pageStatus)) {
			status = " and waigouOrder.addUserCode='"
					+ Util.getLoginUser().getCode()
					+ "' and (status in('协商取消','取消') or (status ='设变锁定' and oldStatus in ('协商取消','取消')))";
		} else if ("gyssbdqr".equals(pageStatus)) {
			status = " and (userId="
					+ Util.getLoginUser().getId()
					+ "  or waigouOrder.addUserCode='"
					+ Util.getLoginUser().getCode()
					+ "') and ( status in('协商取消','取消') or (status ='设变锁定' and oldStatus in ('协商取消','取消')))";
		} else {
			return new Object[] { null, 0, null };
		}
		hql += status + " and wwType  in ('工序外委','包工包料') ";

		if (rootMarkId != null && rootMarkId.length() > 0
				&& !rootMarkId.contains("delete")
				&& !rootMarkId.contains("select")
				&& !rootMarkId.contains("update") && rootSlfCard != null
				&& rootSlfCard.length() > 0 && !rootSlfCard.contains("delete")
				&& !rootSlfCard.contains("select")
				&& !rootSlfCard.contains("update")) {
			hql += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ rootMarkId + "%' or rootMarkId like '%" + rootMarkId
					+ "%')" + " and rootSelfCard like '%" + rootSlfCard
					+ "%'))";
		}
		if (rootMarkId != null && rootMarkId.length() > 0
				&& !rootMarkId.contains("delete")
				&& !rootMarkId.contains("select")
				&& !rootMarkId.contains("update")
				&& (rootSlfCard == null || rootSlfCard.length() == 0)) {
			hql += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ rootMarkId + "%' or rootMarkId like '%" + rootMarkId
					+ "%')))";
		}
		if ((rootMarkId == null || rootMarkId.length() == 0)
				&& rootSlfCard != null && rootSlfCard.length() > 0
				&& !rootSlfCard.contains("delete")
				&& !rootSlfCard.contains("select")
				&& !rootSlfCard.contains("update")) {
			hql += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where rootSelfCard like '%"
					+ rootSlfCard + "%'))";
		}
		if (waigouPlan.getAddTime() != null
				&& waigouPlan.getAddTime().length() > 0) {
			String[] strarray = waigouPlan.getAddTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].trim().length() > 0) {
					hql += " and addTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and addTime < '" + strarray[1].trim() + "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAddTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAddTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if (waigouPlan.getAcArrivalTime() != null
				&& waigouPlan.getAcArrivalTime().length() > 0) {
			String[] strarray = waigouPlan.getAcArrivalTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].length() > 0
						&& strarray[0].trim().length() > 0) {
					hql += " and acArrivalTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and acArrivalTime < '" + strarray[1].trim()
								+ "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAcArrivalTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAcArrivalTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}

		List<WaigouPlan> list = null;
		int count = 0;

		if (sunxu != null && sunxu.equals("dao")) {
			hql += "order by id desc";
		}
		if (wgOrderId != null && wgOrderId > 0) {
			list = totalDao.query(hql);
			count = list.size();
		} else {
			list = totalDao.findAllByPage(hql, pageNo, pageSize);
			count = totalDao.getCount(hql);
		}
		waigouPlan.setRootMarkId(rootMarkId);
		waigouPlan.setPlanNumber(planNumber);
		waigouPlan.setRootSlfCard(rootSlfCard);
		if (list != null && list.size() > 0) {
			for (WaigouPlan wp : list) {
				Float rukuNum = 0f;// 入库数量
				List<WaigouDeliveryDetail> wdList = totalDao
						.query(
								"from WaigouDeliveryDetail where waigouPlanId=? and status not in ('退货','退回')",
								wp.getId());
				if (wdList != null && wdList.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (WaigouDeliveryDetail wd : wdList) {
						if (wd.getQuerenTime() != null
								&& wd.getQuerenTime().length() > 0) {
							if (sb.length() == 0) {
								sb.append(wd.getQuerenTime() + ":"
										+ wd.getShNumber());
							} else {
								sb.append("；" + wd.getQuerenTime() + ":"
										+ wd.getShNumber());
							}
						}
						if (wd.getYrukuNum() != null) {
							rukuNum += wd.getYrukuNum();
						}
					}
					wp.setAcArrivalTime(sb.toString());
				}
				if (rukuNum < wp.getNumber() || "生产中".equals(wp.getStatus())) {
					wp.setIsshowBl(true);
				} else {
					wp.setIsshowBl(false);
				}
				wp.setRukuNum(rukuNum);
				List<Object[]> objsList = totalDao
						.query(
								"select ywMarkId,rootMarkId,rootSelfCard,banBenNumber from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?) ",
								wp.getId());
				List<String> hadBanben = new ArrayList<String>();
				if (objsList != null && objsList.size() > 0) {
					for (Object[] objs : objsList) {
						if (objs[0] != null) {
							if (wp.getYwmarkId() == null) {
								wp.setYwmarkId(objs[0].toString());
							} else if (!wp.getYwmarkId().contains(
									objs[0].toString())) {
								wp.setYwmarkId(wp.getYwmarkId() + "、"
										+ objs[0].toString());
							}
						}
						if (objs[1] != null) {
							if (wp.getRootMarkId() == null) {
								wp.setRootMarkId(objs[1].toString());
							} else if (!wp.getRootMarkId().contains(
									objs[1].toString())) {
								wp.setRootMarkId(wp.getRootMarkId() + "、"
										+ objs[1].toString());
							}
						}
						if (objs[2] != null) {
							if (wp.getRootSlfCard() == null) {
								wp.setRootSlfCard(objs[2].toString());
							} else if (!wp.getRootSlfCard().contains(
									objs[2].toString())) {
								wp.setRootSlfCard(wp.getRootSlfCard() + "、"
										+ objs[2].toString());
							}
						}
						if (objs[3] != null) {
							if (!hadBanben.contains(objs[3].toString())) {
								hadBanben.add(objs[3].toString());
							}
						}
					}
				}
				if (hadBanben.size() > 0) {
					for (String banben : hadBanben) {
						if (wp.getProcardbanben() == null) {
							wp.setProcardbanben(banben);
						} else {
							wp.setProcardbanben(wp.getProcardbanben() + ","
									+ banben);
						}
						if (!Util.isEquals(wp.getBanben(), banben)) {
							;
							wp.setCybanben("yes");
						}
					}
				} else {
					if (wp.getBanben() != null && wp.getBanben().length() > 0) {
						wp.setCybanben("yes");
					}
				}
				Procard procard = (Procard) totalDao
						.getObjectByCondition(
								" from Procard where id in (select procardId  from  ProcardWGCenter where wgOrderId =? )",
								wp.getId());
				if (procard != null && procard.getSbId() != null) {
					wp.setSbNumber(procard.getSbNumber());
					wp.setSbId(procard.getSbId());
					ProcardTemplateBanBen ptbb = (ProcardTemplateBanBen) totalDao
							.getObjectByCondition(
									"from ProcardTemplateBanBen where procardTemplateBanBenApply.id=? and markId=?",
									procard.getSbId(), procard.getMarkId());
					if (ptbb != null) {
						wp.setSbmsg(ptbb.getRemark());
					}
				} else {
					wp.setSbNumber("无设变");
				}
				// 设变零件
				// 相关变更零件
				List<String> bgmarkIdList = totalDao
						.query(
								"select DISTINCT sbmarkId from WaigouPlanLock where entityName='WaigouPlan' and entityId=? and zxStatus in('执行中','已执行') and dataStatus<>'取消'",
								wp.getId());
				StringBuffer bgmarkIds = new StringBuffer();// 关联件号
				// 拼接
				for (String bgmarkId : bgmarkIdList) {
					if (bgmarkIds.length() == 0) {
						bgmarkIds.append(bgmarkId);
					} else {
						bgmarkIds.append("；" + bgmarkId);
					}
				}
				//wp.setBgMarkIds(bgmarkIds.toString());
			}
		}

		Object[] o = { list, count, waigouOrder };
		return o;
	}

	/**
	 * 通过Id 获取送货订单明细
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public WaigouDeliveryDetail getWaiGouddById(Integer id) {
		// TODO Auto-generated method stub
		return (WaigouDeliveryDetail) totalDao.getObjectById(
				WaigouDeliveryDetail.class, id);
	}

	/**
	 * 通过Id 获取送货订单明细和下工序
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public WaigouDeliveryDetail getWaiGouddById2(Integer id) {
		// TODO Auto-generated method stub
		WaigouDeliveryDetail wwd = (WaigouDeliveryDetail) totalDao
				.getObjectById(WaigouDeliveryDetail.class, id);
		if (wwd != null && wwd.getType() != null && wwd.getType().equals("外委")) {
			Integer pno = Util.getSplitNumber(wwd.getProcessNo(), ";", "max");
			String nextProcessName = (String) totalDao
					.getObjectByCondition(
							"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') "
									+ " and procard.id in(select procardId from ProcardWGCenter where wgOrderId=? ) and processNO >? order by processNO",
							wwd.getWaigouPlanId(), pno);
			if (nextProcessName == null) {
				nextProcessName = "无";
			}
			Procard procard = (Procard) totalDao.getObjectByCondition(
					" from Procard where id in (select procardId  from ProcardWGCenter where "
							+ " wgOrderId =? )", wwd.getWaigouPlanId());
			if (procard != null && procard.getSbId() != null) {
				wwd.setSbNumber(procard.getSbNumber());
				wwd.setSbId(procard.getSbId());
				ProcardTemplateBanBen ptbb = (ProcardTemplateBanBen) totalDao
						.getObjectByCondition(
								"from ProcardTemplateBanBen where procardTemplateBanBenApply.id=? and markId=?",
								procard.getSbId(), procard.getMarkId());
				if (ptbb != null) {
					wwd.setSbRemark(ptbb.getRemark());
				}
			}
			int count =	totalDao.getCount("from GysMarkIdCkolse where gysId =? and  markId =? and isjytx = 0 ",wwd.getGysId(),wwd.getMarkId() );
			if(count>0){
				wwd.setJytixing("是");
			}
			wwd.setNextProcessName(nextProcessName);
		}
		return wwd;
	}

	/**
	 * 查询待检外购外委检验模版
	 * 
	 * @param markId
	 * @param banben
	 * @param type
	 * @return
	 */
	@Override
	public Object[] getOsTemplate(String markId, String banben, String type,
			String gongxuNum, WaigouDeliveryDetail waigoudd, Integer ostId) {
		OsTemplate ot = null;
		String status = "";
		List<OsTemplate> otList = null;
		if (ostId == null || ostId == 0) {
			String hql = "from OsTemplate where partNumber= ? ";
			if ("外购件".equals(type) || "外购".equals(type) || "研发".equals(type)) {
				hql += " and zhonglei='外购件检验' ";
			} else if ("包工包料".equals(type) || "工序外委".equals(type)) {
				// if (gongxuNum.indexOf(";") > 0) {
				// gongxuNum = gongxuNum.substring(gongxuNum.lastIndexOf(";") +
				// 1,
				// gongxuNum.length());
				// }
				hql += " and zhonglei in('外委','外委检验','巡检')";
				// hql += " and gongxuNum='" + gongxuNum + "'";
				if (gongxuNum != null && !"".equals(gongxuNum)) {
					hql += " and gongxuNum = '"
							+ Util.getSplitNumber(gongxuNum, ";", "max") + "' ";
				}
			} else if ("辅料".equals(type)) {
				hql += " and zhonglei in('辅料')";
			} else {
				// 类型为空
				return new Object[] { ot, null, otList, "类型为空", null };
			}
			if (banben == null || banben.length() == 0) {
				String hql2 = " and (banbenNumber is null or banbenNumber='')";
				ot = (OsTemplate) totalDao.getObjectByCondition(hql + hql2,
						markId);
			} else {
				String hql2 = " and (banbenNumber is not null and banbenNumber=?)";
				ot = (OsTemplate) totalDao.getObjectByCondition(hql + hql2,
						markId, banben);
			}
			if (ot == null) {
				// 读取通用模版
				String hql_ty = "from OsTemplate where ispublic='是'";
				if (type.equals("外购件")) {
					hql_ty += " and zhonglei='外购件检验' ";
				} else if (type.equals("包工包料") || type.equals("工序外委")) {
					hql_ty += " and zhonglei in('外委','外委检验','巡检') and gongxuName is null";
				}
				otList = totalDao.query(hql_ty);
				if (otList != null) {
					if (otList.size() == 1) {
						ot = otList.get(0);
						status = "ty";
					}
				}

			}
		} else {
			ot = (OsTemplate) totalDao.get(OsTemplate.class, ostId);
		}
		String mess = "";// 提示
		Float choujianNum = 0f;
		List lists = new ArrayList();
		if (ot != null) {
			if (ot.getFilename() == null) {
				// 查询产品图纸
			}

			// 查询检验明细
			String hql_OsScope = "from OsScope where osTemplate.id=? order by content desc,title desc";
			List list = totalDao.query(hql_OsScope, ot.getId());
			WaigoujianpinciZi xujianpingci = findxunjianpici(waigoudd, ot);
			Float Number = waigoudd.getQrNumber();
			if (waigoudd.getZhuanhuanNum() != null
					&& waigoudd.getZhuanhuanNum() > 0) {
				Number = waigoudd.getZhuanhuanNum();
			}
			if (xujianpingci != null) {
				Float choujian = xujianpingci.getChoujian();
				if ("复检".equals(waigoudd.getAgaincheck())) {
					choujian = xujianpingci.getErchoujian();
				}
				if (choujian != null && choujian > 0) {
					choujianNum = choujian;
					if (choujian > 5) {
						choujian = 5f;
					}
					for (int i = 0; i < choujian; i++) {
						lists.add(list);
					}
				} else {
					String s = "抽检数量";
					if ("复检".equals(waigoudd.getAgaincheck())) {
						s = "再次抽检数量";
					}
					mess = "类型为" + ot.getXjbz() + "的检验标准，数量：" + Number
							+ "的范围，未设置 " + s + " 请及时前往《检验标准管理》添加。";
					AlertMessagesServerImpl.addAlertMessages("检验标准管理", mess,
							"1");
				}
			} else {
				mess = "类型为" + ot.getXjbz() + "的检验标准，未设置数量：" + Number
						+ "的范围，请及时前往《检验标准管理》添加。";
				// 发消息给通知检验标准管理人员
				AlertMessagesServerImpl.addAlertMessages("检验标准管理", mess, "1");
			}
		}
		return new Object[] { ot, lists, otList, status, mess, choujianNum };
	}

	/**
	 * 生成外购件检验记录
	 * 
	 * @param waigoudd
	 * @param osRecordScopeList
	 * @param 不合格品数量
	 * @return
	 */
	@Override
	public String updateStick2(OsRecord osRecord, List<OsRecord> osRecordList,
			String pageStatus, Float jyNumber, Float buhegeNumber,
			List<OsRecord> bhgosRecordList) {
		Integer dpId = 0;
		if (osRecord == null)
			return "数据异常!请检查后重试!";

		Users loginUser = Util.getLoginUser();
		if (loginUser == null)
			return "请先登录!";

		WaigouDeliveryDetail wddOld = (WaigouDeliveryDetail) totalDao
				.getObjectById(WaigouDeliveryDetail.class, osRecord.getWwddId());
		if (wddOld == null)
			return "数据异常!请检查后重试!";
		if ("待入库".equals(wddOld.getStatus()) || "入库".equals(wddOld.getStatus())) {
			return "请勿重复检验!请刷新后重试";
		}
		if (wddOld.getType().equals("外委")) {
			osRecord.setZhonglei("外委");
			String hql_procard = "from Procard where id in (select procardId from ProcessInforWWApplyDetail where id in(select wwDetailId from WaigouPlan where id = ?))";
			Procard p = (Procard) totalDao.getObjectByCondition(hql_procard,
					wddOld.getWaigouPlanId());
			if (p != null) {
				osRecord.setYwmakrId(p.getYwMarkId());
				osRecord.setNeiordeNum(p.getOrderNumber());
				osRecord.setSelfCard(p.getSelfCard());
			}
		} else {
			String ywMarkId = "";
			String neiorderNum = "";
			String selfCard = "";
			osRecord.setZhonglei("外购件检验");
			String hql_procardList = " from Procard where id in (select procardId from ProcardWGCenter where wgOrderId = ?)";
			List<Procard> procardList = totalDao.query(hql_procardList, wddOld
					.getWaigouPlanId());
			for (Procard p : procardList) {
				ywMarkId += ";" + p.getYwMarkId();
				selfCard += ";" + p.getSelfCard();
				neiorderNum += ";" + p.getOrderNumber();
			}
			if (ywMarkId.length() > 1) {
				ywMarkId = ywMarkId.substring(1);
			}
			if (selfCard.length() > 0) {
				selfCard = selfCard.substring(1);
			}
			if (neiorderNum.length() > 1) {
				neiorderNum = neiorderNum.substring(1);
			}
			osRecord.setYwmakrId(ywMarkId);
			osRecord.setNeiordeNum(neiorderNum);
			osRecord.setSelfCard(selfCard);
		}
		String wwdNumber = null;// 送货单号
		if (wddOld.getWaigouDelivery() != null)
			wwdNumber = wddOld.getWaigouDelivery().getPlanNumber();

		// if (wddOld.getIsHege() != null) return "该待检记录已被检验，请勿重复检验";

		Integer buhege = 0;// 不合格数量

		// 根据采购明细ID查询有没有未关闭的门
		boolean b = closeWei(loginUser, wddOld.getId());// 有没有没关上的门

		osRecord.setWwdNumber(wwdNumber);// 送货单
		osRecord.setNowDate(Util.getDateTime());// 当前时间
		osRecord.setSeeDate(Util.getDateTime("yyyy-MM-dd"));
		osRecord.setSelfCard(wddOld.getExamineLot());// 批次
		OsTemplate ot = (OsTemplate) totalDao.getObjectById(OsTemplate.class,
				osRecord.getTemplateId());
		osRecord.setTemplate(ot);
		osRecord.setWgType(wddOld.getWgType());// 物料类别
		if (osRecord.getMeasuring_no() != null
				&& osRecord.getMeasuring_no().length() > 0) {
			String measuring_no = "'"
					+ osRecord.getMeasuring_no().replaceAll(";", "','") + "'";
			List<Measuring> meaList = totalDao
					.query(" from Measuring where measuring_no in ("
							+ measuring_no + ")");
			if (meaList != null && meaList.size() > 0) {
				String measuringMatetag = "";
				for (Measuring mea : meaList) {
					measuringMatetag += ";" + mea.getMatetag();
				}
				if (measuringMatetag.length() > 0) {
					measuringMatetag = measuringMatetag.substring(1);
				}
				osRecord.setMeasuringMatetag(measuringMatetag);
			}
		}

		// 处理检验记录
		Set<OsRecordScope> orsSet = new HashSet<OsRecordScope>();
		if (osRecordList != null && osRecordList.size() > 0) {
			List<OsScope> ossList = new ArrayList<OsScope>();
			for (int i = 0; i < osRecordList.size(); i++) {
				OsRecord osRecord_page = osRecordList.get(i);
				OsTemplate ost = new OsTemplate();
				if (i == 0 && "ty".equals(pageStatus)) {
					ost.setPartNumber(wddOld.getMarkId());// 件号
					ost.setName(wddOld.getProName());// 名称
					ost.setBanbenNumber(wddOld.getBanben());
					String zhonglei = wddOld.getType();
					if ("外购".equals(zhonglei)) {
						zhonglei = "外购件检验";
					} else {
						zhonglei = "外委检验";
					}
					ost.setZhonglei(zhonglei);
					ost.setCreateDate(Util.getDateTime());// 添加时间
					ost.setUsername(Util.getLoginUser().getName());// 添加人
					Set<OsScope> scopes = new HashSet<OsScope>();
					for (OsRecordScope ors : osRecord_page.getScopes()) {
						OsScope os = new OsScope();
						if (ors.getScopeId() != null) {
							OsScope oldos = (OsScope) totalDao.getObjectById(
									OsScope.class, ors.getScopeId());
							BeanUtils.copyProperties(oldos, os, new String[] {
									"id", "osTemplate" });
						} else if (ors.getScope() != null) {
							os = ors.getScope();
							os.setType("手动填写");
						}
						scopes.add(os);
						totalDao.save(os);
						ossList.add(os);
					}
					ost.setScopes(scopes);
					totalDao.save(ost);
				}

				if (osRecord_page == null)
					continue;
				if ("不合格".equals(osRecord_page.getVerification())) {// 不合格品处理流程
					buhege++;
					if (osRecord_page.getKuweiId() != null) {
						// return "第" + i + 1 + "条不合格数量库位ID为空";
						String close = closeDoors(wddOld, osRecord_page);
						if (!"true".equals(close))
							return close;
					}
				}
				int count = 0;
				for (OsRecordScope ors : osRecord_page.getScopes()) {
					OsScope os = null;
					if ("ty".equals(pageStatus) && ossList != null
							&& ossList.size() > 0) {
						os = ossList.get(count);
					} else if (ors.getScopeId() != null) {
						os = (OsScope) totalDao.getObjectById(OsScope.class,
								ors.getScopeId());
						if (os == null) {
							return "没有找到对应的检查项!";
						}
						if (ors.getContent() == null
								|| "".equals(ors.getContent())) {// 没有填写检验明细结果，则设置检验明细结果为合格或者不合格
							ors.setContent(osRecord_page.getVerification());
						}
					}
					ors.setScope(os);
					ors.setJyCount(i + 1);
					totalDao.save(ors);
					orsSet.add(ors);
					count++;
				}
			}
		} else {
			return "数据异常!请检查后重试!";
		}
		// 根据检验标准判定上的AC RE判定退货还是接收;
		Float tuhuNum = buhege.floatValue();
		if (buhegeNumber != null && buhege != null) {
			if ((float) buhegeNumber > (float) buhege) {
				tuhuNum = buhegeNumber;
				buhege = buhegeNumber.intValue();

			}
		}
		WaigoujianpinciZi xjpici = null;// 根据检验标准判断是否需要退货
		String ishege = "合格";
		boolean isHg = true;
		String againcheck = "";
		if (ot != null) {
			xjpici = findxunjianpici(wddOld, ot);
			boolean bool = true;
			Float RE = xjpici.getRe();
			if ("复检".equals(wddOld.getAgaincheck())) {
				bool = false;
				RE = xjpici.getRe1();
				if (RE != null && buhege >= RE) {
					againcheck = "待分检";
					ishege = "不合格";
				}
			}
			if (buhege > 0 && buhege >= RE) {// 超过这个AC RE判定标准 整批退货
				if (bool) {
					Float RE1 = xjpici.getRe1();
					if ((RE1 != null && buhege >= RE1)
							|| (RE1 == null || RE1 == 0)) {
						againcheck = "待分检";
					} else {
						againcheck = "待复检";
					}
				}
				ishege = "不合格";
				isHg = false;
			} else {
				againcheck = "";
			}
		}

		Float rukuNum = wddOld.getQrNumber() - tuhuNum;
		float jyhgNumber = jyNumber - buhege;
		osRecord.setVerification(ishege);
		osRecord.setRecordScope(orsSet);
		osRecord.setQuantity(wddOld.getQrNumber());
		osRecord.setJyNumber(jyNumber);
		osRecord.setHgNumber(jyhgNumber);
		osRecord.setGongxuName(ot.getGongxuName());
		osRecord.setGongxuNum(ot.getGongxuNum());
		String measuringMatetag = "";
		if (osRecord.getMeasuring_no() != null
				&& osRecord.getMeasuring_no().length() > 0) {
			String measuring_no = "'"
					+ osRecord.getMeasuring_no().replaceAll(";", "','") + "'";
			List<String> strList = totalDao
					.query("select matetag from Measuring where measuring_no in ("
							+ measuring_no + ")");
			for (String str : strList) {
				measuringMatetag += ";" + str;
			}
			if (measuringMatetag.length() > 1) {
				measuringMatetag = measuringMatetag.substring(1);
			}
		}
		osRecord.setMeasuringMatetag(measuringMatetag);

		if (isHg) {
			osRecord.setRuku(true);
		} else {
			osRecord.setRuku(true);
		}
		// 缺陷类型数量统计，不合格品质量周报月报统计。
		if (bhgosRecordList != null && bhgosRecordList.size() > 0) {
			bhgTypeNum(osRecord, bhgosRecordList, buhege.floatValue(), wddOld
					.getType());
		} else {
			totalDao.save(osRecord);
		}
		/****
		 * 数据统一汇总
		 */
		wddOld.setIsHege(osRecord.getVerification());// 是否合格
		wddOld.setUserId(loginUser.getId());// 检验员id
		wddOld.setUserCode(loginUser.getCode());// 检验员工号
		wddOld.setUserName(loginUser.getName());// 检验员名称
		wddOld.setCheckTime(Util.getDateTime());// 检验时间
		wddOld.setJyNumber(jyNumber);// 检验数量
		wddOld.setJybhgNumber(buhege.floatValue());// 检验不合格数量
		wddOld.setJyhgNumber(jyhgNumber);// 检验数量
		// Float tuihuoNumber = wddOld.getQrNumber();// 退货数量 默认为确认数量
		String status = "待入库";
		String zzpd = "";
		String wgdSheetStatus = "";
		if ("不合格".equals(ishege)) {
			hgNumber = 0f;
			wgdSheetStatus = "待终检";
			status = againcheck;
		} else {
			zzpd = "合格";
			wgdSheetStatus = "完成";
			// tuihuoNumber = 0f;
		}
		// if ("合格".equals(osRecord.getVerification())) {
		// hgNumber = osRecord.getHgNumber();// 确认数量减去不合格数量(页面)
		// tuihuoNumber = osRecord.getQuantity() - osRecord.getHgNumber();
		// } else {
		// status = "退货";
		// }
		wddOld.setStatus(status);
		wddOld.setHgNumber(rukuNum);
		wddOld.setBhgNumber(buhege.floatValue());

		if ("完成".equals(wgdSheetStatus)) {
			wddOld.setHegeRate(1F);
		}
		wddOld.setAgaincheck(againcheck);
		String msg = totalDao.update(wddOld) + "";

		// 更新送货单状态
		WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
				.getObjectById(WaigouDelivery.class, wddOld.getWaigouDelivery()
						.getId());
		// 记录不良品率（判断是否合格和是否是否是复检）
		if ("合格".equals(ishege)) {// 合格后记录合格率
			jisuanHege(wddOld, buhege);
		}
		if ("待分检".equals(wddOld.getAgaincheck()) && "不合格".equals(ishege)) {// 第二次检验
			jisuanBeHege(wddOld, buhege);
		}

		// 更新来料日报表信息
		waigouDaSh(osRecord, loginUser, wddOld, buhege, jyNumber, jyhgNumber,
				zzpd, wgdSheetStatus);

		// 待检库出库出库记录=>生成不合格品入库记录=>生成不合格品库存记录=>创建不良品处理单
		outCheckKu(osRecord, wddOld, buhege, ishege, againcheck, waigouDelivery);
		// 更新采购订单信息
		if (wddOld.getWaigouPlanId() != null) {
			// 查询采购订单，更新状态
			updateWaiPlan(osRecord, loginUser, wddOld, buhege, isHg, rukuNum,
					status, waigouDelivery, null);
		}
		if (b)
			sleeps(3000);

		if (!isHg) {
			msg = againcheck;
		}
		waigouDelivery.setStatus(wddOld.getStatus());
		totalDao.update(waigouDelivery);
		totalDao.update(wddOld);
		// 发送 库位信息
		String pinmu = sendPinMu(wddOld);
		if (!"true".equals(pinmu))
			return pinmu;

		return msg;

	}

	/**
	 * 更新库位屏幕信息
	 * 
	 * @param wddOld
	 * @return
	 */
	private String sendPinMu(WaigouDeliveryDetail wddOld) {
		List<WareBangGoogs> listw = totalDao.query(
				"from WareBangGoogs where fk_waigouDeliveryDetail_id = ?",
				wddOld.getId());// and status = '检验中'
		if (listw != null && listw.size() > 0) {// 更新几个库位
			for (WareBangGoogs wbg : listw) {
				StringBuffer buffer = new StringBuffer();
				StringBuffer buffer2 = new StringBuffer("");
				wbg.setStatus("待确认");
				WarehouseNumber wn = byIdWN(wbg.getFk_ware_id());
				if (wn == null)
					return "库位为空！状态更新失败";//
				updateWN(wn, "合格品");
				int ir = 1;
				int listwbg = totalDao.getCount(
						"from WareBangGoogs where fk_ware_id = ?", wbg
								.getFk_ware_id());
				if (listwbg == 1) {
					buffer.append(Util.neirong(wddOld.getMarkId() + "", 30));// 件号
					buffer
							.append(Util.neirong(wddOld.getExamineLot() + "",
									16));// 批次
					buffer.append(Util.neirong(wbg.getNumber() + "", 8));// 数量
					buffer.append(Util.neirong(wn.getNumber() + "", 16));// 库位编号
					buffer.append(Util.neirong(wn.getMarkTyptName() + "", 8));// 库位状态
					buffer.append(Util.neirong(wddOld.getGysName() + "", 30));// 供应商
				} else {
					List<WareBangGoogs> listwbgs = totalDao.query(
							"from WareBangGoogs where fk_ware_id = ?", wbg
									.getFk_ware_id());
					for (WareBangGoogs ws : listwbgs) {
						if (ir > 2) {
							if (ws.getFk_waigouDeliveryDetail_id() == wddOld
									.getId()) {
								buffer2.append(Util.neirong("件号:"
										+ wddOld.getMarkId() + "", 30));// 件号
								buffer2.append(Util.neirong("批次:"
										+ wddOld.getExamineLot() + "", 16));// 批次
								buffer2.append(Util.neirong("数量:"
										+ ws.getNumber() + "", 8));// 数量
								buffer2.append(Util.neirong("库位编号:"
										+ wn.getNumber() + "", 16));// 库位编号
								buffer2.append(Util.neirong("库位状态:"
										+ wn.getMarkTyptName() + "", 8));// 库位状态
								buffer2.append(Util.neirong("供应商:"
										+ wddOld.getGysName() + "", 30));// 供应商
							} else {
								WaigouDeliveryDetail deli = findWaigouPlanById(ws
										.getFk_waigouDeliveryDetail_id());
								if (deli == null)
									continue;
								buffer2.append(Util.neirong("件号:"
										+ deli.getMarkId() + "", 30));// 件号
								buffer2.append(Util.neirong("批次:"
										+ deli.getExamineLot() + "", 16));// 批次
								buffer2.append(Util.neirong("数量:"
										+ ws.getNumber() + "", 8));// 数量
								buffer2.append(Util.neirong("库位编号:"
										+ wn.getNumber() + "", 16));// 库位编号
								buffer2.append(Util.neirong("库位状态:"
										+ wn.getMarkTyptName() + "", 8));// 库位状态
								buffer2.append(Util.neirong("供应商:"
										+ deli.getGysName() + "", 30));// 供应商
							}
						} else {
							if (ws.getFk_waigouDeliveryDetail_id() == wddOld
									.getId()) {
								buffer.append(Util.neirong(wddOld.getMarkId()
										+ "", 30));// 件号
								buffer.append(Util.neirong(wddOld
										.getExamineLot()
										+ "", 16));// 批次
								buffer.append(Util.neirong(ws.getNumber() + "",
										8));// 数量
								buffer.append(Util.neirong(wn.getNumber() + "",
										16));// 库位编号
								buffer.append(Util.neirong(wn.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer.append(Util.neirong(wddOld.getGysName()
										+ "", 30));// 供应商
							} else {
								WaigouDeliveryDetail deli = findWaigouPlanById(ws
										.getFk_waigouDeliveryDetail_id());
								if (deli == null)
									continue;
								buffer.append(Util.neirong(deli.getMarkId()
										+ "", 30));// 件号
								buffer.append(Util.neirong(deli.getExamineLot()
										+ "", 16));// 批次
								buffer.append(Util.neirong(ws.getNumber() + "",
										8));// 数量
								buffer.append(Util.neirong(wn.getNumber() + "",
										16));// 库位编号
								buffer.append(Util.neirong(wn.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer.append(Util.neirong(deli.getGysName()
										+ "", 30));// 供应商
							}
						}
						ir++;
					}
				}
				totalDao.update(wbg);
				// 亮灯的颜色
				UtilTong.openColorLight(wn.getIp(), duankou, true, true, wn
						.getOneNumber(), wn.getNumid(),
						wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
								.getMarkColor());
				sleeps(500);
				// 发送详细屏幕信息
				UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
						.getOneNumber(), wn.getNumid(), buffer.toString(), 1);
				if (ir > 3) {
					sleeps(500);
					UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
							.getOneNumber(), wn.getNumid(), buffer2.toString(),
							3);
				}
				if (ir == 3) {
					sleeps(500);
					UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
							.getOneNumber(), wn.getNumid(), buffer2.toString(),
							2);
				}
			}
		}
		return "true";
	}

	private String closeDoors(WaigouDeliveryDetail wddOld,
			OsRecord osRecord_page) {
		WareBangGoogs googs = (WareBangGoogs) totalDao
				.getObjectByCondition(
						"from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and fk_ware_id = ?",
						wddOld.getId(), osRecord_page.getKuweiId());
		if (googs != null) {
			googs.setNumber(googs.getNumber() - 1f);
			if (googs.getNumber() == 0) {// 当前库位此件号数量为空
				int ki = totalDao
						.getCount(
								"from WareBangGoogs where fk_ware_id = ? and fk_waigouDeliveryDetail_id not in ("
										+ wddOld.getId() + ")", googs
										.getFk_ware_id());
				if (ki > 0)
					totalDao.delete(googs);// 还有其他物品
				else {// 没有其余物品了=>将库位清空
					WarehouseNumber wnb = (WarehouseNumber) totalDao
							.getObjectById(WarehouseNumber.class, googs
									.getFk_ware_id());
					if (wnb == null)
						return googs.getFk_ware_id() + "库位不存在";
					wnb.setHave("无");
					wnb.setStatus("未满");
					updateWN(wnb, "空");
					totalDao.update(wnb);
				}
			} else
				totalDao.update(googs);
		}
		return "true";
	}

	/**
	 * 更新来料日报表信息
	 * 
	 * @param osRecord
	 * @param loginUser
	 * @param wddOld
	 * @param buhege
	 * @param jyNumber
	 * @param jyhgNumber
	 * @param zzpd
	 * @param wgdSheetStatus
	 */
	private void waigouDaSh(OsRecord osRecord, Users loginUser,
			WaigouDeliveryDetail wddOld, Integer buhege, float jyNumber,
			float jyhgNumber, String zzpd, String wgdSheetStatus) {
		String hql_wgdSheet = "from WaigouDailySheet where wgddId = ?";
		WaigouDailySheet wgdSheet = (WaigouDailySheet) totalDao
				.getObjectByCondition(hql_wgdSheet, wddOld.getId());
		if (wgdSheet != null) {
			wgdSheet.setCjTime(Util.getDateTime());// 初检时间
			wgdSheet.setCjUser(loginUser.getName());// 初检人
			wgdSheet.setCjUserId(loginUser.getId());// 初检人Id
			wgdSheet.setCjNumber(jyNumber);// 初检数量
			wgdSheet.setCjbhgNumber(buhege.floatValue());// 初检不合格数量
			wgdSheet.setCjhgNumber(jyhgNumber);// 初检合格数量
			wgdSheet.setZzpd(zzpd);
			wgdSheet.setStatus(wgdSheetStatus);
			wgdSheet.setScopeId(osRecord.getId());
			totalDao.update(wgdSheet);
		} else {// 没有 则新建
			wgdSheet = new WaigouDailySheet();
			wgdSheet.setMarkId(wddOld.getMarkId());// 件号
			wgdSheet.setProName(wddOld.getProName());// 产品名称
			wgdSheet.setGysName(wddOld.getGysName());// 供应商名称
			ZhUser zhusers = (ZhUser) totalDao.get(ZhUser.class, wddOld
					.getGysId());
			if (zhusers != null) {
				wgdSheet.setGysjc(zhusers.getName()); // 供应商简称
			}
			wgdSheet.setGysId(wddOld.getGysId()); // 供应商Id
			wgdSheet.setTuhao(wddOld.getTuhao());// 图号;
			wgdSheet.setLlNumber(wddOld.getQrNumber());// 来料数量
			wgdSheet.setExamineLot(wddOld.getExamineLot());// 检验批次
			wgdSheet.setCgOrderNum(wddOld.getCgOrderNum());// 采购订单号
			wgdSheet.setWgType(wddOld.getWgType());// 物料类别
			wgdSheet.setBanbenNumber(wddOld.getBanben());// 版本
			wgdSheet.setKgliao(wddOld.getKgliao());// 供料属性
			wgdSheet.setShOrderNum(wddOld.getWaigouDelivery().getPlanNumber());// 送货单号
			String caizi = "";
			String hql = " from YuanclAndWaigj where markId = ?";
			// 版本
			if (wddOld.getBanben() != null && wddOld.getBanben().length() > 0) {
				hql += " and banbenhao = '" + wddOld.getBanben() + "'";
			} else {
				hql += " and ( banbenhao = '' or banbenhao is null)";
			}
			// 供料属性
			if (wddOld.getKgliao() != null && wddOld.getKgliao().length() > 0) {
				hql += " and kgliao = '" + wddOld.getKgliao() + "'";
			} else {
				hql += " and(kgliao = '' or  kgliao is null)";
			}
			YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj) totalDao
					.getObjectByCondition(hql, wddOld.getMarkId());
			if (yuanclAndWaigj != null) {
				caizi = yuanclAndWaigj.getCaizhi();
			} else {
				caizi = wddOld.getWgType();
			}
			wgdSheet.setCaizi(caizi);
			wgdSheet.setWgddId(wddOld.getId());
			wgdSheet.setYear(Util.getDateTime("yyyy") + "年");
			wgdSheet.setMonth(Util.getDateTime("yyyy-MM") + "月");
			wgdSheet.setLiaodate(Util.getDateTime("MM-dd"));
			wgdSheet.setWeek(Util.getWeek(null, null));
			wgdSheet.setAddTime(Util.getDateTime());
			wgdSheet.setAddUser(Util.getLoginUser().getName());
			wgdSheet.setCjTime(Util.getDateTime());// 初检时间
			wgdSheet.setCjUser(loginUser.getName());// 初检人、
			wgdSheet.setCjUserId(loginUser.getId());// 初检人Id
			wgdSheet.setCjNumber(jyNumber);// 初检数量
			wgdSheet.setCjbhgNumber(buhege.floatValue());// 初检不合格数量
			wgdSheet.setCjhgNumber(jyhgNumber);// 初检合格数量
			wgdSheet.setSpecification(wddOld.getSpecification());// 规格
			wgdSheet.setZzpd(zzpd);
			wgdSheet.setStatus(wgdSheetStatus);
			wgdSheet.setScopeId(osRecord.getId());
			totalDao.save(wgdSheet);
		}
	}

	/**
	 * 查询采购订单，更新状态
	 * 
	 * @param osRecord
	 * @param loginUser
	 * @param wddOld
	 * @param buhege
	 * @param isHg
	 * @param rukuNum
	 * @param status
	 * @param waigouDelivery
	 * @param waigouplan
	 */
	private void updateWaiPlan(OsRecord osRecord, Users loginUser,
			WaigouDeliveryDetail wddOld, Integer buhege, boolean isHg,
			Float rukuNum, String status, WaigouDelivery waigouDelivery,
			WaigouPlan waigouPlan) {

		WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
				wddOld.getWaigouPlanId());
		if (waigouplan == null)
			return;

		waigouplan.setStatus(status);
		// 更新采购单合格数量;
		if (waigouplan.getHgNumber() != null) {
			waigouplan.setHgNumber(wddOld.getHgNumber()
					+ waigouplan.getHgNumber());
		} else {
			waigouplan.setHgNumber(wddOld.getHgNumber());
		}

		waigouplan.setKeruku(waigouplan.getHgNumber());
		totalDao.update(waigouplan);

		// 更新物料需求汇总上的合格数量
		if (waigouplan.getMopId() != null && waigouplan.getMopId() > 0) {
			// Float hgNumber = (Float) totalDao.getObjectByCondition(
			// "select sum(hgNumber) from WaigouPlan where mopId=?",
			// waigouplan.getMopId());
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, waigouplan.getMopId());
			// mop.setHgCount(hgNumber);
			if (mop != null) {
				if (mop.getHgCount() != null) {
					mop.setHgCount(mop.getHgCount() + wddOld.getHgNumber());
				} else {
					mop.setHgCount(wddOld.getHgNumber());
				}
				totalDao.update(mop);
			}

		}

		// 查询采购订单，更新状态
		WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
				WaigouOrder.class, waigouplan.getWaigouOrder().getId());
		waigouOrder.setStatus(status);
		totalDao.update(waigouOrder);

		String jynumberMes = "";
		jynumberMes = "(不合格：" + buhege + wddOld.getUnit() + ")";
		String wlwzdtmes = "质量检验:<br/>供应商:"
				+ waigouDelivery.getGysName()
				+ "<br/>送货单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
				+ waigouDelivery.getId() + "'>"
				+ waigouDelivery.getPlanNumber()
				+ "</a><br/>检验批次:<a href='OsRecord_showScope.action?record.id="
				+ osRecord.getId() + "'>" + wddOld.getExamineLot()
				+ "</a><br/>检验数量:" + wddOld.getQrNumber() + wddOld.getUnit()
				+ jynumberMes + "<br/>检验人:" + loginUser.getName()
				+ ";<br/>检验时间:" + Util.getDateTime() + "<br/>当前位置:"
				+ wddOld.getQrWeizhi() + "<hr/>";
		/************************ 演示设置为空 ****************************/

		// 物料位置
		WlWeizhiDt wDt = new WlWeizhiDt(null, waigouplan.getId(), null,
				wlwzdtmes);
		totalDao.save(wDt);

		// 查询采购明细对应生产批次
		// String hql_procard =
		// "from Procard where id in (select procardId from ProcardWGCenter where wgOrderId=?) ";
		String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id = ?)";
		List list_procrd = totalDao.query(hql_procard, waigouplan.getMopId());
		if (list_procrd != null && list_procrd.size() > 0) {
			for (int j = 0, len = list_procrd.size(); j < len; j++) {
				Procard procard = (Procard) list_procrd.get(j);
				procard.setWlstatus(status);
				totalDao.update(procard);
				// 物料位置
				WlWeizhiDt wDt1 = new WlWeizhiDt(procard.getId(), null, null,
						wlwzdtmes);
				totalDao.save(wDt1);
			}
		}

		if (buhege > 0) {
			// 发送消息给采购员;
			AlertMessagesServerImpl.addAlertMessages("采购订单管理",
					"您添加的采购订单检验出不合格数量:" + buhege + "，订单编号:"
							+ waigouOrder.getPlanNumber() + "件号:"
							+ waigouplan.getMarkId(), "1", waigouplan
							.getUserCode());
			ZhUser zhuer = (ZhUser) totalDao.get(ZhUser.class, wddOld
					.getGysId());

			// 发送消息给供应商
			AlertMessagesServerImpl.addAlertMessages("送货单管理(供应商)",
					"您的送货单检验出不合格数量:" + buhege + "，送货单编号:"
							+ waigouDelivery.getPlanNumber() + "件号:"
							+ wddOld.getMarkId(), "1", zhuer.getUsercode());
		}
		if (rukuNum > 0 && isHg) {
			// 发消息给仓库入库
			String functionName = "";
			if ("外购".equals(wddOld.getType())) {
				functionName = "外购入库";
			} else if ("外委".equals(wddOld.getType())) {
				functionName = "外委入库";
			}
			String hql2 = " select u.id from Users u join u.moduleFunction f  where f.functionName =?";
			List<Integer> userIdList = totalDao.query(hql2, functionName);
			Integer[] userIds = null;
			if (userIdList != null && userIdList.size() > 0) {
				userIds = new Integer[userIdList.size()];
				for (int i = 0; i < userIdList.size(); i++) {
					userIds[i] = userIdList.get(i);
				}
			}

			AlertMessagesServerImpl.addAlertMessages(functionName + "提醒",
					"送货单号:" + wddOld.getWaigouDelivery().getPlanNumber()
							+ ",件号:" + wddOld.getMarkId() + "," + "名称:"
							+ wddOld.getProName() + ",检验批次:"
							+ wddOld.getExamineLot() + "," + "已签收检验完毕,合格数量:"
							+ wddOld.getHgNumber() + "(" + wddOld.getUnit()
							+ "),可入库数量:" + wddOld.getHgNumber() + "("
							+ wddOld.getUnit() + ")。请及时入库", userIds, "", true,
					"yes");

		}
	}

	/**
	 * 待检库出库
	 * 
	 * @param osRecord
	 * @param wddOld
	 * @param buhege
	 * @param ishege
	 * @param againcheck
	 * @param waigouDelivery
	 */
	private void outCheckKu(OsRecord osRecord, WaigouDeliveryDetail wddOld,
			Integer buhege, String ishege, String againcheck,
			WaigouDelivery waigouDelivery) {
		Integer dpId;
		// 如果有不合格品库，不合格品先出库;
		String hql_bhg = " from GoodsStore where goodsStoreWarehouse = ? and wwddId=? and goodsStoreMarkId =?";
		GoodsStore bhggt = (GoodsStore) totalDao.getObjectByCondition(hql_bhg,
				"不合格品库", wddOld.getId(), wddOld.getMarkId());

		if (bhggt != null) {
			String hql = "from Goods where goodsMarkId = ? ";
			if (bhggt.getGoodsStoreWarehouse() != null
					&& bhggt.getGoodsStoreWarehouse().length() > 0) {
				hql += " and goodsClass='" + bhggt.getGoodsStoreWarehouse()
						+ "'";
			}
			if (bhggt.getGoodHouseName() != null
					&& bhggt.getGoodHouseName().length() > 0) {
				hql += " and goodHouseName='" + bhggt.getGoodHouseName() + "'";
			}
			if (bhggt.getGoodsStorePosition() != null
					&& bhggt.getGoodsStorePosition().length() > 0) {
				hql += " and goodsPosition='" + bhggt.getGoodsStorePosition()
						+ "'";
			}
			if (bhggt.getBanBenNumber() != null
					&& bhggt.getBanBenNumber().length() > 0) {
				hql += " and banBenNumber='" + bhggt.getBanBenNumber() + "'";
			}
			if (bhggt.getKgliao() != null && bhggt.getKgliao().length() > 0) {
				hql += " and kgliao='" + bhggt.getKgliao() + "'";
			}
			if (bhggt.getGoodsStoreLot() != null
					&& bhggt.getGoodsStoreLot().length() > 0) {
				hql += " and goodsLotId='" + bhggt.getGoodsStoreLot() + "'";
			}
			Goods s = (Goods) totalDao.getObjectByCondition(hql,
					new Object[] { bhggt.getGoodsStoreMarkId() });
			if (s != null) {
				s.setGoodsCurQuantity(0f);
				Sell sell = new Sell();
				sell.setSellMarkId(s.getGoodsMarkId());// 件号
				sell.setSellLot(s.getGoodsLotId());//批次
				sell.setSellWarehouse(s.getGoodsClass());// 库别
				sell.setGoodHouseName(s.getGoodHouseName());// 仓区
				sell.setKuwei(s.getGoodsPosition());// 库位
				sell.setBanBenNumber(s.getBanBenNumber());// 版本号
				sell.setKgliao(s.getKgliao());// 供料属性
				sell.setWgType(s.getWgType());// 物料类别
				sell.setSellGoods(s.getGoodsFullName());// 品名
				sell.setSellFormat(s.getGoodsFormat());// 规格
				sell.setSellCount(s.getGoodsCurQuantity());// 出库数量
				sell.setSellUnit(s.getGoodsUnit());// 单位
				sell.setSellSupplier(s.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setTuhao(s.getTuhao());// 图号
				sell.setBedit(false);// 出库权限
				sell.setBedit(false);// 编辑权限
				sell.setSellZhishu(s.getGoodsZhishu());
				sell.setPrintStatus("YES");// 打印状态
				sell.setStyle("不合格品出库");
				sell.setYwmarkId(s.getYwmarkId());// 业务件号
				sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
				sell.setOrderNum(s.getNeiorderId());// 内部订单号
				totalDao.save(sell);
				totalDao.update(s);
				// 入到待检库
				// 入到不合格品库;
				GoodsStore gt = new GoodsStore();
				gt.setGoodsStoreMarkId(wddOld.getMarkId());// 件号
				gt.setGoodsStoreWarehouse("待检库");// 库别
				gt.setGoodHouseName(bhggt.getGoodHouseName());// 仓区
				gt.setGoodsStorePosition(bhggt.getGoodsStorePosition());// 库位
				gt.setBanBenNumber(wddOld.getBanben());// 版本
				gt.setKgliao(wddOld.getKgliao());// 供料属性
				gt.setGoodsStoreLot(wddOld.getExamineLot());// 批次
				gt.setHsPrice(wddOld.getHsPrice()==null?null:wddOld.getHsPrice().doubleValue());// 含税单价
				gt.setGoodsStoreGoodsName(wddOld.getProName());// 名称
				gt.setGoodsStoreFormat(wddOld.getSpecification());// 规格
				gt.setWgType(wddOld.getWgType());// 物料类别
				gt.setGoodsStoreUnit(wddOld.getUnit());// 单位
				gt.setGoodsStoreCount(buhege.floatValue());// 数量
				gt.setGoodsStorePrice(wddOld.getPrice()==null?null:wddOld.getPrice().doubleValue());// 不含税单价
				gt.setTaxprice(wddOld.getTaxprice());// 税率
				gt.setPriceId(wddOld.getPriceId());// 价格Id
				gt.setGoodsStoreSupplier(wddOld.getGysName());// 供应商名称
				gt.setGysId(wddOld.getGysId() + "");// 供应商Id
				gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
				gt.setGoodsStoreTime(Util.getDateTime());
				gt.setWwddId(wddOld.getId());// 送货单明细Id
				gt.setGoodsStoreSendId(waigouDelivery.getPlanNumber());// 送货单号
				gt.setTuhao(wddOld.getTuhao());// 图号
				gt.setStyle("待检入库");// 入库类型
				gt.setYwmarkId(bhggt.getYwmarkId());
				gt.setNeiorderId(bhggt.getNeiorderId());
				gt.setWaiorderId(bhggt.getWaiorderId());
				gt.setPrintStatus("YES");
				gt.setGoodsStoreZhishu(wddOld.getZhuanhuanNum());
				goodsStoreServer.saveSgrk(gt);
			}
		}

		// 待检库出库;
		String hql_goods = " from GoodsStore where goodsStoreWarehouse = ? and wwddId=? and goodsStoreMarkId =?";
		GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_goods,
				"待检库", wddOld.getId(), wddOld.getMarkId());
		if (t != null) {
			String hql = "from Goods where goodsMarkId = ? ";
			if (t.getGoodsStoreWarehouse() != null
					&& t.getGoodsStoreWarehouse().length() > 0) {
				hql += " and goodsClass='" + t.getGoodsStoreWarehouse() + "'";
			}
			if (t.getGoodHouseName() != null
					&& t.getGoodHouseName().length() > 0) {
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
			if (t.getGoodsStoreLot() != null
					&& t.getGoodsStoreLot().length() > 0) {
				hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
			}
			Goods s = (Goods) totalDao.getObjectByCondition(hql,
					new Object[] { t.getGoodsStoreMarkId() });
			t.setInputSource("待入库");
			totalDao.update(t);
			boolean bool = false;
			if (s != null) {
				bool = true;
				s.setInputSource("待入库");
				totalDao.update(s);
			}
			if (buhege > 0) {
				if (bool) {
					s.setGoodsCurQuantity(s.getGoodsCurQuantity() - buhege);
					Sell sell = new Sell();
					sell.setSellMarkId(s.getGoodsMarkId());// 件号
					sell.setSellWarehouse(s.getGoodsClass());// 库别
					sell.setGoodHouseName(s.getGoodHouseName());// 仓区
					sell.setKuwei(s.getGoodsPosition());// 库位
					sell.setBanBenNumber(s.getBanBenNumber());// 版本号
					sell.setKgliao(s.getKgliao());// 供料属性
					sell.setWgType(s.getWgType());// 物料类别
					sell.setSellGoods(s.getGoodsFullName());// 品名
					sell.setSellFormat(s.getGoodsFormat());// 规格
					sell.setSellCount(buhege.floatValue());// 出库数量
					sell.setSellUnit(s.getGoodsUnit());// 单位
					sell.setSellSupplier(s.getGoodsSupplier());// 供应商
					sell.setSellTime(Util.getDateTime());// 出库时间
					sell.setTuhao(s.getTuhao());// 图号
					sell.setBedit(false);// 出库权限
					sell.setBedit(false);// 编辑权限
					sell.setSellZhishu(s.getGoodsZhishu());
					sell.setPrintStatus("YES");// 打印状态
					sell.setSellLot(s.getGoodsLotId());// 批次
					sell.setYwmarkId(s.getYwmarkId());// 业务件号
					sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
					sell.setOrderNum(s.getNeiorderId());// 内部订单号
					sell.setStyle("待检库出库");
					totalDao.save(sell);
					totalDao.update(s);
				}
				// 入到不合格品库;
				GoodsStore gt = new GoodsStore();
				gt.setGoodsStoreMarkId(wddOld.getMarkId());// 件号
				gt.setGoodsStoreWarehouse("不合格品库");// 库别
				if (t != null) {
					gt.setGoodHouseName(t.getGoodHouseName());// 仓区
					gt.setGoodsStorePosition(t.getGoodsStorePosition());// 库位
				}
				gt.setBanBenNumber(wddOld.getBanben());// 版本
				gt.setKgliao(wddOld.getKgliao());// 供料属性
				gt.setGoodsStoreLot(wddOld.getExamineLot());// 批次
				gt.setHsPrice(wddOld.getHsPrice()==null?null:wddOld.getHsPrice().doubleValue());// 含税单价
				gt.setGoodsStoreGoodsName(wddOld.getProName());// 名称
				gt.setGoodsStoreFormat(wddOld.getSpecification());// 规格
				gt.setWgType(wddOld.getWgType());// 物料类别
				gt.setGoodsStoreUnit(wddOld.getUnit());// 单位
				gt.setGoodsStoreCount(buhege.floatValue());// 数量
				gt.setGoodsStorePrice(wddOld.getPrice()==null?null:wddOld.getPrice().doubleValue());// 不含税单价
				gt.setTaxprice(wddOld.getTaxprice());// 税率
				gt.setPriceId(wddOld.getPriceId());// 价格Id
				gt.setGoodsStoreSupplier(wddOld.getGysName());// 供应商名称
				gt.setGysId(wddOld.getGysId() + "");// 供应商Id
				gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
				gt.setGoodsStoreTime(Util.getDateTime());
				gt.setWwddId(wddOld.getId());// 送货单明细Id
				gt.setGoodsStoreSendId(waigouDelivery.getPlanNumber());// 送货单号
				gt.setTuhao(wddOld.getTuhao());// 图号
				gt.setStyle("不合格品入库");// 入库类型
				gt.setPrintStatus("YES");
				gt.setYwmarkId(t.getYwmarkId());
				gt.setNeiorderId(t.getNeiorderId());
				gt.setWaiorderId(t.getWaiorderId());
				gt.setGoodsStoreZhishu(wddOld.getZhuanhuanNum());
				goodsStoreServer.saveSgrk(gt);
				// 创建不良品处理单;
				DefectiveProduct dp = new DefectiveProduct();
				dp.setCgOrderNum(wddOld.getCgOrderNum());// 采购订单号
				dp.setShOrderNum(waigouDelivery.getPlanNumber());// 送货单号;
				dp.setMarkId(wddOld.getMarkId());// 件号
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
				dp.setGysUsersId(waigouDelivery.getUserId());// 供应商UserId
				dp.setExamineLot(wddOld.getExamineLot());// 检验批次
				dp.setJyuserId(wddOld.getUserId());// 检验员Id
				dp.setJyuserCode(wddOld.getUserCode());// 检验员工号
				dp.setJyuserName(wddOld.getUserName());// 检验员名称
				dp.setCheckTime(wddOld.getCheckTime());// 初验时间
				dp.setLlNumber(wddOld.getQrNumber());// 来料数量
				dp.setJyNumber(wddOld.getJyNumber());// 检验数量
				dp.setJyhgNumber(wddOld.getJyhgNumber());// 检验合格数量
				dp.setJybhgNumber(buhege.floatValue());// 检验不合格数量
				dp.setWgddId(wddOld.getId());// 送货单明细Id
				dp.setIshege(ishege);
				dp.setZhuanhuanNum(wddOld.getZhuanhuanNum());
				dp.setZhuanhuanUit(wddOld.getZhuanhuanUnit());
				dp.setType(wddOld.getType() + "来料不良");
				dp.setStatus("待确认");
				dp.setWlType(wddOld.getType());
				dp.setAgaincheck(againcheck);
				dp.setRamk(osRecord.getType());
				dp.setAddTime(Util.getDateTime());// 添加时间
				dp.setAddUser(Util.getLoginUser().getName());// 添加人
				wddOld.setStatus("待复检");
				totalDao.save(dp);
				dpId = dp.getId();
				// 发消息给检验员 让其再次确认
				AlertMessagesServerImpl.addAlertMessages(wddOld.getType()
						+ "待确认不良品管理", "您检验的件号:" + dp.getMarkId() + "，订单编号:"
						+ dp.getCgOrderNum() + "检验批次:" + dp.getExamineLot()
						+ "来料数量:" + dp.getLlNumber() + "检验出不合格数量:" + buhege
						+ "请及时确认。", "1");
				// 发消息给检验员 让其再次确认
				// 发消息给检测室实验员，进行环保、盐雾等检测。
				AlertMessagesServerImpl.addAlertMessages("来料日报表管理", "件号:"
						+ dp.getMarkId() + "，订单编号:" + dp.getCgOrderNum()
						+ "检验批次:" + dp.getExamineLot()
						+ "已检验完毕，请及时进行环保、盐雾等检测。检测完毕后请上传，检测文件。 ", "1");
			} else {
				// 发消息给仓库入库
				if (wddOld.getQrUsersId() != null) {
					Integer[] userIds = { wddOld.getQrUsersId() };
					AlertMessagesServerImpl.addAlertMessages("外购外委入库提醒",
							"送货单号:"
									+ wddOld.getWaigouDelivery()
											.getPlanNumber() + ",件号:"
									+ wddOld.getMarkId() + "" + ",名称:"
									+ wddOld.getProName() + ",检验批次:"
									+ wddOld.getExamineLot() + ","
									+ "已检验完毕,合格数量:" + wddOld.getHgNumber()
									+ "(" + wddOld.getUnit() + "),可入库数量:"
									+ wddOld.getHgNumber() + "("
									+ wddOld.getUnit() + ")。请及时入库", userIds,
							"", true, "false");
				}
			}
		}

	}

	/**
	 * 检验合格后日周年月合格率
	 */
	public void jisuanHege(WaigouDeliveryDetail wddOld, Integer buhege) {
		if (wddOld != null) {
			QualifiedRate qualifiedRate = (QualifiedRate) totalDao
					.getObjectByCondition("From QualifiedRate where gysid =? ",
							wddOld.getGysId());
			ZhUser zhUser = (ZhUser) totalDao.get(ZhUser.class, wddOld
					.getGysId());
			if (qualifiedRate != null) {// 是否之前有记录
				qualifiedRate.setBhgCount(qualifiedRate.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				qualifiedRate.setShCount(qualifiedRate.getShCount()
						+ wddOld.getQrNumber());// 总数量
				qualifiedRate.setCheckNopass(qualifiedRate.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				qualifiedRate
						.setQualifiedrate(1 - (qualifiedRate.getBhgCount() / qualifiedRate
								.getShCount()));// 合格率
				qualifiedRate.setPiciCount(qualifiedRate.getPiciCount() + 1F);// 总批次
				qualifiedRate
						.setPicihgCount(qualifiedRate.getPicihgCount() + 1F);// 合格批次
				qualifiedRate.setPiciQualifiedrate(qualifiedRate
						.getPicihgCount()
						/ qualifiedRate.getPiciCount());// 批次率
				zhUser.setZhiliang(qualifiedRate.getPiciQualifiedrate());
				zhUser.setHegelv(qualifiedRate.getQualifiedrate());
				totalDao.update(qualifiedRate);
			} else {
				QualifiedRate qualifiedRate1 = new QualifiedRate();
				qualifiedRate1.setGysid(wddOld.getGysId());// 供应商名字
				qualifiedRate1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				qualifiedRate1.setBhgCount(buhege.floatValue());// 不合格数量
				qualifiedRate1.setShCount(wddOld.getQrNumber());// 总送货数量
				qualifiedRate1.setQualifiedrate(1 - (qualifiedRate1
						.getBhgCount() / qualifiedRate1.getShCount()));// 合格率
				// 不良品
				qualifiedRate1.setCheckNopass(buhege.floatValue());// 检验不合格
				qualifiedRate1.setNoProduce(0F);
				// 批次合格率数据
				qualifiedRate1.setPiciCount(1F);// 总批次
				qualifiedRate1.setPicihgCount(1F);// 合格批次
				qualifiedRate1.setPiciQualifiedrate(qualifiedRate1
						.getPicihgCount()
						/ qualifiedRate1.getPiciCount());// 批次率
				// 交付及时率数据
				qualifiedRate1.setJiaofuCount(0F);// 交付总次数
				qualifiedRate1.setZhunshijiaofuCount(0F);// 准时交付次数
				qualifiedRate1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				qualifiedRate1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				qualifiedRate1.setShenqinCount(0F);// 申请数量（要货数量）
				qualifiedRate1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(qualifiedRate1.getPiciQualifiedrate());
				zhUser.setHegelv(qualifiedRate1.getQualifiedrate());
				totalDao.save(qualifiedRate1);
			}
			totalDao.update(zhUser);
			String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());// 当前时间
			String year = nowDate.substring(0, 4);// 获取年份
			// 计算日合格率
			String hql_Day = "from DayHege where day =? and gysid = ?";
			String yearmouthday = nowDate.substring(0, 10);// 获取当前年月日
			DayHege dayHege = (DayHege) totalDao.getObjectByCondition(hql_Day,
					yearmouthday, wddOld.getGysId());
			if (dayHege != null) {// 是否之前有记录
				dayHege
						.setBhgCount(dayHege.getBhgCount()
								+ buhege.floatValue());// 不合格数量
				dayHege.setShCount(dayHege.getShCount() + wddOld.getQrNumber());// 总数量
				dayHege.setCheckNopass(dayHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				dayHege.setQualifiedrate(1 - (dayHege.getBhgCount() / dayHege
						.getShCount()));// 合格率
				dayHege.setPiciCount(dayHege.getPiciCount() + 1F);// 总批次
				dayHege.setPicihgCount(dayHege.getPicihgCount() + 1F);// 合格批次
				dayHege.setPiciQualifiedrate(dayHege.getPicihgCount()
						/ dayHege.getPiciCount());// 批次率
				totalDao.update(dayHege);
			} else {
				DayHege dayHege1 = new DayHege();
				dayHege1.setDay(yearmouthday);
				dayHege1.setGysid(wddOld.getGysId());// 供应商名字
				dayHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				dayHege1.setBhgCount(buhege.floatValue());// 不合格数量
				dayHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				dayHege1
						.setQualifiedrate(1 - (dayHege1.getBhgCount() / dayHege1
								.getShCount()));// 合格率
				// 不良品
				dayHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				dayHege1.setNoProduce(0F);
				// 批次合格率数据
				dayHege1.setPiciCount(1F);// 总批次
				dayHege1.setPicihgCount(1F);// 合格批次
				dayHege1.setPiciQualifiedrate(dayHege1.getPicihgCount()
						/ dayHege1.getPiciCount());// 批次率
				// 交付及时率数据
				dayHege1.setJiaofuCount(0F);// 交付总次数
				dayHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				dayHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				dayHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				dayHege1.setShenqinCount(0F);// 申请数量（要货数量）
				dayHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(dayHege1.getPiciQualifiedrate());
				zhUser.setHegelv(dayHege1.getQualifiedrate());
				totalDao.save(dayHege1);
			}
			String hql_week = "from ZhouHege where week =? and gysid = ?";
			String week = year + "-" + Integer.toString(Util.getNowWeek())
					+ "周";// 获取年周数
			ZhouHege zhouHege = (ZhouHege) totalDao.getObjectByCondition(
					hql_week, week, wddOld.getGysId());
			if (zhouHege != null) {// 是否之前有记录
				zhouHege.setBhgCount(zhouHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				zhouHege.setShCount(zhouHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				zhouHege.setCheckNopass(zhouHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				zhouHege
						.setQualifiedrate(1 - (zhouHege.getBhgCount() / zhouHege
								.getShCount()));// 合格率
				zhouHege.setPiciCount(zhouHege.getPiciCount() + 1F);// 总批次
				zhouHege.setPicihgCount(zhouHege.getPicihgCount() + 1F);// 合格批次
				zhouHege.setPiciQualifiedrate(zhouHege.getPicihgCount()
						/ zhouHege.getPiciCount());// 批次率
				totalDao.update(zhouHege);
			} else {
				ZhouHege zhouHege1 = new ZhouHege();
				zhouHege1.setWeek(week);
				zhouHege1.setGysid(wddOld.getGysId());// 供应商名字
				zhouHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				zhouHege1.setBhgCount(buhege.floatValue());// 不合格数量
				zhouHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				zhouHege1
						.setQualifiedrate(1 - (zhouHege1.getBhgCount() / zhouHege1
								.getShCount()));// 合格率
				// 不良品
				zhouHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				zhouHege1.setNoProduce(0F);
				// 批次合格率数据
				zhouHege1.setPiciCount(1F);// 总批次
				zhouHege1.setPicihgCount(1F);// 合格批次
				zhouHege1.setPiciQualifiedrate(zhouHege1.getPicihgCount()
						/ zhouHege1.getPiciCount());// 批次率
				// 交付及时率数据
				zhouHege1.setJiaofuCount(0F);// 交付总次数
				zhouHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				zhouHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				zhouHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				zhouHege1.setShenqinCount(0F);// 申请数量（要货数量）
				zhouHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(zhouHege1.getPiciQualifiedrate());
				zhUser.setHegelv(zhouHege1.getQualifiedrate());
				totalDao.save(zhouHege1);
			}
			String hql_mouth = "from MouthHege where mouth =? and gysid = ?";
			String mouth1 = nowDate.substring(0, 7);// 获取年月份
			MouthHege mouthHege = (MouthHege) totalDao.getObjectByCondition(
					hql_mouth, mouth1, wddOld.getGysId());
			if (mouthHege != null) {// 是否之前有记录
				mouthHege.setBhgCount(mouthHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				mouthHege.setShCount(mouthHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				mouthHege.setCheckNopass(mouthHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				mouthHege
						.setQualifiedrate(1 - (mouthHege.getBhgCount() / mouthHege
								.getShCount()));// 合格率
				mouthHege.setPiciCount(mouthHege.getPiciCount() + 1F);// 总批次
				mouthHege.setPicihgCount(mouthHege.getPicihgCount() + 1F);// 合格批次
				mouthHege.setPiciQualifiedrate(mouthHege.getPicihgCount()
						/ mouthHege.getPiciCount());// 批次率
				totalDao.update(mouthHege);
			} else {
				MouthHege mouthHege1 = new MouthHege();
				mouthHege1.setMouth(mouth1);
				mouthHege1.setGysid(wddOld.getGysId());// 供应商名字
				mouthHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				mouthHege1.setBhgCount(buhege.floatValue());// 不合格数量
				mouthHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				mouthHege1
						.setQualifiedrate(1 - (mouthHege1.getBhgCount() / mouthHege1
								.getShCount()));// 合格率
				// 不良品
				mouthHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				mouthHege1.setNoProduce(0F);
				// 批次合格率数据
				mouthHege1.setPiciCount(1F);// 总批次
				mouthHege1.setPicihgCount(1F);// 合格批次
				mouthHege1.setPiciQualifiedrate(mouthHege1.getPicihgCount()
						/ mouthHege1.getPiciCount());// 批次率
				// 交付及时率数据
				mouthHege1.setJiaofuCount(0F);// 交付总次数
				mouthHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				mouthHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				mouthHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				mouthHege1.setShenqinCount(0F);// 申请数量（要货数量）
				mouthHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(mouthHege1.getPiciQualifiedrate());
				zhUser.setHegelv(mouthHege1.getQualifiedrate());
				totalDao.save(mouthHege1);
			}
			String quarter = "";// 计算季度
			String mouthStr = nowDate.substring(5, 7);
			if (Integer.parseInt(mouthStr) >= 1
					&& Integer.parseInt(mouthStr) <= 3) {
				quarter = year + "年1季";
			} else if (Integer.parseInt(mouthStr) >= 4
					&& Integer.parseInt(mouthStr) <= 6) {
				quarter = year + "年2季";
			} else if (Integer.parseInt(mouthStr) >= 7
					&& Integer.parseInt(mouthStr) <= 9) {
				quarter = year + "年3季";
			} else if (Integer.parseInt(mouthStr) >= 10
					&& Integer.parseInt(mouthStr) <= 12) {
				quarter = year + "年4季";
			}
			String hql_quarter = "from QuarterHege where quarter =? and gysid = ?";
			QuarterHege quarterHege = (QuarterHege) totalDao
					.getObjectByCondition(hql_quarter, quarter, wddOld
							.getGysId());
			if (quarterHege != null) {// 是否之前有记录
				quarterHege.setBhgCount(quarterHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				quarterHege.setShCount(quarterHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				quarterHege.setCheckNopass(quarterHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				quarterHege
						.setQualifiedrate(1 - (quarterHege.getBhgCount() / quarterHege
								.getShCount()));// 合格率
				quarterHege.setPiciCount(quarterHege.getPiciCount() + 1F);// 总批次
				quarterHege.setPicihgCount(quarterHege.getPicihgCount() + 1F);// 合格批次
				quarterHege.setPiciQualifiedrate(quarterHege.getPicihgCount()
						/ quarterHege.getPiciCount());// 批次率
				totalDao.update(quarterHege);
			} else {
				QuarterHege quarterHege1 = new QuarterHege();
				quarterHege1.setQuarter(quarter);
				quarterHege1.setGysid(wddOld.getGysId());// 供应商名字
				quarterHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				quarterHege1.setBhgCount(buhege.floatValue());// 不合格数量
				quarterHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				quarterHege1
						.setQualifiedrate(1 - (quarterHege1.getBhgCount() / quarterHege1
								.getShCount()));// 合格率
				// 不良品
				quarterHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				quarterHege1.setNoProduce(0F);
				// 批次合格率数据
				quarterHege1.setPiciCount(1F);// 总批次
				quarterHege1.setPicihgCount(1F);// 合格批次
				quarterHege1.setPiciQualifiedrate(quarterHege1.getPicihgCount()
						/ quarterHege1.getPiciCount());// 批次率
				// 交付及时率数据
				quarterHege1.setJiaofuCount(0F);// 交付总次数
				quarterHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				quarterHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				quarterHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				quarterHege1.setShenqinCount(0F);// 申请数量（要货数量）
				quarterHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(quarterHege1.getPiciQualifiedrate());
				zhUser.setHegelv(quarterHege1.getQualifiedrate());
				totalDao.save(quarterHege1);
			}
			String hql_year = "from YearHege where year =? and gysid = ?";
			YearHege yearHege = (YearHege) totalDao.getObjectByCondition(
					hql_year, year, wddOld.getGysId());
			if (yearHege != null) {// 是否之前有记录
				yearHege.setBhgCount(yearHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				yearHege.setShCount(yearHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				yearHege.setCheckNopass(yearHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				yearHege
						.setQualifiedrate(1 - (yearHege.getBhgCount() / yearHege
								.getShCount()));// 合格率
				yearHege.setPiciCount(yearHege.getPiciCount() + 1F);// 总批次
				yearHege.setPicihgCount(yearHege.getPicihgCount() + 1F);// 合格批次
				yearHege.setPiciQualifiedrate(yearHege.getPicihgCount()
						/ yearHege.getPiciCount());// 批次率
				totalDao.update(yearHege);
			} else {
				YearHege yearHege1 = new YearHege();
				yearHege1.setYear(year);
				yearHege1.setGysid(wddOld.getGysId());// 供应商名字
				yearHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				yearHege1.setBhgCount(buhege.floatValue());// 不合格数量
				yearHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				yearHege1
						.setQualifiedrate(1 - (yearHege1.getBhgCount() / yearHege1
								.getShCount()));// 合格率
				// 不良品
				yearHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				yearHege1.setNoProduce(0F);
				// 批次合格率数据
				yearHege1.setPiciCount(1F);// 总批次
				yearHege1.setPicihgCount(1F);// 合格批次
				yearHege1.setPiciQualifiedrate(yearHege1.getPicihgCount()
						/ yearHege1.getPiciCount());// 批次率
				// 交付及时率数据
				yearHege1.setJiaofuCount(0F);// 交付总次数
				yearHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				yearHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				yearHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				yearHege1.setShenqinCount(0F);// 申请数量（要货数量）
				yearHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(yearHege1.getPiciQualifiedrate());
				zhUser.setHegelv(yearHege1.getQualifiedrate());
				totalDao.save(yearHege1);
			}
		}
	}

	/*
	 * 检验合格后日周年月合格率
	 */
	public void jisuanBeHege(WaigouDeliveryDetail wddOld, Integer buhege) {
		if (wddOld != null) {
			QualifiedRate qualifiedRate = (QualifiedRate) totalDao
					.getObjectByCondition("From QualifiedRate where gysid =? ",
							wddOld.getGysId());
			ZhUser zhUser = (ZhUser) totalDao.get(ZhUser.class, wddOld
					.getGysId());
			if (qualifiedRate != null) {// 是否之前有记录
				qualifiedRate.setBhgCount(qualifiedRate.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				qualifiedRate.setShCount(qualifiedRate.getShCount()
						+ wddOld.getQrNumber());// 总数量
				qualifiedRate.setCheckNopass(qualifiedRate.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				qualifiedRate
						.setQualifiedrate(1 - (qualifiedRate.getBhgCount() / qualifiedRate
								.getShCount()));// 合格率
				qualifiedRate.setPiciCount(qualifiedRate.getPiciCount() + 1F);// 总批次
				qualifiedRate.setPicihgCount(qualifiedRate.getPicihgCount());// 合格批次
				qualifiedRate.setPiciQualifiedrate(qualifiedRate
						.getPicihgCount()
						/ qualifiedRate.getPiciCount());// 批次率
				zhUser.setZhiliang(qualifiedRate.getPiciQualifiedrate());
				zhUser.setHegelv(qualifiedRate.getQualifiedrate());
				totalDao.update(qualifiedRate);
			} else {
				QualifiedRate qualifiedRate1 = new QualifiedRate();
				qualifiedRate1.setGysid(wddOld.getGysId());// 供应商名字
				qualifiedRate1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				qualifiedRate1.setBhgCount(buhege.floatValue());// 不合格数量
				qualifiedRate1.setShCount(wddOld.getQrNumber());// 总送货数量
				qualifiedRate1.setQualifiedrate(1 - (qualifiedRate1
						.getBhgCount() / qualifiedRate1.getShCount()));// 合格率
				// 不良品
				qualifiedRate1.setCheckNopass(buhege.floatValue());// 检验不合格
				qualifiedRate1.setNoProduce(0F);
				// 批次合格率数据
				qualifiedRate1.setPiciCount(1F);// 总批次
				qualifiedRate1.setPicihgCount(0F);// 合格批次
				qualifiedRate1.setPiciQualifiedrate(qualifiedRate1
						.getPicihgCount()
						/ qualifiedRate1.getPiciCount());// 批次率
				// 交付及时率数据
				qualifiedRate1.setJiaofuCount(0F);// 交付总次数
				qualifiedRate1.setZhunshijiaofuCount(0F);// 准时交付次数
				qualifiedRate1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				qualifiedRate1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				qualifiedRate1.setShenqinCount(0F);// 申请数量（要货数量）
				qualifiedRate1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(qualifiedRate1.getPiciQualifiedrate());
				zhUser.setHegelv(qualifiedRate1.getQualifiedrate());
				totalDao.save(qualifiedRate1);
			}
			totalDao.update(zhUser);
			String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());// 当前时间
			String year = nowDate.substring(0, 4);// 获取年份
			// 计算日合格率
			String hql_Day = "from DayHege where day =? and gysid = ?";
			String yearmouthday = nowDate.substring(0, 10);// 获取当前年月日
			DayHege dayHege = (DayHege) totalDao.getObjectByCondition(hql_Day,
					yearmouthday, wddOld.getGysId());
			if (dayHege != null) {// 是否之前有记录
				dayHege
						.setBhgCount(dayHege.getBhgCount()
								+ buhege.floatValue());// 不合格数量
				dayHege.setShCount(dayHege.getShCount() + wddOld.getQrNumber());// 总数量
				dayHege.setCheckNopass(dayHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				dayHege.setQualifiedrate(1 - (dayHege.getBhgCount() / dayHege
						.getShCount()));// 合格率
				dayHege.setPiciCount(dayHege.getPiciCount() + 1F);// 总批次
				dayHege.setPicihgCount(dayHege.getPicihgCount());// 合格批次
				dayHege.setPiciQualifiedrate(dayHege.getPicihgCount()
						/ dayHege.getPiciCount());// 批次率
				totalDao.update(dayHege);
			} else {
				DayHege dayHege1 = new DayHege();
				dayHege1.setDay(yearmouthday);
				dayHege1.setGysid(wddOld.getGysId());// 供应商名字
				dayHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				dayHege1.setBhgCount(buhege.floatValue());// 不合格数量
				dayHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				dayHege1
						.setQualifiedrate(1 - (dayHege1.getBhgCount() / dayHege1
								.getShCount()));// 合格率
				// 不良品
				dayHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				dayHege1.setNoProduce(0F);
				// 批次合格率数据
				dayHege1.setPiciCount(1F);// 总批次
				dayHege1.setPicihgCount(0F);// 合格批次
				dayHege1.setPiciQualifiedrate(dayHege1.getPicihgCount()
						/ dayHege1.getPiciCount());// 批次率
				// 交付及时率数据
				dayHege1.setJiaofuCount(0F);// 交付总次数
				dayHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				dayHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				dayHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				dayHege1.setShenqinCount(0F);// 申请数量（要货数量）
				dayHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(dayHege1.getPiciQualifiedrate());
				zhUser.setHegelv(dayHege1.getQualifiedrate());
				totalDao.save(dayHege1);
			}
			String hql_week = "from ZhouHege where week =? and gysid = ?";
			String week = year + "-" + Integer.toString(Util.getNowWeek())
					+ "周";// 获取年周数
			ZhouHege zhouHege = (ZhouHege) totalDao.getObjectByCondition(
					hql_week, week, wddOld.getGysId());
			if (zhouHege != null) {// 是否之前有记录
				zhouHege.setBhgCount(zhouHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				zhouHege.setShCount(zhouHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				zhouHege.setCheckNopass(zhouHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				zhouHege
						.setQualifiedrate(1 - (zhouHege.getBhgCount() / zhouHege
								.getShCount()));// 合格率
				zhouHege.setPiciCount(zhouHege.getPiciCount() + 1F);// 总批次
				zhouHege.setPicihgCount(zhouHege.getPicihgCount());// 合格批次
				zhouHege.setPiciQualifiedrate(zhouHege.getPicihgCount()
						/ zhouHege.getPiciCount());// 批次率
				totalDao.update(zhouHege);
			} else {
				ZhouHege zhouHege1 = new ZhouHege();
				zhouHege1.setWeek(week);
				zhouHege1.setGysid(wddOld.getGysId());// 供应商名字
				zhouHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				zhouHege1.setBhgCount(buhege.floatValue());// 不合格数量
				zhouHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				zhouHege1
						.setQualifiedrate(1 - (zhouHege1.getBhgCount() / zhouHege1
								.getShCount()));// 合格率
				// 不良品
				zhouHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				zhouHege1.setNoProduce(0F);
				// 批次合格率数据
				zhouHege1.setPiciCount(1F);// 总批次
				zhouHege1.setPicihgCount(0F);// 合格批次
				zhouHege1.setPiciQualifiedrate(zhouHege1.getPicihgCount()
						/ zhouHege1.getPiciCount());// 批次率
				// 交付及时率数据
				zhouHege1.setJiaofuCount(0F);// 交付总次数
				zhouHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				zhouHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				zhouHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				zhouHege1.setShenqinCount(0F);// 申请数量（要货数量）
				zhouHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(zhouHege1.getPiciQualifiedrate());
				zhUser.setHegelv(zhouHege1.getQualifiedrate());
				totalDao.save(zhouHege1);
			}
			String hql_mouth = "from MouthHege where mouth =? and gysid = ?";
			String mouth1 = nowDate.substring(0, 7);// 获取年月份
			MouthHege mouthHege = (MouthHege) totalDao.getObjectByCondition(
					hql_mouth, mouth1, wddOld.getGysId());
			if (mouthHege != null) {// 是否之前有记录
				mouthHege.setBhgCount(mouthHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				mouthHege.setShCount(mouthHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				mouthHege.setCheckNopass(mouthHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				mouthHege
						.setQualifiedrate(1 - (mouthHege.getBhgCount() / mouthHege
								.getShCount()));// 合格率
				mouthHege.setPiciCount(mouthHege.getPiciCount() + 1F);// 总批次
				mouthHege.setPicihgCount(mouthHege.getPicihgCount());// 合格批次
				mouthHege.setPiciQualifiedrate(mouthHege.getPicihgCount()
						/ mouthHege.getPiciCount());// 批次率
				totalDao.update(mouthHege);
			} else {
				MouthHege mouthHege1 = new MouthHege();
				mouthHege1.setMouth(mouth1);
				mouthHege1.setGysid(wddOld.getGysId());// 供应商名字
				mouthHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				mouthHege1.setBhgCount(buhege.floatValue());// 不合格数量
				mouthHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				mouthHege1
						.setQualifiedrate(1 - (mouthHege1.getBhgCount() / mouthHege1
								.getShCount()));// 合格率
				// 不良品
				mouthHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				mouthHege1.setNoProduce(0F);
				// 批次合格率数据
				mouthHege1.setPiciCount(1F);// 总批次
				mouthHege1.setPicihgCount(0F);// 合格批次
				mouthHege1.setPiciQualifiedrate(mouthHege1.getPicihgCount()
						/ mouthHege1.getPiciCount());// 批次率
				// 交付及时率数据
				mouthHege1.setJiaofuCount(0F);// 交付总次数
				mouthHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				mouthHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				mouthHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				mouthHege1.setShenqinCount(0F);// 申请数量（要货数量）
				mouthHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(mouthHege1.getPiciQualifiedrate());
				zhUser.setHegelv(mouthHege1.getQualifiedrate());
				totalDao.save(mouthHege1);
			}
			String quarter = "";// 计算季度
			String mouthStr = nowDate.substring(5, 7);
			if (Integer.parseInt(mouthStr) >= 1
					&& Integer.parseInt(mouthStr) <= 3) {
				quarter = year + "年1季";
			} else if (Integer.parseInt(mouthStr) >= 4
					&& Integer.parseInt(mouthStr) <= 6) {
				quarter = year + "年2季";
			} else if (Integer.parseInt(mouthStr) >= 7
					&& Integer.parseInt(mouthStr) <= 9) {
				quarter = year + "年3季";
			} else if (Integer.parseInt(mouthStr) >= 10
					&& Integer.parseInt(mouthStr) <= 12) {
				quarter = year + "年4季";
			}
			String hql_quarter = "from QuarterHege where quarter =? and gysid = ?";
			QuarterHege quarterHege = (QuarterHege) totalDao
					.getObjectByCondition(hql_quarter, quarter, wddOld
							.getGysId());
			if (quarterHege != null) {// 是否之前有记录
				quarterHege.setBhgCount(quarterHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				quarterHege.setShCount(quarterHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				quarterHege.setCheckNopass(quarterHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				quarterHege
						.setQualifiedrate(1 - (quarterHege.getBhgCount() / quarterHege
								.getShCount()));// 合格率
				quarterHege.setPiciCount(quarterHege.getPiciCount() + 1F);// 总批次
				quarterHege.setPicihgCount(quarterHege.getPicihgCount());// 合格批次
				quarterHege.setPiciQualifiedrate(quarterHege.getPicihgCount()
						/ quarterHege.getPiciCount());// 批次率
				totalDao.update(quarterHege);
			} else {
				QuarterHege quarterHege1 = new QuarterHege();
				quarterHege1.setQuarter(quarter);
				quarterHege1.setGysid(wddOld.getGysId());// 供应商名字
				quarterHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				quarterHege1.setBhgCount(buhege.floatValue());// 不合格数量
				quarterHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				quarterHege1
						.setQualifiedrate(1 - (quarterHege1.getBhgCount() / quarterHege1
								.getShCount()));// 合格率
				// 不良品
				quarterHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				quarterHege1.setNoProduce(0F);
				// 批次合格率数据
				quarterHege1.setPiciCount(1F);// 总批次
				quarterHege1.setPicihgCount(0F);// 合格批次
				quarterHege1.setPiciQualifiedrate(quarterHege1.getPicihgCount()
						/ quarterHege1.getPiciCount());// 批次率
				// 交付及时率数据
				quarterHege1.setJiaofuCount(0F);// 交付总次数
				quarterHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				quarterHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				quarterHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				quarterHege1.setShenqinCount(0F);// 申请数量（要货数量）
				quarterHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(quarterHege1.getPiciQualifiedrate());
				zhUser.setHegelv(quarterHege1.getQualifiedrate());
				totalDao.save(quarterHege1);
			}
			String hql_year = "from YearHege where year =? and gysid = ?";
			YearHege yearHege = (YearHege) totalDao.getObjectByCondition(
					hql_year, year, wddOld.getGysId());
			if (yearHege != null) {// 是否之前有记录
				yearHege.setBhgCount(yearHege.getBhgCount()
						+ buhege.floatValue());// 不合格数量
				yearHege.setShCount(yearHege.getShCount()
						+ wddOld.getQrNumber());// 总数量
				yearHege.setCheckNopass(yearHege.getCheckNopass()
						+ buhege.floatValue());// 检验不合格数
				yearHege
						.setQualifiedrate(1 - (yearHege.getBhgCount() / yearHege
								.getShCount()));// 合格率
				yearHege.setPiciCount(yearHege.getPiciCount() + 1F);// 总批次
				yearHege.setPicihgCount(yearHege.getPicihgCount());// 合格批次
				yearHege.setPiciQualifiedrate(yearHege.getPicihgCount()
						/ yearHege.getPiciCount());// 批次率
				totalDao.update(yearHege);
			} else {
				YearHege yearHege1 = new YearHege();
				yearHege1.setYear(year);
				yearHege1.setGysid(wddOld.getGysId());// 供应商名字
				yearHege1.setGysname(wddOld.getGysName());// 供应商名字
				// 总合格率数据
				yearHege1.setBhgCount(buhege.floatValue());// 不合格数量
				yearHege1.setShCount(wddOld.getQrNumber());// 总送货数量
				yearHege1
						.setQualifiedrate(1 - (yearHege1.getBhgCount() / yearHege1
								.getShCount()));// 合格率
				// 不良品
				yearHege1.setCheckNopass(buhege.floatValue());// 检验不合格
				yearHege1.setNoProduce(0F);
				// 批次合格率数据
				yearHege1.setPiciCount(1F);// 总批次
				yearHege1.setPicihgCount(0F);// 合格批次
				yearHege1.setPiciQualifiedrate(yearHege1.getPicihgCount()
						/ yearHege1.getPiciCount());// 批次率
				// 交付及时率数据
				yearHege1.setJiaofuCount(0F);// 交付总次数
				yearHege1.setZhunshijiaofuCount(0F);// 准时交付次数
				yearHege1.setJiaofuhege(0F);// 交付合格率
				// 缺件率数据
				yearHege1.setShijiyunsongCount(0F);// 实际送货数量=总送货数量
				yearHege1.setShenqinCount(0F);// 申请数量（要货数量）
				yearHege1.setQuejianrate(0F);// 缺件率
				zhUser.setZhiliang(yearHege1.getPiciQualifiedrate());
				zhUser.setHegelv(yearHege1.getQualifiedrate());
				totalDao.save(yearHege1);
			}
		}

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

	@Override
	public Object[] findWwRukuDailingList(WaigouDeliveryDetail waigoudd,
			int pageNo, int pageSize, String pageStatus) {
		if (waigoudd == null) {
			waigoudd = new WaigouDeliveryDetail();
		}
		String hql = totalDao.criteriaQueries(waigoudd, null, "rootMarkId",
				"rootSlfCard", "selfCard", "ywmarkId", "neiorderNum");
		String addhql = "";
		String addhql2 = "";
		if (waigoudd.getRootMarkId() != null
				&& waigoudd.getRootMarkId().length() > 0
				&& !waigoudd.getRootMarkId().contains("'")) {
			addhql = " where markId like '%" + waigoudd.getRootMarkId() + "%'";
		}
		if (waigoudd.getRootSlfCard() != null
				&& waigoudd.getRootSlfCard().length() > 0
				&& !waigoudd.getRootSlfCard().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where selfCard like '%" + waigoudd.getRootSlfCard()
						+ "%'";
			} else {
				addhql += " and selfCard like '%" + waigoudd.getRootSlfCard()
						+ "%'";
			}
		}
		if (waigoudd.getNeiorderNum() != null
				&& waigoudd.getNeiorderNum().length() > 0
				&& !waigoudd.getNeiorderNum().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where orderNumber like '%"
						+ waigoudd.getNeiorderNum() + "%'";
			} else {
				addhql += " and orderNumber like '%"
						+ waigoudd.getNeiorderNum() + "%'";
			}
		}
		if (waigoudd.getYwmarkId() != null
				&& waigoudd.getYwmarkId().length() > 0
				&& !waigoudd.getYwmarkId().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where ywMarkId like '%" + waigoudd.getYwmarkId()
						+ "%'";
			} else {
				addhql += " and ywMarkId like '%" + waigoudd.getYwmarkId()
						+ "%'";
			}
		}

		if (waigoudd.getSelfCard() != null
				&& waigoudd.getSelfCard().length() > 0
				&& !waigoudd.getSelfCard().contains("'")) {
			hql += " and waigouPlanId in(select wgOrderId from  ProcardWGCenter where procardId in(select id from Procard where selfCard like '%"
					+ waigoudd.getSelfCard() + "%'))";
		}
		if (addhql.length() > 0) {
			addhql2 = " and waigouPlanId in(select wgOrderId from  ProcardWGCenter where procardId in(select id from Procard where rootId in(select id  from Procard "
					+ addhql + " )))";
		}
		hql += addhql2
				+ " and waigouPlanId in (select id from WaigouPlan where wwType is not null and wwType in('工序外委','包工包料')) and status <> '已领'";
		// 当前供应商所遇的送货单
		// List<WaigouDeliveryDetail> list = totalDao.findAllByPage(hql
		// + " order by id desc", pageNo, pageSize);
		List<WaigouDeliveryDetail> list = totalDao.query(hql
				+ " order by id desc");
		List<WaigouDeliveryDetail> returnList = new ArrayList<WaigouDeliveryDetail>();
		if (list != null && list.size() > 0) {
			for (WaigouDeliveryDetail detail : list) {
				GoodsStore gs = (GoodsStore) totalDao
						.getObjectByCondition(
								"from GoodsStore where wwddId=? and goodsStoreWarehouse='外协库' ",
								detail.getId());
				String cqkwSql = " and 1=1";
				if (gs != null) {
					if (gs.getGoodHouseName() == null
							|| gs.getGoodHouseName().length() == 0) {
						cqkwSql += " and (goodHouseName is null or goodHouseName ='')";
					} else {
						cqkwSql += " and goodHouseName  ='"
								+ gs.getGoodHouseName() + "'";
					}
					if (gs.getGoodsStorePosition() == null
							|| gs.getGoodsStorePosition().length() == 0) {
						cqkwSql += " and (goodsPosition is null or goodsPosition ='')";
					} else {
						cqkwSql += " and goodsPosition  ='"
								+ gs.getGoodsStorePosition() + "'";
					}
					String hql_goods = " from Goods where goodsMarkId = ? and goodsLotId=? and processNo=? and goodsClass in ('外协库') "
							+ cqkwSql;
					Goods good = (Goods) totalDao.getObjectByCondition(
							hql_goods, gs.getGoodsStoreMarkId(), gs
									.getGoodsStoreLot(), gs.getProcessNo());
					if (good != null) {
						if (good.getGoodsCurQuantity() <= 0) {
							continue;
						}
						detail.setChangqu(good.getGoodHouseName());
						detail.setKuwei(good.getGoodsPosition());
						detail.setKcCount(good.getGoodsCurQuantity());
					}
				}
				List<Procard> procardList = totalDao
						.query(
								"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?)",
								detail.getWaigouPlanId());
				if (procardList != null && procardList.size() > 0) {
					for (Procard procard : procardList) {
						if (procard != null) {
							if (detail.getNeiorderNum() == null) {
								detail.setNeiorderNum(procard.getOrderNumber());// 内部订单号
								detail
										.setRootSlfCard(procard
												.getRootSelfCard());// 总成批次
								detail.setRootMarkId(procard.getRootMarkId());
								detail.setSelfCard(procard.getSelfCard());
								detail.setYwmarkId(procard.getYwMarkId());
								detail.setFilnalCount(procard.getFilnalCount());
							} else {
								if (procard.getOrderNumber() != null) {
									if (!detail.getNeiorderNum().contains(
											procard.getOrderNumber())) {
										detail.setNeiorderNum(detail
												.getNeiorderNum()
												+ "、"
												+ procard.getOrderNumber());// 内部订单号
									}
								}
								if (!detail.getRootSlfCard().contains(
										procard.getRootSelfCard())) {
									detail.setRootSlfCard(detail
											.getRootSlfCard()
											+ "、" + procard.getRootSelfCard());// 总成批次
								}
								if (!detail.getRootMarkId().contains(
										procard.getRootMarkId())) {
									detail.setRootMarkId(detail.getRootMarkId()
											+ "、" + procard.getRootMarkId());
								}
								if (!detail.getSelfCard().contains(
										procard.getSelfCard())) {
									detail.setSelfCard(detail.getSelfCard()
											+ "、" + procard.getSelfCard());
								}
								if (!detail.getYwmarkId().contains(
										procard.getYwMarkId())) {
									detail.setYwmarkId(detail.getYwmarkId()
											+ "、" + procard.getYwMarkId());
								}
								detail.setFilnalCount(detail.getFilnalCount()
										+ procard.getFilnalCount());
							}
						}
					}
				}
				returnList.add(detail);
			}
		}
		Double sumMoney = 0d;
		Float sumNum = 0f;
		Double sumbhsprice = 0d;
		Float yrukuNum = 0f;
		Float kcCount = 0f;
		// int count = totalDao.getCount(hql);
		int count = 0;
		if (returnList != null && returnList.size() > 0) {
			count = returnList.size();
			for (WaigouDeliveryDetail w : returnList) {
				if (w.getFilnalCount() != null) {
					sumMoney += w.getFilnalCount();
				}
				if (w.getQrNumber() != null) {
					sumNum += w.getQrNumber();
				}
				if (w.getHgNumber() != null) {
					sumbhsprice += w.getHgNumber();
				}
				if (w.getYrukuNum() != null) {
					yrukuNum += w.getYrukuNum();
				}
				if (w.getKcCount() != null) {
					kcCount += w.getKcCount();
				}

			}
		}
		Object[] o = { list, count, sumMoney, sumNum, sumbhsprice, yrukuNum,
				kcCount };
		return o;
	}

	@Override
	public Object[] findWwRukuDailingList2(WaigouDeliveryDetail waigoudd,
			int pageNo, int pageSize, String pageStatus) {
		// 生成页面hql
		if (waigoudd == null) {
			waigoudd = new WaigouDeliveryDetail();
		}
		String hql = totalDao.criteriaQueries(waigoudd, null, "rootMarkId",
				"rootSlfCard", "selfCard", "ywmarkId", "neiorderNum");
		String addhql = "";
		String addhql2 = "";
		if (waigoudd.getRootMarkId() != null
				&& waigoudd.getRootMarkId().length() > 0
				&& !waigoudd.getRootMarkId().contains("'")) {
			addhql = " where markId like '%" + waigoudd.getRootMarkId() + "%'";
		}
		if (waigoudd.getRootSlfCard() != null
				&& waigoudd.getRootSlfCard().length() > 0
				&& !waigoudd.getRootSlfCard().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where selfCard like '%" + waigoudd.getRootSlfCard()
						+ "%'";
			} else {
				addhql += " and selfCard like '%" + waigoudd.getRootSlfCard()
						+ "%'";
			}
		}
		if (waigoudd.getNeiorderNum() != null
				&& waigoudd.getNeiorderNum().length() > 0
				&& !waigoudd.getNeiorderNum().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where orderNumber like '%"
						+ waigoudd.getNeiorderNum() + "%'";
			} else {
				addhql += " and orderNumber like '%"
						+ waigoudd.getNeiorderNum() + "%'";
			}
		}
		if (waigoudd.getYwmarkId() != null
				&& waigoudd.getYwmarkId().length() > 0
				&& !waigoudd.getYwmarkId().contains("'")) {
			if (addhql.length() == 0) {
				addhql = " where ywMarkId like '%" + waigoudd.getYwmarkId()
						+ "%'";
			} else {
				addhql += " and ywMarkId like '%" + waigoudd.getYwmarkId()
						+ "%'";
			}
		}

		if (waigoudd.getSelfCard() != null
				&& waigoudd.getSelfCard().length() > 0
				&& !waigoudd.getSelfCard().contains("'")) {
			hql += " and waigouPlanId in(select wgOrderId from  ProcardWGCenter where procardId in(select id from Procard where selfCard like '%"
					+ waigoudd.getSelfCard() + "%'))";
		}
		if (addhql.length() > 0) {
			addhql2 = " and waigouPlanId in(select wgOrderId from  ProcardWGCenter where procardId in(select id from Procard where rootId in(select id  from Procard "
					+ addhql + " )))";
		}
		hql += addhql2
				+ " and waigouPlanId in (select id from WaigouPlan where wwType is not null and wwType in('工序外委','包工包料')) and status <> '已领'";
		// 页面传值hql拼接结束

		List<Goods> goodsList = totalDao
				.query("from Goods where goodsClass='外协库' and goodsCurQuantity>0 and processNo is not null ");
		Double sumMoney = 0d;
		Float sumNum = 0f;
		Double sumbhsprice = 0d;
		Float yrukuNum = 0f;
		Float goodsCount = 0f;
		Float count = 0f;
		StringBuffer sb = new StringBuffer();
		for (Goods wxGoods : goodsList) {
			goodsCount += wxGoods.getGoodsCurQuantity();
			count++;
			String cqkwSql = " and 1=1";
			if (wxGoods.getGoodHouseName() == null
					|| wxGoods.getGoodHouseName().length() == 0) {
				cqkwSql += " and (goodHouseName is null or goodHouseName ='')";
			} else {
				cqkwSql += " and goodHouseName  ='"
						+ wxGoods.getGoodHouseName() + "'";
			}
			if (wxGoods.getGoodsPosition() == null
					|| wxGoods.getGoodsPosition().length() == 0) {
				cqkwSql += " and (goodsStorePosition is null or goodsStorePosition ='')";
			} else {
				cqkwSql += " and goodsStorePosition  ='"
						+ wxGoods.getGoodsPosition() + "'";
			}
			String sql = "select wwddId from GoodsStore where goodsStoreMarkId=? and goodsStoreLot=? and processNo=? and goodsStoreWarehouse='外协库' "
					+ cqkwSql;
			Integer wwddId = (Integer) totalDao.getObjectByCondition(sql,
					wxGoods.getGoodsMarkId(), wxGoods.getGoodsLotId(), wxGoods
							.getProcessNo());
			if (wwddId != null) {
				wxGoods.setWddId(wwddId);
				if (sb.length() == 0) {
					sb.append(wwddId);
				} else {
					sb.append("," + wwddId);
				}
			}
		}
		if (sb.length() > 0) {
			List<WaigouDeliveryDetail> wdDtailList = totalDao.query(hql
					+ " and id in(" + sb.toString() + ")");
			if (wdDtailList != null && wdDtailList.size() > 0) {
				for (WaigouDeliveryDetail detail : wdDtailList) {
					List<Procard> procardList = totalDao
							.query(
									"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?)",
									detail.getWaigouPlanId());
					if (procardList != null && procardList.size() > 0) {
						for (Procard procard : procardList) {
							if (procard != null) {
								if (detail.getNeiorderNum() == null) {
									detail.setNeiorderNum(procard
											.getOrderNumber());// 内部订单号
									detail.setRootSlfCard(procard
											.getRootSelfCard());// 总成批次
									detail.setRootMarkId(procard
											.getRootMarkId());
									detail.setSelfCard(procard.getSelfCard());
									detail.setYwmarkId(procard.getYwMarkId());
									detail.setFilnalCount(procard
											.getFilnalCount());
								} else {
									if (procard.getOrderNumber() != null) {
										if (!detail.getNeiorderNum().contains(
												procard.getOrderNumber())) {
											detail.setNeiorderNum(detail
													.getNeiorderNum()
													+ "、"
													+ procard.getOrderNumber());// 内部订单号
										}
									}
									if(procard.getRootSelfCard()!=null){
										if(detail.getRootSlfCard()!=null){
											if (!detail.getRootSlfCard().contains(
													procard.getRootSelfCard())) {
												detail.setRootSlfCard(detail
														.getRootSlfCard()
														+ "、"
														+ procard.getRootSelfCard());// 总成批次
											}
										}else{
											detail.setRootSlfCard(procard.getRootSelfCard());// 总成批次
										}
									}
									if(procard.getRootMarkId()!=null){
										if(detail.getRootMarkId()!=null){
											if (!detail.getRootMarkId().contains(
													procard.getRootMarkId())) {
												detail
												.setRootMarkId(detail
														.getRootMarkId()
														+ "、"
														+ procard
														.getRootMarkId());
											}
										}else{
											detail
											.setRootMarkId(procard
													.getRootMarkId());
										}
									}
									if(detail.getSelfCard()!=null){
										if (!detail.getSelfCard().contains(
												procard.getSelfCard())) {
											detail.setSelfCard(detail.getSelfCard()
													+ "、" + procard.getSelfCard());
										}
									}else{
										detail.setSelfCard(procard.getSelfCard());
									}
									if(detail.getYwmarkId()!=null){
										if (!detail.getYwmarkId().contains(
												procard.getYwMarkId())) {
											detail.setYwmarkId(detail.getYwmarkId()
													+ "、" + procard.getYwMarkId());
										}
									}else{
										detail.setYwmarkId(procard.getYwMarkId());
									}
									detail.setFilnalCount(detail
											.getFilnalCount()
											+ procard.getFilnalCount());
								}
							}
						}
					}
					for (Goods wxGoods : goodsList) {
						if (wxGoods.getWddId() != null
								&& wxGoods.getWddId().equals(detail.getId())) {
							detail.setChangqu(wxGoods.getGoodHouseName());
							detail.setKuwei(wxGoods.getGoodsPosition());
							detail.setKcCount(wxGoods.getGoodsCurQuantity());
							break;
						}
					}
					if (detail.getFilnalCount() != null) {
						sumMoney += detail.getFilnalCount();
					}
					if (detail.getQrNumber() != null) {
						sumNum += detail.getQrNumber();
					}
					if (detail.getHgNumber() != null) {
						sumbhsprice += detail.getHgNumber();
					}
					if (detail.getYrukuNum() != null) {
						yrukuNum += detail.getYrukuNum();
					}
					if (detail.getKcCount() != null) {
						goodsCount += detail.getKcCount();
					}
				}
			}
			Object[] o = { wdDtailList, count, sumMoney, sumNum, sumbhsprice,
					yrukuNum, goodsCount };
			return o;
		}
		return null;
	}

	@Override
	public String adjust(Procard procard) {
		String hql_all = "select sum(cgNumber),sum(wwblCount),sum(filnalCount) "
				+ "from Procard where rootid=? and markId=? and kgliao=?"
				+ " and procardStyle='外购'  and cgNumber is not null and (sbStatus is null or sbStatus!='删除') "
				+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc";
		Object[] yuanCounts = (Object[]) totalDao.getObjectByCondition(hql_all,
				procard.getRootId(), procard.getMarkId(), procard.getKgliao());
		if (yuanCounts != null && yuanCounts[0] != null) {
			// Float yfilnalCount =Float.valueOf(yuanCounts[0].toString()) ;
			Float ycgNumber = Float.valueOf(yuanCounts[0].toString());
			Float ceCount = procard.getCgNumber() - ycgNumber;// 差额数量，新采购数量-原采购数量
			// 查询的结余库存
			if (ceCount < 0) {
				// 减少

				boolean candelete = false;// 是否能减少
				// 判断是否能减少

				Float wwblCount = 0F;
				if (yuanCounts[1] != null) {
					wwblCount = Float.valueOf(yuanCounts[1].toString());
				}
				Float filnalCount = Float.valueOf(yuanCounts[2].toString());
				// 需求呆滞量
				Float oldDzNumber = filnalCount - wwblCount
						- procard.getCgNumber();

				// 现有库存量(件号+版本+供料属性+库别)
				String goodsClassSql = null;
				// if (procard.getProductStyle() != null
				// && procard.getProductStyle().equals("试制")) {
				// // 试制的外购件去试制库取
				// goodsClassSql = " and goodsClass ='试制库'";
				// } else {
				String kgsql = "";
				if (procard.getKgliao() != null
						&& procard.getKgliao().length() > 0) {
					kgsql += " and kgliao ='" + procard.getKgliao() + "'";
				}
				goodsClassSql = " and goodsClass in ('外购件库') " + kgsql;
				// }
				String specification_sql = "";
				// if (procard.getSpecification() != null
				// && procard.getSpecification().length() > 0) {
				// specification_sql = " and specification = '"
				// + procard.getSpecification() + "'";
				// }
				String banben_hql = "";
				String banben_hql2 = "";
				if (procard.getBanBenNumber() != null
						&& procard.getBanBenNumber().length() > 0) {
					banben_hql = " and banBenNumber='"
							+ procard.getBanBenNumber() + "'";
					banben_hql2 = " and banben='" + procard.getBanBenNumber()
							+ "'";
				}
				String hqlGoods = "";
				hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
						+ goodsClassSql
						+ " and goodsCurQuantity>0 "
						+ banben_hql
						+ " and (fcStatus is null or fcStatus='可用')";
				Float kcCount = (Float) totalDao.getObjectByCondition(hqlGoods,
						procard.getMarkId());
				if (kcCount == null || kcCount < 0) {
					kcCount = 0f;
				}
				// 系统在途量(已生成物料需求信息，未到货)
				String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
						+ banben_hql2
						+ " and kgliao=? and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null)"
						+ specification_sql;
				Double ztCountd = (Double) totalDao.getObjectByCondition(
						hql_zc0, procard.getMarkId(), procard.getKgliao());
				if (ztCountd == null) {
					ztCountd = 0D;
				}
				Float ztCount = ztCountd.floatValue();

				// 系统占用量(已计算过采购量(1、有库存 2、采购中)，未领料)
				String zyCountSql = "select sum(hascount) from Procard where rootId <>? and markId=?  and kgliao=? "
						+ banben_hql
						+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and cgNumber is not null and (sbStatus is null or sbStatus!='删除')";
				Float zyCount = (Float) totalDao.getObjectByCondition(
						zyCountSql, procard.getRootId(), procard.getMarkId(),
						procard.getKgliao());
				if (zyCount == null || zyCount < 0) {
					zyCount = 0f;
				}
				// (库存量+在途量(已生成采购，未到货))-占用量=剩余可用库存量
				Float daizhiCount = (kcCount + ztCount) - zyCount;
				if (daizhiCount < 0) {
					daizhiCount = 0F;
				}
				if (oldDzNumber <= daizhiCount) {
					candelete = true;
				}

				if (candelete) {
					ceCount = 0 - ceCount;// 变为正数方便计算
					Float cecgCount = ceCount;// 记录采购减少额
					List<Procard> yProcardList = totalDao
							.query(
									"from Procard where rootId=? and markId=? and kgliao=? and cgNumber is not null "
											+ "and (outcgNumber is null or outcgNumber=0) and (sbStatus is null or sbStatus!='删除') order by cgNumber desc",
									procard.getRootId(), procard.getMarkId(),
									procard.getKgliao());
					if (yProcardList != null && yProcardList.size() > 0) {
						for (Procard p : yProcardList) {
							boolean isgoods = false;
							Float syTjNumber = p.getFilnalCount()
									- p.getTjNumber();
							if (ceCount > 0) {
								if (p.getZtzyNumber() == null) {
									p.setZtzyNumber(0F);
								}
								if (ceCount > syTjNumber) {
									// 新的采购量小于总需求量时说明增加了新的库存或在途 并且可用量大于等于需求量时
									if (procard.getCgNumber() < filnalCount) {
										if ((kcCount - zyCount) > 0) {
											p.setTjNumber(p.getFilnalCount());
											isgoods = true;
										} else {
											p.setZtzyNumber(p.getFilnalCount());
										}
									}
									ceCount -= syTjNumber;
								} else {
									if (procard.getCgNumber() < filnalCount) {
										if ((kcCount - zyCount) > 0) {
											p.setTjNumber(p.getTjNumber()
													+ ceCount);
										} else {
											p.setZtzyNumber(p.getZtzyNumber()
													+ ceCount);
										}
									}
									ceCount = 0f;
								}
							}
							if (cecgCount > 0) {
								if (cecgCount > p.getCgNumber()) {
									cecgCount -= p.getCgNumber();
									p.setCgNumber(0f);
								} else {
									p.setCgNumber(p.getCgNumber() - cecgCount);
									cecgCount = 0f;
								}
							}
							totalDao.update(p);
							if (isgoods) {
								// 有新的库存进入，激活上层外购件
								procardServer.jihuoProcard(p.getRootId(), p
										.getBelongLayer() - 1, "外购件物料需求减少，件号为:"
										+ p.getMarkId());
							}
							if (cecgCount == 0 && ceCount == 0) {
								break;
							}

						}
					} else {
						return "未找到对应零件数据,调整失败!";
					}

				} else {
					return "库存量(可用量)不足,无法减少采购数量,请重新核对库存数量或增加库存可用量!";
				}
			} else if (ceCount > 0) {// 增加
				Procard p = (Procard) totalDao
						.getObjectByCondition(
								"from Procard where rootId=? and markId=? and kgliao=? and cgNumber is not null and (outcgNumber is null or outcgNumber=0) and (sbStatus is null or sbStatus!='删除') order by id desc",
								procard.getRootId(), procard.getMarkId(),
								procard.getKgliao());
				p.setCgNumber(p.getCgNumber() + ceCount);
				totalDao.update(p);
			}

		} else {
			return "false";
		}
		return "true";
	}

	@Override
	public Procard findById(Integer procardId) {
		return (Procard) totalDao.get(Procard.class, procardId);
	}

	public boolean exportFile1(List procardList) {
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("待采购物料清单".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("待采购物料清单", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title);
			wcf_title.setBackground(jxl.format.Colour.WHITE);
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE);
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.setColumnView(29, 30);
			ws.addCell(new Label(0, 0, "待采购计划", wcf_title));
			ws.mergeCells(0, 0, 29, 0);
			WritableCellFormat titleFormat = new WritableCellFormat(wf_title);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置居中对齐
			titleFormat.setAlignment(jxl.format.Alignment.CENTRE);// 设置居中对齐（这俩哪个是上下/左右对齐也没验证过）
			ws.addCell(new Label(0, 1, "序号"));
			ws.addCell(new Label(1, 1, "价格状态"));
			ws.addCell(new Label(2, 1, "订单号"));
			ws.addCell(new Label(3, 1, "总成件号"));
			ws.addCell(new Label(4, 1, "业务件号"));
			ws.addCell(new Label(5, 1, "总成批次"));
			ws.addCell(new Label(6, 1, "物料类别"));
			ws.addCell(new Label(7, 1, "件号"));
			ws.addCell(new Label(8, 1, "规格"));
			ws.addCell(new Label(9, 1, "版本"));
			ws.addCell(new Label(10, 1, "批次"));
			ws.addCell(new Label(11, 1, "零件名称"));
			ws.addCell(new Label(12, 1, "供料属性"));
			ws.addCell(new Label(13, 1, "库存数量"));
			ws.addCell(new Label(14, 1, "占用数量"));
			ws.addCell(new Label(15, 1, "在途数量"));
			ws.addCell(new Label(16, 1, "呆滞数量"));
			ws.addCell(new Label(17, 1, "需求数量"));
			ws.addCell(new Label(18, 1, "采购数量"));
			ws.addCell(new Label(19, 1, "下单数量"));
			ws.addCell(new Label(20, 1, "到货数量"));
			ws.addCell(new Label(21, 1, "激活数量"));
			ws.addCell(new Label(22, 1, "领料数量"));
			ws.addCell(new Label(23, 1, "欠料数量"));
			ws.addCell(new Label(24, 1, "单位"));
			ws.addCell(new Label(25, 1, "序号"));
			ws.addCell(new Label(26, 1, "数量"));
			ws.addCell(new Label(27, 1, "到货数量"));
			ws.addCell(new Label(28, 1, "入库数量"));
			ws.addCell(new Label(29, 1, "产品状态"));
			ws.addCell(new Label(30, 1, "供应商"));
			ws.addCell(new Label(31, 1, "采购员"));
			ws.addCell(new Label(32, 1, "工序信息"));
			DecimalFormat df = new DecimalFormat("#.###");
			int count = 2;
			for (int i = 0; i < procardList.size(); i++) {
				Procard p = (Procard) procardList.get(i);
				List<WaigouPlan> waigouPlanList = p.getWaigouPlanList();
				if (waigouPlanList != null && waigouPlanList.size() > 0) {
					int fristrow = 0;
					for (int j = 0; j < waigouPlanList.size(); j++) {
						if (j == 0) {
							fristrow = count;
						}
						WaigouPlan w = waigouPlanList.get(j);
						ws.addCell(new Label(0, count, (i + 1) + ""));
						String pricestatus = "";
						if (p.getOnePeice() != null) {
							if (p.getOnePeice() == 0) {
								pricestatus = "无";
							} else if (p.getAllMonty() == 0) {
								pricestatus = "配";
							}
						}
						ws.addCell(new Label(1, count, pricestatus));
						ws.addCell(new Label(2, count, p.getOrderNumber()));
						ws.addCell(new Label(3, count, p.getRootMarkId()));
						ws.addCell(new Label(4, count, p.getYwMarkId()));
						ws.addCell(new Label(5, count, p.getRootSelfCard()));
						ws.addCell(new Label(6, count, p.getWgType()));
						ws.addCell(new Label(7, count, p.getMarkId()));
						ws.addCell(new Label(8, count, p.getSpecification()));
						ws.addCell(new Label(9, count, p.getBanBenNumber()));
						ws.addCell(new Label(10, count, p.getSelfCard()));
						ws.addCell(new Label(11, count, p.getProName()));
						ws.addCell(new Label(12, count, p.getKgliao()));
						if (p.getKcNumber() == null) {
							p.setKcNumber(0F);
						}
						if (p.getZzNumber() == null) {
							p.setZzNumber(0F);
						}
						if (p.getZtNumber() == null) {
							p.setZtNumber(0F);
						}
						ws.addCell(new jxl.write.Number(13, count, (p
								.getKcNumber() == null) ? 0 : p.getKcNumber()));
						ws.addCell(new jxl.write.Number(14, count, (p
								.getZzNumber() == null) ? 0 : p.getZzNumber()));
						ws.addCell(new jxl.write.Number(15, count, (p
								.getZtNumber() == null) ? 0 : p.getZtNumber()));
						ws
								.addCell(new jxl.write.Number(16, count, ((p
										.getKcNumber() == null || p
										.getZzNumber() == null) ? 0 : p
										.getZtNumber()
										+ p.getKcNumber() - p.getZzNumber())));
						ws.addCell(new jxl.write.Number(17, count, (p
								.getFilnalCount() == null) ? 0 : p
								.getFilnalCount()));
						ws.addCell(new jxl.write.Number(18, count, (p
								.getCgNumber() == null) ? 0 : p.getCgNumber()));
						ws.addCell(new jxl.write.Number(19, count, (p
								.getOutcgNumber() == null) ? 0 : p
								.getOutcgNumber()));
						ws.addCell(new jxl.write.Number(20, count, (p
								.getTjNumber() == null) ? 0 : p.getTjNumber()));
						Float hascount = Float.valueOf(df.format(p
								.getHascount()));
						if (hascount - 0 == 0) {
							hascount = 0f;
						}
						ws.addCell(new jxl.write.Number(21, count, p
								.getTjNumber()));
						ws.addCell(new jxl.write.Number(22, count, hascount));
						Float qlNumber = 0f;
						if (p.getFilnalCount() != null
								&& p.getTjNumber() != null) {
							qlNumber = p.getFilnalCount() - p.getTjNumber();

						}
						ws.addCell(new jxl.write.Number(23, count, qlNumber));

						ws.addCell(new Label(24, count, p.getUnit()));
						ws.addCell(new Label(25, count, (j + 1) + ""));
						ws.addCell(new jxl.write.Number(26, count, (w
								.getNumber() == null) ? 0 : w.getNumber()));
						if (w.getQsNum() == null) {
							ws.addCell(new Label(27, count, ""));
						} else {
							ws.addCell(new jxl.write.Number(27, count, w
									.getQsNum()));
						}
						if (w.getHasruku() == null) {
							ws.addCell(new jxl.write.Number(28, count, 0));
						} else {
							ws.addCell(new jxl.write.Number(28, count, w
									.getHasruku()));
						}
						ws.addCell(new Label(29, count, w.getStatus()));
						ws.addCell(new Label(30, count, w.getGysName()));
						ws.addCell(new Label(31, count, w.getWaigouOrder()
								.getAddUserName()));
						ws.addCell(new Label(32, count, p.getProcessNames()));
						count++;
					}
					for (int m = 0; m <= 24; m++) {
						ws.mergeCells(m, fristrow, m, (count - 1));
					}
				} else {
					ws.addCell(new Label(0, count, (i + 1) + ""));
					String pricestatus = "";
					if (p.getOnePeice() != null) {
						if (p.getOnePeice() == 0) {
							pricestatus = "无";
						} else if (p.getAllMonty() == 0) {
							pricestatus = "配";
						}
					}
					ws.addCell(new Label(1, count, pricestatus));
					ws.addCell(new Label(2, count, p.getOrderNumber()));
					ws.addCell(new Label(3, count, p.getRootMarkId()));
					ws.addCell(new Label(4, count, p.getYwMarkId()));
					ws.addCell(new Label(5, count, p.getRootSelfCard()));
					ws.addCell(new Label(6, count, p.getWgType()));
					ws.addCell(new Label(7, count, p.getMarkId()));
					ws.addCell(new Label(8, count, p.getSpecification()));
					ws.addCell(new Label(9, count, p.getBanBenNumber()));
					ws.addCell(new Label(10, count, p.getSelfCard()));
					ws.addCell(new Label(11, count, p.getProName()));
					ws.addCell(new Label(12, count, p.getKgliao()));
					if (p.getKcNumber() == null) {
						p.setKcNumber(0F);
					}
					if (p.getZzNumber() == null) {
						p.setZzNumber(0F);
					}
					if (p.getZtNumber() == null) {
						p.setZtNumber(0F);
					}
					ws
							.addCell(new jxl.write.Number(13, count, p
									.getKcNumber()));
					ws
							.addCell(new jxl.write.Number(14, count, p
									.getZzNumber()));
					ws
							.addCell(new jxl.write.Number(15, count, p
									.getZtNumber()));
					ws.addCell(new jxl.write.Number(16, count, (p.getZtNumber()
							+ p.getKcNumber() - p.getZzNumber())));
					ws.addCell(new jxl.write.Number(17, count, p
							.getFilnalCount()));
					ws
							.addCell(new jxl.write.Number(18, count, p
									.getCgNumber()));
					ws.addCell(new jxl.write.Number(19, count, p
							.getOutcgNumber()));
					ws
							.addCell(new jxl.write.Number(20, count, p
									.getDhNumber()));
					ws
							.addCell(new jxl.write.Number(21, count, p
									.getTjNumber()));
					Float hascount = Float.valueOf(df.format(p.getHascount()));
					if (hascount - 0 == 0) {
						hascount = 0f;
					}
					ws.addCell(new jxl.write.Number(22, count, hascount));
					Float qlNumber = 0f;
					if (p.getCgNumber() != null && p.getCgNumber() > 0
							&& p.getDhNumber() != null) {
						qlNumber = (p.getCgNumber() - p.getDhNumber());
					} else {
						if (p.getFilnalCount() != null
								&& p.getTjNumber() != null) {
							qlNumber = p.getFilnalCount() - p.getTjNumber();

						}
					}
					ws.addCell(new jxl.write.Number(23, count, qlNumber));

					ws.addCell(new Label(24, count, p.getUnit()));
					ws.addCell(new Label(32, count, p.getProcessNames()));
					count++;

				}
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean exportFile2(List procardList) {
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("待采购物料清单".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("待采购物料清单", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title);
			wcf_title.setBackground(jxl.format.Colour.WHITE);
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE);
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.setColumnView(29, 30);
			ws.addCell(new Label(0, 0, "待采购计划", wcf_title));
			ws.mergeCells(0, 0, 23, 0);
			WritableCellFormat titleFormat = new WritableCellFormat(wf_title);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置居中对齐
			titleFormat.setAlignment(jxl.format.Alignment.CENTRE);// 设置居中对齐（这俩哪个是上下/左右对齐也没验证过）
			ws.addCell(new Label(0, 1, "序号"));
			ws.addCell(new Label(1, 1, "物料类别"));
			ws.addCell(new Label(2, 1, "件号"));
			ws.addCell(new Label(3, 1, "规格"));
			ws.addCell(new Label(4, 1, "版本"));
			ws.addCell(new Label(5, 1, "零件名称"));
			ws.addCell(new Label(6, 1, "供料属性"));
			ws.addCell(new Label(7, 1, "需求数量"));
			ws.addCell(new Label(8, 1, "库存数量"));
			ws.addCell(new Label(9, 1, "采购数量"));
			ws.addCell(new Label(10, 1, "申购日期"));
			ws.addCell(new Label(11, 1, "下单数量"));
			ws.addCell(new Label(12, 1, "入库数量"));
			ws.addCell(new Label(13, 1, "领料数量"));
			ws.addCell(new Label(14, 1, "欠料数量"));
			ws.addCell(new Label(15, 1, "实时库存"));
			ws.addCell(new Label(16, 1, "单位"));
			ws.addCell(new Label(17, 1, "序号"));
			ws.addCell(new Label(18, 1, "采购员"));
			ws.addCell(new Label(19, 1, "供应商"));
			ws.addCell(new Label(20, 1, "状态"));
			ws.addCell(new Label(21, 1, "签收数量"));
			ws.addCell(new Label(22, 1, "备注"));
			DecimalFormat df = new DecimalFormat("#.###");
			int count = 2;
			for (int i = 0; i < procardList.size(); i++) {
				Procard p = (Procard) procardList.get(i);
				List<WaigouPlan> waigouPlanList = p.getWaigouPlanList();
				if (waigouPlanList != null && waigouPlanList.size() > 0) {
					int fristrow = 0;
					for (int j = 0; j < waigouPlanList.size(); j++) {
						if (j == 0) {//
							fristrow = count;
						}
						WaigouPlan w = waigouPlanList.get(j);
						ws.addCell(new Label(0, count, (i + 1) + ""));
						ws.addCell(new Label(1, count, p.getWgType()));
						ws.addCell(new Label(2, count, p.getMarkId()));
						ws.addCell(new Label(3, count, p.getSpecification()));
						ws.addCell(new Label(4, count, p.getBanBenNumber()));
						ws.addCell(new Label(5, count, p.getProName()));
						ws.addCell(new Label(6, count, p.getKgliao()));
						ws.addCell(new jxl.write.Number(7, count, (p
								.getFilnalCount() == null) ? 0 : p
								.getFilnalCount()));
						ws.addCell(new jxl.write.Number(8, count, (p
								.getKcNumber() == null) ? 0 : p.getKcNumber()));
						ws.addCell(new jxl.write.Number(9, count, (p
								.getCgNumber() == null) ? 0 : p.getCgNumber()));
						if (p.getWlqrtime() != null) {
							ws.addCell(new Label(10, count, p.getWlqrtime()));
						} else {
							ws.addCell(new Label(10, count, "无"));
						}
						ws.addCell(new jxl.write.Number(11, count, (p
								.getOutcgNumber() == null) ? 0 : p
								.getOutcgNumber()));
						ws.addCell(new jxl.write.Number(12, count, (p
								.getTjNumber() == null) ? 0 : p.getTjNumber()));
						Float qlNumber = 0f;
						if (p.getFilnalCount() != null
								&& p.getTjNumber() != null) {
							qlNumber = p.getFilnalCount() - p.getTjNumber();

						}
						DecimalFormat dec = new DecimalFormat("0.0000");
						ws.addCell(new Label(13, count,
								(p.getHascount() == null) ? 0 + "" : dec
										.format(p.getHascount())));
						ws.addCell(new jxl.write.Number(14, count, qlNumber));
						ws.addCell(new Label(15, count,
								p.getNowgoodsCount() == null ? 0 + "" : dec
										.format(p.getNowgoodsCount())));
						ws.addCell(new Label(16, count, p.getUnit()));
						ws.addCell(new Label(17, count, (j+1)+""));
						ws.addCell(new Label(18, count, w.getWaigouOrder()
								.getAddUserName()));
						ws.addCell(new Label(19, count, w.getGysName()));
						ws.addCell(new Label(20, count, w.getStatus()));
						ws.addCell(new jxl.write.Number(21, count, w.getQsNum()==null?0:w.getQsNum()));
						ws.addCell(new Label(22, count, p.getRemark()));
						count++;
					}
					for (int m = 0; m <= 17; m++) {
						ws.mergeCells(m, fristrow, m, (count - 1));
					}
				} else {
					ws.addCell(new Label(0, count, (i + 1) + ""));
					ws.addCell(new Label(1, count, p.getWgType()));
					ws.addCell(new Label(2, count, p.getMarkId()));
					ws.addCell(new Label(3, count, p.getSpecification()));
					ws.addCell(new Label(4, count, p.getBanBenNumber()));
					ws.addCell(new Label(5, count, p.getProName()));
					ws.addCell(new Label(6, count, p.getKgliao()));
					ws
							.addCell(new jxl.write.Number(7, count, (p
									.getFilnalCount() == null) ? 0 : p
									.getFilnalCount()));
					ws.addCell(new jxl.write.Number(8, count,
							(p.getKcNumber() == null) ? 0 : p.getKcNumber()));
					ws.addCell(new jxl.write.Number(9, count,
							(p.getCgNumber() == null) ? 0 : p.getCgNumber()));
					if (p.getWlqrtime() != null) {
						ws.addCell(new Label(10, count, p.getWlqrtime()));
					} else {
						ws.addCell(new Label(10, count, "无"));
					}
					ws
							.addCell(new jxl.write.Number(11, count, (p
									.getOutcgNumber() == null) ? 0 : p
									.getOutcgNumber()));
					ws.addCell(new jxl.write.Number(12, count,
							(p.getTjNumber() == null) ? 0 : p.getTjNumber()));
					Float qlNumber = 0f;
					if (p.getFilnalCount() != null && p.getTjNumber() != null) {
						qlNumber = p.getFilnalCount() - p.getTjNumber();

					}
					DecimalFormat dec = new DecimalFormat("0.0000");
					ws.addCell(new Label(13, count,
							(p.getHascount() == null) ? 0 + "" : dec.format(p
									.getHascount())));
					ws.addCell(new jxl.write.Number(14, count, qlNumber));
					ws.addCell(new Label(15, count,
							p.getNowgoodsCount() == null ? 0 + "" : dec
									.format(p.getNowgoodsCount())));
					ws.addCell(new Label(16, count, p.getUnit()));
					ws.addCell(new Label(17, count, ""));
					ws.addCell(new Label(18, count, ""));
					ws.addCell(new Label(19, count, ""));
					ws.addCell(new Label(20, count, ""));
					ws.addCell(new Label(21, count, ""));
					ws.addCell(new Label(22, count, ""));
					count++;
				}
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean exportFile(List procardList) {
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response
					.setHeader("Content-disposition", "attachment; filename="
							+ new String("外购价格表".getBytes("GB2312"), "8859_1")
							+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("外购价格表", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // 单元格定义
			wcf_title.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.addCell(new Label(0, 0, "外购价格表", wcf_title));
			ws.mergeCells(0, 0, 16, 0);
			ws.addCell(new Label(0, 1, "序号"));
			ws.addCell(new Label(1, 1, "件号"));
			ws.addCell(new Label(2, 1, "名称"));
			ws.addCell(new Label(3, 1, "规格"));
			ws.addCell(new Label(4, 1, "税率"));
			ws.addCell(new Label(5, 1, "含税价"));
			ws.addCell(new Label(6, 1, "不含税价"));
			ws.addCell(new Label(7, 1, "型别"));
			ws.addCell(new Label(8, 1, "合同编号"));
			ws.addCell(new Label(9, 1, "供应商编号"));
			ws.addCell(new Label(10, 1, "开始时间"));
			ws.addCell(new Label(11, 1, "失效时间"));
			ws.addCell(new Label(12, 1, "订货量(从)"));
			ws.addCell(new Label(13, 1, "订货量(到)"));
			ws.addCell(new Label(14, 1, "采购比例"));
			ws.addCell(new Label(15, 1, "供料属性"));
			ws.addCell(new Label(16, 1, "物料类别"));
			for (int i = 0; i < procardList.size(); i++) {
				Procard p = (Procard) procardList.get(i);
				ws.addCell(new Label(0, i + 2, i + 1 + ""));
				ws.addCell(new Label(1, i + 2, p.getMarkId()));
				ws.addCell(new Label(2, i + 2, p.getProName()));
				ws.addCell(new Label(3, i + 2, ""));
				ws.addCell(new Label(4, i + 2, ""));
				ws.addCell(new Label(5, i + 2, ""));
				ws.addCell(new Label(6, i + 2, ""));
				ws.addCell(new Label(7, i + 2, ""));
				ws.addCell(new Label(8, i + 2, ""));
				ws.addCell(new Label(9, i + 2, ""));
				ws.addCell(new Label(10, i + 2, ""));
				ws.addCell(new Label(11, i + 2, ""));
				ws.addCell(new Label(12, i + 2, ""));
				ws.addCell(new Label(13, i + 2, ""));
				ws.addCell(new Label(14, i + 2, ""));
				ws.addCell(new Label(15, i + 2, p.getKgliao()));
				ws.addCell(new Label(16, i + 2, p.getWgType()));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public WaigouDeliveryDetail findWaigouPlanById(Integer id) {
		if (id != null && id > 0) {
			WaigouDeliveryDetail waigoudd = (WaigouDeliveryDetail) totalDao
					.get(WaigouDeliveryDetail.class, id);
			return waigoudd;
		}
		return null;
	}

	@Override
	public WaigouDeliveryDetail findWaigouPlanById_1(Integer id) {
		return (WaigouDeliveryDetail) totalDao
				.getObjectByCondition(
						"from WaigouDeliveryDetail where id = ? and status = '待入库'",
						id);
	}

	@Override
	public List<WarehouseNumber> findAllWNList_1() {
		String hql = " from WarehouseNumber where (status = '未满' or status is null)";
		return totalDao.query(hql);
	}

	@Override
	public Object[] findWaigouPlanListByid(Integer id) {
		if (id != null && id > 0) {
			WaigouOrder waigourOrder = (WaigouOrder) totalDao.get(
					WaigouOrder.class, id);
			if (waigourOrder != null) {
				List<WaigouPlan> waigouPlanList = totalDao.query(
						"from WaigouPlan where waigouOrder.id = ?", id);
				BigDecimal b1 = new BigDecimal(Double.toString(0));
				ZhUser zhuser = (ZhUser) totalDao.getObjectById(ZhUser.class,
						waigourOrder.getGysId());
				// if (waigourOrder.getRootId() != null) {
				// Procard procard = (Procard) totalDao.get(Procard.class,
				// waigourOrder.getRootId());
				// // waigourOrder.setNeiorderNum(procard.getOrderNumber());
				// }

				if (waigouPlanList != null && waigouPlanList.size() > 0) {
					for (WaigouPlan plan : waigouPlanList) {
						if ("关闭".equals(plan.getStatus())
								|| "取消".equals(plan.getStatus())) {
							continue;
						}
						if (plan.getType() != null
								&& plan.getType().equals("外委")) {
							List<String> outNumbers = (List<String>) totalDao
									.query(
											"select orderNumber from Procard where "
													+ "id in(select rootId from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?))",
											plan.getId());
							if (outNumbers != null && outNumbers.size() > 0) {
								StringBuffer sb = new StringBuffer();
								List<String> had = new ArrayList<String>();
								for (String outNumber : outNumbers) {
									if (!had.contains(outNumber)) {
										had.add(outNumber);
										if (sb.length() == 0) {
											sb.append(outNumber);
										} else {
											sb.append("，" + outNumber);
										}
									}
								}
								plan.setNeiorderNum(sb.toString());
							}
							// if(plan.getWwSource().equals("手动外委")){
							// String outNumber = (String)
							// totalDao.getObjectByCondition("select orderNumber from Procard where "
							// +
							// "id (select rootId from Procard where id in(select procardId from ProcessInforWWApplyDetail where id=?))",
							// plan.getWwDetailId());
							// plan.setNeiorderNum(outNumber);
							// }else if(plan.getWwSource().equals("BOM外委")){
							// String outNumber
							// =totalDao.getObjectByCondition(hql, agr)
							// plan.setNeiorderNum(outNumber);
							// }
						} else {
							if (plan.getJiaofuTime() == null) {
								plan.setJiaofuTime(plan.getShArrivalTime());
							}
							if (plan.getJiaofuTime() == null
									|| "".equals(plan.getJiaofuTime())) {
								plan.setJiaofuTime(Util
										.getDateTime("yyyy-MM-dd"));
							} else {
								plan.setJiaofuTime(Util.DateToString(Util
										.StringToDate(plan.getJiaofuTime(),
												"yyyy-MM-dd"), "yyyy-MM-dd"));
							}
						}
						BigDecimal b2 = new BigDecimal(plan.getMoney()
								.toString());
						b1 = b1.add(b2);
					}
				}
				b1 = b1.setScale(4, BigDecimal.ROUND_DOWN);
				Object[] obj = { waigourOrder, waigouPlanList, b1.toString(),
						zhuser };
				return obj;
			}
		}
		return null;
	}

	@Override
	public Object[] findZaiTuwl(Procard procard, int pageNo, int pageSize,
			String pageStatus) {
		// TODO Auto-generated method stub
		if (procard == null) {
			procard = new Procard();
		}
		String hql = totalDao.criteriaQueries(procard, null);
		hql = "select markId,kgliao,sum(cgNumber),sum(cgNumber),sum(dhNumber)"
				+ hql
				+ " and cgNumber is not null and status not in('完成','待入库','入库') group by markId,kgliao";
		List<Object[]> list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/*
	 * 导出采购订单
	 */
	public boolean exportWgOrderList(List<WaigouOrder> list) {
		// TODO Auto-generated method stub
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response
					.setHeader("Content-disposition", "attachment; filename="
							+ new String("采购订单表".getBytes("GB2312"), "8859_1")
							+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("采购订单表", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // 单元格定义
			wcf_title.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.addCell(new Label(0, 0, "采购订单表", wcf_title));
			ws.mergeCells(0, 0, 8, 0);
			ws.addCell(new Label(0, 1, "序号"));
			ws.addCell(new Label(1, 1, "供应商"));
			ws.addCell(new Label(2, 1, "采购月份"));
			ws.addCell(new Label(3, 1, "订单编号"));
			ws.addCell(new Label(4, 1, "状态"));
			ws.addCell(new Label(5, 1, "添加日期"));
			ws.addCell(new Label(6, 1, "联系人"));
			ws.addCell(new Label(7, 1, "通知日期"));
			ws.addCell(new Label(8, 1, "确认日期"));
			for (int i = 0; i < list.size(); i++) {
				WaigouOrder wgo = list.get(i);
				ws.addCell(new Label(0, i + 2, i + 1 + ""));
				ws.addCell(new Label(1, i + 2, wgo.getGysName()));
				ws.addCell(new Label(2, i + 2, wgo.getCaigouMonth()));
				ws.addCell(new Label(3, i + 2, wgo.getPlanNumber()));
				ws.addCell(new Label(4, i + 2, wgo.getStatus()));
				ws.addCell(new Label(5, i + 2, wgo.getAddTime()));
				if (wgo.getAddTime().equals("待通知")) {
					ws.addCell(new Label(6, i + 2, wgo.getAddUserName()));
				} else {
					ws.addCell(new Label(6, i + 2, wgo.getTzUserName()));
				}
				ws.addCell(new Label(7, i + 2, wgo.getTongzhiTime()));
				ws.addCell(new Label(8, i + 2, wgo.getQuerenTime()));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean exportNoPrice() {
		// TODO Auto-generated method stub
		try {
			List<Object[]> procardList = totalDao
					.query("select markId, proName,kgliao,wgType from Procard where wlstatus='待采购' and procardStyle='外购'  and markid+kgliao not in (select partNumber+kgliao from Price where productCategory='零件' and produceType='外购')");
			List<Object[]> procardList2 = totalDao
					.query("select trademark, yuanName,kgliao,wgType from Procard where wlstatus='待采购' and procardStyle!='外购' and trademark is not null and trademark !=''  and trademark+kgliao not in (select partNumber+kgliao from Price where productCategory='零件' and produceType='外购')");
			List<Object[]> list = new ArrayList<Object[]>();
			list.addAll(procardList);
			list.addAll(procardList2);
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("未录入外购价格表".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("未录入外购价格表", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // 单元格定义
			wcf_title.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.addCell(new Label(0, 0, "外购价格表", wcf_title));
			ws.mergeCells(0, 0, 16, 0);
			ws.addCell(new Label(0, 1, "序号"));
			ws.addCell(new Label(1, 1, "件号"));
			ws.addCell(new Label(2, 1, "名称"));
			ws.addCell(new Label(3, 1, "规格"));
			ws.addCell(new Label(4, 1, "税率"));
			ws.addCell(new Label(5, 1, "含税价"));
			ws.addCell(new Label(6, 1, "不含税价"));
			ws.addCell(new Label(7, 1, "型别"));
			ws.addCell(new Label(8, 1, "合同编号"));
			ws.addCell(new Label(9, 1, "供应商编号"));
			ws.addCell(new Label(10, 1, "开始时间"));
			ws.addCell(new Label(11, 1, "失效时间"));
			ws.addCell(new Label(12, 1, "订货量(从)"));
			ws.addCell(new Label(13, 1, "订货量(到)"));
			ws.addCell(new Label(14, 1, "采购比例"));
			ws.addCell(new Label(15, 1, "供料属性"));
			ws.addCell(new Label(16, 1, "物料类别"));
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = list.get(i);
				String markId = null;
				String proName = null;
				String kgliao = null;
				String wgType = null;
				if (objs[0] != null) {
					markId = objs[0].toString();
				}
				if (objs[1] != null) {
					proName = objs[1].toString();
				}
				if (objs[2] != null) {
					kgliao = objs[2].toString();
				}
				if (objs[3] != null) {
					wgType = objs[3].toString();
				}
				ws.addCell(new Label(0, i + 2, i + 1 + ""));
				ws.addCell(new Label(1, i + 2, markId));
				ws.addCell(new Label(2, i + 2, proName));
				ws.addCell(new Label(3, i + 2, ""));
				ws.addCell(new Label(4, i + 2, ""));
				ws.addCell(new Label(5, i + 2, ""));
				ws.addCell(new Label(6, i + 2, ""));
				ws.addCell(new Label(7, i + 2, ""));
				ws.addCell(new Label(8, i + 2, ""));
				ws.addCell(new Label(9, i + 2, ""));
				ws.addCell(new Label(10, i + 2, ""));
				ws.addCell(new Label(11, i + 2, ""));
				ws.addCell(new Label(12, i + 2, ""));
				ws.addCell(new Label(13, i + 2, ""));
				ws.addCell(new Label(14, i + 2, ""));
				ws.addCell(new Label(15, i + 2, kgliao));
				ws.addCell(new Label(16, i + 2, wgType));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public List getAllWgType() {
		// TODO Auto-generated method stub
		return totalDao
				.query("from UnitManager where type='material' order by unitname");
	}

	public Object[] upfindDeliveryRuGuiBacode(String bacode, String mxId) {
		// TODO Auto-generated method stub
		boolean b = false;
		if (bacode != null) {
			WarehouseNumber wN = (WarehouseNumber) totalDao
					.getObjectByCondition(
							"from WarehouseNumber where barCode = ?", bacode);
			if (wN == null)
				return null;
			if (wN.getIp() != null && !"".equals(wN.getIp())) {// 有门，开门操作
				if ("无".equals(wN.getHave()) && !"开".equals(wN.getKwStatus())) {
					List<WaigouDeliveryDetail> listwdd = new ArrayList<WaigouDeliveryDetail>();
					String[] mxStr = mxId.split(",");
					for (String s : mxStr) {
						WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
								.getObjectById(WaigouDeliveryDetail.class,
										Integer.parseInt(s));
						if (detail == null) {
							Object[] o = { "选择送货单明细不存在,请检查后重试!" };
							return o;
						} else {
							// 查询此物品有没有待存入状态
							int ie = totalDao
									.getCount(
											"from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and status = '待存入' ",
											detail.getId());
							if (ie == 0) {
								// 添加中间表 建立关系
								WareBangGoogs bwg = new WareBangGoogs();
								bwg.setFk_waigouDeliveryDetail_id(detail
										.getId());// 采购明细单ID
								bwg.setFk_ware_id(wN.getId());
								bwg.setNumber(detail.getQrNumber());// 可操作数量
								bwg.setTowCode(UUID.randomUUID().toString());
								bwg.setStatus("待存入");
								if (totalDao.save(bwg)) {// 添加成功后 更新库位状态。
									listwdd.add(detail);
									b = true;
								} else {
									Object[] o = { "关系保存失败，请重试！" };
									return o;
								}
							} else {
								Object[] o = { "您尚有未关闭的库位，或该库位已有待存入物品，请先检查后再进行存物操作！" };
								return o;
							}
						}
					}
					if (b) {
						// 返回主页面
						UtilTong.backTowMa(wN.getIp(), duankou, wN
								.getOneNumber());
						sleeps(1000);
						// 开门
						String messages = UtilTong.OCKuWei(wN.getIp(), duankou,
								true, wN.getOneNumber(), wN.getNumid());// 开关门操作
						if ("true".equals(messages)) {
							wN.setKwStatus("开");
							wN.setCzUserId(Util.getLoginUser().getId());
							wN.setSczTime(Util.getDateTime());
							totalDao.update(wN);// 更新状态
							Object[] o = { messages, listwdd, wN };
							return o;
						} else {
							throw new RuntimeException("开门失败，请检查网络！");
						}
					} else {
						Object[] o = { "信息保存有误，开门失败！" };
						return o;
					}
				} else {
					Object[] o = { "库位不为空或库位未关闭，开门失败！" };
					return o;
				}
			} else {
				// 没有IP，不开门
				List<WaigouDeliveryDetail> listwdd = new ArrayList<WaigouDeliveryDetail>();
				String[] mxStr = mxId.split(",");
				for (String s : mxStr) {
					WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
							.getObjectById(WaigouDeliveryDetail.class, Integer
									.parseInt(s));
					if (detail == null) {
						Object[] o = { "选择送货单明细不存在,请检查后重试!" };
						return o;
					} else {
						// 将库位放置明细表中
						detail.setQrWeizhi(wN.getNumber());
						totalDao.update(detail);
						listwdd.add(detail);
					}
				}
				Object[] o = { "true", listwdd, wN };
				return o;
			}
		}
		return null;
	}

	@Override
	public Object[] upRuKuiBacode(String bacode) {
		// TODO Auto-generated method stub
		if (bacode != null) {
			WareBangGoogs wbg_1 = byTowCodeWBG(bacode);
			if (wbg_1 == null)
				return null;
			WarehouseNumber wN = byIdWN(wbg_1.getFk_ware_id());
			// 返回主页面
			UtilTong.backTowMa(wN.getIp(), duankou, wN.getOneNumber());
			sleeps(1000);
			if (wN == null)
				return null;
			WaigouDeliveryDetail detail = findWaigouPlanById(wbg_1
					.getFk_waigouDeliveryDetail_id());
			if (detail == null) {
				Object[] o = { "选择送货单明细不存在,请检查后重试!" };
				return o;
			} else {
				// 开门
				String messages = UtilTong.OCKuWei(wN.getIp(), duankou, true,
						wN.getOneNumber(), wN.getNumid());// 开关门操作
				if ("true".equals(messages)) {
					// 停止闪灯
					messages = UtilTong.openColorLight(wN.getIp(), duankou,
							true, false, wN.getOneNumber(), wN.getNumid(), wN
									.getMarkTypt() == null ? 0 : wN
									.getMarkTypt().getMarkColor());
					wN.setKwStatus("开");
					wN.setCzUserId(Util.getLoginUser().getId());
					wN.setSczTime(Util.getDateTime());
					if (totalDao.update(wN)) {// 更新状态
						wbg_1.setStatus("确认中");
						if (totalDao.save(wbg_1)) {// 添加成功后 更新库位状态。
							Object[] o = { messages, detail, wN };
							return o;
						} else {
							throw new RuntimeException("确认失败，请重试！");
						}
					} else {
						Object[] o = { "库位状态更新失败，请重试！" };
						return o;
					}
				} else {
					Object[] o = { "开门失败，请检查网络！" };
					return o;
				}
			}
		}
		return null;
	}

	@Override
	public String updateWNToRK(WaigouDeliveryDetail wgd, WarehouseNumber wn) {
		// TODO Auto-generated method stub
		// 返回主页面
		UtilTong.backTowMa(wn.getIp(), duankou, wn.getOneNumber());
		sleeps(1000);
		String messages = UtilTong.OCKuWei(wn.getIp(), duankou, false, wn
				.getOneNumber(), wn.getNumid());// 关门操作
		if ("true".equals(messages)) {
			wn.setSczTime(Util.getDateTime());
			wn.setCzUserId(null);
			wn.setKwStatus("关");
			StringBuffer buffer = new StringBuffer();
			WaigouDeliveryDetail detail = getWaiGouddById(wgd.getId());
			if (detail == null)
				return "送货单明细异常";
			else {
				// 待检库出库
				String hql_ = " from GoodsStore where goodsStoreWarehouse = ? and wwddId=? and goodsStoreMarkId =?";
				GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_,
						"待检库", detail.getId(), detail.getMarkId());
				if (t != null) {
					String hql = "from Goods where goodsMarkId = ? ";
					if (t.getGoodsStoreWarehouse() != null
							&& t.getGoodsStoreWarehouse().length() > 0) {
						hql += " and goodsClass='" + t.getGoodsStoreWarehouse()
								+ "'";
					}
					if (t.getGoodHouseName() != null
							&& t.getGoodHouseName().length() > 0) {
						hql += " and goodHouseName='" + t.getGoodHouseName()
								+ "'";
					}
					if (t.getGoodsStorePosition() != null
							&& t.getGoodsStorePosition().length() > 0) {
						hql += " and goodsPosition='"
								+ t.getGoodsStorePosition() + "'";
					}
					if (t.getBanBenNumber() != null
							&& t.getBanBenNumber().length() > 0) {
						hql += " and banBenNumber='" + t.getBanBenNumber()
								+ "'";
					}
					if (t.getKgliao() != null && t.getKgliao().length() > 0) {
						hql += " and kgliao='" + t.getKgliao() + "'";
					}
					if (t.getGoodsStoreLot() != null
							&& t.getGoodsStoreLot().length() > 0) {
						hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
					}
					Goods s = (Goods) totalDao.getObjectByCondition(hql,
							new Object[] { t.getGoodsStoreMarkId() });
					if (s != null) {
						s.setGoodsCurQuantity(s.getGoodsCurQuantity()
								- detail.getHgNumber());
						Sell sell = new Sell();
						sell.setSellMarkId(s.getGoodsMarkId());// 件号
						sell.setSellWarehouse(s.getGoodsClass());// 库别
						sell.setGoodHouseName(s.getGoodHouseName());// 仓区
						sell.setKuwei(s.getGoodsPosition());// 库位
						sell.setBanBenNumber(s.getBanBenNumber());// 版本号
						sell.setKgliao(s.getKgliao());// 供料属性
						sell.setWgType(s.getWgType());// 物料类别
						sell.setSellGoods(s.getGoodsFullName());// 品名
						sell.setSellFormat(s.getGoodsFormat());// 规格
						sell.setSellCount(detail.getHgNumber());// 出库数量
						sell.setSellUnit(s.getGoodsUnit());// 单位
						sell.setSellSupplier(s.getGoodsSupplier());// 供应商
						sell.setSellTime(Util.getDateTime());// 出库时间
						sell.setTuhao(s.getTuhao());// 图号
						sell.setBedit(false);// 出库权限
						sell.setBedit(false);// 编辑权限
						sell.setPrintStatus("YES");// 打印状态
						sell.setSellLot(s.getGoodsLotId());// 批次
						sell.setStyle("待检库出库");
						totalDao.save(sell);
						totalDao.update(s);
					}

				}

				/**
				 * 开始入库;
				 */
				GoodsStore goodsStore = new GoodsStore();
				goodsStore.setGoodsStoreTime(Util.getDateTime());// 入库时间;
				goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));// 入库日期
				goodsStore.setGoodsStorePlanner(Util.getLoginUser().getName());// 操作人
				goodsStore.setStatus("入库");
				goodsStore.setStyle("批产");
				goodsStore.setGoodsStoreFormat(detail.getSpecification());// 规格
				goodsStore.setGoodsStoreMarkId(detail.getMarkId());// 件号
				goodsStore.setGoodsStoreGoodsName(detail.getProName());// 名称
				goodsStore.setGoodsStoreUnit(detail.getUnit());// 单位
				goodsStore.setKgliao(detail.getKgliao());// 供料属性
				goodsStore.setBanBenNumber(detail.getBanben());// 版本号;
				goodsStore.setGoodsStoreSupplier(detail.getGysName());// 供应商名称
				goodsStore.setGysId(detail.getGysId() + "");// 供应商Id
				goodsStore.setHsPrice(detail.getHsPrice()==null?null:detail.getHsPrice().doubleValue());// 含税价格
				goodsStore.setGoodsStorePrice(detail.getPrice()==null?null:detail.getPrice().doubleValue());// 不含税价格
				goodsStore.setWgType(detail.getWgType());// 物料类别

				goodsStore.setGoodsStoreWarehouse("外购件库");// 所属仓库;固定的 外购件库
				goodsStore.setTuhao(detail.getTuhao());
				goodsStore.setGoodHouseName(wn.getWarehouseArea());// 所属仓区;
				goodsStore.setGoodsStorePosition(wn.getNumber());// 所属库位
				goodsStore.setGoodsStoreSendId(detail.getWaigouDelivery()
						.getPlanNumber());// 送货单号
				goodsStore.setWwddId(detail.getId());// 送货单明细Id;
				WaigouPlan waigouplan = (WaigouPlan) totalDao.get(
						WaigouPlan.class, detail.getWaigouPlanId());
				if (waigouplan != null) {
					goodsStore.setGoodsStoreProMarkId(waigouplan
							.getWaigouOrder().getRootMarkId());// 总成件号
					ProcardWGCenter procardwgcenter = (ProcardWGCenter) totalDao
							.get(ProcardWGCenter.class, waigouplan
									.getWaigouOrder().getId());
					if (procardwgcenter != null) {
						Procard procard = (Procard) totalDao.get(Procard.class,
								procardwgcenter.getProcardId());
						goodsStore.setGoodsStoreLot(procard.getSelfCard());// 批次号
					}
				}
				WareBangGoogs bwg = (WareBangGoogs) totalDao
						.getObjectByCondition(
								"from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and fk_ware_id = ? and status = '确认中' ",
								detail.getId(), wn.getId());
				if (bwg != null && bwg.getNumber() != null
						&& bwg.getNumber() >= 0) {
					goodsStore.setGoodsStoreCount(bwg.getNumber());// 入库数量
				} else {
					return "没有得到数量或数量小于0;";
				}
				String hql_osRecord = " from OsRecord where wwddId =?";
				OsRecord osRecord = (OsRecord) totalDao.getObjectByCondition(
						hql_osRecord, detail.getId());
				if (osRecord != null) {
					goodsStore.setOsrecordId(osRecord.getId());
				}
				goodsStoreServer.save(goodsStore, osRecord);
				String hql_goods = " from Goods where goodsMarkId = ? and goodsLotId = ? ";
				Goods s = (Goods) totalDao.getObjectByCondition(hql_goods,
						goodsStore.getGoodsStoreMarkId(), goodsStore
								.getGoodsStoreLot());
				if (s != null) {
					bwg.setFk_good_id(s.getGoodsId());// 中间表设置库存Id;
					bwg.setStatus("入库");
				}
				if (detail.getHgNumber().equals(bwg.getNumber())) {
					detail.setStatus("入库");
					detail.setYcNumber(detail.getHgNumber());// 存入数量;
				} else if (detail.getHgNumber() > bwg.getNumber()) {
					detail.setStatus("入库中");
					detail.setYcNumber(bwg.getNumber());
				} else {
					detail.setStatus("入库");
					bwg.setNumber(detail.getHgNumber());
					detail.setYcNumber(detail.getHgNumber());
				}
				goodsStoreServer.updateWaiProcard(goodsStore
						.getGoodsStoreMarkId(),
						goodsStore.getGoodsStoreCount(), "外购件库", goodsStore
								.getGoodsStoreId());
				if (bwg != null) {
					buffer.append(Util.neirong(detail.getMarkId() + "", 30));// 件号
					buffer
							.append(Util.neirong(detail.getExamineLot() + "",
									16));// 批次
					buffer.append(Util.neirong(detail.getHgNumber() + "", 8));// 数量
					buffer.append(Util.neirong(wn.getNumber() + "", 16));// 库位编号
					buffer.append(Util.neirong(wn.getMarkTyptName() + "", 8));// 库位状态
					buffer.append(Util.neirong(detail.getGysName() + "", 30));// 供应商
					totalDao.update(detail);
					totalDao.update(bwg);
				}
			}
			totalDao.update(wn);// 更新库位信息
			sleeps(1000);
			// 灭灯
			UtilTong.closeLight(wn.getIp(), duankou, wn.getOneNumber(), wn
					.getNumid());
			// 亮灯的颜色
			UtilTong.openColorLight(wn.getIp(), duankou, true, true, wn
					.getOneNumber(), wn.getNumid(),
					wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
							.getMarkColor());
			sleeps(500);
			// 发送详细屏幕信息
			UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn.getOneNumber(),
					wn.getNumid(), buffer.toString(), 1);
		}
		return messages;
	}

	@Override
	public String updateWNToRG(List<WaigouDeliveryDetail> list,
			WarehouseNumber wn) {
		// TODO Auto-generated method stub
		if (wn.getIp() != null && !"".equals(wn.getIp())) {
			String messages = UtilTong.OCKuWei(wn.getIp(), duankou, false, wn
					.getOneNumber(), wn.getNumid());// 关门操作
			if ("true".equals(messages)) {
				wn.setSczTime(Util.getDateTime());
				wn.setCzUserId(null);
				wn.setKwStatus("关");
				StringBuffer buffer = new StringBuffer();
				StringBuffer buffer1 = null;
				StringBuffer buffer2 = null;
				if (list.size() == 2) {
					buffer1 = new StringBuffer();
				} else if (list.size() > 2) {
					buffer1 = new StringBuffer();
					buffer2 = new StringBuffer();
				}
				int i = 0;
				for (WaigouDeliveryDetail wdd : list) {
					i++;
					WaigouDeliveryDetail detail = getWaiGouddById(wdd.getId());
					if (detail == null)
						continue;
					else {
						WareBangGoogs bwg = (WareBangGoogs) totalDao
								.getObjectByCondition(
										"from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and fk_ware_id = ? and status = '待存入' ",
										detail.getId(), wn.getId());
						if (bwg != null) {
							boolean b = false;
							if (detail.getYcNumber() == null) {// 第一次存
								if (wdd.getYcNumber() < detail.getQrNumber()) {// 小于
									bwg.setNumber(wdd.getYcNumber());
									if (wdd.getYcNumber() <= 0) {
										detail.setYcNumber(0f);
										wn.setHave("无");
										totalDao.delete(bwg);
										continue;
									} else {
										detail.setYcNumber(wdd.getYcNumber());
										wn.setHave("有");
										b = true;
										bwg.setStatus("待检验");
										totalDao.update(bwg);
									}
								} else {// 大于等于
									wn.setHave("有");
									b = true;
									bwg.setStatus("待检验");
									totalDao.update(bwg);
									detail.setStatus("待检验");
									detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
									detail.setYcNumber(wdd.getYcNumber());
								}
							} else {// 非首次存入
								if (wdd.getYcNumber() + detail.getYcNumber() < detail
										.getQrNumber()) {// 小于
									bwg.setNumber(wdd.getYcNumber());
									if (wdd.getYcNumber() <= 0) {
										detail.setYcNumber(0f);
										wn.setHave("无");
										totalDao.delete(bwg);
										continue;
									} else {
										if (detail.getYcNumber() > 0)
											detail.setQrWeizhi(detail
													.getQrWeizhi()
													+ "," + wn.getNumber());// 存入后库位位置
										else
											detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
										detail.setYcNumber(wdd.getYcNumber()
												+ detail.getYcNumber());
										wn.setHave("有");
										b = true;
										bwg.setStatus("待检验");
										totalDao.update(bwg);
									}
								} else {// 大于等于
									wn.setHave("有");
									b = true;
									bwg.setStatus("待检验");
									totalDao.update(bwg);
									detail.setStatus("待检验");
									if (detail.getYcNumber() > 0)
										detail.setQrWeizhi(detail.getQrWeizhi()
												+ "," + wn.getNumber());// 存入后库位位置
									else
										detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
									detail.setYcNumber(wdd.getYcNumber()
											+ detail.getYcNumber());
								}
							}
							if (b && "空".equals(wn.getMarkTyptName())) {
								// 改变库位中物品的状态
								updateWN(wn, "待检");
							}
							if (i == 1) {
								buffer.append(Util.neirong(detail.getMarkId()
										+ "", 30));// 件号
								buffer.append(Util.neirong(detail
										.getExamineLot()
										+ "", 16));// 批次
								buffer.append(Util.neirong(wdd.getYcNumber()
										+ "", 8));// 数量
								buffer.append(Util.neirong(wn.getNumber() + "",
										16));// 库位编号
								buffer.append(Util.neirong(wn.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer.append(Util.neirong(detail.getGysName()
										+ "", 30));// 供应商
								detail.setWbdId(bwg.getId());// 区分是否有智能库位
							} else if (i == 2) {
								buffer1.append(Util.neirong(detail.getMarkId()
										+ "", 30));// 件号
								buffer1.append(Util.neirong(detail
										.getExamineLot()
										+ "", 16));// 批次
								buffer1.append(Util.neirong(wdd.getYcNumber()
										+ "", 8));// 数量
								buffer1.append(Util.neirong(
										wn.getNumber() + "", 16));// 库位编号
								buffer1.append(Util.neirong(wn
										.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer1.append(Util.neirong(detail.getGysName()
										+ "", 30));// 供应商
								detail.setWbdId(bwg.getId());// 区分是否有智能库位
							} else if (i >= 3) {
								buffer2.append(Util.neirong(detail.getMarkId()
										+ "", 30));// 件号
								buffer2.append(Util.neirong(detail
										.getExamineLot()
										+ "", 16));// 批次
								buffer2.append(Util.neirong(wdd.getYcNumber()
										+ "", 8));// 数量
								buffer2.append(Util.neirong(
										wn.getNumber() + "", 16));// 库位编号
								buffer2.append(Util.neirong(wn
										.getMarkTyptName()
										+ "", 8));// 库位状态
								buffer2.append(Util.neirong(detail.getGysName()
										+ "", 30));// 供应商
								detail.setWbdId(bwg.getId());// 区分是否有智能库位
							}
							totalDao.update(detail);

							// 发消息给检验员;
							String hql_user = "SELECT u.id from Category c join c.userSet u where c.name=?";
							List<Integer> userIdList = totalDao.query(hql_user,
									detail.getWgType());
							Integer[] userIds = null;
							if (userIdList != null && userIdList.size() > 0) {
								userIds = new Integer[userIdList.size()];
								for (int j = 0; j < userIdList.size(); j++) {
									userIds[j] = userIdList.get(j);
								}
							}
							AlertMessagesServerImpl.addAlertMessages("检验提醒",
									"送货单号:"
											+ detail.getWaigouDelivery()
													.getPlanNumber() + ",件号:"
											+ detail.getMarkId() + "" + ",名称:"
											+ detail.getProName() + ",检验批次:"
											+ detail.getExamineLot() + ","
											+ "已存库。确认数量:"
											+ detail.getQrNumber() + "("
											+ detail.getUnit() + ")。请及时检验",
									userIds, "", true, "false");

						}
					}
				}
				totalDao.update(wn);// 更新库位信息
				sleeps(300);
				// 灭灯
				UtilTong.closeLight(wn.getIp(), duankou, wn.getOneNumber(), wn
						.getNumid());
				// 亮灯的颜色
				UtilTong.openColorLight(wn.getIp(), duankou, true, true, wn
						.getOneNumber(), wn.getNumid(),
						wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
								.getMarkColor());
				sleeps(6000);
				// 发送详细屏幕信息
				UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
						.getOneNumber(), wn.getNumid(), buffer.toString(), 1);
				sleeps(500);
				if (i >= 2) {
					UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
							.getOneNumber(), wn.getNumid(), buffer1.toString(),
							2);
					sleeps(500);
				}
				if (i >= 3) {
					UtilTong.querenpingKuWei1(wn.getIp(), duankou, wn
							.getOneNumber(), wn.getNumid(), buffer2.toString(),
							3);
					sleeps(500);
				}
			}
			return messages;
		} else {
			for (WaigouDeliveryDetail wdd : list) {
				WaigouDeliveryDetail detail = getWaiGouddById(wdd.getId());
				if (detail == null)
					continue;
				else {
					if (detail.getYcNumber() == null) {// 第一次存
						if (wdd.getYcNumber() < detail.getQrNumber()) {// 小于
							if (wdd.getYcNumber() <= 0) {
								detail.setYcNumber(0f);
								continue;
							} else {
								detail.setYcNumber(wdd.getYcNumber());
							}
						} else {// 大于等于
							detail.setStatus("待检验");
							detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
							detail.setYcNumber(wdd.getYcNumber());
						}
					} else {// 非首次存入
						if (wdd.getYcNumber() + detail.getYcNumber() < detail
								.getQrNumber()) {// 小于
							if (wdd.getYcNumber() <= 0)
								continue;
							else {
								if (detail.getYcNumber() > 0)
									detail.setQrWeizhi(detail.getQrWeizhi()
											+ "," + wn.getNumber());// 存入后库位位置
								else
									detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
								detail.setYcNumber(wdd.getYcNumber()
										+ detail.getYcNumber());
							}
						} else {// 大于等于
							detail.setStatus("待检验");
							if (detail.getYcNumber() > 0)
								detail.setQrWeizhi(detail.getQrWeizhi() + ","
										+ wn.getNumber());// 存入后库位位置
							else
								detail.setQrWeizhi(wn.getNumber());// 存入后库位位置
							detail.setYcNumber(wdd.getYcNumber()
									+ detail.getYcNumber());
						}
					}
				}
				totalDao.update(detail);
				// 发消息给检验员;
				String hql_user = "SELECT u.id from Category c join c.userSet u where c.name=?";
				List<Integer> userIdList = totalDao.query(hql_user, detail
						.getWgType());
				Integer[] userIds = null;
				if (userIdList != null && userIdList.size() > 0) {
					userIds = new Integer[userIdList.size()];
					for (int j = 0; j < userIdList.size(); j++) {
						userIds[j] = userIdList.get(j);
					}
				}
				AlertMessagesServerImpl.addAlertMessages("检验提醒", "送货单号:"
						+ detail.getWaigouDelivery().getPlanNumber() + ",件号:"
						+ detail.getMarkId() + "" + ",名称:"
						+ detail.getProName() + ",检验批次:"
						+ detail.getExamineLot() + "," + "已存库。确认数量:"
						+ detail.getQrNumber() + "(" + detail.getUnit()
						+ ")。请及时检验", userIds, "", true, "false");
			}

			return "true";
		}
	}

	@Override
	public WarehouseNumber byIdWN(Integer id) {
		// TODO Auto-generated method stub
		return (WarehouseNumber) totalDao.getObjectById(WarehouseNumber.class,
				id);
	}

	@Override
	public Object[] findRuGuiQuerenCode(Integer id) {
		// TODO Auto-generated method stub
		WarehouseNumber number = byIdWN(id);
		if (number != null) {
			List<WareBangGoogs> googs = totalDao
					.query(
							"from WareBangGoogs where fk_ware_id = ? and status = '待存入'",
							id);
			if (googs != null && googs.size() > 0) {
				if (googs.get(0).getFk_waigouDeliveryDetail_id() == null) {
					List<OaAppDetail> details = new ArrayList<OaAppDetail>();
					for (WareBangGoogs w : googs) {
						OaAppDetail detail = (OaAppDetail) totalDao
								.getObjectById(OaAppDetail.class, w
										.getFk_oadetail_id());
						if (detail == null)
							continue;
						details.add(detail);
					}
					Object[] o = { "oa", details, number };
					return o;
				} else {
					List<WaigouDeliveryDetail> details = new ArrayList<WaigouDeliveryDetail>();
					for (WareBangGoogs w : googs) {
						WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
								.getObjectById(WaigouDeliveryDetail.class, w
										.getFk_waigouDeliveryDetail_id());
						if (detail == null)
							continue;
						details.add(detail);
						if ("确认中".equals(w.getStatus())) {
							Object[] o = { "rkqr", details, number };
							return o;
						}
					}
					Object[] o = { "wg", details, number };
					return o;
				}
			} else {
				List<WareBangGoogs> goog1 = totalDao
						.query(
								"from WareBangGoogs where fk_ware_id = ? and status = '确认中'",
								id);
				if (goog1 != null && goog1.size() > 0) {
					for (WareBangGoogs w : goog1) {
						WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
								.getObjectById(WaigouDeliveryDetail.class, w
										.getFk_waigouDeliveryDetail_id());
						if (detail == null)
							continue;
						if ("确认中".equals(w.getStatus())) {
							Object[] o = { "rkqr", detail, number };
							return o;
						}
					}
					Object[] o = { "rkqr", null, number };
					return o;
				}
			}
		}
		Object[] o = { "库位信息为空", null, null };
		return o;
	}

	@Override
	public List findRuGuiWN() {
		// TODO Auto-generated method stub
		return totalDao.query("from WarehouseNumber where czUserId = ?", Util
				.getLoginUser().getId());
	}

	@Override
	public boolean xiugaigys(Integer id, Integer gysId) {
		WaigouOrder waigouOrder = (WaigouOrder) totalDao.get(WaigouOrder.class,
				id);
		ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, gysId);
		waigouOrder.setGysId(gysId);// 供应商Id;
		waigouOrder.setUserCode(zhuser.getUsercode());// 供应商编号
		waigouOrder.setLxPeople(zhuser.getCperson());// 供应商联系人
		waigouOrder.setGysName(zhuser.getName());// 供应商名称
		waigouOrder.setGysPhone(zhuser.getCfax());// 供应商电话
		waigouOrder.setGhAddress(zhuser.getCompanydz());// 供货地址/供应商地址
		waigouOrder.setUserId(zhuser.getUserid());// 供应商在user中的Id
		boolean bool = totalDao.update(waigouOrder);
		Set<WaigouPlan> wwpSet = waigouOrder.getWwpSet();
		for (WaigouPlan waigouPlan : wwpSet) {
			waigouPlan.setUserId(zhuser.getUserid());// 供应商在user中的Id
			waigouPlan.setGysName(zhuser.getName());// 供应商名称
			waigouPlan.setUserCode(zhuser.getUsercode());// 供应商编号
			waigouPlan.setGysId(gysId);// 供应商Id;
			if (!totalDao.update(waigouPlan)) {
				return false;
			}
		}
		return bool;
	}

	@Override
	public List<ZhUser> findAllZhUser() {
		String hql = "from ZhUser where blackliststauts = '正常使用'";
		return totalDao.find(hql);
	}

	@Override
	public WaigouOrder findWaigouOrderById(int id) {
		return (WaigouOrder) totalDao.get(WaigouOrder.class, id);
	}

	public List<KuweiSongHuoDan> findAllWNList_2(Integer id, String status) {
		if (status == null || "".equals(status)) {
			status = "待检验";
		}
		List<Map> list = totalDao
				.findBySql("SELECT a.id id,a.number number ,b.number number1 ,b.towCode  FROM ta_WarehouseNumber a  LEFT JOIN ta_mj_WareBangGoogs b ON b.fk_waigouDeliveryDetail_id = "
						+ id
						+ " and  b.fk_ware_id = a.id  WHERE  b.status = '"
						+ status + "'");
		List<KuweiSongHuoDan> list1 = null;
		if (list != null && list.size() > 0) {
			list1 = new ArrayList<KuweiSongHuoDan>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				KuweiSongHuoDan kh = new KuweiSongHuoDan();
				kh.setKuweiId((Integer) map.get("id"));
				kh.setKuweiNo((String) map.get("number"));
				kh.setNumber((Double) map.get("number1"));
				kh.setTowCode((String) map.get("towCode"));
				list1.add(kh);
			}
		}
		return list1;

	}

	@Override
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
	public String oaCloseW(WarehouseNumber wn, Borrow borrow) {
		// TODO Auto-generated method stub
		String message = UtilTong.OCKuWei(wn.getIp(), duankou, false, wn
				.getOneNumber(), wn.getNumid());// 关门操作
		if ("true".equals(message)) {
			wn.setKwStatus("关");
			wn.setCzUserId(null);
			if (totalDao.update(wn)) {// 更新库位状态
				borrow.setCqStatus("已取");
				totalDao.update(borrow);
			} else
				totalDao.update2(wn);
			return "true";
		}
		return "连接异常，关门失败！";
	}

	@Override
	public String oaCloseW(WarehouseNumber wn, Also also) {
		// TODO Auto-generated method stub
		String message = UtilTong.OCKuWei(wn.getIp(), duankou, false, wn
				.getOneNumber(), wn.getNumid());// 关门操作
		if ("true".equals(message)) {
			wn.setKwStatus("关");
			wn.setCzUserId(null);
			if (totalDao.update(wn)) {// 更新库位状态
				also.setCqStatus("已存");
				totalDao.update(also);
			} else
				totalDao.update2(wn);
			return "true";
		}
		return "连接异常，关门失败！";
	}

	/**
	 * 关闭未关的门
	 * 
	 * @param users
	 * @param ids
	 * @return
	 */
	public boolean closeWei(Users users, int ids) {
		boolean b = false;
		boolean b1 = false;
		// 查询其他库门有没有开的。
		List<WarehouseNumber> numbers = totalDao
				.query(
						"from WarehouseNumber where czUserId = ? and kwStatus = '开' and id in (select fk_ware_id from WareBangGoogs where fk_waigouDeliveryDetail_id = ?)",
						users.getId(), ids);
		if (numbers != null && numbers.size() > 0) {// 如果有，将此前的所有开的门都关闭
			b = true;
			for (WarehouseNumber wnb : numbers) {
				if (b1)
					sleeps(9000);
				String mes = UtilTong.OCKuWei(wnb.getIp(), duankou, false, wnb
						.getOneNumber(), wnb.getNumid());// 开门操作
				if ("true".equals(mes)) {
					wnb.setKwStatus("关");
					wnb.setCzUserId(null);
					if (totalDao.update(wnb)) {// 更新库位状态
					} else
						totalDao.update2(wnb);
				}
				b1 = true;
			}
		}
		return b;
	}

	// 休眠9秒
	public void sleeps(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String oaOpenW(String wn, String status) {
		// TODO Auto-generated method stub
		if ("lingliao".equals(status)) {
			status = "入库";
		} else {
			status = "待检验";
		}
		WareBangGoogs bGs = (WareBangGoogs) totalDao.getObjectByCondition(
				"from WareBangGoogs where towCode = ? and status = ?", wn,
				status);
		if (bGs == null)
			return "数据为空，开门失败！";
		Users users = Util.getLoginUser();
		// 查询其他库门有没有开的。
		if (bGs.getFk_waigouDeliveryDetail_id() != null
				&& bGs.getFk_waigouDeliveryDetail_id() > 0) {
			if (closeWei(users, bGs.getFk_waigouDeliveryDetail_id()))
				sleeps(4000);
		}
		// 打开选择的库位
		WarehouseNumber wN = (WarehouseNumber) totalDao.getObjectById(
				WarehouseNumber.class, bGs.getFk_ware_id());
		if (wN == null)
			return "库位为空，开门失败！";
		if (!"开".equals(wN.getKwStatus())) {
			// 返回主页面，灯停止闪烁
			// 结束闪烁
			// Util.openColorXin(wN.getFourlightIp(), duankou, true, false, wN
			// .getNumid(), wN.getMarkTypt() == null ? 0 : wN
			// .getMarkTypt().getMarkColor());
			// 结束闪烁
			// UtilTong.openColorLight(wN.getIp(), duankou, false,
			// false,wN.getOneNumber(), wN
			// .getNumid(), wN.getMarkTypt() == null ? 0 : wN
			// .getMarkTypt().getMarkColor());
			// 灭灯
			UtilTong.closeLight(wN.getIp(), duankou, wN.getOneNumber(), wN
					.getNumid());
			// 亮灯的颜色
			String message = UtilTong.openColorLight(wN.getIp(), duankou, true,
					true, wN.getOneNumber(), wN.getNumid(),
					wN.getMarkTypt() == null ? 0 : wN.getMarkTypt()
							.getMarkColor());
			sleeps(500);
			UtilTong.backTowMa(wN.getIp(), duankou, wN.getOneNumber());
			sleeps(500);
			// 开门操作
			UtilTong.OCKuWei(wN.getIp(), duankou, true, wN.getOneNumber(), wN
					.getNumid());
			if ("true".equals(message)) {
				wN.setKwStatus("开");
				wN.setCzUserId(users.getId());
				if (totalDao.update(wN)) {// 更新库位状态
				} else
					totalDao.update2(wN);
				return "true";
			} else {
				return "连接异常，开门失败！";
			}
		}
		return "开门失败！";
	}

	@Override
	public String sendTowGz(WarehouseNumber wn, Integer id) {
		// TODO Auto-generated method stub
		String message = UtilTong.sendTowMa_1(wn.getIp(), wn.getOneNumber(),
				duankou, (id + mim) + "");
		if ("true".equals(message))
			return message;
		return "连接断开，发送失败！";
	}

	@Override
	public String sendTow(WarehouseNumber wn, Integer id) {

		// TODO Auto-generated method stub
		WareBangGoogs bangGoogs = (WareBangGoogs) totalDao
				.getObjectByCondition(
						"from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and fk_ware_id = ?",
						id, wn.getId());
		if (bangGoogs == null)
			return "数据为空，发送失败！";
		String message = UtilTong.sendTowMa_1(wn.getIp(), wn.getOneNumber(),
				duankou, bangGoogs.getTowCode());
		if ("true".equals(message)) {
			return message;
		}
		return "连接断开，发送失败！";
	}

	@Override
	public boolean Ostbdxjbz(OsTemplate ost) {
		if (ost != null) {
			OsTemplate oldost = (OsTemplate) totalDao.get(OsTemplate.class, ost
					.getId());
			Waigoujianpinci xjbz = (Waigoujianpinci) totalDao.get(
					Waigoujianpinci.class, ost.getXjbzId());
			oldost.setXjbzId(xjbz.getId());
			oldost.setXjbz(xjbz.getLeixing());
			return totalDao.update(oldost);
		}
		return false;
	}

	@Override
	public WaigoujianpinciZi findxunjianpici(WaigouDeliveryDetail waigoudd,
			OsTemplate osTemplate) {
		if (waigoudd != null && osTemplate != null) {
			Float Number = waigoudd.getQrNumber();
			if (waigoudd.getZhuanhuanNum() != null
					&& waigoudd.getZhuanhuanNum() > 0) {
				Number = waigoudd.getZhuanhuanNum();
			}
			String hql = " from WaigoujianpinciZi where waigoujianpinciId = "
					+ osTemplate.getXjbzId() + " and caigoushuliang1<="
					+ Number + " and caigoushuliang2 >= " + Number;
			return (WaigoujianpinciZi) totalDao.getObjectByCondition(hql);
		}

		return null;
	}

	@Override
	public List<Waigoujianpinci> findAllxjbzlist() {
		String hql = "from Waigoujianpinci where type = '巡检'";
		return totalDao.query(hql);
	}

	@Override
	public boolean updatejyz(WaigouDeliveryDetail waigoudd) {
		if (waigoudd != null) {
			if ("待检验".equals(waigoudd.getStatus())) {
				waigoudd.setStatus("检验中");
			}
			waigoudd.setJyuserName(Util.getLoginUser().getName());// 检验人姓名
			if (totalDao.update(waigoudd)) {
				List<WareBangGoogs> lists = totalDao
						.query(
								"from WareBangGoogs where fk_waigouDeliveryDetail_id = ?",
								waigoudd.getId());
				if (lists != null && lists.size() > 0) {
					for (WareBangGoogs wareBangGoogs : lists) {
						WarehouseNumber number = byIdWN(wareBangGoogs
								.getFk_ware_id());
						if (number == null)
							continue;
						// 闪烁
						UtilTong.openColorLight(number.getIp(), duankou, true,
								false, number.getOneNumber(),
								number.getNumid(),
								number.getMarkTypt() == null ? 0 : number
										.getMarkTypt().getMarkColor());
						// Util.openColorXin(number.getFourlightIp(), duankou,
						// true, true, number.getNumid(), number
						// .getMarkTypt() == null ? 0 : number
						// .getMarkTypt().getMarkColor());

					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public List<WaigouOrder> findAllDsh() {
		return totalDao.query("from WaigouOrder");// where status = '送货中'
	}

	@Override
	public Object[] findAlljyz(int pageNo, int pageSize) {
		String hql1 = "from WaigouDeliveryDetail where status = '检验中' and type='外购'";
		List list2 = totalDao.findAllByPage(hql1, pageNo, pageSize);
		int count = totalDao.getCount(hql1);
		Object[] o = { list2, count };
		return o;
	}

	@Override
	public List<WaigouDeliveryDetail> findAlljyz() {
		// Users user = Util.getLoginUser();
		// if (user == null) {
		// return null;
		// }
		List list1 = totalDao
				.query("from WaigouDeliveryDetail where status = '检验中' and type='外委'");
		// String hql =
		// "SELECT c.name from Category c join c.userSet u where u.id="
		// + user.getId();
		// List<String> strList = totalDao.query(hql);
		String categoryName = "";
		// if (strList == null || strList.size() == 0) {
		// categoryName = " and 1=1";
		// } else {
		// categoryName = " and wgType in(";
		// int i = 0;
		// for (String str : strList) {
		// if (i == 0) {
		// categoryName += "'" + str + "'";
		// } else {
		// categoryName += ",'" + str + "'";
		// }
		// i++;
		// }
		// categoryName += ")";
		// }
		// if (categoryName.length() > 0 && categoryName != null) {
		// }
		// categoryName = categoryName.substring(1);
		String hql1 = "from WaigouDeliveryDetail where status = '检验中' and type='外购' "
				+ categoryName;
		List list2 = totalDao.query(hql1);
		list1.addAll(list2);
		return list1;
		// String hql = " from WaigouDeliveryDetail where status = '检验中'";
		// return totalDao.findAllByPage(hql, 1, 1);
		// return totalDao.query(hql);
	}

	@Override
	public Object[] findAllDrk(WaigouDeliveryDetail wgdd, String pageStatus,
			int parseInt, int pageSize, String tag) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		List<WaigouDeliveryDetail> list1 = new ArrayList<WaigouDeliveryDetail>();
		String type = "'外购','研发'";
		if ("ww".equals(pageStatus)) {
			type = "'外委'";
		} else if ("muju".equals(pageStatus)) {
			type = "'模具'";
		} else if ("fl".equals(pageStatus)) {
			type = "'辅料'";
		}
		// else if ("yf".equals(pageStatus)) {
		// type = "研发";
		// }
		String str_hql = "";
		if (!"all".equals(tag)) {
			str_hql = " and  qrUsersId = " + user.getId();
		}
		String hql = "SELECT c.name from Category c join c.userSet u where u.id="
				+ user.getId();
		List<String> strList = totalDao.query(hql);
		String categoryName = " and 1=1 ";
		if ("'外购','研发'".equals(type)) {
			categoryName = " and wgType in(";
			int i = 0;
			if (strList == null || strList.size() == 0) {
				categoryName = "";
			} else {
				for (String str : strList) {
					if (i == 0) {
						categoryName += "'" + str + "'";
					} else {
						categoryName += ",'" + str + "'";
					}
					i++;
				}
			}
			categoryName += ")";
		}
		categoryName = " and 1=1 ";
		String yclSql = "";
		if (wgdd != null && wgdd.getWgType() != null
				&& !"".equals(wgdd.getWgType())) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgdd.getWgType());
			if (category != null) {
				getWgtype(category);
			}
			if (strbu.length() == 0) {
				yclSql = " and wgType in ('-1')";
			} else {
				yclSql = " and wgType in (" + strbu.toString().substring(1)
						+ ")";
			}
		}
		String markId = "";
		if (wgdd != null && wgdd.getMarkId() != null
				&& !"".equals(wgdd.getMarkId())) {
			markId = " and markId like '%" + wgdd.getMarkId() + "%' ";
		}

		if (categoryName.length() > 0 && categoryName != null) {
			categoryName = categoryName.substring(1);
			String hql1 = "from WaigouDeliveryDetail where status = '待入库' and hgNumber >0  and type in ("
					+ type + ") " + markId + categoryName + yclSql;
			list1 = totalDao.findAllByPage(hql1 + str_hql
					+ "order by wgType asc,markId asc", parseInt, pageSize);
			if (list1.size() > 0) {
				for (WaigouDeliveryDetail w : list1) {
					StringBuffer bf = new StringBuffer();
					String hql_goods = "select distinct(goodsPosition) from Goods where goodsMarkId = ? and kgliao = ? ";
					if (w.getBanben() != null && w.getBanben().length() > 0) {
						hql_goods += " and banBenNumber = '" + w.getBanben()
								+ "' ";
					}
					if (w.getType() != null
							&& ("外购".equals(w.getType()) || "研发".equals(w
									.getType()))) {
						hql_goods += " and goodsClass in ('外购件库','研发库') ";
					} else if (w.getType() != null && "外委".equals(w.getType())) {
						hql_goods += " and goodsClass = '外协库' ";
						Integer pno = Util.getSplitNumber(w.getProcessNo(),
								";", "max");
						String nextProcessName = (String) totalDao
								.getObjectByCondition(
										"select processName from ProcessInfor where (dataStatus is null or dataStatus!='删除') "
												+ " and procard.id in(select procardId from ProcardWGCenter where wgOrderId=? ) and processNO >? order by processNO",
										w.getWaigouPlanId(), pno);
						if (nextProcessName == null) {
							nextProcessName = "无";
						}
						w.setNextProcessName(nextProcessName);
					} else {
						hql_goods += " and goodsClass in ('综合库') ";
					}
					// hql_goods += " order by goodsCurQuantity desc";
					List<String> lists = null;
					if ("外委".equals(w.getType())) {
						lists = new ArrayList<String>();
						lists.add("外协库");
					} else {
						lists = totalDao.query(hql_goods, w.getMarkId(), w
								.getKgliao());
					}
					if (lists.size() > 0) {
						for (String s : lists) {
							if (s != null && !"".equals(s)) {
								WarehouseNumber ware1 = (WarehouseNumber) totalDao
										.getObjectByCondition(
												"from WarehouseNumber where number = ?",
												s);
								if (ware1 != null && ware1.getNumber() != null) {
									// bf
									// .append(ware1
									// .getWareHouseName()
									// + "-"
									// + ware1
									// .getWarehouseArea()
									// + "-"
									// + s
									// +
									// "; <a href='WaigouwaiweiPlanAction!querenKuBacode.action?bacode="
									// + ware1.getBarCode()
									// + "&id="
									// + w.getId()
									// + "&pageStatus="
									// + pageStatus
									// + " '>选择"
									// + s + "库位</a><br/>");
									bf
											.append(ware1.getWareHouseName()
													+ "-"
													+ ware1.getWarehouseArea()
													+ "-"
													+ s
													+ "; <input type='button' name='tuijian' style='margin: 5px; height: 45px;' value='选择"
													+ s
													+ "库位' onclick=\"javascript:this.disabled='disabled';"
													+ "window.location='WaigouwaiweiPlanAction!querenKuBacode.action?bacode="
													+ ware1.getBarCode()
													+ "&id=" + w.getId()
													+ "&pageStatus="
													+ pageStatus + "&tag="
													+ tag + "&cpage="
													+ parseInt
													+ "&checkTimes="+w.getCheckTime()+" ';zhengmu();\"><br/>");
								}
							}
						}
						w.setClassNames(bf.toString());
					} else {
						// bf
						// .append("外购件库-板材仓-B1101; <input type='button' value='选择B1101库位' onclick=\"javascript:this.disabled='disabled';"
						// +
						// "window.location='WaigouwaiweiPlanAction!querenKuBacode.action?bacode=62bf75a0-9864-430b-8afd-09ae1fb24431&id="
						// + w.getId()
						// + "&pageStatus="
						// + pageStatus + " ';zhengmu();\"><br/>");
						// w.setClassNames(bf.toString());
					}
				}
			}
			int count = totalDao.getCount(hql1 + str_hql);
			Object[] o = { list1, count };
			return o;
		}
		// list1 = totalDao
		// .query("from WaigouDeliveryDetail where status = '待入库' and type='"
		// + type + "' and hgNumber >0 ");
		return null;
	}

	@Override
	public String queRenSendTow(Integer id) {
		// TODO Auto-generated method stub
		List<WareBangGoogs> bangGoogs = findWareBangdign(false, id, "待确认");
		if (bangGoogs != null && bangGoogs.size() > 0) {
			if (bangGoogs.size() == 1) {
				WarehouseNumber wn = byIdWN(bangGoogs.get(0).getFk_ware_id());
				if (wn == null)
					return "库位不存在";
				// 闪烁
				// Util.openColorXin(wn.getFourlightIp(), duankou, true, true,
				// wn
				// .getNumid(), wn.getMarkTypt() == null ? 0 : wn
				// .getMarkTypt().getMarkColor());
				UtilTong.openColorLight(wn.getIp(), duankou, true, false, wn
						.getOneNumber(), wn.getNumid(),
						wn.getMarkTypt() == null ? 0 : wn.getMarkTypt()
								.getMarkColor());
				String message = UtilTong
						.sendTowMa_1(wn.getIp(), wn.getOneNumber(), duankou,
								bangGoogs.get(0).getTowCode());
				if ("true".equals(message)) {
					return message;
				}
				return "连接异常，确认码发送失败！";
			} else
				return "trues";
		}
		return "未存柜";
	}

	@Override
	public List<WareBangGoogs> findWareBangdign(boolean b, Integer id) {
		// TODO Auto-generated method stub
		String hql = "";
		if (b)
			hql = "from WareBangGoogs where fk_ware_id = ?";
		else
			hql = "from WareBangGoogs where fk_waigouDeliveryDetail_id = ?";
		return totalDao.query(hql, id);
	}

	@Override
	public List<WareBangGoogs> findWareBangdign(boolean b, Integer id,
			String status) {
		// TODO Auto-generated method stub
		String hql = "";
		if (b)
			hql = "from WareBangGoogs where fk_ware_id = ? and status = ?";
		else
			hql = "from WareBangGoogs where fk_waigouDeliveryDetail_id = ? and (status = ? or status = '确认中')";
		return totalDao.query(hql, id, status);
	}

	@Override
	public List findRuGuiWN(Integer id) {
		// TODO Auto-generated method stub
		List<WareBangGoogs> bangGoogs = findWareBangdign(false, id, "待确认");
		List<WarehouseNumber> listwn = new ArrayList<WarehouseNumber>();
		for (WareBangGoogs wbg : bangGoogs) {
			WarehouseNumber number = byIdWN(wbg.getFk_ware_id());
			listwn.add(number);
		}
		return listwn;
	}

	@Override
	public WareBangGoogs byTowCodeWBG(String str) {
		// TODO Auto-generated method stub
		return (WareBangGoogs) totalDao.getObjectByCondition(
				"from WareBangGoogs where towCode = ?", str);
	}

	@Override
	public Borrow byIdBorrow(Integer id) {
		// TODO Auto-generated method stub
		return (Borrow) totalDao.getObjectById(Borrow.class, id);
	}

	@Override
	public String upBorrowGzBacode(String bacode) {
		// TODO Auto-generated method stub
		Borrow borrow = byIdBorrow(Integer.parseInt(bacode) - mim);
		if (borrow == null)
			return "二维码内容有误";
		WarehouseNumber wN = byIdWN(borrow.getWare_id());
		if (wN == null)
			return "库位不存在";
		// 结束闪烁
		// 灭灯
		UtilTong.closeLight(wN.getIp(), duankou, wN.getOneNumber(), wN
				.getNumid());
		// 亮灯的颜色
		UtilTong.openColorLight(wN.getIp(), duankou, true, true, wN
				.getOneNumber(), wN.getNumid(), wN.getMarkTypt() == null ? 0
				: wN.getMarkTypt().getMarkColor());
		// Util.openColorXin(wN.getFourlightIp(), duankou, true, false, wN
		// .getNumid(), wN.getMarkTypt() == null ? 0 : wN.getMarkTypt()
		// .getMarkColor());
		// 返回主页面
		UtilTong.backTowMa(wN.getIp(), duankou, wN.getOneNumber());
		sleeps(1000);
		// 开门
		String messages = UtilTong.OCKuWei(wN.getIp(), duankou, true, wN
				.getOneNumber(), wN.getNumid());// 开关门操作
		if ("true".equals(messages)) {
			wN.setKwStatus("开");
			wN.setCzUserId(Util.getLoginUser().getId());
			wN.setSczTime(Util.getDateTime());
			if (totalDao.update(wN)) {// 更新库位状态
				// 改变库存信息，改变借信息，删除表关系，更新屏幕消息
				borrow.setCqStatus("取中");
				if (totalDao.update(borrow)) {
					WareBangGoogs googs = (WareBangGoogs) totalDao
							.getObjectByCondition(
									"from WareBangGoogs where fk_ware_id = ? and fk_store_id = ? and fk_oadetail_id = ?",
									wN.getId(), borrow.getStore().getId(),
									borrow.getOa_id());
					if (googs != null)
						totalDao.delete(googs);
					AttendanceTowServerImpl.sendPin_1(wN);
				}
			}
			return messages;
		}
		return "开门失败";
	}

	@Override
	public Also byIdAlso(Integer id, String s) {
		// TODO Auto-generated method stub
		return (Also) totalDao.getObjectByCondition(
				"from Also where id = ? and cqStatus = ?", id, s);
	}

	@Override
	public String upAlsoBacode(String bacode, Also also, String tag) {
		// TODO Auto-generated method stub
		boolean b = false;
		if (bacode != null) {
			WarehouseNumber wN = (WarehouseNumber) totalDao
					.getObjectByCondition(
							"from WarehouseNumber where barCode = ?", bacode);
			if (wN == null)
				return "库位不存在";
			if ("无".equals(wN.getHave()) && !"开".equals(wN.getKwStatus()))
				b = true;
			else {
				if ("有".equals(wN.getHave()) && !"开".equals(wN.getKwStatus())) {
					if ("未满".equals(wN.getStatus())
							&& ("工装".equals(wN.getMarkTyptName())))
						b = true;
					else
						return "库位已满或此库位不为工装库，开门失败！";
				} else
					return "库位已打开，请先关闭！";
			}
			if (b) {
				// 返回主页面
				UtilTong.backTowMa(wN.getIp(), duankou, wN.getOneNumber());
				sleeps(1000);
				// 开门
				String messages = UtilTong.OCKuWei(wN.getIp(), duankou, true,
						wN.getOneNumber(), wN.getNumid());// 开关门操作
				if ("true".equals(messages)) {
					wN.setKwStatus("开");
					wN.setCzUserId(Util.getLoginUser().getId());
					wN.setSczTime(Util.getDateTime());
					totalDao.update(wN);// 更新状态
					// 添加中间表 建立关系
					WareBangGoogs bwg = new WareBangGoogs();
					bwg.setFk_oadetail_id(also.getBorrow().getOa_id());// 采购明细单ID
					bwg.setFk_store_id(also.getStore().getId());// 库存ID
					bwg.setFk_ware_id(wN.getId());
					bwg.setNumber(also.getProcessQuantity().floatValue());// 可操作数量
					bwg.setTowCode(UUID.randomUUID().toString());
					bwg.setStatus("入库");
					if (totalDao.save(bwg)) {// 添加成功后 更新库位状态。
						also.setWareHouse(wN.getNumber());
						also.setWare_id(wN.getId());
						also.setCqStatus("存中");
						if (totalDao.update(also)) {
							AttendanceTowServerImpl.sendPin_1(wN);
							return "true";
						}
					} else
						return "归还保存失败，请重试！";
				} else
					return "开门失败，请检查网络！";
			}
		}
		return null;
	}

	@Override
	public WaigouPlan getWaigouPlanById(Integer id) {
		if (id != null) {
			return (WaigouPlan) totalDao.get(WaigouPlan.class, id);
		}
		return null;
	}

	@Override
	public List<WaigouPlan> findwgListBymarkId(String markId) {
		if (markId != null && !"".equals(markId)) {
			String hql = "from WaigouPlan where  waigouOrder.id in  (select id from WaigouOrder where id in (select waigouOrder.id from WaigouPlan where markId = ?) and status  in ('待核对')) and markId = ? and waigouOrder.addUserCode=?";
			return totalDao.query(hql, markId, markId, Util.getLoginUser()
					.getCode());
		}
		return null;
	}

	@Override
	public String xiugaimxgys(Integer id, Integer priceId, WaigouOrder wgorder) {
		if (id != null) {
			String msg = "";
			Users user = Util.getLoginUser();
			if (user == null) {
				return "请先登录!";
			}
			String newdate = Util.getDateTime("yyyy-MM-dd");
			WaigouPlan wgplan = (WaigouPlan) totalDao.get(WaigouPlan.class, id);
			WaigouOrder oldwgorder = wgplan.getWaigouOrder();
			// if (!user.getCode().equals(oldwgorder.getAddUserCode())) {
			// return "您不是此订单采购员，无法修改此订单的供应商!";
			// }
			if (wgorder != null) {
				wgorder = (WaigouOrder) totalDao.get(WaigouOrder.class, wgorder
						.getId());
				if (wgorder.getGysId().equals(wgplan.getGysId())) {
					return "待更改供应商和之前所属供应商一样，无须更改!";
				}
				WaigouPlan wgplan1 = null;// 查询之前有没有对应的订单
				if (wgplan.getType().equals("外购")) {
					String hql_wgplan1 = " from WaigouPlan where waigouOrder.id = ? and markId = ? and waigouOrder.addUserCode=? and status = '待核对' ";
					wgplan1 = (WaigouPlan) totalDao.getObjectByCondition(
							hql_wgplan1, wgorder.getId(), wgplan.getMarkId(),
							user.getCode());
				} else {
					String hql_wgplan1 = " from WaigouPlan where waigouOrder.id = ? and markId = ? and waigouOrder.addUserCode=? and processNOs=? and wwType=? and status = '待核对' and addTime > '"
							+ newdate + "'";
					wgplan1 = (WaigouPlan) totalDao.getObjectByCondition(
							hql_wgplan1, wgorder.getId(), wgplan.getMarkId(),
							user.getCode(), wgplan.getProcessNOs(), wgplan
									.getWwType());
				}
				if (wgplan1 != null) {
					wgplan1.setNumber(wgplan.getNumber() + wgplan1.getNumber());
					wgplan1.setSyNumber(wgplan1.getSyNumber()
							+ wgplan.getNumber());
					wgplan1
							.setMoney(wgplan1.getNumber()
									* wgplan1.getHsPrice());
					// wgplan.setNumber(0F);
					// wgplan.setSyNumber(0F);
					// wgplan.setRemark(user.getName() + "把此件号从"
					// + wgplan.getWaigouOrder().getPlanNumber() + "订单  的"
					// + wgplan.getGysName() + " 转到"
					// + wgorder.getPlanNumber() + "订单，的 "
					// + wgorder.getGysName() + "下。");
					// totalDao.update(wgplan);
					totalDao.update(wgplan1);
					if ("外委".equals(wgplan1.getType())) {
						List<ProcardWGCenter> wgCenterList = totalDao.query(
								"from ProcardWGCenter where wgOrderId=?",
								wgplan.getId());
						if (wgCenterList != null && wgCenterList.size() > 0) {
							for (ProcardWGCenter wgcenter : wgCenterList) {
								wgcenter.setWgOrderId(wgplan1.getId());
								totalDao.save(wgcenter);
							}
						}
					}
					if (wgplan.getMopId() != null) {
						// 修改供应商修改物料需求明细明细对应关系
						ManualOrderPlan mop0 = (ManualOrderPlan) totalDao.get(
								ManualOrderPlan.class, wgplan.getMopId());
						List<ManualOrderPlanDetail> modList = totalDao
								.query(
										" from ManualOrderPlanDetail where manualPlan.id = ?",
										mop0.getId());
						ManualOrderPlan mop1 = (ManualOrderPlan) totalDao.get(
								ManualOrderPlan.class, wgplan1.getMopId());
						Float number = wgplan.getNumber();
						for (ManualOrderPlanDetail mod : modList) {
							if (number > 0) {
								ManualOrderPlanDetail mod0 = new ManualOrderPlanDetail();
								BeanUtils.copyProperties(mod, mod0,
										new String[] { "id", "manualPlan",
												"cgnumber", "outcgNumber" });
								Float cgNumber = mod.getCgnumber();
								if (number < mod.getCgnumber()) {
									mod0.setCgnumber(number);
									mod.setCgnumber(cgNumber - number);
									mod0.setOutcgNumber(number);
									number -= number;
								} else {
									mod0.setCgnumber(mod.getCgnumber());
									mod0.setOutcgNumber(mod.getCgnumber());
									mod.setCgnumber(0f);
									mod.setProcardId(null);// 关系必须清理
									number -= mod0.getCgnumber();
								}
								mod.setRemarks("修改供应商；数量由" + cgNumber + "变为"
										+ mod.getCgnumber());
								mop0.setNumber(mop0.getNumber()
										- mod0.getCgnumber());
								mop0.setOutcgNumber(mop0.getOutcgNumber()
										- mod0.getCgnumber());
								totalDao.update(mod);
								totalDao.update(mop0);
								mod0.setManualPlan(mop1);
								totalDao.save(mod0);
								mop1.setNumber(mop1.getNumber()
										+ mod0.getCgnumber());
								mop1.setOutcgNumber(mop1.getOutcgNumber()
										+ mod0.getCgnumber());
								totalDao.update(mop1);
							} else {
								break;
							}
						}
					}
					Set<WaigouPlan> wwpSet = oldwgorder.getWwpSet();
					if (wwpSet != null && wwpSet.size() > 0) {
						wwpSet.remove(wgplan);
						oldwgorder.setWwpSet(wwpSet);
						totalDao.delete(wgplan);
					}
					if (wwpSet == null || wwpSet.size() == 0) {
						totalDao.delete(wgplan);
						totalDao.delete(oldwgorder);
						return "修改成功;该订单明细已经转入新的订单，订单号为："
								+ wgorder.getPlanNumber();
					}
					if (totalDao.update(oldwgorder)) {
						return "修改成功;该订单明细已经转入新的订单，订单号为："
								+ wgorder.getPlanNumber();
					} else {
						return "修改失败";
					}
				} else {
					Price price = null;
					if (priceId == null) {
						String nowTime = Util.getDateTime();
						String str = "";
						if (wgplan.getBanben() != null
								&& !"".equals(wgplan.getBanben())) {
							str = " and banbenhao = '" + wgplan.getBanben()
									+ "'";
						} else {
							str = " and (banbenhao = '' or banbenhao is null)";
						}
						if ("外购".equals(wgplan.getType())) {
							str += " and kgliao = ? ";
						}
						String hql = " from Price where partNumber = ?  and pricePeriodStart<= '"
								+ nowTime
								+ "'and ( pricePeriodEnd >= '"
								+ nowTime
								+ "' or pricePeriodEnd is null  or pricePeriodEnd = '') and gysId = ? "
								+ str + "order by id desc";
						Price pricelist = null;
						if ("外购".equals(wgplan.getType())) {
							pricelist = (Price) totalDao.getObjectByCondition(
									hql, wgplan.getMarkId(),
									wgorder.getGysId(), wgplan.getKgliao());
						} else if ("外委".equals(wgplan.getType())) {
							pricelist = (Price) totalDao
									.getObjectByCondition(hql, wgplan
											.getMarkId(), wgorder.getGysId());
						}
						if (pricelist != null) {
							price = pricelist;
						} else {
							return "此供应商无该件号价格合同，请检查。";
						}
					} else {
						price = (Price) totalDao.getObjectById(Price.class,
								priceId);
					}
					if (price == null)
						return "价格合同不存在，请重试。";
					WaigouPlan newwgplan = new WaigouPlan();
					ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
							.getGysId());
					BeanUtils.copyProperties(wgplan, newwgplan, new String[] {
							"id", "hsPrice", "price", "taxprice", "priceId",
							"userId", "userCode", "gysId", "gysName",
							"addTime", "wwDetailId", "waigouOrder", "epId" });
					newwgplan.setHsPrice(price.getHsPrice());
					newwgplan.setPrice(price.getBhsPrice());
					newwgplan.setTaxprice(price.getTaxprice());
					newwgplan.setPriceId(price.getId());
					newwgplan.setUserId(zhuser.getUserid());
					newwgplan.setUserCode(zhuser.getUsercode());
					newwgplan.setGysId(zhuser.getId());
					newwgplan.setAddTime(Util.getDateTime());
					newwgplan.setGysName(zhuser.getCmp());
					newwgplan.setGysjc(zhuser.getName());
					newwgplan.setMoney(newwgplan.getHsPrice()
							* newwgplan.getNumber());
					Set<WaigouPlan> wwset = wgorder.getWwpSet();
					totalDao.save(newwgplan);
					wwset.add(newwgplan);
					wgorder.setWwpSet(wwset);
					totalDao.update(wgorder);
					List<ProcardWGCenter> pwgList = (List<ProcardWGCenter>) totalDao
							.query(
									" from ProcardWGCenter where wgOrderId = ? ",
									wgplan.getId());
					for (ProcardWGCenter pwg : pwgList) {
						pwg.setWgOrderId(newwgplan.getId());
						totalDao.update(pwg);
					}
					Set<WaigouPlan> wwpSet = oldwgorder.getWwpSet();
					if (wwpSet != null && wwpSet.size() > 0) {
						wwpSet.remove(wgplan);
						totalDao.delete(wgplan);
						oldwgorder.setWwpSet(wwpSet);
					}
					if (wwpSet == null || wwpSet.size() == 0) {
						totalDao.delete(wgplan);
						if (totalDao.delete(oldwgorder)) {
							return "该订单明细已经转入新的订单，订单号为："
									+ wgorder.getPlanNumber();
						}
						return "供应商修改失败！";
					}
					if ("外委".equals(newwgplan.getType())
							&& newwgplan.getWwDetailId() != null) {
						ProcessInforWWApplyDetail wwdetail = (ProcessInforWWApplyDetail) totalDao
								.get(ProcessInforWWApplyDetail.class, newwgplan
										.getWwDetailId());
						wwdetail.setGysName(newwgplan.getGysName());
						wwdetail.setPriceId(newwgplan.getPriceId());
						totalDao.update(wwdetail);
					}
					if (totalDao.update(oldwgorder)) {
						return "该订单明细已经转入新的订单，订单号为：" + wgorder.getPlanNumber();
					} else {
						return false + "";
					}
				}
			} else if (priceId != null) {
				Price price = (Price) totalDao.get(Price.class, priceId);
				if (price.getGysId() != null
						&& oldwgorder.getGysId().equals(price.getGysId())) {
					return "待更改供应商和之前所属供应商一样，无须更改!";
				}
				String hql = "from WaigouOrder where status = '待核对' and gysId = ? and addUserCode=? ";
				WaigouOrder wgorder1 = (WaigouOrder) totalDao
						.getObjectByCondition(hql, price.getGysId(), user
								.getCode());
				if (wgorder1 != null) {
					String hql_wgplan1 = " from WaigouPlan where waigouOrder.id = ? and markId = ? and status = '待核对' and addTime > '"
							+ newdate + "'";
					WaigouPlan wgplan1 = (WaigouPlan) totalDao
							.getObjectByCondition(hql_wgplan1,
									wgorder1.getId(), wgplan.getMarkId());
					if (wgplan1 != null) {
						wgplan1.setNumber(wgplan.getNumber()
								+ wgplan1.getNumber());
						wgplan1.setSyNumber(wgplan1.getSyNumber()
								+ wgplan.getNumber());
						wgplan1.setMoney(wgplan1.getNumber()
								* wgplan1.getHsPrice());
						// wgplan.setNumber(0F);
						// wgplan.setSyNumber(0F);
						// wgplan.setRemark(user.getName() + "把此件号从"
						// + wgplan.getWaigouOrder().getPlanNumber()
						// + "订单  的" + wgplan.getGysName() + " 转到"
						// + wgorder.getPlanNumber() + "订单，的 "
						// + wgorder.getGysName() + "下。");
						// totalDao.update(wgplan) ;
						totalDao.update(wgplan1);
						List<ProcardWGCenter> pwgList = (List<ProcardWGCenter>) totalDao
								.query(
										" from ProcardWGCenter where wgOrderId = ? ",
										wgplan.getId());
						for (ProcardWGCenter pwg : pwgList) {
							pwg.setWgOrderId(wgplan1.getId());
							totalDao.update(pwg);
						}

						// 修改供应商修改物料需求明细明细对应关系
						ManualOrderPlan mop0 = (ManualOrderPlan) totalDao.get(
								ManualOrderPlan.class, wgplan.getMopId());
						List<ManualOrderPlanDetail> modList = totalDao
								.query(
										" from ManualOrderPlanDetail where manualPlan.id = ?",
										mop0.getId());
						ManualOrderPlan mop1 = (ManualOrderPlan) totalDao.get(
								ManualOrderPlan.class, wgplan1.getMopId());
						Float number = wgplan.getNumber();
						for (ManualOrderPlanDetail mod : modList) {
							if (number > 0) {
								ManualOrderPlanDetail mod0 = new ManualOrderPlanDetail();
								BeanUtils.copyProperties(mod, mod0,
										new String[] { "id", "manualPlan",
												"cgnumber", "outcgNumber" });
								Float cgNumber = mod.getCgnumber();
								if (number < mod.getCgnumber()) {
									mod0.setCgnumber(number);
									mod.setCgnumber(cgNumber - number);
									number -= number;
								} else {
									mod0.setCgnumber(mod.getCgnumber());
									mod.setCgnumber(0f);
									number -= mod.getCgnumber();
								}
								mod.setRemarks("修改供应商；数量由" + cgNumber + "变为"
										+ mod.getCgnumber());
								mop0.setNumber(mop0.getNumber()
										- mod0.getCgnumber());
								mop0.setOutcgNumber(mop0.getOutcgNumber()
										- mod0.getCgnumber());
								totalDao.update(mod);
								totalDao.update(mop0);
								mod0.setManualPlan(mop1);
								totalDao.save(mod0);
								mop1.setNumber(mop1.getNumber()
										+ mod0.getCgnumber());
								mop1.setOutcgNumber(mop1.getOutcgNumber()
										+ mod0.getCgnumber());
								totalDao.update(mop1);
							} else {
								break;
							}
						}

						Set<WaigouPlan> wwpSet = oldwgorder.getWwpSet();
						if (wwpSet != null && wwpSet.size() > 0) {
							wwpSet.remove(wgplan);
							totalDao.delete(wgplan);
							oldwgorder.setWwpSet(wwpSet);
						}
						if (wwpSet == null || wwpSet.size() == 0) {
							totalDao.delete(wgplan);
							return totalDao.delete(oldwgorder) + "";
						}
						if ("外委".equals(wgplan1.getType())
								&& wgplan1.getWwDetailId() != null) {
							ProcessInforWWApplyDetail wwdetail = (ProcessInforWWApplyDetail) totalDao
									.get(ProcessInforWWApplyDetail.class,
											wgplan1.getWwDetailId());
							wwdetail.setGysName(wgplan1.getGysName());
							wwdetail.setPriceId(wgplan1.getPriceId());
							totalDao.update(wwdetail);
						}
						if (totalDao.update(oldwgorder)) {
							return "修改成功;该订单明细已经转入新的订单，订单号为："
									+ wgorder1.getPlanNumber();
						} else {
							return false + "";
						}
					} else {
						WaigouPlan newwgplan = new WaigouPlan();
						ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class,
								price.getGysId());
						BeanUtils.copyProperties(wgplan, newwgplan,
								new String[] { "id", "hsPrice", "price",
										"taxprice", "priceId", "userId",
										"userCode", "gysId", "gysName",
										"addTime", "wwDetailId", "waigouOrder",
										"epId", "money" });
						newwgplan.setHsPrice(price.getHsPrice());
						newwgplan.setPrice(price.getBhsPrice());
						newwgplan.setTaxprice(price.getTaxprice());
						Double money = price.getHsPrice()
								* newwgplan.getNumber();
						newwgplan.setMoney(money);
						newwgplan.setPriceId(price.getId());
						newwgplan.setUserId(zhuser.getUserid());
						newwgplan.setUserCode(zhuser.getUsercode());
						newwgplan.setGysId(zhuser.getId());
						newwgplan.setAddTime(Util.getDateTime());
						newwgplan.setGysName(zhuser.getCmp());
						newwgplan.setGysjc(zhuser.getName());
						Set<WaigouPlan> wwset = wgorder1.getWwpSet();
						wwset.add(newwgplan);
						wgorder1.setWwpSet(wwset);
						totalDao.update(wgorder1);
						List<ProcardWGCenter> pwgList = (List<ProcardWGCenter>) totalDao
								.query(
										" from ProcardWGCenter where wgOrderId = ? ",
										wgplan.getId());
						for (ProcardWGCenter pwg : pwgList) {
							pwg.setWgOrderId(newwgplan.getId());
							totalDao.update(pwg);
						}
						Set<WaigouPlan> wwpSet = oldwgorder.getWwpSet();
						if (wwpSet != null && wwpSet.size() > 0) {
							wwpSet.remove(wgplan);
							totalDao.delete(wgplan);
							oldwgorder.setWwpSet(wwpSet);
						}
						if (wwpSet == null || wwpSet.size() == 0) {
							totalDao.delete(wgplan);
							return totalDao.delete(oldwgorder) + "";
						}
						if ("外委".equals(newwgplan.getType())
								&& newwgplan.getWwDetailId() != null) {
							ProcessInforWWApplyDetail wwdetail = (ProcessInforWWApplyDetail) totalDao
									.get(ProcessInforWWApplyDetail.class,
											newwgplan.getWwDetailId());
							wwdetail.setGysName(newwgplan.getGysName());
							wwdetail.setPriceId(newwgplan.getPriceId());
							totalDao.update(wwdetail);
						}
						if (totalDao.update(oldwgorder)) {
							return "修改成功;该订单明细已经转入新的订单，订单号为："
									+ wgorder1.getPlanNumber();
						} else {
							return false + "";
						}
					}
				} else {
					wgorder1 = new WaigouOrder();
					ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
							.getGysId());
					wgorder1.setGysId(zhuser.getId());// 供应商Id
					wgorder1.setUserId(zhuser.getUserid());// 供应商的用户id
					wgorder1.setUserCode(zhuser.getUsercode()); // 用户工号(供应商编号)
					wgorder1.setLxPeople(zhuser.getCperson());// 供应商联系人
					wgorder1.setGysName(zhuser.getCmp());// 供应商名称
					wgorder1.setGysPhone(zhuser.getCfax());// 供应商电话
					wgorder1.setStatus("待核对");//
					// 状态(待通知、待确认、待审核（外委订单需要审核）、生产中、送货中、完成)
					wgorder1.setCaigouMonth(Util.getDateTime("yyyy-MM") + "月");// 采购月份
					// 得到最大的采购计划编号
					String str = "OS";
					String maxNumber = updatecgOrderNumber(str);
					if ("外委".equals(oldwgorder.getType())) {
						String planNumber = oldwgorder.getPlanNumber();
						str = planNumber.substring(0, planNumber.length() - 11);
						maxNumber = cgwwOrderNumber(str);
					}
					wgorder1.setPlanNumber(maxNumber);// 采购计划单号(wg201412001)
					wgorder1.setAddTime(Util.getDateTime());// 添加时间
					wgorder1.setAddUserCode(user.getCode());// 添加人工号
					wgorder1.setAddUserName(user.getName());// 添加人姓名
					wgorder1.setAddUserPhone(user.getPassword()
							.getPhoneNumber());// 添加人电话
					wgorder1.setTzUserName(user.getName());
					wgorder1.setTzUserCode(user.getCode());// 采购员工号
					wgorder1.setWwSource(wgplan.getWwSource());
					wgorder1
							.setTzUserPhone(user.getPassword().getPhoneNumber());// 采购员电话
					wgorder1.setGhAddress(zhuser.getCompanydz());// 供货地址（供应商地址)
					wgorder1.setShAddress(Util.getLoginCompanyInfo()
							.getAddress());// 送货地址(公司地址)
					wgorder1.setWwType(wgplan.getWaigouOrder().getWwType());// 外委类型（工序外委,包工包料,外购）
					wgorder1.setType(wgplan.getWaigouOrder().getType());// 计划类型（外购、外委）
					wgorder1.setRootId(wgplan.getWaigouOrder().getRootId());// 总成Id
					wgorder1.setFax(Util.getLoginCompanyInfo().getFax());// 传真
					wgorder1.setProcessApplyId(wgplan.getWaigouOrder()
							.getProcessApplyId());// 工序外委表Id
					wgorder1.setPayType(zhuser.getFkfs());// 付款方式
					wgorder1.setRootMarkId(wgplan.getWaigouOrder()
							.getRootMarkId());// 总成件号
					wgorder1.setApplystatus("未申请");// 申请状态
					wgorder1.setYwMarkId(wgplan.getWaigouOrder().getYwMarkId());// 业务件号
					WaigouPlan newwgplan = new WaigouPlan();
					BeanUtils.copyProperties(wgplan, newwgplan, new String[] {
							"id", "hsPrice", "price", "taxprice", "priceId",
							"userId", "userCode", "gysId", "gysName",
							"addTime", "wwDetailId", "waigouOrder", "epId",
							"money", "applystatus" });
					newwgplan.setHsPrice(price.getHsPrice());
					newwgplan.setPrice(price.getBhsPrice());
					newwgplan.setTaxprice(price.getTaxprice());
					newwgplan.setPriceId(price.getId());
					newwgplan.setUserId(zhuser.getUserid());
					newwgplan.setUserCode(zhuser.getUsercode());
					newwgplan.setGysId(zhuser.getId());
					newwgplan.setAddTime(Util.getDateTime());
					newwgplan.setGysName(zhuser.getCmp());
					Double money = price.getHsPrice() * newwgplan.getNumber();
					newwgplan.setMoney(money);
					newwgplan.setGysjc(zhuser.getName());
					Set<WaigouPlan> wwset = new HashSet<WaigouPlan>();
					wwset.add(newwgplan);
					wgorder1.setWwpSet(wwset);
					totalDao.save(wgorder1);
					List<ProcardWGCenter> pwgList = (List<ProcardWGCenter>) totalDao
							.query(
									" from ProcardWGCenter where wgOrderId = ? ",
									wgplan.getId());
					for (ProcardWGCenter pwg : pwgList) {
						pwg.setWgOrderId(newwgplan.getId());
						totalDao.update(pwg);
					}

					// wgplan.setNumber(0F);
					// wgplan.setSyNumber(0F);
					// wgplan.setRemark(user.getName() + "把此件号从"
					// + wgplan.getWaigouOrder().getPlanNumber() + "订单  的"
					// + wgplan.getGysName() + " 转到"
					// + wgorder1.getPlanNumber() + "订单，的 "
					// + wgorder1.getGysName() + "下。");
					// totalDao.update(wgplan);
					Set<WaigouPlan> wwpSet = oldwgorder.getWwpSet();
					if (wwpSet != null && wwpSet.size() > 0) {
						wwpSet.remove(wgplan);
						oldwgorder.setWwpSet(wwpSet);
						totalDao.delete(wgplan);
					}
					if (wwpSet == null || wwpSet.size() == 0) {
						totalDao.delete(wgplan);
						totalDao.delete(oldwgorder);
					} else {
						totalDao.update(oldwgorder);
					}

					if ("外委".equals(newwgplan.getType())
							&& newwgplan.getWwDetailId() != null) {
						ProcessInforWWApplyDetail wwdetail = (ProcessInforWWApplyDetail) totalDao
								.get(ProcessInforWWApplyDetail.class, newwgplan
										.getWwDetailId());
						wwdetail.setGysName(newwgplan.getGysName());
						wwdetail.setPriceId(newwgplan.getPriceId());
						totalDao.update(wwdetail);
					}
					wgorder1.setApplystatus("未申请");
					if (wgorder1.getEpId() == null) {// 之前申请过了需要重新申请
						Integer epId = null;
						if (wgorder1.getType().equals("外购")) {
							// 外购采购订单进入审批
							try {
								epId = CircuitRunServerImpl.createProcess(
										"外购采购订单申请", WaigouOrder.class, wgorder1
												.getId(), "applystatus", "id",
										"WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=findAll&id="
												+ wgorder1.getId(), "外购采购订单申请",
										true);
							} catch (Exception e) {
								msg += "审批流程有误,请联系管理员!\\n";
							}
						} else if (wgorder1.getType().equals("外委")) {
							// 外委采购订单进入审批
							try {
								epId = CircuitRunServerImpl.createProcess(
										"外委采购订单申请", WaigouOrder.class, wgorder1
												.getId(), "applystatus", "id",
										"WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=findAll&id="
												+ wgorder1.getId(), "外委采购订单申请",
										true);
							} catch (Exception e) {
								msg += "审批流程有误,请联系管理员!";
							}
						} else if (wgorder1.getType().equals("辅料")) {
							// 外委采购订单进入审批
							try {
								epId = CircuitRunServerImpl.createProcess(
										"辅料采购订单申请", WaigouOrder.class, wgorder1
												.getId(), "applystatus", "id",
										"WaigouwaiweiPlanAction!findWgOrderList.action?pageStatus=findAll&tag=fl"
												+ wgorder1.getId(), "辅料采购订单申请",
										true);
							} catch (Exception e) {
								msg += "审批流程有误,请联系管理员!";
							}
						}
						CircuitRun circuitRun = (CircuitRun) totalDao
								.getObjectById(CircuitRun.class, epId);
						if (circuitRun.getAllStatus().equals("同意")) {
							wgorder1.setApplystatus("同意");
						}
						wgorder1.setEpId(circuitRun.getId());
					}
					if (totalDao.update(wgorder1)) {
						return "修改成功;该订单明细已经转入新的订单，订单号为："
								+ wgorder1.getPlanNumber();
					} else {
						return false + "";
					}
				}
			}
		}
		return "error";
	}

	/**
	 * 得到最大的采购计划编号
	 * 
	 * @return
	 */
	private String updatecgOrderNumber(String str) {
		if (str == null || "".equals(str)) {
			str = "OS";
		}
		String maxNumber = str + Util.getDateTime("yyyyMMdd");
		String hql_maxnumber = "select max(planNumber) from WaigouOrder where planNumber like '"
				+ maxNumber + "%'";
		Object obj = totalDao.getObjectByCondition(hql_maxnumber);
		if (obj != null) {
			String maxNumber2 = obj.toString();
			int num2 = Integer.parseInt(maxNumber2.substring(maxNumber2
					.length() - 4, maxNumber2.length()));
			num2++;
			if (num2 < 10) {
				maxNumber += "000" + num2;
			} else if (num2 < 100) {
				maxNumber += "00" + num2;
			} else if (num2 < 1000) {
				maxNumber += "0" + num2;
			} else {
				maxNumber += num2 + "";
			}
		} else {
			maxNumber += "0001";
		}
		return maxNumber;
	}

	/**
	 * 得到最大的采购计划编号
	 * 
	 * @return
	 */
	private String cgwwOrderNumber(String str) {
		if (str == null || "".equals(str)) {
			str = "OS";
		}
		String maxNumber = str + Util.getDateTime("yyyyMMdd");
		String hql_maxnumber = "select max(planNumber) from WaigouOrder where planNumber like '"
				+ maxNumber + "%'";
		Object obj = totalDao.getObjectByCondition(hql_maxnumber);
		if (obj != null) {
			String maxNumber2 = obj.toString();
			int num2 = Integer.parseInt(maxNumber2.substring(maxNumber2
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
		return maxNumber;
	}

	@Override
	public String querenKu(String bacode, Integer id, String mxId,String checkTimes) {
		WarehouseNumber wn = (WarehouseNumber) totalDao.getObjectByCondition(
				"from WarehouseNumber where barCode = ?", bacode);
		if (wn == null)
			return "库位不存在";

		if (id > 0) {
			mxId = id + ",";
		} else if (mxId != null && !"".equals(mxId)) {
		} else {
			return "入库失败,数据异常或者已入库!请刷新后重试!";
		}
		Users users = Util.getLoginUser();
		StringBuffer buffer = new StringBuffer();
		// mxId += "45879,4578,422,422,478,4578,987,65,32,56";
		String[] mxStr = mxId.split(",");
		String[] checkTimesArray = checkTimes.split(",");
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < mxStr.length; i++) {
			set.add(mxStr[i]);// ID去重复
		}
		int index = 0;
		for (String ss : set) {
			if (ss == null || "".equals(ss))
				continue;
			String checkTime = checkTimesArray[index];
			index++;
			WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
					.getObjectByCondition(
							"from WaigouDeliveryDetail where id = ? and status = '待入库' and yrukuNum is null ",
							Integer.parseInt(ss));
			if (detail != null) {
				/****
				 * 先验证数量是否已经存入
				 */
				if (detail.getYcNumber() != null
						&& detail.getYcNumber() >= detail.getHgNumber()) {
					return detail.getMarkId() + ":" + detail.getHgNumber()
							+ ";检测到重复入库行为,请刷新后重试!";
				}

				buffer.append(detail.getId() + "+" + detail.getStatus() + "|");
				if (detail.getType().equals("外购")) {
					if (wn.getWareHouseName() == null
							|| (!wn.getWareHouseName().equals("外购件库")
									&& !wn.getWareHouseName().equals("研发库") && !wn
									.getWareHouseName().equals("售后库"))) {
						buffer.append(" 外购件需入外购件库，该库位不属于外购件库 ");
						DoorBangDingServerImpl.caeLogInfor(buffer, users
								.getCardId(), mxId, users.getName(), users
								.getCode(), "入库", null, null);
						return "外购件需入外购件库，该库位不属于外购件库";
					}
				} else if (detail.getType().equals("外委")) {
					if (wn.getWareHouseName() == null
							|| !wn.getWareHouseName().equals("外协库")) {
						buffer.append(" 外协零件需入外协库，该库位不属于外协库 ");
						DoorBangDingServerImpl.caeLogInfor(buffer, users
								.getCardId(), mxId, users.getName(), users
								.getCode(), "入库", null, null);
						return "外协零件需入外协库，该库位不属于外协库";
					}
				} else if (detail.getType().equals("辅料")) {
					if (wn.getWareHouseName() == null
							|| !wn.getWareHouseName().equals("综合库")) {
						buffer.append("辅料需入综合库，该库位不属于综合库 ");
						DoorBangDingServerImpl.caeLogInfor(buffer, users
								.getCardId(), mxId, users.getName(), users
								.getCode(), "入库", null, null);
						return "辅料需入综合库，该库位不属于综合库";
					}
				}
			} else {
				detail = (WaigouDeliveryDetail) totalDao.get(
						WaigouDeliveryDetail.class, Integer.parseInt(ss));
				buffer.append("件号:" + detail.getMarkId() + "批次:"
						+ detail.getExamineLot() + " +入库失败,数据异常或者已入库!请刷新后重试! ");
				DoorBangDingServerImpl.caeLogInfor(buffer, users.getCardId(),
						mxId, users.getName(), users.getCode(), "入库", null,
						null);
				return "件号:" + detail.getMarkId() + "批次:"
						+ detail.getExamineLot() + "入库失败,数据异常或者已入库!请刷新后重试!";
			}
			try {
				String errorMessage = (String) ActionContext.getContext()
						.getApplication().get("shdRuku_" + detail.getId());
				if (errorMessage == null) {
					Users loginUser = Util.getLoginUser();
					ActionContext.getContext().getApplication().put(
							"shdRuku_" + detail.getId(),
							loginUser.getDept() + "的" + loginUser.getName()
									+ "正在入库该零件,请勿重复点击入库或刷新后重试!");
				} else {
					return errorMessage;
				}
				// 待检库出库
				String hql_ = " from GoodsStore where goodsStoreWarehouse = ? and wwddId=? and goodsStoreMarkId =?";
				GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_,
						"待检库", detail.getId(), detail.getMarkId());
				if (t != null) {
					String hql = "from Goods where goodsMarkId = ? ";
					if (t.getGoodsStoreWarehouse() != null
							&& t.getGoodsStoreWarehouse().length() > 0) {
						hql += " and goodsClass='" + t.getGoodsStoreWarehouse()
								+ "'";
					}
					if (t.getGoodHouseName() != null
							&& t.getGoodHouseName().length() > 0) {
						hql += " and goodHouseName='" + t.getGoodHouseName()
								+ "'";
					}
					if (t.getGoodsStorePosition() != null
							&& t.getGoodsStorePosition().length() > 0) {
						hql += " and goodsPosition='"
								+ t.getGoodsStorePosition() + "'";
					}
					if (t.getBanBenNumber() != null
							&& t.getBanBenNumber().length() > 0) {
						hql += " and banBenNumber='" + t.getBanBenNumber()
								+ "'";
					}
					if (t.getKgliao() != null && t.getKgliao().length() > 0) {
						hql += " and kgliao='" + t.getKgliao() + "'";
					}
					if (t.getGoodsStoreLot() != null
							&& t.getGoodsStoreLot().length() > 0) {
						hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
					}
					Goods s = (Goods) totalDao.getObjectByCondition(hql,
							new Object[] { t.getGoodsStoreMarkId() });
					if (s != null) {
						s.setGoodsCurQuantity(s.getGoodsCurQuantity()
								- detail.getHgNumber());
						if (s.getGoodsCurQuantity() < 0) {
							s.setGoodsCurQuantity(0f);
						}
						Sell sell = new Sell();
						sell.setSellMarkId(s.getGoodsMarkId());// 件号
						sell.setSellWarehouse(s.getGoodsClass());// 库别
						sell.setGoodHouseName(s.getGoodHouseName());// 仓区
						sell.setKuwei(s.getGoodsPosition());// 库位
						sell.setBanBenNumber(s.getBanBenNumber());// 版本号
						sell.setKgliao(s.getKgliao());// 供料属性
						sell.setProNumber(s.getProNumber());// 项目编号
						sell.setWgType(s.getWgType());// 物料类别
						sell.setSellGoods(s.getGoodsFullName());// 品名
						sell.setSellFormat(s.getGoodsFormat());// 规格
						sell.setSellCount(detail.getHgNumber());// 出库数量
						sell.setSellUnit(s.getGoodsUnit());// 单位
						sell.setSellSupplier(s.getGoodsSupplier());// 供应商
						sell.setSellTime(Util.getDateTime());// 出库时间
						sell.setTuhao(s.getTuhao());// 图号
						sell.setSellLot(s.getGoodsLotId());// 批次
						sell.setBedit(false);// 出库权限
						sell.setBedit(false);// 编辑权限
						sell.setPrintStatus("YES");// 打印状态
						sell.setStyle("待检库出库");
						totalDao.save(sell);
						totalDao.update(s);
					}
				}

				/**
				 * 开始入库;goodsStoreLot
				 */

				GoodsStore goodsStore = new GoodsStore();
				goodsStore.setGoodsStoreTime(Util.getDateTime());// 入库时间;
				goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));// 入库日期
				goodsStore.setGoodsStorePlanner(Util.getLoginUser().getName());// 操作人
				goodsStore.setStatus("入库");
				goodsStore.setStyle("采购入库");
				goodsStore.setGoodsStoreFormat(detail.getSpecification());// 规格
				goodsStore.setGoodsStoreMarkId(detail.getMarkId());// 件号
				goodsStore.setProNumber(detail.getProNumber());// 项目编号
				goodsStore.setGoodsStoreGoodsName(detail.getProName());// 名称
				goodsStore.setGoodsStoreUnit(detail.getUnit());// 单位
				goodsStore.setKgliao(detail.getKgliao());// 供料属性
				goodsStore.setBanBenNumber(detail.getBanben());// 版本号;
				goodsStore.setGoodsStoreSupplier(detail.getGysName());// 供应商名称
				goodsStore.setGysId(detail.getGysId() + "");// 供应商Id
				goodsStore.setHsPrice(detail.getHsPrice()==null?null:detail.getHsPrice().doubleValue());// 含税价格
				goodsStore.setGoodsStorePrice(detail.getPrice()==null?null:detail.getPrice().doubleValue());// 不含税价格
				goodsStore.setTaxprice(detail.getTaxprice());// 税率
				goodsStore.setGoodsStoreWarehouse(wn.getWareHouseName());// 所属仓库;
				goodsStore.setWgType(detail.getWgType());// 物料类别
				goodsStore.setGoodHouseName(wn.getWarehouseArea());// 所属仓区;
				if ("外购件库".equals(wn.getWareHouseName())) {
					YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
							.getObjectByCondition(
									" from YuanclAndWaigj where markId=? and kgliao=? and round is not null ",
									detail.getMarkId(), detail.getKgliao());
					if (wgj != null) {
						goodsStore.setGoodsStoreround(wgj.getRound());
						goodsStore.setGoodsStorelasttime(detail.getCheckTime());
						String nexttime = Util.getSpecifiedDayAfter(checkTime, wgj.getRound().intValue());
						goodsStore.setGoodsStorenexttime(nexttime);
					}
				}
				if ("模具".equals(detail.getType())) {
					goodsStore.setLendNeckStatus("借");
					goodsStore.setLendNeckCount(goodsStore.getGoodsStoreCount());
				} else if("辅料".equals(detail.getType())){
					OaAppDetailTemplate oate = (OaAppDetailTemplate) totalDao
							.getObjectByCondition(
									" from OaAppDetailTemplate where wlcode =? ",
									detail.getMarkId());
					if (oate != null) {
						goodsStore.setLendNeckStatus(oate.getLendNeckStatus());
						if ("借".equals(oate.getLendNeckStatus())) {
							goodsStore
									.setLendNeckCount(goodsStore.getGoodsStoreCount());
						}
					}
				}
				goodsStore.setGoodsStorePosition(wn.getNumber());// 所属库位
				goodsStore.setGoodsStoreSendId(detail.getWaigouDelivery()
						.getPlanNumber());// 送货单号
				goodsStore.setNeiorderId(detail.getCgOrderNum());// 采购订单号
				goodsStore.setWwddId(detail.getId());// 送货单明细Id;
				goodsStore.setGoodsStoreLuId(detail.getDemanddept());// 需求部门
				// 入库时即生成打印单号
				String printNumber = getMaxPrintNumber(goodsStore);
				// WaigouPlan waigouplan = (WaigouPlan)
				// totalDao.get(WaigouPlan.class,
				// detail.getWaigouPlanId());
				// if (waigouplan != null) {
				// goodsStore.setGoodsStoreProMarkId(waigouplan.getWaigouOrder()
				// .getRootMarkId());// 总成件号
				// }
				goodsStore.setPrintNumber(printNumber);// 打印单号
				goodsStore.setTuhao(detail.getTuhao());// 图号
				if(detail.getHsPrice()!=null){
					goodsStore.setMoney(detail.getHsPrice().doubleValue() * detail.getHgNumber());// 总额
				}
				goodsStore.setGoodsStoreCount(detail.getHgNumber());// 入库数量
				String hql_osRecord = " from OsRecord where wwddId =?";
				OsRecord osRecord = (OsRecord) totalDao.getObjectByCondition(
						hql_osRecord, detail.getId());
				if (osRecord != null) {
					goodsStore.setOsrecordId(osRecord.getId());

				}
				goodsStore.setGoodsStoreLot(detail.getExamineLot());
				/*** 添加入库记录、库存记录 ****/

				Goods goodss = goodsStoreServer.save(goodsStore, osRecord);
				/*** 入库激活以及送货单状态调整 ****/
				detail.setStatus("入库");
				detail.setYcNumber(detail.getHgNumber());// 已存数量
				goodsStoreServer.updateWaiProcard(goodsStore
						.getGoodsStoreMarkId(),
						goodsStore.getGoodsStoreCount(), "外购件库", goodsStore
								.getGoodsStoreId());
				detail.setPrintNumber(printNumber);
				totalDao.update(detail);
				// 类型为模具，入库之后，自动成工装报检表
				if ("模具".equals(detail.getType())) {
					MouldApplyOrder mao = (MouldApplyOrder) totalDao
							.getObjectByCondition(
									" from MouldApplyOrder where planNumber = ? ",
									detail.getMujuNumber());
					if (mao != null) {
						Set<MouldDetail> mdSet = mao.getMdSet();
						for (MouldDetail mouldDetail : mdSet) {
							Gzstore gzstore = new Gzstore();
							gzstore.setFk_stoid(goodss.getGoodsId());
							gzstore.setNumber("mj_" + mouldDetail.getMarkId());
							gzstore.setMatetag(mouldDetail.getProName());
							gzstore.setTotal(detail.getHgNumber());
							gzstore.setStorehouse(goodss.getGoodHouseName());
							gzstore.setPlace(goodss.getGoodsPosition());
							gzstore.setParClass("模具");
							gzstore.setCurAmount(detail.getHgNumber());
							gzstore.setMaxBorrowNum(detail.getHgNumber()
									.intValue());
							totalDao.save(gzstore);
						}

					}
				}
				/*** 生成应付账单信息 ***/
				corePayableServer.addCorePayable(detail, goodss);
				totalDao.clear();
			} catch (Exception e) {
				e.printStackTrace();
				ActionContext.getContext().getApplication().put(
						"shdRuku_" + "件号:" + detail.getMarkId() + "批次:"
								+ detail.getExamineLot(), null);
				return "入库失败!原因如下:" + e.getMessage();
			} finally {
				ActionContext.getContext().getApplication().put(
						"shdRuku_" + detail.getId(), null);
			}
		}
		// DoorBangDingServerImpl.caeLogInfor(buffer, users.getCardId(), mxId,
		// users.getName(), users.getCode(), "入库", null, null);
		return "true";
	}

	@Override
	public List<Price> findPrice(String markId, String kgliao, String banben) {
		String nowTime = Util.getDateTime();
		String str = "";
		if (banben != null && !"".equals(banben)) {
			str = " and banbenhao = '" + banben + "'";
		}
		String hql = " from Price where partNumber = ? and kgliao = ? and pricePeriodStart<= '"
				+ nowTime
				+ "'and ( pricePeriodEnd >= '"
				+ nowTime
				+ "' or pricePeriodEnd is null  or pricePeriodEnd = '')" + str;
		List<Price> priceList = totalDao.query(hql, markId, kgliao);
		List<Price> priceList2 = new ArrayList<Price>();
		for (Price price : priceList) {
			if (price.getGysId() != null) {
				ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
						.getGysId());
				if (zhuser != null) {
					price.setGys(zhuser.getCmp());
					priceList2.add(price);
				}
			}
		}
		return priceList2;
	}

	@Override
	public String querenjiaoqiAll(List wwPlanList) {
		String mes = "";
		WaigouOrder waigouOrder = null;
		if (wwPlanList != null && wwPlanList.size() > 0) {
			for (int i = 0; i < wwPlanList.size(); i++) {
				WaigouPlan wwplan = (WaigouPlan) wwPlanList.get(i);
				if (wwplan != null && wwplan.getId() != null) {
					WaigouPlan old = (WaigouPlan) totalDao.getObjectById(
							WaigouPlan.class, wwplan.getId());
					if (old != null
							&& old.getStatus() != null
							&& (old.getStatus().equals("待确认") || old
									.getStatus().equals("协商确认"))) {
						if (waigouOrder == null) {
							waigouOrder = old.getWaigouOrder();
						}
						old.setKuCunCount(wwplan.getKuCunCount());
						old.setJiaofuTime(wwplan.getJiaofuTime());
						if (old.getJiaofuTime() != null
								&& old.getJiaofuTime().length() > 0) {
							old.setStatus("协商确认");
							totalDao.update(old);
							mes = old.getMarkId() + ",确认完成.\n";
						} else {
							mes = old.getMarkId() + ",请填写交付日期.\n";
						}
					}
				}
			}
		}
		if (waigouOrder != null) {
			String hql_wgPlan = "from WaigouPlan where (jiaofuTime is null or jiaofuTime ='')and waigouOrder.id=?";
			Integer count = totalDao.getCount(hql_wgPlan, waigouOrder.getId());
			if (count <= 0) {
				// 通知采购员确认采购订单
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				AlertMessagesServerImpl.addAlertMessages("待确认"
						+ waigouOrder.getType() + "订单管理", "尊敬的【"
						+ waigouOrder.getTzUserName() + "】,您好:\n订单号:【"
						+ waigouOrder.getPlanNumber() + "】的交付日期已全部录入,请您确认。详询"
						+ waigouOrder.getLxPeople() + "("
						+ waigouOrder.getGysPhone() + ")。\n【"
						+ waigouOrder.getGysName() + "】\n["
						+ companyInfo.getName() + "]", "1", waigouOrder
						.getAddUserCode());
				// "helper");
			}
		}
		return mes;
	}

	@Override
	public String applyWWorder(Integer id) {
		// TODO Auto-generated method stub
		WaigouOrder wgorder = (WaigouOrder) totalDao.get(WaigouOrder.class, id);
		Integer epId = wgorder.getEpId();
		String r = "";
		if (wgorder.getType().equals("外购") || wgorder.getType().equals("辅料")
				|| wgorder.getType().equals("研发")) {
			r = "1";
			// 外购采购订单进入审批
			if (epId == null) {
				try {
					String type = wgorder.getType();
					String tag = "";
					if ("辅料".equals(type)) {
						tag = "fl";
					}
					epId = CircuitRunServerImpl.createProcess(type + "采购订单申请",
							WaigouOrder.class, wgorder.getId(), "applystatus",
							"id",
							"WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=findAll&id="
									+ wgorder.getId() + "&tag=" + tag, type
									+ "采购订单申请", true);
					if (epId != null && epId > 0) {
						wgorder.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							wgorder.setStatus("待通知");
							wgorder.setApplystatus("同意");
							/*********************** 计算订单总额 **************************/
							Double allMoney = 0d;
							if (wgorder.getWwpSet() != null
									&& wgorder.getWwpSet().size() > 0) {
								for (WaigouPlan wa : wgorder.getWwpSet()) {
									if (wa != null && wa.getMoney() != null)
										allMoney += wa.getMoney();
								}
							}
							allMoney = (double)Util.FomartDouble(allMoney, 4);
							wgorder.setAllMoney(allMoney);
							/*********************** 计算订单总额结束 **************************/
							/***********************
							 * 添加预付款申请单表 ************************** if (allMoney
							 * != null && allMoney > 0) {//
							 * totalDao.getObjectById
							 * (ZhUser.class,wgorder.getGysId()); ZhUser zhuser
							 * = (ZhUser) totalDao .getObjectByCondition(
							 * " from ZhUser where id=? and fkfs in ('货到付款','款到发货')"
							 * , wgorder.getGysId()); if (zhuser != null) { if
							 * (zhuser.getFkZhouQi() != null &&
							 * zhuser.getFkZhouQi() > 0) {
							 * PrepaymentsApplication prepayApp = new
							 * PrepaymentsApplication(); String newDate =
							 * Util.getDateTime();
							 * prepayApp.setAddTime(newDate);
							 * prepayApp.setAddTime(Util
							 * .getDateTime("yyyy-MM-dd")); Users u = (Users)
							 * totalDao .getObjectByCondition(
							 * "from Users where code = ? and name = ? and onWork <> '离职'"
							 * , wgorder .getAddUserCode(), wgorder
							 * .getAddUserName()); if (u == null) { u =
							 * Util.getLoginUser(); }
							 * prepayApp.setAddName(u.getName());// 添加人
							 * prepayApp.setAddDept(u.getDept());// 添加人部门 //
							 * prepayApp
							 * .setJbName(wgorder.getAddUserName());//申请人
							 * prepayApp.setYyName(wgorder .getGysName() + " " +
							 * wgorder.getCaigouMonth() + "预付款单");// 项目名称
							 * prepayApp.setPoNumber(wgorder
							 * .getPlanNumber());// 订单编号(PO编号)
							 * prepayApp.setNumber(ppNumber());// 预付款单编号 boolean
							 * bl = true; if (wgorder.getGysId() != null) { if
							 * (zhuser.getYufuBiLi() != null &&
							 * zhuser.getYufuBiLi() > 0) {
							 * prepayApp.setYfbl(zhuser .getYufuBiLi());
							 * prepayApp.setYfMoney(Util.FomartFloat((allMoney *
							 * zhuser.getYufuBiLi()/100), 4)); // 将数字转成中文
							 * prepayApp .setYfMoneyDX(NumberToCN
							 * .NumberCN(prepayApp .getYfMoney()
							 * .doubleValue())); } else { bl = false; //
							 * prepayApp.setYfMoney(allMoney); }
							 * prepayApp.setAllMoney(allMoney);// 采购总额 // 预计报销日期
							 * if (zhuser.getFkZhouQi() != null &&
							 * zhuser.getFkZhouQi() >= 0) { prepayApp
							 * .setExpectedTime(Util .getSpecifiedDayAfter(
							 * newDate, zhuser .getFkZhouQi())); }
							 * prepayApp.setStatus("待完善"); if (bl) {// 调用审批流程
							 * totalDao.save(prepayApp); // 调用审批流程 String
							 * processName = "预付款申请"; Integer epId1 = null; try
							 * { epId1 = CircuitRunServerImpl .createProcess(
							 * processName, PrepaymentsApplication.class,
							 * prepayApp .getId(), "status", "id", null//
							 * "zhaobiaoAction!toselectyufu.action?prepayApp.id=+ p.getId()"
							 * , prepayApp .getAddDept() + prepayApp
							 * .getAddName() + " 有预付款申请单，请您审批！", true); if
							 * (epId1 != null && epId1 > 0) { prepayApp
							 * .setEpId(epId1); CircuitRun circuitRun1 =
							 * (CircuitRun) totalDao .get( CircuitRun.class,
							 * epId1); if ("同意" .equals(circuitRun1
							 * .getAllStatus()) && "审批完成" .equals(circuitRun1
							 * .getAuditStatus())) { prepayApp .setStatus("同意");
							 * } else { prepayApp .setStatus("未审批"); } totalDao
							 * .update(prepayApp); } } catch (Exception e) {
							 * e.printStackTrace(); } } else { // 发消息 // RtxUtil
							 * // .sendNotify( // u.getCode(), // "您有 编号：" // +
							 * prepayApp // .getNumber() // +
							 * " 的预付款申请单信息待完善。(供应商：" // + zhuser // .getName()
							 * // + "的预付款比例和付款周期不完善。请及时处理！)", // "系统消息", "0", //
							 * "0"); } } } } } 添加预付款申请单表结束
							 **************************/
						} else {
							wgorder.setStatus("待审批");
							wgorder.setApplystatus("未审批");
						}
						totalDao.update(wgorder);
					}
				} catch (Exception e) {
					return "审批流程有误,请联系管理员!" + e.getMessage();
				}
			} else {
				boolean b = CircuitRunServerImpl.updateCircuitRun(epId);
				CircuitRun circuitRun = (CircuitRun) totalDao.get(
						CircuitRun.class, epId);
				if ("同意".equals(circuitRun.getAllStatus())
						&& "审批完成".equals(circuitRun.getAuditStatus())) {
					wgorder.setStatus("待通知");
					wgorder.setApplystatus("同意");
				} else {
					wgorder.setStatus("待审批");
					wgorder.setApplystatus("未审批");
				}
				totalDao.update(wgorder);
			}
		} else {
			r = "2";
			// 外委采购订单进入审批
			try {
				if (epId == null) {
					String processName = "外委采购订单申请";
					if (wgorder.getWwSource().equals("BOM外委")) {
						processName = "BOM外委采购订单申请";
					}
					epId = CircuitRunServerImpl.createProcess(processName,
							WaigouOrder.class, wgorder.getId(), "applystatus",
							"id",
							"WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=findAll&id="
									+ wgorder.getId(), processName, true);
					if (epId != null && epId > 0) {
						wgorder.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							wgorder.setStatus("待通知");
							wgorder.setApplystatus("同意");
							Set<WaigouPlan> wgpList = wgorder.getWwpSet();
							if (wgpList != null && wgpList.size() > 0) {
								for (WaigouPlan wp : wgpList) {
									wp.setStatus("待通知");
									totalDao.update(wp);
								}
							}
						} else {
							wgorder.setApplystatus("未审批");
						}
						totalDao.update(wgorder);
					}
				} else {
					boolean b = CircuitRunServerImpl.updateCircuitRun(epId);
				}
			} catch (Exception e) {
				return "审批流程有误,请联系管理员!";
			}
		}
		CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
				CircuitRun.class, epId);
		if (circuitRun.getAllStatus().equals("同意")) {
			wgorder.setApplystatus("同意");
		}
		wgorder.setEpId(epId);
		return totalDao.update(wgorder) + r;
	}

	@Override
	public String applyWWorder(Integer[] processIds) {
		// TODO Auto-generated method stub
		String r = "";
		if (processIds != null && processIds.length > 0) {
			for (Integer id : processIds) {
				WaigouOrder wgorder = (WaigouOrder) totalDao.get(
						WaigouOrder.class, id);
				Integer epId = wgorder.getEpId();
				if (wgorder.getType().equals("外购")
						|| wgorder.getType().equals("辅料")
						|| wgorder.getType().equals("研发")) {
					r = "1";
					// 外购采购订单进入审批
					if (epId == null) {
						try {
							String type = wgorder.getType();
							String tag = "";
							if ("辅料".equals(type)) {
								tag = "fl";
							}
							epId = CircuitRunServerImpl.createProcess(type
									+ "采购订单申请", WaigouOrder.class, wgorder
									.getId(), "applystatus", "id",
									"WaigouwaiweiPlanAction!findWgPlanList.action?pageStatus=findAll&id="
											+ wgorder.getId() + "&tag=" + tag,
									type + "采购订单申请", true);
							if (epId != null && epId > 0) {
								wgorder.setEpId(epId);
								CircuitRun circuitRun = (CircuitRun) totalDao
										.get(CircuitRun.class, epId);
								if ("同意".equals(circuitRun.getAllStatus())
										&& "审批完成".equals(circuitRun
												.getAuditStatus())) {
									wgorder.setStatus("待通知");
									wgorder.setApplystatus("同意");
									/*********************** 计算订单总额 **************************/
									Double allMoney = 0d;
									if (wgorder.getWwpSet() != null
											&& wgorder.getWwpSet().size() > 0) {
										for (WaigouPlan wa : wgorder
												.getWwpSet()) {
											if (wa != null
													&& wa.getMoney() != null)
												allMoney += wa.getMoney();
										}
									}
									allMoney = (double)Util.FomartDouble(allMoney, 4);
									wgorder.setAllMoney(allMoney);
									/*********************** 计算订单总额结束 **************************/
									/*********************** 添加预付款申请单表 **************************/
									// if (allMoney != null && allMoney > 0) {
									// PrepaymentsApplication prepayApp = new
									// PrepaymentsApplication();
									// String newDate = Util.getDateTime();
									// prepayApp.setAddTime(newDate);
									// prepayApp.setAddTime(Util
									// .getDateTime("yyyy-MM-dd"));
									// Users u = (Users) totalDao
									// .getObjectByCondition(
									// "from Users where code = ? and name = ? and onWork <> '离职'",
									// wgorder
									// .getAddUserCode(),
									// wgorder
									// .getAddUserName());
									// if (u == null) {
									// u = Util.getLoginUser();
									// }
									// prepayApp.setAddName(u.getName());// 添加人
									// prepayApp.setAddDept(u.getDept());//
									// 添加人部门
									// //
									// prepayApp.setJbName(wgorder.getAddUserName());//申请人
									// prepayApp.setYyName(wgorder
									// .getGysName()
									// + " "
									// + wgorder.getCaigouMonth()
									// + "预付款单");// 项目名称
									// prepayApp.setPoNumber(wgorder
									// .getPlanNumber());// 订单编号(PO编号)
									// prepayApp.setNumber(ppNumber());// 预付款单编号
									// boolean bl = true;
									// if (wgorder.getGysId() != null) {
									// ZhUser zhuser = (ZhUser) totalDao
									// .getObjectById(
									// ZhUser.class,
									// wgorder.getGysId());
									// if (zhuser.getYufuBiLi() != null
									// && zhuser.getYufuBiLi() > 0) {
									// prepayApp.setYfbl(zhuser
									// .getYufuBiLi());
									// prepayApp
									// .setYfMoney(Util
									// .towWei(allMoney
									// * zhuser
									// .getYufuBiLi()
									// / 100));
									// // 将数字转成中文
									// prepayApp
									// .setYfMoneyDX(NumberToCN
									// .NumberCN(prepayApp
									// .getYfMoney()
									// .doubleValue()));
									// } else {
									// bl = false;
									// prepayApp.setYfMoney(allMoney);
									// }
									// prepayApp.setAllMoney(allMoney);// 采购总额
									// // 预计报销日期
									// if (zhuser.getFkZhouQi() != null
									// && zhuser.getFkZhouQi() > 0) {
									// prepayApp
									// .setExpectedTime(Util
									// .getSpecifiedDayAfter(
									// newDate,
									// zhuser
									// .getFkZhouQi()));
									// } else {
									// bl = false;
									// }
									// prepayApp.setStatus("待完善");
									// totalDao.save(prepayApp);
									// if (bl) {// 调用审批流程
									// // 调用审批流程
									// String processName = "预付款申请";
									// Integer epId1 = null;
									// try {
									// epId1 = CircuitRunServerImpl
									// .createProcess(
									// processName,
									// PrepaymentsApplication.class,
									// prepayApp
									// .getId(),
									// "status",
									// "id",
									// null//
									// "zhaobiaoAction!toselectyufu.action?prepayApp.id=+ p.getId()"
									// ,
									// prepayApp
									// .getAddDept()
									// + prepayApp
									// .getAddName()
									// + " 有预付款申请单，请您审批！",
									// true);
									// if (epId1 != null
									// && epId1 > 0) {
									// prepayApp
									// .setEpId(epId1);
									// CircuitRun circuitRun1 = (CircuitRun)
									// totalDao
									// .get(
									// CircuitRun.class,
									// epId1);
									// if ("同意"
									// .equals(circuitRun1
									// .getAllStatus())
									// && "审批完成"
									// .equals(circuitRun1
									// .getAuditStatus())) {
									// prepayApp
									// .setStatus("同意");
									// } else {
									// prepayApp
									// .setStatus("未审批");
									// }
									// totalDao
									// .update(prepayApp);
									// }
									// } catch (Exception e) {
									// e.printStackTrace();
									// }
									// } else {
									// // 发消息
									// RtxUtil
									// .sendNotify(
									// u.getCode(),
									// "您有 编号："
									// + prepayApp
									// .getNumber()
									// + " 的预付款申请单信息待完善。(供应商："
									// + zhuser
									// .getName()
									// + "的预付款比例和付款周期不完善。请及时处理！)",
									// "系统消息", "0",
									// "0");
									// }
									// }
									// }
									/*********************** 添加预付款申请单表结束 **************************/
								} else {
									wgorder.setStatus("待审批");
									wgorder.setApplystatus("未审批");
								}
								totalDao.update(wgorder);
							}
						} catch (Exception e) {
							e.printStackTrace();
							return "审批流程有误,请联系管理员!";
						}
					} else {
						boolean b = CircuitRunServerImpl.updateCircuitRun(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							wgorder.setStatus("待通知");
							wgorder.setApplystatus("同意");
						} else {
							wgorder.setStatus("待审批");
							wgorder.setApplystatus("未审批");
						}
						totalDao.update(wgorder);
					}
				} else {
					r = "2";
					// 外委采购订单进入审批
					try {
						if (epId == null) {
							String processName = "外委采购订单申请";
							if (wgorder.getWwSource().equals("BOM外委")) {
								processName = "BOM外委采购订单申请";
							}
							epId = CircuitRunServerImpl.createProcess(
									processName, WaigouOrder.class, wgorder
											.getId(), "applystatus", "id",
									"WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=findAll&id="
											+ wgorder.getId(), processName,
									true);
							if (epId != null && epId > 0) {
								wgorder.setEpId(epId);
								CircuitRun circuitRun = (CircuitRun) totalDao
										.get(CircuitRun.class, epId);
								if ("同意".equals(circuitRun.getAllStatus())
										&& "审批完成".equals(circuitRun
												.getAuditStatus())) {
									wgorder.setStatus("待通知");
									wgorder.setApplystatus("同意");
									Set<WaigouPlan> wgpList = wgorder
											.getWwpSet();
									if (wgpList != null && wgpList.size() > 0) {
										for (WaigouPlan wp : wgpList) {
											wp.setStatus("待通知");
											totalDao.update(wp);
										}
									}
								} else {
									wgorder.setApplystatus("未审批");
								}
								totalDao.update(wgorder);
							}
						} else {
							boolean b = CircuitRunServerImpl
									.updateCircuitRun(epId);
						}
					} catch (Exception e) {
						return "审批流程有误,请联系管理员!";
					}
				}
				CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, epId);
				if (circuitRun.getAllStatus().equals("同意")) {
					wgorder.setApplystatus("同意");
				}
				wgorder.setEpId(epId);
				totalDao.update(wgorder);
			}
		}
		return "true" + r;
	}

	public String allUpdateJiaoFuTime(Integer id, String Time) {
		String hql = "From WaigouPlan";
		WaigouOrder waigouOrder = null;
		if (id != null && id > 0) {
			waigouOrder = (WaigouOrder) totalDao.getObjectById(
					WaigouOrder.class, id);
			if (waigouOrder == null)
				return "订单不存在确认失败！请检查！";
			hql += " where waigouOrder.id=" + id;
			List<WaigouPlan> waigouPlan = totalDao.find(hql);
			if (waigouPlan == null || waigouPlan.size() == 0)
				return "订单明细为空，无法确认时间！请检查！";
			for (WaigouPlan oldWaigouPlan : waigouPlan) {
				if (oldWaigouPlan != null
						&& (oldWaigouPlan.getStatus().equals("待确认") || oldWaigouPlan
								.getStatus().equals("协商确认"))) {
					oldWaigouPlan.setKuCunCount(0F);
					oldWaigouPlan.setJiaofuTime(Time);
					oldWaigouPlan.setQuerenTime(Util.getDateTime());
					oldWaigouPlan.setStatus("协商确认");
					totalDao.update(oldWaigouPlan);
				}
			}
			waigouOrder.setQuerenTime(Util.getDateTime());
			totalDao.update(waigouOrder);
			String hql_wgPlan = "from WaigouPlan where (jiaofuTime is null or jiaofuTime ='')and waigouOrder.id=?";
			Integer count = totalDao.getCount(hql_wgPlan, waigouOrder.getId());
			if (count <= 0) {
				// 通知采购员确认采购订单
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				AlertMessagesServerImpl.addAlertMessages("待确认"
						+ waigouOrder.getType() + "订单管理", "尊敬的【"
						+ waigouOrder.getAddUserName() + "】,您好:\n订单号:【"
						+ waigouOrder.getPlanNumber() + "】的交付日期已全部录入,请您确认。详询"
						+ waigouOrder.getLxPeople() + "("
						+ waigouOrder.getGysPhone() + ")。\n【"
						+ waigouOrder.getGysName() + "】\n["
						+ companyInfo.getName() + "]", "1", waigouOrder
						.getAddUserCode());
			}
		}
		return "交付时间确定成功";
	}

	@Override
	public Map<Integer, Object> getWwclDetail(String wwOrderNumber, Integer id) {
		// TODO Auto-generated method stub
		WaigouOrder order = null;
		Users user = Util.getLoginUser();
		boolean totalEnough = true;
		Map<String, Integer> flagMap = new HashMap<String, Integer>();
		Integer maxFlag = 0;
		String gysSql = " and 1=1";
		// if (user == null) {
		// throw new RuntimeException("请先登录");
		// } else if (user.getDept().equals("供应商")) {
		// totalEnough = false;// 供应商无出库权限
		// gysSql = " and userId=" + user.getId();
		// }

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
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if (order != null) {
			map.put(1, order);
			// 获取已出库的明细
			Set<WaigouPlan> wwpSet = order.getWwpSet();
			List<Procard> procardList1 = new ArrayList<Procard>();
			if (wwpSet != null && wwpSet.size() > 0) {
				// List<Sell> sellList = new ArrayList<Sell>();
				// List<Goods> returngoodsList = new ArrayList<Goods>();
				if (order.getWwSource().equals("BOM外委")) {
					for (WaigouPlan plan : wwpSet) {
						if(plan.getStatus().equals("取消")||plan.getStatus().equals("删除")){
							continue;
						}
						List<Procard> procardList = totalDao
								.query(
										"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?)",
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
								Float needCount = (Float) totalDao
										.getObjectByCondition(
												"select sum(procardCount) from ProcardWGCenter where wgOrderId=? and procardId=? ",
												plan.getId(), procard.getId());
								if (!b) {
									procard.setRealReceive(needCount);
									procardList1.add(procard);
								} else {
									List<Procard> sonProcardList = totalDao
											.query(
													"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除') and id in(select procardId from ProcessInforWWProcard where "
															+ "wwxlId in(select wwxlId from ProcardWGCenter where procardId=? and wgOrderId=?) and (status is null or status not in ('删除','取消')) )",
													procard.getId(), procard
															.getId(), plan
															.getId());
									if (sonProcardList == null
											|| sonProcardList.size() == 0) {
										// 领取下层委外零件
										sonProcardList = totalDao
												.query(
														"from Procard where procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and (needProcess is null or needProcess!='yes')))",
														procard.getId());
										for (Procard son : sonProcardList) {
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											if (maxsonprocess != null) {
												Float needCount2 = null;
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
												son.setRealReceive(needCount2);
												procardList1.add(son);
											}
										}
									} else {
										for (Procard son : sonProcardList) {
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
											son.setRealReceive(needCount2);
											procardList1.add(son);
										}
									}

								}
							}
						}
					}
				} else {
					Map<String, Procard> wgjmap = new HashMap();
					for (WaigouPlan plan : wwpSet) {
						if(plan.getStatus().equals("取消")||plan.getStatus().equals("删除")){
							continue;
						}
						if (plan.getWwType().equals("工序外委")) {
							List<ProcessInforWWApplyDetail> applyDetailList = totalDao
									.query(
											"from ProcessInforWWApplyDetail where id in( "
													+ " select wwxlId from ProcardWGCenter where wgOrderId = ?)",
											plan.getId());
							if (applyDetailList != null
									&& applyDetailList.size() > 0) {
								Procard procard = null;
								Float needCount = 0f;
								Set<String> orderNumber = new HashSet<String>();
								for (ProcessInforWWApplyDetail applyDetail : applyDetailList) {
									procard = (Procard) totalDao.getObjectById(
											Procard.class, applyDetail
													.getProcardId());
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
															+ procard
																	.getMarkId()
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
											orderNumber.add(procard
													.getOrderNumber());
											// 需要在制品出库
											needCount += applyDetail
													.getApplyCount();
											// if (needCount != null &&
											// needCount > 0) {
											// procard.setRealReceive(needCount);
											// procardList1.add(procard);
											// }
										}
									}
									// 合并要领的外购件
									// 出外购件
									List<ProcessInforWWProcard> processwwprocardList = totalDao
											.query(
													"from ProcessInforWWProcard where (status is null or status='使用') and applyDtailId=? and (status is null or status not in ('删除','取消'))",
													applyDetail.getId());
									if (processwwprocardList != null
											&& processwwprocardList.size() > 0) {
										for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
											// 存在外购件，判断库存是否充足
											// 第三步计算外购件需领数量
											// 外购件需领数量
											// float count = applyDetail
											// .getApplyCount();
											// if
											// (processInforWWProcard.getHascount()
											// == null) {
											// processInforWWProcard
											// .setHascount(processInforWWProcard
											// .getApplyCount());
											// }
											float count = processInforWWProcard
													.getApplyCount();
											if (count == 0) {
												// 已经领过了
												continue;
											}
											// float tqlcount = 0f;
											Procard son = (Procard) totalDao
													.getObjectById(
															Procard.class,
															processInforWWProcard
																	.getProcardId());
											boolean need = true;
											if (b
													&& son.getProcardStyle()
															.equals("自制")) {
												// 如果是首工序外委下层自制件判断是否为尾工序外委给了同一个供应商
												Integer xcmaxProcessno = (Integer) totalDao
														.getObjectByCondition(
																"select max(processNO) from ProcessInfor "
																		+ "where procard.id=? and (dataStatus is null or dataStatus!='删除')",
																son.getId());
												Float twwCount = (Float) totalDao
														.getObjectByCondition(
																"select count(*) from WaigouPlan where gysId=?"
																		+ " and id in(select wgOrderId from ProcardWGCenter where procardId=?) and status not in('删除','取消')"
																		+ " and (processNOs='"
																		+ xcmaxProcessno
																		+ "' or processNOs like '%;"
																		+ xcmaxProcessno
																		+ "' )",
																order
																		.getGysId(),
																son.getId());
												if (twwCount != null
														&& twwCount > 0) {
													need = false;
												}
											}
											if (need) {
												// count = count *
												// son.getQuanzi2()
												// / son.getQuanzi1();
												String key = son.getMarkId()
														+ son.getKgliao();
												if (son.getBanBenNumber() != null) {
													key += son
															.getBanBenNumber();
												}
												if (wgjmap.get(key) != null) {
													Procard p = wgjmap.get(key);
													String order_wgj = p
															.getOrderNumber();
													if (!order_wgj.contains(son
															.getOrderNumber())) {
														order_wgj += "<br/>"
																+ son
																		.getOrderNumber();
													}
													p.setFilnalCount(p
															.getFilnalCount()
															+ count);
												} else {
													son.setFilnalCount(count);
													wgjmap.put(key, son);
												}
											}

										}
									}

								}
								if (needCount != null && needCount > 0) {
									procard.setRealReceive(needCount);
									if (orderNumber != null
											&& orderNumber.size() > 0) {
										String order_ = "";
										for (String str : orderNumber) {
											order_ += str + "<br/>";
										}
										procard.setOrderNumber(order_);
									}
									procardList1.add(procard);
								}
							}

						} else {
							List<ProcessInforWWApplyDetail> wwDetaiList = totalDao
									.query(
											"from ProcessInforWWApplyDetail where id in( select wwxlId from ProcardWGCenter where wgOrderId=?)",
											plan.getId());
							if (wwDetaiList != null && wwDetaiList.size() > 0) {
								for (ProcessInforWWApplyDetail applyDetail : wwDetaiList) {
									Procard procard = (Procard) totalDao
											.getObjectById(Procard.class,
													applyDetail.getProcardId());
									if (procard != null) {
										// 领取下层委外零件
										List<Procard> sonProcardList = totalDao
												.query(
														"from Procard where (wwblCount is null or wwblCount=0)and  procard.id =? and (sbStatus is null or sbStatus!='删除')"
																+ " and (procardStyle in ('总成','自制') or (procardStyle='外购' and needProcess='yes'))",
														procard.getId());
										for (Procard son : sonProcardList) {
											ProcessInfor maxsonprocess = (ProcessInfor) totalDao
													.getObjectByCondition(
															"from  ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and processNO=("
																	+ "select max(processNO) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除'))",
															son.getId(), son
																	.getId());
											if (maxsonprocess != null) {
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
												son.setRealReceive(needCount2);
												procardList1.add(son);
											}
										}
									}
									// 合并要领的外购件
									// 出外购件
									List<ProcessInforWWProcard> processwwprocardList = totalDao
											.query(
													"from ProcessInforWWProcard where applyDtailId=? and (status is null or status not in ('删除','取消'))",
													applyDetail.getId());
								}
							}
						}
					}
					if (wgjmap.size() > 0) {
						for (String wgj : wgjmap.keySet()) {
							Procard p = wgjmap.get(wgj);
							p.setRealReceive(p.getFilnalCount());
							procardList1.add(p);
						}
					}
				}
			}
			List<Procard> goodsList1 = new ArrayList<Procard>();
			Map<String, Procard> mapAaa = new HashMap<String, Procard>();
			for (Procard aaa : procardList1) {
				Procard aaaTemp = mapAaa.get(aaa.getMarkId());
				if (aaaTemp != null) {
					aaaTemp.setRealReceive(aaaTemp.getRealReceive()
							+ aaa.getRealReceive());
				} else {
					mapAaa.put(aaa.getMarkId(), aaa);
				}
			}
			for (String key : mapAaa.keySet()) {
				Procard aaaTemp = mapAaa.get(key);
				goodsList1.add(aaaTemp);
			}
			map.put(2, goodsList1);
			map.put(3, totalEnough);
		}
		return map;
	}

	@Override
	public String querenDelivery(int id, List<WaigouDeliveryDetail> list_wdd) {
		if (id > 0) {
			WaigouDelivery wd = (WaigouDelivery) totalDao.get(
					WaigouDelivery.class, id);
			if (list_wdd != null && list_wdd.size() > 0) {
				for (WaigouDeliveryDetail wgdd : list_wdd) {
					if (wgdd != null && wgdd.getId() != null) {
						WaigouDeliveryDetail oldwgdd = (WaigouDeliveryDetail) totalDao
								.get(WaigouDeliveryDetail.class, wgdd.getId());
						Float number = oldwgdd.getShNumber();
						if (wgdd.getShNumber() != null
								&& wgdd.getShNumber() >= 0
								&& wgdd.getShNumber() <= oldwgdd.getShNumber()) {
							oldwgdd.setShNumber(wgdd.getShNumber());
							oldwgdd.setStatus("待打印");
							totalDao.update(oldwgdd);
							if (oldwgdd.getWaigouPlanId() != null) {
								WaigouPlan wgp = (WaigouPlan) totalDao.get(
										WaigouPlan.class, oldwgdd
												.getWaigouPlanId());
								float synumber1 = wgp.getSyNumber();
								synumber1 = synumber1
										+ (number - wgdd.getShNumber());
								wgp.setSyNumber(synumber1);
								totalDao.update(wgp);
							}
						}
					}
				}
			}
			wd.setStatus("待打印");
			if (!totalDao.update(wd)) {
				return "error";
			}
			return "true";
		}
		return "error";
	}

	public String ShenpiForPrice(Integer id) {
		if (id != null) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			String processName = "刷新价格审批流程";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						WaigouPlan.class, waigouplan.getId(), "epStatus",
						"epId", "", "刷新价格审批流程，请您审批", true);
				if (epId != null && epId > 0) {
					waigouplan.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						waigouplan.setEpStatus("同意");
					} else {
						waigouplan.setEpStatus("未审批");
					}
				}
				return "刷新价格审批流程已提交";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "提交失败";

	}

	@Override
	public String shuaixinPrice(Integer id) {
		if (id != null) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			String time = Util.getDateTime("yyyy-MM-dd");
			String hql_price = "from Price where partNumber=? and gysId =? and '"
					+ time
					+ "'>= pricePeriodStart and ('"
					+ time
					+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null) ";
			if ("外购".equals(waigouplan.getType())) {
				if (waigouplan.getBanben() != null
						&& waigouplan.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + waigouplan.getBanben()
							+ "'";
				}
				if (waigouplan.getKgliao() != null
						&& waigouplan.getKgliao().length() > 0) {
					hql_price += " and kgliao='" + waigouplan.getKgliao() + "'";
				}

			} else if ("外委".equals(waigouplan.getType())) {
				hql_price += " and  (gongxunum ='待填充' or gongxunum is null or gongxunum = '"
						+ waigouplan.getProcessNOs() + "')";
				hql_price += " and wwType = '" + waigouplan.getWwType() + "'";
			} else if ("辅料".equals(waigouplan.getType())) {
				hql_price += " and productCategory = '辅料'";
				if (waigouplan.getBanben() != null
						&& waigouplan.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + waigouplan.getBanben()
							+ "'";
				}
				if (waigouplan.getKgliao() != null
						&& waigouplan.getKgliao().length() > 0) {
					hql_price += " and kgliao='" + waigouplan.getKgliao() + "'";
				}
			}

			/************** 查询产品单价 ********/
			List<Price> list_price = totalDao.query(hql_price
					+ " order by  hsPrice", waigouplan.getMarkId(), waigouplan
					.getGysId());
			Price price = null;
			for (int j = 0; j < list_price.size(); j++) {
				if(price.getEndnum() == null || price.getEndnum()==0){
					price = list_price.get(j);
					break;
				}else if (price.getEndnum() != null
						&& price.getEndnum() >= waigouplan.getNumber()) {
					price = list_price.get(j);
					break;
				}
			}
			if (price == null) {
				return "未找到件号:" + waigouplan.getMarkId() + "供应商为"
						+ waigouplan.getGysName() + " ，"
						+ waigouplan.getNumber() + "数量内的阶梯单价，未找到！";
			}
			Double oldprice = waigouplan.getHsPrice();
			if ("待核对".equals(waigouplan.getStatus()) && price != null) {
				waigouplan.setPrice(price.getBhsPrice());
				waigouplan.setHsPrice(price.getHsPrice());
				waigouplan.setTaxprice(price.getTaxprice());
				Double money = waigouplan.getMoney();
				waigouplan.setMoney(waigouplan.getHsPrice()
						* waigouplan.getNumber());
				waigouplan.setPriceId(price.getId());

				WaigouOrder wgorder = waigouplan.getWaigouOrder();
				wgorder.setAllMoney(wgorder.getAllMoney() == null ? waigouplan
						.getMoney() : wgorder.getAllMoney() - money
						+ waigouplan.getMoney());
				totalDao.update(wgorder);
				if (totalDao.update(waigouplan)) {
					return "刷新价格成功，价格由:" + oldprice + " 变为:"
							+ waigouplan.getHsPrice();
				}
			} else {
				if ("辅料".equals(waigouplan.getType()) || "外购".equals(waigouplan.getType()) && price != null) {
					String processName = "刷新价格(外购/辅料)审批流程";
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								WaigouPlan.class, waigouplan.getId(),
								"epStatus", "epId",
								"WaigouwaiweiPlanAction!updatePricefoeshenpi.action?id="
										+ waigouplan.getId(), "刷新价格审批流程，请您审批",
								true);
						if (epId != null && epId > 0) {
							waigouplan.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								waigouplan.setEpStatus("同意");
							} else {
								waigouplan.setEpStatus("未审批");
							}
						}
						totalDao.update(waigouplan);
						return "刷新价格审批流程已提交";
					} catch (Exception e) {
						e.printStackTrace();
						return e.toString();
					}
				} else if ("外委".equals(waigouplan.getType()) && price != null) {
					String processName = "刷新价格(外委)审批流程";
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								WaigouPlan.class, waigouplan.getId(),
								"epStatus", "epId",
								"WaigouwaiweiPlanAction!updatePricefoeshenpi.action?id="
										+ waigouplan.getId(), "刷新价格审批流程，请您审批",
								true);
						if (epId != null && epId > 0) {
							waigouplan.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								waigouplan.setEpStatus("同意");
							} else {
								waigouplan.setEpStatus("未审批");
							}
						}
						totalDao.update(waigouplan);
						return "刷新价格审批流程已提交";
					} catch (Exception e) {
						e.printStackTrace();
						return e.toString();
					}
				}
			}
		}
		return "error";
	}

	public Object[] findwaigouplanAndNewPrice(Integer id) {
		if (id != null) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			String time = Util.getDateTime("yyyy-MM-dd");
			String hql_price = "from Price where partNumber=? and gysId =? and '"
					+ time
					+ "'>= pricePeriodStart and ('"
					+ time
					+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
			if ("外购".equals(waigouplan.getType())) {
				if (waigouplan.getBanben() != null
						&& waigouplan.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + waigouplan.getBanben()
							+ "'";
				}
				if (waigouplan.getKgliao() != null
						&& waigouplan.getKgliao().length() > 0) {
					hql_price += " and kgliao='" + waigouplan.getKgliao() + "'";
				}

			} else if ("外委".equals(waigouplan.getType())) {
				hql_price += " and (gongxunum ='待填充' or gongxunum is null or gongxunum = '"
						+ waigouplan.getProcessNOs() + "')";
				hql_price += " and wwType = '" + waigouplan.getWwType() + "'";
			}

			/************** 查询产品单价 ********/
			List<Price> list_price = totalDao.query(hql_price
					+ " order by  hsPrice", waigouplan.getMarkId(), waigouplan
					.getGysId());
			Price price = null;
			for (int j = 0; j < list_price.size(); j++) {
				price = list_price.get(j);
				if (price.getEndnum() != null
						&& price.getEndnum() >= waigouplan.getNumber()) {
					break;
				}
			}
			if (price != null) {
				Object[] o = { waigouplan, price };
				return o;
			}
		}
		return null;
	}

	@Override
	public boolean delWaigouOrder(WaigouOrder waigouorder) {
		if (waigouorder != null && waigouorder.getId() != null) {
			waigouorder = (WaigouOrder) totalDao.get(WaigouOrder.class,
					waigouorder.getId());
			Set<WaigouPlan> wwpSet = waigouorder.getWwpSet();
			for (WaigouPlan waigouPlan : wwpSet) {
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, waigouPlan.getMopId());
				Float delNum = waigouPlan.getNumber();
				Set<ManualOrderPlanDetail> modSet = mop.getModSet();
				for (ManualOrderPlanDetail mod : modSet) {
					if (delNum <= 0f) {
						break;
					}
					if (mod.getOutcgNumber() == null) {
						continue;
					}
					if (mod.getOutcgNumber() >= delNum) {
						mod.setOutcgNumber(mod.getOutcgNumber() - delNum);
						delNum = 0f;
					} else {
						delNum -= mod.getOutcgNumber();
						mod.setOutcgNumber(0f);
					}
					if (mod.getProcardId() != null) {
						Procard procard = (Procard) totalDao.get(Procard.class,
								mod.getProcardId());
						if (procard != null) {
							procard.setOutcgNumber(mod.getOutcgNumber());
							procard.setWlstatus("待采购");
							totalDao.update(procard);
						}
					}
					totalDao.update(mod);
				}

				Float outcgNumber = mop.getOutcgNumber()
						- waigouPlan.getNumber();
				Float wshCount = mop.getWshCount() - waigouPlan.getSyNumber();
				mop.setOutcgNumber(outcgNumber < 0 ? 0 : outcgNumber);
				mop.setWshCount(wshCount == 0 ? null : wshCount);
				totalDao.update(mop);

				/*** 不用这个表维护关系了 */
				// String hql_1 = "from ProcardWGCenter where  wgOrderId = ?";
				// List<ProcardWGCenter> list = totalDao.query(hql_1, waigouPlan
				// .getId());
				// if (list != null && list.size() > 0) {
				// Float number = waigouPlan.getNumber();
				// for (ProcardWGCenter procardWGCenter : list) {
				// if (number > 0) {
				// Procard procard = (Procard) totalDao.get(
				// Procard.class, procardWGCenter
				// .getProcardId());
				// Float outcgNumber = procard.getOutcgNumber();
				// if (outcgNumber != null && outcgNumber > 0) {
				// outcgNumber -= procardWGCenter
				// .getProcardCount();
				// }
				// if (outcgNumber == null || outcgNumber < 0) {
				// outcgNumber = 0f;
				// }
				// procard.setOutcgNumber(outcgNumber);
				// procard.setWlstatus("待采购");
				// totalDao.update(procard);
				// number -= procardWGCenter.getProcardCount();
				// }
				// totalDao.delete(procardWGCenter);
				// }
				// }
			}
			return totalDao.delete(waigouorder);
		}
		return false;
	}

	@Override
	public Object[] findAllWaigouDeliveryDetail(
			WaigouDeliveryDetail waigouDeliveryDetail, int parseInt,
			int pageSize, String firsttime, String endtime, String pageStatus,
			String tag) {
		if (waigouDeliveryDetail == null) {
			waigouDeliveryDetail = new WaigouDeliveryDetail();
		}
		String str = "";
		if (waigouDeliveryDetail.getWaigouDelivery() != null
				&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber() != null
				&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber()
						.length() > 0) {
			str = " and waigouDelivery.planNumber like '%"
					+ waigouDeliveryDetail.getWaigouDelivery().getPlanNumber()
					+ "%'";
			waigouDeliveryDetail.setWaigouDelivery(null);
		} else {
			waigouDeliveryDetail.setWaigouDelivery(null);
		}
		String cq = waigouDeliveryDetail.getChangqu();
		if ("gys".equals(pageStatus) || "gys".equals(tag)) {
			Users loginUser = Util.getLoginUser();
			str += " and waigouDelivery.userId=" + loginUser.getId();
		} else if ("dqr".equals(pageStatus)) {
			// str += " and waigouDelivery.status = '待确认'";
		} else if ("bhg".equals(pageStatus)) {
			str += " and status = '退货待核对' and hgNumber like '0' ";
		} else if ("bhgdl".equals(pageStatus)) {
			Users loginUser = Util.getLoginUser();
			str += " and thStatus = '待领' and bhgNumber > 0 and waigouDelivery.userId="
					+ loginUser.getId();
		}
		String status = "";
		if (waigouDeliveryDetail.getStatus() != null
				&& waigouDeliveryDetail.getStatus().length() > 0) {
			String[] strarray = waigouDeliveryDetail.getStatus().split(",");
			status = waigouDeliveryDetail.getStatus();
			if (strarray != null && strarray.length > 0) {
				String str1 = "";
				for (int i = 0; i < strarray.length; i++) {
					str1 += "," + "'" + strarray[i] + "'";
				}
				if (str1 != null && str1.length() > 1) {
					str1 = str1.substring(1);
					str += " and status in (" + str1 + ")";
				}
			}
			waigouDeliveryDetail.setStatus(null);
		}
		String kgliao = "";
		if (waigouDeliveryDetail.getKgliao() != null
				&& waigouDeliveryDetail.getKgliao().length() > 0) {
			String[] strarray = waigouDeliveryDetail.getKgliao().split(",");
			if (strarray != null && strarray.length > 0) {
				String str2 = "";
				for (int i = 0; i < strarray.length; i++) {
					str2 += "," + "'" + strarray[i] + "'";
				}
				if (str2 != null && str2.length() > 1) {
					str2 = str2.substring(1);
					str += " and kgliao in (" + str2 + ")";
				}
			}
			kgliao = waigouDeliveryDetail.getKgliao();
			waigouDeliveryDetail.setKgliao(null);
		}
		if (firsttime != null && firsttime.length() > 0) {
			firsttime = firsttime + " 00:00:00";
		}
		if (endtime != null && endtime.length() > 0) {
			endtime = endtime + " 23:59:59";
		}

		String hql = totalDao.criteriaQueries(waigouDeliveryDetail, "rukuTime",
				new String[] { firsttime, endtime }, "");
		waigouDeliveryDetail.setStatus(status);
		waigouDeliveryDetail.setKgliao(kgliao);
		// 按件号查询不分页。
		int count = totalDao.getCount(hql + str);
		if (waigouDeliveryDetail.getMarkId() != null
				&& waigouDeliveryDetail.getMarkId().length() > 0) {
			pageSize = count;
		}
		List<WaigouDeliveryDetail> list = totalDao.findAllByPage(hql + str
				+ " order by addTime desc", parseInt, pageSize);
		Float sumNum = 0f;
		Float sumqrNum = 0f;
		Float sumheNum = 0f;
		if (list != null) {
			for (WaigouDeliveryDetail w2 : list) {
				sumNum += w2.getShNumber();
				if (w2.getQrNumber() != null) {
					sumqrNum += w2.getQrNumber();
				}
				if (w2.getHgNumber() != null) {
					sumheNum += w2.getHgNumber();
				}
			}
		}
		List cqList = totalDao
				.query("select goodHouseName from GoodHouse where goodHouseName in(select changqu from WaigouDeliveryDetail )");
		Object[] o = { list, count, sumNum, sumqrNum, sumheNum, cqList,
				pageSize };
		return o;
	}

	@Override
	public void exportExcelWaigouDelivery(WaigouDelivery waigouDelivery,
			String pageStatus, String firsttime, String endtime) {
		String str1 = "";
		// if (waigouDelivery.getStatus() != null
		// && waigouDelivery.getStatus().length() > 0) {
		// String[] strarray = waigouDelivery.getStatus().split(",");
		// if (strarray != null && strarray.length > 0) {
		//
		// for (int i = 0; i < strarray.length; i++) {
		// str1 += "," + "'" + strarray[i] + "'";
		// }
		// if (str1 != null && str1.length() > 1) {
		// str1 = str1.substring(1);
		// str1 += " and status in (" + str1 + ")";
		// }
		// }
		// waigouDelivery.setStatus(null);
		// }
		if (waigouDelivery.getDeliveryDetail() != null) {
			waigouDelivery.setDeliveryDetail(null);
		}
		String hql = totalDao.criteriaQueries(waigouDelivery, "addTime",
				new String[] { firsttime, endtime }, "");
		String str = "";
		if ("dqr".equals(pageStatus)) {
			str = " and status = '待确认'";
		} else if ("gys".equals(pageStatus)) {
			str = " and status='待打印'";
			Users loginUser = Util.getLoginUser();
			hql += " and userId=" + loginUser.getId();
		}
		// 当前供应商所遇的送货单
		List list = totalDao.find(hql + str + str1);// +
		// " and type = '外协'"这个类中没有这个属性不得不注释

		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response
					.setHeader("Content-disposition", "attachment; filename="
							+ new String("送货单明细".getBytes("GB2312"), "8859_1")
							+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("送货单明细信息", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(18, 25);

			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "送货单号"));
			ws.addCell(new Label(2, 0, "供应商名称"));
			ws.addCell(new Label(3, 0, "联系人"));
			ws.addCell(new Label(4, 0, "联系方式"));
			ws.addCell(new Label(5, 0, "状态"));
			ws.addCell(new Label(6, 0, "添加日期"));
			for (int i = 0; i < list.size(); i++) {
				WaigouDelivery wdd = (WaigouDelivery) list.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, wdd.getPlanNumber()));
				ws.addCell(new Label(2, i + 1, wdd.getGysName()));
				ws.addCell(new Label(3, i + 1, wdd.getContacts()));
				ws.addCell(new Label(4, i + 1, wdd.getContactsPhone()));
				ws.addCell(new Label(5, i + 1, wdd.getStatus()));
				ws.addCell(new Label(6, i + 1, wdd.getAddTime()));
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
	public void exportExcelWaigouPlan(WaigouPlan waigouPlan, String pageStatus,
			String tag) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return;
		}
		String other = "";
		String wgType = "";
		if (waigouPlan.getWgType() != null
				&& !"".equals(waigouPlan.getWgType())) {
			wgType = waigouPlan.getWgType();
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgType);
			if (category != null) {
				getWgtype(category);
			}
			other = " and wgType in (" + strbu.toString().substring(1) + ")";
		}
		if (waigouPlan.getStatus() != null
				&& waigouPlan.getStatus().length() > 0) {
			String[] strarray = waigouPlan.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str = "";
				for (int i = 0; i < strarray.length; i++) {
					str += "," + "'" + strarray[i] + "'";
				}
				if (str != null && str.length() > 1) {
					str = str.substring(1);
					other += " and status in (" + str + ")";
				}
			}
		}
		String fatherhql = "";
		if (waigouPlan != null && waigouPlan.getWaigouOrder() != null) {
			fatherhql = totalDao.criteriaQueries(waigouPlan.getWaigouOrder(),
					null);
			if (fatherhql != "" && fatherhql.length() > 0) {
				fatherhql = " and waigouOrder.id in ( select id " + fatherhql
						+ ")";
			}
		}
		String planNumber = waigouPlan.getPlanNumber();
		String rootMarkId = waigouPlan.getRootMarkId();
		String rootSlfCard = waigouPlan.getRootSlfCard();
		if (planNumber != null && planNumber.length() > 0
				&& !planNumber.contains("delete")
				&& !planNumber.contains("select")
				&& !planNumber.contains("update")) {
			other += " and waigouOrder.planNumber like '%" + planNumber + "%'";
		}
		if (rootMarkId != null && rootMarkId.length() > 0
				&& !rootMarkId.contains("delete")
				&& !rootMarkId.contains("select")
				&& !rootMarkId.contains("update") && rootSlfCard != null
				&& rootSlfCard.length() > 0 && !rootSlfCard.contains("delete")
				&& !rootSlfCard.contains("select")
				&& !rootSlfCard.contains("update")) {
			other += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ rootMarkId + "%' or rootMarkId like '%" + rootMarkId
					+ "%')" + " and rootSelfCard like '%" + rootSlfCard
					+ "%'))";
		}
		other += fatherhql;
		String hql = totalDao.criteriaQueries(waigouPlan, null, "status",
				"wgType", "addTime", "acArrivalTime", "waigouOrder",
				"planNumber", "rootMarkId", "rootSlfCard")
				+ other;
		if (waigouPlan.getAddTime() != null
				&& waigouPlan.getAddTime().length() > 0) {
			String[] strarray = waigouPlan.getAddTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].trim().length() > 0) {
					hql += " and addTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and addTime < '" + strarray[1].trim() + "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAddTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAddTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if (waigouPlan.getAcArrivalTime() != null
				&& waigouPlan.getAcArrivalTime().length() > 0) {
			String[] strarray = waigouPlan.getAcArrivalTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].length() > 0
						&& strarray[0].trim().length() > 0) {
					hql += " and acArrivalTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and acArrivalTime < '" + strarray[1].trim()
								+ "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAcArrivalTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAcArrivalTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if ("ww".equals(tag)) {
			hql += " and type = '外委'";
		} else if ("fl".equals(tag)) {
			hql += " and type = '辅料'";
		} else if ("wg".equals(tag)) {
			hql += " and type = '外购'";
		}
		if (user.getInternal() != null && user.getInternal().equals("否")) {
			hql += " and waigouOrder.userId=" + user.getId();
		}
		List<WaigouPlan> list = totalDao.query(hql);
		for (WaigouPlan w : list) {
			String ywMarkId = "";
			String rootMarkId1 = "";
			String neiorderNum = "";
			String hql_procardList = " from Procard where id in (select procardId from ProcardWGCenter where wgOrderId = ?)";
			List<Procard> procardList = totalDao.query(hql_procardList, w
					.getId());
			for (Procard p : procardList) {
				ywMarkId += ";" + p.getYwMarkId();
				rootMarkId1 += ";" + p.getRootMarkId();
				neiorderNum += ";" + p.getOrderNumber();
			}
			if (ywMarkId.length() > 1) {
				ywMarkId = ywMarkId.substring(1);
			}
			if (rootMarkId1.length() > 1) {
				rootMarkId1 = rootMarkId1.substring(1);
			}
			if (neiorderNum.length() > 1) {
				neiorderNum = neiorderNum.substring(1);
			}
			w.setYwmarkId(ywMarkId);
			w.setRootMarkId(rootMarkId1);
			w.setNeiorderNum(neiorderNum);
		}

		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("采购订单明细".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("采购订单明细", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(14, 16);
			ws.setColumnView(21, 16);
			ws.setColumnView(22, 16);
			ws.setColumnView(23, 16);

			int ie = 0;
			ws.addCell(new Label(ie, 0, "序号"));
			ie++;
			ws.addCell(new Label(ie, 0, "订单编号"));
			ie++;
			ws.addCell(new Label(ie, 0, "供应商"));
			ie++;
			ws.addCell(new Label(ie, 0, "物料类别"));
			ie++;
			ws.addCell(new Label(ie, 0, "件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "零件名称"));
			ie++;
			if ("ww".equals(tag)) {
				ws.addCell(new Label(ie, 0, "工序号"));
				ie++;
				ws.addCell(new Label(ie, 0, "工序名称"));
				ie++;

			}
			ws.addCell(new Label(ie, 0, "规格"));
			ie++;
			ws.addCell(new Label(ie, 0, "版本"));
			ie++;
			ws.addCell(new Label(ie, 0, "版次"));
			ie++;
			ws.addCell(new Label(ie, 0, "供货属性"));
			ie++;
			ws.addCell(new Label(ie, 0, "图号"));
			ie++;
			ws.addCell(new Label(ie, 0, "单位"));
			ie++;
			ws.addCell(new Label(ie, 0, "数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "添加时间"));
			ie++;
			ws.addCell(new Label(ie, 0, "签收时间"));
			ie++;
			ws.addCell(new Label(ie, 0, "产品状态"));
			ie++;
			ws.addCell(new Label(ie, 0, "剩余未送货数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "送出数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "合格数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "签收数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "未签收数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "业务件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "总成件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "内部订单号"));
			ie++;// 23
			ws.addCell(new Label(ie, 0, "项目编号"));
			ie++;
			if ("huizong".equals(tag)) {
				ws.addCell(new Label(ie, 0, "含税单价"));
				ie++;
				ws.addCell(new Label(ie, 0, "不含税单价"));
				ie++;
				ws.addCell(new Label(ie, 0, "税率"));
				ie++;// 25
			}
			for (int i = 0; i < list.size(); i++) {
				WaigouPlan wgp = (WaigouPlan) list.get(i);
				int ie1 = 0;
				ws.addCell(new Label(ie1, i + 1, i + 1 + ""));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getWaigouOrder()
						.getPlanNumber()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getGysName()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getWgType()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getMarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getProName()));
				ie1++;
				if ("ww".equals(tag)) {
					ws.addCell(new Label(ie1, i + 1, wgp.getProcessNOs()));
					ie1++;
					ws.addCell(new Label(ie1, i + 1, wgp.getProcessNames()));
					ie1++;

				}
				ws.addCell(new Label(ie1, i + 1, wgp.getSpecification()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getBanben()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getBanci() == null) ? 0 : wgp.getBanci()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getKgliao()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getTuhao()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getUnit()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1, wgp.getNumber()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getAddTime()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getAcArrivalTime()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getStatus()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getSyNumber() == null) ? 0 : wgp.getSyNumber()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getNumber() - wgp.getSyNumber())));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getHgNumber() == null) ? 0 : wgp.getHgNumber()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getQsNum() == null) ? 0 : wgp.getQsNum()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getQsNum() == null) ? wgp.getNumber() : wgp
								.getNumber()
								- wgp.getQsNum()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getYwmarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getRootMarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getNeiorderNum()));
				ie1++;// 23
				ws.addCell(new Label(ie1, i + 1, wgp.getProNumber()));
				ie1++;
				if ("huizong".equals(tag)) {
					ws.addCell(new jxl.write.Number(ie1, i + 1, (wgp
							.getHsPrice() == null) ? 0 : wgp.getHsPrice()));
					ie1++;
					ws.addCell(new jxl.write.Number(ie1, i + 1,
							(wgp.getPrice() == null) ? 0 : wgp.getPrice()));
					ie1++;
					ws.addCell(new jxl.write.Number(ie1, i + 1, (wgp
							.getTaxprice() == null) ? 0 : wgp.getTaxprice()));
					ie1++;// 26
				}
			}
			try {
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void exportExcelWaigouPlan2(WaigouPlan waigouPlan,
			String pageStatus, String tag) {

		String other = "";
		String wgType = "";
		if (waigouPlan.getWgType() != null
				&& !"".equals(waigouPlan.getWgType())) {
			wgType = waigouPlan.getWgType();
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgType);
			if (category != null) {
				getWgtype(category);
			}
			other = " and wgType in (" + strbu.toString().substring(1) + ")";
		}
		if (waigouPlan.getStatus() != null
				&& waigouPlan.getStatus().length() > 0) {
			String[] strarray = waigouPlan.getStatus().split(",");
			if (strarray != null && strarray.length > 0) {
				String str = "";
				for (int i = 0; i < strarray.length; i++) {
					str += "," + "'" + strarray[i] + "'";
				}
				if (str != null && str.length() > 1) {
					str = str.substring(1);
					other += " and status in (" + str + ")";
				}
			}
		}
		String planNumber = waigouPlan.getPlanNumber();
		String rootMarkId = waigouPlan.getRootMarkId();
		String rootSlfCard = waigouPlan.getRootSlfCard();
		if (planNumber != null && planNumber.length() > 0
				&& !planNumber.contains("delete")
				&& !planNumber.contains("select")
				&& !planNumber.contains("update")) {
			other += " and waigouOrder.planNumber like '%" + planNumber + "%'";
		}
		if (rootMarkId != null && rootMarkId.length() > 0
				&& !rootMarkId.contains("delete")
				&& !rootMarkId.contains("select")
				&& !rootMarkId.contains("update") && rootSlfCard != null
				&& rootSlfCard.length() > 0 && !rootSlfCard.contains("delete")
				&& !rootSlfCard.contains("select")
				&& !rootSlfCard.contains("update")) {
			other += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ rootMarkId + "%' or rootMarkId like '%" + rootMarkId
					+ "%')" + " and rootSelfCard like '%" + rootSlfCard
					+ "%'))";
		}
		if (rootMarkId != null
				&& rootMarkId.length() > 0
				&& !rootMarkId.contains("delete")
				&& !rootMarkId.contains("select")
				&& !rootMarkId.contains("update")
				&& (rootSlfCard == null ||rootSlfCard.length() == 0)) {
			other += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where  (ywMarkId like '%"
					+ rootMarkId + "%' or rootMarkId like '%" + rootMarkId
					+ "%')))";
		}
		if ((rootMarkId == null || rootMarkId.length() == 0)
				&& rootSlfCard != null && rootSlfCard.length() > 0
				&& !rootSlfCard.contains("delete")
				&& !rootSlfCard.contains("select")
				&& !rootSlfCard.contains("update")) {
			other += " and id in(select wgOrderId from ProcardWGCenter where "
					+ "procardId in(select id from Procard where rootSelfCard like '%"
					+ rootSlfCard + "%'))";
		}

		String fatherhql = "";
		if (waigouPlan != null && waigouPlan.getWaigouOrder() != null) {
			fatherhql = totalDao.criteriaQueries(waigouPlan.getWaigouOrder(),
					null);
			if (fatherhql != "" && fatherhql.length() > 0) {
				fatherhql = " and waigouOrder.id in ( select id " + fatherhql
						+ ")";
			}
		}
		other += fatherhql;
		String hql = totalDao.criteriaQueries(waigouPlan, null, "status",
				"wgType", "addTime", "acArrivalTime", "waigouOrder",
				"planNumber", "rootMarkId", "rootSlfCard")
				+ other;
		if (waigouPlan.getAddTime() != null
				&& waigouPlan.getAddTime().length() > 0) {
			String[] strarray = waigouPlan.getAddTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].trim().length() > 0) {
					hql += " and addTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and addTime < '" + strarray[1].trim() + "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAddTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAddTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if (waigouPlan.getAcArrivalTime() != null
				&& waigouPlan.getAcArrivalTime().length() > 0) {
			String[] strarray = waigouPlan.getAcArrivalTime().split(",");
			if (strarray != null && strarray.length > 0) {
				if (strarray[0] != null && strarray[0].length() > 0
						&& strarray[0].trim().length() > 0) {
					hql += " and acArrivalTime > '" + strarray[0].trim() + "'";
				}
				if (strarray.length > 1) {
					if (strarray[1] != null && strarray[1].trim().length() > 0) {
						hql += " and acArrivalTime < '" + strarray[1].trim()
								+ "'";
					}
					if ("".equals(strarray[0].trim())
							&& !"".equals(strarray[1].trim())) {
						waigouPlan.setAcArrivalTime(" ," + strarray[1].trim());
					} else {
						waigouPlan.setAcArrivalTime(strarray[0].trim() + ","
								+ strarray[1].trim());
					}
				}
			}
		}
		if ("ww".equals(tag)) {
			hql += " and type = '外委'";
		} else if("fl".equals(tag)){
			hql += " and type = '辅料'";
		}else {
			hql += " and type = '外购'";
		}
		List<WaigouPlan> list = totalDao.query(hql);
		for (WaigouPlan w : list) {
			String ywMarkId = "";
			String rootMarkId1 = "";
			String neiorderNum = "";
			String hql_procardList = " from Procard where id in (select procardId from ProcardWGCenter where wgOrderId = ?)";
			List<Procard> procardList = totalDao.query(hql_procardList, w
					.getId());
			for (Procard p : procardList) {
				ywMarkId += ";" + p.getYwMarkId();
				rootMarkId1 += ";" + p.getRootMarkId();
				neiorderNum += ";" + p.getOrderNumber();
			}
			if (ywMarkId.length() > 1) {
				ywMarkId = ywMarkId.substring(1);
			}
			if (rootMarkId1.length() > 1) {
				rootMarkId1 = rootMarkId1.substring(1);
			}
			if (neiorderNum.length() > 1) {
				neiorderNum = neiorderNum.substring(1);
			}
			w.setYwmarkId(ywMarkId);
			w.setRootMarkId(rootMarkId1);
			w.setNeiorderNum(neiorderNum);
		}

		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("采购订单明细".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("采购订单明细", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(14, 16);
			ws.setColumnView(21, 16);
			ws.setColumnView(22, 16);
			ws.setColumnView(23, 16);

			int ie = 0;
			ws.addCell(new Label(ie, 0, "序号"));
			ie++;
			ws.addCell(new Label(ie, 0, "订单编号"));
			ie++;
			ws.addCell(new Label(ie, 0, "供应商"));
			ie++;
			ws.addCell(new Label(ie, 0, "物料类别"));
			ie++;
			ws.addCell(new Label(ie, 0, "件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "零件名称"));
			ie++;
			if ("ww".equals(tag)) {
				ws.addCell(new Label(ie, 0, "工序号"));
				ie++;
				ws.addCell(new Label(ie, 0, "工序名称"));
				ie++;
				ws.addCell(new Label(ie, 0, "类型"));
				ie++;
			}
			ws.addCell(new Label(ie, 0, "规格"));
			ie++;
			ws.addCell(new Label(ie, 0, "版本"));
			ie++;
			ws.addCell(new Label(ie, 0, "版次"));
			ie++;
			ws.addCell(new Label(ie, 0, "供货属性"));
			ie++;
			ws.addCell(new Label(ie, 0, "图号"));
			ie++;
			ws.addCell(new Label(ie, 0, "单位"));
			ie++;
			ws.addCell(new Label(ie, 0, "数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "添加时间"));
			ie++;
			ws.addCell(new Label(ie, 0, "签收时间"));
			ie++;
			ws.addCell(new Label(ie, 0, "产品状态"));
			ie++;
			ws.addCell(new Label(ie, 0, "剩余未送货数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "送出数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "合格数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "签收数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "未签收数量"));
			ie++;
			ws.addCell(new Label(ie, 0, "业务件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "总成件号"));
			ie++;
			ws.addCell(new Label(ie, 0, "内部订单号"));
			ie++;// 23
			ws.addCell(new Label(ie, 0, "含税单价"));
			ie++;
			ws.addCell(new Label(ie, 0, "不含税单价"));
			ie++;
			ws.addCell(new Label(ie, 0, "税率"));
			ie++;// 25
			ws.addCell(new Label(ie, 0, "金额"));
			ie++;// 25
			for (int i = 0; i < list.size(); i++) {
				WaigouPlan wgp = (WaigouPlan) list.get(i);
				int ie1 = 0;
				ws.addCell(new Label(ie1, i + 1, i + 1 + ""));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getWaigouOrder()
						.getPlanNumber()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getGysName()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getWgType()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getMarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getProName()));
				ie1++;
				if ("ww".equals(tag)) {
					ws.addCell(new Label(ie1, i + 1, wgp.getProcessNOs()));
					ie1++;
					ws.addCell(new Label(ie1, i + 1, wgp.getProcessNames()));
					ie1++;
					ws.addCell(new Label(ie1, i + 1, wgp.getWwType()));
					ie1++;
				}
				ws.addCell(new Label(ie1, i + 1, wgp.getSpecification()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getBanben()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getBanci() == null) ? 0 : wgp.getBanci()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getKgliao()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getTuhao()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getUnit()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1, wgp.getNumber()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getAddTime()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getAcArrivalTime()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getStatus()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getSyNumber() == null) ? 0 : wgp.getSyNumber()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getNumber() - wgp.getSyNumber())));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getHgNumber() == null) ? 0 : wgp.getHgNumber()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getQsNum() == null) ? 0 : wgp.getQsNum()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getQsNum() == null) ? wgp.getNumber() : wgp
								.getNumber()
								- wgp.getQsNum()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getYwmarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getRootMarkId()));
				ie1++;
				ws.addCell(new Label(ie1, i + 1, wgp.getNeiorderNum()));
				ie1++;// 23
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getHsPrice() == null) ? 0 : wgp.getHsPrice()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getPrice() == null) ? 0 : wgp.getPrice()));
				ie1++;
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getTaxprice() == null) ? 0 : wgp.getTaxprice()));
				ie1++;// 26
				ws.addCell(new jxl.write.Number(ie1, i + 1,
						(wgp.getMoney() == null) ? 0 : (double)Util.FomartDouble(wgp
								.getMoney(), 4)));
				ie1++;// 26
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
	public void exportExcelWaigouDeliveryDetail(
			WaigouDeliveryDetail waigouDeliveryDetail, String firsttime,
			String endtime, String pageStatus) {
		if (waigouDeliveryDetail == null) {
			waigouDeliveryDetail = new WaigouDeliveryDetail();
		}
		if ("huizong".equals(pageStatus) || "gys".equals(pageStatus)) {
			String str = "";
			if (waigouDeliveryDetail.getWaigouDelivery() != null
					&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber() != null
					&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber()
							.length() > 0) {
				str = " and waigouDelivery.planNumber like '%"
						+ waigouDeliveryDetail.getWaigouDelivery()
								.getPlanNumber() + "%'";

			} else {
				waigouDeliveryDetail.setWaigouDelivery(null);
			}
			if (waigouDeliveryDetail.getStatus() != null
					&& waigouDeliveryDetail.getStatus().length() > 0) {
				String[] strarray = waigouDeliveryDetail.getStatus().split(",");
				if (strarray != null && strarray.length > 0) {
					String str1 = "";
					for (int i = 0; i < strarray.length; i++) {
						str1 += "," + "'" + strarray[i] + "'";
					}
					if (str1 != null && str1.length() > 1) {
						str1 = str1.substring(1);
						str += " and status in (" + str1 + ")";
					}
				}
				waigouDeliveryDetail.setStatus(null);
			}
			if (waigouDeliveryDetail.getKgliao() != null
					&& waigouDeliveryDetail.getKgliao().length() > 0) {
				String[] strarray = waigouDeliveryDetail.getKgliao().split(",");
				if (strarray != null && strarray.length > 0) {
					String str2 = "";
					for (int i = 0; i < strarray.length; i++) {
						str2 += "," + "'" + strarray[i] + "'";
					}
					if (str2 != null && str2.length() > 1) {
						str2 = str2.substring(1);
						str += " and kgliao in (" + str2 + ")";
					}
				}
				waigouDeliveryDetail.setKgliao(null);
			}
			if (firsttime != null && firsttime.length() > 0) {
				firsttime = firsttime + " 00:00:00";
			}
			if (endtime != null && endtime.length() > 0) {
				endtime = endtime + " 23:59:59";
			}
			if ("gys".equals(pageStatus)) {
				Users loginUser = Util.getLoginUser();
				str += " and waigouDelivery.userId=" + loginUser.getId();
			}
			String hql = totalDao.criteriaQueries(waigouDeliveryDetail,
					"rukuTime", new String[] { firsttime, endtime }, "");
			hql += str;
			List<WaigouDeliveryDetail> list = totalDao.query(hql
					+ " ORDER BY rukuTime");
			List<WaigouDeliveryDetail> wgddList = new ArrayList<WaigouDeliveryDetail>();
			for (WaigouDeliveryDetail wgdd : list) {
				// String addusername = (String) totalDao
				// .getObjectByCondition(
				// "select addUserName from WaigouOrder where planNumber = ? ",
				// wgdd.getCgOrderNum());
				// if (addusername != null) {
				// wgdd.setCaigouUserName(addusername);
				// }
				// String goodHouseName = (String) totalDao
				// .getObjectByCondition(
				// "select goodHouseName from Goods where goodsMarkId = ? and goodsClass = ?",
				// wgdd.getMarkId(), "外购件库");
				// if (goodHouseName != null) {
				// wgdd.setChangqu(goodHouseName);
				// }
				//
				// String printNumber = (String) totalDao
				// .getObjectByCondition(
				// "select printNumber from GoodsStore where wwddId=? and goodsStoreWarehouse='外购件库'",
				// wgdd.getId());
				// if (printNumber != null) {
				// wgdd.setKuwei(printNumber);
				// }

				String ywMarkId = "";
				String rootMarkId = "";
				String neiorderNum = "";
				if ("外购".equals(wgdd.getType())) {
					// String hql_procardList =
					// " from ManualOrderPlanDetail where manualPlan.id in (select mopId from WaigouPlan where id=?)";
					// List<ManualOrderPlanDetail> procardList = totalDao.query(
					// hql_procardList, wgdd.getWaigouPlanId());
					// for (ManualOrderPlanDetail p : procardList) {
					// ywMarkId += ";" + p.getYwMarkId();
					// rootMarkId += ";" + p.getRootMarkId();
					// neiorderNum += ";" + p.getOrderNumber();
					// }
					// if (ywMarkId.length() > 1) {
					// ywMarkId = ywMarkId.substring(1);
					// }
					// if (rootMarkId.length() > 1) {
					// rootMarkId = rootMarkId.substring(1);
					// }
					// if (neiorderNum.length() > 1) {
					// neiorderNum = neiorderNum.substring(1);
					// }
				} else {
					String hql_procard = "from Procard where id in (select procardId from ProcessInforWWApplyDetail where id in(select wwDetailId from WaigouPlan where id = ?))";
					Procard p = (Procard) totalDao.getObjectByCondition(
							hql_procard, wgdd.getWaigouPlanId());
					if (p != null) {
						ywMarkId = p.getYwMarkId();
						rootMarkId = p.getRootMarkId();
						neiorderNum = p.getOrderNumber();
					}
				}
				wgdd.setYwmarkId(ywMarkId);
				wgdd.setRootMarkId(rootMarkId);
				wgdd.setNeiorderNum(neiorderNum);
				wgddList.add(wgdd);
			}
			try {
				String str1 = "";
				if (endtime != null && endtime.length() > 0) {
					str1 = endtime + "之前";
				}
				String filename = "送货单明细(含单价)" + str1;
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(StrutsStatics.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String(filename.getBytes("GB2312"),
										"8859_1") + ".xls");
				response.setContentType("application/msexcel");
				WritableWorkbook wwb = Workbook.createWorkbook(os);
				WritableSheet ws = wwb.createSheet(filename, 0);
				jxl.write.NumberFormat fivedps1 = new jxl.write.NumberFormat(
						"0.0000");
				WritableCellFormat fivedpsFormat1 = new WritableCellFormat(
						fivedps1);

				// wcfN.setBorder(jxl.format.Border.ALL,
				// jxl.format.BorderLineStyle.THIN);
				ws.setColumnView(0, 16);
				ws.setColumnView(1, 16);
				ws.setColumnView(2, 18);
				ws.setColumnView(4, 24);
				ws.setColumnView(5, 20);
				ws.setColumnView(6, 12);
				ws.setColumnView(13, 16);
				ws.addCell(new Label(0, 0, "序号"));
				ws.addCell(new Label(1, 0, "采购订单号"));
				ws.addCell(new Label(2, 0, "件号"));
				ws.addCell(new Label(3, 0, "零件名称"));
				ws.addCell(new Label(4, 0, "规格"));
				ws.addCell(new Label(5, 0, "单位"));
				ws.addCell(new Label(6, 0, "含税单价"));
				ws.addCell(new Label(7, 0, "送货单号"));
				ws.addCell(new Label(8, 0, "入库时间"));
				ws.addCell(new Label(9, 0, "入库单号"));
				ws.addCell(new Label(10, 0, "送货数量"));
				ws.addCell(new Label(11, 0, "合格数量"));
				ws.addCell(new Label(12, 0, "入库数量"));
				ws.addCell(new Label(13, 0, "仓区"));
				ws.addCell(new Label(14, 0, "总额(含税)"));
				ws.addCell(new Label(15, 0, "供应商"));
				ws.addCell(new Label(16, 0, "采购员"));
				ws.addCell(new Label(17, 0, "不含税单价"));
				ws.addCell(new Label(18, 0, "送货时间"));
				ws.addCell(new Label(19, 0, "供货属性"));
				ws.addCell(new Label(20, 0, "采购类型"));
				ws.addCell(new Label(21, 0, "业务件号"));
				ws.addCell(new Label(22, 0, "税率"));
				ws.addCell(new Label(23, 0, "状态"));
				DecimalFormat df = new DecimalFormat("#.####");
				for (int i = 0; i < wgddList.size(); i++) {
					WaigouDeliveryDetail wdd = (WaigouDeliveryDetail) list
							.get(i);
					ws.addCell(new Label(0, i + 1, i + 1 + ""));
					ws.addCell(new Label(1, i + 1, wdd.getCgOrderNum()));
					ws.addCell(new Label(2, i + 1, wdd.getMarkId()));
					ws.addCell(new Label(3, i + 1, wdd.getProName()));
					ws.addCell(new Label(4, i + 1, wdd.getSpecification()));
					ws.addCell(new Label(5, i + 1, wdd.getUnit()));
					Double hsPrice = 0d;
					if (wdd.getHsPrice() != null) {
						hsPrice = Double.parseDouble(df
								.format(wdd.getHsPrice()));
					}
					jxl.write.Number hsNumber = new jxl.write.Number(6, i + 1,
							hsPrice);
					ws.addCell(hsNumber);
					ws.addCell(new Label(7, i + 1, wdd.getWaigouDelivery()
							.getPlanNumber()));
					ws.addCell(new Label(8, i + 1,
							((wdd.getRukuTime() == null) || "".equals(wdd
									.getRukuTime())) ? "" : wdd.getRukuTime()
									.substring(0, 10)));

					ws.addCell(new Label(9, i + 1, wdd.getPrintNumber()));
					Double hgNumber = 0d;
					if (wdd.getHgNumber() != null) {
						hgNumber = Double.parseDouble(df.format(wdd
								.getHgNumber()));
					}
					Double shNumber = 0d;
					if (wdd.getShNumber() != null) {
						shNumber = Double.parseDouble(df.format(wdd
								.getShNumber()));
					}
					ws.addCell(new jxl.write.Number(10, i + 1, shNumber));
					ws.addCell(new jxl.write.Number(11, i + 1, hgNumber));
					ws.addCell(new jxl.write.Number(12, i + 1, (wdd
							.getYrukuNum() == null ? 0 : wdd.getYrukuNum())));
					ws.addCell(new Label(13, i + 1, wdd.getChangqu()));
					try {
						Double money = 0d;
						if (hgNumber > 0 && hsPrice > 0) {
							money = hgNumber * hsPrice;
						}
						ws.addCell(new jxl.write.Number(14, i + 1, money));
					} catch (Exception e) {
						ws.addCell(new Label(14, i + 1, ""));
					}
					ws.addCell(new Label(15, i + 1, wdd.getGysName()));
					ws.addCell(new Label(16, i + 1, wdd.getCaigouUserName()));
					Double price = 0d;
					if (wdd.getPrice() != null) {
						price = Double.parseDouble(df.format(wdd.getPrice()));
					}
					ws.addCell(new jxl.write.Number(17, i + 1, price));

					ws.addCell(new Label(18, i + 1,
							(wdd.getPrintTime() == null) ? "" : wdd
									.getPrintTime().substring(0, 10)));
					ws.addCell(new Label(19, i + 1, wdd.getKgliao()));
					ws.addCell(new Label(20, i + 1,
							wdd.getType().equals("外委") ? wdd.getWwType() : wdd
									.getType()));

					ws.addCell(new Label(21, i + 1, wdd.getYwmarkId()));
					ws.addCell(new Label(22, i + 1, wdd.getTaxprice() + ""));
					ws.addCell(new Label(23, i + 1, wdd.getStatus()));
				}
				wwb.write();
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}

		} else {
			String str = "";
			if (waigouDeliveryDetail.getWaigouDelivery() != null
					&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber() != null
					&& waigouDeliveryDetail.getWaigouDelivery().getPlanNumber()
							.length() > 0) {
				str = " and waigouDelivery.planNumber like '%"
						+ waigouDeliveryDetail.getWaigouDelivery()
								.getPlanNumber() + "%'";
			} else {
				waigouDeliveryDetail.setWaigouDelivery(null);
			}
			if (waigouDeliveryDetail.getStatus() != null
					&& waigouDeliveryDetail.getStatus().length() > 0) {
				String[] strarray = waigouDeliveryDetail.getStatus().split(",");
				if (strarray != null && strarray.length > 0) {
					String str1 = "";
					for (int i = 0; i < strarray.length; i++) {
						str1 += "," + "'" + strarray[i] + "'";
					}
					if (str1 != null && str1.length() > 1) {
						str1 = str1.substring(1);
						str += " and status in (" + str1 + ")";
					}
				}
				waigouDeliveryDetail.setStatus(null);
			}
			if (waigouDeliveryDetail.getKgliao() != null
					&& waigouDeliveryDetail.getKgliao().length() > 0) {
				String[] strarray = waigouDeliveryDetail.getKgliao().split(",");
				if (strarray != null && strarray.length > 0) {
					String str2 = "";
					for (int i = 0; i < strarray.length; i++) {
						str2 += "," + "'" + strarray[i] + "'";
					}
					if (str2 != null && str2.length() > 1) {
						str2 = str2.substring(1);
						str += " and kgliao in (" + str2 + ")";
					}
				}
				waigouDeliveryDetail.setKgliao(null);
			}
			if ("gys".equals(pageStatus)) {
				Users loginUser = Util.getLoginUser();
				str += " and waigouDelivery.userId=" + loginUser.getId();
			}
			// 单位 箱(包)数
			String hql = totalDao.criteriaQueries(waigouDeliveryDetail,
					"rukuTime", new String[] { firsttime, endtime }, "");
			hql += str + " ORDER BY rukuTime";
			// if (waigouDeliveryDetail.getStatus() != null
			// && waigouDeliveryDetail.getStatus().length() > 0) {
			// hql += " and status='" + waigouDeliveryDetail.getStatus() + "'";
			// }
			List<WaigouDeliveryDetail> list = totalDao.query(hql);
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(StrutsStatics.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition",
						"attachment; filename="
								+ new String("送货单明细".getBytes("GB2312"),
										"8859_1") + ".xls");
				response.setContentType("application/msexcel");
				WritableWorkbook wwb = Workbook.createWorkbook(os);
				WritableSheet ws = wwb.createSheet("送货单明细信息", 0);
				ws.setColumnView(0, 16);
				ws.setColumnView(1, 16);
				ws.setColumnView(2, 18);
				ws.setColumnView(4, 24);
				ws.setColumnView(5, 20);
				ws.setColumnView(6, 12);
				ws.setColumnView(13, 16);
				ws.setColumnView(15, 35);

				ws.addCell(new Label(0, 0, "序号"));
				ws.addCell(new Label(1, 0, "供应商"));
				ws.addCell(new Label(2, 0, "采购订单号"));
				ws.addCell(new Label(3, 0, "物料类别"));
				ws.addCell(new Label(4, 0, "件号"));
				ws.addCell(new Label(5, 0, "零件名称"));
				ws.addCell(new Label(6, 0, "规格"));
				ws.addCell(new Label(7, 0, "图号"));
				ws.addCell(new Label(8, 0, "版本"));
				ws.addCell(new Label(9, 0, "供货属性"));
				ws.addCell(new Label(10, 0, "单位"));
				ws.addCell(new Label(11, 0, "箱(包)数"));
				ws.addCell(new Label(12, 0, "送货数量"));
				ws.addCell(new Label(13, 0, "送货时间"));
				ws.addCell(new Label(14, 0, "确认数量"));
				ws.addCell(new Label(15, 0, "确认时间"));
				ws.addCell(new Label(16, 0, "合格数量"));
				ws.addCell(new Label(17, 0, "状态"));
				ws.addCell(new Label(18, 0, "备注"));
				ws.addCell(new Label(19, 0, "供应商"));
				ws.addCell(new Label(20, 0, "送货单号"));
				ws.addCell(new Label(21, 0, "采购类型"));
				for (int i = 0; i < list.size(); i++) {
					WaigouDeliveryDetail wdd = (WaigouDeliveryDetail) list
							.get(i);
					ws.addCell(new Label(0, i + 1, i + 1 + ""));
					ws.addCell(new Label(1, i + 1, wdd.getGysName()));
					ws.addCell(new Label(2, i + 1, wdd.getCgOrderNum()));
					ws.addCell(new Label(3, i + 1, wdd.getWgType()));
					ws.addCell(new Label(4, i + 1, wdd.getMarkId()));
					ws.addCell(new Label(5, i + 1, wdd.getProName()));
					ws.addCell(new Label(6, i + 1, wdd.getSpecification()));
					ws.addCell(new Label(7, i + 1, wdd.getTuhao()));
					ws.addCell(new Label(8, i + 1, wdd.getBanben()));
					ws.addCell(new Label(9, i + 1, wdd.getKgliao()));
					ws.addCell(new Label(10, i + 1, wdd.getUnit()));
					ws.addCell(new jxl.write.Number(11, i + 1,
							(wdd.getCtn() == null) ? 0 : wdd.getCtn()));
					ws.addCell(new jxl.write.Number(12, i + 1, (wdd
							.getShNumber() == null) ? 0 : wdd.getShNumber()));

					ws.addCell(new Label(13, i + 1,
							(wdd.getPrintTime() == null) ? "" : wdd
									.getPrintTime().substring(0, 10)));
					ws.addCell(new jxl.write.Number(14, i + 1, (wdd
							.getQrNumber() == null) ? 0 : wdd.getQrNumber()));
					ws.addCell(new Label(15, i + 1,
							(wdd.getQuerenTime() == null) ? "" : wdd
									.getQuerenTime().substring(0, 10)));
					ws.addCell(new jxl.write.Number(16, i + 1, (wdd
							.getHgNumber() == null) ? 0 : wdd.getHgNumber()));
					ws.addCell(new Label(17, i + 1, wdd.getStatus()));
					ws.addCell(new Label(18, i + 1, wdd.getRemarks()));
					ws.addCell(new Label(19, i + 1, wdd.getGysName()));
					ws.addCell(new Label(20, i + 1, wdd.getWaigouDelivery()
							.getPlanNumber()));
					ws.addCell(new Label(21, i + 1,
							wdd.getType().equals("外委") ? wdd.getWwType() : wdd
									.getType()));
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

	@Override
	public String backApply(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		WaigouOrder order = (WaigouOrder) totalDao.getObjectById(
				WaigouOrder.class, id);
		if (order == null) {
			return "无效的目标订单!";
		}
		if (order.getAddUserCode() == null
				|| !order.getAddUserCode().equals(user.getCode())) {
			return "对不起!您不是此订单的采购员不能反审此订单!";
		}
		if (!order.getStatus().equals("待通知")
				&& !order.getStatus().equals("待确认")
				&& !order.getStatus().equals("生产中")) {
			return "此订单当前状态为:" + order.getStatus() + ",不能反审!";
		}
		order.setStatus("待核对");
		order.setApplystatus("打回");
		totalDao.update(order);
		Set<WaigouPlan> wpSet = order.getWwpSet();
		if (wpSet != null && wpSet.size() > 0) {
			for (WaigouPlan wp : wpSet) {
				wp.setStatus("待核对");
				totalDao.update(wp);
			}
		}
		return "true";
	}

	@Override
	public String approvalNoPass(WaigouDeliveryDetail waigoudd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findNoPassDelivery(String isHandle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bhgth(Integer Id, Float buhegeNumber, Float hegeNumber,
			String fileName) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录系统!";
		}
		if (Id != null) {
			Float bhgecount = buhegeNumber;// 记录 用于判断状态
			Float hegecount = hegeNumber;
			DefectiveProduct dp = (DefectiveProduct) totalDao.get(
					DefectiveProduct.class, Id);
			if ("已处理".equals(dp.getStatus())) {
				return "该不良品处理单已确认过，无需重复处理!~";
			}
			Float bili = (Float) totalDao
					.getObjectByCondition(
							" select bili from YuanclAndWaigj where  markId = ? and bili is not null ",
							dp.getMarkId());
			Float zhuanhuanhg = 0f;
			Float zhuanhuanbhg = 0f;
			if (dp.getZhuanhuanNum() != null && dp.getZhuanhuanNum() > 0) {
				zhuanhuanhg = hegeNumber / bili;
				zhuanhuanbhg = buhegeNumber / bili;
			}
			float tuihuoNum = buhegeNumber;
			if (tuihuoNum > dp.getLlNumber()) {
				return "不合格数量不能大于来料数量!";
			}
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, dp.getWgddId());
			Float oldwgddhgNumber = wgdd.getHgNumber();
			// 待检库出库 如有不合格数量大于0时 不合格品入到不合格品库
			djkOutAndBhgIn(buhegeNumber, wgdd, bili, dp);
			String goodsClass = "待检库";
			if (!"外购来料不良".equals(dp.getType())
					&& !"研发来料不良".equals(dp.getType())) {
				goodsClass = "外购件库";
			}

			// 更新不合格品库库存 ；合格品出库
			boolean bool = true;

			String zzpd = "";

			// 更新送货单明细状态;
			boolean iszk = true;
			String status = "入库";
			if (!"外购在库不良".equals(dp.getType())
					&& !"外委在库不良".equals(dp.getType())
					&& !"研发在库不良".equals(dp.getType())) {
				status = "待入库";
				iszk = false;
			}
			if (hegeNumber == 0f) {
				if (!iszk) {
					status = "退货";
				}
			}
			WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
					WaigouPlan.class, wgdd.getWaigouPlanId());
			WaigouOrder waigouOrder = null;
			if (waigouPlan != null) {
				waigouOrder = (WaigouOrder) totalDao.getObjectById(
						WaigouOrder.class, waigouPlan.getWaigouOrder().getId());
			}
			String bldescribe = "";
			// if ("外购来料不良".equals(dp.getType())) {
			if (tuihuoNum > 0) {
				hegeNumber = wgdd.getQrNumber() - tuihuoNum;
			}

			wgdd.setStatus(status);
			if (iszk) {
				// wgdd.setBhgNumber(tuihuoNum);
				// wgdd.setHgNumber(hegeNumber);
				if(wgdd.getTuihuoNum()!=null){
					wgdd.setTuihuoNum(tuihuoNum+wgdd.getTuihuoNum());
				}else{
					wgdd.setTuihuoNum(tuihuoNum);
				}
			} else {
				wgdd.setHgNumber(hegeNumber);
				wgdd.setBhgNumber(tuihuoNum);
			}
			wgdd.setHegeRate(wgdd.getHgNumber() / wgdd.getQrNumber());// 合格率
			if (tuihuoNum == 0) {
				wgdd.setIsHege("合格");
			}
			wgdd.setAgaincheck(dp.getResult());
			bool = totalDao.update(wgdd);
			// 更新送货单状态
			WaigouDelivery waigouDelivery = (WaigouDelivery) totalDao
					.getObjectById(WaigouDelivery.class, wgdd
							.getWaigouDelivery().getId());
			waigouDelivery.setStatus(status);
			bool = totalDao.update(waigouDelivery);
			// 更新采购订单信息
			if (waigouPlan != null) {
				// 查询采购订单，更新状态
				waigouOrder.setStatus(status);

				bool = totalDao.update(waigouOrder);
				// 存在不合格数量，将采购订单表的未送数量加回去
				String jynumberMes = "";
				waigouPlan.setSyNumber(waigouPlan.getSyNumber() + tuihuoNum);
				waigouPlan.setQsNum(waigouPlan.getQsNum() - tuihuoNum);// 更新签收数量
				waigouPlan.setHgNumber(waigouPlan.getHgNumber() - tuihuoNum);
				waigouPlan.setKeruku(waigouPlan.getHgNumber());
				if (iszk) {
					if (waigouPlan.getHasruku() != null
							&& waigouPlan.getHasruku() > tuihuoNum) {
						waigouPlan.setHasruku(waigouPlan.getHasruku()
								- tuihuoNum);
					}
					if (waigouPlan.getKeruku() != null
							&& waigouPlan.getKeruku() > tuihuoNum) {
						waigouPlan
								.setKeruku(waigouPlan.getKeruku() - tuihuoNum);
					}
					if (tuihuoNum > 0) {
						// waigouPlan.setStatus("退货");
					}
				}
				// 更新物料需求上的数据。
				if (waigouPlan.getMopId() != null) {
					ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
							ManualOrderPlan.class, waigouPlan.getMopId());
					if (mop != null) {
						if (iszk) {
							if (hegecount > 0) {
								mop.setQsCount(mop.getQsCount() + hegecount);
								mop.setHgCount(mop.getHgCount() + hegecount);
								mop.setRukuNum(mop.getRukuNum() + hegecount);

								Float number = hegecount;
								Set<ManualOrderPlanDetail> modset = mop
										.getModSet();
								for (ManualOrderPlanDetail mod : modset) {
									if (number > 0) {
										if (number >= (mod.getCgnumber() - mod
												.getRukuNum())) {
											number -= (mod.getCgnumber() - mod
													.getRukuNum());
											mod.setRukuNum(mod.getCgnumber());
										} else {
											mod.setRukuNum(mod.getRukuNum()
													+ number);
											number = 0f;
										}
									}
									totalDao.update(mod);
								}
							} else {
								mop.setWshCount(mop.getWshCount() == null ? 0
										: mop.getWshCount() + tuihuoNum);
							}
						} else {
							if (mop.getWshCount() != null
									&& mop.getWshCount() >= tuihuoNum) {
								mop.setWshCount(mop.getWshCount() + tuihuoNum);
							}
							if (mop.getHgCount() != null) {
								mop.setHgCount(mop.getHgCount() - tuihuoNum);
							}
						}
						if (tuihuoNum > 0) {
							// mop.setStatus("退货");
						}
						totalDao.update(mop);
					}

				}

				jynumberMes = "(不合格：" + tuihuoNum + wgdd.getUnit() + ")";
				waigouPlan.setStatus("待入库");
				String wlwzdtmes = "质量检验:<br/>供应商:"
						+ waigouDelivery.getGysName()
						+ "<br/>采购订单号:<a href='WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id="
						+ waigouDelivery.getId() + "'>"
						+ waigouDelivery.getPlanNumber() + "</a><br/>：确认不合格数量"
						+ tuihuoNum + "<br/><br>确认合格数量:</br>" + hegeNumber
						+ " 当前位置:" + wgdd.getQrWeizhi() + "<hr/>";
				// waigouPlan.setWlWeizhiDt(waigouPlan.getWlWeizhiDt()
				// + wlwzdtmes);
				// waigouPlan.setWlWeizhiDt("");
				WlWeizhiDt wDt = new WlWeizhiDt(null, waigouPlan.getId(), null,
						wlwzdtmes);
				totalDao.save(wDt);
				/************************ 演示时设置为空 ****************************/
				// wlwzdtmes = "";
				// waigouPlan.setWlWeizhiDt(waigouPlan.getWlWeizhiDt()
				// + wlwzdtmes);

				if (waigouPlan.getHgNumber() != null) {
					waigouPlan
							.setHgNumber(waigouPlan.getHgNumber() - tuihuoNum);
				} else {
					waigouPlan.setHgNumber(hgNumber);
				}
				waigouPlan.setStatus(status);
				bool = totalDao.update(waigouPlan);

				// 查询采购明细对应生产批次
				// String hql_procard =
				// "from Procard where id in (select procardId from ProcardWGCenter where wgOrderId=?) ";
				String hql_procard = " from Procard where id in (select procardId from ManualOrderPlanDetail where manualPlan.id = ?)";
				List list_procrd = totalDao.query(hql_procard, waigouPlan
						.getMopId());
				for (int j = 0; j < list_procrd.size(); j++) {
					Procard procard = (Procard) list_procrd.get(j);
					procard.setWlstatus("待入库");
					// if (procard.getWlWeizhiDt() == null) {
					// procard.setWlWeizhiDt("");
					// }
					// procard.setWlWeizhiDt(procard.getWlWeizhiDt()
					// + wlwzdtmes);
					if (!totalDao.update(procard)) {
						bool = false;
						break;
					}
					WlWeizhiDt wDt1 = new WlWeizhiDt(procard.getId(), null,
							null, wlwzdtmes);
					totalDao.save(wDt1);

				}
				// 更新来料日报表;
				String hql_wgdSheet = "  from WaigouDailySheet where wgddId = ?";
				WaigouDailySheet wgdSheet1 = (WaigouDailySheet) totalDao
						.getObjectByCondition(hql_wgdSheet, wgdd.getId());
				if (wgdSheet1 != null) {
					wgdSheet1.setZzpd(zzpd);// 最终判定
					wgdSheet1.setZjTime(Util.getDateTime());// 终检时间
					wgdSheet1.setZjUser(user.getName());// 终检人
					wgdSheet1.setZjUserId(user.getId());// 终检人Id
					wgdSheet1.setZjhgNumber(hegeNumber.floatValue());// 终检合格数量
					wgdSheet1.setZjbhgNumber(buhegeNumber.floatValue());// 终检不合格数量
					bldescribe = wgdSheet1.getBldescribe();
					wgdSheet1.setStatus("已终检");
					totalDao.update(wgdSheet1);
				}
			}
			// }
			if (dp.getDbNumber() != null && dp.getDbNumber() - tuihuoNum > 0) {
				// 在库不良是时，
			}
			if (tuihuoNum > 0) {

				// 添加退货单明细
				ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, dp
						.getGysId());
				addReturnSingle(zhuser, dp, wgdd, dp.getRamk(), user);
				//检验缺陷类型记录次数。
				recordGysBad(dp);
				if (tuihuoNum > 0) {
					// 发送消息给采购员;
					AlertMessagesServerImpl.addAlertMessages("采购订单管理",
							"您添加的采购订单不合格品确认出,不合格数量:" + tuihuoNum + ",合格数量:"
									+ hgNumber + ",确认数量" + wgdd.getQrNumber()
									+ ",订单编号:" + waigouOrder.getPlanNumber()
									+ "件号:" + waigouPlan.getMarkId(), "1",
							waigouPlan.getUserCode());
					ZhUser zhuer = (ZhUser) totalDao.get(ZhUser.class, wgdd
							.getGysId());
					// 发送消息给供应商
					AlertMessagesServerImpl.addAlertMessages("不良品管理(供应商)",
							"您添加送货单不合格品确认出,不合格数量:" + tuihuoNum + ",合格数量:"
									+ hgNumber + ",确认数量" + wgdd.getQrNumber()
									+ ",送货单编号:"
									+ wgdd.getWaigouDelivery().getPlanNumber()
									+ "件号:" + wgdd.getMarkId() + "。请及时领回。",
							"1", zhuer.getUsercode());
				}
				if (hegeNumber != null && hegeNumber > 0) {
					// 发消息给仓库入库
					String hql2 = " select u.id from Users u join u.moduleFunction f  where f.functionName =?";
					List<Integer> userIdList = totalDao.query(hql2, "外购入库");
					Integer[] userIds = null;
					if (userIdList != null && userIdList.size() > 0) {
						userIds = new Integer[userIdList.size()];
						for (int i = 0; i < userIdList.size(); i++) {
							userIds[i] = userIdList.get(i);
						}
					}
					AlertMessagesServerImpl.addAlertMessages("外购外委入库提醒",
							"送货单号:" + wgdd.getWaigouDelivery().getPlanNumber()
									+ "," + "件号:" + wgdd.getMarkId() + ",名称:"
									+ wgdd.getProName() + ", 检验批次:"
									+ wgdd.getExamineLot() + ","
									+ "已二次确认完毕,确认合格数量:" + hegeNumber + "("
									+ wgdd.getUnit() + "),可入库数量:" + hegeNumber
									+ "(" + wgdd.getUnit() + ")。请及时入库",
							userIds, "", true);
				}
				if (!"外购来料不良".equals(dp.getType())) {
					// 发送消息给财务
				}
			}
			return bool + "";
		}
		return null;
	}

	@Override
	public Object[] findWgDSheetList(WaigouDailySheet wgdSheet, int parseInt,
			int pageSize, String firsttime, String endtime, String pageStatus) {
		if (wgdSheet == null) {
			wgdSheet = new WaigouDailySheet();
		}
		String yclSql = "";
		String wgType = "";
		if (wgdSheet.getWgType() != null && wgdSheet.getWgType().length() > 0) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgdSheet.getWgType());
			if (category != null) {
				getWgtype(category);
			}
			yclSql = " and wgType in (" + strbu.toString().substring(1) + ")";
			wgType = wgdSheet.getWgType();
			wgdSheet.setWgType(null);
		}
		String zzpdhql = "";
		if(wgdSheet.getZzpd() == null||"".equals(wgdSheet.getZzpd())){
			
		}else if("合格".equals(wgdSheet.getZzpd())||"不合格".equals(wgdSheet.getZzpd())){
			zzpdhql =" and zzpd = '"+wgdSheet.getZzpd()+"'";
		}else{
			zzpdhql =" and zzpd is null";
		}
		String ktime ="";
		String etime ="";
		if (null != firsttime && !"".equals(firsttime)) {
			ktime += " and addTime >= '" + firsttime + "'";
		}
		if (null != endtime && !"".equals(endtime)) {
			etime += " and addTime <= '" + endtime + "'";
		}
		String hql = totalDao.criteriaQueries(wgdSheet, null,"addTime",
				 "zzpd");
		hql += yclSql+zzpdhql+ktime+etime;
		List<WaigouDailySheet> wgdSheetList = totalDao.findAllByPage(hql
				+ " order by addTime desc", parseInt, pageSize);
		int count = totalDao.getCount(hql + " order by addTime desc");
		Object[] obj = new Object[] { wgdSheetList, count };

		wgdSheet.setWgType(wgType);
		return obj;
	}

	@Override
	public void exportExcelwgdSheet(WaigouDailySheet wgdSheet,
			String firsttime, String endtime, String pageStatus) {
		if (wgdSheet == null) {
			wgdSheet = new WaigouDailySheet();
		}
		String yclSql = "";
		String wgType = "";
		if (wgdSheet.getWgType() != null && wgdSheet.getWgType().length() > 0) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", wgdSheet.getWgType());
			if (category != null) {
				getWgtype(category);
			}
			yclSql = " and wgType in (" + strbu.toString().substring(1) + ")";
			wgType = wgdSheet.getWgType();
			wgdSheet.setWgType(null);
		}
		String hql = totalDao.criteriaQueries(wgdSheet, "addTime",
				new String[] { firsttime, endtime }, "");
		hql += yclSql;
		wgdSheet.setWgType(wgType);
		List<WaigouDailySheet> wgdSheetList = totalDao.query(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			String filename = "来料日报表";
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(filename.getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet(filename, 0);
			ws.setColumnView(2, 18);
			ws.setColumnView(3, 28);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 24);
			ws.setColumnView(6, 12);
			ws.setColumnView(14, 30);
			ws.setColumnView(16, 30);

			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "供应商"));
			ws.addCell(new Label(2, 0, "物料类别"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "送货单号"));
			ws.addCell(new Label(5, 0, "采购订单号"));
			ws.addCell(new Label(6, 0, "产品名称"));
			ws.addCell(new Label(7, 0, "版本"));
			ws.addCell(new Label(8, 0, "供料属性"));
			ws.addCell(new Label(9, 0, "产品图号"));
			ws.addCell(new Label(10, 0, "来料数量"));
			ws.addCell(new Label(11, 0, "检验批次"));
			ws.addCell(new Label(12, 0, "来料报检日期"));
			ws.addCell(new Label(13, 0, "抽检人"));
			ws.addCell(new Label(14, 0, "抽检时间"));
			ws.addCell(new Label(15, 0, "抽检数量"));
			ws.addCell(new Label(16, 0, "抽检合格数量"));
			ws.addCell(new Label(17, 0, "抽检不合格数量"));
			ws.addCell(new Label(18, 0, "确认时间"));
			ws.addCell(new Label(19, 0, "确认人"));
			ws.addCell(new Label(20, 0, "确认合格数量"));
			ws.addCell(new Label(21, 0, "确认不合格数量"));
			ws.addCell(new Label(22, 0, "最终判定"));
			for (int i = 0; i < wgdSheetList.size(); i++) {
				WaigouDailySheet wdd = (WaigouDailySheet) wgdSheetList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, wdd.getGysjc()));
				ws.addCell(new Label(2, i + 1, wdd.getWgType()));
				ws.addCell(new Label(3, i + 1, wdd.getMarkId()));
				ws.addCell(new Label(4, i + 1, wdd.getShOrderNum()));
				ws.addCell(new Label(5, i + 1, wdd.getCgOrderNum()));
				ws.addCell(new Label(6, i + 1, wdd.getProName()));
				ws.addCell(new Label(7, i + 1, wdd.getBanbenNumber()));
				ws.addCell(new Label(8, i + 1, wdd.getKgliao()));
				ws.addCell(new Label(9, i + 1, wdd.getTuhao()));
				ws.addCell(new jxl.write.Number(10, i + 1,
						(wdd.getLlNumber() != null) ? wdd.getLlNumber() : 0));
				ws.addCell(new Label(11, i + 1, wdd.getExamineLot()));
				ws.addCell(new Label(12, i + 1, wdd.getLiaodate()));
				ws.addCell(new Label(13, i + 1, wdd.getCjUser()));
				ws.addCell(new Label(14, i + 1, wdd.getCjTime()));
				ws.addCell(new jxl.write.Number(15, i + 1,
						(wdd.getCjNumber() != null) ? wdd.getCjNumber() : 0));
				ws
						.addCell(new jxl.write.Number(16, i + 1, (wdd
								.getCjhgNumber() != null) ? wdd.getCjhgNumber()
								: 0));
				ws.addCell(new jxl.write.Number(17, i + 1, (wdd
						.getCjbhgNumber() != null) ? wdd.getCjbhgNumber() : 0));
				ws.addCell(new Label(18, i + 1, wdd.getZjTime()));
				ws.addCell(new Label(19, i + 1, wdd.getZjUser()));
				ws
						.addCell(new jxl.write.Number(20, i + 1, (wdd
								.getZjhgNumber() != null) ? wdd.getZjhgNumber()
								: 0));
				ws.addCell(new jxl.write.Number(21, i + 1, (wdd
						.getZjbhgNumber() != null) ? wdd.getZjbhgNumber() : 0));
				ws.addCell(new Label(22, i + 1, wdd.getZzpd()));
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
	public String printlingliaodan(Integer id) {
		if (id != null && id > 0) {
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
			String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
			GoodsStore t = (GoodsStore) totalDao
					.getObjectByCondition(hql_gt, wgdd.getWaigouDelivery()
							.getId(), wgdd.getMarkId(), "不合格品库");
			// 更新不合格品库库存 不合格品出库
			boolean bool = true;
			if (t != null) {
				String hql = "from Goods where goodsMarkId = ? ";
				if (t.getGoodsStoreWarehouse() != null
						&& t.getGoodsStoreWarehouse().length() > 0) {
					hql += " and goodsClass='" + t.getGoodsStoreWarehouse()
							+ "'";
				}
				if (t.getGoodHouseName() != null
						&& t.getGoodHouseName().length() > 0) {
					hql += " and goodHouseName='" + t.getGoodHouseName() + "'";
				}
				if (t.getGoodsStorePosition() != null
						&& t.getGoodsStorePosition().length() > 0) {
					hql += " and goodsPosition='" + t.getGoodsStorePosition()
							+ "'";
				}
				if (t.getBanBenNumber() != null
						&& t.getBanBenNumber().length() > 0) {
					hql += " and banBenNumber='" + t.getBanBenNumber() + "'";
				}
				if (t.getKgliao() != null && t.getKgliao().length() > 0) {
					hql += " and kgliao='" + t.getKgliao() + "'";
				}
				if (t.getGoodsStoreLot() != null
						&& t.getGoodsStoreLot().length() > 0) {
					hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
				}
				if (t.getHsPrice() != null && t.getHsPrice() > 0) {
					hql += " and goodsPrice like '%" + t.getHsPrice() + "%'";
				}
				Goods s = (Goods) totalDao.getObjectByCondition(hql,
						new Object[] { t.getGoodsStoreMarkId() });
				if (s != null) {
					s.setGoodsCurQuantity(s.getGoodsCurQuantity()
							- wgdd.getBhgNumber());// 不合格品出库
					bool = totalDao.update(s);
				}
			}
			// 更新送货单明细状态
			wgdd.setThStatus("已领");
			return totalDao.update(wgdd) + "";
		}
		return "请刷新后重试，谢谢!";
	}

	@Override
	public WaigouDeliveryDetail findWgddById(Integer id) {
		if (id != null) {
			return (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
		}
		return null;
	}

	@Override
	public Object[] findwgdSheetById(Integer id) {
		if (id != null) {
			WaigouDailySheet wds = (WaigouDailySheet) totalDao.get(
					WaigouDailySheet.class, id);
			List<OsRecordScope> osrsList = totalDao.query(
					" from OsRecordScope where  osRecord.id = ? ", wds
							.getScopeId());
			Object[] obj = { wds, osrsList };
			return obj;
		}
		return null;
	}

	@Override
	public boolean updatewgdSheet(WaigouDailySheet wgdSheet) {
		if (wgdSheet != null) {
			WaigouDailySheet w = (WaigouDailySheet) totalDao.get(
					WaigouDailySheet.class, wgdSheet.getId());
			w.setBxgczfx(wgdSheet.getBxgczfx());
			w.setCsROHS(wgdSheet.getCsROHS());
			w.setHbNumber(wgdSheet.getHbNumber());
			w.setSaltspraytest(wgdSheet.getSaltspraytest());
			w.setReamk(wgdSheet.getReamk());
			if (wgdSheet.getBxgczfxfile() != null
					&& wgdSheet.getBxgczfxfile().length() > 0) {
				w.setBxgczfxfile(wgdSheet.getBxgczfxfile());
			}
			if (wgdSheet.getCsROHSfile() != null
					&& wgdSheet.getCsROHSfile().length() > 0) {
				w.setCsROHSfile(wgdSheet.getCsROHSfile());
			}
			if (wgdSheet.getSaltspraytestfile() != null
					&& wgdSheet.getSaltspraytestfile().length() > 0) {
				w.setSaltspraytestfile(wgdSheet.getSaltspraytestfile());
			}
			if (wgdSheet.getFlammabilitytestfile() != null
					&& wgdSheet.getFlammabilitytestfile().length() > 0) {
				w.setFlammabilitytestfile(wgdSheet.getFlammabilitytestfile());
			}
			return totalDao.update(w);
		}
		return false;
	}

	@Override
	public Object[] findAllDefectiveProduct(DefectiveProduct dp, int parseInt,
			int pageSize, String firsttime, String endtime, String pageStatus,
			String tag) {
		if (dp == null) {
			dp = new DefectiveProduct();
		}
		String str = "";
		Users user = Util.getLoginUser();
		if ("dqr".equals(pageStatus)) {
			str = " status = '待确认' and (epStatus is null or epStatus = '打回') ";// 
		} else if ("gys".equals(pageStatus)) {
			str = " gysUsersId = " + user.getId();
		}
		if ("wg".equals(tag)) {
			dp.setWlType("外购");
		} else if ("ww".equals(tag)) {
			dp.setWlType("外委");
		}
		String hql = totalDao.criteriaQueries(dp, "checkTime", new String[] {
				firsttime, endtime }, str);
		List<DefectiveProduct> dpList = totalDao.findAllByPage(hql
				+ " order by addTime desc", parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] obj = new Object[] { dpList, count };
		return obj;
	}

	@Override
	public Object[] findReturnSingle(ReturnSingle rs, int parseInt,
			int pageSize, String firsttime, String endtime, String pageStatus,
			String tag) {
		if (rs == null) {
			rs = new ReturnSingle();
		}
		Users user = Util.getLoginUser();
		String str = "";
		if ("gys".equals(pageStatus)) {
			str += " and gysuserId = " + user.getId() + " and  epstatus = '同意'";
		} else if ("dl".equals(pageStatus)) {
			str += " and status = '待领'";
		} else if ("wty".equals(pageStatus)) {
			str += " and status = '待领' and epstatus <> '同意' ";
		}
		if ("zkbl".equals(tag)) {
			str += " and type='退货单' ";
		} else if ("llbl".equals(tag)) {
			str += " and type='采购检验不良处理单' ";
		}
		String hql = totalDao.criteriaQueries(rs, "addTime", new String[] {
				firsttime, endtime }, "");
		hql += str + " order by id desc";
		List<ReturnSingle> rsList = totalDao.findAllByPage(hql, parseInt,
				pageSize);
		int count = totalDao.getCount(hql);
		Object[] obj = new Object[] { rsList, count };
		return obj;
	}

	@Override
	public Object[] findReturnsDetails(ReturnsDetails rds, int parseInt,
			int pageSize, String pageStatus, String tag, String startDate,
			String endDate) {

		if (rds == null) {
			rds = new ReturnsDetails();
		}
		Users user = Util.getLoginUser();
		String str = "";
		if ("gys".equals(pageStatus)) {
			str = " and returnSingle.gysuserId = " + user.getId()
					+ " and returnSingle.epstatus = '同意'";
		} else if ("dl".equals(pageStatus)) {
			str = " and status = '待领' ";
		} else if ("wty".equals(pageStatus)) {
			str = " and status = '待领' and returnSingle.epstatus <> '同意' ";
		}
		if ("zkbl".equals(tag)) {
			ReturnSingle rs = new ReturnSingle();
			rs.setType("退货单");
			rds.setReturnSingle(rs);
		} else if ("llbl".equals(tag)) {
			ReturnSingle rs = new ReturnSingle();
			rs.setType("采购检验不良处理单");
			rds.setReturnSingle(rs);
		}
		if (rds.getReturnSingle() != null) {
			if (rds.getReturnSingle().getType() != null
					&& rds.getReturnSingle().getType().length() > 0) {
				str += " and returnSingle.id in (select id from ReturnSingle where type = '"
						+ rds.getReturnSingle().getType() + "')";
			}

		}
		String startTimeHql = "";
		String endTimeHql = "";
		if (startDate != null && !"".equals(startDate)) {
			startTimeHql = " and approvalTime >= '" + startDate + " 00:00:00'";
		}
		if (endDate != null && !"".equals(endDate)) {
			endTimeHql = " and approvalTime <= '" + endDate + " 23:59:59'";
		}
		String hql = totalDao.criteriaQueries(rds, null, "returnSingle");
		List<ReturnsDetails> rdsList = totalDao.findAllByPage(hql + str
				+ startTimeHql + endTimeHql + " order by id desc", parseInt,
				pageSize);
		int count = totalDao.getCount(hql + str + startTimeHql + endTimeHql);
		Object[] obj = new Object[] { rdsList, count };
		return obj;
	}

	@Override
	public Object[] findReturnsDetailsByrsId(Integer id) {
		if (id != null) {
			ReturnSingle rs = (ReturnSingle) totalDao.get(ReturnSingle.class,
					id);
			String hql = " from ReturnsDetails where returnSingle.id=?";
			List<ReturnsDetails> rdsList = totalDao.query(hql, id);
			Object[] obj = new Object[] { rs, rdsList };
			return obj;
		}
		return null;
	}

	@Override
	public DefectiveProduct findDefectiveProductById(Integer id) {
		if (id != null) {
			return (DefectiveProduct) totalDao.get(DefectiveProduct.class, id);
		}
		return null;
	}

	@Override
	public String updateRsToPrint(ReturnSingle rs) {
		Object[] obj = null;
		if (rs != null) {
			obj = findReturnsDetailsByrsId(rs.getId());
		}
		if (obj != null && obj.length == 2) {
			ReturnSingle rs1 = (ReturnSingle) obj[0];
			List<ReturnsDetails> rdsList = (List<ReturnsDetails>) obj[1];
			if (rdsList != null && rdsList.size() > 0) {
				for (ReturnsDetails rds : rdsList) {
					rds.setStatus("已领");
					// 更新 不合格品库存； 不合格品出库
					WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao
							.get(WaigouDeliveryDetail.class, rds.getWgddId());
					String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?" +
							" and goodsStoreLot =?  ";
					GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(
							hql_gt, wgdd.getId(), wgdd.getMarkId() ,"不合格品库",rds.getExamineLot());
					if (t != null) {
						String hql = "from Goods where goodsMarkId = ? and goods_CurQuantity>0";
						if (t.getGoodsStoreWarehouse() != null
								&& t.getGoodsStoreWarehouse().length() > 0) {
							hql += " and goodsClass='"
									+ t.getGoodsStoreWarehouse() + "'";
						}
						if (t.getGoodsStoreLot() != null
								&& t.getGoodsStoreLot().length() > 0) {
							hql += " and  goodsLotId='" + t.getGoodsStoreLot()
									+ "'";
						}
						Goods s = (Goods) totalDao.getObjectByCondition(hql,
								new Object[] { t.getGoodsStoreMarkId() });
						if (s != null) {
							s.setGoodsCurQuantity(s.getGoodsCurQuantity()
									- rds.getThNumber());
							// 不合格品出库 添加出库记录
							Sell sell = new Sell();
							sell.setSellMarkId(s.getGoodsMarkId());// 件号
							sell.setSellLot(s.getGoodsLotId());//批次
							sell.setSellWarehouse(s.getGoodsClass());// 库别
							sell.setGoodHouseName(s.getGoodHouseName());// 仓区
							sell.setKuwei(s.getGoodsPosition());// 库位
							sell.setBanBenNumber(s.getBanBenNumber());// 版本号
							sell.setKgliao(s.getKgliao());// 供料属性
							sell.setWgType(s.getWgType());// 物料类别
							sell.setSellGoods(s.getGoodsFullName());// 品名
							sell.setSellFormat(s.getGoodsFormat());// 规格
							sell.setSellLot(s.getGoodsLotId());
							if (s.getGoodsCurQuantity() < 0) {// 强制修正，使库存不为负
								s.setGoodsCurQuantity(0f);
							}
							sell.setSellCount(rds.getThNumber());// 出库数量
							sell.setSellUnit(s.getGoodsUnit());// 单位
							sell.setSellSupplier(s.getGoodsSupplier());// 供应商
							sell.setSellTime(Util.getDateTime());// 出库时间
							sell.setTuhao(s.getTuhao());// 图号
							sell.setBedit(false);// 出库权限
							sell.setBedit(false);// 编辑权限
							sell.setPrintStatus("YES");// 打印状态
							sell.setStyle("不合格品出库");
							totalDao.save(sell);
							totalDao.update(s);
						}
						/*** 生成应付账单信息 ***/
						if ("退货单".equals(rs1.getType())) {
							Float hgNum = wgdd.getHgNumber();
							Float yrukuNum = wgdd.getYrukuNum();
							String printNumber = wgdd.getPrintNumber();
							wgdd.setHgNumber(-rds.getThNumber());
							wgdd.setYrukuNum(-rds.getThNumber());
							wgdd.setPrintNumber(rs1.getPlanNum());
							corePayableServer.addCorePayable(wgdd, s);
							wgdd.setHgNumber(hgNum);
							wgdd.setYrukuNum(yrukuNum);
							wgdd.setPrintNumber(printNumber);
						}
					}
					totalDao.update(rds);
					// 更新送货单使供应商可以重新送货;

				}
			}
			rs1.setZhidan(rs.getZhidan());
			rs1.setCaigou(rs.getCaigou());
			rs1.setCaikuzg(rs.getCaikuzg());
			rs1.setCangguan(rs.getCangguan());
			rs1.setCaigouzg(rs.getCaigouzg());
			rs1.setIqc(rs.getIqc());
			rs1.setPrintTime(Util.getDateTime());
			rs1.setPrintUser(Util.getLoginUser().getName());
			rs1.setStatus("已领");
			return totalDao.update(rs1) + "";
		} else {
			return "请刷新后重试谢谢!";
		}
	}

	@Override
	public String lingliaoBywgddId(Integer id, String barcod, Float lingliaoNum) {
		if (barcod == null || barcod.length() == 0) {
			return "请刷卡!";
		}
		Users user = (Users) totalDao.getObjectByCondition(
				" from Users where cardId = ?", barcod);
		if (user == null) {
			return "卡号不存在!";
		}
		if ("".equals(user.getOnWork())) {
			return "该员工已离职!";
		}
		if (lingliaoNum == null) {
			return "请输入领料数量!";
		}
		WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
				WaigouDeliveryDetail.class, id);
		String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
		GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_gt, wgdd
				.getId(), wgdd.getMarkId(), "外协库");
		boolean bool = false;
		if (t != null) {
			String hql = "from Goods where goodsMarkId = ? ";
			if (t.getGoodsStoreWarehouse() != null
					&& t.getGoodsStoreWarehouse().length() > 0) {
				hql += " and goodsClass='" + t.getGoodsStoreWarehouse() + "'";
			}
			if (t.getGoodHouseName() != null
					&& t.getGoodHouseName().length() > 0) {
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
			if (t.getGoodsStoreLot() != null
					&& t.getGoodsStoreLot().length() > 0) {
				hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
			}
			Goods s = (Goods) totalDao.getObjectByCondition(hql,
					new Object[] { t.getGoodsStoreMarkId() });
			if (s != null && barcod != null && barcod.length() > 0) {
				// 出库 添加出库记录
				s.setGoodsCurQuantity(s.getGoodsCurQuantity() - lingliaoNum);
				Sell sell = new Sell();
				sell.setSellMarkId(s.getGoodsMarkId());// 件号
				sell.setSellWarehouse(s.getGoodsClass());// 库别
				sell.setGoodHouseName(s.getGoodHouseName());// 仓区
				sell.setKuwei(s.getGoodsPosition());// 库位
				sell.setBanBenNumber(s.getBanBenNumber());// 版本号
				sell.setKgliao(s.getKgliao());// 供料属性
				sell.setWgType(s.getWgType());// 物料类别
				sell.setSellGoods(s.getGoodsFullName());// 品名
				sell.setSellFormat(s.getGoodsFormat());// 规格
				if (s.getGoodsCurQuantity() < 0) {// 强制修正，使库存不为负
					s.setGoodsCurQuantity(0f);
					sell.setSellCount(0f);// 出库数量
				} else {
					sell.setSellCount(lingliaoNum);// 出库数量
				}
				sell.setSellUnit(s.getGoodsUnit());// 单位
				sell.setSellSupplier(s.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setTuhao(s.getTuhao());// 图号
				sell.setPrintStatus("NO");// 打印状态
				sell.setStyle("领料出库");
				if (user != null) {
					sell.setSellCharger(user.getName());
					sell.setLingliaocardId(barcod);
					sell.setLingliaoName(user.getName());
					sell.setLingliaoUserId(user.getId());
				}
				totalDao.save(sell);
				bool = totalDao.update(s);

			}
		} else {
			return "没有找到相关库存";
		}
		if (bool) {
			wgdd.setLingliaoNum((wgdd.getLingliaoNum() == null) ? lingliaoNum
					: wgdd.getLingliaoNum() + lingliaoNum);
			if (wgdd.getLingliaoNum().equals(wgdd.getHgNumber())) {
				wgdd.setStatus("已领");
			}
			return totalDao.update(wgdd) + "";
		}

		return "error";
	}

	@Override
	public void Sendmsg(Integer id, String pageStatus) {
		WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
				WaigouDeliveryDetail.class, id);
		if ("jy".equals(pageStatus)) {
			// 发消息给检验员;
			String hql_user = "SELECT u.id from Category c join c.userSet u where c.name=?";
			List<Integer> userIdList = totalDao.query(hql_user, wgdd
					.getWgType());
			Integer[] userIds = null;
			if (userIdList != null && userIdList.size() > 0) {
				userIds = new Integer[userIdList.size()];
				for (int i = 0; i < userIdList.size(); i++) {
					userIds[i] = userIdList.get(i);
				}
			}

			AlertMessagesServerImpl.addAlertMessages("检验提醒", "送货单号:"
					+ wgdd.getWaigouDelivery().getPlanNumber() + ",件号:"
					+ wgdd.getMarkId() + ",检验批次:" + wgdd.getExamineLot() + ","
					+ "名称:" + wgdd.getProName() + ",已签收。确认数量:"
					+ wgdd.getQrNumber() + "(" + wgdd.getUnit() + ")。请及时检验",
					userIds, "", true, "false");
		} else if ("rk".equals(pageStatus)) {
			// 发消息给仓库入库
			if (wgdd.getQrUsersId() != null) {
				Integer[] userIds = { wgdd.getQrUsersId() };
				AlertMessagesServerImpl.addAlertMessages("外购外委入库提醒", "送货单号:"
						+ wgdd.getWaigouDelivery().getPlanNumber() + ",件号:"
						+ wgdd.getMarkId() + "" + ",名称:" + wgdd.getProName()
						+ ",检验批次:" + wgdd.getExamineLot() + "," + "已检验完毕,合格数量:"
						+ wgdd.getHgNumber() + "(" + wgdd.getUnit()
						+ "),可入库数量:" + wgdd.getHgNumber() + "("
						+ wgdd.getUnit() + ")。请及时入库", userIds, "", true,
						"false");
			}

		}
	}

	@Override
	public Object[] showPrice(Integer[] arrayId) {
		if (arrayId != null && arrayId.length > 0) {
			String msg = "";
			List<ManualOrderPlan> mopList = new ArrayList<ManualOrderPlan>();
			List zhUsersList = new ArrayList();
			List<List> list_zhuser = new ArrayList<List>();
			Float sumNum = 0f;
			for (Integer id : arrayId) {
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, id);
				mopbili(mop);
				String time = Util.getDateTime();
				String hql_price = "from Price where partNumber=? and kgliao=?   and '"
						+ time
						+ "'>= pricePeriodStart and ('"
						+ time
						+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
				if (mop.getBanben() != null && mop.getBanben().length() > 0) {
					hql_price += " and banbenhao='" + mop.getBanben() + "'";
				}
				if (mop.getCategory() != null && mop.getCategory().length() > 0) {
					if (mop.getCategory().equals("辅料")) {
						hql_price += " and productCategory = '"
								+ mop.getCategory() + "'";// 区分辅料
					}
					if (mop.getCategory().equals("外购")) {
						hql_price += " and produceType = '" + mop.getCategory()
								+ "'";
					}

				}
				List<Price> list_price = totalDao.query(hql_price
						+ " order by firstnum", mop.getMarkId(), mop
						.getKgliao());
				List<ZhUser> zhUsersList0 = new ArrayList<ZhUser>();
				boolean bool = true;
				for (Price price : list_price) {
					ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
							.getGysId());
					if (zhuser != null
							&& !"拉黑".equals(zhuser.getBlackliststauts())) {
						zhUsersList0.add(zhuser);
						bool = false;
					}
				}
				if (bool) {
					msg += "件号：" + mop.getMarkId() + "所对应的价格未找到相对应的供应商\n";
				}
				list_zhuser.add(zhUsersList0);
				sumNum += mop.getNumber()
						- (mop.getOutcgNumber() == null ? 0 : mop
								.getOutcgNumber());
				mopList.add(mop);
			}
			zhUsersList = Util.isPublicElement(list_zhuser);
			if (msg == "" && (zhUsersList == null || zhUsersList.size() == 0)) {
				msg = "您所有选择的件号，并不都属于同一供应商，无法进行手动下单";
			}
			if (msg == "") {
				msg = "true";
			}
			Object[] obj = { mopList, zhUsersList, sumNum, msg };
			return obj;
		}

		return null;
	}

	@Override
	public String sdaddwaigouOrder(Integer[] procardIds, Integer zhuserId,
			Float[] caigouNums) {
		if (zhuserId != null) {
			ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, zhuserId);
			if (zhuser == null) {
				return "请选择供应商";
			}
			Users loginUser = Util.getLoginUser();
			if (loginUser == null) {
				return "请先登录";
			}
			String nowtime = Util.getDateTime();
			boolean bool = false;
			StringBuffer str = new StringBuffer();
			WaigouOrder waiGouOrder = new WaigouOrder();
			waiGouOrder.setGysId(zhuser.getId());// 供应商id
			waiGouOrder.setUserId(zhuser.getUserid());
			/****** 订单表头 **********/
			waiGouOrder.setUserCode(zhuser.getUsercode());
			waiGouOrder.setGysName(zhuser.getCmp());
			waiGouOrder.setGhAddress(zhuser.getCompanydz());
			waiGouOrder.setLxPeople(zhuser.getCperson());
			waiGouOrder.setGysPhone(zhuser.getCmobile() + "/"
					+ zhuser.getCfax() + "/" + zhuser.getCtel());
			waiGouOrder.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
			waiGouOrder.setAddTime(Util.getDateTime());// 添加时间
			waiGouOrder.setAddUserCode(loginUser.getCode());
			waiGouOrder.setAddUserName(loginUser.getName());
			waiGouOrder.setPayType(zhuser.getFkfs());
			waiGouOrder.setPiaojuType(zhuser.getZzsl());
			// 获得网站配置信息
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
					.getApplication().get("companyInfo");
			if (companyInfo != null) {
				waiGouOrder.setShAddress(companyInfo.getAddress());
			}
			// 得到最大的采购订单编号
			waiGouOrder.setPlanNumber(osNumber());// 采购订单编号

			waiGouOrder.setStatus("待核对");
			String type = "";
			String WwType = "";
			waiGouOrder.setAddUserPhone(loginUser.getPassword()
					.getPhoneNumber());
			for (int j = 0; j < procardIds.length; j++) {
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, procardIds[j]);
				type = mop.getCategory();
				WwType = mop.getCategory();
				// 判断是否拥有单张比例
				mopbili(mop);
				Float cgNumber = 0f;
				String unit = mop.getUnit();
				Float caigouNum = caigouNums[j];
				float outcgNum = 0;
				if (mop.getOutcgNumber() == null) {
					outcgNum = 0;
				} else {
					outcgNum = mop.getOutcgNumber();
				}
				Float number = Float.parseFloat(String.format("%.4f", mop
						.getNumber()));
				Float synumber = Float.parseFloat(String.format("%.4f",
						(number - outcgNum)));
				if (caigouNum != null && caigouNum != 0f) {
					if ("pcs".equals(unit) || "PCS".equals(unit)
							|| "个".equals(unit)) {
						Double newnum = Math.ceil(caigouNum);
						caigouNum = newnum.floatValue();
						Double newnum0 = Math.ceil(synumber);
						synumber = newnum0.floatValue();
						Double newnum1 = Math.ceil(number);
						number = newnum1.floatValue();
					}
					if ((float) caigouNum > (float) number
							|| (float) caigouNum > synumber) {

						str.append("件号:" + mop.getMarkId() + "的下单量"
								+ mop.getOutcgNumber() + "超出了采购量:" + number
								+ "无需重复采购!");
						continue;
					} else {
						cgNumber = caigouNum;
					}
				} else {
					if (mop.getOutcgNumber() == null) {
						cgNumber = mop.getNumber();
					} else {
						cgNumber = mop.getNumber() - mop.getOutcgNumber();
					}
					if ("pcs".equals(unit) || "PCS".equals(unit)
							|| "个".equals(unit)) {
						Double newnum = Math.ceil(cgNumber);
						cgNumber = newnum.floatValue();
					}
					if (cgNumber <= 0) {
						str.append("件号:" + mop.getMarkId() + "的下单量"
								+ mop.getOutcgNumber() + "超出了采购量:"
								+ mop.getNumber() + "无需重复采购!");
						continue;
					}
				}

				String hql_price = "from Price where partNumber = ? and gysId =?";
				if (mop.getBanben() != null && mop.getBanben().length() > 0) {
					hql_price += " and banbenhao = '" + mop.getBanben() + "'";
				} else {
					hql_price += " and (banbenhao is null or banbenhao = '')";
				}
				if (mop.getKgliao() != null && mop.getKgliao().length() > 0) {
					hql_price += " and kgliao = '" + mop.getKgliao() + "'";
				} else {
					hql_price += " and (kgliao is null or kgliao = '')";
				}
				hql_price += " and pricePeriodStart <= '" + nowtime
						+ "' and (pricePeriodEnd>= '" + nowtime
						+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
				List<Price> list_price = totalDao
						.query(hql_price + " order by firstnum", mop
								.getMarkId(), zhuser.getId());
				Price price = null;

				for (int k = 0; k < list_price.size(); k++) {
					price = list_price.get(k);
					if (price.getEndnum() != null
							&& price.getEndnum() >= cgNumber) {
						break;
					}
				}
				if (price == null) {
					return "未找到件号:" + mop.getMarkId() + "供应商为"
							+ zhuser.getName() + "的价格。";
				}
				// 2、添加订单明细
				String hql_gys = "from GysMarkIdjiepai where markId =? and kgliao=?  and  zhuserId = ?";

				GysMarkIdjiepai gysMarkid = (GysMarkIdjiepai) totalDao
						.getObjectByCondition(hql_gys, mop.getMarkId(), mop
								.getKgliao(), zhuser.getId());
				WaigouPlan waigouPlan = new WaigouPlan();

				waigouPlan.setMarkId(mop.getMarkId());
				// waigouPlan.settuh
				waigouPlan.setBanben(mop.getBanben());
				waigouPlan.setBanci(mop.getBanci());
				waigouPlan.setUnit(unit);
				waigouPlan.setProNumber(mop.getProNumber());// 项目编号
				waigouPlan.setProName(mop.getProName());
				waigouPlan.setSpecification(mop.getSpecification());
				waigouPlan.setTuhao(mop.getTuhao());
				waigouPlan.setKgliao(mop.getKgliao());
				waigouPlan.setNumber(cgNumber);// 采购数量*供应商采购比例
				waigouPlan.setSyNumber(cgNumber);// 送货数量*供应商采购比例
				waigouPlan.setGysId(zhuser.getId());// 供应商id
				waigouPlan.setGysjc(zhuser.getName());// 供应商简称
				waigouPlan.setWlWeizhiDt("sd");// 表示按手动下单
				waigouPlan.setGysName(zhuser.getCmp());// 供应商名称
				waigouPlan.setUserId(zhuser.getUserid());
				waigouPlan.setUserCode(zhuser.getUsercode());
				waigouPlan.setPrice(price.getBhsPrice());
				waigouPlan.setHsPrice(price.getHsPrice());
				waigouPlan.setTaxprice(price.getTaxprice());
				waigouPlan.setMoney(waigouPlan.getHsPrice() * cgNumber);
				waigouPlan.setMopId(mop.getId());
				waigouPlan.setType(type);
				waigouPlan.setWwType(WwType);
				waigouPlan.setDemanddept(mop.getDemanddept());// 需求部门(辅料使用)
				waigouPlan.setShArrivalTime(mop.getNeedFinalDate());// 应到货时间
				waigouPlan.setPriceId(price.getId());

				/************** 查询产品单价 **********/
				waigouPlan.setAddTime(Util.getDateTime());
				// wwp.setShArrivalTime(procard_wg
				// .getNeedFinalDate());//应到货时间在采购确认通知后计算
				waigouPlan.setStatus("待核对");
				if (gysMarkid != null) {
					waigouPlan.setAllJiepai(gysMarkid.getAllJiepai());// 单件总节拍
					waigouPlan.setDeliveryDuration(gysMarkid
							.getDeliveryDuration());// 耽误时长
					waigouPlan.setSingleDuration(gysMarkid.getSingleDuration());// 单班时长(工作时长)
				}
				waigouPlan.setWaigouOrder(waiGouOrder);
				waigouPlan.setWgType(mop.getWgType());// 物料类别
				// waigouPlan.setYwmarkId(mop.getYwMarkId());// 业务件号
				Set<WaigouPlan> wwpSet = new HashSet<WaigouPlan>();
				String msg = isOverflowMaxkc(waigouPlan.getMarkId(), waigouPlan
						.getBanben(), waigouPlan.getKgliao(), waigouPlan
						.getNumber());
				if (!"true".equals(msg)) {
					throw new RuntimeException(msg);
				}
				if (waiGouOrder.getId() != null) {
					wwpSet = waiGouOrder.getWwpSet();
					wwpSet.add(waigouPlan);
					waiGouOrder.setType(type);
					waiGouOrder.setWwType(WwType);
					waiGouOrder.setWwpSet(wwpSet);
					if (waiGouOrder.getAllMoney() != null) {
						Double nowAll= waigouPlan.getMoney() + waiGouOrder.getAllMoney();
						nowAll = (double)Util.FomartDouble(nowAll, 2);
						waiGouOrder.setAllMoney(nowAll);
					} else {
						waiGouOrder.setAllMoney(waigouPlan.getMoney());// 
					}
					Double moeny = waigouPlan.getHsPrice() * cgNumber;
//					if (!moeny.equals(waigouPlan.getMoney())) {
//						throw new RuntimeException("件号:"
//								+ waigouPlan.getMarkId() + "含税总额计算异常。单价:"
//								+ waigouPlan.getHsPrice() + "+采购数量:" + cgNumber
//								+ "!=总额:" + waigouPlan.getMoney()
//								+ "请验证并立即联系管理员。");
//					}
					bool = totalDao.update(waiGouOrder);
				} else {
					wwpSet.add(waigouPlan);
					waiGouOrder.setWwpSet(wwpSet);
					waiGouOrder.setType(type);
					waiGouOrder.setWwType(WwType);
					waiGouOrder.setApplystatus("未申请");
					waiGouOrder.setAllMoney(waigouPlan.getMoney());// 
					Double moeny = waigouPlan.getHsPrice() * cgNumber;
//					if (!moeny.equals(waigouPlan.getMoney())) {
//						throw new RuntimeException("件号:"
//								+ waigouPlan.getMarkId() + "含税总额计算异常。单价:"
//								+ waigouPlan.getHsPrice() + "+采购数量:" + cgNumber
//								+ "!=总额:" + waigouPlan.getMoney() + ";请验证!~。");
//					}
					bool = totalDao.save(waiGouOrder);
				}
				if (mop.getOutcgNumber() == null) {
					mop.setOutcgNumber(cgNumber);
				} else {
					mop.setOutcgNumber(mop.getOutcgNumber() + cgNumber);
				}
				if (mop.getWshCount() == null) {
					mop.setWshCount(waigouPlan.getNumber());
				} else {
					mop.setWshCount(mop.getWshCount() + waigouPlan.getNumber());
				}
				mop.setStatus("已采购");
				mop.setCaigouTime(Util.getDateTime());
				totalDao.update(mop);

				Set<ManualOrderPlanDetail> modSet = mop.getModSet();
				Float fenpeiNum = cgNumber;
				for (ManualOrderPlanDetail mod : modSet) {
					if (fenpeiNum <= 0) {
						break;
					}
					if (mod.getOutcgNumber() == null
							|| mod.getOutcgNumber() == 0) {
						if (mod.getCgnumber() >= fenpeiNum) {
							mod.setOutcgNumber(fenpeiNum);
							fenpeiNum = 0f;
						} else {
							mod.setOutcgNumber(mod.getCgnumber());
							fenpeiNum = fenpeiNum - mod.getCgnumber();
						}
					} else {
						if (mod.getCgnumber() > mod.getOutcgNumber()) {
							if (fenpeiNum > (mod.getCgnumber() - mod
									.getOutcgNumber())) {
								fenpeiNum = fenpeiNum
										- (mod.getCgnumber() - mod
												.getOutcgNumber());
								mod.setOutcgNumber(mod.getCgnumber());
							} else {
								mod.setOutcgNumber(mod.getOutcgNumber()
										+ fenpeiNum);
								fenpeiNum = 0f;
							}
						}
					}
					if (mod.getProcardId() != null) {
						Procard procard = (Procard) totalDao.get(Procard.class,
								mod.getProcardId());
						if (procard != null) {
							procard.setOutcgNumber(mod.getOutcgNumber());
							procard.setWlstatus("待确定");
							totalDao.update(procard);
						}
					}
					mod.setStatus("已采购");
					totalDao.update(mod);
				}

			}
			if (str != null && str.length() > 0) {
				return str.toString();
			}
			return bool + "";
		}
		return "请刷新后重试，谢谢!";
	}

	/**
	 * 获得外购采购订单编号
	 * 
	 * @return
	 */
	public String osNumber() {
		String maxNumber = updatecgOrderNumber(null);
		return maxNumber;
	}

	/**
	 * 获得预付款申请单编号
	 * 
	 * @return
	 */
	public String ppNumber() {
		String datetime = Util.getDateTime("yyyyMMdd");
		String hql = "select number from PrepaymentsApplication order by id desc";
		List list = totalDao.findAllByPage(hql, 0, 1);
		String contractNumber = "";
		String sc = "PP";
		if (list != null && list.size() > 0) {
			contractNumber = (String) list.get(0);
		}
		if (contractNumber != null && contractNumber.length() > 0) {
			try {
				int num = Integer.parseInt(contractNumber.substring(10,
						contractNumber.length())) + 1;
				if (num >= 1000)
					contractNumber = sc + datetime + num;
				else if ((num >= 100))
					contractNumber = sc + datetime + "0" + num;
				else if ((num >= 10))
					contractNumber = sc + datetime + "00" + num;
				else
					contractNumber = sc + datetime + "000" + num;
			} catch (Exception e) {
				contractNumber = sc + datetime + "0001";
			}
		} else {
			contractNumber = sc + datetime + "0001";
		}
		return contractNumber;
	}

	// @Override
	// public Object[] sdxdshowPrice(Integer[] procardIds) {
	// String msg = "";
	// Integer id = null;
	// if (procardIds != null && procardIds.length > 0) {
	// String str = "";
	// id = procardIds[0];
	// Float cgNumber = 0f;
	// Set<Integer> zhuserId = new HashSet<Integer>();
	// List<ManualOrderPlan> mopList = new ArrayList<ManualOrderPlan>();
	// for (int i = 0; i < procardIds.length; i++) {
	// str += "," + procardIds[i];
	// ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
	// ManualOrderPlan.class, procardIds[i]);
	// if (mop.getOutcgNumber() != null) {
	// cgNumber += (mop.getNumber() - mop.getOutcgNumber());
	// } else {
	// cgNumber += mop.getNumber();
	// }
	//				
	// mopList.add(mop);
	// }
	// if (str.length() > 0) {
	// str = str.substring(1);
	// str += ")";
	// }
	//			
	// // Object[] obj = showPrice(id);
	// ManualOrderPlan mop = (ManualOrderPlan) obj[0];
	// return new Object[] { msg, mop, obj[1], mopList, cgNumber };
	// }
	// return null;
	// }

	@Override
	public String cxsqwaigouorder(Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		if (id != null) {
			WaigouOrder waigouorder = (WaigouOrder) totalDao.get(
					WaigouOrder.class, id);
			if (!waigouorder.getAddUserCode().equals(user.getCode())) {
				return "您不是此订单采购员，无法进行重新申请!";
			}
			if ("打回".equals(waigouorder.getApplystatus())) {
				if (waigouorder.getEpId() == null) {
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, waigouorder.getEpId());
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						waigouorder.setApplystatus("同意");
					} else {
						waigouorder.setApplystatus("未审批");
					}
					totalDao.update(waigouorder);
				} else if (CircuitRunServerImpl.updateCircuitRun(waigouorder
						.getEpId())) {
					waigouorder.setApplystatus("未审批");
				}
			}
			return totalDao.update(waigouorder) + "";
		}
		return null;
	}

	public Map<Integer, Object> findAllqualifiedRate(
			QualifiedRate qualifiedRate, int pageNo, int pageSize) {
		if (qualifiedRate == null) {
			qualifiedRate = new QualifiedRate();
		}
		String hql = totalDao.criteriaQueries(qualifiedRate, null);
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public String delwgdd(Integer id) {
		if (id != null) {
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
			if (wgdd.getStatus() != null
					&& wgdd.getStatus().length() > 0
					&& ("待检验".equals(wgdd.getStatus())
							|| "待入库".equals(wgdd.getStatus())
							|| "入库".equals(wgdd.getStatus())
							|| "退回".equals(wgdd.getStatus())
							|| "退货".equals(wgdd.getStatus()) || "打回"
							.equals(wgdd.getStatus()))) {
				return "该送货单明细已仓库签收不能删除";
			}
			delWaigouDeliveryDetail(wgdd);
			Integer waigouDeliveryid  =wgdd.getWaigouDelivery().getId();
			totalDao.delete(wgdd);
			WaigouDelivery wdd = wgdd.getWaigouDelivery();
			List<WaigouDeliveryDetail> wgddList = totalDao.query(
					" from WaigouDeliveryDetail where waigouDelivery.id = ? ",
					waigouDeliveryid);
			if (wgddList != null && wgddList.size() == 0) {
				totalDao.delete(wdd); 
			}
			return "删除成功!";
		}
		return "请刷新后重试，谢谢!";
	}

	@Override
	public Float getzhuanhuanNum(Integer id, Float qrNum) {
		Float zhuanhuanNum = null;
		if (id != null) {
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
			String hql_bili = "select bili from YuanclAndWaigj where markId = ? and bili is not null and bili >0";
			Float bili = (Float) totalDao.getObjectByCondition(hql_bili, wgdd
					.getMarkId());
			if (bili != null) {
				zhuanhuanNum = (float) Math.floor(qrNum / bili);
			}
		}
		return zhuanhuanNum;
	}

	// public Procard findProcardforwlwz(Integer procardId){
	// Procard procard = (Procard)totalDao.get(Procard.class, procardId);
	// List<WlWeizhiDt> wlWeizhiDtList =
	// totalDao.query(" from WlWeizhiDt where procardId = ? ",
	// procard.getId());
	// String wlWzDt= "";
	// for(WlWeizhiDt w:wlWeizhiDtList){
	// wlWzDt+=w.getDetail();
	// }
	// procard.setWlWeizhiDt(wlWzDt);
	// return procard;
	// }

	public CorePayableServer getCorePayableServer() {
		return corePayableServer;
	}

	public void setCorePayableServer(CorePayableServer corePayableServer) {
		this.corePayableServer = corePayableServer;
	}

	@Override
	public ManualOrderPlan findmopById(Integer id) {
		if (id != null) {
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, id);
			return mop;
		}
		return null;
	}

	@Override
	public Object[] findZhuser(ManualOrderPlan mop, String gysCodeAndNum) {
		if (mop != null && gysCodeAndNum != null && gysCodeAndNum.length() > 0) {
			String[] str = gysCodeAndNum.split(";");
			StringBuffer msg = new StringBuffer();
			List<ZhUser> zhuserList = new ArrayList<ZhUser>();
			if (str != null && str.length > 0) {
				Float cgNum = (mop.getNumber() - (mop.getOutcgNumber() == null ? 0
						: mop.getOutcgNumber()));
				for (int i = (str.length - 1); i >= 0; i--) {
					String[] codeAndNums = str[i].split(":");
					Float num = Float.parseFloat(codeAndNums[1]);
					ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
							" from ZhUser where usercode = ?", codeAndNums[0]);
					String hql_gys = " from GysMarkIdjiepai where markId = ? and  zhuserId = ?  and kgliao = ?";
					if (mop.getBanben() != null && mop.getBanben().length() > 0) {
						hql_gys += " and  banBenNumber = '" + mop.getBanben()
								+ "' ";
					} else {
						hql_gys += " and (banBenNumber is null or  banBenNumber = '')";
					}
					if (!"辅料".equals(mop.getCategory())) {
						GysMarkIdjiepai gys = (GysMarkIdjiepai) totalDao
								.getObjectByCondition(hql_gys, mop.getMarkId(),
										zhuser.getId(), mop.getKgliao());
						if (gys != null) {
							zhuser.setBilifpNum(cgNum * (gys.getCgbl() / 100));
						} else {
							msg.append("未找到件号:" + mop.getMarkId() + "供应商:"
									+ zhuser.getName() + "版本:"
									+ mop.getBanben() + "供料属性:"
									+ mop.getKgliao() + "，供应商产品分配比例。<br/>");
						}
					}
					zhuser.setMoqNum(num);
					zhuserList.add(zhuser);
				}
			}
			if (msg != null && msg.length() > 0) {
				return new Object[] { msg.toString(), null };
			} else {
				return new Object[] { "true", zhuserList };
			}
		}
		return null;
	}

	@Override
	public boolean isUseMOQ(Integer id, String status, String gysCodeAndNum) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return false;
		}
		if (id != null && id > 0) {
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, id);
			if ("NO".equals(status)) {
				mop.setIsuse("NO");
			} else if ("YES".equals(status) && gysCodeAndNum != null
					&& gysCodeAndNum.length() > 0) {
				String remarks = "";
				String[] strs = gysCodeAndNum.split(";");
				Float nowAllNum = 0F;
				for (int i = 0; i < strs.length; i++) {
					String[] gysCodeAndNums = strs[i].split(":");
					ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
							" from ZhUser where usercode = ? ",
							gysCodeAndNums[0]);
					if (zhuser != null) {
						remarks += "<span>" + zhuser.getName() + "将采购数量:"
								+ gysCodeAndNums[1] + "<span><br/>";
						nowAllNum += Float.valueOf(gysCodeAndNums[1]);
					}
				}
				mop.setGyscodeAndNum(gysCodeAndNum);
				mop.setMoqNum(nowAllNum);
				mop.setMoqGysCode(remarks);

				String processName = "MOQ数量申请";
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ManualOrderPlan.class, mop.getId(), "epStatus",
							"id", "",
							user.getDept() + "部门 " + user.getName() + "件号:"
									+ mop.getMarkId() + " MOQ数量申请,请您审批", true);
					if (epId != null && epId > 0) {
						mop.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							mop.setEpStatus("同意");
							// if (mop.getNumber() > mop.getMoqNum()) {//
							// 如果审批时采购数量大于MOQ数量
							// mop.setIsuse("NO");
							// mop.setNeedNumber(mop.getNumber());// 记录实际需求量
							// mop.setGyscodeAndNum("");
							// mop.setMoqGysCode("");
							// } else {
							// mop.setEpStatus("同意");
							// mop.setIsuse("YES");
							// mop.setNeedNumber(mop.getNumber());// 记录实际需求量
							// mop.setNumber(mop.getMoqNum());// 调整采购量为MOQ数量
							// }

						} else {
							mop.setEpStatus("未审批");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return totalDao.update(mop);
		}
		return false;
	}

	@Override
	public boolean isUseMOQ0(Integer id, String status, String gysCodeAndNum) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return false;
		}
		if (id != null && id > 0) {
			WgProcardMOQ wgmoq = (WgProcardMOQ) totalDao.get(
					WgProcardMOQ.class, id);
			if ("NO".equals(status)) {
				wgmoq.setIsUse("NO");
			} else if ("YES".equals(status) && gysCodeAndNum != null
					&& gysCodeAndNum.length() > 0) {
				String remarks = "";
				String[] strs = gysCodeAndNum.split(";");
				Float nowAllNum = 0F;
				for (int i = 0; i < strs.length; i++) {
					String[] gysCodeAndNums = strs[i].split(":");
					ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
							" from ZhUser where usercode = ? ",
							gysCodeAndNums[0]);
					if (zhuser != null) {
						remarks += "<span>" + zhuser.getName() + "将采购数量:"
								+ gysCodeAndNums[1] + "<span><br/>";
						nowAllNum += Float.valueOf(gysCodeAndNums[1]);
					}
				}
				wgmoq.setGyscodeAndNum(gysCodeAndNum);
				wgmoq.setMoqNum(nowAllNum);
				wgmoq.setRemarks(remarks);

				String processName = "待确认物料MOQ数量申请";
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ManualOrderPlan.class, wgmoq.getId(), "epStatus",
							"id", "", user.getDept() + "部门 " + user.getName()
									+ "MOQ数量申请,请您审批", true);
					if (epId != null && epId > 0) {
						wgmoq.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							wgmoq.setEpStatus("同意");
							wgmoq.setIsUse("YES");
						} else {
							wgmoq.setEpStatus("未审批");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return totalDao.update(wgmoq);
		}
		return false;
	}

	@Override
	public Object[] toisAgainCheck(Integer id) {
		WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
				WaigouDeliveryDetail.class, id);
		String hql = "from OsTemplate where partNumber= ? ";
		OsTemplate ot = null;
		if ("外购件".equals(wgdd.getType())) {
			hql += " and zhonglei='外购件检验' ";
		} else if (wgdd.getType().equals("包工包料")
				|| wgdd.getType().equals("工序外委")) {
			// if (gongxuNum.indexOf(";") > 0) {
			// gongxuNum = gongxuNum.substring(gongxuNum.lastIndexOf(";") +
			// 1,
			// gongxuNum.length());
			// }
			hql += " and zhonglei in('外委','外委检验','巡检')";
			// hql += " and gongxuNum='" + gongxuNum + "'";
		}
		if (wgdd.getBanben() == null || wgdd.getBanben().length() == 0) {
			String hql2 = " and (banbenNumber is null or banbenNumber='')";
			ot = (OsTemplate) totalDao.getObjectByCondition(hql + hql2, wgdd
					.getMarkId());
		} else {
			String hql2 = " and (banbenNumber is not null and banbenNumber=?)";
			ot = (OsTemplate) totalDao.getObjectByCondition(hql + hql2, wgdd
					.getMarkId(), wgdd.getBanben());
		}
		if (ot == null) {
			// 读取通用模版
			String hql_ty = "from OsTemplate where ispublic='是'";
			if (wgdd.getType().equals("外购件")) {
				hql_ty += " and zhonglei='外购件检验' ";
			} else if (wgdd.getType().equals("包工包料")
					|| wgdd.getType().equals("工序外委")) {
				hql_ty += " and zhonglei in('外委','外委检验','巡检')";
			}
			List<OsTemplate> otList = totalDao.query(hql_ty);
			if (otList != null) {
				if (otList.size() == 1) {
					ot = otList.get(0);
				}
			}

		}
		WaigoujianpinciZi xjpc = findxunjianpici(wgdd, ot);
		return new Object[] { wgdd, xjpc };
	}

	@Override
	public String isAgainCheck(DefectiveProduct dp) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (dp != null && dp.getWgddId() != null) {
			WaigouDeliveryDetail oldwgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, dp.getWgddId());
			oldwgdd.setAgaincheck(dp.getResult());
			boolean islailiao = false;
			if (dp.getType().indexOf("来料不良") >= 0) {
				islailiao = true;
				oldwgdd.setStatus("待检验");
			}
			if ("挑选".equals(dp.getResult())) {
				if (dp.getZjhgNumber() > 0) {
					WaigouPlan waigouplan = (WaigouPlan) totalDao.get(
							WaigouPlan.class, oldwgdd.getWaigouPlanId());
					Float tuhuoNum = dp.getLlNumber() - dp.getZjhgNumber();
					waigouplan.setSyNumber(waigouplan.getSyNumber() + tuhuoNum);
					Float hgNum = dp.getZjhgNumber();
					Float cyhgNum = hgNum - oldwgdd.getHgNumber();
					Float cytuhuoNum = tuhuoNum - oldwgdd.getBhgNumber();
					ManualOrderPlan mop = (ManualOrderPlan) totalDao
							.getObjectByCondition(
									" from ManualOrderPlan where id in( select mopId from WaigouPlan where id = ? )"
											+ " ", oldwgdd.getWaigouPlanId());
					if (islailiao) {
						oldwgdd.setShNumber(dp.getZjhgNumber());
						oldwgdd.setQrNumber(dp.getZjhgNumber());
					} else {
						cyhgNum = hgNum;
						cytuhuoNum = tuhuoNum;
						// oldwgdd.setBhgNumber(tuhuoNum);
						oldwgdd.setTuihuoNum(tuhuoNum);
						// 外购件修改物料需求表上的入库数量
						if (mop != null) {
							mop.setRukuNum(mop.getRukuNum() + hgNum);
							mop.setQsCount(mop.getQsCount() + hgNum);
							mop.setHgCount(mop.getHgCount() + hgNum);
							Float flagNumber = +hgNum;
							Set<ManualOrderPlanDetail> modset = mop.getModSet();
							for (ManualOrderPlanDetail mod : modset) {
								if (flagNumber > 0) {
									if (flagNumber >= (mod.getCgnumber() - mod
											.getRukuNum())) {
										flagNumber -= (mod.getCgnumber() - mod
												.getRukuNum());
										mod.setRukuNum(mod.getCgnumber());
									} else {
										mod.setRukuNum(mod.getRukuNum()
												+ flagNumber);
										flagNumber = 0f;
									}
								}
								totalDao.update(mod);
							}
							totalDao.update(mop);
						}
					}
					if (cyhgNum > 0) {
						String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
						GoodsStore t = (GoodsStore) totalDao
								.getObjectByCondition(hql_gt, oldwgdd.getId(),
										oldwgdd.getMarkId(), "不合格品库");
						// 更新不合格品库库存 ；合格品出库
						if (t != null) {
							String hql = "from Goods where goodsMarkId = ? and goodsCurQuantity>0 ";
							if (t.getGoodsStoreWarehouse() != null
									&& t.getGoodsStoreWarehouse().length() > 0) {
								hql += " and goodsClass='"
										+ t.getGoodsStoreWarehouse() + "'";
							}
							if (t.getGoodsStoreLot() != null
									&& t.getGoodsStoreLot().length() > 0) {
								hql += " and goodsLotId='"
										+ t.getGoodsStoreLot() + "'";
							}
							Goods s = (Goods) totalDao.getObjectByCondition(
									hql,
									new Object[] { t.getGoodsStoreMarkId() });
							if (s != null) {
								s.setGoodsCurQuantity(s.getGoodsCurQuantity()
										- cyhgNum);
								// 合格品出库 添加出库记录
								saveSell(s, cyhgNum, user, "合格品出库");
								totalDao.update(s);
							}
							if (!islailiao) {
								// 入到正常库存。
								GoodsStore goodsStore = new GoodsStore();
								BeanUtils.copyProperties(t, goodsStore,
										new String[] { "goodsStoreId",
												"goodsStoreWarehouse",
												"goodsStoreCount", "style" });
								goodsStore.setGoodsStoreDate(Util
										.getDateTime("yyyy-mm-dd"));
								goodsStore
										.setGoodsStoreTime(Util.getDateTime());
								goodsStore.setGoodsStorePlanner(user.getName());
								if ("外购".equals(dp.getWlType())) {
									goodsStore.setGoodsStoreWarehouse("外购件库");
								} else if ("外委".equals(dp.getWlType())) {
									goodsStore.setGoodsStoreWarehouse("外协库");
								}
								goodsStore.setGoodsStoreCount(hgNum);
								goodsStoreServer.saveSgrk(goodsStore);
							}

						}
					}
					dp.setZjbhgNumber(tuhuoNum);
					if (islailiao && cytuhuoNum > 0) {
						// 待检库出库.
						String hql_djgt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
						GoodsStore djt = (GoodsStore) totalDao
								.getObjectByCondition(hql_djgt,
										oldwgdd.getId(), oldwgdd.getMarkId(),
										"待检库");
						if (djt != null) {
							Goods s = (Goods) totalDao
									.getObjectByCondition(
											" from Goods where goodsMarkId =?  "
													+ "and goodsClass=? and goodsCurQuantity>0  and goodsLotId=? ",
											oldwgdd.getMarkId(), "待检库", oldwgdd
													.getExamineLot());
							if (s != null) {
								s.setGoodsCurQuantity(s.getGoodsCurQuantity()
										- cytuhuoNum);
								// 待检库出库
								saveSell(s, cytuhuoNum, user, "待检库出库");
								totalDao.update(s);
							}
						}
						// 添加不合格品入库记录
						String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
						GoodsStore t = (GoodsStore) totalDao
								.getObjectByCondition(hql_gt, oldwgdd.getId(),
										oldwgdd.getMarkId(), "不合格品库");
						if (t != null) {
							GoodsStore gt = new GoodsStore();
							BeanUtils.copyProperties(t, gt, new String[] {
									"goodsStoreId", "goodsStoreCount",
									"goodsStoreDate", "goodsStorePlanner",
									"goodsStoreCharger" });
							t.setGoodsStoreCount(cytuhuoNum);
							t.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
							t.setGoodsStoreTime(Util.getDateTime());
							t.setGoodsStorePlanner(user.getName());
							t.setGoodsStoreCharger(user.getName());
							goodsStoreServer.saveSgrk(t);
						}
					}
					if (tuhuoNum > 0) {
						// 添加退货单明细
						ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, dp
								.getGysId());
						addReturnSingle(zhuser, dp, oldwgdd, dp.getRamk(), user);
						recordGysBad(dp);
					}
					if (islailiao) {
						// 重新生成检验批次;
						String maxexamineLot = "";
						String mouth = Util.getDateTime("yyyyMM");
						String hql_examineLot = "select max(examineLot) from WaigouDeliveryDetail where examineLot like '"
								+ mouth + "%'";
						Object object = (Object) totalDao
								.getObjectByCondition(hql_examineLot);
						if (object != null) {
							Long selfCard = Long.parseLong(object.toString()) + 1;// 当前最大流水卡片
							maxexamineLot = selfCard.toString();
						} else {
							maxexamineLot = mouth + "00001";
						}
						oldwgdd.setExamineLot(maxexamineLot);
						waigouplan.setStatus("待检验");
						oldwgdd.setStatus("待检验");
					}
					waigouplan.setQsNum(waigouplan.getQsNum() - tuhuoNum);
					waigouplan.setHgNumber(waigouplan.getHgNumber()
							- oldwgdd.getHgNumber());
					waigouplan.setKeruku(waigouplan.getKeruku()
							- oldwgdd.getHgNumber());
					totalDao.update(waigouplan);
					// 修改物料需求上的数据
					if (mop != null) {
						mop.setQsCount(mop.getQsCount() - tuhuoNum);
						mop
								.setHgCount(mop.getHgCount()
										- oldwgdd.getHgNumber());
						totalDao.update(mop);
					}
				} else {
					return "请填写分拣后数量";
				}
			}
			return totalDao.update(oldwgdd) + "";
		}
		return null;
	}

	@Override
	public Object[] findProcardBywgMarkId(Integer id) {
		if (id != null && id > 0) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			ManualOrderPlan mop = (ManualOrderPlan) totalDao
					.getObjectByCondition(" from ManualOrderPlan where id = ?",
							waigouplan.getMopId());
			List<ManualOrderPlanDetail> mopdList = totalDao.query(
					" from ManualOrderPlanDetail where manualPlan.id = ?", mop
							.getId());
			for (ManualOrderPlanDetail mopd : mopdList) {
				if (mopd.getProcard() != null) {
					mopd.setProcard((Procard) totalDao.get(Procard.class, mopd
							.getProcardId()));
				}
			}
			// List<WaigouPlan> wgPlanList =
			// totalDao.query(" from WaigouPlan where mopId = ? ",
			// waigouplan.getMopId());
			String hql_goodskc = " select sum(goodsCurQuantity) from Goods where goodsMarkId = ? and goodsClass = '外购件库' ";
			if (mop.getBanben() != null && mop.getBanben().length() > 0) {
				hql_goodskc += " and banBenNumber = '" + mop.getBanben() + "'";
			} else {
				hql_goodskc += " and (banBenNumber is null or banBenNumber = '')";
			}
			Double kcNum = (Double) totalDao.getObjectByCondition(hql_goodskc,
					mop.getMarkId());
			mop.setCgbl(kcNum.floatValue());
			return new Object[] { waigouplan, mop, mopdList };
		}
		return null;
	}

	@Override
	public Object[] findAllCgxinxi(Integer id) {
		if (id != null && id > 0) {
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, id);
			List<ManualOrderPlanDetail> mopdList = totalDao.query(
					" from ManualOrderPlanDetail where manualPlan.id = ? ", id);
			for (ManualOrderPlanDetail mopd : mopdList) {
				if (mopd.getProcardId() != null && mopd.getProcardId() > 0) {
					mopd.setProcard((Procard) totalDao.get(Procard.class, mopd
							.getProcardId()));
				}
				if (mopd.getOrderId() != null && mopd.getOrderId() > 0) {
					mopd.setOrder((OrderManager) totalDao.get(
							OrderManager.class, mopd.getOrderId()));
				}
			}
			List<WaigouPlan> waigouplanList = totalDao.query(
					" from WaigouPlan where mopId =? ", id);
			return new Object[] { mop, mopdList, waigouplanList };
		}
		return null;
	}

	@Override
	public String agreesbjd(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		WaigouPlan wp = (WaigouPlan) totalDao.getObjectById(WaigouPlan.class,
				id);
		if (wp == null) {
			return "没有找到对应的订单!";
		}
		if (!wp.getUserId().equals(user.getId())) {
			return "此订单您无赞同权限!";
		}
		if (wp.getSbjdApplyCount() != null && wp.getSbjdApplyCount() > 0) {
			List<ProcardSbWgLog> logList = totalDao
					.query(
							"from ProcardSbWgLog where forObjId=? and forObj='外购订单' and status='待处理'",
							wp.getId());
			if (logList != null && logList.size() > 0) {
				Float applyCount = wp.getSbjdApplyCount();
				for (ProcardSbWgLog psbLog : logList) {
					// ProcardSbWg procardSbwg=(ProcardSbWg)
					// totalDao.getObjectById(ProcardSbWg.class,
					// psbLog.getPsbwgId());
					// if(procardSbwg!=null){
					// }
					if (psbLog.getCount() <= applyCount) {
						applyCount = applyCount - psbLog.getCount();
						psbLog.setStatus("完成");
						totalDao.update(psbLog);
					} else {
						throw new RuntimeException("订单的设变减单数额小于申请的数量,请联系管理员");
					}
				}
				if (applyCount > 0) {
					throw new RuntimeException("订单的设变减单数额大于申请的数量,请联系管理员");
				}
				if (wp.getSbjdCount() == null) {
					wp.setSbjdCount(wp.getSbjdApplyCount());
				} else {
					wp.setSbjdCount(wp.getSbjdCount() + wp.getSbjdApplyCount());
				}
				// wp.setSyNumber(wp.getSyNumber() -
				// wp.getSbjdApplyCount());数量在申请时就扣除了不能二次扣除
				// wp.setNumber(wp.getNumber() - wp.getSbjdApplyCount());
				// 修改物料需求汇总上的数量
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, wp.getMopId());
				if (mop != null) {
					mop.setNumber(mop.getNumber() - wp.getSbjdApplyCount());
					mop.setOutcgNumber(mop.getOutcgNumber()
							- wp.getSbjdApplyCount());
					if (mop.getWshCount() != null) {
						mop.setWshCount(mop.getWshCount()
								- wp.getSbjdApplyCount());
					}
					if (mop.getCancalNum() == null) {
						mop.setCancalNum(wp.getSbjdApplyCount());
					} else {
						mop.setCancalNum(mop.getCancalNum()
								+ wp.getSbjdApplyCount());
					}
					Set<ManualOrderPlanDetail> modSet = mop.getModSet();
					Float jdcount = wp.getSbjdApplyCount();
					for (ManualOrderPlanDetail mod : modSet) {
						if (jdcount > 0) {
							if (jdcount >= mod.getCgnumber()) {
								mod.setCgnumber(0f);
								if (mod.getOutcgNumber() != null) {
									mod.setOutcgNumber(0f);
								}
								if (mod.getCancalNum() != null) {
									mod.setCancalNum(mod.getCancalNum()
											+ jdcount);
								} else {
									mod.setCancalNum(jdcount);
								}
								jdcount -= mod.getCancalNum();
							} else {
								mod.setCgnumber(mod.getOutcgNumber() - jdcount);
								if (mod.getOutcgNumber() != null) {
									mod.setOutcgNumber(mod.getOutcgNumber()
											- jdcount);
								}
								if (mod.getCancalNum() != null) {
									mod.setCancalNum(mod.getCancalNum()
											+ jdcount);
								} else {
									mod.setCancalNum(jdcount);
								}
								jdcount = 0f;
							}
						}
						totalDao.update(mod);
					}
					mop.setEpCancalStatus("设变减单");
					totalDao.update(mop);
				}
				wp.setSbjdApplyCount(0f);
				totalDao.update(wp);
				WaigouOrder wgorder = wp.getWaigouOrder();
				if (wgorder.getOldstatus() != null) {
					wgorder.setStatus(wgorder.getOldstatus());
					totalDao.update(wgorder);
				}
				return "true";
			}
		}
		return "没有申请数量，请核实!";
	}

	@Override
	public Map<String, String> getwgOrderTz(Integer id) {
		// TODO Auto-generated method stub
		Map<String, String> tzwzMap = new HashMap<String, String>();
		List<WaigouPlan> waigouPlanList = totalDao.query(
				"from WaigouPlan where waigouOrder.id=?", id);
		if (waigouPlanList != null && waigouPlanList.size() > 0) {
			for (WaigouPlan waigouPlan : waigouPlanList) {
				String banciSql = "";
				if (waigouPlan.getType().equals("外委")
						&& waigouPlan.getWwType().equals("包工包料")) {
					// 包工包料需要连带下层图纸
					Procard procard = (Procard) totalDao
							.getObjectByCondition(
									"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?)",
									waigouPlan.getId());
					if (procard != null) {
						tzwzMap.putAll(getProcardTzWz(procard, waigouPlan,false));
					}
				} else {
					if (waigouPlan.getBanci() == null) {
						banciSql = " and (banci is null or banci = 0)";
					} else {
						banciSql = " and banci is not null and banci = "
								+ waigouPlan.getBanci();
					}
					List<ProcessTemplateFile> filelist = totalDao.query(
							"from ProcessTemplateFile where markId=?  "
									+ banciSql, waigouPlan.getMarkId());
					if (filelist != null && filelist.size() > 0) {
						for (ProcessTemplateFile file : filelist) {
							String wz = file.getMonth() + "/"
									+ file.getFileName();
							String name = file.getOldfileName();
							if (file.getProcessName() != null) {
								file.setOldfileName("(" + file.getProcessName()
										+ ")" + file.getOldfileName());
							}
							tzwzMap.put(wz, name);
						}
					}
				}
			}
		}
		return tzwzMap;
	}

	private Map<String, String> getProcardTzWz(Procard procard,
			WaigouPlan waigouPlan,boolean xctz) {
		// TODO Auto-generated method stub
		Map<String, String> tzwzMap = new HashMap<String, String>();
		String banciSql = "";
		if (procard.getBanci() == null
			||procard.getBanci()==0) {
			banciSql = " and (banci is null or banci = 0)";
		} else {
			banciSql = " and banci is not null and banci = "
					+ procard.getBanci();
		}
		String banbenSql = null;
		if (procard.getBanBenNumber() == null
				|| procard.getBanBenNumber().length() == 0) {
			banbenSql = " and (banBenNumber is null or banBenNumber='') ";
		} else {
			banbenSql = " and banBenNumber ='" + procard.getBanBenNumber()
					+ "' ";
		}
		List<ProcessTemplateFile> filelist = null;
		if (waigouPlan != null) {
			String relatDown = null;
			if (waigouPlan.getWwSource().equals("手动外委")) {
				if(waigouPlan.getWwType().equals("包工包料")){
					xctz =true;
				}else{
					relatDown = (String) totalDao
					.getObjectByCondition(
							"select relatDown from ProcessInforWWApplyDetail where id in "
							+ "(select wwxlId from ProcardWGCenter where wgOrderId=?)",
							waigouPlan.getId());
					if (relatDown != null && relatDown.equals("是")) {
						xctz = true;
					}
				}

			}
			String[] processNos = waigouPlan.getProcessNOs().split(";");
			StringBuffer sb = new StringBuffer();
			for (String pno : processNos) {
				if (sb.length() == 0) {
					sb.append(pno);
				} else {
					sb.append("," + pno);
				}
			}
			if (procard.getProductStyle().equals("批产")) {
				// filelist = totalDao
				// .query(
				// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO in("
				// + sb.toString()
				// + ") "
				// +banbenSql+ banciSql
				// +
				// "and productStyle='批产' and (status is null or status!='历史') group by oldfileName)",
				// procard.getMarkId());
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is null "
										+ banciSql
										+ "and productStyle='批产' and type in('工艺规范','3D文件','标签文件','标签文件') group by oldfileName",
								procard.getMarkId());
				if (maxIdList != null && maxIdList.size() > 0) {
					StringBuffer maxidsb = new StringBuffer();
					for (Integer maxId : maxIdList) {
						if (maxidsb.length() == 0) {
							maxidsb.append(maxId);
						} else {
							maxidsb.append("," + maxId);
						}
					}
					filelist = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			} else {
				// filelist = totalDao
				// .query(
				// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
				// + sb.toString()
				// + ") )"
				// +banbenSql+ banciSql
				// +
				// "and productStyle='试制' and type in('工艺规范','3D文件') group by oldfileName)",
				// procard.getMarkId(), procard
				// .getProcardTemplateId());
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is  null and glId =?"
										+ banciSql
										+ "and productStyle='试制' and type in('工艺规范','3D文件','标签文件') group by oldfileName",
								procard.getMarkId(), procard
										.getProcardTemplateId());
				if (maxIdList != null && maxIdList.size() > 0) {
					StringBuffer maxidsb = new StringBuffer();
					for (Integer maxId : maxIdList) {
						if (maxidsb.length() == 0) {
							maxidsb.append(maxId);
						} else {
							maxidsb.append("," + maxId);
						}
					}
					filelist = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			}

		} else {
			if (procard.getProductStyle().equals("批产")) {
				// filelist = totalDao
				// .query(
				// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? "
				// +banbenSql+ banciSql
				// +
				// "and productStyle='批产' and (status is null or status!='历史') group by oldfileName)",
				// procard.getMarkId());
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? "
										+ banciSql
										+ "and productStyle='批产' and (status is null or status!='历史') group by oldfileName",
								procard.getMarkId());
				if (maxIdList != null && maxIdList.size() > 0) {
					StringBuffer maxidsb = new StringBuffer();
					for (Integer maxId : maxIdList) {
						if (maxidsb.length() == 0) {
							maxidsb.append(maxId);
						} else {
							maxidsb.append("," + maxId);
						}
					}
					filelist = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			} else {
				// filelist = totalDao
				// .query(
				// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and "
				// +
				// "((processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') )) or (processNO is null and glId=?))"
				// +banbenSql+ banciSql
				// +
				// "and productStyle='试制' and (status is null or status!='历史') group by oldfileName)",
				// procard.getMarkId(), procard
				// .getProcardTemplateId());
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and "
										+ " processNO is null and glId=?"
										+ banciSql
										+ "and productStyle='试制' and (status is null or status!='历史') group by oldfileName",
								procard.getMarkId(), procard
										.getProcardTemplateId());
				if (maxIdList != null && maxIdList.size() > 0) {
					StringBuffer maxidsb = new StringBuffer();
					for (Integer maxId : maxIdList) {
						if (maxidsb.length() == 0) {
							maxidsb.append(maxId);
						} else {
							maxidsb.append("," + maxId);
						}
					}
					filelist = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			}
		}
		if (filelist != null && filelist.size() > 0) {
			for (ProcessTemplateFile file : filelist) {
				String wz = file.getMonth() + "/" + file.getFileName();
				String name = file.getOldfileName();
				if (file.getProcessName() != null) {
					file.setOldfileName("(" + file.getProcessName() + ")"
							+ file.getOldfileName());
				}
				tzwzMap.put(wz, name);
			}
		}
		if (xctz) {
			Set<Procard> sonSet = procard.getProcardSet();
			if (sonSet != null && sonSet.size() > 0) {
				for (Procard son : sonSet) {
					Map<String, String> tzwzMap2 = getProcardTzWz(son, null,xctz);
					tzwzMap.putAll(tzwzMap2);
				}
			}
		}
		return tzwzMap;
	}

	@Override
	public Procard gettoChangeWgj(Integer id, String markId, String kgliao) {
		// TODO Auto-generated method stub
		// String hql_all =
		// "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,sum(filnalCount),sum(cgNumber) "
		// + "from Procard where rootid=? and markId=? and kgliao=?"
		// + " and (klNumber=hascount or hasCount is null)"
		// +
		// " and procardStyle='外购' and (outcgNumber is null or outcgNumber=0) and (sbStatus is null or sbStatus!='删除') "
		// +
		// " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc";
		String hql_all = "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,sum(filnalCount),sum(cgNumber) "
				+ "from Procard where rootid=? and markId=? and kgliao=?"
				+ " and (ABS(hascount-klNumber)<0.00001 or hasCount is null)"
				+ " and procardStyle='外购' and (sbStatus is null or sbStatus!='删除') "
				+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc";
		/*
		 * String hql_all =
		 * "select wgType,markId,banBenNumber,kgliao,proName,specification,unit,sum(filnalCount),sum(cgNumber) "
		 * + "from Procard where rootid=? and markId=? and kgliao=?" +
		 * " and (klNumber=hascount or hasCount is null)" +
		 * " and procardStyle='外购'  and cgNumber is not null and (outcgNumber is null or outcgNumber=0) and (sbStatus is null or sbStatus!='删除') "
		 * +
		 * " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc"
		 * ;
		 */
		// klNumber=hascount表示未领过料，领过料的不能替换
		List<Object[]> list = totalDao.query(hql_all, id, markId, kgliao);
		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);
			if (obj != null) {
				Procard procard_new = new Procard();
				procard_new.setWgType(str(obj, 0));
				procard_new.setMarkId(str(obj, 1));
				if (obj[2] != null) {
					procard_new.setBanBenNumber(str(obj, 2));
				}
				procard_new.setKgliao(str(obj, 3));
				procard_new.setProName(str(obj, 4));
				procard_new.setSpecification(str(obj, 5));
				procard_new.setUnit(str(obj, 6));
				procard_new.setFilnalCount(Float.parseFloat(flo(obj, 7)));
				procard_new.setCgNumber(Float.parseFloat(flo(obj, 8)));
				return procard_new;
			}
		}
		return null;
	}

	@Override
	public String changeWgj(Procard procard, String markId, String kgliao) {
		// TODO Auto-generated method stub
		String hql_all = "select sum(filnalCount),sum(cgNumber) "
				+ "from Procard where rootid=? and markId=? and kgliao=?"
				+ " and ABS(hascount-klNumber)<0.00001"
				+ " and procardStyle='外购'   and (sbStatus is null or sbStatus!='删除') "
				+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc";
		Object[] yuanCounts = (Object[]) totalDao.getObjectByCondition(hql_all,
				procard.getRootId(), markId, kgliao);
		// if (yuanCounts != null && yuanCounts[0] != null
		// && yuanCounts[1] != null) {
		if (yuanCounts != null && yuanCounts[0] != null) {
			Float yfilnalCount = Float.valueOf(yuanCounts[0].toString());
			// Float ycgNumber = Float.valueOf(yuanCounts[1].toString());
			Float quanzi = procard.getFilnalCount() / yfilnalCount;
			Float ncgNumber = procard.getCgNumber();
			List<Procard> yProcardList = totalDao
					.query(
							"from Procard where rootId=? and markId=? and kgliao=?  and (sbStatus is null or sbStatus!='删除')",
							procard.getRootId(), markId, kgliao);
			String banbenSql = "";
			if (procard.getBanBenNumber() == null
					|| procard.getBanBenNumber().length() == 0) {
				banbenSql = " and (banbenhao is null or banbenhao='') ";
			} else {
				banbenSql = " and banbenhao='" + procard.getBanBenNumber()
						+ "'";
			}
			// 替换功能打开历史版本
			// YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
			// .getObjectByCondition(
			// "from YuanclAndWaigj where markId=? and kgliao=? and (banbenStatus is null or banbenStatus!='历史')"
			// + banbenSql, procard.getMarkId(), procard
			// .getKgliao());
			YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
					.getObjectByCondition(
							"from YuanclAndWaigj where markId=? and kgliao=? "
									+ banbenSql, procard.getMarkId(), procard
									.getKgliao());
			if (yclAndWgj == null) {
				return "外购件库中没有找到可用的目标外购件!";
			}
			/***************** MRP计算(库存量&&&&&占用量) *****************/
			String goodsClassSql = null;
			// if (procard.getProductStyle() != null
			// && procard.getProductStyle().equals("试制")) {
			// // 试制的外购件去试制库取
			// goodsClassSql = " and goodsClass ='试制库'";
			// } else {
			String kgsql = "";
			if (procard.getKgliao() != null && procard.getKgliao().length() > 0) {
				kgsql += " and kgliao ='" + procard.getKgliao() + "'";
			}
			// goodsClassSql =
			// " and ((goodsClass in ('外购件库','中间库') "
			// + kgsql + " ) or goodsClass = '备货库')";
			goodsClassSql = " and goodsClass in ('外购件库') " + kgsql;
			// }
			String banben_hql = "";
			String banben_hql2 = "";
			if (procard.getBanBenNumber() != null
					&& procard.getBanBenNumber().length() > 0) {
				banben_hql = " and banBenNumber='" + procard.getBanBenNumber()
						+ "'";
				banben_hql2 = " and banben='" + procard.getBanBenNumber() + "'";
			}
			String specification_sql = "";
			// if (procard.getSpecification() != null
			// && procard.getSpecification().length() > 0) {
			// specification_sql = " and specification = "
			// + procard.getSpecification();
			// } else {
			// specification_sql =
			// " and (specification = '' and specification is null)";
			// }

			// 库存量(件号+版本+供料属性+库别)
			String hqlGoods = "";
			hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
					+ goodsClassSql
					+ " and goodsCurQuantity>0 "
					+ banben_hql
					+ " and (fcStatus is null or fcStatus='可用')";
			Float kcCount = (Float) totalDao.getObjectByCondition(hqlGoods,
					procard.getMarkId());
			if (kcCount == null || kcCount < 0) {
				kcCount = 0f;
			}

			/****************** 占用量=生产占用量+导入占用量 ******************************/
			// 系统占用量(已计算过采购量(1、有库存 2、采购中)，未领料)
			String zyCountSql = "select sum(hascount) from Procard where markId=? and productStyle=? and kgliao=? "
					+ banben_hql
					+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and (sbStatus is null or sbStatus!='删除')";
			// Float zyCount = (Float) totalDao
			// .getObjectByCondition(zyCountSql, procard
			// .getMarkId(),
			// procard.getProductStyle(), procard
			// .getKgliao());
			Float zyCount = (Float) totalDao.getObjectByCondition(zyCountSql,
					procard.getMarkId(), "批产", procard.getKgliao());
			if (zyCount == null || zyCount < 0) {
				zyCount = 0f;
			}

			// 导入占用量(系统切换时导入占用量)
			String hqlGoods_zy = "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
					+ banben_hql
					+ " and goodsClass in ('占用库') and kgliao=? and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
			Float kcCount_zy = (Float) totalDao.getObjectByCondition(
					hqlGoods_zy, procard.getMarkId(), procard.getKgliao());
			if (kcCount_zy == null || kcCount_zy < 0) {
				kcCount_zy = 0f;
			}
			zyCount += kcCount_zy;
			if (zyCount < 0) {
				zyCount = 0F;
			}
			/****************** 结束 占用量=生产占用量+导入占用量 结束 ******************************/
			/****************** 在途量=采购在途量+导入在途量 ******************************/
			// 系统在途量(已生成采购计划，未到货)
			// String hql_zc =
			// "select sum(cgNumber-dhNumber) from  Procard where markId=? and productStyle=? "
			// + banben_hql
			// +
			// " and kgliao=? and jihuoStatua='激活' and status='已发卡' and procardStyle='外购'"
			// +
			// " and cgNumber >0 and dhNumber is not null and (sbStatus is null or sbStatus!='删除')";
			// Float ztCount = (Float) totalDao.getObjectByCondition(
			// hql_zc, procard.getMarkId(), procard
			// .getProductStyle(), procard.getKgliao());

			// 系统在途量(已生成物料需求信息，未到货)
			String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
					+ banben_hql2
					+ " and kgliao=? and (number>rukuNum or rukuNum is null)  and (status<>'取消' or status is null) "
					+ specification_sql;
			Double ztCountd = (Double) totalDao.getObjectByCondition(hql_zc0,
					procard.getMarkId(), procard.getKgliao());
			if (ztCountd == null) {
				ztCountd = 0D;
			}
			Float ztCount = ztCountd.floatValue();

			// 导入在途量(系统切换时导入在途量)
			String hqlGoods_zt = "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
					+ banben_hql
					+ " and kgliao=? and goodsClass in ('在途库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
			Float kcCount_zt = (Float) totalDao.getObjectByCondition(
					hqlGoods_zt, procard.getMarkId(), procard.getKgliao());
			if (kcCount_zt == null || kcCount_zt < 0) {
				kcCount_zt = 0f;
			}
			ztCount += kcCount_zt;
			if (ztCount < 0) {
				ztCount = 0F;
			}
			/****************** 结束 在途量=采购在途量+导入在途量 结束 ******************************/
			// (库存量+在途量(已生成采购，未到货))-占用量=剩余可用库存量
			Float daizhiCount = (kcCount + ztCount) - zyCount;
			if (daizhiCount < 0) {
				daizhiCount = 0F;
			}
			int i = 0;
			for (Procard yProcard : yProcardList) {
				Procard fProcard = yProcard.getProcard();
				Set<Procard> sonSet = fProcard.getProcardSet();
				sonSet.remove(yProcard);
				yProcard.setSbStatus("删除");
				if (yProcard.getFatherId() != null) {
					yProcard.setOldFatherId(yProcard.getFatherId());
					yProcard.setOldRootId(yProcard.getRootId());
				}
				yProcard.setFatherId(null);
				yProcard.setRootId(null);
				yProcard.setProcard(null);
				totalDao.update(fProcard);
				Procard newProcard = new Procard();
				newProcard.setThProcardId(yProcard.getId());
				newProcard.setJihuoStatua("激活");
				String result = String.format("%.4f", yProcard.getFilnalCount()
						* quanzi);
				Float filnalCount = Float.parseFloat(result);
				newProcard.setStatus("已发卡");
				newProcard.setWlstatus("待定");
				newProcard.setYwMarkId(yProcard.getYwMarkId());
				newProcard.setFilnalCount(filnalCount);
				newProcard.setHascount(filnalCount);
				newProcard.setKlNumber(filnalCount);
				newProcard.setRootMarkId(yProcard.getRootMarkId());
				newProcard.setOrderNumber(yProcard.getOrderNumber());
				newProcard.setQuanzi1(yProcard.getQuanzi1());
				newProcard.setQuanzi2(Float.parseFloat(String.format("%.4f",
						yProcard.getQuanzi2() * quanzi)));
				newProcard.setMarkId(yclAndWgj.getMarkId());
				newProcard.setLingliaostatus(yProcard.getLingliaostatus());
				newProcard.setProName(yclAndWgj.getName());
				newProcard.setWgType(yclAndWgj.getWgType());
				newProcard.setKgliao(yclAndWgj.getKgliao());
				newProcard.setTuhao(yclAndWgj.getTuhao());
				newProcard.setSpecification(yclAndWgj.getSpecification());
				newProcard.setBanBenNumber(yclAndWgj.getBanbenhao());
				newProcard.setZhikarenId(yProcard.getZhikarenId());
				newProcard.setZhikaren(yProcard.getZhikaren());
				String banbenSql2 = "";
				if (yclAndWgj.getBanbenhao() == null
						|| yclAndWgj.getBanbenhao().length() == 0) {
					banbenSql2 = " and banBenNumber is null or banBenNumber =''";
				} else {
					banbenSql2 = " and banBenNumber is not null and banBenNumber='"
							+ yclAndWgj.getBanbenhao() + "'";
				}
				Integer banci = (Integer) totalDao
						.getObjectByCondition(
								"select banci from ProcardTemplate where markId=? "
										+ "and (dataStatus is null or dataStatus!='删除') and (banbenStatus is null or banbenStatus !='历史') "
										+ banbenSql2, yclAndWgj.getMarkId());
				if (banci == null) {
					banci = 0;
				}
				newProcard.setBanci(banci);
				newProcard.setRootId(procard.getRootId());
				newProcard.setFatherId(fProcard.getId());
				newProcard.setProcard(fProcard);
				newProcard.setBelongLayer(yProcard.getBelongLayer());
				newProcard.setPlanOrderId(yProcard.getPlanOrderId());// 内部计划单id
				newProcard.setPlanOrderNum(yProcard.getPlanOrderNum());// 内部计划单号
				newProcard.setOrderNumber(yProcard.getOrderNumber());// 订单编号
				newProcard.setOrderId(yProcard.getOrderId());// 订单id
				newProcard.setJioafuDate(yProcard.getJioafuDate());// 交付日期
				newProcard.setYwMarkId(yProcard.getYwMarkId());
				newProcard.setUnit(yclAndWgj.getUnit());
				newProcard.setProductStyle(yProcard.getProductStyle());//
				newProcard.setProcardStyle("外购");
				// 批次计算
				newProcard.setSelfCard(findMaxSelfCard(yclAndWgj.getMarkId()));
				// 比较库存设置tjNumber
				if (daizhiCount > newProcard.getFilnalCount()) {
					daizhiCount -= newProcard.getFilnalCount();
					newProcard.setTjNumber(newProcard.getFilnalCount());
				} else {
					newProcard.setTjNumber(daizhiCount);
					daizhiCount = 0f;
				}
				// 设置minNumber
				Float minNumber = newProcard.getTjNumber()
						/ newProcard.getQuanzi2() * newProcard.getQuanzi1();
				if (newProcard.getTjNumber() >= newProcard.getFilnalCount()) {
					minNumber = fProcard.getFilnalCount();
				}
				newProcard.setMinNumber(minNumber);
				if (ncgNumber != null) {// 以此为采购数量
					if (i < (yProcardList.size() - 1)) {
						if (ncgNumber < (newProcard.getFilnalCount() - newProcard
								.getTjNumber())) {
							newProcard.setCgNumber(ncgNumber);// 采购数量不足,库存数量也不足，需要去手动入库数据
							ncgNumber = 0f;
						} else {
							newProcard.setCgNumber(newProcard.getFilnalCount()
									- newProcard.getTjNumber());// 全部采购
							ncgNumber -= (newProcard.getFilnalCount() - newProcard
									.getTjNumber());
						}
					} else {
						newProcard.setCgNumber(ncgNumber);// 不管采购数量是否有结余都压在最后一个上面
						ncgNumber = 0f;
					}
				}
				totalDao.save(newProcard);
				sonSet.add(newProcard);
				fProcard.setProcardSet(sonSet);
				Float fminNumber = minNumber;
				// 设置关联proessInforwwProcard
				ProcessInforWWProcard pwwprocard = (ProcessInforWWProcard) totalDao
						.getObjectByCondition(
								"from ProcessInforWWProcard where procardId=? "
										+ " and (status is null or status not in('删除','取消')) "
										+ " and (applyDtailId not in (select id from ProcessInforWWApplyDetail where dataStatus in('删除','取消') or processStatus in('删除','取消')) or applyDtailId is null) "
										+ " and (wwxlId not in (select id from WaigouWaiweiPlan where status in('删除','取消') ) or wwxlId is null) ",
								yProcard.getId());
				if (pwwprocard != null) {
					ProcessInforWWProcard newpwwprocard = new ProcessInforWWProcard();
					newpwwprocard.setProcardId(newProcard.getId());// 零件id
					newpwwprocard.setMarkId(newProcard.getMarkId());// 件号
					newpwwprocard.setProcName(newProcard.getProName());// 名称
					newpwwprocard.setBanben(newProcard.getBanBenNumber());// 版本号
					newpwwprocard.setBanci(newProcard.getBanci());// 版次
					Float applyCount = pwwprocard.getApplyCount()
							* newProcard.getFilnalCount()
							/ yProcard.getFilnalCount();
					newpwwprocard.setApplyCount(applyCount);// 数量
					newpwwprocard.setHascount(applyCount);// 剩余未领数量（工序外委使用）
					newpwwprocard.setStatus("使用");// 状态
					newpwwprocard.setAddTime(Util.getDateTime());
					newpwwprocard.setProcardStyle(newProcard.getProcardStyle());// 生产或者外购(生产包括自制和外购半成品)
					newpwwprocard.setApplyDtailId(pwwprocard.getApplyDtailId());// 外委申请明细id对应表ProcessInforWWApplyDetail(手动外委)
					newpwwprocard.setWwxlId(pwwprocard.getWwxlId());// 外委序列ID(WaigouWaiweiPlan的id)(BOM外委)
					pwwprocard.setStatus("删除");
					totalDao.update(pwwprocard);
					totalDao.save(newpwwprocard);
				}
				for (Procard son : sonSet) {
					if (fProcard.getSbStatus() != null
							&& fProcard.getSbStatus().equals("删除")) {
						continue;
					}
					if (son.getMinNumber() == null) {
						son.setMinNumber(0f);
					}
					if (fProcard.getLingliaoType() == null
							|| !fProcard.getLingliaoType().equals("part")) {
						if (fminNumber > son.getMinNumber()) {
							fminNumber = son.getMinNumber();
						}
					} else {
						if (fminNumber < son.getMinNumber()) {
							fminNumber = son.getMinNumber();
						}
					}
				}
				if (fminNumber > 0) {
					if (fminNumber > fProcard.getFilnalCount()) {
						fminNumber = fProcard.getFilnalCount();
					}
					fProcard.setJihuoStatua("激活");
					if (fProcard.getKlNumber() == null) {
						fProcard.setKlNumber(fminNumber);
						fProcard.setHascount(fminNumber);
					} else if (fminNumber > fProcard.getKlNumber()) {
						fProcard.setHascount(fProcard.getHascount()
								+ (fminNumber - fProcard.getKlNumber()));
						fProcard.setKlNumber(fminNumber);
					}
				}
				totalDao.update(fProcard);
				i++;
			}
			return "true";
		} else {
			return "数据异常";
		}

	}

	@Override
	public String changeWgj2(Procard procard, String markId, String kgliao) {
		// TODO Auto-generated method stub
		String hql_all = "select sum(filnalCount),sum(cgNumber) "
				+ "from Procard where rootid=? and markId=? and kgliao=?"
				+ " and klNumber=hascount"
				+ " and procardStyle='外购'  and cgNumber is not null and (sbStatus is null or sbStatus!='删除') "
				+ " group by wgType,markId,banBenNumber,kgliao,proName,specification,unit order by sum(cgNumber) desc";
		Object[] yuanCounts = (Object[]) totalDao.getObjectByCondition(hql_all,
				procard.getRootId(), markId, kgliao);
		if (yuanCounts != null && yuanCounts[0] != null
				&& yuanCounts[1] != null) {
			Float yfilnalCount = Float.valueOf(yuanCounts[0].toString());
			Float ycgNumber = Float.valueOf(yuanCounts[1].toString());
			Float quanzi = procard.getFilnalCount() / yfilnalCount;
			Float ncgNumber = procard.getCgNumber();
			List<Procard> yProcardList = totalDao
					.query(
							"from Procard where rootId=? and markId=? and kgliao=? and cgNumber is not null and (outcgNumber is null or outcgNumber=0) and (sbStatus is null or sbStatus!='删除')",
							procard.getRootId(), markId, kgliao);
			String banbenSql = "";
			if (procard.getBanBenNumber() == null
					|| procard.getBanBenNumber().length() == 0) {
				banbenSql = " and (banbenhao is null or banbenhao='') ";
			} else {
				banbenSql = " and banbenhao='" + procard.getBanBenNumber()
						+ "'";
			}
			YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
					.getObjectByCondition(
							"from YuanclAndWaigj where markId=? and kgliao=? and (banbenStatus is null or banbenStatus!='历史')"
									+ banbenSql, procard.getMarkId(), procard
									.getKgliao());
			if (yclAndWgj == null) {
				return "外购件库中没有找到可用的目标外购件!";
			}
			/***************** MRP计算(库存量&&&&&占用量) *****************/
			String goodsClassSql = null;
			// if (procard.getProductStyle() != null
			// && procard.getProductStyle().equals("试制")) {
			// // 试制的外购件去试制库取
			// goodsClassSql = " and goodsClass ='试制库'";
			// } else {
			String kgsql = "";
			if (procard.getKgliao() != null && procard.getKgliao().length() > 0) {
				kgsql += " and kgliao ='" + procard.getKgliao() + "'";
			}
			// goodsClassSql =
			// " and ((goodsClass in ('外购件库','中间库') "
			// + kgsql + " ) or goodsClass = '备货库')";
			goodsClassSql = " and goodsClass in ('外购件库') " + kgsql;
			// }
			String banben_hql = "";
			if (procard.getBanBenNumber() != null
					&& procard.getBanBenNumber().length() > 0) {
				banben_hql = " and banBenNumber='" + procard.getBanBenNumber()
						+ "'";
			}
			String specification_sql = "";
			// if (procard.getSpecification() != null
			// && procard.getSpecification().length() > 0) {
			// specification_sql = " and specification = "
			// + procard.getSpecification();
			// } else {
			// specification_sql =
			// " and (specification = '' and specification is null)";
			// }

			// 库存量(件号+版本+供料属性+库别)
			String hqlGoods = "";
			hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
					+ goodsClassSql
					+ " and goodsCurQuantity>0 "
					+ banben_hql
					+ " and (fcStatus is null or fcStatus='可用')";
			Float kcCount = (Float) totalDao.getObjectByCondition(hqlGoods,
					procard.getMarkId());
			if (kcCount == null || kcCount < 0) {
				kcCount = 0f;
			}

			/****************** 占用量=生产占用量+导入占用量 ******************************/
			// 系统占用量(已计算过采购量(1、有库存 2、采购中)，未领料)
			String zyCountSql = "select sum(hascount) from Procard where markId=? and productStyle=? and kgliao=? "
					+ banben_hql
					+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and (sbStatus is null or sbStatus!='删除')";
			// Float zyCount = (Float) totalDao
			// .getObjectByCondition(zyCountSql, procard
			// .getMarkId(),
			// procard.getProductStyle(), procard
			// .getKgliao());
			Float zyCount = (Float) totalDao.getObjectByCondition(zyCountSql,
					procard.getMarkId(), "批产", procard.getKgliao());
			if (zyCount == null || zyCount < 0) {
				zyCount = 0f;
			}

			// 导入占用量(系统切换时导入占用量)
			String hqlGoods_zy = "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
					+ banben_hql
					+ " and goodsClass in ('占用库') and kgliao=? and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
			Float kcCount_zy = (Float) totalDao.getObjectByCondition(
					hqlGoods_zy, procard.getMarkId(), procard.getKgliao());
			if (kcCount_zy == null || kcCount_zy < 0) {
				kcCount_zy = 0f;
			}
			zyCount += kcCount_zy;
			if (zyCount < 0) {
				zyCount = 0F;
			}
			/****************** 结束 占用量=生产占用量+导入占用量 结束 ******************************/
			/****************** 在途量=采购在途量+导入在途量 ******************************/
			// 系统在途量(已生成采购计划，未到货)
			String hql_zc = "select sum(cgNumber-dhNumber) from  Procard where markId=? and productStyle=? "
					+ banben_hql
					+ " and kgliao=? and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购'"
					+ " and cgNumber >0 and dhNumber is not null and (sbStatus is null or sbStatus!='删除')";
			// Float ztCount = (Float) totalDao.getObjectByCondition(
			// hql_zc, procard.getMarkId(), procard
			// .getProductStyle(), procard.getKgliao());

			// 系统在途量(已生成物料需求信息，未到货)
			String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
					+ banben_hql
					+ " and kgliao=? and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null)"
					+ specification_sql;
			Double ztCountd = (Double) totalDao.getObjectByCondition(hql_zc0,
					procard.getMarkId(), procard.getKgliao());
			if (ztCountd == null) {
				ztCountd = 0D;
			}
			Float ztCount = ztCountd.floatValue();

			// 导入在途量(系统切换时导入在途量)
			String hqlGoods_zt = "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
					+ banben_hql
					+ " and kgliao=? and goodsClass in ('在途库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
			Float kcCount_zt = (Float) totalDao.getObjectByCondition(
					hqlGoods_zt, procard.getMarkId(), procard.getKgliao());
			if (kcCount_zt == null || kcCount_zt < 0) {
				kcCount_zt = 0f;
			}
			ztCount += kcCount_zt;
			if (ztCount < 0) {
				ztCount = 0F;
			}
			/****************** 结束 在途量=采购在途量+导入在途量 结束 ******************************/
			// (库存量+在途量(已生成采购，未到货))-占用量=剩余可用库存量
			Float daizhiCount = (kcCount + ztCount) - zyCount;
			if (daizhiCount < 0) {
				daizhiCount = 0F;
			}
			int i = 0;
			// 零件替换
			for (Procard yProcard : yProcardList) {
				Procard fProcard = yProcard.getProcard();
				Set<Procard> sonSet = fProcard.getProcardSet();
				sonSet.remove(yProcard);
				yProcard.setSbStatus("删除");
				if (yProcard.getFatherId() != null) {
					yProcard.setOldFatherId(yProcard.getFatherId());
					yProcard.setOldRootId(yProcard.getRootId());
				}
				yProcard.setFatherId(null);
				yProcard.setRootId(null);
				yProcard.setProcard(null);
				totalDao.update(fProcard);
				Procard newProcard = new Procard();
				newProcard.setJihuoStatua("激活");
				String result = String.format("%.4f", yProcard.getFilnalCount()
						* quanzi);
				Float filnalCount = Float.parseFloat(result);
				newProcard.setFilnalCount(filnalCount);
				newProcard.setHascount(filnalCount);
				newProcard.setKlNumber(filnalCount);
				newProcard.setQuanzi1(yProcard.getQuanzi1());
				newProcard.setQuanzi2(Float.parseFloat(String.format("%.4f",
						yProcard.getQuanzi2() * quanzi)));
				newProcard.setMarkId(yclAndWgj.getMarkId());
				newProcard.setProName(yclAndWgj.getName());
				newProcard.setWgType(yclAndWgj.getWgType());
				newProcard.setKgliao(yclAndWgj.getKgliao());
				newProcard.setTuhao(yclAndWgj.getTuhao());
				newProcard.setSpecification(yclAndWgj.getSpecification());
				newProcard.setBanBenNumber(yclAndWgj.getBanbenhao());
				String banbenSql2 = "";
				if (yclAndWgj.getBanbenhao() == null
						|| yclAndWgj.getBanbenhao().length() == 0) {
					banbenSql2 = " and banBenNumber is null or banBenNumber =''";
				} else {
					banbenSql2 = " and banBenNumber is not null and banBenNumber='"
							+ yclAndWgj.getBanbenhao() + "'";
				}
				Integer banci = (Integer) totalDao
						.getObjectByCondition(
								"select banci from ProcardTemplate where markId=? "
										+ "and (dataStatus is null or dataStatus!='删除') and (banbenStatus is null or banbenStatus !='历史') "
										+ banbenSql2, yclAndWgj.getMarkId());
				if (banci == null) {
					banci = 0;
				}
				newProcard.setBanci(banci);
				newProcard.setRootId(procard.getRootId());
				newProcard.setFatherId(fProcard.getId());
				newProcard.setProcard(fProcard);
				newProcard.setBelongLayer(procard.getBelongLayer());
				newProcard.setPlanOrderId(procard.getPlanOrderId());// 内部计划单id
				newProcard.setPlanOrderNum(procard.getPlanOrderNum());// 内部计划单号
				newProcard.setOrderNumber(procard.getOrderNumber());// 订单编号
				newProcard.setOrderId(procard.getOrderId());// 订单id
				newProcard.setJioafuDate(procard.getJioafuDate());// 交付日期
				newProcard.setYwMarkId(procard.getYwMarkId());
				newProcard.setUnit(yclAndWgj.getUnit());
				newProcard.setProcardStyle("外购");
				// 批次计算
				newProcard.setSelfCard(findMaxSelfCard(yclAndWgj.getMarkId()));
				// 比较库存设置tjNumber
				if (daizhiCount > newProcard.getFilnalCount()) {
					daizhiCount -= newProcard.getFilnalCount();
					newProcard.setTjNumber(newProcard.getFilnalCount());
				} else {
					newProcard.setTjNumber(daizhiCount);
					daizhiCount = 0f;
				}
				// 设置minNumber
				Float minNumber = newProcard.getTjNumber()
						/ newProcard.getQuanzi2() * newProcard.getQuanzi1();
				if (newProcard.getTjNumber() >= newProcard.getFilnalCount()) {
					minNumber = fProcard.getFilnalCount();
				}
				newProcard.setMinNumber(minNumber);
				if (ncgNumber != null) {// 以此为采购数量
					if (i < (yProcardList.size() - 1)) {
						if (ncgNumber < (newProcard.getFilnalCount() - newProcard
								.getTjNumber())) {
							newProcard.setCgNumber(ncgNumber);// 采购数量不足,库存数量也不足，需要去手动入库数据
							ncgNumber = 0f;
						} else {
							newProcard.setCgNumber(newProcard.getFilnalCount()
									- newProcard.getTjNumber());// 全部采购
							ncgNumber -= (newProcard.getFilnalCount() - newProcard
									.getTjNumber());
						}
					} else {
						newProcard.setCgNumber(ncgNumber);// 不管采购数量是否有结余都压在最后一个上面
						ncgNumber = 0f;
					}
				}
				totalDao.save(newProcard);
				sonSet.add(newProcard);
				fProcard.setProcardSet(sonSet);
				Float fminNumber = minNumber;
				for (Procard son : sonSet) {
					if (fProcard.getSbStatus() != null
							&& fProcard.getSbStatus().equals("删除")) {
						continue;
					}
					if (fProcard.getLingliaoType() == null
							|| !fProcard.getLingliaoType().equals("part")) {
						if (fminNumber > son.getMinNumber()) {
							fminNumber = son.getMinNumber();
						}
					} else {
						if (fminNumber < son.getMinNumber()) {
							fminNumber = son.getMinNumber();
						}
					}
				}
				if (fminNumber > 0) {
					if (fminNumber > fProcard.getFilnalCount()) {
						fminNumber = fProcard.getFilnalCount();
					}
					fProcard.setJihuoStatua("激活");
					if (fProcard.getKlNumber() == null) {
						fProcard.setKlNumber(fminNumber);
						fProcard.setHascount(fminNumber);
					} else if (fminNumber > fProcard.getKlNumber()) {
						fProcard.setHascount(fProcard.getHascount()
								+ (fminNumber - fProcard.getKlNumber()));
						fProcard.setKlNumber(fminNumber);
					}
				}
				totalDao.update(fProcard);
				i++;
			}

		}
		return "true";
	}

	// 查询该件号最大批次号
	public String findMaxSelfCard(String markId) {
		String mouth = new SimpleDateFormat("yyyyMMdd").format(new Date());
		int yy = Integer.parseInt(mouth.substring(0, 4));
		int mm = Integer.parseInt(mouth.substring(4, 6));
		int dd = Integer.parseInt(mouth.substring(6, 8));
		if (dd >= 26) {
			if (mm == 12) {
				mm = 1;
				yy++;
			} else {
				mm++;
			}
		}
		if (mm < 10) {
			mouth = yy + "0" + mm;
		} else {
			mouth = yy + "" + mm;
		}
		String hql = "select max(selfCard) from Procard where markId=? and selfCard like '%"
				+ mouth + "%'";
		Object object = (Object) totalDao.getObjectByCondition(hql, markId);
		if (object != null) {
			Long selfCard = Long.parseLong(object.toString()) + 1;// 当前最大流水卡片
			return selfCard.toString();
		} else {
			return mouth + "00001";
		}
	}

	@Override
	public WaigouOrder findLastWgOrderById(Integer id) {
		if (id != null) {
			WaigouOrder wgorder = (WaigouOrder) totalDao.get(WaigouOrder.class,
					id);
			WaigouOrder lastwgorder = (WaigouOrder) totalDao
					.getObjectByCondition(
							" from WaigouOrder where addUserCode = ? and planNumber <? and status not in ('入库','完成','退货')  order by planNumber desc ",
							wgorder.getAddUserCode(), wgorder.getPlanNumber());

		}
		return null;
	}

	@Override
	public List<WaigouOrder> gotoprint(Integer[] processIds) {
		if (processIds != null && processIds.length > 0) {
			List<WaigouOrder> wgorderList = new ArrayList<WaigouOrder>();
			for (int i = 0; i < processIds.length; i++) {
				Object[] obj = findWaigouPlanListByid(processIds[i]);
				WaigouOrder wgOrder = (WaigouOrder) obj[0];
				List<WaigouPlan> list = (List<WaigouPlan>) obj[1];
				if (list != null && list.size() > 0) {
					for (int j = (list.size() - 1); j >= 0; j--) {
						WaigouPlan plan = list.get(j);
						if (plan.getStatus() != null
								&& (plan.getStatus().equals("删除") || plan
										.getStatus().equals("取消"))
								&& (plan.getQsNum() == null || plan.getQsNum() == 0)) {
							list.remove(j);
						}
					}
				}
				wgOrder.setWwpList(list);
				wgOrder.setAllMoneys((String) obj[2]);
				wgOrder.setZhUser((ZhUser) obj[3]);
				wgorderList.add(wgOrder);
			}
			return wgorderList;
		}
		return null;
	}

	@Override
	public boolean exportFileMop(List<ManualOrderPlan> mopList) {
		boolean bool = true;
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(
							(Util.getDateTime("yyyyMMddhhmmss") + "_待采购物料清单")
									.getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("待采购物料清单", 0);
			WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 11,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);
			WritableCellFormat wcf_title = new WritableCellFormat(wf_title);
			// wcf_title.setBackground(jxl.format.Colour.WHITE);
			wcf_title.setAlignment(jxl.format.Alignment.CENTRE);
			wcf_title.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			ws.setColumnView(3, 24);
			ws.setColumnView(4, 18);
			ws.setColumnView(5, 18);
			ws.setColumnView(11, 22);
			ws.addCell(new Label(0, 0, "待采购计划", wcf_title));
			ws.mergeCells(0, 0, 11, 0);
			WritableCellFormat titleFormat = new WritableCellFormat(wf_title);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置居中对齐
			titleFormat.setAlignment(jxl.format.Alignment.CENTRE);// 设置居中对齐（这俩哪个是上下/左右对齐也没验证过）
			// 类别 件号 图号 规格 版本 供料属性 零件名称 采购数量 单位 操作
			ws.addCell(new Label(0, 1, "序号", titleFormat));
			ws.addCell(new Label(1, 1, "价格状态", titleFormat));
			ws.addCell(new Label(2, 1, "类别", titleFormat));
			ws.addCell(new Label(3, 1, "件号", titleFormat));
			ws.addCell(new Label(4, 1, "图号", titleFormat));
			ws.addCell(new Label(5, 1, "规格", titleFormat));
			ws.addCell(new Label(6, 1, "版本", titleFormat));
			ws.addCell(new Label(7, 1, "供料属性", titleFormat));
			ws.addCell(new Label(8, 1, "零件名称", titleFormat));
			ws.addCell(new Label(9, 1, "采购数量", titleFormat));
			ws.addCell(new Label(10, 1, "单位", titleFormat));
			ws.addCell(new Label(11, 1, "添加时间", titleFormat));
			DecimalFormat df = new DecimalFormat("#.###");
			int count = 2;
			for (int i = 0; i < mopList.size(); i++) {
				ManualOrderPlan mop = mopList.get(i);
				ws.addCell(new Label(0, i + count, (i + 1) + ""));
				String pricestatus = "";
				if (mop.getPrice() != null && mop.getPrice() > 0) {
					if (mop.getCgbl() != null && mop.getCgbl() > 0) {
						if (mop.getIsuse() == null || "".equals(mop.getIsuse())
								|| "待".equals(mop.getIsuse())) {
							pricestatus = "待";
						}
					} else {
						pricestatus = "配";
					}
				} else {
					pricestatus = "无";
				}
				ws.addCell(new Label(1, i + count, pricestatus, titleFormat));
				ws
						.addCell(new Label(2, i + count, mop.getWgType(),
								titleFormat));
				ws
						.addCell(new Label(3, i + count, mop.getMarkId(),
								titleFormat));
				ws
						.addCell(new Label(4, i + count, mop.getTuhao(),
								titleFormat));
				ws.addCell(new Label(5, i + count, mop.getSpecification(),
						titleFormat));
				ws
						.addCell(new Label(6, i + count, mop.getBanben(),
								titleFormat));
				ws
						.addCell(new Label(7, i + count, mop.getKgliao(),
								titleFormat));
				ws.addCell(new Label(8, i + count, mop.getProName(),
						titleFormat));
				ws.addCell(new jxl.write.Number(9, i + count, mop.getNumber(),
						titleFormat));
				ws
						.addCell(new Label(10, i + count, mop.getUnit(),
								titleFormat));
				ws.addCell(new Label(11, i + count, mop.getAddtime(),
						titleFormat));
			}
			wwb.write();
			wwb.close();
		} catch (Exception e) {
			bool = false;
			e.printStackTrace();
		}
		return bool;
	}

	@Override
	public String updatewgddNum(Integer id, Float num) {
		if (id != null) {// waigouPlanId
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
			if (wgdd.getQrNumber() != null && wgdd.getQrNumber() > 0) {
				return "该送货单明细仓库已签收不能修改送货数量";
			}
			if (num == null || num < 0) {
				return "送货数量不能为空，不能小于0";
			}
			if (num > wgdd.getShNumber()) {
				return "本次修改送货数量不能大于之前送货数量";
			}
			float change = 0f;
			WaigouPlan wgplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					wgdd.getWaigouPlanId());
			if (wgplan != null) {
				change = wgdd.getShNumber() - num;
				wgplan.setSyNumber(wgplan.getSyNumber() + change);
				totalDao.update(wgplan);
				// 修改物料需求上的未送货数量
				if (wgplan.getMopId() != null) {
					ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
							ManualOrderPlan.class, wgplan.getMopId());
					if (mop != null) {
						mop.setWshCount(mop.getWshCount() + change);
						totalDao.update(mop);
					}
				}
			}
			wgdd.setShNumber(num);
			return totalDao.update(wgdd) + "";
		}
		return null;
	}

	@Override
	public String buzheng(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		WaigouPlan wp = (WaigouPlan) totalDao.getObjectById(WaigouPlan.class,
				id);
		if (wp == null) {
			return "没有找到目标订单明细!";
		}
		WaigouOrder wo = wp.getWaigouOrder();
		if (wo == null) {
			return "数据异常,没有找到目标订单";
		}
		if (wo.getAddUserCode() == null
				|| !wo.getAddUserCode().equals(user.getCode())) {
			return "您没有对此订单的修改权限!";
		}
		Float newNumber = wp.getNumber();
		newNumber = (float) Math.ceil(newNumber);
		if (newNumber.equals(wp.getNumber())) {
			return "对不起,不需要补整!";
		}
		if (wp.getNumber().equals(wp.getSyNumber())) {
			wp.setSyNumber(newNumber);
		} else {
			wp.setSyNumber(wp.getSyNumber() + newNumber - wp.getNumber());
		}
		wp.setNumber(newNumber);
		Double money = wp.getNumber() * wp.getHsPrice();
		wp.setMoney((double)Util.FomartDouble(money, 4));
		return totalDao.update(wp) + "";
	}

	@Override
	public String shuaixinAllPrice(Integer[] ids) {
		StringBuffer strb = new StringBuffer();
		if (ids != null && ids.length > 0) {
			for (int i = 0, len = ids.length; i < len; i++) {
				strb.append(shuaixinPrice(ids[i]) + "\\n");
			}
		} else {
			return "请至少选择一项，进行刷新单价!~";
		}
		return strb.toString();
	}

	@Override
	public String wgOrderIsPrint(Integer[] processIds) {
		if (processIds != null && processIds.length > 0) {
			for (int i = 0; i < processIds.length; i++) {
				WaigouOrder wgorder = (WaigouOrder) totalDao.get(
						WaigouOrder.class, processIds[i]);
				wgorder.setPrintTime(Util.getDateTime("yyyy-MM-dd hh:mm:ss"));
				wgorder.setIsprint("是");
				if (!totalDao.update(wgorder)) {
					return "error";
				}
			}
		}
		return "true";
	}

	@Override
	public void sendmsg(WaigouDailySheet wgdSheet) {
		if (wgdSheet != null) {
			WaigouDailySheet old = (WaigouDailySheet) totalDao.get(
					WaigouDailySheet.class, wgdSheet.getId());
			old.setSampleNumber(wgdSheet.getSampleNumber());
			totalDao.update(old);
			// 发消息给检测室实验员，进行环保、盐雾等检测。
			AlertMessagesServerImpl.addAlertMessages("来料日报表管理", "件号:"
					+ old.getMarkId() + "，订单编号:" + old.getCgOrderNum()
					+ "检验批次:" + old.getExamineLot() + " 样品编号:"
					+ wgdSheet.getSampleNumber()
					+ "已检验完毕，请及时进行材质、 环保、盐雾、阻燃等检测。检测完毕后请上传检测文件。 ", "1");
		}

	}

	@Override
	public String addmujuOrder(Integer[] ids) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (ids != null && ids.length > 0) {
			Map<Integer, WaigouOrder> map = new HashMap<Integer, WaigouOrder>();
			for (int i = 0; i < ids.length; i++) {
				MouldApplyOrder oldmoa = (MouldApplyOrder) totalDao.get(
						MouldApplyOrder.class, ids[i]);
				Price price = (Price) totalDao.get(Price.class, oldmoa
						.getPriceId());
				ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
						.getGysId());
				WaigouOrder wgOrder = map.get(zhuser.getId());
				if (wgOrder == null) {
					wgOrder = new WaigouOrder();
					wgOrder.setGysId(price.getGysId());
					wgOrder.setUserId(user.getId());
					wgOrder.setUserCode(user.getCode());// 用户工号
					wgOrder.setGysName(user.getName());// 供应商姓名
					wgOrder.setGhAddress(zhuser.getCompanydz());// 供货地址（供应商地址）
					wgOrder.setLxPeople(zhuser.getCperson());// 供应商联系人
					wgOrder.setGysPhone(zhuser.getCfax()); // 供应商电话/手机
					wgOrder.setPlanNumber(osNumber());// 订单编号
					wgOrder.setAddTime(Util.getDateTime());// 添加时间
					wgOrder.setAddUserCode(user.getCode());// 添加人工号
					wgOrder.setAddUserName(user.getName());// 添加人姓名
					wgOrder.setPayType(zhuser.getFkfs());// 付款方式
					wgOrder.setPiaojuType(zhuser.getZzsl());
					wgOrder.setShAddress(Util.getLoginCompanyInfo()
							.getAddress());
					wgOrder.setStatus("待核对");
					wgOrder.setCaigouMonth(Util.getDateTime("yyyy年MM月"));
					wgOrder
							.setAddUserPhone(user.getPassword()
									.getPhoneNumber());
					wgOrder.setType("模具");
					Set<WaigouPlan> wgplanSet = new HashSet<WaigouPlan>();
					WaigouPlan wgplan = new WaigouPlan();
					wgplan.setMujuNumber(oldmoa.getPlanNumber());
					wgplan.setNumber(oldmoa.getNum().floatValue());
					wgplan.setProName(price.getName());
					wgplan.setSyNumber(oldmoa.getNum().floatValue());
					wgplan.setType("模具");
					wgplan.setHsPrice(price.getHsPrice());
					wgplan.setPrice(price.getBhsPrice());
					wgplan.setTaxprice(price.getTaxprice());
					wgplan.setUserId(user.getId());
					wgplan.setUserCode(user.getCode());
					wgplan.setGysId(zhuser.getId());
					wgplan.setGysName(zhuser.getCmp());
					wgplan.setGysjc(zhuser.getName());
					wgplan.setWlWeizhiDt("mj");// 模具下单
					wgplan.setAddTime(Util.getDateTime());
					wgplanSet.add(wgplan);
					wgOrder.setWwpSet(wgplanSet);
					wgOrder.setApplystatus("未申请");
					totalDao.save(wgOrder);
					map.put(zhuser.getId(), wgOrder);
				} else {
					Set<WaigouPlan> wgplanSet = wgOrder.getWwpSet();
					WaigouPlan wgplan = new WaigouPlan();
					wgplan.setMujuNumber(oldmoa.getPlanNumber());
					wgplan.setNumber(oldmoa.getNum().floatValue());
					wgplan.setProName(price.getName());
					wgplan.setSyNumber(oldmoa.getNum().floatValue());
					wgplan.setType("模具");
					wgplan.setHsPrice(price.getHsPrice());
					wgplan.setPrice(price.getBhsPrice());
					wgplan.setTaxprice(price.getTaxprice());
					wgplan.setUserId(user.getId());
					wgplan.setUserCode(user.getCode());
					wgplan.setGysId(zhuser.getId());
					wgplan.setGysName(zhuser.getCmp());
					wgplan.setGysjc(zhuser.getName());
					wgplan.setWlWeizhiDt("mj");// 模具下单
					wgplan.setAddTime(Util.getDateTime());
					wgplanSet.add(wgplan);
					wgOrder.setWwpSet(wgplanSet);
					totalDao.update(wgOrder);
				}
			}
			return "true";
		}
		return "请选择开模申请单!~";
	}

	@Override
	public String agreeSbquxiaoww(Integer id) {
		// TODO Auto-generated method stub
		Users loginUser = Util.getLoginUser();
		if (loginUser == null) {
			return "请先登录!";
		}
		WaigouPlan wwPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id);
		if (wwPlan == null) {
			return "没有找到目标委外明细!";
		}
		if (wwPlan.getUserId().equals(loginUser.getId())
				|| wwPlan.getWaigouOrder().getAddUserCode().equals(
						loginUser.getCode())) {
			wwPlan.setStatus("取消");
			totalDao.update(wwPlan);
			AlertMessagesServerImpl.addAlertMessages("设变外委取消", "供应商("
					+ wwPlan.getGysName() + ")同意了您的外委单取消申请,委外单号:"
					+ wwPlan.getWaigouOrder().getPlanNumber() + ",外委零件："
					+ wwPlan.getMarkId() + ",外委工序:" + wwPlan.getProcessNames()
					+ "(" + wwPlan.getProcessNOs() + ")。", "1", wwPlan
					.getWaigouOrder().getAddUserCode());
			return "true";
		} else {
			return "对不起您无权限此操作!";
		}
	}

	@Override
	public Object[] findTotalProduct(Integer id) {
		String hql = "from WaigouPlan where  gysId=? and hasruku>0";
		List<WaigouPlan> list = totalDao.query(hql, id);
		Object[] o = { list };
		return o;
	}

	@Override
	public List<WaigouDeliveryDetail> findAllflDrk() {
		String hql = " from WaigouDeliveryDetail where type = '辅料' and status = '待入库'";
		return totalDao.query(hql);
	}

	@Override
	public List findchangeOrderListByplanId(Integer id) {
		// TODO Auto-generated method stub
		// WaigouPlan waigouPlan = getWaigouPlanById(id);
		// if(waigouPlan!=null){
		// if(waigouPlan.getType().equals("外购")){
		// String hql =
		// "from WaigouPlan where  waigouOrder.id in  (select id from WaigouOrder where id in (select waigouOrder.id from WaigouPlan where markId = ?) and applystatus not in ('审批中','同意')) and markId = ? and waigouOrder.addUserCode=?";
		// return totalDao.query(hql, waigouPlan.getMarkId(),
		// waigouPlan.getMarkId(), Util.getLoginUser()
		// .getCode());
		// }else{//订单的外委类型要一样,订单的供应商要有对应的价格
		// }
		// }
		//		
		return null;
	}

	@Override
	public String querenflKu(String bacode, Integer id) {
		WarehouseNumber wn = (WarehouseNumber) totalDao.getObjectByCondition(
				"from WarehouseNumber where barCode = ?", bacode);
		if (wn == null)
			return "库位不存在";
		WaigouDeliveryDetail detail = (WaigouDeliveryDetail) totalDao
				.getObjectByCondition(
						"from WaigouDeliveryDetail where id = ? and status = '待入库' ",
						id);
		if (detail != null) {
			if (detail.getType().equals("辅料")) {
				if (wn.getWareHouseName() == null
						|| !wn.getWareHouseName().equals("综合库")) {
					return "辅料需入综合库，该库位不属于综合库";
				}
			}

		} else
			return "入库失败,数据异常或者已入库!请刷新后重试!";

		// 待检库出库
		String hql_ = " from GoodsStore where goodsStoreWarehouse = ? and wwddId=? and goodsStoreMarkId =?";
		GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_, "待检库",
				detail.getId(), detail.getMarkId());
		if (t != null) {
			String hql = "from Goods where goodsMarkId = ? ";
			if (t.getGoodsStoreWarehouse() != null
					&& t.getGoodsStoreWarehouse().length() > 0) {
				hql += " and goodsClass='" + t.getGoodsStoreWarehouse() + "'";
			}
			if (t.getGoodHouseName() != null
					&& t.getGoodHouseName().length() > 0) {
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
			if (t.getGoodsStoreLot() != null
					&& t.getGoodsStoreLot().length() > 0) {
				hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
			}
			Goods s = (Goods) totalDao.getObjectByCondition(hql,
					new Object[] { t.getGoodsStoreMarkId() });
			if (s != null) {
				s.setGoodsCurQuantity(s.getGoodsCurQuantity()
						- detail.getHgNumber());
				if (s.getGoodsCurQuantity() < 0) {
					s.setGoodsCurQuantity(0f);
				}
				Sell sell = new Sell();
				sell.setSellMarkId(s.getGoodsMarkId());// 件号
				sell.setSellWarehouse(s.getGoodsClass());// 库别
				sell.setGoodHouseName(s.getGoodHouseName());// 仓区
				sell.setKuwei(s.getGoodsPosition());// 库位
				sell.setBanBenNumber(s.getBanBenNumber());// 版本号
				sell.setKgliao(s.getKgliao());// 供料属性
				sell.setProNumber(s.getProNumber());// 项目编号
				sell.setWgType(s.getWgType());// 物料类别
				sell.setSellGoods(s.getGoodsFullName());// 品名
				sell.setSellFormat(s.getGoodsFormat());// 规格
				sell.setSellCount(detail.getHgNumber());// 出库数量
				sell.setSellUnit(s.getGoodsUnit());// 单位
				sell.setSellSupplier(s.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setTuhao(s.getTuhao());// 图号
				sell.setSellLot(s.getGoodsLotId());// 批次
				sell.setBedit(false);// 出库权限
				sell.setBedit(false);// 编辑权限
				sell.setPrintStatus("YES");// 打印状态
				sell.setStyle("待检库出库");
				totalDao.save(sell);
				totalDao.update(s);
			}
		}
		/**
		 * 开始入库;goodsStoreLot wxf
		 */
		GoodsStore goodsStore = new GoodsStore();
		goodsStore.setGoodsStoreTime(Util.getDateTime());// 入库时间;
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));// 入库日期
		goodsStore.setGoodsStorePlanner(Util.getLoginUser().getName());// 操作人
		goodsStore.setStatus("入库");
		goodsStore.setStyle("采购入库");
		goodsStore.setGoodsStoreFormat(detail.getSpecification());// 规格
		goodsStore.setGoodsStoreMarkId(detail.getMarkId());// 件号
		goodsStore.setGoodsStoreGoodsName(detail.getProName());// 名称
		goodsStore.setGoodsStoreUnit(detail.getUnit());// 单位
		goodsStore.setKgliao(detail.getKgliao());// 供料属性
		goodsStore.setBanBenNumber(detail.getBanben());// 版本号;
		goodsStore.setGoodsStoreSupplier(detail.getGysName());// 供应商名称
		goodsStore.setGysId(detail.getGysId() + "");// 供应商Id
		goodsStore.setHsPrice(detail.getHsPrice()==null?null:detail.getHsPrice().doubleValue());// 含税价格
		goodsStore.setGoodsStorePrice(detail.getPrice()==null?null:detail.getPrice().doubleValue());// 不含税价格
		goodsStore.setTaxprice(detail.getTaxprice());// 税率
		goodsStore.setGoodsStoreWarehouse(wn.getWareHouseName());// 所属仓库;
		goodsStore.setWgType(detail.getWgType());// 物料类别
		goodsStore.setGoodHouseName(wn.getWarehouseArea());// 所属仓区;
		goodsStore.setGoodsStorePosition(wn.getNumber());// 所属库位
		goodsStore.setNeiorderId(detail.getCgOrderNum());// 采购订单号
		goodsStore.setGoodsStoreCount(detail.getQrNumber());// 入库数量
		goodsStore.setWwddId(detail.getId());// 送货单明细Id;
		goodsStore.setGoodsStoreLuId(detail.getDemanddept());// 需求部门
		goodsStore.setGoodsStoreLot(detail.getExamineLot());// 批次
		goodsStore.setGoodsStoreSendId(detail.getWaigouDelivery()
				.getPlanNumber());
		// 入库时声称打印单号
		String printNumber = getMaxPrintNumber(goodsStore);
		goodsStore.setPrintNumber(printNumber);
		detail.setPrintNumber(printNumber);
		if ("模具".equals(detail.getType())) {
			goodsStore.setLendNeckStatus("借");
			goodsStore.setLendNeckCount(goodsStore.getGoodsStoreCount());
		} else {
			OaAppDetailTemplate oate = (OaAppDetailTemplate) totalDao
					.getObjectByCondition(
							" from OaAppDetailTemplate where wlcode =? ",
							detail.getMarkId());
			if (oate != null) {
				goodsStore.setLendNeckStatus(oate.getLendNeckStatus());
				if ("借".equals(oate.getLendNeckStatus())) {
					goodsStore
							.setLendNeckCount(goodsStore.getGoodsStoreCount());
				}
			}
		}
		// WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
		// detail.getWaigouPlanId());
		// if (waigouplan != null) {
		// goodsStore.setGoodsStoreProMarkId(waigouplan.getWaigouOrder()
		// .getRootMarkId());// 总成件号
		// }
		goodsStore.setTuhao(detail.getTuhao());// 图号
		if(detail.getHsPrice()!=null){
			goodsStore.setMoney(detail.getHsPrice().doubleValue() * detail.getQrNumber());// 总额
		}
		/*** 添加入库记录、库存记录 ****/
		Goods goodss = goodsStoreServer.save(goodsStore, null);
		goodsStoreServer.updateWaiProcard(goodsStore.getGoodsStoreMarkId(),
				goodsStore.getGoodsStoreCount(), "综合库", goodsStore
						.getGoodsStoreId());
		// 类型为模具/工装，入库之后，自动生成工装报检表
		if ("模具".equals(detail.getType()) || "工装".equals(detail.getType())) {
			MouldApplyOrder mao = (MouldApplyOrder) totalDao
					.getObjectByCondition(
							" from MouldApplyOrder where planNumber = ? ",
							detail.getMujuNumber());
			if (mao != null) {
				Set<MouldDetail> mdSet = mao.getMdSet();
				for (MouldDetail mouldDetail : mdSet) {
					Gzstore gzstore = new Gzstore();
					gzstore.setFk_stoid(goodss.getGoodsId());
					gzstore.setNumber(mouldDetail.getMojuNo());
					gzstore.setMatetag(mouldDetail.getProName());
					gzstore.setTotal(detail.getHgNumber());
					gzstore.setStorehouse(goodss.getGoodHouseName());
					gzstore.setPlace(goodss.getGoodsPosition());
					gzstore.setParClass("模具");
					gzstore.setCurAmount(detail.getHgNumber());
					gzstore.setStorehouse(goodss.getGoodHouseName());
					gzstore.setPlace(goodss.getGoodsPosition());
					gzstore.setMarkId(goodss.getGoodsMarkId());
					gzstore.setMaxBorrowNum(detail.getHgNumber().intValue());
					totalDao.save(gzstore);
				}

			}
		}
		/*** 生成应付账单信息 ***/
		corePayableServer.addCorePayable(detail, goodss);
		return "true";
	}

	@Override
	public List findPriceBywaigouuPlanId(Integer id) {
		// TODO Auto-generated method stub
		WaigouPlan wgplan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id);
		List<Price> priceList = null;
		String nowTime = Util.getDateTime();
		String str = "";
		if (wgplan.getBanben() != null && !"".equals(wgplan.getBanben())) {
			str = " and banbenhao = '" + wgplan.getBanben() + "'";
		} else {
			str = " and (banbenhao = '' or banbenhao is null)";
		}
		if ("外购".equals(wgplan.getType()) || "辅料".equals(wgplan.getType())) {
			String hql = " from Price where partNumber = ? and kgliao = ? and pricePeriodStart<= '"
					+ nowTime
					+ "'and ( pricePeriodEnd >= '"
					+ nowTime
					+ "' or pricePeriodEnd is null  or pricePeriodEnd = '')"
					+ str;
			priceList = totalDao.query(hql, wgplan.getMarkId(), wgplan
					.getKgliao());

		} else if ("外委".equals(wgplan.getType())) {
			String hql = " from Price where partNumber = ? and (gongxunum = ? or gongxunum is null or gongxunum='') and wwType=? and pricePeriodStart<= '"
					+ nowTime
					+ "'and ( pricePeriodEnd >= '"
					+ nowTime
					+ "' or pricePeriodEnd is null  or pricePeriodEnd = '')";
			// + str;
			priceList = totalDao.query(hql, wgplan.getMarkId(), wgplan
					.getProcessNOs(), wgplan.getWwType());
		}
		List<Price> priceList2 = new ArrayList<Price>();
		if (priceList != null && priceList.size() > 0) {
			for (Price price : priceList) {
				if (price.getGysId() != null) {
					ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
							.getGysId());
					if (zhuser != null) {
						price.setGys(zhuser.getCmp());
						priceList2.add(price);
					}
				}
			}
		}
		return priceList2;
	}

	public WaigouDeliveryDetail findWaigouPlanById_2(Integer id) {
		if (id != null) {
			return (WaigouDeliveryDetail) totalDao.getObjectByCondition(
					" from WaigouDeliveryDetail where type = '辅料' and id = ? ",
					id);
		}
		return null;
	}

	@Override
	public List<Procard> showWgProcardList(Integer rootId, String markId,
			String kgliao, String banben, String specification) {
		String hql = " from Procard where rootId = ? and markId = ? and kgliao=? ";
		if (banben != null && banben.length() > 0) {
			hql += " and banBenNumber = '" + banben;
		} else {
			hql += " and ( banBenNumber = '' or banBenNumber is null )";
		}
		return totalDao.query(hql, rootId, markId, kgliao);
	}

	private void mopbili(ManualOrderPlan nowmop) {
		// 判断是否拥有单张用量
		String hql_ycl = " select bili  from YuanclAndWaigj where markId =? and kgliao =? and "
				+ " (banbenStatus is null or banbenStatus <> '历史') and bili is not null and  bili >0 ";
		if (nowmop.getBanben() != null && nowmop.getBanben().length() > 0) {
			hql_ycl += " and banbenhao = '" + nowmop.getBanben() + "'";
		} else {
			hql_ycl += " and (banbenhao is null or banbenhao = '' ) ";
		}
		if (nowmop.getSpecification() != null
				&& nowmop.getSpecification().length() > 0) {
			hql_ycl += " and specification = '" + nowmop.getSpecification()
					+ "'";
		} else {
			hql_ycl += " and ( specification is null or specification = '')";
		}
		Float bili = (Float) totalDao.getObjectByCondition(hql_ycl, nowmop
				.getMarkId(), nowmop.getKgliao());
		Float number = nowmop.getNumber()
				- (nowmop.getOutcgNumber() == null ? 0 : nowmop
						.getOutcgNumber());
		if (bili != null && bili > 0) {
			double num = (number) / bili;
			BigDecimal bg = new BigDecimal(num);
			double f1 = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			double beishu = (double) Math.ceil(f1);
			double f2 = 1d - (beishu - f1);
			if (f2 <= 0.005) {
				beishu = beishu - 1;
			}
			nowmop.setNumber(Float.parseFloat(String.format("%.4f", beishu
					* bili)));
		}
	}

	@Override
	public void saveWgProcardMOQ(List<WgProcardMOQ> wgmoqList) {
		if (wgmoqList != null && wgmoqList.size() > 0) {
			for (WgProcardMOQ wgProcardMOQ : wgmoqList) {
				totalDao.save(wgProcardMOQ);
			}
		}

	}

	@Override
	public WgProcardMOQ findWgMOQOne(Integer rootId, String markId,
			String kgliao, String specification, String banBenNumber) {
		String hql = " from WgProcardMOQ where rootId=? and markId=? and kgliao =? ";
		if (specification != null && specification.length() > 0) {
			hql += " and specification = '" + specification + "'";
		} else {
			hql += " and (specification = '' or specification is null)";
		}
		if (banBenNumber != null && banBenNumber.length() > 0) {
			hql += " and banBenNumber '" + banBenNumber + "'";
		} else {
			hql += " and ( banBenNumber = '' or banBenNumber is null)";
		}
		return (WgProcardMOQ) totalDao
				.getObjectByCondition(hql, rootId, markId);
	}

	@Override
	public List<ZhUser> findZhuser(WgProcardMOQ wgmoq, String gysCodeAndNum) {
		if (wgmoq != null && gysCodeAndNum != null
				&& gysCodeAndNum.length() > 0) {
			String[] str = gysCodeAndNum.split(";");
			List<ZhUser> zhuserList = new ArrayList<ZhUser>();
			if (str != null && str.length > 0) {
				Float cgNum = wgmoq.getCgNumber();
				for (int i = (str.length - 1); i >= 0; i--) {
					String[] codeAndNums = str[i].split(":");
					Float num = Float.parseFloat(codeAndNums[1]);
					ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
							" from ZhUser where usercode = ?", codeAndNums[0]);
					String hql_gys = " from GysMarkIdjiepai where markId = ? and  zhuserId = ?  and kgliao = ?";
					if (wgmoq.getBanBenNumber() != null
							&& wgmoq.getBanBenNumber().length() > 0) {
						hql_gys += " and  banBenNumber = '"
								+ wgmoq.getBanBenNumber() + "' ";
					} else {
						hql_gys += " and (banBenNumber is null or  banBenNumber = '')";
					}
					GysMarkIdjiepai gys = (GysMarkIdjiepai) totalDao
							.getObjectByCondition(hql_gys, wgmoq.getMarkId(),
									zhuser.getId(), wgmoq.getKgliao());
					zhuser.setMoqNum(num);
					zhuser.setBilifpNum(cgNum * (gys.getCgbl() / 100));
					zhuserList.add(zhuser);
				}
			}
			return zhuserList;
		}
		return null;

	}

	public WaigouPlan findwaiddfordId(Integer id) {
		WaigouDailySheet st = (WaigouDailySheet) totalDao.get(
				WaigouDailySheet.class, id);
		WaigouDeliveryDetail wd = (WaigouDeliveryDetail) totalDao.get(
				WaigouDeliveryDetail.class, st.getWgddId());
		WaigouPlan wg = (WaigouPlan) totalDao.get(WaigouPlan.class, wd
				.getWaigouPlanId());
		return wg;
	}

	@Override
	public List findGyswwPlantz(Integer id, String showsonTz) {
		// TODO Auto-generated method stub
		WaigouPlan waigouPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id);
		if (waigouPlan == null) {
			return null;
		}
		List<ProcessTemplateFile> rfileList = new ArrayList<ProcessTemplateFile>();
		String banciSql = "";
		if (waigouPlan.getType().equals("外委")) {
			Procard procard = (Procard) totalDao
					.getObjectByCondition(
							"from Procard where id in(select procardId from ProcardWGCenter where wgOrderId=?)",
							waigouPlan.getId());
			// String banbenSql = null;
			// if (procard.getBanBenNumber() == null
			// || procard.getBanBenNumber().length() == 0) {
			// banbenSql = " and (banBenNumber is null or banBenNumber='') ";
			// } else {
			// banbenSql = " and banBenNumber ='" + procard.getBanBenNumber()
			// + "' ";
			// }
			// 获取本层工序的图纸
			if (!waigouPlan.getWwType().equals("包工包料")
					|| (waigouPlan.getWwType().equals("包工包料") && "noshow"
							.equals(showsonTz))) {
				String[] processNos = waigouPlan.getProcessNOs().split(";");
				StringBuffer sb = new StringBuffer();
				for (String processno : processNos) {
					if (sb.length() == 0) {
						sb.append(processno);
					} else {
						sb.append("," + processno);
					}
				}
				if (procard.getBanci() == null || procard.getBanci() == 0) {
					banciSql = " and (banci is null or banci = 0)";
				} else {
					banciSql = " and banci is not null and banci = "
							+ procard.getBanci();
				}
				List<ProcessTemplateFile> filelist = null;
				if (procard.getProcardTemplateId() != null) {
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, procard
									.getProcardTemplateId());
					if (pt != null) {
						if (pt.getProductStyle().equals("试制")) {
							// filelist = totalDao
							// .query(
							// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO in("
							// + sb.toString()
							// +
							// ") and productStyle=? and (glId in(select id from ProcessTemplate where procardTemplate.id=?))"
							// + banciSql
							// + "group by oldfileName)",
							// procard.getMarkId(), procard
							// .getProductStyle(), pt.getId());// 千万别加工序的删除判定
							List<Integer> maxIdList = totalDao
									.query(
											"select max(id) from ProcessTemplateFile where markId=? and processNO is null"
													+ " and productStyle=? and glId =? and type in('工艺规范','3D文件','其他文件','标签文件')"
													+ banciSql
													+ "group by oldfileName",
											procard.getMarkId(), procard
													.getProductStyle(), pt
													.getId());
							if (maxIdList != null && maxIdList.size() > 0) {
								StringBuffer maxidsb = new StringBuffer();
								for (Integer maxId : maxIdList) {
									if (maxidsb.length() == 0) {
										maxidsb.append(maxId);
									} else {
										maxidsb.append("," + maxId);
									}
								}
								filelist = totalDao
										.query("from ProcessTemplateFile where id in ("
												+ maxidsb.toString() + ")");
							}
						} else if (pt.getProductStyle().equals("批产")) {
							// filelist = totalDao
							// .query(
							// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO in("
							// + sb.toString()
							// + ") and productStyle=? "
							// + banciSql
							// + " group by oldfileName)",
							// procard.getMarkId(), procard
							// .getProductStyle());
							List<Integer> maxIdList = totalDao
									.query(
											"select max(id) from ProcessTemplateFile where markId=? and processNO is null"
													+ " and productStyle=? and type in('工艺规范','3D文件','其他文件','标签文件') "
													+ banciSql
													+ " group by oldfileName",
											procard.getMarkId(), procard
													.getProductStyle());
							if (maxIdList != null && maxIdList.size() > 0) {
								StringBuffer maxidsb = new StringBuffer();
								for (Integer maxId : maxIdList) {
									if (maxidsb.length() == 0) {
										maxidsb.append(maxId);
									} else {
										maxidsb.append("," + maxId);
									}
								}
								filelist = totalDao
										.query("from ProcessTemplateFile where id in ("
												+ maxidsb.toString() + ")");
							}
						}

					}
				}
				if (filelist == null) {
					// filelist = totalDao
					// .query(
					// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO in("
					// + sb.toString()
					// + ") and productStyle=? "
					// + banciSql
					// + " group by oldfileName)", procard
					// .getMarkId(), procard.getProductStyle());
					List<Integer> maxIdList= totalDao.query("select max(id) from ProcessTemplateFile where markId=? and processNO is null"
											+ " and productStyle=? and type in('工艺规范','3D文件','其他文件','标签文件') "
											+ banciSql
											+ " group by oldfileName",  procard
							.getMarkId(), procard
							.getProductStyle());
	if(maxIdList!=null&&maxIdList.size()>0){
		StringBuffer maxidsb = new StringBuffer();
		for(Integer maxId:maxIdList){
			if(maxidsb.length()==0){
				maxidsb.append(maxId);
			}else{
				maxidsb.append(","+maxId);
			}
		}
		filelist=totalDao
		.query(
				"from ProcessTemplateFile where id in ("+maxidsb.toString()+")");
	}
				}
				if (filelist != null && filelist.size() > 0) {
					for (ProcessTemplateFile file : filelist) {

						if (file.getProcessName() != null
								&& !file.getOldfileName().contains(
										"[" + file.getProcessName() + "]")) {
							file.setOldfileName("[" + file.getProcessName()
									+ "]" + file.getOldfileName());
						}
						rfileList.add(file);
					}
				}
			} else if (waigouPlan.getWwType().equals("包工包料")) {
				// 包工包料需要连带下层图纸
				if (procard != null) {
					if (!"noshow".equals(showsonTz)) {
						List<ProcessTemplateFile> fileList2 = getProcardTzWz2(procard);
						rfileList.addAll(fileList2);
					}
				}
			}
		} else {
			if (waigouPlan.getMopId() != null) {
				Procard procard = (Procard) totalDao
						.getObjectByCondition(
								" from Procard "
										+ " where id in (select procardId from ManualOrderPlanDetail where manualPlan.id =? )",
								waigouPlan.getMopId());
				if (procard != null) {
					waigouPlan.setBanci(procard.getBanci());
				}
			}
			if (waigouPlan.getBanci() == null || waigouPlan.getBanci() == 0) {
				banciSql = " and (banci is null or banci = 0)";
			} else {
				banciSql = " and banci is not null and banci = "
						+ waigouPlan.getBanci();
			}
			List<ProcessTemplateFile> filelist = null;
			List<Integer> maxIdList = totalDao.query(
					"select max(id) from ProcessTemplateFile where markId=? "
							+ banciSql + " group by oldfileName", waigouPlan
							.getMarkId());
			if (maxIdList != null && maxIdList.size() > 0) {
				StringBuffer maxidsb = new StringBuffer();
				for (Integer maxId : maxIdList) {
					if (maxidsb.length() == 0) {
						maxidsb.append(maxId);
					} else {
						maxidsb.append("," + maxId);
					}
				}
				filelist = totalDao
						.query("from ProcessTemplateFile where id in ("
								+ maxidsb.toString() + ")");
			}
			if (filelist != null && filelist.size() > 0) {
				for (ProcessTemplateFile file : filelist) {
					if (file.getProcessName() != null
							&& !file.getOldfileName().contains(
									"[" + file.getProcessName() + "]")) {
						file.setOldfileName("[" + file.getProcessName() + "]"
								+ file.getOldfileName());
					}
					rfileList.add(file);
				}
			}
		}
		return rfileList;
	}

	private List<ProcessTemplateFile> getProcardTzWz2(Procard procard) {
		// TODO Auto-generated method stub
		List<ProcessTemplateFile> fileList = new ArrayList<ProcessTemplateFile>();
		String banciSql = "";
		if (procard.getBanci() == null || procard.getBanci() == 0) {
			banciSql = " and (banci is null or banci = 0)";
		} else {
			banciSql = " and banci is not null and banci = "
					+ procard.getBanci();
		}
		List<ProcessTemplateFile> filelist = null;
		if (procard.getProcardTemplateId() != null) {
			ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, procard.getProcardTemplateId());
			if (pt != null) {
				if (pt.getProductStyle().equals("试制")) {
					// filelist = totalDao
					// .query(
					// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=? and processNO is not null and productStyle=? and (glId in(select id from ProcessTemplate where procardTemplate.id=?))"
					// +banbenSql+ banciSql
					// + " group by oldfileName)", procard
					// .getMarkId(), procard
					// .getProductStyle(), pt.getId());// 千万别加工序的删除判定
					filelist = totalDao
							.query("from ProcessTemplateFile where id in "
									+ "()");// 千万别加工序的删除判定
					List<Integer> maxIdList = totalDao
							.query(
									"select max(id) from ProcessTemplateFile where markId=? and processNO is  null and productStyle=? and glId=? and type in('工艺规范','3D文件','其他文件','标签文件')"
											+ banciSql
											+ " group by oldfileName", procard
											.getMarkId(), procard
											.getProductStyle(), pt.getId());
					if (maxIdList != null && maxIdList.size() > 0) {
						StringBuffer maxidsb = new StringBuffer();
						for (Integer maxId : maxIdList) {
							if (maxidsb.length() == 0) {
								maxidsb.append(maxId);
							} else {
								maxidsb.append("," + maxId);
							}
						}
						filelist = totalDao
								.query("from ProcessTemplateFile where id in ("
										+ maxidsb.toString() + ")");
					}
				} else if (pt.getProductStyle().equals("批产")) {
					// filelist = totalDao
					// .query(
					// "from ProcessTemplateFile where id in (select max(id) from ProcessTemplateFile where markId=?  and productStyle=? and type in('工艺规范','3D文件') "
					// +banbenSql+ banciSql
					// + " group by oldfileName)", procard
					// .getMarkId(), procard
					// .getProductStyle());
					List<Integer> maxIdList = totalDao
							.query(
									"select max(id) from ProcessTemplateFile where markId=?  and productStyle=?  and processNO is  null and type in('工艺规范','3D文件','其他文件','标签文件') "
											+ banciSql
											+ " group by oldfileName", procard
											.getMarkId(), procard
											.getProductStyle());
					if (maxIdList != null && maxIdList.size() > 0) {
						StringBuffer maxidsb = new StringBuffer();
						for (Integer maxId : maxIdList) {
							if (maxidsb.length() == 0) {
								maxidsb.append(maxId);
							} else {
								maxidsb.append("," + maxId);
							}
						}
						filelist = totalDao
								.query("from ProcessTemplateFile where id in ("
										+ maxidsb.toString() + ")");
					}
				}

			}
		}
		if (filelist == null) {
			// filelist = totalDao.query(
			// "from ProcessTemplateFile where markId=? and  productStyle=? "
			// +banbenSql+ banciSql, procard.getMarkId(), procard
			// .getProductStyle());
			List<Integer> maxIdList = totalDao
					.query(
							"select max(id) from ProcessTemplateFile where markId=? and  productStyle=?  and processNO is  null and type in('工艺规范','3D文件','其他文件','标签文件') "
									+ banciSql + " group by oldfileName",
							procard.getMarkId(), procard.getProductStyle());
			if (maxIdList != null && maxIdList.size() > 0) {
				StringBuffer maxidsb = new StringBuffer();
				for (Integer maxId : maxIdList) {
					if (maxidsb.length() == 0) {
						maxidsb.append(maxId);
					} else {
						maxidsb.append("," + maxId);
					}
				}
				filelist = totalDao
						.query("from ProcessTemplateFile where id in ("
								+ maxidsb.toString() + ")");
			}
		}

		if (filelist != null && filelist.size() > 0) {
			for (ProcessTemplateFile file : filelist) {
				if (file.getProcessName() != null
						&& !file.getOldfileName().contains(
								"(" + file.getProcessName() + ")")) {
					file.setOldfileName("(" + file.getProcessName() + ")"
							+ file.getOldfileName());
				}
				fileList.add(file);
			}
		}
		Set<Procard> sonSet = procard.getProcardSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (Procard son : sonSet) {
				List<ProcessTemplateFile> fileList2 = getProcardTzWz2(son);
				fileList.addAll(fileList2);
			}
		}
		return fileList;
	}

	/**
	 * 修改订单备注
	 * 
	 * @param waigouPlan
	 * @return
	 */
	public String updateWaigouPlan(WaigouPlan waigouPlan) {
		WaigouPlan waigouPlan_old = (WaigouPlan) totalDao.get(WaigouPlan.class,
				waigouPlan.getId());
		Users users = Util.getLoginUser();
		WaigouOrder waigouOrder = waigouPlan_old.getWaigouOrder();
		if (waigouOrder.getAddUserCode().equals(users.getCode())) {
			if (waigouPlan_old != null) {
				waigouPlan_old.setMore(waigouPlan.getMore());
				waigouPlan_old.setRemark(waigouPlan.getMore());
				if (totalDao.update(waigouPlan_old)) {
					return "修改成功";
				} else {
					return "修改失败";
				}
			} else {
				return "未找到原订单";
			}
		} else {
			return "只可以修改自己下的采购订单";
		}
	}

	@Override
	public String deleteWaiGouPlan(Integer id, String deltag) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!~";
		}
		String show = "";
		if ("del".equals(deltag)) {
			show = "删除";
		} else if ("close".equals(deltag)) {
			show = "关闭";
		} else if ("chong".equals(deltag)) {
			show = "重下";
		}
		if (id != null) {
			WaigouPlan waigouPlan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			if (waigouPlan != null) {
				if ("关闭".equals(waigouPlan.getStatus())
						|| "取消".equals(waigouPlan.getStatus())) {
					return "该订单已" + waigouPlan.getStatus() + "，请勿重复操作!~";
				}
				if (!user.getCode().equals(
						waigouPlan.getWaigouOrder().getAddUserCode())) {
					return "您不是此订单采购员，无法" + show + "该订单下的订单明细!~";
				}
				// 删除、关闭、重下都需要返回物料需求的已采购数量，不然会造成一直存在在途数量.
				ManualOrderPlan mop = (ManualOrderPlan) totalDao
						.getObjectByCondition(
								" from ManualOrderPlan where id = ?",
								waigouPlan.getMopId());
				if (mop != null) {
					Set<ManualOrderPlanDetail> modset = mop.getModSet();
					Float number = waigouPlan.getSyNumber();
					for (ManualOrderPlanDetail mod : modset) {
						if (number > 0) {
							if (mod.getOutcgNumber() == 0) {
								continue;
							}
							if (number >= mod.getOutcgNumber()) {
								number -= mod.getOutcgNumber();
								mod.setOutcgNumber(0f);
							} else {
								mod.setOutcgNumber(mod.getOutcgNumber()
										- number);
								number = 0f;
							}

						} else {
							mod.setOutcgNumber(0f);
						}
						totalDao.update(mod);
					}
					mop
							.setOutcgNumber((mop.getOutcgNumber() - waigouPlan
									.getSyNumber()) < 0 ? 0f : (mop
									.getOutcgNumber() - waigouPlan
									.getSyNumber()));
					Float qxNumber = (waigouPlan.getNumber() - waigouPlan
							.getSyNumber()) == 0 ? waigouPlan.getNumber()
							: (waigouPlan.getNumber() - waigouPlan
									.getSyNumber());
					if (mop.getWshCount() != null) {
						mop
								.setWshCount((mop.getWshCount() - qxNumber) <= 0 ? 0f
										: (mop.getWshCount() - qxNumber));
					}
					totalDao.update(mop);
					if (mop.getOutcgNumber() == 0) {
						String hql_mop = "from ManualOrderPlan where id <> ? and  outcgNumber =0 and markId =? and kgliao =?";
						if (mop.getBanben() != null
								&& mop.getBanben().length() > 0) {
							hql_mop += " and banben ='" + mop.getBanben() + "'";
						} else {
							hql_mop += " and (banben  is null and banben = '')";
						}
						ManualOrderPlan mop0 = (ManualOrderPlan) totalDao
								.getObjectByCondition(hql_mop, mop.getId(), mop
										.getMarkId(), mop.getKgliao());
						if (mop0 != null) {
							mop0.setNumber(mop0.getNumber() + mop.getNumber());
							modset.addAll(mop0.getModSet());
							mop0.setModSet(modset);
							totalDao.update(mop0);
							mop.setModSet(null);
							totalDao.delete(mop);
						}

					}
				}

				boolean bool = false;
				boolean bool1 = false;
				WaigouOrder wgorder = waigouPlan.getWaigouOrder();
				if ("del".equals(deltag)) {
					waigouPlan.setMore("订单关闭，重下:" + waigouPlan.getSyNumber()
							+ waigouPlan.getUnit());
					bool1 = true;
					// 更新订单总价;
					Double money = waigouPlan.getSyNumber()
							* waigouPlan.getHsPrice();
					if (wgorder.getAllMoney() != null) {
						Double nowAll=wgorder
						.getAllMoney()-money;
						nowAll = (double)Util.FomartDouble(nowAll, 4);
						wgorder.setAllMoney(nowAll);
					}
				}
				Set<WaigouPlan> wgplanSet = wgorder.getWwpSet();
				if (wgplanSet != null && wgplanSet.size() > 0) {
					if ("del".equals(deltag)) {
						wgplanSet.remove(waigouPlan);
						totalDao.delete(waigouPlan);
						if (wgplanSet.size() == 0) {
							wgorder.setWwpSet(wgplanSet);
							bool = totalDao.delete(wgorder);
						} else {
							wgorder.setWwpSet(wgplanSet);
							bool = totalDao.update(wgorder);
						}
					} else if ("close".equals(deltag) || "chong".equals(deltag)) {
						if (wgplanSet.size() == 1) {
							if (bool1)
								wgorder.setStatus("关闭");
							else {
								wgorder.setStatus("取消");
							}
						}
						if (bool1)
							waigouPlan.setStatus("关闭");
						else {
							waigouPlan.setStatus("取消");
						}
						waigouPlan.setSyNumber(0f);
						wgplanSet.add(waigouPlan);
						wgorder.setWwpSet(wgplanSet);
						bool = totalDao.update(wgorder);
					}
				}
				return bool + "";
			}
		}
		return "采购订单明细为空！";
	}

	@Override
	public String updateWaiGouPlan(Integer id, String deltag) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!~";
		}
		String show = "";
		if ("del".equals(deltag)) {
			show = "删除";
		} else if ("close".equals(deltag)) {
			show = "关闭";
		} else if ("chong".equals(deltag)) {
			show = "重下";
		}
		if (id != null) {
			WaigouPlan waigouPlan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					id);
			if (waigouPlan != null) {
				if (!user.getCode().equals(
						waigouPlan.getWaigouOrder().getAddUserCode())) {
					return "您不是此订单采购员，无法" + show + "该订单下的订单明细!~";
				}
				boolean bool = false;
				WaigouOrder wgorder = waigouPlan.getWaigouOrder();
				if ("del".equals(deltag) || "chong".equals(deltag)) {
					// 更新物料需求已采购数量，使得可以继续采购
					ManualOrderPlan mop = (ManualOrderPlan) totalDao
							.getObjectByCondition(
									" from ManualOrderPlan where id = ?",
									waigouPlan.getMopId());
					Set<ManualOrderPlanDetail> modset = mop.getModSet();
					Float number = waigouPlan.getNumber();
					for (ManualOrderPlanDetail mod : modset) {
						if (number > 0) {
							if (number >= mod.getOutcgNumber()) {
								number -= mod.getOutcgNumber();
								mod.setOutcgNumber(0f);
							} else if (number < mod.getOutcgNumber()) {
								mod.setOutcgNumber(mod.getOutcgNumber()
										- number);
								number = 0f;
							}
							totalDao.update(mod);
						} else {
							break;
						}
					}
					mop.setOutcgNumber(mop.getOutcgNumber()
							- waigouPlan.getNumber());
					totalDao.update(mop);
					// 更新订单总价;
					if (wgorder.getAllMoney() != null) {
						Double nowAll = wgorder
						.getAllMoney()- waigouPlan.getMoney();
						nowAll = (double) Util.FomartDouble(nowAll, 4);
						wgorder.setAllMoney(nowAll);
					}
				}
				Set<WaigouPlan> wgplanSet = wgorder.getWwpSet();
				if (wgplanSet != null && wgplanSet.size() > 0) {
					if ("del".equals(deltag)) {
						wgplanSet.remove(waigouPlan);
						if (wgplanSet.size() == 0) {
							bool = totalDao.delete(wgorder);
						} else {
							wgorder.setWwpSet(wgplanSet);
							bool = totalDao.update(wgorder);
						}
					} else if ("close".equals(deltag) || "chong".equals(deltag)) {
						if (wgplanSet.size() == 1) {
							wgorder.setStatus("取消");
						}
						waigouPlan.setStatus("取消");
						waigouPlan.setSyNumber(0f);
						wgplanSet.add(waigouPlan);
						wgorder.setWwpSet(wgplanSet);
						bool = totalDao.update(wgorder);
					}
				}
				return bool + "";
			}
		}
		return "采购订单明细为空！";
	}

	@Override
	public String djkOut() {
		String hql = " from Goods where goodsClass = '待检库' and goodsCurQuantity> 0 and "
				+ " goodsChangeTime >= '2018-01-01' and goodsMarkId not in ( "
				+ " SELECT  markId from  WaigouDeliveryDetail where status <> '入库' )";
		List<Goods> goodsList = totalDao.query(hql);
		if (goodsList != null && goodsList.size() > 0) {
			for (Goods s : goodsList) {
				s.setGoodsCurQuantity(0f);
				Sell sell = new Sell();
				sell.setSellMarkId(s.getGoodsMarkId());// 件号
				sell.setSellWarehouse(s.getGoodsClass());// 库别
				sell.setGoodHouseName(s.getGoodHouseName());// 仓区
				sell.setKuwei(s.getGoodsPosition());// 库位
				sell.setBanBenNumber(s.getBanBenNumber());// 版本号
				sell.setKgliao(s.getKgliao());// 供料属性
				sell.setWgType(s.getWgType());// 物料类别
				sell.setSellGoods(s.getGoodsFullName());// 品名
				sell.setSellFormat(s.getGoodsFormat());// 规格
				sell.setSellCount(s.getGoodsCurQuantity());// 出库数量
				sell.setSellUnit(s.getGoodsUnit());// 单位
				sell.setSellSupplier(s.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setTuhao(s.getTuhao());// 图号
				sell.setBedit(false);// 出库权限
				sell.setBedit(false);// 编辑权限
				sell.setSellZhishu(s.getGoodsZhishu());
				sell.setPrintStatus("YES");// 打印状态
				sell.setStyle("待检库出库");
				totalDao.save(sell);
				totalDao.update(s);
			}
		}

		return null;
	}

	@Override
	public List findbomweiwaiwei() {
		// TODO Auto-generated method stub
		List<Procard> procardlist = totalDao
				.query("from Procard where status in('已发卡','已发料','领工序') and id in(select procard.id from ProcessInfor where productStyle='外委' and status !='完成')");
		List<String> rlist = new ArrayList<String>();
		int i = 0;
		for (Procard procard : procardlist) {
			Set<ProcessInfor> processinforSet = procard.getProcessInforSet();
			if (processinforSet != null && processinforSet.size() > 0) {
				ProcessInfor up = null;
				for (ProcessInfor process : processinforSet) {
					if (process != null
							&& process.getProductStyle().equals("外委")) {
						if (up != null && up.getProductStyle().equals("自制")) {
							if (up.getSubmmitCount() > 0
									&& up.getSubmmitCount() > process
											.getSubmmitCount()) {
								if (up.getSubmmitCount() == 1
										&& procard.getFilnalCount() > 1) {
									continue;
								}
								Float scwwcount = (Float) totalDao
										.getObjectByCondition(
												"select sum(beginCount) from WaigouWaiweiPlan where procardId=? and (processNo=? or processNo like '"
														+ process
																.getProcessNO()
														+ ";%')", procard
														.getId(), process
														.getProcessNO()
														+ "");
								if (scwwcount < up.getSubmmitCount()) {
									rlist
											.add(procard.getMarkId()
													+ ";"
													+ procard.getSelfCard()
													+ ";"
													+ process.getProcessNO()
													+ ";"
													+ (up.getSubmmitCount() - scwwcount));
									i++;
									System.out.println(i);
								}
							}
						}
					}
					if (process.getSubmmitCount() == 0) {
						break;
					}
					up = process;
				}
			}
		}
		return rlist;
	}

	@Override
	public List addbomweiwaiwei() {
		List<Procard> procardlist = totalDao
				.query("from "
						+ "Procard where markId in('DKBA6.151.4555')"
						+ "and status in('已发卡','已发料','领工序') and "
						+ "id in(select procard.id from ProcessInfor where productStyle='外委' and status !='完成')");
		List<String> rlist = new ArrayList<String>();
		for (Procard procard : procardlist) {
			System.out.println(procard.getMarkId());
			List<ProcessInfor> processinforList = totalDao
					.query(
							"from ProcessInfor where"
									+ "(dataStatus is null or dataStatus!='删除') and procard.id=? order by processNO",
							procard.getId());
			if (processinforList != null && processinforList.size() > 0) {
				ProcessInfor up = null;
				for (int i = 0; i < processinforList.size(); i++) {
					ProcessInfor process = processinforList.get(i);
					if (process != null
							&& process.getProductStyle().equals("外委")) {
						if (up == null
								|| (up != null && up.getProductStyle().equals(
										"自制"))) {
							if (up == null
									|| (up.getSubmmitCount() > 0 && up
											.getSubmmitCount() > process
											.getSubmmitCount())) {
								if (up != null && up.getSubmmitCount() == 1
										&& procard.getFilnalCount() > 1) {
									continue;
								}
								Float applyCount = 0f;
								List<ProcessInforWWProcard> processInforWWProcardList = new ArrayList<ProcessInforWWProcard>();
								if (up != null) {
									applyCount = up.getSubmmitCount();
								} else {
									applyCount = procard.getFilnalCount();
								}
								Float scwwcount = (Float) totalDao
										.getObjectByCondition(
												"select sum(beginCount) from WaigouWaiweiPlan where procardId=? and (processNo=? or processNo like '"
														+ process
																.getProcessNO()
														+ ";%')", procard
														.getId(), process
														.getProcessNO()
														+ "");
								if (scwwcount < applyCount) {
									Float addCount = applyCount - scwwcount;
									List<ProcessInforWWProcard> pwwpList = null;
									if (up == null) {
										// 首工序外委获取关联零件
										List<String> xcmarkIdList = totalDao
												.query(
														"select distinct(wgprocardMardkId) from ProcessAndWgProcardTem where procardMarkId=? and processName=?",
														procard.getMarkId(),
														process
																.getProcessName());
										StringBuffer xcmarkIdsb = new StringBuffer();
										if (xcmarkIdList == null
												|| xcmarkIdList.size() == 0) {
											rlist.add(procard.getMarkId() + ":"
													+ procard.getSelfCard()
													+ "首工序外委无下层关联零件!");
											break;
										} else {
											for (String xcmarkId : xcmarkIdList) {
												if (xcmarkIdsb.length() == 0) {
													xcmarkIdsb.append("('"
															+ xcmarkId + "'");
												} else {
													xcmarkIdsb.append(",'"
															+ xcmarkId + "'");
												}
											}
											xcmarkIdsb.append(")");
										}
										List<Procard> sonProcardList = totalDao
												.query(
														"from Procard where procard.id=? and (sbStatus is null or sbStatus!='删除') and markId in "
																+ xcmarkIdsb
																		.toString(),
														procard.getId());
										if (sonProcardList != null
												&& sonProcardList.size() > 0) {
											pwwpList = new ArrayList<ProcessInforWWProcard>();
											for (Procard son : sonProcardList) {
												ProcessInforWWProcard processwwprocard = new ProcessInforWWProcard();
												processwwprocard
														.setProcardId(son
																.getId());// 零件id
												processwwprocard.setMarkId(son
														.getMarkId());// 件号
												processwwprocard
														.setProcName(son
																.getProName());// 名称
												processwwprocard.setBanben(son
														.getBanBenNumber());// 版本号
												processwwprocard.setBanci(son
														.getBanci());// 版次
												processwwprocard
														.setStatus("使用");// 状态
												Float glCount = 0f;
												if (son.getProcardStyle()
														.equals("自制")) {
													glCount = addCount
															* son
																	.getCorrCount();
													processwwprocard
															.setProcardStyle("生产");// 生产或者外购(生产包括自制和外购半成品)
												} else {
													glCount = addCount
															* son.getQuanzi2()
															/ son.getQuanzi1();
													if (son.getNeedProcess() != null
															&& son
																	.getNeedProcess()
																	.equals(
																			"yes")) {
														processwwprocard
																.setProcardStyle("生产");// 生产或者外购(生产包括自制和外购半成品)
													} else {
														processwwprocard
																.setProcardStyle("外购");// 生产或者外购(生产包括自制和外购半成品)
													}
												}
												processwwprocard
														.setApplyCount(glCount);// 数量
												processwwprocard
														.setAddTime(Util
																.getDateTime());
												processwwprocard
														.setHascount(glCount);// 剩余未领数量（工序外委使用）
												pwwpList.add(processwwprocard);
											}
										} else {
											rlist.add(procard.getMarkId() + ":"
													+ procard.getSelfCard()
													+ "首工序外委下层关联的零件不存在!");
											break;
										}
									}
									int n = 0;
									WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
									for (int j = i; j < processinforList.size(); j++) {
										ProcessInfor nextWwProcessInfor = processinforList
												.get(j);
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
													wwp.setYwMarkId(procard
															.getYwMarkId());
													wwp.setBanben(procard
															.getBanBenNumber());
													wwp.setBanci(procard
															.getBanci());
													wwp.setMarkId(procard
															.getMarkId());
													wwp
															.setProcessNo(nextWwProcessInfor
																	.getProcessNO()
																	+ "");
													wwp.setProName(procard
															.getProName());
													wwp
															.setProcessName(nextWwProcessInfor
																	.getProcessName());
													wwp.setType("外委");
													Float wwCount = 0f;
													wwp.setNumber(addCount);
													wwp.setBeginCount(addCount);
													wwCount = process
															.getSubmmitCount();
													wwp.setAddTime(Util
															.getDateTime());
													wwp.setJihuoTime(Util
															.getDateTime());
													wwp.setUnit(procard
															.getUnit());
													wwp
															.setShArrivalTime(procard
																	.getNeedFinalDate());// 应到货时间在采购确认通知后计算
													wwp
															.setCaigouMonth(Util
																	.getDateTime("yyyy-MM月"));// 采购月份
													String wwNumber = "";
													String before = null;
													Integer bIndex = 10;
													before = "ww"
															+ Util
																	.getDateTime("yyyyMMdd");
													Integer maxNo = 0;
													String maxNumber = (String) totalDao
															.getObjectByCondition("select max(planNumber) from WaigouWaiweiPlan where planNumber like '"
																	+ before
																	+ "%'");
													if (maxNumber != null) {
														String wwnum = maxNumber
																.substring(
																		bIndex,
																		maxNumber
																				.length());
														try {
															Integer maxNum = Integer
																	.parseInt(wwnum);
															if (maxNum > maxNo) {
																maxNo = maxNum;
															}
														} catch (Exception e) {
															// TODO: handle
															// exception
														}
													}
													maxNo++;
													wwNumber = before
															+ String.format(
																	"%03d",
																	maxNo);
													wwp.setPlanNumber(wwNumber);// 采购计划编号
													wwp.setSelfCard(procard
															.getSelfCard());// 批次
													// wwp.setGysId(nextWwProcessInfor
													// .getZhuserId());// 供应商id
													// wwp.setGysName(nextWwProcessInfor
													// .getGys());// 供应商名称
													wwp
															.setAllJiepai(nextWwProcessInfor
																	.getAllJiepai());// 单件总节拍
													wwp
															.setDeliveryDuration(nextWwProcessInfor
																	.getDeliveryDuration());// 耽误时长
													wwp
															.setSingleDuration(procard
																	.getSingleDuration());// 单班时长(工作时长)
													wwp.setProcardId(procard
															.getId());
													wwp.setProcard(procard);
													// if (wwckCount != null
													// && wwckCount > 0) {
													if (up == null) {
														wwp.setStatus("待激活");
													} else {
														wwp.setStatus("待入库");
														// 在制品待入库
														if (procard
																.getZaizhiApplyZk() == null) {
															procard
																	.setZaizhiApplyZk(0f);
														}
														if (procard
																.getZaizhizkCount() == null) {
															procard
																	.setZaizhizkCount(0f);
														}
														if (procard
																.getKlNumber() == null) {
															procard
																	.setKlNumber(procard
																			.getFilnalCount());
														}
														if (procard
																.getHascount() == null) {
															procard
																	.setHascount(procard
																			.getKlNumber());
														}
														// procard.getKlNumber()-procard.getHascount()=已生产数量
														// 可转库数量=已生产数量-已转库数量-转库申请中数量
														procard
																.setZaizhikzkCount(procard
																		.getFilnalCount()
																		- procard
																				.getZaizhizkCount()
																		- procard
																				.getZaizhiApplyZk());
														// if (procard
														// .getZaizhikzkCount()
														// >=
														// wwCount) {
														procard
																.setZaizhiApplyZk(procard
																		.getZaizhiApplyZk()
																		+ wwCount);
														String orderNum = (String) totalDao
																.getObjectByCondition(
																		"select orderNumber from Procard where id=?",
																		procard
																				.getRootId());
														// 成品待入库
														GoodsStore goodsStore2 = new GoodsStore();
														goodsStore2
																.setNeiorderId(orderNum);
														goodsStore2
																.setWaiorderId(procard
																		.getOutOrderNum());
														goodsStore2
																.setProcardId(procard
																		.getId());
														goodsStore2
																.setGoodsStoreMarkId(procard
																		.getMarkId());
														goodsStore2
																.setBanBenNumber(procard
																		.getBanBenNumber());
														goodsStore2
																.setGoodsStoreLot(procard
																		.getSelfCard());
														goodsStore2
																.setGoodsStoreGoodsName(procard
																		.getProName());
														goodsStore2
																.setApplyTime(Util
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
														goodsStore2
																.setStatus("待入库");
														goodsStore2
																.setStyle("半成品转库");
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
														goodsStore2
																.setProcessNo(process
																		.getProcessNO());
														goodsStore2
																.setProcessName(process
																		.getProcessName());
														totalDao
																.update(procard);
														totalDao
																.save(goodsStore2);
														// 判断外委进委外入库是否要做
														String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='委外库接收半成品' and valueName='委外库接收半成品'";
														String valueCode = (String) totalDao
																.getObjectByCondition(hql1);
														if (valueCode != null
																&& valueCode
																		.equals("否")) {
															// 入库记录直接通过
															goodsStore2
																	.setStatus("入库");
															goodsStore2
																	.setPrintStatus("YES");
															totalDao
																	.update(goodsStore2);
															// 增加库存记录
															String hqlgoods = "from Goods where goodsMarkId='"
																	+ procard
																			.getMarkId()
																	+ "' and goodsLotId='"
																	+ procard
																			.getSelfCard()
																	+ "' and goodsStyle='半成品转库' and processNo="
																	+ process
																			.getProcessNO()
																	+ " and goodsClass='委外库'";
															Goods goods = (Goods) totalDao
																	.getObjectByCondition(hqlgoods);
															if (goods != null) {
																goods
																		.setGoodsCurQuantity(goods
																				.getGoodsCurQuantity()
																				+ goodsStore2
																						.getGoodsStoreCount());
																totalDao
																		.update(goods);
															} else {
																goods = new Goods();
																goods
																		.setGoodsMarkId(goodsStore2
																				.getGoodsStoreMarkId());
																goods
																		.setGoodsFormat(goodsStore2
																				.getGoodsStoreFormat());
																goods
																		.setBanBenNumber(goodsStore2
																				.getBanBenNumber());
																goods
																		.setGoodsFullName(goodsStore2
																				.getGoodsStoreGoodsName());
																goods
																		.setGoodsClass("委外库");
																goods
																		.setGoodsBeginQuantity(goodsStore2
																				.getGoodsStoreCount());
																goods
																		.setGoodsCurQuantity(goodsStore2
																				.getGoodsStoreCount());
																totalDao
																		.save(goods);
															}
															// 添加零件与在制品关系表
															ProcardProductRelation pprelation = new ProcardProductRelation();
															pprelation
																	.setAddTime(Util
																			.getDateTime());
															pprelation
																	.setProcardId(procard
																			.getId());
															pprelation
																	.setGoodsId(goods
																			.getGoodsId());
															pprelation
																	.setZyCount(goodsStore2
																			.getGoodsStoreCount());
															pprelation
																	.setFlagType("本批在制品");
															totalDao
																	.save(pprelation);
															// 将外购外委激活序列状态改为待激活
															wwp
																	.setStatus("待激活");
															// totalDao.save(wwp);
														}
													}
													// } else {
													// return "对不起超过可申请数量("
													// + procard
													// .getZaizhikzkCount()
													// + ")";
													// }
													// } else {
													// wwp.setStatus("待激活");
													// }
													totalDao.save(wwp);
													if (pwwpList != null
															&& pwwpList.size() > 0) {
														for (ProcessInforWWProcard pwwp : pwwpList) {
															pwwp.setWwxlId(wwp
																	.getId());
															totalDao.save(pwwp);
														}
													}
													if (up != null) {
														for (ProcessInforWWProcard processInforWWProcard : processInforWWProcardList) {
															processInforWWProcard
																	.setWwxlId(wwp
																			.getId());// 外委序列ID(WaigouWaiweiPlan的id)(BOM外委)
															totalDao
																	.save(processInforWWProcard);
														}
													}
													n++;
													// wgSet.add(wwp);
												} else {
													wwp
															.setProcessNo(wwp
																	.getProcessNo()
																	+ ";"
																	+ nextWwProcessInfor
																			.getProcessNO());
													wwp
															.setProcessName(wwp
																	.getProcessName()
																	+ ";"
																	+ nextWwProcessInfor
																			.getProcessName());
													totalDao.update(wwp);
												}
												i = j;
											} else {
												i = j;
												break;
											}
										} else {
											i = j;
											break;
										}
									}
									if (wwp.getId() != null) {
										// 匹配供应商
										Price price = (Price) totalDao
												.getObjectByCondition(
														"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
																+ Util
																		.getDateTime()
																+ "')  order by hsPrice",
														wwp.getMarkId(), wwp
																.getProcessNo());
										if (price != null) {
											wwp.setPriceId(price.getId());
											wwp.setGysId(price.getGysId());
											ZhUser zhUser = (ZhUser) totalDao
													.getObjectById(
															ZhUser.class, price
																	.getGysId());
											wwp.setGysName(zhUser.getName());
											wwp.setUserCode(zhUser
													.getUsercode());
											wwp.setUserId(zhUser.getUserid());
											totalDao.update(wwp);
										}
									}
									if (wwp.getStatus() != null
											&& wwp.getStatus().equals("待激活")) {// 说明自动跳过了半成品入委外库操作
										// 下一步操作
										procardServer.zijihuoww(wwp);
									}

								}
							}

						}
					}
					if (process.getSubmmitCount() == 0) {
						break;
					}
					up = process;
				}
			}
		}
		return rlist;
	}

	// @Override
	// public String closeWaigouPlan(Integer id) {
	// Users user = Util.getLoginUser();
	// if(user == null){
	// return "请先登录!~";
	// }
	// if(id!=null){
	// WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class, id);
	// if(!user.getCode().equals(waigouplan.getWaigouOrder().getAddUserCode())){
	// return "您不是此订单的采购员无法关闭该订单";
	// }
	// waigouplan.setGbNum(waigouplan.getSyNumber());
	// waigouplan.setSyNumber(0f);
	// totalDao.update(waigouplan);
	// }
	// return null;
	// }
	// 待检库出库 如有不合格数量大于0时 不合格品入到不合格品库
	private void djkOutAndBhgIn(Float bhegeNumber, WaigouDeliveryDetail wgdd,
			Float bili, DefectiveProduct dp) {
		Users user = Util.getLoginUser();
		String hql_gt = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
		GoodsStore t = (GoodsStore) totalDao.getObjectByCondition(hql_gt, wgdd
				.getId(), wgdd.getMarkId(), "待检库");
		// 待检库出库入相应库别
		if (t != null) {
			String hql = "from Goods where goodsMarkId = ? and goodsClass = ? and goodsCurQuantity >0";
			if (t.getGoodHouseName() != null
					&& t.getGoodHouseName().length() > 0) {
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
			if (t.getGoodsStoreLot() != null
					&& t.getGoodsStoreLot().length() > 0) {
				hql += " and goodsLotId='" + t.getGoodsStoreLot() + "'";
			}
			Goods s = (Goods) totalDao.getObjectByCondition(hql, new Object[] {
					t.getGoodsStoreMarkId(), "待检库" });
			if (s != null) {
				s.setGoodsCurQuantity(0f);
				s.setGoodsZhishu(0f);
				// 合格品出库 添加出库记录
				Sell sell = new Sell();
				sell.setSellMarkId(s.getGoodsMarkId());// 件号
				sell.setSellWarehouse("待检库");// 库别
				sell.setGoodHouseName("待检仓");// 仓区
				sell.setKuwei(s.getGoodsPosition());// 库位
				sell.setBanBenNumber(s.getBanBenNumber());// 版本号
				sell.setKgliao(s.getKgliao());// 供料属性
				sell.setWgType(s.getWgType());// 物料类别
				sell.setSellGoods(s.getGoodsFullName());// 品名
				sell.setSellFormat(s.getGoodsFormat());// 规格
				sell.setSellCount(s.getGoodsCurQuantity());// 出库数量
				sell.setSellUnit(s.getGoodsUnit());// 单位
				sell.setSellSupplier(s.getGoodsSupplier());// 供应商
				sell.setSellTime(Util.getDateTime());// 出库时间
				sell.setYwmarkId(s.getYwmarkId());// 业务件号
				sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
				sell.setOrderNum(s.getNeiorderId());// 内部订单号
				sell.setTuhao(s.getTuhao());// 图号
				sell.setBedit(false);// 出库权限
				sell.setBedit(false);// 编辑权限
				sell.setPrintStatus("YES");// 打印状态
				sell.setStyle("待检库出库");
				sell.setSellPeople(user.getName());
				sell.setSellZhishu(s.getGoodsZhishu());
				totalDao.save(sell);
				totalDao.update(s);
				if (bhegeNumber > 0 && dp.getType().indexOf("来料不良") >= 0) {
					Goods hege_s = (Goods) totalDao.getObjectByCondition(hql,
							new Object[] { t.getGoodsStoreMarkId(), "不合格品库" });
					if (hege_s != null) {
						GoodsStore gt = new GoodsStore();
						BeanUtils.copyProperties(t, gt, new String[] {
								"goodsStoreId", "goodsStoreWarehouse",
								"goodsStoreCount", "goodsStoreDate",
								"goodsStoreTime", "style" });
						gt.setGoodsStoreWarehouse("不合格品库");
						Float count = bhegeNumber
								- hege_s.getGoodsCurQuantity() < 0 ? 0f
								: bhegeNumber - hege_s.getGoodsCurQuantity();
						gt.setGoodsStoreCount(count);
						gt.setPrintStatus("YES");
						gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
						gt.setStyle("不合格品入库");
						gt.setYwmarkId(hege_s.getYwmarkId());// 业务件号
						gt.setNeiorderId(hege_s.getNeiorderId());// 内部订单号
						gt.setWaiorderId(hege_s.getWaiorderId());// 外部订单号
						gt.setGoodsStoreLot(wgdd.getExamineLot());
						gt.setGoodsStoreTime(Util.getDateTime());
						if (bili != null && bili > 0) {
							gt.setGoodsStoreZhishu((float) Math.floor(count
									/ bili));
						}
						goodsStoreServer.saveSgrk(gt);
					} else {
						GoodsStore gt = new GoodsStore();
						BeanUtils.copyProperties(t, gt, new String[] {
								"goodsStoreId", "goodsStoreWarehouse",
								"goodsStoreCount", "goodsStoreDate",
								"goodsStoreTime", "style" });
						gt.setGoodsStoreWarehouse("不合格品库");
						gt.setGoodsStoreCount(bhegeNumber);
						gt.setPrintStatus("YES");
						gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
						gt.setStyle("不合格品入库");
						gt.setGoodsStoreLot(wgdd.getExamineLot());
						gt.setGoodsStoreTime(Util.getDateTime());
						gt.setYwmarkId(t.getYwmarkId());// 业务件号
						gt.setNeiorderId(t.getNeiorderId());// 内部订单号
						gt.setWaiorderId(t.getWaiorderId());// 外部订单号
						if (bili != null && bili > 0) {
							gt.setGoodsStoreZhishu((float) Math
									.floor(bhegeNumber / bili));
						}
						goodsStoreServer.saveSgrk(gt);
					}

				}

			}
		}
		if (bhegeNumber == 0 && dp.getType().indexOf("在库不良") < 0) {
			// 不合格品出库
			String hql_bhg = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
			GoodsStore bhgt = (GoodsStore) totalDao.getObjectByCondition(
					hql_bhg, wgdd.getId(), wgdd.getMarkId(), "不合格品库");
			if (bhgt != null) {
				String hql = "from Goods where goodsMarkId = ? and goodsClass = ? and goodsCurQuantity >0 and goodsLotId = ?";
				Goods s = (Goods) totalDao.getObjectByCondition(hql, bhgt
						.getGoodsStoreMarkId(), "不合格品库", bhgt
						.getGoodsStoreLot());
				if (s != null) {
					// 合格品出库 添加出库记录
					Float number = s.getGoodsCurQuantity();
					Sell sell = new Sell();
					sell.setSellMarkId(s.getGoodsMarkId());// 件号
					sell.setSellWarehouse("不合格品库");// 库别
					sell.setGoodHouseName("不合格品仓");// 仓区
					sell.setKuwei(s.getGoodsPosition());// 库位
					sell.setBanBenNumber(s.getBanBenNumber());// 版本号
					sell.setKgliao(s.getKgliao());// 供料属性
					sell.setWgType(s.getWgType());// 物料类别
					sell.setSellGoods(s.getGoodsFullName());// 品名
					sell.setSellFormat(s.getGoodsFormat());// 规格
					sell.setSellCount(s.getGoodsCurQuantity());// 出库数量
					sell.setSellUnit(s.getGoodsUnit());// 单位
					sell.setSellSupplier(s.getGoodsSupplier());// 供应商
					sell.setSellTime(Util.getDateTime());// 出库时间
					sell.setTuhao(s.getTuhao());// 图号
					sell.setBedit(false);// 出库权限
					sell.setBedit(false);// 编辑权限
					sell.setPrintStatus("YES");// 打印状态
					sell.setSellPeople(user.getName());
					sell.setStyle("合格品出库");
					sell.setYwmarkId(s.getYwmarkId());// 业务件号
					sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
					sell.setOrderNum(s.getNeiorderId());// 内部订单号
					sell.setSellZhishu(s.getGoodsZhishu());
					totalDao.save(sell);
					s.setGoodsCurQuantity(0f);
					s.setGoodsZhishu(0f);
					totalDao.update(s);
					// // 入到待检库
					// String kubie = "待检库";
					// GoodsStore gt = new GoodsStore();
					// BeanUtils.copyProperties(t, gt, new String[] {
					// "goodsStoreId", "goodsStoreWarehouse",
					// "goodsStoreCount", "goodsStoreDate",
					// "goodsStoreTime", "style" });
					// gt.setGoodsStoreWarehouse(kubie);
					// gt.setGoodsStoreCount(number);
					// gt.setPrintStatus("YES");
					// gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
					// gt.setStyle("合格品待检入库");
					// gt.setGoodsStoreLot(wgdd.getExamineLot());
					// gt.setGoodsStoreTime(Util.getDateTime());
					// gt.setGoodsStoreCharger(user.getName());
					// if (bili != null && bili > 0) {
					// gt.setGoodsStoreZhishu((float) Math.floor(number
					// / bili));
					// }
					// goodsStoreServer.saveSgrk(gt);
				}
			}

		}
		if ("外购在库不良".equals(dp.getType()) || "外委在库不良".equals(dp.getType())) {
			Float number = dp.getDbNumber() - bhegeNumber;
			if (number > 0) {// 在库不良 合格品入到在外购件库.
				String hql_bhg = " from  GoodsStore where wwddId=? and goodsStoreMarkId = ? and goodsStoreWarehouse = ?";
				GoodsStore bhggt = (GoodsStore) totalDao.getObjectByCondition(
						hql_bhg, wgdd.getId(), wgdd.getMarkId(), "不合格品库");
				if (bhggt != null) {
					String hql = "from Goods where goodsMarkId = ? and goodsClass = ? and goodsCurQuantity >0";
					if (bhggt.getGoodsStoreLot() != null
							&& bhggt.getGoodsStoreLot().length() > 0) {
						hql += " and goodsLotId='" + bhggt.getGoodsStoreLot()
								+ "'";
					}
					Goods s = (Goods) totalDao.getObjectByCondition(hql,
							new Object[] { t.getGoodsStoreMarkId(), "不合格品库" });
					if (s != null) {
						s.setGoodsCurQuantity(s.getGoodsCurQuantity() - number);
						// 不合格品出库 添加出库记录
						Sell sell = new Sell();
						sell.setSellMarkId(s.getGoodsMarkId());// 件号
						sell.setSellLot(s.getGoodsLotId());//批次
						sell.setSellWarehouse("不合格品库");// 库别
						sell.setGoodHouseName("不合格品仓");// 仓区
						sell.setKuwei(s.getGoodsPosition());// 库位
						sell.setBanBenNumber(s.getBanBenNumber());// 版本号
						sell.setKgliao(s.getKgliao());// 供料属性
						sell.setWgType(s.getWgType());// 物料类别
						sell.setSellGoods(s.getGoodsFullName());// 品名
						sell.setSellFormat(s.getGoodsFormat());// 规格
						sell.setSellCount(number);// 出库数量
						sell.setSellUnit(s.getGoodsUnit());// 单位
						sell.setSellSupplier(s.getGoodsSupplier());// 供应商
						sell.setSellTime(Util.getDateTime());// 出库时间
						sell.setTuhao(s.getTuhao());// 图号
						sell.setBedit(false);// 出库权限
						sell.setBedit(false);// 编辑权限
						sell.setPrintStatus("YES");// 打印状态
						sell.setStyle("不合格品出库");
						sell.setYwmarkId(s.getYwmarkId());// 业务件号
						sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
						sell.setOrderNum(s.getNeiorderId());// 内部订单号
						sell.setSellPeople(user.getName());
						sell.setSellZhishu(s.getGoodsZhishu());
						totalDao.save(sell);
						totalDao.update(s);
						// 入到外购件库 或外协库
						String kubie = "外购件库";
						if ("外委在库不良".equals(dp.getType())) {
							kubie = "外协库";
						}
						GoodsStore gt = new GoodsStore();
						BeanUtils.copyProperties(t, gt, new String[] {
								"goodsStoreId", "goodsStoreWarehouse",
								"goodsStoreCount", "goodsStoreDate",
								"goodsStoreTime", "style" });
						gt.setGoodsStoreWarehouse(kubie);
						gt.setGoodsStoreCount(number);
						gt.setPrintStatus("YES");
						gt.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
						gt.setStyle("合格品入库");
						gt.setGoodHouseName(bhggt.getGoodHouseName());
						gt.setGoodsStoreLot(wgdd.getExamineLot());
						gt.setGoodsStoreTime(Util.getDateTime());
						gt.setGoodsStoreCharger(user.getName());
						gt.setYwmarkId(t.getYwmarkId());// 业务件号
						gt.setNeiorderId(t.getNeiorderId());// 内部订单号
						gt.setWaiorderId(t.getWaiorderId());// 外部订单号
						if (bili != null && bili > 0) {
							gt.setGoodsStoreZhishu((float) Math.floor(number
									/ bili));
						}
						goodsStoreServer.saveSgrk(gt);
					}
				} 
			}
		}
	}

	public List<Sell> ajax_findSellList(Integer id) {
		List<Sell> sellList = totalDao.query(
				"from Sell where wgOrderId=?  order by sellMarkId", id);
		return sellList;
	}

	@Override
	public String ajax_addremark(Integer rootId, String markId, String kgliao,
			String banben, String specification, String remark) {
		String hql = " from Procard where rootId=? and markId = ? and kgliao =? and (wwblCount =0 or wwblCount is null )";
		if (banben != null && banben.length() > 0) {
			hql += " and banBenNumber = '" + banben + "'";
		} else {
			hql += " and (banBenNumber = '' or banBenNumber is null) ";
		}
		if (specification != null && specification.length() > 0) {
			hql += " and specification = '" + specification + "'";
		} else {
			hql += " and (specification = '' or specification is null )";
		}
		List<Procard> procardList = totalDao.query(hql, rootId, markId, kgliao);
		if (procardList != null && procardList.size() > 0) {
			for (Procard procard : procardList) {
				procard.setRemark(remark);
				totalDao.update(procard);
			}
			return "true";
		}
		return "error";
	}

	@Override
	public void XiuFuOrderStatus() {
		List<WaigouOrder> waigouOrderList = totalDao
				.query(" from WaigouOrder where status in ('生产中','送货中')");
		int count = 0;
		for (WaigouOrder waigouOrder : waigouOrderList) {
			Integer wrkcount1 = totalDao
					.getCount(
							" from WaigouPlan where waigouOrder.id =? and status not in ('入库','退回','退货','完成')",
							waigouOrder.getId());
			if (wrkcount1 > 0) {
				waigouOrder.setStatus("生产中");
			} else {
				count++;
				waigouOrder.setStatus("入库");
				totalDao.update(waigouOrder);
			}
		}
		System.out.println("共修复了:" + count + "条数据.");

	}

	@Override
	public String UpdateQrNumber(Integer id, Float qrNumber) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		if (id != null) {
			WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao.get(
					WaigouDeliveryDetail.class, id);
			 if (!user.getId().equals(wgdd.getQrUsersId())) {
			 return "您不是此送货明细的签收人，无法修改此明细的签收数量!~";
			 }
			Float oldQrNumber = wgdd.getQrNumber();
			Float number = oldQrNumber - qrNumber;
			wgdd.setQrNumber(qrNumber);
			if (qrNumber == 0) {
				wgdd.setStatus("退回");
			}
			wgdd
					.setRemarks((wgdd.getRemarks() == null ? "" : wgdd
							.getRemarks())
							+ "<br/>"
							+ user.getName()
							+ "于"
							+ Util.getDateTime()
							+ "将签收数量由:"
							+ oldQrNumber
							+ "改为:" + qrNumber + "。");
			totalDao.update(wgdd);
			// 修改采购明细未送货数量和签收数量
			WaigouPlan wgplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					wgdd.getWaigouPlanId());
			wgplan.setSyNumber(wgplan.getSyNumber() + number);
			wgplan.setQsNum(wgplan.getQsNum() - number);
			totalDao.update(wgplan);
			// 修改物料需求汇总未送货数量和签收数量
			if (wgplan.getMopId() != null) {
				ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
						ManualOrderPlan.class, wgplan.getMopId());
				if (mop != null) {
					mop.setWshCount(mop.getWshCount() + number);
					mop.setQsCount(mop.getQsCount() - number);
					totalDao.update(mop);
				}
			}
			// 修改待检库数量
			GoodsStore gs = (GoodsStore) totalDao
					.getObjectByCondition(
							" from GoodsStore where wwddId = ? and goodsStoreWarehouse = '待检库' ",
							wgdd.getId());
			if (gs != null) {
				gs.setGoodsStoreCount(gs.getGoodsStoreCount() - number);
				Goods goods = (Goods) totalDao
						.getObjectByCondition(
								" from Goods where goodsMarkId=? and goodsLotId =? and goodsClass = ? ",
								gs.getGoodsStoreMarkId(),
								gs.getGoodsStoreLot(), "待检库");
				if (goods != null) {
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							- number);
					totalDao.save(goods);
				}
				totalDao.save(gs);
			}
			return "true";
		}
		return null;
	}

	@Override
	public List<Procard> showtocsbl(Integer id) {
		if (id != null) {
			WaigouPlan wgpaln = (WaigouPlan) totalDao.get(WaigouPlan.class, id);
			if (wgpaln != null) {
				String hql = " from Procard where id in ("
						+ " select procard.id from ProcessInfor where processNO =? and processName =? and status <> '完成' and procard.id in "
						+ " ( select procardId from ProcardWGCenter where wgOrderId = ?) ) "
						+ " and rootId not in (select id from Procard where  status  in('完成','待入库','入库','暂停','取消') ) ";
				String[] processNOs = wgpaln.getProcessNOs().split(";");
				String[] processNames = wgpaln.getProcessNames().split(";");
				Integer processNO = 0;
				String processName = "";
				if (processNOs != null && processNOs.length > 0) {
					processNO = Integer.parseInt(processNOs[0]);
				}
				if (processNames != null && processNames.length > 0) {
					processName = processNames[0];
				}
				return totalDao.query(hql, processNO, processName, id);
			}

		}
		return null;
	}

	@Override
	public String closeWaiweiPlan(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		WaigouPlan wgpLan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id);
		if (wgpLan != null) {
			if ((wgpLan.getWaigouOrder().getAddUserCode() != null && wgpLan
					.getWaigouOrder().getAddUserCode().equals(user.getCode()))
					|| (wgpLan.getWaigouOrder().getTzUserCode() != null && wgpLan
							.getWaigouOrder().getTzUserCode().equals(
									user.getCode()))) {
				if (wgpLan.getStatus().equals("关闭")) {
					return "订单已关闭请勿重复操作!";
				}
				if (wgpLan.getStatus().equals("取消")
						|| wgpLan.getStatus().equals("删除")) {
					return "订单已" + wgpLan.getStatus() + ",无效的操作!";
				}
				wgpLan.setStatus("关闭");
				wgpLan.setCordTime(Util.getDateTime());
				totalDao.update(wgpLan);
				int count = totalDao
				.getCount(
						" from WaigouPlan where waigouOrder.id =? and status <> '取消' ",
						wgpLan.getWaigouOrder().getId());
				int count1 = totalDao
				.getCount(
						" from WaigouPlan where waigouOrder.id =? and status not in ('取消','入库','关闭') ",
						wgpLan.getWaigouOrder().getId());
				WaigouOrder waigouorder = wgpLan.getWaigouOrder();
				if (count == 0) {
					waigouorder.setStatus("取消");
					totalDao.update(waigouorder);
				}else if(count1 == 0){
			waigouorder.setStatus("入库");
			totalDao.update(waigouorder);	
				}
			}
			return "关闭成功";
		}

		return "没有找到对应的订单!";
	}

	@Override
	public String backwwplan(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		WaigouPlan wwplan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id);
		if (wwplan != null) {
			if ((wwplan.getWaigouOrder().getAddUserCode() != null && wwplan
					.getWaigouOrder().getAddUserCode().equals(user.getCode()))
					|| (wwplan.getWaigouOrder().getTzUserCode() != null && wwplan
							.getWaigouOrder().getTzUserCode().equals(
									user.getCode()))) {
				if (wwplan.getStatus().equals("取消")) {
					return "订单已取消请勿重复操作!";
				}
				if (wwplan.getStatus().equals("删除")
						|| wwplan.getStatus().equals("关闭")) {
					return "订单已" + wwplan.getStatus() + ",无效的操作!";
				}
				// 获取对应数据
				if (wwplan.getWwSource().equals("BOM外委")) {
					List<WaigouWaiweiPlan> wgwwplanList = totalDao
							.query(
									"from WaigouWaiweiPlan where id in(select wwxlId from ProcardWGCenter where wgOrderId=?)",
									wwplan.getId());
					if (wgwwplanList != null && wgwwplanList.size() > 0) {
						for (WaigouWaiweiPlan wwp : wgwwplanList) {
							wwp.setStatus("待激活");
							totalDao.update(wwp);
						}
					}
				} else {
					List<ProcessInforWWApplyDetail> wwdList = totalDao
							.query(
									"from ProcessInforWWApplyDetail where id in(select wwxlId from ProcardWGCenter where wgOrderId=?)",
									wwplan.getId());
					if (wwdList != null && wwdList.size() > 0) {
						for (ProcessInforWWApplyDetail wwd : wwdList) {
							wwd.setProcessStatus("外委待下单");
							totalDao.update(wwd);
						}
					}
				}
				wwplan.setStatus("取消");
				wwplan.setCordTime(Util.getDateTime());
				totalDao.update(wwplan);
				//修改明细后修改订单状态
				int count = totalDao
						.getCount(
								" from WaigouPlan where waigouOrder.id =? and status <> '取消' ",
								wwplan.getWaigouOrder().getId());
				int count1 = totalDao
				.getCount(
						" from WaigouPlan where waigouOrder.id =? and status not in ('取消','入库','关闭') ",
						wwplan.getWaigouOrder().getId());
				WaigouOrder waigouorder = wwplan.getWaigouOrder();
				if (count == 0) {
					waigouorder.setStatus("取消");
					totalDao.update(waigouorder);
					
				}else if(count1 == 0){
					waigouorder.setStatus("入库");
					totalDao.update(waigouorder);	
				}
				
				return "true";
			}
			return "对不起，您无权做此操作!";
		}

		return "没有找到对应的订单!";
	}

	@Override
	public void exprotrds(ReturnsDetails rds, String status, String startDate,
			String endDate, String tag) {
		if (rds == null) {
			rds = new ReturnsDetails();
		}
		String str = "";
		if (rds.getReturnSingle() != null) {
			if (rds.getReturnSingle().getType() != null
					&& rds.getReturnSingle().getType().length() > 0) {
				str += " and returnSingle.id in (select id from ReturnSingle where type = '"
						+ rds.getReturnSingle().getType() + "')";
			}

		}
		String startTimeHql = "";
		String endTimeHql = "";
		if (startDate != null && !"".equals(startDate)) {
			startTimeHql = " and approvalTime >= '" + startDate + " 00:00:00'";
		}
		if (endDate != null && !"".equals(endDate)) {
			endTimeHql = " and approvalTime <= '" + endDate + " 23:59:59'";
		}
		String hql = totalDao.criteriaQueries(rds, null, "returnSingle");
		hql += str + startTimeHql + endTimeHql;
		if ("zkbl".equals(tag)) {
			hql += " and returnSingle.id in (select id from ReturnSingle where type = '退货单' ) ";
		} else if ("llbl".equals(tag)) {
			hql += " and returnSingle.id in (select id from ReturnSingle where type = '采购检验不良处理单' ) ";
		}
		List<ReturnsDetails> rdsList = totalDao.query(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			String filename = "退货单明细";
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(filename.getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet(filename, 0);
			ws.setColumnView(16, 30);

			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "采购单号"));
			ws.addCell(new Label(2, 0, "送货单号"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "零件名称"));
			ws.addCell(new Label(5, 0, "供料属性"));
			ws.addCell(new Label(6, 0, "物料类别"));
			ws.addCell(new Label(7, 0, "规格"));
			ws.addCell(new Label(8, 0, "检验批次"));
			ws.addCell(new Label(9, 0, "退货数量"));
			ws.addCell(new Label(10, 0, "单价(含税)"));
			ws.addCell(new Label(11, 0, "金额"));
			ws.addCell(new Label(12, 0, "供应商名称"));
			ws.addCell(new Label(13, 0, "退货类型"));
			ws.addCell(new Label(14, 0, "批准时间"));
			ws.addCell(new Label(15, 0, "状态"));
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < rdsList.size(); i++) {
				ReturnsDetails rds1 = rdsList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, rds1.getCgOrderNum()));
				ws.addCell(new Label(2, i + 1, rds1.getShOrderNum()));
				ws.addCell(new Label(3, i + 1, rds1.getMarkId()));
				ws.addCell(new Label(4, i + 1, rds1.getProName()));
				ws.addCell(new Label(5, i + 1, rds1.getKgliao()));
				ws.addCell(new Label(6, i + 1, rds1.getWgType()));
				ws.addCell(new Label(7, i + 1, rds1.getSpecification()));
				ws.addCell(new Label(8, i + 1, rds1.getExamineLot()));
				ws.addCell(new jxl.write.Number(9, i + 1, Float.parseFloat("-"
						+ rds1.getThNumber())));
				ws.addCell(new jxl.write.Number(10, i + 1, rds1.getHsPrice()));
				ws.addCell(new jxl.write.Number(11, i + 1, Float.parseFloat("-"
						+ df.format(rds1.getHsPrice() * rds1.getThNumber()))));
				ws.addCell(new Label(12, i + 1, rds1.getGysName()));
				ws.addCell(new Label(13, i + 1, rds1.getReturnSingle()
						.getType()));
				ws.addCell(new Label(14, i + 1, rds1.getApprovalTime()
						.substring(0, 10)));
				ws.addCell(new Label(15, i + 1, rds1.getStatus()));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public void saveSell(Goods s, Float sellCount, Users user, String style) {
		// 合格品出库 添加出库记录
		Sell sell = new Sell();
		sell.setSellMarkId(s.getGoodsMarkId());// 件号
		sell.setSellWarehouse(s.getGoodsClass());// 库别
		sell.setGoodHouseName(s.getGoodHouseName());// 仓区
		sell.setKuwei(s.getGoodsPosition());// 库位
		sell.setBanBenNumber(s.getBanBenNumber());// 版本号
		sell.setKgliao(s.getKgliao());// 供料属性
		sell.setWgType(s.getWgType());// 物料类别
		sell.setSellGoods(s.getGoodsFullName());// 品名
		sell.setSellFormat(s.getGoodsFormat());// 规格
		sell.setSellCount(sellCount);// 出库数量
		sell.setSellUnit(s.getGoodsUnit());// 单位
		sell.setSellSupplier(s.getGoodsSupplier());// 供应商
		sell.setSellTime(Util.getDateTime());// 出库时间
		sell.setTuhao(s.getTuhao());// 图号
		sell.setBedit(false);// 出库权限
		sell.setBedit(false);// 编辑权限
		sell.setPrintStatus("YES");// 打印状态
		sell.setStyle(style);
		sell.setYwmarkId(s.getYwmarkId());// 业务件号
		sell.setOutOrderNumer(s.getWaiorderId());// 外部订单号
		sell.setOrderNum(s.getNeiorderId());// 内部订单号
		sell.setSellPlanner(user.getName());
		sell.setSellTime(Util.getDateTime());
		sell.setSellPrice(s.getGoodsBuyPrice());
		sell.setSellbhsPrice(s.getGoodsBuyBhsPrice());
		totalDao.save(sell);

	}

	@Override
	public List<OsRecordScope> findOScopeListBywwddId(Integer id) {
		List<OsRecordScope> osList = totalDao.query(
				" from OsRecordScope where osRecord.id in ("
						+ "select id from OsRecord where wwddId = ?)", id);
		return osList;
	}

	@Override
	public String addwaigouOrderRemark(WaigouOrder wgo) {
		if (wgo != null && wgo.getWwpList() != null) {
			// waigouOrder.setRemarkMore(wgo.getRemarkMore());
			// return totalDao.update(waigouOrder)+"";
			for (WaigouPlan wp : wgo.getWwpList()) {
				if (wp.getId() != null) {
					WaigouPlan waigouPlan = (WaigouPlan) totalDao
							.getObjectById(WaigouPlan.class, wp.getId());
					if (waigouPlan != null) {
						waigouPlan.setRemark(wp.getRemark());
						totalDao.update(waigouPlan);
					}
				}
			}
			return "true";
		}
		return "false";
	}

	private String isOverflowMaxkc(String markId, String banben, String kgliao,
			Float num) {
		// 现有库存
		String hql_goods = "select sum(goodsCurQuantity) from Goods where goodsMarkId =? "
				+ " and kgliao =? and goodsClass = '外购件库' and goodsCurQuantity>0 ";
		String hql = "select  maxkc from YuanclAndWaigj where markid =? and kgliao =? ";
		// 在途量
		String banben_hql2 = "";
		String specification_sql = "";
		if (banben != null && banben.length() > 0) {
			hql += " and banbenhao = '" + banben + "'";
			hql_goods += " and banBenNumber = '" + banben + "'";
			banben_hql2 += " and banben = '" + banben + "'";
		} else {
			hql += " and (banbenhao is null or banbenhao = '' )";
			hql_goods += " and (banBenNumber is null or banBenNumber = '')";
			banben_hql2 += " and (banben is null or banben = '')";
		}
		hql += " and maxkc is not null and maxkc>0 and (banbenStatus is null or banbenStatus<> '历史' )";
		Float maxkc = (Float) totalDao
				.getObjectByCondition(hql, markId, kgliao);
		Float sumgoods = (Float) totalDao.getObjectByCondition(hql_goods,
				markId, kgliao);
		if (sumgoods == null) {
			sumgoods = 0f;
		}
		// 系统在途量(已下采购单，但是未入库的)
		String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
				+ banben_hql2
				+ " and kgliao=? and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null)" +
						" and  outcgNumber >0"
				+ specification_sql;
		Double ztNum = (Double) totalDao.getObjectByCondition(hql_zc0, markId,
				kgliao);
		if (ztNum == null) {
			ztNum = 0d;
		}
		if (maxkc != null && maxkc > 0) {
			if ((num + sumgoods + ztNum) > maxkc) {
				return "件号:<b>" + markId + "</b> 版本:<b>" + banben
						+ "</b>采购数量:<b>" + num + "</b>+现有库存:<b>" + sumgoods
						+ "</b>+在途量:<b>" + ztNum + "</b>>该件号" + "的最高库存:<b>"
						+ maxkc + "</b>，请调整采购量后再进行采购";
			}
		}
		return "true";
	}

	private void addReturnSingle(ZhUser zhuser, DefectiveProduct dp,
			WaigouDeliveryDetail wgdd, String bldescribe, Users user) {
		ReturnsDetails rds = new ReturnsDetails();
		rds.setGysId(zhuser.getId());// 供应商Id
		rds.setGysName(zhuser.getCmp());// 供应商名称
		rds.setGysdizhi(zhuser.getCompanydz());// 供应商地址
		rds.setGysUser(zhuser.getCperson());// 供应商联系人
		rds.setGysPhone(zhuser.getCtel());// 供应商电话
		rds.setMarkId(dp.getMarkId());
		rds.setProName(dp.getProName());
		rds.setTuhao(dp.getTuhao());
		rds.setKgliao(dp.getKgliao());
		rds.setWgType(dp.getWgType());
		rds.setSpecification(dp.getSpecification());
		rds.setUnit(dp.getUnit());// 单位
		rds.setHsPrice(dp.getHsPrice());// 含税单价
		rds.setPrice(dp.getPrice());// 不含税单价
		rds.setTaxprice(dp.getTaxprice());// 税率
		rds.setPriceId(dp.getPriceId());// 价格id
		rds.setThNumber(dp.getZjbhgNumber());// 退货数量
		rds.setExamineLot(dp.getExamineLot());// 检验批次
		rds.setDpId(dp.getId());// 不良处理单Id
		rds.setWgddId(wgdd.getId());// 送货单Id
		rds.setShOrderNum(wgdd.getWaigouDelivery().getPlanNumber());// 送货单号
		rds.setCgOrderNum(wgdd.getCgOrderNum());// 采购订单号
		rds.setBanben(dp.getBanben());// 版本
		rds.setStatus("待领");
		rds.setBldescribe(bldescribe);// 检验不良描述
		Set<ReturnsDetails> rdsSet = null;
		// 判断同供应商是否有待领的退货单
		String hql_rs = " from ReturnSingle where gysId=? and status = ? and epstatus = '未审批'";
		ReturnSingle rs = (ReturnSingle) totalDao.getObjectByCondition(hql_rs,
				dp.getGysId(), "待领");
		if (rs == null) {// 如果没有 创建退货单 退货单明细
			rs = new ReturnSingle();
			rs.setGysId(zhuser.getId());
			rs.setGysName(zhuser.getCmp());
			rs.setGysjc(zhuser.getName());
			String type = "采购检验不良处理单";
			if ("外购在库不良".equals(dp.getType()) || "外委在库不良".equals(dp.getType())
					|| "研发在库不良".equals(dp.getType())) {
				type = "退货单";
			}
			rs.setType(type);
			rs.setGysdizhi(zhuser.getCompanydz());
			rs.setGysUser(zhuser.getCperson());
			rs.setGysPhone(zhuser.getCtel());
			rs.setStatus("待领");
			// 得到最大的退货单编号
			String maxNumber = "DH" + Util.getDateTime("yyyyMMdd");
			String hql_maxnumber = "select max(planNum) from ReturnSingle where planNum like '%"
					+ maxNumber + "%'";
			Object obj = totalDao.getObjectByCondition(hql_maxnumber);
			if (obj != null) {
				String maxNumber2 = obj.toString();
				int num2 = Integer.parseInt(maxNumber2.substring(maxNumber2
						.length() - 4, maxNumber2.length()));
				num2++;
				if (num2 < 10) {
					maxNumber += "000" + num2;
				} else if (num2 < 100) {
					maxNumber += "00" + num2;
				} else if (num2 < 1000) {
					maxNumber += "0" + num2;
				} else {
					maxNumber += num2 + "";
				}
			} else {
				maxNumber += "0001";
			}
			rs.setPlanNum(maxNumber);
			rdsSet = new HashSet<ReturnsDetails>();
			rdsSet.add(rds);
			rs.setThNumber(rds.getThNumber());
			rs.setRdsSet(rdsSet);
			rs.setAddTime(Util.getDateTime());
			rs.setGysuserId(zhuser.getUserid());
			totalDao.save(rs);
			String processName = "不良退货申请";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl
						.createProcess(
								processName,
								ReturnSingle.class,
								rs.getId(),
								"epstatus",
								"id",
								"WaigouwaiweiPlanAction!findReturnsDetailsByrsId.action?pageStatus=noprint",
								user.getDept() + "部门 " + user.getName()
										+ "不良退货申请，请您审批", true);
				if (epId != null && epId > 0) {
					rs.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						rs.setEpstatus("同意");
					} else {
						rs.setEpstatus("未审批");
					}
					totalDao.update(rs);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 如有 则更新退货单 并把此退货单明细加到之前退货单上
			rdsSet = rs.getRdsSet();
			rdsSet.add(rds);
			rds.setReturnSingle(rs);
			rs.setThNumber(rs.getThNumber() + rds.getThNumber());
			totalDao.update(rs);
		}
		// 发送消息给供应商
		AlertMessagesServerImpl.addAlertMessages("退货单管理(供应商)", "您共有"
				+ rs.getThNumber() + "件，产品退回。" + "退货单号:" + rs.getPlanNum()
				+ ",。请联系签收人员打印退货单领回。", "1", zhuser.getUsercode());
	}

	private String mustNotOverflowWgPlanNum(Integer wgPlanId, Float shNumber,
			Float wgNumber, Float rangeOfReceipt, WaigouPlan wgplan) {
		List<WaigouDeliveryDetail> wgddList = totalDao
				.query(
						" from WaigouDeliveryDetail where waigouPlanId =? and status not in ('退回','退货')",
						wgPlanId);
		if (wgddList != null && wgddList.size() > 0) {
			for (WaigouDeliveryDetail wgdd : wgddList) {
				if (wgdd.getHgNumber() != null && wgdd.getHgNumber() > 0) {
					shNumber += wgdd.getHgNumber();
				} else if (wgdd.getQrNumber() != null && wgdd.getQrNumber() > 0) {
					shNumber += wgdd.getQrNumber();
				} else {
					shNumber += wgdd.getShNumber();
				}
				if (wgdd.getTuihuoNum() != null && wgdd.getTuihuoNum() > 0) {
					shNumber -= wgdd.getTuihuoNum();
				}
			}
		}
		if ((wgNumber + (rangeOfReceipt == null ? 0 : rangeOfReceipt)) < shNumber) {
			return "采购单号:" + wgplan.getWaigouOrder().getPlanNumber() + "件号:"
					+ wgplan.getMarkId() + " " + "总送货数量:" + shNumber + ">订单数量:"
					+ wgNumber + "请立即联系管理员。\\n";
		}
		return "";
	}

	private String bhgTypeNum(OsRecord osRecord,
			List<OsRecord> bhgosRecordList, Float bhgNumber, String type) {
		FailureStatistics failureSt = new FailureStatistics();
		int week = 0;
		try {
			week = Util.getNowWeek(new Date());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Set<FailureStatisticsDetail> fsdSet = new HashSet<FailureStatisticsDetail>();
		String weeks = Util.getDateTime("yyyy") + "年" + week + "周";
		failureSt.setDateTime(Util.getDateTime("yyyy年MM月dd日"));
		failureSt.setMarkId(osRecord.getMarkId());
		failureSt.setWeekds(weeks);
		failureSt.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));
		failureSt.setSubmitCount(osRecord.getJyNumber());
		failureSt.setSelfcard(osRecord.getSelfCard());
		failureSt.setProName(osRecord.getProName());
		failureSt.setProcessName(osRecord.getGongxuName());
		try {
			failureSt.setProcessNo(Integer.parseInt(osRecord.getGongxuNum()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (OsRecord os : bhgosRecordList) {
			BeanUtils.copyProperties(osRecord, os, new String[] { "buhegeId",
					"code", "type", "bhgclass", "bhgTypeNum" });
			BuHeGePin bhgp = (BuHeGePin) totalDao.getObjectById(
					BuHeGePin.class, os.getBuhegeId());
			if (bhgp != null) {
				String[] cpAndClass = os.getCode().split(",");
				String code = cpAndClass[0] + bhgp.getCode() + cpAndClass[1];
				code = code.replaceAll(" ", "");
				// 不良品类型明细
				boolean bool = true;
				for (FailureStatisticsDetail f : fsdSet) {
					if (f.getBuhegeId().equals(bhgp.getId())) {
						f.setBadNumber(f.getBadNumber() + os.getBhgTypeNum());
						bool = false;
					}
				}
				if (bool) {
					FailureStatisticsDetail fsd = new FailureStatisticsDetail();
					fsd.setBuhegeId(bhgp.getId());
					fsd.setCode(code);
					fsd.setType(bhgp.getType());
					fsd.setBadNumber(os.getBhgTypeNum().floatValue());
					fsd.setFailureStatistics(failureSt);
					fsdSet.add(fsd);
				}
				// 检验明细不良缺陷赋值
				os.setCode(code);
				os.setType(bhgp.getType());
				totalDao.save(os);
			}
		}
		// 不良品检验记录
		String days = Util.getDateTime("yyyy年MM月dd日");
		String months = Util.getDateTime("yyyy年MM月");
		failureSt.setFailureCount(bhgNumber);
		failureSt.setAdddays(days);
		failureSt.setAddmonths(months);
		failureSt.setFsdSet(fsdSet);
		failureSt.setType(type);
		Float nowberakcount = (Float) totalDao
				.getObjectByCondition(
						"select sum(failureCount) from FailureStatistics where selfcard = ? and markId = ? ",
						failureSt.getSelfcard(), failureSt.getMarkId());
		failureSt.setNowberakcount(nowberakcount + bhgNumber);// 记录当前时间节点该批次件号不合格品总数
		totalDao.save(failureSt);
		Set<FailureStatisticsDetail> fsdSet1 = failureSt.getFsdSet();
		// 统计日报表
		FailureSSOnDay fsday = (FailureSSOnDay) totalDao.getObjectByCondition(
				"  from FailureSSOnDay where days = ? ", days);
		if (fsday != null) {
			Set<FailureStatisticsDetail> fsddaySet = fsday.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = (FailureStatisticsDetail) totalDao
						.getObjectByCondition(
								" from FailureStatisticsDetail where failureSSOnDay.id =? and buhegeId = ?",
								fsday.getId(), f.getBuhegeId());
				if (fsd != null) {
					fsd.setBadNumber(fsd.getBadNumber() + f.getBadNumber());
					totalDao.update(fsd);
				} else {
					if (fsddaySet == null) {
						fsddaySet = new HashSet<FailureStatisticsDetail>();
					}
					fsd = new FailureStatisticsDetail();
					fsd.setBadNumber(f.getBadNumber());
					fsd.setBuhegeId(f.getBuhegeId());
					fsd.setCode(f.getCode());
					fsd.setType(f.getType());
					fsd.setFailureSSOnDay(fsday);
					fsddaySet.add(fsd);
				}
			}
			fsday.setType(type);
			fsday
					.setOneDayFc(fsday.getOneDaySc()
							+ failureSt.getFailureCount());// 每日不合格总数 某工位
			fsday.setOneDaySc(fsday.getOneDaySc() + failureSt.getSubmitCount());// 每日检验总数
			fsday.setFsdSet(fsddaySet);
			totalDao.update(fsday);
		} else {
			fsday = new FailureSSOnDay();
			fsday.setOneDayFc(failureSt.getFailureCount());// 每日不合格总数 某工位
			fsday.setOneDaySc(failureSt.getSubmitCount());// 每日检验总数 某工位
			fsday.setAddTime(Util.getDateTime());
			fsday.setGongwei(failureSt.getGongwei());
			fsday.setDays(days);
			fsday.setType(type);
			fsday.setMonths(months);
			fsday.setWeekds(weeks);
			Set<FailureStatisticsDetail> fsddaySet = fsday.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = new FailureStatisticsDetail();
				fsd.setBadNumber(f.getBadNumber());
				fsd.setBuhegeId(f.getBuhegeId());
				fsd.setCode(f.getCode());
				fsd.setType(f.getType());
				fsd.setFailureSSOnDay(fsday);
				if (fsddaySet == null) {
					fsddaySet = new HashSet<FailureStatisticsDetail>();
				}
				fsddaySet.add(fsd);
			}
			fsday.setFsdSet(fsddaySet);
			totalDao.save(fsday);
		}
		// 统计周报表
		FailureSSOnWeek fsweek = (FailureSSOnWeek) totalDao
				.getObjectByCondition(
						"  from FailureSSOnWeek where  weekds = ? ", weeks);
		if (fsweek != null) {
			Set<FailureStatisticsDetail> fsdweekSet = fsweek.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = (FailureStatisticsDetail) totalDao
						.getObjectByCondition(
								" from FailureStatisticsDetail where failureSSOnWeek.id =? and buhegeId = ?",
								fsweek.getId(), f.getBuhegeId());
				if (fsd != null) {
					fsd.setBadNumber(fsd.getBadNumber() + f.getBadNumber());
					totalDao.update(fsd);
				} else {
					if (fsdweekSet == null) {
						fsdweekSet = new HashSet<FailureStatisticsDetail>();
					}
					fsd = new FailureStatisticsDetail();
					fsd.setBadNumber(f.getBadNumber());
					fsd.setBuhegeId(f.getBuhegeId());
					fsd.setCode(f.getCode());
					fsd.setType(f.getType());
					fsd.setFailureSSOnWeek(fsweek);
					fsdweekSet.add(fsd);
				}
			}
			fsweek.setType(type);
			fsweek.setOneWeekFc(fsweek.getOneWeekFc()
					+ failureSt.getFailureCount());// 每周不合格总数 某工位
			fsweek.setOneWeekSc(fsweek.getOneWeekSc()
					+ failureSt.getSubmitCount());// 每周检验总数 某工位
			fsweek.setFsdSet(fsdweekSet);
			totalDao.update(fsweek);
		} else {
			fsweek = new FailureSSOnWeek();
			fsweek.setOneWeekFc(failureSt.getFailureCount());// 每周不合格总数 某工位
			fsweek.setOneWeekSc(failureSt.getSubmitCount());// 每周检验总数 某工位
			fsweek.setAddTime(Util.getDateTime());
			fsweek.setGongwei(failureSt.getGongwei());
			fsweek.setType(type);
			fsweek.setWeekds(weeks);
			Set<FailureStatisticsDetail> fsdweekSet = fsweek.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = new FailureStatisticsDetail();
				fsd.setBadNumber(f.getBadNumber());
				fsd.setBuhegeId(f.getBuhegeId());
				fsd.setCode(f.getCode());
				fsd.setType(f.getType());
				fsd.setFailureSSOnWeek(fsweek);
				if (fsdweekSet == null) {
					fsdweekSet = new HashSet<FailureStatisticsDetail>();
				}
				fsdweekSet.add(fsd);
			}
			fsweek.setFsdSet(fsdweekSet);
			totalDao.save(fsweek);
		}
		// 统计月报表
		FailureSSOnMonth fsdmonth = (FailureSSOnMonth) totalDao
				.getObjectByCondition(
						"  from FailureSSOnMonth where months = ? ", months);
		if (fsdmonth != null) {
			Set<FailureStatisticsDetail> fsdmonthSet = fsweek.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = (FailureStatisticsDetail) totalDao
						.getObjectByCondition(
								" from FailureStatisticsDetail where failureSSOnWeek.id =? and buhegeId = ?",
								fsweek.getId(), f.getBuhegeId());
				if (fsd != null) {
					fsd.setBadNumber(fsd.getBadNumber() + f.getBadNumber());
					totalDao.update(fsd);
				} else {
					if (fsdmonthSet == null) {
						fsdmonthSet = new HashSet<FailureStatisticsDetail>();
					}
					fsd = new FailureStatisticsDetail();
					fsd.setBadNumber(f.getBadNumber());
					fsd.setBuhegeId(f.getBuhegeId());
					fsd.setCode(f.getCode());
					fsd.setType(f.getType());
					fsd.setFailureSSOnMonth(fsdmonth);
					fsdmonthSet.add(fsd);
				}
			}
			fsdmonth.setOneMonthSc(fsdmonth.getOneMonthSc()
					+ failureSt.getFailureCount());// 每月不合格总数 某工位
			fsdmonth.setOneMonthFc(fsdmonth.getOneMonthFc()
					+ failureSt.getSubmitCount());// 每月检验总数 某工位
			fsdmonth.setFsdSet(fsdmonthSet);
			fsdmonth.setType(type);
			totalDao.update(fsdmonth);
		} else {
			fsdmonth = new FailureSSOnMonth();
			fsdmonth.setOneMonthSc(failureSt.getFailureCount());// 每月不合格总数 某工位
			fsdmonth.setOneMonthFc(failureSt.getSubmitCount());// 每月检验总数 某工位
			fsdmonth.setAddTime(Util.getDateTime());
			fsdmonth.setGongwei(failureSt.getGongwei());
			fsdmonth.setWeekds(weeks);
			fsdmonth.setType(type);
			Set<FailureStatisticsDetail> fsdmonthSet = fsdmonth.getFsdSet();
			for (FailureStatisticsDetail f : fsdSet1) {
				FailureStatisticsDetail fsd = new FailureStatisticsDetail();
				fsd.setBadNumber(f.getBadNumber());
				fsd.setBuhegeId(f.getBuhegeId());
				fsd.setCode(f.getCode());
				fsd.setType(f.getType());
				fsd.setFailureSSOnMonth(fsdmonth);
				if (fsdmonthSet == null) {
					fsdmonthSet = new HashSet<FailureStatisticsDetail>();
				}
				fsdmonthSet.add(fsd);
			}
			fsdmonth.setFsdSet(fsdmonthSet);
			totalDao.save(fsdmonth);
		}
		return null;
	}

	@Override
	public List<ProcardCsBl> findCsblProcardBywgddId(Integer id) {
		if (id != null) {
			return totalDao.query(" from ProcardCsBl where wgplanId =? ", id);
		}
		return null;
	}

	@Override
	public String DefChuLiSq(DefectiveProduct def, int[] uids, String tuisongId) {
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录";
		}
		String msg = "true";
		if (def != null) {
			DefectiveProduct oldDef = (DefectiveProduct) totalDao.get(
					DefectiveProduct.class, def.getId());
			oldDef.setResult(def.getResult());
			oldDef.setZjhgNumber(def.getZjhgNumber());
			oldDef.setZjbhgNumber(def.getZjbhgNumber());
			oldDef.setFenjianNum(def.getFenjianNum());
			oldDef.setZjUsers(users.getName());
			oldDef.setZjTime(Util.getDateTime());
			oldDef.setRamk(def.getRamk());
			totalDao.update(oldDef);
			Integer epId = null;
			if (uids != null && uids.length > 0) {
				StringBuffer userIdSb = new StringBuffer();
				List<Integer> uidlist = new ArrayList<Integer>();
				for (int n = 0; n < uids.length; n++) {
					if (uids[n] > 0 && !uidlist.contains(uids[n])) {
						uidlist.add(uids[n]);
						if (n == 0) {
							userIdSb.append(uids[n]);
						} else {
							userIdSb.append("," + uids[n]);
						}
					}
				}
				String processName = "不良品状态审批流程";
				try {
					if (userIdSb.length() > 0) {
						epId = CircuitRunServerImpl.createProcessbf(
								processName, DefectiveProduct.class, oldDef
										.getId(), "epStatus", "id",
								"WaigouwaiweiPlanAction!tobhgth.action?pageStatus=admin&id="
										+ oldDef.getId(), "采购订单号:"
										+ oldDef.getCgOrderNum() + "/件号："
										+ oldDef.getMarkId() + "/零件名称："
										+ oldDef.getProName() + "的不合格品进入"
										+ oldDef.getResult() + "状态", true,
								userIdSb.toString());
					}
					if (epId != null && epId > 0) {
						oldDef.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							oldDef.setStatus("已处理");
							oldDef.setEpStatus("同意");
						} else {
							oldDef.setEpStatus("未审批");
						}
					}
					String[] tsid = tuisongId.split(";");
					if (tsid != null && tsid.length > 0) {
						for (String a : tsid) {
							AlertMessagesServerImpl
									.addAlertMessages(processName, "采购订单号:"
											+ oldDef.getCgOrderNum() + "/件号："
											+ oldDef.getMarkId() + "/零件名称："
											+ oldDef.getProName() + "的不合格品进入"
											+ oldDef.getResult() + "状态",
											"WaigouwaiweiPlanAction!tobhgth.action?pageStatus=admin&id="
													+ oldDef.getId(), "1", a
													.toString());
						}
					}

				} catch (Exception e) {
					msg = e.getMessage();
					e.printStackTrace();
				}
			}
			if (epId == null) {
				if ("退货".equals(oldDef.getResult())) {
					msg = bhgth(oldDef.getId(), oldDef.getZjbhgNumber(), oldDef
							.getZjhgNumber(), null);
					oldDef.setStatus("已处理");
				} else if ("特采".equals(oldDef.getResult())) {
					msg = bhgth(oldDef.getId(), oldDef.getZjbhgNumber(), oldDef
							.getZjhgNumber(), null);
					oldDef.setStatus("已处理");
				} else if ("挑选".equals(oldDef.getResult())) {
					msg = isAgainCheck(oldDef);
					oldDef.setStatus("已处理");
				} else if ("报废".equals(oldDef.getResult())) {
					oldDef.setStatus("已处理");
				}
			}
			totalDao.update(oldDef);
			return msg;
		}
		return null;
	}

	@Override
	public List<WaigouPlanclApply> findWgClApplyList(Integer id) {
		if (id != null) {
			return totalDao.query(
					" from WaigouPlanclApply where waigouPlanId =?", id);
		}
		return null;
	}

	@Override
	public List<ProcessAndMeasuring> pamListByProcessName(String processNames) {
		if (processNames != null && processNames.length() > 0) {
			processNames = "'" + processNames.replaceAll(";", "','") + "'";
			return totalDao
					.query(" from ProcessAndMeasuring where processName in ("
							+ processNames + ")");
		}
		return null;
	}

	@Override
	public List WaigouPlanduiZhang(String type, String months0, String months1,
			String years0, String years1, WaigouPlan waigoupln) {
		if (type == null || type.length() == 0) {
			return null;
		}
		List<DuiZhangWgPlan> dzwgPlanList = new ArrayList<DuiZhangWgPlan>();
		List<Map> list0 = null;
		String sql_str = "";
		if (waigoupln != null) {
			sql_str = totalDao.criteriaQueries(waigoupln, null);
			String[] strs = sql_str.split("1=1");
			if (strs != null && strs.length == 2) {
				sql_str = strs[1];
			} else {
				sql_str = "";
			}
		}
		if (months1 != null && months1.length() > 0) {
			String years = months1.substring(0, 4);
			if (months0 == null || months0.length() == 0) {
				months0 = years + "-" + "01";
			}
			String sql = "SELECT  SUBSTRING(addTime FROM 1 FOR 7) months,COUNT(*) count,"
					+ " SUM(number) number ,SUM(number*hsPrice) money0 ,SUM(money) money1  "
					+ " FROM ta_sop_w_waigouplan where  addTime LIKE '"
					+ years
					+ "%' and "
					+ " number>0 and status not IN ('取消','删除','关闭') and type = '"
					+ type
					+ "' "
					+ sql_str
					+ " GROUP BY months HAVING months>='"
					+ months0
					+ "' and months<='" + months1 + "'";
			list0 = totalDao.findBySql(sql);
		} else if (years1 != null && years1.length() > 0) {
			if (years0 == null || years0.length() == 0) {
				String addTime = (String) totalDao
						.getObjectByCondition(
								"select addTime from WaigouPlan where type=? and addTime is not null and addTime <> ''  order by addTime ",
								type);
				years0 = addTime.substring(0, 4);
			}
			String sql = "SELECT  SUBSTRING(addTime FROM 1 FOR 4) years,COUNT(*) count,"
					+ " SUM(number) number ,SUM(number*hsPrice) money0 ,SUM(money) money1  "
					+ " FROM ta_sop_w_waigouplan where   "
					+ " number>0 and status not IN ('取消','删除','关闭') and type = '"
					+ type
					+ "' "
					+ sql_str
					+ " GROUP BY years HAVING years>='"
					+ years0 + "' and years<='" + years1 + "'";
			list0 = totalDao.findBySql(sql);
		}
		if (list0 != null && list0.size() > 0) {
			for (Map map : list0) {
				DuiZhangWgPlan dzwgPlan = new DuiZhangWgPlan();
				if (map.get("months") != null) {
					String months = (String) map.get("months");
					dzwgPlan.setMonths(months);
				}
				if (map.get("count") != null) {
					BigInteger count = (BigInteger) map.get("count");
					dzwgPlan.setCount(count.intValue());
				}
				if (map.get("number") != null) {
					Double number = (Double) map.get("number");
					dzwgPlan.setNumber(number.floatValue());
				}
				if (map.get("money0") != null) {
					Double money0 = (Double) map.get("money0");
					dzwgPlan.setMoney0(money0.floatValue());
				}
				if (map.get("money1") != null) {
					Double money1 = (Double) map.get("money1");
					dzwgPlan.setMoney1(money1.floatValue());
				}
				if (map.get("years") != null) {
					String years = (String) map.get("years");
					dzwgPlan.setYeras(years);
				}
				dzwgPlan.setType(type);
				dzwgPlanList.add(dzwgPlan);
			}
		}
		return dzwgPlanList;
	}

	private String getMaxPrintNumber(GoodsStore goodsStore) {
		// 入库时即生成打印单号
		String printNumber = (String) totalDao
				.getObjectByCondition(
						"select printNumber from GoodsStore where goodsStoreSendId = ? and "
								+ " printNumber is not null and printNumber <> '' and goodsStoreWarehouse =? ",
						goodsStore.getGoodsStoreSendId(), goodsStore
								.getGoodsStoreWarehouse());
		if (printNumber == null || "".equals(printNumber)) {
			String month = "";
			month = Util.getDateTime("yyyyMM");
			String beforestr = "WGRK";
			if ("外协库".equals(goodsStore.getGoodsStoreWarehouse())) {
				beforestr = "WWRK";
			} else if ("综合库".equals(goodsStore.getGoodsStoreWarehouse())) {
				beforestr = "FLRK";
			}
			String maxplanNumber = (String) totalDao.getObjectByCondition(
					" select max(printNumber) from GoodsStore where goodsStoreWarehouse =? "
							+ " and printNumber like '" + beforestr + month
							+ "%' ", goodsStore.getGoodsStoreWarehouse());
			if (maxplanNumber != null && !"".equals(maxplanNumber)) {
				maxplanNumber = (1000001 + Integer.parseInt(maxplanNumber
						.substring((beforestr+month).length())))
						+ "";
				maxplanNumber = maxplanNumber.substring(1);
			} else {
				maxplanNumber = "000001";
			}
			printNumber = beforestr +month+maxplanNumber;
		}
		return printNumber;
	}

	/***
	 * 零件时间分析
	 * 
	 * @param markid
	 *            件号
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	@Override
	public Map<String, Object> findmarkIdTime(String markid, String startTime,
			String endTime) {
		if (markid != null && markid.length() > 0) {
			List markidlist = null;
			if ("loadMarkid".equals(markid)) {
				String hql = "select markId from WaigouPlan where status='入库' GROUP BY markId ORDER BY id desc";
				markidlist = totalDao.findAllByPage(hql, 1, 30);
				if (markidlist != null && markidlist.size() > 0) {
					markid = (String) markidlist.get(0);
					markid = "1.03.11054";
				} else {
					return null;
				}
			}

			String hql = "select addTime,acArrivalTime,rukuTime from WaigouPlan "
					+ "where markId=? and status='入库'";
			if (startTime != null && startTime.length() > 0) {
				hql += " and addTime>'" + startTime + "'";
			}
			if (endTime != null && endTime.length() > 0) {
				hql += " and addTime<'" + endTime + "'";
			}
			List<Object[]> list = totalDao.query(hql, markid);
			if (list != null && list.size() > 0) {
				List datelist = new ArrayList();
				List caigoutimelist = new ArrayList();
				List rukutimelist = new ArrayList();

				float[] caigounums = new float[list.size()];
				float[] rukunums = new float[list.size()];
				int i = 0;
				for (Object[] objects : list) {
					String addTime = (String) objects[0];
					String acArrivalTime = (String) objects[1];
					String rukuTimefrom = (String) objects[2];
					datelist.add(addTime.substring(0, 10));
					try {
						// 采购时间（到货时间-订单添加时间）
						Long caigoutimes = Util.getDateDiff(addTime,
								"yyyy-MM-dd HH:mm:ss", acArrivalTime,
								"yyyy-MM-dd HH:mm:ss");
						Float caigouhours = Float.parseFloat(String.format(
								"%.2f", caigoutimes / 3600F));
						// 入库时间（入库时间-到货时间）
						Long rukutimes = Util.getDateDiff(acArrivalTime,
								"yyyy-MM-dd HH:mm:ss", rukuTimefrom,
								"yyyy-MM-dd HH:mm:ss");
						Float rukuhours = Float.parseFloat(String.format(
								"%.2f", rukutimes / 3600F));

						caigounums[i] = caigouhours;
						caigoutimelist.add(caigouhours);

						rukunums[i] = rukuhours;
						rukutimelist.add(rukuhours);

					} catch (Exception e) {
						e.printStackTrace();
					}
					i++;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				// 求中位数
				Float cgzws = Util.median(caigounums);
				Float rkzws = Util.median(rukunums);

				map.put("datelist", datelist);
				map.put("caigoutimelist", caigoutimelist);
				map.put("rukutimelist", rukutimelist);
				map.put("cgzws", cgzws);
				map.put("rkzws", rkzws);
				map.put("markids", markidlist);
				map.put("markid", markid);
				return map;
			}
		}
		return null;
	}

	/***
	 * 采购入库总时间分析
	 * 
	 * @return
	 */
	@Override
	public Map<String, Object> findcaigouAllTime() {
		String hql = "select addTime,rukuTime from WaigouPlan where type='外购' and status='入库' " +
				"and rukuTime is not null and rukuTime is not null and addTime>'2018-01-01 00:00:00'";
		List<Object[]> list = totalDao.query(hql);
		if (list != null && list.size() > 0) {
			List datelist = new ArrayList();
			List timelonglist = new ArrayList();
			List zwslist = new ArrayList();
			float[] nums = new float[list.size()];
			int i = 0;
			for (Object[] objects : list) {
				String atime = (String) objects[0];
				String btime = (String) objects[1];
				datelist.add(atime.substring(0, 10));
				try {
					Long times = Util.getDateDiff(atime, "yyyy-MM-dd HH:mm:ss",
							btime, "yyyy-MM-dd HH:mm:ss");
					Float hours = Float.parseFloat(String.format("%.2f",
							times / 3600F));
					timelonglist.add(hours);
					nums[i] = hours;
				} catch (Exception e) {
					e.printStackTrace();
					nums[i] = 0F;
				}
				i++;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			// 求中位数
			Float zws = Util.median(nums);
			for (float f : nums) {
				zwslist.add(zws);
			}
			map.put("timelong", timelonglist);
			map.put("yuefen", datelist);
			map.put("zws", zwslist);
			return map;
		}
		return null;
	}
	
	private Boolean ispower(Users users,String functionName){
		if(users==null){
			users = Util.getLoginUser();
		}
		int count  =0;
		if(functionName==null || "".equals(functionName)){
			HttpServletRequest request = ServletActionContext.getRequest();
			String servletPath = request.getServletPath();
			if(servletPath!=null && servletPath.length()>0){
				servletPath = servletPath.substring(1);
					count =	totalDao.getCount("from ModuleFunction where functionLink like '%"+servletPath+"%' and id in (" +
							" select m.id from ModuleFunction m join m.users u where u.id =? ) ", users.getId());
			}
		}else{
			count =	totalDao.getCount("from ModuleFunction where functionName = ? and id in (" +
					" select m.id from ModuleFunction m join m.users u where u.id =? ) ",functionName, users.getId());
		}
		
		
		return count>0?true:false;
	}

	@Override
	public String delwaigouDelivery(Integer id) {
		Users user = Util.getLoginUser();
		if(user == null){
			return "请先登录!~";
		}
		if(id!=null){
			WaigouDelivery wgdl =	(WaigouDelivery) totalDao.get(WaigouDelivery.class, id);
			if(!"待打印".equals(wgdl.getStatus()) && !"待确认".equals(wgdl.getStatus())){
				return "送货单状态为:"+wgdl.getStatus()+"不能删除!~";
			}
//			if(wgdl.getContacts().indexOf(user.getName())<0){
//				return "您的采购订单，不在此送货单上，您不能删除!~";
//			}
			Set<WaigouDeliveryDetail> wddSet =wgdl.getWddSet();
			String errormsg = "";
			for (WaigouDeliveryDetail wgdd : wddSet) {
				delWaigouDeliveryDetail(wgdd);
			}
			totalDao.delete(wgdl);
			return "删除成功";
		}
		return null;
	}
	private void  delWaigouDeliveryDetail(WaigouDeliveryDetail wgdd){
		WaigouPlan wgp = (WaigouPlan) totalDao.get(WaigouPlan.class, wgdd
				.getWaigouPlanId());
		wgp.setSyNumber(wgdd.getShNumber() + wgp.getSyNumber());
		if (wgp.getSyNumber().equals(wgp.getNumber())) {
			wgp.setStatus("生产中");
			List<WaigouPlan> wgpList = totalDao.query(
					" from WaigouPlan where waigouOrder.id = ?", wgp
							.getWaigouOrder().getId());
			if (wgpList != null && wgpList.size() == 1) {
				WaigouOrder wgo = wgp.getWaigouOrder();
				wgo.setStatus("生产中");
				totalDao.update(wgo);
			}
		}
	}
	private void recordGysBad(DefectiveProduct dp){
		if(dp!=null){
			WaigouDeliveryDetail wgdd =	(WaigouDeliveryDetail) totalDao.get(WaigouDeliveryDetail.class, dp.getWgddId());
			String str = "";
			if("外委".equals(wgdd.getType())){
				str = " and processNo ='"+wgdd.getProcessNo()+"'";
			}
			String hql_fs = "from FailureStatistics where markId = ? and selfcard = ?  "+str;
			FailureStatistics  fs =(FailureStatistics) totalDao.getObjectByCondition(hql_fs, wgdd.getMarkId(),wgdd.getExamineLot());
			if(fs!=null ){
				GysMarkIdCkolse gyscoles =	(GysMarkIdCkolse) totalDao.getObjectByCondition("from GysMarkIdCkolse where gysId =? and markId =? ", wgdd.getGysId(),wgdd.getMarkId());
				if(gyscoles == null){
					gyscoles = new GysMarkIdCkolse();
					gyscoles.setGysId(wgdd.getGysId());
					gyscoles.setGysCmp(wgdd.getGysName());
					gyscoles.setGysName(wgdd.getGysjc());
					gyscoles.setGysCode(wgdd.getWaigouDelivery().getUserCode());
					gyscoles.setMarkId(wgdd.getMarkId());
					gyscoles.setIsjytx(1);
					gyscoles.setIsClose(1);
				}
				Set<FailureStatisticsDetail> fsdSet = fs.getFsdSet();
				for (FailureStatisticsDetail fsd : fsdSet) {
					GysLaiLiaoQuanXianTj	gllqx = (GysLaiLiaoQuanXianTj) totalDao.getObjectByCondition("from GysLaiLiaoQuanXianTj where markId=? and gysCode =? and  qxCode =? ", 
							wgdd.getMarkId(),wgdd.getWaigouDelivery().getUserCode(),fsd.getCode());
					if(gllqx!=null){
						gllqx.setCount(gllqx.getCount()+1);
						if(gllqx.getCount()==2){
							//同一供应商，同一件号，同一缺陷代码，出现次数2次时，下次检验时提醒。
							gyscoles.setIsjytx(0);
						}else if(gllqx.getCount()>=3){
							//同一供应商，同一件号，同一缺陷代码，出现次数3次时，关闭该供应商该件号的送货功能。
							gyscoles.setIsClose(0);
						}
						gllqx.setUpdateTime(Util.getDateTime());
						totalDao.update(gllqx);
					}else{
						gllqx = new GysLaiLiaoQuanXianTj();
						gllqx.setMarkId(wgdd.getMarkId());
						gllqx.setGysCmp(wgdd.getGysName());
						gllqx.setGysName(wgdd.getGysjc());
						gllqx.setGysCode(wgdd.getWaigouDelivery().getUserCode());
						gllqx.setCount(1);
						gllqx.setWddId(wgdd.getId());
						gllqx.setDefId(dp.getId());
						gllqx.setQxCode(fsd.getCode());
						gllqx.setQxType(fsd.getType());
						gllqx.setYears(Util.getDateTime("yyyy"));
						
					}
				}
				GysLaiLiaoQuanXianTj gllqx =(GysLaiLiaoQuanXianTj) totalDao.getObjectByCondition("from GysLaiLiaoQuanXianTj where years=? and  jidu =? ", Util.getDateTime("yyyy"),Util.getQuarter(null, null));
				if(gllqx!=null){
					gllqx.setTuCount(gllqx.getTuCount()+1);
					if(gllqx.getCount()>=3){
						//同一供应商，同一件号，出现次数3次时，关闭该供应商该件号的送货功能。
						gyscoles.setIsClose(0);
					}
					if(gyscoles.getId()!=null){
						totalDao.update(gyscoles);
					}else{
						totalDao.save(gyscoles);
					}
					totalDao.update(gllqx);
				}else{
					gllqx = new GysLaiLiaoQuanXianTj();
					gllqx.setMarkId(wgdd.getMarkId());
					gllqx.setGysCmp(wgdd.getGysName());
					gllqx.setGysName(wgdd.getGysjc());
					gllqx.setGysCode(wgdd.getWaigouDelivery().getUserCode());
					gllqx.setCount(1);
					gllqx.setWddId(wgdd.getId());
					gllqx.setDefId(dp.getId());
					gllqx.setYears(Util.getDateTime("yyyy"));
					gllqx.setTuCount(1);
					gllqx.setJidu(Util.getQuarter(null, null));
					totalDao.save(gllqx);
				}
			}
		}
	}

	@Override
	public Object[] findAllGysMarkIdCkolse(GysMarkIdCkolse gysClose,Integer pageSize,Integer pageNo,String status) {
		if(gysClose==null){
			gysClose = new GysMarkIdCkolse();
		}
		String hql = totalDao.criteriaQueries(gysClose, null);
		List<GysMarkIdCkolse> gysCloseList =totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[]{gysCloseList,count};
	}

	@Override
	public String updateGysMarkIdCkolse(GysMarkIdCkolse gysClose) {
		if(gysClose!=null){
			GysMarkIdCkolse  old =	(GysMarkIdCkolse) totalDao.get(GysMarkIdCkolse.class, gysClose.getId());
			old.setIsClose(1);
			return totalDao.update(old)+"";
		}
		return null;
	}

	@Override
	public String sdjs(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null ){
			return "请先登录!";
		}
		WaigouPlan wp = (WaigouPlan) totalDao.getObjectById(WaigouPlan.class, id);
		if(wp==null){
			return "没有找到目标外委明细!";
		}
		if(!wp.getStatus().equals("设变锁定")){
			return "当前无需解锁!";
		}
		if(!user.getCode().equals(wp.getWaigouOrder().getAddUserCode())){
			return "对不起，您无权此操作!";
		}
		wp.setStatus(wp.getOldStatus());
		return totalDao.update(wp)+"";
	}

	@Override
	public String splitwwpCount(Integer id, int splitCount1, int splitCount2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user == null){
			return "请先登录!";
		}
		WaigouWaiweiPlan wwp = (WaigouWaiweiPlan) totalDao.getObjectById(WaigouWaiweiPlan.class, id);
		if(wwp.getStatus().equals("待激活")||wwp.getStatus().equals("待入库")
				||(wwp.getStatus().equals("设变锁定")&&(wwp.getOldStatus().equals("待激活")||wwp.getOldStatus().equals("待入库")))){
			if(splitCount1<=0){
				return "第一条数量异常请填写大于0的整数";
			}
			if(splitCount2<=0){
				return "第二条数量异常请填写大于0的整数";
			}
			int allcount = splitCount1 + splitCount2;
			if(wwp.getNumber()<allcount||wwp.getNumber()>allcount){
				return "拆分后总数量:"+allcount+",不等于原有总数量:"+wwp.getNumber()+",拆分失败!";
			}
			wwp.setNumber((float)splitCount1);
			List<ProcessInforWWProcard> pwwpList = totalDao.query("from ProcessInforWWProcard where wwxlId=?", id);
			if(pwwpList!=null&&pwwpList.size()>0){
				for(ProcessInforWWProcard pwwp:pwwpList){
					pwwp.setApplyCount(pwwp.getApplyCount()*splitCount1/allcount);
					pwwp.setHascount(pwwp.getHascount()*splitCount1/allcount);
					totalDao.update(pwwp);
				}
			}
			totalDao.update(wwp);
			WaigouWaiweiPlan wwp2 = new WaigouWaiweiPlan();
			BeanUtils.copyProperties(wwp, wwp2, new String[]{"id","baoxiaodans"});
			wwp2.setNumber((float)splitCount2);
			wwp2.setBeginCount((float)splitCount2);
			wwp2.setAddTime(Util.getDateTime());
			totalDao.save(wwp2);
			if(pwwpList!=null&&pwwpList.size()>0){
				for(ProcessInforWWProcard pwwp:pwwpList){
					ProcessInforWWProcard pwwp2 = new ProcessInforWWProcard();
					BeanUtils.copyProperties(pwwp, pwwp2, new String[]{"id"});
					pwwp2.setApplyCount(pwwp2.getApplyCount()*splitCount2/splitCount1);
					pwwp2.setHascount(pwwp2.getHascount()*splitCount2/splitCount1);
					pwwp2.setWwxlId(wwp2.getId());
					totalDao.save(pwwp2);
				}
			}
			return "true";
		}else{
			return "当前状态为:"+(wwp.getStatus().equals("设变锁定")?wwp.getOldStatus():wwp.getStatus())+"不能拆分!";
		}
	}
}