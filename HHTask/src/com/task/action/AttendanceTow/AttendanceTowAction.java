package com.task.action.AttendanceTow;

import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.task.DaoImpl.TotalDaoImpl;
import com.task.Server.AttendanceTowServer;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.entity.AttendanceTow;
import com.task.entity.AttendanceTowCollect;
import com.task.entity.Download;
import com.task.util.MKUtil;


@SuppressWarnings("unchecked")
public class AttendanceTowAction{
	private AttendanceTow attendanceTow;
	private AttendanceTowCollect attendanceTowCollect;
	private AttendanceTowServer attendanceTowServer;
	private Download download;
	private List<Download> downloadList;
	private List<AttendanceTow> attendanceTowList;
	private List<AttendanceTowCollect> attendanceTowCollectList;
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private String tag;
	private String pageStatus;// 页面状态
	private Integer id;
	// 分页
	private String cpage = "1";// 页数从一开始
	private String total;
	private String url;// 路径
	private String date;// 日期
	private String cardId;// 卡号
	private int pageSize = 15;// 每页显示条数
	
	
	public String showListAttendance(){
		if (attendanceTow !=null) {
			ActionContext.getContext().getSession().put("attendanceTow", attendanceTow);
		}else {
			attendanceTow = (AttendanceTow) ActionContext.getContext().getSession().get("attendanceTow");
		}
		Map<Integer, Object> map = attendanceTowServer.findAttendanceTow(attendanceTow, Integer.parseInt(cpage), pageSize, tag);
		attendanceTowList = (List<AttendanceTow>) map.get(1);
		if (attendanceTowList != null && attendanceTowList.size()>0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize -1)/pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("AttendanceTowAction_showListAttendance.action?tag="+tag);
			errorMessage = null;
		}else {
			errorMessage = "没有消息";
		}
		return "attendanceTow_show";
	}
	
	/**
	 * Android获取打卡信息接口
	 * @return
	 */
	public String showListDownload(){
		if (cardId==null||"".equals(cardId))
			return MKUtil.writeJSON(false, "卡号为空！", null);
		downloadList = attendanceTowServer.findDownload(date, cardId);
		if (downloadList != null && downloadList.size()>0) 
			return MKUtil.writeJSON(true, "获取成功", downloadList);
		else 
			return MKUtil.writeJSON(true, "抱歉！您当天没有打卡记录。", downloadList);
	}
	

	public String findDownloadList(){
		if (download !=null) {
			ActionContext.getContext().getSession().put("DownloadList", download);
		}else {
			download = (Download) ActionContext.getContext().getSession().get("DownloadList");
		}
		Map<Integer, Object> map = attendanceTowServer.findDownloads(download, Integer.parseInt(cpage), pageSize,tag);
		downloadList = (List<Download>) map.get(1);
		if (downloadList != null && downloadList.size()>0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize -1)/pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("AttendanceTowAction_findDownloadList.action?tag="+tag);
			errorMessage = null;
		}else {
			errorMessage = "没有消息";
		}
		return "download_show_show";
	}
	
	public Download getDownload() {
		return download;
	}

	public void setDownload(Download download) {
		this.download = download;
	}

	public String showListAttendanceCollect(){
		if (attendanceTowCollect !=null) {
			ActionContext.getContext().getSession().put("attendanceTowCollect", attendanceTowCollect);
		}else {
			attendanceTowCollect = (AttendanceTowCollect) ActionContext.getContext().getSession().get("attendanceTowCollect");
		}
		Map<Integer, Object> map = attendanceTowServer.findAttendanceTowCollect(attendanceTowCollect, Integer.parseInt(cpage), pageSize,tag);
		attendanceTowCollectList = (List<AttendanceTowCollect>) map.get(1);
		if (attendanceTowCollectList != null && attendanceTowCollectList.size()>0) {
			int count = (Integer) map.get(2);
			int pageCount = (count + pageSize -1)/pageSize;
			this.setTotal(pageCount + "");
			this.setUrl("AttendanceTowAction_showListAttendanceCollect.action?tag="+tag);
			errorMessage = null;
		}else {
			errorMessage = "没有消息";
		}
		return "attendanceTowCollect_show";
	}
	
	public void findSxtKq(){
		MKUtil.writeJSON(attendanceTowServer.findSxtKq(id));
	}
	
	public void findSxtKqLeave(){
		MKUtil.writeJSON(attendanceTowServer.findSxtKqLeave(id));
	}
	
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getCpage() {
		return cpage;
	}
	public void setCpage(String cpage) {
		this.cpage = cpage;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	public AttendanceTow getAttendanceTow() {
		return attendanceTow;
	}


	public void setAttendanceTow(AttendanceTow attendanceTow) {
		this.attendanceTow = attendanceTow;
	}


	public AttendanceTowServer getAttendanceTowServer() {
		return attendanceTowServer;
	}


	public void setAttendanceTowServer(AttendanceTowServer attendanceTowServer) {
		this.attendanceTowServer = attendanceTowServer;
	}


	public List<AttendanceTow> getAttendanceTowList() {
		return attendanceTowList;
	}


	public void setAttendanceTowList(List<AttendanceTow> attendanceTowList) {
		this.attendanceTowList = attendanceTowList;
	}


	public String getPageStatus() {
		return pageStatus;
	}


	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}


	public AttendanceTowCollect getAttendanceTowCollect() {
		return attendanceTowCollect;
	}


	public void setAttendanceTowCollect(AttendanceTowCollect attendanceTowCollect) {
		this.attendanceTowCollect = attendanceTowCollect;
	}


	public List<AttendanceTowCollect> getAttendanceTowCollectList() {
		return attendanceTowCollectList;
	}


	public void setAttendanceTowCollectList(
			List<AttendanceTowCollect> attendanceTowCollectList) {
		this.attendanceTowCollectList = attendanceTowCollectList;
	}
	public List<Download> getDownloadList() {
		return downloadList;
	}
	public void setDownloadList(List<Download> downloadList) {
		this.downloadList = downloadList;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
