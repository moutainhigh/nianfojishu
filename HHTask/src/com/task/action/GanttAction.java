package com.task.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;



import com.opensymphony.xwork2.ActionSupport;
import com.task.Dao.TotalDao;
import com.task.Server.GanttServer;
import com.task.entity.Gantt;
import com.task.util.MKUtil;
import com.task.util.Util;

public class GanttAction  extends ActionSupport{
	private List<Gantt> gannts;
	private Gantt gantt;
	private  GanttServer ganttServer;
	
	
	private String errorMessage;
	private Integer ganttId;
	private String chexkSelectusername;
	
	private Integer urlPageNum;
	
	
	
	
	private Integer parentId;
	
	
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	/**
	 * 甘特图显示
	 * @return
	 */
	public void showGantt(){
		try{
			gannts= ganttServer.queryAllGantts();
			MKUtil.writeJSON(gannts);
			//MKUtil.writeJSON(true,"", gannts,maxCeng);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
 	}
	/**
	 * 得到根节点所在层数
	 */
	public void getMaxCengNum(){
		Integer maxCeng=ganttServer.queryMaxCengNUm();
		MKUtil.writeJSON(maxCeng);
		
		
	}
	/**
	 * ztree显示
	 * @return
	 */
	public void showGantt1(){
		try{
			gannts= ganttServer.queryAllGantts1(parentId);
			
//			Integer maxCeng=ganttServer.queryMaxCengNUm();
			MKUtil.writeJSON(gannts);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
 	}
	public void gainChild(){
		try{
			gannts=ganttServer.gainChild(parentId);
			MKUtil.writeJSON(gannts);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 添加甘特图一项数据
	 * @return
	 */
	public String addOneGantt(){
		boolean flag=ganttServer.addOneGantt(gantt);
		errorMessage="add"+flag;
		ServletActionContext.getRequest().getSession().setAttribute("errorMessage", errorMessage);
		ServletActionContext.getRequest().getSession().setAttribute("urlPageNum", urlPageNum);
		return "gantt";
	}
	/**
	 *添加子项目 
	 */
	public String addSon(){
		ganttServer.addSon(gantt);
		return "gantt";
	}
	
	/**
	 * 删除甘特图一项数据
	 */
	public String deleteOneGantt(){
		gantt=new Gantt();
		gantt.setId(ganttId);
		boolean flag=ganttServer.deleteOneGantt(gantt);
		errorMessage="del"+flag;
		ServletActionContext.getRequest().getSession().setAttribute("errorMessage", errorMessage);
		//ServletActionContext.getRequest().getSession().setAttribute("urlPageNum", urlPageNum);
		ServletActionContext.getRequest().getSession().removeAttribute("urlPageNum");
		return "gantt";
	
	}
	
	

	
	/**
	 * 修改甘特图一项数据
	 * @return
	 */
	public String updateOneGantt(){
		//gantt.setPeopleIds(chexkSelectusername);
		boolean flag=ganttServer.updateOneGantt(gantt);
		errorMessage="upd"+flag;
		ServletActionContext.getRequest().getSession().setAttribute("errorMessage", errorMessage);
		ServletActionContext.getRequest().getSession().setAttribute("urlPageNum", urlPageNum);
		return "gantt";
	}
	@SuppressWarnings("unchecked")
	public String findAllProject(){
		gannts= ganttServer.queryAllGantts();
		MKUtil.writeJSON(gannts);
		return null;
	}
	
	
	
	public void test(){
		if(gantt==null){
			gantt=new Gantt();
		}
		gantt=ganttServer.findById(14);
		
		Float resulr=ganttServer.gainDanTai(gantt, null);
		
		System.out.println("resulr:"+resulr);
	}
	/**
	 * 
	 */
	public void updateIsShow(){
		
	}
	
	public void findById(){
		try {
			gantt	=ganttServer.findById(parentId);
			MKUtil.writeJSON(gantt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the gannts
	 */
	public List<Gantt> getGannts() {
		return gannts;
	}
	/**
	 * @param gannts the gannts to set
	 */
	public void setGannts(List<Gantt> gannts) {
		this.gannts = gannts;
	}
	public void setGantt(Gantt gantt) {
		this.gantt = gantt;
	}
	public Gantt getGantt() {
		return gantt;
	}
	/**
	 * @return the ganttServer
	 */
	public GanttServer getGanttServer() {
		return ganttServer;
	}
	/**
	 * @param ganttServer the ganttServer to set
	 */
	public void setGanttServer(GanttServer ganttServer) {
		this.ganttServer = ganttServer;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setGanttId(Integer ganttId) {
		this.ganttId = ganttId;
	}
	public Integer getGanttId() {
		return ganttId;
	}
	
	public void setUrlPageNum(Integer urlPageNum) {
		this.urlPageNum = urlPageNum;
	}
	public Integer getUrlPageNum() {
		return urlPageNum;
	}
	public void setChexkSelectusername(String chexkSelectusername) {
		this.chexkSelectusername = chexkSelectusername;
	}
	public String getChexkSelectusername() {
		return chexkSelectusername;
	}           

}
