package com.task.Server.sop;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.ManualOrderPlanTotal;

/**
 * 
 * @author 王晓飞
 *
 */
public interface ManualOrderPlanServer {
	public boolean addmaualPlan1(ManualOrderPlanDetail mod);
	/**
	 * 添加
	 * @param manualPlan
	 * @return
	 */
	String addmaualPlan(ManualOrderPlanDetail mod);
	
	String maxTotalNum();
	/**
	 * 添加多条物料需求
	 */
	String addMaulPlanMultpart(List<ManualOrderPlanDetail> modList,ManualOrderPlanTotal total);
	
	/**
	 * 根据id获得物料需求单
	 */
	ManualOrderPlanTotal getManaualOrderPlanTotalById(Integer id,String status);
	/**
	 * 查询物料需求单
	 * @param total
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param tag
	 * @return
	 */
	Object[] findManualPlanTotal(ManualOrderPlanTotal total,Integer pageNo,
			Integer pageSize,String status,String tag);
	/**
	 * 删除物料需求单
	 * @param id
	 * @return
	 */
	String delManualTotal(Integer id);
	/**
	 * 修改物料需求单
	 * @param details
	 * @param total
	 * @return
	 */
	String updateManualTotal(List<ManualOrderPlanDetail> details,ManualOrderPlanTotal total) throws Exception;
	
	/**
	 * 获得审批人签名
	 */
	Map<Integer, Object> findPayExecutionNode(Integer id);
	
	/**
	 * 解析物料需求文件
	 * list  msg
	 */
	Object[] analysisFromFile(File file);
	
	/**
	 * 解析辅料需求文件
	 * list  msg
	 */
	Object[] analysisflFromFile(File file);
	/**
	 * 查询
	 * @param maualPlan
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param endTime 
	 * @param startTime 
	 * @return
	 */
	public Object[] findmodnList(ManualOrderPlanDetail mod ,int pageNo,int pageSize,
			String status,String tag,String flag, String startTime, String endTime);
	/**
	 * 删除
	 * @param maualPlan
	 * @return
	 */
	public String delmod(ManualOrderPlanDetail mod);
	/**
	 * 修改
	 * @param maualPlan
	 * @return
	 */
	public String updatemod(ManualOrderPlanDetail mod);
	/**
	 * 根据Id查询
	 * @param id
	 * @return
	 */
	public ManualOrderPlanDetail findmaualPlanById(Integer id);

	/**
	 * 导入物料需求信息
	 */
	String	daorumaualPlan(File file,String category,int planType);
	/**
	 * 批量审核同意
	 */
	
	String plshehe(int[] ids);
	
	/**
	 * 查询所有物料需求汇总信息
	 */
	public Object[] chaxuanmopList(ManualOrderPlan mop ,int pageNo,int pageSize,String status,String tag, String flag, String daochu);
	//导出
	void exportExcel(ManualOrderPlan mop, int pageNo, int pageSize,
			String status, String tag, String flag);
	//导出
	void exportExcel0(ManualOrderPlanDetail mod1, int pageNo, int pageSize,
			String status, String tag, String flag);
	
	
	/**
	 * 导入辅料需求信息
	 */
	String	saveLotPlan(File file,String category);
	
	/**
	 * 
	 */
	ManualOrderPlanDetail getManualOrderPlanDetail(String markId);
	
	/**
	 * 取消物料需求
	 */
	String quexiao(Integer id);
	
	/**
	 * 获得Plan
	 */
	ManualOrderPlan getOrderPlanById(Integer id,String tag);
	
	/**
	 * 变更物料需求数量
	 * @param num 
	 */
	String changePlanNumber(ManualOrderPlan plan,Float num, String tag);
	public void text();
	
	/**
	 * 删除同一个订单下，所有未下采购单的需求明细。
	 */
	
	String delAllMod(Integer rootId);
	
}

	