package com.task.Server.caiwu.core;

import java.io.File;
import java.util.List;

import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.Sell;
import com.task.entity.caiwu.core.CorePayable;
import com.task.entity.caiwu.core.CorePayableMonth;
import com.task.entity.caiwu.core.MonthPayableBill;
import com.task.entity.caiwu.core.SupplierCorePayable;
import com.task.entity.payment.FundApply;
import com.task.entity.paymonth.PayMonth;
import com.task.entity.paymonth.PayMonthDetail;
import com.task.entity.sop.WaigouDeliveryDetail;

/****
 * 主营应付
 * 
 * @author liupei
 * 
 */
public interface CorePayableServer {
	/****
	 * 添加主营应付
	 * 
	 * @param detail
	 * @param goods
	 * @return
	 */
	boolean addCorePayable(WaigouDeliveryDetail detail, Goods goods);

	/****
	 * 主营应付月度对账单
	 * 
	 * @return
	 */
	Object[] findCorePayableMonthList(CorePayableMonth corePayableMonth,
			int pageNo, int pageSize, String pageStatus);

	/****
	 * 主营应付列表
	 * 
	 * @return
	 */
	Object[] findCorePaybleList(CorePayable corePayable, int pageNo,
			int pageSize, String pageStatus,String firstTime,String endTime);

	/****
	 * 批量核对账单
	 * 
	 * @param ids
	 * @param attachment
	 * @param attachmentFileName
	 * @return
	 */
	boolean gyshdzd(Integer[] ids, String pageStatus);

	/****
	 * 上传发票
	 * 
	 * @param corePayable
	 * @param attachment
	 * @param attachmentFileName
	 * @return
	 */
	boolean uploadFapiao(CorePayable corePayable, File attachment,
			String attachmentFileName);

	/****
	 * 批量上传发票
	 * 
	 * @param ids
	 * @param attachment
	 * @param attachmentFileName
	 * @return
	 */
	boolean uploadFapiao(Integer[] ids, String fapiaoNum, File attachment,
			String attachmentFileName);

	/****
	 * 发票复核
	 * 
	 * @param ids
	 * @param pageStatus
	 * @return
	 */
	boolean fpfh(Integer[] ids, String pageStatus);

	/****
	 * 供应商付款汇总表
	 * 
	 * @return
	 */
	Object[] findSupplierCorePayableList(
			SupplierCorePayable supplierCorePayable, int pageNo, int pageSize,
			String pageStatus);

	/****
	 * 申请付款
	 * 
	 * @param ids
	 * @param attachment
	 * @param attachmentFileName
	 * @return
	 */
	boolean shenqingfukuan(Integer[] ids, String pageStatus);

	/****
	 * 申请付款审批及调额
	 * 
	 * @param ids
	 * @param attachment
	 * @param attachmentFileName
	 * @return
	 */
	boolean fukuanAudit(List<CorePayable> list);

	/****
	 * 月度应收应付账单
	 * 
	 * @return
	 */
	Object[] findMonthPayableBillList(MonthPayableBill monthPayableBill,
			int pageNo, int pageSize, String pageStatus);

	/***
	 * 补充生成已记账的应付的月度账单以及凭证
	 * 
	 * @return
	 */
	String addOldCorePayable();

	/****
	 * 应付明细补充导入
	 * 
	 * @param attachment
	 * @return
	 */
	String addBcCorePayable(File attachment);

	/****
	 * 添加应付 （入库导入）
	 * 
	 * @param goodsStore
	 * @return
	 */
	boolean addCorePayable(GoodsStore goodsStore);

	boolean goodsSell(Sell sell);
	

	boolean zhuyinPingzheng(CorePayable corePayable);

	void chuliPir(Float zgBhsMoney);

	Object[] findSupplierCorePayablePerson(
			SupplierCorePayable supplierCorePayable, int pageNo, int pageSize,
			String pageStatus);
	void exprotCorePayable(CorePayable corePayable,String firstTime,String endTime);
	public List<PayMonthDetail> findPayMonthDetail(PayMonth payMonth,String firstTime,String endTime,String cangqu,Integer max);
	public String addPayMonth(PayMonth payMonth,List<PayMonthDetail> pmdList,String firstTime,String endTime);
	public PayMonth findPMbyId(Integer id);
	public List<PayMonthDetail> findPMDByid(PayMonth pm);
	public Object[] findPayMonth(PayMonth payMonth, int parseInt,int pageSize, String tag);
	public String delcp(PayMonth payMonth);
}