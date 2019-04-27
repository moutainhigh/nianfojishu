package com.task.Server;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.task.entity.AssScore;
import com.task.entity.Users;
import com.task.entity.Wage;
import com.task.entity.dmltry.DmlAppFileUrl;
import com.task.entity.payment.FundApply;

public interface WageServer {

	public boolean addWage(AssScore assScore);// 根据工号和卡号查找该用户工资信息

	public String exportWageArticle(Wage pageWage, String articleOrSingle,
			String wageStatus);// 导出工资条

	public Object[] findAllWage(int pageNo, int pageSize, String status,
			Wage wage);// 查询所有工资(分页)

	public Object[] findWageByCondition(Wage wage, int pageNo, int pageSize,
			String ststus);// 条件查询工资(分页)

	public String addTryPeople();// 添加试用人员

	public Wage findWageById(int id); // 根据id查询工资

	public List androidprintWage(String code, String mouth); // 根据id查询工资android

	public boolean updateWageByWageStandard();// 通过工资模板更新工资

	public boolean addChageWage(Wage newWage, Wage oldWage); // 添加变动工资

	public boolean updateWage(Wage wage, String status);// 修改工资

	public String showWageDetails(Wage wage);// 查看工资明细

	public Wage findWageByCodeAndCardId(String code, String cardId, String mouth); // 通过工号和卡号查询工资信息

	public boolean delWage(Wage wage); // 删除工资

	public boolean updateWage(Wage wage); // 更改工资

	public String uploadChageWage(File chageWageFile, String moth); // 上传变动工资

	public String uploadSbWage(File chageWageFile); // 上传变动工资

	public String batchAudit(int wageId[], String status); // 工资批量审核(同意/打回)

	public Object[] findAllWageByMouth(Wage wage);// 查询所有工资(无分页)

	public Object[] findUserAllWage(String code, String cardId, int pageNo,
			int pageSize);// 根据工号和卡号查询所有工资信息

	public Wage findUserSumWage(String code, String cardId); // 根据工号和卡号查询实发工资信息

	public String wageBalance(int addUserId); // 工资平衡(奖金分配计算工资)

	public String exportOutsourcing(String exportMouth);// 导出外包工资

	public String allWageBalance(String mouth);

	/***
	 * 每月工资处理 添加工资(承包奖金分配工资)
	 * 
	 * @param code
	 *            工号数组
	 * @param cardId
	 *            卡号数组
	 * @param money
	 *            金额数组
	 * @return boolean
	 */
	public String addWage(String[] code, String[] cardId, Float[] money,
			Float[] money1, String status);

	/***
	 * 通过登录人员职位查询所属成员的工资信息
	 * 
	 * @param duty
	 *            职位
	 * @return
	 */
	public Object[] findWageByMouthAndDept(String code, int pageNo, int pageSize);

	/***
	 * 根据登陆人工号和月份查询并更新承包部留
	 * 
	 * @param wage
	 *            工资对象
	 * @return
	 */
	public boolean updateBuliu(Wage wage);

	/***
	 * 检查工资人员信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkWage();

	/***
	 * 工资汇总
	 * 
	 * @param mouth
	 *            查询月份(为空则查询全部)
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List huizongWage(String mouth);

	/***
	 * 部门奖金分配
	 * 
	 * @param code
	 *            [] 工号
	 * @param money
	 *            [] 奖金
	 * @param status
	 * @return
	 */
	public String addBumenBonus(String[] code, Float[] money, String status);

	/***
	 * 提交状态为"自查"的工资信息为审核
	 * 
	 * @return
	 */
	public int submitWageAudit();

	
	public Map<Integer, Object> fourmouthgobzi(String code,int userid, int pageNo, int pageSize);
	
	/**
	 * 个人财务情况详细
	 */
	public Wage wage(String mouth);

	/***
	 * 添加离职人员工资
	 * 
	 * @param wage
	 * @return
	 */
	boolean addLeaveWage(Wage wage);

	/***
	 * 通过id查询工资明细
	 * 
	 * @param id
	 * @return
	 */
	Wage findWageById(Integer id);

	/***
	 * 生成凭证以及付款单
	 * 
	 * @param month
	 * @return
	 */
	boolean createCvAndReg(String month);

	/**
	 * 查询某个人的截止某一月份为止的奖金.
	 */
	List<Wage> getWageByUsers(Integer userId, String months);

	/**
	 * 跳转个人奖金考核折线图
	 * 
	 */
	Users getUsers(Integer id);

	/***
	 * 工资数据分析
	 * 
	 * @return
	 */
	Map<String, Object> findWageMonth(Integer userId, String pageStatus);
	

	
}
