package com.task.action.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.payment.PaymentVoucherServer;
import com.task.Server.sys.CircuitRunServer;
import com.task.entity.AlertMessages;
import com.task.entity.payment.PaymentVoucher;
import com.task.entity.system.CircuitRun;
import com.task.entity.system.ExecutionNode;
import com.task.entity.system.OptionRun;
import com.task.util.MKUtil;

/**
 * CircuitRunAction层
 * 
 * @author 刘培
 * 
 */
@SuppressWarnings("serial")
public class CircuitRunAction extends ActionSupport {

	private CircuitRunServer circuitRunServer;// Server层
	private PaymentVoucherServer paymentVoucherServer;
	private CircuitRun circuitRun;// 对象
	private ExecutionNode executionNode;// 流程对象
	private List<CircuitRun> circuitRunList;// 节点集合
	@SuppressWarnings("unchecked")
	private List list;// 集合
	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	private int id;// id
	private Integer[] ids;// ids
	private String pageStatus;// 页面状态
	private Integer ccId;// 流程id
	private Integer auditLevel;// 审批等级
	private boolean auditStauts;
	private boolean nextMessage = true;// 下级消息提醒
	private boolean sendSms = true;// 发送短息
	private String number;
	private List<OptionRun> optionrunList;
	private String auditOption;
	private String startTime;
	private String endTime;
	private Integer addUserId;	

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private PaymentVoucher paymentVoucher;
	private int msgId;// 系统消息Id;

	/***
	 * 统一审核页面
	 * 
	 * @return
	 */
	public String findAduitPage() {
		circuitRun = circuitRunServer.findCircuitRunById(id);// 查询审批流程
		if (circuitRun != null) {
			list = circuitRunServer.findAllExNodeByEpId(id);// 审批节点
			executionNode = circuitRunServer.findAuditExeNode(id);// 待审批节点
			successMessage = circuitRunServer.findEntityByCcId(id);// 审批明细
			optionrunList = circuitRunServer.findListOptionRun(id);
			// 验证码处理
			if (circuitRun.getIsVerification() != null
					&& "是".equals(circuitRun.getIsVerification())) {
				boolean bool = false;
				if ("PaymentVoucher".equals(circuitRun.getEntityName())) {
					// paymentVoucher =
					// circuitRunServer.findPaymentVoucher(id);//查询借款信息
					paymentVoucher = paymentVoucherServer
							.findPaymentVoucher(id);// 查询借款信息
					if (paymentVoucher != null) {
						number = paymentVoucher.getNumber();
						bool = true;
					}
				}
				// 如果不是借款，则显示生成随机验证码
				if (!bool) {
					Random ran = new Random();
					number = "" + ran.nextInt(10) + ran.nextInt(10)
							+ ran.nextInt(10) + ran.nextInt(10)
							+ ran.nextInt(10) + ran.nextInt(10);
				}
			}
		} else {
			errorMessage = "不存在您要审批的信息,可能已被删除!请您核实!谢谢!";
		}
		return "process_allAudit";
	}

	/***
	 * 统一审批操作
	 * 
	 * @return
	 */
	public String updateAudit() {
		try {
			errorMessage = circuitRunServer.updateExecutionNode(executionNode
					.getId(), auditStauts, executionNode.getAuditOpinion(),
					nextMessage, sendSms, auditOption);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage ="审批失败，请抓紧联系系统管理员。错误原因:"+ e.getMessage();
		}
		circuitRun = circuitRunServer.findCircuitRunById(id);// 查询审批流程
		MKUtil.writeJSON(true, errorMessage, circuitRun.getAllStatus());
		return null;
	}

	/***
	 * 审批对象对应注解明细(android接口)
	 */
	public void findCirDetail_android() {
		Integer crId = circuitRunServer.findCirRunByMesId(id);
		if (crId != null) {
			successMessage = circuitRunServer.findEntityByCcId(crId);// 审批明细
			circuitRun = circuitRunServer.findCircuitRunById(crId);// 查询审批流程
			list = circuitRunServer.findAllExNodeByEpId(crId);// 审批节点
			// executionNode = circuitRunServer.findAuditExeNode(crId);// 待审批节点
			MKUtil.writeJSON(true, "", new Object[] { circuitRun, list,
					successMessage });
			return;
		}
		MKUtil.writeJSON(false, "检查数据的有效性!", "");
	}

	/**
	 * 审批分页查询
	 * 
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Object findCircuitRun() {
		if (circuitRun != null) {
			ActionContext.getContext().getSession().put("circuitRun",
					circuitRun);
		} else {
			circuitRun = (CircuitRun) ActionContext.getContext().getSession()
					.get("circuitRun");
		}
		Object[] object = this.circuitRunServer.findCircuitRun(circuitRun,
				Integer.parseInt(cpage), pageSize);
		if (object != null && object.length > 0) {
			circuitRunList = (List<CircuitRun>) object[0];
			if (circuitRunList != null && circuitRunList.size() > 0) {
				int count = (Integer) object[1];
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("CircuitRunAction_findCircuitRun.action");
			}
			errorMessage = null;
		} else {
			errorMessage = "没有找到你要查询的内容,请检查后重试!";
		}
		return "findCircuitRun";
	}
	
	/**
	 * 工作流
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void gonzuoliu(){
		try {
			Object[] obj =circuitRunServer.gonzuoliu(addUserId);
			circuitRunList=(List<CircuitRun>)obj[0];
			String biaozhun=(String)obj[1];
			MKUtil.writeJSON(true,biaozhun,circuitRunList);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(true,"error",null);
		}
	}
	
	public void xiangxi(){
		Integer A=new Integer(id);
		if(id !=0 && A!=null){
			circuitRun=circuitRunServer.gonzuoliuxiangxi(id);
			if(circuitRun!=null){
				MKUtil.writeJSON(circuitRun);
			}else {
				MKUtil.writeJSON(null);
			}
			
		}

	}
	

	/***
	 * 日历查询审批消息
	 * 
	 * @param user
	 * @return
	 */
	public String findCircuitRun4calendar() {

		Object[] objects = circuitRunServer.showWorksCircuitRun(null, 0, 0,
				"audit");
		List<CircuitRun> list = (List<CircuitRun>) objects[0];

		class Cal {
			public String title;
			public String start;
			public String url;
			public String color;
			public String content;
		}
		List<Cal> cals = new ArrayList<Cal>();
		for (CircuitRun circuitRun : list) {
			Cal cal = new Cal();
			cal.title = "审核消息\n" + circuitRun.getName();
			cal.content = "" + circuitRun.getMessage();
			cal.start = circuitRun.getAddDateTime();
			cal.url = "javascript:void(0)";
			// cal.color="#257e4a";
			cal.color = "#CC6699";
			cals.add(cal);
		}
		MKUtil.writeJSON(cals);
		return null;
	}

	/**
	 * 执行一下,发送所有未审批消息提醒
	 */
	public void CircuitRunRemind() {
		circuitRunServer.CircuitRunRemind();
	}

	/***
	 * 人员审批时间分析
	 */
	public void findAuditTime() {
		MKUtil.writeJSON(circuitRunServer.findAuditTime(id, auditOption,
				startTime, endTime));
	}

	public CircuitRunServer getCircuitRunServer() {
		return circuitRunServer;
	}

	public void setCircuitRunServer(CircuitRunServer circuitRunServer) {
		this.circuitRunServer = circuitRunServer;
	}

	public CircuitRun getCircuitRun() {
		return circuitRun;
	}

	public void setCircuitRun(CircuitRun circuitRun) {
		this.circuitRun = circuitRun;
	}

	public List<CircuitRun> getCircuitRunList() {
		return circuitRunList;
	}

	public void setCircuitRunList(List<CircuitRun> circuitRunList) {
		this.circuitRunList = circuitRunList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public Integer getCcId() {
		return ccId;
	}

	public void setCcId(Integer ccId) {
		this.ccId = ccId;
	}

	public Integer getAuditLevel() {
		return auditLevel;
	}

	public void setAuditLevel(Integer auditLevel) {
		this.auditLevel = auditLevel;
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

	public ExecutionNode getExecutionNode() {
		return executionNode;
	}

	public void setExecutionNode(ExecutionNode executionNode) {
		this.executionNode = executionNode;
	}

	public boolean isAuditStauts() {
		return auditStauts;
	}

	public void setAuditStauts(boolean auditStauts) {
		this.auditStauts = auditStauts;
	}

	public boolean isNextMessage() {
		return nextMessage;
	}

	public void setNextMessage(boolean nextMessage) {
		this.nextMessage = nextMessage;
	}

	public boolean isSendSms() {
		return sendSms;
	}

	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}

	public PaymentVoucher getPaymentVoucher() {
		return paymentVoucher;
	}

	public void setPaymentVoucher(PaymentVoucher paymentVoucher) {
		this.paymentVoucher = paymentVoucher;
	}

	public PaymentVoucherServer getPaymentVoucherServer() {
		return paymentVoucherServer;
	}

	public void setPaymentVoucherServer(
			PaymentVoucherServer paymentVoucherServer) {
		this.paymentVoucherServer = paymentVoucherServer;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public List<OptionRun> getOptionrunList() {
		return optionrunList;
	}

	public void setOptionrunList(List<OptionRun> optionrunList) {
		this.optionrunList = optionrunList;
	}

	public String getAuditOption() {
		return auditOption;
	}

	public void setAuditOption(String auditOption) {
		this.auditOption = auditOption;
	}

	public Integer[] getIds() {
		return ids;
	}

	public void setIds(Integer[] ids) {
		this.ids = ids;
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

}
