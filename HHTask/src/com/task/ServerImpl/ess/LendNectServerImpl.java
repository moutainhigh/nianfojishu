package com.task.ServerImpl.ess;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.struts2.ServletActionContext;



import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.ess.GoodsStoreServer;
import com.task.Server.ess.LendNectServer;
import com.task.entity.ApplyScrap;
import com.task.entity.GoodHouse;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.Lend;
import com.task.entity.Nect;
import com.task.entity.Repay;
import com.task.entity.Scrap;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardTemplate;
import com.task.util.Util;

@SuppressWarnings("unchecked")
public class LendNectServerImpl implements LendNectServer{
	private TotalDao totalDao;
	private GoodsStoreServer goodsStoreServer;
	
	public Object[] findAAllLNGoods(Goods goods, String startDate,
			String endDate, int cpage, int pageSize, String tag,
			String pagestatus) {
		if(goods==null){
			goods=new Goods();
		}
		
		String quantity=" and goodsCurQuantity>0  ";//库存量设置
		
		if("lend".equals(tag)){
			quantity +="and lendNeckCount>0";
		}
		String hql = "from Goods ";
		String goodHouseName="";//仓区
		boolean isall = false;//权限
		
		goods.setGoodsClass("综合库");
		if("lend".equals(tag)){
			goods.setLendNeckStatus("借"); 
		}else if("nect".equals(tag)){
			goods.setLendNeckStatus("领");
		}else if("canChange".equals(tag)){
			goods.setLendNeckStatus("领");
			goods.setNectCanChangeStatus("是");
		}
		//仓区条件goodHouseName
		String hql_cq = "  goodHouseName in (";
		if (goods != null && goods.getGoodHouseName() != null
				&& goods.getGoodHouseName().length() > 0) {
			String str = "";
			String[] cangqus = goods.getGoodHouseName().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			goodHouseName = goods.getGoodHouseName();
			goods.setGoodHouseName(null);//?
			hql_cq += ")";
		} else {
			hql_cq = "";
		}
		//日期范围搜索
		String[] between = new String[2];
		if ( startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		if(goods!=null){
			hql = totalDao.criteriaQueries(goods, "goodsChangeTime", between,
					hql_cq);//时间仓区搜索
		}
		
		hql+=quantity;
		Object[] goodsAarr = new Object[4];
		Integer count = totalDao.getCount(hql);
		List<Goods> list = totalDao.findAllByPage(hql, cpage, pageSize);
//		if(list!=null && list.size()>0) {
//			StringBuffer buffer = null;
//			for (Goods goods2 : list) {
//				buffer = new StringBuffer("from ManualOrderPlanDetail where markId=? and epStatus='同意'");
//				String name = goods2.getGoodsFullName();
//				if(name!=null && !name.equals("")) {
//					buffer.append(" and proName='"+name+"'");
//				}
//				String banben = goods2.getBanBenNumber();
//				if(banben!=null && !banben.equals("")) {
//					buffer.append(" and banben= '"+banben+"'");
//				}
//				String goodsFormat = goods2.getGoodsFormat();
//				if(goodsFormat!=null && !goodsFormat.equals("")) {
//					buffer.append(" and specification = '"+goodsFormat+"'");
//				}
//				String goodsUnit = goods2.getGoodsUnit();
//				if(goodsUnit!=null && !goodsUnit.equals("")) {
//					buffer.append(" and unit = '"+goodsUnit+"'");
//				}
//				String kgliao = goods2.getKgliao();
//				if(kgliao!=null) {
//					buffer.append(" and kgliao = '"+kgliao+"'");
//				}
//				String wgType = goods2.getWgType();
//				if(wgType!=null && !wgType.equals("")) {
//					buffer.append(" and wgType = '"+wgType+"'");
//				}
//				List<ManualOrderPlanDetail> detailList = totalDao.query(buffer.toString(), goods2.getGoodsMarkId());
//				if(detailList!=null && detailList.size()>0) {
//					buffer = new StringBuffer();
//					for (ManualOrderPlanDetail detail : detailList) {
//						String demanddept = detail.getDemanddept();
//						if(demanddept!=null && !demanddept.equals("")) {
//							if(buffer.toString().equals("")) {
//								buffer.append(demanddept);
//							}else {
//								buffer.append(","+demanddept);
//							}
//						}
//						
//					}
//					
//					goods2.setDemanddept(buffer.toString());
//				}
//			}
//		}
			
		double sumcount = 0;//所有库存量累加
		for (Object obj : list) {
			Goods g = (Goods) obj;
			sumcount += g.getGoodsCurQuantity();
		}

		goodsAarr[0] = count;//共有多少条数据
		goodsAarr[1] = list;//每页库存数据
		goodsAarr[2] = sumcount;//所有库存量累加
		goodsAarr[3] = isall;
		
		goods.setGoodHouseName(goodHouseName);
		return goodsAarr;
	}
	
	
	
	//查找出借历史
	public Object[] findLendHistory(Lend lend, String startDate,
			String endDate, int cpage, int pageSize) {
		if(lend==null){
			lend =new Lend();
		}
		String hql="from Lend";
		String goodHouseName="";//仓区
		
		//日期范围搜索
		String[] between = new String[2];
		if (startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		//仓区条件goodHouseName
		String hql_cq = "  goodHouse in (";
		if (lend.getGoodHouse() != null && lend.getGoodHouse() != null
				&& lend.getGoodHouse().length() > 0) {
			String str = "";
			String[] cangqus = lend.getGoodHouse().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			goodHouseName = lend.getGoodHouse();
			lend.setGoodHouse(null);//?
			hql_cq += ")";
		} else {
			hql_cq = "";
		}
		
		
		
		if(lend!=null){
			hql = totalDao.criteriaQueries(lend, "date", between,
					hql_cq);//时间仓区搜索
		}
		hql+=" order by time desc";
		Object[] lends = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;//所有库存量累加
		for (Object obj : list) {
			Lend g = (Lend) obj;
			sumcount += g.getNum();
		}
		lends[0] = count;//共有多少条数据
		lends[1] = list;//每页库存数据
		lends[2] = sumcount;//所有库存量累加
		lends[3] = true;
		
		lend.setGoodHouse(goodHouseName);
		return lends;	
	}
	
	
	//查找领用历史
	public Object[] findNectHistory(Nect nect, String startDate,
			String endDate, int cpage, int pageSize,String status) {
		if(nect==null){
			nect=new Nect();
		}
			
		String hql="from Nect";
		String goodHouseName="";//仓区条件
		String canChange="";//可以旧换新条件
		//日期范围搜索
		String[] between = new String[2];
		if (startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		
		
		//仓区条件goodHouseName
		String hql_cq = "  goodHouse in (";
		if (nect != null && nect.getGoodHouse() != null
				&& nect.getGoodHouse().length() > 0) {
			String str = "";
			String[] cangqus = nect.getGoodHouse().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			goodHouseName = nect.getGoodHouse();
			nect.setGoodHouse(null);//
			hql_cq += ")";
			
		} else {
			hql_cq = "";
		}
		
		if(nect!=null){
			hql = totalDao.criteriaQueries(nect, "date", between,
					hql_cq);//时间仓区搜索
		}
		
		//是否存在以旧换新条件
		if(status!=null  && status.length()>0){
//			canChange="  canChangeNum>0";
//			hql=totalDao.criteriaQueries(nect, "date", between, canChange);
			hql +=" and canChangeNum>0";
		}
		
		
		
		if(nect.getCardNum()!=null && nect.getCardNum().length()>0){
			hql+=" and cardNum='"+nect.getCardNum()+"'";
		}
		Object[] nects = new Object[4];
		Integer count = totalDao.getCount(hql);
		
		hql+=" order by time desc";
		
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;//所有库存量累加
		for (Object obj : list) {
			Nect g = (Nect) obj;
			sumcount += g.getNum();
		}
		nects[0] = count;//共有多少条数据
		nects[1] = list;//每页库存数据
		nects[2] = sumcount;//所有库存量累加
		nects[3] = true;
		
		
		if(nect!=null){
			nect.setGoodHouse(goodHouseName);
		}
		
		return nects;
	}
	
	
	
	//根据卡号查找可以以旧换新历史记录
	public Object[] findCanChangeNewNects(Nect nect, String startDate,
			String endDate, int cpage, int pageSize,String status) {
		if(nect==null){
			nect=new Nect();
		}
			
		String hql="from Nect";
		String goodHouseName="";//仓区条件
		//日期范围搜索
		String[] between = new String[2];
		if (startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		
		
		//仓区条件goodHouseName
		String hql_cq = "  goodHouse in (";
		if (nect != null && nect.getGoodHouse() != null
				&& nect.getGoodHouse().length() > 0) {
			String str = "";
			String[] cangqus = nect.getGoodHouse().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			goodHouseName = nect.getGoodHouse();
			nect.setGoodHouse(null);//
			hql_cq += ")";
			
		} else {
			hql_cq = "";
		}
		
		if(nect!=null){
			hql = totalDao.criteriaQueries(nect, "date", between,
					hql_cq);//时间仓区搜索
			
			
		}
		hql+=" order by time desc";
		Object[] nects = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;//所有库存量累加
		for (Object obj : list) {
			Nect g = (Nect) obj;
			sumcount += g.getNum();
		}
		nects[0] = count;//共有多少条数据
		nects[1] = list;//每页库存数据
		nects[2] = sumcount;//所有库存量累加
		nects[3] = true;
		
		
		if(nect!=null){
			nect.setGoodHouse(goodHouseName);
		}
		
		return nects;
	}
	
	//查找归还历史记录
	public Object[] findRepayHistory(Repay repayHistory, String startDate,
			String endDate, int cpage, int pageSize) {
		if(repayHistory == null){
			repayHistory = new Repay();
		}
		String hql="from Repay";
		String goodHouseName="";
		
		
		//日期范围搜索
		String[] between = new String[2];
		if (startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		
		//仓区条件goodHouseName
		String hql_cq = "  goodHouse in (";
		if (repayHistory.getGoodHouse() != null && repayHistory.getGoodHouse() != null
				&& repayHistory.getGoodHouse().length() > 0) {
			String str = "";
			String[] cangqus = repayHistory.getGoodHouse().split(",");
			if (cangqus != null && cangqus.length > 0) {
				for (int i = 0; i < cangqus.length; i++) {
					str += ",'" + cangqus[i] + "'";
				}
				if (str.length() >= 1) {
					str = str.substring(1);
				}
				hql_cq += str;
			}
			goodHouseName = repayHistory.getGoodHouse();
			repayHistory.setGoodHouse(null);//
			hql_cq += ")";
			
		} else {
			hql_cq = "";
		}
		
		

		if(repayHistory!=null){
			hql = totalDao.criteriaQueries(repayHistory, "ldate", between,
					hql_cq);//时间仓区搜索
		}
		hql+=" order by rtime desc";
		Object[] obj = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;//所有库存量累加
		for (Object o : list) {
			Repay g = (Repay) o;
			sumcount += g.getNum();
		}
		obj[0] = count;//共有多少条数据
		obj[1] = list;//每页库存数据
		obj[2] = sumcount;//所有库存量累加
		obj[3] = true;
		
		repayHistory.setGoodHouse(goodHouseName);
		
		return obj;
	}
	//查找报废历史记录
	public Object[] findScrapHistory(ApplyScrap scrapHistory, String startDate,
			String endDate, int cpage, int pageSize) {
		if(scrapHistory==null){
			scrapHistory=new ApplyScrap();
		}
		String hql="from ApplyScrap";
		//日期范围搜索
		String[] between = new String[2];
		if (startDate !=null && endDate !=null  && !"".equals(endDate)
				&& !"".equals(startDate)) {
			between[0] = startDate;
			between[1] = endDate;
		}
		if(scrapHistory!=null){
			hql = totalDao.criteriaQueries(scrapHistory, "badDate", between,
					"");//时间搜索
			
		}
		hql+=" order by badTime desc";
		Object[] obj = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, cpage, pageSize);
		double sumcount = 0;//所有库存量累加
		for (Object o : list) {
			ApplyScrap g = (ApplyScrap) o;
			sumcount += g.getBadNum();
		}
		obj[0] = count;//共有多少条数据
		obj[1] = list;//每页库存数据
		obj[2] = sumcount;//所有库存量累加
		obj[3] = true;
		
		return obj;
	}
	
	
	public Users findUserByCardNum(String cardNum) {
		List<Users> l = totalDao.query("from Users u where u.cardId = ? ",
				cardNum);
		if (l != null && l.size() > 0) {
			Users users = l.get(0);
			return users;
		}
		return null;
	}
	//插入外借记录
	public boolean insertLend(Lend lend, Goods good) {
		boolean lFlag = false,gFlag=false,cFlag=true;
		if(lend!=null && good!=null){
			lend.setState("未还");
			lend.setGiveBackNum(lend.getNum());
			Date now=new Date();
			String lendTime=Util.DateToString(now, "yyyy-MM-dd HH:mm:ss");
			String lendDate=Util.DateToString(now, "yyyy-MM-dd");
			lend.setDate(lendDate);
			lend.setTime(lendTime);
			if("供应商".equals(lend.getDept())){
				lend.setPlace(lend.getDept()+"_"+lend.getPeopleName()+"_"+lend.getUseingCode());
			}
			lFlag=totalDao.save(lend);
			if(lFlag){//外借成功
				Goods g=(Goods)totalDao.get(Goods.class, good.getGoodsId());//数据库对象
				if(g!=null){
					if(g.getLendNeckCount()<lend.getNum()){
						return false;
					}else{
						//更新可借用量
						g.setLendNeckCount(g.getLendNeckCount()-lend.getNum());
						gFlag=totalDao.update(g);
						//更新出库面积---判断仓区，成品面积有才更新仓区占地面积
						//仓区
						GoodHouse goodHouse=(GoodHouse)totalDao.get("from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",new Object[]{g.getGoodsClass(),g.getGoodHouseName()});
						//单件
						ProcardTemplate procardArea=(ProcardTemplate)totalDao.get("from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='') and productStyle='批产' ", new Object[]{g.getGoodsMarkId()});
						if(goodHouse!=null && procardArea!=null){
							//存在面积
							if(goodHouse.getGoodAllArea()>0 && goodHouse.getGoodAllArea()!=null && procardArea.getActualUsedProcardArea()!=null && procardArea.getActualUsedProcardArea() >0){
								//已用面积减少
								Double procardActualUsed=procardArea.getActualUsedProcardArea()*lend.getNum();
								procardActualUsed=Double.valueOf(new DecimalFormat("0.00").format(procardActualUsed));
								if(procardActualUsed>=goodHouse.getGoodIsUsedArea()){
									goodHouse.setGoodIsUsedArea(0D);
									goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
								}else{
									Double use=Double.valueOf(new DecimalFormat("0.00").format(goodHouse.getGoodIsUsedArea()-procardActualUsed));
									goodHouse.setGoodIsUsedArea(use);
									goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
								}	
								cFlag=totalDao.update(goodHouse);
								
							}
						}
					}
					Gzstore gz =	(Gzstore) totalDao.getObjectByCondition(" from Gzstore where fk_stoid =? ", good.getGoodsId());
					if(gz!=null){
						gz.setParClass(lend.getPlace());
						totalDao.update(gz);
					}
					
					
				}
			}
			
			
		}
		
		return cFlag && gFlag && lFlag;
	}
	@Override
	//插入领用记录
	public boolean insertNect(Nect nect, Goods good) {
		boolean nFlag=false,gFlag=false,cFlag=true,chuKuFlag=false;
		Sell sell=new Sell();
		if(nect!=null && good!=null){
			Date now=new Date();
			String nectTime=Util.DateToString(now, "yyyy-MM-dd HH:mm:ss");
			String nectDate=Util.DateToString(now, "yyyy-MM-dd");
			nect.setDate(nectDate);
			nect.setTime(nectTime);
			nect.setCanChangeNum(nect.getNum());
			nect.setStatus("直接领用");
			//1)保存领用对象
			nFlag=totalDao.save(nect);
			
			Goods g=(Goods)totalDao.get(Goods.class, good.getGoodsId());//数据库对象
			if(g!=null){
				if(g.getGoodsCurQuantity()<nect.getNum()){
					return false;
				}else{
					//2)更新库存
					//g.setLendNeckCount(g.getLendNeckCount()-nect.getNum());
					g.setGoodsCurQuantity(g.getGoodsCurQuantity()-nect.getNum());
					
					gFlag=totalDao.update(g);
					if(gFlag){
						//3)插入一条出库记录
						sell.setSellWarehouse(g.getGoodsClass());
						sell.setGoodHouseName(g.getGoodHouseName());
						sell.setKuwei(g.getGoodsPosition());
						sell.setSellMarkId(g.getGoodsMarkId());
						sell.setBanBenNumber(g.getBanBenNumber());
						sell.setKgliao(g.getKgliao());
						sell.setSellLot(g.getGoodsLotId());
						sell.setWgType(g.getWgType());
						sell.setSellGoods(g.getGoodsFullName());
						sell.setSellFormat(g.getGoodsFormat());
						sell.setSellCount(nect.getNum());// 数量
						sell.setSellUnit(g.getGoodsUnit());
						sell.setSellZhishu(g.getGoodsZhishu());
						sell.setGoodsStoreZHUnit(g.getGoodsStoreZHUnit());
						sell.setSellSupplier(g.getGoodsSupplier());
						sell.setSellCompanyName(g.getGoodsCustomer());
						sell.setCustomerId(g.getCustomerId());
						sell.setSupplierId(g.getSupplierId());
						sell.setSellDate(nect.getDate());
						sell.setSellTime(nect.getTime());
						sell.setSellLuId(g.getDemanddept());//需求部门
						sell.setSellPrice(g.getGoodsBuyPrice());// 采购批次单价(含税)
						sell.setSellbhsPrice(g.getGoodsBuyBhsPrice());//批次 不含税单价
						sell.setTaxprice(g.getTaxprice());//税率
						sell.setSellGoodsMore("库存领用");
						sell.setSellAdminName(nect.getAdmin());
						sell.setSellGetGoodsMan(nect.getAdmin());
						sell.setSellArtsCard(g.getGoodsArtsCard());
						sell.setSellProMarkId(g.getGoodsProMarkId());
						if(good.getGongxuNum()!=null){
							sell.setProcessNo(Integer.parseInt(g.getGongxuNum()));
						}
						sell.setBout(g.getBout());
						sell.setBedit(g.getBedit());
						sell.setOrderNum(g.getNeiorderId());
						sell.setOutOrderNumer(g.getWaiorderId());
						sell.setSellCharger(nect.getPeopleName());//领料人
						sell.setSellchardept(nect.getDept());//领料人部门
						sell.setYwmarkId(g.getYwmarkId());
						sell.setKuwei(g.getGoodsPosition());
						chuKuFlag=totalDao.save(sell);
						
					}
					//4）更新出库面积---判断仓区，成品面积有才更新仓区占地面积
					//仓区
					GoodHouse goodHouse=(GoodHouse)totalDao.get("from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",new Object[]{g.getGoodsClass(),g.getGoodHouseName()});
					//单件
					ProcardTemplate procardArea=(ProcardTemplate)totalDao.get("from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='') and productStyle='批产' ", new Object[]{g.getGoodsMarkId()});
					if(goodHouse!=null && procardArea!=null){
						//存在面积
						if(goodHouse.getGoodAllArea()>0 && goodHouse.getGoodAllArea()!=null && procardArea.getActualUsedProcardArea()!=null && procardArea.getActualUsedProcardArea() >0){
							//已用面积减少
							Double procardActualUsed=procardArea.getActualUsedProcardArea()*nect.getNum();
							procardActualUsed=Double.valueOf(new DecimalFormat("0.00").format(procardActualUsed));
							if(procardActualUsed>=goodHouse.getGoodIsUsedArea()){
								goodHouse.setGoodIsUsedArea(0D);
								goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
							}else{
								Double use=Double.valueOf(new DecimalFormat("0.00").format(goodHouse.getGoodIsUsedArea()-procardActualUsed));
								goodHouse.setGoodIsUsedArea(use);
								goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
							}	
							cFlag=totalDao.update(goodHouse);
							
						}
					}
				}
			}
		}
		return cFlag && gFlag && nFlag  && chuKuFlag;
	}
	//插入以旧换新记录
	public Object[] insertOneChangeNew(Nect nect, Goods goods, Users user,Float num,Nect newNect) {
		String msg="";
		boolean nFlag=false,newNectFlag=false,gFlag=false,chuKuFlag=false,areaFlag=true;
		newNect=new Nect();
		Date now=new Date();
		Sell sell=new Sell();
		String time=Util.DateToString(now,"yyyy-MM-dd HH:mm:ss");
		String date=Util.DateToString(now,"yyyy-MM-dd");

		newNect.setGoodsMarkId(nect.getGoodsMarkId());
		newNect.setGoodsLotId(goods.getGoodsLotId());
		newNect.setGoodsFullName(goods.getGoodsFullName());

		newNect.setNum(num);
		newNect.setUnit(goods.getGoodsUnit());
		newNect.setFormat(goods.getGoodsFormat());
		newNect.setWgType(goods.getWgType());
		newNect.setStorehouse(goods.getGoodsClass());
		newNect.setGoodHouse(goods.getGoodHouseName());
		newNect.setWareHouse(goods.getGoodsPosition());
		newNect.setDate(date);
		newNect.setTime(time);
		newNect.setRemark("以旧换新领用");
		newNect.setCardNum(nect.getCardNum());
		newNect.setPeopleName(nect.getPeopleName());
		newNect.setDept(nect.getDept());
		newNect.setPower(nect.getPower());
		newNect.setGoodsId(goods.getGoodsId());
		newNect.setAdminId(user.getId());
		newNect.setAdmin(user.getName());
		newNect.setProcessPieceNum("");
		newNect.setStatus("以旧换新领");
		newNect.setCanChangeNum(num);
		if(nect!=null && goods!=null && num!=null){
			if(num<=0){
				msg="请检查可换数量";
				return new Object[]{newNect,msg};
			}
			//1)更新领用表（可换数量）
			Float canChangeNum=nect.getCanChangeNum()-num;
			if(canChangeNum<0){
				msg="申请以旧 换新数量大于可换数量，请检查";
			}else{
				nect.setCanChangeNum(nect.getCanChangeNum()-num);
				nFlag=totalDao.update(nect);
				if(nFlag){
					//2)旧产品入废品库
					GoodsStore goodStore=new GoodsStore();
					goodStore.setGoodsStoreWarehouse("废品库");
					//private String goodHouseName;// 仓区*************
					goodStore.setGoodHouseName("");
					//private String goodsStorePosition;// 库位
					goodStore.setGoodsStorePosition("");
					//private Integer kuweiId;// 库位id
					goodStore.setKuweiId(null);
					goodStore.setGoodsStoreMarkId(nect.getGoodsMarkId());
					Goods nectGoods=(Goods) totalDao.get(Goods.class, nect.getGoodsId());
					
					goodStore.setBanBenNumber(nectGoods.getBanBenNumber());
					goodStore.setKgliao(nectGoods.getKgliao());
					goodStore.setGoodsStoreLot(nectGoods.getGoodsLotId());
					//private Float hsPrice;// 单价(含税)********采购批次单价(含税)
					goodStore.setHsPrice(nectGoods.getGoodsPrice()==null?null:nectGoods.getGoodsPrice().doubleValue());
					goodStore.setGoodsStoreGoodsName(nectGoods.getGoodsFullName());
					goodStore.setGoodsStoreFormat(nectGoods.getGoodsFormat());
					goodStore.setWgType(nectGoods.getWgType());
					goodStore.setGoodsStoreUnit(nectGoods.getGoodsUnit());
					//private Float goodsStoreCount;// 数量***************总数
					goodStore.setGoodsStoreCount(num);
					goodStore.setGoodsStoreZhishu(nectGoods.getGoodsZhishu());
					goodStore.setGoodsStoreZHUnit(nectGoods.getGoodsStoreZHUnit());
					//private Float goodsStorePrice;// 单价(不含税)***************
					//private Double taxprice; // 税率***************
					//private Float money;// 总额**************								
					//private Integer priceId;// 价格id********************
					goodStore.setGoodsStoreCompanyName(nectGoods.getGoodsCustomer());
					goodStore.setGoodsStoreSupplier(nectGoods.getGoodsSupplier());
					goodStore.setGysId(nectGoods.getSupplierId());
					goodStore.setCustomerId(nectGoods.getCustomerId());
					//private String goodsStoreDate;// 时间***************
			
					
					goodStore.setGoodsStoreDate(date);
					//private String goodsStoreTime;// 系统默认入库时间**************
					goodStore.setGoodsStoreTime(time);
					
					//private Float goodsStoreTotal;// 总数 ***************数量
					goodStore.setGoodsStoreLuId(nectGoods.getGoodsCode());
					//private String goodsStoreTechnologeIf;//**************
					//private String goodsStoreFapiaoId;// 发票号**************
					
					//private String goodsStoreHrtongId;// 合同号**************
					
					//private String goodsStoreUseful;// 用途
					
					//private String goodsStoreNumber;// 申请单编号***************
					
					//private String goodsStorePerson;// 负责人*********
					goodStore.setGoodsStorePerson(nect.getPeopleName());
					goodStore.setGoodsStoreGoodsMore("以旧换新申请入库");
					goodStore.setGoodsStoreAdminId(user.getId());
					goodStore.setGoodsStoreAdminName(user.getName());
					//private String goodsStoreCharger;// 负责人**************
					//private String goodsStorePlanner;// 计划员************
					//private String goodsStorePiaoId;// 票号**********
					goodStore.setGoodsStoreMarkId(nectGoods.getGoodsFormat());
					goodStore.setGoodsStoreArtsCard(nectGoods.getGoodsArtsCard());
					goodStore.setGoodsStoreProMarkId(nectGoods.getGoodsProMarkId());

					//private Integer wwddId;// 送货单明细Id;******************
					
					//private String goodsStoreSendId;// 送货单号***********
					//private Integer osrecordId;// 检验id*************
					//private Float beginning_num;// 期初数量*************
					//private Float contrast_num;// 对比数量**************
					//private String baoxiao_status;// 报销状态(未报完、已报销)***********
					goodStore.setGoodsStorelasttime(nectGoods.getGoodslasttime());
					
					goodStore.setGoodsStorenexttime(nectGoods.getGoodsnexttime());
					//private Float goodsStoreround;// 质检周期*(*************
					goodStore.setTuhao(nectGoods.getTuhao());
					goodStore.setNeiorderId(nectGoods.getNeiorderId());
					goodStore.setWaiorderId(nectGoods.getWaiorderId());
					goodStore.setOrder_Id(nectGoods.getOrder_Id());
					//private String inputSource;// 入库来源（手动入库(来源为手动入库时出库关联的订单id为order_Id)，生产入库(默认为生产入库)）手动入库******
					goodStore.setInputSource("以旧换新入库");
					//private Float deleteZt;// 减去对应在途数量
					goodStore.setDeleteZt(0F);
					//private Integer izabId;// 正式订单与备货内部计划关系表**************
//					if(good.getGongxuNum()!=null || good.getGongxuNum()!="" || good.getGongxuNum().length()>0){
//						
//						System.out.println("-------------------good.getGongxuNum()"+good.getGongxuNum());
//						goodStore.setProcessNo(Integer.parseInt(good.getGongxuNum()));
//					}
					
					
					goodStore.setStatus("入库");
					goodStore.setApplyTime(time);
					goodStore.setPrintStatus("YES");
					goodStore.setSqUsersName(nect.getPeopleName());
					String hql="from Users where cardId=?";
					goodStore.setSqUsrsId(user.getId());
					goodStore.setSqUsersCode(user.getCode());
					goodStore.setSqUsersdept(user.getDept());
					goodStore.setYwmarkId(nectGoods.getYwmarkId());
					goodStore.setGoodsStoreMarkId(nectGoods.getGoodsMarkId());
					goodStore.setProcessName(nectGoods.getGongxuName());
						goodsStoreServer.saveSgrk(goodStore);
						msg="旧产品入报废库成功";
						
						//3)领用新产品
						newNectFlag=totalDao.save(newNect);
						if(newNectFlag){

							//4)更新库存
							//goods.setLendNeckCount(goods.getLendNeckCount()-nect.getNum());
							goods =(Goods)totalDao.get(goods.getClass(), goods.getGoodsId());
							if(goods.getGoodsCurQuantity()-num<0){
								msg="库存不足，请检查可换数量";
							}else{
								goods.setGoodsCurQuantity(goods.getGoodsCurQuantity()-num);
								gFlag=totalDao.update(goods);
							}
							
							
							
							if(gFlag){
								//5)插入一条出库记录
								sell.setSellWarehouse(goods.getGoodsClass());
								sell.setGoodHouseName(goods.getGoodHouseName());
								sell.setKuwei(goods.getGoodsPosition());
								sell.setSellMarkId(goods.getGoodsMarkId());
								sell.setBanBenNumber(goods.getBanBenNumber());
								sell.setKgliao(goods.getKgliao());
								sell.setSellLot(goods.getGoodsLotId());
								sell.setWgType(goods.getWgType());
								sell.setSellGoods(goods.getGoodsFullName());
								sell.setSellFormat(goods.getGoodsFormat());
								sell.setSellCount(num);// 数量
								sell.setSellUnit(goods.getGoodsUnit());
								sell.setSellZhishu(goods.getGoodsZhishu());
								sell.setGoodsStoreZHUnit(goods.getGoodsStoreZHUnit());
								sell.setSellSupplier(goods.getGoodsSupplier());
								sell.setSellCompanyName(goods.getGoodsCustomer());
								sell.setCustomerId(goods.getCustomerId());
								sell.setSupplierId(goods.getSupplierId());
								
								sell.setSellDate(date);
								sell.setSellTime(time);
								//private String sellCompanyPeople;// 客户方负责人*************
								
								//private String sellNumber; // 编号***********************
								
								//private String sellFapiao; // 发票号********************
								
								//private String sellInvoice; // 发票**************
								
								//private String sellPaydate; // 回款日期**************
								
								sell.setSellLuId(goods.getGoodsCode());
								//private String sellUsefull; // 用途***************

								//private Float sellTotal; // 总数******************数量
								sell.setSellPrice(goods.getGoodsBuyPrice());
								//private Integer sellReturnCount;// 返回入库数*********************
								sell.setSellGoodsMore("库存以旧换新领用");
								sell.setSellAdminName(user.getName());
								
								
								sell.setSellGetGoodsMan(user.getName());
								//private String sellPlanner; // 计划员***************
								sell.setSellCharger(goods.getLingliaocardId());
								//private String sellMarkFormat; // 合成字段***************
								sell.setSellArtsCard(goods.getGoodsArtsCard());
								sell.setSellProMarkId(goods.getGoodsProMarkId());
								
								//工序号
								if(goods.getGongxuNum()!=null){
									sell.setProcessNo(Integer.parseInt(goods.getGongxuNum()));
								}
								sell.setBout(goods.getBout());
								sell.setBedit(goods.getBedit());
								sell.setOrderNum(goods.getNeiorderId());
								sell.setOutOrderNumer(goods.getWaiorderId());
								sell.setLingliaocardId(goods.getLingliaocardId());
								sell.setLingliaoName(goods.getLingliaoName());
								if(goods.getLingliaoUserId()!=null){
									sell.setLingliaoUserId(goods.getLingliaoUserId());
								}
								
								sell.setYwmarkId(goods.getYwmarkId());
								sell.setKuwei("");
								chuKuFlag=totalDao.save(sell);
								
							}
							//4）更新出库面积---判断仓区，成品面积有才更新仓区占地面积
							if(chuKuFlag){

								//仓区
								GoodHouse goodHouse=(GoodHouse)totalDao.get("from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",new Object[]{goods.getGoodsClass(),goods.getGoodHouseName()});
								//单件
								ProcardTemplate procardArea=(ProcardTemplate)totalDao.get("from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='') and productStyle='批产' ", new Object[]{goods.getGoodsMarkId()});
								if(goodHouse!=null && procardArea!=null){
									//存在面积
									if(goodHouse.getGoodAllArea()>0 && goodHouse.getGoodAllArea()!=null && procardArea.getActualUsedProcardArea()!=null && procardArea.getActualUsedProcardArea() >0){
										//已用面积减少
										Double procardActualUsed=procardArea.getActualUsedProcardArea()*nect.getNum();
										procardActualUsed=Double.valueOf(new DecimalFormat("0.00").format(procardActualUsed));
										if(procardActualUsed>=goodHouse.getGoodIsUsedArea()){
											goodHouse.setGoodIsUsedArea(0D);
											goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
										}else{
											Double use=Double.valueOf(new DecimalFormat("0.00").format(goodHouse.getGoodIsUsedArea()-procardActualUsed));
											goodHouse.setGoodIsUsedArea(use);
											goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
										}	
										areaFlag=totalDao.update(goodHouse);
										
									}
									
								}
							}
						}else{
							msg="新产品领用失败";
						}
						if(chuKuFlag){
							msg = "以旧换新领用成功";
							return new Object[]{newNect,msg};
						}
						return new Object[]{newNect,msg};
					
				}
				
			}
			
			
			
			
		}
		return new Object[]{newNect,msg};
	}
	public Object[] findIsLentByCNum(Lend lend, int pageSize,int cpage) {
		String hql="from Lend where state='未还' and cardNum='"+lend.getCardNum()+ "' order by time desc";
		Object[] lends = new Object[4];
		Integer count = totalDao.getCount(hql);
		List list = totalDao.query(hql);
		double sumcount = 0;//所有库存量累加
		lends[0] = count;//共有多少条数据
		lends[1] = list;//每页库存数据
		lends[2] = sumcount;//所有库存量累加
		lends[3] = true;
		return lends;
	}
	// 插入某条归还记录
	public String insertOneRepay(Repay repay) {
		String msg="";
		boolean rFlag,lFlag,gFlag;
		if(repay.getNum()<=0){
			msg = "归还失败!数量不能等于0";
			return msg;
		}
		Lend lend=(Lend)totalDao.get(Lend.class,repay.getLendId());
		Goods good=(Goods)totalDao.get(Goods.class,repay.getGoodsId());
		if(lend!=null){
			if(lend.getNum()>=repay.getNum()){
				Date now=new Date();
				String rtime=Util.DateToString(now,"yyyy-MM-dd HH:mm:ss");
				String rdate=Util.DateToString(now,"yyyy-MM-dd");
				repay.setRdate(rdate);
				repay.setRtime(rtime);
				//归还表插值
				 rFlag=totalDao.save(repay);
				//借用表更新
				//1)更新未归还数量
				if(rFlag){
					//lend.setNum(lend.getNum()-repay.getNum());
					Float notGive=lend.getGiveBackNum()-repay.getNum();//未归还数量
					if(notGive<=0){//已还清
						lend.setGiveBackNum(0F);
						lend.setState("已还");
					}else{//未还清
						lend.setGiveBackNum(notGive);
					}
					lFlag=totalDao.update(lend);
					//库存表更新
					if(lFlag){
						//1)更新库存可借领数量
						good.setLendNeckCount(good.getLendNeckCount()+repay.getNum());
						//2)更新库存仓区面积********************
						//判断仓区，成品面积有才更新仓区占地面积
						//仓区
						GoodHouse goodHouse=(GoodHouse)totalDao.get("from  GoodHouse where goodsStoreWarehouse=? and goodHouseName=? and goodAllArea is not null  and goodIsUsedArea is not null",new Object[]{good.getGoodsClass(),good.getGoodHouseName()});
						//单件
						ProcardTemplate procardArea=(ProcardTemplate)totalDao.get("from ProcardTemplate where markId=? and (dataStatus!='删除' or dataStatus is  null)  and (banbenStatus='默认' or banbenStatus is null  or banbenStatus='')  and productStyle='批产' ", new Object[]{good.getGoodsMarkId()});
						if(goodHouse!=null && procardArea!=null){
							//存在面积
							if(goodHouse.getGoodAllArea()>0 && goodHouse.getGoodAllArea()!=null && procardArea.getActualUsedProcardArea()!=null && procardArea.getActualUsedProcardArea()>0){
								//单件占地面积goodsStoreCount
								Double procardActualUsed=procardArea.getActualUsedProcardArea()*repay.getNum();
								procardActualUsed=Double.valueOf(new DecimalFormat("0.00").format(procardActualUsed+goodHouse.getGoodIsUsedArea()));
								goodHouse.setGoodIsUsedArea(procardActualUsed);
								goodHouse.setGoodLeaveArea(goodHouse.getGoodAllArea()-goodHouse.getGoodIsUsedArea());
								totalDao.update(goodHouse);
							}
						}
						
						gFlag=totalDao.update(good);
						if(gFlag){
							msg="归还成功";
							return msg;
						}else{
							msg = "库存表更新可借数量错误，请检查";
							return msg;
						}
					}else{
						msg = "借用表更新可借数量错误，请检查";
						return msg;
					}
				}else{
					msg = "归还表插值错误，请检查";
					return msg;
				}
				
			}else{
				msg = "归还数量不能大于借用数量，请重输!";
				return msg;
			}
		}else{
			msg = "未找到相关借用记录，请刷新重试!";
			return msg;
		}
		
	}
	//插入申请报废记录
	public String insertOneScrap(ApplyScrap scrap){
		String msg="";
		boolean sFlag = false,lFlag = false,gFlag = false,chuKuFlag = false;
		if(scrap.getBadNum()<=0){
			msg = "申请报废失败!数量不能等于0";
			return msg;
		}
		Lend lend=(Lend)totalDao.get(Lend.class,scrap.getLendId());
		Goods good=(Goods)totalDao.get(Goods.class,scrap.getGoodsId());
		Sell sell=new Sell();
		if(lend!=null){
			if(lend.getGiveBackNum()>=scrap.getBadNum()){
				//1)申请报废表插值
				Date now=new Date();
				String badTime=Util.DateToString(now,"yyyy-MM-dd HH:mm:ss");
				String badDate=Util.DateToString(now,"yyyy-MM-dd");
				scrap.setBadDate(badDate);
				scrap.setBadTime(badTime);
				sFlag=totalDao.save(scrap);
				if(sFlag){
					Float notGive=lend.getGiveBackNum()-scrap.getBadNum();//未归还数量
					if(notGive<=0){//已还清
						lend.setGiveBackNum(lend.getNum());
						lend.setState("已还");
					}else{//未还清
						lend.setGiveBackNum(notGive);
					}
					//2)更新借用表
					lFlag=totalDao.update(lend);
					
					if(lFlag){
						//3)更新库存表
						//good.setLendNeckCount(good.getLendNeckCount()-scrap.getBadNum());//???????????????
						good.setGoodsCurQuantity(good.getGoodsCurQuantity()-scrap.getBadNum());
						gFlag=totalDao.update(good);
						if(gFlag){
							//4)出库记录表插值
							sell.setSellWarehouse(good.getGoodsClass());
							sell.setGoodHouseName(good.getGoodHouseName());
							sell.setKuwei(good.getGoodsPosition());
							sell.setSellMarkId(good.getGoodsMarkId());
							sell.setBanBenNumber(good.getBanBenNumber());
							sell.setKgliao(good.getKgliao());
							sell.setSellLot(good.getGoodsLotId());
							sell.setWgType(good.getWgType());
							sell.setSellGoods(good.getGoodsFullName());
							sell.setSellFormat(good.getGoodsFormat());
							sell.setSellCount(scrap.getBadNum());// 数量
							sell.setSellUnit(good.getGoodsUnit());
							sell.setSellZhishu(good.getGoodsZhishu());
							sell.setGoodsStoreZHUnit(good.getGoodsStoreZHUnit());
							sell.setSellSupplier(good.getGoodsSupplier());
							sell.setSellCompanyName(good.getGoodsCustomer());
							sell.setCustomerId(good.getCustomerId());
							sell.setSupplierId(good.getSupplierId());
							
							
						
							//****************设置出库日期
							
							 sell.setSellDate(scrap.getBadDate());
							 sell.setSellTime(scrap.getBadTime());
							//private String sellCompanyPeople;// 客户方负责人*************
							
							//private String sellNumber; // 编号***********************
							
							//private String sellFapiao; // 发票号********************
							
							//private String sellInvoice; // 发票**************
							
							//private String sellPaydate; // 回款日期**************
							
							sell.setSellLuId(good.getGoodsCode());
							//private String sellUsefull; // 用途***************

							//private Float sellTotal; // 总数******************数量
							sell.setSellPrice(good.getGoodsBuyPrice());
							//private Integer sellReturnCount;// 返回入库数*********************
							sell.setSellGoodsMore("归还申请报废");
							sell.setSellAdminName(scrap.getAdmin());
							
							
							sell.setSellGetGoodsMan(scrap.getAdmin());
							//private String sellPlanner; // 计划员***************
							sell.setSellCharger(good.getLingliaocardId());
							//private String sellMarkFormat; // 合成字段***************
							sell.setSellArtsCard(good.getGoodsArtsCard());
							sell.setSellProMarkId(good.getGoodsProMarkId());
							
							//private String sellHkId;// 追款编号**************
							
							//private String sellSendnum;// 送货编号*********

							//private Float sellSendCost;// 运费（同送货编号总费用）
							
							//private String fproductno;// BTS条码
							
							//private String tuhao;//
							
							//工序号
							if(good.getGongxuNum()!=null){
								sell.setProcessNo(Integer.parseInt(good.getGongxuNum()));
							}
							
							//private String planID;// 计划单号**************
							
							//private String style;// 出库类型（正常出库/返修出库/退料出库）****************

							sell.setBout(good.getBout());
							sell.setBedit(good.getBedit());
							//private String printStatus;// 打印状态(YES/NO)****************
							sell.setOrderNum(good.getNeiorderId());
							sell.setOutOrderNumer(good.getWaiorderId());
							//private String rootSelfCard;//总成批次*******************
							
							//private Integer wgOrderId;//采购订单id************
							
							sell.setLingliaocardId(good.getLingliaocardId());
							sell.setLingliaoName(good.getLingliaoName());
							if(good.getLingliaoUserId()!=null){
								sell.setLingliaoUserId(good.getLingliaoUserId());
							}
							
							sell.setYwmarkId(good.getYwmarkId());
							sell.setKuwei("");
//							Util.DateToString(date, timeFormat)
							//sell.setSellDate("");
							chuKuFlag=totalDao.save(sell);
							if(chuKuFlag){
								//5)入【废品库】
								GoodsStore goodStore=new GoodsStore();
								goodStore.setGoodsStoreWarehouse("废品库");
								//private String goodHouseName;// 仓区*************
								goodStore.setGoodHouseName("");
								//private String goodsStorePosition;// 库位
								goodStore.setGoodsStorePosition("");
								//private Integer kuweiId;// 库位id
								goodStore.setKuweiId(null);
								goodStore.setGoodsStoreMarkId(good.getGoodsMarkId());
								goodStore.setBanBenNumber(good.getBanBenNumber());
								goodStore.setKgliao(good.getKgliao());
								goodStore.setGoodsStoreLot(good.getGoodsLotId());
								//private Float hsPrice;// 单价(含税)********采购批次单价(含税)
								goodStore.setHsPrice(good.getGoodsPrice()==null?null:good.getGoodsPrice().doubleValue());
								goodStore.setGoodsStoreGoodsName(good.getGoodsFullName());
								goodStore.setGoodsStoreFormat(good.getGoodsFormat());
								goodStore.setWgType(good.getWgType());
								goodStore.setGoodsStoreUnit(good.getGoodsUnit());
								//private Float goodsStoreCount;// 数量***************总数
								goodStore.setGoodsStoreCount(sell.getSellCount());
								goodStore.setGoodsStoreZhishu(good.getGoodsZhishu());
								goodStore.setGoodsStoreZHUnit(good.getGoodsStoreZHUnit());
								goodStore.setGoodsStorePrice(good.getGoodsPrice()==null?null:good.getGoodsPrice().doubleValue());
								//private Double taxprice; // 税率***************
								//private Float money;// 总额**************								
								//private Integer priceId;// 价格id********************
								goodStore.setGoodsStoreCompanyName(good.getGoodsCustomer());
								goodStore.setGoodsStoreSupplier(good.getGoodsSupplier());
								goodStore.setGysId(good.getSupplierId());
								goodStore.setCustomerId(good.getCustomerId());
								//private String goodsStoreDate;// 时间***************
								goodStore.setGoodsStoreDate(sell.getSellDate());
								//private String goodsStoreTime;// 系统默认入库时间**************
								goodStore.setGoodsStoreTime(sell.getSellTime());
								
								//private Float goodsStoreTotal;// 总数 ***************数量
								goodStore.setGoodsStoreLuId(good.getGoodsCode());
								//private String goodsStoreTechnologeIf;//**************
								//private String goodsStoreFapiaoId;// 发票号**************
								
								//private String goodsStoreHrtongId;// 合同号**************
								
								//private String goodsStoreUseful;// 用途
								
								//private String goodsStoreNumber;// 申请单编号***************
								
								//private String goodsStorePerson;// 负责人*********
								goodStore.setGoodsStorePerson(sell.getSellAdminName());
								goodStore.setGoodsStoreGoodsMore("归还申请报废入库");
								goodStore.setGoodsStoreAdminId(sell.getSellAdminId());
								goodStore.setGoodsStoreAdminName(sell.getSellAdminName());
								//private String goodsStoreCharger;// 负责人**************
								//private String goodsStorePlanner;// 计划员************
								//private String goodsStorePiaoId;// 票号**********
								goodStore.setGoodsStoreMarkId(good.getGoodsFormat());
								goodStore.setGoodsStoreArtsCard(good.getGoodsArtsCard());
								goodStore.setGoodsStoreProMarkId(good.getGoodsProMarkId());

								//private Integer wwddId;// 送货单明细Id;******************
								
								//private String goodsStoreSendId;// 送货单号***********
								//private Integer osrecordId;// 检验id*************
								//private Float beginning_num;// 期初数量*************
								//private Float contrast_num;// 对比数量**************
								//private String baoxiao_status;// 报销状态(未报完、已报销)***********
								goodStore.setGoodsStorelasttime(good.getGoodslasttime());
								
								goodStore.setGoodsStorenexttime(good.getGoodsnexttime());
								//private Float goodsStoreround;// 质检周期*(*************
								goodStore.setTuhao(good.getTuhao());
								goodStore.setNeiorderId(good.getNeiorderId());
								goodStore.setWaiorderId(good.getWaiorderId());
								goodStore.setOrder_Id(good.getOrder_Id());
								//private String inputSource;// 入库来源（手动入库(来源为手动入库时出库关联的订单id为order_Id)，生产入库(默认为生产入库)）手动入库******
								goodStore.setInputSource("报废入库");
								//private Float deleteZt;// 减去对应在途数量
								goodStore.setDeleteZt(0F);
								//private Integer izabId;// 正式订单与备货内部计划关系表**************
//								if(good.getGongxuNum()!=null || good.getGongxuNum()!="" || good.getGongxuNum().length()>0){
//									
//									System.out.println("-------------------good.getGongxuNum()"+good.getGongxuNum());
//									goodStore.setProcessNo(Integer.parseInt(good.getGongxuNum()));
//								}
								
								
								goodStore.setStatus("入库");
								//private String style;// 入库类型（/返修入库/退货入库/批产/试制/中间件）（冲销转库和半成品转库为特殊入库类型与功能相关慎用）*********
								
								//private String planID;// 计划单号************
								goodStore.setApplyTime(sell.getSellDate());
								//private String printStatus;// 打印状态(YES/NO);
								goodStore.setPrintStatus("YES");
								goodStore.setSqUsersName(lend.getPeopleName());
								String hql="from Users where cardId=?";
								Users user=(Users) totalDao.get(hql,new Object[]{lend.getCardNum()});
								goodStore.setSqUsrsId(user.getId());
								goodStore.setSqUsersCode(user.getCode());
								goodStore.setSqUsersdept(lend.getDept());
								//private String startDate;//***********
								//private String endDate;//***********
								//private Boolean bedit;//**************
								goodStore.setYwmarkId(good.getYwmarkId());
								goodStore.setGoodsStoreMarkId(good.getGoodsMarkId());
								goodStore.setProcessName(good.getGongxuName());
								goodStore.setSellId(sell.getGoodsId());
									//6)库存记录插值
								try {
									goodsStoreServer.saveSgrk(goodStore);
									msg="申请报废成功";
								} catch (Exception e) {
									e.printStackTrace();
									msg="申请报废失败";
									
								}
									return msg;
									
							}
						}
					}
					
				}
				
			}
		}
		msg="申请报废失败";
		return msg;
	}
	//删除某条领用历史记录
	public boolean  delOneNect(Nect nect) {
		return totalDao.delete(nect);
	}
	//删除某条借用历史记录
	public boolean delOneLend(Lend lend) {
		// TODO Auto-generated method stub
		return totalDao.delete(lend);
	}
	// 删除某条归还历史记录
	public boolean delOneRepay(Repay repay) {
		// TODO Auto-generated method stub
		return totalDao.delete(repay);
	}
	// 删除某条报废历史记录
	public boolean delOneScrap(ApplyScrap scrap) {
		// TODO Auto-generated method stub
		return totalDao.delete(scrap);
	}
	
	//查找库存可以旧换新
	public List findGoodsNectOrder(Nect nect, int pageNo, int pageSize,Float canChangeNum) {
		//nect=findNectById(nect.getId());
		String hql=" from Goods where lendNeckStatus='领' and goods_class='综合库'  and  nectCanChangeStatus='是' and  goods_curquantity>0  and goods_markId='"+nect.getGoodsMarkId()+"'";
		String hql1=" from Goods where lendNeckStatus='领' and goods_class='综合库'  and  nectCanChangeStatus='是' and  goods_curquantity>="+canChangeNum +" and goods_markId='"+nect.getGoodsMarkId()+"'" ;
		List list=totalDao.find(hql);
		List list1=totalDao.find(hql1);
		if(list1.size()>0){
			return list1;
		}else {
			return list;
		}
		
		
	}
	//导出领用历史记录execl
	public void exportNectHis(Nect nectHistory,String startDate, String endDate) {
		try {
			String hql=" from Nect where 1=1 ";
			String goodHouseName="";
			if(nectHistory!=null){
				//日期范围搜索
				String[] between = new String[2];
				if (startDate !=null && endDate !=null  && !"".equals(endDate)
						&& !"".equals(startDate)) {
					between[0] = startDate;
					between[1] = endDate;
				}
				
				
				//仓区条件goodHouseName
				String hql_cq = "  goodHouse in (";
				if (nectHistory.getGoodHouse() != null && nectHistory.getGoodHouse() != null
						&& nectHistory.getGoodHouse().length() > 0) {
					String str = "";
					String[] cangqus = nectHistory.getGoodHouse().split(",");
					if (cangqus != null && cangqus.length > 0) {
						for (int i = 0; i < cangqus.length; i++) {
							str += ",'" + cangqus[i] + "'";
						}
						if (str.length() >= 1) {
							str = str.substring(1);
						}
						hql_cq += str;
					}
					goodHouseName = nectHistory.getGoodHouse();
					nectHistory.setGoodHouse(null);//?
					hql_cq += ")";
				} else {
					hql_cq = "";
				}
				if(nectHistory!=null){
					hql = totalDao.criteriaQueries(nectHistory, "date", between,
							hql_cq);//时间仓区搜索
				}
			}
			
			List<Nect> nects=totalDao.find(hql);
			HttpServletResponse response=ServletActionContext.getResponse();
			OutputStream os=response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("库存领用历史记录".getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			//WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("领用数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			ws.setColumnView(9, 20);
			ws.setColumnView(10, 18);
			ws.setColumnView(11, 20);
			ws.setColumnView(12, 20);
			ws.setColumnView(13, 20);
			
			ws.addCell(new Label(0, 0, "卡号"));
			ws.addCell(new Label(1, 0, "领用人"));
			ws.addCell(new Label(2, 0, "部门"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "批次"));
			ws.addCell(new Label(5, 0, "名称"));
			ws.addCell(new Label(6, 0, "规格"));
			ws.addCell(new Label(7, 0, "数量"));
			ws.addCell(new Label(8, 0, "单位"));
			ws.addCell(new Label(9, 0, "领用方式"));
			ws.addCell(new Label(10, 0, "仓库"));
			ws.addCell(new Label(11, 0, "仓区"));
			ws.addCell(new Label(12, 0, "库位"));
			ws.addCell(new Label(13, 0, "领用时间"));
			for(int i=0;i<nects.size();i++){
				Nect nect=nects.get(i);
				ws.addCell(new Label(0, i+1, nect.getCardNum()));
				ws.addCell(new Label(1, i+1, nect.getPeopleName()));
				ws.addCell(new Label(2, i+1, nect.getDept()));
				ws.addCell(new Label(3, i+1, nect.getGoodsMarkId()));
				ws.addCell(new Label(4, i+1, nect.getGoodsLotId()));
				ws.addCell(new Label(5, i+1,nect.getGoodsFullName()));
				ws.addCell(new Label(6, i+1, nect.getFormat()));
				ws.addCell(new Label(7, i+1, nect.getNum().toString()));
				ws.addCell(new Label(8, i+1, nect.getUnit()));
				ws.addCell(new Label(9, i+1, nect.getStatus()));
				ws.addCell(new Label(10, i+1, nect.getStorehouse()));
				ws.addCell(new Label(11, i+1, nect.getGoodHouse()));
				ws.addCell(new Label(12, i+1, nect.getWareHouse()));
				ws.addCell(new Label(13, i+1, nect.getDate()));	
			}
			wwb.write();
			wwb.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		
	}
	//导出外借历史记录execl
	public void exportLendHis(Lend lend,String startDate, String endDate) {
		try {
			String hql=" from Lend where 1=1 ";
			String goodHouseName="";
			if(lend!=null){
				//日期范围搜索
				String[] between = new String[2];
				if (startDate !=null && endDate !=null  && !"".equals(endDate)
						&& !"".equals(startDate)) {
					between[0] = startDate;
					between[1] = endDate;
				}
				
				
				//仓区条件goodHouseName
				String hql_cq = "  goodHouse in (";
				if (lend.getGoodHouse() != null && lend.getGoodHouse() != null
						&& lend.getGoodHouse().length() > 0) {
					String str = "";
					String[] cangqus = lend.getGoodHouse().split(",");
					if (cangqus != null && cangqus.length > 0) {
						for (int i = 0; i < cangqus.length; i++) {
							str += ",'" + cangqus[i] + "'";
						}
						if (str.length() >= 1) {
							str = str.substring(1);
						}
						hql_cq += str;
					}
					goodHouseName = lend.getGoodHouse();
					lend.setGoodHouse(null);//?
					hql_cq += ")";
				} else {
					hql_cq = "";
				}
				if(lend!=null){
					hql = totalDao.criteriaQueries(lend, "date", between,
							hql_cq);//时间仓区搜索
				}
			}
			
			List<Lend> selectLend=totalDao.find(hql);
			
			HttpServletResponse response=ServletActionContext.getResponse();
			OutputStream os=response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("库存外借历史记录".getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			//WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("外借数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			ws.setColumnView(9, 20);
			ws.setColumnView(10, 18);
			ws.setColumnView(11, 20);
			ws.setColumnView(12, 20);
			
			ws.addCell(new Label(0, 0, "卡号"));
			ws.addCell(new Label(1, 0, "借主"));
			ws.addCell(new Label(2, 0, "部门"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "批次"));
			ws.addCell(new Label(5, 0, "名称"));
			ws.addCell(new Label(6, 0, "规格"));
			ws.addCell(new Label(7, 0, "数量"));
			ws.addCell(new Label(8, 0, "单位"));
			ws.addCell(new Label(9, 0, "仓库"));
			ws.addCell(new Label(10, 0, "仓区"));
			ws.addCell(new Label(11, 0, "库位"));
			ws.addCell(new Label(12, 0, "外借时间"));
			for(int i=0;i<selectLend.size();i++){
				Lend lend1= selectLend.get(i);
				ws.addCell(new Label(1, i+1, lend1.getPeopleName()));
				ws.addCell(new Label(2, i+1, lend1.getDept()));
				ws.addCell(new Label(3, i+1, lend1.getGoodsMarkId()));
				ws.addCell(new Label(4, i+1, lend1.getGoodsLotId()));
				ws.addCell(new Label(5, i+1,lend1.getGoodsFullName()));
				ws.addCell(new Label(6, i+1, lend1.getFormat()));
				ws.addCell(new Label(7, i+1, lend1.getNum().toString()));
				ws.addCell(new Label(8, i+1, lend1.getUnit()));
				ws.addCell(new Label(9, i+1, lend1.getStorehouse()));
				ws.addCell(new Label(10, i+1, lend1.getGoodHouse()));
				ws.addCell(new Label(11, i+1, lend1.getWareHouse()));
				ws.addCell(new Label(12, i+1, lend1.getDate()));	
			}
			wwb.write();
			wwb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//导出归还历史记录execl
	public void exportRepayHis(Repay repayHistory, String startDate, String endDate) {
		try {
			String hql=" from Repay where 1=1 ";
			String goodHouseName="";
			if(repayHistory!=null){
				//日期范围搜索
				String[] between = new String[2];
				if (startDate !=null && endDate !=null  && !"".equals(endDate)
						&& !"".equals(startDate)) {
					between[0] = startDate;
					between[1] = endDate;
				}
				
				
				//仓区条件goodHouseName
				String hql_cq = "  goodHouse in (";
				if (repayHistory.getGoodHouse() != null && repayHistory.getGoodHouse() != null
						&& repayHistory.getGoodHouse().length() > 0) {
					String str = "";
					String[] cangqus = repayHistory.getGoodHouse().split(",");
					if (cangqus != null && cangqus.length > 0) {
						for (int i = 0; i < cangqus.length; i++) {
							str += ",'" + cangqus[i] + "'";
						}
						if (str.length() >= 1) {
							str = str.substring(1);
						}
						hql_cq += str;
					}
					goodHouseName = repayHistory.getGoodHouse();
					repayHistory.setGoodHouse(null);//?
					hql_cq += ")";
				} else {
					hql_cq = "";
				}
				if(repayHistory!=null){
					hql = totalDao.criteriaQueries(repayHistory, "rdate", between,
							hql_cq);//时间仓区搜索
				}
			}
			
			List<Repay> repays=totalDao.find(hql);
			
			
			HttpServletResponse response=ServletActionContext.getResponse();
			OutputStream os=response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("库存归还历史记录".getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			//WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("归还数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			ws.setColumnView(9, 20);
			ws.setColumnView(10, 18);
			ws.setColumnView(11, 20);
			ws.setColumnView(12, 20);
			
			ws.addCell(new Label(0, 0, "卡号"));
			ws.addCell(new Label(1, 0, "借主"));
			ws.addCell(new Label(2, 0, "部门"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "批次"));
			ws.addCell(new Label(5, 0, "名称"));
			ws.addCell(new Label(6, 0, "规格"));
			ws.addCell(new Label(7, 0, "数量"));
			ws.addCell(new Label(8, 0, "单位"));
			ws.addCell(new Label(9, 0, "仓库"));
			ws.addCell(new Label(10, 0, "仓区"));
			ws.addCell(new Label(11, 0, "库位"));
			ws.addCell(new Label(12, 0, "外借时间"));
			for(int i=0;i<repays.size();i++){
				Repay repay=repays.get(i);
				ws.addCell(new Label(0, i+1, repay.getCardNum()));
				ws.addCell(new Label(1, i+1, repay.getPeopleName()));
				ws.addCell(new Label(2, i+1, repay.getDept()));
				ws.addCell(new Label(3, i+1, repay.getGoodsMarkId()));
				ws.addCell(new Label(4, i+1, repay.getGoodsLotId()));
				ws.addCell(new Label(5, i+1,repay.getGoodsFullName()));
				ws.addCell(new Label(6, i+1, repay.getFormat()));
				ws.addCell(new Label(7, i+1, repay.getNum().toString()));
				ws.addCell(new Label(8, i+1, repay.getUnit()));
				ws.addCell(new Label(9, i+1, repay.getStorehouse()));
				ws.addCell(new Label(10, i+1, repay.getGoodHouse()));
				ws.addCell(new Label(11, i+1, repay.getWareHouse()));
				ws.addCell(new Label(12, i+1, repay.getLdate()));	
			}
			wwb.write();
			wwb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//导出报废历史记录execl
	public void exportScrapHis(ApplyScrap scrapHistory, String startDate, String endDate) {
		// TODO Auto-generated method stub
		
		try {
			String hql=" from ApplyScrap where 1=1 ";
			String goodHouseName="";
			if(scrapHistory!=null){
				//日期范围搜索
				String[] between = new String[2];
				if (startDate !=null && endDate !=null  && !"".equals(endDate)
						&& !"".equals(startDate)) {
					between[0] = startDate;
					between[1] = endDate;
				}
				if(scrapHistory!=null){
					hql = totalDao.criteriaQueries(scrapHistory, "badDate", between,
							"");//时间仓区搜索
				}
			}
			
			List<ApplyScrap> scraps=totalDao.find(hql);
			
			HttpServletResponse response=ServletActionContext.getResponse();
			OutputStream os=response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("库存报废历史记录".getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			//WritableSheet ws =null;
			WritableSheet ws = wwb.createSheet("归报废数据", 0);
			ws.setColumnView(0, 18);
			ws.setColumnView(1, 14);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 20);
			ws.setColumnView(8, 20);
			
			ws.addCell(new Label(0, 0, "卡号"));
			ws.addCell(new Label(1, 0, "责任人"));
			ws.addCell(new Label(2, 0, "部门"));
			ws.addCell(new Label(3, 0, "件号"));
			ws.addCell(new Label(4, 0, "批次"));
			ws.addCell(new Label(5, 0, "名称"));
			ws.addCell(new Label(6, 0, "规格"));
			ws.addCell(new Label(7, 0, "报废数量"));
			ws.addCell(new Label(8, 0, "报废时间"));
			for(int i=0;i<scraps.size();i++){
				ApplyScrap scrap=scraps.get(i);
				ws.addCell(new Label(0, i+1, scrap.getCardNum()));
				ws.addCell(new Label(1, i+1, scrap.getUsername()));
				ws.addCell(new Label(2, i+1, scrap.getDept()));
				ws.addCell(new Label(3, i+1, scrap.getGoodsMarkId()));
				ws.addCell(new Label(4, i+1, scrap.getGoodsLotId()));
				ws.addCell(new Label(5, i+1,scrap.getGoodsFullName()));
				ws.addCell(new Label(6, i+1, scrap.getFormat()));
				ws.addCell(new Label(7, i+1, scrap.getBadNum().toString()));
				ws.addCell(new Label(8, i+1, scrap.getBadDate()));	
			}
			wwb.write();
			wwb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public Goods findGoodsById(Integer id) {
		Goods good=(Goods) totalDao.get(Goods.class, id);
		
		return good;
		
		
	}
	public Lend findLendById(Integer lendId) {
		if(lendId!=null){
			return (Lend)totalDao.get(Lend.class, lendId);
			
			
		}
		return null;
	}
	public ApplyScrap findScrapById(Integer scrapId) {
		if(scrapId!=null){
			return (ApplyScrap)totalDao.get(ApplyScrap.class, scrapId);
		}
		return null;
	}
	
	public Repay findRepayById(Integer repayId) {
		if(repayId!=null){
			return (Repay)totalDao.get(Repay.class, repayId);
		}
		return null;
	}
	public Nect findNectById(Integer nectId) {
		if(nectId!=null){
			return (Nect)totalDao.get(Nect.class,nectId);
		}
		return null;
	}
	
	
	
	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}
	public GoodsStoreServer getGoodsStoreServer() {
		return goodsStoreServer;
	}

	public void setGoodsStoreServer(GoodsStoreServer goodsStoreServer) {
		this.goodsStoreServer =  goodsStoreServer;
	}



	@Override
	public String pladdNect(int[] ids, Float[] nums, String cards) {
	Users loginUsers =		Util.getLoginUser();
		if(loginUsers == null){
			return "请先登录!~";
		}
		if(cards!=null){
			Users users =	(Users) totalDao.getObjectByCondition(" from Users where cardId=? and onWork in ('在职','试用') " , cards);
			if(users==null){
				return "无效卡号:"+cards;
			}
			if(!"允许".equals(users.getPower())){
				return "卡号:"+cards+"，姓名:"+users.getName()+"无领用权限!";
			}
			if(ids!=null && ids.length>0){
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < ids.length; i++) {
					Goods goods =	(Goods) totalDao.get(Goods.class,ids[i]);
					if(goods.getDemanddept()!=null
							&& goods.getDemanddept().equals(users.getDept())){
						Nect nect = new Nect();
						nect.setGoodsMarkId(goods.getGoodsMarkId());
						nect.setGoodsLotId(goods.getGoodsLotId());
						nect.setGoodsFullName(goods.getGoodsFullName());
						nect.setNum(nums[i]);
						nect.setUnit(goods.getGoodsUnit());
						nect.setFormat(goods.getGoodsFormat());
						nect.setWgType(goods.getWgType());
						nect.setStorehouse(goods.getGoodsClass());
						nect.setGoodHouse(goods.getGoodHouseName());
						nect.setWareHouse(goods.getGoodsPosition());
						nect.setDate(Util.getDateTime("yyyy-MM-dd"));
						nect.setTime(Util.getDateTime());
						nect.setCardNum(cards);
						nect.setPeopleName(users.getName());
						nect.setDept(users.getDept());
						nect.setPower(users.getPower());
						nect.setGoodsId(goods.getGoodsId());
						nect.setAdminId(loginUsers.getId());
						nect.setAdmin(loginUsers.getName());
						boolean bool =	insertNect(nect, goods);
						if(!bool){
							msg.append("件号:"+goods.getGoodsMarkId()+"批次:"+goods.getGoodsLotId()+"领用失败 !~<br/>");
						}
					}else{
						msg.append(users.getName()+"的部门:"+users.getDept()+"与件号:"+goods.getGoodsMarkId()+
								"批次:"+goods.getGoodsLotId()+"的需求部门:"+goods.getDemanddept()+"不一致。不可领用!~<br/>");
					}
					
				}
				if(msg!=null && msg.length()>0){
					throw new RuntimeException("领用失败，异常信息如下:<br/>"+msg.toString());
				}
				return "true";
			}
		}
		return "请刷卡!~";
	}

}
