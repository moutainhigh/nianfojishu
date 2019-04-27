package com.task.ServerImpl.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.hibernate.dialect.FirebirdDialect;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.sop.ManualOrderPlanServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.ess.ProcardBlServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.OaAppDetailTemplate;
import com.task.entity.Price;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.approval.Signature;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.ManualOrderPlanTotal;
import com.task.entity.sop.Procard;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.YcWaiGouProcrd;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.AuditNode;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.ExecutionNode;
import com.task.util.Util;

@SuppressWarnings("unchecked")
public class ManualOrderPlanServerImpl implements ManualOrderPlanServer {
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public void xiufuHebingshuju() {
		List<ManualOrderPlanDetail> details = totalDao
				.query("from ManualOrderPlanDetail mm,ManualOrderPlan mo where mm.moptId in "
						+ "(select id from manualorderplantotal where epId in (select fk_exeProId from ExecutionNode where fk_exeProId in "
						+ "(select id FROM CircuitRun where id in (select epId from Manualorderplantotal) and allStatus = '同意') and auditDateTime > '2018-06-21' )) "
						+ "and mm.manualPlan.id=mo.id and mm.cgnumber < mo.number");

		for (ManualOrderPlanDetail mod : details) {
			ManualOrderPlan maualPlan = (ManualOrderPlan) totalDao
					.getObjectById(ManualOrderPlan.class, mod.getManualPlan()
							.getId());
			Set<ManualOrderPlanDetail> modSet = maualPlan.getModSet();
			mod.setManualPlan(null);
			mod.setShengChengMopIdTime(Util.getDateTime());
			modSet.remove(mod);
			maualPlan.setNumber(maualPlan.getNumber() - mod.getCgnumber());
			maualPlan.setModSet(modSet);
			totalDao.update(maualPlan);

			Set<ManualOrderPlanDetail> modSet1 = new HashSet<ManualOrderPlanDetail>();
			mod.setShengChengMopIdTime(Util.getDateTime());
			modSet1.add(mod);
			ManualOrderPlan maualPlan2 = new ManualOrderPlan(mod.getMarkId(),
					mod.getProName(), mod.getSpecification(), mod.getBanben(),
					mod.getCgnumber(), mod.getTuhao(), mod.getUnit(), mod
							.getKgliao(), mod.getWgType(), modSet1);
			mod.setManualPlan(maualPlan2);
			maualPlan2.setCategory(mod.getCategory());// 申请类别 （外购件、辅料）
			maualPlan2.setModSet(modSet1);
			maualPlan2.setAddtime(Util.getDateTime());// 添加时间(物料确认时间)
			maualPlan2.setNeedFinalDate(mod.getNeedFinalDate());
			maualPlan2.setOutcgNumber(mod.getOutcgNumber());
			maualPlan2.setBanci(mod.getBanci());
			maualPlan2.setStatus(mod.getStatus());
			maualPlan2.setProNumber(mod.getProNumber());
			maualPlan2.setDemanddept(mod.getDemanddept());
			totalDao.save(maualPlan2);
			if ("已采购".equals(mod.getStatus())) {
				List<WaigouPlan> liPlans = totalDao.query(
						"from WaigouPlan where mopId = ?", maualPlan.getId());
				for (WaigouPlan waigouPlan : liPlans) {
					waigouPlan.setMopId(maualPlan2.getId());
					totalDao.update(waigouPlan);
				}
			}
		}
	}

	public boolean addmaualPlan1(ManualOrderPlanDetail mod) {
		if (mod != null) {
			boolean bool = false;
			String hql = " from ManualOrderPlan where markId = ? and wgType = ? and (outcgNumber is null or outcgNumber=0)"
					+ " and status <> '取消' ";
			// 版本
			if (mod.getBanben() != null && mod.getBanben().length() > 0) {
				hql += " and banben = '" + mod.getBanben() + "'";
			} else {
				hql += " and (banben = '' or banben is null)";
			}
			// 规格
			if (mod.getSpecification() != null
					&& mod.getSpecification().length() > 0) {
				hql += " and specification = '" + mod.getSpecification() + "'";
			} else {
				hql += " and  (specification = '' or specification is null)";
			}
			// 供料属性
			if (mod.getKgliao() != null && mod.getKgliao().length() > 0) {
				hql += " and kgliao = '" + mod.getKgliao() + "'";
			} else {
				hql += " and (kgliao = '' or kgliao is null )";
			}
			// // 图号
			// if (mod.getTuhao() != null && mod.getTuhao().length() > 0) {
			// hql += " and tuhao = '" + mod.getTuhao() + "'";
			// } else {
			// hql += " and (tuhao is null or tuhao = '')";
			// }
			// 零件名称
			// if (mod.getProName() != null && mod.getProName().length() > 0) {
			// hql += " and proName = '" + mod.getProName() + "'";
			// }

			if (mod.getUnit() != null && mod.getUnit().length() > 0) {
				hql += " and unit = '" + mod.getUnit() + "'";
			} else {
				hql += " and (unit='' or unit is null )";
			}

			if (mod.getCategory() != null && mod.getCategory().length() > 0) {
				hql += " and category = '" + mod.getCategory() + "'";
			} else {
				hql += " and (category = '' or category is null)";
			}
			if (mod.getDemanddept() != null && mod.getDemanddept().length() > 0) {
				hql += " and demanddept = '" + mod.getDemanddept() + "'";
			} else {
				hql += " and (demanddept = '' or demanddept is null)";
			}

			ManualOrderPlan maualPlan = (ManualOrderPlan) totalDao
					.getObjectByCondition(hql, mod.getMarkId(), mod.getWgType());
			Set<ManualOrderPlanDetail> modSet = new HashSet<ManualOrderPlanDetail>();
			if (maualPlan == null) {
				bool = addManuPlan(mod, maualPlan, modSet);

			} else {
				if ("手动添加".equals(mod.getType())) {
					if (ProcardBlServerImpl.SystemShezhi("物料需求不合并")) {
						bool = addManuPlan(mod, maualPlan, modSet);
					} else {
						bool = updateManPlan(mod, maualPlan);
					}
				} else {
					bool = updateManPlan(mod, maualPlan);
				}
			}
			return bool;
		}
		return false;
	}

	private boolean updateManPlan(ManualOrderPlanDetail mod,
			ManualOrderPlan maualPlan) {
		boolean bool;
		Set<ManualOrderPlanDetail> modSet = maualPlan.getModSet();
		mod.setManualPlan(maualPlan);
		mod.setShengChengMopIdTime(Util.getDateTime());
		modSet.add(mod);
		mod.setOldNumber(mod.getCgnumber());
		maualPlan.setCategory(mod.getCategory());// 申请类别 （外购件、辅料）
		maualPlan.setNumber(mod.getCgnumber()
				+ (maualPlan.getNumber() == null ? 0f : maualPlan.getNumber()));
		maualPlan.setModSet(modSet);
		maualPlan.setOldNumber(maualPlan.getNumber());
		bool = totalDao.update(maualPlan);
		String needFinalDate = (String) totalDao
				.getObjectByCondition(
						"select  needFinalDate from ManualOrderPlanDetail where manualPlan.id=? order by needFinalDate desc ",
						maualPlan.getId());
		maualPlan.setNeedFinalDate(needFinalDate);
		return bool;
	}

	/**
	 * 添加新的物料需求表信息
	 * 
	 * @param mod
	 * @param maualPlan
	 * @param modSet
	 * @return
	 */
	private boolean addManuPlan(ManualOrderPlanDetail mod,
			ManualOrderPlan maualPlan, Set<ManualOrderPlanDetail> modSet) {
		boolean bool;
		mod.setManualPlan(maualPlan);
		mod.setShengChengMopIdTime(Util.getDateTime());
		mod.setOldNumber(mod.getCgnumber());
		modSet.add(mod);
		maualPlan = new ManualOrderPlan(mod.getMarkId(), mod.getProName(), mod
				.getSpecification(), mod.getBanben(), mod.getCgnumber(), mod
				.getTuhao(), mod.getUnit(), mod.getKgliao(), mod.getWgType(),
				modSet);
		maualPlan.setCategory(mod.getCategory());// 申请类别 （外购件、辅料）
		maualPlan.setModSet(modSet);
		maualPlan.setAddtime(Util.getDateTime());// 添加时间(物料确认时间)
		maualPlan.setNeedFinalDate(mod.getNeedFinalDate());
		maualPlan.setRukuNum(0F);
		maualPlan.setBanci(mod.getBanci());
		maualPlan.setStatus("未采购");
		maualPlan.setProNumber(mod.getProNumber());
		maualPlan.setDemanddept(mod.getDemanddept());
		maualPlan.setOldNumber(maualPlan.getNumber());
		// 判断该件号的采购量加上在途量加上库存量是否超出最高库存量。wxf
		
		bool = totalDao.save(maualPlan);
		return bool;
	}

	@Autowired
	public String maxTotalNum() {
		String before = Util.getDateTime("yyyyMM");
		String maxnumber = (String) totalDao
				.getObjectByCondition("select max(totalNum) from ManualOrderPlanTotal where totalNum like '"
						+ before + "%'");
		DecimalFormat df = new DecimalFormat("0000");
		if (maxnumber != null && !"".equals(maxnumber)) {
			// String number1 = paymentVoucher2.getNumber();
			String now_number = maxnumber.split(before)[1];
			Integer number2 = Integer.parseInt(now_number) + 1;
			String number3 = df.format(number2);
			return before + number3;
		} else {
			return before + "0001";
		}
	}

	// 添加多条物料需求为一条单据
	@Autowired
	public String addMaulPlanMultpart(List<ManualOrderPlanDetail> modList,
			ManualOrderPlanTotal total) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		boolean bool = false;
		if (modList != null && modList.size() > 0) {
			Set<ManualOrderPlanDetail> modSet = new HashSet<ManualOrderPlanDetail>();
			Float totalMoney = 0f;
			StringBuffer msg = new StringBuffer();
			String monthsplan = Util.getDateTime("yyyy-MM");
			for (ManualOrderPlanDetail mod : modList) {
				if (mod != null) {
					mod.setAddTime(Util.getDateTime());// 添加时间
					mod.setAddUsers(user.getName());// 添加人
					mod.setAddUsersCode(user.getCode());// 添加人工号
					if (mod.getProNumber() != null
							&& !"".equals(mod.getProNumber())) {
						mod.setCategory("研发");
					}
					mod.setRukuNum(0f);
					mod.setType("手动添加");// 添加方式
					mod.setProNumber(total.getProCode());
					if(total.getPlanType()!=null && total.getPlanType()>0){
						monthsplan = Util.getNextMonth3("yyyy-MM");
					}
					if ("辅料".equals(mod.getCategory())) {
						OaAppDetailTemplate oa = (OaAppDetailTemplate) totalDao
								.getObjectByCondition(
										" from OaAppDetailTemplate where wlcode =? and epStatus = '同意'",
										mod.getMarkId());
						if (oa == null) {
							msg.append("件号:" + mod.getMarkId()
									+ "不在辅料库中存在，或者审批同意。<br/>");
						}
						String time = Util.getDateTime();
						String hql = " from Price where partNumber =? and kgliao=? and"
								+ " pricePeriodStart<='"
								+ time
								+ "' and (pricePeriodEnd>'"
								+ time
								+ "' or pricePeriodEnd is null or pricePeriodEnd = '') and productCategory = '辅料'";
						String hql_banben = "";
						if (mod.getBanben() != null
								&& mod.getBanben().length() > 0) {
							hql += " and banbenhao ='" + mod.getBanben() + "'";
							hql_banben += " and banben = '" + mod.getBanben()
									+ "'";
						} else {
							hql += " and (banbenhao is null or banbenhao = '') ";
							hql_banben += " and (banben is null or banben = '' )";
						}
						Price price = (Price) totalDao.getObjectByCondition(
								hql, mod.getMarkId(), mod.getKgliao());
						if (price != null) {
							mod.setHsprice(price.getHsPrice().floatValue());
							mod.setBhsprice(price.getBhsPrice().floatValue());
							mod.setTotalMoney(mod.getHsprice()
									* mod.getCgnumber());
							totalMoney += mod.getTotalMoney();
						}
						Float yearSumNum = (Float) totalDao
								.getObjectByCondition(
										"select sum(cgnumber) from ManualOrderPlanDetail where "
												+ " markId =? and months like '"
												+ monthsplan.substring(0,4)
												+ "%' and demanddept =? and epStatus = '同意'",
										mod.getMarkId(), mod.getDemanddept());
						mod.setYearSumNum(yearSumNum);
					}
					mod.setMonths(monthsplan);
					modSet.add(mod);
					bool = totalDao.save(mod);
					if (!bool) {
						return "添加失败";
					}
				}
			}
			if (msg != null && msg.length() > 0) {
				throw new RuntimeException(msg.toString());
			}
			if (bool) {
				if (total == null) {
					total = new ManualOrderPlanTotal();
				}
				if (total.getTotalNum() == null
						|| "".equals(total.getTotalNum())) {
					String before = Util.getDateTime("yyyyMM");
					String maxnumber = (String) totalDao
							.getObjectByCondition("select max(totalNum) from ManualOrderPlanTotal where totalNum like '"
									+ before + "%'");
					DecimalFormat df = new DecimalFormat("0000");
					if (maxnumber != null && !"".equals(maxnumber)) {
						// String number1 = paymentVoucher2.getNumber();
						String now_number = maxnumber.split(before)[1];
						Integer number2 = Integer.parseInt(now_number) + 1;
						String number3 = df.format(number2);
						total.setTotalNum(before + number3);
					} else {
						total.setTotalNum(before + "0001");
					}
				}
				if (total.getUserId() == null)
					total.setUserId(user.getId());
				if (total.getUserName() == null)
					total.setUserName(user.getName());
				if (total.getUserCode() == null)
					total.setUserCode(user.getCode());
				if (total.getUserDept() == null)
					total.setUserDept(user.getDept());

				total.setTotalMoney(totalMoney);
				total.setAddTime(Util.getDateTime());
				total.setDetails(modSet);
				total.setMonths(monthsplan);
				boolean save = totalDao.save(total);
				if (save) {
					String valueCode = (String) totalDao
							.getObjectByCondition("select valueCode from CodeTranslation"
									+ " where type ='sys' and keyCode = '物料需求单添加分级审批'");
					String auditLe = "";
					if (valueCode != null && valueCode.equals("是")) {
						auditLe = "一级";
						Float allMoney = total.getEstimatePrice();
						if (allMoney != null && allMoney > 0) {
							if (allMoney <= 500F) {
								auditLe = "一级";
							} else if (allMoney <= 1000F) {
								auditLe = "二级";
							} else if (allMoney <= 10000F) {
								auditLe = "三级";
							} else {
								auditLe = "四级";
							}
						}
					}

					String processName = auditLe + "物料需求单申请";
					// if ("研发".equals(modList.get(0).getCategory())) {
					// processName = "项目采购申请";
					// processName = "项目需求单申请";
					// } else
					// if ("售后".equals(modList.get(0).getCategory())) {
					// processName = "售后备件采购申请";
					// } else
					if ("辅料".equals(modList.get(0).getCategory())) {
						processName = auditLe + "辅料采购申请";
					}
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								ManualOrderPlanTotal.class, total.getId(),
								"epStatus", "id",
								"ManualOrderPlanAction_manualPlanTotalPrint.action?id="
										+ total.getId(), user.getDept() + "部门 "
										+ user.getName() + "的采购申请单，请您审批", true);
						if (epId != null && epId > 0) {
							total.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								total.setEpStatus("同意");
								// for (ManualOrderPlanDetail mod : modList) {
								// mod.setPlanTotal(total);
								// addmaualPlan1(mod);
								// }
								// if ("预测订单".equals(mod.getType())) {
								// YcWaiGouProcrd ycprocard = (YcWaiGouProcrd)
								// totalDao
								// .getObjectByCondition(
								// " from YcWaiGouProcrd where mopdId =? ",
								// mod.getId());
								// ycprocard.setEpstatus("同意");
								// totalDao.update(ycprocard);
								// }
							} else {
								total.setEpStatus("未审批");
							}
							return totalDao.update(total) + "";
						}
					} catch (Exception e) {
						throw new RuntimeException(e.toString());
					}
				}
			}
		}

		return bool + "";
	}

	@Override
	public ManualOrderPlanTotal getManaualOrderPlanTotalById(Integer id,
			String status) {
		ManualOrderPlanTotal total = (ManualOrderPlanTotal) totalDao.get(
				ManualOrderPlanTotal.class, id);// getObjectById(ManualOrderPlanTotal.class,
		// id);
		if (total != null) {
			System.out.println(total.getDetails());// 不能注释。
			int index = 0;
			Set<ManualOrderPlanDetail> detailSet = total.getDetails();
			for (ManualOrderPlanDetail detail : detailSet) {
				StringBuffer goodsBuffer = new StringBuffer(
						"from Goods where goodsMarkId=?"
								+ " and goodsCurQuantity>0 and (fcStatus is null or fcStatus<>'封存' )"
								+ " and goodsClass in ('外购件库','综合库','研发库')");

				if (detail.getKgliao() != null
						&& detail.getKgliao().length() > 0) {
					goodsBuffer.append(" and kgliao = '" + detail.getKgliao()
							+ "'");
				}
				if (detail.getWgType() != null
						&& detail.getWgType().length() > 0) {
					goodsBuffer.append(" and wgType = '" + detail.getWgType()
							+ "'");
				}
				if (detail.getProName() != null
						&& detail.getProName().length() > 0) {
					goodsBuffer.append(" and goodsFullName = '"
							+ detail.getProName() + "'");
				}
				if (detail.getUnit() != null && detail.getUnit().length() > 0) {
					goodsBuffer.append(" and goodsUnit = '" + detail.getUnit()
							+ "'");
				}
				if (detail.getSpecification() != null
						&& detail.getSpecification().length() > 0) {
					goodsBuffer.append(" and goodsFormat = '"
							+ detail.getSpecification() + "'");
				}
				Float kcCount = (Float) totalDao.getObjectByCondition(
						"select sum(goodsCurQuantity)  "
								+ goodsBuffer.toString(), detail.getMarkId());
				detail.setKcCount(kcCount);
				if (status != null && "picking".equals(status)) {
					if (detail.getPickingStatus() != null
							&& "已领完".equals(detail.getPickingStatus())) {
						continue;
					}
					List<Goods> goodsList = totalDao.query(goodsBuffer
							.toString(), detail.getMarkId());
					if (goodsList != null && goodsList.size() > 0) {
						for (Goods goods : goodsList) {
							goods.setListIndex(index);
							index++;
						}
					}
					detail.setGoodsList(goodsList);

					// 设置领取数量
					if (goodsList != null && goodsList.size() == 1) {
						float weiLingNum = 0;// 未领数量
						if (detail.getPickingNumber() == null) {
							weiLingNum = detail.getCgnumber();
						} else {
							weiLingNum = detail.getCgnumber()
									- detail.getPickingNumber();
						}
						float curQuantity = goodsList.get(0)
								.getGoodsCurQuantity();
						if (weiLingNum < curQuantity) {
							detail.setLingquNum(weiLingNum);// 领取数量
							goodsList.get(0).setXqCount(weiLingNum);// 库存领取数量
						} else {
							detail.setLingquNum(curQuantity);
							goodsList.get(0).setXqCount(curQuantity);
						}
					}
				}

			}
			return total;
		}

		return null;
		// List<ManualOrderPlanDetail> list =
		// totalDao.query("from ManualOrderPlanDetail where planTotal.id=?",
		// id);
		// for (ManualOrderPlanDetail manualOrderPlanDetail : list) {
		// total.getDetails().add(manualOrderPlanDetail);
		// }
	}

	// 查询物料需求单
	@Override
	public Object[] findManualPlanTotal(ManualOrderPlanTotal total,
			Integer pageNo, Integer pageSize, String status, String tag) {
		if (total == null) {
			total = new ManualOrderPlanTotal();
		}
		String markId = total.getMarkId();
		total.setMarkId(null);
		String hql = totalDao.criteriaQueries(total, null);

		// 领料
		if (status != null && "picking".equals(status)) {
			hql += " and epStatus = '同意'";
		}

		if (markId != null && markId.length() > 0) {
			List<Integer> list = totalDao
					.query("select d.planTotal.id from ManualOrderPlanDetail"
							+ " d where d.markId like '%" + markId + "%'");
			StringBuffer idBuffer = new StringBuffer();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (i == 0) {
						idBuffer.append(list.get(i));
					} else {
						idBuffer.append("," + list.get(i));
					}
				}
				hql += " and id in (" + idBuffer.toString() + ")";
			} else {
				hql += " and id =0 ";
			}
			total.setMarkId(markId);
		}
		// if ("wsq".equals(status)) {
		// hql += " and epId is null";
		// }
		// if ("fl".equals(flag)) {
		// hql += " and category = '辅料' ";
		// }else {
		// hql +=
		// " and (category <> '辅料' or category is null or category = '')";
		// }
		// hql += " and outcgNumber is not null and outcgNumber = cgnumber";
		// if ("已采购".equals(mod.getStatus())) {
		// } else if ("未采购".equals(mod.getStatus())) {
		// hql += " and (outcgNumber is null or outcgNumber = 0)";
		// }
		List<ManualOrderPlanTotal> modPlanList = null;
		if ("daoru".equals(tag)) {
			// modPlanList = totalDao.query(hql + " order by id desc");
		} else {
			modPlanList = totalDao.findAllByPage(hql + " order by id desc",
					pageNo, pageSize);
			for (ManualOrderPlanTotal manualOrderPlanTotal : modPlanList) {
				System.out.println(manualOrderPlanTotal.getDetails());// 不要删除
			}
		}
		// Float num = 0f;
		// Float num1 = 0f;
		// Float num2 = 0f;
		// for (ManualOrderPlanTotal mod0 : modPlanList) {
		// num += mod0.getCgnumber() == null ? 0 : mod0.getCgnumber();
		// num1 += mod0.getOutcgNumber() == null ? 0 : mod0.getOutcgNumber();
		// num2 += mod0.getRukuNum() == null ? 0 : mod0.getRukuNum();
		// }
		int count = totalDao.getCount(hql + " order by id desc");
		return new Object[] { modPlanList, count };
	}

	@Override
	public String delManualTotal(Integer id) {
		ManualOrderPlanTotal total = (ManualOrderPlanTotal) totalDao
				.getObjectById(ManualOrderPlanTotal.class, id);
		boolean flag = false;
		if (total != null) {
			Set<ManualOrderPlanDetail> details = total.getDetails();
			if (details != null && details.size() > 0) {
				for (ManualOrderPlanDetail manualOrderPlanDetail : details) {
					flag = totalDao.delete(manualOrderPlanDetail);
					if (!flag) {
						return "删除失败";
					}
				}
			}
			Integer epId = null;
			if (total.getEpId() != null && total.getEpId() > 0) {
				epId = total.getEpId();
			}
			flag = totalDao.delete(total);
			if (flag) {
				if (epId != null)
					CircuitRunServerImpl.deleteCircuitRun(epId);
				return "删除成功";
			}
		} else {
			return "没有找到该需求单";
		}

		return "删除失败";
	}

	@Override
	public String updateManualTotal(List<ManualOrderPlanDetail> details,
			ManualOrderPlanTotal total) throws Exception {
		Users user = (Users) totalDao
				.getObjectByCondition(
						" from Users where id = ("
								+ " select userId from ManualOrderPlanTotal where id =? )",
						total.getId());
		Set<ManualOrderPlanDetail> modSet = new HashSet<ManualOrderPlanDetail>();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < details.size(); i++) {
			if (details.get(i) != null) {
				Integer id = details.get(i).getId();
				if (id == null) {
					continue;
				}
				if (i == 0) {
					buffer.append(id);
				} else {
					buffer.append("," + id);
				}
			}
		}
		List<ManualOrderPlanDetail> list = totalDao.query(
				"from ManualOrderPlanDetail where planTotal.id=? and id not in("
						+ buffer.toString() + ")", total.getId());
		if (list != null && list.size() > 0) {
			for (ManualOrderPlanDetail manualOrderPlanDetail : list) {
				totalDao.delete(manualOrderPlanDetail);
			}
		}
		boolean bool = false;
		Float totalMoney = 0f;
		for (ManualOrderPlanDetail mod : details) {
			if (mod != null) {
				mod.setRukuNum(0f);
				mod.setType("手动添加");// 添加方式
				mod.setProNumber(total.getProCode());
				if (mod.getId() != null) {
					ManualOrderPlanDetail old = (ManualOrderPlanDetail) totalDao
							.get(ManualOrderPlanDetail.class, mod.getId());
					// 文件没有做修改，继续保持
					if (mod.getFileName() != null
							&& !"".equals(mod.getFileName())
							&& (mod.getFileUrl() == null || "".equals(mod
									.getFileUrl()))) {
						String fileUrl = (String) totalDao
								.getObjectByCondition(
										"select fileUrl from ManualOrderPlanDetail where id =?",
										mod.getId());
						old.setFileUrl(fileUrl);
					}
					// 文件删除
					old.setUpdateTime(Util.getDateTime());
					old.setCgnumber(mod.getCgnumber());
					old.setTuhao(mod.getTuhao());
					old.setYearSumNum(mod.getYearSumNum());
					old.setYongtu(mod.getYongtu());
					if (old.getHsprice() != null) {
						old.setTotalMoney(old.getHsprice() * old.getCgnumber());
						totalMoney += old.getTotalMoney();
					}
					old.setDemanddept(mod.getDemanddept());
					bool = totalDao.update(old);
					modSet.add(old);
					bool = true;
				} else {
					if (mod.getMarkId() == null
							|| mod.getMarkId().length() == 0) {
						continue;
					}
					mod.setAddUsers(user.getName());// 添加人
					mod.setAddUsersCode(user.getCode());// 添加人工号
					mod.setAddTime(Util.getDateTime());// 添加时间
					String time = Util.getDateTime();
					String hql = " from Price where partNumber =? and kgliao=? and"
							+ " pricePeriodStart<='"
							+ time
							+ "' and (pricePeriodEnd>'"
							+ time
							+ "' or pricePeriodEnd is null or pricePeriodEnd = '') and productCategory = '辅料'";
					String hql_banben = "";
					if (mod.getBanben() != null && mod.getBanben().length() > 0) {
						hql += " and banbenhao ='" + mod.getBanben() + "'";
						hql_banben += " and banben = '" + mod.getBanben() + "'";
					} else {
						hql += " and (banbenhao is null or banbenhao = '') ";
						hql_banben += " and (banben is null or banben = '' )";
					}
					Price price = (Price) totalDao.getObjectByCondition(hql,
							mod.getMarkId(), mod.getKgliao());
					if (price != null) {
						mod.setHsprice(price.getHsPrice().floatValue());
						mod.setBhsprice(price.getBhsPrice().floatValue());
						mod.setTotalMoney(mod.getHsprice() * mod.getCgnumber());
						totalMoney += mod.getTotalMoney();
					}
					Float yearSumNum = (Float) totalDao
							.getObjectByCondition(
									"select sum(cgnumber) from ManualOrderPlanDetail where "
											+ " markId =? and months like '"
											+ Util.getDateTime("yyyy")
											+ "%' and demanddept =? and epStatus = '同意'",
									mod.getMarkId(), mod.getDemanddept());
					mod.setYearSumNum(yearSumNum);
					mod.setMonths(Util.getDateTime("yyyy-MM月"));
					bool = totalDao.save(mod);
					bool = true;
					modSet.add(mod);
				}
				if (!bool) {
					throw new Exception("物料需求明细保存异常，修改失败！");
				}
			}
		}
		if (bool) {
			// if(total==null ){
			// total = new ManualOrderPlanTotal();
			// }
			ManualOrderPlanTotal planTotal = (ManualOrderPlanTotal) totalDao
					.getObjectById(ManualOrderPlanTotal.class, total.getId());
			planTotal.setUpdateTime(Util.getDateTime());
			planTotal.setRemark(total.getRemark());
			planTotal.setApplication(total.getApplication());
			planTotal.setProCode(total.getProCode());
			planTotal.setProName(total.getProName());
			planTotal.setTotalMoney(totalMoney);
			boolean isfou = false;

			String valueCode = (String) totalDao
					.getObjectByCondition("select valueCode from CodeTranslation"
							+ " where type ='sys' and keyCode = '物料需求单添加分级审批'");
			String auditLe = "";
			if (valueCode != null && valueCode.equals("是")) {
				String auditLe1 = "一级";
				Float allMoney1 = planTotal.getEstimatePrice();
				if (allMoney1 != null && allMoney1 > 0) {
					if (allMoney1 <= 500F) {
						auditLe1 = "一级";
					} else if (allMoney1 <= 1000F) {
						auditLe1 = "二级";
					} else if (allMoney1 <= 10000F) {
						auditLe1 = "三级";
					} else {
						auditLe1 = "四级";
					}
				}
				auditLe = "一级";
				Float allMoney = total.getEstimatePrice();
				if (allMoney != null && allMoney > 0) {
					if (allMoney <= 500F) {
						auditLe = "一级";
					} else if (allMoney <= 1000F) {
						auditLe = "二级";
					} else if (allMoney <= 10000F) {
						auditLe = "三级";
					} else {
						auditLe = "四级";
					}
				}
				if (!auditLe1.equals(auditLe))
					isfou = true;
			}

			planTotal.setEstimatePrice(total.getEstimatePrice());
			planTotal.setDetails(modSet);
			// boolean update = totalDao.update(planTotal);
			if (isfou || "打回".equals(planTotal.getEpStatus())) {
				if (planTotal.getEpId() != null) {
					CircuitRunServerImpl.deleteCircuitRun(planTotal.getEpId());
				}
				String processName = auditLe + "物料需求单申请";
				// if ("研发".equals(details.get(0).getCategory())) {
				// processName = "项目采购申请";
				// } else
				// if ("售后".equals(details.get(0).getCategory())) {
				// processName = "售后备件采购申请";
				// } else
				if ("辅料".equals(details.get(0).getCategory())) {
					processName = auditLe + "辅料采购申请";
				}
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ManualOrderPlanTotal.class, planTotal.getId(),
							"epStatus", "id",
							"ManualOrderPlanAction_manualPlanTotalPrint.action?id="
									+ planTotal.getId(), user.getDept() + "部门 "
									+ user.getName() + "的采购申请单，请您审批", true);
					if (epId != null && epId > 0) {
						planTotal.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							planTotal.setEpStatus("同意");
							// for (ManualOrderPlanDetail mod : details) {
							// mod.setPlanTotal(planTotal);
							// addmaualPlan1(mod);
							// }
							// if ("预测订单".equals(mod.getType())) {
							// YcWaiGouProcrd ycprocard = (YcWaiGouProcrd)
							// totalDao
							// .getObjectByCondition(
							// " from YcWaiGouProcrd where mopdId =? ",
							// mod.getId());
							// ycprocard.setEpstatus("同意");
							// totalDao.update(ycprocard);
							// }
						} else {
							planTotal.setEpStatus("未审批");
						}
					}
				} catch (Exception e) {
					throw new Exception(e.toString());
				}
			}
			return totalDao.update(planTotal) + "";
		}
		return null;
	}

	// 签名
	@Override
	public Map<Integer, Object> findPayExecutionNode(Integer id) {
		// FundApply fundApply = (FundApply)
		// this.totalDao.getObjectById(FundApply.class, id);
		ManualOrderPlanTotal total = (ManualOrderPlanTotal) totalDao
				.getObjectById(ManualOrderPlanTotal.class, id);
		String nodeHql = "from ExecutionNode where auditStatus='同意' and  circuitRun.id=? order by auditLevel desc";
		// List<ExecutionNode> list1 = this.totalDao.query(hql,
		// fundApply.getEpId());
		List<ExecutionNode> nodeList = totalDao.query(nodeHql, total.getEpId());
		List<Signature> list = new ArrayList<Signature>();
		for (int i = 0; i < nodeList.size(); i++) {
			ExecutionNode executionNode = nodeList.get(i);
			String username = executionNode.getAuditUserName();
			String hql = "from Signature where name=? and default_address=?";
			Signature signature = (Signature) totalDao.getObjectByCondition(
					hql, username, "默认");
			Signature signature2 = new Signature();
			BeanUtils.copyProperties(signature, signature2);

			String auditDateTime = executionNode.getAuditDateTime();
			if (auditDateTime != null) {
				auditDateTime = auditDateTime.replaceAll("-", "");
				auditDateTime = auditDateTime.replaceAll(":", "");
				auditDateTime = auditDateTime.replaceAll(" ", "");
			}
			executionNode.setAuditDateTime(auditDateTime);
			list.add(signature2);
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, list); // 签名
		map.put(2, nodeList);// 审批节点
		return map;
	}

	public String addmaualPlan(ManualOrderPlanDetail mod) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		if (mod != null) {
			mod.setAddTime(Util.getDateTime());// 添加时间
			mod.setAddUsers(user.getName());// 添加人
			mod.setAddUsersCode(user.getCode());// 添加人工号
			mod.setRukuNum(0f);
			mod.setStatus("未采购");
			mod.setType("手动添加");// 添加方式
			mod.setOldNumber(mod.getCgnumber());
			boolean bool = totalDao.save(mod);
			if (bool) {
				String processName = "物料需求申请";
				if ("研发".equals(mod.getCategory())) {
					processName = "项目采购申请";
				} else if ("售后".equals(mod.getCategory())) {
					processName = "售后备件采购申请";
				} else if ("辅料".equals(mod.getCategory())) {
					processName = "辅料需求申请";
				}
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ManualOrderPlanDetail.class, mod.getId(),
							"epStatus", "id", "", user.getDept() + "部门 "
									+ user.getName() + "的采购申请，请您审批", true);
					if (epId != null && epId > 0) {
						mod.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							mod.setEpStatus("同意");
							// addmaualPlan1(mod);
							// if ("预测订单".equals(mod.getType())) {
							// YcWaiGouProcrd ycprocard = (YcWaiGouProcrd)
							// totalDao
							// .getObjectByCondition(
							// " from YcWaiGouProcrd where mopdId =? ",
							// mod.getId());
							// ycprocard.setEpstatus("同意");
							// totalDao.update(ycprocard);
							// }
						} else {
							mod.setEpStatus("未审批");
						}
						return totalDao.update(mod) + "";
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.toString());
				}
			}
		}
		return null;
	}

	public Object[] findmodnList(ManualOrderPlanDetail mod, int pageNo,
			int pageSize, String status, String tag, String flag,
			String startTime, String endTime) {
		if (mod == null) {
			mod = new ManualOrderPlanDetail();
		}
		String hql = totalDao.criteriaQueries(mod, "addTime", new String[] {
				startTime, endTime }, null);

		if ("wsq".equals(status)) {
			hql += " and epId is null";
		}
		if ("fl".equals(flag)) {
			hql += " and category = '辅料' ";
		} else {
			hql += " and (category <> '辅料' or category is null or category = '')";
		}

		List<ManualOrderPlanDetail> modPlanList = null;
		if ("daoru".equals(tag)) {
			modPlanList = totalDao.query(hql
					+ " and type='导入添加' order by id desc");
		} else if ("download".equals(tag)) {
			try {
				boolean allFieldNull = Util.isAllFieldNull(mod);
				if (allFieldNull) {
					if (!"".equals(startTime) || !"".equals(endTime)) {
						if (startTime != null || endTime != null) {
							allFieldNull = false;
						}
					}
				}
				if (allFieldNull) {
					modPlanList = totalDao.findAllByPage(hql
							+ " order by id desc", pageNo, pageSize);
				} else {
					modPlanList = totalDao.query(hql + " order by id desc");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			modPlanList = totalDao.findAllByPage(hql + " order by id desc",
					pageNo, pageSize);
		}
		Float num = 0f;
		Float num1 = 0f;
		Float num2 = 0f;
		for (ManualOrderPlanDetail mod0 : modPlanList) {
			num += mod0.getCgnumber() == null ? 0 : mod0.getCgnumber();
			num1 += mod0.getOutcgNumber() == null ? 0 : mod0.getOutcgNumber();
			num2 += mod0.getRukuNum() == null ? 0 : mod0.getRukuNum();
		}
		int count = totalDao.getCount(hql + " order by id desc");
		return new Object[] { modPlanList, count, num, num1, num2 };
	}

	public String delmod(ManualOrderPlanDetail mod) {
		if (mod != null) {
			ManualOrderPlanDetail oldmod = (ManualOrderPlanDetail) totalDao
					.get(ManualOrderPlanDetail.class, mod.getId());
			if (oldmod != null) {
				if (oldmod.getManualPlan() != null) {
					Integer id = oldmod.getId();
					Float cgnumber = oldmod.getCgnumber();
					Integer procardId = oldmod.getProcardId();
					Integer mopid = oldmod.getManualPlan().getId();
					// 清理需求汇总表
					ManualOrderPlan manualPlan = (ManualOrderPlan) totalDao
							.getObjectById(ManualOrderPlan.class, mopid);
					if (manualPlan != null) {
						// 查询汇总下层还有几个
						Integer detailCount = totalDao
								.getCount(
										"from ManualOrderPlanDetail where manualPlan.id=? and id <>?",
										mopid, id);
						manualPlan.setNumber(manualPlan.getNumber() - cgnumber);
						// 如果数量为0 或者 没有下层明细了
						if (manualPlan.getNumber() <= 0 || detailCount == 0) {
							totalDao.delete(manualPlan);
						} else {
							totalDao.update(manualPlan);
						}
					}
					// 设置生产批次不采购
					if (procardId != null) {
						Procard procard = (Procard) totalDao.getObjectById(
								Procard.class, procardId);
						if (procard != null) {
							procard.setCgNumber(cgnumber);
							totalDao.update(procard);
							if (procard.getRootId() != null) {
								Procard rootProcard = (Procard) totalDao
										.getObjectById(Procard.class, procard
												.getRootId());
								if (rootProcard != null) {
									rootProcard.setWlstatus("待确认");
									totalDao.update(rootProcard);
								}
							}
						}
					}
				}
				Integer ii = oldmod.getEpId();
				if (totalDao.delete(oldmod)) {
					if (ii != null && ii > 0) {
						CircuitRunServerImpl.deleteCircuitRun(ii);
					}
					return "true";
				} else {
					return "删除失败";
				}
			}
		}
		return "数据异常，删除失败！";
	}

	public String updatemod(ManualOrderPlanDetail mod) {
		if (mod != null) {
			ManualOrderPlanDetail oldmod = (ManualOrderPlanDetail) totalDao
					.get(ManualOrderPlanDetail.class, mod.getId());
			Users user = Util.getLoginUser();
			if (user == null) {
				return "请先登录";
			}
			if ("同意".equals(mod.getEpStatus())) {
				return "已同意，不能修改";
			}
			if ("安全库存".equals(oldmod.getType()) && oldmod.getEpId() == null) {
				String processName = "手动下单申请";
				oldmod.setAddUsers(user.getName());
				oldmod.setAddUsersCode(user.getCode());
				Integer epId = null;
				try {
					epId = CircuitRunServerImpl.createProcess(processName,
							ManualOrderPlanDetail.class, mod.getId(),
							"epStatus", "id", "", user.getDept() + "部门 "
									+ user.getName() + "手动下单申请，请您审批", true);
					if (epId != null && epId > 0) {
						oldmod.setEpId(epId);
						CircuitRun circuitRun = (CircuitRun) totalDao.get(
								CircuitRun.class, epId);
						if ("同意".equals(circuitRun.getAllStatus())
								&& "审批完成".equals(circuitRun.getAuditStatus())) {
							oldmod.setEpStatus("同意");
							// addmaualPlan1(oldmod);
						} else {
							oldmod.setEpStatus("未审批");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			oldmod.setCgnumber(mod.getCgnumber());
			oldmod.setRemarks(mod.getRemarks());
			return totalDao.update(oldmod) + "";
		}
		return null;
	}

	@Override
	public ManualOrderPlanDetail findmaualPlanById(Integer id) {
		if (id != null) {
			return (ManualOrderPlanDetail) totalDao.get(
					ManualOrderPlanDetail.class, id);
		}
		return null;
	}

	@Override
	public String daorumaualPlan(File wlxqflie, String category,int planType) {
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
			FileCopyUtils.copy(wlxqflie, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
			StringBuffer strb = new StringBuffer();
			Integer errorCount = 0;// 错误数量
			Integer cfCount = 0;// 重复数量
			Integer successCount = 0;// 成功数量
			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 1) {
				Set<ManualOrderPlanDetail> details = new HashSet<ManualOrderPlanDetail>();
				Float totalMoney = 0f;
				 String planMonths = Util.getDateTime("yyyy-MM月");
				for (i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);
					if (cells.length < 2) {
						continue;
					}
					String markId = cells[1].getContents();// 件号
					if (markId == null || markId.length() == 0) {
						strb.append("第（" + (i + 1) + "）行，件号未填写.<br/>");
						errorCount++;
						continue;
					}
					String proName = cells[2].getContents();// 名称
					String specification = cells[3].getContents();// 规格
					String wgType = cells[4].getContents();// 物料类别
					String kgliao = cells[5].getContents();// 供料属性
					if (kgliao == null || kgliao.length() == 0) {
						if ("辅料".equals(category)) {
							kgliao = "TK";
						} else {
							strb.append("第（" + (i + 1) + "）行，供料属性未填写.<br/>");
							errorCount++;
							continue;
						}
					}
					String banben = cells[6].getContents();// 版本
					String unit = cells[7].getContents();// 单位
					String tuhao = cells[8].getContents();// 图号
					String cgnumberstr = cells[9].getContents();// 采购数量
					String remarks = "";
					if (cells.length >= 11) {
						remarks = cells[10].getContents();// 备注
					}
					String demanddept = "";
					if (cells.length >= 12) {
						demanddept = cells[11].getContents();// 需求部门
						if(demanddept==null||"".equals(demanddept)){
							strb.append("第（" + (i + 1) + "）行，未填写需求部门");
							continue;
						}
					}
					String yongtu = "";
					if (cells.length >= 13) {
						yongtu = cells[12].getContents();// 用途
					}
					if ("辅料".equals(category)) {
						OaAppDetailTemplate oa = (OaAppDetailTemplate) totalDao
								.getObjectByCondition(
										" from OaAppDetailTemplate where wlcode =? and epStatus = '同意'",
										markId);
						if (oa == null) {
							strb.append("第（" + (i + 1) + "）行，未在辅料库找到已审批同意的件号:"
									+ markId + "<br/>");
							continue;
						}
						proName = oa.getDetailAppName();
						specification = oa.getDetailFormat();
						wgType = oa.getDetailChildClass();
						unit = oa.getDetailUnit();
						if (oa.getTuhao() != null && oa.getTuhao().length() > 0) {
							tuhao = oa.getTuhao();
						}
					} else {
						String hql_yclAndWgj = " from YuanclAndWaigj where markId = ? and kgliao = ? ";
						if (banben != null && banben.length() > 0) {
							hql_yclAndWgj += " and banbenhao = '" + banben
									+ "'";
						} else {
							hql_yclAndWgj += " and (banbenStatus is null or banbenStatus <> '历史')";
						}
						YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
								.getObjectByCondition(hql_yclAndWgj, markId,
										kgliao);
						if (yclAndWgj != null) {
							proName = yclAndWgj.getName();
							specification = yclAndWgj.getSpecification();
							wgType = yclAndWgj.getWgType();
							tuhao = yclAndWgj.getTuhao();
							unit = yclAndWgj.getUnit();
						} else {
							strb.append("第（" + (i + 1) + "）行，件号:" + markId
									+ ",供料属性:" + kgliao + ",版本:" + banben
									+ "未在外购件库中找到.<br/>");
							continue;
						}
					}
					ManualOrderPlanDetail mopd = new ManualOrderPlanDetail();
					mopd.setMarkId(markId);
					mopd.setProName(proName);
					mopd.setSpecification(specification);
					mopd.setWgType(wgType);
					mopd.setKgliao(kgliao);
					mopd.setUnit(unit);
					mopd.setTuhao(tuhao);
					mopd.setBanben(banben);
					mopd.setAddUsers(user.getName());
					mopd.setAddUsersCode(user.getCode());
					mopd.setAddTime(Util.getDateTime());
					mopd.setType("导入添加");
					mopd.setCategory(category);
					mopd.setEpStatus("未审批");
					mopd.setDemanddept(demanddept);
					mopd.setYongtu(yongtu);
					Float cgnumber = 0f;
					if (cgnumberstr != null && cgnumberstr.length() > 0) {
						try {
							cgnumber = Float.parseFloat(cgnumberstr);
						} catch (Exception e) {
							errorCount++;
							strb.append("第" + (i + 1) + "行，采购数量有误<br/>");
							continue;
						}
					} else {
						errorCount++;
						strb.append("第" + (i + 1) + "行，请填写采购数量<br/>");
						continue;
					}
					mopd.setCgnumber(cgnumber);
					mopd.setRemarks(remarks);
					String time = Util.getDateTime();
					String hql = " from Price where partNumber =? and kgliao=? and"
							+ " pricePeriodStart<='"
							+ time
							+ "' and (pricePeriodEnd>'"
							+ time
							+ "' or pricePeriodEnd is null or pricePeriodEnd = '') and productCategory = '辅料'";
					String hql_banben = "";
					if (mopd.getBanben() != null
							&& mopd.getBanben().length() > 0) {
						hql += " and banbenhao ='" + mopd.getBanben() + "'";
						hql_banben += " and banben = '" + mopd.getBanben()
								+ "'";
					} else {
						hql += " and (banbenhao is null or banbenhao = '') ";
						hql_banben += " and (banben is null or banben = '' )";
					}
					Price price = (Price) totalDao.getObjectByCondition(hql,
							mopd.getMarkId(), mopd.getKgliao());
					if (price != null) {
						mopd.setHsprice(price.getHsPrice().floatValue());
						mopd.setBhsprice(price.getBhsPrice().floatValue());
						mopd.setTotalMoney(mopd.getHsprice()
								* mopd.getCgnumber());
						totalMoney += mopd.getTotalMoney();
					}
						if(planType>0){
							planMonths =Util.getNextMonth3("yyyy-MM");
						}
					if (mopd.getDemanddept() != null) {
						Float yearSumNum = (Float) totalDao
								.getObjectByCondition(
										"select sum(cgnumber) from ManualOrderPlanDetail where "
												+ " markId =? and months like '"
												+ planMonths.substring(0,4)
												+ "%' and demanddept =? and epStatus = '同意'",
										mopd.getMarkId(), mopd.getDemanddept());
						mopd.setYearSumNum(yearSumNum);
					}
					mopd.setMonths(planMonths+"月");
					mopd.setOldNumber(mopd.getCgnumber());
					if (totalDao.save(mopd)) {
						details.add(mopd);
						successCount++;
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个<br/>n失败" + errorCount
						+ "个<br/>重复" + cfCount + "个<br/>失败的行数分别为：<br/>"
						+ strb.toString();
				if (successCount > 0) {
					String totalNum = "";
					ManualOrderPlanTotal mot = new ManualOrderPlanTotal();
					String before = Util.getDateTime("yyyyMM");
					String maxnumber = (String) totalDao
							.getObjectByCondition("select max(totalNum) from ManualOrderPlanTotal where totalNum like '"
									+ before + "%'");
					DecimalFormat df = new DecimalFormat("0000");
					if (maxnumber != null && !"".equals(maxnumber)) {
						// String number1 = paymentVoucher2.getNumber();
						String now_number = maxnumber.split(before)[1];
						Integer number2 = Integer.parseInt(now_number) + 1;
						String number3 = df.format(number2);
						totalNum = before + number3;
					} else {
						totalNum = before + "0001";
					}
					mot.setTotalNum(totalNum);
					mot.setUserId(user.getId());
					mot.setUserName(user.getName());
					mot.setUserCode(user.getCode());
					mot.setUserDept(user.getDept());
					mot.setAddTime(Util.getDateTime());
					mot.setCategory(category);
					mot.setTotalMoney(totalMoney);
					mot.setDetails(details);
					mot.setMonths(planMonths);
					totalDao.save(mot);

					String valueCode = (String) totalDao
							.getObjectByCondition("select valueCode from CodeTranslation"
									+ " where type ='sys' and keyCode = '物料需求单添加分级审批'");
					String auditLe = "";
					if (valueCode != null && valueCode.equals("是")) {
						auditLe = "一级";
						Float allMoney = mot.getEstimatePrice();
						if (allMoney != null && allMoney > 0) {
							if (allMoney <= 500F) {
								auditLe = "一级";
							} else if (allMoney <= 1000F) {
								auditLe = "二级";
							} else if (allMoney <= 10000F) {
								auditLe = "三级";
							} else {
								auditLe = "四级";
							}
						}
					}
					String processName = auditLe + "物料需求单申请";
					// if ("研发".equals(category) {
					// processName = "项目采购申请";
					// processName = "项目需求单申请";
					// } else
					// if ("售后".equals(category) {
					// processName = "售后备件采购申请";
					// } else
					if ("辅料".equals(category)) {
						processName = auditLe + "辅料采购申请";
					}
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								ManualOrderPlanTotal.class, mot.getId(),
								"epStatus", "id",
								"ManualOrderPlanAction_manualPlanTotalPrint.action?id="
										+ mot.getId(), user.getDept() + "部门 "
										+ user.getName() + "的采购申请单，请您审批", true);
						if (epId != null && epId > 0) {
							mot.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								mot.setEpStatus("同意");
							} else {
								mot.setEpStatus("未审批");
							}
							return totalDao.update(mot) + "";
						}
					} catch (Exception e) {
						 msg =e.toString();
						e.printStackTrace();
					}
				}
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = e.toString();
			e.printStackTrace();
		}
		if(!"true".equals(msg)){
			throw new RuntimeException(msg);
		}
		return msg;
	}

	@Override
	public String plshehe(int[] ids) {
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				ManualOrderPlanDetail mopd = (ManualOrderPlanDetail) totalDao
						.get(ManualOrderPlanDetail.class, ids[i]);
				mopd.setEpStatus("同意");
				if (!addmaualPlan1(mopd)) {
					return "审核失败!";
				}
			}
			return "true";
		} else {
			return "请至少选择一个。";
		}
	}

	@Override
	public Object[] chaxuanmopList(ManualOrderPlan mop, int pageNo,
			int pageSize, String status, String tag, String flag, String daochu) {
		if (mop == null) {
			mop = new ManualOrderPlan();
		}
		boolean b = true;
		if ("daochu".equals(daochu)) {
			b = false;
		}
		String hql = totalDao.criteriaQueries(mop, "", "epStatus", "gysName",
				"addUsername", "startTime", "endTime", "status");

		if ("未入库".equals(mop.getEpStatus())) {
			hql += " and  status in ('未采购','已采购') ";
		}
		if (mop.getEpStatus() != null && mop.getEpStatus().length() > 0) {
			hql += " and  status = '" + mop.getEpStatus() + "'";
		}
		if ("fl".equals(flag)) {
			hql += " and category = '辅料' ";
		} else {
			hql += " and (category <> '辅料' or category is null or category = '')";
		}
		if (!b) {
			hql += " and  status not  in ('取消','已入库') ";
		}
		// 供应商名称
		if (mop.getGysName() != null && mop.getGysName().length() > 0) {
			hql += " and id  in (select mopId from WaigouPlan where  gysName like '%"
					+ mop.getGysName() + "%')";
		}

		// 采购员
		if (mop.getAddUsername() != null && mop.getAddUsername().length() > 0) {
			hql += " and id in (select mopId from WaigouPlan where  waigouOrder.id in (select id from WaigouOrder where  addUserName like'%"
					+ mop.getAddUsername() + "%')  )";
		}

		// 日期
		String date = "";
		String epStatus = mop.getEpStatus();
		String gysName = mop.getGysName();
		String addUsername = mop.getAddUsername();
		String startTime = "";
		String endTime = "";

		if (mop.getStartTime() != null && mop.getStartTime().length() > 0
				&& mop.getEndTime() != null && mop.getEndTime().length() > 0) {
			mop.setEpStatus(null);
			mop.setGysName(null);
			mop.setAddUsername(null);

			startTime = mop.getStartTime();
			endTime = mop.getEndTime();
			mop.setStartTime(null);
			mop.setEndTime(null);

			date = totalDao.criteriaQueries(mop, "addTime", new String[] {
					startTime, endTime }, "");
		}
		if (date.length() > 0) {
			int index = date.lastIndexOf("1=1");
			date = date.substring(index + 3);
		}
		hql += date;
		mop.setEpStatus(epStatus);
		mop.setGysName(gysName);
		mop.setAddUsername(addUsername);
		mop.setStartTime(startTime);
		mop.setEndTime(endTime);

		List<ManualOrderPlan> modPlanList = null;
		modPlanList = totalDao.findAllByPage(hql + " order by id desc", pageNo,
				pageSize);
		if (modPlanList != null && modPlanList.size() > 0 && b) {
			for (ManualOrderPlan mop0 : modPlanList) {
				if (b) {// 导出不用计算明细
					String hql_mod = " from ManualOrderPlanDetail where manualPlan.id = ?";
					List<ManualOrderPlanDetail> modList = totalDao.query(
							hql_mod, mop0.getId());
					mop0.setModLst(modList);
				}
				// 查找供应商名称并赋值 (去重) //查找采购员并赋值 (去重)
				String gysName1 = "";
				String addUsername1 = "";
				List<WaigouPlan> wgPlanList = totalDao.query(
						" from WaigouPlan where mopId = ?", mop0.getId());
				if (wgPlanList != null && wgPlanList.size() > 0 && !b) {// 如果是导出方法，使用for循环遍历得到和值
					for (WaigouPlan waigouPlan : wgPlanList) {
						if (waigouPlan == null)
							continue;
						gysName1 += waigouPlan.getGysName() + ",";
						if (waigouPlan.getWaigouOrder() != null) {
							addUsername1 += waigouPlan.getWaigouOrder()
									.getAddUserName()
									+ ",";
						}
					}

					mop0.setGysName(gysName1);
					mop0.setAddUsername(addUsername1);
				}
				mop0.setWgPlanList(wgPlanList);
				if (b) {// 非导出
					Set setGys = new HashSet();
					Set setCgy = new HashSet();
					if (wgPlanList != null && wgPlanList.size() > 0) {
						for (int i = 0; i < wgPlanList.size(); i++) {
							setGys.add(wgPlanList.get(i).getGysName());
							if (wgPlanList.get(i).getWaigouOrder() != null) {
								setCgy.add(wgPlanList.get(i).getWaigouOrder()
										.getAddUserName());
							}
						}

						for (Object obj : setGys) {
							String gys = obj.toString();
							gysName1 += "<span style='float:left;'>" + gys
									+ "</span><br/>";
						}
						for (Object obj : setCgy) {
							String cgy = obj.toString();
							addUsername1 += "<span style='float:left;'>" + cgy
									+ "</span><br/>";
						}
						mop0.setGysName(gysName1);
						mop0.setAddUsername(addUsername1);
					}
				}
			}
		}
		int count = totalDao.getCount(hql + " order by id desc");
		return new Object[] { modPlanList, count };
	}

	@Override
	public void exportExcel(ManualOrderPlan mop, int pageNo, int pageSize,
			String status, String tag, String flag) {
		Object[] obj = chaxuanmopList(mop, pageNo, pageSize, status, tag, flag,
				"daochu");
		List<ManualOrderPlan> mopList = (List<ManualOrderPlan>) obj[0];
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("物料需求汇总信息".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);

			WritableSheet ws = wwb.createSheet("物料需求汇总信息", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(18, 25);
			// 序号
			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "件号"));
			ws.addCell(new Label(2, 0, "零件名称"));
			ws.addCell(new Label(3, 0, "规格"));
			ws.addCell(new Label(4, 0, "单位"));
			ws.addCell(new Label(5, 0, "供料属性"));
			ws.addCell(new Label(6, 0, "版本号"));
			ws.addCell(new Label(7, 0, "状态"));
			ws.addCell(new Label(8, 0, "添加时间"));
			ws.addCell(new Label(9, 0, "需求时间"));
			ws.addCell(new Label(10, 0, "需求量"));
			ws.addCell(new Label(11, 0, "已采购量"));
			ws.addCell(new Label(12, 0, "未送货数量"));
			ws.addCell(new Label(13, 0, "签收数量"));
			ws.addCell(new Label(14, 0, "合格数量"));
			ws.addCell(new Label(15, 0, "到货数量"));
			// ws.addCell(new Label(16, 0, "序号"));
			// ws.addCell(new Label(17, 0, "采购单号"));
			// ws.addCell(new Label(18, 0, "采购员"));
			// ws.addCell(new Label(19, 0, "供应商"));
			// ws.addCell(new Label(20, 0, "下单数量"));
			// ws.addCell(new Label(21, 0, "签收数量"));
			// ws.addCell(new Label(22, 0, "合格数量"));
			// ws.addCell(new Label(23, 0, "未送货数量"));

			// ws.addCell(new jxl.write.Number(22, count, hascount));
			int count = 1;
			for (int i = 0; i < mopList.size(); i++) {
				ManualOrderPlan mop1 = (ManualOrderPlan) mopList.get(i);
				// List<WaigouPlan> wgplanList = mop1.getWgPlanList();
				// if (wgplanList != null && wgplanList.size() > 0) {
				// int fristrow = 0;
				// for (int j = 0; j < wgplanList.size(); j++) {
				// WaigouPlan wgPlan = wgplanList.get(j);
				// WaigouOrder wgorder = wgPlan.getWaigouOrder();
				// if (j == 0) {
				// fristrow = count;
				// }
				// ws.addCell(new Label(0, count, (i + 1) + ""));
				// ws.addCell(new Label(1, count, mop1.getMarkId()));
				// ws.addCell(new Label(2, count, mop1.getProName()));
				// ws
				// .addCell(new Label(3, count, mop1
				// .getSpecification()));
				// ws.addCell(new Label(4, count, mop1.getUnit()));
				// ws.addCell(new Label(5, count, mop1.getKgliao()));
				// ws.addCell(new Label(6, count, mop1.getBanben()));
				// String status1 = "未采购";
				// if (mop1.getOutcgNumber() != null
				// && mop1.getOutcgNumber() > 0) {
				// if (mop1.getRukuNum() != null
				// && mop1.getRukuNum().equals(
				// mop1.getNumber())) {
				// status1 = "已入库";
				// } else {
				// status1 = "已采购";
				// }
				// }
				// ws.addCell(new Label(7, count, status1));
				// ws.addCell(new Label(8, count, mop1.getAddtime()));
				// ws
				// .addCell(new Label(9, count, mop1
				// .getNeedFinalDate()));
				// ws.addCell(new jxl.write.Number(10, count, mop1
				// .getNumber()));
				// ws.addCell(new jxl.write.Number(11, count, mop1
				// .getOutcgNumber() == null ? 0 : mop1
				// .getOutcgNumber()));
				// ws.addCell(new jxl.write.Number(12, count, mop1
				// .getWshCount()));
				// ws.addCell(new jxl.write.Number(13, count, mop1
				// .getQsCount()));
				// ws.addCell(new jxl.write.Number(14, count, mop1
				// .getHgCount()));
				// ws.addCell(new jxl.write.Number(15, count, mop1
				// .getRukuNum() == null ? 0 : mop1.getRukuNum()));
				// // ws.addCell(new Label(16, count, (j + 1) + ""));
				// // ws
				// // .addCell(new Label(17, count,wgorder==null?"": wgorder
				// // .getPlanNumber()));
				// // ws.addCell(new Label(18, count, wgorder==null?"":wgorder
				// // .getAddUserName()));
				// // ws.addCell(new Label(19, count, wgPlan.getGysName()));
				// // ws.addCell(new jxl.write.Number(20, count, wgPlan
				// // .getNumber()));
				// // ws.addCell(new jxl.write.Number(21, count, wgPlan
				// // .getQsNum() == null ? 0 : wgPlan.getQsNum()));
				// // ws.addCell(new jxl.write.Number(22, count, wgPlan
				// // .getHgNumber() == null ? 0 : wgPlan
				// // .getHgNumber()));
				// // ws.addCell(new jxl.write.Number(23, count, wgPlan
				// // .getSyNumber() == null ? 0 : wgPlan
				// // .getSyNumber()));
				// count++;
				// }
				// for (int m = 0; m <= 15; m++) {
				// ws.mergeCells(m, fristrow, m, (count - 1));
				// }
				// } else {

				ws.addCell(new Label(0, count, (i + 1) + ""));
				ws.addCell(new Label(1, count, mop1.getMarkId()));
				ws.addCell(new Label(2, count, mop1.getProName()));
				ws.addCell(new Label(3, count, mop1.getSpecification()));
				ws.addCell(new Label(4, count, mop1.getUnit()));
				ws.addCell(new Label(5, count, mop1.getKgliao()));
				ws.addCell(new Label(6, count, mop1.getBanben()));
				ws.addCell(new Label(7, count, mop1.getStatus()));
				ws.addCell(new Label(8, count, mop1.getAddtime()));
				ws.addCell(new Label(9, count, mop1.getNeedFinalDate()));
				ws.addCell(new jxl.write.Number(10, count, mop1.getNumber()));
				ws.addCell(new jxl.write.Number(11, count, mop1
						.getOutcgNumber() == null ? 0 : mop1.getOutcgNumber()));
				ws.addCell(new jxl.write.Number(12, count,
						mop1.getWshCount() == null ? 0 : mop1.getWshCount()));
				ws.addCell(new jxl.write.Number(13, count,
						mop1.getQsCount() == null ? 0 : mop1.getQsCount()));
				ws.addCell(new jxl.write.Number(14, count,
						mop1.getHgCount() == null ? 0 : mop1.getHgCount()));
				ws.addCell(new jxl.write.Number(15, count,
						mop1.getRukuNum() == null ? 0 : mop1.getRukuNum()));
				count++;
				// }

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
	public void exportExcel0(ManualOrderPlanDetail mod1, int pageNo,
			int pageSize, String status, String tag, String flag) {

		Object[] obj = findmodnList(mod1, pageNo, pageSize, status, tag, flag,
				null, null);
		List<ManualOrderPlanDetail> modList = (List<ManualOrderPlanDetail>) obj[0];
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("物料需求明细信息".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);

			WritableSheet ws = wwb.createSheet("物料需求明细信息", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(19, 25);
			// 序号
			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "订单号(内)"));
			ws.addCell(new Label(2, 0, "总成件号"));
			ws.addCell(new Label(3, 0, "总成批次"));
			ws.addCell(new Label(4, 0, "件号"));
			ws.addCell(new Label(5, 0, "零件名称"));
			ws.addCell(new Label(6, 0, "需求数量"));
			ws.addCell(new Label(7, 0, "采购数量"));
			ws.addCell(new Label(8, 0, "入库数量"));
			ws.addCell(new Label(9, 0, "规格"));
			ws.addCell(new Label(10, 0, "单位"));
			ws.addCell(new Label(11, 0, "供料属性"));
			ws.addCell(new Label(12, 0, "版本号"));
			ws.addCell(new Label(13, 0, "是否紧急"));
			ws.addCell(new Label(14, 0, "添加方式"));
			ws.addCell(new Label(15, 0, "添加时间"));
			ws.addCell(new Label(16, 0, "到货日期"));
			ws.addCell(new Label(17, 0, "添加人"));
			ws.addCell(new Label(18, 0, "添加人工号"));
			ws.addCell(new Label(19, 0, "审批动态"));
			ws.addCell(new Label(20, 0, "状态"));
			for (int i = 0; i < modList.size(); i++) {
				ManualOrderPlanDetail mod = modList.get(i);
				ws.addCell(new Label(0, i + 1, (i + 1) + ""));
				ws.addCell(new Label(1, i + 1, mod.getOrderNumber()));
				ws.addCell(new Label(2, i + 1, mod.getRootMarkId()));
				ws.addCell(new Label(3, i + 1, mod.getRootSelfCard()));
				ws.addCell(new Label(4, i + 1, mod.getMarkId()));
				ws.addCell(new Label(5, i + 1, mod.getProName()));
				ws.addCell(new jxl.write.Number(6, i + 1,
						mod.getCgnumber() == null ? 0 : mod.getCgnumber()));
				ws
						.addCell(new jxl.write.Number(7, i + 1, mod
								.getOutcgNumber() == null ? 0 : mod
								.getOutcgNumber()));
				ws.addCell(new jxl.write.Number(8, i + 1,
						mod.getRukuNum() == null ? 0 : mod.getRukuNum()));
				ws.addCell(new Label(9, i + 1, mod.getSpecification()));
				ws.addCell(new Label(10, i + 1, mod.getUnit()));
				ws.addCell(new Label(11, i + 1, mod.getKgliao()));
				ws.addCell(new Label(12, i + 1, mod.getBanben()));
				ws.addCell(new Label(13, i + 1, mod.getIsurgent()));
				ws.addCell(new Label(14, i + 1, mod.getType()));
				ws.addCell(new Label(15, i + 1, mod.getAddTime()));
				ws.addCell(new Label(16, i + 1, mod.getNeedFinalDate()));
				ws.addCell(new Label(17, i + 1, mod.getAddUsers()));
				ws.addCell(new Label(18, i + 1, mod.getAddUsersCode()));
				ws.addCell(new Label(19, i + 1, mod.getEpStatus()));
				ws.addCell(new Label(20, i + 1, mod.getStatus()));
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
	public String saveLotPlan(File wlxqflie, String category) {
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
			FileCopyUtils.copy(wlxqflie, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
			StringBuffer strb = new StringBuffer();
			Integer errorCount = 0;// 错误数量
			Integer cfCount = 0;// 重复数量
			Integer successCount = 0;// 成功数量
			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 1) {
				for (i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);
					if (cells.length < 2) {
						continue;
					}
					String markId = cells[1].getContents();// 件号
					if (markId == null || markId.length() == 0) {
						strb.append("第（" + (i + 1) + "）行，件号未填写.<br/>");
						errorCount++;
						continue;
					} else {
						String checkmarkId_hql = "from OaAppDetailTemplate where wlcode = ? and epStatus = ?";
						OaAppDetailTemplate temp = (OaAppDetailTemplate) totalDao
								.getObjectByCondition(checkmarkId_hql, markId,
										"同意");
						if (temp == null) {
							strb.append("第（" + (i + 1)
									+ "）行，件号不是被确认的，未申请或者审批没通过.<br/>");
							errorCount++;
							continue;
						}
					}
					String proName = cells[2].getContents();// 名称
					String specification = cells[3].getContents();// 规格
					String wgType = cells[4].getContents();// 物料类别
					String kgliao = cells[5].getContents();// 供料属性
					// if (kgliao == null || kgliao.length() == 0) {
					// strb.append("第（" + (i + 1) + "）行，供料属性未填写.<br/>");
					// errorCount++;
					// continue;
					// }
					String banben = cells[6].getContents();// 版本
					String unit = cells[7].getContents();// 单位
					String tuhao = cells[8].getContents();// 图号
					String cgnumberstr = cells[9].getContents();// 采购数量
					String remarks = "";
					if (cells.length == 11) {
						remarks = cells[10].getContents();// 备注
					}
					String hql_yclAndWgj = " from YuanclAndWaigj where markId = ? and kgliao = ? ";
					if (banben != null && banben.length() > 0) {
						hql_yclAndWgj += " and banbenhao = '" + banben + "'";
					} else {
						hql_yclAndWgj += " and (banbenStatus is null or banbenStatus <> '历史')";
					}
					YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
							.getObjectByCondition(hql_yclAndWgj, markId, kgliao);
					if (yclAndWgj != null) {
						proName = yclAndWgj.getName();
						specification = yclAndWgj.getSpecification();
						wgType = yclAndWgj.getWgType();
						tuhao = yclAndWgj.getTuhao();
						unit = yclAndWgj.getUnit();
						kgliao = yclAndWgj.getKgliao();
					}
					ManualOrderPlanDetail mopd = new ManualOrderPlanDetail();
					mopd.setMarkId(markId);
					mopd.setProName(proName);
					mopd.setSpecification(specification);
					mopd.setWgType(wgType);
					mopd.setKgliao(kgliao);
					mopd.setUnit(unit);
					mopd.setTuhao(tuhao);
					mopd.setBanben(banben);
					mopd.setAddUsers(user.getName());
					mopd.setAddUsersCode(user.getCode());
					mopd.setAddTime(Util.getDateTime());
					mopd.setType("手动添加");// 添加方式
					mopd.setCategory(category); // 申请类别
					mopd.setEpStatus("未审批");
					Float cgnumber = 0f;
					if (cgnumberstr != null && cgnumberstr.length() > 0) {
						try {
							cgnumber = Float.parseFloat(cgnumberstr);
						} catch (Exception e) {
							errorCount++;
							strb.append("第" + (i + 1) + "行，采购数量有误<br/>");
							continue;
						}
					} else {
						errorCount++;
						strb.append("第" + (i + 1) + "行，请填写采购数量<br/>");
						continue;
					}
					mopd.setCgnumber(cgnumber);
					mopd.setRemarks(remarks);
					//
					// @: TODO 这里判断有没有和库存相同的数据
					if (totalDao.save(mopd)) {
						successCount++;
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个<br/>n失败" + errorCount
						+ "个<br/>重复" + cfCount + "个<br/>失败的行数分别为：<br/>"
						+ strb.toString();
				if (successCount > 0) {// 发消息给需审批人审批
					String hql_dept = "select B.dept from CircuitCustomize A join A.deptset B where A.name = '物料需求申请' and B.dept = '"
							+ user.getDept() + "' ";
					String deptName = (String) totalDao
							.getObjectByCondition(hql_dept);
					String hql_AuditNode = " from AuditNode where circuitCustomize.id in (select id from CircuitCustomize where name = '物料需求申请' )";
					if (deptName != null && deptName.length() > 0) {
						hql_AuditNode = "from AuditNode where circuitCustomize.id=in(select id from CircuitCustomize where name = '物料需求申请' ) and id in ( select a.id from AuditNode a join a.deptSet b where b.dept ="
								+ deptName + ") order by auditLevel";
					}
					AuditNode auditNode = (AuditNode) totalDao
							.getObjectByCondition(hql_AuditNode);
					if (auditNode != null) {
						AlertMessagesServerImpl.addAlertMessages("物料需求信息导入审批",
								user.getName() + "物料需求信息导入" + successCount
										+ "条，请您审批.", new Integer[] { auditNode
										.getAuditUserId() },
								"ManualOrderPlanAction_findmanualPlanList.action?status=wsq&mod1.addUsersCode="
										+ user.getCode() + "&tag=daoru", true);
					}

				}
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "error";
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public ManualOrderPlanDetail getManualOrderPlanDetail(String markId) {
		String hql = "from ManualOrderPlanDetail where markId = ?";
		return (ManualOrderPlanDetail) totalDao.getObjectByCondition(hql,
				markId);
	}

	// 解析物料需求文件
	@Override
	public Object[] analysisFromFile(File wlxqflie) {
		List<ManualOrderPlanDetail> list = new ArrayList<ManualOrderPlanDetail>();
		Users user = Util.getLoginUser();

		if (user == null) {
			return new Object[] { null, "请先登录" };
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
			FileCopyUtils.copy(wlxqflie, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
			StringBuffer strb = new StringBuffer();
			Integer errorCount = 0;// 错误数量
			Integer cfCount = 0;// 重复数量
			Integer successCount = 0;// 成功数量
			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 1) {
				for (i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);
					if (cells.length < 2) {
						continue;
					}
					String markId = cells[1].getContents();// 件号
					if (markId == null || markId.length() == 0) {
						strb.append("第（" + (i + 1) + "）行，件号未填写.\n");
						errorCount++;
						continue;
					}
					String proName = cells[2].getContents();// 名称
					String specification = cells[3].getContents();// 规格
					String wgType = cells[4].getContents();// 物料类别
					String kgliao = cells[5].getContents();// 供料属性
					if (kgliao == null || kgliao.length() == 0) {
						strb.append("第（" + (i + 1) + "）行，供料属性未填写.\n");
						errorCount++;
						continue;
					}
					String banben = null;// 版本
					if (cells.length > 6) {
						banben = cells[6].getContents();
					}
					String unit = null;// 单位
					if (cells.length > 7) {
						unit = cells[7].getContents();
					}
					String tuhao = null;// 图号
					if (cells.length > 8) {
						tuhao = cells[8].getContents();
					}
					String proNumber = null;// 项目编号
					if (cells.length > 9) {
						proNumber = cells[9].getContents();
					}
					String cgnumberstr = null;// 采购数量
					if (cells.length > 10) {
						cgnumberstr = cells[10].getContents();
					}
					String needFinalDate = null;
					if (cells.length > 11) {
						needFinalDate = cells[11].getContents();
					}
					String remarks = "";
					if (cells.length > 12) {
						remarks = cells[12].getContents();// 备注
					}
					String hql_yclAndWgj = " from YuanclAndWaigj where markId = ? and kgliao = ?";
					if (banben != null && banben.length() > 0) {
						hql_yclAndWgj += " and banbenhao = '" + banben + "'";
					} else {
						hql_yclAndWgj += " and (banbenStatus is null or banbenStatus <> '历史')";
					}
					YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
							.getObjectByCondition(hql_yclAndWgj, markId, kgliao);
					if (yclAndWgj != null) {
						proName = yclAndWgj.getName();
						specification = yclAndWgj.getSpecification();
						wgType = yclAndWgj.getWgType();
						tuhao = yclAndWgj.getTuhao();
						unit = yclAndWgj.getUnit();
					} else {
						strb.append("第（" + (i + 1) + "）行，件号:" + markId
								+ ",供料属性:" + kgliao + ",版本:" + banben
								+ "未在外购件库中找到.\n");
						continue;
					}
					ManualOrderPlanDetail mopd = new ManualOrderPlanDetail();
					mopd.setMarkId(markId);
					mopd.setProName(proName);
					mopd.setSpecification(specification);
					if (cgnumberstr != null && cgnumberstr != "") {
						mopd.setCgnumber(Float.parseFloat(cgnumberstr));// 数量
					}

					mopd.setWgType(wgType);
					mopd.setKgliao(kgliao);
					mopd.setUnit(unit);
					mopd.setTuhao(tuhao);
					mopd.setProNumber(proNumber);
					mopd.setBanben(banben);
					mopd.setAddUsers(user.getName());
					mopd.setAddUsersCode(user.getCode());
					mopd.setAddTime(Util.getDateTime());
					mopd.setCategory("外购件");
					mopd.setNeedFinalDate(needFinalDate);
					mopd.setRemarks(remarks);
					if (list.add(mopd)) {
						successCount++;
					}
				}
				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个\n失败" + errorCount
						+ "个\n失败的行数分别为：\n" + strb.toString();
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "error";
			e.printStackTrace();
		}

		return new Object[] { list, msg };
	}

	@Override
	public Object[] analysisflFromFile(File wlxqflie) {
		List<ManualOrderPlanDetail> list = new ArrayList<ManualOrderPlanDetail>();
		Users user = Util.getLoginUser();
		if (user == null) {
			return new Object[] { "请先登录", null };
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
			FileCopyUtils.copy(wlxqflie, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
			StringBuffer strb = new StringBuffer();
			Integer errorCount = 0;// 错误数量
			Integer cfCount = 0;// 重复数量
			Integer successCount = 0;// 成功数量
			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 1) {
				for (i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);
					if (cells.length < 2) {
						continue;
					}
					String markId = cells[1].getContents();// 件号
					if (markId == null || markId.length() == 0) {
						strb.append("第（" + (i + 1) + "）行，件号未填写.\n");
						errorCount++;
						continue;
					} else {
						String checkmarkId_hql = "from OaAppDetailTemplate where wlcode = ? and epStatus = ?";
						OaAppDetailTemplate temp = (OaAppDetailTemplate) totalDao
								.getObjectByCondition(checkmarkId_hql, markId,
										"同意");
						if (temp == null) {
							strb.append("第（" + (i + 1)
									+ "）行，件号不是被确认的，未申请或者审批没通过.<br/>");
							errorCount++;
							continue;
						}
					}
					String proName = null;// 名称
					if (cells.length >= 3 && cells[2].getContents() != null
							&& !"".equals(cells[2].getContents())) {
						proName = cells[2].getContents();
					}
					String specification = null;// 规格
					if (cells.length >= 4 && cells[3].getContents() != null
							&& !"".equals(cells[3].getContents())) {
						specification = cells[3].getContents();
					}
					String wgType = null;// 物料类别
					if (cells.length >= 5 && cells[4].getContents() != null
							&& !"".equals(cells[4].getContents())) {
						wgType = cells[4].getContents();
					}
					String kgliao = null;// 供料属性
					if (cells.length >= 6 && cells[5].getContents() != null
							&& !"".equals(cells[5].getContents())) {
						kgliao = cells[5].getContents();
					}
					String banben = null;// 版本
					if (cells.length >= 7 && cells[6].getContents() != null
							&& !"".equals(cells[6].getContents())) {
						banben = cells[6].getContents();
					}
					String unit = null;// 单位
					if (cells.length >= 8 && cells[7].getContents() != null
							&& !"".equals(cells[7].getContents())) {
						unit = cells[7].getContents();
					}
					String tuhao = null;// 图号
					if (cells.length >= 9 && cells[8].getContents() != null
							&& !"".equals(cells[8].getContents())) {
						tuhao = cells[8].getContents();
					}
					// String projectCode = null;//项目编号
					//					
					String cgnumberstr = null;// 采购数量
					if (cells.length >= 11 && cells[10].getContents() != null
							&& !"".equals(cells[10].getContents())) {
						cgnumberstr = cells[10].getContents();
					}
					String remarks = "";
					if (cells.length >= 12 && cells[11].getContents() != null
							&& !"".equals(cells[11].getContents())) {
						remarks = cells[11].getContents();// 备注
					}
					// String hql_yclAndWgj =
					// " from YuanclAndWaigj where markId = ? and kgliao = ? ";
					// if (banben != null && banben.length() > 0) {
					// hql_yclAndWgj += " and banbenhao = '" + banben + "'";
					// } else {
					// hql_yclAndWgj +=
					// " and (banbenStatus is null or banbenStatus <> '历史')";
					// }
					String hql_oa = "from OaAppDetailTemplate where wlcode=? ";
					OaAppDetailTemplate template = (OaAppDetailTemplate) totalDao
							.getObjectByCondition(hql_oa, markId);
					if (template != null) {
						proName = template.getDetailAppName();
						specification = template.getDetailFormat();
						wgType = template.getDetailChildClass();
						unit = template.getDetailUnit();
					}

					// YuanclAndWaigj yclAndWgj = (YuanclAndWaigj) totalDao
					// .getObjectByCondition(hql_yclAndWgj, markId, kgliao);
					// if (yclAndWgj != null) {
					// proName = yclAndWgj.getName();
					// specification = yclAndWgj.getSpecification();
					// wgType = yclAndWgj.getWgType();
					// tuhao = yclAndWgj.getTuhao();
					// unit = yclAndWgj.getUnit();
					// }
					ManualOrderPlanDetail mopd = new ManualOrderPlanDetail();
					mopd.setMarkId(markId);
					mopd.setProName(proName);
					mopd.setSpecification(specification);
					mopd.setWgType(wgType);
					mopd.setKgliao(kgliao);
					mopd.setUnit(unit);
					mopd.setTuhao(tuhao);
					mopd.setBanben(banben);
					mopd.setAddUsers(user.getName());
					mopd.setAddUsersCode(user.getCode());
					mopd.setAddTime(Util.getDateTime());
					mopd.setType("手动添加");// 添加方式
					mopd.setCategory("辅料"); // 申请类别
					mopd.setEpStatus("未审批");
					Float cgnumber = 0f;
					try {
						if (cgnumber != null) {
							cgnumber = Float.parseFloat(cgnumberstr);
						}
					} catch (Exception e) {
						errorCount++;
						strb.append("第" + i + "行，采购数量有误,\n");
						continue;
					}
					mopd.setCgnumber(cgnumber);
					mopd.setRemarks(remarks);
					list.add(mopd);
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个\n失败" + errorCount + "个\n重复"
						+ cfCount + "个\n失败的行数分别为：\n" + strb.toString();

			} else {
				msg = "没有获取到行数";
			}
			return new Object[] { list, msg };
		} catch (Exception e) {
			msg = "error";
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String quexiao(Integer id) {
		if (id != null) {
			ManualOrderPlan mop = (ManualOrderPlan) totalDao.get(
					ManualOrderPlan.class, id);
			mop.setStatus("取消");
			Set<ManualOrderPlanDetail> modSet = mop.getModSet();
			for (ManualOrderPlanDetail mod : modSet) {
				mod.setStatus("取消");
				if (mod.getProcardId() != null) {
					Procard procard = (Procard) totalDao.get(Procard.class, mod
							.getProcardId());
					if (procard != null) {
						procard.setCgNumber(null);
						totalDao.update(procard);
					}
				}
				totalDao.update(mod);
			}
			return totalDao.update(mop) + "";
		}
		return null;
	}

	@Override
	public ManualOrderPlan getOrderPlanById(Integer id, String tag) {

		return (ManualOrderPlan) totalDao.getObjectById(ManualOrderPlan.class,
				id);
	}

	// 变更物料需求数量
	@Override
	public String changePlanNumber(ManualOrderPlan plan, Float number,
			String tag) {
		if (number <= 0) {
			return "变更数量必须大于0，变更失败。";
		}

		if (plan != null && plan.getId() != null) {
			plan = (ManualOrderPlan) totalDao.getObjectById(
					ManualOrderPlan.class, plan.getId());
		}
		if (plan.getEpStatus() != null
				&& (!"打回".equals(plan.getEpStatus()) && !"同意".equals(plan
						.getEpStatus()))) {
			return "请等待，MOQ数量申请同意或打回！";
		}
		float cgnumber = plan.getNumber();
		float outcgnumber = 0;
		float cancalNumber = 0;
		if (plan.getOutcgNumber() != null && !"".equals(plan.getOutcgNumber())) {
			outcgnumber = plan.getOutcgNumber();
		}
		if (plan.getCancalNum() != null && !"".equals(plan.getCancalNum())
				&& !"同意".equals(plan.getEpCancalStatus())) {
			cancalNumber = plan.getCancalNum();
		}
		if (cgnumber - outcgnumber - cancalNumber - number < 0) {
			return "需求数量小于已采购数量和取消数量的总和，不能变更数量";
		}

		if (number > plan.getNumber()) {
			return "变更失败，变更数量不能大于总数量。";
		}
		if (plan.getEpCancalId() != null) {
			CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
					CircuitRun.class, plan.getEpCancalId());
			if (circuitRun != null) {
				totalDao.delete(circuitRun);
			}
		}

		/***************** MRP计算(库存量&&&&&占用量) *****************/

		String kgsql = "";
		if (plan.getKgliao() != null && plan.getKgliao().length() > 0) {
			kgsql += " and kgliao ='" + plan.getKgliao() + "'";
		}
		// goodsClassSql =
		// " and ((goodsClass in ('外购件库','中间库') "
		// + kgsql + " ) or goodsClass = '备货库')";
		String goodsClassSql = " and goodsClass in ('外购件库') " + kgsql;
		String banben_hql = "";
		String banben_hql2 = "";
		if (plan.getBanben() != null && plan.getBanben().length() > 0) {
			banben_hql = " and banBenNumber='" + plan.getBanben() + "'";
			banben_hql2 = " and banben='" + plan.getBanben() + "'";
		}
		String specification_sql = "";
		// if (plan.getSpecification() != null
		// && plan.getSpecification().length() > 0) {
		// specification_sql = " and specification = '"
		// + plan.getSpecification() + "'";
		// }

		// 库存量(件号+版本+供料属性+库别)
		String hqlGoods = "";
		hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
				+ goodsClassSql
				+ " and goodsCurQuantity>0 "
				+ banben_hql
				+ " and (fcStatus is null or fcStatus='可用')";
		Float kcCount = (Float) totalDao.getObjectByCondition(hqlGoods, plan
				.getMarkId());
		if (kcCount == null || kcCount < 0) {
			kcCount = 0f;
		}

		/****************** 占用量=生产占用量+导入占用量 ******************************/
		// 系统占用量(含损耗)(已计算过采购量(1、有库存 2、采购中)，未领料)
		String zyCountSql = "select sum(hascount) from Procard where markId=? and kgliao=? "
				+ banben_hql
				+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and (lingliaostatus='是' or lingliaostatus is null ) "
				+ " and (sbStatus<>'删除' or sbStatus is null ) ";
		Double zyCountD = (Double) totalDao.getObjectByConditionforDouble(zyCountSql,
				plan.getMarkId(), plan.getKgliao());
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
				+ banben_hql2
				+ " and kgliao=? and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null) "
				+ specification_sql + " ";
		Double ztCountd = (Double) totalDao.getObjectByCondition(hql_zc0, plan
				.getMarkId(), plan.getKgliao());
		if (ztCountd == null) {
			ztCountd = 0D;
		}
		Float ztCount = ztCountd.floatValue();

		// // 导入在途量(系统切换时导入在途量)
		// String hqlGoods_zt =
		// "select sum(goodsCurQuantity) from Goods where goodsMarkId=?"
		// + banben_hql
		// +
		// " and kgliao=? and goodsClass in ('在途库') and goodsCurQuantity>0 and (fcStatus is null or fcStatus='可用')";
		// Float kcCount_zt = (Float) totalDao.getObjectByCondition(
		// hqlGoods_zt, procard.getMarkId(), procard
		// .getKgliao());
		// if (kcCount_zt == null || kcCount_zt < 0) {
		// kcCount_zt = 0f;
		// }
		// ztCount += kcCount_zt;
		// if (ztCount < 0) {
		// ztCount = 0F;
		// }
		/****************** 结束 在途量=采购在途量+导入在途量 结束 ******************************/
		// (库存量+在途量(已生成采购，未到货))-占用量=剩余可用库存量
		Float daizhiCount = (kcCount + ztCount) - zyCount;
		if (daizhiCount < 0) {
			daizhiCount = 0F;
		}
		// if(plan.getEpCancalStatus()!=null&&plan.getEpCancalStatus().equals("同意")){
		// 变更
		daizhiCount = daizhiCount - number;
		// }else{
		// //修改
		// float oldCancalNum = plan.getCancalNum();
		//			
		// }
		if (daizhiCount < 0) {
			return "变更失败，原因:库存量(" + kcCount + ")+在途量(" + ztCount + ")-占用量("
					+ zyCount + ")-变更量(" + number + ")=可取消量(" + daizhiCount
					+ ")";
			// if((ztCount-(zyCount-kcCount))>0) {
			// return "变更失败，变更后数量小于需求数量,最大取消数量为："+(ztCount-(zyCount-kcCount));
			// }else {
			// return "现在物料需求数量小于生产需求量,变更失败,最大变更数量为：0";
			// }

		} else {
			// plan.setNumber(number);
			Integer epId = null;
			String ccName = "物料需求变更申请";
			try {
				plan.setEpCancalStatus("未审批");
				// plan.setStatus("取消审批中");
				plan.setCancalNum(number);
				totalDao.update(plan);

				Users loginUser = Util.getLoginUser();
				epId = CircuitRunServerImpl.createProcess(ccName,
						ManualOrderPlan.class, plan.getId(), "epCancalStatus",
						"id",
						"ManualOrderPlanAction_getOrderPlanById.action?id="
								+ plan.getId() + "&tag=search", "部门："
								+ loginUser.getDept() + ",姓名："
								+ loginUser.getName() + "物料需求变更申请，请您审批", true);
				plan.setEpCancalId(epId);

				CircuitRun circuitRun = (CircuitRun) totalDao.getObjectById(
						CircuitRun.class, epId);
				if (circuitRun != null
						&& "同意".equals(circuitRun.getAllStatus())) {
					plan.setEpCancalStatus("同意");
				}
				totalDao.update(plan);
				return "提交成功，请等待审批。";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "变更失败";
	}

	@Override
	public void text() {
		// TODO Auto-generated method stub
		List<ManualOrderPlanDetail> details = totalDao
				.query("from ManualOrderPlanDetail where proNumber is not null and proNumber <> '' and addTime > '2018-04-18'");
		for (ManualOrderPlanDetail manualOrderPlanDetail : details) {
			String str = manualOrderPlanDetail.getProNumber();
			if (manualOrderPlanDetail.getManualPlan() != null
					&& manualOrderPlanDetail.getManualPlan().getId() != null) {
				ManualOrderPlan mop = manualOrderPlanDetail.getManualPlan();
				if (mop != null) {
					List<ManualOrderPlan> mopLi = totalDao.query(
							"from ManualOrderPlan where id = ?", mop.getId());
					if (mopLi != null && mopLi.size() > 0) {
						for (ManualOrderPlan manualOrderPlan : mopLi) {
							manualOrderPlan.setProNumber(str);
							totalDao.update2(manualOrderPlan);
						}
					}
					List<WaigouPlan> plans = totalDao.query(
							"from WaigouPlan where mopId = ?", mop.getId());
					if (plans != null && plans.size() > 0) {
						for (WaigouPlan waigouPlan : plans) {
							waigouPlan.setProNumber(str);
							totalDao.update2(waigouPlan);
							List<WaigouDeliveryDetail> list = totalDao
									.query(
											"from WaigouDeliveryDetail where waigouPlanId = ?",
											waigouPlan.getId());
							if (list != null && list.size() > 0) {
								for (WaigouDeliveryDetail wdd : list) {
									wdd.setProNumber(str);
									totalDao.update2(wdd);
									if (wdd.getExamineLot() != null) {
										List<GoodsStore> goodsStores = totalDao
												.query(
														"from GoodsStore where goodsStoreMarkId = ? and goodsStoreLot = ?",
														wdd.getMarkId(),
														wdd.getExamineLot());
										if (goodsStores != null
												&& goodsStores.size() > 0) {
											for (GoodsStore goodsStore : goodsStores) {
												goodsStore.setProNumber(str);
												totalDao.update2(goodsStore);
											}
										}
										List<Goods> goods = totalDao
												.query(
														"from Goods where goodsMarkId = ? and goodsLotId = ?",
														wdd.getMarkId(),
														wdd.getExamineLot());
										if (goods != null && goods.size() > 0) {
											for (Goods good : goods) {
												good.setProNumber(str);
												totalDao.update2(good);
												List<Sell> sell = totalDao
														.query(
																"from Sell where sellMarkId = ? and sellLot = ?",
																good
																		.getGoodsMarkId(),
																good
																		.getGoodsLotId());
												if (sell != null
														&& sell.size() > 0) {
													for (Sell sell2 : sell) {
														sell2.setProNumber(str);
														totalDao.update2(sell2);
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
		}
	}

	@Override
	public String delAllMod(Integer rootId) {
		Procard procard = (Procard) totalDao.get(Procard.class, rootId);
//		if ("初始".equals(procard.getStatus())) {
			String hql = " from ManualOrderPlanDetail where procardId in (select id from Procard where rootId =?) "
					+ " and (outcgNumber is null or outcgNumber = 0)";
			List<ManualOrderPlanDetail> modList = totalDao.query(hql, rootId);
			if (modList != null && modList.size() > 0) {
				for (ManualOrderPlanDetail mod : modList) {
					ManualOrderPlan mop = mod.getManualPlan();
					mop.setNumber(mop.getNumber() - mod.getCgnumber());
					if (mop.getNumber() <= 0) {
						totalDao.delete(mop);
					}
					totalDao.delete(mod);
				}
			}
			return "删除成功!~";
//		}
//		return "总成状态不为初始。不允许删除!~";
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
}
