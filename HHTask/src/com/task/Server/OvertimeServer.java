package com.task.Server;

import java.util.List;
import java.util.Map;

import com.task.entity.AnnualLeave;
import com.task.entity.AttendanceTow;
import com.task.entity.Overtime;
import com.task.entity.OvertimeDetail;
import com.task.entity.Users;

@SuppressWarnings("unchecked")
public interface OvertimeServer {
	public Users findUserByCode(String code);//查询Users为input
	
	public String finAllMarkIdForSetlect();//查询加工单号(状态为领工序)为select
	public void overc();
	public String addOvertime(Overtime overtime);//添加加班记录
	/**
	 * 添加加班记录(批量)
	 * @param overtime
	 * @param list
	 * @return
	 */
	public String addOvertimeList(Overtime overtime, Integer[] usersId);//
	
	public String addMoneyOvertime(Overtime overtime, List<Users> list);//添加加班记录
	
	public String deleteOvertime(Overtime overtime);//删除加班记录
	
	public String updateOvertime(Overtime overtime);
	public String updateOvertime(Overtime overtime,List<OvertimeDetail> overtimeDetails);//更新加班记录
	
	public Overtime getOvertimeById(Integer id);//获得加班记录
	
	public Object[] findAllOvertimeForJbForDtj(Map map);//获得加班记录集合  加班 未提交
	public Object[] findAllOvertimeForJbForYtj(Map map,int pageNo,int pageSize );//获得加班记录集合  加班 未提交
	
	public Object[] findAllOvertimeForSpForDsp(Map map);//获得加班记录集合 待审批
	public Object[] findAllOvertimeForSpForYsp(Map map,int pageNo,int pageSize );//获得加班记录集合 已审批
	
	public Object[] findAllOvertimeForRsForDQr(Map map);//获得待确认加班集合 
	public Object[] findAllOvertimeForRsForYQr(Map map,int pageNo,int pageSize );//获得已确认加班集合集合 
	/**
	 * 管理所有的加班记录
	 * @return
	 */
	public Object[] findOvertimeListForAll(String startDate,String endDate, Overtime overtime,int pageNo, int pageSize, String tags);
	/**
	 * 根据条件导出加班记录
	 * @param overtime
	 * @return
	 */
	public List exportExcelOvertimeListForAll(Overtime overtime);

	public AnnualLeave ByCodeAnnualLeave(String overtimeCode);

	public void addAnnualLeave(AnnualLeave a);

	public void updateAnnualLeave(AnnualLeave a);

	public String addOvertime1(Overtime overtime);

	public List<Overtime> finAllMarkIdForSetlectAll(Integer pageNo,Integer pageSize);
	/**
	 * 判断加班时间在不在班次内，并且加班时长不能超过班次时长。
	 */
	public String isbancisc(Integer userId,String startDate,String endDate,Integer xiuxi);
	
	/**
	 * 输出非换休的加班申请
	 * @param date
	 */
	public String systemOverTime(String date);
	
	/**
	 * 根据加班申请获取附近的打卡记录
	 * @param overtime
	 * @return
	 */
	public List<AttendanceTow> getAttendanceTow(Overtime overtime);
	/**
	 * 补加班
	 * @param overtime
	 * @return
	 */
	public String backupOvertime(Overtime overtime);

	/**
	 * 判断加班时间在不在班次内，并且加班时长不能超过班次时长。(多人)
	 */
	public String isbancisc(String usersId, String startDate,
			String endDate, Integer xiuxi);
	
	/**
	 * 根据overtime.id 获得明细
	 * @param id
	 * @return
	 */
	public List<OvertimeDetail> findOvertimeIdByDetail(Integer id,String pageStatus);
	
	/**
	 * 检查加班记录是否存在
	 */
	public Integer checkOverTimeDetailByuserId(OvertimeDetail detail,Integer usersId);
}
