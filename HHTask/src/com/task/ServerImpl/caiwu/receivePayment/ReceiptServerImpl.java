package com.task.ServerImpl.caiwu.receivePayment;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.caiwu.receivePayment.ReceiptServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.ShortMessageServiceImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Users;
import com.task.entity.caiwu.CwUseDetail;
import com.task.entity.caiwu.CwVouchers;
import com.task.entity.caiwu.CwVouchersDetail;
import com.task.entity.caiwu.core.MonthPayableBill;
import com.task.entity.caiwu.core.SupplierCorePayable;
import com.task.entity.caiwu.receivePayment.Receipt;
import com.task.entity.caiwu.receivePayment.ReceiptLog;
import com.task.entity.fin.Escrow;
import com.task.entity.fin.budget.SubBudgetRate;
import com.task.entity.payee.Payee;
import com.task.entity.payment.FundApply;
import com.task.entity.payment.FundApplyDetailed;
import com.task.entity.seal.SealLog;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.CompanyInfo;
import com.task.util.Upload;
import com.task.util.Util;
import com.tast.entity.zhaobiao.ZhUser;

/****
 * 付款单ServerImpl
 * 
 * @author liupei
 * 
 */
public class ReceiptServerImpl implements ReceiptServer {

	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	/****
	 * 添加应付账单
	 * 
	 * @param receipt
	 * @return
	 */
	@Override
	public boolean addReceipt(Receipt receipt) {
		boolean bool = false;
		// 添加付款单
		// Receipt receipt = new Receipt();
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
		// receipt.setPayee(corePayableMonth
		// .getSupplierName());
		// receipt.setPayeeId(corePayableMonth
		// .getSupplierId());
		// receipt.setSummary(corePayableMonth
		// .getSupplierName()
		// + corePayableMonth.getJzMonth()
		// + "应付账款");
		// receipt.setPayType("采购订单");
		// receipt.setAboutNum(corePayableMonth
		// .getRecNumber());
		// receipt.setFk_monthlyBillsId(corePayableMonth
		// .getId());
		// receipt.setAllMoney(corePayableMonth
		// .getYingfukuanJine());
		receipt.setAccountPaid(0F);
		receipt.setUnPay(receipt.getAllMoney());
		receipt.setPayIng(0F);
		receipt.setPayOn(0F);
		receipt.setPayLast(receipt.getAllMoney());
		// receipt.setFukuanDate(Util.getDateTime("yyyy-MM-dd"));
		// receipt.setPaymentCycle(corePayableMonth
		// .getFukuanZq());
		receipt.setFukuanMonth(Util.getDateTime("yyyy-MM"));
		receipt.setStatus("待付款");

		receipt.setAddTime(Util.getDateTime());
		receipt.setAddUserCode(Util.getLoginUser().getCode());
		receipt.setAddUserName(Util.getLoginUser().getName());
		if (receipt.getPayeeId() == null) {
			Payee payee = (Payee) totalDao.getObjectByCondition(
					"from Payee where name=?", receipt.getPayee());
			if (payee != null) {
				receipt.setPayeeId(payee.getId());
			}
		}
		bool = totalDao.save(receipt);

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
			bool = totalDao.update(monthPayableBill);
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
			bool = totalDao.save(monthPayableBill);
		}

		// 添加应付汇总
		// 查询该收款单位在付款汇总表是否存在
		String hql_SupplierCorePayable = "from SupplierCorePayable where supplierName=? and coreType='非主营' and payableType='付款'";
		SupplierCorePayable scp = (SupplierCorePayable) totalDao
				.getObjectByCondition(hql_SupplierCorePayable, receipt
						.getPayee());
		if (scp != null) {
			scp.setYingfukuanJine(scp.getYingfukuanJine()
					+ receipt.getAllMoney());
			scp
					.setWeifukuanJine(scp.getWeifukuanJine()
							+ receipt.getAllMoney());
			bool = totalDao.update(scp);
		} else {
			scp = new SupplierCorePayable();
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
			bool = totalDao.save(scp);
		}
		return bool;
	}

	/****
	 * 付款账单管理
	 */
	@Override
	public Object[] findReceiptList(Receipt receipt, int pageNo, int pageSize,
			String pageStatus) {
		if (receipt == null) {
			receipt = new Receipt();
		}
		String hql = totalDao.criteriaQueries(receipt, null,
				new String[] { "payeeId" });
		if (pageStatus != null && pageStatus.length() > 0) {
			if ("viewAuditPay".equals(pageStatus)) {
				hql += " and status='审批中'";
			} else if ("auditPay".equals(pageStatus)) {
				hql += " and status='审批中'";
			} else if ("dfk".equals(pageStatus)) {
				hql += " and status='待付款'";
			}
		}
		if (receipt.getPayeeId() != null && receipt.getPayeeId() > 0) {
			hql += " and payeeId=" + receipt.getPayeeId();
		}

		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/***
	 * 申请付款
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public String applyForPay(List<Receipt> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Receipt pageReceipt = list.get(i);
				if (pageReceipt != null) {
					Receipt dbReceipt = (Receipt) totalDao.getObjectById(
							Receipt.class, pageReceipt.getId());
					if (dbReceipt != null) {
						dbReceipt.setPayOn(pageReceipt.getPayOn());
						dbReceipt.setStatus("审批中");
						totalDao.update(dbReceipt);
						/********* 生成付款记录单 **********/
						ReceiptLog receiptLog = new ReceiptLog();
						// 生成编号
						String num = "AP" + Util.getDateTime("yyyyMMdd");
						String hql_finMaxnum = "select max(pkNumber) from ReceiptLog where pkNumber like '%"
								+ num + "%'";
						String maxfkNumber = (String) totalDao
								.getObjectByCondition(hql_finMaxnum);
						if (maxfkNumber != null && maxfkNumber.length() > 0) {
							String subnum = (Integer.parseInt("1"
									+ maxfkNumber.substring(10, maxfkNumber
											.length())) + 1)
									+ "";
							num += subnum.substring(1, subnum.length());
						} else {
							num += "0001";
						}
						receiptLog.setPkNumber(num);
						receiptLog.setAllMoney(pageReceipt.getPayOn());
						receiptLog.setAddTime(Util.getDateTime());
						receiptLog.setApplyUserCode(Util.getLoginUser()
								.getCode());
						receiptLog.setApplyUserName(Util.getLoginUser()
								.getName());
						receiptLog.setStatus("待审批");
						receiptLog.setReceipt(dbReceipt);
						totalDao.save(receiptLog);
					} else {
						new RuntimeException("数据异常!");
					}
				}
			}
			AlertMessagesServerImpl.addAlertMessages("付款审批", "您有待审核付款申请需要处理!",
					"1");
			return "true";
		}
		return "请最少选择一行!";
	}

	/***
	 * 付款调额及审批
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public String auditPay(List<ReceiptLog> list, Integer subId) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				ReceiptLog pageReceiptLog = list.get(i);
				if (pageReceiptLog == null) {
					continue;
				}
				ReceiptLog dbReceiptLog = (ReceiptLog) totalDao.getObjectById(
						ReceiptLog.class, pageReceiptLog.getId());
				// 通知相关银行账户的负责人前去付款
				if (subId != null) {
					SubBudgetRate sbr=(SubBudgetRate) totalDao.getObjectById(SubBudgetRate.class, subId);
					if(sbr!=null){
						dbReceiptLog.setPaybank(sbr.getName());
						dbReceiptLog.setPaybankId(sbr.getId());
					}
					String hql_users = "   select U.id  from SubBudgetRate S join S.userSet U  where S.id =? ";
					Integer userId = (Integer) totalDao.getObjectByCondition(
							hql_users, subId);
					AlertMessagesServerImpl.addAlertMessages("付款记录管理",
							"您负责的银行账户有付款任务，请您及时前往付款", new Integer[] { userId },
							"", true);
				}
				if (dbReceiptLog != null) {
					Receipt dbReceipt = (Receipt) totalDao.getObjectById(
							Receipt.class, dbReceiptLog.getReceipt().getId());
					// 更新付款记录信息
					dbReceiptLog.setAllMoney(pageReceiptLog.getAllMoney());
					if (dbReceipt.getUserId() != null) {
						dbReceiptLog.setStatus("收款确认");
					} else {
						dbReceiptLog.setStatus("上传凭证");
					}
					totalDao.update(dbReceiptLog);

					// 更新付款单信息
					dbReceipt.setPayOn(0F);
					dbReceipt.setPayIng(dbReceipt.getPayIng()
							+ pageReceiptLog.getAllMoney());
					// dbReceipt.setPayLast(dbReceipt.getAllMoney()
					// - pageReceipt.getPayIng());
					if (dbReceipt.getAllMoney() - dbReceipt.getPayIng() > 0) {
						dbReceipt.setStatus("待付款");
					} else {
						dbReceipt.setStatus("付款中");
					}

					if (dbReceipt.getFk_fundApplyId() == null) {
						FundApply fundApply = new FundApply();
						fundApply.setRelationclientId(dbReceipt.getPayeeId());
						fundApply.setApprovaldept("");
						fundApply.setAbout("其它");
						fundApply.setIsSelfDept("本部门");
						fundApply.setJituan("否");
						fundApply.setBaoxiaoDate(Util.getDate());
						fundApply.setVoucherbasis("通知");
						fundApply.setIsTax("单据");
						fundApply.setAttachmentsCount(0F);
						fundApply.setJingbanren("否");
						fundApply.setVoucherway("银行转账");
						fundApply.setVouchercondition("即付");
						fundApply.setCurrency("");
						fundApply.setTotalMoney(dbReceipt.getAllMoney()
								.doubleValue());

						Set<FundApplyDetailed> fadSet = new HashSet<FundApplyDetailed>();
						FundApplyDetailed fundApplyDetailed = new FundApplyDetailed();
						fundApplyDetailed.setBudgetDept("");
						fundApplyDetailed.setZjStyle(dbReceipt
								.getSubBudgetName());
						fundApplyDetailed.setFk_SubBudgetRateId(dbReceipt
								.getFk_SubBudgetRateId());
						fundApplyDetailed.setVoucherMoney(dbReceipt
								.getAllMoney().doubleValue());
						fundApplyDetailed.setPay_use(dbReceipt.getSummary());
						fundApplyDetailed.setFundApply(fundApply);
						fadSet.add(fundApplyDetailed);
						fundApply.setFadSet(fadSet);

						if ("税务局".equals(dbReceipt.getPayee())) {
							fundApply.setZhifuoryufu("支付");
							fundApply.setJingbanren("是");
						} else if ("上海市住房公积金管理中心".equals(dbReceipt.getPayee())) {
							fundApply.setZhifuoryufu("支付");
							fundApply.setJingbanren("是");
						}
						boolean bool = totalDao.save(fundApply);
						if (bool) {
							dbReceipt.setFk_fundApplyId(fundApply.getId());
						}
					}

					totalDao.update(dbReceipt);
					// 通知申请人抓紧付款
					AlertMessagesServerImpl.addAlertMessages("付款记录管理",
							"您有新的付款任务,请您及时付款!", "1");

					// 给报销人发送消息，让其确认已收款
					// 获得网站配置信息

					String mes = "您的相关账款(" + dbReceipt.getSummary() + "),本次付款"
							+ pageReceiptLog.getAllMoney()
							+ "元,已经进入付款流程,请两个工作日(休息或节假日顺延)内确认已收到账款!--"
							+ Util.getLoginCompanyInfo().getName();

					if (dbReceipt.getUserId() != null) {
						Users users = (Users) totalDao.getObjectById(
								Users.class, dbReceipt.getUserId());
						if (users != null) {
							// 内部员工发送提醒以及rtx消息
							AlertMessagesServerImpl.addAlertMessages("收款确认",
									mes, "1", users.getCode());
						}
					}

					// 供应商
					String phone = "";
					if (dbReceipt.getZhUserId() != null) {
						ZhUser zhuser = (ZhUser) totalDao.getObjectById(
								ZhUser.class, dbReceipt.getZhUserId());
						if (zhuser != null) {
							// 同时发送短信提醒
							phone = zhuser.getCtel();

						}
					}
					if (phone != null && phone.length() > 0) {
						ShortMessageServiceImpl sms = new ShortMessageServiceImpl();
						sms.setTotalDao(totalDao);
						String sendMessage = sms.send(phone, mes);
					}

					// 同步更新收付款汇总表数据
					String hql_SupplierCorePayable = "from SupplierCorePayable where supplierName=? and payableType='付款'";
					SupplierCorePayable scp = (SupplierCorePayable) totalDao
							.getObjectByCondition(hql_SupplierCorePayable,
									dbReceipt.getPayee());
					if (scp != null) {
						if (scp.getFukuanzhongJine() == null) {
							scp.setFukuanzhongJine(0F);
						}
						scp.setFukuanzhongJine(scp.getFukuanzhongJine()
								+ pageReceiptLog.getAllMoney());
						totalDao.update(scp);
					} else {
						new RuntimeException("付款汇总表数据异常!");
					}
					quKey(subId, dbReceiptLog);

				} else {
					new RuntimeException("数据异常!");
				}
			}
			return "true";
		}
		return "请最少选择一行!";
	}

	/**
	 * 如果需要用到库存现金，生成取钥匙验证码
	 * 
	 * @param subId
	 * @param dbReceiptLog
	 */
	@Override
	public void quKey(Integer subId, ReceiptLog dbReceiptLog) {
		int ii = totalDao
				.getCount(
						"from SubBudgetRate where name = '库存现金' and id in (select subBudgetRate.id from SubBudgetRate where id = ?)",
						subId);
		if (ii > 0) {
			Users us = Util.getLoginUser();
			SealLog sealLog = new SealLog(us.getName(), us.getCode());
			CircuitRunServerImpl.seal_1(totalDao, sealLog, "保险柜钥匙");
		}
		int ii2 = totalDao
				.getCount(
						"from SubBudgetRate where name = '银行存款' and id in (select subBudgetRate.id from SubBudgetRate where id = ?)",
						subId);
		if (ii2 > 0) {
			Users us = Util.getLoginUser();
			SealLog sealLog = new SealLog(us.getName(), us.getCode());
			CircuitRunServerImpl.seal_1(totalDao, sealLog, "U盾", dbReceiptLog
					.getId());
		}
	}

	/****
	 * 付款记录管理
	 */
	@Override
	public Object[] findReceiptLogList(ReceiptLog receiptLog, int pageNo,
			int pageSize, String pageStatus, Boolean isAccountCheck) {
		if (receiptLog == null) {
			receiptLog = new ReceiptLog();
		}
		String hql = totalDao.criteriaQueries(receiptLog, null, "receipt");
		if (receiptLog != null && receiptLog.getReceipt() != null
				&& receiptLog.getReceipt().getId() != null) {
			hql += " and receipt.id=" + receiptLog.getReceipt().getId();
		}
		if (receiptLog != null && receiptLog.getReceipt() != null
				&& receiptLog.getReceipt().getPayee() != null
				&& !"".equals(receiptLog.getReceipt().getPayee())) {
			hql += " and receipt.payee like '%"
					+ receiptLog.getReceipt().getPayee() + "%'";
		}
		if ("audit".equals(pageStatus)) {
			hql += " and status='待审批'";
			pageNo = 0;
			pageSize = 0;
		} else if ("skqr".equals(pageStatus)) {
			hql += " and status='收款确认' and receipt.userId="
					+ Util.getLoginUser().getId();
		} else if ("wtfk".equals(pageStatus)) {
			hql += " and status='上传凭证'";
			pageNo = 0;
			pageSize = 0;
		} else if ("lishi".equals(pageStatus)) {
			hql += " and status not in ('待审批','收款确认','上传凭证')";
		} else if ("all".equals(pageStatus)) {
		}
		if (isAccountCheck && isAccountCheck != null) {
			// 剔除已对账
			hql += "and id not in(SELECT receiptLogid FROM AccountCheck)";
		}
		hql += " order by id desc";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);

		Object[] o = { list, count };
		return o;
	}

	/***
	 * 个人付款确认
	 * 
	 * @return
	 */
	@Override
	public String chenkreceiptLog(Integer logId) {
		if (logId != null) {
			ReceiptLog receiptLog = (ReceiptLog) totalDao.getObjectById(
					ReceiptLog.class, logId);
			Receipt receipt = (Receipt) totalDao.getObjectById(Receipt.class,
					receiptLog.getReceipt().getId());
			if (receiptLog != null && receipt != null) {
				/************* 更新付款记录信息 *****************/
				receiptLog.setStatus("上传凭证");
				totalDao.update(receiptLog);
				return "true";
			} else {
				throw new RuntimeException("数据异常，请重试!");
			}

		}
		return null;
	}

	/***
	 * 上传支付凭证，生成财务凭证
	 * 
	 * @param logId
	 *            付款记录id
	 * @param subjectId
	 *            科目id
	 * @param proofFile
	 *            支付凭证文件
	 * @param fileName
	 *            文件名称
	 * @return
	 */
	@Override
	public String uploadPayProof(ReceiptLog receiptLog1, Integer subjectId,
			File proofFile, String fileName) {
		if (receiptLog1.getId() != null && subjectId != null
				&& proofFile != null) {
			ReceiptLog receiptLog = (ReceiptLog) totalDao.getObjectById(
					ReceiptLog.class, receiptLog1.getId());
			SubBudgetRate subBudgetRate = (SubBudgetRate) totalDao
					.getObjectById(SubBudgetRate.class, subjectId);
			Receipt receipt = (Receipt) totalDao.getObjectById(Receipt.class,
					receiptLog.getReceipt().getId());
			if (receiptLog != null && subBudgetRate != null && receipt != null) {
				/************* 更新付款记录信息 *****************/
				updateReceiptLog(proofFile, fileName, receiptLog, receiptLog1);
				/************* 更新付款账单信息 *****************/
				updateReceipt(receiptLog, receipt);
				/************* 更新报销单对应信息 *****************/
				String jingbanren = "";
				if (receipt.getFk_fundApplyId() != null) {
					/************* 更新报销单对应信息 *****************/
					jingbanren = updateFundApply(receiptLog, receipt,
							jingbanren);
				}
				/************* 更新月度账单应付汇总信息 *****************/
				// 添加月度应付账单
				String hql_MonthPayableBill = "from MonthPayableBill where jzMonth=?";
				MonthPayableBill monthPayableBill = (MonthPayableBill) totalDao
						.getObjectByCondition(hql_MonthPayableBill, receipt
								.getFukuanMonth());
				if (monthPayableBill != null) {
					monthPayableBill.setRealfukuanJine(monthPayableBill
							.getRealfukuanJine()
							+ receiptLog.getAllMoney());
					monthPayableBill.setWeifukuanJine(monthPayableBill
							.getWeifukuanJine()
							- receiptLog.getAllMoney());
					totalDao.update(monthPayableBill);
				}
				/************* 更新收款单位应付汇总信息 *****************/
				// 查询该收款单位在付款汇总表是否存在
				String hql_SupplierCorePayable = "from SupplierCorePayable where supplierName=? and payableType='付款'";
				SupplierCorePayable scp = (SupplierCorePayable) totalDao
						.getObjectByCondition(hql_SupplierCorePayable, receipt
								.getPayee());
				if (scp != null) {
					scp.setRealfukuanJine(scp.getRealfukuanJine()
							+ receiptLog.getAllMoney());
					scp.setWeifukuanJine(scp.getWeifukuanJine()
							- receiptLog.getAllMoney());
					scp.setFukuanzhongJine(scp.getFukuanzhongJine()
							- receiptLog.getAllMoney());
					scp.setFkCount(scp.getFkCount() + 1);
					totalDao.update(scp);
				}

				if (jingbanren.length() > 0) {
					/************* 生成经办人应收账款汇总信息 *****************/
					// 查询该收款单位在付款汇总表是否存在
					saveSuppCpa(receiptLog, jingbanren, scp);
				}

				/************* 生成凭证 *****************/
				CwVouchers cwVouchers = new CwVouchers();
				String num = shengcNum();
				cwVouchers.setNumber(num);
				cwVouchers.setVouchermonth(Util.getDateTime("yyyy-MM"));
				cwVouchers.setVoucherdate(Util.getDateTime("yyyy-MM-dd"));
				cwVouchers.setCreatetime(Util.getDateTime());
				cwVouchers.setUserName(Util.getLoginUser().getName());
				cwVouchers.setCreateCode(Util.getLoginUser().getCode());
				cwVouchers.setCreateName(Util.getLoginUser().getName());
				cwVouchers.setJieMoney((double)receiptLog.getAllMoney());
				cwVouchers.setDaiMoney((double)receiptLog.getAllMoney());
				cwVouchers.setFk_receiptLogId(receiptLog.getId());

				Set<CwVouchersDetail> cwVouchersDetailSet = new HashSet<CwVouchersDetail>();

				// *******************凭证分录 --贷方(银行存款/库存现金)********************
				CwVouchersDetail cwVouchersDetail_dai = new CwVouchersDetail();
				cwVouchersDetail_dai.setvClass("付");
				cwVouchersDetail_dai.setRemark(receipt.getSummary());
				// 查找一级科目
				SubBudgetRate oneLevelsub = (SubBudgetRate) totalDao
						.getObjectById(SubBudgetRate.class, subBudgetRate
								.getRootId());
				cwVouchersDetail_dai.setSub(oneLevelsub.getName());
				cwVouchersDetail_dai.setSubId(oneLevelsub.getId());

				// 明细科目
				cwVouchersDetail_dai
						.setDetailSub(findBudgetRateNames(subBudgetRate.getId()));
				cwVouchersDetail_dai.setDetailSubId(subBudgetRate.getId());
				cwVouchersDetail_dai.setJieMoney(0d);
				cwVouchersDetail_dai.setDaiMoney((double)receiptLog.getAllMoney());
				cwVouchersDetail_dai.setCreateTime(Util.getDateTime());
				cwVouchersDetail_dai.setCreateName(Util.getLoginUser()
						.getName());
				cwVouchersDetail_dai.setCreateCode(Util.getLoginUser()
						.getCode());
				cwVouchersDetail_dai.setCwVouchers(cwVouchers);
				cwVouchersDetailSet.add(cwVouchersDetail_dai);

				// ***************** 凭证分录 --借方(各种科目)*********************
				SubBudgetRate subBudgetRate_jie = null;
				CwVouchersDetail cwVouchersDetail_jie = new CwVouchersDetail();
				Set<CwUseDetail> cwUseDetailSet = new HashSet<CwUseDetail>();
				// 费用报销
				if (receipt.getFk_fundApplyId() != null) {
					String hql_fad = "from FundApplyDetailed where fundApply.id=?";

					FundApplyDetailed fundApplyDetailed = (FundApplyDetailed) totalDao
							.getObjectByCondition(hql_fad, receipt
									.getFk_fundApplyId());
					if (fundApplyDetailed != null) {
						subBudgetRate_jie = (SubBudgetRate) totalDao
								.getObjectById(SubBudgetRate.class,
										fundApplyDetailed
												.getFk_SubBudgetRateId());

						// ***************** 分录--辅助明细********************
						fenLufuZu(receiptLog, receipt, cwVouchersDetail_jie,
								cwUseDetailSet, fundApplyDetailed);
					}
				} else {
					// 科目余额(应付账款)
					subBudgetRate_jie = (SubBudgetRate) totalDao
							.getObjectByCondition("from SubBudgetRate where name='应付账款'");
				}
				if (subBudgetRate_jie == null) {
					return "true";
				}
				cwVouchersDetail_jie.setRemark(receipt.getSummary());
				cwVouchersDetail_jie.setvClass("付");
				// 查找一级科目
				SubBudgetRate oneLevelsub_jie = (SubBudgetRate) totalDao
						.getObjectById(SubBudgetRate.class, subBudgetRate_jie
								.getRootId());
				cwVouchersDetail_jie.setSub(oneLevelsub_jie.getName());
				cwVouchersDetail_jie.setSubId(oneLevelsub_jie.getId());
				// 明细科目
				cwVouchersDetail_jie
						.setDetailSub(findBudgetRateNames(subBudgetRate_jie
								.getId()));
				cwVouchersDetail_jie.setDetailSubId(subBudgetRate_jie.getId());

				cwVouchersDetail_jie.setJieMoney((double)receiptLog.getAllMoney());
				cwVouchersDetail_jie.setDaiMoney(0d);
				cwVouchersDetail_jie.setCreateTime(Util.getDateTime());
				cwVouchersDetail_jie.setCreateName(Util.getLoginUser()
						.getName());
				cwVouchersDetail_jie.setCreateCode(Util.getLoginUser()
						.getCode());
				cwVouchersDetail_jie.setCwUseDetails(cwUseDetailSet);
				cwVouchersDetail_jie.setCwVouchers(cwVouchers);
				cwVouchersDetailSet.add(cwVouchersDetail_jie);

				cwVouchers.setCwVouchersDetails(cwVouchersDetailSet);
				totalDao.save(cwVouchers);

				/************* 更新科目余额 *****************/
				// 更新借方科目余额
				updatesubBudgetRate(subBudgetRate_jie.getId(), receiptLog
						.getAllMoney().doubleValue(), 0D);
				// 更新贷方科目余额
				updatesubBudgetRate(subBudgetRate.getId(), 0D, receiptLog
						.getAllMoney().doubleValue());
				AlertMessagesServerImpl.addAlertMessages("做账确认",
						"您有凭证编号为" + cwVouchers.getNumber() + "的"
								+ "凭证 待做账，请前往《做账确认》功能确认", "1");
				return "true";
			} else {
				throw new RuntimeException("数据异常，请重试!");
			}

		}
		return null;
	}

	/**
	 * 更新经办人应收账款汇总信息
	 * 
	 * @param receiptLog
	 * @param jingbanren
	 * @param scp
	 */
	private void saveSuppCpa(ReceiptLog receiptLog, String jingbanren,
			SupplierCorePayable scp) {
		String hql_SupplierCorePayableys = "from SupplierCorePayable where supplierName=? and payableType='收款'";
		SupplierCorePayable scpys = (SupplierCorePayable) totalDao
				.getObjectByCondition(hql_SupplierCorePayableys, jingbanren);
		if (scpys != null) {
			scpys.setYingfukuanJine(scpys.getYingfukuanJine()
					+ receiptLog.getAllMoney());
			scpys.setWeifukuanJine(scpys.getWeifukuanJine()
					+ receiptLog.getAllMoney());
			totalDao.update(scp);
		} else {
			String hql = "from Payee where name =?";
			Payee payee = (Payee) totalDao
					.getObjectByCondition(hql, jingbanren);
			if (payee != null) {
				scpFei(receiptLog, payee);
			}
		}
	}

	/**
	 * 更新应付登记信息
	 * 
	 * @param receiptLog
	 * @param receipt
	 */
	private String updateFundApply(ReceiptLog receiptLog, Receipt receipt,
			String jingbanren) {
		FundApply fundApply = (FundApply) totalDao.getObjectById(
				FundApply.class, receipt.getFk_fundApplyId());
		if (fundApply != null) {
			if (fundApply.getPayMoney() == null) {
				fundApply.setPayMoney(0D);
			}
			fundApply.setPayMoney(fundApply.getPayMoney()
					+ receiptLog.getAllMoney());
			if ("预付".equals(fundApply.getZhifuoryufu())) {
				if ("是".equals(fundApply.getJingbanren())) {
					jingbanren = fundApply.getApprovalApplier();// 经办方
				} else {
					jingbanren = fundApply.getRelationclient();// 借款方
				}
				fundApply.setStatus("待还款");
			} else if (fundApply.getPayMoney()
					.equals(fundApply.getTotalMoney())) {
				fundApply.setStatus("封闭");
			}
			totalDao.update(fundApply);
		}
		return jingbanren;
	}

	/**
	 * 非主营收付款信息
	 * 
	 * @param receiptLog
	 * @param payee
	 */
	private void scpFei(ReceiptLog receiptLog, Payee payee) {
		SupplierCorePayable scp;
		scp = new SupplierCorePayable();
		scp.setSupplierName(payee.getName());
		scp.setPayeeId(payee.getId());
		scp.setCoreType("非主营");
		scp.setPayableType("收款");
		scp.setYingfukuanJine(receiptLog.getAllMoney());
		scp.setRealfukuanJine(0F);
		scp.setWeifukuanJine(receiptLog.getAllMoney());
		scp.setFukuanzhongJine(0F);
		scp.setFkCount(0);
		scp.setFukuanzhongJine(0F);
		scp.setAddTime(Util.getDateTime());
		totalDao.save(scp);
	}

	/**
	 * 更新付款账单信息
	 * 
	 * @param receiptLog
	 * @param receipt
	 */
	private void updateReceipt(ReceiptLog receiptLog, Receipt receipt) {
		receipt.setAccountPaid(receipt.getAccountPaid()
				+ receiptLog.getAllMoney());
		receipt.setUnPay(receipt.getAllMoney() - receipt.getAccountPaid());
		receipt.setPayIng(0F);
		receipt.setPayLast(receipt.getUnPay());
		if (receipt.getUnPay() == 0) {
			receipt.setStatus("完成");
		}
		totalDao.update(receipt);
	}

	/**
	 * 更新付款记录信息
	 * 
	 * @param proofFile
	 * @param fileName
	 * @param receiptLog
	 */
	private void updateReceiptLog(File proofFile, String fileName,
			ReceiptLog receiptLog, ReceiptLog receiptLog1) {
		receiptLog.setRealPayDate(Util.getDateTime());
		// 上传凭证附件
		String realFilePath = "/upload/file/payPz";
		String path = ServletActionContext.getServletContext().getRealPath(
				realFilePath);
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();// 如果不存在文件夹就新建
		}
		Upload upload = new Upload();
		String filenewname = upload.UploadFile(proofFile, fileName, null,
				realFilePath, null);
		receiptLog.setFileName(filenewname);
		receiptLog.setFkpzNum(receiptLog1.getFkpzNum());
		receiptLog.setPayUserName(Util.getLoginUser().getName());
		receiptLog.setPayUserCode(Util.getLoginUser().getCode());
		receiptLog.setStatus("完成");
		totalDao.update(receiptLog);
	}

	/**
	 * 分录--辅助明细
	 * 
	 * @param receiptLog
	 * @param receipt
	 * @param cwVouchersDetail_jie
	 * @param cwUseDetailSet
	 * @param fundApplyDetailed
	 */
	private void fenLufuZu(ReceiptLog receiptLog, Receipt receipt,
			CwVouchersDetail cwVouchersDetail_jie,
			Set<CwUseDetail> cwUseDetailSet, FundApplyDetailed fundApplyDetailed) {
		CwUseDetail cwUseDetail = new CwUseDetail();
		cwUseDetail.setPayee(fundApplyDetailed.getFundApply()
				.getRelationclient());
		cwUseDetail.setUseFor(fundApplyDetailed.getPay_use());
		cwUseDetail.setUsemoney((double)receiptLog.getAllMoney());
		cwUseDetail.setAboutNum(fundApplyDetailed.getFundApply().getNumber());
		cwUseDetail.setFk_fundApplyId(receipt.getFk_fundApplyId());
		cwUseDetail.setPayNum(receiptLog.getPkNumber());
		cwUseDetail.setPayType(receipt.getPayType());
		cwUseDetail.setCwVouchersDetail(cwVouchersDetail_jie);
		cwUseDetailSet.add(cwUseDetail);
	}

	/**
	 * 生成应付凭证编号
	 * 
	 * @return
	 */
	private String shengcNum() {
		// 生成编号
		String num = "P" + Util.getDateTime("yyyyMM");
		String hql_finMaxnum = "select max(number) from CwVouchers where number like '%"
				+ num + "%'";
		String maxfkNumber = (String) totalDao
				.getObjectByCondition(hql_finMaxnum);
		if (maxfkNumber != null && maxfkNumber.length() > 0) {
			String subnum = "";
			Integer maxnum = Integer.parseInt(maxfkNumber.substring(7,
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
		return num;
	}

	/***
	 * 更新科目余额(迭代更新上层N层科目)
	 * 
	 * @param subBudgetRate
	 * @param borrowMoney
	 * @param lendMoney
	 * @return
	 */
	@Override
	public boolean updatesubBudgetRate(Integer subBudgetRateId,
			double borrowMoney, double lendMoney) {
		if (subBudgetRateId != null && subBudgetRateId > 0) {
			SubBudgetRate dbsubBudgetRate = (SubBudgetRate) totalDao
					.getObjectById(SubBudgetRate.class, subBudgetRateId);
			if (dbsubBudgetRate != null) {
				dbsubBudgetRate.setBorrowMoney(dbsubBudgetRate.getBorrowMoney()
						+ borrowMoney);
				dbsubBudgetRate.setLendMoney(dbsubBudgetRate.getLendMoney()
						- lendMoney);
				Double jieyuMoney = dbsubBudgetRate.getBorrowQichuMoney()
						+ dbsubBudgetRate.getBorrowMoney()
						- dbsubBudgetRate.getLendQichuMoney()
						- dbsubBudgetRate.getLendMoney();
				if (jieyuMoney > 0) {
					dbsubBudgetRate.setBorrowJieyuMoney(jieyuMoney);
				} else {
					dbsubBudgetRate.setLendJieyuMoney(jieyuMoney);
				}
				boolean bool = totalDao.update(dbsubBudgetRate);
				// 更新父类科目余额
				if (dbsubBudgetRate.getFatherId() != null
						&& dbsubBudgetRate.getFatherId() > 0) {
					updatesubBudgetRate(dbsubBudgetRate.getFatherId(),
							borrowMoney, lendMoney);
				} else {
					return bool;
				}
			}
		}
		return false;
	}

	/***
	 * 查询科目父类名称表
	 * 
	 * @return
	 */
	@Override
	public String findBudgetRateNames(Integer subBudgetRateId) {
		String subNames = "";
		if (subBudgetRateId != null && subBudgetRateId > 0) {
			SubBudgetRate dbsubBudgetRate = (SubBudgetRate) totalDao
					.getObjectById(SubBudgetRate.class, subBudgetRateId);
			if (dbsubBudgetRate != null) {
				subNames = dbsubBudgetRate.getName();
				// 更新父类科目余额
				if (dbsubBudgetRate.getFatherId() != null
						&& dbsubBudgetRate.getFatherId() > 0) {
					subNames = findBudgetRateNames(dbsubBudgetRate
							.getFatherId())
							+ "-" + subNames;
				} else {
					return subNames;
				}
			}
		}
		return subNames;
	}

	@Override
	public String weituo(Integer[] ids, String bwtCompany, String payWay) {
		// TODO Auto-generated method stub
		if (ids != null && ids.length > 0) {
			for (int id : ids) {
				ReceiptLog receiptLog = (ReceiptLog) totalDao.getObjectById(
						ReceiptLog.class, id);
				if (receiptLog != null) {
					if (receiptLog.getHaswt() != null
							&& receiptLog.getHaswt().equals("是")) {
						throw new RuntimeException("已委托过，委托失败!");
					}
					Escrow escrow = new Escrow();
					escrow.setSourceEntity("ReceiptLog");
					Receipt r = receiptLog.getReceipt();
					if (r.getPayeeId() != null) {
						if ("采购订单".equals(r.getPayType())) {
							ZhUser zuser = (ZhUser) totalDao.getObjectById(
									ZhUser.class, r.getPayeeId());
							escrow.setShoukuanFang(zuser.getCmp());
						} else if ("费用报销".equals(r.getPayType())) {
							Payee payee = (Payee) totalDao.getObjectById(
									Payee.class, r.getPayeeId());
							escrow.setShoukuanFang(payee.getName());// 收款方
							escrow.setShoukuanZhanghao(payee.getaNumber());// 收款账号
							escrow.setShoukuanyh(payee.getBank());
						}
					} else {
						escrow.setShoukuanFang(r.getPayee());// 收款方
						// escrow.setShoukuanZhanghao(fa.getShoukuanZhanghao());//
						// 收款账号
					}
					StringBuffer sb = new StringBuffer();
					escrow.setPayWay(payWay);
					escrow.setFukuanYongtu(sb.toString());// 付款用途
					escrow.setYingfuJine((double) receiptLog.getAllMoney());// 应付款金额
					escrow.setShifuJine(0d);// 实际款金额
					escrow.setMore(r.getSummary());// 备注
					escrow.setUsername(receiptLog.getPayUserName());// 申请人
					escrow.setApplyTime(Util.getDateTime());// 申请时间
					escrow.setPaymentMonth(null);// 支付月份
					escrow.setPaymentTime(null);// 支付时间
					escrow.setStatus("未审批");// 状态
					escrow.setZijinshiyongNum(receiptLog.getPkNumber());// 资金使用申请单号
					escrow.setZijinshyongID(receiptLog.getId());// 资金使用申请id
					escrow.setBwtCompany(bwtCompany);
					totalDao.save(escrow);
					// escrow.setepId;//资金使用申请id
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl
								.createProcess("委托付款", Escrow.class, escrow
										.getId(), "status", "id",
										"FundApplyAction_findfundDetailedList.action?id="
												+ escrow.getZijinshyongID(),
										"有向" + r.getPayee() + "付款的资金委托申请，请您审批",
										true);
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
					receiptLog.setHaswt("是");
					totalDao.update(receiptLog);
				}

			}
		}
		return "true";
	}

	@Override
	public String chongxin(ReceiptLog receiptLog1, Integer subjectId,
			File proofFile, String fileName) {
		// TODO Auto-generated method stub
		if (receiptLog1.getId() != null && subjectId != null
				&& proofFile != null) {
			ReceiptLog receiptLog = (ReceiptLog) totalDao.getObjectById(
					ReceiptLog.class, receiptLog1.getId());
			if (receiptLog != null) {
				/************* 更新付款记录信息 *****************/
				updateReceiptLog(proofFile, fileName, receiptLog, receiptLog1);
				return "true";
			}
		}
		return "信息为空，上传失败！";
	}

}
