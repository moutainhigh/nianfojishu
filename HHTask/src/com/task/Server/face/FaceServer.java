package com.task.Server.face;

import java.util.List;
import java.util.Map;

import com.task.entity.Users;
import com.task.entity.face.FaceAlarm;
import com.task.entity.face.FaceCamera;
import com.task.entity.face.FaceUsers;
import com.task.entity.face.FaceWorkTime;

/**
 * 人脸识别接口
 * @author wcy
 *
 */
public interface FaceServer{
	 
	/**
	 * 根据用户ID查询人脸识别列表
	 * @param userId
	 * @param pageStatus
	 * @return
	 */
	List<FaceUsers> findFaceUsersByUserId(Integer userId,String pageStatus);
	
	/**
	 * 添加人脸识别用户
	 * @param faceUsers
	 * @param pageStatus
	 * @return
	 */
	public String addFaceUsers(FaceUsers faceUsers,String pageStatus);
	
	/**
	 * 添加报警识别用户
	 */
	public String addAlarmFaceUsers(FaceUsers faceUsers,String pageStatus);
	/**
	 * 根据FaceUsers.id 删除人脸识别用户
	 * @param id
	 * @return
	 */
	String deleteFaceUsersById(Integer id);
	
	/**
	 * 根据Users.id获取users
	 */
	Users getUsersById(Integer id);
	
	/**
	 * 查询摄像头列表
	 */
	Map<String, Object> findFaceCameraByCon(FaceCamera camera,Integer pageNo,Integer pageSize,String pageStatus);
	
	/**
	 * 添加摄像头
	 */
	String addFaceCamera(FaceCamera camera,String pageStatus);
	
	/**
	 * 修改摄像头
	 */
	String updateFaceCamera(FaceCamera camera,String pageStatus);
	
	/**
	 * 删除摄像头
	 */
	String deleteFaceCamera(Integer id);
	
	/**
	 * 根据ID获取摄像头
	 */
	FaceCamera getFaceCameraById(Integer id);
	
	/**
	 * 工作分析，获取员工列表
	 */
	Map<String, Object> findWorkTimeLongByCon(FaceWorkTime workTime,Integer pageNo,Integer pageSize,String pageStatus);

	/**
	 * 根据IDS集合获取人脸识别后的工作时长列表
	 * @param ids
	 * @return
	 */
	List<FaceWorkTime> getFaceWorkTimeByIds(String ids);
	
	
	/**
	 * 
	 */
	Map<String, Object> searchLineChart(String userCode,String startTime,String endTime);

	List<FaceAlarm> findSxtFaceAlarm(Integer id);
}
