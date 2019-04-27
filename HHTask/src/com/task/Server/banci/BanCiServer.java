package com.task.Server.banci;


import java.util.List;

import com.task.entity.Users;
import com.task.entity.banci.BanCi;
import com.task.entity.banci.BanCiTime;
import com.task.entity.banci.SchedulingTable;

public interface BanCiServer {
    /**
     * 查询所有的班次
     * @param banCi
     * @param parseInt 页数
     * @param pageSize 每页个数
     * @return
     */
	public Object[] findbanCi(BanCi banCi, int parseInt, int pageSize);
    /**
     * 添加班次
     * @param banCi
     * @return
     */
	public String addbanCi(BanCi banCi, List<BanCiTime> banCiTime);
	/**
	 * 通过id来查询班次
	 * @param banCi
	 * @return
	 */

	public BanCi salBanCiByid(Integer i);
	/**
	 * 修改班次
	 * @param banCi
	 * @return
	 */

	public String updateBanCi(BanCi banCi, List<BanCiTime> banCiTime);
    /**
     * 删除班次
     * @param banCi
     * @return
     */
	public boolean delBanCi(BanCi banCi);
	
	/**
	 * @author Li_Cong
	 * 给用户绑定班次
	 * @param usersId 用户list
	 * @param Integer 班次
	 * @return
	 */
	public String bangDingBanci(Integer[] usersId, Integer integer); // 给用户绑定班次
	/**
	 * @author Li_Cong
	 * 给用户解除绑定班次
	 * @param usersId 用户list
	 * @param Integer 班次
	 * @return
	 */
	public String jieBangBanci(Integer[] usersId, Integer integer); // 
	
	/**
	 * 条件查询所有未绑定班次的用户 分页查询
	 * @param id 班次id
	 * @param parseInt 页数
	 * @param pageSize 条数
	 * @return
	 */
	public Object[] findAllTagw(Users users, int parseInt, int pageSize, String tag);
	/**
	 * 查询所有与该班次已绑定的用户 分页查询
	 * @param id 班次id
	 * @param parseInt 页数
	 * @param pageSize 条数
	 * @return
	 */
	public Object[] findAllTagw(Integer id, Users users, int parseInt, int pageSize, String tag);

	/**
	 * 查询该一体机所有已绑定班次的用户
	 * @param id 班次id
	 * @return
	 */
	public List<Users> findAllBangBc(Integer id);
	
	
	public void shengchengimagenum();
	
	/**
	 * 班次批量调整功能
	 * @param banci1 班次1
	 * @param banci2 班次2
	 * @param i ：1：将班次调换  2 ：将1班次批量绑定给班次2
	 * @return
	 */
	public String changeBanCi(String banci1, String banci2, String i);
	
	
	/**
	 * 根据班次Id、开始时间、结束时间查询排班表
	 */
	List<SchedulingTable> fandSchedulIngByDate(Integer banCiId,String startTime,String endTime);
	
	/**
	 * 根据第一个员工id的排班、开始时间、结束时间查询排班表
	 */
	List<SchedulingTable> fandSchedulIngBySelfDate(Integer userId,String startTime,String endTime);
	
	String settingScheduling(SchedulingTable schedulingTable,String pageStatus);
	
	String settingSchedulingByUsersBanci(SchedulingTable schedulingTable,String usersIds);
}
