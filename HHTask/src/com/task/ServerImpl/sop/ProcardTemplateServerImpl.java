package com.task.ServerImpl.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.*;
import jxl.format.UnderlineStyle;
import jxl.write.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font; //import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import org.apache.struts2.ServletActionContext;
import org.hibernate.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.InsuranceGoldServer;
import com.task.Server.WageStandardServer;
import com.task.Server.sop.ProcardTemplateServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.ServerImpl.yw.ConvertNumber;
import com.task.entity.Category;
import com.task.entity.DTOProcess;
import com.task.entity.DataGrid;
import com.task.entity.InsuranceGold;
import com.task.entity.Price;
import com.task.entity.Users;
import com.task.entity.VOProductTree;
import com.task.entity.WageStandard;
import com.task.entity.fin.UserMoneyDetail;
import com.task.entity.fin.UserMonthMoney;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.entity.sop.Banbenxuhao;
import com.task.entity.sop.FilerootIdLog;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardSpecification;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardTemplateChangeLog;
import com.task.entity.sop.ProcardTemplatePrivilege;
import com.task.entity.sop.ProcardTemplateReplace;
import com.task.entity.sop.ProcessAndWgProcardTem;
import com.task.entity.sop.ProcessFuLiaoTemplate;
import com.task.entity.sop.ProcessGzstoreFuLiao;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforReceiveLog;
import com.task.entity.sop.ProcessPriceUpdateLog;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ProcessinforFuLiao;
import com.task.entity.sop.jy.ProcardTemplateJY;
import com.task.entity.sop.jy.ProcessTemplateJY;
import com.task.entity.sop.ycl.PanelSize;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.util.MKUtil;
import com.task.util.Upload;
import com.task.util.Util;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.ProcessMarkIdZijian;

@SuppressWarnings("unchecked")
public class ProcardTemplateServerImpl implements ProcardTemplateServer {
	private StringBuffer strbu = new StringBuffer();
	private Integer jindu_sum = 0;
	private Integer jindu_cl = 0;
	private TotalDao totalDao;
	private WageStandardServer wss; // 工资模板
	private InsuranceGoldServer igs; // 五险一金Server层
	private static final Double SECONDS = 619200.0;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	/***
	 * 根据名称查询量具
	 */
	public List listliangju(String markId) {
		if (markId != null) {
			List procardTemplates = totalDao
					.query("from Measuring where matetag like '%" + markId
							+ "%'");
			return procardTemplates;
		}
		return null;
	}

	/***
	 * 根据工装编号查询工装
	 */
	public List listGzstoreBycarModel(String code) {
		if (code != null) {
			List procardTemplates = totalDao
					.query("from Gzstore where matetag like '%" + code + "%' ");
			return procardTemplates;
		}
		return null;
	}

	/***
	 * 添加流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public boolean addProcardTemplate(ProcardTemplate procardTemplate) {
		if (procardTemplate != null) {
			Users user = Util.getLoginUser();
			String nowtime = Util.getDateTime();
			procardTemplate.setAddcode(user.getCode());
			procardTemplate.setAdduser(user.getName());
			procardTemplate.setAddtime(nowtime);
			ProcardTemplate father = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, procardTemplate.getFatherId());
			if (father != null
					&& father.getBzStatus() != null
					&& (father.getBzStatus().equals("已批准") || father
							.getBzStatus().equals("设变发起中"))) {
				return false;
			}
			if (procardTemplate.getProcardTemplate() != null
					&& procardTemplate.getProcardTemplate().getId() != null) {
				Float maxXuHao = (Float) totalDao
						.getObjectByCondition(
								"select max(xuhao) from ProcardTemplate where procardTemplate.id=?",
								procardTemplate.getProcardTemplate().getId());
				if (maxXuHao == null) {
					procardTemplate.setXuhao(1f);
				} else {
					procardTemplate.setXuhao(maxXuHao + 1);
				}
			} else {
				procardTemplate.setXuhao(1f);
			}
			// 是否之前已在BOM中存在
			String kgliaosql = "";
			if (procardTemplate.getKgliao() != null
					&& procardTemplate.getKgliao().length() > 0) {
				kgliaosql = " and kgliao ='" + procardTemplate.getKgliao()
						+ "'";
			} else {
				kgliaosql = " and (kgliao is null or kgliao='')";
			}
			ProcardTemplate oldPt = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId=? and procardStyle=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
									+ kgliaosql, procardTemplate.getMarkId(),
							procardTemplate.getProcardStyle(), "批产");
			if (oldPt == null) {
				oldPt = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
										+ kgliaosql, procardTemplate
										.getMarkId(), "批产");
			}
			if (oldPt == null) {
				if (procardTemplate.getProductStyle().equals("试制")) {// 试制先借用批产，借用不到在借用试制
					oldPt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and procardStyle=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
											+ kgliaosql, procardTemplate
											.getMarkId(), procardTemplate
											.getProcardStyle(), "试制");
					if (oldPt == null) {
						oldPt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
												+ kgliaosql, procardTemplate
												.getMarkId(), "试制");
					}
				}
			}
			if (oldPt != null) {
				String oldProcardStyle = oldPt.getProcardStyle();
				Float oldq1 = oldPt.getQuanzi1();
				Float oldq2 = oldPt.getQuanzi2();
				Float oldc = oldPt.getCorrCount();
				Float xuhao = oldPt.getXuhao();
				String hasChange = oldPt.getHasChange();
				oldPt.setProcardStyle(procardTemplate.getProcardStyle());
				oldPt.setXuhao(procardTemplate.getXuhao());
				if (procardTemplate.getProcardStyle().equals("外购")) {
					oldPt.setQuanzi1(procardTemplate.getQuanzi1());
					oldPt.setQuanzi2(procardTemplate.getQuanzi2());
					oldPt
							.setLingliaostatus(procardTemplate
									.getLingliaostatus());
				} else {
					oldPt.setCorrCount(procardTemplate.getCorrCount());
				}
				if (procardTemplate.getBiaochu() != null
						&& procardTemplate.getBiaochu().length() > 0) {
					oldPt.setBiaochu(procardTemplate.getBiaochu());
				}
				oldPt.setHasChange("是");
				// 重新取一遍为了加载关联子层
				Integer addId = null;
				addId = saveCopyProcard(father, oldPt, procardTemplate
						.getProductStyle());
				oldPt.setHasChange(hasChange);
				oldPt.setProcardStyle(oldProcardStyle);
				oldPt.setQuanzi1(oldq1);
				oldPt.setQuanzi2(oldq2);
				oldPt.setCorrCount(oldc);
				oldPt.setXuhao(xuhao);
				ProcardTemplate newpt = (ProcardTemplate) totalDao
						.getObjectById(ProcardTemplate.class, addId);
				// 添加修改日志
				if (newpt != null) {
					ProcardTemplateChangeLog.addchangeLog(totalDao, father,
							newpt, "子件", "增加", user, nowtime);
				}
				return true;
			}
			procardTemplate.setProcardTemplate(father);
			// 不存在
			if (procardTemplate.getBianzhiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						" select name from Users where id =?", procardTemplate
								.getBianzhiId());
				procardTemplate.setBianzhiName(name);
				procardTemplate.setBzStatus("待编制");
			} else {
				procardTemplate.setBzStatus("初始");
			}
			if (procardTemplate.getJiaoduiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getJiaoduiId());
				procardTemplate.setJiaoduiName(name);
			}
			if (procardTemplate.getShenheId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getShenheId());
				procardTemplate.setShenheName(name);
			}
			if (procardTemplate.getPizhunId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getPizhunId());
				procardTemplate.setPizhunName(name);
			}
			// if (procardTemplate.getProcardStyle().equals("外购")
			// && (procardTemplate.getNeedProcess() == null || procardTemplate
			// .getNeedProcess().equals("no"))) {
			// procardTemplate.setSafeCount(null);
			// procardTemplate.setLastCount(null);
			// }
			procardTemplate.setRootMarkId(procardTemplate.getProcardTemplate()
					.getRootMarkId());
			procardTemplate.setYwMarkId(procardTemplate.getProcardTemplate()
					.getYwMarkId());
			if (procardTemplate.getLoadMarkId() == null
					|| procardTemplate.getLoadMarkId().length() == 0) {
				String sql = "select loadMarkId from ProcardTemplate where markId=? and loadMarkId is not null and loadMarkId !=''";
				if (procardTemplate.getBanBenNumber() == null
						|| procardTemplate.getBanBenNumber().length() == 0) {
					sql += " and (banBenNumber is null or banBenNumber='')";
				} else {
					sql += " and banBenNumber = '"
							+ procardTemplate.getBanBenNumber() + "'";
				}
				String loadMarkId = (String) totalDao.getObjectByCondition(sql,
						procardTemplate.getMarkId());
				if (loadMarkId == null) {
					loadMarkId = (String) totalDao
							.getObjectByCondition(
									"select markId from ProcardTemplate where rootId=?",
									procardTemplate.getRootId());
				}
				procardTemplate.setBanci(0);
				procardTemplate.setLoadMarkId(loadMarkId);
				procardTemplate.setHasChange("是");
				// if (procardTemplate.getProcardStyle().equals("外购")) {//
				// 外购件返初始总成到外购件库
				// String wgjSql =
				// "from YuanclAndWaigj where (zcMarkId is null or zcMarkId ='') and markId=?";
				// if (procardTemplate.getSpecification() == null
				// || procardTemplate.getSpecification().length() == 0) {
				// wgjSql += " and (specification is null or specification='')";
				// } else {
				// wgjSql += " and specification ='"
				// + procardTemplate.getSpecification() + "'";
				// }
				// if (procardTemplate.getBanBenNumber() == null
				// || procardTemplate.getBanBenNumber().length() == 0) {
				// wgjSql += " and (banbenhao is null or banbenhao='')";
				// } else {
				// wgjSql += " and banbenhao ='"
				// + procardTemplate.getBanBenNumber() + "'";
				// }
				// YuanclAndWaigj wgjTem = (YuanclAndWaigj) totalDao
				// .getObjectByCondition(wgjSql, procardTemplate
				// .getMarkId());
				// if (wgjTem != null) {
				// wgjTem.setZcMarkId(loadMarkId);
				// totalDao.update(wgjTem);
				// }
				// }
			}
			procardTemplate.sumXiaoHaoCount(0);
			procardTemplate.setAdduser(user.getName());// 添加人
			procardTemplate.setAddcode(user.getCode());// 添加人工号
			procardTemplate.setAddtime(Util.getDateTime());// 添加时间
			boolean bool = true;
			try {
				bool = totalDao.save(procardTemplate);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				throw new RuntimeException("添加失败!");
			}
			// 同步相同上层
			String banbenSql = "";
			if (procardTemplate.getProcardTemplate().getBanBenNumber() == null
					|| procardTemplate.getProcardTemplate().getBanBenNumber()
							.length() == 0) {
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			} else {
				banbenSql = " and banBenNumber='"
						+ procardTemplate.getProcardTemplate()
								.getBanBenNumber() + "'";

			}
			List<ProcardTemplate> fatherList = totalDao
					.query(
							"from ProcardTemplate where productStyle=? and markId=(select markId from ProcardTemplate where id=?) and procardStyle!='外购' and id !=? and (dataStatus is null or dataStatus!='删除') and (banbenStatus is null or banbenStatus='默认')"
									+ banbenSql, father.getProductStyle(),
							procardTemplate.getFatherId(), procardTemplate
									.getFatherId());
			if (fatherList != null && fatherList.size() > 0) {
				for (ProcardTemplate father2 : fatherList) {
					Set<ProcardTemplate> sonList = father2.getProcardTSet();
					boolean has = false;
					for (ProcardTemplate son : sonList) {
						if ((son.getDataStatus() == null || !son
								.getDataStatus().equals("删除"))
								&& son.getMarkId().equals(
										procardTemplate.getMarkId())) {
							has = true;
						}
					}
					if (!has) {
						ProcardTemplate newSon = new ProcardTemplate();
						// newpt2.setMarkId("123");
						BeanUtils.copyProperties(procardTemplate, newSon,
								new String[] { "id", "procardTemplate",
										"procardTSet", "processTemplate",
										"zhUsers", "fatherId" });
						newSon.setId(null);
						newSon.setRootId(father2.getRootId());
						newSon.setFatherId(father2.getId());
						if (father2.getBelongLayer() != null) {
							newSon.setBelongLayer(father2.getBelongLayer() + 1);
						}
						// 复制process
						Set<ProcessTemplate> processSet = procardTemplate
								.getProcessTemplate();
						if (processSet != null && processSet.size() > 0) {
							Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
							for (ProcessTemplate process : processSet) {
								ProcessTemplate p = new ProcessTemplate();
								BeanUtils.copyProperties(process, p,
										new String[] { "id", "procardTemplate",
												"processPRNScore",
												"taSopGongwei",
												"processFuLiaoTemplate" });
								// -----辅料开始---------
								if (p.getIsNeedFuliao() != null
										&& p.getIsNeedFuliao().equals("yes")) {
									Set<ProcessFuLiaoTemplate> fltmpSet = process
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
											pinfoFl.setProcessTemplate(p);
											pinfoFlSet.add(pinfoFl);
										}
										p.setProcessFuLiaoTemplate(pinfoFlSet);
									}
								}
								// -----辅料结束---------
								newprocessSet.add(p);
							}
							newSon.setProcessTemplate(newprocessSet);
						}
						newSon.setProcardTemplate(father2);
						sonList.add(newSon);
						father2.setProcardTSet(sonList);
						String msg = isSameProcardStyle(newSon);
						if ("".equals(msg)) {
							totalDao.save(newSon);
						} else {
							throw new RuntimeException(msg);
						}

					}
				}
			}
			if (bool) {
				// 添加修改日志
				ProcardTemplateChangeLog.addchangeLog(totalDao, father,
						procardTemplate, "子件", "增加", user, nowtime);
				// 添加成功以后，向编制人员发送提醒消息
				if (procardTemplate.getBianzhiId() != null) {
					AlertMessagesServerImpl.addAlertMessages("BOM编制提醒",
							"件号" + procardTemplate.getMarkId()
									+ "需要您前往编制,请及时处理,谢谢!",
							new Integer[] { procardTemplate.getBianzhiId() },
							"procardTemplateGyAction_findSonsForBz.action?id="
									+ procardTemplate.getRootId(), true);
				}
				return bool;
			}
		}
		return false;
	}

	/***
	 * 添加总成流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public String addRootProcardTem(ProcardTemplate procardTemplate) {
		if (procardTemplate != null) {
			procardTemplate.setMarkId(procardTemplate.getMarkId().replaceAll(
					" ", ""));// 去除件号中的所有空格
			if (procardTemplate.getMarkId() == null
					|| procardTemplate.getMarkId().length() == 0) {
				return "件号不能为空,添加失败!";
			}
			ProcardTemplate old = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')",
							procardTemplate.getMarkId(), procardTemplate
									.getProductStyle());
			if (old != null) {
				return "该总成件号已存在添加失败!";
			}
			if (procardTemplate.isDanJiaojian()) {
				procardTemplate.sumXiaoHaoCount(0);
			}
			procardTemplate.setProcardStyle("总成");
			procardTemplate.setRootMarkId(procardTemplate.getMarkId());
			procardTemplate.setLoadMarkId(procardTemplate.getMarkId());
			procardTemplate.setFatherId(0);// 父类id设置为0
			procardTemplate.setBelongLayer(1);// 设置层数为1
			procardTemplate.setAddDateTime(Util.getDateTime());// 设置层数为1
			procardTemplate.setDailyoutput(0F);// 日产量归零
			procardTemplate.setOnePrice(0D);// 单件价格归零
			Users user = Util.getLoginUser();
			procardTemplate.setCreateUserId(user.getId());
			procardTemplate.setCreateUserName(user.getName());
			procardTemplate.setCreateDate(Util.getDateTime());
			procardTemplate.setScore(0);
			procardTemplate.setTotalScore(75);
			if (procardTemplate.getBianzhiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						" select name from Users where id =?", procardTemplate
								.getBianzhiId());
				procardTemplate.setBianzhiName(name);
				procardTemplate.setBzStatus("待编制");
			} else {
				procardTemplate.setBzStatus("初始");
			}
			if (procardTemplate.getJiaoduiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getJiaoduiId());
				procardTemplate.setJiaoduiName(name);
			}
			if (procardTemplate.getShenheId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getShenheId());
				procardTemplate.setShenheName(name);
			}
			if (procardTemplate.getPizhunId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplate
								.getPizhunId());
				procardTemplate.setPizhunName(name);
			}
			boolean bool = totalDao.save(procardTemplate);
			if (bool) {
				procardTemplate.setRootId(procardTemplate.getId());
				return "true";
			} else {
				return "添加失败!";
			}
		}
		return "模板为空，添加失败!";
	}

	/***
	 * 删除流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public String delProcardTemplate(ProcardTemplate procardTemplate) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (procardTemplate != null) {
			procardTemplate = (ProcardTemplate) totalDao.get(
					ProcardTemplate.class, procardTemplate.getId());
			String markId = procardTemplate.getMarkId();
			if (procardTemplate.getProcardStyle().equals("总成")) {
				if (procardTemplate.getBzStatus().equals("已批准")) {
					return "此总成状态为已批准不允许删除!";
				}
				// 查询是否有转过生产,如果转过生产就不能删除就不允许删除
				Float count = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from Procard where markId=? and procardTemplateId=? and procardStyle='总成' and (sbStatus is null or sbStatus !='删除') and status not in('取消')",
								procardTemplate.getMarkId(), procardTemplate
										.getId());
				if (count != null && count > 0) {
					// 判断是否特权BOM
					Float tqCount = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
											+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) and p2.id=?",
									procardTemplate.getRootId());
					if (tqCount != null && tqCount > 0) {
						Float count2 = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from Procard where markId=? and procardTemplateId=? and procardStyle='总成' and (sbStatus is null or sbStatus !='删除') and status not in('取消','完成','待入库','入库')",
										procardTemplate.getMarkId(),
										procardTemplate.getId());
						if (count2 != null && count2 > 0) {
							return "此总成有未结束的生产BOM不允许删除!";
						}
					} else {
						return "此总成有对应有效的生产BOM不允许删除!";
					}
				}
				ProcardTemplate old = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, procardTemplate.getId());
				old.setDataStatus("删除");
				boolean b = totalDao.update(old);
				b = b & tbDownDataStatus(old);
				return b + "";
			} else {// 获取上层零件编制状态,已批准状态不允许删除
				String bzStatus = (String) totalDao.getObjectByCondition(
						"select bzStatus from ProcardTemplate where id=?",
						procardTemplate.getFatherId());
				if (bzStatus != null
						&& (bzStatus.equals("已批准") || bzStatus.equals("设变发起中"))) {
					return "此零件上层零件状态为:" + bzStatus + "不允许删除下阶层!";
				}
			}
			boolean b = true;
			// 添加修改日志
			ProcardTemplate father = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, procardTemplate.getFatherId());
			ProcardTemplateChangeLog.addchangeLog(totalDao, father,
					procardTemplate, "子件", "删除", user, nowtime);
			// 删除上层同件号的同件号零件
			String banbenSql = null;
			if (procardTemplate.getProcardTemplate().getBanBenNumber() == null
					|| procardTemplate.getProcardTemplate().getBanBenNumber()
							.length() == 0) {
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			} else {
				banbenSql = " and banBenNumber='"
						+ procardTemplate.getProcardTemplate()
								.getBanBenNumber() + "'";

			}
			List<ProcardTemplate> sonList = totalDao
					.query(
							"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus='默认') and productStyle=? and "
									+ " procardTemplate.id in(select id from ProcardTemplate where markId=(select markId from ProcardTemplate where id=?) "
									+ "and (dataStatus is null or dataStatus!='删除') "
									+ banbenSql + ")", markId, procardTemplate
									.getProductStyle(), procardTemplate
									.getProcardTemplate().getId());
			if (sonList != null && sonList.size() > 0) {
				boolean b2 = false;
				for (int i = (sonList.size() - 1); i >= 0; i--) {
					ProcardTemplate procardt = sonList.get(i);
					if (!b2) {
						if ("外购".equals(procardt.getProcardStyle())
								&& procardt.getProcardTemplate() != null) {
							b2 = true;
							List<ProcessAndWgProcardTem> proAndwgptList = totalDao
									.query(
											" from ProcessAndWgProcardTem where procardMarkId = ? and wgprocardMardkId =? ",
											procardt.getProcardTemplate()
													.getMarkId(), procardt
													.getMarkId());
							if (proAndwgptList != null
									&& proAndwgptList.size() > 0) {
								for (ProcessAndWgProcardTem processAndWgProcardTem : proAndwgptList) {
									totalDao.delete(processAndWgProcardTem);
								}
							}
						}
					}
					// Set<ProcessTemplate> processSet =
					// procardt.getProcessTemplate();
					// if(processSet!=null&&processSet.size()>0){
					// for(ProcessTemplate process:processSet){
					// process.setDataStatus("删除");
					// totalDao.update(process);
					// }
					// }
					procardt.setDataStatus("删除");
					procardt.setProcardTemplate(null);
					b = b & totalDao.update(procardt);
					b = b & tbDownDataStatus(procardt);
				}
			}
			return "true";
		}

		return "没有找到目标零件!";
	}

	public boolean tbDownDataStatus(ProcardTemplate pt) {
		Set<ProcardTemplate> sonSet = pt.getProcardTSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (ProcardTemplate son : sonSet) {
				// Set<ProcessTemplate> processSet = son.getProcessTemplate();
				// if(processSet!=null&&processSet.size()>0){
				// for(ProcessTemplate process:processSet){
				// process.setDataStatus("删除");
				// totalDao.update(process);
				// }
				// }
				son.setDataStatus("删除");
				son.setProcardTemplate(null);
				totalDao.update(son);
				tbDownDataStatus(son);
			}
		}
		pt.setProcardTSet(null);
		totalDao.update(pt);
		return true;
	}

	/***
	 * 根据id查询流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public ProcardTemplate findProcardTemById(int id) {
		if ((Object) id != null && id > 0) {
			ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
			return pt;
		}
		return null;
	}

	/***
	 * 修改流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateProcardTemplate(ProcardTemplate procardTem, int updatelv) {
		boolean b = true;
		boolean bzbool = false;
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请登录!";
		}
		if (procardTem.getBianzhiId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getBianzhiId());
			procardTem.setBianzhiName(name);
			if (procardTem.getBzStatus().equals("初始")) {
				procardTem.setBzStatus("待编制");
				if (procardTem.getBianzhiId() == null) {
					procardTem.setBianzhiId(user.getId());
					procardTem.setBianzhiName(user.getName());
				}
				bzbool = true;
			}
		}
		if (procardTem.getJiaoduiId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getJiaoduiId());
			procardTem.setJiaoduiName(name);
		}
		if (procardTem.getShenheId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getShenheId());
			procardTem.setShenheName(name);
		}
		if (procardTem.getPizhunId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getPizhunId());
			procardTem.setPizhunName(name);
		}
		if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("外购")
				&& procardTem.getOldProcardStyle() != null
				&& procardTem.getOldProcardStyle().equals("待定")) {// 与库中匹配
			if (procardTem.getKgliao() == null
					|| procardTem.getKgliao().length() == 0) {
				return "请先填写供料属性";
			}
			// 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
			String wgjSql = "from YuanclAndWaigj where markId=? and kgliao=? and productStyle=?";
			// if (sonPt.getSpecification() == null
			// || sonPt.getSpecification().length() == 0) {
			// wgjSql +=
			// " and (specification is null or specification='')";
			// } else {
			// wgjSql += " and specification ='"
			// + sonPt.getSpecification() + "'";
			// }
			if (procardTem.getBanBenNumber() == null
					|| procardTem.getBanBenNumber().length() == 0) {
				wgjSql += " and (banbenhao is null or banbenhao='')";
			} else {
				wgjSql += " and banbenhao ='" + procardTem.getBanBenNumber()
						+ "'";
			}
			YuanclAndWaigj wgjTem = (YuanclAndWaigj) totalDao
					.getObjectByCondition(wgjSql, procardTem.getMarkId(),
							procardTem.getKgliao(), procardTem
									.getProductStyle());
			if (wgjTem == null) {
				if (procardTem.getWgType() == null
						|| procardTem.getWgType().length() == 0) {
					return "请先填写物料类别";
				}
				wgjTem = new YuanclAndWaigj();
				wgjTem.setMarkId(procardTem.getMarkId());// 件号
				wgjTem.setName(procardTem.getProName());// 名称
				wgjTem.setSpecification(procardTem.getSpecification());// 规格
				wgjTem.setUnit(procardTem.getUnit());// BOM用的单位
				wgjTem.setCkUnit(procardTem.getUnit());// 仓库用的单位
				wgjTem.setBanbenhao(procardTem.getBanBenNumber());// 版本号
				wgjTem.setZcMarkId(procardTem.getLoadMarkId());// 总成号
				wgjTem.setClClass("外购件");// 材料类型（外购件,原材料,辅料）
				wgjTem.setCaizhi(procardTem.getClType());// 材质类型
				wgjTem.setTuhao(procardTem.getTuhao());// 图号
				wgjTem.setKgliao(procardTem.getKgliao());// 供料属性
				wgjTem.setWgType(procardTem.getWgType());
				wgjTem.setBanbenStatus("默认");
				wgjTem.setProductStyle(procardTem.getProductStyle());
				wgjTem.setPricestatus("新增");
				wgjTem.setAddTime(Util.getDateTime());
				wgjTem.setAddUserCode(user.getCode());
				wgjTem.setAddUserName(user.getName());
				wgjTem.setImportance(procardTem.getImportance());
				totalDao.save(wgjTem);
			} else if (wgjTem.getStatus() != null
					&& wgjTem.getStatus().equals("禁用")) {
				return "外购件" + wgjTem.getMarkId() + "的"
						+ wgjTem.getSpecification() + "规格的"
						+ wgjTem.getBanbenhao() + "版本已禁用!";
			} else {
				if (procardTem.getKgliao() == null
						|| procardTem.getKgliao().length() == 0) {
					procardTem.setKgliao(wgjTem.getKgliao());
				}
				procardTem.setWgType(wgjTem.getWgType());
				if (wgjTem.getStatus() != null
						&& wgjTem.getStatus().equals("已确认")) {
					procardTem.setProName(wgjTem.getName());// 名称
					procardTem.setSpecification(wgjTem.getSpecification());// 规格
					procardTem.setUnit(wgjTem.getUnit());// BOM用的单位
					procardTem.setWgType(wgjTem.getWgType());
				} else {
					wgjTem.setName(procardTem.getProName());// 名称
					wgjTem.setSpecification(procardTem.getSpecification());// 规格
					wgjTem.setUnit(procardTem.getUnit());// BOM用的单位
					wgjTem.setWgType(procardTem.getWgType());
					totalDao.update(wgjTem);
				}
			}

		}
		if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("总成")) {
			if (procardTem.getDanjiaojian() == null
					|| !procardTem.getDanjiaojian().equals("单交件")) {// 非单交件
				procardTem.setTrademark(null);
				procardTem.setSpecification(null);
				procardTem.setYuanUnit(null);
				procardTem.setQuanzi1(null);
				procardTem.setQuanzi2(null);
				procardTem.setLuhao(null);
				procardTem.setNumber(null);
				procardTem.setActualFixed(null);
				// return totalDao.update(procardTem) + "";
			} else {// 单交件
				b = b & totalDao.update(procardTem);
				List<ProcardTemplate> plist = totalDao.query(
						"from ProcardTemplate where rootId=? and rootId!=id",
						procardTem.getId());
				if (plist.size() > 0) {
					for (ProcardTemplate pson : plist) {
						pson.setProcardTemplate(null);
						b = b & totalDao.delete(pson);
					}
				}
				return "true";
			}
		}
		boolean bool = false;
		if (procardTem != null) {
			procardTem.sumXiaoHaoCount(0);
			// 当自制件的展开尺寸发生变化 自动修改下层外购件的权值。
			if ("自制".equals(procardTem.getProcardStyle())) {
				ProcardTemplate oldpt = (ProcardTemplate) totalDao.get(
						ProcardTemplate.class, procardTem.getId());
				if (procardTem.getThisWidth() != null
						&& procardTem.getThisLength() != null
						&& procardTem.getThisHight() != null
						&& (!procardTem.getThisHight().equals(
								oldpt.getThisHight())
								|| !procardTem.getThisWidth().equals(
										oldpt.getThisWidth()) || !procardTem
								.getThisLength().equals(oldpt.getThisLength()))) {
					Set<ProcardTemplate> sonpt = oldpt.getProcardTSet();
					for (ProcardTemplate p : sonpt) {
						// 计算板材外购件的权值2
						String hql_wgj = " from YuanclAndWaigj where markId = ?  ";
						YuanclAndWaigj wgj = null;
						if (oldpt.getClType() != null
								&& oldpt.getClType().length() > 0) {
							hql_wgj += " and caizhi = '"
									+ oldpt.getClType()
									+ "' and density is not null and density >0";
							wgj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql_wgj, p
											.getMarkId());
							if (wgj != null) {
								Float quanzhi2 = (procardTem.getThisLength() + 12)
										* (procardTem.getThisWidth() + 12)
										* (procardTem.getThisHight())
										* wgj.getDensity() / 1000000;
								p.setQuanzi2(quanzhi2);
								totalDao.update(p);
							}
						}
						if (oldpt.getBiaochu() != null
								&& oldpt.getBiaochu().length() > 0) {
							String hql_fuhe = " from PanelSize where caizhi = ?";
							PanelSize fuhe = (PanelSize) totalDao
									.getObjectByCondition(hql_fuhe, oldpt
											.getBiaochu());
							if (fuhe != null) {
							} else {
								hql_wgj += " and name like '%"
										+ oldpt.getBiaochu()
										+ "%' and areakg is not null and areakg >0";
								wgj = (YuanclAndWaigj) totalDao
										.getObjectByCondition(hql_wgj, p
												.getMarkId());
								if (wgj != null) {
									Float quanzhi2 = (procardTem
											.getThisLength()
											* procardTem.getThisWidth() * 2)
											/ wgj.getAreakg() / 1000000;
									p.setQuanzi2(quanzhi2);
								}
							}
						}
					}
				}
				totalDao.clear();
			}

			bool = totalDao.update(procardTem);
			AttendanceTowServerImpl.updateJilu(3, procardTem, procardTem
					.getId(), procardTem.getMarkId());
		}
		String fMarkId = null;
		if (procardTem.getProcardTemplate() != null) {
			fMarkId = (String) totalDao.getObjectByCondition(
					"select markId from ProcardTemplate where id=?", procardTem
							.getFatherId());
		}

		List<ProcardTemplate> sameMarkIdList = null;
		String sqladd = null;
		if (procardTem.getProductStyle().equals("批产")) {
			sqladd = " and productStyle='批产' ";
		} else {
			sqladd = " and productStyle='试制' and rootId="
					+ procardTem.getRootId();
		}
		if (bool & updatelv == 2) {// 二级同步修改所有的同件号
			sameMarkIdList = totalDao
					.query(
							"from ProcardTemplate where markId=? and  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') "
									+ sqladd, procardTem.getMarkId());
		} else if (bool & updatelv == 1) {// 一级同步修改同父零件下的同件号
			if (fMarkId != null && fMarkId.length() > 0) {
				sameMarkIdList = totalDao
						.query(
								"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
										+ sqladd
										+ "  and procardTemplate.markId=?",
								procardTem.getMarkId(), fMarkId);
			}
		}
		if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
			for (ProcardTemplate sameCard : sameMarkIdList) {
				if ("待定".equals(sameCard.getProcardStyle())
						&& !"总成".equals(procardTem.getProcardStyle())
						&& sameCard.getBelongLayer() != null
						&& sameCard.getBelongLayer() != 1) {
					sameCard.setProcardStyle(procardTem.getProcardStyle());
				}
				sameCard.setProName(procardTem.getProName());
				sameCard.setUnit(procardTem.getUnit());
				sameCard.setCarStyle(procardTem.getCarStyle());
				sameCard.setSafeCount(procardTem.getSafeCount());
				sameCard.setLastCount(procardTem.getLastCount());
				sameCard.setBanBenNumber(procardTem.getBanBenNumber());
				sameCard.setTuhao(procardTem.getTuhao());
				sameCard.setTrademark(procardTem.getTrademark());// 牌号(件号)
				sameCard.setSpecification(procardTem.getSpecification());// 规格
				sameCard.setYtuhao(procardTem.getYtuhao());// 原材料图号
				sameCard.setYuanName(procardTem.getYuanName());// 原材料名称
				sameCard.setBiaochu(procardTem.getBiaochu());
				sameCard.setLuhao(procardTem.getLuhao());// 炉号
				sameCard.setLoadMarkId(procardTem.getLoadMarkId());
				sameCard.setImportance(procardTem.getImportance());
				if (fMarkId != null
						&& fMarkId.length() > 0
						&& sameCard.getProcardTemplate() != null
						&& sameCard.getProcardTemplate().getMarkId() != null
						&& fMarkId.equals(sameCard.getProcardTemplate()
								.getMarkId())) {
					if (sameCard.getProcardStyle().equals("外购")) {
						// 上层同件号修改权值
						sameCard.setQuanzi1(procardTem.getQuanzi1());
						sameCard.setQuanzi2(procardTem.getQuanzi2());
					} else {
						sameCard.setCorrCount(procardTem.getCorrCount());
					}
					sameCard.setLingliaostatus(procardTem.getLingliaostatus());
				}
				// 当自制件的展开尺寸发生变化 自动修改下层外购件的权值。
				if ("自制".equals(sameCard.getProcardStyle())
						&& procardTem.getThisWidth() != null
						&& procardTem.getThisLength() != null
						&& procardTem.getThisHight() != null
						&& (!procardTem.getThisHight().equals(
								sameCard.getThisHight())
								|| !procardTem.getThisWidth().equals(
										sameCard.getThisWidth()) || !procardTem
								.getThisLength().equals(
										sameCard.getThisLength()))) {
					Set<ProcardTemplate> sonpt = sameCard.getProcardTSet();
					for (ProcardTemplate p : sonpt) {
						// 计算板材外购件的权值2
						String hql_wgj = " from YuanclAndWaigj where markId = ?  ";
						YuanclAndWaigj wgj = null;
						if (sameCard.getClType() != null
								&& sameCard.getClType().length() > 0) {
							hql_wgj += " and caizhi = '"
									+ sameCard.getClType()
									+ "' and density is not null and density >0";
							wgj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql_wgj, p
											.getMarkId());
							if (wgj != null) {
								Float quanzhi2 = (procardTem.getThisLength() + 12)
										* (procardTem.getThisWidth() + 12)
										* (procardTem.getThisHight())
										* wgj.getDensity() / 1000000;
								p.setQuanzi2(quanzhi2);
								totalDao.update(p);
							}
						} else if (sameCard.getBiaochu() != null
								&& sameCard.getBiaochu().length() > 0) {
							String hql_fuhe = " from PanelSize where caizhi = ?";
							PanelSize fuhe = (PanelSize) totalDao
									.getObjectByCondition(hql_fuhe, sameCard
											.getBiaochu());
							if (fuhe != null) {
							} else {
								hql_wgj += " and name like '%"
										+ sameCard.getBiaochu()
										+ "%' and areakg is not null and areakg >0";
								wgj = (YuanclAndWaigj) totalDao
										.getObjectByCondition(hql_wgj, p
												.getMarkId());
								if (wgj != null) {
									Float quanzhi2 = (procardTem
											.getThisLength()
											* procardTem.getThisWidth() * 2)
											/ wgj.getAreakg() / 1000000;
									p.setQuanzi2(quanzhi2);
									totalDao.update(p);
								}
							}
						}

					}
				}

				sameCard.setBili(procardTem.getBili());
				sameCard.setClType(procardTem.getClType());
				sameCard.setCaizhi(procardTem.getCaizhi());
				// sameCard.setLingliaostatus(procardTem.getLingliaostatus());
				sameCard.setLingliaoType(procardTem.getLingliaoType());
				sameCard.setStatus(procardTem.getStatus());
				sameCard.setNumb(procardTem.getNumb());
				sameCard.setPageTotal(procardTem.getPageTotal());
				sameCard.setFachuDate(procardTem.getFachuDate());
				sameCard.setBianzhiName(procardTem.getBianzhiName());// 编制人
				sameCard.setBianzhiId(procardTem.getBianzhiId());// 编制ID
				sameCard.setBianzhiDate(procardTem.getBianzhiDate());// 编制时间
				sameCard.setJiaoduiName(procardTem.getJiaoduiName());// 校对人
				sameCard.setJiaoduiId(procardTem.getJiaoduiId());// 校对ID
				sameCard.setJiaoduiDate(procardTem.getJiaoduiDate());// 校对时间
				sameCard.setShenheName(procardTem.getShenheName());// 审核人
				sameCard.setShenheId(procardTem.getShenheId());// 审核ID
				sameCard.setShenheDate(procardTem.getShenheDate());// 审核时间
				sameCard.setPizhunName(procardTem.getPizhunName());// 批准人
				sameCard.setPizhunId(procardTem.getPizhunId());// 批准ID
				sameCard.setPizhunDate(procardTem.getPizhunDate());// 批准时间
				sameCard.setNeedProcess(procardTem.getNeedProcess());// 半成品
				sameCard.setZzjihuo(procardTem.getZzjihuo());// 自制件激活
				bool = totalDao.update(sameCard);
				// AttendanceTowServerImpl.updateJilu(3,sameCard,
				// sameCard.getId(), sameCard.getMarkId()+"(同步修改)");
			}
		}
		if (bool && bzbool) {
			AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", "件号"
					+ procardTem.getMarkId() + "需要您前往编制,请及时处理,谢谢!",
					new Integer[] { procardTem.getBianzhiId() },
					"procardTemplateGyAction_findSonsForBz.action?id="
							+ procardTem.getRootId(), true);
		}
		return bool + "";
	}

	public Object[] findAllProcardTemplateReplace(
			ProcardTemplateReplace procardTemplateReplace, int pageNo,
			int pageSize) {
		if (procardTemplateReplace == null) {
			procardTemplateReplace = new ProcardTemplateReplace();
		}
		String hql = totalDao.criteriaQueries(procardTemplateReplace, null);
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public Object[] findAllProcardTemplatePrivilege(
			ProcardTemplatePrivilege procardTemplatePrivilege, int pageNo,
			int pageSize) {
		if (procardTemplatePrivilege == null) {
			procardTemplatePrivilege = new ProcardTemplatePrivilege();
		}
		String hql = totalDao.criteriaQueries(procardTemplatePrivilege, null);
		hql += "order by id DESC";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/**
	 * 判断件号是否在试制总成ProcardTemplate中存在
	 * 
	 * @param markId
	 *            (件号、业务件号)
	 * @return
	 */
	public String isTruebyMarkId(String markId) {
		if (markId != null) {
			String pt_sql = "from ProcardTemplate where (markId = ? or ywMarkId = ?) and (dataStatus is null or dataStatus !='删除') and rootId =id and productStyle ='试制'";
			ProcardTemplate pt = (ProcardTemplate) totalDao
					.getObjectByCondition(pt_sql, markId, markId);
			if (pt != null) {
				return "存在";
			} else {
				return "不存在";
			}
		} else {
			return "异常";
		}
	}

	public String addPTPrivilege(
			ProcardTemplatePrivilege procardTemplatePrivilege) {
		if (procardTemplatePrivilege != null) {
			if ("存在"
					.equals(isTruebyMarkId(procardTemplatePrivilege.getMarkId()))) {
				ProcardTemplatePrivilege pTPrivilege_old = (ProcardTemplatePrivilege) totalDao
						.getObjectByCondition(
								"from ProcardTemplatePrivilege where markId=?",
								procardTemplatePrivilege.getMarkId());
				if (pTPrivilege_old == null) {
					if (totalDao.save(procardTemplatePrivilege)) {
						return "添加成功";
					} else {
						return "添加失败";
					}
				} else {
					return "该件号已经存在该表....";
				}
			} else {
				return "该件号不为试制总成...";
			}

		} else {
			return "数据异常";
		}
	}

	public String updatePTPrivilege(
			ProcardTemplatePrivilege procardTemplatePrivilege) {
		if (procardTemplatePrivilege != null) {
			if ("存在"
					.equals(isTruebyMarkId(procardTemplatePrivilege.getMarkId()))) {
				ProcardTemplatePrivilege pTPrivilege_old = (ProcardTemplatePrivilege) totalDao
						.getObjectByCondition(
								"from ProcardTemplatePrivilege where markId=?",
								procardTemplatePrivilege.getMarkId());
				if (pTPrivilege_old == null) {
					if (totalDao.update(procardTemplatePrivilege)) {
						return "添加成功";
					} else {
						return "添加失败";
					}
				} else {
					return "该件号已经存在该表....";
				}
			} else {
				return "该件号不为试制总成...";
			}
		} else {
			return "数据异常";
		}
	}

	public String delPTPrivilege(Integer id) {
		if (id != null) {
			ProcardTemplatePrivilege pTPrivilege = (ProcardTemplatePrivilege) totalDao
					.get(ProcardTemplatePrivilege.class, id);
			if (pTPrivilege != null) {
				if (totalDao.delete(pTPrivilege)) {
					return "删除成功";
				} else {
					return "删除失败";
				}
			} else {
				return "该条数据不存在无法删除";
			}
		} else {
			return "数据异常";
		}
	}

	public ProcardTemplatePrivilege toupdatePTPrivilege(Integer id) {
		if (id != null) {
			return (ProcardTemplatePrivilege) totalDao.get(
					ProcardTemplatePrivilege.class, id);

		} else {
			return null;
		}
	}

	/***
	 * 查询所有总成流水卡片模板(分页)
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public Object[] findAllProcardTemplate(ProcardTemplate procardTemplate,
			int pageNo, int pageSize, String pageStatus) {
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
			procardTemplate.setBelongLayer(1);// 只查询第一层的总成模板
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null,
				"productStyle");
		if ("sop".equals(pageStatus)) {
			hql += " and productStyle = '试制'";
		} else if ("price".equals(pageStatus)) {
			if (procardTemplate.getProductStyle() != null
					&& procardTemplate.getProductStyle().length() > 0)
				hql += " and productStyle = '"
						+ procardTemplate.getProductStyle() + "'";
		} else {
			hql += " and productStyle = '批产'";
		}
		hql += " and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		// for (int i = 0; i < list.size(); i++) {
		// ProcardTemplate pt = (ProcardTemplate) list.get(i);
		// ProcardTemplate totalCard = (ProcardTemplate) totalDao
		// .getObjectByCondition("from ProcardTemplate where id=?", pt
		// .getRootId());
		// if (totalCard != null) {
		// pt.setRootMarkId(totalCard.getMarkId());
		// pt.setYwMarkId(totalCard.getYwMarkId());
		// }
		// }
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public void exportBom(ProcardTemplate procardTemplate) {
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
			procardTemplate.setBelongLayer(1);// 只查询第一层的总成模板
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null);
		hql += " and (banbenStatus is null or banbenStatus='默认')";
		List<ProcardTemplate> pList = totalDao.find(hql);
		try {
			// set response
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("bom导出".getBytes("GB2312"), "8859_1")
					+ ".xlsx");
			response.setContentType("application/msexcel");
			// set response end

			// start format excel
			// 创建工作簿
			// 用SXSSFWorkbook 大数据Write
			SXSSFWorkbook workBook = new SXSSFWorkbook(50000);
			// XSSFWorkbook workBook = new XSSFWorkbook();
			// 创建工作表
			// XSSFSheet sheet = workBook.createSheet("工序汇总信息");
			org.apache.poi.ss.usermodel.Sheet sheet = workBook
					.createSheet("bom导出");
			// 创建行
			// XSSFRow row = sheet.createRow(2);
			Row row = sheet.createRow(2);
			CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 1, 10);
			// 创建样式
			CellStyle style = workBook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			Font font = workBook.createFont();
			font.setFontHeightInPoints((short) 16);
			font.setBold(true);
			style.setFont(font);
			sheet.addMergedRegion(rangeAddress);
			row = sheet.createRow(0);
			// XSSFCell cell = row.createCell(1);

			org.apache.poi.ss.usermodel.Cell cell = row.createCell(1);
			cell.setCellValue("bom导出");
			cell.setCellStyle(style);
			row = sheet.createRow(1);
			cell = row.createCell(0,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("件号");
			cell = row.createCell(1,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("图号");
			cell = row.createCell(2,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("名称");
			cell = row.createCell(3,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("卡片类型");
			cell = row.createCell(4,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("产品类型");
			cell = row.createCell(5,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("总成件号");
			cell = row.createCell(6,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("业务件号");
			cell = row.createCell(7,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("编制状态");
			cell = row.createCell(8,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("长");
			cell = row.createCell(9,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("宽");
			cell = row.createCell(10,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("高");
			cell = row.createCell(11,
					org.apache.poi.ss.usermodel.CellType.STRING);
			cell.setCellValue("用量");
			int count = -1;
			for (int i = 0; i < pList.size(); i++) {
				ProcardTemplate p = (ProcardTemplate) pList.get(i);
				count++;
				row = sheet.createRow(count + 2);
				cell = row.createCell(0, CellType.STRING);
				cell.setCellValue(p.getMarkId());
				cell = row.createCell(1, CellType.STRING);
				cell.setCellValue(p.getTuhao());
				cell = row.createCell(2, CellType.STRING);
				cell.setCellValue(p.getProName());
				cell = row.createCell(3, CellType.STRING);
				cell.setCellValue(p.getProcardStyle());
				cell = row.createCell(4, CellType.STRING);
				cell.setCellValue(p.getProductStyle());
				cell = row.createCell(5, CellType.STRING);
				cell.setCellValue(p.getRootMarkId());
				cell = row.createCell(6, CellType.STRING);
				cell.setCellValue(p.getYwMarkId());
				cell = row.createCell(7, CellType.STRING);
				cell.setCellValue(p.getBzStatus());
				Float corrCountDao = gainDanTai(p, 1f);
				if ("自制".equals(p.getProcardStyle())) {
					cell = row.createCell(8, CellType.STRING);
					if (p.getThisLength() != null
							&& !"".equals(p.getThisLength())) {
						cell.setCellValue(p.getThisLength());
					} else {
						cell.setCellValue("无");
					}
					cell = row.createCell(9, CellType.STRING);
					if (p.getThisWidth() != null
							&& !"".equals(p.getThisWidth())) {
						cell.setCellValue(p.getThisWidth());
					} else {
						cell.setCellValue("无");
					}
					cell = row.createCell(10, CellType.STRING);
					if (p.getThisHight() != null
							&& !"".equals(p.getThisHight())) {
						cell.setCellValue(p.getThisHight());
					} else {
						cell.setCellValue("无");
					}
				} else {
					cell = row.createCell(8, CellType.STRING);
					cell.setCellValue("-");
					cell = row.createCell(9, CellType.STRING);
					cell.setCellValue("-");
					cell = row.createCell(10, CellType.STRING);
					cell.setCellValue("-");
				}
				cell = row.createCell(11, CellType.STRING);
				cell.setCellValue(corrCountDao);

			}
			workBook.write(os);
			workBook.close();// 记得关闭工作簿
		} catch (IOException e) {
			e.printStackTrace();
		}
		// try {
		// HttpServletResponse response = (HttpServletResponse) ActionContext
		// .getContext().get(ServletActionContext.HTTP_RESPONSE);
		// OutputStream os = response.getOutputStream();
		// response.reset();
		// response.setHeader("Content-disposition", "attachment; filename="
		// + new String("Bom零件导出表".getBytes("GB2312"), "8859_1")
		// + ".xls");
		// response.setContentType("application/msexcel");
		// WritableWorkbook wwb = Workbook.createWorkbook(os);
		// WritableSheet ws = wwb.createSheet("Bom零件导出表", 0);
		// ws.addCell(new Label(0, 0, "件号"));
		// ws.addCell(new Label(1, 0, "图号"));
		// ws.addCell(new Label(2, 0, "名称"));
		// ws.addCell(new Label(3, 0, "卡片类型"));
		// ws.addCell(new Label(4, 0, "产品类型 "));
		// ws.addCell(new Label(5, 0, "总成件号"));
		// ws.addCell(new Label(6, 0, "业务件号"));
		// ws.addCell(new Label(7, 0, "编制状态"));
		// ws.addCell(new Label(8, 0, "长"));
		// ws.addCell(new Label(9, 0, "宽"));
		// ws.addCell(new Label(10, 0, "高"));
		// for (int i = 0; i < pList.size(); i++) {
		// ProcardTemplate p = (ProcardTemplate) pList.get(i);
		// ws.addCell(new Label(0, i + 1, p.getMarkId()));
		// ws.addCell(new Label(1, i + 1, p.getTuhao()));
		// ws.addCell(new Label(2, i + 1, p.getProName()));
		// ws.addCell(new Label(3, i + 1, p.getProcardStyle()));
		// ws.addCell(new Label(4, i + 1, p.getProductStyle()));
		// ws.addCell(new Label(5, i + 1, p.getRootMarkId()));
		// ws.addCell(new Label(6, i + 1, p.getYwMarkId()));
		// ws.addCell(new Label(7, i + 1, p.getBzStatus()));
		// if("自制".equals(p.getProcardStyle())){
		//					
		// }
		// }
		// wwb.write();
		// wwb.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (WriteException e) {
		// e.printStackTrace();
		// }

	}

	/***
	 * 根据首层父类id查询流水卡片模板(组装树形结构)
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public List findProcardTemByRootId(int rootId) {
		if ((Object) rootId != null && rootId > 0) {
			String hql = "from ProcardTemplate where rootId=?  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')  order by xuhao";
			List<ProcardTemplate> ptList = totalDao.query(hql, rootId);
			if (ptList != null && ptList.size() > 0) {
				for (ProcardTemplate pt : ptList) {
					if (pt.getProcardStyle().equals("总成")) {
						Float tqCount = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
												+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) and p2.id=?",
										pt.getRootId());
						if (tqCount != null && tqCount > 0) {
							pt.setTq("yes");
						}
					}
				}
			}
			return ptList;

		}
		return null;
	}

	public String findProcardTemByRootId1(int rootId) {
		int count = 1;
		DataGrid dg = new DataGrid();
		int length = 1;
		// 查询所有零组件信息
		String hql = "from ProcardTemplate where rootId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by xuhao";
		List<ProcardTemplate> proTemList = totalDao.query(hql, rootId);
		length += proTemList.size();
		for (ProcardTemplate proTem : proTemList) {

			// 组装零件信息
			VOProductTree part = new VOProductTree(count++, proTem.getMarkId(),
					proTem.getProName(), proTem.getProcardStyle(), proTem
							.getCaizhi(), proTem.getYuanName(), proTem
							.getNumber(), proTem.getMaxCount(), proTem
							.getTrademark(), proTem.getLuhao(), proTem
							.getSpecification(), proTem.getCorrCount(), proTem
							.getUnit(), proTem.getTuhao(), proTem.getBiaochu(),
					proTem.getClType(), proTem.getBanBenNumber(), proTem
							.getLoadMarkId(), proTem.getKgliao());
			// 获得零件对应工序信息
			Set<ProcessTemplate> processTemSet = proTem.getProcessTemplate();
			if (processTemSet != null && processTemSet.size() > 0) {
				length += processTemSet.size();
				for (ProcessTemplate processTem : processTemSet) {
					// 数据有效性效验

					VOProductTree process = null;

					process = new VOProductTree(count++, processTem
							.getProcessName(), processTem.getAllJiepai(),
							processTem.getProcessStatus(), processTem
									.getProductStyle(), processTem
									.getProcessNO());
					part.getChildren().add(process);
				}
			}
			dg.getRows().add(part);
		}
		dg.setTotal(new Long(length));
		String jsonStr = JSON.toJSON(dg).toString();
		return jsonStr;

	}

	/***
	 * 添加工序信息
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public String addProcessTemplate(ProcessTemplate processTemplate, Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplate thispt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (thispt.getProductStyle().equals("批产")) {
			if (thispt.getBzStatus() != null
					&& thispt.getBzStatus().equals("已批准")) {
				return "已批准状态不能添加工序!";
			}
		}
		if (thispt.getBzStatus() != null
				&& thispt.getBzStatus().equals("设变发起中")) {
			return "设变申请中不能添加工序!";
		}
		String addTime = Util.getDateTime();
		if (processTemplate != null) {
			ProcessGzstore pg = (ProcessGzstore) totalDao.getObjectByCondition(
					" from ProcessGzstore where processName = ?",
					processTemplate.getProcessName());
			if (pg == null) {
				return "对不起,此工序不在工序库中存在!";
			}
			if (pg.getIsSpecial() != null && pg.getIsSpecial().equals("特殊")) {
				processTemplate.setIsSpecial("特殊");
			} else {
				processTemplate.setIsSpecial("普通");
			}
			Float old = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplate where processNO=? and processName=? and procardTemplate.id=?",
							processTemplate.getProcessNO(), processTemplate
									.getProcessName(), id);
			if (old != null && old > 0) {// 同工序号同工序名称
				return "此工序已存在,请勿重复添加!";
			}

			Float hasCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplate where processNO=? and procardTemplate.id=?",
							processTemplate.getProcessNO(), id);
			if (hasCount != null && hasCount > 0) {// 顺延之后工序
				List<ProcessTemplate> laterprocesslist = totalDao
						.query(
								"from ProcessTemplate where processNO>=? and procardTemplate.markId=(select markId from ProcardTemplate where id=?) and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus !='历史')"
										+ "and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus !='历史') order by processNO",
								processTemplate.getProcessNO(), id);
				Integer nowNO = processTemplate.getProcessNO();
				List<ProcessTemplate> syprocesslist = new ArrayList<ProcessTemplate>();
				for (ProcessTemplate laterprocess : laterprocesslist) {
					if (laterprocess.getProcessNO().equals(nowNO)) {
						syprocesslist.add(laterprocess);
					} else if (laterprocess.getProcessNO().equals(nowNO + 1)) {
						syprocesslist.add(laterprocess);
						nowNO++;
					} else {
						break;
					}
				}
				nowNO++;
				for (int i = (syprocesslist.size() - 1); i >= 0; i--) {
					if (i == (syprocesslist.size() - 1)) {// 第一次进来查询是否有遗留图纸，有则改为历史
						List<ProcessTemplateFile> fileList = totalDao
								.query(
										"from ProcessTemplateFile where (status is null or status!='历史' ) "
												+ "and processNO is not null and processNO=? and markId=(select markId from ProcardTemplate where id=?)",
										nowNO, id);
						if (fileList != null && fileList.size() > 0) {
							for (ProcessTemplateFile file : fileList) {
								file.setStatus("历史");
								totalDao.update(file);
							}
						}
					}
					ProcessTemplate syprocess = syprocesslist.get(i);
					if (nowNO > syprocess.getProcessNO()) {
						nowNO--;
						List<ProcessTemplateFile> fileList = totalDao
								.query(
										"from ProcessTemplateFile where (status is null or status!='历史' ) "
												+ "and processNO is not null and processNO=? and markId=(select markId from ProcardTemplate where id=?)",
										syprocess.getProcessNO(), id);
						if (fileList != null && fileList.size() > 0) {
							for (ProcessTemplateFile file : fileList) {
								file.setProcessNO(file.getProcessNO() + 1);
								file.setProcessName(syprocess.getProcessName());
								totalDao.update(file);
							}
						}
					}
					syprocess.setProcessNO(syprocess.getProcessNO() + 1);
					totalDao.update(syprocess);
				}
			}
			String banbenSql = null;
			String banben = (String) totalDao.getObjectByCondition(
					"select banBenNumber from ProcardTemplate where id=?", id);
			if (banben == null || banben.length() == 0) {
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			} else {
				banbenSql = " and banBenNumber='" + banben + "'";

			}
			boolean b = true;
			// 获取所有与要添加工序的零件同件号的零件
			List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao
					.query(
							"from ProcardTemplate where  markId=? and id!=? and  productStyle=?"
									+ "and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"
									+ banbenSql, thispt.getMarkId(), thispt
									.getId(), thispt.getProductStyle());
			ptList.add(thispt);
			List<ProcessTemplate> processList = new ArrayList<ProcessTemplate>();
			int tag = 0;
			for (int i = 0; i < ptList.size(); i++) {
				ProcardTemplate pt = ptList.get(i);
				Float ajp = (Float) totalDao
						.getObjectByCondition(
								"select sum(allJiepai) from ProcessTemplate where procardTemplate.id=?",
								pt.getId());
				if (ajp == null) {
					pt.setAllJiepai(processTemplate.getAllJiepai());
				} else {
					pt.setAllJiepai(processTemplate.getAllJiepai() + ajp);
				}
				totalDao.update(pt);
				ProcessTemplate process = new ProcessTemplate();
				BeanUtils.copyProperties(processTemplate, process,
						new String[] { "id", "procardTemplate", "taSopGongwei",
								"processFuLiaoTemplate", "fuliaoList",
								"processTemplateFile" });
				process.setAddTime(addTime);
				process.setAddUser(user.getName());
				process.setProcardTemplate(pt);
				processList.add(process);
				String bancisql = null;
				if (pt.getBanci() == null || pt.getBanci() == 0) {
					bancisql = " and (banci is null or banci=0)";
				} else {
					bancisql = " and banci = " + pt.getBanci();
				}
				if (tag == 0 && pt.getProductStyle().equals("批产")) {// 新添加的工序添加共用工艺文件
					List<ProcessTemplateFile> files = null;
					Float hasgxCunt = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcessTemplate where procardTemplate.markId=? and (dataStatus is null and dataStatus !='删除') and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus !='历史') and (procardTemplate.dataStatus is null or procardTemplate.dataStatus!='删除')",
									pt.getMarkId());
					if (hasgxCunt == null || hasgxCunt == 0) {// 无工序
						// 添加图纸
						files = totalDao
								.query(
										"from ProcessTemplateFile where markId=? and processNO is null and type in('3D文件','工艺规范') "
												+ bancisql, pt.getMarkId());
					} else {
						files = totalDao
								.query(
										"from ProcessTemplateFile where markId=? "
												+ bancisql
												+ " and processNO is null and type in('3D文件','工艺规范') and (status is null or status!='历史') and oldfileName in"
												+ "(select oldfileName from ProcessTemplateFile where markId=? "
												+ bancisql
												+ " group by oldfileName having count(*)>1)",
										pt.getMarkId(), pt.getMarkId());
					}
					if (files != null && files.size() > 0) {
						List<String> oldFilenames = new ArrayList<String>();
						for (ProcessTemplateFile file : files) {
							if (!oldFilenames.contains(file.getOldfileName())) {
								ProcessTemplateFile newFile = new ProcessTemplateFile();
								BeanUtils.copyProperties(file, newFile,
										new String[] { "id", "processNO",
												"processName" });
								newFile.setProcessNO(processTemplate
										.getProcessNO());
								newFile.setProcessName(processTemplate
										.getProcessName());
								newFile.setBanBenNumber(pt.getBanBenNumber());
								newFile.setBanci(pt.getBanci());
								totalDao.save(newFile);
								oldFilenames.add(file.getOldfileName());
							}
						}
					}
					tag++;
				}
			}
			int n = 0;
			for (ProcessTemplate process : processList) {
				try {
					// 辅料
					ProcessGzstore processGzstore = (ProcessGzstore) totalDao
							.getObjectByCondition(
									"from ProcessGzstore where processName=?",
									process.getProcessName());
					if (processGzstore.getIsNeedFuliao() != null
							&& processGzstore.getIsNeedFuliao().equals("yes")) {
						Set<ProcessGzstoreFuLiao> fuliaoSet1 = processGzstore
								.getProcessGzstoreFuLiaos();
						Set<ProcessFuLiaoTemplate> fuliaoSet2 = new HashSet<ProcessFuLiaoTemplate>();
						if (fuliaoSet1 != null && fuliaoSet1.size() > 0) {
							for (ProcessGzstoreFuLiao fuliao1 : fuliaoSet1) {
								ProcessFuLiaoTemplate fuliao2 = new ProcessFuLiaoTemplate(
										fuliao1);
								fuliao2.setProcessTemplate(process);
								fuliaoSet2.add(fuliao2);
							}
						}
						process.setProcessFuLiaoTemplate(fuliaoSet2);
						process.setIsNeedFuliao("yes");
					} else {
						process.setIsNeedFuliao("no");
					}
				} catch (Exception e) {
					// TODO: handle exception
					return "系统异常";
				}
				b = b & totalDao.save(process);
				ProcardTemplate pt = process.getProcardTemplate();
				if (pt.getProductStyle().equals("试制")) {// 新添加的工序添加共用工艺文件
					List<ProcessTemplateFile> files = null;
					String banciSql = "";
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						banciSql = " and (banci is null or banci=0)";
					} else {
						banciSql = " and banci=" + pt.getBanci();
					}
					// 添加图纸
					files = totalDao
							.query(
									"from ProcessTemplateFile where glId=? and processNO is null  and type in('3D文件','工艺规范')  "
											+ banciSql, pt.getId());
					if (files != null && files.size() > 0) {
						List<String> oldFilenames = new ArrayList<String>();
						for (ProcessTemplateFile file : files) {
							if (!oldFilenames.contains(file.getOldfileName())) {
								ProcessTemplateFile newFile = new ProcessTemplateFile();
								BeanUtils.copyProperties(file, newFile,
										new String[] { "id", "processNO",
												"processName" });
								newFile.setProcessNO(processTemplate
										.getProcessNO());
								newFile.setProcessName(processTemplate
										.getProcessName());
								newFile.setBanBenNumber(pt.getBanBenNumber());
								newFile.setBanci(pt.getBanci());
								newFile.setProductStyle("试制");
								newFile.setGlId(process.getId());
								totalDao.save(newFile);
								oldFilenames.add(file.getOldfileName());
							}
						}
					}
				}
				if (b) {
					if (n == 0) {
						// 添加修改日志
						ProcardTemplateChangeLog.addchangeLog(totalDao, process
								.getProcardTemplate(), process, "工序", "增加",
								user, addTime);
					}
					if ("yes".equals(process.getProcessStatus())
							&& process.getParallelId() == null) {
						process.setParallelId(process.getId());
					}
				}
				n++;
			}
			return b + "";
		}
		return "没有找到对应的模板卡片";
	}

	/***
	 * 通过流水卡片id(外键)查询对应工序信息
	 * 
	 * @param fkId
	 * @return
	 */
	@Override
	public List findProcessByFkId(Integer fkId) {
		if (fkId != null && fkId > 0) {
			String hql = "from ProcessTemplate pc where pc.procardTemplate.id=? and (pc.dataStatus is null or pc.dataStatus !='删除')  order by pc.processNO";
			return totalDao.query(hql, fkId);
		}
		return null;
	}

	/***
	 * 查询流水卡片模板(页面显示流水卡片模板详细使用)
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Object[] findCardTemForShow(int id) {
		if ((Object) id != null && id > 0) {
			ProcardTemplate pc = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
			if (pc != null) {
				// 下层流水卡片模板
				Set<ProcardTemplate> pcSet = pc.getProcardTSet();
				List<ProcardTemplate> pclist = new ArrayList<ProcardTemplate>();
				pclist.addAll(pcSet);
				// 对应工序信息
				Set<ProcessTemplate> pceSet = pc.getProcessTemplate();
				List<ProcessTemplate> pcelist = new ArrayList<ProcessTemplate>();
				pcelist.addAll(pceSet);
				String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
				String valueCode = (String) totalDao.getObjectByCondition(hql1);
				Integer banci = pc.getBanci();
				if (banci == null) {
					banci = 0;
				}
				String sbmsg = null;
				String ptbbaStatus = "in( '关联并通知生产','生产后续','上传佐证','关闭')";
				if (pc.getBzStatus() == null || !pc.getBzStatus().equals("已批准")) {
					ptbbaStatus = " not in ('取消','删除','设变发起','分发项目组','项目主管初评','项目工程师评审','关联并通知生产','生产后续','上传佐证','关闭')";
				}
				if (banci > 1) {
					banci--;
					sbmsg = (String) totalDao
							.getObjectByCondition(
									"select remark from ProcardTemplateBanBen where banci=? and  procardTemplateBanBenApply.processStatus "
											+ ptbbaStatus
											+ "  and markId=? order by id desc",
									banci, pc.getMarkId());
				} else if (banci == 1) {
					sbmsg = (String) totalDao
							.getObjectByCondition(
									"select remark from ProcardTemplateBanBen where (banci is null or banci=0) and  procardTemplateBanBenApply.processStatus "
											+ ptbbaStatus
											+ " and markId=? order by id desc",
									pc.getMarkId());
				}
				return new Object[] { pc, pclist, pcelist, valueCode, sbmsg };
			}
		}
		return null;
	}

	@Override
	public Object[] findPrintProcessList(ProcardTemplate procardTemplate,
			Integer cpage, Integer pageSize) {
		String hql = " from ProcessTemplate";
		if (null != procardTemplate) {
			hql = "from ProcessTemplate p where p.procardTemplate.markId='"
					+ procardTemplate.getMarkId() + "'";
		}
		Object[] processdAarr = new Object[2];
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		for (Object obj : list) {
			ProcardTemplate procardT = (ProcardTemplate) obj;
			procardT.setProcardTemplate(procardT.getProcardTemplate());
		}
		Integer count = totalDao.getCount(hql);
		processdAarr[0] = count;
		processdAarr[1] = list;
		return processdAarr;
	}

	@Override
	public List findSelectedList(Integer[] processId) {
		String sellidString = "";
		/*
		 * for (int i = 0,len=processId.length; i <len ; i++) { if(i==len-1){
		 * sellidString+=processId[i].toString(); }else
		 * sellidString+=processId[i].toString()+",";
		 * 
		 * }
		 */
		String hql = "from ProcessTemplate where id in(:test)";

		Query query = totalDao.createQuery(hql);
		query.setParameterList("test", processId);
		List list = query.list();
		return list;
	}

	/***
	 * 通过id查找工序详细
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public ProcessTemplate findProcessT(Integer id) {
		if (id != null && id > 0) {
			ProcessTemplate process = (ProcessTemplate) totalDao.getObjectById(
					ProcessTemplate.class, id);
			if (process != null && process.getProcessFuLiaoTemplate() != null
					&& process.getProcessFuLiaoTemplate().size() > 0) {
				for (ProcessFuLiaoTemplate fl : process
						.getProcessFuLiaoTemplate()) {
					fl.getId();
				}
			}
			return process;
		}
		return null;
	}

	/***
	 * 更新工序模板信息
	 * 
	 * @param processT
	 * @return
	 */
	@Override
	public String updateProcessT(ProcessTemplate processT) {
		Users user = Util.getLoginUser();
		String nowtime = Util.getDateTime();
		if (user == null) {
			return "请先登录!";
		}
		if (processT != null) {
			ProcessTemplate old = (ProcessTemplate) totalDao
					.getObjectByCondition(
							"from ProcessTemplate where id !=? and processNO=? and (dataStatus is null or dataStatus!='删除') and procardTemplate.id=(select procardTemplate.id from ProcessTemplate where id=?)",
							processT.getId(), processT.getProcessNO(), processT
									.getId());
			if (old != null) {
				return "该工序号已存在，请重新填写!";
			}
			ProcessTemplate oldProcessT = (ProcessTemplate) totalDao
			.getObjectById(ProcessTemplate.class, processT.getId());
			ProcardTemplate procard = oldProcessT.getProcardTemplate();
			 if(procard.getProductStyle().equals("批产")){
			 if (procard.getBzStatus() != null
			 && procard.getBzStatus().equals("已批准")) {
			 return "已批准状态不能修改工序!";
			 }
			 }
			if (processT.getOpcaozuojiepai() == null) {
				processT.setOpcaozuojiepai(3f);
			}
			if (processT.getOpshebeijiepai() == null) {
				processT.setOpshebeijiepai(1f);
			}
			if (processT.getGzzhunbeijiepai() == null) {
				processT.setGzzhunbeijiepai(1f);
			}
			if (processT.getGzzhunbeicishu() == null) {
				processT.setGzzhunbeicishu(1f);
			}
			processT.setAllJiepai(processT.getOpcaozuojiepai()
					+ processT.getOpshebeijiepai()
					+ processT.getGzzhunbeijiepai()
					* processT.getGzzhunbeicishu());
			boolean b = true;
			ProcardTemplateChangeLog.addchangeLog(totalDao, oldProcessT,
					processT, user, nowtime);
			List<ProcessTemplate> porcessinOtherCardList = totalDao
					.query(
							"from ProcessTemplate where id !=? and processNO=? "
									+ "and procardTemplate.id in"
									+ "(select id from ProcardTemplate "
									+ "where markId=? and productStyle=? and (banbenStatus is null or banbenStatus ='默认'))",
							oldProcessT.getId(), oldProcessT.getProcessNO(),
							oldProcessT.getProcardTemplate().getMarkId(),
							oldProcessT.getProcardTemplate().getProductStyle());
			if (oldProcessT != null) {
				// 对应模板
				processT.setProcardTemplate(oldProcessT.getProcardTemplate());
				// 并行处理
				String newProcessStatus = processT.getProcessStatus();
				String oldProcessStatus = oldProcessT.getProcessStatus();
				// 判断并行状态是否存在变化
				if ("yes".equals(newProcessStatus)) {
					// 查询上一道工序
					String hql = "from ProcessTemplate pt where pt.procardTemplate.id=? and processNO<? order by processNO desc";
					ProcessTemplate pt = (ProcessTemplate) totalDao
							.getObjectByCondition(hql, oldProcessT
									.getProcardTemplate().getId(), oldProcessT
									.getProcessNO());
					if (pt != null) {
						// 上一道工序已经并行,延续上一道工序的并行id
						if (newProcessStatus.equals(pt.getProcessStatus())) {
							processT.setParallelId(pt.getParallelId());
						} else {
							processT.setParallelId(oldProcessT.getId());
						}
					} else {
						// 说明是第一道工序(并行从自身id开始)
						processT.setParallelId(oldProcessT.getId());
					}
				} else {
					processT.setParallelId(null);
				}
				// ------------辅料--------------------------

				if (processT.getIsNeedFuliao() != null
						&& processT.getIsNeedFuliao().equals("yes")) {// 需要辅料
					if (processT.getFuliaoList() != null
							&& processT.getFuliaoList().size() > 0) {
						oldProcessT.setIsNeedFuliao("yes");
						// 原有的辅料
						Set<ProcessFuLiaoTemplate> oldset = oldProcessT
								.getProcessFuLiaoTemplate();
						oldProcessT.setProcessFuLiaoTemplate(null);
						if (oldset == null || oldset.size() == 0) {// 原来没有辅料
							oldProcessT
									.setProcessFuLiaoTemplate(new HashSet<ProcessFuLiaoTemplate>(
											processT.getFuliaoList()));
						} else {// 原来有辅料
							// 存储新需要的辅料中的id
							List<Integer> newidList = new ArrayList<Integer>();
							Set<ProcessFuLiaoTemplate> newSet = new HashSet<ProcessFuLiaoTemplate>();
							if (processT.getFuliaoList() != null
									&& processT.getFuliaoList().size() > 0) {
								for (ProcessFuLiaoTemplate fuliao : processT
										.getFuliaoList()) {// 将页面传过来的辅料的id存放起来
									if (fuliao != null
											&& fuliao.getId() != null) {
										newidList.add(fuliao.getId());
									}
								}
								for (ProcessFuLiaoTemplate newFuliao : processT
										.getFuliaoList()) {
									if (newFuliao != null
											&& newFuliao.getId() != null) {// 已存在的修改
										for (ProcessFuLiaoTemplate oldFuliao : oldset) {
											if (oldFuliao.getId().equals(
													newFuliao.getId())) {
												BeanUtils
														.copyProperties(
																newFuliao,
																oldFuliao,
																new String[] { "processGzstore" });
												newSet.add(oldFuliao);
											}
										}
									} else if (newFuliao != null
											&& newFuliao.getId() == null) {// 新添加的
										newSet.add(newFuliao);
									}
								}
								BeanUtils.copyProperties(processT, oldProcessT,
										new String[] { "procardTemplate",
												"taSopGongwei",
												"processFuLiaoTemplate",
												"addUser", "addTime", "epId",
												"isSpecial" });
								oldProcessT.setProcessFuLiaoTemplate(newSet);
								totalDao.update(oldProcessT);
								AttendanceTowServerImpl.updateJilu(5,
										oldProcessT, oldProcessT.getId(),
										oldProcessT.getProcessNO() + "工序"
												+ oldProcessT.getProcessName()
												+ "(涉及辅料)");
								for (ProcessFuLiaoTemplate oldFuliao : oldset) {
									if (!newidList.contains(oldFuliao.getId())) {// 删除原来有现在没有的辅料信息
										oldFuliao.setProcessTemplate(null);
										totalDao.delete(oldFuliao);
									}
								}
							}
						}
					} else {
						oldProcessT.setIsNeedFuliao("no");
					}
				} else {// 不需要辅料
					oldProcessT.setIsNeedFuliao("no");
					Set<ProcessFuLiaoTemplate> set = oldProcessT
							.getProcessFuLiaoTemplate();
					oldProcessT.setProcessFuLiaoTemplate(null);
					if (set != null) {
						for (ProcessFuLiaoTemplate processFuLiaoTemplate : set) {
							processFuLiaoTemplate.setProcessTemplate(null);
							totalDao.delete(processFuLiaoTemplate);
						}
					}
					BeanUtils.copyProperties(processT, oldProcessT,
							new String[] { "procardTemplate", "taSopGongwei",
									"processFuLiaoTemplate", "addUser",
									"addTime", "epId", "isSpecial" });
					oldProcessT.setProcessFuLiaoTemplate(null);
					b = b & totalDao.update(oldProcessT);
					AttendanceTowServerImpl.updateJilu(5, oldProcessT,
							oldProcessT.getId(), oldProcessT.getProcessNO()
									+ "工序" + oldProcessT.getProcessName());
				}
				// 同步修改其他件号下的该工序信息
				if (porcessinOtherCardList != null
						&& porcessinOtherCardList.size() > 0) {
					for (ProcessTemplate other : porcessinOtherCardList) {
						other.setProcessNO(processT.getProcessNO());
						other.setProcessName(processT.getProcessName());
						other.setProductStyle(processT.getProductStyle());
						other.setZjStatus(processT.getZjStatus());
						other.setProcessStatus(processT.getProcessStatus());
						other.setIsPrice(processT.getIsPrice());
						other.setKaoqingstatus(processT.getKaoqingstatus());
						other.setGuifanstatus(processT.getGuifanstatus());
						other.setIsJisuan(processT.getIsJisuan());
						other.setGongzhuangstatus(processT
								.getGongzhuangstatus());
						other.setGzstoreId(processT.getGzstoreId());
						other.setNumber(processT.getNumber());
						other.setMatetag(processT.getMatetag());
						other.setLiangjustatus(processT.getLiangjustatus());
						other.setMeasuringId(processT.getMeasuringId());
						other.setMeasuringNumber(processT.getMeasuringNumber());
						other.setMeasuringMatetag(processT
								.getMeasuringMatetag());
						other.setMeasuring_no(processT.getMeasuring_no());
						other.setShebeistatus(processT.getShebeistatus());
						other.setShebeiId(processT.getShebeiId());
						other.setShebeiNo(processT.getShebeiNo());
						other.setShebeiName(processT.getShebeiName());
						other.setGongwei(processT.getGongwei());
						totalDao.update(other);
						// AttendanceTowServerImpl.updateJilu(5,other,
						// other.getId(), other.getProcessNO()+ "工序" +
						// oldProcessT.getProcessName() + "(同步修改)");
					}
				}
				// 同步修改所有同件号的alljiepai
				List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao
						.query(
								"from ProcardTemplate where  markId=(select procardTemplate.markId from ProcessTemplate where id=?) and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
								processT.getId());
				for (int i = 0; i < ptList.size(); i++) {
					ProcardTemplate pt = ptList.get(i);
					Float ajp = (Float) totalDao
							.getObjectByCondition(
									"select sum(allJiepai) from ProcessTemplate where procardTemplate.id=?",
									pt.getId());
					if (ajp == null) {
						pt.setAllJiepai(null);
					} else {
						pt.setAllJiepai(ajp);
					}
					totalDao.update(pt);
				}
				return b + "";
			} else {
				return "不存在你要修改的工序信息！";
			}

		} else {
			return "数据异常!";
		}
	}

	/***
	 * 删除工序信息
	 */
	public String delProcessT(ProcessTemplate processT) {
		Users user = Util.getLoginUser();
		String nowtime = Util.getDateTime();
		ProcardTemplate procard = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where id=(select procardTemplate.id from ProcessTemplate where id=?)",
						processT.getId());
		 if(procard.getProductStyle().equals("批产")){
		 if (procard.getBzStatus() != null
		 && procard.getBzStatus().equals("已批准")) {
		 return "已批准状态不能删除工序!";
		 }
		 }
		// 同步修改所有同件号的alljiepai
		List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao
				.query(
						"from ProcardTemplate where  markId=(select procardTemplate.markId from ProcessTemplate where id=?) and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
						processT.getId());
		// 同步删除同零件号同工序号下绑定的外购件关系
		List<ProcessAndWgProcardTem> proAndwgPt = totalDao
				.query(
						" from ProcessAndWgProcardTem where procardMarkId =(select procardTemplate.markId from ProcessTemplate where id=? ) and processNo = ? and processName =?",
						processT.getId(), processT.getProcessNO(), processT
								.getProcessName());
		if (proAndwgPt != null && proAndwgPt.size() > 0) {
			for (ProcessAndWgProcardTem processAndWgProcardTem : proAndwgPt) {
				totalDao.delete(processAndWgProcardTem);
			}
		}
		// 查询所有于此工序的零件同件号的零件下的与此工序同工序号和工序名的工序
		String banbenSql = null;
		if (procard.getBanBenNumber() == null
				|| procard.getBanBenNumber().length() == 0) {
			banbenSql = " and (banBenNumber is null or banBenNumber='')";
		} else {
			banbenSql = " and banBenNumber='" + procard.getBanBenNumber() + "'";

		}
		List<ProcessTemplate> processList = totalDao
				.query(
						"from ProcessTemplate where processNO=? and processName=? and (dataStatus is null or dataStatus!='删除')"
								+ " and procardTemplate.id in (select id from ProcardTemplate where "
								+ "markId=? "
								+ banbenSql
								+ "and productStyle=? and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除'))",
						processT.getProcessNO(), processT.getProcessName(),
						procard.getMarkId(), procard.getProductStyle());
		int n = 0;
		for (ProcessTemplate process : processList) {
			if (n == 0) {
				ProcardTemplateChangeLog.addchangeLog(totalDao, process
						.getProcardTemplate(), process, "工序", "删除", user,
						nowtime);
			}
			process.setDataStatus("删除");
			process.setOldPtId(process.getProcardTemplate().getId());
			process.setOldbanci(process.getProcardTemplate().getBanci());
			process.setProcardTemplate(null);
			process.setDeleteTime(Util.getDateTime());
			totalDao.update(process);
			n++;
		}

		for (int i = 0; i < ptList.size(); i++) {
			ProcardTemplate pt = ptList.get(i);
			Float ajp = (Float) totalDao
					.getObjectByCondition(
							"select sum(allJiepai) from ProcessTemplate where procardTemplate.id=?",
							pt.getId());
			if (ajp == null) {
				pt.setAllJiepai(null);
			} else {
				pt.setAllJiepai(ajp);
			}
			totalDao.update(pt);
		}
		return "true";
	}

	public String deleteAllProcess(Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		ProcardTemplate thispt = (ProcardTemplate) totalDao
				.getObjectByCondition("from ProcardTemplate where id=?", id);
		if (thispt == null) {
			return "没有找到目标零件!";
		}
		String BanbenSql = null;
		if (thispt.getBanBenNumber() == null
				|| thispt.getBanBenNumber().length() == 0) {
			BanbenSql = " and (banBenNumber is null  or banBenNumber='')";
		} else {
			BanbenSql = " and banBenNumber='" + thispt.getBanBenNumber() + "'";
		}
		List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao
				.query(
						"from ProcardTemplate where  markId=(select markId from ProcardTemplate where id=?) and (banbenStatus is null or banbenStatus ='默认') and productStyle=?"
								+ BanbenSql, id, thispt.getProductStyle());
		if (ptList != null && ptList.size() > 0) {
			int i = 0;
			for (ProcardTemplate pt : ptList) {
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplate process : processSet) {
						process.setDataStatus("删除");
						process.setProcardTemplate(null);
						process.setOldPtId(pt.getId());
						process.setOldbanci(pt.getBanci());
						process.setDeleteTime(Util.getDateTime());
						totalDao.update(process);
						ProcardTemplateChangeLog.addchangeLog(totalDao, pt,
								process, "工序", "删除", user, nowtime);
					}
				}
				pt.setAllJiepai(null);
				totalDao.update(pt);
				i++;
			}
			return "true";
		}
		return "没有找到对应的零件!";
	}

	/***
	 * 显示试算页面
	 */
	@SuppressWarnings("unchecked")
	public String packageData(Integer id, Map map) {
		int count = 1;
		DataGrid dg = new DataGrid();
		int length = 1;
		// 查询所有零组件信息
		String hql = "from ProcardTemplate where rootId=? and procardStyle<>'外购' and (dataStatus is null or dataStatus!='删除')";
		List<ProcardTemplate> proTemList = totalDao.query(hql, id);
		length += proTemList.size();
		for (ProcardTemplate proTem : proTemList) {

			// 组装零件信息
			VOProductTree part = new VOProductTree(count++, proTem.getMarkId(),
					proTem.getProName(), 0D, null, proTem.getId());
			// 获得零件对应工序信息
			Set<ProcessTemplate> processTemSet = proTem.getProcessTemplate();
			if (processTemSet != null && processTemSet.size() > 0) {
				length += processTemSet.size();
				for (ProcessTemplate processTem : processTemSet) {
					// 数据有效性效验
					Double OPLabourBeat = 0.0; // 人工节拍
					Double OPEquipmentBeat = 0.0; // 设备节拍
					Double PRLabourBeat = 0.0; // 人工节拍
					Double PRPrepareIndex = 0.0; // 准备次数
					if (processTem.getOpcaozuojiepai() != null) {
						OPLabourBeat = Double.parseDouble(String.format("%.2f",
								processTem.getOpcaozuojiepai()));

					}
					if (processTem.getOpshebeijiepai() != null) {
						OPEquipmentBeat = Double.parseDouble(String.format(
								"%.2f", processTem.getOpshebeijiepai()));
					}
					if (processTem.getGzzhunbeijiepai() != null) {
						PRLabourBeat = Double.parseDouble(String.format("%.2f",
								processTem.getGzzhunbeijiepai()));
					}
					if (processTem.getGzzhunbeicishu() != null) {
						PRPrepareIndex = Double.parseDouble(String.format(
								"%.2f", processTem.getGzzhunbeicishu()));
					}

					VOProductTree process = null;
					if (map != null && map.size() > 0) {
						DTOProcess dto = (DTOProcess) map.get(processTem
								.getId());
						process = new VOProductTree(count++, processTem
								.getProcessName(), processTem.getProcessNO()
								.toString(), processTem.getShebeiNo(), dto
								.getOPLabourBeat(), dto.getOPEquipmentBeat(),
								dto.getPRLabourBeat(), dto.getPRPrepareTIme(),
								dto.getHandlers(),
								processTem.getProcessMomey(), part.getId(),
								processTem.getId(), "PR", dto.getSumMoney(),
								dto.getUnitPrice(), dto.getJobNum());
					} else {
						process = new VOProductTree(count++, processTem
								.getProcessName(),
								processTem.getGongwei() == null ? ""
										: processTem.getGongwei().toString(),
								processTem.getShebeiNo(), OPLabourBeat,
								OPEquipmentBeat, PRLabourBeat, PRPrepareIndex,
								processTem.getOperatorName(), processTem
										.getProcessMomey(), part.getId(),
								processTem.getId(), "PR", null, null,
								processTem.getOperatorCode());
					}
					part.getChildren().add(process);
				}
			}
			dg.getRows().add(part);
		}
		dg.setTotal(new Long(length));
		String jsonStr = JSON.toJSON(dg).toString();
		return jsonStr;
	}

	/***
	 * 查找总成信息
	 * 
	 * @param procardTem
	 * @param mentioningAwardPrice
	 * @return
	 */
	@Override
	public String packageProduct(ProcardTemplate pp, Double mentioningAwardPrice) {
		DataGrid dg = new DataGrid();
		if (pp.getDailyoutput() == null) {
			pp.setDailyoutput(0F);
		}
		VOProductTree data = new VOProductTree(pp.getId(), pp.getMarkId(), pp
				.getProName(), pp.getDailyoutput().doubleValue(), pp
				.getProductStyle(), pp.getCarStyle(), mentioningAwardPrice, pp
				.getId());
		dg.getRows().add(data);
		dg.setTotal(new Long(1));
		String resultStr = JSON.toJSONString(dg);
		return resultStr;
	}

	/**
	 * @Title: trial
	 * @Description: 试算数据
	 * @param id
	 *            成品ID
	 * @return String 前台数据
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String trial(Integer id, Float dayOutput) {
		Map map = trialMentioningAwardPrice(id, dayOutput);
		String url = null;
		if (map != null && map.size() > 0)
			url = packageData(id, map);
		return url;
	}

	/**
	 * @Title: trialMentioningAwardPrice
	 * @Description: 试算
	 * @param id
	 * @return Map
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public Map trialMentioningAwardPrice(Integer id, Float dayOutput) {
		Map session = ActionContext.getContext().getSession();
		ProcardTemplate procardt = findProcardTemById(id); // 查询成品,根据ID

		// 处理前面录入的日产量
		if (dayOutput != null) {
			procardt.setDailyoutput(dayOutput);
		}
		List<ProcardTemplate> procardSet = findProcardTemByRootId(id); // 根据成品ID查询组件
		/**
		 * 遍历所有组件
		 */
		Double allProcessWages = 0.0; // 此组件工序工资
		Double allOPSynthesizeStrength = 0.0; // 操作过程综合强度
		Double allPRSynthesizeStrength = 0.0; // 准备过程综合强度

		Double allOPSynthesizeCoefficient = 0.0; // 操作过程综合系数(add)
		Double allPRSynthesizeCoefficient = 0.0; // 操作过程综合系数(add)

		/**
		 * 操作过程
		 */
		Double OPSkillIndex = 0.0; // 操作技能指数
		Double OPNotReplaceCoefficient = 0.0; // 不可替换系数
		Double OPLoadIndex = 0.0; // 负荷指数
		Double OPLabourBeat = 0.0; // 人工节拍
		/**
		 * 准备过程
		 */
		Double PRSkillIndex = 0.0;// 技能指数
		Double PRNotReplaceCoefficient = 0.0; // 不可替换系数
		Double PRLoadIndex = 0.0; // 负荷指数
		Double PRLabourBeat = 0.0; // 人工节拍
		Map map = new HashMap(); // 存储数据
		try {
			/**
			 * 遍历算 所有工序工资 求出技能指数 、可替换人数、负荷指数、人工节拍各总和
			 */
			for (ProcardTemplate procardTemplate : procardSet) {
				Set<ProcessTemplate> processset = procardTemplate
						.getProcessTemplate(); // 此组件需要的工序
				/**
				 * 遍历一个组件所需要的工序
				 */
				for (ProcessTemplate processT : processset) {
					DTOProcess dto = (DTOProcess) session.get(processT.getId()); // 从Session中取出存储的前台填写数据
					String jobNumStr = null;
					if (dto != null) {
						jobNumStr = dto.getJobNum(); // 获取完成此工序的人员工号
						if (jobNumStr == null || jobNumStr.length() <= 0) {
							// 保存页面添加人员信息
							processT.setOperatorDept("");
							processT.setOperatorCode("");
							processT.setOperatorCardId("");
							processT.setOperatorName("");
							// 保存参数信息
							processT.setOpcaozuojiepai(Float.valueOf(dto
									.getOPLabourBeat().toString()));
							processT.setOpshebeijiepai(Float.valueOf(dto
									.getOPEquipmentBeat().toString()));
							processT.setGzzhunbeijiepai(Float.valueOf(dto
									.getPRLabourBeat().toString()));
							processT.setGzzhunbeicishu(Float.valueOf(dto
									.getPRPrepareTIme().toString()));
							// 清空记录
							processT.setProcessMomey(0D);

						}
					} else {
						jobNumStr = processT.getOperatorCode(); // 获取完成此工序的人员工号
					}
					if (jobNumStr == null || jobNumStr.equals("")) {
						continue;
					}
					String[] allJobNum = jobNumStr.split(";");
					Double workingHoursWages = 0.0; // 工序工时工资
					String dept = "";
					String cardId = "";
					String name = "";
					for (String jobNum : allJobNum) { // 统计工序中基本工时工资
						WageStandard wageStandard = wss.findWSByUser(jobNum); // 根据工号查询工资模板
						if (wageStandard == null) {
							continue;
						}
						if (dto != null) {
							dept += ";" + wageStandard.getDept();
							cardId += ";" + wageStandard.getCardId();
							name += ";" + wageStandard.getUserName();
							// 保存页面添加人员信息
							processT.setOperatorDept(dept);
							processT.setOperatorCode(jobNumStr);
							processT.setOperatorCardId(cardId);
							processT.setOperatorName(name);
							// 保存参数信息
							processT.setOpcaozuojiepai(Float.valueOf(dto
									.getOPLabourBeat().toString()));
							processT.setOpshebeijiepai(Float.valueOf(dto
									.getOPEquipmentBeat().toString()));
							processT.setGzzhunbeijiepai(Float.valueOf(dto
									.getPRLabourBeat().toString()));
							processT.setGzzhunbeicishu(Float.valueOf(dto
									.getPRPrepareTIme().toString()));
						}

						InsuranceGold insuranceGold = igs
								.findInsuranceGoldBylc(wageStandard
										.getLocalOrField(), wageStandard
										.getCityOrCountryside(), wageStandard
										.getPersonClass()); // 福利系数
						workingHoursWages += workingHoursWages
								+ (wageStandard.getGangweigongzi()
										+ wageStandard.getSsBase()
										* (insuranceGold.getQYoldageInsurance()
												+ insuranceGold
														.getQYmedicalInsurance()
												+ insuranceGold
														.getQYunemploymentInsurance()
												+ insuranceGold
														.getQYinjuryInsurance() + insuranceGold
												.getQYmaternityInsurance())
										/ 100 + wageStandard.getGjjBase()
										* insuranceGold.getQYhousingFund()
										/ 100);
					}

					Double basicWorkingHoursWages = workingHoursWages / SECONDS; // 工序中基本工时工资(秒工资)
					Double processWages = 0.0;
					if (dto != null) {
						processWages = basicWorkingHoursWages
								* ((dto.getOPLabourBeat() + dto
										.getOPEquipmentBeat())
										* procardt.getDailyoutput() + (dto
										.getPRLabourBeat() * dto
										.getPRPrepareTIme())); // 基本工时工资(单个工序工资=秒工资*节拍=人工节拍+设备节拍+准备人工节拍*准备次数)
					} else {
						processWages = basicWorkingHoursWages
								* ((processT.getOpcaozuojiepai() + processT
										.getOpshebeijiepai())
										* procardt.getDailyoutput() + (processT
										.getGzzhunbeijiepai() * processT
										.getGzzhunbeicishu())); // 基本工时工资(单个工序工资)
					}
					allProcessWages = allProcessWages + processWages; // 所有工序工资

					/**
					 * 操作过程统计
					 */
					if (processT.getOptechnologyRate() != null)
						OPSkillIndex += processT.getOptechnologyRate(); // 统计技能指数
					if (processT.getOpnoReplaceRate() != null)
						OPNotReplaceCoefficient += processT
								.getOpnoReplaceRate(); // 统计不可替换系数
					if (processT.getOpfuheRate() != null)
						OPLoadIndex += processT.getOpfuheRate(); // 统计负荷指数
					if (dto != null) {
						if (dto.getOPLabourBeat() != null)
							OPLabourBeat += dto.getOPLabourBeat(); // 统计人工节拍
						// 前台替换
					} else {
						if (processT.getOpcaozuojiepai() != null)
							OPLabourBeat += processT.getOpcaozuojiepai(); // 统计人工节拍
					}
					/**
					 * 准备过程统计
					 */
					if (processT.getGztechnologyRate() != null)
						PRSkillIndex += processT.getGztechnologyRate(); // 统计技能指数
					if (processT.getGznoReplaceRate() != null)
						PRNotReplaceCoefficient += processT
								.getGznoReplaceRate(); // 统计不可替换系数
					if (processT.getGzfuheRate() != null)
						PRLoadIndex += processT.getGzfuheRate(); // 统计负荷指数
					if (dto != null) {
						if (dto.getOPLabourBeat() != null)
							PRLabourBeat += dto.getPRLabourBeat(); // 统计人工节拍
					} else {
						if (processT.getGzzhunbeijiepai() != null)
							PRLabourBeat += processT.getGzzhunbeijiepai(); // 统计人工节拍
					}
				}
			}
			/**
			 * 遍历算出综合强度
			 */
			for (ProcardTemplate procardTemplate : procardSet) {
				Set<ProcessTemplate> processset = procardTemplate
						.getProcessTemplate(); // 此组件需要的工序
				/**
				 * 遍历一个组件所需要的工序
				 */
				try {
					for (ProcessTemplate processT : processset) {
						if (processT.getOpjiaofu() == null)
							continue;
						DTOProcess dto = (DTOProcess) session.get(processT
								.getId());
						Double dtoOPla = 0.0;// 操作人工节拍
						Double dtoPRla = 0.0;// 准备过程人工节拍
						if (dto != null) {
							if (dto.getOPLabourBeat() != null)
								dtoOPla = dto.getOPLabourBeat();
							if (dto.getPRLabourBeat() != null)
								dtoPRla = dto.getPRLabourBeat();
						} else {
							if (processT.getOpcaozuojiepai() != null)
								dtoOPla = processT.getOpcaozuojiepai()
										.doubleValue();
							if (processT.getGzzhunbeijiepai() != null)
								dtoPRla = processT.getGzzhunbeijiepai()
										.doubleValue();
						}
						Double OPcannot = 0.0;// 操作不可替换系数
						Double PRcannot = 0.0;// 准备过程不可替换系数
						if (processT.getOpnoReplaceRate() != null)
							OPcannot = processT.getOpnoReplaceRate()
									.doubleValue();
						if (processT.getGznoReplaceRate() != null)
							PRcannot = processT.getGznoReplaceRate()
									.doubleValue();
						// 操作过程 综合系数
						Double OPSynthesizeCoefficient = ConvertNumber.isNum(
								processT.getOptechnologyRate().doubleValue(),
								OPSkillIndex)
								+ ConvertNumber.isNum(OPcannot,
										OPNotReplaceCoefficient)
								+ ConvertNumber.isNum(processT.getOpfuheRate()
										.doubleValue(), OPLoadIndex)
								+ ConvertNumber.isNum(dtoOPla, OPLabourBeat);
						// 统计操作综合系数
						allOPSynthesizeCoefficient += OPSynthesizeCoefficient;
						// 准备过程 综合系数
						Double PRSynthesizeCoefficient = ConvertNumber.isNum(
								processT.getGztechnologyRate().doubleValue(),
								PRSkillIndex)
								+ ConvertNumber.isNum(PRcannot,
										PRNotReplaceCoefficient)
								+ ConvertNumber.isNum(processT.getGzfuheRate()
										.doubleValue(), PRLoadIndex)
								+ ConvertNumber.isNum(dtoPRla, PRLabourBeat);
						// 统计准备综合系数
						allPRSynthesizeCoefficient += PRSynthesizeCoefficient;
						// 操作过程 综合强度 = 综合系数 * 交付数量
						Double OPsynthesizeStrength = OPSynthesizeCoefficient
								* processT.getOpjiaofu();
						allOPSynthesizeStrength += OPsynthesizeStrength; // 统计单个零件操作过程所有工序强度
						// 准备过程 综合强度 = 综合系数 * 准备次数
						Double PRSynthesizeStrength = 0.0;
						if (dto != null) {
							PRSynthesizeStrength = PRSynthesizeCoefficient
									* dto.getPRPrepareTIme();
						} else {
							PRSynthesizeStrength = PRSynthesizeCoefficient
									* processT.getGzzhunbeicishu();
						}
						allPRSynthesizeStrength += PRSynthesizeStrength; // 统计单个零件准备过程所有工序强度
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 销售单价
			if (procardt.getLaborcost() == null) {
				procardt.setLaborcost(0F);
			}
			// 调整比例
			if (procardt.getFenpeiRate() == null) {
				procardt.setFenpeiRate(0F);
			}

			Double mentioningAwardPrice = (procardt.getDailyoutput()
					* procardt.getLaborcost() * procardt.getFenpeiRate() - allProcessWages)
					/ procardt.getDailyoutput(); // ((日产量 * 报价费用 * 可调系数) -
			// 所有工序人工成本工资) / 日产量=提奖价
			/*********** 单件价格 ************/
			procardt.setOnePrice(mentioningAwardPrice);

			Double distributeBonus = mentioningAwardPrice
					* procardt.getDailyoutput(); // 可分配奖金(元/月) = 单件价格 *入库量

			// 单件计价奖金
			session.put("mentioningAwardPrice", mentioningAwardPrice);

			// 开始计算工序金额
			/**
			 * 操作过程
			 */
			Double OPdistributeProportion = allOPSynthesizeStrength
					/ (allOPSynthesizeStrength + allPRSynthesizeStrength); // 分配比例
			// =
			// sum(综合强度)
			// /
			// (sum(操作综合强度)
			// +
			// sum(准备综合强度))
			Double OPdistributeTotal = distributeBonus * OPdistributeProportion; // 分配总额
			// =
			// 可分配奖金(元/月)
			// *
			// 分配比例
			/**
			 * 准备过程
			 */
			Double PRdistributeProportion = allPRSynthesizeStrength
					/ (allOPSynthesizeStrength + allPRSynthesizeStrength); // 分配比例
			// =
			// sum(综合强度)
			// /
			// (sum(操作综合强度)
			// +
			// sum(准备综合强度))
			Double PRdistributeTotal = distributeBonus * PRdistributeProportion; // 分配总额
			// =
			// 可分配奖金(元/月)
			// *
			// 分配比例

			/**
			 * 遍历算出综合强度
			 */
			for (ProcardTemplate procardTemplate : procardSet) {
				Set<ProcessTemplate> processset = procardTemplate
						.getProcessTemplate(); // 此组件需要的工序
				/**
				 * 遍历一个组件所需要的工序
				 */
				for (ProcessTemplate processT : processset) {
					DTOProcess newDto = null;
					// 工序提交量==总成提交量
					processT.setOpjiaofu(procardt.getDailyoutput());

					// ///////开始非空验证
					if (processT.getOptechnologyRate() == null) {
						processT.setOptechnologyRate(0F);
					}
					if (processT.getOpCouldReplaceRate() == null) {
						processT.setOpCouldReplaceRate(0F);
					}
					if (processT.getOpfuheRate() == null) {
						processT.setOpfuheRate(0F);
					}
					if (processT.getOpcaozuojiepai() == null) {
						processT.setOpcaozuojiepai(0F);
					}
					if (processT.getOpshebeijiepai() == null) {
						processT.setOpshebeijiepai(0F);
					}
					if (processT.getOpnoReplaceRate() == null) {
						processT.setOpnoReplaceRate(0F);
					}
					if (processT.getOpzonghezhishu() == null) {
						processT.setOpzonghezhishu(0F);
					}
					if (processT.getOpzongheqiangdu() == null) {
						processT.setOpzongheqiangdu(0F);
					}
					if (processT.getGztechnologyRate() == null) {
						processT.setGztechnologyRate(0F);
					}
					if (processT.getGzCouldReplaceRate() == null) {
						processT.setGzCouldReplaceRate(0F);
					}
					if (processT.getGzfuheRate() == null) {
						processT.setGzfuheRate(0F);
					}
					if (processT.getGzzhunbeicishu() == null) {
						processT.setGzzhunbeicishu(0F);
					}
					if (processT.getGznoReplaceRate() == null) {
						processT.setGznoReplaceRate(0F);
					}
					if (processT.getGzzonghezhishu() == null) {
						processT.setGzzonghezhishu(0F);
					}
					if (processT.getGzzongheqiangdu() == null) {
						processT.setGzzongheqiangdu(0F);
					}
					if (processT.getGzzhunbeijiepai() == null) {
						processT.setGzzhunbeijiepai(0F);
					}

					if (processT.getOpjiaofu() == null
							|| processT.getOpjiaofu().equals("")) {
						Double newOPLabourBeat = 0.0;
						Double newOPEquipmentBeat = 0.0;
						Double newPRLabourBeat = 0.0;
						Double newPRPrepareTIme = 0.0;
						if (processT.getOpcaozuojiepai() != null)
							newOPLabourBeat = processT.getOpcaozuojiepai()
									.doubleValue();
						if (processT.getOpshebeijiepai() != null)
							newOPEquipmentBeat = processT.getOpshebeijiepai()
									.doubleValue();
						if (processT.getGzzhunbeijiepai() != null)
							newPRLabourBeat = processT.getGzzhunbeijiepai()
									.doubleValue();
						if (processT.getGzzhunbeicishu() != null)
							newPRPrepareTIme = processT.getGzzhunbeicishu()
									.doubleValue();
						newDto = new DTOProcess(processT.getId(), null,
								newOPLabourBeat, newOPEquipmentBeat,
								newPRLabourBeat, newPRPrepareTIme, null, 0.0,
								0.0);
						map.put(processT.getId(), newDto);
						continue;
					}
					DTOProcess dto = (DTOProcess) session.get(processT.getId());// 从前台获取保存的工序对象数据
					Double dtoOPla = 0.0;
					Double dtoPRla = 0.0;
					if (dto != null) {
						if (dto.getOPLabourBeat() != null)
							dtoOPla = dto.getOPLabourBeat();
						if (dto.getPRLabourBeat() != null)
							dtoPRla = dto.getPRLabourBeat();
					} else {
						if (processT.getOpcaozuojiepai() != null)
							dtoOPla = processT.getOpcaozuojiepai()
									.doubleValue();
						if (processT.getGzzhunbeijiepai() != null)
							dtoPRla = processT.getGzzhunbeijiepai()
									.doubleValue();
					}
					Double OPcannot = 0.0; // 不可替换系数
					Double PRcannot = 0.0;
					if (processT.getOpnoReplaceRate() != null)
						OPcannot = processT.getOpnoReplaceRate().doubleValue();
					if (processT.getGznoReplaceRate() != null)
						PRcannot = processT.getGznoReplaceRate().doubleValue();
					// 操作过程 综合系数
					Double OPSynthesizeCoefficient = ConvertNumber.isNum(
							processT.getOptechnologyRate().doubleValue(),
							OPSkillIndex)
							+ ConvertNumber.isNum(OPcannot,
									OPNotReplaceCoefficient)
							+ ConvertNumber.isNum(processT.getOpfuheRate()
									.doubleValue(), OPLoadIndex)
							+ ConvertNumber.isNum(dtoOPla, OPLabourBeat);
					// 准备过程 综合系数
					Double PRSynthesizeCoefficient = ConvertNumber.isNum(
							processT.getGztechnologyRate().doubleValue(),
							PRSkillIndex)
							+ ConvertNumber.isNum(PRcannot,
									PRNotReplaceCoefficient)
							+ ConvertNumber.isNum(processT.getGzfuheRate()
									.doubleValue(), PRLoadIndex)
							+ ConvertNumber.isNum(dtoPRla, PRLabourBeat);
					// Double OPMoney = OPdistributeTotal *
					// OPSynthesizeCoefficient
					// / OPdistributeProportion; //操作过程 工序该分配金额
					// Double PRMoney = PRdistributeTotal *
					// PRSynthesizeCoefficient
					// / PRdistributeProportion; //准备过程 工序该分配金额
					Double OPMoney = OPdistributeTotal
							* OPSynthesizeCoefficient
							/ allOPSynthesizeCoefficient; // 操作过程 工序该分配金额 =
					// 操作分配总额 *
					// 综合系数 / sum(综合系数)
					Double PRMoney = PRdistributeTotal
							* PRSynthesizeCoefficient
							/ allPRSynthesizeCoefficient; // 准备过程 工序该分配金额 =
					// 准备分配总额 *
					// 综合指数 / sum(综合指数)
					Double sumMoney = OPMoney + PRMoney; // 单工序分配总额
					Double unitPrice = sumMoney / processT.getOpjiaofu(); // 每个工序分配金额

					processT.setProcessMomey(unitPrice);

					// =
					// 分配总额
					// /
					// 交付数量
					if (dto != null) {
						newDto = new DTOProcess(processT.getId(), dto
								.getJobNum(), dto.getOPLabourBeat(), dto
								.getOPEquipmentBeat(), dto.getPRLabourBeat(),
								dto.getPRPrepareTIme(), dto.getHandlers(),
								sumMoney, unitPrice);
					} else {
						newDto = new DTOProcess(processT.getId(), processT
								.getOperatorCode(), processT
								.getOpcaozuojiepai().doubleValue(), processT
								.getOpshebeijiepai().doubleValue(), processT
								.getGzzhunbeijiepai().doubleValue(), processT
								.getGzzhunbeicishu().doubleValue(), processT
								.getOperatorName(), sumMoney, unitPrice);
					}
					map.put(processT.getId(), newDto);
					if (dto != null)
						session.remove(processT.getId());
				}
			}
			totalDao.update(procardt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
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

	@Override
	public List<ProcardTemplate> getAllNames(String markId, String procardStyle) {
		// TODO Auto-generated method stub
		if (markId != null) {
			// String hql_ss =
			// " from ProcardTemplate A JOIN ( select count(*)B where A.markId = B.markId ORDER BY id  DESC";
			// List<ProcardTemplate> plist0 = totalDao.findByPage(hql_ss, 1,
			// 10);
			// String hql = "from ProcardTemplate where markId like '%" + markId
			// + "%'";
			// if (procardStyle != null && !"".equals(procardStyle)) {
			// hql += " and procardStyle ='" + procardStyle +
			// "' order by id desc";
			// }
			// List<ProcardTemplate> plist1 = totalDao.findByPage(hql, 1, 10);
			// if (plist1.size() > 0) {
			//
			// List<ProcardTemplate> plist2 = new ArrayList<ProcardTemplate>();
			// List<String> markIdList = new ArrayList<String>();
			// int count = 0;
			// for (ProcardTemplate p1 : plist1) {
			// if (p1.getMarkId() != null
			// && !markIdList.contains(p1.getMarkId())) {
			// markIdList.add(p1.getMarkId());
			// plist2.add(p1);
			// count++;
			// if (count == 10) {// 只取前十条
			// break;
			// }
			// } else {
			// int count2 = 0;
			// for (ProcardTemplate p2 : plist2) {
			// if (p1.getMarkId() != null
			// && p2.getMarkId().equals(p1.getMarkId())
			// && p2.getProcardStyle() != null
			// && p1.getProcardStyle() != null
			// && p2.getProcardStyle().endsWith(
			// p1.getProcardStyle())) {
			// count2++;
			// }
			// }
			// if (count2 == 0) {// 没有同件号的同时也同生产类型的情况
			// plist2.add(p1);
			// count++;
			// if (count == 10) {// 只取前十条
			// break;
			// }
			// }
			// }
			// }
			// return plist2;
			// }CONCAT
			String addsql1 = "";
			String addsql2 = "";

			if (procardStyle != null && !"".equals(procardStyle)) {
				addsql1 = " and procardStyle = '" + procardStyle + "'";
				addsql2 = "CONCAT(a.markId,a.procardStyle) = CONCAT(b.markId,b.procardStyle)";
			} else {
				addsql2 = "a.markId=b.markId";
			}
			String sql = "SELECT top 10 A.id,A.markId,A.procardStyle FROM  ta_sop_w_ProcardTemplate A JOIN (select count(*) count,  markId,procardStyle  FROM  ta_sop_w_ProcardTemplate WHERE (banbenStatus is null or banbenStatus='默认') and markId like '%"
					+ markId
					+ "%' "
					+ addsql1
					+ "  GROUP BY markId,procardStyle) B ON "
					+ addsql2
					+ "  ORDER BY id  DESC";
			List<Map> list = totalDao.findBySql(sql);
			// String hql_ss =
			// " from ProcardTemplate A JOIN ( select count(*)B with  A.markId = B.markId ORDER BY id  DESC";
			// List<ProcardTemplate> plist0 = totalDao.findByPage(hql_ss, 1,
			// 10);
			List<ProcardTemplate> ptLsit = new ArrayList<ProcardTemplate>();
			if (list != null && list.size() > 0) {
				for (Map map : list) {
					ProcardTemplate pt = new ProcardTemplate();
					pt.setId((Integer) map.get("id"));
					pt.setMarkId((String) map.get("markId"));
					pt.setProcardStyle((String) map.get("procardStyle"));
					ptLsit.add(pt);
				}
			}
			return ptLsit;

		}
		return null;
	}

	@Override
	public ProcardTemplate findProcardTemByMarkId(String markId, String statue) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> plist = new ArrayList<ProcardTemplate>();
		if (markId != null && markId.length() > 0) {
			if ("writename".equals(statue)) {
				return (ProcardTemplate) totalDao
						.getObjectByCondition("from ProcardTemplate where procardStyle in ('总成','自制') and  (markId ='"
								+ markId + "' or ywMarkId='" + markId + "')");
			} else {
				return (ProcardTemplate) totalDao
						.getObjectByCondition("from ProcardTemplate where markId ='"
								+ markId + "'");
			}
		}
		return null;
	}

	@Override
	public Integer saveCopyProcard(ProcardTemplate pt1, ProcardTemplate pt2,
			String productStyle) {
		ProcardTemplate sameSon = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where  (banbenStatus is null or banbenStatus='' or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')  and  markId=? and  procardTemplate.id=?",
						pt2.getMarkId(), pt1.getId());
		if (sameSon != null) {
			MKUtil.writeJSON(false, "此件号下已存在该零件复制失败!", null);
			throw new RuntimeException("此件号下已存在该零件复制失败!");
		}
		Integer sonId = null;
		List<ProcardTemplate> sameptList = getSameProcardTemplate(pt1
				.getMarkId(), pt1.getBanBenNumber(), pt1.getProductStyle());
		pt2 = findProcardTemById(pt2.getId());
		String olprocardStyle = pt2.getProcardStyle();
		if (olprocardStyle.equals("总成")) {
			pt2.setProcardStyle("自制");
		}
		for (ProcardTemplate pt : sameptList) {
			if (!pt.getProcardStyle().equals("外购")) {
				if (sonId == null) {
					sonId = saveCopyProcardImple(pt, pt2, productStyle);
				} else {
					saveCopyProcardImple(pt, pt2, productStyle);
				}
			}
		}
		pt2.setProcardStyle(olprocardStyle);
		if (sonId != null) {
			return sonId;
		} else {
			MKUtil.writeJSON(false, "复制失败!", null);
			throw new RuntimeException("复制失败!");
		}
	}

	/**
	 * 将pt2复制到pt1下
	 * 
	 * @param pt1
	 * @param pt2
	 * @param productStyle
	 * @return
	 */
	public Integer saveCopyProcardImple(ProcardTemplate pt1,
			ProcardTemplate pt2, String productStyle) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		String time = Util.getDateTime();
		if (pt1 != null && pt2 != null) {
			// 获取主副两个对象的持久层
			ProcardTemplate oldpt1 = pt1;
			ProcardTemplate oldpt2 = pt2;
			if (oldpt1 != null && oldpt2 != null) {
				// if (oldpt2.getProcardStyle() != null&&
				// oldpt2.getProcardStyle().equals("总成")
				// &&oldpt1.getProcardStyle() != null&&
				// oldpt1.getProcardStyle().equals("总成")) {
				// // 如果是总成
				// List<ProcardTemplate> ptSet = totalDao
				// .query(
				// "from ProcardTemplate where procardTemplate.id=? order by id asc",
				// oldpt2.getId());
				// // 删除原有的process
				// Set<ProcessTemplate> processSet1 = oldpt1
				// .getProcessTemplate();
				// oldpt1.setProcessTemplate(null);
				// if (processSet1 != null && processSet1.size() > 0) {
				// for (ProcessTemplate process : processSet1) {
				// process.setProcardTemplate(null);
				// b = b & totalDao.delete(process);
				// }
				//
				// }
				// // 复制process
				// Set<ProcessTemplate> processSet2 = oldpt2
				// .getProcessTemplate();
				// if (b & processSet2 != null && processSet2.size() > 0) {
				// Set<ProcessTemplate> newprocessSet = new
				// HashSet<ProcessTemplate>();
				// for (ProcessTemplate process : processSet2) {
				// ProcessTemplate p = new ProcessTemplate();
				// BeanUtils.copyProperties(process, p, new String[] {
				// "id", "procardTemplate", "processPRNScore",
				// "taSopGongwei", "processFuLiaoTemplate" });
				// // -----辅料开始---------
				// if (p.getIsNeedFuliao() != null
				// && p.getIsNeedFuliao().equals("yes")) {
				// Set<ProcessFuLiaoTemplate> fltmpSet = process
				// .getProcessFuLiaoTemplate();
				// if (fltmpSet.size() > 0) {
				// Set<ProcessFuLiaoTemplate> pinfoFlSet = new
				// HashSet<ProcessFuLiaoTemplate>();
				// for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
				// ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
				// BeanUtils.copyProperties(fltmp,
				// pinfoFl, new String[] { "id",
				// "processTemplate" });
				// if (pinfoFl.getQuanzhi1() == null) {
				// pinfoFl.setQuanzhi1(1f);
				// }
				// if (pinfoFl.getQuanzhi2() == null) {
				// pinfoFl.setQuanzhi2(1f);
				// }
				// pinfoFl.setProcessTemplate(p);
				// pinfoFlSet.add(pinfoFl);
				// }
				// p.setProcessFuLiaoTemplate(pinfoFlSet);
				// }
				// }
				// // -----辅料结束---------
				// newprocessSet.add(p);
				// }
				// oldpt1.setProcessTemplate(newprocessSet);
				// }
				// // 原材料属性
				// oldpt1.setDanjiaojian(oldpt2.getDanjiaojian());
				// oldpt1.setTrademark(oldpt2.getTrademark());
				// oldpt1.setSpecification(oldpt2.getSpecification());
				// oldpt1.setLuhao(oldpt2.getLuhao());
				// oldpt1.setNumber(oldpt2.getNumber());
				// oldpt1.setActualFixed(oldpt2.getActualFixed());
				// oldpt1.setYuanUnit(oldpt2.getYuanUnit());
				// oldpt1.setQuanzi1(oldpt2.getQuanzi1());
				// oldpt1.setQuanzi2(oldpt2.getQuanzi2());
				// oldpt1.setClClass(oldpt2.getClClass());
				// oldpt1.setStatus(oldpt2.getStatus());
				// oldpt1.setLingliaostatus(oldpt2.getLingliaostatus());
				// b = b & totalDao.update(oldpt1);
				// if (b & ptSet != null && ptSet.size() > 0) {
				// // Set<ProcardTemplate> ptSet2 = new
				// // HashSet<ProcardTemplate>();
				// // for (ProcardTemplate pt : ptSet) {
				// // ptSet2.add(pt);
				// // }
				// for (ProcardTemplate pt : ptSet) {
				// b = b & saveCopyProcard(pt1, pt);
				// }
				// }
				//
				// } else {
				// 复制procardTemplate
				ProcardTemplate newpt2 = new ProcardTemplate();
				// newpt2.setMarkId("123");
				BeanUtils.copyProperties(pt2, newpt2, new String[] { "id",
						"procardTemplate", "procardTSet", "processTemplate",
						"zhUsers" });
				if (newpt2.getProcardStyle() != null
						&& newpt2.getProcardStyle().equals("总成")) {
					newpt2.setProcardStyle("自制");
				}
				if (newpt2.getBzStatus() == null
						|| newpt2.getBzStatus().length() == 0
						|| newpt2.getBzStatus().equals("待编制")) {
					newpt2.setBianzhiName(user.getName());
					newpt2.setBianzhiId(user.getId());
				}
				newpt2.setId(null);
				newpt2.setRootId(oldpt1.getRootId());
				newpt2.setFatherId(oldpt1.getId());
				newpt2.setRootMarkId(oldpt1.getRootMarkId());
				newpt2.setYwMarkId(oldpt1.getYwMarkId());
				newpt2.setProductStyle(oldpt1.getProductStyle());
				newpt2.setAdduser(user.getName());
				newpt2.setAddcode(user.getCode());
				newpt2.setAddtime(Util.getDateTime());
				if (oldpt1.getBelongLayer() != null) {
					newpt2.setBelongLayer(oldpt1.getBelongLayer() + 1);
				}
				List<ProcardTemplate> ptSet = totalDao
						.query(
								"from ProcardTemplate where procardTemplate.id=? and (banbenStatus is null or banbenStatus='' or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除') order by id asc",
								oldpt2.getId());
				// Set<ProcardTemplate> ptSet2 = new
				// HashSet<ProcardTemplate>();
				// if (ptSet.size() > 0) {
				// for (ProcardTemplate pt : ptSet) {
				// ptSet2.add(pt);
				// }
				// }
				// 复制process
				Set<ProcessTemplate> processSet = oldpt2.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
					for (ProcessTemplate process : processSet) {
						ProcessTemplate p = new ProcessTemplate();
						BeanUtils.copyProperties(process, p, new String[] {
								"id", "procardTemplate", "processPRNScore",
								"taSopGongwei", "processFuLiaoTemplate" });
						p.setAddTime(Util.getDateTime());
						p.setAddUser(user.getName());
						// -----辅料开始---------
						if (p.getIsNeedFuliao() != null
								&& p.getIsNeedFuliao().equals("yes")) {
							Set<ProcessFuLiaoTemplate> fltmpSet = process
									.getProcessFuLiaoTemplate();
							if (fltmpSet.size() > 0) {
								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
									BeanUtils.copyProperties(fltmp, pinfoFl,
											new String[] { "id",
													"processTemplate" });
									if (pinfoFl.getQuanzhi1() == null) {
										pinfoFl.setQuanzhi1(1f);
									}
									if (pinfoFl.getQuanzhi2() == null) {
										pinfoFl.setQuanzhi2(1f);
									}
									pinfoFl.setProcessTemplate(p);
									pinfoFlSet.add(pinfoFl);
								}
								p.setProcessFuLiaoTemplate(pinfoFlSet);
							}
						}
						// -----辅料结束---------
						newprocessSet.add(p);
						totalDao.save(p);
						if (productStyle.equals("试制")) {// 试制图纸复制
							String tzSql = null;
							if (pt2.getProductStyle().equals("试制")) {
								tzSql = "from ProcessTemplateFile where glId="
										+ process.getId()
										+ " and processNO is not null";
							} else {
								tzSql = "from ProcessTemplateFile where glId is null and processNO ='"
										+ process.getProcessNO()
										+ "' and markId='"
										+ newpt2.getMarkId()
										+ "'";
								if (pt2.getBanci() == null
										|| pt2.getBanci() == 0) {
									tzSql += " and (banci is null or banci=0)";
								} else {
									tzSql += " and banci=" + pt2.getBanci();
								}
							}
							List<ProcessTemplateFile> processFileList = totalDao
									.query(tzSql);
							if (processFileList != null
									&& processFileList.size() > 0) {
								for (ProcessTemplateFile processFile : processFileList) {
									if (processFile.getBanci() == null) {
										processFile.setBanci(0);
									}
									ProcessTemplateFile newfile = new ProcessTemplateFile();
									BeanUtils.copyProperties(processFile,
											newfile, new String[] { "id" });
									newfile.setAddTime(time);
									newfile.setStatus("默认");
									newfile.setGlId(p.getId());
									newfile.setProductStyle("试制");
									totalDao.save(newfile);
								}
							}
						}
					}
					newpt2.setProcessTemplate(newprocessSet);
				}
				newpt2.setProcardTemplate(oldpt1);
				Set<ProcardTemplate> oldpt1ptSet = oldpt1.getProcardTSet();
				if (oldpt1ptSet != null && oldpt1ptSet.size() > 0) {
					for (ProcardTemplate p : oldpt1ptSet) {
						if (p.getMarkId() != null
								&& newpt2.getMarkId() != null
								&& p.getMarkId().equals(newpt2.getMarkId())
								&& (p.getBanbenStatus() == null || !p
										.getBanbenStatus().equals("历史"))
								&& (p.getDataStatus() == null || !p
										.getDataStatus().equals("删除"))) {
							// 原流水卡片模板下已含有被复制的流水卡片模板
							return 0;
						}
					}
					oldpt1ptSet.add(newpt2);
				}
				oldpt1.setProcardTSet(oldpt1ptSet);
				totalDao.save(newpt2);
				if (productStyle.equals("试制")) {// 试制图纸复制
					String tzSql = null;
					if (pt2.getProductStyle().equals("试制")) {
						tzSql = "from ProcessTemplateFile where glId="
								+ pt2.getId() + " and processNO is  null";
					} else {
						tzSql = "from ProcessTemplateFile where glId is null and processNO is null and markId='"
								+ newpt2.getMarkId() + "'";
						if (pt2.getBanci() == null || pt2.getBanci() == 0) {
							tzSql += " and (banci is null or banci=0)";
						} else {
							tzSql += " and banci=" + pt2.getBanci();
						}
					}
					List<ProcessTemplateFile> processFileList = totalDao
							.query(tzSql);
					if (processFileList != null && processFileList.size() > 0) {
						for (ProcessTemplateFile processFile : processFileList) {
							if (processFile.getBanci() == null) {
								processFile.setBanci(0);
							}
							ProcessTemplateFile newfile = new ProcessTemplateFile();
							BeanUtils.copyProperties(processFile, newfile,
									new String[] { "id" });
							newfile.setAddTime(time);
							newfile.setStatus("默认");
							newfile.setGlId(newpt2.getId());
							newfile.setProductStyle("试制");
							totalDao.save(newfile);
						}
					}
				}
				if (ptSet.size() > 0) {
					for (ProcardTemplate pt : ptSet) {
						saveCopyProcardImple(newpt2, pt, productStyle);
					}
				}
				// }
				return newpt2.getId();
			}
		}
		return null;
	}

	@Override
	public boolean saveCopyProcess(ProcardTemplate pt1, ProcardTemplate pt2) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return false;
		}
		boolean b = true;
		List<ProcardTemplate> samePtList = getSameProcardTemplate(pt1
				.getMarkId(), pt1.getBanBenNumber(), pt1.getProductStyle());
		pt2 = findProcardTemById(pt2.getId());
		for (ProcardTemplate pt : samePtList) {
			b = b & saveCopyProcessImple(pt, pt2, user);
		}
		if (b) {
			return b;
		} else {
			throw new RuntimeException("复制失败!");
		}
	}

	public boolean saveCopyProcessImple(ProcardTemplate pt1,
			ProcardTemplate pt2, Users user) {
		// TODO Auto-generated method stub
		ProcardTemplate oldpt1 = pt1;
		ProcardTemplate oldpt2 = pt2;
		if (oldpt1 != null && oldpt2 != null) {
			Set<ProcessTemplate> ptSet1 = oldpt1.getProcessTemplate();
			Set<ProcessTemplate> ptSet2 = oldpt2.getProcessTemplate();
			if (ptSet2 == null) {
				return true;
			} else if (ptSet1 == null) {
				for (ProcessTemplate pt : ptSet2) {
					ProcessTemplate newpt = new ProcessTemplate();
					BeanUtils.copyProperties(pt, newpt, new String[] { "id",
							"procardTemplate", "processPRNScore",
							"taSopGongwei", "processFuLiaoTemplate" });
					newpt.setAddTime(Util.getDateTime());
					newpt.setAddUser(user.getName());
					// -----辅料开始---------
					if (newpt.getIsNeedFuliao() != null
							&& newpt.getIsNeedFuliao().equals("yes")) {
						Set<ProcessFuLiaoTemplate> fltmpSet = pt
								.getProcessFuLiaoTemplate();
						if (fltmpSet.size() > 0) {
							Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
							for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
								ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
								BeanUtils
										.copyProperties(fltmp, pinfoFl,
												new String[] { "id",
														"processTemplate" });
								if (pinfoFl.getQuanzhi1() == null) {
									pinfoFl.setQuanzhi1(1f);
								}
								if (pinfoFl.getQuanzhi2() == null) {
									pinfoFl.setQuanzhi2(1f);
								}
								pinfoFl.setProcessTemplate(newpt);
								pinfoFlSet.add(pinfoFl);
							}
							newpt.setProcessFuLiaoTemplate(pinfoFlSet);
						}
					}
					// -----辅料结束---------
					newpt.setProcardTemplate(oldpt1);
					ptSet1.add(newpt);
					oldpt1.setProcessTemplate(ptSet1);
					totalDao.save(newpt);
				}

			} else {
				boolean b = true;
				Set<ProcessTemplate> ptSet22 = new HashSet<ProcessTemplate>();
				for (ProcessTemplate pt22 : ptSet2) {
					for (ProcessTemplate pt11 : ptSet1) {
						if (pt11.getProcessNO() != null
								&& pt22.getProcessNO() != null
								&& pt11.getProcessNO().equals(
										pt22.getProcessNO())) {

						} else {
							ptSet22.add(pt22);
						}
					}
				}
				for (ProcessTemplate pt22 : ptSet22) {
					ProcessTemplate newpt = new ProcessTemplate();
					BeanUtils.copyProperties(pt22, newpt, new String[] { "id",
							"procardTemplate", "processPRNScore",
							"taSopGongwei", "processFuLiaoTemplate" });
					// -----辅料开始---------
					if (newpt.getIsNeedFuliao() != null
							&& newpt.getIsNeedFuliao().equals("yes")) {
						Set<ProcessFuLiaoTemplate> fltmpSet = pt22
								.getProcessFuLiaoTemplate();
						if (fltmpSet.size() > 0) {
							Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
							for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
								ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
								BeanUtils
										.copyProperties(fltmp, pinfoFl,
												new String[] { "id",
														"processTemplate" });
								if (pinfoFl.getQuanzhi1() == null) {
									pinfoFl.setQuanzhi1(1f);
								}
								if (pinfoFl.getQuanzhi2() == null) {
									pinfoFl.setQuanzhi2(1f);
								}
								pinfoFl.setProcessTemplate(newpt);
								pinfoFlSet.add(pinfoFl);
							}
							newpt.setProcessFuLiaoTemplate(pinfoFlSet);
						}
					}
					// -----辅料结束---------
					newpt.setProcardTemplate(oldpt1);
					ptSet1.add(newpt);
					oldpt1.setProcessTemplate(ptSet1);
					b = b & totalDao.save(newpt);
				}
				return b;
			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean updateProcard(Integer id) {
		if (id != null) {
			boolean b = true;
			ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
			if (pt != null && pt.getMarkId() != null) {
				Set<ProcessTemplate> processSet1 = pt.getProcessTemplate();
				List<Integer> processNOs1 = new ArrayList<Integer>();
				if (processSet1.size() > 0) {
					for (ProcessTemplate process1 : processSet1) {
						processNOs1.add(process1.getProcessNO());// 获取修改后的所有的工序号
					}
				}
				// List<ProcardTemplate> ptList = totalDao.query(
				// "from ProcardTemplate where productStyle=? and markId='"
				// + pt.getMarkId() + "' and id!=" + id
				// + " and procardStyle='" + pt.getProcardStyle()
				// + "'", pt.getProductStyle());
				List<Procard> proList = totalDao
						.query(
								"from Procard where productStyle=? and status  in('初始','已发卡','已发料','领工序') and markId='"
										+ pt.getMarkId()
										+ "' and procardStyle='"
										+ pt.getProcardStyle() + "'", pt
										.getProductStyle());
				// if (ptList.size() > 0) {// 同步procardTemplate
				// for (ProcardTemplate pt2 : ptList) {
				// // ProcardTemplate fp=(ProcardTemplate)
				// // totalDao.getObjectById(ProcardTemplate.class,
				// // pt2.getFatherId());
				// // Set<ProcardTemplate> pSet=fp.getProcardTSet();
				// // Set<ProcardTemplate> pSet2=new
				// // HashSet<ProcardTemplate>();
				// // for(ProcardTemplate sonp:pSet){
				// // if(sonp.getId().equals(pt2.getId())){
				// // pSet2.add(sonp);
				// // }
				// // }
				// // pSet.removeAll(pSet2);
				// // b=b&totalDao.delete(pt2);
				// // //pt2.setProcardTemplate(null);
				// // b=b&saveCopyProcard(fp,pt);
				// // if (pt.getProcardStyle() != null
				// // && pt.getProcardStyle().equals("自制")) {
				// if (pt.getProcardStyle().equals("外购")) {
				// BeanUtils.copyProperties(pt, pt2, new String[] {
				// "id", "rootId", "fatherId", "belongLayer",
				// "procardStyle", "addDateTime",
				// "procardTemplate", "procardTSet",
				// "processTemplate", "zhUsers", "corrCount",
				// "quanzi1", "quanzi2" });
				// } else {
				// BeanUtils
				// .copyProperties(pt, pt2, new String[] {
				// "id", "rootId", "fatherId",
				// "belongLayer", "procardStyle",
				// "addDateTime", "procardTemplate",
				// "procardTSet", "processTemplate",
				// "zhUsers", "corrCount" });
				// }
				// // } else {
				// // BeanUtils.copyProperties(pt, pt2, new String[] {
				// // "id", "rootId", "fatherId", "belongLayer",
				// // "quanzi1", "quanzi2", "addDateTime",
				// // "procardTemplate", "procardTSet",
				// // "processTemplate", "zhUsers" });
				// // }
				// Set<ProcessTemplate> processSet2 = pt2
				// .getProcessTemplate();// 循环获取其他模板的工序
				// List<Integer> processNOs2 = new ArrayList<Integer>();
				// if (processSet2.size() > 0) {
				// for (ProcessTemplate process21 : processSet2) {
				// processNOs2.add(process21.getProcessNO());// 循环获取其他模板的所有的工序号
				// }
				// }
				// List<Integer> toupdate = new ArrayList<Integer>();//
				// 存储与修改有的工序相比都含有的工序
				// List<Integer> toadd = new ArrayList<Integer>();//
				// 存储与修改有的工序相比少的工序
				// List<Integer> todelte = new ArrayList<Integer>();//
				// 存储与修改有的工序相比多的工序
				// for (Integer no1 : processNOs1) {
				// if (processNOs2.contains(no1)) {
				// toupdate.add(no1);
				// } else {
				// toadd.add(no1);
				// }
				// }
				// for (Integer no2 : processNOs2) {
				// if (!processNOs1.contains(no2)) {
				// todelte.add(no2);
				// }
				// }
				// if (toupdate.size() > 0) {// 修改在修改列表中存在的工序号的工序
				// for (ProcessTemplate process25 : processSet1) {
				// if (process25.getProcessNO() != null
				// && toupdate.contains(process25
				// .getProcessNO())) {
				// for (ProcessTemplate process26 : processSet2) {
				// if (process26.getProcessNO() != null
				// && process26
				// .getProcessNO()
				// .equals(
				// process25
				// .getProcessNO())) {
				// //
				// -----------------------processInfor辅料开始--------------------
				// // 删除原有的辅料
				// Set<ProcessFuLiaoTemplate> ifFuliaoSet = process26
				// .getProcessFuLiaoTemplate();
				// process26
				// .setProcessFuLiaoTemplate(null);
				// if (ifFuliaoSet != null
				// && ifFuliaoSet.size() > 0) {
				// for (ProcessFuLiaoTemplate ifFuLiao : ifFuliaoSet) {
				// ifFuLiao
				// .setProcessTemplate(null);
				// totalDao.delete(ifFuLiao);
				// }
				// }
				// if (process25.getIsNeedFuliao() != null
				// && process25
				// .getIsNeedFuliao()
				// .equals("yes")) {
				// Set<ProcessFuLiaoTemplate> fltmpSet = process25
				// .getProcessFuLiaoTemplate();
				// if (fltmpSet.size() > 0) {
				// Set<ProcessFuLiaoTemplate> pinfoFlSet = new
				// HashSet<ProcessFuLiaoTemplate>();
				// for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
				// ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
				// BeanUtils
				// .copyProperties(
				// fltmp,
				// pinfoFl,
				// new String[] {
				// "id",
				// "processTemplate" });
				// if (pinfoFl
				// .getQuanzhi1() == null) {
				// pinfoFl
				// .setQuanzhi1(1f);
				// }
				// if (pinfoFl
				// .getQuanzhi2() == null) {
				// pinfoFl
				// .setQuanzhi2(1f);
				// }
				// pinfoFl
				// .setProcessTemplate(process26);
				// pinfoFlSet.add(pinfoFl);
				// }
				// process26
				// .setProcessFuLiaoTemplate(pinfoFlSet);
				// }
				// }
				// //
				// -----------------------processInfor辅料结束--------------------
				// BeanUtils
				// .copyProperties(
				// process25,
				// process26,
				// new String[] {
				// "id",
				// "procardTemplate",
				// "taSopGongwei",
				// "processFuLiaoTemplate" });
				// process26.setProcardTemplate(pt2);
				// b = b & totalDao.update(process26);
				// }
				// }
				// }
				//
				// }
				// }
				// if (todelte.size() > 0) {// 删除在删除列表中存在的工序号的工序
				// Set<ProcessTemplate> deletSet = new
				// HashSet<ProcessTemplate>();
				// for (ProcessTemplate process22 : processSet2) {
				// if (process22.getProcessNO() != null
				// && todelte.contains(process22
				// .getProcessNO())) {
				// deletSet.add(process22);
				// }
				// }
				// processSet2.removeAll(deletSet);
				// for (ProcessTemplate delete : deletSet) {
				// delete.setProcardTemplate(null);
				// delete.setTaSopGongwei(null);
				// // 删除原有的辅料
				// Set<ProcessFuLiaoTemplate> ifFuliaoSet = delete
				// .getProcessFuLiaoTemplate();
				// delete.setProcessFuLiaoTemplate(null);
				// if (ifFuliaoSet != null
				// && ifFuliaoSet.size() > 0) {
				// for (ProcessFuLiaoTemplate ifFuLiao : ifFuliaoSet) {
				// ifFuLiao.setProcessTemplate(null);
				// totalDao.delete(ifFuLiao);
				// }
				// }
				// b = b & totalDao.delete(delete);
				// }
				// }
				// if (toadd.size() > 0) {// 添加在添加列表中存在的工序号的工序
				// for (ProcessTemplate process23 : processSet1) {
				// if (process23.getProcessNO() != null
				// && toadd.contains(process23
				// .getProcessNO())) {
				// ProcessTemplate process24 = new ProcessTemplate();
				// // -----------辅料开始----------
				// if (process23.getIsNeedFuliao() != null
				// && process23.getIsNeedFuliao()
				// .equals("yes")) {
				// Set<ProcessFuLiaoTemplate> fltmpSet = process23
				// .getProcessFuLiaoTemplate();
				// if (fltmpSet.size() > 0) {
				// Set<ProcessFuLiaoTemplate> pinfoFlSet = new
				// HashSet<ProcessFuLiaoTemplate>();
				// for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
				// ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
				// BeanUtils
				// .copyProperties(
				// fltmp,
				// pinfoFl,
				// new String[] {
				// "id",
				// "processTemplate" });
				// if (pinfoFl.getQuanzhi1() == null) {
				// pinfoFl.setQuanzhi1(1f);
				// }
				// if (pinfoFl.getQuanzhi2() == null) {
				// pinfoFl.setQuanzhi2(1f);
				// }
				// pinfoFl
				// .setProcessTemplate(process24);
				// pinfoFlSet.add(pinfoFl);
				// }
				// process24
				// .setProcessFuLiaoTemplate(pinfoFlSet);
				// }
				// }
				// // -----------辅料结束----------
				// BeanUtils.copyProperties(process23,
				// process24, new String[] { "id",
				// "procardTemplate",
				// "taSopGongwei",
				// "processFuLiaoTemplate" });
				// process24.setProcardTemplate(pt2);
				// b = b & totalDao.save(process24);
				// processSet2.add(process24);
				// }
				// }
				// }
				//
				// //
				// if(pt.getProcardStyle()!=null&&pt.getProcardStyle().equals("组合")){//模板里总成不会同件号所以不用判断
				// // Set<ProcardTemplate> procardSet=
				// // pt2.getProcardTSet();
				// // if(procardSet!=null&&procardSet.size()>0){
				// // for(ProcardTemplate ptNext:procardSet){
				// // ProcardTemplate ptNext2=ptNext;
				// // ptNext=n;
				// // }
				// // }
				// // }
				// if (b) {
				// pt2.setProcessTemplate(processSet2);
				// b = b & totalDao.update(pt2);
				// }
				// if (b && pt2.getProcardStyle() != null
				// && pt2.getProcardStyle().equals("组合")) {
				// Set<ProcardTemplate> ptSet = pt2.getProcardTSet();
				// pt2.setProcardTSet(null);
				// b = b & totalDao.update(pt2);
				// if (ptSet != null && ptSet.size() > 0) {
				// for (ProcardTemplate pt3 : ptSet) {
				// totalDao.delete(pt3);
				// }
				//
				// }
				// Set<ProcardTemplate> ptSet2 = pt.getProcardTSet();
				// if (ptSet2 != null && ptSet2.size() > 0) {
				// for (ProcardTemplate pt4 : ptSet2) {
				// b = b & saveCopyProcard(pt2, pt4);
				// }
				// }
				// }
				// }
				// }
				if (proList.size() > 0) {// 同步procard
					for (Procard pd : proList) {
						// BeanUtils.copyProperties(pt, pd, new
						// String[]{"id","rootId","fatherId","belongLayer","quanzi1","quanzi2"
						// ,"addDateTime","procardTemplate","procardTSet","processTemplate","zhUsers"});
						// BeanUtils.copyProperties(pt, pd, new
						// String[]{"id","rootId","fatherId","belongLayer","quanzi1","quanzi2","procardTime","procard","procardSet"
						// ,"processInforSet","procardPartsSet","procardPro","oneProcardBonus","status"});
						if (pd.getStatus().equals("初始")
								|| pd.getStatus().equals("已发卡")) {
							// if (pt.getProcardStyle() != null
							// && pt.getProcardStyle().equals("自制")) {

							if (pt.getProcardStyle() != null
									&& pt.getProcardStyle().equals("外购")
									&& pd.getNeedProcess() != null
									&& pd.getNeedProcess().equals("yes")
									&& (pt.getNeedProcess() == null || pt
											.getNeedProcess().equals("no"))) {// 外购件原需要工序，现不要工序
								String sumOld = "select sum(filnalCount) from Procard where status ='已发卡' and markId=?";
								Object sumobj = null;
								sumobj = totalDao.getObjectByCondition(sumOld,
										pt.getMarkId());
								Float sumoldCount = 0F;
								if (sumobj != null) {
									sumoldCount = Float.parseFloat(sumobj
											.toString());
								}
								Float sumAll = sumoldCount;
								// 查询库存数量
								Object obj = totalDao
										.getObjectByCondition(
												"select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?",
												pt.getMarkId(), pt.getUnit());
								Float sumCount = 0F;
								if (obj != null) {
									sumCount = Float.parseFloat(obj.toString());
								}
								if (sumCount == null) {
									sumCount = Float
											.parseFloat(totalDao
													.getObjectByCondition(
															"select sum(goodsZhishu) from Goods where goodsMarkId=?",
															pt.getMarkId())
													.toString());
								}

								// 判断数量是否足够
								pd.setJihuoStatua("激活");
								pd.setKlNumber(pd.getFilnalCount());
								pd.setTjNumber(0F);
								pd.setMinNumber(0F);
								if (sumCount != null
										&& sumCount > 0
										&& (pt.getNeedProcess() == null || pt
												.getNeedProcess().equals("no"))) {
									// 数量充足
									if (sumCount - sumoldCount >= 0) {
										if (sumCount - sumAll >= 0) {
											pd.setTjNumber(pd.getFilnalCount());
										} else {
											// 数量不足，
											pd.setTjNumber(sumAll - sumCount);
										}

									}
									Float minNumber = pd.getTjNumber()
											/ pd.getQuanzi2() * pd.getQuanzi1();
									if (pd.getTjNumber() == pd.getFilnalCount()) {
										minNumber = (float) Math
												.ceil(minNumber);
									}
									pd.setMinNumber(minNumber);
								}
							} else if (pt.getProcardStyle() != null
									&& pt.getProcardStyle().equals("外购")
									&& pt.getNeedProcess() != null
									&& pt.getNeedProcess().equals("yes")
									&& (pd.getNeedProcess() == null || pd
											.getNeedProcess().equals("no"))) {// 外购件原不需要工序，现要工序
								pd.setTjNumber(0f);
							}
							// } else {
							// BeanUtils.copyProperties(pt, pd, new String[] {
							// "id", "rootId", "fatherId", "status",
							// "belongLayer", "quanzi1", "quanzi2" });
							// }
							if ((pt.getLingliaostatus() == null || pt
									.getLingliaostatus().equals("是"))
									&& pd.getLingliaostatus() != null
									&& pd.getLingliaostatus().equals("否")) {
								// 不领料变领料
								if (pd.getStatus().equals("已发料")) {
									pd.setLingliaostatus("是");
									pd.setStatus("已发卡");
									pd.setKlNumber(pd.getFilnalCount());
									pd.setHascount(null);
								}

							}
							if ((pd.getLingliaostatus() == null || pd
									.getLingliaostatus().equals("是"))
									&& pt.getLingliaostatus() != null
									&& pt.getLingliaostatus().equals("否")) {
								// 领料变不领料
								if (pd.getStatus().equals("已发卡")
										|| pd.getStatus().equals("初始")) {
									pd.setLingliaostatus("否");
									pd.setStatus("已发料");
									pd.setKlNumber(pd.getFilnalCount());
									pd.setHascount(0f);
								}
							}
							if (pt.getProcardStyle() != null
									&& pt.getProcardStyle().equals("外购")) {
								BeanUtils.copyProperties(pt, pd, new String[] {
										"id", "rootId", "fatherId", "status",
										"belongLayer", "corrCount", "hascount",
										"quanzi1", "quanzi2" });
							} else {

								BeanUtils.copyProperties(pt, pd,
										new String[] { "id", "rootId",
												"fatherId", "status",
												"belongLayer", "corrCount",
												"hascount" });
							}
						}
						Set<ProcessInfor> processSet3 = pd.getProcessInforSet();// 循环获取其他模板的工序
						List<Integer> processNOs2 = new ArrayList<Integer>();
						if (processSet3.size() > 0) {
							for (ProcessInfor process31 : processSet3) {
								processNOs2.add(process31.getProcessNO());// 循环获取其他模板的所有的工序号
							}
						}
						List<Integer> toupdate = new ArrayList<Integer>();// 存储与修改有的工序相比都含有的工序
						List<Integer> toadd = new ArrayList<Integer>();// 存储与修改有的工序相比少的工序
						List<Integer> todelte = new ArrayList<Integer>();// 存储与修改有的工序相比多的工序
						for (Integer no1 : processNOs1) {
							if (processNOs2.contains(no1)) {
								toupdate.add(no1);
							} else {
								toadd.add(no1);
							}
						}
						for (Integer no2 : processNOs2) {
							if (!processNOs1.contains(no2)) {
								todelte.add(no2);
							}
						}
						if (toupdate.size() > 0) {// 修改在修改列表中存在的工序号的工序
							for (ProcessTemplate process35 : processSet1) {
								if (process35.getProcessNO() != null
										&& toupdate.contains(process35
												.getProcessNO())) {
									for (ProcessInfor process36 : processSet3) {
										if (process36.getStatus() != null
												&& (process36.getStatus()
														.equals("初始")
														|| process36
																.getStatus()
																.equals("自检") || process36
														.getStatus().equals(
																"已领"))) {
											// 工序状态为初始,为自检或者领工序时才可以修改
											if (process36.getProcessNO() != null
													&& process36
															.getProcessNO()
															.equals(
																	process35
																			.getProcessNO())) {

												if (process36.getStatus()
														.equals("已领")) {// 领工序时只可以修改几个检查项和工位信息
													process36
															.setZjStatus(process35
																	.getZjStatus());
													// process36
													// .setGongwei(process35
													// .getGongwei());
													// process36
													// .setShebeiNo(process35
													// .getShebeiNo());
													process36
															.setShebeistatus(process35
																	.getShebeistatus());// 设备验证
													process36
															.setGongzhuangstatus(process35
																	.getGongzhuangstatus());// 工装验证
													process36
															.setLiangjustatus(process35
																	.getLiangjustatus());// 量具验证
													process36
															.setGuifanstatus(process35
																	.getGuifanstatus());// 规范验证
													process36
															.setKaoqingstatus(process35
																	.getKaoqingstatus());// 考勤验证
													b = b
															& totalDao
																	.update(process36);
												} else {

													// -----------------------processInfor辅料开始--------------------
													// 删除原有的辅料
													Set<ProcessinforFuLiao> ifFuliaoSet = process36
															.getProcessinforFuLiao();
													process36
															.setProcessinforFuLiao(null);
													if (ifFuliaoSet != null
															&& ifFuliaoSet
																	.size() > 0) {
														for (ProcessinforFuLiao ifFuLiao : ifFuliaoSet) {
															ifFuLiao
																	.setProcessInfor(null);
															totalDao
																	.delete(ifFuLiao);
														}
													}
													if (process35
															.getIsNeedFuliao() != null
															&& process35
																	.getIsNeedFuliao()
																	.equals(
																			"yes")) {
														Set<ProcessFuLiaoTemplate> fltmpSet = process35
																.getProcessFuLiaoTemplate();
														if (fltmpSet.size() > 0) {
															Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
															for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
																ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
																BeanUtils
																		.copyProperties(
																				fltmp,
																				pinfoFl,
																				new String[] { "id" });
																if (pinfoFl
																		.getQuanzhi1() == null) {
																	pinfoFl
																			.setQuanzhi1(1f);
																}
																if (pinfoFl
																		.getQuanzhi2() == null) {
																	pinfoFl
																			.setQuanzhi2(1f);
																}
																pinfoFl
																		.setTotalCount(pd
																				.getFilnalCount()
																				* pinfoFl
																						.getQuanzhi2()
																				/ pinfoFl
																						.getQuanzhi1());
																pinfoFl
																		.setProcessInfor(process36);
																pinfoFl
																		.setOutCount(0f);
																pinfoFlSet
																		.add(pinfoFl);
															}
															process36
																	.setProcessinforFuLiao(pinfoFlSet);
														}
													}
													// -----------------------processInfor辅料结束--------------------
													BeanUtils
															.copyProperties(
																	process35,
																	process36,
																	new String[] { "id" });
													process36.setProcard(pd);
													b = b
															& totalDao
																	.update(process36);
												}
											}
										}
									}
								}

							}
						}
						if (todelte.size() > 0) {// 删除在删除列表中存在的工序号的工序
							Set<ProcessInfor> deletSet = new HashSet<ProcessInfor>();
							for (ProcessInfor process32 : processSet3) {
								if (process32.getStatus() != null
										&& (process32.getStatus().equals("初始") || process32
												.getStatus().equals("自检"))) {
									// 工序状态为初始或者为自检时才可以删除
									if (process32.getProcessNO() != null
											&& todelte.contains(process32
													.getProcessNO())) {
										deletSet.add(process32);
									}
								}
							}
							processSet3.removeAll(deletSet);
							for (ProcessInfor delete : deletSet) {
								// delete.setProcard(null);
								// delete.setOsWork(null);
								// delete.setProcardPro(null);
								// delete.setProductProcess(null);
								// 删除原有的辅料
								Set<ProcessinforFuLiao> ifFuliaoSet = delete
										.getProcessinforFuLiao();
								delete.setProcessinforFuLiao(null);
								if (ifFuliaoSet != null
										&& ifFuliaoSet.size() > 0) {
									for (ProcessinforFuLiao ifFuLiao : ifFuliaoSet) {
										ifFuLiao.setProcessInfor(null);
										totalDao.delete(ifFuLiao);
									}
								}
								b = b & totalDao.delete(delete);
							}
						}
						if (toadd.size() > 0) {// 添加在添加列表中存在的工序号的工序
							for (ProcessTemplate process33 : processSet1) {
								if (process33.getProcessNO() != null
										&& toadd.contains(process33
												.getProcessNO())) {
									ProcessInfor process34 = new ProcessInfor();
									BeanUtils.copyProperties(process33,
											process34, new String[] { "id" });
									// -----------------辅料开始------------------
									if (process33.getIsNeedFuliao() != null
											&& process33.getIsNeedFuliao()
													.equals("yes")) {
										process34.setIsNeedFuliao("yes");
										Set<ProcessFuLiaoTemplate> fltmpSet = process33
												.getProcessFuLiaoTemplate();
										if (fltmpSet.size() > 0) {
											Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
											for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
												ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
												BeanUtils.copyProperties(fltmp,
														pinfoFl,
														new String[] { "id" });
												if (pinfoFl.getQuanzhi1() == null) {
													pinfoFl.setQuanzhi1(1f);
												}
												if (pinfoFl.getQuanzhi2() == null) {
													pinfoFl.setQuanzhi2(1f);
												}
												pinfoFl
														.setTotalCount(pd
																.getFilnalCount()
																* pinfoFl
																		.getQuanzhi2()
																/ pinfoFl
																		.getQuanzhi1());
												pinfoFl
														.setProcessInfor(process34);
												pinfoFl.setOutCount(0f);
												pinfoFlSet.add(pinfoFl);
											}
											process34
													.setProcessinforFuLiao(pinfoFlSet);
										}
									}
									// ------------辅料结束-----
									Float klCount = 0f;// 可领数量
									Integer chaju = 0;// 工序号的差距
									if (processSet3 != null) {
										for (ProcessInfor process37 : processSet3) {// 寻找最近工序号的总数量和可领数量优先选择上条工序
											if (process37.getProcessNO() != null
													&& process33.getProcessNO() != null) {
												Integer chaju1 = process37
														.getProcessNO()
														- process33
																.getProcessNO();
												if ((chaju == 0)
														|| (chaju > 0 && chaju1 < 0)
														|| (chaju1 > 0 && chaju1 < chaju)
														|| (chaju1 < 0 && chaju1 > chaju)) {
													chaju = chaju1;
													klCount = process37
															.getTotalCount();
												}
											}
										}
									}
									process34.setTotalCount(klCount);
									process34.setStatus("初始");
									process34.setProcard(pd);
									b = b & totalDao.save(process34);
									processSet3.add(process34);
								}
							}
						}
						if (b) {
							pd.setProcessInforSet(processSet3);
							b = b & totalDao.update(pd);
						}

					}
				}

			}
			return b;
		}
		return false;
	}

	// /**
	// *
	// 次方法为备用方法功能是同步所有同件号的ProcardTemplate不管procardStyle是否相同，但是只是同步基本信息和ProcardTemplate下的工序信息，ProcardTemplate下面的其他卡片信息不管
	// */
	// @SuppressWarnings("unchecked")
	// @Override
	// public boolean updateProcard(Integer id) {
	// if (id != null) {
	// boolean b = true;
	// ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
	// ProcardTemplate.class, id);
	// if (pt != null && pt.getMarkId() != null) {
	// Set<ProcessTemplate> processSet1 = pt.getProcessTemplate();
	// List<Integer> processNOs1 = new ArrayList<Integer>();
	// if (processSet1.size() > 0) {
	// for (ProcessTemplate process1 : processSet1) {
	// processNOs1.add(process1.getProcessNO());// 获取修改后的所有的工序号
	// }
	// }
	// List<ProcardTemplate> ptList = totalDao
	// .query("from ProcardTemplate where markId='"
	// + pt.getMarkId() + "' and id!=" + id);
	// List<Procard> proList = totalDao
	// .query("from Procard where status  in('初始','已发卡','已发料','领工序') and markId='"
	// + pt.getMarkId()
	// + "'");
	// if (ptList.size() > 0) {// 同步procardTemplate
	// for (ProcardTemplate pt2 : ptList) {
	// BeanUtils.copyProperties(pt, pt2, new String[] { "id",
	// "rootId", "fatherId", "belongLayer","procardStyle",
	// "addDateTime", "procardTemplate",
	// "procardTSet", "processTemplate", "zhUsers",
	// "corrCount" });
	// Set<ProcessTemplate> processSet2 = pt2
	// .getProcessTemplate();// 循环获取其他模板的工序
	// List<Integer> processNOs2 = new ArrayList<Integer>();
	// if (processSet2.size() > 0) {
	// for (ProcessTemplate process21 : processSet2) {
	// processNOs2.add(process21.getProcessNO());// 循环获取其他模板的所有的工序号
	// }
	// }
	// List<Integer> toupdate = new ArrayList<Integer>();// 存储与修改有的工序相比都含有的工序
	// List<Integer> toadd = new ArrayList<Integer>();// 存储与修改有的工序相比少的工序
	// List<Integer> todelte = new ArrayList<Integer>();// 存储与修改有的工序相比多的工序
	// for (Integer no1 : processNOs1) {
	// if (processNOs2.contains(no1)) {
	// toupdate.add(no1);
	// } else {
	// toadd.add(no1);
	// }
	// }
	// for (Integer no2 : processNOs2) {
	// if (!processNOs1.contains(no2)) {
	// todelte.add(no2);
	// }
	// }
	// if (toupdate.size() > 0) {// 修改在修改列表中存在的工序号的工序
	// for (ProcessTemplate process25 : processSet1) {
	// if (process25.getProcessNO() != null
	// && toupdate.contains(process25
	// .getProcessNO())) {
	// for (ProcessTemplate process26 : processSet2) {
	// if (process26.getProcessNO() != null
	// && process26
	// .getProcessNO()
	// .equals(
	// process25
	// .getProcessNO())) {
	// // -----------------------processInfor辅料开始--------------------
	// //删除原有的辅料
	// // Set<ProcessFuLiaoTemplate>
	// ifFuliaoSet=process26.getProcessFuLiaoTemplate();
	// // process26.setProcessFuLiaoTemplate(null);
	// // if(ifFuliaoSet!=null&&ifFuliaoSet.size()>0){
	// // for(ProcessFuLiaoTemplate ifFuLiao:ifFuliaoSet){
	// // ifFuLiao.setProcessTemplate(null);
	// // totalDao.delete(ifFuLiao);
	// // }
	// // }
	// //
	// if(process25.getIsNeedFuliao()!=null&&process25.getIsNeedFuliao().equals("yes")){
	// // Set<ProcessFuLiaoTemplate>
	// fltmpSet=process25.getProcessFuLiaoTemplate();
	// // if(fltmpSet.size()>0){
	// // Set<ProcessFuLiaoTemplate> pinfoFlSet=new
	// HashSet<ProcessFuLiaoTemplate>();
	// // for(ProcessFuLiaoTemplate fltmp:fltmpSet){
	// // ProcessFuLiaoTemplate pinfoFl=new ProcessFuLiaoTemplate();
	// // BeanUtils.copyProperties(fltmp, pinfoFl, new String
	// []{"id","processTemplate"});
	// // if(pinfoFl.getQuanzhi1()==null){
	// // pinfoFl.setQuanzhi1(1f);
	// // }
	// // if(pinfoFl.getQuanzhi2()==null){
	// // pinfoFl.setQuanzhi2(1f);
	// // }
	// // pinfoFl.setProcessTemplate(process26);
	// // pinfoFlSet.add(pinfoFl);
	// // }
	// // process26.setProcessFuLiaoTemplate(pinfoFlSet);
	// // }
	// // }
	// // -----------------------processInfor辅料结束--------------------
	// BeanUtils.copyProperties(process25,
	// process26, new String[] {
	// "id",
	// "procardTemplate",
	// "taSopGongwei","processFuLiaoTemplate" });
	// process26.setProcardTemplate(pt2);
	// b = b & totalDao.update(process26);
	// }
	// }
	// }
	//
	// }
	// }
	// if (todelte.size() > 0) {// 删除在删除列表中存在的工序号的工序
	// Set<ProcessTemplate> deletSet = new HashSet<ProcessTemplate>();
	// for (ProcessTemplate process22 : processSet2) {
	// if (process22.getProcessNO() != null
	// && todelte.contains(process22
	// .getProcessNO())) {
	// deletSet.add(process22);
	// }
	// }
	// processSet2.removeAll(deletSet);
	// for (ProcessTemplate delete : deletSet) {
	// delete.setProcardTemplate(null);
	// delete.setTaSopGongwei(null);
	// //删除原有的辅料
	// Set<ProcessFuLiaoTemplate> ifFuliaoSet=delete.getProcessFuLiaoTemplate();
	// delete.setProcessFuLiaoTemplate(null);
	// // if(ifFuliaoSet!=null&&ifFuliaoSet.size()>0){
	// // for(ProcessFuLiaoTemplate ifFuLiao:ifFuliaoSet){
	// // ifFuLiao.setProcessTemplate(null);
	// // totalDao.delete(ifFuLiao);
	// // }
	// // }
	// b = b & totalDao.delete(delete);
	// }
	// }
	// if (toadd.size() > 0) {// 添加在添加列表中存在的工序号的工序
	// for (ProcessTemplate process23 : processSet1) {
	// if (process23.getProcessNO() != null
	// && toadd.contains(process23
	// .getProcessNO())) {
	// ProcessTemplate process24 = new ProcessTemplate();
	// // -----------辅料开始----------
	// //
	// if(process23.getIsNeedFuliao()!=null&&process23.getIsNeedFuliao().equals("yes")){
	// // Set<ProcessFuLiaoTemplate>
	// fltmpSet=process23.getProcessFuLiaoTemplate();
	// // if(fltmpSet.size()>0){
	// // Set<ProcessFuLiaoTemplate> pinfoFlSet=new
	// HashSet<ProcessFuLiaoTemplate>();
	// // for(ProcessFuLiaoTemplate fltmp:fltmpSet){
	// // ProcessFuLiaoTemplate pinfoFl=new ProcessFuLiaoTemplate();
	// // BeanUtils.copyProperties(fltmp, pinfoFl, new String
	// []{"id","processTemplate"});
	// // if(pinfoFl.getQuanzhi1()==null){
	// // pinfoFl.setQuanzhi1(1f);
	// // }
	// // if(pinfoFl.getQuanzhi2()==null){
	// // pinfoFl.setQuanzhi2(1f);
	// // }
	// // pinfoFl.setProcessTemplate(process24);
	// // pinfoFlSet.add(pinfoFl);
	// // }
	// // process24.setProcessFuLiaoTemplate(pinfoFlSet);
	// // }
	// // }
	// // -----------辅料结束----------
	// BeanUtils.copyProperties(process23,
	// process24, new String[] { "id",
	// "procardTemplate",
	// "taSopGongwei","processFuLiaoTemplate" });
	// process24.setProcardTemplate(pt2);
	// b = b & totalDao.save(process24);
	// processSet2.add(process24);
	// }
	// }
	// }
	//
	// if (b) {
	// pt2.setProcessTemplate(processSet2);
	// b = b & totalDao.update(pt2);
	// }
	// }
	// }
	//
	// }
	// return b;
	// }
	// return false;
	// }
	@Override
	public boolean delteUnderProcardTemplate(ProcardTemplate pt) {
		// TODO Auto-generated method stub
		if (pt != null) {
			boolean b = true;
			Set<ProcardTemplate> ptSet = pt.getProcardTSet();
			pt.setProcardTSet(null);
			if (ptSet != null && ptSet.size() > 0) {
				for (ProcardTemplate pt2 : ptSet) {
					b = b & delteUnderProcardTemplate(pt2);
				}
			}
			Set<ProcessTemplate> processSet = pt.getProcessTemplate();
			if (processSet != null && processSet.size() > 0) {
				for (ProcessTemplate process : processSet) {
					b = b & totalDao.delete(process);
				}
			}
			pt.setProcessTemplate(null);
			pt.setProcardTemplate(null);
			b = b & totalDao.delete(pt);
			return b;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String jingyiJisuan(Integer id, ProcardTemplate procardTem) {
		if (id != null) {
			Float singleDuration = 0F;
			Float needCapacity = 0F;
			// 总成
			ProcardTemplate rootProcard = (ProcardTemplate) totalDao
					.getObjectById(ProcardTemplate.class, id);
			if (procardTem != null && procardTem.getSingleDuration() != null) {
				singleDuration = procardTem.getSingleDuration();// 上班时长
				needCapacity = procardTem.getNeedNumber()
						/ procardTem.getNeedZhoueqi();// 客户需求产能
				rootProcard.setSingleDuration(singleDuration);
				rootProcard.setNeedCapacity(needCapacity);
			} else {
				singleDuration = rootProcard.getSingleDuration();// 上班时长
				needCapacity = rootProcard.getNeedCapacity();// 客户需求
			}

			if (rootProcard != null) {
				// 得到最大的层次
				String maxhql = "select max(belongLayer) from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除')";
				Integer belongLayer = (Integer) totalDao.getObjectByCondition(
						maxhql, rootProcard.getId());
				// 根据层次计算
				for (int i = belongLayer; i > 0; i--) {
					// 开始计算产品的单班产量
					String hql_pro = "from ProcardTemplate where rootId=? and belongLayer=? and (dataStatus is null or dataStatus!='删除')";
					List<ProcardTemplate> proList = totalDao.query(hql_pro,
							rootProcard.getId(), i);
					/** 处理外购件的产能、时长、供应商信息 **/
					Float maxTime = 0F;
					String message = "";
					boolean bool = true;
					for (ProcardTemplate procardTemplate : proList) {
						procardTemplate.setSingleDuration(singleDuration);
						procardTemplate.setNeedCapacity(needCapacity);
						if ("外购".equals(procardTemplate.getProcardStyle())) {
							// 查询供应量最大的供应商产品信息
							String hql_gys = "from GysMarkIdjiepai where markId=? and procardStyle='总成' order by capacity desc";
							GysMarkIdjiepai gysJiepai = (GysMarkIdjiepai) totalDao
									.getObjectByCondition(hql_gys,
											procardTemplate.getMarkId());
							if (gysJiepai != null) {
								if (gysJiepai.getCapacity() == null
										|| gysJiepai.getDeliveryDuration() == null) {
									message += "外购件:"
											+ procardTemplate.getMarkId()
											+ "尚未填报生产节拍!\\n";
									bool = false;
									continue;
								}
								procardTemplate.setSingleDuration(gysJiepai
										.getSingleDuration());
								procardTemplate
										.setCapacity((float) (gysJiepai
												.getCapacity()
												/ procardTemplate.getQuanzi1() * procardTemplate
												.getQuanzi2()));
								procardTemplate.setCapacitySurplus(gysJiepai
										.getCapacitySurplus());
								procardTemplate.setCapacityRatio(gysJiepai
										.getCapacityRatio());
								procardTemplate.setDeliveryDuration(gysJiepai
										.getDeliveryDuration());
								procardTemplate.setDeliveryRatio(gysJiepai
										.getDeliveryRatio());
								procardTemplate.setDeliveryPeriod(gysJiepai
										.getDeliveryPeriod());
								procardTemplate.setDeliveryAmount(gysJiepai
										.getDeliveryAmount());
								procardTemplate.setProSingleDuration(gysJiepai
										.getDeliveryDuration());
								procardTemplate.setAllJiepai(gysJiepai
										.getAllJiepai());
								procardTemplate
										.setCapacitySurplus(procardTemplate
												.getCapacity()
												- needCapacity);
								procardTemplate.setZhuserId(gysJiepai
										.getZhuserId());
								procardTemplate.setGys(gysJiepai.getGys());

								totalDao.update(procardTemplate);
								// 得到最大外购件延误时间
								maxTime = maxTime > procardTemplate
										.getDeliveryDuration() ? maxTime
										: procardTemplate.getDeliveryDuration();
							} else {
								message += "外购件:" + procardTemplate.getMarkId()
										+ "尚未填报生产节拍!\\n";
								bool = false;
								continue;
							}
						}
					}
					if (!bool) {
						return message;
					}
					/** 处理外购件的产能、时长、供应商信息结束 **/

					/** 处理自制、外委、组合、总成 **/
					for (ProcardTemplate procardTemplate : proList) {
						if ("外购".equals(procardTemplate.getProcardStyle())) {
							continue;
						}
						procardTemplate.setSingleDuration(singleDuration);
						procardTemplate.setNeedCapacity(needCapacity);

						// 获得下层最大总节拍
						String hql_minJiepai = "select max(allJiepai) from ProcardTemplate where fatherId=? and procardStyle <> '外购' and (dataStatus is null or dataStatus!='删除')";
						Object obj = totalDao.getObjectByCondition(
								hql_minJiepai, procardTemplate.getId());
						Float sumjiepai = 0F;
						if (obj != null) {
							sumjiepai = Float.parseFloat(obj.toString());
						}

						// 获得下层总延误时长
						String hql_deliveryDuration = "select sum(deliveryDuration) from ProcardTemplate where fatherId=?  and procardStyle <> '外购' and (dataStatus is null or dataStatus!='删除')";
						Object obj_deliveryDuration = totalDao
								.getObjectByCondition(hql_deliveryDuration,
										procardTemplate.getId());
						Float sumdeliveryDuration = 0F;
						if (obj_deliveryDuration != null) {
							sumdeliveryDuration = Float
									.parseFloat(obj_deliveryDuration.toString());
						}

						List<ProcessTemplate> ptList = new ArrayList<ProcessTemplate>();
						ptList.addAll(procardTemplate.getProcessTemplate());
						Float allJiepai = sumjiepai;
						Float maxJiepai = 0F;
						Float allWaiWeiDate = sumdeliveryDuration;// 生产总时长
						Float maxWaiWeiDate = 0F;// 外委最大时长
						for (int j = 0; j < ptList.size(); j++) {
							ProcessTemplate pt = ptList.get(j);
							if (pt.getIsJisuan() == null
									|| pt.getIsJisuan().equals("yes")) {// 参与计算
								if ("外委".equals(pt.getProductStyle())) {
									// 查询外委供应商填报信息
									String hql_ww = "from ProcessMarkIdZijian where gysMarkIdjiepai.markId=? and processNO=? and processName=? order by capacity desc";
									ProcessMarkIdZijian pkid = (ProcessMarkIdZijian) totalDao
											.getObjectByCondition(
													hql_ww,
													procardTemplate.getMarkId(),
													pt.getProcessNO(), pt
															.getProcessName());
									if (pkid != null
											&& pkid.getCapacity() != null
											&& pkid.getCapacity() > 0F) {
										pt.setOpcaozuojiepai(pkid
												.getOpcaozuojiepai());
										pt.setOpshebeijiepai(pkid
												.getOpshebeijiepai());
										pt.setGzzhunbeijiepai(pkid
												.getGzzhunbeijiepai());
										pt.setGzzhunbeicishu(pkid
												.getGzzhunbeicishu());
										pt.setGys(pkid.getGys());
										pt.setDeliveryDuration(pkid
												.getDeliveryDuration());
										pt.setZhuserId(pkid.getZhuserId());
									} else {
										return procardTemplate.getMarkId()
												+ " 的外委工序: "
												+ pt.getProcessName()
												+ " 尚未填报生产节拍!";
									}
								}
								Float caozuojiepai = pt.getOpcaozuojiepai() == null ? 0F
										: pt.getOpcaozuojiepai();
								Float shebeijiepai = pt.getOpshebeijiepai() == null ? 0F
										: pt.getOpshebeijiepai();
								Float zhunbeijiepai = pt.getGzzhunbeijiepai() == null ? 0F
										: pt.getGzzhunbeijiepai();
								Float zhunbeicishu = pt.getGzzhunbeicishu() == null ? 0F
										: pt.getGzzhunbeicishu();
								Float nowAllJiepai = caozuojiepai
										+ shebeijiepai + zhunbeijiepai
										* zhunbeicishu;// 总节拍
								Float rgAllJiepai = caozuojiepai
										+ zhunbeijiepai * zhunbeicishu;// 人工节拍
								pt.setAllJiepai(Float.parseFloat(String.format(
										"%.2f", nowAllJiepai)));// 更新总节拍
								// 计算工序的产能、配送比
								// 产能
								if (nowAllJiepai > 0) {
									pt
											.setCapacity(Float
													.parseFloat(String
															.format(
																	"%.0f",
																	singleDuration
																			* 3600
																			/ (nowAllJiepai + sumjiepai))));
								} else {
									pt.setCapacity(0F);
								}
								// 产能盈余
								pt.setCapacitySurplus(pt.getCapacity()
										- needCapacity);
								allJiepai += nowAllJiepai;// 节拍
								// if (j > 0) {
								// ProcessTemplate topPt = ptList.get(j - 1);//
								// 获得上一道工序
								if ("自制".equals(pt.getProductStyle())) {
									if ("yes".equals(pt.getProcessStatus())) {
										// 连续的并行工序
										// if ("yes".equals(topPt
										// .getProcessStatus())) {
										// allJiepai -= nowAllJiepai;
										// allJiepai += rgAllJiepai;
										// maxJiepai = topPt
										// .getOpshebeijiepai();
										// // 选择设备节拍更大的工序
										// if (maxJiepai < pt
										// .getOpshebeijiepai()) {
										// allJiepai -= maxJiepai;
										// maxJiepai = pt
										// .getOpshebeijiepai();
										// allJiepai += pt
										// .getOpshebeijiepai();
										// }
										allJiepai -= nowAllJiepai;
										Float maxbin = nowAllJiepai;// 并行工序最大总节拍
										Float sumbin = nowAllJiepai;// 并行工序总结拍累加和
										int n = 1;// 并行工序连续数量
										int m = 0;// 不参与计算工序数量
										while (j + n < ptList.size()) {
											ProcessTemplate pt2 = ptList.get(j
													+ n);
											if (pt2.getIsJisuan() == null
													|| pt2.getIsJisuan()
															.equals("yes")) {// 参与计算
												if (!"yes".equals(pt2
														.getProcessStatus())
														|| !"自制"
																.equals(pt2
																		.getProductStyle())) {
													break;
												}
												Float caozuojiepai2 = pt2
														.getOpcaozuojiepai() == null ? 0F
														: pt2
																.getOpcaozuojiepai();
												Float shebeijiepai2 = pt2
														.getOpshebeijiepai() == null ? 0F
														: pt2
																.getOpshebeijiepai();
												Float zhunbeijiepai2 = pt2
														.getGzzhunbeijiepai() == null ? 0F
														: pt2
																.getGzzhunbeijiepai();
												Float zhunbeicishu2 = pt2
														.getGzzhunbeicishu() == null ? 0F
														: pt2
																.getGzzhunbeicishu();
												Float nowAllJiepai2 = caozuojiepai2
														+ shebeijiepai2
														+ zhunbeijiepai2
														* zhunbeicishu2;// 总节拍
												Float rgAllJiepai2 = caozuojiepai2
														+ zhunbeijiepai2
														* zhunbeicishu2;// 人工节拍
												pt2
														.setAllJiepai(Float
																.parseFloat(String
																		.format(
																				"%.2f",
																				nowAllJiepai2)));// 更新总节拍
												// 计算工序的产能、配送比
												// 产能
												if (nowAllJiepai2 > 0) {
													pt2
															.setCapacity(Float
																	.parseFloat(String
																			.format(
																					"%.0f",
																					singleDuration
																							* 3600
																							/ (nowAllJiepai2 + sumjiepai))));
												} else {
													pt2.setCapacity(0F);
												}
												// 产能盈余
												pt2.setCapacitySurplus(pt2
														.getCapacity()
														- needCapacity);
												if (nowAllJiepai2 > maxbin) {
													maxbin = nowAllJiepai2;
												}
												sumbin += nowAllJiepai2;
												totalDao.update(pt2);
											}
											n++;
										}
										Float binjiepai = (singleDuration * 3600 * maxbin)
												/ (singleDuration * 3600
														- sumbin + maxbin);// 几道并行工序的总时长
										allJiepai += binjiepai;
										j = j + n - 1;// 工序下标跳n次因为本身会加1所以这里减去一

										// } else {
										// maxJiepai = pt.getAllJiepai();
										// }
										// else 上一道工序为不并行 （已累加 ）
									} else {
										maxJiepai = pt.getAllJiepai();
									}
									// else 不并行工序 （已累加 ）
								} else {

									// 不累加外委工序节拍
									allJiepai -= nowAllJiepai;
									allWaiWeiDate += pt.getDeliveryDuration() == null ? 0
											: pt.getDeliveryDuration();// 送货时长
									if ("yes".equals(pt.getProcessStatus())) {
										// 连续的并行工序
										// if ("外委"
										// .equals(topPt.getProductStyle())
										// && "yes".equals(topPt
										// .getProcessStatus())) {
										// // 选择节拍更大的工序
										// if (maxJiepai < pt.getAllJiepai()) {
										// // allJiepai = allJiepai
										// // - maxJiepai;
										// // maxJiepai =
										// // pt.getAllJiepai();
										// allWaiWeiDate = allWaiWeiDate
										// - maxWaiWeiDate;
										// maxWaiWeiDate = pt
										// .getDeliveryDuration();
										// } else {
										// // allJiepai = allJiepai
										// // - pt.getAllJiepai();
										// allWaiWeiDate = allWaiWeiDate
										// - pt
										// .getDeliveryPeriod();
										//
										// }
										// } else {
										// maxJiepai = pt
										// .getDeliveryDuration();
										// maxWaiWeiDate = pt
										// .getDeliveryDuration();
										// }
										allJiepai -= nowAllJiepai;
										Float maxbin = nowAllJiepai;// 并行工序最大总节拍
										Float sumbin = nowAllJiepai;// 并行工序总结拍累加和
										int n = 1;// 并行工序连续数量
										while (j + n < ptList.size()) {
											ProcessTemplate pt2 = ptList.get(j
													+ n);
											if (pt2.getIsJisuan() == null
													|| pt2.getIsJisuan()
															.equals("yes")) {// 参与计算
												if (!"yes".equals(pt2
														.getProcessStatus())
														|| !"外委"
																.equals(pt2
																		.getProductStyle())) {
													break;
												}
												// 查询外委供应商填报信息
												String hql_ww = "from ProcessMarkIdZijian where gysMarkIdjiepai.markId=? and processNO=? and processName=? order by capacity desc";
												ProcessMarkIdZijian pkid = (ProcessMarkIdZijian) totalDao
														.getObjectByCondition(
																hql_ww,
																procardTemplate
																		.getMarkId(),
																pt2
																		.getProcessNO(),
																pt2
																		.getProcessName());
												if (pkid != null) {
													pt2
															.setOpcaozuojiepai(pkid
																	.getOpcaozuojiepai());
													pt2
															.setOpshebeijiepai(pkid
																	.getOpshebeijiepai());
													pt2
															.setGzzhunbeijiepai(pkid
																	.getGzzhunbeijiepai());
													pt2
															.setGzzhunbeicishu(pkid
																	.getGzzhunbeicishu());
													pt2.setGys(pkid.getGys());
													pt2
															.setDeliveryDuration(pkid
																	.getDeliveryDuration());
													pt2.setZhuserId(pkid
															.getZhuserId());
												} else {
													return procardTemplate
															.getMarkId()
															+ " 的外委工序: "
															+ pt2
																	.getProcessName()
															+ " 尚未填报生产节拍!";
												}
												Float caozuojiepai2 = pt2
														.getOpcaozuojiepai() == null ? 0F
														: pt
																.getOpcaozuojiepai();
												Float shebeijiepai2 = pt2
														.getOpshebeijiepai() == null ? 0F
														: pt
																.getOpshebeijiepai();
												Float zhunbeijiepai2 = pt2
														.getGzzhunbeijiepai() == null ? 0F
														: pt
																.getGzzhunbeijiepai();
												Float zhunbeicishu2 = pt2
														.getGzzhunbeicishu() == null ? 0F
														: pt
																.getGzzhunbeicishu();
												Float nowAllJiepai2 = caozuojiepai2
														+ shebeijiepai2
														+ zhunbeijiepai2
														* zhunbeicishu2;// 总节拍
												Float rgAllJiepai2 = caozuojiepai2
														+ zhunbeijiepai2
														* zhunbeicishu2;// 人工节拍
												pt2
														.setAllJiepai(Float
																.parseFloat(String
																		.format(
																				"%.2f",
																				nowAllJiepai2)));// 更新总节拍
												// 计算工序的产能、配送比
												// 产能
												if (nowAllJiepai2 > 0) {
													pt2
															.setCapacity(Float
																	.parseFloat(String
																			.format(
																					"%.0f",
																					singleDuration
																							* 3600
																							/ (nowAllJiepai2 + sumjiepai))));
												} else {
													pt2.setCapacity(0F);
												}
												// 产能盈余
												pt2.setCapacitySurplus(pt2
														.getCapacity()
														- needCapacity);
												if (nowAllJiepai2 > maxbin) {
													maxbin = nowAllJiepai2;
												}
												sumbin += nowAllJiepai2;
												totalDao.update(pt2);
											}
											n++;
										}
										Float binjiepai = (singleDuration * 3600 * maxbin)
												/ (singleDuration * 3600
														- sumbin + maxbin);// 几道并行工序的总时长
										allJiepai += binjiepai;
										j = j + n - 1;// 工序下标跳n次因为本身会加1所以这里减去一
										// else 上一道工序为不并行 （已累加 ）
									} else {
										maxJiepai = pt.getDeliveryDuration();
										maxWaiWeiDate = pt
												.getDeliveryDuration();
									}
									// else 不并行工序 （已累加 ）

								}
								// } else {
								// maxJiepai = pt.getDeliveryDuration();
								// maxWaiWeiDate = pt.getDeliveryDuration();
								// }
								totalDao.update(pt);
							}
						}

						// 总节拍累加权值
						procardTemplate.setAllJiepai(allJiepai
								* (procardTemplate.getCorrCount() == null ? 1
										: procardTemplate.getCorrCount()));

						// allWaiWeiDate += allJiepai / 3600;//累加外委工序生产时长
						procardTemplate.setDeliveryDuration(allWaiWeiDate);
						if (allJiepai > 0) {
							float capa = singleDuration * 3600 / allJiepai;
							int a = (int) Math.ceil(capa);
							procardTemplate.setCapacity((float) a);
						} else {
							procardTemplate.setCapacity(0F);
						}
						String hql = "from ProcessTemplate where procardTemplate.id=? and capacitySurplus<0 and isJisuan = 'yes' and (dataStatus is null or dataStatus!='删除')";
						int noNum = totalDao.getCount(hql, procardTemplate
								.getId());
						procardTemplate.setNoNum(noNum);
						totalDao.update(procardTemplate);

						// 如果是总成，计算单批最大数量
						if ("总成".equals(procardTemplate.getProcardStyle())) {
							/** min算法 **/
							// 自制件最小产能
							Float minZz = procardTemplate.getCapacity();

							// 计算外委最小产能
							String hql_minWw = " select min(capacity) from ProcessTemplate where productStyle='外委' and capacity is not null and procardTemplate.id in (select id from ProcardTemplate where rootId=?)";
							Float minWw = (Float) totalDao.query(hql_minWw,
									procardTemplate.getId()).get(0);
							if (minWw == null) {
								minWw = minZz;
							}

							// 计算外购最小产能
							String hql_minWg = " select min(capacity) from ProcardTemplate where rootId=? and procardStyle='外购' and (dataStatus is null or dataStatus!='删除')";
							Float minWg = (Float) totalDao.query(hql_minWg,
									procardTemplate.getId()).get(0);
							if (minWg == null) {
								minWg = minZz;
							}

							// 通过比对得到最小批次数量
							float pici = minZz > minWw ? minWw : minZz;
							pici = pici > minWg ? minWg : pici;

							int minPici = (int) Math.ceil(pici);

							procardTemplate.setMaxCount((float) minPici);

							/*** 计算最大批次数量 ***/
							// 如果自制产能最低
							if (minPici == minZz) {
								/*** 根据延误时长得到单批次最大数量 **/
								Float maxHour = procardTemplate
										.getDeliveryDuration();
								// 比较外委时间和外购时间，取最大时间
								if (maxTime > maxHour) {
									maxHour = maxTime;
								}
								if (maxTime > 0) {
									float maxNumber = maxHour * 3600
											/ procardTemplate.getAllJiepai();
									// 通过比对得到最小批次数量
									pici = maxNumber > minWw ? minWw
											: maxNumber;
									pici = pici > minWg ? minWg : pici;
									minPici = (int) Math.ceil(pici);
									procardTemplate
											.setMaxCount((float) minPici);
								}
							}

							// 更新下层所有件号的最大数量
							String hql_upMax = "update ProcardTemplate set maxCount=corrCount*"
									+ minPici
									+ " where rootId=? and procardStyle<>'总成' and (dataStatus is null or dataStatus!='删除')";
							totalDao.createQueryUpdate(hql_upMax, null,
									procardTemplate.getId());
							String hql_upMaxw = "update ProcardTemplate set maxCount="
									+ minPici
									+ "/quanzi1*quanzi2 where rootId=? and procardStyle='外购' and (dataStatus is null or dataStatus!='删除')";
							totalDao.createQueryUpdate(hql_upMaxw, null,
									procardTemplate.getId());

							// 时长算法
							// float capa =
							// procardTemplate.getDeliveryDuration()
							// * 3600 / allJiepai;
							// int a = (int) Math.ceil(capa);
							// procardTemplate.setMaxCount((float) a);
							// // 更新下层所有件号的最大数量
							// String hql_upMax =
							// "update ProcardTemplate set maxCount=corrCount*"
							// + a
							// + " where rootId=? and procardStyle<>'总成'";
							// totalDao.createQueryUpdate(hql_upMax, null,
							// procardTemplate.getId());
							// String hql_upMaxw =
							// "update ProcardTemplate set maxCount="
							// + a
							// +
							// "/quanzi1*quanzi2 where rootId=? and procardStyle='外购'";
							// totalDao.createQueryUpdate(hql_upMaxw, null,
							// procardTemplate.getId());
						}
					}
					// /** 计算所有外购件的周期批次 **/
					// // 外购周期(因为外购件是同步生产，所以得到最大的送货周期作为外购的周期批次)
					// String hql_wai_zhou =
					// "select max(deliveryPeriod) from ProcardTemplate where procardStyle='外购'";
					// Integer wai_zhou = (Integer) totalDao
					// .getObjectByCondition(hql_wai_zhou);

				}

				// /*** 计算批次周期 ****/
				// // 总成
				// rootProcard = (ProcardTemplate) totalDao.getObjectById(
				// ProcardTemplate.class, id);
				// Float zongCapacity = rootProcard.getCapacity();
				// totalDao.update(rootProcard);
			}
		}
		return "";
	}

	/***
	 *计算每的其他数据(产能比、送货量、生产时间)
	 * 
	 * @param id
	 * @param procardTem
	 * @return
	 */
	@Override
	public String jingyiJisuan2(Integer id, ProcardTemplate procardTem) {
		if (id != null) {
			Float singleDuration = 0F;
			Float needCapacity = 0F;
			// 总成
			ProcardTemplate rootProcard = (ProcardTemplate) totalDao
					.getObjectById(ProcardTemplate.class, id);
			if (procardTem != null && procardTem.getSingleDuration() != null) {
				singleDuration = procardTem.getSingleDuration();// 上班时长
				needCapacity = procardTem.getNeedCapacity();// 客户需求
				rootProcard.setSingleDuration(singleDuration);
				rootProcard.setNeedCapacity(needCapacity);
			} else {
				singleDuration = rootProcard.getSingleDuration();// 上班时长
				needCapacity = rootProcard.getNeedCapacity();// 客户需求
			}
			if (rootProcard != null) {
				// 得到最大的层次
				String maxhql = "select max(belongLayer) from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除')";
				Integer belongLayer = (Integer) totalDao.getObjectByCondition(
						maxhql, rootProcard.getId());
				// 根据层次计算
				for (int i = belongLayer; i > 0; i--) {
					/** 计算本层的其他数据(产能比、送货量、生产时间) **/
					// 得到最小产能
					String hql_mincanneng = "select min(capacity) from ProcessTemplate where procardTemplate.id in "
							+ "(select id from ProcardTemplate where rootId=? and belongLayer=?) and productStyle='自制' and (dataStatus is null or dataStatus!='删除')";
					Float capacity_min = (Float) totalDao.query(hql_mincanneng,
							rootProcard.getId(), i).get(0);
					if (capacity_min != null && capacity_min > 0) { // 更新产能比、送货量、生产时间
						String sql_cannengbi = "update ProcessTemplate set capacityRatio=Convert(decimal(18,2),capacity/"
								+ capacity_min
								+ "),deliveryAmount=deliveryPeriod*capacity,proSingleDuration="
								+ singleDuration
								+ "*capacity/"
								+ capacity_min
								+ " where "
								+ "procardTemplate.id in(select id from ProcardTemplate where rootId=? and belongLayer=?)";
						totalDao.createQueryUpdate(sql_cannengbi, null,
								rootProcard.getId(), i); // 更新外委生产时间
						String sql_chengchan = "update ProcessTemplate set proSingleDuration="
								+ singleDuration
								+ "*capacityRatio where procardTemplate.id in"
								+ "(select id from ProcardTemplate where rootId=? and belongLayer=?)";
						totalDao.createQueryUpdate(sql_chengchan, null,
								rootProcard.getId(), i);
					}

				}
				rootProcard.setJisuanStatus("ok");
				totalDao.update(rootProcard);
			}
		}
		return "";
	}

	@Override
	public boolean saveMoveProcardTemplate(Integer moveId, Integer targetId) {
		// TODO Auto-generated method stub
		if (moveId != null && targetId != null) {
			ProcardTemplate move = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, moveId);
			ProcardTemplate target = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, targetId);
			if (!target.getProcardStyle().equals("总成")) {
				// 将移动的序号设置比目标大一
				Set<ProcardTemplate> sonSet = target.getProcardTemplate()
						.getProcardTSet();
				for (ProcardTemplate son : sonSet) {
					if (son.getXuhao() == null) {
						son.setXuhao(1f);
					}
					if (move.getXuhao() == null) {
						move.setXuhao(1f);
					}
					if (son.getXuhao() < target.getXuhao()
							|| son.getXuhao() > move.getXuhao()) {
						continue;
					}
					if ((son.getXuhao().equals(target.getXuhao()) || son
							.getXuhao() > target.getXuhao())
							&& !son.getId().equals(targetId)) {
						son.setXuhao(son.getXuhao() + 1);
					}
					totalDao.update(son);
				}
				move.setXuhao(target.getXuhao() + 1);
				if (target.getProcardTemplate().getId().equals(
						move.getProcardTemplate().getId())) {
					// 在同一父卡下只改变序号
					return totalDao.update(move);
				} else {
					// 不在同一父卡下先改变序号然后为移动到目标父卡下做准备（将目标设置为目标父卡）
					totalDao.update(move);
					target = target.getProcardTemplate();
				}
			} else {
				Float maxXuHao = (Float) totalDao
						.getObjectByCondition(
								"select max(xuhao) from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')",
								targetId);
				if (maxXuHao == null) {
					move.setXuhao(1f);
				} else {
					move.setXuhao(maxXuHao + 1);
				}
			}
			if (move != null && target != null) {// 将移动的对象移动到目标下
				boolean b = true;
				move.setFatherId(target.getId());
				Integer belongLayer = 1;
				if (target.getBelongLayer() != null) {
					belongLayer += target.getBelongLayer();
				}
				move.setBelongLayer(belongLayer);
				move.setProcardTemplate(target);
				b = b & totalDao.update(move);
				Set<ProcardTemplate> ptSet = move.getProcardTSet();
				if (ptSet != null && ptSet.size() > 0) {
					for (ProcardTemplate pt : ptSet) {
						b = b & updateBelongLayer(pt, belongLayer);
					}

				}
				return b;
			}
		}
		return false;
	}

	public boolean updateBelongLayer(ProcardTemplate pt, Integer belongLayer) {
		boolean b = true;
		belongLayer++;
		pt.setBelongLayer(belongLayer);
		b = b & totalDao.update(pt);
		Set<ProcardTemplate> ptSet = pt.getProcardTSet();
		if (b & ptSet != null && ptSet.size() > 0) {
			for (ProcardTemplate ptSon : ptSet) {
				b = b & updateBelongLayer(ptSon, belongLayer);
			}
			return b;
		} else {
			return b;
		}
	}

	@Override
	public String updateMarkId(Integer id, String markId) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (id != null && markId != null) {
			ProcardTemplate old = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
			if (old != null) {
				if (old.getProcardTemplate() == null) {
					return "总成不能直接替换零件!";
				}
				ProcardTemplate oldfather = old.getProcardTemplate();
				String banbenSql = "";
				if (oldfather.getBanBenNumber() == null
						&& oldfather.getBanBenNumber().length() == 0) {
					banbenSql = " and (banBenNumber is null or banBenNumber ='')";
				} else {
					banbenSql = " and (banBenNumber='"
							+ oldfather.getBanBenNumber() + "')";
				}
				List<ProcardTemplate> sameList = totalDao
						.query(
								"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus !='删除')"
										+ " and procardTemplate.id in(select id from ProcardTemplate where markId=? and "
										+ banbenSql + " and productStyle=? )",
								old.getMarkId(), oldfather.getMarkId(),
								oldfather.getProductStyle());
				if (sameList != null && sameList.size() > 0) {
					boolean b = true;
					for (ProcardTemplate pt : sameList) {
						ProcardTemplate pt2 = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where (banbenStatus is null or banbenStatus='' or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除') and markId=? and productStyle=?",
										markId, pt.getProductStyle());
						if (pt2 == null) {
							return "对不起没有找到替换零件!";
						}
						ProcardTemplate father = pt.getProcardTemplate();
						if (father == null) {
							return "对不起没有找到被替换零件的上层零件!";
						}
						String msg = checkUpdatelimt(father, null);
						if (!msg.equals("true")) {
							return msg;
						}
						// 添加替换下层
						String oldProcardStyle = pt2.getProcardStyle();
						Float xuhao = pt2.getXuhao();
						Float quanzi1 = pt2.getQuanzi1();
						Float quanzi2 = pt2.getQuanzi2();
						Float corrCount = pt2.getCorrCount();
						pt2.setXuhao(pt.getXuhao());
						if (oldProcardStyle.equals("总成")) {
							pt2.setProcardStyle("自制");
						}
						if (pt.getProcardStyle().equals("外购")
								&& pt2.getProcardStyle().equals("外购")) {
							pt2.setQuanzi1(pt.getQuanzi1());
							pt2.setQuanzi2(pt.getQuanzi2());
						} else if (pt.getProcardStyle().equals("外购")
								&& !pt2.getProcardStyle().equals("外购")) {
							pt2.setCorrCount(pt.getQuanzi2() / pt.getQuanzi1());
						} else if (!pt.getProcardStyle().equals("外购")
								&& pt2.getProcardStyle().equals("外购")) {
							pt2.setQuanzi1(1f);
							pt2.setQuanzi2(pt.getCorrCount());
						} else {
							pt2.setCorrCount(pt.getCorrCount());
						}
						if (pt.getBiaochu() != null
								&& pt.getBiaochu().length() > 0) {
							pt2.setBiaochu(pt.getBiaochu());
						}
						// 重新取一遍为了加载关联子层
						Integer addId = saveCopyProcard(father, pt2, pt
								.getProductStyle());
						ProcardTemplate newpt = (ProcardTemplate) totalDao
								.getObjectById(ProcardTemplate.class, addId);
						// 添加修改日志
						if (newpt != null) {
							ProcardTemplateChangeLog.addchangeLog(totalDao,
									father, newpt, "子件", "增加", user, nowtime);
						}
						pt2.setProcardStyle(oldProcardStyle);
						pt2.setXuhao(xuhao);
						pt2.setQuanzi1(quanzi1);
						pt2.setQuanzi2(quanzi2);
						pt2.setCorrCount(corrCount);
						totalDao.clear();
						// 删除被替换下层
						List<ProcardTemplate> sonList = totalDao
								.query(
										"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')  and productStyle=? and  procardTemplate.id in(select id from ProcardTemplate where markId=(select markId from ProcardTemplate where id=?))",
										pt.getMarkId(), pt.getProductStyle(),
										father.getId());
						if (sonList != null && sonList.size() > 0) {
							for (int j = (sonList.size() - 1); j >= 0; j--) {
								ProcardTemplate procardt = sonList.get(j);
								if ("外购".equals(procardt.getProcardStyle())) {
									List<ProcessAndWgProcardTem> proAndwgptList = totalDao
											.query(
													" from ProcessAndWgProcardTem where procardMarkId = ? and wgprocardMardkId =? ",
													procardt
															.getProcardTemplate()
															.getMarkId(),
													procardt.getMarkId());
									if (proAndwgptList != null
											&& proAndwgptList.size() > 0) {
										for (ProcessAndWgProcardTem processAndWgProcardTem : proAndwgptList) {
											totalDao
													.delete(processAndWgProcardTem);
										}
									}
								}
								if (j == (sonList.size() - 1)) {
									// 添加修改日志
									ProcardTemplateChangeLog.addchangeLog(
											totalDao, father, pt, "子件", "删除",
											user, nowtime);
								}
								procardt.setDataStatus("删除");
								procardt.setProcardTemplate(null);
								b = b & totalDao.update(procardt);
								b = b & tbDownDataStatus(procardt);
							}
						}

					}
					return b + "";
				}
			}

		}
		return "对不起没有找到被替换零件!";
	}

	@Override
	public List<String> getAllMarkId() {
		// TODO Auto-generated method stub
		return totalDao.query("select markId from ProcardTemplate");
	}

	@Override
	public int updateFk() {
		// TODO Auto-generated method stub
		List<ProcardTemplate> ptList = totalDao
				.query("from ProcardTemplate where fk_ProcardTId is null and procardStyle!='总成' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')");
		if (ptList != null && ptList.size() > 0) {
			for (ProcardTemplate pt : ptList) {
				ProcardTemplate ptf = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, pt.getFatherId());
				if (ptf == null) {
					continue;
				}
				pt.setProcardTemplate(ptf);
				Set<ProcardTemplate> ptSet = ptf.getProcardTSet();
				if (ptSet == null) {
					ptSet = new HashSet<ProcardTemplate>();
				}
				ptSet.add(pt);
				ptf.setProcardTSet(ptSet);
				totalDao.update(ptf);
			}
		}
		return 1;
		// return totalDao
		// .createQueryUpdate(
		// null,
		// "update ta_sop_w_ProcardTemplate set fk_ProcardTId=fatherId where fk_ProcardTId is null and procardStyle!='总成'",
		// null);
	}

	@Override
	public boolean updateBomMaxCount(ProcardTemplate procardTem, Float maxCount) {
		// TODO Auto-generated method stub
		boolean b = true;
		List<ProcardTemplate> sameMarkIdList = totalDao
				.query(
						"from ProcardTemplate where markId=? and id !=? and (banbenStatus='默认' or banbenStatus is null)",
						procardTem.getMarkId(), procardTem.getId());
		// System.out.println(procardTem.getProcardStyle());
		if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
			for (ProcardTemplate sameCard : sameMarkIdList) {
				sameCard.setProName(procardTem.getProName());
				sameCard.setUnit(procardTem.getUnit());
				sameCard.setCarStyle(procardTem.getCarStyle());
				sameCard.setSafeCount(procardTem.getSafeCount());
				sameCard.setLastCount(procardTem.getLastCount());
				sameCard.setLingliaostatus(procardTem.getLingliaostatus());
				sameCard.setLingliaoType(procardTem.getLingliaoType());
				sameCard.setStatus(procardTem.getStatus());
				sameCard.setNumb(procardTem.getNumb());
				sameCard.setBanBenNumber(procardTem.getBanBenNumber());
				sameCard.setTuhao(procardTem.getTuhao());
				sameCard.setYtuhao(procardTem.getYtuhao());
				sameCard.setBili(procardTem.getBili());
				sameCard.setClType(procardTem.getClType());
				sameCard.setCaizhi(procardTem.getCaizhi());
				sameCard.setPageTotal(procardTem.getPageTotal());
				sameCard.setFachuDate(procardTem.getFachuDate());
				sameCard.setBianzhiName(procardTem.getBianzhiName());// 编制人
				sameCard.setBianzhiId(procardTem.getBianzhiId());// 编制ID
				sameCard.setBianzhiDate(procardTem.getBianzhiDate());// 编制时间
				sameCard.setJiaoduiName(procardTem.getJiaoduiName());// 校对人
				sameCard.setJiaoduiId(procardTem.getJiaoduiId());// 校对ID
				sameCard.setJiaoduiDate(procardTem.getJiaoduiDate());// 校对时间
				sameCard.setShenheName(procardTem.getShenheName());// 审核人
				sameCard.setShenheId(procardTem.getShenheId());// 审核ID
				sameCard.setShenheDate(procardTem.getShenheDate());// 审核时间
				sameCard.setPizhunName(procardTem.getPizhunName());// 批准人
				sameCard.setPizhunId(procardTem.getPizhunId());// 批准ID
				sameCard.setPizhunDate(procardTem.getPizhunDate());// 批准时间
				totalDao.update(sameCard);
			}
		}

		if (procardTem.getBianzhiId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where userId =?", procardTem
							.getBianzhiId());
			procardTem.setBianzhiName(name);
		}
		if (procardTem.getJiaoduiId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getJiaoduiId());
			procardTem.setJiaoduiName(name);
		}
		if (procardTem.getShenheId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getShenheId());
			procardTem.setShenheName(name);
		}
		if (procardTem.getPizhunId() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getPizhunId());
			procardTem.setPizhunName(name);
		}
		if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("总成")) {
			if (procardTem.getDanjiaojian() == null
					|| !procardTem.getDanjiaojian().equals("单交件")) {// 一般总成
				procardTem.setTrademark(null);
				procardTem.setSpecification(null);
				procardTem.setYuanUnit(null);
				procardTem.setQuanzi1(null);
				procardTem.setQuanzi2(null);
				procardTem.setLuhao(null);
				procardTem.setNumber(null);
				procardTem.setActualFixed(null);
				b = b & totalDao.update(procardTem);
			} else {// 单交件
				List<ProcardTemplate> plist = totalDao.query(
						"from ProcardTemplate where rootId=? and rootId!=id",
						procardTem.getId());
				if (plist.size() > 0) {
					for (ProcardTemplate pson : plist) {
						pson.setProcardTemplate(null);
						b = b & totalDao.delete(pson);
					}
				}
				b = b & totalDao.update(procardTem);
				return b;
			}
			if (b) {
				ProcardTemplate procardTem2 = (ProcardTemplate) totalDao
						.getObjectById(ProcardTemplate.class, procardTem
								.getId());
				Set<ProcardTemplate> procardTemSet = procardTem2
						.getProcardTSet();
				if (procardTemSet != null && procardTemSet.size() > 0) {
					for (ProcardTemplate procardTem3 : procardTemSet) {
						procardTem3.setRootMarkId(procardTem.getMarkId());
						b = b
								& updateBomMaxCount(procardTem3, procardTem2
										.getMaxCount());
					}
				}
			}

		} else if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("自制")) {
			if (procardTem.getProcardTemplate() == null) {// 从action层进来的
				ProcardTemplate procardTem4 = (ProcardTemplate) totalDao
						.getObjectById(ProcardTemplate.class, procardTem
								.getId());
				if (maxCount == null) {
					procardTem4.setMaxCount(null);
				}
				if (procardTem4 != null && procardTem4.getCorrCount() != null) {
					double count = Math.ceil(maxCount
							* procardTem4.getCorrCount());
					maxCount = (float) count;
					procardTem4.setMaxCount(maxCount);
					procardTem4.sumXiaoHaoCount(0);
				}
				Set<ProcardTemplate> procardTemSet = procardTem4
						.getProcardTSet();
				if (procardTemSet != null && procardTemSet.size() > 0) {
					for (ProcardTemplate procardTem5 : procardTemSet) {
						procardTem5.setRootMarkId(procardTem.getMarkId());
						b = b
								& updateBomMaxCount(procardTem5, procardTem4
										.getMaxCount());
					}
				}
				if (b) {
					b = b & totalDao.update(procardTem4);
				}
			} else {
				if (maxCount == null) {
					procardTem.setMaxCount(null);
				}
				if (procardTem != null && procardTem.getCorrCount() != null) {
					double count = Math.ceil(maxCount
							* procardTem.getCorrCount());
					maxCount = (float) count;
					procardTem.setMaxCount(maxCount);
					procardTem.sumXiaoHaoCount(0);
				}
				Set<ProcardTemplate> procardTemSet = procardTem
						.getProcardTSet();
				if (procardTemSet != null && procardTemSet.size() > 0) {
					for (ProcardTemplate procardTem5 : procardTemSet) {
						b = b
								& updateBomMaxCount(procardTem5, procardTem
										.getMaxCount());
					}
				}
				if (b) {
					b = b & totalDao.update(procardTem);
				}
			}

		} else if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("外购")) {
			if (maxCount == null) {
				procardTem.setMaxCount(null);
			}
			if (maxCount != null && procardTem.getQuanzi1() != null
					&& procardTem.getQuanzi2() != null
					&& (procardTem.getQuanzi2() - 0) != 0) {
				// double count = Math.ceil(maxCount * procardTem.getQuanzi2()
				// / procardTem.getQuanzi1());
				// maxCount = (float) count;
				maxCount = maxCount * procardTem.getQuanzi2()
						/ procardTem.getQuanzi1();
				procardTem.setMaxCount(maxCount);
			}
			return totalDao.update(procardTem);
		} else if (procardTem.getProcardStyle() != null
				&& procardTem.getProcardStyle().equals("自制")) {
			if (maxCount == null) {
				procardTem.setMaxCount(null);
			}
			if (procardTem != null && procardTem.getCorrCount() != null) {
				double count = Math.ceil(maxCount * procardTem.getCorrCount());
				maxCount = (float) count;
				procardTem.setMaxCount(maxCount);
				procardTem.sumXiaoHaoCount(0);
			}
			return totalDao.update(procardTem);
		}
		return b;
	}

	@Override
	public String saveCopyProcardToJY(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt != null) {
			List<ProcardTemplateJY> list = totalDao
					.query(
							"from ProcardTemplateJY where markId=? and rootId=id and versionStatus='当前'",
							pt.getMarkId());
			if (list.size() > 0) {// 已存在历史版本
				ProcardTemplateJY maxPtJY = list.get(0);
				if (maxPtJY.getProgressStatus() != null
						&& !maxPtJY.getProgressStatus().equals("完成")) {// 最新版本尚未完成修改
					return "进入精益BOM失败，最新版本尚未完成改进！";
				} else {
					totalDao.createQueryUpdate(
							"update ProcardTemplateJY set versionStatus='历史' where rootId="
									+ maxPtJY.getId(), null, null);
					Integer versionNo = maxPtJY.getVersionNo() + 1;
					ProcardTemplateJY ptjy = new ProcardTemplateJY();
					BeanUtils.copyProperties(pt, ptjy, new String[] { "id",
							"rootId", "fatherId" });
					ptjy.setProgressStatus("未分析");
					ptjy.setVersionStatus("当前");
					ptjy.setVersionNo(versionNo);
					boolean b = totalDao.save(ptjy);
					if (b) {
						b = b
								& saveCopyProcardToJY2(ptjy, pt, ptjy.getId(),
										versionNo);
					}
					if (b) {
						return "进入精益BOM成功";
					}
				}
			} else {// 不存在历史版本,需要产生两个版本版本0为初始版本标记为完成，版本1用作完善标记为未改进
				ProcardTemplateJY ptjy0 = new ProcardTemplateJY();
				BeanUtils.copyProperties(pt, ptjy0, new String[] { "id",
						"rootId", "fatherId" });
				ptjy0.setProgressStatus("完成");
				ptjy0.setVersionStatus("历史");
				ptjy0.setVersionNo(0);
				boolean b = totalDao.save(ptjy0);
				if (b) {
					b = b & saveCopyProcardToJY2(ptjy0, pt, ptjy0.getId(), 0);
					if (b) {
						ProcardTemplateJY ptjy1 = new ProcardTemplateJY();
						BeanUtils.copyProperties(pt, ptjy1, new String[] {
								"id", "rootId", "fatherId" });
						ptjy1.setProgressStatus("未分析");
						ptjy1.setVersionStatus("当前");
						ptjy1.setVersionNo(1);
						b = b & totalDao.save(ptjy1);
						if (b) {
							b = b
									& saveCopyProcardToJY2(ptjy1, pt, ptjy1
											.getId(), 1);
							if (b) {
								return "进入精益BOM成功";
							}
						}
					}
				}
			}

		}
		return "进入精益BOM失败";
	}

	public boolean saveCopyProcardToJY2(ProcardTemplateJY ptjy,
			ProcardTemplate pt, Integer rootId, Integer versionNo) {
		boolean b = true;
		if (pt.getProcardStyle() != null && pt.getProcardStyle().equals("总成")) {// 如果pt为总成则表示将pt下的工序和模板复制到ptjy下
			ptjy.setRootId(rootId);
			Set<ProcessTemplate> processtSet = pt.getProcessTemplate();
			if (processtSet != null && processtSet.size() > 0) {
				Set<ProcessTemplateJY> processtJYSet = new HashSet<ProcessTemplateJY>();
				for (ProcessTemplate processT : processtSet) {
					if (processT.getCapacitySurplus() != null
							&& processT.getCapacitySurplus() < 0) {
						ProcessTemplateJY processTJY = new ProcessTemplateJY();
						BeanUtils.copyProperties(processT, processTJY,
								new String[] { "id", "procardTemplateJY" });
						processTJY.setProcardTemplateJY(ptjy);
						processtJYSet.add(processTJY);
					}
				}
				ptjy.setProcessTemplateJY(processtJYSet);
			}
			b = b & totalDao.update(ptjy);
			Set<ProcardTemplate> ptSet = pt.getProcardTSet();
			if (b & ptSet != null && ptSet.size() > 0) {
				for (ProcardTemplate ptSon : ptSet) {
					// if (ptSon.getProcardStyle() != null
					// && !ptSon.getProcardStyle().equals("外购")) {
					b = b
							& saveCopyProcardToJY2(ptjy, ptSon, rootId,
									versionNo);
					// }
				}
			}
		} else {// 如果pt不是总成则表示将pt本身复制到ptjy下
			ProcardTemplateJY ptjySon = new ProcardTemplateJY();
			BeanUtils.copyProperties(pt, ptjySon, new String[] { "id",
					"rootId", "fatherId", "procardTemplateJY", "procardTJYSet",
					"processTemplateJY" });
			ptjySon.setRootId(rootId);
			ptjySon.setFatherId(ptjy.getId());
			ptjySon.setProcardTemplateJY(ptjy);
			ptjySon.setVersionNo(versionNo);
			if (versionNo == 0) {
				ptjySon.setProgressStatus("完成");
				ptjySon.setVersionStatus("历史");
			} else {
				ptjySon.setProgressStatus("未分析");
				ptjySon.setVersionStatus("当前");
			}
			Set<ProcessTemplate> processtSet = pt.getProcessTemplate();
			if (processtSet != null && processtSet.size() > 0) {
				Set<ProcessTemplateJY> processtJYSet = new HashSet<ProcessTemplateJY>();
				for (ProcessTemplate processT : processtSet) {
					if (processT.getCapacitySurplus() != null
							&& processT.getCapacitySurplus() < 0) {
						ProcessTemplateJY processTJY = new ProcessTemplateJY();
						BeanUtils.copyProperties(processT, processTJY,
								new String[] { "id", "procardTemplateJY" });
						processTJY.setProcardTemplateJY(ptjySon);
						processtJYSet.add(processTJY);
					}
				}
				ptjySon.setProcessTemplateJY(processtJYSet);
			}
			b = b & totalDao.save(ptjySon);
			Set<ProcardTemplate> ptSet = pt.getProcardTSet();
			if (b & ptSet != null && ptSet.size() > 0) {
				for (ProcardTemplate ptSon2 : ptSet) {
					// if (ptSon2.getProcardStyle() != null
					// && !ptSon2.getProcardStyle().equals("外购")) {
					b = b
							& saveCopyProcardToJY2(ptjySon, ptSon2, rootId,
									versionNo);
					// }
				}
			}

		}
		return b;
	}

	@Override
	public String changeTolp(ProcardTemplate pt1, ProcardTemplate pt2) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> list = totalDao
				.query(
						"from ProcardTemplate where markId=? and productStyle='批产' and (banbenStatus =null or banbenStatus='默认')",
						pt1.getMarkId());
		if (list.size() > 0) {
			return "对不起，该件号已存在批产BOM";
		}

		// 发现版本不一样不允许转换(准备转批产的试制版本和批产原有的零件的版本)
		List<ProcardTemplate> trialList = totalDao.query(
				"from ProcardTemplate where rootId  = ?"
						+ " and (dataStatus is null or dataStatus!='删除')", pt2
						.getId());
		if (trialList != null && trialList.size() > 0) {
			for (ProcardTemplate trial : trialList) {
				String banbenNumber = "";

				if (trial.getBanBenNumber() == null
						|| "".equals(trial.getBanBenNumber())) {
					banbenNumber = "and (banBenNumber is not null and banBenNumber<>'')";
				} else {
					banbenNumber = "and banBenNumber <>'"
							+ trial.getBanBenNumber()
							+ "' "
							+ " and banbenStatus is not null and banbenStatus<> '历史'";
				}
				Integer count = totalDao
						.getCount(
								"from ProcardTemplate where markId = ? and productStyle='批产' "
										+ "  and (dataStatus is null or dataStatus!='删除') "
										+ banbenNumber, trial.getMarkId());
				if (count != null && count > 0) {
					return "批产中有相同件号，并且版本不一致，不能转换：冲突的件号为：" + trial.getMarkId();
				}
			}
		}
		pt1.setProcardStyle("总成");
		pt1.setProductStyle("批产");
		pt1.setBelongLayer(1);
		pt1.setProName(pt2.getProName());
		pt1.setMaxCount(pt2.getMaxCount());
		if (totalDao.save(pt1)) {
			pt1.setRootId(pt1.getId());
			return changeTolp2(pt1, pt2) + "";
		}
		return "数据有误，转换失败";
	}

	public boolean changeTolp2(ProcardTemplate pt1, ProcardTemplate pt2) {
		if (pt1 != null && pt2 != null) {
			// 获取主副两个对象的持久层
			ProcardTemplate oldpt1 = null;
			ProcardTemplate oldpt2 = null;
			if (pt1.getProcardTemplate() != null) {// 说明pt1是在server层中产生的不是从action层中传过来的
				oldpt1 = pt1;
			} else {
				oldpt1 = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, pt1.getId());
			}
			if (pt2.getProcardTemplate() != null) {// 说明pt2是在server层中产生的不是从action层中传过来的
				oldpt2 = pt2;
			} else {
				oldpt2 = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, pt2.getId());
			}
			boolean b = true;
			if (oldpt1 != null && oldpt2 != null) {
				if (oldpt2.getProcardStyle() != null
						&& oldpt2.getProcardStyle().equals("总成")
						&& oldpt1.getProcardStyle() != null
						&& oldpt1.getProcardStyle().equals("总成")) {
					// 如果是总成
					List<ProcardTemplate> ptSet = totalDao
							.query(
									"from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') order by id asc",
									oldpt2.getId());
					// 删除原有的process
					Set<ProcessTemplate> processSet1 = oldpt1
							.getProcessTemplate();
					oldpt1.setProcessTemplate(null);
					if (processSet1 != null && processSet1.size() > 0) {
						for (ProcessTemplate process : processSet1) {
							process.setProcardTemplate(null);
							b = b & totalDao.delete(process);
						}

					}
					// 复制process
					Set<ProcessTemplate> processSet2 = oldpt2
							.getProcessTemplate();
					if (b & processSet2 != null && processSet2.size() > 0) {
						Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
						for (ProcessTemplate process : processSet2) {
							ProcessTemplate p = new ProcessTemplate();
							BeanUtils.copyProperties(process, p, new String[] {
									"id", "procardTemplate", "processPRNScore",
									"taSopGongwei", "processFuLiaoTemplate" });
							// -----辅料开始---------
							if (p.getIsNeedFuliao() != null
									&& p.getIsNeedFuliao().equals("yes")) {
								Set<ProcessFuLiaoTemplate> fltmpSet = process
										.getProcessFuLiaoTemplate();
								if (fltmpSet.size() > 0) {
									Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
									for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
										ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
										BeanUtils.copyProperties(fltmp,
												pinfoFl, new String[] { "id",
														"processTemplate" });
										if (pinfoFl.getQuanzhi1() == null) {
											pinfoFl.setQuanzhi1(1f);
										}
										if (pinfoFl.getQuanzhi2() == null) {
											pinfoFl.setQuanzhi2(1f);
										}
										pinfoFl.setProcessTemplate(p);
										pinfoFlSet.add(pinfoFl);
									}
									p.setProcessFuLiaoTemplate(pinfoFlSet);
								}
							}
							// -----辅料结束---------
							newprocessSet.add(p);
						}
						oldpt1.setProcessTemplate(newprocessSet);
					}
					// 原材料属性
					oldpt1.setDanjiaojian(oldpt2.getDanjiaojian());
					oldpt1.setTrademark(oldpt2.getTrademark());
					oldpt1.setSpecification(oldpt2.getSpecification());
					oldpt1.setLuhao(oldpt2.getLuhao());
					oldpt1.setNumber(oldpt2.getNumber());
					oldpt1.setActualFixed(oldpt2.getActualFixed());
					oldpt1.setYuanUnit(oldpt2.getYuanUnit());
					oldpt1.setQuanzi1(oldpt2.getQuanzi1());
					oldpt1.setQuanzi2(oldpt2.getQuanzi2());
					oldpt1.setClClass(oldpt2.getClClass());
					oldpt1.setStatus(oldpt2.getStatus());
					oldpt1.setLingliaostatus(oldpt2.getLingliaostatus());
					b = b & totalDao.update(oldpt1);
					if (b & ptSet != null && ptSet.size() > 0) {
						// Set<ProcardTemplate> ptSet2 = new
						// HashSet<ProcardTemplate>();
						// for (ProcardTemplate pt : ptSet) {
						// ptSet2.add(pt);
						// }
						for (ProcardTemplate pt : ptSet) {
							saveCopyProcard(pt1, pt, "批产");
						}
					}

				} else {
					// 复制procardTemplate
					ProcardTemplate newpt2 = new ProcardTemplate();
					// newpt2.setMarkId("123");
					BeanUtils.copyProperties(pt2, newpt2, new String[] { "id",
							"procardTemplate", "procardTSet",
							"processTemplate", "zhUsers" });
					newpt2.setProductStyle("批产");
					if (newpt2.getProcardStyle() != null
							&& newpt2.getProcardStyle().equals("总成")) {
						newpt2.setProcardStyle("自制");
					}
					newpt2.setId(null);
					newpt2.setRootId(oldpt1.getRootId());
					newpt2.setFatherId(oldpt1.getId());
					if (oldpt1.getBelongLayer() != null) {
						newpt2.setBelongLayer(oldpt1.getBelongLayer() + 1);
					}
					List<ProcardTemplate> ptSet = totalDao
							.query(
									"from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') order by id asc",
									oldpt2.getId());
					// Set<ProcardTemplate> ptSet2 = new
					// HashSet<ProcardTemplate>();
					// if (ptSet.size() > 0) {
					// for (ProcardTemplate pt : ptSet) {
					// ptSet2.add(pt);
					// }
					// }
					// 复制process
					Set<ProcessTemplate> processSet = oldpt2
							.getProcessTemplate();
					if (processSet != null && processSet.size() > 0) {
						Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
						for (ProcessTemplate process : processSet) {
							ProcessTemplate p = new ProcessTemplate();
							BeanUtils.copyProperties(process, p, new String[] {
									"id", "procardTemplate", "processPRNScore",
									"taSopGongwei", "processFuLiaoTemplate" });
							// -----辅料开始---------
							if (p.getIsNeedFuliao() != null
									&& p.getIsNeedFuliao().equals("yes")) {
								Set<ProcessFuLiaoTemplate> fltmpSet = process
										.getProcessFuLiaoTemplate();
								if (fltmpSet.size() > 0) {
									Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
									for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
										ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
										BeanUtils.copyProperties(fltmp,
												pinfoFl, new String[] { "id",
														"processTemplate" });
										if (pinfoFl.getQuanzhi1() == null) {
											pinfoFl.setQuanzhi1(1f);
										}
										if (pinfoFl.getQuanzhi2() == null) {
											pinfoFl.setQuanzhi2(1f);
										}
										pinfoFl.setProcessTemplate(p);
										pinfoFlSet.add(pinfoFl);
									}
									p.setProcessFuLiaoTemplate(pinfoFlSet);
								}
							}
							// -----辅料结束---------
							newprocessSet.add(p);
						}
						newpt2.setProcessTemplate(newprocessSet);
					}
					newpt2.setProcardTemplate(oldpt1);
					Set<ProcardTemplate> oldpt1ptSet = oldpt1.getProcardTSet();
					if (oldpt1ptSet != null && oldpt1ptSet.size() > 0) {
						for (ProcardTemplate p : oldpt1ptSet) {
							if (p.getMarkId() != null
									&& newpt2.getMarkId() != null
									&& p.getMarkId().equals(newpt2.getMarkId())) {
								// 原流水卡片模板下已含有被复制的流水卡片模板
								return true;
							}
						}
						oldpt1ptSet.add(newpt2);
					}
					oldpt1.setProcardTSet(oldpt1ptSet);
					b = b & totalDao.save(newpt2);
					if (b & ptSet.size() > 0) {
						for (ProcardTemplate pt : ptSet) {
							saveCopyProcard(newpt2, pt, "批产");
						}
					}
				}
				return b;
			}
		}
		return false;
	}

	@Override
	public List getProcessTz(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplate process = findProcessT(id);
		if (process != null && process.getProcardTemplate() != null) {
			ProcardTemplate pt = process.getProcardTemplate();
			String canChange = "no";
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准图纸删除'";
			String sc = (String) totalDao.getObjectByCondition(hql1);
			if (sc != null && sc.equals("是")) {
				canChange = "yes";
			} else {
				if (pt.getBzStatus() == null || !pt.getBzStatus().equals("已批准")) {
					canChange = "yes";
				} else {
					Float tqCount = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
											+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) and p2.id=?",
									pt.getRootId());
					if (tqCount != null && tqCount > 0) {
						canChange = "yes";
					}
				}
			}
			String addSql1 = null;
			Integer banci = process.getProcardTemplate().getBanci();
			if (banci == null || banci == 0) {
				addSql1 = " and (banci is null or banci =0)";
			} else {
				addSql1 = " and banci =" + banci;
			}
			String addSql2 = null;
			if (process.getProcardTemplate().getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' and  glId="
						+ process.getId();
			}
			List<ProcessTemplateFile> ptFileList = totalDao.query(
					"from ProcessTemplateFile where processNO=? and processName=? and markId=? "
							+ addSql1 + addSql2 + " order by oldfileName",
					process.getProcessNO(), process.getProcessName(), process
							.getProcardTemplate().getMarkId());
			if (ptFileList != null && ptFileList.size() > 0) {
				for (ProcessTemplateFile pf : ptFileList) {
					pf.setCanChange(canChange);
				}
			}
			return ptFileList;
		}
		return null;
	}

	@Override
	public String saveProcessTemplateFile(
			ProcessTemplateFile processTemplateFile, Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		processTemplateFile.setStatus("默认");
		ProcessTemplate process = (ProcessTemplate) totalDao.getObjectById(
				ProcessTemplate.class, id);
		if (process != null) {
			ProcardTemplate pt = process.getProcardTemplate();
			if (pt.getBzStatus() != null && pt.getBzStatus().equals("已批准")) {
				return "此工序的零件编制状态为已批准不允许上传图纸!";
			}
			if (pt.getBzStatus() != null && pt.getBzStatus().equals("设变发起中")) {
				return "此工序的零件正在设变发起中不允许上传图纸!";
			}
			processTemplateFile.setProductStyle(process.getProcardTemplate()
					.getProductStyle());
			processTemplateFile.setProcessName(process.getProcessName());
			processTemplateFile.setProcessNO(process.getProcessNO());
			Integer banci = process.getProcardTemplate().getBanci();
			if (process.getProcardTemplate().getBzStatus() != null
					&& process.getProcardTemplate().getBzStatus().equals("已批准")) {
				// 直接加章
				if (processTemplateFile.getFileName2() != null) {
					processTemplateFile.setFileName(processTemplateFile
							.getFileName2());
					processTemplateFile.setFileName2(processTemplateFile
							.getFileName());
				}
			}
			if (banci == null) {
				banci = 0;
			}
			processTemplateFile.setBanci(banci);
			processTemplateFile.setMarkId(process.getProcardTemplate()
					.getMarkId());
			processTemplateFile.setBanBenNumber(process.getProcardTemplate()
					.getBanBenNumber());
			if (pt.getProductStyle().equals("试制")) {
				processTemplateFile.setGlId(process.getId());
			}
			if (totalDao.save(processTemplateFile)) {
				ProcardTemplateChangeLog.addchangeLog(totalDao, process
						.getProcardTemplate(), processTemplateFile, "图纸", "添加",
						user, nowtime);
				AttendanceTowServerImpl.updateJilu(6, processTemplateFile,
						processTemplateFile.getId(), "件号:"
								+ processTemplateFile.getMarkId() + "第"
								+ processTemplateFile.getProcessNO() + "工序"
								+ processTemplateFile.getProcessName() + " "
								+ processTemplateFile.getType() + " 图纸名:"
								+ processTemplateFile.getOldfileName()
								+ "(上传图纸)");
				// 同步其他同版本试制工序图纸
				String banbenSql = null;
				String banbenSql2 = null;
				if (pt.getProductStyle().equals("试制")) {
					if (process.getProcardTemplate().getBanBenNumber() == null
							|| process.getProcardTemplate().getBanBenNumber()
									.length() == 0) {
						banbenSql = " and (procardTemplate.banBenNumber is null or procardTemplate.banBenNumber='')";
						banbenSql2 = " and (banBenNumber is null or banBenNumber='')";

					} else {
						banbenSql = " and procardTemplate.banBenNumber='"
								+ process.getProcardTemplate()
										.getBanBenNumber() + "'";
						banbenSql2 = " and banBenNumber='"
								+ process.getProcardTemplate()
										.getBanBenNumber() + "'";
					}
					List<ProcessTemplate> processlist = totalDao
							.query(
									"from ProcessTemplate where processNO =? and processName=? and id!=? and (dataStatus is null or dataStatus !='删除')"
											+ " and procardTemplate.markId=? and procardTemplate.productStyle='试制' "
											+ banbenSql,
									process.getProcessNO(), process
											.getProcessName(), process.getId());
					if (processlist != null && processlist.size() > 0) {
						for (ProcessTemplate process2 : processlist) {
							ProcessTemplateFile file2 = new ProcessTemplateFile();
							BeanUtils.copyProperties(processTemplateFile,
									file2, new String[] { "id" });
							file2.setGlId(process2.getId());
							file2.setProductStyle("试制");
							file2.setBanBenNumber(process2.getProcardTemplate()
									.getBanBenNumber());
							file2.setBanci(process2.getProcardTemplate()
									.getBanci());
							totalDao.save(file2);
						}
					}
				}
				// if (pt.getProductStyle().equals("试制")) {
				// //如果是试制则复制图纸到批产同版本上
				// ProcardTemplate pcpt = (ProcardTemplate)
				// totalDao.getObjectByCondition("from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"+banbenSql2,
				// pt.getMarkId());
				// if(pcpt!=null){
				// ProcessTemplate pcprocess = (ProcessTemplate)
				// totalDao.getObjectByCondition("from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') and processNO =? and processName=? ",
				// pt.getMarkId(),process.getProcessNO(),process.getProcessName());
				// if(pcprocess!=null){
				// ProcessTemplateFile file2 = new ProcessTemplateFile();
				// BeanUtils.copyProperties(processTemplateFile, file2, new
				// String []{"id"});
				// file2.setGlId(null);
				// file2.setProductStyle("批产");
				// file2.setBanci(pcpt.getBanci());
				// totalDao.save(file2);
				// }
				// }
				//					
				// }
				return "true";
			} else
				return "false";
		}
		return "没有找到目标工序";
	}

	@Override
	public boolean deleteProcessTz(Integer id, String tag) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return false;
		}
		String nowtime = Util.getDateTime();
		ProcessTemplateFile file = (ProcessTemplateFile) totalDao
				.getObjectById(ProcessTemplateFile.class, id);
		if (file != null) {
			boolean b = true;
			String addSql = "";
			String glSql = "";
			if (file.getProductStyle() == null
					|| !file.getProductStyle().equals("试制")) {
				addSql = " and (productStyle is null or productStyle !='试制') ";
			} else {
				String banben = null;
				if (file.getProcessNO() == null) {
					banben = (String) totalDao
							.getObjectByCondition(
									"select banBenNumber from ProcardTemplate where id=?",
									file.getGlId());
				} else {
					banben = (String) totalDao
							.getObjectByCondition(
									"select procardTemplate.banBenNumber from ProcessTemplate where id=?",
									file.getGlId());
				}
				String banbensql = null;
				if (banben == null) {
					banbensql = " and (banBenNumber is null or banBenNumber='')";
				} else {
					banbensql = " and banBenNumber ='" + banben + "'";
				}
				glSql = " and productStyle ='试制' and ((glId in (select id from ProcardTemplate where markId='"
						+ file.getMarkId()
						+ "' "
						+ banbensql
						+ "  and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除'))"
						+ " and  processNO is null ) "
						+ "or (processNO is not null and glId in (select id from ProcessTemplate where (dataStatus is null or dataStatus!='删除') and procardTemplate.id in(select id from ProcardTemplate where markId='"
						+ file.getMarkId()
						+ "'  "
						+ banbensql
						+ "  and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')) )))";
			}
			if (file.getBanci() == null || file.getBanci() == 0) {
				addSql += " and (banci is null or banci=0) ";
			} else {
				addSql += " and  banci=" + file.getBanci();
			}
			// if ("sop".equals(tag)) {
			// b = totalDao.delete(file);
			// } else {
			// }
			// 同件号公用文件，此文件为下发的原图，原图的删除需要同步到整个零件
			// List<ProcessTemplateFile> fileList = totalDao.query(
			// "from ProcessTemplateFile where markId=? and fileName=? and status='默认'"
			// + addSql2, file.getMarkId(), file.getFileName());
			List<ProcessTemplateFile> fileList = null;
			if (file.getOldfileName() != null
					&& file.getOldfileName().length() > 0) {

				fileList = totalDao.query(
						"from ProcessTemplateFile where markId=? and oldfileName=? and fileName=?"
								+ addSql + glSql, file.getMarkId(), file
								.getOldfileName(), file.getFileName());
			} else {
				fileList = totalDao
						.query(
								"from ProcessTemplateFile where markId=? and (oldfileName is null or  oldfileName='')"
										+ addSql + glSql, file.getMarkId());
			}
			for (ProcessTemplateFile f : fileList) {
				b = b & totalDao.delete(f);
			}
			ProcardTemplate pt = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId=? "
									+ addSql
									+ " and (dataStatus=null or dataStatus!='删除' ) ",
							file.getMarkId());
			if (pt != null) {
				ProcardTemplateChangeLog.addchangeLog(totalDao, pt, file, "图纸",
						"删除", user, nowtime);
			}
			AttendanceTowServerImpl.updateJilu(7, file, file.getId(), "件号:"
					+ file.getMarkId() + "第" + file.getProcessNO() + "工序"
					+ file.getProcessName() + " " + file.getType() + " 图纸名:"
					+ file.getOldfileName() + "(删除)");
			return b;
		}
		return false;
	}

	@Override
	public ProcessTemplateFile findProcessTz(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplateFile) totalDao.getObjectById(
				ProcessTemplateFile.class, id);
	}

	@Override
	public ProcessTemplate findProcessTByTz(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplate) totalDao
				.getObjectByCondition(
						"from ProcessTemplate where id=(select processTemplate.id from ProcessTemplateFile where id=?)",
						id);
	}

	@Override
	public Integer updateProcessNo(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplate process = (ProcessTemplate) totalDao
				.getObjectByCondition(
						"from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') order by processNO desc",
						id);
		if (process == null) {
			return 1;
		}
		return process.getProcessNO() + 1;
	}

	@Override
	public boolean checkHasSonMarkId(Integer fatherId, Integer id, String markId) {
		List list = null;
		if (id == null || id == 0) {
			if (fatherId == null) {
				return false;
			}
			list = totalDao
					.query(
							"from ProcardTemplate where markId=? and procardTemplate.id=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
							markId, fatherId);
		} else {
			list = totalDao
					.query(
							"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and procardTemplate.id=(select procardTemplate.id from ProcardTemplate where id=?) and id!=?",
							markId, id, id);
		}
		if (list != null && list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String applySpecial(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplate process = (ProcessTemplate) totalDao.getObjectById(
				ProcessTemplate.class, id);
		Integer epId;
		try {
			epId = CircuitRunServerImpl.createProcess("特殊工序申请",
					ProcessTemplate.class, id, "status", "id",
					"ProcardTemplateAction!specialProcessDetail.action?id="
							+ id, "特殊工序申请审批", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "审批流程有误,请联系管理员!";
		}
		if (epId != null) {
			process.setEpId(epId);
			process.setIsSpecial("审批中");
			return totalDao.update(process) + "";
		} else {
			return "审批流程有误,请联系管理员!";
		}
	}

	@Override
	public ProcardTemplate getPtByProcessId(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where  id =(select procardTemplate.id from ProcessTemplate where id=?)",
						id);
		if (pt.getRootMarkId() == null || pt.getRootMarkId().equals("")) {
			String rootMarkId = (String) totalDao.getObjectByCondition(
					"select markId from ProcardTemplate where id=?", pt
							.getRootId());
			pt.setRootMarkId(rootMarkId);
		}
		return pt;
	}

	/**
	 * 0不同步，1同步同父类下的同件号，2同步所有同件号
	 */
	@Override
	public int isTb(ProcardTemplate procardTemplate,
			ProcardTemplate oldProcardTem) {
		// TODO Auto-generated method stub
		// 名称
		if (!isEquals(oldProcardTem.getProName(), procardTemplate.getProName())) {
			return 2;
		}
		// 单位
		if (!isEquals(oldProcardTem.getUnit(), procardTemplate.getUnit())) {
			return 2;
		}
		// 车型
		if (!isEquals(oldProcardTem.getCarStyle(), procardTemplate
				.getCarStyle())) {
			return 2;
		}
		// 安全库存
		if (!isEquals(oldProcardTem.getSafeCount(), procardTemplate
				.getSafeCount())) {
			return 2;
		}
		// 最低库存
		if (!isEquals(oldProcardTem.getLastCount(), procardTemplate
				.getLastCount())) {
			return 2;
		}
		// 版本号
		if (!isEquals(oldProcardTem.getBanBenNumber(), procardTemplate
				.getBanBenNumber())) {
			return 2;
		}
		// 重要性
		if (!isEquals(oldProcardTem.getImportance(), procardTemplate
				.getImportance())) {
			return 2;
		}
		// 图号
		if (!isEquals(oldProcardTem.getTuhao(), procardTemplate.getTuhao())) {
			return 2;
		}
		// 牌号(件号)
		if (!isEquals(oldProcardTem.getTrademark(), procardTemplate
				.getTrademark())) {
			return 2;
		}
		// 规格
		if (!isEquals(oldProcardTem.getSpecification(), procardTemplate
				.getSpecification())) {
			return 2;
		}
		// // 原材料名称
		// if (!isEquals(oldProcardTem.getYuanName(), procardTemplate
		// .getYuanName())) {
		// return 2;
		// }
		// // 炉号
		// if (!isEquals(oldProcardTem.getLuhao(), procardTemplate.getLuhao()))
		// {
		// return true;
		// }
		// // 原材料图号
		// if (!isEquals(oldProcardTem.getYtuhao(),
		// procardTemplate.getYtuhao())) {
		// return true;
		// }
		// //比例
		// sameCard.setBili(procardTem.getBili());
		// 材料类别
		if (!isEquals(oldProcardTem.getClType(), procardTemplate.getClType())) {
			return 2;
		}
		// sameCard.setCaizhi(procardTem.getCaizhi());
		// 领料类型
		if (!isEquals(oldProcardTem.getLingliaoType(), procardTemplate
				.getLingliaoType())) {
			return 2;
		}
		// 状态
		if (!isEquals(oldProcardTem.getStatus(), procardTemplate.getStatus())) {
			return 2;
		}
		// 页数
		if (!isEquals(oldProcardTem.getPageTotal(), procardTemplate
				.getPageTotal())) {
			return 2;
		}
		// 工艺编号
		if (!isEquals(oldProcardTem.getNumb(), procardTemplate.getNumb())) {
			return 2;
		}
		// 发出日期
		if (!isEquals(oldProcardTem.getFachuDate(), procardTemplate
				.getFachuDate())) {
			return 2;
		}
		// 编制人
		if (!isEquals(oldProcardTem.getBianzhiId(), procardTemplate
				.getBianzhiId())) {
			return 2;
		}
		// 校对人
		if (!isEquals(oldProcardTem.getJiaoduiId(), procardTemplate
				.getJiaoduiId())) {
			return 2;
		}
		// 审核人
		if (!isEquals(oldProcardTem.getShenheId(), procardTemplate
				.getShenheId())) {
			return 2;
		}
		// 批准人
		if (!isEquals(oldProcardTem.getPizhunId(), procardTemplate
				.getPizhunId())) {
			return 2;
		}
		// 初始总成
		if (!isEquals(oldProcardTem.getLoadMarkId(), procardTemplate
				.getLoadMarkId())) {
			return 2;
		}
		// 领料状态
		if (!isEquals(oldProcardTem.getLingliaostatus(), procardTemplate
				.getLingliaostatus())) {
			return 1;
		}
		// 权值1
		if (!isEquals(oldProcardTem.getQuanzi1(), procardTemplate.getQuanzi1())) {
			return 1;
		}
		// 权值2
		if (!isEquals(oldProcardTem.getQuanzi2(), procardTemplate.getQuanzi2())) {
			return 1;
		}
		if (!isEquals(oldProcardTem.getCorrCount(), procardTemplate
				.getCorrCount())) {
			return 1;
		}
		// 表处
		if (!isEquals(oldProcardTem.getBiaochu(), procardTemplate.getBiaochu())) {
			return 2;
		}
		// 半成品
		if (!isEquals(oldProcardTem.getNeedProcess(), procardTemplate
				.getNeedProcess())) {
			return 2;
		}
		// 长
		if (!isEquals(oldProcardTem.getThisLength(), procardTemplate
				.getThisLength())) {
			return 2;
		}
		// 宽
		if (!isEquals(oldProcardTem.getThisWidth(), procardTemplate
				.getThisWidth())) {
			return 2;
		}
		// 厚
		if (!isEquals(oldProcardTem.getThisHight(), procardTemplate
				.getThisHight())) {
			return 2;
		}
		// 自制件激活
		if (!isEquals(oldProcardTem.getZzjihuo(), procardTemplate.getZzjihuo())) {
			return 2;
		}
		return 0;
	}

	/**
	 * 0不同步，1同步同父类下的同件号，2同步所有同件号
	 */
	public String tbAndupdate(ProcardTemplate procardTemplate,
			ProcardTemplate oldProcardTem) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		int changeLv = 0;
		boolean changewgj = false;
		boolean changewgj2 = false;
		// 名称
		if (!isEquals(oldProcardTem.getProName(), procardTemplate.getProName())) {
			oldProcardTem.setProName(procardTemplate.getProName());
			changewgj = true;
			changeLv = 2;
		}
		// 供料属性
		if (!isEquals(oldProcardTem.getKgliao(), procardTemplate.getKgliao())) {
			changewgj2 = true;
			oldProcardTem.setKgliao(procardTemplate.getKgliao());
			changeLv = 1;
		}
		// 物料类别
		if (!isEquals(oldProcardTem.getWgType(), procardTemplate.getWgType())) {
			changewgj = true;
			oldProcardTem.setWgType(procardTemplate.getWgType());
			changeLv = 2;
		}
		// 单位
		if (!isEquals(oldProcardTem.getUnit(), procardTemplate.getUnit())) {
			changewgj = true;
			oldProcardTem.setUnit(procardTemplate.getUnit());
			changeLv = 2;
		}
		// 比例
		if (!isEquals(oldProcardTem.getBili(), procardTemplate.getBili())) {
			changewgj = true;
			oldProcardTem.setBili(procardTemplate.getBili());
			changeLv = 2;
		}
		// 图号
		if (!isEquals(oldProcardTem.getTuhao(), procardTemplate.getTuhao())) {
			changewgj = true;
			oldProcardTem.setTuhao(procardTemplate.getTuhao());
			changeLv = 2;
		}
		// 重要性
		if (!isEquals(oldProcardTem.getImportance(), procardTemplate
				.getImportance())) {
			changewgj = true;
			oldProcardTem.setImportance(procardTemplate.getImportance());
			changeLv = 2;
		}
		// // 牌号(件号)
		// if (!isEquals(oldProcardTem.getTrademark(), procardTemplate
		// .getTrademark())) {
		// return 2;
		// }
		// 规格
		if (!isEquals(oldProcardTem.getSpecification(), procardTemplate
				.getSpecification())) {
			changewgj = true;
			oldProcardTem.setSpecification(procardTemplate.getSpecification());
			changeLv = 2;
		}
		if (oldProcardTem.getProcardStyle() != null
				&& oldProcardTem.getProcardStyle().equals("外购") && changewgj) {// 外购件库中需要修改对应的值
			if (changewgj) {
				// 修改外购件
				List<YuanclAndWaigj> wgjList = totalDao
						.query(
								"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and markId=?",
								oldProcardTem.getMarkId());
				if (wgjList != null && wgjList.size() > 0) {
					for (YuanclAndWaigj wgj : wgjList) {
						if ("已确认".equals(wgj.getStatus())) {
							throw new RuntimeException("该件号在外购件库中已确认,不允许修改!");
						}
						// 名称
						wgj.setName(procardTemplate.getProName());
						// 供料属性
						wgj.setKgliao(procardTemplate.getKgliao());
						// 物料类别
						wgj.setWgType(procardTemplate.getWgType());
						wgj.setUnit(procardTemplate.getUnit());
						wgj.setBili(procardTemplate.getBili());
						// 图号
						wgj.setTuhao(procardTemplate.getTuhao());
						// 重要性
						wgj.setImportance(procardTemplate.getImportance());
						// 规格
						wgj
								.setSpecification(procardTemplate
										.getSpecification());
						totalDao.update(wgj);
					}
				} else {
					throw new RuntimeException("该件号不在外购件库中数据异常请前往外购件库中添加!");
				}
			}
			if (changewgj2
					&& procardTemplate.getProcardStyle().equals("外购")
					&& procardTemplate.getKgliao() != null
					&& (procardTemplate.getKgliao().equals("TK")
							|| procardTemplate.getKgliao().equals("TK AVL")
							|| procardTemplate.getKgliao().equals("TK Price") || procardTemplate
							.getKgliao().equals("CS"))) {// 供料属性修改
				String banbenSql = null;
				if (oldProcardTem.getBanBenNumber() == null
						|| oldProcardTem.getBanBenNumber().length() == 0) {
					banbenSql = " and (banbenhao is null or banbenhao='')";
				} else {
					banbenSql = " and banbenhao ='"
							+ oldProcardTem.getBanBenNumber() + "'";
				}
				List<YuanclAndWaigj> wgjList = totalDao
						.query(
								"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and markId=? and　kgliao=? "
										+ banbenSql, oldProcardTem.getMarkId(),
								procardTemplate.getKgliao());
				if (wgjList == null || wgjList.size() == 0) {// 没有外购件库中没有此供料属性
					List<YuanclAndWaigj> wgjList2 = totalDao
							.query(
									"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and status='禁用' and markId=? and　kgliao=? "
											+ banbenSql, oldProcardTem
											.getMarkId(), procardTemplate
											.getKgliao());
					if (wgjList2 != null && wgjList2.size() > 0) {
						throw new RuntimeException("该外购件已被禁用!");
					}
				} else {// 生成外购数据
					YuanclAndWaigj wgjTem = new YuanclAndWaigj();
					wgjTem.setMarkId(oldProcardTem.getMarkId());// 件号
					wgjTem.setName(procardTemplate.getProName());// 名称
					wgjTem.setSpecification(procardTemplate.getSpecification());// 规格
					wgjTem.setUnit(procardTemplate.getUnit());// BOM用的单位
					wgjTem.setCkUnit(procardTemplate.getUnit());// 仓库用的单位
					wgjTem.setBanbenhao(oldProcardTem.getBanBenNumber());// 版本号
					wgjTem.setZcMarkId(oldProcardTem.getLoadMarkId());// 总成号
					wgjTem.setClClass("外购件");// 材料类型（外购件,原材料,辅料）
					wgjTem.setCaizhi(procardTemplate.getClType());// 材质类型
					wgjTem.setTuhao(procardTemplate.getTuhao());// 图号
					wgjTem.setKgliao(procardTemplate.getKgliao());// 供料属性
					wgjTem.setWgType(procardTemplate.getWgType());
					wgjTem.setBanbenStatus("默认");
					wgjTem.setProductStyle(oldProcardTem.getProductStyle());
					wgjTem.setPricestatus("新增");
					wgjTem.setAddTime(Util.getDateTime());
					wgjTem.setAddUserCode(user.getCode());
					wgjTem.setAddUserName(user.getName());
					wgjTem.setImportance(procardTemplate.getImportance());
					totalDao.save(wgjTem);

				}
			}
		}
		String oldProcardStyle = oldProcardTem.getProcardStyle();
		// 零件类型
		if (!isEquals(oldProcardTem.getProcardStyle(), procardTemplate
				.getProcardStyle())) {
			if (oldProcardTem.getProcardStyle() != null
					&& oldProcardTem.getProcardStyle().equals("待定")
					&& procardTemplate.getProcardStyle() != null
					&& procardTemplate.getProcardStyle().equals("外购")) {// 与库中匹配
				if (procardTemplate.getKgliao() == null
						|| procardTemplate.getKgliao().length() == 0) {
					throw new RuntimeException("请先填写供料属性");
				}
				procardTemplate.setQuanzi1(1f);
				procardTemplate.setQuanzi2(procardTemplate.getCorrCount());
				// 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
				String wgjSql = "from YuanclAndWaigj where markId=? and kgliao=?";
				if (oldProcardTem.getProductStyle().equals("批产")) {
					wgjSql += "and productStyle='批产'";
				}
				// if (sonPt.getSpecification() == null
				// || changeLvecification().length() == 0) {
				// wgjSql +=
				// " and (specification is null or specification='')";
				// } else {
				// wgjSql += " and specification ='"
				// + sonPt.getSpecification() + "'";
				// }
				if (oldProcardTem.getBanBenNumber() == null
						|| oldProcardTem.getBanBenNumber().length() == 0) {
					wgjSql += " and (banbenhao is null or banbenhao='')";
				} else {
					wgjSql += " and banbenhao ='"
							+ oldProcardTem.getBanBenNumber() + "'";
				}
				YuanclAndWaigj wgjTem = (YuanclAndWaigj) totalDao
						.getObjectByCondition(wgjSql,
								oldProcardTem.getMarkId(), procardTemplate
										.getKgliao());
				if (wgjTem == null) {
					if (procardTemplate.getWgType() == null
							|| procardTemplate.getWgType().length() == 0) {
						throw new RuntimeException("请先填写物料类别");
					}
					wgjTem = new YuanclAndWaigj();
					wgjTem.setMarkId(oldProcardTem.getMarkId());// 件号
					wgjTem.setName(procardTemplate.getProName());// 名称
					wgjTem.setSpecification(procardTemplate.getSpecification());// 规格
					wgjTem.setUnit(procardTemplate.getUnit());// BOM用的单位
					wgjTem.setCkUnit(procardTemplate.getUnit());// 仓库用的单位
					wgjTem.setBanbenhao(oldProcardTem.getBanBenNumber());// 版本号
					wgjTem.setZcMarkId(oldProcardTem.getLoadMarkId());// 总成号
					wgjTem.setClClass("外购件");// 材料类型（外购件,原材料,辅料）
					wgjTem.setCaizhi(procardTemplate.getClType());// 材质类型
					wgjTem.setTuhao(procardTemplate.getTuhao());// 图号
					wgjTem.setKgliao(procardTemplate.getKgliao());// 供料属性
					wgjTem.setWgType(procardTemplate.getWgType());
					wgjTem.setBanbenStatus("默认");
					wgjTem.setProductStyle(oldProcardTem.getProductStyle());
					wgjTem.setPricestatus("新增");
					wgjTem.setAddTime(Util.getDateTime());
					wgjTem.setAddUserCode(user.getCode());
					wgjTem.setAddUserName(user.getName());
					wgjTem.setImportance(procardTemplate.getImportance());
					totalDao.save(wgjTem);
				} else if (wgjTem.getStatus() != null
						&& wgjTem.getStatus().equals("禁用")) {
					throw new RuntimeException("外购件" + wgjTem.getMarkId() + "的"
							+ wgjTem.getSpecification() + "规格的"
							+ wgjTem.getBanbenhao() + "版本已禁用!");
				}
			}
			oldProcardTem.setProcardStyle(procardTemplate.getProcardStyle());
			changeLv = 2;
		}
		// 车型
		if (!isEquals(oldProcardTem.getCarStyle(), procardTemplate
				.getCarStyle())) {
			oldProcardTem.setCarStyle(procardTemplate.getCarStyle());
			changeLv = 2;
		}
		// 安全库存
		if (!isEquals(oldProcardTem.getSafeCount(), procardTemplate
				.getSafeCount())) {
			oldProcardTem.setSafeCount(procardTemplate.getSafeCount());
			changeLv = 2;
		}
		// 最低库存
		if (!isEquals(oldProcardTem.getLastCount(), procardTemplate
				.getLastCount())) {
			oldProcardTem.setLastCount(procardTemplate.getLastCount());
			changeLv = 2;
		}
		// 版本号(不允许直接修改)
		if (!isEquals(oldProcardTem.getBanBenNumber(), procardTemplate
				.getBanBenNumber())) {
			throw new RuntimeException("版本号不允许直接修改!");
		}

		// 材料类别
		if (!isEquals(oldProcardTem.getClType(), procardTemplate.getClType())) {
			oldProcardTem.setClType(procardTemplate.getClType());
			changeLv = 2;
		}
		// sameCard.setCaizhi(procardTem.getCaizhi());
		// 领料类型
		if (!isEquals(oldProcardTem.getLingliaoType(), procardTemplate
				.getLingliaoType())) {
			oldProcardTem.setLingliaoType(procardTemplate.getLingliaoType());
			changeLv = 2;
		}
		// 页数
		if (!isEquals(oldProcardTem.getPageTotal(), procardTemplate
				.getPageTotal())) {
			oldProcardTem.setPageTotal(procardTemplate.getPageTotal());
			changeLv = 2;
		}
		// 工艺编号
		if (!isEquals(oldProcardTem.getNumb(), procardTemplate.getNumb())) {
			oldProcardTem.setNumb(procardTemplate.getNumb());
			changeLv = 2;
		}
		// 发出日期
		if (!isEquals(oldProcardTem.getFachuDate(), procardTemplate
				.getFachuDate())) {
			oldProcardTem.setFachuDate(procardTemplate.getFachuDate());
			changeLv = 2;
		}
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode == null) {
			valueCode = "4";
		}
		if (oldProcardTem.getBzStatus().equals("初始")) {
			if (user != null) {
				oldProcardTem.setBianzhiId(user.getId());
				oldProcardTem.setBianzhiName(user.getName());
				oldProcardTem.setBzStatus("待编制");
				changeLv = 2;
			}
		}
		if (oldProcardTem.getBzStatus().equals("待编制")) {
			// 编制人
			if (valueCode.equals("2")) {
				// 批准人
				if (!isEquals(oldProcardTem.getPizhunId(), procardTemplate
						.getPizhunId())) {
					oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
					if (procardTemplate.getPizhunId() != null) {
						Users u = (Users) totalDao.getObjectById(Users.class,
								procardTemplate.getPizhunId());
						if (u != null) {
							oldProcardTem.setPizhunName(u.getName());
						}
					}
					changeLv = 2;
				}
			} else {
				// 校对人
				if (!isEquals(oldProcardTem.getJiaoduiId(), procardTemplate
						.getJiaoduiId())) {
					oldProcardTem.setJiaoduiId(procardTemplate.getJiaoduiId());
					if (procardTemplate.getJiaoduiId() != null) {
						Users u = (Users) totalDao.getObjectById(Users.class,
								procardTemplate.getJiaoduiId());
						if (u != null) {
							oldProcardTem.setJiaoduiName(u.getName());
						}
					}
					changeLv = 2;
				}

			}
		}
		if (oldProcardTem.getBzStatus().equals("已编制")) {
			if (valueCode.equals("4")) {
				// 审核人
				if (!isEquals(oldProcardTem.getShenheId(), procardTemplate
						.getShenheId())) {
					oldProcardTem.setShenheId(procardTemplate.getShenheId());
					if (procardTemplate.getShenheId() != null) {
						Users u = (Users) totalDao.getObjectById(Users.class,
								procardTemplate.getShenheId());
						if (u != null) {
							oldProcardTem.setShenheName(u.getName());
						}
					}
					changeLv = 2;
				}
			} else {
				// 批准人
				if (!isEquals(oldProcardTem.getPizhunId(), procardTemplate
						.getPizhunId())) {
					oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
					if (procardTemplate.getPizhunId() != null) {
						Users u = (Users) totalDao.getObjectById(Users.class,
								procardTemplate.getPizhunId());
						if (u != null) {
							oldProcardTem.setPizhunName(u.getName());
						}
					}
					changeLv = 2;
				}
			}
		}
		if (oldProcardTem.getBzStatus().equals("已校对")) {
			// 批准人
			if (!isEquals(oldProcardTem.getPizhunId(), procardTemplate
					.getPizhunId())) {
				oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
				if (procardTemplate.getPizhunId() != null) {
					Users u = (Users) totalDao.getObjectById(Users.class,
							procardTemplate.getPizhunId());
					if (u != null) {
						oldProcardTem.setPizhunName(u.getName());
					}
				}
				changeLv = 2;
			}
		}

		// 初始总成
		if (!isEquals(oldProcardTem.getLoadMarkId(), procardTemplate
				.getLoadMarkId())) {
			oldProcardTem.setLoadMarkId(procardTemplate.getLoadMarkId());
		}
		// 领料状态
		if (!isEquals(oldProcardTem.getLingliaostatus(), procardTemplate
				.getLingliaostatus())) {
			oldProcardTem
					.setLingliaostatus(procardTemplate.getLingliaostatus());
			if (changeLv == 0) {
				changeLv = 1;
			}
		}
		// 权值
		if (procardTemplate.getProcardStyle().equals("外购")) {
			if (!isEquals(oldProcardTem.getQuanzi1(), procardTemplate
					.getQuanzi1())) {
				oldProcardTem.setQuanzi1(procardTemplate.getQuanzi1());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
			if (!isEquals(oldProcardTem.getQuanzi2(), procardTemplate
					.getQuanzi2())) {
				oldProcardTem.setQuanzi2(procardTemplate.getQuanzi2());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
			// 半成品
			if (!isEquals(oldProcardTem.getNeedProcess(), procardTemplate
					.getNeedProcess())) {
				oldProcardTem.setNeedProcess(procardTemplate.getNeedProcess());
				changeLv = 2;
			}
		} else {
			if (!isEquals(oldProcardTem.getCorrCount(), procardTemplate
					.getCorrCount())) {
				oldProcardTem.setCorrCount(procardTemplate.getCorrCount());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
		}

		// 自制件激活
		if (!isEquals(oldProcardTem.getZzjihuo(), procardTemplate.getZzjihuo())) {
			oldProcardTem.setZzjihuo(procardTemplate.getZzjihuo());
			changeLv = 2;
		}
		boolean b = true;
		if (oldProcardTem.getProcardStyle() != null
				&& oldProcardTem.getProcardStyle().equals("总成")) {
			if (oldProcardTem.getDanjiaojian() == null
					|| !oldProcardTem.getDanjiaojian().equals("单交件")) {// 非单交件
				oldProcardTem.setTrademark(null);
				oldProcardTem.setSpecification(null);
				oldProcardTem.setYuanUnit(null);
				oldProcardTem.setQuanzi1(null);
				oldProcardTem.setQuanzi2(null);
				oldProcardTem.setLuhao(null);
				oldProcardTem.setNumber(null);
				oldProcardTem.setActualFixed(null);
				// return totalDao.update(procardTem) + "";
			} else {// 单交件
				b = b & totalDao.update(oldProcardTem);
				List<ProcardTemplate> plist = totalDao.query(
						"from ProcardTemplate where rootId=? and rootId!=id",
						oldProcardTem.getId());
				if (plist.size() > 0) {
					for (ProcardTemplate pson : plist) {
						pson.setProcardTemplate(null);
						pson.setDataStatus("删除");
						b = b & tbDownDataStatus(pson);
					}
				}
				return "true";
			}
		}
		boolean bool = false;
		oldProcardTem.sumXiaoHaoCount(0);
		// 当自制件的展开尺寸发生变化 自动修改下层外购件的权值。
		if ("自制".equals(oldProcardTem.getProcardStyle())) {
			if (procardTemplate.getThisWidth() != null
					&& procardTemplate.getThisLength() != null
					&& procardTemplate.getThisHight() != null
					&& (!procardTemplate.getThisHight().equals(
							oldProcardTem.getThisHight())
							|| procardTemplate.getThisWidth().equals(
									oldProcardTem.getThisWidth()) || procardTemplate
							.getThisLength().equals(
									oldProcardTem.getThisLength()))) {
				Set<ProcardTemplate> sonpt = oldProcardTem.getProcardTSet();
				for (ProcardTemplate p : sonpt) {
					// 计算板材外购件的权值2
					String hql_wgj = " from YuanclAndWaigj where markId = ?  ";
					YuanclAndWaigj wgj = null;
					if (oldProcardTem.getClType() != null
							&& oldProcardTem.getClType().length() > 0) {
						hql_wgj += " and caizhi = '"
								+ oldProcardTem.getClType()
								+ "' and density is not null and density >0";
						wgj = (YuanclAndWaigj) totalDao.getObjectByCondition(
								hql_wgj, p.getMarkId());
						if (wgj != null) {
							Float quanzhi2 = (procardTemplate.getThisLength() + 12)
									* (procardTemplate.getThisWidth() + 12)
									* (procardTemplate.getThisHight())
									* wgj.getDensity() / 1000000;
							p.setQuanzi2(quanzhi2);
							totalDao.update(p);
						}
					}
					if (procardTemplate.getBiaochu() != null
							&& procardTemplate.getBiaochu().length() > 0) {
						String hql_fuhe = " from PanelSize where caizhi = ?";
						PanelSize fuhe = (PanelSize) totalDao
								.getObjectByCondition(hql_fuhe, procardTemplate
										.getBiaochu());
						if (fuhe != null) {
						} else {
							hql_wgj += " and name like '%"
									+ procardTemplate.getBiaochu()
									+ "%' and areakg is not null and areakg >0";
							wgj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql_wgj, p
											.getMarkId());
							if (wgj != null) {
								Float quanzhi2 = (procardTemplate
										.getThisLength()
										* procardTemplate.getThisWidth() * 2)
										/ wgj.getAreakg() / 1000000;
								p.setQuanzi2(quanzhi2);
							}
						}
					}
				}
			}
		}
		// 表处
		if (!isEquals(oldProcardTem.getBiaochu(), procardTemplate.getBiaochu())) {
			oldProcardTem.setBiaochu(procardTemplate.getBiaochu());
			changeLv = 2;
		}
		// 长
		if (!isEquals(oldProcardTem.getThisLength(), procardTemplate
				.getThisLength())) {
			oldProcardTem.setThisLength(procardTemplate.getThisLength());
			changeLv = 2;
		}
		// 宽
		if (!isEquals(oldProcardTem.getThisWidth(), procardTemplate
				.getThisWidth())) {
			oldProcardTem.setThisWidth(procardTemplate.getThisWidth());
			changeLv = 2;
		}
		// 厚
		if (!isEquals(oldProcardTem.getThisHight(), procardTemplate
				.getThisHight())) {
			oldProcardTem.setThisHight(procardTemplate.getThisHight());
			changeLv = 2;
		}

		bool = totalDao.update(oldProcardTem);
		AttendanceTowServerImpl.updateJilu(3, oldProcardTem, oldProcardTem
				.getId(), oldProcardTem.getMarkId());
		String fMarkId = null;
		if (oldProcardTem.getProcardTemplate() != null) {
			fMarkId = (String) totalDao.getObjectByCondition(
					"select markId from ProcardTemplate where id=?",
					oldProcardTem.getFatherId());
		}

		List<ProcardTemplate> sameMarkIdList = null;
		if (bool & changeLv == 2) {// 二级同步修改所有的同件号
			sameMarkIdList = totalDao
					.query(
							"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') ",
							oldProcardTem.getMarkId());
		} else if (bool & changeLv == 1) {// 一级同步修改同父零件下的同件号
			if (fMarkId != null && fMarkId.length() > 0) {
				sameMarkIdList = totalDao
						.query(
								"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and procardTemplate.markId=?",
								oldProcardTem.getMarkId(), fMarkId);
			}
		}
		if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
			for (ProcardTemplate sameCard : sameMarkIdList) {
				if (!"总成".equals(oldProcardTem.getProcardStyle())
						&& sameCard.getBelongLayer() != null
						&& sameCard.getBelongLayer() != 1) {
					if ("待定".equals(sameCard.getProcardStyle())
							|| Util.isEquals(sameCard.getProcardStyle(),
									oldProcardTem.getProcardStyle())) {
						sameCard.setProcardStyle(oldProcardTem
								.getProcardStyle());
						sameCard.setWgType(oldProcardTem.getWgType());
					}
				}
				sameCard.setProName(oldProcardTem.getProName());
				sameCard.setUnit(oldProcardTem.getUnit());
				sameCard.setCarStyle(oldProcardTem.getCarStyle());
				sameCard.setSafeCount(oldProcardTem.getSafeCount());
				sameCard.setLastCount(oldProcardTem.getLastCount());
				// sameCard.setBanBenNumber(oldProcardTem.getBanBenNumber());
				sameCard.setTuhao(oldProcardTem.getTuhao());
				// sameCard.setTrademark(oldProcardTem.getTrademark());// 牌号(件号)
				sameCard.setSpecification(oldProcardTem.getSpecification());// 规格
				// sameCard.setYtuhao(oldProcardTem.getYtuhao());// 原材料图号
				// sameCard.setYuanName(oldProcardTem.getYuanName());// 原材料名称
				sameCard.setBiaochu(oldProcardTem.getBiaochu());
				// sameCard.setLuhao(oldProcardTem.getLuhao());// 炉号
				sameCard.setLoadMarkId(oldProcardTem.getLoadMarkId());
				sameCard.setImportance(oldProcardTem.getImportance());
				if (fMarkId != null
						&& fMarkId.length() > 0
						&& sameCard.getProcardTemplate() != null
						&& sameCard.getProcardTemplate().getMarkId() != null
						&& fMarkId.equals(sameCard.getProcardTemplate()
								.getMarkId())) {
					if (sameCard.getProcardStyle().equals("外购")) {
						// 上层同件号修改权值
						sameCard.setQuanzi1(oldProcardTem.getQuanzi1());
						sameCard.setQuanzi2(oldProcardTem.getQuanzi2());
					} else {
						sameCard.setCorrCount(oldProcardTem.getCorrCount());
					}
					sameCard.setLingliaostatus(oldProcardTem
							.getLingliaostatus());
				}
				// 当自制件的展开尺寸发生变化 自动修改下层外购件的权值。
				if ("自制".equals(sameCard.getProcardStyle())
						&& oldProcardTem.getThisWidth() != null
						&& oldProcardTem.getThisLength() != null
						&& oldProcardTem.getThisHight() != null
						&& (!oldProcardTem.getThisHight().equals(
								sameCard.getThisHight())
								|| !oldProcardTem.getThisWidth().equals(
										sameCard.getThisWidth()) || !oldProcardTem
								.getThisLength().equals(
										sameCard.getThisLength()))) {
					Set<ProcardTemplate> sonpt = sameCard.getProcardTSet();
					for (ProcardTemplate p : sonpt) {
						// 计算板材外购件的权值2
						String hql_wgj = " from YuanclAndWaigj where markId = ?  ";
						YuanclAndWaigj wgj = null;
						if (sameCard.getClType() != null
								&& sameCard.getClType().length() > 0) {
							hql_wgj += " and caizhi = '"
									+ sameCard.getClType()
									+ "' and density is not null and density >0";
							wgj = (YuanclAndWaigj) totalDao
									.getObjectByCondition(hql_wgj, p
											.getMarkId());
							if (wgj != null) {
								Float quanzhi2 = (oldProcardTem.getThisLength() + 12)
										* (oldProcardTem.getThisWidth() + 12)
										* (oldProcardTem.getThisHight())
										* wgj.getDensity() / 1000000;
								p.setQuanzi2(quanzhi2);
								totalDao.update(p);
							}
						} else if (sameCard.getBiaochu() != null
								&& sameCard.getBiaochu().length() > 0) {
							String hql_fuhe = " from PanelSize where caizhi = ?";
							PanelSize fuhe = (PanelSize) totalDao
									.getObjectByCondition(hql_fuhe, sameCard
											.getBiaochu());
							if (fuhe != null) {
							} else {
								hql_wgj += " and name like '%"
										+ sameCard.getBiaochu()
										+ "%' and areakg is not null and areakg >0";
								wgj = (YuanclAndWaigj) totalDao
										.getObjectByCondition(hql_wgj, p
												.getMarkId());
								if (wgj != null) {
									Float quanzhi2 = (oldProcardTem
											.getThisLength()
											* oldProcardTem.getThisWidth() * 2)
											/ wgj.getAreakg() / 1000000;
									p.setQuanzi2(quanzhi2);
									totalDao.update(p);
								}
							}
						}

					}
				}

				sameCard.setBili(oldProcardTem.getBili());
				sameCard.setClType(oldProcardTem.getClType());
				sameCard.setCaizhi(oldProcardTem.getCaizhi());
				// sameCard.setLingliaostatus(procardTem.getLingliaostatus());
				sameCard.setLingliaoType(oldProcardTem.getLingliaoType());
				sameCard.setStatus(oldProcardTem.getStatus());
				sameCard.setNumb(oldProcardTem.getNumb());
				sameCard.setPageTotal(oldProcardTem.getPageTotal());
				sameCard.setFachuDate(oldProcardTem.getFachuDate());
				sameCard.setBianzhiName(oldProcardTem.getBianzhiName());// 编制人
				sameCard.setBianzhiId(oldProcardTem.getBianzhiId());// 编制ID
				sameCard.setBianzhiDate(oldProcardTem.getBianzhiDate());// 编制时间
				sameCard.setJiaoduiName(oldProcardTem.getJiaoduiName());// 校对人
				sameCard.setJiaoduiId(oldProcardTem.getJiaoduiId());// 校对ID
				sameCard.setJiaoduiDate(oldProcardTem.getJiaoduiDate());// 校对时间
				sameCard.setShenheName(oldProcardTem.getShenheName());// 审核人
				sameCard.setShenheId(oldProcardTem.getShenheId());// 审核ID
				sameCard.setShenheDate(oldProcardTem.getShenheDate());// 审核时间
				sameCard.setPizhunName(oldProcardTem.getPizhunName());// 批准人
				sameCard.setPizhunId(oldProcardTem.getPizhunId());// 批准ID
				sameCard.setPizhunDate(oldProcardTem.getPizhunDate());// 批准时间
				sameCard.setNeedProcess(oldProcardTem.getNeedProcess());// 半成品
				sameCard.setZzjihuo(oldProcardTem.getZzjihuo());// 自制件激活
				bool = totalDao.update(sameCard);
				// AttendanceTowServerImpl.updateJilu(3,sameCard,
				// sameCard.getId(), sameCard.getMarkId()+"(同步修改)");
			}
		}
		// if (bool && bzbool) {
		// AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", "件号"
		// + procardTem.getMarkId() + "需要您前往编制,请及时处理,谢谢!",
		// new Integer[] { procardTem.getBianzhiId() },
		// "procardTemplateGyAction_findSonsForBz.action?id="
		// + procardTem.getRootId(), true);
		// }
		return "true";
	}

	public boolean isEquals(String str1, String str2) {
		if ((str1 == null || str1.length() == 0) && str2 != null
				&& str2.length() > 0) {
			return false;
		}
		if ((str2 == null || str2.length() == 0) && str1 != null
				&& str1.length() > 0) {
			return false;
		}
		if (str1 != null && str1.length() >= 0 && str2 != null
				&& str2.length() > 0 && !str1.equals(str2)) {
			return false;
		}
		return true;
	}

	public boolean isEquals(Float f1, Float f2) {
		if (f1 == null && f2 != null) {
			return false;
		}
		if (f2 == null && f1 != null) {
			return false;
		}
		if (f1 != null && f2 != null && !f1.equals(f2)) {
			return false;
		}
		return true;
	}

	public boolean isEquals(Integer i1, Integer i2) {
		if (i1 == null && i2 != null) {
			return false;
		}
		if (i2 == null && i1 != null) {
			return false;
		}
		if (i1 != null && i2 != null && !i1.equals(i2)) {
			return false;
		}
		return true;
	}

	@Override
	public List findAllMarkId_1(String type) {
		// TODO Auto-generated method stub
		if (type.equals("yes"))
			return totalDao
					.query("from ProcardTemplate where procardStyle <> '外购' and (dataStatus is null or dataStatus!='删除') and standardSize <> '' and errorRange <> '' ");
		else if (type.equals("no"))
			return totalDao
					.query("from ProcardTemplate where procardStyle <> '外购' and (dataStatus is null or dataStatus!='删除') and standardSize is null or standardSize = '' or errorRange is null or errorRange = '' ");
		else
			return null;
	}

	@Override
	public ProcardTemplate findProcardTemByIdAndMarkId(Integer id, String markId) {
		// TODO Auto-generated method stub
		return (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where id = ? and markId = ? and (dataStatus is null or dataStatus!='删除')",
						id, markId);
	}

	@Override
	public String updateProcard(ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub
		ProcardTemplate procardTemplate1 = findProcardTemByIdAndMarkId(
				procardTemplate.getId(), procardTemplate.getMarkId());
		if (procardTemplate1 != null) {
			procardTemplate1.setStandardSize(procardTemplate.getStandardSize());
			procardTemplate1.setErrorRange(procardTemplate.getErrorRange());
			if (totalDao.update(procardTemplate1))
				return "操作成功！";
		}
		return procardTemplate.getMarkId() + "模板不存在，操作失败！";
	}

	@Override
	public List<ProcardTemplate> getProcardTemplateZC() {
		String hql = "select markId,maxCount from ProcardTemplate where markId is not null and markId <> '' and procardStyle = '总成' and productStyle = '批产' and (dataStatus is null or dataStatus!='删除') GROUP BY markId,maxCount order by markId ";
		return totalDao.find(hql);
	}

	@Override
	public List<ProcardTemplate> getSameProcardTemplate(String markId,
			String banBenNumber, String productStyle) {
		// TODO Auto-generated method stub
		String addSql = null;
		if (banBenNumber == null || banBenNumber.length() == 0) {
			return totalDao
					.query(
							"from ProcardTemplate where  productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and (banBenNumber is null or banBenNumber='')  and  markId=? ",
							productStyle, markId);
		} else {
			return totalDao
					.query(
							"from ProcardTemplate where  productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and banBenNumber=? and  markId=? ",
							productStyle, banBenNumber, markId);
		}
	}

	@Override
	public Map<Integer, Object> findProcardSpecification(
			ProcardSpecification procardSpecification, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (procardSpecification == null) {
			procardSpecification = new ProcardSpecification();
		}
		String sql = "";
		String hql = totalDao.criteriaQueries(procardSpecification, sql);
		hql += " order by id desc";
		List listInt = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, listInt);
		map.put(2, count);
		return map;
	}

	@Override
	public List<ProcardTemplate> findProcessNameGroupByMarkId() {
		// TODO Auto-generated method stub
		List<String> markIdList = totalDao
				.query("select distinct markId from ProcardTemplate");
		if (markIdList != null && markIdList.size() > 0) {
			List<ProcardTemplate> list = new ArrayList<ProcardTemplate>();
			for (String markId : markIdList) {
				ProcardTemplate procard = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=?", markId);
				Set<ProcessTemplate> processSet = procard.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					String processName = "";
					int i = 0;
					for (ProcessTemplate process : processSet) {
						if (i == 0) {
							processName += process.getProcessName();
						} else {
							processName += ";" + process.getProcessName();
						}
						i++;
					}
					procard.setProName(processName);
					list.add(procard);
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public ProcardTemplate findprocardTemplateByMarkId(String markId,
			String name, String status) {
		String hql = " from ProcardTemplate where markId=? and proName = ? and productStyle =? and banbenStatus =? and (dataStatus is null or dataStatus!='删除')";
		String productStyle = "批产";
		if ("sz".equals(status)) {
			productStyle = "试制";
		}
		return (ProcardTemplate) totalDao.getObjectByCondition(hql, markId,
				name, productStyle, "默认");
	}

	@Override
	public String checkUpdatelimt(ProcardTemplate oldProcardTem,
			ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (oldProcardTem.getBzStatus() == null) {
			oldProcardTem.setBzStatus("初始");
		}
		if (oldProcardTem.getBzStatus().equals("初始") && procardTemplate != null) {
			oldProcardTem.setBianzhiId(procardTemplate.getBianzhiId());
			oldProcardTem.setBianzhiName(procardTemplate.getBianzhiName());
			oldProcardTem.setBianzhiDate(procardTemplate.getBianzhiDate());
		} else if (oldProcardTem.getBzStatus().equals("待编制")) {
			if (oldProcardTem.getBianzhiId() == null
					|| !oldProcardTem.getBianzhiId().equals(user.getId())) {
				// 暂时屏蔽以后再打开
				// errorMessage = "对不起您不是编制人,修改失败!";
				// return ERROR;
			} else if (procardTemplate != null) {
				oldProcardTem.setJiaoduiId(procardTemplate.getJiaoduiId());
				oldProcardTem.setJiaoduiName(procardTemplate.getJiaoduiName());
				oldProcardTem.setJiaoduiDate(procardTemplate.getJiaoduiDate());
			}
		} else if (oldProcardTem.getBzStatus().equals("已编制")) {
			if (oldProcardTem.getJiaoduiId() == null
					|| !oldProcardTem.getJiaoduiId().equals(user.getId())) {
				return "对不起您不是校对人,修改失败!";
			} else if (procardTemplate != null) {
				oldProcardTem.setShenheId(procardTemplate.getShenheId());
				oldProcardTem.setShenheName(procardTemplate.getShenheName());
				oldProcardTem.setShenheDate(procardTemplate.getShenheDate());
			}
		} else if (oldProcardTem.getBzStatus().equals("已校对")) {
			if (oldProcardTem.getShenheId() == null
					|| !oldProcardTem.getShenheId().equals(user.getId())) {
				return "对不起您不是审核人,修改失败!";
			} else if (procardTemplate != null) {
				oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
				oldProcardTem.setPizhunName(procardTemplate.getPizhunName());
				oldProcardTem.setPizhunDate(procardTemplate.getPizhunDate());
			}
		} else if (oldProcardTem.getBzStatus().equals("已审核")) {
			if (oldProcardTem.getPizhunId() == null
					|| !oldProcardTem.getPizhunId().equals(user.getId())) {
				return "对不起您不是批准人,修改失败!";
			}
		} else if (oldProcardTem.getBzStatus().equals("已批准")) {
			return "已批准状态不允许修改，请先前往申请设变或者版本升级!";
		}
		return "true";
	}

	@Override
	public List<String> findAllMarkId(String markId) {
		if (markId != null && markId.length() > 0) {
			String hql = " select markId from ProcardTemplate where markId like '%"
					+ markId + "%' group by markId ";
			return totalDao.findByPage(hql, 1, 10);
		}
		return null;
	}

	@Override
	public Map<String, Object> ProcardTemduibiWgj(String markId1, String markId2) {
		if (markId1 != null && !"".equals(markId1) && markId2 != null
				&& !"".equals(markId2)) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<ProcardTemplate> pt1_wg = totalDao
					.query(
							" FROM ProcardTemplate where fatherId in (SELECT  id  FROM ProcardTemplate WHERE markId = ? ) and  procardStyle = '外购' ",
							markId1);
			List<ProcardTemplate> pt2_wg = totalDao
					.query(
							" FROM ProcardTemplate where fatherId in (SELECT  id  FROM ProcardTemplate WHERE markId = ? )  and procardStyle = '外购'",
							markId2);
			for (int i = 0; i < pt1_wg.size(); i++) {
				ProcardTemplate pt1 = pt1_wg.get(i);
				boolean bool1_1 = false;
				boolean bool1_2 = false;
				boolean bool1_3 = false;
				if ((pt1.getBanBenNumber() == null || "".equals(pt1
						.getBanBenNumber()))) {
					bool1_1 = true;
				}
				if ((pt1.getWgType() == null || "".equals(pt1.getWgType()))) {
					bool1_2 = true;
				}
				if ((pt1.getKgliao() == null || "".equals(pt1.getKgliao()))) {
					bool1_3 = true;
				}
				for (int j = 0; j < pt2_wg.size(); j++) {
					ProcardTemplate pt2 = pt2_wg.get(j);
					boolean bool2_1 = false;
					boolean bool2_2 = false;
					boolean bool2_3 = false;
					if (bool1_1
							&& (pt2.getBanBenNumber() == null || "".equals(pt2
									.getBanBenNumber()))) {
						bool2_1 = true;
					}
					if (bool1_2
							&& (pt2.getWgType() == null || "".equals(pt2
									.getWgType()))) {
						bool2_2 = true;
					}
					if (bool1_3
							&& (pt2.getKgliao() == null || "".equals(pt2
									.getKgliao()))) {
						bool2_3 = true;
					}
					if (pt1.getMarkId().equals(pt2.getMarkId())
							&& (pt1.getBanBenNumber() != null && (bool2_1 || (pt1
									.getBanBenNumber() != null
									&& !"".equals(pt1.getBanBenNumber()) && pt1
									.getBanBenNumber().equals(
											pt2.getBanBenNumber()))))
							&& (bool2_2 || (pt1.getWgType() != null
									&& pt1.getWgType().length() > 0 && pt1
									.getWgType().equals(pt2.getWgType())))
							&& (bool2_3 || (pt1.getKgliao() != null
									&& pt1.getKgliao().length() > 0 && pt1
									.getKgliao().equals(pt2.getKgliao())))) {
						pt1_wg.remove(i);
						pt2_wg.remove(j);
						j--;
						i--;
					}
				}
			}
			List<ProcardTemplate> pt1_zz = totalDao
					.query(
							" FROM ProcardTemplate where fatherId in (SELECT  id  FROM ProcardTemplate WHERE markId = ? ) and procardStyle <> '外购' ",
							markId1);
			List<ProcardTemplate> pt2_zz = totalDao
					.query(
							" FROM ProcardTemplate where fatherId in (SELECT  id  FROM ProcardTemplate WHERE markId = ? ) and procardStyle <> '外购'",
							markId2);

			map.put("pt1_wg", pt1_wg);
			map.put("pt2_wg", pt2_wg);
			map.put("pt1_zz", pt1_zz);
			map.put("pt2_zz", pt2_zz);
			return map;
		}
		return null;
	}

	public List<ProcardTemplate> CheckIn(String jianhaoSet) {
		if (jianhaoSet == null) {
			return null;
		} else {
			String s = "";
			String[] ss = jianhaoSet.split(";");
			for (int i = 0; i < ss.length; i++) {
				s = s + ",'" + ss[i] + "'";
			}
			s = s.substring(1);
			String hql = "from ProcardTemplate where markId in (" + s
					+ ") or ( ywMarkId in (" + s + ") and procardStyle='总成')";
			return totalDao.find(hql);
		}
	}

	@Override
	public Object[] findwgProcard(Integer id, String status) {
		if (id != null) {
			ProcessTemplate processtem = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, id);
			ProcardTemplate procardt = processtem.getProcardTemplate();
			List<ProcardTemplate> wgProcardTList = null;
			if ("yibang".equals(status)) {
				wgProcardTList = totalDao
						.query(
								" from ProcardTemplate  where fatherId = ?  and (banbenStatus = '默认' or banbenStatus is null ) and (dataStatus is null or dataStatus!='删除') and markId in(  "
										+ "   select wgprocardMardkId  from ProcessAndWgProcardTem where procardMarkId = ? )  ",
								procardt.getId(), procardt.getMarkId());
			} else if ("weibang".equals(status)) {
				wgProcardTList = totalDao
						.query(
								" from ProcardTemplate  where fatherId = ?  and (banbenStatus = '默认' or banbenStatus is null ) and (dataStatus is null or dataStatus!='删除') and markId not in(  "
										+ "   select wgprocardMardkId  from ProcessAndWgProcardTem where procardMarkId = ? )  ",
								procardt.getId(), procardt.getMarkId());
			} else if ("gxyibang".equals(status)) {
				wgProcardTList = totalDao
						.query(
								" from ProcardTemplate  where fatherId = ?  and (banbenStatus = '默认' or banbenStatus is null ) and (dataStatus is null or dataStatus!='删除') and markId  in(  "
										+ "   select wgprocardMardkId  from ProcessAndWgProcardTem where procardMarkId = ? and processNo = ? )  ",
								procardt.getId(), procardt.getMarkId(),
								processtem.getProcessNO());
			} else if ("gxweibang".equals(status)) {
				wgProcardTList = totalDao
						.query(
								" from ProcardTemplate  where fatherId = ?  and (banbenStatus = '默认' or banbenStatus is null ) and (dataStatus is null or dataStatus!='删除') and markId not in(  "
										+ "   select wgprocardMardkId  from ProcessAndWgProcardTem where procardMarkId = ? and processNo = ? )  ",
								procardt.getId(), procardt.getMarkId(),
								processtem.getProcessNO());
			} else {
				wgProcardTList = (List<ProcardTemplate>) totalDao
						.query(
								" from ProcardTemplate where fatherId = ?  and (banbenStatus = '默认' or banbenStatus is null ) and (dataStatus is null or dataStatus!='删除') ",
								procardt.getId());
			}
			if (wgProcardTList == null || wgProcardTList.size() == 0) {
				return new Object[] { "该工序所对应的自制件下层无零件!" };
			}
			return new Object[] { "true", wgProcardTList };
		}
		return new Object[] { "请刷新后重试" };
	}

	@Override
	public String processAndwgProcard(Integer id, String[] markIds) {
		if (id != null) {
			ProcessTemplate processtem = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, id);
			ProcardTemplate procardt = processtem.getProcardTemplate();
			List<ProcessAndWgProcardTem> processAndWgPrcardList = totalDao
					.query(
							" from ProcessAndWgProcardTem where procardMarkId = ? and processNo = ? ",
							procardt.getMarkId(), processtem.getProcessNO());
			if (processAndWgPrcardList != null
					&& processAndWgPrcardList.size() > 0) {
				for (ProcessAndWgProcardTem processAndWgProcardTem : processAndWgPrcardList) {
					totalDao.delete(processAndWgProcardTem);
				}
			}
			if (markIds != null && markIds.length > 0) {
				for (int i = 0; i < markIds.length; i++) {
					ProcessAndWgProcardTem processAndWg = addPawPt2(markIds,
							processtem, procardt, i);
					if (!totalDao.save(processAndWg)) {
						return "关联外购件失败";
					}
				}
			}
			// 获取对应的生产零件然后重算他的工序可领数量
			List<Procard> procardList = totalDao.query(
					" from Procard where markId=? and (sbStatus is null or sbStatus!='删除') "
							+ "and status in('已发料','领工序')", procardt
							.getMarkId());
			if (procardList != null && procardList.size() > 0) {
				for (Procard procard : procardList) {
					String hql_process = "from ProcessInfor where procard.id=?  and status<>'完成'"
							+ " and processNO in (select processNo from ProcessAndWgProcardTem where procardMarkId=?)";
					List<ProcessInfor> list_process = totalDao.query(
							hql_process, procard.getId(), procard.getMarkId());
					for (ProcessInfor processInfor : list_process) {
						String hql_glprocess = "select min(minNumber) from Procard where procard.id=? "
								+ "and markId in (select wgprocardMardkId from ProcessAndWgProcardTem "
								+ "where procardMarkId=? and processNo=? and processName=?)";
						Float minNumber2 = (Float) totalDao
								.getObjectByCondition(hql_glprocess, procard
										.getId(), procard.getMarkId(),
										processInfor.getProcessNO(),
										processInfor.getProcessName());
						if (minNumber2 != null) {
							float cminNumber2 = (float) Math.ceil(minNumber2);
							if ((cminNumber2 - minNumber2) > 0.05) {
								minNumber2 = (float) Math.floor(minNumber2);
							} else {
								minNumber2 = cminNumber2;
							}
							processInfor.setTotalCount(minNumber2);
							totalDao.update(processInfor);
						}
					}
				}
			}
			return "true";
		}
		return "请刷新后重试!";
	}

	/**
	 * 
	 * @param markIds
	 * @param processtem
	 * @param procardt
	 * @param i
	 * @return
	 */
	private ProcessAndWgProcardTem addPawPt2(String[] markIds,
			ProcessTemplate processtem, ProcardTemplate procardt, int i) {
		ProcessAndWgProcardTem processAndWg = new ProcessAndWgProcardTem();
		processAndWg.setProcardMarkId(procardt.getMarkId());
		processAndWg.setProcessNo(processtem.getProcessNO());
		processAndWg.setProcessName(processtem.getProcessName());
		processAndWg.setWgprocardMardkId(markIds[i]);
		processAndWg.setAddTime(Util.getDateTime());
		return processAndWg;
	}

	@Override
	public List<ProcessAndWgProcardTem> findProcessAndwgProcard(Integer id) {
		if (id != null) {
			ProcessTemplate processtem = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, id);
			ProcardTemplate procardt = processtem.getProcardTemplate();
			List<ProcessAndWgProcardTem> processAndWgPrcardList = totalDao
					.query(
							" from ProcessAndWgProcardTem where procardMarkId = ? and processNo = ? ",
							procardt.getMarkId(), processtem.getProcessNO());
			return processAndWgPrcardList;
		}
		return null;
	}

	@Override
	public boolean isNeedJjPrice(Integer id) {
		if (id != null) {
			ProcessTemplate process = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, id);
			if (process.getProcessMomey() != null
					&& process.getProcessMomey() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isNeedJjPCPrice(Integer id) {
		if (id != null) {
			ProcessInfor process = (ProcessInfor) totalDao.get(
					ProcessTemplate.class, id);
			if (process.getProcessMoney() != null
					&& process.getProcessMoney() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String updatProcesPrice(ProcessTemplate process) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (process != null) {
			ProcessTemplate oldpt0 = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, process.getId());
			String markId = oldpt0.getProcardTemplate().getMarkId();
			List<ProcardTemplate> procardTList = totalDao
					.query(
							" from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus != '历史') "
									+ " and (dataStatus is null or dataStatus <> '删除')",
							markId);
			for (ProcardTemplate procardT0 : procardTList) {
				String hql_process = " from ProcessTemplate where procardTemplate.id = ? and processNO =? and processName =? ";
				ProcessTemplate oldpt = (ProcessTemplate) totalDao
						.getObjectByCondition(hql_process, procardT0.getId(),
								oldpt0.getProcessNO(), oldpt0.getProcessName());
				ProcessPriceUpdateLog ppul = new ProcessPriceUpdateLog(oldpt
						.getId(), oldpt.getProcessNO(), oldpt.getProcessName(),
						user.getName(), user.getCode(), Util.getDateTime(),
						procardT0.getMarkId(), procardT0.getProName(),
						procardT0.getId(), process.getProcesdianshu(), process
								.getProcessjjMoney(), process.getJjratio(),
						user.getName() + "于"
								+ Util.getDateTime("yyyy年MM月dd日  HH时")
								+ "修改工序计件单价由" + oldpt.getProcessjjMoney()
								+ "变为" + process.getProcessjjMoney() + "。工序点数由"
								+ oldpt.getProcesdianshu() + "变为"
								+ process.getProcesdianshu() + "。" + "奖金系数由"
								+ oldpt.getJjratio() + "变为"
								+ process.getJjratio() + "。");
				oldpt.setProcessjjMoney(process.getProcessjjMoney());
				oldpt.setProcesdianshu(process.getProcesdianshu());
				oldpt.setJjratio(process.getJjratio());
				oldpt.setSjprocessMomey(process.getSjprocessMomey());
				oldpt.setYkprocessMomey(process.getYkprocessMomey());
				ppul.setSjprocessMomey(process.getSjprocessMomey());
				ppul.setYkprocessMomey(process.getYkprocessMomey());
				if (totalDao.update(oldpt)) {
					if (!totalDao.save(ppul)) {
						return "修改失败";
					}
				} else {
					return "修改失败";
				}
			}
		}
		return "true";
	}

	@Override
	public String updatProcesPcPrice(ProcessInfor process) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		if (process != null) {
			ProcessInfor oldpt = (ProcessInfor) totalDao.get(
					ProcessInfor.class, process.getId());
			Procard procard = oldpt.getProcard();
			ProcessPriceUpdateLog ppul = new ProcessPriceUpdateLog(oldpt
					.getProcessNO(), oldpt.getProcessName(), user.getName(),
					user.getCode(), Util.getDateTime(), procard.getMarkId(),
					procard.getProName(), process.getProcesdianshu(), process
							.getProcessjjMoney(), process.getProcesdianshu(),
					procard.getYwMarkId(), procard.getSelfCard(),
					oldpt.getId(), procard.getId(), "");
			oldpt.setProcessjjMoney(process.getProcessjjMoney());
			oldpt.setProcesdianshu(process.getProcesdianshu());
			oldpt.setJjratio(process.getJjratio());
			oldpt.setSjprocessMomey(process.getSjprocessMomey());
			oldpt.setYkprocessMomey(process.getYkprocessMomey());
			ppul.setSjprocessMomey(process.getSjprocessMomey());
			ppul.setYkprocessMomey(process.getYkprocessMomey());
			if (totalDao.update(oldpt)) {
				Double jjratio = oldpt.getJjratio();
				// 同步更新该工序所有已生成的奖金明细;
				List<UserMoneyDetail> umdlist = totalDao.query(
						" from UserMoneyDetail where processInforId = ?", oldpt
								.getId());
				if (umdlist != null && umdlist.size() > 0) {
					for (UserMoneyDetail umd : umdlist) {
						// processjjAllMoney =
						// proLog.getProcessjjMoney()*proLog.getSubmitNumber()*jjratio*oldProcess.getProcesdianshu();
						// processjjAllMoneygc =
						// proLog.getProcessjjMoney()*proLog.getSubmitNumber()*oldProcess.getProcesdianshu();
						ProcessInforReceiveLog proLog = (ProcessInforReceiveLog) totalDao
								.get(ProcessInforReceiveLog.class, umd
										.getReceiveLogId());
						proLog.setProcessjjMoney(oldpt.getProcessjjMoney());
						proLog.setJjratio(oldpt.getJjratio());
						proLog.setProcesdianshu(oldpt.getProcesdianshu());
						proLog.setSjprocessMomey(oldpt.getSjprocessMomey());
						proLog.setYkprocessMomey(oldpt.getYkprocessMomey());
						if (jjratio == null || jjratio == 0) {
							jjratio = 1d;
						}
						Double processjjAllMoney = oldpt.getProcesdianshu()
								* oldpt.getProcessjjMoney() * jjratio
								* proLog.getSubmitNumber();
						Double processjjAllMoneygc = oldpt.getProcesdianshu()
								* oldpt.getProcessjjMoney()
								* proLog.getSubmitNumber();
						proLog.setProcessjjAllMoney(processjjAllMoney);
						String[] usercodes = proLog.getUsercodes().split(",");
						Float nowMoney = umd.getNowMoney();
						umd
								.setNowMoney((processjjAllMoney.floatValue() / usercodes.length));
						umd.setProcessjjAllMoneygc(processjjAllMoneygc
								/ usercodes.length);
						UserMonthMoney userMonthMoney = umd.getUserMonthMoney();
						userMonthMoney.setMoney(userMonthMoney.getMoney()
								- nowMoney + umd.getNowMoney());
						totalDao.update(proLog);
						totalDao.update(umd);
						totalDao.update(userMonthMoney);
					}
				}

				return totalDao.save(ppul) + "";
			}
		}
		return "";
	}

	@Override
	public Object[] findProUpdatePriceLog(ProcessPriceUpdateLog ppul,
			int pageNo, int pageSize) {
		if (ppul == null) {
			ppul = new ProcessPriceUpdateLog();
		}
		String hql = totalDao.criteriaQueries(ppul, null);
		List<ProcessPriceUpdateLog> ppulList = totalDao.query(hql
				+ " order by id desc");
		int count = totalDao.getCount(hql);
		return new Object[] { ppulList, count };
	}

	@Override
	public String daoruProcessJJMoney(File ppulFile) {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		String msg = "true";
		boolean flag = true;
		String fileName = Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int i = 0;
		try {
			FileCopyUtils.copy(ppulFile, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = jxl.Workbook.getWorkbook(is);// 创建workbook
			}
			StringBuffer strb = new StringBuffer();
			Integer errorCount = 0;// 错误数量
			Integer cfCount = 0;// 重复数量
			Integer successCount = 0;// 成功数量
			jxl.Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			int count = 0;
			if (exclecolums > 1) {
				jindu_sum = (Integer) session
						.getAttribute("jindu_sum_daoruProcessJJMoney");
				if (jindu_sum == null) {
					session.setAttribute("jindu_sum_daoruProcessJJMoney",
							exclecolums - 1);
				}
				List<Integer> strList = new ArrayList<Integer>();
				for (i = 1; i < exclecolums; i++) {
					jxl.Cell[] cells = st.getRow(i);
					if (cells.length < 2) {
						continue;
					}
					String strprocessNo = cells[1].getContents();// 工序号
					String processName = cells[2].getContents();// 工序名
					String markId = cells[3].getContents();// 件号
					String proName = cells[4].getContents();// 零件名
					Double procesdianshu = null;
					Double processjjMoney = null;
					Double sjprocessMomey = null;
					Double sjprocessMomey0 = null;
					if (cells[5].getType() == jxl.CellType.NUMBER) {
						NumberCell numberCell = (NumberCell) cells[5]; // 工序点数
						procesdianshu = numberCell.getValue();
					}
					if (cells[6].getType() == jxl.CellType.NUMBER) {
						NumberCell numberCell = (NumberCell) cells[6]; // 工序计件工资
						processjjMoney = numberCell.getValue();
					}
					if (cells[7].getType() == jxl.CellType.NUMBER) {
						NumberCell numberCell = (NumberCell) cells[7]; // 奖金系数
						sjprocessMomey = numberCell.getValue();
					}
					String ywMarkId = "";
					if (cells.length == 9) {
						ywMarkId = cells[8].getContents();// 业务件号
					}
					if (processName == null || processName.length() == 0) {
						jindu_cl = (Integer) session
								.getAttribute("jindu_cl_daoruProcessJJMoney");
						if (jindu_cl == null) {
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", 1);
						} else {
							jindu_cl++;
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", jindu_cl);
						}
						errorCount++;
						strb.append("第" + (i + 1) + "没有填写工序名称<br/>");
						continue;
					}
					if (markId == null || markId.length() == 0) {
						jindu_cl = (Integer) session
								.getAttribute("jindu_cl_daoruProcessJJMoney");
						if (jindu_cl == null) {
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", 1);
						} else {
							jindu_cl++;
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", jindu_cl);
						}
						errorCount++;
						strb.append("第" + (i + 1) + "没有填写件号<br/>");
						continue;
					}
					Integer processNo = null;
					ProcessPriceUpdateLog ppul = new ProcessPriceUpdateLog();
					try {
						if (strprocessNo != null && strprocessNo.length() > 0) {
							processNo = Integer.parseInt(strprocessNo);
						} else {
							jindu_cl = (Integer) session
									.getAttribute("jindu_cl_daoruProcessJJMoney");
							if (jindu_cl == null) {
								session.setAttribute(
										"jindu_cl_daoruProcessJJMoney", 1);
							} else {
								jindu_cl++;
								session.setAttribute(
										"jindu_cl_daoruProcessJJMoney",
										jindu_cl);
							}
							strb.append("第" + (i + 1) + "没有工序号<br/>");
							errorCount++;
							continue;
						}
					} catch (Exception e) {
						jindu_cl = (Integer) session
								.getAttribute("jindu_cl_daoruProcessJJMoney");
						if (jindu_cl == null) {
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", 1);
						} else {
							jindu_cl++;
							session.setAttribute(
									"jindu_cl_daoruProcessJJMoney", jindu_cl);
						}
						errorCount++;
						strb.append("第" + (i + 1) + "行,工序号" + strprocessNo
								+ "填写不规范!应为数字<br/>");
					}
					Double jjratio = 0d;
					Double ykprocessMomey = 0d;
					ppul.setProcessName(processName);
					ppul.setMarkId(markId);
					ppul.setProName(proName);
					ppul.setProcesdianshu(procesdianshu);
					ppul.setProcessjjMoney(processjjMoney);

					if (sjprocessMomey != null && processjjMoney != null) {
						ykprocessMomey = processjjMoney - sjprocessMomey;
						if (processjjMoney > 0) {
							jjratio = sjprocessMomey / processjjMoney;
						}
					}
					ppul.setJjratio(jjratio);
					ppul.setYkprocessMomey(ykprocessMomey);
					ppul.setYwMarkId(ywMarkId);
					List<ProcardTemplate> procardtList = null;
					ProcessTemplate processT = null;
					String str_ = "";
					if (ywMarkId != null && ywMarkId.length() > 0) {
						str_ += " and ywMarkId = '" + ywMarkId + "'";
					}
					procardtList = totalDao
							.query(
									" from ProcardTemplate where markId = ? "
											+ str_
											+ "  and ( banbenStatus ='默认'or banbenStatus is null) and (dataStatus is null or dataStatus <> '删除' )",
									markId);
					if (procardtList != null && procardtList.size() > 0) {
						for (int m = 0; m < procardtList.size(); m++) {
							count++;
							ProcardTemplate procardt = procardtList.get(m);
							ppul.setProcardId(procardt.getId());
							processT = (ProcessTemplate) totalDao
									.getObjectByCondition(
											" from ProcessTemplate where procardTemplate.id = ? and processName = ? and processNo = ?",
											procardt.getId(), processName,
											processNo);
							if (processT != null) {
								processT.setProcessjjMoney(processjjMoney);
								processT.setSjprocessMomey(sjprocessMomey);
								processT.setYkprocessMomey(ykprocessMomey);
								processT.setJjratio(jjratio);
								processT.setProcesdianshu(procesdianshu);
								if (processT.getProcessMomey() != null
										&& processT.getProcessMomey() > 0) {
									jindu_cl = (Integer) session
											.getAttribute("jindu_cl_daoruProcessJJMoney");
									if (jindu_cl == null) {
										session.setAttribute(
												"jindu_cl_daoruProcessJJMoney",
												1);
									} else {
										jindu_cl++;
										session.setAttribute(
												"jindu_cl_daoruProcessJJMoney",
												jindu_cl);
									}
									errorCount++;
									strb.append("第" + (i + 1) + "行，" + "工序号:"
											+ processNo + "工序名:" + processName
											+ "件号:" + markId
											+ "。该工序已有另一套奖金计算方式，添加失败!<br/>");
									break;
								}
								ppul.setProcessNo(processT.getProcessNO());
								ppul.setProcessId(processT.getId());
								ppul.setUpdateTime(Util.getDateTime());
								ppul.setUserName(user.getName());
								ppul.setUsercode(user.getCode());
								ppul
										.setMore(user.getName()
												+ "于"
												+ Util
														.getDateTime("yyyy年MM月dd日  HH时MM分")
												+ "导入");
								if (totalDao.update(processT)) {
									totalDao.save(ppul);
									if (m == 0) {
										successCount++;
										Integer jindu_cl = (Integer) session
												.getAttribute("jindu_cl_daoruProcessJJMoney");
										if (jindu_cl == null) {
											session
													.setAttribute(
															"jindu_cl_daoruProcessJJMoney",
															1);
										} else {
											jindu_cl++;
											session
													.setAttribute(
															"jindu_cl_daoruProcessJJMoney",
															jindu_cl);
										}
									}

									// 同步更新所有现有批次工序上的工序单价;
									String str = "";
									if (procardt.getBanBenNumber() != null
											&& procardt.getBanBenNumber()
													.length() > 0) {
										str += " and banBenNumber = '"
												+ procardt.getBanBenNumber()
												+ "'";
									} else {
										str += " and (banBenNumber is null or banBenNumber = '')";
									}
									if (procardt.getYwMarkId() != null
											&& procardt.getYwMarkId().length() > 0) {
										str += " and ywMarkId = '"
												+ procardt.getYwMarkId() + "'";
									} else {
										str += " and (ywMarkId is null or ywMarkId = '')";
									}
									String hql_process = "from ProcessInfor where procard.id in (select id from Procard where status  in('初始','已发卡','已发料','领工序') and markId = ?  "
											+ str + ") and processNO = ?";
									List<ProcessInfor> ListProcess = totalDao
											.query(hql_process, procardt
													.getMarkId(), processT
													.getProcessNO());
									for (ProcessInfor processInfor : ListProcess) {
										processInfor.setProcesdianshu(processT
												.getProcesdianshu());
										processInfor.setProcessjjMoney(processT
												.getProcessjjMoney());
										processInfor.setJjratio(processT
												.getJjratio());
										processInfor.setYkprocessMomey(processT
												.getYkprocessMomey());
										processInfor.setSjprocessMomey(processT
												.getSjprocessMomey());
										totalDao.update(processInfor);
									}

								}
							} else {
								jindu_cl = (Integer) session
										.getAttribute("jindu_cl_daoruProcessJJMoney");
								if (jindu_cl == null) {
									session.setAttribute(
											"jindu_cl_daoruProcessJJMoney", 1);
								} else {
									jindu_cl++;
									session.setAttribute(
											"jindu_cl_daoruProcessJJMoney",
											jindu_cl);
								}
								strb.append("第" + (i + 1) + "行,在自制件" + markId
										+ "未找到工序名为:" + processName + "工序号为："
										+ processNo + "的工序.<br/>");
								errorCount++;
								break;
							}

						}

					}
					if (count % 200 == 0) {
						totalDao.clear();
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个<br/>失败" + errorCount
						+ "个<br/>重复" + cfCount + "个<br/>失败的行数分别为：<br/>"
						+ strb.toString();
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "第" + (i + 1) + "行" + "有异常，" + e;
			e.printStackTrace();
		}
		return msg;

	}

	@Override
	public List<ProcessInfor> showpcProcess(Integer id) {
		if (id != null) {

			ProcessTemplate processT = (ProcessTemplate) totalDao.get(
					ProcessTemplate.class, id);
			ProcardTemplate pocardT = processT.getProcardTemplate();
			String str = "";
			if (pocardT.getBanBenNumber() != null
					&& pocardT.getBanBenNumber().length() > 0) {
				str += " and banBenNumber = '" + pocardT.getBanBenNumber()
						+ "'";
			} else {
				str += " and (banBenNumber is null or banBenNumber = '')";
			}
			if (pocardT.getYwMarkId() != null
					&& pocardT.getYwMarkId().length() > 0) {
				str += " and ywMarkId = '" + pocardT.getYwMarkId() + "'";
			} else {
				str += " and (ywMarkId is null or ywMarkId = '')";
			}
			String hql_process = "from ProcessInfor where procard.id in ( from Procard where status  in('初始','已发卡','已发料','领工序') and markId = ?  "
					+ str + ") and processNO = ?";
			List<ProcessInfor> ListProcess = totalDao.query(hql_process,
					pocardT.getMarkId(), processT.getProcessNO());
			for (ProcessInfor processInfor : ListProcess) {
				Procard proard = processInfor.getProcard();
				processInfor.setMarkId(proard.getMarkId());
				processInfor.setSelfCard(proard.getSelfCard());
				processInfor.setCount(proard.getFilnalCount());
			}
			return ListProcess;

		}
		return null;
	}

	@Override
	public void processAndWgProcard(Integer rootId) {
		String root_hql = "";
		if (rootId != null) {
			root_hql = " and rootId = " + rootId;
		}
		String hql = " from ProcardTemplate where procardStyle  <> '外购' and (banbenStatus = '默认' or banbenStatus is null or banbenStatus = '') and (dataStatus<> '删除' or dataStatus is null or dataStatus = '' )";
		List<ProcardTemplate> procardTList = totalDao.query(hql + root_hql);
		int count = 0;
		for (ProcardTemplate procardT : procardTList) {
			count++;
			List<ProcessTemplate> processList = totalDao
					.query(
							" from ProcessTemplate where procardTemplate.id =? and processName <> '入库'   order by processNO ",
							procardT.getId());
			for (int i = 0; i < processList.size(); i++) {
				ProcessTemplate processT = processList.get(i);
				// 1.板材绑定在零件的第1道工序（料号1.01开始的为板材料号）
				String yclSql = "";
				if (i == 0) {
					Category category = (Category) totalDao
							.getObjectByCondition(
									" from Category where name =? ", "板材");
					if (category != null) {
						getWgtype(category);
					}
					Category category1 = (Category) totalDao
							.getObjectByCondition(
									" from Category where name =? ", "型材");
					if (category1 != null) {
						getWgtype(category1);
					}
					if (strbu.length() > 1) {
						strbu = new StringBuffer(strbu.toString().substring(1));
					}
					yclSql = " and wgType in (" + strbu.toString() + ")";
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ?  "
											+ yclSql
											+ " and procardStyle = '外购'   and (banbenStatus = '默认' or banbenStatus is null) "
											+ "and (dataStatus is null or dataStatus <> '删除')",
									procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				// 2.粉末绑定在喷涂工序上（料号1.08开始的为粉末料号）
				if (processT.getProcessName() != null
						&& processT.getProcessName().indexOf("喷涂") >= 0) {
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ? and wgType = '粉末' and procardStyle = '外购'  and (banbenStatus = '默认' or banbenStatus is null)"
											+ " and (dataStatus is null or dataStatus <> '删除') ",
									procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				// 3.所有总成件下的外购件都绑定在总成的组装工序上
				if ("总成".equals(procardT.getProcardStyle())
						&& "组装".equals(processT.getProcessName())) {
					String str = "";
					if (i == 0) {
						str = "  and wgType not in (" + strbu.toString() + ") ";
					}
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ?  and procardStyle = '外购'   and (banbenStatus = '默认' or banbenStatus is null)"
											+ " and (dataStatus is null or dataStatus <> '删除') "
											+ str, procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				// 5.碰焊、氩弧焊或二氧化碳焊（规格以GB13681开头的外购件绑定在这些焊接工序下）
				if ("碰焊".equals(processT.getProcessName())
						|| "氩弧焊".equals(processT.getProcessName())
						|| "二氧化碳焊".equals(processT.getProcessName())) {
					String str = "";
					if (i == 0) {
						str = "  and wgType not in (" + strbu.toString() + ") ";
					}
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ?  and procardStyle = '外购' and  specification like 'GB13681%'   and (banbenStatus = '默认' or banbenStatus is null)"
											+ " and (dataStatus is null or dataStatus <> '删除') "
											+ str, procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				// YT6.1件号开头的下所有除粉末外购件都绑定到焊接工序上
				if (procardT.getMarkId().startsWith("YT6.1")
						&& processT.getProcessName().indexOf("焊") >= 0) {
					String str = "";
					if (i == 0) {
						str = "  and wgType not in (" + strbu.toString() + ") ";
					}
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ?  and procardStyle = '外购' and  wgType <> '粉末'   and (banbenStatus = '默认' or banbenStatus is null)"
											+ " and (dataStatus is null or dataStatus <> '删除') "
											+ str, procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}

				// 压铆工序绑定的外购件(以下“型号”是指外购件规格前面的几个字母)
				// S-M CLS JM BSO SOS BSOS FHS NFHS FH BS SO SP ZS GN AS FC
				if ("压铆".equals(processT.getProcessName())) {
					String str = "";
					if (i == 0) {
						str = " and wgType not in (" + strbu.toString() + ")";
					}
					String hql_wg = " from  ProcardTemplate where fatherId = ?  and procardStyle = '外购' "
							+ " and (specification like 'S-M%' or specification like 'CLS%' or specification like 'JM%' "
							+ " or specification like 'BSO%' or specification like 'SOS%' or specification like 'BSOS%' or specification like 'FHS%' "
							+ " or specification like 'NFHS%' or specification like 'FH%' or specification like 'BS%' or specification like 'SO%'"
							+ " or specification like 'SP%' or specification like 'ZS%' or specification like 'GN%' or specification like 'AS%' or "
							+ " specification like 'FC%'  )   and (banbenStatus = '默认' or banbenStatus is null) and (dataStatus is null or dataStatus <> '删除')";
					List<ProcardTemplate> wgProcardList = totalDao.query(hql_wg
							+ str, procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				strbu = new StringBuffer();
			}
			// 4.只有唯一一道组装工序的组件所有它下阶外购件都绑定在该组装工序上
			if (processList != null && processList.size() == 1) {
				ProcessTemplate processT = processList.get(0);
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name =? ", "板材");
				if (category != null) {
					getWgtype(category);
				}
				Category category1 = (Category) totalDao.getObjectByCondition(
						" from Category where name =? ", "型材");
				if (category1 != null) {
					getWgtype(category1);
				}
				if (strbu.length() > 1) {
					strbu = new StringBuffer(strbu.toString().substring(1));
				}
				String str = "  and wgType not in (" + strbu.toString() + ") ";
				List<ProcardTemplate> wgProcardList = totalDao
						.query(
								" from  ProcardTemplate where fatherId = ?  and procardStyle = '外购'  and (banbenStatus = '默认' or banbenStatus is null) "
										+ " and (dataStatus is null or dataStatus <> '删除') "
										+ str, procardT.getId());
				if (wgProcardList != null && wgProcardList.size() > 0) {
					for (ProcardTemplate wgprocardT : wgProcardList) {
						addPawPt(procardT, processT, wgprocardT);
					}
				}
				strbu = new StringBuffer();
			}

			// 5.当只有2道工序，且第二道工序为“转ⅹⅹ”时，所有外购件绑定在第一道工序上
			if (processList != null && processList.size() == 2) {
				ProcessTemplate processT = processList.get(0);
				ProcessTemplate processT2 = processList.get(1);
				if (processT2.getProcessName().indexOf("转") >= 0) {
					Category category = (Category) totalDao
							.getObjectByCondition(
									" from Category where name =? ", "板材");
					if (category != null) {
						getWgtype(category);
					}
					Category category1 = (Category) totalDao
							.getObjectByCondition(
									" from Category where name =? ", "型材");
					if (category1 != null) {
						getWgtype(category1);
					}
					if (strbu.length() > 1) {
						strbu = new StringBuffer(strbu.toString().substring(1));
					}
					String str = "  and wgType not in (" + strbu.toString()
							+ ") ";
					List<ProcardTemplate> wgProcardList = totalDao
							.query(
									" from  ProcardTemplate where fatherId = ?  and procardStyle = '外购'  and (banbenStatus = '默认' or banbenStatus is null)  "
											+ " and (dataStatus is null or dataStatus <> '删除')"
											+ str, procardT.getId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (ProcardTemplate wgprocardT : wgProcardList) {
							addPawPt(procardT, processT, wgprocardT);
						}
					}
				}
				strbu = new StringBuffer();
			}
			if (count % 200 == 0) {
				totalDao.clear();
			}
		}
	}

	/**
	 * 
	 * @param procardT
	 * @param processT
	 * @param wgprocardT
	 */
	private void addPawPt(ProcardTemplate procardT, ProcessTemplate processT,
			ProcardTemplate wgprocardT) {
		ProcessAndWgProcardTem pawpt = (ProcessAndWgProcardTem) totalDao
				.getObjectByCondition(
						" from ProcessAndWgProcardTem where procardMarkId =? and wgprocardMardkId =? and processNo = ? and processName =? ",
						procardT.getMarkId(), wgprocardT.getMarkId(), processT
								.getProcessNO(), processT.getProcessName());
		if (pawpt == null) {
			pawpt = new ProcessAndWgProcardTem();
			pawpt.setProcardMarkId(procardT.getMarkId());// 自制件件号
			pawpt.setWgprocardMardkId(wgprocardT.getMarkId());// 外购件件号
			pawpt.setProcessNo(processT.getProcessNO());// 工序号
			pawpt.setProcessName(processT.getProcessName());// 工序名
			pawpt.setAddTime(Util.getDateTime());// 添加时间
			pawpt.setSource("一键添加");
			totalDao.save(pawpt);
		}
	}

	@Override
	public List<ProcardTemplate> findwbdProcessWgProcard(Integer id) {
		if (id != null) {
			List<ProcardTemplate> procardTList = totalDao
					.query(
							" from ProcardTemplate where procardStyle <> '外购' and rootId = ?  and (banbenStatus = '默认' or banbenStatus is null)"
									+ " and (dataStatus <> '删除' or dataStatus is null ) ",
							id);
			List<ProcardTemplate> wgProcardTList = new ArrayList<ProcardTemplate>();
			for (ProcardTemplate procardT : procardTList) {
				List<ProcardTemplate> wgprocardList1 = totalDao
						.query(
								" from ProcardTemplate where procardStyle = '外购' and fatherId =?  and (banbenStatus = '默认' or banbenStatus is null)"
										+ " and (dataStatus <> '删除' or dataStatus is null )",
								procardT.getId());
				for (ProcardTemplate wgprocardT : wgprocardList1) {
					int count = totalDao
							.getCount(
									" from ProcessAndWgProcardTem where procardMarkId = ? and wgprocardMardkId = ? "
											+ " and processName in (select processName from ProcessTemplate where procardTemplate.id = ? and "
											+ " (dataStatus is null or dataStatus <> '删除') )",
									procardT.getMarkId(), wgprocardT
											.getMarkId(), procardT.getId());
					if (count == 0) {
						wgprocardT.setProcardTemplate(procardT);
						wgProcardTList.add(wgprocardT);
					}
				}
			}
			return wgProcardTList;
		}
		return null;
	}

	// 板材绑定在零件的第1道工序（料号1.01开始的为板材料号）
	@Override
	public void processAndWgProcard1() {
		List<ProcardTemplate> wgProcardTList = totalDao
				.query(" from ProcardTemplate where procardStyle = '外购' and markId like '1.01%' and (banbenStatus = '默认' or banbenStatus is null) ");
		for (ProcardTemplate wgprocardT : wgProcardTList) {
			ProcessTemplate processT = (ProcessTemplate) totalDao
					.getObjectByCondition(
							" from ProcessTemplate where procardTemplate.id = ? order by processNO ",
							wgprocardT.getProcardTemplate().getId());
			if (processT != null) {
				addPawPt1(wgprocardT, processT);
			}
		}
	}

	/**
	 * 
	 * @param wgprocardT
	 * @param processT
	 */
	private void addPawPt1(ProcardTemplate wgprocardT, ProcessTemplate processT) {
		ProcessAndWgProcardTem pawpt = new ProcessAndWgProcardTem();
		pawpt.setProcardMarkId(wgprocardT.getProcardTemplate().getMarkId());// 自制件件号
		pawpt.setWgprocardMardkId(wgprocardT.getMarkId());// 外购件件号
		pawpt.setProcessNo(processT.getProcessNO());// 工序号
		pawpt.setProcessName(processT.getProcessName());// 工序名
		pawpt.setAddTime(Util.getDateTime());// 添加时间
		totalDao.save(pawpt);
	}

	// 粉末绑定在喷涂工序上（料号1.08开始的为粉末料号）
	@Override
	public void processAndWgProcard2() {
		List<ProcardTemplate> wgProcardTList = totalDao
				.query(" from ProcardTemplate where procardStyle = '外购' and markId like '1.08%' and (banbenStatus = '默认' or banbenStatus is null) ");
		for (ProcardTemplate wgprocardT : wgProcardTList) {
			List<ProcessTemplate> processTList = totalDao
					.query(
							" from ProcessTemplate where procardTemplate.id = ? and processName = '喷涂'  order by processNO ",
							wgprocardT.getProcardTemplate().getId());
			if (processTList != null && processTList.size() > 0) {
				for (ProcessTemplate processT : processTList) {
					if (processT != null) {
						addPawPt1(wgprocardT, processT);
					}
				}
			}

		}

	}

	// 所有总成件下的外购件都绑定在总成的组装工序上
	@Override
	public void processAndWgProcard3() {
		List<ProcardTemplate> wgProcardTList = totalDao
				.query(" from ProcardTemplate where procardStyle = '外购' and fatherId =rootId  and markId not like '1.01%'  and (banbenStatus = '默认' or banbenStatus is null) ");
		for (ProcardTemplate wgprocardT : wgProcardTList) {
			ProcessTemplate processT = (ProcessTemplate) totalDao
					.getObjectByCondition(
							" from ProcessTemplate where procardTemplate.id = ? and processName = '组装'  order by processNO ",
							wgprocardT.getProcardTemplate().getId());
			if (processT != null) {
				addPawPt1(wgprocardT, processT);
			}
		}

	}

	// 只有唯一一道组装工序的组件所有它下阶外购件都绑定在该组装工序上
	@Override
	public void processAndWgProcard4() {
		// SELECT * FROM ta_sop_w_ProcessTemplate where id IN ( SELECT
		// fk_pricessTId FROM ta_sop_w_ProcessTemplate where fk_pricessTId IN (
		// SELECT id FROM ta_sop_w_ProcardTemplate where procardStyle = '自制' )
		// GROUP BY fk_pricessTId HAVING count(*)=2) and processName = '配齐零件'
		String hql_processT = " from ProcessTemplate where id in ( select procardTemplate.id from ProcessTemplate where procardTemplate.id in (select id from ProcessTemplate where procardStyle = '自制') GROUP BY   procardTemplate.id HAVING  count(*)=1 )  and processName = '组装' ";
		List<ProcessTemplate> processTList = totalDao.query(hql_processT);
		if (processTList != null && processTList.size() > 0) {
			for (ProcessTemplate processT : processTList) {
				List<ProcardTemplate> wgprocardTList = totalDao
						.query(
								" from ProcardTemplate where fatherId = ? and markId not like '1.08%' and procardStyle = '外购'  and (banbenStatus = '默认' or banbenStatus is null)   ",
								processT.getProcardTemplate().getId());
				if (wgprocardTList != null && wgprocardTList.size() > 0) {
					for (ProcardTemplate wgprocardT : wgprocardTList) {
						addPawPt1(wgprocardT, processT);
					}
				}
			}
		}

	}

	// 碰焊、氩弧焊或二氧化碳焊（规格以GB13681开头的外购件绑定在这些焊接工序下）
	@Override
	public void processAndWgProcard5() {
		List<ProcardTemplate> wgProcardTList = totalDao
				.query(" from ProcardTemplate where procardStyle = '外购' and specification like 'GB13681%' and (banbenStatus = '默认' or banbenStatus is null) ");
		for (ProcardTemplate wgprocardT : wgProcardTList) {
			List<ProcessTemplate> processTList = totalDao
					.query(
							" from ProcessTemplate where procardTemplate.id = ? and (processName = '碰焊' or processName = '氩弧焊' or processName = '二氧化碳焊') order by processNO ",
							wgprocardT.getProcardTemplate().getId());
			if (processTList != null && processTList.size() > 0) {
				for (ProcessTemplate processT : processTList) {
					if (processT != null) {
						addPawPt1(wgprocardT, processT);
					}
				}
			}

		}

	}

	// 压铆工序绑定的外购件(以下“型号”是指外购件规格前面的几个字母)
	// S-M CLS JM BSO SOS BSOS FHS NFHS FH BS SO SP ZS GN AS
	@Override
	public void processAndWgProcard6() {
		String hql_wg = " from  ProcardTemplate procardStyle = '外购' "
				+ " and (specification like 'S-M%' or specification like 'CLS%' or specification like 'JM%' "
				+ " or specification like 'BSO%' or specification like 'SOS%' or specification like 'BSOS%' or specification like 'FHS%' "
				+ " or specification like 'NFHS%' or specification like 'FH%' or specification like 'BS%' or specification like 'SO%'"
				+ " or specification like 'SP%' or specification like 'ZS%' or specification like 'GN%' or specification like 'AS%')   and (banbenStatus = '默认' or banbenStatus is null) ";
		List<ProcardTemplate> wgprocardTList = totalDao.query(hql_wg);
		if (wgprocardTList != null && wgprocardTList.size() > 0) {
			for (ProcardTemplate wgprocardT : wgprocardTList) {
				List<ProcessTemplate> processTList = totalDao
						.query(
								" from ProcessTemplate where procardTemplate.id = ? and processName = '压铆'  order by processNO ",
								wgprocardT.getProcardTemplate().getId());
				if (processTList != null && processTList.size() > 0) {
					for (ProcessTemplate processT : processTList) {
						if (processT != null) {
							addPawPt1(wgprocardT, processT);
						}
					}
				}
			}
		}

	}

	@Override
	public List ytcompare() {
		// TODO Auto-generated method stub
		List<String> returnList = new ArrayList<String>();
		List<String> markIdList = totalDao
				.query("select distinct markId from ProcardTemplate where  (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')");
		int i = 0;
		for (String markId : markIdList) {
			List<ProcessTemplateFile> fileList = totalDao
					.query(
							"from ProcessTemplateFile where markId=? and (status is null or status='默认') and oldfileName like '%原图%'",
							markId);
			if (fileList != null && fileList.size() > 0) {
				Float hascount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcessTemplateFile where markId=? and processNO is not null  and (status is null or status='默认') and oldfileName like '%原图%'",
								markId);
				if (hascount > 0) {
					continue;
				} else {
					// System.out.println(markId);
					// break;
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where  (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除') and markId=?",
									markId);
					Set<ProcessTemplate> processSet = pt.getProcessTemplate();
					if (processSet != null && processSet.size() > 0) {
						for (ProcessTemplate process : processSet) {
							for (ProcessTemplateFile processTemplateFile : fileList) {
								ProcessTemplateFile newfile = new ProcessTemplateFile();
								BeanUtils.copyProperties(processTemplateFile,
										newfile, new String[] {});
								newfile
										.setProcessName(process
												.getProcessName());
								newfile.setProcessNO(process.getProcessNO());
								totalDao.save(newfile);
								i++;
							}
						}
					}
				}
			} else {
				returnList.add(markId);
			}
			if (i > 200) {
				totalDao.clear();
			}
		}
		return returnList;
	}

	@Override
	public String deleteSamebcfm() {
		// TODO Auto-generated method stub
		List<Integer> fatherIdList = totalDao
				.query("select fatherId from ProcardTemplate where  procardStyle='外购' "
						+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')  and (quanzi1 is null or quanzi2 is null or kgliao is null) ");
		int i = 0;
		if (fatherIdList != null && fatherIdList.size() > 0) {
			for (Integer fId : fatherIdList) {
				List<ProcardTemplate> sonList = totalDao
						.query(
								"from ProcardTemplate  where fatherId=? and procardStyle='外购' "
										+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') "
										+ "and markId in(select markId from ProcardTemplate where fatherId=? and procardStyle='外购' "
										+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') group by markId having count(*)>1)"
										+ " order by kgliao desc", fId, fId);
				if (sonList != null && sonList.size() > 0) {
					String markId = "";
					for (ProcardTemplate pt : sonList) {
						if (markId.equals(pt.getMarkId())) {
							pt.setDataStatus("删除");
							pt.setProcardTemplate(null);
							totalDao.update(pt);
							i++;
						} else {
							markId = pt.getMarkId();
						}
					}
				}
			}
		}

		return i + "";
	}

	// updateProcardArea
	@Override
	public boolean updateProcardArea(Integer id, Double procardArea,
			Integer procardCengNum, Double actualArea) {
		// TODO Auto-generated method stub"from ProcardTemplate where id=?",
		// idprocardStyle

		ProcardTemplate pro = (ProcardTemplate) totalDao.get(
				ProcardTemplate.class, id);
		// ProcardTemplate
		// pro=(ProcardTemplate)totalDao.getObjectByCondition("from ProcardTemplate where id=?",
		// id);
		pro.setProcardArea(procardArea);
		pro.setProcardCengNum(procardCengNum);
		pro.setActualUsedProcardArea(actualArea);
		totalDao.update(pro);

		return totalDao.update(pro);
	}

	@Override
	public ProcessTemplate findProcessTemByProcessId(Integer id) {
		if (id != null) {
			ProcessInfor process = (ProcessInfor) totalDao.get(
					ProcessInfor.class, id);
			Procard procard = process.getProcard();
			return (ProcessTemplate) totalDao
					.getObjectByCondition(
							" from ProcessTemplate where processNO =? and processName =? and procardTemplate.id in "
									+ "(select id from ProcardTemplate where markId = ? and rootMarkId=? and (banbenStatus = '' or banbenStatus is null or banbenStatus = '默认' )"
									+ " and (dataStatus<> '删除' or dataStatus is null)  )",
							process.getProcessNO(), process.getProcessName(),
							procard.getMarkId(), procard.getRootMarkId());

		}
		return null;
	}

	@Override
	public String updateProcardTemplate2(ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		ProcardTemplate oldProcardTem = findProcardTemById(procardTemplate
				.getId());
		if (oldProcardTem == null) {
			return "没有找到目标零件!";
		}
		if (checkHasSonMarkId(procardTemplate.getFatherId(), procardTemplate
				.getId(), procardTemplate.getMarkId())) {
			return "父卡下已有此件号,修改失败!";
		}
		if (oldProcardTem.getBzStatus().equals("已批准")) {
			return "此零件编制状态为已批准,不允许修改";
		}
		if (oldProcardTem.getBzStatus().equals("设变发起中")) {
			return "此零件编制状态为设变发起中,不允许修改";
		}
		ProcardTemplateChangeLog.addchangeLog(totalDao, oldProcardTem,
				procardTemplate, user, nowtime);
		oldProcardTem.setOldProcardStyle(oldProcardTem.getProcardStyle());
		String msg = tbAndupdate(procardTemplate, oldProcardTem);
		return msg;
	}

	@Override
	public boolean updateCarStyle(Integer id, String carStyle) {
		// TODO Auto-generated method stub
		if (id != null) {
			ProcardTemplate pro = (ProcardTemplate) totalDao.get(
					ProcardTemplate.class, id);
			pro.setCarStyle(carStyle);
			return totalDao.update(pro);

		}
		return false;
	}

	@Override
	public ProcardTemplate getProcardByMarkId(String markId) {

		ProcardTemplate template = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')",
						markId);
		return template;
	}

	@Override
	public ProcardTemplate getProcardById(Integer id) {
		ProcardTemplate template = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);

		return template;
	}

	@Override
	public Object[] findAllProcardTemp(ProcardTemplate procardTemplate,
			int pageNo, int pageSize, String pageStatus) {
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null,
				"productStyle");
		if ("sop".equals(pageStatus)) {
			hql += " and productStyle = '试制'";
		} else {
			hql += " and productStyle = '批产'";
		}
		if ("lp".equals(pageStatus)) {
			hql += " and procardStyle <> '总成'";
		}
		hql += " and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除') ";
		int count = totalDao.getCount(hql);
		List list = null;
		if (count <= 0) {
			// 如果在BOM中没有配件记录，则去外购件里查询配件
			count = 0;
			YuanclAndWaigj waigj = new YuanclAndWaigj();
			waigj.setMarkId(procardTemplate.getMarkId());
			waigj.setName(procardTemplate.getProName());
			waigj.setProductStyle(procardTemplate.getProductStyle());// 产品类型
			// waigj.setp//卡片类型 业务件号
			// waigj.setTuhao(procardTemplate.getTuhao());
			waigj.setTuhao(procardTemplate.getYtuhao());// 原材料图号
			waigj.setTrademark(procardTemplate.getTrademark());// 牌号
			waigj.setSpecification(procardTemplate.getSpecification());// 规格

			String hqlWaigj = totalDao.criteriaQueries(waigj, null, null);
			// hqlWaigj+=" and (tuhao ="+procardTemplate.getTuhao()+" or tuhao = "+procardTemplate.getYtuhao()+")";
			count = totalDao.getCount(hqlWaigj);
			List<YuanclAndWaigj> yuanList = totalDao.findAllByPage(hqlWaigj,
					pageNo, pageSize);
			ProcardTemplate template = null;
			list = new ArrayList();
			for (YuanclAndWaigj yuanclAndWaigj : yuanList) {
				template = new ProcardTemplate();
				template.setMarkId(yuanclAndWaigj.getMarkId());
				template.setProName(yuanclAndWaigj.getName());
				template.setProductStyle(yuanclAndWaigj.getProductStyle());
				list.add(template);
			}

		} else {
			list = totalDao.findAllByPage(hql, pageNo, pageSize);

		}
		Object[] o = { list, count };
		return o;
	}

	@Override
	public YuanclAndWaigj getYuanclAndWaigjByMarkId(String markId) {

		YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj) totalDao
				.getObjectByCondition("from YuanclAndWaigj where markId = ?",
						markId);
		return yuanclAndWaigj;
	}
	public String xiufusztusztosz(Integer rootId) {
		 List<ProcardTemplate> ptList = totalDao
		 .query("from ProcardTemplate where rootId=? and productstyle='试制' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and "
		 +
		 "id not in(select glId from ProcessTemplateFile where  processNO is null and glId is not null)",rootId);
		 String time = Util.getDateTime();
		if (ptList != null && ptList.size() > 0) {
			List<Banbenxuhao> banbenxuhaoList = totalDao
					.query("from Banbenxuhao");
			Map<String, Integer> banBenxuhaoMap = new HashMap<String, Integer>();
			for (Banbenxuhao banebnxuhao : banbenxuhaoList) {
				banBenxuhaoMap.put(banebnxuhao.getBanbenNumber(), banebnxuhao
						.getBanbenxuhao());
			}
			int i = 0;
			for (ProcardTemplate pt : ptList) {
				String banbenSql = null;
				if (pt.getBanBenNumber() == null
						|| pt.getBanBenNumber().length() == 0) {
					banbenSql = " and (banBenNumber is null or banBenNumber='')";
				} else {
					banbenSql = " and banBenNumber='"
						+ pt.getBanBenNumber() + "'";
				}
				System.out
						.println("修复图纸:" + pt.getMarkId() + "--" + pt.getId());
				ProcardTemplate szpt = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=? and productStyle='试制' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
										+ " and id in(select glId from ProcessTemplateFile where  processNO is null and glId is not null)" +banbenSql,
								pt.getMarkId());
				
				if (szpt != null
						&& Util.isEquals(pt.getBanBenNumber(), szpt
								.getBanBenNumber())) {
					String banciSql = null;
					if (szpt.getBanci() == null
							|| szpt.getBanci() == 0) {
						banciSql = " and (banci is null or banci=0)";
					} else {
						banciSql = " and banci=" + szpt.getBanci() ;
					}
					 List<ProcessTemplateFile> ptfiles2 = totalDao
					 .query(
					 "from ProcessTemplateFile where  processNO is null and glId=? and markId=? "
					 + banciSql,pt.getId(), pt.getMarkId());
					 if (ptfiles2 != null && ptfiles2.size() > 0) {
					 for (ProcessTemplateFile oldFile : ptfiles2) {
					 ProcessTemplateFile newFile = new ProcessTemplateFile();
					 BeanUtils.copyProperties(oldFile, newFile,
					 new String[] { "id" });
					 newFile.setProductStyle("试制");
					 newFile.setAddTime(time);
					 newFile.setGlId(pt.getId());
					 newFile.setBanci(pt.getBanci());
					 newFile.setBanBenNumber(pt.getBanBenNumber());
					 totalDao.save(newFile);
					 }
					 }
					  List<ProcessTemplate> processList1 = totalDao
					  .query( "from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')   ",
					  pt.getId());
					  if (processList1 != null && processList1.size() > 0) {
					  for (ProcessTemplate process1 : processList1) {
					  for(ProcessTemplateFile oldFile : ptfiles2){
					  if(oldFile.getType().equals("3D模型")){
					  continue;
					  }
					  ProcessTemplateFile newFile = new ProcessTemplateFile();
					  BeanUtils.copyProperties(oldFile, newFile,
					  new String[] { "id" });
					  newFile.setProductStyle("试制");
					  newFile.setAddTime(time);
					  newFile.setGlId(process1.getId());
					  newFile.setProcessName(process1.getProcessName());
					  newFile.setProcessNO(process1.getProcessNO());
					  newFile.setBanci(pt.getBanci());
					  newFile.setBanBenNumber(pt.getBanBenNumber());
					  totalDao.save(newFile);
					  }
					  }
					  }
					
					
				}
			}
		}
		return  "";
	}
	// @Override
	// public String xiufusztu(Integer rootId) {
	// // TODO Auto-generated method stub
	// List<ProcardTemplate> ptList = totalDao
	// .query("from ProcardTemplate where rootId=? and productstyle='试制' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and "
	// +
	// "id not in(select glId from ProcessTemplateFile where  processNO is null and glId is not null)",rootId);
	// String time = Util.getDateTime();
	// if (ptList != null && ptList.size() > 0) {
	// List<Banbenxuhao> banbenxuhaoList = totalDao
	// .query("from Banbenxuhao");
	// Map<String, Integer> banBenxuhaoMap = new HashMap<String, Integer>();
	// for (Banbenxuhao banebnxuhao : banbenxuhaoList) {
	// banBenxuhaoMap.put(banebnxuhao.getBanbenNumber(), banebnxuhao
	// .getBanbenxuhao());
	// }
	// int i=0;
	// for (ProcardTemplate pt : ptList) {
	// System.out.println("修复图纸:"+pt.getMarkId()+"--"+pt.getId());
	// ProcardTemplate pcpt = (ProcardTemplate) totalDao
	// .getObjectByCondition(
	// "from ProcardTemplate where markId=? and productStyle='试制' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
	// +
	// " and id in(select glId from ProcessTemplateFile where type='成型图' and processNO is null and glId is not null)",
	// pt.getMarkId());
	// String banciSql = null;
	//				
	// if (pcpt != null
	// && Util.isEquals(pt.getBanBenNumber(), pcpt
	// .getBanBenNumber())) {
	// if (pcpt.getBanBenNumber() == null || pcpt.getBanBenNumber().length()==0)
	// {
	// banciSql = " and (banBenNumber is null or banBenNumber='')";
	// } else {
	// banciSql = " and banBenNumber='" + pcpt.getBanBenNumber()+"'";
	// }
	// List<ProcessTemplateFile> ptfiles2 = totalDao
	// .query(
	// "from ProcessTemplateFile where type='成型图' and processNO is null and glId is null and markId=? "
	// + banciSql, pt.getMarkId());
	// if (ptfiles2 != null && ptfiles2.size() > 0) {
	// for (ProcessTemplateFile oldFile : ptfiles2) {
	// ProcessTemplateFile newFile = new ProcessTemplateFile();
	// BeanUtils.copyProperties(oldFile, newFile,
	// new String[] { "id" });
	// newFile.setProductStyle("试制");
	// newFile.setAddTime(time);
	// newFile.setGlId(pt.getId());
	// newFile.setBanci(pt.getBanci());
	// newFile.setBanBenNumber(pt.getBanBenNumber());
	// totalDao.save(newFile);
	// }
	// }
	// // List<ProcessTemplate> processList1 = totalDao
	// // .query(
	// //
	// "from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')   ",
	// // pt.getId());
	// // if (processList1 != null && processList1.size() > 0) {
	// // for (ProcessTemplate process1 : processList1) {
	// // for(ProcessTemplateFile oldFile : ptfiles2){
	// // if(oldFile.getType().equals("3D模型")){
	// // continue;
	// // }
	// // ProcessTemplateFile newFile = new ProcessTemplateFile();
	// // BeanUtils.copyProperties(oldFile, newFile,
	// // new String[] { "id" });
	// // newFile.setProductStyle("试制");
	// // newFile.setAddTime(time);
	// // newFile.setGlId(process1.getId());
	// // newFile.setProcessName(process1.getProcessName());
	// // newFile.setProcessNO(process1.getProcessNO());
	// // newFile.setBanci(pt.getBanci());
	// // newFile.setBanBenNumber(pt.getBanBenNumber());
	// // totalDao.save(newFile);
	// // }
	// // }
	// // }
	// }
	// // ProcardTemplate pcpt = (ProcardTemplate) totalDao
	// // .getObjectByCondition(
	// //
	// "from ProcardTemplate where markId=? and productStyle='批产' and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')",
	// // pt.getMarkId());
	// // String banciSql = null;
	// //
	// // if (pcpt != null
	// // && Banbenxuhao.comparebanben(pt.getBanBenNumber(), pcpt
	// // .getBanBenNumber(), banBenxuhaoMap) <= 0) {
	// // if (pcpt.getBanci() == null || pcpt.getBanci() == 0) {
	// // banciSql = " and (banci is null or banci=0)";
	// // } else {
	// // banciSql = " and banci=" + pcpt.getBanci();
	// // }
	// // List<ProcessTemplateFile> ptfiles2 = totalDao
	// // .query(
	// //
	// "from ProcessTemplateFile where processNO is null and glId is null and markId=? "
	// // + banciSql, pt.getMarkId());
	// // if (ptfiles2 != null && ptfiles2.size() > 0) {
	// // for (ProcessTemplateFile oldFile : ptfiles2) {
	// // ProcessTemplateFile newFile = new ProcessTemplateFile();
	// // BeanUtils.copyProperties(oldFile, newFile,
	// // new String[] { "id" });
	// // newFile.setProductStyle("试制");
	// // newFile.setAddTime(time);
	// // newFile.setGlId(pt.getId());
	// // newFile.setBanci(pt.getBanci());
	// // newFile.setBanBenNumber(pt.getBanBenNumber());
	// // totalDao.save(newFile);
	// // }
	// // }
	// // List<ProcessTemplate> processList1 = totalDao
	// // .query(
	// //
	// "from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')   ",
	// // pt.getId());
	// // List<ProcessTemplate> processList2 = totalDao
	// // .query(
	// //
	// "from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')   ",
	// // pcpt.getId());
	// //
	// // if (processList1 != null && processList1.size() > 0
	// // && processList2 != null && processList2.size() > 0) {
	// // for (ProcessTemplate process1 : processList1) {
	// // for (ProcessTemplate process2 : processList2) {
	// // if (process1.getProcessNO().equals(
	// // process2.getProcessNO())) {
	// // Float hadcount = (Float) totalDao
	// // .getObjectByCondition(
	// //
	// "select count(*) from ProcessTemplateFile where processNO is not null and glId=?",
	// // process1.getId());
	// // if (hadcount == null || hadcount == 0) {
	// // List<ProcessTemplateFile> ptfiles3 = totalDao
	// // .query(
	// //
	// "from ProcessTemplateFile where processNO=? and glId is null and markId=? "
	// // + banciSql,
	// // process2.getProcessNO(),
	// // pcpt.getMarkId());
	// // if (ptfiles3 != null
	// // && ptfiles3.size() > 0) {
	// // for (ProcessTemplateFile oldFile : ptfiles3) {
	// // ProcessTemplateFile newFile = new ProcessTemplateFile();
	// // BeanUtils.copyProperties(
	// // oldFile, newFile,
	// // new String[] { "id" });
	// // newFile.setProductStyle("试制");
	// // newFile.setAddTime(time);
	// // newFile.setGlId(process1
	// // .getId());
	// // newFile.setBanci(pt.getBanci());
	// // newFile.setBanBenNumber(pt
	// // .getBanBenNumber());
	// // totalDao.save(newFile);
	// // }
	// // }
	// // }
	// // }
	// // }
	// // }
	// // }
	// // }
	//
	// i++;
	// if(i==200){
	// totalDao.clear();
	// i=0;
	// }
	// }
	// }
	// FilerootIdLog fl = new FilerootIdLog();
	// fl.setRootId(rootId);
	// totalDao.save(fl);
	// // for(ProcardTemplate pt:ptList){
	// // List<ProcessTemplateFile> ptfiles1 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=?",
	// // pt.getId());
	// // String banciSql = null;
	// // if(pt.getBanci()==null||pt.getBanci()==0){
	// // banciSql = " and (banci is null or banci=0)";
	// // }else{
	// // banciSql = " and banci="+pt.getBanci();
	// // }
	// // String banbenSql = null;
	// // String banbenSql2 = null;
	// // if(pt.getBanBenNumber()==null||pt.getBanBenNumber().length()==0){
	// // banbenSql = " and (banBenNumber is null or banBenNumber='')";
	// // banbenSql2 =
	// //
	// " and (procardTemplate.banBenNumber is null or procardTemplate.banBenNumber='')";
	// // }else{
	// // banbenSql = " and banBenNumber='"+pt.getBanBenNumber()+"'";
	// // banbenSql2 =
	// // " and procardTemplate.banBenNumber='"+pt.getBanBenNumber()+"'";
	// // }
	// // if(ptfiles1==null||ptfiles1.size()==0){
	// // List<ProcessTemplateFile> ptfiles2 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId is null and markId=? "+banciSql,
	// // pt.getMarkId());
	// // if(ptfiles2==null||ptfiles2.size()==0){
	// // ptfiles2 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId is null and markId=? and (status is null or status!='历史')",
	// // pt.getMarkId());
	// // }
	// // if(ptfiles2==null||ptfiles2.size()==0){
	// // Integer glId = (Integer)
	// //
	// totalDao.getObjectByCondition("select glId from ProcessTemplateFile where processNO is null and glId is not null and markId=? "
	// // +
	// //
	// "and glId in(select id from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') "+banbenSql+" )",
	// // pt.getMarkId());
	// // if(glId==null){
	// // glId = (Integer)
	// //
	// totalDao.getObjectByCondition("select glId from ProcessTemplateFile where processNO is null and glId is not null and markId=? "
	// // +
	// //
	// "and glId in(select id from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') )",
	// // pt.getMarkId());
	// // if(glId!=null){
	// // ptfiles2 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? and (banbenStatus is null or banbenStatus!='历史')",
	// // glId);
	// // }
	// // }else{
	// // ptfiles2 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? "+banciSql,
	// // glId);
	// // if(ptfiles2==null){
	// // ptfiles2 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? and (banbenStatus is null or banbenStatus!='历史')",
	// // glId);
	// // }
	// // }
	// // }
	// // if(ptfiles2!=null){
	// // for(ProcessTemplateFile oldFile :ptfiles2){
	// // ProcessTemplateFile newFile = new ProcessTemplateFile();
	// // BeanUtils.copyProperties(oldFile, newFile, new String []{"id"});
	// // newFile.setProductStyle("试制");
	// // newFile.setAddTime(time);
	// // newFile.setGlId(pt.getId());
	// // newFile.setBanci(pt.getBanci());
	// // newFile.setBanBenNumber(pt.getBanBenNumber());
	// // totalDao.save(newFile);
	// // }
	// // }
	// // }
	// // //工序图纸
	// // List<ProcessTemplate> processList =
	// //
	// totalDao.query("from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') and (guifanstatus is null or guifanstatus!='否') and id not in (select glId from ProcessTemplateFile where processNO is not null and glId is not null)  ",
	// // pt.getId());
	// // if(processList!=null&&processList.size()>0){
	// // for(ProcessTemplate process:processList){
	// // List<ProcessTemplateFile> ptfiles3 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO=? and glId is null and markId=? "+banciSql,
	// // process.getProcessNO(),pt.getMarkId());
	// // if(ptfiles3==null||ptfiles3.size()==0){
	// // ptfiles3 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO=?  and glId is null and markId=? and (status is null or status!='历史')",
	// // process.getProcessNO(),pt.getMarkId());
	// // }
	// // if(ptfiles3==null||ptfiles3.size()==0){
	// // Integer glId = (Integer)
	// //
	// totalDao.getObjectByCondition("select glId from ProcessTemplateFile where processNO=? and processName=? and glId is not null and markId=? "
	// // +
	// //
	// "and glId in(select id from ProcessTemplate where processNO=? and processName=? and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus!='历史') and (procardTemplate.dataStatus is null or procardTemplate.dataStatus!='删除') and (dataStatus is null or dataStatus!='删除') and procardTemplate.markId=?  "+banbenSql2+" )"
	// // ,process.getProcessNO(),
	// // process.getProcessName(),pt.getMarkId(),process.getProcessNO(),
	// // process.getProcessName(),pt.getMarkId());
	// // if(glId==null){
	// // glId = (Integer)
	// //
	// totalDao.getObjectByCondition("select glId from ProcessTemplateFile where processNO=? and processName=? and glId is not null and markId=? "
	// // +
	// //
	// "and glId in(select id from ProcessTemplate where processNO=? and processName=? and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus!='历史') and (procardTemplate.dataStatus is null or procardTemplate.dataStatus!='删除') and (dataStatus is null or dataStatus!='删除') and procardTemplate.markId=?  )"
	// // ,process.getProcessNO(),
	// // process.getProcessName(),pt.getMarkId(),process.getProcessNO(),
	// // process.getProcessName(),pt.getMarkId());
	// // if(glId!=null){
	// // ptfiles3 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? ",
	// // glId);
	// // }
	// // }else{
	// // ptfiles3 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? "+banciSql,
	// // glId);
	// // if(ptfiles3==null||ptfiles3.size()==0){
	// // ptfiles3 =
	// //
	// totalDao.query("from ProcessTemplateFile where processNO is null and glId=? and (banbenStatus is null or banbenStatus!='历史')",
	// // glId);
	// // }
	// // }
	// // }
	// // for(ProcessTemplateFile oldFile :ptfiles3){
	// // ProcessTemplateFile newFile = new ProcessTemplateFile();
	// // BeanUtils.copyProperties(oldFile, newFile, new String []{"id"});
	// // newFile.setProductStyle("试制");
	// // newFile.setAddTime(time);
	// // newFile.setGlId(pt.getId());
	// // newFile.setBanci(pt.getBanci());
	// // newFile.setBanBenNumber(pt.getBanBenNumber());
	// // totalDao.save(newFile);
	// // }
	// // }
	// // }
	// //
	// //
	// // }
	//
	// return "true";
	// }
	// 零件下发工序图纸
	@Override
	public String xiufusztu(Integer rootId) {
		List<ProcardTemplate> ptList = totalDao
				.query(
						"from ProcardTemplate pt where rootId=? and (pt.dataStatus is null or pt.dataStatus='删除') and (pt.productStyle='试制')"
								+ " and (pt.procardStyle !='外购' or (pt.procardStyle='外购' and pt.needProcess='yes')) and pt.id in"
								+ "(select pf.glId from ProcessTemplateFile pf where pf.glId is not null and pf.processNO is null and pf.banci=pt.banci and pf.type in('工艺规范','成型图','3D文件','NC数冲','镭射') )",
						rootId);

		int i = 0;
		String time = Util.getDateTime();
		int j = 0;
		int all = ptList.size();
		for (ProcardTemplate pt : ptList) {
			j++;
			System.out.println("共" + all + "条,第" + j + "条,修复图纸:"
					+ pt.getMarkId() + "--" + pt.getId());
			List<ProcessTemplate> processList = totalDao
					.query(
							" from ProcessTemplate pt where (pt.dataStatus is null or pt.dataStatus='删除') and procardTemplate.id=? and pt.id not in"
									+ "(select pf.glId from ProcessTemplateFile pf where pf.glId is not null and pf.processNO is not null and pf.markId=? and pf.banci=?)",
							pt.getId(), pt.getMarkId(), pt.getBanci());
			if (processList != null && processList.size() > 0) {
				List<ProcessTemplateFile> fileList = totalDao
						.query(
								"from ProcessTemplateFile where processNO is null and glId=? and banci=? and type in('工艺规范','成型图','3D文件','NC数冲','镭射')",
								pt.getId(), pt.getBanci());
				if (fileList != null && fileList.size() > 0) {
					for (ProcessTemplateFile file : fileList) {
						for (ProcessTemplate process : processList) {
							ProcessTemplateFile newFile = new ProcessTemplateFile();
							BeanUtils.copyProperties(file, newFile,
									new String[] { "id" });
							newFile.setProductStyle("试制");
							// newFile.setAddTime(time);
							newFile.setGlId(process.getId());
							newFile.setProcessName(process.getProcessName());
							newFile.setProcessNO(process.getProcessNO());
							newFile.setBanci(pt.getBanci());
							newFile.setBanBenNumber(pt.getBanBenNumber());
							totalDao.save(newFile);
						}
					}
				}
			}

			i++;
			if (i == 100) {
				i = 0;
				totalDao.clear();
			}
		}
		FilerootIdLog fl = new FilerootIdLog();
		fl.setRootId(rootId);
		totalDao.save(fl);
		return "true";
	}

	@Override
	public String xiufusztu2(Integer rootId) {
		List<ProcardTemplate> ptList = totalDao
				.query(
						"from ProcardTemplate pt where rootId=? and (pt.dataStatus is null or pt.dataStatus='删除') and (pt.productStyle='批产')"
								+ " and (pt.procardStyle !='外购' or (pt.procardStyle='外购' and pt.needProcess='yes')) and pt.markId in"
								+ "(select pf.markId from ProcessTemplateFile pf where pf.glId is  null and pf.processNO is null and pf.banci=pt.banci and pf.type in('工艺规范','成型图','3D文件','NC数冲','镭射') )",
						rootId);

		int i = 0;
		String time = Util.getDateTime();
		int j = 0;
		int all = ptList.size();
		for (ProcardTemplate pt : ptList) {
			j++;
			System.out.println("共" + all + "条,第" + j + "条,修复图纸:"
					+ pt.getMarkId() + "--" + pt.getId());
			List<ProcessTemplate> processList = totalDao
					.query(
							" from ProcessTemplate pt where (pt.dataStatus is null or pt.dataStatus='删除') and procardTemplate.id=? and pt.processNO not in"
									+ "(select pf.processNO from ProcessTemplateFile pf where pf.glId is  null and pf.processNO is not null and pf.markId=? and pf.banci=?)",
							pt.getId(), pt.getMarkId(), pt.getBanci());
			if (processList != null && processList.size() > 0) {
				List<ProcessTemplateFile> fileList = totalDao
						.query(
								"from ProcessTemplateFile where processNO is null and glId is null and markId=? and banci=? and type in('工艺规范','成型图','3D文件','NC数冲','镭射')",
								pt.getMarkId(), pt.getBanci());
				if (fileList != null && fileList.size() > 0) {
					for (ProcessTemplateFile file : fileList) {
						for (ProcessTemplate process : processList) {
							ProcessTemplateFile newFile = new ProcessTemplateFile();
							BeanUtils.copyProperties(file, newFile,
									new String[] { "id" });
							newFile.setProductStyle("批产");
							// newFile.setAddTime(time);
							newFile.setProcessName(process.getProcessName());
							newFile.setProcessNO(process.getProcessNO());
							newFile.setBanci(pt.getBanci());
							newFile.setBanBenNumber(pt.getBanBenNumber());
							totalDao.save(newFile);
						}
					}
				}
			}

			i++;
			if (i == 100) {
				i = 0;
				totalDao.clear();
			}
		}
		FilerootIdLog fl = new FilerootIdLog();
		fl.setRootId(rootId);
		totalDao.save(fl);
		return "true";
	}

	@Override
	public String gxtbsc(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		ProcardTemplate zc = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (zc != null) {
			String ywMakrId = zc.getYwMarkId() == null ? "" : zc.getYwMarkId();
			Float tqCount = (Float) totalDao.getObjectByCondition(
					"select count(*) from ProcardTemplatePrivilege "
							+ " where markId=? or markId=?", zc.getMarkId(),
					ywMakrId);
			if (tqCount == null || tqCount == 0) {
				return "对不起此BOM无次特权!";
			}
			List<ProcardTemplate> ptlist = totalDao
					.query(
							"from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除') ",
							zc.getId());
			List<Procard> procardList = totalDao
					.query(
							"from Procard where rootId in(select id from Procard where markId=? and status not in('取消','删除','完成','待入库','入库'))"
									+ " and (sbStatus is null or sbStatus!='删除') and (procardStyle!='外购' or (procardStyle='外购' and needProcess='yes')) "
									+ "and id not in(select procard.id from ProcessInfor where (dataStatus is null or dataStatus!='删除') and procard.id is not null )  order by markId",
							zc.getMarkId());
			if (procardList != null && procardList.size() > 0) {
				String noMarkId = null;
				Integer index = -1;
				for (Procard procard : procardList) {
					ProcardTemplate bz = null;
					if (noMarkId == null || !noMarkId.equals(procard)) {
						noMarkId = procard.getMarkId();
						int i = 0;
						for (ProcardTemplate pt : ptlist) {
							if (pt.getMarkId().equals(noMarkId)) {
								bz = pt;
								index = i;
								break;
							}
							i++;
						}
					} else {
						bz = ptlist.get(index);
					}
					if (bz != null) {
						Set<ProcessInfor> processSet = new HashSet<ProcessInfor>();
						Set<ProcessTemplate> setProCess = bz
								.getProcessTemplate();
						for (ProcessTemplate processTem : setProCess) {
							if (processTem.getDataStatus() != null
									&& processTem.getDataStatus().equals("删除")) {
								continue;
							}
							ProcessInfor process = new ProcessInfor();
							BeanUtils.copyProperties(processTem, process,
									new String[] { "id", "procardTemplate" });
							// process.setProcessNO(processTem.getProcessNO());//
							// 工序号
							// process.setProcessName(processTem.getProcessName());//
							// 工序名称
							// process.setProcessStatus(processTem.getProcessStatus());//
							// 状态(并行/单独)
							// process.setParallelId(processTem.getParallelId());//
							// 并行开始id

							// 人工节拍和设备节拍处理
							if (process.getProductStyle() != null
									&& process.getProductStyle().equals("外委")) {// 外委工序节拍设为1
								process.setOpcaozuojiepai(1f);
								process.setOpshebeijiepai(1f);
								process.setGzzhunbeijiepai(1f);
								process.setGzzhunbeicishu(1f);
								process.setAllJiepai(1f);
							} else {
								if (process.getOpcaozuojiepai() == null) {
									process.setOpcaozuojiepai(0F);
								}
								if (process.getOpshebeijiepai() == null) {
									process.setOpshebeijiepai(0F);
								}
							}
							process.setTotalCount(procard.getFilnalCount());// 可领取量
							process.setStatus("初始");
							process.setProcessTemplateId(processTem.getId());
							process.setProcard(procard);

							if ("试制".equals(procard.getProductStyle())) {
								process.setZjStatus("no");
							}
							// -----------------辅料------------------
							if (processTem.getIsNeedFuliao() != null
									&& processTem.getIsNeedFuliao().equals(
											"yes")) {
								process.setIsNeedFuliao("yes");
								Set<ProcessFuLiaoTemplate> fltmpSet = processTem
										.getProcessFuLiaoTemplate();
								if (fltmpSet.size() > 0) {
									Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
									for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
										ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
										BeanUtils.copyProperties(fltmp,
												pinfoFl, new String[] { "id" });
										if (pinfoFl.getQuanzhi1() == null) {
											pinfoFl.setQuanzhi1(1f);
										}
										if (pinfoFl.getQuanzhi2() == null) {
											pinfoFl.setQuanzhi2(1f);
										}
										pinfoFl.setTotalCount(procard
												.getFilnalCount()
												* pinfoFl.getQuanzhi2()
												/ pinfoFl.getQuanzhi1());
										pinfoFl.setProcessInfor(process);
										pinfoFl.setOutCount(0f);
										pinfoFl.setAddUser(Util.getLoginUser()
												.getName());
										pinfoFl.setAddTime(Util.getDateTime());
										pinfoFlSet.add(pinfoFl);
									}
									process.setProcessinforFuLiao(pinfoFlSet);
								}
							}
							try {
								processSet.add(process);
								totalDao.save(process);
							} catch (Exception e) {
								// TODO: handle exception
								System.out.println("hh");
							}
						}
						procard.setProcessInforSet(processSet);
						totalDao.update(procard);
					}

				}
			}

		}
		return "true";
	}

	public ProcardTemplateReplace findptlateReplce(Integer id) {
		return (ProcardTemplateReplace) totalDao.get(
				ProcardTemplateReplace.class, id);

	}

	@Override
	public List<Object[]> getUnpaotuRootId() {
		// TODO Auto-generated method stub

		// return
		// totalDao.query("select rootId from ProcardTemplate where procardStyle ='总成' and productStyle ='批产'"
		// +
		// " and (banbenStatus is null or banbenStatus!='历史') and  (dataStatus is null or dataStatus!='删除')"
		// +
		// " and id not in (select rootId from FilerootIdLog)");
		List<Object[]> ptList = totalDao
				.query("select id,productStyle from ProcardTemplate where  procardStyle ='总成' and id= rootId"
						+ " and (banbenStatus is null or banbenStatus!='历史') and  (dataStatus is null or dataStatus!='删除')"
						+ " and id not in (select rootId from FilerootIdLog)");
		return ptList;
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
	public List<ProcardTemplate> findProcardTemByConditionId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcardTemplate where adduser='唐晓斌' and addtime >='2018-07-20 10:12:12' and addtime<='2018-07-20 10:12:15'"
								+ " and fatherId=?", id);
	}

	@Override
	public String xiufuxclingjian1(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate yfather = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (yfather != null) {
			// 获取要同步的零件
			List<ProcardTemplate> sameptList = null;
			String addSql = null;
			if (yfather.getBanBenNumber() == null
					|| yfather.getBanBenNumber().length() == 0) {
				sameptList = totalDao
						.query(
								"from ProcardTemplate where  productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and (banBenNumber is null or banBenNumber='')  and  markId=? ",
								yfather.getProductStyle(), yfather.getMarkId());
			} else {
				sameptList = totalDao
						.query(
								"from ProcardTemplate where  productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and banBenNumber=? and  markId=? ",
								yfather.getProductStyle(), yfather
										.getBanBenNumber(), yfather.getMarkId());
			}
			if (sameptList != null) {
				List<ProcardTemplate> ySonList = totalDao
						.query(
								"from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除')  and (banbenStatus is null or banbenStatus !='历史')",
								yfather.getId());
				for (ProcardTemplate sfather : sameptList) {
					List<ProcardTemplate> sSonList = totalDao
							.query(
									"from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除')  and (banbenStatus is null or banbenStatus !='历史')",
									sfather.getId());
					List<ProcardTemplate> addSonList = new ArrayList<ProcardTemplate>();
					for (ProcardTemplate yson : ySonList) {
						boolean had = false;
						if (sSonList != null && sSonList.size() > 0) {
							for (ProcardTemplate sson : sSonList) {
								if (sson.getMarkId().equalsIgnoreCase(
										yson.getMarkId())) {
									had = true;
									break;
								}
							}
						}
						if (!had) {
							addSonList.add(yson);
						}
					}
					if (addSonList.size() > 0) {
						for (ProcardTemplate addson : addSonList) {
							saveCopyProcardImple(sfather, addson, sfather
									.getProductStyle());
						}
					}

				}

			}
		}

		return null;
	}

	@Override
	public String xiufuxclingjian2(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	private String isSameProcardStyle(ProcardTemplate procardT) {
		// 判断同件号，同业务件号，同产品类型，只能有同一卡片类型。
		int count = totalDao
				.getCount(
						" from ProcardTemplate where markId =? and ywMarkId =?  "
								+ " and productStyle =? and procardStyle <> ? and "
								+ " (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '删除')",
						procardT.getMarkId(), procardT.getYwMarkId(), procardT
								.getProductStyle(), procardT.getProcardStyle());
		if (count > 0) {
			return "同业务件号下，件号:[" + procardT.getMarkId() + "]，有不属于:["
					+ procardT.getProcardStyle() + "]的卡片类型，不符合工艺规范，请检查!~";
		}
		return "";
	}
	
	// 递归 求单台用量
	public Float gainDanTai(ProcardTemplate sonProcardT, Float num) {
		Float corrCount = sonProcardT.getCorrCount();
		num = (corrCount == null ? 1 : corrCount) * (num == null ? 1 : num);
		if (sonProcardT.getFatherId() != null
				|| !"总成".equals(sonProcardT.getProcardStyle())) {
			ProcardTemplate fatherProcard = (ProcardTemplate) totalDao.get(ProcardTemplate.class,
					sonProcardT.getFatherId());
			num = gainDanTai(fatherProcard, num);
		}
		return num;
	}

	@Override
	public void testSendMsg() {
		// TODO Auto-generated method stub
		AlertMessagesServerImpl.addAlertMessages(
				"系统维护异常组",
				"（测试）下层的minnumber大于上层的finallcount。id:"
						+ 123, "提交工序", "2");
	}



}