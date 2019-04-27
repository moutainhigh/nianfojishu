package com.task.ServerImpl.gys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.task.Dao.TotalDao;
import com.task.Server.gys.GyslgxServer;
import com.task.entity.BrushCard;
import com.task.entity.InternalOrderDetail;
import com.task.entity.Machine;
import com.task.entity.Users;
import com.task.entity.gys.ProcardGys;
import com.task.entity.gys.ProcessGysInfor;
import com.task.entity.gys.ProcessGysZj;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.Measuring;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcessZj;
import com.task.entity.sop.RunningWaterCard;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.qd.LogoStickers;
import com.task.util.Util;
import com.tast.entity.zhaobiao.ZhUser;

public class GyslgxServerImpl implements GyslgxServer{
 private TotalDao totalDao;

public TotalDao getTotalDao() {
	return totalDao;
}

public void setTotalDao(TotalDao totalDao) {
	this.totalDao = totalDao;
}

@Override
public Map<Integer, Object> findProcardGysByCondition(ProcardGys procardGys,
		int pageNo,String pageStatus, int pageSize,String sql) {
	// TODO Auto-generated method stub
	if (procardGys == null) {
		procardGys = new ProcardGys();
	}
	String sql1=null;
	String sql2=null;
	if(sql==null){
		sql1="(jihuoStatua='未激活' or jihuoStatua is null)";
		sql2="jihuoStatua='激活'";
	}else{
		sql1=sql+" and (jihuoStatua='未激活' or jihuoStatua is null)";
		sql2=sql+" and jihuoStatua='激活'";
	}
	if(pageStatus!=null&&pageStatus.equals("NoCardLingGX")){
		sql2=sql2+" and status!='入库'";
	}
	List objs1=totalDao.query("from ProcardGys where "+sql1);
	String hql2 = totalDao.criteriaQueries(procardGys, sql2, null);
	int count = totalDao.getCount(hql2);
	List objs2 = totalDao.findAllByPage(hql2, pageNo, pageSize);
	Map<Integer, Object> map = new HashMap<Integer, Object>();
	map.put(1, objs1);
	map.put(2, objs2);
	map.put(3, count);
	// 下层流水卡片模板
	List<ProcardGys> pclist = totalDao.query("from ProcardGys where fatherId=?",procardGys.getId());;
	map.put(4, pclist);
	return map;
}


@Override
public boolean jihuoProcardGys(Integer id) {
	// TODO Auto-generated method stub
	Integer count =totalDao.createQueryUpdate("update ProcardGys set jihuoStatua='激活' where rootId=?", null, id);
	if(count>0){
		return true;
	}
	return false;
}

@Override
public ProcardGys findProcardGysById(Integer id) {
	// TODO Auto-generated method stub
	if(id!=null&&id>0){
		ProcardGys p=(ProcardGys) totalDao.getObjectById(ProcardGys.class, id);
		return p;
	}
	return null;
}

@Override
public Integer findMaxbelongLayer(Integer rootId) {
	// TODO Auto-generated method stub
	if (rootId != null && rootId > 0) {
		String hql = "select belongLayer from ProcardGys where rootId=? and procardstyle in ('总成','自制','组合','外购') order by belonglayer desc";
		Object obj = totalDao.getObjectByCondition(hql, rootId);
		return Integer.parseInt(obj.toString());
	}
	return null;
}

@Override
public List<ProcardGys> findProByBel(Integer fatherId, Integer maxBelongLayer) {
	// TODO Auto-generated method stub
	if (fatherId != null && fatherId > 0) {
		String hql = "from ProcardGys where fatherId=? ";
		if (maxBelongLayer != null && maxBelongLayer == 1) {
			hql = "from ProcardGys where id=? ";

		}
		return totalDao.query(hql, fatherId);
	}
	return null;
}

@Override
public Object[] findProcardByRunCard(Integer cardId) {
	// TODO Auto-generated method stub
	if (cardId != null && cardId > 0) {
		ProcardGys procardGys = (ProcardGys) totalDao.getObjectById(ProcardGys.class,
				cardId);
		if (procardGys != null) {
			// 如果是领取工序查看,判断是否为最小批次
					String hql2 = "from ProcardGys where markId=? and cardId =null and oldProcardId=null and"
							+ " procardStyle=? and rootId in "
							+ "(select id from ProcardGys where status not in('完成','入库') and markId in "
							+ "(select markId from ProcardGys where rootId=?)) order by selfCard ";
					ProcardGys minProcard = (ProcardGys) totalDao
							.getObjectByCondition(hql2,
									procardGys.getMarkId(), procardGys
											.getProcardStyle(), procardGys
											.getRootId());
					if (minProcard != null
							&& !procardGys.getId().equals(minProcard.getId())) {
						return new Object[] {
								null,
								null,
								null,
								"请先领取该件号的最小批次,更换卡号为"
										+ minProcard.getCardNum()
										+ "的生产周转卡!" };
					}
			// 下层流水卡片模板
			Set<ProcardGys> pcSet = procardGys.getProcardSet();
			List<ProcardGys> pclist = new ArrayList<ProcardGys>();
			pclist.addAll(pcSet);
			// 对应工序信息
			Set<ProcessGysInfor> pceSet = procardGys.getProcessInforSet();
			List<ProcessGysInfor> pcelist = new ArrayList<ProcessGysInfor>();
			pcelist.addAll(pceSet);
			// ----------------------工装-------------------------------------

			// --------------------------------------------------------------
			return new Object[] { procardGys, pclist, pcelist };
		}
	}
	return null;
}
@SuppressWarnings("unchecked")
public List listProvisionByMarkId(String markId) {
	String hql = "from Provision where provisionStatus='zj' and id in (select provisionId  from MarkIdZijian  where markId=?)";
	List list = totalDao.query(hql, markId);
	if (list == null || list.size() <= 0) {
		String hql2 = "from Provision where provisionStatus='zj'";
		List list2 = totalDao.query(hql2);
		list.addAll(list2);
	}
	return list;
}
@Override
public boolean saveZj(List contentList, List isQualifiedList, int processId) {
	if (contentList != null && contentList.size() > 0 && processId > 0) {
		// 查询工序信息
		ProcessGysInfor processInfor = (ProcessGysInfor) totalDao.getObjectById(
				ProcessGysInfor.class, processId);
		if (processInfor != null) {
			boolean bool = false;
			// 添加自检表
			for (int i = 0; i < contentList.size(); i++) {
				String content = (String) contentList.get(i);
				String isQualified = (String) isQualifiedList.get(i);
				if (content != null) {
					ProcessGysZj processZj = new ProcessGysZj();
					processZj.setZjItem(content);
					processZj.setIsQualified(isQualified);
					processZj.setProcessGysInfor(processInfor);// 对应工序关系
					bool = totalDao.save(processZj);
				}
			}
			// 更新工序状态
			if (bool) {
				processInfor.setStatus("自检");
				totalDao.update(processInfor);
			}
		}
		return true;
	}
	return false;
}
/***
 * 根据id查询工序(判断如何领取工序)
 * 
 * @param id
 * @return
 */
@SuppressWarnings("unchecked")
@Override
public Object[] findProcess(Integer id) {
	if (id != null && id > 0) {
		ProcessGysInfor process = (ProcessGysInfor) totalDao.getObjectById(
				ProcessGysInfor.class, id);
		if (process != null) {
			List list = new ArrayList();
			String hql = "from ProcessGysInfor c where c.procardGys.id=? and processNO<? and productStyle='自制' order by processNO";
			ProcessGysInfor topInfor = (ProcessGysInfor) totalDao
					.getObjectByCondition(hql,
							process.getProcardGys().getId(), process
									.getProcessNO());
			if (topInfor != null) {
				// 判断上一道工序是否完成
				if (!topInfor.getStatus().equals("完成")) {
					if (topInfor.getSubmmitCount() > 0F) {
						process.setTotalCount(topInfor.getSubmmitCount());
						list.add(process);
					} else {
						return new Object[] { null,
								"上一道工序尚未提交,请先领取上一道工序!谢谢!", false };
					}
				} else {
					list.add(process);
				}
			} else {
				list.add(process);
			}
			// 判断是否并行
			if (process.getProcessStatus().equals("yes")) {
				String hql2 = "from ProcessGysInfor c where c.procardGys.id=? and parallelId=?  and productStyle='自制' and totalCount!=applyCount and status <>'初始' order by processNO";
				list = totalDao.query(hql2, process.getProcardGys().getId(),
						process.getParallelId());
			}
			// 开始领取
			return new Object[] { list, "可以领取", true };
		}
	}
	return null;
}
/***
 * 领取工序
 * 
 * @param processIds
 * @param processNumbers
 * @param processCards
 * @return
 */
@Override
public String collorProcess(Integer[] processIds, Float[] processNumbers,
		List processCards) {
	String message = "领取成功";
	ZhUser zhUser=Util.getCurrzhUser();
	Integer zhUserId=null;
	if(zhUser==null){
		//return null;
	}else{
		zhUserId=zhUser.getId();
	}
	for (int i = 0; i < processIds.length; i++) {
		ProcessGysInfor process = (ProcessGysInfor) totalDao.getObjectById(
				ProcessGysInfor.class, processIds[i]);
		if (process != null) {
			if (!process.getStatus().equals("完成")) {
				// -------------------------------设备-------------------------------------------------------------------
				// 检查设备是否正常
				if (process.getShebeistatus()!=null&&!"否".equals(process.getShebeistatus())) {
					String sbhql = "from Machine where  workPosition=? and no=? and isGys=?";
					Machine machine = (Machine) totalDao
							.getObjectByCondition(sbhql, process
									.getGongwei(), process.getShebeiNo(),zhUserId);
					if (machine != null) {
						if (!"正常".equals(machine.getStatus())) {
							message = process.getProcessName()
									+ " 工序对应的设备不在正常运转,无法领取工序!";
							break;
						}
					} else {
						message = process.getProcessName()
								+ " 的设备校验已启用,请您先完善设备信息!";
						break;
					}
				}

				// -----------------------工艺规范-------------------------------------------------------、
				/*** 判断工序对应工艺规范是否存在 ****/
				if (process.getGongzhuangstatus()!=null&&!"否".equals(process.getGongzhuangstatus())) {
					// 查询工艺规程
					boolean gongyiBool = false;
					String hql_gongyi1 = "select id from GongyiGuicheng where jianNumb=? and isGys=?";
					Integer gongyigcId = (Integer) totalDao
							.getObjectByCondition(hql_gongyi1, process
									.getProcardGys().getMarkId(),zhUserId);
					if (gongyigcId != null) {
						// 查找工序
						String hql_gongyi2 = "select id from ProcessData where gongyiGuichengId = ? and gongxuNo=? and isGys=?";
						Integer processDataId = (Integer) totalDao
								.getObjectByCondition(hql_gongyi2,
										gongyigcId, process.getProcessNO(),zhUserId);
						if (processDataId != null) {
							String hql_gongyi3 = "select url from GongyiGuichengAffix where gongyiGuichengId=? and processDataId=?"
									+ " and affixType='tupian' and weizhi='gxsmlq' and isGys=?";
							String fileName = (String) totalDao
									.getObjectByCondition(hql_gongyi3,
											gongyigcId, processDataId,zhUserId);
							if (fileName != null && fileName.length() > 0) {
								gongyiBool = true;
							}
						}
					}
					if (gongyiBool == false) {
						message = process.getProcessName()
								+ " 工序无对应工艺规范信息,请通知工艺部门上传!";
						break;
					}
				}
				/*** 判断工序对应工艺规范是否存在结束 ****/

				// --------------------------------------量具------------------------------------------------
				if (process.getLiangjustatus()!=null&&!"否".equals(process.getLiangjustatus())) {
					if (process.getMeasuringId() != null) {
						Measuring newm = (Measuring) totalDao
								.getObjectById(Measuring.class, process
										.getMeasuringId());
						if (newm.getCalibrationTime() == null) {
							message = process.getProcessName()
									+ " 该量具尚未校验！！！";
							break;
						} else {
							// for (int j = 0; j < processCards.size(); j++)
							// {
							// //查选是否有借领记录
							// String sbhql5 =
							// "from Borrow where number=? and cardNum  in '"+processCards.get(j)+"'";
							// Borrow br = (Borrow)
							// totalDao.getObjectByCondition( sbhql5,
							// newm.getMeasuring_no());
							// if (br==null) {
							// message=process.getProcessName()+" 请选去工具库领去量具！！！";
							// break;
							// }
							// }
							long chashi = ((Util
									.StringToDate(
											newm.getCalibrationTime(),
											"yyyy-MM-dd").getTime() - Util
									.StringToDate(
											Util.getDateTime("yyyy-MM-dd"),
											"yyyy-MM-dd").getTime()) / 1000 / 60 / 60 / 24);
							int shijian = (int) chashi;
							if (shijian < 0) {
								message = process.getProcessName()
										+ " 该量具校验时间已过，请去校验!";
								break;
							}
						}
					}
				}
				// ------------------------------工装------------------------
				if (process.getGongzhuangstatus()!=null&&"是".equals(process.getGongzhuangstatus())) {

				}
				// -------------------------------设备-------------------------------------------------------------------
				// 检查设备是否正常
				if (process.getShebeistatus()!=null&&"是".equals(process.getShebeistatus())) {
					String sbhql = "from Machine where  workPosition=? and no=? and isGys=?";
					Machine machine = (Machine) totalDao
							.getObjectByCondition(sbhql, process
									.getGongwei(), process.getShebeiNo(),zhUserId);
					if (machine != null) {
						if (!"正常".equals(machine.getStatus())) {
							message = process.getProcessName()
									+ " 工序对应的设备不在正常运转,无法领取工序!";
							break;
						}
					} else {
						message = process.getProcessName()
								+ " 的量具校验已启用,请您先完善量具信息!";
						break;
					}
				}
				// 查询人员信息
				Users user = null;
				String hql = "from Users where cardId=?";
				String[] cards = (String[]) processCards.get(i);
				String cardid = "";
				String code = "";
				String name = "";
				for (int j = 0; j < cards.length; j++) {
					if (cards[j] != null && cards[j].length() > 0) {
						user = (Users) totalDao.getObjectByCondition(hql,
								cards[j]);
						if (user == null) {
							message = "不存在卡号为" + cards[j] + "的人员,请检查卡号";
							break;
						} else {
							// --------------------------------------//-------------------------验证考勤--------------------------------------------------------
							if (process.getKaoqingstatus()!=null&&!"否".equals(process.getKaoqingstatus())) {
								String cardId = user.getCardId();
								int card = Integer.parseInt(cardId);

								String hql2 = " from BrushCard where personId= (select id from Person where cardNo=? ) and brushDate=?";
								BrushCard bc = (BrushCard) totalDao
										.getObjectByCondition(hql2, card
												+ "", Util
												.getDateTime("yyyy-MM-dd"));
								if (bc == null) {
									message = "该员工     " + user.getName()
											+ "(" + user.getCode()
											+ ")今天没有考勤记录或考勤数据未同步!";
									break;
								}
							}
							// --------------------------------------验证考勤 完毕
							if (j == cards.length - 1) {
								cardid += user.getCardId();
								code += user.getCode();
								name += user.getName();
							} else {
								cardid += user.getCardId() + ",";
								code += user.getCode() + ",";
								name += user.getName() + ",";
							}
						}
					} else {
						if (i == 0) {
							message = "员工卡号不能为空!";
						} else {
							message = "领取成功";
						}
						break;
					}
				}

				if (user != null && code != null && code.length() > 0) {
					if (processNumbers[i] <= (process.getTotalCount() - process
							.getApplyCount())) {
						// 处理领取工序
						process.setApplyCount(processNumbers[i]
								+ process.getApplyCount());// 已领数量
						process.setUserCardId(cardid);
						process.setUsercodes(code);
						process.setUsernames(name);
						process.setUserId(user.getId());
						process.setFirstApplyDate(Util.getDateTime());
						process.setStatus("已领");

						// 更新工艺卡片状态为"领工序"
						process.getProcardGys().setStatus("领工序");
						// 更新工位信息
						if (process.getProcardGys().getGongwei() == null) {
							String hql2 = "from ProcessGysInfor pi where pi.procardGys.id=? order by processNO ";
							ProcessGysInfor minPi = (ProcessGysInfor) totalDao
									.getObjectByCondition(hql2, process
											.getProcardGys().getId());
							if (minPi != null) {
								process.getProcardGys().setGongwei(
										minPi.getGongwei());
								process.getProcardGys().setShebeiName(
										minPi.getShebeiName());
								process.getProcardGys().setShebeiNo(
										minPi.getShebeiNo());
							}
						}

						// 更新流水卡信息
						if (process.getProcardGys().getCardNum() != null) {
							String hql2 = "from RunningWaterCard where cardNum=?";
							RunningWaterCard rwc = (RunningWaterCard) totalDao
									.getObjectByCondition(hql2, process
											.getProcardGys().getCardNum());
							if (rwc != null) {
								rwc.setOwnUsername(process.getUsernames());// 更新目前持有人
								rwc.setCardStatus("领工序");
								message = "领取成功";
							}
						}
					} else {
						message = "领取数量不能大于总数量!";
						break;
					}
				} else {
					break;
				}
			}
		} else {
			message = "数据异常,工序不存在!";
			break;
		}
	}
	return message;
}
public ProcessGysInfor getObjectByIdProcessInfor(Integer id) {
	ProcessGysInfor oldProcess = (ProcessGysInfor) totalDao.getObjectById(
			ProcessGysInfor.class, id);
	return oldProcess;
}
public Gzstore getObjectByIdGzstore(Integer id) {
	Gzstore oldProcess = (Gzstore) totalDao
			.getObjectById(Gzstore.class, id);
	return oldProcess;
}
/***
 * 提交工序
 * 
 * @return
 */
@Override
public String updateProcess(ProcessGysInfor process) {
	if (process != null) {
		ProcessGysInfor oldProcess = (ProcessGysInfor) totalDao.getObjectById(
				ProcessGysInfor.class, process.getId());
		if (oldProcess != null) {
			boolean isShouJian = false;
			ProcardGys procard = (ProcardGys) totalDao.getObjectById(
					ProcardGys.class, oldProcess.getProcardGys().getId());
			/** 添加首检记录 **/

			if (oldProcess.getSubmmitCount() == 0
					&& process.getSubmmitCount() == 1
					&& oldProcess.getZjStatus().equals("yes")) {
				isShouJian = true;
				// 查询最近首检数据
				String hqlLs = "from LogoStickers where markId=?  and billDate = (select max(billDate) from LogoStickers where markId=? and processNO = '"
						+ oldProcess.getProcessNO() + "' )";
				LogoStickers ls = (LogoStickers) totalDao
						.getObjectByCondition(hqlLs, procard.getMarkId(),
								procard.getMarkId());
				// 添加首检数据
				LogoStickers newLs = new LogoStickers();
				if (ls != null) {
					BeanUtils.copyProperties(ls, newLs);
				} else {
					newLs.setStickStyle("首检样品");
					newLs.setMarkId(procard.getMarkId());
					newLs.setPartsName(procard.getProName());

				}
				newLs.setIsGys(procard.getZhuserId()+"");
				newLs.setCount(1F);
				newLs.setLotId(procard.getSelfCard());// 批次
				newLs.setProcessNO(oldProcess.getProcessNO().toString());// 工序号
				newLs.setOperator(oldProcess.getUsernames());// 操作者
				newLs.setCode(oldProcess.getUsercodes());// 操作者工号
				newLs.setExaminerCode(null);// 检验者工号
				newLs.setExaminerName(null);// 检验者姓名
				newLs.setBillDate(Util.getDateTime());// 时间
				newLs.setIsPrint("NO");// 是够打印
				// 计算编号
				String hqlLsnumber = "select max(number) from LogoStickers where stickStyle='"
						+ newLs.getStickStyle() + "'";
				List list = totalDao.find(hqlLsnumber);
				String newNumber = "";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				if (null != list && list.size() > 0 && null != list.get(0)) {
					String maxNumber = (String) list.get(0);
					String y = sdf.format(new Date());
					String headStr = maxNumber.substring(0, 12);
					String headStr2 = maxNumber.substring(0, 6);
					if (maxNumber.contains(y)) {
						newNumber = headStr + strAddOne(maxNumber);
					} else {
						newNumber = headStr2 + y + "001";
					}
				} else {
					if ("报废品".equals(newLs.getStickStyle())) {
						newNumber = "QD-RP-" + sdf.format(new Date())
								+ "001";
					} else if ("待处理品".equals(newLs.getStickStyle())) {
						newNumber = "QD-WP-" + sdf.format(new Date())
								+ "001";
					} else if ("首检样品".equals(newLs.getStickStyle())) {
						newNumber = "QD-FP-" + sdf.format(new Date())
								+ "001";
					}
				}
				newLs.setNumber(newNumber);
				totalDao.save(newLs);
			} else if (oldProcess.getZjStatus().equals("yes")) {
				// 检查首检样品的内容不为空，并且已经打印
				String hqlLs = "from LogoStickers where markId=?  and lotId=? and processNO=? and isGys=?";
				LogoStickers ls = (LogoStickers) totalDao
						.getObjectByCondition(hqlLs, procard.getMarkId(),
								procard.getSelfCard(), oldProcess
										.getProcessNO().toString(),procard.getZhuserId()+"");
				if (ls == null || ls.getDemandExamContent() == null
						|| ls.getDemandExamContent().length() <= 0) {
					return "首检模版的内容不能为空，请相关负责人补全首检内容后再提交工序!谢谢!";
				}

				if ("NO".equals(ls.getIsPrint())) {
					return "首检记录尚未打印，请打印后再提交工序!谢谢!";
				}

			}

			Float sumNumber = oldProcess.getApplyCount()
					- oldProcess.getSubmmitCount()
					- oldProcess.getBreakCount()
					- process.getSubmmitCount() - process.getBreakCount();
			if (sumNumber >= 0) {
				Float allSubmieNum = oldProcess.getSubmmitCount()
						+ process.getSubmmitCount()
						+ oldProcess.getBreakCount()
						+ process.getBreakCount();// 本工序总提交量
				/***
				 * 判断提交数量不能高于上一道工序的提交量
				 */
				// 查询上一道工序
				String hql3 = "from ProcessGysInfor where processNO<? and procard.id=? order by processNO desc";
				ProcessGysInfor onProcessInfor = (ProcessGysInfor) totalDao
						.getObjectByCondition(hql3, oldProcess
								.getProcessNO(), procard.getId());
				if (onProcessInfor != null) {
					if (allSubmieNum > onProcessInfor.getSubmmitCount()) {
						return "抱歉!您的提交量不能大于上一道工序的总提交量!";
					}
				}

				/**
				 * 计算设备节拍
				 */
				// 计算设备时间
				float alltime = (oldProcess.getOpshebeijiepai() + oldProcess
						.getOpcaozuojiepai())
						* (allSubmieNum);// (历史提交、损坏数量+当前提交、损坏数量)*设备节拍
				if (alltime > 0) {
					long time = (long) alltime;
					try {
						Date firstTime = null;
						// 如果是第一次提交，并是首检数据,从领取时间计算
						if (oldProcess.getSubmmitCount() == 0
								&& process.getSubmmitCount() == 1) {
							firstTime = Util.StringToDate(oldProcess
									.getFirstApplyDate(),
									"yyyy-MM-dd HH:mm:ss");
						} else {// 从第一次提交时间开始计算
							// 分割第一次提交时间
							String subDate = oldProcess.getSubmitDate();
							int dirstIndex = subDate.indexOf(",");
							if (dirstIndex > 0) {
								subDate = subDate.substring(0, dirstIndex);
							}
							firstTime = Util.StringToDate(subDate,
									"yyyy-MM-dd HH:mm:ss");
						}

						Date nowTime = Util.StringToDate(
								Util.getDateTime(), "yyyy-MM-dd HH:mm:ss");
						Long time2 = (nowTime.getTime() - firstTime
								.getTime()) / 1000;
						if (time2 < time) {
							return "您比设备干的还快，请诚信提交!";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				oldProcess.setSubmmitCount(process.getSubmmitCount()
						+ oldProcess.getSubmmitCount());// 提交量
				oldProcess.setBreakCount(process.getBreakCount()
						+ oldProcess.getBreakCount());// 不合格量

				/***
				 * 补料单提交处理
				 */
				Integer oldProcardId = procard.getOldProcardId();
				if (oldProcardId != null && oldProcardId > 0) {
					String hql = "from ProcessGysInfor pi where pi.procard.id=? and processNO=?  ";
					ProcessGysInfor zcProcessInfor = (ProcessGysInfor) totalDao
							.getObjectByCondition(hql, oldProcardId,
									oldProcess.getProcessNO());
					if (zcProcessInfor != null) {
						zcProcessInfor.setTotalCount(zcProcessInfor
								.getTotalCount()
								+ process.getSubmmitCount());
						zcProcessInfor.setApplyCount(zcProcessInfor
								.getApplyCount()
								+ process.getSubmmitCount());
					}
				}

				// 报废处理
				if (process.getBreakCount() > 0) {
					/**
					 * 更新后面工序的总数量
					 */
					String updateSql = "update ta_sop_w_ProcessGysInfor set totalCount=?,applyCount= "
							+ "CASE applyCount WHEN 0 THEN applyCount ELSE applyCount-"
							+ process.getBreakCount()
							+ " END where fk_procardId=? and processNO>?";
					totalDao.createQueryUpdate(null, updateSql, oldProcess
							.getTotalCount()
							- oldProcess.getBreakCount(), oldProcess
							.getProcardGys().getId(), oldProcess
							.getProcessNO());
					/**
					 * 生成补料单
					 */
					LogoStickers logoStickers = new LogoStickers();
					// 生成编号
					String date = Util.getDateTime("yyyyMM");
					String number = "";
					String hql = "select max(number) from LogoStickers where stickStyle='补料单' and number like 'QD-RP-"
							+ date + "%'";
					Object object = (Object) totalDao
							.getObjectByCondition(hql);
					if (object != null) {
						String maxNumber = object.toString();
						Long selfCard = Long.parseLong(maxNumber.substring(
								6, maxNumber.length())) + 1;// 当前最大流水卡片
						number = "QD-RP-" + selfCard.toString();
					} else {
						number = "QD-RP-" + date + "001";
					}
					logoStickers.setNumber(number);// 编号
					logoStickers.setStickStyle("补料单");
					logoStickers.setMarkId(procard.getMarkId());// 件号
					logoStickers.setLotId(procard.getSelfCard());// 批次号
					logoStickers.setProcessNO(oldProcess.getProcessNO()
							.toString());
					Users loginUser = Util.getLoginUser();// 获得登录用户
					logoStickers.setOperator(loginUser.getName());
					logoStickers.setCode(loginUser.getCode());
					logoStickers.setCount(process.getBreakCount());// 报废数量
					logoStickers.setPartsName(procard.getProName());// 名称
					logoStickers.setBillDate(Util.getDateTime());
					logoStickers.setOldProcardId(procard.getId());// 老流水单id
					logoStickers.setWorkingGroup(loginUser.getPassword()
							.getDeptNumber());// 部门编码
					logoStickers.setIsPrint("NO");
					logoStickers.setStatus("报废");
					totalDao.save(logoStickers);
				}

				// 数量全部完成
				if (oldProcess.getTotalCount() == oldProcess
						.getApplyCount()
						&& sumNumber == 0) {
					oldProcess.setStatus("完成");// 完成工序

					/*** 更新物品所在工位信息 ***/
					// 查询是否存在下一道工序
					String hql2 = "from ProcessGysInfor pi where pi.procard.id=? and processNO>? order by processNO";
					ProcessGysInfor nextPi = (ProcessGysInfor) totalDao
							.getObjectByCondition(hql2, oldProcess
									.getProcardGys().getId(), oldProcess
									.getProcessNO());
					if (nextPi != null) {
						oldProcess.getProcardGys().setGongwei(
								nextPi.getGongwei());
						oldProcess.getProcardGys().setShebeiName(
								nextPi.getShebeiName());
						oldProcess.getProcardGys().setShebeiNo(
								nextPi.getShebeiNo());
					} else {
						oldProcess.getProcardGys().setGongwei(
								oldProcess.getGongwei());
						oldProcess.getProcardGys().setShebeiName(
								oldProcess.getShebeiName());
						oldProcess.getProcardGys().setShebeiNo(
								oldProcess.getShebeiNo());
					}
					// 检查是否最后一道自制工序
					if (procard != null) {
						// 查询所有工序中的自制并且没有完成的工序
						String hql = "select count(*) from ProcessGysInfor pi where pi.procard.id=? and productStyle='自制' and status<>'完成'";
						Object obj = totalDao.getObjectByCondition(hql,
								procard.getId());
						// 如果不存在数据，说明自制的都已经完成。// 更新生产周转卡信息
						if (obj == null || "0".equals(obj.toString())) {
							procard.setStatus("完成");
							// 有卡，和无卡并行
							if (procard.getCardId() != null) {
								RunningWaterCard rwc = (RunningWaterCard) totalDao
										.getObjectById(
												RunningWaterCard.class,
												procard.getCardId());
								if (rwc != null) {
									rwc.setCardStatus("完成");
									rwc.setLastProcessTime(Util
											.getDateTime());
								}
							}
						}
					}
				}
				String subDatetime = Util.getDateTime();
				if (oldProcess.getSubmitDate() != null) {
					oldProcess.setSubmitDate(oldProcess.getSubmitDate()
							+ "," + subDatetime);
				} else {
					oldProcess.setSubmitDate(subDatetime);
				}
				totalDao.update(oldProcess);
				totalDao.update(procard);

				// 为打印用
				process.setProcessNO(oldProcess.getProcessNO());
				process.setProcessName(oldProcess.getProcessName());
				process.setUsernames(oldProcess.getUsernames());
				process.setSubmitDate(subDatetime);


				return "提交工序成功";
			} else {
				return "累计提交数量不能大于领取数量!";
			}
		} else {
			return "不存在您提交的工序!";
		}

	}
	return "数据异常!";
}
/***
 * 提交激活
 * 
 * @param processId
 * @return
 */
@SuppressWarnings("unchecked")
@Override
public String updateJihuo(Integer processId, Integer id) {
	ProcessGysInfor oldProcess = (ProcessGysInfor) totalDao.getObjectById(
			ProcessGysInfor.class, processId);
	// 检查是否是最后一道工序
	String processNOhql = "select max(processNO) from ProcessGysInfor where procardGys.id=? and productStyle='自制'";
	Object proObj = totalDao.getObjectByCondition(processNOhql, oldProcess
			.getProcardGys().getId());
	if (proObj != null) {
		Integer maxProcessNO = Integer.parseInt(proObj.toString());
		// 最后一道工序
		if (oldProcess.getProcessNO().equals(maxProcessNO)) {
			// 提交数量
			ProcardGys fatherProcard = findProcardGysById(id);
			fatherProcard.setTjNumber(oldProcess.getSubmmitCount());// 提交数量
			fatherProcard.setMinNumber(fatherProcard.getTjNumber()
					/ (fatherProcard.getCorrCount() == null ? 1
							: fatherProcard.getCorrCount()));// 最小提交数量
			// 完成操作
			if (fatherProcard.getFilnalCount().equals(
					fatherProcard.getTjNumber())) {
				fatherProcard.setStatus("完成");
			}
			totalDao.update(fatherProcard);
			if (fatherProcard != null
					&& "总成".equals(fatherProcard.getProcardStyle())) {
				if (fatherProcard.getRukuCount() == null)
					fatherProcard.setRukuCount(0F);// 入库数量清0
				if (fatherProcard.getCardId() != null) {
					// 如果是总成的最后一道工序，则更新流水卡状态为“完成”，可入库
					RunningWaterCard rwc = (RunningWaterCard) totalDao
							.getObjectById(RunningWaterCard.class,
									fatherProcard.getCardId());
					if (rwc != null) {
						rwc.setCardStatus("完成");
					}
				}
				return "";
			}
			// 查询本层是否存在未提交数据
			String thisHql = "from ProcardGys where rootId=? and belongLayer=? and (minNumber is null or minNumber<1)";
			int count = totalDao.getCount(thisHql, fatherProcard
					.getRootId(), fatherProcard.getBelongLayer());
			if (count == 0) {// 本层都已经提交
				/** 开始激活上层数据 */
				// 获得最小激活数量
				String fatherHql = "select max(minNumber) from ProcardGys where rootId=? and belongLayer=? ";
				Object fatherObj = totalDao.getObjectByCondition(fatherHql,
						fatherProcard.getRootId(), fatherProcard
								.getBelongLayer());
				if (fatherObj != null) {
					Float minNumber = Float
							.parseFloat(fatherObj.toString());
					// 获得上层所有流水单信息
					String fatherListhql = "from ProcardGys where rootId=? and belongLayer=?";
					List<ProcardGys> list = totalDao.query(fatherListhql,
							fatherProcard.getRootId(), fatherProcard
									.getBelongLayer() - 1);
					for (ProcardGys procard2 : list) {
						// 判断材料是否足够
						String hql3 = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?";
						String hql4 = "select sum(goodsZhishu) from Goods where goodsMarkId=?";
						if ("外购".equals(procard2.getProcardStyle())) {
							ProcardGys waiProcar = procard2;
							// 查询状态
							String sumOld = "select sum(filnalCount) from ProcardGys where status ='已发卡' and markId=?";
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
							Object obj = totalDao.getObjectByCondition(
									hql3, waiProcar.getMarkId(), waiProcar
											.getUnit());
							Float sumCount = 0F;
							if (obj != null) {
								sumCount = Float.parseFloat(obj.toString());
							}
							if (sumCount == null) {
								sumCount = Float.parseFloat(totalDao
										.getObjectByCondition(hql4,
												waiProcar.getMarkId())
										.toString());
							}

							// 判断数量是否足够
							waiProcar.setJihuoStatua("激活");
							waiProcar.setKlNumber(waiProcar
									.getFilnalCount());
							waiProcar.setTjNumber(0F);
							waiProcar.setMinNumber(0F);
							if (sumCount != null && sumCount > 0) {
								// 数量充足
								if (sumCount - sumoldCount >= 0) {
									if (sumCount - sumAll >= 0) {
										waiProcar.setTjNumber(waiProcar
												.getFilnalCount());
									} else {
										// 数量不足，
										waiProcar.setTjNumber(sumAll
												- sumCount);
									}

								}
								waiProcar.setMinNumber(waiProcar
										.getTjNumber()
										/ waiProcar.getQuanzi2()
										* waiProcar.getQuanzi1());
							}
							totalDao.update(waiProcar);

						} else {
							minNumber =(float) Math.floor(minNumber);
							procard2.setJihuoStatua("激活");
							if (procard2.getFilnalCount() > minNumber) {
								procard2.setKlNumber(procard2
										.getFilnalCount());
							} else {
								procard2.setKlNumber(minNumber);// 可领数量
							}
							totalDao.update(procard2);
//							String hql = " update ProcessInfor set totalCount=? where procard.id=?  ";
//							totalDao.createQueryUpdate(hql, null, procard2
//									.getKlNumber(), procard2.getId());
						}
					}

				}

			}

		}
	}
	return "";
}
@SuppressWarnings("unchecked")
@Override
public boolean deleteprocardtree(Integer id) {
	// TODO Auto-generated method stub
	ProcardGys procard = (ProcardGys) totalDao.getObjectById(ProcardGys.class, id);
	if (procard != null) {
		return  totalDao.delete(procard);
	}
	return false;
}
public static String strAddOne(String bianhao) {
	Integer intHao = Integer.parseInt(bianhao.substring(12));
	intHao++;
	String strHao = intHao.toString();
	while (strHao.length() < 3)
		strHao = "0" + strHao;
	return strHao;
}
 
}
