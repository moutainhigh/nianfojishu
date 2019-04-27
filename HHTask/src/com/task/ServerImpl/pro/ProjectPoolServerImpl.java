package com.task.ServerImpl.pro;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.springframework.util.FileCopyUtils;

import com.task.Dao.TotalDao;
import com.task.Server.pro.ProjectPoolServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.entity.DeptNumber;
import com.task.entity.Users;
import com.task.entity.project.ProjectManageYfpd;
import com.task.entity.project.ProjectManageyf;
import com.task.entity.project.ProjectManageyfAgree;
import com.task.entity.project.ProjectManageyfEr;
import com.task.entity.project.ProjectManageyfUser;
import com.task.entity.project.ProjectPool;
import com.task.entity.project.YfUser;
import com.task.util.Util;

public class ProjectPoolServerImpl implements ProjectPoolServer {
	private TotalDao totalDao;
	private List<Integer> deptIds;
	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	//添加项目池
	@Override
	public String addPool(ProjectPool projectPool) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if(user==null){
			return "请先登录!";
		}
		String time = Util.getDateTime("yyyy-MM-dd");
		projectPool.setAddUserId(user.getId());
		projectPool.setAddUserCode(user.getCode());
		projectPool.setAddUserName(user.getName());
		projectPool.setAddTime(time);
		//项目池编号
		String before = "propool-" + Util.getDateTime("yyyyMM-");
		String maxnumber = (String) totalDao
				.getObjectByCondition("select max(poolNumber) from ProjectPool where poolNumber like '"
						+ before + "%'");
		DecimalFormat df = new DecimalFormat("0000");
		if (maxnumber != null && !"".equals(maxnumber)) {
			// String number1 = paymentVoucher2.getNumber();
			String now_number = maxnumber.split(before)[1];
			Integer number2 = Integer.parseInt(now_number) + 1;
			String number3 = df.format(number2);
			projectPool.setPoolNumber(before + number3);
		} else {
			projectPool.setPoolNumber(before + "0001");
		}
		
		List<ProjectManageyf> pmyfList = projectPool.getPmyfList();
		totalDao.save(projectPool);
		if(pmyfList!=null&&pmyfList.size()>0){
			Integer maxInt=0;
			//项目编号
			String before2 = "RDPRO-" + Util.getDateTime("yyyyMMdd")+"-";
			String maxnumber2 = (String) totalDao
					.getObjectByCondition("select max(proNum) from ProjectManageyf where proNum like '"
							+ before2 + "%'");
			int i=0;
			if (maxnumber2 != null && !"".equals(maxnumber2)) {
				// String number1 = paymentVoucher2.getNumber();
				String now_number = maxnumber2.split(before2)[1];
				try {
					maxInt= Integer.parseInt(now_number);
				} catch (NumberFormatException e) {
					i=Integer.parseInt(now_number.split("-")[1]);
				}
			} else {
				//projectPool.setPoolNumber(before + "0001");
				maxInt = 1;
			}
			
			for(ProjectManageyf pmyf :pmyfList){
				if(pmyf!=null&&pmyf.getProName()!=null&&pmyf.getProName().length()>0){
					i++;
					String projectNum = null;
					int thisNum = maxInt+i;
					projectNum = before2 + df.format(thisNum);;
					pmyf.setProNum(projectNum);
					pmyf.setPoolId(projectPool.getId());
					pmyf.setAddUserId(user.getId());
					pmyf.setAddUserCode(user.getCode());
					pmyf.setAddUserName(user.getName());
					pmyf.setStartTime(time);
					pmyf.setAddTime(time);
					pmyf.setBelongLayer(1);
					pmyf.setStatus("待指派");
					totalDao.save(pmyf);
					pmyf.setRootId(pmyf.getId());
					totalDao.update(pmyf);
				}
				
			}
		}
		
		return "true";
	}
	//删除项目池
	@SuppressWarnings("unchecked")
	@Override
	public String delProjectPool(Integer id) {
		ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, id);
		int count = totalDao.getCount("from ProjectManageyf where poolId=? and rootId<>id", id);
		if(count>0){
			return "主项目下还有子项目不能删除";
		}
		
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where poolId=? and rootId=id", id);
		for (ProjectManageyf projectManageyf : list) {
			Integer proId = projectManageyf.getId();
			List<ProjectManageyfUser> yfUserList = totalDao.query("from ProjectManageyfUser where rootId=? ",projectManageyf.getRootId());
			for (ProjectManageyfUser projectManageyfUser : yfUserList) {
				totalDao.delete(projectManageyfUser);
			}
			List<YfUser> yfUsers = totalDao.query("from YfUser where projectManagerYfId=?", proId);
			for (YfUser yfUser : yfUsers) {
				totalDao.delete(yfUser);
			}
			List<ProjectManageYfpd> pdList = totalDao.query("from ProjectManageYfpd where proId = ?", proId);
			for (ProjectManageYfpd projectManageYfpd : pdList) {
				totalDao.delete(projectManageYfpd);
			}
			List<ProjectManageyfEr> erList = totalDao.query("from ProjectManageyfEr where projectId=?", proId);
			for (ProjectManageyfEr projectManageyfEr : erList) {
				totalDao.delete(projectManageyfEr);
			}
			totalDao.delete(projectManageyf);
		}
		totalDao.delete(pool);
		return "删除成功";
	}

	//查询项目池
	@SuppressWarnings("unchecked")
	@Override
	public Object[] findPoolByCondition(ProjectPool projectPool, int pageNo,
			int pageSize, String pageStatus) {
		if (projectPool == null) {
			projectPool = new ProjectPool();
		}
		String hql = totalDao.criteriaQueries(projectPool, null);
		List<ProjectPool> list = totalDao.findAllByPage(hql+ " order by id desc", pageNo, pageSize);
		//String principal = "";
		String principal = "";//负责人
		if(list!=null&&list.size()>0){
			for(ProjectPool pool:list){
				List<ProjectManageyf> yfList = totalDao.query("from ProjectManageyf where poolId=? and id=rootId and belongLayer=1", pool.getId());
				for (ProjectManageyf projectManageyf : yfList) {
					principal = findPrincipalPmyfId(projectManageyf.getId());
					projectManageyf.setPrincipals(principal);
				}
				pool.setPmyfList(yfList);
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}
	
	//获得项目池
	@Override
	public ProjectPool getProjectPoolById(Integer poolId) {
		
		ProjectPool projectPool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, poolId);
		if(null!=projectPool){
			List<ProjectManageyf> yfList = totalDao.query("from ProjectManageyf where poolId=? and id=rootId and belongLayer=1", projectPool.getId());
//			String principal = "";//负责人
//			for (ProjectManageyf projectManageyf : yfList) {
//				principal = findPrincipalPmyfId(projectManageyf.getId());
//				projectManageyf.setPrincipals(principal);
//			}
			projectPool.setPmyfList(yfList);
			return projectPool;
		}
		return null;
	}
	
	//编辑项目池
	@Override
	public String editPool(ProjectPool projectPool) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectPool.getId());
		if(null==pool){
			return "没有找到研发项目池信息";
		}
		
		pool.setPoolName(projectPool.getPoolName());
		pool.setReEndTime(projectPool.getReEndTime());
		double newMoney = projectPool.getTotalMoney()==null?0.0:projectPool.getTotalMoney();//新项目金额
		double oldMoney = pool.getTotalMoney()==null?0.0:pool.getTotalMoney();		//旧项目金额
		pool.setTotalMoney(projectPool.getTotalMoney());
		pool.setStartTime(projectPool.getStartTime());
		boolean result = totalDao.update(pool);
		if(result){
			
			List<ProjectManageyf> pmyfList = projectPool.getPmyfList();
			ProjectManageyf manageyf = null;
			DecimalFormat df = new DecimalFormat("0000");
			for (ProjectManageyf projectManageyf : pmyfList) {
				if(null!=projectManageyf){
					if(null!= projectManageyf.getId()){
						//修改
						manageyf =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
						manageyf.setProName(projectManageyf.getProName());
						manageyf.setRemark(projectManageyf.getRemark());
						manageyf.setReTime(projectManageyf.getReTime());
						//manageyf.setStartTime(projectManageyf.getStartTime());
						totalDao.update(manageyf);
					}else{
						manageyf = new ProjectManageyf();
						//增加
						String time = Util.getDateTime();
						//项目编号
						String before2 = "RDPRO-" + Util.getDateTime("yyyyMMdd")+"-";
						String maxnumber2 = (String) totalDao
								.getObjectByCondition("select max(proNum) from ProjectManageyf where proNum like '"
										+ before2 + "%'");
						if (maxnumber2 != null && !"".equals(maxnumber2)) {
							// String number1 = paymentVoucher2.getNumber();
							String now_number = maxnumber2.split(before2)[1];
							
							
							int maxInt=0;
							try {
								maxInt= Integer.parseInt(now_number);
							} catch (NumberFormatException e) {
								maxInt=Integer.parseInt(now_number.split("-")[1]);
							}
						
							
							
							Integer number2 = maxInt + 1;
							String number3 = df.format(number2);
							//projectPool.setPoolNumber(before + number3);
							manageyf.setProNum(before2 + number3);
						}else{
							manageyf.setProNum(before2+"0001");
						}
						manageyf.setProName(projectManageyf.getProName());
						manageyf.setRemark(projectManageyf.getRemark());
						manageyf.setReTime(projectManageyf.getReTime());
						manageyf.setAddUserId(loginUser.getId());
						manageyf.setAddUserCode(loginUser.getCode());
						manageyf.setAddUserName(loginUser.getName());
						manageyf.setStartTime(Util.getDateTime("yyyy-MM-dd"));
						manageyf.setAddTime(time);
						manageyf.setBelongLayer(1);
						manageyf.setStatus("待指派");
						manageyf.setPoolId(pool.getId());
						totalDao.save(manageyf);
						manageyf.setRootId(manageyf.getId());
						totalDao.update(manageyf);
						
					}
					
					//修改项目池金额，所有的项目人员进行更改
					if(newMoney!=oldMoney){
						updateyfProPd(manageyf.getId());
					}
				}
				
			}
			return "success";
		}
		return "修改项目池失败";
	}
	
	//根据项目id查找负责人
	public String findPrincipalPmyfId(Integer projectId){
		StringBuffer principal = new StringBuffer();//负责人
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectId);
		if(null==projectManageyf ){
			return "";
		}else{
			//判断是子项目还是父项目
			if(projectManageyf.getId()==projectManageyf.getRootId() || projectManageyf.getBelongLayer()==1){
				//父项目
				List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where usertype='项目主管'" +
						" and id in(select yfUserId from YfUser where approve = true and projectManagerYfId = ?) ",projectId);
				if(null==list || list.size()==0){
					return "";
				}else{
					for (ProjectManageyfUser projectYfUser : list) {
						principal.append(projectYfUser.getUserName()+",");
					}
					System.out.println(principal.substring(0, principal.length()-1));
					return principal.substring(0, principal.length()-1);
				}
			}else{
				//子项目   负责人为项目第一个审批同意人
				List<ProjectManageyfUser> list =totalDao.query("from ProjectManageyfUser where id in " +
						"(select yfUserId from YfUser where approve = true and projectManagerYfId =?) ", projectId);
				if(null==list || list.size()==0){
					return "";
				}else{
					return list.get(0).getUserName();
				}
			}
		}
	}

	//获取个人研发项目
	@SuppressWarnings("unchecked")
	@Override
	public Object[] findselfProjectmanageYf(ProjectManageyf projectManageyf,
			int pageNo, int pageSize, String pageStatus) {
		Users user =Util.getLoginUser();
		if(user==null){
			return null;
		}
		if (projectManageyf == null) {
			projectManageyf = new ProjectManageyf();
		}
		String hql = totalDao.criteriaQueries(projectManageyf, null);
		//查找项目参与人
		String applychoose = "";
		if("applychoose".equals(pageStatus)){
			applychoose = " and usertype='参与人'";
		}else{
			applychoose = " and usertype='项目主管'";
		}
		//hql += " and id in (select rootId from ProjectManageyfUser where userId = "+user.getId()+")";
		
		hql+=" and  id in (select projectManagerYfId from YfUser where approve=true and yfUserId in " +
				" (select id from ProjectManageyfUser where userId = "+user.getId()+applychoose+" ))";
		List<ProjectManageyf> list = totalDao.findAllByPage(hql+ " order by id desc", pageNo, pageSize);
		String principal = "";//负责人
		for (ProjectManageyf projectyf : list) {
			principal = findPrincipalPmyfId(projectyf.getId());
			projectyf.setPrincipals(principal);
			if("applychoose".equals(pageStatus)){
				List<ProjectManageyfEr> erList = totalDao.query("from ProjectManageyfEr where projectId = ? and addUserId =?",
						projectyf.getId(),user.getId());
				//chooseStatus
				if(null!=erList && erList.size()>0){
					projectyf.setChooseStatus(erList.get(0).getStatus());
				}
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	//进入项目指派页面
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfUser> getAssignUserList(Integer id) {//项目id
		String hql = "from ProjectManageyfUser where usertype='项目主管' and id in (select yfUserId from YfUser where projectManagerYfId = ?)";
		return totalDao.query(hql, id);
	}

	//项目指派
	@SuppressWarnings("unchecked")
	@Override
	public Object[] projectManageryfAssign(ProjectManageyfUser pmyfUser) {
		Users loginUser = Util.getLoginUser();
		if(loginUser==null){
			return new Object[]{"请先登录",0};
		}
		Users user = (Users) totalDao.getObjectById(Users.class, pmyfUser.getUserId());
		if(user==null){
			return new Object[]{"没有找到目标人员",0};
		}
		ProjectManageyf rootProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, pmyfUser.getRootId());
		if(null==rootProject){
			return new Object[]{"没有找到对应的研发项目记录",0};
		}
		Integer count = totalDao.getCount("select count(*) from ProjectManageyfUser where rootId = ? and userId =?", pmyfUser.getRootId(),user.getId());
		if(count !=null&&count>0){
			return new Object[]{"此人已被指派,请勿重复指派",0};
		}
//		pmyfUser.setZpTime(Util.getDateTime("yyyy-MM-dd"));
		pmyfUser.setUserName(user.getName()); //
		pmyfUser.setUserCode(user.getCode()); //
		if(user.getDept()!=null){
			pmyfUser.setUserDept(user.getDept());
		}
		if(null==pmyfUser.getYfDate() || "".equals(pmyfUser.getYfDate())){ //设置预完成时间
			pmyfUser.setYfDate(rootProject.getReTime());
		}
		pmyfUser.setPoolId(rootProject.getPoolId()); //项目池id
		boolean flag = totalDao.save(pmyfUser);
		if(flag){
			YfUser yfUser = new YfUser();
			yfUser.setProjectManagerYfId(rootProject.getId());
			yfUser.setYfUserId(pmyfUser.getId());
			yfUser.setApprove(true);//设置项目允许成员
			totalDao.save(yfUser);
			ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, pmyfUser.getRootId());
			projectManageyf.setZpTime(Util.getDateTime("yyyy-MM-dd"));
			if("待指派".equals(projectManageyf.getStatus())){
				projectManageyf.setStatus("待完善");
			}
			totalDao.update(projectManageyf);  //更新指派时间
			String[] principals = findPrincipalPmyfId(projectManageyf.getId()).split(",");
			//添加人员明细
//			ProjectManageYfpd pd = new ProjectManageYfpd();
//			pd.setProId(projectManageyf.getId());
//			pd.setUserId(pmyfUser.getUserId());
//			pd.setUserName(pmyfUser.getUserName());
//			pd.setStartTime(Util.getDateTime("yyyy-MM-dd"));
//				//pd.setyProportion(getProPercentnm(yfUser.getProjectManagerYfId())); //原占比
//			//pd.setMoney(getProMoneyByProId(projectManageyf)); //原金额
//			totalDao.save(pd);
			AlertMessagesServerImpl.addAlertMessages("项目管理指派负责人：", loginUser.getName()+"，指派："+user.getName()
					+"，负责项目:"+rootProject.getProName()+"!", new Integer[]{user.getId()},
					"projectPoolAction_selfProjectManageyfList.action", true);
			//设置这个项目池下所有主项目人员的管理，发送分数
			if(projectManageyf.getGradeStore()!=null && projectManageyf.getGradeStore()>0){
				List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where poolId ="+projectManageyf.getPoolId()+" and rootId <>"+projectManageyf.getId());
				if(null!=list && list.size()>0){
					for (ProjectManageyf yfProject : list) {
						List<ProjectManageyfUser> yfUserList = totalDao.query("from ProjectManageyfUser where usertype='项目主管' and id in(select yfUserId "
								+ "from YfUser where approve=true and projectManagerYfId= "+yfProject.getId()+")");
						if(null!=yfUserList && yfUserList.size()>0){
							ProjectManageyfUser projectManageyfUser = yfUserList.get(0);
							if(null!=projectManageyf && null!=projectManageyfUser.getUserId()){
								boolean exists = true;
								for (String principal : principals) {
									if(principal.equals(projectManageyfUser.getUserName())) {
										exists = false;
									}
								}
								if(exists) {
									AlertMessagesServerImpl.addAlertMessages("确认项目分数", 
											projectManageyfUser.getUserName()+"，已确认项目分数，请您审批。：" , new Integer[]{projectManageyfUser.getUserId()},
											"projectPoolAction_toProjectGrade.action?tag=affirmGrade&id="+yfProject.getId(), true);
									if("同意".equals(yfProject.getGradeStatus()) && projectManageyfUser.getId() !=yfUser.getYfUserId()){
										yfProject.setGradeStatus("审批中");
										totalDao.update(yfProject);
									}
								}
								
							}							
						}
					}
					// TODO: 
				}
			}
			
			//updateyfProPd(yfUser.getProjectManagerYfId());
			return new Object[]{"成功",pmyfUser.getId()};
		}else{
			return new Object[]{"指派失败，请重试",0};
		}
		
	}

	//取消指派
	@Override
	public String cancelAssion(Integer pmyfId) {
		if(null!=pmyfId && !"".equals(pmyfId)){
			ProjectManageyfUser pmyfUser =(ProjectManageyfUser) totalDao.getObjectById(ProjectManageyfUser.class, pmyfId);
			YfUser yfUser = (YfUser) totalDao.getObjectByCondition("from YfUser where yfUserId =? and projectManagerYfId=?", pmyfId,pmyfUser.getRootId());
			if(totalDao.delete(pmyfUser) && totalDao.delete(yfUser)){
				ProjectManageYfpd pd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where proId="+yfUser.getProjectManagerYfId()+" and userId="+pmyfUser.getUserId());
				if(pd!=null){
					totalDao.delete(pd);
				}
				//updateyfProPd(yfUser.getProjectManagerYfId());
				return "成功";
			}
			return "删除失败";
		}else{
			return "没有找到目标";
		}
	}

	//根据id查找项目
	@Override
	public ProjectManageyf getProjectManageyfById(Integer id) {
		if(id!=null && id!=0){
			ProjectManageyf projectManageyf=(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, id);
			//项目负责人
			projectManageyf.setPrincipals(findPrincipalPmyfId(id));
			if(projectManageyf.getGradeStore()!=null) {
				BigDecimal bg = new BigDecimal(getProportion(projectManageyf)*100);
				double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setProportion(doubleValue+"%");
			}
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(pool);
			return projectManageyf;
		}
		return null;
	}

	//获取项目池下的其他项目
	@Override
	public List<ProjectManageyf> getOtherPMyf(Integer id) {
		if(null!=id){
			ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, id);
			List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId= id and poolId=? and gradeStore is not null and id<>?", projectManageyf.getPoolId(),id);
			for (ProjectManageyf projectManageyf2 : list) {
				projectManageyf2.setPrincipals(findPrincipalPmyfId(projectManageyf2.getId()));
			}
			return list;
		}
		return null;
	}

	//保存分数
	@SuppressWarnings("unchecked")
	@Override
	public String saveProjectStore(ProjectManageyf projectManageyf) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		if(null!=projectManageyf && null!=projectManageyf.getId()){
			ProjectManageyf manageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
			manageyf.setGradeStore(projectManageyf.getGradeStore()); //分数
			manageyf.setGradeRemark(projectManageyf.getGradeRemark()); //备注
			boolean update = totalDao.update(manageyf);
			if(update){
				List<ProjectManageyf> list= totalDao.query("from ProjectManageyf where poolId =? and id<>? and rootId=id",
						manageyf.getPoolId(),manageyf.getId());
				if(null!=list && list.size()>0){
					manageyf.setGradeStatus("预申请");
					StringBuffer buffer = new StringBuffer("(");
					for (int i = 0; i < list.size(); i++) {
						buffer.append(list.get(i).getId()+",");
					}
					String substring = buffer.substring(0, buffer.length()-1);
					substring+=")";
//						agree = new ProjectManageyfAgree(); //审批记录表
//						agree.setProjectManagerYfId(manageyf.getId());//设置项目id
//						totalDao.save(agree);// 保存记录表
					
					String hql = "select userId from ProjectManageyfUser where usertype='项目主管' and id in (select yfUserId from YfUser" +
							" where projectManagerYfId in "+substring+")";
					List<Integer> userList = totalDao.query(hql);
					int sumCount = 0;
					Set<Integer> set = new HashSet<Integer>();
					for (int i = 0; i < userList.size(); i++) {
						set.add(userList.get(i));
						
					}
					for (Integer integer : set) {
						if(integer.intValue()!=loginUser.getId().intValue()){
							AlertMessagesServerImpl.addAlertMessages("确认研发项目分数", 
									loginUser.getName()+"，已确认研发项目分数，请您审批。：" , new Integer[]{integer},
									"projectPoolAction_toProjectGrade.action?tag=affirmGrade&id="+manageyf.getId(), true);
						}else{
							sumCount++;
						}
					}
//					if(sumCount==0 && userList.size()>0){
//						
//					}else 
					if(sumCount== userList.size()){
						manageyf.setGradeStatus("同意");
					}else if (userList.size()>0 && userList.size()>sumCount){
						manageyf.setGradeStatus("审批中");
					}
				}else{
					manageyf.setStatus("待完善");
					manageyf.setGradeStatus("同意");
				}
				totalDao.update(manageyf);
				return "success";
					
			}
		}

		return "评分失败，请刷新后重试";
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> findProjectManageyfByRootId(Integer rootId) {
		if(null!=rootId){
			List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = ? order by belongLayer,fatherId desc",rootId);
			//List<ProjectManageyf> newList = new ArrayList<ProjectManageyf>();
			ProjectManageyf yf = null;
			for (ProjectManageyf projectManageyf : list) {
				projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId()));
				if((int)projectManageyf.getId()== (int)projectManageyf.getRootId()){
					yf = projectManageyf;
				}
			}
			
			List<ProjectManageyf> treeNode = getTreeNode(list,yf.getId());
			return  treeNode; 
		}
		return null;
	}
	
	private List<ProjectManageyf> getTreeNode(List<ProjectManageyf> list,Integer proId){
		List<ProjectManageyf> rootProjectManageyf = new ArrayList<ProjectManageyf>();
		
		for (ProjectManageyf projectManageyf : list) {
			//判断第一个元素
			if((int)projectManageyf.getId()==proId){
				rootProjectManageyf.add(projectManageyf);
			}
			findResources(list, projectManageyf.getId(), rootProjectManageyf);
//			for (ProjectManageyf project : list) {
//				if(project.getFatherId()!=null){
//					if(Integer.parseInt(project.getFatherId()) == projectManageyf.getId()){
//						if(!rootProjectManageyf.contains(project)){
//							rootProjectManageyf.add(project);
//						}
//						for (ProjectManageyf project1 : list) {
//							if(project1.getFatherId()!=null){
//								if(Integer.parseInt(project1.getFatherId()) == project.getId()){
//									if(!rootProjectManageyf.contains(project1)){
//										rootProjectManageyf.add(project1);
//									}
//									for (ProjectManageyf project2 : list) {
//										if(project2.getFatherId()!=null){
//											if(Integer.parseInt(project2.getFatherId()) == project1.getId()){
//												if(!rootProjectManageyf.contains(project2)){
//													rootProjectManageyf.add(project2);
//												}
//											}
//										}	
//									}
//								}
//							}	
//						}
//					}
//				}
//			}
			
		}

	    return rootProjectManageyf;  
	}
	
	private ProjectManageyf findResources(List<ProjectManageyf> list, Integer proId,List<ProjectManageyf> result) {
		
		for (ProjectManageyf projectManageyf : list) {
			if(projectManageyf.getFatherId()!=null){
				if(Integer.parseInt(projectManageyf.getFatherId()) == proId){
					if(!result.contains(projectManageyf)){
						result.add(projectManageyf);
					}
					findResources(list, projectManageyf.getId(), result);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> findProjectwhereByRootId(Integer rootId) {
		if(null!=rootId){
			List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = ? and status='完成' and rootId<>id",rootId);
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, list.get(0).getPoolId()); 
			for (ProjectManageyf projectManageyf : list) {
				//设定实际分数
				if(null==projectManageyf.getActualStore() && null==projectManageyf.getGradeStore()){
					projectManageyf.setActualStore(projectManageyf.getGradeStore());
				}
				
				projectManageyf.setProjectPool(pool);
				projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId())); //负责人
				//占比
				double proportion = (double)projectManageyf.getActualStore()/(double)projectManageyf.getSumStore()*100;
				NumberFormat nf = NumberFormat.getNumberInstance();
	            nf.setMaximumFractionDigits(2);
	            String format = nf.format(proportion);
				//dformat.format(proportion);
				projectManageyf.setProportion(format+"%");
				//proportion
				//金额
				Double totalMoney = pool.getTotalMoney(); //项目池总金额
				Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "
						+projectManageyf.getPoolId());//主项目总分数
				//获得主项目中主管申请的分数
				ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getRootId());
				if(null==totalCount ||null==yfProject.getGradeStore() ||null==totalMoney || null==projectManageyf.getActualStore()){
					//throw new Exception("主项目没有设置分数，不能查看项目汇总");
					projectManageyf.setMoney(null);
				}else{
					//总项目→金额
					double totalProportion =(double)yfProject.getGradeStore() * totalMoney/(double)totalCount;
					//本项目金额
					double money  = projectManageyf.getActualStore()*totalProportion/projectManageyf.getSumStore();
//					NumberFormat nf1 = NumberFormat.getNumberInstance();
//		            nf1.setMaximumFractionDigits(2);
//		            String format1 = nf1.format(money);
		            BigDecimal bg = new BigDecimal(money);  
		            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
					projectManageyf.setMoney(f1);
				}
			}
			return  list; 
		}
		return null;
	}
	
	

	//查找评分确认人列表
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfAgree> findProjectManageyfAgrees(
			Integer projectId) {
		if(null!=projectId){
			return totalDao.query("from ProjectManageyfAgree where projectManagerYfId = ?" +
					" and agreeUserId is null and agreeUserName is null", projectId);
			
		}
		return null;
	}
	
	//提交评分是否同意结果审批
	@SuppressWarnings("unchecked")
	@Override
	public String submitExamineResult(ProjectManageyfAgree agree) {
		Users loginUser = Util.getLoginUser();
		if(null== loginUser){
			return "请先登录";
		}
		
		if(null!= agree && null!= agree.getProjectManagerYfId() && null!=agree.getResult()){
			//查看是否已审批
			int count = totalDao.getCount("from ProjectManageyfAgree where agreeUserId =? and projectManagerYfId =?", loginUser.getId(),agree.getProjectManagerYfId());
			if(count>0){
				return "您已审批过此项目，请务重新审批";
			}
			//totalDao.save(agree);
			
			//查看是否是自己审批
			Integer oneself =totalDao.getCount("from ProjectManageyfUser where usertype='项目主管'" +
					" and userId =? and id in(select yfUserId from YfUser where projectManagerYfId = ?) ",
					loginUser.getId(), agree.getProjectManagerYfId());
			if(oneself>0){
				return "不能审批自己的项目,谢谢";
			}
			//获得项目池下有几个项目审批人
			List<Integer> list = totalDao.query("select userId from ProjectManageyfUser where id in(select yfUserId from YfUser where projectManagerYfId in (" +
					"select ymyf.id from ProjectManageyf ymyf where  ymyf.poolId = (select yf.poolId from ProjectManageyf yf where yf.id= "+agree.getProjectManagerYfId()+" ))) ");
			Set<Integer> set = new HashSet<Integer>();
			for (Integer integer : list) {
				set.add(integer);
			}
			
			//获得已审批人数
			Integer agreeCount = totalDao.getCount("from ProjectManageyfAgree where projectManagerYfId = ?" +
					" and agreeUserName is not null and result is not null", agree.getProjectManagerYfId());
			
			agree.setAgreeUserName(loginUser.getName());
			agree.setAgreeUserId(loginUser.getId());
			agree.setAgreeDate(Util.getDateTime("yyyy-MM-dd"));
			ProjectManageyf projectManageyf =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, agree.getProjectManagerYfId());
			//判断是不是需要最后评论的人
			if(set.size()-2>=agreeCount){ //是
				if("YES".equals(agree.getResult())){
					projectManageyf.setGradeStatus("同意");
				}else{
					projectManageyf.setGradeStatus("打回");
				}
			}else{
				if("YES".equals(agree.getResult())){
					projectManageyf.setGradeStatus("审批中");
				}else{
					projectManageyf.setGradeStatus("打回");
				}
			}
			totalDao.update(projectManageyf);
			return "审批成功";
		}
		
		return "操作失败";
	}

	//修改研发项目或添加子项目
	@SuppressWarnings("unchecked")
	@Override
	public Object[] saveAndUpdateYf(ProjectManageyf projectManageyf) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return new Object[]{"请先登录",0};
		}
		
		if(null!=projectManageyf){
			String currentDate = Util.getDateTime("yyyy-MM-dd");
			if(null==projectManageyf.getId()){
				//添加
				Integer rootId = projectManageyf.getRootId();
				if(projectManageyf.getProNum()!=null && !"".equals(projectManageyf.getProNum())){
					//项目编号由用户手动输入
					Integer count = totalDao.getCount("from ProjectManageyf where proNum=?", projectManageyf.getProNum());
					if(null!=count && count>0){
						return new Object[]{"项目编号已经存在！",0};
					}
				}else{
					String proNum = (String) totalDao.getObjectByCondition("select proNum from ProjectManageyf where id=?",rootId);
					String before = proNum+"-";// "propool-" + Util.getDateTime("yyyyMM-");
					String maxnumber = (String) totalDao.getObjectByCondition(
							"select max(proNum) from ProjectManageyf where proNum like '" + before + "%'");
					DecimalFormat df = new DecimalFormat("0");
					if(null==maxnumber || maxnumber.length()<before.length()){
						projectManageyf.setProNum(before + "1");
					}else{
						String now_number = maxnumber.split(before)[1];
						Integer number2 = Integer.parseInt(now_number) + 1;
						String number3 = df.format(number2);
						projectManageyf.setProNum(before +number3);
					}
				}
				projectManageyf.setAddTime(currentDate );
				projectManageyf.setAddUserId(loginUser.getId());
				projectManageyf.setAddUserCode(loginUser.getCode());
				projectManageyf.setAddUserName(loginUser.getName());
				projectManageyf.setStatus("待填报");
				if(null!=projectManageyf.getStartIsMilestone() && null!=projectManageyf.getEndIsMilestone() &&
						"true".equals(projectManageyf.getStartIsMilestone()) && "true".equals(projectManageyf.getEndIsMilestone())){
					projectManageyf.setStatus("完成");
				}
				totalDao.save(projectManageyf);

				if(projectManageyf.getGradeStore()!=null && projectManageyf.getGradeStore()>0) {
					Long count = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId());
					Integer sumStore;
					if(null==count){
						sumStore = projectManageyf.getGradeStore();
					}else{
						sumStore = Integer.parseInt(count.toString());
					}
					projectManageyf.setSumStore(sumStore);
					totalDao.update(projectManageyf);
					//设置其他项目的总分数
					List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = "+projectManageyf.getRootId());
					if(null!=list && list.size()>0){
						for (ProjectManageyf otherProject : list) {
							if(null!=otherProject){
								otherProject.setSumStore(projectManageyf.getSumStore());
								totalDao.update(otherProject);
							}
						}
					}
					updateyfProPd(projectManageyf.getId());
				}
				
				return new Object[]{"添加成功",projectManageyf.getId()};
			}else{
				Integer newGradeStore = projectManageyf.getGradeStore();
				Integer oldGradeStore = 0;
				//修改
				ProjectManageyf yf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
				if(null!=yf){
					oldGradeStore = yf.getGradeStore();
					yf.setProName(projectManageyf.getProName());
					yf.setRemark(projectManageyf.getRemark());
					yf.setStartTime(projectManageyf.getStartTime());
					yf.setReTime(projectManageyf.getReTime());
					yf.setGradeStore(newGradeStore);
					yf.setDepends(projectManageyf.getDepends());
					yf.setModel(projectManageyf.getModel());
					yf.setStartIsMilestone(projectManageyf.getStartIsMilestone());
					yf.setEndIsMilestone(projectManageyf.getEndIsMilestone());
					projectManageyf.setRootId(yf.getRootId());
					
					if(projectManageyf.getFatherId()!=null && !"".equals(projectManageyf.getFatherId()) 
							&& !"null".equals(projectManageyf.getFatherId())){
						yf.setFatherId(projectManageyf.getFatherId());
					}
					if(projectManageyf.getBelongLayer()!=null){
						yf.setBelongLayer(projectManageyf.getBelongLayer());
					}
//					if(null==yf.getGradeStore() && null!=projectManageyf.getSelfStore()){ //设置总分数
//						if(null!= projectManageyf.getSelfStore()){
//							yf.setGradeStore(projectManageyf.getGradeStore());
//						}
//					}else if(null!=yf.getGradeStore()){
//						if(null!= projectManageyf.getSelfStore()){
//							yf.setGradeStore(yf.getGradeStore()-yf.getSelfStore()+projectManageyf.getSelfStore());
//						}
//					}
					totalDao.update(yf);
					
					//判断是否改变了分数
					if(newGradeStore!=null && oldGradeStore!=null){
						int nStore = 0;
						int oStore = 0;
						if(newGradeStore!=null){
							nStore = newGradeStore;
						}
						if(oldGradeStore!=null){
							oStore = oldGradeStore;
						}
						if(nStore!=oStore){
							
							//分数改变后 变更其他项目和人员分数
							Long count = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId());
							Integer sumStore;
							if(null==count){
								sumStore = yf.getGradeStore();
							}else{
								sumStore = Integer.parseInt(count.toString());
							}
							yf.setSumStore(sumStore);
							totalDao.update(yf);
							//设置其他项目的总分数
							List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = "+yf.getRootId());
							if(null!=list && list.size()>0){
								for (ProjectManageyf otherProject : list) {
									if(null!=otherProject){
										otherProject.setSumStore(yf.getSumStore());
										totalDao.update(otherProject);
									}
								}
							}
							updateyfProPd(yf.getId());//更新人员明细的金额和占比
						}
						
						
					}
					
					//设置父项目的状态
					if(yf.getFatherId()!=null){
						projectManageyf= (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class,Integer.parseInt(yf.getFatherId()));
						if("待确认".equals(projectManageyf.getStatus())){
							projectManageyf.setStatus("执行中");
							totalDao.update(projectManageyf);
						}
					}
					return new Object[]{"修改成功",yf.getId()};
				}else{
					return new Object[]{"数据异常，请刷新后重试",0};
				}
			}
		}else{
			return new Object[]{"数据异常，请刷新后重试",0};
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String delProject(Integer id,String pageStatus) {
		if(null!=id){
			ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, id);
			if(projectManageyf.getRootId()==id){
				if(null==pageStatus || !"manage".equals(pageStatus)){
					return "没有权限删除跟项目";
				}else{
					
				}
			}
			Integer count = totalDao.getCount("select count(*) from ProjectManageyf where fatherId = ?", String.valueOf(id) );
			if(count>0){
				return "还有子项目，不能删除";
			}
			List<ProjectManageyfUser> yfList = totalDao.query("from ProjectManageyfUser where id in(select yfUserId from YfUser where projectManagerYfId="+projectManageyf.getId()+")");
			for (ProjectManageyfUser projectManageyfUser : yfList) {
				List<ProjectManageYfpd> list = totalDao.query("from ProjectManageYfpd where proId="+projectManageyf.getId()+" and userId = "+projectManageyfUser.getUserId());
				for (ProjectManageYfpd projectManageYfpd : list) {
					totalDao.delete(projectManageYfpd);
				}
				totalDao.delete(projectManageyfUser);
			}
			List<YfUser> yfuserList = totalDao.query("from YfUser where projectManagerYfId="+projectManageyf.getId());
			for (YfUser yfUser : yfuserList) {
				totalDao.delete(yfUser);
			}
			if(totalDao.delete(projectManageyf)){
				Integer integer = totalDao.getCount("from ProjectManageyf where belongLayer = "+projectManageyf.getBelongLayer()+" and rootId="+projectManageyf.getRootId() );
				if(integer==0){
					if(null!=projectManageyf.getFatherId()){
						ProjectManageyf yfManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, Integer.parseInt(projectManageyf.getFatherId()));
						if(null!=yfManageyf){
							updateyfProPd(yfManageyf.getId());
						}
						
					}
				}else{
					List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where belongLayer = "+projectManageyf.getBelongLayer()+" and rootId="+projectManageyf.getRootId());
					if(null!=list && list.size()>0){
						updateyfProPd(list.get(0).getId());
					}
				}
				//TODO:更新其他项目的sumStore
				Long gradeStore = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId());
				List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = "+projectManageyf.getRootId());
				if(null!=list && list.size()>0){
					for (ProjectManageyf otherProject : list) {
						if(null!=otherProject){
							if(gradeStore!=null){
								otherProject.setSumStore(Integer.parseInt(gradeStore.toString()));
							}
							totalDao.update(otherProject);
						}
					}
				}
				//updateyfProPd(projectManageyf.getId());
				return "删除成功";
			}else{
				return "删除失败";
			}
		}
		return null;
	}

	//查找所有部门
	@SuppressWarnings("unchecked")
	@Override
	public List<DeptNumber> findDeptByRootId(Integer rootId) {
		if(rootId!=null){//where fatherId = ?, rootId
			return totalDao.query("from DeptNumber ");
		}
		return null;
	}

	//添加项目参与人
	@Override
	public String[] addProjectPlayers(Integer userId,Integer yfProjectId) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return new String[]{"请先登录","0"};
		}
		ProjectManageyf yfProject = getProjectManageyfById(yfProjectId);
		if(null==yfProject){
			return new String[]{"该项目不存在","0"};
		}
		Users user=null;
		if(null==userId){
			return new String[]{"不存在的用户","0"};
		}else {
			user = (Users) totalDao.getObjectById(Users.class, userId);
			if(user==null) {
				return new String[]{"不存在的用户","0"};
			}
		}
		//查看是否已经添加
		Integer count = totalDao.getCount("from ProjectManageyfEr where projectId = ? and addUserId=?",yfProjectId,userId);
//		Integer count = totalDao.getCount("select count(*) from YfUser where projectManagerYfId =? and yfUserId in(select id from ProjectManageyfUser where userId =?)", yfProjectId,userId);
		if(count>0){
			return new String[]{"此用户已经在此项目中了","0"};
		}
		String date = Util.getDateTime("yyyy-MM-dd");
		ProjectManageyfEr er = new ProjectManageyfEr();
		er.setProjectId(yfProjectId);
		er.setAddDate(date);
		er.setAddUserId(user.getId());
		er.setAddUserName(user.getName());
//		ProjectManageyfUser projectYfUser = new ProjectManageyfUser();
//		projectYfUser.setUserId(userId);
//		projectYfUser.setUserDept(user.getDept());
//		projectYfUser.setUserCode(user.getCode());
//		projectYfUser.setUserName(user.getName());
//		projectYfUser.setUsertype("参与人");
//		projectYfUser.setPoolId(yfProject.getPoolId());
//		projectYfUser.setRootId(yfProject.getRootId());
//		projectYfUser.setZpTime(date);
//		if(totalDao.save(projectYfUser)){
		if(totalDao.save(er)) {
//			YfUser yfUser = new YfUser();
//			yfUser.setProjectManagerYfId(yfProjectId);//项目id
//			yfUser.setYfUserId(projectYfUser.getId());//参与人id
//			yfUser.setApprove(false);//未认证
//			totalDao.save(yfUser);
			//ProjectManageyf projectYf =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, yfProjectId);
			AlertMessagesServerImpl.addAlertMessages("研发项目预选参与人", loginUser.getName()+
					"，预选您负责参与研发项目:"+yfProject.getProName()+"!请前往报选子项目。", new Integer[]{user.getId()},
					"projectPoolAction_toAddSubProject.action?pageStatus=applychoose&id="+yfProjectId, true);
			yfProject.setStatus("待填报");
			return new String[]{"success",String.valueOf(er.getId())};
		}else{
			return new String[]{"出错了","0"};
		}
	}

	//根据主项目id和项目id获得未参与人列表   id=主项目id   id2=proId
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfUser> findprojectYfUserByProId(Integer rootId,Integer proId) {
		Users loginUser = Util.getLoginUser();
		//本项目不显示
		String appendHql = "";
		if(null!= proId){
			appendHql = " and id <> "+proId;
		}
		
		if(null!=rootId){
			return totalDao.query("from ProjectManageyfUser where userId <> "+loginUser.getId()+" and  id in(select MIN(id) " +
					"from ProjectManageyfUser where rootId = ? "+appendHql+" GROUP BY userId)", rootId);
		}
		return null;
	}

	//取消参与人
	@Override
	public String cancelPlayers(Integer erId, Integer yfProjectId) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		ProjectManageyf yfProject = getProjectManageyfById(yfProjectId);
		if(null==yfProject){
			return "该项目不存在";
		}
		ProjectManageyfEr er = (ProjectManageyfEr) totalDao.getObjectById(ProjectManageyfEr.class, erId);
		if(er!=null) {
			boolean delete = totalDao.delete(er);
			if(delete) {
				return "success";
			}else {
				return "删除失败";
			}
		}else {
			return "没有找到预选人";
		}
//		YfUser yfUser = (YfUser) totalDao.getObjectByCondition("from YfUser where projectManagerYfId =? and yfUserId =? and approve=false", yfProjectId,proejctYfUserId);
//		if(null!=yfUser){
//			totalDao.delete(yfUser);
//			ProjectManageyfUser projectManageyfUser = (ProjectManageyfUser) totalDao.getObjectById(ProjectManageyfUser.class, proejctYfUserId);
//			totalDao.delete(projectManageyfUser);
//			updateyfProPd(yfProjectId);
//			return "success";
//		}
//		return null;
	}
	
	//根据部门id获得所有人
	@SuppressWarnings("unchecked")
	@Override
	public List getUsersByDeptId(Integer id,String ids) {
		if (id != null && id != 0) {
			
//			deptIds = new ArrayList<Integer>();
//			getUnderDeptIdById(id);
//			deptIds.add(id);
//			StringBuffer sb = new StringBuffer();
//			for (int i = 0; i < deptIds.size(); i++) {
//				if (i == (deptIds.size() - 1)) {
//					sb.append(deptIds.get(i) + ")");
//				} else {
//					sb.append(deptIds.get(i) + ",");
//				}
//			}
//			List<String> deptnames = totalDao
//					.query("select dept from DeptNumber where id in("
//							+ sb.toString());
//			StringBuffer sb2 = new StringBuffer();
//			for (int i = 0; i < deptnames.size(); i++) {
//				if (i == (deptnames.size() - 1)) {
//					sb2.append("'" + deptnames.get(i) + "')");
//				} else {
//					sb2.append("'" + deptnames.get(i) + "',");
//				}
//			}
			String whereIds = "";
			if(null!=ids && ids.length()>0){
				whereIds = " and id not in ("+ids+") ";
			}
			String deptName = (String) totalDao.getObjectByCondition("select dept from DeptNumber where id = ?", id);
			List list = totalDao
					.query("from Users where dept=? and onWork not in ('离职','离职中','内退','退休','病休')"
							+whereIds + " order by code ",deptName);
			
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * 通过部门id递归获取该部门下所有的下级部门id
	 * 
	 * @param deptId
	 */
	@SuppressWarnings("unchecked")
	public void getUnderDeptIdById(Integer deptId) {
		List list = totalDao.query("select id from DeptNumber where fatherId="
				+ deptId);
		if (list.size() != 0) {
			List<Integer> ids = list;
			deptIds.addAll(ids);
			for (Integer id : ids) {
				deptId = id;
				getUnderDeptIdById(deptId);
			}
		}

	}

	//根据项目id获得所有参与人
	@SuppressWarnings("unchecked")
	@Override
	public ProjectManageyfUser getProjectYfUserByprojectId(Integer projectId) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return null;
		}
		//ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectId);
//		totalDao.getObjectById(ProjectManageyf.class, projectId);
		String hql = "from ProjectManageyfUser where userId = ? and id in(select yfUserId from YfUser where projectManagerYfId = ?)";
		List<ProjectManageyfUser> list = totalDao.query(hql, loginUser.getId(),projectId);
		if(null!=list && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	//报选项目
	@SuppressWarnings("unchecked")
	@Override
	public String chooseSubProject(Integer projectId) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		ProjectManageyf projectManageyf =  (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectId);
		//获得root项目id
		Integer rootId = projectManageyf.getRootId();
		Integer count = totalDao.getCount("from ProjectManageyfEr where addUserId = ? and projectId=?",loginUser.getId(), rootId);
		if(null!=count && count>0) {
			return "您已经报选了此项目，请勿重新申报";
		}
//		List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where userId = ? and rootId =?", loginUser.getId(),rootId);
//		if(null==list || list.size()==0){
//			return "您没有被指派为此项目的参与人，或者您已经参与了子项目";
//		}
		//ProjectManageyfUser projectManageyfUser = list.get(0);
//		List<YfUser> yfUserList = totalDao.query("from YfUser where projectManagerYfId =? and yfUserId =?",rootId,projectManageyfUser.getId());
//		if(null!=yfUserList && yfUserList.size()>0){
//			YfUser yfUser = yfUserList.get(0);
//			yfUser.setProjectManagerYfId(projectId);
//			totalDao.update(yfUser);
			//获得审批人→主项目负责人id
			List<Integer> fzrList =totalDao.query("select userId from ProjectManageyfUser where userType='项目主管'" +
					" and id in(select yfUserId from YfUser where projectManagerYfId=?)", rootId);
			
			if(null!=fzrList && fzrList.size()>0){
				ProjectManageyfEr er   = new ProjectManageyfEr();
				er.setAddDate(Util.getDateTime("yyyy-MM-dd"));
				er.setAddUserId(loginUser.getId());
				er.setAddUserName(loginUser.getName());
				er.setProjectId(projectId);
				er.setStatus("预申请");
				totalDao.save(er);
				for (int i = 0; i < fzrList.size(); i++) {
					
					Integer userId = fzrList.get(i);
					AlertMessagesServerImpl.addAlertMessages("报选项目申请", loginUser.getName()+
							"，报选负责研发项目:"+projectManageyf.getProName()+"!", new Integer[]{userId},
							"projectPoolAction_toAddSubProject.action?id="+rootId, true);
				}
				return "success";
			}
//		}
		
		return null;
	}

	//填报项目进度
	@Override
	public String fillSchedule(ProjectManageyf projectManageyf) {
		
		ProjectManageyf project =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
		
		
		
		return null;
	}

	//查询项目中的申报人
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfEr> selectChooseList(Integer projectId,String status) {
		String hqlStatus = "";
		if(status!=null &&!"".equals(status)) {
			hqlStatus = "and status = '"+status+"'";
		}
		List<ProjectManageyfEr> list = totalDao.query("from ProjectManageyfEr where projectId = ?"+hqlStatus, projectId);
		
		return list;
	}

	//审批报选项目
	@SuppressWarnings("unchecked")
	@Override
	public String examineProject(ProjectManageyfEr er,Integer rootProjectId,Integer weight) {
		ProjectManageyfEr projectManageyfEr = (ProjectManageyfEr) totalDao.getObjectByCondition("from ProjectManageyfEr "
				+ " where projectId=? and addUserId=? ", er.getProjectId(),er.getAddUserId());
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(
				ProjectManageyf.class, projectManageyfEr.getProjectId());
		Users loginUser = Util.getLoginUser();
		if(loginUser==null) {
			return "请先登录";
		}
		if(null!=projectManageyfEr){
			
			Integer count = totalDao.getCount("from ProjectManageyfUser where userId = ? and "
					+ " id in(select yfUserId from YfUser where approve=true and projectManagerYfId=?)",
					er.getAddUserId(),er.getProjectId());
			if(count!=null && count>0) {

				projectManageyfEr.setStatus(er.getStatus());
				totalDao.update(projectManageyfEr);
				return "指派成功";
			}
			projectManageyfEr.setStatus(er.getStatus());
			if("同意".equals(er.getStatus())){
				Users users  = (Users) totalDao.getObjectById(Users.class, er.getAddUserId());
				ProjectManageyfUser projectManageyfUser = new ProjectManageyfUser();
				
				projectManageyfUser.setPoolId(projectManageyf.getPoolId());
				projectManageyfUser.setRootId(projectManageyf.getRootId());
				projectManageyfUser.setUserCode(users.getCode());
				projectManageyfUser.setUserDept(users.getDept());
				projectManageyfUser.setUserId(users.getId());
				projectManageyfUser.setUserName(users.getName());
				projectManageyfUser.setUsertype("参与人");
				projectManageyfUser.setWeight(weight);
				projectManageyfUser.setZpTime(Util.getDateTime("yyyy-MM-dd"));
				boolean save = totalDao.save(projectManageyfUser);
				if(save) {
					YfUser yfUser = new YfUser();
					yfUser.setProjectManagerYfId(er.getProjectId());
					yfUser.setYfUserId(projectManageyfUser.getId());
					yfUser.setApprove(true);
					boolean save2 = totalDao.save(yfUser);
					if(save2) {
						List<ProjectManageYfpd> listPd = totalDao.query("from ProjectManageYfpd where userId="+projectManageyfUser.getUserId()
						+" and proId = "+projectManageyf.getId());
						if(null==listPd || listPd.size()==0){
							//添加人员明细信息
							ProjectManageYfpd pd = new ProjectManageYfpd();
							pd.setProId(projectManageyf.getId());
							pd.setUserId(projectManageyfUser.getUserId());
							pd.setUserCode(projectManageyf.getUserCode());
							pd.setUserName(projectManageyfUser.getUserName());
							pd.setStartTime(Util.getDateTime("yyyy-MM-dd"));
							if(projectManageyf.getGradeStore()!=null){
								double proportion = getProportion(projectManageyf);
								BigDecimal bg= new BigDecimal(proportion*100);
								int doubleValue = bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
								pd.setProportion(doubleValue +"%"); //占比
								BigDecimal bigDecimal = new BigDecimal(getProMoneyByProId(projectManageyf));
								double money = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
								pd.setMoney(money); //金额
								pd.setWeight(weight);//权重
							}
							totalDao.save(pd);
							//更新其他人员明细信息
							if(projectManageyf.getGradeStore()!=null){
								updateyfProPd(pd.getProId());
							}
						}else {
							ProjectManageYfpd pd = listPd.get(0);
							pd.setWeight(weight);
							totalDao.update(pd);
						}
						
						if("待填报".equals(projectManageyf.getStatus())){
							projectManageyf.setStatus("待完善");
							if(null==projectManageyf.getZpTime() || "".equals(projectManageyf.getZpTime())){
								projectManageyf.setZpTime(Util.getDateTime("yyyy-MM-dd"));
							}
							totalDao.update(projectManageyf);
						}
						totalDao.update(projectManageyfEr);
						AlertMessagesServerImpl.addAlertMessages("研发项目提醒", loginUser.getName()+
								"，同意您报选的项目:"+projectManageyf.getProName()+"的研发，请查收", new Integer[]{projectManageyfUser.getUserId()},
								"projectPoolAction_selfProjectManageyfList.action?pageStatus=applychoose", true);
						return "指派成功";
					}else {
						return "保存出错";
					}
				}else {
					return "保存失败";
				}
				
			}else {
				totalDao.update(projectManageyfEr);
				AlertMessagesServerImpl.addAlertMessages("研发项目提醒", 
						"您报选的"+projectManageyf.getProName()+"项目被打回，请重新申请。", new Integer[]{er.getAddUserId()},
						"projectPoolAction_getERById.action?pageStatus=applychoose", true);
				return "审批成功";
			}
		}
		return "系统异常";
	}

	//保存或提交项目
	@SuppressWarnings("unchecked")
	@Override
	public String saveOrSubmitSchedule(ProjectManageyf projectYf, String tag) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		if(null!=projectYf){
			ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectYf.getId());
			
			projectManageyf.setSchedule(projectYf.getSchedule());
			if(null!=projectYf.getYfProjectFile() && !"".equals(projectYf.getYfProjectFile())){
				projectManageyf.setYfProjectFile(projectYf.getYfProjectFile());
				projectManageyf.setAliasFile(projectYf.getAliasFile());
			}
			
			if("save".equals(tag)){
				projectManageyf.setStatus("执行中");
				totalDao.update(projectManageyf);
				return "保存成功";
			}else{
				//查询项目状态，是否可提交
				Integer count = totalDao.getCount("from ProjectManageyf where fatherId="+projectManageyf.getId()+" and status<>'完成' ");
				if(count!=null && count>0){
					return "未完成所有的子项目，不能提交父项目。";
				}
				//查看文件是否为项目主管提交，如果是，则直接完成项目
				String userCode = loginUser.getCode();
				Integer count2 = totalDao.getCount("from ProjectManageyf where id in(select projectManagerYfId from YfUser" +
						" where yfUserId in(select id from ProjectManageyfUser where userCode=? and usertype='项目主管'))", userCode);
				if(count2!=null && count2>0){
					projectManageyf.setStatus("完成");
					totalDao.update(projectManageyf);
				}else{
					projectManageyf.setStatus("待确认");
					totalDao.update(projectManageyf);
					//根据rootid查询父层的项目
					if(projectManageyf.getBelongLayer()>1){
						//totalDao.query("from ProjectManageyfUser where id in(select y.yfUserId from YfUser y where y.projectManagerYfId = "+projectManageyf.getFatherId()+")");
						List<ProjectManageyfUser> examineList = totalDao.query("from ProjectManageyfUser" +
								" where id in(select yfUserId from YfUser where approve =true and" +
								" projectManagerYfId = "+projectManageyf.getFatherId()+")");
						if(null!=examineList && examineList.size()>0){
							for (ProjectManageyfUser yfUser : examineList) {
								//加载审批流程
								AlertMessagesServerImpl.addAlertMessages("研发项目提交审批", "研发项目子项目："+projectManageyf.getProName()+
									"，完成已提交，请您审批。", new Integer[]{yfUser.getUserId()},
									"projectPoolAction_toExamineSubProject.action?id="+projectManageyf.getId(), true);
								
							}
						}else{
							//父父项目人员审批
							examineList = totalDao.query("from ProjectManageyfUser where id in " +
									"(select yfUserId from YfUser where approve =true and projectManagerYfId in(" +
									"select p.fatherId from ProjectManageyf p where p.id="+projectManageyf.getFatherId()+"))" );
							if(null!=examineList && examineList.size()>0){
								for (ProjectManageyfUser yfUser : examineList) {
									//加载审批流程
									AlertMessagesServerImpl.addAlertMessages("研发项目提交审批", "研发项目子项目："+projectManageyf.getProName()+
										"，完成已提交，请您审批。", new Integer[]{yfUser.getUserId()},
										"projectPoolAction_toExamineSubProject.action?id="+projectManageyf.getId(), true);
								}
							}else{
								return "没有找到要审批的父项目人员，请稍后再试";
							}
						}
					}else{
						//主项目交给项目主管审批
						List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where usertype='项目主管' and id in (" +
								"select yfUserId from YfUser where projectManagerYfId = "+projectManageyf.getId()+")");
						if(null!= list && list.size()>0){
							for (ProjectManageyfUser yfUser : list) {
								//加载审批流程
								AlertMessagesServerImpl.addAlertMessages("研发项目提交审批", "研发项目："+projectManageyf.getProName()+
									"，完成已提交，请您审批。", new Integer[]{yfUser.getUserId()},
									"projectPoolAction_toExamineSubProject.action?id="+projectManageyf.getId(), true);
								
							}
						}
					}
				}
				
				return "提交成功";
			}
			//projectManageyf.setYfProjectFile(yfProjectFile);
			
		}else{
			return "请填写内容";
		}
	}

	//审批子项目
	//result 审批结果 同意or打回
	@SuppressWarnings("unchecked")
	@Override
	public String examineSubProject(ProjectManageyf projectManageyf, String result) {//
		ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
		if(null!=yfProject && ("完成".equals(yfProject.getStatus()) || "打回".equals(yfProject.getStatus()))){
			return "您已经审批过此项目，请勿重复审批。";
		}
		if(null!=yfProject){
			if("同意".equals(result)){
				yfProject.setActualStore(projectManageyf.getActualStore());//实际分值
				if((int)yfProject.getRootId()==(int)yfProject.getId()){
					yfProject.setStatus("确认关闭");
				}else{
					yfProject.setStatus("完成");
				}
				yfProject.setShijiTime(Util.getDateTime("yyyy-MM-dd"));
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					long time = format.parse(yfProject.getShijiTime()).getTime()-format.parse(yfProject.getReTime()).getTime();
					float day = (float)time/(1000*3600*24f);
					yfProject.setDelayTime((int)day);//提前为正数，延期为负数
					
				} catch (Exception e) {
				}
				//设定人员明细
				List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where id in(select yfUserId from YfUser where" +
						" approve =true and projectManagerYfId =?)",yfProject.getId());
				ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, yfProject.getPoolId());
				Integer totalWeight = 0;
				for (ProjectManageyfUser projectManageyfUser : list) {
					ProjectManageYfpd pd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where userId = ? and proId=?",projectManageyfUser.getUserId(),yfProject.getId());
					if(null==pd || null==pd.getWeight()){
						continue;
					}
					
					totalWeight+=pd.getWeight();
				}
				for (ProjectManageyfUser projectManageyfUser : list) {
					ProjectManageYfpd pd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where userId = ? and proId=?",projectManageyfUser.getUserId(),yfProject.getId());
					if(null==pd || null==pd.getWeight()){
						continue;
					}
					double proportion = getProportion(yfProject);
					double bili = (double)pd.getWeight()/(double)totalWeight;
					BigDecimal bg = new BigDecimal(proportion*bili);
					double doubleValue = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
					
					pd.setProportion((doubleValue*100)+"%");
					
					pd.setMoney(doubleValue*pool.getTotalMoney());
//					if(yfProject.getSelfStore()!=null && yfProject.getSelfStore()== yfProject.getActualStore()){
//						pd.setsProportion(pd.getyProportion());
//						pd.setsMoney(pd.getyMoney());
//					}else{
//						double proportion  = (double)yfProject.getActualStore()/(double)yfProject.getSumStore()*100;//项目分值
//						double selfProprotion = proportion/ list.size();
//						NumberFormat nf = NumberFormat.getNumberInstance();
//				        nf.setMaximumFractionDigits(2);
//				        String format = nf.format(selfProprotion);
//				        
//						pd.setsProportion(format+"%"); //设置个人分值
//						
//						ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, yfProject.getPoolId());
//						//项目池金额
//						Double totalMoney = pool.getTotalMoney(); //项目池总金额
//						Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "
//								+yfProject.getPoolId());//主项目总分数
//						//获得主项目中主管申请的分数
//						ProjectManageyf rootProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, yfProject.getRootId());
//						if(null==totalCount ||null==rootProject.getGradeStore() ||null==totalMoney || null==yfProject.getActualStore()){
//							//throw new Exception("主项目没有设置分数，不能查看项目汇总");
//						}else{
//							//总项目→金额
//							//double totalProportion =(double)yfProject.getGradeStore() * totalMoney/(double)totalCount;
//							//本项目金额
//							double money  = yfProject.getActualStore()*totalMoney/yfProject.getSumStore();
//				            BigDecimal bg = new BigDecimal(money/list.size());
//				            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
//							pd.setsMoney(f1);
//						}
//					}
					totalDao.update(pd);
				}
				
				//父项目
				if(null!=yfProject.getFatherId()){
					projectManageyf.setRootId(yfProject.getRootId());
					projectManageyf.setFatherId(yfProject.getFatherId());
					int count = 0;
					while (null!=projectManageyf.getFatherId()) {
						count++;
						projectManageyf = (ProjectManageyf) totalDao.getObjectByCondition("from ProjectManageyf where rootId = "
								+projectManageyf.getRootId()+" and id="+projectManageyf.getFatherId()+" ");//and rootId<>id
						
						//List<ProjectManageyf> fatherList = totalDao.query("from ProjectManageyf where rootId="+projectManageyf.getRootId()+
						//		" and belongLayer="+projectManageyf.getBelongLayer());//父项目中的当前层
//						int successCount = 0; //完成的项目总数
//						for (ProjectManageyf fatherPro : fatherList) {
//							if("完成".equals(fatherPro.getStatus())){
//								successCount++;
//							}
//						}
						//本层项目的总数和已完成总数
						int befongLayerSuccess = 0;
						List<ProjectManageyf> beProList = totalDao.query("from ProjectManageyf where rootId="+projectManageyf.getRootId()+
								" and belongLayer="+(projectManageyf.getBelongLayer()+1)+" and fatherId = "+projectManageyf.getId());
						for (ProjectManageyf bePro : beProList) {
							if("完成".equals(bePro.getStatus())){
								befongLayerSuccess++;
							}
						}
						if((int)projectManageyf.getRootId()==(int)projectManageyf.getId()){
							if(beProList.size()==befongLayerSuccess){
								projectManageyf.setStatus("待提交");
								
							}else{
								projectManageyf.setStatus("执行中");
							}
							totalDao.update(projectManageyf);
							break;
						}
						
						if(beProList.size()==befongLayerSuccess){
							projectManageyf.setStatus("完成");
							
						}else{
							projectManageyf.setStatus("执行中");
						}
						totalDao.update(projectManageyf);
					}
				}
				
				
			}else{
				yfProject.setStatus("重新执行");
			}
			
			totalDao.update(yfProject);
			return "审批成功！";
		}
		return "没有找到这个项目";
	}

	//直接指派参与人
	@SuppressWarnings("unchecked")
	@Override
	public String directBindPlayers(YfUser yfUser,Integer nowId,Integer weight) {
		Users loginUser = Util.getLoginUser();
		List<YfUser> list = totalDao.query("from YfUser where yfUserId = ? and projectManagerYfId = ?",
				yfUser.getYfUserId(),yfUser.getProjectManagerYfId());//yfUser.getProjectManagerYfId() == rootId
		if(null!=list && list.size()>0){
			YfUser yfUser2 = list.get(0);
			yfUser2.setProjectManagerYfId(nowId);
			yfUser2.setApprove(true);
			ProjectManageyf projectManageyf =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, yfUser2.getProjectManagerYfId());//本项目
			if("待填报".equals(projectManageyf.getStatus())){
				projectManageyf.setStatus("待完善");
				if(null==projectManageyf.getZpTime() || "".equals(projectManageyf.getZpTime())){
					projectManageyf.setZpTime(Util.getDateTime("yyyy-MM-dd"));
				}
				totalDao.update(projectManageyf);
			}
			totalDao.update(yfUser2);
			ProjectManageyfUser projectManageyfUser = (ProjectManageyfUser) totalDao.getObjectById(ProjectManageyfUser.class,yfUser.getYfUserId());
			List<ProjectManageYfpd> listPd = totalDao.query("from ProjectManageYfpd where userId="+projectManageyfUser.getUserId()+" and proId = "+projectManageyf.getId());
			if(null==listPd || listPd.size()==0){
				//添加人员明细信息
				ProjectManageYfpd pd = new ProjectManageYfpd();
				pd.setProId(projectManageyf.getId());
				pd.setUserId(projectManageyfUser.getUserId());
				pd.setUserCode(projectManageyf.getUserCode());
				pd.setUserName(projectManageyfUser.getUserName());
				pd.setStartTime(Util.getDateTime("yyyy-MM-dd"));
				if(projectManageyf.getGradeStore()!=null){
					double proportion = getProportion(projectManageyf);
					BigDecimal bg= new BigDecimal(proportion*100);
					int doubleValue = bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
					pd.setProportion(doubleValue +"%"); //占比
					BigDecimal bigDecimal = new BigDecimal(getProMoneyByProId(projectManageyf));
					double money = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					pd.setMoney(money); //金额
					pd.setWeight(weight);//权重
				}
				totalDao.save(pd);
				//更新其他人员明细信息
				if(projectManageyf.getGradeStore()!=null){
					updateyfProPd(pd.getProId());
				}
			}
			AlertMessagesServerImpl.addAlertMessages("研发项目指派", loginUser.getName()+
					"，指派你参与:"+projectManageyf.getProName()+"的研发，查收", new Integer[]{projectManageyfUser.getUserId()},
					"projectPoolAction_selfProjectManageyfList.action?pageStatus=applychoose", true);
			return "添加成功！";
		}else if(null!=yfUser.getProjectManagerYfId() && null!=yfUser.getYfUserId()){
			//判断此项目中是否已经存在了该人员
			List<ProjectManageyfUser> query = totalDao.query("from ProjectManageyfUser where id in (" +
					"select yfUserId from YfUser where projectManagerYfId ="+yfUser.getProjectManagerYfId()+
					" and yfUserId = "+yfUser.getYfUserId()+")");
			if(null==query || query.size()==0){
				//totalDao.save(yfUser);
				YfUser yfUser2 = new YfUser();
				yfUser2.setYfUserId(yfUser.getYfUserId());
				yfUser2.setProjectManagerYfId(nowId);
				yfUser2.setApprove(true);
				ProjectManageyf projectManageyf =(ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, yfUser2.getProjectManagerYfId());//本项目
				if("待填报".equals(projectManageyf.getStatus())){
					projectManageyf.setStatus("待完善");
					if(null==projectManageyf.getZpTime() || "".equals(projectManageyf.getZpTime())){
						projectManageyf.setZpTime(Util.getDateTime("yyyy-MM-dd"));
					}
					totalDao.update(projectManageyf);
				}
				totalDao.save(yfUser2);
				ProjectManageyfUser projectManageyfUser = (ProjectManageyfUser) totalDao.getObjectById(ProjectManageyfUser.class,yfUser.getYfUserId());
				List<ProjectManageYfpd> listPd = totalDao.query("from ProjectManageYfpd where userId="+projectManageyfUser.getUserId()+" and proId = "+projectManageyf.getId());
				if(null==listPd || listPd.size()==0){
					//添加人员明细信息
					ProjectManageYfpd pd = new ProjectManageYfpd();
					pd.setProId(projectManageyf.getId());
					pd.setUserId(projectManageyfUser.getUserId());
					pd.setUserCode(projectManageyf.getUserCode());
					pd.setUserName(projectManageyfUser.getUserName());
					pd.setStartTime(Util.getDateTime("yyyy-MM-dd"));
					double proportion = getProportion(projectManageyf);
					BigDecimal bg= new BigDecimal((proportion*100));
					double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					pd.setProportion(doubleValue +"%"); //占比
					BigDecimal bigDecimal = new BigDecimal(getProMoneyByProId(projectManageyf));
					double money = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					pd.setMoney(money); //金额
					pd.setWeight(weight);//权重
					totalDao.save(pd);
					updateyfProPd(pd.getProId());//更新其他人员信息
				}
				AlertMessagesServerImpl.addAlertMessages("研发项目指派", loginUser.getName()+
						"，指派你参与:"+projectManageyf.getProName()+"的研发，请查收!", new Integer[]{projectManageyfUser.getUserId()},
						"projectPoolAction_selfProjectManageyfList.action?pageStatus=applychoose", true);
				return "添加成功！";
			}else{
				return "该项目中已有该参与人，请勿重复添加。";
			}
		}
		return "指派失败";
	}

	//更新人员明细信息
	@SuppressWarnings("unchecked")
	public void updateyfProPd(Integer proId){
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		Integer belongLayer = projectManageyf.getBelongLayer();
		Integer rootId = projectManageyf.getRootId();
		ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class,projectManageyf.getPoolId());
		if(pool.getTotalMoney()==null || pool.getTotalMoney()<=0){
			return;
		}
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where belongLayer="+belongLayer+" and rootId = "+rootId);
		for (ProjectManageyf yf : list) {
			//获取项目中的所有人员明细
			List<ProjectManageYfpd> pdList = totalDao.query("from ProjectManageYfpd where proId="+yf.getId());
			double totalWeight = 0;//本项目总权重
			for (ProjectManageYfpd pd : pdList) {
				if(null==pd.getWeight()){
					pd.setWeight(0);
				}
				totalWeight+=pd.getWeight();
			}
			//项目池占比
			double proportion = getProportion(yf);
			//项目金额
			double money = pool.getTotalMoney();
			
			for (ProjectManageYfpd pd : pdList) {
				//更新人员明细
				
				//权重总分
				double bili =  (double)pd.getWeight()/totalWeight;
				//个人所得占比
				double selfProportion = proportion*bili;
				//个人所得金额
				double selfMoney = selfProportion*money;
				pd.setMoney(selfMoney);
				pd.setProportion((selfProportion*100)+"%");
				totalDao.update(pd);
			}
//			if(1==yf.getBelongLayer()){
//			}
			//查找子层项目
			List<ProjectManageyf> subList = getSubListById(yf.getId());
			for (ProjectManageyf subPro : subList) {
				//获取项目中的所有人员明细
				List<ProjectManageYfpd> pdSubList = totalDao.query("from ProjectManageYfpd where proId="+subPro.getId());
				double totalSubWeight = 0;
				for (ProjectManageYfpd subPd : pdSubList) {
					if(subPd.getWeight()!=null) {
						totalSubWeight+=subPd.getWeight();
					}
				}
				for (ProjectManageYfpd subPd : pdSubList) {
					//项目池占比
					double subProportion = getProportion(subPro);
					
					//项目池金额
					double subMoney = pool.getTotalMoney();
					if(subPd.getWeight()==null || subPd.getWeight()==0){
						continue;
					}
					//更新人员明细
					//double money = getProMoneyByProId(subPro);
					//权重总分
					if(null==subPd.getWeight()){
						subPd.setWeight(0);
					}
					double bili =  (double)subPd.getWeight()/(double)totalSubWeight;
					//个人所得占比
					BigDecimal bg = new BigDecimal(subProportion*bili);
					double selfProportion = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					//个人所得金额
					double selfMoney = selfProportion*subMoney;
					subPd.setMoney(selfMoney);
					subPd.setProportion((selfProportion*100)+"%");
					totalDao.update(subPd);
				}
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String cancalBindPlayers(YfUser yfUser) {
		List<YfUser> list = totalDao.query("from YfUser where  yfUserId =? and projectManagerYfId =?", 
				yfUser.getYfUserId(),yfUser.getProjectManagerYfId());
		if(null!=list && list.size()>0){
			YfUser yfUser2 = list.get(0);
			Integer rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProjectManageyf where id=?", yfUser.getProjectManagerYfId());
			if(null!=rootId){
				yfUser2.setProjectManagerYfId(rootId);
				totalDao.update(yfUser2);
				updateyfProPd(yfUser.getProjectManagerYfId());
			}
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfUser> getUserListByProId(Integer proId) {
		List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where id in " +
				"(select yfUserId from YfUser where approve =true and  projectManagerYfId=? )", proId);
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		if(projectManageyf!=null &&(projectManageyf.getGradeStore()!=null && projectManageyf.getGradeStore()>0)) {
			//项目占比
			double proportion = getProportion(projectManageyf);
			projectManageyf.setProportion((proportion*100)+"%");
			//金额
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			Double totalMoney = pool.getTotalMoney(); //项目池总金额
			Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "+projectManageyf.getPoolId());//主项目总分数
			//获得主项目中主管申请的分数
			ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getRootId());
			if(null==totalCount ||null==yfProject.getGradeStore() ||null==totalMoney || null==projectManageyf.getGradeStore()){
				//throw new Exception("主项目没有设置分数，不能查看项目汇总");
				projectManageyf.setMoney(null);
			}else{
				if(null!=projectManageyf.getSumStore()){
					//总项目→金额
					double totalProportion =(double)yfProject.getGradeStore() * totalMoney/(double)totalCount;
					//本项目金额
					double money  = projectManageyf.getGradeStore()*totalProportion/projectManageyf.getSumStore();
		            BigDecimal bg = new BigDecimal(money);  
		            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
					projectManageyf.setMoney(f1);
				}
			}
		}
		return list;
	}

	//取消项目参与人
	@SuppressWarnings("unchecked")
	@Override
	public String cancelBindPlayers(YfUser yfUser) {
		if(null!=yfUser ){
			YfUser yfUser2 = (YfUser) totalDao.getObjectByCondition("from YfUser where yfUserId =? and projectManagerYfId=?", 
					yfUser.getYfUserId(),yfUser.getProjectManagerYfId());
			ProjectManageyfUser pmyfUser =(ProjectManageyfUser) totalDao.getObjectById(ProjectManageyfUser.class, yfUser.getYfUserId());
			ProjectManageyfEr er = (ProjectManageyfEr) totalDao.getObjectByCondition("from ProjectManageyfEr where" +
					" addUserId=? and projectId = ?", pmyfUser.getUserId(),yfUser2.getProjectManagerYfId());
			if(null!=er){
				totalDao.delete(er);
			}
			//yfUser2.setApprove(false);
			if( totalDao.delete(yfUser2)){
				//updateyfProPd(yfUser2.getProjectManagerYfId());
				List<ProjectManageYfpd> list =  totalDao.query("from ProjectManageYfpd where proId="+yfUser2.getProjectManagerYfId()+" and userId="+pmyfUser.getUserId());
				if(null!=list&& list.size()>0){
					for (ProjectManageYfpd pd : list) {
						if(null!=pd){
							totalDao.delete(pd);
						}
					}
				}
				totalDao.delete(pmyfUser);
				updateyfProPd(yfUser.getProjectManagerYfId());
				return "取消成功";
			}
			return "取消失败";
		}else{
			return "没有找到目标";
		}
	}

	//项目明细
	@Override
	public List<ProjectManageyf> getStoreResult()throws Exception {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return null;
		}
		String hql = "from ProjectManageyf where id in(select projectManagerYfId from YfUser where " +
				"approve=true and  yfUserId in(select id from ProjectManageyfUser where userId=?))";
		List<ProjectManageyf> list = totalDao.query(hql,loginUser.getId());
		if(null!=list && list.size()>0){
			for (ProjectManageyf projectManageyf : list) {
				ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
				Double totalMoney = pool.getTotalMoney(); //项目池总金额
				Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "+projectManageyf.getPoolId());//主项目总分数
				if(null==totalCount ||null==projectManageyf.getGradeStore() ||null==totalMoney ){
					throw new Exception("主项目下还有没设置分数的项目");
				}
				//总项目→金额
				double totalProportion =projectManageyf.getGradeStore() * totalMoney/totalCount;
				//本项目金额
				double proportion  = projectManageyf.getGradeStore()*totalProportion/projectManageyf.getSumStore();
				DecimalFormat    df   = new DecimalFormat("#########0.00");
				df.format(proportion);
				projectManageyf.setMoney(proportion);
			}
		}
		return list;
	}

	//个人研发项目汇总
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> selfYfProjectStore(String pageStatus) throws Exception{
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			throw new Exception("请先登录");
		}
		List<ProjectManageyf> list = null;
		if("applychoose".equals(pageStatus)){
			//个人的研发项目
			List<ProjectManageyfUser> userList = totalDao.query("from ProjectManageyfUser where userId=? and" +
					" usertype='参与人'", loginUser.getId());
			if(null!=userList &&userList.size()>0){
				StringBuffer yfuserStr =new StringBuffer(); 
				for (ProjectManageyfUser projectManageyfUser : userList) {
					yfuserStr.append(projectManageyfUser.getId()+",");
				}
				String str = yfuserStr.substring(0, yfuserStr.length()-1);
				ProjectManageyfUser projectManageyfUser = userList.get(0);
				String hql = "from ProjectManageyf where status='完成' and id in(select projectManagerYfId from YfUser where " +
				" yfUserId in("+str+")) order by id desc";
				list = totalDao.query(hql);
				if(null!=list && list.size()>0){
					for (ProjectManageyf projectManageyf : list) {
						//设定实际分数
						if(null==projectManageyf.getActualStore()){
							projectManageyf.setActualStore(projectManageyf.getGradeStore());
						}
						
						//工号
						projectManageyf.setUserCode(projectManageyfUser.getUserCode());
						//姓名
						projectManageyf.setUserName(projectManageyfUser.getUserName());
						//占比
						double proportion = (double)projectManageyf.getActualStore()/(double)projectManageyf.getSumStore()*100;
						NumberFormat nf = NumberFormat.getNumberInstance();
			            nf.setMaximumFractionDigits(2);
			            String format = nf.format(proportion);
						//dformat.format(proportion);
						projectManageyf.setProportion(format+"%");
						//proportion
						//金额
						ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
						projectManageyf.setProjectPool(pool);
						Double totalMoney = pool.getTotalMoney(); //项目池总金额
						Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "+projectManageyf.getPoolId());//主项目总分数
						//获得主项目中主管申请的分数
						ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectByCondition("from ProjectManageyf where id=? and gradeStatus='同意'", projectManageyf.getRootId());
						if(null==yfProject){
							break;
						}
						if(null==totalCount ||null==yfProject.getGradeStore() ||null==totalMoney || null==projectManageyf.getGradeStore()){
							//throw new Exception("主项目没有设置分数，不能查看项目汇总");
							projectManageyf.setMoney(null);
						}else{
							//总项目→金额
							double totalProportion =(double)yfProject.getGradeStore() * totalMoney/(double)totalCount;
							//本项目金额
							double money  = projectManageyf.getGradeStore()*totalProportion/projectManageyf.getSumStore();
//							NumberFormat nf1 = NumberFormat.getNumberInstance();
//				            nf1.setMaximumFractionDigits(2);
//				            String format1 = nf1.format(money);
				            BigDecimal bg = new BigDecimal(money);  
				            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
							projectManageyf.setMoney(f1);
						}
					}
				}
			}
			return list;
		}else{
			//项目主管，查看本项目中所有项目
			List<ProjectManageyf> rootProject = totalDao.query("from ProjectManageyf where id in(" +
					"select projectManagerYfId from YfUser where yfUserId in (select id from ProjectManageyfUser " +
					"where userId = ? and usertype ='项目主管')) and status='完成' order by id desc ", loginUser.getId());
			if(null!=rootProject && rootProject.size()>0){
				for (ProjectManageyf projectManageyf : rootProject) {
					//设定实际分数
					if(null==projectManageyf.getActualStore()){
						projectManageyf.setActualStore(projectManageyf.getGradeStore());
					}
					
					//项目池信息
					ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
					projectManageyf.setProjectPool(pool);
					//ProjectPool projectPool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getId());
					
					//工号
					//projectManageyf.setUserCode(projectManageyfUser.getUserCode());
					//姓名
					//projectManageyf.setUserName(projectManageyfUser.getUserName());
					//负责人
					projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId()));
					//占比
					double proportion = (double)projectManageyf.getActualStore()/(double)projectManageyf.getSumStore()*100;
					NumberFormat nf = NumberFormat.getNumberInstance();
		            nf.setMaximumFractionDigits(2);
		            String format = nf.format(proportion);
					//dformat.format(proportion);
					projectManageyf.setProportion(format+"%");
					//proportion
					//金额
					Double totalMoney = pool.getTotalMoney(); //项目池总金额
					Long totalCount = totalDao.count("select SUM(gradeStore) from ProjectManageyf WHERE rootId = id and  poolId = "
							+projectManageyf.getPoolId());//主项目总分数
					//获得主项目中主管申请的分数
					ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getRootId());
					if(null==totalCount ||null==yfProject.getGradeStore() ||null==totalMoney || null==projectManageyf.getActualStore()){
						//throw new Exception("主项目没有设置分数，不能查看项目汇总");
						projectManageyf.setMoney(null);
					}else{
						//总项目→金额
						double totalProportion =(double)yfProject.getGradeStore() * totalMoney/(double)totalCount;
						//本项目金额
						double money  = projectManageyf.getGradeStore()*totalProportion/projectManageyf.getSumStore();
//						NumberFormat nf1 = NumberFormat.getNumberInstance();
//			            nf1.setMaximumFractionDigits(2);
//			            String format1 = nf1.format(money);
			            BigDecimal bg = new BigDecimal(money);  
			            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
						projectManageyf.setMoney(f1);
					}
				}
			}
			return rootProject;
		}
	}
	
	//获得项目中的人数
	public Integer getProjectManageyfPerson(ProjectManageyf projectManageyf){
		Integer personCount=0;
		if(null!=projectManageyf){
			if(projectManageyf.getRootId()==projectManageyf.getId()){
				personCount = totalDao.getCount("select count(1) from ProjectManageyfUser where id in " +
						"(select yfUserId from YfUser where approve=true and projectManagerYfId in(select id from ProjectManageyf where rootId = "+projectManageyf.getRootId()+"))");
			}else{
				personCount = totalDao.getCount("select count(1) from ProjectManageyfUser where id in " +
						"(select yfUserId from YfUser where approve=true and projectManagerYfId = "+projectManageyf.getId()+"))");
			}
		}
		return personCount;
	}

	//查看项目明细
	@Override
	public ProjectManageyf getProStoreByProId(ProjectManageyf projectManageyf)throws Exception {
		//projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
		if(null!=projectManageyf && null!= projectManageyf.getId()){
			Integer personCount=0;
			if(projectManageyf.getRootId()==projectManageyf.getId()){
				personCount = totalDao.getCount("select count(1) from ProjectManageyfUser where id in " +
						"(select yfUserId from YfUser where approve=true and projectManagerYfId in(select id from ProjectManageyf where rootId = "+projectManageyf.getRootId()+"))");
			}else{
				personCount = totalDao.getCount("select count(1) from ProjectManageyfUser where id in " +
						"(select yfUserId from YfUser where approve=true and projectManagerYfId = "+projectManageyf.getId()+"))");
			}
			//项目中的人数
			projectManageyf.setPerson(personCount);
			//占比
			double proportion = getProportion(projectManageyf);
			BigDecimal bg = new BigDecimal(proportion*100);
			double doubleValue = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			projectManageyf.setProportion(doubleValue+"%");
			//ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			//已完成
			Integer complete = 0;
			//未完成
			Integer unComplete=0;
			//全部
			List<ProjectManageyf> subList = getProSubByRootId(projectManageyf.getRootId());
			projectManageyf.setSumCount(subList.size());
			
			
			for (ProjectManageyf subPro : subList) {
				if("完成".equals(subPro.getStatus())){
					complete++;
				}else{
					unComplete++;
				}
			}
			projectManageyf.setComplete(complete );
			projectManageyf.setUnComplete(unComplete);
			
			//金额
			projectManageyf.setMoney(getProMoneyByProId(projectManageyf));
		}
		return projectManageyf;
	}

	private String str =null;
	
	//根据fatherID查找所有子项目
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> getSubListById(Integer proId) {
		//List<ProjectManageyf> list = new ArrayList<ProjectManageyf>();
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		Integer rootId = projectManageyf.getRootId();
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = ?", rootId);// rootId<>id and 
		List<ProjectManageyf> parentNode = new ArrayList<ProjectManageyf>();
		parentNode.add(projectManageyf);
		List<ProjectManageyf> resultNode = new ArrayList<ProjectManageyf>();
		getChildNode(parentNode,list,resultNode);//List<ProjectManageyf> list2 = 
		if(null==resultNode || resultNode.size()==0){
			for (ProjectManageyf project : parentNode) {
				String principals = findPrincipalPmyfId(project.getId());
				project.setPrincipals(principals);
			}
			return parentNode;
		}
		for (ProjectManageyf project : resultNode) {
			String principals = findPrincipalPmyfId(project.getId());
			project.setPrincipals(principals);
		}
		return resultNode;
	}
	/** 
	 * 递归取得当前目标成本所有子子节点 
	 * @return 子节点集合 
	 */  
	public List<ProjectManageyf> getChildNode(List<ProjectManageyf> parentNode,List<ProjectManageyf> list,List<ProjectManageyf> resultNode) {  
	    if(parentNode==null || parentNode.size()<=0) return parentNode;  
	      
	    List<ProjectManageyf> result = new ArrayList<ProjectManageyf>();  
	    List<ProjectManageyf> remove = new ArrayList<ProjectManageyf>();
	    
	    //遍历传来的父节点集合  
	    for (ProjectManageyf key : parentNode) {  
	        //遍历数据集  
	        for (ProjectManageyf entity: list) {
	        	if(null==entity.getFatherId() && key.getId()==entity.getRootId()){
	        		result.add(entity);
	                resultNode.add(entity);  
	                remove.add(entity);
	        	}else if(key != null && null!=entity.getFatherId() && key.getId()==Integer.parseInt(entity.getFatherId())){  
	            	result.add(entity);  
	                resultNode.add(entity);
	                remove.add(entity);
	            }
	        }  
	        list.removeAll(remove);
	    }  
	    //递归调用  
	    getChildNode(result,list,resultNode);  
	    return result;  
	}  
	
	@SuppressWarnings("unchecked")
	private String getProByFatherId(String fatherId){
		String str = "";
		List<ProjectManageyf> list = totalDao.query(
				" from  ProjectManageyf where fatherId = ?", fatherId);
		if (list != null && list.size() > 0) {
			String append = null;
			for (ProjectManageyf pro : list) {
				append+=","+pro.getId();
				str+=getProByFatherId(pro.getFatherId());
			}
			return append;
		}else{
			return str;
		}
	}

	//根据项目id获取下层项目分数百分比
	@Override
	public String getProPercentnm(Integer proId,Integer store,String  tag) throws Exception{
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		if("add".equals(tag)){
			try {
				Long sumStore = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId()+
						" and belongLayer="+(projectManageyf.getBelongLayer()+1)+" and fatherId="+projectManageyf.getId());
				if(null==sumStore){
					sumStore=0l;
				}
				sumStore+=store;
				double zhanbi = (double)store/(double)sumStore;
				//BigDecimal bg = new BigDecimal(zhanbi);
				BigDecimal bg = new BigDecimal(zhanbi*100);
				//double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).intValue();
				return bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue()+"%";
			} catch (Exception e) {
				throw new Exception("该项目没有设定项目分值："+e.toString());
			}
		}else{
			try {
				Long sumStore = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId()+
						" and belongLayer="+(projectManageyf.getBelongLayer())+" and fatherId="+projectManageyf.getFatherId());
				sumStore-=projectManageyf.getGradeStore();
				sumStore+=store;
				double zhanbi = (double)store/(double)sumStore;
				BigDecimal bg = new BigDecimal(zhanbi*100);
				//double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).intValue();
				return bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue()+"%";
			} catch (Exception e) {
				throw new Exception("该项目没有设定项目分值："+e.toString());
			}
		}
		
	}

	/**
	 * 根据项目id获取项目金额0.00
	 */
	@Override
	public Double getProMoneyByProId(ProjectManageyf projectManageyf){
		if(null==projectManageyf){
			return 0.0;
		}
		if(null==projectManageyf.getGradeStore() && null!=projectManageyf.getId()){
			projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
		}
		ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
		//金额
		Double totalMoney = pool.getTotalMoney(); //项目池总金额
		if(totalMoney!=null) {
			//本项目占比
			double proportion = getProportion(projectManageyf);
			
			double money = proportion* totalMoney;
			
			BigDecimal bgc = new BigDecimal(money);
			double doubleValue = bgc.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
			return doubleValue;
			
		}else {
			return 0.0;
		}
	}

	//根据项目id查找项目人员明细
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageYfpd> getProyfpdByProId(Integer proId) {
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		List<ProjectManageYfpd> list = null;
		if(projectManageyf.getRootId()==projectManageyf.getId()){
			List<ProjectManageyf> allList = totalDao.query("from ProjectManageyf where rootId = "+ projectManageyf.getRootId());// rootId<>id and
			list = new ArrayList<ProjectManageYfpd>();
			for (ProjectManageyf yfproject : allList) {
				List<ProjectManageYfpd> pdList  = totalDao.query("from ProjectManageYfpd where proportion is not null and proId = ?", yfproject.getId());
				for (ProjectManageYfpd projectManageYfpd : pdList) {
					list.add(projectManageYfpd);
				}
			}
		}else{
			list = totalDao.query("from ProjectManageYfpd where proId = ?", proId);
		}
		return list;
	}
	
	//项目池占比
	@SuppressWarnings("unchecked")
	public double getProportion(ProjectManageyf projectManageyf){
		if(null!=projectManageyf ){
			if(null==projectManageyf.getGradeStore()){
				projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
			}
			
			double zhanbi =0.0;
			while(true){
				if(null==projectManageyf){
					break;
				}
				if( null==projectManageyf.getFatherId()){//主项目
					ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
					projectManageyf.setProjectPool(pool);
					//项目池下所有主项目总分
					List<ProjectManageyf> rootList = totalDao.query("from ProjectManageyf where rootId = id and  poolId = "+projectManageyf.getPoolId());
					double sumRootStore = 0.0;
					for (ProjectManageyf rootPro : rootList) {
						if(null!=rootPro && null!=rootPro.getActualStore()){ //实际分数
							sumRootStore+=rootPro.getActualStore();
						}else if(null!=rootPro && null!=rootPro.getGradeStore()){
							sumRootStore+=rootPro.getGradeStore();
						}
					}
					//Long  l = totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = id and  poolId = "+pool.getId());
//					//项目池占比
					if(sumRootStore>0) {
						if(null!=projectManageyf.getActualStore()){
							if(zhanbi==0){
								zhanbi = (double)projectManageyf.getActualStore()/sumRootStore;
							}else{
								zhanbi = zhanbi*(double)projectManageyf.getActualStore()/sumRootStore;
							}
						}else if(null!= projectManageyf.getGradeStore()){
							if(zhanbi==0){
								zhanbi = (double)projectManageyf.getGradeStore()/sumRootStore;
							}else{
								zhanbi = zhanbi*(double)projectManageyf.getGradeStore()/sumRootStore;
							}
						}
					}
					break;
				}
				
				//子项目中的所有父项目  获取所有的本层项目
				List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId="+projectManageyf.getRootId()
						+" and belongLayer="+projectManageyf.getBelongLayer()+" and fatherId="+projectManageyf.getFatherId());
				double sumFatherStore = 0.0;
				for (ProjectManageyf fatherPro : list) {
					if(null!= fatherPro && null!= fatherPro.getActualStore()){ //实际分数
						sumFatherStore +=fatherPro.getActualStore();
					}else if(null!= fatherPro && null!= fatherPro.getGradeStore()){
						sumFatherStore += fatherPro.getGradeStore();
					}
				}
				//除数为0过滤
				if(sumFatherStore>=0){
					//Long fl =totalDao.count("select sum(gradeStore) from ProjectManageyf where rootId = "+projectManageyf.getRootId()+" and belongLayer ="+projectManageyf.getBelongLayer());
					if(null!=projectManageyf.getActualStore()){
						if(zhanbi==0){
							zhanbi = (double)projectManageyf.getActualStore()/sumFatherStore;
						}else{
							zhanbi = zhanbi*(double)projectManageyf.getActualStore()/sumFatherStore;
						}
					}else if(null!= projectManageyf.getGradeStore()){
						if(zhanbi==0){
							zhanbi = (double)projectManageyf.getGradeStore()/sumFatherStore;
						}else{
							zhanbi = zhanbi*(double)projectManageyf.getGradeStore()/sumFatherStore;
						}
					}
				}
				
				//父项目
				projectManageyf =(ProjectManageyf) totalDao.getObjectByCondition("from ProjectManageyf where id = "+projectManageyf.getFatherId());
			}
			BigDecimal bg = new BigDecimal(zhanbi);
            double f1 = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();  
			return f1;
		}else{
			return 0.0;
		}
	}

	//总项目汇总
	@Override
	public List<ProjectManageyf> projectManageResult() {
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId=id order by id desc");
		for (ProjectManageyf projectManageyf : list) {
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(pool);
			if(projectManageyf.getGradeStore()!=null){
				double proportion = getProportion(projectManageyf);
				BigDecimal bg = new BigDecimal(proportion*100);
				int intValue = bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
				projectManageyf.setProportion(intValue+"%");
				
				projectManageyf.setMoney(getProMoneyByProId(projectManageyf));
			}
			//负责人
			String principals = findPrincipalPmyfId(projectManageyf.getId());
			projectManageyf.setPrincipals(principals);
			
			//已完成
			Integer complete = 0;
			//未完成
			Integer unComplete=0;
			//全部
			List<ProjectManageyf> subList = getProSubByRootId(projectManageyf.getRootId());
			projectManageyf.setSumCount(subList.size());
			
			
			for (ProjectManageyf subPro : subList) {
				if("完成".equals(subPro.getStatus())){
					complete++;
				}else{
					unComplete++;
				}
			}
			projectManageyf.setComplete(complete );
			projectManageyf.setUnComplete(unComplete);
		}
		return list;
	}

	//所有叶子节点信息
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> selectSubPro(Integer id) {
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where rootId = "+id+" and rootId<>id and id not in" +// and fatherId is not null
		"(select fatherId from ProjectManageyf where rootId ="+id+" and fatherId is not null)");
		//"from ProjectManageyf where rootId ="+rootId+" and fatherId is not null and id not in" +
		//"(select fatherId from ProjectManageyf where rootId ="+rootId+" and fatherId is not null)";
		for (ProjectManageyf projectManageyf : list) {
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(pool);
			//项目池金额
			Double totalMoney = pool.getTotalMoney();
			if(totalMoney!=null && projectManageyf.getGradeStore()!=null) {
				
				//占比
				double proportion = getProportion(projectManageyf);
				BigDecimal bg = new BigDecimal(proportion*100);
				double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setProportion(doubleValue+"%");
				
				//金额
				//Double money = totalMoney*proportion;
				BigDecimal bg1 = new BigDecimal(totalMoney*proportion);
				double money = bg1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setMoney(money);
				//负责人
				String principals = findPrincipalPmyfId(projectManageyf.getId());
				projectManageyf.setPrincipals(principals);
			}
			
			Integer personCount = totalDao.getCount("from ProjectManageyfUser where id in " +
					"(select yfUserId from YfUser where approve=true and projectManagerYfId ="+projectManageyf.getId()+")");
			//项目中的人数
			projectManageyf.setPerson(personCount);
			
		}
		
		return list;
	}

	/**
	 * 根据主项目id查找所有的叶子节点项目
	 * @return
	 */
	public List<ProjectManageyf> getProSubByRootId(Integer rootId){
		
		String hql = "from ProjectManageyf where rootId ="+rootId+" and id not in" +
				"(select fatherId from ProjectManageyf where rootId ="+rootId+" and fatherId is not null)";
		List<ProjectManageyf> list = totalDao.query(hql);
		return list;
	}
	
	/**
	 * 定时消息，发送研发项目完成信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sendProjectManageYfInfo(){
		List<ProjectManageyf> list = totalDao.query("from ProjectManageyf where status<>'完成' ");
		for (ProjectManageyf projectManageyf : list) {
			//String startTimeStr = projectManageyf.getStartTime();
			String reTimeStr = projectManageyf.getReTime();
			
			SimpleDateFormat startFormat = new SimpleDateFormat();
			try {
				//Date startDate = startFormat.parse(startTimeStr);
				Date reDate = startFormat.parse(reTimeStr);
				//long date = reDate.getTime()/(60*60*24) - startDate.getTime()/(60*60*24);
				Date currentDate = new Date();
				//long current  = currentDate.getTime()/(60*60*24);
				BigDecimal bg = new BigDecimal(reDate.getTime()/(60*60*24)*0.8);
				int intStartValue = bg.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				bg = new BigDecimal( currentDate.getTime()/(60*60*24));
				
				int reTime = bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
				if(intStartValue == reTime ){
//					AlertMessagesServerImpl.addAlertMessages("研发项目:"+projectManageyf.getProName(), 
//							projectManageyf.getProName()+"，已经到达完成时间的80%，请尽快：" , new Integer[]{projectManageyfUser.getUserId()},
//							"projectPoolAction_toProjectGrade.action?tag=affirmGrade&id="+yfProject.getId(), true);
					//AlertMessagesServerImpl.addAlertMessages(functionName, content, messageType, code)
					List<ProjectManageYfpd> pdList = totalDao.query("from ProjectManageYfpd where proId="+projectManageyf.getId());
					if(null!=pdList && pdList.size()>0){
						for (ProjectManageYfpd projectManageYfpd : pdList) {
							AlertMessagesServerImpl.addAlertMessages("研发项目消息提醒", "研发项目："+projectManageyf.getProName()
									+",已经到达预完成时间的80%,请尽快完成！", "研发项目消息提醒", projectManageYfpd.getUserId()+"");
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

	//提交延期
	@SuppressWarnings("unchecked")
	@Override
	public String submitForPostpone(ProjectManageyf projectManageyf,String pageStatus) {
		if(null!=projectManageyf && null!=projectManageyf.getId()){
			ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
			if(null!=yfProject){
				yfProject.setOutTime(projectManageyf.getOutTime());
				yfProject.setOutExplain(projectManageyf.getOutExplain());
				//yfProject.setOutTimeResult(false);
				if(yfProject.getRootId()==yfProject.getId() && (null==pageStatus  || "".equals(pageStatus))){
					//主项目。。项目主管申请
					ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, yfProject.getPoolId());
					AlertMessagesServerImpl.addAlertMessages("研发项目申请延期", "研发项目："+yfProject.getProName()+"申请延期，请您处理.", new Integer[]{pool.getAddUserId()},
							"projectPoolAction_examineForPostone.action?id="+yfProject.getId(), true);
				}else{
					//子项目
					List<ProjectManageyfUser> list = totalDao.query("from ProjectManageyfUser where id in(select yfUserId from YfUser where projectManagerYfId ="+yfProject.getRootId()+")");
					for (ProjectManageyfUser projectManageyfUser : list) {
						AlertMessagesServerImpl.addAlertMessages("研发项目申请延期", "研发项目："+yfProject.getProName()+"申请延期，请您处理.", new Integer[]{projectManageyfUser.getUserId()},
								"projectPoolAction_examineForPostone.action?id="+yfProject.getId(), true);
					}
				}
				totalDao.update(yfProject);
				return "提交成功";
			}
		}
		
		
		return null;
	}

	//审批延期申请
	@Override
	public String examineForPostone(Integer proId, String status) {
		Users loginUser = Util.getLoginUser();
		if(null==loginUser){
			return "请先登录";
		}
		ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, proId);
		if(null!=projectManageyf ){
			projectManageyf.setOutTimeResult(status);
			projectManageyf.setOutTimeexplainPerson(loginUser.getName());
			if("同意".equals(status)){
				//审批同意后延期成功。
			}
			totalDao.update(projectManageyf);
			return "审批成功";
		}
		return null;
	}

	//主项目提交审批文件
	@Override
	public String finalSubmitFile(ProjectManageyf projectManageyf,List<File> attachments, List<String> attachmentNames) {
		//String fileNames = "";
		StringBuffer buffer = new StringBuffer();
		ProjectManageyf yfProject = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, projectManageyf.getId());
		if(null!=attachmentNames && attachmentNames.size()>0){
			for (int i = 0; i < attachmentNames.size(); i++) {
				if(null!=attachments){
					String fileName= Util.getDateTime("yyyyMMddHHmmss")+(attachmentNames.get(i).substring(attachmentNames.get(i).lastIndexOf(".")));
					//上传到服务器
					String fileRealPath = ServletActionContext .getServletContext().getRealPath("/upload/file")+ "\\" + fileName;
					File saveFile = new File(fileRealPath);
					try {
						FileCopyUtils.copy(attachments.get(i), saveFile);
					} catch (Exception e) {
						return "文件出错!";
					}

					// 备份到项目
					String beiFenfileRealPath = ServletActionContext.getServletContext().getRealPath("/upload/file/"+fileName);
					File beiFenFile = new File(beiFenfileRealPath);
					try {
						FileCopyUtils.copy(attachments.get(i), beiFenFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffer.append(fileName+",");
				}
			}
			String projectFile  = buffer.substring(0,buffer.length()-1);
			yfProject.setYfProjectFile(projectFile);
		}
		yfProject.setSchedule(projectManageyf.getSchedule());
		yfProject.setStatus("待确认");
		boolean update = totalDao.update(yfProject);
		if(update){
			ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, yfProject.getPoolId());
			AlertMessagesServerImpl.addAlertMessages("研发项目提交", "研发主项目："+yfProject.getProName()+",完成已经提交，请您审批。",new Integer[]{pool.getAddUserId()},
					"projectPoolAction_toExamineSubProject.action?id="+projectManageyf.getId(), true);
		}
		
		
		return "提交成功";
	}

	//按月份汇总人员信息
	@Override
	public List<ProjectManageYfpd> summarizingMonth() {
		//查找最小的年份
		String date = (String) totalDao.getObjectByCondition("select min(startTime) from ProjectManageyf ");
		if(null!=date && date.length()>3){
			int year = Integer.parseInt(date.substring(0, 3));
			int currentYear = Integer.parseInt(Util.getDateTime("yyyy"));
			
			for (int i = year; i <=currentYear; i++) {
				List<ProjectManageYfpd> list = totalDao.query("from ProjectManageYfPd where startTime like '"+i+"%' ");
				
			}
		}
		return null;
	}
	
	//按项目池汇总人员信息
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageYfpd> summarizingPool() {
		//获得所有项目池编号
		List<Integer> list = totalDao.query("select id from ProjectPool ");
		List<ProjectManageYfpd> pdList = new ArrayList<ProjectManageYfpd>();
		//double money=0.0;
		for (Integer integer : list) {
			
			//项目池中的人员
			List userList = totalDao.query("select min(userId) from ProjectManageYfpd where  proId in (select id from ProjectManageyf where poolId ="+integer+") and proportion is not null GROUP BY userId");
			
			for (Object userId : userList) {
				//按用户查找金额
				Double money = (Double) totalDao.getObjectByCondition("SELECT sum(money) from ProjectManageYfpd where userId = "+userId+" and id in(select id from ProjectManageYfpd where " +
						" proId in (select id from ProjectManageyf where poolId ="+integer+") and proportion is not null)");
				//查找用户名
				List<ProjectManageYfpd> list2 = totalDao.query("from ProjectManageYfpd where userId="+userId);
				if(null!=list2 && list2.size()>0){
					ProjectManageYfpd projectManageYfpd = list2.get(0);
					ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, integer);
					
					ProjectManageYfpd pd = new ProjectManageYfpd();
					pd.setUserId((Integer) userId);
					pd.setUserCode(projectManageYfpd.getUserCode());
					pd.setUserName(projectManageYfpd.getUserName());
					if(null!=money){
						BigDecimal bg = new BigDecimal(money);
						double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
						pd.setMoney(doubleValue);
					}
					pd.setPoolId(pool.getId());
					pd.setPoolNum(pool.getPoolNumber());
					pd.setPoolName(pool.getPoolName());
					pdList.add(pd);
					
				}
			}
		}
		return pdList;
	}

	//按主项目汇总人员信息
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageYfpd> summarizingRootPro() {
		List<Integer> list = totalDao.query("select rootId from ProjectManageyf where rootId = id");
		List<ProjectManageYfpd> pdList = new ArrayList<ProjectManageYfpd>();
		
		for (Integer rootId : list) {
			//所有用户
			List<Integer> userList = totalDao.query("select min(userId) from ProjectManageYfpd where  proId in (select id from ProjectManageyf where rootId="+rootId+") and proportion is not null GROUP BY userId");
			for (Integer integer : userList) {
				Double money = (Double) totalDao.getObjectByCondition("SELECT sum(money) from ProjectManageYfpd where userId = "+integer+" and id in(select id from ProjectManageYfpd where  proId in " +
						"(select id from ProjectManageyf where rootId ="+rootId+") and proportion is not null)");
				//查找用户名
				List<ProjectManageYfpd> list2 = totalDao.query("from ProjectManageYfpd where userId="+integer);
				if(null!=list2 && list2.size()>0){
					ProjectManageYfpd projectManageYfpd = list2.get(0);
					//ProjectPool pool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, integer);
					ProjectManageyf projectManageyf = (ProjectManageyf) totalDao.getObjectByCondition("from ProjectManageyf where rootId = id and rootId="+rootId);
					ProjectManageYfpd pd = new ProjectManageYfpd();
					pd.setUserId((Integer) integer);
					pd.setUserCode(projectManageYfpd.getUserCode());
					pd.setUserName(projectManageYfpd.getUserName());
					if(null!=money){
						BigDecimal bg = new BigDecimal(money);
						double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
						pd.setMoney(doubleValue);
					}
					pd.setProId(projectManageyf.getId());
					pd.setProNum(projectManageyf.getProNum());
					pd.setProName(projectManageyf.getProName());
					pdList.add(pd);
					
				}
			}
		}
		return pdList;
	}

	//按年份汇总人员信息
	@Override
	public List<ProjectManageYfpd> summarizingYear() {
		
		return null;
	}

	//按人员汇总
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageYfpd> personnelSummary() {
		List<ProjectManageYfpd> pdList = new ArrayList<ProjectManageYfpd>();
		List list = totalDao.query("select min(userId) from ProjectManageYfpd where id in (select id from ProjectManageYfpd where money is not null) GROUP BY userId");
		ProjectManageYfpd pd=null;
		for (Object str : list) {
			Double money = (Double) totalDao.getObjectByCondition("SELECT SUM(money) from ProjectManageYfpd where userId = "+str+" GROUP BY userId");
			pd = new ProjectManageYfpd();
			List<ProjectManageYfpd> list2 = totalDao.query("from ProjectManageYfpd where userId = "+str);
			if(null!=list2 && list2.size()>0){
				ProjectManageYfpd projectManageYfpd = list2.get(0);
				if(money!=null) {
					BigDecimal bg = new BigDecimal(money);
					double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					pd.setMoney(doubleValue);
				}
				pd.setUserId(projectManageYfpd.getUserId());
				pd.setUserCode(projectManageYfpd.getUserCode());
				pd.setUserName(projectManageYfpd.getUserName());
				pdList.add(pd);
			}
			//pd.setUserId(object.)
		}
		return pdList;
	}

	
	//查询user所有的项目
	@Override
	public List<ProjectManageyf> showSelfProject(Integer userId) {
		String hql ="from ProjectManageyf where id in(select projectManagerYfId from YfUser where " +
				"approve=true and yfUserId in(select id from ProjectManageyfUser where userId=?))";
		List<ProjectManageyf> list = totalDao.query(hql, userId);
		for (ProjectManageyf projectManageyf : list) {
			ProjectPool projectPool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(projectPool);//项目池信息
			Integer person = getProjectManageyfPerson(projectManageyf);
			projectManageyf.setPerson(person);
			ProjectManageYfpd projectManageYfpd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where proId=? and userId=?", projectManageyf.getId(),userId);
			
			projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId()));
			if(projectManageyf.getGradeStore()!=null) {
				//占比
				double proportion = getProportion(projectManageyf);
				BigDecimal bg = new BigDecimal(proportion*100);
				double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setProportion(doubleValue+"%");
				
				//金额
				Double totalMoney = projectPool.getTotalMoney();
				BigDecimal bg1 = new BigDecimal(totalMoney*proportion);
				double money = bg1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setMoney(money);
				if(null!=projectManageYfpd && null!=projectManageYfpd.getMoney()){
					bg = new BigDecimal(projectManageYfpd.getMoney());
					doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					projectManageyf.setSelfMoney(doubleValue);
				}
			}
			
			
		}
		return list;
	}

	//查询user在项目池下的所有项目
	@Override
	public List<ProjectManageyf> showSelfProjectAndPool(Integer userId,
			Integer poolId) {
		String hql ="from ProjectManageyf where poolId=? and id in(select projectManagerYfId from YfUser where " +
				"approve=true and yfUserId in(select id from ProjectManageyfUser where userId=?))";
		List<ProjectManageyf> list = totalDao.query(hql,poolId, userId);
		for (ProjectManageyf projectManageyf : list) {
			projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId()));
			ProjectPool projectPool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(projectPool);//项目池信息
			
			Integer person = getProjectManageyfPerson(projectManageyf);
			projectManageyf.setPerson(person);
			ProjectManageYfpd projectManageYfpd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where proId=? and userId=?", projectManageyf.getId(),userId);
			if(projectManageyf.getGradeStore()!=null) {
				//占比
				double proportion = getProportion(projectManageyf);
				BigDecimal bg = new BigDecimal(proportion*100);
				double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setProportion(doubleValue+"%");
				
				//金额
				Double totalMoney = projectPool.getTotalMoney();
				BigDecimal bg1 = new BigDecimal(totalMoney*proportion);
				double money = bg1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setMoney(money);
				if(null!=projectManageYfpd && null!=projectManageYfpd.getMoney()){
					bg = new BigDecimal(projectManageYfpd.getMoney());
					doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					projectManageyf.setSelfMoney(doubleValue);
				}
			}
			
			
		}
		return list;
	}
	//查询user在主项目下的所有项目
	@Override
	public List<ProjectManageyf> showSelfProjectByRoot(Integer userId,
			Integer proId) {
		String hql ="from ProjectManageyf where rootId=(select rootId from ProjectManageyf where id =?) and id in(select projectManagerYfId from YfUser where " +
				"approve=true and yfUserId in(select id from ProjectManageyfUser where userId=?))";
		List<ProjectManageyf> list = totalDao.query(hql, proId,userId);
		for (ProjectManageyf projectManageyf : list) {
			projectManageyf.setPrincipals(findPrincipalPmyfId(projectManageyf.getId()));
			ProjectPool projectPool = (ProjectPool) totalDao.getObjectById(ProjectPool.class, projectManageyf.getPoolId());
			projectManageyf.setProjectPool(projectPool);//项目池信息
			Integer person = getProjectManageyfPerson(projectManageyf);
			projectManageyf.setPerson(person);
			ProjectManageYfpd projectManageYfpd = (ProjectManageYfpd) totalDao.getObjectByCondition("from ProjectManageYfpd where proId=? and userId=?", projectManageyf.getId(),userId);
			if(projectManageyf.getGradeStore()!=null) {
				
				//占比
				double proportion = getProportion(projectManageyf);
				BigDecimal bg = new BigDecimal(proportion*100);
				double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setProportion(doubleValue+"%");
				
				//金额
				Double totalMoney = projectPool.getTotalMoney();
				BigDecimal bg1 = new BigDecimal(totalMoney*proportion);
				double money = bg1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				projectManageyf.setMoney(money);
				if(null!=projectManageYfpd && null!=projectManageYfpd.getMoney()){
					bg = new BigDecimal(projectManageYfpd.getMoney());
					doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					projectManageyf.setSelfMoney(doubleValue);
				}
			}
			
		}
		return list;
	}

	//根据ids删除其他主项目
	@SuppressWarnings("unchecked")
	@Override
	public String delProjectByIds(String ids)throws Exception {
		String projectId = ids.substring(0,ids.indexOf(","));
		Integer userId = Util.getLoginUser().getId();
		Integer rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProjectManageyf where id="+projectId);
		List<ProjectManageyf> list= totalDao.query("from ProjectManageyf where id not in("+ids+") and rootId =? " +
				"and id in (select id from ProjectManageyf where rootId in(select projectManagerYfId from YfUser"
				+ " where yfUserId in( select id from ProjectManageyfUser where userId=?)))",rootId,userId);
		if(list!=null && list.size()>0){
			for (ProjectManageyf projectManageyf : list) {
				totalDao.delete(projectManageyf);
			}
		}
		return "true";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyfEr> findUnErList(Integer proId, String pageStatus) {
		List<ProjectManageyfEr> list=null;
		if(proId!=null) {
			if(pageStatus!=null && pageStatus.equals("shenpi")) {
				return totalDao.query("from ProjectManageyfEr where projectId=? and status='预申请' ", proId);
			}else if(pageStatus!=null && pageStatus.equals("bind")) {
				return totalDao.query("from ProjectManageyfEr where projectId=? and status is null ", proId);
			}
			//根据项目Id查找主项目id
			Integer rootId = (Integer) totalDao.getObjectByCondition("select rootId from ProjectManageyf where id = ?", proId);
			list = totalDao.query("from ProjectManageyfEr where projectId=? and addUserId not in"
					+ " (select userId from ProjectManageyfUser where id in(select yfUserId from YfUser where approve=true and"
					+ " projectManagerYfId = ?))",rootId,proId );
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String zhipaiPlayers(Integer id, Integer userId,Integer weight, String pageStatus) {
		Users loginUser = Util.getLoginUser();
		if(id!=null && userId!=null) {
			Users users = (Users) totalDao.getObjectById(Users.class, userId);
			if(users==null) {
				return "用户不存在";
			}
			ProjectManageyf project = (ProjectManageyf) totalDao.getObjectById(ProjectManageyf.class, id);
			if(project==null) {
				return "项目不存在";
			}
			Integer count = (Integer) totalDao.getObjectByCondition("select id from ProjectManageyfUser"
					+ " where userId = ? and id in (select yfUserId "
					+ "	from YfUser where projectManagerYfId = ?)", userId,id);
			YfUser yfUser =null;
			if(count!=null && count>0) {
				yfUser = (YfUser) totalDao.getObjectByCondition(
						"from YfUser where yfUserId=? and projectManagerYfId=?", count,id);
			}
			if(yfUser!=null) {
				yfUser.setApprove(true);
				boolean update = totalDao.update(yfUser);
				if(update) {
					return "指派成功";
							
				}
			}else {
				ProjectManageyfUser projectManageyfUser = new ProjectManageyfUser();
				
				projectManageyfUser.setPoolId(project.getPoolId());
				projectManageyfUser.setRootId(project.getRootId());
				projectManageyfUser.setUserCode(users.getCode());
				projectManageyfUser.setUserDept(users.getDept());
				projectManageyfUser.setUserId(users.getId());
				projectManageyfUser.setUserName(users.getName());
				projectManageyfUser.setUsertype("参与人");
				projectManageyfUser.setWeight(weight);
				projectManageyfUser.setZpTime(Util.getDateTime("yyyy-MM-dd"));
				boolean save = totalDao.save(projectManageyfUser);
				if(save) {
					yfUser = new YfUser();
					yfUser.setProjectManagerYfId(id);
					yfUser.setYfUserId(projectManageyfUser.getId());
					yfUser.setApprove(true);
					boolean save2 = totalDao.save(yfUser);
					if(save2) {
						List<ProjectManageYfpd> listPd = totalDao.query("from ProjectManageYfpd where userId="+projectManageyfUser.getUserId()
						+" and proId = "+project.getId());
						if(null==listPd || listPd.size()==0){
							//添加人员明细信息
							ProjectManageYfpd pd = new ProjectManageYfpd();
							pd.setProId(project.getId());
							pd.setUserId(projectManageyfUser.getUserId());
							pd.setUserCode(project.getUserCode());
							pd.setUserName(projectManageyfUser.getUserName());
							pd.setStartTime(Util.getDateTime("yyyy-MM-dd"));
							if(project.getGradeStore()!=null){
								double proportion = getProportion(project);
								BigDecimal bg= new BigDecimal(proportion*100);
								int doubleValue = bg.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
								pd.setProportion(doubleValue +"%"); //占比
								BigDecimal bigDecimal = new BigDecimal(getProMoneyByProId(project));
								double money = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
								pd.setMoney(money); //金额
								pd.setWeight(weight);//权重
							}
							totalDao.save(pd);
							//更新其他人员明细信息
							if(project.getGradeStore()!=null){
								updateyfProPd(pd.getProId());
							}
						}else {
							ProjectManageYfpd pd = listPd.get(0);
							pd.setWeight(weight);
							totalDao.update(pd);
						}
						
						if("待填报".equals(project.getStatus())){
							project.setStatus("待完善");
							if(null==project.getZpTime() || "".equals(project.getZpTime())){
								project.setZpTime(Util.getDateTime("yyyy-MM-dd"));
							}
							totalDao.update(project);
						}
						AlertMessagesServerImpl.addAlertMessages("研发项目指派", loginUser.getName()+
								"，指派你参与:"+project.getProName()+"的研发，请查收!", new Integer[]{projectManageyfUser.getUserId()},
								"projectPoolAction_selfProjectManageyfList.action?pageStatus=applychoose", true);
						return "指派成功";
					}else {
						return "保存出错";
					}
				}else {
					return "保存失败";
				}
			}
		}
		return "参数错误";
	}

	@Override
	public ProjectManageyfEr getProjectManageyfErById(Integer id, String pageStatus) {
		ProjectManageyfEr er = (ProjectManageyfEr) totalDao.getObjectById(ProjectManageyfEr.class, id);
		
		return er;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> searchProFile(ProjectManageyf pro, Integer pageSize, Integer pageNo, String pageStatus) {
		Users loginUser = Util.getLoginUser();
		if(loginUser==null) {
			throw new RuntimeException("请先登录");
		}
		
		if (pro==null) {
			pro = new ProjectManageyf();
		}
		
		String hql = totalDao.criteriaQueries(pro, " yfProjectFile is not null order by id desc");
		Integer count = totalDao.getCount(hql);
		List<ProjectManageyf> list = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("count", count);
		return map;
	}

	@Override
	public String xqBind(int id, List<ProjectManageyfEr> erList, String ids) {
		
		return "绑定成功";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectManageyf> findselfProjectmanageYfByDateDiff(Integer userId, String startTime, String endTime,
			String pageStatus) {
		if(userId==null) {
			Users user =Util.getLoginUser();
			if(user==null){
				return null;
			}
			userId=user.getId();
			
		}
		//查找项目参与人
		String applychoose = "";
//		if("applychoose".equals(pageStatus)){
//			applychoose = " and usertype='参与人'";
//		}else{
//			applychoose = " and usertype='项目主管'";
//		}
		
		String hql = " from ProjectManageyf where id in (select projectManagerYfId from YfUser where approve=true and yfUserId in " +
				" (select id from ProjectManageyfUser where userId = ? "+applychoose+" ))";
				
		if(startTime!=null && endTime!=null) {
			hql+=" and ("
					+ "(zpTime<='"+startTime+"' and reTime>='"+endTime+"')"
					+ " or (zpTime>='"+startTime+"' and reTime<='"+endTime+"')"
					+ " or (zpTime<='"+startTime+"' and reTime>='"+startTime+"' and reTime<='"+endTime+"')"
					+ " or (endDate>='"+endTime+"' and reTime<='"+endTime+"' and reTime>='"+startTime+"') "
					+ ")"; 
		}
		List<ProjectManageyf> list=null;
		try {
			list = totalDao.query(hql, userId);
			String principal = "";//负责人
			for (ProjectManageyf projectyf : list) {
				projectyf.setProjectPool(null);
				principal = findPrincipalPmyfId(projectyf.getId());
				projectyf.setPrincipals(principal);
				if("applychoose".equals(pageStatus)){
					List<ProjectManageyfEr> erList = totalDao.query("from ProjectManageyfEr where projectId = ? and addUserId =?",
							projectyf.getId(),userId);
					if(null!=erList && erList.size()>0){
						projectyf.setChooseStatus(erList.get(0).getStatus());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
}
