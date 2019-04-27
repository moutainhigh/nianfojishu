package com.task.ServerImpl.banci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.task.Dao.TotalDao;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.banci.BanCiServer;
import com.task.entity.Users;
import com.task.entity.banci.BanCi;
import com.task.entity.banci.BanCiTime;
import com.task.entity.banci.SchedulingTable;
import com.task.util.PostUtil;
import com.task.util.Util;

@SuppressWarnings("unchecked")
public class BanCiServerImpl implements BanCiServer {
	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	// 添加班次
	@Override
	public String addbanCi(BanCi banCi, List<BanCiTime> banCiTime) {
		if(banCi!=null){
			if(banCi.getName()!=null){
				int i = totalDao.getCount("from BanCi where name = ?", banCi.getName());
				if(i>0){
					return "班次名称已存在，添加失败!";
				}
			}else {
				return "班次名称为空，添加失败!";
			}
			String time = Util.getDateTime();
			banCi.setAddtime(time);
			Integer wxmTime = 0;//班次总时长
			Integer ztxxTime = 0;//中途休息时长
			StringBuffer buffer = new StringBuffer();//班次时段
			for (int i = 0; i < banCiTime.size(); i++) {
				BanCiTime banCiTime2 = banCiTime.get(i);
				if(banCiTime2==null) continue;
				if("是".equals(banCi.getXiaFuis())){
					banCiTime2.setXiaFu(true);
				}else {
					banCiTime2.setXiaFu(false);
				}
				if("次日".equals(banCiTime2.getDayType())||"次日".equals(banCiTime2.getEdayType())){
					banCi.setIsOvernight("是");
				}
				Integer xxTime = 0;
				if("当日".equals(banCiTime2.getDayType())&&"次日".equals(banCiTime2.getEdayType())){
					xxTime = (int) (Util.getWorkTime2(banCiTime2.getStartTime(),
					"24:00:00") / 60000);// 此段工作时长
					xxTime += (int) (Util.getWorkTime2("00:00:00",
							banCiTime2.getEndTime()) / 60000);// 此段工作时长
				}else {
					xxTime = (int) (Util.getWorkTime2(banCiTime2.getStartTime(),
							banCiTime2.getEndTime()) / 60000);// 此段工作时长
				}
				buffer.append("第"+banCiTime2.getDuan()+"段考勤时间："+banCiTime2.getStartTime()+"-"+banCiTime2.getEndTime()+"    ");
				banCiTime2.setBanCi(banCi);
				banCiTime2.setAddTime(time);
				banCiTime2.setXxTimeNumber(xxTime);
				wxmTime += xxTime;
				if(i==0){
					firstBanciTime(banCiTime, i, banCiTime2, xxTime);
					banCi.setFirsttime(banCiTime2.getStartTime());
				}else if(i==banCiTime.size()-1){
					ztxxTime = lastBanCiTime(banCiTime, ztxxTime, i, banCiTime2, xxTime);
					banCi.setFinishtime(banCiTime2.getEndTime());
				}else {
					ztxxTime = zhongBanCiTime(banCiTime, ztxxTime, i, banCiTime2, xxTime);
				}
				totalDao.save(banCiTime2);
			}
			banCi.setGzTime(wxmTime);
			banCi.setXxTime(ztxxTime);
			banCi.setBanCiTimeShow(buffer.toString());
			if(totalDao.save(banCi)){
				return "true";
			}
		}
		return "添加失败！";
	}

	/**
	 * 添加班次 中途时间段
	 * @param banCiTime
	 * @param ztxxTime
	 * @param i
	 * @param banCiTime2
	 * @param xxTime
	 * @return
	 */
	private Integer zhongBanCiTime(List<BanCiTime> banCiTime, Integer ztxxTime,
			int i, BanCiTime banCiTime2, Integer xxTime) {
		ztxxTime = upZhongBanCiTime(ztxxTime, banCiTime, i, banCiTime2,
				banCiTime2, xxTime);
		return ztxxTime;
	}

	/**
	 * 添加班次 最后一段上班时间
	 * @param banCiTime
	 * @param ztxxTime 中途休息时长
	 * @param i
	 * @param banCiTime2
	 * @param xxTime
	 * @return
	 */
	private Integer lastBanCiTime(List<BanCiTime> banCiTime, Integer ztxxTime,
			int i, BanCiTime banCiTime2, Integer xxTime) {
		//最后一段上班时间
		if(banCiTime2!=null&&banCiTime2.getStartTime()!=null&&!"".equals(banCiTime2.getStartTime())&&banCiTime2.getEndTime()!=null&&!"".equals(banCiTime2.getEndTime())){
			if(banCiTime2.getStartBeforeMin()!=null&&banCiTime2.getStartBeforeMin()>0){//允许上班前打卡时间
				banCiTime2.setStartBeforeMin(banCiTime2.getStartBeforeMin());
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime2.getStartTime(), banCiTime2.getStartBeforeMin()*(-1)));//开始前打卡时间
				ztxxTime += ztxxTime(banCiTime.get(i-1), banCiTime2);
			}else {//最后一段上班时间与上一段下班时间之间的时长/2为允许上班之前时间
				int timeX = 60;
				BanCiTime banCiTime3 = banCiTime.get(i-1);
				if(banCiTime3!=null){
					if(banCiTime3.getEndTime()!=null){
						timeX = (int) (Util.getWorkTime2(banCiTime3.getEndTime(),banCiTime2.getStartTime()) / 60000);
						ztxxTime+=timeX;
					}
				}
				banCiTime2.setStartBeforeMin(timeX/2);
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime2.getStartTime(),timeX/2*(-1)));//开始前打卡时间
			}
			if(banCiTime2.getStartLaterMin()!=null&&banCiTime2.getStartLaterMin()>=0){//上班后打卡
				banCiTime2.setStartLaterMin(banCiTime2.getStartLaterMin());
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime2.getStartTime(), banCiTime2.getStartLaterMin()));//开始后打卡时间(允许迟到时间)
			}else {
				banCiTime2.setStartLaterMin(30);
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime2.getStartTime(), 30));//开始后打卡时间(上午下班有效时间)
			}
			if(banCiTime2.getEndBeforeMin()!=null&&banCiTime2.getEndBeforeMin()>=0){
				banCiTime2.setEndBeforeMin(banCiTime2.getEndBeforeMin());
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime2.getEndTime(), banCiTime2.getEndBeforeMin()*(-1)));//上班结束前打卡时间(允许早退时间)
			}else {
				int i1 = xxTime-30;
				banCiTime2.setEndBeforeMin(i1);//允许早退时间
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime2.getEndTime(), i1*(-1)));//结束前打卡时间(允许早退时间)
			}
			if(banCiTime2.getEndLaterMin()!=null&&banCiTime2.getEndBeforeMin()>0){//允许最晚下班时间
				banCiTime2.setEndLaterMin(banCiTime2.getEndLaterMin());
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime2.getEndTime(), banCiTime2.getEndLaterMin()));//结束后打卡时间(下午迟到时间)
			}else{
				banCiTime2.setEndLaterMin(60);
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime2.getEndTime(), 60));//结束后打卡时间(允许下班时间)
			}
			isfor(banCiTime2);
		}
		return ztxxTime;
	}

	/**
	 * 添加班次，处理第一段时间
	 * @param banCiTime
	 * @param i
	 * @param banCiTime2
	 * @param xxTime
	 */
	private void firstBanciTime(List<BanCiTime> banCiTime, int i,
			BanCiTime banCiTime2, Integer xxTime) {
		if(banCiTime2!=null&&banCiTime2.getStartTime()!=null&&!"".equals(banCiTime2.getStartTime())&&banCiTime2.getEndTime()!=null&&!"".equals(banCiTime2.getEndTime())){
			if(banCiTime2.getStartBeforeMin()!=null&&banCiTime2.getStartBeforeMin()>0){//允许上班前打卡时间
				banCiTime2.setStartBeforeMin(banCiTime2.getStartBeforeMin());
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime2.getStartTime(), banCiTime2.getStartBeforeMin()*(-1)));//开始前打卡时间
			}else {
				banCiTime2.setStartBeforeMin(120);
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime2.getStartTime(),-120));//开始前打卡时间
			}
			if(banCiTime2.getStartLaterMin()!=null&&banCiTime2.getStartLaterMin()>=0){//上班后打卡
				banCiTime2.setStartLaterMin(banCiTime2.getStartLaterMin());
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime2.getStartTime(), banCiTime2.getStartLaterMin()));//开始后打卡时间(允许迟到时间)
			}else {
				banCiTime2.setStartLaterMin(30);
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime2.getStartTime(), 30));//开始后打卡时间(上午下班有效时间)
			}
			if(banCiTime2.getEndBeforeMin()!=null&&banCiTime2.getEndBeforeMin()>=0){
				banCiTime2.setEndBeforeMin(banCiTime2.getEndBeforeMin());
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime2.getEndTime(), banCiTime2.getEndBeforeMin()*(-1)));//上班结束前打卡时间(允许早退时间)
			}else {
				int i1 = xxTime-30;
				banCiTime2.setEndBeforeMin(i1);//允许早退时间
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime2.getEndTime(), i1*(-1)));//结束前打卡时间(允许早退时间)
			}
			if(banCiTime2.getEndLaterMin()!=null&&banCiTime2.getEndBeforeMin()>0){//允许最晚下班时间
				banCiTime2.setEndLaterMin(banCiTime2.getEndLaterMin());
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime2.getEndTime(), banCiTime2.getEndLaterMin()));//结束后打卡时间(下午迟到时间)
			}else{//第一段时间下班时间最迟为下一段时间的上班时间期间的时间/2允许下班最迟时间
				int timeX = 120;
				if(banCiTime.size()>1){
					BanCiTime banCiTime3 = banCiTime.get(i+1);
					if(banCiTime3!=null){
						if(banCiTime3.getStartTime()!=null){
							timeX = (int) (Util.getWorkTime2(banCiTime2.getEndTime(),banCiTime3.getStartTime()) / 60000);
						}
					}
				}
				banCiTime2.setEndLaterMin(timeX/2);
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime2.getEndTime(), timeX/2));//结束后打卡时间(允许下班时间)
			}
			isfor(banCiTime2);
		}
	}

	/**
	 * 上班下班是否打卡
	 * @param banCiTime2
	 */
	private void isfor(BanCiTime banCiTime2) {
		if("是".equals(banCiTime2.getStartIsFor())){
			banCiTime2.setStartIsDaka(true);
		}else {
			banCiTime2.setStartIsDaka(false);
		}
		if("是".equals(banCiTime2.getEndIsFor())){
			banCiTime2.setEndIsDaka(true);
		}else {
			banCiTime2.setEndIsDaka(false);
		}
	}

	/**
	 * 获得当前班次与上一时段间隔
	 * @param banCiTime 上一时段
	 * @param banCiTime2 本时段
	 * @return
	 */
	private Integer ztxxTime(BanCiTime banCiTime, BanCiTime banCiTime2) {
		Integer i = 0;
		if(banCiTime!=null){
			if(banCiTime.getEndTime()!=null){
				i = (int) (Util.getWorkTime2(banCiTime.getEndTime(),banCiTime2.getStartTime()) / 60000);
			}
			if(i<0){
				i = (int) (Util.getWorkTime2(banCiTime.getEndTime(), "24:00:00") / 60000);// 此段工作时长
				i += (int) (Util.getWorkTime2("00:00:00", banCiTime2.getStartTime()) / 60000);// 此段工作时长
			}
		}
		return i;
	}

	// 删除班次
	@Override
	public boolean delBanCi(BanCi banCi) {
		boolean b = false;
		BanCi banCi2 = (BanCi) this.totalDao.getObjectById(BanCi.class, banCi
				.getId());
		if (banCi2 != null) {
			String hql = "from Users where banci_id=?";
			List<Users> list = this.totalDao.query(hql, banCi2.getId());
			if (list!=null&&list.size()>0) {
				for (Users users : list) {
					users.setBanci_id(null);
					users.setBanci_name(null);
					totalDao.update(users);
				}
			}
			b = this.totalDao.delete(banCi2);
		}
		return b;
	}

	// 查找所有的班次
	@Override
	public Object[] findbanCi(BanCi banCi, int parseInt, int pageSize) {
		if (banCi == null) {
			banCi = new BanCi();
		}
		String hql = totalDao.criteriaQueries(banCi, null);
		// if(banCi!=null){
		// if(banCi.getName()!=null&&!"".equals(banCi.getName())){
		// hql +="and name like'%"+banCi.getName()+"%'";
		// }
		// }
		hql += " order by name";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		for (Object object : list) {
			BanCi banCi2 = (BanCi) object;
			if(banCi2.getBanCiTime()!=null&&banCi2.getBanCiTime().size()>0&&(banCi2.getBanCiTimeShow()==null||"".equals(banCi2.getBanCiTimeShow()))){
				StringBuffer banCiTimeShow = new StringBuffer();
				for (BanCiTime bt : banCi2.getBanCiTime()) {
					banCiTimeShow.append(bt.getStartTime()+"-"+bt.getEndTime()+"  ");
				}
				banCi2.setBanCiTimeShow(banCiTimeShow.toString());
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	// 根据ID查找班次
	@Override
	public BanCi salBanCiByid(Integer i) {
		BanCi banCi2 = (BanCi) this.totalDao.getObjectById(BanCi.class, i);
		return banCi2;
	}

	// 更新班次
	@Override
	public String updateBanCi(BanCi banCi2, List<BanCiTime> banCiTimes) {
		BanCi banCi = (BanCi) this.totalDao.getObjectById(BanCi.class, banCi2.getId());
		if (banCi2!=null) {
			banCi.setUpdatetime(Util.getDateTime());
			banCi.setXiaFuis(banCi2.getXiaFuis());
			if(banCi2.getName()!=null&&!banCi2.getName().equals(banCi.getName())){
				int i = totalDao.getCount("from BanCi where name = ?", banCi2.getName());
				if(i>0)
					return "班次名称已存在，修改失败!";
			}
			String time = Util.getDateTime();
			Integer wxmTime = 0;//班次总时长
			Integer ztxxTime = 0;//中途休息时长
			List<BanCiTime> banCiTime = new ArrayList<BanCiTime>();
			for (BanCiTime banCiTime2 : banCiTimes) {
				if(banCiTime2!=null)
					banCiTime.add(banCiTime2);
			}
			StringBuffer buffer = new StringBuffer();//班次休息时段
			for (int i = 0; i < banCiTime.size(); i++) {
				BanCiTime banCiTime1 = banCiTime.get(i);
				if(banCiTime1==null) continue;
				BanCiTime banCiTime2 = banCiTime1;
				boolean bb = false;//修改
				if(banCiTime1.getId()!=null){
					banCiTime2 = (BanCiTime) totalDao.getObjectById(BanCiTime.class, banCiTime1.getId());
					if(banCiTime2!=null){
						bb = true;
					}else {
						banCiTime2 = banCiTime1;
					}
				}
				
				if("次日".equals(banCiTime1.getDayType())||"次日".equals(banCiTime1.getEdayType())){
					banCi.setIsOvernight("是");
				}
				
				if("是".equals(banCi.getXiaFuis())){
					banCiTime2.setXiaFu(true);
				}else {
					banCiTime2.setXiaFu(false);
				}
				
				Integer xxTime = 0;
				if("当日".equals(banCiTime2.getDayType())&&"次日".equals(banCiTime2.getEdayType())){
					xxTime = (int) (Util.getWorkTime2(banCiTime2.getStartTime(),
							"24:00:00") / 60000);// 此段工作时长
					xxTime += (int) (Util.getWorkTime2("00:00:00",
							banCiTime2.getEndTime()) / 60000);// 此段工作时长
				}else {
					xxTime = (int) (Util.getWorkTime2(banCiTime2.getStartTime(),
							banCiTime2.getEndTime()) / 60000);// 此段工作时长
				}
				buffer.append("第"+banCiTime2.getDuan()+"段考勤时间："+banCiTime2.getStartTime()+"-"+banCiTime2.getEndTime()+"    ");
				banCiTime2.setBanCi(banCi);
				banCiTime2.setAddTime(time);
				banCiTime2.setXxTimeNumber(xxTime);
				banCiTime2.setDayType(banCiTime1.getDayType());
				banCiTime2.setEdayType(banCiTime1.getEdayType());
				banCiTime2.setStartIsFor(banCiTime1.getStartIsFor());
				banCiTime2.setEndIsFor(banCiTime1.getEndIsFor());
				wxmTime += xxTime;
				if(bb)
					banCiTime2.setUpdateTime(time);
				else
					banCiTime2.setAddTime(time);
				if(i==0){
					upfristBanciTime(banCiTime, i, banCiTime1, banCiTime2,
							xxTime);
					banCi.setFirsttime(banCiTime2.getStartTime());
				}else if(i==banCiTime.size()-1){
					ztxxTime = upLastBanCiTime(ztxxTime, banCiTime, i,
							banCiTime1, banCiTime2, xxTime);
					banCi.setFinishtime(banCiTime2.getEndTime());
				}else {
					ztxxTime = upZhongBanCiTime(ztxxTime, banCiTime, i,
							banCiTime1, banCiTime2, xxTime);
				}
				if(bb)
					totalDao.update(banCiTime2);
				else
					totalDao.save(banCiTime2);
			}
			banCi.setGzTime(wxmTime);
			banCi.setXxTime(ztxxTime);
			banCi.setBanCiTimeShow(buffer.toString());
			if(totalDao.update(banCi)){
				return "true";
			}
		}
		return "修改失败！";
	}

	/**
	 * 修改班次，中途时间段
	 * @param ztxxTime
	 * @param banCiTime
	 * @param i
	 * @param banCiTime1
	 * @param banCiTime2
	 * @param xxTime
	 * @return
	 */
	private Integer upZhongBanCiTime(Integer ztxxTime,
			List<BanCiTime> banCiTime, int i, BanCiTime banCiTime1,
			BanCiTime banCiTime2, Integer xxTime) {
		//中途时间段
		if(banCiTime1.getStartBeforeMin()!=null&&banCiTime1.getStartBeforeMin()>0){//允许上班前打卡时间
			banCiTime2.setStartBeforeMin(banCiTime1.getStartBeforeMin());
			banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartBeforeMin()*(-1)));//开始前打卡时间
			ztxxTime += ztxxTime(banCiTime.get(i-1), banCiTime1);
		}else {//最后一段上班时间与上一段下班时间之间的时长/2为允许上班之前时间
			int timeX = 60;
			BanCiTime banCiTime3 = banCiTime.get(i-1);
			if(banCiTime3!=null){
				if(banCiTime3.getEndTime()!=null){
					timeX = (int) (Util.getWorkTime2(banCiTime3.getEndTime(),banCiTime1.getStartTime()) / 60000);
					ztxxTime+=timeX;
				}
			}
			banCiTime2.setStartBeforeMin(timeX/2);
			banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(),timeX/2*(-1)));//开始前打卡时间
		}
		if(banCiTime1.getStartLaterMin()!=null&&banCiTime1.getStartLaterMin()>=0){//上班后打卡
			banCiTime2.setStartLaterMin(banCiTime1.getStartLaterMin());
			banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartLaterMin()));//开始后打卡时间(允许迟到时间)
		}else {
			banCiTime2.setStartLaterMin(30);
			banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), 30));//开始后打卡时间(上午下班有效时间)
		}
		if(banCiTime1.getEndBeforeMin()!=null&&banCiTime1.getEndBeforeMin()>=0){
			banCiTime2.setEndBeforeMin(banCiTime1.getEndBeforeMin());
			banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndBeforeMin()*(-1)));//上班结束前打卡时间(允许早退时间)
		}else {
			int i1 = xxTime-30;
			banCiTime2.setEndBeforeMin(i1);//允许早退时间
			banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), i1*(-1)));//结束前打卡时间(允许早退时间)
		}
		if(banCiTime1.getEndLaterMin()!=null&&banCiTime1.getEndBeforeMin()>0){//允许最晚下班时间
			banCiTime2.setEndLaterMin(banCiTime1.getEndLaterMin());
			banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndLaterMin()));//结束后打卡时间(下午迟到时间)
		}else{
			int timeX = 120;
			BanCiTime banCiTime3 = banCiTime.get(i+1);
			if(banCiTime3!=null){
				if(banCiTime3.getStartTime()!=null){
					timeX = (int) (Util.getWorkTime2(banCiTime1.getEndTime(),banCiTime3.getStartTime()) / 60000);
				}
			}
			banCiTime2.setEndLaterMin(timeX/2);
			banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), timeX/2));//结束后打卡时间(允许下班时间)
		}
		isfor(banCiTime2);
		return ztxxTime;
	}

	/**
	 * 修改班次，最后一时间段
	 * @param ztxxTime
	 * @param banCiTime
	 * @param i
	 * @param banCiTime1
	 * @param banCiTime2
	 * @param xxTime
	 * @return
	 */
	private Integer upLastBanCiTime(Integer ztxxTime,
			List<BanCiTime> banCiTime, int i, BanCiTime banCiTime1,
			BanCiTime banCiTime2, Integer xxTime) {
		//最后一段上班时间
		if(banCiTime1.getStartTime()!=null&&!"".equals(banCiTime1.getStartTime())&&banCiTime1.getEndTime()!=null&&!"".equals(banCiTime1.getEndTime())){
			if(banCiTime1.getStartBeforeMin()!=null&&banCiTime1.getStartBeforeMin()>0){//允许上班前打卡时间
				banCiTime2.setStartBeforeMin(banCiTime1.getStartBeforeMin());
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartBeforeMin()*(-1)));//开始前打卡时间
				ztxxTime += ztxxTime(banCiTime.get(i-1), banCiTime1);
			}else {//最后一段上班时间与上一段下班时间之间的时长/2为允许上班之前时间
				int timeX = 60;
				BanCiTime banCiTime3 = banCiTime.get(i-1);
				if(banCiTime3!=null){
					if(banCiTime3.getEndTime()!=null){
						timeX = (int) (Util.getWorkTime2(banCiTime3.getEndTime(),banCiTime1.getStartTime()) / 60000);
						ztxxTime+=timeX;
					}
				}
				banCiTime2.setStartBeforeMin(timeX/2);
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(),timeX/2*(-1)));//开始前打卡时间
			}
			if(banCiTime1.getStartLaterMin()!=null&&banCiTime1.getStartLaterMin()>=0){//上班后打卡
				banCiTime2.setStartLaterMin(banCiTime1.getStartLaterMin());
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartLaterMin()));//开始后打卡时间(允许迟到时间)
			}else {
				banCiTime2.setStartLaterMin(30);
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), 30));//开始后打卡时间(上午下班有效时间)
			}
			if(banCiTime1.getEndBeforeMin()!=null&&banCiTime1.getEndBeforeMin()>=0){
				banCiTime2.setEndBeforeMin(banCiTime1.getEndBeforeMin());
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndBeforeMin()*(-1)));//上班结束前打卡时间(允许早退时间)
			}else {
				int i1 = xxTime-30;
				banCiTime2.setEndBeforeMin(i1);//允许早退时间
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), i1*(-1)));//结束前打卡时间(允许早退时间)
			}
			if(banCiTime1.getEndLaterMin()!=null&&banCiTime1.getEndBeforeMin()>0){//允许最晚下班时间
				banCiTime2.setEndLaterMin(banCiTime1.getEndLaterMin());
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndLaterMin()));//结束后打卡时间(下午迟到时间)
			}else{
				banCiTime2.setEndLaterMin(60);
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), 60));//结束后打卡时间(允许下班时间)
			}
			isfor(banCiTime2);
		}
		return ztxxTime;
	}

	/**
	 * 修改班次，第一时间段	 
	 * @param banCiTime
	 * @param i
	 * @param banCiTime1
	 * @param banCiTime2
	 * @param xxTime
	 */
	private void upfristBanciTime(List<BanCiTime> banCiTime, int i,
			BanCiTime banCiTime1, BanCiTime banCiTime2, Integer xxTime) {
		if(banCiTime1.getStartTime()!=null&&!"".equals(banCiTime1.getStartTime())&&banCiTime1.getEndTime()!=null&&!"".equals(banCiTime1.getEndTime())){
			if(banCiTime1.getStartBeforeMin()!=null&&banCiTime1.getStartBeforeMin()>0){//允许上班前打卡时间
				banCiTime2.setStartBeforeMin(banCiTime1.getStartBeforeMin());
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartBeforeMin()*(-1)));//开始前打卡时间
			}else {
				banCiTime2.setStartBeforeMin(120);
				banCiTime2.setStartBeforeTime(Util.getminuteAfter(banCiTime1.getStartTime(),-120));//开始前打卡时间
			}
			if(banCiTime1.getStartLaterMin()!=null&&banCiTime1.getStartLaterMin()>=0){//上班后打卡
				banCiTime2.setStartLaterMin(banCiTime1.getStartLaterMin());
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), banCiTime1.getStartLaterMin()));//开始后打卡时间(允许迟到时间)
			}else {
				banCiTime2.setStartLaterMin(30);
				banCiTime2.setStartLaterTime(Util.getminuteAfter(banCiTime1.getStartTime(), 30));//开始后打卡时间(上午下班有效时间)
			}
			if(banCiTime1.getEndBeforeMin()!=null&&banCiTime1.getEndBeforeMin()>=0){
				banCiTime2.setEndBeforeMin(banCiTime1.getEndBeforeMin());
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndBeforeMin()*(-1)));//上班结束前打卡时间(允许早退时间)
			}else {
				int i1 = xxTime-30;
				banCiTime2.setEndBeforeMin(i1);//允许早退时间
				banCiTime2.setEndBeforeTime(Util.getminuteAfter(banCiTime1.getEndTime(), i1*(-1)));//结束前打卡时间(允许早退时间)
			}
			if(banCiTime1.getEndLaterMin()!=null&&banCiTime1.getEndBeforeMin()>0){//允许最晚下班时间
				banCiTime2.setEndLaterMin(banCiTime1.getEndLaterMin());
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), banCiTime1.getEndLaterMin()));//结束后打卡时间(下午迟到时间)
			}else{//第一段时间下班时间最迟为下一段时间的上班时间期间的时间/2允许下班最迟时间
				int timeX = 120;
				if(banCiTime.size()>1){
					BanCiTime banCiTime3 = banCiTime.get(i+1);
					if(banCiTime3!=null){
						if(banCiTime3.getStartTime()!=null){
							timeX = (int) (Util.getWorkTime2(banCiTime1.getEndTime(),banCiTime3.getStartTime()) / 60000);
						}
					}
				}
				banCiTime2.setEndLaterMin(timeX/2);
				banCiTime2.setEndLaterTime(Util.getminuteAfter(banCiTime1.getEndTime(), timeX/2));//结束后打卡时间(允许下班时间)
			}
			isfor(banCiTime2);
		}
	}

	@Override
	public String bangDingBanci(Integer[] usersId, Integer integer) {
		// TODO Auto-generated method stub
		boolean b = true;
		if (integer > 0 && integer != null) {
			BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, integer);
			if (banCi != null) {
				if (usersId != null && usersId.length > 0) {
					for (int i = 0; i < usersId.length; i++) {
						Users user = (Users) totalDao.getObjectById(
								Users.class, usersId[i]);// 查询用户
						if (user != null) {
							user.setBanci_id(banCi.getId());
							user.setBanci_name(banCi.getName());
							b = b & totalDao.save(user);
						} else {
							System.out.println("用户不存在！绑定失败");
						}
					}
					return "绑定成功！";
				}
				return "选择用户为空！";
			}
			return "班次不存在，绑定失败！";
		}
		return "班次ID为空！";
	}

	@Override
	public List<Users> findAllBangBc(Integer id) {
		// TODO Auto-generated method stub
		String hql = "from Users where banci_id = ?";
		return totalDao.query(hql, id);
	}

	// 已经绑定过的users
	@Override
	public Object[] findAllTagw(Integer id, Users users, int parseInt,
			int pageSize,String tag) {
		// TODO Auto-generated method stub
		if (users == null) {
			users = new Users();
		}
		String oldDept = "";
		String dept = "";
		if("dept".equals(tag)){
			Users users2 = Util.getLoginUser();
			oldDept = users2.getDept();
			dept = oldDept;
		}else {
			if(users.getDept()!=null){
				String[] dept1 = users.getDept().split(";");
				if(dept1.length>0){
					oldDept = users.getDept();
					dept = oldDept.replaceAll(";","','");
					users.setDept(null);
				}
			}
		}
		String hql = "";
		hql = totalDao.criteriaQueries(users, null);
		if(!"".equals(dept)){
			hql += " and dept in ('"+dept+"')";
			users.setDept(oldDept);
			pageSize = 500;
		}else {
			pageSize = 15;
		}
		hql += " and banci_name is not null and banci_name <> '' and banci_id = ? and onWork <> '离职' order by dept";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize, id);
		int count = totalDao.getCount(hql, id);
		Object[] o = { list, count };
		return o;
	}

	// 未绑定过的users
	@Override
	public Object[] findAllTagw(Users users, int parseInt, int pageSize,String tag) {
		// TODO Auto-generated method stub
		if (users == null) {
			users = new Users();
		}
		String oldDept = "";
		String dept = "";
		
		if("dept".equals(tag)){
			Users users2 = Util.getLoginUser();
			oldDept = users2.getDept();
			dept = oldDept;
		}else {
			if(users.getDept()!=null){
				String[] dept1 = users.getDept().split(";");
				if(dept1.length>0){
					oldDept = users.getDept();
					dept = oldDept.replaceAll(";","','");
					users.setDept(null);
				}
			}
		}
		
		String hql = "";
		hql = totalDao.criteriaQueries(users, null);
		if(!"".equals(dept)){
			hql += " and dept in ('"+dept+"')";
			users.setDept(oldDept);
			pageSize = 500;
		}
		hql += " and (banci_name is null or banci_name = '') and banci_id is null and onWork <> '离职' and code is not null order by dept";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public String jieBangBanci(Integer[] usersId, Integer integer) {
		// TODO Auto-generated method stub
		boolean b = true;
		if (integer > 0 && integer != null) {
			if (usersId != null && usersId.length > 0) {
				for (int i = 0; i < usersId.length; i++) {
					Users user = (Users) totalDao.getObjectById(Users.class,
							usersId[i]);// 查询用户
					if (user != null) {
						user.setBanci_id(null);
						user.setBanci_name(null);
						b = b & totalDao.save(user);
					} else {
						System.out.println("用户不存在！解绑失败");
					}
				}
				return "解绑成功！";
			}
			return "选择用户为空！";
		}
		return "班次不存在，解绑失败！";
	}

	@Override
	public void shengchengimagenum() {
		// TODO Auto-generated method stub
		String st = "'000647','000690','000806','000953','000972'," +
				"'000980','001158','001286','001486','002046'," +
				"'002458','002781','002790','002825','002911'," +
				"'003902','004139','004169','004183','004373'," +
				"'004440','004565','004892','005251','005655'," +
				"'006287','006291','006319','006493'," +
				"'006609','006836','006889','007579','007797'," +
				"'008467','008655','008667','008676','008696'," +
				"'008805','008893','009150','009212','009275'," +
				"'009361','009519','009739','010102','010248'," +
				"'010262','011170','011189','011714','011915'," +
				"'011916','012632','012638','012692','012698'," +
				"'013005','013039','013046','013051','013070'," +
				"'013111','013209','013415','013450','013454'," +
				"'013474','013633','013661','013673','013739'," +
				"'013740','013759','013760','013762','013917'," +
				"'014258','015288','015557','015591','015711'," +
				"'015935','016029','016263','016833','016924'," +
				"'016947','017034','017135','017184','017281'," +
				"'017369','017529','017534','017536','017612'," +
				"'017614','017892','018017','018047','018089'," +
				"'018193','018273','018394','018426','018572'," +
				"'018750','018859','018887','018920','019050'," +
				"'019073','019120','019132','019232','019346'," +
				"'019531','019568','019671','019694','019718'," +
				"'019722','019762','019805','019918','019976'," +
				"'020078','020167','020211','020212','020234'," +
				"'020246','020281','020285','020322','020354'," +
				"'020378','020400','020407','020417','020441'," +
				"'020446','020457','020461','020527','020530'," +
				"'020543','020564','020640','020648','020649'," +
				"'020657','020658','020712','020727','020765'," +
				"'020767','020872','020877','020882','020967'," +
				"'021018','021019','021055','021056','021063'," +
				"'021082','021083','021103','021108','021229'," +
				"'021231','021253','021261','021265','021314'," +
				"'021335','021396','021403','021408','021418'," +
				"'021428','021439','021456','021473','021501'," +
				"'021580','021582','021588','021593','021598'," +
				"'021650','021695','021788','022055','022084'," +
				"'022092','022135','022190','022199','022237'," +
				"'022262','022351','022374','022477','022484'," +
				"'022631','022673','022773','022780','022876'," +
				"'022889','022968','022981','023082','023284'," +
				"'023373','023391','023507','023527','023532'," +
				"'023575','023639','023665','023670','023678'," +
				"'023694','023746','023874','023941','024002'," +
				"'024016','024025','024027','024033','024039'," +
				"'024055','024064','024066','024071','024113'," +
				"'024114','024129','024131','024135','024145'," +
				"'024148','024212','024220','024249','024293'," +
				"'024392','024395','024434','024530','024534'," +
				"'024540','024543','024592','024619','024631'," +
				"'024644','024645','024674','024694','024703'," +
				"'024744','024807','024812','024829','024836'," +
				"'024854','024886','024891','024925','024944'," +
				"'024968','024971','025057','025074','025114'," +
				"'025132','025183','025232','025236','025238'," +
				"'025239','025243','025261','025270','025337'," +
				"'025377','025417','025514','025622','025719'," +
				"'025755','025776','025822','025823','025848'," +
				"'025912','025957','026020','026029','026093'," +
				"'026095','026237','026259','026304','026333'," +
				"'026362','026488','026527','026555'," +
				"'026667','026674'," +
				"'026700','026719','026741','026750','026755'," +
				"'026798','026811','026827','026909','026928'," +
				"'026965','026974','027047','027059','027079'," +
				"'027105','027148','027220','027272','027273'," +
				"'027290'";
		List<Users> userlist = totalDao
				.query("from Users where code in ("+st+") order by code");
		if (userlist != null && userlist.size() > 0) {
			int i = 1;
			for (Users s : userlist) {
				s.setCodeIdNum(i);
				totalDao.update2(s);
				i++;
			}
		}
//		String [] si = st.split("','");
//		System.out.println("si:"+si);
//		for (int i = 0; i<si.length ; i++) {
//			int f = i+1;
//			Users users = (Users) totalDao.getObjectByCondition("from Users where code = ?", si[i]);
//			if (users!=null) {
//				users.setCodeIdNum(f);
//				totalDao.update2(users);
//			}
//		}
//		List<Users> userlist = totalDao
//				.query("from Users where codeIdNum > 16");
//		if (userlist != null && userlist.size() > 0) {
//			int i = 1;
//			for (Users s : userlist) {
//				s.setCodeIdNum(s.getCodeIdNum()-1);
//				totalDao.update2(s);
//				i++;
//			}
//		}
	}

	@Override
	public String changeBanCi(String banci1, String banci2, String i) {
		// TODO Auto-generated method stub
		if("1".equals(i)){//对调
			int i1 = Integer.parseInt(banci1);
			int i2 = Integer.parseInt(banci2);
			BanCi banCi1 = salBanCiByid(i1);
			if(banCi1==null) return "班次1为空";
			BanCi banCi2 = salBanCiByid(i2);
			if(banCi2==null) return "班次2为空";
			List<Users> list1 = totalDao.query("from Users where banci_id = ? and onWork <> '离职'", i1);
			List<Users> list2 = totalDao.query("from Users where banci_id = ? and onWork <> '离职'", i2);
			if(list1!=null&&list1.size()>0){
				if(list2!=null&&list2.size()>0){
					for (Users u : list2) {
						u.setBanci_name(banCi1.getName());
						u.setBanci_id(banCi1.getId());
						totalDao.update2(u);
					}
					for (Users u : list1) {
						u.setBanci_name(banCi2.getName());
						u.setBanci_id(banCi2.getId());
						totalDao.update2(u);
					}
				}else {
					for (Users u : list1) {
						u.setBanci_name(banCi2.getName());
						u.setBanci_id(banCi2.getId());
						totalDao.update2(u);
					}
				}
			}else {
				if(list2!=null&&list2.size()>0){
					for (Users u : list2) {
						u.setBanci_name(banCi1.getName());
						u.setBanci_id(banCi1.getId());
						totalDao.update2(u);
					}
				}
			}
			return "调换成功！";
		}else if("2".equals(i)){//将1班次所有绑定给班次2
			int i1 = Integer.parseInt(banci1);
			int i2 = Integer.parseInt(banci2);
			BanCi banCi1 = salBanCiByid(i1);
			if(banCi1==null) return "班次1为空";
			BanCi banCi2 = salBanCiByid(i2);
			if(banCi2==null) return "班次2为空";
			List<Users> list1 = totalDao.query("from Users where banci_id = ? and onWork <> '离职'", i1);
			if(list1!=null&&list1.size()>0){
				for (Users u : list1) {
					u.setBanci_name(banCi2.getName());
					u.setBanci_id(banCi2.getId());
					totalDao.update2(u);
				}
			}else {
				return "班次1为空，调换失败！";
			}
			return "调换成功！";
		}
		return "请选择操作类型";
	}

	@Override
	public List<SchedulingTable> fandSchedulIngByDate(Integer banCiId, String startTime, String endTime) {
		
		BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, banCiId);
		if(banCi==null) {
			return null;
		}
		List<SchedulingTable> list = totalDao.query("from SchedulingTable"
				+ " where banCi.id=? and dateTime>=? and dateTime<=? and users.id is null",banCiId,startTime,endTime);
		if(list==null ) {
			list = new ArrayList<SchedulingTable>();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date afterDate=simpleDateFormat.parse(startTime);
			String afterDateStr =simpleDateFormat.format(afterDate);//增加后一天后的时间
			String searchFormat=null;
			String apiAddress = "http://api.goseek.cn/Tools/holiday?date=";
			while(true) {
//				list.contains(arg0)
				SchedulingTable table = (SchedulingTable) totalDao.getObjectByCondition("from SchedulingTable where"
						+ " dateTime = ? and banCi.id=? and users.id is null",afterDateStr,banCiId );
				if(!list.contains(table)) {
					//获取是否是节假日
					searchFormat = searchDateFormat.format(afterDate);
//					String result = PostUtil.getRequest(apiAddress+searchFormat);
//					JSONObject parseObject = JSON.parseObject(result);
//					Integer dataType = parseObject.getInteger("data");
					Integer dataType = 0;
					table = new SchedulingTable();
					table.setDataType(dataType);
					table.setDateTime(afterDateStr);
					table.setBanCi(banCi);
					table.setAddtime(Util.getDateTime());
					totalDao.save(table);
					list.add(table);
				}
				if(afterDateStr!=null && afterDateStr.equals(endTime)) {
					return list;
				}
				afterDate = Util.getCalendarDate(afterDate, 1);
				afterDateStr = simpleDateFormat.format(afterDate);
			}
			//return list;
		} catch (ParseException e) {
			throw new RuntimeException("日期转换异常："+e.toString());
		}
		
		
	}

	@Override
	public String settingScheduling(SchedulingTable schedulingTable, String tag) {
		Users loginUser = Util.getLoginUser();
		if(loginUser==null) {
			return "请先登录";
		}
		if(schedulingTable==null ) {
			return "无参数，请添加具体参数";
		}
		boolean update=false;
		if(schedulingTable.getId()==null) {
			schedulingTable.setAddtime(Util.getDateTime());
			schedulingTable.setCardId(loginUser.getCardId());
			schedulingTable.setCode(loginUser.getCode());
			schedulingTable.setDept(loginUser.getDept());
			update = totalDao.save(schedulingTable);
		}else {
			
			SchedulingTable table = (SchedulingTable) totalDao.getObjectById(SchedulingTable.class, schedulingTable.getId());
			
			//判断是否是历史日期，小于今天的不能修改
			String dateTime = table.getDateTime();
			String currentDateTime = Util.getDateTime();
			String yesterday = Util.getCalendarModified(currentDateTime, 5,-1);
			String format = "yyyy-MM-dd";
			boolean compareTime = Util.compareTime(yesterday, format, dateTime, format);
			if(compareTime) {
				throw new RuntimeException("不能修改历史排班");
			}
			
			table.setUpdatetime(currentDateTime);
			table.setDataType(schedulingTable.getDataType());
			table.setRemarks(schedulingTable.getRemarks());
			//schedulingTable.setUpdatetime(Util.getDateTime());
			update = totalDao.update(table);
		}
		
		
		return update+"";
	}

	@Override
	public List<SchedulingTable> fandSchedulIngBySelfDate(Integer userId, String startTime, String endTime) {
		Users users = (Users) totalDao.getObjectById(Users.class, userId);
		if(users==null) {
			return null;
		}
		BanCi banCi =null;
		if(users.getBanci_id()!=null) {
			banCi = (BanCi) totalDao.getObjectById(BanCi.class, users.getBanci_id());
			
		}
		List<SchedulingTable> list = totalDao.query("from SchedulingTable"
				+ " where users.id=? and dateTime>=? and dateTime<=?",userId,startTime,endTime);
		if(list==null ) {
			list = new ArrayList<SchedulingTable>();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date afterDate=simpleDateFormat.parse(startTime);
			String afterDateStr =simpleDateFormat.format(afterDate);//增加后一天后的时间
			String searchFormat=null;
			String apiAddress = "http://api.goseek.cn/Tools/holiday?date=";
			while(true) {
//				list.contains(arg0)
				SchedulingTable table = (SchedulingTable) totalDao.getObjectByCondition("from SchedulingTable where"
						+ " dateTime = ?  and users.id is not null and users.id=?",afterDateStr,userId );
				if(!list.contains(table)) {
					//获取是否是节假日
					searchFormat = searchDateFormat.format(afterDate);
//					String result = PostUtil.getRequest(apiAddress+searchFormat);
//					JSONObject parseObject = JSON.parseObject(result);
//					Integer dataType = parseObject.getInteger("data");
					Integer dataType = 0;
					table = new SchedulingTable();
					table.setDataType(dataType);
					table.setDateTime(afterDateStr);
					//table.setBanCi(banCi);
					table.setUsers(users);
					if(dataType==0 && users.getBanci_id()!=null) {
						table.setBanCi(banCi);
						table.setBanci_name(banCi.getName());
					}
					table.setAddtime(Util.getDateTime());
					totalDao.save(table);
					list.add(table);
				}
				if(afterDateStr!=null && afterDateStr.equals(endTime)) {
					return list;
				}
				afterDate = Util.getCalendarDate(afterDate, 1);
				afterDateStr = simpleDateFormat.format(afterDate);
			}
			//return list;
		} catch (ParseException e) {
			throw new RuntimeException("日期转换异常："+e.toString());
		}
	}

	//根据员工排班
	@Override
	public String settingSchedulingByUsersBanci(SchedulingTable schedulingTable, String usersIds) {
//		SchedulingTable scheduling = (SchedulingTable) totalDao.getObjectById(SchedulingTable.class, schedulingTable.getId());
//		if(scheduling==null) {
//			return "对象为空";
//		}
		Integer dataType = schedulingTable.getDataType();
		BanCi banCi = null;
		if(dataType==0) {
			if(schedulingTable.getBanCi()==null || schedulingTable.getBanCi().getId()==null) {
				return "工作日请选择班次";
			}
			Integer banciId = schedulingTable.getBanCi().getId();
			banCi = (BanCi) totalDao.getObjectById(BanCi.class, banciId);
			if(banCi==null) {
				return "班次不存在";
			}
		}
		String[] usersIdStr = usersIds.split(",");
		SchedulingTable scheduling = null;
		for (String string : usersIdStr) {
			Integer usersId = Integer.parseInt(string);
			scheduling = (SchedulingTable) totalDao.getObjectByCondition("from SchedulingTable"
					+ " where users.id=? and dateTime=?", usersId,schedulingTable.getDateTime());
			if(scheduling==null) {
				List<SchedulingTable> list = fandSchedulIngBySelfDate(usersId, schedulingTable.getDateTime(), schedulingTable.getDateTime());
				scheduling = list.get(0);
			}
			if(banCi!=null) {
				scheduling.setBanCi(banCi);
				scheduling.setBanci_name(banCi.getName());
			}else {
				scheduling.setBanci_name(null);
				scheduling.setBanCi(null);
			}
			scheduling.setDataType(dataType);
			scheduling.setRemarks(schedulingTable.getRemarks());
			boolean update = totalDao.update(scheduling);
			if(!update) {
				return "修改失败";
			}
		}
		
		return "修改成功";
	}
	
	/**
	 * 
	 * @param userId 用户Id
	 * @param dateTime 查询时间
	 * @return map 
	 * 	usersId 用户Id
	 * 	banciId 调换后的班次	不上班这项为空
	 * 	type	节日类型 (0:工作日，1.公休日,2.节假日)
	 * 	date	查询的时间
	 */
	public static Map<String, Object> updateORsearchIfyouReWork(Integer userId,Integer banciId,String dateTime,TotalDao totalDao){
		Map<String, Object> map = new HashMap<String,Object>();
		if(totalDao==null) {
			totalDao = TotalDaoImpl.findTotalDao();
		}
		SchedulingTable schedulingTable=null;//and users is not null and users.id is not null 
		Users users=null;
		if(userId!=null ) {
			users = (Users) totalDao.getObjectById(Users.class, userId);
			if(users==null) {
				throw new RuntimeException("所查找的员工不存在");
			}
			banciId = users.getBanci_id();
			schedulingTable = (SchedulingTable) totalDao.getObjectByQuery("from SchedulingTable where"
					+ " dateTime = ? and users.id=?",dateTime,userId );
		}
		
		if(schedulingTable!=null && schedulingTable.getDataType()!=null) {  //设置了个人排班
			BanCi banCi = schedulingTable.getBanCi();
			map.put("usersId", userId);
			if(banCi!=null) {
				map.put("banciId", banCi.getId());
			}
			map.put("type", schedulingTable.getDataType());//是否上班
			map.put("date", schedulingTable.getDateTime());
//			map.put("start", "");
		}else {	//按照班次排班
			Integer banci_id = banciId;
			SchedulingTable table = (SchedulingTable) totalDao.getObjectByQuery("from SchedulingTable where"
					+ " dateTime = ? and banCi.id=?",dateTime,banci_id );
			if(table!=null) {
				if(table.getDataType()==0) {
					map.put("banciId", banci_id);
				}
			}else {
				SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date= Util.StringToDate(dateTime, "yyyy-MM-dd");
				String searchFormat=null;
				Integer dataType=0;
				try {
					String apiAddress = "http://api.goseek.cn/Tools/holiday?date=";
					//获取是否是节假日
					searchFormat = searchDateFormat.format(date);
					String result = PostUtil.getRequest(apiAddress+searchFormat);
					JSONObject parseObject = JSON.parseObject(result);
					dataType = parseObject.getInteger("data");
				} catch (Exception e) {
					System.out.println("获取节假日异常");
				}
				table = new SchedulingTable();
				table.setDataType(dataType);
				table.setDateTime(dateTime);
				table.setUsers(users);
				if(dataType==0) {
					BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, banci_id);
					table.setBanCi(banCi);
					table.setBanci_name(banCi.getName());
					map.put("banciId", banCi.getId());
				}
				table.setAddtime(Util.getDateTime());
				totalDao.save(table);
			}
			map.put("usersId", userId);
			map.put("type", table.getDataType());//是否上班
			map.put("date", table.getDateTime());
		}
		
		return map;
	}
}