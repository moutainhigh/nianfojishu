package com.task.ServerImpl.fin.budget;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.struts2.ServletActionContext;
import org.springframework.util.FileCopyUtils;

import com.task.Dao.TotalDao;
import com.task.Server.fin.budget.SubjectBudgetServer;
import com.task.entity.Category;
import com.task.entity.DeptNumber;
import com.task.entity.Price;
import com.task.entity.caiwu.baobiao.BalanceSheet;
import com.task.entity.caiwu.baobiao.CashFlow;
import com.task.entity.caiwu.baobiao.Management;
import com.task.entity.caiwu.baobiao.ProfitSheet;
import com.task.entity.caiwu.baobiao.XsfyjCwfyMx;
import com.task.entity.Users;
import com.task.entity.fin.budget.SubBudgetRate;
import com.task.entity.fin.budget.SubBudgetRateMonth;
import com.task.util.Util;
import com.task.entity.fin.budget.SubBudgetRateUser;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.ZhUser;

/**
 * 科目预算金额serverImpl
 * 
 * @author jhh
 * 
 */
@SuppressWarnings("unchecked")
public class SubjectBudgetServerImpl implements SubjectBudgetServer {
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	/***
	 * 科目预算查询
	 * 
	 * @param subBudgetRate
	 * @param cpage
	 * @param pageSize
	 * @return
	 */
	@Override
	public Object[] findSubBudget(SubBudgetRate subBudgetRate, Integer cpage,
			Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 添加科目
	 * 
	 * @param subBudgetRate
	 * @return
	 */
	@Override
	public boolean addSubBudgetRate(SubBudgetRate subBudgetRate) {
		if (subBudgetRate != null) {
			boolean bool = totalDao.save(subBudgetRate);
			if (bool) {
				if (subBudgetRate.getBelongLayer() == 1) {// 更新一级科目的rootId为自身id
					subBudgetRate.setRootId(subBudgetRate.getId());
				} else {// 更细父子关系
					SubBudgetRate fatherSbRate = (SubBudgetRate) totalDao
							.getObjectById(SubBudgetRate.class, subBudgetRate
									.getFatherId());
					if (fatherSbRate != null) {
						subBudgetRate.setSubBudgetRate(fatherSbRate);
					}
				}
			}
			return bool;
		}
		return false;
	}

	/**
	 * 删除科目
	 * 
	 * @param subBudgetRate
	 * @return
	 */
	@Override
	public boolean delSubBudgetRate(SubBudgetRate subBudgetRate) {
		if (subBudgetRate != null) {
			return totalDao.delete(subBudgetRate);
		}
		return false;
	}

	/***
	 * 查询所有科目信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SubBudgetRate> findAllSubBudgetByUser() {
		Users user = Util.getLoginUser();
		if (user != null) {
			// String hql =
			// " from SubBudgetRate where id not in (select id from SubBudgetRate where rootId in (select id from SubBudgetRate where  name= '银行存款')  and id not in ( select S.id from SubBudgetRate S join  S.userSet u where u.id = ? ))";
			// List<SubBudgetRate> subList = totalDao.query(hql, user.getId());
			//where borrowYearBegingMoney <> 0 or lendYearBegingMoney <> 0 or borrowYearSumMoney <> 0 or lendYearSumMoney <> 0 or borrowMoney <> 0 or lendMoney <> 0 or borrowJieyuMoney <> 0 or lendJieyuMoney <> 0 or borrowQichuMoney <> 0 or lendQichuMoney <> 0
			String hql = " from SubBudgetRate ";
			List<SubBudgetRate> subList = totalDao.query(hql);
			return subList;
		}
		return null;
	}

	@Override
	public List<SubBudgetRate> findAllSubBudgetByUser(String tag) {
		Users user = Util.getLoginUser();
		if (user != null) {
			String hql = " from SubBudgetRate where 1=1";
			if ("all".equals(tag)) {
			} else if ("z".equals(tag)) {
				hql += " and borrowYearBegingMoney <> 0 or lendYearBegingMoney <> 0 or borrowYearSumMoney <> 0 or lendYearSumMoney <> 0 or borrowMoney <> 0 or lendMoney <> 0 or borrowJieyuMoney <> 0 or lendJieyuMoney <> 0 or borrowQichuMoney <> 0 or lendQichuMoney <> 0";
			}
			List<SubBudgetRate> subList = totalDao.query(hql);
			return subList;
		}
		return null;
	}

	public List<SubBudgetRate> findAllSubBudget() {
		Users user = Util.getLoginUser();
		if (user != null) {
			String hql = " from SubBudgetRate";
			List<SubBudgetRate> subList = totalDao.query(hql);
			return subList;
		}
		return null;
	}

	/***
	 * 通过id查询科目信息
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public SubBudgetRate findSubBudgetRateById(Integer id) {
		if (id != null && id > 0) {
			return (SubBudgetRate) totalDao.getObjectById(SubBudgetRate.class,
					id);
		}
		return null;
	}

	/**
	 * 修改科目
	 * 
	 * @param subBudgetRate
	 * @return
	 */
	@Override
	public boolean updateSubBudgetRate(SubBudgetRate subBudgetRate) {
		if (subBudgetRate != null) {
			return totalDao.update(subBudgetRate);
		}
		return false;
	}

	/**
	 * 修改科目(数据处理)
	 * 
	 * @param subBudgetRate
	 * @return
	 */
	@Override
	public Object[] updateSubBudgetRate(SubBudgetRate subBudgetRate, Integer id) {
		String message = "";
		boolean bool = false;
		SubBudgetRate oldSbBudgetRate = null;
		if (subBudgetRate != null && id != null) {
			oldSbBudgetRate = findSubBudgetRateById(id);
			if (oldSbBudgetRate != null) {
				oldSbBudgetRate.setName(subBudgetRate.getName());
				bool = updateSubBudgetRate(oldSbBudgetRate);
			} else {
				message = "不存在您要修改科目信息!请检查数据!";
			}
		} else {
			message = "数据异常";
		}
		return new Object[] { bool, message, oldSbBudgetRate };
	}

	/***
	 * 更新科目选中状态根据部门id
	 * 
	 * @return
	 */
	public void updateSBRateFroDept(Integer deptId) {
		if (deptId != null && deptId > 0) {
			// 清空科目表选中状态
			String sql = "update ta_fin_subBudgetRate set checked=0";
			totalDao.createQueryUpdate(null, sql);
			// 更新对应部门的科目状态为选中
			String sql2 = "update ta_fin_subBudgetRate set checked=1 where id in (select ta_subid from ta_fin_subDept where ta_deptid=? )";
			totalDao.createQueryUpdate(null, sql2, deptId);
		}
	}

	/***
	 * 更新科目选中状态根据用户id
	 * 
	 * @return
	 */
	public void updateSBRateFroUsers(Integer userId) {
		if (userId != null && userId > 0) {
			// 清空科目表选中状态
			String sql = "update ta_fin_subBudgetRate set userChecked=0";
			totalDao.createQueryUpdate(null, sql);
			// 更新对应部门的科目状态为选中
			String sql2 = "update ta_fin_subBudgetRate set userChecked=1 where id in (select ta_subId from ta_fin_subUsers where ta_userId=? )";
			totalDao.createQueryUpdate(null, sql2, userId);
		}
	}

	/***
	 * 科目与部门绑定
	 * 
	 * @return
	 */
	@Override
	public boolean updateSubDept(Integer deptId, Integer subId) {
		if (deptId != null && subId != null && deptId > 0 && subId > 0) {
			SubBudgetRate subBudgetRate = (SubBudgetRate) totalDao
					.getObjectById(SubBudgetRate.class, subId);
			if (subBudgetRate != null) {
				DeptNumber dept = (DeptNumber) totalDao.getObjectById(
						DeptNumber.class, deptId);
				if (dept != null) {
					Set<DeptNumber> deptSet = subBudgetRate.getDeptNumberSet();
					if (deptSet.contains(dept)) {
						deptSet.remove(dept);
					} else {
						deptSet.add(dept);
						subBudgetRate.setDeptNumberSet(deptSet);
					}
					return totalDao.update(subBudgetRate);
				}
			}
		}
		return false;
	}

	@Override
	public BalanceSheet findbalanceByMonths(String months) {
		if (months == null || months.length() == 0) {
			return (BalanceSheet) totalDao.getObjectByCondition(
					" from BalanceSheet order by months desc ", months);
		}
		return (BalanceSheet) totalDao.getObjectByCondition(
				" from BalanceSheet where months = ? ", months);
	}

	@Override
	public ProfitSheet findprofitByMonths(String months) {
		ProfitSheet Profit = null;
		if (months == null || months.length() == 0) {
			Profit = (ProfitSheet) totalDao
					.getObjectByCondition(" from ProfitSheet order by months desc  ");
			if (Profit != null) {
				months = Profit.getMonths();
			}
		} else {
			Profit = (ProfitSheet) totalDao.getObjectByCondition(
					" from ProfitSheet where months = ? ", months);
		}
		if (Profit != null) {
			String lastyear = (Integer.parseInt(months.substring(0, 4)) - 1)
					+ months.substring(4);
			ProfitSheet lastYearProfit = (ProfitSheet) totalDao
					.getObjectByCondition(
							" from ProfitSheet where months = ? ", lastyear);
			if (lastYearProfit != null) {
				Profit.setLastYearProfit(lastYearProfit);
			}
		}
		return Profit;
	}

	@Override
	public CashFlow findcashflowByMonths(String months) {
		if (months == null || months.length() == 0) {
			return (CashFlow) totalDao.getObjectByCondition(
					" from CashFlow  order by months desc  ");
		}
		return (CashFlow) totalDao.getObjectByCondition(
				" from CashFlow where months = ? ", months);
	}

	@Override
	public List findAllSubDpetjsp(Integer id) {
		// TODO Auto-generated method stub
		Users user = (Users) totalDao.getObjectById(Users.class, id);
		if (user != null) {
			List<SubBudgetRate> list = totalDao.query("from SubBudgetRate");
			if (list != null && list.size() > 0) {
				for (SubBudgetRate sb : list) {
					Float count = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from SubBudgetRateUser where uid=? and sid=?",
									user.getId(), sb.getId());
					if (count != null && count > 0) {
						sb.setChecked(true);
					}
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public Boolean bdAndqxUser(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = (Users) totalDao.getObjectById(Users.class, id);
		SubBudgetRate sb = (SubBudgetRate) totalDao.getObjectById(
				SubBudgetRate.class, id2);
		if (user != null && sb != null) {
			SubBudgetRateUser su = (SubBudgetRateUser) totalDao
					.getObjectByCondition(
							"from SubBudgetRateUser where uid=? and sid=?",
							user.getId(), sb.getId());
			if (su != null) {// 取消绑定
				totalDao.delete(su);
				if (sb.getFatherId() != null) {
					SubBudgetRate father = (SubBudgetRate) totalDao
							.getObjectByCondition(
									"from SubBudgetRate where id=?", sb
											.getFatherId());
					jugeFtaher(father, id);
				}
				quxiaoson(sb, id);
			} else {
				SubBudgetRateUser su2 = new SubBudgetRateUser();
				su2.setUid(id);
				su2.setSid(sb.getId());
				totalDao.save(su2);
				if (sb.getFatherId() != null) {
					SubBudgetRate father = (SubBudgetRate) totalDao
							.getObjectByCondition(
									"from SubBudgetRate where id=?", sb
											.getFatherId());
					bangdingFtaher(father, id);
				}
				bangdingson(sb, id);
			}
		}
		return null;
	}

	private void bangdingson(SubBudgetRate sb, Integer id) {
		// TODO Auto-generated method stub
		Set<SubBudgetRate> sonSet = sb.getSubBudgetRateSet();
		if (sonSet != null && sonSet.size() > 0) {
			List<SubBudgetRateUser> suList = totalDao
					.query(
							"from SubBudgetRateUser where uid=? and sid in(select id from SubBudgetRate where fatherId=?) ",
							id, sb.getId());
			if (suList != null && suList.size() > 0) {
				for (SubBudgetRateUser su : suList) {
					totalDao.delete(su);
				}
			}
			for (SubBudgetRate son : sonSet) {
				SubBudgetRateUser su = (SubBudgetRateUser) totalDao
						.getObjectByCondition(
								"from SubBudgetRateUser where uid=? and sid=?",
								id, son.getId());
				if (su == null) {
					SubBudgetRateUser su2 = new SubBudgetRateUser();
					su2.setUid(id);
					su2.setSid(son.getId());
					totalDao.save(su2);
					bangdingson(son, id);
				}
			}
		}
	}

	private void bangdingFtaher(SubBudgetRate father, Integer id) {
		// TODO Auto-generated method stub
		Float count = (Float) totalDao.getObjectByCondition(
				"select count(*) from SubBudgetRateUser where uid=? and sid=?",
				id, father.getId());
		if (count == null || count == 0) {
			SubBudgetRateUser su = new SubBudgetRateUser();
			su.setUid(id);
			su.setSid(father.getId());
			totalDao.save(su);
			if (father.getSubBudgetRate() != null) {
				bangdingFtaher(father.getSubBudgetRate(), id);
			}
		}
	}

	private void quxiaoson(SubBudgetRate sb, Integer id) {
		// TODO Auto-generated method stub
		Set<SubBudgetRate> sonSet = sb.getSubBudgetRateSet();
		if (sonSet != null && sonSet.size() > 0) {
			List<SubBudgetRateUser> suList = totalDao
					.query(
							"from SubBudgetRateUser where uid=? and sid in(select id from  SubBudgetRate where fatherId=?) ",
							id, sb.getId());
			if (suList != null && suList.size() > 0) {
				for (SubBudgetRateUser su : suList) {
					totalDao.delete(su);
				}
			}
			for (SubBudgetRate son : sonSet) {
				quxiaoson(son, id);
			}
		}
	}

	private void jugeFtaher(SubBudgetRate father, Integer id) {
		// TODO Auto-generated method stub
		Float count = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from SubBudgetRateUser where uid=? and sid in(select id from  SubBudgetRate where fatherId=?)",
						id, father.getId());
		if (count == null || count == 0) {
			SubBudgetRateUser su = (SubBudgetRateUser) totalDao
					.getObjectByCondition(
							"from SubBudgetRateUser where uid=? and sid=?", id,
							father.getId());
			totalDao.delete(su);
			SubBudgetRate father2 = (SubBudgetRate) totalDao
					.getObjectByCondition("from SubBudgetRate where id=?",
							father.getFatherId());
			if (father2 != null) {
				jugeFtaher(father2, id);
			}
		}
	}

	@Override
	public String daoru(File addfile, String months) {
		if (months == null || months.length() == 0) {
			months = Util.getDateTime("yyyy-MM");
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
			FileCopyUtils.copy(addfile, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}

			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 2) {
				List<Integer> strList = new ArrayList<Integer>();
				StringBuffer strb = new StringBuffer();
				Integer errorCount = 0;// 错误数量
				Integer cfCount = 0;// 重复数量
				Integer successCount = 0;// 成功数量
				Integer lastbelongLayer = 0;
				String lastbianhao = "";
				int count = 0;
				SubBudgetRate lastsub = new SubBudgetRate();
				for (i = 2; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					if (cells[0].getContents() != null
							&& cells[0].getContents() != "") {
						String bianhao = cells[0].getContents();// 编号，导进来的编号
						// 用来判断层级从属;
						String name = cells[1].getContents();// 科目名称
						if (name != null && name.length() > 0) {
							name = name.trim();
						}
						if ("应付职工薪酬".equals(name)) {
							System.out.println(name + " ..............");
						}
						String a = cells[2].getContents();// 年初借方金额
						Double borrowYearBegingMoney = 0d;
						if (a != null && a.length() > 0) {
							borrowYearBegingMoney = Double.parseDouble(a);
						}
						String b = cells[3].getContents();// 年初贷方金额
						Double lendYearBegingMoney = 0d;
						if (b != null && b.length() > 0) {
							lendYearBegingMoney = Double.parseDouble(b);
						}
						String c = cells[4].getContents();// 借方-期初金额
						Double borrowQichuMoney = 0d;
						if (c != null && c.length() > 0) {
							borrowQichuMoney = Double.parseDouble(c);
						}
						String d = cells[5].getContents();// 贷方-期初金额
						Double lendQichuMoney = 0d;
						if (d != null && d.length() > 0) {
							lendQichuMoney = Double.parseDouble(d);
						}
						String e = cells[6].getContents();// 本期借方发生
						Double borrowMoney = 0d;
						if (e != null && e.length() > 0) {
							borrowMoney = Double.parseDouble(e);
						}
						String f = cells[7].getContents();// 本期贷方发生
						Double lendMoney = 0d;
						if (f != null && f.length() > 0) {
							lendMoney = Double.parseDouble(f);
						}
						String g = cells[8].getContents();// 本年借方累计
						Double borrowYearSumMoney = 0d;
						if (g != null && g.length() > 0) {
							borrowYearSumMoney = Double.parseDouble(g);
						}
						String h = cells[9].getContents();// 本年贷方累计
						Double lendYearSumMoney = 0d;
						if (h != null && h.length() > 0) {
							lendYearSumMoney = Double.parseDouble(h);
						}
						String j = cells[10].getContents();// 期末借方余额
						Double borrowJieyuMoney = 0d;
						if (j != null && j.length() > 0) {
							borrowJieyuMoney = Double.parseDouble(j);
						}
						String k = cells[11].getContents();// 期末贷方余额
						Double lendJieyuMoney = 0d;
						if (k != null && k.length() > 0) {
							lendJieyuMoney = Double.parseDouble(k);
						}
						if (name.equals("辅助材料")) {
							System.out.println(name + "----");
						}
						SubBudgetRate sub = null;
						Integer belongLayer = ((bianhao.length() - 4) / 2) + 1;
						sub = (SubBudgetRate) totalDao.getObjectByCondition(
								" from SubBudgetRate where subNumber = ? ",
								bianhao);
						boolean subisnull = false;
						if (sub == null) {
							subisnull = true;
							sub = new SubBudgetRate();
						}
						sub.setBookKDate(months);
						sub.setName(name);// 科目名称
						sub.setSubNumber(bianhao);
						sub.setBorrowYearBegingMoney(borrowYearBegingMoney);// 年初借方金额
						sub.setLendYearBegingMoney(lendYearBegingMoney);
						sub.setBorrowQichuMoney(borrowQichuMoney);
						sub.setLendQichuMoney(lendQichuMoney);
						sub.setBorrowMoney(borrowMoney);
						sub.setLendMoney(lendMoney);
						sub.setBorrowYearSumMoney(borrowYearSumMoney);
						sub.setLendYearSumMoney(lendYearSumMoney);
						sub.setBorrowJieyuMoney(borrowJieyuMoney);
						sub.setLendJieyuMoney(lendJieyuMoney);
						sub.setBelongLayer(belongLayer);
						if (subisnull) {
							totalDao.save(sub);
						} else {
							totalDao.update(sub);
						}
						if (bianhao.length() == 4) {
							sub.setRootId(sub.getId());
							totalDao.update(sub);
							successCount++;
							count = 0;
						} else if (bianhao.length() >= 6) {
							lastsub = (SubBudgetRate) totalDao
									.getObjectByCondition(
											" from SubBudgetRate where subNumber =? ",
											bianhao.substring(0, bianhao
													.length() - 3));
							if (lastsub == null) {
								sub.setRootId(sub.getId());
							} else {
								sub.setFatherName(lastsub.getName());
								sub.setFatherId(lastsub.getId());
								sub.setRootId(lastsub.getRootId());
								sub.setSubBudgetRate(lastsub);
							}
							totalDao.update(sub);
							successCount++;

							count++;
						}
						SubBudgetRateMonth submonth = new SubBudgetRateMonth();

						submonth.setName(name);
						submonth.setSubNumber(bianhao);
						submonth.setBookKDate(months);
						submonth.setAddTimeDate(Util.getDateTime());
						submonth
								.setBorrowYearBegingMoney(borrowYearBegingMoney);// 年初借方金额
						submonth.setLendYearBegingMoney(lendYearBegingMoney);
						submonth.setBorrowQichuMoney(borrowQichuMoney);
						submonth.setLendQichuMoney(lendQichuMoney);
						submonth.setBorrowMoney(borrowMoney);
						submonth.setLendMoney(lendMoney);
						submonth.setBorrowYearSumMoney(borrowYearSumMoney);
						submonth.setLendYearSumMoney(lendYearSumMoney);
						submonth.setBorrowJieyuMoney(borrowJieyuMoney);
						submonth.setLendJieyuMoney(lendJieyuMoney);
						submonth.setFk_SubBudgetRateId(sub.getId());
						submonth.setFathrName(sub.getFatherName());
						totalDao.save(submonth);
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个\\n失败" + errorCount + "个\\n重复"
						+ cfCount + "个\\n失败的行数分别为：\\n" + strb.toString();
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "导入出错,请截屏发给管理员!\\n" + e.getMessage() + (i + 1) + "行";
			e.printStackTrace();
		}
		return msg;

	}

	@Override
	public Object[] getjisunGS(String bianhao, String month) {
		if (month == null || month.length() == 0) {
			month = Util.getDateTime("yyyy-MM");
		}
		String years = month.substring(0, 4) + "";
		if (bianhao != null && bianhao.length() > 0) {
			List<SubBudgetRateMonth> sbRateList = new ArrayList<SubBudgetRateMonth>();
			// List<SubBudgetRate> sbRateList = new ArrayList<SubBudgetRate>();

			/**
			 * 资产负债表公式z2~z97;
			 */
			String jisugs = "";
			List doubleList = new ArrayList();
			if ("z2".equals(bianhao)) {
				jisugs = "货币资金 = 库存现金（期末余额）+银行存款（期末余额）+其他货币资金（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('库存现金','银行存款','其他货币资金')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select cash2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z3".equals(bianhao)) {
				jisugs = "交易性金融资产 = 交易性金融资产（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('交易性金融资产')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select jyxjrzc2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z4".equals(bianhao)) {
				jisugs = "应收票据 = 应收票据（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('应收票据') and  belongLayer = 1)  and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select yspj2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z5".equals(bianhao)) {
				jisugs = "应收帐款 = 应付账款(期末借方余额)-坏账准备[应收账款(期末余额)]";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber   from SubBudgetRate where name in('应付账款')  and  belongLayer = 1) and  bookKDate =?",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where fatherId in( select id from SubBudgetRate where name = '坏账准备' ) and name = '应收账款') and   bookKDate =?  ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						"select yszk2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z6".equals(bianhao)) {
				jisugs = "预付账款=预付账款(借方-期末余额)+应付账款(借方-期末余额)-预付账款[坏账准备](期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('预付账款','应付账款')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where fatherId in( select id from SubBudgetRate where name = '坏账准备'  ) and name = '预付账款') and   bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select yfzk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z7".equals(bianhao)) {
				jisugs = "应收利息=应收利息(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应收利息')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select yslx2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z8".equals(bianhao)) {
				jisugs = "应收股利=应收股利(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应收股利')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						"select ysgl2,months,id from  BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z9".equals(bianhao)) {
				jisugs = "其他应收款=其他应收款(期末余额)-坏账准备[其他应收款](期末余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他应收款')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where fatherId in( select id from SubBudgetRate where name = '坏账准备' ) and name = '其他应收款')  and   bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select qtysk2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z10".equals(bianhao)) {
				jisugs = "存货=材料采购(期末余额)+在途物资(期末余额)+原材料(期末余额)+库存商品(期末余额)+周转材料(期末余额)+委托加工物资(期末余额)+生产成本(期末余额)+劳务成本(期末余额)-存货跌价准备()";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('材料采购','在途物资','原材料','库存商品','周转材料','委托加工物资','生产成本','劳务成本')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('存货跌价准备')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select ch2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z11".equals(bianhao)) {
				jisugs = "原材料=原材料(期末余额)+材料成本差异（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('原材料') and  belongLayer = 1)  and  bookKDate =? ",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('材料成本差异')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select ycl2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z12".equals(bianhao)) {
				jisugs = "库存商品=库存商品(期末余额)+发出商品（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('库存商品')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('发出商品')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select kcsp2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z13".equals(bianhao)) {
				jisugs = " 一年内到期的非流动资产 =  一年内到期的持有至到期投资 + 长期待摊费用 + 一年内可收回的长期应收款 ";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('持有至到期投资','长期待摊费用','长期应收款') and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  fldzcyears2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z14".equals(bianhao)) {
				// doubleList =
				// totalDao.query(" select  from BalanceSheet where months like '"+years+"%' and months <= "
				// , month);
			} else if ("z17".equals(bianhao)) {
				jisugs = "可供出售金融资产 =可供出售金融资产(期末余额) ";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('可供出售金融资产') and  belongLayer = 1 ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  kgcsjrzc2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z18".equals(bianhao)) {
				jisugs = " 持有至到期投资 =持有至到期投资(期末余额)- 一年内到期的投资部部分金额 - 持有至到期投资减值准备 ";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('持有至到期投资','持有至到期投资减值准备')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  cyzdqtz2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z19".equals(bianhao)) {
				jisugs = " 长期应收款 =长期应收款(期末余额)-一年内到期的投资部门的金额-未实现融资收益(期末余额)-坏账准备[长期应收款(期末余额)] ";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('长期应收款','未实现融资收益')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				SubBudgetRateMonth sub = (SubBudgetRateMonth) totalDao
						.getObjectByCondition(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where fatherId in( select id from SubBudgetRate where name = '坏账准备'  ) and name = '长期应收款') and   bookKDate =? ",
								month);
				sbRateList.add(sub);
				doubleList = totalDao.query(
						" select  cqysk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z20".equals(bianhao)) {
				jisugs = "长期股权投资=长期股权投资(期末余额) - 长期股权投资减值准备";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('长期股权投资','长期股权投资减值准备')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  cqgqtz2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z21".equals(bianhao)) {
				jisugs = "投资性房地产=投资性房地产(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('投资性房地产')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  tzxfdc2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z22".equals(bianhao)) {
				jisugs = "固定资产原价=固定资产(期末余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('固定资产')  and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  gizcyj2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ? ", month);
			} else if ("z23".equals(bianhao)) {
				jisugs = "累计折旧=累计折旧(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('累计折旧')  and  belongLayer = 1 ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						"  select  zcljzj2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z24".equals(bianhao)) {
				jisugs = "固定资产净值=固定资产原价-累计折旧(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('固定资产','累计折旧')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gdzcjz2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z25".equals(bianhao)) {
				jisugs = "固定资产减值准备=固定资产减值准备(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('固定资产减值准备')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  gdzcjzzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z26".equals(bianhao)) {
				jisugs = "固定资产净额=固定资产净值-固定资产减值准备(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('固定资产','累计折旧','固定资产减值准备') and  belongLayer = 1)  and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gdzcje2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z27".equals(bianhao)) {
				jisugs = "在建工程=在建工程(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('在建工程')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  zjgc2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z28".equals(bianhao)) {
				jisugs = "工程物资=工程物资(期末余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('工程物资')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gchz2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z29".equals(bianhao)) {
				jisugs = "固定资产清理= 固定资产清理(期末余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('固定资产清理')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gdzcql2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z30".equals(bianhao)) {
				jisugs = "生产性生物资产 = 生产性生物资产(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('生产性生物资产')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  scxswzc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z31".equals(bianhao)) {
				jisugs = "油气资产 = 油气资产(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('油气资产')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  yqzc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z32".equals(bianhao)) {
				jisugs = "无形资产 = 无形资产(期末余额)-无形资产减值准备(期末余额)-累计摊销(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('无形资产','无形资产减值准备','累计摊销')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  wxzc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z33".equals(bianhao)) {
				jisugs = "土地使用权 = 土地使用权(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber   from SubBudgetRate where name in('土地使用权') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  tdsyq2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z34".equals(bianhao)) {
				jisugs = "开发支出 = 开发支出(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('开发支出') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  kfzc2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z35".equals(bianhao)) {
				jisugs = "商誉 =商誉(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('商誉')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  shangyu2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z36".equals(bianhao)) {
				jisugs = "长期待摊费用（递延资产） =长期待摊费用(期末余额)-将于1年内(含1年)摊销的数额";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('长期待摊费用')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  cqdtfy2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z37".equals(bianhao)) {
				jisugs = "递延所得税资产 =递延所得税资产(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('递延所得税资产') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						"  select  dysdsc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z38".equals(bianhao)) {
				jisugs = "递延所得税资产 =递延所得税资产(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('递延所得税资产') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  dysdsc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z39".equals(bianhao)) {

			} else if ("z50".equals(bianhao)) {
				jisugs = "短期借款 = 短期借款（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('短期借款')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  dqjk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z51".equals(bianhao)) {
				jisugs = "交易性金融负债 =交易性金融负债(期末余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('交易性金融负债')and  belongLayer = 1 )  and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  jyxjrfz2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z52".equals(bianhao)) {
				jisugs = "应付票据 =应付票据(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付票据')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yfpj2,months from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z53".equals(bianhao)) {
				jisugs = "应付账款 =应付账款(期末贷方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('短期借款')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  yingfuZK2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z54".equals(bianhao)) {
				jisugs = "预收款项=预收账款(期末贷方余额)+应收账款(期末贷方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('预收账款','应收账款')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yskx2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z55".equals(bianhao)) {
				jisugs = " 应付职工薪酬= 应付职工薪酬(期末借方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付职工薪酬')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  yfzgxc2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z56".equals(bianhao)) {
				jisugs = "应付工资=应付工资(期末借方余额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付工资')  and  fatherName = '应付职工薪酬') and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yfgz2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z57".equals(bianhao)) {
				jisugs = "应付福利费=应付福利费(期末借方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付福利费')  and  fatherName = '应付职工薪酬') and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yfflf2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z58".equals(bianhao)) {
				jisugs = "应交税费=应交税费(期末贷方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('应交税费')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						"  select  yjsf2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z59".equals(bianhao)) {
				jisugs = "应交税金=应交税金(期末贷方余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应交税金')  and  fatherName = '应交税费') and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yjsj2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z60".equals(bianhao)) {
				jisugs = "应付利息=应付利息(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付利息')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						"  select  yflx2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z61".equals(bianhao)) {
				jisugs = "应付股利（应付利润）=应付股利(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付股利')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yfgl2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z62".equals(bianhao)) {
				jisugs = "其他应付款=其他应付款(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他应付款')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  qtyfk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z63".equals(bianhao)) {
				jisugs = " 一年内到期的非流动负债=一年内到期的(长期借款+长期应付款+应付债券)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('长期借款','长期应付款','应付债券')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  fldfzyears2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z64".equals(bianhao)) {
				jisugs = "其他流动负债 = 其他流动负债(期末余额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他流动负债')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  qtldfz2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z65".equals(bianhao)) {

				// doubleList =
				// totalDao.query("  from BalanceSheet where months like '"+years+"%' and months <= "
				// , month);
			} else if ("z67".equals(bianhao)) {
				jisugs = "长期借款 =长期借款(期末余额) - 一年内到期部分的金额";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('长期借款')  and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  cqjk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z68".equals(bianhao)) {
				jisugs = "应付债券=应付债券（期末余额）- 一年内到期的金额";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('应付债券')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yfzq2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z69".equals(bianhao)) {
				jisugs = "长期应付款= 长期应付款（期末余额）- 一年内到期的金额 - 未确认融资费用";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('长期应付款','未确认融资费用') and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  cqyfk2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z72".equals(bianhao)) {
				jisugs = "递延所得税负债= 递延所得税负债（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('递延所得税负债')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  dysdsfz2,months,id from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z73".equals(bianhao)) {
				jisugs = "其他非流动负债= 其他非流动负债（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他非流动负债')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  qtfldfz2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z74".equals(bianhao)) {
				jisugs = "特准储备基金= 特准储备基金（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('特准储备基金')  and  fatherName = '其他非流动负债') and  bookKDate =? ",
								month);
			} else if ("z78".equals(bianhao)) {
				jisugs = "实收资本（股本）=实收资本(或股本)（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('实收资本(或股本)')  and  belongLayer = 1 ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  sszb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z79".equals(bianhao)) {
				jisugs = "国家资本=国家资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('国家资本')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gjzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z80".equals(bianhao)) {
				jisugs = "集体资本=集体资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('集体资本')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  jtzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z81".equals(bianhao)) {
				jisugs = "法人资本=法人资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('法人资本')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  frzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z82".equals(bianhao)) {
				jisugs = "国有法人资本=国有法人资本（期末余额）";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('国有法人资本')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gyfrzb2,months,id   from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z83".equals(bianhao)) {
				jisugs = "集体法人资本=集体法人资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('集体法人资本') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  jtfrzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=?", month);
			} else if ("z84".equals(bianhao)) {
				jisugs = "个人资本=个人资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('个人资本') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  grzb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z85".equals(bianhao)) {
				jisugs = "外商资本=外商资本（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('外商资本')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  wszb2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z86".equals(bianhao)) {
				jisugs = "资本公积=资本公积（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('资本公积')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  zbgj2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("z88".equals(bianhao)) {
				jisugs = "盈余公积=盈余公积（期末余额）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('盈余公积')) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yygj2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("z91".equals(bianhao)) {
				jisugs = "未分配利润= 期初未分配利润+本期净利润-提取的各种盈余公积-分出的利润+以前年度损益调整的未分配利润期末未分配利润=期初未分配利润"
						+ "+本期净利润-（提取盈余公积+对股东的分配+其他）-（盈余公积弥补亏损+其他）";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('本年利润','利润分配') ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  wfplr2,months,id  from BalanceSheet where months like '"
								+ years + "%' and months <=? ", month);
			}
			/**
			 * 利润表:(Lr1~Lr39)
			 */
			else if ("Lr1".equals(bianhao)) {
				jisugs = "营业收入=主营业务收入(本期借方发生额)+其他业务收入(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('主营业务收入','其他业务收入')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  ywsr,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr2".equals(bianhao)) {
				jisugs = "主营业务收入=主营业务收入(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('主营业务收入')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  zyywsr,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr3".equals(bianhao)) {
				jisugs = "其他业务收入=其他业务收入(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"   from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他业务收入')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  qtywsr,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr4".equals(bianhao)) {
				jisugs = "营业成本=主营业务成本(本期借方发生额)+其他业务成本(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('主营业务成本','其他业务成本') and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  ywcb,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr5".equals(bianhao)) {
				jisugs = "主营业务成本=主营业务成本(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber  from SubBudgetRate where name in('主营业务成本') and  belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  zyywcb,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr6".equals(bianhao)) {
				jisugs = "其他业务成本=其他业务成本(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('其他业务成本')  and  belongLayer = 1 ) and  bookKDate =?",
								month);
				doubleList = totalDao.query(
						" select  qtywcb,months,id   from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr7".equals(bianhao)) {
				jisugs = "营业税金及附加=营业税金及附加(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('营业税金及附加')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  ywsjandfj,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr8".equals(bianhao)) {
				jisugs = "销售费用=销售费用(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('销售费用')  and  belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  xsfy,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr9".equals(bianhao)) {
				jisugs = "管理费用=管理费用(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('管理费用')  and  belongLayer = 1 )and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  glfy,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr10".equals(bianhao)) {
				jisugs = "业务招待费=业务招待费(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('业务招待费')  and fatherName ='管理费用') and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  ywzdf,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr11".equals(bianhao)) {
				jisugs = "研究与开发费=研究与开发费(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('研究与开发费')  and fatherName ='管理费用' ) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  yjykff,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr12".equals(bianhao)) {
				jisugs = "财务费用=财务费用(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('财务费用')  and belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  cwfy,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr16".equals(bianhao)) {
				jisugs = "资产减值损失=资产减值损失(本期借方发生额)";
				sbRateList = totalDao
						.query(
								"  from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('资产减值损失')  and belongLayer = 1) and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  zcjzss,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr18".equals(bianhao)) {
				jisugs = "公允价值变动损益=公允价值变动损益(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('公允价值变动损益') and belongLayer = 1)  and  bookKDate =? ",
								month);
				doubleList = totalDao.query(
						" select  gyzbdsy,months,id   from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr19".equals(bianhao)) {
				jisugs = "投资收益=投资收益(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('投资收益')  and belongLayer = 1) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  tzsy,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr21".equals(bianhao)) {
				jisugs = "营业利润=营业收入-营业成本-营业税金及附加-销售费用-管理费用-财务费用-资产减值损失+公允价值变动损益+投资收益";
				doubleList = totalDao.query(
						" select  ywlr,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr22".equals(bianhao)) {
				jisugs = "营业外收入=营业外收入(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('营业外收入')  and belongLayer = 1) and  bookKDate =?   ",
								month);
				doubleList = totalDao.query(
						" select  ywsr1,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr27".equals(bianhao)) {
				jisugs = "营业外支出=营业外支出(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('营业外支出')  and belongLayer = 1 ) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  ywzc,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("Lr31".equals(bianhao)) {
				jisugs = "利润总额=营业利润+营业外收入-营业外支出";
				doubleList = totalDao.query(
						" select  lrze,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("Lr32".equals(bianhao)) {
				jisugs = "所得税费用=所得税费用(本期借方发生额)";
				sbRateList = totalDao
						.query(
								" from SubBudgetRateMonth where subNumber in (select subNumber from SubBudgetRate where name in('所得税费用')  and belongLayer = 1 ) and  bookKDate =?  ",
								month);
				doubleList = totalDao.query(
						" select  sdsfy,months,id  from ProfitSheet where months like '"
								+ years + "%' and months <= ?", month);
			}
			/**
			 *现金流量表 xj2~xj68
			 */
			else if ("xj2".equals(bianhao)) {
				jisugs = "销售商品、提供劳务收到的现金=营业收入+应交税费[应交增值税(销项税额)]+(应收票据期初余额-应收票据期末余额)+"
						+ "(应收账款期初余额-应收账款期末余额)+(预收款项期末余额-预收款项期初余额)-当期计提的坏账准备";
				doubleList = totalDao.query(
						" select  jymoneyIn1,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj3".equals(bianhao)) {
				jisugs = "收到的税费反还 = 实际收到的增值税、消费税、营业税、所得税、关税、教育费附加返还等项";
				doubleList = totalDao.query(
						" select  jymoneyIn2,months,id  from CashFlow where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("xj4".equals(bianhao)) {
				jisugs = "收到的其他与经营活动有关的现金=罚款收入+接受捐赠现金收入+经营租赁固定资产收到的现金+投资性房地产收到的租金收入+"
						+ " 出租和出借包装物的租金收入+逾期未退还出租和出借包装物没收的押金收入+流动资产损失中由个人赔偿的现金收入+除税费返还外的其他政府补助收入等";
				doubleList = totalDao.query(
						" select  jymoneyIn3,months,id  from CashFlow where months like '"
								+ years + "%' and months <=? ", month);
			} else if ("xj8".equals(bianhao)) {
				jisugs = "购买商品、接受劳务支付的现金= 营业成本+应交税费[应交增值税(进项税额)]+(存货期末余额-存货期初余额)+(应付账款期初余额-应付账款期末余额)"
						+ " + (应付票据期初余额-应付票据期末余额)+(预付账款期末余额-预付账款期初余额)-当期列入生产成本、制造费用的职工薪酬-"
						+ " 当期列入生产成本、制造费用的折旧费";
				doubleList = totalDao.query(
						" select  jymoneyOut1,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj9".equals(bianhao)) {
				jisugs = "支付给职工以及为职工支付的现金= 生产成本、制造费用、管理费用中职工薪酬+(应付职工薪酬期初余额-应付职工薪酬期末余额)"
						+ " - [应付职工薪酬(在建工程)期初余额 - 应付职工薪酬(在建工程)期末余额]";
				doubleList = totalDao.query(
						" select  jymoneyOut2,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj10".equals(bianhao)) {
				jisugs = "支付的各项税费=  当期所得费用+ (应交所得税期初余额-应交所得税期末余额)+支付的营业税金及附加+应交税费-"
						+ " 应交增值税(已交税金)";
				doubleList = totalDao.query(
						" select  jymoneyOut3,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj11".equals(bianhao)) {
				jisugs = "支付的其他与经营活动有关的现金=支付其他管理费用+支付的其他销售费用+支付的其他制造费用+"
						+ " 进行捐赠的现金支出+罚款支出等";
				doubleList = totalDao.query(
						" select  jymoneyOut4,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj15".equals(bianhao)) {
				jisugs = "收回投资所收到的现金= 出售、转让或到期收回除现金等价物以外的可供出售金融资产、长期股权投资"
						+ " 、交易性金融资产等而收到的现金";
				doubleList = totalDao.query(
						" select  tzmoneyIn1,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj16".equals(bianhao)) {
				jisugs = "取得投资收益所收到的现金= 因股权投资而收到的现金股利+丛子公司、合营企业、联营企业分回利润"
						+ "、现金股利而收到的现金+因债权性投资而收到的利息";
				doubleList = totalDao.query(
						" select  tzmoneyIn2,months,id  from CashFlow where months like '"
								+ years + "%' and months <= ?", month);
			} else if ("xj17".equals(bianhao)) {
				jisugs = "处置固定资产、无形资产和其他长期资产所收回的现金净额= 处置固定资产、无形资产和其他长期资产收回的现金-"
						+ "处置固定资产、无形资产和其他长期资产所发生的现金支出";
				doubleList = totalDao
						.query(
								" select  tzmoneyIn3,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj18".equals(bianhao)) {
				jisugs = "处置子公司及其他营业单位收到的现金净额 = 处置子公司及其他营业 months like '"
						+ years + "%'单位所取得的现金 - 相关处置费用-子公司及其"
						+ "他营业单位持有的现金和现金等价物";
				doubleList = totalDao
						.query(
								" select  tzmoneyIn4,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj19".equals(bianhao)) {
				jisugs = "收到的其他与投资活动有关的现金 = 其他与投资活动有关的现金流入";
				doubleList = totalDao
						.query(
								" select  tzmoneyIn5,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj21".equals(bianhao)) {
				jisugs = "购建固定资产、无形资产和其他长期资产所支付的现金 = 企业本期购买、建造国定资产、取得无形资产和"
						+ "其他长期资产(如投资性房地产)的价款+税费+现金支付的应由在建工程和无形资产负担的职工薪酬";
				doubleList = totalDao
						.query(
								" select  tzmoneyOut1,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj22".equals(bianhao)) {
				jisugs = "投资所支付的现金 = 取得除现金等价物以外的交易性金融资产、持有至到期投资、"
						+ "可供出售金融资产、长期股权投资的现金+支付给劵商的佣金、手续费等附加费用";
				doubleList = totalDao
						.query(
								" select  tzmoneyOut2,months,id  from CashFlow where and months <=? ",
								month);
			} else if ("xj23".equals(bianhao)) {
				jisugs = "取得子公司及其他营业单位支付的现金净额 = 企业购买子公司及其他营业单位支出的现金 - 子公司"
						+ "及其他营业单位持有的现金和现金等价物";
				doubleList = totalDao
						.query(
								" select  tzmoneyOut3,months,id  from CashFlow where and months <=? ",
								month);
			} else if ("xj24".equals(bianhao)) {
				jisugs = "支付的其他与投资活动有关的现金 = 其他与投资活动有关的现金流出";
				doubleList = totalDao
						.query(
								" select  tzmoneyOut4,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj29".equals(bianhao)) {
				jisugs = "吸收投资所收到的现金 =发起人投入的现金+已发行股票方式筹集的资金实际收到股款净额";
				doubleList = totalDao
						.query(
								" select  czmoneyIn1,months,id  from CashFlow where and months <=? ",
								month);
			} else if ("xj30".equals(bianhao)) {
				jisugs = "借款所收到的现金 =企业举借各种短期、长期借款而收到的现金+"
						+ "发行债券收入-委托其他单位发行债券所支付的佣金等发行费用";
				doubleList = totalDao
						.query(
								" select  czmoneyIn2,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj31".equals(bianhao)) {
				jisugs = "收到的其他与筹资活动有关的现金  = 其他与筹资活动有关的现金流入";
				doubleList = totalDao
						.query(
								" select  czmoneyIn3,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj34".equals(bianhao)) {
				jisugs = " 偿还债务所支付的现金  = 企业偿还银行、金融机构的借款本金+偿还债券本金";
				doubleList = totalDao
						.query(
								" select  czmoneyOut1,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj35".equals(bianhao)) {
				jisugs = "分配股利、利润或偿付利息所支付的现金  = 企业实际支付的现金股利+"
						+ "支付给其他投资单位的利润+用现金支付的借款利息+支付的债券利息";
				doubleList = totalDao
						.query(
								" select  czmoneyOut2,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj36".equals(bianhao)) {
				jisugs = "支付的其他与筹资活动有关的现金  = 其他与筹资活动有关的现金流出";
				doubleList = totalDao
						.query(
								" select  czmoneyOut2,months,id  from CashFlow where and months <= ?",
								month);
			} else if ("xj40".equals(bianhao)) {
				jisugs = "汇率变动对现金的影响  = 收入的外币现金*(期末汇率-记账汇率)-支付的外币现金*( 期末汇率-记账汇率)";
				doubleList = totalDao
						.query(
								" select  hlyx,months,id from CashFlow where and months <= ?",
								month);
			}
			return new Object[] { jisugs, sbRateList, doubleList };
		}

		return null;
	}

	@Override
	public Management findMaByMonths(String months) {
		if (months == null || months.length() == 0) {
			months = Util.getDateTime("yyyy-MM");
		}
		return (Management) totalDao.getObjectByCondition(
				" from Management where months = ? ", months);
	}

	@Override
	public XsfyjCwfyMx findXcmByMonths(String months) {
		if (months == null || months.length() == 0) {
			months = Util.getDateTime("yyyy-MM");
		}
		return (XsfyjCwfyMx) totalDao.getObjectByCondition(
				" from XsfyjCwfyMx where months = ? ", months);
	}

	@Override
	public String jiezhuang() {
		// String months = Util.getDateTime("yyyy-MM");
		String months = "2018-03";
		List<SubBudgetRate> subList = totalDao.query(
				" from SubBudgetRate where bookKDate = ?", months);
		if (subList != null && subList.size() > 0) {
			for (SubBudgetRate sub : subList) {
				SubBudgetRateMonth nextsubM = new SubBudgetRateMonth();
				nextsubM.setBookKDate(months);
				nextsubM.setName(sub.getName());
				nextsubM.setSubNumber(sub.getSubNumber());
				nextsubM.setBorrowYearBegingMoney(sub
						.getBorrowYearBegingMoney());
				nextsubM.setLendYearBegingMoney(sub.getLendYearBegingMoney());
				nextsubM.setBorrowYearSumMoney(sub.getBorrowYearSumMoney());
				nextsubM.setLendYearSumMoney(sub.getLendYearSumMoney());
				nextsubM.setBorrowQichuMoney(sub.getBorrowQichuMoney());
				nextsubM.setLendQichuMoney(sub.getLendQichuMoney());
				nextsubM.setBorrowMoney(sub.getBorrowMoney());
				nextsubM.setLendMoney(sub.getLendMoney());
				nextsubM.setBorrowJieyuMoney(sub.getBorrowJieyuMoney());
				nextsubM.setLendJieyuMoney(sub.getLendJieyuMoney());
				nextsubM.setAddTimeDate(Util.getDateTime());
				nextsubM.setFk_SubBudgetRateId(sub.getId());
				totalDao.save(nextsubM);

				// 将当前期末调换到期初
				sub.setBorrowQichuMoney(sub.getBorrowJieyuMoney());
				sub.setLendQichuMoney(sub.getLendJieyuMoney());
				// 将本期发生清零
				sub.setBorrowMoney(0D);
				sub.setLendMoney(0D);
				// 科目月份切换到下一个月
				sub.setBookKDate(Util.getNextMonth3(months));
				totalDao.update(sub);

			}
		}
		return "true";
	}

	@Override
	public List<SubBudgetRateMonth> findSubMonths(String months) {
		if (months == null || "".equals(months)) {
			months = (String) totalDao
					.getObjectByCondition("select bookKDate from SubBudgetRateMonth order by bookKDate desc");
		}
		return totalDao.query(" from SubBudgetRateMonth where bookKDate = ?",
				months);
	}

	@Override
	public String getNextSubNumber(Integer id) {
		SubBudgetRate sub = (SubBudgetRate) totalDao.get(SubBudgetRate.class,
				id);
		String subNumber = (String) totalDao.getObjectByCondition(
				"select max(subNumber) from SubBudgetRate where fatherId =? ",
				id);
		String number = sub.getSubNumber();
		if (subNumber == null || subNumber.length() == 0) {
			subNumber = number + "01";
		} else {
			String num = ((Integer.parseInt(subNumber
					.substring(number.length())) + 101) + "").substring(1);
			subNumber += num;
		}
		return subNumber;
	}

	@Override
	public Boolean SubBangUsers(Integer userId, Integer[] subId) {
		if (userId != null && subId != null && subId.length > 0) {
			Users user = (Users) totalDao.get(Users.class, userId);
			for (int i = 0; i < subId.length; i++) {
				SubBudgetRate sub = (SubBudgetRate) totalDao.get(
						SubBudgetRate.class, subId[i]);
				if (user != null && sub != null) {
					Set<Users> userSet = sub.getUserSet();
					if (userSet == null) {
						userSet = new HashSet<Users>();
					}
					if (userSet.contains(user)) {
						userSet.remove(user);
					} else {
						userSet.add(user);
						sub.setUserSet(userSet);
					}
				}
				if (!totalDao.update(sub)) {
					return false;
				}
			}
			return true;

		}
		return false;
	}
}
