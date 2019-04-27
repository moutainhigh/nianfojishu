package com.task.action.ess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.task.Server.ess.PrintedOutServer;
import com.task.entity.PrintedOut;
import com.task.entity.PrintedOutOrder;
import com.task.entity.Sell;
import com.task.entity.Users;
import com.task.entity.sop.BuHeGePin;
import com.task.util.MKUtil;
import com.task.util.Util;

public class PrintedOutAction {

	private PrintedOutServer printServer;
	private List list;
	private List<PrintedOut> printList;
	private PrintedOutOrder poor;
	private Object obj;
	private Sell sell;
	private PrintedOut printedOut;
	private Integer id;
	private String startTime;
	private String endTime;
	private String tag;

	private String errorMessage;
	private String successMessage;
	private int pageSize = 15;
	private String cpage = "1";
	private String total;
	private String url;
	private String status = "";
	private String entiyName;//
	private int pageSize1 = 8;
	private boolean noLook;

	// 查询出需要打印的集合
	public String findSellprintList() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (sell != null) {
			session.put("sell", sell);
			session.put("status", status);
		} else {
			String stat = (String) session.get("status");
			if (status != null && status.equals(stat)) {
				sell = (Sell) session.get("sell");
			} else {
				session.remove("sell");
			}
		}
		if (pageSize != 0) {
			ActionContext.getContext().getSession().put("XOUTpageSize",
					pageSize);
		} else {
			pageSize = Integer.parseInt(ActionContext.getContext().getSession()
					.get("XOUTpageSize").toString());
		}

		Object[] objs = printServer.findprintList(sell,
				Integer.parseInt(cpage), pageSize, status, startTime, endTime,
				tag);
		list = (List) objs[0];
		int count = (Integer) objs[1];
		pageSize = (Integer) objs[2] == 0 ? 15 : (Integer) objs[2];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("PrintedOutAction_findSellprintList.action?status="
				+ status + "&tag=" + tag);
		return "print_sellList";
	}

	// 查询所有打印记录
	public String findAllPrintedOutList() {
		if (printedOut != null) {
			ActionContext.getContext().getSession().put("printedOut",
					printedOut);
		} else {
			printedOut = (PrintedOut) ActionContext.getContext().getSession()
					.get("printedOut");
		}

		Object[] objs = printServer.findAllPrintedOutList(printedOut, Integer
				.parseInt(cpage), pageSize, status, startTime, endTime);
		printList = (List<PrintedOut>) objs[0];
		int count = (Integer) objs[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("PrintedOutAction_findAllPrintedOutList.action?status="
				+ status);
		return "printedOut_List";
	}

	// 添加打印记录
	public String addprint() {
		if (Util.getLoginUser() != null) {
			noLook = printServer.getAuth1(Util.getLoginUser().getCode());
			if (poor.getId() == null) {
				poor = printServer.addprint(poor);
			}
			Object[] obj = printServer.chaXunPoorandPo(poor.getId());
			poor = (PrintedOutOrder) obj[0];
			printList = (List<PrintedOut>) obj[1];
			return "printedOut_print";
		}else{
			errorMessage="请登录后重试!";
			return "error";
		}
	}

	// 查询所有打印单
	public String findAllPrintOrder() {
		if (poor != null) {
			ActionContext.getContext().getSession().put("poor", poor);
		} else {
			poor = (PrintedOutOrder) ActionContext.getContext().getSession()
					.get("poor");
		}

		Object[] objs = printServer.findAllPrintOrder(poor, Integer
				.parseInt(cpage), pageSize, status);
		list = (List<PrintedOutOrder>) objs[0];
		int count = (Integer) objs[1];
		int pageCount = (count + pageSize - 1) / pageSize;
		this.setTotal(pageCount + "");
		this.setUrl("PrintedOutAction_findAllPrintOrder.action?status="
				+ status);
		return "printedOut_poorList";// printedOut_print.jsp
	}

	// 根据打印单Id查询
	public String findPoorandPo() {
		try {
			noLook = printServer.getAuth1(Util.getLoginUser().getCode());
			Object[] obj = printServer.chaXunPoorandPo(id);
			poor = (PrintedOutOrder) obj[0];
			printList = (List<PrintedOut>) obj[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "printedOut_print";
	}

	// 删除打印明细;
	public String delPrintedOut() {
		boolean bool = printServer.delPrintedOut(printedOut);
		if (bool) {
			errorMessage = "删除成功!";
		}
		return "error";
	}

	// 根据已打印过的实体类Id查出对应的打印单据查询
	public String findPoorandPoByEntiyId() {
		Object[] obj = printServer.findPoorandPoByEntiyId(id, entiyName);
		if (obj != null) {
			if (obj[0] != null) {
				poor = (PrintedOutOrder) obj[0];
			}
			if (obj[1] != null) {
				printList = (List<PrintedOut>) obj[1];
			}
		}
		return "printedOut_print";
	}

	public void updatePrintCount() {
		try {
			errorMessage = printServer.updatePrintCount(id);
			MKUtil.writeJSON(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(e);
		}
	}

	//
	public void qingchu() {

	}

	public PrintedOutServer getPrintServer() {
		return printServer;
	}

	public void setPrintServer(PrintedOutServer printServer) {
		this.printServer = printServer;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List<PrintedOut> getPrintList() {
		return printList;
	}

	public void setPrintList(List<PrintedOut> printList) {
		this.printList = printList;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public PrintedOut getPrintedOut() {
		return printedOut;
	}

	public void setPrintedOut(PrintedOut printedOut) {
		this.printedOut = printedOut;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Sell getSell() {
		return sell;
	}

	public void setSell(Sell sell) {
		this.sell = sell;
	}

	public PrintedOutOrder getPoor() {
		return poor;
	}

	public void setPoor(PrintedOutOrder poor) {
		this.poor = poor;
	}

	public String getEntiyName() {
		return entiyName;
	}

	public void setEntiyName(String entiyName) {
		this.entiyName = entiyName;
	}

	public boolean isNoLook() {
		return noLook;
	}

	public void setNoLook(boolean noLook) {
		this.noLook = noLook;
	}

	public int getPageSize1() {
		return pageSize1;
	}

	public void setPageSize1(int pageSize1) {
		this.pageSize1 = pageSize1;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
