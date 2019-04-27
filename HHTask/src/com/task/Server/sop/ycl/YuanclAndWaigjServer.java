package com.task.Server.sop.ycl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.task.entity.sop.ycl.FenMoTzRecord;
import com.task.entity.sop.ycl.WaiGouJianLT;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.zhuseroffer.ZhuserOffer;
import com.tast.entity.zhaobiao.ZhUser;


public interface YuanclAndWaigjServer {
	
	/**
	 * 根据库别和件号查询
	 * @param markId
	 * @return
	 */
	public List<YuanclAndWaigj> checkYuanclAndWaigjBycode(String markId,String wgType);
	/**
	 * 添加原材料和外购件
	 * @param yuanclAndWaigj
	 * @return
	 * 
	 */
    public String add (YuanclAndWaigj yuanclAndWaigj);
    /**
     * 修改外购件和原材料
     * @param yuanclAndWaigj
     * @return
     */
    public String update (YuanclAndWaigj yuanclAndWaigj,String upstatus);
    /**
     * 删除外购件和原材料
     * @param yuanclAndWaigj
     * @return
     */
    public String delete (YuanclAndWaigj yuanclAndWaigj);
    /**
     * 得到所有的外购件和原材料
     * @return
     */
    public List<YuanclAndWaigj> findAll ();
    /**
     * 根据条件查到对应的原材料和外购件
     * @param yuanclAndWaigj
     * @param parseInt
     * @param pageSize
     * @return
     */
    public Map<Integer, Object> findYuanclAndWaigjsByStatus(YuanclAndWaigj yuanclAndWaigj, int pageNo, int pageSize,String status,String kgliao);
    /**
     * 根据条件查到对应的原材料和外购件
     * @param yuanclAndWaigj
     * @param parseInt
     * @param pageSize
     * @return
     */
    public Map<Integer, Object> findYuanclAndWaigjsByCondition(YuanclAndWaigj yuanclAndWaigj, int pageNo, int pageSize,String productStyle,String pageStatus);
   /**
    * 通过id获取对象
    * @param id
    * @return
    */
    public YuanclAndWaigj getById(Integer id);
    /**
     * 通过(牌号或者规格)或者件号查到对应的原材料或外购件
     * @param trademark
     * @param clClass
     * @return
     */
  public List<YuanclAndWaigj> getAllNames(YuanclAndWaigj yuanclAndWaigj);
  /**
   * 更新已有的原材料和外购件到库里
   * @return
   */
	public boolean updateHad();
	/**
	 * 修改原材料和外购件状态
	 * @param yuanclAndWaigj
	 * @return
	 */
	public String updateStatus(YuanclAndWaigj yuanclAndWaigj);
	/**
	 * 通过 牌号和规格获取
	 * @param trademark
	 * @param specification
	 * @return
	 */
	public YuanclAndWaigj getYWbytrademark(String trademark,String specification);
	/**
	 * 根据物料类型查找供货商
	 */
	public boolean saveZhUserByType(YuanclAndWaigj yuanclAndWaigj,String cycle);
	
	public List<ZhuserOffer> findzhOfferById(YuanclAndWaigj yuanclAndWaigj);
	/**
	 * 导出
	 */
	 public void explorExcel(YuanclAndWaigj yw);
	 
	 public	void updatepriceStatus();
	 
	 /**
	  * 判断外购件库是否不允许修改(系统设置项中有 《外购禁止修改》 valueCode = 是  代表不能修改)
	  * @return 返回  noUpdate
	  */
	 public	String noUpdateYuan(String tag);
	 
	 public boolean exportNoPrice( YuanclAndWaigj yuanclAndWaigj);
	 /**
	  * 根据客供属性分裂外购件
	  */
	public void KgLiaoPart();
	/**
	 * 
	 * 修改物料类别同时修改同件号同版本下BOM和Procard上的物料类别
	 */
	public String  xgWgType(YuanclAndWaigj yuanclAndWaigj);
	
	/**
	 * 添加外购件LT
	 */
	public String addWaiGouJianLt(WaiGouJianLT lt);
	
	/**
	 * 查询外购件LT
	 */
	public Object[] findALlWaiGouJianLT(WaiGouJianLT lt, int pageNo, int pageSize,String status);
	
	/**
	 * 修改外购件LT
	 */
	public String updateWaiGouJianLt(WaiGouJianLT lt);
	/**
	 * 删除外购件LT
	 */
	public String delWaiGouJianLt(WaiGouJianLT lt);
	/**
	 * 根据Id查询外购件LT
	 */
	public WaiGouJianLT findWaiGouJianLTById(Integer id);
	/**
	 * 查询所有外购件LT
	 */
	
	public List<WaiGouJianLT> findAllltList();
	/**
	 * 修改周期
	 */
	public String xgperiod(YuanclAndWaigj yuanclAndWaigj);
	/**
	 * /导入采购周期
	 */
	public String daoruperiod(File file);
	
	/**
	 * 外购件表查询信息
	 * @param markId
	 * @return
	 */
	public List<YuanclAndWaigj> checkYuanclAndWaigjBycode(YuanclAndWaigj yuanclAndWaigj);
	/**
	 * 根据件号和相应信息查询外购件库
	 * @param yuanclAndWaigj
	 * @param tags
	 * @return
	 */
	public List<YuanclAndWaigj> checkYuanclAndWaigjBycode(YuanclAndWaigj yuanclAndWaigj,String tags);
	public Map<Integer, Object> checkZhUser(ZhUser zhUser,int pageNo, int pageSize,String wgType);
	public String bandZhuser(Integer[] zhUserId,Integer yuanclAndWaigjId);
	public String importFile(File importFile);
	
	/**
	 * 批量审批
	 */
	public String auditYclWgl(Integer[] Ids ,Integer num);
	
	/**
	 * 根据件号查询库存，占用，在途量
	 * @return
	 */
	public Map<String, Object> findMarkIdByMRPCount(YuanclAndWaigj yuanclAndWaigj,Integer pageSize,Integer pageNO,String pageStatus);
	
	public Map<Integer, Object> findyuan(String ids);
	/**
	 * 粉末用量调整
	 * @return
	 */
	String fenMoTzSq(FenMoTzRecord fmtr);
	/**
	 * 查询所有粉末用量调整记录
	 */
	Object[] findAllListfmtr(FenMoTzRecord fmtr,Integer pageSize,Integer pageNO,String status);
	/**
	 * 粉末用量调整批量
	 */
	String tzylPL(Integer[] ids,List<FenMoTzRecord> fmtrList);
	
	}
