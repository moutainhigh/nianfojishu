package com.task.action;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.StrutsStatics;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.OsRecordService;
import com.task.Server.UserServer;
import com.task.entity.Users;
import com.task.entity.WarehouseNumber;
import com.task.entity.android.OsRecord;
import com.task.entity.android.OsRecordScope;
import com.task.entity.sop.WaigouDeliveryDetail;
import com.task.util.MKUtil;

@SuppressWarnings("unchecked")
public class OsRecordAction extends ActionSupport {

	private UserServer userService;
	private OsRecordService osRecordService;

	private String usercode;
	private String password;
	private String jsonText;

	
	private OsRecord record;
	private WaigouDeliveryDetail waigoudd;// 送货单明细
	private String pageStatus;
	private List<OsRecord> list;
	private List lists;
	private List<OsRecordScope> ss;
	private String barCode;// 库位二维码
	private WarehouseNumber whn;
	private String firsttime;
	private String endtime;

	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	private String tag;

	private String errorMessage;
	private String successMessage;// 成功信息

	public void submit() {
		if (!userService.login(usercode, password)) {
			MKUtil.writeJSON(false, "工号或者密码错误", null);
		}
		Users u = userService.findUserByCode(usercode);
		try {
			List<OsRecord> list = JSON.parseArray(jsonText, OsRecord.class);
			for (OsRecord insRecord : list) {
				insRecord.setUsername(u.getName());
			}
			osRecordService.add(list);
			MKUtil.writeJSON(true, "数据提交成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			MKUtil.writeJSON(false, "数据提交异常", null);
		}
	}

	public String list() {
		if (record != null) {
			ActionContext.getContext().getSession().put("osrecord", record);
		} else {
			record = (OsRecord) ActionContext.getContext().getSession().get(
					"osrecord");
		}
		if(firsttime!=null ) {
			ActionContext.getContext().getSession().put("osrecordFirst", firsttime);
		}else {
			firsttime = (String) ActionContext.getContext().getSession().get("osrecordFirst");
		}
		if(endtime!=null) {
			ActionContext.getContext().getSession().put("osrecordEnd", endtime);
		}else {
			endtime = (String) ActionContext.getContext().getSession().get("osrecordEnd");
		}
		if (null != record) {
			record.setRuku(true);
		}

		Object[] object = osRecordService.list(pageSize, Integer
				.parseInt(cpage), record, pageStatus,firsttime,endtime);
		if (object != null && object.length > 0) {
			list = (List<OsRecord>) object[0];
			if (list != null && list.size() > 0) {
				long sum = (Long) object[1];
				long pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("OsRecord_list.action?pageStatus="+ pageStatus);
				errorMessage = null;
			} else {
				errorMessage = "抱歉!您查询的抽检记录不存在!";
			}
		} else {
			errorMessage = "抱歉!没有您查询的抽检记录!";
		}
		if ("xj".equals(pageStatus) || "sj".equals(pageStatus)) {
			return "InsRecord_list";
		}
		return "list";//OsRecord_list.jsp
	}
	
	public void exportOsRecord() {
		if (record != null) {
			ActionContext.getContext().getSession().put("osrecord", record);
		} else {
			record = (OsRecord) ActionContext.getContext().getSession().get(
					"osrecord");
		}
		if (null != record) {
			record.setRuku(true);
		}

		Object[] object = osRecordService.list(200000,1, record, pageStatus,firsttime,endtime);
		if (object != null && object.length > 0) {
			list = (List<OsRecord>) object[0];
			
			
			
			
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("外购检验记录");
			HSSFRow row = sheet.createRow(0);
			row.createCell(0).setCellValue("序号");
			row.createCell(1).setCellValue("名称");
			row.createCell(2).setCellValue("件号");
			row.createCell(3).setCellValue("时间");
			row.createCell(4).setCellValue("检查批次");
			row.createCell(5).setCellValue("本批数量");
			row.createCell(6).setCellValue("是否合格");
			row.createCell(7).setCellValue("检验人");
			if(list!=null) {
				int index = 0;
				for(int i=0;i<list.size();i++) {
					OsRecord osRecord = list.get(i);
					index++;
					HSSFRow createRow = sheet.createRow(index);
					createRow.createCell(0).setCellValue(i+1);
					if(osRecord.getProName()==null || osRecord.getProName().equals("")) {
						createRow.createCell(1).setCellValue(osRecord.getTemplate().getCtype());
					}else {
						createRow.createCell(1).setCellValue(osRecord.getProName());
					}
					if(osRecord.getMarkId()==null || osRecord.getMarkId().equals("")) {
						createRow.createCell(2).setCellValue(osRecord.getTemplate().getPartNumber());
					}else {
						createRow.createCell(2).setCellValue(osRecord.getMarkId());
					}
					createRow.createCell(3).setCellValue(osRecord.getNowDate());
					createRow.createCell(4).setCellValue(osRecord.getJcpc());
					createRow.createCell(5).setCellValue(osRecord.getQuantity());
					createRow.createCell(6).setCellValue(osRecord.getVerification());
//					createRow.createCell(6).setCellValue(osRecord.getCode());
					createRow.createCell(7).setCellValue(osRecord.getUsername());
				}
			}

		    HttpServletResponse response = (HttpServletResponse) ActionContext
						.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os;
			try {
				os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ new String("检验记录".getBytes("GB2312"), "8859_1")
						+ ".xls");
				response.setContentType("application/msexcel");
				workbook.write(os);
				workbook.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
	}

	public String rukuList() {
		if (record != null) {
			ActionContext.getContext().getSession().put("osrecordRuku", record);
		} else {
			record = (OsRecord) ActionContext.getContext().getSession().get(
					"osrecordRuku");
		}
		if (null == record) {
			record = new OsRecord();
		}
		record.setRuku(false);
		Object[] object = osRecordService.list(pageSize, Integer
				.parseInt(cpage), record, pageStatus,firsttime,endtime);
		if (object != null && object.length > 0) {
			list = (List<OsRecord>) object[0];
			if (list != null && list.size() > 0) {
				long sum = (Long) object[1];
				long pageCount = (sum + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("OsRecord_rukuList.action");
				errorMessage = null;
			} else {
				errorMessage = "没有待入库任务,可以休息一会儿了~";
			}
		} else {
			errorMessage = "抱歉!没有您查询的抽检记录!";
		}
		if ("screanRuku".equals(pageStatus)) {
			return "OsRecord_screanRuku";
		}
		return "rukuList";
	}

	public String rukuInput() {
		record = osRecordService.get(record.getId());
		if (record != null) {
			whn = osRecordService.findGoodHouseByKuwei(barCode);
			if (whn != null) {
				waigoudd = osRecordService.findWwDDById(record.getWwddId());
				if (waigoudd != null) {
					if (whn.getWarehouseArea().equals(waigoudd.getType() + "库")) {
						list = osRecordService.findHitoryKw(record.getMarkId(),
								record.getBanbenNumber(), record.getKgliao());
					} else {
						errorMessage = "库别错误,请扫描" + whn.getWarehouseArea()
								+ "的库位!";
					}
					return "rukuInput";
				} else {
					errorMessage = "订单数据异常!";
				}
			} else {
				errorMessage = "您扫描的库位码不存在,请重新扫描!";
				return "rukuInput";
			}
		} else {
			errorMessage = "数据异常!请查询后重试!";
		}
		return ERROR;
	}

	public String get() {
		try {
			record.setSeeDate(new String(record.getSeeDate().getBytes(
					"ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		list = osRecordService.get(record);
		return "show";
	}

	public String showScope() {
		ss = osRecordService.getScope(record);
		return "showScope";
	}

	/***
	 * 删除巡检记录
	 * 
	 * @return
	 */
	public String deleteOsRecord() {
		if (osRecordService.deleteOsRecord(record.getId())) {
			errorMessage = "删除成功!";
			url = "OsRecord_list.action?pageStatus=" + pageStatus;
		}
		return ERROR;
	}

	public void getMaxJcpc(){
		try {
			String maxjcpc = osRecordService.getMaxJcpc(usercode, password);
			MKUtil.writeJSON(maxjcpc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public UserServer getUserService() {
		return userService;
	}

	public void setUserService(UserServer userService) {
		this.userService = userService;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJsonText() {
		return jsonText;
	}

	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
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

	public OsRecordService getOsRecordService() {
		return osRecordService;
	}

	public void setOsRecordService(OsRecordService osRecordService) {
		this.osRecordService = osRecordService;
	}

	public List<OsRecord> getList() {
		return list;
	}

	public void setList(List<OsRecord> list) {
		this.list = list;
	}

	public OsRecord getRecord() {
		return record;
	}

	public void setRecord(OsRecord record) {
		this.record = record;
	}

	public List<OsRecordScope> getSs() {
		return ss;
	}

	public void setSs(List<OsRecordScope> ss) {
		this.ss = ss;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public WaigouDeliveryDetail getWaigoudd() {
		return waigoudd;
	}

	public void setWaigoudd(WaigouDeliveryDetail waigoudd) {
		this.waigoudd = waigoudd;
	}

	public List getLists() {
		return lists;
	}

	public void setLists(List lists) {
		this.lists = lists;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getFirsttime() {
		return firsttime;
	}

	public void setFirsttime(String firsttime) {
		this.firsttime = firsttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}