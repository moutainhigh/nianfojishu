package com.task.ServerImpl.zhuseroffer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import com.task.Dao.TotalDao;
import com.task.Server.zhuseroffer.ZhuserOfferServer;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Price;
import com.task.entity.Users;
import com.task.entity.payment.FundApply;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.CircuitRun;
import com.task.entity.zhuseroffer.NoPriceprocess;
import com.task.entity.zhuseroffer.Sample;
import com.task.entity.zhuseroffer.SumProcess;
import com.task.entity.zhuseroffer.ZhuserOffer;
import com.task.util.Upload;
import com.task.util.Util;

public class ZhuserOfferServerImpl implements ZhuserOfferServer{
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public Map<Integer, Object> findListByZhUser(ZhuserOffer zhuseroffer,int pageNo, int pageSize) {
		if (zhuseroffer == null) {
			zhuseroffer= new ZhuserOffer();
		}
		Users user = Util.getLoginUser();
		String hql = "usercode='"+user.getCode()+"'and  yuanclAndWaigj.id<> null order by id desc";
		hql = totalDao.criteriaQueries(zhuseroffer,hql,null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	public Map<Integer, Object> findyuancailByStatus(YuanclAndWaigj yuanclAndWaigouj,int pageNo, int pageSize) {
		if (yuanclAndWaigouj == null) {
			yuanclAndWaigouj= new YuanclAndWaigj();
		}
		String hql="";
		if(yuanclAndWaigouj.getPricestatus()!=null){
			hql = "pricestatus='"+yuanclAndWaigouj.getPricestatus()+"' order by id desc";
			hql = totalDao.criteriaQueries(yuanclAndWaigouj,hql,null);
		}else{
			hql=" from YuanclAndWaigj order by id desc";
		}
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	public Map<String, String> processTemplateFileList1(Integer id){
		YuanclAndWaigj yuanclAndWaigj =new YuanclAndWaigj();
		if (id != null&&!"".equals(id)) {
			yuanclAndWaigj = (YuanclAndWaigj)totalDao.get(YuanclAndWaigj.class, id);
		}
		Map<String, String> tzMap = new HashMap<String, String>();
		String hql ="from ProcessTemplateFile where markId ='"+yuanclAndWaigj.getMarkId()
			+"' and (status is null or status <> '历史')  and (sbStatus is null or sbStatus is not '删除') " ;
		List<ProcessTemplateFile> processTemplateFile_List = totalDao.query(hql);
		if (processTemplateFile_List != null && processTemplateFile_List.size() > 0) {
			for (ProcessTemplateFile file : processTemplateFile_List) {
				String wz = file.getMonth() + "/"
						+ file.getFileName();
				String name = file.getOldfileName();
				tzMap.put(wz, name);
			}
		}
		return tzMap;
	}
	public Map<String, String> findprocessTemplateFileList2(Integer id){
		ZhuserOffer zhuseroffer = new ZhuserOffer();
		if (id != null&&!"".equals(id)) {
			zhuseroffer = findZhOfferById(id);
		}
		Map<String, String> tzMap = new HashMap<String, String>();
		NoPriceprocess np = (NoPriceprocess)totalDao.get(NoPriceprocess.class, zhuseroffer.getProcessId());
		if(np!=null){
		String hql_pwd = "from ProcessInforWWApplyDetail where markId =? and processNOs = ? and processNames =? and procardId=? and (dataStatus is null or dataStatus not in('删除','取消'))";
		ProcessInforWWApplyDetail wwDetail = (ProcessInforWWApplyDetail)totalDao.getObjectByCondition(hql_pwd, np.getMarkId(),np.getProcessNO(),np.getProcessName(),np.getProcardId());
	if (wwDetail != null) {
		Procard procard = (Procard) totalDao.getObjectById(Procard.class,
				wwDetail.getProcardId());
		if (procard != null) {
			String[] processNos = wwDetail.getProcessNOs().split(";");
			StringBuffer sb = new StringBuffer();
			for (String pno : processNos) {
				if (sb.length() == 0) {
					sb.append(pno);
				} else {
					sb.append("," + pno);
				}
			}
			List<ProcessTemplateFile> list1 = null;
			String banbenSql = null;
			if(procard.getBanBenNumber()==null || procard.getBanBenNumber().length()==0){
				banbenSql = " and (banBenNumber is null or banBenNumber='') ";
			}else{
				banbenSql = " and banBenNumber ='"+procard.getBanBenNumber()+"' ";
			}
			String banciSql = null;
			if (procard.getBanci() == null || procard.getBanci() == 0) {
				banciSql = " and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + procard.getBanci();
			}
					if (procard.getProductStyle().equals("批产")) {
						// list1 = totalDao
						// .query(
						// "from ProcessTemplateFile where markId=? and processNO in("
						// + sb.toString()
						// + ") "
						// + banciSql
						// +
						// "and productStyle='批产' and (status is null or status!='历史')",
						// procard.getMarkId());
						List<Integer> maxIdList = totalDao
								.query(
										"select max(id) from ProcessTemplateFile where markId=? and type in('工艺规范','3D文件') and processNO is null "
												+ banciSql
												+ "and productStyle='批产' group by oldfileName",
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
							list1 = totalDao
									.query("from ProcessTemplateFile where id in ("
											+ maxidsb.toString() + ")");
						}
					} else {
						// list1 = totalDao
						// .query(
						// "from ProcessTemplateFile where id in(select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
						// + sb.toString()
						// + ") )"
						// + banbenSql + banciSql
						// + "and productStyle='试制' group by oldfileName)",
						// procard.getMarkId(), procard
						// .getProcardTemplateId());
						List<Integer> maxIdList = totalDao
								.query(
										"select max(id) from ProcessTemplateFile where markId=? and type in('工艺规范','3D文件') and processNO is null and glId=? "
												+ banciSql
												+ "and productStyle='试制' group by oldfileName",
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
							list1 = totalDao
									.query("from ProcessTemplateFile where id in ("
											+ maxidsb.toString() + ")");
						}
					}
			if (list1 != null && list1.size() > 0) {
				for (ProcessTemplateFile file : list1) {
					String wz = file.getMonth() + "/" + file.getFileName();
					String name = file.getOldfileName();
					if (file.getProcessName() != null) {
						file.setOldfileName("(" + file.getProcessName()
								+ ")" + file.getOldfileName());
					}
					tzMap.put(wz, name);
				}
			}
			if (wwDetail.getWwType().equals("包工包料")
					|| (wwDetail.getRelatDown() != null && wwDetail
							.getRelatDown().equals("是"))) {
				Set<Procard> sonptSet = procard.getProcardSet();
				if (sonptSet != null && sonptSet.size() > 0) {
					for (Procard son : sonptSet) {
						List<ProcessTemplateFile> list2 = findSonTz(son);
						if (list2 != null && list2.size() > 0) {
							for (ProcessTemplateFile file : list2) {
								String wz = file.getMonth() + "/"
										+ file.getFileName();
								String name = file.getOldfileName();
								if (file.getProcessName() != null) {
									file.setOldfileName("("
											+ file.getProcessName() + ")"
											+ file.getOldfileName());
								}
								tzMap.put(wz, name);
							}
						}
					}
				}
			}
		}
	}
	}
		return tzMap;
	}
	public Map<String, String> findbjtzList(Integer id){
		ZhuserOffer zhuseroffer = new ZhuserOffer();
		if (id != null&&!"".equals(id)) {
			zhuseroffer = findZhOfferById(id);
		}
		Map<String, String> tzMap = new HashMap<String, String>();
		if(zhuseroffer.getYuanclAndWaigj()!=null){
			YuanclAndWaigj yuanclAndWaigj = zhuseroffer.getYuanclAndWaigj();
			String hql ="from ProcessTemplateFile where markId ='"+yuanclAndWaigj.getMarkId()
			+"' and (status is null or status <> '历史')  and (sbStatus is null or sbStatus is not '删除') " ;
		List<ProcessTemplateFile> processTemplateFile_List = totalDao.query(hql);
		if (processTemplateFile_List != null && processTemplateFile_List.size() > 0) {
			for (ProcessTemplateFile file : processTemplateFile_List) {
				String wz = file.getMonth() + "/"
						+ file.getFileName();
				String name = file.getOldfileName();
				tzMap.put(wz, name);
			}
		}
		}else if(zhuseroffer.getProcessId()!=null&&!"".equals(zhuseroffer.getProcessId())){
				NoPriceprocess np = (NoPriceprocess)totalDao.get(NoPriceprocess.class, zhuseroffer.getProcessId());
				if(np!=null){
				if (np.getProcardId() != null) {
					String hql_pwd = "from ProcessInforWWApplyDetail where markId =? and processNOs = ? and processNames =? and procardId=? and (dataStatus is null or dataStatus not in('删除','取消'))";
					ProcessInforWWApplyDetail wwDetail = (ProcessInforWWApplyDetail) totalDao
							.getObjectByCondition(hql_pwd, np.getMarkId(), np
									.getProcessNO(), np.getProcessName(), np
									.getProcardId());
					
					if (wwDetail != null) {
						Procard procard = (Procard) totalDao.getObjectById(
								Procard.class, wwDetail.getProcardId());
						if (procard != null) {
							String banbenSql = null;
							if (procard.getBanBenNumber() == null
									|| procard.getBanBenNumber().length() == 0) {
								banbenSql = " and (banBenNumber is null or banBenNumber='') ";
							} else {
								banbenSql = " and banBenNumber ='"
										+ procard.getBanBenNumber() + "' ";
							}
							String[] processNos = wwDetail.getProcessNOs()
									.split(";");
							StringBuffer sb = new StringBuffer();
							for (String pno : processNos) {
								if (sb.length() == 0) {
									sb.append(pno);
								} else {
									sb.append("," + pno);
								}
							}
							List<ProcessTemplateFile> list1 = null;
							String banciSql = null;
							if (procard.getBanci() == null
									|| procard.getBanci() == 0) {
								banciSql = " and (banci is null or banci=0)";
							} else {
								banciSql = " and banci=" + procard.getBanci();
							}
							if (procard.getProductStyle().equals("批产")) {
								// list1 = totalDao
								// .query(
								// "from ProcessTemplateFile where markId=? and processNO in("
								// + sb.toString()
								// + ") "
								// + banciSql
								// +
								// "and productStyle='批产' and (status is null or status!='历史')",
								// procard.getMarkId());
								// 先使用零件图纸
								List<Integer> maxIdList = totalDao
										.query(
												"select max(id) from ProcessTemplateFile where markId=? and processNO in("
														+ sb.toString()
														+ ") "
														+ banciSql
														+ "and productStyle='批产' and type in('工艺规范','3D文件')  group by oldfileName",
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
									list1 = totalDao
											.query("from ProcessTemplateFile where id in ("
													+ maxidsb.toString() + ")");
								}
							} else {
								// list1 = totalDao
								// .query(
								// "from ProcessTemplateFile where id in(select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
								// + sb.toString()
								// + ") )"
								// + banciSql
								// +
								// "and productStyle='试制' group by oldfileName)",
								// procard.getMarkId(), procard
								// .getProcardTemplateId());
								List<Integer> maxIdList = totalDao
										.query(
												"select max(id) from ProcessTemplateFile where markId=? and processNO is null and glId =?"
														+ banciSql
														+ "and productStyle='试制' and type in('工艺规范','3D文件') group by oldfileName",
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
									list1 = totalDao
											.query("from ProcessTemplateFile where id in ("
													+ maxidsb.toString() + ")");
								}
							}
							if (list1 != null && list1.size() > 0) {
								for (ProcessTemplateFile file : list1) {
									String wz = file.getMonth() + "/"
											+ file.getFileName();
									String name = file.getOldfileName();
									if (file.getProcessName() != null
											&& !file.getOldfileName().contains(
													file.getProcessName())) {
										file.setOldfileName("("
												+ file.getProcessName() + ")"
												+ file.getOldfileName());
									}
									tzMap.put(wz, name);
								}
							}

							if (wwDetail.getWwType().equals("包工包料")
									|| (wwDetail.getRelatDown() != null && wwDetail
											.getRelatDown().equals("是"))) {
								Set<Procard> sonptSet = procard.getProcardSet();
								if (sonptSet != null && sonptSet.size() > 0) {
									for (Procard son : sonptSet) {
										List<ProcessTemplateFile> list2 = findSonTz(son);
										if (list2 != null && list2.size() > 0) {
											for (ProcessTemplateFile file : list2) {
												String wz = file.getMonth()
														+ "/"
														+ file.getFileName();
												if (file.getProcessName() != null
														&& !file
																.getOldfileName()
																.contains(
																		file
																				.getProcessName())) {
													file
															.setOldfileName("("
																	+ file
																			.getProcessName()
																	+ ")"
																	+ file
																			.getOldfileName());
												}
												String name = file
														.getOldfileName();
												tzMap.put(wz, name);
											}
										}
									}
								}
							}
						}

					}
				}else if(np.getProcessId()!=null){
					ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class, np.getProcessId());
					if(pt!=null){
						String banciSql = null;
						if (pt.getBanci() == null
								|| pt.getBanci() == 0) {
							banciSql = " and (banci is null or banci=0)";
						} else {
							banciSql = " and banci=" + pt.getBanci();
						}
						String[] processNos = np.getProcessNO() .split(";");
						StringBuffer sb = new StringBuffer();
						for (String pno : processNos) {
							if (sb.length() == 0) {
								sb.append(pno);
							} else {
								sb.append("," + pno);
							}
						}
						List<ProcessTemplateFile> list1 = null;
						if (pt.getProductStyle().equals("批产")) {
							// list1 = totalDao
							// .query(
							// "from ProcessTemplateFile where markId=? and processNO in("
							// + sb.toString()
							// + ") "
							// + banciSql
							// +
							// "and productStyle='批产' and (status is null or status!='历史')",
							// procard.getMarkId());
							// 先使用零件图纸
							List<Integer> maxIdList = totalDao
									.query(
											"select max(id) from ProcessTemplateFile where markId=?  "
													+ banciSql
													+ "and productStyle='批产' and type in('工艺规范','3D文件')  group by oldfileName",
											pt.getMarkId());
							if (maxIdList != null && maxIdList.size() > 0) {
								StringBuffer maxidsb = new StringBuffer();
								for (Integer maxId : maxIdList) {
									if (maxidsb.length() == 0) {
										maxidsb.append(maxId);
									} else {
										maxidsb.append("," + maxId);
									}
								}
								list1 = totalDao
										.query("from ProcessTemplateFile where id in ("
												+ maxidsb.toString() + ")");
							}
						} else {
							// list1 = totalDao
							// .query(
							// "from ProcessTemplateFile where id in(select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
							// + sb.toString()
							// + ") )"
							// + banciSql
							// +
							// "and productStyle='试制' group by oldfileName)",
							// procard.getMarkId(), procard
							// .getProcardTemplateId());
							List<Integer> maxIdList = totalDao
									.query(
											"select max(id) from ProcessTemplateFile where markId=? and processNO is null and glId =?"
													+ banciSql
													+ "and productStyle='试制' and type in('工艺规范','3D文件') group by oldfileName",
											pt.getMarkId(), pt.getId());
							if (maxIdList != null && maxIdList.size() > 0) {
								StringBuffer maxidsb = new StringBuffer();
								for (Integer maxId : maxIdList) {
									if (maxidsb.length() == 0) {
										maxidsb.append(maxId);
									} else {
										maxidsb.append("," + maxId);
									}
								}
								list1 = totalDao
										.query("from ProcessTemplateFile where id in ("
												+ maxidsb.toString() + ")");
							}
						}
						if (list1 != null && list1.size() > 0) {
							for (ProcessTemplateFile file : list1) {
								String wz = file.getMonth() + "/"
										+ file.getFileName();
								String name = file.getOldfileName();
								if (file.getProcessName() != null
										&& !file.getOldfileName().contains(
												file.getProcessName())) {
									file.setOldfileName("("
											+ file.getProcessName() + ")"
											+ file.getOldfileName());
								}
								tzMap.put(wz, name);
							}
						}
					}
				}
		}
		}
		return tzMap;
	}
	
	public SumProcess findSumProcessByid(Integer id){
		if (id != null&&!"".equals(id)){
		   return (SumProcess) totalDao.get(SumProcess.class, id);
		}else{
			return null;
		}
	}
	public Map<String, String> findpicforSumProcess(Integer id){
		Map<String, String> tzMap = new HashMap<String, String>();
		if (id != null&&!"".equals(id)){
			SumProcess sp = (SumProcess)totalDao.get(SumProcess.class, id);
			List<ZhuserOffer> zhuserOfferList = totalDao.query("from ZhuserOffer where sumProcessId = ? ",sp.getId());
			for(ZhuserOffer zhuseroffer : zhuserOfferList){
				if (zhuseroffer.getYuanclAndWaigj() != null) {
					YuanclAndWaigj yuanclAndWaigj = zhuseroffer
							.getYuanclAndWaigj();
					String banbenSql = null;
					if (yuanclAndWaigj.getBanbenhao() == null
							|| yuanclAndWaigj.getBanbenhao().length() == 0) {
						banbenSql = " and (banBenNumber is null or banBenNumber='') ";
					} else {
						banbenSql = " and banBenNumber ='"
								+ yuanclAndWaigj.getBanbenhao() + "' ";
					}
					List<ProcessTemplateFile> processTemplateFile_List = null;
					String hql = "from ProcessTemplateFile where id in()";
					List<Integer> maxIdList = totalDao
							.query("select max(id) from ProcessTemplateFile where markId ='"
									+ yuanclAndWaigj.getMarkId()
									+ "' and (status is null or status <> '历史')  and (sbStatus is null or sbStatus is not '删除') "
									+ banbenSql + " group by oldfileName");
					if (maxIdList != null && maxIdList.size() > 0) {
						StringBuffer maxidsb = new StringBuffer();
						for (Integer maxId : maxIdList) {
							if (maxidsb.length() == 0) {
								maxidsb.append(maxId);
							} else {
								maxidsb.append("," + maxId);
							}
						}
						processTemplateFile_List = totalDao
								.query("from ProcessTemplateFile where id in ("
										+ maxidsb.toString() + ")");
					}
					if (processTemplateFile_List != null
							&& processTemplateFile_List.size() > 0) {
						for (ProcessTemplateFile file : processTemplateFile_List) {
							String wz = file.getMonth() + "/"
									+ file.getFileName();
							String name = file.getOldfileName();
							tzMap.put(wz, name);
						}
					}
				}else if(zhuseroffer.getProcessId()!=null&&!"".equals(zhuseroffer.getProcessId())){
						NoPriceprocess np = (NoPriceprocess)totalDao.get(NoPriceprocess.class, zhuseroffer.getProcessId());
						if(np!=null){
						if(np.getProcardId()!=null){
							String hql_pwd = "from ProcessInforWWApplyDetail where markId =? and processNOs = ? and processNames =? and procardId = ? and (dataStatus is null or dataStatus not in('删除','取消')) ";
							ProcessInforWWApplyDetail wwDetail = (ProcessInforWWApplyDetail)totalDao.getObjectByCondition(hql_pwd, np.getMarkId(),np.getProcessNO(),np.getProcessName(),np.getProcardId());
							if (wwDetail != null) {
								Procard procard = (Procard) totalDao.getObjectById(Procard.class,
										wwDetail.getProcardId());
								if (procard != null) {
									String[] processNos = wwDetail.getProcessNOs().split(";");
									StringBuffer sb = new StringBuffer();
									for (String pno : processNos) {
										if (sb.length() == 0) {
											sb.append(pno);
										} else {
											sb.append("," + pno);
										}
							}
							List<ProcessTemplateFile> list1 = null;
							String banbenSql = null;
							String banciSql = null;
							if(procard.getBanBenNumber()==null || procard.getBanBenNumber().length()==0){
								banbenSql = " and (banBenNumber is null or banBenNumber='') ";
							}else{
								banbenSql = " and banBenNumber ='"+procard.getBanBenNumber()+"' ";
							}
							if (procard.getBanci() == null || procard.getBanci() == 0) {
								banciSql = " and (banci is null or banci=0)";
							} else {
								banciSql = " and banci=" + procard.getBanci();
							}
									if (procard.getProductStyle().equals("批产")) {
										// list1 = totalDao
										// .query(
										// "from ProcessTemplateFile where markId=? and processNO in("
										// + sb.toString()
										// + ") "
										// + banciSql
										// +
										// "and productStyle='批产' and (status is null or status!='历史')",
										// procard.getMarkId());
										// 图纸先以零件BOM为主
										List<Integer> maxIdList = totalDao
												.query(
														"select max(id) from ProcessTemplateFile where markId=? and processNO  is null "
																+ banciSql
																+ "and productStyle='批产' and type in('工艺规范','3D文件') group by oldfileName",
														procard.getMarkId());
										if (maxIdList != null
												&& maxIdList.size() > 0) {
											StringBuffer maxidsb = new StringBuffer();
											for (Integer maxId : maxIdList) {
												if (maxidsb.length() == 0) {
													maxidsb.append(maxId);
												} else {
													maxidsb.append("," + maxId);
												}
											}
											list1 = totalDao
													.query("from ProcessTemplateFile where id in ("
															+ maxidsb
																	.toString()
															+ ")");
										}
									} else {
										// list1 = totalDao
										// .query(
										// "from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
										// + sb.toString()
										// + ") )"
										// + banciSql
										// +
										// "and productStyle='试制' and (status is null or status!='历史')",
										// procard.getMarkId(), procard
										// .getProcardTemplateId());
										// 图纸先以零件BOM为主
										List<Integer> maxIdList = totalDao
												.query(
														"select max(id) from ProcessTemplateFile where markId=? and type in('工艺规范','3D文件') and processNO is  null and glId =?"
																+ banciSql
																+ "and productStyle='试制' group by oldfileName",
														procard.getMarkId(),
														procard
																.getProcardTemplateId());
										if (maxIdList != null
												&& maxIdList.size() > 0) {
											StringBuffer maxidsb = new StringBuffer();
											for (Integer maxId : maxIdList) {
												if (maxidsb.length() == 0) {
													maxidsb.append(maxId);
												} else {
													maxidsb.append("," + maxId);
												}
											}
											list1 = totalDao
													.query("from ProcessTemplateFile where id in ("
															+ maxidsb
																	.toString()
															+ ")");
										}
									}
							if (list1 != null && list1.size() > 0) {
								for (ProcessTemplateFile file : list1) {
									String wz = file.getMonth() + "/" + file.getFileName();
									String name = file.getOldfileName();
									if (file.getProcessName() != null) {
										file.setOldfileName("(" + file.getProcessName()
												+ ")" + file.getOldfileName());
									}
									tzMap.put(wz, name);
								}
							}
							if (wwDetail.getWwType().equals("包工包料")
									|| (wwDetail.getRelatDown() != null && wwDetail
											.getRelatDown().equals("是"))) {
								Set<Procard> sonptSet = procard.getProcardSet();
								if (sonptSet != null && sonptSet.size() > 0) {
									for (Procard son : sonptSet) {
										List<ProcessTemplateFile> list2 = findSonTz(son);
										if (list2 != null && list2.size() > 0) {
											for (ProcessTemplateFile file : list2) {
												String wz = file.getMonth() + "/"
														+ file.getFileName();
												String name = file.getOldfileName();
												if (file.getProcessName() != null) {
													file.setOldfileName("("
															+ file.getProcessName() + ")"
															+ file.getOldfileName());
												}
												tzMap.put(wz, name);
											}
										}
									}
								}
							}
						}

					}
						}else if(np.getProcessId()!=null){
							ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class, np.getProcessId());
							if(pt!=null){
								String banciSql = null;
								if (pt.getBanci() == null
										|| pt.getBanci() == 0) {
									banciSql = " and (banci is null or banci=0)";
								} else {
									banciSql = " and banci=" + pt.getBanci();
								}
								String[] processNos = np.getProcessNO() .split(";");
								StringBuffer sb = new StringBuffer();
								for (String pno : processNos) {
									if (sb.length() == 0) {
										sb.append(pno);
									} else {
										sb.append("," + pno);
									}
								}
								List<ProcessTemplateFile> list1 = null;
								if (pt.getProductStyle().equals("批产")) {
									// list1 = totalDao
									// .query(
									// "from ProcessTemplateFile where markId=? and processNO in("
									// + sb.toString()
									// + ") "
									// + banciSql
									// +
									// "and productStyle='批产' and (status is null or status!='历史')",
									// procard.getMarkId());
									// 先使用零件图纸
									List<Integer> maxIdList = totalDao
											.query(
													"select max(id) from ProcessTemplateFile where markId=?  "
															+ banciSql
															+ "and productStyle='批产' and type in('工艺规范','3D文件')  group by oldfileName",
													pt.getMarkId());
									if (maxIdList != null
											&& maxIdList.size() > 0) {
										StringBuffer maxidsb = new StringBuffer();
										for (Integer maxId : maxIdList) {
											if (maxidsb.length() == 0) {
												maxidsb.append(maxId);
											} else {
												maxidsb.append("," + maxId);
											}
										}
										list1 = totalDao
												.query("from ProcessTemplateFile where id in ("
														+ maxidsb.toString()
														+ ")");
									}
								} else {
									// list1 = totalDao
									// .query(
									// "from ProcessTemplateFile where id in(select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
									// + sb.toString()
									// + ") )"
									// + banciSql
									// +
									// "and productStyle='试制' group by oldfileName)",
									// procard.getMarkId(), procard
									// .getProcardTemplateId());
									List<Integer> maxIdList = totalDao
											.query(
													"select max(id) from ProcessTemplateFile where markId=? and processNO is null and glId =?"
															+ banciSql
															+ "and productStyle='试制' and type in('工艺规范','3D文件') group by oldfileName",
													pt.getMarkId(), pt.getId());
									if (maxIdList != null
											&& maxIdList.size() > 0) {
										StringBuffer maxidsb = new StringBuffer();
										for (Integer maxId : maxIdList) {
											if (maxidsb.length() == 0) {
												maxidsb.append(maxId);
											} else {
												maxidsb.append("," + maxId);
											}
										}
										list1 = totalDao
												.query("from ProcessTemplateFile where id in ("
														+ maxidsb.toString()
														+ ")");
									}
								}
								if (list1 != null && list1.size() > 0) {
									for (ProcessTemplateFile file : list1) {
										String wz = file.getMonth() + "/"
												+ file.getFileName();
										String name = file.getOldfileName();
										if (file.getProcessName() != null
												&& !file.getOldfileName().contains(
														file.getProcessName())) {
											file.setOldfileName("("
													+ file.getProcessName() + ")"
													+ file.getOldfileName());
										}
										tzMap.put(wz, name);
									}
								}
							}
						}
						}
				}
				}
			}
		return tzMap;
	}
	private List<ProcessTemplateFile> findSonTz(Procard procard) {
		// TODO Auto-generated method stub
		String banbenSql = null;
		if(procard.getBanBenNumber()==null || procard.getBanBenNumber().length()==0){
			banbenSql = " and (banBenNumber is null or banBenNumber='') ";
		}else{
			banbenSql = " and banBenNumber ='"+procard.getBanBenNumber()+"' ";
		}
		if (procard.getProcardStyle().equals("外购")) {
			List<ProcessTemplateFile> list1 = null;
			String banciSql = null;
			if (procard.getBanci() == null || procard.getBanci() == 0) {
				banciSql = " and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + procard.getBanci();
			}
			if (procard.getProductStyle().equals("批产")) {
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is null "
										+ banciSql
										+ "and productStyle='批产'  group by oldfileName",
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
					list1 = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			} else {
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is null and glId =? "
										+ banciSql
										+ "and productStyle='试制'  group by oldfileName",
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
					list1 = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			}
			return list1;
		} else {
			List<ProcessTemplateFile> list1 = null;
			String banciSql = null;
			if (procard.getBanci() == null || procard.getBanci() == 0) {
				banciSql = " and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + procard.getBanci();
			}
			if (procard.getProductStyle().equals("批产")) {
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is not null "
										+ banciSql
										+ "and productStyle='批产' group by oldfileName",
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
					list1 = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			} else {
				List<Integer> maxIdList = totalDao
						.query(
								"select max(id) from ProcessTemplateFile where markId=? and processNO is not null and glId =? "
										+ banciSql
										+ "and productStyle='试制' group by oldfileName",
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
					list1 = totalDao
							.query("from ProcessTemplateFile where id in ("
									+ maxidsb.toString() + ")");
				}
			}
			//
			if(list1==null){
				list1 = new ArrayList<ProcessTemplateFile>();
			}
			Set<Procard> sonptSet = procard.getProcardSet();
			if (sonptSet != null && sonptSet.size() > 0) {
				for (Procard son : sonptSet) {
					List<ProcessTemplateFile> list2 = findSonTz(son);
					if (list2 != null && list2.size() > 0) {
						list1.addAll(list2);
					}
				}
			}
			return list1;
		}
	}
	public Map<Integer, Object> findListByStatus(ZhuserOffer zhuseroffer,int pageNo, int pageSize) {
		if (zhuseroffer == null) {
			zhuseroffer= new ZhuserOffer();
		}
		Users user = Util.getLoginUser();
		String hql = "usercode='"+user.getCode()+"'and status in('未报价','已报价') order by id desc";
		hql = totalDao.criteriaQueries(zhuseroffer,hql,null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	public Map<Integer, Object> findQueRenListByZhUser(ZhuserOffer zhuseroffer,int pageNo, int pageSize) {
		if (zhuseroffer == null) {
			zhuseroffer= new ZhuserOffer();
		}
		Users user = Util.getLoginUser();
		String hql = "status='打样' and usercode='"+user.getCode()+"'";
		hql = totalDao.criteriaQueries(zhuseroffer,hql,null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	public Map<Integer, Object> findQueRenList(ZhuserOffer zhuseroffer,int pageNo, int pageSize) {
		if (zhuseroffer == null) {
			zhuseroffer= new ZhuserOffer();
		}
		String hql = "status='打样' order by id desc";
		hql = totalDao.criteriaQueries(zhuseroffer,hql,null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	public Map<Integer, Object> findDailuRuListByZhUser(ZhuserOffer zhuseroffer,int pageNo, int pageSize) {
		if (zhuseroffer == null) {
			zhuseroffer= new ZhuserOffer();
		}
		String hql = "status='待录入价格' order by id desc";
		hql = totalDao.criteriaQueries(zhuseroffer,hql,null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	
	@Override
	public ZhuserOffer findZhOfferById(Integer id) {
		return (ZhuserOffer)totalDao.get(ZhuserOffer.class, id);
	}
	public Sample findSampleById(Integer id) {
		return (Sample)totalDao.get(Sample.class, id);
	}
	public ZhuserOffer findZhOfferBySam(Integer id) {
		Sample s = (Sample)totalDao.get(Sample.class, id);
		ZhuserOffer z= findZhOfferById(s.getZhuserOffer().getId());
		return z;
	}
	public Sample findSampleBySam(Integer id) {
		String hql = "from Sample where zhuserOffer.id=?";
		Sample s = (Sample)totalDao.getObjectByQuery(hql, id);
		return s;
	}
	@Override
	public boolean updateZhOffer(ZhuserOffer zhuseroffer) {
		if(zhuseroffer!=null){
			ZhuserOffer z = findZhOfferById(zhuseroffer.getId());
			if(zhuseroffer.getHsPrice()!=null&&zhuseroffer.getBhsPrice()!=null&&zhuseroffer.getTaxprice()!=null){
					z.setHsPrice(zhuseroffer.getHsPrice());
					z.setBhsPrice(zhuseroffer.getBhsPrice());
					z.setTaxprice(zhuseroffer.getTaxprice());
					z.setStatus("已报价");
				return totalDao.update(z);
			}else{
				return false;
			}
		}
		return false;
	}

	@Override
	public String updateQurenOffer(Integer[] offerId,Integer id) {
		YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj)totalDao.get(YuanclAndWaigj.class,id);
		String nowdate = Util.getDateTime("yyyy-MM-dd");
		if(nowdate.compareTo(yuanclAndWaigj.getBjEndDate())<0){
			return "未到确认期限，请到"+yuanclAndWaigj.getBjEndDate()+"报价结束后在确定";
		}else{
			for(Integer ids : offerId){
				ZhuserOffer z = (ZhuserOffer)totalDao.get(ZhuserOffer.class, ids);
				z.setStatus("打样");
				if(z!=null){
					totalDao.update(z);
				}else{
					return "没有该报价订单";
				}
			}
			String hql = "from ZhuserOffer where yuanclAndWaigj.id ="+id+" and status <>'打样' ";
			List<ZhuserOffer> s = totalDao.find(hql);
			for(ZhuserOffer z : s){
				z.setStatus("结束");
				totalDao.update(z);
			}
				yuanclAndWaigj.setPricestatus("打样中");
				totalDao.update(yuanclAndWaigj);
			return "接受该报价，请发送样品";
		}
	}
	/**
	 * 手动添加样品
	 */
	public List<Sample> addSampleByself(Sample sample){
		Upload u = new Upload();
		String uploadPath = "/upload/file/sample";
		File file1 = new File(uploadPath);
		if (!file1.exists()) {
			file1.mkdirs();// 如果不存在文件夹就创建
		}
		if (sample.getPicF()!=null&&!"".equals(sample.getPicFFileName())) {
			sample.setPic(u.UploadFile(sample.getPicF(), sample.getPicFFileName(),null,uploadPath,null));
		} if(sample.getCheckNoteF()!=null&&!"".equals(sample.getCheckNoteFFileName())){
			sample.setCheckNote(u.UploadFile(sample.getCheckNoteF(), sample.getCheckNoteFFileName(),null,uploadPath,null));
		} if(sample.getCaiZhiF()!=null&&!"".equals(sample.getCaiZhiFFileName())){
			sample.setCaiZhi(u.UploadFile(sample.getCaiZhiF(), sample.getCaiZhiFFileName(),null,uploadPath,null));	
		} if(sample.getHuanBaoF()!=null&&!"".equals(sample.getHuanBaoFFileName())){
			sample.setHuanBao(u.UploadFile(sample.getHuanBaoF(), sample.getHuanBaoFFileName(),null,uploadPath,null));	
		} if(sample.getCaiLiaoF()!=null&&!"".equals(sample.getCaiLiaoFFileName())){
			sample.setCaiLiao(u.UploadFile(sample.getCaiLiaoF(), sample.getCaiLiaoFFileName(),null,uploadPath,null));
		} if(sample.getYanWuF()!=null&&!"".equals(sample.getYanWuFFileName())){
			sample.setYanWu(u.UploadFile(sample.getYanWuF(), sample.getYanWuFFileName(),null,uploadPath,null));	
		} if(sample.getMoJuRenZhenF()!=null&&!"".equals(sample.getMoJuRenZhenFFileName())){
			sample.setMoJuRenZhen(u.UploadFile(sample.getCheckNoteF(), sample.getCheckNoteFFileName(),null,uploadPath,null));	
		} if(sample.getGongYiF()!=null&&!"".equals(sample.getGongYiFFileName())){
			sample.setGongYi(u.UploadFile(sample.getGongYiF(), sample.getGongYiFFileName(),null,uploadPath,null));
		}
		if(totalDao.save(sample)){//添加成功后进行查询手动添加的样品
			String hql_noZid = "from Sample where zhuserOffer.id == null";
			List<Sample> sList = totalDao.query(hql_noZid);
			return sList;
		}else{
			return null;
		}
		
	}
	@Override
	public String addSample(ZhuserOffer zhuseroffer,Sample sample,String status) {
		if (zhuseroffer != null) {
			Upload u = new Upload();
			String uploadPath = "/upload/file/sample";
			File file1 = new File(uploadPath);
			if (!file1.exists()) {
				file1.mkdirs();// 如果不存在文件夹就创建
			}
			if (sample.getPicF()!=null&&!"".equals(sample.getPicFFileName())) {
					sample.setPic(u.UploadFile(sample.getPicF(), sample.getPicFFileName(),null,uploadPath,null));
				} if(sample.getCheckNoteF()!=null&&!"".equals(sample.getCheckNoteFFileName())){
					sample.setCheckNote(u.UploadFile(sample.getCheckNoteF(), sample.getCheckNoteFFileName(),null,uploadPath,null));
				} if(sample.getCaiZhiF()!=null&&!"".equals(sample.getCaiZhiFFileName())){
					sample.setCaiZhi(u.UploadFile(sample.getCaiZhiF(), sample.getCaiZhiFFileName(),null,uploadPath,null));	
				} if(sample.getHuanBaoF()!=null&&!"".equals(sample.getHuanBaoFFileName())){
					sample.setHuanBao(u.UploadFile(sample.getHuanBaoF(), sample.getHuanBaoFFileName(),null,uploadPath,null));	
				} if(sample.getCaiLiaoF()!=null&&!"".equals(sample.getCaiLiaoFFileName())){
					sample.setCaiLiao(u.UploadFile(sample.getCaiLiaoF(), sample.getCaiLiaoFFileName(),null,uploadPath,null));
				} if(sample.getYanWuF()!=null&&!"".equals(sample.getYanWuFFileName())){
					sample.setYanWu(u.UploadFile(sample.getYanWuF(), sample.getYanWuFFileName(),null,uploadPath,null));	
				} if(sample.getMoJuRenZhenF()!=null&&!"".equals(sample.getMoJuRenZhenFFileName())){
					sample.setMoJuRenZhen(u.UploadFile(sample.getCheckNoteF(), sample.getCheckNoteFFileName(),null,uploadPath,null));	
				} if(sample.getGongYiF()!=null&&!"".equals(sample.getGongYiFFileName())){
					sample.setGongYi(u.UploadFile(sample.getGongYiF(), sample.getGongYiFFileName(),null,uploadPath,null));
				}
				sample.setStatus("分析文档已提交");
				sample.setZhuserOffer(zhuseroffer);
				totalDao.save(sample);
				zhuseroffer.setStatus("分析文档已提交");
				totalDao.update(zhuseroffer);
				return "提交分析文件成功";
				}else{
					return "获取报价表失败";
				}
		}

	@Override
	public Map<Integer, Object> findAllSample(Sample sample, int pageNo,
			int pageSize) {
		if (sample == null) {
			sample= new Sample();
		}
		String hql = totalDao.criteriaQueries(sample,null);
		hql+=" order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	
	public Map<Integer, Object> findAll(Sample sample, int pageNo,
			int pageSize) {
		if (sample == null) {
			sample= new Sample();
		}
		String hql = totalDao.criteriaQueries(sample,null);
		hql+=" order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	@Override
	public Map<Integer, Object> findAllSampleByZhUser(Sample sample,
			int pageNo, int pageSize) {
		if (sample == null) {
			sample= new Sample();
		}
		Users user = Util.getLoginUser();
		String hql = "from Sample where zhuserOffer.usercode='"+user.getCode()+"' order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	
	public Map<Integer, Object> findAllSampleByCaiGou(Sample sample,
			int pageNo, int pageSize,String status) {
		if (sample == null) {
			sample= new Sample();
		}
		String hql = "";
		if("caigou".equals(status)){
			hql = "from Sample where status='分析文档已提交' order by id desc";
		}else if("pass".equals(status)){
			hql = "from Sample where status='同意' order by id desc";
		}
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}
	
	
	public String inputPrice(YuanclAndWaigj yuanclAndWaigj){
		yuanclAndWaigj = (YuanclAndWaigj)totalDao.get(YuanclAndWaigj.class, yuanclAndWaigj.getId());
		if(yuanclAndWaigj!=null){
			String hql = "from ZhuserOffer where yuanclAndWaigj.id ="+yuanclAndWaigj.getId()+" and status = '同意' ";
			ZhuserOffer z = (ZhuserOffer)totalDao.getObjectByCondition(hql, null);
		if(z!=null){
			if("同意".equals(z.getStatus())){
				if(z!=null){
					Price p = new Price();
					p.setHsPrice(z.getHsPrice());
					p.setBhsPrice(z.getBhsPrice());
					p.setTaxprice(z.getTaxprice());
					p.setPartNumber(z.getMarkId());
					p.setName(z.getName());
					p.setSpecification(z.getSpecification());
					p.setBanbenhao(z.getBanbenhao());
					p.setWlType(z.getWgType());
					p.setGysId(z.getZhUserId());
					p.setBanbenhao(z.getBanbenhao());
					p.setGys(z.getCmp());
					p.setChargePerson(z.getCperson());
					p.setKgliao(z.getKgliao());
					if(totalDao.save(p)){
						YuanclAndWaigj y = z.getYuanclAndWaigj();
						if(y!=null){
							y.setPricestatus("有效");
							totalDao.update(y);
						}
						z.setStatus("录入价格完成");
						totalDao.update(z);
						return "录入价格完成";
					}else{
						return "录入价格失败";
					}
			}else{
				return "录入价格未完成";
			}
		}else{
			return "请审批！！";
		}
		}else{
			return "报价表不存在";
		}
		}else{
			return "外购件不存在";
		}
		
	}
	//确认样品
	public String passYangpin(Integer[] zhOfferId,Integer yuanclAndWaigjId){
		YuanclAndWaigj yuanclAndWaigj =(YuanclAndWaigj)totalDao.get(YuanclAndWaigj.class, yuanclAndWaigjId);
		for(Integer ids :zhOfferId){
			ZhuserOffer zhuserOffer = (ZhuserOffer)totalDao.get(ZhuserOffer.class, ids);
			String hql = "from Sample where zhuserOffer.id=?";
			Sample s = (Sample)totalDao.getObjectByQuery(hql, zhuserOffer.getId());
			if("分析文档已提交".equals(s.getStatus())){
				String processName = "样品审批流程";
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ZhuserOffer.class, zhuserOffer.getId(), "status",
							"id",
							"ZhuserOfferAction_findZhOfferForlook.action?zhuserOffer.id="+zhuserOffer.getId(), "报价是否通过，请您审批", true);
					if (epId != null && epId > 0) {
						zhuserOffer.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							zhuserOffer.setStatus("同意");
						} else {
							zhuserOffer.setStatus("未审批");
						}
						zhuserOffer.setStatus("未审批");
						totalDao.update(zhuserOffer);
						yuanclAndWaigj.setPricestatus("未审批");
						totalDao.update(yuanclAndWaigj);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return totalDao.update(s)+"";
			
			}else{
				return "未找到原样品记录";
			}
		}
		return null;
	}
//	public String updateSample(Sample sample,String status){
//		if(sample!=null){
//			Sample s = findSampleById(sample.getId());
//		if(s!=null){
//			s.setYangPin(status);
//			s.setStatus(status);
//			ZhuserOffer z = findZhOfferById(s.getZhuserOffer().getId());
//			if("到货".equals(status)){
//				String processName = "样品审批流程";
//				Integer epId = null;
//				try {
//					epId = CircuitRunServerImpl.createProcess(processName,
//							ZhuserOffer.class, zhuserOffer.getId(), "status",
//							"id",
//							"ZhuserOfferAction_findZhOfferForlook.action?zhuserOffer.id="+zhuserOffer.getId(), "报价是否通过，请您审批", true);
//					if (epId != null && epId > 0) {
//						sample.setEpId(epId);
//						CircuitRun circuitRun = (CircuitRun) totalDao.get(
//								CircuitRun.class, epId);
//						//ZhuserOffer zhuerOffer = sample.getZhuserOffer();
//						if ("同意".equals(circuitRun.getAllStatus())
//								&& "审批完成".equals(circuitRun.getAuditStatus())) {
//							sample.setStatus("同意");
//						} else {
//							sample.setStatus("未审批");
//						}
//						z.setStatus("审批中");
//						totalDao.update(z);
//						YuanclAndWaigj ycaiandWaigj = z.getYuanclAndWaigj();
//						ycaiandWaigj.setPricestatus("审批中");
//						totalDao.update(ycaiandWaigj);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			return totalDao.update(s)+"";
//		}else{
//			return "未找到原样品记录";
//		}
//		}else{
//			return "样品记录为空";
//		}
//	}
	public String shenpiZhoffer(Integer zhuserOfferId,String status){
		if(zhuserOfferId!=null){
			ZhuserOffer zhuserOffer = (ZhuserOffer)totalDao.get(ZhuserOffer.class, zhuserOfferId);
			if(status!=null){
				//-----
			}
			String processName = "样品审批流程";
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						ZhuserOffer.class, zhuserOffer.getId(), "status",
						"id",
						"ZhuserOfferAction_findZhOfferForlook.action?zhuserOffer.id="+zhuserOffer.getId(), "报价是否通过，请您审批", true);
				if (epId != null && epId > 0) {
					zhuserOffer.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						zhuserOffer.setStatus("同意");
					} else {
						zhuserOffer.setStatus("未审批");
					}
					YuanclAndWaigj ycaiandWaigj = zhuserOffer.getYuanclAndWaigj();
					ycaiandWaigj.setPricestatus("未审批");
					totalDao.update(ycaiandWaigj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "系统异常，添加失败！";
			}
			return "请对该报价进行审批！！";
		}else{
			return "没有对应的报价列表";
		}
	}

	@Override
	public String updateSample(Sample sample, String status) {
		// TODO Auto-generated method stub
		return null;
	}


	

}