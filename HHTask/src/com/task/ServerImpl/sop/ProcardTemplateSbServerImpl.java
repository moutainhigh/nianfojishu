package com.task.ServerImpl.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Alignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.sop.ManualOrderPlanServer;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.ProcardTemplateSbServer;
import com.task.Server.sop.ProcardTemplateServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.entity.Category;
import com.task.entity.ChartNOGzType;
import com.task.entity.ChartNOSC;
import com.task.entity.Users;
import com.task.entity.UsersGroup;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.entity.sop.Banbenxuhao;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardAboutBanBenApply;
import com.task.entity.sop.ProcardBanBenJudge;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateAboutsb;
import com.task.entity.sop.ProcardTemplateAboutsbDetail;
import com.task.entity.sop.ProcardTemplateAboutsbcltype;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardTemplateBanBenApply;
import com.task.entity.sop.ProcardTemplateBanBenJudges;
import com.task.entity.sop.ProcardTemplatesb;
import com.task.entity.sop.ProcardTemplatesbChangeLog;
import com.task.entity.sop.ProcardTemplatesbDifference;
import com.task.entity.sop.ProcardTemplatesbDifferenceDetail;
import com.task.entity.sop.ProcessAboutBanBenApply;
import com.task.entity.sop.ProcessAndWgProcardTem;
import com.task.entity.sop.ProcessFuLiaoTemplate;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ProcessTemplateFilesb;
import com.task.entity.sop.ProcessTemplatesb;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouPlanLock;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.ycl.PanelSize;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.zgkh.AssessPersonnel;
import com.task.util.MKUtil;
import com.task.util.Upload;
import com.task.util.Util;

public class ProcardTemplateSbServerImpl implements ProcardTemplateSbServer{
	private ProcardServer procardServer;
	private ProcardTemplateServer procardTemplateServer;
	private final static String gxtzPath = "/upload/file/processTz";
	private float sumprice = 0f;
	private TotalDao totalDao;
	private Integer bomXuhao = 1;
	private Integer sbData = 0;
	private Integer thisminId = 0;
	private String jsbcmsg = "";
	private List<YuanclAndWaigj> aboutwgjList;
	private List<ProcardTemplate> aboutptList;
	private List<ProcardTemplatesb> aboutptsbList;
	private Map<String, Integer> banBenxuhaoMap;
	private ManualOrderPlanServer  manualPlanServer;
	private List<Integer> hasbd =new ArrayList<Integer>();
	private List<String> hadclMarkIdList = new ArrayList<String>();
	private List<String> sbMarkIdList = new ArrayList<String>();
	int sdnum = 0;
	private ResultSet gzType;
	public ProcardServer getProcardServer() {
		return procardServer;
	}
	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}
	public ProcardTemplateServer getProcardTemplateServer() {
		return procardTemplateServer;
	}
	public void setProcardTemplateServer(ProcardTemplateServer procardTemplateServer) {
		this.procardTemplateServer = procardTemplateServer;
	}
	public float getSumprice() {
		return sumprice;
	}
	public void setSumprice(float sumprice) {
		this.sumprice = sumprice;
	}
	public TotalDao getTotalDao() {
		return totalDao;
	}
	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}
	public Integer getBomXuhao() {
		return bomXuhao;
	}
	public void setBomXuhao(Integer bomXuhao) {
		this.bomXuhao = bomXuhao;
	}
	public Integer getSbData() {
		return sbData;
	}
	public void setSbData(Integer sbData) {
		this.sbData = sbData;
	}
	public Integer getThisminId() {
		return thisminId;
	}
	public void setThisminId(Integer thisminId) {
		this.thisminId = thisminId;
	}
	public String getJsbcmsg() {
		return jsbcmsg;
	}
	public void setJsbcmsg(String jsbcmsg) {
		this.jsbcmsg = jsbcmsg;
	}
	public List<YuanclAndWaigj> getAboutwgjList() {
		return aboutwgjList;
	}
	public void setAboutwgjList(List<YuanclAndWaigj> aboutwgjList) {
		this.aboutwgjList = aboutwgjList;
	}
	public List<ProcardTemplate> getAboutptList() {
		return aboutptList;
	}
	public void setAboutptList(List<ProcardTemplate> aboutptList) {
		this.aboutptList = aboutptList;
	}
	
	public List<ProcardTemplatesb> getAboutptsbList() {
		return aboutptsbList;
	}
	public void setAboutptsbList(List<ProcardTemplatesb> aboutptsbList) {
		this.aboutptsbList = aboutptsbList;
	}
	public List<String> getHadclMarkIdList() {
		return hadclMarkIdList;
	}
	public void setHadclMarkIdList(List<String> hadclMarkIdList) {
		this.hadclMarkIdList = hadclMarkIdList;
	}
	public Map<String, Integer> getBanBenxuhaoMap() {
		return banBenxuhaoMap;
	}
	public void setBanBenxuhaoMap(Map<String, Integer> banBenxuhaoMap) {
		this.banBenxuhaoMap = banBenxuhaoMap;
	}
	public ManualOrderPlanServer getManualPlanServer() {
		return manualPlanServer;
	}
	public void setManualPlanServer(ManualOrderPlanServer manualPlanServer) {
		this.manualPlanServer = manualPlanServer;
	}
	public List<Integer> getHasbd() {
		return hasbd;
	}
	public void setHasbd(List<Integer> hasbd) {
		this.hasbd = hasbd;
	}
	public int getSdnum() {
		return sdnum;
	}
	public void setSdnum(int sdnum) {
		this.sdnum = sdnum;
	}
	public ResultSet getGzType() {
		return gzType;
	}
	public void setGzType(ResultSet gzType) {
		this.gzType = gzType;
	}
	public static String getGxtzpath() {
		return gxtzPath;
	}
	@Override
	public Object[] upptlvnew(List<ProcardTemplateBanBen> ptbbList,
			Integer rootId, ProcardTemplateBanBenApply ptbbA,
			ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		if (users == null) {
			return new Object[] { "请选登录!" };
		}
		ProcardTemplate totalPt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, rootId);
		if (totalPt == null) {
			return new Object[] { "对不起,没有找到对应的总成!" };
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, ptbbA.getId());
		if (ptbbApply == null) {
			return new Object[] { "对不起,没有找到设变申请!" };
		}
		if(!ptbbApply.getProcessStatus().equals("选择设变零件")){
			return new Object[] { "对不起,当前状态为："+ptbbApply.getProcessStatus()+",无法选择设变零件!" };
		}
		ptbbApply.setNeedDeptJudege(ptbbA.getNeedDeptJudege());
		ProcardTemplateBanBenJudges oldJudges = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, ptbbJudges
						.getId());
		if (oldJudges == null) {
			return new Object[] { "对不起,没有找到对应的项目工程师!" };
		}
		String nowTime = Util.getDateTime();
		// oldJudges.setSelectUserId(ptbbJudges.getSelectUserId());
		// oldJudges.setSelectUsers(ptbbJudges.getSelectUsers());
		oldJudges.setPlTime(nowTime);
		totalDao.update(oldJudges);
		ptbbApply.setProcessStatus("变更设计");
		if (ptbbList != null && ptbbList.size() > 0) {
			ptbbApply.setZpTime(Util.getDateTime());
			// 进程状态(设变发起,分发项目组,项目主管初评,选择设变零件，变更设计,工程师评审，成本审核，各部门评审，资料更新,关联并通知生产,生产后续,关闭,取消)
			Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
					.getProcardTemplateBanBen();
			if (ptbbSet == null) {
				ptbbSet = new HashSet<ProcardTemplateBanBen>();
			} else {
				// 选出要删除的对象
				Set<ProcardTemplateBanBen> deleteSet = new HashSet<ProcardTemplateBanBen>();
				for (ProcardTemplateBanBen fordelte : ptbbSet) {
					boolean had = false;
					for (ProcardTemplateBanBen newptbb : ptbbList) {
						if (newptbb != null && newptbb.getId() != null
								&& newptbb.getId().equals(fordelte.getId())) {
							fordelte.setNewBanBenNumber(newptbb
									.getNewBanBenNumber());
							fordelte.setUptype(newptbb.getUptype());// 版本升级,设变
							fordelte.setChangeProcess(newptbb
									.getChangeProcess());// 是否修改工序
							fordelte.setChangeTz(newptbb.getChangeTz());// 是否修改图纸
							fordelte.setRemark(newptbb.getRemark());// 说明
							had = true;
							break;
						}
					}
					if (!had) {
						deleteSet.add(fordelte);
					}
				}
				if (deleteSet != null && deleteSet.size() > 0) {
					ptbbSet.removeAll(deleteSet);
					for (ProcardTemplateBanBen delte : deleteSet) {
						delte.setProcardTemplateBanBenApply(null);
						List<ProcardTemplate> backptList = totalDao
								.query(
										"from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus!='历史')",
										delte.getMarkId());
						if (backptList != null && backptList.size() > 0) {
							for (ProcardTemplate back : backptList) {
								back.setBzStatus("已批准");
								totalDao.update(back);
							}
						}
						totalDao.delete(delte);
					}
				}
			}
			int i = 1;
//			List<String> sbMarkIdList = new ArrayList<String>();
			for (ProcardTemplateBanBen ptbb : ptbbList) {
				if (ptbb != null && ptbb.getPtId() != null) {
					ProcardTemplate newProcardtemplate = null;// 如果是件号替换，查询出新的件号是否已存在
					if (ptbb.getRemark() == null
							|| ptbb.getRemark().length() == 0) {
						throw new RuntimeException("第" + (i + 1) + "条没有填写设变明细!");
					}
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, ptbb
									.getPtId());
					if (pt == null) {
						throw new RuntimeException("你选择的第" + i
								+ "条数据没有找到对应的零件!");
					}
					pt.setBzStatus("设变发起中");
					totalDao.update(pt);
//					sbMarkIdList.add(pt.getMarkId());
					if (ptbb.getId() == null) {
						// ptbb.setptId;//Bom模板id
						ptbb.setFptId(pt.getFatherId());
						ptbb.setMarkId(pt.getMarkId());// 件号
						ptbb.setRootMarkId(totalPt.getMarkId());
						ptbb.setYwMarkId(totalPt.getYwMarkId());// 业务件号（总成对外销售使用）
						ptbb.setProName(pt.getProName());// 名称
						ptbb.setUnit(pt.getUnit());// 单位
						ptbb.setProcardStyle(pt.getProcardStyle());// 卡片类型(总成，组合，外购，自制)
						ptbb.setProductStyle(pt.getProductStyle());// 产品类型(试制，批产)
						ptbb.setBanBenNumber(pt.getBanBenNumber());// 原版本号
						// ptbb.setnewBanBenNumber;//升级版本号
						Integer banci = pt.getBanci();
						if (banci == null) {
							banci = 0;
						}
						ptbb.setBanci(banci);// 版次
						ptbb.setNewbanci(banci + 1);// 新版次
						ptbb.setAddTime(nowTime);
						// ptbb.setuptype;
						// ptbb.setchangeProcess;//是否修改工序
						// ptbb.setchangeTz;//是否修改图纸
						// ptbb.setxuhao;//序号
						ptbb.setProcardTemplateBanBenApply(ptbbApply);
						ptbb.setKgliao(pt.getKgliao());
						ptbbSet.add(ptbb);
					} else {
						for (ProcardTemplateBanBen had : ptbbSet) {
							if (had.getId().equals(ptbb.getId())) {
								ptbb = had;
								break;
							}
						}
					}
					if (ptbb.getProductStyle().equals("批产")) {
						if (ptbb.getSingleSb().equals("是")) {
							throw new RuntimeException("批产不允许单独设变");
						}
					} else {
						if (ptbb.getSingleSb().equals("是")
								&& ptbb.getUptype().equals("设变")) {
							throw new RuntimeException("单独设变必须是版本升级或者件号替换!");
						}
					}
					if (ptbb.getUptype().equals("件号替换")) {
						if (ptbb.getNewmarkId() == null
								|| ptbb.getNewmarkId().length() == 0) {
							throw new RuntimeException("零件" + ptbb.getMarkId()
									+ "的设变类型为件号替换请填写件号!");
						}
						String newbanbenSql = null;
						if (ptbb.getNewBanBenNumber() == null
								|| ptbb.getNewBanBenNumber().equals("")) {
							newbanbenSql = " and (banBenNumber is null or banBenNumber='')";
						} else {
							newbanbenSql = " and banBenNumber='"
									+ ptbb.getNewBanBenNumber();
						}
						newProcardtemplate = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=? and productStyle='批产' "
												+ "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')",
										ptbb.getNewmarkId());
						if (ptbb.getProductStyle().equals("批产")) {
							if (newProcardtemplate != null
									&& !Util.isEquals(
											ptbb.getNewBanBenNumber(),
											newProcardtemplate
													.getBanBenNumber())) {
								throw new RuntimeException("零件"
										+ ptbb.getMarkId()
										+ "的设变类型为件号替换，新版本号为:"
										+ ptbb.getNewBanBenNumber()
										+ ",系统现有版本为："
										+ newProcardtemplate.getBanBenNumber());
							}
						} else {
							if (newProcardtemplate != null) {
								if (!Util.isEquals(ptbb.getNewBanBenNumber(),
										newProcardtemplate.getBanBenNumber())) {
									String pcbaneben = newProcardtemplate
											.getBanBenNumber();
									newProcardtemplate = (ProcardTemplate) totalDao
											.getObjectByCondition(
													"from ProcardTemplate where markId=? and productStyle='试制' "
															+ "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')"
															+ newbanbenSql,
													ptbb.getNewmarkId());
									if (newProcardtemplate == null) {
										throw new RuntimeException("零件"
												+ ptbb.getMarkId()
												+ "的设变类型为件号替换，新版本号为:"
												+ ptbb.getNewBanBenNumber()
												+ ",系统现有批产版本为：" + pcbaneben
												+ "并且系统中也无此版本试制零件!");
									}
								}
							} else {
								newProcardtemplate = (ProcardTemplate) totalDao
										.getObjectByCondition(
												"from ProcardTemplate where markId=? and productStyle='试制' "
														+ "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')"
														+ newbanbenSql, ptbb
														.getNewmarkId());
								if (newProcardtemplate == null) {
									newProcardtemplate = (ProcardTemplate) totalDao
											.getObjectByCondition(
													"from ProcardTemplate where markId=? and productStyle='试制' "
															+ "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')",
													ptbb.getNewmarkId());
									throw new RuntimeException("零件"
											+ ptbb.getMarkId()
											+ "的设变类型为件号替换，新版本号为:"
											+ ptbb.getNewBanBenNumber()
											+ ",系统现无批产此零件"
											+ "并且系统中试制零件版本为:"
											+ newProcardtemplate
													.getBanBenNumber() + "!");
								}
							}
						}
					}
					// 在制数量
					Float zaizhiCount = 0f;
					// 在途数量
					Float zaituCount = 0f;
					
					String kgsql = "";
					if (ptbb.getKgliao() != null
							&& ptbb.getKgliao().length() > 0) {
						kgsql += " and kgliao ='" + ptbb.getKgliao() + "'";
					}
					String goodsClassSql = " and goodsClass in ('外购件库') "
							+ kgsql;
					String banben_hql = "";
					String banben_hql2 = "";
					if (ptbb.getBanBenNumber() != null
							&& ptbb.getBanBenNumber().length() > 0) {
						banben_hql = " and banBenNumber='"
								+ ptbb.getBanBenNumber() + "'";
						banben_hql2 = " and banben='" + ptbb.getBanBenNumber()
								+ "'";
					}
					String specification_sql = "";

					// 库存量(件号+版本+供料属性+库别)
					String hqlGoods = "";
					hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
							+ goodsClassSql
							+ " and goodsCurQuantity>0 "
							+ banben_hql
							+ " and (fcStatus is null or fcStatus='可用')";
					Float kcCount = (Float) totalDao.getObjectByCondition(
							hqlGoods, ptbb.getMarkId());
					if (kcCount == null || kcCount < 0) {
						kcCount = 0f;
					}
					/****************** 占用量=生产占用量+导入占用量 ******************************/
					// 系统占用量(含损耗)(已计算过采购量(1、有库存 2、采购中)，未领料)
					String zyCountSql = "select sum(filnalCount-klNumber+hascount) from Procard where markId=? "
						+ banben_hql
						+ " and jihuoStatua='激活' and status='已发卡' and procardStyle='外购' and (lingliaostatus='是' or lingliaostatus is null or lingliaostatus='' ) "
						+ " and (sbStatus<>'删除' or sbStatus is null ) ";
					if("外购".equals(ptbb.getProcardStyle())){
						zyCountSql+=" and kgliao='"+ptbb.getKgliao()+"'";
					}
					Double zyCountD = (Double) totalDao.getObjectByConditionforDouble(
							zyCountSql, ptbb.getMarkId());
					if (zyCountD == null || zyCountD < 0) {
						zyCountD = 0d;
					}
					zaizhiCount = zyCountD.floatValue();
					
					if("外购".equals(ptbb.getProcardStyle())){
						// 系统在途量(已生成物料需求信息，未到货)
						String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
								+ banben_hql2
								+ " and kgliao=? and (number>rukuNum or rukuNum is null)"
								+ specification_sql;
						Double ztCount = (Double) totalDao.getObjectByCondition(
								hql_zc0, ptbb.getMarkId(), ptbb.getKgliao());
						if (ztCount == null) {
							ztCount = 0D;
						}
						zaituCount=ztCount.floatValue();
					}else{
						// 系统委外量(委外数量)--自制件的在途数量
						String wwCountSql = "select sum(wwblCount) from  Procard where markId=? "
								+ banben_hql
								+ "and jihuoStatua='激活' and status='已发卡' and procardStyle='外购' "
								+ "and cgNumber is not null and wwblCount is not null";
						Float wwCount = (Float) totalDao.getObjectByCondition(
								wwCountSql, ptbb.getMarkId());
						if (wwCount == null || wwCount < 0) {
							wwCount = 0f;
						}
						zaituCount=wwCount.floatValue();
					}
					ptbb.setZaizhiCount(zaizhiCount);
					ptbb.setZaituCount(zaituCount);
					// 库存数量
					if (ptbb.getProcardStyle().equals("自制")) {
						ptbb.setKucunCount(0f);
					} else {
						ptbb.setKucunCount(kcCount);
					}
					if (ptbb.getId() == null) {
						totalDao.save(ptbb);
					} else {
						totalDao.update(ptbb);
					}
					List<Procard> procardSameList = null;
					// if (pt.getProductStyle().equals("试制")) {
					// // 同总成生产任务
					// procardSameList = totalDao
					// .query(
					// "from Procard where markId =? and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where procardTemplateId=? and (tjNumber is null or tjNumber<filnalCount) and status in('初始','已发卡','设变锁定','已发料','领工序','待入库'))",
					// ptbb.getMarkId(), rootId);
					// } else {
					// // 同总成生产任务
					// }
					procardSameList = totalDao
							.query(
//									"from Procard where markId =? and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where markId=? and (tjNumber is null or tjNumber<filnalCount) and status in('初始','已发卡','已发料','设变锁定','完成','领工序','待入库'))",
									"from Procard where markId =? and productStyle=?  and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where markId=? and  status in('初始','已发卡','已发料','设变锁定','完成','领工序','待入库'))",
									ptbb.getMarkId(),totalPt.getProductStyle(), totalPt.getMarkId());
					if (procardSameList != null && procardSameList.size() > 0) {
						for (Procard same : procardSameList) {
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus from Procard where id=?",
											same.getRootId());
							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							pabb.setProcardId(same.getId());// 零件id
							pabb.setFprocardId(same.getFatherId());
							pabb.setOrderNumber(same.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(same.getMarkId());// 件号
							pabb.setSelfCard(same.getSelfCard());// 批次
							pabb.setProName(same.getProName());// 零件名称
							pabb.setBanebenNumber(same.getBanBenNumber());// 版本号
							if (same.getBanci() != null) {
								pabb.setBanci(same.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(same.getFilnalCount());

							// pabb.setclType;//处理方案
							pabb.setStatus("待处理");// 状态
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(same.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(same.getProcardStyle());

							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											same.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getApplyCount() < same
												.getFilnalCount()) {// 有还没有领的工序
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(null);// 工序号
											processabb.setProcessName("未生产");// 工序名称
											processabb.setScCount(same
													.getFilnalCount()
													- process.getApplyCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
									if (process.getApplyCount() == 0) {
										continue;
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									Float wcCount = null;
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										//零件做完数量
										wcCount = process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
									if(wcCount!=null){
										ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
										processabb.setProcessNo(null);// 工序号
										processabb.setProcessName("完成");// 工序名称
										processabb.setScCount(same.getFilnalCount());// 生产数量
										processabb.setPabb(pabb);// 生产关联零件
										processabbjSet.add(processabb);
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(same.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							if(!same.getStatus().equals("设变锁定")){
								// 记录原生产状态
								same.setOldStatus(same.getStatus());
								// 暂停生产
								same.setStatus("设变锁定");
							}
							same.setSduser(users.getName());
							totalDao.save(pabb);
							totalDao.update(same);
							sbsuodingwaiwei(same, ptbbApply, ptbb, users);
						}
					}
					// 同件号非同总成零件(对到明细上面)
					List<Procard> procardList = totalDao
							.query(
									"from Procard where (sbStatus is null or sbStatus !='删除') and productStyle=? and rootId not in(select id from Procard where markId =?) and markId =?"
											+ "and rootId in(select id from Procard where procardStyle='总成'  and status in('初始','已发卡','已发料','设变锁定','领工序','完成','待入库'))",
											ptbbApply.getProductStyle(),totalPt.getMarkId(), pt.getMarkId());
					if (procardList != null && procardList.size() > 0) {
						for (Procard other : procardList) {
							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus from Procard where id=?",
											other.getRootId());
							pabb.setProcardId(other.getId());// 零件id
							pabb.setFprocardId(other.getFatherId());
							pabb.setOrderNumber(other.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(other.getMarkId());// 件号
							pabb.setSelfCard(other.getSelfCard());// 批次
							pabb.setProName(other.getProName());// 零件名称
							pabb.setBanebenNumber(other.getBanBenNumber());// 版本号
							if (other.getBanci() != null) {
								pabb.setBanci(other.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(other.getFilnalCount());
							pabb.setStatus("待处理");
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(other.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());
							;// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(other.getProcardStyle());
							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											other.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getSubmmitCount() == 0) {
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											if (process.getApplyCount() == 0) {
												processabb.setProcessNo(null);// 工序号
												processabb
														.setProcessName("未生产");// 工序名称
											} else {
												processabb.setProcessNo(process
														.getProcessNO());// 工序号
												processabb
														.setProcessName(process
																.getProcessName());// 工序名称
											}
											processabb.setScCount(other
													.getFilnalCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
											break;
										}
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										zzCount += process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(other.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							// 记录原生产状态
							other.setOldStatus(other.getStatus());
							// 暂停生产
							if(!other.getStatus().equals("设变锁定")){
								// 记录原生产状态
								other.setOldStatus(other.getStatus());
								// 暂停生产
								other.setStatus("设变锁定");
							}
							other.setSduser(users.getName());
							totalDao.save(pabb);
							totalDao.update(other);
							sbsuodingwaiwei(other, ptbbApply, ptbb, users);
						}
					}
					String sql = null;
					if (ptbb.getSingleSb() != null
							&& ptbb.getSingleSb().equals("是")) {
						sql = " and rootId=" + rootId;
					} else {
						if (ptbb.getBanBenNumber() == null
								|| ptbb.getBanBenNumber().length() == 0) {
							sql = " and (banBenNumber is null or banBenNumber='') ";
						} else {
							sql = " and banBenNumber='"
									+ ptbb.getBanBenNumber() + "'";
						}
					}
					List<ProcardTemplate> ptList = totalDao
							.query(
									"from ProcardTemplate where markId = ? "
											+ sql
											+ " and productStyle=? and (banbenStatus is null or banbenStatus!='历史')",
									pt.getMarkId(), ptbb.getProductStyle());
					for (ProcardTemplate ptSame : ptList) {
						ptSame.setBzStatus("设变发起中");
						totalDao.update(ptSame);
					}
//					//生成设变BOM
//					//先查此设变有没有关联生成过此零件的设变零件
//					List<ProcardTemplatesb> ptsbList = totalDao.query("from ProcardTemplatesb where sbApplyId=? and sbApplyStatus='正常' and markId=?",
//							ptbbA.getId(),pt.getMarkId());
//					if(ptsbList!=null&&ptsbList.size()>0){
//						for(ProcardTemplatesb ptsb :ptsbList){
//							ptsb.setBzStatus("待编制");
//							ptsb.setSbApplyDetailId(ptbb.getId());
//							ptsb.setIssb("yes");
//							totalDao.update(ptsb);
//						}
//					}else{
//						pttoptsb(ptbbApply,ptbb,pt,null,users,nowTime,0,null);
//					}
					i++;
				}
			}
			
			//生成对应的设变BOM树并将对应零件对应发起设变零件标记为待编制
			StringBuffer sbmarkId = new StringBuffer();
			if(ptbbSet!=null&&ptbbSet.size()>0){
				for(ProcardTemplateBanBen ptbbm: ptbbSet){
					if(sbmarkId.length()==0){
						sbmarkId.append("'"+ptbbm.getMarkId()+"'");
					}else{
						sbmarkId.append(",'"+ptbbm.getMarkId()+"'");
					}
				}
			}
//			List<Integer> addptIdList =null;
			List<Integer> addptIdList =gettoybyBomptId(rootId,sbmarkId);
			pttoptsb(ptbbApply,ptbbSet,totalPt,null,users,nowTime,null,addptIdList);

		}
		totalDao.update(ptbbApply);
		return new Object[] { "true", ptbbApply.getId() };
	}
	//设变锁定外委
	private void sbsuodingwaiwei(Procard same,
			ProcardTemplateBanBenApply ptbbApply,ProcardTemplateBanBen ptbb, Users users) {
		// TODO Auto-generated method stub
		//集齐本身及其上层的所有id
		StringBuffer sb = new StringBuffer();
		Procard sonp = same;
		sb.append(sonp.getId());
		while(true){
			if(sonp.getProcard()!=null){
				sonp = sonp.getProcard();
				sb.append(","+sonp.getId());
			}else{
				break;
			}
		}
		//获取所有需要锁定的数据
		List<WaigouPlan> waigouPlanList = totalDao.query(" from WaigouPlan where status not in('取消','删除','入库','关闭') " +
				"and id in(select wgOrderId from ProcardWGCenter where procardId in("+sb.toString()+") )");
		if(waigouPlanList!=null&&waigouPlanList.size()>0){
			for(WaigouPlan wp:waigouPlanList){
				WaigouPlanLock wplock = new WaigouPlanLock();
				wplock.setEntityName("WaigouPlan");//被锁定表名
				wplock.setEntityId(wp.getId());//被输定表Id
//				wplock.setProcardId();//设变相关零件Id
				wplock.setMarkId(wp.getMarkId());//设变相关零件件号
				wplock.setSbApplyId(ptbbApply.getId());//设变单Id
				wplock.setSbApplyDetailId(ptbb.getId());//设变明细Id
				wplock.setSbProcardId(same.getId());//设变零件Id
				wplock.setSbmarkId(same.getMarkId());//件号
				wplock.setRemark(ptbb.getRemark());//设变内容说明
				wplock.setZxStatus("执行中");
				wplock.setDataStatus("使用");
				if(!wp.getStatus().equals("设变锁定")){
					wplock.setLockStatus(wp.getStatus());
					wp.setOldStatus(wp.getStatus());
					wp.setStatus("设变锁定");
					totalDao.update(wp);
				}else{
					wplock.setLockStatus(wp.getOldStatus());
				}
				wplock.setPlanNumber(wp.getWaigouOrder().getPlanNumber());
				totalDao.save(wplock);
			}
		}
		List<WaigouWaiweiPlan> waigouwaiweiPlanList = totalDao.query("from WaigouWaiweiPlan where " +
				"(status='待激活' or (status='设变锁定' and oldStatus in('待入库','待激活') )) and procardId in("+sb.toString()+") ");
		if(waigouwaiweiPlanList!=null&&waigouwaiweiPlanList.size()>0){
			for(WaigouWaiweiPlan wp:waigouwaiweiPlanList){
				WaigouPlanLock wplock = new WaigouPlanLock();
				wplock.setEntityName("WaigouWaiweiPlan");//被锁定表名
				wplock.setEntityId(wp.getId());//被输定表Id
//				wplock.setProcardId();//设变相关零件Id
				wplock.setMarkId(wp.getMarkId());//设变相关零件件号
				wplock.setSbApplyId(ptbbApply.getId());//设变单Id
				wplock.setSbApplyDetailId(ptbb.getId());//设变明细Id
				wplock.setSbProcardId(same.getId());//设变零件Id
				wplock.setSbmarkId(same.getMarkId());//件号
				wplock.setRemark(ptbb.getRemark());//设变内容说明
				wplock.setZxStatus("执行中");
				wplock.setDataStatus("使用");
				if(!wp.getStatus().equals("设变锁定")){
					wplock.setLockStatus(wp.getStatus());
					wp.setOldStatus(wp.getStatus());
					wp.setStatus("设变锁定");
					totalDao.update(wp);
				}else{
					wplock.setLockStatus(wp.getOldStatus());
				}
				wplock.setPlanNumber(wp.getPlanNumber());
				totalDao.save(wplock);
			}
		}
		List<ProcessInforWWApplyDetail> pwwdList = 
			totalDao.query(" from ProcessInforWWApplyDetail where " +
					"procardId in("+sb.toString()+") and (processStatus in('预选未审批','合同待确认','外委待下单') or (processStatus='设变锁定' and oldprocessStatus in('预选未审批','合同待确认','外委待下单')))");
		if(pwwdList!=null&&pwwdList.size()>0){
			for(ProcessInforWWApplyDetail pwwd:pwwdList){
				WaigouPlanLock wplock = new WaigouPlanLock();
				wplock.setEntityName("ProcessInforWWApplyDetail");//被锁定表名
				wplock.setEntityId(pwwd.getId());//被输定表Id
//				wplock.setProcardId();//设变相关零件Id
				wplock.setMarkId(pwwd.getMarkId());//设变相关零件件号
				wplock.setSbApplyId(ptbbApply.getId());//设变单Id
				wplock.setSbApplyDetailId(ptbb.getId());//设变明细Id
				wplock.setSbProcardId(same.getId());//设变零件Id
				wplock.setSbmarkId(same.getMarkId());//件号
				wplock.setRemark(ptbb.getRemark());//设变内容说明
				wplock.setZxStatus("执行中");
				wplock.setDataStatus("使用");
				if(!pwwd.getProcessStatus().equals("设变锁定")){
					wplock.setLockStatus(pwwd.getProcessStatus());
					pwwd.setOldprocessStatus(pwwd.getProcessStatus());
					pwwd.setProcessStatus("设变锁定");
					totalDao.update(pwwd);
				}else{
					wplock.setLockStatus(pwwd.getOldprocessStatus());
				}
				wplock.setPlanNumber(pwwd.getProcessInforWWApply().getWwApplyNumber());
				totalDao.save(wplock);
			}
		}
	}
	private List<Integer> gettoybyBomptId(Integer rootId, StringBuffer sbmarkId) {
		// TODO Auto-generated method stub
		List<Integer> addptIdList =new ArrayList<Integer>();
		List<ProcardTemplate> sbptList = totalDao.query("from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除') and markId in("+sbmarkId.toString()+")",rootId);
		if(sbptList!=null&&sbptList.size()>0){
			for(ProcardTemplate pt:sbptList){
				ProcardTemplate xhpt = pt;
				addptIdList.add(xhpt.getId());
				while (true){
					 xhpt= xhpt.getProcardTemplate();
					if(xhpt!=null){
						addptIdList.add(xhpt.getId());
					}else{
						break;
					}
				}
				Set<ProcardTemplate>sonSet = pt.getProcardTSet();
				if(sonSet!=null&&sonSet.size()>0){
					for(ProcardTemplate son:sonSet){
						if((son.getDataStatus()==null||!son.getDataStatus().equals("删除")) && (son.getBanbenStatus()==null||!son.getBanbenStatus().equals("历史"))){
							addptIdList.add(son.getId());
						}
					}
				}
			}
		}
		
		return addptIdList;
	}
	/**
	 * 根据工程BOM树生成设变BOM树
	 * @param ptbbApply
	 * @param sbMarkIdList
	 * @param pt
	 * @param fatherptsb
	 * @param user
	 * @param nowTime
	 * @param rootId
	 */
	private void pttoptsb(ProcardTemplateBanBenApply ptbbApply,
			Set<ProcardTemplateBanBen> ptbbSet, ProcardTemplate pt,ProcardTemplatesb fatherptsb,Users user,String nowTime,Integer rootId,List<Integer> addptIdList) {
		ProcardTemplatesb newptsb = new ProcardTemplatesb();
		newptsb.setSbApplyId(ptbbApply.getId());//设变单id
		newptsb.setSbNumber(ptbbApply.getSbNumber());
		newptsb.setOldPtId(pt.getId());//原生产bomId
		newptsb.setSbApplyStatus("正常");//设变状态('正常','删除')
		try {
			// TODO Auto-generated method stub
			BeanUtils.copyProperties(pt, newptsb, new String[] { "id",
					"procardTemplate", "procardTSet", "processTemplate",
					"zhUsers", "maxCount", 
					"rootId", "fatherId",  });
			for(ProcardTemplateBanBen ptbb: ptbbSet){
				if(ptbb.getMarkId().equals(newptsb.getMarkId())){
					newptsb.setBzStatus("待编制");
					newptsb.setBianzhiId(user.getId());
					newptsb.setBianzhiName(user.getName());
					if(ptbb.getUptype().equals("版本升级")){
						newptsb.setBanBenNumber(ptbb.getNewBanBenNumber());
					}
					newptsb.setBanci(ptbb.getNewbanci());
				}
			}
			String banciSql = null;
			if(pt.getBanci()==null||pt.getBanci()==0){
				banciSql =" and (banci is null or banci=0)";
			}else{
				banciSql =" and banci="+pt.getBanci();
			}
			//查询此件号是否已经生成过图纸
			Float hadtzCount = 0f;
			if(newptsb.getProductStyle().equals("批产")){
				hadtzCount = (Float) totalDao.getObjectByCondition("select count(*) from ProcessTemplateFilesb where sbApplyId=? and markId=?",ptbbApply.getId(), pt.getMarkId());
			}
				
			// 同步工序
			Set<ProcessTemplate> processSet1 = pt.getProcessTemplate();
			Set<ProcessTemplatesb> processSet2 = new HashSet<ProcessTemplatesb>();
			if (processSet1 != null) {// 添加在添加列表中存在的工序号的工序
				for (ProcessTemplate process1 : processSet1) {
					if (process1.getProcessNO() != null) {
						ProcessTemplatesb process2 = new ProcessTemplatesb();
//						// -----------辅料开始----------
//						if (process1.getIsNeedFuliao() != null
//								&& process1.getIsNeedFuliao().equals("yes")) {
//							Set<ProcessFuLiaoTemplate> fltmpSet = process1
//									.getProcessFuLiaoTemplate();
//							if (fltmpSet.size() > 0) {
//								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//									BeanUtils.copyProperties(fltmp, pinfoFl,
//											new String[] { "id",
//													"processTemplate" });
//									if (pinfoFl.getQuanzhi1() == null) {
//										pinfoFl.setQuanzhi1(1f);
//									}
//									if (pinfoFl.getQuanzhi2() == null) {
//										pinfoFl.setQuanzhi2(1f);
//									}
//									pinfoFl.setProcessTemplate(process2);
//									pinfoFlSet.add(pinfoFl);
//								}
//								process2.setProcessFuLiaoTemplate(pinfoFlSet);
//							}
//						}
//						// -----------辅料结束----------
						BeanUtils
								.copyProperties(process1, process2,
										new String[] { "id", "procardTemplate",
												"taSopGongwei",
												"processFuLiaoTemplate" });
						process2.setProcardTemplatesb(newptsb);
						process2.setProcesstId(process2.getId());
						totalDao.save(process2);
						processSet2.add(process2);
						List<ProcessTemplateFile> ptFileList = null;
						if ("试制".equals(pt.getProductStyle())) {
							ptFileList = totalDao.query(
									"from ProcessTemplateFile where  "
									+" glId=? and processNO =? "+banciSql,
									 process1
									.getId(),process2.getProcessNO());
						} else {
							if(hadtzCount==null||hadtzCount==0){
								ptFileList = totalDao
								.query(
										"from ProcessTemplateFile where  "
										+ "  markId=? and processNO =? and productStyle='批产' "
										+ banciSql,pt
										.getMarkId(),process2
										.getProcessNO());
							}
						}
						if (ptFileList != null && ptFileList.size() > 0) {
							for (ProcessTemplateFile ptFile : ptFileList) {
								if (ptFile != null) {
									ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
									BeanUtils
									.copyProperties(ptFile,
											ptFile2, new String[] {
											"id","glId" });
									ptFile2.setBanci(newptsb.getBanci());
									ptFile2.setSbApplyId(ptbbApply.getId());
									ptFile2.setProcesstFileId(ptFile.getId());
									ptFile2.setGlId(process2.getId());
									totalDao.save2(ptFile2);
									ptFile2=null ;//释放资源
								}
								ptFile=null;//释放资源
							}
						}
						if(ptFileList!=null){
							ptFileList.clear();//释放资源
						}
					}
				}
			}
			newptsb.setProcessTemplatesb(processSet2);
			if(fatherptsb!=null){
				newptsb.setProcardTemplatesb(fatherptsb);
				newptsb.setFatherId(fatherptsb.getId());
			}
			totalDao.save(newptsb);
			if(newptsb.getProcardStyle().equals("总成")&&rootId==null){
				rootId = newptsb.getId();
			}
			newptsb.setRootId(rootId);
			List<ProcessTemplateFile> ptFileList = null;
			if ("试制".equals(pt.getProductStyle())) {
				ptFileList = totalDao.query(
						"from ProcessTemplateFile where " +
						"glId=? "+banciSql+" and processNO is null", pt
						.getId());
			} else {
				if(hadtzCount==null||hadtzCount==0){
					ptFileList = totalDao.query(
							"from ProcessTemplateFile where "
							+ "  markId=? and  processNO is null and productStyle='批产' "
							+ banciSql, pt.getMarkId());
				}
			}
			if (ptFileList != null && ptFileList.size() > 0) {
				for (ProcessTemplateFile ptFile : ptFileList) {
					if (ptFile != null) {
						ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
						BeanUtils
						.copyProperties(ptFile, ptFile2,
								new String[] { "id",
								"productStyle", "glId" });
						ptFile2.setBanci(newptsb.getBanci());
						ptFile2.setProductStyle(newptsb.getProductStyle());
						ptFile2.setSbApplyId(ptbbApply.getId());
						ptFile2.setProcesstFileId(ptFile.getId());
						ptFile2.setGlId(newptsb.getId());
						totalDao.save(ptFile2);
						ptFile=null;//释放资源
						ptFile2=null;//释放资源
					}
				}
				if(ptFileList!=null){
					ptFileList.clear();//释放资源
				}
			}
			//复制下层
			Set<ProcardTemplate> ptsonSet =pt.getProcardTSet();
			if(ptsonSet!=null&&ptsonSet.size()>0){
				for(ProcardTemplate ptson:ptsonSet ){
					if((ptson.getBanbenStatus()==null||!ptson.getBanbenStatus().equals("历史"))&&addptIdList.contains(ptson.getId())){
						pttoptsb(ptbbApply,ptbbSet,ptson,newptsb,user,nowTime,newptsb.getRootId(), addptIdList);
					}
				}
			}
			
		} catch (BeansException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Integer getBomrootId( Integer id) {
		// TODO Auto-generated method stub
//		Integer rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProcardTemplatesb where sbApplyId=? and sbApplyStatus!='删除'" +
//				"and oldPtId in(select ptId from ProcardTemplateBanBen where procardTemplateBanBenApply.id=? and markId=?)", id,id,markId);
//		if(rootId==null){
//			rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProcardTemplatesb where sbApplyId=? and sbApplyStatus!='删除'" +
//					"and markId=?", id,id,markId);
//		}
//		return rootId;
		Integer rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProcardTemplatesb where sbApplyId=? and sbApplyStatus!='删除' ", id);
		return rootId;
	}
	@Override
	public List<ProcardTemplate> findProcardTemByRootId(Integer rootId) {
		// TODO Auto-generated method stub
		if ( rootId != null && rootId > 0) {
			String hql = "from ProcardTemplatesb where rootId=?  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')  order by xuhao";
			List<ProcardTemplate> ptList= totalDao.query(hql, rootId);
			return ptList;
			
		}
		return null;
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
							"from ProcardTemplatesb where   markId=? and procardTemplatesb.id=? and " +
							"(banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
							markId, fatherId);
		}else {
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
	public String addProcardTemplatesb(ProcardTemplatesb procardTemplatesb) {
		if (procardTemplatesb != null) {
			Users user = Util.getLoginUser();
			if(user==null){
				return "请先登录!";
			}
			String nowtime = Util.getDateTime();
			procardTemplatesb.setAddcode(user.getCode());
			procardTemplatesb.setAdduser(user.getName());
			procardTemplatesb.setAddtime(nowtime);
			ProcardTemplatesb father = getProcardsbTemById(procardTemplatesb.getFatherId());
			if (father != null
					&& father.getBzStatus() != null
					&& (father.getBzStatus().equals("已批准") || father
							.getBzStatus().equals("设变发起中"))) {
				return father.getMarkId()+"的编制状态为："+father.getBzStatus()+"不允许修改!";
			}
			if (procardTemplatesb.getProcardTemplatesb() != null
					&& procardTemplatesb.getProcardTemplatesb().getId() != null) {
				Float maxXuHao = (Float) totalDao
						.getObjectByCondition(
								"select max(xuhao) from ProcardTemplatesb where procardTemplatesb.id=?",
								procardTemplatesb.getProcardTemplatesb().getId());
				if (maxXuHao == null) {
					procardTemplatesb.setXuhao(1f);
				} else {
					procardTemplatesb.setXuhao(maxXuHao + 1);
				}
			} else {
				procardTemplatesb.setXuhao(1f);
			}
			// 是否之前已在BOM中存在
			String kgliaosql = "";
			if (procardTemplatesb.getKgliao() != null
					&& procardTemplatesb.getKgliao().length() > 0) {
				kgliaosql = " and kgliao ='" + procardTemplatesb.getKgliao()
						+ "'";
			} else {
				kgliaosql = " and (kgliao is null or kgliao='')";
			}
			//查询设变BOM中是否含有此零件
			ProcardTemplatesb oldptsb = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId=?  and markId=?  and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除') "+kgliaosql
					, father.getSbApplyId(),procardTemplatesb.getMarkId());
			if(oldptsb!=null){//设变BOM中有此零件
				if(procardTemplatesb.getProcardStyle().equals("外购")
						&&!oldptsb.getProcardStyle().equals("外购")){
					return "此零件在此BOM中已存在非外购件类型，请勿以外购件类型加入此BOM!";
				}
				if(!procardTemplatesb.getProcardStyle().equals("外购")
						&&oldptsb.getProcardStyle().equals("外购")){
					return "此零件在此BOM中已存外购件类型，请勿以非外购件类型加入此BOM!";
				}
				String oldProcardStyle = oldptsb.getProcardStyle();
				Float oldq1 = oldptsb.getQuanzi1();
				Float oldq2 = oldptsb.getQuanzi2();
				Float oldc = oldptsb.getCorrCount();
				Float xuhao = oldptsb.getXuhao();
				String hasChange = oldptsb.getHasChange();
				oldptsb.setProcardStyle(procardTemplatesb.getProcardStyle());
				oldptsb.setXuhao(procardTemplatesb.getXuhao());
				if (procardTemplatesb.getProcardStyle().equals("外购")) {
					oldptsb.setQuanzi1(procardTemplatesb.getQuanzi1());
					oldptsb.setQuanzi2(procardTemplatesb.getQuanzi2());
					oldptsb
							.setLingliaostatus(procardTemplatesb
									.getLingliaostatus());
				} else {
					oldptsb.setCorrCount(procardTemplatesb.getCorrCount());
				}
				if (procardTemplatesb.getBiaochu() != null
						&& procardTemplatesb.getBiaochu().length() > 0) {
					oldptsb.setBiaochu(procardTemplatesb.getBiaochu());
				}
				oldptsb.setHasChange("是");
				// 重新取一遍为了加载关联子层
				Integer addId = null;
				addId = saveCopyProcardsb(father, oldptsb, procardTemplatesb.getProductStyle());
				oldptsb.setHasChange(hasChange);
				oldptsb.setProcardStyle(oldProcardStyle);
				oldptsb.setQuanzi1(oldq1);
				oldptsb.setQuanzi2(oldq2);
				oldptsb.setCorrCount(oldc);
				oldptsb.setXuhao(xuhao);
				ProcardTemplatesb newpt = (ProcardTemplatesb) totalDao
						.getObjectById(ProcardTemplatesb.class, addId);
				// 添加修改日志
				if (newpt != null) {
					ProcardTemplatesbChangeLog.addchangeLog(totalDao, father,
							newpt, "子件", "增加", user, nowtime );
				}
				return "true";
			
			}
			ProcardTemplate oldPt = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId=? and procardStyle=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
									+ kgliaosql, procardTemplatesb.getMarkId(),
									procardTemplatesb.getProcardStyle(), "批产");
			if (oldPt == null) {
				oldPt = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
										+ kgliaosql, procardTemplatesb
										.getMarkId(), "批产");
			}
			if (oldPt == null) {
				if (procardTemplatesb.getProductStyle().equals("试制")) {// 试制先借用批产，借用不到在借用试制
					oldPt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and procardStyle=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
											+ kgliaosql, procardTemplatesb
											.getMarkId(), procardTemplatesb
											.getProcardStyle(), "试制");
					if (oldPt == null) {
						oldPt = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus !='删除')"
								+ kgliaosql, procardTemplatesb
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
				oldPt.setProcardStyle(procardTemplatesb.getProcardStyle());
				oldPt.setXuhao(procardTemplatesb.getXuhao());
				if (procardTemplatesb.getProcardStyle().equals("外购")) {
					oldPt.setQuanzi1(procardTemplatesb.getQuanzi1());
					oldPt.setQuanzi2(procardTemplatesb.getQuanzi2());
					oldPt
							.setLingliaostatus(procardTemplatesb
									.getLingliaostatus());
				} else {
					oldPt.setCorrCount(procardTemplatesb.getCorrCount());
				}
				if (procardTemplatesb.getBiaochu() != null
						&& procardTemplatesb.getBiaochu().length() > 0) {
					oldPt.setBiaochu(procardTemplatesb.getBiaochu());
				}
				oldPt.setHasChange("是");
				// 重新取一遍为了加载关联子层
				Integer addId = null;
				addId = saveCopyProcard(father, oldPt, procardTemplatesb
						.getProductStyle());
				oldPt.setHasChange(hasChange);
				oldPt.setProcardStyle(oldProcardStyle);
				oldPt.setQuanzi1(oldq1);
				oldPt.setQuanzi2(oldq2);
				oldPt.setCorrCount(oldc);
				oldPt.setXuhao(xuhao);
				ProcardTemplatesb newpt = (ProcardTemplatesb) totalDao
						.getObjectById(ProcardTemplatesb.class, addId);
				// 添加修改日志
				if (newpt != null) {
					ProcardTemplatesbChangeLog.addchangeLog(totalDao, father,
							newpt, "子件", "增加", user, nowtime );
				}
				return "true";
			}
			procardTemplatesb.setProcardTemplatesb(father);
			// 不存在
			if (procardTemplatesb.getBianzhiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						" select name from Users where id =?", procardTemplatesb
								.getBianzhiId());
				procardTemplatesb.setBianzhiName(name);
				procardTemplatesb.setBzStatus("待编制");
			} else {
				procardTemplatesb.setBzStatus("初始");
			}
			if (procardTemplatesb.getJiaoduiId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplatesb
								.getJiaoduiId());
				procardTemplatesb.setJiaoduiName(name);
			}
			if (procardTemplatesb.getShenheId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplatesb
								.getShenheId());
				procardTemplatesb.setShenheName(name);
			}
			if (procardTemplatesb.getPizhunId() != null) {
				String name = (String) totalDao.getObjectByCondition(
						"select name from Users where id =?", procardTemplatesb
								.getPizhunId());
				procardTemplatesb.setPizhunName(name);
			}
//			if (procardTemplatesb.getProcardStyle().equals("外购")
//					&& (procardTemplatesb.getNeedProcess() == null || procardTemplatesb
//							.getNeedProcess().equals("no"))) {
//				procardTemplatesb.setSafeCount(null);
//				procardTemplatesb.setLastCount(null);
//			}
			procardTemplatesb.setRootMarkId(procardTemplatesb.getProcardTemplatesb()
					.getRootMarkId());
			procardTemplatesb.setYwMarkId(procardTemplatesb.getProcardTemplatesb()
					.getYwMarkId());
			procardTemplatesb.setSbApplyId(father.getSbApplyId());
			if (procardTemplatesb.getLoadMarkId() == null
					|| procardTemplatesb.getLoadMarkId().length() == 0) {
				String sql = "select loadMarkId from ProcardTemplate where markId=? and loadMarkId is not null and loadMarkId !=''";
				if (procardTemplatesb.getBanBenNumber() == null
						|| procardTemplatesb.getBanBenNumber().length() == 0) {
					sql += " and banBenNumber is null or banBenNumber!=''";
				} else {
					sql += " and banBenNumber = '"
							+ procardTemplatesb.getBanBenNumber() + "'";
				}
				String loadMarkId = (String) totalDao.getObjectByCondition(sql,
						procardTemplatesb.getMarkId());
				if (loadMarkId == null) {
					loadMarkId = (String) totalDao
							.getObjectByCondition(
									"select markId from ProcardTemplate where rootId=?",
									procardTemplatesb.getRootId());
				}
				procardTemplatesb.setBanci(0);
				procardTemplatesb.setLoadMarkId(loadMarkId);
				procardTemplatesb.setHasChange("是");
			}
			procardTemplatesb.sumXiaoHaoCount(0);
			procardTemplatesb.setAdduser(user.getName());// 添加人
			procardTemplatesb.setAddcode(user.getCode());// 添加人工号
			procardTemplatesb.setAddtime(Util.getDateTime());// 添加时间
			boolean bool = true;
			try {
				bool = totalDao.save(procardTemplatesb);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				throw new RuntimeException("添加失败!");
			}
			// 同步相同上层
			String banbenSql = "";
			if(procardTemplatesb.getProcardTemplatesb().getBanBenNumber()==null
					||procardTemplatesb.getProcardTemplatesb().getBanBenNumber().length()==0){
				banbenSql =" and (banBenNumber is null or banBenNumber!='')";
			}else{
				banbenSql =" and banBenNumber='"+procardTemplatesb.getProcardTemplatesb().getBanBenNumber()+"'";
				
			}
			List<ProcardTemplatesb> fatherList = totalDao
					.query(
							"from ProcardTemplatesb where sbApplyId=? and  markId=(select markId from ProcardTemplatesb where id=?) and procardStyle!='外购' and id !=? and (dataStatus is null or dataStatus!='删除') and (banbenStatus is null or banbenStatus='默认')"+banbenSql,
							procardTemplatesb.getSbApplyId(),procardTemplatesb.getFatherId(), procardTemplatesb
									.getFatherId());
			if (fatherList != null && fatherList.size() > 0) {
				for (ProcardTemplatesb father2 : fatherList) {
					Set<ProcardTemplatesb> sonList = father2.getProcardsbTSet();
					boolean has = false;
					for (ProcardTemplatesb son : sonList) {
						if (son.getMarkId().equals(procardTemplatesb.getMarkId())) {
							has = true;
						}
					}
					if (!has) {
						ProcardTemplatesb newSon = new ProcardTemplatesb();
						// newpt2.setMarkId("123");
						BeanUtils.copyProperties(procardTemplatesb, newSon,
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
						Set<ProcessTemplatesb> processSet = procardTemplatesb
								.getProcessTemplatesb();
						if (processSet != null && processSet.size() > 0) {
							Set<ProcessTemplatesb> newprocessSet = new HashSet<ProcessTemplatesb>();
							for (ProcessTemplatesb process : processSet) {
								ProcessTemplatesb p = new ProcessTemplatesb();
								BeanUtils.copyProperties(process, p,
										new String[] { "id", "procardTemplate",
												"processPRNScore",
												"taSopGongwei",
												"processFuLiaoTemplate" });
								p.setProcesstId(process.getId());
								// -----辅料开始---------
//								if (p.getIsNeedFuliao() != null
//										&& p.getIsNeedFuliao().equals("yes")) {
//									Set<ProcessFuLiaoTemplate> fltmpSet = process
//											.getProcessFuLiaoTemplate();
//									if (fltmpSet.size() > 0) {
//										Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//										for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//											ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//											BeanUtils
//													.copyProperties(
//															fltmp,
//															pinfoFl,
//															new String[] {
//																	"id",
//																	"processTemplate" });
//											if (pinfoFl.getQuanzhi1() == null) {
//												pinfoFl.setQuanzhi1(1f);
//											}
//											if (pinfoFl.getQuanzhi2() == null) {
//												pinfoFl.setQuanzhi2(1f);
//											}
//											pinfoFl.setProcessTemplate(p);
//											pinfoFlSet.add(pinfoFl);
//										}
//										p.setProcessFuLiaoTemplate(pinfoFlSet);
//									}
//								}
								// -----辅料结束---------
								newprocessSet.add(p);
							}
							newSon.setProcessTemplatesb(newprocessSet);
						}
						newSon.setProcardTemplatesb(father2);
						sonList.add(newSon);
						father2.setProcardsbTSet(sonList);
						totalDao.save(newSon);
					}
				}
			}
			if (bool) {
				// 添加修改日志
				ProcardTemplatesbChangeLog.addchangeLog(totalDao, father,
						procardTemplatesb, "子件", "增加", user, nowtime);
				return "true";
			}
		}
		return "添加失败";
	}
	
	private Integer saveCopyProcardsb(ProcardTemplatesb pt1,
			ProcardTemplatesb pt2, String productStyle) {
		ProcardTemplatesb sameSon = (ProcardTemplatesb) totalDao
				.getObjectByCondition(
						"from ProcardTemplatesb where  (banbenStatus is null or banbenStatus!='历史' ) and (dataStatus is null or dataStatus!='删除') " +
						" and  markId=? and  procardTemplatesb.id=?",
						pt2.getMarkId(), pt1.getId());
		if (sameSon != null) {
			MKUtil.writeJSON(false, "此件号下已存在该零件复制失败!", null);
			throw new RuntimeException("此件号下已存在该零件复制失败!");
		}
		Integer sonId = null;
		List<ProcardTemplatesb> sameptList = findSameProcardTemplatesb(pt1
				.getSbApplyId(), pt1.getMarkId(), pt1.getBanBenNumber(), pt1
				.getProductStyle(),pt2.getMarkId());
		pt2 = getProcardsbTemById(pt2.getId());
		String olprocardStyle = pt2.getProcardStyle();
		if (olprocardStyle.equals("总成")) {
			pt2.setProcardStyle("自制");
		}
		for (ProcardTemplatesb pt : sameptList) {
			if (!pt.getProcardStyle().equals("外购")) {
				if (sonId == null) {
					sonId = saveCopyProcardsbImple(pt, pt2, productStyle);
				} else {
					saveCopyProcardsbImple(pt, pt2, productStyle);
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
	private Integer saveCopyProcardsbImple(ProcardTemplatesb pt1,
			ProcardTemplatesb pt2, String productStyle) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		String time = Util.getDateTime();
		if (pt1 != null && pt2 != null) {
			// 获取主副两个对象的持久层
			ProcardTemplatesb oldpt1 = pt1;
			ProcardTemplatesb oldpt2 = pt2;
			if (oldpt1 != null && oldpt2 != null) {
				ProcardTemplatesb newpt2 = new ProcardTemplatesb();
				// newpt2.setMarkId("123");
				BeanUtils.copyProperties(pt2, newpt2, new String[] { "id",
						"procardTemplatesb", "procardsbTSet", "processTemplatesb",
						"zhUsers" });
				if (newpt2.getProcardStyle() != null
						&& newpt2.getProcardStyle().equals("总成")) {
					newpt2.setProcardStyle("自制");
				}
				if(newpt2.getBzStatus()==null||newpt2.getBzStatus().length()==0||newpt2.getBzStatus().equals("待编制")){
					newpt2.setBianzhiName(user.getName());
					newpt2.setBianzhiId(user.getId());
				}
				newpt2.setSbApplyId(pt1.getSbApplyId());
				newpt2.setId(null);
				newpt2.setRootId(oldpt1.getRootId());
				newpt2.setFatherId(oldpt1.getId());
				newpt2.setRootMarkId(oldpt1.getRootMarkId());
				newpt2.setYwMarkId(oldpt1.getYwMarkId());
				newpt2.setProductStyle(oldpt1.getProductStyle());
				newpt2.setAdduser(user.getName());
				newpt2.setAddcode(user.getCode());
				newpt2.setAddtime(Util.getDateTime());
				newpt2.setBelongLayer(oldpt1.getBelongLayer()+1);
				if (oldpt1.getBelongLayer() != null) {
					newpt2.setBelongLayer(oldpt1.getBelongLayer() + 1);
				}
				List<ProcardTemplatesb> ptSet = totalDao
						.query(
								"from ProcardTemplatesb where procardTemplatesb.id=? and (banbenStatus is null or banbenStatus='' or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除') order by id asc",
								oldpt2.getId());
				// Set<ProcardTemplate> ptSet2 = new
				// HashSet<ProcardTemplate>();
				// if (ptSet.size() > 0) {
				// for (ProcardTemplate pt : ptSet) {
				// ptSet2.add(pt);
				// }
				// }
				// 复制process
				Set<ProcessTemplatesb> processSet = oldpt2.getProcessTemplatesb();
				if (processSet != null && processSet.size() > 0) {
					Set<ProcessTemplatesb> newprocessSet = new HashSet<ProcessTemplatesb>();
					for (ProcessTemplatesb process : processSet) {
						ProcessTemplatesb p = new ProcessTemplatesb();
						BeanUtils.copyProperties(process, p, new String[] {
								"id", "procardTemplatesb", "processPRNScore",
								"taSopGongwei", "processFuLiaoTemplate" });
						p.setAddTime(Util.getDateTime());
						p.setAddUser(user.getName());
						p.setProcesstId(process.getId());
						// -----辅料开始---------
//						if (p.getIsNeedFuliao() != null
//								&& p.getIsNeedFuliao().equals("yes")) {
//							Set<ProcessFuLiaoTemplate> fltmpSet = process
//									.getProcessFuLiaoTemplate();
//							if (fltmpSet.size() > 0) {
//								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//									BeanUtils.copyProperties(fltmp, pinfoFl,
//											new String[] { "id",
//													"processTemplate" });
//									if (pinfoFl.getQuanzhi1() == null) {
//										pinfoFl.setQuanzhi1(1f);
//									}
//									if (pinfoFl.getQuanzhi2() == null) {
//										pinfoFl.setQuanzhi2(1f);
//									}
//									pinfoFl.setProcessTemplate(p);
//									pinfoFlSet.add(pinfoFl);
//								}
//								p.setProcessFuLiaoTemplate(pinfoFlSet);
//							}
//						}
//						// -----辅料结束---------
						newprocessSet.add(p);
						totalDao.save(p);
						if (productStyle.equals("试制")) {// 试制图纸复制
							String tzSql = null;
							if (pt2.getProductStyle().equals("试制")) {
								tzSql = "from ProcessTemplateFilesb where glId="
										+ process.getId()
										+ " and processNO is not null";
							} else {
								tzSql = "from ProcessTemplateFilesb where glId is null and processNO ='"
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
							List<ProcessTemplateFilesb> processFileList = totalDao
									.query(tzSql);
							if (processFileList != null
									&& processFileList.size() > 0) {
								for (ProcessTemplateFilesb processFile : processFileList) {
									if (processFile.getBanci() == null) {
										processFile.setBanci(0);
									}
									ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
									BeanUtils.copyProperties(processFile,
											newfile, new String[] { "id" });
									newfile.setAddTime(time);
									newfile.setProductStyle(pt1.getProductStyle());
									newfile.setSbApplyId(pt1.getSbApplyId());
									newfile.setProcesstFileId(processFile.getProcesstFileId());
									newfile.setGlId(p.getId());
									totalDao.save(newfile);
								}
							}
						}
					}
					newpt2.setProcessTemplatesb(newprocessSet);
				}
				newpt2.setProcardTemplatesb(oldpt1);
				Set<ProcardTemplatesb> oldpt1ptSet = oldpt1.getProcardsbTSet();
				if (oldpt1ptSet != null && oldpt1ptSet.size() > 0) {
					for (ProcardTemplatesb p : oldpt1ptSet) {
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
				oldpt1.setProcardsbTSet(oldpt1ptSet);
				totalDao.save(newpt2);
				if (productStyle.equals("试制")) {// 试制图纸复制
					String tzSql = null;
					if (pt2.getProductStyle().equals("试制")) {
						tzSql = "from ProcessTemplateFilesb where glId="
								+ pt2.getId() + " and processNO is  null";
					} else {
						tzSql = "from ProcessTemplateFilesb where glId is null and processNO is null and markId='"
								+ newpt2.getMarkId() + "'";
						if (pt2.getBanci() == null || pt2.getBanci() == 0) {
							tzSql += " and (banci is null or banci=0)";
						} else {
							tzSql += " and banci=" + pt2.getBanci();
						}
					}
					List<ProcessTemplateFilesb> processFileList = totalDao
							.query(tzSql);
					if (processFileList != null && processFileList.size() > 0) {
						for (ProcessTemplateFilesb processFile : processFileList) {
							if (processFile.getBanci() == null) {
								processFile.setBanci(0);
							}
							ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
							BeanUtils.copyProperties(processFile, newfile,
									new String[] { "id" });
							newfile.setAddTime(time);
							newfile.setStatus("默认");
							newfile.setAddTime(time);
							newfile.setProductStyle(pt1.getProductStyle());
							newfile.setSbApplyId(pt1.getSbApplyId());
							newfile.setProcesstFileId(processFile.getProcesstFileId());
							newfile.setGlId(newpt2.getId());
							totalDao.save(newfile);
						}
					}
				}
				if (ptSet.size() > 0) {
					for (ProcardTemplatesb pt : ptSet) {
						saveCopyProcardsbImple(newpt2, pt, productStyle);
					}
				}
				// }
				return newpt2.getId();
			}
		}
		return null;
	}
	@Override
	public ProcardTemplatesb getProcardsbTemById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplatesb) totalDao.getObjectById(ProcardTemplatesb.class, id);
	}
	
	@Override
	public Integer saveCopyProcard(ProcardTemplatesb pt1, ProcardTemplate pt2,
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
		List<ProcardTemplatesb> sameptList =null;
		pt2 = getProcardTemById(pt2.getId());
		String olprocardStyle = pt2.getProcardStyle();
		if (olprocardStyle.equals("总成")) {
			pt2.setProcardStyle("自制");
		}
		sameptList=findSameProcardTemplatesb(pt1.getSbApplyId(),pt1.getMarkId(),
				pt1.getBanBenNumber(),pt1.getProductStyle(),pt2.getMarkId());
		if(sameptList!=null&&sameptList.size()>0){
			for (ProcardTemplatesb pt : sameptList) {
				if (!pt.getProcardStyle().equals("外购")) {
					if (sonId == null) {
						sonId = saveCopyProcardImple(pt, pt2, productStyle);
					} else {
						saveCopyProcardImple(pt, pt2, productStyle);
					}
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
	public Integer saveCopyProcardImple(ProcardTemplatesb pt1,
			ProcardTemplate pt2, String productStyle) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		String time = Util.getDateTime();
		if (pt1 != null && pt2 != null) {
			// 获取主副两个对象的持久层
			ProcardTemplatesb oldpt1 = pt1;
			ProcardTemplate oldpt2 = pt2;
			if (oldpt1 != null && oldpt2 != null) {
				ProcardTemplatesb newpt2 = new ProcardTemplatesb();
				// newpt2.setMarkId("123");
				BeanUtils.copyProperties(pt2, newpt2, new String[] { "id",
						"procardTemplate", "procardTSet", "processTemplate",
						"zhUsers" });
				if (newpt2.getProcardStyle() != null
						&& newpt2.getProcardStyle().equals("总成")) {
					newpt2.setProcardStyle("自制");
				}
				if(newpt2.getBzStatus()==null||newpt2.getBzStatus().length()==0||newpt2.getBzStatus().equals("待编制")){
					newpt2.setBianzhiName(user.getName());
					newpt2.setBianzhiId(user.getId());
				}
				newpt2.setId(null);
				newpt2.setSbApplyId(oldpt1.getSbApplyId());
				newpt2.setRootId(oldpt1.getRootId());
				newpt2.setFatherId(oldpt1.getId());
				newpt2.setBelongLayer(oldpt1.getBelongLayer()+1);
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
					Set<ProcessTemplatesb> newprocessSet = new HashSet<ProcessTemplatesb>();
					for (ProcessTemplate process : processSet) {
						ProcessTemplatesb p = new ProcessTemplatesb();
						BeanUtils.copyProperties(process, p, new String[] {
								"id", "procardTemplate", "processPRNScore",
								"taSopGongwei", "processFuLiaoTemplate" });
						p.setAddTime(Util.getDateTime());
						p.setAddUser(user.getName());
						p.setProcesstId(process.getId());
						// -----辅料开始---------
//						if (p.getIsNeedFuliao() != null
//								&& p.getIsNeedFuliao().equals("yes")) {
//							Set<ProcessFuLiaoTemplate> fltmpSet = process
//									.getProcessFuLiaoTemplate();
//							if (fltmpSet.size() > 0) {
//								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//									BeanUtils.copyProperties(fltmp, pinfoFl,
//											new String[] { "id",
//													"processTemplate" });
//									if (pinfoFl.getQuanzhi1() == null) {
//										pinfoFl.setQuanzhi1(1f);
//									}
//									if (pinfoFl.getQuanzhi2() == null) {
//										pinfoFl.setQuanzhi2(1f);
//									}
//									pinfoFl.setProcessTemplate(p);
//									pinfoFlSet.add(pinfoFl);
//								}
//								p.setProcessFuLiaoTemplate(pinfoFlSet);
//							}
//						}
//						// -----辅料结束---------
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
									ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
									BeanUtils.copyProperties(processFile,
											newfile, new String[] { "id" });
									newfile.setAddTime(time);
									newfile.setProductStyle(pt1.getProductStyle());
									newfile.setSbApplyId(pt1.getSbApplyId());
									newfile.setProcesstFileId(processFile.getId());
									newfile.setGlId(p.getId());
									totalDao.save(newfile);
									processFile=null;
								}
								processFileList.clear();
							}
						}
					}
					newpt2.setProcessTemplatesb(newprocessSet);
				}
				newpt2.setProcardTemplatesb(oldpt1);
				Set<ProcardTemplatesb> oldpt1ptSet = oldpt1.getProcardsbTSet();
				if (oldpt1ptSet != null && oldpt1ptSet.size() > 0) {
					for (ProcardTemplatesb p : oldpt1ptSet) {
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
				oldpt1.setProcardsbTSet(oldpt1ptSet);
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
							ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
							BeanUtils.copyProperties(processFile, newfile,
									new String[] { "id" });
							newfile.setAddTime(time);
							newfile.setStatus("默认");
							newfile.setAddTime(time);
							newfile.setProductStyle(pt1.getProductStyle());
							newfile.setSbApplyId(pt1.getSbApplyId());
							newfile.setProcesstFileId(processFile.getId());
							newfile.setGlId(newpt2.getId());
							totalDao.save(newfile);
							processFile=null;
						}
						processFileList.clear();
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
	public ProcardTemplate getProcardTemById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class, id);
	}
	@Override
	public List<ProcardTemplatesb> findSameProcardTemplatesb(Integer sbApplyId, String markId,
			String banBenNumber,String productStyle,String sonMarkId) {
		// TODO Auto-generated method stub
		String addSql = null;
		if (banBenNumber == null || banBenNumber.length() == 0) {
			return totalDao
					.query(
							"from ProcardTemplatesb where sbApplyId=? and productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and (banBenNumber is null or banBenNumber='')  and  markId=?" +
							" and id not in(select fatherId from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')) ",
							sbApplyId,productStyle,markId,sbApplyId,sonMarkId);
		} else {
			return totalDao
					.query(
							"from ProcardTemplatesb where sbApplyId=? and productStyle=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and banBenNumber=? and  markId=? "+
							" and id not in(select fatherId from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')) ",
							sbApplyId,productStyle,banBenNumber ,markId,sbApplyId,sonMarkId);
		}
	}
	@Override
	public String delProcardTemplate(ProcardTemplatesb oldProCard) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (oldProCard != null) {
			String markId = oldProCard.getMarkId();
			if (oldProCard.getProcardStyle().equals("总成")) {
				if (oldProCard.getBzStatus().equals("已批准")) {
					return "此总成状态为已批准不允许删除!";
				}
				// 查询是否有转过生产,如果转过生产就不能删除就不允许删除
				Float count = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from Procard where markId=? and procardTemplateId=? and procardStyle='总成' and (sbStatus is null or sbStatus !='删除') and status not in('完成','待入库','入库')",
								oldProCard.getMarkId(), oldProCard
										.getOldPtId());
				if (count != null && count > 0) {
					return "此总成有未结束的生产BOM不允许删除!";
				}
				ProcardTemplatesb old = (ProcardTemplatesb) totalDao.getObjectById(
						ProcardTemplatesb.class, oldProCard.getId());
				old.setDataStatus("删除");
				boolean b = totalDao.update(old);
				b = b & tbDownDataStatus(old);
				return b + "";
			} else {// 获取上层零件编制状态,已批准状态不允许删除
				String bzStatus = (String) totalDao.getObjectByCondition(
						"select bzStatus from ProcardTemplatesb where id=?",
						oldProCard.getFatherId());
				if (bzStatus != null
						&& (bzStatus.equals("已批准") || bzStatus.equals("设变发起中"))) {
					return "此零件上层零件状态为:" + bzStatus + "不允许删除下阶层!";
				}
			}
			boolean b = true;
			// 添加修改日志
			ProcardTemplatesb father = (ProcardTemplatesb) totalDao.getObjectById(
					ProcardTemplatesb.class, oldProCard.getFatherId());
			ProcardTemplatesbChangeLog.addchangeLog(totalDao, father,
					oldProCard, "子件", "删除", user, nowtime);
			// 删除上层同件号的同件号零件
			String banbenSql=null;
			if(oldProCard.getProcardTemplatesb().getBanBenNumber()==null
					||oldProCard.getProcardTemplatesb().getBanBenNumber().length()==0){
				banbenSql =" and (banBenNumber is null or banBenNumber='')";
			}else{
				banbenSql =" and banBenNumber='"+oldProCard.getProcardTemplatesb().getBanBenNumber()+"'";
				
			}
			List<ProcardTemplatesb> sonList = totalDao
					.query(
							"from ProcardTemplatesb where  markId=? and (banbenStatus is null or banbenStatus='默认') and productStyle=? and " +
							" procardTemplatesb.id in(select id from ProcardTemplatesb where sbApplyId=? and markId=? " +
							"and (dataStatus is null or dataStatus!='删除') "+banbenSql+")",
							markId, oldProCard.getProductStyle(),oldProCard.getSbApplyId(),
							oldProCard.getProcardTemplatesb().getMarkId());
			if (sonList != null && sonList.size() > 0) {
				boolean b2 = false;
				for (int i = (sonList.size() - 1); i >= 0; i--) {
					ProcardTemplatesb procardt = sonList.get(i);
					if (!b2) {
						if ("外购".equals(procardt.getProcardStyle())
								&& procardt.getProcardTemplatesb() != null) {
							b2 = true;
							List<ProcessAndWgProcardTem> proAndwgptList = totalDao
									.query(
											" from ProcessAndWgProcardTem where procardMarkId = ? and wgprocardMardkId =? ",
											procardt.getProcardTemplatesb()
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
					procardt.setProcardTemplatesb(null);
					b = b & totalDao.update(procardt);
					b = b & tbDownDataStatus(procardt);
				}
			}
			return "true";
		}

		return "没有找到目标零件!";
	}
	private boolean tbDownDataStatus(ProcardTemplatesb pt) {
		// TODO Auto-generated method stub
		Set<ProcardTemplatesb> sonSet = pt.getProcardsbTSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (ProcardTemplatesb son : sonSet) {
				// Set<ProcessTemplate> processSet = son.getProcessTemplate();
				// if(processSet!=null&&processSet.size()>0){
				// for(ProcessTemplate process:processSet){
				// process.setDataStatus("删除");
				// totalDao.update(process);
				// }
				// }
				son.setDataStatus("删除");
				son.setProcardTemplatesb(null);
				totalDao.update(son);
				tbDownDataStatus(son);
			}
		}
		pt.setProcardsbTSet(null);
		totalDao.update(pt);
		return true;
	}
	@Override
	public List findCardTemplatesbTz(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplatesb pt = (ProcardTemplatesb) totalDao.getObjectById(
				ProcardTemplatesb.class, id);
		if (pt != null) {
			String addSql1 = "";
			if (pt.getBanci() == null || pt.getBanci() == 0) {
				addSql1 = " and (banci is null or banci =0)";
			} else {
				addSql1 = " and banci =" + pt.getBanci();
			}
			String addSql2 = "";
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' and glId=" + pt.getId();
			}
			String canChange = "no";
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准图纸删除'";
			String sc = (String) totalDao.getObjectByCondition(hql1);
			if(sc!=null&&sc.equals("是")){
				canChange = "yes";
			}else{
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
			List<ProcessTemplateFilesb> ptFileList = totalDao.query(
					"from ProcessTemplateFilesb where sbApplyId=? and processNO is null and markId=? "
							+ addSql1 + addSql2 + " order by oldfileName",pt.getSbApplyId(), pt
							.getMarkId());
			if (ptFileList != null && ptFileList.size() > 0) {
				for (ProcessTemplateFilesb pf : ptFileList) {
					pf.setCanChange(canChange);
				}
			}
			return ptFileList;
		} else {
			return null;
		}
	}
	@Override
	public String saveProcardTemplateFile(
			ProcessTemplateFilesb processTemplateFile, Integer id,
			String ytRadio, String tag) {
		// TODO Auto-generated method stub
		processTemplateFile.setStatus("默认");
		ProcardTemplatesb procard = (ProcardTemplatesb) totalDao.getObjectById(
				ProcardTemplatesb.class, id);
		processTemplateFile.setSbApplyId(procard.getSbApplyId());
		//全尺寸测量章
		if("总成".equals(procard.getProcardStyle())&& "试制".equals(procard.getProductStyle())){
			String realFilePath = "/upload/file/processTz/"
					+ Util.getDateTime("yyyy-MM");
			String path = ServletActionContext.getServletContext().getRealPath(
					realFilePath);
            String icon_fileRealPath = ServletActionContext.getServletContext().getRealPath("/upload/file/yz/icon_cl.png");
            // 生成加章文件
            Util.markPDFByIcon(icon_fileRealPath, path + "/"
                    + processTemplateFile.getFileName2(), path + "/" + processTemplateFile.getFileName2(), 1.7f, 1.14f, 2.5f, 9f);
        }
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (procard != null) {
			// if(procard.getBzStatus()!=null
			// &&procard.getBzStatus().equals("已批准")){
			// return"此零件编制状态为已批准不允许上传图纸!";
			// }
			Integer banci = procard.getBanci();
			if (banci == null) {
				banci = 0;
			}
			if (procard.getBzStatus() != null
					&& procard.getBzStatus().equals("已批准")) {
				// 直接加章
				if (processTemplateFile.getFileName2() != null) {
					processTemplateFile.setFileName(processTemplateFile
							.getFileName2());
					processTemplateFile.setFileName2(processTemplateFile
							.getFileName());
				}
			}
//			String banbenSql = null;
//			if (procard.getBanBenNumber() == null
//					|| procard.getBanBenNumber().equals("")) {
//				banbenSql = " and (banBenNumber is null or banBenNumber='')";
//			} else {
//				banbenSql = " and banBenNumber='" + procard.getBanBenNumber()
//						+ "'";
//			}
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM图纸名称唯一'";
			String tzwy = (String) totalDao.getObjectByCondition(hql1);
			String fileName = processTemplateFile.getOldfileName();
			String type = processTemplateFile.getOldfileName().substring(fileName.lastIndexOf("."), fileName.length());
			if(tzwy==null||!tzwy.equals("否")){//删除原图纸
				// 覆盖之前存在的
				String sqlhadtz = "from ProcessTemplateFilesb where sbApplyId=? and productStyle=? and markId='"
						+ procard.getMarkId()
						+ "' and type='"
						+ processTemplateFile.getType() + "'";
				if (procard.getBanci() == null || procard.getBanci() == 0) {
					sqlhadtz += " and (banci is null or banci =0)";
				} else {
					sqlhadtz += " and banci =" + procard.getBanci();
				}
				if (type.equalsIgnoreCase(".jpeg")) {
					String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
					String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
							+ fileName2 + "')";
				} else if (type.equalsIgnoreCase(".jpg")) {
					String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
					String fileName2 = fileName.replaceAll( ".jpg",".PDF");
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
							+ fileName2 + "')";
				} else if (type.equalsIgnoreCase(".PDF")) {
					fileName = fileName.replaceAll(".pdf", ".PDF");
					String fileName1 = fileName.replaceAll(".PDF", ".jpg");
					String fileName2 = fileName.replaceAll(".PDF",".jpeg" );
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
							+ fileName2 + "')";
				} else {
					sqlhadtz += " and oldfileName='" + fileName + "'";
				}
				if(procard.getProductStyle().equals("试制")){
					sqlhadtz += " and ((glId in(select id from ProcardTemplatesb where sbApplyId="+procard.getSbApplyId()+" and markId='"
						+ procard.getMarkId()
//						+ "'" +banbenSql
						+ "'"
						+" and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')) "
						+ " and processNO is null) or (processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id in "
						+ "(select id from ProcardTemplate where markId='"
						+ procard.getMarkId()
//						+ "' " +banbenSql
						+ "' "
						+"and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')))))";
//				}else{
//					sqlhadtz +=" and glId is null";
				}
				
				
				
				List<ProcessTemplateFilesb> hadFileList = totalDao.query(sqlhadtz,procard.getSbApplyId() ,procard
						.getProductStyle());
				if (hadFileList != null && hadFileList.size() > 0) {
					int logn = 0;
					for (ProcessTemplateFilesb hadfile : hadFileList) {
						if (logn == 0) {
							ProcardTemplatesbChangeLog.addchangeLog(totalDao, procard,
									hadfile, "图纸", "删除", user, Util.getDateTime());
						}
						logn++;
						totalDao.delete(hadfile);
					}
				}
			}
			
			
			// 获取所有同版本的试制领件
			List<ProcardTemplatesb> szptList = null;
			if(procard.getProductStyle().equals("试制")){
				szptList=totalDao
				.query(
						" from ProcardTemplatesb where sbApplyId=? and markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
						,procard.getSbApplyId(), procard.getMarkId(), procard
//						+ banbenSql,procard.getSbApplyId(), procard.getMarkId(), procard
						.getId());
			}
			String hql2 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='零件图纸自动下发工序'";
			String xftz = (String) totalDao.getObjectByCondition(hql2);

			// if (ytRadio != null && ytRadio.equals("yes")) {
			if (xftz != null && xftz.equals("是")
					&& !processTemplateFile.getType().equals("3D模型")) {
				Set<ProcessTemplatesb> processSet = procard.getProcessTemplatesb();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplatesb process : processSet) {
						if (process.getDataStatus() != null
								&& process.getDataStatus().equals("删除")) {
							continue;
						}
						ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
						BeanUtils.copyProperties(processTemplateFile, newfile,
								new String[] {});
						newfile.setProductStyle(procard.getProductStyle());
						newfile.setProcessName(process.getProcessName());
						newfile.setProcessNO(process.getProcessNO());
						newfile.setMarkId(procard.getMarkId());
						newfile.setBanci(banci);
						newfile.setUserName(user.getName());// 上传人姓名
						newfile.setUserCode(user.getCode());// 上传人工号
						newfile.setAddTime(nowtime);// 上传时间
						// if ("sop".equals(tag)) {
						// newfile.setGlId(process.getId());
						// }
						newfile.setSbApplyId(process.getProcardTemplatesb().getSbApplyId());
//						newfile.setProcesstFileId(processTemplateFile.getId());
						if (procard.getProductStyle().equals("试制")) {
							newfile.setGlId(process.getId());
							newfile.setProductStyle("试制");
						} else {
							newfile.setGlId(null);
							newfile.setProductStyle("批产");
						}
						totalDao.save(newfile);
					}
				}
				processTemplateFile.setProductStyle(procard.getProductStyle());
				processTemplateFile.setProcessName(procard.getProName());
				processTemplateFile.setProcessNO(null);
				processTemplateFile.setMarkId(procard.getMarkId());
				processTemplateFile.setBanci(banci);
				processTemplateFile.setUserName(user.getName());// 上传人姓名
				processTemplateFile.setUserCode(user.getCode());// 上传人工号
				processTemplateFile.setAddTime(nowtime);// 上传时间
				processTemplateFile.setSbApplyId(procard.getSbApplyId());
//				processTemplateFile.setProcesstFileId(processTemplateFile.getId());
				if (procard.getProductStyle().equals("试制")) {
					processTemplateFile.setGlId(procard.getId());
					processTemplateFile.setProductStyle("试制");
				} else {
					processTemplateFile.setGlId(null);
					processTemplateFile.setProductStyle("批产");
				}
				// if ("sop".equals(tag)) {
				// processTemplateFile.setGlId(procard.getId());
				// }
				ProcardTemplatesbChangeLog.addchangeLog(totalDao, procard,
						processTemplateFile, "图纸", "添加", user, nowtime);
				totalDao.save(processTemplateFile);
				// 同步同版本试制图纸
				if (szptList != null && szptList.size() > 0) {
					for (ProcardTemplatesb szpt : szptList) {
						Set<ProcessTemplatesb> processSet2 = szpt
								.getProcessTemplatesb();
						if (processSet2 != null && processSet2.size() > 0) {
							for (ProcessTemplatesb szprocess : processSet2) {
								if (szprocess.getDataStatus() != null
										&& szprocess.getDataStatus().equals(
												"删除")) {
									continue;
								}
								ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
								BeanUtils.copyProperties(processTemplateFile,
										newfile, new String[] {});
								newfile.setProductStyle(szpt.getProductStyle());
								newfile.setProcessName(szprocess
										.getProcessName());
								newfile.setProcessNO(szprocess.getProcessNO());
								newfile.setMarkId(szpt.getMarkId());
								newfile.setBanci(szpt.getBanci());
								newfile.setUserName(user.getName());// 上传人姓名
								newfile.setUserCode(user.getCode());// 上传人工号
								newfile.setAddTime(nowtime);// 上传时间
								newfile.setSbApplyId(szpt.getSbApplyId());
								// if ("sop".equals(tag)) {
								// newfile.setGlId(process.getId());
								// }
								if (procard.getProductStyle().equals("试制")) {
									newfile.setGlId(szprocess.getId());
									newfile.setProductStyle("试制");
								}
								totalDao.save(newfile);
							}
						}
						ProcessTemplateFilesb newfile2 = new ProcessTemplateFilesb();
						BeanUtils.copyProperties(processTemplateFile,
								newfile2, new String[] {});
						newfile2.setProductStyle(szpt.getProductStyle());
						newfile2.setProcessName(szpt.getProName());
						newfile2.setProcessNO(null);
						newfile2.setMarkId(szpt.getMarkId());
						newfile2.setBanci(szpt.getBanci());
						newfile2.setUserName(user.getName());// 上传人姓名
						newfile2.setUserCode(user.getCode());// 上传人工号
						newfile2.setAddTime(nowtime);// 上传时间
						newfile2.setSbApplyId(szpt.getSbApplyId());
						if (procard.getProductStyle().equals("试制")) {
							newfile2.setGlId(szpt.getId());
							newfile2.setProductStyle("试制");
						}
						// if ("sop".equals(tag)) {
						// processTemplateFile.setGlId(procard.getId());
						// }
						totalDao.save(newfile2);
					}
				}
//				if (procard.getProductStyle().equals("试制")) {
//					// 生成同版本批产图纸
//					ProcardTemplatesb pcpt = (ProcardTemplatesb) totalDao
//							.getObjectByCondition(
//									" from ProcardTemplatesb where sbApplyId=? and markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
//											+ banbenSql,procard.getSbApplyId(), procard.getMarkId(),
//									procard.getId());
//					if (pcpt != null) {
//						Set<ProcessTemplatesb> processSet2 = pcpt
//								.getProcessTemplatesb();
//						if (processSet2 != null && processSet2.size() > 0) {
//							for (ProcessTemplatesb pcprocess : processSet2) {
//								if (pcprocess.getDataStatus() != null
//										&& pcprocess.getDataStatus().equals(
//												"删除")) {
//									continue;
//								}
//								ProcessTemplateFilesb newfile = new ProcessTemplateFilesb();
//								BeanUtils.copyProperties(processTemplateFile,
//										newfile, new String[] {});
//								newfile.setProductStyle(pcpt.getProductStyle());
//								newfile.setProcessName(pcprocess
//										.getProcessName());
//								newfile.setProcessNO(pcprocess.getProcessNO());
//								newfile.setMarkId(procard.getMarkId());
//								newfile.setBanci(pcpt.getBanci());
//								newfile.setUserName(user.getName());// 上传人姓名
//								newfile.setUserCode(user.getCode());// 上传人工号
//								newfile.setAddTime(nowtime);// 上传时间
//								newfile.setProductStyle("批产");
//								newfile.setGlId(null);
//								totalDao.save(newfile);
//							}
//						}
//						ProcessTemplateFilesb newfile2 = new ProcessTemplateFilesb();
//						BeanUtils.copyProperties(processTemplateFile, newfile2,
//								new String[] {});
//						newfile2.setProductStyle(pcpt.getProductStyle());
//						newfile2.setProcessName(pcpt.getProName());
//						newfile2.setProcessNO(null);
//						newfile2.setMarkId(pcpt.getMarkId());
//						newfile2.setBanci(pcpt.getBanci());
//						newfile2.setUserName(user.getName());// 上传人姓名
//						newfile2.setUserCode(user.getCode());// 上传人工号
//						newfile2.setAddTime(nowtime);// 上传时间
//						newfile2.setGlId(null);
//						newfile2.setProductStyle("批产");
//						// if ("sop".equals(tag)) {
//						// processTemplateFile.setGlId(procard.getId());
//						// }
//						totalDao.save(newfile2);
//					}
//				}

				return "true";
				// } else {
				// return "该零件下没有工序，不能上传原图!";
			} else {
				processTemplateFile.setProductStyle(procard.getProductStyle());
				processTemplateFile.setUserName(user.getName());// 上传人姓名
				processTemplateFile.setUserCode(user.getCode());// 上传人工号
				processTemplateFile.setAddTime(nowtime);// 上传时间
				processTemplateFile.setProcessName(procard.getProName());
				processTemplateFile.setProcessNO(null);
				processTemplateFile.setMarkId(procard.getMarkId());
				processTemplateFile.setBanci(banci);
				if (procard.getProductStyle().equals("试制")) {
					processTemplateFile.setGlId(procard.getId());
					processTemplateFile.setProductStyle("试制");
				} else {
					processTemplateFile.setGlId(null);
					processTemplateFile.setProductStyle("批产");
				}
				ProcardTemplatesbChangeLog.addchangeLog(totalDao, procard,
						processTemplateFile, "图纸", "添加", user, nowtime);
				totalDao.save(processTemplateFile);
				// 同步同版本试制图纸
				if (szptList != null && szptList.size() > 0) {
					for (ProcardTemplatesb szpt : szptList) {
						ProcessTemplateFilesb newfile2 = new ProcessTemplateFilesb();
						BeanUtils.copyProperties(processTemplateFile, newfile2,
								new String[] {});
						newfile2.setProductStyle(szpt.getProductStyle());
						newfile2.setProcessName(szpt.getProName());
						newfile2.setProcessNO(null);
						newfile2.setMarkId(szpt.getMarkId());
						newfile2.setBanci(szpt.getBanci());
						newfile2.setUserName(user.getName());// 上传人姓名
						newfile2.setUserCode(user.getCode());// 上传人工号
						newfile2.setAddTime(nowtime);// 上传时间
						newfile2.setGlId(szpt.getId());
						newfile2.setProductStyle("试制");
						newfile2.setSbApplyId(szpt.getSbApplyId());
						// if ("sop".equals(tag)) {
						// processTemplateFile.setGlId(procard.getId());
						// }
						totalDao.save(newfile2);
					}
				}
//				if (procard.getProductStyle().equals("试制")) {
//					// 生成同版本批产图纸
//					ProcardTemplatesb pcpt = (ProcardTemplatesb) totalDao
//							.getObjectByCondition(
//									" from ProcardTemplatesb where sbApplyId=? and markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
//											+ banbenSql,procard.getSbApplyId(), procard.getMarkId(),
//									procard.getId());
//					if (pcpt != null) {
//						ProcessTemplateFile newfile2 = new ProcessTemplateFile();
//						BeanUtils.copyProperties(processTemplateFile, newfile2,
//								new String[] {});
//						newfile2.setProductStyle(pcpt.getProductStyle());
//						newfile2.setProcessName(pcpt.getProName());
//						newfile2.setProcessNO(null);
//						newfile2.setMarkId(pcpt.getMarkId());
//						newfile2.setBanci(pcpt.getBanci());
//						newfile2.setUserName(user.getName());// 上传人姓名
//						newfile2.setUserCode(user.getCode());// 上传人工号
//						newfile2.setAddTime(nowtime);// 上传时间
//						newfile2.setGlId(null);
//						newfile2.setProductStyle("批产");
//						totalDao.save(newfile2);
//					}
//				}
				return "true";
			}
		}
		return "没有找到目标零件!";
	}
	@Override
	public String addProcessTemplate(ProcessTemplatesb processTemplatesb,
			Integer id) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplatesb thispt= (ProcardTemplatesb) totalDao.getObjectById(ProcardTemplatesb.class, id);
		if(thispt.getProductStyle().equals("批产")){
			if (thispt.getBzStatus() != null
					&& thispt.getBzStatus().equals("已批准")) {
				return "已批准状态不能添加工序!";
			}
		}
		String addTime = Util.getDateTime();
		if (processTemplatesb != null) {
			ProcessGzstore pg = (ProcessGzstore) totalDao.getObjectByCondition(
					" from ProcessGzstore where processName = ?",
					processTemplatesb.getProcessName());
			if (pg == null) {
				return "对不起,此工序不在工序库中存在!";
			}
			if (pg.getIsSpecial() != null && pg.getIsSpecial().equals("特殊")) {
				processTemplatesb.setIsSpecial("特殊");
			} else {
				processTemplatesb.setIsSpecial("普通");
			}
			Float old = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplatesb where processNO=? and processName=? and procardTemplatesb.id=?",
							processTemplatesb.getProcessNO(), processTemplatesb
									.getProcessName(), id);
			if (old != null && old > 0) {// 同工序号同工序名称
				return "此工序已存在,请勿重复添加!";
			}

			Float hasCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplatesb where processNO=? and procardTemplatesb.id=?",
							processTemplatesb.getProcessNO(), id);
			if (hasCount != null && hasCount > 0) {// 顺延之后工序
				List<ProcessTemplatesb> laterprocesslist = totalDao
						.query(
								"from ProcessTemplatesb where procardTemplatesb.sbApplyId=? and processNO>=? and procardTemplatesb.markId=? and (procardTemplatesb.banbenStatus is null or procardTemplatesb.banbenStatus !='历史')"
										+ "and (procardTemplatesb.banbenStatus is null or procardTemplatesb.banbenStatus !='历史') order by processNO",
										thispt.getSbApplyId(), processTemplatesb.getProcessNO(),thispt.getMarkId());
				Integer nowNO = processTemplatesb.getProcessNO();
				List<ProcessTemplatesb> syprocesslist = new ArrayList<ProcessTemplatesb>();
				for (ProcessTemplatesb laterprocess : laterprocesslist) {
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
						List<ProcessTemplateFilesb> fileList = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and (status is null or status!='历史' ) "
												+ "and processNO is not null and processNO=? and markId=?",
												 thispt.getSbApplyId() ,nowNO,thispt.getMarkId());
						if (fileList != null && fileList.size() > 0) {
							for (ProcessTemplateFilesb file : fileList) {
								file.setStatus("历史");
								totalDao.update(file);
							}
						}
					}
					ProcessTemplatesb syprocess = syprocesslist.get(i);
					if (nowNO > syprocess.getProcessNO()) {
						nowNO--;
						List<ProcessTemplateFilesb> fileList = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and (status is null or status!='历史' ) "
												+ "and processNO is not null and processNO=? and markId=?",
												 thispt.getSbApplyId() ,syprocess.getProcessNO(), thispt.getMarkId());
						if (fileList != null && fileList.size() > 0) {
							for (ProcessTemplateFilesb file : fileList) {
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
			String banbenSql=null;
			String banben =thispt.getBanBenNumber();
			if(banben==null
					||banben.length()==0){
				banbenSql =" and (banBenNumber is null or banBenNumber='')";
			}else{
				banbenSql =" and banBenNumber='"+banben+"'";
				
			}
			boolean b = true;
			// 获取所有与要添加工序的零件同件号的零件
			List<ProcardTemplatesb> ptList = (List<ProcardTemplatesb>) totalDao
					.query(
							"from ProcardTemplatesb where sbApplyId=? and markId=? and id!=? and  productStyle=?" +
							"and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"+banbenSql,
							thispt.getSbApplyId(),thispt.getMarkId(),thispt.getId(),thispt.getProductStyle());
			ptList.add(thispt);
			List<ProcessTemplatesb> processList = new ArrayList<ProcessTemplatesb>();
			int tag=0;
			for (int i = 0; i < ptList.size(); i++) {
				ProcardTemplatesb pt = ptList.get(i);
				Float ajp = (Float) totalDao
						.getObjectByCondition(
								"select sum(allJiepai) from ProcessTemplatesb where procardTemplatesb.id=?",
								pt.getId());
				if (ajp == null) {
					pt.setAllJiepai(processTemplatesb.getAllJiepai());
				} else {
					pt.setAllJiepai(processTemplatesb.getAllJiepai() + ajp);
				}
				totalDao.update(pt);
				ProcessTemplatesb process = new ProcessTemplatesb();
				BeanUtils.copyProperties(processTemplatesb, process,
						new String[] { "id", "procardTemplatesb", "taSopGongwei",
								"processFuLiaoTemplate", "fuliaoList",
								"processTemplateFilesb" });
				process.setAddTime(addTime);
				process.setAddUser(user.getName());
				process.setProcardTemplatesb(pt);
				processList.add(process);
				if (tag == 0&&pt.getProductStyle().equals("批产")) {// 新添加的工序添加共用工艺文件
					List<ProcessTemplateFilesb> files = null;
					String banciSql = "";
					if(pt.getBanci()==null||pt.getBanci()==0){
						banciSql =" and (banci is null or banci=0)";
					}else{
						banciSql =" and banci="+pt.getBanci();
					}
					Float hasgxCunt = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcessTemplatesb where procardTemplatesb.markId=? and (dataStatus is null and dataStatus !='删除') and (procardTemplatesb.banbenStatus is null or procardTemplatesb.banbenStatus !='历史') and (procardTemplatesb.dataStatus is null or procardTemplatesb.dataStatus!='删除')",
									pt.getMarkId());
					if (hasgxCunt == null || hasgxCunt == 0) {// 无工序
						// 添加图纸
						files = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and markId=? and processNO is null and type in('3D文件','工艺规范') and (status is null or status!='历史') "+banciSql,
										 thispt.getSbApplyId() ,pt.getMarkId());
					} else {
						files = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and markId=? and processNO is null and type in('3D文件','工艺规范') and (status is null or status!='历史') "+banciSql+" and oldfileName in"
												+ "(select oldfileName from ProcessTemplateFilesb where markId=? and (status is null or status!='历史') "+banciSql+" group by oldfileName having count(*)>1)",
												thispt.getSbApplyId() ,pt.getMarkId(), pt.getMarkId());
					}
					if (files != null && files.size() > 0) {
						List<String> oldFilenames = new ArrayList<String>();
						for (ProcessTemplateFilesb file : files) {
							if (!oldFilenames.contains(file.getOldfileName())) {
								ProcessTemplateFilesb newFile = new ProcessTemplateFilesb();
								BeanUtils.copyProperties(file, newFile,
										new String[] { "id", "processNO",
												"processName" });
								newFile.setProcessNO(processTemplatesb
										.getProcessNO());
								newFile.setProcessName(processTemplatesb
										.getProcessName());
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
			for (ProcessTemplatesb process : processList) {
				b = b & totalDao.save(process);
				ProcardTemplatesb pt = process.getProcardTemplatesb();
				if(pt.getProductStyle().equals("试制")){// 新添加的工序添加共用工艺文件
					List<ProcessTemplateFilesb> files = null;
					String banciSql = "";
					if(pt.getBanci()==null||pt.getBanci()==0){
						banciSql =" and (banci is null or banci=0)";
					}else{
						banciSql =" and banci="+pt.getBanci();
					}
						// 添加图纸
					files = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and glId=? and processNO is null  and type in('3D文件','工艺规范') and (status is null or status!='历史') "+banciSql,
										thispt.getSbApplyId() ,pt.getId());
					if (files != null && files.size() > 0) {
						List<String> oldFilenames = new ArrayList<String>();
						for (ProcessTemplateFilesb file : files) {
							if (!oldFilenames.contains(file.getOldfileName())) {
								ProcessTemplateFilesb newFile = new ProcessTemplateFilesb();
								BeanUtils.copyProperties(file, newFile,
										new String[] { "id", "processNO",
												"processName" });
								newFile.setProcessNO(processTemplatesb
										.getProcessNO());
								newFile.setProcessName(processTemplatesb
										.getProcessName());
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
						ProcardTemplatesbChangeLog.addchangeLog(totalDao, process
								.getProcardTemplatesb(), process, "工序", "增加",
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
	@Override
	public String updateProcessT(ProcessTemplatesb processTemplate) {
		Users user = Util.getLoginUser();
		String nowtime = Util.getDateTime();
		if(user==null){
			return "请先登录!";
		}
		if (processTemplate != null) {
			ProcessTemplatesb old = (ProcessTemplatesb) totalDao.getObjectById(ProcessTemplatesb.class, processTemplate.getId());
			if(old==null){
				return "没有找到目标工序!";
			}
			ProcardTemplatesb ptsb= old.getProcardTemplatesb();
			
			ProcessTemplatesb had = (ProcessTemplatesb) totalDao
					.getObjectByCondition(
							"from ProcessTemplatesb where id !=? and processNO=? and procardTemplatesb.id=?" +
							" and (dataStatus is null or dataStatus!='删除')",
							processTemplate.getId(), processTemplate.getProcessNO(), ptsb.getId());
			if (had != null) {
				return "该工序号已存在，请重新填写!";
			}
			if (processTemplate.getOpcaozuojiepai() == null) {
				processTemplate.setOpcaozuojiepai(3f);
			}
			if (processTemplate.getOpshebeijiepai() == null) {
				processTemplate.setOpshebeijiepai(1f);
			}
			if (processTemplate.getGzzhunbeijiepai() == null) {
				processTemplate.setGzzhunbeijiepai(1f);
			}
			if (processTemplate.getGzzhunbeicishu() == null) {
				processTemplate.setGzzhunbeicishu(1f);
			}
			processTemplate.setAllJiepai(processTemplate.getOpcaozuojiepai()
					+ processTemplate.getOpshebeijiepai()
					+ processTemplate.getGzzhunbeijiepai()
					* processTemplate.getGzzhunbeicishu());
			boolean b = true;
			ProcardTemplatesbChangeLog.addchangeLog(totalDao, old,
					processTemplate, user, nowtime);
			List<ProcessTemplatesb> porcessinOtherCardList = totalDao
					.query(
							"from ProcessTemplatesb where id !=? and processNO=? and procardTemplatesb.id " +
							"in(select id from ProcardTemplatesb where sbApplyId=? and markId=? " +
									"and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除'))",
									old.getId(), old.getProcessNO(),ptsb.getSbApplyId(),
									ptsb.getMarkId());
			// 对应模板
			processTemplate.setProcardTemplatesb(ptsb);
			// 并行处理
			String newProcessStatus = processTemplate.getProcessStatus();
			String oldProcessStatus = old.getProcessStatus();
			// 判断并行状态是否存在变化
			if ("yes".equals(newProcessStatus)) {
				// 查询上一道工序
				String hql = "from ProcessTemplatesb  where procardTemplatesb.id=? and processNO<? order by processNO desc";
				ProcessTemplatesb pt = (ProcessTemplatesb) totalDao
				.getObjectByCondition(hql, ptsb.getId(), processTemplate
						.getProcessNO());
				if (pt != null) {
					// 上一道工序已经并行,延续上一道工序的并行id
					if (newProcessStatus.equals(pt.getProcessStatus())) {
						processTemplate.setParallelId(pt.getParallelId());
					} else {
						processTemplate.setParallelId(old.getId());
					}
				} else {
					// 说明是第一道工序(并行从自身id开始)
					processTemplate.setParallelId(old.getId());
				}
			} else {
				processTemplate.setParallelId(null);
			}
			boolean changetz =false;
			String banciSql =null;
			if(!Util.isEquals(processTemplate.getProcessNO(), old.getProcessNO())
					||!Util.isEquals(processTemplate.getProcessName(), old.getProcessName())){
				changetz = true;
				if(ptsb.getBanci()==null||ptsb.getBanci()==0){
					banciSql =" and (banci is null or banci = 0)";
				}else{
					banciSql =" and banci="+ptsb.getBanci();
				}
				//设变BOM中都是试制或者批产，所以不用特意查询只需要把这个BOM中的有效同工序文件都进行修改就可以了
				List<ProcessTemplateFilesb> filesbList = totalDao.query(" from ProcessTemplateFilesb where sbApplyId=? and markId=? and processNO=?"+banciSql, 
						ptsb.getSbApplyId(),ptsb.getMarkId(),old.getProcessNO());
				if(filesbList!=null&&filesbList.size()>0){
					for(ProcessTemplateFilesb filesb:filesbList){
						filesb.setProcessNO(processTemplate.getProcessNO());
						filesb.setProcessName(processTemplate.getProcessName());
						totalDao.update(filesb);
					}
				}
			}
			BeanUtils.copyProperties(processTemplate, old,
					new String[] { "procardTemplate", "taSopGongwei",
					"addUser","addTime","epId","isSpecial" });
			b = b & totalDao.update(old);
			AttendanceTowServerImpl.updateJilu(5, old,
						old.getId(), old.getProcessNO()
						+ "工序" + old.getProcessName());
			// 同步修改其他件号下的该工序信息
			if (porcessinOtherCardList != null
					&& porcessinOtherCardList.size() > 0) {
				for (ProcessTemplatesb other : porcessinOtherCardList) {
					other.setProcessNO(processTemplate.getProcessNO());
					other.setProcessName(processTemplate.getProcessName());
					other.setProductStyle(processTemplate.getProductStyle());
					other.setZjStatus(processTemplate.getZjStatus());
					other.setProcessStatus(processTemplate.getProcessStatus());
					other.setIsPrice(processTemplate.getIsPrice());
					other.setKaoqingstatus(processTemplate.getKaoqingstatus());
					other.setGuifanstatus(processTemplate.getGuifanstatus());
					other.setIsJisuan(processTemplate.getIsJisuan());
					other.setGongzhuangstatus(processTemplate
							.getGongzhuangstatus());
					other.setGzstoreId(processTemplate.getGzstoreId());
					other.setNumber(processTemplate.getNumber());
					other.setMatetag(processTemplate.getMatetag());
					other.setLiangjustatus(processTemplate.getLiangjustatus());
					other.setMeasuringId(processTemplate.getMeasuringId());
					other.setMeasuringNumber(processTemplate.getMeasuringNumber());
					other.setMeasuringMatetag(processTemplate
							.getMeasuringMatetag());
					other.setMeasuring_no(processTemplate.getMeasuring_no());
					other.setShebeistatus(processTemplate.getShebeistatus());
					other.setShebeiId(processTemplate.getShebeiId());
					other.setShebeiNo(processTemplate.getShebeiNo());
					other.setShebeiName(processTemplate.getShebeiName());
					other.setGongwei(processTemplate.getGongwei());
					totalDao.update(other);
					// AttendanceTowServerImpl.updateJilu(5,other,
					// other.getId(), other.getProcessNO()+ "工序" +
					// oldProcessT.getProcessName() + "(同步修改)");
				}
			}
			// 同步修改所有同件号的alljiepai
			List<ProcardTemplatesb> ptList = (List<ProcardTemplatesb>) totalDao
			.query(
					"from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
					ptsb.getId(),ptsb.getMarkId());
			for (int i = 0; i < ptList.size(); i++) {
				ProcardTemplatesb pt = ptList.get(i);
				Float ajp = (Float) totalDao
				.getObjectByCondition(
						"select sum(allJiepai) from ProcessTemplatesb where procardTemplatesb.id=?",
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
			return "数据异常!";
		}
	}
	@Override
	public ProcessTemplatesb findProcessT(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplatesb) totalDao.getObjectById(ProcessTemplatesb.class, id);
	}
	/***
	 * 删除工序信息
	 */
	public String delProcessT(ProcessTemplatesb processT) {
		Users user = Util.getLoginUser();
		String nowtime = Util.getDateTime();
		ProcardTemplatesb procard = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where id=(select procardTemplatesb.id from ProcessTemplatesb where id=?)", processT.getId());
		if(procard.getProductStyle().equals("批产")){
			if (procard.getBzStatus() != null
					&& procard.getBzStatus().equals("已批准")) {
				return "已批准状态不能删除工序!";
			}
		}
		// 同步修改所有同件号的alljiepai
		List<ProcardTemplatesb> ptList = (List<ProcardTemplatesb>) totalDao
				.query(
						"from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
						procard.getSbApplyId(),procard.getMarkId());
		
		// 查询所有于此工序的零件同件号的零件下的与此工序同工序号和工序名的工序
		String banbenSql=null;
		if(procard.getBanBenNumber()==null
				||procard.getBanBenNumber().length()==0){
			banbenSql =" and (banBenNumber is null or banBenNumber='')";
		}else{
			banbenSql =" and banBenNumber='"+procard.getBanBenNumber()+"'";
		}
		List<ProcessTemplatesb> processList = totalDao
				.query(
						"from ProcessTemplatesb where processNO=? and processName=? and (dataStatus is null or dataStatus!='删除')"
								+ " and procardTemplatesb.sbApplyId=? and procardTemplatesb.id in (select id from ProcardTemplatesb where "
								+ "markId=? "+banbenSql
								+ "and productStyle=? and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除'))",
						processT.getProcessNO(), processT.getProcessName(),procard.getSbApplyId(),
						procard.getMarkId(),procard.getProductStyle());
		int n = 0;
		for (ProcessTemplatesb process : processList) {
			if (n == 0) {
				ProcardTemplatesbChangeLog.addchangeLog(totalDao, process
						.getProcardTemplatesb(), process, "工序", "删除", user,
						nowtime);
			}
			process.setDataStatus("删除");
			process.setOldPtId(process.getProcardTemplatesb().getId());
			process.setOldbanci(process.getProcardTemplatesb().getBanci());
			process.setProcardTemplatesb(null);
			process.setDeleteTime(Util.getDateTime());
			totalDao.update(process);
			n++;
		}

		for (int i = 0; i < ptList.size(); i++) {
			ProcardTemplatesb pt = ptList.get(i);
			Float ajp = (Float) totalDao
					.getObjectByCondition(
							"select sum(allJiepai) from ProcessTemplatesb where procardTemplatesb.id=?",
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
	@Override
	public Object[] findCardTemForShow(Integer id) {
		if ((Object) id != null && id > 0) {
			ProcardTemplatesb pc = (ProcardTemplatesb) totalDao.getObjectById(
					ProcardTemplatesb.class, id);
			if (pc != null) {
				// 下层流水卡片模板
				Set<ProcardTemplatesb> pcSet = pc.getProcardsbTSet();
				List<ProcardTemplatesb> pclist = new ArrayList<ProcardTemplatesb>();
				pclist.addAll(pcSet);
				// 对应工序信息
				Set<ProcessTemplatesb> pceSet = pc.getProcessTemplatesb();
				List<ProcessTemplatesb> pcelist = new ArrayList<ProcessTemplatesb>();
				pcelist.addAll(pceSet);
				String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
				String valueCode = (String) totalDao.getObjectByCondition(hql1);
				Integer banci = pc.getBanci();
				if (banci == null) {
					banci = 0;
				}
				String sbmsg = null;
				if (banci > 1) {
					banci--;
					sbmsg = (String) totalDao
							.getObjectByCondition(
									"select remark from ProcardTemplateBanBen where banci=? and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
									banci, pc.getMarkId());
				} else if (banci == 1) {
					sbmsg = (String) totalDao
							.getObjectByCondition(
									"select remark from ProcardTemplateBanBen where (banci is null or banci=0) and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
									pc.getMarkId());
				}
				return new Object[] { pc, pclist, pcelist, valueCode, sbmsg };
			}
		}
		return null;
	}
	@Override
	public String updateProcardTemplate2(ProcardTemplatesb procardTemplatesb) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		ProcardTemplatesb oldProcardTem = getProcardsbTemById(procardTemplatesb
				.getId());
		if (oldProcardTem == null) {
			return "没有找到目标零件!";
		}
		if (checkHasSonMarkId(oldProcardTem.getFatherId(), oldProcardTem.getId(),
				procardTemplatesb.getMarkId())) {
			return "父卡下已有此件号,修改失败!";
		}
		if (oldProcardTem.getBzStatus().equals("已批准")) {
			return "此零件编制状态为已批准,不允许修改";
		}
		if (oldProcardTem.getBzStatus().equals("设变发起中")) {
			return "此零件编制状态为设变发起中,不允许修改";
		}
		ProcardTemplatesbChangeLog.addchangeLog(totalDao, oldProcardTem,
				procardTemplatesb, user, nowtime);
		oldProcardTem.setOldProcardStyle(oldProcardTem.getProcardStyle());
		String msg = tbAndupdate(procardTemplatesb, oldProcardTem);
		return msg;
	}
	/**
	 * 0不同步，1同步同父类下的同件号，2同步所有同件号
	 */
	public String tbAndupdate(ProcardTemplatesb procardTemplate,
			ProcardTemplatesb oldProcardTem) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		int changeLv = 0;
		boolean changewgj = false;
		boolean changewgj2 = false;
		// 名称
		if (!Util.isEquals(oldProcardTem.getProName(), procardTemplate.getProName())) {
			oldProcardTem.setProName(procardTemplate.getProName());
			changewgj = true;
			changeLv = 2;
		}
		// 供料属性
		if (!Util.isEquals(oldProcardTem.getKgliao(), procardTemplate.getKgliao())) {
			changewgj2 = true;
			oldProcardTem.setKgliao(procardTemplate.getKgliao());
			changeLv = 1;
		}
		// 物料类别
		if (!Util.isEquals(oldProcardTem.getWgType(), procardTemplate.getWgType())) {
			changewgj = true;
			oldProcardTem.setWgType(procardTemplate.getWgType());
			changeLv = 2;
		}
		// 单位
		if (!Util.isEquals(oldProcardTem.getUnit(), procardTemplate.getUnit())) {
			changewgj = true;
			oldProcardTem.setUnit(procardTemplate.getUnit());
			changeLv = 2;
		}
		// 比例
		if (!Util.isEquals(oldProcardTem.getBili(), procardTemplate.getBili())) {
			changewgj = true;
			oldProcardTem.setBili(procardTemplate.getBili());
			changeLv = 2;
		}
		// 图号
		if (!Util.isEquals(oldProcardTem.getTuhao(), procardTemplate.getTuhao())) {
			changewgj = true;
			oldProcardTem.setTuhao(procardTemplate.getTuhao());
			changeLv = 2;
		}
		// 重要性
		if (!Util.isEquals(oldProcardTem.getImportance(), procardTemplate
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
		if (!Util.isEquals(oldProcardTem.getSpecification(), procardTemplate
				.getSpecification())) {
			changewgj = true;
			oldProcardTem.setSpecification(procardTemplate.getSpecification());
			changeLv = 2;
		}
		if (oldProcardTem.getProcardStyle() != null
				&& oldProcardTem.getProcardStyle().equals("外购") && changewgj) {// 外购件库中需要修改对应的值
			if(changewgj){
				// 修改外购件
				List<YuanclAndWaigj> wgjList = totalDao
				.query(
						"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and markId=?",
						oldProcardTem.getMarkId());
				if (wgjList != null && wgjList.size() > 0) {
					for (YuanclAndWaigj wgj : wgjList) {
//						if ("已确认".equals(wgj.getStatus())) {
//							throw new RuntimeException("该件号在外购件库中已确认,不允许修改!");
//						}
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
						wgj.setSpecification(procardTemplate.getSpecification());
						totalDao.update(wgj);
					}
				} else {
					throw new RuntimeException("该件号不在外购件库中数据异常请前往外购件库中添加!");
				}
			}
			if(changewgj2&&procardTemplate.getProcardStyle().equals("外购")
					&&procardTemplate.getKgliao()!=null&&
					(procardTemplate.getKgliao().equals("TK")
							||procardTemplate.getKgliao().equals("TK AVL")
							||procardTemplate.getKgliao().equals("TK Price")
							||procardTemplate.getKgliao().equals("CS"))){//供料属性修改
				String banbenSql = null;
				if(oldProcardTem.getBanBenNumber()==null || oldProcardTem.getBanBenNumber().length()==0){
					banbenSql = " and (banbenhao is null or banbenhao='')";
				}else{
					banbenSql = " and banbenhao ='"+oldProcardTem.getBanBenNumber()+"'";
				}
				List<YuanclAndWaigj> wgjList = totalDao
				.query(
						"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and markId=? and　kgliao=? " +banbenSql,
						oldProcardTem.getMarkId(),procardTemplate.getKgliao());
				if (wgjList == null || wgjList.size()== 0) {//没有外购件库中没有此供料属性
					List<YuanclAndWaigj> wgjList2 = totalDao
					.query(
							"from YuanclAndWaigj where (banbenStatus is null or banbenStatus !='历史') and status='禁用' and markId=? and　kgliao=? " +banbenSql,
							oldProcardTem.getMarkId(),procardTemplate.getKgliao());
					if(wgjList2!=null&&wgjList2.size()>0){
						throw new RuntimeException("该外购件已被禁用!");
					}
				}else{//生成外购数据
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
		String oldProcardStyle= oldProcardTem.getProcardStyle();
		// 零件类型
		if (!Util.isEquals(oldProcardTem.getProcardStyle(), procardTemplate
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
		if (!Util.isEquals(oldProcardTem.getCarStyle(), procardTemplate
				.getCarStyle())) {
			oldProcardTem.setCarStyle(procardTemplate.getCarStyle());
			changeLv = 2;
		}
		// 安全库存
		if (!Util.isEquals(oldProcardTem.getSafeCount(), procardTemplate
				.getSafeCount())) {
			oldProcardTem.setSafeCount(procardTemplate.getSafeCount());
			changeLv = 2;
		}
		// 最低库存
		if (!Util.isEquals(oldProcardTem.getLastCount(), procardTemplate
				.getLastCount())) {
			oldProcardTem.setLastCount(procardTemplate.getLastCount());
			changeLv = 2;
		}
		// 版本号(不允许直接修改)
		// if (!isEquals(oldProcardTem.getBanBenNumber(), procardTemplate
		// .getBanBenNumber())) {
		// return 2;
		// }

		// 材料类别
		if (!Util.isEquals(oldProcardTem.getClType(), procardTemplate.getClType())) {
			oldProcardTem.setClType(procardTemplate.getClType());
			changeLv = 2;
		}
		// sameCard.setCaizhi(procardTem.getCaizhi());
		// 领料类型
		if (!Util.isEquals(oldProcardTem.getLingliaoType(), procardTemplate
				.getLingliaoType())) {
			oldProcardTem.setLingliaoType(procardTemplate.getLingliaoType());
			changeLv = 2;
		}
		// 页数
		if (!Util.isEquals(oldProcardTem.getPageTotal(), procardTemplate
				.getPageTotal())) {
			oldProcardTem.setPageTotal(procardTemplate.getPageTotal());
			changeLv = 2;
		}
		// 工艺编号
		if (!Util.isEquals(oldProcardTem.getNumb(), procardTemplate.getNumb())) {
			oldProcardTem.setNumb(procardTemplate.getNumb());
			changeLv = 2;
		}
		// 发出日期
		if (!Util.isEquals(oldProcardTem.getFachuDate(), procardTemplate
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
				if (!Util.isEquals(oldProcardTem.getPizhunId(), procardTemplate
						.getPizhunId())) {
					oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
					if(procardTemplate.getPizhunId()!=null){
						Users u = (Users) totalDao.getObjectById(Users.class, procardTemplate.getPizhunId());
						if(u!=null){
							oldProcardTem.setPizhunName(u.getName());
						}
					}
					changeLv = 2;
				}
			} else {
				// 校对人
				if (!Util.isEquals(oldProcardTem.getJiaoduiId(), procardTemplate
						.getJiaoduiId())) {
					oldProcardTem.setJiaoduiId(procardTemplate.getJiaoduiId());
					if(procardTemplate.getJiaoduiId()!=null){
						Users u = (Users) totalDao.getObjectById(Users.class, procardTemplate.getJiaoduiId());
						if(u!=null){
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
				if (!Util.isEquals(oldProcardTem.getShenheId(), procardTemplate
						.getShenheId())) {
					oldProcardTem.setShenheId(procardTemplate.getShenheId());
					if(procardTemplate.getShenheId()!=null){
						Users u = (Users) totalDao.getObjectById(Users.class, procardTemplate.getShenheId());
						if(u!=null){
							oldProcardTem.setShenheName(u.getName());
						}
					}
					changeLv = 2;
				}
			} else {
				// 批准人
				if (!Util.isEquals(oldProcardTem.getPizhunId(), procardTemplate
						.getPizhunId())) {
					oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
					if(procardTemplate.getPizhunId()!=null){
						Users u = (Users) totalDao.getObjectById(Users.class, procardTemplate.getPizhunId());
						if(u!=null){
							oldProcardTem.setPizhunName(u.getName());
						}
					}
					changeLv = 2;
				}
			}
		}
		if (oldProcardTem.getBzStatus().equals("已校对")) {
			// 批准人
			if (!Util.isEquals(oldProcardTem.getPizhunId(), procardTemplate
					.getPizhunId())) {
				oldProcardTem.setPizhunId(procardTemplate.getPizhunId());
				if(procardTemplate.getPizhunId()!=null){
					Users u = (Users) totalDao.getObjectById(Users.class, procardTemplate.getPizhunId());
					if(u!=null){
						oldProcardTem.setPizhunName(u.getName());
					}
				}
				changeLv = 2;
			}
		}

		// 初始总成
		if (!Util.isEquals(oldProcardTem.getLoadMarkId(), procardTemplate
				.getLoadMarkId())) {
			oldProcardTem.setLoadMarkId(procardTemplate.getLoadMarkId());
		}
		// 领料状态
		if (!Util.isEquals(oldProcardTem.getLingliaostatus(), procardTemplate
				.getLingliaostatus())) {
			oldProcardTem
					.setLingliaostatus(procardTemplate.getLingliaostatus());
			if (changeLv == 0) {
				changeLv = 1;
			}
		}
		// 权值
		if (procardTemplate.getProcardStyle().equals("外购")) {
			if (!Util.isEquals(oldProcardTem.getQuanzi1(), procardTemplate
					.getQuanzi1())) {
				oldProcardTem.setQuanzi1(procardTemplate.getQuanzi1());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
			if (!Util.isEquals(oldProcardTem.getQuanzi2(), procardTemplate
					.getQuanzi2())) {
				oldProcardTem.setQuanzi2(procardTemplate.getQuanzi2());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
			// 半成品
			if (!Util.isEquals(oldProcardTem.getNeedProcess(), procardTemplate
					.getNeedProcess())) {
				oldProcardTem.setNeedProcess(procardTemplate.getNeedProcess());
				changeLv = 2;
			}
		} else {
			if (!Util.isEquals(oldProcardTem.getCorrCount(), procardTemplate
					.getCorrCount())) {
				oldProcardTem.setCorrCount(procardTemplate.getCorrCount());
				if (changeLv == 0) {
					changeLv = 1;
				}
			}
		}

		// 自制件激活
		if (!Util.isEquals(oldProcardTem.getZzjihuo(), procardTemplate.getZzjihuo())) {
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
				List<ProcardTemplatesb> plist = totalDao.query(
						"from ProcardTemplatesb where rootId=? and rootId!=id",
						oldProcardTem.getId());
				if (plist.size() > 0) {
					for (ProcardTemplatesb pson : plist) {
						pson.setProcardTemplatesb(null);
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
				Set<ProcardTemplatesb> sonpt = oldProcardTem.getProcardsbTSet();
				for (ProcardTemplatesb p : sonpt) {
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
		if (!Util.isEquals(oldProcardTem.getBiaochu(), procardTemplate.getBiaochu())) {
			oldProcardTem.setBiaochu(procardTemplate.getBiaochu());
			changeLv = 2;
		}
		// 长
		if (!Util.isEquals(oldProcardTem.getThisLength(), procardTemplate
				.getThisLength())) {
			oldProcardTem.setThisLength(procardTemplate.getThisLength());
			changeLv = 2;
		}
		// 宽
		if (!Util.isEquals(oldProcardTem.getThisWidth(), procardTemplate
				.getThisWidth())) {
			oldProcardTem.setThisWidth(procardTemplate.getThisWidth());
			changeLv = 2;
		}
		// 厚
		if (!Util.isEquals(oldProcardTem.getThisHight(), procardTemplate
				.getThisHight())) {
			oldProcardTem.setThisHight(procardTemplate.getThisHight());
			changeLv = 2;
		}

		bool = totalDao.update(oldProcardTem);
//		AttendanceTowServerImpl.updateJilu(3, oldProcardTem, oldProcardTem
//				.getId(), oldProcardTem.getMarkId());
		String fMarkId = null;
		if (oldProcardTem.getProcardTemplatesb() != null) {
			fMarkId = (String) totalDao.getObjectByCondition(
					"select markId from ProcardTemplatesb where id=?",
					oldProcardTem.getFatherId());
		}

		List<ProcardTemplatesb> sameMarkIdList = null;
		if (bool & changeLv == 2) {// 二级同步修改所有的同件号
			sameMarkIdList = totalDao
					.query(
							"from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
							oldProcardTem.getSbApplyId(),oldProcardTem.getMarkId());
		} else if (bool & changeLv == 1) {// 一级同步修改同父零件下的同件号
			if (fMarkId != null && fMarkId.length() > 0) {
				sameMarkIdList = totalDao
						.query(
								"from ProcardTemplatesb where sbApplyId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and procardTemplatesb.markId=? and (dataStatus is null or dataStatus!='删除')",
								oldProcardTem.getSbApplyId(),oldProcardTem.getMarkId(), fMarkId);
			}
		}
		if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
			for (ProcardTemplatesb sameCard : sameMarkIdList) {
				if ( !"总成".equals(oldProcardTem.getProcardStyle())
						&& sameCard.getBelongLayer() != null
						&& sameCard.getBelongLayer() != 1) {
					if("待定".equals(sameCard.getProcardStyle())||Util.isEquals(sameCard.getProcardStyle(), oldProcardTem.getProcardStyle()) ){
						sameCard.setProcardStyle(oldProcardTem.getProcardStyle());
						sameCard.setWgType(oldProcardTem.getWgType());
					}
				}
				sameCard.setProName(oldProcardTem.getProName());
				sameCard.setUnit(oldProcardTem.getUnit());
				sameCard.setCarStyle(oldProcardTem.getCarStyle());
				sameCard.setSafeCount(oldProcardTem.getSafeCount());
				sameCard.setLastCount(oldProcardTem.getLastCount());
				sameCard.setBanBenNumber(oldProcardTem.getBanBenNumber());
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
						&& sameCard.getProcardTemplatesb() != null
						&& sameCard.getProcardTemplatesb().getMarkId() != null
						&& fMarkId.equals(sameCard.getProcardTemplatesb()
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
					Set<ProcardTemplatesb> sonpt = sameCard.getProcardsbTSet();
					for (ProcardTemplatesb p : sonpt) {
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
	@Override
	public ProcessTemplateFilesb findGyTzById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplateFilesb) totalDao.getObjectById(
				ProcessTemplateFilesb.class, id);
	}
	@Override
	public boolean deletesbBomTz(Integer id, String type) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return false;
		}
		String nowtime = Util.getDateTime();
		ProcessTemplateFilesb file = (ProcessTemplateFilesb) totalDao
				.getObjectById(ProcessTemplateFilesb.class, id);
		if (file != null) {
			Integer sbApplyId = file.getSbApplyId();
			boolean b = true;
			String addSql = "";
			String glSql="";
			if (file.getProductStyle() == null
					|| !file.getProductStyle().equals("试制")) {
				addSql = " and (productStyle is null or productStyle !='试制') ";
			} else {
				String banben = null;
				if(file.getProcessNO()==null){
					banben = (String) totalDao.getObjectByCondition("select banBenNumber from ProcardTemplatesb where id=?", file.getGlId());
				}else{
					banben = (String) totalDao.getObjectByCondition("select procardTemplatesb.banBenNumber from ProcessTemplatesb where id=?", file.getGlId());
				}
				String banbensql = null;
				if(banben==null){
					banbensql  =" and (banBenNumber is null or banBenNumber='')";
				}else{
					banbensql = " and banBenNumber ='"+banben+"'";
				}
				
				
				glSql = " and productStyle ='试制' and ((glId in (select id from ProcardTemplatesb where markId='"+file.getMarkId()+"' "+banbensql+" and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除'))" +
						" and  processNO is null ) " +
						"or (processNO is not null and glId in (select id from ProcessTemplatesb where (dataStatus is null or dataStatus!='删除') and procardTemplatesb.id in(select id from ProcardTemplatesb where markId='"+file.getMarkId()+"' "+banbensql+" and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')) )))";
			}
			if (file.getBanci() == null || file.getBanci() == 0) {
				addSql += " and (banci is null or banci=0) ";
			} else {
				addSql += " and  banci=" + file.getBanci();
			}
			if ("sop".equals(type)) {
				b = totalDao.delete(file);
			} else {
				// 同件号公用文件，此文件为下发的原图，原图的删除需要同步到整个零件
				// List<ProcessTemplateFile> fileList = totalDao.query(
				// "from ProcessTemplateFile where markId=? and fileName=? and status='默认'"
				// + addSql2, file.getMarkId(), file.getFileName());
				List<ProcessTemplateFilesb> fileList = null;
				if (file.getOldfileName() != null
						&& file.getOldfileName().length() > 0) {

					fileList = totalDao.query(
							"from ProcessTemplateFilesb where sbApplyId=? and markId=? and oldfileName=? and fileName=?"
									+ addSql+glSql,sbApplyId, file.getMarkId(), file
									.getOldfileName(),file.getFileName());
				} else {
					fileList = totalDao
							.query(
									"from ProcessTemplateFilesb where sbApplyId=? and markId=? and (oldfileName is null or  oldfileName='')"
											+ addSql+glSql,sbApplyId, file.getMarkId());
				}
				for (ProcessTemplateFilesb f : fileList) {
					b = b & totalDao.delete(f);
				}
			}
			ProcardTemplatesb pt = (ProcardTemplatesb) totalDao
					.getObjectByCondition(
							"from ProcardTemplatesb where markId=? "
									+ addSql
									+ " and (dataStatus=null or dataStatus!='删除' ) ",
							file.getMarkId());
			if (pt != null) {
				ProcardTemplatesbChangeLog.addchangeLog(totalDao, pt, file, "图纸",
						"删除", user, nowtime);
			}
//			AttendanceTowServerImpl.updateJilu(7, file, file.getId(), "件号:"
//					+ file.getMarkId() + "第" + file.getProcessNO() + "工序"
//					+ file.getProcessName() + " " + file.getType() + " 图纸名:"
//					+ file.getOldfileName() + "(删除)");
			return b;
		}
		return false;
	}
	@Override
	public String saveProcessTemplateFile(
			ProcessTemplateFilesb processTemplateFile, Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		processTemplateFile.setStatus("默认");
		ProcessTemplatesb process = (ProcessTemplatesb) totalDao.getObjectById(
				ProcessTemplatesb.class, id);
		if (process != null) {
			ProcardTemplatesb pt = process.getProcardTemplatesb();
			 if(pt.getBzStatus()!=null
			 &&pt.getBzStatus().equals("已批准")){
				 return"此工序的零件编制状态为已批准不允许上传图纸!";
			 }
			processTemplateFile.setProductStyle(pt.getProductStyle());
			processTemplateFile.setProcessName(process.getProcessName());
			processTemplateFile.setProcessNO(process.getProcessNO());
			Integer banci = process.getProcardTemplatesb().getBanci();
			if (pt.getBzStatus() != null
					&& pt.getBzStatus().equals("已批准")) {
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
			processTemplateFile.setMarkId(pt
					.getMarkId());
			if (pt.getProductStyle().equals("试制")) {
				processTemplateFile.setGlId(process.getId());
			}
			if (totalDao.save(processTemplateFile)) {
				ProcardTemplatesbChangeLog.addchangeLog(totalDao,pt, processTemplateFile, "图纸", "添加",
						user, nowtime);
				//同步其他同版本试制工序图纸
				String banbenSql=null;
				String banbenSql2=null;
				if (pt.getProductStyle().equals("试制")){
					if(pt.getBanBenNumber()==null||pt.getBanBenNumber().length()==0){
						banbenSql = " and (procardTemplatesb.banBenNumber is null or procardTemplate.banBenNumber='')";
						banbenSql2 = " and (banBenNumber is null or banBenNumber='')";
						
					}else{
						banbenSql = " and procardTemplatesb.banBenNumber='"+pt.getBanBenNumber()+"'";
						banbenSql2 = " and banBenNumber='"+pt.getBanBenNumber()+"'";
					}
					List<ProcessTemplatesb> processlist = totalDao.query("from ProcessTemplatesb where processNO =? and processName=? and id!=? and (dataStatus is null or dataStatus !='删除')" +
							" and procardTemplatesb.markId=? and procardTemplatesb.sbApplyId=? and procardTemplate.productStyle='试制' "+banbenSql, process.getProcessNO(),process.getProcessName(),process.getId(),pt.getSbApplyId());
					if(processlist!=null&&processlist.size()>0){
						for(ProcessTemplatesb process2: processlist){
							ProcessTemplateFilesb file2 = new ProcessTemplateFilesb();
							BeanUtils.copyProperties(processTemplateFile, file2, new String []{"id"});
							file2.setGlId(process2.getId());
							file2.setProductStyle("试制");
							file2.setBanci(pt.getBanci());
							file2.setSbApplyId(pt.getSbApplyId());
							totalDao.save(file2);
						}
					}
				}
				return "true";
			} else
				return "false";
		}
		return "没有找到目标工序";
	}
	@Override
	public List<ProcessTemplatesb> findProcessByFkId(Integer fkId) {
		// TODO Auto-generated method stub
		if (fkId != null && fkId > 0) {
			String hql = "from ProcessTemplatesb pc where pc.procardTemplatesb.id=? and (pc.dataStatus is null or pc.dataStatus !='删除')  order by pc.processNO";
			return totalDao.query(hql, fkId);
		}
		return null;
	}
	@Override
	public List getProcesssbTz(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplatesb process = findProcessT(id);
		if (process != null && process.getProcardTemplatesb()!= null) {
			ProcardTemplatesb pt = process.getProcardTemplatesb();
			String canChange="no";
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准图纸删除'";
			String sc = (String) totalDao.getObjectByCondition(hql1);
			if(sc!=null&&sc.equals("是")){
				canChange = "yes";
			}else{
				if(pt.getBzStatus()==null||!pt.getBzStatus().equals("已批准")){
					canChange ="yes";
				}else{
					Float tqCount = (Float) totalDao.getObjectByCondition("select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2" +
							" where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) and p2.id=?", pt.getRootId());
					if(tqCount!=null&&tqCount>0){
						canChange ="yes";
					}
				}
			}
			String addSql1 = null;
			Integer banci = pt.getBanci();
			if (banci == null || banci == 0) {
				addSql1 = " and (banci is null or banci =0)";
			} else {
				addSql1 = " and banci =" + banci;
			}
			String addSql2 = null;
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' and  glId="
						+ process.getId();
			}
			List<ProcessTemplateFilesb> ptFileList = totalDao.query(
					"from ProcessTemplateFilesb where processNO=? and processName=? and markId=? "
					+ addSql1 + addSql2 + " order by oldfileName",
			process.getProcessNO(), process.getProcessName(), pt.getMarkId());
			if(ptFileList!=null&&ptFileList.size()>0){
				for(ProcessTemplateFilesb pf:ptFileList){
					pf.setCanChange(canChange);
				}
			}
			return ptFileList;
		}
		return null;
	}
	@Override
	public List<ProcardTemplatesb> findPtsbListByFatherId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
		.query(
				"from ProcardTemplatesb where fatherId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by xuhao",
				id);
	}
	@Override
	public String deleteSons(int[] checkboxs, Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (checkboxs != null && checkboxs.length > 0 && id != null) {
			for (int i = 0; i < checkboxs.length; i++) {
				ProcardTemplatesb procardTemplate = (ProcardTemplatesb) totalDao
						.getObjectByCondition(
								"from ProcardTemplatesb where id=? and procardTemplatesb.id=?",
								checkboxs[i], id);
				if (procardTemplate.getProcardTemplatesb() != null
						&& procardTemplate.getProcardTemplatesb().getBzStatus() != null
						&& (procardTemplate.getProcardTemplatesb().getBzStatus()
								.equals("已批准") || procardTemplate
								.getProcardTemplatesb().getBzStatus().equals(
										"设变发起中"))) {
					return "上层零件编制状态为:"
							+ procardTemplate.getProcardTemplatesb()
									.getBzStatus() + "不允许删除下阶层!";
				}
				if (procardTemplate != null) {
					String markId = procardTemplate.getMarkId();
					boolean b = true;
					// 删除上层同件号的同件号零件
					List<ProcardTemplatesb> sonList = totalDao
							.query(
									"from ProcardTemplatesb where markId=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除') " +
									" and productStyle=? and  procardTemplatesb.id in(select id from ProcardTemplatesb where sbApplyId=? and markId=(select markId from ProcardTemplatesb where id=?))",
									markId, procardTemplate.getProductStyle(),procardTemplate.getSbApplyId(),
									id);
					List<String> markIds = new ArrayList<String>();
					if (sonList != null && sonList.size() > 0) {
						for (int j = (sonList.size() - 1); j >= 0; j--) {
							ProcardTemplatesb procardt = sonList.get(j);
							if (!markIds.contains(procardt)) {
								String xiaohao = null;
								ProcardTemplatesbChangeLog.addchangeLog(totalDao,
										procardTemplate, procardt, "子件", "删除",
										user, nowtime);
								markIds.add(procardt.getMarkId());
							}
							Set<ProcessTemplatesb> processSet = procardt
									.getProcessTemplatesb();
							if (processSet != null && processSet.size() > 0) {
								for (ProcessTemplatesb process : processSet) {
									process.setDataStatus("删除");
									process.setProcardTemplatesb(null);
									process.setOldPtId(procardt.getId());
									process.setOldbanci(procardt.getBanci());
									process.setDeleteTime(Util.getDateTime());
									totalDao.update(process);
								}
							}
							procardt.setDataStatus("删除");
							procardt.setProcardTemplatesb(null);
							b = b & totalDao.update(procardt);
							b = b & tbDownDataStatus(procardt);
						}
					}
				}
			}
		}
		return "删除成功!";
	}
	@Override
	public String sonMoveStatus(List<ProcardTemplatesb> procardTemplatesbList,
			Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplatesb father = (ProcardTemplatesb) totalDao.getObjectById(
				ProcardTemplatesb.class, id);
		if (father == null) {
			return "没有找到目标上层零件!";
		}
		List<ProcardTemplatesb> otherList = totalDao
				.query(
						"from ProcardTemplatesb where (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and procardTemplatesb.id in"
								+ "(select id from ProcardTemplatesb where sbApplyId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and id!=? and markId=?) order by markId",
								father.getSbApplyId(),id, father.getMarkId());
		String bzStatus = father.getBzStatus();
		if (procardTemplatesbList != null && procardTemplatesbList.size() > 0) {
			String nowtime = Util.getDateTime();
			for (ProcardTemplatesb pt : procardTemplatesbList) {
				if (pt != null && pt.getId() != null) {
					ProcardTemplatesb old = (ProcardTemplatesb) totalDao
							.getObjectById(ProcardTemplatesb.class, pt.getId());
					boolean b1 = false;
					if (bzStatus == null || !bzStatus.equals("已批准")) {
						if (old.getProcardStyle().equals("外购")) {
							if (!Util.isEquals(old.getQuanzi1(), pt.getQuanzi1())
									|| !Util.isEquals(old.getQuanzi2(), pt
											.getQuanzi2())) {
								old.setHasChange("是");
								b1 = true;
								ProcardTemplatesbChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
							old.setQuanzi1(pt.getQuanzi1());
							old.setQuanzi2(pt.getQuanzi2());
						} else {
							if (!Util.isEquals(old.getCorrCount(), pt.getCorrCount())) {
								old.setHasChange("是");
								b1 = true;
								ProcardTemplatesbChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
							old.setCorrCount(pt.getCorrCount());
						}
						if (Util.isEquals(old.getLingliaostatus(), pt
								.getLingliaostatus())) {
							old.setHasChange("是");
							if (b1) {
								ProcardTemplatesbChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
						}
						old.setLingliaostatus(pt.getLingliaostatus());
					}
					old.setXuhao(pt.getXuhao());
					totalDao.update(old);
					if (otherList != null && otherList.size() > 0) {
						boolean b = false;// 开始同步标记
						for (ProcardTemplatesb other : otherList) {
							if (other.getMarkId().equals(old.getMarkId())) {
								b = true;
								if (bzStatus == null || !bzStatus.equals("已批准")) {
									if (other.getProcardStyle().equals("外购")) {
										if (!Util.isEquals(other.getQuanzi1(), pt
												.getQuanzi1())
												|| !Util.isEquals(
														other.getQuanzi2(), pt
																.getQuanzi2())) {
											old.setHasChange("是");
										}
										other.setQuanzi1(pt.getQuanzi1());
										other.setQuanzi2(pt.getQuanzi2());
									} else {
										if (Util.isEquals(other.getCorrCount(), pt
												.getCorrCount())) {
											old.setHasChange("是");
										}
										other.setCorrCount(pt.getCorrCount());
									}
									if (Util.isEquals(other.getLingliaostatus(), pt
											.getLingliaostatus())) {
										other.setHasChange("是");
									}
									other.setLingliaostatus(pt
											.getLingliaostatus());
								}
								other.setXuhao(pt.getXuhao());
								totalDao.update(other);
							} else if (b) {
								// 已经开始同步了，但是件号已经不同表示这个件号已经被遍历完了
								break;
							}
						}
					}
				}
			}
		}
		return null;
	}
	@Override
	public Integer nextNoProcessNo(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplatesb process = (ProcessTemplatesb) totalDao
				.getObjectByCondition(
						"from ProcessTemplatesb where procardTemplatesb.id=? and (dataStatus is null or dataStatus!='删除') order by processNO desc",
						id);
		if (process == null) {
			return 1;
		}
		return process.getProcessNO() + 1;
	}
	@Override
	public ProcessTemplatesb findProcesssbT(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplatesb)totalDao.getObjectById(ProcessTemplatesb.class, id);
	}
	@Override
	public String surechange(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, id);
		if(ptbbApply==null){
			return "没有找到对应的设计变更单!";
		}
		List<ProcardTemplateBanBen> ptbbList = totalDao.query("from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?", id);
		if(ptbbList!=null||ptbbList.size()>0){
			
		}else{
			return  "没有设变明细,异常设变!";
		}
		ProcardTemplatesb ptsb = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId = ? and id=rootId and (dataStatus is null or dataStatus!='删除')", id);
		if(ptsb==null){
			//总成删除还没想好怎么处理
			return "目前不支持删除BOM!";
		}
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class, ptbbApply.getRootId());
		if(pt==null){
			return "原发设变的工程BOM没有找到,数据异常!";
		}
		//清理之前的对比数据
		List<ProcardTemplatesbDifferenceDetail> ptsbddList = totalDao.query("from ProcardTemplatesbDifferenceDetail where ptsbdId in(select id from  ProcardTemplatesbDifference where sbApplyId=?)", ptbbApply.getId());
		List<ProcardTemplatesbDifference> ptsbdList = totalDao.query("from ProcardTemplatesbDifference where sbApplyId=?", ptbbApply.getId());
		List<ProcardTemplateAboutsbDetail> ptaboutsbdList = totalDao.query("from ProcardTemplateAboutsbDetail where ptasbId in(select id from  ProcardTemplateAboutsb where sbApplyId=?)", ptbbApply.getId());
		List<ProcardTemplateAboutsb> ptaboutsbList = totalDao.query("from ProcardTemplateAboutsb where sbApplyId=?", ptbbApply.getId());
		if(ptsbddList!=null&&ptsbddList.size()>0){
			for(ProcardTemplatesbDifferenceDetail obj:ptsbddList){
				totalDao.delete(obj);
			}
		}
		if(ptsbdList!=null&&ptsbdList.size()>0){
			for(ProcardTemplatesbDifference obj:ptsbdList){
				totalDao.delete(obj);
			}
		}
		if(ptaboutsbdList!=null&&ptaboutsbdList.size()>0){
			for(ProcardTemplateAboutsbDetail obj:ptaboutsbdList){
				totalDao.delete(obj);
			}
		}
		if(ptaboutsbList!=null&&ptaboutsbList.size()>0){
			for(ProcardTemplateAboutsb obj:ptaboutsbList){
				totalDao.delete(obj);
			}
		}
		boolean isthissb=false;
		compareSbDiffrence(ptbbApply,ptbbList,ptsb,pt,isthissb,1f,1f,null);
		ptbbApply.setProcessStatus("工程师评审");
		//设置查询涉及外购件的在制,在途,库存数据
		List<ProcardTemplateAboutsb> ptAoutsbList = totalDao.query("from ProcardTemplateAboutsb where yongliao!=0 and sbApplyId=?", ptbbApply.getId());
		if(ptAoutsbList!=null&&ptAoutsbList.size()>0){
			for(ProcardTemplateAboutsb ptasb:ptAoutsbList){
				setProcardTemplateAboutsbData(ptasb);
			}
		}
		List<ProcardTemplatesb> tobeYpzList = totalDao.query(" from ProcardTemplatesb where sbApplyId=? " +
				"and markId in(select markId from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?)" +
				" and (dataStatus is null or dataStatus!='删除') and bzStatus!='已批准'", ptbbApply.getId(),ptbbApply.getId());
		if(tobeYpzList!=null&&tobeYpzList.size()>0){
			for(ProcardTemplatesb tobeYpz:tobeYpzList){
				tobeYpz.setBzStatus("已批准");
				totalDao.update(tobeYpz);
			}
		}
		totalDao.update(ptbbApply);		
		return "true";
	}
	/**
	 * 设置设变涉及外购件的在制,在途,库存数据
	 * @param ptasb
	 */
	private void setProcardTemplateAboutsbData(ProcardTemplateAboutsb ptasb) {
		// TODO Auto-generated method stub
		if(ptasb.getProcardStyle().equals("外购")){
			String kgsql = "";
			if (ptasb.getKgliao() != null
					&& ptasb.getKgliao().length() > 0) {
				kgsql += " and kgliao ='" + ptasb.getKgliao() + "'";
			}else{
				if(ptasb.getYongliao()<=0){
					kgsql += " and (kgliao is null or kgliao='')";//原来没有填后来设变后新增
				}else{
					throw new RuntimeException("外购件"+ptasb.getMarkId()+"的供料属性为空") ;
				}
			}
			// goodsClassSql =
			// " and ((goodsClass in ('外购件库','中间库') "
			// + kgsql + " ) or goodsClass = '备货库')";
			String goodsClassSql = " and goodsClass in ('外购件库') "
				+ kgsql;
			String banben_hql = "";
			String banben_hql2 = "";
			if (ptasb.getBanben() != null
					&& ptasb.getBanben().length() > 0) {
				banben_hql = " and banBenNumber='"
					+ ptasb.getBanben() + "'";
				banben_hql2 = " and banben='"
					+ ptasb.getBanben() + "'";
			}
			String specification_sql = "";
			// if (procard.getSpecification() != null
			// && procard.getSpecification().length() > 0) {
			// specification_sql = " and specification = '"
			// + procard.getSpecification() + "'";
			// }
			
			// 库存量(件号+版本+供料属性+库别)
			String hqlGoods = "";
			hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
				+ goodsClassSql
				+ " and goodsCurQuantity>0 "
				+ banben_hql
				+ " and (fcStatus is null or fcStatus='可用')";
			Float kcCount = (Float) totalDao.getObjectByCondition(
					hqlGoods, ptasb.getMarkId());
			if (kcCount == null || kcCount < 0) {
				kcCount = 0f;
			}
			
			/****************** 占用量=生产占用量+导入占用量 ******************************/
			// 系统占用量(含损耗)(已计算过采购量(1、有库存 2、采购中)，未领料)
			String zyCountSql = "select sum(hascount) from Procard where markId=?  "+kgsql
				+ banben_hql
				+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and (lingliaostatus='是' or lingliaostatus is null ) "
				+ " and (sbStatus<>'删除' or sbStatus is null ) ";
			Double zyCountD = (Double) totalDao.getObjectByConditionforDouble(
					zyCountSql, ptasb.getMarkId());
			if (zyCountD == null || zyCountD < 0) {
				zyCountD = 0d;
			}
			Float zyCount = zyCountD.floatValue();
			
			// // 导入占用量(系统切换时导入占用量)
			// String hqlGoods_zy =
			// "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
			// + banben_hql
			// +
			// " and goodsClass in ('占用库') and kgliao=? and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
			// Float kcCount_zy = (Float) totalDao.getObjectByCondition(
			// hqlGoods_zy, procard.getMarkId(), procard
			// .getKgliao());
			// if (kcCount_zy == null || kcCount_zy < 0) {
			// kcCount_zy = 0f;
			// }
			// zyCount += kcCount_zy;
			// if (zyCount < 0) {
			// zyCount = 0F;
			// }
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
				+ banben_hql2+kgsql
				+ "  and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null) "
				+ specification_sql;
			Double ztCountd = (Double) totalDao.getObjectByCondition(
					hql_zc0, ptasb.getMarkId());
			if (ztCountd == null) {
				ztCountd = 0D;
			}
			Float ztCount = ztCountd.floatValue();
			
			/****************** 结束 在途量=采购在途量+导入在途量 结束 ******************************/
			// (库存量+在途量(已生成采购，未到货))-占用量=剩余可用库存量
			Float daizhiCount = (kcCount + ztCount) - zyCount;
			if (daizhiCount < 0) {
				daizhiCount = 0F;
			}
			ptasb.setZaikuCount(kcCount);
			ptasb.setZaizhiCount(zyCount);
			ptasb.setZaituCount(ztCount);
			ptasb.setDaizhiCount(daizhiCount);
		}else{
			//计算在途
			String banbenSql = null;
			if(ptasb.getBanben()==null||ptasb.getBanben().length()==0){
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			}else{
				banbenSql = " and banBenNumber ='"+ptasb.getBanben()+"'";
			}
			Float zaituCount = (Float)totalDao.getObjectByCondition("select count(*) from Procard where markId=? and status not in('完成','入库','待入库','取消','删除') and (sbStatus is null or sbStatus!='删除' ) "+banbenSql 
					+" and rootId not in(select id from Procard where id=rootId and status  in('完成','入库','待入库','取消','删除') and (sbStatus is null or sbStatus!='删除' ))", ptasb.getMarkId());
			ptasb.setZaizhiCount(zaituCount);
		}
		totalDao.save(ptasb);
		
	}
	/**
	 * 
	 * @param ptbbApply
	 * @param ptbbList
	 * @param ptsb
	 * @param pt
	 * @param isthissb
	 * @param yyongliang上层原用量
	 * @param nyongliang上层现用量
	 * @param ptbb
	 */
	private void compareSbDiffrence(ProcardTemplateBanBenApply ptbbApply,
			List<ProcardTemplateBanBen> ptbbList, ProcardTemplatesb ptsb,
			ProcardTemplate pt, boolean isthissb ,Float yyongliang,Float nyongliang,ProcardTemplateBanBen ptbb) {
		// TODO Auto-generated method stub
		ProcardTemplateBanBen ptbbthis=ptbb;
		if(ptbbthis!=null&&ptbbthis.getMarkId().equals(ptsb.getMarkId())){
			ptsb.setBzStatus("已批准");
			isthissb =true;
			totalDao.update(ptsb);
		}else{
			for(ProcardTemplateBanBen ptbb2:ptbbList){
				if(ptbb2.getMarkId().equals(ptsb.getMarkId())){
					ptsb.setBzStatus("已批准");
					ptbbthis=ptbb2;
					isthissb =true;//设变相关零件
					totalDao.update(ptsb);
					break;
				}
			}
		}
		Float ythisyongliang =yyongliang;//本层用量
		if(pt.getProcardStyle().equals("外购")){
			try {
				ythisyongliang =yyongliang* Util.Floatdiv(pt.getQuanzi2(), pt.getQuanzi1(), 4);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				ythisyongliang=0f;
//				throw new RuntimeException(pt.getMarkId()+"用量有误!");
			}
		} else if (!pt.getId().equals(pt.getRootId())) {
			if (pt.getCorrCount() == null) {
				ythisyongliang = 0f;
			} else {
				ythisyongliang = yyongliang * pt.getCorrCount();
			}
		}
		Float nthisyongliang =nyongliang;//本层用量
		if(ptsb.getProcardStyle().equals("外购")){
			try {
				nthisyongliang =nyongliang* Util.Floatdiv(ptsb.getQuanzi2(), ptsb.getQuanzi1(), 4);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(ptsb.getMarkId()+"用量有误!");
			}
		}else if(!ptsb.getId().equals(ptsb.getRootId())){
			nthisyongliang =nyongliang*ptsb.getCorrCount() ;
		}
		if(isthissb){//是否为此次设变相关零件，如果是则比对本身相关差异
			//比对本身是否有改变
			ProcardTemplatesbDifference.addProcardTemplatesbDifference(totalDao,ptsb,pt,yyongliang,nyongliang,ptbbthis);
			if(ptbbthis.getMarkId().equals(ptsb.getMarkId())){
				//比对下层工序
				int i = ProcardTemplatesbDifference.compareSbAndptprocess(totalDao,ptsb,pt);
				if(i==0){
					ptsb.setGxxg("否");
				}else{
					ptsb.setGxxg("是");
				}
				totalDao.update(ptsb);
				//比对图纸
				ProcardTemplatesbDifference.compareSbAndptyz(totalDao,ptsb,pt,null,null);
			}
		}
		Set<ProcardTemplatesb> sonptsbSet =  ptsb.getProcardsbTSet();
		Set<ProcardTemplate> sonptset = pt.getProcardTSet();
		List<ProcardTemplatesb> addList = new ArrayList<ProcardTemplatesb>();
		List<ProcardTemplate> deleteList = new ArrayList<ProcardTemplate>();
		
		if((sonptsbSet==null||sonptsbSet.size()==0)
				&&(sonptset!=null&&sonptset.size()>0)&&isthissb){//全部删除了
			if(ptbbthis!=null&&ptbbthis.getMarkId().equals(ptsb.getMarkId())){//零件无下层,全部新增
				for(ProcardTemplate sonpt:sonptset){
					if(sonpt.getDataStatus()==null
							||!sonpt.getDataStatus().equals("删除")){
//						deleteList.add(sonpt);
						//生成删除记录
						ProcardTemplatesbDifference.addProcardTemplatesbDifference(
								totalDao,ptsb, pt, sonpt, "子件", "删除",
								ythisyongliang,nthisyongliang,ptbbthis);
					}
				}
			}else{//由于预编译BOM生成时只复制到设变零件的下一层，所以上层零件无下层零件时就只比对用量
				Float cy= nthisyongliang-ythisyongliang;
				Float cy2 = Math.abs(cy);
				if(cy2>1||(cy2/nthisyongliang)>0.005){//有差异
					for(ProcardTemplate sonpt:sonptset){
						if(sonpt.getDataStatus()==null
								||!sonpt.getDataStatus().equals("删除")){
							addptylcy(pt,sonpt,cy,ptbbthis);
						}
					}
				}
			}
		}else if((sonptset==null||sonptset.size()==0)
				&&(sonptsbSet!=null&&sonptsbSet.size()>0)&&isthissb){
			for(ProcardTemplatesb sonptsb:sonptsbSet){
				if(sonptsb.getDataStatus()==null
						||!sonptsb.getDataStatus().equals("删除")){
					addList.add(sonptsb);
				}
			}
		}else {//互相比对
			List<String> hadMarkIdList = new ArrayList<String>();//存放设变bom有的下层的件号
			for(ProcardTemplatesb sonptsb:sonptsbSet){
				if(sonptsb.getDataStatus()!=null
						&&sonptsb.getDataStatus().equals("删除")){
					continue;
				}
				hadMarkIdList.add(sonptsb.getMarkId());
				boolean gccz =false;//工程BOM中是否存在此件号
				for(ProcardTemplate sonpt:sonptset){
					if(sonpt.getDataStatus()!=null
							&&sonpt.getDataStatus().equals("删除")){
						continue;
					}
					if(sonptsb.getMarkId().equals(sonpt.getMarkId())){
						compareSbDiffrence(ptbbApply, ptbbList, sonptsb, sonpt, isthissb, ythisyongliang, nthisyongliang, ptbbthis);
						gccz =true;
//						ProcardTemplateBanBen ptbb3=ptbb;
//						boolean isthissb2=isthissb;
//						for(ProcardTemplateBanBen ptbb2:ptbbList){
//							if(ptbb2.getMarkId().equals(sonpt.getMarkId())){
//								ptbb3=ptbb2;
//								isthissb2 =true;//设变相关零件
//								break;
//							}
//						}
//						if(isthissb2){
//							//比对本身是否有改变
//							ProcardTemplatesbDifference.addProcardTemplatesbDifference(totalDao,sonptsb,sonpt,yyongliang,nyongliang,ptbb3);
//							//比对下层工序
//							int ri =ProcardTemplatesbDifference.compareSbAndptprocess(totalDao,sonptsb,sonpt);
//							if(ri==0){
//								ptsb.setGxxg("否");
//							}else{
//								ptsb.setGxxg("是");
//							}
//							totalDao.update(ptsb);
//							//比对图纸
//							ProcardTemplatesbDifference.compareSbAndptyz(totalDao,sonptsb,sonpt,null,null);
//						}
						
					}
				}
				if(!gccz&&isthissb){//工程BOM不存在此零件
					addList.add(sonptsb);
				}
			}
			//查出工程BOM中有但是设变BOM中没有的数据
			if(isthissb){
				for(ProcardTemplate sonpt:sonptset){
					if(sonpt.getDataStatus()!=null
							&&sonpt.getDataStatus().equals("删除")){
						continue;
					}
					if(!hadMarkIdList.contains(sonpt.getMarkId())){
						if(ptbbthis!=null&&(ptbbthis.getMarkId().equals(ptsb.getMarkId())
								||ptbbthis.getMarkId().equals(sonpt.getMarkId()))){//上层零件为设变零件才比对零件是否有删除
							deleteList.add(sonpt);
						}else{//上层零件不是设变零件直接比对上层的单台用量，如有差异则标记下层的差异
							Float cy= nthisyongliang-ythisyongliang;
							Float cy2 = Math.abs(cy);
							if(cy2>1||(cy2/nthisyongliang)>0.005){//有差异
								addptylcy(pt,sonpt,cy,ptbbthis);
							}
						}
					}
				}
			}
			if(isthissb){//如果是设变相关的零件则记录差异
				//生成添加日志
				for(ProcardTemplatesb sonsb :addList){
					ProcardTemplatesbDifference.addProcardTemplatesbDifference(totalDao, ptsb, sonsb, "子件", "增加",ythisyongliang,nthisyongliang,ptbbthis);
				}
				//生成删除日志
				for(ProcardTemplate son :deleteList){
					ProcardTemplatesbDifference.addProcardTemplatesbDifference(totalDao,ptsb, pt, son, "子件", "删除",ythisyongliang,nthisyongliang,ptbbthis);
				}
			}
		}
		
	}
	private void addptylcy(ProcardTemplate pt, ProcardTemplate son, Float cy,ProcardTemplateBanBen ptbb) {
		// TODO Auto-generated method stub
		Float thiscy=cy;
		try {
			if(son.getProcardStyle().equals("外购")){
				thiscy=Util.Floatdelete(cy*son.getQuanzi2(), son.getQuanzi1());
			}else{
				thiscy = cy*son.getCorrCount();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		ProcardTemplateAboutsb ptasb1= (ProcardTemplateAboutsb) totalDao.getObjectByCondition("from ProcardTemplateAboutsb where sbApplyId=? and markId=? ",ptbb.getProcardTemplateBanBenApply().getId(),son.getMarkId());
		if(ptasb1==null){
			//生成设变涉及相关零件主表
			ptasb1 = new ProcardTemplateAboutsb();
			ptasb1.setSbApplyId(ptbb.getProcardTemplateBanBenApply().getId());//设变单id
			ptasb1.setSbNumber(ptbb.getProcardTemplateBanBenApply().getSbNumber());//设变单号
			ptasb1.setProcardStyle(son.getProcardStyle());//生产类型
			ptasb1.setProductStyle(son.getProductStyle());//生产类型
			ptasb1.setMarkId(son.getMarkId());//件号
			ptasb1.setProName(son.getProName());//名称
			ptasb1.setBanben(son.getBanBenNumber());//版本
			ptasb1.setBanci(son.getBanci());
			ptasb1.setWgType(son.getWgType());//物料类别
			ptasb1.setKgliao(son.getKgliao());//客供属性
			ptasb1.setSpecification(son.getSpecification());//规格
			ptasb1.setUnit(son.getUnit());// 单位(自制)
			ptasb1.setTuhao(son.getTuhao());//图号
			ptasb1.setYongliao(thiscy);//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
			totalDao.save(ptasb1);
		}else{
			ptasb1.setYongliao(Util.Floatadd(ptasb1.getYongliao(), thiscy));//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
//			ptasbd1 = totalDao.getObjectByCondition("", agr)
		}
		//生成设变相关零件子表
		
		ProcardTemplateAboutsbDetail ptasbd1 =  new ProcardTemplateAboutsbDetail();
		ptasbd1.setSbptId(ptbb.getId());//设变明细Id(表明是由于哪个设变零件到导致的)
		ptasbd1.setSbptMarkId(ptbb.getMarkId());//设变明细件号
		if(son.getProcardTemplate()!=null){
			ptasbd1.setScmarkId(son.getProcardTemplate().getMarkId());//上层零件件号
		}
		ptasbd1.setProcardStyle(son.getProcardStyle());//生产类型
		ptasbd1.setProductStyle(son.getProductStyle());//生产类型
		ptasbd1.setMarkId(son.getMarkId());//件号
		ptasbd1.setProName(son.getProName());//名称
		ptasbd1.setBanben(son.getBanBenNumber());//版本
		ptasbd1.setBanci(son.getBanci());
		ptasbd1.setWgType(son.getWgType());//物料类别
		ptasbd1.setKgliao(son.getKgliao());//客供属性
		ptasbd1.setSpecification(son.getSpecification());//规格
		ptasbd1.setUnit(son.getUnit());// 单位(自制)
		ptasbd1.setTuhao(son.getTuhao());//图号
		ptasbd1.setYongliao(thiscy);//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
		ptasbd1.setPtasbId(ptasb1.getId());
		totalDao.save(ptasbd1);
		if(!son.getProcardStyle().equals("外购")){//下层零件生成涉及零件记录
			Set<ProcardTemplate> thissonDownSet = son.getProcardTSet();
			if(thissonDownSet!=null&&thissonDownSet.size()>0){
				for(ProcardTemplate thissonDown:thissonDownSet){
					Float yl2 = thiscy;
					if(thissonDown.getProcardStyle().equals("外购")){
						try {
							yl2 = thiscy*Util.Floatdiv(thissonDown.getQuanzi2(), thissonDown.getQuanzi1(),4);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException(thissonDown.getMarkId()+"用量有误!");
						}
					}else{
						yl2 = thiscy*thissonDown.getCorrCount();
					}
					addsbxgljyl(thissonDown,ptbb,yl2,totalDao);
				}
			}
		}
		
	}
	
	private static void addsbxgljyl(ProcardTemplate son,ProcardTemplateBanBen ptbb,Float thisyl,TotalDao totalDao) {
		// TODO Auto-generated method stub
		ProcardTemplateAboutsb ptasb1= (ProcardTemplateAboutsb) totalDao.getObjectByCondition("from ProcardTemplateAboutsb where sbApplyId=? and markId=? ",ptbb.getProcardTemplateBanBenApply().getId(),son.getMarkId());
		if(ptasb1==null){
			//生成设变涉及相关零件主表
			ptasb1 = new ProcardTemplateAboutsb();
			ptasb1.setSbApplyId(ptbb.getProcardTemplateBanBenApply().getId());//设变单id
			ptasb1.setSbNumber(ptbb.getProcardTemplateBanBenApply().getSbNumber());//设变单号
			ptasb1.setProcardStyle(son.getProcardStyle());//生产类型
			ptasb1.setProductStyle(son.getProductStyle());//生产类型
			ptasb1.setMarkId(son.getMarkId());//件号
			ptasb1.setProName(son.getProName());//名称
			ptasb1.setBanben(son.getBanBenNumber());//版本
			ptasb1.setBanci(son.getBanci());
			ptasb1.setWgType(son.getWgType());//物料类别
			ptasb1.setKgliao(son.getKgliao());//客供属性
			ptasb1.setSpecification(son.getSpecification());//规格
			ptasb1.setUnit(son.getUnit());// 单位(自制)
			ptasb1.setTuhao(son.getTuhao());//图号
			ptasb1.setYongliao(thisyl);//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
			totalDao.save(ptasb1);
		}else{
			ptasb1.setYongliao(Util.Floatadd(ptasb1.getYongliao(), thisyl));//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
//			ptasbd1 = totalDao.getObjectByCondition("", agr)
		}
		//生成设变相关零件子表
		
		ProcardTemplateAboutsbDetail ptasbd1 =  new ProcardTemplateAboutsbDetail();
		ptasbd1.setSbptId(ptbb.getId());//设变明细Id(表明是由于哪个设变零件到导致的)
		ptasbd1.setSbptMarkId(ptbb.getMarkId());//设变明细件号
		if(son.getProcardTemplate()!=null){
			ptasbd1.setScmarkId(son.getProcardTemplate().getMarkId());//上层零件件号
		}
		ptasbd1.setProcardStyle(son.getProcardStyle());//生产类型
		ptasbd1.setProductStyle(son.getProductStyle());//生产类型
		ptasbd1.setMarkId(son.getMarkId());//件号
		ptasbd1.setProName(son.getProName());//名称
		ptasbd1.setBanben(son.getBanBenNumber());//版本
		ptasbd1.setBanci(son.getBanci());
		ptasbd1.setWgType(son.getWgType());//物料类别
		ptasbd1.setKgliao(son.getKgliao());//客供属性
		ptasbd1.setSpecification(son.getSpecification());//规格
		ptasbd1.setUnit(son.getUnit());// 单位(自制)
		ptasbd1.setTuhao(son.getTuhao());//图号
		ptasbd1.setYongliao(thisyl);//单台用量增减（表示生产一个总成的用量增减 正数为增负数为减）
		ptasbd1.setPtasbId(ptasb1.getId());
		totalDao.save(ptasbd1);
		if(!son.getProcardStyle().equals("外购")){//下层零件生成涉及零件记录
			Set<ProcardTemplate> thissonDownSet = son.getProcardTSet();
			if(thissonDownSet!=null&&thissonDownSet.size()>0){
				for(ProcardTemplate thissonDown:thissonDownSet){
					Float yl2 = thisyl;
					if(thissonDown.getProcardStyle().equals("外购")){
						try {
							yl2 = thisyl*Util.Floatdiv(thissonDown.getQuanzi2(), thissonDown.getQuanzi1(),4);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException(thissonDown.getMarkId()+"用量有误!");
						}
					}else{
						yl2 = thisyl*thissonDown.getCorrCount();
					}
					addsbxgljyl(thissonDown,ptbb,yl2,totalDao);
				}
			}
		}
	}
	
	
	@Override
	public List<UsersGroup> findUsersGroupBytag(String tag) {
		// TODO Auto-generated method stub
		return totalDao.query("from UsersGroup where status='sb'");
	}
	@Override
	public List<AssessPersonnel> findsbrylistbyzb(Integer id) {
		// TODO Auto-generated method stub
		return totalDao.query(" from AssessPersonnel where usersGroup.id=?", id);
	}
	@Override
	public String submitgcsps(ProcardTemplateBanBenApply bbAply,
			List<ProcardTemplateAboutsbcltype> ptasbclTypeList,
			List<ProcardBanBenJudge> pbbJudgeList,
			List<ProcardBanBenJudge> pbbJudgeList2, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录";
		}
		ProcardTemplateBanBenApply oldbbAppy = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if(oldbbAppy==null){
			return "没有找到目标设变单!";
		}
		if(!oldbbAppy.getProcessStatus().equals("工程师评审")){
			return "当前设变单的状态为:"+oldbbAppy.getProcessStatus()+",不能进行此操作!";
		}
		String nowTime = Util.getDateTime();
		//获取工程师
		ProcardTemplateBanBenJudges gcs = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType='工程师' ", oldbbAppy.getId(),user.getId());
		if(gcs==null){
			return "您不是此设变单的工程师无法进行此操作!";
		}
		gcs.setRemark(remark);
		totalDao.update(gcs);
		//设变相关零件处理方案
		if(ptasbclTypeList!=null&&ptasbclTypeList.size()>0){
			for(ProcardTemplateAboutsbcltype ptasbclType:ptasbclTypeList){
				if(ptasbclType!=null&&ptasbclType.getPtasbId()!=null&&ptasbclType.getPtbbjId()!=null){
					if((ptasbclType.getZaizhicltype()!=null&&ptasbclType.getZaizhicltype().length()>0)
							||(ptasbclType.getZaitucltype()!=null&&ptasbclType.getZaitucltype().length()>0)
							||(ptasbclType.getZaikucltype()!=null&&ptasbclType.getZaikucltype().length()>0)
							||(ptasbclType.getDaizhicltype()!=null&&ptasbclType.getDaizhicltype().length()>0)){
						ProcardTemplateAboutsbcltype oldcltype =(ProcardTemplateAboutsbcltype) totalDao.getObjectByCondition
							(" from ProcardTemplateAboutsbcltype where ptasbId=? and userId=? ", ptasbclType.getPtasbId(),gcs.getUserId());
						if(oldcltype!=null){
							oldcltype.setZaizhicltype(ptasbclType.getZaizhicltype());//在制处理方案
							oldcltype.setZaitucltype(ptasbclType.getZaitucltype());//在途处理方案
							oldcltype.setZaikucltype(ptasbclType.getZaikucltype());//库存处理方案
							oldcltype.setDaizhicltype(ptasbclType.getDaizhicltype());//呆滞处理方案
							ptasbclType.setAddTime(nowTime);
							totalDao.update(oldcltype);
						}else{
							ptasbclType.setUserName(gcs.getUserName());
							ptasbclType.setUserId(gcs.getUserId());
							ptasbclType.setAddTime(nowTime);
							totalDao.save(ptasbclType);
						}
					}
				}
			}
		}
		//填写设变关联订单的处理方案
		if (pbbJudgeList != null && pbbJudgeList.size() > 0) {
			for (ProcardBanBenJudge pbbJudge : pbbJudgeList) {
				if (pbbJudge != null && pbbJudge.getRemark() != null
						&& pbbJudge.getRemark().length() > 0) {
					if (pbbJudge.getRemark().equals("请选择")) {
						return "请选择有效的处理方案!";
					}
					ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
							.getObjectByCondition(
									"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
									pbbJudge.getProcessbbJudgeId(),
									pbbJudge.getPtbbJudgeId());
					if (had != null) {
						had.setRemark(pbbJudge.getRemark());
						had.setBcremark(pbbJudge.getBcremark());
						totalDao.update(had);
					} else {
						pbbJudge.setUserId(user.getId());
						pbbJudge.setUserName(user.getName());
						pbbJudge.setAddTime(nowTime);
						totalDao.save(pbbJudge);
					}
					ProcessAboutBanBenApply processbb = (ProcessAboutBanBenApply) totalDao
					.getObjectById(
							ProcessAboutBanBenApply.class,
							pbbJudge.getProcessbbJudgeId());
					if (processbb != null) {
						processbb.setClType(pbbJudge.getRemark());
						totalDao.update(processbb);
					}

				}
			}
		}
		if (pbbJudgeList2 != null && pbbJudgeList2.size() > 0) {
			for (ProcardBanBenJudge pbbJudge2 : pbbJudgeList2) {
				if (pbbJudge2 != null && pbbJudge2.getRemark() != null
						&& pbbJudge2.getRemark().length() > 0) {
					if (pbbJudge2.getRemark().equals("请选择")) {
						return "请选择有效的处理方案!";
					}
					ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
							.getObjectByCondition(
									"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
									pbbJudge2.getProcessbbJudgeId(),
									pbbJudge2.getPtbbJudgeId());
					if (had != null) {
						had.setRemark(pbbJudge2.getRemark());
						totalDao.update(had);
					} else {
						pbbJudge2.setUserId(user.getId());
						pbbJudge2.setUserName(user.getName());
						pbbJudge2.setAddTime(nowTime);
						totalDao.save(pbbJudge2);
					}
					ProcessAboutBanBenApply processbb2 = (ProcessAboutBanBenApply) totalDao
					.getObjectById(
							ProcessAboutBanBenApply.class,
							pbbJudge2.getProcessbbJudgeId());
					if (processbb2 != null) {
						processbb2.setClType(pbbJudge2.getRemark());
						totalDao.update(processbb2);
					}
				}
			}
		}
		// 获取所有关联并通知生产件
		List<ProcardAboutBanBenApply> pabbList = totalDao.query(
				"from ProcardAboutBanBenApply where bbapplyId=?",
				oldbbAppy.getId());
		if (pabbList != null && pabbList.size() > 0) {
			StringBuffer syprocardIds= new StringBuffer();
			for (ProcardAboutBanBenApply pabb : pabbList) {
				Set<ProcessAboutBanBenApply> prcoessabbSet = pabb
						.getProcessabbSet();
				String clType = "顺延";
				if (prcoessabbSet != null
						&& prcoessabbSet.size() > 0) {
					for (ProcessAboutBanBenApply processbb : prcoessabbSet) {
						if (processbb.getClType() == null
								|| !processbb.getClType().equals(
										"顺延")) {
							clType = processbb.getClType();
						}
					}
				}
				if (clType != null && clType.equals("顺延")) {// 放开生产锁定
					pabb.setStatus("完成");
					totalDao.update(pabb);
					Procard procard = (Procard) totalDao
							.getObjectById(Procard.class, pabb
									.getProcardId());
					if (procard != null
							&& procard.getStatus().equals("设变锁定")) {
						procard.setStatus(procard.getOldStatus());
						totalDao.update(procard);
					}
					if(syprocardIds.length()==0){
						syprocardIds.append(pabb.getProcardId());
					}else{
						syprocardIds.append(","+pabb.getProcardId());
					}
				}
			}
			if(syprocardIds.length()>0){
				List<WaigouPlanLock> wpLockList = totalDao.query("from WaigouPlanLock where sbApplyId=? and dataStatus<>'取消'" +
						"and sbProcardId in("+syprocardIds.toString()+")", oldbbAppy.getId());
				if(wpLockList!=null&&wpLockList.size()>0){
					for(WaigouPlanLock wpLock:wpLockList){
						wpLock.setZxStatus("不执行");
						totalDao.update(wpLock);
						Float unpassCount = (Float) totalDao.getObjectByCondition("select count(id) from WaigouPlanLock where  entityName=? " +
								" and entityId=? and zxStatus='执行中' and dataStatus<>'取消'",wpLock.getEntityName(), wpLock.getEntityId());
						if(unpassCount==null||unpassCount==0){
							if(wpLock.getEntityName().equals("WaigouPlan")){
								WaigouPlan wp = (WaigouPlan) totalDao.getObjectById(WaigouPlan.class, wpLock.getEntityId());
								if(wp.getStatus().equals("设变锁定")){
									wp.setStatus(wp.getOldStatus());
									totalDao.update(wp);
								}
							}else if(wpLock.getEntityName().equals("WaigouWaiweiPlan")){
								WaigouWaiweiPlan wwp = (WaigouWaiweiPlan) totalDao.getObjectById(WaigouWaiweiPlan.class, wpLock.getEntityId());
								if(wwp.getStatus().equals("设变锁定")){
									wwp.setStatus(wwp.getOldStatus());
									totalDao.update(wwp);
								}
							}else if(wpLock.getEntityName().equals("ProcessInforWWApplyDetail")){
								ProcessInforWWApplyDetail processww = (ProcessInforWWApplyDetail) totalDao.getObjectById(ProcessInforWWApplyDetail.class, wpLock.getEntityId());
								if(processww.getProcessStatus().equals("设变锁定")){
									processww.setProcessStatus(processww.getOldprocessStatus());
									totalDao.update(processww);
								}
							}
						}
					}
				}
			}
		}
		oldbbAppy.setLastop("评选处理方案");
		oldbbAppy.setLastUserId(user.getId());
		oldbbAppy.setLastUserName(user.getName());
		oldbbAppy.setLastTime(Util.getDateTime());
		oldbbAppy.setProcessStatus("指派各部门");
		totalDao.update(oldbbAppy);
		return "评审成功";
	}
	@Override
	public String updatecbsh(ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		String nowTime = Util.getDateTime();
		if (user == null) {
			return "请先登录";
		}
		Float count = (Float) totalDao
		.getObjectByCondition(
				"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=(select ptbbApply.id from ProcardTemplateBanBenJudges where id=? ) and id!=? and judgeType='成本'  and (remark is null or remark='')",
				ptbbJudges.getId(),ptbbJudges.getId());
		List<Integer> uIdList = null;
		if(count==null||count==0){
			 uIdList = totalDao.query(
					"select userId from ProcardTemplateBanBenJudges where ptbbApply.id=(select ptbbApply.id from ProcardTemplateBanBenJudges where id=? ) and judgeType='内审' ",
					ptbbJudges.getId());
		}
		
		ProcardTemplateBanBenJudges old = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, ptbbJudges
						.getId());
		if (old == null) {
			return "没有找到对应的成本评审人员";
		}
		if (!old.getUserId().equals(user.getId())) {
			return "您没有提交此内部评审权限!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao.getObjectByCondition(" from ProcardTemplateBanBenApply where id=(select ptbbApply.id from ProcardTemplateBanBenJudges where id=? )", ptbbJudges.getId());
		if (!ptbbApply.getProcessStatus().equals("成本审核")) {
			return "当前状态不是成本审核!";
		}
		if(ptbbJudges.getRemark()==null||ptbbJudges.getRemark().length()==0){
			return "请填写审核内容!";
		}
		old.setRemark(ptbbJudges.getRemark());
		old.setPlTime(nowTime);
		totalDao.update(old);
		ptbbApply.setLastop("成本审核");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		if (count == null || count == 0) {
			if(uIdList==null||uIdList.size()==0){
				//获取工程师
				List<Integer> gcsUserIdList =  totalDao.query("select userId from ProcardTemplateBanBenJudges where ptbbApply.id=? and  judgeType='工程师' ", ptbbApply.getId());
				Integer[] userId = new Integer[gcsUserIdList.size()];
				for(int i=0;i<gcsUserIdList.size();i++){
					userId[i]=gcsUserIdList.get(i);
				}
				if (userId != null) {// 发送消息
					AlertMessagesServerImpl.addAlertMessages("设变成本评审", "总成为:"
							+ ptbbApply.getMarkId() + ",业务件号为："
							+ ptbbApply.getYwMarkId() + "的BOM内部评审结束" + "请前往处理.设变单号:"+ptbbApply.getSbNumber(),
							userId,
							"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
									+ ptbbApply.getId(), true, "no");
				}
				// 获取所有关联并通知生产件
				List<ProcardAboutBanBenApply> pabbList = totalDao.query(
						"from ProcardAboutBanBenApply where bbapplyId=?",
						ptbbApply.getId());
				if (pabbList != null && pabbList.size() > 0) {
					for (ProcardAboutBanBenApply pabb : pabbList) {
						Set<ProcessAboutBanBenApply> prcoessabbSet = pabb
								.getProcessabbSet();
						String clType = "顺延";
						if (prcoessabbSet != null
								&& prcoessabbSet.size() > 0) {
							for (ProcessAboutBanBenApply processbb : prcoessabbSet) {
								if (processbb.getClType() == null
										|| !processbb.getClType().equals(
												"顺延")) {
									clType = processbb.getClType();
								}
							}
						}
						if (clType != null && clType.equals("顺延")) {// 放开生产锁定
							pabb.setStatus("完成");
							totalDao.update(pabb);
							Procard procard = (Procard) totalDao
									.getObjectById(Procard.class, pabb
											.getProcardId());
							if (procard != null
									&& procard.getStatus().equals("设变锁定")) {
								procard.setStatus(procard.getOldStatus());
								totalDao.update(procard);
							}
						}
					}
				}
				ptbbApply.setProcessStatus("资料更新");
				totalDao.update(ptbbApply);
				return "成本审核结束!";
			}
			ptbbApply.setProcessStatus("各部门评审");
			totalDao.update(ptbbApply);
			Integer[] userId = new Integer[uIdList.size()] ;
			for(int i=0;i<uIdList.size();i++){
				userId[i]=uIdList.get(i);
			}
			if (userId != null) {// 发送消息
				AlertMessagesServerImpl.addAlertMessages("设变内审", "总成为:"
						+ ptbbApply.getMarkId() + ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM成本评审结束" + "请前往处理.设变单号:"+ptbbApply.getSbNumber(),
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			return "成本审核结束!";
		}
		totalDao.update(ptbbApply);
		return "提交成功!";
	}
	@Override
	public String backsbApply(Integer id, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录!";
		}
		ProcardTemplateBanBenApply old = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, id);
		if(old==null){
			return "没有找到目标设变单!";
		}
		ProcardTemplateBanBenJudges ptbbj=null;
		String ptbbjType="";
		String bcakStatus = "工程师评审";
		if(old.getProcessStatus().equals("成本审核")){
			ptbbjType = "成本";
		}else if(old.getProcessStatus().equals("各部门评审")){
			ptbbjType = "内审";
		}else if(old.getProcessStatus().equals("指派各部门")){
			ptbbjType = "工程师";
		}else if(old.getProcessStatus().equals("工程师评审")){
			ptbbjType = "工程师";
			bcakStatus = "变更设计";
		}
		ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition("from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType=? and userId=? ",old.getId(), ptbbjType,user.getId());
		if(ptbbj==null){
			return "对不起您无权此操作!";
		}
		if(old.getProcessStatus().equals("工程师评审")){
			List<ProcardTemplatesb> ptsbList = totalDao.query("from ProcardTemplatesb where sbApplyId=? and markId in(select markId from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?)", id,id);
			if(ptsbList!=null&&ptsbList.size()>0){
				for(ProcardTemplatesb ptsb:ptsbList){
					ptsb.setBzStatus("待编制");
					totalDao.update(ptsb);
				}
			}
		}
		List<ProcardAboutBanBenApply> pabbaList = totalDao.query("from ProcardAboutBanBenApply where bbapplyId=?", id);
		if(pabbaList!=null&&pabbaList.size()>0){
			for(ProcardAboutBanBenApply pab:pabbaList){
				pab.setStatus("待处理");
				totalDao.update(pab);
			}
		}
		ptbbj.setRemark(remark);
		old.setLastop("打回");
		old.setLastUserId(user.getId());
		old.setLastUserName(user.getName());
		old.setLastTime(Util.getDateTime());
		old.setProcessStatus(bcakStatus);
		return "打回成功!";
	}
	@Override
	public String submitnp(ProcardTemplateBanBenApply bbAply,
			ProcardTemplateBanBenJudges ptbbJudges,
			List<ProcardTemplateAboutsbcltype> ptasbclTypeList,
			List<ProcardBanBenJudge> pbbJudgeList,
			List<ProcardBanBenJudge> pbbJudgeList2, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录";
		}
		ProcardTemplateBanBenApply oldbbAppy = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if(oldbbAppy==null){
			return "没有找到目标设变单!";
		}
		if(!oldbbAppy.getProcessStatus().equals("各部门评审")){
			return "当前设变单的状态为:"+oldbbAppy.getProcessStatus()+",不能进行此操作!";
		}
		String nowTime = Util.getDateTime();
		//获取工程师
		ProcardTemplateBanBenJudges ns = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType='内审' ", oldbbAppy.getId(),user.getId());
		if(ns==null){
			return "您不是此设变单的内审人员无法进行此操作!";
		}else{
			ns.setRemark(ptbbJudges.getRemark());
			ns.setPlTime(nowTime);
			totalDao.update(ns);
		}
		
		//设变相关零件处理方案
		if(ptasbclTypeList!=null&&ptasbclTypeList.size()>0){
			for(ProcardTemplateAboutsbcltype ptasbclType:ptasbclTypeList){
				if(ptasbclType!=null&&ptasbclType.getPtasbId()!=null&&ptasbclType.getPtbbjId()!=null){
					if((ptasbclType.getZaizhicltype()!=null&&ptasbclType.getZaizhicltype().length()>0)
							||(ptasbclType.getZaitucltype()!=null&&ptasbclType.getZaitucltype().length()>0)
							||(ptasbclType.getZaikucltype()!=null&&ptasbclType.getZaikucltype().length()>0)
							||(ptasbclType.getDaizhicltype()!=null&&ptasbclType.getDaizhicltype().length()>0)){
						ProcardTemplateAboutsbcltype oldcltype =(ProcardTemplateAboutsbcltype) totalDao.getObjectByCondition
							(" from ProcardTemplateAboutsbcltype where ptasbId=? and userId=? ", ptasbclType.getPtasbId(),ns.getUserId());
						if(oldcltype!=null){
							oldcltype.setZaizhicltype(ptasbclType.getZaizhicltype());//在制处理方案
							oldcltype.setZaitucltype(ptasbclType.getZaitucltype());//在途处理方案
							oldcltype.setZaikucltype(ptasbclType.getZaikucltype());//库存处理方案
							oldcltype.setDaizhicltype(ptasbclType.getDaizhicltype());//呆滞处理方案
							ptasbclType.setAddTime(nowTime);
							totalDao.update(oldcltype);
						}else{
							ptasbclType.setUserName(ns.getUserName());
							ptasbclType.setUserId(ns.getUserId());
							ptasbclType.setAddTime(nowTime);
							totalDao.save(ptasbclType);
						}
					}
				}
			}
		}
		//填写设变关联订单的处理方案
		if (pbbJudgeList != null && pbbJudgeList.size() > 0) {
			for (ProcardBanBenJudge pbbJudge : pbbJudgeList) {
				if (pbbJudge != null && pbbJudge.getRemark() != null
						&& pbbJudge.getRemark().length() > 0) {
					if (pbbJudge.getRemark().equals("请选择")) {
						return "请选择有效的处理方案!";
					}
					ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
							.getObjectByCondition(
									"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
									pbbJudge.getProcessbbJudgeId(),
									pbbJudge.getPtbbJudgeId());
					if (had != null) {
						had.setRemark(pbbJudge.getRemark());
						had.setBcremark(pbbJudge.getBcremark());
						totalDao.update(had);
					} else {
						pbbJudge.setUserId(user.getId());
						pbbJudge.setUserName(user.getName());
						pbbJudge.setAddTime(nowTime);
						totalDao.save(pbbJudge);
					}
//					ProcessAboutBanBenApply processbb = (ProcessAboutBanBenApply) totalDao
//					.getObjectById(
//							ProcessAboutBanBenApply.class,
//							pbbJudge.getProcessbbJudgeId());
//					if (processbb != null) {
//						processbb.setClType(pbbJudge.getRemark());
//						totalDao.update(processbb);
//					}

				}
			}
		}
		if (pbbJudgeList2 != null && pbbJudgeList2.size() > 0) {
			for (ProcardBanBenJudge pbbJudge2 : pbbJudgeList2) {
				if (pbbJudge2 != null && pbbJudge2.getRemark() != null
						&& pbbJudge2.getRemark().length() > 0) {
					if (pbbJudge2.getRemark().equals("请选择")) {
						return "请选择有效的处理方案!";
					}
					ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
							.getObjectByCondition(
									"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
									pbbJudge2.getProcessbbJudgeId(),
									pbbJudge2.getPtbbJudgeId());
					if (had != null) {
						had.setRemark(pbbJudge2.getRemark());
						totalDao.update(had);
					} else {
						pbbJudge2.setUserId(user.getId());
						pbbJudge2.setUserName(user.getName());
						pbbJudge2.setAddTime(nowTime);
						totalDao.save(pbbJudge2);
					}
//					ProcessAboutBanBenApply processbb2 = (ProcessAboutBanBenApply) totalDao
//					.getObjectById(
//							ProcessAboutBanBenApply.class,
//							pbbJudge2.getProcessbbJudgeId());
//					if (processbb2 != null) {
//						processbb2.setClType(pbbJudge2.getRemark());
//						totalDao.update(processbb2);
//					}
				}
			}
		}
		oldbbAppy.setLastop("评选处理方案");
		oldbbAppy.setLastUserId(user.getId());
		oldbbAppy.setLastUserName(user.getName());
		oldbbAppy.setLastTime(Util.getDateTime());
		Float count = (Float) totalDao
		.getObjectByCondition(
				"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审' and (remark is null or remark='') ",
				oldbbAppy.getId());
		if (count == null || count == 0) {
			//获取工程师
			List<Integer> gcsUserIdList =  totalDao.query("select userId from ProcardTemplateBanBenJudges where ptbbApply.id=? and  judgeType='工程师' ", oldbbAppy.getId());
			Integer[] userId = new Integer[gcsUserIdList.size()];
			for(int i=0;i<gcsUserIdList.size();i++){
				userId[i]=gcsUserIdList.get(i);
			}
			if (userId != null) {// 发送消息
				AlertMessagesServerImpl.addAlertMessages("设变内审结束", "总成为:"
						+ oldbbAppy.getMarkId() + ",业务件号为："
						+ oldbbAppy.getYwMarkId() + "的BOM内部评审结束" + "请前往处理.设变单号:"+oldbbAppy.getSbNumber(),
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ oldbbAppy.getId(), true, "no");
			}
			// 获取所有关联并通知生产件
			List<ProcardAboutBanBenApply> pabbList = totalDao.query(
					"from ProcardAboutBanBenApply where bbapplyId=?",
					oldbbAppy.getId());
			if (pabbList != null && pabbList.size() > 0) {
				for (ProcardAboutBanBenApply pabb : pabbList) {
					Set<ProcessAboutBanBenApply> prcoessabbSet = pabb
							.getProcessabbSet();
					String clType = "顺延";
					if (prcoessabbSet != null
							&& prcoessabbSet.size() > 0) {
						for (ProcessAboutBanBenApply processbb : prcoessabbSet) {
							if (processbb.getClType() == null
									|| !processbb.getClType().equals(
											"顺延")) {
								clType = processbb.getClType();
							}
						}
					}
					if (clType != null && clType.equals("顺延")) {// 放开生产锁定
						pabb.setStatus("完成");
						totalDao.update(pabb);
						Procard procard = (Procard) totalDao
								.getObjectById(Procard.class, pabb
										.getProcardId());
						if (procard != null
								&& procard.getStatus().equals("设变锁定")) {
							procard.setStatus(procard.getOldStatus());
							totalDao.update(procard);
						}
					}
				}
			}
			oldbbAppy.setProcessStatus("资料更新");
		}
		totalDao.update(oldbbAppy);
		return "评审成功";
	
	}
	@Override
	public String noprocardns(ProcardTemplateBanBenApply bbAply,
			ProcardTemplateBanBenJudges ptbbJudges,
			List<ProcardTemplateAboutsbcltype> ptasbclTypeList,
			String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录";
		}
		ProcardTemplateBanBenApply oldbbAppy = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if(oldbbAppy==null){
			return "没有找到目标设变单!";
		}
		if(!oldbbAppy.getProcessStatus().equals("各部门评审")){
			return "当前设变单的状态为:"+oldbbAppy.getProcessStatus()+",不能进行此操作!";
		}
		String nowTime = Util.getDateTime();
		//获取工程师
		ProcardTemplateBanBenJudges ns = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType='内审' ", oldbbAppy.getId(),user.getId());
		if(ns==null){
			return "您不是此设变单的内审人员无法进行此操作!";
		}else{
			ns.setRemark(ptbbJudges.getRemark());
			ns.setPlTime(nowTime);
			totalDao.update(ns);
		}
		
		//设变相关零件处理方案
		if(ptasbclTypeList!=null&&ptasbclTypeList.size()>0){
			for(ProcardTemplateAboutsbcltype ptasbclType:ptasbclTypeList){
				if(ptasbclType!=null&&ptasbclType.getPtasbId()!=null&&ptasbclType.getPtbbjId()!=null){
					if((ptasbclType.getZaizhicltype()!=null&&ptasbclType.getZaizhicltype().length()>0)
							||(ptasbclType.getZaitucltype()!=null&&ptasbclType.getZaitucltype().length()>0)
							||(ptasbclType.getZaikucltype()!=null&&ptasbclType.getZaikucltype().length()>0)
							||(ptasbclType.getDaizhicltype()!=null&&ptasbclType.getDaizhicltype().length()>0)){
						ProcardTemplateAboutsbcltype oldcltype =(ProcardTemplateAboutsbcltype) totalDao.getObjectByCondition
						(" from ProcardTemplateAboutsbcltype where ptasbId=? and userId=? ", ptasbclType.getPtasbId(),ns.getUserId());
						if(oldcltype!=null){
							oldcltype.setZaizhicltype(ptasbclType.getZaizhicltype());//在制处理方案
							oldcltype.setZaitucltype(ptasbclType.getZaitucltype());//在途处理方案
							oldcltype.setZaikucltype(ptasbclType.getZaikucltype());//库存处理方案
							oldcltype.setDaizhicltype(ptasbclType.getDaizhicltype());//呆滞处理方案
							ptasbclType.setAddTime(nowTime);
							totalDao.update(oldcltype);
						}else{
							ptasbclType.setUserName(ns.getUserName());
							ptasbclType.setUserId(ns.getUserId());
							ptasbclType.setAddTime(nowTime);
							totalDao.save(ptasbclType);
						}
					}
				}
			}
		}
		oldbbAppy.setLastop("评选处理方案");
		oldbbAppy.setLastUserId(user.getId());
		oldbbAppy.setLastUserName(user.getName());
		oldbbAppy.setLastTime(Util.getDateTime());
		Float count = (Float) totalDao
		.getObjectByCondition(
				"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审' and (remark is null or remark='') ",
				oldbbAppy.getId());
		if (count == null || count == 0) {
			//获取工程师
			List<Integer> gcsUserIdList =  totalDao.query("select userId from ProcardTemplateBanBenJudges where ptbbApply.id=? and  judgeType='工程师' ", oldbbAppy.getId());
			Integer[] userId = new Integer[gcsUserIdList.size()];
			for(int i=0;i<gcsUserIdList.size();i++){
				userId[i]=gcsUserIdList.get(i);
			}
			if (userId != null) {// 发送消息
				AlertMessagesServerImpl.addAlertMessages("设变成本评审", "总成为:"
						+ oldbbAppy.getMarkId() + ",业务件号为："
						+ oldbbAppy.getYwMarkId() + "的BOM内部评审结束" + "请前往处理.设变单号:"+oldbbAppy.getSbNumber(),
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
						+ oldbbAppy.getId(), true, "no");
			}
			// 获取所有关联并通知生产件
			List<ProcardAboutBanBenApply> pabbList = totalDao.query(
					"from ProcardAboutBanBenApply where bbapplyId=?",
					oldbbAppy.getId());
			if (pabbList != null && pabbList.size() > 0) {
				for (ProcardAboutBanBenApply pabb : pabbList) {
					Set<ProcessAboutBanBenApply> prcoessabbSet = pabb
					.getProcessabbSet();
					String clType = "顺延";
					if (prcoessabbSet != null
							&& prcoessabbSet.size() > 0) {
						for (ProcessAboutBanBenApply processbb : prcoessabbSet) {
							if (processbb.getClType() == null
									|| !processbb.getClType().equals(
									"顺延")) {
								clType = processbb.getClType();
							}
						}
					}
					if (clType != null && clType.equals("顺延")) {// 放开生产锁定
						pabb.setStatus("完成");
						totalDao.update(pabb);
						Procard procard = (Procard) totalDao
						.getObjectById(Procard.class, pabb
								.getProcardId());
						if (procard != null
								&& procard.getStatus().equals("设变锁定")) {
							procard.setStatus(procard.getOldStatus());
							totalDao.update(procard);
						}
					}
				}
			}
			oldbbAppy.setProcessStatus("资料更新");
		}
		totalDao.update(oldbbAppy);
		return "评审成功";
		
	}
	@Override
	public List<UsersGroup> finduserDeptList(String string) {
		// TODO Auto-generated method stub
		return totalDao.query("from UsersGroup where status=?", string);
	}
	@Override
	public List<Users> getusersBygroup(Integer id, String ids) {
		// TODO Auto-generated method stub
		List<Users> list = totalDao.query("from Users where id in(select userId from AssessPersonnel where usersGroup.id=?)", id);
		if(ids!=null&&ids.length()>0){
			List<Integer> selectedList = new ArrayList<Integer>();
			String[] uIdStrs = ids.split(";");
			if(uIdStrs!=null&&uIdStrs.length>0){
				for(String uIdStr:uIdStrs){
					Integer uId= Integer.parseInt(uIdStr);
					if(!selectedList.contains(uId)){
						selectedList.add(uId);
					}
				}
				for(int i=0;i<list.size();i++){
					Users user = (Users) list.get(i);
					if(selectedList.contains(user.getId())){
						user.setSelected(true);
					}else{
						user.setSelected(false);
					}
				}
			}
		}else{
			for(int i=0;i<list.size();i++){
				Users user = (Users) list.get(i);
					user.setSelected(false);
			}
		}
		return list;
	}
	@Override
	public String suresb(Integer id, ProcardTemplateBanBenJudges ptbbJudges,
			List<ProcardTemplateBanBen> ptbbList) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录!";
		}
		String time = Util.getDateTime();
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply != null) {
			if (!ptbbApply.getProcessStatus().equals("资料更新")) {
				return "当前进度状态为:" + ptbbApply.getProcessStatus() + "操作失败!";
			}
			ProcardTemplateBanBenJudges gcs = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition("from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='工程师'" +
					" and userId=?", id,user.getId());
			if(gcs==null){
				return "您不是此设变单指定的工程师无权进行此操作!";
			}
			
			if (ptbbJudges.getSelectUserId() != null
					&& ptbbJudges.getSelectUserId().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("编制通知")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("编制通知");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);
				AlertMessagesServerImpl.addAlertMessages("BOM设变编制通知", user
						.getName()
						+ "对总成为:"
						+ ptbbApply.getMarkId()
						+ ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM设变申请已通过，请前去完成BOM审批流程",
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			if (ptbbJudges.getSelectUserId2() != null
					&& ptbbJudges.getSelectUserId2().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId2().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("编制完通知")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("编制完通知");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);

			}
			if (ptbbJudges.getSelectUserId3() != null
					&& ptbbJudges.getSelectUserId3().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId3().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("佐证")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("佐证");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);

			}
			Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
					.getProcardTemplateBanBen();
			if (ptbbSet != null && ptbbSet.size() > 0) {
				Integer[] userId2 = new Integer[ptbbSet.size()];
				int bzi = 0;
				hadclMarkIdList =new ArrayList<String>();
				sbMarkIdList =new ArrayList<String>();
				for (ProcardTemplateBanBen ptbb : ptbbSet) {
					for (ProcardTemplateBanBen ptbb2 : ptbbList) {
						sbMarkIdList.add(ptbb2.getMarkId());
						if (ptbb2.getId() != null
								&& ptbb2.getId().equals(ptbb.getId())) {
							if (ptbb2.getBianzhiId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getBianzhiId());
								if (u != null) {
									ptbb.setBianzhiId(u.getId());
									ptbb.setBianzhiName(u.getName());
									userId2[bzi] = u.getId();
									bzi++;
								}
							}
							if (ptbb2.getJiaoduiId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getJiaoduiId());
								if (u != null) {
									ptbb.setJiaoduiId(u.getId());
									ptbb.setJiaoduiName(u.getName());
								}
							}
							if (ptbb2.getShenheId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getShenheId());
								if (u != null) {
									ptbb.setShenheId(u.getId());
									ptbb.setShenheName(u.getName());
								}
							}
							if (ptbb2.getPizhunId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getPizhunId());
								if (u != null) {
									ptbb.setPizhunId(u.getId());
									ptbb.setPizhunName(u.getName());
								}
							}
						}
					}
					if(!hadclMarkIdList.contains(ptbb.getMarkId())){
						Float tqCount = (Float) totalDao.getObjectByCondition("select count(*) from ProcardTemplatePrivilege where markId=? or markId=?", ptbbApply.getMarkId(),ptbbApply.getYwMarkId()==null?"":ptbbApply.getYwMarkId());
						procardTemBanBenLvup(ptbb, user, time, ptbbApply,ptbbSet,tqCount);
					}
				}
				AlertMessagesServerImpl.addAlertMessages("BOM设变编制通知", user
						.getName()
						+ "对总成为:"
						+ ptbbApply.getMarkId()
						+ ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM设变申请已通过，请前去编制完善BOM",
						userId2,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			ptbbApply.setProcessStatus("关联并通知生产");
			ptbbApply.setLastop("开启BOM编制");
			ptbbApply.setLastUserId(user.getId());
			ptbbApply.setLastUserName(user.getName());
			ptbbApply.setLastTime(Util.getDateTime());
			totalDao.update(ptbbApply);
		}
		return "BOM模板设变开启,请前往编制";
	}
	private void procardTemBanBenLvup(ProcardTemplateBanBen ptbb, Users user,
			String time, ProcardTemplateBanBenApply ptbbApply,
			Set<ProcardTemplateBanBen> ptbbSet,Float tqCount) {
		// TODO Auto-generated method stub
		if(hadclMarkIdList.contains(ptbb.getMarkId())){
			return ;//已处理
		}else{
			hadclMarkIdList.add(ptbb.getMarkId());
		}
		String productStyle = ptbbApply.getProductStyle();
		ProcardTemplatesb ptsbdelete = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId =? and markId=? and (banbenStatus is null or banbenStatus !='历史') and dataStatus='删除' and " +
				"(fatherId is null or fatherId not in(select id from ProcardTemplatesb where sbApplyId=? and dataStatus='删除')) ", ptbbApply.getId(),ptbb.getMarkId(),ptbbApply.getId());
		List<ProcardTemplatesb> ptsbundeleteList = totalDao.query("from ProcardTemplatesb where sbApplyId =? and markId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') and " +
				"(fatherId is null or fatherId not in(select id from ProcardTemplatesb where sbApplyId=? and dataStatus='删除')) ", ptbbApply.getId(),ptbb.getMarkId(),ptbbApply.getId());
		if(ptsbdelete==null&&(ptsbundeleteList==null||ptsbundeleteList.size()==0)){
			ptsbdelete = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId =? and markId=? and (banbenStatus is null or banbenStatus !='历史') and dataStatus='删除' ", ptbbApply.getId(),ptbb.getMarkId());
			if(ptsbdelete!=null){//被级联删除了，这种设变发的有点傻(就像伍忠威这样的)，但是没办法还是得处理
				return ;
			}else{
				throw new RuntimeException("设变零件"+ptbb.getMarkId()+"，数据异常!");
			}
		}
		ProcardTemplatesb ptsbundelete=null;
		if(ptsbundeleteList!=null&&ptsbundeleteList.size()>0){
			ptsbundelete = ptsbundeleteList.get(0);
		}
		ProcardTemplatesb psb = ptsbundelete==null?ptsbdelete:ptsbundelete;
		if(psb.getMarkId().equals("YT6.100.2392")){
			System.out.println(psb.getId());
		}
		// 外购件升级库中版本
		if (psb.getProcardStyle().equals("外购")) {
			List<YuanclAndWaigj> wgjList = null;
			String productStylSql = null;
			if (productStyle.equals("批产")) {
				productStylSql = " and  productStyle='批产'";
			} else {
				productStylSql = " and  1=1";
			}
			wgjList = totalDao
			.query(
					"from YuanclAndWaigj where markId=? and (banbenStatus is null or banbenStatus !='历史')  "
					+ productStylSql, psb
					.getMarkId());
			String waiBanbenNumber = psb.getBanBenNumber();
			if (wgjList != null && wgjList.size() > 0) {
				boolean has = false;
				for (YuanclAndWaigj wgj : wgjList) {
					if (Util.isEquals(waiBanbenNumber, wgj
							.getBanbenhao())) {
						wgj.setStatus("使用");
						totalDao.update(wgj);
						has = true;
						break;
					}

				}
				if (!has) {
					YuanclAndWaigj wgj = wgjList.get(0);
					YuanclAndWaigj wgjnew = new YuanclAndWaigj();
					BeanUtils.copyProperties(wgj, wgjnew,
							new String[] { "id",
									"zhuserOfferSet" });
					wgjnew.setBanbenhao(waiBanbenNumber);
					wgjnew.setPricestatus("新增");
					wgjnew.setBanbenStatus("默认");
					wgjnew.setStatus("使用");
					wgjnew.setAddTime(time);
					wgjnew.setAddUserCode(user.getCode());
					wgjnew.setAddUserName(user.getName());
					totalDao.save(wgjnew);
					if (psb.getProductStyle().equals("批产")
							|| ptbb.getSingleSb() == null
							|| !ptbb.getSingleSb().equals("是")) {
						wgj.setBanbenStatus("历史");
						totalDao.update(wgj);
					}
				}
			}
		}
		//生产历史版本
		String historySql = "from ProcardTemplate where markId=? and productStyle=? and banbenStatus='历史' and (dataStatus is null or dataStatus!='删除')";
		if (ptbb.getBanBenNumber() == null || ptbb.getBanBenNumber().length() == 0) {
			historySql = historySql
					+ " and (banBenNumber is null or banBenNumber='')";
		} else {
			historySql = historySql + " and banBenNumber='"
					+ ptbb.getBanBenNumber() + "'";
		}
		if (ptbb.getBanci() == null || ptbb.getBanci() == 0) {
			historySql = historySql
					+ " and (banci is null or banci=0)";
		} else {
			historySql = historySql + " and banci='"
					+ ptbb.getBanci() + "'";
		}
		ProcardTemplate history = (ProcardTemplate) totalDao
				.getObjectByCondition(historySql, ptbb.getMarkId(),productStyle);
		String banciSql = null;
		if (ptbb.getBanci() == null || ptbb.getBanci() == 0) {
			banciSql = " and ( banci is null or banci=0)";
		} else {
			banciSql = " and banci=" + ptbb.getBanci();
		}
		if(ptbb.getMarkId().equals("DKBA6.866.8204")){
			System.out.println(banciSql);
		}
		//获取工程BOM数据
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectByCondition("from ProcardTemplate where markId=? and productStyle=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
				+banciSql, ptbb.getMarkId(),productStyle);
		if (pt !=null&&history == null) {// 此版本还没有历史，添加历史
			history = new ProcardTemplate();
			BeanUtils.copyProperties(pt, history, new String[] {
					"id", "procardTemplate", "procardTSet",
					"processTemplate", "zhUsers" });
			history.setBanbenStatus("历史");
			// 同步历史工序
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
						if (productStyle.equals("试制")) {// 复制出一份图纸给历史模板工序
							String tzSql = "from ProcessTemplateFile where glId=? and processNO is not null "
									+ banciSql;
							List<ProcessTemplateFile> processFileList = totalDao
									.query(tzSql, process2.getId());
							if (processFileList != null
									&& processFileList.size() > 0) {
								for (ProcessTemplateFile processFile : processFileList) {
									if (processFile.getBanci() == null) {
										processFile.setBanci(0);
									}
									ProcessTemplateFile newfile = new ProcessTemplateFile();
									BeanUtils.copyProperties(
											processFile, newfile,
											new String[] { "id" });
									newfile.setBzStatus("初始");
									newfile.setStatus("默认");
									newfile.setGlId(process2
											.getId());
									newfile.setProductStyle("试制");
									totalDao.save(newfile);
								}
							}
						}
						processSet2.add(process2);
					}
				}
			}
			history.setProcessTemplate(processSet2);
			totalDao.save(history);
			if (productStyle.equals("试制")) {// 复制出一份图纸给历史模板
				String tzSql = "from ProcessTemplateFile where glId=? and processNO is  null "
						+ banciSql;
				List<ProcessTemplateFile> processFileList = totalDao
						.query(tzSql, pt.getId());
				if (processFileList != null
						&& processFileList.size() > 0) {
					for (ProcessTemplateFile processFile : processFileList) {
						if (processFile.getBanci() == null) {
							processFile.setBanci(0);
						}
						ProcessTemplateFile newfile = new ProcessTemplateFile();
						BeanUtils.copyProperties(processFile,
								newfile, new String[] { "id" });
						newfile.setBzStatus("初始");
						newfile.setStatus("默认");
						newfile.setGlId(history.getId());
						newfile.setProductStyle("试制");
						totalDao.save(newfile);
					}
				}
			}
		}
		
		if(ptsbdelete!=null&&ptsbdelete.getFatherId()!=null){//零件删除 获取上层零件，对比是否要删除
			ProcardTemplatesb fathersb = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where id=? and (banbenStatus is null or banbenStatus !='历史')", ptsbdelete.getFatherId());
			if(fathersb!=null&&hadclMarkIdList.contains(fathersb.getMarkId())){//上层零件没有处理过
				//查看上层零件是否还有此零件(即删了后又加回来了)
				ProcardTemplatesb sameson = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where fatherId=? and markId=? and (dataStatus is null or dataStatus !='删除' ) " +
						"and (banbenStatus is null or banbenStatus !='历史')", fathersb.getId(),ptsbdelete.getMarkId());
				if(sameson==null){//同步上层零件删除此零件
					String fatherbanbensql = null;
					if(fathersb.getBanBenNumber()==null||fathersb.getBanBenNumber().length()==0){
						fatherbanbensql = " and (banBenNumber is null or banBenNumber='')";
					}else{
						fatherbanbensql = " and banBenNumber = '"+fathersb.getBanBenNumber().length()+"'";
					}
					List<ProcardTemplate> sameFatherList = totalDao.query("from ProcardTemplate where markId=? and procardStyle=? "+fatherbanbensql+ "  and (dataStatus is null or dataStatus !='删除' ) " +
						"and (banbenStatus is null or banbenStatus !='历史')", fathersb.getMarkId(),productStyle);
					if(sameFatherList!=null&&sameFatherList.size()>0){
						for(ProcardTemplate sameFather:sameFatherList){
							Set<ProcardTemplate> sonSet =  sameFather.getProcardTSet();
							if(sonSet!=null&&sonSet.size()>0){
								for(ProcardTemplate son:sonSet){
									if(son.getDataStatus()!=null&&son.getDataStatus().equals("删除")){
										continue;
									}
									if(son.getMarkId().equals(ptsbdelete.getMarkId())){
										son.setDataStatus("删除");
										son.setProcardTemplate(null);
										totalDao.update(son);
										tbDownDataStatus(son);
										break;
									}
								}
							}
							
						}
					}
				}
			}
		}
		if(ptsbundelete!=null){
			//同步工程BOM此零件
			//判断是否改变了procardStyle
			ProcardTemplatesbDifferenceDetail ptsbdd = (ProcardTemplatesbDifferenceDetail) totalDao.getObjectByCondition("from ProcardTemplatesbDifferenceDetail where datatype='本身'" +
					" and sxName='零件类型' and ptsbdId in(select id from ProcardTemplatesbDifference where sbApplyId=? and markId=?) ", ptbbApply.getId(),ptsbundelete.getMarkId());
			
			String banbenSql = null;
			if(ptbb.getBanBenNumber()==null||ptbb.getBanBenNumber().length()==0){
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			}else{
				banbenSql = " and banBenNumber = '"+ptbb.getBanBenNumber()+"'";
			}
			List<ProcardTemplate> procardTemplateList = totalDao.query("from ProcardTemplate where markId=? and productStyle=? "+banbenSql+" and (banbenStatus is null or banbenStatus!='历史')" +
					" and (dataStatus is null or dataStatus !='删除') ", ptbb.getMarkId(),productStyle);
			if(procardTemplateList!=null&&procardTemplateList.size()>0){
				for(ProcardTemplate samept:procardTemplateList){
					ProcardTemplatesb tbptsb= ptsbundelete;//用来同步的预编译零件
					boolean hadsamefather=false;
					if(samept.getProcardTemplate()!=null){
						for(ProcardTemplatesb ptsbxh: ptsbundeleteList){//寻找是否有同上层的零件
							if(ptsbxh.getProcardTemplatesb()!=null
									&& Util.isEquals(samept.getProcardTemplate().getMarkId(), ptsbxh.getProcardTemplatesb().getMarkId())){
								if(productStyle.equals("试制")){
									if(Util.isEquals(samept.getProcardTemplate().getBanBenNumber(), ptsbxh.getProcardTemplatesb().getBanBenNumber())){
										tbptsb = ptsbxh;
										hadsamefather =true;
									}
								}else{
									tbptsb = ptsbxh;
									hadsamefather =true;
								}
								break;
							}
						}
					}
					//将预编译BOM对比工程BOM并复制
					String banbensbSql = null;
					String banbenySql2 = null;
					String bancisbSql = null;
					String banciySql2 = null;
					if(tbptsb.getBanBenNumber()==null||tbptsb.getBanBenNumber().length()==0){
						banbensbSql = " and (banBenNumber is null or banBenNumber='')";
					}else{
						banbensbSql = " and banBenNumber='"+tbptsb.getBanBenNumber()+"'";
					}
					if(samept.getBanBenNumber()==null||samept.getBanBenNumber().length()==0){
						banbenySql2 = " and (banBenNumber is null or banBenNumber='')";
					}else{
						banbenySql2 = " and banBenNumber='"+samept.getBanBenNumber()+"'";
					}
					if(tbptsb.getBanci()==null||tbptsb.getBanci()==0){
						bancisbSql = " and (banci is null or banci=0)";
					}else{
						bancisbSql = " and banci='"+tbptsb.getBanci()+"'";
					}
					if(samept.getBanci()==null||samept.getBanci()==0){
						banciySql2 = " and (banci is null or banci=0)";
					}else{
						banciySql2 = " and banci='"+samept.getBanci()+"'";
					}
					
					//1.复制基础资料
					BeanUtils.copyProperties(tbptsb, samept, new String[] { "id","belongLayer",
							"procardTemplatesb", "procardsbTSet", "processTemplatesb",
							"zhUsers", "maxCount", "procardStyle","quanzi1","quanzi2",
							"rootId", "fatherId","procardStyle","corrCount","lingliaostatus",
							"ywMarkId","rootMarkId"});
					if(hadsamefather){//同上层复制用量
						samept.setCorrCount(tbptsb.getCorrCount());
						samept.setQuanzi1(tbptsb.getQuanzi1());
						samept.setQuanzi2(tbptsb.getQuanzi2());
						samept.setLingliaostatus(tbptsb.getLingliaostatus());
					}
					if(ptsbdd!=null&&!pt.getProcardStyle().equals("总成")){
						samept.setProcardStyle(tbptsb.getProcardStyle());
					}
					//设置审批人员
					if(ptbb.getBianzhiId()==null){
						samept.setBianzhiId(user.getId());
						samept.setBianzhiName(user.getName());
					}else{
						samept.setBianzhiId(ptbb.getBianzhiId());
						samept.setBianzhiName(ptbb.getBianzhiName());
						
					}
					if(ptbb.getJiaoduiId()==null){
					}else{
						samept.setJiaoduiId(ptbb.getJiaoduiId());
						samept.setJiaoduiName(ptbb.getJiaoduiName());
						
					}
					if(ptbb.getShenheId()==null){
					}else{
						samept.setShenheId(ptbb.getShenheId());
						samept.setShenheName(ptbb.getShenheName());
						
					}
					if(ptbb.getPizhunId()==null){
					}else{
						samept.setPizhunId(ptbb.getPizhunId());
						samept.setPizhunName(ptbb.getPizhunName());
						
					}
					samept.setBzStatus("待编制");
					
					//2.复制零件图纸
					//获取预编译图纸
					List<ProcessTemplateFilesb> ptFilesbList = null;
					if ("试制".equals(tbptsb.getProductStyle())) {
						ptFilesbList = totalDao.query(
								"from ProcessTemplateFilesb where sbApplyId=? and processNO is null  "
								+ bancisbSql + " and glId=? ",
								ptbbApply.getId(), tbptsb.getId());
					} else {
						ptFilesbList = totalDao
						.query(
								"from ProcessTemplateFilesb where sbApplyId=? and processNO is null "
								+ "  and markId=?  "
								+ bancisbSql,ptbbApply.getId(), tbptsb
								.getMarkId());
					}
					//获取工程BOM图纸
					List<ProcessTemplateFile> ptFileList = null;
					if ("试制".equals(samept.getProductStyle())) {//(将原来的留作历史，直接将预编译的全部添加过来)
//						ptFileList = totalDao.query(
//								"from ProcessTemplateFile where glId=? and processNO is null and productStyle='试制'"
//								+ banciySql2 ,samept.getId() );
					} else {//直接查询最新版次有没有数据进行，如果有则说明已经处理过不再处理如果没有这复制预编译图纸
						ptFileList = totalDao
						.query(
								"from ProcessTemplateFile where processNO is null "
								+ "  and markId=?  and productStyle='批产'"
								+ bancisbSql, samept.getMarkId());
					}
					if(ptFileList==null||ptFileList.size()==0){
						compareAndcopytzfromprocesstsbtoprocesst(tbptsb, samept, ptFilesbList, null);
					}
					
					//3.复制工序信息
					boolean fzgx =false;
					for(ProcardTemplateBanBen tptbb: ptbbSet){
						if(tbptsb.getMarkId().equals(tbptsb.getMarkId())){
							fzgx=true;
						}
					}
					if(fzgx){//只有设变零件才会同步工序
						copyprocessfromptsbtopt(tbptsb,samept,ptbbApply,banbensbSql,banbenySql2,bancisbSql,banciySql2,tqCount);
					}
					
					//4.复制下层
					List<ProcardTemplatesb> sonsbList =null;
					if(!tbptsb.getProcardStyle().equals("外购")){
						sonsbList=totalDao.query(" from ProcardTemplatesb where fatherId=? and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus !='删除')", tbptsb.getId());
						if(sonsbList==null&&sonsbList.size()==0){
							throw new RuntimeException("零件："+tbptsb.getMarkId()+"下没有子件!");
						}
					}
					if(	sonsbList!=null&&sonsbList.size()>0&&!samept.getProcardStyle().equals("外购")){
						Set<ProcardTemplate> sonSet = samept.getProcardTSet();
						if(sonSet==null){
							sonSet = new HashSet<ProcardTemplate>();
						}
						if(sonSet.size()==0){//添加子件
							for(ProcardTemplatesb sonsb:sonsbList){
								addsonsbtopt(ptbbApply,sonSet,sonsb,samept);
							}
						}else{//比对下层
							List<String> sbmarkIdlist = new ArrayList<String>();
							List<String> markIdList = new ArrayList<String>();
							int i=0;
							Set<ProcardTemplate> deletesonSet = new HashSet<ProcardTemplate>();
							List<ProcardTemplatesb> xzSontList = new ArrayList<ProcardTemplatesb>(); 
							for(ProcardTemplatesb sonsb:sonsbList){
								if(sonsb.getDataStatus()!=null
										&&sonsb.getDataStatus().equals("删除")){
									continue;
								}
								if(sonsb.getBanbenStatus()!=null
										&&sonsb.getBanbenStatus().equals("历史")){
									continue;
								}
								sbmarkIdlist.add(sonsb.getMarkId());
								i++;
								for(ProcardTemplate son:sonSet){
									if(son.getDataStatus()!=null
											&&son.getDataStatus().equals("删除")){
										continue;
									}
									if(i==1){
										markIdList.add(son.getMarkId());
									}
									if(sonsb.getMarkId().equals(son.getMarkId())){
										//同件号处理
										boolean issblj=false;
										boolean scsb=false;
										for(ProcardTemplateBanBen ptbb2:ptbbSet){
											if(ptbb2.getMarkId().equals(sonsb.getMarkId())){
												procardTemBanBenLvup(ptbb2, user, time, ptbbApply, ptbbSet, tqCount);
												issblj=true;
												break;
											}
										}
										if(!issblj){//非设变零件只同步用量
											boolean needcl=false;
											if(son.getProcardStyle().equals(sonsb.getProcardStyle())){
												//类型一样
												if(son.getProcardStyle().equals("外购")
														&&!Util.isEquals(son.getKgliao(), sonsb.getKgliao())){
													needcl =true;
												}
												
											}else{
												needcl=true;
											}
											if(needcl){
												//删除原来的
												deletesonSet.add(son);
												//增加新的零件
												xzSontList.add(sonsb);
											}else{
												son.setQuanzi1(sonsb.getQuanzi1());
												son.setQuanzi2(sonsb.getQuanzi2());
												son.setCorrCount(sonsb.getCorrCount());
												son.setLingliaostatus(sonsb.getLingliaostatus());
												totalDao.save(son);
											}
											
										}
									}
								}
							}
							//增加工程BOM中没有的零件
							for(ProcardTemplatesb sonsb:sonsbList){
								if(!markIdList.contains(sonsb.getMarkId())){
									addsonsbtopt(ptbbApply,sonSet,sonsb,samept);
//									if(sbMarkIdList!=null&&sbMarkIdList.contains(sonsb.getMarkId())){
//									}else{
//										//获取工程BOM中最新数据
//									}
								}
							}
							//删除预编译BOM中没有的数据
							for(ProcardTemplate son:sonSet){
								if(son.getDataStatus()!=null
										&&son.getDataStatus().equals("删除")){
									continue;
								}
								if(!sbmarkIdlist.contains(son.getMarkId())){
									deletesonSet.add(son);
								}
							}
							sonSet.remove(deletesonSet);
							//补充替换供料属性或者生产属性的零件
							if(xzSontList.size()>0){
								for(ProcardTemplatesb sonsb:xzSontList){
									addsonsbtopt(ptbbApply,sonSet,sonsb,samept);
								}
							}
							
							for(ProcardTemplate deleteson:deletesonSet){
								deleteson.setDataStatus("删除");
								deleteson.setProcardTemplate(null);
								totalDao.update(deleteson);
								tbDownDataStatus(deleteson);
							}
							samept.setProcardTSet(sonSet);
							totalDao.update(samept);
						}
					}
				}
			}
			
			
			
		}
		
		
		
	}
	
	
	private void addsonsbtopt(ProcardTemplateBanBenApply ptbbApply,
			Set<ProcardTemplate> sonSet, ProcardTemplatesb sonsb,
			ProcardTemplate samept) {
		Users user = Util.getLoginUser();
		String time = Util.getDateTime();
		if (sonsb != null && samept != null) {
			// 获取主副两个对象的持久层
			ProcardTemplatesb oldpt2 = sonsb;
			ProcardTemplate oldpt1 = samept;
			boolean b = true;
			if (oldpt1 != null && oldpt2 != null) {
				if (sonSet != null && sonSet.size() > 0) {
					for (ProcardTemplate p : sonSet) {
						if (p.getMarkId() != null && oldpt1.getMarkId() != null
								&& p.getMarkId().equals(oldpt1.getMarkId())) {
							// 原流水零件模板下已含有被复制的流水零件模板
							return ;
						}
					}
				}else{
					sonSet = new HashSet<ProcardTemplate>();
				}
				
				// 复制procardTemplate
				ProcardTemplate newpt2 = new ProcardTemplate();
				// newpt2.setMarkId("123");
				BeanUtils.copyProperties(oldpt2, newpt2, new String[] { "id",
						"procardTemplatesb", "procardsbTSet", "processTemplatesb",
						"zhUsers", "ywMarkId", "rootMarkId", "productStyle" });
				newpt2.setId(null);
				newpt2.setProductStyle(samept.getProductStyle());
				newpt2.setRootId(oldpt1.getRootId());
				newpt2.setFatherId(oldpt1.getId());
				newpt2.setYwMarkId(oldpt1.getYwMarkId());
				newpt2.setAddcode(user.getCode());
				newpt2.setAdduser(user.getName());
				newpt2.setAddtime(Util.getDateTime());
				newpt2.setProcardTemplate(oldpt1);
				sonSet.add(newpt2);
				oldpt1.setProcardTSet(sonSet);
				totalDao.update(oldpt1);
				b = b & totalDao.save(newpt2);
				if (oldpt1.getBelongLayer() != null) {
					newpt2.setBelongLayer(oldpt1.getBelongLayer() + 1);
				}
				List<ProcardTemplatesb> ptSet = totalDao
						.query(
								"from ProcardTemplatesb where procardTemplatesb.id=? and (banbenStatus!='历史' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by id asc",
								oldpt2.getId());
				// Set<ProcardTemplate> ptSet2 = new
				// HashSet<ProcardTemplate>();
				// if (ptSet.size() > 0) {
				// for (ProcardTemplate pt : ptSet) {
				// ptSet2.add(pt);
				// }
				// }
				// 复制process
				Set<ProcessTemplatesb> processsbSet = oldpt2.getProcessTemplatesb();
				String bancisbSql = null;
				if(oldpt2.getBanci()==null||oldpt2.getBanci()==0){
					bancisbSql=" and (banci is null or banci=0)";
				}else{
					bancisbSql="and banci ="+oldpt2.getBanci();
				}
				if (processsbSet != null && processsbSet.size() > 0) {
					Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
					for (ProcessTemplatesb processsb : processsbSet) {
						//添加工序
						addprocesstsboprocess(processsb, ptbbApply, newpt2, newprocessSet, bancisbSql);
					}
					newpt2.setProcessTemplate(newprocessSet);
				}
				if (ptbbApply.getProductStyle().equals("试制")) {// 试制图纸复制
					List<ProcessTemplateFilesb> ptFilesbList =totalDao.query(
							"from ProcessTemplateFilesb where sbApplyId=? and processNO is null  "
							+ bancisbSql + " and glId=? ",
							ptbbApply.getId(), oldpt2.getId());
					
					compareAndcopytzfromprocesstsbtoprocesst(oldpt2, newpt2, ptFilesbList, null);
				}else{
					String oldpt2markId=oldpt2.getMarkId();
					String bancisql = null;
					if(newpt2.getBanci()==null||newpt2.getBanci()==0){
						bancisql = " and (banci is null or banci=0)";
					}else{
						bancisql = " and  banci="+newpt2.getBanci();
					}
					Float hadthismarkId = (Float) totalDao.getObjectByCondition("select count(*) from ProcardTemplate where markId = ? "+bancisql+" and id !=? and productStyle='批产' " +
							"and (banbenStatus!='历史' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')", oldpt2markId,newpt2.getId());
					if(hadthismarkId==null||hadthismarkId==0){//新零件
						List<ProcessTemplateFilesb> ptFilesbList = totalDao
						.query(
								"from ProcessTemplateFilesb where sbApplyId=? and processNO is null "
								+ "  and markId=?  "
								+ bancisbSql,ptbbApply.getId(), oldpt2
								.getMarkId());
						compareAndcopytzfromprocesstsbtoprocesst(oldpt2, newpt2, ptFilesbList, null);
					}
				}
				if (b & ptSet.size() > 0) {
					for (ProcardTemplatesb ptsb : ptSet) {
						addsonsbtopt(ptbbApply, null, ptsb, newpt2);
					}
				}
				return ;
			}
		}
		return ;
	}
	/**
	 * 比较预编译BOM零件和工程BOM零件工序，并复制编译BOM零件工序信息到工程BOM零件
	 * @param tbptsb
	 * @param samept
	 * @param ptbbApply
	 * @param banbensbSql
	 * @param banbenySql2
	 * @param bancisbSql
	 * @param banciySql2
	 * @param tqCount
	 */
	private void copyprocessfromptsbtopt(ProcardTemplatesb tbptsb,
			ProcardTemplate samept, ProcardTemplateBanBenApply ptbbApply,
			String banbensbSql,String banbenySql2,String bancisbSql,String banciySql2,Float tqCount ) {
		// TODO Auto-generated method stub
		//3.复制工序
		if(!tbptsb.getProcardStyle().equals("外购")
				||(tbptsb.getNeedProcess()!=null&&tbptsb.getNeedProcess().equals("yes"))){//需要工序（基础资料已复制过，可以用samept判断）
			List<ProcessTemplatesb> processTsbList=totalDao.query("from ProcessTemplatesb where procardTemplatesb.id=? and (dataStatus is null or dataStatus!='删除')"
					, tbptsb.getId());
			if((processTsbList==null||processTsbList.size()==0)&&(tqCount==null||tqCount==0)){
				throw new RuntimeException(tbptsb.getMarkId()+"没有填充工序");
			}
			Set<ProcessTemplate> processTset = samept.getProcessTemplate();
			if(processTsbList!=null&&processTsbList.size()>0
					&&(processTset==null||processTset.size()==0)){//现有，原没有
				processTset = new HashSet<ProcessTemplate>();
				for(ProcessTemplatesb processTsb:processTsbList){
					//全部添加
					addprocesstsboprocess(processTsb,ptbbApply,samept,processTset,bancisbSql);
				}
				samept.setProcessTemplate(processTset);
				
			}else if(processTset!=null&&processTset.size()>0
					&&(processTsbList==null||processTsbList.size()==0)){//现没有，原有
				//全部删除
				for(ProcessTemplate deletep:processTset){
					deletep.setProcardTemplate(null);
					deletep.setDataStatus("删除");
					deletep.setOldPtId(samept.getId());
					deletep.setOldbanci(samept.getBanci());
					deletep.setDeleteTime(Util.getDateTime());
					totalDao.update(deletep);
				}
				samept.setProcessTemplate(null);
			}else{
				List<Integer> processsbnoList = new ArrayList<Integer>();
				List<Integer> processnoList = new ArrayList<Integer>();
				int i=0;
				//第一步同步同工序号数据
				for(ProcessTemplatesb processTsb: processTsbList){
					processsbnoList.add(processTsb.getProcessNO());
					i++;
					for(ProcessTemplate processT:processTset){
						if(processT.getDataStatus()!=null&&
								processT.getDataStatus().equals("删除")){
							continue;
						}
						if(i==1){
							processnoList.add(processT.getProcessNO());
						}
						if(processTsb.getProcessNO().equals(processT.getProcessNO())){
							//工序号一样复制基础资料
							BeanUtils
							.copyProperties(processTsb, processT,
									new String[] { "id", "procardTemplatesb",
									"taSopGongwei",
							"processFuLiaoTemplate" });
							//获取预编译图纸
							List<ProcessTemplateFilesb> ptFilesbList = null;
							if ("试制".equals(tbptsb.getProductStyle())) {
								ptFilesbList = totalDao.query(
										"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
										+ bancisbSql + " and glId=? ",
										ptbbApply.getId(),processTsb.getProcessNO(), processTsb.getId());
							} else {
								ptFilesbList = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
										+ "  and markId=?  "
										+ bancisbSql,ptbbApply.getId(), processTsb
										.getProcessNO(), tbptsb
										.getMarkId());
							}
							//获取工程BOM图纸
							List<ProcessTemplateFile> ptFileList = null;
							if ("试制".equals(samept.getProductStyle())) {//原版次的图纸留作历史屏蔽查询
//								ptFileList = totalDao.query(
//										"from ProcessTemplateFile where glId=? and processNO =? and productStyle='试制'"
//										+ banciySql2 ,processT.getId(),processT.getProcessNO() );
							} else {//直接查询最新版次有没有数据进行，如果有则说明已经处理过不再处理如果没有这复制预编译图纸
								ptFileList = totalDao
								.query(
										"from ProcessTemplateFile where  processNO =? "
										+ "  and markId=?  and productStyle='批产'"
										+ bancisbSql, processT
										.getProcessNO(), samept
										.getMarkId());
							}
							if(ptFileList==null||ptFileList.size()==0){
								compareAndcopytzfromprocesstsbtoprocesst(processTsb,processT,ptFilesbList,null);
							}
							
						}
					}
				}
				//添加工程BOM没有的工序
				for(ProcessTemplatesb processTsb: processTsbList){
					if(!processnoList.contains(processTsb.getProcessNO())){
						addprocesstsboprocess(processTsb, ptbbApply, samept, processTset, bancisbSql);
					}
				}
				//删除预编译BOM没有的工序
				Set<ProcessTemplate> deleteSet =new HashSet<ProcessTemplate>();
				if (processTset != null && processTset.size() > 0) {
					for (ProcessTemplate deletep : processTset) {
						if (!processsbnoList.contains(deletep.getProcessNO())) {
							deleteSet.add(deletep);
						}
					}
					processTset.removeAll(deleteSet);
				}
				if(deleteSet!=null&&deleteSet.size()>0){
					for(ProcessTemplate deletep:deleteSet){
						deleteSet.add(deletep);
						deletep.setProcardTemplate(null);
						deletep.setDataStatus("删除");
						deletep.setOldPtId(samept.getId());
						deletep.setOldbanci(samept.getBanci());
						deletep.setDeleteTime(Util.getDateTime());
						totalDao.update(deletep);
					}
				}
				samept.setProcessTemplate(processTset);
			}
			
			
		}else{
			//非半成品外购件删除工序
			Set<ProcessTemplate> processTset = samept.getProcessTemplate();
			if(processTset!=null&&processTset.size()>0){
				for (ProcessTemplate deletep : processTset) {
					deletep.setProcardTemplate(null);
					deletep.setDataStatus("删除");
					deletep.setOldPtId(samept.getId());
					deletep.setOldbanci(samept.getBanci());
					deletep.setDeleteTime(Util.getDateTime());
					totalDao.update(deletep);
				}
				processTset=new HashSet<ProcessTemplate>();
				samept.setProcessTemplate(null);
			}
		}
		totalDao.update(samept);
		
	}
	/**
	 * 复制预编译工序到工程零件下
	 * @param processTsb
	 * @param ptbbApply
	 * @param samept
	 */
	private void addprocesstsboprocess(ProcessTemplatesb processTsb,
			ProcardTemplateBanBenApply ptbbApply, ProcardTemplate samept,
			Set<ProcessTemplate> processTset,String bancisbSql) {
		// TODO Auto-generated method stub
		ProcessTemplate newprocesst = new ProcessTemplate();
		BeanUtils
		.copyProperties(processTsb, newprocesst,
				new String[] { "id", "procardTemplatesb",
				"taSopGongwei",
		"processFuLiaoTemplate" });
		newprocesst.setProcardTemplate(samept);
		newprocesst.setProcessTsbId(processTsb.getId());
		totalDao.save(newprocesst);
		processTset.add(newprocesst);
		List<ProcessTemplateFilesb> ptFilesbList = null;
		if ("试制".equals(processTsb.getProductStyle())) {
			ptFilesbList = totalDao.query(
					"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
					+ bancisbSql + " and glId=? ",
					ptbbApply.getId(),processTsb.getProcessNO(), processTsb.getId());
		} else {
			ptFilesbList = totalDao
			.query(
					"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
					+ "  and markId=?  "
					+ bancisbSql,ptbbApply.getId(), processTsb
					.getProcessNO(), samept
					.getMarkId());
		}
		if (ptFilesbList != null && ptFilesbList.size() > 0) {
			for (ProcessTemplateFilesb ptFilesb : ptFilesbList) {
				if (ptFilesb != null) {
					ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
					BeanUtils
					.copyProperties(ptFilesb,
							ptFile2, new String[] {
							"id","glId" });
					ptFile2.setBanci(samept.getBanci());
					ptFile2.setProcesstsbfileId(ptFilesb.getId());
					if("试制".equals(samept.getProductStyle())){
						ptFile2.setGlId(newprocesst.getId());
					}
					totalDao.save(ptFile2);
				}
			}
		}
	}
	/**
	 * 比对并复制预编译BOM零件图纸到工程BOM零件图纸
	 * @param tbptsb
	 * @param samept
	 * @param ptFilesbList
	 * @param ptFileList
	 */
	private void compareAndcopytzfromprocesstsbtoprocesst(
			ProcardTemplatesb tbptsb, ProcardTemplate samept,
			List<ProcessTemplateFilesb> ptFilesbList,
			List<ProcessTemplateFile> ptFileList) {
		// TODO Auto-generated method stub
		//好伤心，图纸的逻辑应该是直接保留原来的作为历史然后直接添加预编译里面的图纸就可以了，完全不需要继续比对，写复杂了。。。。只能是全传ptFileList=nulll了；
		if((ptFilesbList==null||ptFilesbList.size()==0)&&(ptFileList==null||ptFileList.size()==0)){
			
		}else if(ptFilesbList!=null&&ptFilesbList.size()>0
				&&(ptFileList==null||ptFileList.size()==0)){
			//现有，原没有 全部复制
			for (ProcessTemplateFilesb ptFilesb : ptFilesbList) {
				if (ptFilesb != null) {
					ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
					BeanUtils
					.copyProperties(ptFilesb,
							ptFile2, new String[] {
							"id","glId" });
					ptFile2.setProductStyle(samept.getProductStyle());
					ptFile2.setBanci(samept.getBanci());
					ptFile2.setProcesstsbfileId(ptFilesb.getId());
					if("试制".equals(samept.getProductStyle())){
						ptFile2.setGlId(samept.getId());
					}
					totalDao.save(ptFile2);
				}
			}
		}else if(ptFileList!=null&&ptFileList.size()>0
				&&(ptFilesbList==null||ptFilesbList.size()==0)){
			//原有，现没有 全部删除
			for(ProcessTemplateFile file:ptFileList){
				totalDao.delete(file);
			}
		}else{
			List<String> fileNamesbList = new ArrayList<String>();
			List<String> fileNameList = new ArrayList<String>();
			int i=0;
			for(ProcessTemplateFilesb filesb:ptFilesbList){
				fileNamesbList.add(filesb.getFileName());
				i++;
				for(ProcessTemplateFile file:ptFileList){
					if(i==1){
						fileNameList.add(file.getFileName());
					}
					if(filesb.getFileName().equals(file.getFileName())){
						if(!file.getProcessName().equals(samept.getProName())){
							file.setProcessName(samept.getProName());
							file.setProcesstsbfileId(file.getId());
						}
						file.setBanci(samept.getBanci());
						file.setBanBenNumber(samept.getBanBenNumber());
						totalDao.update(file);
					}
				}
			}
			//删除预编译BOM没有的
			if(ptFileList!=null&&ptFileList.size()>0){
				for(ProcessTemplateFile file:ptFileList){
					if(!fileNamesbList.contains(file.getFileName())){
						totalDao.delete(file);
					}
				}
			}
			//添加工程BOM没有的
			for(ProcessTemplateFilesb filesb:ptFilesbList){
				if(!fileNameList.contains(filesb.getFileName())){
					ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
					BeanUtils
					.copyProperties(filesb,
							ptFile2, new String[] {
							"id","glId" });
					ptFile2.setProductStyle(samept.getProductStyle());
					ptFile2.setBanci(samept.getBanci());
					ptFile2.setProcesstsbfileId(filesb.getId());
					if("试制".equals(samept.getProductStyle())){
						ptFile2.setGlId(samept.getId());
					}
					totalDao.save(ptFile2);
				}
			}
		}
		
		
	}
	
	/**
	 * 比对并复制预编译BOM工序图纸到工程BOM工序图纸
	 * @param processTsb
	 * @param processT
	 * @param ptFilesbList
	 * @param ptFilesbList2
	 */
	private void compareAndcopytzfromprocesstsbtoprocesst(
			ProcessTemplatesb processTsb, ProcessTemplate processT,
			List<ProcessTemplateFilesb> ptFilesbList,
			List<ProcessTemplateFile> ptFileList) {
		// TODO Auto-generated method stub
		if(ptFilesbList!=null&&ptFilesbList.size()>0
				&&(ptFileList==null||ptFileList.size()==0)){
			//现有，原没有 全部复制
			for (ProcessTemplateFilesb ptFilesb : ptFilesbList) {
				if (ptFilesb != null) {
					ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
					BeanUtils
					.copyProperties(ptFilesb,
							ptFile2, new String[] {
							"id","glId" });
					ptFile2.setProductStyle(processT.getProcardTemplate().getProductStyle());
					ptFile2.setBanci(processT.getProcardTemplate().getBanci());
					ptFile2.setProcesstsbfileId(ptFilesb.getId());
					if("试制".equals(processT.getProcardTemplate().getProductStyle())){
						ptFile2.setGlId(processT.getId());
					}
					totalDao.save(ptFile2);
				}
			}
		}else if(ptFileList!=null&&ptFileList.size()>0
				&&(ptFilesbList==null||ptFilesbList.size()==0)){
			//原有，现没有 全部删除
			for(ProcessTemplateFile file:ptFileList){
				totalDao.delete(file);
			}
		}else{
			List<String> fileNamesbList = new ArrayList<String>();
			List<String> fileNameList = new ArrayList<String>();
			int i=0;
			if(ptFilesbList!=null&&ptFilesbList.size()>0){
				for(ProcessTemplateFilesb filesb:ptFilesbList){
					fileNamesbList.add(filesb.getFileName());
					i++;
					for(ProcessTemplateFile file:ptFileList){
						if(i==1){
							fileNameList.add(file.getFileName());
						}
						if(filesb.getFileName().equals(file.getFileName())){
							if(!file.getProcessName().equals(processT.getProcessName())){
								file.setProcessName(processT.getProcessName());
								file.setProcesstsbfileId(file.getId());
								totalDao.update(file);
							}
						}
					}
				}
			}
			//删除预编译BOM没有的
			if(ptFileList!=null&&ptFileList.size()>0){
				for(ProcessTemplateFile file:ptFileList){
					if(!fileNamesbList.contains(file.getFileName())){
						totalDao.delete(file);
					}
				}
			}
			//添加工程BOM没有的
			for(ProcessTemplateFilesb filesb:ptFilesbList){
				if(!fileNameList.contains(filesb.getFileName())){
					ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
					BeanUtils
					.copyProperties(filesb,
							ptFile2, new String[] {
							"id","glId" });
					ptFile2.setProductStyle(processT.getProcardTemplate().getProductStyle());
					ptFile2.setBanci(processT.getProcardTemplate().getBanci());
					ptFile2.setProcesstsbfileId(filesb.getId());
					if("试制".equals(processT.getProcardTemplate().getProductStyle())){
						ptFile2.setGlId(processT.getId());
					}
					totalDao.save(ptFile2);
				}
			}
		}
		
		
	}
	/**
	 * 删除下层子件
	 * @param pt
	 * @return
	 */
	public boolean tbDownDataStatus(ProcardTemplate pt) {
		Set<ProcardTemplate> sonSet = pt.getProcardTSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (ProcardTemplate son : sonSet) {
				// Set<ProcessTemplate> processSet = son.getProcessTemplate();
				// if (processSet != null && processSet.size() > 0) {
				// for (ProcessTemplate process : processSet) {
				// process.setDataStatus("删除");
				// totalDao.update(process);
				// }
				// }
				son.setDataStatus("删除");
				son.setProcardTemplate(null);
				tbDownDataStatus(son);
				totalDao.update(son);
			}
		}
		pt.setProcardTSet(null);
		totalDao.update(pt);
		return true;
	}
	@Override
	public String submitzpgbm(ProcardTemplateBanBenApply bbAply,
			List<ProcardTemplateBanBenJudges> ptbbJudgeslist, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录";
		}
		ProcardTemplateBanBenApply oldbbAppy = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if(oldbbAppy==null){
			return "没有找到目标设变单!";
		}
		if(!oldbbAppy.getProcessStatus().equals("指派各部门")){
			return "当前设变单的状态为:"+oldbbAppy.getProcessStatus()+",不能进行此操作!";
		}
		String nowTime = Util.getDateTime();
		Float fsyCount = (Float) totalDao.getObjectByCondition("select count(*) from ProcessAboutBanBenApply where pabb.id in" +
				"(select id from ProcardAboutBanBenApply where bbapplyId=?) and (clType is null or clType!='顺延')", bbAply.getId());
		//获取工程师
		ProcardTemplateBanBenJudges gcs = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType='工程师' ", oldbbAppy.getId(),user.getId());
		if(gcs==null){
			return "您不是此设变单的工程师无法进行此操作!";
		}
		gcs.setRemark(remark);
		totalDao.update(gcs);
		//获取设变需要评审的组别
		List<String> pszbList = new ArrayList<String>();// 人员组别
		// 获取设变需要评审的组别
		// 1.成本(必须)
		pszbList.add("设变成本组");
		if(fsyCount>0){
			// 2.生管()
			Float sgCount = (Float) totalDao
			.getObjectByCondition(
					"select count(*) from ProcardTemplatesbDifferenceDetail where  ptsbdId in(select id from ProcardTemplatesbDifference where  sbApplyId=?) and ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))",
					bbAply.getId());
			if (sgCount != null && sgCount > 0) {
				pszbList.add("设变生管组");
			}
			// 3.外委
			Float wwCount1 = (Float) totalDao
			.getObjectByCondition(
					"select count(*) from WaigouPlan where  type='外委' and status not in('入库','删除','取消') and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
					bbAply.getId());
			Float wwCount2 = (Float) totalDao
			.getObjectByCondition(
					"select count(*) from WaigouWaiweiPlan where status in('待入库','待激活') and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
					bbAply.getId());
			Float wwCount3 = (Float) totalDao
			.getObjectByCondition(
					"select count(*) from ProcessInforWWApplyDetail where processStatus in('预选未审批','合同待确认','外委待下单') and (dataStatus is null or dataStatus not in('删除','取消')) and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
					bbAply.getId());
			if (wwCount1 == null) {
				wwCount1 = 0f;
			}
			if (wwCount2 == null) {
				wwCount2 = 0f;
			}
			if (wwCount3 == null) {
				wwCount3 = 0f;
			}
			if ((wwCount1 + wwCount2 + wwCount3) > 0) {
				pszbList.add("设变外委组");
			}
			Float pmcCount = (Float) totalDao
			.getObjectByCondition(
					"select count(*) from ProcardTemplateAboutsb where procardStyle='外购' and kgliao!='CS' and sbApplyId=? and yongliao<>0",
					bbAply.getId());
			if (pmcCount != null && pmcCount > 0) {
				// 4.PMC()
				pszbList.add("设变物控组");
				// 5.外购
				pszbList.add("设变外购组");
			}
			//6.品质()
			//7.仓库()
			Float ckCount1 = (Float) totalDao.getObjectByCondition("select count(*) from Goods where goodsCurQuantity>0 and goodsMarkId in(select markId from ProcardTemplateAboutsb where procardStyle='外购' and yongliao<>0 and sbApplyId=? )",bbAply.getId());
			if(ckCount1!=null&&ckCount1>0){
				pszbList.add("设变仓库组");
			}
		}
		//指派人员
		Set<ProcardTemplateBanBenJudges> ptbbjSet = oldbbAppy.getPtbbjset(); 
		//此次指派了哪些组
		List<String> thiszpzb = new ArrayList<String>();
		List<ProcardTemplateBanBenJudges> cbryList = totalDao.query("from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='成本'", bbAply.getId());
		List<ProcardTemplateBanBenJudges> nsryList = totalDao.query("from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审'", bbAply.getId());
		List<Integer> cbuserIds =new ArrayList<Integer>();
		List<Integer> nsuserIds =new ArrayList<Integer>();
		for(ProcardTemplateBanBenJudges thisptbbj:ptbbJudgeslist){
			if(thisptbbj!=null&&thisptbbj.getUserId()!=null){
				AssessPersonnel aperson = (AssessPersonnel) totalDao.getObjectById(AssessPersonnel.class, thisptbbj.getUserId());
				if(aperson!=null){
					String zb = aperson.getUsersGroup().getGroupName();
					if(!thiszpzb.contains(zb)){
						thiszpzb.add(zb);
					}
					Users bzpr = (Users) totalDao.getObjectById(Users.class, aperson.getUserId());
					if(bzpr==null||bzpr.getOnWork().equals("离职")){
						throw new RuntimeException(zb+"的"+ aperson.getUserName()+"人员异常!");
					}
					boolean had=false;
					if(zb.equals("设变成本组")){
						cbuserIds.add(aperson.getUserId());
						for(ProcardTemplateBanBenJudges cbptbbj:cbryList){
							if(cbptbbj.getUserId().equals(aperson.getUserId())){
								had = true;
								break;
							}
						}
						if(!had){//添加
							ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
							ptbbj.setDept(bzpr.getDept());// 部门
							ptbbj.setUserId(bzpr.getId());// 项目工程师id
							ptbbj.setUserName(bzpr.getName());// 评审名称
							ptbbj.setUserCode(bzpr.getCode());// 用户工号
							ptbbj.setAddTime(nowTime);// 添加时间
							ptbbj.setPtbbApply(oldbbAppy);// 版本升级主表
							ptbbj.setJudgeType("成本");//
							ptbbj.setJudgedId(gcs.getId());
							ptbbjSet.add(ptbbj);
							totalDao.save(ptbbj);
							cbryList.add(ptbbj);
						}
					}else{
						nsuserIds.add(aperson.getUserId());
						for(ProcardTemplateBanBenJudges nsptbbj:nsryList){
							if(nsptbbj.getUserId().equals(aperson.getUserId())){
								had = true;
								break;
							}
						}
						if(!had){//添加
							ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
							ptbbj.setDept(bzpr.getDept());// 部门
							ptbbj.setUserId(bzpr.getId());// 项目工程师id
							ptbbj.setUserName(bzpr.getName());// 评审名称
							ptbbj.setUserCode(bzpr.getCode());// 用户工号
							ptbbj.setAddTime(nowTime);// 添加时间
							ptbbj.setPtbbApply(oldbbAppy);// 版本升级主表
							ptbbj.setJudgeType("内审");//
							ptbbj.setJudgedId(gcs.getId());
							ptbbjSet.add(ptbbj);
							totalDao.save(ptbbj);
							nsryList.add(ptbbj);
						}
					}
				}
			}
		}
		//判断是否有漏掉的组别
		StringBuffer sb = new StringBuffer();
		for(String needzb:pszbList){
			if(!thiszpzb.contains(needzb)){
				if(sb.length()==0){
					sb.append(needzb);
				}else{
					sb.append(","+needzb);
				}
			}
		}
		if(sb.length()>0){
			//临时屏蔽
			throw new RuntimeException("您还缺少指派"+sb.toString()+"的人员");
		}
		//删除多余的被指派的人员
		if(cbryList!=null&&cbryList.size()>0){
			for(ProcardTemplateBanBenJudges cbry:cbryList){
				if(cbry.getId()!=null&&!cbuserIds.contains(cbry.getUserId())){
					cbry.setPtbbApply(null);
					ptbbjSet.remove(cbry);
					totalDao.delete(cbry);
				}
			}
		}
		if(nsryList!=null&&nsryList.size()>0){
			for(ProcardTemplateBanBenJudges nsry:nsryList){
				if(nsry.getId()!=null&&!nsuserIds.contains(nsry.getUserId())){
					nsry.setPtbbApply(null);
					ptbbjSet.remove(nsry);
					totalDao.delete(nsry);
				}
			}
		}
		Integer[] userId = new Integer[cbuserIds.size()];
		for(int i=0;i<cbuserIds.size();i++){
			userId[i]=cbuserIds.get(i);
		}
		if (userId != null) {// 发送消息
			AlertMessagesServerImpl.addAlertMessages("设变成本评审", "总成为:"
					+ oldbbAppy.getMarkId() + ",业务件号为："
					+ oldbbAppy.getYwMarkId() + "的BOM工程师评审结束" + "请前往处理.设变单号:"+oldbbAppy.getSbNumber(),
					userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ oldbbAppy.getId(), true, "no");
		}
		oldbbAppy.setLastop("指派各部门评审人员");
		oldbbAppy.setLastUserId(user.getId());
		oldbbAppy.setLastUserName(user.getName());
		oldbbAppy.setLastTime(Util.getDateTime());
		oldbbAppy.setProcessStatus("成本审核");
		totalDao.update(oldbbAppy);
		return "true";
	}
	@Override
	public String daoRuHwBom(File bomTree, String bomTreeFileName, Integer id) {
		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
			Sheet rs = null;
			try {
				rs = rwb.getSheet(1);
			} catch (IndexOutOfBoundsException e2) {
				rs = rwb.getSheet(0);
				e2.printStackTrace();
			}
			if (rs == null) {
				return "未找到需要处理的Sheet文件!";
			}
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 2) {
				String hql_wgType = "select name from Category where type = '物料' and id not in (select fatherId from Category where fatherId  is not NULL)";
				List<String> umList = totalDao.query(hql_wgType);
				StringBuffer sb = new StringBuffer();
				if (umList != null && umList.size() > 0) {
					for (String um : umList) {
						sb.append("," + um);
					}
					sb.append(",");
				}
				String umStr = sb.toString();
				// Cell[] cellsAll = rs.getRow(1);
				// String ywMarkId = cellsAll[5].getContents();// 对外件号
				// ywMarkId = ywMarkId.replaceAll(" ", "");
				// ywMarkId = ywMarkId.replaceAll("	", "");
				// String createUser = cellsAll[6].getContents();// 创建人
				// createUser = createUser.replaceAll(" ", "");
				// createUser = createUser.replaceAll("	", "");
				// String createTime = cellsAll[7].getContents();// 创建时间
				// createTime = createTime.replaceAll(" ", "");
				// createTime = createTime.replaceAll("	", "");
				// String lastUser = cellsAll[8].getContents();// 最后修改人
				// lastUser = lastUser.replaceAll(" ", "");
				// lastUser = lastUser.replaceAll("	", "");
				Cell[] ywcells = rs.getRow(3);
				String ywMarkId = ywcells[4].getContents();
				if (ywMarkId != null && ywMarkId.length() > 0) {
					ywMarkId = ywMarkId.replaceAll(" ", "");
					ywMarkId = ywMarkId.replaceAll("：", ":");
					// ywMarkId = ywMarkId.replaceAll("（", "(");
					// ywMarkId = ywMarkId.replaceAll("）", ")");
					// ywMarkId = ywMarkId.replaceAll("x", "X");
					String[] ywMarkIds = ywMarkId.split(":");
					if (ywMarkIds != null && ywMarkIds.length == 2) {
						ywMarkId = ywMarkIds[1];
					} else {
						ywMarkId = null;
					}
				}
				List<ProcardTemplatesb> ptList = new ArrayList<ProcardTemplatesb>();
				boolean isbegin = false;
				ProcardTemplate zzpt = null;// 当做表示
				int base = 0;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
				for (int i = base; i < excelcolumns; i++) {
					ProcardTemplatesb pt = new ProcardTemplatesb();
					pt.setYwMarkId(ywMarkId);
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String number = "";// 序号
					try {
						number = cells[0].getContents();// 
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					number = number.replaceAll(" ", "");
					number = number.replaceAll("	", "");
					// if (number.length() == 0) {
					// if (isbegin) {
					// break;
					// } else {
					// base++;
					// continue;
					// }
					// }else if(number.equals("序号")){
					// base++;
					// continue;
					// }
					try {
						Integer num = Integer.parseInt(number);
						isbegin = true;
					} catch (Exception e) {
						// TODO: handle exception
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					String markId = cells[3].getContents();// 件号
					if (markId != null) {
						markId = markId.replaceAll(" ", "");
						markId = markId.replaceAll("	", "");
						markId = markId.replaceAll("_", "-");
						markId = markId.replaceAll("_s", "-S");
						markId = markId.replaceAll("-s", "-S");
						markId = markId.replaceAll("dkba", "DKBA");
						// markId = markId.replaceAll("（", "(");
						// markId = markId.replaceAll("）", ")");
						// markId = markId.replaceAll("x", "X");
					}
					String hwdrMarkId = cells[2].getContents();// 件号
					if (hwdrMarkId != null) {
						hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
						hwdrMarkId = hwdrMarkId.replaceAll("	", "");
						hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
						hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
						// hwdrMarkId = hwdrMarkId.replaceAll("（", "(");
						// hwdrMarkId = hwdrMarkId.replaceAll("）", ")");
						// hwdrMarkId = hwdrMarkId.replaceAll("x", "X");
					}
					boolean zouzui = true;
					if (markId == null || markId.length() == 0) {
						// markId="test"+i;
						jymsg += "第" + (i + 1) + "行,件号不能为空!</br>";
						// if (hwdrMarkId==null||hwdrMarkId.length() == 0) {
						// throw new RuntimeException("件号不能为空,第" + (i + 1)
						// + "行错误!");
						// }else{
						// markId=hwdrMarkId;
						// }
					} else {
						if (markId.startsWith("DKBA") && markId.length() >= 12
								&& !markId.equals("DKBA3359")
								&& markId.indexOf(".") < 0) {
							markId = markId.substring(0, 5) + "."
									+ markId.substring(5, 8) + "."
									+ markId.substring(8, markId.length());
						}
						if ((markId.equals("DKBA0.480.0128")
								|| markId.equals("DKBA0.480.0251")
								|| markId.equals("DKBA0.480.0236")
								|| markId.equals("DKBA0.480.0345")
								|| markId.equals("DKBA0.480.1381") || markId
								.equals("*"))
								&& hwdrMarkId != null
								&& hwdrMarkId.length() > 0) {
							zouzui = false;
							markId = hwdrMarkId;
						}
					}
					String banben = "";
					try {
						banben = cells[4].getContents();// 版本
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
					if (!zouzui) {
						banben = null;
					}
					String proName = cells[5].getContents();// 名称
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					// proName = proName.replaceAll("（", "(");
					// proName = proName.replaceAll("）", ")");
					// proName = proName.replaceAll("x", "X");
					Integer belongLayer = null;// 层数
					Float corrCount = null;// 消耗量
					for (int jc = 6; jc <= 15; jc++) {
						String xiaohao = cells[jc].getContents();
						if (xiaohao != null && xiaohao.length() > 0) {
							try {
								corrCount = Float.parseFloat(xiaohao);
								belongLayer = jc - 5;
								break;
							} catch (Exception e) {
								jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
							}
						}
						// else if(xiaohao==null||xiaohao.length()==0){
						// jymsg+= "消耗量有异常,第" + (i + 1)
						// + "行错误!</br>";
						// }
					}
					if (corrCount == null || corrCount == 0) {
						jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
					}
					String source = cells[16].getContents();// 来源
					String unit = cells[18].getContents();// 单位
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = cells[19].getContents();// 表处
						if ("A000".equals(biaochu)) {
							biaochu = "";
						}
						// biaochu = biaochu.replaceAll("）", ")");
						// biaochu = biaochu.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String clType = "";
					try {
						clType = cells[20].getContents();// 材料类别（材质）
						// if ("DKBA8.052.6323".equals(markId)) {
						// System.out
						// .println(markId + " ====================");
						// }
						// clType = clType.replaceAll("（", "(");
						// clType = clType.replaceAll("）", ")");
						// clType = clType.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String thislengthStr = cells[21].getContents();
					String thiswidthStr = cells[22].getContents();
					String thishigthStr = cells[23].getContents();
					Float thislength = null;
					Float thiswidth = null;
					Float thishigth = null;
					try {
						thislength = Float.parseFloat(thislengthStr);

					} catch (Exception e) {
					}
					try {
						thiswidth = Float.parseFloat(thiswidthStr);
					} catch (Exception e) {
					}
					try {
						thishigth = Float.parseFloat(thishigthStr);
					} catch (Exception e) {
					}
					String loadMarkId = "";
					try {
						loadMarkId = cells[24].getContents();// 初始总成(备注)
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					loadMarkId = loadMarkId.replaceAll("（", "(");
					loadMarkId = loadMarkId.replaceAll("）", ")");
					loadMarkId = loadMarkId.replaceAll("x", "X");
					loadMarkId = loadMarkId.replaceAll("_", "-");
					String wgType = null;// 外购件类型
					try {
						wgType = cells[25].getContents();// 外购件类型
						wgType = wgType.replaceAll(" ", "");
						wgType = wgType.replaceAll("	", "");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String specification = null;// 规格
					try {
						specification = cells[26].getContents();// 外购件类型
						specification = specification.replaceAll(" ", "");
						specification = specification.replaceAll("	", "");
						specification = specification.replaceAll("（", "(");
						specification = specification.replaceAll("）", ")");
						specification = specification.replaceAll("x", "X");
						specification = specification.replaceAll("_", "-");
						specification = specification.replaceAll("，", ",");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					for (int n = 10; n < 14; n++) {
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						proName = proName.replaceAll(String.valueOf((char) n),
								"");
						// tuhao = tuhao.replaceAll(String.valueOf((char) n),
						// "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
						// type = type.replaceAll(String.valueOf((char) n), "");
					}
					pt.setSpecification(specification);
					pt.setProcardStyle("待定");
					boolean hasTrue = false;
					String useguige = specification;
					if (useguige == null || useguige.length() == 0) {
						useguige = loadMarkId;
					}
					if (markId.startsWith("DKBA0")
							|| markId.startsWith("dkba0")) {// 标准件不启用版本号
						banben = null;
					}
					// 技术规范号查找大类
					String procardStyle = null;
					List<String> sameMax = bmmap.get(markId + useguige);
					if (sameMax != null) {
						proName = sameMax.get(3);
						procardStyle = sameMax.get(0);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)) );
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)) );
							pt.setProcardStyle("外购");
							// }
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}
					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ useguige
											+ "' or tuhao ='"
											+ markId
											+ useguige
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ useguige + "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setProcardStyle("外购");
								pt.setTuhao(markId + "-" + useguige);
								pt.setSpecification(useguige);
								markId = hasMarkId;
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购件";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and (tuhao ='"
												+ markId
												+ "-"
												+ useguige
												+ "' or tuhao ='"
												+ markId
												+ useguige
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ useguige + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									markId = hasMarkId;
									dlType = "自制";
								} else {
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
											pt.setProcardStyle("外购");
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购件";
											pt.setProcardStyle("外购");
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%'");
										String maxNo4 = (String) totalDao
										.getObjectByCondition("select max(markId) from ProcardTemplatesb where sbApplyId " +
													"in(select id from ProcardTemplateBanBenApply where processStatus not in('设变发起','分发项目组','项目主管初评','资料更新','关联并通知生产','生产后续','关闭','取消'))" +
												" and markId like'"
												+ jsType
												+ "%' and markId not like '"
												+ jsType + "%.%' ");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo4 != null
												&& maxNo4.length() > 0) {
											String[] maxStrs = maxNo4
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									pt.setSpecification(useguige);
									pt.setTuhao(markId + "-" + useguige);
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + useguige, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}

									pt.setShowSize(loadMarkId);
								}
								hasTrue = true;
								// totalDao.clear();
							}
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}

					}
					// if (loadMarkId != null) {
					// if (zouzui
					// & (!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345")
					// && !markId.equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					if (!hasTrue) {
						// 尝试将国标markId转换为系统使用的markId
						Object[] change = (Object[]) totalDao
								.getObjectByCondition(
										"select valueCode,valueName from CodeTranslation where keyCode=? or valueCode=? and type='国标'",
										markId, markId);
						if (change != null && change.length == 2
								&& change[0] != null
								&& change[0].toString().length() > 0) {
							pt.setTuhao(markId);
							markId = change[0].toString();
							if (change[1] != null
									&& change[1].toString().length() > 0) {
								proName = change[1].toString();
							}
							banben = null;// 国标件不要版本
						}
						if ("外购".equals(source) || "原材料".equals(source)) {
							ChartNOSC chartnosc = (ChartNOSC) totalDao
									.getObjectByCondition(
											" from ChartNOSC where chartNO  =? ",
											markId);
							if (chartnosc == null && isChartNOGzType(markId)) {
								// 同步添加到编码库里
								chartnosc = new ChartNOSC();
								chartnosc.setChartNO(markId);
								chartnosc.setChartcode(Util
										.Formatnumber(markId));
								chartnosc.setIsuse("YES");
								chartnosc.setRemak("BOM导入");
								chartnosc.setSjsqUsers(user.getName());
								totalDao.save(chartnosc);
							}
						}

					}
					if (markId != null
							&& (markId.startsWith("GB") || markId
									.startsWith("gb"))) {
						jymsg += "第" + (i + 1) + "行,国标件:" + markId
								+ "无法在国标编码库中查到!</br>";
					}

					if (markId != null
							&& (markId.startsWith("DKBA0") || markId
									.equals("DKBA3359"))) {

						jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
								+ "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					// pt.setSpecification(type);
					if (source != null) {
						if (source.equals("外购") || source.equals("原材料")) {
							pt.setProcardStyle("外购");
						} else if (source.equals("半成品")) {
							pt.setProcardStyle("外购");
							pt.setNeedProcess("yes");
						} else if (source.equals("生产") || source.equals("自制")
								|| source.equals("组合")) {
							pt.setProcardStyle("自制");
						}
					}
					if (procardStyle != null) {
						pt.setProcardStyle(procardStyle);
					}
					if (pt.getProcardStyle() == null
							|| pt.getProcardStyle().length() == 0) {
						pt.setProcardStyle("待定");
					}
					if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, pt
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(pt.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + pt.getProcardStyle() + "]类型:["
									+ procardStyle0 + "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					if (pt.getTuhao() == null || pt.getTuhao().length() <= 0) {
						pt.setTuhao(hwdrMarkId);
					}

					// if (bizhong != null && bizhong.length() > 0) {
					// pt.setBili(Float.parseFloat(bizhong.toString()));
					// }
					pt.setAddcode(user.getCode());
					pt.setAdduser(user.getName());
					pt.setAddtime(nowTime);
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("待编制");
					pt.setBianzhiId(user.getId());
					pt.setBianzhiName(user.getName());
					pt.setProductStyle("批产");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setThisLength(thislength);
					pt.setThisWidth(thiswidth);
					pt.setThisHight(thishigth);
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  productStyle='批产' and (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							if (wgType != null && wgType.length() > 0) {
								if (!haswgType.equals(wgType)) {
									jymsg += "第" + (i + 1) + "行错误，物料类别:"
											+ wgType + "与现有物料类别:" + haswgType
											+ "不符<br/>";
								}
							} else {
								wgType = haswgType;
							}
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							} else if (umStr == null || umStr.length() == 0
									|| !umStr.contains("," + wgType + ",")) {
								jymsg += "第" + (i + 1)
										+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if (wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) and avgDeliveryTime is not null order by id desc ",
										wgType);
						if (category != null) {
							if (category.getAvgProductionTakt() != null
									&& category.getAvgDeliveryTime() != null) {
								pt.setAvgProductionTakt(category
										.getAvgProductionTakt());
								pt.setAvgDeliveryTime(category
										.getAvgDeliveryTime());
							} else {
								jymsg += "第" + (i + 1) + "行错误，该外购件物料类别("
										+ wgType + "),未填写生产节拍和配送时长!<br/>";
							}
						} else {
							jymsg += "第" + (i + 1)
									+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
						}
					}
					ptList.add(pt);
				}

				if (ptList.size() > 0) {
					ProcardTemplatesb totalPt = null;
					if (id != null) {// 表示导入子BOM
						totalPt = (ProcardTemplatesb) totalDao.getObjectById(
								ProcardTemplatesb.class, id);
						if (totalPt == null) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getProductStyle().equals("外购件!")) {
							throw new RuntimeException("上层零件为外购件无法导入子件!</br>");
						}
						if (totalPt.getBanbenStatus() != null
								&& totalPt.getBanbenStatus().equals("历史")) {
							throw new RuntimeException("上层零件为历史件无法导入子件!</br>");
						}
						if (totalPt.getDataStatus() != null
								&& totalPt.getDataStatus().equals("删除")) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						ywMarkId = totalPt.getYwMarkId();
					}
					String msg = "";
					Integer sbCount = ptList.size();
					if (ptList.size() >= 1) {
						// 获取关联ProcardTemplate,关联外购件库数据
						StringBuffer markIdsb = new StringBuffer();
						for (ProcardTemplatesb nowpt : ptList) {
							if (id != null) {
								nowpt.setBelongLayer(totalPt.getBelongLayer()
										+ nowpt.getBelongLayer());
							}
							if (markIdsb.length() == 0) {
								markIdsb.append("'" + nowpt.getMarkId() + "'");
							} else {
								markIdsb.append(",'" + nowpt.getMarkId() + "'");
							}
						}
						if (markIdsb.length() > 0) {
							aboutptsbList = totalDao
							.query(" from ProcardTemplatesb where id in("
									+ "select min(id) from ProcardTemplatesb where sbApplyId=? and (dataStatus is null or dataStatus !='删除') and markId in("
									+ markIdsb.toString()
									+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId",totalPt.getSbApplyId());
//							aboutptsbList = new ArrayList<ProcardTemplatesb>();
							aboutptList = totalDao
									.query(" from ProcardTemplate where id in("
											+ "select min(id) from ProcardTemplate where productStyle='批产' and (dataStatus is null or dataStatus !='删除') and markId in("
											// +
											// "select min(id) from ProcardTemplate where productStyle='批产' and  markId in("
											+ markIdsb.toString()
											+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId");
							aboutwgjList = totalDao
									.query("from YuanclAndWaigj where productStyle='批产' and markId in("
											+ markIdsb.toString()
											+ ") and (banbenStatus is null or banbenStatus !='历史' ) order by markId");
						}
						if (id != null) {
							msg += addProcardTemplateTreeK3(totalPt, ptList,
									totalPt.getRootId(), -1, totalPt
											.getRootMarkId(), ywMarkId, base,
									2, user);
						} else {
							msg += addProcardTemplateTreeK3(totalPt, ptList,
									totalPt.getRootId(), 0,
									totalPt.getMarkId(), ywMarkId, base, 2,
									user);

						}
					}

					if (jymsg.length() > 0 || msg.length() > 0) {
						String msg0 = "板材粉末未计算信息:<br/>" + jsbcmsg;
						jsbcmsg = "";
						throw new RuntimeException(jymsg + msg + msg0);
					} else {
						String nowtime = Util.getDateTime();
						for (ProcardTemplatesb hasHistory : ptList) {
							hasHistory.setAddcode(user.getCode());
							hasHistory.setAdduser(user.getName());
							hasHistory.setAddtime(nowTime);
							if (hasHistory.getHistoryId() != null) {
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
											.query(
													"from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus!='删除')",
													hasHistory.getHistoryId());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplatesb> nowSonSet = hasHistory
												.getProcardsbTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplatesb nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplate historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcard(hasHistory,
															historySon, "批产");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}else if(hasHistory.getHistoryId2() != null){
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplatesb> historySonList = (List<ProcardTemplatesb>) totalDao
											.query(
													"from ProcardTemplatesb where fatherId=?",
													hasHistory.getHistoryId2());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplatesb> nowSonSet = hasHistory
												.getProcardsbTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplatesb nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplatesb historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcardsb(hasHistory,
															historySon, "批产");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else {
								if (hasHistory.getId() != null) {
									if (hasHistory.getBanci() == null) {
										hasHistory.setBanci(0);
									}
									if (hasHistory.getBanbenStatus() == null) {
										hasHistory.setBanbenStatus("默认");
									}
									if (hasHistory.getBzStatus() == null
											|| hasHistory.getBzStatus().equals(
													"初始")
											|| hasHistory.getBzStatus().equals(
													"待编制")) {
										hasHistory.setBzStatus("待编制");
										hasHistory.setBianzhiId(user.getId());
										hasHistory.setBianzhiName(user
												.getName());
										totalDao.update(hasHistory);
									}
								}
							}
						}
						// if (id != null) {
						// Set<ProcardTemplate> sons = totalPt
						// .getProcardTSet();
						// if (sons != null) {
						// for (ProcardTemplate son : sons) {
						// if (son.getId() >= thisminId) {
						// ProcardTemplateChangeLog.addchangeLog(
						// totalDao, totalPt, son, user,
						// Util.getDateTime());
						// }
						// }
						// }
						// }
						if (jsbcmsg.length() > 0) {
							String msg0 = jsbcmsg;
							jsbcmsg = "";
							return "导入成功,识别了" + sbCount + "条数据!<br/>"
									+ "板材粉末未计算信息:" + msg0;
						}

						return "导入成功,识别了" + sbCount + "条数据!";
					}
				} else if (jymsg.length() > 0) {
					return jymsg;
				}
			} else {
				return "没有数据!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "文件异常,导入失败!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}
	@Override
	public String daoRuHwSZBom(File bomTree, String bomTreeFileName, Integer id) {

		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
			Sheet rs = rwb.getSheet(1);// 获得第二张Sheet表
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 2) {
				Cell[] ywcells = rs.getRow(3);
				String ywMarkId = ywcells[4].getContents();
				if (ywMarkId != null && ywMarkId.length() > 0) {
					ywMarkId = ywMarkId.replaceAll(" ", "");
					ywMarkId = ywMarkId.replaceAll("：", ":");
					String[] ywMarkIds = ywMarkId.split(":");
					if (ywMarkIds != null && ywMarkIds.length == 2) {
						ywMarkId = ywMarkIds[1];
					} else {
						ywMarkId = null;
					}
				}
				List<ProcardTemplatesb> ptList = new ArrayList<ProcardTemplatesb>();
				boolean isbegin = false;
				int base = 0;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				// 获取到的技术规范类的最大下标
				Map<String, Integer> jsgfmap = new HashMap<String, Integer>();
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
				for (int i = base; i < excelcolumns; i++) {
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String number = "";// 序号
					try {
						number = cells[0].getContents();// 
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					number = number.replaceAll(" ", "");
					number = number.replaceAll("	", "");
					try {
						Integer num = Integer.parseInt(number);
						isbegin = true;
					} catch (Exception e) {
						// TODO: handle exception
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					String markId = cells[3].getContents();// 件号
					if (markId != null) {
						markId = markId.replaceAll(" ", "");
						markId = markId.replaceAll("	", "");
						markId = markId.replaceAll("_", "-");
						markId = markId.replaceAll("dkba", "DKBA");
					}
					String hwdrMarkId = cells[2].getContents();// 件号
					if (hwdrMarkId != null) {
						hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
						hwdrMarkId = hwdrMarkId.replaceAll("	", "");
						hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
						hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
					}
					if (markId == null || markId.length() == 0) {
						// markId="test"+i;
						jymsg += "件号不能为空,第" + (i + 1) + "行错误!</br>";
						// if (hwdrMarkId==null||hwdrMarkId.length() == 0) {
						// throw new RuntimeException("件号不能为空,第" + (i + 1)
						// + "行错误!");
						// }else{
						// markId=hwdrMarkId;
						// }
					} else if ((markId.equals("DKBA0.480.0128")
							|| markId.equals("DKBA0.480.0251")
							|| markId.equals("DKBA0.480.0236")
							|| markId.equals("DKBA0.480.0345")
							|| markId.equals("DKBA0.480.1381") || markId
							.equals("*"))
							&& hwdrMarkId != null && hwdrMarkId.length() > 0) {
						markId = hwdrMarkId;
					}
					if (markId.startsWith("DKBA") && !markId.equals("DKBA3359")
							&& markId.length() >= 12 && markId.indexOf(".") < 0) {
						markId = markId.substring(0, 5) + "."
								+ markId.substring(5, 8) + "."
								+ markId.substring(8, markId.length());
					}
					String banben = "";
					try {
						banben = cells[4].getContents();// 版本
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
					String proName = cells[5].getContents();// 名称
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					proName = proName.trim();
					Integer belongLayer = null;// 层数
					Float corrCount = null;// 消耗量
					for (int jc = 6; jc <= 15; jc++) {
						String xiaohao = cells[jc].getContents();
						if (xiaohao != null && xiaohao.length() > 0) {
							try {
								corrCount = Float.parseFloat(xiaohao);
								belongLayer = jc - 5;
								break;
							} catch (Exception e) {
								jymsg += "消耗量有异常,第" + (i + 1) + "行错误!</br>";
							}
							// }else if(xiaohao==null||xiaohao.length()==0){
							// jymsg+= "消耗量有异常,第" + (i + 1)
							// + "行错误!</br>";
						}
					}
					if (belongLayer == null) {
						jymsg += "层数有异常,第" + (i + 1) + "行错误!</br>";
					}
					if (corrCount == null || corrCount == 0) {
						jymsg += "消耗量有异常,第" + (i + 1) + "行错误!</br>";
					}
					ProcardTemplatesb pt = new ProcardTemplatesb();
					pt.setYwMarkId(ywMarkId);
					String source = cells[16].getContents();// 来源
					String unit = cells[18].getContents();// 单位
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = cells[19].getContents();// 表处
					} catch (Exception e) {
						e.printStackTrace();
					}
					String clType = "";
					try {
						clType = cells[20].getContents();// 材料类别（材质）
					} catch (Exception e) {
						e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String thislengthStr = cells[21].getContents();
					String thiswidthStr = cells[22].getContents();
					String thishigthStr = cells[23].getContents();
					Float thislength = null;
					Float thiswidth = null;
					Float thishigth = null;
					try {
						thislength = Float.parseFloat(thislengthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						thiswidth = Float.parseFloat(thiswidthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						thishigth = Float.parseFloat(thishigthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					String loadMarkId = "";
					try {
						loadMarkId = cells[24].getContents();// 初始总成
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					loadMarkId = loadMarkId.replaceAll("_", "-");
					loadMarkId = loadMarkId.replaceAll("X", "x");
					String wgType = null;// 外购件类型
					try {
						wgType = cells[25].getContents();// 外购件类型
						wgType = wgType.replaceAll(" ", "");
						wgType = wgType.replaceAll("	", "");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String specification = null;// 规格
					try {
						specification = cells[26].getContents();// 外购件类型
						specification = specification.replaceAll(" ", "");
						specification = specification.replaceAll("	", "");
						specification = specification.replaceAll("（", "(");
						specification = specification.replaceAll("）", ")");
						specification = specification.replaceAll("x", "X");
						specification = specification.replaceAll("_", "-");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					for (int n = 10; n < 14; n++) {
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						if (proName != null && proName.length() > 0) {
							proName = proName.replaceAll(String
									.valueOf((char) n), "");
						}
						// tuhao = tuhao.replaceAll(String.valueOf((char) n),
						// "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
						// type = type.replaceAll(String.valueOf((char) n), "");
					}
					pt.setSpecification(specification);
					pt.setProcardStyle("待定");
					boolean hasTrue = false;
					// 技术规范号查找大类
					String procardStyle = null;
					String userguige = specification;
					if (userguige == null || userguige.length() == 0) {
						userguige = loadMarkId;
					}
					if ((markId.startsWith("DKBA0") || markId
							.startsWith("dkba0"))
							|| (specification != null && (specification
									.startsWith("DKBA0") || specification
									.startsWith("dkba0")))) {// 标准件不启用版本号
						banben = null;
					}
					List<String> sameMax = bmmap.get(markId + userguige);
					if (sameMax != null) {
						proName = sameMax.get(3);
						procardStyle = sameMax.get(0);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setTuhao(markId + "-" + userguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)) );
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setTuhao(markId + "-" + userguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)) );
							pt.setProcardStyle("外购");
							// }
						}
						hasTrue = true;
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}
					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ userguige
											+ "' or tuhao ='"
											+ markId
											+ userguige
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ userguige + "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setTuhao(markId + "-" + userguige);
								markId = hasMarkId;
								pt.setProcardStyle("外购");
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and "
												+ "(tuhao ='"
												+ markId
												+ "-"
												+ userguige
												+ "' or tuhao ='"
												+ markId
												+ userguige
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ userguige + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									markId = hasMarkId;
								} else {
									String jsgfNo = markId;
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											pt.setProcardStyle("外购");
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购";
											pt.setProcardStyle("外购");
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%' ");
										String maxNo4 = (String) totalDao
										.getObjectByCondition("select max(markId) from ProcardTemplatesb where sbApplyId " +
													"in(select id from ProcardTemplateBanBenApply where processStatus not in('设变发起','分发项目组','项目主管初评','资料更新','关联并通知生产','生产后续','关闭','取消'))" +
												" and markId like'"
												+ jsType
												+ "%' and markId not like '"
												+ jsType + "%.%' ");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											try {
												Integer nowMax = Integer
														.parseInt(maxStrs[1]);
												if (maxInteger < nowMax) {
													maxInteger = nowMax;
												}
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo4 != null
												&& maxNo4.length() > 0) {
											String[] maxStrs = maxNo4
											.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
													.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									pt.setTuhao(markId + "-" + userguige);
									pt.setSpecification(userguige);
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + userguige, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}
									pt.setShowSize(loadMarkId);
								}
								hasTrue = true;
								// totalDao.clear();
							}
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}

					}
					// if (loadMarkId != null) {
					// if ((!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345") && !markId
					// .equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					if (!hasTrue) {
						// 尝试将国标markId转换为系统使用的markId
						Object[] change = (Object[]) totalDao
								.getObjectByCondition(
										"select valueCode,valueName from CodeTranslation where keyCode=? or valueCode=? and type='国标'",
										markId, markId);
						if (change != null && change.length == 2
								&& change[0] != null
								&& change[0].toString().length() > 0) {
							pt.setTuhao(markId);
							markId = change[0].toString();
							if (change[1] != null
									&& change[1].toString().length() > 0) {
								proName = change[1].toString();
							}
							banben = null;// 国标件不要版本
						}
						if ("外购".equals(source) || "原材料".equals(source)) {
							ChartNOSC chartnosc = (ChartNOSC) totalDao
									.getObjectByCondition(
											" from ChartNOSC where chartNO  =? ",
											markId);
							if (chartnosc == null && isChartNOGzType(markId)) {
								// 同步添加到编码库里
								chartnosc = new ChartNOSC();
								chartnosc.setChartNO(markId);
								chartnosc.setChartcode(Util
										.Formatnumber(markId));
								chartnosc.setIsuse("YES");
								chartnosc.setRemak("BOM导入");
								chartnosc.setSjsqUsers(user.getName());
								totalDao.save(chartnosc);
							}
						}

					}
					if (markId != null
							&& (markId.startsWith("GB") || markId
									.startsWith("gb"))) {
						jymsg += "第" + (i + 1) + "行,国标件:" + markId
								+ "无法在国标编码库中查到!</br>";
					}

					if (markId != null
							&& (markId.startsWith("DKBA0") || markId
									.equals("DKBA3359"))) {
						jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
								+ "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					// pt.setSpecification(type);

					if (source != null && source.length() > 0) {
						if (source.equals("外购") || source.equals("半成品")) {
							pt.setProcardStyle("外购");
						} else if (source.equals("生产") || source.equals("自制")) {
							pt.setProcardStyle("自制");
						}
					}
					if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, pt
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(pt.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + pt.getProcardStyle() + "]类型,:["
									+ procardStyle0 + "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					// pt.setTuhao(tuhao);
					// if (bizhong != null && bizhong.length() > 0) {
					// pt.setBili(Float.parseFloat(bizhong.toString()));
					// }
					pt.setAddcode(user.getCode());
					pt.setAdduser(user.getName());
					pt.setAddtime(nowTime);
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("初始");
					pt.setProductStyle("试制");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setThisLength(thislength);
					pt.setThisWidth(thiswidth);
					pt.setThisHight(thishigth);
					if ("外购".equals(pt.getProcardStyle())) {
						String wgType2 = (String) totalDao
								.getObjectByCondition(
										"select wgType from ProcardTemplate where markId = ? and wgType is not null and wgType <> '' ",
										markId);
						if (wgType2 != null && wgType2.length() > 0) {
							// if (wgType != null && wgType.length() > 0) {
							// if(!wgType2.equals(wgType)){
							// jymsg += "第" + (i + 1) + "行错误，物料类别:" + wgType
							// + "与现有BOM物料类别:" + wgType2 + "不符<br/>";
							// }
							// } else {
							wgType = wgType2;
							// }
						}
					}
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							// if (wgType != null && wgType.length() > 0) {
							// if(!haswgType.equals(wgType)){
							// jymsg += "第" + (i + 1) + "行错误，物料类别:" + wgType
							// + "与现有外购件库中物料类别:" + haswgType + "不符<br/>";
							// }
							// } else {
							wgType = haswgType;
							// }
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if (wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) order by id desc ",
										wgType);
						if (category != null) {
							if (category.getAvgProductionTakt() != null
									&& category.getAvgDeliveryTime() != null) {
								pt.setAvgProductionTakt(category
										.getAvgProductionTakt());
								pt.setAvgDeliveryTime(category
										.getAvgDeliveryTime());
								// } else {
								// jymsg += "第" + (i + 1) + "行错误，该外购件物料类别("
								// + wgType + "),未填写生产节拍和配送时长!<br/>";
							}
						} else {
							jymsg += "第" + (i + 1) + "行错误，该外购件物料类别(" + wgType
									+ "),不在现有物料类别库中!<br/>";
						}
					}
					ptList.add(pt);
				}
				Integer sbCount = ptList.size();
				List<ProcardTemplate> sametotalptList = new ArrayList<ProcardTemplate>();
				if (ptList.size() > 0) {
					ProcardTemplatesb totalPt = null;
					if (id != null) {// 表示导入子BOM
						totalPt = (ProcardTemplatesb) totalDao.getObjectById(
								ProcardTemplatesb.class, id);
						if (totalPt == null) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getProductStyle().equals("外购件!")) {
							throw new RuntimeException("上层零件为外购件无法导入子件!</br>");
						}
						if (totalPt.getBanbenStatus() != null
								&& totalPt.getBanbenStatus().equals("历史")) {
							throw new RuntimeException("上层零件为历史件无法导入子件!</br>");
						}
						if (totalPt.getDataStatus() != null
								&& totalPt.getDataStatus().equals("删除")) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getBzStatus() != null
								&& totalPt.getBzStatus().equals("已批准")) {
							throw new RuntimeException("上层零件已批准不允许导入子件!</br>");
						}
						ywMarkId = totalPt.getYwMarkId();
						// 增加日志

					}
					String msg = "";

					if (ptList.size() >= 1) {

						// 获取关联ProcardTemplate,关联外购件库数据
						StringBuffer markIdsb = new StringBuffer();
						for (ProcardTemplatesb nowpt : ptList) {
							if (id != null) {
								nowpt.setBelongLayer(totalPt.getBelongLayer()
										+ nowpt.getBelongLayer());
							}
							if (markIdsb.length() == 0) {
								markIdsb.append("'" + nowpt.getMarkId() + "'");
							} else {
								markIdsb.append(",'" + nowpt.getMarkId() + "'");
							}
						}
						if (markIdsb.length() > 0) {
							aboutptList = totalDao
									.query(" from ProcardTemplate where  id in("
											+ "select min(id) from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and   markId in("
											+ markIdsb.toString()
											+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId");
//							aboutptsbList= new ArrayList<ProcardTemplatesb>();
							aboutptsbList = totalDao
							.query(" from ProcardTemplatesb where id in("
									+ "select min(id) from ProcardTemplatesb where sbApplyId=? and (dataStatus is null or dataStatus !='删除') and markId in("
									+ markIdsb.toString()
									+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId",totalPt.getSbApplyId());
							aboutwgjList = totalDao
									.query("from YuanclAndWaigj where  markId in("
											+ markIdsb.toString()
											+ ") and (banbenStatus is null or banbenStatus !='历史' ) order by markId");
						}
						List<Banbenxuhao> banbenxuhaoList = totalDao
								.query("from Banbenxuhao");
						banBenxuhaoMap = new HashMap<String, Integer>();
						for (Banbenxuhao banebnxuhao : banbenxuhaoList) {
							banBenxuhaoMap.put(banebnxuhao.getBanbenNumber(),
									banebnxuhao.getBanbenxuhao());
						}
						try {
							if (id != null) {
								msg += addProcardTemplateTreeK3(totalPt,
										ptList, totalPt.getRootId(), -1,
										totalPt.getRootMarkId(), ywMarkId,
										base, 2, user);
							} else {
								msg += addProcardTemplateTreeK3(totalPt,
										ptList, totalPt.getRootId(), 0, totalPt
												.getMarkId(), ywMarkId, base,
										2, user);
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					}

					if (msg.length() > 0 || jymsg.length() > 0) {
						String msg0 = jsbcmsg;
						jsbcmsg = "";
						throw new RuntimeException(jymsg + msg + msg0);
					} else {
						String nowtime = Util.getDateTime();
						for (ProcardTemplatesb hasHistory : ptList) {
							hasHistory.setAddcode(user.getCode());
							hasHistory.setAdduser(user.getName());
							hasHistory.setAddtime(nowTime);
//							if (id != null && hasHistory.getId() != null
//									&& hasHistory.getFatherId() != null
//									&& hasHistory.getFatherId().equals(id)) {
//								ProcardTemplateChangeLog.addchangeLog(totalDao,
//										totalPt, hasHistory, "子件", "增加", user,
//										nowtime);
//							}
//							if(hasHistory.getMarkId().equals("DKBA6.157.7672")){
//								System.out.println(hasHistory.getId()+"~!!@#"+hasHistory.getHistoryId2());
//							}
							if (hasHistory.getHistoryId() != null) {
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
											.query(
													"from ProcardTemplate where fatherId=?",
													hasHistory.getHistoryId());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplatesb> nowSonSet = hasHistory
												.getProcardsbTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplatesb nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplate historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcard(hasHistory,
															historySon, "试制");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}else if(hasHistory.getHistoryId2() != null){
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplatesb> historySonList = (List<ProcardTemplatesb>) totalDao
											.query(
													"from ProcardTemplatesb where fatherId=?",
													hasHistory.getHistoryId2());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplatesb> nowSonSet = hasHistory
												.getProcardsbTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplatesb nowSon : nowSonSet) {
												if((nowSon.getDataStatus()==null||nowSon.getDataStatus().equals("删除"))){
													hadMarkIds.add(nowSon.getMarkId());
												}
											}
										}
										for (ProcardTemplatesb historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcardsb(hasHistory,
															historySon, "试制");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else {
								if (hasHistory.getId() != null) {
									if (hasHistory.getBanci() == null) {
										hasHistory.setBanci(0);
									}
									if (hasHistory.getBanbenStatus() == null) {
										hasHistory.setBanbenStatus("默认");
									}
									if (hasHistory.getBzStatus() == null
											|| hasHistory.getBzStatus().equals(
													"初始")
											|| hasHistory.getBzStatus().equals(
													"待编制")) {
										hasHistory.setBzStatus("待编制");
										hasHistory.setBianzhiId(user.getId());
										hasHistory.setBianzhiName(user
												.getName());
										totalDao.update(hasHistory);
									}
								}
							}
						}
						if (jsbcmsg.length() > 0) {
							String msg0 = jsbcmsg;
							jsbcmsg = "";
							return "导入成功,识别了" + sbCount + "条数据!<br/>"
									+ "板材粉末未计算信息:" + msg0;
						}
						return "导入成功,识别了" + sbCount + "条数据!";
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}
	
	
	private boolean isChartNOGzType(String markId) {
		boolean flag = false;
		if (markId == null || "".equals(markId)) {
			return flag;
		}
		// 查出编码库所有编码规则的字符长度
		List<Integer> lenList = totalDao
				.query("SELECT distinct	LENGTH(type) FROM ChartNOGzType  where LENGTH(type)>0 ");
		int markId_len = markId.length();
		if (lenList != null && lenList.size() > 0) {
			out: // 跳出外循环的标志
			for (Integer len : lenList) {
				// 判断传过来的件号的字符长度与编码规则的字符长度是否相等
				if (len == markId_len) {
					// 根据该字符长度查出所有等于改长度所有编码规则
					List<ChartNOGzType> gzTypeList = totalDao.query(
							" from ChartNOGzType where  LENGTH(type) =?", len);
					if (gzTypeList != null && gzTypeList.size() > 0) {
						// 截取该字符前面两个字符
						String beforMarkId = markId.substring(0, 2);
						// 遍历所有改长度的编码规则
						for (ChartNOGzType gzType : gzTypeList) {
							String type = gzType.getType();
							// 判断该字符最前面两个是否属于在规则类型前两个
							if (type.indexOf(beforMarkId) == 0) {
								// 字符串转char数组
								char[] markIds = markId.toCharArray();
								char[] types = type.toCharArray();
								// 分别记录.出现的下标是多少；就是.所处的位置是在哪的
								String type_index = "";
								String markId_index = "";
								for (int i = 0; i < types.length; i++) {
									if ('.' == types[i]) {
										type_index += i;
									}
									if ('.' == markIds[i]) {
										markId_index += i;
									}
								}
								// 比较两则的下标是否完全一致；
								if (markId_index.equals(type_index)) {
									// 1 字符长度一致;
									// 2 前两位一致;
									// 3 分割点.所处位置一致
									// 则说明该件号所用的规则为编码规则返回true;
									flag = true;
									break out;
								}
							}
						}
					}
				}
			}
		} else {// lenList 为null 或者size（）==0说明该公司不用编码库编码直接返回false;
			flag = false;
		}
		return flag;

	}
	public String addProcardTemplateTreeK3(ProcardTemplatesb fatherPt,
			List<ProcardTemplatesb> ptList, Integer rootId, Integer i,
			String zcMarkId, String ywmarkId, int base, int bomtype, Users user) {
		String msg0 = "";
		String msg = "";
		i++;
		Set<ProcardTemplatesb> sonSet = fatherPt.getProcardsbTSet();
		if (sonSet == null) {
			sonSet = new HashSet<ProcardTemplatesb>();
		}
		List<String> markIdList = new ArrayList<String>();
		Float xh = 0f;
		String time = Util.getDateTime();
		if (i == 0 && markIdList.size() == 0) {
			markIdList = totalDao.query(
					"select markId from ProcardTemplatesb where procardTemplatesb.id=? "
							+ "and (dataStatus is null or dataStatus!='删除')",
					fatherPt.getId());
		}
		for (int j = i; j < ptList.size(); j++) {
			ProcardTemplatesb sonPt = ptList.get(j);
//			if(fatherPt.getMarkId().equals("DKBA6.157.2367")
//					&&sonPt.getMarkId().equals("DKBA8.099.8399")){
//				System.out.println(i);
//				
//			}
//			 System.out.println(fatherPt.getMarkId()+"-----"+sonPt.getMarkId());
			// System.out.println(fatherPt.getMarkId());
			// 判断是否为原材料
			YuanclAndWaigj ycl = null;
			try {
				if (sonPt.getBelongLayer() == null) {
					msg += "第" + (j + 1 + base) + "行错误,无阶层用量<br/>";
					continue;
				}
			} catch (Exception e) {
				msg += "第" + (i + base) + "行和第" + (j + 1 + base) + "出错" + e;
			}

			if (sonPt.getBelongLayer().equals(fatherPt.getBelongLayer() + 1)) {
				if (fatherPt.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
					msg += "第" + (i + base) + "行和第" + (j + 1 + base)
							+ "行错误,子零件件号不能和父零件件号相同!<br/>";
				}
				if (markIdList.contains(sonPt.getMarkId())) {
					msg += "第" + (j + 1 + base) + "行错误,同一父零件下不能有相同件号:"
							+ sonPt.getMarkId() + "的零件!<br/>";
				}
				sonPt.setSbApplyId(fatherPt.getSbApplyId());
				if (sonPt.getProcardStyle().equals("原材料")) {
					sonPt.setProcardStyle("外购");
				}
				if (sonPt.getProcardStyle().equals("半成品")) {
					sonPt.setProcardStyle("外购");
					sonPt.setNeedProcess("yes");
				}
//				//比对预编译BOM
//				//思路待定中
//				msg = compareBanben2(sonPt,aboutptsbList, ywmarkId, user,
//						(j + 1 + base));
				// 版本比对
				msg += compareBanben(sonPt, aboutptList, ywmarkId, user,
						(j + 1 + base));
				sonPt.setYwMarkId(ywmarkId);
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("待定")) {
					if (aboutwgjList != null && aboutwgjList.size() > 0) {
						for (YuanclAndWaigj aboutwgj : aboutwgjList) {
							if (sonPt.getMarkId().equals(aboutwgj.getMarkId())) {
								sonPt.setProcardStyle("外购");
								break;
							}
						}
					}

				}
				// 设置总成件号
				sonPt.setRootMarkId(zcMarkId);
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("外购")) {
					// 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
					if (sonPt.getKgliao() == null
							|| sonPt.getKgliao().length() == 0) {
						if (sonPt.getMarkId().endsWith("cs")
								|| sonPt.getMarkId().endsWith("CS")) {
							sonPt.setKgliao("CS");
						} else {
							sonPt.setKgliao("TK");
						}
					} else if ("TK".equalsIgnoreCase(sonPt.getKgliao())) {// 导入时没有默认自购
						sonPt.setKgliao("TK");
					} else if ("CS".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("CS");
					} else if ("TK AVL".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("TK AVL");
					} else if ("TK Price".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("TK Price");
					} else {
						msg += "第"
								+ (j + 1 + base)
								+ "行"
								+ sonPt.getMarkId()
								+ "--此件号的供料属性不在系统定义中（TK,CS,TK AVL,TK Price），请先处理!<br/>";
					}
					// 此版本和供料属性可用
					YuanclAndWaigj pcky = null;// 批产可用版本
					YuanclAndWaigj wgjTem = null;// 同供料属性批产可用版本
					YuanclAndWaigj wgjTem2 = null;// 同供料属性可用版本
					YuanclAndWaigj wgjTemotherkgliao = null;// 不同供料属性可用版本
					YuanclAndWaigj wgjTemotherkgliao2 = null;// 不同供料属性可用版本
					YuanclAndWaigj wgjhistory = null;// 同供料属性不可用版本
					YuanclAndWaigj wgjhistorytherkgliao = null;// 不同供料属性不可用版本
					YuanclAndWaigj wgjTemerror = null;// 除开以上六种只同件号
					if (aboutwgjList != null && aboutwgjList.size() > 0) {
						boolean checkMarkId = false;
						for (YuanclAndWaigj aboutwgj : aboutwgjList) {
							if (sonPt.getMarkId().equalsIgnoreCase(
									aboutwgj.getMarkId())) {
								checkMarkId = true;
								if (aboutwgj.getBanbenStatus() == null
										|| (!aboutwgj.getBanbenStatus().equals(
												"历史") && !"禁用".equals(aboutwgj
												.getStatus()))) {
									if(sonPt.getUnit()!=null&&sonPt.getUnit().length()>0){
										if(!Util.isEquals(sonPt.getUnit(),aboutwgj.getUnit())){
											msg += "第" + (j + 1 + base) + "行,外购件"
											+ aboutwgj.getMarkId() + "的单位为:"+sonPt.getUnit()
											+ "与现有的单位:"+aboutwgj.getUnit()+"的单位不一致!<br/>";
										}
									}
									if (aboutwgj.getProductStyle().equals("批产")) {
										pcky = aboutwgj;
									}
									if (Util.isEquals(aboutwgj.getBanbenhao(),
											sonPt.getBanBenNumber())) {
										if (Util.isEquals(aboutwgj.getKgliao(),
												sonPt.getKgliao())) {
											if (aboutwgj.getProductStyle()
													.equals("试制")) {
												wgjTem2 = aboutwgj;
											} else {
												wgjTem = aboutwgj;
											}
										} else {
											if (aboutwgj.getProductStyle()
													.equals("试制")) {
												wgjTemotherkgliao2 = aboutwgj;
											} else {
												wgjTemotherkgliao = aboutwgj;
											}
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										}
									}
								} else {
									if (Util.isEquals(aboutwgj.getBanbenhao(),
											sonPt.getBanBenNumber())) {
										if (Util.isEquals(aboutwgj.getKgliao(),
												sonPt.getKgliao())) {
											wgjhistory = aboutwgj;
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										} else {
											wgjhistorytherkgliao = aboutwgj;
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										}
									}
								}
								wgjTemerror = aboutwgj;
							} else if (checkMarkId) {
								break;
							}
						}
					}
					if (pcky != null) {
						if ("试制".equals(sonPt.getProductStyle())) {
							if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) < 0) {
								msg += "第" + (j + 1 + base) + "行"
										+ sonPt.getMarkId()
										+ "--此件号版本低于系统外购件库中版本(系统版本为:"
										+ wgjTemerror.getBanbenhao()
										+ "，导入版本为:" + sonPt.getBanBenNumber()
										+ ")，请先处理!<br/>";
							} else if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) > 0) {// 如果试制的版本号比较高
								if(wgjTem2!=null){
									//有试制的同版本同供料属性数据
									sonPt.setProName(pcky.getName());
									sonPt.setSpecification(pcky.getSpecification());
									sonPt.setTuhao(pcky.getTuhao());// 图号
									sonPt.setUnit(pcky.getUnit());// BOM用的单位
									sonPt.setTuhao(pcky.getTuhao());// 图号
									sonPt.setWgType(pcky.getWgType());
									sonPt.setClType(pcky.getCaizhi());
									sonPt.setBili(pcky.getBili());
								}else{
									boolean addnew = false;
									if (wgjTem2 == null) {
										addnew = true;
									} else if (Banbenxuhao.comparebanben(sonPt
											.getBanBenNumber(), wgjTem2
											.getBanbenhao(), banBenxuhaoMap) > 0) {
										addnew = true;
									}
									if (addnew) {
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(sonPt.getMarkId());// 件号
										wgjTem.setName(pcky.getName());// 名称
										wgjTem.setSpecification(pcky
												.getSpecification());// 规格
										wgjTem.setUnit(pcky.getUnit());// BOM用的单位
										wgjTem.setCkUnit(pcky.getCkUnit());// 仓库用的单位
										wgjTem
										.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(pcky.getCaizhi());// 材质类型
										wgjTem.setTuhao(pcky.getTuhao());// 图号
										wgjTem.setWgType(pcky.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt.getImportance());
										wgjTem.setBili(pcky.getBili());
										wgjTem.setCgperiod(sonPt.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if (wgjTem.getWgType() == null) {
											msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此外购件没有物料类别，请先处理!<br/>";
										} else {
											totalDao.save(wgjTem);
										}
										sonPt.setProName(pcky.getName());
										sonPt.setSpecification(pcky
												.getSpecification());
										sonPt.setTuhao(pcky.getTuhao());// 图号
										sonPt.setUnit(pcky.getUnit());// BOM用的单位
										sonPt.setTuhao(pcky.getTuhao());// 图号
										sonPt.setWgType(pcky.getWgType());
										sonPt.setClType(pcky.getCaizhi());
										sonPt.setBili(pcky.getBili());
										aboutwgjList.add(wgjTem);
									}
								}
							} else {// 版本号一致
								sonPt.setProName(pcky.getName());
								sonPt.setSpecification(pcky.getSpecification());
								sonPt.setTuhao(pcky.getTuhao());// 图号
								sonPt.setUnit(pcky.getUnit());// BOM用的单位
								sonPt.setTuhao(pcky.getTuhao());// 图号
								sonPt.setWgType(pcky.getWgType());
								sonPt.setClType(pcky.getCaizhi());
								sonPt.setBili(pcky.getBili());
							}
						} else {
							if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) != 0) {
								if (!"试制".equals(sonPt.getProductStyle())) {
									ProcardTemplateBanBen ptbb = (ProcardTemplateBanBen) totalDao.getObjectByCondition("from ProcardTemplateBanBen where markId=?" +
											" and procardTemplateBanBenApply.id=(select sbApplyId from ProcardTemplatesb where id=?) and uptype='版本升级'",
											sonPt.getMarkId(),rootId);
									if(ptbb==null||(!Util.isEquals(ptbb.getNewBanBenNumber(), sonPt
									.getBanBenNumber()))){
										msg += "第" + (j + 1 + base) + "行"
										+ sonPt.getMarkId()
										+ "--此件号与系统外购件库中版本不一致(系统版本为:"
										+ wgjTemerror.getBanbenhao()
										+ "，导入版本为:"
										+ sonPt.getBanBenNumber()
										+ ")，请先处理!<br/>";
									}else{
										for (ProcardTemplatesb old : aboutptsbList) {
											if (old.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
												if(!Util.isEquals(sonPt.getBanBenNumber(), old.getBanBenNumber())){
													return "第" + (j + 1 + base) + "行" + sonPt.getMarkId()
													+ "--此件号版本与系统设变BOM中的版本不一致(系统设变BOM版本为:"
													+ old.getBanBenNumber() + "，导入版本为:"
													+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
												}else{
													sonPt.setHistoryId2(old.getId());
													copyjcMsg2(old, sonPt, ywmarkId, 1, user);
												}
												break;
											}
										}
									}
								}

							} else {// 资料复制
								sonPt.setTuhao(pcky.getTuhao());// 图号以库中为标准
								sonPt.setProName(pcky.getName());
								sonPt.setUnit(pcky.getUnit());
								sonPt.setSpecification(pcky.getSpecification());
								sonPt.setLoadMarkId(pcky.getZcMarkId());
								sonPt.setImportance(pcky.getImportance());
							}
						}
					} else {
						if (wgjTem == null) {// 不存在可用、同版本、同供料属性批产外购件
							if (wgjTem2 != null) {// 存在可用同版本同供料属性试制外购件
								sonPt.setTuhao(wgjTem2.getTuhao());// 图号以库中为标准
								sonPt.setProName(wgjTem2.getName());
								sonPt.setUnit(wgjTem2.getUnit());
								sonPt.setSpecification(wgjTem2
										.getSpecification());
								sonPt.setLoadMarkId(wgjTem2.getZcMarkId());
								sonPt.setImportance(wgjTem2.getImportance());
							} else if (wgjhistory != null) {// 存在不可用、同版本、同供料属性
								msg += "外购件" + wgjhistory.getMarkId() + "的"
										+ wgjhistory.getSpecification() + "规格的"
										+ wgjhistory.getBanbenhao()
										+ "版本已禁用!<br/>";
							} else if (wgjTemotherkgliao != null) {// 存在可用、同版本、不同供料属性批产外购件则需要添加新供料属性
								wgjTem = new YuanclAndWaigj();
								wgjTem.setMarkId(wgjTemotherkgliao.getMarkId());// 件号
								wgjTem.setName(wgjTemotherkgliao.getName());// 名称
								wgjTem.setSpecification(wgjTemotherkgliao
										.getSpecification());// 规格
								wgjTem.setUnit(wgjTemotherkgliao.getUnit());// BOM用的单位
								wgjTem.setCkUnit(wgjTemotherkgliao.getCkUnit());// 仓库用的单位
								wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
								if (ywmarkId == null || ywmarkId.length() == 0) {
									wgjTem.setZcMarkId(zcMarkId);// 总成号
								} else {
									wgjTem.setZcMarkId(ywmarkId);// 总成号
								}
								wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
								wgjTem.setCaizhi(wgjTemotherkgliao.getCaizhi());// 材质类型
								wgjTem.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								wgjTem.setWgType(wgjTemotherkgliao.getWgType());
								wgjTem.setKgliao(sonPt.getKgliao());
								wgjTem.setProductStyle(sonPt.getProductStyle());
								wgjTem.setPricestatus("新增");
								wgjTem.setBanbenStatus("默认");
								wgjTem.setBili(wgjTemotherkgliao.getBili());
								wgjTem.setAddTime(time);
								wgjTem.setAddUserCode(user.getCode());
								wgjTem.setAddUserName(user.getName());
								wgjTem.setImportance(sonPt.getImportance());
								wgjTem.setAreakg(wgjTemotherkgliao.getAreakg());// 每公斤使用面积
								wgjTem.setDensity(wgjTemotherkgliao
										.getDensity());// 密度
								wgjTem.setMinkc(wgjTemotherkgliao.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
								wgjTem.setCgcount(wgjTemotherkgliao
										.getCgcount());// 安全库存采购的采购量
								wgjTem.setCgperiod(wgjTemotherkgliao
										.getCgperiod());
								wgjTem.setAvgProductionTakt(sonPt
										.getAvgProductionTakt());// 生产节拍(s)
								wgjTem.setAvgDeliveryTime(sonPt
										.getAvgDeliveryTime());// 配送时长(d)
								if(wgjTem.getWgType()==null){
									msg += "第" + (j + 1 + base) + "行"
									+ sonPt.getMarkId()
									+ "--此外购件没有物料类别，请先处理!<br/>";
								}else{
									totalDao.save(wgjTem);
								}
								if (sonPt.getProductStyle().equals("批产")) {// 生成批产外购数据时将试制设置为历史
									String wgjbanbenSql = null;
									if (wgjTem.getBanbenhao() == null
											|| wgjTem.getBanbenhao().equals("")) {
										wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
									} else {
										wgjbanbenSql = " and banbenhao='"
												+ wgjTem.getBanbenhao() + "'";
									}
									List<YuanclAndWaigj> hisszwgjList = totalDao
											.query(
													"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
															+ wgjbanbenSql,
													wgjTem.getMarkId(), wgjTem
															.getKgliao());
									if (hisszwgjList != null
											&& hisszwgjList.size() > 0) {
										for (YuanclAndWaigj hisszwgj : hisszwgjList) {
											hisszwgj.setBanbenStatus("历史");
											totalDao.update(hisszwgj);
										}
									}
								}
								sonPt.setProName(wgjTemotherkgliao.getName());
								sonPt.setSpecification(wgjTemotherkgliao
										.getSpecification());
								sonPt.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								sonPt.setUnit(wgjTemotherkgliao.getUnit());// BOM用的单位
								sonPt.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								sonPt.setWgType(wgjTemotherkgliao.getWgType());
								sonPt.setClType(wgjTemotherkgliao.getCaizhi());
								sonPt.setBili(wgjTemotherkgliao.getBili());
								aboutwgjList.add(wgjTem);
							} else if (wgjTemotherkgliao2 != null) {// 存在可用、同版本、不同供料属性试制外购件则需要添加新供料属性
								wgjTem = new YuanclAndWaigj();
								wgjTem
										.setMarkId(wgjTemotherkgliao2
												.getMarkId());// 件号
								wgjTem.setName(wgjTemotherkgliao2.getName());// 名称
								wgjTem.setSpecification(wgjTemotherkgliao2
										.getSpecification());// 规格
								wgjTem.setUnit(wgjTemotherkgliao2.getUnit());// BOM用的单位
								wgjTem
										.setCkUnit(wgjTemotherkgliao2
												.getCkUnit());// 仓库用的单位
								wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
								if (ywmarkId == null || ywmarkId.length() == 0) {
									wgjTem.setZcMarkId(zcMarkId);// 总成号
								} else {
									wgjTem.setZcMarkId(ywmarkId);// 总成号
								}
								wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
								wgjTem
										.setCaizhi(wgjTemotherkgliao2
												.getCaizhi());// 材质类型
								wgjTem.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								wgjTem
										.setWgType(wgjTemotherkgliao2
												.getWgType());
								wgjTem.setKgliao(sonPt.getKgliao());
								wgjTem.setProductStyle(sonPt.getProductStyle());
								wgjTem.setPricestatus("新增");
								wgjTem.setBanbenStatus("默认");
								wgjTem.setBili(wgjTemotherkgliao2.getBili());
								wgjTem.setAddTime(time);
								wgjTem.setAddUserCode(user.getCode());
								wgjTem.setAddUserName(user.getName());
								wgjTem.setImportance(sonPt.getImportance());
								wgjTem
										.setAreakg(wgjTemotherkgliao2
												.getAreakg());// 每公斤使用面积
								wgjTem.setDensity(wgjTemotherkgliao2
										.getDensity());// 密度
								wgjTem.setMinkc(wgjTemotherkgliao2.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
								wgjTem.setCgcount(wgjTemotherkgliao2
										.getCgcount());// 安全库存采购的采购量
								wgjTem.setCgperiod(wgjTemotherkgliao2
										.getCgperiod());
								wgjTem.setAvgProductionTakt(sonPt
										.getAvgProductionTakt());// 生产节拍(s)
								wgjTem.setAvgDeliveryTime(sonPt
										.getAvgDeliveryTime());// 配送时长(d)
								sonPt.setProName(wgjTemotherkgliao2.getName());
								sonPt.setSpecification(wgjTemotherkgliao2
										.getSpecification());
								sonPt.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								sonPt.setUnit(wgjTemotherkgliao2.getUnit());// BOM用的单位
								sonPt.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								sonPt.setWgType(wgjTemotherkgliao2.getWgType());
								sonPt.setClType(wgjTemotherkgliao2.getCaizhi());
								sonPt.setBili(wgjTemotherkgliao2.getBili());
								if (sonPt.getProductStyle().equals("批产")) {// 生成批产外购数据时将试制设置为历史
									String wgjbanbenSql = null;
									if (wgjTem.getBanbenhao() == null
											|| wgjTem.getBanbenhao().equals("")) {
										wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
									} else {
										wgjbanbenSql = " and banbenhao='"
												+ wgjTem.getBanbenhao() + "'";
									}
									List<YuanclAndWaigj> hisszwgjList = totalDao
											.query(
													"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
															+ wgjbanbenSql,
													wgjTem.getMarkId(), wgjTem
															.getKgliao());
									if (hisszwgjList != null
											&& hisszwgjList.size() > 0) {
										for (YuanclAndWaigj hisszwgj : hisszwgjList) {
											hisszwgj.setBanbenStatus("历史");
											totalDao.update(hisszwgj);
											// 资料借用
											wgjTem.setImportance(sonPt
													.getImportance());
											wgjTem.setAreakg(hisszwgj
													.getAreakg());// 每公斤使用面积
											wgjTem.setDensity(hisszwgj
													.getDensity());// 密度
											wgjTem
													.setMinkc(hisszwgj
															.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
											wgjTem.setCgcount(hisszwgj
													.getCgcount());// 安全库存采购的采购量
											wgjTem.setCgperiod(hisszwgj
													.getCgperiod());
											wgjTem
													.setAvgProductionTakt(hisszwgj
															.getAvgProductionTakt());// 生产节拍(s)
											wgjTem.setAvgDeliveryTime(hisszwgj
													.getAvgDeliveryTime());// 配送时长(d)
											wgjTem.setName(hisszwgj.getName());
											wgjTem.setSpecification(hisszwgj
													.getSpecification());
											wgjTem
													.setTuhao(hisszwgj
															.getTuhao());// 图号
											wgjTem.setUnit(hisszwgj.getUnit());// BOM用的单位
											wgjTem
													.setTuhao(hisszwgj
															.getTuhao());// 图号
											wgjTem.setWgType(hisszwgj
													.getWgType());
											wgjTem.setCaizhi(hisszwgj
													.getCaizhi());
											wgjTem.setBili(hisszwgj.getBili());
											sonPt
													.setProName(hisszwgj
															.getName());
											sonPt.setSpecification(hisszwgj
													.getSpecification());
											sonPt.setTuhao(hisszwgj.getTuhao());// 图号
											sonPt.setUnit(hisszwgj.getUnit());// BOM用的单位
											sonPt.setTuhao(hisszwgj.getTuhao());// 图号
											sonPt.setWgType(hisszwgj
													.getWgType());
											sonPt.setClType(hisszwgj
													.getCaizhi());
											sonPt.setBili(hisszwgj.getBili());
										}
									}
								}
								if(wgjTem.getWgType()==null){
									msg += "第" + (j + 1 + base) + "行"
									+ sonPt.getMarkId()
									+ "--此外购件没有物料类别，请先处理!<br/>";
								}else{
									totalDao.save(wgjTem);
								}
								aboutwgjList.add(wgjTem);
							} else if (wgjhistorytherkgliao != null) {// 存在不可用、同版本、不同供料属性
								if (!"试制".equals(sonPt.getProductStyle())) {
									msg += "第" + (j + 1 + base) + "行外购件"
											+ wgjTem.getMarkId() + "的"
											+ wgjTem.getSpecification() + "规格的"
											+ wgjTem.getBanbenhao()
											+ "版本已禁用!<br/>";
								}
							} else {
								// 查询其他版本的有没有
								if (wgjTemerror != null) {
									if (!"试制".equals(sonPt.getProductStyle())) {
										msg += "第" + (j + 1 + base) + "行"
												+ sonPt.getMarkId()
												+ "--此件号与系统外购件库中版本不一致(系统版本为:"
												+ wgjTemerror.getBanbenhao()
												+ "，导入版本为:"
												+ sonPt.getBanBenNumber()
												+ ")，请先处理!<br/>";
									} else {// 添加试制新版本外购件
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(wgjTemerror
												.getMarkId());// 件号
										wgjTem.setName(wgjTemerror.getName());// 名称
										wgjTem.setSpecification(wgjTemerror
												.getSpecification());// 规格
										wgjTem.setUnit(wgjTemerror.getUnit());// BOM用的单位
										wgjTem.setCkUnit(wgjTemerror
												.getCkUnit());// 仓库用的单位
										wgjTem.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(wgjTemerror
												.getCaizhi());// 材质类型
										wgjTem.setTuhao(wgjTemerror.getTuhao());// 图号
										wgjTem.setWgType(wgjTemerror
												.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setBili(wgjTemerror.getBili());
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt
												.getImportance());
										wgjTem.setAreakg(wgjTemerror
												.getAreakg());// 每公斤使用面积
										wgjTem.setDensity(wgjTemerror
												.getDensity());// 密度
										wgjTem.setMinkc(wgjTemerror.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
										wgjTem.setCgcount(wgjTemerror
												.getCgcount());// 安全库存采购的采购量
										wgjTem.setCgperiod(wgjTemerror
												.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if(wgjTem.getWgType()==null){
											msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此外购件没有物料类别，请先处理!<br/>";
										}else{
											totalDao.save(wgjTem);
										}
										sonPt.setProName(wgjTemerror.getName());
										sonPt.setSpecification(wgjTemerror
												.getSpecification());
										sonPt.setTuhao(wgjTemerror.getTuhao());// 图号
										sonPt.setUnit(wgjTemerror.getUnit());// BOM用的单位
										sonPt.setTuhao(wgjTemerror.getTuhao());// 图号
										sonPt
												.setWgType(wgjTemerror
														.getWgType());
										sonPt
												.setClType(wgjTemerror
														.getCaizhi());
										sonPt.setBili(wgjTemerror.getBili());
										aboutwgjList.add(wgjTem);
									}
								} else {// 没有出现过此件号，需添加
									// SELECT distinct LENGTH(type) FROM
									// ta_ChartNOGzType where LENGTH(type)>0

									// 导入的外购件号是否符合编码库的编码规则;
									boolean flag = true;
									// boolean flag =
									// isChartNOGzType(sonPt.getMarkId());
									if (flag) {
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(sonPt.getMarkId());// 件号
										wgjTem.setName(sonPt.getProName());// 名称
										wgjTem.setSpecification(sonPt
												.getSpecification());// 规格
										wgjTem.setUnit(sonPt.getUnit());// BOM用的单位
										wgjTem.setCkUnit(sonPt.getUnit());// 仓库用的单位
										wgjTem.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(sonPt.getClType());// 材质类型
										wgjTem.setTuhao(sonPt.getTuhao());// 图号
										wgjTem.setWgType(sonPt.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt
												.getImportance());
										wgjTem.setBili(sonPt.getBili());
										wgjTem.setCgperiod(sonPt.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if(wgjTem.getWgType()==null){
											msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此外购件没有物料类别，请先处理!<br/>";
										}else{
											totalDao.save(wgjTem);
										}
										if (sonPt.getProductStyle()
												.equals("批产")) {// 生成批产外购数据时将试制设置为历史
											String wgjbanbenSql = null;
											if (wgjTem.getBanbenhao() == null
													|| wgjTem.getBanbenhao()
															.equals("")) {
												wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
											} else {
												wgjbanbenSql = " and banbenhao='"
														+ wgjTem.getBanbenhao()
														+ "'";
											}
											List<YuanclAndWaigj> hisszwgjList = totalDao
													.query(
															"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
																	+ wgjbanbenSql,
															wgjTem.getMarkId(),
															wgjTem.getKgliao());
											if (hisszwgjList != null
													&& hisszwgjList.size() > 0) {
												for (YuanclAndWaigj hisszwgj : hisszwgjList) {
													hisszwgj
															.setBanbenStatus("历史");
													totalDao.update(hisszwgj);
												}
											}
										}
										aboutwgjList.add(wgjTem);
										ChartNOSC chartnosc = (ChartNOSC) totalDao
												.getObjectByCondition(
														" from ChartNOSC where chartNO  =? ",
														wgjTem.getMarkId());
										if (chartnosc == null
												&& isChartNOGzType(wgjTem
														.getMarkId())) {
											// 同步添加到编码库里
											chartnosc = new ChartNOSC();
											chartnosc.setChartNO(wgjTem
													.getMarkId());
											chartnosc.setChartcode(Util
													.Formatnumber(wgjTem
															.getMarkId()));
											chartnosc.setIsuse("YES");
											chartnosc.setRemak("BOM导入");
											chartnosc.setSjsqUsers(user
													.getName());
											totalDao.save(chartnosc);
										}
									} else {
										msg += "第" + (j + 1 + base) + "行外购件件号"
												+ sonPt.getMarkId()
												+ "的格式不符合编码库里的编码规则！<br/>";

									}
								}
							}
						} else if (wgjTem.getStatus() != null
								&& wgjTem.getStatus().equals("禁用")) {
							// if (!"试制".equals(sonPt.getProductStyle())) {
							msg += "第" + (j + 1 + base) + "行外购件"
									+ wgjTem.getMarkId() + "的"
									+ wgjTem.getSpecification() + "规格的"
									+ wgjTem.getBanbenhao() + "版本的"
									+ sonPt.getKgliao() + "的供料属性已禁用!<br/>";
							// }
						} else {
							sonPt.setTuhao(wgjTem.getTuhao());// 图号以库中为标准
							sonPt.setProName(wgjTem.getName());
							sonPt.setUnit(wgjTem.getUnit());
							sonPt.setSpecification(wgjTem.getSpecification());
							sonPt.setLoadMarkId(wgjTem.getZcMarkId());
							sonPt.setImportance(wgjTem.getImportance());
						}
					}

				}
				sonPt.setFatherId(fatherPt.getId());
				sonPt.setRootId(rootId);
				sonPt.setProcardTemplatesb(fatherPt);
				sonPt.setXuhao(xh);
				if (sonPt.getProcardStyle().equals("外购")
						|| sonPt.getProcardStyle().equals("待定")) {
					sonPt.setQuanzi1(1f);
					sonPt.setQuanzi2(sonPt.getCorrCount());
				}
				if (fatherPt.getCardXiaoHao() != null
						&& sonPt.getCorrCount() != null) {
					sonPt.setCardXiaoHao(fatherPt.getCardXiaoHao()
							* sonPt.getCorrCount());
				} else {
					sonPt.setCardXiaoHao(0f);
				}
				xh++;
				sonPt.setYwMarkId(ywmarkId);
				sonSet.add(sonPt);
				// sonPt.setProductStyle(fatherPt.getProductStyle());
				markIdList.add(sonPt.getMarkId());
				if(sonPt.getBanci()==null){
					sonPt.setBanci(0);
				}
				if (sonPt.getId() == null) {
					if(sonPt.getProcardStyle()==null
							||(!sonPt.getProcardStyle().equals("总成")&&!sonPt.getProcardStyle().equals("自制")
									&&!sonPt.getProcardStyle().equals("待定")&&!sonPt.getProcardStyle().equals("外购"))){
						msg += "第" + (j + 1 + base) + "行"
						+ sonPt.getMarkId()
						+ "--此零件生成类型错误：("+sonPt.getProcardStyle()+") ,只能为：(总成,自制,外购,'待定'),请先处理!<br/>";
					}
					totalDao.save(sonPt);
				} else {
					totalDao.update(sonPt);
				}
				if (thisminId == null || thisminId == 0) {
					thisminId = sonPt.getId();
				}
				msg+=isSameProcardStyle(sonPt);
//				aboutptList.add(sonPt);
				aboutptsbList.add(sonPt);
				if (ptList.size() > (j + 1) && sonPt.getHistoryId() == null&&sonPt.getHistoryId2() == null) {
					msg += addProcardTemplateTreeK3(sonPt, ptList, rootId, j,
							zcMarkId, ywmarkId, base, bomtype, user);
				}
				/************************** 板材粉末自动计算 *************************************************/
				if ("自制".equals(sonPt.getProcardStyle())
						&& sonPt.getBelongLayer() != null) {
					List<String> sonMarkIds = new ArrayList<String>();
					String banbensql = "";
					if (sonPt.getBanBenNumber() != null
							&& !"".equals(sonPt.getBanBenNumber())) {
						banbensql = " and banBenNumber = '"
								+ sonPt.getBanBenNumber() + "'";
					} else {
						banbensql = " and (banBenNumber is null or banBenNumber = '' )";
					}
					ProcardTemplate bzpt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									" from ProcardTemplate where markId = ? and bzStatus = '已批准' and productStyle= '批产' "
											+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')"
											+ banbensql, sonPt.getMarkId());
					if (bzpt == null && "试制".equals(sonPt.getProductStyle())) {
						bzpt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										" from ProcardTemplate where markId = ? and bzStatus = '已批准' and productStyle= '试制' "
												+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')"
												+ banbensql, sonPt.getMarkId());
					}
					if (bzpt == null) {
						Set<ProcardTemplatesb> ptSonsonSet = sonPt
								.getProcardsbTSet();
						if (ptSonsonSet != null && ptSonsonSet.size() > 0) {
							for (ProcardTemplatesb ptsonson : ptSonsonSet) {
								sonMarkIds.add(ptsonson.getMarkId());
							}
						}
						List<ProcardTemplate> zzptList = (List<ProcardTemplate>) totalDao
								.query(
										" from ProcardTemplate where markId = ? and id!=?"
												+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')",
										sonPt.getMarkId(), sonPt.getId());
						ProcardTemplate historyPt1 = null;// 查看该件号之前是否存在
						ProcardTemplate historyPtother1 = null;// 查看该件号之前是否存在不同版本
						String productStylesql1 = null;
						if (sonPt.getProductStyle() != null
								&& sonPt.getProductStyle().equals("试制")) {
							productStylesql1 = " and 1=1";// 试制可借用批产
						} else {
							productStylesql1 = " and productStyle='批产'";
						}
						String sqlhistory1 = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql1;
						String sqlhistoryOther1 = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql1;
						Set<ProcardTemplatesb> sonsetPt0 = sonPt.getProcardsbTSet();
						if (sonsetPt0 == null) {
							sonsetPt0 = new HashSet<ProcardTemplatesb>();
						}
						String productStylesql = null;
						if (sonPt.getProductStyle() != null
								&& sonPt.getProductStyle().equals("试制")) {
							productStylesql = " and 1=1";// 试制可借用批产
						} else {
							productStylesql = " and productStyle='批产'";
						}

						String sqlhistory = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql;
						// 板材粉末计算开始

						if (sonPt.getThisLength() != null
								&& sonPt.getThisLength() > 0
								&& sonPt.getThisWidth() != null
								&& sonPt.getThisWidth() > 0
								&& sonPt.getThisHight() != null
								&& sonPt.getThisHight() > 0) {
							// 判断是否存在材料类别进行进行板材
							if (sonPt.getClType() != null
									&& sonPt.getClType().length() > 0) {
								PanelSize panel = null;
								Float hudu = null;
								if (sonPt.getThisHight() != null
										&& sonPt.getThisHight() > 0) {
									hudu = sonPt.getThisHight();
								} else {
									ProcardTemplate oldpt = (ProcardTemplate) totalDao
											.getObjectByCondition(
													" from  ProcardTemplate where markId = ? and g and thisLength >0 and thisWidth>0 and (banbenStatus is null or  banbenStatus != '历史') and (dataStatus is null or dataStatus!='删除')",
													sonPt.getMarkId());
									hudu = oldpt.getThisHight();
								}
								int hudu2 = (int) (float) hudu;
								String hql_panel = " from PanelSize where caizhi ='"
										+ sonPt.getClType()
										+ "' and  "
										+ hudu
										+ " BETWEEN  fristThickness AND endThickness ";
								panel = (PanelSize) totalDao
										.getObjectByCondition(hql_panel);
								if (panel != null) {
									String specification = "T" + hudu + "x"
											+ panel.getSize();
									String specification2 = null;
									String hql_wgj = null;
									if (hudu2 == hudu) {
										specification2 = "T" + hudu2 + "x"
												+ panel.getSize();
										hql_wgj = " from YuanclAndWaigj where caizhi like '%"
												+ sonPt.getClType()
												+ "%' and ( specification like '%"
												+ specification
												+ "%' or specification like '%"
												+ specification2
												+ "%') and (banbenStatus is null or  banbenStatus != '历史')";
									} else {
										hql_wgj = " from YuanclAndWaigj where caizhi like '%"
												+ sonPt.getClType()
												+ "%' and specification like '%"
												+ specification
												+ "%' and (banbenStatus is null or  banbenStatus != '历史')";
									}
									YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
											.getObjectByCondition(hql_wgj);

									if (wgj != null
											&& !sonMarkIds.contains(wgj
													.getMarkId())) {
										if (wgj.getBanbenhao() == null
												|| wgj.getBanbenhao().length() == 0) {// 无版本号
											sqlhistory += " and (banBenNumber is null or banBenNumber='')";
										} else {
											sqlhistory += " and banBenNumber ='"
													+ wgj.getBanbenhao() + "'";
										}
										ProcardTemplatesb wgpt = new ProcardTemplatesb();
										historyPt1 = (ProcardTemplate) totalDao
												.getObjectByCondition(
														sqlhistory1, wgj
																.getMarkId());
										if (historyPt1 != null) {
											BeanUtils.copyProperties(
													historyPt1, wgpt,
													new String[] { "id",
															"procardTemplate",
															"procardTSet",
															"processTemplate",
															"zhUsers",
															"quanzi1",
															"quanzi2",
															"maxCount",
															"corrCount",
															"rootId",
															"fatherId",
															"belongLayer",
															"cardXiaoHao",
															"loadMarkId",
															"ywMarkId",
															"historyId",
															"thisLength",
															"thisWidth",
															"thisHight",
															"productStyle" });
											wgpt.setYwMarkId(ywmarkId);
											wgpt.setHistoryId(historyPt1
													.getId());
										} else {
											wgpt.setMarkId(wgj.getMarkId());// 件号
											wgpt.setWgType(wgj.getWgType());// 物料类别
											wgpt.setCaizhi(wgj.getCaizhi());// 材质
											wgpt.setProName(wgj.getName());// 名称\
											wgpt.setBanBenNumber(wgj
													.getBanbenhao());
											wgpt.setSpecification(wgj
													.getSpecification());// 规格
											wgpt.setKgliao(wgj.getKgliao());// 供料属性
											wgpt.setBili(wgj.getBili());// 单张重量
											wgpt.setUnit(wgj.getUnit());// 单位
											wgpt.setProcardStyle("外购");
										}
										wgpt.setProductStyle(sonPt
												.getProductStyle());
										wgpt.setBelongLayer(sonPt
												.getBelongLayer() + 1);// 层数
										wgpt.setFatherId(sonPt.getId());
										wgpt.setRootMarkId(sonPt
												.getRootMarkId());
										wgpt.setRootId(sonPt.getRootId());
										wgpt.setQuanzi1(1f);
										Float quanzhi2 = 0f;
										if(wgj
												.getDensity()==null){
											throw new RuntimeException("外购件库中:"+wgj.getMarkId()+"没有填写密度!");
										}
										quanzhi2 = (sonPt.getThisHight()
												* (sonPt.getThisLength() + 12)
												* (sonPt.getThisWidth() + 12) * wgj
												.getDensity()) / 1000000;
										wgpt.setQuanzi2(quanzhi2);
										wgpt.setSpecification(wgj
												.getSpecification());
										wgpt.setSunhao(5f);
										wgpt.setSbApplyId(sonPt.getSbApplyId());
										// wgpt.setSunhaoType("百分比");
										sonsetPt0.add(wgpt);
										wgpt.setProcardTemplatesb(sonPt);
										if (wgpt.getBzStatus() == null) {
											wgpt.setBzStatus("待编制");
										}
										if (!wgpt.getBzStatus().equals("已批准")
												&& !wgpt.getBzStatus().equals(
														"设变发起中")) {
											wgpt.setBianzhiName(user.getName());
											wgpt.setBianzhiId(user.getId());
										}
										wgpt.setRemark("板材粉末自动计算添加");
										totalDao.save(wgpt);
										sonPt.setProcardsbTSet(sonsetPt0);
										if ("试制"
												.equals(sonPt.getProductStyle())) {
											List<ProcessTemplateFile> ptFileList = totalDao
													.query(
															"from ProcessTemplateFile where processNO is null "
																	+ " and status='默认' and markId=? and glId is null ",
															wgpt.getMarkId());
											if (ptFileList != null
													&& ptFileList.size() > 0) {
												for (ProcessTemplateFile ptFile : ptFileList) {
													ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
													BeanUtils
															.copyProperties(
																	ptFile,
																	ptFile2,
																	new String[] {
																			"id",
																			"productStyle",
																			"glId" });
													ptFile2.setGlId(wgpt
															.getId());
													ptFile2
															.setProductStyle("试制");
													totalDao.save(ptFile2);
												}
											}

										} else {
											// 横向比较之前的自制件是否存在本外购件，如果不存在则新添，存在则修改其长、宽、厚和权值2
											// 为了达到数据一致的效果
											for (ProcardTemplate zzpt : zzptList) {
												if (!zzpt.getId().equals(
														sonPt.getId())) {
													String hql_historywgpt = " from ProcardTemplate where fatherId = ? and markId = ? and (banbenStatus is null or  banbenStatus != '历史') and (dataStatus is null or dataStatus!='删除') ";
													ProcardTemplate historywgpt = (ProcardTemplate) totalDao
															.getObjectByCondition(
																	hql_historywgpt,
																	zzpt
																			.getId(),
																	wgpt
																			.getMarkId());
													if (historywgpt != null) {
														historywgpt
																.setQuanzi2(wgpt
																		.getQuanzi2());
														historywgpt
																.setThisHight(wgpt
																		.getThisHight());
														historywgpt
																.setThisLength(wgpt
																		.getThisLength());
														historywgpt
																.setThisWidth(wgpt
																		.getThisWidth());
														historywgpt
																.setSunhao(5f);
														// historywgpt.setSunhaoType("百分比");
														totalDao
																.update(historywgpt);
													} else {
														historywgpt = new ProcardTemplate();
														BeanUtils
																.copyProperties(
																		wgpt,
																		historywgpt,
																		new String[] {
																				"id",
																				"procardTemplate",
																				"procardTSet",
																				"processTemplate",
																				"zhUsers",
																				"maxCount",
																				"corrCount",
																				"rootId",
																				"fatherId",
																				"belongLayer",
																				"cardXiaoHao",
																				"loadMarkId",
																				"ywMarkId",
																				"historyId",
																				"thisLength",
																				"thisWidth",
																				"thisHight",
																				"productStyle" });

														historywgpt
																.setYwMarkId(zzpt
																		.getYwMarkId());
														historywgpt
																.setHistoryId(wgpt
																		.getHistoryId());
														historywgpt
																.setRootId(zzpt
																		.getRootId());
														historywgpt
																.setRootMarkId(zzpt
																		.getRootMarkId());
														historywgpt
																.setFatherId(zzpt
																		.getId());
														historywgpt
																.setProcardTemplate(zzpt);
														historywgpt
																.setBelongLayer(zzpt
																		.getBelongLayer() + 1);
														historywgpt
																.setProductStyle(zzpt
																		.getProductStyle());
														historywgpt
																.setSunhao(5f);
														// historywgpt.setSunhaoType("百分比");
														totalDao
																.save(historywgpt);

													}
												}
											}
										}

									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，没有在外购件库找到" + "材质为："
												+ sonPt.getClType() + "，规格为:"
												+ specification + "的板材。 !<br/>";
									}
								} else {
									jsbcmsg += "第" + (j + 1 + base)
											+ "行错误，没有在板材尺寸库中找到" + "材质为："
											+ sonPt.getClType() + "，厚度为:"
											+ hudu + "的板材。 !<br/>";

								}
							}
							// 开始计算本层自制件有长宽的粉末
							if (sonPt.getCorrCount() != null
									&& sonPt.getCorrCount() > 0
									&& sonPt.getBiaochu() != null
									&& sonPt.getBiaochu().length() > 0) {
								Float mianji = sonPt.getThisLength()
										* sonPt.getThisWidth();
								if (mianji > 0) {
									// 查询粉末表处对照关系
									String biaochu = (String) totalDao
											.getObjectByCondition(
													" select valueCode from CodeTranslation where type = '粉末' and keyCode =? ",
													sonPt.getBiaochu());
									if (biaochu != null && biaochu.length() > 0) {
										PanelSize fuhefenmo = (PanelSize) totalDao
												.getObjectByCondition(
														" from PanelSize where caizhi= ? and type = '复合粉末'",
														biaochu);
										if (fuhefenmo != null) {
											String hql_fenmowgj1 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo1()
													+ "%' and wgType = '粉末' and (banbenStatus is null or  banbenStatus != '历史') ";
											if (fuhefenmo.getFenmo1().indexOf(
													"华彩") == -1) {
												hql_fenmowgj1 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj1 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj1);
											if (fenmowgj1 != null) {
												// 复合粉末1
												fenmojisuan(
														fenmowgj1,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount1(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo1()
														+ "的复合粉末1 !<br/>";
											}
											String hql_fenmowgj2 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo2()
													+ "%' and wgType = '粉末'  and (banbenStatus is null or  banbenStatus != '历史') ";
											if (fuhefenmo.getFenmo2().indexOf(
													"华彩") == -1) {
												hql_fenmowgj2 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj2 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj2);
											if (fenmowgj2 != null) {
												// 复合粉末2
												fenmojisuan(
														fenmowgj2,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount2(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第"
														+ (j + 1 + base)
														+ "行错误，"
														+ "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo2()
														+ "的复合粉末2 !<br/>&bcfmjs&";
											}

										} else {
											String hql_fenmowgj = " from  YuanclAndWaigj where name like '%"
													+ biaochu
													+ "%' and wgType = '粉末'  and (banbenStatus is null or  banbenStatus != '历史') ";
											YuanclAndWaigj fenmowgj = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj);
											if (fenmowgj != null) {
												// 粉末
												fenmojisuan(fenmowgj,
														sonMarkIds, sqlhistory,
														historyPtother1,
														sqlhistory1, ywmarkId,
														sonPt, mianji, 2,
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，"
														+ "即没有在复合粉末库中找到材质为:"
														+ sonPt.getBiaochu()
														+ "的复合粉末。"
														+ "也没有在外购件库中找到" + "表处为"
														+ biaochu
														+ "的粉末 !<br/>";
											}
										}
									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，" + "未找到"
												+ sonPt.getBiaochu()
												+ "的对应关系表处<br/>";
									}
								}
							}

						} else {
							// 开始计算自制件本身没有长宽的但其下层自制件有长宽的 粉末;
							if (sonPt.getBiaochu() != null
									&& sonPt.getBiaochu().length() > 0) {
								Float briefnum = 1f;// 所有上阶层消耗数;
								Integer brieBelongLayer = sonPt
										.getBelongLayer();
								Float mianji = 0f;
								DecimalFormat df1 = new DecimalFormat(
										"###,###,###,###,###");
								for (int m = j + 1; m < ptList.size(); m++) {
									ProcardTemplatesb pt = ptList.get(m);
									if (pt.getCorrCount() == null) {
										continue;
									}
									if (pt.getBelongLayer() == sonPt
											.getBelongLayer()) {// 当层数和
										break;
									} else if ("自制"
											.equals(pt.getProcardStyle())) {
										if (pt.getThisHight() != null
												&& pt.getThisHight() > 0
												&& pt.getThisWidth() != null
												&& pt.getThisWidth() > 0
												&& pt.getThisLength() != null
												&& pt.getThisLength() > 0) {
											if (brieBelongLayer >= pt
													.getBelongLayer()) {// 如果跳出本层回归到上层让上阶层消耗数归1；
												briefnum = 1f;// 重新归1；
											}
											Float aa = briefnum
													* pt.getThisLength()
													* pt.getThisWidth()
													* pt.getCorrCount();
											mianji += aa;
										} else {
											ProcardTemplate oldpt = (ProcardTemplate) totalDao
													.getObjectByCondition(
															" from ProcardTemplate where markId = ? and thisHight>0 and thisLength >0 and thisWidth>0  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
															pt.getMarkId());
											if (oldpt != null
													&& oldpt.getThisWidth() != null
													&& oldpt.getThisWidth() > 0
													&& oldpt.getThisHight() != null
													&& oldpt.getThisHight() > 0
													&& oldpt.getThisLength() != null
													&& oldpt.getThisLength() > 0) {
												Float aa = briefnum
														* oldpt.getThisLength()
														* oldpt.getThisWidth()
														* oldpt.getCorrCount();
												mianji += aa;
											} else {
												if (pt.getBelongLayer() != null
														&& pt.getBelongLayer()
																- brieBelongLayer == 1) {
													briefnum = briefnum
															* pt.getCorrCount();
												} else {
													briefnum = 1 * pt
															.getCorrCount();
												}
												brieBelongLayer = pt
														.getBelongLayer();
											}
										}
									}
								}
								if (mianji > 0) {
									// 查询粉末表处对照关系
									String biaochu = (String) totalDao
											.getObjectByCondition(
													" select valueCode from CodeTranslation where type = '粉末' and keyCode =? ",
													sonPt.getBiaochu());
									if (biaochu != null && biaochu.length() > 0) {
										PanelSize fuhefenmo = (PanelSize) totalDao
												.getObjectByCondition(
														" from PanelSize where caizhi= ? and type = '复合粉末'",
														biaochu);
										if (fuhefenmo != null) {
											String hql_fenmowgj1 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo1()
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (fuhefenmo.getFenmo1().indexOf(
													"华彩") == -1) {
												hql_fenmowgj1 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj1 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj1);
											if (fenmowgj1 != null) {
												// 复合粉末1
												fenmojisuan(
														fenmowgj1,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount1(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo1()
														+ "的复合粉末1 !<br/>";
											}
											String hql_fenmowgj2 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo2()
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (fuhefenmo.getFenmo2().indexOf(
													"华彩") == -1) {
												hql_fenmowgj2 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj2 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj2);
											if (fenmowgj2 != null) {
												// 复合粉末2
												fenmojisuan(
														fenmowgj2,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount2(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo2()
														+ "的复合粉末2 !<br/>";
											}
										} else {
											String hql_fenmowgj = " from  YuanclAndWaigj where name like '%"
													+ biaochu
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (biaochu.indexOf("华彩") == -1) {
												hql_fenmowgj += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj);
											if (fenmowgj != null) {
												// 粉末
												fenmojisuan(fenmowgj,
														sonMarkIds, sqlhistory,
														historyPtother1,
														sqlhistory1, ywmarkId,
														sonPt, mianji, 2,
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，"
														+ "即没有在复合粉末库中找到材质为:"
														+ sonPt.getBiaochu()
														+ "的复合粉末。"
														+ "也没有在外购件库中找到" + "表处为"
														+ sonPt.getBiaochu()
														+ "的粉末 !<br/>";
											}
										}
									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，" + "未找到"
												+ sonPt.getBiaochu()
												+ "的对应关系表处<br/>";
									}
								}
							}
						}
					}
				}
			} else if (sonPt.getBelongLayer() > (fatherPt.getBelongLayer() + 1)) {
				continue;
			} else {
				break;
			}
		}
		if (sonSet.size() > 0 && !fatherPt.getProcardStyle().equals("总成")) {
			if (fatherPt.getProcardStyle().equals("外购")) {
				msg += "外购件" + fatherPt.getMarkId() + "下不能有零件!<br/>";
			}
		}
		fatherPt.setProcardsbTSet(sonSet);
		totalDao.update(fatherPt);
		return msg;
	}
	
	private String compareBanben(ProcardTemplatesb sonPt,
			List<ProcardTemplate> aboutptList2, String ywmarkId, Users user,
			int count) {
		// TODO Auto-generated method stub
		if (aboutptList2 != null && aboutptList2.size() > 0) {
			ProcardTemplate pc = null;// 批产
			ProcardTemplate sz = null;// 试制
			ProcardTemplate ls = null;// 历史版次
			ProcardTemplate high = null;// 最高版本
			boolean checkmakrId = false;
			for (ProcardTemplate old : aboutptList2) {
				if (old.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
					checkmakrId = true;
					if ((old.getBanbenStatus() == null || !old
							.getBanbenStatus().equals("历史"))
							&& (old.getDataStatus() == null || !old
									.getDataStatus().equals("删除"))) {
						if(sonPt.getUnit()!=null&&sonPt.getUnit().length()>0){
							if(!Util.isEquals(old.getUnit(), sonPt.getUnit())){
								return "第" + count + "行,零件"
								+ sonPt.getMarkId() + "的单位为:"+sonPt.getUnit()
								+ "与现有的单位:"+old.getUnit()+"的单位不一致!<br/>";
							}
						}
						if(old.getBzStatus()!=null
								&&old.getBzStatus().equals("禁用")){
							if(Util.isEquals(old.getBanBenNumber(),
									sonPt.getBanBenNumber())){
								if(old.getProcardStyle().equals("外购")
										&&Util.isEquals(old.getKgliao(), sonPt.getKgliao())){
									return "第" + count + "行" + sonPt.getMarkId()
									+ "--此件号的"+old.getBanBenNumber()+"版本的"+old.getKgliao()+"供料属性已禁用<br/>";
								}else{
									return "第" + count + "行" + sonPt.getMarkId()
									+ "--此件号版本已禁用<br/>";
								}
							}
						}else{
							if (old.getProductStyle().equals("批产")) {
								if (pc == null) {
									pc = old;
								}
							} else {
								if (sz == null
										&& Util.isEquals(old.getBanBenNumber(),
												sonPt.getBanBenNumber())) {
									sz = old;
								}
							}
							if (high == null) {
								high = old;
							} else {
								int cjCount = Banbenxuhao.comparebanben(old
										.getBanBenNumber(), high.getBanBenNumber(),
										banBenxuhaoMap);
								if (cjCount > 0 && cjCount < 5) {
									high = old;
								}
								
							}
						}
						
					}
					if (ls != null) {
						Integer banci1 = old.getBanci();
						Integer banci2 = ls.getBanci();
						if (banci1 == null) {
							banci1 = 0;
						}
						if (banci2 == null) {
							banci2 = 0;
						}
						if (banci1 > banci2) {
							ls = old;
						}
					} else {
						ls = old;
					}
					
				} else if (checkmakrId) {
					break;
				}
			}
			if(checkmakrId){//工程BOM中有这个零件
				boolean szhad = false;
				if (sonPt.getProductStyle().equals("试制")) {
					if (high != null) {
						int cjCount = Banbenxuhao.comparebanben(sonPt
								.getBanBenNumber(), high.getBanBenNumber(),
								banBenxuhaoMap);
						if (cjCount < 0) {
							return "第" + count + "行" + sonPt.getMarkId()
							+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
							+ high.getBanBenNumber() + "，导入版本为:"
							+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						}
						szhad = true;
					}
				}
				if (pc != null) {// 有批产数据
					if (!Util.isEquals(pc.getBanBenNumber(), sonPt.getBanBenNumber())) {
						if (!szhad) {
							return "第" + count + "行" + sonPt.getMarkId()
							+ "--此件号版本与系统批产BOM中的版本不一致(系统批产版本为:"
							+ pc.getBanBenNumber() + "，导入版本为:"
							+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						} else {
							int cjCount = Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pc.getBanBenNumber(),
									banBenxuhaoMap);
							if (cjCount < 0) {
								return "第" + count + "行" + sonPt.getMarkId()
								+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
								+ pc.getBanBenNumber() + "，导入版本为:"
								+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
							}
						}
					} else {
						copyjcMsg(pc, sonPt, ywmarkId, 0, user);
						sonPt.setHistoryId(pc.getId());
						if (pc.getTuhao() != null
								&& (pc.getTuhao().startsWith("DKBA0") || pc
										.getTuhao().startsWith("dkba0"))) {
							sonPt.setBanBenNumber(null);
						}
						return "";
					}
				}
				if (sonPt.getProductStyle().equals("试制")) {
					if (sz != null) {
						copyjcMsg(sz, sonPt, ywmarkId, 0, user);
						sonPt.setHistoryId(sz.getId());
						if (sz.getTuhao() != null
								&& (sz.getTuhao().startsWith("DKBA0") || sz
										.getTuhao().startsWith("dkba0"))) {
							sonPt.setBanBenNumber(null);
						}
						return "";
					} else if (high != null) {
						int cjCount = Banbenxuhao.comparebanben(sonPt
								.getBanBenNumber(), high.getBanBenNumber(),
								banBenxuhaoMap);
						if (cjCount > 0 && cjCount < 5) {// 导入了新版本
							sonPt.setProName(high.getProName());
							sonPt.setSpecification(high.getSpecification());
							sonPt.setWgType(high.getWgType());
							sonPt.setTuhao(high.getTuhao());
							return "";
						} else {
							return "第" + count + "行" + sonPt.getMarkId()
							+ "--此件号版本与系统试制BOM中的版本不一致(系统批产版本为:"
							+ high.getBanBenNumber() + "，导入版本为:"
							+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						}
					}
				}
				if (ls != null) {
					sonPt.setBanci(ls.getBanci());
				} else {
					sonPt.setBanci(0);
				}
			}else{//工程BOM中没有这个零件就去设变BOM中找
				for (ProcardTemplatesb old : aboutptsbList) {
					if (old.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
						if(!Util.isEquals(sonPt.getBanBenNumber(), old.getBanBenNumber())){
							return "第" + count + "行" + sonPt.getMarkId()
							+ "--此件号版本与系统设变BOM中的版本不一致(系统设变BOM版本为:"
							+ old.getBanBenNumber() + "，导入版本为:"
							+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						}else{
							sonPt.setHistoryId2(old.getId());
							copyjcMsg2(old, sonPt, ywmarkId, 1, user);
						}
						break;
					}
				}
			}

		}
		return "";
	}
	private String compareBanben2(ProcardTemplatesb sonPt,
			List<ProcardTemplatesb> aboutptList2, String ywmarkId, Users user,
			int count) {
		// TODO Auto-generated method stub
		if (aboutptList2 != null && aboutptList2.size() > 0) {
			ProcardTemplatesb pc = null;// 批产
			ProcardTemplatesb sz = null;// 试制
			ProcardTemplatesb ls = null;// 历史版次
			ProcardTemplatesb high = null;// 最高版本
			boolean checkmakrId = false;
			for (ProcardTemplatesb old : aboutptList2) {
				if (old.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
					checkmakrId = true;
					if ((old.getBanbenStatus() == null || !old
							.getBanbenStatus().equals("历史"))
							&& (old.getDataStatus() == null || !old
									.getDataStatus().equals("删除"))) {
						if (old.getProductStyle().equals("批产")) {
							if (pc == null) {
								pc = old;
							}
						} else {
							if (sz == null
									&& Util.isEquals(old.getBanBenNumber(),
											sonPt.getBanBenNumber())) {
								sz = old;
							}
						}
						if (high == null) {
							high = old;
						} else {
							int cjCount = Banbenxuhao.comparebanben(old
									.getBanBenNumber(), high.getBanBenNumber(),
									banBenxuhaoMap);
							if (cjCount > 0 && cjCount < 5) {
								high = old;
							}
							
						}
						
					}
					if (ls != null) {
						Integer banci1 = old.getBanci();
						Integer banci2 = ls.getBanci();
						if (banci1 == null) {
							banci1 = 0;
						}
						if (banci2 == null) {
							banci2 = 0;
						}
						if (banci1 > banci2) {
							ls = old;
						}
					} else {
						ls = old;
					}
					
				} else if (checkmakrId) {
					break;
				}
			}
			boolean szhad = false;
			if (sonPt.getProductStyle().equals("试制")) {
				if (high != null) {
					int cjCount = Banbenxuhao.comparebanben(sonPt
							.getBanBenNumber(), high.getBanBenNumber(),
							banBenxuhaoMap);
					if (cjCount < 0) {
						return "第" + count + "行" + sonPt.getMarkId()
						+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
						+ high.getBanBenNumber() + "，导入版本为:"
						+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					}
					szhad = true;
				}
			}
			if (pc != null) {// 有批产数据
				if (!Util.isEquals(pc.getBanBenNumber(), sonPt.getBanBenNumber())) {
					if (!szhad) {
						return "第" + count + "行" + sonPt.getMarkId()
						+ "--此件号版本与系统批产BOM中的版本不一致(系统批产版本为:"
						+ pc.getBanBenNumber() + "，导入版本为:"
						+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					} else {
						int cjCount = Banbenxuhao.comparebanben(sonPt
								.getBanBenNumber(), pc.getBanBenNumber(),
								banBenxuhaoMap);
						if (cjCount < 0) {
							return "第" + count + "行" + sonPt.getMarkId()
							+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
							+ pc.getBanBenNumber() + "，导入版本为:"
							+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						}
					}
				} else {
					copyjcMsg2(pc, sonPt, ywmarkId, 0, user);
					sonPt.setHistoryId(pc.getId());
					if (pc.getTuhao() != null
							&& (pc.getTuhao().startsWith("DKBA0") || pc
									.getTuhao().startsWith("dkba0"))) {
						sonPt.setBanBenNumber(null);
					}
					return "";
				}
			}
			if (sonPt.getProductStyle().equals("试制")) {
				if (sz != null) {
					copyjcMsg2(sz, sonPt, ywmarkId, 0, user);
					sonPt.setHistoryId(sz.getId());
					if (sz.getTuhao() != null
							&& (sz.getTuhao().startsWith("DKBA0") || sz
									.getTuhao().startsWith("dkba0"))) {
						sonPt.setBanBenNumber(null);
					}
					return "";
				} else if (high != null) {
					int cjCount = Banbenxuhao.comparebanben(sonPt
							.getBanBenNumber(), high.getBanBenNumber(),
							banBenxuhaoMap);
					if (cjCount > 0 && cjCount < 5) {// 导入了新版本
						sonPt.setProName(high.getProName());
						sonPt.setSpecification(high.getSpecification());
						sonPt.setWgType(high.getWgType());
						sonPt.setTuhao(high.getTuhao());
						return "";
					} else {
						return "第" + count + "行" + sonPt.getMarkId()
						+ "--此件号版本与系统试制BOM中的版本不一致(系统批产版本为:"
						+ high.getBanBenNumber() + "，导入版本为:"
						+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					}
				}
			}
			if (ls != null) {
				sonPt.setBanci(ls.getBanci());
			} else {
				sonPt.setBanci(0);
			}
			
		}
		return "";
	}
	
	private void copyjcMsg(ProcardTemplate historyPt, ProcardTemplatesb sonPt,
			String ywmarkId, Integer youxiao, Users user) {
		try {
			// TODO Auto-generated method stub
			BeanUtils.copyProperties(historyPt, sonPt, new String[] { "id",
					"procardTemplate", "procardTSet", "processTemplate",
					"zhUsers", "quanzi1", "quanzi2", "maxCount", "corrCount",
					"rootId", "fatherId", "belongLayer", "cardXiaoHao",
					"loadMarkId", "ywMarkId", "historyId", "kgliao",
					"thisLength", "thisWidth", "thisHight", "biaochu",
					"productStyle", "bzStatus", "procardStyle", "sunhao",
					"sunhaoType", "banBenNumber", "lingliaostatus","wgType" });
			if ((historyPt.getBanbenStatus() == null || !"历史".equals(historyPt
					.getBzStatus()))
					&& (historyPt.getDataStatus() == null || !historyPt
							.getDataStatus().equals("删除"))) {
				sonPt.setBzStatus(historyPt.getBzStatus());
			} else {
				sonPt.setBzStatus("待编制");
				sonPt.setBianzhiId(user.getId());
				sonPt.setBianzhiName(user.getName());
			}
			if (sonPt.getProcardStyle().equals("待定")) {
				if (historyPt.getProcardStyle().equals("总成")) {
					sonPt.setProcardStyle("自制");
				} else {
					sonPt.setProcardStyle(historyPt.getProcardStyle());
				}
			}
			sonPt.setYwMarkId(ywmarkId);
			sonPt.setHistoryId(historyPt.getId());
			if (youxiao == 1 && historyPt.getBanbenStatus() != null
					&& historyPt.getBanbenStatus().equals("已批准")) {
				if (sonPt.getProcardStyle() != null
						&& !sonPt.getProcardStyle().equals("外购")) {// 非外购件连带长宽高
					sonPt.setQuanzi1(historyPt.getQuanzi1());
					sonPt.setQuanzi2(historyPt.getQuanzi2());
					sonPt.setThisLength(historyPt.getThisLength());
					sonPt.setThisWidth(historyPt.getThisWidth());
					sonPt.setThisHight(historyPt.getThisHight());
				}
			}
			if (historyPt.getProcardStyle().equals("总成")) {
				sonPt.setProcardStyle("自制");
			}
			String banciSql = null;
			if (historyPt.getBanci() == null || historyPt.getBanci() == 0) {
				banciSql = "and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + historyPt.getBanci();
			}
			// 同步工序
			Set<ProcessTemplate> processSet1 = historyPt.getProcessTemplate();
			Set<ProcessTemplatesb> processSet2 = new HashSet<ProcessTemplatesb>();
			if (processSet1 != null) {// 添加在添加列表中存在的工序号的工序
				for (ProcessTemplate process1 : processSet1) {
					if (process1.getProcessNO() != null) {
						ProcessTemplatesb process2 = new ProcessTemplatesb();
						// -----------辅料开始----------
//						if (process1.getIsNeedFuliao() != null
//								&& process1.getIsNeedFuliao().equals("yes")) {
//							Set<ProcessFuLiaoTemplate> fltmpSet = process1
//									.getProcessFuLiaoTemplate();
//							if (fltmpSet.size() > 0) {
//								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//									BeanUtils.copyProperties(fltmp, pinfoFl,
//											new String[] { "id",
//													"processTemplate" });
//									if (pinfoFl.getQuanzhi1() == null) {
//										pinfoFl.setQuanzhi1(1f);
//									}
//									if (pinfoFl.getQuanzhi2() == null) {
//										pinfoFl.setQuanzhi2(1f);
//									}
//									pinfoFl.setProcessTemplate(process2);
//									pinfoFlSet.add(pinfoFl);
//								}
//								process2.setProcessFuLiaoTemplate(pinfoFlSet);
//							}
//						}
						// -----------辅料结束----------
						BeanUtils
								.copyProperties(process1, process2,
										new String[] { "id", "procardTemplate",
												"taSopGongwei",
												"processFuLiaoTemplate" });
						process2.setProcardTemplatesb(sonPt);
						totalDao.save(process2);
						processSet2.add(process2);
						if ("试制".equals(sonPt.getProductStyle())) {
							List<ProcessTemplateFile> ptFileList = null;
							if ("试制".equals(historyPt.getProductStyle())) {
								ptFileList = totalDao.query(
										"from ProcessTemplateFile where processNO =? "
												+ banciSql + " and glId=? ",
										process2.getProcessNO(), process1
												.getId());
							} else {
								ptFileList = totalDao
										.query(
												"from ProcessTemplateFile where processNO =? "
														+ "  and markId=? and productStyle='批产' "
														+ banciSql, process2
														.getProcessNO(), sonPt
														.getMarkId());
							}
							if (ptFileList != null && ptFileList.size() > 0) {
								for (ProcessTemplateFile ptFile : ptFileList) {
									if (ptFile != null) {
										ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
										BeanUtils
												.copyProperties(ptFile,
														ptFile2, new String[] {
																"id",
																"productStyle",
																"glId" });
										ptFile2.setProductStyle(sonPt.getProductStyle());
										ptFile2.setSbApplyId(sonPt.getSbApplyId());
										ptFile2.setProcesstFileId(ptFile.getId());
										ptFile2.setGlId(process2.getId());
										totalDao.save(ptFile2);
									}
								}
							}
						}

					}
				}
			}
			if ("试制".equals(sonPt.getProductStyle())) {
				if (sonPt.getId() == null) {
					totalDao.save(sonPt);
				}
				List<ProcessTemplateFile> ptFileList = null;
				if ("试制".equals(historyPt.getProductStyle())) {
					ptFileList = totalDao.query(
							"from ProcessTemplateFile where processNO is null "
									+ banciSql + " and glId=? ", historyPt
									.getId());
				} else {
					ptFileList = totalDao.query(
							"from ProcessTemplateFile where processNO is null "
									+ "  and markId=? and productStyle='批产' "
									+ banciSql, sonPt.getMarkId());
				}
				if (ptFileList != null && ptFileList.size() > 0) {
					for (ProcessTemplateFile ptFile : ptFileList) {
						if (ptFile != null) {
							ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
							BeanUtils
									.copyProperties(ptFile, ptFile2,
											new String[] { "id",
													"productStyle", "glId" });
							ptFile2.setProductStyle(sonPt.getProductStyle());
							ptFile2.setSbApplyId(sonPt.getSbApplyId());
							ptFile2.setProcesstFileId(ptFile.getId());
							ptFile2.setGlId(sonPt.getId());
							totalDao.save(ptFile2);
						}
					}
				}
			}

			sonPt.setProcessTemplatesb(processSet2);
		} catch (BeansException e) {
			System.out.println(historyPt.getId()
					+ " ---------------------------");
			e.printStackTrace();
		}
	}
	private void copyjcMsg2(ProcardTemplatesb historyPt, ProcardTemplatesb sonPt,
			String ywmarkId, Integer youxiao, Users user) {
		try {
			// TODO Auto-generated method stub
			BeanUtils.copyProperties(historyPt, sonPt, new String[] { "id",
					"procardTemplatesb", "procardsbTSet", "processTemplatesb",
					"zhUsers", "quanzi1", "quanzi2", "maxCount", "corrCount",
					"rootId", "fatherId", "belongLayer", "cardXiaoHao",
					"loadMarkId", "ywMarkId", "historyId", "kgliao","historyId2",
					"thisLength", "thisWidth", "thisHight", "biaochu",
					"productStyle", "bzStatus", "procardStyle", "sunhao",
					"sunhaoType", "banBenNumber", "lingliaostatus","wgType" });
			if ((historyPt.getBanbenStatus() == null || !"历史".equals(historyPt
					.getBzStatus()))
					&& (historyPt.getDataStatus() == null || !historyPt
							.getDataStatus().equals("删除"))) {
				sonPt.setBzStatus(historyPt.getBzStatus());
			} else {
				sonPt.setBzStatus("待编制");
				sonPt.setBianzhiId(user.getId());
				sonPt.setBianzhiName(user.getName());
			}
			if (sonPt.getProcardStyle().equals("待定")) {
				if (historyPt.getProcardStyle().equals("总成")) {
					sonPt.setProcardStyle("自制");
				} else {
					sonPt.setProcardStyle(historyPt.getProcardStyle());
				}
			}
			sonPt.setYwMarkId(ywmarkId);
			sonPt.setHistoryId2(historyPt.getId());
			if (youxiao == 1 && historyPt.getBanbenStatus() != null
					&& historyPt.getBanbenStatus().equals("已批准")) {
				if (sonPt.getProcardStyle() != null
						&& !sonPt.getProcardStyle().equals("外购")) {// 非外购件连带长宽高
					sonPt.setQuanzi1(historyPt.getQuanzi1());
					sonPt.setQuanzi2(historyPt.getQuanzi2());
					sonPt.setThisLength(historyPt.getThisLength());
					sonPt.setThisWidth(historyPt.getThisWidth());
					sonPt.setThisHight(historyPt.getThisHight());
				}
			}
			if (historyPt.getProcardStyle().equals("总成")) {
				sonPt.setProcardStyle("自制");
			}
			String banciSql = null;
			if (historyPt.getBanci() == null || historyPt.getBanci() == 0) {
				banciSql = "and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + historyPt.getBanci();
			}
			// 同步工序
			Set<ProcessTemplatesb> processSet1 = historyPt.getProcessTemplatesb();
			Set<ProcessTemplatesb> processSet2 = new HashSet<ProcessTemplatesb>();
			if (processSet1 != null) {// 添加在添加列表中存在的工序号的工序
				for (ProcessTemplatesb process1 : processSet1) {
					if (process1.getProcessNO() != null) {
						ProcessTemplatesb process2 = new ProcessTemplatesb();
						// -----------辅料开始----------
//						if (process1.getIsNeedFuliao() != null
//								&& process1.getIsNeedFuliao().equals("yes")) {
//							Set<ProcessFuLiaoTemplate> fltmpSet = process1
//									.getProcessFuLiaoTemplate();
//							if (fltmpSet.size() > 0) {
//								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
//								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
//									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
//									BeanUtils.copyProperties(fltmp, pinfoFl,
//											new String[] { "id",
//													"processTemplate" });
//									if (pinfoFl.getQuanzhi1() == null) {
//										pinfoFl.setQuanzhi1(1f);
//									}
//									if (pinfoFl.getQuanzhi2() == null) {
//										pinfoFl.setQuanzhi2(1f);
//									}
//									pinfoFl.setProcessTemplate(process2);
//									pinfoFlSet.add(pinfoFl);
//								}
//								process2.setProcessFuLiaoTemplate(pinfoFlSet);
//							}
//						}
						// -----------辅料结束----------
						BeanUtils
						.copyProperties(process1, process2,
								new String[] { "id", "procardTemplatesb"});
						process2.setProcardTemplatesb(sonPt);
						totalDao.save(process2);
						processSet2.add(process2);
						if ("试制".equals(sonPt.getProductStyle())) {
							List<ProcessTemplateFilesb> ptFileList = null;
							if ("试制".equals(historyPt.getProductStyle())) {
								ptFileList = totalDao.query(
										"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
										+ banciSql + " and glId=? ",historyPt.getSbApplyId(),
										process2.getProcessNO(), process1
										.getId());
							} else {
								ptFileList = totalDao
								.query(
										"from ProcessTemplateFilesb where sbApplyId=? and processNO =? "
										+ "  and markId=? and productStyle='批产' "
										+ banciSql,historyPt.getSbApplyId(), process2
										.getProcessNO(), sonPt
										.getMarkId());
							}
							if (ptFileList != null && ptFileList.size() > 0) {
								for (ProcessTemplateFilesb ptFile : ptFileList) {
									if (ptFile != null) {
										ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
										BeanUtils
										.copyProperties(ptFile,
												ptFile2, new String[] {
												"id",
												"productStyle",
										"glId" });
										ptFile2.setProductStyle(sonPt.getProductStyle());
										ptFile2.setSbApplyId(sonPt.getSbApplyId());
										ptFile2.setProcesstFileId(ptFile.getId());
										ptFile2.setGlId(process2.getId());
										totalDao.save(ptFile2);
									}
								}
							}
						}
						
					}
				}
			}
			if ("试制".equals(sonPt.getProductStyle())) {
				if (sonPt.getId() == null) {
					totalDao.save(sonPt);
				}
				List<ProcessTemplateFilesb> ptFileList = null;
				if ("试制".equals(historyPt.getProductStyle())) {
					ptFileList = totalDao.query(
							"from ProcessTemplateFilesb where sbApplyId=? and processNO is null "
							+ banciSql + " and glId=? ",historyPt.getSbApplyId(), historyPt
							.getId());
				} else {
					ptFileList = totalDao.query(
							"from ProcessTemplateFilesb where sbApplyId=? and processNO is null "
							+ "  and markId=? and productStyle='批产' "
							+ banciSql,historyPt.getSbApplyId(), sonPt.getMarkId());
				}
				if (ptFileList != null && ptFileList.size() > 0) {
					for (ProcessTemplateFilesb ptFile : ptFileList) {
						if (ptFile != null) {
							ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
							BeanUtils
							.copyProperties(ptFile, ptFile2,
									new String[] { "id",
									"productStyle", "glId" });
							ptFile2.setProductStyle(sonPt.getProductStyle());
							ptFile2.setSbApplyId(sonPt.getSbApplyId());
							ptFile2.setProcesstFileId(ptFile.getId());
							ptFile2.setGlId(sonPt.getId());
							totalDao.save(ptFile2);
						}
					}
				}
			}
			
			sonPt.setProcessTemplatesb(processSet2);
		} catch (BeansException e) {
			System.out.println(historyPt.getId()
					+ " ---------------------------");
			e.printStackTrace();
		}
	}
	
	private String isSameProcardStyle(ProcardTemplatesb procardT){
		//判断同件号，同业务件号，同产品类型，只能有同一卡片类型。
		int  count =totalDao.getCount(" from ProcardTemplatesb where sbApplyId=? and markId =? and ywMarkId =?  " +
				" and productStyle =? and procardStyle <> ? and " +
				" (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '删除')",procardT.getSbApplyId(), procardT.getMarkId(),procardT.getYwMarkId(),
				procardT.getProductStyle(),procardT.getProcardStyle());
		if(count>0){
			return "同业务件号下，件号:["+procardT.getMarkId()+"]，有不属于:["+procardT.getProcardStyle()+"]的卡片类型，不符合工艺规范，请检查!~";
		}
		return "";
	}
	private void fenmojisuan(YuanclAndWaigj fenmowgj1, List<String> sonMarkIds,
			String sqlhistory, ProcardTemplate historyPt1, String sqlhistory1,
			String ywmarkId, ProcardTemplatesb sonPt, Float mianji,
			Integer miancount, Set<ProcardTemplatesb> sonsetPt0, Users user) {
		if (fenmowgj1 != null && !sonMarkIds.contains(fenmowgj1.getMarkId())) {
			if (fenmowgj1.getBanbenhao() == null
					|| fenmowgj1.getBanbenhao().length() == 0) {// 无版本号
				sqlhistory += " and (banBenNumber is null or banBenNumber='')";
			} else {
				sqlhistory += " and banBenNumber ='" + fenmowgj1.getBanbenhao()
						+ "'";
			}
			ProcardTemplatesb wgpt = new ProcardTemplatesb();
			historyPt1 = (ProcardTemplate) totalDao.getObjectByCondition(
					sqlhistory1, fenmowgj1.getMarkId());
			if (historyPt1 != null) {
				BeanUtils.copyProperties(historyPt1, wgpt,
						new String[] { "id", "procardTemplate", "procardTSet",
								"processTemplate", "zhUsers", "quanzi1",
								"quanzi2", "maxCount", "corrCount", "rootId",
								"fatherId", "belongLayer", "cardXiaoHao",
								"loadMarkId", "ywMarkId", "historyId",
								"thisLength", "thisWidth", "thisHight",
								"productStyle" });
				wgpt.setYwMarkId(ywmarkId);
				wgpt.setHistoryId(historyPt1.getId());
			} else {
				wgpt.setMarkId(fenmowgj1.getMarkId());// 件号
				wgpt.setWgType(fenmowgj1.getWgType());// 物料类别
				wgpt.setCaizhi(fenmowgj1.getCaizhi());// 材质
				wgpt.setProName(fenmowgj1.getName());// 名称\
				wgpt.setBanBenNumber(fenmowgj1.getBanbenhao());// 版本
				wgpt.setKgliao(fenmowgj1.getKgliao());// 供料属性
				wgpt.setSpecification(fenmowgj1.getSpecification());// 规格
				wgpt.setUnit(fenmowgj1.getUnit());// 单位
				wgpt.setProcardStyle("外购");
			}
			wgpt.setBelongLayer(sonPt.getBelongLayer() + 1);// 层数
			wgpt.setFatherId(sonPt.getId());
			wgpt.setRootMarkId(sonPt.getRootMarkId());
			wgpt.setRootId(sonPt.getRootId());
			wgpt.setProductStyle(sonPt.getProductStyle());
			wgpt.setQuanzi1(1f);
			Float quanzhi2 = 0f;
			if(fenmowgj1.getAreakg()==null){
				throw new RuntimeException(fenmowgj1.getMarkId()+"没有填写每公斤喷粉面积");
			}
			quanzhi2 = (mianji * miancount / fenmowgj1.getAreakg()) / 1000000;
			wgpt.setQuanzi2(quanzhi2);
			sonsetPt0.add(wgpt);
			wgpt.setProcardTemplatesb(sonPt);
			if (wgpt.getBzStatus() == null) {
				wgpt.setBzStatus("待编制");
			}
			if (!wgpt.getBzStatus().equals("已批准")
					&& !wgpt.getBzStatus().equals("设变发起中")) {
				wgpt.setBianzhiName(user.getName());
				wgpt.setBianzhiId(user.getId());
			}
			wgpt.setRemark("板材粉末自动计算添加");
			totalDao.save(wgpt);
			sonPt.setProcardsbTSet(sonsetPt0);
			if ("试制".equals(sonPt.getProductStyle())) {
				List<ProcessTemplateFile> ptFileList = totalDao
						.query(
								"from ProcessTemplateFile where processNO is null "
										+ " and status='默认' and markId=? and glId is null ",
								wgpt.getMarkId());
				if (ptFileList != null && ptFileList.size() > 0) {
					for (ProcessTemplateFile ptFile : ptFileList) {
						ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
						BeanUtils.copyProperties(ptFile, ptFile2, new String[] {
								"id", "productStyle", "glId" });
						ptFile2.setGlId(wgpt.getId());
						ptFile2.setProductStyle("试制");
						ptFile2.setSbApplyId(sonPt.getSbApplyId());
						ptFile2.setProcesstFileId(wgpt.getId());
						totalDao.save(ptFile2);
					}
				}

			}
		}
	}
	@Override
	public String checkAndUpdateTz(Integer id, String path, String path2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplatesb pt = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId=? and id=rootId", id);
		Float tqCount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardTemplatePrivilege where markId=? or markId=? ",
						pt.getMarkId(), pt.getYwMarkId() == null ? "" : pt
								.getYwMarkId());
		if (pt != null ) {
			// boolean fg=false;
			boolean fg = true;
			// if(pt.getBianzhiId()!=null&&pt.getBianzhiId().equals(user.getId())){
			// fg=true;
			// }

			String month = Util.getDateTime("yyyy-MM");
			if (pt.getYwMarkId() != null || pt.getMarkId() != null) {
				// 件号查找
				File file = null;
				if (pt.getYwMarkId() == null || pt.getYwMarkId().length() == 0) {
					file = new File(path + "\\" + pt.getMarkId());
				} else {
					file = new File(path + "\\" + pt.getYwMarkId());
					if (!file.exists()) {
						file = new File(path + "\\" + pt.getMarkId());
					}
				}
				if (!file.exists()) {
					return "对不起没有找到对应的文件";
				}
				File[] sonList1 = file.listFiles();
				if (sonList1 != null && sonList1.length > 0) {
					// 将文件放入特定位置
					File cfdz = new File(path2 + "/upload/file/processTz/"
							+ month);// 存放图纸的文件夹
					if (!cfdz.exists()) {
						cfdz.mkdirs();// 如果不存在文件夹就创建
					}
					StringBuffer drmes = new StringBuffer();
					StringBuffer errMsgSb = new StringBuffer();
					for (File son1 : sonList1) {
						if (son1.isDirectory() && son1.getName() != null) {
							if (son1.getName().equals("3D文件")) {
								sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "3D文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "3D文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功3D文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("3D模型")) {
								sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.getName() != null) {
											String modelpath = path2
													+ "/upload/file/processTz/3Dmodel";
											File modelDir = new File(modelpath);// 存放图纸的文件夹
											if (!modelDir.exists()) {
												modelDir.mkdirs();// 如果不存在文件夹就创建
											}
											String fileName = son2.getName();
											String markId = null;
											// if(fileName.lastIndexOf(".")>0){
											// markId = fileName.substring(0,
											// fileName.lastIndexOf("."));
											// }else{
											markId = fileName;
											// }
//											ProcardTemplatesb pt2 = (ProcardTemplatesb) totalDao
//													.getObjectByCondition(
//															"from ProcardTemplatesb where id=? and (markId=? or ywMarkId=?) and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
//															id, markId, markId);// 3D模型只管总成
											ProcardTemplatesb pt2 = pt;// 3D模型只管总成

											if (pt2 == null) {
												errMsgSb.append(fileName
														+ "没有找到对应的零件!<br/>");
											} else {
												// if
												// (pt.getBzStatus().equals("已批准"))
												// {
												// return typeName + fileName +
												// "对应的零件状态为已批准不能导入图纸!<br/>";
												// }
												markId = pt2.getMarkId();
												Integer banci = pt2.getBanci() == null ? 0
														: pt2.getBanci();
												String producttag = null;
												if ("试制".equals(pt
														.getProductStyle())) {
													producttag = "sz";
												} else {
													producttag = "pc";
												}
												String ywMarkId = null;
												if (pt2.getYwMarkId() == null
														|| pt2.getYwMarkId()
																.length() == 0) {
													ywMarkId = pt2.getMarkId();
												} else {
													ywMarkId = pt2
															.getYwMarkId();
												}
												String banben = pt2
														.getBanBenNumber() == null ? ""
														: pt2.getBanBenNumber();
												String realFileName = ywMarkId
														+ "-" + producttag
														+ "-" + banben;
												// + "-" + banci;//不管版次了版本通用
												String modelpath2 = path2
														+ "/upload/file/processTz/3Dmodel/"
														+ realFileName;
												//删除文件夹下的文件
												Util.delAllFile(modelpath2);
												//删除文件架
												Util.delFolder(modelpath2);
												File modelDir2 = new File(
														modelpath2);// 存放图纸的文件夹
												// 覆盖之前存在的
												String bancisql = null;
												if (pt2.getBanci() == null
														|| pt2.getBanci() == 0) {
													bancisql = " and (banci is null or banci=0)";
												} else {
													bancisql = " and banci="
															+ pt2.getBanci();
												}

												String sqlhadtz = "from ProcessTemplateFilesb where sbApplyId.id=? and type='3D模型' and markId=? "
														+ bancisql;//3D模型只保留一份
												List<ProcessTemplateFilesb> hadFileList = totalDao
														.query(sqlhadtz,pt.getSbApplyId(),pt2.getMarkId());
												if (hadFileList != null
														&& hadFileList.size() > 0) {
													int logn = 0;
													for (ProcessTemplateFilesb hadfile : hadFileList) {
														// if
														// (!fg&&hadfile.getUserCode()
														// != null
														// && !hadfile
														// .getUserCode()
														// .equals(
														// user
														// .getCode())) {
														// errMsgSb
														// .append("3D文件"
														// + fileName
														// +
														// "重复,不是同一人且不是编制人上传!<br/>");
														// b = false;
														// continue;
														// }
														// 找到文件删除
//														if (logn == 0) {
//															ProcardTemplateChangeLog
//																	.addchangeLog(
//																			totalDao,
//																			pt,
//																			hadfile,
//																			"图纸",
//																			"删除",
//																			user,
//																			Util
//																					.getDateTime());
//														}
														logn++;
														totalDao
																.delete(hadfile);
													}
												}
												if (!modelDir2.exists()) {
													modelDir2.mkdirs();// 如果不存在文件夹就创建
												}
												ProcessTemplateFilesb ptFile = new ProcessTemplateFilesb();
												ptFile.setSbApplyId(pt.getSbApplyId());
												ptFile.setProductStyle(pt
														.getProductStyle());
												ptFile.setBanci(banci);
												ptFile.setMarkId(markId);

												String realFileName2 = null;
												ptFile
														.setFileName(realFileName);// 文件名
												ptFile
														.setFileName2(realFileName2);// 文件名
												ptFile.setOldfileName(fileName);// 文件原名称
												ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
												ptFile.setType("3D模型");// 文件类型(视频文件，工艺规范)
												ptFile.setStatus("默认");// 默认,历史
												ptFile.setUserName(user
														.getName());// 上传人姓名
												ptFile.setUserCode(user
														.getCode());// 上传人工号
												ptFile
														.setAddTime(Util
																.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
												ptFile.setProcessName(pt
														.getProName());
												ptFile.setProcessNO(null);
												if (pt.getProductStyle()
														.equals("试制")) {
													ptFile.setGlId(pt.getId());
													ptFile
															.setProductStyle("试制");
												}
												totalDao.save(ptFile);
//												ProcardTemplateChangeLog
//														.addchangeLog(
//																totalDao,
//																pt,
//																ptFile,
//																"图纸",
//																"添加",
//																user,
//																Util
//																		.getDateTime());
												// 遍历添加下层文件及文件夹
												addModelSonFil(son2, path2,
														"/upload/file/processTz/3Dmodel/"
																+ realFileName);
												sdnum++;

												// String msg =
												// checkAndUpdateTz2(id,
												// son2.getName(), "3D模型",
												// null, son2, cfdz, month,
												// user);
												// if (!msg.equals("true")) {
												// errMsgSb.append(msg);
												// }
											}
										}
									}
								}
								drmes.append("导入成功3D模型:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("视频文件")
									|| son1.getName().equals("视频")) {
								sdnum = 0;// 上传成功视频文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "视频文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "视频文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功视频文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("工艺规范")) {
								sdnum = 0;// 上传成功工艺规范文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.getName() != null
												&& !"Thumbs.db".equals(son2
														.getName())) {
											if (son2.isDirectory()
													&& (son2.getName().equals(
															"工艺卡")
															|| son2
																	.getName()
																	.equals(
																			"原图") || son2
															.getName().equals(
																	"展开图"))) {
												File[] sonList3 = son2
														.listFiles();
												if (sonList3 != null
														&& sonList3.length > 0) {
													for (File son3 : sonList3) {
														if (!son3.isDirectory()
																&& son3
																		.getName() != null
																&& !"Thumbs.db"
																		.equals(son3
																				.getName())) {
															String msg = null;
															if (pt
																	.getProductStyle()
																	.equals(
																			"试制")) {
																msg = checkAndUpdateTz3(
																		pt.getId(),
																		son3
																				.getName(),
																		"工艺规范",
																		son2,
																		son3,
																		cfdz,
																		month,
																		user,
																		tqCount,pt.getSbApplyId());
															} else {
																msg = checkAndUpdateTz2(
																		pt.getId(),
																		son3
																				.getName(),
																		"工艺规范",
																		son2,
																		son3,
																		cfdz,
																		month,
																		user,
																		tqCount,pt.getSbApplyId());
															}
															if (!msg
																	.equals("true")) {
																errMsgSb
																		.append(msg);
															}
														}
													}
												}
											} else {
												for (File son3 : sonList2) {
													if (!son3.isDirectory()
															&& son3.getName() != null
															&& !"Thumbs.db"
																	.equals(son3
																			.getName())) {
														String msg = null;
														if (pt
																.getProductStyle()
																.equals("试制")) {
															msg = checkAndUpdateTz3(
																	pt.getId(),
																	son3
																			.getName(),
																	"工艺规范",
																	son2, son3,
																	cfdz,
																	month,
																	user,
																	tqCount,pt.getSbApplyId());
														} else {
															msg = checkAndUpdateTz2(
																	pt.getId(),
																	son3
																			.getName(),
																	"工艺规范",
																	son2, son3,
																	cfdz,
																	month,
																	user,
																	tqCount,pt.getSbApplyId());
														}
														if (!msg.equals("true")) {
															errMsgSb
																	.append(msg);
														}
													}
												}
											}
										}
									}
								}
								drmes.append("导入成功工艺规范文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("成型图")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "成型图",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "成型图",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功成型图:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("标签文件")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "标签文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "标签文件",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功标签文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("编程文件")) {
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.getName().equals("NC数冲")
												|| son2.getName().equals("镭射")) {
											sdnum = 0;// 上传成功成型图数量
											File[] sonList3 = son2.listFiles();
											if (sonList3 != null
													&& sonList3.length > 0) {
												for (File son3 : sonList3) {
													if (!son2.isDirectory()
															&& son2.getName() != null) {
														String msg = null;
														if (pt
																.getProductStyle()
																.equals("试制")) {
															msg = checkAndUpdateTz3(
																	pt.getId(),
																	son2
																			.getName(),
																	son2
																			.getName(),
																	null, son2,
																	cfdz,
																	month,
																	user,
																	tqCount,pt.getSbApplyId());
														} else {
															msg = checkAndUpdateTz2(
																	pt.getId(),
																	son2
																			.getName(),
																	son2
																			.getName(),
																	null, son2,
																	cfdz,
																	month,
																	user,
																	tqCount,pt.getSbApplyId());
														}
														if (!msg.equals("true")) {
															errMsgSb
																	.append(msg);
														}
													}
												}
											}
											drmes.append("导入成功"
													+ son2.getName() + ":"
													+ sdnum + "份<br/>");
										}

									}
								}
								// return "导入成功！";
							}
							if (son1.getName().equalsIgnoreCase("SOP")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "SOP",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "SOP",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功SOP文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equalsIgnoreCase("SAP")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(pt.getId(),
														son2.getName(), "SAP",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											} else {
												msg = checkAndUpdateTz2(pt.getId(),
														son2.getName(), "SAP",
														null, son2, cfdz,
														month, user, tqCount,pt.getSbApplyId());
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
									}
								}
								drmes.append("导入成功SAP文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("其它")
									|| son1.getName().equals("其它文件")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											try {
												if (pt.getProductStyle()
														.equals("试制")) {
													msg = checkAndUpdateTz3(pt.getId(),
															son2.getName(),
															"其它文件", null, son2,
															cfdz, month, user,
															tqCount,pt.getSbApplyId());
												} else {
													msg = checkAndUpdateTz2(pt.getId(),
															son2.getName(),
															"其它文件", null, son2,
															cfdz, month, user,
															tqCount,pt.getSbApplyId());
												}
												if (!msg.equals("true")) {
													errMsgSb.append(msg);
												}
											} catch (Exception e) {
												// TODO: handle exception
												errMsgSb.append(e.getMessage());
											}

										}
									}
								}
								drmes.append("导入成功其它文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
						}
					}
					return drmes.toString() + "<br/>" + errMsgSb.toString();
				}
			}
		}
		return "对不起没有找到目标BOM";
	}
	
	/**
	 * 
	 * @param rootId
	 *            总成id
	 * @param fileName
	 *            文件名称
	 * @param typeName
	 *            文件类型
	 * @param up
	 *            上层文件夹（工艺规范类型专用）
	 * @param son2
	 *            要导入的文件
	 * @param cfdz
	 *            地址前缀
	 * @param month
	 *            保存文件的文件夹月份名
	 * @param user
	 *            操作用户
	 * @return
	 */
	public String checkAndUpdateTz2(Integer rootId, String fileName,
			String typeName, File up, File son2, File cfdz, String month,
			Users user, Float tqCount,Integer sbApplyId) {
		String markId = fileName.substring(0, fileName.lastIndexOf("."));
		if (markId.indexOf("-原图") > 0) {
			markId = markId.substring(0, markId.indexOf("-原图"));
		} else if (markId.indexOf("-展开图") > 0) {
			markId = markId.substring(0, markId.indexOf("-展开图"));
		} else if (markId.indexOf("-工艺卡") > 0) {
			markId = markId.substring(0, markId.indexOf("-工艺卡"));
		} else if (markId.indexOf("-成型图") > 0) {
			markId = markId.substring(0, markId.indexOf("-成型图"));
		}
		ProcardTemplatesb pt = (ProcardTemplatesb) totalDao
				.getObjectByCondition(
						"from ProcardTemplatesb where rootId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
						rootId, markId);
		if (pt == null) {
			return typeName + fileName + "没有找到对应的零件!<br/>";
		}
//		String bltzhql = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准零件补漏图纸'";
//		String bltz = (String) totalDao.getObjectByCondition(bltzhql);
//		if (bltz == null || !bltz.equals("是")) {
			if (pt.getBzStatus() != null && pt.getBzStatus().equals("已批准")
					&& (tqCount == null || tqCount == 0)) {
				return typeName + fileName + "对应的零件,已批准无法上传图纸!<br/>";
			}
//		}
		boolean b = true;
		String type = fileName.substring(fileName.lastIndexOf("."), fileName
				.length());
		// 覆盖之前存在的
		String sqlhadtz = "from ProcessTemplateFilesb where sbApplyId=? and productStyle=? and type='"
				+ typeName + "'";
		if (pt.getBanci() == null || pt.getBanci() == 0) {
			sqlhadtz += " and (banci is null or banci =0)";
		} else {
			sqlhadtz += " and banci =" + pt.getBanci();
		}
		if (type.equalsIgnoreCase(".jpeg")) {
			String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
			String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".jpg")) {
			String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
			String fileName2 = fileName.replaceAll(".jpg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".PDF")) {
			fileName = fileName.replaceAll(".pdf", ".PDF");
			String fileName1 = fileName.replaceAll(".PDF", ".jpg");
			String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else {
			sqlhadtz += " and oldfileName='" + fileName + "'";
		}
		List<ProcessTemplateFilesb> hadFileList = totalDao.query(sqlhadtz,sbApplyId, pt
				.getProductStyle());
		if (hadFileList != null && hadFileList.size() > 0) {
			if (pt.getBzStatus() != null && pt.getBzStatus().equals("已批准")) {
				return typeName + fileName + "对应的零件,已批准无法覆盖图纸!<br/>";
			}
			int logn = 0;
			for (ProcessTemplateFilesb hadfile : hadFileList) {
//				if (logn == 0) {
//					ProcardTemplateChangeLog.addchangeLog(totalDao, pt,
//							hadfile, "图纸", "删除", user, Util.getDateTime());
//				}
				logn++;
				totalDao.delete(hadfile);
			}
		}
		if (b) {
			Integer banci = pt.getBanci();
			if (banci == null) {
				banci = 0;
			}
			ProcessTemplateFilesb ptFile = new ProcessTemplateFilesb();
			ptFile.setSbApplyId(sbApplyId);
			ptFile.setProductStyle(pt.getProductStyle());
			ptFile.setBanBenNumber(pt.getBanBenNumber());
			ptFile.setBanci(banci);
			ptFile.setMarkId(markId);
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS") + type;
			String realFileName2 = null;
			if (type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".dib")
					|| type.equalsIgnoreCase(".gif")
					|| type.equalsIgnoreCase(".jfif")
					|| type.equalsIgnoreCase(".jpe")
					|| type.equalsIgnoreCase(".jpeg")
					|| type.equalsIgnoreCase(".jpg")
					|| type.equalsIgnoreCase(".png")
					|| type.equalsIgnoreCase(".tif")
					|| type.equalsIgnoreCase(".tiff")
					|| type.equalsIgnoreCase(".ico")
					|| type.equalsIgnoreCase(".pdf")) {
				realFileName2 = "jz_" + realFileName;
			}
			ptFile.setFileName(realFileName);// 文件名
			ptFile.setFileName2(realFileName2);// 文件名
			ptFile.setOldfileName(fileName);// 文件原名称
			ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
			ptFile.setType(typeName);// 文件类型(视频文件，工艺规范)
			ptFile.setStatus("默认");// 默认,历史
			ptFile.setUserName(user.getName());// 上传人姓名
			ptFile.setUserCode(user.getCode());// 上传人工号
			ptFile.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
			ptFile.setProcessName(pt.getProName());
			ptFile.setProcessNO(null);
			Upload upload = new Upload();
			String msg = upload.UploadFile(son2, fileName, realFileName,
					"/upload/file/processTz/" + month, null);
			if (realFileName2 != null) {
				if (type.equalsIgnoreCase(".bmp")
						|| type.equalsIgnoreCase(".dib")
						|| type.equalsIgnoreCase(".gif")
						|| type.equalsIgnoreCase(".jfif")
						|| type.equalsIgnoreCase(".jpe")
						|| type.equalsIgnoreCase(".jpeg")
						|| type.equalsIgnoreCase(".jpg")
						|| type.equalsIgnoreCase(".png")
						|| type.equalsIgnoreCase(".tif")
						|| type.equalsIgnoreCase(".tiff")
						|| type.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				} else if (type.equalsIgnoreCase(".pdf")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					try {
						Util.markPDFByIcon(icon_fileRealPath, cfdz + "/"
								+ realFileName, cfdz + "/" + realFileName2);
					} catch (Exception e) {
						// TODO: handle exception
						throw new RuntimeException(fileName + "文件有问题!");
					}
				}
				if (pt.getBzStatus().equals("已批准")) {// 直接加章
					ptFile.setFileName(realFileName2);// 文件名
					ptFile.setFileName2(realFileName);// 文件名
				}
			}
			totalDao.save(ptFile);
//			ProcardTemplateChangeLog.addchangeLog(totalDao, pt, ptFile, "图纸",
//					"添加", user, Util.getDateTime());
			if (typeName.equals("工艺规范") || typeName.equals("3D文件")) {// 工艺规范或者3D文件下发工序
				Set<ProcessTemplatesb> processSet = pt.getProcessTemplatesb();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplatesb process : processSet) {
						if (process.getDataStatus() != null
								&& process.getDataStatus().equals("删除")) {
							continue;
						}
						ProcessTemplateFilesb processFile = new ProcessTemplateFilesb();
						processFile.setSbApplyId(sbApplyId);
						processFile.setBanBenNumber(pt.getBanBenNumber());
						processFile.setBanci(banci);
						processFile.setProductStyle(pt.getProductStyle());
						processFile.setMarkId(markId);
						processFile.setFileName(ptFile.getFileName());// 文件名
						processFile.setFileName2(ptFile.getFileName2());// 文件名
						processFile.setOldfileName(fileName);// 文件原名称
						processFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
						processFile.setType(typeName);// 文件类型(视频文件，工艺规范)
						processFile.setStatus("默认");// 默认,历史
						processFile.setUserName(user.getName());// 上传人姓名
						processFile.setUserCode(user.getCode());// 上传人工号
						processFile.setAddTime(Util
								.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
						processFile.setProcessName(process.getProcessName());
						processFile.setProcessNO(process.getProcessNO());
						totalDao.save(processFile);
					}
				}
			}
			sdnum++;
		}
		return b + "";
	}

	/**
	 * 
	 * @param rootId
	 *            总成id
	 * @param fileName
	 *            文件名称
	 * @param typeName
	 *            文件类型
	 * @param up
	 *            上层文件夹（工艺规范类型专用）
	 * @param son2
	 *            要导入的文件
	 * @param cfdz
	 *            地址前缀
	 * @param month
	 *            保存文件的文件夹月份名
	 * @param user
	 *            操作用户
	 * @param tqcount
	 *            特权
	 * @return
	 */
	public String checkAndUpdateTz3(Integer rootId, String fileName,
			String typeName, File up, File son2, File cfdz, String month,
			Users user, Float tqCount,Integer sbApplyId ) {
		String markId = fileName.substring(0, fileName.lastIndexOf("."));
		if (typeName.equals("NC数冲") || typeName.equals("镭射")) {
			if (markId.startsWith("YT") || markId.startsWith("yt")) {
				markId = markId.substring(0, 3) + "." + markId.substring(3, 6)
						+ "." + markId.substring(6, markId.length());
			} else if (markId.startsWith("DKBA") || markId.startsWith("dkba")) {
				markId = markId.substring(0, 5) + "." + markId.substring(5, 8)
						+ "." + markId.substring(8, markId.length());
			}
		}
		if (markId.indexOf("-原图") > 0) {
			markId = markId.substring(0, markId.indexOf("-原图"));
		} else if (markId.indexOf("-展开图") > 0) {
			markId = markId.substring(0, markId.indexOf("-展开图"));
		} else if (markId.indexOf("-工艺卡") > 0) {
			markId = markId.substring(0, markId.indexOf("-工艺卡"));
		} else if (markId.indexOf("-成型图") > 0) {
			markId = markId.substring(0, markId.indexOf("-成型图"));
		}
		String banbenNumber = (String) totalDao
				.getObjectByCondition(
						"select banBenNumber from ProcardTemplatesb where rootId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
						rootId, markId);
		String banbenSql = null;
		if (banbenNumber == null || banbenNumber.length() == 0) {
			banbenSql = " and (banBenNumber is null or banBenNumber='')";
		} else {
			banbenSql = " and banBenNumber = '" + banbenNumber + "'";
		}
		List<ProcardTemplatesb> ptList = (List<ProcardTemplatesb>) totalDao
				.query(
						"from ProcardTemplatesb where sbApplyId=? and  markId=? and productStyle='试制'  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') "
								+ banbenSql,sbApplyId, markId);
		if (ptList == null || ptList.size() == 0) {
			return typeName + fileName + "没有找到对应的零件!<br/>";
			// } else if (pt.getBzStatus().equals("已批准")) {
			// return typeName + fileName + "对应的零件状态为已批准不能导入图纸!<br/>";
		} else {
			ProcardTemplatesb pt = ptList.get(0);
			if (pt.getBzStatus() != null && pt.getBzStatus().equals("已批准")
					&& (tqCount == null || tqCount == 0)) {
				return typeName + fileName + "对应的零件,已批准无法上传图纸!<br/>";
			}
		}
		boolean b = true;
		String type = fileName.substring(fileName.lastIndexOf("."), fileName
				.length());
		// 覆盖之前存在的
		String sqlhadtz = "from ProcessTemplateFilesb where sbApplyId=? and productStyle=? and type='"
				+ typeName + "'";
		if (ptList.get(0).getBanci() == null || ptList.get(0).getBanci() == 0) {
			sqlhadtz += " and (banci is null or banci =0)";
		} else {
			sqlhadtz += " and banci =" + ptList.get(0).getBanci();
		}
		if (type.equalsIgnoreCase(".jpeg")) {
			String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
			String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".jpg")) {
			String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
			String fileName2 = fileName.replaceAll(".jpg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".PDF")) {
			fileName = fileName.replaceAll(".pdf", ".PDF");
			String fileName1 = fileName.replaceAll(".PDF", ".jpg");
			String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else {
			sqlhadtz += " and oldfileName='" + fileName + "'";
		}

		sqlhadtz += " and ((glId in(select id from ProcardTemplate where  markId='"
				+ markId
				+ "'"
				+ banbenSql
				+ " and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')) "
				+ " and processNO is null) or (processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id in "
				+ "(select id from ProcardTemplate where markId='"
				+ markId
				+ "' "
				+ banbenSql
				+ "and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')))))";
		List<ProcessTemplateFilesb> hadFileList = totalDao.query(sqlhadtz, sbApplyId, "试制");
		if (hadFileList != null && hadFileList.size() > 0) {
			int logn = 0;
			for (ProcessTemplateFilesb hadfile : hadFileList) {
//				if (logn == 0) {
//					ProcardTemplateChangeLog.addchangeLog(totalDao, ptList
//							.get(0), hadfile, "图纸", "删除", user, Util
//							.getDateTime());
//				}
				logn++;
				totalDao.delete(hadfile);
			}
		}
		if (b) {
			Integer banci = ptList.get(0).getBanci();
			if (banci == null) {
				banci = 0;
			}
			ProcessTemplateFilesb ptFile = new ProcessTemplateFilesb();
			ptFile.setSbApplyId(sbApplyId);
			ptFile.setProductStyle(ptList.get(0).getProductStyle());
			ptFile.setBanBenNumber(ptList.get(0).getBanBenNumber());
			ptFile.setBanci(banci);
			ptFile.setMarkId(markId);
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS") + type;
			String realFileName2 = null;
			if (type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".dib")
					|| type.equalsIgnoreCase(".gif")
					|| type.equalsIgnoreCase(".jfif")
					|| type.equalsIgnoreCase(".jpe")
					|| type.equalsIgnoreCase(".jpeg")
					|| type.equalsIgnoreCase(".jpg")
					|| type.equalsIgnoreCase(".png")
					|| type.equalsIgnoreCase(".tif")
					|| type.equalsIgnoreCase(".tiff")
					|| type.equalsIgnoreCase(".ico")
					|| type.equalsIgnoreCase(".pdf")) {
				realFileName2 = "jz_" + realFileName;
			}

			ptFile.setFileName(realFileName);// 文件名
			ptFile.setFileName2(realFileName2);// 文件名
			ptFile.setOldfileName(fileName);// 文件原名称
			ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
			ptFile.setType(typeName);// 文件类型(视频文件，工艺规范)
			ptFile.setStatus("默认");// 默认,历史
			ptFile.setUserName(user.getName());// 上传人姓名
			ptFile.setUserCode(user.getCode());// 上传人工号
			ptFile.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
			ptFile.setProcessName(ptList.get(0).getProName());
			ptFile.setProcessNO(null);
			Upload upload = new Upload();
			upload.UploadFile(son2, fileName, realFileName,
					"/upload/file/processTz/" + month, null);
			if (realFileName2 != null) {
				if (type.equalsIgnoreCase(".bmp")
						|| type.equalsIgnoreCase(".dib")
						|| type.equalsIgnoreCase(".gif")
						|| type.equalsIgnoreCase(".jfif")
						|| type.equalsIgnoreCase(".jpe")
						|| type.equalsIgnoreCase(".jpeg")
						|| type.equalsIgnoreCase(".jpg")
						|| type.equalsIgnoreCase(".png")
						|| type.equalsIgnoreCase(".tif")
						|| type.equalsIgnoreCase(".tiff")
						|| type.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				} else if (type.equalsIgnoreCase(".pdf")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					Util.markPDFByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				}
				if (ptList.get(0).getBzStatus().equals("已批准")) {// 直接加章
					ptFile.setFileName(realFileName2);// 文件名
					ptFile.setFileName2(realFileName);// 文件名
				}
			}
			for (ProcardTemplatesb pt : ptList) {
				ProcessTemplateFilesb ptFile2 = new ProcessTemplateFilesb();
				BeanUtils.copyProperties(ptFile, ptFile2);
				ptFile2.setGlId(pt.getId());
				ptFile2.setProductStyle("试制");
				totalDao.save(ptFile2);
			}
//			ProcardTemplateChangeLog.addchangeLog(totalDao, ptList.get(0),
//					ptFile, "图纸", "添加", user, Util.getDateTime());
			if (typeName.equals("工艺规范") || typeName.equals("3D文件")) {// 工艺规范或者3D文件下发工序
				for (ProcardTemplatesb pt : ptList) {
					Set<ProcessTemplatesb> processSet = pt.getProcessTemplatesb();
					if (processSet != null && processSet.size() > 0) {
						for (ProcessTemplatesb process : processSet) {
							if (process.getDataStatus() != null
									&& process.getDataStatus().equals("删除")) {
								continue;
							}
							ProcessTemplateFilesb processFile = new ProcessTemplateFilesb();
							processFile.setSbApplyId(sbApplyId);
							processFile.setBanBenNumber(pt.getBanBenNumber());
							processFile.setBanci(banci);
							processFile.setProductStyle(pt.getProductStyle());
							processFile.setMarkId(markId);
							processFile.setFileName(ptFile.getFileName());// 文件名
							processFile.setFileName2(ptFile.getFileName2());// 文件名
							processFile.setOldfileName(fileName);// 文件原名称
							processFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
							processFile.setType(typeName);// 文件类型(视频文件，工艺规范)
							processFile.setStatus("默认");// 默认,历史
							processFile.setUserName(user.getName());// 上传人姓名
							processFile.setUserCode(user.getCode());// 上传人工号
							processFile.setAddTime(Util
									.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
							processFile
									.setProcessName(process.getProcessName());
							processFile.setProcessNO(process.getProcessNO());
							processFile.setGlId(process.getId());
							processFile.setProductStyle("试制");
							totalDao.save(processFile);
						}
					}
				}
			}
			sdnum++;
		}
		return b + "";
	}
	
	private void addModelSonFil(File file, String before, String savePath) {
		// TODO Auto-generated method stub
		File[] sonList = file.listFiles();
		for (File sonFile : sonList) {
			if (sonFile.isDirectory()) {
				File directory = new File(before + savePath + "/"
						+ sonFile.getName());
				if (!directory.exists()) {
					directory.mkdirs();
				}
				addModelSonFil(sonFile, before, savePath + "/"
						+ sonFile.getName());
			} else if (sonFile.isFile()) {
				Upload upload = new Upload();
				upload.UploadFile(sonFile, null, sonFile.getName(), savePath,
						null);
			}
		}
	}
	@Override
	public List<ProcardTemplatesb> getGongxuPt(Integer id) {
		// TODO Auto-generated method stub
		String hql = "from ProcardTemplatesb where sbApplyId=? and (dataStatus is null or dataStatus!='删除') and (procardstyle in ('总成','自制','组合') or (needProcess ='yes' and procardstyle='外购')) " +
				"and id not in(select procardTemplatesb.id from ProcessTemplatesb where (dataStatus is null or dataStatus !='删除') and procardTemplatesb.id in (select id from ProcardTemplatesb where sbApplyId=? and (procardstyle in ('总成','自制','组合') or (needProcess ='yes' and procardstyle='外购'))))";
		List<ProcardTemplatesb> list = totalDao.query(hql, id, id);
		return list;
	}
	/****
	 * 一键导入工序
	 * 
	 * @param rootId
	 * @return
	 */
	@Override
	public Object[] updatedaoRuProcessTemplate(ProcardTemplatesb pt,
			Statement sql, ResultSet rs) {
		String addTime = Util.getDateTime();
		String processNOs = "";
		String processNames = "";
		String MarkIds = "";
		String errmess = "";
		int sucesscount = 0;
		int errorcount = 0;
		List<ProcessTemplatesb> processTemplatelist = new ArrayList<ProcessTemplatesb>();
		List<ProcardTemplatesb> sameList = totalDao
				.query(
						"from ProcardTemplatesb where sbApplyId=? and markId =? and (procardStyle in('总成','自制') or (procardStyle ='外购' and needProcess='yes')) and productStyle=?  and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
						pt.getSbApplyId(),pt.getMarkId(), pt.getProductStyle());
		try {
			rs = sql
					.executeQuery("select gyorder,procedurekind,proceduretext,TimeQty,ToolNo,ProMachine from GykTechnics where partid in "
							+ "(select partid from GykProDes where partno ='"
							+ pt.getMarkId()
							+ "') and gyorder is not null order by gyorder ");
			if (rs != null) {
				String processNO = "";
				String processName = "";
				String processExplain = "";
				Float timeQty = null;// 点数
				String toolNo = "";// 工装编号
				String proMachine = "";// 设备
				int oneerror = 0;
				List<String> hadprocessNoList = new ArrayList<String>();
				while (rs.next()) {
					processNO = rs.getString(1);
					if (hadprocessNoList.contains(processNO)) {
						processNO = (Integer.parseInt(processNO) + 1) + "";
						hadprocessNoList.add(processNO);
					} else {
						hadprocessNoList.add(processNO);
					}
					processName = rs.getString(2);
					processName = processName.replaceAll(" ", "");
					processName = processName.replaceAll("（", "(");
					processName = processName.replaceAll("）", ")");
					if ("装配".equals(processName)) {
						processName = "组装";
					}
					// processExplain = rs.getString(3);
					// if (processExplain != null
					// && processExplain.length() > 0) {
					// for (int n = 10; n < 14; n++) {
					// processExplain = processExplain
					// .replaceAll(String
					// .valueOf((char) n), "");
					// }
					// }

					timeQty = rs.getFloat(4);
					toolNo = rs.getString(5);
					if (toolNo != null && toolNo.length() > 0) {
						toolNo = toolNo.replaceAll(" ", "");
						toolNo = toolNo.replaceAll("（", "(");
						toolNo = toolNo.replaceAll("）", ")");
					}
					proMachine = rs.getString(6);
					if (proMachine != null && proMachine.length() > 0) {
						proMachine = proMachine.replaceAll(" ", "");
						proMachine = proMachine.replaceAll("（", "(");
						proMachine = proMachine.replaceAll("）", ")");
					}
					// 查询工序库，判断工序是否存在
					String hq_ProcessGzstore = "from ProcessGzstore where processName=?";
					ProcessGzstore processGzstore = (ProcessGzstore) totalDao
							.getObjectByCondition(hq_ProcessGzstore,
									processName);
					if (processGzstore != null) {
						for (ProcardTemplatesb pt2 : sameList) {
							// Set<ProcessTemplate> processSet2 =
							// pt2
							// .getProcessTemplate();
							String hql_process = "from ProcessTemplatesb pc where pc.procardTemplatesb.id=? and (pc.dataStatus is null or pc.dataStatus !='删除')  order by pc.processNO";
							Integer processCount = totalDao.getCount(
									hql_process, pt2.getId());
							if (processCount == null || processCount == 0) {
								ProcessTemplatesb processTemplate = new ProcessTemplatesb();
								BeanUtils.copyProperties(processGzstore,
										processTemplate, new String[] { "id" });
								if (processGzstore.getProductStyle() != null
										&& processGzstore.getProductStyle()
												.equals("外委")) {
									processTemplate.setProcessStatus("no");
								}
								processTemplate.setAddTime(addTime);
								processTemplate.setAddUser(Util.getLoginUser()
										.getName());
								processTemplate.setProcessNO(Integer
										.parseInt(processNO));
								if (processTemplate.getOpcaozuojiepai() == null) {
									processTemplate.setOpcaozuojiepai(3f);
								}
								if (processTemplate.getOpshebeijiepai() == null) {
									processTemplate.setOpshebeijiepai(1f);
								}
								if (processTemplate.getGzzhunbeijiepai() == null) {
									processTemplate.setGzzhunbeijiepai(1f);
								}
								if (processTemplate.getGzzhunbeicishu() == null) {
									processTemplate.setGzzhunbeicishu(1f);
								}
								processTemplate.setAllJiepai(processTemplate
										.getOpcaozuojiepai()
										+ processTemplate.getOpshebeijiepai()
										+ processTemplate.getGzzhunbeijiepai()
										* processTemplate.getGzzhunbeicishu());
								// processTemplate
								// .setProcessExplain(processExplain);
								// 点数
								if (timeQty != null) {
									processTemplate.setProcesdianshu(timeQty
											.doubleValue());
								}
								// 工装编号
								if (toolNo != null) {
									String hql = "from Gzstore where matetag=?";
									Gzstore gzstore = (Gzstore) totalDao
											.getObjectByQuery(hql, toolNo);
									if(gzstore==null){
										gzstore=new Gzstore();
										gzstore.setNumber(toolNo);
										totalDao.save(gzstore);
									}
									if (gzstore != null) {
										processTemplate.setGzstoreId(gzstore
												.getId());
										processTemplate.setNumber(gzstore
												.getNumber());
										processTemplate.setMatetag(gzstore
												.getMatetag());
									}
								}
								// 设备

								processTemplate.setProcardTemplatesb(pt2);
								processTemplatelist.add(processTemplate);
							}
						}
					} else {
						return new Object[] {
								"0",
								pt.getMarkId() + "的比对工序[" + processName
										+ "],在PEBS系统中的工序库中未查询到!<br/>" };
					}
				}
				if (processTemplatelist != null
						&& processTemplatelist.size() > 0 && oneerror == 0) {
					List<Integer> processNoList = new ArrayList<Integer>();
					String banciSql = "";
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						banciSql = " and (banci is null or banci =0)";
					} else {
						banciSql = " and banci=" + pt.getBanci();
					}
					for (ProcessTemplatesb processTemplate : processTemplatelist) {
						totalDao.save(processTemplate);
						if (!processNoList.contains(processTemplate
								.getProcessNO())) {
//							ProcardTemplateChangeLog
//									.addchangeLog(totalDao, processTemplate
//											.getProcardTemplate(),
//											processTemplate, "工序", "增加", Util
//													.getLoginUser(), Util
//													.getDateTime());
							processNoList.add(processTemplate.getProcessNO());
						} else {
							if (pt.getProductStyle() == null
									|| !pt.getProductStyle().equals("试制")) {
								continue;
							}
						}
						Float hasgxCunt = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcessTemplatesb where procardTemplatesb.markId=? and (dataStatus is null and dataStatus !='删除') and (procardTemplatesb.banbenStatus is null or procardTemplatesb.banbenStatus !='历史') and (procardTemplatesb.dataStatus is null or procardTemplatesb.dataStatus!='删除')",
										pt.getMarkId());
						List<ProcessTemplateFilesb> files = null;
						if (hasgxCunt == null || hasgxCunt == 0) {// 无工序
							// 添加图纸
							if (pt.getProductStyle() != null
									&& pt.getProductStyle().equals("试制")) {
								files = totalDao
										.query(
												"from ProcessTemplateFilesbsb where sbApplyId=? and markId=? and processNO is null and type in('3D文件','工艺规范') and glId="
														+ pt.getId() + banciSql,
														pt.getSbApplyId(),pt.getMarkId());
							} else {
								files = totalDao
										.query(
												"from ProcessTemplateFilesb where sbApplyId=? and markId=? and processNO is null and type in('3D文件','工艺规范') and glId is null "
														+ banciSql, pt.getSbApplyId(),pt
														.getMarkId());
							}
						} else {
							// 添加图纸
							if (pt.getProductStyle() != null
									&& pt.getProductStyle().equals("试制")) {
								files = totalDao
										.query(
												"from ProcessTemplateFilesb where sbApplyId=? and  markId=? and processNO is null "
														+ banciSql
														+ " and glId=?  and oldfileName in"
														+ "(select oldfileName from ProcessTemplateFile where markId=? "
														+ banciSql
														+ " and processNO !=? and processName !=? group by oldfileName)",
												pt.getMarkId(), pt.getId(),pt.getSbApplyId(), pt
														.getMarkId(),
												processTemplate.getProcessNO(),
												processTemplate
														.getProcessName());
							} else {
								files = totalDao
										.query(
												"from ProcessTemplateFilesb where sbApplyId=? and  markId=? and processNO is null "
														+ banciSql
														+ " and glId is null and oldfileName in"
														+ "(select oldfileName from ProcessTemplateFile where markId=? "
														+ banciSql
														+ " and processNO !=? and processName !=? group by oldfileName)",
														pt.getSbApplyId(),pt.getMarkId(), pt.getMarkId(),
												processTemplate.getProcessNO(),
												processTemplate
														.getProcessName());
							}

						}
						if (files != null && files.size() > 0) {
							List<String> oldFilenames = new ArrayList<String>();
							for (ProcessTemplateFilesb file : files) {
								if (!oldFilenames.contains(file
										.getOldfileName())) {
									Float hasCount = null;
									if (pt.getProductStyle() != null
											&& pt.getProductStyle()
													.equals("试制")) {
										hasCount = 0f;
									} else {
										hasCount = (Float) totalDao
												.getObjectByCondition(
														"select count(*) from ProcessTemplateFilesb where sbApplyId=? and  markId=? and glId is null and processNO=? and processName =? "
																+ banciSql
																+ " and oldfileName=?",
																pt.getSbApplyId(),pt.getMarkId(),
														processTemplate
																.getProcessNO(),
														processTemplate
																.getProcessName(),
														file.getOldfileName());
									}

									if (hasCount == null || hasCount == 0) {
										ProcessTemplateFile newFile = new ProcessTemplateFile();
										BeanUtils.copyProperties(file, newFile,
												new String[] { "id",
														"processNO",
														"processName" });
										newFile.setProcessNO(processTemplate
												.getProcessNO());
										newFile.setProcessName(processTemplate
												.getProcessName());
										if (pt.getProductStyle() != null
												&& pt.getProductStyle().equals(
														"试制")) {
											newFile.setGlId(processTemplate
													.getId());
											newFile.setProductStyle("试制");
										}
										totalDao.save(newFile);
									}
									oldFilenames.add(file.getOldfileName());
								}
							}
						}

					}
					sucesscount++;
				} else {
					return new Object[] { "0", pt.getMarkId() + "未比对到工序<br/>" };
				}
			} else {
				return new Object[] { "0", pt.getMarkId() + "未比对到工序<br/>" };
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[] { "0", "连接数据源异常!<br/>" };
		}
		return new Object[] { "1", "导入成功" };

	}
	@Override
	public void mibufatherAndsonrealction() {
		// TODO Auto-generated method stub
		List<ProcardTemplate> sonPtlist =totalDao.query("from ProcardTemplate where fatherId is not null and fatherId!=0 and procardTemplate.id is null and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') ");
		if(sonPtlist!=null&&sonPtlist.size()>0){
			for(ProcardTemplate pt:sonPtlist){
				ProcardTemplate father = (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class, pt.getFatherId());
				if(father!=null){
					Set<ProcardTemplate> sonSet = father.getProcardTSet();
					if(sonSet==null){
						sonSet = new HashSet<ProcardTemplate>();
					}
					sonSet.add(pt);
					pt.setProcardTemplate(father);
					father.setProcardTSet(sonSet);
					totalDao.update(pt);
					totalDao.update(father);
					
				}
			}
		}
		
		
		
	}
	@Override
	public void mibufatherAndsonrealction2() {
		// TODO Auto-generated method stub
		List<ProcardTemplatesb> sonPtlist =totalDao.query("from ProcardTemplatesb where fatherId is not null and fatherId!=0 and procardTemplatesb.id is null and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除') ");
		if(sonPtlist!=null&&sonPtlist.size()>0){
			for(ProcardTemplatesb pt:sonPtlist){
				ProcardTemplatesb father = (ProcardTemplatesb) totalDao.getObjectById(ProcardTemplatesb.class, pt.getFatherId());
				if(father!=null){
					Set<ProcardTemplatesb> sonSet = father.getProcardsbTSet();
					if(sonSet==null){
						sonSet = new HashSet<ProcardTemplatesb>();
					}
					sonSet.add(pt);
					pt.setProcardTemplatesb(father);
					father.setProcardsbTSet(sonSet);
					totalDao.update(pt);
					totalDao.update(father);
					
				}
			}
		}
		
		
		
	}
	
	@Override
	public String updateUnuploadTzname(Integer id, String path) {
		// TODO Auto-generated method stub
		ProcardTemplatesb pt = (ProcardTemplatesb) totalDao.getObjectByCondition("from ProcardTemplatesb where sbApplyId=? and id=rootId", id);
		List<ProcardTemplatesb> ptList = totalDao.query(
				"from ProcardTemplatesb where rootId=?", pt.getId());
		if (pt != null || ptList.size() == 0) {
			String month = Util.getDateTime("yyyy-MM");
			Users user = Util.getLoginUser();
			if (pt.getYwMarkId() != null || pt.getMarkId() != null) {
				// 件号查找
				File file = new File(path + "\\" + pt.getYwMarkId());
				if (!file.exists()) {
					file = new File(path + "\\" + pt.getMarkId());
				}
				if (!file.exists()) {
					return "对不起没有找到对应的文件";
				}
				File[] sonList1 = file.listFiles();
				if (sonList1 != null && sonList1.length > 0) {
					// 将文件放入特定位置
					StringBuffer drmes = new StringBuffer();
					for (File son1 : sonList1) {
						if (son1.isDirectory() && son1.getName() != null) {
							if (son1.getName().equals("3D文件")) {
								int sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功3D文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("SOP")) {
								int sdnum = 0;// 上传成功SOP文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功SOP文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("SAP")) {
								int sdnum = 0;// 上传成功SAP文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功SAP文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("3D模型")) {
								int sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& !markId
															.equals("DKBA3359")
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											sdnum++;
										}
									}
								}
								drmes.append("修改成功3D模型:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("视频文件")
									|| son1.getName().equals("视频")) {
								int spnum = 0;// 上传成功视频文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											spnum++;
										}
									}
								}
								drmes.append("修改成功视频文件:" + spnum + "份<br/>");
							}
							if (son1.getName().equals("工艺规范")) {
								int gynum = 0;// 上传成功工艺规范文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.isDirectory()
												&& son2.getName() != null) {
											if (son2.getName().contains("工艺卡")
													|| son2.getName().contains(
															"原图")
													|| son2.getName().contains(
															"展开图")) {
												File[] sonList3 = son2
														.listFiles();
												if (sonList3 != null
														&& sonList3.length > 0) {
													for (File son3 : sonList3) {
														if (!son3.isDirectory()
																&& son3
																		.getName() != null
																&& !"Thumbs.db"
																		.equals(son3
																				.getName())) {
															String fileName = son3
																	.getName();
															String markId = fileName
																	.substring(
																			0,
																			fileName
																					.lastIndexOf("."));
															String type = fileName
																	.substring(
																			fileName
																					.lastIndexOf("."),
																			fileName
																					.length());
															if ((markId
																	.startsWith("DKBA") || markId
																	.startsWith("dkba"))
																	&& (markId
																			.indexOf(".") > 6 || markId
																			.indexOf(".") < 0)) {
																markId = markId
																		.substring(
																				0,
																				5)
																		+ "."
																		+ markId
																				.substring(
																						5,
																						8)
																		+ "."
																		+ markId
																				.substring(
																						8,
																						markId
																								.length());
															}
															markId = markId
																	.replaceAll(
																			"_",
																			"-");
															String newFilename = son2
																	.getAbsolutePath()
																	+ "\\"
																	+ markId
																	+ type;
															son3
																	.renameTo(new File(
																			newFilename));
															gynum++;
														}
													}
												}
											}
										}

									}
								}
								drmes.append("修改成功工艺规范文件:" + gynum + "份<br/>");
							}
							if (son1.getName().equals("成型图")) {
								int sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											sdnum++;
										}
									}
								}
								drmes.append("导入成功成型图:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
						}
					}
					return drmes.toString();
				}
			}
		}
		return "对不起没有找到目标BOM";
	}
	@Override
	public void findDaoChuBom(Integer id) {
		// TODO Auto-generated method stub
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			ProcardTemplatesb totalCard = (ProcardTemplatesb) totalDao
					.getObjectById(ProcardTemplatesb.class, id);
			OutputStream os = response.getOutputStream();
			response.reset();
			String name = totalCard.getYwMarkId();
			if (name == null || name.length() == 0) {
				name = totalCard.getMarkId();
			}
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(name.getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws1 = wwb.createSheet("sheet1", 0);
			WritableSheet ws = wwb.createSheet("生产物料BOM表", 1);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.LEFT);
			// ws.addCell(new jxl.write.Label(0, 0, "物料代码", wc));
			// ws.addCell(new jxl.write.Label(1, 0, "物料名称", wc));
			// ws.addCell(new jxl.write.Label(2, 0, "规格型号", wc));
			// ws.addCell(new jxl.write.Label(3, 0, "物料属性", wc));
			ws.addCell(new jxl.write.Label(0, 3, "版本:" + totalCard.getBanci(),
					wc));
			ws.addCell(new jxl.write.Label(4, 3, "产品编码:"
					+ totalCard.getYwMarkId(), wc));
			ws.addCell(new jxl.write.Label(22, 3, "客户名称：华为", wc));
			ws.mergeCells(4, 2, 7, 2);
			ws.mergeCells(0, 2, 3, 2);
			// ws.addCell(new jxl.write.Label(7, 0, "建立日期", wc));
			// ws.addCell(new jxl.write.Label(8, 0, "使用人员", wc));
			// ws.addCell(new jxl.write.Label(0, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(1, 1, totalCard.getProName(),
			// wc));
			// ws.addCell(new jxl.write.Label(2, 1,
			// totalCard.getSpecification(),
			// wc));
			// ws.addCell(new jxl.write.Label(3, 1, totalCard.getProcardStyle(),
			// wc));
			// ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(), wc));
			// ws.addCell(new jxl.write.Label(5, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(6, 1,
			// totalCard.getCreateUserName(),
			// wc));
			// ws
			// .addCell(new jxl.write.Label(7, 1, totalCard
			// .getCreateDate(), wc));
			// ws.addCell(new jxl.write.Label(8, 1,
			// totalCard.getCreateUserName(),
			// wc));
			ws.addCell(new jxl.write.Label(0, 4, "序号", wc));
			ws.addCell(new jxl.write.Label(1, 4, "物料编号", wc));
			ws.addCell(new jxl.write.Label(2, 4, "模型图号", wc));
			ws.addCell(new jxl.write.Label(3, 4, "图号", wc));
			ws.addCell(new jxl.write.Label(4, 4, "版本", wc));
			ws.addCell(new jxl.write.Label(5, 4, "名称", wc));
			ws.mergeCells(6, 1, 15, 1);// 合并单元格
			WritableCellFormat wc2 = new WritableCellFormat(wf);
			wc2.setAlignment(Alignment.CENTRE);
			ws.addCell(new jxl.write.Label(6, 4, "阶层/单台数量6-15", wc2));
			ws.addCell(new jxl.write.Label(16, 4, "来源", wc));
			ws.addCell(new jxl.write.Label(17, 4, "实际用量", wc));
			ws.addCell(new jxl.write.Label(18, 4, "单位", wc));
			ws.addCell(new jxl.write.Label(19, 4, "表处", wc));
			ws.addCell(new jxl.write.Label(20, 4, "材质", wc));
			ws.addCell(new jxl.write.Label(21, 4, "长", wc));
			ws.addCell(new jxl.write.Label(22, 4, "宽", wc));
			ws.addCell(new jxl.write.Label(23, 4, "高", wc));
			ws.addCell(new jxl.write.Label(24, 4, "备注", wc));
			ws.addCell(new jxl.write.Label(25, 4, "材料类别", wc));
			ws.addCell(new jxl.write.Label(26, 4, "规格(导入截止列)", wc));
			ws.addCell(new jxl.write.Label(27, 4, "零件类型", wc));
			ws.addCell(new jxl.write.Label(28, 4, "是否有工序", wc));
			ws.addCell(new jxl.write.Label(29, 4, "编制状态", wc));
			ws.addCell(new jxl.write.Label(30, 4, "展开图", wc));
			daoChuHWBom2(totalCard, 5, ws, wc,totalCard.getSbApplyId());
			// for (int i = 0; i < list.size(); i++) {
			// Oabusiness o = (Oabusiness) list.get(i);
			// ws.addCell(new jxl.write.Number(0, i + 4, i + 1, wc));
			// ws.addCell(new Label(1, i + 4, o.getOaproductnumber(), wc));
			// ws.addCell(new Label(2, i + 4, o.getOaproductName(), wc));
			// ws.addCell(new Label(3, i + 4, o.getOaspecification(), wc));
			// ws.addCell(new Label(4, i + 4, o.getOaunit(), wc));
			// ws.addCell(new Label(5, i + 4, String.format("%.2f", o
			// .getOaquantity()), wc));
			// // 单价
			// ws.addCell(new Label(6, i + 4, String.format("%.2f", o
			// .getOaunitprice()), wc));
			// ws.addCell(new Label(7, i + 4, String.format("%.2f", o
			// .getOatotalMon()), wc));
			// ws.addCell(new Label(8, i + 4, o.getOastatus(), wc));
			// ws.addCell(new Label(9, i + 4, o.getOausername(), wc));
			// ws.addCell(new Label(10, i + 4, o.getOafactory(), wc));
			// // 支出
			// ws
			// .addCell(new Label(11, i + 4, o
			// .getOainvoicenumber(), wc));
			// ws.addCell(new Label(12, i + 4, o.getOadate(), wc));
			// // 结余
			// ws.addCell(new Label(13, i + 4, o.getOahetongnumber(), wc));
			// }

			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}
	private int daoChuHWBom2(ProcardTemplatesb pt, int i,
			WritableSheet ws, WritableCellFormat wc,Integer sbApplyId) {
		// TODO Auto-generated method stub
		try {
			ws.addCell(new jxl.write.Label(0, i, (i - 1) + "", wc));
			ws.addCell(new jxl.write.Label(1, i, "", wc));
			ws.addCell(new jxl.write.Label(2, i, pt.getTuhao(), wc));
			ws.addCell(new jxl.write.Label(3, i, pt.getMarkId(), wc));
			// ws.addCell(new jxl.write.Label(3, i, pt.getTuhao(), wc));
			ws.addCell(new jxl.write.Label(4, i, pt.getBanBenNumber(), wc));
			ws.addCell(new jxl.write.Label(5, i, pt.getProName(), wc));
			if (pt.getProcardStyle().equals("外购")) {
				if (pt.getNeedProcess() != null
						&& pt.getNeedProcess().equals("yes")) {
					ws.addCell(new jxl.write.Label(16, i, "半成品", wc));
				} else {
					ws.addCell(new jxl.write.Label(16, i, "外购", wc));
				}
				try {
					ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
							pt.getQuanzi2() / pt.getQuanzi1() + "", wc));
					ws.addCell(new jxl.write.Label(17, i, pt.getQuanzi2()
							/ pt.getQuanzi1() + "", wc));
				} catch (Exception e) {
					// TODO: handle exception
					ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
							"待填", wc));
					ws.addCell(new jxl.write.Label(17, i, "待填", wc));
				}

				if (pt.getThisLength() != null) {
					ws.addCell(new jxl.write.Label(21, i, pt.getThisLength()
							+ "", wc));
				}
				if (pt.getThisWidth() != null) {
					ws.addCell(new jxl.write.Label(22, i, pt.getThisWidth()
							+ "", wc));
				}
				if (pt.getThisHight() != null) {
					ws.addCell(new jxl.write.Label(23, i, pt.getThisHight()
							+ "", wc));
				}
				ws
						.addCell(new jxl.write.Label(24, i, pt
								.getSpecification(), wc));
			} else if (pt.getProcardStyle().equals("待定")) {
				ws.addCell(new jxl.write.Label(16, i, "待定", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i, pt
						.getCorrCount()
						+ "", wc));
				ws.addCell(new jxl.write.Label(17, i, pt.getCorrCount() + "",
						wc));
				ws.addCell(new jxl.write.Label(24, i, pt.getLoadMarkId(), wc));
			} else if (pt.getProcardStyle().equals("总成")) {
				ws.addCell(new jxl.write.Label(16, i, "自制", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
						1 + "", wc));
			} else {
				ws.addCell(new jxl.write.Label(16, i, "自制", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i, pt
						.getCorrCount()
						+ "", wc));
				ws.addCell(new jxl.write.Label(17, i, pt.getCorrCount() + "",
						wc));
				ws.addCell(new jxl.write.Label(24, i, pt.getLoadMarkId(), wc));
			}
			ws.addCell(new jxl.write.Label(18, i, pt.getUnit(), wc));
			ws.addCell(new jxl.write.Label(19, i, pt.getBiaochu(), wc));
			ws.addCell(new jxl.write.Label(20, i, pt.getClType(), wc));
			if (pt.getThisLength() != null) {
				ws.addCell(new jxl.write.Label(21, i, pt.getThisLength() + "",
						wc));
			}
			if (pt.getThisWidth() != null) {
				ws.addCell(new jxl.write.Label(22, i, pt.getThisWidth() + "",
						wc));
			}
			if (pt.getThisHight() != null) {
				ws.addCell(new jxl.write.Label(23, i, pt.getThisHight() + "",
						wc));
			}
			ws.addCell(new jxl.write.Label(25, i, pt.getWgType(), wc));
			ws.addCell(new jxl.write.Label(26, i, pt.getSpecification(), wc));
			ws.addCell(new jxl.write.Label(27, i, pt.getProcardStyle(), wc));
			if (pt.getProcessTemplatesb() == null
					|| pt.getProcessTemplatesb().size() == 0) {
				ws.addCell(new Label(28, i, "否", wc));// 没有工序
			} else {
				ws.addCell(new Label(28, i, "是", wc));// 有工序
			}
			ws.addCell(new Label(29, i, pt.getBzStatus(), wc));// 有工序
			// 图纸
			String addSql2 = null;
			if (pt.getProductStyle() == null) {
				pt.setProductStyle("批产");
			}
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and glId='" + pt.getId()
						+ "' and processNO is null  ";
			}
			if (pt.getBanci() == null) {
				addSql2 += " and (banci is null or banci=0)";
			} else {
				addSql2 += " and banci=" + pt.getBanci();
			}
			Float zktfileCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplateFilesb where sbApplyId=? and markId=? and oldfileName like '%展开图%'"
									+ addSql2,sbApplyId, pt.getMarkId());
			if (zktfileCount != null && zktfileCount > 0) {
				ws.addCell(new Label(30, i, "是", wc));// 已有展开图
			} else {
				ws.addCell(new Label(30, i, "否", wc));// 已有展开图
			}
			i++;
			// if (!pt.getProcardStyle().equals("外购") && pt.getTrademark() !=
			// null
			// && pt.getTrademark().length() > 0) {
			// ws.addCell(new jxl.write.Label(0, i, (i - 2) + "", wc));
			// ws.addCell(new jxl.write.Label(1, i, "", wc));
			// ws.addCell(new jxl.write.Label(3, i, pt.getTrademark(), wc));
			// // ws.addCell(new jxl.write.Label(3, i, pt.getYtuhao(), wc));
			// ws.addCell(new jxl.write.Label(4, i, pt.getBanBenNumber(), wc));
			// ws.addCell(new jxl.write.Label(5, i, pt.getYuanName(), wc));
			// if (pt.getQuanzi2() != null && pt.getQuanzi1() != null
			// && pt.getQuanzi1() != 0) {
			// ws.addCell(new jxl.write.Label(6 + pt.getBelongLayer(), i,
			// pt.getQuanzi2() / pt.getQuanzi1() + "", wc));
			// }
			// ws.addCell(new jxl.write.Label(16, i, "原材料", wc));
			// if (pt.getQuanzi2() != null && pt.getQuanzi1() != null
			// && pt.getQuanzi1() != 0) {
			// ws.addCell(new jxl.write.Label(17, i, pt.getQuanzi2()
			// / pt.getQuanzi1() + "", wc));
			// }
			// ws.addCell(new jxl.write.Label(18, i, pt.getYuanUnit(), wc));
			// ws.addCell(new jxl.write.Label(19, i, pt.getBiaochu(), wc));
			// ws.addCell(new jxl.write.Label(20, i, pt.getClType(), wc));
			// ws
			// .addCell(new jxl.write.Label(24, i, pt
			// .getSpecification(), wc));
			// ws.addCell(new jxl.write.Label(25, i, pt.getWgType(), wc));
			// ws.addCell(new jxl.write.Label(26, i, "原材料", wc));
			// ws.addCell(new jxl.write.Label(27, i, "", wc));// 已有原图
			// ws.addCell(new jxl.write.Label(28, i, "", wc));// 已有展开图
			// ws.addCell(new jxl.write.Label(29, i, "", wc));// 已有工艺卡
			// i++;
			// }
			List<ProcardTemplatesb> sonSet = totalDao
					.query(
							" from ProcardTemplatesb where "
									+ " procardTemplatesb.id = ? and (dataStatus is null or dataStatus <> '删除')",
							pt.getId());
			if (sonSet != null && sonSet.size() > 0) {
				for (ProcardTemplatesb son : sonSet) {
					if (son.getBanbenStatus() == null
							|| son.getBanbenStatus().equals("默认")) {
						i = daoChuHWBom2(son, i, ws, wc,sbApplyId);
					}
				}
			}
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 材质
		return i;
	}
	@Override
	public String transferWork(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录!";
		}
		ProcardTemplateBanBenApply pabba = (ProcardTemplateBanBenApply) totalDao.getObjectById(ProcardTemplateBanBenApply.class, id);
		if(pabba==null){
			return "没有找到对应的设变单!";
		}
		ProcardTemplateBanBenJudges ptbbj= null;
		if(pabba.getProcessStatus().equals("项目主管初评")){
			ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='初评' and userId=? ",id, user.getId());
		}else if(pabba.getProcessStatus().equals("选择设变零件")){
			ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='工程师' and userId=? ",id, user.getId());
		}else if(pabba.getProcessStatus().equals("成本审核")){
			ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='成本' and userId=? ",id, user.getId());
		}else if(pabba.getProcessStatus().equals("各部门评审")){
			ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审' and userId=? ",id, user.getId());
//		}else if(pabba.getProcessStatus().equals("上传佐证")){
//			ptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='佐证' and userId=? ",id, user.getId());
		}
		if(ptbbj==null){
			return  "对不起目前宁无权转交此设变单工作!";
		}
		Users u = (Users) totalDao.getObjectById(Users.class, id2);
		if(u==null){
			return "对不起没有找到目标人员!";
		}
		ProcardTemplateBanBenJudges newptbbj = (ProcardTemplateBanBenJudges) totalDao.getObjectByCondition(" from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType=? and userId=? ",id,ptbbj.getJudgeType(), u.getId());
		if(newptbbj!=null){
			return "对不起，此人已被指派过,不能被再次指派!";
		}
		Set<ProcardTemplateBanBenJudges> ptbbjSet = pabba.getPtbbjset();
		newptbbj = new ProcardTemplateBanBenJudges();
		newptbbj.setDept(u.getDept());//部门
		newptbbj.setUserId(u.getId());//评审人员id
		newptbbj.setUserName(u.getName());//评审名称
		newptbbj.setUserCode(u.getCode());//用户
		newptbbj.setAddTime(Util.getDateTime());//添加时间
		newptbbj.setJudgeType(ptbbj.getJudgeType());//评审类别（初评,工程师,成本,内审,编制通知,编制完通知,佐证）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
		newptbbj.setPtbbApply(pabba);//版本升级主表applyId
		newptbbj.setJudgedId(ptbbj.getId());//指派员Id
		ptbbjSet.add(newptbbj);
		ptbbjSet.remove(ptbbj);
		totalDao.save(newptbbj);
		totalDao.delete(ptbbj);
		pabba.setPtbbjset(ptbbjSet);
		totalDao.update(pabba);
		AlertMessagesServerImpl.addAlertMessages("设变工作移交", user.getName()+"将总成为:"
				+ pabba.getMarkId() + ",业务件号为："
				+ pabba.getYwMarkId() + "的BOM设变工作移交给你，" + "请前往处理.设变单号:"+pabba.getSbNumber(),
				new Integer[]{u.getId()},
				"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
						+ pabba.getId(), true, "no");
		return "true";
	}
}