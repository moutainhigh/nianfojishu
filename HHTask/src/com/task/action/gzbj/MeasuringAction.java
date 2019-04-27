package com.task.action.gzbj;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.task.Server.gzbj.MeasuringServer;
import com.task.entity.Scrap;
import com.task.entity.Users;
import com.task.entity.gzbj.Checkrecord;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.Measuring;
import com.task.entity.caiwu.baobiao.AccountsDate;
import com.task.util.MKUtil;
import com.task.util.Util;

public class MeasuringAction {
	private MeasuringServer measuringServer;
	private Measuring measuring;
	private AccountsDate accountsDate;
	private List<AccountsDate> listaccountsDate;
	
	private String errorMessage;
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private List<Map> maps;// 正常
	private Integer MeasuringId;
	private Integer id;
	
	private List<Measuring> dMeasuringList;// 待校检
	private List<Measuring> bMeasuringList;// 报废
	private List<Measuring> zMeasuringList;// 校检中
	private String loginname;// 当前用户
	private String empno;// 员工编号
	private String empname;// 员工姓名
	private List<Scrap> scrapList;
	
	private String text;
	private String msg;
	
	private Checkrecord checkrecord;
	private List<Checkrecord> checkrecordList;
	private String lasttime;// 校检时间
	private Integer period;// 校准周期
	// private String aaa="11";
	private String t;
	private Float num;// 当前库存
	private String code;
	private String flag;
	private File[] attachment;
	private String[] attachmentContentType;
	private String[] attachmentFileName;
	private File measuringfile;
	private String tag;
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Scrap> getScrapList() {
		return scrapList;
	}

	public void setScrapList(List<Scrap> scrapList) {
		this.scrapList = scrapList;
	}

	public List<Checkrecord> getCheckrecordList() {
		return checkrecordList;
	}

	public void setCheckrecordList(List<Checkrecord> checkrecordList) {
		this.checkrecordList = checkrecordList;
	}

	public Float getNum() {
		return num;
	}

	public void setNum(Float num) {
		this.num = num;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public Checkrecord getCheckrecord() {
		return checkrecord;
	}

	public void setCheckrecord(Checkrecord checkrecord) {
		this.checkrecord = checkrecord;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	

	public String getEmpname() {
		return empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public List<Measuring> getdMeasuringList() {
		return dMeasuringList;
	}

	public void setdMeasuringList(List<Measuring> dMeasuringList) {
		this.dMeasuringList = dMeasuringList;
	}

	public List<Measuring> getbMeasuringList() {
		return bMeasuringList;
	}

	public void setbMeasuringList(List<Measuring> bMeasuringList) {
		this.bMeasuringList = bMeasuringList;
	}

	public List<Measuring> getzMeasuringList() {
		return zMeasuringList;
	}

	public void setzMeasuringList(List<Measuring> zMeasuringList) {
		this.zMeasuringList = zMeasuringList;
	}

	public Integer getMeasuringId() {
		return MeasuringId;
	}

	public void setMeasuringId(Integer measuringId) {
		MeasuringId = measuringId;
	}

	public List<Map> getMaps() {
		return maps;
	}

	public void setMaps(List<Map> maps) {
		this.maps = maps;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public MeasuringServer getMeasuringServer() {
		return measuringServer;
	}

	public void setMeasuringServer(MeasuringServer measuringServer) {
		this.measuringServer = measuringServer;
	}

	public Measuring getMeasuring() {
		return measuring;
	}

	public void setMeasuring(Measuring measuring) {
		this.measuring = measuring;
	}
	
	public String execute(){
		return findMeasuringById0();
	}

	// 查看量具信息
	public String saveMeasuring() {
		if (measuring != null) {
			ActionContext.getContext().getSession().put("measuring", measuring);
		} else {
			measuring = (Measuring) ActionContext.getContext().getSession()
					.get("measuring");
		}
		if (flag == null || !flag.equals("showhas")) {
			this.dMeasuringList = this.measuringServer.updatedMeasuring(text,measuring,tag);// 待校检
			this.zMeasuringList = this.measuringServer.findzMeasuring(text,measuring,tag);// 校检中
			this.bMeasuringList = this.measuringServer.findbMeasuring(text,measuring,tag);// 报废
		}

		if (dMeasuringList != null) {
			for (int i = 0; i < dMeasuringList.size(); i++) {
				if (dMeasuringList.get(i).getCurAmount() <= 0) {
					msg = "djj";
				}
			}
		}

		Object[] object = this.measuringServer.saveMeasuring(measuring, Integer
				.parseInt(cpage), pageSize,msg,tag);
		if (object != null && object.length > 0) {
			maps = (List<Map>) object[0];
			if (maps != null && maps.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this
						.setUrl("MeasuringAction_saveMeasuring.action?tag="+tag);
				errorMessage = null;
			} 
		}
		return "findMeasuring";//showMeasuring.jsp
	}
	
	//根据Id获取
	public String findMeasuringById0(){
		this.measuring = this.measuringServer.findMeasuringById(id);
		return "findMeasuring";
	}
	
	// 手动刷新
	public String saveMeasuring1() {
		if (measuring != null) {
			ActionContext.getContext().getSession().put("measuring", measuring);
		} else {
			measuring = (Measuring) ActionContext.getContext().getSession()
					.get("measuring");
		}

		this.dMeasuringList = this.measuringServer.updatedMeasuring(text,measuring,tag);// 待校检
		Object[] object = this.measuringServer.saveMeasuring1(measuring,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			maps = (List<Map>) object[0];
			if (maps != null && maps.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("MeasuringAction_saveMeasuring.actiont?tag="+tag);
				errorMessage = null;
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "findMeasuring1";
	}

	public String getMeasuringList() {
		List<Measuring> measuringList = (List<Measuring>) this.measuringServer
				.getMeasuringList();
		MKUtil.writeJSON(true, "操作成功", measuringList);
		return null;
	}

	// 根据编号查询量具信息
	public String findMeasuringById() {
		Users loginUser = Util.getLoginUser();// 获得登陆用户
		this.loginname = loginUser.getName();
		this.code = loginUser.getCode();
		this.measuring = this.measuringServer
				.findMeasuringById(this.MeasuringId);
		if ("1".equals(text)) {
			this.checkrecord = this.measuringServer
					.findCheckrecordById(this.MeasuringId);
			this.msg = "标识";
		}
		if ("2".equals(text)) {
			this.msg = "周期";
		}
		if ("3".equals(t)) {
			this.msg = "修改";
		}
		return "findMeasuringById";//showMeasuringById.jsp
	}

	// 更新量具信息
	public String updateMeasuringById() {
		this.measuringServer.updateMeasuringById(this.measuring);
		return "updateMeasuring";
	}

	// 更新校检状态及向报检记录表中插入数据
	public String updateandsaveMeasuring() throws Exception {
		String fileName = "";
		if(attachment!=null && attachment.length>0){
			fileName = MKUtil.saveFile(attachment, attachmentFileName, "Checkrecord");
		}
		if ("3".equals(text)) {
			// 将其周期进行更改
			updateMeasuringById();
		} else {
			if ("1".equals(text)) {
				// 确认校检（校检正常）
				if (measuring.getCalibrationTime() != null
						&& !measuring.getCalibrationTime().equals("")) {
					lasttime = measuring.getCalibrationTime();
				}
				this.measuringServer.updateandsaveMeasuring1(this.measuring,
						this.empname, this.empno, this.lasttime, this.period,fileName);
			} else {
				updateMeasuringById();
				this.measuringServer.updateandsaveMeasuring(this.measuring,
						this.empname, this.empno,fileName);
			}
		}
		return "updateandsaveMeasuring";
	}

	// 修改量具信息
	public String updateandsaveMeasuring1() throws Exception {
		String fileName = "";
		if(attachment!=null && attachment.length>0){
			fileName = MKUtil.saveFile(attachment, attachmentFileName, "Checkrecord");
			measuring.setFileName(fileName);
		}		
		this.measuringServer.updateandsaveMeasuring2(this.measuring);
		measuring = null;
		return saveMeasuring();
	}

	// 查看量具明细
	public String findMeasuringdetail() {
		this.measuring = this.measuringServer
				.findMeasuringdetail(this.MeasuringId);
		if ("1".equals(t)) {
			this.msg = "报废";
			this.scrapList = this.measuringServer
					.findScrapdetail(this.MeasuringId);// 查看校检记录明细
		} else {
			this.checkrecordList = this.measuringServer
					.findCheckcorddetail(this.MeasuringId);// 查看校检记录明细
		}

		return "findMeasuringdetail";//showMeasuringDetail.jsp
	}

	// 报废（报废的情况应该从库存表中减一，在量具表中减一，将其状态改为报废）
	public String updateMeasuringandstore() {
		this.measuringServer.updateMeasuringandstoreById(this.MeasuringId,
				this.num, this.empno);
		return "updateMeasuringandstore";
	}
	// 添加量、检具信息
	public String addmeasuring(){
		errorMessage =		measuringServer.addmeasuring(measuring);
		return "measuring_add";
	}
	//导入量、检具信息
	public String daorumeasuring(){
		errorMessage = measuringServer.daorumeasuring(measuringfile);
		return "measuring_add";
	}
	//导出量、检具信息;
	public String exportExcel(){
		measuringServer.exportExcel(measuring);
		return "error";
	}
	//删除 量、检具信息;
	public String delmeasuring(){
		System.out.println(tag+" ===============");
		 measuringServer.delmeasuring(measuring);
		return "findMeasuring1";
	}
	
	//是否是唯一的本厂编号
	public void  isOne(){
		boolean bool = measuringServer.isOneMeasuring_no(empname);
		MKUtil.writeJSON(bool);
	}
	public void sendmsgjiaoyan(){
		measuringServer.sendmsgjiaoyan();
	}
	
	//查询AccountsDate
	public String findAccountsDate(){
		listaccountsDate = measuringServer.FindAccountsDate();
		return "AccountsDate_add";
	}
	//添加
	public String addAccountsDate(){
		errorMessage = "添加失败";
		if(measuringServer.addAccountsDate(accountsDate)){
			errorMessage = "添加成功";
		}
		return "findAccountsDate";
	}
	//删除
	public String  delAccountsDate(){
		errorMessage = "删除失败";
		if(measuringServer.delAccountsDate(accountsDate)){
			errorMessage = "删除成功";
		}
		return "error";
	} 
	
	//查询所有检验明细
	public String findALlCheckrecord(){
		if (checkrecord != null) {
			ActionContext.getContext().getSession().put("checkrecord", checkrecord);
		} else {
			checkrecord = (Checkrecord) ActionContext.getContext().getSession()
					.get("checkrecord");
		}

		Object[] object = this.measuringServer.findALlCheckrecord(checkrecord,
				Integer.parseInt(cpage), pageSize,tag);
		if (object != null && object.length > 0) {
				checkrecordList = (List<Checkrecord>) object[0];
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("MeasuringAction_findALlCheckrecord.actiont?tag="+tag);
				errorMessage = null;
		}
		return "checkrecordList_show";
	}
	//
	public void getMeaById(){
		try {
			measuring =	measuringServer.getMeaById(MeasuringId);
			MKUtil.writeJSON(measuring);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void caiwujisun(){
		measuringServer.caiwujisun(msg);
	}
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public File[] getAttachment() {
		return attachment;
	}

	public void setAttachment(File[] attachment) {
		this.attachment = attachment;
	}

	public String[] getAttachmentContentType() {
		return attachmentContentType;
	}

	public void setAttachmentContentType(String[] attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}

	public String[] getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String[] attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public File getMeasuringfile() {
		return measuringfile;
	}

	public void setMeasuringfile(File measuringfile) {
		this.measuringfile = measuringfile;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public AccountsDate getAccountsDate() {
		return accountsDate;
	}

	public void setAccountsDate(AccountsDate accountsDate) {
		this.accountsDate = accountsDate;
	}

	public List<AccountsDate> getListaccountsDate() {
		return listaccountsDate;
	}

	public void setListaccountsDate(List<AccountsDate> listaccountsDate) {
		this.listaccountsDate = listaccountsDate;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
