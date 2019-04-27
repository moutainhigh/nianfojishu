package com.task.ServerImpl.payment;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.task.Dao.TotalDao;
import com.task.Server.payment.FundApplyServer;
import com.task.ServerImpl.ess.ProcardBlServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.GoodsStore;
import com.task.entity.Password;
import com.task.entity.Users;
import com.task.entity.approval.Signature;
import com.task.entity.caiwu.noncore.NonCorePayable;
import com.task.entity.fin.Escrow;
import com.task.entity.fin.EscrowPayCom;
import com.task.entity.fin.budget.DeptMonthBudget;
import com.task.entity.fin.budget.SubBudgetRate;
import com.task.entity.kvp.ExecuteKVP;
import com.task.entity.opinion.CustomerOpinion;
import com.task.entity.payee.Payee;
import com.task.entity.payment.FundApply;
import com.task.entity.payment.FundApplyDetailed;
import com.task.entity.payment.InvoiceCheck;
import com.task.entity.payment.InvoiceCheckRecording;
import com.task.entity.payment.Invoice_detail;
import com.task.entity.project.QuotedPriceCost;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.ExecutionNode;
import com.task.entity.systemfile.SystemFile;
import com.task.util.MD5;
import com.task.util.Util;
import com.task.util.invoice.MainController;

@SuppressWarnings("unchecked")
public class FundApplyServerImpl implements FundApplyServer {
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public String addFundApply(FundApply fundApply) {
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!";
		}
		if (fundApply != null) {
			String about = fundApply.getAbout();
			if (fundApply.getVoucherbasis().equals("合同")) {
				if (fundApply.getHeTongId() != null) {
					// BarContract bc = (BarContract) totalDao.getObjectById(
					// BarContract.class, fundApply.getHeTongId());
					SystemFile systemFile = (SystemFile) totalDao
							.getObjectById(SystemFile.class, fundApply
									.getHeTongId());
					if (systemFile == null) {
						return "您选择的合同不存在!";
					} else {
						fundApply.setContractnum(systemFile.getFileNo());
						Float yyMoney = systemFile.getYyMoney();
						BigDecimal decimal = new BigDecimal(fundApply
								.getTotalMoney());
						float floatValue = decimal.floatValue();
						if (yyMoney != null) {
							systemFile.setYyMoney(floatValue + yyMoney);
						} else {
							systemFile.setYyMoney(floatValue);
						}
						totalDao.update(systemFile);
					}
				} else {
					return "请先选择合同!";
				}

			}

			//发票号不为空，验证总金额 totalMoney
			if (fundApply.getInvoiceNum() != null
					&& fundApply.getInvoiceNum().length() > 0) {
				String[] fps = fundApply.getInvoiceNum().split(",");
				String hql = "select sum(jshj) from InvoiceCheck where fphm in (''";
				for (String fp : fps) {
					hql += ",'" + fp + "'";
				}
				hql += ")";
				Float summoney=(Float) totalDao.getObjectByCondition(hql);
				if(summoney>0){
					if(fundApply.getTotalMoney().floatValue()>summoney){
						return "本次登记金额不能超过所选发票的的总金额("+summoney+"元)";
					}
				}
			}

			if (fundApply.getRelationclientId() != null) {
				Payee payee = (Payee) totalDao.getObjectById(Payee.class,
						fundApply.getRelationclientId());
				if (payee == null) {
					return "请选择收款单位!";
				}
				fundApply.setRelationclient(payee.getName());
			} else {
				return "请选择收款单位!";
			}
			List<FundApplyDetailed> fadList = fundApply.getFadList();
			Set<FundApplyDetailed> fadSet = new HashSet<FundApplyDetailed>();
			if (fadList != null && fadList.size() > 0) {
				int i = 1;
				for (FundApplyDetailed fundApplyDetailed : fadList) {
					// if("差旅、招待费".equals(fundApplyDetailed.getZjStyle())){
					// String hql = " from KVPAssess where kvp_number = ? ";
					// KVPAssess kvp = (KVPAssess)
					// totalDao.getObjectByCondition(hql,
					// fundApplyDetailed.getZjStyle());
					// if(kvp!=null){
					// fundApplyDetailed.setGoodsStoreMarkId(kvp.getPart_number());//件号
					// fundApplyDetailed.setPartName(kvp.getPart_name());//零件名称
					// fundApplyDetailed.setProcessesNo(kvp.getProcess_no());//工序号
					// fundApplyDetailed.setProcessesName(kvp.getProcess_name());//工序名
					// fundApplyDetailed.setPlanMonth(fundApply.getPlanMonth());
					// fundApplyDetailed.setSaveTime(Util.getDateTime());
					// }
					// }

					if (about.equals("冲账")) {
						FundApply jiankuai = (FundApply) totalDao
								.getObjectById(FundApply.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (jiankuai == null) {
							throw new RuntimeException("第" + i + "行借款不存在,申请失败!");
						}
						if (jiankuai.getBackMoney() == null) {
							jiankuai.setBackMoney(0d);
						}
						if ((jiankuai.getTotalMoney() - jiankuai.getBackMoney()) >= fundApplyDetailed
								.getVoucherMoney()) {
							jiankuai.setBackMoney(jiankuai.getBackMoney()
									+ fundApplyDetailed.getVoucherMoney());
						} else {
							throw new RuntimeException("第" + i + "行,还款超额!");
						}
						if (jiankuai.getTotalMoney().equals(
								jiankuai.getBackMoney())) {
							jiankuai.setStatus("关闭");
						} else {
							jiankuai.setStatus("未付清");
						}
						fundApplyDetailed.setDeptMonthBudgetName("借款单号:"
								+ jiankuai.getNumber());
						totalDao.update(jiankuai);
					} else if (about.equals("预算")) {
						DeptMonthBudget db = (DeptMonthBudget) totalDao
								.getObjectById(DeptMonthBudget.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (db == null) {
							throw new RuntimeException("第" + i + "行预算不存在,申请失败!");
						}
						Double klMoney = 0d;
						if (db.getRealMoney() == null) {
							klMoney = db.getAccountMoney();
						} else {
							klMoney = db.getAccountMoney() - db.getRealMoney();
						}
						if (klMoney < fundApplyDetailed.getVoucherMoney()) {
							throw new RuntimeException(
									"第"
											+ i
											+ "行预算超额"
											+ (fundApplyDetailed
													.getVoucherMoney() - klMoney)
											+ "元,申请失败!");
						} else {
							if (db.getRealMoney() == null) {
								db.setRealMoney(fundApplyDetailed
										.getVoucherMoney());
							} else {
								db.setRealMoney(db.getRealMoney()
										+ fundApplyDetailed.getVoucherMoney());
							}
							totalDao.update(db);
						}
					} else if (about.equals("项目")) {
						QuotedPriceCost qpcost = (QuotedPriceCost) totalDao
								.getObjectById(QuotedPriceCost.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (qpcost == null) {
							throw new RuntimeException("第" + i + "行预算不存在,申请失败!");
						}
						Double klMoney = 0d;
						if (qpcost.getBxMoney() == null) {
							klMoney = qpcost.getMoney();
						} else {
							klMoney = qpcost.getMoney() - qpcost.getBxMoney();
						}
						if (klMoney < fundApplyDetailed.getVoucherMoney()) {
							throw new RuntimeException(
									"第"
											+ i
											+ "行预算超额"
											+ (fundApplyDetailed
													.getVoucherMoney() - klMoney)
											+ "元,申请失败!");
						} else {
							if (qpcost.getBxMoney() == null) {
								qpcost.setBxMoney(fundApplyDetailed
										.getVoucherMoney());
							} else {
								qpcost.setBxMoney(qpcost.getBxMoney()
										+ fundApplyDetailed.getVoucherMoney());
							}
							fundApplyDetailed.setDeptMonthBudgetName(qpcost
									.getMarkId());
							totalDao.update(qpcost);
						}
					} else if (about.equalsIgnoreCase("KVP")) {
						ExecuteKVP kvp = (ExecuteKVP) totalDao.getObjectById(
								ExecuteKVP.class, fundApplyDetailed
										.getDeptMonthBudgetID());
						if (kvp == null) {
							throw new RuntimeException("第" + i + "行预算不存在,申请失败!");
						}
						Double klMoney = 0d;
						if (kvp.getBxMoney() == null) {
							klMoney = kvp.getCostsavings();
						} else {
							klMoney = kvp.getCostsavings() - kvp.getBxMoney();
						}
						if (klMoney < fundApplyDetailed.getVoucherMoney()) {
							throw new RuntimeException(
									"第"
											+ i
											+ "行预算超额"
											+ (fundApplyDetailed
													.getVoucherMoney() - klMoney)
											+ "元,申请失败!");
						} else {
							if (kvp.getBxMoney() == null) {
								kvp.setBxMoney(fundApplyDetailed
										.getVoucherMoney());
							} else {
								kvp.setBxMoney(kvp.getBxMoney()
										+ fundApplyDetailed.getVoucherMoney());
							}
							fundApplyDetailed.setDeptMonthBudgetName(kvp
									.getKvpAssess().getPart_name());
							totalDao.update(kvp);
						}
					} else if (about.equalsIgnoreCase("质量")) {
						CustomerOpinion option = (CustomerOpinion) totalDao
								.getObjectById(CustomerOpinion.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (option == null) {
							throw new RuntimeException("第" + i + "行预算不存在,申请失败!");
						}
						if (option.getApplyMoney() == null) {
							option.setApplyMoney(fundApplyDetailed
									.getVoucherMoney());
						} else {
							option.setApplyMoney(option.getApplyMoney()
									+ fundApplyDetailed.getVoucherMoney());
						}
						fundApplyDetailed.setDeptMonthBudgetName(option
								.getTitle());
						totalDao.update(option);
					} else if (about.equalsIgnoreCase("外购(历史)")) {
						GoodsStore gs = (GoodsStore) totalDao.getObjectById(
								GoodsStore.class, fundApplyDetailed
										.getDeptMonthBudgetID());

						if (gs == null) {
							throw new RuntimeException("对不起,第" + i
									+ "行没有找到对应的入库记录!");
						}
						if (gs.getBaoxiao_status() != null
								&& !gs.getBaoxiao_status().equals("未报完")) {
							throw new RuntimeException("对不起,第" + i
									+ "行该入库记录已经报销过!");
						}
						gs.setStatus("已报完");
						totalDao.update(gs);
						fundApplyDetailed.setProcessesName(gs
								.getGoodsStoreGoodsName());
						fundApplyDetailed.setMarkId(gs.getGoodsStoreMarkId());
						fundApplyDetailed.setZjStyleMx(fundApplyDetailed
								.getZjStyle());
						fundApplyDetailed.setZjStyle(gs.getGoodsStoreLot());
					} else if (about.equalsIgnoreCase("外委")
							|| about.equalsIgnoreCase("外购")) {
						WaigouPlan wgPlan = (WaigouPlan) totalDao
								.getObjectById(WaigouPlan.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (wgPlan == null) {
							throw new RuntimeException("对不起,第" + i + "行没有找到对应的"
									+ about + "记录!");
						}
						// if(wgPlan.getHasjk()!=null&&wgPlan.getHasjk().equals("是")){
						// return "对不起,该"+about+"记录已经报销过!";
						// }
						if (wgPlan.getYfMoney() == null) {
							float f = fundApplyDetailed.getVoucherMoney()
									.floatValue();
							if (f > wgPlan.getMoney()) {
								throw new RuntimeException("对不起,第" + i
										+ "行报销金额超过该订单金额!");
							}
							wgPlan.setYfMoney(fundApplyDetailed
									.getVoucherMoney());
						} else {
							float f = wgPlan.getYfMoney().floatValue()
									+ fundApplyDetailed.getVoucherMoney()
											.floatValue();
							if (f > wgPlan.getMoney()) {
								throw new RuntimeException("对不起,第" + i
										+ "行报销金额超过该订单金额!");
							}
							wgPlan.setYfMoney(wgPlan.getYfMoney()
									+ fundApplyDetailed.getVoucherMoney());
						}
						wgPlan.setHasjk("是");
						totalDao.update(wgPlan);
						fundApplyDetailed.setBudgetDept(wgPlan.getGysName());
						fundApplyDetailed.setMarkId(wgPlan.getMarkId());
					} else if (about.equalsIgnoreCase("非主营业务应付")) {
						// NonCorePayable np = (NonCorePayable) totalDao
						// .getObjectById(NonCorePayable.class,
						// fundApplyDetailed
						// .getDeptMonthBudgetID());
						// if (np == null) {
						// throw new RuntimeException("对不起,第" + i + "行没有找到对应的"
						// + about + "记录!");
						// }
						// Float klMoney = np.getYingfukuanJIne();
						// if (np.getRealfukuanJIne() != null) {
						// klMoney = klMoney - np.getRealfukuanJIne();
						// float f = fundApplyDetailed.getVoucherMoney()
						// .floatValue();
						// if (f > klMoney) {
						// throw new RuntimeException("对不起,第" + i
						// + "行报销金额超过该订单金额!");
						// }
						// np.setRealfukuanJIne((float) (np
						// .getRealfukuanJIne() + fundApplyDetailed
						// .getVoucherMoney()));
						// } else {
						// np.setRealfukuanJIne((float) (np
						// .getRealfukuanJIne()));
						// }
						// totalDao.update(np);
						// if (np.getScpId() != null) {
						// // 更新付款汇总表
						// SupplierCorePayable scp = (SupplierCorePayable)
						// totalDao
						// .getObjectById(SupplierCorePayable.class,
						// np.getScpId());
						// if (scp != null) {
						// scp.setYingfukuanJine(scp.getYingfukuanJine()
						// - np.getRealfukuanJIne());
						// scp.setWeifukuanJine(scp.getWeifukuanJine()
						// - np.getRealfukuanJIne());
						// scp.setRealfukuanJine(scp.getRealfukuanJine()
						// + np.getRealfukuanJIne());
						// if (scp.getFkCount() == null) {
						// scp.setFkCount(0);
						// }
						// scp.setFkCount(scp.getFkCount() + 1);
						// totalDao.update(scp);
						// }
						// }

					} else if (about.equalsIgnoreCase("其他")) {

					}
					// 查找科目信息
					if (fundApplyDetailed.getFk_SubBudgetRateId() != null
							&& fundApplyDetailed.getFk_SubBudgetRateId() > 0) {
						try {
							SubBudgetRate subBudgetRate = (SubBudgetRate) totalDao
									.getObjectById(SubBudgetRate.class,
											fundApplyDetailed
													.getFk_SubBudgetRateId());
							if (subBudgetRate != null) {
								fundApplyDetailed.setZjStyle(subBudgetRate
										.getName());// 科目名称
							} else {
								throw new RuntimeException("对不起,第" + i
										+ "行科目信息错误!");
							}
						} catch (NumberFormatException e) {
							throw new RuntimeException("对不起,第" + i + "行科目信息错误!");
						}
					}

					fadSet.add(fundApplyDetailed);
					i++;
				}
			}
			String createdate1 = "jkbx_" + Util.getDateTime("yyyy");
			String hql = "select max(number) from FundApply";
			String max_numbere = (String) this.totalDao
					.getObjectByCondition(hql);
			if (max_numbere != null && !"".equals(max_numbere)) {
				// String number1 = paymentVoucher2.getNumber();
				String now_number = max_numbere.split("_")[1];
				long number2 = Long.parseLong(now_number) + 1;
				String number3 = Long.toString(number2);
				fundApply.setNumber("jkbx_" + number3);
			} else {
				String number2 = createdate1 + "001";
				fundApply.setNumber(number2);
			}
			fundApply.setUserId(Util.getLoginUser().getId());
			fundApply.setApprovalApplier(Util.getLoginUser().getName());
			fundApply.setAddTime(Util.getDateTime());
			fundApply.setFadSet(fadSet);
			fundApply.setStatus("申请中");
			fundApply.setBackMoney(0D);
			boolean bool = totalDao.save(fundApply);
			// if (true) {
			String processName = null;
			if (fundApply.getZhifuoryufu().equals("预付")) {
				processName = "应付登记预付申请";
			} else {
				processName = "应付登记支付申请";
			}
			if (!"是".equals(fundApply.getJituan())
					&& fundApply.getTotalMoney() >= 10000) {
				processName = "大额" + processName;
			}
			if ("是".equals(fundApply.getJituan())) {
				processName = "集团" + processName;
			}
			Integer epId = null;
			try {
				Double backMoney=getbackmoney(Util.getLoginUser().getId());
				String jkye="";
				String bxorjk="";
				if("冲账".equals(fundApply.getAbout())){
					jkye="(借款提示:本次申请批准后,"+Util.getLoginUser().getName()+"的借款余额为:"+backMoney+"元)";
					bxorjk="本次冲账金额为"+fundApply.getTotalMoney()+"元";
				}else if("预付".equals(fundApply.getZhifuoryufu())){
					jkye="(借款提示:本次申请批准后,"+Util.getLoginUser().getName()+"的借款累计为:"+(backMoney+fundApply.getTotalMoney())+"元)";
					bxorjk="本次借款金额为"+fundApply.getTotalMoney()+"元";
				}else{
					jkye="(借款提示:"+Util.getLoginUser().getName()+"的借款累计为:"+backMoney+"元)";
					bxorjk="本次报销金额为"+fundApply.getTotalMoney()+"元";
				}
				
				
				
				
				epId = CircuitRunServerImpl.createProcess(processName,
						FundApply.class, fundApply.getId(), "epStattus", "id",
						"FundApplyAction_findfundDetailedList.action?id="
								+ fundApply.getId(), fundApply
								.getApprovaldept()
								+ "部门 "
								+ fundApply.getRelationclient()
								+ "的"+processName+","
								+ bxorjk
								+ ",请您审批!"+jkye, true);
				if (epId != null && epId > 0) {
					fundApply.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						fundApply.setEpStattus("同意");
					} else {
						fundApply.setEpStattus("未审批");
					}
					if (totalDao.update(fundApply)) {
						if ("支付".equals(fundApply.getZhifuoryufu())
								&& fundApply.getInvoiceNum() != null
								&& fundApply.getInvoiceNum().length() > 2) {
							try {
								String fapiao = "'"
										+ fundApply.getInvoiceNum().replaceAll(
												",,,", ",").replaceAll(",,",
												",").replaceAll(",", "','")
										+ "'";
								List<InvoiceCheck> checks = totalDao
										.query("from InvoiceCheck where fphm in ("
												+ fapiao + ")");
								for (InvoiceCheck invo : checks) {
									invo.setBaoxiaoStatus("已报销");
									totalDao.update(invo);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						return "true";
					} else {
						return "false";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "审批流程有误，请检查后重新申请！" + e;
			}

			// } else {
			// fundApply.setEpStattus("同意");
			// fundApply.setStatus("财务确认");
			// totalDao.update(fundApply);
			// }
			return "true";

		}
		return "请刷新后重试，谢谢!";
	}

	@Override
	public void bushuju(Integer id) {
		FundApply fundApply = getFundApplyById(id);
		if (fundApply != null) {
			String processName = null;
			if (fundApply.getZhifuoryufu().equals("预付")) {
				processName = "应付登记预付申请";
			} else {
				processName = "应付登记支付申请";
			}
			if (!"是".equals(fundApply.getJituan())
					&& fundApply.getTotalMoney() >= 10000) {
				processName = "大额" + processName;
			}
			if ("是".equals(fundApply.getJituan())) {
				processName = "集团" + processName;
			}
			Integer epId = null;
			try {
				epId = CircuitRunServerImpl.createProcess(processName,
						FundApply.class, fundApply.getId(), "epStattus", "id",
						"FundApplyAction_findfundDetailedList.action?id="
								+ fundApply.getId(), fundApply
								.getApprovaldept()
								+ "部门 "
								+ fundApply.getRelationclient()
								+ "应付登记预付申请,类型:"
								+ fundApply.getZhifuoryufu()
								+ ",请您审批", true, fundApply.getApprovaldept());
				if (epId != null && epId > 0) {
					fundApply.setEpId(epId);
					CircuitRun circuitRun = (CircuitRun) totalDao.get(
							CircuitRun.class, epId);
					if ("同意".equals(circuitRun.getAllStatus())
							&& "审批完成".equals(circuitRun.getAuditStatus())) {
						fundApply.setEpStattus("同意");
					} else {
						fundApply.setEpStattus("未审批");
					}
					totalDao.update(fundApply);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// List<CircuitRun> circuitRun =
		// totalDao.query("from CircuitRun where id in (24172,24190,24202)");
		// for (CircuitRun circuitRun2 : circuitRun) {
		// CircuitRunServerImpl.fundApply(circuitRun2, totalDao);
		// }
	}

	@Override
	public String delFundApply(FundApply fundApply) {
		if (fundApply != null) {
			if (!"同意".equals(fundApply.getEpStattus())
					&& !"审批中".equals(fundApply.getEpStattus())) {
				fundApply = (FundApply) totalDao.get(FundApply.class, fundApply
						.getId());
				Set<FundApplyDetailed> fadSet = fundApply.getFadSet();
				for (FundApplyDetailed fundApplyDetailed : fadSet) {
					// 退还借款金额
					if (fundApply.getAbout().equals("冲账")) {
						FundApply jiankuai = (FundApply) totalDao
								.getObjectById(FundApply.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (jiankuai == null) {
							break;
						}
						if (fundApply.getEpStattus() == null
								|| !fundApply.getEpStattus().equals("打回")) {
							if (jiankuai.getBackMoney() > fundApplyDetailed
									.getVoucherMoney()) {
								jiankuai.setBackMoney(jiankuai.getBackMoney()
										- fundApplyDetailed.getVoucherMoney());
								jiankuai.setStatus("未付清");
							} else {
								jiankuai.setBackMoney(0d);
								jiankuai.setStatus("待还款");
							}
						}
						totalDao.update(jiankuai);
					} else if (fundApply.getAbout().equals("预算")) {
						DeptMonthBudget db = (DeptMonthBudget) totalDao
								.getObjectById(DeptMonthBudget.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (db == null) {
							continue;
						}
						if (fundApply.getEpStattus() == null
								|| !fundApply.getEpStattus().equals("打回")) {
							if (db.getRealMoney() > fundApplyDetailed
									.getVoucherMoney()) {
								db.setRealMoney(db.getRealMoney()
										- fundApplyDetailed.getVoucherMoney());
							} else {
								db.setRealMoney(0d);
							}
							totalDao.update(db);
						}
					} else if (fundApply.getAbout().equals("项目")) {
						QuotedPriceCost qpcost = (QuotedPriceCost) totalDao
								.getObjectById(QuotedPriceCost.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (qpcost == null) {
							continue;
						}
						if (fundApply.getEpStattus() == null
								|| !fundApply.getEpStattus().equals("打回")) {
							if (qpcost.getBxMoney() > fundApplyDetailed
									.getVoucherMoney()) {
								qpcost.setBxMoney(qpcost.getBxMoney()
										- fundApplyDetailed.getVoucherMoney());
							} else {
								qpcost.setBxMoney(0d);
							}
							totalDao.update(qpcost);
						}

					} else if (fundApply.getAbout().equalsIgnoreCase("KVP")) {
						ExecuteKVP kvp = (ExecuteKVP) totalDao.getObjectById(
								ExecuteKVP.class, fundApplyDetailed
										.getDeptMonthBudgetID());
						if (kvp == null) {
							continue;
						}
						if (fundApply.getEpStattus() == null
								|| !fundApply.getEpStattus().equals("打回")) {
							if (kvp.getBxMoney() > fundApplyDetailed
									.getVoucherMoney()) {
								kvp.setBxMoney(kvp.getBxMoney()
										- fundApplyDetailed.getVoucherMoney());
							} else {
								kvp.setBxMoney(0d);
							}
							totalDao.update(kvp);
						}
					} else if (fundApply.getAbout().equalsIgnoreCase("外购(历史)")) {
						GoodsStore gs = (GoodsStore) totalDao.getObjectById(
								GoodsStore.class, fundApplyDetailed
										.getDeptMonthBudgetID());
						gs.setBaoxiao_status("未报完");
						totalDao.update(gs);
					} else if (fundApply.getAbout().equalsIgnoreCase("外委")
							|| fundApply.getAbout().equalsIgnoreCase("外购")) {
						WaigouPlan wgPlan = (WaigouPlan) totalDao
								.getObjectById(WaigouPlan.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (fundApply.getEpStattus() == null
								|| !fundApply.getEpStattus().equals("打回")) {
							wgPlan.setYfMoney(wgPlan.getYfMoney()
									- fundApplyDetailed.getVoucherMoney());
						}
						wgPlan.setHasjk("否");
						totalDao.update(wgPlan);
					} else if (fundApply.getAbout().equalsIgnoreCase("非主营业务应付")) {
						// NonCorePayable np = (NonCorePayable) totalDao
						// .getObjectById(NonCorePayable.class,
						// fundApplyDetailed
						// .getDeptMonthBudgetID());
						// if (np != null) {
						// if (np.getRealfukuanJIne() != null) {
						// if (fundApplyDetailed.getVoucherMoney() > np
						// .getRealfukuanJIne()) {
						// np.setRealfukuanJIne(0f);
						// } else {
						// np
						// .setRealfukuanJIne((float) (np
						// .getRealfukuanJIne() - fundApplyDetailed
						// .getVoucherMoney()));
						// }
						// }
						// totalDao.update(np);
						// }
					} else if (fundApply.getAbout().equalsIgnoreCase("合同")) {
						SystemFile systemFile = (SystemFile) totalDao
								.getObjectById(SystemFile.class, fundApply
										.getHeTongId());
						if (systemFile != null) {
							BigDecimal decimal = new BigDecimal(
									fundApplyDetailed.getVoucherMoney());
							float floatValue = decimal.floatValue();
							systemFile.setYyMoney(systemFile.getYyMoney()
									- floatValue);
							totalDao.update(systemFile);
						}
					}

					totalDao.delete(fundApplyDetailed);
				}
				return totalDao.delete(fundApply) + "";
			} else {
				return "状态为同意或者审批中，不能删除!";
			}
		}
		return "请刷新后重试，谢谢!";
	}

	@Override
	public Object[] findFundApplyList(FundApply fundApply, int pageNo,
			int pageSize, String pagestatus, String firstTime, String endTime) {
		if (fundApply == null) {
			fundApply = new FundApply();
		}
		String hql = totalDao.criteriaQueries(fundApply, "addTime",
				new String[] { firstTime, endTime }, "");
		if ("person".equals(pagestatus)) {
			Users user = Util.getLoginUser();
			hql += " and  userId =" + user.getId();
		}
		if ("personyf".equals(pagestatus)) {
			Users user = Util.getLoginUser();
			hql += " and zhifuoryufu='预付'  and  userId =" + user.getId();
		} else if ("fzy".equals(pagestatus)) {
			hql += " and about='非主营业务应付'";
		} else if ("zy".equals(pagestatus)) {
			hql += " and about='主营应付'";
		}
		hql += " order by id desc";
		List<FundApply> fundApplyList = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { fundApplyList, count };
	}

	@Override
	public String updateFundApply(FundApply fundApply) {
		if (fundApply != null) {
			FundApply oldfundApply = (FundApply) totalDao.get(FundApply.class,
					fundApply.getId());
			if ("预付".equals(oldfundApply.getZhifuoryufu())) {
				oldfundApply.setPayStyle(fundApply.getPayStyle());
				oldfundApply.setBaoxiaoDate(fundApply.getBaoxiaoDate());
				oldfundApply.setAttachmentsCount(fundApply
						.getAttachmentsCount());
				oldfundApply.setIsSelfDept(fundApply.getIsSelfDept());
				oldfundApply.setIsTax(fundApply.getIsTax());
				oldfundApply.setInvoiceNum(fundApply.getInvoiceNum());
				oldfundApply.setExplain(fundApply.getExplain());
				oldfundApply.setZhifuoryufu("支付");
				return totalDao.update(oldfundApply) + "";
			}

		}

		return "请刷新后重试，谢谢!";
	}

	@Override
	public FundApply getFundApplyById(Integer id) {
		if (id != null && id > 0) {
			return (FundApply) totalDao.get(FundApply.class, id);
		}
		return null;
	}

	public List<FundApplyDetailed> findfadListByid(Integer id) {
		if (id != null && id > 0) {
			FundApply fundApply = (FundApply) totalDao.get(FundApply.class, id);
			Set<FundApplyDetailed> fadSet = fundApply.getFadSet();
			List<FundApplyDetailed> fadlist = new ArrayList<FundApplyDetailed>();
			for (FundApplyDetailed fundApplyDetailed : fadSet) {
				fadlist.add(fundApplyDetailed);
			}
			return fadlist;
		}
		return null;
	}

	public FundApply getFundApplyAndDetailById(Integer id) {
		if (id != null && id > 0) {
			FundApply fundApply = (FundApply) totalDao.get(FundApply.class, id);
			if (fundApply != null) {
				Set<FundApplyDetailed> fadSet = fundApply.getFadSet();
				List<FundApplyDetailed> fadlist = new ArrayList<FundApplyDetailed>();
				for (FundApplyDetailed fundApplyDetailed : fadSet) {
					fadlist.add(fundApplyDetailed);
				}
				fundApply.setFadList(fadlist);
				return fundApply;
			}
		}
		return null;
	}

	@Override
	public List getzjStyleMx(String style) {
		if ("设备维修费".equals(style)) {
			String hql = " from Maintenance";
			return totalDao.query(hql);
		} else if ("差旅、招待费".equals(style)) {
			String hql = "select kvp_number from KVPAssess";
			return totalDao.query(hql);
		} else if ("零件采购费".equals(style)) {
			String hql = " from WaigouOrder";
			return totalDao.query(hql);
		}
		return null;
	}

	@Override
	public String agreeApply(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		FundApply fundApply = (FundApply) totalDao.getObjectById(
				FundApply.class, id);
		if (fundApply == null) {
			return "没有找到目标!";
		} else {
			if (!fundApply.getStatus().equals("财务确认")) {
				return "此状态不能同意!";
			}
			fundApply.setCwUserCode(user.getCode());
			fundApply.setCwUserName(user.getName());
			fundApply.setSureTime1(Util.getDateTime());
			// if(fundApply.getVoucherNature().equals("冲账")){
			// Set<FundApplyDetailed> fadSet = fundApply.getFadSet();
			// if(fadSet!=null&&fadSet.size()>0){
			// for(FundApplyDetailed fundApplyDetailed:fadSet){
			// FundApply jiankuai = (FundApply)
			// totalDao.getObjectById(FundApply.class,
			// fundApplyDetailed.getDeptMonthBudgetID());
			// if(jiankuai.getBackMoney()==null){
			// jiankuai.setBackMoney(0d);
			// }
			// if((jiankuai.getTotalMoney()-jiankuai.getBackMoney())>=fundApplyDetailed.getVoucherMoney()){
			// jiankuai.setBackMoney(jiankuai.getBackMoney()+fundApplyDetailed.getVoucherMoney());
			// }else{
			// throw new RuntimeException("还款超额!");
			// }
			// if(jiankuai.getTotalMoney().equals(jiankuai.getBackMoney())){
			// jiankuai.setStatus("关闭");
			// }else{
			// jiankuai.setStatus("未付清");
			// }
			// totalDao.update(jiankuai);
			// }
			// }
			// fundApply.setStatus("封闭");
			// }
			// fundApply.setStatus("个人确认");
			if (fundApply.getAbout().equals("支付")) {
				fundApply.setStatus("关闭");
			} else {
				fundApply.setStatus("待还款");
			}
			totalDao.update(fundApply);
			return "true";
		}
	}

	@Override
	public String backApply(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		FundApply fundApply = (FundApply) totalDao.getObjectById(
				FundApply.class, id);
		if (fundApply == null) {
			return "没有找到目标!";
		} else {
			if (!fundApply.getStatus().equals("财务确认")) {
				return "此状态不能打回!";
			}
			String about = fundApply.getAbout();
			Set<FundApplyDetailed> fadSet = fundApply.getFadSet();
			if (fadSet != null && fadSet.size() > 0) {
				for (FundApplyDetailed fundApplyDetailed : fadSet) {
					// 退回金额
					if (about.equals("冲账")) {
						FundApply jiankuai = (FundApply) totalDao
								.getObjectById(FundApply.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (jiankuai == null) {
							break;
						}
						if (jiankuai.getBackMoney() > fundApplyDetailed
								.getVoucherMoney()) {
							jiankuai.setBackMoney(jiankuai.getBackMoney()
									- fundApplyDetailed.getVoucherMoney());
							jiankuai.setStatus("未付清");
						} else {
							jiankuai.setBackMoney(0d);
							jiankuai.setStatus("待还款");
						}
						totalDao.update(jiankuai);
					} else if (about.equals("预算")) {
						DeptMonthBudget db = (DeptMonthBudget) totalDao
								.getObjectById(DeptMonthBudget.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (db == null) {
							break;
						}
						if (db.getRealMoney() > fundApplyDetailed
								.getVoucherMoney()) {
							db.setRealMoney(db.getRealMoney()
									- fundApplyDetailed.getVoucherMoney());
						} else {
							db.setRealMoney(0d);
						}
						totalDao.update(db);
					} else if (about.equals("项目")) {
						QuotedPriceCost qpcost = (QuotedPriceCost) totalDao
								.getObjectById(QuotedPriceCost.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (qpcost == null) {
							break;
						}
						if (qpcost.getBxMoney() > fundApplyDetailed
								.getVoucherMoney()) {
							qpcost.setBxMoney(qpcost.getBxMoney()
									- fundApplyDetailed.getVoucherMoney());
						} else {
							qpcost.setBxMoney(0d);
						}
						totalDao.update(qpcost);

					} else if (about.equalsIgnoreCase("KVP")) {
						ExecuteKVP kvp = (ExecuteKVP) totalDao.getObjectById(
								ExecuteKVP.class, fundApplyDetailed
										.getDeptMonthBudgetID());
						if (kvp == null) {
							break;
						}
						if (kvp.getBxMoney() > fundApplyDetailed
								.getVoucherMoney()) {
							kvp.setBxMoney(kvp.getBxMoney()
									- fundApplyDetailed.getVoucherMoney());
						} else {
							kvp.setBxMoney(0d);
						}
						totalDao.update(kvp);
					} else if (about.equals("外购(历史)")) {
						GoodsStore gs = (GoodsStore) totalDao.getObjectById(
								GoodsStore.class, fundApplyDetailed
										.getDeptMonthBudgetID());
						if (gs == null) {
							break;
						}
						gs.setBaoxiao_status("未报完");
						totalDao.update(gs);
					} else if (about.equals("外委") || about.equals("外购")) {
						WaigouPlan wgPlan = (WaigouPlan) totalDao
								.getObjectById(WaigouPlan.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (wgPlan == null) {
							break;
						}
						wgPlan.setHasjk("否");
						wgPlan.setYfMoney(wgPlan.getYfMoney()
								- fundApplyDetailed.getVoucherMoney());
						totalDao.update(wgPlan);
					} else if (fundApply.getAbout().equalsIgnoreCase("非主营业务应付")) {
						NonCorePayable np = (NonCorePayable) totalDao
								.getObjectById(NonCorePayable.class,
										fundApplyDetailed
												.getDeptMonthBudgetID());
						if (np != null) {
							if (np.getRealfukuanJIne() != null) {
								if (fundApplyDetailed.getVoucherMoney() > np
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
			fundApply.setCwUserCode(user.getCode());
			fundApply.setCwUserName(user.getName());
			fundApply.setSureTime1(Util.getDateTime());
			fundApply.setStatus("打回");
			totalDao.update(fundApply);
			return "true";

		}
	}

	@Override
	public String sureSelf(Integer id, String password) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录";
		}
		FundApply fundApply = (FundApply) totalDao.getObjectById(
				FundApply.class, id);
		if (fundApply == null) {
			return "没有找到目标!";
		}
		Password pwd = (Password) totalDao.getObjectByCondition(
				"from Password where user.id=?", fundApply.getUserId());
		MD5 md5 = new MD5();
		String mdsPassword = md5.getMD5(password.getBytes());// 密码MD5转换
		if (pwd.getPassword().equals(mdsPassword)) {
			if (fundApply.getAbout().equals("支付")) {
				fundApply.setStatus("关闭");
			} else {
				fundApply.setStatus("待还款");
			}
			totalDao.update(fundApply);
			return "true";
		} else {
			return "密码错误!";
		}
	}

	public Map<Integer, Object> findPay_ExecutionNode(Integer id) {
		FundApply fundApply = (FundApply) this.totalDao.getObjectById(
				FundApply.class, id);
		String hql = "from ExecutionNode where auditStatus='同意' and  circuitRun.id=? order by auditLevel desc";
		List<ExecutionNode> list1 = this.totalDao.query(hql, fundApply
				.getEpId());
		List<Signature> list = new ArrayList<Signature>();
		for (int i = 0; i < list1.size(); i++) {
			ExecutionNode executionNode = (ExecutionNode) list1.get(i);
			String username = executionNode.getAuditUserName();
			String hql1 = "from Signature where name='" + username
					+ "' and default_address='默认' ";
			Signature signature = (Signature) this.totalDao
					.getObjectByCondition(hql1);
			String auditDateTime = executionNode.getAuditDateTime();
			if (auditDateTime != null) {
				auditDateTime = auditDateTime.replaceAll("-", "");
				auditDateTime = auditDateTime.replaceAll(":", "");
				auditDateTime = auditDateTime.replaceAll(" ", "");
			}
			executionNode.setAuditDateTime(auditDateTime);
			list.add(signature);
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, list);
		map.put(2, list1);
		return map;
	}

	@Override
	public String zyjl() {
		// TODO Auto-generated method stub.
		// List<BaoxiaoDan> bxdList = totalDao.query("from BaoxiaoDan");
		// List<PaymentVoucher>
		return null;
	}

	@Override
	public String weituo(int[] ids, String bwtCompany) {
		// String hql_cwdf = "from FundApply where status in ('财务确认') ";
		// List list = totalDao.query(hql_cwdf);
		// for (int i = 0; i < list.size(); i++) {
		// FundApply fundApply = (FundApply) list.get(i);
		// CorePayable corePayable = new CorePayable();
		// corePayable.setSubjectItem("费用报销");
		// corePayable.setZhaiyao(fundApply.getExplain());
		// corePayable.setYingfukuanJine(fundApply.getTotalMoney()
		// .floatValue());// 应付款金额
		// corePayable.setYipizhunJine(0F);
		// corePayable.setRealfukuanJine(0F);
		// corePayable.setSaveTime(fundApply.getAddTime());
		// corePayable.setSaveUser(fundApply.getApprovalApplier());
		// corePayable.setStatus("待付款");
		// corePayable.setFk_fundApplyId(fundApply.getId());
		// corePayable.setSupplierName(fundApply.getRelationclient());
		// corePayable.setOrderNumber(fundApply.getNumber());
		// // 生成编号
		// String num = "F" + Util.getDateTime("yyyyMMdd");
		// String hql_finMaxnum =
		// "select max(fkNumber) from CorePayable where fkNumber like '%"
		// + num + "%'";
		// String maxfkNumber = (String) totalDao
		// .getObjectByCondition(hql_finMaxnum);
		// if (maxfkNumber != null && maxfkNumber.length() > 0) {
		// String subnum = (Integer.parseInt("1"
		// + maxfkNumber.substring(9, maxfkNumber.length())) + 1)
		// + "";
		// num += subnum.substring(1, subnum.length());
		// } else {
		// num += "0001";
		// }
		// corePayable.setFkNumber(num);
		// totalDao.save(corePayable);
		// }
		// ids = null;
		// TODO Auto-generated method stub
		addEscrowPayCom(bwtCompany);// 添加新的委托公司
		if (ids != null && ids.length > 0) {
			for (int id : ids) {
				FundApply fa = (FundApply) totalDao.getObjectById(
						FundApply.class, id);
				if (fa != null) {
					if (fa.getHaswt() != null && fa.getHaswt().equals("是")) {
						throw new RuntimeException("已委托过，委托失败!");
					}
					Escrow escrow = new Escrow();
					escrow.setShoukuanFang(fa.getRelationclient());// 收款方
					escrow.setShoukuanZhanghao(fa.getShoukuanZhanghao());// 收款账号
					StringBuffer sb = new StringBuffer();
					Set<FundApplyDetailed> fadSet = fa.getFadSet();
					for (FundApplyDetailed detailed : fadSet) {
						if (sb.length() == 0) {
							sb.append(detailed.getPay_use());
						} else {
							sb.append(";" + detailed.getPay_use());
						}
					}
					escrow.setPayWay(fa.getPayStyle());
					escrow.setFukuanYongtu(sb.toString());// 付款用途
					escrow.setYingfuJine(fa.getTotalMoney());// 应付款金额
					escrow.setShifuJine(0d);// 实际款金额
					escrow.setMore(fa.getExplain());// 备注
					escrow.setUsername(fa.getApprovalApplier());// 申请人
					escrow.setApplyTime(Util.getDateTime());// 申请时间
					escrow.setPaymentMonth(null);// 支付月份
					escrow.setPaymentTime(null);// 支付时间
					escrow.setStatus("未审批");// 状态
					escrow.setZijinshiyongNum(fa.getNumber());// 资金使用申请单号
					escrow.setZijinshyongID(fa.getId());// 资金使用申请id
					escrow.setBwtCompany(bwtCompany);
					totalDao.save(escrow);
					// escrow.setepId;//资金使用申请id
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess("委托付款",
								Escrow.class, escrow.getId(), "status", "id",
								"FundApplyAction_findfundDetailedList.action?id="
										+ escrow.getZijinshyongID(), "有向"
										+ fa.getRelationclient()
										+ "付款的资金委托申请，请您审批", true);
						if (epId != null && epId > 0) {
							escrow.setEpId(epId);
							CircuitRun circuitRun = (CircuitRun) totalDao.get(
									CircuitRun.class, epId);
							if ("同意".equals(circuitRun.getAllStatus())
									&& "审批完成".equals(circuitRun
											.getAuditStatus())) {
								escrow.setStatus("同意");
							} else {
								escrow.setStatus("未审批");
							}
							totalDao.update(escrow);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					fa.setHaswt("是");
					totalDao.update(fa);
				}

			}
		}
		return "true";
	}

	/**
	 * 添加新的委托公司
	 * 
	 * @param accessWebcam
	 */
	private void addEscrowPayCom(String s) {
		if (s != null && !"".equals(s)) {
			int I = totalDao.getCount("from EscrowPayCom where payCom = ?", s);
			if (I == 0) {
				totalDao.save(new EscrowPayCom(s));
			}
		}
	}

	@Override
	public Integer findwwOrder(String wwNumber) {
		// TODO Auto-generated method stub
		return (Integer) totalDao.getObjectByCondition(
				"select id from WaigouOrder where planNumber=?", wwNumber);
	}

	@Override
	public List getskdw() {
		// TODO Auto-generated method stub
		return totalDao.query("from Payee where epStattus='同意'");
	}

	@Override
	public String addInvoiceCheckRecording(InvoiceCheckRecording in) {
		// TODO Auto-generated method stub
		String res = "";
		if (in != null && in.getInvoiceFilees() != null
				&& in.getInvoiceFilees().length() > 0) {// 图片识别
			// 打开存放文件的位置
			
			String realFilePath = "/upload/file/invoiceChe";
			String path = ServletActionContext.getServletContext().getRealPath(
					realFilePath)
					+ "\\" + in.getInvoiceFilees();
			res = MainController.check2(path);
		} else {
			res = MainController.check1(in.getFpdm(), in.getFphm(), in.getJe(),
					in.getKprq(), in.getJym());
		}
		Users us = Util.getLoginUser();
		if (us != null) {
			in.setAddDept(us.getDept());
			in.setAddName(us.getName());
			in.setAddUserId(us.getId());
			in.setAddTime(Util.getDateTime());
			try {
				addJson1(res, in, us);
				totalDao.save(in);
				if (in.getCode() != null
						&& (in.getCode() == 0 || in.getCode() == 20509)) {
					if (in.getMsg() != null && in.getMsg().length() < 20) {
						return "查验成功";
					} else {
						return in.getMsg();
					}
				} else {
					return in.getMsgTow() == null || "".equals(in.getMsgTow()) ? in
							.getMsg()
							: in.getMsgTow();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				totalDao.save(in);
				return in.getMsg();
			}
			// InvoiceCheck invoiceCheck = findbyFp(in);
			// if(invoiceCheck==null){
			// }else {
			// in.setInvoiceCheck(invoiceCheck);
			// totalDao.save(in);
			// return "查验成功";
			// }
		} else {
			return "请先登录";
		}
	}

	private void addJson1(String result, InvoiceCheckRecording in, Users us)
			throws UnsupportedEncodingException {
		try {
			JSONObject jsonObject = new JSONObject(result);
			in.setCode(intName(jsonObject, "code"));
			in.setMsg(stringName(jsonObject, "msg"));
			if (in.getCode() != null && in.getCode() > 0) {
				switch (in.getCode()) {
				case 20412:
					in
							.setMsgTow("国税系统有符合代码、号码的发票，但是其它信息和国税系统的发票不一致。*请检查开票日期、税前金额或校验码");
					break;
				case 20413:
					in
							.setMsgTow("所查发票不存在，开票单位未及时上传发票信息，请在开票1-2日后再行查验;国税系统中查无此票");
					break;
				case 20419:
					in.setMsgTow("当前发票信息不规范，请检查发票查验项。");
					break;
				case 20420:
					in.setMsgTow("地区国税异常，建议1小时后再试。");
					break;
				case 20426:
					in.setMsgTow("国税局查验异常。");
					break;
				default:
					break;
				}
			}
			if (in.getInvoiceFilees() != null
					&& in.getInvoiceFilees().length() > 0) {// 如果是发票文件识别 data为数组
				JSONArray data = jsonObject.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);// 单张发票信息
					if (object != null) {
						if (in.getCode() == 0 || in.getCode() == 20509) {
							in.setMsgTow(stringName(object, "msg"));
							in.setInvoiceid(stringName(object, "invoiceid"));
							in.setCode(intName(object, "code"));
							if (in.getCode() != null && in.getCode() > 0) {
								switch (in.getCode()) {
								case 20412:
									in
											.setMsgTow("国税系统有符合代码、号码的发票，但是其它信息和国税系统的发票不一致。*请检查开票日期、税前金额或校验码");
									break;
								case 20413:
									in
											.setMsgTow("所查发票不存在，开票单位未及时上传发票信息，请在开票1-2日后再行查验;国税系统中查无此票");
									break;
								case 20419:
									in.setMsgTow("当前发票信息不规范，请检查发票查验项。");
									break;
								case 20420:
									in.setMsgTow("地区国税异常，建议1小时后再试。");
									break;
								case 20426:
									in.setMsgTow("国税局查验异常。");
									break;
								case 20801:
									in.setMsgTow("OCR识别失败！建议更换清晰图片后重试。");
									break;
								default:
									break;
								}
							}
						}
						JSONArray forbidens = object.getJSONArray("forbidens");
						in.setForbidens(forbidens.toString());//
						JSONObject invoice = object.getJSONObject("invoice");
						if (invoice != null) {
							in.setFpdm(stringName(invoice, "fpdm"));
							in.setFphm(stringName(invoice, "fphm"));
							in.setKprq(stringName(invoice, "kprq"));
							in.setJe(doubleName(invoice, "je"));
							in.setJym(stringName(invoice, "jym"));
							InvoiceCheck invoiceCheck = findbyFp(in);
							System.out.println(in.getInvoiceFilees());
							if (invoiceCheck == null) {
								// 判断正确。如果没有就添加
								InvoiceCheck check = com.alibaba.fastjson.JSONObject
										.parseObject(invoice.toString(),
												InvoiceCheck.class);
								setCheck(us, check,in.getInvoiceFilees());
								in.setInvoiceCheck(check);
								JSONArray invoice_detail = object
										.getJSONArray("invoice_detail");
								for (int j = 0; j < invoice_detail.length(); j++) {
									JSONObject objectD = invoice_detail
											.getJSONObject(i);
									if (objectD != null) {
										addInvoice_detail(check, objectD);
									}
								}
							} else {
								in.setInvoiceCheck(invoiceCheck);
								in
										.setMsg("此发票已被："
												+ invoiceCheck.getAddName()
												+ " 于 "
												+ invoiceCheck.getAddTime()
												+ " 已添加,添加方式："
												+ (invoiceCheck.getFile_path() == null
														|| ""
																.equals(invoiceCheck
																		.getFile_path()) ? "四要素查询"
														: "OCR识别"));
							}
						}
					}
				}
			} else {// 四要素
				JSONObject data = jsonObject.getJSONObject("data");
				if (data != null) {
					if (in.getCode() == 0 || in.getCode() == 20509) {
						in.setMsgTow(stringName(data, "msg"));
						in.setInvoiceid(stringName(data, "invoiceid"));
					}
					JSONArray forbidens = data.getJSONArray("forbidens");
					in.setForbidens(forbidens.toString());//
					JSONObject data_1 = data.getJSONObject("data");
					if (data_1 != null) {
						JSONObject invoice = data_1.getJSONObject("invoice");
						if (invoice != null) {
							InvoiceCheck invoiceCheck = findbyFp(in);
							if (invoiceCheck == null) {
								// 判断正确。如果没有就添加
								InvoiceCheck check = com.alibaba.fastjson.JSONObject
										.parseObject(invoice.toString(),
												InvoiceCheck.class);
								setCheck(us, check,in.getInvoiceFilees());
								in.setInvoiceCheck(check);
								JSONArray invoice_detail = data_1
										.getJSONArray("invoice_detail");
								for (int i = 0; i < invoice_detail.length(); i++) {
									JSONObject object = invoice_detail
											.getJSONObject(i);
									if (object != null) {
										addInvoice_detail(check, object);
									}
								}
							} else {
								in.setInvoiceCheck(invoiceCheck);
							}
						}
					}
				}
			}
			// System.out.println("code:"+code +"msg:"+message +"\ncode:"+
			// yanzhengnum);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 添加发票明细
	 * 
	 * @param check
	 * @param object
	 */
	private void addInvoice_detail(InvoiceCheck check, JSONObject object) {
		Invoice_detail detail = new Invoice_detail();
		net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(object
				.toString());
		detail = (Invoice_detail) net.sf.json.JSONObject.toBean(obj,
				Invoice_detail.class);
		detail.setInvoiceCheck(check);
		totalDao.save(detail);
	}

	/**
	 * 赋值固定发票信息
	 * 
	 * @param us
	 * @param check
	 */
	private void setCheck(Users us, InvoiceCheck check,String url) {
		check.setAddDept(us.getDept());
		check.setAddName(us.getName());
		check.setAddUserId(us.getId());
		check.setBaoxiaoStatus("未报销");
		check.setAddTime(Util.getDateTime());
		check.setFile_path(url);
		String fpzlShow = "";
		if (check.getTitle().indexOf("增值税普通") > 0) {
			fpzlShow = "普票";
		} else if (check.getTitle().indexOf("增值税专用") > 0) {
			fpzlShow = "专票";
		} else if (check.getTitle().indexOf("增值税电子") > 0) {
			fpzlShow = "电票";
		} else {
			fpzlShow = "其他";
		}
		check.setFpzlShow(fpzlShow);
		totalDao.save(check);
	}

	/**
	 * 根据开票日期，发票代码，发票号码查询发票信息
	 * 
	 * @param in
	 * @return
	 */
	private InvoiceCheck findbyFp(InvoiceCheckRecording in) {
		InvoiceCheck invoiceCheck = (InvoiceCheck) totalDao
				.getObjectByCondition(
						"from InvoiceCheck where kprq = ? and fpdm = ? and fphm = ?",
						in.getKprq(), in.getFpdm(), in.getFphm());
		return invoiceCheck;
	}

	private static int intName(JSONObject jsonObject, String ss) {
		int i = 0;
		try {
			i = jsonObject.getInt(ss);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

	private static Double doubleName(JSONObject jsonObject, String ss) {
		Double i = 0d;
		try {
			i = jsonObject.getDouble(ss);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

	private static String stringName(JSONObject jsonObject, String ss) {
		String s = "";
		try {
			s = jsonObject.getString(ss);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public Object[] findInvoiceCheckList(InvoiceCheck invoiceCheck,
			int parseInt, int pageSize, String pagestatus, String fapiaohao) {
		// TODO Auto-generated method stub
		if (invoiceCheck == null) {
			invoiceCheck = new InvoiceCheck();
		}
		String sql = "";
		if ("all".equals(pagestatus)) {
		} else if ("allWei".equals(pagestatus)) {
			sql += " baoxiaoStatus = '未报销' ";
		} else if ("allCode".equals(pagestatus)) {
			sql += " baoxiaoStatus = '未报销' ";
			Users u = Util.getLoginUser();
			if (u != null) {
				sql += " and addUserId = " + u.getId();
			}
		} else if ("code".equals(pagestatus)) {
			Users u = Util.getLoginUser();
			if (u != null) {
				sql += " addUserId = " + u.getId();
			} else {
				sql += " addUserId = 0 ";
			}
		} else if ("dept".equals(pagestatus)) {
			Users u = Util.getLoginUser();
			if (u != null) {
				sql += " addDept = '" + u.getDept() + "'";
			} else {
				sql += " addUserId = 0 ";
			}
		} else {
			sql += " addUserId = 0 ";
		}
		if (fapiaohao != null && fapiaohao.length() > 0) {
			String[] fps = fapiaohao.split(",");
			sql += " and fphm not in (''";
			for (String fp : fps) {
				sql += ",'" + fp + "'";
			}
			sql += ")";
		}
		String hql = totalDao.criteriaQueries(invoiceCheck, sql)
				+ " order by baoxiaoStatus,id desc";
		List fundApplyList = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { fundApplyList, count };
	}

	@Override
	public InvoiceCheck findInvoiceCheckbyId(Integer id) {
		// TODO Auto-generated method stub
		InvoiceCheck check = (InvoiceCheck) totalDao.getObjectById(
				InvoiceCheck.class, id);
		if (check != null) {
			Set<Invoice_detail> details = check.getDetails();
			if (details != null && details.size() > 0) {
				check.setDetails(details);
			}
		}
		return check;
	}

	@Override
	public boolean selectInvoice() {
		// TODO Auto-generated method stub
		return ProcardBlServerImpl.SystemShezhi("发票查验");
	}

	@Override
	public String addInvoiceQuota(InvoiceCheck invoiceCheck) {
		Users us = Util.getLoginUser();
		if(us!=null){
			invoiceCheck.setAddDept(us.getDept());
			invoiceCheck.setAddName(us.getName());
			invoiceCheck.setAddUserId(us.getId());
			invoiceCheck.setAddTime(Util.getDateTime());
			invoiceCheck.setBaoxiaoStatus("未报销");
			
			boolean save = totalDao.save(invoiceCheck);
			if(save) {
				return "添加成功";
			}else {
				return "添加失败";
			}
		}else {
			return "请先登录";
		}
	}

	@Override
	public String uploadInvoiceCheckFile(InvoiceCheck ic) {
		
		if(ic!=null && ic.getId()!=null && ic.getFile_path()!=null && !ic.getFile_path().equals("")) {
			InvoiceCheck invoiceCheck = (InvoiceCheck) totalDao.getObjectById(InvoiceCheck.class, ic.getId());
			if(invoiceCheck!=null) {
				invoiceCheck.setFile_path(ic.getFile_path());
				boolean update = totalDao.update(invoiceCheck);
				if(update) {
					return "上传成功";
				}
			}
		}else {
			return "不满足上传附件的条件。";
		}
		return "系统异常";
	}

	@Override
	public InvoiceCheck findInvoickCheckByFpdm(String fpdm) {
		if(fpdm!=null && !fpdm.equals("")) {
			InvoiceCheck invoiceCheck = (InvoiceCheck) totalDao.getObjectByCondition("from InvoiceCheck where fphm = ?", fpdm);
			return invoiceCheck;
		}
		return null;
	}
	
	@Override
	public Double getbackmoney(Integer userid){
		String hql="select sum(totalMoney-backMoney) from FundApply where userId=? and zhifuoryufu='预付' and status in ('待还款','未付清')";
		return (Double) totalDao.getObjectByConditionforDouble(hql, userid);
	}
	

}
