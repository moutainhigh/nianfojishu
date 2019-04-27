package com.task.Server.jxkh;

import java.util.List;

import com.task.entity.Users;
import com.task.entity.jxkh.BmzZlh;
import com.task.entity.jxkh.DeptFenPeiXiShu;
import com.task.entity.jxkh.DeptUsersDuty;
import com.task.entity.jxkh.FenPeiQingKuang;
import com.task.entity.jxkh.InCome;
import com.task.entity.jxkh.RankingCounts;
import com.task.entity.jxkh.SalesAmountCoefficient;
import com.task.entity.jxkh.TargetAchievedMark;
import com.task.entity.jxkh.WaiWeiJieGouMuBiao;
import com.task.entity.jxkh.WeiWaiJieGou;
import com.task.entity.jxkh.WorkShopTiQuMx;
import com.task.entity.jxkh.WorkShopXiaoLvZz;
import com.task.entity.jxkh.YearImprove;
import com.task.entity.jxkh.ZbSjZk;
import com.task.entity.jxkh.ZhiZaoPingJia;
import com.task.entity.jxkh.DeptLeaderPenPei;

public interface JiaoXiaoKaoHeServer {

	//1.部门职责确定表
	/**
	 * 添加部门职责确定
	 */
	 String addDeptUsersDuty(DeptUsersDuty dud);
	 /**
	  * 查询所有部门职责确定
	  * @param dud
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllDudList(DeptUsersDuty dud,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改部门职责确定
	  * @param dud
	  * @return
	  */
	 String updateDud(DeptUsersDuty dud);
	 /**
	  * 删除部门职责确定
	  * @param dud
	  * @return
	  */
	 String delDud(DeptUsersDuty dud);
	 /**
	  * 
	  * @param dud
	  * @param status
	  * @return
	  */
	 List<DeptUsersDuty> findDudList(DeptUsersDuty dud,String status);
	 /**
	  * 根据Id查询
	  * @param id
	  * @return
	  */
	 DeptUsersDuty findDudById(Integer id);
	 //2.名次人数确定表 
	 /**
	  * 添加名次人数确定
	  */
	 String addRankingCounts(RankingCounts rankCounts);
	 /**
	  * 查询所有名次人数确定
	  */
	 Object[] findAllRankCountsList(RankingCounts rankCounts,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改名次人数确定
	  * @param rankCounts
	  * @return
	  */
	 String	updateRankCounts(RankingCounts rankCounts);
	 /**
	  * 删除名次人数确定
	  * @param rankCounts
	  * @return
	  */
	 String delRankCounts(RankingCounts rankCounts);
	 /**
	  * 根据Id查询
	  * @param id
	  * @return
	  */
	 RankingCounts findRankCountsById(Integer id);
	 /**
	  * 
	  * @param rankCounts
	  * @return
	  */
	 RankingCounts findRankCountsBy(RankingCounts rankCounts);
	 
	 //3.月销售额与提取系数关系表  SalesAmountCoefficient
	 
	 /**
	  * 添加
	  */
	 String addsac(SalesAmountCoefficient sac);
	 /**
	  * 查询
	  * @param sac
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllSacList(SalesAmountCoefficient sac,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 根据Id查询
	  * @param sac
	  * @return
	  */
	 SalesAmountCoefficient getsacById(SalesAmountCoefficient sac);
	 /**
	  * 修改
	  * @param sac
	  * @return
	  */
	 String	updateSac(SalesAmountCoefficient sac);
	 /**
	  * 删除
	  * @param sac
	  * @return
	  */
	 String delSac(SalesAmountCoefficient sac);
	 /**
	  * 根据销售金额得到相应提取系数
	  * @param sales
	  * @return
	  */
	 SalesAmountCoefficient getSacByValue(Float sales);
	//4.目标达成未达成加减分表: TargetAchievedMark 
	 /**
	  * 添加
	  */
	 String addTargetAchievedMark(TargetAchievedMark tam);
	 /**
	  * 查询
	  * @param tam
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllTamList(TargetAchievedMark tam,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改
	  * @param tam
	  * @return
	  */
	 String updateTam(TargetAchievedMark tam);
	 /**
	  * 删除
	  * @param tam
	  * @return
	  */
	 String delTam(TargetAchievedMark tam);
	 /**
	  * 根据条件获取单个
	  * @param tam
	  * @return
	  */
	 TargetAchievedMark findTamBy(TargetAchievedMark tam);
	 
	 //5.委外结构目标值表 :WaiWeiJieGouMuBiao
	  /**
	   * 添加
	   */
	 String addWwJgMb(WaiWeiJieGouMuBiao wwjgmb);
	 
	 /**
	  * 查询
	  * @param wwjgmb
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllWwJgMbList(WaiWeiJieGouMuBiao wwjgmb,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改
	  * @param wwjgmb
	  * @return
	  */
	 String updateWwJgMb(WaiWeiJieGouMuBiao wwjgmb);
	 /**
	  * 删除
	  * @param wwjgmb
	  * @return
	  */
	 String dleWwJgMb(WaiWeiJieGouMuBiao wwjgmb);
	 /**
	  * 根据条件查询获取单个
	  * @param wwjgmb
	  * @return
	  */
	 WaiWeiJieGouMuBiao findWwJgMbBy(WaiWeiJieGouMuBiao wwjgmb);
	 /**
	  * 
	  */
	 SalesAmountCoefficient updateMaxSac();
	 //6.制造评价
	 /**
	  * 添加
	  */
	 String addZhiZaoPingJia(ZhiZaoPingJia zzpj);
	 /**
	  * 查询
	  * @param zzpj
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllZzpjList(ZhiZaoPingJia zzpj,Integer pageSize,Integer pageNo,String status );
	 /**
	  * 查询单个
	  */
	 ZhiZaoPingJia findZzpjById(Integer id); 
	 /**
	  * 查询
	  */
	 Object[] findZzpjBy(ZhiZaoPingJia zzpj);
	 /**
	  * 修改
	  * @param zzpj
	  * @return
	  */
	 String updateZzpj(ZhiZaoPingJia zzpj);
	 /**
	  * 删除
	  * @param zzpj
	  * @return
	  */
	 String delZzpj(ZhiZaoPingJia zzpj);
	 
	 //7.部门长周例会
	 /**
	  * 添加
	  */
	 String addBmzZlh(BmzZlh bmzzlh);
	 String addBmzZlh0(int id,BmzZlh bmzzlh);
	 /**
	  * 查询
	  * @param bmzzlh
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllBmzZlhList(BmzZlh bmzzlh,Integer pageSize,Integer pageNo,String status );
	 /**
	  * 查询单个
	  */
	 BmzZlh findbmzzlhById(Integer id);
	 
	 /**
	  * 查询
	  */
	 Object[] findbmzzlList(BmzZlh bmzzlh);
	 /**
	  * 修改
	  */
	 String updateBmzZlh(BmzZlh bmzzlh); 
	 /**
	  * 删除
	  */
	 String delBmzZlh(BmzZlh bmzzlh);
	//8.车间效率增长情况表 WorkShopXiaoLvZz
	 /**
	  * 添加
	  */
	 String addWorkShopXiaoLvZz(WorkShopXiaoLvZz wsxlz);
	 /**
	  * 查询
	  * @param wsxlz
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllWsxlzList(WorkShopXiaoLvZz wsxlz,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 查询
	  */
	 Object[] findWsxlzBy(WorkShopXiaoLvZz wsxlz);
	 /**
	  * 查询单个
	  */
	 WorkShopXiaoLvZz findWsxlzById(Integer id);
	 /**
	  * 修改
	  */
	 String updateWsxlz(WorkShopXiaoLvZz wsxlz);
	 /**
	  * 删除
	  */
	 String delWsxlz(WorkShopXiaoLvZz wsxlz);
	 //9.委外比结构比表: WeiWaiJieGou
	 /**
	  * 添加
	  */
	 String addWeiWaiJieGou(WeiWaiJieGou wwjg);
	 String addWeiWaiJieGou0(WeiWaiJieGou wwjg,String status);
	 /**
	  * 查询
	  */
	 Object[] findAllWwjgList(WeiWaiJieGou wwjg,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 查询
	  * @param wwjg
	  * @return
	  */
	Object [] findWwjgBy(WeiWaiJieGou wwjg);
	 /**
	  * 查询单个
	  */
	 WeiWaiJieGou findWwjgById(Integer id);
	 /**
	  * 修改
	  * @param wwjg
	  * @return
	  */
	 String updateWwjg(WeiWaiJieGou wwjg);
	 /**
	  * 删除
	  * @param wwjg
	  * @return
	  */
	 String delWwjg(WeiWaiJieGou wwjg);
	 
	 /**
	  * 添加/修改 制造评价
	  */
	 String addZzpj(ZhiZaoPingJia zzpj);
	 //10.指标实际状况:(ta_ZbSjZk)
	 
	 /**
	  * 添加
	  */
	 String addZbSjZk(ZbSjZk zbSjZk);
	 /**
	  * 查询
	  */
	 Object[] findAllZbSjZk(ZbSjZk zbSjZk,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改
	  */
	 String updateZbSjZk(ZbSjZk zbSjZk);
	 /**
	  * 删除	 
	  */
	 String delZbSjZk(ZbSjZk zbSjZk);
	 /**
	  * 根据Id查询
	  */
	 ZbSjZk findzbSjZkbyId(Integer id );
	 //11.车间工资中提取明细 WorkShopTiQuMx
	 
	 /**
	  * 添加
	  */
	 String addWstqmx(WorkShopTiQuMx wstqmx);
	 /**
	  * 查询
	  * @param wstqmx
	  * @param pageSize
	  * @param pageNo
	  * @param status
	  * @return
	  */
	 Object[] findAllWstqmx(WorkShopTiQuMx wstqmx,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改
	  * @param wstqmx
	  * @return
	  */
	 String updateWstqmx(WorkShopTiQuMx wstqmx);
	 /**
	  * 删除
	  * @param wstqmx
	  * @return
	  */
	 String delWstqmx(WorkShopTiQuMx wstqmx);
	 /**
	  * 根据Id查询
	  * @param id
	  * @return
	  */
	 WorkShopTiQuMx findWstqmxById(Integer id);
	 // 分配情况表:(ta_FenPeiQingKuang)
	 /**
	  * 获取各部门分配情况表
	  */
	 Object[] huoquFenPeiQingkuang(String months);
	 
	 //各部门分配系数表:ta_DeptFenPeiXiShu)
	 
	 /**
	  * 添加
	  */
	 String adddfpxs(DeptFenPeiXiShu dppxs);
	 /**
	  * 查询
	  */
	 Object[] findAlldfpxs(DeptFenPeiXiShu dppxs,Integer pageSize,Integer pageNo,String status);
	 /**
	  * 修改
	  */
	 String updatedfpxs(DeptFenPeiXiShu dppxs); 
	 /**
	  * 删除
	  * 
	  */
	 String deldfpxs(DeptFenPeiXiShu dppxs);
	 /**根据Id查询
	  */
	 DeptFenPeiXiShu getdppxsById(Integer id);
	 /**
	  * 获取获取部门长分配情况信息
	  */
	List<DeptLeaderPenPei> huoqudlppList(String months);
	/**
	 * 
	 */
	List<DeptLeaderPenPei> updatedlppList(List<DeptLeaderPenPei> dlppList,String months);
	//年度改善自选 YearImprove
	/**
	 * 添加	
	 */
	String addyearimprove(YearImprove yearimprove);
	/**
	 * 查询
	 */
	Object[] findAllyiveList(YearImprove yearimprove,String status,Integer pageSize,Integer pageNO);
	/**
	 *根据Id查询
	 */
	YearImprove findyearimproveById(Integer id);
	/**
	 * 修改
	 */
	String updateyearimprove(YearImprove yearimprove);
	/**
	 * 删除
	 */
	String delyearimprove(YearImprove yearimprove);
	//收入
	/**
	 * 添加收入情况表
	 */
	void addInCome(DeptLeaderPenPei dlpp,Double average,Users user);
	
	void addInCome2(DeptUsersDuty dud, Double average,String months,Users user);
	
	/**
	 * 查询收入情况。
	 */
	Object[] findAllInComeList(InCome inCome, String months,Integer pageSize, Integer pageNO);
	/**
	 * 完善收入
	 */
	String wanShaInCome(InCome inCome);
	
	/**
	 * 根据Id获取收入
	 */
	
	InCome getInComeById(Integer id);
	
}
