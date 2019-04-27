package com.task.ServerImpl.sys;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.task.entity.*;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.LeaveServer;
import com.task.Server.caiwu.core.CorePayableServer;
import com.task.Server.ess.GoodsServer;
import com.task.Server.ess.GoodsStoreServer;
import com.task.Server.sop.ManualOrderPlanServer;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.WaigouWaiweiPlanServer;
import com.task.Server.sys.CircuitRunServer;
import com.task.Server.zhaobiao.NianXiuServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.ShortMessageServiceImpl;
import com.task.ServerImpl.SmsServiceImpl;
import com.task.ServerImpl.WageServerImpl;
import com.task.ServerImpl.caiwu.receivePayment.ReceiptServerImpl;
import com.task.ServerImpl.ess.ProcardBlServerImpl;
import com.task.ServerImpl.fin.budget.SaleBudgetServerImpl;
import com.task.ServerImpl.menjin.AccessServerImpl;
import com.task.ServerImpl.peb.ProductEBServerImpl;
import com.task.action.UsersAction;
import com.task.action.xinxi.TwoDimensionCode;
import com.task.entity.approval.Sequences;
import com.task.entity.banci.BanCi;
import com.task.entity.banci.BanCiTime;
import com.task.entity.caiwu.CwPingZheng;
import com.task.entity.caiwu.baobiao.AccountsDate;
import com.task.entity.caiwu.core.CorePayable;
import com.task.entity.caiwu.core.MonthPayableBill;
import com.task.entity.caiwu.core.SupplierCorePayable;
import com.task.entity.caiwu.noncore.NonCorePayable;
import com.task.entity.caiwu.noncore.NonCoreReceivablesDetail;
import com.task.entity.caiwu.pz.CwCertificate;
import com.task.entity.caiwu.receivePayment.Receipt;
import com.task.entity.caiwu.receivePayment.ReceiptLog;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.dangan.ArchiveUnarchiverAplt;
import com.task.entity.dangan.DangAn;
import com.task.entity.expresscabinet.Courier;
import com.task.entity.fin.Escrow;
import com.task.entity.fin.EscrowMonth;
import com.task.entity.fin.budget.DeptMonthBudget;
import com.task.entity.fin.budget.SaleBudget;
import com.task.entity.fin.budget.SubBudgetRate;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.entity.kvp.ExecuteKVP;
import com.task.entity.menjin.Access;
import com.task.entity.menjin.AccessWebcam;
import com.task.entity.menjin.InEmployeeCarInfor;
import com.task.entity.menjin.Visit;
import com.task.entity.menjin.visitor.Visitor;
import com.task.entity.opinion.CustomerOpinion;
import com.task.entity.payee.Payee;
import com.task.entity.payment.FundApply;
import com.task.entity.payment.FundApplyDetailed;
import com.task.entity.payment.PaymentVoucher;
import com.task.entity.peb.PebBorrowAndLendLog;
import com.task.entity.peb.PebUsers;
import com.task.entity.peb.SubTeam;
import com.task.entity.project.QuotedPrice;
import com.task.entity.project.QuotedPriceCost;
import com.task.entity.project.QuotedPriceUserCost;
import com.task.entity.renshi.Dimission_ZhengYi;
import com.task.entity.seal.SealLog;
import com.task.entity.seal.SealLogType;
import com.task.entity.singlecar.SingleCar;
import com.task.entity.sop.BreakSubmit;
import com.task.entity.sop.DefectiveProduct;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.ManualOrderPlanTotal;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardCsBlOrder;
import com.task.entity.sop.ProcardProductRelation;
import com.task.entity.sop.ProcardSbWg;
import com.task.entity.sop.ProcardSbWgLog;
import com.task.entity.sop.ProcardTBanbenRelation;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardWxTuiLiao;
import com.task.entity.sop.ProcessFuLiaoTemplate;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforReceiveLog;
import com.task.entity.sop.ProcessInforWWApply;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessInforWWProcard;
import com.task.entity.sop.ProcessInfordeleteApply;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ReturnSingle;
import com.task.entity.sop.ReturnsDetails;
import com.task.entity.sop.SCTuiliaoSqDan;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouPlanclApply;
import com.task.entity.sop.YcWaiGouProcrd;
import com.task.entity.sop.muju.MouldApplyOrder;
import com.task.entity.sop.qd.LogoStickers;
import com.task.entity.sop.ycl.DosageTzDan;
import com.task.entity.sop.ycl.FenMoTzRecord;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.sop.ycl.YuanclAndWaigjAppli;
import com.task.entity.system.AuditNode;
import com.task.entity.system.CircuitCustomize;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.CompanyInfo;
import com.task.entity.system.ExecutionNode;
import com.task.entity.system.Option;
import com.task.entity.system.OptionRun;
import com.task.entity.system.SystemDemand;
import com.task.entity.systemfile.SystemFile;
import com.task.entity.zhuseroffer.NoPriceprocess;
import com.task.entity.zhuseroffer.Sample;
import com.task.entity.zhuseroffer.SumProcess;
import com.task.entity.zhuseroffer.ZhuserOffer;
import com.task.util.AESEnctypeUtil;
import com.task.util.Parent;
import com.task.util.PostUtil;
import com.task.util.RtxUtil;
import com.task.util.SortableField;
import com.task.util.Upload;
import com.task.util.Util;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.Gyscgbl;

/***
 * 定制流程
 * 
 * @author 刘培
 * 
 * 
 */
@SuppressWarnings( { "unchecked", "static-access" })
public class CircuitRunServerImpl implements CircuitRunServer {
	private static final Float SECONDS = 619200f;
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	private static GoodsStoreServer goodsStoreServer;
	private static GoodsServer goodsServer;
	private static LeaveServer leaveServer;
	private static ProcardServer procardServer;
	private static CorePayableServer corePayableServer;
	private static NianXiuServer nianXiuServer;
	private static WaigouWaiweiPlanServer wwpServer;

	public CorePayableServer getCorePayableServer() {
		return corePayableServer;
	}

	public void setCorePayableServer(CorePayableServer corePayableServer) {
		this.corePayableServer = corePayableServer;
	}

	public GoodsStoreServer getGoodsStoreServer() {
		return goodsStoreServer;
	}

	public void setGoodsStoreServer(GoodsStoreServer goodsStoreServer) {
		this.goodsStoreServer = goodsStoreServer;
	}

	public LeaveServer getLeaveServer() {
		return leaveServer;
	}

	public void setLeaveServer(LeaveServer leaveServer) {
		this.leaveServer = leaveServer;
	}

	private static ManualOrderPlanServer manualPlanServer;

	public ManualOrderPlanServer getManualPlanServer() {
		return manualPlanServer;
	}

	public void setManualPlanServer(ManualOrderPlanServer manualPlanServer) {
		this.manualPlanServer = manualPlanServer;
	}

	public NianXiuServer getNianXiuServer() {
		return nianXiuServer;
	}

	public void setNianXiuServer(NianXiuServer nianXiuServer) {
		this.nianXiuServer = nianXiuServer;
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	public static Float getSeconds() {
		return SECONDS;
	}

	public WaigouWaiweiPlanServer getWwpServer() {
		return wwpServer;
	}

	public void setWwpServer(WaigouWaiweiPlanServer wwpServer) {
		CircuitRunServerImpl.wwpServer = wwpServer;
	}

	public void setGoodsServer(GoodsServer goodsServer) {
		this.goodsServer = goodsServer;
	}

	public GoodsServer getGoodsServer() {
		return goodsServer;
	}

	/***
	 * 通过id查询执行流程
	 * 
	 * @param id
	 * @return
	 */
	public CircuitRun findCircuitRunById(Integer id) {
		if (id != null) {
			CircuitRun cr = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, id);
			if (cr != null) {
				if (cr.getEntityName().equals("AskForLeave")) {
					Integer xgepId = (Integer) totalDao
							.getObjectByCondition(
									"select epId from SingleCar where id=(select singleCarId from AskForLeave where id=?)",
									cr.getEntityId());
					if (xgepId != null) {
						cr.setXgepId(xgepId);
					}
				} else if (cr.getEntityName().equals("SingleCar")) {
					Integer xgepId = (Integer) totalDao.getObjectByCondition(
							"select epId from AskForLeave where singleCarId=?",
							cr.getEntityId());
					if (xgepId != null) {
						cr.setXgepId(xgepId);
					}
				}
			}

			return cr;
		}
		return null;
	}

	/***
	 * 查询epId对应的所有审批的节点
	 * 
	 * @param epId
	 * @return
	 */
	@Override
	public List findAllExNodeByEpId(Integer epId) {
		if (epId != null && epId > 0) {
			String hql = "from ExecutionNode where circuitRun.id=?  order by auditLevel,auditDateTime";
			return totalDao.query(hql, epId);
		}
		return null;
	}

	/****
	 * 通过执行流程Id查询当前登录用户是否应审核该流程
	 * 
	 * @param id
	 *            执行流程Id
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Override
	public ExecutionNode findAuditExeNode(Integer id) {
		Users loginUser = Util.getLoginUser();
		String hql = "from ExecutionNode where id = (select e.id from ExecutionNode e join e.circuitRun c  "
				+ "where c.allStatus not in ('同意','打回') and c.id=? and e.auditUserId=? "
				+ "and e.auditLevel=c.auditLevel  and e.auditStatus='未审批')";
		return (ExecutionNode) totalDao.getObjectByQuery(hql, id, loginUser
				.getId());
	}

	/****
	 * 通过执行流程Id查询当前登录用户所有可以审核的数据id集合
	 * 
	 * @param id
	 *            Integer[] 执行流程Id数组
	 * @return
	 */
	@Override
	public List findAuditExeNode(Integer[] id) {
		Users loginUser = Util.getLoginUser();
		String hql = "select min(auditLevel) from ExecutionNode where circuitRun.id=? and auditUserId=? and auditStatus='false'";
		return null;
	}

	/***
	 * 根据实体对象查询当前登录用户待审批的流程对应的对象id集合 (eg: Map map =
	 * CircuitRunServerImpl.findAuditExeNode(Template.class, false);)
	 * 
	 * @param classs
	 *            流程关联对象
	 * @param isHistory
	 *            是否查询历史(true/false)
	 * @return
	 */
	public static Map<String, Object> findAuditExeNode(Class classs,
			boolean isHistory) {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (classs != null) {
			String auditStatus = "=";
			if (isHistory) {
				auditStatus = "<>";
			}
			Users loginUser = Util.getLoginUser();
			// Users loginUser = (Users)
			// totalDao.getObjectByCondition("from Users where code='001'");
			String entityName = classs.getSimpleName();// 获得类名称
			String hql = "select c.entityId from ExecutionNode e join e.circuitRun c  where  e.auditUserId=? and e.auditStatus"
					+ auditStatus
					+ "'未审批' and c.entityName=? and c.allStatus not in ('同意','打回')";
			if (isHistory == false) {
				hql += " and e.auditLevel=c.auditLevel";
			}
			List<Integer> list = totalDao.query(hql, loginUser.getId(),
					entityName);
			if (list != null && list.size() > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("entityId", list);
				return map;
			}
			/***
			 * 返回值的使用范例
			 */
			// String hql2 = "from Template where id in (:entityId)";
			// List list2 = totalDao.find(hql2, map);
		}
		return null;
	}

	/***
	 * 根据实体对象查询当前登录用户待审批的流程对应的对象id集合 (eg: Map map =
	 * CircuitRunServerImpl.findAuditExeNode(Template.class, false);)
	 * 
	 * @param classs
	 *            流程关联对象
	 * @param isHistory
	 *            是否查询历史(true/false)
	 * @return
	 */
	public static List findAudit(boolean isHistory) {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		String auditStatus = "=";
		if (isHistory) {
			auditStatus = "<>";
		}
		Users loginUser = Util.getLoginUser();
		String hql = "from CircuitRun where id in ( select c.id from ExecutionNode e join e.circuitRun c  where  e.auditUserId=? and e.auditStatus"
				+ auditStatus + "'未审批'";
		if (isHistory == false) {
			hql += " and e.auditLevel=c.auditLevel";
		}
		hql += ")";
		hql += " order by addDateTime desc ";
		return totalDao.query(hql, loginUser.getId());
	}

	/***
	 * 查询谋层审批人Id
	 * 
	 * @param epId
	 *            流程Id
	 * @param auditLevel
	 *            审批等级
	 * @return
	 */
	public static Integer[] findAuditUserId(Integer epId, Integer auditLevel) {
		TotalDao totalDao = createTotol();
		String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=?";
		List list = totalDao.query(hql2, epId, auditLevel);
		if (list != null && list.size() > 0) {// 存在下一级人员
			Integer[] usersId = new Integer[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Integer userid = Integer.parseInt(list.get(i).toString());
				usersId[i] = userid;
			}
			return usersId;
		}
		return null;
	}

	/***
	 * 查询谋层审批人Id
	 * 
	 * @param epId
	 *            流程Id
	 * @param auditLevel
	 *            审批等级
	 * @return
	 */
	@Override
	public Integer[] findAuditUserIds(Integer epId, Integer auditLevel) {
		String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=?";
		List list = totalDao.query(hql2, epId, auditLevel);
		if (list != null && list.size() > 0) {// 存在下一级人员
			Integer[] usersId = new Integer[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Integer userid = Integer.parseInt(list.get(i).toString());
				usersId[i] = userid;
			}
			return usersId;
		}
		return null;
	}

	/****
	 * 审批处理
	 * 
	 * @param executionNode
	 * @return
	 */
	@Override
	public String updateAllExeNode(ExecutionNode executionNode,
			CircuitRun circuitRun, boolean nextMessage, String more,
			boolean isMessage) {
		if (executionNode != null) {
			// TotalDao totalDao = CircuitRunServerImpl.createTotol();
			// 计算审批时长
			if (executionNode.getStartDateTime() != null) {
				Long times;
				try {
					times = Util.getDateDiff(executionNode.getStartDateTime(),
							"yyyy-MM-dd HH:mm:ss", executionNode
									.getAuditDateTime(), "yyyy-MM-dd HH:mm:ss");
					Float hours = Float.parseFloat(String.format("%.2f",
							times / 3600F));
					executionNode.setTimeLong(hours);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			boolean bool = totalDao.update(executionNode);
			if (bool) {
				String auditStauts = executionNode.getAuditStatus();// 本条审批状态
				String allAuditStauts = "";// 总状态
				String zzOpinion = "";// 最终意见
				// 更新流程状态以及动态
				circuitRun.setAuditStatus(executionNode.getAuditUserName()
						+ auditStauts);// XXX同意(打回)
				String oldmessageIds = circuitRun.getMessageIds();
				if ("同意".equals(auditStauts)) {
					boolean bools = false;
					// 一层有一个人审批就行
					if ("oneAudit".equals(circuitRun.getAuditType())) {
						bools = true;

					} else {
						// 查询本级别是否存在未审批人员
						String hql = "from ExecutionNode where circuitRun.id=? and auditLevel=? and auditStatus='未审批' ";
						int count = totalDao.getCount(hql, circuitRun.getId(),
								executionNode.getAuditLevel());
						if (count > 0) {// 存在尚未审批的人员
							allAuditStauts = "审批中";
						} else {
							bools = true;
						}
					}
					if (bools) {
						// 查询下一级审批人员
						String hql2 = "from ExecutionNode where circuitRun.id=? and auditLevel=?";
						List list = totalDao.query(hql2, circuitRun.getId(),
								(executionNode.getAuditLevel() + 1));
						if (list != null && list.size() > 0) {// 存在下一级人员
							if (nextMessage) {
								// 计算个人审批标准时间
								String messageIds = "";
								for (int i = 0, len = list.size(); i < len; i++) {
									Float zws = 0F;
									ExecutionNode executionNode1 = (ExecutionNode) list
											.get(i);
									executionNode1
											.setStartDateTime(executionNode
													.getAuditDateTime());
									Integer[] usersId = { executionNode1
											.getAuditUserId() };

									// 查询该流程对应的审批记录
									String prolog = "from ExecutionNode  where auditUserId=? "
											+ " and circuitRun.name=? and timeLong is not null";

									Integer auditcount = totalDao.getCount(
											prolog, executionNode1
													.getAuditUserId(),
											circuitRun.getName());

									prolog += " order by timeLong";

									List getzws = new ArrayList();
									if (auditcount % 2 == 0) {// 偶数 （1/2+1）
										getzws = totalDao
												.findAllByPage(
														prolog,
														(int) Math
																.ceil(auditcount / 2 / 2),
														2,
														executionNode1
																.getAuditUserId(),
														circuitRun.getName());
									} else {// 奇数
										getzws = totalDao
												.findAllByPage(
														prolog,
														(int) Math
																.ceil(auditcount / 2),
														1,
														executionNode1
																.getAuditUserId(),
														circuitRun.getName());
									}
									if (getzws.size() > 0) {
										Float avgAuitTimeLong = 0F;
										for (int k = 0; k < getzws.size(); k++) {
											ExecutionNode executionNode_zws = (ExecutionNode) getzws
													.get(k);
											avgAuitTimeLong += executionNode_zws
													.getTimeLong();
										}

										zws = avgAuitTimeLong / getzws.size();
									}
									// 生成提醒消息
									messageIds += ","
											+ AlertMessagesServerImpl
													.addAlertMessages(
															"1",
															more
																	+ "   温馨提示:此流程您已审批【"
																	+ auditcount
																	+ "】次,您的标准审批时长为【" + zws + "】小时，辛苦了，相信您会更努力!",
															usersId,
															"CircuitRunAction_findAduitPage.action?id="
																	+ circuitRun
																			.getId(),
															isMessage);
								}
								circuitRun.setMessageIds(messageIds);
							}
							// 更新流程审核状态
							allAuditStauts = "审批中";
							// 更新流程审核等级
							circuitRun.setAuditLevel(executionNode
									.getAuditLevel() + 1);
						} else {
							// 不存在下一级人员，则为最后一个审批人员
							allAuditStauts = "同意";
							// 最后一个人的审批意见;
							zzOpinion = executionNode.getAuditOption();
						}

					}

				} else if ("打回".equals(auditStauts)) {
					if ("oneBack".equals(circuitRun.getAuditType())
							|| "oneAudit".equals(circuitRun.getAuditType())) {
						allAuditStauts = "打回";
					} else if ("lastBack".equals(circuitRun.getAuditType())) {
						/*** 判断是否为最后一个审批人员 */
						// 查询本级别是否存在未审批人员
						String hql = "from ExecutionNode where circuitRun.id=? and auditLevel=? and auditStatus='未审批' ";
						int count = totalDao.getCount(hql, circuitRun.getId(),
								executionNode.getAuditLevel());
						if (count > 0) {// 存在尚未审批的人员
							allAuditStauts = "审批中";
						} else {
							// 查询下一级审批人员
							String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=?";
							List list = totalDao.query(hql2,
									circuitRun.getId(), (executionNode
											.getAuditLevel() + 1));
							if (list != null && list.size() > 0) {// 存在下一级人员
								if (nextMessage) {
									Integer[] usersId = new Integer[list.size()];
									for (int i = 0; i < list.size(); i++) {
										Integer userid = Integer.parseInt(list
												.get(i).toString());
										usersId[i] = userid;
									}
									// 通知下层人员审核
									// 生成提醒消息
									String messageIds = AlertMessagesServerImpl
											.addAlertMessages(more, more,
													usersId,
													"CircuitRunAction_findAduitPage.action?id="
															+ circuitRun
																	.getId(),
													isMessage);
									circuitRun.setMessageIds(messageIds);
								}
								// 更新流程审核状态
								allAuditStauts = "审批中";
								// 更新流程审核等级
								circuitRun.setAuditLevel(executionNode
										.getAuditLevel() + 1);
							} else {
								// 不存在下一级人员，则为最后一个审批人员
								allAuditStauts = "打回";
							}

						}
					}
				}
				// 处理总状态
				circuitRun.setAllStatus(allAuditStauts);
				circuitRun.setZzopinion(zzOpinion);
				totalDao.update(circuitRun);

				// 处理回调状态
				if (circuitRun.getAllStatus().equals("同意")
						&& circuitRun.getHdStatus() != null
						&& circuitRun.getHdStatus().length() > 0) {
					allAuditStauts = circuitRun.getHdStatus();
				}
				if (circuitRun.getAllStatus().equals("打回")) {
					if ("外购减单处理申请".equals(circuitRun.getName())) {
						WaigouPlanclApply wgclApply = (WaigouPlanclApply) totalDao
								.getObjectById(WaigouPlanclApply.class,
										circuitRun.getEntityId());
						ProcardSbWg oldsbwg = (ProcardSbWg) totalDao
								.getObjectById(ProcardSbWg.class, wgclApply
										.getProcardSbWgId());
						WaigouPlan oldplan = (WaigouPlan) totalDao
								.getObjectById(WaigouPlan.class, wgclApply
										.getWaigouPlanId());
						ProcardSbWgLog psbwLog = (ProcardSbWgLog) totalDao
								.getObjectByCondition(
										"from ProcardSbWgLog where clApplyId=?",
										circuitRun.getEntityId());
						if (oldsbwg != null && oldplan != null
								&& psbwLog != null) {
							psbwLog.setStatus("打回");
							totalDao.update(psbwLog);
							if (!oldplan.getStatus().equals("取消")
									&& !oldplan.getStatus().equals("删除")) {
								oldplan.setStatus(oldplan.getOldStatus());
							}
							// 同时修改采购订单的状态
							WaigouOrder waigouordere = oldplan.getWaigouOrder();
							if (!"取消".equals(waigouordere.getStatus())
									&& !"删除".equals(waigouordere.getStatus())) {
								waigouordere.setStatus(waigouordere
										.getOldstatus());
							}
							totalDao.update(oldplan);
							totalDao.update(waigouordere);
						}

					}
					if ("FundApply".equals(circuitRun.getEntityName())) {
						FundApply fundApply = (FundApply) totalDao
								.getObjectById(FundApply.class, circuitRun
										.getEntityId());
						if (fundApply != null) {
							String about = fundApply.getAbout();
							Set<FundApplyDetailed> fadSet = fundApply
									.getFadSet();
							if (fadSet != null && fadSet.size() > 0) {
								for (FundApplyDetailed fundApplyDetailed : fadSet) {
									// 退回金额
									if (about.equals("冲账")) {
										FundApply jiankuai = (FundApply) totalDao
												.getObjectById(
														FundApply.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (jiankuai == null) {
											break;
										}
										if (jiankuai.getBackMoney() > fundApplyDetailed
												.getVoucherMoney()) {
											jiankuai.setBackMoney(jiankuai
													.getBackMoney()
													- fundApplyDetailed
															.getVoucherMoney());
											jiankuai.setStatus("未付清");
										} else {
											jiankuai.setBackMoney(0d);
											jiankuai.setStatus("待还款");
										}
										totalDao.update(jiankuai);
									} else if (about.equals("预算")) {
										DeptMonthBudget db = (DeptMonthBudget) totalDao
												.getObjectById(
														DeptMonthBudget.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (db == null) {
											continue;
										}
										if (db.getRealMoney() > fundApplyDetailed
												.getVoucherMoney()) {
											db.setRealMoney(db.getRealMoney()
													- fundApplyDetailed
															.getVoucherMoney());
										} else {
											db.setRealMoney(0d);
										}
										totalDao.update(db);
									} else if (about.equals("项目")) {
										QuotedPriceCost qpcost = (QuotedPriceCost) totalDao
												.getObjectById(
														QuotedPriceCost.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (qpcost == null) {
											continue;
										}
										if (qpcost.getBxMoney() > fundApplyDetailed
												.getVoucherMoney()) {
											qpcost.setBxMoney(qpcost
													.getBxMoney()
													- fundApplyDetailed
															.getVoucherMoney());
										} else {
											qpcost.setBxMoney(0d);
										}
										totalDao.update(qpcost);

									} else if (about.equalsIgnoreCase("质量")) {
										CustomerOpinion option = (CustomerOpinion) totalDao
												.getObjectById(
														CustomerOpinion.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (option == null) {
											continue;
										}
										if (option.getApplyMoney() > fundApplyDetailed
												.getVoucherMoney()) {
											option.setApplyMoney(option
													.getApplyMoney()
													- fundApplyDetailed
															.getVoucherMoney());
										} else {
											option.setApplyMoney(0d);
										}
										totalDao.update(option);
									} else if (about.equalsIgnoreCase("KVP")) {
										ExecuteKVP kvp = (ExecuteKVP) totalDao
												.getObjectById(
														ExecuteKVP.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (kvp == null) {
											continue;
										}
										if (kvp.getBxMoney() > fundApplyDetailed
												.getVoucherMoney()) {
											kvp.setBxMoney(kvp.getBxMoney()
													- fundApplyDetailed
															.getVoucherMoney());
										} else {
											kvp.setBxMoney(0d);
										}
										totalDao.update(kvp);
									} else if (about.equalsIgnoreCase("外购(历史)")) {
										GoodsStore gs = (GoodsStore) totalDao
												.getObjectById(
														GoodsStore.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (gs == null) {
											continue;
										}
										gs.setBaoxiao_status("未报完");
										totalDao.update(gs);
									} else if (about.equalsIgnoreCase("外委")
											|| about.equalsIgnoreCase("外购")) {
										WaigouPlan wgPlan = (WaigouPlan) totalDao
												.getObjectById(
														WaigouPlan.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										wgPlan.setHasjk("否");
										wgPlan.setYfMoney(wgPlan.getYfMoney()
												- fundApplyDetailed
														.getVoucherMoney());
										totalDao.update(wgPlan);
									} else if (fundApply.getAbout()
											.equalsIgnoreCase("非主营业务应付")) {
										NonCorePayable np = (NonCorePayable) totalDao
												.getObjectById(
														NonCorePayable.class,
														fundApplyDetailed
																.getDeptMonthBudgetID());
										if (np != null) {
											if (np.getRealfukuanJIne() != null) {
												if (fundApplyDetailed
														.getVoucherMoney() > np
														.getRealfukuanJIne()) {
													np.setRealfukuanJIne(0f);
												} else {
													np
															.setRealfukuanJIne((float) (np
																	.getRealfukuanJIne() - fundApplyDetailed
																	.getVoucherMoney()));
												}
											}
											totalDao.update(np);
										}
									}
								}
							}
							fundApply.setStatus("打回");
						}
					}
					if ("委托付款".equals(circuitRun.getName())) {
						Escrow e = (Escrow) totalDao.getObjectById(
								Escrow.class, circuitRun.getEntityId());
						e.setSpTime(Util.getDateTime());
						totalDao.update(e);
						if (e.getSourceEntity() != null
								&& e.getSourceEntity().equals("ReceiptLog")) {
							ReceiptLog fa = (ReceiptLog) totalDao
									.getObjectByCondition(
											"from ReceiptLog where id =(select zijinshyongID from Escrow where id=? )",
											circuitRun.getEntityId());
							if (fa != null) {
								fa.setHaswt("是");
								totalDao.update(fa);
							}
						} else {
							FundApply fa = (FundApply) totalDao
									.getObjectByCondition(
											"from FundApply where id =(select zijinshyongID from Escrow where id=? )",
											circuitRun.getEntityId());
							if (fa != null) {
								fa.setHaswt("是");
								totalDao.update(fa);
							}
						}

					}
					if ("委托付款月度汇总申请".equals(circuitRun.getName())) {
						EscrowMonth em = (EscrowMonth) totalDao.getObjectById(
								EscrowMonth.class, circuitRun.getEntityId());
						em.setSpTime(Util.getDateTime());
						totalDao.update(em);
					}
					if ("内部计划审批".equals(circuitRun.getName())) {
						List<Product_Internal> piList = totalDao
								.query(
										"from Product_Internal where ioDetailId in (select id from InternalOrderDetail where internalOrder.id=?)",
										circuitRun.getEntityId());
						if (piList != null && piList.size() > 0) {
							for (Product_Internal pi : piList) {
								pi.setStatus("打回");
								totalDao.update(pi);
							}
						}
					}
					if ("委外预选申请".equals(circuitRun.getName())) {
						// TODO Auto-generated method stub
						ProcessInforWWApply apply = (ProcessInforWWApply) totalDao
								.getObjectById(ProcessInforWWApply.class,
										circuitRun.getEntityId());
						Users login = Util.getLoginUser();
						if (apply != null) {
							// 当含有未匹配到合同的明细时不生成采购订单
							apply.setProcessStatus("预选打回");
							apply.setStatus("打回");
							apply.setShenpiTime(Util
									.getDateTime("yyyy-MM-dd HH:mm:ss"));
							totalDao.update(apply);
							Set<ProcessInforWWApplyDetail> detailSet = apply
									.getProcessInforWWApplyDetails();
							if (detailSet != null) {
								for (ProcessInforWWApplyDetail detail : detailSet) {
									if (detail.getDataStatus() != null
											&& (detail.getDataStatus().equals(
													"删除") || detail
													.getDataStatus().equals(
															"取消"))) {
										continue;
									}
									Procard procard = (Procard) totalDao
											.getObjectById(Procard.class,
													detail.getProcardId());
									if (procard != null
											&& detail.getProcessNOs() != null
											&& detail.getProcessNOs().length() > 0) {
										String[] processNOS = detail
												.getProcessNOs().split(";");
										if (processNOS != null
												&& processNOS.length > 0) {
											for (String processNOStr : processNOS) {
												try {
													Integer processNO = Integer
															.parseInt(processNOStr);
													ProcessInfor process = (ProcessInfor) totalDao
															.getObjectByCondition(
																	"from ProcessInfor where processNO=? and (dataStatus is null or dataStatus!='删除') and procard.id=?",
																	processNO,
																	procard
																			.getId());
													if (process != null) {// 将外委打回数量回传到工序上
														// if (process
														// //不处理，再次申请时在处理
														// .getSelectWwCount()
														// ==
														// null) {
														// process
														// .setSelectWwCount(detail
														// .getApplyCount());
														// } else {
														// process
														// .setSelectWwCount(process
														// .getSelectWwCount()
														// + detail
														// .getApplyCount());
														// }
														process
																.setApplyWwCount(process
																		.getApplyWwCount()
																		- detail
																				.getApplyCount());
														totalDao
																.update(process);
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									// 还原下层被占数量
									if (detail.getWwType() != null
											&& detail.getWwType()
													.equals("包工包料")) {// 包工包料回传采购
										List<ProcessInforWWProcard> processwwprocardList = totalDao
												.query(
														"from ProcessInforWWProcard where applyDtailId=?",
														detail.getId());
										if (processwwprocardList != null
												&& processwwprocardList.size() > 0) {
											for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
												Procard wgProcard = (Procard) totalDao
														.getObjectById(
																Procard.class,
																processInforWWProcard
																		.getProcardId());
												if (wgProcard != null) {
													wgProcard
															.setWwblCount(wgProcard
																	.getWwblCount()
																	- detail
																			.getApplyCount()
																	* wgProcard
																			.getQuanzi2()
																	/ wgProcard
																			.getQuanzi1());
													if (wgProcard
															.getWwblCount() < 0) {
														wgProcard
																.setWwblCount(0f);
													}
													totalDao.update(wgProcard);
												}
											}
										}
										// 归还下层半成品,自制件和组合（下层组合将整体被包公包料）
										backProcardWwblCount(procard, detail
												.getApplyCount(), 0);
									}

								}
							}

						} else {
							return "没有找到目标!";
						}

					}

					// 物料需求取消申请打回
					if ("物料需求变更申请".equals(circuitRun.getName())) {
						ManualOrderPlan plan = (ManualOrderPlan) totalDao
								.getObjectById(ManualOrderPlan.class,
										circuitRun.getEntityId());
						plan.setCancalNum(null);
						totalDao.update(plan);
					}
					if ("加班审批流程".equals(circuitRun.getName())
							|| "主管加班审批流程".equals(circuitRun.getName())) {
						List<OvertimeDetail> list = totalDao.query(
								"from OvertimeDetail where overtime.id=? ",
								circuitRun.getEntityId());
						Overtime overtime = (Overtime) totalDao.getObjectById(
								Overtime.class, circuitRun.getEntityId());
						Integer xiuxi = 0;
						Integer totalMinutes = 0;
						for (OvertimeDetail overtimeDetail : list) {
							// oldStart 有值表示的是历史明细
							// startTime 有值表示的是新的明细
							String startTime = overtimeDetail.getStartTime();
							String endTime = overtimeDetail.getEndTime();
							if (startTime != null && endTime != null
									&& !startTime.equals("")
									&& !endTime.equals("")) {
								// 新的数据
								totalDao.delete(overtimeDetail);
							} else {
								// 旧数据还原
								if (overtimeDetail.getXiuxi() != null) {
									xiuxi += overtimeDetail.getXiuxi();
								}
								if (overtimeDetail.getHour() != null
										&& overtimeDetail.getHour() > 0) {
									totalMinutes += overtimeDetail.getHour() * 60;
								}
								if (overtimeDetail.getMinutes() != null) {
									totalMinutes += overtimeDetail.getMinutes();
								}

								overtimeDetail.setStartTime(overtimeDetail
										.getOldStart());
								overtimeDetail.setEndTime(overtimeDetail
										.getOldEnd());
								overtimeDetail.setOldStart(null);
								overtimeDetail.setOldEnd(null);
								totalDao.update(overtimeDetail);
							}
						}
						overtime.setOverTimeLong(totalMinutes / 60f);
						if (overtime.getHuanxiu() != null
								&& overtime.getHuanxiu().equals("是")) {
							overtime
									.setUsablehxTime(overtime.getOverTimeLong());
						}
						overtime.setXiuxi(xiuxi);
						totalDao.update(overtime);
					}
				}
				// 更新对应实体状态
				if (circuitRun.getEntityName() != null
						&& circuitRun.getEntityName().length() > 0) {
					String hql = "update " + circuitRun.getEntityName()
							+ " set " + circuitRun.getEntityStatusName()
							+ "=? where " + circuitRun.getEntityIdName() + "=?";
					try {
						totalDao.createQueryUpdate(hql, null, allAuditStauts,
								circuitRun.getEntityId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 设置消息为已读
				if (oldmessageIds != null && oldmessageIds.length() > 0) {
					oldmessageIds = "'" + oldmessageIds.replaceAll(",", "','")
							+ "'";
					String hql = "update AlertMessages set readStatus='yes' where id in ("
							+ oldmessageIds + ") and receiveuserId=?";
					try {
						totalDao.createQueryUpdate(hql, null, Util
								.getLoginUser().getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 处理审批中的逻辑Lc_add
				if ("审批中".equals(circuitRun.getAllStatus())) {
					/** 请假 加班申请审批中生成进出门凭证流程--李聪 **/
					// 请假
					AskForLeave(circuitRun, totalDao);
					// 加班
					OverTime(circuitRun, totalDao);
					// 来访申请
					Visit(circuitRun, totalDao);
				}
				// 审批全部同意后的回调逻辑
				if (circuitRun.getAllStatus().equals("同意")) {
					// 审批同意后统一处理方法，请勿在这里继续添加
					updateAuditAgree(circuitRun, totalDao);
				} else {
					// //打回
					// if("QuotedPriceUserCost".equals(circuitRun
					// .getEntityName())){
					// QuotedPriceUserCost qpuc = (QuotedPriceUserCost)
					// totalDao.getObjectById(QuotedPriceUserCost.class,
					// circuitRun.getEntityId());
					// if(qpuc!=null){
					// Investor investor = (Investor)
					// totalDao.getObjectById(Investor.class,
					// qpuc.getInvestorId());
					// if(investor!=null){
					// investor.setDjMoney(investor.getDjMoney()-qpuc.getTzMoney());
					// investor.setKyMoney(investor.getKyMoney()+qpuc.getTzMoney());
					// }
					// }
					// }
					//
					if ((circuitRun.getAllStatus().equals("打回"))) {
						// 打回
						if ("文件管控批准流程".equals(circuitRun.getName())
								|| "文件编号作废审批流程".equals(circuitRun.getName())) {
							SystemFile systemFile = (SystemFile) totalDao
									.getObjectById(SystemFile.class, circuitRun
											.getEntityId());
							if ("文件编号作废审批流程".equals(circuitRun.getName())) {
								systemFile.setStatus("作废打回");
							} else {
								systemFile.setStatus("打回");
							}
							totalDao.update(systemFile);
						}

						if ("手工出库单审核".equals(circuitRun.getName())) {
							Sell sell = (Sell) totalDao.getObjectById(
									Sell.class, circuitRun.getEntityId());
							Integer handwordSellNumber = sell
									.getHandwordSellNumber();
							List<Sell> list = totalDao.query(
									"from Sell where handwordSellNumber=?",
									handwordSellNumber);
							for (Sell sell2 : list) {
								sell2.setHandwordSellStatus("打回");
								totalDao.update(sell2);
							}

						}

						if ("文件受控反审流程".equals(circuitRun.getName())) {
							SystemFile systemFile = (SystemFile) totalDao
									.getObjectById(SystemFile.class, circuitRun
											.getEntityId());
							if (systemFile != null) {
								if ("同意".equals(circuitRun.getAllStatus())) {
									systemFile.setStatus("反审成功");
								} else if ("打回".equals(circuitRun
										.getAllStatus())) {
									systemFile.setStatus("反审失败");
								}
								totalDao.update(systemFile);
							}
						}

						if("访客申请".equals(circuitRun.getName())) {
							Visitor visitor = (Visitor) totalDao.getObjectById(Visitor.class, circuitRun.getEntityId());
							if(visitor!=null) {
								Integer followId = visitor.getFollowId();
								if(followId!=null) {
									List<Visitor> list = totalDao.query("from Visitor where followId = ?", followId);
									for (Visitor visitor2 : list) {
										visitor2.setVisitorStatus("拒绝访客");
										totalDao.update(visitor2);
									}
								}
								StringBuffer contentBuffer = new StringBuffer();
								contentBuffer.append("尊敬的"+visitor.getVisitorName()+"，您好！\n");
								contentBuffer.append("您的访客申请被拒绝，");
								String message  = executionNode.getAuditOpinion();//审批意见
								if(message!=null && !message.equals("")) {
									contentBuffer.append("备注信息："+message+"。");
								}
								contentBuffer.append("请联系被访人，谢谢。");
								ShortMessageServiceImpl shortMessageServer=new ShortMessageServiceImpl();
								shortMessageServer.send(visitor.getVisitorPhone(), contentBuffer.toString());
							}
						}

						if ("成品退货申请".equals(circuitRun.getName())) {
							ChengPinTuiHuo cptu = (ChengPinTuiHuo) totalDao
									.get(ProductManager.class, circuitRun
											.getEntityId());
							Sell sell = (Sell) totalDao.get(Sell.class, cptu
									.getSellId());
							sell.setTksellCount(sell.getTksellCount()
									- cptu.getSqNum());
							totalDao.update(sell);
						}
                        
						if ("快递员注册流程".equals(circuitRun.getName())) {
							Courier courier = (Courier) totalDao.getObjectById(
									Courier.class, circuitRun.getEntityId());
							if (courier != null) {
								String Scode = "尊敬的"+courier.getCouName()+"您好!您申请我们的快递柜投放因证件不清晰,未通过审核,请上传清晰证件照,保证审核成功率";  //加上注册信息链接
								String return_code = new ShortMessageServiceImpl().send(courier.getPhoneNumber(), Scode);
								System.out.println("输出一下审批未通过后短信是否发送"+return_code);
								totalDao.update(courier);
							}
						}
					} else if (circuitRun.getAllStatus().equals("审批中")) {
						if ("手工出库单审核".equals(circuitRun.getName())) {
							Sell sell = (Sell) totalDao.getObjectById(
									Sell.class, circuitRun.getEntityId());
							Integer handwordSellNumber = sell
									.getHandwordSellNumber();
							List<Sell> list = totalDao.query(
									"from Sell where handwordSellNumber=?",
									handwordSellNumber);
							for (Sell sell2 : list) {
								sell2.setHandwordSellStatus("审批中");
								totalDao.update(sell2);
							}

						}
					}

				}
				// if (circuitRun.getAllStatus().equals("同意")
				// || circuitRun.getAllStatus().equals("打回")) {
				// 给申请人，发送消息提醒
				AlertMessagesServerImpl.addAlertMessages("流程审批提醒", "您的"
						+ circuitRun.getName() + ",审批状态:"
						+ circuitRun.getAllStatus() + ",最新动态:"
						+ circuitRun.getAuditStatus() + "。请注意查看!",
						new Integer[] { circuitRun.getAddUserId() },
						"CircuitRunAction_findAduitPage.action?id="
								+ circuitRun.getId(), true, "yes");
				// }
			}
		}
		return "审批成功";
	}

	public static String updateAuditAgree(CircuitRun circuitRun,
			TotalDao totalDao) {
		/** 奖金分配审批后生成工资信息--刘培 **/
		if ("Bonusmoney".equals(circuitRun.getEntityName())) {
			String hql_bonusmoney = "select addUserId from Bonusmoney where id=?";
			Object obj = totalDao.getObjectByQuery(hql_bonusmoney, circuitRun
					.getEntityId());
			if (obj != null) {
				int addUserId = Integer.parseInt(obj.toString());
				WageServerImpl wsImpl = new WageServerImpl();
				wsImpl.setTotalDao(totalDao);
				String message = wsImpl.wageBalance(addUserId);
			}
		}
		/** 奖金分配审批后生成工资信息结束 **/
		/** 月度销售收入审核同意后生成预算科目金额信息--刘培 **/
		if ("SaleBudget".equals(circuitRun.getEntityName())) {
			SaleBudget sb = (SaleBudget) totalDao.getObjectById(
					SaleBudget.class, circuitRun.getEntityId());
			if (sb != null) {
				SaleBudgetServerImpl sbImpl = new SaleBudgetServerImpl();
				sbImpl.setTotalDao(totalDao);
				boolean bool_sb = sbImpl.subSaleBudget(sb);
				// System.out.println(bool_sb);
			}
		}
		/** 月度销售收入审核同意后生成预算科目金额信息结束--刘培 **/

		/** 年度预算审核 **/
		if ("Yusuantianbaobiao".equals(circuitRun.getEntityName())) {
			Yusuantianbaobiao yusuantianbaobiao = (Yusuantianbaobiao) totalDao
					.getObjectById(Yusuantianbaobiao.class, circuitRun
							.getEntityId());
			Yusuantianbaototal yusuantianbaototal = yusuantianbaobiao
					.getYusuantianbaototal();
			yusuantianbaototal.setZongshu(yusuantianbaototal.getZongshu()
					+ yusuantianbaobiao.getZongjine());
			yusuantianbaototal.setShengyu(yusuantianbaototal.getShengyu()
					+ yusuantianbaobiao.getZongjine());
			totalDao.update(yusuantianbaototal);
		}

		/** 审批后借款后续操作 --毛小龙 **/
		if ("PaymentVoucher".equals(circuitRun.getEntityName())) {
			paymentVoucher(circuitRun, totalDao);
		}
		/** 审批后借款后续操作结束 **/
		// 资金使用申请
		if ("资金使用申请".equals(circuitRun.getName())
				|| "FundApply".equals(circuitRun.getEntityName())
				|| "应付登记预付申请".equals(circuitRun.getName())
				|| "应付登记支付申请".equals(circuitRun.getName())
				|| "大额应付登记预付申请".equals(circuitRun.getName())
				|| "大额应付登记支付申请".equals(circuitRun.getName())
				|| "集团应付登记预付申请".equals(circuitRun.getName())
				|| "集团应付登记支付申请".equals(circuitRun.getName())) {
			fundApply(circuitRun, totalDao);
		}
		/** bom版本升级--唐晓斌 **/
		if ("ProcardTemplateBanBenApply".equals(circuitRun.getEntityName())) {
			// String msg = procardTemBanBenLvup(circuitRun
			// .getEntityId());
		}
		if ("订单申请审核".equals(circuitRun.getName())
				|| "试制订单申请审核".equals(circuitRun.getName())
				|| "售后订单申请审核".equals(circuitRun.getName())) {
			String msg = orderChongxiao(circuitRun.getEntityId());
		}
		if ("BOM设变申请".equals(circuitRun.getName())) {
			String msg = procardTemSb(circuitRun.getEntityId(), totalDao);
		}
		if ("委外预选申请".equals(circuitRun.getName())) {
			String msg = gxww(circuitRun.getEntityId(), totalDao);
		}
		if ("外购减单处理申请".equals(circuitRun.getName())) {
			String msg = wgjd(circuitRun.getEntityId(), totalDao);
		}
		if ("手工外委明细删除申请".equals(circuitRun.getName())) {
			String msg = deletesgwwdetail(circuitRun.getEntityId(),null, totalDao);
		}
		if ("手工外委删除申请".equals(circuitRun.getName())) {
			String msg = deletesgww(circuitRun.getEntityId(), totalDao);
		}
		if ("委托付款".equals(circuitRun.getName())) {
			Escrow e = (Escrow) totalDao.getObjectById(Escrow.class, circuitRun
					.getEntityId());
			e.setSpTime(Util.getDateTime());
			totalDao.update(e);
			FundApply fa = (FundApply) totalDao
					.getObjectByCondition(
							"from FundApply where id =(select zijinshyongID from Escrow where id=? )",
							circuitRun.getEntityId());
			if (fa != null) {
				fa.setStatus("关闭");
				totalDao.update(fa);
			}
		}
		if ("刷新价格审批流程".equals(circuitRun.getName())
				|| "刷新价格(外购/辅料)审批流程".equals(circuitRun.getName())
				|| "刷新价格(外委)审批流程".equals(circuitRun.getName())) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao.get(WaigouPlan.class,
					circuitRun.getEntityId());
			String time = Util.getDateTime("yyyy-MM-dd");
			String hql_price = "from Price where partNumber=? and gysId =? and '"
					+ time
					+ "'>= pricePeriodStart and ('"
					+ time
					+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
			if ("外购".equals(waigouplan.getType()) || "辅料".equals(waigouplan.getType())) {
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
				hql_price += " and gongxunum = '" + waigouplan.getProcessNOs()
						+ "'";
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
			// if (price == null) {
			// return "未找到件号:" + waigouplan.getMarkId() + "供应商为"
			// + waigouplan.getGysName() + " ，"
			// + waigouplan.getNumber() + "数量内的阶梯单价，未找到！";
			// }
			// Float oldprice = waigouplan.getHsPrice();
			if (price != null) {
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
				// 同步刷新送货单明细上的价格。
				List<WaigouDeliveryDetail> wgddList = totalDao.query(
						" from WaigouDeliveryDetail where waigouPlanId =? ",
						waigouplan.getId());
				if (wgddList != null && wgddList.size() > 0) {
					for (WaigouDeliveryDetail wgdd : wgddList) {
						wgdd.setHsPrice(waigouplan.getHsPrice());
						wgdd.setPrice(waigouplan.getPrice());
						wgdd.setTaxprice(waigouplan.getTaxprice());
						totalDao.update(wgdd);
						// 刷新入库记录上的单价
						List<GoodsStore> gsList = totalDao
								.query(
										" from GoodsStore where wwddId =? and goodsStoreMarkId =?",
										wgdd.getId(), wgdd.getMarkId());
						if (gsList != null && gsList.size() > 0) {
							for (GoodsStore gs : gsList) {
								gs.setHsPrice(wgdd.getHsPrice()==null?null:wgdd.getHsPrice().doubleValue());
								gs.setGoodsStorePrice(wgdd.getPrice()==null?null:wgdd.getPrice().doubleValue());
								gs.setTaxprice(wgdd.getTaxprice());
								gs.setMoney(gs.getHsPrice()
										* gs.getGoodsStoreCount());
								totalDao.update(gs);
							}
						}
						// 刷新库存上的单价
						List<Goods> gList = totalDao
								.query(
										" from Goods where goodsMarkId=? and goodsLotId =? ",
										wgdd.getMarkId(), wgdd.getExamineLot());
						if (gList != null && gList.size() > 0) {
							for (Goods g : gList) {
								AccountsDate accountsDate = (AccountsDate) totalDao
										.getObjectByCondition(" from AccountsDate");
								Double avghsprice = wgdd.getHsPrice();
								Double avgbhsprice = wgdd.getPrice();
								if (accountsDate != null) {
									String hql_banben = "";
									if (g.getBanBenNumber() != null
											&& g.getBanBenNumber().length() > 0) {
										hql_banben = " and banBenNumber='"
												+ g.getBanBenNumber() + "'";
									}
									// 移动加权平均，计算所有入库记录。求平均价格
									if ("allAgv".equals(accountsDate
											.getGoodsType())) {
										String hql_allAgv = "select sum(goodsStoreCount*hsPrice)/sum(goodsStoreCount),sum(goodsStoreCount*goodsStorePrice)/sum(goodsStoreCount) from GoodsStore "
												+ "where goodsStoreMarkId=? and goodsStoreWarehouse=? and hsPrice is not null "
												+ hql_banben;

										List<Object[]> list = totalDao.query(
												hql_allAgv, g.getGoodsMarkId(),
												g.getGoodsClass());
										Object[] obj = list.get(0);
										if (obj != null && obj[0] != null) {
											avghsprice = Double
													.parseDouble(obj[0]
															.toString());
											avgbhsprice = Double
											.parseDouble(obj[1]
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
												+ accountsDate.getJihao()
												+ "' ";
										List<Object[]> list = totalDao.query(
												hql_allAgv, g.getGoodsMarkId(),
												g.getGoodsClass());
										Object[] obj = list.get(0);
										if (obj != null && obj[0] != null) {
											try {
												avghsprice = Double.parseDouble(obj[0]
																.toString());
												avgbhsprice = Double.parseDouble(obj[1]
																.toString());
											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									}
								}
								g.setGoodsPrice(avgbhsprice.floatValue());
								g.setGoodshsPrice(avghsprice.floatValue());
								g.setGoodsBuyPrice(wgdd.getHsPrice().floatValue());
								g.setGoodsBuyBhsPrice(wgdd.getPrice().floatValue());
								g.setTaxprice(wgdd.getTaxprice().floatValue());
								totalDao.update(g);
							}
						}
						// 刷新出库记录单价
						List<Sell> sellList = totalDao
								.query(
										" from Sell where sellMarkId =? and sellLot =? ",
										wgdd.getMarkId(), wgdd.getExamineLot());
						if (sellList != null && sellList.size() > 0) {
							for (Sell sell : sellList) {
								Goods g = (Goods) totalDao
										.getObjectByCondition(
												" from Goods where  goodsMarkId=? and goodsLotId =? and goodsClass = ? ",
												sell.getSellMarkId(), sell
														.getSellLot(), sell
														.getSellWarehouse());
								sell.setGoodsPrice(g.getGoodsPrice());
								sell.setSellPrice(wgdd.getHsPrice().floatValue());
								sell.setSellbhsPrice(wgdd.getPrice().floatValue());
								sell.setTaxprice(wgdd.getTaxprice()
										.floatValue());
								totalDao.update(sell);
							}
						}
					}
				}
				// 刷新对账单上的单价
				List<CorePayable> cpList = totalDao
						.query(
								" from CorePayable where orderNumber=? and zhaiyao =? ",
								wgorder.getPlanNumber(), waigouplan.getMarkId());
				if (cpList != null && cpList.size() > 0) {
					for (CorePayable cp : cpList) {
						cp.setPriceId(waigouplan.getPriceId());
						cp.setHsPrice(waigouplan.getHsPrice());
						cp.setBhsPrice(waigouplan.getPrice());
						cp.setYingfukuanJine(waigouplan.getHsPrice()
								* cp.getNumber());
						totalDao.update(cp);
					}
				}
				totalDao.update(waigouplan);
				totalDao.update(wgorder);

			}
		}
		if ("样品审批流程".equals(circuitRun.getName())) {
			ZhuserOffer zhuserOffer = (ZhuserOffer) totalDao
					.getObjectByCondition("from ZhuserOffer where id =?",
							circuitRun.getEntityId());
			zhuserOffer.getYuanclAndWaigj().setPricestatus("同意");
		}
		if ("样品(工序)审批流程".equals(circuitRun.getName())) {
			Sample sample = (Sample) totalDao.getObjectByCondition(
					"from Sample where id =?", circuitRun.getEntityId());
			sample.getZhuserOffer().setStatus("同意");
			NoPriceprocess no = (NoPriceprocess) totalDao.get(
					NoPriceprocess.class, sample.getZhuserOffer()
							.getProcessId());
			no.setStutas("同意");
			totalDao.update(no);
			if (no.getSumProcessId() != null) {
				String hql_np = "select stutas from NoPriceprocess where sumProcessId= ? group by stutas";
				List<String> hql_zh = totalDao.query(hql_np, no
						.getSumProcessId());
				if (hql_zh.size() == 1 && hql_zh.get(0).equals("同意")) {
					SumProcess sp = (SumProcess) totalDao.get(SumProcess.class,
							no.getSumProcessId());
					sp.setStutas("同意");
				}
			}
			SumProcess sp = (SumProcess) totalDao.get(SumProcess.class, no
					.getSumProcessId());
			sp.setStutas("同意");

		}
		if ("BOM外委采购订单申请".equals(circuitRun.getName())
				|| "外委采购订单申请".equals(circuitRun.getName())
				|| "外购采购订单申请".equals(circuitRun.getName())
				|| "辅料采购订单申请".equals(circuitRun.getName())
				|| "WaigouOrder".equals(circuitRun.getEntityName())) {
			WaigouOrder waigouOrder = (WaigouOrder) totalDao.getObjectById(
					WaigouOrder.class, circuitRun.getEntityId());
			// if (waigouOrder !=
			// null&&waigouOrder.getType().equals("外委")) {
			CodeTranslation codeTranslation = (CodeTranslation) totalDao
					.getObjectByCondition("from CodeTranslation where type='sys' and valueName='自动通知供应商'");
			String status = "待通知";
			if (codeTranslation != null
					&& "是".equals(codeTranslation.getValueCode())) {
				status = "待确认";
			}
			waigouOrder.setStatus(status);
			Set<WaigouPlan> planSet = waigouOrder.getWwpSet();
			if (planSet != null && planSet.size() > 0) {
				for (WaigouPlan waigouPlan : planSet) {
					waigouPlan.setStatus(status);
				}
			}

			totalDao.update(waigouOrder);
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
					.getApplication().get("companyInfo");
			String type = waigouOrder.getType();
			if ("研发".equals(type)) {
				type = "外购";
			}
			AlertMessagesServerImpl.addAlertMessages(type + "采购订单确认(供应商)",
					"尊敬的【" + waigouOrder.getLxPeople()
							+ "】,您好:\n您有新的订单已经确认签订,订单号:【"
							+ waigouOrder.getPlanNumber() + "】。详询"
							+ waigouOrder.getTzUserName() + "("
							+ waigouOrder.getTzUserPhone()
							+ ")。\n以下地址可以直接登录:\n用户名:"
							+ waigouOrder.getUserCode() + "\n初始密码:" + 123456
							+ "(请及时修改密码)\n[" + companyInfo.getName() + "]",
					"1", waigouOrder.getUserCode());
			// }

		}
		if ("内部计划审批".equals(circuitRun.getName())) {
			List<Product_Internal> piList = totalDao
					.query(
							"from Product_Internal where ioDetailId in (select id from InternalOrderDetail where internalOrder.id=?)",
							circuitRun.getEntityId());
			if (piList != null && piList.size() > 0) {
				for (Product_Internal pi : piList) {
					pi.setStatus("同意");
					totalDao.update(pi);
				}
			}
		}
		/**
		 * 用车申请
		 */
		if ("SingleCar".equals(circuitRun.getEntityName())) {
			SingleCar singleCar = (SingleCar) totalDao.getObjectById(
					SingleCar.class, circuitRun.getEntityId());
			singleCar.setUseStatus("使用中");
		}
		/**
		 * 仓库物料封存或解封审批--唐晓斌
		 */
		if ("Goods".equals(circuitRun.getEntityName())) {
			Goods goods = (Goods) totalDao.getObjectById(Goods.class,
					circuitRun.getEntityId());
			if (goods != null) {
				if ("仓库物料封存或解封申请".equals(circuitRun.getName())) {
					if (goods.getFcStatus() != null
							&& goods.getFcStatus().equals("封存")) {
						goods.setFcStatus("可用");
					} else {
						goods.setFcStatus("封存");
					}
				}
				if ("库存锁仓申请".equals(circuitRun.getName())) {
					goods.setIslock("YES");
				} else if ("库存解锁申请".equals(circuitRun.getName())) {
					goods.setIslock("NO");
				}
				totalDao.update(goods);
			}
		}
		/**
		 * 特殊工序申请--唐晓斌
		 */
		if ("ProcessTemplate".equals(circuitRun.getEntityName())) {
			ProcessTemplate process = (ProcessTemplate) totalDao.getObjectById(
					ProcessTemplate.class, circuitRun.getEntityId());
			if (process != null) {
				process.setIsSpecial("特殊");
				totalDao.update(process);
			}
		}
		if ("工序库特殊工序申请".equals(circuitRun.getName())) {
			ProcessGzstore processGz = (ProcessGzstore) totalDao.getObjectById(
					ProcessGzstore.class, circuitRun.getEntityId());
			if (processGz != null) {
				processGz.setIsSpecial("特殊");
				totalDao.update(processGz);
				// 同步所有工序
				List<ProcessTemplate> processTList = totalDao
						.query(
								"from ProcessTemplate where processName=? and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus!='历史')",
								processGz.getProcessName());
				if (processTList != null && processTList.size() > 0) {
					for (ProcessTemplate processT : processTList) {
						processT.setIsSpecial("特殊");
						totalDao.update(processT);
					}
				}
				List<ProcessInfor> processList = totalDao
						.query(
								"from ProcessInfor where processName=? and (dataStatus is null or dataStatus!='删除') and status not in('完成')",
								processGz.getProcessName());
				if (processList != null && processList.size() > 0) {
					for (ProcessInfor process : processList) {
						process.setIsSpecial("特殊");
						totalDao.update(process);
					}
				}
			}
		}
		/**
		 * 库存报废申请--王晓飞
		 */
		if ("BaoFeiGoods".equals(circuitRun.getEntityName())) {
			BaoFeiGoods bfg = (BaoFeiGoods) totalDao.get(BaoFeiGoods.class,
					circuitRun.getEntityId());
			bfg
					.setGoodsCurQuantity(bfg.getGoodsCurQuantity()
							- bfg.getSq_num());
			if (bfg.getGoodsCurQuantity() < 0) {
				bfg.setGoodsCurQuantity(0f);
			}
			totalDao.update(bfg);
			// 旧库存减少
			Goods goods = (Goods) totalDao.get(Goods.class, bfg.getGoodsId());
			float cur = goods.getGoodsCurQuantity() - bfg.getSq_num();
			if (cur < 0) {
				cur = 0;
			}
			goods.setGoodsCurQuantity(cur);
			totalDao.update(goods);

			// 新库存增加
			Goods newGoods = (Goods) totalDao.get(Goods.class, bfg
					.getNewGoodsId());
			newGoods.setGoodsCurQuantity(bfg.getSq_num());
			totalDao.update(newGoods);

		}
		/**
		 * 模具开模申请--王晓飞
		 */
		if ("模具开模申请".equals(circuitRun.getName())) {
			if ("MouldApplyOrder".equals(circuitRun.getEntityName())) {
				MouldApplyOrder mop = (MouldApplyOrder) totalDao.get(
						MouldApplyOrder.class, circuitRun.getEntityId());
				mop.setMaketype(circuitRun.getZzopinion());
				totalDao.update(mop);
			}
		}
		/**
		 * 项目费用申报--唐晓斌
		 */
		if ("QuotedPriceCost".equals(circuitRun.getEntityName())) {
			QuotedPriceCost qpc = (QuotedPriceCost) totalDao.getObjectById(
					QuotedPriceCost.class, circuitRun.getEntityId());
			if (qpc != null) {
				QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
						QuotedPrice.class, qpc.getQpId());
				if (qp != null) {
					Float money1 = (Float) totalDao
							.getObjectByCondition(
									"select sum(money) from ProjectTime where quoId=? and classNumber in('sb','gz','rg')",
									qp.getId());
					Float money2 = (Float) totalDao
							.getObjectByCondition(
									"select sum(money) from QuotedPriceCost where qpId=? and applyStatus='同意'",
									qp.getId());
					Float moneysj = (Float) totalDao
							.getObjectByCondition(
									"select sum(moneysj) from ProjectTime where quoId=? and classNumber not in('sb','gz','rg','gzf') ",
									qp.getId());
					Float moneysz = (Float) totalDao
							.getObjectByCondition(
									"select sum(moneysz) from ProjectTime where quoId=? and classNumber not in('sb','gz','rg','gzf') ",
									qp.getId());
					if (money1 == null) {
						money1 = 0f;
					}
					if (money2 == null) {
						money2 = 0f;
					}
					if (moneysj == null) {
						moneysj = 0f;
					}
					if (moneysz == null) {
						moneysz = 0f;
					}
					moneysj = moneysj + money1;
					moneysz = moneysz + money1;
					if (qp.getGzcb() == null || qp.getGzcb().equals("是")) {
						String hql = "select money from ProjectTime where quoId=? and classNumber=?";
						Float moneyGz = (Float) totalDao.getObjectByCondition(
								hql, qp.getId(), "gzf");
						if (moneyGz == null) {
							moneyGz = 0f;
						}
						qp.setAllFeiyong((double) money1 + money2 + moneyGz);
					} else {
						qp.setAllFeiyong((double) money1 + money2);
					}
					totalDao.update(qp);
				}
			}
			qpc.setApplyStatus("同意");
			totalDao.update(qpc);
		}
		/**
		 * 项目投资申请--唐晓斌
		 */
		if ("QuotedPriceUserCost".equals(circuitRun.getEntityName())) {
			QuotedPriceUserCost qpuc = (QuotedPriceUserCost) totalDao
					.getObjectById(QuotedPriceUserCost.class, circuitRun
							.getEntityId());
			if (qpuc != null && "撤资".equals(qpuc.getTzStatus())) {
				return "该投资已撤销!";
			}
			if (qpuc != null) {
				QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
						QuotedPrice.class, qpuc.getQpId());
				if (qp == null) {
					return "没有找到投资目标";
				} else if (qpuc.getTzMoney() > (qp.getAllFeiyong()
						* qp.getFbBili() - qp.getTouziFeiyong())) {
					circuitRun.setAllStatus("超额打回");
					totalDao.update(circuitRun);
					qpuc.setApplyStatus("超额打回");
					totalDao.update(qpuc);
					return "投资的金额超过目标总金额的一半,审批失败!";
				} else {
					qpuc.setApplyStatus("同意");
					totalDao.update(qpuc);
					qp
							.setTouziFeiyong(qp.getTouziFeiyong()
									+ qpuc.getTzMoney());
					totalDao.update(qp);
				}
			}
		}
		/**
		 * 项目下阶段申请--唐晓斌
		 */
		if ("QuotedPrice".equals(circuitRun.getEntityName())) {
			QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
					QuotedPrice.class, circuitRun.getEntityId());
			String changeType = null;
			if (qp.getStatus().equals("首件申请中")) {
				changeType = "首件阶段";
			} else if (qp.getStatus().equals("试制申请中")) {
				changeType = "试制阶段";
			} else if (qp.getStatus().equals("批产申请中")) {
				changeType = "批产阶段";
			}
			List<QuotedPrice> qpList = totalDao.query(
					"from QuotedPrice where rootId =?", circuitRun
							.getEntityId());
			for (QuotedPrice qp2 : qpList) {
				qp2.setStatus(changeType);
				totalDao.update(qp2);
			}
		}
		if ("ProcardTBanbenRelation".equals(circuitRun.getEntityName())) {
			ProcardTBanbenRelation ptbbr = (ProcardTBanbenRelation) totalDao
					.getObjectById(ProcardTBanbenRelation.class, circuitRun
							.getEntityId());
			if (ptbbr != null) {
				ptbbr.setStatus("停用");
				totalDao.update(ptbbr);
			}
		}
		/**
		 * 人员调动申请--王晓飞
		 */
		if ("人员调动申请".equals(circuitRun.getName())) {
			Transfer transfer = (Transfer) totalDao.get(Transfer.class,
					circuitRun.getEntityId());
			Users users = (Users) totalDao.get(Users.class, transfer
					.getUserId());
			users.setDept(transfer.getDept());
			Password pass = users.getPassword();
			pass.setDeptNumber(transfer.getDeptNo());
			users.setPassword(pass);
			users.setDeptId(transfer.getDeptId());
			// 更新users表部门
			totalDao.update(users);
			// 设置Transfer表调动时间
			String time = Util.getDateTime("yyyy-MM-dd HH:mm:ss");
			transfer.setDgTime(time);
			totalDao.update(transfer);
			// 设置Careertrack表调动时间;
			Careertrack ck = (Careertrack) totalDao.getObjectByCondition(
					"from Careertrack where userId =?", transfer.getUserId());
			ck.setDept(transfer.getDept());
			ck.setDiaodongTime(time);
			totalDao.update(ck);
		}
		/**
		 * 人员晋升申请--王晓飞
		 */
		if ("人员晋升申请".equals(circuitRun.getName())) {
			Promotion pn = (Promotion) totalDao.get(Promotion.class, circuitRun
					.getEntityId());
			Users users = (Users) totalDao.get(Users.class, pn.getUserId());
			users.setPost(pn.getRank());
			users.setDuty(pn.getDuty());
			// 更新users表职级;
			totalDao.update(users);
			// 设置Promotion表 晋升时间
			String time = Util.getDateTime("yyyy-MM-dd HH:mm:ss");
			pn.setJsTime(time);
			totalDao.update(pn);
			// 设置Careertrack表晋升时间;
			Careertrack ck = (Careertrack) totalDao.getObjectByCondition(
					"from Careertrack where userId =?", pn.getUserId());
			if (ck != null) {
				ck.setJinshengTime(time);
				totalDao.update(ck);
			}
		}
		/**
		 * 员工转正申请-----王晓飞
		 */
		if ("员工转正申请".equals(circuitRun.getName())) {
			Becoming bg = (Becoming) totalDao.get(Becoming.class, circuitRun
					.getEntityId());
			Users users = (Users) totalDao.get(Users.class, bg.getUserId());
			users.setOnWork(bg.getStatus());
			String time = Util.getDateTime("yyyy-MM-dd HH:mm:ss");
			users.setZhuanzhengtime(time);
			// 更新users表职级;
			totalDao.update(users);
			// 设置Becoming表 晋升时间

			bg.setZzdate(time);
			totalDao.update(bg);
			// 设置Careertrack表晋升时间;
			Careertrack ck = (Careertrack) totalDao.getObjectByCondition(
					"from Careertrack where userId =?", bg.getUserId());
			ck.setStatus("在职");
			ck.setZhuanzhengTime(time);
			totalDao.update(ck);
		}
		/**
		 * 销假申请---王晓飞
		 */
		if ("QxAskForLeave".equals(circuitRun.getEntityName())) {
			qxAskForLeave(circuitRun, totalDao);
		}
		/**
		 * 生产退料申请---王晓飞
		 */
//		if ("SCTuiliaoSqDan".equals(circuitRun.getEntityName())) {
//			sCTuiliaoSqDan(circuitRun, totalDao);
//		}
		/**
		 * 生产退料申请---李聪
		 */
		// if ("Procard".equals(circuitRun.getEntityName())) {
		// procard(circuitRun, totalDao);
		// }
		/**
		 * 图号申请---王晓飞
		 */
		if ("ChartNOSQ".equals(circuitRun.getEntityName())) {
			ChartNOSQ cq = (ChartNOSQ) totalDao.get(ChartNOSQ.class, circuitRun
					.getEntityId());
			Set<ChartNOSC> ccSet = cq.getChartNOSCSet();
			for (ChartNOSC chartNOSC : ccSet) {
				chartNOSC.setIsuse("YES");
				totalDao.update(chartNOSC);
			}
			if (cq.getHsNum() != null && cq.getHsNum() > 0) {
				String hql_hs = " from  ChartNOSC where isuse = 'NO'  "
						+ " and  sqNo= '" + cq.getSqNo() + "'";
				List<ChartNOSC> hsCCList = totalDao.query(hql_hs);
				for (ChartNOSC chartNOSC2 : hsCCList) {
					chartNOSC2.setIsuse("YES");
					totalDao.update(chartNOSC2);
				}
			}
			Users user = (Users) totalDao.get(Users.class, cq.getAddUserId());
			AlertMessagesServerImpl.addAlertMessages("编码查看",
					"您申请的编码已审批完成,申请单编号为:" + cq.getSqNo() + "申请类别:"
							+ cq.getType(), "1", user.getCode());

		}
		/** 供应商产品配额修改申请 王晓飞 **/
		if ("Gyscgbl".equals(circuitRun.getEntityName())) {
			Gyscgbl gyscgbl = (Gyscgbl) totalDao.get(Gyscgbl.class, circuitRun
					.getEntityId());
			GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao.get(
					GysMarkIdjiepai.class, gyscgbl.getGmId());
			gsm.setCgbl(gyscgbl.getCgbl());
			totalDao.update(gsm);

		}
		/** 物料类别修改申请 王晓飞 **/

		if ("YuanclAndWaigj".equals(circuitRun.getEntityName())) {
			yuanclAndWaigj(circuitRun, totalDao);
		}
		/** 外购件禁用/使用申请 李聪 **/
		if ("YuanclAndWaigjAppli".equals(circuitRun.getEntityName())) {
			yuanclAndWaigjAppli(circuitRun, totalDao);
		}
		/** 外购来料不良品处理申请 王晓飞 **/
		if ("ReturnsDetails".equals(circuitRun.getEntityName())) {
			ReturnsDetails returnsDetails = (ReturnsDetails) totalDao.get(
					ReturnsDetails.class, circuitRun.getEntityId());
			if (circuitRun.getZzopinion() != null
					&& circuitRun.getZzopinion().length() > 0) {
				returnsDetails.setClResult(circuitRun.getZzopinion());
				totalDao.update(returnsDetails);
			}

		}
		/** 物料需求申请 王晓飞 **/
		if ("ManualOrderPlanDetail".equals(circuitRun.getEntityName())) {
			ManualOrderPlanDetail mod = (ManualOrderPlanDetail) totalDao.get(
					ManualOrderPlanDetail.class, circuitRun.getEntityId());
			manualPlanServer.addmaualPlan1(mod);
			if ("预测订单".equals(mod.getType())) {
				YcWaiGouProcrd ycprocard = (YcWaiGouProcrd) totalDao
						.getObjectByCondition(
								" from YcWaiGouProcrd where mopdId =? ", mod
										.getId());
				ycprocard.setEpstatus("同意");
				totalDao.update(ycprocard);
			}
		}

		/** 物料需求申请单 **/
		if ("ManualOrderPlanTotal".equals(circuitRun.getEntityName())) {
			ManualOrderPlanTotal total = (ManualOrderPlanTotal) totalDao.get(
					ManualOrderPlanTotal.class, circuitRun.getEntityId());
			Set<ManualOrderPlanDetail> details = total.getDetails();
			if (details != null && details.size() > 0) {
				for (ManualOrderPlanDetail mod : details) {
					manualPlanServer.addmaualPlan1(mod);
					mod.setEpStatus("同意");
					totalDao.update(mod);
					if ("预测订单".equals(mod.getType())) {
						YcWaiGouProcrd ycprocard = (YcWaiGouProcrd) totalDao
								.getObjectByCondition(
										" from YcWaiGouProcrd where mopdId =? ",
										mod.getId());
						ycprocard.setEpstatus("同意");
						totalDao.update(ycprocard);
					}
				}
			}
		}
		/** 现场不良品处理申请 王晓飞 **/
		if ("BreakSubmit".equals(circuitRun.getEntityName())) {
			breakSubmit(circuitRun, totalDao);
		}
		/** MOQ数量申请 王晓飞 **/
		if ("MOQ数量申请".equals(circuitRun.getName())) {
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, circuitRun.getEntityId());
			mop.setEpStatus("同意");
			Float needNumber = mop.getNumber();
			if (mop.getOutcgNumber() != null && mop.getOutcgNumber() > 0) {
				needNumber -= mop.getOutcgNumber();
			}
			if (needNumber >= mop.getMoqNum()) {// 如果审批时采购数量大于MOQ数量
				mop.setIsuse("NO");
				mop.setNeedNumber(mop.getNumber());// 记录实际需求量
				mop.setGyscodeAndNum("");
				mop.setMoqGysCode("");
			} else {
				mop.setIsuse("YES");
				mop.setNeedNumber(mop.getNumber());// 记录实际需求量
				mop.setNumber(mop.getNumber() - needNumber + mop.getMoqNum());// 调整采购量为MOQ数量
			}
			totalDao.update(mop);
		}
		/** 回款入账申请 王晓飞 **/
		if ("回款入账申请".equals(circuitRun.getName())) {
			TaHkBackMoney backMoney = (TaHkBackMoney) totalDao.get(
					ManualOrderPlan.class, circuitRun.getEntityId());
			// 更新账户余额;
			SubBudgetRate sub = (SubBudgetRate) totalDao.getObjectByCondition(
					" from SubBudgetRate where id = ? ", backMoney.getSubId());
			sub.setBorrowMoney(sub.getBorrowMoney() == null ? backMoney
					.getHkbmMoney() : backMoney.getHkbmMoney()
					+ sub.getBorrowMoney());// 本期借方发生余额
			sub
					.setBorrowJieyuMoney(sub.getBorrowJieyuMoney() == null ? backMoney
							.getHkbmMoney()
							: backMoney.getHkbmMoney()
									+ sub.getBorrowJieyuMoney());// 期末借方余额
			sub
					.setBorrowYearBegingMoney(sub.getBorrowYearBegingMoney() == null ? backMoney
							.getHkbmMoney()
							: backMoney.getHkbmMoney()
									+ sub.getBorrowYearBegingMoney());// 本年借方余额
			totalDao.update(sub);
		}
		/** 非主营业务应付申请 刘培 ---添加进付款管理系统 **/
		if ("非主营业务应付申请".equals(circuitRun.getName())) {
			NonCorePayable nonCorePayable = (NonCorePayable) totalDao
					.getObjectById(NonCorePayable.class, circuitRun
							.getEntityId());
			if (nonCorePayable != null) {
				// 查询该收款单位在付款汇总表是否存在
				String hql = "from SupplierCorePayable where supplierName=? and coreType='非主营' and payableType='付款'";
				SupplierCorePayable scp = (SupplierCorePayable) totalDao
						.getObjectByCondition(hql, nonCorePayable
								.getShoukuandanwei());
				if (scp != null) {
					scp.setYingfukuanJine(scp.getYingfukuanJine()
							+ nonCorePayable.getYingfukuanJIne());
					scp.setWeifukuanJine(scp.getWeifukuanJine()
							+ nonCorePayable.getYingfukuanJIne());
					totalDao.update(scp);
				} else {
					scp = new SupplierCorePayable();
					scp.setSupplierName(nonCorePayable.getShoukuandanwei());
					scp.setCoreType("非主营");
					scp.setPayableType("付款");
					scp.setYingfukuanJine(nonCorePayable.getYingfukuanJIne());
					scp.setRealfukuanJine(nonCorePayable.getRealfukuanJIne());
					scp.setWeifukuanJine(nonCorePayable.getYingfukuanJIne());
					scp.setFukuanzhongJine(0F);
					scp.setFkCount(0);
					scp.setFukuanzhongJine(0F);
					scp.setWeifukuanJine(0F);
					scp.setAddTime(Util.getDateTime());
					totalDao.save(scp);
				}
				nonCorePayable.setScpId(scp.getId());
				totalDao.update(nonCorePayable);

			}
		}
		/**
		 * 模具开模申请 wxf
		 */
		if ("模具开模申请".equals(circuitRun.getName())) {
			MouldApplyOrder moa = (MouldApplyOrder) totalDao.getObjectById(
					NonCorePayable.class, circuitRun.getEntityId());
			if (moa != null) {
				Users user0 = Util.getLoginUser();
				moa.setShUserName(user0.getName());
				moa.setAgreeUserName(user0.getName());
				totalDao.update(moa);
			}
		}

		/**
		 * 外委调整工序申请 wxf
		 */
		if ("外委调整工序申请".equals(circuitRun.getName())) {
			processInforWWApplyDetail(circuitRun, totalDao);
		}
		/**
		 * 超损补料申请 wxf
		 */
		if ("超损补料申请".equals(circuitRun.getName())
				|| "外委补料申请".equals(circuitRun.getName())) {
			ProcardCsBlOrder csblOrder = (ProcardCsBlOrder) totalDao.get(
					ProcardCsBlOrder.class, circuitRun.getEntityId());
			if ("外委补料".equals(csblOrder.getType())) {
				procardServer.chengcsblProcard(csblOrder);
			} else {
				procardServer.chengcsblProcard2(csblOrder);
			}
		}
		/**
		 * 不良退货申请 wxf
		 */
		if ("不良退货申请".equals(circuitRun.getName())) {
			ReturnSingle rs = (ReturnSingle) totalDao.get(ReturnSingle.class,
					circuitRun.getEntityId());
			rs.setApprovalTime(Util.getDateTime());
			Set<ReturnsDetails> rdsSet = rs.getRdsSet();
			for (ReturnsDetails returnsDetails : rdsSet) {
				returnsDetails.setApprovalTime(Util.getDateTime());
				totalDao.update(returnsDetails);
			}
			totalDao.update(rs);

		}
		/**
		 *外协退料申请 wxf
		 * 
		 */
		if ("外协退料申请".equals(circuitRun.getName())) {
			ProcardWxTuiLiao wxtl = (ProcardWxTuiLiao) totalDao.get(
					ProcardWxTuiLiao.class, circuitRun.getEntityId());
			/**
			 * 查询相关的出库记录
			 */
			String processNOs = wxtl.getProcessNos();
			String[] processNOArray = processNOs.split(";");
			String processNames = wxtl.getProcessNames();
			String[] processNameArray = processNames.split(";");
			String finalProcessName = processNameArray[processNameArray.length - 1];
			Integer finalProcessNO = Integer
					.parseInt(processNOArray[processNOArray.length - 1]);
			List<Sell> sellList = totalDao.query(
					" from Sell where sellWarehouse = '外协库' and sellMarkId=? and "
							+ " orderNum =? and processNo=?", wxtl.getMarkId(),
					wxtl.getOrderNum(), finalProcessNO);
			/**
			 * 插入入库记录。
			 */
			Users user = Util.getLoginUser();
			Float sqtlNum = wxtl.getSqtlNum();
			if (sellList != null && sellList.size() > 0) {
				for (Sell s : sellList) {
					if (sqtlNum <= 0) {
						break;
					}
					if (s.getSellCount() >= sqtlNum) {
						s.setTksellCount(sqtlNum);
						GoodsStore gs = new GoodsStore();
						GoodsStore gs0 = (GoodsStore) totalDao
								.getObjectByCondition(
										" from GoodsStore where goodsStoreWarehouse = '外协库' and goodsStoreMarkId = ? and goodsStoreLot=? ",
										s.getSellMarkId(), s.getSellLot());
						BeanUtils.copyProperties(gs0, gs, new String[] { "id",
								"style" });
						gs.setApplyTime(Util.getDateTime());// 申请入库时间
						gs.setInputSource("外协退料入库");// 来源
						gs.setStatus("待入库");//
						gs.setStyle("外协退料入库");//
						gs.setPrintStatus("YES");
						gs.setSqUsersName(user.getName());// 入库申请人
						gs.setSqUsrsId(user.getId());
						gs.setSqUsersCode(user.getCode());
						gs.setSqUsersdept(user.getDept());
						gs.setGoodsStoreCount(sqtlNum);
						gs.setNeiorderId(s.getOrderNum());
						gs.setProcessName(finalProcessName);
						totalDao.save(gs);
						Sell sell = new Sell();
						BeanUtils.copyProperties(s, sell, new String[] { "id",
								"style" });
						sell.setSellCount(sqtlNum);
						sell.setStyle("外协退料产生");
						s.setSellCount(s.getSellCount() - sqtlNum);
						totalDao.update(s);
						totalDao.save(sell);
						sqtlNum = 0f;
					} else {
						s.setTksellCount(s.getSellCount());
						GoodsStore gs = new GoodsStore();
						GoodsStore gs0 = (GoodsStore) totalDao
								.getObjectByCondition(
										" from GoodsStore where goodsStoreWarehouse = '外协库' and goodsStoreMarkId = ? and goodsStoreLot=? ",
										s.getSellMarkId(), s.getSellLot());
						BeanUtils.copyProperties(gs0, gs, new String[] { "id",
								"style" });
						gs.setApplyTime(Util.getDateTime());// 申请入库时间
						gs.setInputSource("外协退料入库");// 来源
						gs.setStatus("待入库");//
						gs.setStyle("外协退料入库");//
						gs.setPrintStatus("YES");
						gs.setSqUsersName(user.getName());// 入库申请人
						gs.setSqUsrsId(user.getId());
						gs.setSqUsersCode(user.getCode());
						gs.setSqUsersdept(user.getDept());
						gs.setGoodsStoreCount(sqtlNum);
						gs.setNeiorderId(s.getOrderNum());
						gs.setProcessName(finalProcessName);
						totalDao.save(gs);
						Sell sell = new Sell();
						BeanUtils.copyProperties(s, sell, new String[] { "id",
								"style" });
						sell.setSellCount(s.getSellCount());
						sell.setStyle("外协退料产生");
						s.setSellCount(0f);
						totalDao.save(sell);
						totalDao.update(s);
						sqtlNum -= s.getSellCount();
					}
				}
			}
			/**
			 * 修改下工序可领数量
			 */
			if (wxtl.getNextProcessId() != null) {
				ProcessInfor nextProcess = (ProcessInfor) totalDao.get(
						ProcessInfor.class, wxtl.getNextProcessId());
				if (nextProcess != null) {
					nextProcess.setTotalCount(nextProcess.getTotalCount()
							- wxtl.getSqtlNum());
					totalDao.update(nextProcess);
				}
			}
			/**
			 * 修改相关外委工序和前工序的领取数量和已领数量
			 */
//			List<ProcessInfor> processList = totalDao
//					.query(
//							" from ProcessInfor where procard.id =? and processNO<=? "
//									+ " and (dataStatus is null or dataStatus <> '删除' ) ",
//							wxtl.getProcardId(), finalProcessNO);
//			if (processList != null && processList.size() > 0) {
//				Float tlNum = wxtl.getSqtlNum();
//				for (ProcessInfor process : processList) {
//					process.setSubmmitCount(process.getSubmmitCount() - tlNum);
//					process.setApplyCount(process.getApplyCount() - tlNum);
//					// List<ProcessInforReceiveLog> pirList =
//					// totalDao.query(" from ProcessInforReceiveLog where fk_processInforId =?"
//					// +
//					// " and status = '提交' and (istuliao is null or istuliao <> '是') ",
//					// process.getId());
//					// if(pirList!=null && pirList.size()>0){
//					// Float flagcount = tlNum;
//					// for (ProcessInforReceiveLog proLog : pirList) {
//					// if(flagcount>0){
//					// if(flagcount<=proLog.getSubmitNumber()){
//					// ProcessInforReceiveLog proLog0 = new
//					// ProcessInforReceiveLog();
//					// BeanUtils.copyProperties(proLog, proLog0, new
//					// String[]{"id","receiveNumber","submitNumber"});
//					// proLog.setSubmitNumber(proLog.getSubmitNumber()-flagcount);
//					// proLog.setReceiveNumber(proLog.getReceiveNumber()-flagcount);
//					// proLog0.setSubmitNumber(flagcount);
//					// proLog0.setReceiveNumber(flagcount);
//					// proLog0.setIstuliao("是");
//					// flagcount = 0f;
//					// totalDao.update(proLog);
//					// totalDao.save(proLog0);
//					// }else{
//					// ProcessInforReceiveLog proLog0 = new
//					// ProcessInforReceiveLog();
//					// BeanUtils.copyProperties(proLog, proLog0, new
//					// String[]{"id","receiveNumber","submitNumber"});
//					// proLog0.setSubmitNumber(proLog.getSubmitNumber());
//					// proLog0.setReceiveNumber(proLog.getReceiveNumber());
//					// proLog.setSubmitNumber(0f);
//					// proLog.setReceiveNumber(0f);
//					// proLog0.setIstuliao("是");
//					// flagcount = flagcount-proLog.getSubmitNumber();
//					// totalDao.update(proLog);
//					// totalDao.save(proLog0);
//					// }
//					//								
//					// }
//					// }
//					// }
//					totalDao.update(process);
//				}
//			}

			/**
			 * 修改在制品占用件ckcont。以便下次继续领料。
			 */
			ProcardProductRelation ppr = (ProcardProductRelation) totalDao
					.getObjectByCondition(
							" from ProcardProductRelation where procardId = ? and ckCount>0  ",
							wxtl.getProcardId());
			ppr.setCkCount(ppr.getCkCount() - wxtl.getSqtlNum());
			totalDao.update(ppr);
			/**
			 * 修改外协退料申请表同意退料数量
			 */
			wxtl.setAgreeNum(wxtl.getSqtlNum());
			totalDao.update(wxtl);
			Procard procard = (Procard) totalDao.getObjectById(Procard.class, wxtl.getProcardId());
			if(procard!=null){
				if(procard.getZaizhizkCount()==null){
					procard.setZaizhizkCount( wxtl.getSqtlNum());
				}else{
					procard.setZaizhizkCount(procard.getZaizhizkCount()+ wxtl.getSqtlNum());
				}
				if(procard.getZaizhizkCount()>procard.getFilnalCount()){
					procard.setZaizhizkCount(procard.getFilnalCount());
				}
				totalDao.update(procard);
			}

		}
		/**
		 * 不合格品状态审批流程 wxf
		 */
		if ("不良品状态审批流程".equals(circuitRun.getName())) {
			String msg = "";
			DefectiveProduct dp = (DefectiveProduct) totalDao.get(
					DefectiveProduct.class, circuitRun.getEntityId());
			if ("退货".equals(dp.getResult())) {
				msg = wwpServer.bhgth(dp.getId(), dp.getZjbhgNumber(), dp
						.getZjhgNumber(), null);
			} else if ("特采".equals(dp.getResult())) {
				msg = wwpServer.bhgth(dp.getId(), dp.getZjbhgNumber(), dp
						.getZjhgNumber(), null);
			} else if ("挑选".equals(dp.getResult())) {
				msg = wwpServer.isAgainCheck(dp);
			} else if ("报废".equals(dp.getResult())) {

			}
			dp.setStatus("已处理");
			totalDao.update(dp);
			if (!"true".equals(msg)) {
				throw new RuntimeException(msg);
			}
		}
		/**
		 * 订单取消申请 wxf
		 */
		if ("订单取消申请".equals(circuitRun.getName())) {
			ProductManager product = (ProductManager) totalDao.get(
					ProductManager.class, circuitRun.getEntityId());
			product.setStatus("取消待评审");
			List<Procard> procardList = totalDao.query(
					" from Procard where ywmarkId =? and orderNumber =?",
					product.getYwMarkId(), product.getOrderManager()
							.getOrderNum());
			if (procardList != null) {
				for (Procard procard : procardList) {
					procard.setStatus("暂停");
					totalDao.update(procard);
				}
			}
			totalDao.update(product);
		}
		/**
		 * 成品退货申请 wxf
		 */
		if ("成品退货申请".equals(circuitRun.getName())) {
			ChengPinTuiHuo cptu = (ChengPinTuiHuo) totalDao.get(
					ChengPinTuiHuo.class, circuitRun.getEntityId());
			// 更新订单产品的出库数量，申请开票数量
			ProductManager product = (ProductManager) totalDao.get(
					ProductManager.class, cptu.getProductId());
			if (product.getSellCount() != null && product.getSellCount() > 0) {
				product.setSellCount(product.getSellCount() - cptu.getSqNum());
			}
			if (product.getApplyNumber() != null
					&& product.getApplyNumber() > 0) {
				product.setApplyNumber(product.getApplyNumber()
						- cptu.getSqNum());
			}
			totalDao.update(product);
			// 更新订单的完成率 完成率(出库量/订单数量*100%)
			OrderManager order = (OrderManager) totalDao.get(
					OrderManager.class, cptu.getOrderId());
			Float completionrate = (Float) totalDao
					.getObjectByCondition(
							" select (sellCount/num)/100 from ProductManager "
									+ " where orderManager.id =? and num is not null and num>0 and sellCount is not null and sellCount>0",
							cptu.getOrderId());
			
			order.setCompletionrate(completionrate == null?0f:completionrate);
			totalDao.update(order);
			// 更新procard上的，已入库数量
			Procard procard = (Procard) totalDao.getObjectByCondition("from Procard where  " +
					" markId=? and selfCard =? ", cptu.getMarkId(),cptu.getSelfCard());
			if(procard!=null){
				procard.setHasRuku(procard.getHasRuku()-cptu.getSqNum());
				totalDao.update(procard);
			}
			Sell sell = (Sell) totalDao.get(Sell.class, cptu.getSellId());
			// 生成待入库的入库记录，入库方式为:成品退货入库
			GoodsStore gs = new GoodsStore();
			gs.setGoodsStoreMarkId(cptu.getMarkId());// 件号
			gs.setGoodsStoreGoodsName(sell.getSellGoods());
			gs.setGoodsStoreLot(cptu.getSelfCard());// 批次
			gs.setGoodsStoreCount(cptu.getSqNum());// 入库数量
			gs.setGoodsStoreWarehouse(cptu.getKubie());
			gs.setGoodHouseName(cptu.getCangqu());
			gs.setGoodsStorePosition(cptu.getKuwei());
			gs.setGoodsStoreUnit(sell.getSellUnit());
			gs.setHsPrice(sell.getSellPrice()==null?null:sell.getSellPrice().doubleValue());
			gs.setGoodsStorePrice(sell.getSellbhsPrice()==null?null:sell.getSellbhsPrice().doubleValue());
			gs.setTaxprice(sell.getTaxprice().doubleValue());
			gs.setMoney(gs.getGoodsStorePrice() * gs.getGoodsStoreCount());
			gs.setGoodsStoreCompanyName(sell.getSellCompanyName());
			gs.setCustomerId(sell.getCustomerId());
			gs.setApplyTime(Util.getDateTime());
			gs.setNeiorderId(order.getOrderNum());
			gs.setWaiorderId(order.getOutOrderNumber());
			gs.setOrder_Id(order.getId());
			gs.setStatus("待入库");
			gs.setStyle("总成退货入库");
			gs.setOrderId(order.getId() + "");
			Users user = Util.getLoginUser();
			gs.setSqUsersName(cptu.getSqUsers());
			gs.setSqUsrsId(cptu.getSqUsersId());
			gs.setSqUsersCode(cptu.getSqUserCode());
			gs.setSqUsersdept(user.getDept());
			gs.setYwmarkId(sell.getYwmarkId());
			totalDao.save(gs);
		}
		/**
		 * wxf 合同附件查看申请
		 */
		if("合同附件查看申请".equals(circuitRun.getName())){
			AppLiPrice  appliPrice= (AppLiPrice) totalDao.get(
					AppLiPrice.class, circuitRun.getEntityId());
			appliPrice.setAppStratu("同意");
			totalDao.update(appliPrice);
		}
		/**
		 * wxf 粉末用量调整申请
		 */
		if("粉末用量调整申请".equals(circuitRun.getName())){
			FenMoTzRecord fmtr =	(FenMoTzRecord) totalDao.get(FenMoTzRecord.class, circuitRun.getEntityId());
			//更新外购件库上的每公斤喷粉面积
			List<YuanclAndWaigj> yclList =		totalDao.query("from YuanclAndWaigj where markId = ? and (banbenStatus is null or  banbenStatus <> '历史')",
					fmtr.getMarkId());
			for (YuanclAndWaigj ycl : yclList) {
					ycl.setAreakg(fmtr.getAreakg1());
					totalDao.update(ycl);
			}
			//更新BOM上的权值
			List<ProcardTemplate> ptList =		totalDao.query("from ProcardTemplate where markId = ? and (dataStatus is null or dataStatus <> '删除') ", 
					fmtr.getMarkId());
			for (ProcardTemplate pt : ptList) {
				pt.setQuanzi2(pt.getQuanzi2()*fmtr.getAreakg0()/fmtr.getAreakg1());
				totalDao.update(pt);
			}
		}
		/**
		 * wxf 粉末用量批量调整申请
		 */
		if("粉末用量批量调整申请".equals(circuitRun.getName())){
			
			DosageTzDan dtd =	(DosageTzDan) totalDao.get(DosageTzDan.class, circuitRun.getEntityId());
			Set<FenMoTzRecord> fmtrSet	= dtd.getFmtrSet();
			for (FenMoTzRecord fmtr : fmtrSet) {
				//更新外购件库上的每公斤喷粉面积
				List<YuanclAndWaigj> yclList =		totalDao.query("from YuanclAndWaigj where markId = ? and (banbenStatus is null or  banbenStatus <> '历史')",
						fmtr.getMarkId());
				for (YuanclAndWaigj ycl : yclList) {
						ycl.setAreakg(fmtr.getAreakg1());
						totalDao.update(ycl);
				}
				//更新BOM上的权值
				List<ProcardTemplate> ptList =		totalDao.query("from ProcardTemplate where markId = ? and (dataStatus is null or dataStatus <> '删除') ", 
						fmtr.getMarkId());
				for (ProcardTemplate pt : ptList) {
					pt.setQuanzi2(pt.getQuanzi2()*fmtr.getAreakg0()/fmtr.getAreakg1());
					totalDao.update(pt);
				}
			}
			
		}
		
		/**
		 * 辅料申购申请
		 */
		if ("辅料申购申请".equals(circuitRun.getName())) {
			OaAppDetailTemplate odt = (OaAppDetailTemplate) totalDao.get(
					OaAppDetailTemplate.class, circuitRun.getEntityId());

			ChartNOSC c = (ChartNOSC) totalDao.getObjectByCondition(
					" from ChartNOSC where chartNO=? ", odt.getWlcode());
			if (c == null) {
				c = new ChartNOSC();
				c.setType(odt.getDetailChildClass());
				c.setChartNO(odt.getWlcode());
				c.setSjsqUsers(odt.getAddUsersName());
				c.setIsuse("YES");
				c.setRemak("辅料申请");
				totalDao.save(c);
			}

		}

		/**
		 * 成品库调仓申请 mdd
		 */
		if ("成品库调仓申请".equals(circuitRun.getName())) {
			goodsServer.changeEP(circuitRun.getEntityId());
			// CpGoodsChangeWG change=(CpGoodsChangeWG)
			// totalDao.getObjectById(CpGoodsChangeWG.class,circuitRun.getEntityId()
			// );
		}

		/**
		 * 文件管控审批流程
		 */
		if ("文件管控审批流程".equals(circuitRun.getName())) {
			SystemFile systemFile = (SystemFile) totalDao.getObjectById(
					SystemFile.class, circuitRun.getEntityId());
			systemFile.setStatus("归档");
			// 状龙需求采购合同受控不作废
			String valueCode = (String) totalDao
					.getObjectByCondition("select  valueCode from CodeTranslation where keyCode = '采购合同不执行作废'");
			if (valueCode == null || !valueCode.equals("是")
					&& !"合同类".equals(systemFile.getFileType())) {
				if (systemFile.getBanben_old() != null
						&& !"".equals(systemFile.getBanben_old())) {
					SystemFile oldSystemFile = (SystemFile) totalDao
							.getObjectByCondition(
									"from SystemFile where fileNo=? and banben=?",
									systemFile.getFileNo(), systemFile
											.getBanben_old());
					if (oldSystemFile != null) {
						oldSystemFile.setStatus("作废");
						totalDao.update(oldSystemFile);
					}
				} else {
					String hql = "from SystemFile where fileName =?";
					List<SystemFile> systemFileList = totalDao.query(hql,
							systemFile.getFileName());
					if (systemFileList != null && systemFileList.size() > 0) {
						String ids = "";
						for (SystemFile s : systemFileList) {
							if (!systemFile.getId().equals(s.getId())) {
								if ("".equals(ids)) {
									ids += s.getBanben();
								} else {
									ids += ";" + s.getBanben();
								}
								s.setStatus("作废");
								totalDao.update(s);
							}
						}
						systemFile.setBanben_old(ids);
					}
				}
			}
			totalDao.update(systemFile);

		}

		/**
		 * 文件管控作废审批流程
		 */
		if ("文件编号作废审批流程".equals(circuitRun.getName())) {
			SystemFile systemFile = (SystemFile) totalDao.getObjectById(
					SystemFile.class, circuitRun.getEntityId());
			systemFile.setStatus("作废");
			// 查找文控员,为文件作废发送消息提醒
			CodeTranslation codeTranslation = (CodeTranslation) totalDao
					.getObjectByCondition(
							"from CodeTranslation where valueName = ? and type = 'sys'",
							"文控员工号");
			if (codeTranslation != null) {
				String valueCode = codeTranslation.getValueCode();
				if (valueCode != null && valueCode != "") {
					AlertMessagesServerImpl.addAlertMessages("文件受控查看·技术部",
							"文件编号：" + systemFile.getFileNo() + "以批准为作废"
									+ "，如有纸质文件请同步作废", "您有新的推送消息提醒", valueCode);
				}
			}
			totalDao.update(systemFile);
		}
		if ("物料需求变更申请".equals(circuitRun.getName())) {
			ManualOrderPlan plan = (ManualOrderPlan) totalDao.getObjectById(
					ManualOrderPlan.class, circuitRun.getEntityId());
			if (plan.getCancalNum() <= plan.getNumber()) {
				List<ManualOrderPlanDetail> modList = totalDao.query(
						"from ManualOrderPlanDetail where manualPlan.id=? ",
						plan.getId());
				float number = plan.getCancalNum();
				if (modList != null && modList.size() > 0) {
					for (ManualOrderPlanDetail detail : modList) {
						float cgnumber = detail.getCgnumber();// 采购数量
						float outCgNumber = detail.getOutcgNumber() == null ? 0
								: detail.getOutcgNumber();
						if (number > cgnumber - outCgNumber) {
							if (detail.getRemarks() == null) {
								detail.setRemarks("[数量变更：原数量：" + cgnumber
										+ "，取消数量" + ((cgnumber - outCgNumber))
										+ "]");
							} else {
								detail.setRemarks(detail.getRemarks()
										+ ",[数量变更：原数量：" + cgnumber + "，取消数量"
										+ ((cgnumber - outCgNumber)) + "]");
							}
							number = number - (cgnumber - outCgNumber);
							detail.setCgnumber(cgnumber - number);
							totalDao.update(detail);
						} else {
							detail.setCgnumber(cgnumber - number);
							if (detail.getRemarks() == null) {
								detail.setRemarks("[数量变更：原数量：" + cgnumber
										+ "，取消数量" + number + "]");
							} else {
								detail.setRemarks(detail.getRemarks()
										+ ",[数量变更：原数量：" + cgnumber + "，取消数量"
										+ number + "]");
							}
							totalDao.update(detail);
							break;
						}
					}
					plan.setNumber(plan.getNumber() - plan.getCancalNum());
				}
				totalDao.update(plan);
			}
			// Set<ManualOrderPlanDetail> modSet = plan.getModSet();

			// StringBuffer buffer = new StringBuffer(
			// "from ManualOrderPlanDetail where markId = ? "
			// +
			// "and kgliao=? and (status<>'取消' or status is null) ");
			// if (plan.getBanben() != null
			// && plan.getBanben().length() > 0) {
			// buffer.append(" and banben ='" + plan.getBanben()
			// + "'");
			// }
			// if (plan.getSpecification() != null
			// && plan.getSpecification().length() > 0) {
			// buffer.append(" and specification ='"
			// + plan.getSpecification() + "'");
			// }
			//
			// float number = plan.getCancalNum();
			// List<ManualOrderPlanDetail> planDetails = totalDao
			// .query(buffer.toString(), plan.getMarkId(),
			// plan.getKgliao());
			// if (planDetails != null && planDetails.size() > 0) {
			// for (ManualOrderPlanDetail detail : planDetails) {
			// float cgnumber = detail.getCgnumber();// 采购数量
			// if (number > cgnumber - detail.getRukuNum()) {
			// detail.setCgnumber(0f);
			// number = number - cgnumber;
			// totalDao.update(detail);
			// } else {
			// detail.setCgnumber(cgnumber - number);
			// totalDao.update(detail);
			// break;
			// }
			// }
			// }

		}

		if ("手工出库单审核".equals(circuitRun.getName())) {
			Sell sell = (Sell) totalDao.getObjectById(Sell.class, circuitRun
					.getEntityId());
			Integer handwordSellNumber = sell.getHandwordSellNumber();
			List<Sell> list = totalDao.query(
					"from Sell where handwordSellNumber=?", handwordSellNumber);
			for (Sell sell2 : list) {
				Goods g = (Goods) totalDao.getObjectById(Goods.class, sell2
						.getGoodsId());
				Float goodsCurQuantity = g.getGoodsCurQuantity();
				float kcNumber = new BigDecimal(goodsCurQuantity).setScale(4,
						BigDecimal.ROUND_HALF_UP).floatValue();
				if (kcNumber - sell2.getSellCount() < 0) {
					throw new RuntimeException("当前库存小于要出库的库存，出库失败，请核对库存");
				}
				g.setGoodsCurQuantity(g.getGoodsCurQuantity()
						- sell2.getSellCount());
				totalDao.update(g);
				// 生成出库凭证
				corePayableServer.goodsSell(sell2);

				// 出库面积更新
				goodsServer.updateGoodHouseBySell(sell2);

				sell2.setHandwordSellStatus("同意");
				totalDao.update(sell2);
			}

		}

		if ("人员借入申请".equals(circuitRun.getName())
				&& circuitRun.getEntityName().equals("PebBorrowAndLendLog")) {
			PebBorrowAndLendLog log = (PebBorrowAndLendLog) totalDao
					.getObjectById(PebBorrowAndLendLog.class, circuitRun
							.getEntityId());
			if (log != null) {

				// 借入
				if (log.getPebUsersId() != null) {
					PebUsers borrowPebUsers = (PebUsers) totalDao
							.getObjectById(PebUsers.class, log.getPebUsersId());

					Integer borrowCount = 0;
					if (borrowPebUsers.getBorrowNum() != null) {
						borrowCount = borrowPebUsers.getBorrowNum()
								+ log.getBorrowNum();
					} else {
						borrowCount = log.getBorrowNum();
					}
					borrowPebUsers.setBorrowNum(borrowCount);
					if (borrowPebUsers.getBorrowNum() > 0) {
						// 计算日人均。。。
						if (borrowPebUsers.getZsNumber() != null
								&& borrowPebUsers.getDangAnNum() != null) {
							Integer lendNum = 0;
							if (borrowPebUsers.getLendNum() != null) {
								lendNum = borrowPebUsers.getLendNum();
							}
							float noChuQin = 0;
							if (borrowPebUsers.getNoChuQinNum() != null) {
								noChuQin = borrowPebUsers.getNoChuQinNum();
							}
							borrowPebUsers.setActualNum(borrowPebUsers
									.getDangAnNum()
									- lendNum + borrowCount - noChuQin);
							BigDecimal szBigDecimal = new BigDecimal(
									borrowPebUsers.getZsNumber());
							BigDecimal actualBigdecimal = new BigDecimal(
									borrowPebUsers.getActualNum());
							if (actualBigdecimal.floatValue() == 0) {
								borrowPebUsers.setAvgNumber(0f);
								borrowPebUsers.setUPPH(null);
							} else {
								float renjun = szBigDecimal.divide(
										actualBigdecimal, 5,
										BigDecimal.ROUND_HALF_UP).floatValue();
								borrowPebUsers.setAvgNumber(renjun);
								if (borrowPebUsers.getGzHour() != null) {
									BigDecimal bigDecimal = new BigDecimal(
											renjun);
									BigDecimal bigDecimal2 = new BigDecimal(
											borrowPebUsers.getGzHour());
									float UPPH = bigDecimal.divide(bigDecimal2,
											5, BigDecimal.ROUND_HALF_UP)
											.floatValue();
									borrowPebUsers.setUPPH(UPPH);
								}
							}
						}

					}
					totalDao.update(borrowPebUsers);
				}

				// 借出
				String borrowBanzu = log.getBorrowBanzu();
				SubTeam subTeam = ProductEBServerImpl.getSubTeamByBZJGname(
						borrowBanzu, totalDao);
				// SubTeam subTeam = (SubTeam)
				// totalDao.getObjectByCondition("from SubTeam where subName =?",
				// borrowBanzu);
				// if(subTeam==null) {
				// //当前登录人在负责人列表中
				// borrowBanzu = (String)
				// totalDao.getObjectByCondition("select name from PebBanzuJiegou where id in"
				// + "(select fatherId from PebBanzuJiegou where name=?)",
				// borrowBanzu);
				// if(borrowBanzu!=null) {
				// subTeam = (SubTeam)
				// totalDao.getObjectByCondition("from SubTeam where subName =?",
				// borrowBanzu);
				// if(subTeam==null) {
				// borrowBanzu = (String)
				// totalDao.getObjectByCondition("select name from PebBanzuJiegou where id in"
				// + "(select fatherId from PebBanzuJiegou where name=?)",
				// borrowBanzu);
				// if(borrowBanzu!=null) {
				// subTeam = (SubTeam)
				// totalDao.getObjectByCondition("from SubTeam where subName =?",
				// borrowBanzu);
				// }
				// }
				//						
				// }
				// }
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd");

				try {
					PebBorrowAndLendLog lendLog = new PebBorrowAndLendLog();
					if (subTeam != null) {
						Date date = simpleDateFormat.parse(log.getSqTime());
						simpleDateFormat = new SimpleDateFormat("yyyy");
						Integer year = Integer.parseInt(simpleDateFormat
								.format(date));
						simpleDateFormat = new SimpleDateFormat("MM");
						Integer month = Integer.parseInt(simpleDateFormat
								.format(date));
						simpleDateFormat = new SimpleDateFormat("dd");
						Integer day = Integer.parseInt(simpleDateFormat
								.format(date));
						PebUsers lendPebusers = (PebUsers) totalDao
								.getObjectByCondition(
										"from PebUsers where banZu = ? and year=?"
												+ " and month=? and day=?",
										subTeam.getSubName(), year, month, day);
						if (lendPebusers != null) {
							Integer lendNum = 0;
							if (lendPebusers.getLendNum() != null) {
								lendNum = lendPebusers.getLendNum()
										+ log.getBorrowNum();
							} else {
								lendNum = log.getBorrowNum();
							}
							lendPebusers.setLendNum(lendNum);

							Integer borrowNum = 0;
							if (lendPebusers.getBorrowNum() != null) {
								borrowNum = lendPebusers.getBorrowNum();
							}

							// 计算日人均
							if (lendPebusers.getDangAnNum() != null) {
								float noChuQin = 0;
								if (lendPebusers.getNoChuQinNum() != null) {
									noChuQin = lendPebusers.getNoChuQinNum();
								}

								lendPebusers.setActualNum(lendPebusers
										.getDangAnNum()
										- lendNum + borrowNum - noChuQin);
								BigDecimal szBigDecimal = new BigDecimal(
										lendPebusers.getZsNumber());
								BigDecimal actualBigdecimal = new BigDecimal(
										lendPebusers.getActualNum());
								if (actualBigdecimal.floatValue() == 0) {
									lendPebusers.setAvgNumber(0f);
									lendPebusers.setUPPH(null);
								} else {
									float renjun = szBigDecimal.divide(
											actualBigdecimal, 5,
											BigDecimal.ROUND_HALF_UP)
											.floatValue();
									lendPebusers.setAvgNumber(renjun);
									if (lendPebusers.getGzHour() != null) {
										BigDecimal bigDecimal = new BigDecimal(
												renjun);
										BigDecimal bigDecimal2 = new BigDecimal(
												lendPebusers.getGzHour());
										float UPPH = bigDecimal.divide(
												bigDecimal2, 5,
												BigDecimal.ROUND_HALF_UP)
												.floatValue();
										lendPebusers.setUPPH(UPPH);
									}
								}
							}
							totalDao.update(lendPebusers);
							lendLog.setPebUsersId(lendPebusers.getId());

						}else {
							//如果没有人事档案信息，则添加
							lendPebusers = new PebUsers();
								//{"banZu":"焊接","dangAnNum":"78","day":"2","id":"8731","month":"1","noChuQinNum":"8.0","year":"2019"}
							lendPebusers.setBanZu(subTeam.getSubName());
							lendPebusers.setYear(year);
							lendPebusers.setMonth(month);
							lendPebusers.setDay(day);
							lendPebusers.setLendNum(log.getBorrowNum());
							totalDao.save(lendPebusers);
							lendLog.setPebUsersId(lendPebusers.getId());
							
						}
					}
					lendLog.setAddTime(Util.getDateTime());
					lendLog.setAddUserName(Util.getLoginUser().getName());
					lendLog.setLendNum(log.getBorrowNum());
					lendLog.setLendBanzu(log.getLendBanzu());
					lendLog.setBorrowBanzu(log.getBorrowBanzu());
					lendLog.setSqTime(log.getSqTime());
					totalDao.save(lendLog);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}
		
		if("访客申请".equals(circuitRun.getName())) {
			Visitor visitor = (Visitor) totalDao.getObjectById(Visitor.class, circuitRun.getEntityId());
			if(visitor!=null) {
				String dateFormat = "yyyy-MM-dd HH:mm:ss";
				String nowDateTime = Util.getDateTime(dateFormat);
				String applyDateTime = visitor.getDateTime()+Util.getDateTime(" HH:mm:ss");
				String newDateTime = null;
				String phone = null;
				try {
					if(Util.getDateDiff(nowDateTime, dateFormat, applyDateTime, dateFormat)>0) {
						newDateTime = visitor.getDateTime()+" 00:00:00";
					}else {
						newDateTime = visitor.getDateTime()+" "+Util.getDateTime("HH:mm:ss");
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
					throw new RuntimeException("系统处理日期异常");
				}
				Integer followId = visitor.getFollowId();
				if(followId!=null) {
					List<Visitor> list = totalDao.query("from Visitor where followId = ?", followId);
					for (Visitor visitor2 : list) {
						visitor2.setVisitorStatus("待打印");
						visitor2.setDateTime(newDateTime);
						totalDao.update(visitor2);
						if(visitor2.getVisitorPhone()!=null) {
							phone = visitor2.getVisitorPhone();
						}
					}
				}
//				Set<ExecutionNode> nodeSet = circuitRun.getExNodeSet();
//				String message  = "";
//				if(nodeSet!=null && nodeSet.size()>0) {
//					for (ExecutionNode executionNode : nodeSet) {
//						message = executionNode.getAuditOpinion();//审批意见
//					}
//				}
				
				StringBuffer contentBuffer = new StringBuffer();
				contentBuffer.append("尊敬的"+visitor.getVisitorName()+"，您好！您的访客申请已经通过，");
				//获取到当前地址
				AESEnctypeUtil util = new AESEnctypeUtil();
				String aeseAuth = util.enctype(visitor.getId()+"");
//				
				String address ="visitorAction.action?p="+aeseAuth;
				System.out.println("address="+AlertMessagesServerImpl.pebsUrl+"/"+address);
				String shortUrl=null;
				try {
//					String encodeAddress = URLEncoder.encode(AlertMessagesServerImpl.pebsUrl+"/"+address,"UTF-8");
//					System.out.println(encodeAddress);
//					String apiAddress = "http://h5ip.cn/index/api?format=json&url="+encodeAddress;
//					String result = PostUtil.getRequest(apiAddress);
//					JSONObject parseObject = JSON.parseObject(result);
//					String msg = parseObject.getString("msg");
//					if(msg!=null && msg.length()>0 ) {
//						throw new RuntimeException("短信发送异常："+URLDecoder.decode(msg, "UTF-8"));
//					}else {
//						shortUrl = parseObject.getString("short_url");
//					}
					shortUrl = ShortLinkServerImpl.addStaticShortLink(AlertMessagesServerImpl.pebsUrl+"/"+address, "visitor");
					
				} catch (Exception e) {
					shortUrl = AlertMessagesServerImpl.pebsUrl+"/"+address;
//					throw new RuntimeException("短信发送异常："+e.getMessage());
				}finally {
					if(shortUrl==null) {
						shortUrl = AlertMessagesServerImpl.pebsUrl+"/"+address;
					}
					contentBuffer.append("请凭链接中的二维码 "+shortUrl+" 到访客机打印凭证后通过门禁设备，谢谢");
					
					ShortMessageServiceImpl shortMessageServer=new ShortMessageServiceImpl();
					String send = shortMessageServer.send(phone, contentBuffer.toString());
					System.out.println(send);
					visitor.setSmsLine(shortUrl);
					totalDao.update(visitor);
				}
				
			}
		}

		if ("人员借入取消申请".equals(circuitRun.getName())
				&& circuitRun.getEntityName().equals("PebBorrowAndLendLog")) {
			PebBorrowAndLendLog log = (PebBorrowAndLendLog) totalDao
					.getObjectById(PebBorrowAndLendLog.class, circuitRun
							.getEntityId());

			// 修改借入人数
			log.setEpStatus("已取消");
			totalDao.update(log);

			PebUsers pebUsers = (PebUsers) totalDao.getObjectById(
					PebUsers.class, log.getPebUsersId());
			if (pebUsers != null) {
				List<PebBorrowAndLendLog> borrowAndLendLogList = totalDao
						.query(
								"from PebBorrowAndLendLog "
										+ " where pebUsersId =? and (epStatus is null or epStatus='同意')",
								pebUsers.getId());
				Integer borrowCount = 0;
				Integer lendCount = 0;
				for (PebBorrowAndLendLog pebBorrowAndLendLog : borrowAndLendLogList) {
					if (pebBorrowAndLendLog.getLendBanzu() != null
							&& pebBorrowAndLendLog.getLendNum() != null) {
						lendCount += pebBorrowAndLendLog.getLendNum();
					}
					if (pebBorrowAndLendLog.getBorrowBanzu() != null
							&& pebBorrowAndLendLog.getBorrowNum() != null) {
						borrowCount += pebBorrowAndLendLog.getBorrowNum();
					}
				}
				pebUsers.setBorrowNum(borrowCount);
				pebUsers.setLendNum(lendCount);
				if (pebUsers.getDangAnNum() != null) {
					float noChuQin = 0;
					if (pebUsers.getNoChuQinNum() != null) {
						noChuQin = pebUsers.getNoChuQinNum();
					}
					pebUsers.setActualNum(pebUsers.getDangAnNum() + borrowCount
							- lendCount - noChuQin);
				}
				if (pebUsers.getDangAnNum() != null
						&& pebUsers.getDangAnNum() != 0) {

					BigDecimal szBigDecimal = new BigDecimal(pebUsers
							.getZsNumber());
					Float actualNum = pebUsers.getActualNum();
					if (actualNum != null && actualNum != 0) {
						BigDecimal actualBigdecimal = new BigDecimal(pebUsers
								.getActualNum());
						float renjun = szBigDecimal.divide(actualBigdecimal, 5,
								BigDecimal.ROUND_HALF_UP).floatValue();
						pebUsers.setAvgNumber(renjun);
						if (pebUsers.getGzHour() != null) {
							BigDecimal bigDecimal = new BigDecimal(renjun);
							BigDecimal bigDecimal2 = new BigDecimal(pebUsers
									.getGzHour());
							float UPPH = bigDecimal.divide(bigDecimal2, 5,
									BigDecimal.ROUND_HALF_UP).floatValue();
							pebUsers.setUPPH(UPPH);
						}
					}
				}
				totalDao.update(pebUsers);
			}

			// 删除借出记录
			PebBorrowAndLendLog lendLog = (PebBorrowAndLendLog) totalDao
					.getObjectByCondition(
							"from PebBorrowAndLendLog "
									+ "where lendBanzu=? and borrowBanzu=? and lendNum=? and sqTime=?",
							log.getLendBanzu(), log.getBorrowBanzu(), log
									.getBorrowNum(), log.getSqTime());
			if (lendLog != null) {
				lendLog.setEpStatus("已取消");
				totalDao.update(lendLog);
				PebUsers lendPebUsers = (PebUsers) totalDao.getObjectById(
						PebUsers.class, lendLog.getPebUsersId());
				if (lendPebUsers != null) {
					List<PebBorrowAndLendLog> borrowAndLendLogList = totalDao
							.query(
									"from PebBorrowAndLendLog "
											+ " where pebUsersId =? and (epStatus is null or epStatus='同意')",
									lendPebUsers.getId());
					Integer borrowCount = 0;
					Integer lendCount = 0;
					for (PebBorrowAndLendLog pebBorrowAndLendLog : borrowAndLendLogList) {
						if (pebBorrowAndLendLog.getLendBanzu() != null
								&& pebBorrowAndLendLog.getLendNum() != null) {
							lendCount += pebBorrowAndLendLog.getLendNum();
						}
						if (pebBorrowAndLendLog.getBorrowBanzu() != null
								&& pebBorrowAndLendLog.getBorrowNum() != null) {
							borrowCount += pebBorrowAndLendLog.getBorrowNum();
						}
					}
					lendPebUsers.setBorrowNum(borrowCount);
					lendPebUsers.setLendNum(lendCount);
					if (lendPebUsers.getDangAnNum() != null) {
						float noChuQin = 0;
						if (lendPebUsers.getNoChuQinNum() != null) {
							noChuQin = lendPebUsers.getNoChuQinNum();
						}
						lendPebUsers.setActualNum(lendPebUsers.getDangAnNum()
								+ borrowCount - lendCount - noChuQin);
					}
					if (lendPebUsers.getDangAnNum() != null
							&& lendPebUsers.getDangAnNum() != 0) {

						BigDecimal szBigDecimal = new BigDecimal(lendPebUsers
								.getZsNumber());
						Float actualNum = lendPebUsers.getActualNum();
						if (actualNum != null && actualNum != 0) {
							BigDecimal actualBigdecimal = new BigDecimal(
									lendPebUsers.getActualNum());
							float renjun = szBigDecimal.divide(
									actualBigdecimal, 5,
									BigDecimal.ROUND_HALF_UP).floatValue();
							lendPebUsers.setAvgNumber(renjun);
							if (lendPebUsers.getGzHour() != null) {
								BigDecimal bigDecimal = new BigDecimal(renjun);
								BigDecimal bigDecimal2 = new BigDecimal(
										lendPebUsers.getGzHour());
								float UPPH = bigDecimal.divide(bigDecimal2, 5,
										BigDecimal.ROUND_HALF_UP).floatValue();
								lendPebUsers.setUPPH(UPPH);
							}
						}
					}
					totalDao.update(lendPebUsers);
				}
			}
		}

		if ("未出勤人数填报".equals(circuitRun.getName())
				&& circuitRun.getEntityName().equals("PebUsers")) {
			PebUsers pebUsers = (PebUsers) totalDao.getObjectById(
					PebUsers.class, circuitRun.getEntityId());
			if (pebUsers != null) {
				pebUsers.setNoChuQinNum(pebUsers.getApplyNum());
				pebUsers.setEpStatus("同意");
				String hql = "";
				if (pebUsers.getCategory() == null) {
					String isBanzu = (String) totalDao.getObjectByCondition(
							"select isBanzu from SubTeam where subName = ?",
							pebUsers.getBanZu());
					if (isBanzu != null) {
						if (isBanzu.equals("工序")) {
							pebUsers.setCategory("bj");
						}
					}
				}
				if (pebUsers.getCategory() != null
						&& pebUsers.getCategory().equals("bj")) {
					hql = "select sum(zsNumber) from PebProductionBanjin where processName=?";
				} else {
					hql = "select sum(zsNumber) from PebProduction where banzu=?";
				}
				Float zsNum = null;
				if (pebUsers.getBanZu().equals("数据机房")) {
					zsNum = (Float) totalDao
							.getObjectByCondition(
									hql
											+ " and zsNumber is not null and year=? and month=?",
									pebUsers.getBanZu(), pebUsers.getYear(),
									pebUsers.getMonth());
				} else {
					zsNum = (Float) totalDao
							.getObjectByCondition(
									hql
											+ " and zsNumber is not null and year=? and month=? and day=?",
									pebUsers.getBanZu(), pebUsers.getYear(),
									pebUsers.getMonth(), pebUsers.getDay());
				}
				pebUsers.setZsNumber(zsNum);
				if (pebUsers.getDangAnNum() != null
						&& pebUsers.getDangAnNum() != 0) {

					BigDecimal szBigDecimal = new BigDecimal(zsNum);
					Float actualNum = pebUsers.getActualNum();
					if (actualNum != null && actualNum != 0) {
						BigDecimal actualBigdecimal = new BigDecimal(pebUsers
								.getActualNum());
						float renjun = szBigDecimal.divide(actualBigdecimal, 5,
								BigDecimal.ROUND_HALF_UP).floatValue();
						pebUsers.setAvgNumber(renjun);
						if (pebUsers.getGzHour() != null) {
							BigDecimal bigDecimal = new BigDecimal(renjun);
							BigDecimal bigDecimal2 = new BigDecimal(pebUsers
									.getGzHour());
							float UPPH = bigDecimal.divide(bigDecimal2, 5,
									BigDecimal.ROUND_HALF_UP).floatValue();
							pebUsers.setUPPH(UPPH);
						}
					}
				}
				totalDao.update(pebUsers);
			}
		}

		if ("文件受控反审流程".equals(circuitRun.getName())) {
			SystemFile systemFile = (SystemFile) totalDao.getObjectById(
					SystemFile.class, circuitRun.getEntityId());
			if (systemFile != null) {
				if ("同意".equals(circuitRun.getAllStatus())) {
					systemFile.setStatus("反审成功");
				} else if ("打回".equals(circuitRun.getAllStatus())) {
					systemFile.setStatus("反审失败");
				}
				totalDao.update(systemFile);
			}
		}

		if ("月度职工薪酬审批".equals(circuitRun.getName())
				|| "月度公积金审批".equals(circuitRun.getName())
				|| "月度社保审批".equals(circuitRun.getName())
				|| "月度个税审批".equals(circuitRun.getName())) {
			WagePay wagePay = (WagePay) totalDao.getObjectById(WagePay.class,
					circuitRun.getEntityId());
			wagePay.setPayStatus("付款");
			wagePay.setEpStatus("同意");
			totalDao.update(wagePay);
			if ("月度职工薪酬审批".equals(circuitRun.getName())) {
				String sql = "update ta_fin_wage set ta_fin_wagestatus='同意' where  ta_fin_wagestatus='审核' and ta_fin_mouth=?";
				totalDao.createQueryUpdate(null, sql, wagePay.getMonth());
			}
			Receipt receipt = new Receipt();
			receipt.setPayee(wagePay.getPayee());
			receipt.setPayeeId(null);
			receipt.setSummary(wagePay.getMonth() + wagePay.getType() + "费用");
			receipt.setPayType(wagePay.getType());
			receipt.setAboutNum(null);
			receipt.setFk_monthlyBillsId(null);
			receipt.setAllMoney(wagePay.getAllmoney().floatValue());
			receipt.setPaymentCycle(0);
			receipt.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));
			ReceiptServerImpl tsi = new ReceiptServerImpl();
			tsi.setTotalDao(totalDao);
			tsi.addReceipt(receipt);

		}

		if("个人日程审批流程".equals(circuitRun.getName())){
			Calendar calendar=(Calendar) totalDao.getObjectById(Calendar.class,circuitRun.getEntityId());
			String CompT=Util.getDateTime("yyyy-MM-dd HH:mm:ss");
			calendar.setCompTime(CompT);
			Long times;
			try {
				times = Util.getDateDiff(calendar.getViewTime(),
						"yyyy-MM-dd HH:mm:ss", CompT, "yyyy-MM-dd HH:mm:ss");
				Float hours = Float.parseFloat(String.format("%.2f",
						times / 3600F));
				calendar.setView2compPeriod(hours);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			calendar.setCalendarState("完成");
			totalDao.update(calendar);
		}
		if ("月度薪酬总额审批".equals(circuitRun.getName())
				|| "月度公积金审批".equals(circuitRun.getName())
				|| "月度社保审批".equals(circuitRun.getName())
				|| "月度个税审批".equals(circuitRun.getName())) {
			WagePay wagePay = (WagePay) totalDao.getObjectById(WagePay.class,
					circuitRun.getEntityId());
			wagePay.setPayStatus("付款");
			wagePay.setEpStatus("同意");
			totalDao.update(wagePay);
			String sql = "update ta_fin_wage set ta_fin_wagestatus='同意' where  ta_fin_wagestatus='审核' and ta_fin_mouth=?";
			totalDao.createQueryUpdate(null, sql, wagePay.getMonth());

			Receipt receipt = new Receipt();
			receipt.setPayee("职工薪酬");
			receipt.setPayeeId(null);
			receipt.setSummary(wagePay.getMonth() + "职工薪酬" + "费用");
			receipt.setPayType("职工薪酬");
			receipt.setAboutNum(null);
			receipt.setFk_monthlyBillsId(null);
			receipt.setAllMoney(wagePay.getZgscmoney().floatValue());
			receipt.setPaymentCycle(0);
			receipt.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));

			Receipt receipt1 = new Receipt();
			receipt1.setPayee("上海市住房公积金管理中心");
			receipt1.setPayeeId(null);
			receipt1.setSummary(wagePay.getMonth() + "公积金" + "费用");
			receipt1.setPayType("公积金");
			receipt1.setAboutNum(null);
			receipt1.setFk_monthlyBillsId(null);
			receipt1.setAllMoney(wagePay.getGjjmoney().floatValue());
			receipt1.setPaymentCycle(0);
			receipt1.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));

			Receipt receipt2 = new Receipt();
			receipt2.setPayee("上海市社会保险事业管理中心");
			receipt2.setPayeeId(null);
			receipt2.setSummary(wagePay.getMonth() + "社保" + "费用");
			receipt2.setPayType("社保");
			receipt2.setAboutNum(null);
			receipt2.setFk_monthlyBillsId(null);
			receipt2.setAllMoney(wagePay.getSbmoney().floatValue());
			receipt2.setPaymentCycle(0);
			receipt2.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));

			Receipt receipt3 = new Receipt();
			receipt3.setPayee("税务局");
			receipt3.setPayeeId(null);
			receipt3.setSummary(wagePay.getMonth() + "个税" + "费用");
			receipt3.setPayType("个税");
			receipt3.setAboutNum(null);
			receipt3.setFk_monthlyBillsId(null);
			receipt3.setAllMoney(wagePay.getGsmoney().floatValue());
			receipt3.setPaymentCycle(0);
			receipt3.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));

			ReceiptServerImpl tsi = new ReceiptServerImpl();
			tsi.setTotalDao(totalDao);
			tsi.addReceipt(receipt);
			tsi.addReceipt(receipt1);
			tsi.addReceipt(receipt2);
			tsi.addReceipt(receipt3);

		}
		
		if ("快递员注册流程".equals(circuitRun.getName())) {
			Courier courier = (Courier) totalDao.getObjectById(
					Courier.class, circuitRun.getEntityId());
			if (courier != null) {
				if ("同意".equals(circuitRun.getAllStatus())) {							
					String Scode = "尊敬的"+courier.getCouName()+"您好!您申请我们的快递柜投放已经通过审核,谢谢您的信任,我们期待您的使用";  //加上注册信息链接
					String return_code = new ShortMessageServiceImpl().send(courier.getPhoneNumber(), Scode);
					System.out.println("输出一下审批同意后短信是否发送"+return_code);
				} else if ("打回".equals(circuitRun.getAllStatus())) {
					String Scode = "尊敬的"+courier.getCouName()+"您好!您申请我们的快递柜投放因证件不清晰,未通过审核,请上传清晰证件照,保证审核成功率";  //加上注册信息链接
					String return_code = new ShortMessageServiceImpl().send(courier.getPhoneNumber(), Scode);
					System.out.println("输出一下审批未通过后短信是否发送"+return_code);
				}
				totalDao.update(courier);
			}
		}

		/** 请假，加班，来访申请审批同意后生成进出门凭证流程操作start 李聪 **/
		// 请假
		AskForLeave(circuitRun, totalDao);
		// 加班
		OverTime(circuitRun, totalDao);
		// 来访申请
		Visit(circuitRun, totalDao);
		/** 请假，加班，来访申请审批同意后生成进出门凭证流程操作end 李聪 **/
		// 常访申请
		ChangFang(circuitRun, totalDao);
		// 档案存档申请 生成验证码
		Dangan(circuitRun, totalDao);
		// 印章申请同意后续操作
		SealLog(circuitRun, totalDao);
		// 非主营应收申请同意后续操作
		NonCoreReceivables(circuitRun, totalDao);
		// 离职工资单同意后续操作
		Dimission_ZhengYi(circuitRun, totalDao);
		// 证件信息表
		Credentials(circuitRun, totalDao);
		// 考勤汇总补卡申请
		Attendance(circuitRun, totalDao);
		// 外购订单计算订单总额
		// WaigouOrder(circuitRun, totalDao);

		// 系统需求
		SystemDemand(circuitRun, totalDao);

		return "";
	}

	private static String deletesgww(Integer entityId, TotalDao totalDao2) {
		// TODO Auto-generated method stub
		ProcessInfordeleteApply pa = (ProcessInfordeleteApply) totalDao2.getObjectById(ProcessInfordeleteApply.class, entityId);
		if(pa!=null){
			List<ProcessInforWWApplyDetail> pwwadList = totalDao2.query("from ProcessInforWWApplyDetail where " +
					"id in (select processinforwwDetailId from ProcessInfordeleteApplyDetail where pida.id=?)", entityId);
			if(pwwadList!=null&&pwwadList.size()>0){
				for(ProcessInforWWApplyDetail pwwad:pwwadList){
					deletesgwwdetail(null,pwwad, totalDao2);
				}
			}
		}
		return null;
	}

	private static String wgjd(Integer entityId, TotalDao totalDao2) {
		// TODO Auto-generated method stub
		WaigouPlanclApply wgclApply = (WaigouPlanclApply) totalDao2
				.getObjectById(WaigouPlanclApply.class, entityId);
		if (wgclApply != null) {
			ProcardSbWg oldsbwg = (ProcardSbWg) totalDao2.getObjectById(
					ProcardSbWg.class, wgclApply.getProcardSbWgId());
			WaigouPlan oldplan = (WaigouPlan) totalDao2.getObjectById(
					WaigouPlan.class, wgclApply.getWaigouPlanId());
			ProcardSbWgLog psbwLog = (ProcardSbWgLog) totalDao2
					.getObjectByCondition(
							"from ProcardSbWgLog where clApplyId=?", entityId);
			if (oldsbwg != null && oldplan != null && psbwLog != null) {
				WaigouOrder waigouOrder = oldplan.getWaigouOrder();
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				String status = "完成";
				oldplan.setStatus(oldplan.getOldStatus());
				oldplan.setNumber(oldplan.getNumber() - psbwLog.getCount());
				oldplan.setSyNumber(oldplan.getSyNumber()-psbwLog.getCount());
				oldplan.setMoney(oldplan.getNumber()*oldplan.getHsPrice());
				if (oldplan.getSbjdCount() == null) {
					oldplan.setSbjdCount(psbwLog.getCount());
				} else {
					oldplan.setSbjdCount(oldplan.getSbjdCount()
							+ psbwLog.getCount());
				}
				if (oldplan.getNumber() == 0) {
					oldplan.setStatus("设变扣单");
				}
				// 处理物料需求上的数量.
				ManualOrderPlan mop = (ManualOrderPlan) totalDao2.get(
						ManualOrderPlan.class, oldplan.getMopId());
				if (mop != null) {

					mop.setNumber(mop.getNumber() - psbwLog.getCount());
					if(mop.getWshCount()!=null && mop.getWshCount()>0){
						mop.setWshCount(mop.getWshCount()-psbwLog.getCount());
					}
					if(mop.getNumber()<0){
						mop.setNumber(0f);
					}
					if (mop.getCancalNum() == null) {
						mop.setCancalNum(psbwLog.getCount());
					} else {
						mop.setCancalNum(mop.getCancalNum()
								+ psbwLog.getCount());
					}
					Set<ManualOrderPlanDetail> modSet = mop.getModSet();
					Float jdcount = psbwLog.getCount();
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
						totalDao2.update(mod);
					}
					mop.setOutcgNumber(mop.getOutcgNumber()
							- psbwLog.getCount());
					mop.setWshCount(mop.getWshCount() - psbwLog.getCount());
					mop.setEpCancalStatus("设变扣单");
					totalDao2.update(mop);
				}
				// 同时修改采购订单的状态
				WaigouOrder waigouordere = oldplan.getWaigouOrder();
				waigouordere.setStatus(waigouordere.getOldstatus());
				totalDao2.update(oldplan);
				totalDao2.update(waigouordere);
				AlertMessagesServerImpl.addAlertMessages(waigouOrder.getType()
						+ "订单设变减单协商", "尊敬的【" + waigouOrder.getTzUserName()
						+ "】,您好:\n订单号:【" + waigouOrder.getPlanNumber()
						+ "】的订单数量发生改变请前往查看" + waigouOrder.getLxPeople() + "("
						+ waigouOrder.getGysPhone() + ")。\n【"
						+ waigouOrder.getGysName() + "】\n["
						+ companyInfo.getName() + "]", "1", waigouOrder
						.getAddUserCode());
				// if (oldplan.getStatus().equals("待核对")
				// || oldplan.getStatus().equals("待通知")) {// 订单还未到供应商可以直接修改数量
				//
				// } else {//
				// status = "待处理";
				// if (oldplan.getSbjdApplyCount() == null) {
				// oldplan.setSbjdApplyCount(psbwLog.getCount());
				// } else {
				// oldplan.setSbjdApplyCount(oldplan.getSbjdApplyCount()
				// + psbwLog.getCount());
				// }
				// AlertMessagesServerImpl.addAlertMessages(waigouOrder
				// .getType()
				// + "订单设变减单协商", "尊敬的【" + waigouOrder.getTzUserName()
				// + "】,您好:\n订单号:【" + waigouOrder.getPlanNumber()
				// + "】的订单数量发生改变请前往查看" + waigouOrder.getLxPeople()
				// + "(" + waigouOrder.getGysPhone() + ")。\n【"
				// + waigouOrder.getGysName() + "】\n["
				// + companyInfo.getName() + "]", "1", waigouOrder
				// .getAddUserCode());
				// /****** 推送邮件 ******/
				// // // 查询供应商信息
				// // ZhUser zhuser = (ZhUser) totalDao.getObjectById(
				// // ZhUser.class, waigouOrder.getGysId());
				// // if (zhuser != null && zhuser.getYx() != null
				// // && zhuser.getYx().length() > 0) {
				// // zhuser.setYx(zhuser.getYx().replaceAll("；", ";"));
				// // String[] yxs = zhuser.getYx().split(";");
				// // Set<WaigouPlan> planSets = waigouOrder.getWwpSet();
				// // String caigouDetail = "";
				// // String maildetail = "(测试数据)尊敬的【"
				// // + waigouOrder.getLxPeople()
				// // + "】先生/女士,您好:<br/>您订单有减单请求,订单号:【"
				// // + waigouOrder.getPlanNumber()
				// // + "】。<br/><div id='printdiv'>"
				// // +
				// //
				// "<div align='center' style='font-size: 20px; height: 25px;'>"
				// // +
				// //
				// "零件号:"+oldplan.getMarkId()+","+"减单数量:"+waigouplan.getSbjdApplyCount()+oldplan.getUnit()+";"
				// // + "</div>"
				// // +
				// //
				// "<div align='center' style='font-size: 20px; height: 25px;'>"
				// // + companyInfo.getName()
				// // + "</div>"
				// // +
				// //
				// "<div align='center' style='font-size: 14px; height: 25px;'>"
				// // + companyInfo.getEnglishName()
				// // + "</div>"
				// // + "<div align='right' style='font-size: 12px;'>"
				// // + "电话:"
				// // + companyInfo.getTel()
				// // +
				// //
				// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				// // + "传真:"
				// // + companyInfo.getFax()
				// // +
				// //
				// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				// // +
				// //
				// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第 1 页/共 1"
				// // +
				// //
				// "页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				// // + "</div>"
				// // +
				// //
				// "</div><br/><font color='red'>以下地址进入:<a href='http://pebs.i-brights.cn/'>PEBS(请点击)</a><br/>用户名:"
				// // + waigouOrder.getUserCode()
				// // + "<br/>初始密码:123456(请及时修改密码)<br/>["
				// // + companyInfo.getName()
				// // + "]<br/>"
				// // + "注意:本邮件由系统发送,请勿直接回复。<br/>具体事项请联系"
				// // + waigouOrder.getTzUserName()
				// // + ",手机号:"
				// // + waigouOrder.getTzUserPhone()
				// // + "/邮箱:"
				// // + waigouOrder.getAddUserYx() + "</font>";
				// // for (String yx : yxs) {
				// // // 发送邮件提醒
				// // SendMail sendMail = new SendMail();
				// //
				// // sendMail
				// // .sendMail(yx, "采购订单确认(供应商)", maildetail);
				// // }
				// // }
				// totalDao2.update(oldplan);
				// }

				psbwLog.setStatus(status);
				if (oldsbwg.getClCount() == null) {
					oldsbwg.setClCount(psbwLog.getCount());
				} else {
					oldsbwg.setClCount(oldsbwg.getClCount()
							+ psbwLog.getCount());
				}
				totalDao2.update(oldsbwg);

			}

		}

		return null;
	}

	private static String deletesgwwdetail(Integer entityId,ProcessInforWWApplyDetail detail, TotalDao totalDao2) {
		// TODO Auto-generated method stub
		if(detail==null){
			detail = (ProcessInforWWApplyDetail) totalDao2
			.getObjectById(ProcessInforWWApplyDetail.class, entityId);
		}
		if (detail != null) {
			ProcessInforWWApply apply = detail.getProcessInforWWApply();
			String applyStatus = apply.getStatus();
			Procard procard = (Procard) totalDao2.getObjectById(Procard.class,
					detail.getProcardId());
			if (procard != null && detail.getProcessNOs() != null
					&& detail.getProcessNOs().length() > 0) {
				String[] processNOS = detail.getProcessNOs().split(";");
				if (processNOS != null && processNOS.length > 0) {
					for (String processNOStr : processNOS) {
						try {
							Integer processNO = Integer.parseInt(processNOStr);
							ProcessInfor process = (ProcessInfor) totalDao2
									.getObjectByCondition(
											"from ProcessInfor where processNO=? and (dataStatus is null or dataStatus!='删除') and procard.id=?",
											processNO, procard.getId());
							if (process != null) {// 将外委打回数量回传到工序上
								if (detail.getDataStatus() == null
										|| (!detail.getDataStatus()
												.equals("取消") && !detail
												.getDataStatus().equals("删除"))) {
									if (applyStatus.equals("未申请")
											|| applyStatus.equals("打回")) {
										process.setSelectWwCount(process
												.getSelectWwCount()
												- detail.getApplyCount());
										if (process.getSelectWwCount() < 0) {
											process.setSelectWwCount(0f);
										}
									} else if (applyStatus.equals("未审批")
											|| applyStatus.equals("审批中")) {
										process.setApplyWwCount(process
												.getApplyWwCount()
												- detail.getApplyCount());
										if (process.getApplyWwCount() < 0) {
											process.setApplyWwCount(0f);
										}

									} else {
										process.setAgreeWwCount(process
												.getAgreeWwCount()
												- detail.getApplyCount());
										if (process.getAgreeWwCount() < 0) {
											process.setAgreeWwCount(0f);
										}
									}
									totalDao2.update(process);
								}
							}

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					// 还原下层被占数量
					if ((detail.getDataStatus() == null || (!detail
							.getDataStatus().equals("取消") && !detail
							.getDataStatus().equals("删除")))
							&& !applyStatus.equals("打回")
							&& detail.getWwType() != null
							&& detail.getWwType().equals("包工包料")) {// 包工包料回传采购
						List<ProcessInforWWProcard> processwwprocardList = totalDao2
								.query(
										"from ProcessInforWWProcard where applyDtailId=?",
										detail.getId());
						if (processwwprocardList != null
								&& processwwprocardList.size() > 0) {
							for (ProcessInforWWProcard processInforWWProcard : processwwprocardList) {
								Procard wgProcard = (Procard) totalDao2
										.getObjectById(Procard.class,
												processInforWWProcard
														.getProcardId());
								if (wgProcard != null) {
									wgProcard.setWwblCount(wgProcard
											.getWwblCount()
											- detail.getApplyCount()
											* wgProcard.getQuanzi2()
											/ wgProcard.getQuanzi1());
									if (wgProcard.getWwblCount() < 0) {
										wgProcard.setWwblCount(0f);
									}
									totalDao2.update(wgProcard);
								}
							}
						}
						// 归还下层半成品,自制件和组合（下层组合将整体被包公包料）
						backProcardWwblCount(procard, detail.getApplyCount(),
								0, totalDao2);
						Procard rootp = (Procard) totalDao2
								.getObjectByCondition(
										"from Procard where id=(select rootId from Procard where id=? )",
										detail.getProcardId());
						if (rootp != null) {
							rootp.setWlstatus("待定");
							totalDao2.update(rootp);
						}
					}
				}
			}
			List<ProcessInforWWProcard> processWWprocardList = totalDao2.query(
					"from ProcessInforWWProcard where applyDtailId=?", detail
							.getId());
			if (processWWprocardList != null && processWWprocardList.size() > 0) {
				for (int i = (processWWprocardList.size() - 1); i >= 0; i--) {
					ProcessInforWWProcard processWWprocard = processWWprocardList
							.get(i);
					totalDao2.delete(processWWprocard);
				}
			}
			detail.setDataStatus("删除");
			detail.setProcessStatus("删除");
			totalDao2.update(detail);
			if (!detail.getProcessInforWWApply().getStatus().equals("未申请")) {
				Float yyCount = (Float) totalDao2
						.getObjectByCondition(
								"select count(*) from ProcessInforWWApplyDetail where processInforWWApply.id=? "
										+ "and (dataStatus is null or dataStatus !='删除')",
								detail.getProcessInforWWApply().getId());
				if (yyCount == 0) {
					apply.setProcessStatus("删除");
					apply.setStatus("删除");
					totalDao2.update(apply);
				}
			}
		}

		return null;
	}

	/**
	 * 归还下层外委包料数量
	 * 
	 * @param procard
	 * @param wwblCount
	 * @param type
	 * @return
	 */
	private static String backProcardWwblCount(Procard procard,
			Float wwblCount, int type, TotalDao totalDao2) {
		// TODO Auto-generated method stub
		Set<Procard> sonset = procard.getProcardSet();
		if (sonset != null && sonset.size() > 0) {
			for (Procard son : sonset) {
				if (!son.getProcardStyle().equals("外购")) {
					if (son.getWwblCount() != null) {
						son.setWwblCount(son.getWwblCount() - wwblCount
								* son.getCorrCount());
						if (son.getWwblCount() < 0) {
							son.setWwblCount(0f);
						}
						totalDao2.update2(son);
						backProcardWwblCount(son, wwblCount
								* son.getCorrCount(), 1, totalDao2);
					}
				} else {
					if (type != 0
							|| (son.getNeedProcess() != null && son
									.getNeedProcess().equals("yes"))) {
						if (son.getWwblCount() != null) {
							son.setWwblCount(son.getWwblCount() - wwblCount
									* son.getQuanzi2() / son.getQuanzi1());
							if (son.getWwblCount() < 0) {
								son.setWwblCount(0f);
							}
							totalDao2.update2(son);
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * 生产退料单申请同意后续
	 * 
	 * @param circuitRun
	 * @param totalDao2
	 */
	private static void procard(CircuitRun circuitRun, TotalDao totalDao2) {
		// TODO Auto-generated method stub
		Procard procard = (Procard) totalDao2.getObjectById(Procard.class,
				circuitRun.getEntityId());
		if (procard != null) {
			procard.setHascount(procard.getHascount()
					+ procard.getStuiLiaoNumber());
			procard
					.setYtuiLiaoNumber(procard.getYtuiLiaoNumber() == null ? procard
							.getStuiLiaoNumber()
							: procard.getStuiLiaoNumber()
									+ procard.getYtuiLiaoNumber());
			ProcardBlServerImpl.addsellTui(procard);
			totalDao2.update(procard);
		}
	}

	/**
	 * 借款后续操作
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void paymentVoucher(CircuitRun circuitRun, TotalDao totalDao) {
		// 审批同意后做后续操作
		String createdate1 = Util.getDateTime("yyyy");
		String createdate2 = Util.getDateTime("yyyy-MM-dd");
		String module_name1 = circuitRun.getEntityName();// 获得模块名称数
		String hql = "from Password where user.id=?";
		Password password = (Password) totalDao.getObjectByQuery(hql, Util
				.getLoginUser().getId());
		String deptNumber = password.getDeptNumber();// 获得部门编号
		Users users = (Users) totalDao.getObjectById(Users.class, Util
				.getLoginUser().getId());
		String post = users.getPost();// 获得职称
		String hql1 = "select max(se_number) from Sequences where code="
				+ users.getCode();
		String max_number = (String) totalDao.getObjectByQuery(hql1);
		String hql2 = "from Sequences";
		Sequences sequences = (Sequences) totalDao.getObjectByQuery(hql2);
		String se_number = "";
		if (sequences == null) {
			se_number = deptNumber + "-" + post + "-" + createdate1 + "001";
		} else {
			if (max_number != null && !"".equals(max_number)) {
				String[] numberList = max_number.split("-");
				long number2 = Long.parseLong(numberList[2]) + 1;
				String number3 = number2 + "";
				se_number = deptNumber + "-" + post + "-" + number3;
			}
		}
		sequences = new Sequences();
		sequences.setCode(Util.getLoginUser().getCode());
		sequences.setModule_name(module_name1);
		sequences.setSe_number(se_number);
		sequences.setSe_date(createdate2);
		totalDao.save(sequences);

		/********* 借款同意后自动生成凭证 ************/
		PaymentVoucher paymentVoucher = (PaymentVoucher) totalDao
				.getObjectById(PaymentVoucher.class, circuitRun.getEntityId());
		CwPingZheng cwpz = new CwPingZheng();
		Users loginUser = Util.getLoginUser();
		cwpz.setUserName(loginUser.getName());
		cwpz.setUserDept(loginUser.getDept());
		cwpz.setUserId(loginUser.getId());
		cwpz.setSubject(paymentVoucher.getApprovalApplier() + "的借款");
		cwpz.setMoney(paymentVoucher.getVoucherMoney().doubleValue());
		cwpz.setProcessStatus("待入");
		cwpz.setAddDateTime(Util.getDateTime());
		cwpz
				.setDetailUrl("paymentVoucherAction_salPaymentDetail.action?test=2&paymentVoucher.id="
						+ paymentVoucher.getId());
		totalDao.save(cwpz);
		/********* 借款同意后自动生成凭证完成 ************/
	}

	/**
	 * 资金使用申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	public static void fundApply(CircuitRun circuitRun, TotalDao totalDao) {
		FundApply fundApply = (FundApply) totalDao.getObjectById(
				FundApply.class, circuitRun.getEntityId());
		if (fundApply != null) {
			if ("支付".equals(fundApply.getZhifuoryufu())
					&& "冲账".equals(fundApply.getAbout())) {
				// 查询该收款单位在付款汇总表是否存在
				String hql_SupplierCorePayable = "from SupplierCorePayable where supplierName=? and payableType='收款'";
				SupplierCorePayable scp = (SupplierCorePayable) totalDao
						.getObjectByCondition(hql_SupplierCorePayable,
								fundApply.getRelationclient());
				if (scp != null) {
					if (scp.getYingfukuanJine() == null) {
						scp.setYingfukuanJine(0F);
					}
					if (scp.getWeifukuanJine() == null) {
						scp.setWeifukuanJine(0F);
					}
					// scp.setYingfukuanJine(scp.getYingfukuanJine()
					// - fundApply.getTotalMoney().floatValue());
					scp.setWeifukuanJine(scp.getWeifukuanJine()
							- fundApply.getTotalMoney().floatValue());
					scp.setRealfukuanJine(scp.getRealfukuanJine()
							+ fundApply.getTotalMoney().floatValue());
					totalDao.update(scp);
				}
			} else {
				if (fundApply != null) {
					if("预付".equals(fundApply.getZhifuoryufu())){
						fundApply.setStatus("待还款");
					}else{
						fundApply.setStatus("财务确认");
					}
					totalDao.update(fundApply);
				}

				// String jingbanren = "";
				// if ("预付".equals(fundApply.getZhifuoryufu())
				// && "是".equals(fundApply.getJingbanren())) {
				// jingbanren = fundApply.getApprovalApplier();
				// }

				// 添加付款单
				Receipt receipt = new Receipt();
				// 生成编号
				String num = "APB" + Util.getDateTime("yyyyMM");
				String hql_finMaxnum = "select max(pkNumber) from Receipt where pkNumber like '%"
						+ num + "%'";
				String maxfkNumber = (String) totalDao
						.getObjectByCondition(hql_finMaxnum);
				if (maxfkNumber != null && maxfkNumber.length() > 0) {
					String subnum = "";
					Integer maxnum = Integer.parseInt(maxfkNumber.substring(9,
							maxfkNumber.length())) + 1;
					if (maxnum < 10) {
						subnum += "0000" + maxnum;// 00009
					} else if (maxnum < 100) {
						subnum += "000" + maxnum;// 00099
					} else if (maxnum < 1000) {
						subnum += "00" + maxnum;// 00999
					} else if (maxnum < 10000) {
						subnum += "0" + maxnum;// 09999
					} else {
						subnum += "" + maxnum;
					}
					num += subnum;
				} else {
					num += "00001";
				}
				receipt.setPkNumber(num);
				receipt.setPayee(fundApply.getRelationclient());
				receipt.setPayeeId(fundApply.getRelationclientId());
				if (receipt.getPayeeId() != null) {
					Payee payee = (Payee) totalDao.getObjectById(Payee.class,
							receipt.getPayeeId());
					if (payee != null) {
						if (payee.getCode() != null
								&& payee.getCode().length() > 0) {
							Users users = (Users) totalDao
									.getObjectByCondition(
											"from Users where code=?", payee
													.getCode());
							if (users != null) {
								receipt.setUserId(users.getId());
							}
						}
					}
				}
				receipt.setSummary(fundApply.getExplain());
				receipt.setPayType("费用报销");
				receipt.setAboutNum(fundApply.getNumber());
				receipt.setFk_fundApplyId(fundApply.getId());
				receipt.setAllMoney(fundApply.getTotalMoney().floatValue());
				receipt.setAccountPaid(0F);
				receipt.setUnPay(fundApply.getTotalMoney().floatValue());
				receipt.setPayIng(0F);
				receipt.setPayOn(0F);
				receipt.setPayLast(fundApply.getTotalMoney().floatValue());
				receipt.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));
				receipt.setPaymentCycle(0);
				receipt.setFukuanMonth(Util.getDateTime("yyyy-MM"));
				receipt.setStatus("待付款");

				receipt.setAddTime(Util.getDateTime());
				receipt.setAddUserCode(Util.getLoginUser().getCode());
				receipt.setAddUserName(Util.getLoginUser().getName());
				totalDao.save(receipt);

				// 添加月度应付账单
				String hql_MonthPayableBill = "from MonthPayableBill where jzMonth=?";
				MonthPayableBill monthPayableBill = (MonthPayableBill) totalDao
						.getObjectByCondition(hql_MonthPayableBill, receipt
								.getFukuanMonth());
				if (monthPayableBill != null) {
					monthPayableBill.setYingfukuanJine(monthPayableBill
							.getYingfukuanJine()
							+ receipt.getAllMoney());
					monthPayableBill.setWeifukuanJine(monthPayableBill
							.getWeifukuanJine()
							+ receipt.getAllMoney());
					totalDao.update(monthPayableBill);
				} else {
					monthPayableBill = new MonthPayableBill();
					monthPayableBill.setJzMonth(receipt.getFukuanMonth());
					monthPayableBill.setYingfukuanJine(receipt.getAllMoney());
					monthPayableBill.setRealfukuanJine(0F);
					monthPayableBill.setWeifukuanJine(receipt.getAllMoney());
					monthPayableBill.setYingshoukuanJine(0F);
					monthPayableBill.setRealshoukuanJine(0F);
					monthPayableBill.setWeishoukuanJine(0F);
					monthPayableBill.setStatus("");
					monthPayableBill.setSaveTime(Util.getDateTime());
					monthPayableBill.setSaveUser(Util.getLoginUser().getName());
					totalDao.save(monthPayableBill);
				}

				// 添加应付汇总
				// 查询该收款单位在付款汇总表是否存在
				String hql_SupplierCorePayable = "from SupplierCorePayable where supplierName=? and payableType='付款'";
				SupplierCorePayable scp = (SupplierCorePayable) totalDao
						.getObjectByCondition(hql_SupplierCorePayable, receipt
								.getPayee());
				if (scp != null) {
					if (scp.getYingfukuanJine() == null) {
						scp.setYingfukuanJine(0F);
					}
					if (scp.getWeifukuanJine() == null) {
						scp.setWeifukuanJine(0F);
					}
					scp.setYingfukuanJine(scp.getYingfukuanJine()
							+ receipt.getAllMoney());
					scp.setWeifukuanJine(scp.getWeifukuanJine()
							+ receipt.getAllMoney());
					totalDao.update(scp);
				} else {
					scp = new SupplierCorePayable();
					scp.setPayeeId(fundApply.getRelationclientId());
					scp.setSupplierName(receipt.getPayee());
					scp.setCoreType("非主营");
					scp.setPayableType("付款");
					scp.setYingfukuanJine(receipt.getAllMoney());
					scp.setRealfukuanJine(0F);
					scp.setWeifukuanJine(receipt.getAllMoney());
					scp.setFukuanzhongJine(0F);
					scp.setFkCount(0);
					scp.setFukuanzhongJine(0F);
					scp.setAddTime(Util.getDateTime());
					totalDao.save(scp);
				}
			}
		}
	}

	/**
	 * 销假申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void qxAskForLeave(CircuitRun circuitRun, TotalDao totalDao) {
		QxAskForLeave qxAskForLeave = (QxAskForLeave) totalDao.get(
				QxAskForLeave.class, circuitRun.getEntityId());
		Users users = (Users) totalDao.getObjectByCondition(
				" from Users where code = ? ", qxAskForLeave.getUserCode());
		AskForLeave ask = (AskForLeave) totalDao.get(AskForLeave.class,
				qxAskForLeave.getLeaveId());
		if (users != null) {
			// Float day1 = 0f;
			BanCi banci = (BanCi) totalDao
					.get(BanCi.class, users.getBanci_id());
			Float gzTime = banci.getGzTime() / 60f;
			if (gzTime == null || gzTime == 0) {
				gzTime = 8f;
			}
			float[] askTime = null;
			float[] qxTime = null;
			try {
				askTime = leaveServer.computeDayAndHourByTime(ask
						.getLeaveStartDate(), ask.getLeaveEndDate(), users
						.getId());
				qxTime = leaveServer.computeDayAndHourByTime(qxAskForLeave
						.getStartDate(), qxAskForLeave.getEndDate(), users
						.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			float askDay = askTime[0] + askTime[1] / gzTime;
			float qxDay = qxTime[0] + qxTime[1] / gzTime;
			float gjDay = askDay - qxDay;
			// if (ask.getLeaveDays() != null
			// && ask.getLeaveDays() > 0) {
			// day1 = ask.getLeaveDays() + 0f;
			// } else if (ask.getLeaveHours() != null
			// && ask.getLeaveHours() > 0) {
			// day1 = ask.getLeaveHours() / gzTime;
			// }
			// Float bjday = day1 - qxAskForLeave.getDays();
			WageStandard wageStandard = (WageStandard) totalDao
					.getObjectByCondition(
							" from WageStandard where code = ? order by id desc",
							users.getCode());
			if (wageStandard != null) {
				Float yingfagongzi = wageStandard.getYingfagongzi();
				if (yingfagongzi == null || yingfagongzi == 0) {
					yingfagongzi = 2190f;
				}
				Float jixiaokaohegongzi = wageStandard.getJixiaokaohegongzi();
				if (jixiaokaohegongzi == null && jixiaokaohegongzi == 0) {
					jixiaokaohegongzi = 0f;
				}
				Float gongzi = wageStandard.getGangweigongzi();
				Float money = (gongzi / 21.75f) * gjDay;
				DecimalFormat df = new DecimalFormat("#.##");
				money = Float.valueOf(df.format(money));
				RewardPunish rewardpunish = new RewardPunish();
				rewardpunish.setCode(users.getCode());// 工号
				rewardpunish.setUserId(users.getId());// 员工Id
				rewardpunish.setName(users.getName());// 姓名
				rewardpunish.setDate(new Date());
				rewardpunish.setDept(users.getDept());
				rewardpunish.setType("销假");
				rewardpunish.setMoney(money.doubleValue());
				String str = users.getName() + "请事假始于"
						+ qxAskForLeave.getStartDate() + "止于"
						+ qxAskForLeave.getEndDate() + " 共计" + qxTime[0] + "天"
						+ qxTime[1] + "小时，补工资" + money + "元。";
				rewardpunish.setExplain(str);
				totalDao.save(rewardpunish);
			}
			ask.setLeaveStartDate(qxAskForLeave.getStartDate());
			ask.setLeaveEndDate(qxAskForLeave.getEndDate());
			totalDao.update(ask);
			qxAskForLeave.setEpStatus("同意");
			totalDao.update(qxAskForLeave);
		}
	}

	/**
	 * 物料类别修改申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void yuanclAndWaigj(CircuitRun circuitRun, TotalDao totalDao) {
		YuanclAndWaigj y = (YuanclAndWaigj) totalDao.get(YuanclAndWaigj.class,
				circuitRun.getEntityId());
		if (y.getNewwgType() != null && y.getNewwgType().length() > 0) {
			// 同步更新BOM状态
			String bomsql = " from ProcardTemplate where markId=?   and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')";
			String procardsql = " from Procard where markId=?   and (sbStatus is null or sbStatus!='删除') ";
			// String banbensql = "";
			// if (y.getBanbenhao() != null
			// && y.getBanbenhao().length() > 0) {
			// bomsql += " and banBenNumber = '"
			// + y.getBanbenhao() + "'";
			// procardsql += "  and banBenNumber = '"
			// + y.getBanbenhao() + "'";
			// banbensql = " and banben = '"
			// + y.getBanbenhao() + "'";
			// } else {
			// bomsql +=
			// " and (banBenNumber is null or banBenNumber='')";
			// procardsql +=
			// " and (banBenNumber is null or banBenNumber='')";
			// banbensql = " and banben = '"
			// + y.getBanbenhao() + "'";
			// }
			List<ProcardTemplate> bomList = totalDao.query(bomsql, y
					.getMarkId());
			if (bomList != null && bomList.size() > 0) {
				for (ProcardTemplate bom : bomList) {
					bom.setWgType(y.getNewwgType());
					totalDao.update(bom);
				}
			}
			List<Procard> procardList = totalDao.query(procardsql, y
					.getMarkId());
			if (procardList != null && procardList.size() > 0) {
				for (Procard procard : procardList) {
					procard.setWgType(y.getNewwgType());
					totalDao.update(procard);
				}
			}

			// 修改物料需求上的数据
			String hql_mop = "from ManualOrderPlan where markId = ?  and (outcgNumber is null or outcgNumber = 0)";
			// + banbensql;
			List<ManualOrderPlan> mopList = totalDao.query(hql_mop, y
					.getMarkId());
			if (mopList != null && mopList.size() > 0) {
				for (ManualOrderPlan mop : mopList) {
					mop.setWgType(y.getNewwgType());
					totalDao.update(mop);
				}
			}

			// 修改物料需求明细上的数据
			String hql_mod = "from ManualOrderPlanDetail where markId = ? and (outcgNumber is null or outcgNumber = 0) ";
			// + banbensql;
			List<ManualOrderPlanDetail> modList = totalDao.query(hql_mod, y
					.getMarkId());
			if (modList != null && modList.size() > 0) {
				for (ManualOrderPlanDetail mod : modList) {
					mod.setWgType(y.getNewwgType());
					totalDao.update(mod);
				}
			}

			y.setWgType(y.getNewwgType());
			List<YuanclAndWaigj> yclList = totalDao
					.query(
							"from YuanclAndWaigj where markId=? and (banbenStatus is null or banbenStatus!='历史') and id !=?",
							y.getMarkId(), y.getId());
			if (yclList != null && yclList.size() > 0) {
				for (YuanclAndWaigj y2 : yclList) {
					y2.setWgType(y.getNewwgType());
					y2.setNewwgType(null);
					totalDao.update(y2);
				}
			}
			y.setNewwgType(null);
			totalDao.update(y);
		}
	}

	/**
	 * 外购件禁用/使用申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void yuanclAndWaigjAppli(CircuitRun circuitRun,
			TotalDao totalDao) {
		YuanclAndWaigjAppli y = (YuanclAndWaigjAppli) totalDao.getObjectById(
				YuanclAndWaigjAppli.class, circuitRun.getEntityId());
		if (y != null) {
			YuanclAndWaigj yuan = (YuanclAndWaigj) totalDao.getObjectById(
					YuanclAndWaigj.class, y.getYuanId());
			String style = "";
			if (yuan != null) {
				if ("禁用".equals(y.getStyle())) {
					yuan.setStatus("禁用");
					style = "禁用";
					yuan.setJystatus("禁用已同意");
				} else if ("使用".equals(y.getStyle())) {
					yuan.setStatus("使用");
					style = "使用";
					yuan.setJystatus("使用已同意");
				}
			}
			totalDao.update(yuan);

			String bbsql = null;
			if (yuan.getBanbenhao() != null && yuan.getBanbenhao().length() > 0) {
				bbsql = " and banbenhao ='" + yuan.getBanbenhao() + "'";
			} else {
				bbsql = " and (banbenhao is null or banbenhao='')";
			}
			String kgLiaoHql = null;
			if (yuan.getKgliao() != null && yuan.getKgliao().length() > 0) {
				kgLiaoHql = " and kgliao='" + yuan.getKgliao() + "'";
			} else {
				kgLiaoHql = " and (kgliao is null or kgliao ='')";
			}
			String productStyle = "";
			if (yuan.getProductStyle() != null
					&& yuan.getProductStyle().length() > 0) {
				productStyle = " and productStyle='" + yuan.getProductStyle()
						+ "'";
			}

			// 同步更新BOM模板状态
			String bomsql = " from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
					+ bbsql + kgLiaoHql + productStyle;
			List<ProcardTemplate> bomList = totalDao.query(bomsql, y
					.getMarkId());
			if (bomList != null && bomList.size() > 0) {
				for (ProcardTemplate bom : bomList) {
					if ("使用".equals(style)) {
						bom.setBzStatus("已批准");
					} else {
						bom.setBzStatus(yuan.getStatus());
					}
					totalDao.update(bom);
				}
			}

			List<YuanclAndWaigj> yclList = totalDao
					.query(
							"from YuanclAndWaigj where markId=? and (banbenStatus is null or banbenStatus!='历史') and id !=?"
									+ bbsql + kgLiaoHql + productStyle, y
									.getMarkId(), yuan.getId());
			if (yclList != null && yclList.size() > 0) {
				for (YuanclAndWaigj y2 : yclList) {
					y2.setStatus(style);
					y2.setJystatus(style + "已同意");
					totalDao.update(y2);
				}
			}
		}
	}

	/**
	 * 现场不良品处理申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void breakSubmit(CircuitRun circuitRun, TotalDao totalDao) {
		BreakSubmit breaksubmit = (BreakSubmit) totalDao.get(BreakSubmit.class,
				circuitRun.getEntityId());
		Procard procard = (Procard) totalDao.get(Procard.class, breaksubmit
				.getProcardId());
		if (circuitRun.getZzopinion() != null
				&& circuitRun.getZzopinion().length() > 0) {
			breaksubmit.setClResult(circuitRun.getZzopinion());
			totalDao.update(breaksubmit);
			if ("报废".equals(breaksubmit.getClResult())) {
				String markId = breaksubmit.getMarkId();
				if ("外购件不合格".equals(breaksubmit.getType())) {
					markId = breaksubmit.getWgmarkId();
					procard = (Procard) totalDao.getObjectByCondition(
							" from Procard where markId = ? and fatherId=? ",
							markId, procard.getId());
				}
				String hql_goods = " from Goods where goodsClass = '不合格品库' and  goodsMarkId = ? and goodsLotId = ?  and  goodsCurQuantity >0";
				if (procard != null) {
					if (procard.getBanBenNumber() != null
							&& procard.getBanBenNumber().length() > 0) {
						hql_goods += " and banBenNumber =  '"
								+ procard.getBanBenNumber() + "'";
					}
					if (procard.getKgliao() != null
							&& procard.getKgliao().length() > 0) {
						hql_goods += " and kgliao =  '" + procard.getKgliao()
								+ "'";
					}
				}
				// 从不合格品出库
				Goods goods = (Goods) totalDao.getObjectByCondition(hql_goods,
						markId, procard.getSelfCard());
				if (goods != null) {
					Sell sell = new Sell();
					sell.setSellWarehouse("不合格品库");// 库别
					sell.setSellMarkId(goods.getGoodsMarkId());// 件号
					sell.setBanBenNumber(goods.getBanBenNumber());// 版本号
					sell.setKgliao(goods.getKgliao());// 供料属性
					sell.setSellLot(goods.getGoodsLotId());// 批次
					sell.setWgType(goods.getWgType());// 物料类别
					sell.setSellGoods(goods.getGoodsFullName());// 品名
					sell.setSellFormat(goods.getGoodsFormat());// 规格
					sell.setSellCount(breaksubmit.getQrbreakcount());// 出库数量
					sell.setSellUnit(goods.getGoodsUnit());// 单位
					sell.setSellDate(Util.getDateTime());// 日期
					sell.setProcessNo(goods.getProcessNo());// 工序号
					sell.setStyle("报废出库");
					sell.setTuhao(goods.getTuhao());// 图号
					sell.setPrintStatus("YES");
					totalDao.save(sell);
					goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()
							- breaksubmit.getQrbreakcount());
					totalDao.update(goods);
					// 入到废品库
					GoodsStore goodsStore = new GoodsStore();
					goodsStore.setGoodsStoreMarkId(breaksubmit.getMarkId());// 件号
					goodsStore.setGoodsStoreWarehouse("废品库");// 库别
					goodsStore.setBanBenNumber(procard.getBanBenNumber());// 版本号
					goodsStore.setKgliao(procard.getKgliao());// 供料属性
					goodsStore.setGoodsStoreLot(procard.getSelfCard());// 批次
					goodsStore.setGoodsStoreGoodsName(procard.getProName());// 名称
					goodsStore.setGoodsStoreFormat(procard.getSpecification());// 规格
					goodsStore.setWgType(procard.getWgType());// 物料类别
					goodsStore.setGoodsStoreUnit(procard.getUnit());// 单位
					goodsStore
							.setGoodsStoreCount(breaksubmit.getQrbreakcount());// 入库数量
					goodsStore.setGoodsStoreTime(Util.getDateTime());// 入库时间
					goodsStore.setGoodsStoreProMarkId(procard.getRootMarkId());// 总成件号
					goodsStore.setTuhao(procard.getTuhao());// 图号
					goodsStore.setInputSource("报废入库");//
					goodsStore.setStatus("入库");
					goodsStore.setStyle("不合格品入库");// 入库类型
					goodsStore.setPrintStatus("YES");// 打印状态
					Integer processNo = breaksubmit.getProcessNo();
					if ("上工序不合格".equals(breaksubmit.getBreakgroup())) {
						processNo = (Integer) totalDao
								.getObjectByCondition(
										" select max(processNo) from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除') and (dataStatus is null or dataStatus!='删除') and processNO <?   ",
										breaksubmit.getProcardId(), processNo);
					}
					goodsStore.setProcessNo(processNo);// 工序号
					goodsStoreServer.saveSgrk(goodsStore);
				}
				/****************************************** 添加补料单 *************************************************************/
				LogoStickers logoStickers = new LogoStickers();
				// 生成编号
				String date = Util.getDateTime("yyyyMM");
				String number = "";
				String hql = "select max(number) from LogoStickers where stickStyle='补料单' and number like 'QD-RP-"
						+ date + "%'";
				Object object = (Object) totalDao.getObjectByCondition(hql);
				if (object != null) {
					String maxNumber = object.toString();
					Long selfCard1 = Long.parseLong(maxNumber.substring(6,
							maxNumber.length())) + 1;// 当前最大流水卡片
					number = "QD-RP-" + selfCard1.toString();
				} else {
					number = "QD-RP-" + date + "001";
				}
				logoStickers.setNumber(number);// 编号
				logoStickers.setStickStyle("补料单");

				logoStickers.setMarkId(procard.getMarkId());// 件号
				logoStickers.setLotId(procard.getSelfCard());// 批次号
				logoStickers.setProcessNO(breaksubmit.getProcessNo() + "");
				logoStickers.setProcessName(breaksubmit.getProcessName());
				Users loginUser = Util.getLoginUser();// 获得登录用户
				logoStickers.setOperator(loginUser.getName());
				logoStickers.setCode(loginUser.getCode());
				logoStickers.setCount(breaksubmit.getQrbreakcount());// 报废数量
				logoStickers.setPartsName(procard.getProName());// 名称
				logoStickers.setBillDate(Util.getDateTime());
				logoStickers.setOldProcardId(procard.getId());// 老流水单id
				logoStickers.setWorkingGroup(loginUser.getPassword()
						.getDeptNumber());// 部门编码
				logoStickers.setIsPrint("NO");
				logoStickers.setStatus("报废");
				// 材料信息
				if (procard.getProcardStyle().equals("外购件")) {
					logoStickers.setClMarkId(procard.getMarkId());
				} else {
					logoStickers.setClMarkId(procard.getTrademark());
				}
				logoStickers.setClSelfCard(procard.getLingliaoDetail());
				logoStickers.setWgType(procard.getWgType());
				totalDao.save(logoStickers);
			} else if ("让步".equals(breaksubmit.getClResult())) {
				ProcessInfor process = null;
				if ("上工序不合格".equals(breaksubmit.getBreakgroup())) {
					process = (ProcessInfor) totalDao
							.getObjectByCondition(
									"  from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除') and (dataStatus is null or dataStatus!='删除') and processNO <? "
											+ " order by  processNO desc ",
									breaksubmit.getProcardId(), breaksubmit
											.getProcessNo());
				} else if ("本工序不合格".equals(breaksubmit.getBreakgroup())) {
					process = (ProcessInfor) totalDao.get(ProcessInfor.class,
							breaksubmit.getProcessId());
				}
				if ("零件损坏".equals(breaksubmit.getType())) {
					process.setBreakCount(process.getBreakCount()
							- breaksubmit.getQrbreakcount());
					process.setSubmmitCount(process.getSubmmitCount()
							+ breaksubmit.getQrbreakcount());
					float count = procard.getFilnalCount();
					if (process.getSubmmitCount() == process.getTotalCount()
							&& process.getTotalCount() == count) {
						process.setStatus("完成");
					}
					totalDao.update(process);
					Integer maxProcessNo = (Integer) totalDao
							.getObjectByCondition(
									"select processNO from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除') order by processNO desc  ",
									breaksubmit.getProcardId());
					if (maxProcessNo == process.getProcessNO()) {
						procard.setTjNumber(procard.getTjNumber()
								+ breaksubmit.getQrbreakcount());
						if ("完成".equals(process.getStatus())) {
							procard.setStatus("完成");
						}
					} else {
						// 处理后工序的可领数量
						List<ProcessInfor> processList = totalDao
								.query(
										" from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除') and  processNO>?",
										procard.getId(), process.getProcessNO());
						for (ProcessInfor process0 : processList) {
							process0.setTotalCount(process0.getTotalCount()
									+ breaksubmit.getQrbreakcount());
							totalDao.update(process0);
						}
					}
					totalDao.update(procard);
				} else if ("外购件不合格".equals(breaksubmit.getType())) {

				}

			} else if ("报废".equals(breaksubmit.getClResult())) {

			}
		}
	}

	/**
	 * 外委调整工序申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void processInforWWApplyDetail(CircuitRun circuitRun,
			TotalDao totalDao) {
		ProcessInforWWApplyDetail pwad = (ProcessInforWWApplyDetail) totalDao
				.getObjectById(ProcessInforWWApplyDetail.class, circuitRun
						.getEntityId());
		if (pwad != null) {
			String newprocessNOs = pwad.getNewprocessNOs();
			String newprocessNames = pwad.getNewprocessNames();
			String[] newprocessNOsArray = newprocessNOs.split(";");
			List<ProcessInfor> processInforList = totalDao
					.query(
							" from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除')  order by processNO ",
							pwad.getProcardId());
			if (processInforList != null && processInforList.size() > 0) {
				for (ProcessInfor processInfor : processInforList) {
					processInfor.setSelectWwCount(0f);
					processInfor.setApplyWwCount(0f);
					processInfor.setAgreeWwCount(0f);
					totalDao.update(processInfor);
				}
			}
			for (int i = 0; i < newprocessNOsArray.length; i++) {
				ProcessInfor process = (ProcessInfor) totalDao
						.getObjectByCondition(
								" from ProcessInfor where procard.id =?  and (dataStatus is null or dataStatus!='删除') and processNO =? ",
								pwad.getProcardId(), Integer
										.parseInt(newprocessNOsArray[i]));
				process.setAgreeWwCount(pwad.getApplyCount());
				totalDao.update(process);
			}
			pwad.setProcessNOs(newprocessNOs);
			pwad.setProcessNames(newprocessNames);
			totalDao.update(pwad);
		}
	}

	/**
	 * 生产退料申请
	 * 
	 * @param circuitRun
	 * @param totalDao
	 */
	private static void sCTuiliaoSqDan(CircuitRun circuitRun, TotalDao totalDao) {
		SCTuiliaoSqDan sqd = (SCTuiliaoSqDan) totalDao.get(
				SCTuiliaoSqDan.class, circuitRun.getEntityId());
		// 同意后，把退料数量放入不合格品库,并创建不良品处理单；
		GoodsStore goodsStore = new GoodsStore();
		goodsStore.setGoodsStoreMarkId(sqd.getMarkId());// 件号
		goodsStore.setGoodsStoreWarehouse("不合格品库");// 库别
		goodsStore.setBanBenNumber(sqd.getBanBenNumber());// 版本
		goodsStore.setKgliao(sqd.getKgliao());// 供料属性
		goodsStore.setGoodsStoreLot(sqd.getExamineLot());// 批次
		goodsStore.setHsPrice(sqd.getHsPrice()==null?null:sqd.getHsPrice().doubleValue());// 含税单价
		goodsStore.setGoodsStoreGoodsName(sqd.getProName());// 名称
		goodsStore.setGoodsStoreFormat(sqd.getSpecification());// 规格
		goodsStore.setWgType(sqd.getWgType());// 物料类别
		goodsStore.setGoodsStoreUnit(sqd.getUnit());// 单位
		goodsStore.setGoodsStoreCount(sqd.getTlNumber());// 数量
		goodsStore.setGoodsStorePrice(sqd.getBhsPrice()==null?null:sqd.getBhsPrice().doubleValue());// 不含税单价
		// 总额
		goodsStore.setPriceId(sqd.getPriceId());// 价格Id
		goodsStore.setGoodsStoreSupplier(sqd.getGys());// 供应商名称
		goodsStore.setGysId(sqd.getZhuserId() + "");// 供应商Id
		goodsStore.setGoodsStoreDate(Util.getDateTime("yyyy-MM-dd"));
		goodsStore.setGoodsStoreTime(Util.getDateTime());
		String hql_wgdd = " from  WaigouDeliveryDetail where markId = ? and examineLot = ?";
		WaigouDeliveryDetail wgdd = (WaigouDeliveryDetail) totalDao
				.getObjectByCondition(hql_wgdd, sqd.getMarkId(), sqd
						.getExamineLot());
		String cgOrderNum = "暂无";
		String shOrderNum = "暂无";
		String gysName = "暂无";
		String gysId = "0";
		float hsprice = 0;
		float bhsprice = 0;
		double taxprice = 0;
		int priceId = 0;
		int gysuserId = 0;
		int waiorderId = 0;
		if (wgdd != null) {
			goodsStore.setWwddId(wgdd.getWaigouDelivery().getId());// 送货单Id
			goodsStore.setGoodsStoreSendId(wgdd.getWaigouDelivery()
					.getPlanNumber());// 送货单号
			goodsStore.setGoodsStoreSupplier(wgdd.getGysName());// 供应商名称
			goodsStore.setGysId(wgdd.getGysId() + "");// 供应商Id
			cgOrderNum = wgdd.getCgOrderNum();
			shOrderNum = wgdd.getWaigouDelivery().getPlanNumber();
			gysName = wgdd.getGysName();
			gysId = wgdd.getGysId() + "";
			priceId = wgdd.getPriceId();
			hsprice = wgdd.getHsPrice().floatValue();
			bhsprice = wgdd.getPrice().floatValue();
			taxprice = wgdd.getTaxprice();
			gysuserId = wgdd.getWaigouDelivery().getUserId();
			waiorderId = wgdd.getWaigouDelivery().getId();
			goodsStore.setTaxprice(taxprice);// 税率
		}
		goodsStore.setTuhao(sqd.getTuhao());// 图号
		goodsStoreServer.saveSgrk(goodsStore);
		// 判断同件号，同检验批次是否存在不良品处理单;
		DefectiveProduct dp = null;
		if (sqd.getMarkId() != null && sqd.getMarkId().length() > 0
				&& sqd.getExamineLot() != null
				&& sqd.getExamineLot().length() > 0) {
			String hql = " from DefectiveProduct where markId = ? and examineLot = ? and status = '待确认' and type = '外购在库不良' ";
			dp = (DefectiveProduct) totalDao.getObjectByCondition(hql, sqd
					.getMarkId(), sqd.getExamineLot());
		}
		if (dp != null) {
			dp.setDbNumber(sqd.getTlNumber() + dp.getDbNumber());
			totalDao.update(dp);
		} else {
			dp = new DefectiveProduct();
			dp.setCgOrderNum(cgOrderNum);// 采购订单号
			dp.setShOrderNum(shOrderNum);// 送货单号;
			dp.setMarkId(sqd.getMarkId());
			;// 件号
			dp.setKgliao(sqd.getKgliao());// 供料属性（自购、指定、客供）
			dp.setBanben(sqd.getBanBenNumber());// 版本
			dp.setTuhao(sqd.getTuhao());// 图号
			dp.setProName(sqd.getProName());// 零件名称、
			dp.setWgType(sqd.getWgType());// 物料类别
			dp.setSpecification(sqd.getSpecification());// 规格
			dp.setUnit(sqd.getUnit());// 单位
			dp.setHsPrice(hsprice);// 含税单价
			dp.setPrice(bhsprice);// 不含税单价
			dp.setTaxprice(taxprice);// 税率
			dp.setPriceId(priceId);// 价格Id
			dp.setGysName(gysName);// 供应商名称
			dp.setGysId(Integer.parseInt(gysId));// 供应商Id;
			dp.setGysUsersId(gysuserId);// 供应商UserId
			dp.setExamineLot(sqd.getExamineLot());// 检验批次
			dp.setWgddId(waiorderId);// 送货单Id
			dp.setType("外购在库不良");
			dp.setStatus("待确认");
			dp.setDbNumber(sqd.getTlNumber());// 调拨数量
			dp.setLlNumber(sqd.getLlNumber());// 来料数量
			dp.setJyNumber(sqd.getLlNumber());// 检验数量
			dp.setJyhgNumber(sqd.getLlNumber() - sqd.getTlNumber());// 检验合格数量
			dp.setJybhgNumber(sqd.getTlNumber());// 检验不合格数量
			Users user = Util.getLoginUser();
			dp.setJyuserId(user.getId());
			dp.setJyuserCode(user.getCode());
			dp.setJyuserName(user.getName());
			dp.setAddTime(Util.getDateTime());// 添加时间
			dp.setAddUser(Util.getLoginUser().getName());// 添加人
			totalDao.save(dp);
		}
	}

	/**
	 * 订单申请审核
	 * 
	 * @param entityId
	 * @return
	 */
	public static String orderChongxiao(Integer entityId) {
		// TODO Auto-generated method stub
		TotalDao totalDao = createTotol();
		OrderManager om = (OrderManager) totalDao.getObjectById(
				OrderManager.class, entityId);
		Set<ProductManager> pmSet = om.getProducts();
		if (pmSet != null && pmSet.size() > 0) {
			for (ProductManager pm : pmSet) {
				if ("计划转换".equals(pm.getStatus())) {
					AlertMessagesServerImpl.addAlertMessages("内部计划转换(无卡)",
							"您有新的内部计划需要转换:" + pm.getPieceNumber() + "("
									+ pm.getYwMarkId() + ")" + pm.getNum()
									+ pm.getDanwei(), "2");
				}
			}
			// 更新内部计划为同意
			Set<InternalOrder> innerOrders = om.getInnerOrders();
			for (InternalOrder internalOrder : innerOrders) {
				internalOrder.setStatus("同意");
				totalDao.update(internalOrder);
			}
		}

		if (om != null && om.getOrderType() != null
				&& om.getOrderType().equals("正式")) {
			if (pmSet != null && pmSet.size() > 0) {
				for (ProductManager pm : pmSet) {
					if (pm.getCxApplyCount() != null
							&& pm.getCxApplyCount() > 0) {// 有绑定冲销
						// 获取预冲销关联数据
						List<ProductZsAboutBh> pzabList = totalDao.query(
								"from ProductZsAboutBh where zsProductId=?", pm
										.getId());
						if (pzabList != null && pzabList.size() > 0) {
							for (ProductZsAboutBh pzab : pzabList) {
								// 获取对应备货订单信息
								ProductManager bhPm = (ProductManager) totalDao
										.getObjectById(ProductManager.class,
												pzab.getBhProductId());
								Float thisCxCount = pzab.getAboutCount();// 该备货订单此次用以冲销数量
								if (bhPm != null) {
									pzab.setStatus("同意");
									// 备货订单关联的内部计划明细
									List<InternalOrderDetail> idetailList = totalDao
											.query(
													"from InternalOrderDetail where id in(select ioDetailId from Product_Internal where productId=?) and (cxCount is null or num>cxCount)",
													bhPm.getId());
									if (pm.getHasTurn() == null) {
										pm.setHasTurn(0f);
									}
									if (bhPm.getCxCount() != null
											&& bhPm.getHasTurn() > bhPm
													.getCxCount()) {// 结余已转数量为本次的冲销已转数量
										if ((bhPm.getHasTurn() - bhPm
												.getCxCount()) > thisCxCount) {// 超过该备货订单此次用以冲销数量
											pm.setHasTurn(pm.getHasTurn()
													+ thisCxCount);
										} else {
											pm.setHasTurn(pm.getHasTurn()
													+ (pm.getHasTurn() - pm
															.getCxCount()));
										}
									}
									bhPm.setCxCount(bhPm.getCxCount()
											+ thisCxCount);
									bhPm.setCxApplyCount(bhPm.getCxApplyCount()
											- thisCxCount);
									pm
											.setCxCount(pm.getCxCount()
													+ thisCxCount);
									pm.setCxApplyCount(pm.getCxApplyCount()
											- thisCxCount);
									totalDao.update(bhPm);
									totalDao.update(pm);
									if (idetailList != null
											&& idetailList.size() > 0) {// 计算allocationsNum
										for (InternalOrderDetail idetail : idetailList) {
											InternalZsAboutBh izsaboutbh = new InternalZsAboutBh();
											izsaboutbh.setPzsAboutbhId(pzab
													.getId());// 订单冲销对应表Id
											izsaboutbh.setIdetailId(idetail
													.getId());// 内部计划Id
											izsaboutbh.setMarkId(pzab
													.getMarkId());// 件号
											izsaboutbh.setYwMakrId(pzab
													.getYwMarkId());// 业务件号
											if (idetail.getCxCount() == null) {
												idetail.setCxCount(0f);
											}
											Float thisdCxCount = 0f;// 此内部计划冲销数量
											if ((idetail.getNum().floatValue() - idetail
													.getCxCount().floatValue()) > thisCxCount
													.floatValue()) {// 足够冲销
												thisdCxCount = thisCxCount;
												thisCxCount = 0f;
											} else {
												thisdCxCount = idetail.getNum()
														- idetail.getCxCount();
												thisCxCount = thisCxCount
														- (idetail.getNum() - idetail
																.getCxCount());
											}
											if (pm.getCxRukuCount() == null) {
												pm.setCxRukuCount(0f);
											}
											// 冲销转库之后在填充正式订单入库数量
											// if
											// (idetail.getQuantityCompletion()
											// > idetail
											// .getCxCount()) {//
											// 结余入库数量为本次的冲销入库数量
											// if ((idetail
											// .getQuantityCompletion() -
											// idetail
											// .getCxCount()) > thisdCxCount) {
											// pm.setCxRukuCount(pm
											// .getCxRukuCount()
											// + thisdCxCount);
											// pm
											// .setAllocationsNum(pm
											// .getAllocationsNum()
											// + thisdCxCount);
											// } else {
											// pm
											// .setCxRukuCount(pm
											// .getCxRukuCount()
											// + (idetail
											// .getQuantityCompletion() -
											// idetail
											// .getCxCount()));
											// pm
											// .setAllocationsNum(pm
											// .getAllocationsNum()
											// + (idetail
											// .getQuantityCompletion() -
											// idetail
											// .getCxCount()));
											// }
											// // 更新订单入库率
											// OrderManager or = pm
											// .getOrderManager();
											// Integer orderId = or.getId();
											// String hql5 =
											// "select sum(num) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
											// List list = totalDao.query(
											// hql5, orderId);
											// if (list != null
											// && list.size() > 0
											// && list.get(0) != null) {
											// String hql6 =
											// "select sum(sellCount) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
											// String hql7 =
											// "select sum(timeNum) from ProductManager where (status is null or status!='取消') and orderManager.id= ?";
											// Float pronum = (Float) list
											// .get(0);
											// Float proallocationsNum = (Float)
											// totalDao
											// .getObjectByCondition(
											// hql6,
											// orderId);
											// Float timeNum = (Float) totalDao
											// .getObjectByCondition(
											// hql7,
											// orderId);
											// if (proallocationsNum == null) {
											// proallocationsNum = 0f;
											// }
											// if (timeNum == null) {
											// timeNum = 0f;
											// }
											// if (pronum == null
											// || pronum <= 0) {
											// or
											// .setCompletionrate(0F);
											// } else {
											// or
											// .setCompletionrate(proallocationsNum
											// / pronum
											// * 100);
											// or.setTimeRate(timeNum
											// / pronum * 100);
											// }
											// if (pronum == 0
											// || pronum == null) {
											// or
											// .setCompletionrate(0F);
											// or.setTimeRate(0F);
											// } else {
											// or
											// .setCompletionrate(proallocationsNum
											// / pronum
											// * 100);
											// or.setTimeRate(timeNum
											// / pronum * 100);
											// }
											//
											// } else {
											// or.setCompletionrate(0F);
											// }
											// if (or.getCompletionrate() > 0) {
											// totalDao.update(or);
											// }
											// }
											idetail.setCxCount(idetail
													.getCxCount()
													+ thisdCxCount);
											izsaboutbh.setCount(thisdCxCount);// 数量
											totalDao.update(idetail);
											totalDao.save(izsaboutbh);
											if (thisCxCount == 0) {
												break;
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
		return null;
	}

	/**
	 * 外委预选申请同意之后
	 * 
	 * @param entityId
	 * @return
	 */
	private static String gxww(Integer entityId, TotalDao totalDao) {
		// TODO Auto-generated method stub
		ProcessInforWWApply apply = (ProcessInforWWApply) totalDao
				.getObjectById(ProcessInforWWApply.class, entityId);
		Users login = Util.getLoginUser();
		if (apply != null) {
			apply.setProcessStatus("合同待确认");
			apply.setShenpiTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));
			Set<ProcessInforWWApplyDetail> detailSet = apply
					.getProcessInforWWApplyDetails();
			if (detailSet != null) {
				for (ProcessInforWWApplyDetail detail : detailSet) {
					if (detail.getDataStatus() != null
							&& (detail.getDataStatus().equals("删除") || detail
									.getDataStatus().equals("取消"))) {
						continue;
					}
					detail.setProcessStatus("合同待确认");
					totalDao.update(detail);
					Procard procard = (Procard) totalDao.getObjectById(
							Procard.class, detail.getProcardId());
					if (procard == null) {
						return "未找到对应的生产BOM";
					}
					if (procard != null && detail.getProcessNOs() != null
							&& detail.getProcessNOs().length() > 0) {
						String[] processNOS = detail.getProcessNOs().split(";");
						if (processNOS != null && processNOS.length > 0) {
							for (String processNOStr : processNOS) {
								try {
									Integer processNO = Integer
											.parseInt(processNOStr);
									ProcessInfor process = (ProcessInfor) totalDao
											.getObjectByCondition(
													"from ProcessInfor where processNO=? and (dataStatus is null or dataStatus!='删除') and procard.id=?",
													processNO, procard.getId());
									if (process != null) {// 将外委同意数量回传到工序上
										if (process.getAgreeWwCount() == null) {
											process.setAgreeWwCount(detail
													.getApplyCount());
										} else {
											process.setAgreeWwCount(process
													.getAgreeWwCount()
													+ detail.getApplyCount());
										}
										process.setApplyWwCount(process
												.getApplyWwCount()
												- detail.getApplyCount());
										totalDao.update(process);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					}
					// 工序外委连同下层工序外委
					if (detail.getWwType().equals("工序外委")
							&& detail.getRelatDown() != null
							&& detail.getRelatDown().equals("是")) {
						updateDownProcessww(procard, detail.getApplyCount(),
								totalDao);
					}
				}
				// // 判断有没有同总成的申请没有过的如果没有就将总成设定为待采购
				// Float totalCount = (Float) totalDao
				// .getObjectByCondition(
				// "select count(*) and markId=? and selfCard=? and id!=?",
				// apply.getMarkId(), apply.getSelfCard(), apply
				// .getId());
				// if (totalCount == null || totalCount == 0) {// 同总成没有未审批的外委列表
				// Procard totalProcard = (Procard) totalDao
				// .getObjectByCondition(
				// " from Procard where markId=? and selfCard=? ",
				// apply.getMarkId(), apply.getSelfCard());
				// if (totalProcard != null) {
				// totalProcard.setWlstatus("待采购");
				// totalDao.update(totalProcard);
				// /******************* 开始计算整个bom的采购数量 ***********************/
				// String hql_caigouN =
				// "from Procard where rootId=? and needCount>0";
				// List list_caigou = totalDao.query(hql_caigouN,
				// totalProcard.getId());
				// for (int i = 0; i < list_caigou.size(); i++) {
				// Procard procard = (Procard) list_caigou.get(i);
				// Float needNumber = procard.getNeedCount();// 原材料/外购件的需求总量
				// if ("外购".equals(procard.getProcardStyle())) {
				// // 判断外委数量
				// if (procard.getWwblCount() != null
				// && procard.getWwblCount() > 0) {
				// needNumber -= procard.getWwblCount();
				// if (needNumber <= 0) {
				// continue;
				// }
				// }
				// if (!"TK".equals(procard.getKgliao())
				// && !"TK AVL"
				// .equals(procard.getKgliao())
				// && !"TK Price".equals(procard
				// .getKgliao())
				// && !"CS".equals(procard.getKgliao())) {
				// procard.setKgliao("TK");
				// }
				// Float number = procard.getNeedCount();
				// if (procard.getProductStyle() != null
				// && procard.getProductStyle().equals(
				// "试制")) {// 试制外购件参与计算的数据全设置
				// procard.setSingleDuration(1f);
				// procard.setCapacity(1f);
				// procard.setCapacitySurplus(1f);
				// procard.setCapacityRatio(1f);
				// procard.setDeliveryDuration(1f);
				// procard.setDeliveryRatio(1f);
				// procard.setDeliveryPeriod(1);
				// procard.setDeliveryAmount(1f);
				// procard.setProSingleDuration(1f);
				// procard.setAllJiepai(1f);
				// }
				// /***************** MRP计算(库存量&&&&&占用量) *****************/
				// // 库存量
				// String hqlGoods =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId='"
				// + procard.getMarkId()
				// +
				// "' and goodsClass in ('外购件库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount = (Float) totalDao
				// .getObjectByCondition(hqlGoods);
				// if (kcCount == null) {
				// kcCount = 0f;
				// }
				// if (kcCount < 0) {
				// throw new RuntimeException(procard
				// .getMarkId()
				// + "的库存数据异常("
				// + kcCount
				// + "),请抓紧联系管理员!生成失败!");
				// }
				// /****************** 占用量=生产占用量+导入占用量
				// ******************************/
				// // 系统占用量(已计算过采购量(1、有库存 2、采购中)，未领料)
				// String zyCountSql =
				// "select sum(tjNumber-klNumber+hasCount) from  Procard where markId=? and productStyle=? "
				// +
				// "and jihuoStatua='激活' and status='已发卡' and procardStyle='外购' and cgNumber is not null";
				// Float zyCount = (Float) totalDao
				// .getObjectByCondition(zyCountSql,
				// procard.getMarkId(), procard
				// .getProductStyle());
				// if (zyCount == null) {
				// zyCount = 0f;
				// }
				// // 导入占用量(系统切换时导入占用量)
				// String hqlGoods_zy =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId='"
				// + procard.getMarkId()
				// +
				// "' and goodsClass in ('占用库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount_zy = (Float) totalDao
				// .getObjectByCondition(hqlGoods_zy);
				// if (kcCount_zy == null) {
				// kcCount_zy = 0f;
				// }
				// zyCount += kcCount_zy;
				// if (zyCount < 0) {
				// throw new RuntimeException(procard
				// .getMarkId()
				// + "的占用量数据异常("
				// + zyCount
				// + "),请抓紧联系管理员!生成失败!");
				// }
				// /****************** 结束 占用量=生产占用量+导入占用量 结束
				// ******************************/
				// /****************** 在途量=采购在途量+导入在途量
				// ******************************/
				// // 系统在途量(已生成采购计划，未到货)
				// String hql_zc =
				// "select sum(cgNumber-dhNumber) from  Procard where markId=? and productStyle=? "
				// +
				// "and jihuoStatua='激活' and status='已发卡' and procardStyle='外购' "
				// + "and cgNumber is not null and dhNumber is not null";
				// Float ztCount = (Float) totalDao
				// .getObjectByCondition(hql_zc, procard
				// .getMarkId());
				// if (ztCount == null) {
				// ztCount = 0f;
				// }
				//
				// // 导入在途量(系统切换时导入在途量)
				// String hqlGoods_zt =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId='"
				// + procard.getMarkId()
				// +
				// "' and goodsClass in ('在途库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount_zt = (Float) totalDao
				// .getObjectByCondition(hqlGoods_zt);
				// if (kcCount_zt == null) {
				// kcCount_zt = 0f;
				// }
				// ztCount += kcCount_zt;
				// if (zyCount < 0) {
				// throw new RuntimeException(procard
				// .getMarkId()
				// + "的在途量数据异常("
				// + zyCount
				// + "),请抓紧联系管理员!生成失败!");
				// }
				// /****************** 结束 在途量=采购在途量+导入在途量 结束
				// ******************************/
				//
				// // 占用量-(库存量+在途量(已生成采购，未到货))=剩余可用库存量
				// Float daizhiCount = (kcCount + ztCount)
				// - zyCount;
				// // Float caigouCount = 0F;// 临时屏蔽
				// // 激活外购件并判断数量是否足够(外购件保持
				// // finalCount=klnumber=hasCount，变动是tjNumber,minNumber
				// // )
				// procard.setJihuoStatua("激活");
				// procard.setStatus("已发卡");
				// procard.setKlNumber(number);
				// procard.setHascount(number);
				// procard.setTjNumber(0F);
				// procard.setMinNumber(0F);
				// // 剩余可用库存量多余
				// if (daizhiCount > 0) {
				// /****
				// * 有库存
				// */
				// if (procard.getHascount() == null) {
				// procard.setHascount(number);
				// }
				// // 待采购量(需求数量-呆滞数量)
				// Float dcgNumber = needNumber - daizhiCount;
				// // 库存量小于待采购量(部分库存)
				// if (dcgNumber > 0) {
				// procard.setCgNumber(dcgNumber);
				// procard.setTjNumber(daizhiCount);
				// procard.setWlstatus("待采购");
				// if (ztCount > zyCount) {
				// procard.setGongwei("在途");
				// } else {
				// procard.setGongwei("外购件库");
				// }
				// } else {
				// // 库存量大于待采购量，不再进行采购(全部库存)
				// procard.setCgNumber(0F);
				// procard.setTjNumber(needNumber);
				// procard.setWlstatus("入库");
				// if (ztCount > zyCount) {
				// procard.setGongwei("在途");
				// } else {
				// procard.setGongwei("外购件库");
				// }
				// }
				// // 换算最小激活数量
				// Float minNumber = procard.getTjNumber()
				// / procard.getQuanzi2()
				// * procard.getQuanzi1();
				// if (procard.getTjNumber() == number) {
				// minNumber = (float) Math
				// .ceil(minNumber);
				// }
				// procard.setMinNumber(minNumber);
				//
				// } else {
				// // 无库存，完全采购
				// procard.setCgNumber(needNumber);
				// procard.setWlstatus("待采购");
				// procard.setGongwei("待采购");
				// }
				// } else {
				// // 计算原材料采购数量
				// if (procard.getTrademark() != null
				// && procard.getTrademark().length() > 0
				// && procard.getSpecification() != null
				// && procard.getSpecification().length() > 0) {
				// // 按照单件消耗的模式
				// if ("KG".equals(procard.getYuanUnit())
				// || "G"
				// .equals(procard
				// .getYuanUnit())
				// || "kg".equals(procard
				// .getYuanUnit())
				// || "g"
				// .equals(procard
				// .getYuanUnit())
				// || "千克".equals(procard
				// .getYuanUnit())
				// || "克"
				// .equals(procard
				// .getYuanUnit())
				// || "公斤".equals(procard
				// .getYuanUnit())
				// || "斤"
				// .equals(procard
				// .getYuanUnit())) {
				//
				// /***************** MRP计算(库存量&&&&&占用量) *****************/
				// // 库存量
				// String hqlGoods =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
				// +
				// " and goodsFormat=? and goodsClass in ('原材料库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount = (Float) totalDao
				// .getObjectByCondition(
				// hqlGoods,
				// procard.getTrademark(),
				// procard
				// .getSpecification());
				// if (kcCount == null) {
				// kcCount = 0f;
				// }
				//
				// // 系统占用量
				// // String zyCountSql =
				// //
				// "select sum(tjNumber-klNumber+hasCount) from  Procard where markId=? and productStyle=? "
				// // +
				// //
				// "and jihuoStatua='激活' and status<>'完成' and procardStyle='外购' and klNumber is not null and hasCount is not null";
				// // Float zyCount = (Float)
				// // totalDao.getObjectByCondition(
				// // zyCountSql, procard.getMarkId(),
				// // procard
				// // .getProductStyle());
				// // if (zyCount == null) {
				// // zyCount = 0f;
				// // }
				// // 导入占用量(系统切换时导入占用量)
				// String hqlGoods_zy =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId=?  and goodsFormat=? and goodsClass in ('占用库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount_zy = (Float) totalDao
				// .getObjectByCondition(
				// hqlGoods_zy,
				// procard.getTrademark(),
				// procard
				// .getSpecification());
				// if (kcCount_zy == null) {
				// kcCount_zy = 0f;
				// }
				// // zyCount += kcCount_zy;
				// Float zyCount = kcCount_zy;
				// if (zyCount < 0) {
				// throw new RuntimeException(procard
				// .getMarkId()
				// + "的占用量数据异常,请抓紧联系管理员!生成失败!");
				// }
				// // 系统在途量
				// String hql_zc =
				// "select sum(number) from WaigouPlan where markId=? and goodsFormat=? and status!='入库'";
				// Float ztCount = (Float) totalDao
				// .getObjectByCondition(
				// hql_zc,
				// procard.getTrademark(),
				// procard
				// .getSpecification());
				// if (ztCount == null) {
				// ztCount = 0f;
				// }
				//
				// // 导入在途量(系统切换时导入在途量)
				// String hqlGoods_zt =
				// "select sum(goodsCurQuantity) from Goods where goodsMarkId=?   and goodsFormat=? goodsClass in ('在途库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
				// Float kcCount_zt = (Float) totalDao
				// .getObjectByCondition(
				// hqlGoods_zt,
				// procard.getTrademark(),
				// procard
				// .getSpecification());
				// if (kcCount_zt == null) {
				// kcCount_zt = 0f;
				// }
				// ztCount += kcCount_zt;
				// /****************** 占用量=生产占用量+导入占用量
				// ******************************/
				//
				// // 库存量-(占用量(所有已生成，未领料)-在途量(已生成采购，未到货))=剩余可用库存量
				// Float caigouCount = (kcCount + ztCount)
				// - zyCount;
				// // Float caigouCount = 0F;// 临时屏蔽
				// // 激活外购件并判断数量是否足够(外购件保持
				// // finalCount=klnumber=hasCount，变动是tjNumber,minNumber
				// // )
				// procard.setTjNumber(0F);
				// // 库存量多余
				// if (caigouCount > 0) {
				// /****
				// * 有库存
				// */
				// // 待采购量
				// Float dcgNumber = needNumber
				// - caigouCount;
				// // 库存量小于待采购量(部分库存)
				// if (dcgNumber > 0) {
				// procard.setCgNumber(dcgNumber);
				// procard.setWlstatus("待采购");
				// } else {
				// // 库存量大于待采购量，不再进行采购(全部库存)
				// procard.setCgNumber(0F);
				// procard.setWlstatus("入库");
				// }
				//
				// } else {
				// // 无库存，完全采购
				// procard.setCgNumber(needNumber);
				// procard.setWlstatus("待采购");
				// }
				//
				// }
				//
				// }
				// }
				// }
				// //开始激活生产任务
				// sendRunCard(totalProcard, "绑定激活生产批次");
				// }
				// }
			}

		}
		return "没有找到目标!";
	}

	private static void updateDownProcessww(Procard procard, Float applyCount,
			TotalDao totalDao) {
		// TODO Auto-generated method stub
		Set<Procard> sonset = procard.getProcardSet();
		if (sonset != null && sonset.size() > 0) {
			for (Procard son : sonset) {
				if (!son.getProcardStyle().equals("外购")
						|| (son.getNeedProcess() != null && son
								.getNeedProcess().equals("yes"))) {
					float count = 0;
					if (son.getProcardStyle().equals("外购")) {
						count = applyCount * son.getQuanzi2()
								/ son.getQuanzi1();
					} else {
						count = applyCount * son.getCorrCount();
					}
					Set<ProcessInfor> processSet = son.getProcessInforSet();
					for (ProcessInfor process : processSet) {
						if (process.getAgreeWwCount() == null) {
							process.setAgreeWwCount(count);
						} else {
							process.setAgreeWwCount(process.getAgreeWwCount()
									+ count);
						}
						process.setApplyWwCount(process.getApplyWwCount()
								- count);
						if (process.getApplyWwCount() < 0) {
							process.setApplyWwCount(0f);
						}
						totalDao.update(process);
					}
					updateDownProcessww(son, count, totalDao);
				}
			}
		}
	}

	private static String procardTemSb(Integer entityId, TotalDao totalDao) {
		// TODO Auto-generated method stub
		ProcardTemplate entity = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, entityId);
		if (entity == null) {
			return "没有找到目标!";
		}
		// 图纸升级
		String addSql2 = null;
		if (entity.getProductStyle().equals("批产")) {
			addSql2 = " and (productStyle is null or productStyle !='试制') ";
		} else {
			addSql2 = " and productStyle ='试制' ";
		}
		String tzSql = "from ProcessTemplateFile where markId=? and status='默认' "
				+ addSql2;
		if (entity.getBanci() == null || entity.getBanci().equals(0)) {
			tzSql += " and (banci is null or banci=0)";
		} else {
			tzSql += " and banci is not null and banci=" + entity.getBanci();
		}
		List<ProcessTemplateFile> processFileList = totalDao.query(tzSql,
				entity.getMarkId());
		if (processFileList != null && processFileList.size() > 0) {
			for (ProcessTemplateFile processFile : processFileList) {
				if (processFile.getBanci() == null) {
					processFile.setBanci(0);
				}
				ProcessTemplateFile newfile = new ProcessTemplateFile();
				BeanUtils.copyProperties(processFile, newfile, new String[] {
						"id", "fileName", "month" });
				newfile.setBzStatus("初始");
				newfile.setBanci(processFile.getBanci() + 1);// 新版次升级
				// 复制文件
				String fileRealPath = ServletActionContext.getServletContext()
						.getRealPath("/upload/file/processTz");
				File file = new File(fileRealPath + "/"
						+ processFile.getMonth() + "/"
						+ processFile.getFileName());
				if (file != null) {
					String month = Util.getDateTime("yyyy-MM");
					String fileWjj = fileRealPath + "/" + month;
					File cfdz = new File(fileWjj);// 存放图纸的文件夹
					if (!cfdz.exists()) {
						cfdz.mkdirs();// 如果不存在文件夹就创建
					}
					String type = processFile.getFileName().substring(
							processFile.getFileName().lastIndexOf("."),
							processFile.getFileName().length());
					String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS")
							+ type;
					newfile.setFileName(realFileName);// 文件名
					Upload upload = new Upload();
					upload.UploadFile(file, realFileName, realFileName,
							"/upload/file/processTz/" + month, null);
					newfile.setMonth(month);
					newfile.setFileName(realFileName);
					newfile.setStatus("默认");
					totalDao.save(newfile);
				}
				processFile.setStatus("历史");
				totalDao.update(processFile);
			}
		}
		String sqlSame = "from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus='默认')";
		if (entity.getBanBenNumber() == null
				|| entity.getBanBenNumber().length() == 0) {
			sqlSame = sqlSame
					+ " and (banBenNumber is null or banBenNumber='')";
		} else {
			sqlSame = sqlSame + " and banBenNumber='"
					+ entity.getBanBenNumber() + "'";
		}
		List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao.query(
				sqlSame, entity.getMarkId());
		if (ptList != null && ptList.size() > 0) {
			for (int i = 0; i < ptList.size(); i++) {
				ProcardTemplate pt = ptList.get(i);
				if (i == 0) {
					// 生成历史版本
					String historySql = "from ProcardTemplate where markId=? and banbenStatus='历史' ";
					if (pt.getBanBenNumber() == null
							|| pt.getBanBenNumber().length() == 0) {
						historySql = historySql
								+ " and (banBenNumber is null or banBenNumber='')";
					} else {
						historySql = historySql + " and banBenNumber='"
								+ pt.getBanBenNumber() + "'";
					}
					ProcardTemplate history = (ProcardTemplate) totalDao
							.getObjectByCondition(historySql, pt.getMarkId());
					if (history == null) {// 此版本还没有历史，添加历史
						history = new ProcardTemplate();
						BeanUtils.copyProperties(pt, history, new String[] {
								"id", "procardTemplate", "procardTSet",
								"processTemplate", "zhUsers" });
						history.setBanbenStatus("历史");
						// 同步工序
						Set<ProcessTemplate> processSet1 = pt
								.getProcessTemplate();
						Set<ProcessTemplate> processSet2 = new HashSet<ProcessTemplate>();
						if (processSet1 != null && processSet1.size() > 0) {// 添加在添加列表中存在的工序号的工序
							for (ProcessTemplate process1 : processSet1) {
								if (process1.getProcessNO() != null) {
									ProcessTemplate process2 = new ProcessTemplate();
									// -----------辅料开始----------
									if (process1.getIsNeedFuliao() != null
											&& process1.getIsNeedFuliao()
													.equals("yes")) {
										Set<ProcessFuLiaoTemplate> fltmpSet = process1
												.getProcessFuLiaoTemplate();
										if (fltmpSet.size() > 0) {
											Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
											for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
												ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
												BeanUtils
														.copyProperties(
																fltmp,
																pinfoFl,
																new String[] {
																		"id",
																		"processTemplate" });
												if (pinfoFl.getQuanzhi1() == null) {
													pinfoFl.setQuanzhi1(1f);
												}
												if (pinfoFl.getQuanzhi2() == null) {
													pinfoFl.setQuanzhi2(1f);
												}
												pinfoFl
														.setProcessTemplate(process2);
												pinfoFlSet.add(pinfoFl);
											}
											process2
													.setProcessFuLiaoTemplate(pinfoFlSet);
										}
									}
									// -----------辅料结束----------
									BeanUtils.copyProperties(process1,
											process2, new String[] { "id",
													"procardTemplate",
													"taSopGongwei",
													"processFuLiaoTemplate" });
									process2.setProcardTemplate(history);
									totalDao.save(process2);
									processSet2.add(process2);
								}
							}
						}
						history.setProcessTemplate(processSet2);
						totalDao.save(history);

					}
				}
				pt.setBzStatus("初始");
				pt.setSbStatus("同意");
				if (pt.getBanci() == null) {
					pt.setBanci(1);
				} else {
					pt.setBanci(pt.getBanci() + 1);
				}
				totalDao.update(pt);
			}
			return "true";
		}
		return "没有找到目标BOM!";
	}

	/**
	 * 加班审批中或同意后都生成（一条申请只能生成一次）
	 */
	public static void OverTime(CircuitRun circuitRun, TotalDao totalDao) {
		if ("Overtime".equals(circuitRun.getEntityName())) {
			Overtime overtime = (Overtime) totalDao.getObjectByCondition(
					"from Overtime where epId=?", circuitRun.getId());
			if (overtime != null) {
				// 加班生产零件产生的成本
				if (overtime.getReCost() != null && overtime.getReCost() > 0
						&& overtime.getOvertimeType() != null
						&& overtime.getOvertimeType().equals("生产")) {
					String neirong = overtime.getMarkId();
					if (neirong != null) {
						String str[] = neirong.split("/");
						String markId = str[0].substring(0, str[0].length());
						String selfCard = null;
						if (str[1].contains(",")) {
							selfCard = str[1].substring(0, str[1].length() - 4);
						} else {
							selfCard = str[1].substring(0, str[1].length());
						}
						Procard procard = (Procard) totalDao
								.getObjectByCondition(
										"from Procard where markId=? and selfCard=?",
										markId, selfCard);
						if (procard != null) {
							String proStatus = null;
							if (procard.getProductStyle().equals("批产")) {
								proStatus = "批产阶段";
							} else {
								String type = (String) totalDao
										.getObjectByCondition(
												"select type from ProjectOrderPart where markId=? and  projectOrder.id =?",
												procard.getMarkId(), procard
														.getPlanOrderId());
								if (type != null && type.equals("首件")) {
									proStatus = "首件阶段";
								} else {
									proStatus = "试制阶段";
								}
							}
							QuotedPrice qp = (QuotedPrice) totalDao
									.getObjectByCondition(
											"from QuotedPrice where markId=? and procardStyle='总成'",
											markId);
							if (qp != null) {
								QuotedPriceCost quotedPriceCost = new QuotedPriceCost();
								quotedPriceCost.setProStatus(proStatus);
								quotedPriceCost.setTzMoney(0d);
								quotedPriceCost.setUserName(overtime
										.getOvertimeName());
								quotedPriceCost.setUserCode(overtime
										.getOvertimeCode());
								quotedPriceCost.setDept(overtime
										.getOvertimeDept());
								quotedPriceCost.setApplyStatus("同意");
								quotedPriceCost.setSource("加班申报");
								quotedPriceCost.setAddTime(Util.getDateTime());
								quotedPriceCost.setOverTimeId(overtime.getId());
								quotedPriceCost.setCostType("加班费");
								//
								quotedPriceCost.setMoney((double) overtime
										.getReCost());
								if (procard.getJbfei() == null) {
									procard.setJbfei(overtime.getReCost());
								} else {
									procard.setJbfei(procard.getJbfei()
											+ overtime.getReCost());
								}
								totalDao.update(procard);
								// quotedPriceCost.setProStatus(qp.getStatus());
								quotedPriceCost.setMarkId(qp.getMarkId());
								quotedPriceCost.setSelfCard(selfCard);
								quotedPriceCost.setQpId(qp.getId());
								// 没有审批结点直接同意
								// 重新计算零件总费用
								// Float money1 = (Float)
								// totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=?",
								// quotedPriceCost.getQpId());
								// Float money2 = (Float)
								// totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where qpId=? and applyStatus='同意'",
								// quotedPriceCost.getQpId());
								// if(money1==null){
								// money1 = 0f;
								// }
								// if(money2==null){
								// money2 = 0f;
								// }
								if (qp.getRealAllfy() != null) {
									qp.setRealAllfy(qp.getRealAllfy()
											+ quotedPriceCost.getMoney());
								} else {
									qp.setRealAllfy(quotedPriceCost.getMoney());
								}
								if (qp.getYingkui() == null) {
									qp.setYingkui(0 - qp.getRealAllfy());
								} else {
									qp.setYingkui(qp.getYingkui()
											- quotedPriceCost.getMoney());
								}
								totalDao.update(qp);
								quotedPriceCost.setApplyStatus("同意");
								totalDao.save(quotedPriceCost);
							}
						}

					}

				}
				// 门禁通行权限
				Access access1 = (Access) totalDao.getObjectByCondition(
						"from Access where entityName=? and entityId=?",
						"Overtime", overtime.getId());
				if (access1 == null) {
					String access = "";
					try {
						access = AccessServerImpl.createAccessRecordCode("员工卡",
								overtime.getOvertimeName(), overtime
										.getStartDate().toString(), overtime
										.getOvertimeCardId(), overtime
										.getOvertimeCode(), Overtime.class,
								overtime.getId(), overtime.getCarPaiNum());
						if (!"".equals(access) && access != null) {
							overtime.setAccessStatus("已生成");
						}
					} catch (Exception e) {
						access = null;
						e.printStackTrace();
					}
					String access2 = "";
					try {
						access2 = AccessServerImpl.updateAccessRecordCode(
								Overtime.class, overtime.getId(), overtime
										.getEndDate().toString());
						if (!"".equals(access2) && access2 != null) {
							overtime.setAccessStatus("已生成2");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 设置换休有效期
				if ("是".equals(overtime.getHuanxiu())) {
					if ("同意".equals(circuitRun.getAllStatus())) {
						// 加班开始时间
						AnnualLeave annualLeave = (AnnualLeave) totalDao
								.getObjectByCondition(
										"from AnnualLeave where jobNum =? and status='换休'",
										overtime.getOvertimeCode());
						Integer gzTime = (Integer) totalDao
								.getObjectByCondition(
										"select gzTime from BanCi "
												+ "where id=(select banci_id from Users where code = ?)",
										overtime.getOvertimeCode());
						Float gzHour = 8f;
						if (gzTime == null || gzTime == 0) {
							gzHour = 8f;
						} else {
							gzHour = (float) gzTime / 60;// new
							// BigDecimal(gzTime).divide(new
							// BigDecimal(60)).floatValue();
						}
						if (null == annualLeave) {
							annualLeave = new AnnualLeave();
							annualLeave.setJobNum(overtime.getOvertimeCode());
							annualLeave.setName(overtime.getOvertimeName());
							annualLeave.setDept(overtime.getOvertimeDept());
							float days = (float) (overtime.getOverTimeLong() / gzHour);
							annualLeave.setSurplus(days);// 可用年休，单位（天）
							annualLeave.setStatus("换休");
							totalDao.save(annualLeave);
						} else {
							float surplus = 0.0f;
							if (null != annualLeave.getSurplus()) {
								float days = (float) (overtime
										.getOverTimeLong() / gzHour);
								surplus = annualLeave.getSurplus() + days;
							} else {
								annualLeave
										.setSurplus(annualLeave.getSurplus());// 可用年休，单位（天）
								// surplus = annualLeave.getSurplus()+days;
							}
							annualLeave.setSurplus(surplus);
							totalDao.update(annualLeave);
						}
						overtime.setUsablehxTime(overtime.getOverTimeLong());// 设置可用换休时间
					}
				}

				totalDao.update(overtime);
			}

			// 补加班申请
			overtime = (Overtime) totalDao.getObjectById(Overtime.class,
					circuitRun.getEntityId());
			if (overtime != null && circuitRun.getAllStatus().equals("同意")) {
				List<OvertimeDetail> list = totalDao.query(
						"from OvertimeDetail where overtime.id=? ", overtime
								.getId());
				Integer xiuxi = 0;
				Integer totalMinutes = 0;
				long startLong = 0;
				long endLong = 0;
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				for (OvertimeDetail overtimeDetail : list) {
					// oldStart 有值表示的是历史明细
					// startTime 有值表示的是新的明细
					String startTime = overtimeDetail.getStartTime();
					String endTime = overtimeDetail.getEndTime();
					if (startTime != null && endTime != null
							&& !startTime.equals("") && !endTime.equals("")) {
						try {
							long start = dateFormat.parse(startTime).getTime();
							long end = dateFormat.parse(endTime).getTime();
							if (startLong == 0 || start < startLong) {
								startLong = start;
							}
							if (endLong == 0 || end > endLong) {
								endLong = end;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}

						if (overtimeDetail.getXiuxi() != null
								&& overtimeDetail.getXiuxi() > 0) {
							xiuxi += overtimeDetail.getXiuxi();
						}
						if (overtimeDetail.getHour() != null
								&& overtimeDetail.getHour() > 0) {
							totalMinutes += overtimeDetail.getHour() * 60;
						}
						if (overtimeDetail.getMinutes() != null) {
							totalMinutes += overtimeDetail.getMinutes();
						}
					} else {
						// 旧的数据提供查看，不做处理
					}
				}
				overtime.setOverTimeLong(totalMinutes / 60f);
				if (overtime.getHuanxiu() != null
						&& overtime.getHuanxiu().equals("是")) {
					overtime.setUsablehxTime(overtime.getOverTimeLong());
				}
				overtime.setXiuxi(xiuxi);
				// 设置最大分钟和最小时间
				overtime.setStartDate(dateFormat.format(new Date(startLong)));
				;
				overtime.setEndDate(dateFormat.format(new Date(endLong)));

				if ("是".equals(overtime.getHuanxiu())) {
					overtime.setUsablehxTime(overtime.getOverTimeLong());
				}
				totalDao.update(overtime);
			}

		}
	}

	/**
	 * 请假审批中或同意后都生成（一条申请只能生成一次）
	 */
	public static void AskForLeave(CircuitRun circuitRun, TotalDao totalDao) {
		// 请假 --改为审批中生成进出门凭证
		if ("AskForLeave".equals(circuitRun.getEntityName())) {
			AskForLeave askForLeave = (AskForLeave) totalDao
					.getObjectByCondition("from AskForLeave where epId=?",
							circuitRun.getId());
			if (askForLeave != null) {
				/**
				 * 临时给人事管理人员发送消息
				 */
				if ("同意".equals(circuitRun.getAllStatus())
						&& !"年休".equals(askForLeave.getLeaveTypeOf())
						&& !"换休".equals(askForLeave.getLeaveTypeOf())
						&& !"公出".equals(askForLeave.getLeaveTypeOf())) {
					sendRenshiAdmin(circuitRun, askForLeave, totalDao);
				}
				Access access1 = (Access) totalDao.getObjectByCondition(
						"from Access where entityName=? and entityId=?",
						"AskForLeave", askForLeave.getLeaveId());
				if (access1 == null) {
					String access = "";
					try {
						access = AccessServerImpl.createAccessRecordCode("员工卡",
								askForLeave.getLeavePerson(), askForLeave
										.getLeaveEndDate().toString(),
								askForLeave.getLeaveUserCardId(), askForLeave
										.getLeavePersonCode(),
								AskForLeave.class, askForLeave.getLeaveId(),
								askForLeave.getCarPaiNum());
						if (!"".equals(access) && access != null) {
							askForLeave.setAccessStatus("已生成");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						access = null;
						e.printStackTrace();
					}
					String access2 = "";
					try {
						access2 = AccessServerImpl.updateAccessRecordCode(
								AskForLeave.class, askForLeave.getLeaveId(),
								askForLeave.getLeaveStartDate());
						if (!"".equals(access2) && access2 != null) {
							askForLeave.setAccessStatus("已生成2");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 判断请假类型： 换休
				if ("同意".equals(circuitRun.getAllStatus())) {
					if ("换休".equals(askForLeave.getLeaveTypeOf())) {
						askForLeave.setHuanxiustatus("已换休");
						totalDao.update(askForLeave);
						AnnualLeave annualLeave = (AnnualLeave) totalDao
								.getObjectByCondition(
										"from AnnualLeave where jobNum=? and status='换休' ",
										askForLeave.getLeavePersonCode());
						if (null != annualLeave) {
							// 请假天数
							Float hours = askForLeave.getLeaveHours();
							Integer days = askForLeave.getLeaveDays();

							// 工作时间
							Integer fenzhong = (Integer) totalDao
									.getObjectByCondition(
											"select gzTime from BanCi where id =("
													+ "select banci_id from Users where code=?)",
											askForLeave.getLeavePersonCode());
							Float gzTime = 8f;
							if (fenzhong != null && fenzhong > 0) {
								gzTime = (float) fenzhong / 60;
							}

							float hourss = hours / gzTime;
							float askDays = days + hourss;
							if (null != annualLeave.getSurplus()) {
								List<Overtime> list = totalDao.query(
										"from Overtime where overtimeCode = ?",
										askForLeave.getLeavePersonCode());
								float subHour = askDays * gzTime;// 减去的时间
								if (null != list && list.size() > 0) {
									for (Overtime overtime : list) {
										if (null != overtime) {
											Float hoursTime = overtime
													.getOverTimeLong();// 小时
											if (subHour > 0) {
												if (subHour > hoursTime) {
													overtime
															.setHuanxiustatus("已换休");
													subHour = subHour
															- hoursTime;
												} else {
													overtime
															.setHuanxiustatus("已换休");
													subHour = 0;
												}
											}
											totalDao.update(overtime);
										}
									}
								}

							} else {
								annualLeave.setSurplus(0f);
							}

							annualLeave.setSurplus(annualLeave.getSurplus()
									- askDays);
							totalDao.update(annualLeave);
						}
					}
				}

			}
		}

	}

	/**
	 * 临时给人事管理人员发送消息
	 */
	private static void sendRenshiAdmin(CircuitRun circuitRun,
			AskForLeave askForLeave, TotalDao totalDao) {
		String hql = "select valueCode from CodeTranslation where type = 'sys' and valueName = ? ";
		String valueCode = (String) totalDao.getObjectByCondition(hql, "请假管理员");
		if (valueCode != null && !valueCode.equals("")) {
			Users users = (Users) totalDao.getObjectByCondition(
					"from Users where code = ? and onWork <> '离职'", valueCode);
			if (users != null) {
				AlertMessagesServerImpl.addAlertMessages("流程审批提醒", "姓名："
						+ askForLeave.getLeavePerson() + "部门："
						+ askForLeave.getLeavePersonDept() + "工号："
						+ askForLeave.getLeavePersonCode()
						+ circuitRun.getName() + " 时长："
						+ askForLeave.getLeaveDays() + "天"
						+ askForLeave.getLeaveHours() + " 小时" + " 开始时间："
						+ askForLeave.getLeaveStartDate() + " 结束时间："
						+ askForLeave.getLeaveEndDate() + ",审批状态:"
						+ circuitRun.getAllStatus() + "。请注意查看!",
						new Integer[] { users.getId() },
						"CircuitRunAction_findAduitPage.action?id="
								+ circuitRun.getId(), true, "yes");
			}
		}
	}

	/**
	 * 常访申请后生成常访有效期
	 */
	public static void ChangFang(CircuitRun circuitRun, TotalDao totalDao) {
		if ("InEmployeeCarInfor".equals(circuitRun.getEntityName())) {
			InEmployeeCarInfor inEmployeeCarInfor = (InEmployeeCarInfor) totalDao
					.getObjectByCondition(
							"from InEmployeeCarInfor where epId=?", circuitRun
									.getId());
			if (inEmployeeCarInfor != null) {
				inEmployeeCarInfor.setEffectiveDate(Util.getNextMonth3(Util
						.getDateTime("yyyy-MM"))
						+ "-01");
				totalDao.update(inEmployeeCarInfor);
			}
		}
	}

	/**
	 * 来访申请审批后生成进门凭证
	 */
	public static void Visit(CircuitRun circuitRun, TotalDao totalDao) {
		if ("Visit".equals(circuitRun.getEntityName())) {
			Visit visit = (Visit) totalDao.getObjectByCondition(
					"from Visit where epId=?", circuitRun.getId());
			if (visit != null) {
				Access access1 = (Access) totalDao.getObjectByCondition(
						"from Access where entityName=? and entityId=?",
						"Visit", visit.getId());
				if (access1 == null) {
					Access access = null;
					try {
						access = AccessServerImpl.createAccessRecord(visit
								.getVerifycar(), visit.getVerifyManner(), visit
								.getVisitsName(), visit.getVisitstime(), visit
								.getVisitsLic(), null, visit.getVisitsTel(),
								Visit.class, visit.getId());
						if (access != null) {
							if ("车牌".equals(access.getYanzheng())) {
								visit.setVisitsCode(access.getCarPai());
							} else if ("手机".equals(access.getYanzheng())) {
								visit.setVisitsCode(access.getYanzhengnum());
							}
							visit.setVisitsAllCode(access.getYanzhengnum());
							visit.setVisit_laiStatus("进门中");// 审批已同意 进门中
							totalDao.update(visit);
							// 触发摄像头识别
							// 获取最后一条 来访进门记录 申请来访时间为今天（小于明天）的就触发一次摄像头
							Access access2 = (Access) totalDao
									.getObjectByCondition("from Access where outInDoor='进门' and yanzheng ='车牌' order by id desc");
							if (access2 != null) {
								if (Util.getDateDiff(Util.getDateTime(),
										"yyyy-MM-dd HH:mm:ss", access2
												.getVisitstime(), "yyyy-MM-dd") < 0) {// 第二数减去第一数
									if (AccessServerImpl.RriggerCmd(access2
											.getAccess_EquIp()) != null) {
										System.out.println("触发成功");
									} else {
										System.out.println("触发失败");
									}
								} else {
									System.out.println("不是今天进门，不用触发");
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						access = null;
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 离职工资单
	 * 
	 * @param circuitRun
	 */
	private static void Dimission_ZhengYi(CircuitRun circuitRun,
			TotalDao totalDao) {
		// TODO Auto-generated method stub
		if ("Dimission_ZhengYi".equals(circuitRun.getEntityName())) {
			Dimission_ZhengYi zhengYi = (Dimission_ZhengYi) totalDao
					.getObjectByCondition(
							"from Dimission_ZhengYi where epId=?", circuitRun
									.getId());
			if (zhengYi != null) {
				String hql = "from Wage where userId=? and wageClass='离职工资'";
				Wage wage1 = (Wage) totalDao.getObjectByCondition(hql, zhengYi
						.getCodeId());
				if (wage1 != null)
					return;
				Wage wage = new Wage();
				wage.setUserName(zhengYi.getName());// 姓名
				wage.setUserId(zhengYi.getCodeId());// 用户id
				wage.setCode(zhengYi.getCode());// 工号
				wage.setCardId(zhengYi.getCardId());// 卡号
				wage.setDept(zhengYi.getDept());// 部门
				wage.setMouth(zhengYi.getJz_Time().substring(0, 7) + "月");// 月份
				wage.setGangweigongzi(Float.valueOf(zhengYi.getGw_Money()));// 岗位工资
				wage.setJixiaokaohegongzi(Float.valueOf(zhengYi
						.getJiangj_Money()));// 绩效工资
				wage.setBaomijintie(Float.valueOf(zhengYi.getButie()));// 保密津贴
				wage.setJiangjin(0F);// 奖金
				wage.setDianhuabutie(0F);// 电话补贴
				wage.setGonglinggongzi(0F);// 特殊补贴
				wage.setWucanfei(Float.valueOf(zhengYi.getCanfei()));// 午餐费
				wage.setJiabanfei(0F);// 加班费
				wage.setBingshikangdeng(0F);// 病事旷
				wage.setBfgongzi(0F);// 补发补扣
				wage.setJinenggongzi(0F);// 技能工资
				wage.setOther(0F);// 其它

				wage.setLeaveNumber(zhengYi.getLog_number());// 离职申请单编号
				wage.setLeaveBuchang(Float.valueOf(zhengYi.getBc_Money()));// 离职补偿--------------重要的。

				// 扣款项
				wage.setTongchoujin(jine(zhengYi.getPension()));// 统筹金（养老保险）
				wage.setYiliaobaoxian(jine(zhengYi.getYiliao()));// 医疗保险
				wage.setShiyebaoxian(jine(zhengYi.getShiye()));// 失业保险
				wage.setGongjijin(jine(zhengYi.getFund()));// 公积金
				wage.setFangzufei(jine(zhengYi.getRent()));// 房租
				wage.setDirections(zhengYi.getRemark());// 说明
				wage.setShuidianfei(0F);// 水电费
				wage.setOutsourcing(0F);// 外包工资
				wage.setNoJishui(0F);// 不计税
				if ("是".equals(zhengYi.getBuzu_min())) {
					wage.setIsBucha("yes");// 补差
				} else {
					wage.setIsBucha("no");// 补差
				}
				wage.setBuchagongzi(0F);// 补差工资
				wage.setNoBucha(0F);// 不补差
				wage.setWageStatus("自查");
				wage.setWageClass("离职工资");
				wage.setAddDateTime(Util.getDateTime());// 添加时间
				wage.setAddUserName(zhengYi.getAddUserName());// 添加人员名称
				wage.setAddUserId(zhengYi.getAddUserId());// 添加人id
				wage.setWageStatue("正常");// 人员状态
				wage.setYingjiaoshuijin(0F);// 应交税金
				Float yingfagongzi = wage.getGangweigongzi()
						+ wage.getDianhuabutie() + wage.getBaomijintie()
						+ wage.getJixiaokaohegongzi() + wage.getWucanfei();// 应发工资计算
				wage.setYingfagongzi(yingfagongzi);// 应发工资

				wage.setShifagongzi(0f);// 实发工资
				Float yingfaWage = yingfagongzi + wage.getLeaveBuchang()
						+ wage.getTongchoujin() + wage.getYiliaobaoxian()
						+ wage.getShiyebaoxian() + wage.getGongjijin()
						+ wage.getFangzufei() + wage.getShuidianfei()
						+ wage.getYingjiaoshuijin();
				wage.setShifagongzi(yingfaWage);// 实发工资

				totalDao.save(wage);
				// 将用户设置为离职
				Users users = (Users) totalDao.getObjectById(Users.class,
						zhengYi.getCodeId());
				if (users != null) {
					users.setOnWork("离职");
					users.setCardId("");
					totalDao.update(users);
					// 离职后删除成员表的所有东西

					// 清除员工绑定的所有功能
					// String sql = "delete from ta_usersMF where  ta_userId=?";
					// totalDao.createQueryUpdate(null, sql, users.getId());
				}
				// List<AssessPersonnel> assessPersonnelList = totalDao
				// .query("from AssessPersonnel where code= ?",
				// zhengYi.getCode());
				// for (AssessPersonnel assess : assessPersonnelList) {
				// totalDao.delete(assess);
				// }
			}
		}
	}

	// 系统需求管理
	public static void SystemDemand(CircuitRun circuitRun, TotalDao totalDao) {
		if ("SystemDemand".equals(circuitRun.getEntityName())) {
			SystemDemand systemDemand = (SystemDemand) totalDao
					.getObjectByCondition("from SystemDemand where epId=?",
							circuitRun.getId());
			if (null != systemDemand) {
				systemDemand.setStatus("解决中");
				totalDao.update(systemDemand);
			}
		}
	}

	public static Float jine(String s) {
		if (s == null || "".equals(s))
			return 0f;
		Float float1 = Float.valueOf(s);
		if (float1 > 0) {
			float1 *= -1;
		}
		return float1;
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

	/**
	 * 外购订单申请 同意后流程
	 */
	public static void WaigouOrder(CircuitRun circuitRun, TotalDao totalDao) {
		if ("WaigouOrder".equals(circuitRun.getEntityName())) {
			WaigouOrder wgorder = (WaigouOrder) totalDao.getObjectByCondition(
					"from WaigouOrder where epId=?", circuitRun.getId());
			if (wgorder != null) {
				/*********************** 计算订单总额 **************************/
				Double allMoney = 0d;
				if (wgorder.getWwpSet() != null
						&& wgorder.getWwpSet().size() > 0) {
					for (WaigouPlan wa : wgorder.getWwpSet()) {
						if (wa != null && wa.getMoney() != null)
							allMoney += wa.getMoney();
					}
				}
				allMoney = (double)Util.FomartDouble(allMoney,4);
				wgorder.setAllMoney(allMoney);
				/*********************** 计算订单总额结束 **************************/
				/*********************** 添加预付款申请单表 **************************/
				// if (allMoney != null && allMoney > 0) {
				// PrepaymentsApplication prepayApp = new
				// PrepaymentsApplication();
				// String newDate = Util.getDateTime();
				// prepayApp.setAddTime(newDate);
				// prepayApp.setAddTime(Util.getDateTime("yyyy-MM-dd"));
				// Users u = (Users) totalDao
				// .getObjectByCondition(
				// "from Users where code = ? and name = ? and onWork <> '离职'",
				// wgorder.getAddUserCode(), wgorder
				// .getAddUserName());
				// if (u == null) {
				// u = Util.getLoginUser();
				// }
				// prepayApp.setAddName(u.getName());// 添加人
				// prepayApp.setAddDept(u.getDept());// 添加人部门
				// prepayApp.setYyName(wgorder.getGysName() + " "
				// + wgorder.getCaigouMonth() + "预付款单");// 项目名称
				// prepayApp.setPoNumber(wgorder.getPlanNumber());// 订单编号(PO编号)
				// prepayApp.setNumber(ppNumber());// 预付款单编号
				// boolean bl = true;
				// if (wgorder.getGysId() != null) {
				// ZhUser zhuser = (ZhUser) totalDao.getObjectById(
				// ZhUser.class, wgorder.getGysId());
				// if (zhuser.getYufuBiLi() != null
				// && zhuser.getYufuBiLi() > 0) {
				// prepayApp.setYfbl(zhuser.getYufuBiLi());
				// prepayApp.setYfMoney(Util.towWei(allMoney
				// * zhuser.getYufuBiLi() / 100));
				// // 将数字转成中文
				// prepayApp.setYfMoneyDX(NumberToCN
				// .NumberCN(prepayApp.getYfMoney()
				// .doubleValue()));
				// } else {
				// bl = false;
				// prepayApp.setYfMoney(allMoney);
				// }
				// prepayApp.setAllMoney(allMoney);// 采购总额
				// // 预计报销日期
				// if (zhuser.getFkZhouQi() != null
				// && zhuser.getFkZhouQi() > 0) {
				// prepayApp.setExpectedTime(Util
				// .getSpecifiedDayAfter(newDate, zhuser
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
				// epId1 = CircuitRunServerImpl.createProcess(
				// processName,
				// PrepaymentsApplication.class, prepayApp
				// .getId(), "status", "id", null//
				// "zhaobiaoAction!toselectyufu.action?prepayApp.id=+ p.getId()"
				// , prepayApp.getAddDept()
				// + prepayApp.getAddName()
				// + " 有预付款申请单，请您审批！", true);
				// if (epId1 != null && epId1 > 0) {
				// prepayApp.setEpId(epId1);
				// CircuitRun circuitRun1 = (CircuitRun) totalDao
				// .get(CircuitRun.class, epId1);
				// if ("同意".equals(circuitRun1.getAllStatus())
				// && "审批完成".equals(circuitRun1
				// .getAuditStatus())) {
				// prepayApp.setStatus("同意");
				// } else {
				// prepayApp.setStatus("未审批");
				// }
				// totalDao.update(prepayApp);
				// }
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// } else {
				// // 发消息
				// RtxUtil.sendNotify(u.getCode(), "您有 编号："
				// + prepayApp.getNumber()
				// + " 的预付款申请单信息待完善。(供应商：" + zhuser.getName()
				// + "的预付款比例和付款周期不完善。请及时处理！)", "系统消息", "0",
				// "0");
				// }
				// }
				// }
			}
		}
	}

	/**
	 * 考勤日报汇总补卡申请 同意后流程
	 */
	public static void Attendance(CircuitRun circuitRun, TotalDao totalDao) {
		if ("Attendance".equals(circuitRun.getEntityName())) {
			Attendance attendance = (Attendance) totalDao.getObjectByCondition(
					"from Attendance where epId=?", circuitRun.getId());
			if (attendance != null) {
				BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class,
						attendance.getBanci_Id());
				if (banCi == null) {
					return;
				}
				Users s = (Users) totalDao.getObjectById(Users.class,
						attendance.getUserId());
				if (s == null)
					return;
				List<AttendanceFu> fus = totalDao
						.query(
								"from AttendanceFu where AttendanceId = ? order by duan asc",
								attendance.getAttendanceId());
				if (fus != null && fus.size() > 0) {
					for (int i = 0; i < fus.size(); i++) {
						AttendanceFu attendanceFu = null;
						if (i == 0) {
							attendanceFu = fus.get(i);
							attendanceFu.setWorkDateTime(attendance
									.getWorkDateTime());
							totalDao.update(attendanceFu);
						} else if (i == fus.size() - 1) {
							attendanceFu = fus.get(i);
							attendanceFu.setClosingDateTime(attendance
									.getClosingDateTime());
							totalDao.update(attendanceFu);
						}
						BanCiTime ban = (BanCiTime) totalDao
								.getObjectByCondition(
										"from BanCiTime where banCi.id = ? and duan = ?",
										banCi.getId(), attendanceFu.getDuan());
						if (ban == null)
							return;
						String start = ban.getStartTime().substring(0, 5);// 开始时间
						String end = ban.getEndTime().substring(0, 5);// 结束时间
						Integer shichang = (int) (Util.getWorkTime2(start, end) / 60000);// 此段工作时长
						AttendanceTowServerImpl.huizongAttendFu(attendance
								.getUserId(), totalDao, attendance
								.getDateTime(), attendance.getWorkDateTime(),
								s, banCi, attendance, ban, start, end,
								shichang, attendanceFu);
					}
				}
				AttendanceTowServerImpl.updateAttend(attendance);
				String yuefen = attendance.getDateTime().substring(0, 7);
				int listDate = totalDao.getCount(
						"from KQDate where banci_Id = ? and kqDate like '%"
								+ yuefen + "%'", banCi.getId());
				nianXiuServer.onePeople(yuefen, listDate, s);
				// 重置参考提示
				setMoreJIeSi(totalDao, attendance);
			}
		}
	}

	/**
	 * 同意后重置参考提示
	 * 
	 * @param totalDao
	 * @param attendance
	 */
	private static void setMoreJIeSi(TotalDao totalDao, Attendance attendance) {
		List<Attendance> i1 = totalDao
				.query(
						"from Attendance where dateTime like '"
								+ attendance.getDateTime().substring(0, 7)
								+ "%' and code = ? and status in ('未审批','审批中') and attendanceId <> ? ",
						attendance.getCode(), attendance.getAttendanceId());
		if (i1 != null && i1.size() > 0) {
			int i = totalDao
					.getCount("from Attendance where dateTime like '"
							+ attendance.getDateTime().substring(0, 7)
							+ "%' and code = ? and status = '同意'", attendance
							.getCode());
			if (i > 0) {
				String ss = "当月已同意补卡申请" + i + "次";
				if (i >= 2) {
					ss = "<font color='red'>" + ss + "</font>";
				}
				for (Attendance attendance2 : i1) {
					attendance2.setMoreJiesi(ss);
					totalDao.update(attendance2);
				}
			}
		}
	}

	/**
	 * 私车转公车申请 同意后流程
	 */
	public static void Credentials(CircuitRun circuitRun, TotalDao totalDao) {
		if ("Credentials".equals(circuitRun.getEntityName())) {
			Credentials credentials = (Credentials) totalDao
					.getObjectByCondition("from Credentials where epId=?",
							circuitRun.getId());
			if (credentials != null) {
				credentials.setIsGong("yes");
				totalDao.update(credentials);
			}
		}
	}

	/**
	 * 存档申请 同意后流程
	 */
	public static void Dangan(CircuitRun circuitRun, TotalDao totalDao) {
		if ("DangAn".equals(circuitRun.getEntityName())) {
			DangAn dangan = (DangAn) totalDao.getObjectByCondition(
					"from DangAn where epId=?", circuitRun.getId());
			if (dangan != null) {
				String shixiaodate = "";
				if (!"".equals(dangan.getShenqingdate())
						&& dangan.getShenqingdate() != null) {
					shixiaodate = Util.getSpecifiedDayAfter(dangan
							.getShenqingdate(), 7);
				}
				StringBuffer buffer = new StringBuffer();
				String yz = Util.yanNumber(6);
				dangan.setYanzheng(yz);
				dangan.setShixiaodate(shixiaodate);
				dangan.setShTime(Util.getDateTime());

				boolean b = true;// 发短信和生成二维码
				if ("财务档案".equals(dangan.getQutype())
						|| "发票".equals(dangan.getQutype())) {
					b = false;
				}
				if (b) {
					buffer.append("您申请编号：" + dangan.getCdAceNum() + "的"
							+ "档案室进门验证码为：" + yz);
				}
				List<ArchiveUnarchiverAplt> apltList = totalDao.query(
						"from ArchiveUnarchiverAplt where ta_dangan = ?",
						dangan.getId());
				if (apltList != null && apltList.size() > 0) {
					for (ArchiveUnarchiverAplt aplt : apltList) {
						if (aplt.getDaGuiId() != null) {
							dangan_1(totalDao, dangan, shixiaodate, buffer, b,
									aplt);
						}
					}
				}
				totalDao.update(dangan);
				if (b) {
					new SmsServiceImpl().send(dangan.getSqTel(), buffer
							.toString());
				}
				List<String> codes1 = new ArrayList<String>();
				codes1.add(dangan.getSqCode());
				// codes1.add("472");
				if (codes1 != null && codes1.size() > 0) {
					RtxUtil.sendNoLoginNotify(codes1, buffer.toString(),
							"系统消息", "0", "0");
				}
				System.out.println(buffer.toString());
			}
		}
	}

	/**
	 * 档案存取生成验证码
	 * 
	 * @param totalDao
	 * @param dangan
	 * @param shixiaodate
	 * @param buffer
	 * @param b
	 *            财务档案&&发票为false 档案为true
	 * @param aplt
	 */
	private static void dangan_1(TotalDao totalDao, DangAn dangan,
			String shixiaodate, StringBuffer buffer, boolean b,
			ArchiveUnarchiverAplt aplt) {
		AccessWebcam accessWebcam = (AccessWebcam) totalDao.getObjectById(
				AccessWebcam.class, aplt.getDaGuiId());
		if (accessWebcam != null) {
			aplt.setDaGuiposition(accessWebcam.getCabinetOpenInstruction());
			String yz2 = Util.yanNumber(b ? 10 : 6);
			aplt.setInCodes(yz2);
			if (b) {
				String realFilePath = AlertMessagesServerImpl.pebsUrl
						+ "/upload/file/daQRcode";
				System.out.println(yz2 + "二维码生成"
						+ new TwoDimensionCode().danganQRcode(yz2));
				buffer.append("\n" + aplt.getDaName() + aplt.getCqType()
						+ " 档案柜 " + accessWebcam.getCabinetNum() + "的"
						+ aplt.getCqType()// +" 验证码为："+ yz1
						+ "二维码地址为：" + realFilePath + "/" + yz2 + ".png");
			} else {
				buffer.append("\n" + aplt.getDaName() + aplt.getCqType()
						+ dangan.getQutype() + "柜 "
						+ accessWebcam.getCabinetNum() + "的" + aplt.getCqType()
						+ "验证码为：" + yz2);
				if ("发票".equals(dangan.getQutype()) && aplt.getDaId() != null) {
					updateCwFp(totalDao, dangan, aplt);
				}
			}
			// if ("取档".equals(aplt.getCqType())) {
			// } else {
			// buffer.append("\n" + aplt.getDaName() + " 存档案柜 "
			// + accessWebcam.getCabinetNum());
			// }
			aplt.setDaGuihao(accessWebcam.getCabinetNum());
			aplt.setShixiaoTime(shixiaodate);
			aplt.setAce_Ip(accessWebcam.getCabinetAccessSim());
			totalDao.update(aplt);
		}
	}

	/**
	 * 更新发票张数
	 * 
	 * @param totalDao
	 * @param dangan
	 * @param aplt
	 */
	private static void updateCwFp(TotalDao totalDao, DangAn dangan,
			ArchiveUnarchiverAplt aplt) {
		CwCertificate certificate = (CwCertificate) totalDao.getObjectById(
				CwCertificate.class, aplt.getDaId());
		if (certificate != null) {
			if (certificate.getFujianNum() == null) {
				certificate.setFujianNum(0);
			} else {
				if (dangan.getNum() >= certificate.getFujianNum()) {
					certificate.setFujianNum(0);
				} else {
					certificate.setFujianNum(certificate.getFujianNum()
							- dangan.getNum());
				}
			}
			totalDao.update(certificate);
		}
	}

	/**
	 * 印章申请 同意后流程
	 */
	public static void SealLog(CircuitRun circuitRun, TotalDao totalDao) {
		if ("SealLog".equals(circuitRun.getEntityName())) {
			SealLog sealLog = (SealLog) totalDao.getObjectByCondition(
					"from SealLog where epId=?", circuitRun.getId());
			if (sealLog != null && sealLog.getName() != null) {
				String[] nam = sealLog.getName().split(",");
				boolean gz = false;
				boolean frz = false;
				boolean hetong = false;
				boolean cwz = false;
				boolean bxgys = false;
				if (nam.length > 0) {
					for (int i = 0; i < nam.length; i++) {
						if ("公章".equals(nam[i])) {
							gz = true;
						} else if ("法人章".equals(nam[i])) {
							frz = true;
						} else if ("合同章".equals(nam[i])) {
							hetong = true;
						} else if ("财务章".equals(nam[i])) {
							cwz = true;
						} else if ("保险柜钥匙".equals(nam[i])) {
							bxgys = true;
						}
					}
				}
				if (gz)
					seal_1(totalDao, sealLog, "公章", sealLog);
				if (frz)
					seal_1(totalDao, sealLog, "法人章", sealLog);
				if (hetong)
					seal_1(totalDao, sealLog, "合同章", sealLog);
				if (cwz)
					seal_1(totalDao, sealLog, "财务章", sealLog);
				if (bxgys)
					seal_1(totalDao, sealLog, "保险柜钥匙");
			}
		}
	}

	/**
	 * 生成取章码/钥匙
	 * 
	 * @param totalDao
	 * @param sealLog
	 * @param zhangType
	 */
	public static void seal_1(TotalDao totalDao, SealLog sealLog,
			String zhangType) {
		SealLogType logType = (SealLogType) totalDao.getObjectByCondition(
				"from SealLogType where slname = ?", zhangType);
		if (logType != null) {
			Users users = (Users) totalDao.getObjectByCondition(
					"from Users where code = ? and name = ?", sealLog
							.getUserCode(), sealLog.getUserName());
			if (users != null) {
				String nowdate = Util.getDateTime();
				DangAn dangan = null;
				String shixiaoshijian = shixiaoTime(nowdate);
				StringBuffer buffer = new StringBuffer();
				if (logType.getCunShiId() != null && logType.getCunShiId() > 0) {
					dangan = addDangan(totalDao, sealLog, zhangType, logType,
							users, nowdate, shixiaoshijian, buffer);
				}
				addArchi(totalDao, zhangType, logType, users, nowdate, dangan,
						shixiaoshijian, buffer);
				// 给管理员手机号发短信
				String ss = new SmsServiceImpl().send(logType.getBgTel(),
						buffer.toString());
				// 给管理员企业微信发消息
				RtxUtil.sendNotify(logType.getBgCode(), buffer.toString(),
						"系统消息", "0", "0");
				RtxUtil.sendNotify("472", buffer.toString() + "-_+_-" + ss,
						"系统消息", "0", "0");
				System.out.println(buffer.toString());
			}
		}
	}
	/**
	 * 生成取U盾
	 * 
	 * @param totalDao
	 * @param sealLog
	 * @param zhangType
	 */
	public static void seal_1(TotalDao totalDao, SealLog sealLog,
			String zhangType,Integer receiptLogId) {
		SealLogType logType = (SealLogType) totalDao.getObjectByCondition(
				"from SealLogType where slname = ?", zhangType);
		if (logType != null) {
			Users users = (Users) totalDao.getObjectByCondition(
					"from Users where code = ? and name = ?", sealLog
					.getUserCode(), sealLog.getUserName());
			if (users != null) {
				String nowdate = Util.getDateTime();
				DangAn dangan = null;
				String shixiaoshijian = shixiaoTime(nowdate);
				StringBuffer buffer = new StringBuffer();
				if (logType.getCunShiId() != null && logType.getCunShiId() > 0) {
					dangan = addDangan(totalDao, sealLog, zhangType, logType,
							users, nowdate, shixiaoshijian, buffer);
				}
				addArchi(totalDao, zhangType, logType, users, nowdate, dangan,
						shixiaoshijian, buffer, receiptLogId);
				// 给管理员手机号发短信
				String ss = new SmsServiceImpl().send(logType.getBgTel(),
						buffer.toString());
				// 给管理员企业微信发消息
				RtxUtil.sendNotify(logType.getBgCode(), buffer.toString(),
						"系统消息", "0", "0");
				RtxUtil.sendNotify("472", buffer.toString() + "-_+_-" + ss,
						"系统消息", "0", "0");
				System.out.println(buffer.toString());
			}
		}
	}

	/**
	 * 
	 * @param totalDao
	 * @param sealLog
	 * @param zhangType
	 * @param s
	 *            印章申请
	 */
	public static void seal_1(TotalDao totalDao, SealLog sealLog,
			String zhangType, SealLog s) {
		SealLogType logType = (SealLogType) totalDao.getObjectByCondition(
				"from SealLogType where slname = ?", zhangType);
		if (logType != null) {
			Users users = (Users) totalDao.getObjectByCondition(
					"from Users where code = ? and name = ?", sealLog
							.getUserCode(), sealLog.getUserName());
			if (users != null) {
				String nowdate = Util.getDateTime();
				DangAn dangan = null;
				String shixiaoshijian = shixiaoTime(nowdate);
				StringBuffer buffer = new StringBuffer();
				if (logType.getCunShiId() != null && logType.getCunShiId() > 0) {
					dangan = addDangan(totalDao, sealLog, zhangType, logType,
							users, nowdate, shixiaoshijian, buffer);
				}
				addArchi(totalDao, zhangType, logType, users, nowdate, dangan,
						shixiaoshijian, buffer, s);
				// 给管理员手机号发短信
				String ss = new SmsServiceImpl().send(logType.getBgTel(),
						buffer.toString());
				// 给管理员企业微信发消息
				RtxUtil.sendNotify(logType.getBgCode(), buffer.toString(),
						"系统消息", "0", "0");
				RtxUtil.sendNotify("472", buffer.toString() + "-_+_-" + ss,
						"系统消息", "0", "0");
				System.out.println(buffer.toString());
			}
		}
	}

	/**
	 * 添加进门验证码
	 * 
	 * @param totalDao
	 * @param sealLog
	 * @param zhangType
	 * @param logType
	 * @param users
	 * @param nowdate
	 * @param shixiaoshijian
	 * @param buffer
	 * @return
	 */
	private static DangAn addDangan(TotalDao totalDao, SealLog sealLog,
			String zhangType, SealLogType logType, Users users, String nowdate,
			String shixiaoshijian, StringBuffer buffer) {
		DangAn dangan;
		dangan = new DangAn();
		dangan.setSqName(users.getName());
		dangan.setSqDept(users.getDept());
		dangan.setSqCardId(logType.getBgCardId());// 保管人员卡号
		dangan.setSqCode(users.getCode());
		dangan.setSquserId(users.getId());
		dangan.setSqTel(logType.getBgTel());
		dangan.setCdAceName(logType.getCunFangWeizhi());
		dangan.setCdAceNum(logType.getCunFangNum());
		dangan.setCdAceId(logType.getCunShiId());
		dangan.setStatus("同意");
		dangan.setQutype(zhangType + "申请");
		dangan.setEpId(sealLog.getEpId());
		dangan.setUseStatus("未使用");
		String yz = Util.yanNumber(6);
		dangan.setYanzheng(yz);
		dangan.setShenqingdate(nowdate);// 此处为审核时间
		dangan.setShixiaodate(shixiaoshijian);
		totalDao.save(dangan);
		buffer.append(users.getDept() + users.getName() + "申请编号："
				+ dangan.getCdAceNum() + "的" + dangan.getCdAceName()
				+ " 印章申请  进门验证码为：" + yz);
		return dangan;
	}

	/**
	 * 添加开柜验证码
	 * 
	 * @param totalDao
	 * @param zhangType
	 * @param logType
	 * @param users
	 * @param nowdate
	 * @param dangan
	 * @param shixiaoshijian
	 * @param buffer
	 */
	private static void addArchi(TotalDao totalDao, String zhangType,
			SealLogType logType, Users users, String nowdate, DangAn dangan,
			String shixiaoshijian, StringBuffer buffer) {
		ArchiveUnarchiverAplt arc = new ArchiveUnarchiverAplt();
		arc.setCqType("取档");
		arc.setDaName(zhangType);
		String yz1 = Util.yanNumber(10);
		arc.setInCodes(yz1);
		arc.setCardId(logType.getBgCardId());
		arc.setUseType("未使用");
		arc.setDaGuiId(logType.getGuiId());
		arc.setDaGuihao(logType.getGuiHao());
		arc.setDaGuiposition(logType.getGuiHaoNum());// 开门指令
		arc.setDangan(dangan);
		arc.setAddTime(nowdate);
		arc.setShixiaoTime(shixiaoshijian);
		arc.setAce_Ip(logType.getGuiIp());
		totalDao.save(arc);
		TwoDimensionCode code = new TwoDimensionCode();
		System.out.println(yz1 + "二维码生成" + code.danganQRcode(yz1));
		buffer.append("\n" + " 档案柜 " + arc.getDaGuihao() + "的取 "
				+ zhangType// +" 验证码为："+ yz1
				+ " 二维码地址为：" + AlertMessagesServerImpl.pebsUrl
				+ "/upload/file/daQRcode" + "/" + yz1 + ".png");
	}
	/**
	 * 添加取U盾开柜验证码
	 * 
	 * @param totalDao
	 * @param zhangType
	 * @param logType
	 * @param users
	 * @param nowdate
	 * @param dangan
	 * @param shixiaoshijian
	 * @param buffer
	 */
	private static void addArchi(TotalDao totalDao, String zhangType,
			SealLogType logType, Users users, String nowdate, DangAn dangan,
			String shixiaoshijian, StringBuffer buffer,Integer receIpLogId) {
		ArchiveUnarchiverAplt arc = new ArchiveUnarchiverAplt();
		arc.setCqType("取档");
		arc.setDaName(zhangType);
		arc.setReceipId(receIpLogId);//付款明细ID
		String yz1 = Util.yanNumber(10);
		arc.setInCodes(yz1);
		arc.setCardId(logType.getBgCardId());
		arc.setUseType("未使用");
		arc.setDaGuiId(logType.getGuiId());
		arc.setDaGuihao(logType.getGuiHao());
		arc.setDaGuiposition(logType.getGuiHaoNum());// 开门指令
		arc.setDangan(dangan);
		arc.setAddTime(nowdate);
		arc.setShixiaoTime(Util.getSpecifiedDayBeforeday(shixiaoshijian, 100));
		arc.setAce_Ip(logType.getGuiIp());
		totalDao.save(arc);
		TwoDimensionCode code = new TwoDimensionCode();
		System.out.println(yz1 + "二维码生成" + code.danganQRcode(yz1));
		buffer.append("\n" + " 档案柜 " + arc.getDaGuihao() + "的取 "
				+ zhangType// +" 验证码为："+ yz1
				+ " 二维码地址为：" + AlertMessagesServerImpl.pebsUrl
				+ "/upload/file/daQRcode" + "/" + yz1 + ".png");
	}

	/**
	 * @param sealLog
	 *            印章申请ID
	 */
	private static void addArchi(TotalDao totalDao, String zhangType,
			SealLogType logType, Users users, String nowdate, DangAn dangan,
			String shixiaoshijian, StringBuffer buffer, SealLog sealLog) {
		ArchiveUnarchiverAplt arc = new ArchiveUnarchiverAplt();
		arc.setCqType("取档");
		arc.setDaName(zhangType);
		String yz1 = Util.yanNumber(10);
		arc.setInCodes(yz1);
		arc.setCardId(logType.getBgCardId());
		arc.setSealId(sealLog.getId());
		arc.setUseType("未使用");
		arc.setDaGuiId(logType.getGuiId());
		arc.setDaGuihao(logType.getGuiHao());
		arc.setDaGuiposition(logType.getGuiHaoNum());// 开门指令
		arc.setDangan(dangan);
		arc.setAddTime(nowdate);
		arc.setShixiaoTime(shixiaoshijian);
		arc.setAce_Ip(logType.getGuiIp());
		totalDao.save(arc);
		TwoDimensionCode code = new TwoDimensionCode();
		System.out.println(yz1 + "二维码生成" + code.danganQRcode(yz1));
		buffer.append("\n" + sealLog.getUserDept()+"部门 "+sealLog.getUserName()+ "的"+sealLog.getUseFor()
				+zhangType + "申请 已同意！ \n 档案柜 " + arc.getDaGuihao() + "的取 "
				+ zhangType// +" 验证码为："+ yz1
				+ " 二维码地址为：" + AlertMessagesServerImpl.pebsUrl
				+ "/upload/file/daQRcode" + "/" + yz1 + ".png");
	}

	/**
	 * 得到失效时间
	 * 
	 * @param shenqing
	 *            申请时间
	 * @return
	 */
	private static String shixiaoTime(String shenqing) {
		String shixiaoshijian = "";
		if (shenqing != null) {
			String dates = Util.getFullDateWeekTime(shenqing.substring(0, 10));
			if ("星期五".equals(dates)) {
				shixiaoshijian = Util.getSpecifiedDayBeforeday(shenqing, 4);
			} else if ("星期六".equals(dates)) {
				shixiaoshijian = Util.getSpecifiedDayBeforeday(shenqing, 3);
			} else if ("星期日".equals(dates)) {
				shixiaoshijian = Util.getSpecifiedDayBeforeday(shenqing, 2);
			} else {
				shixiaoshijian = Util.getSpecifiedDayBeforeday(shenqing, 1);
			}
		}
		return shixiaoshijian;
	}

	/**
	 * 非主营业务应收申请(现金收讫章|财务章) 同意后流程
	 */
	public static void NonCoreReceivables(CircuitRun circuitRun,
			TotalDao totalDao) {
		if ("NonCoreReceivablesDetail".equals(circuitRun.getEntityName())) {
			NonCoreReceivablesDetail recei = (NonCoreReceivablesDetail) totalDao
					.getObjectByCondition(
							"from NonCoreReceivablesDetail where epId=?",
							circuitRun.getId());
			if (recei != null) {
				SealLogType logType = (SealLogType) totalDao
						.getObjectByCondition(
								"from SealLogType where slname = ?", "财务章");
				if (logType != null) {
					Users users = (Users) totalDao.getObjectByCondition(
							"from Users where code = ?", recei.getSaveCode());
					if (users != null) {
						StringBuffer buffer = new StringBuffer();
						DangAn dangan = new DangAn();
						dangan.setSqName(users.getName());
						dangan.setSqDept(users.getDept());
						dangan.setSqCardId(logType.getBgCardId());// 保管人员卡号
						dangan.setSqCode(users.getCode());
						dangan.setSquserId(users.getId());
						dangan.setSqTel(logType.getBgTel());
						dangan.setCdAceName(logType.getCunFangWeizhi());
						dangan.setCdAceNum(logType.getCunFangNum());
						dangan.setCdAceId(logType.getCunShiId());
						dangan.setStatus("同意");
						dangan.setQutype("印章申请");
						dangan.setEpId(recei.getEpId());
						dangan.setUseStatus("未使用");
						String yz = Util.yanNumber(6);
						dangan.setYanzheng(yz);
						String nowdate = Util.getDateTime();
						dangan.setShenqingdate(nowdate);// 此处为审核时间
						if (dangan.getShenqingdate() != null) {
							dangan.setShixiaodate(Util
									.getSpecifiedDayBeforeday(dangan
											.getShenqingdate(), 7));
						} else {
							dangan.setShixiaodate("空");
						}
						totalDao.save(dangan);
						buffer
								.append(users.getDept() + users.getName()
										+ "申请编号：" + dangan.getCdAceNum() + "的"
										+ dangan.getCdAceName()
										+ " 印章申请  进门验证码为：" + yz);
						ArchiveUnarchiverAplt arc = new ArchiveUnarchiverAplt();
						arc.setCqType("取档");
						arc.setDaName("财务章");
						String yz1 = Util.yanNumber(6);
						arc.setInCodes(yz1);
						arc.setCardId(dangan.getSqCardId());
						arc.setUseType("未使用");
						arc.setDaGuiId(logType.getGuiId());
						arc.setDaGuihao(logType.getGuiHao());
						arc.setDaGuiposition(logType.getGuiHaoNum());// 开门指令
						arc.setDangan(dangan);
						arc.setAddTime(nowdate);
						arc.setShixiaoTime(dangan.getShixiaodate());
						arc.setAce_Ip(logType.getGuiIp());
						totalDao.save(arc);
						TwoDimensionCode code = new TwoDimensionCode();
						System.out.println(yz1 + "二维码生成"
								+ code.danganQRcode(yz1));
						buffer.append("\n" + " 档案柜 " + arc.getDaGuihao()
								+ "的取章验证码为：" + yz1 + " 二维码地址为："
								+ UsersAction.mainUrl + "/upload/file/daQRcode"
								+ "/" + yz1 + ".png");
						String ss = new SmsServiceImpl().send(
								dangan.getSqTel(), buffer.toString());
						RtxUtil.sendNotify(logType.getBgCode(), buffer
								.toString(), "系统消息", "0", "0");
						RtxUtil.sendNotify("472", buffer.toString() + "-_+_-",
								"系统消息", "0", "0");
						System.out.println(buffer.toString());
					}
				}

			}
		}
	}

	/***
	 * 打回的数据修改后重置流程状态，重新开始审批
	 * 
	 * @param epId
	 * @return
	 */
	public static boolean updateCircuitRun(Integer epId) {
		if (epId != null && epId > 0) {
			TotalDao totalDao = createTotol();
			CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, epId);
			if (circuitRun != null) {
				// 更新所有审批状态为"未审批",重新开始审批
				String hql = "update ExecutionNode set auditStatus='未审批',auditOpinion='',auditDateTime='' where circuitRun.id=?";
				totalDao.createQueryUpdate(hql, null, epId);

				// 更新流程审核等级
				circuitRun.setAuditLevel(1);
				circuitRun.setAllStatus("未审批");
				boolean bool = totalDao.update(circuitRun);
				// // 更新对应实体状态
				// if (circuitRun.getEntityName() != null
				// && circuitRun.getEntityName().length() > 0) {
				//
				// String hql2 = "update " + circuitRun.getEntityName()
				// + " set " + circuitRun.getEntityStatusName()
				// + "='未审批' where " + circuitRun.getEntityIdName()
				// + "="+circuitRun
				// .getEntityId();
				// try {
				// totalDao.createQueryUpdate(hql2, null);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// 重新发送消息提醒
				String more = circuitRun.getMore();
				if (more != null && more.length() > 0) {
					// 获得第一层的审批人
					String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=1";
					List list = totalDao.query(hql2, circuitRun.getId());
					Integer[] usersId = new Integer[list.size()];
					if (list == null || list.size() == 0) {
						circuitRun.setAllStatus("同意");
						circuitRun.setAuditStatus("审批完成");
						totalDao.update(circuitRun);
					}
					for (int i = 0; i < list.size(); i++) {
						Integer userid = Integer.parseInt(list.get(i)
								.toString());
						usersId[i] = userid;
					}
					// 生成提醒消息
					String messageIds = AlertMessagesServerImpl
							.addAlertMessages(more, circuitRun.getMessage()
									+ "(已修改)", usersId,
									"CircuitRunAction_findAduitPage.action?id="
											+ circuitRun.getId(), true);
					if (messageIds != null) {
						// 将消息ids保存到审批表中
						circuitRun.setMessageIds(messageIds);
					}
				}
				return totalDao.update(circuitRun);
			}
		}
		return false;
	}

	/***
	 * 当删除数据时调用,用于清空流程，并删除消息
	 * 
	 * @param epId
	 *            流程id
	 * @return
	 */
	public static boolean deleteCircuitRun(Integer epId) {
		if (epId != null && epId > 0) {
			try {
				TotalDao totalDao = createTotol();
				CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, epId);
				if (circuitRun != null) {
					// 清空消息
					String hql_del = "delete AlertMessages where id in ("
							+ circuitRun.getMessageIds() + ") ";
					totalDao.createQueryUpdate(hql_del, null);
					// 删除流程
					return totalDao.delete(circuitRun);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/****
	 * 审批操作(下级无消息提醒)
	 * 
	 * @param id
	 *            节点id
	 * @param auditStatus
	 *            审批操作(true/false)
	 * @param auditOpinion
	 *            审批意见
	 * @return
	 */
	@Override
	public String updateExecutionNode(Integer id, boolean auditStatus,
			String auditOpinion) {
		if (id == null || id <= 0) {
			return "节点id或审批状态不能为空!";
		}
		// TotalDao totalDao = CircuitRunServerImpl.createTotol();
		ExecutionNode executionNode = (ExecutionNode) totalDao.getObjectById(
				ExecutionNode.class, id);
		if (executionNode != null) {
			executionNode.setAuditStatus(auditStatus == true ? "同意" : "打回");// 审批状态
			executionNode.setAuditOpinion(auditOpinion);// 审批意见
			executionNode.setAuditDateTime(Util.getDateTime());
			// 查询对应执行流程
			CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, executionNode.getCircuitRun().getId());

			return updateAllExeNode(executionNode, circuitRun, false, null,
					false);
		}
		return "参数异常";
	}

	/****
	 * 审批操作
	 * 
	 * @param id
	 *            节点id
	 * @param auditStatus
	 *            审批操作(true/false)
	 * @param auditOpinion
	 *            审批意见
	 * @param nextMessage
	 *            下个审批人是否发送提醒消息
	 * @return
	 */
	@Override
	public String updateExecutionNode(Integer id, boolean auditStatus,
			String auditOpinion, boolean nextMessage, boolean isMessage,
			String auditOption) {
		if (id == null || id <= 0) {
			return "数据异常!";
		}
		ExecutionNode executionNode = (ExecutionNode) totalDao.getObjectById(
				ExecutionNode.class, id);
		if (executionNode != null) {
			if (!"未审批".equals(executionNode.getAuditStatus())) {
				return "您已经审批过该申请,请无重复审批!";
			}
			// 查询对应执行流程
			CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, executionNode.getCircuitRun().getId());
			if (circuitRun != null) {
				if ("同意".equals(circuitRun.getAllStatus())
						|| "打回".equals(circuitRun.getAllStatus())) {
					return "该审批流程已经" + circuitRun.getAllStatus()
							+ ",无需重复审批!谢谢!";
				}
				executionNode.setAuditStatus(auditStatus == true ? "同意" : "打回");// 审批状态
				executionNode.setAuditOpinion(auditOpinion);// 审批意见
				executionNode.setAuditOption(auditOption);// 审批意见1(意见选项)
				executionNode.setAuditDateTime(Util.getDateTime());

				return updateAllExeNode(executionNode, circuitRun, nextMessage,
						circuitRun.getMessage(), isMessage);
			}
		}
		return "参数异常";
	}

	/****
	 * 审批操作
	 * 
	 * @param id
	 *            执行流程id
	 * @param auditStatus
	 *            审批操作(true/false)
	 * @param auditOpinion
	 *            审批意见
	 * @param nextMessage
	 *            下个审批人是否发送提醒消息
	 * @return
	 */
	@Override
	public String updateExeNodeByCirId(Integer id, boolean auditStatus,
			String auditOpinion, boolean nextMessage, String more,
			boolean isMessage) {
		if (id == null || id <= 0) {
			return "流程id或审批状态不能为空!";
		}
		// 查询对应执行流程
		CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
				CircuitRun.class, id);
		Users loginUser = Util.getLoginUser();
		String hql = "select e.id from ExecutionNode e join e.circuitRun c  where  e.auditUserId=? and e.auditLevel=c.auditLevel  and e.auditStatus='未审批' and c.id=?";
		Integer nodeId = (Integer) totalDao.getObjectByQuery(hql, loginUser
				.getId(), id);
		if (nodeId != null && nodeId > 0) {
			ExecutionNode executionNode = (ExecutionNode) totalDao
					.getObjectById(ExecutionNode.class, nodeId);
			if (executionNode != null) {
				executionNode.setAuditStatus(auditStatus == true ? "同意" : "打回");// 审批状态
				executionNode.setAuditOpinion(auditOpinion);// 审批意见
				executionNode.setAuditDateTime(Util.getDateTime());
				return updateAllExeNode(executionNode, circuitRun, nextMessage,
						circuitRun.getMessage(), isMessage);
			}
		}
		return "参数异常";
	}

	/****
	 * ===================================下面是添加流程的各种接口==========================
	 */
	private static TotalDao createTotol() {
		// 获得totalDao
		TotalDao totalDao = TotalDaoImpl.findTotalDao();
		CircuitRunServerImpl ccs = new CircuitRunServerImpl();
		ccs.setTotalDao(totalDao);
		return totalDao;
	}

	/***
	 * 统一调用添加流程
	 * 
	 * @param circuitCustomize
	 *            定制流程
	 * @param classs
	 *            对象
	 * @param entityId
	 *            对象id
	 * @param statusName
	 *            状态字段
	 * @param idName
	 *            id字段
	 * @param detailUrl
	 *            对应明细地址
	 * @param more
	 *            消息内容
	 * @param isMessage
	 *            是否发送短信
	 * @param totalDao
	 * @return
	 * @throws Exception
	 */
	private static Integer addEp(CircuitCustomize circuitCustomize,
			Class classs, Integer entityId, String statusName, String idName,
			String detailUrl, String more, boolean isMessage,
			TotalDao totalDao, String hdStatus) throws Exception {
		/************ 创建执行流程 */
		CircuitRun circuitRun = new CircuitRun();
		BeanUtils.copyProperties(circuitCustomize, circuitRun, new String[] {
				"id", "auditNodeSet", "addDateTime", "setoption" });
		Users loginUser = Util.getLoginUser();
		circuitRun.setAddDateTime(Util.getDateTime());// 添加时间
		circuitRun.setEntityName(classs.getSimpleName());// 实体类类名
		circuitRun.setEntityStatusName(statusName);// 实体类状态
		circuitRun.setEntityIdName(idName);// 实体类id字段名
		circuitRun.setEntityId(entityId);// 实体类id
		circuitRun.setAllStatus("未审批");// 总状态
		circuitRun.setAuditStatus("未开始审批");// 审核进度
		circuitRun.setAuditLevel(1);// 第一级开始审批
		circuitRun.setCcId(circuitCustomize.getId());// 定制流程id
		circuitRun.setMessage(more);// 提醒消息
		circuitRun.setDetailUrl(detailUrl);// 对应明细地址
		circuitRun.setHdStatus(hdStatus);// 回调状态
		circuitRun.setAddUserId(loginUser.getId());
		circuitRun.setAddUserName(loginUser.getName());
		circuitRun.setAddUserDept(loginUser.getDept());
		if (circuitCustomize.getSetoption() != null) {
			Set<OptionRun> setoptionrun = new HashSet<OptionRun>();
			for (Option option : circuitCustomize.getSetoption()) {
				OptionRun optionRun = new OptionRun();
				optionRun.setName(option.getName());
				optionRun.setCircuitRun(circuitRun);
				setoptionrun.add(optionRun);
			}
			circuitRun.setSetoptionrun(setoptionrun);
		}

		/************ 创建审批节点 */
		String hql = "from AuditNode where circuitCustomize.id=?  order by auditLevel";
		// 判断这个定制流程是否绑定的有部门并且包含当前登陆人所在的部门

		Set<DeptNumber> deptSet = circuitCustomize.getDeptset();
		if (deptSet != null && deptSet.size() > 0) {
			DeptNumber dept = null;
			if (circuitCustomize.getDept() != null
					&& circuitCustomize.getDept().length() > 0) {
				dept = (DeptNumber) totalDao.getObjectByCondition(
						" from DeptNumber where dept = ? order by id desc",
						circuitCustomize.getDept());
			} else {
				String deptnumber = loginUser.getDept();
				String hql_dept = " from DeptNumber where id in (select d.id  from DeptNumber d join d.CircuitCustomizeSet c where c.id=?) and dept = ? ";
				dept = (DeptNumber) totalDao.getObjectByCondition(hql_dept,
						circuitCustomize.getId(), deptnumber);
			}
			if (dept != null) {
				hql = "from AuditNode where circuitCustomize.id=? and id in ( select a.id from AuditNode a join a.deptSet b where b.id ="
						+ dept.getId() + ") order by auditLevel";
			} else {
				throw new Exception(circuitCustomize.getName() + "流程还未绑定"
						+ loginUser.getDept() + "部门，请先绑定" + loginUser.getDept()
						+ "。");
			}
		}
		// 得到所有的定制流程审批节点

		List<AuditNode> auditNodeList = totalDao.query(hql, circuitCustomize
				.getId());
		String allstatus = "未审批";
		if (auditNodeList != null && auditNodeList.size() > 0) {
			Set<ExecutionNode> exNodeSet = new HashSet<ExecutionNode>();
			// 生成对应的审批流程节点
			for (AuditNode auditNode : auditNodeList) {
				ExecutionNode executionNode = new ExecutionNode();
				BeanUtils.copyProperties(auditNode, executionNode,
						new String[] { "id" });
				executionNode.setAuditStatus("未审批");
				executionNode.setCircuitRun(circuitRun);
				exNodeSet.add(executionNode);
			}
			circuitRun.setExNodeSet(exNodeSet);
		} else {
			circuitRun.setAllStatus("同意");
			circuitRun.setAuditStatus("审批完成");
			allstatus = "同意";
		}
		// 更新对应实体状态
		// if (circuitRun.getEntityName() != null
		// && circuitRun.getEntityName().length() > 0) {
		// String hql2 = "update " + circuitRun.getEntityName() + " set "
		// + circuitRun.getEntityStatusName() + "=? where "
		// + circuitRun.getEntityIdName() + "=?";
		// // update Zhaobiao set status=? where id=?
		// try {
		// totalDao.createQueryUpdate(hql2, null, allstatus, circuitRun
		// .getEntityId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// 添加审批流程
		boolean bool = totalDao.save(circuitRun);
		if (bool && more != null && more.length() > 0 && auditNodeList != null) {
			// 获得第一层的审批人
			String hql2 = "from ExecutionNode where circuitRun.id=? and auditLevel=1";
			List list = totalDao.query(hql2, circuitRun.getId());

			// 计算个人审批标准时间
			String messageIds = "";
			for (int i = 0, len = list.size(); i < len; i++) {
				Float zws = 0F;
				ExecutionNode executionNode1 = (ExecutionNode) list.get(i);
				executionNode1.setStartDateTime(circuitRun.getAddDateTime());
				Integer[] usersId = { executionNode1.getAuditUserId() };

				// 查询该流程对应的审批记录
				String prolog = "from ExecutionNode  where auditUserId=? "
						+ " and circuitRun.name=? and timeLong is not null";

				Integer auditcount = totalDao.getCount(prolog, executionNode1
						.getAuditUserId(), circuitRun.getName());

				prolog += " order by timeLong";

				List getzws = new ArrayList();
				if (auditcount % 2 == 0) {// 偶数 （1/2+1）
					getzws = totalDao.findAllByPage(prolog, (int) Math
							.ceil(auditcount / 2 / 2), 2, executionNode1
							.getAuditUserId(), circuitRun.getName());
				} else {// 奇数
					getzws = totalDao.findAllByPage(prolog, (int) Math
							.ceil(auditcount / 2), 1, executionNode1
							.getAuditUserId(), circuitRun.getName());
				}
				if (getzws.size() > 0) {
					Float avgAuitTimeLong = 0F;
					for (int k = 0; k < getzws.size(); k++) {
						ExecutionNode executionNode_zws = (ExecutionNode) getzws
								.get(k);
						avgAuitTimeLong += executionNode_zws.getTimeLong();
					}

					zws = avgAuitTimeLong / getzws.size();
				}
				// 生成提醒消息
				messageIds += ","
						+ AlertMessagesServerImpl.addAlertMessages("1", more
								+ "   温馨提示:此流程您已审批【" + auditcount
								+ "】次,您的标准审批时长为【" + zws + "】小时，辛苦了，相信您会更努力!", usersId,
								"CircuitRunAction_findAduitPage.action?id="
										+ circuitRun.getId(), isMessage);
			}
			if (messageIds != null) {
				// 将消息ids保存到审批表中
				circuitRun.setMessageIds(messageIds);
				totalDao.update(circuitRun);
			}
		}

		if (bool && "同意".equals(circuitRun.getAllStatus())) {
			// 执行
			updateAuditAgree(circuitRun, totalDao);
		}

		return circuitRun.getId();
	}

	/***
	 * 统一调用添加流程
	 * 
	 * @param circuitCustomize
	 *            定制流程
	 * @param classs
	 *            对象
	 * @param entityId
	 *            对象id
	 * @param statusName
	 *            状态字段
	 * @param idName
	 *            id字段
	 * @param detailUrl
	 *            对应明细地址
	 * @param more
	 *            消息内容
	 * @param isMessage
	 *            是否发送短信
	 * @param uIds
	 *            增加在流程节点之前的用户Id
	 * @param totalDao
	 * @return
	 */
	private static Integer addEp(CircuitCustomize circuitCustomize,
			Class classs, Integer entityId, String statusName, String idName,
			String detailUrl, String more, boolean isMessage,
			TotalDao totalDao, String hdStatus, String uIds) throws Exception {
		/************ 创建执行流程 */
		CircuitRun circuitRun = new CircuitRun();
		BeanUtils.copyProperties(circuitCustomize, circuitRun, new String[] {
				"id", "auditNodeSet", "addDateTime" });
		Users loginUser = Util.getLoginUser();
		circuitRun.setAddDateTime(Util.getDateTime());// 添加时间
		circuitRun.setEntityName(classs.getSimpleName());// 实体类类名
		circuitRun.setEntityStatusName(statusName);// 实体类状态
		circuitRun.setEntityIdName(idName);// 实体类id字段名
		circuitRun.setEntityId(entityId);// 实体类id
		circuitRun.setAllStatus("未审批");// 总状态
		circuitRun.setAuditStatus("未开始审批");// 审核进度
		circuitRun.setAuditLevel(1);// 第一级开始审批
		circuitRun.setCcId(circuitCustomize.getId());// 定制流程id
		circuitRun.setMessage(more);// 提醒消息
		circuitRun.setDetailUrl(detailUrl);// 对应明细地址
		circuitRun.setHdStatus(hdStatus);// 回调状态
		if (loginUser != null) {
			circuitRun.setAddUserId(loginUser.getId());
			circuitRun.setAddUserName(loginUser.getName());
			circuitRun.setAddUserDept(loginUser.getDept());
		}

		/************ 创建审批节点 */
		// 得到所有的定制流程审批节点
		// 生成前置审批结点
		List<Users> firstUserList = null;
		if (uIds != null && uIds.length() > 0) {
			firstUserList = totalDao
					.query(
							"from Users where id in("
									+ uIds
									+ ") and id  not in( select auditUserId from AuditNode where circuitCustomize.id=?) ",
							circuitCustomize.getId());
		}
		int level = 0;
		Set<ExecutionNode> exNodeSet = new HashSet<ExecutionNode>();

		if (circuitCustomize.getName().equals("BOM结构审批申请")
				|| "文件管控审批流程".equals(circuitCustomize.getName())) {
			// 忽视现有的审批节点，以BOM现有的审批人员为标准
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao.query("from Users where id in(" + uIds
						+ ")  ");
			}
			if (firstUserList != null && firstUserList.size() > 0) {
				level = 1;
				// 级别前后按照uIds中的排序前后
				String[] uIdList = uIds.split(",");
				for (String idStr : uIdList) {
					Integer idint = Integer.parseInt(idStr);
					for (Users firstUser : firstUserList) {
						if (firstUser.getId().equals(idint)) {
							ExecutionNode executionNode = new ExecutionNode();
							executionNode.setAuditLevel(level);// 审批等级(1/2/3/4)
							executionNode.setAuditUserName(firstUser.getName());// 审批人姓名
							executionNode.setAuditUserDept(firstUser.getDept());// 审批人部门
							executionNode.setAuditUserId(firstUser.getId());// 审批人id
							executionNode.setAuditStatus("未审批");//
							executionNode.setCircuitRun(circuitRun);
							exNodeSet.add(executionNode);
							level++;
							break;
						}
					}
				}
				circuitRun.setExNodeSet(exNodeSet);
			}
		} else {
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao
						.query(
								"from Users where id in("
										+ uIds
										+ ") and id  not in( select auditUserId from AuditNode where circuitCustomize.id=?) ",
								circuitCustomize.getId());
			}
			if (firstUserList != null && firstUserList.size() > 0) {
				level = 1;
				for (Users firstUser : firstUserList) {
					ExecutionNode executionNode = new ExecutionNode();
					executionNode.setAuditLevel(1);// 审批等级(1/2/3/4)
					executionNode.setAuditUserName(firstUser.getName());// 审批人姓名
					executionNode.setAuditUserDept(firstUser.getDept());// 审批人部门
					executionNode.setAuditUserId(firstUser.getId());// 审批人id
					executionNode.setAuditStatus("未审批");//
					executionNode.setCircuitRun(circuitRun);
					exNodeSet.add(executionNode);
				}
				circuitRun.setExNodeSet(exNodeSet);
			}
			/************ 创建审批节点 */
			// 判断这个定制流程是否绑定的有部门并且包含当前登陆人所在的部门
			String hql = "from AuditNode where circuitCustomize.id=?  order by auditLevel";
			if (loginUser != null) {
				Set<DeptNumber> deptSet = circuitCustomize.getDeptset();
				String hql_dept = " from DeptNumber where deptNumber = ?";
				String deptnumber = loginUser.getPassword().getDeptNumber();
				DeptNumber dept = (DeptNumber) totalDao.getObjectByCondition(
						hql_dept, deptnumber);
				if (deptSet != null && deptSet.size() > 0) {
					if (dept != null && deptSet.contains(dept)) {
						hql = "from AuditNode where circuitCustomize.id=? and id in ( select a.id from AuditNode a join a.deptSet b where b.id ="
								+ dept.getId() + ") order by auditLevel";
					} else {
						throw new Exception(circuitCustomize.getName()
								+ "流程还未绑定" + loginUser.getDept() + "部门，请先绑定"
								+ loginUser.getDept() + "。");
					}
				}
			}
			List<AuditNode> auditNodeList = totalDao.query(hql,
					circuitCustomize.getId());
			if (auditNodeList != null && auditNodeList.size() > 0) {
				// 生成对应的审批流程节点
				for (AuditNode auditNode : auditNodeList) {
					ExecutionNode executionNode = new ExecutionNode();
					BeanUtils.copyProperties(auditNode, executionNode,
							new String[] { "id" });
					executionNode.setAuditStatus("未审批");
					executionNode.setCircuitRun(circuitRun);
					executionNode.setAuditLevel(executionNode.getAuditLevel()
							+ level);
					exNodeSet.add(executionNode);
				}
				circuitRun.setExNodeSet(exNodeSet);
			}
		}
		if (exNodeSet == null || exNodeSet.size() == 0) {
			circuitRun.setAllStatus("同意");
			circuitRun.setAuditStatus("审批完成");
		}
		// // 更新对应实体状态
		// if (circuitRun.getEntityName() != null
		// && circuitRun.getEntityName().length() > 0) {
		// String hql2 = "update " + circuitRun.getEntityName() + " set "
		// + circuitRun.getEntityStatusName() + "=? where "
		// + circuitRun.getEntityIdName() + "=?";
		// // update Zhaobiao set status=? where id=?
		// try {
		// totalDao.createQueryUpdate(hql2, null, "未审批", circuitRun
		// .getEntityId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// 添加审批流程
		boolean bool = totalDao.save(circuitRun);
		if (bool && more != null && more.length() > 0 && exNodeSet != null
				&& exNodeSet.size() > 0) {
			// 获得第一层的审批人
			String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=1";
			List list = totalDao.query(hql2, circuitRun.getId());
			Integer[] usersId = new Integer[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Integer userid = Integer.parseInt(list.get(i).toString());
				usersId[i] = userid;
			}
			// 生成提醒消息
			String messageIds = AlertMessagesServerImpl.addAlertMessages(more,
					more, usersId, "CircuitRunAction_findAduitPage.action?id="
							+ circuitRun.getId(), isMessage);
			if (messageIds != null) {
				// 将消息ids保存到审批表中
				circuitRun.setMessageIds(messageIds);
				totalDao.update(circuitRun);
			}
		}

		return circuitRun.getId();
	}

	/***
	 * 统一调用添加流程
	 * 
	 * @param circuitCustomize
	 *            定制流程
	 * @param classs
	 *            对象
	 * @param entityId
	 *            对象id
	 * @param statusName
	 *            状态字段
	 * @param idName
	 *            id字段
	 * @param detailUrl
	 *            对应明细地址
	 * @param more
	 *            消息内容
	 * @param isMessage
	 *            是否发送短信
	 * @param uIds
	 *            增加在流程节点之前的用户Id
	 * 
	 * @param totalDao
	 * 
	 * @param leaveAssign
	 *            是否指定等级（1:同级）（2：递增）（3：uIds=1:451,1:12,2:45）
	 * @return
	 */
	private static Integer addEp(CircuitCustomize circuitCustomize,
			Class classs, Integer entityId, String statusName, String idName,
			String detailUrl, String more, boolean isMessage,
			TotalDao totalDao, String hdStatus, String uIds, String leaveAssign)
			throws Exception {
		/************ 创建执行流程 */
		CircuitRun circuitRun = new CircuitRun();
		BeanUtils.copyProperties(circuitCustomize, circuitRun, new String[] {
				"id", "auditNodeSet", "addDateTime" });
		Users loginUser = Util.getLoginUser();
		circuitRun.setAddDateTime(Util.getDateTime());// 添加时间
		circuitRun.setEntityName(classs.getSimpleName());// 实体类类名
		circuitRun.setEntityStatusName(statusName);// 实体类状态
		circuitRun.setEntityIdName(idName);// 实体类id字段名
		circuitRun.setEntityId(entityId);// 实体类id
		circuitRun.setAllStatus("未审批");// 总状态
		circuitRun.setAuditStatus("未开始审批");// 审核进度
		circuitRun.setAuditLevel(1);// 第一级开始审批
		circuitRun.setCcId(circuitCustomize.getId());// 定制流程id
		circuitRun.setMessage(more);// 提醒消息
		circuitRun.setDetailUrl(detailUrl);// 对应明细地址
		circuitRun.setHdStatus(hdStatus);// 回调状态
		circuitRun.setAddUserId(loginUser.getId());
		circuitRun.setAddUserName(loginUser.getName());
		circuitRun.setAddUserDept(loginUser.getDept());

		/************ 创建审批节点 */
		// 得到所有的定制流程审批节点
		Set<ExecutionNode> exNodeSet = new HashSet<ExecutionNode>();
		if (leaveAssign != null && leaveAssign.equals("2")) {// 递增
			// 生成前置审批结点
			List<Users> firstUserList = null;
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao
						.query(
								"from Users where id in("
										+ uIds
										+ ") and id  not in( select auditUserId from AuditNode where circuitCustomize.id=?) ",
								circuitCustomize.getId());
			}
			int level = 0;
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao.query("from Users where id in(" + uIds
						+ ")  ");
			}
			if (firstUserList != null && firstUserList.size() > 0) {
				level = 1;
				// 级别前后按照uIds中的排序前后
				String[] uIdList = uIds.split(",");
				for (String idStr : uIdList) {
					Integer idint = Integer.parseInt(idStr);
					for (Users firstUser : firstUserList) {
						if (firstUser.getId().equals(idint)) {
							ExecutionNode executionNode = new ExecutionNode();
							executionNode.setAuditLevel(level);// 审批等级(1/2/3/4)
							executionNode.setAuditUserName(firstUser.getName());// 审批人姓名
							executionNode.setAuditUserDept(firstUser.getDept());// 审批人部门
							executionNode.setAuditUserId(firstUser.getId());// 审批人id
							executionNode.setAuditStatus("未审批");//
							executionNode.setCircuitRun(circuitRun);
							exNodeSet.add(executionNode);
							level++;
							break;
						}
					}
				}
				circuitRun.setExNodeSet(exNodeSet);
			}
		} else if (leaveAssign != null && leaveAssign.equals("3")) {
			String[] userIdAndLeaves = uIds.split(",");
			for (int i = 0; i < userIdAndLeaves.length; i++) {
				String[] obj = userIdAndLeaves[i].split(":");
				int level = Integer.parseInt(obj[0]);
				int uid = Integer.parseInt(obj[1]);
				Users users = (Users) totalDao.getObjectById(Users.class, uid);
				ExecutionNode executionNode = new ExecutionNode();
				executionNode.setAuditLevel(level);// 审批等级(1/2/3/4)
				executionNode.setAuditUserName(users.getName());// 审批人姓名
				executionNode.setAuditUserDept(users.getDept());// 审批人部门
				executionNode.setAuditUserId(users.getId());// 审批人id
				executionNode.setAuditStatus("未审批");//
				executionNode.setCircuitRun(circuitRun);
				exNodeSet.add(executionNode);
			}
			circuitRun.setExNodeSet(exNodeSet);
		} else {
			// 生成前置审批结点
			List<Users> firstUserList = null;
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao
						.query(
								"from Users where id in("
										+ uIds
										+ ") and id  not in( select auditUserId from AuditNode where circuitCustomize.id=?) ",
								circuitCustomize.getId());
			}
			int level = 0;
			if (uIds != null && uIds.length() > 0) {
				firstUserList = totalDao
						.query(
								"from Users where id in("
										+ uIds
										+ ") and id  not in( select auditUserId from AuditNode where circuitCustomize.id=?) ",
								circuitCustomize.getId());
			}
			if (firstUserList != null && firstUserList.size() > 0) {
				level = 1;
				for (Users firstUser : firstUserList) {
					ExecutionNode executionNode = new ExecutionNode();
					executionNode.setAuditLevel(1);// 审批等级(1/2/3/4)
					executionNode.setAuditUserName(firstUser.getName());// 审批人姓名
					executionNode.setAuditUserDept(firstUser.getDept());// 审批人部门
					executionNode.setAuditUserId(firstUser.getId());// 审批人id
					executionNode.setAuditStatus("未审批");//
					executionNode.setCircuitRun(circuitRun);
					exNodeSet.add(executionNode);
				}
				circuitRun.setExNodeSet(exNodeSet);
			}
			/************ 创建审批节点 */
			// 判断这个定制流程是否绑定的有部门并且包含当前登陆人所在的部门
			Set<DeptNumber> deptSet = circuitCustomize.getDeptset();
			String hql_dept = " from DeptNumber where deptNumber = ?";
			String deptnumber = loginUser.getPassword().getDeptNumber();
			DeptNumber dept = (DeptNumber) totalDao.getObjectByCondition(
					hql_dept, deptnumber);
			String hql = "from AuditNode where circuitCustomize.id=?  order by auditLevel";
			if (deptSet != null && deptSet.size() > 0) {
				if (dept != null && deptSet.contains(dept)) {
					hql = "from AuditNode where circuitCustomize.id=? and id in ( select a.id from AuditNode a join a.deptSet b where b.id ="
							+ dept.getId() + ") order by auditLevel";
				} else {
					throw new Exception(circuitCustomize.getName() + "流程还未绑定"
							+ loginUser.getDept() + "部门，请先绑定"
							+ loginUser.getDept() + "。");
				}
			}
			List<AuditNode> auditNodeList = totalDao.query(hql,
					circuitCustomize.getId());
			if (auditNodeList != null && auditNodeList.size() > 0) {
				// 生成对应的审批流程节点
				for (AuditNode auditNode : auditNodeList) {
					ExecutionNode executionNode = new ExecutionNode();
					BeanUtils.copyProperties(auditNode, executionNode,
							new String[] { "id" });
					executionNode.setAuditStatus("未审批");
					executionNode.setCircuitRun(circuitRun);
					executionNode.setAuditLevel(executionNode.getAuditLevel()
							+ level);
					exNodeSet.add(executionNode);
				}
				circuitRun.setExNodeSet(exNodeSet);
			}

		}

		if (exNodeSet == null || exNodeSet.size() == 0) {
			circuitRun.setAllStatus("同意");
			circuitRun.setAuditStatus("审批完成");
		}
		// // 更新对应实体状态
		// if (circuitRun.getEntityName() != null
		// && circuitRun.getEntityName().length() > 0) {
		// String hql2 = "update " + circuitRun.getEntityName() + " set "
		// + circuitRun.getEntityStatusName() + "=? where "
		// + circuitRun.getEntityIdName() + "=?";
		// // update Zhaobiao set status=? where id=?
		// try {
		// totalDao.createQueryUpdate(hql2, null, "未审批", circuitRun
		// .getEntityId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// 添加审批流程
		boolean bool = totalDao.save(circuitRun);
		if (bool && more != null && more.length() > 0 && exNodeSet != null
				&& exNodeSet.size() > 0) {
			// 获得第一层的审批人
			String hql2 = "select auditUserId from ExecutionNode where circuitRun.id=? and auditLevel=1";
			List list = totalDao.query(hql2, circuitRun.getId());
			Integer[] usersId = new Integer[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Integer userid = Integer.parseInt(list.get(i).toString());
				usersId[i] = userid;
			}
			// 生成提醒消息
			String messageIds = AlertMessagesServerImpl.addAlertMessages(more,
					more, usersId, "CircuitRunAction_findAduitPage.action?id="
							+ circuitRun.getId(), isMessage);
			if (messageIds != null) {
				// 将消息ids保存到审批表中
				circuitRun.setMessageIds(messageIds);
				totalDao.update(circuitRun);
			}
		}

		return circuitRun.getId();
	}

	/***
	 * 生成执行流程(简单单一的、不发送消息提醒的)
	 * 
	 * @param ccId
	 *            定制流程id
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(Integer ccId) throws Exception {
		if (ccId != null && ccId > 0) {
			TotalDao totalDao = createTotol();
			// 查询定制流程
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectById(CircuitCustomize.class, ccId);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, null, null, null, null, null,
						null, false, totalDao, null);
			} else {
				throw new Exception("无法找到Id为" + ccId + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程(简单的，发送消息提醒)
	 * 
	 * @param ccId
	 *            定制流程id
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            是否发送手机短信提醒
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(Integer ccId, String more,
			Boolean isMessage) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		// 发送消息提醒、说明必须填写
		if (more == null || more.length() <= 0) {
			throw new Exception("说明必须填写!");
		}
		if (ccId != null && ccId > 0) {
			// 查询定制流程
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectById(CircuitCustomize.class, ccId);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, null, null, null, null, null,
						more, isMessage, totalDao, null);
			} else {
				throw new Exception("无法找到Id为" + ccId + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程(复杂、不发送消息)
	 * 
	 * @param ccId
	 *            定制流程id
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            是否生成提醒
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(Integer ccId, Class classs,
			Integer entityId, String statusName, String idName, String hdStatus)
			throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (ccId != null && ccId > 0) {
			// 查询定制流程
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectById(CircuitCustomize.class, ccId);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, classs, entityId, statusName,
						idName, null, null, false, totalDao, hdStatus);
			} else {
				throw new Exception("无法找到Id为" + ccId + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程
	 * 
	 * @param ccId
	 *            定制流程id
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param statusName
	 *            回调审批状态( 对应实体类的审批状态)
	 * @param idName
	 *            对应实体类的主键 名称
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            是否发送手机提醒
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(Integer ccId, Class classs,
			Integer entityId, String statusName, String idName, String more,
			Boolean isMessage, String hdStatus) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		// 发送消息提醒、说明必须填写
		if (isMessage) {
			if (more == null || more.length() <= 0) {
				throw new Exception("说明必须填写!");
			}
		}
		if (ccId != null && ccId > 0) {
			// 查询定制流程
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectById(CircuitCustomize.class, ccId);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, classs, entityId, statusName,
						idName, null, more, isMessage, totalDao, hdStatus);
			} else {
				throw new Exception("无法找到Id为" + ccId + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程
	 * 
	 * @param ccId
	 *            定制流程id
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param detailUrl
	 *            对应的详细信息地址
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            是否发送手机提醒
	 * @param hdStatus
	 *            回调状态(用于更新结束状态)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(Integer ccId, Class classs,
			Integer entityId, String statusName, String idName,
			String detailUrl, String more, Boolean isMessage, String hdStatus)
			throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		// 发送消息提醒、说明必须填写
		if (isMessage) {
			if (more == null || more.length() <= 0) {
				throw new Exception("说明必须填写!");
			}
		}
		if (ccId != null && ccId > 0) {
			// 查询定制流程
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectById(CircuitCustomize.class, ccId);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, classs, entityId, statusName,
						idName, detailUrl, more, isMessage, totalDao, hdStatus);
			} else {
				throw new Exception("无法找到Id为" + ccId + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程(复杂、不发提醒)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(String ccName, Class classs,
			Integer entityId, String statusName, String idName, String hdStatus)
			throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (ccName != null && ccName.length() > 0) {
			// 查询定制流程
			String hql = "from CircuitCustomize where name=?";
			CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
					.getObjectByQuery(hql, ccName);
			if (circuitCustomize != null) {
				return addEp(circuitCustomize, classs, entityId, statusName,
						idName, null, null, false, totalDao, null);
			} else {
				throw new Exception("无法找到Id为" + ccName + "的定制流程!");
			}
		} else {
			throw new Exception("定制流程Id不能为空!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            说明(用于消息提醒,必须填写)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(String ccName, Class classs,
			Integer entityId, String statusName, String idName, String more,
			Boolean isMessage) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				List lsit = totalDao.query(hql, ccName);
				if (lsit != null && lsit.size() > 0) {
					CircuitCustomize circuitCustomize = (CircuitCustomize) lsit
							.get(0);
					if (circuitCustomize != null) {
						return addEp(circuitCustomize, classs, entityId,
								statusName, idName, null, more, isMessage,
								totalDao, null);
					} else {
						throw new Exception("无法找到名称为" + ccName + "的定制流程!");
					}
				} else {
					throw new Exception("无法找到名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程名称不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            说明(用于消息提醒,必须填写)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcessbf(String ccName, Class classs,
			Integer entityId, String statusName, String idName, String more,
			Boolean isMessage, String uIds) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				List lsit = totalDao.query(hql, ccName);
				if (lsit != null && lsit.size() > 0) {
					CircuitCustomize circuitCustomize = (CircuitCustomize) lsit
							.get(0);
					if (circuitCustomize != null) {
						return addEp(circuitCustomize, classs, entityId,
								statusName, idName, null, more, isMessage,
								totalDao, null, uIds);
					} else {
						throw new Exception("无法找到名称为" + ccName + "的定制流程!");
					}
				} else {
					throw new Exception("无法找到名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程名称不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            说明(用于消息提醒,必须填写)
	 * @param hdStatus
	 *            回调状态(用于更新结束状态)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(String ccName, Class classs,
			Integer entityId, String statusName, String idName, String more,
			Boolean isMessage, String hdStatus) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByQuery(hql, ccName);
				if (circuitCustomize != null) {
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, null, more, isMessage,
							totalDao, hdStatus);
				} else {
					throw new Exception("无法找到Id为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param more
	 *            说明(用于消息提醒,必须填写)
	 * @param isMessage
	 *            说明(用于消息提醒,必须填写)
	 * @param hdStatus
	 *            回调状态(用于更新结束状态)
	 * @param uIds
	 *            前置审批人员的id
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcessbf(String ccName, Class classs,
			Integer entityId, String statusName, String idName, String more,
			Boolean isMessage, String hdStatus, String uIds) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByQuery(hql, ccName);
				if (circuitCustomize != null) {
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, null, more, isMessage,
							totalDao, hdStatus, uIds);
				} else {
					throw new Exception("无法找到Id为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒、查看详细)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param statusName
	 *            对应实体状态名称
	 * @param idName
	 *            对应实体Id名称
	 * @param detailUrl
	 *            回调明细地址
	 * @param more
	 *            提醒消息
	 * @param isMessage
	 *            是否发送消息(false/true)
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(String ccName, Class classs,
			Integer entityId, String statusName, String idName,
			String detailUrl, String more, Boolean isMessage) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByCondition(hql, ccName);
				if (circuitCustomize != null) {
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, detailUrl, more, isMessage,
							totalDao, null);
				} else {
					throw new Exception("无法找到流程名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒、查看详细)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param statusName
	 *            对应实体状态名称
	 * @param idName
	 *            对应实体Id名称
	 * @param detailUrl
	 *            回调明细地址
	 * @param more
	 *            提醒消息
	 * @param isMessage
	 *            是否发送消息(false/true)
	 * @param dept
	 *            申请部门
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcess(String ccName, Class classs,
			Integer entityId, String statusName, String idName,
			String detailUrl, String more, Boolean isMessage, String dept)
			throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByCondition(hql, ccName);
				if (circuitCustomize != null) {
					if (dept != null && dept.length() > 0) {
						circuitCustomize.setDept(dept);
					}
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, detailUrl, more, isMessage,
							totalDao, null);
				} else {
					throw new Exception("无法找到流程名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒、查看详细)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param statusName
	 *            对应实体状态名称
	 * @param idName
	 *            对应实体Id名称
	 * @param detailUrl
	 *            回调明细地址
	 * @param more
	 *            提醒消息
	 * @param isMessage
	 *            是否发送消息(false/true)
	 * @param uIds
	 *            增加在流程节点之前的用户Id
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcessbf(String ccName, Class classs,
			Integer entityId, String statusName, String idName,
			String detailUrl, String more, Boolean isMessage, String uIds)
			throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByCondition(hql, ccName);
				if (circuitCustomize != null) {
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, detailUrl, more, isMessage,
							totalDao, null, uIds);
				} else {
					throw new Exception("无法找到流程名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 生成执行流程(复杂、发提醒、查看详细)
	 * 
	 * @param ccName
	 *            定制流程名称
	 * @param classs
	 *            对应实体类(可有可无,推荐填写)
	 * @param entityId
	 *            对应实体类Id(可有可无,推荐填写)
	 * @param statusName
	 *            对应实体状态名称
	 * @param idName
	 *            对应实体Id名称
	 * @param detailUrl
	 *            回调明细地址
	 * @param more
	 *            提醒消息
	 * @param isMessage
	 *            是否发送消息(false/true)
	 * @param uIds
	 *            增加在流程节点之前的用户Id
	 * @param leaveAssign
	 *            是否指定等级（1:同级）（2：递增）（3：uIds=1:451,1:12,2:45）
	 * @return
	 * @throws Exception
	 */
	public static Integer createProcessbf(String ccName, Class classs,
			Integer entityId, String statusName, String idName,
			String detailUrl, String more, Boolean isMessage, String uIds,
			String leaveAssign) throws Exception {
		// 获得hibernateTemplate对象，并赋值给totalDao
		TotalDao totalDao = createTotol();
		if (more != null && more.length() > 0) {
			if (ccName != null && ccName.length() > 0) {
				// 查询定制流程
				String hql = "from CircuitCustomize where name=?";
				CircuitCustomize circuitCustomize = (CircuitCustomize) totalDao
						.getObjectByCondition(hql, ccName);
				if (circuitCustomize != null) {
					return addEp(circuitCustomize, classs, entityId,
							statusName, idName, detailUrl, more, isMessage,
							totalDao, null, uIds, leaveAssign);
				} else {
					throw new Exception("无法找到流程名称为" + ccName + "的定制流程!");
				}
			} else {
				throw new Exception("定制流程Id不能为空!");
			}
		} else {
			throw new Exception("说明必须填写!");
		}
	}

	/***
	 * 通过流程id,获得对应实体对象
	 * 
	 * @param circuitRunId
	 *            流程id
	 * @return
	 */
	@Override
	public String findEntityByCcId(Integer circuitRunId) {
		if (circuitRunId != null) {
			CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, circuitRunId);
			if (circuitRun != null) {
				String hql = "from " + circuitRun.getEntityName() + " where "
						+ circuitRun.getEntityIdName() + "="
						+ circuitRun.getEntityId();
				Object obj = totalDao.getObjectByQuery(hql);
				List<SortableField> list = Parent.getSortableField(obj);// 获取泛型中类里面的注解
				// 输出结果
				// String message = "";
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				for (SortableField l : list) {
					map.put(l.getMeta().name(), l.getValue().toString());
				}
				if ("OrderManager".equals(circuitRun.getEntityName())) {
					String hql_pro = "from ProductManager where orderManager.id=?";
					List<ProductManager> list_pro = totalDao.query(hql_pro,
							circuitRun.getEntityId());
					if (list_pro != null) {
						for (ProductManager productManager : list_pro) {
							map.put("|-----------------", "-----------------|");
							map.put("件号", productManager.getPieceNumber() + "("
									+ productManager.getYwMarkId() + ")");
							if (productManager.getDanwei() == null
									|| "null"
											.equals(productManager.getDanwei())) {
								productManager.setDanwei("PCS");
							}
							map.put("订单数量", productManager.getNum() + ""
									+ productManager.getDanwei());
							map.put("交付日期", productManager.getPaymentDate());
							map.put("|------------------",
									"------------------|");
						}
					}
				}
				// message = message.substring(0, message.lastIndexOf("<br/>"));
				return JSON.toJSONString(map);

			}

		}
		return null;
	}

	/*
	 * 查看借款审批(non-Javadoc)
	 * 
	 * @see com.task.Server.sys.CircuitRunServer#findPaymentVoucher(int)
	 */
	@Override
	public PaymentVoucher findPaymentVoucher(int id) {
		String hql = "from PaymentVoucher where epId=?";
		PaymentVoucher paymentVoucher = (PaymentVoucher) this.totalDao
				.getObjectByQuery(hql, id);
		return paymentVoucher;
	}

	/***
	 * 通过消息提醒id，获得审批
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Integer findCirRunByMesId(Integer id) {
		AlertMessages am = (AlertMessages) totalDao.getObjectById(
				AlertMessages.class, id);
		if (am != null) {
			// "CircuitRunAction_findAduitPage.action?id=2450"
			String url = am.getFunctionUrl();
			if (url != null && url.length() > 0) {
				int cirindex = url.indexOf("=");
				Integer cirId = Integer.parseInt(url.substring(cirindex + 1,
						url.length()));
				return cirId;
			}
		}
		return null;
	}

	// 查询审批
	@Override
	public Object[] findCircuitRun(CircuitRun circuitRun, int parseInt,
			int pageSize) {
		if (circuitRun == null) {
			circuitRun = new CircuitRun();
		}
		String hql = totalDao.criteriaQueries(circuitRun, null, "addUserId");

		if (circuitRun.getAddUserId() != null && circuitRun.getAddUserId() == 0) {
			// 个人待审批
			hql += " and id in (select circuitRun.id from ExecutionNode where auditUserId="
					+ Util.getLoginUser().getId()
					+ " and auditStatus='未审批'  and circuitRun.allStatus not in ('打回','同意') " +
							"and auditLevel= circuitRun.auditLevel)";
		}
		if (circuitRun.getAddUserId() != null && circuitRun.getAddUserId() == 1) {
			// 个人所有审批记录
			hql += " and id in (select circuitRun.id from ExecutionNode where auditUserId="
					+ Util.getLoginUser().getId()
					+ ")";
		}
		hql += " order by id desc";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	// 版本升级
	public boolean banbenShengji(List<ProcardTemplateBanBen> ptbbList) {
		// TODO Auto-generated method stub
		if (ptbbList != null && ptbbList.size() > 0) {
			buildBanbenRelation(ptbbList);// 建立升级前版本关系
			for (ProcardTemplateBanBen bb2 : ptbbList) {// 将当前默认的bom复制一份出来作为历史，并修改当前的工艺状态用来重新审批
				List<ProcardTemplate> ptList2 = totalDao
						.query(
								"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null)  ",
								bb2.getMarkId());
				if (ptList2.size() > 0) {
					for (ProcardTemplate pt2 : ptList2) {
						String historySql = "from ProcardTemplate where markId=? and banbenStatus='历史' ";
						if (pt2.getBanBenNumber() == null
								|| pt2.getBanBenNumber().length() == 0) {
							historySql = historySql
									+ " and (banBenNumber is null or banBenNumber='')";
						} else {
							historySql = historySql + " and banBenNumber='"
									+ pt2.getBanBenNumber() + "'";
						}
						ProcardTemplate history = (ProcardTemplate) totalDao
								.getObjectByCondition(historySql, pt2
										.getMarkId());
						if (history == null) {// 此版本还没有历史，添加历史
							history = new ProcardTemplate();
							BeanUtils.copyProperties(pt2, history,
									new String[] { "id", "procardTemplate",
											"procardTSet", "processTemplate",
											"zhUsers" });
							history.setBanbenStatus("历史");
							// 同步工序
							Set<ProcessTemplate> processSet1 = pt2
									.getProcessTemplate();
							Set<ProcessTemplate> processSet2 = new HashSet<ProcessTemplate>();
							if (processSet1 != null && processSet1.size() > 0) {// 添加在添加列表中存在的工序号的工序
								for (ProcessTemplate process1 : processSet1) {
									if (process1.getProcessNO() != null) {
										ProcessTemplate process2 = new ProcessTemplate();
										// -----------辅料开始----------
										if (process1.getIsNeedFuliao() != null
												&& process1.getIsNeedFuliao()
														.equals("yes")) {
											Set<ProcessFuLiaoTemplate> fltmpSet = process1
													.getProcessFuLiaoTemplate();
											if (fltmpSet.size() > 0) {
												Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
												for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
													ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
													BeanUtils
															.copyProperties(
																	fltmp,
																	pinfoFl,
																	new String[] {
																			"id",
																			"processTemplate" });
													if (pinfoFl.getQuanzhi1() == null) {
														pinfoFl.setQuanzhi1(1f);
													}
													if (pinfoFl.getQuanzhi2() == null) {
														pinfoFl.setQuanzhi2(1f);
													}
													pinfoFl
															.setProcessTemplate(process2);
													pinfoFlSet.add(pinfoFl);
												}
												process2
														.setProcessFuLiaoTemplate(pinfoFlSet);
											}
										}
										// -----------辅料结束----------
										BeanUtils
												.copyProperties(
														process1,
														process2,
														new String[] {
																"id",
																"procardTemplate",
																"taSopGongwei",
																"processFuLiaoTemplate" });
										process2.setProcardTemplate(history);
										totalDao.save(process2);
										processSet2.add(process2);
									}
								}
							}
							history.setProcessTemplate(processSet2);
							totalDao.save(history);

						}
						pt2.setBzStatus("初始");
						if (pt2.getBanci() == null) {
							pt2.setBanci(1);
						} else {
							pt2.setBanci(pt2.getBanci() + 1);
						}
						totalDao.update(pt2);
					}
				}
			}
			buildBanbenRelation(ptbbList);// 第二遍建立升级后版本关系
		}
		return true;
	}

	// 建立版本关系
	public boolean buildBanbenRelation(List<ProcardTemplateBanBen> ptbbList) {
		for (ProcardTemplateBanBen bb : ptbbList) {// 第一遍查看最初的父子件号是否存在版本关系，如果没有则建立版本关系
			List<ProcardTemplate> ptList = totalDao
					.query(
							"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null)  ",
							bb.getMarkId());
			if (ptList.size() > 0) {
				for (ProcardTemplate pt : ptList) {
					// 父件号
					ProcardTemplate father = null;
					if (pt.getFatherId() != null) {
						father = (ProcardTemplate) totalDao.getObjectById(
								ProcardTemplate.class, pt.getFatherId());
						String sql = "from ProcardTBanbenRelation where fmarkId=? and smarkId=? ";
						if (father.getBanBenNumber() == null
								|| father.getBanBenNumber().equals("")) {
							sql = sql + " and (fbanben is null or fbanben='')";
						} else {
							sql = sql + " and fbanben='"
									+ father.getBanBenNumber() + "'";
						}
						if (pt.getBanBenNumber() == null
								|| pt.getBanBenNumber().equals("")) {
							sql = sql + " and (sbanben is null or sbanben='')";
						} else {
							sql = sql + " and sbanben='" + pt.getBanBenNumber()
									+ "'";
						}
						ProcardTBanbenRelation bbRelation = (ProcardTBanbenRelation) totalDao
								.getObjectByCondition(sql, father.getMarkId(),
										pt.getMarkId());
						if (bbRelation == null) {
							bbRelation = new ProcardTBanbenRelation();
							bbRelation.setFmarkId(father.getMarkId());
							bbRelation.setFbanben(father.getBanBenNumber());
							if (father.getBanci() == null) {
								bbRelation.setFbanci(0);
							} else {
								bbRelation.setFbanci(father.getBanci());
							}
							bbRelation.setSmarkId(pt.getMarkId());
							bbRelation.setSbanben(pt.getBanBenNumber());
							if (pt.getBanci() == null) {
								bbRelation.setFbanci(0);
							} else {
								bbRelation.setSbanci(pt.getBanci());
							}
							totalDao.save(bbRelation);
						}
					}
					// 子件号
					Set<ProcardTemplate> sonSet = pt.getProcardTSet();
					if (sonSet != null && sonSet.size() > 0) {
						for (ProcardTemplate son : sonSet) {
							String sql2 = "from ProcardTBanbenRelation where fmarkId=? and smarkId=? ";
							if (pt.getBanBenNumber() == null
									|| pt.getBanBenNumber().equals("")) {
								sql2 = sql2
										+ " and (fbanben is null or fbanben='')";
							} else {
								sql2 = sql2 + " and fbanben='"
										+ pt.getBanBenNumber() + "'";
							}
							if (son.getBanBenNumber() == null
									|| son.getBanBenNumber().equals("")) {
								sql2 = sql2
										+ " and (sbanben is null or sbanben='')";
							} else {
								sql2 = sql2 + " and sbanben='"
										+ son.getBanBenNumber() + "'";
							}
							ProcardTBanbenRelation bbRelation2 = (ProcardTBanbenRelation) totalDao
									.getObjectByCondition(sql2, pt.getMarkId(),
											son.getMarkId());
							if (bbRelation2 == null) {
								bbRelation2 = new ProcardTBanbenRelation();
								bbRelation2.setFmarkId(pt.getMarkId());
								bbRelation2.setFbanben(pt.getBanBenNumber());
								if (father != null && father.getBanci() == null) {
									bbRelation2.setFbanci(0);
								} else {
									bbRelation2.setFbanci(pt.getBanci());
								}
								bbRelation2.setSmarkId(son.getMarkId());
								bbRelation2.setSbanben(son.getBanBenNumber());
								if (son.getBanci() == null) {
									bbRelation2.setFbanci(0);
								} else {
									bbRelation2.setSbanci(son.getBanci());
								}
								totalDao.save(bbRelation2);
							}
						}
					}
				}

			}
		}
		return true;
	}

	// 查找百分比通过户籍
	public InsuranceGold findInsuranceGoldBylc(String localOrField,
			String cityOrCountryside, String personClass) {
		if (localOrField != null && cityOrCountryside != null) {
			String dateTime = Util.getDateTime("yyyy-MM-dd");
			String hql = "from InsuranceGold where localOrField=? and cityOrCountryside=? and personClass=? and validityStartDate<=? and validityEndDate>=?";
			List list = totalDao.query(hql, localOrField, cityOrCountryside,
					personClass, dateTime, dateTime);
			if (list != null && list.size() > 0) {
				return (InsuranceGold) list.get(0);
			}
		}
		return null;
	}

	// 根据工号和卡号查找该用户当前使用的工资信息
	public WageStandard findWSByUser(String code) {
		String hql = "from WageStandard where code=? and standardStatus='默认'";
		List list = totalDao.query(hql, code);
		WageStandard ws = null;
		if (list != null && list.size() > 0) {
			ws = (WageStandard) list.get(0);
		}
		return ws;
	}

	/**
	 * 归还下层外委包料数量
	 * 
	 * @param procard
	 * @param wwblCount
	 * @param type
	 * @return
	 */
	private String backProcardWwblCount(Procard procard, Float wwblCount,
			int type) {
		// TODO Auto-generated method stub
		Set<Procard> sonset = procard.getProcardSet();
		if (sonset != null && sonset.size() > 0) {
			for (Procard son : sonset) {
				if (!son.getProcardStyle().equals("外购")) {
					if (son.getWwblCount() != null && son.getWwblCount() > 0) {
						son.setWwblCount(son.getWwblCount() - wwblCount
								* son.getCorrCount());
						if (son.getWwblCount() < 0) {
							son.setWwblCount(0f);
						}
						totalDao.update(son);
						backProcardWwblCount(son, wwblCount
								* son.getCorrCount(), 1);
					}
				} else {
					if (type != 0
							|| (son.getNeedProcess() != null && son
									.getNeedProcess().equals("yes"))) {
						son.setWwblCount(son.getWwblCount() - wwblCount
								* son.getQuanzi2() / son.getQuanzi1());
						if (son.getWwblCount() < 0) {
							son.setWwblCount(0f);
						}
						totalDao.update(son);
					}
				}
			}
		}
		return "";
	}

	/**
	 * 我的工作台
	 * 
	 * @return
	 */
	@Override
	public List showWorks(String pageStatus) {
		Users loginUser = Util.getLoginUser();
		String hql = "";
		if ("audit".equals(pageStatus)) {// (待审批)
			hql = "select name,count(*) from CircuitRun c where allstatus<>'打回' and id in "
					+ "(select fk_exeproid from ExecutionNode e where auditUserId=? and auditstatus='未审批') GROUP BY name ORDER BY count(*) DESC";
		} else if ("apply".equals(pageStatus)) {// (我的申请)
			hql = "select name,count(*) from CircuitRun where allstatus<>'打回' and adduserid=? and"
					+ " allstatus not in ('同意','打回') GROUP BY name ORDER BY count(*) DESC";
		}
		return totalDao.query(hql, loginUser.getId());
	}

	/**
	 * 我的工作台(审批记录)
	 * 
	 * @return
	 */
	public Object[] showWorksCircuitRun(CircuitRun circuitRun, int parseInt,
			int pageSize, String pageStatus) {
		if (circuitRun == null) {
			circuitRun = new CircuitRun();
		}
		Users loginUser = Util.getLoginUser();
		String hql = totalDao.criteriaQueries(circuitRun, null);
		if ("audit".equals(pageStatus)) {// (待审批)
			hql += " and allstatus<>'打回' and id in "
					+ "(select fk_exeproid from ExecutionNode e where auditUserId=? and auditstatus='未审批') ";
		} else if ("apply".equals(pageStatus)) {// (我的申请)
			hql += " and allstatus<>'打回' and adduserid=? and allstatus not in ('同意','打回')";
		}
		hql += " order by addDateTime desc";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize, loginUser
				.getId());
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public List<OptionRun> findListOptionRun(Integer id) {
		if (id != null) {
			String hql = " from OptionRun where circuitRun.id = ?";
			return totalDao.query(hql, id);
		}
		return null;
	}

	@Override
	public void CircuitRunRemind() {
		String hql = "from ExecutionNode where id in (select e.id from ExecutionNode e join e.circuitRun c  "
				+ "where c.allStatus not in ('同意','打回') and e.auditLevel=c.auditLevel and e.auditStatus='未审批')";
		List<ExecutionNode> list = totalDao.query(hql);
		String hql2 = "select code from Users where id =? ";
		for (ExecutionNode executionNode : list) {
			String s_code = (String) totalDao.getObjectByCondition(hql2,
					executionNode.getAuditUserId());
			try {
				String objectHql = "from "
						+ executionNode.getCircuitRun().getEntityName()
						+ " where "
						+ executionNode.getCircuitRun().getEntityIdName()
						+ "=?";
				Object object = totalDao.getObjectByCondition(objectHql,
						executionNode.getCircuitRun().getEntityId());
				if (object == null) {
					totalDao.delete(executionNode.getCircuitRun());
					continue;
				}
			} catch (Exception e) {
			}
			if (s_code != null && s_code.length() > 0) {
				RtxUtil.sendNotify(s_code, executionNode.getCircuitRun()
						.getMessage()
						+ "  \n\t\t\t 系统推送 \n\tPEBS系统(点击进入审批):"
						+ AlertMessagesServerImpl.pebsUrl
						+ "CircuitRunAction_findAduitPage.action?id="
						+ executionNode.getCircuitRun().getId(), "未审批信息推送",
						"0", "0");
			}
		}
	}

	/***
	 * 人员审批时间分析
	 * 
	 * @param userId
	 *            人员id
	 * @param processName
	 *            流程名称
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	@Override
	public Map<String, Object> findAuditTime(Integer userId,
			String processName, String startTime, String endTime) {
		if (userId != null) {
			// 加载审批人员
			List userList = null;
			if (userId == 0) {
				String hql = "select id,name from Users where id in ("
						+ "select userId from AssessPersonnel where usersGroup.status='sh')"
						+ " and onWork not in ('离职','内退') ";
				userList = totalDao.query(hql);
				if (userList != null && userList.size() > 0) {
					Object[] userobject = (Object[]) userList.get(0);
					userId = (Integer) userobject[0];
				} else {
					return null;
				}
			}

			String hql = "select auditDateTime,timeLong from ExecutionNode "
					+ "where auditUserId=?  and timeLong is not null";
			if (processName != null && processName.length() > 0) {
				hql += " and circuitRun.name like '%" + processName + "%'";
			}
			if (startTime != null && startTime.length() > 0) {
				hql += " and auditDateTime>'" + startTime + "'";
			}
			if (endTime != null && endTime.length() > 0) {
				hql += " and auditDateTime<'" + endTime + "'";
			}
			List<Object[]> list = totalDao.query(hql, userId);
			Map<String, Object> map = new HashMap<String, Object>();
			if (list != null && list.size() > 0) {
				List datelist = new ArrayList();
				List timelonglist = new ArrayList();
				List zwslist = new ArrayList();
				float[] nums = new float[list.size()];
				int i = 0;
				for (Object[] objects : list) {
					String atime = (String) objects[0];
					Float hours = (Float) objects[1];
					datelist.add(atime.substring(0, 10));
					timelonglist.add(hours);
					nums[i] = hours;
					i++;
				}
				// 求中位数
				Float zws = Util.median(nums);
				for (float f : nums) {
					zwslist.add(zws);
				}
				map.put("timelong", timelonglist);
				map.put("yuefen", datelist);
				map.put("zws", zwslist);
			}
			map.put("users", userList);
			return map;
		}
		return null;
	}

	@Override
	public Object[] gonzuoliu(Integer addUserId) {

		List<CircuitRun> list = totalDao.query("FROM CircuitRun WHERE  addUserId=?   AND  allStatus NOT IN('同意','打回')",
				addUserId);
		String chang = null;
		for (CircuitRun circuitRun : list) {
			Float zws = 0F;
			// 查询该流程对应的审批记录
			String prolog = "from ExecutionNode  where  circuitRun.name=? and timeLong is not null";

			Integer auditcount = totalDao.getCount(prolog, circuitRun.getName());

			prolog += " order by timeLong";

			List getzws = new ArrayList();
			if (auditcount % 2 == 0) {// 偶数 （1/2+1）
				getzws = totalDao.findAllByPage(prolog, (int) Math.ceil(auditcount / 2 / 2), 2, circuitRun.getName());
			} else {// 奇数
				getzws = totalDao.findAllByPage(prolog, (int) Math.ceil(auditcount / 2), 1, circuitRun.getName());
			}
			if (getzws.size() > 0) {
				Float avgAuitTimeLong = 0F;
				for (int k = 0; k < getzws.size(); k++) {
					ExecutionNode executionNode_zws = (ExecutionNode) getzws.get(k);
					avgAuitTimeLong += executionNode_zws.getTimeLong();
				}
				
				zws = avgAuitTimeLong / getzws.size();// 标准时长
				chang=zws+"";

			}
		}

		return new Object[] { list, chang};
	}

	@Override
	public CircuitRun gonzuoliuxiangxi(int id) {
		
		return (CircuitRun)totalDao.getObjectByCondition("from CircuitRun where id=?", id);
	}

}