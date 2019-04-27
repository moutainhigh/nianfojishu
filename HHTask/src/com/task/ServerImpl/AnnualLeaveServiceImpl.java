package com.task.ServerImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.IAnnualLeaveService;
import com.task.entity.AnnualLeave;
import com.task.entity.AskForLeave;
import com.task.entity.Overtime;
import com.task.entity.Users;
import com.task.entity.banci.BanCi;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.menjin.RechargeZhi;
import com.task.util.Util;

@SuppressWarnings("unchecked")
public class AnnualLeaveServiceImpl implements IAnnualLeaveService {

	private TotalDao totalDao;
	private final int FIVE=5;
	private final int TEN=10;
	private final int FIFTEEN=15;
	
	
	public  void qinkongdept() {
		Users user = (Users) Util.getLoginUser();
		String hql = "  from AnnualLeave  where status='换休' and dept=? ";
		List list = totalDao.query(hql, user.getDept());
		if (list!=null) {
			for (int i = 0; i < list.size(); i++) {
				AnnualLeave newA=(AnnualLeave) list.get(i);
				newA.setSurplus(0F);
				totalDao.update(newA);
			}
		}
	}
	
	/*
	 * 根据工号查询换休
	 */
	public AnnualLeave BynameHuanxiu(String personcode) {
		Users users = (Users) totalDao.getObjectByCondition("from Users where code='"+personcode+"'");
		
		// 换休有效期→查询系统设置
		String queryOverTime = "from Overtime where overtimeCode = ? and huanxiu='是' and (status='同意' or status='已打卡')";
		CodeTranslation codeTranslation = (CodeTranslation) totalDao
				.getObjectByCondition("from CodeTranslation where type='sys' and valueName='换休有效期'");
		List<Overtime> list = null;
		if (null == codeTranslation) {
			list = totalDao.query(queryOverTime, personcode);
		} else {
			if (Integer.parseInt(codeTranslation.getValueCode()) > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.MONTH, -Integer.parseInt(codeTranslation.getValueCode()));
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String passDate = format.format(calendar.getTime());
				list = totalDao.query(queryOverTime+" and startDate>?", personcode, passDate);
			}
		}
	
		float overTimeLong = 0.0f;//小时
		for (Overtime overtime : list) {
			if(overtime.getUsablehxTime()==null) {
				overtime.setUsablehxTime(0f);
			}
			overTimeLong += overtime.getUsablehxTime();//可用换休时间
		}
		
		String hql1 = " from AnnualLeave  where status ='换休' and jobNum=?";
		AnnualLeave annualLeave = (AnnualLeave) totalDao.getObjectByCondition(hql1, personcode);
		if(null!=annualLeave && null!= annualLeave.getSurplus()){
			BigDecimal bg = new BigDecimal(annualLeave.getSurplus());
			float floatValue = bg.setScale(BigDecimal.ROUND_HALF_UP, 2).floatValue();
			annualLeave.setSurplus(floatValue);
		}else{
			if(overTimeLong>0){
				annualLeave = new AnnualLeave();
				annualLeave.setDept(users.getDept());
				annualLeave.setJobNum(users.getCode());
				annualLeave.setName(users.getName());
				annualLeave.setStatus("换休");
			}else{
				return null;
			}
		}
		
		BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users.getBanci_id());
		Float gzTime = 8f; //每天工作时长
		if(null !=banCi && null!=banCi.getGzTime()){
			gzTime = banCi.getGzTime()/60f;
		}
		if (gzTime == null || gzTime == 0) {
			gzTime = 8f;
		}
		Float surplus =overTimeLong/gzTime;
		annualLeave.setSurplus(surplus );
		if(null==annualLeave.getId()){
			totalDao.save(annualLeave);
		}else{
			totalDao.update(annualLeave);
		}
		return annualLeave;
	}
	
	public List listhuanxiumingxi(String carId) {
		//String hql = "  from AskForLeave  where approvalStatus='同意' and leaveTypeOf='年休'  and leaveUserCardId=?";
		//String hql = "  from Overtime  where  huanxiustatus='已换休' " +
		//"and status !='打回' and  overtimeCode=?";
		String hql= "  from AskForLeave  where leaveTypeOf='换休'  and leaveStartDate>'2014-10-01' and  leavePersonCode=? order by leaveId desc";
		List list = totalDao.query(hql, carId);
		return list;
		
	}
	public void gengxinhuanxiu() {
		String hql = "  from Users  where onWork in  ('试用','在职')  and  dept!='供应商'";
		List list = totalDao.query(hql);
		if (list!=null) {
			for (int i = 0; i < list.size(); i++) {
				Users use=(Users) list.get(i);
				//换休时间----------------------------------------  --- 
				//当前时间的三个月后
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//String currentDate = format.format(new Date());//截止时间deadline
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.MONTH, -3);
				String deadline = format.format(calendar.getTime());
				String hql2 = "  from Overtime  where  huanxiustatus is null " +
						"and status in  ('同意','加班前未刷卡','加班后未刷卡','未刷卡') and " +
						" huanxiu='是' and  startDate >'2014-10-01'  and   overtimeCode=? and startDate> ?";
				List list2 = totalDao.query(hql2,use.getCode(),deadline);
				Float num=0F;
				if (list2!=null) {
					for (int j = 0; j <list2.size(); j++) {
						Overtime newot=(Overtime) list2.get(j);
						Date appStartTime=Util.StringToDate(newot.getStartDate(), "yyyy-MM-dd HH:mm:ss");//申请加班开始时间
						Date appEndTime=Util.StringToDate(newot.getEndDate(), "yyyy-MM-dd HH:mm:ss");//申请加班结束时间
						float diff = (float) ((appEndTime.getTime() - appStartTime.getTime())/(1000.0*60.0*60.0)/8.0);
						num=num+diff;
						newot.setHuanxiustatus("已换休");
						totalDao.save(newot);
					}
					
				}
				//---------------------------------------------------------
				String hql3 = "  from AskForLeave  where leaveTypeOf='换休' and  approvalStatus='同意'  and  huanxiustatus is null " +
				"and  leaveStartDate>'2014-10-01'  and  leavePersonCode=?";
				List list3 = totalDao.query(hql3,use.getCode());
				Float qinjianum=0F;
				if (list3!=null) {
					for (int k = 0; k < list3.size(); k++) {
						AskForLeave newa=(AskForLeave) list3.get(k);
						qinjianum=newa.getLeaveDays()+newa.getLeaveHours()/8;
						newa.setHuanxiustatus("已换休");
						totalDao.save(newa);
					}
				}
				//-------------------------------------------------------------------
				String hql4 = " from  AnnualLeave  where status='换休' and  jobNum=?";
				AnnualLeave huanxiu = (AnnualLeave) totalDao
						.getObjectByCondition(hql4, use.getCode());
				//AnnualLeave saveaAnnualLeave1 = huanxiu;
				if (huanxiu!=null) {
					huanxiu.setSurplus(huanxiu.getSurplus()+num-qinjianum);
					totalDao.update(huanxiu);
				} else {
					AnnualLeave newA=new AnnualLeave();
					newA.setJobNum(use.getCode());
					newA.setName(use.getName());
					newA.setDept(use.getDept());
					newA.setSurplus(num-qinjianum);
					newA.setStatus("换休");
					totalDao.save(newA);
				}
				//----------
			}
		}
		
	}
	public void gengxinhuanxiu(String code) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -3);
		String formatDate = format.format(calendar.getTime());
		
		List<Overtime> list = totalDao.query("from Overtime where applyCode =? and status='同意'" +
				" and huanxiustatus='已换休' and startDate>? and huanxiu='是'", code,formatDate);
	
		float overTimeLong = 0.0f;
		for (Overtime overtime : list) {
			overTimeLong += overtime.getOverTimeLong();
		}
		
		String hql1 = " from AnnualLeave where status ='换休' and jobNum=?";
		AnnualLeave annualLeave = (AnnualLeave) totalDao.getObjectByCondition(
				hql1, code);
		Users users = (Users) totalDao.getObjectByCondition("from Users where code='"+code+"'");
		BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users.getBanci_id());
		Float gzTime = 8f; //每天工作时长
		if(null !=banCi && null!=banCi.getGzTime()){
			gzTime = banCi.getGzTime()/60f;
		}
		if (gzTime == null || gzTime == 0) {
			gzTime = 8f;
		}
		Float surplus =overTimeLong/gzTime;
		annualLeave.setSurplus(surplus );
		
		totalDao.update(annualLeave);
//		String hql = "  from Users  where onWork in  ('试用','在职')  and  dept!='供应商' and code = ?";
//		List list = totalDao.query(hql,code);
//		if (list!=null) {
//			for (int i = 0; i < list.size(); i++) {
//				Users use=(Users) list.get(i);
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mms:ss");
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(new Date());
//				calendar.add(Calendar.MONTH, -3);
//				String deadline = format.format(calendar.getTime());
//				
//				BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class,use.getBanci_id());
//				float gzTime = banCi.getGzTime()/60f;
//				//换休时间----------------------------------------  --- 
//				String hql2 = "  from Overtime  where  huanxiustatus is null " +
//				"and status in  ('同意','加班前未刷卡','加班后未刷卡','未刷卡') and  huanxiu='是' and  startDate >'2014-10-01'  and   overtimeCode=? and startDate>?";
//				List list2 = totalDao.query(hql2,use.getCode(),deadline);
//				Float num=0F;
//				if (null!=list2 && list2.size()>0) {
//					for (int j = 0; j <list2.size(); j++) {
//						Overtime newot=(Overtime) list2.get(j);
//						Date appStartTime=Util.StringToDate(newot.getStartDate(), "yyyy-MM-dd HH:mm:ss");//申请加班开始时间
//						Date appEndTime=Util.StringToDate(newot.getEndDate(), "yyyy-MM-dd HH:mm:ss");//申请加班结束时间
//						float diff = (float) ((appEndTime.getTime() - appStartTime.getTime())/(1000.0*60.0*60.0)/gzTime);
//						num=num+diff;
//						newot.setHuanxiustatus("已换休");
//						totalDao.save(newot);
//					}
//					
//				}
//				//---------------------------------------------------------
//				String hql3 = "  from AskForLeave  where leaveTypeOf='换休' and  approvalStatus='同意'  and  huanxiustatus is null " +
//				"and  leaveStartDate>'2014-10-01'  and  leavePersonCode=?";
//				List list3 = totalDao.query(hql3,use.getCode());
//				Float qinjianum=0F;
//				if (list3!=null) {
//					for (int k = 0; k < list3.size(); k++) {
//						AskForLeave newa=(AskForLeave) list3.get(k);
//						qinjianum=newa.getLeaveDays()+newa.getLeaveHours()/8;
//						newa.setHuanxiustatus("已换休");
//						totalDao.save(newa);
//					}
//				}
//				//-------------------------------------------------------------------
//				String hql4 = " from  AnnualLeave  where status='换休' and  jobNum=?";
//				AnnualLeave huanxiu = (AnnualLeave) totalDao
//				.getObjectByCondition(hql4, use.getCode());
//				//AnnualLeave saveaAnnualLeave1 = huanxiu;
//				if (huanxiu!=null) {
//					huanxiu.setSurplus(huanxiu.getSurplus()+num-qinjianum);
//					totalDao.update(huanxiu);
//				} else {
//					AnnualLeave newA=new AnnualLeave();
//					newA.setJobNum(use.getCode());
//					newA.setName(use.getName());
//					newA.setDept(use.getDept());
//					newA.setSurplus(num-qinjianum);
//					newA.setStatus("换休");
//					totalDao.save(newA);
//				}
//				//----------
//			}
//		}
		
	}
	
	public Object[] listhuanxiu(AnnualLeave al,Integer cpage, Integer PageSize) {
		if (al == null) {
			al = new AnnualLeave();
		}
		String hql = totalDao.criteriaQueries(al, null) +" and status='换休' order by id desc";
		List<AnnualLeave> list = totalDao.findAllByPage(hql, cpage, PageSize);
		//更新换休信息
		String queryOverTime = "from Overtime where overtimeCode = ? and huanxiu='是' and status='同意'";
		CodeTranslation codeTranslation = (CodeTranslation) totalDao
				.getObjectByCondition("from CodeTranslation where type='sys' and valueName='换休有效期'");
		
		for (AnnualLeave annualLeave : list) {
			// 换休有效期→查询系统设置
			List<Overtime> listOvertime =null;
			if (null == codeTranslation) {
//				listOvertime = totalDao.query(queryOverTime, annualLeave.getJobNum());
			} else {
				if (Integer.parseInt(codeTranslation.getValueCode()) > 0) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.MONTH, -Integer.parseInt(codeTranslation.getValueCode()));
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String passDate = format.format(calendar.getTime());
					listOvertime = totalDao.query(queryOverTime+" and startDate>?", annualLeave.getJobNum(), passDate);
				}
				float overTimeLong = 0.0f;
				for (Overtime overtime : listOvertime) {
					if(overtime.getUsablehxTime()==null) {
						overtime.setUsablehxTime(0f);
					}
					if(null!=overtime.getOverTimeLong()){
						overTimeLong += overtime.getUsablehxTime();
					}
				}
				Users users = (Users) totalDao.getObjectByCondition("from Users where code='"+annualLeave.getJobNum()+"'");
				if(users!=null){
					Float gzTime = 8f; //每天工作时长
					if(null!=users.getBanci_id()){
						BanCi banCi = (BanCi) totalDao.getObjectById(BanCi.class, users.getBanci_id());
						if(null !=banCi && null!=banCi.getGzTime()){
							gzTime = banCi.getGzTime()/60f;
						}
						if (gzTime == null || gzTime == 0) {
							gzTime = 8f;
						}
					}
					Float surplus =overTimeLong/gzTime;
					annualLeave.setSurplus(surplus );
				}
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}
	/*
	 * 根据工号查询年休
	 */
	public AnnualLeave BynameNianxiu(String personcode) {
		String hql1 = " from  AnnualLeave  where status ='年休' and jobNum=?";
		AnnualLeave annualLeave = (AnnualLeave) totalDao.getObjectByCondition(hql1, personcode);
		return annualLeave;
	}
	//查询年休请假记录下的个人年休明细
	public List ByAskForLeaveCarId(String carId) {
		String hql = "from AskForLeave where approvalStatus='同意' and leaveTypeOf='年休' and leavePersonCode=? order by leaveId desc";
		List list = totalDao.query(hql, carId);
		return list;
		
	}
	
	public Object[] listAnnualLeave(AnnualLeave al,Integer cpage, Integer PageSize,String pagestatus) {
		if (al == null) {
			al = new AnnualLeave();
		}
		if("hx".equals(pagestatus)){
			al.setStatus("换休");
		}else if("nx".equals(pagestatus)){
			al.setStatus("年休");
		}
		String hql = totalDao.criteriaQueries(al, null);
		
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}
	
	public Object[] ByCode(String code,Integer cpage, Integer PageSize) {
		String hql1 = " from  Users  where code=?";
		Users useers = (Users) totalDao.getObjectByCondition(
				hql1, code);
		//String hql = "  from AskForLeave where approvalStatus='同意' and  leaveUserCardId=?";
		//List list = totalDao.query(hql,useers.getCardId());
		//return list;   and  leaveUserCardId='"+useers.getCardId()+"'
		
		
		String hql = " from AskForLeave where approvalStatus='同意'  and leaveTypeOf='年休' and leaveUserCardId=? order by leaveId desc";
		List list = totalDao.findAllByPage(hql, cpage, PageSize,useers.getCardId());
		int count = totalDao.getCount(hql,useers.getCardId());
		Object[] o = { list, count };
		return o;
	}
	
	public Object[] ByCodehuanxiu(String code,Integer cpage, Integer PageSize) {
		String hql1 = " from  Users  where code=?";
		Users useers = (Users) totalDao.getObjectByCondition(
				hql1, code);
		//String hql = "  from AskForLeave where approvalStatus='同意' and  leaveUserCardId=?";
		//List list = totalDao.query(hql,useers.getCardId());
		//return list;   and  leaveUserCardId='"+useers.getCardId()+"'
		if(null==useers || null==useers.getCardId()){
			return null;
		}else{
			String hql = " from AskForLeave where  leaveStartDate>'2014-10-01'  and leaveTypeOf='换休'  and  leaveUserCardId=? order by leaveId desc";
			List list = totalDao.findAllByPage(hql, cpage, PageSize,useers.getCardId());
			int count = totalDao.getCount(hql,useers.getCardId());
			Object[] o = { list, count };
			return o;
		}
	}
	public Object[] mingxijiaban(String jobNum, Overtime askForLeave, Integer parseInt,
			Integer pageSize){
		
			if (askForLeave== null){
				askForLeave = new Overtime();
			}
			//String hql = totalDao.criteriaQueries(AskForLeave, null, null);
			//String hql2 = "  from Overtime  where  huanxiustatus is null " +
			//"and status in  ('同意','加班前未刷卡','加班后未刷卡','未刷卡') and    startDate >'2014-10-01'  and   overtimeCode=?";
			String hql = "  from Overtime  where    status in  ('同意','加班前未刷卡','加班后未刷卡','未刷卡') and   startDate >'2014-10-01' and  overtimeCode=? order by id desc";
			List list = totalDao.findAllByPage(hql, parseInt, pageSize,jobNum);
			int count = totalDao.getCount(hql,jobNum);
			Object[] o = { list, count };
			return o;
	}
	
	@Override
	public void add(AnnualLeave annual) {
		annual.setStatus("年休");
		totalDao.save(annual);
	}

	@Override
	public void delete(AnnualLeave annual) {
		// TODO Auto-generated method stub
		totalDao.delete(annual);
	}
	
	@Override
	public void update(AnnualLeave annual) {
		// TODO Auto-generated method stub
		totalDao.update(annual);
	}
	
	@Override
	public AnnualLeave getAnnualLeaveById(Integer id) {
		// TODO Auto-generated method stub
		return (AnnualLeave) totalDao.getObjectById(AnnualLeave.class, id);
	}
	@Override


	public Object[] findAnnualLeaveCondition1(Map map, Integer pageNo,
			Integer pageSize) {
		// TODO Auto-generated method stub
		String hql = "from AnnualLeave a where status ='换休'";
		if (map != null) {
			if (map.get("nameStr") != null) {
				hql += " and a.name like '%" + map.get("nameStr") + "%'";
			}
			if (map.get("deptIDStr") != null) {
				hql += " and a.dept = '" + map.get("deptIDStr") + "'";
			}
			if (map.get("jobNumStr") != null) {
				hql += " and a.jobNum = '" + map.get("jobNumStr") + "'";
			}
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[]{list,count};
	}
	
	
	@Override
	public Object[] findAnnualLeaveCondition(Map map, Integer pageNo,
			Integer pageSize) {
		// TODO Auto-generated method stub
		String hql = "from AnnualLeave a where status ='年休'";
		if (map != null) {
			if (map.get("nameStr") != null) {
				hql += " and a.name like '%" + map.get("nameStr") + "%'";
			}
			if (map.get("deptIDStr") != null) {
				hql += " and a.dept = '" + map.get("deptIDStr") + "'";
			}
			if (map.get("jobNumStr") != null) {
				hql += " and a.jobNum = '" + map.get("jobNumStr") + "'";
			}
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[]{list,count};
	}
	public void batchAddAnnualLeave() {
		// TODO Auto-generated method stub
		/*Session session = totalDao.createSession();
		Connection con = null;
		try {
			Transaction tx = session.beginTransaction();
			 con = session.connection();
			PreparedStatement stmt = con.prepareStatement("update tableName set f_lengthOfService =f_lengthOfService*工龄 ");
			stmt.executeUpdate();
			tx.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if(con != null){
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(session != null){
				session.close();
			}
			e.printStackTrace();
		}*/
		
	/*	Session session = null;
		Transaction tx = null;
		try{
			session = totalDao.createSession();
			tx = session.beginTransaction();
			List<AnnualLeave> list = session.createQuery("from AnnualLeave").list();
			for(int i = 0;i < list.size();i++){
				AnnualLeave al = list.get(i);
				if(al.getLengthOfService() == 0){
					al.setSurplus(al.getStandardAnnualLeave()+al.getSurplus());
				}
				if(i%30==0){
					session.flush();
					session.clear();
				}
				session.update(al);
			}
			tx.commit();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(session != null){
				session.close();
			}
		}*/
		
		/**
		 * 首先判断入职时间是否大于一年,满一年才去算工龄
		 * 如果大于一年,则查询出该人的工龄,再判断该享受的标准
		 * 最后累加年休
		 */
		Session session = totalDao.createSession();
		Transaction tx = null;
		int count = 0;
		String hql = "from AnnualLeave where  status='年休' and    exists (from Users u where getDate()>dateadd(year,1,u.joined))";
		try {
			tx = session.beginTransaction();
			Iterator<AnnualLeave> it = session.createQuery(hql).iterate();
			while (it.hasNext()) {
				AnnualLeave al = it.next();
				al.setLengthOfService(al.getLengthOfService() + 1);
				if (al.getLengthOfService() >= 1
						&& al.getLengthOfService() <= 10) {
					al.setStandardAnnualLeave(FIVE);
				} else if (al.getLengthOfService() > 10
						&& al.getLengthOfService() <= 20) {
					al.setStandardAnnualLeave(TEN);
				} else {
					al.setStandardAnnualLeave(FIFTEEN);
				}
				al.setSurplus(al.getStandardAnnualLeave() + al.getSurplus());
				if ( ++count % 20 == 0 ) {
			        //flush a batch of updates and release memory:
			        session.flush();
			        session.clear();
			    }
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public TotalDao getTotalDao() {
		return totalDao;
	}
	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public void daochuAll(String tag) {
		// TODO Auto-generated method stub
		List list = null;
		if(tag==null){
			list = totalDao.query("from AnnualLeave where status = '换休' and jobNum in (select code from Users where dept in ('物流','采购','市场','加工','总成班','品质','工艺','总经办','人力资源','信息','财务') and onWork not in ('离职','内退')) order by dept desc");
			for (Object object : list) {
				AnnualLeave ann = (AnnualLeave) object;
				AnnualLeave ann1 = (AnnualLeave) totalDao.getObjectByCondition("from AnnualLeave where jobNum = ? and status = '年休'", ann.getJobNum());
				if(ann1!=null){
					ann.setLinshi(ann1.getSurplus());
				}else {
					ann.setLinshi(0f);
				}
			}
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(ServletActionContext.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ new String(("年休换休汇总").getBytes("GB2312"), "8859_1") + ".xls");
				response.setContentType("application/msexcel");
				WritableWorkbook wwb = Workbook.createWorkbook(os);
				WritableSheet ws = wwb.createSheet("年休换休汇总", 0);
				ws.setColumnView(1, 15);
				ws.setColumnView(2, 10);
				ws.setColumnView(4, 18);
				ws.setColumnView(5, 18);
				WritableFont wf = new WritableFont(WritableFont.ARIAL, 14,
						WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
						Colour.BLACK);
				WritableCellFormat wcf = new WritableCellFormat(wf);
				wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
				wcf.setAlignment(Alignment.CENTRE);

				jxl.write.Label label0 = new Label(0, 0, "年休换休汇总数据", wcf);
				ws.addCell(label0);
				ws.mergeCells(0, 0, 6, 0);

				wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD,
						false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
				WritableCellFormat wc = new WritableCellFormat(wf);
				wc.setAlignment(Alignment.CENTRE);
				ws.addCell(new jxl.write.Label(0, 1, "序号", wc));
				ws.addCell(new jxl.write.Label(1, 1, "姓名", wc));
				ws.addCell(new jxl.write.Label(2, 1, "工号", wc));
				ws.addCell(new jxl.write.Label(3, 1, "部门", wc));
				ws.addCell(new jxl.write.Label(4, 1, "可用换休/天", wc));
				ws.addCell(new jxl.write.Label(5, 1, "可用年休/天", wc));
				ws.addCell(new jxl.write.Label(6, 1, "合计/天", wc));

				for (int i = 0; i < list.size(); i++) {
					AnnualLeave go = (AnnualLeave) list.get(i);
					ws.addCell(new jxl.write.Number(0, i + 2, i + 1, wc));
					ws.addCell(new Label(1, i + 2, go.getName(), wc));
					ws.addCell(new Label(2, i + 2, go.getJobNum(), wc));
					ws.addCell(new Label(3, i + 2, go.getDept(), wc));
					ws.addCell(new jxl.write.Number(4, i + 2, go.getSurplus(), wc));
					ws.addCell(new jxl.write.Number(5, i + 2, go.getLinshi(), wc));
					ws.addCell(new jxl.write.Number(6, i + 2, go.getSurplus()+go.getLinshi(), wc));
				}
				wwb.write();
				wwb.close();
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			list = totalDao.query("from AnnualLeave where status = ? and jobNum in (select code from Users where dept in ('物流','采购','市场','加工','总成班','品质','工艺','总经办','人力资源') and onWork not in ('离职','内退')) order by dept desc", tag);
			try {
				HttpServletResponse response = (HttpServletResponse) ActionContext
				.getContext().get(ServletActionContext.HTTP_RESPONSE);
				OutputStream os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ new String((tag+"汇总").getBytes("GB2312"), "8859_1") + ".xls");
				response.setContentType("application/msexcel");
				WritableWorkbook wwb = Workbook.createWorkbook(os);
				WritableSheet ws = wwb.createSheet(tag+"汇总", 0);
				ws.setColumnView(1, 15);
				ws.setColumnView(2, 10);
				ws.setColumnView(4, 18);
				WritableFont wf = new WritableFont(WritableFont.ARIAL, 14,
						WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
						Colour.BLACK);
				WritableCellFormat wcf = new WritableCellFormat(wf);
				wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
				wcf.setAlignment(Alignment.CENTRE);
				
				jxl.write.Label label0 = new Label(0, 0, tag+"汇总数据", wcf);
				ws.addCell(label0);
				ws.mergeCells(0, 0, 4, 0);
				
				wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD,
						false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
				WritableCellFormat wc = new WritableCellFormat(wf);
				wc.setAlignment(Alignment.CENTRE);
				ws.addCell(new jxl.write.Label(0, 1, "序号", wc));
				ws.addCell(new jxl.write.Label(1, 1, "姓名", wc));
				ws.addCell(new jxl.write.Label(2, 1, "工号", wc));
				ws.addCell(new jxl.write.Label(3, 1, "部门", wc));
				ws.addCell(new jxl.write.Label(4, 1, "累计可用"+tag+"/天", wc));
				
				for (int i = 0; i < list.size(); i++) {
					AnnualLeave go = (AnnualLeave) list.get(i);
					ws.addCell(new jxl.write.Number(0, i + 2, i + 1, wc));
					ws.addCell(new Label(1, i + 2, go.getName(), wc));
					ws.addCell(new Label(2, i + 2, go.getJobNum(), wc));
					ws.addCell(new Label(3, i + 2, go.getDept(), wc));
					ws.addCell(new jxl.write.Number(4, i + 2, go.getSurplus(), wc));
				}
				wwb.write();
				wwb.close();
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public List overTimeList(String code) {
		// TODO Auto-generated method stub
		return totalDao.query("from Overtime where overtimeCode = ? and huanxiustatus = '已换休' order by id desc", code);
	}

}